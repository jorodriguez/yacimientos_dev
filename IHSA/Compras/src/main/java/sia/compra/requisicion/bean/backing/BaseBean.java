/*
 * BaseBean.java
 * Creada el 16/06/2009, 10:28:39 AM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com
 * @date 16/06/2009
 */

import javax.faces.event.ValueChangeEvent;

import java.io.Serializable;
import javax.swing.text.Highlighter.Highlight;
import org.primefaces.component.effect.Effect;

/**
 * <p>The BaseBean is a nice little helper class for common functionality
 * accross the component examples.  The BaseBean or the notion of a base
 * bean is handy in most application as it can provice commonality for logging,
 * init and dispose methods as well as references to Service lookup
 * mechanism. </p>
 *
 * <p>The valueChangeEffect is used by most example beans to highlight
 * changes in backing bean values that are reflected on the client side.</p>
 *
 * @since 1.7
 */
public class BaseBean implements Serializable {
    //the logger for this class

//    protected final Log logger = LogFactory.getLog(this.getClass());
    // effect that shows a value binding chance on there server
    protected Effect valueChangeEffect;

    public BaseBean() {
        //valueChangeEffect = new Highlight("#fda505");
        //valueChangeEffect.setFired(true);
    }

    /**
     * Resets the valueChange effect to fire when the current response
     * is completed.
     *
     * @param event jsf action event
     */
    public void effectChangeListener(ValueChangeEvent event) {
      //  valueChangeEffect.setFired(false);
    }

    /**
     * Used to initialize the managed bean.
     */
    protected void init() {
    }

    public Effect getValueChangeEffect() {
        return valueChangeEffect;
    }

    public void setValueChangeEffect(Effect valueChangeEffect) {
        this.valueChangeEffect = valueChangeEffect;
    }
}



