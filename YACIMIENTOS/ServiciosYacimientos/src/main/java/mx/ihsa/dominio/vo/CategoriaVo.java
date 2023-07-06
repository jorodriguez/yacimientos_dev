/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.dominio.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jrodriguez
 */
@Getter
@Setter
public class CategoriaVo extends Vo {

    private String codigo;
    private int idCategoria;
    private String categoriaSuperior;

}
