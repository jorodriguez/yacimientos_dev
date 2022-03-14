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
import sia.modelo.InvInventario;
import sia.modelo.InvInventarioMovimiento;
import sia.modelo.InvTransaccion;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.inventarios.InventarioMovimientoVO;
import sia.util.UtilLog4j;

/**
 *
 * @author Aplimovil SA de CV
 */
@Stateless (name = "Inventarios_InventarioMovimientoService")
@LocalBean
public class InventarioMovimientoImpl extends AbstractFacade<InvInventarioMovimiento>  {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    Audit audit;

    public InventarioMovimientoImpl() {
	super(InvInventarioMovimiento.class);
    }

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    
    public List<InventarioMovimientoVO> buscarPorFiltros(InventarioMovimientoVO filtro, Integer campo) {
	return buscarPorFiltros(filtro, null, null, null, false, campo);
    }

    
    public List<InventarioMovimientoVO> buscarPorFiltros(InventarioMovimientoVO filtro, Integer inicio, Integer tamanioPagina, String campoOrdenar, boolean esAscendente, Integer campo) {
	CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
	CriteriaQuery query = criteriaBuilder.createQuery();
	Root inventarioMovimiento = query.from(InvInventarioMovimiento.class);
	Join inventario = inventarioMovimiento.join("inventario");
	Join transaccion = inventarioMovimiento.join("transaccion");        
	Join usuario = inventarioMovimiento.join("genero");

	//Obtener el campo por el cual se va a ordenar la lista
	Path orderBy = campoOrdenar == null ? inventarioMovimiento.get("id") : inventarioMovimiento.get(campoOrdenar);
	Order order = esAscendente ? criteriaBuilder.asc(orderBy) : criteriaBuilder.desc(orderBy);

	aplicarFiltros(filtro, criteriaBuilder, query, inventarioMovimiento, inventario);
	query.orderBy(order);

	query.select(criteriaBuilder.construct(InventarioMovimientoVO.class,
		inventarioMovimiento.get("id"), inventario.get("id"), inventarioMovimiento.get("fecha"),
		inventarioMovimiento.get("tipoMovimiento"), inventarioMovimiento.get("numeroUnidades"),
		transaccion.get("id"), usuario.get("nombre"),transaccion.get("folioRemision")));

	TypedQuery<InventarioMovimientoVO> typedQuery = getEntityManager().createQuery(query);

	//establecer la paginacion basados en los parametros
	if (inicio != null && tamanioPagina != null) {
	    typedQuery.setFirstResult(inicio);
	    typedQuery.setMaxResults(tamanioPagina);
	}

	return typedQuery.getResultList();
    }

    
    public int contarPorFiltros(InventarioMovimientoVO filtro, Integer campo) {
	CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
	CriteriaQuery query = criteriaBuilder.createQuery();
	Root inventarioMovimiento = query.from(InvInventarioMovimiento.class);
	Join inventario = inventarioMovimiento.join("inventario");
	aplicarFiltros(filtro, criteriaBuilder, query, inventarioMovimiento, inventario);
	query.select(criteriaBuilder.count(inventarioMovimiento));
	return ((Long) getEntityManager().createQuery(query).getSingleResult()).intValue();
    }

    
    public InventarioMovimientoVO buscar(Integer id) throws SIAException {
	InvInventarioMovimiento inventarioMovimiento = this.find(id);

	return new InventarioMovimientoVO(
		inventarioMovimiento.getId(), inventarioMovimiento.getInventario().getId(),
		inventarioMovimiento.getFecha(), inventarioMovimiento.getTipoMovimiento(),
		inventarioMovimiento.getNumeroUnidades(), inventarioMovimiento.getTransaccion().getId(),
		inventarioMovimiento.getGenero().getNombre()
	);
    }

    
    public void crear(InventarioMovimientoVO inventarioMovimientoVO, String username, int campo) throws SIAException {
	try {
	    UtilLog4j.log.info(this, "InventarioMovimientoImpl.create()");

	    InvInventarioMovimiento inventarioMovimiento = new InvInventarioMovimiento();
	    Usuario usuario = new Usuario(username);

	    inventarioMovimiento.setInventario(new InvInventario(inventarioMovimientoVO.getInventarioId()));
	    inventarioMovimiento.setFecha(inventarioMovimientoVO.getFecha());
	    inventarioMovimiento.setTipoMovimiento(inventarioMovimientoVO.getTipoMovimiento());
	    inventarioMovimiento.setNumeroUnidades(inventarioMovimientoVO.getNumeroUnidades());
	    inventarioMovimiento.setTransaccion(new InvTransaccion(inventarioMovimientoVO.getTransaccionId()));

	    inventarioMovimiento.setEliminado(Constantes.BOOLEAN_FALSE);
	    inventarioMovimiento.setGenero(usuario);
	    inventarioMovimiento.setFechaGenero(new Date());
	    inventarioMovimiento.setHoraGenero(new Date());

	    this.audit.register(AuditActions.CREATE, inventarioMovimiento, usuario);

	    super.create(inventarioMovimiento);

	    inventarioMovimientoVO.setId(inventarioMovimiento.getId());

	    UtilLog4j.log.info(this, "InvInventarioMovimiento creado exitosamente.");
	} catch (Exception ex) {
	    throw new SIAException(ex.getMessage());
	}
    }

    
    public void actualizar(InventarioMovimientoVO inventarioMovimientoVO, String username, int campo) throws SIAException {
	try {
	    UtilLog4j.log.info(this, "InventarioMovimientoImpl.update()");

	    InvInventarioMovimiento inventarioMovimiento = this.find(inventarioMovimientoVO.getId());
	    Usuario usuario = new Usuario(username);

	    inventarioMovimiento.setInventario(new InvInventario(inventarioMovimientoVO.getInventarioId()));
	    inventarioMovimiento.setFecha(inventarioMovimientoVO.getFecha());
	    inventarioMovimiento.setTipoMovimiento(inventarioMovimientoVO.getTipoMovimiento());
	    inventarioMovimiento.setNumeroUnidades(inventarioMovimientoVO.getNumeroUnidades());
	    inventarioMovimiento.setTransaccion(new InvTransaccion(inventarioMovimientoVO.getTransaccionId()));

	    inventarioMovimiento.setModifico(usuario);
	    inventarioMovimiento.setFechaModifico(new Date());
	    inventarioMovimiento.setHoraModifico(new Date());

	    this.audit.register(AuditActions.UPDATE, inventarioMovimiento, usuario);

	    super.edit(inventarioMovimiento);

	    UtilLog4j.log.info(this, "InvInventarioMovimiento actualizado exitosamente.");
	} catch (Exception ex) {
	    throw new SIAException(ex.getMessage());
	}
    }

    
    public void eliminar(Integer id, String username, Integer campo) throws SIAException {
	try {
	    UtilLog4j.log.info(this, "InventarioMovimientoImpl.delete()");

	    InvInventarioMovimiento inventarioMovimiento = this.find(id);
	    Usuario usuario = new Usuario(username);

	    inventarioMovimiento.setModifico(usuario);
	    inventarioMovimiento.setFechaModifico(new Date());
	    inventarioMovimiento.setHoraModifico(new Date());
	    inventarioMovimiento.setEliminado(Constantes.BOOLEAN_TRUE);

	    this.audit.register(AuditActions.DELETE, inventarioMovimiento, usuario);

	    super.edit(inventarioMovimiento);

	    UtilLog4j.log.info(this, "InvInventarioMovimiento eliminado exitosamente.");
	} catch (Exception ex) {
	    throw new SIAException(ex.getMessage());
	}
    }

    private void aplicarFiltros(InventarioMovimientoVO filtro, CriteriaBuilder criteriaBuilder, CriteriaQuery query, Root inventarioMovimiento, Join inventario) {
	List<Predicate> predicates = new ArrayList<Predicate>(6);

	predicates.add(criteriaBuilder.equal(inventarioMovimiento.get("eliminado"), Constantes.BOOLEAN_FALSE));

	if (!esNuloOVacio(filtro.getInventarioId())) {
	    predicates.add(criteriaBuilder.equal(inventario.get("id"), filtro.getInventarioId()));
	}

	if (filtro.getFecha() != null) {
	    predicates.add(criteriaBuilder.lessThanOrEqualTo(inventarioMovimiento.get("fecha"), filtro.getFecha()));
	}

	if (!esNuloOVacio(filtro.getTipoMovimiento())) {
	    predicates.add(criteriaBuilder.equal(inventarioMovimiento.get("tipoMovimiento"), filtro.getTipoMovimiento()));
	}

	if (!esNuloOVacio(filtro.getNumeroUnidades())) {
	    predicates.add(criteriaBuilder.equal(inventarioMovimiento.get("numeroUnidades"), filtro.getNumeroUnidades()));
	}

	if (!esNuloOVacio(filtro.getTransaccionId())) {
	    predicates.add(criteriaBuilder.equal(inventarioMovimiento.get("transaccionId"), filtro.getTransaccionId()));
	}

	query.where(predicates.toArray(new Predicate[0]));
    }
}
