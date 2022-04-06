/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import sia.constantes.Constantes;
import sia.modelo.Convenio;
import sia.servicios.sgl.impl.SgTipoSolicitudViajeImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SiManejoFechaImpl {

    @Inject
    private SgTipoSolicitudViajeImpl sgTipoSolicitudViajeRemote;
    //
    //

    
    public int dias(Date fechaFin, Date fechaInicio) {
        long dif = fechaFin.getTime() - fechaInicio.getTime();
        return (int) Math.floor(dif / (1000 * 60 * 60 * 24));
    }

    /**
     * Visto aquí:
     * http://www.cpxall.com/2012/10/calcular-diferencia-horas-entre-dos-fechas-java.html
     *
     * @param cUno
     * @param cDos
     * @return
     */
    
    public long getDiffInMinutes(Calendar cUno, Calendar cDos) {
        long totalMinutos = 0;

        UtilLog4j.log.info(this, "cUno.timeInMillis: " + cUno.getTime() + " - " + cUno.getTimeInMillis());
        UtilLog4j.log.info(this, "cDos.timeInMillis: " + cDos.getTime() + " - " + cDos.getTimeInMillis());
        totalMinutos = ((cDos.getTimeInMillis() - cUno.getTimeInMillis()) / 1000 / 60);

        return totalMinutos;
    }

    /**
     * Método obtenido de aquí: http://alejandroayala.solmedia.ec/?p=667 Nota:
     * Si este método devuelve 0 es que la diferencia de días es < 1, osea, es
     * decir, la diferencia es solo de horas
     *
     *
     * @return
     */
    
    public int diferenciaDias(Calendar fechaInicio, Calendar fechaFin) {
//        UtilLog4j.log.info(this,"ManejoFecha.diferenciaDias()");

        UtilLog4j.log.info(this, "FechaInicio: " + fechaInicio.getTime());
        UtilLog4j.log.info(this, "FechaFin: " + fechaFin.getTime());

        long milisegundosFechaInicio = fechaInicio.getTimeInMillis();
//        UtilLog4j.log.info(this,"milisegundosFechaInicio: " + milisegundosFechaInicio);

        long milisegundosFechaFin = fechaFin.getTimeInMillis();
        long diferenciaMilisegundos = milisegundosFechaFin - milisegundosFechaInicio;
        long diferenciaDias = diferenciaMilisegundos / (24 * 60 * 60 * 1000);
        UtilLog4j.log.info(this, "diferenciaDias: " + diferenciaDias);

        return (int) diferenciaDias;
    }

    
    public Calendar getInicioSemana() {
        Calendar fechaHoy = new GregorianCalendar();
        //Dia actual de la semana
        int diaActual = fechaHoy.get(Calendar.DAY_OF_WEEK);
        //Obtener la fecha de inicio de la semana
        int primerDiaSemana = 1;

        if (diaActual == primerDiaSemana) { //Hoy es el primer día de la semana, por lo tanto, solo hay que obtener los mínimos
            //Asignando el mínimo de horas, minutos, segundos y milisegundos a la fecha
            fechaHoy.set(Calendar.HOUR_OF_DAY, fechaHoy.getActualMinimum(Calendar.HOUR_OF_DAY));
            fechaHoy.set(Calendar.MINUTE, fechaHoy.getActualMinimum(Calendar.MINUTE));
            fechaHoy.set(Calendar.SECOND, fechaHoy.getActualMinimum(Calendar.SECOND));
            fechaHoy.set(Calendar.MILLISECOND, fechaHoy.getActualMinimum(Calendar.MILLISECOND));
        } else {
            fechaHoy.add(Calendar.DAY_OF_MONTH, -(diaActual - primerDiaSemana));
            //Asignando el mínimo de horas, minutos, segundos y milisegundos a la fecha
            fechaHoy.set(Calendar.HOUR_OF_DAY, fechaHoy.getActualMinimum(Calendar.HOUR_OF_DAY));
            fechaHoy.set(Calendar.MINUTE, fechaHoy.getActualMinimum(Calendar.MINUTE));
            fechaHoy.set(Calendar.SECOND, fechaHoy.getActualMinimum(Calendar.SECOND));
            fechaHoy.set(Calendar.MILLISECOND, fechaHoy.getActualMinimum(Calendar.MILLISECOND));
        }

        UtilLog4j.log.info(this, "Fecha inicio de semana (Date): " + fechaHoy.getTime());
        return fechaHoy;
    }

    
    public Calendar getFinSemana() {
        //Días = 1 - 7 (SUNDAY/Domíngo - SATURDAY/Sábado) - Constante=DAY_OF_WEEK
        //Meses = 0 - 11 (January/Enero - DECEMBER/Diciembre) - Constante=MONTH
        Calendar fechaHoy = new GregorianCalendar();
//        UtilLog4j.log.info(this,"Fecha actual (Date): " + fechaHoy.getTime());

        //Dia actual de la semana
        int diaActual = fechaHoy.get(Calendar.DAY_OF_WEEK);
//        UtilLog4j.log.info(this,"Dia actual de la semana: " + diaActual);

        //Obtener la fecha de inicio de la semana
        int ultimoDiaSemana = 7;

        if (diaActual == ultimoDiaSemana) { //Hoy es el último día de la semana, por lo tanto solo hay que obtener los máximos
            //Asignando el máximo de horas, minutos, segundos y milisegundos a la fecha
            fechaHoy.set(Calendar.HOUR_OF_DAY, fechaHoy.getActualMaximum(Calendar.HOUR_OF_DAY));
            fechaHoy.set(Calendar.MINUTE, fechaHoy.getActualMaximum(Calendar.MINUTE));
            fechaHoy.set(Calendar.SECOND, fechaHoy.getActualMaximum(Calendar.SECOND));
            fechaHoy.set(Calendar.MILLISECOND, fechaHoy.getActualMaximum(Calendar.MILLISECOND));
        } else {
            fechaHoy.add(Calendar.DAY_OF_MONTH, (ultimoDiaSemana - diaActual));
            //Asignando el máximo de horas, minutos, segundos y milisegundos a la fecha
            fechaHoy.set(Calendar.HOUR_OF_DAY, fechaHoy.getActualMaximum(Calendar.HOUR_OF_DAY));
            fechaHoy.set(Calendar.MINUTE, fechaHoy.getActualMaximum(Calendar.MINUTE));
            fechaHoy.set(Calendar.SECOND, fechaHoy.getActualMaximum(Calendar.SECOND));
            fechaHoy.set(Calendar.MILLISECOND, fechaHoy.getActualMaximum(Calendar.MILLISECOND));
        }
        UtilLog4j.log.info(this, "Fecha Fin de semana (Date): " + fechaHoy.getTime());
        return fechaHoy;
    }

    
    public boolean belongsToDateThisWeek(Calendar fecha, boolean inclusive) {
        UtilLog4j.log.info(this, "SiManejoFechaImpl.belongsToDateThisWeek()");
        Calendar fechaInicioSemana = getInicioSemana();
        Calendar fechaFinSemana = getFinSemana();

        if (inclusive) {
            cleanCalendar(fecha);
            cleanCalendar(fechaInicioSemana);
            cleanCalendar(fechaFinSemana);
        }

        UtilLog4j.log.info(this, "Inicio de Semana: " + fechaInicioSemana.getTime());
        UtilLog4j.log.info(this, "Fin de Semana: " + fechaFinSemana.getTime());
        UtilLog4j.log.info(this, "Fecha: " + fecha.getTime());

        if (fecha.compareTo(fechaInicioSemana) == 0 || fecha.compareTo(fechaFinSemana) == 0) {
            UtilLog4j.log.info(this, "belongs to date this week: " + true);
            return true;
        } else {
            UtilLog4j.log.info(this, "belongs to date this week: " + ((fecha.after(fechaInicioSemana) && fecha.before(fechaFinSemana)) ? true : false));
            return ((fecha.after(fechaInicioSemana) && fecha.before(fechaFinSemana)) ? true : false);
        }
    }

    
    public int[] convenioExpiresInLessThan30Days(Convenio convenio) {
//        UtilLog4j.log.info(this,"ManejoFecha.convenioExpiresInLessThan30Days()");

        int[] info = new int[2];

        Calendar fechaHoy = Calendar.getInstance();
        Calendar fechaVencimientoContrato = Calendar.getInstance();
        fechaVencimientoContrato.setTime(convenio.getFechaVencimiento());

        UtilLog4j.log.info(this, "FechaHoy: " + fechaHoy.getTime());
        UtilLog4j.log.info(this, "FechaVEncimientoContrato: " + fechaVencimientoContrato.getTime());

        int diferenciaDias = diferenciaDias(fechaHoy, fechaVencimientoContrato);
        UtilLog4j.log.info(this, "DiferenciaDias para Expiración de Contratos : " + diferenciaDias);

        info[0] = (diferenciaDias == 30) ? 1 : 0;
        info[1] = diferenciaDias;

        return info;
    }

    
    public boolean theFirstDateIsLessThanTheSecond(Calendar primeraFecha, Calendar segundaFecha) {
//        UtilLog4j.log.info(this,"ManejoFecha.theFirstDateIsLessThanTheSecond()");

//        UtilLog4j.log.info(this,"primeraFecha.getTime(): " + primeraFecha.getTime());
//        UtilLog4j.log.info(this,"segundaFecha.getTime(): " + segundaFecha.getTime());
//        UtilLog4j.log.info(this,"PrimeraFecha.before(segundaFecha): " + primeraFecha.before(segundaFecha));
        return primeraFecha.before(segundaFecha);
    }

    
    public boolean theFirstDateIsLessThanTheSecond(Date primeraFecha, Date segundaFecha) {
//        UtilLog4j.log.info(this,"ManejoFecha.theFirstDateIsLessThanTheSecond()");
//
//        UtilLog4j.log.info(this,"primeraFecha: " + primeraFecha);
//        UtilLog4j.log.info(this,"segundaFecha: " + segundaFecha);

        Calendar calendarUno = Calendar.getInstance();
        calendarUno.setTime(primeraFecha);

        Calendar calendarDos = Calendar.getInstance();
        calendarDos.setTime(segundaFecha);

        UtilLog4j.log.info(this, "calendarUno: " + calendarUno.getTime());
        UtilLog4j.log.info(this, "calendarDos: " + calendarDos.getTime());

        UtilLog4j.log.info(this, "theFirstDateIsLessThanTheSecond: " + theFirstDateIsLessThanTheSecond(calendarUno, calendarDos));
        return theFirstDateIsLessThanTheSecond(calendarUno, calendarDos);
    }

    
    public Calendar getGreaterDate(Calendar fechaUno, Calendar fechaDos) {
//        UtilLog4j.log.info(this,"ManejoFecha.getGreaterDate()");
        return ((fechaUno.after(fechaDos)) ? fechaDos : fechaUno);
    }

    
    public Date getGreaterDate(Date fechaUno, Date fechaDos) {
//        UtilLog4j.log.info(this,"ManejoFecha.getGreaterDate()");
        Calendar calendarUno = Calendar.getInstance();
        calendarUno.setTime(fechaUno);

        Calendar calendarDos = Calendar.getInstance();
        calendarDos.setTime(fechaDos);

        return getGreaterDate(calendarUno, calendarDos).getTime();
    }

    /*
     * +++++++++++++++++++ Metodos para fechas de Timer's en Avisos
     * +++++++++++++++++++++++++
     */
    //@Descripcion: Retorna la fecha actual
    public Date fechaHoy() {
        Calendar cal = Calendar.getInstance();
        return new Date(cal.getTimeInMillis());
    }

    //Sumar dias a una fecha determinada
    //@param fch La fecha
    //@param dias Dias a sumar
    //@return La fecha nueva (sumada)
    //@Descripcion: Calculo la fecha de pago
    
    public Date fechaSumarDias(Date fch, int dia) {
        UtilLog4j.log.info(this, "fecha " + fch);
        UtilLog4j.log.info(this, "Dias sumar " + dia);
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(fch.getTime());
        cal.add(Calendar.DATE, dia);
        return new Date(cal.getTimeInMillis());
    }

    //Sumar Mes a una fecha determinada
    //@param fch La fecha
    //@param dias Dias a sumar
    //@return La fecha nueva (sumada)
    //@Descripcion: ocupo este metodo para sumar la periodicidad
    
    public Date fechaSumarMes(Date fch, int mes) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(fch.getTime());
        cal.add(Calendar.MONTH, mes);
        return new Date(cal.getTimeInMillis());
    }
    //Restarle dias a una fecha determinada
    //@param fch La fecha
    //@param dias Dias a restar
    //@return La fecha restando los dias
    //@Descripcion: ocupo este metodo para calcular el Dia de Aviso

    
    public Date fechaRestarDias(Date fch, int dias) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(fch.getTime());
        cal.add(Calendar.DATE, -dias);
        return new Date(cal.getTimeInMillis());
    }

    
    public Date componerFechaApartirDeDia(Date fch, int dia) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(fch.getTime());
        cal.set(Calendar.DATE, dia);
        return new Date(cal.getTimeInMillis());
    }

    
    public boolean dayIsSame(Calendar fechaUno, Calendar fechaDos) {

        int year1 = fechaUno.get(Calendar.YEAR);
        int year2 = fechaDos.get(Calendar.YEAR);

        UtilLog4j.log.info(this, "dayIsSame: " + (year1 != year2 ? false : fechaUno.get(Calendar.DAY_OF_YEAR) == fechaDos.get(Calendar.DAY_OF_YEAR)));
        return year1 != year2 ? false : fechaUno.get(Calendar.DAY_OF_YEAR) == fechaDos.get(Calendar.DAY_OF_YEAR);
    }

    
    public boolean dayIsSame(Date fechaUno, Date fechaDos) {
//        UtilLog4j.log.info(this,"ManejoFecha.dayIsSame");
        Calendar fechaUnoCalendar = Calendar.getInstance();
        fechaUnoCalendar.setTime(fechaUno);

        Calendar fechaDosCalendar = Calendar.getInstance();
        fechaDosCalendar.setTime(fechaDos);
        UtilLog4j.log.info(this, "fechaUno (" + fechaUnoCalendar.getTime() + ") isSame fechaDos (" + fechaDosCalendar.getTime() + "): " + (dayIsSame(fechaUnoCalendar, fechaDosCalendar)));
        return dayIsSame(fechaUnoCalendar, fechaDosCalendar);
    }

    
    public boolean dayIsToday(Calendar fecha) {
        Calendar cHoy = Calendar.getInstance();
        return (cleanCalendar(fecha).compareTo(cleanCalendar(cHoy)) == 0 ? true : false);
    }

    
    public boolean dayIsToday(Date fecha) {
        UtilLog4j.log.info(this, "dayIsToday()");
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        return dayIsToday(c);
    }

    /**
     * Devuelve: -1 si 'fechaUno' < 'fechaDos' 0 si son el mismo dia 1 si
     * 'fechaUno' > 'fechaDos'
     *
     * @param fechaUno
     * @param fechaDos
     * @return
     */
    
    public int compare(Date fechaUno, Date fechaDos) {
//        UtilLog4j.log.info(this,"SiManejoFechaImpl.compare()");
        UtilLog4j.log.info(this, "FechaUno: " + fechaUno);
        UtilLog4j.log.info(this, "FechaDos: " + fechaDos);
        if (dayIsSame(fechaUno, fechaDos)) {
            UtilLog4j.log.info(this, "compare: " + 0);
            return 0;
        } else {
            UtilLog4j.log.info(this, "compare: " + (theFirstDateIsLessThanTheSecond(fechaUno, fechaDos) ? -1 : 1));
            return (theFirstDateIsLessThanTheSecond(fechaUno, fechaDos) ? -1 : 1);
        }
    }

    
    public boolean dateIsBeforeSpecificTime(Date fecha, int hora, int minuto) {
        UtilLog4j.log.info(this, "SiMaenejoFechaImpl.dateIsBeforeSpecificTime()");
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);

        int horaFecha = c.get(Calendar.HOUR_OF_DAY);
        int minutoFecha = c.get(Calendar.MINUTE);

        UtilLog4j.log.info(this, "Hora-Fecha : " + horaFecha + "-" + minutoFecha + " parámetros: " + hora + "-" + minuto);

        //Calendar con hora y  minuto desde la Fecha a comparar
        Calendar cFecha = Calendar.getInstance();
        cFecha.set(Calendar.HOUR_OF_DAY, horaFecha);
        cFecha.set(Calendar.MINUTE, minutoFecha);

        Calendar cHoraEspecifica = Calendar.getInstance();
        cHoraEspecifica.set(Calendar.HOUR_OF_DAY, hora);
        cHoraEspecifica.set(Calendar.MINUTE, 0);

        UtilLog4j.log.info(this, "Fecha a comparar: " + cFecha.getTime());
        UtilLog4j.log.info(this, "Fecha específica: " + cHoraEspecifica.getTime());

        UtilLog4j.log.info(this, "La fecha es antes que la hora específica: " + theFirstDateIsLessThanTheSecond(cFecha, cHoraEspecifica));

        return theFirstDateIsLessThanTheSecond(cFecha, cHoraEspecifica);
    }

    
    public boolean dateIsTomorrow(Date fecha) {
        Calendar cFecha = Calendar.getInstance();
        cFecha.setTime(fecha);
        cFecha.add(Calendar.DATE, -1);

        UtilLog4j.log.info(this, "datyIsToday: " + dayIsToday(cFecha));
        return dayIsToday(cFecha);
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

        UtilLog4j.log.info(this, "cleanCalendar: " + cal.getTime());
        return cal;
    }

    
    public Calendar getFechaHoy(boolean haveTime) {
        Calendar hoy = Calendar.getInstance();

        if (!haveTime) {
            hoy = cleanCalendar(hoy);
        }

        UtilLog4j.log.info(this, "Hoy: " + hoy.getTime());
        return hoy;
    }

    
    public Calendar converterDateToCalendar(Date date, boolean haveTime) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        if (!haveTime) {
            c = cleanCalendar(c);
        }
        UtilLog4j.log.info(this, "SiManejoFechaImpl.converterDateToCalendar(): " + c.getTime());
        return c;
    }

    
    public int compare(Calendar cUno, Calendar cDos, boolean withTime) {
        UtilLog4j.log.info(this, "SiManejoFechaImpl.compare()");

        if (withTime) {
            UtilLog4j.log.info(this, "cUno: " + cUno.getTime());
            UtilLog4j.log.info(this, "cDos: " + cDos.getTime());
            UtilLog4j.log.info(this, "compare (with time): " + cUno.compareTo(cDos));

            return cUno.compareTo(cDos);
        } else {
            cUno = cleanCalendar(cUno);
            cDos = cleanCalendar(cDos);

            UtilLog4j.log.info(this, "cUno: " + cUno.getTime());
            UtilLog4j.log.info(this, "cDos: " + cDos.getTime());
            UtilLog4j.log.info(this, "compare (without time): " + cUno.compareTo(cDos));

            return cUno.compareTo(cDos);
        }
    }

    /**
     * Modificado por joel rodrigguez cambie int dia = cal.DAY_OF_WEEK; por int
     * dia = cal.get(cal.DAY_OF_WEEK); no tomaba bien el valor del dia
     * 25/nov/2013
     *
     * @param fecha
     * @return
     */
    
    public boolean finSemana(Date fecha) {
        boolean v = false;
        int dia = obtenerNumeroDiaDeFecha(fecha);
        //UtilLog4j.log.info(this,"Fecha de sol "+Constantes.FMT_ddMMyyy.format(fecha));
        //UtilLog4j.log.info(this,"Fecha de calendar "+Constantes.FMT_ddMMyyy.format(cal.getTime()));
        //UtilLog4j.log.info(this,"Dia see "+ dia);
        if (dia == 1 || dia == 7) {
            v = true;
        }

        return v;
    }

    
    public Date traerFechaProlongadaDiaLaboralesApartirHoy() {
        Date fechaP = this.fechaSumarDias(new Date(), 1);
        int dia = obtenerNumeroDiaDeFecha(fechaP);
        switch (dia) {
//            case Constantes.NUMERO_DIA_VIERNES:
//                //sumar 3 dias para llegar a lunes
//                fechaP = this.fechaSumarDias(new Date(), 3);
//                break;
            case Constantes.NUMERO_DIA_SABADO:
                //sumar 3 dias para llegar a lunes
                fechaP = this.fechaSumarDias(new Date(), 2);
                break;
            case Constantes.NUMERO_DIA_DOMINGO:
                //sumar 3 dias para llegar a lunes
                fechaP = this.fechaSumarDias(new Date(), 1);
                break;
        }
        return fechaP;
    }

    
    public int obtenerNumeroDiaDeFecha(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        return cal.get(cal.DAY_OF_WEEK);
    }

    //Verifica horaMinima
    
    public boolean validaHoraMinima(Date horaSalida, Date horaMinima) {
        boolean v;
        try {
            String[] arrHoraSalida = Constantes.FMT_HHmmss.format(horaSalida).split(":");
            Calendar cFechaSalida = Calendar.getInstance();
            cFechaSalida.setTime(Constantes.FMT_ddMMyyy.parse("01/01/1970"));
            cFechaSalida.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrHoraSalida[0]));
            cFechaSalida.set(Calendar.MINUTE, Integer.parseInt(arrHoraSalida[1]));
            UtilLog4j.log.info(this, "Hora Salida: " + cFechaSalida.getTime());
            //

            String[] arrHoraMinima = Constantes.FMT_HHmmss.format(horaMinima).split(":");
            Calendar cFechaMinima = Calendar.getInstance();
            cFechaMinima.setTime(Constantes.FMT_ddMMyyy.parse("01/01/1970"));
            cFechaMinima.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrHoraMinima[0]));
            cFechaMinima.set(Calendar.MINUTE, Integer.parseInt(arrHoraMinima[1]));
            UtilLog4j.log.info(this, "Hora Min: " + cFechaMinima.getTime());
            if (cFechaSalida.getTimeInMillis() < cFechaMinima.getTimeInMillis()) {
                v = true;
            } else {
                v = false;
            }
        } catch (Exception e) {
            v = false;
        }
        return v;
    }

    /**
     * NOTA : Este metodo es usado en al validar la hora de aprobacion
     *
     * @param horaRegreso
     * @param horaMaxima
     * @return
     */
    
    public boolean validaHoraMaxima(Date horaRegreso, Date horaMaxima) {
        boolean v;
        try {
            String[] arrHoraSalida = Constantes.FMT_HHmmss.format(horaRegreso).split(":");
            Calendar cFechaRegreso = Calendar.getInstance();
            cFechaRegreso.setTime(Constantes.FMT_ddMMyyy.parse("01/01/1970"));
            cFechaRegreso.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrHoraSalida[0]));
            cFechaRegreso.set(Calendar.MINUTE, Integer.parseInt(arrHoraSalida[1]));
            UtilLog4j.log.info(this, "Hora Salida: " + cFechaRegreso.getTime());
            //

            String[] arrHoraMaxima = Constantes.FMT_HHmmss.format(horaMaxima).split(":");
            Calendar cFechaMaxima = Calendar.getInstance();
            cFechaMaxima.setTime(Constantes.FMT_ddMMyyy.parse("01/01/1970"));
            cFechaMaxima.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrHoraMaxima[0]));
            cFechaMaxima.set(Calendar.MINUTE, Integer.parseInt(arrHoraMaxima[1]));
            UtilLog4j.log.info(this, "Hora Min: " + cFechaMaxima.getTime());
            if (cFechaRegreso.getTimeInMillis() > cFechaMaxima.getTimeInMillis()) {
                v = true;
            } else {
                v = false;
            }
        } catch (ParseException e) {
            UtilLog4j.log.fatal(e);
            v = false;
        } catch (NumberFormatException e) {
            UtilLog4j.log.fatal(e);
            v = false;
        }
        return v;
    }

    
    public boolean validaSolicitaHoyParaMananaDespuesHora(Date fechaSalida, int idTipoSolicitud) {
        Calendar c = Calendar.getInstance();
        int hm = sgTipoSolicitudViajeRemote.buscarPorId(idTipoSolicitud).getHoraMaxima();
        UtilLog4j.log.info(this, "Hora salida : " + c.get(Calendar.HOUR_OF_DAY) + "  - + -+ - +- +- +- Hora maxima de la solicitud: " + hm);
        if (dateIsTomorrow(fechaSalida) && c.get(Calendar.HOUR_OF_DAY) >= hm) {
            UtilLog4j.log.info(this, "-- -  +- +- True");
            return true;
        } else {
            UtilLog4j.log.info(this, "-- -  +- +- False");
            return false;
        }
    }

    
    public boolean validarSolicitudViajePoliticaHorasAnticipadas(Date fechaSalida, Date horaValidar, int idTipoSolicitud) {
        Calendar c = Calendar.getInstance();
        c.setTime(horaValidar);
        int hm = sgTipoSolicitudViajeRemote.buscarPorId(idTipoSolicitud).getHoraMaxima();
        if (dateIsTomorrow(fechaSalida) && c.get(Calendar.HOUR_OF_DAY) >= hm) {
            UtilLog4j.log.info(this, "La solicitud violó la politica de horas anticipadas por solicitar - se solicito a las : " + c.get(Calendar.HOUR_OF_DAY));
            return true;
        } else {
            return false;
        }
    }

    
    public boolean validaFechaSalidaViaje(Date fechaProgramada, Date horaProgramada) {
        boolean v;
        try {
            Calendar cProg = Calendar.getInstance();
            String[] hora = Constantes.FMT_HHmmss.format(horaProgramada).split(":");
            cProg.setTime(fechaProgramada);
            cProg.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hora[0]));
            cProg.set(Calendar.MINUTE, Integer.parseInt(hora[1]));
            UtilLog4j.log.info(this, "FEcha programada: " + cProg.getTime());
            //
            Calendar cHoy = Calendar.getInstance();
            if (cProg.getTimeInMillis() > cHoy.getTimeInMillis()) {
                v = true;
            } else {
                v = false;
            }
        } catch (Exception e) {
            v = false;
        }
        return v;
    }

    
    public boolean validaHoraMaximaAprobacion(int idRol, int horaMaxima) {
        boolean retur = false;
        try {
            if (idRol == Constantes.ROL_DIRECCION_GENERAL) {
                //si es el director general el que esta conectado
                UtilLog4j.log.info(this, "Es el jefe no se valida la hora de aprobacion");
                retur = false;
            } else {
                //validar si son las 5:0 pm
                //String[] horaReal = Constantes.FMT_HHmmss.format(new Date()).split(":");
                Calendar fecha = Calendar.getInstance();
                fecha.setTime(Constantes.FMT_ddMMyyy.parse("01/01/1970"));
                fecha.set(Calendar.HOUR_OF_DAY, horaMaxima);
                fecha.set(Calendar.MINUTE, 00);
                UtilLog4j.log.info(this, "Hora de aprobacion : " + fecha.getTime());

                if (validaHoraMaxima(new Date(), fecha.getTime())) {
                    retur = true;
                    UtilLog4j.log.info(this, "## La aprobacion esta violando la hora maxima##");
                } else {
                    retur = false;
                    UtilLog4j.log.info(this, "##La aprobacion NO viola la hora maxima##");
                }
            }
        } catch (ParseException ex) {
            Logger.getLogger(SiManejoFechaImpl.class.getName()).log(Level.SEVERE, null, ex);
            retur = false;
        }
        return retur;
    }

    
    public boolean validaHoraMaximaAprobacion(int horaMaxima) {
        boolean retur = false;
        try {
            //String[] horaReal = Constantes.FMT_HHmmss.format(new Date()).split(":");
            Calendar fecha = Calendar.getInstance();
            fecha.setTime(Constantes.FMT_ddMMyyy.parse("01/01/1970"));
            fecha.set(Calendar.HOUR_OF_DAY, horaMaxima);
            fecha.set(Calendar.MINUTE, 00);
            UtilLog4j.log.info(this, "Hora de aprobacion : " + fecha.getTime());

            if (validaHoraMaxima(new Date(), fecha.getTime())) {
                retur = true;
                UtilLog4j.log.info(this, "## La aprobacion esta violando la hora maxima##");
            } else {
                retur = false;
                UtilLog4j.log.info(this, "##La aprobacion NO viola la hora maxima##");
            }

        } catch (ParseException ex) {
            Logger.getLogger(SiManejoFechaImpl.class.getName()).log(Level.SEVERE, null, ex);
            retur = false;
        }
        return retur;
    }

//    public void regresaMeses() {
//	Calendar c = Calendar.getInstance();
//	int mes = c.get(c.MONTH);
//	for (int i = 0; i <= mes; i++) {
//	    String m = Constantes.MESES[i];
//	}
//    }
    
    public int traerAnioActual() {
        Calendar c = Calendar.getInstance();
        return c.get(c.YEAR);
    }

    /**
     *
     * @param cadena
     * @return
     */
    
    public String cambiarddmmyyyyAyyyymmaa(String cadena) {
        StringBuilder sb = new StringBuilder();
        String[] fecha = cadena.split("/");
        sb.append(fecha[2]).append("/").append(fecha[1]).append("/").append(fecha[0]);
        return sb.toString();
    }

    
    public Date convertirStringFechaddMMyyyy(String fecha) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormat.parse(fecha);
        } catch (ParseException ex) {
            Logger.getLogger(SiManejoFechaImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    
    public String convertirFechaStringddMMyyyy(Date fecha) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormat.format(fecha);
        } catch (Exception ex) {
            Logger.getLogger(SiManejoFechaImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    
    public String convertirHoraStringhmma(Date fecha) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
            return dateFormat.format(fecha);
        } catch (Exception ex) {
            Logger.getLogger(SiManejoFechaImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    
    public String convertirHoraStringHHmmss(Date fecha) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            return dateFormat.format(fecha);
        } catch (Exception ex) {
            Logger.getLogger(SiManejoFechaImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    
    public String convertirFechaStringyyyyMMdd(Date fecha) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            return dateFormat.format(fecha);
        } catch (Exception ex) {
            Logger.getLogger(SiManejoFechaImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    
    public String traerStringInicioMesddMMyyyy() {
        Calendar cal = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(cal.getActualMinimum(Calendar.DAY_OF_MONTH)).append("/");
        sb.append(cal.get(Calendar.MONTH)).append("/");
        sb.append(cal.get(Calendar.YEAR));
        return sb.toString();
    }

    
    public Date componerHora(int hora, int minuto) {
        //Calendar con hora y  minuto desde la Fecha a comparar
        Calendar cFecha = Calendar.getInstance();
        cFecha.set(Calendar.HOUR_OF_DAY, hora);
        cFecha.set(Calendar.MINUTE, minuto);
        UtilLog4j.log.info(this, "La hora es : " + cFecha);
        return cFecha.getTime();
    }

    
    public String horasEntreFechas(Date inicio, Date fin) {
        //System.out.println("Inicio : ::  " + inicio);
        //System.out.println("Fin: ::  " + fin);
        float min = (fin.getTime() - inicio.getTime()) / (60 * 1000);
        String hora = String.valueOf(BigDecimal.valueOf(min / 60).intValue());
        String minutos = String.valueOf(BigDecimal.valueOf(min % 60).intValue());

        return hora + " horas y " + minutos + " minutos";
    }

    
    public Date componerFecha(Date fecha, Date hora) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(fecha.getTime());
        Calendar cHora = Calendar.getInstance();
        cHora.setTimeInMillis(hora.getTime());

        c.set(Calendar.HOUR_OF_DAY, cHora.get(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, cHora.get(Calendar.MINUTE));
        return c.getTime();
    }

    
    public Date sumarTiempo(Date horaSalida, int hr, int min) {
        boolean v;
        try {
            String[] arrHoraSalida = Constantes.FMT_HHmmss.format(horaSalida).split(":");
            Calendar cFechaSalida = Calendar.getInstance();
            cFechaSalida.setTime(Constantes.FMT_ddMMyyy.parse("01/01/1970"));
            cFechaSalida.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrHoraSalida[0]) + hr);
            cFechaSalida.set(Calendar.MINUTE, Integer.parseInt(arrHoraSalida[1]) + min);
            UtilLog4j.log.info(this, "Hora Salida: " + cFechaSalida.getTime());
            return cFechaSalida.getTime();

        } catch (Exception e) {
            return null;
        }
    }

    
    public List<SelectItem> meses() {
        List<SelectItem> meses = new ArrayList<SelectItem>();
        meses.add(new SelectItem(1, "Enero"));
        meses.add(new SelectItem(2, "Febrero"));
        meses.add(new SelectItem(3, "Marzo"));
        meses.add(new SelectItem(4, "Abril"));
        meses.add(new SelectItem(5, "Mayo"));
        meses.add(new SelectItem(6, "Junio"));
        meses.add(new SelectItem(7, "Julio"));
        meses.add(new SelectItem(8, "Agosto"));
        meses.add(new SelectItem(9, "Septiembre"));
        meses.add(new SelectItem(10, "Octubre"));
        meses.add(new SelectItem(11, "Noviembre"));
        meses.add(new SelectItem(12, "Diciembre"));
        return meses;
    }

    
    public boolean salidaProximoLunes(Date actual, Date salida, int estatus) { //se creo la variable correo para regresar true solo tomando encuenta la fecha de salida
        //se verifica fecha y hora 
        boolean v = false;
        int hoy = obtenerNumeroDiaDeFecha(actual);
        int sal = obtenerNumeroDiaDeFecha(salida);
        Calendar hora = Calendar.getInstance();
        int hactual = hora.get(Calendar.HOUR_OF_DAY);
        int s = numeroSemana(salida);
        int h = numeroSemana(actual);
        if (hoy == 6 && sal == 2) {//los días corren del 1 al 7 empezando en domingo

            if ((estatus == Constantes.ESTATUS_APROBAR && (s == (h + 1)) && (hactual >= 12))
                    || (estatus == Constantes.ESTATUS_PENDIENTE && (s == (h + 1)) && (hactual >= 10))) {
                v = true;
            }

        } else if ((hoy == 7 || hoy == 1) && sal == 2 && s == (h + 1)) {
            v = true;
        } 

        return v;
    }

    public int numeroSemana(Date dia) {
        Calendar fecha = Calendar.getInstance();
        fecha.setTime(dia);
        int semana = fecha.get(Calendar.WEEK_OF_YEAR);

        return semana;
    }

    
    public Date traerDiaSemana(Date hoy, Date salida) {
        Date regresa = new Date();
        int numDia = obtenerNumeroDiaDeFecha(hoy);
        int diasSumar = 0;
        int semanaActual = numeroSemana(hoy);
        int semanaSalida = numeroSemana(salida);

        if ((semanaActual + 1) == semanaSalida) {
            if (numDia == 6) {
                regresa = hoy;
            } else {
                diasSumar = 6-numDia;
                regresa = fechaSumarDias(hoy, diasSumar);
            }
        } else {
            salida = fechaRestarDias(salida, 3);
            diasSumar = dias(hoy, salida);
            regresa = fechaSumarDias(hoy, diasSumar);
        }

        return regresa;

    }
}
