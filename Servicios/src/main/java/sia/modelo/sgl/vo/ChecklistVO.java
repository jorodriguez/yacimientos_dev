/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author b75ckd35th
 */
@Getter
@Setter
public class ChecklistVO {

    private int idChecklist;
    private Date fechaGenero;
    private Date inicoSemana;
    private Date finSemana;
    private CaracteristicaVo caracteristicaVo;
    private String style;
    private String observacion;
    private boolean principal;
    private boolean estado; //Si este campo es 'True' quiere decir que esta característica está en perfecto estado

}
