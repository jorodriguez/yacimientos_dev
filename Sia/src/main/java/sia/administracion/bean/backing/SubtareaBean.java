/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;
import sia.administracion.bean.model.SubtareaBeanModel;
import sia.modelo.requisicion.vo.OcCodigoSubTareaVO;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author jcarranza
 */
@ManagedBean(name = "subtareaBean")
@ViewScoped
public class SubtareaBean {
    @ManagedProperty(value = "#{subtareaBeanModel}")
    private SubtareaBeanModel subtareaBeanModel;

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;
    
    public SubtareaBean(){
    
    }
    @PostConstruct
    public void llenaCampo() {
        this.subtareaBeanModel.inicia();
    }
    
    /**
     * @param subtareaBeanModel the subtareaBeanModel to set
     */
    public void setSubtareaBeanModel(SubtareaBeanModel subtareaBeanModel) {
        this.subtareaBeanModel = subtareaBeanModel;
    }

    /**
     * @return the sesion
     */
    public Sesion getSesion() {
        return sesion;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }
    
        /**
     * @return the subVO
     */
    public OcCodigoSubTareaVO getSubVO() {
        return this.subtareaBeanModel.getSubVO();
    }

    /**
     * @param subVO the subVO to set
     */
    public void setSubVO(OcCodigoSubTareaVO subVO) {
        this.subtareaBeanModel.setSubVO(subVO);
    }

    /**
     * @return the lstSubtareas
     */
    public List<OcCodigoSubTareaVO> getLstSubtareas() {
        return this.subtareaBeanModel.getLstSubtareas();
    }

    /**
     * @param lstSubtareas the lstSubtareas to set
     */
    public void setLstSubtareas(List<OcCodigoSubTareaVO> lstSubtareas) {
        this.subtareaBeanModel.setLstSubtareas(lstSubtareas);
    }

    public void nuevaSubtarea(){
            this.subtareaBeanModel.nuevaSubtarea();
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoSubtarea);");    
    }
    
    
    public void editarSubtarea(){
    int idSubtarea = Integer.parseInt(FacesUtils.getRequestParameter("idSubtarea"));
        if (idSubtarea > 0) {
            this.subtareaBeanModel.setIdSubTarea(idSubtarea);
            this.subtareaBeanModel.editarSubtarea();
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoSubtarea);");
        }
    }
    
    public void borrarSubtarea(){
    int idSubtarea = Integer.parseInt(FacesUtils.getRequestParameter("idSubtarea"));
        if (idSubtarea > 0) {
            this.subtareaBeanModel.setIdSubTarea(idSubtarea);
            this.subtareaBeanModel.borrarSubtarea();         
            this.subtareaBeanModel.cargarSubtareas();
        }
    }
    
    public void guardarSubtarea(){    
            this.subtareaBeanModel.guardarSubtarea();
            this.subtareaBeanModel.cargarSubtareas();
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoSubtarea);");        
    }
    
}
