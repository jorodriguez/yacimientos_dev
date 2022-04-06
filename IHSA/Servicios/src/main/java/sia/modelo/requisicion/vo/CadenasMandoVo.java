/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.requisicion.vo;

import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class CadenasMandoVo extends Vo{
    private String idSolicita;
    private String solicita;
    private String  idRevisa;
    private String  revisa;
    private String idAprueba;
    private String  aprueba;
    private String campo;
    private int idCampo;
}
