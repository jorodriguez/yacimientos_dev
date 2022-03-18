/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Setter
@Getter
public class CheckListDetalleVo {

    private int id;
    private int idCheckList;
    private int idCaracteristica;
    private boolean estado;
    private String observacion;
    private ChecklistVO checklistVO = new ChecklistVO();
    private CaracteristicaVo caracteristicaVo = new CaracteristicaVo();

}
