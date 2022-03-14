/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.catalogos.bean.model;

import java.io.Serializable;
import java.util.Date;
import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import sia.constantes.Constantes;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;

/**
 *
 * @author ihsa
 */
@ManagedBean(name = "reporteModel")
@CustomScoped(value = "#{window}")
public class ReporteModel implements Serializable {

    //EJB
    @EJB
    private SgViajeroImpl sgViajeroImpl;
    private SiManejoFechaImpl siManejoFechaLocal;

    /**
     * Creates a new instance of ReporteModel
     */
    public ReporteModel() {
    }
    private Date inicio = new Date(new Date().getYear(), new Date().getMonth(), 1);
    private Date fin = new Date();
    private DataModel lista;
    private DataModel datamodel;

    public void traerViajero() {
        
        String fechaInicio = Constantes.FMT_ddMMyyy.format(getInicio());
            String fechaFin = Constantes.FMT_ddMMyyy.format(getFin());
        setLista(new ListDataModel(sgViajeroImpl.viajerosPorFecha(fechaInicio, fechaFin, Constantes.ESTATUS_VIAJE_FINALIZAR, true)));
        
            setDatamodel(new ListDataModel(sgViajeroImpl.viajerosAreosPorFecha(fechaInicio,fechaFin, Constantes.ESTATUS_VIAJE_PROCESO)));
    }

    /**
     * @return the inicio
     */
    public Date getInicio() {
        return inicio;
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    /**
     * @return the fin
     */
    public Date getFin() {
        return fin;
    }

    /**
     * @param fin the fin to set
     */
    public void setFin(Date fin) {
        this.fin = fin;
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
        return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
        this.lista = lista;
    }

    /**
     * @return the datamodel
     */
    public DataModel getDatamodel() {
        return datamodel;
    }

    /**
     * @param datamodel the datamodel to set
     */
    public void setDatamodel(DataModel datamodel) {
        this.datamodel = datamodel;
    }
}
