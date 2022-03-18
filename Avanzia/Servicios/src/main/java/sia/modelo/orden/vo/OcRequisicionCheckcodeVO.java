/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.orden.vo;

import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class OcRequisicionCheckcodeVO extends Vo{
    int idOrden;
    int idRequisicion;
    String rfc;
    String checkcode; 
    String url;
}
