/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class MotivoRetrasoVO implements Serializable{
    private int idMotivoRetraso;
    private String justificacion;
    private Date horaReunion;
    private int idLugar;
    private int idInvitado;
    private String lugar;
    private String invitado;
    private String mail;
    private String nombreEmpresa;
    private String idUsuario;
    private String usuario;
}
