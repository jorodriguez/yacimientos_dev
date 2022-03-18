/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.vehiculo.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgAsignarVehiculo;
import sia.modelo.SgChecklist;
import sia.modelo.SgVehiculo;
import sia.modelo.SgVehiculoChecklist;
import sia.modelo.SiAdjunto;
import sia.modelo.SiOperacion;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.sgl.impl.SgChecklistImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgAsignarVehiculoImpl extends AbstractFacade<SgAsignarVehiculo>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgAsignarVehiculoImpl() {
	super(SgAsignarVehiculo.class);
    }
    @Inject
    private SgChecklistImpl sgChecklistRemote;
    @Inject
    private SiOperacionImpl siOperacionRemote;

    
    public List<SgAsignarVehiculo> traerVehiculoAsignado(Usuario usuario) {
	List<SgAsignarVehiculo> retVal = null;

	try {
	    retVal
		    = em.createQuery("SELECT av FROM SgAsignarVehiculo av "
			    + " WHERE av.usuario.id = :idUser "
			    + " AND av.eliminado = :eli "
			    + " AND av.siOperacion.id = :idOpera "
			    + " ORDER BY av.id  DESC "
		    ).setParameter("idOpera", 1)
		    .setParameter("idUser", usuario.getId())
		    .setParameter("eli", Constantes.NO_ELIMINADO)
		    .setMaxResults(5)
		    .getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, null, e);
	}

	return retVal;
    }

    
    public boolean guardarAsignacionVehiculo(Usuario sesion, String usuario, SgAsignarVehiculo sgAsignarVehiculo, int sgVehiculo, int idChecklist) {
	boolean v = false;
	try {
	    sgAsignarVehiculo.setUsuario(new Usuario(usuario));
	    sgAsignarVehiculo.setSgVehiculo(new SgVehiculo(sgVehiculo));
	    sgAsignarVehiculo.setSgChecklist(new SgChecklist(idChecklist));
	    sgAsignarVehiculo.setSiOperacion(new SiOperacion(Constantes.ID_SI_OPERACION_ASIGNAR));
	    sgAsignarVehiculo.setHoraOperacion(new Date());
	    sgAsignarVehiculo.setGenero(sesion);
	    sgAsignarVehiculo.setFechaGenero(new Date());
	    sgAsignarVehiculo.setHoraGenero(new Date());
	    sgAsignarVehiculo.setEliminado(Constantes.NO_ELIMINADO);
	    sgAsignarVehiculo.setTerminada(Constantes.BOOLEAN_FALSE);
	    sgAsignarVehiculo.setPertenece(0);

	    create(sgAsignarVehiculo);

	    v = true;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.toString(), e);
	}

	return v;
    }

    
    public boolean modificarAsiganacion(Usuario usuario, SgAsignarVehiculo sgAsignarVehiculo) {
	boolean v = false;
	
	try {
	    sgAsignarVehiculo.setModifico(usuario);
	    sgAsignarVehiculo.setFechaModifico(new Date());
	    sgAsignarVehiculo.setHoraModifico(new Date());
	    edit(sgAsignarVehiculo);
	    v = true;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.toString(), e);
	}

	return v;
    }

    
    public void eliminarAsiganacion(Usuario usuario, SgAsignarVehiculo sgAsignarVehiculo) {
	try {
	    sgAsignarVehiculo.setModifico(usuario);
	    sgAsignarVehiculo.setFechaModifico(new Date());
	    sgAsignarVehiculo.setHoraModifico(new Date());
	    sgAsignarVehiculo.setEliminado(Constantes.ELIMINADO);

	    edit(sgAsignarVehiculo);
	    
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.toString(), e);
	}
    }

    
    public boolean guardarCartaAsignacion(Usuario usuario, SgAsignarVehiculo sgAsignarVehiculo, SiAdjunto siAdjunto) {
	boolean v = false;
	try {
	    sgAsignarVehiculo.setTerminada(Constantes.BOOLEAN_FALSE);
	    sgAsignarVehiculo.setModifico(usuario);
	    sgAsignarVehiculo.setSiAdjunto(siAdjunto);
	    sgAsignarVehiculo.setFechaModifico(new Date());
	    sgAsignarVehiculo.setHoraModifico(new Date());

	    edit(sgAsignarVehiculo);
	    v = true;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.toString(), e);
	}
	return v;
    }

    
    public boolean guardarCartaRecepcion(Usuario usuario, SgAsignarVehiculo sgAsignarVehiculo, SiAdjunto siAdjunto) {
	boolean v = false;	
	try {
	    sgAsignarVehiculo.setTerminada(Constantes.BOOLEAN_TRUE);
	    sgAsignarVehiculo.setModifico(usuario);
	    sgAsignarVehiculo.setSiAdjunto(siAdjunto);
	    sgAsignarVehiculo.setFechaModifico(new Date());
	    sgAsignarVehiculo.setHoraModifico(new Date());

	    edit(sgAsignarVehiculo);
	    
	    v = true;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.toString(), e);
	}
	return v;
    }

    
    public void quitarCarta(Usuario usuario, SgAsignarVehiculo sgAsignarVehiculo, int idTipoEspecifico) {
	try {
	    sgAsignarVehiculo.setTerminada(Constantes.BOOLEAN_FALSE);
	    sgAsignarVehiculo.setModifico(usuario);
	    sgAsignarVehiculo.setSiAdjunto(null);
	    sgAsignarVehiculo.setFechaModifico(new Date());
	    sgAsignarVehiculo.setHoraModifico(new Date());
	    edit(sgAsignarVehiculo);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.toString());
	}
    }

    
    public List<SgAsignarVehiculo> traerAsignacionVehiculo(int sgVehiculo) {
	try {
	    return em.createQuery("SELECT av FROM SgAsignarVehiculo av "
		    + " WHERE av.sgVehiculo.id = :idV "
		    + " AND av.eliminado = :eli "
		    + "ORDER BY av.id  DESC ")
		    .setParameter("idV", sgVehiculo)
		    .setParameter("eli", Constantes.NO_ELIMINADO)
		    .setMaxResults(3).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.toString());
	    return null;
	}
    }

    
    public List<SgAsignarVehiculo> traerUsuarioPorVehiculo(int sgVehiculo) {
	try {
	    return em.createQuery(
		    "SELECT av FROM SgAsignarVehiculo av "
		    + " WHERE av.sgVehiculo.id = :idV "
		    + " AND av.eliminado = :eli AND av.id NOT IN "
		    + " (SELECT va.id FROM SgAsignarVehiculo va WHERE va.sgVehiculo.id = :idVehi "
		    + " AND va.siOperacion.id = :idOpera) "
		    + " ORDER BY av.id  DESC "
	    ).setParameter("idV", sgVehiculo)
		    .setParameter("eli", Constantes.NO_ELIMINADO)
		    .setParameter("idVehi", sgVehiculo)
		    .setParameter("idOpera", 2)
		    .getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.toString(), e);
	    return null;
	}
    }

    
    public void recibirVehiculo(Usuario usuario, int idAsigna, SgAsignarVehiculo sgAsignarVehiculo, SgVehiculoChecklist sgVehiculoChecklist, SgVehiculo sgVehiculo) {
	try {
	    SgAsignarVehiculo sav = new SgAsignarVehiculo();
	    sav.setSgVehiculo(sgVehiculo);
	    sav.setUsuario(sgAsignarVehiculo.getUsuario());
	    sav.setSgChecklist(sgVehiculoChecklist.getSgChecklist());
	    sav.setSiOperacion(siOperacionRemote.find(2));
	    sav.setObservacion(sgAsignarVehiculo.getObservacion());
	    sav.setFechaOperacion(sgAsignarVehiculo.getFechaOperacion());
	    sav.setHoraOperacion(new Date());
	    sav.setGenero(usuario);
	    sav.setFechaGenero(new Date());
	    sav.setHoraGenero(new Date());
	    sav.setEliminado(Constantes.NO_ELIMINADO);
	    sav.setPertenece(idAsigna);
	    sav.setTerminada(Constantes.BOOLEAN_TRUE);

	    create(sav);
	    //Modifica el registro anterior
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.toString(), e);
	}
    }

    
    public boolean modificarAsignaVehiculoDespuesRecibir(Usuario usuario, int idAsigna) {
	boolean v;
	try {
	    SgAsignarVehiculo asigna = find(idAsigna);
	    asigna.setTerminada(Constantes.BOOLEAN_TRUE);
	    asigna.setModifico(usuario);
	    asigna.setFechaModifico(new Date());
	    asigna.setHoraModifico(new Date());
	    edit(asigna);
	    v = true;
	} catch (Exception e) {

	    UtilLog4j.log.fatal(this, "Ocurrio un error en modificar AsignaDepuesRecibir" + e.toString());
	    v = false;
	}
	return v;
    }

    
    public SgAsignarVehiculo buscarRecepcionVehiculo(SgAsignarVehiculo sgAsignarVehiculo) {
	try {
	    return (SgAsignarVehiculo) em.createQuery("SELECT av FROM SgAsignarVehiculo av "
		    + " WHERE av.pertenece = :per"
		    + " AND av.eliminado = :eli ORDER BY av.id  DESC ").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("per", sgAsignarVehiculo.getId()).getSingleResult();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.toString());
	    return null;
	}
    }

    
    public boolean eliminarRecepcion(Usuario usuario, SgAsignarVehiculo sgAsignarVehiculo) {
	boolean v;
	try {
	    sgAsignarVehiculo.setEliminado(Constantes.ELIMINADO);
	    sgAsignarVehiculo.setTerminada(Constantes.BOOLEAN_FALSE);
	    sgAsignarVehiculo.setModifico(usuario);
	    sgAsignarVehiculo.setFechaModifico(new Date());
	    sgAsignarVehiculo.setHoraModifico(new Date());
	    edit(sgAsignarVehiculo);
	    v = true;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepción al elimianr recepcion " + e.getMessage());
	    v = false;
	}
	return v;
    }

    
    public void asignacionSinTerminar(Usuario usuario, SgAsignarVehiculo sgAsignarVehiculo) {
	try {
	    sgAsignarVehiculo.setTerminada(Constantes.BOOLEAN_FALSE);
	    sgAsignarVehiculo.setModifico(usuario);
	    sgAsignarVehiculo.setFechaModifico(new Date());
	    sgAsignarVehiculo.setHoraModifico(new Date());
	    edit(sgAsignarVehiculo);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepción al elimianr recepcion " + e.getMessage());
	}
    }

    
    public SgAsignarVehiculo buscarUsuarioPorVehiculo(int idVehiculo) {
	try {
	    return (SgAsignarVehiculo) em.createQuery("SELECT av FROM SgAsignarVehiculo av "
		    + " WHERE av.sgVehiculo.id = :v"
		    + " AND av.eliminado = :eli "
		    + " AND av.pertenece = :cero"
		    + " AND av.terminada = :ter")
		    .setParameter("eli", Constantes.NO_ELIMINADO)
		    .setParameter("v", idVehiculo)
		    .setParameter("ter", Constantes.BOOLEAN_FALSE)
		    .setParameter("cero", 0)
		    .getSingleResult();
	} catch (NoResultException nre) {
	    UtilLog4j.log.fatal(this, "No existe responsable para el vehículo: " + idVehiculo);
	    return null;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    return null;
	}
    }

    
    public UsuarioVO traerResponsableVehiculo(int idVehiculo) {
	UsuarioVO usuarioVO;
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append("select u.nombre, g.nombre, u.email, u.telefono, av.fecha_operacion, u.id from SG_ASIGNAR_VEHICULO av "
		    + " inner join usuario u on av.usuario = u.id "
		    + " inner join gerencia g on u.gerencia = g.id "
		    + " where av.sg_vehiculo = ? "
		    + " and av.pertenece = ? "
		    + " and av.terminada = ? "
		    + " and av.eliminado = ? ");
	    Object[] objects = (Object[]) em.createNativeQuery(sb.toString())
		    .setParameter(1, idVehiculo)
		    .setParameter(2, Constantes.CERO)
		    .setParameter(3, Constantes.BOOLEAN_FALSE)
		    .setParameter(4, Constantes.NO_ELIMINADO)
		    .getSingleResult();
	    usuarioVO = castUsuario(objects);
	} catch (NoResultException ex) {
	    UtilLog4j.log.fatal(this, "No hay responsable del vehículo  . . . . " + ex.getMessage());
	    usuarioVO = null;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Ocurrio un error  al traer el empleado responsable del vehículo  . . . . " + e.getMessage());
	    usuarioVO = null;
	}
	return usuarioVO;
    }

    private UsuarioVO castUsuario(Object[] objects) {
	UsuarioVO usuarioVO = new UsuarioVO();
	usuarioVO.setNombre((String) objects[0]);
	usuarioVO.setGerencia((String) objects[1]);
	usuarioVO.setMail((String) objects[2]);
	usuarioVO.setTelefono((String) objects[3]);
	usuarioVO.setFechaIngreso((Date) objects[4]);
	usuarioVO.setId((String) objects[5]);
	return usuarioVO;
    }

    
    public int contarAsignacion(int idVehiculo) {
	clearQuery();
	query.append("select count(av.SG_VEHICULO) from SG_ASIGNAR_VEHICULO av");
	query.append("  where av.SG_VEHICULO = ").append(idVehiculo);
	query.append("  and av.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
    }

    
    public VehiculoVO traerVehiculobyResponsable(String usuario) {
	VehiculoVO vehiculoVO;
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append("select V.ID, V.NUMERO_PLACA,V.CAPACIDAD_PASAJEROS,m.NOMBRE,mo.NOMBRE,c.NOMBRE from SG_ASIGNAR_VEHICULO av "
		    + " inner join SG_VEHICULO v on v.ID=av.SG_VEHICULO "
		    + " inner join SG_MARCA m on m.ID=v.SG_MARCA "
		    + " inner join SG_MODELO mo on mo.ID=v.SG_MODELO "
		    + " inner join SG_COLOR c on c.id=v.SG_COLOR "
		    + " where av.USUARIO = ? "
		    + " and av.pertenece = ? "
		    + " and av.terminada = ? "
		    + " and av.eliminado = ? "
                    + " and v.eliminado = ? "
                    + " and v.baja = ? "
                    + " limit 1 ");
	    Object[] objects = (Object[]) em.createNativeQuery(sb.toString())
		    .setParameter(1, usuario)
		    .setParameter(2, Constantes.CERO)
		    .setParameter(3, Constantes.BOOLEAN_FALSE)
		    .setParameter(4, Constantes.NO_ELIMINADO)
                    .setParameter(5, Constantes.NO_ELIMINADO)
                    .setParameter(6, Constantes.BOOLEAN_FALSE)
		    .getSingleResult();
	    vehiculoVO = castVehiculoVO(objects);
	} catch (NoResultException ex) {
	    UtilLog4j.log.fatal(this, "No hay responsable del vehículo  . . . . " + ex.getMessage());
	    vehiculoVO = null;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Ocurrio un error  al traer el empleado responsable del vehículo  . . . . " + e.getMessage());
	    vehiculoVO = null;
	}
	return vehiculoVO;
    }

    public VehiculoVO castVehiculoVO(Object[] object) {
	VehiculoVO v = new VehiculoVO();
	v.setId((Integer) object[0]);
	v.setNumeroPlaca((String) object[1]);
	v.setCapacidadPasajeros((Integer) object[2]);
	v.setMarca((String) object[3]);
	v.setModelo((String) object[4]);
	v.setColor((String) object[5]);
	return v;
    }

    private String consulta() {
	String sb = "select  v.ID, ma.NOMBRE, mo.NOMBRE,  co.NOMBRE, te.NOMBRE, v.CAPACIDAD_PASAJEROS, v.NUMERO_PLACA, "
		+ " v.SERIE, ma.id, mo.id, co.id, te.id, o.id, o.nombre, v.numero_economico, v.numero_activo, "
		+ " v.seguro, v.gps, v.caja_herramienta, p.id, p.nombre, e.id, e.nombre, k.id, k.kilometraje"
		+ " from Sg_Asignar_Vehiculo va "
		+ "	inner join USUARIO u on va.USUARIO =  u.ID"
		+ "	left  join SI_ADJUNTO a on va.SI_ADJUNTO = a.ID"
		+ "	inner join SG_CHECKLIST ch on va.SG_CHECKLIST = ch.ID"
		+ "	inner join sg_vehiculo v on va.SG_VEHICULO = v.ID"
		+ "	inner join SG_MODELO mo on v.SG_MODELO = mo.ID"
		+ "	inner join SG_MARCA ma on v.SG_MARCA= ma.ID"
		+ "	inner join SG_COLOR co on v.SG_COLOR  = co.ID"
		+ "	inner join SG_TIPO_ESPECIFICO te on v.SG_TIPO_ESPECIFICO = te.ID"
		+ "	inner join sg_oficina o on v.sg_oficina = o.id"
		+ "	left join proveedor p on v.proveedor = p.id"
		+ "	inner join estatus e on v.estatus = e.id"
		+ "	inner join SG_KILOMETRAJE k on k.SG_VEHICULO = v.ID and k.ACTUAL = 'True' ";
	return sb;
    }

    
    public List<VehiculoVO> buscarVehiculoAsignado(String usuario) {
	try {
	    String q = consulta()
		    + " WHERE va.usuario = ? "
		    + " AND va.eliminado = 'False' "
		    + " and va.TERMINADA = 'False' "
		    + " and u.eliminado = 'False' ";
	    List<Object[]> lo = em.createNativeQuery(q).setParameter(1, usuario).getResultList();
	    List<VehiculoVO> vehiculos = null;
	    if (lo != null) {
		vehiculos = new ArrayList<VehiculoVO>();
		for (Object[] object : lo) {
		    vehiculos.add(castVehiculo(object));
		}
	    }
	    return vehiculos;
	} catch (Exception e) {
	    UtilLog4j.log.error(e);
	}
	return null;
    }

    private VehiculoVO castVehiculo(Object[] object) {
	VehiculoVO v = new VehiculoVO();
	v.setId((Integer) object[0]);
	v.setMarca((String) object[1]);
	v.setModelo((String) object[2]);
	v.setColor((String) object[3]);
	v.setTipoEspecifico((String) object[4]);
	v.setCapacidadPasajeros((Integer) object[5]);
	v.setNumeroPlaca((String) object[6]);
	v.setSerie((String) object[7]);
	v.setIdMarca((Integer) object[8]);
	v.setIdModelo((Integer) object[9]);
	v.setIdColor((Integer) object[10]);
	v.setIdTipoEspecifico((Integer) object[11]);
	v.setIdOficina((Integer) object[12]);
	v.setOficina((String) object[13]);
	v.setNumeroEconomico((String) object[14]);
	v.setNumeroActivo((String) object[15]);
	v.setSeguro((String) object[16]);
	v.setGps((Boolean) object[17]);
	v.setCajaHerramienta((Boolean) object[18]);
	v.setIdProveedor(object[19] != null ? (Integer) object[19] : 0);
	v.setProveedor(v.getIdProveedor() != 0 ? (String) object[20] : Constantes.RFC_IHSA);
	v.setIdEstado(object[21] != null ? (Integer) object[21] : 0);
	v.setEstado((String) object[22]);
	v.setIdKilometraje((Integer) object[23]);
	return v;

    }
}
