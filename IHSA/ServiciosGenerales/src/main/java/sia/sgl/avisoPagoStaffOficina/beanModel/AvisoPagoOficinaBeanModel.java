/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.avisoPagoStaffOficina.beanModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import sia.constantes.Constantes;
import sia.modelo.SgAvisoPago;
import sia.modelo.SgAvisoPagoOficina;
import sia.modelo.SgOficina;
import sia.modelo.SgPeriodicidad;
import sia.modelo.SgTipoTipoEspecifico;
import sia.servicios.sgl.impl.SgAvisoPagoImpl;
import sia.servicios.sgl.impl.SgAvisoPagoOficinaImpl;
import sia.servicios.sgl.impl.SgAvisoPagoStaffImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgPeriodicidadImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Named(value = "avisoPagoOficinaBeanModel")

public class AvisoPagoOficinaBeanModel implements Serializable {

    @Inject
    private Sesion sesion;
    //
    @Inject
    private SgOficinaImpl oficinaService;
    @Inject
    private SgAvisoPagoOficinaImpl sgAvisoPagoOficinaService;
    @Inject
    private SgTipoTipoEspecificoImpl sgTipoTipoEspecificoImpl;
    @Inject
    private SgPeriodicidadImpl sgPeriodicidadService;
    @Inject
    private SgAvisoPagoImpl sgAvisoPagoService;
    @Inject
    private SiManejoFechaImpl siManejoFechaService;
    @Inject
    private SgAvisoPagoStaffImpl sgAvisoPagoStaffServicice;
    //variables
    private boolean mrMostrarPanelAgregarNuevoAviso = false;
    private boolean mrPopupModificarAviso = false;
    private boolean mrPopupCatalogoAvisos = false;
    private boolean mrMostrarPanelCatalogo = false;
    private int idPagoOficinaSeleccionado;
    private int idPeriodicidadSeleccionada;
    private int idTipoEspecificoSeleccionado;
    private SgAvisoPago sgAvisoPago;
    private int diaAviso;
    private DataModel avisosPagosOficinaModel;
    private DataModel avisosPagosCatalogoModel;
    private List<SelectItem> listaOficinaItems;
    private List<SelectItem> listaPagosOficinaItems;
    private List<SelectItem> listaPeriodosPagoItems;
    private SgOficina sgOficinaSeleccionada;
    private SgPeriodicidad sgPeriodicidad;
    private Date fechaActual;
    private Date fechaPago;
    private Date fechaRealPago;
    private Date fechaAviso;
    private Date fechaRealAviso;
    SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Creates a new instance of AvisoPagoOficinaBeanModel
     */
    public AvisoPagoOficinaBeanModel() {
    }

    @PostConstruct
    public void beginConversationAvisoPago() {
	traerAllAvisosToOficina();
    }

    public void traerOficinas() {
	UtilLog4j.log.info(this, "AvisoPagoOficinafBean.trarOficinas");
	try {
	    this.setSgOficinaSeleccionada(this.oficinaService.find(sesion.getOficinaActual().getId()));
	} catch (Exception e) {
	    UtilLog4j.log.info(this, e.getMessage());
	}
    }

    public void traerAllAvisosToOficina() {
	UtilLog4j.log.info(this, "traerAllAvisosToOficina");
	try {
	    ListDataModel<SgAvisoPago> avisosModel = new ListDataModel(sgAvisoPagoOficinaService.findAllAvisoPagoToOficina(sesion.getOficinaActual().getId()));
	    this.setAvisosPagosOficinaModel((DataModel) avisosModel);
	    UtilLog4j.log.info(this, "Datamodel de avisos asignado " + avisosModel.getRowCount());
	} catch (Exception e) {
	    UtilLog4j.log.info(this, e.getMessage());
	}
    }

    public void traerCatalogoAvisos() {
	UtilLog4j.log.info(this, "AvisoPagoOficinaBeanModel.traerCatalogoAvisos");
	try {
	    UtilLog4j.log.info(this, "idStaffSeleccionado " + getIdPagoOficinaSeleccionado());
	    ListDataModel<SgAvisoPago> avisosModel = new ListDataModel(sgAvisoPagoOficinaService.findAllCatalogoAvisos(sesion.getOficinaActual().getId()));
	    this.setAvisosPagosCatalogoModel((DataModel) avisosModel);
	    UtilLog4j.log.info(this, "Datamodel de catalogo de avisos asignado " + avisosModel.getRowCount());
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion " + e.getMessage());
	}
    }

    public void traerOficinaItems() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	List<SgOficina> lo;
	try {
	    UtilLog4j.log.info(this, "AvisoPAgoStaffBeanMode.traerStaffItems");
	    //Tipo Especifico
	    lo = oficinaService.traerOficina(null, Constantes.BOOLEAN_FALSE, Constantes.BOOLEAN_TRUE);
	    for (SgOficina of : lo) {
		SelectItem item = new SelectItem(of.getId(), of.getNombre());
		l.add(item);
	    }
	    setListaOficinaItems(l);
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Ocurrio un error en la consulta de Staff Items'");
	}
    }

    public void traerPagosPorTipoEspecificoToOficinaItems() {
	UtilLog4j.log.info(this, "traerPagosPorTipoEspecificoToOficinaItems");
	if (sesion.getOficinaActual().getId() > 0) {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    List<SgTipoTipoEspecifico> lt;
	    try {                                               //Tipo especifico para oficina = 2
		lt = sgTipoTipoEspecificoImpl.traerPorIdTipoAvisoStaff(2, sesion.getOficinaActual().getId(), Constantes.BOOLEAN_FALSE);
		UtilLog4j.log.info(this, "la lista de tipos especificos tiene " + lt.size());
		for (SgTipoTipoEspecifico tipoEsp : lt) {
		    UtilLog4j.log.info(this, " tipo " + tipoEsp.getSgTipoEspecifico().getNombre());
		    SelectItem item = new SelectItem(tipoEsp.getSgTipoEspecifico().getId(), tipoEsp.getSgTipoEspecifico().getNombre());
		    l.add(item);
		}
		setListaPagosOficinaItems(l);
	    } catch (Exception e) {
		UtilLog4j.log.info(this, "Aqui en la excepción");
	    }
	} else {
	    setListaPagosOficinaItems(null);
	}
    }

    public void traerPeriodosItems() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	List<SgPeriodicidad> lp;
	try {
	    UtilLog4j.log.info(this, "AvisoPAgoStaffBeanMode.traerPeriodosItems");
	    lp = sgPeriodicidadService.findAllPeriodos();
	    for (SgPeriodicidad p : lp) {
		UtilLog4j.log.info(this, "periodos " + p.getNombre());
		SelectItem item = new SelectItem(p.getId(), p.getNombre());
		l.add(item);
	    }
	    setListaPeriodosPagoItems(l);
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Ocurrio un error en la consulta de Periodos'");
	}
    }

    public void createAvisoPago() {
	UtilLog4j.log.info(this, "AvisoPAgoStaffBeanMode.createAvisoPago");
	SgAvisoPago avisoPagoEncontrado = null;
	SgAvisoPago avisoP = null;
	SgAvisoPagoOficina relacionEncontrada = null;
	try {
	    if (getIdPagoOficinaSeleccionado() > 0 && getIdPeriodicidadSeleccionada() > 0 && sesion.getOficinaActual().getId() > 0 && getSgAvisoPago() != null) {
		UtilLog4j.log.info(this, "Todo preparado para insetar..");
		avisoPagoEncontrado = sgAvisoPagoService.findSgAvisoPagoRepetido(getIdPagoOficinaSeleccionado());

		if (avisoPagoEncontrado == null) {
		    UtilLog4j.log.info(this, "el aviso no esta en la tabla , crear aviso y crear relacion..");
		    calcularFechas();
		    getSgAvisoPago().setFechaProximoAviso(fechaAviso);
		    avisoP = sgAvisoPagoService.createAvisoPago(getSgAvisoPago(),
			    0,
			    getIdPeriodicidadSeleccionada(),
			    getIdPagoOficinaSeleccionado(),
			    sesion.getUsuario());
		    sgAvisoPagoOficinaService.createRelacionAvisoPagoOficina(avisoP, sesion.getOficinaActual().getId(), sesion.getUsuario());
		    sgAvisoPagoService.ponerComoTipoEspecificoUsado(avisoP, sesion.getUsuario());
		    traerAllAvisosToOficina();
		} else {
		    UtilLog4j.log.info(this, "el aviso ya esta en la tabla.. buscar que no se repita");
		    relacionEncontrada = sgAvisoPagoOficinaService.findSgAvisoPagoRepetidoRelacionParaOficina(sesion.getOficinaActual().getId(), avisoPagoEncontrado.getId(), Constantes.BOOLEAN_FALSE);
		    if (relacionEncontrada == null) {
			UtilLog4j.log.info(this, "la realacion no se encontro se tiene que crear..");

			sgAvisoPagoOficinaService.createRelacionAvisoPagoOficina(avisoPagoEncontrado, sesion.getOficinaActual().getId(), sesion.getUsuario());
			UtilLog4j.log.info(this, "se creo la relacion.");
			traerAllAvisosToOficina();
		    }
		}
	    }

	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Ocurrio un error en la consulta de Periodos'");
	}
    }

    public void crearAviso() {
	calcularFechas();
	getSgAvisoPago().setFechaProximoAviso(fechaAviso);
	sgAvisoPagoService.createAvisoPago(getSgAvisoPago(),
		sesion.getOficinaActual().getId(),
		getIdPeriodicidadSeleccionada(),
		getIdPagoOficinaSeleccionado(),
		sesion.getUsuario());
    }

    public void createRelacionOficina() {
	UtilLog4j.log.info(this, "create realcion");
	sgAvisoPagoOficinaService.createRelacionAvisoPagoOficina(getSgAvisoPago(), sesion.getOficinaActual().getId(), sesion.getUsuario());
	UtilLog4j.log.info(this, "Solo se creo la relacion");

    }

    public void createOficinaAndRelacion() {
	UtilLog4j.log.info(this, "createOficinaAndRelacion");
	UtilLog4j.log.info(this, "periodicidad " + getSgAvisoPago().getSgPeriodicidad().getNombre());
	UtilLog4j.log.info(this, "Pago " + getSgAvisoPago().getSgTipoEspecifico().getNombre());

	sgAvisoPagoOficinaService.createRelacionAvisoPagoOficina(getSgAvisoPago(), sesion.getOficinaActual().getId(), sesion.getUsuario());
	UtilLog4j.log.info(this, "Se creo la oficina y la relacion");
    }

    public boolean createAvisoBuscandoEnEliminados() {
	UtilLog4j.log.info(this, "createAvisoEnEliminados");
	if (sgAvisoPagoService.createSgAvisoPagoRepetidoAtributosOficina(sesion.getOficinaActual().getId(), getIdTipoEspecificoSeleccionado(), getIdPeriodicidadSeleccionada(), sgAvisoPago.getDiaEstimadoPago(), getSgAvisoPago().getDiaEstimadoPago(), sesion.getUsuario()) != null) {
	    UtilLog4j.log.info(this, "se creo la relacion");
	    return true;
	} else {
	    return false;
	}
    }

    public void eliminarRelacion() {
	UtilLog4j.log.info(this, "create realcion");

	UtilLog4j.log.info(this, "Solo se creo la relacion");

    }

    public void editAvisoPago() {
	UtilLog4j.log.info(this, "AvisoPagoOficinaBeanMode.editAvisoPago");
	SgAvisoPago avisoPagoEncontrado = null;
	SgAvisoPago avisoP = null;
	SgAvisoPagoOficina relacionEncontrada = null;
	int count = 0;
	try {
	    if (getIdPagoOficinaSeleccionado() > 0 && getIdPeriodicidadSeleccionada() > 0 && sesion.getOficinaActual().getId() > 0 && getSgAvisoPago() != null) {
		UtilLog4j.log.info(this, "Todo preparado para modificar el aviso..");
                //Calcular fechas

		//buscar el registro en la tabla de SgAvisos, con todos sus atributos
		avisoPagoEncontrado = sgAvisoPagoService.findSgAvisoPagoRepetidoAtributos(getIdTipoEspecificoSeleccionado(), getIdPeriodicidadSeleccionada(), getSgAvisoPago().getDiaAnticipadoPago(), getSgAvisoPago().getDiaEstimadoPago());
		if (avisoPagoEncontrado != null) {
		    UtilLog4j.log.info(this, "Ya existe un registro con esas especificaciones..");
		    UtilLog4j.log.info(this, "Se procede a saber si esta siendo utilizado por mas de un usuario..");
		    if (sgAvisoPagoOficinaService.findCoutSgAvisoPagoRepetidoRelacion(avisoPagoEncontrado.getId()) > 1) {

			UtilLog4j.log.info(this, "El aviso esta siendo utilizado por mas de un usuario, no se puede modificar el registro de la tabla sgAvisoPago..");
			UtilLog4j.log.info(this, "Saber si el oficina seleccionado es quien lo esta utilizando");
			relacionEncontrada = sgAvisoPagoOficinaService.findSgAvisoPagoRepetidoRelacionParaOficina(sesion.getOficinaActual().getId(), avisoPagoEncontrado.getId(), Constantes.BOOLEAN_FALSE);
			if (relacionEncontrada != null) {
			    UtilLog4j.log.info(this, "ya existe una relación con esta oficina..se anula la operación");
			    //ya existe un registro con las mismas especificaciones relacionado en con esta oficina
//                            calcularFechas();
//                            getSgAvisoPago().setFechaProximoAviso(fechaAviso);
//                            sgAvisoPagoService.editAvisoPagoOficina(getSgAvisoPago(),
//                                    getIdPeriodicidadSeleecionada(),
//                                    getIdTipoEspecificoSeleccionado(),
//                                    sesion.getUsuario());

			} else {
			    UtilLog4j.log.info(this, "no existe la relacion con este oficina..se creara una nueva");
			    sgAvisoPagoOficinaService.createRelacionAvisoPagoOficina(avisoPagoEncontrado, sesion.getOficinaActual().getId(), sesion.getUsuario());
			    UtilLog4j.log.info(this, "Se creo la relacion " + avisoPagoEncontrado.getSgTipoEspecifico().getNombre() + " y el staff id " + getIdPagoOficinaSeleccionado());
			    UtilLog4j.log.info(this, "se elimina la relacion antigua ");
			    //sgAvisoPagoOficinaService.deletePorAvisoAndOficina(this.sgAvisoPagoOficinaService.findSgAvisoPagoRepetidoRelacionParaOficina(sesion.getOficinaActual().getId(), getSgAvisoPago().getId()), sesion.getUsuario());
			    sgAvisoPagoOficinaService.deleteRelacionAvisoOficina(getSgAvisoPago().getId(), sesion.getOficinaActual().getId(), sesion.getUsuario());
			    UtilLog4j.log.info(this, "se elimino la relacion ");

			}
		    } else {
			//modificar el registro en SgAvisoPago
			UtilLog4j.log.info(this, "El aviso no esta siendo utilizado por mas de un usuario en la tabla SgAvisoPago ");
			calcularFechas();
			getSgAvisoPago().setFechaProximoAviso(getFechaAviso());
			sgAvisoPagoService.editAvisoPago(getSgAvisoPago(),
				getIdPeriodicidadSeleccionada(),
				getIdTipoEspecificoSeleccionado(),
				sesion.getUsuario());
		    }
		} else {
		    count = sgAvisoPagoOficinaService.findCoutSgAvisoPagoRepetidoRelacion(getSgAvisoPago().getId());
		    if (count == 1) {
			UtilLog4j.log.info(this, " se repite una vez el se modifica en sgAviso ");
			calcularFechas();
			getSgAvisoPago().setFechaProximoAviso(fechaAviso);
			sgAvisoPagoService.editAvisoPago(getSgAvisoPago(),
				getIdPeriodicidadSeleccionada(),
				getIdTipoEspecificoSeleccionado(),
				sesion.getUsuario());
			UtilLog4j.log.info(this, "se creo solo la relacion ");
		    } else {

			UtilLog4j.log.info(this, "El aviso no esta en la tabla de SgAvisos, se creará..");
			calcularFechas();
			SgAvisoPago avisoTemp = new SgAvisoPago();
			avisoTemp.setFechaProximoAviso(getFechaAviso());
			avisoTemp.setDiaAnticipadoPago(getSgAvisoPago().getDiaAnticipadoPago());
			avisoTemp.setDiaEstimadoPago(getSgAvisoPago().getDiaEstimadoPago());
//                        UtilLog4j.log.info(this, "Fechha de aviso .." + formateador.format(getFechaAviso()));

			avisoP = sgAvisoPagoService.createAvisoPago(avisoTemp,
				sesion.getOficinaActual().getId(),
				getIdPeriodicidadSeleccionada(),
				getIdPagoOficinaSeleccionado(),
				sesion.getUsuario());
			UtilLog4j.log.info(this, "El aviso se creo satisfactoriamente..Se creara una nueva relación");
			//SgAvisoPagoStaff nuevaRelacion = new SgAvisoPagoStaff();
			//nuevaRelacion.setSgAvisoPago(avisoPagoEncontrado);
			sgAvisoPagoOficinaService.createRelacionAvisoPagoOficina(avisoP, sesion.getOficinaActual().getId(), sesion.getUsuario());
			UtilLog4j.log.info(this, "Se creo satisfactoriamente la relacion");
			UtilLog4j.log.info(this, "Se eliminara  la relacion pasada");
			// sgAvisoPagoOficinaService.deleteRelacionAvisoOficina(getSgAvisoPago().getId(), sesion.getOficinaActual().getId(), sesion.getUsuario());
			//sgAvisoPagoService.deleteAviso(avisoPagoEncontrado,sesion.getUsuario());
			this.deteleRelacionAvisoPago();
			UtilLog4j.log.info(this, "Relacion eliminada");

		    }
		}
		traerAllAvisosToOficina();
	    }

	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion " + e.getMessage());
	    UtilLog4j.log.info(this, "Ocurrio un error en la modificacion");
	}
    }

    public void deteleRelacionAvisoPago() {
	UtilLog4j.log.info(this, "AvisoPAgoStaffBeanMode.deteleAvisoPago");
	try {
	    if (getSgAvisoPago() != null) {
		if (sgAvisoPagoOficinaService.findCoutSgAvisoPagoRepetidoRelacion(getSgAvisoPago().getId())
			+ sgAvisoPagoStaffServicice.findCoutSgAvisoPagoRepetidoRelacion(getSgAvisoPago().getId()) == 1) {
		    //eliminar aviso
		    sgAvisoPagoService.deleteAviso(getSgAvisoPago(), sesion.getUsuario());
		}
		UtilLog4j.log.info(this, "Todo preparado para eliminar el aviso..");
		this.sgAvisoPagoService.deletePorAvisoAndOficina(getSgAvisoPago(), sesion.getOficinaActual().getId(), sesion.getUsuario());
		traerAllAvisosToOficina();
		UtilLog4j.log.info(this, "Se eliminó la relacion..");
	    }

	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Ocurrio un error en la eliminación'");
	}
    }

    /**
     * *
     *
     * Calculo de fechas
     */
    public boolean calcularFechas() {
	try {
	    this.setFechaActual(new Date());
	    UtilLog4j.log.info(this, "Calcular fechas");
	    this.setFechaPago(siManejoFechaService.componerFechaApartirDeDia(getFechaActual(), sgAvisoPago.getDiaEstimadoPago()));
	    this.setFechaAviso(siManejoFechaService.fechaRestarDias(getFechaPago(), getSgAvisoPago().getDiaAnticipadoPago()));

	    //Calcular el ultimo dia del mes que sea
	    // Calendar cal = GregorianCalendar.getInstance();
	    // UtilLog4j.log.info(this, "Último día de este mes: " + cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
	    UtilLog4j.log.info(this, "Fecha Actual " + formateador.format(getFechaActual()));
	    UtilLog4j.log.info(this, "Fecha de pago " + formateador.format(getFechaPago()));
	    UtilLog4j.log.info(this, "Fecha de Aviso " + formateador.format(getFechaAviso()));
	    UtilLog4j.log.info(this, "Consultar Periodicidad " + getIdPeriodicidadSeleccionada());
	    setSgPeriodicidad(sgPeriodicidadService.find(getIdPeriodicidadSeleccionada()));

	    this.setFechaAviso(siManejoFechaService.fechaSumarMes(getFechaAviso(), getSgPeriodicidad().getMes()));
	    UtilLog4j.log.info(this, "Fecha de Aviso real " + formateador.format(getFechaAviso()));

	    this.setFechaPago(siManejoFechaService.fechaSumarMes(getFechaPago(), getSgPeriodicidad().getMes()));
	    UtilLog4j.log.info(this, "Fecha de Pago real " + formateador.format(getFechaPago()));

	    while (getFechaActual().compareTo(getFechaAviso()) > 0) {
		UtilLog4j.log.info(this, "La fechaActual es mayor la fecha de aviso ...Sumar periodicidad a la fecha de aviso " + getSgPeriodicidad().getMes());
		this.setFechaAviso(siManejoFechaService.fechaSumarMes(getFechaAviso(), getSgPeriodicidad().getMes()));
		//this.fechaAviso = siManejoFechaService.fechaSumarMes(fechaAviso,5);
		UtilLog4j.log.info(this, "Nueva fecha de aviso con el mes incluido " + formateador.format(getFechaAviso()));
		//compara si esta en el mismo mes...SI: Sumar nuevamente la periodiciada ; No: Guardar
	    }

	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion : " + e.getMessage());
	    UtilLog4j.log.info(this, "Ocurrio un error en la consulta de Periodos'");
	}
	return true;
    }

    public boolean buscarAvisoSeleccion() {
	UtilLog4j.log.info(this, "getidStaff" + sesion.getOficinaActual().getId());
	UtilLog4j.log.info(this, "tipo especifico " + getSgAvisoPago().getSgTipoEspecifico().getNombre());

	boolean ret = false;
	//if(sgAvisoPagoStaffService.findSgAvisoPagoRepetidoRelacionParaOficina(getIdStaffSeleccionado(),getSgAvisoPago().getId())!=null)
	if (sgAvisoPagoOficinaService.findSgAvisoPagoRepetidoRelacionParaOficina(sesion.getOficinaActual().getId(), getSgAvisoPago().getSgTipoEspecifico().getId(), Constantes.BOOLEAN_FALSE) != null) {
	    ret = true;
	} else {
	    ret = false;
	}
	return ret;
    }

    public boolean buscarAvisoCreate() {
	boolean ret = false;
	UtilLog4j.log.info(this, "idTipo " + getIdPagoOficinaSeleccionado());
	UtilLog4j.log.info(this, "id Periodicidad " + getIdPeriodicidadSeleccionada());
	UtilLog4j.log.info(this, "dia estimado " + getSgAvisoPago().getDiaEstimadoPago());
	UtilLog4j.log.info(this, "Dia anticipado " + getSgAvisoPago().getDiaAnticipadoPago());

	SgAvisoPago aviso = sgAvisoPagoService.findSgAvisoPagoRepetidoAtributos(getIdPagoOficinaSeleccionado(), getIdPeriodicidadSeleccionada(), getSgAvisoPago().getDiaEstimadoPago(), getSgAvisoPago().getDiaAnticipadoPago());
	if (aviso != null) {
	    UtilLog4j.log.info(this, "se encontró el aviso");
	    //buscar relacion
	    if (sgAvisoPagoOficinaService.findSgAvisoPagoRepetidoRelacionParaOficina(sesion.getOficinaActual().getId(), aviso.getId(), Constantes.BOOLEAN_FALSE) != null) {
		UtilLog4j.log.info(this, "el aviso se encontro en la relacion");
		ret = true;
	    } else {
		ret = false;
	    }
	} else {
	    UtilLog4j.log.info(this, "NO se encontró el aviso");
	    ret = false;
	}

	return ret;
    }

    public boolean buscarPagoRepetido() {
	UtilLog4j.log.info(this, "idPago (Tipo especifico) " + getIdPagoOficinaSeleccionado());
	UtilLog4j.log.info(this, "id Periodicidad " + getIdPagoOficinaSeleccionado());
	boolean ret = false;
	//Comproabar si ya esta como serviio
	SgAvisoPagoOficina vs = sgAvisoPagoOficinaService.findAvisoPagoRepetido(sesion.getOficinaActual().getId(), getIdPagoOficinaSeleccionado());
	if (vs != null) {
	    setSgAvisoPago(vs.getSgAvisoPago());
	    ret = true;
	} else {
	    ret = false;
	}
	return ret;
    }

    public boolean buscarPagoRepetidoAtributos() {
	UtilLog4j.log.info(this, "idPago (Tipo especifico) " + getIdPagoOficinaSeleccionado());
	UtilLog4j.log.info(this, "id Periodicidad " + getIdPagoOficinaSeleccionado());
	boolean ret = false;
	SgAvisoPago avisoEncontrado = sgAvisoPagoService.findSgAvisoPagoRepetidoAtributos(getIdPagoOficinaSeleccionado(), getIdPeriodicidadSeleccionada(), getSgAvisoPago().getDiaEstimadoPago(), getSgAvisoPago().getDiaAnticipadoPago());

	if (avisoEncontrado != null) {
	    ret = true;
	} else {
	    ret = false;
	}
	return ret;
    }

    /**
     * @return the listaOficinaItems
     */
    public List<SelectItem> getListaOficinaItems() {
	return listaOficinaItems;
    }

    /**
     * @param listaOficinaItems the listaOficinaItems to set
     */
    public void setListaOficinaItems(List<SelectItem> listaOficinaItems) {
	this.listaOficinaItems = listaOficinaItems;
    }

    /**
     * @return the avisosPagosOficinaModel
     */
    public DataModel getAvisosPagosOficinaModel() {
	return avisosPagosOficinaModel;
    }

    /**
     * @param avisosPagosOficinaModel the avisosPagosOficinaModel to set
     */
    public void setAvisosPagosOficinaModel(DataModel avisosPagosOficinaModel) {
	this.avisosPagosOficinaModel = avisosPagosOficinaModel;
    }

    /**
     * @return the idPagoOficinaSeleccionado
     */
    public int getIdPagoOficinaSeleccionado() {
	return idPagoOficinaSeleccionado;
    }

    /**
     * @param idPagoOficinaSeleccionado the idPagoOficinaSeleccionado to set
     */
    public void setIdPagoOficinaSeleccionado(int idPagoOficinaSeleccionado) {
	this.idPagoOficinaSeleccionado = idPagoOficinaSeleccionado;
    }

    /**
     * @return the avisosPagosCatalogoModel
     */
    public DataModel getAvisosPagosCatalogoModel() {
	return avisosPagosCatalogoModel;
    }

    /**
     * @param avisosPagosCatalogoModel the avisosPagosCatalogoModel to set
     */
    public void setAvisosPagosCatalogoModel(DataModel avisosPagosCatalogoModel) {
	this.avisosPagosCatalogoModel = avisosPagosCatalogoModel;
    }

    /**
     * @return the listaPagosOficinaItems
     */
    public List<SelectItem> getListaPagosOficinaItems() {
	return listaPagosOficinaItems;
    }

    /**
     * @param listaPagosOficinaItems the listaPagosOficinaItems to set
     */
    public void setListaPagosOficinaItems(List<SelectItem> listaPagosOficinaItems) {
	this.listaPagosOficinaItems = listaPagosOficinaItems;
    }

    /**
     * @return the listaPeriodosPagoItems
     */
    public List<SelectItem> getListaPeriodosPagoItems() {
	return listaPeriodosPagoItems;
    }

    /**
     * @param listaPeriodosPagoItems the listaPeriodosPagoItems to set
     */
    public void setListaPeriodosPagoItems(List<SelectItem> listaPeriodosPagoItems) {
	this.listaPeriodosPagoItems = listaPeriodosPagoItems;
    }

    /**
     * @return the idPeriodicidadSeleccionada
     */
    public int getIdPeriodicidadSeleccionada() {
	return idPeriodicidadSeleccionada;
    }

    /**
     * @param idPeriodicidadSeleccionada the idPeriodicidadSeleccionada to set
     */
    public void setIdPeriodicidadSeleccionada(int idPeriodicidadSeleccionada) {
	this.idPeriodicidadSeleccionada = idPeriodicidadSeleccionada;
    }

    /**
     * @return the sgAvisoPago
     */
    public SgAvisoPago getSgAvisoPago() {
	return sgAvisoPago;
    }

    /**
     * @param sgAvisoPago the sgAvisoPago to set
     */
    public void setSgAvisoPago(SgAvisoPago sgAvisoPago) {
	this.sgAvisoPago = sgAvisoPago;
    }

    /**
     * @return the diaAviso
     */
    public int getDiaAviso() {
	return diaAviso;
    }

    /**
     * @param diaAviso the diaAviso to set
     */
    public void setDiaAviso(int diaAviso) {
	this.diaAviso = diaAviso;
    }

    /**
     * @return the idTipoEspecificoSeleccionado
     */
    public int getIdTipoEspecificoSeleccionado() {
	return idTipoEspecificoSeleccionado;
    }

    /**
     * @param idTipoEspecificoSeleccionado the idTipoEspecificoSeleccionado to
     * set
     */
    public void setIdTipoEspecificoSeleccionado(int idTipoEspecificoSeleccionado) {
	this.idTipoEspecificoSeleccionado = idTipoEspecificoSeleccionado;
    }

    /**
     * @return the sgOficinaSeleccionada
     */
    public SgOficina getSgOficinaSeleccionada() {
	return sgOficinaSeleccionada;
    }

    /**
     * @param sgOficinaSeleccionada the sgOficinaSeleccionada to set
     */
    public void setSgOficinaSeleccionada(SgOficina sgOficinaSeleccionada) {
	this.sgOficinaSeleccionada = sgOficinaSeleccionada;
    }

    /**
     * @return the sgPeriodicidad
     */
    public SgPeriodicidad getSgPeriodicidad() {
	return sgPeriodicidad;
    }

    /**
     * @param sgPeriodicidad the sgPeriodicidad to set
     */
    public void setSgPeriodicidad(SgPeriodicidad sgPeriodicidad) {
	this.sgPeriodicidad = sgPeriodicidad;
    }

    /**
     * @return the fechaActual
     */
    public Date getFechaActual() {
	return (Date) fechaActual.clone();
    }

    /**
     * @param fechaActual the fechaActual to set
     */
    public void setFechaActual(Date fechaActual) {
	this.fechaActual = (Date) fechaActual.clone();
    }

    /**
     * @return the fechaPago
     */
    public Date getFechaPago() {
	return (Date) fechaPago.clone();
    }

    /**
     * @param fechaPago the fechaPago to set
     */
    public void setFechaPago(Date fechaPago) {
	this.fechaPago = (Date) fechaPago.clone();
    }

    /**
     * @return the fechaRealPago
     */
    public Date getFechaRealPago() {
	return (Date) fechaRealPago.clone();
    }

    /**
     * @param fechaRealPago the fechaRealPago to set
     */
    public void setFechaRealPago(Date fechaRealPago) {
	this.fechaRealPago = (Date) fechaRealPago.clone();
	//this.fechaRealPago = fechaRealPago;
    }

    /**
     * @return the fechaAviso
     */
    public Date getFechaAviso() {
	return (Date) fechaAviso.clone();
    }

    /**
     * @param fechaAviso the fechaAviso to set
     */
    public void setFechaAviso(Date fechaAviso) {
	this.fechaAviso = (Date) fechaAviso.clone();
	//this.fechaAviso = fechaAviso;
    }

    /**
     * @return the fechaRealAviso
     */
    public Date getFechaRealAviso() {
	return (Date) fechaRealAviso.clone();
    }

    /**
     * @param fechaRealAviso the fechaRealAviso to set
     */
    public void setFechaRealAviso(Date fechaRealAviso) {
	this.fechaRealAviso = (Date) fechaRealAviso.clone();
    }

    /**
     * @return the mrMostrarPanelAgregarNuevoAviso
     */
    public boolean isMrMostrarPanelAgregarNuevoAviso() {
	return mrMostrarPanelAgregarNuevoAviso;
    }

    /**
     * @param mrMostrarPanelAgregarNuevoAviso the
     * mrMostrarPanelAgregarNuevoAviso to set
     */
    public void setMrMostrarPanelAgregarNuevoAviso(boolean mrMostrarPanelAgregarNuevoAviso) {
	this.mrMostrarPanelAgregarNuevoAviso = mrMostrarPanelAgregarNuevoAviso;
    }

    /**
     * @return the mrPopupModificarAviso
     */
    public boolean isMrPopupModificarAviso() {
	return mrPopupModificarAviso;
    }

    /**
     * @param mrPopupModificarAviso the mrPopupModificarAviso to set
     */
    public void setMrPopupModificarAviso(boolean mrPopupModificarAviso) {
	this.mrPopupModificarAviso = mrPopupModificarAviso;
    }

    /**
     * @return the mrPopupCatalogoAvisos
     */
    public boolean isMrPopupCatalogoAvisos() {
	return mrPopupCatalogoAvisos;
    }

    /**
     * @param mrPopupCatalogoAvisos the mrPopupCatalogoAvisos to set
     */
    public void setMrPopupCatalogoAvisos(boolean mrPopupCatalogoAvisos) {
	this.mrPopupCatalogoAvisos = mrPopupCatalogoAvisos;
    }

    /**
     * @return the mrMostrarPanelCatalogo
     */
    public boolean isMrMostrarPanelCatalogo() {
	return mrMostrarPanelCatalogo;
    }

    /**
     * @param mrMostrarPanelCatalogo the mrMostrarPanelCatalogo to set
     */
    public void setMrMostrarPanelCatalogo(boolean mrMostrarPanelCatalogo) {
	this.mrMostrarPanelCatalogo = mrMostrarPanelCatalogo;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }
}
