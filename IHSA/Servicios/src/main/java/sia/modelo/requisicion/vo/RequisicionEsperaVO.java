/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.requisicion.vo;

import java.util.List;
import javax.faces.model.DataModel;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.ReRequisicionEts;

/**
 *
 * @author jcarranza
 */
@Getter
@Setter
public class RequisicionEsperaVO {
    
    private RequisicionMovimientoVO registro;    
    private DataModel<ReRequisicionEts> lstArchivos;
    private List<RequisicionMovimientoVO> msgs;
    
}
