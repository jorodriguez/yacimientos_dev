/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.utils;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import sia.util.UtilLog4j;

/**
 *
 * @author mrojas
 */
public class LifeCycleListener implements PhaseListener {

    @Override
    public void afterPhase(PhaseEvent pe) {
        UtilLog4j.log.info(
                this, 
                "END PHASE " + pe.getPhaseId() + " - SOURCE : " + pe.getSource().getClass()
        );
    }

    @Override
    public void beforePhase(PhaseEvent pe) {
        UtilLog4j.log.info(
                this, 
                "START PHASE " + pe.getPhaseId() + " - SOURCE : " + pe.getSource()
        );
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }
    
}
