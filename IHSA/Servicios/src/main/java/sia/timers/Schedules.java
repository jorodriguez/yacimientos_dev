/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.timers;

import javax.ejb.ScheduleExpression;
import javax.ejb.TimerConfig;
import sia.constantes.Constantes;

/**
 *
 * @author mrojas
 */
public enum Schedules {

    //Saca automáticamente a los huéspedes de staff y hotel
    SALIDA_AUTOMATICA(
            new ScheduleExpression()
                    .hour(Constantes.HORA_EJECUCION_TIMER)
                    .minute(Constantes.MINUTOS_EJECUCION_TIMER)
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_SALIDA_AUTOMATICA, true)
    ) //Notifica viaje aereo
    , NOTIFICA_VIAJE_AEREO(
            new ScheduleExpression()
                    .hour(Constantes.HORA_EJECUCION_TIMER_GESTION_RIESGO)
                    .minute("02")
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_NOTIFICA_VIAJE_AEREO, true)
    ) //para marcar a los viajes por salir a viajes en proceso Se ejecuta a una hora fija por definir
    , VIAJES_SEMAFORO(
            new ScheduleExpression()
                    .hour(Constantes.HORA_EJECUCION_TIMER_SEMAFORO)
                    .minute("04")
                    .second("00"),
            new TimerConfig(Constantes.TIMER_VIAJES_SEMAFORO, true)
    ), VIAJES_SOLICITUDES(
            new ScheduleExpression()
                    .hour(Constantes.HORA_EJECUCION_TIMER_LIMPIARSOLICITUDESYVIAJES)
                    .minute("00")
                    .second("00"),
            new TimerConfig(Constantes.TIMER_LIMPIAR_VIAJESYSOLICITUDES, true)
    ) //    , CANCELAR_SOLICITUDVIAJES_LJ(
    //	    new ScheduleExpression()
    //            .dayOfWeek("Mon-Thu")
    //	    .hour(Constantes.HORA_EJECUCION_TIMER_CANCEL_SV)
    //	    .minute("01")
    //	    .second("00"),
    //	    new TimerConfig(Constantes.TIMER_SV_CANCELAR, true)
    //    ), CANCELAR_SOLICITUDVIAJES_V(
    //	    new ScheduleExpression()
    //            .dayOfWeek("Fri")
    //	    .hour(Constantes.HORA_EJECUCION_TIMER_CANCEL_SVV)
    //	    .minute("01")
    //	    .second("00"),
    //	    new TimerConfig(Constantes.TIMER_SV_CANCELAR, true)
    //    )
    , REGRESO_AUTOMATICO_VIAJES_TERRESTRES_FUERA_OFICINA(
            new ScheduleExpression()
                    .hour(Constantes.HORA_EJECUCION_TIMER)
                    .minute("06")
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_REGRESO_AUTOMATICO_VIAJES_TERRESTRES_FUERA_OFICINA, true)
    ), AVISO_VENCIMIENTO_CONVENIOS(
            new ScheduleExpression()
                    .hour(Constantes.HORA_EJECUCION_TIMER)
                    .minute("08")
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_AVISO_VENCIMIENTO_CONVENIOS, true)
    ), AVISO_VENCIMIENTO_LICENCIAS(
            /*
	     * Descripción: Ejecuta un metodo de busqueda de registros de licencias
	     * siempre y cuando cumplan con 15 dias anticipados a la fecha de
	     * vencimiento de las mismas. El envio se hace a Resposables de SGL y
	     * Analistas por Oficina, por esta razón solo se muestran los usuarios
	     * que tienen asignado un Vehiculo. (Las licencias no tienen Oficina, la
	     * tomo del vehiculo asignado)
             */
            new ScheduleExpression()
                    .hour(Constantes.HORA_EJECUCION_TIMER_LICENCIA)
                    .minute("27")
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_AVISO_VENCIMIENTO_LICENCIAS, true)
    ), AVISO_VENCIMIENTO_LICENCIAS_SEMANAL( //realizar rpruebas en conjunto
            new ScheduleExpression()
                    .dayOfWeek("fri")
                    .hour(Constantes.HORA_EJECUCION_TIMER_LICENCIA)
                    .minute("00")
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_AVISO_VENCIMIENTO_LICENCIAS_SEMANAL, true)
    ), AVISO_VENCIMIENTO_CURSO_MANEJO(
            new ScheduleExpression()
                    .dayOfWeek("fri")
                    .hour(Constantes.HORA_EJECUCION_TIMER_LICENCIA)
                    .minute("10")
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_AVISO_VENCIMIENTO_CURSO_MANEJO, true)
    ),    QUITA_VIGENCIA_CURSO_MANEJO(
            new ScheduleExpression()
                    .hour(Constantes.HORA_EJECUCION_TIMER_LICENCIA)
                    .minute("20")
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_QUITA_VIGENCIA_CURSO_MANEJO, true)
    ), QUITA_VIGENCIA_LICENCIA(
            new ScheduleExpression()
                    .hour(Constantes.HORA_EJECUCION_TIMER_LICENCIA)
                    .minute("35")
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_QUITA_VIGENCIA_LICENCIA, true)
    ),
    AVISO_PAGOS(
            new ScheduleExpression()
                    .hour(Constantes.HORA_EJECUCION_TIMER)
                    .minute("12")
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_AVISO_PAGOS, true)
    ) //Aviso de salida huesped staff
    , AVISO_SALIDA_ESTANCIA_STAFF(
            new ScheduleExpression()
                    .hour(Constantes.HORA_EJECUCION_TIMER)
                    .minute("50")
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_AVISO_SALIDA_ESTANCIA_STAFF, true)
    ), VENCIMIENTO_PAGO_SERVICIO(
            /*
	     * Ejecuta un metodo de busqueda de registros de pagos devéhiculos
	     * (TENENCIA y SEGUROS) comparando la fecha de vencimiento del mismo.
             */
            new ScheduleExpression()
                    .hour(Constantes.HORA_EJECUCION_TIMER)
                    .minute("30")
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_VENCIMIENTO_PAGO_SERVICIO, true)
    ), AVISO_MANTENIMIENTO(
            new ScheduleExpression()
                    .hour(Constantes.HORA_EJECUCION_TIMER)
                    .minute("45")
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_AVISO_MANTENIMIENTO, true)
    ) //Publica el reporte de los viajes terrestres
    , REPORTE_DIARIO_VIAJE(
            new ScheduleExpression().hour("17")
                    .minute("00")
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_REPORTE_DIARIO_VIAJE, true)
    ) //Publica el reporte de monto acumulado
    /*, REPORTE_MONTO_ACUMULADO( //mrojas : implementado en cron, pendiente de validar
	    new ScheduleExpression()
	    .hour(Constantes.HORA_EJECUCION_TIMER_REPORTE_MONTO_ACUMULADO)
	    .minute("47")
	    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
	    new TimerConfig(Constantes.TIMER_REPORTE_MONTO_ACUMULADO, true)
    )*/ //Publica el reporte de oc/s por autorizar
    , ESTADO_SEMAFORO(
            new ScheduleExpression()
                    .hour(Constantes.HORA_EJECUCION_TIMER_ESTADO_SEMAFORO)
                    .minute("30")
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_ESTADO_SEMAFORO, true)
    ) // Crear timer para envio de reporte de compradores
    , REPORTE_COMPRADORES(
            new ScheduleExpression()
                    .dayOfWeek("Mon-Fri")
                    .hour(Constantes.HORA_EJECUCION_TIMER_REPORTE_COMPRADORES)
                    .minute(Constantes.MINUTOS_EJECUCION_TIMER)
                    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
            new TimerConfig(Constantes.TIMER_CONFIG_REPORTE_COMPRADORES, true)
    ); //Publica el reporte autoriza OC/16S
    /*, REPORTE_AUTORIZA_ORDENES( //TODO : mrojas : implementado ya en un proceso externo, pendiente de validar
	    new ScheduleExpression()
	    .hour(Constantes.HORA_EJECUCION_TIMER_REPORTE_AUTORIZA_ORDENES)
	    .minute("35")
	    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
	    new TimerConfig(Constantes.TIMER_REPORTE_AUTORIZA_ORDENES, true)
    )*/
 /*, CONTROL_OFICIOS_NOTIFICACION_ALTA_OFICIOS( //TODO : mrojas : implementado ya en un proceso externo, pendiente de validar
	    new ScheduleExpression()
	    .dayOfWeek("Mon-Thu")
	    .hour(Constantes.HORA_EJECUCION_TIMER_CONTROL_OFICIOS_NOTIFICACION_ALTA_OFICIOS_7_PM)
	    .minute(Constantes.MINUTOS_EJECUCION_TIMER)
	    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
	    new TimerConfig(Constantes.TIMER_CONFIG_CONTROL_OFICIOS_NOTIFICACION_ALTA_OFICIOS, true),*/
 /*), CONTROL_OFICIOS_NOTIFICACION_ALTA_OFICIOS_3_PM( //TODO : mrojas : implementado ya en un proceso externo, pendiente de validar
	    new ScheduleExpression()
	    .dayOfWeek("Fri")
	    .hour(Constantes.HORA_EJECUCION_TIMER_CONTROL_OFICIOS_NOTIFICACION_ALTA_OFICIOS_3_PM)
	    .minute(Constantes.MINUTOS_EJECUCION_TIMER)
	    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
	    new TimerConfig(Constantes.TIMER_CONFIG_CONTROL_OFICIOS_NOTIFICACION_ALTA_OFICIOS, true)
    ) //timer para notificacion de oficios no promovidos*/
 /*, CONTROL_OFICIOS_NOTIFICACION_NO_PROMOVIDOS_OFICIOS( //TODO : mrojas : implementado ya en un proceso externo, pendiente de validar
	    new ScheduleExpression()
	    .dayOfWeek("Fri")
	    .hour(Constantes.HORA_EJECUCION_TIMER)
	    .minute(Constantes.MINUTOS_EJECUCION_TIMER)
	    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
	    new TimerConfig(Constantes.TIMER_CONFIG_CONTROL_OFICIOS_NOTIFICACION_NO_PROMOVIDOS_OFICIOS, true)),*/
 /*CONVENIOS_POR_VENCER( //TODO : mrojas : implementado ya en un proceso externo, pendiente de validar
	    new ScheduleExpression()
	    .dayOfWeek("Mon")
	    .hour(8)
	    .minute(20)
	    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
	    new TimerConfig(Constantes.CONVENIOS_POR_VENCER, true)),
    CONVENIOS_VENCIDOS( //TODO : mrojas : implementado ya en un proceso externo, pendiente de validar
	    new ScheduleExpression()
	    .dayOfWeek("Mon-Fri")
	    .hour(23)
	    .minute(50)
	    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
	    new TimerConfig(Constantes.CONVENIOS_VENCIDOS, true)),*/
 /*SOLICITUDES_SIN_APROBAR(
	    new ScheduleExpression()
	    .dayOfWeek("Mon-Sun")
	    .hour(14)
	    .minute(03)
	    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
	    new TimerConfig(Constantes.TIMER_CONFIG_REPORTE_SV_POR_APROBAR, true)),*/
 /*SOLICITUDES_SIN_APROBAR_VIERNES( //FIXME : al parecer no se utiliza
	    new ScheduleExpression()
	    .dayOfWeek("Fri")
	    .hour(12)
	    .minute(30)
	    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
	    new TimerConfig(Constantes.TIMER_CONFIG_REPORTE_SV_POR_APROBAR_VIERNES, true)),*/
 /*, CAMBIO_DE_APROBACION( //TODO : mrojas : implementado ya en un proceso externo, pendiente de validar
	    new ScheduleExpression()
	    .dayOfWeek("Mon-Fri")
	    .hour(17)
	    .minute(30)
	    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
	    new TimerConfig(Constantes.TIMER_CAMBIO_DE_APROBACION, true ));/*,
    //TODO : mrojas : implementado ya en un proceso externo, pendiente de validar
    REPORTE_GERENTE_SERVICIOS(
	    new ScheduleExpression()
	    .hour(21)
	    .minute(00)
	    .second(Constantes.SEGUNDOS_EJECUCION_TIMER),
	    new TimerConfig(Constantes.TIMER_REPORTE_GERENTE_SG, true )
    );*/

    private final ScheduleExpression scheduleExpression;
    private final TimerConfig timerConfig;

    private Schedules(final ScheduleExpression expression, final TimerConfig timerConfig) {
        this.scheduleExpression = expression;
        this.timerConfig = timerConfig;
    }

    public ScheduleExpression getScheduleExpression() {
        return scheduleExpression;
    }

    public TimerConfig getTimerConfig() {
        return timerConfig;
    }
}
