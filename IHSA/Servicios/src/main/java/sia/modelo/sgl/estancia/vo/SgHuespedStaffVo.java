/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.estancia.vo;

import java.util.Date;
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
public class SgHuespedStaffVo extends Vo {

    private String nombreHuesped;
    private int idSgStaff;
    private int idSgStaffHabitacion;
    private int idSgTipoEspecifico;
    private String emailHuesped;
    private Date fechaIngreso;
    private Date fechaSalida;
    private int idSgSolicitudEstancia;
    private String codigoSgSolicitudEstancia;
    private int idSgDetalleSolicitudEstancia;
    private int idSgOficina;
    private String nombreSgOficina;
    private String nombreSgStaff;
    private String numeroSgStaff;
    private String nombreSgStaffHabitacion;
    private String numeroSgStaffHabitacion;
    private int idGerencia;
    private String nombreGerencia;
    private int idSgTipo;
    private long total;
    private String oficina;
}
