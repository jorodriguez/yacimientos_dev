/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

import lombok.Getter;
import lombok.Setter;
import sia.modelo.SgCaracteristica;

/**
 *
 * @author b75ckd35th
 * @author mluis
 */
@Getter
@Setter
public class CaracteristicaVo {

    private boolean asignada; //Si este campo es 'True' significa que existe una relación entre esta Característica y un Staff
    private SgCaracteristica caracteristica;
    private int id;
    private String nombre;
    private boolean principal;
    private long idTipo;
    private String tipo;
    private long cantidad;
    private boolean estado;
    private int idRelacion;

    
    public String toString() {
	return "CaracteristicaVO{" + "caracteristica=" + this.caracteristica.getNombre() + ", asignada=" + asignada + '}';
    }
}
