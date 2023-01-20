package lector.timers;

import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.inject.Inject;
import lector.constantes.Constantes;
import lector.servicios.catalogos.impl.UsuarioImpl;
import lector.util.UtilLog4j;

public class Timers implements TimedObject {

    //Servicios
    @Resource
    private TimerService timerService;
    //
    @Inject
    private UsuarioImpl usuarioRemote;
  

    //S
    private String telefonoSeguridad = Constantes.VACIO;

    private final static String SIA_EMAIL = "siaihsa@ihsa.mx";
    private final static String SIA_USER = "SIA";

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
