/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.orden.vo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.OrdenVO;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class OrdenCorreoVo {
    
    private String gerencia;
    private String campo;
    private  String proveedor;
    private List<OrdenVO> lorden;
    private double total;
    
}
