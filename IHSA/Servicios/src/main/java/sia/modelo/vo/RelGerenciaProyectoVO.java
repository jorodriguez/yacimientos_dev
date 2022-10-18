/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.vo;

import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author jorodriguez
 */
public class RelGerenciaProyectoVO extends Vo{
    
    private Integer idProyectoOT;
    private String nombreProyectoOT;
    private Integer idGerencia;
    private String nombreGerencia;

    
    public RelGerenciaProyectoVO(){
        
    }
    
    /**
     * @return the idProyectoOT
     */
    public Integer getIdProyectoOT() {
        return idProyectoOT;
    }

    /**
     * @param idProyectoOT the idProyectoOT to set
     */
    public void setIdProyectoOT(Integer idProyectoOT) {
        this.idProyectoOT = idProyectoOT;
    }

    /**
     * @return the nombreProyectoOT
     */
    public String getNombreProyectoOT() {
        return nombreProyectoOT;
    }

    /**
     * @param nombreProyectoOT the nombreProyectoOT to set
     */
    public void setNombreProyectoOT(String nombreProyectoOT) {
        this.nombreProyectoOT = nombreProyectoOT;
    }

    /**
     * @return the idGerencia
     */
    public Integer getIdGerencia() {
        return idGerencia;
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(Integer idGerencia) {
        this.idGerencia = idGerencia;
    }

    /**
     * @return the nombreGerencia
     */
    public String getNombreGerencia() {
        return nombreGerencia;
    }

    /**
     * @param nombreGerencia the nombreGerencia to set
     */
    public void setNombreGerencia(String nombreGerencia) {
        this.nombreGerencia = nombreGerencia;
    }
    

    
}
