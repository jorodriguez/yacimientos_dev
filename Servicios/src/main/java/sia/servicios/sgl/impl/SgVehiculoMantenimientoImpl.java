/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import sia.constantes.Constantes;
import sia.modelo.Proveedor;
import sia.modelo.SgVehiculo;
import sia.modelo.SgVehiculoMantenimiento;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.sgl.pago.vo.PagoServicioVo;
import sia.modelo.sgl.vehiculo.vo.SgMantenimientoVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@LocalBean 
public class SgVehiculoMantenimientoImpl extends AbstractFacade<SgVehiculoMantenimiento> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SgKilometrajeImpl sgKilometrajeService;
    @Inject
    private SgEstadoVehiculoImpl sgEstadoVehiculoService;
    @Inject
    private MonedaImpl monedaService;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    //
    StringBuilder qry = new StringBuilder();
    
    @Inject
    DSLContext context;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgVehiculoMantenimientoImpl() {
	super(SgVehiculoMantenimiento.class);
    }

    
    public void registroEntradaMantenimiento(int idTipoMantenimiento, VehiculoVO sgVehiculo, Proveedor proveedor, Integer kilomentraje, Usuario usuarioGenero, String Observaciones, Date fechaIngreso) {
	UtilLog4j.log.info(this, "entrando a registrar mantenimiento");
	UtilLog4j.log.info(this, "idtipoMatenimiento " + idTipoMantenimiento);
	UtilLog4j.log.info(this, "vehiculo " + sgVehiculo.getSerie());
	UtilLog4j.log.info(this, "proveedor " + proveedor.getNombre());
	SgVehiculoMantenimiento vMantenimientoOld = null;	
	try {
	    SgVehiculoMantenimiento mantenimiento = new SgVehiculoMantenimiento();
	    mantenimiento.setSgKilometraje(sgKilometrajeService.createKilometrajeActual(sgVehiculo.getId(), idTipoMantenimiento, kilomentraje, usuarioGenero));
	    mantenimiento.setSgVehiculo(new SgVehiculo(sgVehiculo.getId()));
	    mantenimiento.setProveedor(proveedor);
	    mantenimiento.setEliminado(Constantes.BOOLEAN_FALSE);
	    mantenimiento.setGenero(usuarioGenero);
	    mantenimiento.setFechaGenero(new Date());
	    mantenimiento.setHoraGenero(new Date());
	    mantenimiento.setObservacion(Observaciones);
	    mantenimiento.setFechaIngreso(fechaIngreso);
	    mantenimiento.setFechaSalida(null);
	    mantenimiento.setTerminado(Constantes.BOOLEAN_FALSE);
	    mantenimiento.setImporte(BigDecimal.ZERO);

	    mantenimiento.setActual(idTipoMantenimiento == 4 ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);

	    if (idTipoMantenimiento == 4) {
		UtilLog4j.log.info(this, "Es un mantenimineto Preventivo...");
		//buscar el ultimo mantenimiento preventivo y ponerlo actual = false
		vMantenimientoOld = findUltimoMantenimientoPreventivo(sgVehiculo.getId());
		if (vMantenimientoOld != null) {		    
		    vMantenimientoOld.setActual(Constantes.BOOLEAN_FALSE);
		    edit(vMantenimientoOld);
		    UtilLog4j.log.info(this, "se edito el ultimo mantenimiento preventivo");
		}
	    }
	    create(mantenimiento);

	    try {
		this.sgEstadoVehiculoService.createEstadoVehiculoActual(sgVehiculo.getId(), idTipoMantenimiento, Observaciones, usuarioGenero);
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, "Excepción al guardar el log");
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
	    }

//            //poner indisponible el vehiculo
	    //sgVehiculoService.asigarDisponibilidad(sgVehiculo, Constantes.BOOLEAN_FALSE, usuarioGenero);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en registro de mantenimiento  " + e.getMessage());
	}
    }

    
    public void modificarRegistroEntradaMantenimiento(SgVehiculoMantenimiento sgMantenimiento, int idTipoMantenimiento, Proveedor proveedor, Integer kilomentrajeNuevo, Usuario usuarioModifico, String Observaciones, Date fechaIngreso) {	
	try {
	    if (!sgMantenimiento.isTerminado()) {
		sgMantenimiento.setFechaIngreso(fechaIngreso);
		sgMantenimiento.setFechaModifico(new Date());
		sgMantenimiento.setHoraModifico(new Date());
		sgMantenimiento.setModifico(usuarioModifico);
		sgMantenimiento.setObservacion(Observaciones);
		sgMantenimiento.setSgKilometraje(sgKilometrajeService.editKilometrajeActual(sgMantenimiento.getSgKilometraje(), idTipoMantenimiento, kilomentrajeNuevo, usuarioModifico));
		//sgMantenimiento.setSgVehiculo(sgMantenimiento.getSgVehiculo());
		sgMantenimiento.setProveedor(proveedor);
		sgMantenimiento.setImporte(BigDecimal.ZERO);
		//poner el registro entrante como actual = 'True'
//                if (idTipoMantenimiento == 4) {
//                    sgMantenimiento.setActual(Constantes.BOOLEAN_TRUE);
//                }
		//modificar el estado del vehiculo
		this.sgEstadoVehiculoService.editEstadoVehiculo(sgMantenimiento.getSgVehiculo(),
			idTipoMantenimiento,
			Observaciones,
			usuarioModifico);

		edit(sgMantenimiento);

		UtilLog4j.log.info(this, "Todo bien en la modificacion de entrada a mantenimiento");
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en la modificacion de entrada a Mantenimiento " + e.getMessage());
	}
    }

    
    public void registroSalidaMantenimiento(SgVehiculoMantenimiento sgMantenimiento, int idMoneda, Usuario usuarioModifico) {
	try {
	    sgMantenimiento.setMoneda(monedaService.find(idMoneda));
	    sgMantenimiento.setModifico(usuarioModifico);
	    sgMantenimiento.setFechaModifico(new Date());
	    sgMantenimiento.setHoraModifico(new Date());
	    sgMantenimiento.setTerminado(Constantes.BOOLEAN_FALSE);
	    edit(sgMantenimiento);
	    this.sgEstadoVehiculoService.activarDesactivarEstado(this.sgEstadoVehiculoService.findEstadoVehiculoActual(sgMantenimiento.getSgVehiculo().getId()), usuarioModifico, false);
	    UtilLog4j.log.info(this, "Todo bien en la salida de mantenimiento");
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en el registro de salida de Mantenimiento " + e.getMessage());
	}
    }

    
    public void terminarMantenimiento(SgVehiculoMantenimiento sgMantenimiento, Usuario usuario) {
	UtilLog4j.log.info(this, "termianr mantenimiento ");
	try {
	    sgMantenimiento.setTerminado(Constantes.BOOLEAN_TRUE);
	    sgMantenimiento.setModifico(usuario);
	    sgMantenimiento.setFechaModifico(new Date());
	    sgMantenimiento.setHoraModifico(new Date());
	    edit(sgMantenimiento);
	    UtilLog4j.log.info(this, "Mantenimiento terminado");
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al terminar el mantenimiento " + e.getMessage());

	}
    }

    
    public void eliminarRegistroMantenimiento(SgVehiculoMantenimiento sgMantenimiento, Usuario usuarioGenero) {
	SgVehiculoMantenimiento vMantenimientoOld = null;
	SgVehiculoMantenimiento vMantenimientoOldPreve = null;
	try {
	    sgMantenimiento.setEliminado(Constantes.BOOLEAN_TRUE);
	    edit(sgMantenimiento);
	    UtilLog4j.log.info(this, "Todo bien en eliminar mantenimiento");

	    //activar el ultimo registro de mantenimiento
	    vMantenimientoOld = findUltimoMantenimientoParaActivar(sgMantenimiento.getSgVehiculo().getId());
	    if (vMantenimientoOld != null) {
		vMantenimientoOld.setTerminado(Constantes.BOOLEAN_FALSE);
		edit(vMantenimientoOld);
	
		if (sgMantenimiento.getSgKilometraje().getSgTipoEspecifico().getId() == 4) {
		    vMantenimientoOldPreve = findUltimoMantenimientoPreventivo(sgMantenimiento.getSgVehiculo().getId());
		    vMantenimientoOldPreve.setActual(Constantes.BOOLEAN_TRUE);
		    edit(vMantenimientoOldPreve);
		    UtilLog4j.log.info(this, "Se edito el mantenimiento preventivo");
		}

	    } else {
		UtilLog4j.log.info(this, "No existieron otros mantenimientos para activar..");
	    }

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en la eliminacion de registro de Mantenimiento " + e.getMessage());
	}
    }

    //traer el ultimo mantenimiento para activarlo
    private SgVehiculoMantenimiento findUltimoMantenimientoParaActivar(Integer idVehiculo) {
	UtilLog4j.log.info(this, "findUltimoMantenimientoParaActivar");
	//Select vm.* from SG_VEHICULO_MANTENIMIENTO vm
	//Where vm.id = (Select max(m.id) From SG_VEHICULO_MANTENIMIENTO m Where m.SG_VEHICULO = 7 AND m.ELIMINADO = 'False')
	try {
	    return (SgVehiculoMantenimiento) em.createQuery("Select vm from SgVehiculoMantenimiento vm "
		    + " Where vm.id = (Select max(m.id) From SgVehiculoMantenimiento m Where m.sgVehiculo.id = :idVehiculo AND m.eliminado = :eliminado)").setParameter("idVehiculo", idVehiculo).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getSingleResult();
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	    return null;
	}

    }

    //traer ultimo mantenimiento de tipo preventivo para poner actual = 'True' o 'False'
    private SgVehiculoMantenimiento findUltimoMantenimientoPreventivo(Integer idVehiculo) {
	UtilLog4j.log.info(this, "IdVehiculo a buscar " + idVehiculo);
	String q = "";
	Integer id;
	Query a = null;
	try {
	    SgMantenimientoVo vo = this.findMantenimientosMaxResults(idVehiculo, 4, 3).get(0);
	    UtilLog4j.log.info(this, vo != null ? "Encotrado " + vo.getId() : " NULL");
	    return find(vo.getId());
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	    return null;
	}

    }

    
    public void desactivarMantenimientoPreventivoActual(Integer idVehiculo, String idUsuario) {
	try {
	    SgVehiculoMantenimiento vm = findUltimoMantenimientoPreventivo(idVehiculo);
	    if (vm != null) {
		vm.setActual(Constantes.BOOLEAN_FALSE);
		edit(vm);
		UtilLog4j.log.info(this, "Se desactivo el mantenimiento preventivo actual ");
	    }

	} catch (Exception e) {

	    UtilLog4j.log.fatal(this, "Excepcion al desactivar el mantenimiento preventivo " + e.getMessage());
	}

    }

    
    public SgVehiculoMantenimiento findRegistroEntradaNOTerminado(int sgVehiculo) {
	SgVehiculoMantenimiento ret = null;
	try {
	    ret = (SgVehiculoMantenimiento) em.createQuery("SELECT m FROM SgVehiculoMantenimiento m "
		    + " WHERE m.sgVehiculo.id = :idVehiculo AND m.terminado = :terminado AND m.eliminado = :eliminado and m.actual = :actual ").setParameter("actual", Constantes.BOOLEAN_TRUE).setParameter("idVehiculo", sgVehiculo).setParameter("terminado", Constantes.BOOLEAN_FALSE).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getSingleResult();
	    if (ret != null) {
		return ret;
	    } else {
		return null;
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "excepcion al buscar el registro no terminado de entrada  a mantenimiento " + e.getMessage());
	    return null;
	}
    }

    
    public List<SgMantenimientoVo> findMantenimientosMaxResults(Integer idVehiculo, int idTipoEspecifico, int maxResults) {
	List<SgMantenimientoVo> lret = new ArrayList<>();

	try {
	    limpiarCuerpoQuery();
	    qry.append(" SELECT ");
	    qry.append(" vm.id as id, ");
	    qry.append(" (select km.KILOMETRAJE from SG_KILOMETRAJE km where km.id = vm.SG_KILOMETRAJE) as kilometraje, ");
	    qry.append(" vm.FECHA_INGRESO,");
	    qry.append(" (select tipoEsp.nombre from SG_KILOMETRAJE km,SG_TIPO_ESPECIFICO tipoEsp where km.id = vm.SG_KILOMETRAJE AND km.SG_TIPO_ESPECIFICO = tipoEsp.id) as nombre_Sg_Tipo_Especifico,     ");
	    qry.append(" vm.IMPORTE, ");
	    qry.append(" CASE WHEN vm.PROX_MANTENIMIENTO_KILOMETRAJE is null THEN 0  ");
	    qry.append(" WHEN vm.PROX_MANTENIMIENTO_KILOMETRAJE is not null THEN vm.PROX_MANTENIMIENTO_KILOMETRAJE    ");
	    qry.append(" END AS kilometraje_Proximo_Mantenimiento,");

	    qry.append(" vm.PROX_MANTENIMIENTO_FECHA as fecha_Proximo_Mantenimiento,    ");
	    qry.append(" vm.TERMINADO, ");

	    qry.append(" CASE WHEN vm.SI_ADJUNTO is null THEN 0");
	    qry.append(" WHEN vm.SI_ADJUNTO is not null THEN vm.SI_ADJUNTO ");
	    qry.append(" END AS id_Adjunto,");

	    qry.append(" CASE WHEN vm.SI_ADJUNTO is null THEN ''");
	    qry.append(" WHEN vm.SI_ADJUNTO is not null THEN (select ad.nombre from SI_ADJUNTO ad where ad.ID = vm.si_adjunto)   ");
	    qry.append(" END AS nombre_adjunto,");

	    qry.append(" CASE WHEN vm.moneda is null THEN ''");
	    qry.append(" WHEN vm.moneda is not null THEN (select mo.nombre from moneda mo where mo.ID = vm.moneda)   ");
	    qry.append(" END AS moneda, ");

	    qry.append(" pro.nombre as nombre_Proveedor,");
	    qry.append(" pro.id as id_Proveedor,");
	    qry.append(" vm.actual,");

	    qry.append(" CASE WHEN vm.SI_ADJUNTO is null THEN ''");
	    qry.append(" WHEN vm.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = vm.si_adjunto)   ");
	    qry.append(" END AS uuid");

	    qry.append(" FROM SG_VEHICULO_MANTENIMIENTO vm,PROVEEDOR pro");
	    qry.append(" where vm.SG_KILOMETRAJE in (select km.id from SG_KILOMETRAJE km where km.SG_TIPO_ESPECIFICO = ").append(idTipoEspecifico).append(" AND km.SG_VEHICULO = ").append(idVehiculo).append(" AND eliminado = 'False')");
	    qry.append(" AND vm.PROVEEDOR = pro.id");
	    qry.append(" AND vm.ELIMINADO = 'False' Order by vm.id desc");

	    UtilLog4j.log.info(this, "#QUERY# " + qry.toString());

	    if (maxResults == 0) {
		//Consultar todo
		lret = context.fetch(qry.toString()).into(SgMantenimientoVo.class);
	    } else {
		qry.append(" limit ").append(maxResults);
                lret = context.fetch(qry.toString()).into(SgMantenimientoVo.class); 
	    }
	    
	} catch (DataAccessException e) {
            UtilLog4j.log.error(e);
	}
        
        return lret;
    }

    private SgMantenimientoVo castReturnObjectMantenimiento(Object[] obj) {
	try {
	    UtilLog4j.log.info(this, "castReturnObjectMantenimiento");
	    SgMantenimientoVo mant = new SgMantenimientoVo();
	    mant.setId((Integer) obj[0]);
	    mant.setKilometraje((Integer) obj[1]);
	    mant.setFechaIngreso((Date) obj[2]);
	    mant.setNombreSgTipoEspecifico((String) obj[3]);
	    mant.setImporte((BigDecimal) obj[4]);
	    mant.setKilometrajeProximoMantenimiento((Integer) obj[5]);
	    mant.setFechaProximoMantenimiento((Date) obj[6]);
	    mant.setTerminado((boolean) obj[7]);
	    mant.setIdAdjunto((Integer) obj[8]);
	    mant.setNombreAdjunto((String) obj[9]);
	    mant.setMoneda((String) obj[10]);
	    mant.setNombreProveedor((String) obj[11]);
	    mant.setIdProveedor((Integer) obj[12]);
	   // mant.setActual((String) obj[13]);
	    mant.setUuid((String) obj[14]);

	    return mant;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion " + e);
	    return null;
	}
    }

    
    public void addArchivoAdjunto(SgVehiculoMantenimiento sgMantenimiento, Usuario usuarioModifico, SiAdjunto siAdjunto) {
	UtilLog4j.log.info(this, "addArchivoAdjunto");
	try {
	    sgMantenimiento.setFechaModifico(new Date());
	    sgMantenimiento.setHoraModifico(new Date());
	    sgMantenimiento.setModifico(usuarioModifico);
	    sgMantenimiento.setSiAdjunto(siAdjunto);
	    edit(sgMantenimiento);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al agregar el archivo adjunto " + e.getMessage());
	}
    }

    
    public void deleteArchivoAdjunto(SgVehiculoMantenimiento sgMantenimiento, Usuario usuarioModifico) {
	UtilLog4j.log.info(this, "deleteArchivoAdjunto");
	try {
	    sgMantenimiento.setFechaModifico(new Date());
	    sgMantenimiento.setHoraModifico(new Date());
	    sgMantenimiento.setModifico(usuarioModifico);
	    sgMantenimiento.setSiAdjunto(null);
	    edit(sgMantenimiento);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al eliminar el archivo adjunto " + e.getMessage());
	}
    }

    
    public boolean findProveedorEnMantenimiento(Proveedor proveedor) {
	List<SgVehiculoMantenimiento> lret = null;
	try {
	    lret = em.createQuery("SELECT m FROM SgVehiculoMantenimiento m "
		    + " WHERE m.proveedor = :proveedor AND m.eliminado = :eliminado").setParameter("proveedor", proveedor).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList();
	    if (!lret.isEmpty()) {
		return true;
	    } else {
		return false;
	    }

	} catch (Exception e) {
            UtilLog4j.log.error(e);
	    return false;
	}
    }

    /**
     * Campo baja sirve para saber si el vehiculo esta dado de baja
     * Actualizacion de Query: 26-NOV-2012
     */
    
    public List<VehiculoVO> traerMantenimientosProximoKilometrajePorRealizar(int oficina, int KmProximo) {
	List<SgVehiculoMantenimiento> lret = null;
	UtilLog4j.log.info(this, "#traerMantenimientosProximoKilometrajePorRealizar#");
	try {
	    clearQuery();
	    query.append(" Select vm.id" //0
                    + " ,vm.prox_mantenimiento_kilometraje" //1
	            + " ,ve.numero_placa" //2
	            + " ,co.nombre as color"//3
	            + " ,mar.nombre as marca"//4
	            + " ,mo.nombre as modelo" //6
	            + " ,km.KILOMETRAJE"//6
	            + " From SG_VEHICULO_MANTENIMIENTO vm"
                    + " INNER join SG_KILOMETRAJE km on km.ACTUAL= ? and km.SG_VEHICULO = vm.SG_VEHICULO and km.ELIMINADO = ?"
                    + " INNER join SG_VEHICULO ve on ve.ID = vm.SG_VEHICULO and ve.ESTATUS = ?"
                    + " INNER JOIN SG_COLOR co on co.ID = ve.SG_COLOR and co.ELIMINADO = ?"
                    + " INNER JOIN SG_MARCA mar on mar.id = ve.SG_MARCA and mar.ELIMINADO = ?"
                    + " INNER JOIN SG_MODELO mo on mo.ID = ve.SG_MODELO and mo.ELIMINADO = ?"
	            + " Where vm.ACTUAL = ?"
                    + " AND (vm.PROX_MANTENIMIENTO_KILOMETRAJE - ?) <= km.kilometraje"
	            + " AND ve.sg_oficina = ?"
	            + " AND ve.baja = ?"
	            + " AND vm.eliminado = ?"
	            + " order by ve.numero_placa");

	    List<VehiculoVO> le = new ArrayList<VehiculoVO>();
	    List<Object[]> lo = em.createNativeQuery(query.toString())
                    .setParameter(1, Constantes.BOOLEAN_TRUE)
                    .setParameter(2, Constantes.BOOLEAN_FALSE)
                    .setParameter(3, Constantes.ESTADO_VEHICULO_ACTIVO)
                    .setParameter(4, Constantes.BOOLEAN_FALSE)
                    .setParameter(5, Constantes.BOOLEAN_FALSE)
                    .setParameter(6, Constantes.BOOLEAN_FALSE)
                    .setParameter(7, Constantes.BOOLEAN_TRUE)
                    .setParameter(8, KmProximo)
                    .setParameter( 9, oficina)
                    .setParameter(10, Constantes.BOOLEAN_FALSE)
                    .setParameter(11, Constantes.BOOLEAN_FALSE)
                    .getResultList();
	    for (Object[] obj : lo) {
		VehiculoVO vehiculo;
		vehiculo = new VehiculoVO();
		vehiculo.setKmProximoMantenimiento(Long.parseLong(obj[1].toString()));
		vehiculo.setNumeroPlaca((String) obj[2]);
		vehiculo.setColor((String) obj[3]);
		vehiculo.setMarca((String) obj[4]);
		vehiculo.setModelo((String) obj[5]);
		vehiculo.setKmActual((Integer) obj[6]);
		le.add(vehiculo);
	    }
	    return le;

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al traer los vehiculos con mantenimiento " + e.getMessage());
	    return null;
	}
    }

    
    public List<VehiculoVO> traerMantenimientosProximaFechaPorRealizar(int oficina, Date proximaFecha) {
	List<SgVehiculoMantenimiento> lret = null;
	UtilLog4j.log.info(this, "traerMantenimientosProximaFechaPorRealizar");
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	clearQuery();
	try {
	    UtilLog4j.log.info(this, "Fecha a buscar " + sdf.format(proximaFecha));
	    query.append("Select vm.id,vm.PROX_MANTENIMIENTO_FECHA"
                    + ", ve.numero_placa" 
                    + ", co.nombre as color"
                    + ", mo.nombre as modelo" 
                    + ", mar.nombre as marca " 
                    + ", km.kilometraje"
                    + " From SG_VEHICULO_MANTENIMIENTO vm" 
                    + " INNER join SG_KILOMETRAJE km on km.ACTUAL = ? and km.SG_VEHICULO = vm.SG_VEHICULO and km.ELIMINADO = ?" 
                    + " INNER join SG_VEHICULO ve on ve.ID = vm.SG_VEHICULO and ve.ESTATUS = ?" 
                    + "	INNER JOIN SG_COLOR co on co.ID = ve.SG_COLOR and co.ELIMINADO = ?" 
                    + " INNER JOIN SG_MARCA mar on mar.id = ve.SG_MARCA and mar.ELIMINADO = ?" 
                    + " INNER JOIN SG_MODELO mo on mo.ID = ve.SG_MODELO and mo.ELIMINADO = ?" 
                    + " Where vm.ACTUAL = ?" 
                    + " and vm.eliminado = ?" 
                    + "	AND vm.PROX_MANTENIMIENTO_FECHA <= to_date('").append(sdf.format(proximaFecha)).append("', 'MM/dd/yyyy')" 
                    + "	AND ve.sg_oficina = ?" 
                    + "	AND ve.baja = ?" 
                    + " AND ve.ELIMINADO = ?" 
                    + " order by ve.numero_placa");
	    List<VehiculoVO> le = new ArrayList<>();
	    List<Object[]> lo = em.createNativeQuery(query.toString())
                    .setParameter(1, Constantes.BOOLEAN_TRUE)
                    .setParameter(2, Constantes.BOOLEAN_FALSE)
                    .setParameter(3, Constantes.ESTADO_VEHICULO_ACTIVO)
                    .setParameter(4, Constantes.BOOLEAN_FALSE)
                    .setParameter(5, Constantes.BOOLEAN_FALSE)
                    .setParameter(6, Constantes.BOOLEAN_FALSE)
                    .setParameter(7, Constantes.BOOLEAN_TRUE)
                    .setParameter(8, Constantes.BOOLEAN_FALSE)
                    .setParameter(9, oficina)
                    .setParameter(10, Constantes.BOOLEAN_FALSE)
                    .setParameter(11, Constantes.BOOLEAN_FALSE)
                    .getResultList();
	    UtilLog4j.log.info(this, "Todo bien al ejecutar ");
	    for (Object[] obj : lo) {
		VehiculoVO vehiculo;
		vehiculo = new VehiculoVO();
		vehiculo.setFechaProxMantenimiento((Date) obj[1]);
		vehiculo.setNumeroPlaca((String) obj[2]);
		vehiculo.setColor((String) obj[3]);
		vehiculo.setModelo((String) obj[4]);
		vehiculo.setMarca((String) obj[5]);
		vehiculo.setKmActual((Integer) obj[6]);
		le.add(vehiculo);
	    }
	    return le;

	} catch (Exception e) {
            UtilLog4j.log.error(e);
	    return null;
	}
    }

    /**
     * Recoje una coleccion de registros los cuales coinciden con un
     * mantenimiento por realizarse,esta busqueda se realiza mediante la tabla
     * sg_vehiculo_mantenimiento
     *
     * @param idOficina
     * @return
     */
//    private List<VehiculoVO> getVehiculosConProximoMtto(Integer idOficina) {
//        String q = "";
//        try {
//            q = "SELECT v.NUMERO_PLACA," //0
//                    + " v.CAJON_ESTACIONAMIENTO," //1
//                    + " v.SERIE,"//2
//                    + " v.CAPACIDAD_PASAJEROS,"//3
//                    + " te.NOMBRE,"//4
//                    + " km.KILOMETRAJE as km_actual,"//5
//                    + " v.PERIODO_KM_MANTENIMIENTO as periodo_KM,"//6
//                    + " v.PARTIDA_PERIODO_KM as partida,"//7
//                    + " case when (((v.PERIODO_KM_MANTENIMIENTO * v.PARTIDA_PERIODO_KM) - 500) <= 0)  then 0"
//                    + "       when (((v.PERIODO_KM_MANTENIMIENTO * v.PARTIDA_PERIODO_KM) - 500) > 0) then (((v.PERIODO_KM_MANTENIMIENTO * v.PARTIDA_PERIODO_KM) - 500))"
//                    + "       END as KM_NOTIFICACION," //7
//                    + " ((v.PERIODO_KM_MANTENIMIENTO * v.PARTIDA_PERIODO_KM)) as ProximoKMPreventivo,"//8
//                    + " vm.TERMINADO," //9
//                    + " co.NOMBRE as color,"//10
//                    + " ma.NOMBRE as marca,"//11
//                    + " mo.NOMBRE as modelo"//12
//
//                    + " Where v.SG_OFICINA =  " + idOficina
//                    + " AND vm.SG_VEHICULO = v.id "
//                    + " AND  vm.SG_KILOMETRAJE = km.ID "
//                    + " AND km.ACTUAL = '" + Constantes.BOOLEAN_TRUE + "' "
//                    + " AND v.SG_TIPO_ESPECIFICO = te.ID "
//                    + " AND km.KILOMETRAJE >= ((v.PERIODO_KM_MANTENIMIENTO * v.PARTIDA_PERIODO_KM) - 500)"
//                    + " AND v.SG_OFICINA = ofi.ID"
//                    + " AND v.SG_COLOR = co.id"
//                    + " AND v.SG_MARCA = ma.ID"
//                    + " AND v.SG_MODELO = mo.ID"
//                    + " AND v.BAJA ='" + Constantes.BOOLEAN_FALSE + "'"
//                    + " AND v.ELIMINADO ='" + Constantes.BOOLEAN_FALSE + "'"
//                    + " AND vm.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "'";
//
//            List<VehiculoVO> le = new ArrayList<VehiculoVO>();
//            List<Object[]> lo = em.createNativeQuery(q).getResultList();
//            for (Object[] objects : lo) {
//                le.add(castReturnVehiculoVO(objects));
//            }
//            return le;
//
//        } catch (Exception e) {
//            UtilLog4j.log.info(this,"Excepcion al traer los vehiculos con mantenimiento " + e.getMessage());
//            return null;
//        }
//    }
    /**
     * Recoje una coleccion de registros los cuales coinciden con un
     * mantenimiento por realizarse,esta busqueda solo se realiza a la tabla
     * sg_vehiculo estos vehiculos son los que aun no se les ha registrado
     * mantenimiento preventivo
     *
     * @param idOficina
     * @return
     */
    
    public List<VehiculoVO> getVehiculosConProximoMtto(Integer idOficina) {
	String q = "";
	try {
	    q = "SELECT  v.id,"//0
		    + " v.NUMERO_PLACA,"//1
		    + " v.CAJON_ESTACIONAMIENTO,"//2
		    + " v.SERIE,"//3
		    + " v.CAPACIDAD_PASAJEROS,"//4
		    + " te.NOMBRE,"//5
		    + " km.KILOMETRAJE as km_actual,"//6
		    + " v.PERIODO_KM_MANTENIMIENTO as periodo_KM,"//7
		    + " v.PARTIDA_PERIODO_KM as prtida,"//8
		    + " case when (((v.PERIODO_KM_MANTENIMIENTO * v.PARTIDA_PERIODO_KM) - 500) <= 0)  then 0"
		    + "    when (((v.PERIODO_KM_MANTENIMIENTO * v.PARTIDA_PERIODO_KM) - 500) > 0) then (((v.PERIODO_KM_MANTENIMIENTO * v.PARTIDA_PERIODO_KM) - 500))"
		    + "    END as KM_NOTIFICACION,"//9
		    + " ((v.PERIODO_KM_MANTENIMIENTO * v.PARTIDA_PERIODO_KM)) as ProximoKMPreventivo,"//10
		    + " ofi.NOMBRE,"//11
		    + " co.NOMBRE as color,"//12
		    + " ma.NOMBRE as marca,"//13
		    + " mo.NOMBRE as modelo"//14
		    + " From SG_VEHICULO v,"
		    + "  SG_TIPO_ESPECIFICO te,"
		    + "  SG_KILOMETRAJE km,"
		    + "  SG_OFICINA ofi,"
		    + "  SG_COLOR co,"
		    + "  SG_MARCA ma,"
		    + "  SG_MODELO mo"
		    + " Where v.SG_OFICINA = " + idOficina
		    + " AND km.SG_VEHICULO = v.id  "
		    + " AND km.ACTUAL = 'True' "
		    + " AND v.SG_TIPO_ESPECIFICO = te.ID "
		    + " AND km.KILOMETRAJE >= ((v.PERIODO_KM_MANTENIMIENTO * v.PARTIDA_PERIODO_KM) - 500)"
		    + " AND v.SG_OFICINA = ofi.ID"
		    + " AND v.SG_COLOR = co.id"
		    + " AND v.SG_MARCA = ma.ID"
		    + " AND v.SG_MODELO = mo.ID"
		    + " AND v.BAJA ='False'"
		    + " AND v.ELIMINADO ='False'"
		    + " AND km.ELIMINADO ='False' "
		    + " order by ma.nombre, mo.nombre";

	    List<VehiculoVO> le = new ArrayList<VehiculoVO>();
	    List<Object[]> lo = em.createNativeQuery(q).getResultList();
	    for (Object[] objects : lo) {
		le.add(castReturnVehiculoVO(objects));
	    }
	    return le;

	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion al traer los vehiculos con mantenimiento " + e.getMessage());
	    return null;
	}
    }

    private VehiculoVO castReturnVehiculoVO(Object[] obj) {
	VehiculoVO vehiculoVO;
	vehiculoVO = new VehiculoVO();
	vehiculoVO.setId((Integer) obj[0]);
	vehiculoVO.setNumeroPlaca((String) obj[1]);
	vehiculoVO.setCajonEstacionamiento((String) obj[2]);
	vehiculoVO.setSerie((String) obj[3]);
	vehiculoVO.setCapacidadPasajeros((Integer) obj[4]);
	vehiculoVO.setTipo((String) obj[5]);
	vehiculoVO.setKmActual((Integer) obj[6]);
	vehiculoVO.setPeriodoMantenimiento((Integer) obj[7]);
	vehiculoVO.setPartida((Integer) obj[8]);
	vehiculoVO.setKmNotificacion((Long) obj[9]);
	vehiculoVO.setKmProximoMantenimiento((Long) obj[10]);
	vehiculoVO.setColor((String) obj[12]);
	vehiculoVO.setMarca((String) obj[13]);
	vehiculoVO.setModelo((String) obj[14]);
	return vehiculoVO;
    }

    private void limpiarCuerpoQuery() {
	qry.delete(0, qry.length());
    }

    
    public List<PagoServicioVo> traerPago(int idOficina, int idServicio) {
	clearQuery();
	List<PagoServicioVo> lr = new ArrayList<PagoServicioVo>();
	query.append("select  mod.NOMBRE, sum(vm.IMPORTE) from SG_VEHICULO_MANTENIMIENTO vm");
	query.append("      inner join SG_VEHICULO v on vm.SG_VEHICULO = v.ID");
	query.append("      inner join SG_MODELO mod on v.SG_MODELO = mod.ID");
	query.append("      inner join SG_KILOMETRAJE k on vm.SG_KILOMETRAJE = k.id");
	query.append("  where v.SG_OFICINA = ").append(idOficina);
	if (idServicio > 0) {
	    query.append("  and k.SG_TIPO_ESPECIFICO = ").append(idServicio);
	}
//	query.append("  and vm.FECHA_SALIDA between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
	query.append("  and vm.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  and v.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("group by mod.NOMBRE");
	List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();
	PagoServicioVo o;
	if (l != null || l.size() > 0) {
	    for (Object[] objects : l) {
		o = new PagoServicioVo();
		o.setTipoEspecifico((String) objects[0]);
		BigDecimal bd = (BigDecimal) objects[1];
		o.setTotal((Double) bd.doubleValue());
		lr.add(o);
	    }
	}
	return lr;
    }

    
    public List<PagoServicioVo> traerMantenimientoPorServicio(int servicio, int idOficina, String vehiuculo) {
	clearQuery();
	List<PagoServicioVo> lr = null;
	query.append("select mod.NOMBRE, te.NOMBRE, p.NOMBRE, vm.FECHA_INGRESO, vm.FECHA_SALIDA, vm.FECHA_SALIDA, vm.IMPORTE,  m.SIGLAS, 'NA', adj.ID, adj.UUID ");
	query.append(" from SG_VEHICULO_MANTENIMIENTO vm ");
	query.append("      inner join SG_VEHICULO v on vm.SG_VEHICULO = v.ID");
	query.append("      inner join SG_MODELO mod on v.SG_MODELO = mod.ID");
	query.append("      inner join PROVEEDOR p on vm.PROVEEDOR = p.ID");
	query.append("      inner join SG_KILOMETRAJE k on vm.SG_KILOMETRAJE = k.ID");
	query.append("      inner join SG_TIPO_ESPECIFICO te on k.SG_TIPO_ESPECIFICO = te.ID");
	query.append("      inner join SI_ADJUNTO adj on vm.SI_ADJUNTO = adj.ID");
	query.append("      inner join MONEDA m on vm.MONEDA = m.ID");
	query.append("  where  v.SG_OFICINA = ").append(idOficina);
	query.append("  and te.id = ").append(servicio);
	//query.append("  and vm.FECHA_SALIDA between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
	query.append("  and mod.nombre = '").append(vehiuculo).append("'");
	query.append("  and v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  and vm.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append(" order by vm.fecha_ingreso asc");
	List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();
	PagoServicioVo o;
	if (l != null || l.size() > 0) {
	    lr = new ArrayList<PagoServicioVo>();
	    for (Object[] objects : l) {
		lr.add(castPagoServicio(objects));
	    }
	}
	return lr;
    }

    private PagoServicioVo castPagoServicio(Object[] objects) {
	PagoServicioVo o = new PagoServicioVo();
	o.setVehiculo((String) objects[0]);
	o.setTipoEspecifico((String) objects[1]);
	o.setProveedor((String) objects[2]);
	o.setInicio((Date) objects[3]);
	o.setFin((Date) objects[4]);
	o.setVencimiento((Date) objects[5]);
	BigDecimal bd = (BigDecimal) objects[6];
	o.setImporte(bd.doubleValue());
	o.setMoneda((String) objects[7]);
	o.setRecibo((String) objects[8]);
	o.setIdAdjunto((Integer) objects[9]);
	o.setAdjuntoUUID((String) objects[10]);
	return o;
    }

    
    public double totalImporteVehiculo(int idVehiculo, int idMoneda) {
	clearQuery();
	try {
	    query.append("select sum(vm.IMPORTE), vm.MONEDA from SG_VEHICULO_MANTENIMIENTO vm  ");
	    query.append("  where vm.SG_VEHICULO = ").append(idVehiculo);
	    query.append("  and vm.moneda = ").append(idMoneda);
	    query.append("  and  vm.ELIMINADO  = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append("  group by vm.MONEDA ");
	    return ((Double) em.createNativeQuery(query.toString()).getSingleResult());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "No encontro ningun mantenimiento + + + " + e.getMessage());
	    return 0.0;
	}

    }

    
    public List<PagoServicioVo> traerTotalPago(int anio) {
	clearQuery();
	List<PagoServicioVo> lr = new ArrayList<PagoServicioVo>();
	query.append("select  o.NOMBRE, sum(vm.IMPORTE) from SG_VEHICULO_MANTENIMIENTO vm");
	query.append("      inner join SG_VEHICULO v on vm.SG_VEHICULO = v.ID");
	query.append("      inner join SG_OFICINA o on v.SG_OFICINA = o.ID");
	query.append("  where vm.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	if (anio > 0) {
	    query.append("  and extract(year from vm.FECHA_INGRESO) =  ").append(anio);
	}
	query.append("  and v.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append(" group by o.NOMBRE");
	List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();
	PagoServicioVo o;
	if (l != null) {
	    for (Object[] objects : l) {
		o = new PagoServicioVo();
		o.setTipoEspecifico((String) objects[0]);
		BigDecimal bd = (BigDecimal) objects[1];
		o.setTotal((Double) bd.doubleValue());
		lr.add(o);
	    }
	}
	return lr;
    }

    /**
     *
     * @param oficina
     * @param anio
     * @return
     */
    
    public List<PagoServicioVo> traerMantoPagoPorAnio(int oficina, int anio) {
	String q = "select te.NOMBRE, sum(mv.IMPORTE) from SG_VEHICULO_MANTENIMIENTO mv "
		+ "	    inner join SG_KILOMETRAJE k on mv.SG_KILOMETRAJE = k.ID"
		+ "	    inner join SG_TIPO_ESPECIFICO te on k.SG_TIPO_ESPECIFICO = te.ID"
		+ "	    inner join SG_VEHICULO v on mv.SG_VEHICULO =  v.ID"
		+ "	    inner join SG_OFICINA o on v.SG_OFICINA = o.ID"
		+ "	where v.ELIMINADO = 'False'"
		+ "	and o.ELIMINADO = 'False'"
		+ "	and v.SG_OFICINA =  " + oficina;
	if (anio > 0) {
	    q += "	and extract(year from mv.FECHA_INGRESO) = " + anio;
	}
	q += "	group by te.NOMBRE";
	List<PagoServicioVo> lr = new ArrayList<PagoServicioVo>();
	List<Object[]> l = em.createNativeQuery(q).getResultList();
	PagoServicioVo o;
	if (l != null) {
	    for (Object[] objects : l) {
		o = new PagoServicioVo();
		o.setTipoEspecifico((String) objects[0]);
		BigDecimal bd = (BigDecimal) objects[1];
		o.setTotal((Double) bd.doubleValue());
		lr.add(o);
	    }
	}
	return lr;
    }

    
    public List<PagoServicioVo> traerMantoPagoPorMes(int oficina, int anio, int tipoServicio) {
	String q = "select substring(to_char(mv.FECHA_INGRESO, 'DD/MM/YYYY'), 4 , 10),  "
		+ "	    sum(mv.IMPORTE), count(v.id) from SG_VEHICULO_MANTENIMIENTO mv "
		+ "	    inner join SG_KILOMETRAJE k on mv.SG_KILOMETRAJE = k.ID"
		+ "	    inner join SG_TIPO_ESPECIFICO te on k.SG_TIPO_ESPECIFICO = te.ID"
		+ "	    inner join SG_VEHICULO v on mv.SG_VEHICULO =  v.ID"
		+ "	    inner join SG_OFICINA o on v.SG_OFICINA = o.ID"
		+ "	where v.ELIMINADO = 'False'"
		+ "	and o.ELIMINADO = 'False'"
		+ "	and v.SG_OFICINA =  " + oficina
		+ "	and te.ID = " + tipoServicio
		+ "	and v.SG_OFICINA =  " + oficina;
	if (anio > 0) {
	    q += "	and extract(year from mv.FECHA_INGRESO) = " + anio;
	}
	q += "	group by substring(to_char(mv.FECHA_INGRESO, 'DD/MM/YYYY'), 4 , 10)";
	List<PagoServicioVo> lr = new ArrayList<>();
	List<Object[]> l = em.createNativeQuery(q).getResultList();
	PagoServicioVo o;
	if (l != null) {
	    for (Object[] objects : l) {
		o = new PagoServicioVo();
		o.setTipoEspecifico((String) objects[0]);
		BigDecimal bd = (BigDecimal) objects[1];
		o.setTotal((Double) bd.doubleValue());
		lr.add(o);
	    }
	}
	return lr;
    }
}
