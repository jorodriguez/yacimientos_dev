/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.cadena.aprobacion.vo;

import lombok.Getter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author mluis
 */

public class CadenaAprobacionVo extends Vo{    
    private String idRevisa;
    private int idCampo;
    private String idSolicita;
    private String idAprueba;
    private String solicita;
    private String revisa;
    private String aprueba;
    private boolean selected;
 
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
     * @return the idSolicita
     */
    public String getIdSolicita() {
        return idSolicita;
    }

    /**
     * @param idSolicita the idSolicita to set
     */
    public void setIdSolicita(String idSolicita) {
        this.idSolicita = idSolicita;
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
     * @return the solicita
     */
    public String getSolicita() {
        return solicita;
    }

    /**
     * @param solicita the solicita to set
     */
    public void setSolicita(String solicita) {
        this.solicita = solicita;
    }

    /**
     * @return the revisa
     */
    public String getRevisa() {
        return revisa;
    }

    /**
     * @param revisa the revisa to set
     */
    public void setRevisa(String revisa) {
        this.revisa = revisa;
    }

    /**
     * @return the aprueba
     */
    public String getAprueba() {
        return aprueba;
    }

    /**
     * @param aprueba the aprueba to set
     */
    public void setAprueba(String aprueba) {
        this.aprueba = aprueba;
    }

    /**
     * @return the idCampo
     */
    public int getIdCampo() {
        return idCampo;
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
        this.idCampo = idCampo;
    }

    /**
     * @return the select
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the select to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    
}
