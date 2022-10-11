/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.staff.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class StaffVo {

    private int idStaff;
    private String nombre;
    private String numeroStaff;
    private int numeroCuarto;
    private String telefono;
    private String oficina;
    private int idOficina;
}
