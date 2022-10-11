/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.proveedor.Vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class ContactoProveedorVO {
    private int idContactoProveedor;
    private String nombre;    
    private int idPuesto;
    private String correo;
    private String telefono;
    private int idProveedor;
    private boolean selected;    
    private boolean editar;
    private String puesto;
    private String celular;    
    private String rfc;
    private String curp;
    private String poder;
    private String notaria;
    private Date emision;
    private int tipoID;
    private String tipoTxt;
    private Date idVigencia;
    private String nombreNotario;
    private String referencia;
    private int tipoContacto;
    private String tipoContactoTxt;
    private boolean notifica = false;
}
