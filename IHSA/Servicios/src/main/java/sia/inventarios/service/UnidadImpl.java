package sia.inventarios.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.inventarios.audit.Audit;
import sia.inventarios.audit.AuditActions;
import static sia.inventarios.service.Utilitarios.crearExpresionLike;
import static sia.inventarios.service.Utilitarios.esNuloOVacio;
import sia.modelo.SiUnidad;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.inventarios.UnidadVO;
import sia.util.UtilLog4j;

/**
 *
 * @author Aplimovil SA de CV
 */
//Stateless (name = "Inventarios_UnidadService")
@Stateless
public class UnidadImpl extends AbstractFacade<SiUnidad> implements UnidadRemote {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    Audit audit;

    public UnidadImpl() {
        super(SiUnidad.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Esta función permite buscar todas las unidades basadas en el filtro
     * indicado
     *
     * @param filtro vo que contiene los parámetros de busqueda
     * @param campo
     * @return Lista de unidades basadas en el filtro
     */
    public List<UnidadVO> buscarPorFiltros(UnidadVO filtro, Integer campo) {
        return buscarPorFiltros(filtro, null, null, null, true, campo);
    }

    public List<UnidadVO> buscarPorFiltros(UnidadVO filtro, Integer inicio, Integer tamanioPagina, String campoOrdenar, boolean esAscendente, Integer campo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery query = criteriaBuilder.createQuery();
        Root unidad = query.from(SiUnidad.class);
        //Obtener el campo por el cual se va a ordenar la lista
        Path orderBy = campoOrdenar == null ? unidad.get("nombre") : unidad.get(campoOrdenar);
        Order order = esAscendente ? criteriaBuilder.asc(orderBy) : criteriaBuilder.desc(orderBy);

        aplicarFiltros(filtro, criteriaBuilder, query, unidad);
        query.orderBy(order);
        query.select(criteriaBuilder.construct(UnidadVO.class, unidad.get("id"), unidad.get("nombre"), unidad.get("descripcion")));

        TypedQuery<UnidadVO> typedQuery = getEntityManager().createQuery(query);

        //establecer la paginacion basados en los parametros
        if (inicio != null && tamanioPagina != null) {
            typedQuery.setFirstResult(inicio);
            typedQuery.setMaxResults(tamanioPagina);
        }
        return typedQuery.getResultList();
    }

    public int contarPorFiltros(UnidadVO filtro, Integer campo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery query = criteriaBuilder.createQuery();
        Root unidad = query.from(SiUnidad.class);
        aplicarFiltros(filtro, criteriaBuilder, query, unidad);
        query.select(criteriaBuilder.count(unidad));
        return ((Long) getEntityManager().createQuery(query).getSingleResult()).intValue();
    }

    public UnidadVO buscar(Integer id) throws SIAException {
        SiUnidad unidad = this.find(id);
        return new UnidadVO(unidad.getId(), unidad.getNombre(), unidad.getDescripcion());
    }

    public void crear(UnidadVO unidadVO, String username, int campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "UnidadService.create()");

            SiUnidad unidad = new SiUnidad();
            Usuario usuario = new Usuario(username);

            unidad.setId(0); // necesita ser cero porque el modelo tiene un constraint @NotNull
            unidad.setNombre(unidadVO.getNombre());
            unidad.setDescripcion(unidadVO.getDescripcion());
            unidad.setEliminado(Constantes.BOOLEAN_FALSE);
            unidad.setGenero(usuario);
            unidad.setFechaGenero(new Date());
            unidad.setHoraGenero(new Date());

            this.audit.register(AuditActions.CREATE, unidad, usuario);

            super.create(unidad);

            unidadVO.setId(unidad.getId());

            UtilLog4j.log.info(this, "SiUnidad creado exitosamente.");
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    public void actualizar(UnidadVO unidadVO, String username, int campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "UnidadService.update()");

            SiUnidad unidad = this.find(unidadVO.getId());
            Usuario usuario = new Usuario(username);

            unidad.setNombre(unidadVO.getNombre());
            unidad.setDescripcion(unidadVO.getDescripcion());
            unidad.setModifico(usuario);
            unidad.setFechaModifico(new Date());
            unidad.setHoraModifico(new Date());

            this.audit.register(AuditActions.UPDATE, unidad, usuario);

            super.edit(unidad);

            UtilLog4j.log.info(this, "SiUnidad actualizado exitosamente.");
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    public void eliminar(Integer id, String username, Integer campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "UnidadService.delete()");

            SiUnidad unidad = this.find(id);
            Usuario usuario = new Usuario(username);

            unidad.setModifico(usuario);
            unidad.setFechaModifico(new Date());
            unidad.setHoraModifico(new Date());
            unidad.setEliminado(Constantes.BOOLEAN_TRUE);

            this.audit.register(AuditActions.DELETE, unidad, usuario);

            super.edit(unidad);

            UtilLog4j.log.info(this, "SiUnidad eliminado exitosamente.");
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    /**
     * Este metodo aplica los filtros al criteria query
     *
     * @UnidadVO VO que continene la informacion de filtrado
     * @CriteriaBuilder Objeto de JPA que permite la creacion dinamica de
     * sentencias SQL
     * @CriteriaQuery Objecto utilizado para realizar la consulta
     * @Root objeto que representa la entidad sobra la cual se realiza la
     * consulta
     */
    private void aplicarFiltros(UnidadVO filtro, CriteriaBuilder criteriaBuilder, CriteriaQuery query, Root unidad) {

        List<Predicate> predicates = new ArrayList<Predicate>(3);
        predicates.add(criteriaBuilder.equal(unidad.get("eliminado"), Constantes.BOOLEAN_FALSE));

        if (!esNuloOVacio(filtro.getNombre())) {
            predicates.add(criteriaBuilder.like(unidad.get("nombre"), crearExpresionLike(filtro.getNombre())));
        }

        if (!esNuloOVacio(filtro.getDescripcion())) {
            predicates.add(criteriaBuilder.like(unidad.get("descripcion"), crearExpresionLike(filtro.getDescripcion())));
        }

        query.where(predicates.toArray(new Predicate[0]));
    }

}
