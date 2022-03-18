/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.campo.vo;

import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class CampoVo extends Vo {

    private boolean selected;
    private int pendiente;
    private int satArticuloID;
    private String satArticuloCode;
    private String satArticuloDesc;
    private int idCompaniaPais;

    public CampoVo() {
    }

    public CampoVo(int id) {
        this.id = id;
    }

    public CampoVo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public CampoVo(int id, String nombre, int total) {
        this.id = id;
        this.nombre = nombre;
        this.pendiente = total;
    }

}
