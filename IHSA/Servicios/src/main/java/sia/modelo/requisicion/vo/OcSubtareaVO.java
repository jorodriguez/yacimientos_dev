/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.requisicion.vo;

/**
 *
 * @author jcarranza
 */
public class OcSubtareaVO {
    
    private int id;
    private int idCodigoSubtarea;
    private int idTarea;
    private int idProyectoOT;

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
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the idCodigoSubtarea
     */
    public int getIdCodigoSubtarea() {
        return idCodigoSubtarea;
    }

    /**
     * @param idCodigoSubtarea the idCodigoSubtarea to set
     */
    public void setIdCodigoSubtarea(int idCodigoSubtarea) {
        this.idCodigoSubtarea = idCodigoSubtarea;
    }

    /**
     * @return the idTarea
     */
    public int getIdTarea() {
        return idTarea;
    }

    /**
     * @param idTarea the idTarea to set
     */
    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }     
    
}
