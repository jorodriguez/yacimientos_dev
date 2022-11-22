/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class CatalogoContratoVo extends Vo implements Serializable {

    private boolean selected;

    public CatalogoContratoVo() {
    }

    public CatalogoContratoVo(Integer id, String nombre) {
	this.id = id;
	this.nombre = nombre;
    }
}
