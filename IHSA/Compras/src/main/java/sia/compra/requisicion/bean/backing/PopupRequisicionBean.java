
/*
 * PopupRequisicionBean.java
 * Creado el 16/07/2009, 09:27:18 AM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.CustomScoped;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import sia.constantes.TipoRequisicion;
import sia.modelo.Gerencia;
import sia.modelo.ProyectoOt;
import sia.modelo.Requisicion;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com @date 16/07/2009
 */
@Named (value = PopupRequisicionBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class PopupRequisicionBean implements Serializable {

    //------------------------------------------------------
    public static final String BEAN_NAME = "popupRequisicionBean";
    //------------------------------------------------------
    
    @Inject
    private UsuarioBean usuarioBean;
    private CompaniaBean companiaBean = (CompaniaBean) FacesUtilsBean.getManagedBean("companiaBean");
    private GerenciaBean gerenciaBean = (GerenciaBean) FacesUtilsBean.getManagedBean("gerenciaBean");
    private ProyectoOtBean proyectoOtBean = (ProyectoOtBean) FacesUtilsBean.getManagedBean("proyectoOtBean");
    private MonedaBean monedaBean = (MonedaBean) FacesUtilsBean.getManagedBean("monedaBean");
    private List<SelectItem> listaG;    
    private List<SelectItem> listaProyectosOT;
    private List<SelectItem> listaUnidadCosto;    
    private int idGerencia;    
    private int idProyectoOT;
    private int idNombreTarea;
    private String tipoRequisicion;    
    //
    // Esto es para mostrar los datos en el panel emergente si no no muestra nada y marca error
    private Requisicion requisicion = new Requisicion();
    // render flags for both dialogs
    private boolean draggableRendered = false;
    private boolean modalRendered = false;
    // if we should use the auto centre attribute on the draggable dialog
    private boolean autoCentre = false;
    private boolean mostrarProyectos = false;
    private boolean mostrarTiposObra = false;
    private String operacion;
    //
//   private OcTareaVo ocTareaVo;
    //
    private String idRevisa;
    private String idAprueba;
    private int idUnidadCosto;

    /**
     * Creates a new instance of PopupRequisicionBean
     */
    public PopupRequisicionBean() {
//        this.listaGerencias = gerenciaBean.getListaGerencias();
//        this.listaProyectos = proyectoOtBean.getListaPorGerencia(this.extraerPrimerElemento(listaGerencias), "Iberoamericana de Hidrocarburos S.A. de C.V.");
//        this.listaTiposObra = tipoObraBean.getListaTiposObra(this.extraerPrimerElemento(listaProyectos));
    }

    // Zona de Listas
    //--- Lista de compañia
    public List getListaCompanias() {
	return companiaBean.getListaCompanias();
    }

    //--- Lista de Monedas
    public List getListaMonedas() {
	return monedaBean.getListaMonedas(usuarioBean.getUsuarioConectado().getApCampo().getId());
    }

    //--- Lista de Analistas
//    public List getListaAnalista() {
//        return usuarioBean.getListaAnalista();
//    }
    public List<SelectItem> getListaTipoRequisicion() {
	try {
	    List<SelectItem> ls = new ArrayList<>();
	    ls.add(new SelectItem(TipoRequisicion.PS, "Productos/Servicios"));
	    ls.add(new SelectItem(TipoRequisicion.AF, "Activo Fijo"));
	    return ls;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	    return null;
	}
    }

    public void cambiarTipoRequisicion(ValueChangeEvent event) {
	setTipoRequisicion((String) event.getNewValue());
	if (getTipoRequisicion().equals(TipoRequisicion.AF.name())) {
	    setListaG(gerenciaBean.traerGereciaActivoFijo(usuarioBean.getUsuarioConectado().getApCampo().getId()));
	    setIdGerencia(0);
	    setListaProyectosOT(null);
	    setListaUnidadCosto(null);
	    setIdUnidadCosto(0);
	} else {
	    this.listaG = gerenciaBean.listaGerenciasConAbreviatura(usuarioBean.getUsuarioConectado().getApCampo().getId());
	    setListaProyectosOT(null);
            //setListaUnidadCosto(proyectoOtBean.listaUnidadCosto(getIdGerencia(), getIdProyectoOT()));
	    //setListaUnidadCosto(null);
	    setIdUnidadCosto(0);
	    setIdGerencia(0);
	    setIdProyectoOT(0);
	}

    }

    //--- Lista de Usuarios que revisan requisicion
    public List<SelectItem> getListaRevisan() {
	if (requisicion != null && this.requisicion.getSolicita() != null) {
	    return usuarioBean.listaRevisa(usuarioBean.getUsuarioConectado().getId());
	}
	return null;
	//return usuarioBean.listaRevisa(usuarioBean.getUsuarioConectado().getId());
    }

    public List<SelectItem> getListaAprueban() {
	if (requisicion != null && this.requisicion.getSolicita() != null && getIdRevisa() != null) {
	    return usuarioBean.getListaAprueban(usuarioBean.getUsuarioConectado().getId(), getIdRevisa());
	}
	return null;
    }

    public void cambiarValorCompania(ValueChangeEvent event) {
	this.requisicion.setCompania(this.companiaBean.buscarPorNombre(event.getNewValue()));
	this.listaG = gerenciaBean.getListaGerencias(this.requisicion.getCompania().getRfc(), usuarioBean.getUsuarioConectado().getApCampo().getId());
	//
	this.requisicion.getTipoObra().setId(null);
	//
    }

    public void cambiarValorGerencia(ValueChangeEvent event) {
	idGerencia = (Integer) event.getNewValue();
	if (idGerencia > 0) {
	    if (tipoRequisicion.equals(TipoRequisicion.PS.name())) {
		this.listaProyectosOT = getProyectoOtBean().listaProyectoPorGerencia(getIdGerencia(), requisicion.getApCampo().getId(), getIdProyectoOT());
		if (getProyectoOtBean().isContineOt()) {
		    this.listaUnidadCosto = getProyectoOtBean().listaUnidadCosto(getIdGerencia(), getIdProyectoOT(), 0);
		    getProyectoOtBean().setContineOt(false);
		} else {
		    this.listaUnidadCosto = new ArrayList<>();
		    getProyectoOtBean().setContineOt(false);
		}                
	    } else {
		this.listaProyectosOT = gerenciaBean.traerProyectoActivoFijo(getIdGerencia(), usuarioBean.getUsuarioConectado().getApCampo().getId());
	    }            
	} else {
	    this.listaProyectosOT = new ArrayList<>();
	    this.listaUnidadCosto = new ArrayList<>();            
	}
	idProyectoOT = 0;
	idUnidadCosto = 0;
	idNombreTarea = 0;
    }

    public void cambiarGerencia(ValueChangeEvent event) {
	idGerencia = (Integer) event.getNewValue();
	if (idGerencia != 0) {
	    this.listaProyectosOT = getProyectoOtBean().listaProyectoPorGerencia(getIdGerencia(), usuarioBean.getUsuarioConectado().getApCampo().getId());
	} else {
	    this.requisicion.setGerencia(new Gerencia());
	    this.requisicion.setProyectoOt(new ProyectoOt());
	    this.listaProyectosOT = null;
	}
	idProyectoOT = 0;
	idUnidadCosto = 0;
	idNombreTarea = 0;
    }

    public void cambiarValorProyectoOt(ValueChangeEvent event) { 
	idProyectoOT = (Integer) event.getNewValue();
	if (idProyectoOT != 0) {
	    listaUnidadCosto = getProyectoOtBean().listaUnidadCosto(getIdGerencia(), getIdProyectoOT(), 0);
	} else {
//	    requisicion.getProyectoOt().setId(null);
	    idNombreTarea = 0;
	    idUnidadCosto = 0;
	    listaUnidadCosto = null;
	}
    }

////    public void cambiarValorUnidadCosto(ValueChangeEvent event) {
////        idUnidadCosto = (Integer) event.getNewValue();
////        //
////        if (idUnidadCosto != 0) {
////            listaTO = tipoObraBean.listaTarea(getIdProyectoOT(), getIdGerencia(), getIdUnidadCosto());
////            setIdNombreTarea(0);
////            setOcTareaVo(null);
////        } else {
////            this.requisicion.getProyectoOt().setId(null);
////            listaTO = null;
////            setIdNombreTarea(0);
////            setOcTareaVo(null);
////        }
////    }
    public String mostrarObras() {
	//      this.listaTiposObra = tipoObraBean.getListaTiposObra(this.extraerPrimerElemento(listaProyectos));
	this.mostrarTiposObra = true;
	return "";
    }

    public void cambiarValorRevisa(ValueChangeEvent event) {
	//this.requisicion.setRevisa(this.usuarioBean.buscarPorNombre(event.getNewValue()));
	setIdRevisa((String) event.getNewValue());
    }

    public void cambiarValorAprueba(ValueChangeEvent event) {
	//this.requisicion.setAprueba(this.usuarioBean.buscarPorNombre(event.getNewValue()));
	setIdAprueba((String) event.getNewValue());
    }

    public boolean getDraggableRendered() {
	return draggableRendered;
    }

    public void setDraggableRendered(boolean draggableRendered) {
	this.draggableRendered = draggableRendered;
    }

    public boolean getAutoCentre() {
	return autoCentre;
    }

    public void setAutoCentre(boolean autoCentre) {
	this.autoCentre = autoCentre;
    }

    public void toggleModal() {
//        if (modalRendered) {
////            this.mostrarProyectos = false;
////            this.mostrarTiposObra = false;
//            if ((this.requisicion.getGerencia().getId() == null) & (this.requisicion.getCompania().getRfc() == null)) {
//                this.listaProyectos = proyectoOtBean.getListaPorGerencia(this.extraerPrimerElemento(listaGerencias), "Iberoamericana de Hidrocarburos S.A. de C.V.");
//                this.listaTiposObra = tipoObraBean.getListaTiposObra(this.extraerPrimerElemento(listaProyectos));
//            } else {
//                this.listaProyectos = proyectoOtBean.getListaPorGerencia(this.requisicion.getGerencia().getNombre(), this.requisicion.getCompania().getNombre());
//                this.listaTiposObra = tipoObraBean.getListaTiposObra(this.requisicion.getProyectoOt().getNombre());
//            }
//        }
	modalRendered = !modalRendered;
    }

    /**
     * @return the requisicion
     */
    public Requisicion getRequisicion() {
	return requisicion;
    }

    /**
     * @param requisicion the requisicion to set
     */
    public void setRequisicion(Requisicion requisicion) {
	this.requisicion = requisicion;
//////        if (this.operacion.equals("Actualizar")) {
//////            this.listaG = this.gerenciaBean.getListaGerencias(this.requisicion.getCompania().getRfc(), requisicion.getApCampo().getId());
//////            if ((this.requisicion.getGerencia().getId() != null) && (this.requisicion.getCompania().getRfc() != null)) {
//////                idGerencia = requisicion.getGerencia().getId();
//////                //this.listaProyectos = proyectoOtBean.getListaPorGerenciaCompleta(this.requisicion.getGerencia().getNombre(), this.requisicion.getCompania().getNombre(),this.usuarioBean.getUsuarioConectado().getApCampo().getId());
//////                this.listaProyectosOT = proyectoOtBean.getListaPorGerencia(this.requisicion.getGerencia().getId(), this.requisicion.getCompania().getNombre(), this.usuarioBean.getUsuarioConectado().getApCampo().getId());
//////            } else {
//////                this.listaProyectosOT = null;
//////            }
//////            if (this.requisicion.getProyectoOt().getId() != null) {
//////                idProyectoOT = requisicion.getProyectoOt().getId();
//////                this.listaTiposObra = tipoObraBean.getListaTiposObraCompleta(this.requisicion.getProyectoOt().getNombre());
//////            } else {
//////                this.listaTiposObra = null;
//////            }
//////        } else {
//////            this.listaG = gerenciaBean.getListaGerencias("IHI070320FI3", usuarioBean.getUsuarioConectado().getApCampo().getId());
//////            this.listaProyectosOT = null;
//////            idGerencia = 0;
//////            idProyectoOT = 0;
//////            this.listaTiposObra = null;
//////        }

//////        this.operacion = "";
    }

    /**
     * @return the modalRenderedModificar
     */
    public boolean getModalRendered() {
	return modalRendered;
    }

    /**
     * @param modalRenderedModificar the modalRenderedModificar to set
     */
    public void setModalRendered(boolean modalRendered) {
	this.modalRendered = modalRendered;
    }

    /**
     * @return the mostrarProyectos
     */
    public boolean isMostrarProyectos() {
	return mostrarProyectos;
    }

    /**
     * @param mostrarProyectos the mostrarProyectos to set
     */
    public void setMostrarProyectos(boolean mostrarProyectos) {
	this.mostrarProyectos = mostrarProyectos;
    }

    /**
     * @return the mostrarTiposObra
     */
    public boolean isMostrarTiposObra() {
	return mostrarTiposObra;
    }

    /**
     * @param mostrarTiposObra the mostrarTiposObra to set
     */
    public void setMostrarTiposObra(boolean mostrarTiposObra) {
	this.mostrarTiposObra = mostrarTiposObra;
    }

    /**
     * @return the operacion
     */
    public String getOperacion() {
	return operacion;
    }

    /**
     * @param operacion the operacion to set
     */
    public void setOperacion(String operacion) {
	this.operacion = operacion;
    }

    /**
     * @return the listaG
     */
    public List<SelectItem> getListaG() {
	return listaG;
    }

    /**
     * @param listaG the listaG to set
     */
    public void setListaG(List<SelectItem> listaG) {
	this.listaG = listaG;
    }

    /**
     * @return the listaProyectosOT
     */
    public List<SelectItem> getListaProyectosOT() {
	return listaProyectosOT;
    }

    /**
     * @param listaProyectosOT the listaProyectosOT to set
     */
    public void setListaProyectosOT(List<SelectItem> listaProyectosOT) {
	this.listaProyectosOT = listaProyectosOT;
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
	return idGerencia;
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
	this.idGerencia = idGerencia;
    }

    /**
     * @return the idProyectoOT
     */
    public int getIdProyectoOT() {
	return idProyectoOT;
    }

    /**
     * @param idProyectoOT the idProyectoOT to set
     */
    public void setIdProyectoOT(int idProyectoOT) {
	this.idProyectoOT = idProyectoOT;
    }

    /**
     * @return the idRevisa
     */
    public String getIdRevisa() {
	return idRevisa;
    }

    /**
     * @param idRevisa the idRevisa to set
     */
    public void setIdRevisa(String idRevisa) {
	this.idRevisa = idRevisa;
    }

    /**
     * @return the idAprueba
     */
    public String getIdAprueba() {
	return idAprueba;
    }

    /**
     * @param idAprueba the idAprueba to set
     */
    public void setIdAprueba(String idAprueba) {
	this.idAprueba = idAprueba;
    }

    /**
     * @return the idNombreTarea
     */
    public int getIdNombreTarea() {
	return idNombreTarea;
    }

    /**
     * @param idNombreTarea the idNombreTarea to set
     */
    public void setIdNombreTarea(int idNombreTarea) {
	this.idNombreTarea = idNombreTarea;
    }

    /**
     * @return the idUnidadCosto
     */
    public int getIdUnidadCosto() {
	return idUnidadCosto;
    }

    /**
     * @param idUnidadCosto the idUnidadCosto to set
     */
    public void setIdUnidadCosto(int idUnidadCosto) {
	this.idUnidadCosto = idUnidadCosto;
    }

    /**
     * @return the listaUnidadCosto
     */
    public List<SelectItem> getListaUnidadCosto() {
	return listaUnidadCosto;
    }

    /**
     * @param listaUnidadCosto the listaUnidadCosto to set
     */
    public void setListaUnidadCosto(List<SelectItem> listaUnidadCosto) {
	this.listaUnidadCosto = listaUnidadCosto;
    }

    /**
     * @return the tipoRequisicion
     */
    public String getTipoRequisicion() {
	return tipoRequisicion;
    }

    /**
     * @param tipoRequisicion the tipoRequisicion to set
     */
    public void setTipoRequisicion(String tipoRequisicion) {
	this.tipoRequisicion = tipoRequisicion;
    }

    /**
     * @return the proyectoOtBean
     */
    public ProyectoOtBean getProyectoOtBean() {
	return proyectoOtBean;
    }
}
