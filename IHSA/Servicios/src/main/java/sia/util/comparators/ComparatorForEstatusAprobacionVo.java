/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.util.comparators;

import java.util.Comparator;
import sia.modelo.sgl.vo.EstatusAprobacionVO;

/**
 *
 * @author b75ckd35th
 */
public class ComparatorForEstatusAprobacionVo implements Comparator {

    
    public int compare(Object o1, Object o2) {
        EstatusAprobacionVO ea1 = (EstatusAprobacionVO) o1;
        EstatusAprobacionVO ea2 = (EstatusAprobacionVO) o2;

        return ea1.getFechaSalida().compareTo(ea2.getFechaSalida());
    }
}
