/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.util.comparators;

import java.util.Comparator;
import sia.modelo.sgl.viaje.vo.ViajeroVO;

/**
 *
 * @author b75ckd35th
 */
public class ComparatorForViajeroVoOrderByNombre implements Comparator {

    
    public int compare(Object o1, Object o2) {

        ViajeroVO v1 = (ViajeroVO) o1;
        ViajeroVO v2 = (ViajeroVO) o2;
        String nombreUno = (v1.isEmpleado() ? v1.getUsuario() : v1.getInvitado());
        String nombreDos = (v2.isEmpleado() ? v2.getUsuario() : v2.getInvitado());

        return nombreUno.compareTo(nombreDos);
    }
    
}
