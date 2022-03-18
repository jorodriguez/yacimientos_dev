/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.gr.vo;

import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author ihsa
 */

@Getter
@Setter
public class GrIntercepcionVO extends Vo {

    private ViajeVO viajeA;
    private ViajeVO viajeB;
    private int puntoSeguridadID;
    private String puntoSeguridadNombre;
    private boolean intercambiarViajeros;
    private boolean intercambiarVehiculo;
    
   
}

