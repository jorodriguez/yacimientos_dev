/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.catalogos.bean.backing;

import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import javax.faces.model.DataModel;
import sia.catalogos.bean.model.ReporteModel;
import sia.constantes.Constantes;

/**
 *
 * @author ihsa
 */
@ManagedBean(name = "reporteBean")
@RequestScoped
public class ReporteBean implements Serializable {

    /**
     * Creates a new instance of ReporteBean
     */
    @ManagedProperty(value = "#{reporteModel}")
    private ReporteModel reporteModel;
    //
    
    public String irReporteViajero(){
        reporteModel.setLista(null);        
            reporteModel.setDatamodel(null);        
        reporteModel.setFin(new Date());
        return "/vistas/recursos/reporteViajero";
    }

    public ReporteBean() {
    }

    public void traerViajero() {
        reporteModel.traerViajero();
    }
    

    /**
     * @return the inicio
     */
    public Date getInicio() {
        return reporteModel.getInicio();
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(Date inicio) {
        reporteModel.setInicio(inicio);
    }

    /**
     * @return the fin
     */
    public Date getFin() {
        return reporteModel.getFin();
    }

    /**
     * @param fin the fin to set
     */
    public void setFin(Date fin) {
        reporteModel.setFin(fin);
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
        return reporteModel.getLista();
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
        reporteModel.setLista(lista);
    }

    /**
     * @param reporteModel the reporteModel to set
     */
    public void setReporteModel(ReporteModel reporteModel) {
        this.reporteModel = reporteModel;
    }
    /**
     * @return the datamodel
     */
    public DataModel getDatamodel() {
        return reporteModel.getDatamodel();
    }

    /**
     * @param datamodel the datamodel to set
     */
    public void setDatamodel(DataModel datamodel) {
        reporteModel.setDatamodel(datamodel);
    }
}
