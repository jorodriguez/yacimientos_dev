package sia.inventarios.service;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import sia.constantes.Constantes;
import sia.modelo.InvAviso;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.inventarios.AvisoVO;

/**
 * @author Aplimovil SA de CV
 */
@Stateless
public class AvisoImpl extends AbstractFacade<InvAviso>  {

    @Resource(name = "sia_hostname")
    private String hostName;

    @Resource(name = "sia_port")
    private String port;

    @Resource(name = "sia_context_path")
    private String contextPath;

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    public AvisoImpl() {
	super(InvAviso.class);
    }

    //@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    
    public Integer crearAviso(String usuarioDestinatario, AvisoVO avisoVO, String usuarioCreador) {
	InvAviso aviso = new InvAviso();
	aviso.setUsuario(new Usuario(usuarioDestinatario));
	aviso.setAsunto(avisoVO.getAsunto());
	aviso.setMensaje(avisoVO.getMensaje());
	aviso.setEliminado(Constantes.BOOLEAN_FALSE);
	aviso.setLeido(Constantes.BOOLEAN_FALSE);
	aviso.setFecha(new Date());
	aviso.setGenero(new Usuario(usuarioCreador));
	aviso.setFechaGenero(new Date());
	aviso.setHoraGenero(new Date());
	getEntityManager().persist(aviso);
	return aviso.getId();
    }

    
    public List<AvisoVO> listarAvisos(String usuarioId) {
	CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
	CriteriaQuery<AvisoVO> q = criteriaBuilder.createQuery(AvisoVO.class);
	Root<InvAviso> aviso = q.from(InvAviso.class);
	Join<InvAviso, Usuario> usuario = aviso.join("usuario");

	q.orderBy(criteriaBuilder.desc(aviso.get("fecha")));
	q.where(criteriaBuilder.and(criteriaBuilder.equal(usuario.get("id"), usuarioId),
		criteriaBuilder.equal(aviso.get("eliminado"), Constantes.BOOLEAN_FALSE)));

	q.select(criteriaBuilder.construct(AvisoVO.class, aviso.get("id"), aviso.get("fecha"), aviso.get("asunto"),
		aviso.get("mensaje"), aviso.get("leido")));

	return getEntityManager().createQuery(q).getResultList();
    }

    
    public AvisoVO leerAviso(Integer avisoId, String usuarioId) {
	InvAviso entidad = obtenerEntidad(avisoId);
	if (entidad.getUsuario().getId().equals(usuarioId)) {
	    entidad.setLeido(Constantes.BOOLEAN_TRUE);
	    actulizarModificacion(entidad);
	    return new AvisoVO(entidad.getId(), entidad.getFecha(), entidad.getAsunto(), entidad.getMensaje(),
		    entidad.isLeido());
	}
	return new AvisoVO();
    }

    
    public void marcarComoLeidos(List<AvisoVO> avisos) {
	for (AvisoVO aviso : avisos) {
	    InvAviso entidad = obtenerEntidad(aviso.getId());
	    entidad.setLeido(Constantes.BOOLEAN_FALSE);
	    actulizarModificacion(entidad);
	}
    }

    
    public void eliminarAvisos(List<AvisoVO> avisos) {
	for (AvisoVO aviso : avisos) {
	    InvAviso entidad = obtenerEntidad(aviso.getId());
	    entidad.setEliminado(Constantes.BOOLEAN_TRUE);
	    actulizarModificacion(entidad);
	}
    }

    
    public String obtenerAvisoUrl(Integer avisoId) {
	return String.format("http://%s:%s/%s/views/inicio/mensaje.jsf?mensajeId=%d",
		hostName, port, contextPath, avisoId);
    }

    
    public String obtenerAvisoMovimientoUrl(Integer movimientoId) {
	return String.format("http://%s:%s/%s/views/inventarios/movimiento.jsf?transaccionId=%d",
		hostName, port, contextPath, movimientoId);
    }

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    private InvAviso obtenerEntidad(Integer avisoId) {
	return getEntityManager().find(InvAviso.class, avisoId);
    }

    private void actulizarModificacion(InvAviso entidad) {
	entidad.setModifico(entidad.getUsuario());
	entidad.setHoraModifico(new Date());
	entidad.setFechaModifico(new Date());
    }
}
