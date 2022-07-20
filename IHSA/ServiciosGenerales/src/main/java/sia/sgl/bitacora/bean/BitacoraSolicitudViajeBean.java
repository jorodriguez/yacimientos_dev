/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.bitacora.bean;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.sgl.bitacora.bean.model.BitacoraSolicitudViajeBeanModel;
import sia.sgl.sistema.bean.backing.Sesion;

/**
 *
 * @author mluis
 */
//@Named(value = "bitacoraSolicitudViajeBean")
@RequestScoped
public class BitacoraSolicitudViajeBean implements Serializable {

    @Inject
    Sesion sesion;
    @Inject
    private BitacoraSolicitudViajeBeanModel bitacoraSolicitudViajeBeanModel;

    /**
     * Creates a new instance of BitacoraBean
     */
    public BitacoraSolicitudViajeBean() {
    }

    public String goToBitacoraViaje() {
        bitacoraSolicitudViajeBeanModel.beginBitacoraSolicitudViaje();
        return "/vistas/sgl/viaje/bitacora/bitacoraSolicitudViaje";
    }
}
