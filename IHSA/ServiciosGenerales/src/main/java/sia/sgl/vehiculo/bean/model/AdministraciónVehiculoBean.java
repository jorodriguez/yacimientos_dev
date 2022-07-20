/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package sia.sgl.vehiculo.bean.model;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.Env;

/**
 *
 * @author mluis
 */
@Named(value = "administraciónVehiculoBean")
@ViewScoped
public class AdministraciónVehiculoBean implements Serializable {

    /**
     * Creates a new instance of AdministraciónVehiculoBean
     */
    public AdministraciónVehiculoBean() {
    }
    
    @Inject
    Sesion sesion;
    
    @PostConstruct
    public void iniciar(){
        
        Integer parametro = Env.getContextAsInt(sesion.getCtx(), "VEHICULO_ID");
        if (parametro > 0) {
            
        }
    }
    
}
