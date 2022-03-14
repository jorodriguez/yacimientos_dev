/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgCaracteristicaCocina;
import sia.modelo.SgCaracteristicaGym;
import sia.modelo.SgCaracteristicaHabitacion;
import sia.modelo.SgCaracteristicaStaff;
import sia.modelo.SgCocina;
import sia.modelo.SgDireccion;
import sia.modelo.SgGym;
import sia.modelo.SgOficina;
import sia.modelo.SgStaff;
import sia.modelo.SgStaffChecklist;
import sia.modelo.SgStaffHabitacion;
import sia.modelo.SgTipo;
import sia.modelo.Usuario;
import sia.modelo.sgl.staff.vo.StaffVo;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sgl.vo.CheckListDetalleVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgStaffImpl extends AbstractFacade<SgStaff> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private UsuarioImpl usuarioService;
    @Inject
    private SgDireccionImpl direccionService;
    @Inject
    private SgStaffHabitacionImpl habitacionStaffService;
    @Inject
    private SgGymImpl gimnasioService;
    @Inject
    private SgCocinaImpl cocinaService;
    @Inject
    private SgCaracteristicaImpl caracteristicaService;
    @Inject
    private SgCaracteristicaStaffImpl caracteristicaStaffService;
    @Inject
    private SgCaracteristicaHabitacionImpl caracteristicaHabitacionService;
    @Inject
    private SgCaracteristicaGymImpl caracteristicaGimnasioService;
    @Inject
    private SgCaracteristicaCocinaImpl caracteristicaCocinaService;
    @Inject
    private SgStaffChecklistImpl staffChecklistService;
    @Inject
    private SgChecklistDetalleImpl checklistDetalleService;
    @Inject
    private SgChecklistImpl checklistService;
    @Inject
    private SgTipoImpl sgTipoRemote;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgStaffImpl() {
	super(SgStaff.class);
    }

    
    public List<SgStaff> getAllStaffByStatus(boolean status) {
	UtilLog4j.log.info(this, "SgStaffImpl.getAllStaffByStatus()");
	if (status) {
	    return em.createQuery("SELECT s FROM SgStaff s WHERE s.eliminado = :estado ORDER BY s.id ASC").setParameter("estado", status).getResultList();
	}
	return null;
    }

    
    public List<SgStaff> getAllStaffByStatusAndOficina(boolean status, int oficina) {
        UtilLog4j.log.info(this, "SgStaffImpl.getAllStaffByStatusAndOficina()");
        List<SgStaff> staffList = null;
        try {
            if (oficina > 0) {
                staffList = em.createQuery("SELECT s FROM SgStaff s WHERE s.eliminado = :estado AND s.sgOficina.id = :idOficina ORDER BY s.id ASC").setParameter("estado", status).setParameter("idOficina", oficina).getResultList();
                if (staffList != null) {
                    UtilLog4j.log.info(this, "Se encontraron " + staffList.size() + " staff");                    
                } 
            } 
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());
            staffList = null;
        }
        return staffList;
    }

    
    public List<SgStaff> getAllStaffWithAvailableRoomsByOficinaList(int oficina) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.getAllStaffWithAvailableRoomsByOficinaList()");
	List<SgStaff> staffList = getAllStaffByStatusAndOficina(Constantes.NO_ELIMINADO, oficina);
	List<SgStaff> staffConHabitacionesDisponiblesList = new ArrayList<SgStaff>();

	int contHabitacionLibre = 0;

	for (SgStaff staff : staffList) {
	    List<SgStaffHabitacion> habitaciones = habitacionStaffService.getAllHabitacionesByStaff(staff, Constantes.NO_ELIMINADO);
	    if (habitaciones != null && habitaciones.size() > 0) {
		for (SgStaffHabitacion habitacion : habitaciones) {
		    if (!habitacion.isOcupada()) {
			contHabitacionLibre++;
		    }
		}
		if (contHabitacionLibre > 0) {
		    staffConHabitacionesDisponiblesList.add(staff);
		}
	    }
	    contHabitacionLibre++;
	}
	return (staffConHabitacionesDisponiblesList != null ? staffConHabitacionesDisponiblesList : Collections.EMPTY_LIST);
    }

    
    public void createStaff(SgStaff staff, int oficina, SgDireccion direccion, String idUsuario, boolean status, int idPais) throws Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.createStaff()");

	if (staff != null && direccion != null && idUsuario != null && !idUsuario.equals("")) {
	    try {
		//Guardando la dirección
		direccion = direccionService.guardarDireccion(direccion, new Usuario(idUsuario), status, idPais);

		try {
		    //Guardando el Staff
		    staff.setSgDireccion(direccion);
		    staff.setSgOficina(new SgOficina(oficina));
		    staff.setNumeroCuartos(0);
		    staff.setGenero(new Usuario(idUsuario));
		    staff.setFechaGenero(new Date());
		    staff.setHoraGenero(new Date());
		    staff.setEliminado(status);
		    super.create(staff);

		} catch (Exception e) {
		    UtilLog4j.log.info(this, e.getMessage());
		    throw new Exception("Error al guardar el Staff");
		}
	    } catch (Exception e) {
		UtilLog4j.log.info(this, e.getMessage());
		throw new Exception("Error al guardar la dirección");
	    }
	}
    }

    
    public void updateStaff(SgStaff staff, String idUsuario, boolean status, int idPais) throws Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.updateStaff()");

	if (staff != null && idUsuario != null && !idUsuario.equals("")) {
	    try {
		//Actualizando la dirección
		direccionService.modificarSgDireccion(staff.getSgDireccion(), new Usuario(idUsuario), idPais);

		try {
		    //Actualizando el Staff
		    staff.setGenero(new Usuario(idUsuario));
		    staff.setFechaGenero(new Date());
		    staff.setHoraGenero(new Date());
		    staff.setEliminado(status);
		    super.edit(staff);
		} catch (Exception e) {
		    UtilLog4j.log.info(this, e.getMessage());
		    throw new Exception("Error al actualizar el Staff");
		}
	    } catch (Exception e) {
		UtilLog4j.log.info(this, e.getMessage());
		throw new Exception("Error al actualizar la Dirección");
	    }
	}
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SgStaff update(SgStaff staff, String idUsuario, int idPais) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.update()");

	//Actualizando la dirección
	direccionService.modificarSgDireccion(staff.getSgDireccion(), new Usuario(idUsuario), idPais);

//        staff.setModifico(new Usuario(idUsuario));
//        staff.setFechaModifico(new Date());
//        staff.setHoraModifico(new Date());
	super.edit(staff);

	return staff;
    }

    
    public void deleteStaff(SgStaff staff, String idUsuario, boolean status) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.deleteStaff()");
	UtilLog4j.log.info(this, "Staff: " + staff.getId());

	//Validar que el Staff no esté siendo usado en algún otro lugar
	if (!isUsed(staff)) {
	    direccionService.deleteDireccion(staff.getSgDireccion(), idUsuario, status);
	    //Eliminando Habitaciones del Staff
	    List<SgStaffHabitacion> habitaciones = habitacionStaffService.getAllHabitacionesByStaff(staff, Constantes.NO_ELIMINADO);
	    for (SgStaffHabitacion habitacion : habitaciones) {
		updateHabitacionStaff(habitacion, idUsuario, Constantes.ELIMINADO);
	    }
	    //Eliminando los Gimnasios del Staff
	    List<SgGym> gimnasios = gimnasioService.getAllGimnasiosByStaff(staff, Constantes.NO_ELIMINADO);
	    for (SgGym gimnasio : gimnasios) {
		updateGimnasioStaff(gimnasio, idUsuario, Constantes.ELIMINADO);
	    }
	    //Eliminando Cocinas del Staff
	    List<SgCocina> cocinas = cocinaService.getAllCocinasByStaff(staff, Constantes.NO_ELIMINADO);
	    for (SgCocina cocina : cocinas) {
		updateCocinaStaff(cocina, idUsuario, Constantes.ELIMINADO);
	    }

	    //Eliminando las relaciones de las Características con el Staff
	    //Relaciones con las Generales
	    List<CaracteristicaVo> caracteristicasStaff = caracteristicaStaffService.getAllCaracteristicaStaffByStaffList(staff.getId());
	    for (CaracteristicaVo cs : caracteristicasStaff) {
		caracteristicaStaffService.delete(cs.getId(), idUsuario);
	    }

	    //Eliminando el Staff
	    staff.setGenero(new Usuario(idUsuario));
	    staff.setFechaGenero(new Date());
	    staff.setHoraGenero(new Date());
	    staff.setEliminado(status);
	    try {
		super.edit(staff);
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
		UtilLog4j.log.fatal(this, "Hubo un error al eliminar el Staff: " + staff.getId());
		e.printStackTrace();
	    }

	    try {
		//Eliminando Checklist de Staff
		//Trayendo relaciones entre el Staff y Checklist
		List<SgStaffChecklist> checklistStaffList = staffChecklistService.getAllStaffChecklistByStaffAndStatusList(staff.getId(), Constantes.NO_ELIMINADO);
		for (SgStaffChecklist checklistStaff : checklistStaffList) {
		    //Traer todos los Detalles;
		    List<CheckListDetalleVo> items = checklistDetalleService.getAllItemsChecklistList(checklistStaff.getSgChecklist().getId(), status);
		    //Eliminando items de Detalle
		    for (CheckListDetalleVo item : items) {
			checklistDetalleService.deleteItemChecklist(item.getId(), idUsuario);
		    }
		    //Eliminar Relaciones
		    staffChecklistService.deleteStaffChecklist(checklistStaff, idUsuario);
		    //Eliminar Checklist
		    checklistService.deleteChecklist(checklistStaff.getSgChecklist(), idUsuario);
		}
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, "Hubo un error al eliminar los Checklist");
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
	    }
	} else {
	    throw new SIAException(this.getClass().getName(), "deleteStaff()", "No se puede eliminar el Staff debido a que ya está siendo usado en otras opciones del Sistema", "Usado en otro lado el Staff con id: " + staff.getId());
	}
    }

    
    public void addHabitacionToStaff(SgStaff staff, SgStaffHabitacion habitacion, String idUsuario, boolean status) throws Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.addHabitacionToStaff()");

	if (staff != null && habitacion != null && idUsuario != null && !idUsuario.equals("")) {
	    SgCaracteristica caracteristica = null;
	    try {
		habitacion.setOcupada(status);
		habitacion.setSgStaff(staff);
		habitacion.setGenero(new Usuario(idUsuario));
		habitacion.setFechaGenero(new Date());
		habitacion.setHoraGenero(new Date());
		habitacion.setEliminado(status);

		habitacionStaffService.create(habitacion);

		try { //Actualizar el número de habitaciones del Staff
		    UtilLog4j.log.info(this, "Actualizando el número de Habitaciones del Staff");
		    UtilLog4j.log.info(this, "Habitaciones actuales: " + staff.getNumeroCuartos());
		    staff.setNumeroCuartos(staff.getNumeroCuartos() + 1);
		    super.edit(staff);
		    UtilLog4j.log.info(this, "Habitaciones actualizadas: " + staff.getNumeroCuartos());

		    try { //Crear Característica principal
			caracteristica = caracteristicaService.create("Habitación " + habitacion.getNombre(), true, Constantes.CERO, idUsuario);

			try { //Crear la relación entre Característica principal y Habitación
			    caracteristicaHabitacionService.create(caracteristica, habitacion, null, idUsuario);
			} catch (Exception e) {
			    UtilLog4j.log.fatal(this, e.getMessage());
			    throw new Exception("Error al crear la relación entre la Característica principal" + caracteristica.getNombre() + " y la Habitación " + habitacion.getNombre());
			}
		    } catch (Exception e) {
			UtilLog4j.log.fatal(this, e.getMessage());
			throw new Exception("Error al crear la Característica principal de la Habitación: " + habitacion.getId());
		    }
		} catch (Exception e) {
		    UtilLog4j.log.fatal(this, e.getMessage());
		    throw new Exception("Error al actualizar el número de Habitaciones (agregar) del Staff");
		}
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());

		throw new Exception("Error al agregar la Habitación al Staff");
	    }
	} else {
	    throw new Exception("Faltan datos para agregar la Habitación al Staff");
	}
    }

    
    public void addGimnasioToStaff(SgStaff staff, SgGym gimnasio, String idUsuario, boolean status) throws Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.addGimnasioToStaff()");

	if (staff != null && gimnasio != null && idUsuario != null && !idUsuario.equals("")) {
	    SgCaracteristica caracteristica = null;
	    try {
		gimnasio.setSgStaff(staff);
		gimnasio.setGenero(new Usuario(idUsuario));
		gimnasio.setFechaGenero(new Date());
		gimnasio.setHoraGenero(new Date());
		gimnasio.setEliminado(status);

		gimnasioService.create(gimnasio);

		try { //Crear Característica principal
		    caracteristica = caracteristicaService.create("Gimnasio " + gimnasio.getNombre(), true, Constantes.CERO, idUsuario);

		    try { //Crear la relación entre Característica principal y Gimnasio
			caracteristicaGimnasioService.create(caracteristica, gimnasio, null, idUsuario);
		    } catch (Exception e) {
			UtilLog4j.log.fatal(this, e.getMessage());
			e.printStackTrace();
			throw new Exception("Error al crear la relación entre la Característica principal" + caracteristica.getNombre() + " y el Gimnasio " + gimnasio.getNombre());
		    }
		} catch (Exception e) {
		    UtilLog4j.log.fatal(this, e.getMessage());
		    e.printStackTrace();
		    throw new Exception("Error al crear la Característica principal del Gimnasio: " + gimnasio.getId());
		}
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
		throw new Exception("Error al agregar el Gimnasio al Staff");
	    }
	} else {
	    throw new Exception("Faltan datos para agregar el Gimnasio al Staff");
	}
    }

    
    public void addCocinaToStaff(SgStaff staff, SgCocina cocina, String idUsuario, boolean status) throws Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.addCocinaToStaff()");

	if (staff != null && cocina != null && idUsuario != null && !idUsuario.equals("")) {
	    SgCaracteristica caracteristica = null;
	    try {
		cocina.setSgStaff(staff);
		cocina.setGenero(new Usuario(idUsuario));
		cocina.setFechaGenero(new Date());
		cocina.setHoraGenero(new Date());
		cocina.setEliminado(status);

		cocinaService.create(cocina);

		try { //Crear Característica principal
		    caracteristica = caracteristicaService.create("Cocina " + cocina.getNombre(), true, Constantes.CERO, idUsuario);

		    try { //Crear la relación entre Característica principal y Cocina
			caracteristicaCocinaService.create(caracteristica, cocina, null, idUsuario);
		    } catch (Exception e) {
			UtilLog4j.log.fatal(this, e.getMessage());
			throw new Exception("Error al crear la relación entre la Característica principal" + caracteristica.getNombre() + " y la Cocina " + cocina.getId());
		    }
		} catch (Exception e) {
		    UtilLog4j.log.fatal(this, e.getMessage());
		    throw new Exception("Error al crear la Característica principal de la Cocina: " + cocina.getId());
		}
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
		throw new Exception("Error al agregar la Cocina al Staff");
	    }
	} else {
	    throw new Exception("Faltan datos para agregar la Cocina al Staff");
	}
    }

    
    public void updateHabitacionStaff(SgStaffHabitacion habitacion, String idUsuario, boolean status) throws Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.updateHabitacionStaff()");
	UtilLog4j.log.info(this, "Status: " + status);
	UtilLog4j.log.info(this, "Habitación a actualizar: " + habitacion.getId());
//        try {
//            habitacion.setEliminado(status);
//            habitacionStaffService.edit(habitacion);
//        }
//        catch (Exception e) {
//            UtilLog4j.log.info(this, "Error al actualizar la Habitación");
//            UtilLog4j.log.info(this, e.getMessage());
//            e.printStackTrace();
//            throw new Exception("Error al actualizar la Habitación");
//        }

	if (status) {
	    //Actualizar la Habitación
	    this.habitacionStaffService.edit(habitacion);
	    //Actualizar la Característica Principal
	    SgCaracteristicaHabitacion caracteristicaHabitacion = caracteristicaHabitacionService.getCaracteristicaHabitacionPrincipalByHabitacion(habitacion);
	    SgCaracteristica caracteristica = caracteristicaHabitacion.getSgCaracteristica();
	    caracteristica.setNombre("Habitación " + habitacion.getNombre());
	    try {
		caracteristicaService.update(caracteristica, idUsuario);
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, "Error al actualizar la Característica Principal de la Habitación");
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
		throw new Exception("Error al actualizar la Característica Principal de la Habitación");
	    }
	} else if (status) { //Si se está eliminando la Habitación, debería decrementarse el número de cuartos del Staff

	    UtilLog4j.log.info(this, "Actualizando el número de Habitaciones del Staff");
	    SgStaff staff = habitacion.getSgStaff();
	    UtilLog4j.log.info(this, "Número actual de cuartos: " + staff.getNumeroCuartos());
	    staff.setNumeroCuartos(staff.getNumeroCuartos() - 1);
	    try {
		super.edit(staff);
		UtilLog4j.log.info(this, "Número actualizado de cuartos: " + staff.getNumeroCuartos());
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, "Error al actualizar el número de Habitaciones (quitar) del Staff");
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
		throw new Exception("Error al actualizar el número de Habitaciones (quitar) del Staff");
	    }

	    //También se deben eliminar las relaciones de la Habitación con sus Características
	    List<CaracteristicaVo> caracteristicasHabitacion = caracteristicaHabitacionService.getAllCaracteristicaHabitacionByHabitacionList(habitacion.getId());
	    try {
		for (CaracteristicaVo ch : caracteristicasHabitacion) {
		    caracteristicaHabitacionService.delete(ch.getId(), idUsuario);
		}
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, "Error al eliminar en cascada las Caracteristica de la Habitación: " + habitacion.getId());
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
		throw new Exception("Error al eliminar en cascada las Caracteristica de la Habitación: " + habitacion.getId());
	    }

	    //Eliminar Relación con la Principal y Principal
	    SgCaracteristicaHabitacion ch = caracteristicaHabitacionService.getCaracteristicaHabitacionPrincipalByHabitacion(habitacion);
	    caracteristicaHabitacionService.delete(ch.getId(), idUsuario);
	    SgCaracteristica car = ch.getSgCaracteristica();

	    try {
		caracteristicaService.delete(car, idUsuario);
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, "Hubo un error al eliminar la Característica Principal: " + car.getId());
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
	    }
	}
    }

    
    public SgStaffHabitacion deleteHabitacionStaff(SgStaffHabitacion habitacionStaff, String idUsuario, int idPais) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.deleteHabitacionStaff()");

	if (habitacionStaff.isOcupada()) {

	    //Eliminar Relación con Característica Principal y Característica Principal
	    SgCaracteristicaHabitacion ch = caracteristicaHabitacionService.getCaracteristicaHabitacionPrincipalByHabitacion(habitacionStaff);
	    caracteristicaHabitacionService.delete(ch.getId(), idUsuario);
	    SgCaracteristica car = ch.getSgCaracteristica();
	    caracteristicaService.delete(car, idUsuario);

	    //Eliminar las relaciones de la Habitación con sus Características Secundarias
	    List<CaracteristicaVo> caracteristicasHabitacion = caracteristicaHabitacionService.getAllCaracteristicaHabitacionByHabitacionList(habitacionStaff.getId());
	    for (CaracteristicaVo carh : caracteristicasHabitacion) {
		caracteristicaHabitacionService.delete(carh.getId(), idUsuario);
	    }

	    habitacionStaffService.delete(habitacionStaff, idUsuario);

	    //Actualizar número de Habitaciones de Staff
	    SgStaff staff = habitacionStaff.getSgStaff();
	    UtilLog4j.log.info(this, "Número actual de cuartos: " + staff.getNumeroCuartos());
	    staff.setNumeroCuartos(staff.getNumeroCuartos() - 1);
	    update(staff, idUsuario, idPais);
	    UtilLog4j.log.info(this, "Número actualizado de cuartos: " + staff.getNumeroCuartos());
	} else {
	    throw new SIAException(this.getClass().getName(), "deleteHabitacionStaff", "", "sgl.staff.habitacion.mensaje.error.habitacionOcupada", "La Habitación está ocupada");
	}

	return habitacionStaff;
    }

    
    public void updateGimnasioStaff(SgGym gimnasio, String idUsuario, boolean status) throws Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.updateGimnasioStaff()");
	try {
	    gimnasio.setEliminado(status);
	    gimnasioService.edit(gimnasio);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    e.printStackTrace();
	    throw new Exception("Error al actualizar el Gimnasio");
	}
	if (status) { //Si se está eliminando el Gimnasio, deberían eliminarse también sus relaciones con Características
	    List<CaracteristicaVo> caracteristicasGimnasio = caracteristicaGimnasioService.getAllCaracteristicaGymByGimnasioList(gimnasio.getId());
	    try {
		for (CaracteristicaVo ch : caracteristicasGimnasio) {
		    caracteristicaGimnasioService.delete(ch.getId(), idUsuario);
		}
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
		throw new Exception("Error al eliminar en cascada las Caracteristica del Gimnasio: " + gimnasio.getId());
	    }

	    //Eliminar Relación con la Principal y Principal
	    SgCaracteristicaGym cg = caracteristicaGimnasioService.getCaracteristicaGymPrincipalByGimnasio(gimnasio);
	    caracteristicaGimnasioService.delete(cg.getId(), idUsuario);
	    SgCaracteristica car = cg.getSgCaracteristica();

	    try {
		caracteristicaService.delete(car, idUsuario);
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, "Hubo un error al eliminar la Característica Principal: " + car.getId());
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
	    }
	} else if (status) {
	    //Actualizar la Característica Principal
	    SgCaracteristicaGym caracteristicaGimnasio = caracteristicaGimnasioService.getCaracteristicaGymPrincipalByGimnasio(gimnasio);
	    SgCaracteristica caracteristica = caracteristicaGimnasio.getSgCaracteristica();
	    caracteristica.setNombre("Gimnasio " + gimnasio.getNombre());

	    try {
		caracteristicaService.update(caracteristica, idUsuario);
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
		throw new Exception("Error al actualizar la Característica Principal del Gimnasio");
	    }
	}
    }

    
    public void updateCocinaStaff(SgCocina cocina, String idUsuario, boolean status) throws Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.updateCocinaStaff()");
	try {
	    cocina.setEliminado(status);
	    cocinaService.edit(cocina);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    e.printStackTrace();
	    throw new Exception("Error al actualizar la Cocina");
	}
	if (status) { //Si se está eliminando el Gimnasio, deberían eliminarse también sus relaciones con Características
	    List<CaracteristicaVo> caracteristicasCocina = caracteristicaCocinaService.getAllCaracteristicaCocinaByCocinaList(cocina.getId());
	    try {
		for (CaracteristicaVo ch : caracteristicasCocina) {
		    caracteristicaCocinaService.delete(ch.getId(), idUsuario);
		}
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
		throw new Exception("Error al eliminar en cascada las Caracteristica de la : " + cocina.getId());
	    }

	    //Eliminar Relación con la Principal y Principal
	    SgCaracteristicaCocina cc = caracteristicaCocinaService.getCaracteristicaCocinaPrincipalByCocina(cocina);
	    caracteristicaCocinaService.delete(cc.getId(), idUsuario);
	    SgCaracteristica car = cc.getSgCaracteristica();
	    car.setEliminado(Constantes.ELIMINADO);

	    try {
		caracteristicaService.delete(car, idUsuario);
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, "Hubo un error al eliminar la Característica Principal: " + car.getId());
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
	    }
	} else if (status) {
	    //Actualizar la Característica Principal
	    SgCaracteristicaCocina caracteristicaCocina = caracteristicaCocinaService.getCaracteristicaCocinaPrincipalByCocina(cocina);
	    SgCaracteristica caracteristica = caracteristicaCocina.getSgCaracteristica();
	    caracteristica.setNombre("Cocina " + cocina.getNombre());

	    try {
		caracteristicaService.update(caracteristica, idUsuario);
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
		throw new Exception("Error al actualizar la Característica Principal de la Cocina");
	    }
	}
    }

    
    public boolean isUsed(SgStaff sgStaff) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.isUsed()");

	int cont = 0;

	List<Object> staffs = em.createQuery("SELECT sh FROM SgStaffHabitacion sh WHERE sh.sgStaff.id = :idStaff AND sh.eliminado = :eliminado").setParameter("idStaff", sgStaff.getId()).setParameter("eliminado", Constantes.NO_ELIMINADO).getResultList();
	if (staffs != null && !staffs.isEmpty()) {
	    UtilLog4j.log.info(this, "SgStaff " + sgStaff.getId() + " usado en SgStaffHabitacion");
	    cont++;
	    staffs = null;
	}

	staffs = em.createQuery("SELECT schk FROM SgStaffChecklist schk WHERE schk.sgStaff.id = :idStaff AND schk.eliminado = :eliminado").setParameter("idStaff", sgStaff.getId()).setParameter("eliminado", Constantes.NO_ELIMINADO).getResultList();
	if (staffs != null && !staffs.isEmpty()) {
	    UtilLog4j.log.info(this, "SgStaff " + sgStaff.getId() + " usado en SgStaffChecklist");
	    cont++;
	    staffs = null;
	}

	if (cont == 0) {
	    UtilLog4j.log.info(this, "El Staff " + sgStaff.getId() + " no está siendo usado");
	    return false;
	} else {
	    UtilLog4j.log.info(this, "El Staff " + sgStaff.getId() + " está siendo usado");
	    return true;
	}
    }

    
    public List<SgStaffHabitacion> getAllHabitacionesByStaff(SgStaff staff, boolean status) {
	UtilLog4j.log.info(this, "SgStaffImpl.getAllHabitacionesByStaff()");
	return habitacionStaffService.getAllHabitacionesByStaff(staff, status);
    }

    
    public List<SgGym> getAllGimnasiosByStaff(SgStaff staff, boolean status) {
	UtilLog4j.log.info(this, "SgStaffImpl.getAllGimnasiosByStaff()");
	return gimnasioService.getAllGimnasiosByStaff(staff, status);
    }

    
    public List<SgCocina> getAllCocinasByStaff(SgStaff staff, boolean status) {
	UtilLog4j.log.info(this, "SgStaffImpl.getAllCocinasByStaff()");
	return cocinaService.getAllCocinasByStaff(staff, status);
    }

    
    public List<CaracteristicaVo> getAllCaracteristicasStaffList(int staff) throws SIAException, Exception {
	return caracteristicaStaffService.getAllCaracteristicaStaffByStaffList(staff);
    }

    
    public List<CaracteristicaVo> getAllCaracteristicasHabitacionList(int habitacion) throws SIAException, Exception {
	return caracteristicaHabitacionService.getAllCaracteristicaHabitacionByHabitacionList(habitacion);
    }

    
    public List<CaracteristicaVo> getAllCaracteristicasGimnasioList(int gimnasio) throws SIAException, Exception {
	return caracteristicaGimnasioService.getAllCaracteristicaGymByGimnasioList(gimnasio);
    }

    
    public List<CaracteristicaVo> getAllCaracteristicasCocinaList(int cocina) throws SIAException, Exception {
	return caracteristicaCocinaService.getAllCaracteristicaCocinaByCocinaList(cocina);
    }

    
    public SgCaracteristica addCaracteristica(Object relacion, Object area, String nombreCaracteristica, Integer cantidad, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.addCaracteristica()");
	SgTipo sgTipo = sgTipoRemote.find(3);
	SgCaracteristica caracteristicaNueva = null;

	if (relacion instanceof SgCaracteristicaStaff) {
	    UtilLog4j.log.info(this, "SgCaracteristicaStaff");

	    //Buscar si existe la Característica
	    SgCaracteristica car = caracteristicaService.findByName(nombreCaracteristica);

	    if (car != null) { //La Característica existe. Solo hay que crear la relación
		//No permitir duplicar la realación
		SgCaracteristicaStaff carStaffExistente = caracteristicaStaffService.findByCaracteristicaAndStaff(car, (SgStaff) area);

		if (carStaffExistente == null) {
		    caracteristicaStaffService.create(car, (SgStaff) area, cantidad, idUsuario);
		} else {
		    UtilLog4j.log.info(this, "La Característica ya está asignada");
		    throw new SIAException("La Característica \" " + car.getNombre() + "\"  ya está asignada");
		}
	    } else { //La Característica no existe. Hay que crearla y luego la relación
		UtilLog4j.log.info(this, "Nombre nueva Característica: " + nombreCaracteristica);
		//Crear Característica
		caracteristicaNueva = caracteristicaService.create(nombreCaracteristica, false, Constantes.CERO, idUsuario);
		//Crear relación
		caracteristicaStaffService.create(caracteristicaNueva, (SgStaff) area, cantidad, idUsuario);
	    }
	}

	if (relacion instanceof SgCaracteristicaHabitacion) {
	    UtilLog4j.log.info(this, "SgCaracteristicaHabitacion");

	    //Buscar si existe la Característica
	    SgCaracteristica car = caracteristicaService.findByName(nombreCaracteristica);

	    if (car != null) { //La Característica existe. Solo hay que crear la relación
		//No permitir duplicar la realación
		SgCaracteristicaHabitacion carHabitacionExistente = caracteristicaHabitacionService.findByCaracteristicaAndHabitacion(car, (SgStaffHabitacion) area);

		if (carHabitacionExistente == null) {
		    caracteristicaHabitacionService.create(car, (SgStaffHabitacion) area, cantidad, idUsuario);
		} else {
		    UtilLog4j.log.info(this, "La Característica ya está asignada");
		    throw new SIAException("La Característica \" " + car.getNombre() + "\"  ya está asignada");
		}
	    } else { //La Característica no existe. Hay que crearla y luego la relación
		UtilLog4j.log.info(this, "Nombre nueva Característica: " + nombreCaracteristica);
		//Crear Característica
		caracteristicaNueva = caracteristicaService.create(nombreCaracteristica, false, Constantes.CERO, idUsuario);
		//Crear relación
		caracteristicaHabitacionService.create(caracteristicaNueva, (SgStaffHabitacion) area, cantidad, idUsuario);
	    }
	}

	if (relacion instanceof SgCaracteristicaCocina) {
	    UtilLog4j.log.info(this, "SgCaracteristicaCocina");

	    //Buscar si existe la Característica
	    SgCaracteristica car = caracteristicaService.findByName(nombreCaracteristica);

	    if (car != null) { //La Característica existe. Solo hay que crear la relación
		//No permitir duplicar la realación
		SgCaracteristicaCocina carCocinaExistente = caracteristicaCocinaService.findByCaracteristicaAndCocina(car, (SgCocina) area);

		if (carCocinaExistente == null) {
		    caracteristicaCocinaService.create(car, (SgCocina) area, cantidad, idUsuario);
		} else {
		    UtilLog4j.log.info(this, "La Característica ya está asignada");
		    throw new SIAException("La Característica \" " + car.getNombre() + "\"  ya está asignada");
		}
	    } else { //La Característica no existe. Hay que crearla y luego la relación
		UtilLog4j.log.info(this, "Nombre nueva Característica: " + nombreCaracteristica);
		//Crear Característica
		caracteristicaNueva = caracteristicaService.create(nombreCaracteristica, false, Constantes.CERO, idUsuario);
		//Crear relación
		caracteristicaCocinaService.create(caracteristicaNueva, (SgCocina) area, cantidad, idUsuario);
	    }
	}

	if (relacion instanceof SgCaracteristicaGym) {
	    UtilLog4j.log.info(this, "SgCaracteristicaGym");

	    //Buscar si existe la Característica
	    SgCaracteristica car = caracteristicaService.findByName(nombreCaracteristica);

	    if (car != null) { //La Característica existe. Solo hay que crear la relación
		//No permitir duplicar la realación
		SgCaracteristicaGym carGimnasioExistente = caracteristicaGimnasioService.findByCaracteristicaAndGimnasio(car, (SgGym) area);

		if (carGimnasioExistente == null) {
		    caracteristicaGimnasioService.create(car, (SgGym) area, cantidad, idUsuario);
		} else {
		    UtilLog4j.log.info(this, "La Característica ya está asignada");
		    throw new SIAException("La Característica \" " + car.getNombre() + "\"  ya está asignada");
		}
	    } else { //La Característica no existe. Hay que crearla y luego la relación
		UtilLog4j.log.info(this, "Nombre nueva Característica: " + nombreCaracteristica);
		//Crear Característica
		caracteristicaNueva = caracteristicaService.create(nombreCaracteristica, false, Constantes.CERO, idUsuario);
		//Crear relación
		caracteristicaGimnasioService.create(caracteristicaNueva, (SgGym) area, cantidad, idUsuario);
	    }
	}

	return caracteristicaNueva;
    }

    
    public void removeCaracteristica(Object relacion, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgStaffImpl.removeCaracteristica()");

	if (relacion instanceof SgCaracteristicaStaff) {
	    caracteristicaStaffService.delete(((SgCaracteristicaStaff) relacion).getId(), idUsuario);
	}
	if (relacion instanceof SgCaracteristicaHabitacion) {
	    caracteristicaHabitacionService.delete(((SgCaracteristicaHabitacion) relacion).getId(), idUsuario);
	}
	if (relacion instanceof SgCaracteristicaGym) {
	    caracteristicaGimnasioService.delete(((SgCaracteristicaGym) relacion).getId(), idUsuario);
	}
	if (relacion instanceof SgCaracteristicaCocina) {
	    caracteristicaCocinaService.delete(((SgCaracteristicaCocina) relacion).getId(), idUsuario);
	}
    }

    
    public List<StaffVo> traerStaff() {
	clearQuery();
	List<StaffVo> lstaff = new ArrayList<StaffVo>();
	try {
	    query.append("select st.ID, st.NOMBRE, st.NUMERO_STAFF, st.NUMERO_CUARTOS, o.ID, o.NOMBRE ");
	    query.append(" from SG_STAFF st");
	    query.append("      inner join SG_OFICINA o on st.SG_OFICINA = o.ID");
	    query.append("  where st.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();

	    for (Object[] objects : lo) {
		lstaff.add(castStaffVo(objects));
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(e);
	}
	return lstaff;
    }

    private StaffVo castStaffVo(Object[] objects) {
	StaffVo staffVo = new StaffVo();
	staffVo.setIdStaff((Integer) objects[0]);
	staffVo.setNombre((String) objects[1]);
	staffVo.setNumeroStaff((String) objects[2]);
	staffVo.setNumeroCuarto((Integer) objects[3]);
	staffVo.setIdOficina((Integer) objects[4]);
	staffVo.setOficina((String) objects[5]);
	return staffVo;
    }

    
    public StaffVo buscarPorNombre(String nombre) {
	clearQuery();
	try {
	    query.append("select st.ID, st.NOMBRE, st.NUMERO_STAFF, st.NUMERO_CUARTOS, o.ID, o.NOMBRE ");
	    query.append(" from SG_STAFF st");
	    query.append("      inner join SG_OFICINA o on st.SG_OFICINA = o.ID");
	    query.append("  where st.nombre = '").append(nombre).append("' and st.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	    Object[] lo = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();

	    return castStaffVo(lo);

	} catch (Exception e) {
	    UtilLog4j.log.fatal(e);
	}
	return null;
    }
}
