/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.contrato.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Setter
@Getter
public class ContratoFormasNotasVo {
    private int id;
    private int idConvenioFormas;
    private String idUsuario;
    private String usuario;
    private String observacion;
    private Date fechaNota;
    private Date horaNota;
    //
    //
}
