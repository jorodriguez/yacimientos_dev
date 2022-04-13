/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.util.comparators;

import java.util.Calendar;
import java.util.Comparator;
import sia.modelo.sgl.viaje.vo.ViajeroVO;


/**
 *
 * @author b75ckd35th
 */
public class ComparatorForViajeroVo implements Comparator {

    
    public int compare(Object o1, Object o2) {

        ViajeroVO v1 = (ViajeroVO) o1;
        ViajeroVO v2 = (ViajeroVO) o2;
        return v1.getCodigoSolicitudViaje().compareTo(v2.getCodigoSolicitudViaje());
    }

    public Calendar cleanCalendar(Calendar cal) {
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.HOUR_OF_DAY);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    public int compare(Calendar cUno, Calendar cDos, boolean withTime) {
        if (withTime) {
            return cUno.compareTo(cDos);
        } else {
            cUno = cleanCalendar(cUno);
            cDos = cleanCalendar(cDos);
            return cUno.compareTo(cDos);
        }
    }
}
