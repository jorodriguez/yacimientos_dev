/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.gerencia.vo;

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
public class RhCampoGerenciaVo extends Vo {

    private int idApCampo;
    private int idGerencia;
    private String nombreApCampo;
    private String nombreGerencia;
}
