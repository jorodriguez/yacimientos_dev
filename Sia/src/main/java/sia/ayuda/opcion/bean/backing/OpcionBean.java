/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ayuda.opcion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.tree.DefaultTreeModel;
import sia.ayuda.opcion.bean.model.ArbolOpcionesBean;
import sia.ayuda.opcion.bean.model.OpcionBeanModel;
import sia.modelo.SiModulo;
import sia.modelo.SiOpcion;
import sia.excepciones.SIAException;
import sia.modelo.SiRol;
import sia.modelo.rol.vo.RolVO;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.sistema.bean.backing.GenericPanelPopup;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author sluis
 */
@ManagedBean (name="opcionBean")
@ViewScoped
public class OpcionBean implements Serializable{
    
    @ManagedProperty(value = "#{opcionBeanModel}")
    private OpcionBeanModel opcionBeanModel;    
    @ManagedProperty(value = "#{genericPanelPopup}")
    private GenericPanelPopup popup;
     @Inject
    private ArbolOpcionesBean arbol;
    
  /*  public void goToCatalogoOpciones() {
        getOpcionBeanModel().beginConversacionCatalogoOpciones();
        //return "/vistas/administracion/opcion/catalogoOpciones";
    }*/
    
  /*  public DataModel getOpcionesFiltradasByModuloDataModel() {
        getOpcionBeanModel().getAllOpcionesActivas();
        return getOpcionBeanModel().getOpcionesFiltradasByModuloDataModel();
    }*/
    public DataModel getOpcionesFiltradasInTabla() {
        getOpcionesFiltradas();
        if(getNombreOpcionSeleccionada() !=null && !getNombreOpcionSeleccionada().equals("") ){
              getOpcionBeanModel().reloadAllopcionBySiOpcion();
        }else if(getNombreModuloSeleccionado()!=null && !getNombreModuloSeleccionado().equals("")){
          getOpcionBeanModel().reloadAllOpcionesByModulo();
        } 
    
        return getOpcionBeanModel().getOpcionesFiltradas();
    }
     public DataModel getOpcionesFiltradas() {
       
        if(arbol.getOpcionSeleccionada()!=null && !arbol.getOpcionSeleccionada().equals("") ){
           setNombreOpcionSeleccionada(  arbol.getOpcionSeleccionada());
       setNombreModuloSeleccionado( arbol.getModSelecionado());
          getOpcionBeanModel().getAllOpcionesActivasBySiOpcion();
             
        } 
        else if(arbol.getModSelecionado() !=null && !arbol.getModSelecionado().equals("")) {
           setNombreModuloSeleccionado( arbol.getModSelecionado());
            setNombreOpcionSeleccionada(  "");
          getOpcionBeanModel().getAllOpcionesActivasByModulo();
        }
        
        return getOpcionBeanModel().getOpcionesFiltradas();
    }
        //JVG
    public void cargarRol(ValueChangeEvent event){
         System.out.println("---------> Rol selecionado: " + event.getNewValue());
         getOpcionBeanModel().setNombreRolSeleccionado(event.getNewValue().toString());   
    }
     public void cargarRolSelecionado(ValueChangeEvent event){
         System.out.println("---------> Rol selecionado: " + event.getNewValue());
         getOpcionBeanModel().setNombreRolSeleccionado(event.getNewValue().toString());
      
    }
     public void cargarOpcionSelcionada(ValueChangeEvent event){
        System.out.println("---------> opcion selecionada: " + event.getNewValue());
        setNombreSiOpcionSeleccionada(event.getNewValue().toString());
    }
    
    public List<SelectItem> getRolListItem() {
        System.out.println("Fue a llenar el Combo rol");
        List<RolVO> rolesTmp = getOpcionBeanModel().getAllRolesByModuloActivos();
        List<SelectItem> rolListItem = new ArrayList<SelectItem>();
        
        for(RolVO op : rolesTmp) {
            SelectItem item = new SelectItem(op.getNombre());
            rolListItem.add(item);
        }
        return rolListItem;
    }
    public List<SelectItem> getAllRolListItem() {
        System.out.println("Fue a llenar el Combo rol-----");
        List<SiRol> rolesTmp = getOpcionBeanModel().getAllRoles();
        List<SelectItem> rolListItem = new ArrayList<SelectItem>();
        
        for(SiRol op : rolesTmp) {
            SelectItem item = new SelectItem(op.getNombre());
            rolListItem.add(item);
        }
        return rolListItem;
    }
    
    public List<SelectItem> getOpcionListItem() {
        System.out.println("Fue a llenar el Combo opciones");
        List<SiOpcionVo> opcionesTmp = getOpcionBeanModel().getAllOpciones();
        List<SelectItem>opcionesListItem = new ArrayList<SelectItem>();
        
        for(SiOpcionVo op : opcionesTmp) {
            SelectItem item = new SelectItem(op.getNombre());
           opcionesListItem.add(item);
        }
        return opcionesListItem;
    }
    
    public SiOpcion getOpcionSeleccionada() {
        return getOpcionBeanModel().getOpcionSeleccionada();
    }
    
    public void setOpcionSeleccionada(SiOpcionVo opcionSeleccionada) {
        getOpcionBeanModel().setOpcionSeleccionada(opcionSeleccionada);
    }
 
    /**
     * @return the nombreOpcion
     */
    public String getNombreOpcion() {
        return getOpcionBeanModel().getNombreOpcion();
    }

    /**
     * @param nombreOpcion the nombreOpcion to set
     */
    public void setNombreOpcion(String nombreOpcion) {
        System.out.println("Se esta asignando una Opcion. OpcionBean.java 78" );
        UtilLog4j.log.info(this, "Se esta asignando una Opcion. OpcionBean.java 78");
        getOpcionBeanModel().setNombreOpcion(nombreOpcion);
    }    
    
    /**
     * @return the paginaOpcion
     */
    public String getPaginaOpcion() {
        return getOpcionBeanModel().getPaginaOpcion();
    }

    /**
     * @param paginaOpcion the paginaOpcion to set
     */
    public void setPaginaOpcion(String paginaOpcion) {
        getOpcionBeanModel().setPaginaOpcion(paginaOpcion);
    }
      /**
     * @return the estatusContar
     */
    public String getEstatusContar() {
        return getOpcionBeanModel().getEstatusContar();
    }
    
    /**
     * @param estatusContar the paginaOpcion to set
     */
    public void setEstatusContar(String estatusContar) {
        getOpcionBeanModel().setEstatusContar(estatusContar);
    }
    
    /**
     * @return the nombreModuloSeleccionado
     */
    public String getNombreModuloSeleccionado() {
        return getOpcionBeanModel().getNombreModuloSeleccionado();
    }

    /**
     * @param nombreModuloSeleccionado the nombreModuloSeleccionado to set
     */
    public void setNombreModuloSeleccionado(String nombreModuloSeleccionado) {
        getOpcionBeanModel().setNombreModuloSeleccionado(nombreModuloSeleccionado);
    }    
    
    public String getNombreRolSeleccionado() {
        return getOpcionBeanModel().getNombreRolSeleccionado();
    }
    
    public void setNombreRolSeleccionado(String nombreRolSeleccionado) {
        getOpcionBeanModel().setNombreRolSeleccionado(nombreRolSeleccionado);
    }
    
     public String getNombreOpcionSeleccionada() {
        return getOpcionBeanModel().getNombreOpcionSeleccionada();
    }
    
    public void setNombreOpcionSeleccionada(String nombreOpcionSeleccionado) {
        getOpcionBeanModel().setNombreOpcionSeleccionada(nombreOpcionSeleccionado);
    }
    
    public void mostrarPopupCrearOpcion() {
        System.out.println("mostrarPopupCrearOpcion");
        setNombreModuloSeleccionado(arbol.getModSelecionado());
        System.out.println("nombreModuloSeleccionado: " + getOpcionBeanModel().getNombreModuloSeleccionado());
        UtilLog4j.log.info(this, "mostrarPopupCrearOpcion");
        UtilLog4j.log.info(this, "nombreModuloSeleccionado: " + getOpcionBeanModel().getNombreModuloSeleccionado());
        if(getOpcionBeanModel().getNombreModuloSeleccionado() != null && !opcionBeanModel.getNombreModuloSeleccionado().equals("") && !opcionBeanModel.getNombreModuloSeleccionado().equals("-1")) {
            getOpcionBeanModel().setModalRenderedCrearOpcion(!opcionBeanModel.isModalRenderedCrearOpcion());
        }
        else {
            FacesUtils.addInfoMessage("No has seleccionado ningún módulo");
        }
    }
    
    public void mostrarPopupActualizar() {
        System.out.println("mostrarPopupActualizar");
        UtilLog4j.log.info(this, "mostrarPopupActualizar");
        String name=getOpcionBeanModel().getOpcionesFiltradas().getRowData().getNombre();
        String pagina=getOpcionBeanModel().getOpcionesFiltradas().getRowData().getPagina();
        getOpcionBeanModel().setNombreOpcion(name);
        getOpcionBeanModel().setPaginaOpcion(pagina);
        getOpcionBeanModel().setOpcionSeleccionada(getOpcionBeanModel().getOpcionesFiltradas().getRowData());
        getPopup().toogleModalActualizar(actionEvent);
    }
    
    public void mostrarPopupEliminar() {
        System.out.println("mostrarPopupEliminar");
        UtilLog4j.log.info(this, "mostrarPopupEliminar");
        getOpcionBeanModel().setOpcionSeleccionada(getOpcionBeanModel().getOpcionesFiltradas().getRowData());
        UtilLog4j.log.info(this, "mostrarPopupEliminar");
        getPopup().toogleModalElimnar(actionEvent);
    }
    
    public void mostrarPopupCrearModulo() {
        getOpcionBeanModel().setMrPopupCrearModulo(!opcionBeanModel.isMrPopupCrearModulo());
    }
    
    public void mostrarPopupCrearRol() {
        getOpcionBeanModel().setMrPopupCrearRol(!opcionBeanModel.isMrPopupCrearRol());
    }
    
    public void ocultarPopupCrearModulo() {
        getOpcionBeanModel().setNombreOpcion("");
        getOpcionBeanModel().setPaginaOpcion("");
        getOpcionBeanModel().setMrPopupCrearModulo(!opcionBeanModel.isMrPopupCrearModulo());
    }
    
     public void ocultarPopupCrearRol() {
        getOpcionBeanModel().setNombreOpcion("");
        getOpcionBeanModel().setPaginaOpcion("");
        getOpcionBeanModel().setMrPopupCrearRol(!opcionBeanModel.isMrPopupCrearRol());
    }
    
    public void crearOpcion() throws Exception {
        if (getOpcionBeanModel().getNombreOpcion() != null && !opcionBeanModel.getNombreOpcion().trim().equals("")) {
            System.out.println();
                try {
                    getOpcionBeanModel().crearOpcion();
                    getOpcionBeanModel().setEstatusContar("0");
                    arbol.ReloadArbolOpciones( );
                    getOpcionBeanModel().setNombreRolSeleccionado("-1");
                    cancelarCrearOpcion(actionEvent);
                } catch (Exception e) {
                    System.out.println("Mensaje Exception: " + e.getMessage());
                    if (getOpcionBeanModel().getPaginaOpcion()!=null && !opcionBeanModel.getPaginaOpcion().equals("")){
                    UtilLog4j.log.fatal(this, "Mensaje Exception: " + e.getMessage());
                    e.printStackTrace();
                    if(e.getMessage() == null || e.getMessage().equals("")) {
                        FacesUtils.addInfoMessage(new SIAException().getMessage());
                    }
                    else {
                        FacesUtils.addInfoMessage(e.getMessage());
                    }
                }
             }
        } else {
            FacesUtils.addInfoMessage("El nombre de la opción no puede ser vacío");
        }
    }
    
    public void actualizarOpcion() {
        if (getOpcionBeanModel().getNombreOpcion() != null && !opcionBeanModel.getNombreOpcion().trim().equals("")) {
                try {
                    getOpcionBeanModel().actualizarOpcion();
                    arbol.ReloadArbolOpciones( );
                    cancelarActualizarOpcion(actionEvent);
                } catch (Exception e) {
                    System.out.println("Mensaje Exception: " + e.getMessage());
                    if (getOpcionBeanModel().getPaginaOpcion()!=null && !opcionBeanModel.getPaginaOpcion().equals("")){
                    UtilLog4j.log.fatal(this, "Mensaje Exception: " + e.getMessage());
                    if(e.getMessage() == null || e.getMessage().equals("")) {
                        FacesUtils.addInfoMessage(new SIAException().getMessage());
                    }
                    else {
                        FacesUtils.addInfoMessage(e.getMessage());
                    }
                }
            }
        } else {
            FacesUtils.addInfoMessage("El nombre de la opción no puede ser vacío");
        }
    }
    
    public void eliminarOpcion() {
        try {
            getOpcionBeanModel().eliminarOpcion();
            arbol.ReloadArbolOpciones( );
            cancelarEliminarOpcion(actionEvent);
        } catch (Exception e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }
    
    public void crearModulo() {
        try {
            getOpcionBeanModel().crearModulo();
            arbol.ReloadArbolOpciones( );
            ocultarPopupCrearModulo(actionEvent);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            FacesUtils.addInfoMessage(new SIAException().getMessage());
        }
    }
    
    public void crearRol() {
        try {
            getOpcionBeanModel().crearModulo();
            arbol.ReloadArbolOpciones( );
            ocultarPopupCrearModulo(actionEvent);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            FacesUtils.addInfoMessage(new SIAException().getMessage());
        }
    }
    
    public void cancelarCrearOpcion() {
        getOpcionBeanModel().setNombreOpcion(null);
        getOpcionBeanModel().setPaginaOpcion(null);
        getOpcionBeanModel().setModalRenderedCrearOpcion(!opcionBeanModel.isModalRenderedCrearOpcion());
    }
    
    public void cancelarActualizarOpcion() {
        getOpcionBeanModel().setNombreOpcion(null);
        getOpcionBeanModel().setPaginaOpcion(null);
        getPopup().toogleModalActualizar(actionEvent);
    }
    
    public void cancelarEliminarOpcion() {
        getOpcionBeanModel().setNombreOpcion(null);
        getOpcionBeanModel().setPaginaOpcion(null);
        getPopup().toogleModalElimnar(actionEvent);
    }
    
    /**
     * @return the modalRenderedCrearOpcion
     */
    public boolean isModalRenderedCrearOpcion() {
        return getOpcionBeanModel().isModalRenderedCrearOpcion();
    }

    /**
     * @param modalRenderedCrearOpcion the modalRenderedCrearOpcion to set
     */
    public void setModalRenderedCrearOpcion(boolean modalRenderedCrearOpcion) {
        getOpcionBeanModel().setModalRenderedCrearOpcion(modalRenderedCrearOpcion);
    }  
    
    /**
     * @return the mrPopupCrearModulo
     */
    public boolean isMrPopupCrearModulo() {
        return getOpcionBeanModel().isMrPopupCrearModulo();
    }
    
     public boolean isMrPopupCrearRol() {
        return getOpcionBeanModel().isMrPopupCrearRol();
    }

    /**
     * @return the popup
     */
    public GenericPanelPopup getPopup() {
        return popup;
    }

    /**
     * @param popup the popup to set
     */
    public void setPopup(GenericPanelPopup popup) {
        this.popup = popup;
    }

    /**
     * @return the opcionBeanModel
     */
    public OpcionBeanModel getOpcionBeanModel() {
        return opcionBeanModel;
    }

    /**
     * @param opcionBeanModel the opcionBeanModel to set
     */
    public void setOpcionBeanModel(OpcionBeanModel opcionBeanModel) {
        this.opcionBeanModel = opcionBeanModel;
    }
    /**
     * @return the selecionada
     */
    public boolean isSelecionada() {
        return opcionBeanModel.isSelecionada();
    }

    /**
     * @param selecionada the selecionada to set
     */
    public void setSelecionada(boolean selecionada) {
        this.opcionBeanModel.setSelecionada(selecionada) ;
    }
    public void filtrarTablaByArbol(ValueChangeEvent event){
        System.out.println(event.getNewValue().toString());
    }
  
/**
     * @return the disableSiOpcion
     */
    public boolean isDisableSiOpcion() {
        return opcionBeanModel.isDisableSiOpcion();
    }

    /**
     * @param disableSiOpcion the disableSiOpcion to set
     */
    public void setDisableSiOpcion(boolean disableSiOpcion) {
        this.opcionBeanModel.setDisableSiOpcion( disableSiOpcion);
    }
    
    /**
     * @return the disableSiRol
     */
     public boolean isDisableSiRol() {
        return opcionBeanModel.isDisableSiRol();
    }

    /**
     * @param disableSiRol the disableSiOpcion to set
     */
    public void setDisableSiRol(boolean disableSiRol) {
        this.opcionBeanModel.setDisableSiRol( disableSiRol);
    }
      public void addRol(){
        try{
        for (Object object  : getOpcionBeanModel().getOpcionesFiltradas()) {
            SiOpcionVo vo=(SiOpcionVo)object;
                        if (vo.isSelected()) {
                System.out.println("como regreso el true "+vo.getNombre());
                getOpcionBeanModel().insertarRelacionRolOpcion(vo);
                            
            } else{
                 System.out.println(vo.isSelected()+" <----------------"+ vo.getNombre());
            }                               
        }
        } 
        catch (Exception e){
            System.out.println("fallo en "+ e);
        }
    }
      
      /**
     * @return the nombreSiOpcionSeleccionada
     */
       public String getNombreSiOpcionSeleccionada() {
        return getOpcionBeanModel().getNombreSiOpcionSeleccionada();
    }
    
       /**
     * @param nombreSiOpcionSeleccionada the nombreSiOpcionSeleccionada to set
     */
    public void setNombreSiOpcionSeleccionada(String nombreOpcionSeleccionado) {
        getOpcionBeanModel().setNombreSiOpcionSeleccionada(nombreOpcionSeleccionado);
    }
    
    public DefaultTreeModel getArbol(){
        getOpcionBeanModel().ÁrbolOpcionBean();
        return getOpcionBeanModel().getArbolOpciones();
        
    }
}
