/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class InvitadoVO implements Serializable {

    private int idInvitado;
    private String nombre;
    private int idEmpresa;
    private String empresa;
    private String email;
    private String usuario;
    private String telefono;

}
