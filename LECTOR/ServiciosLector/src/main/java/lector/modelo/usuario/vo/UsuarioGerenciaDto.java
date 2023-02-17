/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.modelo.usuario.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
public class UsuarioGerenciaDto implements Serializable {

    private int id;    
    private String idUsuario;
    private String nombre;    
    private String liberado;    
    private Date fechaBaja;
    private boolean bajaTerminada;
    private String activo;
    private boolean eliminado;    

    public UsuarioGerenciaDto() {
    }

    public UsuarioGerenciaDto(String liberado) {
	this.liberado = liberado;
    }
}
