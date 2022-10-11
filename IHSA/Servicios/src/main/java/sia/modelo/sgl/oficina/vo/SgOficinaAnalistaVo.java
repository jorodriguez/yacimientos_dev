/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.oficina.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author b75ckd35th
 */
@Getter 
@Setter
@ToString
public class SgOficinaAnalistaVo extends Vo {
    private int idSgOficina;
    private String idAnalista;
    private String nombreSgOficina;
    private String nombreAnalista;
    private String emailAnalista;
    private boolean principal ;

}
