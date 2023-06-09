package mx.ihsa.timers;

import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.inject.Inject;
import mx.ihsa.servicios.catalogos.impl.UsuarioImpl;
import mx.ihsa.util.UtilLog4j;

public class Timers implements TimedObject {

    @Resource
    private TimerService timerService;

    @PostConstruct
    public void initTimers() {
        log("Timers.initTimers()");

        if (timerService.getTimers() != null) {
            for (Timer timer : timerService.getTimers()) {
                log("Terminado Timer: " + timer.getInfo());
                timer.cancel();
            }
        }

        /*for (Schedules schedule : Schedules.values()) {
            timerService.createCalendarTimer(
                    schedule.getScheduleExpression(),
                    schedule.getTimerConfig()
            );

            log("Creado el  Timer: " + schedule.getTimerConfig().getInfo());
        }*/
    }

    
    public void ejbTimeout(Timer timer) {
        log("Timers.ejbTimeout()");
        log("Ejecutando Timer: " + new Date());

        /*if (timer.getInfo().equals(Constantes.TIMER_AVISO_VENCIMIENTO_CONVENIOS)) {
            
        } */             

    }

    private void log(String mensaje, Throwable e) {
        UtilLog4j.log.info(this, mensaje, e);
    }

    private void log(String mensaje) {
        log(mensaje, null);
    }

}
