/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.dominio.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jrodriguez
 */
@Getter
@Setter
public class CLocalidadVo extends Vo {

    private String clave;
    private String estado;
    private String municipio;
    private String latitud;
    private String longitud;
}
