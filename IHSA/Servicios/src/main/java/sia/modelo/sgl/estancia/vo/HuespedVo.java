/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.estancia.vo;

import java.util.Date;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sia.modelo.sgl.vo.Vo;

/**
 * Este Vo es para almacenar Huéspedes tanto de Staff como de Hotel, habiendo un
 * booleano que identifica si el huésped es de staff o de hotel llamado
 * 'huespedStaff'
 *
 * @author b75ckd35th
 */
@Getter 
@Setter
@ToString
public class HuespedVo extends Vo {

    //Globales
    private boolean hospedado;
    private boolean cancelado;
    private boolean huespedStaff;
    private boolean invitado;
    private String TipohHuesped;
    
    private String idUsuario;
    private int idInvitado;
    private String nombreHuesped;
    private String emailHuesped;
    private String codigoSgSolicitudEstancia;
    private String nombreSgOficina;  
    private String nombreGerencia;    
    
    private Date fechaIngreso;
    private Date fechaSalida;
    
    private int idSgTipoEspecifico;
    private int idSgSolicitudEstancia;
    private int idSgDetalleSolicitudEstancia;
    private int idSgOficina;
    private int idGerencia;    
    
    //Huésped de Staff
    private int idSgStaff;
    private int idSgStaffHabitacion;
    private String nombreSgStaff;
    private String numeroSgStaff;
    private String nombreSgStaffHabitacion;
    private String numeroSgStaffHabitacion;    
    
    //Huésped de Hotel
    private int idSgHotelHabitacion;
    private int idSgHotel;
    private int idProveedor;
    private String nombreProveedorHotel;
    private String reservacion;    
    private String nombreHabitacion;
    private String numeroHabitacionHotel;
    
    private Integer idAdjunto;    
    private String adjuntoUUID;
    
}
