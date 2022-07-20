/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.vehiculo.bean.model;

import java.io.Serializable;
import javax.inject.Named;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import sia.sgl.sistema.bean.backing.Sesion;

/**
 *
 * @author ihsa
 */
@Named(value = "pagoVehiculoModel")
@ViewScoped
public class PagoVehiculoModel implements Serializable{

    /**
     * Creates a new instance of PagoVehiculoModel
     */
    public PagoVehiculoModel() {
    }

    @Inject
    private Sesion sesion;

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

}
