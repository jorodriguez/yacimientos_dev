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
public class RelProyectoTipoObraVO extends Vo{

    private String nombreProyectoOT;
    private String nombreTipoObra;
    
    public RelProyectoTipoObraVO(){
        
    }

    /**
     * @return the nombreTipoObra
     */
    public String getNombreTipoObra() {
        return nombreTipoObra;
    }

    /**
     * @param nombreTipoObra the nombreTipoObra to set
     */
    public void setNombreTipoObra(String nombreTipoObra) {
        this.nombreTipoObra = nombreTipoObra;
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
    
    

    
}
