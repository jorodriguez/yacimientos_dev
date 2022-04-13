/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.vo;

import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author b75ckd35th
 */
public class ApCompaniaGerenciaVo extends Vo {

    private int idGerencia;
    private String nombreCompania;
    private String rfcCompania;
    private String nombreGerencia;

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
     * @return the rfcCompania
     */
    public String getRfcCompania() {
        return rfcCompania;
    }

    /**
     * @param rfcCompania the rfcCompania to set
     */
    public void setRfcCompania(String rfcCompania) {
        this.rfcCompania = rfcCompania;
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

    /**
     * @return the nombreCompania
     */
    public String getNombreCompania() {
        return nombreCompania;
    }

    /**
     * @param nombreCompania the nombreCompania to set
     */
    public void setNombreCompania(String nombreCompania) {
        this.nombreCompania = nombreCompania;
    }

    
    public String toString() {
        return "ApCompaniaGerenciaVo{" + "idGerencia=" + idGerencia + ", rfcCompania=" + rfcCompania + ", nombreGerencia=" + nombreGerencia + ", nombreCompania=" + nombreCompania + '}';
    }
}
