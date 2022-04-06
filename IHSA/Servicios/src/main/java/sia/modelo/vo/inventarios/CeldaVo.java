/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.vo.inventarios;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class CeldaVo implements Serializable {

    private int id;
    private int idRack;
    private String rack;
    private int idPiso;
    private String piso;
    private String celda;
    private boolean selected;

}
