/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.vehiculo.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.Estatus;
import sia.modelo.Proveedor;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgCaracteristicaVehiculo;
import sia.modelo.SgColor;
import sia.modelo.SgMarca;
import sia.modelo.SgModelo;
import sia.modelo.SgOficina;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgVehiculo;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.notificaciones.sgl.impl.NotificacionServiciosGeneralesImpl;
import sia.servicios.sgl.impl.SgCaracteristicaImpl;
import sia.servicios.sgl.impl.SgCaracteristicaVehiculoImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgPagoServicioVehiculoImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgVehiculoChecklistImpl;
import sia.servicios.sgl.impl.SgVehiculoMantenimientoImpl;
import sia.servicios.sgl.viaje.impl.SgViajeVehiculoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgVehiculoImpl extends AbstractFacade<SgVehiculo> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;        
    @Inject
    private SgTipoEspecificoImpl tipoEspecificoService;
    @Inject
    private SgCaracteristicaImpl caracteristicaService;
    @Inject
    private SgCaracteristicaVehiculoImpl caracteristicaVehiculoService;
    @Inject
    private SgVehiculoMovimientoImpl sgVehiculoMovimientoRemote;
    @Inject
    private NotificacionServiciosGeneralesImpl notificacionServiciosGeneralesRemote;
    @Inject
    SgOficinaImpl sgOficinaRemote;
    @Inject
    private SgAsignarVehiculoImpl sgAsignarVehiculoRemote;
    @Inject
    private SgPagoServicioVehiculoImpl sgPagoServicioVehiculoRemote;
    @Inject
    private SgVehiculoMantenimientoImpl sgVehiculoMantenimientoRemote;
    @Inject
    private SgVehiculoChecklistImpl sgVehiculoChecklistRemote;
    @Inject
    private SgViajeVehiculoImpl sgViajeVehiculoRemote;
    //
    private StringBuilder bodyQuery = new StringBuilder();    

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgVehiculoImpl() {
	super(SgVehiculo.class);
    }

    private void clearCuerpoQuery() {
	this.bodyQuery.delete(0, bodyQuery.length());
    }

    
    public SgVehiculo save(VehiculoVO vehiculoVO, String idUsuario, int oficina) throws SIAException, Exception {

	SgVehiculo vehiculoExistenteSerie = findByNumeroSerie(vehiculoVO.getSerie(), false, false);
	SgVehiculo vehiculoExistentePlaca = findByNumeroPlaca(vehiculoVO.getNumeroPlaca(), false, false);

	if (vehiculoExistentePlaca == null && vehiculoExistenteSerie == null) {
	    SgVehiculo vehiculo = new SgVehiculo();
	    vehiculo.setSgMarca(new SgMarca(vehiculoVO.getIdMarca()));
	    vehiculo.setSgColor(new SgColor(vehiculoVO.getIdColor()));
	    vehiculo.setSgModelo(new SgModelo(vehiculoVO.getIdModelo()));
	    vehiculo.setSgOficina(new SgOficina(oficina));
	    vehiculo.setSgTipo(new SgTipo(Constantes.UNO));
	    vehiculo.setSgTipoEspecifico(new SgTipoEspecifico(vehiculoVO.getIdTipoEspecifico()));
	    vehiculo.setPeriodoKmMantenimiento(vehiculoVO.getPeriodoKmMantenimiento());
	    vehiculo.setSerie(vehiculoVO.getSerie());
	    vehiculo.setNumeroPlaca(vehiculoVO.getNumeroPlaca());
	    vehiculo.setCapacidadPasajeros(vehiculoVO.getCapacidadPasajeros());
	    vehiculo.setCajonEstacionamiento(vehiculoVO.getCajonEstacionamiento());
	    vehiculo.setPartidaPeriodoKm(vehiculoVO.getPartida());

	    vehiculo.setNumeroEconomico(vehiculoVO.getNumeroEconomico());
	    vehiculo.setNumeroActivo(vehiculoVO.getNumeroActivo());
	    vehiculo.setSeguro(vehiculoVO.getSeguro());
	    vehiculo.setGps(vehiculoVO.isGps());
	    vehiculo.setEstatus(new Estatus(Constantes.ESTADO_VEHICULO_ACTIVO));
	    vehiculo.setCajaHerramienta(vehiculo.isCajaHerramienta());
	    //
	    vehiculo.setGenero(new Usuario(idUsuario));
	    vehiculo.setFechaGenero(new Date());
	    vehiculo.setHoraGenero(new Date());
	    vehiculo.setEliminado(Constantes.NO_ELIMINADO);
	    vehiculo.setBaja(Constantes.BOOLEAN_FALSE);
	    create(vehiculo);
	    //Poner usado el tipoEspecifico
	    tipoEspecificoService.ponerUsadoTipoEspecifico(vehiculo.getSgTipoEspecifico().getId(), new Usuario(idUsuario));

	    //Añadir al Vehículo todas las Características de tipo Vehículo existentes
	    List<CaracteristicaVo> caracteristicasVehiculo = caracteristicaService.traerCaracteristicaPorTipo(vehiculo.getSgTipo().getId());
	    for (CaracteristicaVo caracteristica : caracteristicasVehiculo) {
		caracteristicaVehiculoService.create(caracteristica.getId(), vehiculo.getId(), idUsuario);
	    }

	    UtilLog4j.log.info(this, "SgVehiculo CREATED SUCCESSFULLY");

	    return vehiculo;
	} else {
	    if (vehiculoExistentePlaca != null) {
		throw new SIAException(SgVehiculoImpl.class.getName(), "save()", (" Ya existe un Vehículo con el número de Placa: " + vehiculoVO.getNumeroPlaca()));
	    } else {
		throw new SIAException(SgVehiculoImpl.class.getName(), "save()", (" Ya existe un Vehículo con el número de Serie: " + vehiculoVO.getSerie()));
	    }
	}
    }

    
    public SgVehiculo reactivate(VehiculoVO vehiculo, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgVehiculoImpl.reactivate()");

	SgVehiculo vehiculoBaja = findByNumeroSerie(vehiculo.getSerie(), false, true);

	vehiculoBaja.setNumeroPlaca(vehiculo.getNumeroPlaca());
	vehiculoBaja.setSgColor(new SgColor(vehiculo.getIdColor()));
	vehiculoBaja.setCajonEstacionamiento(vehiculo.getCajonEstacionamiento());
	vehiculoBaja.setCapacidadPasajeros(vehiculo.getCapacidadPasajeros());
	vehiculoBaja.setBaja(Constantes.BOOLEAN_FALSE);
	vehiculoBaja.setModifico(new Usuario(idUsuario));
	vehiculoBaja.setFechaModifico(new Date());
	vehiculoBaja.setHoraModifico(new Date());

	super.edit(vehiculoBaja);
	return vehiculoBaja;
    }

    
    public SgVehiculo update(SgVehiculo vehiculo, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgVehiculoImpl.update()");

	SgVehiculo vehiculoActual = find(vehiculo.getId());
	SgVehiculo vehiculoExistentePlaca = findByNumeroPlaca(vehiculo.getNumeroPlaca(), false, false);
	SgVehiculo vehiculoExistenteSerie = findByNumeroSerie(vehiculo.getSerie(), false, false);

	if (vehiculoExistentePlaca != null) {
	    if (vehiculoActual.getId() != vehiculoExistentePlaca.getId()) {
		throw new SIAException(SgVehiculoImpl.class.getName(), "save()", (" Ya existe un Vehículo con el número de Placa: " + vehiculo.getNumeroPlaca()));
	    }
	}
	if (vehiculoExistenteSerie != null) {
	    if (vehiculoActual.getId() != vehiculoExistenteSerie.getId()) {
		throw new SIAException(SgVehiculoImpl.class.getName(), "save()", (" Ya existe un Vehículo con el número de Serie: " + vehiculo.getSerie()));
	    }
	}

	vehiculo.setModifico(new Usuario(idUsuario));
	vehiculo.setFechaModifico(new Date());
	vehiculo.setHoraModifico(new Date());
	edit(vehiculo);

	return vehiculo;
    }

    
    public void udpate(VehiculoVO sgVehiculo, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgVehiculoImpl.update()");

	SgVehiculo vehiculoActual = find(sgVehiculo.getId());
	VehiculoVO vehiculoExistentePlaca = buscarPorPlaca(sgVehiculo.getNumeroPlaca());
	VehiculoVO vehiculoExistenteSerie = buscarPorSerie(sgVehiculo.getSerie());

	if (vehiculoExistentePlaca != null) {
	    if (!vehiculoExistentePlaca.getNumeroPlaca().equals(vehiculoActual.getNumeroPlaca())) {
		throw new SIAException(SgVehiculoImpl.class.getName(), "save()", (" Ya existe un Vehículo con el número de Placa: " + sgVehiculo.getNumeroPlaca()));
	    }
	}
	if (vehiculoExistenteSerie != null) {
	    if (!vehiculoExistenteSerie.getSerie().equals(vehiculoActual.getSerie())) {
		throw new SIAException(SgVehiculoImpl.class.getName(), "save()", (" Ya existe un Vehículo con el número de Serie: " + sgVehiculo.getSerie()));
	    }
	}

	//Añadir otras entidades
	vehiculoActual.setSgTipoEspecifico(new SgTipoEspecifico(sgVehiculo.getIdTipoEspecifico()));
	vehiculoActual.setSgMarca(new SgMarca(sgVehiculo.getIdMarca()));
	vehiculoActual.setSgModelo(new SgModelo(sgVehiculo.getIdModelo()));
	vehiculoActual.setSerie(sgVehiculo.getSerie());
	vehiculoActual.setNumeroPlaca(sgVehiculo.getNumeroPlaca());
	vehiculoActual.setSgColor(new SgColor(sgVehiculo.getIdColor()));
	vehiculoActual.setCapacidadPasajeros(sgVehiculo.getCapacidadPasajeros());
	vehiculoActual.setPeriodoKmMantenimiento(sgVehiculo.getPeriodoKmMantenimiento());
	vehiculoActual.setCajonEstacionamiento(sgVehiculo.getCajonEstacionamiento());
	vehiculoActual.setObservacion(sgVehiculo.getObservacion());
	//
	vehiculoActual.setNumeroEconomico(sgVehiculo.getNumeroEconomico());
	vehiculoActual.setNumeroActivo(sgVehiculo.getNumeroActivo());
	vehiculoActual.setSeguro(sgVehiculo.getSeguro());
	vehiculoActual.setGps(sgVehiculo.isGps());
	vehiculoActual.setCajaHerramienta(sgVehiculo.isCajaHerramienta());
	//
	vehiculoActual.setModifico(new Usuario(idUsuario));
	vehiculoActual.setFechaModifico(new Date());
	vehiculoActual.setHoraModifico(new Date());

	super.edit(vehiculoActual);	
    }

    
    public SgVehiculo delete(VehiculoVO vehiculoVO, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgVehiculoImpl.delete()");

	Long cont = 0l;
	SgVehiculo vehiculo = find(vehiculoVO.getId());

	cont += (Long) em.createQuery("SELECT COUNT(v) FROM SgVehiculoMantenimiento v WHERE v.eliminado = :eliminado AND v.sgVehiculo.id = :idVehiculo").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idVehiculo", vehiculo.getId()).getSingleResult();

	cont += (Long) em.createQuery("SELECT COUNT(v) FROM SgVehiculoChecklist v WHERE v.eliminado = :eliminado AND v.sgVehiculo.id = :idVehiculo").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idVehiculo", vehiculo.getId()).getSingleResult();

	cont += (Long) em.createQuery("SELECT COUNT(v) FROM SgAsignarVehiculo v WHERE v.eliminado = :eliminado AND v.sgVehiculo.id = :idVehiculo").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idVehiculo", vehiculo.getId()).getSingleResult();

	if (cont == 0) {
	    vehiculo.setEliminado(Constantes.ELIMINADO);
	    super.edit(vehiculo);
	    return vehiculo;
	} else {
	    UtilLog4j.log.info(this, "El Vehículo ya está siendo usado en: " + cont + " lugares");
	    throw new SIAException(SgVehiculoImpl.class.getName(), " delete()", " Este Vehículo no se puede eliminar debido a que ya está siendo utilizado. Intenta darlo de Baja");
	}
    }

    
    public SgVehiculo baja(int idVehiculo, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgVehiculoImpl.baja()");

	SgVehiculo vehiculo = find(idVehiculo);
	vehiculo.setBaja(Constantes.BOOLEAN_TRUE);
	vehiculo.setModifico(new Usuario(idUsuario));
	vehiculo.setFechaModifico(new Date());
	vehiculo.setHoraModifico(new Date());
	super.edit(vehiculo);
	return vehiculo;
    }

    
    public SgVehiculo findByNumeroPlaca(String numeroPlaca, boolean eliminado, boolean baja) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgVehiculoImpl.findByNumeroPlaca()");

	try {
	    return (SgVehiculo) em.createQuery("SELECT v FROM SgVehiculo v WHERE v.numeroPlaca = :numeroPlaca "
		    + " AND v.eliminado = :eliminado "
		    + " AND v.baja = :baja").setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO)).setParameter("baja", (baja ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE)).setParameter("numeroPlaca", numeroPlaca).getSingleResult();
	} catch (NoResultException nre) {
	    return null;
	} catch (NonUniqueResultException nure) {
	    throw new SIAException(SgVehiculoImpl.class.getName(), " findByNumeroPlaca()", (" Existe más de un Vehículo con el mimo número de placa: " + numeroPlaca));
	}
    }

    
    public SgVehiculo findByNumeroSerie(String numeroSerie, boolean eliminado, boolean baja) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgVehiculoImpl.findByNumeroSerie()");

	try {
	    return (SgVehiculo) em.createQuery("SELECT v FROM SgVehiculo v WHERE v.serie = :numeroSerie AND v.eliminado = :eliminado AND v.baja = :baja").setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO)).setParameter("baja", (baja ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE)).setParameter("numeroSerie", numeroSerie).getSingleResult();
	} catch (NoResultException nre) {
	    return null;
	} catch (NonUniqueResultException nure) {
	    throw new SIAException(SgVehiculoImpl.class.getName(), " findByNumeroPlaca", (" Existe más de un Vehículo con el mimo número de serie: " + numeroSerie));
	}
    }

    
    public List<VehiculoVO> getAllVehiculoByOficinaList(int oficina) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgVehiculoImpl.getAllVehiculoByOficinaList()");
	StringBuilder sb = new StringBuilder();
	if (oficina > 0) {
	    sb.append(consulta()).append(" where v.SG_OFICINA = ").append(oficina).append(" and v.ELIMINADO = 'False' and v.BAJA = 'False'");
	    sb.append(" and k.KILOMETRAJE = (select max(KILOMETRAJE) from SG_KILOMETRAJE k1 where k1.SG_VEHICULO = v.ID)");
	} else {
	    sb.append(consulta()).append(" where v.ELIMINADO = 'False' and v.BAJA = 'False'");
	    sb.append(" and k.KILOMETRAJE = (select max(KILOMETRAJE) from SG_KILOMETRAJE k1 where k1.SG_VEHICULO = v.ID)");
	}
	List<VehiculoVO> vehiculos = null;
	List<Object[]> objects = em.createNativeQuery(sb.toString()).getResultList();
	if (objects != null) {
	    vehiculos = new ArrayList<VehiculoVO>();
	    for (Object[] object : objects) {
		vehiculos.add(castVehiculo(object));
	    }
	} else {
	    throw new SIAException(SgVehiculoImpl.class.getName(), "getAllVehiculoByOficinaList()",
		    "Faltan parámetros para poder realizar la búsqueda de Vehículo",
		    ("Parámetros: oficina: " + oficina));
	}
	return vehiculos;
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
	v.setKilometraje((Integer) object[24]);
        v.setUsuarioAsignada(object[26] != null ? object[26].toString() : "Sin Asignar");
        v.setIdusuarioAsignada(object[25] != null ? object[25].toString() : "");
        v.setIdEmpresaEmp(object[27] != null ? (Integer)object[27] : 0);
        v.setEmpresaEmp(object[28] != null ? object[28].toString() : "");
	return v;

    }

    
    public List<SgVehiculo> findAllList(boolean eliminado, boolean baja) {
	UtilLog4j.log.info(this, "SgVehiculoImpl.findAllList()");

	List<SgVehiculo> vehiculos = null;

	vehiculos = em.createQuery("SELECT v FROM SgVehiculo v WHERE v.eliminado = :eliminado AND v.baja = :baja").setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO)).setParameter("baja", (baja ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO)).getResultList();

	UtilLog4j.log.info(this, "Se encontraron " + (vehiculos != null ? vehiculos.size() : 0) + " vehiculos");

	return vehiculos;
    }

    
    public List<SgVehiculo> traerVehiculoPorOficina(SgOficina oficinaActual) {
	try {
	    return em.createQuery("SELECT v FROM SgVehiculo v "
		    + " WHERE v.eliminado = :eli "
		    + " AND v.baja = :baja "
		    + " AND v.sgOficina.id = :idOfi "
		    + " ORDER BY v.id DESC").setParameter("idOfi", oficinaActual.getId()).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("baja", Constantes.BOOLEAN_FALSE).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion " + e.getMessage());
	    return null;
	}
    }

    
    public List<SgVehiculo> traerVehiculoPorPlaca(int oficinaActual, String cadenaBuscar) {
	try {
	    return em.createQuery("SELECT v FROM SgVehiculo v "
		    + " WHERE v.eliminado = :eli "
		    + " AND v.baja = :baja "
		    + " AND v.sgOficina.id = :idOfi "
		    + " AND v.numeroPlaca = :placa "
		    + " ORDER BY v.id DESC").setParameter("idOfi", oficinaActual).setParameter("placa", cadenaBuscar).setParameter("baja", Constantes.BOOLEAN_FALSE).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public List<SgVehiculo> traerVehiculoPorModelo(int oficinaActual, String cadenaBuscar) {
	try {
	    return em.createQuery("SELECT v FROM SgVehiculo v "
		    + " WHERE v.eliminado = :eli "
		    + " AND v.baja = :baja "
		    + " AND v.sgOficina.id = :idOfi "
		    + " AND v.sgModelo.nombre = :modelo "
		    + " ORDER BY v.id DESC").setParameter("idOfi", oficinaActual).setParameter("modelo", cadenaBuscar).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("baja", Constantes.BOOLEAN_FALSE).getResultList();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public SgCaracteristica addCaracteristica(int vehiculo, String nombreCaracteristica, int tipo, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgVehiculoImpl.addCaracteristica()");

	SgCaracteristica caracteristicaNueva = null;

	//Buscar si existe la Característica
	SgCaracteristica car = caracteristicaService.findByName(nombreCaracteristica);

	if (car != null) { //La Característica existe. Solo hay que crear la relación
	    //No permitir duplicar la realación
	    SgCaracteristicaVehiculo carStaffExistente = caracteristicaVehiculoService.findByCaracteristicaAndVehiculo(car, vehiculo);

	    if (carStaffExistente == null) {
		caracteristicaVehiculoService.create(car.getId(), vehiculo, idUsuario);
	    } else {
		UtilLog4j.log.info(this, "La Característica ya está asignada");
		throw new SIAException("La Característica \" " + car.getNombre() + "\"  ya está asignada");
	    }
	} else { //La Característica no existe. Hay que crearla y luego la relación
	    UtilLog4j.log.info(this, "Nombre nueva Característica: " + nombreCaracteristica);
	    //Crear Característica
	    caracteristicaNueva = caracteristicaService.create(nombreCaracteristica, false, tipo, idUsuario);
	    //Crear relación
	    caracteristicaVehiculoService.create(caracteristicaNueva.getId(), vehiculo, idUsuario);
	}
	return caracteristicaNueva;
    }

    
    public void removeCaracteristica(int caracteristicaVehiculo, String idUsuario) throws SIAException, Exception {
	caracteristicaVehiculoService.delete(caracteristicaVehiculo, idUsuario);
    }

    
    public List<CaracteristicaVo> getAllCaracteristicasVehiculoList(int vehiculo) throws SIAException, Exception {
	return caracteristicaVehiculoService.getAllCaracteristicaVehiculoByVehiculoList(vehiculo);
    }

    
    public boolean cambiarOficina(int idVehiculo, int idOficinaNueva, String motivo, String idUsuario) throws SIAException, Exception {
	boolean todoBien = false;
	UtilLog4j.log.info(this, "Entrando a cambiar oficina");
	SgVehiculo vehiculo = find(idVehiculo);
	
	if (this.notificacionServiciosGeneralesRemote.enviaNotificacionCambioOficinaVehiculo(idVehiculo, idOficinaNueva, motivo, idUsuario)) {
	    vehiculo.setSgOficina(sgOficinaRemote.find(idOficinaNueva));
	    vehiculo.setModifico(new Usuario(idUsuario));
	    vehiculo.setFechaModifico(new Date());
	    vehiculo.setHoraModifico(new Date());
	    edit(vehiculo);
	
	    //----------guardar movimiento
	    if (sgVehiculoMovimientoRemote.save(motivo, idVehiculo, vehiculo.getSgOficina().getId(), idOficinaNueva, idUsuario)) {
		//envia correo a oficina destino con datos de viaje
		todoBien = true;
	    }
	    //----------
	}

	return todoBien;
    }

    
    public boolean vehiculoEnViaje(int idSgVehiculo) {
	UtilLog4j.log.info(this, "SgVehiculoImpl.vehiculoEnViaje()");

	clearQuery();

	query.append("SELECT COUNT(*) ");
	query.append("FROM SG_VEHICULO ve, SG_VIAJE_VEHICULO vve, SG_VIAJE vi ");
	query.append("WHERE ve.ELIMINADO=").append("'").append(Constantes.NO_ELIMINADO).append("' ");
	query.append("AND vve.ELIMINADO=").append("'").append(Constantes.NO_ELIMINADO).append("' ");
	query.append("AND vi.ELIMINADO=").append("'").append(Constantes.NO_ELIMINADO).append("' ");
	query.append("AND vve.SG_VEHICULO=").append(idSgVehiculo).append(" ");
	query.append("AND vve.SG_VIAJE=vi.ID ");
	query.append("AND vve.SG_VEHICULO=ve.ID ");
	query.append("AND (vi.ESTATUS=501 OR vi.ESTATUS=510)");

	Query q = em.createNativeQuery(query.toString());

	UtilLog4j.log.info(this, q.toString());

	return ((((long) q.getSingleResult())) != 0);
    }

    
    public List<VehiculoVO> traerVehiculoPorOficina(int idOficina, boolean eliminado) {
	clearCuerpoQuery();
	bodyQuery.append(consulta()).append(" WHERE v.ELIMINADO = ").append(Constantes.NO_ELIMINADO);
	bodyQuery.append(" AND v.sg_oficina = ").append(idOficina).append("AND v.baja = ").append(Constantes.BOOLEAN_FALSE);
	bodyQuery.append(" order by mo.nombre asc ");
	List<Object[]> lo = em.createNativeQuery(bodyQuery.toString()).getResultList();
	List<VehiculoVO> lv = new ArrayList<VehiculoVO>();
	for (Object[] objects : lo) {
	    lv.add(castVehiculo(objects));
	}
	return lv;
    }

    
    public VehiculoVO buscarVehiculoPorId(int idVehiculo) {
	try {
	    clearCuerpoQuery();
	    bodyQuery.append(consulta());
	    bodyQuery.append(" WHERE v.ELIMINADO = ").append("'").append(Constantes.NO_ELIMINADO).append("' ");
	    bodyQuery.append(" AND v.id = ").append(idVehiculo).append("     order by k.ID desc limit 1 ");
	    //
	    Object[] obj = (Object[]) em.createNativeQuery(bodyQuery.toString()).getSingleResult();
	    return castVehiculo(obj);

	} catch (Exception e) {
	    e.getStackTrace();
	    return null;
	}
    }

    
    public Map<String, String> buscarDatosVehiculo(int idVehiculo, int idMoneda) {
	Map<String, String> vehiculoMap = new HashMap<String, String>();
	int totalAsignacion = sgAsignarVehiculoRemote.contarAsignacion(idVehiculo);
	vehiculoMap.put("Asignaciones", String.valueOf(totalAsignacion));
	int totalViaje = sgViajeVehiculoRemote.contarVehiculoViajes(idVehiculo);
	vehiculoMap.put("Viajes", String.valueOf(totalViaje));
	double totalImporte = sgPagoServicioVehiculoRemote.totalImportePago(idVehiculo, idMoneda);
	vehiculoMap.put("Gasto de Servicio", String.valueOf(totalImporte));
	double totalManto = sgVehiculoMantenimientoRemote.totalImporteVehiculo(idVehiculo, idMoneda);
	vehiculoMap.put("Mantenimiento", String.valueOf(totalManto));
	int totalChecklist = sgVehiculoChecklistRemote.totalCheckList(idVehiculo);
	vehiculoMap.put("CheckList", String.valueOf(totalChecklist));
	//
	return vehiculoMap;
    }

    
    public VehiculoVO buscarPorPlaca(String placa) {
	VehiculoVO vehiculoVO = new VehiculoVO();
	try {
	    clearQuery();
	    query.append(" select v.ID, v.numero_placa, mar.nombre, mo.nombre, te.nombre from SG_vehiculo v ");
	    query.append("	inner join sg_modelo mo on v.sg_modelo = mo.id");
	    query.append("	inner join sg_marca mar on v.sg_marca = mar.id");
	    query.append("	inner join sg_tipo_especifico te on v.sg_tipo_especifico = te.id");
	    query.append(" where replace(upper(v.NUMERO_PLACA), '-' , '') =  replace(upper('").append(placa).append("')").append(", '-' ,'')");

	    Object[] objects = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	    vehiculoVO.setId((Integer) objects[0]);
	    vehiculoVO.setNumeroPlaca((String) objects[1]);
	    vehiculoVO.setMarca((String) objects[2]);
	    vehiculoVO.setModelo((String) objects[3]);
	    vehiculoVO.setTipoEspecifico((String) objects[4]);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "e : " + e.getMessage());
	    vehiculoVO = null;
	}

	return vehiculoVO;
    }

    
    public List<VehiculoVO> traerVahiculoBaja(int oficina) {
	UtilLog4j.log.info(this, "SgVehiculoImpl.getAllVehiculoByOficinaList()");
	StringBuilder sb = new StringBuilder();
	sb.append(consulta()).append(" where v.SG_OFICINA = ").append(oficina).append(" and v.ELIMINADO = 'False'  and v.BAJA = 'True' ");
	sb.append(" and k.KILOMETRAJE = (select max(KILOMETRAJE) from SG_KILOMETRAJE k1 where k1.SG_VEHICULO = v.ID)");
	sb.append(" order by k.ID desc");

	List<VehiculoVO> vehiculos = null;
	List<Object[]> objects = em.createNativeQuery(sb.toString()).getResultList();
	if (objects != null) {
	    vehiculos = new ArrayList<VehiculoVO>();
	    for (Object[] object : objects) {
		vehiculos.add(castVehiculo(object));
	    }
	}
	return vehiculos;
    }

    private String consulta() {
	String sb = "select  v.ID, ma.NOMBRE, mo.NOMBRE,  co.NOMBRE, te.NOMBRE, v.CAPACIDAD_PASAJEROS, v.NUMERO_PLACA, "
		+ " v.SERIE, ma.id, mo.id, co.id, te.id, o.id, o.nombre, v.numero_economico, v.numero_activo, "
		+ " v.seguro, v.gps, v.caja_herramienta, p.id, p.nombre, e.id, e.nombre, k.id, k.kilometraje, a.USUARIO, u.NOMBRE, se.id, se.NOMBRE "
		+ " from SG_VEHICULO v "
		+ " inner join SG_MODELO mo on v.SG_MODELO = mo.ID"
		+ " inner join SG_MARCA ma on v.SG_MARCA= ma.ID"
		+ " inner join SG_COLOR co on v.SG_COLOR  = co.ID"
		+ " inner join SG_TIPO_ESPECIFICO te on v.SG_TIPO_ESPECIFICO = te.ID"
		+ " inner join sg_oficina o on v.sg_oficina = o.id"
		+ " left join proveedor p on v.proveedor = p.id"
		+ " inner join estatus e on v.estatus = e.id"
		+ " inner join SG_KILOMETRAJE k on k.SG_VEHICULO = v.ID and k.ACTUAL = 'True' "
                + " LEFT join SG_ASIGNAR_VEHICULO a on a.SG_VEHICULO = v.ID and a.ELIMINADO='False' and a.TERMINADA='False'"
                + " LEFT JOIN USUARIO u on u.id = a.USUARIO and u.ELIMINADO='False'"
                + " LEFT join SG_EMPRESA se on se.ID = u.SG_EMPRESA";
	return sb;
    }

    
    public List<VehiculoVO> traerVehiculoPorOficinaAndGerencia(int idOficina, boolean eliminado, int gerencia) {
	clearCuerpoQuery();
	String queryActual = consulta().replaceAll("select", "select distinct ")
                .replaceAll("LEFT join SG_ASIGNAR_VEHICULO", "inner join SG_ASIGNAR_VEHICULO")
                .replaceAll("LEFT JOIN USUARIO u", "inner join USUARIO u")
		+ " WHERE v.ELIMINADO = ?"
		+ " and v.sg_oficina = ?"
		+ " and v.BAJA = ?"
		+ " and u.GERENCIA = ?"
		+ " and k.KILOMETRAJE = (select max(KILOMETRAJE) from SG_KILOMETRAJE k1 where k1.SG_VEHICULO = v.ID)\n"
		+ " order by mo.nombre asc ";
	List<Object[]> lo = em.createNativeQuery(queryActual)
		.setParameter(1, eliminado)
		.setParameter(2, idOficina)
		.setParameter(3, Constantes.BOOLEAN_FALSE)
		.setParameter(4, gerencia)
		.getResultList();
	List<VehiculoVO> lv = new ArrayList<VehiculoVO>();
	for (Object[] objects : lo) {
	    lv.add(castVehiculo(objects));
	}
	return lv;
    }

    
    public List<VehiculoVO> traerVehiculoPorUsuarioAsignado(String usuario) {
	clearCuerpoQuery();
	String queryActual = consulta()
		.replaceAll("LEFT join SG_ASIGNAR_VEHICULO", "inner join SG_ASIGNAR_VEHICULO")
                .replaceAll("LEFT JOIN USUARIO u", "inner join USUARIO u")
		+ " WHERE v.ELIMINADO = ?"
		+ " and a.USUARIO = ?"
		+ " and v.BAJA = ?"
		+ " and k.KILOMETRAJE = (select max(KILOMETRAJE) from SG_KILOMETRAJE k1 where k1.SG_VEHICULO = v.ID)";
	List<Object[]> lo = em.createNativeQuery(queryActual)
		.setParameter(1, Constantes.NO_ELIMINADO)
		.setParameter(2, usuario)
		.setParameter(3, Constantes.BOOLEAN_FALSE)
		.setParameter(4, Constantes.BOOLEAN_FALSE)
		.setParameter(5, Constantes.BOOLEAN_FALSE)
		.getResultList();
	List<VehiculoVO> lv = new ArrayList<VehiculoVO>();
	for (Object[] objects : lo) {
	    lv.add(castVehiculo(objects));
	}
	return lv;
    }

    
    public VehiculoVO buscarPorSerie(String serie) {
	VehiculoVO vehiculoVO = new VehiculoVO();
	try {
	    clearQuery();
	    query.append(" select v.ID, v.numero_placa, mar.nombre, mo.nombre, te.nombre, v.serie from SG_vehiculo v ");
	    query.append("	inner join sg_modelo mo on v.sg_modelo = mo.id");
	    query.append("	inner join sg_marca mar on v.sg_marca = mar.id");
	    query.append("	inner join sg_tipo_especifico te on v.sg_tipo_especifico = te.id");
	    query.append(" where replace(upper(v.serie), '-' , '') =  replace(upper('").append(serie).append("')").append(", '-' ,'')");

	    Object[] objects = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	    vehiculoVO.setId((Integer) objects[0]);
	    vehiculoVO.setNumeroPlaca((String) objects[1]);
	    vehiculoVO.setMarca((String) objects[2]);
	    vehiculoVO.setModelo((String) objects[3]);
	    vehiculoVO.setTipoEspecifico((String) objects[4]);
	    vehiculoVO.setSerie((String) objects[5]);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "e : " + e.getMessage());
	    vehiculoVO = null;
	}

	return vehiculoVO;
    }

    
    public List<VehiculoVO> traerVehiculoOficinaEstado(int oficina, int estado) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgVehiculoImpl.getAllVehiculoByOficinaList()");
	StringBuilder sb = new StringBuilder();
	sb.append(consulta()).append(" where v.SG_OFICINA = ").append(oficina);
	if (estado > 0) {
	    sb.append(" and v.estatus = ").append(estado);
	}
	sb.append(" and k.KILOMETRAJE = (select max(KILOMETRAJE) from SG_KILOMETRAJE k1 where k1.SG_VEHICULO = v.ID and k1.actual = 'True')")
	.append(" and  v.ELIMINADO = 'False' ORDER  BY v.id");
	List<VehiculoVO> vehiculos = null;
	List<Object[]> objects = em.createNativeQuery(sb.toString()).getResultList();
	if (objects != null) {
	    vehiculos = new ArrayList<VehiculoVO>();
	    for (Object[] object : objects) {
		vehiculos.add(castVehiculo(object));
	    }
	} else {
	    throw new SIAException(SgVehiculoImpl.class.getName(), "getAllVehiculoByOficinaList()",
		    "Faltan parámetros para poder realizar la búsqueda de Vehículo",
		    ("Parámetros: oficina: " + oficina));
	}
	return vehiculos;
    }

    
    public void actualizarPropietario(VehiculoVO vehiculo, String id) {
	SgVehiculo sgVehiculo = find(vehiculo.getId());
	sgVehiculo.setProveedor(new Proveedor(vehiculo.getIdProveedor()));
	sgVehiculo.setModifico(new Usuario(id));
	sgVehiculo.setFechaModifico(new Date());
	sgVehiculo.setHoraModifico(new Date());

	edit(sgVehiculo);
    }

    
    public void actualizarEstado(VehiculoVO vehiculo, String id) {
	SgVehiculo sgVehiculo = find(vehiculo.getId());
	sgVehiculo.setEstatus(new Estatus(vehiculo.getIdEstado()));
	sgVehiculo.setModifico(new Usuario(id));
	sgVehiculo.setFechaModifico(new Date());
	sgVehiculo.setHoraModifico(new Date());

	edit(sgVehiculo);
    }

    
    public SgVehiculo buscarPorUltimoNumeroPlaca(String placa) {
	try {
	    String sb = "select v.* from SG_VEHICULO v "
		    + " where v.NUMERO_PLACA is not null "
		    + " and character_length(v.NUMERO_PLACA) > 4"
		    + " and substring(v.NUMERO_PLACA from character_length(v.NUMERO_PLACA) -3 for character_length(v.NUMERO_PLACA)) = ?"
		    + " and v.ELIMINADO = 'False'";

	    return (SgVehiculo) em.createNativeQuery(sb, SgVehiculo.class).setParameter(1, placa).getSingleResult();
	} catch (Exception e) {
	    UtilLog4j.log.warn(e);
	}
	return null;
    }
    
    
    public List<Object[]> traerVehiculosActivoJson() {
	List<Object[]> lista = new ArrayList<Object[]>();

	try {
	    String sql = 
                    "select  v.ID, ma.NOMBRE, mo.NOMBRE, v.NUMERO_PLACA, co.NOMBRE,o.NOMBRE"
                    + " from SG_VEHICULO v"
                    + " inner join SG_MODELO mo on v.SG_MODELO = mo.ID"
                    + " inner join SG_MARCA ma on v.SG_MARCA= ma.ID"
                    + " inner join SG_COLOR co on v.SG_COLOR  = co.ID"
                    + " inner join SG_TIPO_ESPECIFICO te on v.SG_TIPO_ESPECIFICO = te.ID"
                    + " inner join sg_oficina o on v.sg_oficina = o.id"
                    + " left join proveedor p on v.proveedor = p.id"
                    + " inner join estatus e on v.estatus = e.id"
                    + " inner join SG_KILOMETRAJE k on k.SG_VEHICULO = v.ID and k.ACTUAL = ?"
                    + " WHERE v.ELIMINADO = ?"
                    + " AND v.baja = ?"
                    + " and k.KILOMETRAJE = (select max(k1.KILOMETRAJE) from SG_KILOMETRAJE k1 where k1.SG_VEHICULO = v.ID)"
                    + " order by mo.nombre asc";

	    lista = em.createNativeQuery(sql)
                    .setParameter(1, Constantes.BOOLEAN_TRUE)
                    .setParameter(2, Constantes.NO_ELIMINADO)
                    .setParameter(3, Constantes.BOOLEAN_FALSE)
                    .getResultList();
	    
	} catch (Exception e) {
	     UtilLog4j.log.fatal(this, "Excepcion los usuarios " + e, e);
	}
        
        return lista;
    }
}
