/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.gerencia.vo;

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
public class RhTipoGerenciaVo extends Vo {

//    private int idGerencia;
//    private String nombreGerencia;
    private int idSgTipo;
    private String nombreSgTipo;
    private String idUsuario;
    private String nombreUsuario;
    private int idRhCampoGerencia;
}
