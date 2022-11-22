/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Setter
@Getter
public class JustIncumSolVo implements  Serializable{
    private int id;
    private String motivoJustifiacion;
    private String justifico;
    private Date fecha;
    private Date hora;
}
