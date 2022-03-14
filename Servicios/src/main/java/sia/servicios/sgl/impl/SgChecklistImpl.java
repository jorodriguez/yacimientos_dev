/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.LocalBean;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgCaracteristicaCocina;
import sia.modelo.SgCaracteristicaComedor;
import sia.modelo.SgCaracteristicaGym;
import sia.modelo.SgCaracteristicaHabitacion;
import sia.modelo.SgCaracteristicaSalaJunta;
import sia.modelo.SgCaracteristicaSanitario;
import sia.modelo.SgChecklist;
import sia.modelo.SgChecklistDetalle;
import sia.modelo.SgCocina;
import sia.modelo.SgComedor;
import sia.modelo.SgGym;
import sia.modelo.SgOficinaChecklist;
import sia.modelo.SgSalaJunta;
import sia.modelo.SgSanitario;
import sia.modelo.SgStaff;
import sia.modelo.SgStaffChecklist;
import sia.modelo.SgStaffHabitacion;
import sia.modelo.SgVehiculoChecklist;
import sia.modelo.Usuario;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sgl.vo.CheckListDetalleVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgChecklistImpl extends AbstractFacade<SgChecklist> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    private SgStaffChecklistImpl staffChecklistService;
    @Inject
    private SgOficinaChecklistImpl oficinaChecklistService;
    @Inject
    private SgVehiculoChecklistImpl vehiculoChecklistService;
    @Inject
    private SgChecklistDetalleImpl checklistDetalleService;
    @Inject
    private SgStaffHabitacionImpl habitacionService;
    @Inject
    private SgGymImpl gimnasioService;
    @Inject
    private SgCocinaImpl cocinaService;
    @Inject
    private SgSalaJuntaImpl salaJuntasService;
    @Inject
    private SgComedorImpl comedorService;
    @Inject
    private SgSanitarioImpl sanitarioService;
    @Inject
    private SgCaracteristicaImpl caracteristicaService;
    @Inject
    private SgCaracteristicaStaffImpl caracteristicaStaffService;
    @Inject
    private SgCaracteristicaOficinaImpl caracteristicaOficinaService;
    @Inject
    private SgCaracteristicaVehiculoImpl caracteristicaVehiculoService;
    @Inject
    private SgCaracteristicaHabitacionImpl caracteristicaHabitacionService;
    @Inject
    private SgCaracteristicaGymImpl caracteristicaGimnasioService;
    @Inject
    private SgCaracteristicaCocinaImpl caracteristicaCocinaService;
    @Inject
    private SgCaracteristicaSalaJuntaImpl caracteristicaSalaJuntaService;
    @Inject
    private SgCaracteristicaComedorImpl caracteristicaComedorService;
    @Inject
    private SgCaracteristicaSanitarioImpl caracteristicaSanitarioService;
    @Inject
    private SiManejoFechaImpl  manejoFechaService;
    

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgChecklistImpl() {
	super(SgChecklist.class);
    }

    
    public SgChecklist createChecklist(Calendar fechaInicioSemana, Calendar fechaFinSemana, String idUsuario) {
	UtilLog4j.log.info(this, "SgChecklistImpl.createChecklist()");

	UtilLog4j.log.info(this, "Fecha Inicio Semana: " + fechaInicioSemana.getTime());
	UtilLog4j.log.info(this, "Fecha Fin Semana: " + fechaFinSemana.getTime());

	SgChecklist checklist = new SgChecklist();
	checklist.setModificado(Constantes.NO_MODIFICADO);
	checklist.setFechaInicioSemana(fechaInicioSemana.getTime());
	checklist.setFechaFinSemana(fechaFinSemana.getTime());
	checklist.setGenero(new Usuario(idUsuario));
	checklist.setFechaGenero(new Date());
	checklist.setHoraGenero(new Date());
	checklist.setEliminado(Constantes.NO_ELIMINADO);

	try {
	    create(checklist);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Error al crear el checklist");
	    UtilLog4j.log.fatal(this, e.getMessage());
	    return null;
	}
	return checklist;
    }

    
    public List<CheckListDetalleVo> getAllItemsChecklistVO(Object object, boolean status) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgChecklistImpl.getAllItemsChecklistVO()");

	List<CheckListDetalleVo> checklistVOList = new ArrayList<CheckListDetalleVo>();
	CheckListDetalleVo vo = new CheckListDetalleVo();

	if (object instanceof SgStaff) {
	    SgStaff staff = (SgStaff) object;

	    UtilLog4j.log.info(this, "****************************************");
	    UtilLog4j.log.info(this, "Armando Checklist de Staff");
	    UtilLog4j.log.info(this, "****************************************");

	    //Trayendo las Características Generales del Staff
	    UtilLog4j.log.info(this, "Trayendo Características Generales de Staff");
	    SgCaracteristica caracteristicaPrincipal = caracteristicaService.find(1);

	    //Secundarias
	    UtilLog4j.log.info(this, "Añadiendo Características Secundarias de Staff");
	    List<CaracteristicaVo> caracteristicasStaff = caracteristicaStaffService.getAllCaracteristicaStaffByStaffList(staff.getId());

	    //Principal
	    if (caracteristicasStaff != null && caracteristicasStaff.size() > 0) {
		UtilLog4j.log.info(this, "Añadiendo Característica Principal a Staff");
		vo.setIdCaracteristica(caracteristicaPrincipal.getId());
		vo.getCaracteristicaVo().setId(caracteristicaPrincipal.getId());
		vo.getCaracteristicaVo().setNombre(caracteristicaPrincipal.getNombre());
		vo.getCaracteristicaVo().setPrincipal(true);
		vo.setEstado(false);
		checklistVOList.add(vo);
	    }

	    for (CaracteristicaVo carSta : caracteristicasStaff) {
		vo = new CheckListDetalleVo();
		vo.setIdCaracteristica(carSta.getId());
		vo.getCaracteristicaVo().setId(carSta.getId());
		vo.getCaracteristicaVo().setNombre(carSta.getNombre());
		vo.getCaracteristicaVo().setPrincipal(false);
		vo.setEstado(true);
		checklistVOList.add(vo);
	    }

	    //Trayendo las Habitaciones del Staff
	    List<SgStaffHabitacion> habitaciones = habitacionService.getAllHabitacionesByStaff(staff, status);
	    //Trayendo las Características de cada Habitación del Staff y metiéndolas al VO
	    UtilLog4j.log.info(this, "Trayendo Características de Habitaciones");
	    for (SgStaffHabitacion habitacion : habitaciones) {
		vo = new CheckListDetalleVo();

		//Secundarias
		UtilLog4j.log.info(this, "Añadiendo Características Secundarias de Habitación");
		List<CaracteristicaVo> caracteristicasHabitacion = caracteristicaHabitacionService.getAllCaracteristicaHabitacionByHabitacionList(habitacion.getId());

		//Principal
		if (caracteristicasHabitacion != null && caracteristicasHabitacion.size() > 0) {
		    UtilLog4j.log.info(this, "Añadiendo Característica Principal a Habitación");
		    SgCaracteristicaHabitacion caracteristicaPrincipalHabitacion = caracteristicaHabitacionService.getCaracteristicaHabitacionPrincipalByHabitacion(habitacion);
		    vo.getCaracteristicaVo().setNombre(caracteristicaPrincipalHabitacion.getSgCaracteristica().getNombre());
		    vo.setEstado(false);
		    vo.getCaracteristicaVo().setId(caracteristicaPrincipal.getId());
		    vo.getCaracteristicaVo().setPrincipal(true);
		    checklistVOList.add(vo);
		}

		for (CaracteristicaVo carHab : caracteristicasHabitacion) {
		    vo = new CheckListDetalleVo();
		    vo.getCaracteristicaVo().setNombre(carHab.getNombre());
		    vo.setEstado(true);
		    vo.getCaracteristicaVo().setId(carHab.getId());
		    vo.getCaracteristicaVo().setPrincipal(false);
		    checklistVOList.add(vo);
		}
	    }

	    //Trayendo los Gimnasios del Staff
	    List<SgGym> gimnasios = gimnasioService.getAllGimnasiosByStaff(staff, status);
	    //Trayendo las Características de cada Gimnasio del Staff y metiéndolas al VO
	    UtilLog4j.log.info(this, "Trayendo Características de Gimnasios");
	    for (SgGym gimnasio : gimnasios) {
		vo = new CheckListDetalleVo();

		//Secundarias
		UtilLog4j.log.info(this, "Añadiendo Características Secundarias de Gimnasio");
		List<CaracteristicaVo> caracteristicasGimnasio = caracteristicaGimnasioService.getAllCaracteristicaGymByGimnasioList(gimnasio.getId());

		//Principal
		if (caracteristicasGimnasio != null && caracteristicasGimnasio.size() > 0) {
		    UtilLog4j.log.info(this, "Añadiendo Característica Principal de Gimnasio");
		    SgCaracteristicaGym caracteristicaPrincipalGimnasio = caracteristicaGimnasioService.getCaracteristicaGymPrincipalByGimnasio(gimnasio);
		    vo.getCaracteristicaVo().setNombre(caracteristicaPrincipalGimnasio.getSgCaracteristica().getNombre());
		    vo.setEstado(false);
		    vo.getCaracteristicaVo().setId(caracteristicaPrincipal.getId());
		    vo.getCaracteristicaVo().setPrincipal(true);
		    checklistVOList.add(vo);
		}

		for (CaracteristicaVo carGym : caracteristicasGimnasio) {
		    vo = new CheckListDetalleVo();
		    vo.getCaracteristicaVo().setId(carGym.getId());
		    vo.getCaracteristicaVo().setNombre(carGym.getNombre());
		    vo.setEstado(true);
		    checklistVOList.add(vo);
		}
	    }

	    //Trayendo las Cocinas del Staff
	    List<SgCocina> cocinas = cocinaService.getAllCocinasByStaff(staff, status);
	    //Trayendo las Características de cada Cocina del Staff y metiéndolas al VO
	    UtilLog4j.log.info(this, "Trayendo Características de Cocinas");
	    for (SgCocina cocina : cocinas) {
		vo = new CheckListDetalleVo();

		//Secundarias
		UtilLog4j.log.info(this, "Añadiendo Características Secundarias de Cocina");
		List<CaracteristicaVo> caracteristicasCocina = caracteristicaCocinaService.getAllCaracteristicaCocinaByCocinaList(cocina.getId());

		//Principal
		if (caracteristicasCocina != null && caracteristicasCocina.size() > 0) {
		    UtilLog4j.log.info(this, "Añadiendo Característica Principal de Cocina");
		    SgCaracteristicaCocina caracteristicaPrincipalCocina = caracteristicaCocinaService.getCaracteristicaCocinaPrincipalByCocina(cocina);
		    vo.getCaracteristicaVo().setNombre(caracteristicaPrincipalCocina.getSgCaracteristica().getNombre());
		    vo.setEstado(false);
		    vo.getCaracteristicaVo().setId(caracteristicaPrincipal.getId());
		    vo.getCaracteristicaVo().setPrincipal(true);
		    checklistVOList.add(vo);
		}

		for (CaracteristicaVo carCoci : caracteristicasCocina) {
		    vo = new CheckListDetalleVo();
		    vo.getCaracteristicaVo().setId(carCoci.getId());
		    vo.getCaracteristicaVo().setNombre(carCoci.getNombre());
		    vo.setEstado(true);
		    vo.getCaracteristicaVo().setPrincipal(false);
		    checklistVOList.add(vo);
		}
	    }
	} else if (object instanceof OficinaVO) {
	    OficinaVO oficina = (OficinaVO) object;

	    UtilLog4j.log.info(this, "****************************************");
	    UtilLog4j.log.info(this, "Armando Checklist de Oficina");
	    UtilLog4j.log.info(this, "****************************************");

	    //Trayendo las Características Generales del Staff
	    UtilLog4j.log.info(this, "Trayendo Características Generales de Oficina");
	    SgCaracteristica caracteristicaPrincipal = caracteristicaService.find(1);

	    //Secundarias
	    UtilLog4j.log.info(this, "Añadiendo Características Secundarias de Oficina");
	    List<CaracteristicaVo> caracteristicasOficina = caracteristicaOficinaService.getAllCaracteristicaOficinaByOficinaList(oficina.getId());

	    //Principal
	    if (caracteristicasOficina != null && caracteristicasOficina.size() > 0) {
		UtilLog4j.log.info(this, "Añadiendo Característica Principal a Oficina");
		vo.getCaracteristicaVo().setNombre(caracteristicaPrincipal.getNombre());
		vo.setEstado(false);
		vo.getCaracteristicaVo().setId(caracteristicaPrincipal.getId());
		vo.getCaracteristicaVo().setPrincipal(true);
		checklistVOList.add(vo);
	    }

	    for (CaracteristicaVo carOfi : caracteristicasOficina) {
		vo = new CheckListDetalleVo();
		vo.getCaracteristicaVo().setId(carOfi.getId());
		vo.getCaracteristicaVo().setNombre(carOfi.getNombre());
		vo.setEstado(true);
		vo.getCaracteristicaVo().setPrincipal(false);
		checklistVOList.add(vo);
	    }

	    //Trayendo las Salas de Juntas de la Oficina
	    List<SgSalaJunta> salasDeJuntas = salaJuntasService.getAllSalaJuntaByOficinaList(oficina.getId(), status);
	    //Trayendo las Características de cada Sala de Juntas de la Oficina y metiéndolas al VO
	    UtilLog4j.log.info(this, "Trayendo Características de Salas de Junta");
	    for (SgSalaJunta salaJunta : salasDeJuntas) {
		vo = new CheckListDetalleVo();

		//Secundarias
		UtilLog4j.log.info(this, "Añadiendo Características Secundarias de Sala de Junta");
		List<CaracteristicaVo> caracteristicasSalaJunta = caracteristicaSalaJuntaService.getAllCaracteristicaSalaJuntaBySalaJuntaList(salaJunta.getId());

		//Principal
		if (caracteristicasSalaJunta != null && caracteristicasSalaJunta.size() > 0) {
		    UtilLog4j.log.info(this, "Añadiendo Característica Principal a Sala de Junta");
		    SgCaracteristicaSalaJunta caracteristicaPrincipalSalaJunta = caracteristicaSalaJuntaService.getCaracteristicaSalaJuntaPrincipalBySalaJunta(salaJunta);
		    vo.getCaracteristicaVo().setNombre(caracteristicaPrincipalSalaJunta.getSgCaracteristica().getNombre());
		    vo.setEstado(false);
		    vo.getCaracteristicaVo().setId(caracteristicaPrincipal.getId());
		    vo.getCaracteristicaVo().setPrincipal(true);
		    checklistVOList.add(vo);
		}

		for (CaracteristicaVo carSJ : caracteristicasSalaJunta) {
		    vo = new CheckListDetalleVo();
		    vo.getCaracteristicaVo().setId(carSJ.getId());
		    vo.getCaracteristicaVo().setNombre(carSJ.getNombre());
		    vo.setEstado(true);
		    vo.getCaracteristicaVo().setPrincipal(false);
		    checklistVOList.add(vo);
		}
	    }

	    //Trayendo los Comedores de la Oficina
	    List<SgComedor> comedores = comedorService.traerComedorPorOficina(oficina.getId(), status);
	    //Trayendo las Características de cada Comedor de la Oficina y metiéndolas al VO
	    UtilLog4j.log.info(this, "Trayendo Características de Comedor");
	    for (SgComedor comedor : comedores) {
		vo = new CheckListDetalleVo();

		//Secundarias
		UtilLog4j.log.info(this, "Añadiendo Características Secundarias de Comedor");
		List<CaracteristicaVo> caracteristicasComedor = caracteristicaComedorService.getAllCaracteristicaComedorByComedorList(comedor.getId());

		//Principal
		if (caracteristicasComedor != null && caracteristicasComedor.size() > 0) {
		    UtilLog4j.log.info(this, "Añadiendo Característica Principal a Comedor");
		    SgCaracteristicaComedor caracteristicaPrincipalComedor = caracteristicaComedorService.getCaracteristicaComedorPrincipalByComedor(comedor.getId());
		    vo.getCaracteristicaVo().setNombre(caracteristicaPrincipalComedor.getSgCaracteristica().getNombre());
		    vo.setEstado(false);
		    vo.getCaracteristicaVo().setPrincipal(true);
		    checklistVOList.add(vo);
		}

		for (CaracteristicaVo carCom : caracteristicasComedor) {
		    vo = new CheckListDetalleVo();
		    vo.getCaracteristicaVo().setId(carCom.getId());
		    vo.getCaracteristicaVo().setNombre(carCom.getNombre());
		    vo.setEstado(true);
		    vo.getCaracteristicaVo().setPrincipal(false);
		    checklistVOList.add(vo);
		}
	    }

	    //Trayendo los Sanitarios de la Oficina
	    List<SgSanitario> sanitarios = sanitarioService.getAllSanitarioByOficinaList(oficina.getId(), status);
	    //Trayendo las Características de cada Sanitario de la Oficina y metiéndolas al VO
	    UtilLog4j.log.info(this, "Trayendo Características de Sanitario");
	    for (SgSanitario sanitario : sanitarios) {
		vo = new CheckListDetalleVo();

		//Secundarias
		UtilLog4j.log.info(this, "Añadiendo Características Secundarias de Sanitario");
		List<CaracteristicaVo> caracteristicasSanitario = caracteristicaSanitarioService.getAllCaracteristicaSanitarioBySanitarioList(sanitario.getId());

		//Principal
		if (caracteristicasSanitario != null && caracteristicasSanitario.size() > 0) {
		    UtilLog4j.log.info(this, "Añadiendo Característica Principal a Sanitario");
		    SgCaracteristicaSanitario caracteristicaPrincipalSanitario = caracteristicaSanitarioService.getCaracteristicaSanitarioPrincipalBySanitario(sanitario);
		    vo.getCaracteristicaVo().setNombre(caracteristicaPrincipalSanitario.getSgCaracteristica().getNombre());
		    vo.setEstado(false);
		    vo.getCaracteristicaVo().setPrincipal(true);
		    checklistVOList.add(vo);
		}

		for (CaracteristicaVo carSan : caracteristicasSanitario) {
		    vo = new CheckListDetalleVo();
		    vo.getCaracteristicaVo().setId(carSan.getId());
		    vo.getCaracteristicaVo().setNombre(carSan.getNombre());
		    vo.setEstado(true);
		    vo.getCaracteristicaVo().setPrincipal(false);
		    checklistVOList.add(vo);
		}
	    }

	} else if (object instanceof VehiculoVO) {
	    VehiculoVO vehiculo = (VehiculoVO) object;

	    //Trayendo las Características del Vehiculo
	    List<CaracteristicaVo> caracteristicasVehiculo = caracteristicaVehiculoService.getAllCaracteristicaVehiculoByVehiculoList(vehiculo.getId());

	    for (CaracteristicaVo carV : caracteristicasVehiculo) {
		vo = new CheckListDetalleVo();
		vo.getCaracteristicaVo().setId(carV.getId());
		vo.getCaracteristicaVo().setNombre(carV.getNombre());
		vo.setEstado(true);
		vo.getCaracteristicaVo().setPrincipal(false);
		checklistVOList.add(vo);
	    }

	} else {
	    UtilLog4j.log.info(this, "No se pudo encontrar un tipo de objeto compatible para obtener un vo");
	    return null;
	}

	UtilLog4j.log.info(this, "=======================================================================");
	UtilLog4j.log.info(this, "Imprimiendo intento de Checklist (Staff, Oficina o Vehículo)");
	UtilLog4j.log.info(this, "=======================================================================");
	return checklistVOList;
    }

    
    public boolean saveChecklist(Object object, DataModel dataModel, String idUsuario) {
	UtilLog4j.log.info(this, "SgChecklistImpl.saveChecklist()");
	boolean saveChecklistSuccessfull = true;

	UtilLog4j.log.info(this, "Imprimiendo el ChecklistVOModel para saber qué tiene guardado ahora.");
	UtilLog4j.log.info(this, "--------------------------------------------------------------");

	UtilLog4j.log.info(this, "--------------------------------------------------------------");

	if (object instanceof SgStaff) {
	    SgStaff staff = (SgStaff) object;

	    //Guardar Checklist
	    SgChecklist checklist = null;
	    checklist = createChecklist(manejoFechaService.getInicioSemana(), manejoFechaService.getFinSemana(), idUsuario);
	    if (checklist != null) {
		//Guardar relación
		SgStaffChecklist staffChecklist = null;
		staffChecklist = staffChecklistService.createStaffChecklist(staff, checklist, idUsuario);
		if (staffChecklist != null) {
		    //Guardar ítems
		    SgChecklistDetalle item = null;
		    boolean saveItemsSuccessfull = true;
		    for (Iterator iter = dataModel.iterator(); iter.hasNext();) {
//                        UtilLog4j.log.info(this,((ChecklistVO) iter.next()));
			CheckListDetalleVo vo = (CheckListDetalleVo) iter.next();

			if (vo.getCaracteristicaVo().isPrincipal()) { //Si es Principal
			    item = checklistDetalleService.createChecklistDetalle(
				    checklist,
				    vo.getCaracteristicaVo().getId(),
				    false,
				    null,
				    idUsuario);
			} else { //Si No es Principal
			    item = checklistDetalleService.createChecklistDetalle(
				    checklist,
				    vo.getCaracteristicaVo().getId(),
				    (vo.isEstado() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE),
				    vo.getObservacion(),
				    idUsuario);
			}

			if (item == null) {
			    saveItemsSuccessfull = false;
			}
		    }
		    if (!saveItemsSuccessfull) {
			saveChecklistSuccessfull = false;
			//Eliminar StaffChecklist
			staffChecklist.setEliminado(Constantes.ELIMINADO);
			UtilLog4j.log.info(this, "Eliminando StaffChecklist: " + staffChecklist.getId());
			staffChecklistService.edit(staffChecklist);
			//Eliminar Checklist
			checklist.setEliminado(Constantes.ELIMINADO);
			UtilLog4j.log.info(this, "Eliminando Checklist: " + checklist.getId());
			edit(checklist);
		    }
		} else {
		    //Eliminar Checklist
		    saveChecklistSuccessfull = false;
		    checklist.setEliminado(Constantes.ELIMINADO);
		    UtilLog4j.log.info(this, "Eliminando Checklist: " + checklist.getId());
		    edit(checklist);
		}
	    } else {
		saveChecklistSuccessfull = false;
	    }

	    return saveChecklistSuccessfull;
	} else if (object instanceof OficinaVO) {
	    OficinaVO oficina = (OficinaVO) object;

	    //Guardar Checklist
	    SgChecklist checklist = null;
	    checklist = createChecklist(manejoFechaService.getInicioSemana(), manejoFechaService.getFinSemana(), idUsuario);
	    if (checklist != null) {
		//Guardar relación
		SgOficinaChecklist oficinaChecklist = null;
		oficinaChecklist = oficinaChecklistService.createOficinaChecklist(oficina.getId(), checklist, idUsuario);
		if (oficinaChecklist != null) {
		    //Guardar ítems
		    SgChecklistDetalle item = null;
		    boolean saveItemsSuccessfull = true;
		    for (Iterator iter = dataModel.iterator(); iter.hasNext();) {
//                        UtilLog4j.log.info(this,((ChecklistVO) iter.next()));
			CheckListDetalleVo vo = (CheckListDetalleVo) iter.next();

			if (vo.getCaracteristicaVo().isPrincipal()) { //Si es Principal
			    item = checklistDetalleService.createChecklistDetalle(
				    checklist,
				    vo.getCaracteristicaVo().getId(),
				    false,
				    null,
				    idUsuario);
			} else { //Si No es Principal
			    item = checklistDetalleService.createChecklistDetalle(
				    checklist,
				    vo.getCaracteristicaVo().getId(),
				    (vo.isEstado() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE),
				    vo.getObservacion(),
				    idUsuario);
			}

			if (item == null) {
			    UtilLog4j.log.info(this, "Un ítem no se creó");
			    saveItemsSuccessfull = false;
			}
		    }
		    if (!saveItemsSuccessfull) {
			saveChecklistSuccessfull = false;
			//Eliminar StaffChecklist
			oficinaChecklist.setEliminado(Constantes.ELIMINADO);
			UtilLog4j.log.info(this, "Eliminando OficinaChecklist: " + oficinaChecklist.getId());
			oficinaChecklistService.edit(oficinaChecklist);
			//Eliminar Checklist
			checklist.setEliminado(Constantes.ELIMINADO);
			UtilLog4j.log.info(this, "Eliminando Checklist: " + checklist.getId());
			edit(checklist);
		    }
		} else {
		    //Eliminar Checklist
		    saveChecklistSuccessfull = false;
		    checklist.setEliminado(Constantes.ELIMINADO);
		    UtilLog4j.log.info(this, "Eliminando Checklist: " + checklist.getId());
		    edit(checklist);
		}
	    } else {
		saveChecklistSuccessfull = false;
	    }

	    return saveChecklistSuccessfull;
	} else {
	    UtilLog4j.log.info(this, "No se encontró un Objeto coincidente para guardar el Checklist");
	    return !saveChecklistSuccessfull;
	}
    }

    
    public boolean updateChecklist(SgChecklist checklist, DataModel dataModel, String idUsuario) {
	UtilLog4j.log.info(this, "SgChecklistImpl.updateChecklist()");
	boolean updateChecklistSuccessfull = true;

	//Sólo se actualizan los elementos del VO. El VO lleva dentro el ítem (SgStaffDetalle). A este solo hay que actualizarle estado y observación por si han cambiado
	for (Iterator iter = dataModel.iterator(); iter.hasNext();) {
	    CheckListDetalleVo vo = (CheckListDetalleVo) iter.next();
	    if (!vo.getCaracteristicaVo().isPrincipal()) {
		vo.setEstado((vo.isEstado()));
		vo.setObservacion(vo.getObservacion());
		//Actualizar el Detalle
		if (!checklistDetalleService.editChecklistDetalle(vo, idUsuario, Constantes.NO_ELIMINADO)) {
		    updateChecklistSuccessfull = false;
		}
	    }
	}
	if (updateChecklistSuccessfull) {
	    //Actualizar el Checklist para cambiar su campo 'modificado' a true
	    try {
		checklist.setModificado(Constantes.MODIFICADO);
		checklist.setModifico(new Usuario(idUsuario));
		checklist.setFechaModifico(new Date());
		checklist.setHoraModifico(new Date());
		edit(checklist);
	    } catch (Exception e) {
		UtilLog4j.log.info(this, "Error al actualizar el checklist");
		UtilLog4j.log.info(this, e.getMessage());
		e.printStackTrace();
		return !updateChecklistSuccessfull;
	    }
	}
	return updateChecklistSuccessfull;
    }

    
    public List<SgStaffChecklist> getAllChecklistsByStaffAndStatusList(int staff, boolean status) {
	UtilLog4j.log.info(this, "SgChecklistImpl.getAllChecklistsByStaffAndStatusList()");
	return staffChecklistService.getAllStaffChecklistByStaffAndStatusList(staff, status);
    }

    
    public List<SgOficinaChecklist> getAllChecklistsByOficinaAndStatusList(int oficina, boolean status) {
	UtilLog4j.log.info(this, "SgChecklistImpl.getAllChecklistsByOficinaAndStatusList()");
	return oficinaChecklistService.getAllOficinaChecklistByOficinaAndStatusList(oficina, status);
    }

    
    public List<SgVehiculoChecklist> getAllChecklistByVehiculoList(int vehiculo, boolean eliminado) throws SIAException, Exception {
	return vehiculoChecklistService.getAllChecklistByVehiculoList(vehiculo, eliminado);
    }

    
    public SgStaffChecklist getThisWeekChecklistStaff(SgStaff staff) {
	UtilLog4j.log.info(this, "SgChecklistImpl.getThisWeekChecklistStaff()");
	return staffChecklistService.getThisWeekStaffChecklist(staff, manejoFechaService.getInicioSemana(), manejoFechaService.getFinSemana());
    }

    
    public SgOficinaChecklist getThisWeekChecklistOficina(int oficina) {
	UtilLog4j.log.info(this, "SgChecklistImpl.getThisWeekChecklistStaff()");
	return oficinaChecklistService.getThisWeekOficinaChecklist(oficina, manejoFechaService.getInicioSemana(), manejoFechaService.getFinSemana());
    }

    
    public List<CheckListDetalleVo> getChecklistVOItemsByChecklist(SgChecklist checklist) {
	UtilLog4j.log.info(this, "SgChecklistImpl.getChecklistVOItemsByChecklist()");
	List<CheckListDetalleVo> itemsChecklistDetalle = checklistDetalleService.getAllItemsChecklistList(checklist.getId(), Constantes.NO_ELIMINADO);

	return itemsChecklistDetalle;
    }

    /**
     * Devuelve 'true' si el Checklist fue creado en esta semana. Cao contrario
     * devuelve 'false'
     *
     * @param fechaCreacion
     * @return
     */
    public boolean theChecklistWasCreatedThisWeek(Calendar fechaCreacion) {
	return manejoFechaService.belongsToDateThisWeek(fechaCreacion, true);
    }

    
    public boolean iCanChangeTheChecklist(Object object) throws Exception {
	UtilLog4j.log.info(this, "SgChecklistImpl.theChecklistWasCreatedThisWeek()");
	if (object instanceof SgStaffChecklist) {
	    SgStaffChecklist staffChecklist = (SgStaffChecklist) object;
	    UtilLog4j.log.info(this, "SgChecklist modificado: " + staffChecklist.getSgChecklist().isModificado());
	    if (staffChecklist.getSgChecklist().isModificado()) { //Si su campo 'modificado'=true ya no puede ser modificado
		return false;
	    } else { //Si el campo 'modificado'=false
		Calendar calendarFechaCreacion = Calendar.getInstance();
		calendarFechaCreacion.setTime(staffChecklist.getSgChecklist().getFechaGenero());
		return theChecklistWasCreatedThisWeek(calendarFechaCreacion);
	    }
	} else if (object instanceof SgOficinaChecklist) {
	    SgOficinaChecklist oficinaChecklist = (SgOficinaChecklist) object;
	    if (oficinaChecklist.getSgChecklist().isModificado()) { //Si su campo 'modificado'=true ya no puede ser modificado
		return false;
	    } else { //Si el campo 'modificado'=false
		Calendar calendarFechaCreacion = Calendar.getInstance();
		calendarFechaCreacion.setTime(oficinaChecklist.getSgChecklist().getFechaGenero());
		return theChecklistWasCreatedThisWeek(calendarFechaCreacion);
	    }
	} else {
	    throw new Exception("No se encontró un Objeto coincidente para poder validar si puedo cambiar el Checklist");
	}
    }

    
    public boolean deleteChecklist(SgChecklist checklist, String idUsuario) {
	UtilLog4j.log.info(this, "SgChecklistImpl.deleteChecklist()");
	boolean deleteSuccessfull = true;

	checklist.setGenero(new Usuario(idUsuario));
	checklist.setFechaGenero(new Date());
	checklist.setHoraGenero(new Date());
	checklist.setEliminado(Constantes.ELIMINADO);

	try {
	    edit(checklist);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Ocurrió un error al eliminar el Checklist: " + checklist.getId());
	    UtilLog4j.log.fatal(this, e.getMessage());
	    return !deleteSuccessfull;
	}
	return deleteSuccessfull;
    }
}
