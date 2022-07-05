/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.gr.sistema.soporte;

import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import sia.modelo.gr.vo.MapaVO;
import sia.servicios.gr.impl.GrMapaImpl;

/**
 *
 * @author ihsa
 */
@Named(value = "menuBean")
@ViewScoped
public class MenuBean implements Serializable {

    //ManagedBeans
    //Servicios
    @Inject
    private GrMapaImpl grMapaImpl;
    
    
    /**
     * Creates a new instance of Sesion
     */
    public MenuBean() {
        
    }

    public List<MapaVO> getMapasMenu(){
        return grMapaImpl.getMapasMenu();
    }

    public String goConfiguracion() {
        return "/vistas/gr/configuracion";
    }
}
