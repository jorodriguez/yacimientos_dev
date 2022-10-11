package sia.servicios.sgl.accesorio.impl;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgAccesorio;
import sia.modelo.SgAsignarAccesorio;
import sia.modelo.SiAdjunto;
import sia.modelo.SiCondicion;
import sia.modelo.SiOperacion;
import sia.modelo.Usuario;
import sia.modelo.sgl.accesorio.AccesorioAsignadoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.servicios.sistema.impl.SiCondicionImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgAsignarAccesorioImpl extends AbstractFacade<SgAsignarAccesorio> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }
    @Inject
    private SiCondicionImpl siCondicionRemote;
    @Inject
    private SiOperacionImpl siOperacionRemote;

    public SgAsignarAccesorioImpl() {
	super(SgAsignarAccesorio.class);
    }

    
    public List<SgAsignarAccesorio> traerAsignacioAccesorio(int idAccesorio) {
	try {
	    return em.createQuery("SELECT a FROM SgAsignarAccesorio a WHERE "
		    + "  a.sgAccesorio.id = :idAcc "
		    + " AND a.eliminado = :eli "
		    + " ORDER BY a.id DESC").setParameter("idAcc", idAccesorio).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
	} catch (Exception e) {
	    return null;
	}
    }

    /**
     *
     * @param sesion
     * @param idAccesorio
     * @param idCondicion
     * @param usuario
     * @param fechaOperacion
     * @return
     */
    
    public SgAsignarAccesorio guardarAsignarAccesorio(String sesion, int idAccesorio, int idCondicion,
	    String usuario, Date fechaOperacion) {
	SgAsignarAccesorio sgAsignarAccesorio = new SgAsignarAccesorio();
	try {
	    sgAsignarAccesorio.setSgAccesorio(new SgAccesorio(idAccesorio));
	    sgAsignarAccesorio.setUsuario(new Usuario(usuario));
	    sgAsignarAccesorio.setSiCondicion(new SiCondicion(idCondicion));
	    sgAsignarAccesorio.setSiOperacion(new SiOperacion(1));
	    sgAsignarAccesorio.setFechaOperacion(fechaOperacion);
	    sgAsignarAccesorio.setHoraOperacion(new Date());
	    sgAsignarAccesorio.setGenero(new Usuario(sesion));
	    sgAsignarAccesorio.setFechaGenero(new Date());
	    sgAsignarAccesorio.setHoraGenero(new Date());
	    sgAsignarAccesorio.setTerminada(Constantes.BOOLEAN_FALSE);
	    sgAsignarAccesorio.setRecibido(Constantes.BOOLEAN_FALSE);
	    sgAsignarAccesorio.setEliminado(Constantes.NO_ELIMINADO);
	    sgAsignarAccesorio.setPertenece(0);
	    create(sgAsignarAccesorio);
	} catch (Exception ex) {
	    sgAsignarAccesorio = null;
	    Logger.getLogger(SgAsignarAccesorioImpl.class.getName()).log(Level.SEVERE, null, ex);
	}
	return sgAsignarAccesorio;
    }

    
    public void modificarAsignacionAccesorio(Usuario usuario, SgAsignarAccesorio sgAsignarAccesorio, int idCondicion) {
	try {
	    sgAsignarAccesorio.setSiCondicion(siCondicionRemote.find(idCondicion));
	    sgAsignarAccesorio.setModifico(usuario);
	    sgAsignarAccesorio.setFechaModifico(new Date());
	    sgAsignarAccesorio.setHoraModifico(new Date());
	    edit(sgAsignarAccesorio);

        } catch (Exception ex) {
	    Logger.getLogger(SgAsignarAccesorioImpl.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    
    public void modificarRecepcionAccesorio(String usuario, int idAsignar) {
	try {
	    SgAsignarAccesorio sgAsignarAccesorio = find(idAsignar);
	    sgAsignarAccesorio.setModifico(new Usuario(usuario));
	    sgAsignarAccesorio.setFechaModifico(new Date());
	    sgAsignarAccesorio.setHoraModifico(new Date());
	    edit(sgAsignarAccesorio);
	} catch (Exception ex) {
	    Logger.getLogger(SgAsignarAccesorioImpl.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    
    public void eliminarAsignacionAccesorio(String usuario, int idAsignarAccesorio) {
	try {
	    SgAsignarAccesorio sgAsignarAccesorio = find(idAsignarAccesorio);
	    sgAsignarAccesorio.setModifico(new Usuario(usuario));
	    sgAsignarAccesorio.setFechaModifico(new Date());
	    sgAsignarAccesorio.setFechaModifico(new Date());
	    sgAsignarAccesorio.setEliminado(Constantes.ELIMINADO);
	    edit(sgAsignarAccesorio);
	} catch (Exception ex) {
	    Logger.getLogger(SgAsignarAccesorioImpl.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    
    public boolean eliminarCarta(Usuario usuario, int idAsignarAccesorio) {
	boolean v;
	try {
	    SgAsignarAccesorio sgAsignarAccesorio = find(idAsignarAccesorio);
	    sgAsignarAccesorio.setSiAdjunto(null);
	    sgAsignarAccesorio.setFechaModifico(new Date());
	    sgAsignarAccesorio.setHoraModifico(new Date());
	    edit(sgAsignarAccesorio);
	    v = true;
	} catch (Exception ex) {
	    Logger.getLogger(SgAsignarAccesorioImpl.class.getName()).log(Level.SEVERE, null, ex);
	    v = false;
	}
	return v;
    }

    
    public boolean subirCarta(Usuario usuario, int idAsignarAccesorio, SiAdjunto siAdjunto) {
	boolean v;
	try {
	    SgAsignarAccesorio sgAsignarAccesorio = find(idAsignarAccesorio);
	    sgAsignarAccesorio.setSiAdjunto(siAdjunto);
	    sgAsignarAccesorio.setFechaModifico(new Date());
	    sgAsignarAccesorio.setHoraModifico(new Date());
	    edit(sgAsignarAccesorio);
	    v = true;
	} catch (Exception ex) {
	    Logger.getLogger(SgAsignarAccesorioImpl.class.getName()).log(Level.SEVERE, null, ex);
	    v = false;
	}
	return v;
    }

    
    public boolean recibirAccesorio(Usuario usuario, SgAsignarAccesorio sgAsignarAccesorio, int idCondicion) {
	boolean v;
	try {
	    SgAsignarAccesorio sgAsignar = new SgAsignarAccesorio();
	    sgAsignar.setUsuario(sgAsignarAccesorio.getUsuario());
	    sgAsignar.setSiOperacion(new SiOperacion(Constantes.ID_SI_OPERACION_RECIBIR));
	    sgAsignar.setSiCondicion(siCondicionRemote.find(idCondicion));
	    sgAsignar.setSgAccesorio(sgAsignarAccesorio.getSgAccesorio());
	    sgAsignar.setPertenece(Constantes.UNO);
	    sgAsignar.setGenero(usuario);
	    sgAsignar.setFechaGenero(new Date());
	    sgAsignar.setHoraGenero(new Date());
	    sgAsignar.setFechaOperacion(new Date());
	    sgAsignar.setHoraOperacion(new Date());
	    sgAsignar.setRecibido(Constantes.BOOLEAN_TRUE);
	    sgAsignar.setTerminada(Constantes.BOOLEAN_TRUE);
	    sgAsignar.setEliminado(Constantes.NO_ELIMINADO);
	    create(sgAsignar);
	    v = true;
	} catch (Exception ex) {
	    Logger.getLogger(SgAsignarAccesorioImpl.class.getName()).log(Level.SEVERE, null, ex);
	    v = false;
	}
	return v;
    }

    
    public void ponerTerminandaAsignacion(int idAsignarAccesorio, String usuario) {
	SgAsignarAccesorio sgAsignarAccesorio = find(idAsignarAccesorio);
	sgAsignarAccesorio.setTerminada(Constantes.BOOLEAN_TRUE);
	sgAsignarAccesorio.setRecibido(Constantes.BOOLEAN_TRUE);
	sgAsignarAccesorio.setSiOperacion(new SiOperacion(Constantes.UNO));
	sgAsignarAccesorio.setFechaModifico(new Date());
	sgAsignarAccesorio.setHoraModifico(new Date());
	sgAsignarAccesorio.setModifico(new Usuario(usuario));
	edit(sgAsignarAccesorio);
    }

    
    public SgAsignarAccesorio buscarAsignarAccesorioRecibido(SgAsignarAccesorio sgAsignarAccesorio) {
	try {
	    return (SgAsignarAccesorio) em.createQuery("SELECT a FROM SgAsignarAccesorio a WHERE "
		    + "  a.sgAsignarAccesorio.pertenece = :p"
		    + " AND a.eliminado = :eli ").setParameter("p", sgAsignarAccesorio.getPertenece()).setParameter("eli", Constantes.NO_ELIMINADO).getSingleResult();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public AccesorioAsignadoVo buscarAccesorioAsingado(int idAccesorio) {
	AccesorioAsignadoVo accesorioAsignadoVo = null;
	try {
	    clearQuery();
	    query.append(conslulta());
	    query.append(" where ac.SG_ACCESORIO = ").append(idAccesorio);
	    query.append(" and ac.si_operacion = ").append(Constantes.ID_SI_OPERACION_ASIGNAR);
	    query.append("  and ac.recibido = '").append(Constantes.BOOLEAN_FALSE).append("'");
	    query.append(" and ac.PERTENECE = ").append(Constantes.CERO);
	    query.append(" and ac.TERMINADA = '").append(Constantes.BOOLEAN_FALSE).append("'");
	    query.append(" and ac.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	    //
	    Object[] onj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	    accesorioAsignadoVo = castAsignacion(onj);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(e);
	}
	return accesorioAsignadoVo;
    }

    private String conslulta() {
	StringBuilder sb = new StringBuilder();
	sb.append(" select ac.ID, ac.FECHA_OPERACION, ac.HORA_OPERACION, a.ID,t.nombre, te.nombre, mar.nombre, mo.nombre, a.DESCRIPCION, u.ID, u.NOMBRE, ");
	sb.append(" adj.ID, adj.NOMBRE, adj.UUID, ac.pertenece, ac.terminada, l.id, l.subcuenta, l.numero, e.id, e.nombre, o.nombre");
	sb.append(" from SG_ASIGNAR_ACCESORIO ac");
	sb.append("	    inner join USUARIO u on ac.USUARIO = u.ID");
	sb.append("	    inner join SG_ACCESORIO a on ac.SG_ACCESORIO = a.ID");
	sb.append("	    inner join SG_MODELO mo on a.sg_modelo = mo.ID");
	sb.append("	    inner join sg_marca mar on a.sg_marca = mar.ID");
	sb.append("	    inner join sg_tipo t on a.sg_tipo = t.ID");
	sb.append("	    inner join sg_tipo_especifico te on a.tipo_especifico = te.ID");
	sb.append("	    inner join sg_oficina o on a.sg_oficina = o.ID");
	sb.append("	    left join sg_linea l on a.SG_linea = l.ID");
	sb.append("	    left join estatus e on l.estado = e.ID");
	sb.append("	    left join SI_ADJUNTO adj on ac.SI_ADJUNTO = adj.ID");
	return sb.toString();
    }

    private AccesorioAsignadoVo castAsignacion(Object[] obj) {
	AccesorioAsignadoVo accesorioAsignadoVo = new AccesorioAsignadoVo();
	accesorioAsignadoVo.setId((Integer) obj[0]);
	accesorioAsignadoVo.setFechaAsignacion((Date) obj[1]);
	accesorioAsignadoVo.setHoraAsignacion((Date) obj[2]);
	accesorioAsignadoVo.getAccesorioVo().setId((Integer) obj[3]);
	accesorioAsignadoVo.getAccesorioVo().setTipo((String) obj[4]);
	accesorioAsignadoVo.getAccesorioVo().setTipoEspecifico((String) obj[5]);
	accesorioAsignadoVo.getAccesorioVo().setMarca((String) obj[6]);
	accesorioAsignadoVo.getAccesorioVo().setModelo((String) obj[7]);
	accesorioAsignadoVo.getAccesorioVo().setDescripcion((String) obj[8]);
	accesorioAsignadoVo.getUsuarioVO().setId((String) obj[9]);
	accesorioAsignadoVo.getUsuarioVO().setNombre((String) obj[10]);
	accesorioAsignadoVo.getAdjuntoVO().setId(obj[11] != null ? (Integer) obj[11] : Constantes.CERO);
	accesorioAsignadoVo.getAdjuntoVO().setNombre((String) obj[12]);
	accesorioAsignadoVo.getAdjuntoVO().setUuid((String) obj[13]);
	accesorioAsignadoVo.setPertenece((Integer) obj[14]);
	accesorioAsignadoVo.setTerminada((String) obj[15]);
	accesorioAsignadoVo.getAccesorioVo().getLineaVo().setId(obj[16] != null ? (Integer) obj[16] : 0);
	accesorioAsignadoVo.getAccesorioVo().getLineaVo().setSubCuenta((String) obj[17]);
	accesorioAsignadoVo.getAccesorioVo().getLineaVo().setNumero((String) obj[18]);
	accesorioAsignadoVo.getAccesorioVo().getLineaVo().setIdEstado(obj[19] != null ? (Integer) obj[19] : 0);
	accesorioAsignadoVo.getAccesorioVo().getLineaVo().setEstado((String) obj[20]);
	accesorioAsignadoVo.getAccesorioVo().getOficinaVO().setNombre((String) obj[21]);
	return accesorioAsignadoVo;
    }

    
    public List<AccesorioAsignadoVo> traerAsignacioPorUsuario(String idUsuario) {
	List<AccesorioAsignadoVo> lista = null;
	try {
	    clearQuery();
	    query.append(conslulta());
	    query.append(" where ac.usuario = '").append(idUsuario).append("'");
	    query.append(" and ac.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	    System.out.println("Asignaddo : " + query.toString());
	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	    if (lo != null) {
		lista = new ArrayList<AccesorioAsignadoVo>();
		for (Object[] obj : lo) {
		    lista.add(castAsignacion(obj));
		}
	    }

	} catch (Exception e) {
	    UtilLog4j.log.fatal(e);
	}
	return lista;
    }

    
    public List<AccesorioAsignadoVo> traerAsignacion() {
	List<AccesorioAsignadoVo> lista = null;
	try {
	    clearQuery();
	    query.append(conslulta());
	    query.append(" where  ac.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" and ac.si_operacion = ").append(Constantes.ID_SI_OPERACION_ASIGNAR);
	    query.append(" and ac.recibido = '").append(Constantes.BOOLEAN_FALSE).append("'");
	    query.append(" and ac.PERTENECE = ").append(Constantes.CERO);
	    query.append(" and ac.TERMINADA = '").append(Constantes.BOOLEAN_FALSE).append("'");
	    query.append(" and ac.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	    if (lo != null) {
		lista = new ArrayList<AccesorioAsignadoVo>();
		for (Object[] obj : lo) {
		    lista.add(castAsignacion(obj));
		}
	    }

	} catch (Exception e) {
	    UtilLog4j.log.fatal(e);
	}
	return lista;
    }

}
