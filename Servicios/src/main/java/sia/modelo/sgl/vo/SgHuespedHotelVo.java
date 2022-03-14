/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author b75ckd35th
 */
@Getter 
@Setter
public class SgHuespedHotelVo extends Vo {

    private boolean hospedado;
    private boolean cancelado;
    private int idSgHotelHabitacion;
    private int idSgHotel;
    private int idSgSolicitudEstancia;
    private int idSgDetalleSolicitudEstancia;
    private int idSgOficina;
    private int idSgTipoEspecifico;
    private String nombreProveedorHotel;
    private String reservacion;
    private String nombreHuesped;
    private String emailHuesped;
    private String codigoSgSolicitudEstancia;
    private String nombreSgOficina;
    private Date fechaIngreso;
    private Date fechaSalida;
    private int idGerencia;
    private String nombreGerencia;    
    private long total;
    private String hotel;
}
