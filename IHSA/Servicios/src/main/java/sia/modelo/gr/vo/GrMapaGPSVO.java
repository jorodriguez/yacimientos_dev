/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.gr.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */

@Getter
@Setter
public class GrMapaGPSVO {

    public GrMapaGPSVO(String telefonoID, String lonCoord, String latCoord) {        
        this.idCoord = idCoord;
        this.telefonoID = telefonoID;
        this.lonCoord = lonCoord;
        this.latCoord = latCoord;        
    }
    
    private int idCoord;
    private String telefonoID;
    private String lonCoord;
    private String latCoord;        
}
