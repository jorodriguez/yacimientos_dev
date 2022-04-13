/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.estancia.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class DetalleEstanciaVO {
    private int idDetalleEstancia;
    private int idGerencia;
    private int idSolicitudEstancia;
    private int idTipoEspecifico;
    private String codigo;
    private int dias;
    private String gerencia;
    private String tipoDetalle;
    private String idUsuario;
    private String usuario;
    private int idInvitado;
    private String invitado;
    private String descripcion;
    private Date inicioEstancia;
    private Date finEstancia;
    private boolean registrado;
    private boolean cancelado;
    private String correoUsuario;
    private String nombreGenero;
            
}
