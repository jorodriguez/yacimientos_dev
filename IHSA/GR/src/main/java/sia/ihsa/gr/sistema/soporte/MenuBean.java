/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.gr.sistema.soporte;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import sia.modelo.gr.vo.MapaVO;
import sia.servicios.gr.impl.GrMapaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@ManagedBean(name = "menuBean")
@CustomScoped(value = "#{window}")
public class MenuBean implements Serializable {

    //ManagedBeans
    //Servicios
    @EJB
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
