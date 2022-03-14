
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.proyectoOT.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.PrimeFaces;
import sia.administracion.proyectoOT.model.ProyectoOTModel;
import sia.modelo.TipoObra;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.requisicion.vo.OcTareaVo;
import sia.modelo.sgl.vo.Vo;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@ManagedBean
@RequestScoped
public class ProyectoOTBean implements Serializable {

    /**
     * Creates a new instance of ProyectoOTBean
     */
    public ProyectoOTBean() {
    }
    @ManagedProperty(value = "#{proyectoOTModel}")
    private ProyectoOTModel proyectoOTModel;

    public void goToProyectoOT() {
        proyectoOTModel.inicio();        
    }

    public void mostrarPanelNuenoProyecto() {
        proyectoOTModel.mostrarNuevoProyecto();

    }

    private void limpiarAutocomplete() {
        clearComponent("proyectoOT", "proyectoOTSelect");
    }

    public List<SelectItem> getListaBloque() {
        UtilLog4j.log.info(this, "cargarApCamposItems");
        return proyectoOTModel.getApCamposItems();
    }

    public void traerProyectoOtPorNombre(TextChangeEvent event) {
        proyectoOTModel.setLista(new ListDataModel(proyectoOTModel.traerProyectoOTPorCadena((String) event.getNewValue())));
    }

    public void llenarListaOts() {
        proyectoOTModel.llenarLista();
        proyectoOTModel.setCadena("");
    }

    public DataModel getTraerProyectoOT() {
        try {
            return proyectoOTModel.getLista();
        } catch (Exception e) {
            return null;
        }
    }

    public DataModel getTraerRelacionProyectoGerencia() {
        UtilLog4j.log.info(this, "getTraerRelacionProyectoGerencia");
        try {
            if (proyectoOTModel.getProyectoOtVo() != null) {
                proyectoOTModel.setListaG(new ListDataModel(proyectoOTModel.traerRelacionProyectoGerencia()));
                return proyectoOTModel.getListaG();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public void modificarProyectoOT() {
        proyectoOTModel.setProyectoOtVo((ProyectoOtVo) proyectoOTModel.getLista().getRowData());
        //this.proyectoOt = proyectoOTModel.buscarProyectoOT(vo.getId());
//        this.listaG = null;
        proyectoOTModel.setMostrarPop(true);
    }

    public void seleccionarProyectoOT() {
        //this.proyectoOt = (ProyectoOt) this.lista.getRowData();
        proyectoOTModel.setProyectoOtVo((ProyectoOtVo) proyectoOTModel.getLista().getRowData());
        //proyectoOTModel.setListaG(null);
        proyectoOTModel.setGerenciaTarea(null);
        proyectoOTModel.setTareaVo(null);
        proyectoOTModel.setUnidadCostoTarea(null);
    }

    public void mostrarDetalle() {
        proyectoOTModel.setProyectoOtVo((ProyectoOtVo) proyectoOTModel.getLista().getRowData());
        proyectoOTModel.setListaOrden(new ListDataModel(proyectoOTModel.traerDetalleProyectoOt(proyectoOTModel.getProyectoOtVo().getId())));
    }

    public void cerrar() {
//        this.listaG = null;
//        this.proyectoOt = null;
        proyectoOTModel.setMostrarPop(false);
    }

    public void cancelarModificacion() {
//        this.proyectoOt = null;
        proyectoOTModel.setMostrarPop(false);
    }

    public void completarModificacion() {
        this.proyectoOTModel.completarModificacion();
        proyectoOTModel.setMostrarPop(false);

        //Refrescar lista
        //this.lista = new ListDataModel(this.proyectoOTModel.buscarProyectoOT(getCuentaContable()));
        //poner unmensaje de actualizacion
    }

    public void elminarProyectoOT() {
        proyectoOTModel.setProyectoOtVo((ProyectoOtVo) proyectoOTModel.getLista().getRowData());
        if (!this.proyectoOTModel.validarEliminacionProyectoOt()) {
            if (this.proyectoOTModel.elminarProyectoOT()) {
                FacesUtils.addInfoMessage("Se eliminó correctamente el Proyecto Ot");
                if (!proyectoOTModel.getCadena().isEmpty()) {
                    proyectoOTModel.setLista(new ListDataModel(proyectoOTModel.traerProyectoOTPorCadena(proyectoOTModel.getCadena())));
                } else {
                    proyectoOTModel.llenarLista();
                }

                limpiar(event);
            } else {
                FacesUtils.addInfoMessage("Existio un error al eliminar el Proyecto Ot");
            }
        } else {
            FacesUtils.addInfoMessage("Este proyecto no puede eliminarse por que tiene relaciones activas");
        }

    }

    public void cerrarProyectoOT() {
        proyectoOTModel.setProyectoOtVo((ProyectoOtVo) proyectoOTModel.getLista().getRowData());
        if (this.proyectoOTModel.cerrarProyectoOT()) {
            FacesUtils.addInfoMessage("Se cerró el proyecto OT correctamente");
            if (!proyectoOTModel.getCadena().isEmpty()) {
                proyectoOTModel.setLista(new ListDataModel(proyectoOTModel.traerProyectoOTPorCadena(proyectoOTModel.getCadena())));
            } else {
                proyectoOTModel.llenarLista();
            }

//            setCuentaContable(pOT.getCuentaContable());
//            buscarProyectoOT(event);
            proyectoOTModel.setListaG(null);

        } else {
            FacesUtils.addInfoMessage("Existio un error al intentar cerra el proyecto OT ");
        }

    }

    public void abrirProyectoOT() {
        proyectoOTModel.setProyectoOtVo((ProyectoOtVo) proyectoOTModel.getLista().getRowData());
        if (this.proyectoOTModel.abrirProyectoOT()) {
            FacesUtils.addInfoMessage("Se abrio el proyecto OT correctamente");
            if (!proyectoOTModel.getCadena().isEmpty()) {
                proyectoOTModel.setLista(new ListDataModel(proyectoOTModel.traerProyectoOTPorCadena(proyectoOTModel.getCadena())));
            } else {
                proyectoOTModel.llenarLista();
            }

            proyectoOTModel.setListaG(null);

            //limpiar(event);
        } else {
            FacesUtils.addInfoMessage("Existio un error al intentar abrir el proyecto OT ");
        }

    }

//    public void activarProyectoOT() {
//        this.proyectoOt.setVisible("True");
//        this.proyectoOTModel.elminarProyectoOT(proyectoOt);
//    }
    public void limpiar() {

        proyectoOTModel.setCuentaContable("");
        proyectoOTModel.setNombreProyectoOT("");
        proyectoOTModel.setProyectoOtVo(null);// null;
        proyectoOTModel.setListaG(null);// = null;
        proyectoOTModel.setMostrar(false);
        proyectoOTModel.llenarLista();
    }

    public void guardarProyectoOT() {
        UtilLog4j.log.info(this, "Nombre . . . . . ");
        if (!getNombreProyectoOT().isEmpty()) {
            if (!getCuentaContable().isEmpty()) {
                proyectoOTModel.guardarProyectoOT();
                proyectoOTModel.llenarLista();
                proyectoOTModel.setCuentaContable("");
                proyectoOTModel.setNombreProyectoOT("");
                proyectoOTModel.setMostrar(false);
                proyectoOTModel.setListaG(null);// = null;

                UtilLog4j.log.info(this, "Se guardo correctamente ");
            } else {
                FacesUtils.addInfoMessage("Por favor agrege el numero de cuenta contable");
            }
        } else {
            FacesUtils.addInfoMessage("Por favor agrege el nombre de proyecto ot");
        }
    }

    public void agregarRelacionProyectoGerencia() {
        proyectoOTModel.setGerencia(-1);
        proyectoOTModel.setMostrarPanelRelacion(true);
    }

    public List getTraerGerencia() {
        List<SelectItem> listaItem = new ArrayList<SelectItem>();
        List<GerenciaVo> listaGerencia = this.proyectoOTModel.traerGerencia();
        try {
            for (GerenciaVo gerenciaProyecto : listaGerencia) {
                listaItem.add(new SelectItem(gerenciaProyecto.getId(), gerenciaProyecto.getNombre()));
            }
            return listaItem;
        } catch (Exception e) {
            return null;
        }
    }

    public void ocultarPanelRelGerencia() {
        proyectoOTModel.setMostrarPanelRelacion(false);
    }

    public void guardarRelacionGerencia() {
        //buscar repetidos
        if (this.proyectoOTModel.buscarRelacionProyectoOtGerencia()) {
            //FacesUtils.addErrorMessage("Ya existe una relación con la gerencia seleccionada..");
            PrimeFaces.current().executeScript(";alertaGeneral('Ya existe relación la gerencia seleccionada..');");
        } else {
            this.proyectoOTModel.guardarRelacionGerencia();
            proyectoOTModel.setMostrarPanelRelacion(false);
            proyectoOTModel.setGerencia(-1);
            PrimeFaces.current().executeScript(";dialogoOK('dialogoProyGer');");
        }
    }

    public void eliminarRelacionGerencia() {
        proyectoOTModel.setGerenciaTarea((OcTareaVo) proyectoOTModel.getListaG().getRowData());
        UtilLog4j.log.info(this, "Id de gerencia a eliminar " + proyectoOTModel.getGerenciaTarea().getIdGerencia());
        proyectoOTModel.eliminarRelacionGerencia();
    }

    public void seleccionarGerencia() {
        proyectoOTModel.setGerenciaTarea((OcTareaVo) proyectoOTModel.getListaG().getRowData());
        UtilLog4j.log.info(this, "Id de gerencia " + proyectoOTModel.getGerenciaTarea().getIdGerencia());
        proyectoOTModel.setTareaVo(null);
    }

    public void agregarRelacionProyectoTipoObra() {
        proyectoOTModel.setTipoO(-1);
        proyectoOTModel.setMostrarPanelRelTipoObra(true);// true;
    }

    public DataModel getTraerRelacionProyectoUnidadCosto() {
        try {
            if (proyectoOTModel.getProyectoOtVo() != null && proyectoOTModel.getGerenciaTarea() != null) {
                proyectoOTModel.setListaTipoObra(new ListDataModel(this.proyectoOTModel.traerRelacionProyectoUnidadCosto()));
                return proyectoOTModel.getListaTipoObra();
            } else {
                return null;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio una excepcion al traer las unidades de costo . . . " + e.getMessage());
            return null;
        }
    }

    public DataModel getTraerTareas() {
        try {
            if (proyectoOTModel.getProyectoOtVo() != null && proyectoOTModel.getGerenciaTarea() != null && proyectoOTModel.getUnidadCostoTarea() != null) {
                proyectoOTModel.setListaTipo(new ListDataModel(proyectoOTModel.traerTareas()));
                return proyectoOTModel.getListaTipo();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public List getTraerTipoObra() {
        List<SelectItem> listaItem = new ArrayList<SelectItem>();
        List<Vo> listaTipo = this.proyectoOTModel.traerTipoObraVo();
        try {
            for (Vo t : listaTipo) {
                listaItem.add(new SelectItem(t.getId(), t.getNombre()));
            }
            return listaItem;
        } catch (Exception e) {
            return null;
        }
    }

    public void ocultarAgregarRelProyObra() {
        proyectoOTModel.setMostrarPanelRelTipoObra(false);
    }

    public void guardarRelacionTipoObra() {
        if (proyectoOTModel.getTipoO() > 0) {
            if (this.proyectoOTModel.buscarRelacionProyectoTipoObra()) {
                //FacesUtils.addErrorMessage("Ya existe una relación con el tipo de obra seleccionada..");
                PrimeFaces.current().executeScript(";alertaGeneral('Ya existe relación con el tipo de obra seleccionada..');");
            } else {
                this.proyectoOTModel.guardarRelacionTipoObra();
                proyectoOTModel.setMostrarPanelRelTipoObra(false);
                proyectoOTModel.setTipoO(-1);
                PrimeFaces.current().executeScript(";dialogoOK('dialogoProyectoOTTipoObra');");
            }
        } else {
            FacesUtils.addInfoMessage("Por favor seleccione un tipo de obra.");
        }
        proyectoOTModel.setMostrarPanelTipoObra(false);
    }

    public void eliminarRelacionUnidadCosto() {
        proyectoOTModel.setUnidadCostoTarea((OcTareaVo) proyectoOTModel.getListaTipoObra().getRowData());
        if (this.proyectoOTModel.eliminarRelacionUnidadCosto()) {
            FacesUtils.addInfoMessage("Se eliminó correctamente la relación con el tipo de tarea");
        } else {
            FacesUtils.addInfoMessage("Extió un error al intentar eliminar la relacion con el tipo de tarea");
        }
    }

    public void seleccionarTipoTarea() {
        proyectoOTModel.setUnidadCostoTarea((OcTareaVo) proyectoOTModel.getListaTipoObra().getRowData());
    }

    public void eliminarTarea() {
        proyectoOTModel.setTareaVo((OcTareaVo) proyectoOTModel.getListaTipo().getRowData());
        if (this.proyectoOTModel.eliminarTarea()) {
            FacesUtils.addInfoMessage("Se eliminó correctamente la relación con tipo de obra ");
        } else {
            FacesUtils.addInfoMessage("Extió un error al intentar eliminar la relacion con tipo de obra ");
        }
    }
    
    
    public void mostrarAgregarTipoObra() {
        proyectoOTModel.setMostrarPanelTipoObra(true);
//        this.tipoObra = new TipoObra();
    }

    public void ocultarPanelTipoObra() {
        proyectoOTModel.setMostrarPanelTipoObra(false);
    }

    public void guardarTipoObra() {
        proyectoOTModel.setTipoObra(proyectoOTModel.buscarTipoObra());
        if (proyectoOTModel.getTipoObra() == null) {
            this.proyectoOTModel.guardarTipoObra();
            proyectoOTModel.setMostrarPanelTipoObra(false);
            proyectoOTModel.setNombreTipoObra("");
            PrimeFaces.current().executeScript(";dialogoOK('dialogoAgregarObra');");
        } else {
            PrimeFaces.current().executeScript(";alertaGeneral('La obra ya está en la base de datos..');");
//            FacesUtils.addInfoMessage("La obra ya está en la base de datos");
        }
    }

    public DataModel getTraerObra() {
        try {
            proyectoOTModel.setListaTipo(new ListDataModel(this.proyectoOTModel.traerTipoObra()));
            return proyectoOTModel.getListaTipo();//.listaTipo;
        } catch (Exception e) {
            return null;
        }
    }
    /*
     * public void activarTipoObra() { tObra = (TipoObra)
     * this.listaTipo.getRowData(); tObra.setVisible("True");
     * this.proyectoOTModel.actualizarTipoObra(tObra);
     * proyectoOTModel.setObra(""); proyectoOTModel.setTipoObra(null); }
     */
    ////*************************************************************////

    //AUTOCOMPLETE
    public void proyectoOtListener(ValueChangeEvent textChangeEvent) {
        if (textChangeEvent.getComponent() instanceof SelectInputText) {
            SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
            String cadenaDigitada = (String) textChangeEvent.getNewValue();
            this.proyectoOTModel.cargarListaProyectosOtItems(cadenaDigitada);
            if (autoComplete.getSelectedItem() != null) {
                UtilLog4j.log.info(this, "Seleccion " + (ProyectoOtVo) autoComplete.getSelectedItem().getValue());
                autoComplete.getSelectedItem().getValue();
                ProyectoOtVo vo = (ProyectoOtVo) autoComplete.getSelectedItem().getValue();
//              buscar proyecto ot                
                setCuentaContable(vo.getCuentaContable());
                //buscarProyectoOt();
            }
        }
    }
    /*
     * public String buscarProyectoOt() { if (!getCuentaContable().equals("")) {
     * List<ProyectoOtVo> l =
     * this.proyectoOTModel.buscarProyectoOT(getCuentaContable()); this.lista =
     * new ListDataModel(l); if (this.lista.getRowCount() == 0) { this.lista =
     * null; this.proyectoOt = null; this.mostrar = true; this.listaG = null;
     * this.panelBuscar = false; } else { this.mostrar = false; this.proyectoOt
     * = null; this.proyectoOt =
     * proyectoOTModel.buscarProyectoOT(l.get(0).getId()); this.listaG = null;
     * // nombreProyectoOT = ""; } } else { FacesUtils.addErrorMessage("Por
     * favor seleccione un proyecto de la lista desplegable"); } return ""; }
     */

    //CAMBIAR CAMPO    
    public void cambiarValorCampo(ValueChangeEvent valueChangeEvent) {
        proyectoOTModel.setIdApCampoActivo((Integer) valueChangeEvent.getNewValue());
        UtilLog4j.log.info(this, "campo seleccionado : " + proyectoOTModel.getIdApCampoActivo());
        proyectoOTModel.llenarLista();
        proyectoOTModel.setProyectoOtVo(null);
        proyectoOTModel.setPanelBuscar(false);
        proyectoOTModel.setCuentaContable("");

    }

    public void cerrarPupupAltaProyectoOt() {
        proyectoOTModel.setCuentaContable("");
        proyectoOTModel.setNombreProyectoOT("");
        proyectoOTModel.setMostrar(false);
    }

    public String getNombreTipoObra() {
        return proyectoOTModel.getNombreTipoObra();//nombreTipoObra;
    }

    public void setNombreTipoObra(String nombreTipoObra) {
        proyectoOTModel.setNombreTipoObra(nombreTipoObra);//.nombreTipoObra = nombreTipoObra;
    }

    public boolean isMostrarPanelTipoObra() {
        return proyectoOTModel.isMostrarPanelTipoObra();//mostrarPanelTipoObra;
    }

    public void setMostarPanelTipoObra(boolean mostrarPanelTipoObra) {
        proyectoOTModel.setMostrarPanelTipoObra(mostrarPanelTipoObra);//this.mostrarPanelTipoObra = mostrarPanelTipoObra;
    }

    public boolean isMostrarPanelRelTipoObra() {
        return proyectoOTModel.isMostrarPanelRelTipoObra();// mostrarPanelRelTipoObra;
    }

    public void setMostrarPanelRelTipoObra(boolean mostrarPanelRelTipoObra) {
        proyectoOTModel.setMostrarPanelRelTipoObra(mostrarPanelRelTipoObra);//this.mostrarPanelRelTipoObra = mostrarPanelRelTipoObra;
    }

    public String getObra() {
        return proyectoOTModel.getObra();
    }

    public void setObra(String obra) {
        proyectoOTModel.setObra(obra);
    }

    public int getTipoO() {
        return proyectoOTModel.getTipoO();
    }

    public void setTipoO(int tipoO) {
        proyectoOTModel.setTipoO(tipoO);
    }

    public TipoObra getTipoObra() {
        return proyectoOTModel.getTipoObra();
    }

    public void setTipoObra(TipoObra tipoObra) {
        proyectoOTModel.setTipoObra(tipoObra);
    }

    public int getGerencia() {
        return proyectoOTModel.getGerencia();
    }

    public void setGerencia(int gerencia) {
        proyectoOTModel.setGerencia(gerencia);
    }

    public boolean isMostrarPanelRelacion() {
        return proyectoOTModel.isMostrarPanelRelacion();
    }

    public void setMostrarPanelRelacion(boolean mostrarPanelRelacion) {
        proyectoOTModel.setMostrarPanelRelTipoObra(mostrarPanelRelacion);
    }

    public boolean isMostrar() {
        return proyectoOTModel.isMostrar();
    }

    public void setMostrar(boolean mostrar) {
        proyectoOTModel.setMostrar(mostrar);
    }

    public String getCuentaContable() {
        return proyectoOTModel.getCuentaContable();
    }

    public void setCuentaContable(String cuentaContable) {
        proyectoOTModel.setCuentaContable(cuentaContable);
    }

    public void setProyectoOTModel(ProyectoOTModel proyectoOTModel) {
        this.proyectoOTModel = proyectoOTModel;
    }

    public String getNombreProyectoOT() {
        return proyectoOTModel.getNombreProyectoOT();//ombreProyectoOT;
    }

    public void setNombreProyectoOT(String nombreProyectoOT) {
        proyectoOTModel.setNombreProyectoOT(nombreProyectoOT);
    }

    public boolean isPanelBuscar() {
        return proyectoOTModel.isPanelBuscar();
    }

    public void setPanelBuscar(boolean panelBuscar) {
        proyectoOTModel.setPanelBuscar(panelBuscar);
    }

    public boolean isMostrarPop() {
        return proyectoOTModel.isMostrarPop();
    }

    public void setMostrarPop(boolean mostrarPop) {
        proyectoOTModel.setMostrarPop(mostrarPop);
    }

    public void clearComponent(String nombreFormulario, String nombreComponente) {
        UtilLog4j.log.info(this, "Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
            UIComponent parentComponent = component.getParent();
            parentComponent.getChildren().clear();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
        }
    }

    /**
     * @return the listaOrden
     */
    public DataModel getListaOrden() {
        return proyectoOTModel.getListaOrden();//listaOrden;
    }

    /**
     * @param listaOrden the listaOrden to set
     */
    public void setListaOrden(DataModel listaOrden) {
        proyectoOTModel.setListaOrden(listaOrden);
    }

    /**
     * @return the listaProyectosOtItems
     */
    public List<SelectItem> getListaProyectosOtItems() {
        return proyectoOTModel.getListaProyectosOtItems();
    }

    /**
     * @param listaProyectosOtItems the listaProyectosOtItems to set
     */
    public void setListaProyectosOtItems(List<SelectItem> listaProyectosOtItems) {
        this.proyectoOTModel.setListaProyectosOtItems(listaProyectosOtItems);
    }

    public int getIdApCampoActivo() {
        return proyectoOTModel.getIdApCampoActivo();
    }

    /**
     * @param idApCampoActivo the idApCampoActivo to set
     */
    public void setIdApCampoActivo(int idApCampoActivo) {
        this.proyectoOTModel.setIdApCampoActivo(idApCampoActivo);
    }

    public String getNombreCampo() {
        return proyectoOTModel.getNombreCampo();
    }

    /**
     * @param nombreCampo the nombreCampo to set
     */
    public void setNombreCampo(String nombreCampo) {
        this.proyectoOTModel.setNombreCampo(nombreCampo);
    }

    /**
     * @return the proyectoOtVo
     */
    public ProyectoOtVo getProyectoOtVo() {
        return proyectoOTModel.getProyectoOtVo();
    }

    /**
     * @param proyectoOtVo the proyectoOtVo to set
     */
    public void setProyectoOtVo(ProyectoOtVo proyectoOtVo) {
        proyectoOTModel.setProyectoOtVo(proyectoOtVo);
    }

    /**
     * @return the cadena
     */
    public String getCadena() {
        return proyectoOTModel.getCadena();
    }

    /**
     * @param cadena the cadena to set
     */
    public void setCadena(String cadena) {
        proyectoOTModel.setCadena(cadena);
    }
}
