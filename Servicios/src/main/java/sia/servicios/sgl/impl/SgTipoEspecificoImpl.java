/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgTipoTipoEspecifico;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.TipoEspecificoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgTipoEspecificoImpl extends AbstractFacade<SgTipoEspecifico> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private UsuarioImpl usuarioService;
    @Inject
    private SgTipoTipoEspecificoImpl sgTipoTipoEspecificoRemote;
    private StringBuilder q = new StringBuilder();

    public void limpiar() {
	q.delete(0, q.length());
    }

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgTipoEspecificoImpl() {
	super(SgTipoEspecifico.class);
    }

    
    public void guardarTipoEspecifico(int sgTipo, SgTipoEspecifico sgTipoEspecifico, Usuario usuario, boolean BOOLEAN_FALSE) {
	SgTipoEspecifico sgTipoEsp = buscarPorNombre(sgTipoEspecifico.getNombre(), BOOLEAN_FALSE);
	if (sgTipoEsp == null) {
	    sgTipoEspecifico.setGenero(usuario);
	    sgTipoEspecifico.setFechaGenero(new Date());
	    sgTipoEspecifico.setHoraGenero(new Date());
	    sgTipoEspecifico.setEliminado(BOOLEAN_FALSE);
	    sgTipoEspecifico.setUsado(Constantes.BOOLEAN_FALSE);
	    sgTipoEspecifico.setSistema(BOOLEAN_FALSE);
	    create(sgTipoEspecifico);

	    sgTipoTipoEspecificoRemote.guardarRelacionTipoTipoEspecifico(sgTipo, sgTipoEspecifico, usuario, BOOLEAN_FALSE);
	} else {
	    modificarTipoEspecifico(sgTipoEspecifico, usuario);
	    sgTipoTipoEspecificoRemote.modificarTipoEspecifico(sgTipo, sgTipoEsp, usuario, BOOLEAN_FALSE);
	}
    }

    
    public Object[] save(int tipo, SgTipoEspecifico tipoEspecifico, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgTipoEspecificoImpl.save()");

	Object[] array = new Object[2];

	if (tipo > 0 && tipoEspecifico != null && idUsuario != null && !idUsuario.equals("")) {
	    SgTipoEspecifico tipoEspecificoExistente = buscarPorNombre(tipoEspecifico.getNombre(), Constantes.BOOLEAN_FALSE);
	    UtilLog4j.log.info(this, "Tipo específicoExistente? --> " + (tipoEspecificoExistente != null ? true : false));
	    if (tipoEspecificoExistente != null) { //Ya existe el TipoEspecifico
		//Buscar si existe la relación entre el tipo y el tipoEspecífico
		SgTipoTipoEspecifico tipoTipoEspecifico = sgTipoTipoEspecificoRemote.buscarPorTipoPorTipoEspecifico(tipo, tipoEspecificoExistente);
		UtilLog4j.log.info(this, "Relación Tipo-TipoEspecífico existe? --> " + (tipoTipoEspecifico != null ? true : false));
		if (tipoTipoEspecifico != null) { //Ya existe la relación
		    throw new SIAException("SgTipoEspecificoImpl", "save()", "Ya existe el Tipo: " + tipoEspecifico.getNombre());
		} else {
		    //Sólo crear la relación entre el tipoEspecifico existente y el SgTipo
		    sgTipoTipoEspecificoRemote.guardarRelacionTipoTipoEspecifico(tipo, tipoEspecificoExistente, usuarioService.find(idUsuario), Constantes.NO_ELIMINADO);
		    array[0] = tipoEspecificoExistente;
		    array[1] = "Se ha usado un Tipo Específico existente para crear el actual";
		}
	    } else { //No existe el Tipo Específico
		//Crear el Tipo Específico
		tipoEspecifico.setFechaGenero(new Date());
		tipoEspecifico.setHoraGenero(new Date());
		tipoEspecifico.setEliminado(Constantes.NO_ELIMINADO);
		tipoEspecifico.setUsado(Constantes.BOOLEAN_FALSE);
		tipoEspecifico.setSistema(Constantes.BOOLEAN_FALSE);
		tipoEspecifico.setGenero(new Usuario(idUsuario));

		super.create(tipoEspecifico);

		//Crear la relación con el Tipo
		sgTipoTipoEspecificoRemote.guardarRelacionTipoTipoEspecifico(tipo, tipoEspecifico, usuarioService.find(idUsuario), Constantes.NO_ELIMINADO);
		array[0] = tipoEspecifico;
		array[1] = "";
	    }
	} else {
	    throw new SIAException(SgTipoEspecifico.class.getName(), "save()",
		    "Faltan parámetros para poder guardar el Tipo Específico",
		    ("Parámetros: tipo: " + (tipo)
		    + " tipoEspecifico: " + (tipoEspecifico != null ? tipoEspecifico.getId() : null)
		    + "idUsuario" + idUsuario));
	}

	return array;
    }

    
    public void modificarTipoEspecifico(SgTipoEspecifico sgTipoEspecifico, Usuario usuario) {
	sgTipoEspecifico.setGenero(usuario);
	sgTipoEspecifico.setFechaGenero(new Date());
	sgTipoEspecifico.setHoraGenero(new Date());
	sgTipoEspecifico.setEliminado(false);
	edit(sgTipoEspecifico);
    }

    
    public SgTipoEspecifico deleteTipoEspecifico(SgTipoEspecifico tipoEspecifico, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgTipoEspecificoImpl.deleteTipoEspecifico()");

	if (tipoEspecifico != null && idUsuario != null && !idUsuario.equals("")) {

	    if (tipoEspecifico.isUsado()) {
		tipoEspecifico.setGenero(new Usuario(idUsuario));
		tipoEspecifico.setFechaGenero(new Date());
		tipoEspecifico.setHoraGenero(new Date());
		tipoEspecifico.setEliminado(Constantes.ELIMINADO);
		super.edit(tipoEspecifico);
	    } else {
		throw new SIAException(SgTipoEspecificoImpl.class.getName(),
			"deleteTipoEspecifico()",
			"No se puede eliminar el Tipo Específico porque ya está siendo usado. Contacta al Equipo del SIA para cualquier aclaración al correo soportesia@ihsa.mx");
	    }
	} else {
	    throw new SIAException(SgTipoEspecificoImpl.class.getName(), "deleteTipoEspecifico()",
		    "Faltan parámetros para poder eliminar el TipoEspecífico",
		    ("Parámetros: tipoEspecifico: " + (tipoEspecifico != null ? tipoEspecifico : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "TipoEspecifico ELIMINADO SATISFACTORIAMENTE");

	return tipoEspecifico;
    }

    
    public List<SgTipoEspecifico> traerTipoEspecificoSinTipo(SgTipo sgTipo, SgTipoEspecifico sgTipoEspecifico, boolean BOOLEAN_FALSE) {
	try {
	    return em.createQuery("SELECT te FROM SgTipoEspecifico te "
		    + "WHERE te.eliminado = :eli "
		    + "AND te.id NOT IN "
		    + "(SELECT tt.sgTipoEspecifico.id FROM SgTipoTipoEspecifico tt  "
		    + "WHERE tt.sgTipo.id = :idT AND tt.eliminado = :eliminado) "
		    + "ORDER BY te.id ASC").setParameter("eliminado", BOOLEAN_FALSE).setParameter("idT", sgTipo.getId()).setParameter("eli", BOOLEAN_FALSE).getResultList();
	} catch (Exception e) {
	    return null;
	}

    }

    
    public void asignarTipoEspecifico(List<SgTipoEspecifico> listaFilasSeleccionadas, int sgTipo, Usuario usuario, boolean BOOLEAN_FALSE) {
	for (SgTipoEspecifico sgTipoEsp : listaFilasSeleccionadas) {
	    SgTipoTipoEspecifico sgTipoTipoEspecifico = sgTipoTipoEspecificoRemote.buscarPorTipoPorTipoEspecifico(sgTipo, sgTipoEsp);
	    if (sgTipoTipoEspecifico != null) {
		sgTipoTipoEspecificoRemote.modificarRelacionTipoEspecifico(sgTipoTipoEspecifico, usuario, BOOLEAN_FALSE);
	    } else {
		sgTipoTipoEspecificoRemote.guardarRelacionTipoTipoEspecifico(sgTipo, sgTipoEsp, usuario, BOOLEAN_FALSE);
	    }
	}
    }

    
    public SgTipoEspecifico buscarPorNombre(String tipoEspecifico, boolean eliminado) {
	try {
	    return (SgTipoEspecifico) em.createQuery("SELECT te FROM SgTipoEspecifico te "
		    + "WHERE te.nombre = :nombre "
		    + " AND te.eliminado = :eliminado").setParameter("nombre", tipoEspecifico).setParameter("eliminado", eliminado).getSingleResult();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepción al bucar por nombre el Tipo Específico");
	    UtilLog4j.log.fatal(this, e.getMessage());
	    e.printStackTrace();
	    return null;
	}
    }

    
    public void ponerUsadoTipoEspecifico(int idTipoEspecifico, Usuario usuario) {
	UtilLog4j.log.info(this, "SgTipoEspecificoImpl.ponerUsadoTipoEspecifico()");

	SgTipoEspecifico sgTipoEspecifico = find(idTipoEspecifico);

	sgTipoEspecifico.setFechaModifico(new Date());
	sgTipoEspecifico.setHoraModifico(new Date());
	sgTipoEspecifico.setUsado(Constantes.BOOLEAN_TRUE);
	sgTipoEspecifico.setModifico(usuario);
	edit(sgTipoEspecifico);
    }

    
    public List<TipoEspecificoVo> traerTipoEspecificoPorRango(int min, int max) {

	try {
	    limpiar();
	    q.append("SELECT te.id, te.nombre, te.descripcion FROM sg_tipo_especifico te ");
	    q.append(" WHERE te.id  between ").append(min).append(" and ").append(max);
	    q.append(" and te.eliminado  = '").append(Constantes.NO_ELIMINADO).append("'");
	    q.append(" order by  te.nombre asc ");
	    List<Object[]> l = em.createNativeQuery(q.toString()).getResultList();
	    List<TipoEspecificoVo> lte = new ArrayList<TipoEspecificoVo>();
	    for (Object[] objects : l) {
		lte.add(castTipoEspecifico(objects));
	    }
	    return lte;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    e.printStackTrace();
	    return null;
	}
    }

    private TipoEspecificoVo castTipoEspecifico(Object[] objects) {
	TipoEspecificoVo tev = new TipoEspecificoVo();
	tev.setId((Integer) objects[0]);
	tev.setNombre((String) objects[1]);
	tev.setDescripcion((String) objects[2]);
	return tev;

    }

    
    public TipoEspecificoVo buscarPorNombre(String servicioVehiculo) {
	limpiar();
	q.append("SELECT te.id, te.nombre, te.descripcion FROM sg_tipo_especifico te ");
	q.append(" WHERE te.nombre = '").append(servicioVehiculo).append("'");
	Object[] o = (Object[]) em.createNativeQuery(q.toString()).getSingleResult();
	return castTipoEspecifico(o);
    }
}
