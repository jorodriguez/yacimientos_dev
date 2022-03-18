/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.usuario.vo;

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
public class UsuarioResponsableGerenciaVo extends Vo {
    String idUsuario;
    String nombreUsuario;
    String emailUsuario;
    String nombreGerencia;
    String nombreGerenciaResponsable;
    String nombreApCampo;
    int idGerencia;
    int idGerenciaResponsable;
    int idApCampo;
}
