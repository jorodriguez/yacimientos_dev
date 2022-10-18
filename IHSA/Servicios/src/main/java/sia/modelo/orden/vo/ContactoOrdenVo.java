/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.orden.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class ContactoOrdenVo {
    private int id;
    private int idOrden;
    private int idContactoProveedor;
    private  boolean selected;
    private String correo;
    private String nombre;
}
