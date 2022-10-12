/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.vehiculo.bean.backing;

import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import sia.sgl.vehiculo.bean.model.PagoVehiculoModel;

/**
 *
 * @author ihsa
 */
@Named(value = "pagoVehiculoBean")
@javax.faces.bean.RequestScoped
public class PagoVehiculoBean {

    /**
     * Creates a new instance of PagoVehiculoBean
     */
    public PagoVehiculoBean() {
    }

    @ManagedProperty(value = "#{pagoVehiculoModel}")
    private PagoVehiculoModel pagoVehiculoModel;

    /**
     * @param pagoVehiculoModel the pagoVehiculoModel to set
     */
    public void setPagoVehiculoModel(PagoVehiculoModel pagoVehiculoModel) {
	this.pagoVehiculoModel = pagoVehiculoModel;
    }
}
