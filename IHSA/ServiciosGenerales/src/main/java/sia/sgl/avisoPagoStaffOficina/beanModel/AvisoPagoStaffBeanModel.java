/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.avisoPagoStaffOficina.beanModel;

import java.io.Serializable;
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
import sia.modelo.SgAvisoPagoStaff;
import sia.modelo.SgPeriodicidad;
import sia.modelo.SgStaff;
import sia.modelo.SgTipoTipoEspecifico;
import sia.servicios.sgl.impl.SgAvisoPagoImpl;
import sia.servicios.sgl.impl.SgAvisoPagoOficinaImpl;
import sia.servicios.sgl.impl.SgAvisoPagoStaffImpl;
import sia.servicios.sgl.impl.SgPeriodicidadImpl;
import sia.servicios.sgl.impl.SgStaffImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Named(value = "avisoPagoStaffBeanModel")

public class AvisoPagoStaffBeanModel implements Serializable {

    @Inject
    private Sesion sesion;
    @Inject
    private SgAvisoPagoImpl sgAvisoPagoService;
    @Inject
    private SgAvisoPagoStaffImpl sgAvisoPagoStaffService;
    @Inject
    private SgStaffImpl staffService;
    @Inject
    private SgTipoTipoEspecificoImpl sgTipoTipoEspecificoImpl;
    @Inject
    private SgPeriodicidadImpl sgPeriodicidadService;
    @Inject
    private SiManejoFechaImpl siManejoFechaService;
    @Inject
    private SgAvisoPagoOficinaImpl sgAvisoPagoOficinaService;
    //Entidades
    private SgStaff sgStaffSeleccionado;
    private SgAvisoPago sgAvisoPago;
    private SgAvisoPagoStaff sgAvisoPagoRelacion;
    private SgPeriodicidad sgPeriodicidad;
    //Colecciones
    private DataModel avisosPagosStaffModel;
    private DataModel avisosPagosCatalogoModel;
    private List<SelectItem> listaStaffItems;
    private List<SelectItem> listaPagosStaffItems;
    private List<SelectItem> listaPeriodosPagoItems;
    //variables
    private String mensajeError = "";
    private boolean mrMostrarPanelAgregarNuevoAviso = false;
    private boolean mrPopupModificarAviso = false;
    private boolean mrPopupCatalogoAvisos = false;
    private boolean mrMostrarPanelCatalogo = false;
    private int idStaffSeleccionado;
    private int idPagoStaffSeleccionado;
    private int idPeriodicidadSeleecionada;
    private int idTipoEspecificoSeleccionado;
    private int diaEstimadoPago;
    private int diaAviso;
    private Date fechaActual;
    private Date fechaPago;
    private Date fechaAviso;

    public AvisoPagoStaffBeanModel() {
    }

    @PostConstruct
    public void beginConversationAvisoPago() {
	listaStaffItems = new ArrayList<SelectItem>();
	UtilLog4j.log.info(this, "AvisoPagoStaffBeanModel.beginConversationAvisoPago()");
	setIdStaffSeleccionado(-1);
	traerAllAvisosToStaff();
	traerStaffItems();
    }

    /**
     * * Consultas *
     */
    public void traerStaff() {
	UtilLog4j.log.info(this, "AvisoPagoStaffBean.traerStaff");
	try {
	    this.sgStaffSeleccionado = this.staffService.find(getIdStaffSeleccionado());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
    }

    public void traerAllAvisosToStaff() {
	UtilLog4j.log.info(this, "AvisoPagosStaffBeanModel");
	try {
	    ListDataModel<SgAvisoPago> avisosModel = new ListDataModel(sgAvisoPagoStaffService.findAllAvisoPagoToStaff(getIdStaffSeleccionado()));
	    this.avisosPagosStaffModel = (DataModel) avisosModel;
	    UtilLog4j.log.info(this, "Datamodel de avisos asignado " + avisosModel.getRowCount());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
    }

    public void traerCatalogoAvisos() {
	UtilLog4j.log.info(this, "AvisoPagoStaffBeanModel.traerCatalogoAvisos");
	try {
	    UtilLog4j.log.info(this, "idStaffSeleccionado " + getIdPagoStaffSeleccionado());
	    ListDataModel<SgAvisoPago> avisosModel = new ListDataModel(sgAvisoPagoStaffService.findAllCatalogoAvisos(getIdStaffSeleccionado()));
	    this.avisosPagosCatalogoModel = (DataModel) avisosModel;
	    UtilLog4j.log.info(this, "Datamodel de catalogo de avisos asignado " + avisosModel.getRowCount());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion " + e.getMessage());
	}
    }

    public void traerStaffItems() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	List<SgStaff> ls;
	try {
	    UtilLog4j.log.info(this, "AvisoPAgoStaffBeanMode.traerStaffItems");
	    //Tipo Especifico
	    ls = staffService.getAllStaffByStatusAndOficina(Constantes.NO_ELIMINADO, this.sesion.getOficinaActual().getId());
	    for (SgStaff stf : ls) {
		SelectItem item = new SelectItem(stf.getId(), stf.getNombre());
		l.add(item);
	    }
	    setListaStaffItems(l);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Ocurrio un error en la consulta de Staff Items'");
	}
    }

    public void traerPagosPorTipoEspecificoToStaffItems() {
	UtilLog4j.log.info(this, "TraerTipoEspecificoPorTipoStaff");
	if (getIdStaffSeleccionado() > 0) {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    List<SgTipoTipoEspecifico> lt;
	    try {                                               //idStaff = 3

		// lt = sgTipoTipoEspecificoImpl.traerPorIdTipo(3, Constantes.BOOLEAN_FALSE);
		lt = sgTipoTipoEspecificoImpl.traerPorIdTipoAvisoStaff(3, getIdStaffSeleccionado(), Constantes.BOOLEAN_FALSE);
		UtilLog4j.log.info(this, "la lista de tipos especificos tiene " + lt.size());
		for (SgTipoTipoEspecifico tipoEsp : lt) {
		    UtilLog4j.log.info(this, " tipo " + tipoEsp.getSgTipoEspecifico().getNombre());
		    SelectItem item = new SelectItem(tipoEsp.getSgTipoEspecifico().getId(), tipoEsp.getSgTipoEspecifico().getNombre());
		    l.add(item);
		}
		setListaPagosStaffItems(l);
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, "Aqui en la excepción");
	    }
	} else {
	    setListaPagosStaffItems(null);
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
	    UtilLog4j.log.fatal(this, "Ocurrio un error en la consulta de Periodos'");
	}
    }

    public void createAvisoPago() {
	UtilLog4j.log.info(this, "AvisoPAgoStaffBeanMode.createAvisoPago");
	SgAvisoPago avisoPagoEncontrado = null;
	SgAvisoPago avisoP = null;
	SgAvisoPagoStaff relacionEncontrada = null;
	try {
	    if (getIdPagoStaffSeleccionado() > 0 && getIdPeriodicidadSeleecionada() > 0 && getIdStaffSeleccionado() > 0 && getSgAvisoPago() != null) {
		UtilLog4j.log.info(this, "Todo preparado para insetar..");
		avisoPagoEncontrado = sgAvisoPagoService.findSgAvisoPagoRepetido(getIdPagoStaffSeleccionado());

		if (avisoPagoEncontrado == null) {
		    UtilLog4j.log.info(this, "el aviso no esta en la tabla , crear aviso y crear relacion..");
		    calcularFechas();
		    getSgAvisoPago().setFechaProximoAviso(fechaAviso);
		    avisoP = sgAvisoPagoService.createAvisoPago(getSgAvisoPago(),
			    getIdStaffSeleccionado(),
			    getIdPeriodicidadSeleecionada(),
			    getIdPagoStaffSeleccionado(),
			    sesion.getUsuario());
		    sgAvisoPagoStaffService.createRelacionAvisoPagoStaff(avisoP, getIdStaffSeleccionado(), sesion.getUsuario());
		    traerAllAvisosToStaff();
		} else {
		    UtilLog4j.log.info(this, "el aviso ya esta en la tabla.. buscar que no se repita");
		    relacionEncontrada = sgAvisoPagoStaffService.findSgAvisoPagoRepetidoRelacion(getIdStaffSeleccionado(), avisoPagoEncontrado.getId());
		    if (relacionEncontrada == null) {
			UtilLog4j.log.info(this, "la realacion no se encontro se tiene que crear..");

			sgAvisoPagoStaffService.createRelacionAvisoPagoStaff(avisoPagoEncontrado, getIdStaffSeleccionado(), sesion.getUsuario());
			UtilLog4j.log.info(this, "se creo la relacion.");
			traerAllAvisosToStaff();
		    }
		}

	    }

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Ocurrio un error en la consulta de Periodos'");
	}
    }

    public void crearAviso() {
	UtilLog4j.log.info(this, "crearAviso");
	calcularFechas();
	getSgAvisoPago().setFechaProximoAviso(fechaAviso);
	//setSgAvisoPago(sgAvisoPagoService.createAvisoPago(getSgAvisoPago(),
	sgAvisoPagoService.createAvisoPago(getSgAvisoPago(),
		getIdStaffSeleccionado(),
		getIdPeriodicidadSeleecionada(),
		getIdPagoStaffSeleccionado(),
		sesion.getUsuario());
	sgAvisoPagoService.ponerComoTipoEspecificoUsado(getSgAvisoPago(), sesion.getUsuario());
    }

    public void createRelacionStaff() {
	UtilLog4j.log.info(this, "create realacion");
	sgAvisoPagoStaffService.createRelacionAvisoPagoStaff(getSgAvisoPago(), getIdStaffSeleccionado(), sesion.getUsuario());
	UtilLog4j.log.info(this, "Solo se creo la relacion");

    }

    public void eliminarRelacion() {
	UtilLog4j.log.info(this, "create realcion");

	UtilLog4j.log.info(this, "Solo se creo la relacion");

    }

    public void editAvisoPago() {
	UtilLog4j.log.info(this, "AvisoPAgoStaffBeanMode.editAvisoPago");
	SgAvisoPago avisoPagoEncontrado = null;
	SgAvisoPago avisoP = null;
	SgAvisoPagoStaff relacionEncontrada = null;
	int count = 0;
	try {

	    if (getIdPagoStaffSeleccionado() > 0 && getIdPeriodicidadSeleecionada() > 0 && getIdStaffSeleccionado() > 0 && getSgAvisoPago() != null) {
		UtilLog4j.log.info(this, "Todo preparado para modificar el aviso..");
                //Calcular fechas

		//buscar el registro en la tabla de SgAvisos
//                avisoPagoEncontrado = sgAvisoPagoService.findSgAvisoPagoRepetido(getIdPagoStaffSeleccionado());
		avisoPagoEncontrado = sgAvisoPagoService.findSgAvisoPagoRepetidoAtributos(getIdTipoEspecificoSeleccionado(), getIdPeriodicidadSeleecionada(), getSgAvisoPago().getDiaAnticipadoPago(), getSgAvisoPago().getDiaEstimadoPago());
		if (avisoPagoEncontrado != null) {
		    UtilLog4j.log.info(this, "Ya existe un registro con esas especificaciones..");
		    UtilLog4j.log.info(this, "Se procede a saber si esta siendo utilizado por mas de un usuario..");
		    if (sgAvisoPagoStaffService.findCoutSgAvisoPagoRepetidoRelacion(avisoPagoEncontrado.getId()) > 1) {

			UtilLog4j.log.info(this, "El aviso esta siendo utilizado por mas de un usuario, no se puede modificar el registro de la tabla sgAvisoPago..");
			UtilLog4j.log.info(this, "Saber si el staff seleccionado es quien lo esta utilizando");
			relacionEncontrada = sgAvisoPagoStaffService.findSgAvisoPagoRepetidoRelacion(getIdStaffSeleccionado(), avisoPagoEncontrado.getId());
			if (relacionEncontrada != null) {
			    UtilLog4j.log.info(this, "ya existe una relación con este staff..se anula la operación");
//                            calcularFechas();
//                            getSgAvisoPago().setFechaProximoAviso(fechaAviso);
//                            sgAvisoPagoService.editAvisoPago(getSgAvisoPago(),
//                                    getIdPeriodicidadSeleecionada(),
//                                    getIdTipoEspecificoSeleccionado(),
//                                    sesion.getUsuario());

			} else {
			    UtilLog4j.log.info(this, "no existe la relacion con este staff..se creara una nueva");
			    sgAvisoPagoStaffService.createRelacionAvisoPagoStaff(avisoPagoEncontrado, getIdStaffSeleccionado(), sesion.getUsuario());
			    UtilLog4j.log.info(this, "Se creo la relacion " + avisoPagoEncontrado.getSgTipoEspecifico().getNombre() + " y el staff id " + getIdPagoStaffSeleccionado());
			    UtilLog4j.log.info(this, "se elimina la relacion antigua ");
			    sgAvisoPagoStaffService.deleteRelacionAvisoStaff(getSgAvisoPago().getId(), getIdStaffSeleccionado(), sesion.getUsuario());
			    UtilLog4j.log.info(this, "se elimino la relacion ");

			}
		    } else {
			//modificar el registro en SgAvisoPago
			UtilLog4j.log.info(this, "El aviso no esta siendo utilizado por mas de un usuario en la tabla SgAvisoPago ");
			calcularFechas();
			getSgAvisoPago().setFechaProximoAviso(fechaAviso);
			sgAvisoPagoService.editAvisoPago(getSgAvisoPago(),
				getIdPeriodicidadSeleecionada(),
				getIdTipoEspecificoSeleccionado(),
				sesion.getUsuario());
		    }
		} else {
		    count = sgAvisoPagoStaffService.findCoutSgAvisoPagoRepetidoRelacion(getSgAvisoPago().getId());

		    if (count == 1) {
			UtilLog4j.log.info(this, " se repite una vez el se modifica en sgAviso ");
			calcularFechas();
			getSgAvisoPago().setFechaProximoAviso(fechaAviso);
			sgAvisoPagoService.editAvisoPago(getSgAvisoPago(),
				getIdPeriodicidadSeleecionada(),
				getIdTipoEspecificoSeleccionado(),
				sesion.getUsuario());
			UtilLog4j.log.info(this, "se creo solo la relacion ");
		    } else {
			UtilLog4j.log.info(this, "El aviso no esta en la tabla de SgAvisos, se creará..");
			calcularFechas();
			SgAvisoPago avisoTemp = new SgAvisoPago();
			avisoTemp.setFechaProximoAviso(fechaAviso);
			avisoTemp.setDiaAnticipadoPago(getSgAvisoPago().getDiaAnticipadoPago());
			avisoTemp.setDiaEstimadoPago(getSgAvisoPago().getDiaEstimadoPago());
			//getSgAvisoPago().setFechaProximoAviso(fechaAviso);

			avisoP = sgAvisoPagoService.createAvisoPago(avisoTemp,
				getIdStaffSeleccionado(),
				getIdPeriodicidadSeleecionada(),
				getIdPagoStaffSeleccionado(),
				sesion.getUsuario());
			UtilLog4j.log.info(this, "El aviso se creo satisfactoriamente..Se creara una nueva relación");
			sgAvisoPagoStaffService.createRelacionAvisoPagoStaff(avisoP, getIdStaffSeleccionado(), sesion.getUsuario());
			UtilLog4j.log.info(this, "Se creo satisfactoriamente la relacion");
			UtilLog4j.log.info(this, "Se eliminara  la relacion pasada");
			sgAvisoPagoStaffService.deleteRelacionAvisoStaff(getSgAvisoPago().getId(), getIdStaffSeleccionado(), sesion.getUsuario());
			UtilLog4j.log.info(this, "Relacion eliminada");

		    }
		}
		traerAllAvisosToStaff();
	    }

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion " + e.getMessage());
	    UtilLog4j.log.fatal(this, "Ocurrio un error en la modificacion");
	}
    }

    public void deteleRelacionAvisoPago() {
	UtilLog4j.log.info(this, "AvisoPAgoStaffBeanMode.deteleAvisoPago");
	try {
	    if (getSgAvisoPago() != null) {
		UtilLog4j.log.info(this, "Todo preparado para eliminar el aviso..");
		if (sgAvisoPagoOficinaService.findCoutSgAvisoPagoRepetidoRelacion(getSgAvisoPago().getId())
			+ sgAvisoPagoStaffService.findCoutSgAvisoPagoRepetidoRelacion(getSgAvisoPago().getId()) == 1) {
		    //eliminar aviso
		    sgAvisoPagoService.deleteAviso(getSgAvisoPago(), sesion.getUsuario());
		}
		this.sgAvisoPagoStaffService.deleteRelacionAvisoStaff(getSgAvisoPago().getId(), getIdStaffSeleccionado(), sesion.getUsuario());
		traerAllAvisosToStaff();
		UtilLog4j.log.info(this, "Se eliminó la relacion..");
	    }

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Ocurrio un error en la consulta de Periodos'");
	}
    }

    /**
     * *
     *
     * Calculo de fechas
     */
    public boolean calcularFechas() {
	try {
	    this.fechaActual = new Date();
	    UtilLog4j.log.info(this, "Calcular fechas");
	    this.fechaPago = siManejoFechaService.componerFechaApartirDeDia(fechaActual, sgAvisoPago.getDiaEstimadoPago());
	    this.fechaAviso = siManejoFechaService.fechaRestarDias(fechaPago, getSgAvisoPago().getDiaAnticipadoPago());

	    //Calcular el ultimo dia del mes que sea
	    // Calendar cal = GregorianCalendar.getInstance();
	    // UtilLog4j.log.info(this, "Último día de este mes: " + cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
	    UtilLog4j.log.info(this, "Consultar Periodicidad " + getIdPeriodicidadSeleecionada());
	    setSgPeriodicidad(sgPeriodicidadService.find(getIdPeriodicidadSeleecionada()));

	    this.fechaAviso = siManejoFechaService.fechaSumarMes(fechaAviso, getSgPeriodicidad().getMes());

	    this.fechaPago = siManejoFechaService.fechaSumarMes(fechaPago, getSgPeriodicidad().getMes());

	    while (fechaActual.compareTo(fechaAviso) > 0) {
		UtilLog4j.log.info(this, "La fechaActual es mayor la fecha de aviso ...Sumar periodicidad a la fecha de aviso " + getSgPeriodicidad().getMes());
		this.fechaAviso = siManejoFechaService.fechaSumarMes(fechaAviso, getSgPeriodicidad().getMes());
		//this.fechaAviso = siManejoFechaService.fechaSumarMes(fechaAviso,5);
		//compara si esta en el mismo mes...SI: Sumar nuevamente la periodiciada ; No: Guardar
	    }

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion : " + e.getMessage());
	    UtilLog4j.log.fatal(this, "Ocurrio un error en la consulta de Periodos'");
	}
	return true;
    }

    public boolean buscarAvisoSeleccion() {
	UtilLog4j.log.info(this, "getidStaff" + getIdStaffSeleccionado());
	UtilLog4j.log.info(this, "tipo especifico " + getSgAvisoPago().getSgTipoEspecifico().getNombre());

	boolean ret = false;
	//if(sgAvisoPagoStaffService.findSgAvisoPagoRepetidoRelacion(getIdStaffSeleccionado(),getSgAvisoPago().getId())!=null)
	if (sgAvisoPagoStaffService.findSgAvisoPagoRepetidoRelacion(getIdStaffSeleccionado(), getSgAvisoPago().getSgTipoEspecifico().getId()) != null) {
	    ret = true;
	} else {
	    ret = false;
	}

	return ret;
    }

    public boolean buscarAvisoCreate() {
	boolean ret = false;
	UtilLog4j.log.info(this, "idTipo " + getIdPagoStaffSeleccionado());
	UtilLog4j.log.info(this, "id Periodicidad " + getIdPeriodicidadSeleecionada());
	UtilLog4j.log.info(this, "dia estimado " + getSgAvisoPago().getDiaEstimadoPago());
	UtilLog4j.log.info(this, "Dia anticipado " + getSgAvisoPago().getDiaAnticipadoPago());

	SgAvisoPago aviso = sgAvisoPagoService.findSgAvisoPagoRepetidoAtributos(getIdPagoStaffSeleccionado(), getIdPeriodicidadSeleecionada(), getSgAvisoPago().getDiaEstimadoPago(), getSgAvisoPago().getDiaAnticipadoPago());
	if (aviso != null) {
	    UtilLog4j.log.info(this, "se encontró el aviso");
	    //buscar relacion
	    if (sgAvisoPagoStaffService.findSgAvisoPagoRepetidoRelacion(getIdStaffSeleccionado(), aviso.getId()) != null) {
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
	UtilLog4j.log.info(this, "idPago (Tipo especifico) " + getIdPagoStaffSeleccionado());
	UtilLog4j.log.info(this, "id Periodicidad " + getIdPagoStaffSeleccionado());
	boolean ret = false;
	SgAvisoPagoStaff vs = sgAvisoPagoStaffService.findAvisoPagoRepetido(getIdStaffSeleccionado(), getIdPagoStaffSeleccionado());
	if (vs != null) {
	    setSgAvisoPago(vs.getSgAvisoPago());
	    ret = true;
	} else {
	    ret = false;
	}
//
	return ret;
    }

    public boolean buscarPagoRepetidoAtributos() {
	UtilLog4j.log.info(this, "idPago (Tipo especifico) " + getIdPagoStaffSeleccionado());
	UtilLog4j.log.info(this, "id Periodicidad " + getIdPagoStaffSeleccionado());
	boolean ret = false;
	SgAvisoPago avisoEncontrado = sgAvisoPagoService.findSgAvisoPagoRepetidoAtributos(getIdPagoStaffSeleccionado(), getIdPeriodicidadSeleecionada(), getSgAvisoPago().getDiaEstimadoPago(), getSgAvisoPago().getDiaAnticipadoPago());

	if (avisoEncontrado != null) {
	    ret = true;
	} else {
	    ret = false;
	}
	return ret;
    }

    /**
     * @return the avisosPagosStaffModel
     */
    public DataModel getAvisosPagosStaffModel() {
	return avisosPagosStaffModel;
    }

    /**
     * @param avisosPagosStaffModel the avisosPagosStaffModel to set
     */
    public void setAvisosPagosStaffModel(DataModel avisosPagosStaffModel) {
	this.avisosPagosStaffModel = avisosPagosStaffModel;
    }

    /*
     * selectItems de *
     */
    /**
     * @return the listaStaffItems
     */
    public List<SelectItem> getListaStaffItems() {
	return listaStaffItems;
    }

    /**
     * @param listaStaffItems the listaStaffItems to set
     */
    public void setListaStaffItems(List<SelectItem> listaStaffItems) {
	this.listaStaffItems = listaStaffItems;
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
     * @return the sgAvisoPagoRelacion
     */
    public SgAvisoPagoStaff getSgAvisoPagoRelacion() {
	return sgAvisoPagoRelacion;
    }

    /**
     * @param sgAvisoPagoRelacion the sgAvisoPagoRelacion to set
     */
    public void setSgAvisoPagoRelacion(SgAvisoPagoStaff sgAvisoPagoRelacion) {
	this.sgAvisoPagoRelacion = sgAvisoPagoRelacion;
    }

    /**
     * @return the sgStaffSeleccionado
     */
    public SgStaff getSgStaffSeleccionado() {
	return sgStaffSeleccionado;
    }

    /**
     * @param sgStaffSeleccionado the sgStaffSeleccionado to set
     */
    public void setSgStaffSeleccionado(SgStaff sgStaffSeleccionado) {
	this.sgStaffSeleccionado = sgStaffSeleccionado;
    }

    /**
     * @return the idStaffSeleccionado
     */
    public int getIdStaffSeleccionado() {
	return idStaffSeleccionado;
    }

    /**
     * @param idStaffSeleccionado the idStaffSeleccionado to set
     */
    public void setIdStaffSeleccionado(int idStaffSeleccionado) {
	this.idStaffSeleccionado = idStaffSeleccionado;
    }

    /**
     * @return the listaPagosStaffItems
     */
    public List<SelectItem> getListaPagosStaffItems() {
	return listaPagosStaffItems;
    }

    /**
     * @param listaPagosStaffItems the listaPagosStaffItems to set
     */
    public void setListaPagosStaffItems(List<SelectItem> listaPagosStaffItems) {
	this.listaPagosStaffItems = listaPagosStaffItems;
    }

    /**
     * @return the idPagoStaffSeleccionado
     */
    public int getIdPagoStaffSeleccionado() {
	return idPagoStaffSeleccionado;
    }

    /**
     * @param idPagoStaffSeleccionado the idPagoStaffSeleccionado to set
     */
    public void setIdPagoStaffSeleccionado(int idPagoStaffSeleccionado) {
	this.idPagoStaffSeleccionado = idPagoStaffSeleccionado;
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
     * @return the idPeriodicidadSeleecionada
     */
    public int getIdPeriodicidadSeleecionada() {
	return idPeriodicidadSeleecionada;
    }

    /**
     * @param idPeriodicidadSeleecionada the idPeriodicidadSeleecionada to set
     */
    public void setIdPeriodicidadSeleecionada(int idPeriodicidadSeleecionada) {
	this.idPeriodicidadSeleecionada = idPeriodicidadSeleecionada;
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
     * @return the diaEstimadoPago
     */
    public int getDiaEstimadoPago() {
	return diaEstimadoPago;
    }

    /**
     * @param diaEstimadoPago the diaEstimadoPago to set
     */
    public void setDiaEstimadoPago(int diaEstimadoPago) {
	this.diaEstimadoPago = diaEstimadoPago;
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
    }

    public Date fechaPago() {
	return siManejoFechaService.componerFechaApartirDeDia(fechaActual, getSgAvisoPago().getDiaEstimadoPago());
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
     * @return the mensajeError
     */
    public String getMensajeError() {
	return mensajeError;
    }

    /**
     * @param mensajeError the mensajeError to set
     */
    public void setMensajeError(String mensajeError) {
	this.mensajeError = mensajeError;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }
}
