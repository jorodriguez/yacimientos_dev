package sia.inventarios.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.inventarios.audit.Audit;
import sia.inventarios.audit.AuditActions;
import static sia.inventarios.service.Utilitarios.esNuloOVacio;
import sia.modelo.InvArticulo;
import sia.modelo.InvTransaccion;
import sia.modelo.InvTransaccionArticulo;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.inventarios.TransaccionArticuloVO;
import sia.util.UtilLog4j;

/**
 *
 * @author Aplimovil SA de CV
 */
@Stateless (name = "Movimientos_MovimientoService")
@LocalBean
public class TransaccionArticuloImpl extends AbstractFacade<InvTransaccionArticulo>  {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    TransaccionImpl transaccionService;

    @Inject
    Audit audit;

    public TransaccionArticuloImpl() {
        super(InvTransaccionArticulo.class);
    }

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    
    public List<TransaccionArticuloVO> buscarPorFiltros(TransaccionArticuloVO filtro, Integer campo) {
        return buscarPorFiltros(filtro, null, null, null, true, campo);
    }

    
    public List<TransaccionArticuloVO> buscarPorFiltros(TransaccionArticuloVO filtro, Integer inicio,
            Integer tamanioPagina, String campoOrdenar, boolean esAscendente, Integer campo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root transaccionArticulo = criteriaQuery.from(InvTransaccionArticulo.class);
        Join transaccion = transaccionArticulo.join("transaccion");
        Join articulo = transaccionArticulo.join("articulo");

        //Obtener el campo por el cual se va a ordenar la lista
        Path orderBy = campoOrdenar == null ? transaccionArticulo.get("id") : transaccionArticulo.get(campoOrdenar);
        Order order = esAscendente ? criteriaBuilder.asc(orderBy) : criteriaBuilder.desc(orderBy);

        aplicarFiltros(filtro, criteriaBuilder, criteriaQuery, transaccionArticulo, transaccion, articulo);
        criteriaQuery.orderBy(order);
        criteriaQuery.select(criteriaBuilder.construct(TransaccionArticuloVO.class,
                transaccionArticulo.get("id"),
                transaccion.get("id"),
                articulo.get("id"), articulo.get("nombre"),
                transaccionArticulo.get("numeroUnidades"),
                transaccionArticulo.get("identificador")));

        TypedQuery<TransaccionArticuloVO> typedQuery = getEntityManager().createQuery(criteriaQuery);

        //establecer la paginacion basados en los parametros
        if (inicio != null && tamanioPagina != null) {
            typedQuery.setFirstResult(inicio);
            typedQuery.setMaxResults(tamanioPagina);
        }

        return typedQuery.getResultList();
    }

    
    public int contarPorFiltros(TransaccionArticuloVO filtro, Integer campo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery query = criteriaBuilder.createQuery();
        Root transaccionArticulo = query.from(InvTransaccionArticulo.class);
        Join transaccion = transaccionArticulo.join("transaccion");
        Join articulo = transaccionArticulo.join("articulo");

        aplicarFiltros(filtro, criteriaBuilder, query, transaccionArticulo, transaccion, articulo);
        query.select(criteriaBuilder.count(transaccionArticulo));

        return ((Long) getEntityManager().createQuery(query).getSingleResult()).intValue();
    }

    
    public TransaccionArticuloVO buscar(Integer id) throws SIAException {
        InvTransaccionArticulo transaccionMovimiento = this.find(id);
        InvTransaccion transaccion = transaccionMovimiento.getTransaccion();
        InvArticulo articulo = transaccionMovimiento.getArticulo();

        return new TransaccionArticuloVO(
                transaccionMovimiento.getId(),
                transaccion.getId(),
                articulo.getId(), articulo.getNombre(),
                transaccionMovimiento.getNumeroUnidades(),
                transaccionMovimiento.getIdentificador());
    }

    
    public void crear(TransaccionArticuloVO transaccionArticuloVO, String username, int campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "MovimientoService.create()");

            InvTransaccionArticulo transaccionArticulo = new InvTransaccionArticulo();
            Usuario usuario = new Usuario(username);
            InvTransaccion transaccion = transaccionService.find(transaccionArticuloVO.getTransaccionId());

            if (transaccion != null) {
                if (transaccion.getStatus().equals(Constantes.INV_TRANSACCION_STATUS_PREPARACION)) {
                    transaccionArticulo.setTransaccion(transaccion);
                    transaccionArticulo.setArticulo(new InvArticulo(transaccionArticuloVO.getArticuloId()));
                    transaccionArticulo.setNumeroUnidades(transaccionArticuloVO.getNumeroUnidades());
                    transaccionArticulo.setIdentificador(transaccionArticuloVO.getIdentificador());

                    transaccionArticulo.setEliminado(Constantes.BOOLEAN_FALSE);
                    transaccionArticulo.setGenero(usuario);
                    transaccionArticulo.setFechaGenero(new Date());
                    transaccionArticulo.setHoraGenero(new Date());

                    this.audit.register(AuditActions.CREATE, transaccionArticulo, usuario);

                    super.create(transaccionArticulo);

                    transaccion.setNumeroArticulos(transaccion.getNumeroArticulos() + 1);
                    transaccionService.edit(transaccion);

                    transaccionArticuloVO.setId(transaccionArticulo.getId());

                    UtilLog4j.log.info(this, "Movimiento creado exitosamente.");
                } else {
                    throw new Exception("No se puede agregar el TransaccionArticulo a la transaccion #" + transaccion.getId() + " porque ya fue procesada anteriormente.");
                }
            } else {
                throw new Exception("No se encontro la transaccion del TransaccionArticulo #" + transaccionArticuloVO.getTransaccionId());
            }
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    
    public void actualizar(TransaccionArticuloVO transaccionArticuloVO, String username, int campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "MovimientoService.update()");

            InvTransaccionArticulo transaccionArticulo = this.find(transaccionArticuloVO.getId());
            Usuario usuario = new Usuario(username);
            InvTransaccion transaccion = transaccionService.find(transaccionArticuloVO.getTransaccionId());

            if (transaccion != null) {
                if (transaccion.getStatus().equals(Constantes.INV_TRANSACCION_STATUS_PREPARACION)) {
                    if (!transaccionArticulo.getTransaccion().getId().equals(transaccionArticuloVO.getTransaccionId())) {
                        throw new Exception("No se permite cambiar la transaccion de un TransaccionArticulo existente.");
                    }

                    transaccionArticulo.setArticulo(new InvArticulo(transaccionArticuloVO.getArticuloId()));
                    transaccionArticulo.setNumeroUnidades(transaccionArticuloVO.getNumeroUnidades());
                    transaccionArticulo.setIdentificador(transaccionArticuloVO.getIdentificador());

                    transaccionArticulo.setModifico(usuario);
                    transaccionArticulo.setFechaModifico(new Date());
                    transaccionArticulo.setHoraModifico(new Date());

                    this.audit.register(AuditActions.UPDATE, transaccionArticulo, usuario);

                    super.edit(transaccionArticulo);

                    UtilLog4j.log.info(this, "Movimiento actualizado exitosamente.");
                } else {
                    throw new Exception("No se puede actualizar el TransaccionArticulo #" + transaccionArticuloVO.getId() + " de la transaccion #" + transaccion.getId() + " porque ya fue procesada anteriormente.");
                }
            } else {
                throw new Exception("No se encontro la transaccion del TransaccionArticulo #" + transaccionArticuloVO.getTransaccionId());
            }
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    
    public void eliminar(Integer id, String username, Integer campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "MovimientoService.delete()");

            InvTransaccionArticulo transaccionArticulo = this.find(id);
            Usuario usuario = new Usuario(username);
            InvTransaccion transaccion = transaccionArticulo.getTransaccion();

            if (transaccion != null) {
                if (transaccion.getStatus().equals(Constantes.INV_TRANSACCION_STATUS_PREPARACION)) {
                    transaccionArticulo.setModifico(usuario);
                    transaccionArticulo.setFechaModifico(new Date());
                    transaccionArticulo.setHoraModifico(new Date());
                    transaccionArticulo.setEliminado(Constantes.BOOLEAN_TRUE);

                    this.audit.register(AuditActions.DELETE, transaccionArticulo, usuario);

                    super.edit(transaccionArticulo);

                    transaccion.setNumeroArticulos(transaccion.getNumeroArticulos() - 1);
                    transaccionService.edit(transaccion);

                    UtilLog4j.log.info(this, "Movimiento eliminado exitosamente.");
                } else {
                    throw new Exception("No se puede eliminar el TransaccionArticulo #" + id + " de la transaccion #" + transaccion.getId() + " porque ya fue procesada anteriormente.");
                }
            } else {
                throw new Exception("No se encontro la transaccion del TransaccionArticulo #" + id);
            }
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    private void aplicarFiltros(TransaccionArticuloVO filtro, CriteriaBuilder criteriaBuilder, CriteriaQuery query, Root transaccionArticulo, Join transaccion, Join articulo) {
        List<Predicate> predicates = new ArrayList<Predicate>(4);

        predicates.add(criteriaBuilder.equal(transaccionArticulo.get("eliminado"), Constantes.BOOLEAN_FALSE));

        if (!esNuloOVacio(filtro.getTransaccionId())) {
            predicates.add(criteriaBuilder.equal(transaccion.get("id"), filtro.getTransaccionId()));
        }

        if (!esNuloOVacio(filtro.getArticuloId())) {
            predicates.add(criteriaBuilder.equal(articulo.get("id"), filtro.getArticuloId()));
        }

        if (!esNuloOVacio(filtro.getNumeroUnidades())) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(transaccionArticulo.get("numeroUnidades"), filtro.getNumeroUnidades()));
        }

        query.where(predicates.toArray(new Predicate[0]));
    }

}
