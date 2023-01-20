/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.dominio.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


/**
 *
 */
@Getter
@Setter
public class UsuarioRolVo {

    private int idUsuarioRol;
    private String idUsuario;
    private String usuario;
    private int idRol;
    private String nombreRol;
    private int idModulo;
    private String nombreModulo;
    private String correo;
    private boolean principal;
    private String telefono;

    //
    private List<Vo> lista = new ArrayList<Vo>();
}
