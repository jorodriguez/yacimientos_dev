/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.orden.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class OcActivoFijoVO implements Serializable {
    private int id;
    private String codigo; 
    private String oldCodigo; 
    private int ordenId;
    private int ordenDetId;

}
