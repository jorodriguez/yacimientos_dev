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
 * @author mluis
 */
@Getter
@Setter
public class UsuarioGerenciaVo implements Serializable {

    private int id;
    public int idUsuarioGerencia;
    public int idNoticia;
    private String idUsuario;
    private String nombre;
    private int idGerencia;
    private String gerencia;
    private String liberado;
    private int idApCampo;
    private String nombreApCampo;
    private Date fechaBaja;
    private boolean bajaTerminada;
    private String activo;
    private boolean eliminado;
    private boolean terminarBaja;
    private List<UsuarioGerenciaVo> gerenciaUsuario;
    private String idUsuarioLibero;
    private String nombreUsuarioLibero;
    private int idRhCampoGerencia;

    public UsuarioGerenciaVo() {
    }

    public UsuarioGerenciaVo(String gerencia, String liberado) {
	this.gerencia = gerencia;
	this.liberado = liberado;
    }
}
