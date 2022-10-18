/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vehiculo.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class VehiculoCheckListVo {

    private int id;
    private SgKilometrajeVo sgKilometrajeVo;
    private Date fechaGenero;
    private Date inicioSemana;
    private Date finSemana;
    private String obervacion;

}
