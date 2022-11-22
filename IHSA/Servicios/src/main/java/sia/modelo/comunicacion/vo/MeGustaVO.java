/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.comunicacion.vo;

import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author jrodriguez
 */
public class MeGustaVO extends Vo{
    //no borrar esta clase 
    private String nombreUsuario;
    private String fotoUsuario;

    /**
     * @return the nombreUsuario
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * @param nombreUsuario the nombreUsuario to set
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * @return the fotoUsuario
     */
    public String getFotoUsuario() {
        return fotoUsuario;
    }

    /**
     * @param fotoUsuario the fotoUsuario to set
     */
    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }
}
