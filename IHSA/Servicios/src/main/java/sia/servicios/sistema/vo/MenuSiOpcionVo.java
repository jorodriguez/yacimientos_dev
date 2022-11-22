/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class MenuSiOpcionVo {
    SiOpcionVo padre;    
    List<SiOpcionVo> hijos = new ArrayList<SiOpcionVo>();        
}
