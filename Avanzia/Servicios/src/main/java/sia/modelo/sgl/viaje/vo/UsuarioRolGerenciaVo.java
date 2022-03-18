/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import lombok.Data;

/**         
 *
 * @author mluis
 */
@Data
public class UsuarioRolGerenciaVo  {
    private int idUsuarioRolGerencia;
    private int idGerencia;
    private String idUsuario;
    private int idRol;
    private String nombreRol;
    private String usuario;
    private String gerencia;
}
