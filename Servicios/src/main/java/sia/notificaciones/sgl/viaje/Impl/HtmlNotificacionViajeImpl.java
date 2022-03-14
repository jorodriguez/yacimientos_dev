/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.sgl.viaje.Impl;

import com.google.common.base.Splitter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import org.apache.commons.collections.IteratorUtils;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.correo.impl.CodigoHtml;
import sia.excepciones.SIAException;
import sia.modelo.SgSolicitudEstancia;
import sia.modelo.SgSolicitudViaje;
import sia.modelo.SgVehiculo;
import sia.modelo.SgViaje;
import sia.modelo.SgViajeVehiculo;
import sia.modelo.SgViajero;
import sia.modelo.SiPlantillaHtml;
import sia.modelo.Usuario;
import sia.modelo.sgl.estancia.vo.DetalleEstanciaVO;
import sia.modelo.sgl.viaje.vo.DetalleItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.ItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.MotivoRetrasoVO;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeLugarVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.EstatusAprobacionVO;
import sia.modelo.sgl.vo.SgDetalleRutaTerrestreVo;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.notificaciones.sgl.impl.HtmlNotificacionServiciosGeneralesImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.sgl.impl.SgDetalleSolicitudEstanciaImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.servicios.sgl.impl.SgViajeCiudadImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaCiudadImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaLugarImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgItinerarioImpl;
import sia.servicios.sgl.viaje.impl.SgMotivoRetrasoImpl;
import sia.servicios.sgl.viaje.impl.SgRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgUbicacionImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeLugarImpl;
import sia.servicios.sgl.viaje.impl.SgViajeVehiculoImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiPlantillaHtmlImpl;
import sia.util.UtilLog4j;

/*
 *
 * @author mluis
 */
@LocalBean 
public class HtmlNotificacionViajeImpl extends CodigoHtml {

    @Inject
    private SiPlantillaHtmlImpl plantillaHtml;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeRemote;
    @Inject
    private SgItinerarioImpl sgItinerarioRemote;
    @Inject
    private SgViajeroImpl sgViajeroRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private SgDetalleRutaTerrestreImpl sgDetalleRutaTerrestreRemote;
    @Inject
    private SgViajeImpl sgViajeRemote;
    @Inject
    private SgViajeVehiculoImpl sgViajeVehiculoRemote;
    @Inject
    private SgViajeCiudadImpl sgViajeCiudadRemote;
    @Inject
    private SgDetalleRutaCiudadImpl sgDetalleRutaCiudadRemote;
    @Inject
    private SgEstatusAprobacionImpl sgEstatusAprobacionRemote;
    @Inject
    private SgDetalleRutaLugarImpl sgDetalleRutaLugarRemote;
    @Inject
    private SgViajeLugarImpl sgViajeLugarRemote;
    @Inject
    private SgVehiculoImpl sgVehiculoRemote;
    @Inject
    private SiManejoFechaImpl  siManejoFechaLocal;
    @Inject
    private SgMotivoRetrasoImpl sgMotivoRetrasoRemote;
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreRemote;
    @Inject
    private SiCiudadImpl siCiudadRemote;
    @Inject
    private SgDetalleSolicitudEstanciaImpl detalleSolicitudEstanciaRemote;
    @Inject
    private SgUbicacionImpl ubicacionRemote;
    @Inject
    private SgSolicitudEstanciaImpl estanciaRemote;

    //
    private Usuario getResponsableByGerencia(int idGerencia) {
        return this.gerenciaRemote.getResponsableByApCampoAndGerencia(1, idGerencia, false);
    }

    /**
     * MLUIS 01/11/2013
     */
    /**
     *
     * @param listTemp
     * @param idVehiculo
     * @param fechaProgramada
     * @param horaProgramada
     * @param fechaSalida
     * @param horaSalida
     * @param fechaRegreso
     * @param horaRegreso
     * @param redondo
     * @param idViajeIda
     * @param codigo
     * @param vehiculoEmpresa
     * @param autobus
     * @param vehiculoPropio
     * @param tipoViaje
     * @return
     */
    
    public StringBuilder bodyMailTravelCompanyCar(List<ViajeroVO> listTemp, int idVehiculo,
            Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            int idViajeIda, String codigo, boolean vehiculoEmpresa, boolean autobus, boolean vehiculoPropio, int tipoViaje, String origen,
            int idRuta, String conductorResponsable, String telefono) {
        log("bodyMailTravelCompanyCar");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        String titulo = "Viaje terrestre - ".concat(!redondo ? " Sencillo" : " (Regreso)");
        this.cuerpoCorreo.append(this.getTitulo(titulo));
        this.cuerpoCorreo.append("Se ha programado el Viaje <b>".concat(codigo).concat("</b></p>"));
        this.cuerpoCorreo.append("<p> A continuación se muestra la información correspondiente: </p>");
        //DAtos del viaje
        datosViajeParaViajero(fechaProgramada, horaProgramada, fechaSalida, horaSalida, redondo);
        if (vehiculoEmpresa) {
            datosVehiculoPorId(idVehiculo);
        }

        // tabla de viajeros
        listaViajeros(listTemp);
        this.cuerpoCorreo.append("</br>");
        switch (tipoViaje) {
            case Constantes.RUTA_TIPO_CIUDAD:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_CIUDAD);
                break;
            case Constantes.RUTA_TIPO_OFICINA:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_OFICINA);
                break;
            case Constantes.RUTA_TIPO_LUGAR:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_LUGAR);
                break;
        }
        this.cuerpoCorreo.append("</br>");
        //poner conductor
        if (vehiculoEmpresa) {
            agregarConductorResponsable(conductorResponsable, true, false, "100%", telefono);
        } else {
            agregarConductorResponsable(conductorResponsable, false, false, "100%", telefono);
        }

        this.cuerpoCorreo.append("</br>");
        this.cuerpoCorreo.append("<div style=\"margin-top: 20px; margin-bottom: 20px; font-style: italic; text-align: justify;background-color: #fcf8e3; border-color: #faebcc; color: #8a6d3b; border-radius: 4px; border-top-color: #f7e1b5; padding: 15px;\" class=\"col-lg-12 alert alert-warning\">");
        this.cuerpoCorreo.append("La programación del viaje queda sujeta a las condiciones de seguridad impuesta por <strong>Gestión de Riesgos</strong>, así como a la disponibilidad de vehículos y choferes en base a la logística del día de <strong>Servicios Generales y Logística</strong>.");
        this.cuerpoCorreo.append("</div>");
        this.cuerpoCorreo.append("</br>");

        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    /**
     * MLUIS 01/11/2013
     */
    
    public StringBuilder bodyMailTravelCompanyCarForGeneroViaje(int idVehiculo, List<ViajeroVO> sgViajeroList,
            Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            int idViajeIda, String codigo, boolean vehiculoEmpresa, boolean autobus, boolean vehiculoPropio, int tipoViaje, String origen,
            int idRuta, String conductorResponsable,
            String telefono, String responsableGerencia) {
        log("HtmlNotificacionViajeImpl.bodyMailTravelCompanyCarForGeneroViaje()");

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Viaje terrestre - ".concat(codigo)));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<p> Se ha programado el Viaje <b>".concat(codigo).concat("</b></p>"));
        this.cuerpoCorreo.append("<p> A continuación se muestra la información correspondiente: </p>");
        this.cuerpoCorreo.append("<br/>");

        datosViajeVO(fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo);
        if (vehiculoEmpresa) {
            datosVehiculoPorId(idVehiculo);
        }

        if (sgViajeroList != null) {
            listaViajeros(sgViajeroList);
        } else {
            this.cuerpoCorreo.append("</br>");
            this.cuerpoCorreo.append("El viaje <b>").append(codigo).append("</b> se generó sin viajeros. ").append(" Posteriormente se podrán agregar ");
        }
//        this.cuerpoCorreo.append("</table>");
        switch (tipoViaje) {
            case Constantes.RUTA_TIPO_CIUDAD:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_CIUDAD);
                break;
            case Constantes.RUTA_TIPO_OFICINA:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_OFICINA);
                break;
            case Constantes.RUTA_TIPO_LUGAR:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_LUGAR);
                break;
        }
        this.cuerpoCorreo.append("</br>");
        //poner conductor
        if (vehiculoEmpresa) {
            agregarConductorResponsable(conductorResponsable, true, false, "100%", telefono);
        } else {
            agregarConductorResponsable(conductorResponsable, false, false, "100%", telefono);
        }

        this.cuerpoCorreo.append("</br>");
        this.cuerpoCorreo.append("<div style=\"margin-top: 20px; margin-bottom: 20px; font-style: italic; text-align: justify;background-color: #fcf8e3; border-color: #faebcc; color: #8a6d3b; border-radius: 4px; border-top-color: #f7e1b5; padding: 15px;\" class=\"col-lg-12 alert alert-warning\">");
        this.cuerpoCorreo.append("La programación del viaje queda sujeta a las condiciones de seguridad impuesta por <strong>Gestión de Riesgos</strong>, así como a la disponibilidad de vehículos y choferes en base a la logística del día de <strong>Servicios Generales y Logística</strong>.");
        this.cuerpoCorreo.append("</div>");
        this.cuerpoCorreo.append("</br>");

        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    /**
     * MLUIS 01/11/2013
     */
    
    public StringBuilder bodyMailTravelNoCompanyCar(SolicitudViajeVO solicitudViaje, int idViajero,
            Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            int idViajeIda, String codigo, boolean vehiculoEmpresa, boolean autobus, boolean vehiculoPropio, int tipoViaje, String origen,
            int idRuta, String conductorResponsable, String telefono, String responsableGerencia) {
        log("HtmlNotificacionViajeImpl.bodyMailTravelNoCompanyCar()");

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SgViajero sgViajero = sgViajeroRemote.find(idViajero);

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Viaje - " + codigo));
        this.cuerpoCorreo.append("<br/><p>Estimado(a) <b> ".concat(responsableGerencia).concat("</b>"));
        this.cuerpoCorreo.append("<p> Se procesó la Solicitud de Viaje <b>".concat(solicitudViaje.getCodigo()).concat("</b> y se ha generado el Viaje <b>".concat(codigo).concat("</b></p>")));
        this.cuerpoCorreo.append("<p> A continuación se muestra la información correspondiente: </p>");
        this.cuerpoCorreo.append("<table width=\"100%\" border=\"0\" cellspacing=\"8\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\" >");
        this.cuerpoCorreo.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" ><tr><th colspan=\"2\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:16px; background-color:#0099FF;\">Datos de la solicitud</th></tr>");
        this.cuerpoCorreo.append("<tr><td width=\"40%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>".concat("Código").concat("</b></td>"));
        this.cuerpoCorreo.append("<td width=\"60%\" style=\"border: 1px solid #b5b5b5;\">".concat(codigo).concat(" </td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\"><b>".concat("Gerencia</b></td>"));
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat(solicitudViaje.getGerencia()).concat(" </td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">".concat("<b>Fecha de solicitud</b> </td>"));
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat(Constantes.FMT_TextDate.format(solicitudViaje.getFechaSalida())).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\"><b>Hora solicitada </b></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat(Constantes.FMT_hmm_a.format(solicitudViaje.getHoraSalida())+"Hrs").concat("</td></tr>"));
        this.cuerpoCorreo.append("</table></td>");
        this.cuerpoCorreo.append("<td ><table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\" >");
        this.cuerpoCorreo.append("<tr > <th colspan=\"2\" style=\"font-family:Georgia, 'Times New Roman\', Times, serif; font-weight:bold; color:#FFFFFF; font-size:16px; background-color:#0099FF;\" >Datos del viaje</th></tr>");
//        this.cuerpoCorreo.append("<tr>	<td width=\"41%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\">Responsable</td>");
//        this.cuerpoCorreo.append("<td width=\"59%\" height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(sgViaje.getResponsable().getNombre()).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Fecha salida </td>");
        this.cuerpoCorreo.append("<td height=\"18\"  style=\"border: 1px solid #b5b5b5;\">".concat(Constantes.FMT_TextDate.format(fechaProgramada)).concat("</td> </tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Hora salida </td>");
        this.cuerpoCorreo.append("<td height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(Constantes.FMT_hmm_a.format(horaProgramada)+"Hrs").concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Fecha regreso </td>");
        this.cuerpoCorreo.append("<td height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(fechaRegreso != null ? Constantes.FMT_TextDate.format(fechaRegreso) : "-").concat("</td> </tr>"));
        this.cuerpoCorreo.append("<tr> <td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Hora regreso </td>");
        this.cuerpoCorreo.append("<td height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(horaRegreso != null ? Constantes.FMT_hmm_a.format(horaRegreso)+"Hrs" : "-").concat("</td> </tr>"));
        this.cuerpoCorreo.append("</table></td></tr></table></br><table width=\"100%\"><tr><td>");
        this.cuerpoCorreo.append("</td> </tr> </table>");
        this.cuerpoCorreo.append("<table width=\"100%\" cellspacing=\"0\"> <tr> <th style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:16px; background-color:#0099FF;\"> Viajero(s)</th> </tr>");
        this.cuerpoCorreo.append("<tr> <td width=\"13%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\">Nombre</td>");
        this.cuerpoCorreo.append("<tr> <td style=\"border: 1px solid #b5b5b5;\">".concat(sgViajero.getUsuario() != null ? sgViajero.getUsuario().getNombre() : sgViajero.getSgInvitado().getNombre()).concat("</td> </tr>"));
        this.cuerpoCorreo.append(" </table>");
        switch (tipoViaje) {
            case Constantes.RUTA_TIPO_CIUDAD:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_CIUDAD);
                break;
            case Constantes.RUTA_TIPO_OFICINA:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_OFICINA);
                break;
            case Constantes.RUTA_TIPO_LUGAR:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_LUGAR);
                break;
        }
        //poner conductor
        agregarConductorResponsable(conductorResponsable, false, false, "100%", telefono);
        if (autobus) {
            this.cuerpoCorreo.append("<p><b>Nota:</b> El viajero va a viajar en <u>autobús</u></p>");
        } else if (vehiculoPropio) {
            this.cuerpoCorreo.append("<p><b>Nota:</b> El viajero va a viajar en <u>vehículo propio</u></p>");
        }

        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder bodyMailCancelTripCompanyCar(String nombre, String gerenteResponsable, List<ViajeroVO> listTemp,
            int idVehiculo, String codigo, Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso,
            Date horaRegreso, boolean redondo, String motivo, String responsable, String telefono) {
        log("bodyMailCancelTripCompanyCar");
        log("Cancelando el viaje + + + + + + + + + + + + + + ++  + ++ +  ++ + + + + ++ : " + codigo);
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Cancelación de viaje - " + codigo));
        this.cuerpoCorreo.append("<br/><p>Estimado(a) <b> ".concat(gerenteResponsable).concat("</b>"));
        //this.cuerpoCorreo.append("<p> El viaje programado para el día ".concat(sdf.format(sgViaje.getFechaSalida())).concat(", ha sido <b>cancelado</b> por el motivo que a continuación se menciona.").concat(sgSolicitudViaje.getCodigo()).concat("</b></p>"));
        this.cuerpoCorreo.append("<p> El viaje programado para el día ".concat(Constantes.FMT_TextDateLarge.format(fechaProgramada)).concat(", ha sido <b>cancelado</b> por el motivo que a continuación se menciona.").concat("</b></p>"));
        //Cancelo
        //Cancelo
        datosCancelacion(nombre, motivo);
        //Cancelo
        ///------------Datos del viaje
        datosViajeVO(fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo);
        if (idVehiculo != 0) {
            datosVehiculoPorId(idVehiculo);
        }
        listaViajeros(listTemp);
//--
        agregarConductorResponsable(responsable, true, true, "85%", telefono);
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append(plantilla.getFin());
        //
        log("Canceló el viaje : " + codigo);
        return this.cuerpoCorreo;
    }

    
    public StringBuilder bodyMailCancelTravelNoCompanyCar(String nombre, SgViaje sgViaje, String motivo) {
        log("HtmlNotificacionViajeImpl.bodyMailCancelTravelNoCompanyCar()");

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        List<ViajeroVO> lv = sgViajeroRemote.getTravellersByTravel(sgViaje.getId(), null);
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Cancelación de viaje - " + sgViaje.getCodigo()));
        this.cuerpoCorreo.append("<br/><p>Estimado(a) <b> ".concat(sgViaje.getResponsable().getNombre()).concat("</b>"));
        this.cuerpoCorreo.append("<p> El viaje programado para el día ".concat(sgViaje.getFechaSalida() != null ? sdf.format(sgViaje.getFechaSalida()) : sdf.format(sgViaje.getFechaProgramada())).concat(", ha sido <b>cancelado</b> por el motivo que a continuación se menciona.</b></p>"));
        //Cancelo
        datosCancelacion(nombre, motivo);
        ///
        //datosViaje(sgViaje);
        datosViajeParaViajero(sgViaje.getFechaProgramada(), sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(), sgViaje.isRedondo());
        //
        listaViajeros(lv);
//
        agregarConductorResponsable(sgViaje.getResponsable().getNombre(), true, true, "85%", sgViaje.getResponsable().getTelefono());
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder sendMailCancelTraveller(SgViajero sgViajero, String motivo, Usuario gerente) {
        log("sendMailCancelTraveller");

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Cancelación de viajero "));
        this.cuerpoCorreo.append("<br/><p>Estimado(a) <b> ".concat(gerente.getNombre()).concat("</b>"));

        if (sgViajero.getSgViaje() != null) {
            this.cuerpoCorreo.append("<p> El viajero <b> ".concat(sgViajero.getUsuario() != null ? sgViajero.getUsuario().getNombre() : sgViajero.getSgInvitado().getNombre()).concat(" </b> programado en el viaje <b>").concat(sgViajero.getSgViaje().getCodigo()).concat(", </b>con fecha programada de salida ".concat(sdf.format(sgViajero.getSgViaje().getFechaProgramada())).concat(" Perteneciente a la solicitud <b>").concat(sgViajero.getSgSolicitudViaje().getCodigo()).concat("</b> ha sido <b>cancelado</b> por el motivo que a continuación se menciona.</p>")));
            this.cuerpoCorreo.append("<p> Motivo: ".concat(motivo).concat("</p>"));
        } else {
            this.cuerpoCorreo.append("<p> El viajero <b> ".concat(sgViajero.getUsuario() != null ? sgViajero.getUsuario().getNombre() : sgViajero.getSgInvitado().getNombre()).concat("</b> de la solicitud de viaje <b>").concat(sgViajero.getSgSolicitudViaje().getCodigo()).concat(", ha sido <b>cancelado</b> por el motivo que a continuación se menciona.</b></p>"));
            this.cuerpoCorreo.append("<p> Motivo: ".concat(motivo).concat("</p>"));
        }
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlNextAuthorizationSgSolicitudViaje(SolicitudViajeVO solicitudViajeVO, String titulo, int idEstatus, String usuario, int idCampo) {
        log("HtmlNotificacionViajeImpl.getHtmlNextAuthorizationSgSolicitudViaje()");
        String cad = "";
        String link = "";
        boolean tipoA = false;
        List<ViajeroVO> lv = sgViajeroRemote.getAllViajerosList(solicitudViajeVO.getIdSolicitud());
        if (idEstatus == Constantes.ESTATUS_JUSTIFICAR) {
            cad = "Autorizar";
        } else {
            cad = "Aprobar";
            link = "<center><a style = \"" + getBotonConfirmar() + "\" HREF=" + Configurador.urlSia() + "ServiciosGenerales/AUTSV?mg4mvrg235m=" + solicitudViajeVO.getIdSolicitud() + "&v3g9m93v=" + usuario + "&e3g9a9m=" + idEstatus + "&4ca3p0="+idCampo+">" + cad + " solicitud </a></center>";
        }
        if (solicitudViajeVO.getIdSgTipoEspecifico() == Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA) {
            tipoA = true;
        }

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo = gerenciaRemote.traerResponsablePorApCampoYGerencia(idCampo, solicitudViajeVO.getIdGerencia());
        try {
            this.limpiarCuerpoCorreo();
            this.cuerpoCorreo.append(plantilla.getInicio());
            this.cuerpoCorreo.append(this.getTitulo(titulo));
            log("Tipo de solicitud " + solicitudViajeVO.getIdSgTipoEspecifico());
            if (solicitudViajeVO.getIdSgTipoEspecifico() == Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_TERRESTRE && idEstatus != Constantes.ESTATUS_JUSTIFICAR) {
                agregarNotaHorarioAprobacion(solicitudViajeVO.getFechaSalida());
            }
            this.cuerpoCorreo.append("<br/>");
            mostrarEncabezadoSolicitudViaje(solicitudViajeVO.getTipoSolicitud(), solicitudViajeVO.getTipoEspecifico(), usuarioResponsableGerenciaVo.getNombreUsuario(), solicitudViajeVO.getGerencia());

            if (!tipoA) {
                this.cuerpoCorreo.append("<br/>");
                this.cuerpoCorreo.append("<center>".concat(link).concat("</center>"));
            } else {
                if (idEstatus != Constantes.ESTATUS_APROBAR) {
                    this.cuerpoCorreo.append("<br/>");
                    this.cuerpoCorreo.append("<center>".concat(link).concat("</center>"));
                }
            }

            this.cuerpoCorreo.append("<br/><br/>");
            mostrarDetalleSolicitudViajeTerrestre(solicitudViajeVO);
            this.cuerpoCorreo.append("<br/>");
            listaViajeros(lv);
            if (tipoA) {
                String hotel = "";
                String dir = "";
                String ubicacion = "";
                if (lv.size() > 0) {

                    for (ViajeroVO via : lv) {
                        if (via.isEstancia()) {
                            SgSolicitudEstancia se = estanciaRemote.find(via.getSgSolicitudEstancia());
                            List<DetalleEstanciaVO> de = detalleSolicitudEstanciaRemote.traerDetallePorSolicitud(se.getId(), Constantes.BOOLEAN_FALSE);
                            if (de.size() == 1) {
                                String des = de.get(0).getDescripcion();
                                if (des != null && !des.isEmpty()) {
                                    Iterable<String> datos = Splitter.on("-").trimResults().split(des);
                                    List<String> l = IteratorUtils.toList(datos.iterator());

                                    if (!l.isEmpty()) {
                                        dir = l.get(0);
                                        hotel = l.get(1);
                                    }
                                    
                                    if (se.getSgUbicacion()!= null && se.getSgUbicacion().getId() > 0){
                                        ubicacion = ubicacionRemote.find(se.getSgUbicacion().getId()).getNombre();
                                    } else {
                                        ubicacion = "sin Sugerencia";
                                    }
                                    
                                }
                            }
                            break;
                        }
                    }
                }

                detallesEstanciaAerea(hotel, dir, ubicacion);
                if(idEstatus == Constantes.ESTATUS_APROBAR){
                    this.cuerpoCorreo.append("<br/>");
                this.cuerpoCorreo.append("<div style=\"font-size: 16px;margin-top: 20px; margin-bottom: 20px; font-style: italic; text-align: center; background-color: #fcf8e3; border-color: #faebcc; color: #8a6d3b; border-radius: 4px; border-top-color: #f7e1b5; padding: 15px;\" class=\"col-lg-12 alert alert-warning lead\">");
                this.cuerpoCorreo.append("<strong>Favor de responder este correo a todos los involucrados.</strong>");
                this.cuerpoCorreo.append("</div>");
                this.cuerpoCorreo.append("<br/>");
                }
                
            } else {
                this.cuerpoCorreo.append("<br/>");
                this.cuerpoCorreo.append("<div style=\"margin-top: 20px; margin-bottom: 20px; font-style: italic; text-align: justify;background-color: #fcf8e3; border-color: #faebcc; color: #8a6d3b; border-radius: 4px; border-top-color: #f7e1b5; padding: 15px;\" class=\"col-lg-12 alert alert-warning\">");
                this.cuerpoCorreo.append("La programación del viaje queda sujeta a las condiciones de seguridad impuesta por <strong>Gestión de Riesgos</strong>, así como a la disponibilidad de vehículos y choferes en base a la logística del día de <strong>Servicios Generales y Logística</strong>.");
                this.cuerpoCorreo.append("</div>");
                this.cuerpoCorreo.append("<br/>");
            }

            if (solicitudViajeVO.getIdSgTipoEspecifico() == Constantes.SOLICITUDES_TERRESTRE) {
                mostrarObservacionSolicitudViaje(solicitudViajeVO.getMotivo(), solicitudViajeVO.getObservacion());
                //this.cuerpoCorreo.append("<br/>");
            }
            if (solicitudViajeVO.getJustIncumSol() != null) {
                cuerpoCorreo.append("<br/>");
                mostrarJustificacionGerencia(solicitudViajeVO.getJustIncumSol().getJustifico(), solicitudViajeVO.getJustIncumSol().getMotivoJustifiacion(), solicitudViajeVO.getJustIncumSol().getFecha(), solicitudViajeVO.getJustIncumSol().getHora());
            }
            if (solicitudViajeVO.getIdMotivoRetraso() != 0) {
                cuerpoCorreo.append("<br/>");
                mostrarJustificacionRetrasoSolicitudViaje(solicitudViajeVO.getMotivoRetrasoVo());
            }
            this.cuerpoCorreo.append("<br/>");
//            mostrarHistorialAprobacionesSolicitud(solicitudViajeVO.getIdSolicitud());

            this.cuerpoCorreo.append(plantilla.getFin());
            return this.cuerpoCorreo;
        } catch (SIAException siae) {
            Logger.getLogger(HtmlNotificacionViajeImpl.class.getName()).log(Level.SEVERE, null, siae);
            siae.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public StringBuilder getHtmlNextAuthorizationSgSolicitudesViaje(SolicitudViajeVO solicitudViajeVO, SolicitudViajeVO solicitudViajeVO2, String titulo, int idEstatus, String usuario) {
        log("HtmlNotificacionViajeImpl.getHtmlNextAuthorizationSgSolicitudViaje()");
        String cad = "";
        if (idEstatus == Constantes.ESTATUS_JUSTIFICAR) {
            cad = "Autorizar";
        } else {
            cad = "Aprobar";
        }
        String link = "<center><a style = \"" + getBotonConfirmar() + "\" HREF='" + Configurador.urlSia() + "ServiciosGenerales/AUTSV?mg4mvrg235m=" + solicitudViajeVO.getIdSolicitud() + "&v3g9m93v=" + usuario + "&e3g9a9m=" + idEstatus + "&mg4mvrg235m2=" + solicitudViajeVO2.getIdSolicitud() + "'>" + cad + " solicitud </a></center>";
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo = gerenciaRemote.traerResponsablePorApCampoYGerencia(1, solicitudViajeVO.getIdGerencia());
        UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo2 = gerenciaRemote.traerResponsablePorApCampoYGerencia(1, solicitudViajeVO2.getIdGerencia());
        try {
            this.limpiarCuerpoCorreo();
            this.cuerpoCorreo.append(plantilla.getInicio());
            this.cuerpoCorreo.append(this.getTitulo(titulo));
            log("Tipo de solicitud " + solicitudViajeVO.getIdSgTipoEspecifico());
            if (solicitudViajeVO.getIdSgTipoEspecifico() == Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_TERRESTRE && idEstatus != Constantes.ESTATUS_JUSTIFICAR) {
                agregarNotaHorarioAprobacion(solicitudViajeVO.getFechaSalida());
            }
            this.cuerpoCorreo.append("<br/>");
            mostrarEncabezadoSolicitudViaje(solicitudViajeVO.getTipoSolicitud(), solicitudViajeVO.getTipoEspecifico(), usuarioResponsableGerenciaVo.getNombreUsuario(), solicitudViajeVO.getGerencia());
            this.cuerpoCorreo.append("<br/>");
            this.cuerpoCorreo.append("<center>".concat(link).concat("</center>"));
            this.cuerpoCorreo.append("<br/><br/>");
            mostrarDetalleSolicitudViajeTerrestres(solicitudViajeVO, solicitudViajeVO2);
            this.cuerpoCorreo.append("<br/>");
            listaViajeros(sgViajeroRemote.getAllViajerosList(solicitudViajeVO2.getIdSolicitud()));
            this.cuerpoCorreo.append("<br/>");
            if (solicitudViajeVO.getIdSgTipoEspecifico() == Constantes.SOLICITUDES_TERRESTRE) {
                mostrarObservacionSolicitudViaje(solicitudViajeVO.getMotivo(), solicitudViajeVO.getObservacion());
                //this.cuerpoCorreo.append("<br/>");
            }
            if (solicitudViajeVO.getJustIncumSol() != null) {
                cuerpoCorreo.append("<br/>");
                mostrarJustificacionGerencia(solicitudViajeVO.getJustIncumSol().getJustifico(), solicitudViajeVO.getJustIncumSol().getMotivoJustifiacion(), solicitudViajeVO.getJustIncumSol().getFecha(), solicitudViajeVO.getJustIncumSol().getHora());
            }
            if (solicitudViajeVO.getIdMotivoRetraso() != 0) {
                cuerpoCorreo.append("<br/>");
                mostrarJustificacionRetrasoSolicitudViaje(solicitudViajeVO.getMotivoRetrasoVo());
            }
            this.cuerpoCorreo.append("<br/>");
//            mostrarHistorialAprobacionesSolicitud(solicitudViajeVO.getIdSolicitud());

            this.cuerpoCorreo.append(plantilla.getFin());
            return this.cuerpoCorreo;
        } catch (SIAException siae) {
            Logger.getLogger(HtmlNotificacionViajeImpl.class.getName()).log(Level.SEVERE, null, siae);
            siae.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public StringBuilder getHtmlSolicitarViaje(SolicitudViajeVO solicitudViaje, String responsableGerencia) {
        log("HtmlNotificacionViajeImpl.getHtmlSolicitarViaje()");
        try {
            SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
            boolean tipoA = false;

            this.limpiarCuerpoCorreo();
            this.cuerpoCorreo.append(plantilla.getInicio());
            this.cuerpoCorreo.append(this.getTitulo(Constantes.MENSAJE_ASUNTO_CORREO_SOLICITAR_VIAJE + solicitudViaje.getCodigo()));
            log("Tipo de solicitud " + solicitudViaje.getIdSgTipoEspecifico());
            if (solicitudViaje.getIdSgTipoEspecifico() != Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_AEREA) {
                agregarNotaHorarioAprobacion(solicitudViaje.getFechaSalida());
            } else {
                tipoA = true;
            }

            this.cuerpoCorreo.append("<br/>");
            mostrarEncabezadoSolicitudViaje(solicitudViaje.getTipoSolicitud(), solicitudViaje.getTipoEspecifico(), responsableGerencia, solicitudViaje.getGerencia());
            this.cuerpoCorreo.append("<br/>");
            mostrarDetalleSolicitudViajeTerrestre(solicitudViaje);
            this.cuerpoCorreo.append("<br/>");
            List<ViajeroVO> lv = sgViajeroRemote.getAllViajerosList(solicitudViaje.getIdSolicitud());
            listaViajeros(lv);
            this.cuerpoCorreo.append("<br/>");
            if (tipoA) {
                String hotel = "";
                String dir = "";
                String ubicacion = "";
                if (lv.size() > 0) {

                    for (ViajeroVO via : lv) {
                        if (via.isEstancia()) {
                            SgSolicitudEstancia se = estanciaRemote.find(via.getSgSolicitudEstancia());
                            List<DetalleEstanciaVO> de = detalleSolicitudEstanciaRemote.traerDetallePorSolicitud(se.getId(), Constantes.BOOLEAN_FALSE);
                            if (de.size() == 1) {
                                String des = de.get(0).getDescripcion();
                                if (des != null && !des.isEmpty()) {
                                    Iterable<String> datos = Splitter.on("-").trimResults().split(des);
                                    List<String> l = IteratorUtils.toList(datos.iterator());

                                    if (!l.isEmpty()) {
                                        dir = l.get(0);
                                        hotel = l.get(1);
                                    }
                                    ubicacion = ubicacionRemote.find(se.getSgUbicacion().getId()).getNombre();
                                }
                            }
                            break;
                        }
                    }
                }

                detallesEstanciaAerea(hotel, dir, ubicacion);
            } else {
                this.cuerpoCorreo.append("<div style=\"margin-top: 20px; margin-bottom: 20px; font-style: italic; text-align: justify;background-color: #fcf8e3; border-color: #faebcc; color: #8a6d3b; border-radius: 4px; border-top-color: #f7e1b5; padding: 15px;\" class=\"col-lg-12 alert alert-warning\">");
                this.cuerpoCorreo.append("La programación del viaje queda sujeta a las condiciones de seguridad impuesta por <strong>Gestión de Riesgos</strong>, así como a la disponibilidad de vehículos y choferes en base a la logística del día de <strong>Servicios Generales y Logística</strong>.");
                this.cuerpoCorreo.append("</div>");
            }

            this.cuerpoCorreo.append("<br/>");
            if (solicitudViaje.getIdSgTipoEspecifico() == Constantes.SOLICITUDES_TERRESTRE) {
                mostrarObservacionSolicitudViaje(solicitudViaje.getMotivo(), solicitudViaje.getObservacion());
                this.cuerpoCorreo.append("<br/>");
            }
            if (solicitudViaje.getIdMotivoRetraso() != 0) {
                this.cuerpoCorreo.append("<br/>");
                mostrarJustificacionRetrasoSolicitudViaje(solicitudViaje.getMotivoRetrasoVo());
            }
//            this.cuerpoCorreo.append("<br/>");
//            mostrarHistorialAprobacionesSolicitud(sgSolicitudViaje.getId());

            this.cuerpoCorreo.append(plantilla.getFin());
        } catch (SIAException siae) {
            log(siae.getMessage());
            siae.printStackTrace();
            Logger.getLogger(HtmlNotificacionServiciosGeneralesImpl.class.getName()).log(Level.SEVERE, null, siae);
        } catch (Exception e) {
            log(e.getMessage());
            e.printStackTrace();
        }
        return cuerpoCorreo;
    }

    
    public StringBuilder getHtmlSolicitarViajes(SolicitudViajeVO solicitudViaje, SolicitudViajeVO solicitudViaje2, String responsableGerencia) {
        log("HtmlNotificacionViajeImpl.getHtmlSolicitarViaje()");
        try {
            SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

            String titulo = "Solicitudes " + solicitudViaje.getCodigo() + " y " + solicitudViaje2.getCodigo();

            this.limpiarCuerpoCorreo();
            this.cuerpoCorreo.append(plantilla.getInicio());
            this.cuerpoCorreo.append(this.getTitulo(titulo));
            log("Tipo de solicitud " + solicitudViaje.getIdSgTipoEspecifico());
            if (solicitudViaje.getIdSgTipoEspecifico() == Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_TERRESTRE) {
                agregarNotaHorarioAprobacion(solicitudViaje.getFechaSalida());
            }

            this.cuerpoCorreo.append("<br/>");
            mostrarEncabezadoSolicitudViaje(solicitudViaje.getTipoSolicitud(), solicitudViaje.getTipoEspecifico(), responsableGerencia, solicitudViaje.getGerencia());
            this.cuerpoCorreo.append("<br/>");
            mostrarDetalleSolicitudViajeTerrestres(solicitudViaje, solicitudViaje2);
            this.cuerpoCorreo.append("<br/>");
            listaViajeros(sgViajeroRemote.getAllViajerosList(solicitudViaje2.getIdSolicitud()));
            this.cuerpoCorreo.append("<br/>");
            if (solicitudViaje.getIdSgTipoEspecifico() == Constantes.SOLICITUDES_TERRESTRE) {
                mostrarObservacionSolicitudViaje(solicitudViaje.getMotivo(), solicitudViaje.getObservacion());
                this.cuerpoCorreo.append("<br/>");
            }
            if (solicitudViaje.getIdMotivoRetraso() != 0) {
                this.cuerpoCorreo.append("<br/>");
                mostrarJustificacionRetrasoSolicitudViaje(solicitudViaje.getMotivoRetrasoVo());
            }
//            this.cuerpoCorreo.append("<br/>");
//            mostrarHistorialAprobacionesSolicitud(sgSolicitudViaje.getId());

            this.cuerpoCorreo.append(plantilla.getFin());
        } catch (Exception e) {
            log(e.getMessage());
            e.printStackTrace();
        }
        return cuerpoCorreo;
    }

    
    public StringBuilder getHtmlPrepareTravel(SolicitudViajeVO estatus, String titulo, String responsable, List<ViajeroVO> lv) {
        UtilLog4j.log.info("HtmlNotificacionViajeImpl.getHtmlPrepareTravel()");

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
//        SgMotivoRetraso sgMotivoRetraso = sgMotivoRetrasoRemote.findBySolicitud(estatus.getSgSolicitudViaje().getId());

        try {
            this.limpiarCuerpoCorreo();
            this.cuerpoCorreo.append(plantilla.getInicio());
            this.cuerpoCorreo.append(this.getTitulo(titulo));
            this.cuerpoCorreo.append("<br/><p>Has recibido la Solicitud de Viaje. <b> ".concat(estatus.getCodigo()).concat("</b>"));
            this.cuerpoCorreo.append(" Por favor entra al ").append("<A HREF='").append(Configurador.urlSia()).append("Sia' TARGET='_new'>SIA</A>").append(" a programar el Viaje correspondiente.");
            this.cuerpoCorreo.append("<br/><br/>");
            mostrarEncabezadoSolicitudViaje(estatus.getTipoSolicitud(), estatus.getTipoEspecifico(), responsable, estatus.getGerencia());
            this.cuerpoCorreo.append("<br/>");
            //Mostrar Detalle Terrestre
            if (estatus.getIdSgTipoSolicitudViaje() == Constantes.TIPO_ESPECIFICO_SOLICITUD_TERRESTRE) {
                mostrarDetalleSolicitudViajeTerrestre(estatus);
            } //Mostrar Detalle Aéreo con Itinerario
            else if (estatus.getIdSgTipoEspecifico() == Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA) {
                mostrarSgItinerario(estatus.getIdSolicitud(), true);
            }
            listaViajeros(lv);
            this.cuerpoCorreo.append("<br/>");
            this.cuerpoCorreo.append("<div style=\"margin-top: 20px; margin-bottom: 20px; font-style: italic; text-align: justify;background-color: #fcf8e3; border-color: #faebcc; color: #8a6d3b; border-radius: 4px; border-top-color: #f7e1b5; padding: 15px;\" class=\"col-lg-12 alert alert-warning\">");
            this.cuerpoCorreo.append("La programación del viaje queda sujeta a las condiciones de seguridad impuesta por <strong>Gestión de Riesgos</strong>, así como a la disponibilidad de vehículos y choferes en base a la logística del día de <strong>Servicios Generales y Logística</strong>.");
            this.cuerpoCorreo.append("</div>");
            this.cuerpoCorreo.append("<br/>");
            if (estatus.getIdSgTipoSolicitudViaje() == Constantes.SOLICITUDES_TERRESTRE) {
                mostrarObservacionSolicitudViaje(estatus.getMotivo(), estatus.getObservacion());
            }
            if (estatus.getIdMotivoRetraso() != 0) {
                this.cuerpoCorreo.append("<br/>");
                mostrarJustificacionRetrasoSolicitudViaje(estatus.getMotivoRetrasoVo());
            }
            this.cuerpoCorreo.append(plantilla.getFin());
            return this.cuerpoCorreo;
        } catch (SIAException ex) {
            Logger.getLogger(HtmlNotificacionViajeImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    
    public StringBuilder getHtmlApprovedTravel(SolicitudViajeVO estatus, String titulo, String nombreUsuario, String responsable, List<ViajeroVO> lv) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
//        SgMotivoRetraso sgMotivoRetraso = sgMotivoRetrasoRemote.findBySolicitud(estatus.getSgSolicitudViaje().getId());

        try {
            this.limpiarCuerpoCorreo();
            this.cuerpoCorreo.append(plantilla.getInicio());
            this.cuerpoCorreo.append(this.getTitulo(titulo));
            this.cuerpoCorreo.append("<br/><table><thead><tr><th colspan=\"2\"><b>Datos de la aprobación</b></th></tr></thead>");
            this.cuerpoCorreo.append("<tr><td>Validó:</td><td> <font color= \"red\">".concat(nombreUsuario).concat("</font></td></tr>"));
            this.cuerpoCorreo.append("<tr><td>Fecha: </td><td>".concat(Constantes.FMT_ddMMyyy.format(new Date())).concat("</td></tr>"));
            this.cuerpoCorreo.append("<tr><td>Hora: </td><td>".concat(Constantes.FMT_hmm_a.format(new Date())+"Hrs").concat("</td><tr>"));
            this.cuerpoCorreo.append("</table><br/>");
            this.cuerpoCorreo.append("<br/><p>Se ha aprobado la Justificación para la Solicitud de Viaje. <b> ".concat(estatus.getCodigo()).concat("</b><br/><br/>"));

            mostrarEncabezadoSolicitudViaje(estatus.getTipoSolicitud(), estatus.getTipoEspecifico(), responsable, estatus.getGerencia());
            this.cuerpoCorreo.append("<br/>");

            listaViajeros(lv);
            if (estatus.getIdSgTipoEspecifico() == Constantes.SOLICITUDES_TERRESTRE) {
                mostrarObservacionSolicitudViaje(estatus.getMotivo(), estatus.getObservacion());
            }
            if (estatus.getIdMotivoRetraso() != 0) {
                this.cuerpoCorreo.append("<br/>");
                mostrarJustificacionRetrasoSolicitudViaje(estatus.getMotivoRetrasoVo());
            }
            this.cuerpoCorreo.append(plantilla.getFin());
            return this.cuerpoCorreo;
        } catch (Exception ex) {
            Logger.getLogger(HtmlNotificacionViajeImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    
    public StringBuilder getHtmlSolicitudCancelada(SolicitudViajeVO estatus, String motivoCancelacion, Usuario usuarioRealizo, Date fechaModifico, Date horaModifico, String responsable) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
//        SgMotivoRetraso sgMotivoRetraso = sgMotivoRetrasoRemote.findBySolicitud(estatus.getSgSolicitudViaje().getId());

        try {
            this.limpiarCuerpoCorreo();
            this.cuerpoCorreo.append(plantilla.getInicio());
            this.cuerpoCorreo.append(this.getTitulo("Se ha cancelado la solicitud de viaje : ".concat(estatus.getCodigo() != null ? estatus.getCodigo() : "Sin Código").concat("")));
            this.cuerpoCorreo.append("<br/>");
            //motivo de cancelacion
            mostrarMotivoCancelacionSolicitudViaje(usuarioRealizo.getNombre(), motivoCancelacion, fechaModifico, horaModifico,"Canceló");
            this.cuerpoCorreo.append("<br/>");
            //encabezados
            mostrarEncabezadoSolicitudViaje(estatus.getTipoSolicitud(), estatus.getTipoEspecifico(), responsable, estatus.getGerencia());
            this.cuerpoCorreo.append("<br/>");
            log("-------Encabezados agregados");
            //detalle
            mostrarDetalleSolicitudViajeTerrestre(estatus);
            log("-------detalle agregado");
            //viajeros
            listaViajeros(sgViajeroRemote.getAllViajerosList(estatus.getIdSolicitud()));
            log("-------Viajeros agregados");
            //observaciones
            if (estatus.getIdSgTipoEspecifico() == Constantes.SOLICITUDES_TERRESTRE) {
                mostrarObservacionSolicitudViaje(estatus.getMotivo(), estatus.getObservacion());
            }

            if (estatus.getIdMotivoRetraso() != 0) {
                this.cuerpoCorreo.append("<br/>");
                mostrarJustificacionRetrasoSolicitudViaje(estatus.getMotivoRetrasoVo());
            }
            this.cuerpoCorreo.append("<br/>");
//            mostrarHistorialAprobacionesSolicitud(estatus.getIdSolicitud());

            this.cuerpoCorreo.append(plantilla.getFin());

            return this.cuerpoCorreo;
        } catch (SIAException ex) {
            Logger.getLogger(HtmlNotificacionViajeImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void mostrarMensaje(String mensaje) {
        //mostrar mensaje automatico para los estatus aprobados
        this.cuerpoCorreo.append("<br/>");
        log("Cancelar");
        this.cuerpoCorreo.append("<p style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">".concat(mensaje).concat("</p>"));
        this.cuerpoCorreo.append("<br/>");
    }

    private void mostrarEncabezadoSolicitudViaje(String tipoSolicitud, String tipoEspecifico, String responsable, String gerencia) {
        this.cuerpoCorreo.append("<table width=\"100%\" cellspacing=\"0\" border=\"0\">");
        this.cuerpoCorreo.append("<tr> ");
        this.cuerpoCorreo.append("<td width=\"25%\" align=\"left\" style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">");
        this.cuerpoCorreo.append("Tipo de Viaje: ");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("<td width=\"75%\" align=\"left\">");
        this.cuerpoCorreo.append(tipoSolicitud).append(" ").append(tipoEspecifico);
        this.cuerpoCorreo.append("</tr>");

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"25%\" align=\"left\" style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">");
        this.cuerpoCorreo.append(" Gerente responsable: ");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("<td width=\"75%\" align=\"left\">");
        this.cuerpoCorreo.append(responsable);
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"25%\" align=\"left\" style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">");
        this.cuerpoCorreo.append(" Gerencia responsable: ");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("<td width=\"75%\" align=\"left\">");
        this.cuerpoCorreo.append(gerencia);
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("<br/>");
    }

    private void mostrarDetalleSolicitudViajeTerrestre(SolicitudViajeVO solicitud) throws SIAException {
        String destino = "";
        String origen = solicitud.getOrigen();
        ViajeLugarVO viajeLugarVO;
        ItinerarioCompletoVo itinerarioCompleto;
        //ViajeDestinoVo viajeDestino = sgViajeCiudadRemote.findDestinoSolicitudViaje(solicitud.getIdSolicitud());  se cambia por el detalle de la ruta
        if (solicitud.getIdSgTipoSolicitudViaje() == Constantes.SOLICITUDES_TERRESTRE) {
            if (solicitud.getIdOficinaDestino() != 0) {
                destino = solicitud.getDestino();
            } else {
                destino = sgDetalleRutaCiudadRemote.buscarDetalleRutaCiudadDestinoPorRuta(solicitud.getIdRutaTerrestre()).getCiudad();
            }
        } else if (solicitud.getIdSgTipoEspecifico() == Constantes.SOLICITUDES_AEREA) {
            itinerarioCompleto = this.sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(solicitud.getIdSolicitud(), true, false, "id");
            origen = itinerarioCompleto.getNombreCiudadOrigen();
            destino = itinerarioCompleto.getNombreCiudadDestino();
        }

        this.cuerpoCorreo.append("<center> ");
        this.cuerpoCorreo.append("<table width=\"85%\" cellspacing=\"0\" border=\"0\"> ");
        this.cuerpoCorreo.append("<tr style=\"background-color:#CEECF5;\">");
        this.cuerpoCorreo.append("<td width=\"40%\" align=\"center\" style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Origen</td>");
        this.cuerpoCorreo.append("<td width=\"40%\" align=\"center\" style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Destino</td>");
        this.cuerpoCorreo.append("</tr>  ");
        this.cuerpoCorreo.append("<tr>"); //buscar itinerario  vuelo

        //es una solicitud de tipo terrestre a ciudad
        this.cuerpoCorreo.append("<td align=\"center\">".concat(origen).concat(" </td>"));
        this.cuerpoCorreo.append("<td align=\"center\">".concat(destino).concat("</td>"));

        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("<table width=\"85%\" cellspacing=\"0\" border=\"0\"> ");
        this.cuerpoCorreo.append("<tr style=\"background-color:#CEECF5;\">");
        this.cuerpoCorreo.append("<td width=\"40%\" align=\"center\" style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Salida</td>");
        this.cuerpoCorreo.append("<td width=\"40%\" align=\"center\" style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Hora</td>");
        this.cuerpoCorreo.append("</tr> ");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td align=\"center\">".concat(Constantes.FMT_TextDate.format(solicitud.getFechaSalida())).concat("</td>"));
        this.cuerpoCorreo.append("<td align=\"center\">".concat(solicitud.getHoraSalida() != null ? Constantes.FMT_hmm_a.format(solicitud.getHoraSalida())+"Hrs" : "-").concat("</td>"));
        this.cuerpoCorreo.append("</tr> ");
        this.cuerpoCorreo.append("</table>");

        if (solicitud.isRedondo()) {
            this.cuerpoCorreo.append("<table width=\"85%\" cellspacing=\"0\" border=\"0\">");
            this.cuerpoCorreo.append("<tr style=\"background-color:#CEECF5;\">");
            this.cuerpoCorreo.append("<td width=\"40%\" align=\"center\" style=\" font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Regreso</td>");
            this.cuerpoCorreo.append("<td width=\"40%\" align=\"center\" style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Hora</td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td align=\"center\">".concat(Constantes.FMT_TextDate.format(solicitud.getFechaRegreso())).concat("</td>"));
            this.cuerpoCorreo.append("<td align=\"center\">".concat(solicitud.getFechaRegreso() != null ? Constantes.FMT_hmm_a.format(solicitud.getHoraRegreso())+"Hrs" : "-").concat("</td>"));
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("</table>");
        }

        this.cuerpoCorreo.append("</center> ");

        log("Detalle Solicitud de Viaje agregado");
    }

    private void mostrarDetalleSolicitudViajeTerrestres(SolicitudViajeVO solicitud, SolicitudViajeVO solicitud2) throws SIAException {
        String destino = "";
        String interseccion = "";
        int porcentaje = 21;
        int porcentaje2 = 22;
        String columnaR = "";
        String fechaRSolicitud = "";
        String fechaRSolicitud2 = "";
        String origen = solicitud.getOrigen();
        if (solicitud2.getIdOficinaDestino() != 0 && solicitud2.getIdOficinaDestino() != 0) {
            interseccion = solicitud.getDestino();
            destino = solicitud2.getDestino();

        }
        String fs = Constantes.FMT_ddMMyyy.format(solicitud.getFechaSalida()) + " "
                + (solicitud.getHoraSalida() != null ? Constantes.FMT_hmm_a.format(solicitud.getHoraSalida())+"Hrs" : "-");
        String fs2 = Constantes.FMT_ddMMyyy.format(solicitud2.getFechaSalida()) + " "
                + (solicitud2.getHoraSalida() != null ? Constantes.FMT_hmm_a.format(solicitud2.getHoraSalida()) +"Hrs": "-");

        if (solicitud.isRedondo()) {
            //porcentaje= 20;
            columnaR = "<th width=\"22%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Regreso</th>";
            fechaRSolicitud = "<td " + " width=\"21%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">"
                    + Constantes.FMT_ddMMyyy.format(solicitud.getFechaRegreso()) + " "
                    + (solicitud.getHoraRegreso() != null ? Constantes.FMT_hmm_a.format(solicitud.getHoraRegreso())+"Hrs" : "-") + "</td>";
            fechaRSolicitud2 = "<td " + " width=\"22%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">"
                    + Constantes.FMT_ddMMyyy.format(solicitud2.getFechaRegreso()) + " "
                    + (solicitud2.getHoraRegreso() != null ? Constantes.FMT_hmm_a.format(solicitud2.getHoraRegreso())+"Hrs" : "-") + "</td>";
        } else {
            porcentaje = 28;
            porcentaje2 = 30;
        }

        //buscar itinerario  vuelo
        this.cuerpoCorreo.append("<br/><table width=\"100%\" align=\"center\" cellspacing=\"0\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Solicitudes de Viaje </th></tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<th width=\"14%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Número</th>");
        this.cuerpoCorreo.append("<th width=\"" + porcentaje + "%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Origen</th>");
        this.cuerpoCorreo.append("<th width=\"" + porcentaje + "%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Destino</th>");
        this.cuerpoCorreo.append("<th width=\"" + porcentaje2 + "%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Salida</th>");
        this.cuerpoCorreo.append(columnaR);
        this.cuerpoCorreo.append("</tr>");

        //es una solicitud de tipo terrestre a ciudad
        this.cuerpoCorreo.append("<tr>");
        //   UtilLog4j.log.info(this, "IdInvitado: " + viajero.getIdInvitado());
        this.cuerpoCorreo.append("<td ").append(" width=\"14%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">");
        cuerpoCorreo.append(solicitud.getCodigo()).append("</td>");
        this.cuerpoCorreo.append("<td ").append(" width=\"" + porcentaje + "%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">");
        this.cuerpoCorreo.append(solicitud.getOrigen()).append("</td>");
        this.cuerpoCorreo.append("<td ").append(" width=\"" + porcentaje + "%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">");
        cuerpoCorreo.append(solicitud.getDestino()).append("</td>");
        this.cuerpoCorreo.append("<td ").append(" width=\"" + porcentaje2 + "%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">");
        cuerpoCorreo.append(fs).append("</td>");
        cuerpoCorreo.append(fechaRSolicitud);
        this.cuerpoCorreo.append("</tr>");

        this.cuerpoCorreo.append("<tr>");
        //   UtilLog4j.log.info(this, "IdInvitado: " + viajero.getIdInvitado());
        this.cuerpoCorreo.append("<td ").append(" width=\"14%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">");
        cuerpoCorreo.append(solicitud2.getCodigo()).append("</td>");
        this.cuerpoCorreo.append("<td ").append(" width=\"" + porcentaje + "%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">");
        this.cuerpoCorreo.append(solicitud2.getOrigen()).append("</td>");
        this.cuerpoCorreo.append("<td ").append(" width=\"" + porcentaje + "%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">");
        cuerpoCorreo.append(solicitud2.getDestino()).append("</td>");
        this.cuerpoCorreo.append("<td ").append(" width=\"" + porcentaje2 + "%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">");
        cuerpoCorreo.append(fs2).append("</td>");
        cuerpoCorreo.append(fechaRSolicitud2);
        this.cuerpoCorreo.append("</tr>");

        this.cuerpoCorreo.append("</table><br/>");

        log("Detalle Solicitud de Viaje agregado");
    }

    private void listaViajerosHtml(Object object) throws SIAException {
        String bgGris = "\"bgcolor=#FAFAFA\"";
        String bgBlanco = "\"bgcolor=#ffffff\"";
        String f = "";
        //List<SgViajero> listaV = new ArrayList<SgViajero>();
        List<ViajeroVO> listaV = null;
        List<ViajeroVO> lv;

        if (object instanceof SgSolicitudViaje) {
            SgSolicitudViaje solicitud = (SgSolicitudViaje) object;
            //listaV = this.sgViajeroRemote.getViajerosBySolicitudViajeList(solicitud, false);
            listaV = this.sgViajeroRemote.getAllViajerosList(solicitud.getId());
        }
//        else if (object instanceof SgViaje) {
//            SgViaje sgViaje = (SgViaje) object;
//            log("Buscar viajeros por viaje");
//            //listaV = this.sgViajeroRemote.getListaViajeroPorViaje(sgViaje.getId());
//            this.sgViajeroRemote.getTravellersByTravel(idViaje);
//        }

        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<center> ");

        if (listaV != null) {
            this.cuerpoCorreo.append("<table width=\"100%\" cellspacing=\"0\">");
            this.cuerpoCorreo.append("<th colspan=\"4\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Viajero(s)</th>");
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td width=\"35%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Nombre</td>");
            this.cuerpoCorreo.append("<td width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Tipo</td>");
            this.cuerpoCorreo.append("<td width=\"10%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Estancia</td>");
            this.cuerpoCorreo.append("<td width=\"35%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Observación</td>");
            this.cuerpoCorreo.append("</tr>");
            //primera fila
            int i = Constantes.UNO;

            for (ViajeroVO viajeroVo : listaV) {
                if (i % Constantes.DOS == 0) {
                    f = bgBlanco;
                } else {
                    f = bgGris;
                }
                this.cuerpoCorreo.append("<tr>");
                this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"35%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").concat(viajeroVo.isEsEmpleado() ? viajeroVo.getUsuario() : viajeroVo.getInvitado()).concat("</td>"));
//                log("nombre OK");
                //viajero.getUsuario() != null ? "Empleado" : "Invitado"
                this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").concat(viajeroVo.isEmpleado() ? Constantes.CONCEPTO_EMPLEADO : Constantes.CONCEPTO_INVITADO).concat("</td>"));
//                log("tipo OK");
                //viajero.isEstancia() ? "SI" : "NO"
                this.cuerpoCorreo.append("<td ".concat(f));
                this.cuerpoCorreo.append(" width=\"10%\" align=\"center\" style=\"border: 1px solid #b5b5b5;font-size:10px;\">");
                this.cuerpoCorreo.append(viajeroVo.isEstancia() ? viajeroVo.getCodigoEstancia() : "No");
                this.cuerpoCorreo.append("</td>");
//                log("estancia OK");
                this.cuerpoCorreo.append("<td ").append(f).append(" width=\"35%\" align=\"center\" style=\"border: 1px solid #b5b5b5;font-size:10px;\">").append(validarNullHtml(viajeroVo.getObservacion())).append("</td>");
                this.cuerpoCorreo.append("</tr>");
                i++;
            }
            this.cuerpoCorreo.append("</table>");
        } else {
            SgSolicitudViaje solicitud = (SgSolicitudViaje) object;
            this.cuerpoCorreo.append("<p>Viajero :".concat(solicitud.getGenero().getNombre()).concat("</p>"));
        }
        this.cuerpoCorreo.append("</center> ");

    }

    private void mostrarObservacionSolicitudViaje(String motivo, String observacion) {

        this.cuerpoCorreo.append("<center> ");
        this.cuerpoCorreo.append("<table width=\"85%\" cellspacing=\"0\" border=\"0\"> ");
        this.cuerpoCorreo.append("<tr style=\"background-color:#CEECF5;\">");
        this.cuerpoCorreo.append("<td width=\"85%\" align=\"center\" style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Motivo de viaje</td>");
        this.cuerpoCorreo.append("</tr>  ");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td align=\"center\">".concat(motivo).concat(" </td>"));
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("<tr><td></td></tr>");
        if (!observacion.trim().isEmpty()) {
            this.cuerpoCorreo.append("<tr style=\"background-color:#CEECF5;\">");
            this.cuerpoCorreo.append("<td width=\"85%\" align=\"center\" style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Observaciónes agregadas</td>");
            this.cuerpoCorreo.append("</tr>  ");
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td align=\"center\">");
            this.cuerpoCorreo.append(observacion);
            this.cuerpoCorreo.append("</td>");
            this.cuerpoCorreo.append("</tr>");
        }
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("</center>");
//        log("Motivo" + (!sgSolicitudViaje.getObservacion().trim().isEmpty() ? "y Observación" : "") + " Solicitud de Viaje agregados");
    }

    /**
     * Modifico: 15/10/2013 Nestor Lopez * Modifico: 08/11/2013 MLUIS
     *
     * @param listaViajero
     */
    public void listaViajeros(List<ViajeroVO> listaViajero) {
        this.cuerpoCorreo.append("<br/><table width=\"100%\" align=\"center\" cellspacing=\"0\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Viajero(s)</th></tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<th width=\"35%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Nombre</th>");
        this.cuerpoCorreo.append("<th width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Teléfono</th>");
        this.cuerpoCorreo.append("<th width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Tipo de Viajero</th>");
        this.cuerpoCorreo.append("<th width=\"10%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Estancia</th>");
        this.cuerpoCorreo.append("<th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Observación</th>");
        this.cuerpoCorreo.append("</tr>");
        for (ViajeroVO viajero : listaViajero) {
            //
            this.cuerpoCorreo.append("<tr>");
            //   UtilLog4j.log.info(this, "IdInvitado: " + viajero.getIdInvitado());
            this.cuerpoCorreo.append("<td ").append(" width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5;").append(viajero.getIdSolicitudViaje() == 0 ? " background-color:#E47070;\"> " : "\">");
            cuerpoCorreo.append(viajero.getTipoViajero() == Constantes.SG_TIPO_ESPECIFICO_EMPLEADO ? viajero.getUsuario() : viajero.getInvitado()).append("</td>");
            this.cuerpoCorreo.append("<td ").append(" width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5;").append(viajero.getIdSolicitudViaje() == 0 ? " background-color:#E47070;\">" : "\">");
            this.cuerpoCorreo.append(validarNullHtml(viajero.getTelefono())).append("</td>");
            this.cuerpoCorreo.append("<td ").append(" width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;").append(viajero.getIdSolicitudViaje() == 0 ? " background-color:#E47070;\">" : "\">");
            cuerpoCorreo.append(viajero.getTipoViajero() == Constantes.SG_TIPO_ESPECIFICO_EMPLEADO ? "Empleado" : "Invitado").append("</td>");
            this.cuerpoCorreo.append("<td ").append(" width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;").append(viajero.getIdSolicitudViaje() == 0 ? " background-color:#E47070;\">" : "\">");
            cuerpoCorreo.append(viajero.isEstancia() ? "Si" : "No").append("</td>");
            this.cuerpoCorreo.append("<td ").append(" width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5;").append(viajero.getIdSolicitudViaje() == 0 ? " background-color:#E47070;\">" : "\">");
            cuerpoCorreo.append(viajero.getIdSolicitudViaje() == 0 ? "Sin Solicitud" : validarNullHtml(viajero.getObservacion())).append("</td>");
            this.cuerpoCorreo.append("</tr>");
        }
        this.cuerpoCorreo.append("</table><br/>");
        //    return this.cuerpoCorreo;
    }

    /**
     * Modifico: 15/10/2013 Nestor Lopez * Modifico: 08/11/2013 MLUIS
     *
     * @param listaViajero
     */
    public void listaViajerosConCodigoViaje(List<ViajeroVO> listaViajero) {
        this.cuerpoCorreo.append("<br/><table width=\"100%\" align=\"center\" cellspacing=\"0\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"6\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Viajero(s)</th></tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<th width=\"10%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Viaje</th>");
        this.cuerpoCorreo.append("<th width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Nombre</th>");
        this.cuerpoCorreo.append("<th width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Teléfono</th>");
        this.cuerpoCorreo.append("<th width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Tipo de Viajero</th>");
        this.cuerpoCorreo.append("<th width=\"10%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Estancia</th>");
        this.cuerpoCorreo.append("<th width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Observación</th>");
        this.cuerpoCorreo.append("</tr>");
        for (ViajeroVO viajero : listaViajero) {
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td ".concat(" width=\"10%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(viajero.getCodigoViaje()).concat("</td>")));
            this.cuerpoCorreo.append("<td ".concat(" width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(viajero.getIdInvitado() == 0 ? viajero.getUsuario() : viajero.getInvitado()).concat("</td>")));
            this.cuerpoCorreo.append("<td ".concat(" width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(viajero.getTelefono() != null ? viajero.getTelefono() : "--").concat("</td>")));
            this.cuerpoCorreo.append("<td ".concat(" width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(viajero.getIdInvitado() == 0 ? "Empleado" : "Invitado").concat("</td>")));
            this.cuerpoCorreo.append("<td ".concat(" width=\"10%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(viajero.isEstancia() ? "Si" : "No").concat("</td>")));
            this.cuerpoCorreo.append("<td ".concat(" width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(validarNullHtml(viajero.getObservacion())).concat("</td>")));
            this.cuerpoCorreo.append("</tr>");
        }
        this.cuerpoCorreo.append("</table><br/>");
        //    return this.cuerpoCorreo;
    }

    public void mostrarSgItinerario(int idSgSolicitudViaje, boolean isViajeIda) {
        log("HtmlNotificacionViajeImpl.mostrarSgItinerario()");

        log("idSolicitudEstancia: " + idSgSolicitudViaje);

        try {
            int i = 1;
            SgSolicitudViaje sgSolicitudViaje = this.sgSolicitudViajeRemote.find(idSgSolicitudViaje);
//            SgItinerario sgItinerario = this.sgItinerarioRemote.findBySolicitudViaje(sgSolicitudViaje, isViajeIda, false);
            //List<SgDetalleItinerario> list = this.sgDetalleItinerarioRemote.findBySgItinerario(sgItinerario, "id", true, false);
            ItinerarioCompletoVo vo = this.sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(sgSolicitudViaje.getId(), isViajeIda, true, "id");

            String bgGris = "bgcolor=#FAFAFA";
            String bgBlanco = "bgcolor=#ffffff";
            String f = "";
            this.cuerpoCorreo.append("<center> ");

            if (vo.getEscalas() != null) {
                this.cuerpoCorreo.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\">");
                this.cuerpoCorreo.append("<th colspan=\"8\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Itinerario de Vuelo ").append(isViajeIda ? "(ida)" : "(regreso)").append("</th>");
                this.cuerpoCorreo.append("<tr>");
                this.cuerpoCorreo.append("<td width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Origen</td>");
                this.cuerpoCorreo.append("<td width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Destino</td>");
                this.cuerpoCorreo.append("<td width=\"14%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Fecha Salida</td>");
                this.cuerpoCorreo.append("<td width=\"12%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Hora Salida</td>");
                this.cuerpoCorreo.append("<td width=\"14%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Fecha Llegada</td>");
                this.cuerpoCorreo.append("<td width=\"12%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Hora Llegada</td>");
                this.cuerpoCorreo.append("<td width=\"6%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\"># Vuelo</td>");
                this.cuerpoCorreo.append("<td width=\"12%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Aerolínea</td>");
                this.cuerpoCorreo.append("</tr>");

                this.cuerpoCorreo.append("<tr>");
                for (DetalleItinerarioCompletoVo sgDetalleItinerario : vo.getEscalas()) {
                    if (i % 2 == 0) {
                        f = bgBlanco;
                    } else {
                        f = bgGris;
                    }
                    this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(sgDetalleItinerario.getNombreCiudadOrigen()).concat("</td>")));
                    this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(sgDetalleItinerario.getNombreCiudadDestino()).concat("</td>")));
                    this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"14%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(Constantes.FMT_TextDate.format(sgDetalleItinerario.getFechaSalida())).concat("</td>")));
                    this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"12%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(Constantes.FMT_hmm_a.format(sgDetalleItinerario.getHoraSalida())+"Hrs").concat("</td>")));
                    this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"14%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(Constantes.FMT_TextDate.format(sgDetalleItinerario.getFechaLlegada())).concat("</td>")));
                    this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"12%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(Constantes.FMT_hmm_a.format(sgDetalleItinerario.getHoraLlegada())+"Hrs").concat("</td>")));
                    this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"6%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(sgDetalleItinerario.getNumeroVuelo() == null || sgDetalleItinerario.getNumeroVuelo().isEmpty() ? "-" : sgDetalleItinerario.getNumeroVuelo()).concat("</td>")));
                    this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"12%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(sgDetalleItinerario.getNombreAerolinea()).concat("</td>")));
                    this.cuerpoCorreo.append("</tr>");

                    i++;
                }
                this.cuerpoCorreo.append("</tr>");
                this.cuerpoCorreo.append("</table>");
            }
            this.cuerpoCorreo.append("</center> ");
        } catch (Exception e) {
            log("Error al intentar mostrar el Itinerario en el mail");
            log(e.getMessage());
            e.printStackTrace();
        }
    }

    private void agregarTitulo(String tituloSub) {
        this.cuerpoCorreo.append("<center>");
        this.cuerpoCorreo.append("<table style=\"margin:0px auto 0px auto;width:85%;border:1;background-color:#cccccc;font-weight:bold;\">");

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td style=\"text-align:center;\">");
        this.cuerpoCorreo.append(tituloSub);
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("</center>");
    }

    private void mostrarJustificacionGerencia(String nombre, String motivo, Date fecha, Date hora) {
        this.cuerpoCorreo.append("<center> ");
        this.cuerpoCorreo.append("<p style=\"font:Arial, Helvetica, sans-serif; font-size:12px;\">Justificación de: <span style=\"font:Arial, Helvetica, sans-serif; font-size:13px; text-decoration:underline;font-weight: bold;\">");
        cuerpoCorreo.append(nombre);
        cuerpoCorreo.append("</span></p>");
        this.cuerpoCorreo.append("</center> ");

        //La fecha de Salida de esta Solicitud no cumple los requisitos de tiempo
        this.cuerpoCorreo.append("<table width=\"85%\" cellspacing=\"2\" border=\"0\">");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Justificación:</td>");
        this.cuerpoCorreo.append("<td style=\"font:Arial, Helvetica, sans-serif; font-size:13px; color: red; font-weight: bold;\">");
        cuerpoCorreo.append(motivo);
        this.cuerpoCorreo.append("</td></tr>  ");
        cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Fecha:</td>");
        this.cuerpoCorreo.append("<td style=\"font:Arial, Helvetica, sans-serif; font-size:12px; \">");
        cuerpoCorreo.append(Constantes.FMT_ddMMyyy.format(fecha));
        this.cuerpoCorreo.append("</td></tr>  ");
        cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Hora:</td>");
        this.cuerpoCorreo.append("<td style=\"font:Arial, Helvetica, sans-serif; font-size:12px; \">");
        cuerpoCorreo.append(Constantes.FMT_HHmmss.format(hora));
        this.cuerpoCorreo.append("</td></tr>  ");
        this.cuerpoCorreo.append("</table>  ");
        cuerpoCorreo.append("<br/>");
    }

    private void mostrarJustificacionRetrasoSolicitudViaje(MotivoRetrasoVO motivo) {
        log("mostrarJustificacionRetrasoSolicitudViaje");

        if (motivo != null) {
            this.cuerpoCorreo.append("<center> ");
            this.cuerpoCorreo.append("<p style=\"font:Arial, Helvetica, sans-serif; font-size:10px;\"><i>La fecha de <b>salida o regreso</b> de esta solicitud no cumple los requisitos de tiempo, a continuación se muestra la justificación</i></p>");
            this.cuerpoCorreo.append("</center> ");
            this.agregarTitulo("Motivo Retraso");

            //La fecha de Salida de esta Solicitud no cumple los requisitos de tiempo
            this.cuerpoCorreo.append("<center> ");
            this.cuerpoCorreo.append("<table width=\"85%\" cellspacing=\"0\" border=\"0\" align=\"center\">");

            this.cuerpoCorreo.append("<tr style=\"background-color:#CEECF5;\">");
            this.cuerpoCorreo.append("<td colspan=\"2\" width=\"85%\" align=\"center\" style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Personal que visita</td>");
            this.cuerpoCorreo.append("</tr>  ");

            if (motivo.getIdInvitado() != 0 || (motivo.getIdUsuario() != null && !motivo.getIdUsuario().isEmpty())) {
                String visito = "";
                if (motivo.getIdInvitado() != 0) {
                    visito = motivo.getInvitado();
                } else {
                    visito = motivo.getUsuario();
                }
                this.cuerpoCorreo.append("<tr>");
                this.cuerpoCorreo.append("<td style=\"width: 18%;\">");
                this.cuerpoCorreo.append("Nombre :");
                this.cuerpoCorreo.append("</td>");
                this.cuerpoCorreo.append("<td style=\"width: 80%;\">");
                this.cuerpoCorreo.append(visito);
                this.cuerpoCorreo.append("</td>");
                this.cuerpoCorreo.append("</tr>");

                this.cuerpoCorreo.append("<tr>");
                this.cuerpoCorreo.append("<td style=\"width: 18%;\">");
                this.cuerpoCorreo.append("Empresa :");
                this.cuerpoCorreo.append("</td>");
                this.cuerpoCorreo.append("<td style=\"width: 80%;\">");
                this.cuerpoCorreo.append(motivo.getNombreEmpresa());
                this.cuerpoCorreo.append("</td>");
                this.cuerpoCorreo.append("</tr>");

                this.cuerpoCorreo.append("<tr>");
                this.cuerpoCorreo.append("<td style=\"width: 18%;\">");
                this.cuerpoCorreo.append("Email :");
                this.cuerpoCorreo.append("</td>");
                this.cuerpoCorreo.append("<td style=\"width: 80%;\">");
                this.cuerpoCorreo.append(motivo.getMail());
                this.cuerpoCorreo.append("</td>");
                this.cuerpoCorreo.append("</tr>");

            }

            if (motivo.getIdLugar() != 0) {

                this.cuerpoCorreo.append("<tr style=\"background-color:#CEECF5;\">");
                this.cuerpoCorreo.append("<td colspan=\"2\" width=\"85%\" align=\"center\" style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Lugar de reunión</td>");
                this.cuerpoCorreo.append("</tr>  ");

                this.cuerpoCorreo.append("<tr>");
                this.cuerpoCorreo.append("<td style=\"width: 18%;\">");
                this.cuerpoCorreo.append("Lugar :");
                this.cuerpoCorreo.append("</td>");
                this.cuerpoCorreo.append("<td style=\"width: 80%;\">");
                this.cuerpoCorreo.append(motivo.getLugar());
                this.cuerpoCorreo.append("</td>");
                this.cuerpoCorreo.append("</tr>");

                this.cuerpoCorreo.append("<tr>");
                this.cuerpoCorreo.append("<td style=\"width: 18%;\">");
                this.cuerpoCorreo.append("Hora :");
                this.cuerpoCorreo.append("</td>");
                this.cuerpoCorreo.append("<td style=\"width: 80%;\">");
                this.cuerpoCorreo.append(Constantes.FMT_hmm_a.format(motivo.getHoraReunion())+"Hrs");
                this.cuerpoCorreo.append("</td>");
                this.cuerpoCorreo.append("</tr>");
            }

            this.cuerpoCorreo.append("<tr style=\"background-color:#CEECF5;\">");
            this.cuerpoCorreo.append("<td colspan=\"2\" width=\"85%\" align=\"center\" style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Justificación</td>");
            this.cuerpoCorreo.append("</tr>  ");
//
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td colspan=\"2\" style=\"text-align: left;\">");
            this.cuerpoCorreo.append(motivo.getJustificacion());
            this.cuerpoCorreo.append("</td>");
            this.cuerpoCorreo.append("</tr>");

            this.cuerpoCorreo.append("</table>");
            this.cuerpoCorreo.append("</center> ");
            this.cuerpoCorreo.append("</br>");
        }

        log("Motivo de Retraso Solicitud de Viaje agregado");
    }

    private void mostrarMotivoCancelacionSolicitudViaje(String nombreUsuario, String motivoCancelacion, Date fecha, Date hora, String tipo) {
        log("mostrarMotivoCancelacionSolicitudViaje");

        this.cuerpoCorreo.append("<table cellspacing=\"0\" border=\"0\" align=\"left\">");

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">").append(tipo).append(" :</td>");
        this.cuerpoCorreo.append("<td style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\"> ".concat(nombreUsuario));
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td  style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Fecha :</td>");
        this.cuerpoCorreo.append("<td  style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">".concat(Constantes.FMT_TextDate.format(fecha)));
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td  style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Hora : </td>");
        this.cuerpoCorreo.append("<td  style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">".concat(Constantes.FMT_hmm_a.format(hora)+"Hrs"));
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        //
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td  style=\"font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Motivo: </td>");
        this.cuerpoCorreo.append("<td  style=\"font:Arial, Helvetica, sans-serif; font-size:15px; color : red; font-weight: bold;\">".concat(motivoCancelacion));
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");

        this.cuerpoCorreo.append("</table>");
//        this.cuerpoCorreo.append("<hr style=\"color: #B40431;background-color:#B40431;height: 2px;\" />");
        this.cuerpoCorreo.append("<br/>");
    }

    private void mostrarHistorialAprobacionesSolicitud(int idSolicitud) {
        String estilo, lineaGris;
        int cont = 1;
        lineaGris = "style=\"border: 0px solid  #CEECF5;\"";
        SolicitudViajeVO solicitud = this.sgSolicitudViajeRemote.buscarPorId(idSolicitud, Constantes.NO_ELIMINADO, Constantes.CERO);
        List<EstatusAprobacionVO> historial = this.sgEstatusAprobacionRemote.traerHistorialEstatusAprobacionPorSolicitudViaje(idSolicitud, false);

        this.agregarTitulo("Historial de aprobaciones");

        this.cuerpoCorreo.append("<center>");
        this.cuerpoCorreo.append("<table width=\"85%\" style=\"border: 1px solid #b5b5b5;\">");

        this.cuerpoCorreo.append("<tr align=\"center\" style=\"border: 1px solid #CEECF5; font:Arial, Helvetica, sans-serif;background-color:#CEECF5;font-size:11px; font-weight: bold;\">");
        if (solicitud.getIdSgTipoSolicitudViaje() != Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_AEREA) {
            this.cuerpoCorreo.append("<td >");
            this.cuerpoCorreo.append("");
            this.cuerpoCorreo.append("</td>");
        }

        this.cuerpoCorreo.append("<td >");
        this.cuerpoCorreo.append("Operación");
        this.cuerpoCorreo.append("</td>");

        this.cuerpoCorreo.append("<td >");
        this.cuerpoCorreo.append("Usuario");
        this.cuerpoCorreo.append("</td>");

        this.cuerpoCorreo.append("<td >");
        this.cuerpoCorreo.append("Fecha ");
        this.cuerpoCorreo.append("</td>");

        this.cuerpoCorreo.append("<td >");
        this.cuerpoCorreo.append("Hora");
        this.cuerpoCorreo.append("</td>");

        this.cuerpoCorreo.append("</tr>");

        //linea para agregar el primer estatus de solicitada
//        agregarLineaHistoriaSolicitud(solicitud.getColorSgSemaforo(), Constantes.ESTATUS_PENDIENTE_NOMBRE,
//                solicitud.getNombreGenero(),
//                solicitud.getFechaGenero(),
//                solicitud.getHoraGenero(),
//                "",
//                solicitud.getIdSgTipoEspecifico());
        if (historial != null && !historial.isEmpty()) {
            for (EstatusAprobacionVO vo : historial) {
                estilo = cont % 2 == 0 ? "style=\"border: 1px solid lightgray;font-size:11px;\"" : "style=\"border: 1px solid  white;font-size:11px;\"";
                agregarLineaHistoriaSolicitud(vo.getColorSemaforo(), vo.getNombreEstatus(), vo.getNombreUsuarioAprobo(), vo.getFechaGenero(), vo.getHoraGenero(), estilo, solicitud.getIdSgTipoEspecifico());
                cont++;
            }
        }
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("</center>");

    }

    private void agregarLineaHistoriaSolicitud(String semaforo, String estatus, String nombreUsuario, Date fecha, Date hora, String estilo, int idTipoEspecificoSolicitud) {

        //this.cuerpoCorreo.append("<tr").append(estilo).append(">");
        this.cuerpoCorreo.append("<tr>");

        if (idTipoEspecificoSolicitud != Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_AEREA) {
            this.cuerpoCorreo.append("<td >");
            this.cuerpoCorreo.append("<img src=\"").append(Configurador.urlSia()).append("resources/img/").append(semaforo).append(".png\"/>");
            this.cuerpoCorreo.append("</td>");
        }

        this.cuerpoCorreo.append("<td >");
        this.cuerpoCorreo.append(estatus);
        this.cuerpoCorreo.append("</td>");

        this.cuerpoCorreo.append("<td >");
        this.cuerpoCorreo.append(nombreUsuario);
        this.cuerpoCorreo.append("</td>");

        this.cuerpoCorreo.append("<td >");
        this.cuerpoCorreo.append(Constantes.FMT_ddMMyyy.format(fecha));
        this.cuerpoCorreo.append("</td>");

        this.cuerpoCorreo.append("<td >");
        this.cuerpoCorreo.append(Constantes.FMT_hmm_a.format(hora)+"Hrs");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");

    }

    
    public StringBuilder bodyMailsendAirTravel(int idSolicitud, String tipoSolicitud, String tipoEspecifico, String responsable, String gerencia, List<ViajeroVO> listTemp,
            Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            int idViajeIda, String codigo) {
        log("bodyMailsendAirTravel");

        log("-----------------------------------");
        log("-----------------------------------");

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        try {
            this.limpiarCuerpoCorreo();
            this.cuerpoCorreo.append(plantilla.getInicio());
            this.cuerpoCorreo.append(this.getTitulo("Viaje aéreo - ".concat(codigo).concat(idViajeIda == 0 ? " (Ida)" : " (Regreso)")));
            this.cuerpoCorreo.append("<br/>");
            mostrarEncabezadoSolicitudViaje(tipoSolicitud, tipoEspecifico, responsable, gerencia);
            this.cuerpoCorreo.append("<br/>");
            //Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida,Date fechaRegreso, Date horaRegreso, boolean redondo
            datosViajeVO(fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo);
            mostrarSgItinerario(idSolicitud, idViajeIda == 0);
            listaViajeros(listTemp);
            this.cuerpoCorreo.append(plantilla.getFin());

            return this.cuerpoCorreo;
        } catch (Exception e) {
            log(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void datosViajeViajeros(List<ViajeVO> lv, boolean conViajeros) {
        this.cuerpoCorreo.append("<br/><table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\" >");
        this.cuerpoCorreo.append("<thead><tr><th colspan=\"7\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman\', Times, serif; font-weight:bold; color:#FFFFFF; font-size:16px; background-color:#0099FF;\">Viajes programados</th></tr>");
        this.cuerpoCorreo.append("<tr><th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Código </th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Ruta </th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">F. programada </th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">H. programada </th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Viaja en </th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\"> Responsable </th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Viaje</th>");
        this.cuerpoCorreo.append("</tr>");
        for (ViajeVO viajeVO : lv) {
            this.cuerpoCorreo.append("<tr> <td  style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(viajeVO.getCodigo()).append("</td>");
            this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(viajeVO.getRuta() != null ? viajeVO.getRuta() : '-').append("</td>");
            this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(viajeVO.getFechaSalida() != null ? Constantes.FMT_TextDate.format(viajeVO.getFechaSalida()) : Constantes.FMT_TextDate.format(viajeVO.getFechaProgramada())).append("</td>");
            this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(viajeVO.getHoraSalida() != null ? Constantes.FMT_HHmmss.format(viajeVO.getHoraSalida()) : Constantes.FMT_HHmmss.format(viajeVO.getHoraProgramada())).append("</td>");
            if (viajeVO.isAutobus()) {
                cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append("Autobus").append("</td>");
            } else if (viajeVO.isVehiculoEmpresa()) {
                cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append("Vehículo de la empresa").append("</td>");
            } else if (viajeVO.isVehiculoPropio()) {
                cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append("Vehículo propio").append("</td>");
            } else {
                cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append("Viaje aéreo").append("</td>");
            }
            cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(viajeVO.getResponsable()).append("</td>");
            cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(viajeVO.isRedondo() ? "Redondo " : "Sencillo").append("</td>");
            cuerpoCorreo.append("</tr>");
        }
        cuerpoCorreo.append("</table><br/>");
        if (conViajeros) {
            List<ViajeroVO> lvjro = new ArrayList<ViajeroVO>();
            cuerpoCorreo.append("<p>A continuación se muestra la lista de viajeros incluidos en los viajes programados. </p>");
            for (ViajeVO viajeVO : lv) {
                lvjro.addAll(viajeVO.getListaViajeros());
            }

            listaViajerosConCodigoViaje(lvjro);
        }
    }

    /*
     * Los datos del viaje contiene todos los datos del viaje La fecha La hora
     * de regreso
     *
     */
    private void datosViaje(SgViaje sgViaje) {
        log("datosViaje");
        try {
            cuerpoCorreo.append("<br/><table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\" >");
            cuerpoCorreo.append("<thead><tr><th colspan=\"5\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman\', Times, serif; font-weight:bold; color:#FFFFFF; font-size:16px; background-color:#0099FF;\">Datos generales del viaje</th></tr>");
            cuerpoCorreo.append("<tr><th style=\"").append(getEstiloTitulo()).append("\">Fecha programada </th>");
            cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Hora programada </th>");
            cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Fecha de Salida</th>");
            cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Hora de Salida</th>");
            if (sgViaje != null
                    && sgViaje.getFechaProgramada() != null && sgViaje.getHoraProgramada() != null
                    && sgViaje.getFechaSalida() != null && sgViaje.getHoraSalida() != null) {
                cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Tiempo de Retraso</th>");
            }
            cuerpoCorreo.append("</tr>");
            cuerpoCorreo.append("<tr>");
            cuerpoCorreo.append("<td height=\"18\"  style=\"").append(getEstiloContenido()).append("\" align=\"center\">");
            cuerpoCorreo.append(validarNullFechaHtml(sgViaje.getFechaProgramada())).append("</td>");
            cuerpoCorreo.append("<td height=\"18\"  style=\"").append(getEstiloContenido()).append(" \" align=\"center\">");
            cuerpoCorreo.append(validarNullHoraHtml(sgViaje.getHoraProgramada())).append("</td>");
            cuerpoCorreo.append("<td height=\"18\"  style=\"").append(getEstiloContenido()).append("\" align=\"center\">");
            cuerpoCorreo.append(validarNullFechaHtml(sgViaje.getFechaSalida())).append("</td>");
            cuerpoCorreo.append("<td height=\"18\"  style=\"").append(getEstiloContenido()).append("\" align=\"center\">");
            cuerpoCorreo.append(validarNullHoraHtml(sgViaje.getHoraSalida())).append("</td>");
            if (sgViaje != null
                    && sgViaje.getFechaProgramada() != null && sgViaje.getHoraProgramada() != null
                    && sgViaje.getFechaSalida() != null && sgViaje.getHoraSalida() != null) {
                cuerpoCorreo.append("<td height=\"18\"  style=\"").append(getEstiloContenido()).append("\" align=\"center\"><b>");
                cuerpoCorreo.append(validarNullHtml(siManejoFechaLocal.horasEntreFechas(
                        siManejoFechaLocal.componerFecha(sgViaje.getFechaProgramada(), sgViaje.getHoraProgramada()
                        ), siManejoFechaLocal.componerFecha(sgViaje.getFechaSalida(), sgViaje.getHoraSalida())))).append("<b></td>");
            }
            cuerpoCorreo.append("</tr></table><br/>");

        } catch (Exception e) {
            log("Exepcion al llenar Datos " + e.getMessage());

        }
    }

    /**
     * Los datos del viaje y contiene la Fecha y Hora de regreso La fecha La
     * hora de regreso
     *
     * @param fechaProgramada
     * @param horaProgramada
     * @param fechaSalida
     * @param horaSalida
     * @param redondo
     */
    private void datosViajeParaViajero(Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, boolean redondo) {
        log("datosViaje");
        try {
            this.cuerpoCorreo.append("<br/><table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\" >");
            this.cuerpoCorreo.append("<tr><th colspan=\"3\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman\', Times, serif; font-weight:bold; color:#FFFFFF; font-size:16px; background-color:#0099FF;\">Datos generales del viaje</th></tr>");
            this.cuerpoCorreo.append("<tr><th style=\"").append(getEstiloTitulo()).append("\" align=\"center\">Fecha programada </th>");
            this.cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\" align=\"center\">Hora programada </th>");
            this.cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\" align=\"center\">Tipo viaje</th></tr>");
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td  style=\"").append(getEstiloContenido()).append("\" align=\"center\">".concat(fechaSalida != null ? Constantes.FMT_TextDate.format(fechaSalida) : Constantes.FMT_TextDate.format(fechaProgramada)).concat("</td>"));
            this.cuerpoCorreo.append("<td  style=\"").append(getEstiloContenido()).append("\" align=\"center\">".concat(horaSalida != null ? Constantes.FMT_hmm_a.format(horaSalida)+"Hrs" : Constantes.FMT_hmm_a.format(horaProgramada)+"Hrs").concat("</td>"));
            this.cuerpoCorreo.append("<td  style=\"").append(getEstiloContenido()).append("\" align=\"center\">".concat(redondo ? Constantes.redondo : Constantes.sencillo).concat("</td></tr>"));
            this.cuerpoCorreo.append("</table><br/>");
        } catch (Exception e) {
            log("Exepcion al llenar Datos " + e.getMessage());

        }
    }

    /**
     * Los datos del viaje y contiene la Fecha y Hora de regreso La fecha La
     * hora de regreso
     *
     * @param fechaProgramada
     * @param horaProgramada
     * @param fechaSalida
     * @param horaSalida
     * @param fechaRegreso
     * @param horaRegreso
     * @param redondo
     */
    private void datosViajeVO(Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo) {
        log("datosViaje");
        try {

            cuerpoCorreo.append("<br/><table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\" >");
            cuerpoCorreo.append("<thead><tr><th colspan=\"4\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman\', Times, serif; font-weight:bold; color:#FFFFFF; font-size:16px; background-color:#0099FF;\">Datos generales del viaje</th></tr>");
            cuerpoCorreo.append("<tr><th style=\"").append(getEstiloTitulo()).append("\">Fecha programada </th>");
            cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Hora programada </th>");
            cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Fecha de Salida</th>");
            cuerpoCorreo.append("<th style=\"").append(getEstiloTitulo()).append("\">Hora de Salida</th>");
            cuerpoCorreo.append("<tr>");
            cuerpoCorreo.append("<td height=\"18\"  style=\"").append(getEstiloContenido()).append("\" align=\"center\">");
            cuerpoCorreo.append(validarNullFechaHtml(fechaProgramada)).append("</td>");
            cuerpoCorreo.append("<td height=\"18\"  style=\"").append(getEstiloContenido()).append(" \" align=\"center\">");
            cuerpoCorreo.append(validarNullHoraHtml(horaProgramada)).append("</td>");
            cuerpoCorreo.append("<td height=\"18\"  style=\"").append(getEstiloContenido()).append("\" align=\"center\">");
            cuerpoCorreo.append(validarNullFechaHtml(fechaSalida)).append("</td>");
            cuerpoCorreo.append("<td height=\"18\"  style=\"").append(getEstiloContenido()).append("\" align=\"center\">");
            cuerpoCorreo.append(validarNullHoraHtml(horaSalida)).append("</td>");
            cuerpoCorreo.append("</table><br/>");
        } catch (Exception e) {
            log("Exepcion al llenar Datos " + e.getMessage());

        }
    }

    
    public StringBuilder sendMailStopTrip(SgViaje sgViaje, List<ViajeroVO> l, String motivo) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Viaje detenido --".concat(sgViaje.getCodigo())));
        this.cuerpoCorreo.append("<br/><p>Estimado(a) <b> ".concat(sgViaje.getResponsable().getNombre()).concat("</b>"));
        this.cuerpoCorreo.append("<p> El viaje con código <b>".concat(sgViaje.getCodigo()).concat(" "
                + " </b> y fecha de salida ".concat(sgViaje.getFechaSalida() != null ? sdf.format(sgViaje.getFechaSalida()) : sdf.format(sgViaje.getFechaProgramada())).concat(" <b>se ha detenido</b> por el motivo que a continuación se menciona.</b></p>")));
        this.cuerpoCorreo.append("<p> Motivo: ".concat(motivo).concat("</p>"));
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder sendMailAddTravellerToTrip(Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            String codigo, String responsable, String origen, int idRuta, int tipoViaje, int idViaje, String viajero) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Viajero agregado al viaje - ".concat(codigo)));
        this.cuerpoCorreo.append("<br/><p>Estimado(a) <b> ".concat(responsable).concat("</b>"));
        this.cuerpoCorreo.append("<p>El viajero <b>".concat(viajero).concat("</b> se ha agregado al viaje con código <b>".concat(codigo).concat(""
                + "</b> con fecha de salida ".concat("<u>".concat(fechaSalida != null ? sdf.format(fechaSalida) : sdf.format(fechaProgramada)).concat("</u>")))).concat(".</p>"));
        this.cuerpoCorreo.append("<table width=\"100%\" border=\"0\" cellspacing=\"8\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\" >");
        datosViajeVO(fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo);
        this.cuerpoCorreo.append("</td><td>");
        datosVehiculo(idViaje);
        this.cuerpoCorreo.append("</td></tr></table>");

        this.cuerpoCorreo.append("</br>");

        switch (tipoViaje) {
            case Constantes.RUTA_TIPO_CIUDAD:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_CIUDAD);
                break;
            case Constantes.RUTA_TIPO_OFICINA:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_OFICINA);
                break;
            case Constantes.RUTA_TIPO_LUGAR:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_LUGAR);
                break;
        }
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder sendTakeOutTravellToTraveller(String codigo, Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            String responsable, String viajero) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Viajero quitado del viaje -- ".concat(codigo)));
        this.cuerpoCorreo.append("<br/><p>Estimado(a) <b> ".concat(responsable).concat("</b>"));
        this.cuerpoCorreo.append("<p>El viajero <b>".concat(viajero).concat("</b> se ha quitado del viaje con código <b>".concat(codigo).concat(","
                + "</b> con fecha de salida ".concat(fechaSalida != null ? sdf.format(fechaSalida) : sdf.format(fechaProgramada)))).concat(".</p>"));
        datosViajeVO(fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo);
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder sendEmailTakeOutTravellToTravellerInDestiny(String codigo, Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            String responsable, String viajero, String origen, int idRuta, int tipoRuta) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Viajero llegó a su destino - ".concat(codigo)));
        this.cuerpoCorreo.append("<br/><p>Estimado(a) <b> ".concat(responsable).concat("</b>"));
        this.cuerpoCorreo.append("<p>El viajero <b>".concat(viajero).concat("</b> del viaje con código <b>".concat(codigo).concat("</b> ha llegado a su destino")).concat(".</p>"));
        datosViajeVO(fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo);
        rutaViaje(origen, idRuta, tipoRuta);
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder bodyMailPublicTravelCompanyCar(Date fechaProgramada, Date horaProgramada, Date fechaSalida,
            Date horaSalida, Date fechaRegreso, Date horaRegreso, String codigo, boolean redondo, int tipoViaje, String origen,
            int idRuta, String conductorResponsable, String telefono) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Logística de viaje - " + codigo));
        this.cuerpoCorreo.append("<br/><p>Les envío la logística  programada con respecto al viaje <b> ".concat(codigo).concat("</b>"));
        this.cuerpoCorreo.append(" con fecha de salida <u>".concat(fechaSalida != null ? Constantes.FMT_TextDate.format(fechaSalida) : Constantes.FMT_TextDate.format(fechaProgramada)).concat("</u> a las <u>").concat(horaSalida != null ? Constantes.FMT_hmm_a.format(horaSalida)+"Hrs" : Constantes.FMT_hmm_a.format(horaProgramada)+"Hrs").concat("</u>.</p> <p>Si desean enviar algo favor de dejarlo en el área de correspondencia o comunicarse con los conductores.</p>"));

        //datosViajeVO(fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo);
        datosViajeParaViajero(fechaProgramada, horaProgramada, fechaSalida, horaSalida, redondo);

        switch (tipoViaje) {
            case Constantes.RUTA_TIPO_CIUDAD:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_CIUDAD);
                break;
            case Constantes.RUTA_TIPO_OFICINA:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_OFICINA);
                break;
            case Constantes.RUTA_TIPO_LUGAR:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_LUGAR);
                break;
        }
        agregarConductorResponsable(conductorResponsable, true, false, "100%", telefono);
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder bodyMailAnalistaPrincipal(Date fechaProgramada, Date horaProgramada, Date fechaSalida,
            Date horaSalida, Date fechaRegreso, Date horaRegreso, String codigo, boolean redondo, int tipoViaje, String origen,
            int idRuta, String conductorResponsable, String telefono) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Viaje creado - " + codigo));
        this.cuerpoCorreo.append("<br/><p>Se ha creado el viaje <b>").append(codigo).append("</b> favor de acceder al ");
        this.cuerpoCorreo.append("<A HREF='").append(Configurador.urlSia()).append("Sia' TARGET='_new'>SIA</A>").append(" Para validar los Datos");

        //datosViajeVO(fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo);
        datosViajeParaViajero(fechaProgramada, horaProgramada, fechaSalida, horaSalida, redondo);

        switch (tipoViaje) {
            case Constantes.RUTA_TIPO_CIUDAD:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_CIUDAD);
                break;
            case Constantes.RUTA_TIPO_OFICINA:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_OFICINA);
                break;
            case Constantes.RUTA_TIPO_LUGAR:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_LUGAR);
                break;
        }
        // agregarConductorResponsable(conductorResponsable, true, false, "100%", telefono);
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    private void rutaViaje(String origen, int idRuta, int idTipoRuta) {
        try {
            this.cuerpoCorreo.append("<br/><table width=\"100%\" align=\"center\" cellspacing=\"0\">");
            this.cuerpoCorreo.append("<tr><th colspan=\"2\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Ruta de viaje</th></tr>");
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<th width=\"50%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Origen</th>");
            this.cuerpoCorreo.append("<th width=\"50%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Destino</th>");
            this.cuerpoCorreo.append("</tr>");
            //
            this.cuerpoCorreo.append("<tr>");
            //   UtilLog4j.log.info(this, "IdInvitado: " + viajero.getIdInvitado());
            this.cuerpoCorreo.append("<td ").append(" width=\"35%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">");
            cuerpoCorreo.append(validarNullHtml(origen)).append("</td>");
            this.cuerpoCorreo.append("<td ").append(" width=\"20%\" align=\"center\" style=\"border: 1px solid  #b5b5b5;\">");
            this.cuerpoCorreo.append(validarNullHtml(obtenerDestino(idRuta, idTipoRuta))).append("</td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("</tr></table>");
        } catch (Exception ex) {
            log("Error al crear la ruta del viaje: " + ex.getMessage());
        }
    }

    
    public StringBuilder bodyMailTravel(int idViaje, String codigo, SgViajero sgViajero) {
        UtilLog4j.log.info(this, "  * * * ** * * * * ** * bodyMailTravel");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SgViaje sgViaje = sgViajeRemote.find(idViaje);
        List<ViajeroVO> lo = new ArrayList<ViajeroVO>();
        ViajeroVO vo = new ViajeroVO();

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Salida de viaje - " + codigo));
        this.cuerpoCorreo.append("</br>");
        this.cuerpoCorreo.append("Se ha programado el viaje ".concat("<b>").concat(codigo).concat("</b>").concat(". A continuación se muestran los datos del viaje."));
        this.cuerpoCorreo.append("</br>");
        this.cuerpoCorreo.append("<table width=\"100%\" border=\"0\" cellspacing=\"8\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\" >");

        //datosViaje(sgViaje);
        datosViajeParaViajero(sgViaje.getFechaProgramada(), sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(), sgViaje.isRedondo());
        this.cuerpoCorreo.append("</td><td>");
        datosVehiculo(sgViaje.getId());
        this.cuerpoCorreo.append("</td></tr></table>");
        this.cuerpoCorreo.append("</br>");

        String telefono = " ";
        if (sgViajero.getUsuario() != null) {
            if (sgViajero.getUsuario().getTelefono() != null) {
                telefono = sgViajero.getUsuario().getTelefono();
            }
        }
        vo.setUsuario(sgViajero.getUsuario() != null ? sgViajero.getUsuario().getNombre() : "null");
        vo.setInvitado(sgViajero.getSgInvitado() != null ? sgViajero.getSgInvitado().getNombre() : "null");
        vo.setIdInvitado(sgViajero.getSgInvitado() != null ? sgViajero.getSgInvitado().getId() : 0);
        vo.setIdUsuario(sgViajero.getUsuario() != null ? sgViajero.getUsuario().getId() : "null");
        vo.setTelefono(telefono);
        vo.setEstancia(sgViajero.isEstancia());
        vo.setTipoViajero(sgViajero.getUsuario() != null ? Constantes.SG_TIPO_ESPECIFICO_EMPLEADO : 0);
        vo.setIdSolicitudViaje(sgViajero.getSgSolicitudViaje() != null ? sgViajero.getSgSolicitudViaje().getId() : 0);
        lo.add(vo);
        lo.addAll(sgViajeroRemote.getTravellersByTravel(idViaje, null));
        listaViajeros(lo);
        //this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("</br>");
        if (sgViaje.getSgViajeCiudad() != null) {
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_CIUDAD);
        } else if (sgViaje.getSgViajeLugar() != null) {
            log("Poner Ruta");
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_LUGAR);
        } else {
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_OFICINA);
        }
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    /*
     * Correo de notificacion para los analistas en ruta del viaje Donde el
     * parametro destino significa a que usuarios va dirigido
     */
    
    public StringBuilder bodyMailNotificationByAnalistInRoute(String codigo, Date fechaProgramada, Date horaProgramada, Date fechaSalida,
            Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            int tipoViaje, String origen, int idRuta, String conductor, String telefono, boolean ida) {
        UtilLog4j.log.info(this, "********************bodyMailNotificationByAnalistInRoute");

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();

        this.cuerpoCorreo.append(plantilla.getInicio());
        if (ida) {
            this.cuerpoCorreo.append(this.getTitulo("Logística de viaje - " + codigo));
            this.cuerpoCorreo.append("<br/><p>Envío la logística programada con respecto al viaje <b> ".concat(codigo).concat("</b>"));
            this.cuerpoCorreo.append(" con fecha de salida <u>".concat(Constantes.FMT_TextDate.format(fechaSalida)));
            this.cuerpoCorreo.append("</u>, por favor estar pendiente para generar el viaje de regreso.</p>");
        } else {
            this.cuerpoCorreo.append(this.getTitulo("Logística de viaje - " + codigo));
            this.cuerpoCorreo.append("<br/><p>El viaje <b> ".concat(codigo).concat("</b>"));
            this.cuerpoCorreo.append(" salio de la oficina <b> ".concat(origen).concat("</b>."));
            this.cuerpoCorreo.append(" con fecha de salida <u>".concat(fechaSalida != null ? Constantes.FMT_TextDate.format(fechaSalida) : Constantes.FMT_TextDate.format(fechaProgramada)).concat("</u>."));
            //this.cuerpoCorreo.append(" con fecha de salida <u>".concat(Constantes.FMT_TextDate.format(sgViaje.getFechaSalida())).concat("</u>"));

        }

        //DATOS DEL VIAJE con fecha y hora de regresop
        datosViajeVO(fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo);
        //DATOS DEL VIAJE
        switch (tipoViaje) {
            case Constantes.RUTA_TIPO_CIUDAD:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_CIUDAD);
                break;
            case Constantes.RUTA_TIPO_OFICINA:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_OFICINA);
                break;
            case Constantes.RUTA_TIPO_LUGAR:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_LUGAR);
                break;
        }
//////        if (sgViaje.getSgViajeCiudad() != null) {
//////            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_CIUDAD);
//////        } else if (sgViaje.getSgViajeLugar() != null) {
//////            log("Poner Ruta");
//////            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_LUGAR);
//////        } else {
//////            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_OFICINA);
//////        }

        //conductores(16, sgViaje.getSgOficina().getId());
        //solo agregar a el conductor resposable
        //CONDUCTOR RESPOSABLE
        agregarConductorResponsable(conductor, true, false, "100%", telefono);
        //  this.cuerpoCorreo.append("</table>");
        //CONDUCTOR RESPONSABLE
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    private void agregarConductorResponsable(String conductorResponsable, boolean isConductor, boolean isCancelacion, String tableWith, String telefono) {
        String th;
        if (isCancelacion) {
            //rojo
            th = "<th colspan=\"2\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman\', Times, serif; font-weight:bold; color:#FFFFFF; font-size:16px; background-color:#0099FF;\" >";
        } else {
            //azul
            th = "<th colspan=\"2\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman\', Times, serif; font-weight:bold; color:#FFFFFF; font-size:13px; background-color:#0099FF;\" >";
        }
        this.cuerpoCorreo.append("<br/><table align=\"center\" width=\"".concat(tableWith).concat("\" cellspacing=\"0\" cellpadding=\"1\" >"));
        this.cuerpoCorreo.append("<tr >".concat(th).concat(isConductor ? "Conductor" : "Conductor").concat(" del viaje</th></tr>"));
        this.cuerpoCorreo.append("<tr><td width=\"41%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\">Nombre</td>");
        this.cuerpoCorreo.append("<td width=\"41%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\">Teléfono</td></tr>");

        this.cuerpoCorreo.append("<tr><td width=\"59%\" align=\"center\" height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(conductorResponsable).concat("</td>"));
        this.cuerpoCorreo.append("<td width=\"59%\" align=\"center\" height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(telefono == null ? " - " : telefono).concat("</td></tr>"));
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("<br/>");
    }

    /*
     * Correo de notificacion para los analistas en ruta del viaje
     */
    
    public StringBuilder bodyMailNotificationByDireccionGral(List<ViajeVO> listaViaje, String fechaSalida) {
        UtilLog4j.log.info(this, "bodyMailNotificationByAnalistInRoute");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Reporte de viajes - " + fechaSalida));

        this.cuerpoCorreo.append("<br/><p>Se envía la reporte de viajes programados para el día <b> ").append(fechaSalida).append("</b>");
        this.cuerpoCorreo.append("<br/>");
        //Datos del viaje
        datosViajeViajeros(listaViaje, true);
        //Pie del correo
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }
//

    private void datosVehiculo(SgVehiculo sgVehiculo) {
        this.cuerpoCorreo.append("<br/><table width=\"100%\" cellspacing=\"0\" align=\"center\"> <tr> <th colspan=\"4\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:16px; background-color:#0099FF;\"> Datos del vehículo</th> </tr>");
        this.cuerpoCorreo.append("<tr> <th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Marca</th>");
        this.cuerpoCorreo.append("<th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Modelo</th>");
        this.cuerpoCorreo.append("<th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Placa</th>");
        this.cuerpoCorreo.append("<th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Color</th> </tr>");

        this.cuerpoCorreo.append("<tr><td width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(sgVehiculo.getSgMarca().getNombre()).concat("</td>"));
        this.cuerpoCorreo.append("<td width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(sgVehiculo.getSgModelo().getNombre()).concat("</td>"));
        this.cuerpoCorreo.append("<td width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(sgVehiculo.getNumeroPlaca()).concat("</td> "));
        this.cuerpoCorreo.append("<td width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(sgVehiculo.getSgColor().getNombre()).concat("</td> </tr> </table><br/>"));

    }

    private void datosVehiculo(VehiculoVO vehiculoVO) {
        this.cuerpoCorreo.append("<br/><table width=\"100%\" cellspacing=\"0\" align=\"center\"> <tr> <th colspan=\"4\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:16px; background-color:#0099FF;\"> Datos del vehículo</th> </tr>");
        this.cuerpoCorreo.append("<tr> <th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Marca</th>");
        this.cuerpoCorreo.append("<th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Modelo</th>");
        this.cuerpoCorreo.append("<th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Placa</th>");
        this.cuerpoCorreo.append("<th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Color</th> </tr>");

        this.cuerpoCorreo.append("<tr><td width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(vehiculoVO.getMarca()).concat("</td>"));
        this.cuerpoCorreo.append("<td width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(vehiculoVO.getModelo()).concat("</td>"));
        this.cuerpoCorreo.append("<td width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(vehiculoVO.getNumeroPlaca()).concat("</td> "));
        this.cuerpoCorreo.append("<td width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(vehiculoVO.getColor()).concat("</td> </tr> </table><br/>"));

    }

    private void datosVehiculoPorId(int idVehiculo) {
        VehiculoVO sgVehiculo = sgVehiculoRemote.buscarVehiculoPorId(idVehiculo);
        this.cuerpoCorreo.append("<br/><table width=\"100%\" cellspacing=\"0\" align=\"center\"> <tr> <th colspan=\"4\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:16px; background-color:#0099FF;\"> Datos del vehículo</th> </tr>");
        this.cuerpoCorreo.append("<tr> <th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Marca</th>");
        this.cuerpoCorreo.append("<th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Modelo</th>");
        this.cuerpoCorreo.append("<th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Placa</th>");
        this.cuerpoCorreo.append("<th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Color</th> </tr>");

        this.cuerpoCorreo.append("<tr><td width=\"25%\" style=\"border: 1px solid #b5b5b5;\" align=\"center\" >".concat(sgVehiculo.getMarca()).concat("</td>"));
        this.cuerpoCorreo.append("<td width=\"25%\"  style=\"border: 1px solid #b5b5b5;\" align=\"center\"> ".concat(sgVehiculo.getModelo()).concat("</td>"));
        this.cuerpoCorreo.append("<td width=\"25%\" style=\"border: 1px solid #b5b5b5;\" align=\"center\">".concat(sgVehiculo.getNumeroPlaca()).concat("</td> "));
        this.cuerpoCorreo.append("<td width=\"25%\" style=\"border: 1px solid #b5b5b5;\" align=\"center\">".concat(sgVehiculo.getColor()).concat("</td> </tr> </table><br/>"));

    }

    private void datosVehiculo(int idViaje) {
        SgVehiculo sgVehiculo = sgViajeVehiculoRemote.getVehicleByTravel(idViaje).getSgVehiculo();
        this.cuerpoCorreo.append("<br/><table width=\"100%\" cellspacing=\"0\" align=\"center\"> <tr> <th colspan=\"4\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:16px; background-color:#0099FF;\"> Datos del vehículo</th> </tr>");
        this.cuerpoCorreo.append("<tr> <th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Marca</th>");
        this.cuerpoCorreo.append("<th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Modelo</th>");
        this.cuerpoCorreo.append("<th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Placa</th>");
        this.cuerpoCorreo.append("<th width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\"><b>Color</th> </tr>");

        this.cuerpoCorreo.append("<tr><td width=\"25%\" style=\"border: 1px solid #b5b5b5;\" align=\"center\">".concat(sgVehiculo.getSgMarca().getNombre()).concat("</td>"));
        this.cuerpoCorreo.append("<td width=\"25%\" style=\"border: 1px solid #b5b5b5;\" align=\"center\">".concat(sgVehiculo.getSgModelo().getNombre()).concat("</td>"));
        this.cuerpoCorreo.append("<td width=\"25%\" style=\"border: 1px solid #b5b5b5;\" align=\"center\">".concat(sgVehiculo.getNumeroPlaca()).concat("</td> "));
        this.cuerpoCorreo.append("<td width=\"25%\" style=\"border: 1px solid #b5b5b5;\" align=\"center\">".concat(sgVehiculo.getSgColor().getNombre()).concat("</td> </tr> </table><br/>"));

    }

    
    public StringBuilder cancelTripSecurity(String nombre, int idViaje, String codigo, String motivo) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SgViaje sgViaje = sgViajeRemote.find(idViaje);
        //Usuario u = usuarioRemote.find(idSesion);
        List<ViajeroVO> lo = new ArrayList<ViajeroVO>();

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Cancelación de viaje - " + codigo));
        this.cuerpoCorreo.append("</br>");
        this.cuerpoCorreo.append("<p>Se canceló el viaje <b>".concat(codigo).concat("</b> programado para salir el día <b>").concat(sgViaje.getFechaSalida() != null ? Constantes.FMT_TextDate.format(sgViaje.getFechaSalida()) : Constantes.FMT_TextDate.format(sgViaje.getFechaProgramada())));//
        //. A continuación se envia los datos del viaje."));
        this.cuerpoCorreo.append(".</b> Por el motivo que a continuación se menciona. </p>");
        //Cancelo
        datosCancelacion(nombre, motivo);
        //datos del vehiculo
        //datosViaje(sgViaje);
        datosViajeParaViajero(sgViaje.getFechaProgramada(), sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(), sgViaje.isRedondo());
        //
        //Datos del  vehiculo
        if (sgViaje.isVehiculoAsignadoEmpresa()) {
            datosVehiculo(sgViaje.getId());
        }

        lo.addAll(sgViajeroRemote.getTravellersByTravel(idViaje, null));
        listaViajeros(lo);

        //
        if (sgViaje.getSgViajeCiudad() != null) {
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_CIUDAD);
        } else if (sgViaje.getSgViajeLugar() != null) {
            log("Poner Ruta");
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_LUGAR);
        } else {
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_OFICINA);
        }

        if (sgViaje.isVehiculoAsignadoEmpresa()) {
            agregarConductorResponsable(sgViaje.getResponsable().getNombre(), true, true, "100%", sgViaje.getResponsable().getTelefono());
        } else {
            agregarConductorResponsable(sgViaje.getResponsable().getNombre(), false, true, "100%", sgViaje.getResponsable().getTelefono());
        }
        //this.cuerpoCorreo.append("</table>");
        //Pie del correo
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    private void datosCancelacion(String nombre, String motivo) {
        this.cuerpoCorreo.append("<br/><table align=\"left\"><thead><tr><th colspan=\"2\"><b>Datos de la cancelación</b></th></tr></thead>");
        this.cuerpoCorreo.append("<tr><td>Canceló:</td><td align=\"left\">  <b>".concat(nombre).concat("</b></td></tr>"));
        this.cuerpoCorreo.append("<tr><td>Motivo: </td><td align=\"left\"> <font color= \"red\">".concat(motivo).concat("</font></td></tr>"));
        this.cuerpoCorreo.append("<tr><td>Fecha: </td><td  align=\"left\">".concat(Constantes.FMT_ddMMyyy.format(new Date())).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td>Hora: </td><td  align=\"left\">".concat(Constantes.FMT_hmm_a.format(new Date())+"Hrs").concat("</td><tr>"));
        this.cuerpoCorreo.append("</table><br/>");
    }

    //Envia notificacion a la oficina origen para que finalize el viaje
    //Solo aplica cuando es un viaje de regreso
    
    public StringBuilder bodyMailNotificationByOficceForFinalize(String codigo, Date fechaProgramada,
            Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso,
            Date horaRegreso, boolean redondo, int tipoViaje, String origen,
            int idRuta) {
        log("body Mail Notification By Oficce For Finalize");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Finalizar viaje - " + codigo));

        this.cuerpoCorreo.append("<br/><p>El viaje <b>".concat(codigo).concat("</b>").concat(" salió de la oficina <b>").concat(origen).concat("</b>"));
        this.cuerpoCorreo.append(" el día <u>".concat(fechaSalida != null ? Constantes.FMT_TextDate.format(fechaSalida) : Constantes.FMT_TextDate.format(fechaProgramada)).concat("</u>."));
        this.cuerpoCorreo.append("<b> Por favor estar pendiente a su llegada para finalizar el viaje.</b>");
        this.cuerpoCorreo.append("<br/>");
        //

        //Datos del viaje
        datosViajeVO(fechaProgramada, horaProgramada, fechaProgramada, horaSalida, fechaRegreso, horaRegreso, redondo);
//
        switch (tipoViaje) {
            case Constantes.RUTA_TIPO_CIUDAD:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_CIUDAD);
                break;
            case Constantes.RUTA_TIPO_OFICINA:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_OFICINA);
                break;
            case Constantes.RUTA_TIPO_LUGAR:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_LUGAR);
                break;
        }
        //Pie del correo
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder bodyMailAvisoAnalistaViajeCiudadRegreso(SgViaje sgViaje) {
        log("HtmlNotificacionViajeImpl.bodyMailAvisoAnalistaViajeCiudadRegreso()");

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Viaje de regreso - " + sgViaje.getCodigo()));

        this.cuerpoCorreo.append("<br/><p>Se ha programado automáticamente el viaje de regreso <b>\"".concat(sgViaje.getCodigo()).concat("\"</b>"));
        this.cuerpoCorreo.append(" con fecha de salida <u>".concat(sgViaje.getFechaSalida() != null ? Constantes.FMT_TextDate.format(sgViaje.getFechaSalida()) : Constantes.FMT_TextDate.format(sgViaje.getFechaProgramada())).concat("</u>."));
        this.cuerpoCorreo.append("<b> Por favor confirma que los datos sean correctos y si no es así entra al SIA a corregirlos.</b>");

        datosViaje(sgViaje);
        this.cuerpoCorreo.append("<br/>");

        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    /**
     * MLUIS 01/11/2013
     *
     * @param lvro
     * @param vehiculoVO
     * @param tipoViaje
     * @param fechaProgramada
     * @param horaProgramada
     * @param codigo
     * @param horaSalida
     * @param fechaRegreso
     * @param horaRegreso
     * @param redondo
     * @param fechaSalida
     * @param origen
     * @param responsableViaje
     * @param autobus
     * @param idRuta
     * @param vehiculoAsignadoEmpresa
     * @param telefono
     * @return
     */
    
    public StringBuilder bodyMailModifyTravelCompanyCar(List<ViajeroVO> lvro, VehiculoVO vehiculoVO, String codigo,
            Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            int tipoViaje, String origen, int idRuta, String responsableViaje, String telefono, boolean vehiculoAsignadoEmpresa, boolean autobus) {

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Modificación del viaje - " + codigo));

        this.cuerpoCorreo.append("<br/><p> Se modificò la logística  programada para el viaje <b> ".concat(codigo).concat("</b>"));
        this.cuerpoCorreo.append(" con fecha de salida <u>"
                .concat(fechaSalida != null ? Constantes.FMT_TextDate.format(fechaSalida) : Constantes.FMT_TextDate.format(fechaProgramada))
                .concat("</u>").concat(" a las <u>").concat(horaSalida != null ? Constantes.FMT_hmm_a.format(horaSalida)+"Hrs"
                : Constantes.FMT_hmm_a.format(horaProgramada)+"Hrs").concat("</u>."));
        this.cuerpoCorreo.append("<br/>");
        //Datos del viaje
        datosViajeVO(fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo);
        //Viajeros
        listaViajeros(lvro);
        //Ruta del viaje
        switch (tipoViaje) {
            case Constantes.RUTA_TIPO_CIUDAD:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_CIUDAD);
                break;
            case Constantes.RUTA_TIPO_OFICINA:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_OFICINA);
                break;
            case Constantes.RUTA_TIPO_LUGAR:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_LUGAR);
                break;
        }
        // Conductores
        //conductores(16, sgViaje.getSgOficina().getId());
        if (vehiculoAsignadoEmpresa) {
            datosVehiculo(vehiculoVO);
            agregarConductorResponsable(responsableViaje, true, false, "100%", telefono);
        } else {
            agregarConductorResponsable(responsableViaje, false, false, "100%", telefono);
        }

        //Pie del correo
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    /**
     * MLUIS 01/11/2013
     */
    
    public StringBuilder bodyMailTravelNoCompanyCarCity(List<ViajeroVO> listViajero, String codigo,
            Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            int tipoViaje, String origen, int idRuta, String conductor, String telefono) {
        log("HtmlNotificacionViajeImpl.bodyMailCancelTravelNoCompanyCar()");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Viaje fuera de oficina - " + codigo));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("Se ha generado el Viaje <b>".concat(codigo).concat("</b></p>"));
        this.cuerpoCorreo.append("<p> A continuación se muestra la información correspondiente: </p>");

        //Cancelo
        datosViajeVO(fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo);
        ///
        listaViajeros(listViajero);
//
        switch (tipoViaje) {
            case Constantes.RUTA_TIPO_CIUDAD:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_CIUDAD);
                break;
            case Constantes.RUTA_TIPO_OFICINA:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_OFICINA);
                break;
            case Constantes.RUTA_TIPO_LUGAR:
                rutaViaje(origen, idRuta, Constantes.RUTA_TIPO_LUGAR);
                break;
        }

        //agregarConductorResponsable(conductor, false, true, "85%", telefono);
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    /**
     * Creo: NLopez
     *
     * @param nombre
     * @param idViaje
     * @param codigo
     * @param motivo
     * @return
     */
    
    public StringBuilder pausarViajeSeguridad(String nombre, int idViaje, String codigo, String motivo) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SgViaje sgViaje = sgViajeRemote.find(idViaje);
        //Usuario u = usuarioRemote.find(idSesion);
        List<ViajeroVO> lo = new ArrayList<ViajeroVO>();

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Gestión de Riesgos supende el viaje - " + codigo));
        this.cuerpoCorreo.append("</br>");
        this.cuerpoCorreo.append("<p>Debido al alto riesgo, solo se permiten viajes críticos pero  unicamente con autorización previa de Gestion de Riesgos. "
                .concat("Póngase en contacto directamente con Centops lo más pronto posible  al email <b>centops@ihsa.mx</b> o al telefono <b>811 946 9999</b>, ")
                .concat("con la documentación adecuada para justificar la necesidad del viaje, para obtener autorización."));
        //Cancelo
        datosCancelacion(nombre, motivo);
        //datos del vehiculo
        datosViaje(sgViaje);
        //
        //Datos del  vehiculo
        if (sgViaje.isVehiculoAsignadoEmpresa()) {
            datosVehiculo(sgViaje.getId());
        }

        lo.addAll(sgViajeroRemote.getTravellersByTravel(idViaje, null));
        listaViajeros(lo);

        //
        if (sgViaje.getSgViajeCiudad() != null) {
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_CIUDAD);
        } else if (sgViaje.getSgViajeLugar() != null) {
            log("Poner Ruta");
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_LUGAR);
        } else {
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_OFICINA);
        }

        if (sgViaje.isVehiculoAsignadoEmpresa()) {
            agregarConductorResponsable(sgViaje.getResponsable().getNombre(), true, true, "100%", sgViaje.getResponsable().getTelefono());
        } else {
            agregarConductorResponsable(sgViaje.getResponsable().getNombre(), false, true, "100%", sgViaje.getResponsable().getTelefono());
        }
        //this.cuerpoCorreo.append("</table>");
        //Pie del correo
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    /**
     *
     * @param nombre
     * @param idViaje
     * @return
     */
    
    public StringBuilder aprobarViajeDireccion(String nombre, int idViaje) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SgViaje sgViaje = sgViajeRemote.find(idViaje);
        //Usuario u = usuarioRemote.find(idSesion);
        List<ViajeroVO> lo = new ArrayList<ViajeroVO>();

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Gestión de Riesgos autorizó el viaje - " + sgViaje.getCodigo()));
        this.cuerpoCorreo.append("</br>");
        this.cuerpoCorreo.append("<p>Gestión de Riesgos autorizó el viaje <b>".concat(sgViaje.getCodigo()).concat("</b> programado para salir el día <b>").concat(sgViaje.getFechaSalida() != null ? Constantes.FMT_TextDate.format(sgViaje.getFechaSalida()) : Constantes.FMT_TextDate.format(sgViaje.getFechaProgramada())));//
        //. A continuación se envia los datos del viaje."));
        this.cuerpoCorreo.append("</b> a las <b>".concat(sgViaje.getHoraSalida() != null ? Constantes.FMT_hmm_a.format(sgViaje.getHoraSalida())+"Hrs" : "--"));

        //datos del vehiculo
        datosViaje(sgViaje);
        //
        //Datos del  vehiculo
        if (sgViaje.isVehiculoAsignadoEmpresa()) {
            datosVehiculo(sgViaje.getId());
        }

        lo.addAll(sgViajeroRemote.getTravellersByTravel(idViaje, null));
        listaViajeros(lo);

        //
        if (sgViaje.getSgViajeCiudad() != null) {
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_CIUDAD);
        } else if (sgViaje.getSgViajeLugar() != null) {
            log("Poner Ruta");
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_LUGAR);
        } else {
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_OFICINA);
        }

        if (sgViaje.isVehiculoAsignadoEmpresa()) {
            agregarConductorResponsable(sgViaje.getResponsable().getNombre(), true, true, "100%", sgViaje.getResponsable().getTelefono());
        } else {
            agregarConductorResponsable(sgViaje.getResponsable().getNombre(), false, true, "100%", sgViaje.getResponsable().getTelefono());
        }
        //this.cuerpoCorreo.append("</table>");
        //Pie del correo
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder coreoSolicitudCambioItinerario(String mensaje, String codigo, String tipoItinerario, String empleado, int idSolicitud) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        //Usuario u = usuarioRemote.find(idSesion);
        List<ViajeroVO> lo = new ArrayList<ViajeroVO>();

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Cambio de itinerario de la solicitud - " + codigo));
        this.cuerpoCorreo.append("</br>");
        this.cuerpoCorreo.append("<p>Buenos días.</p>");//
        this.cuerpoCorreo.append("<p>El empleado <b>").append(empleado).append("</b> ha solicitado cambio(s) para el itinerario de vuelo de ").append(tipoItinerario.equals(Constantes.BOOLEAN_TRUE) ? "Ida" : "Regreso");
        cuerpoCorreo.append(" la solicitud <b>").append(codigo).append("</b></p>");
        this.cuerpoCorreo.append("<p>A continuación el mensaje enviado: <b>").append(mensaje).append("</b></p>");
        //Datos del itinerario actual
        cuerpoCorreo.append("</br>");
        mostrarSgItinerario(idSolicitud, tipoItinerario.equals(Constantes.BOOLEAN_TRUE));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder coreoSolicitudCambioItinerarioGenero(String mensaje, String codigo, String tipoItinerario, String empleado, int idSolicitud) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        //Usuario u = usuarioRemote.find(idSesion);
        List<ViajeroVO> lo = new ArrayList<ViajeroVO>();

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Cambio de itinerario de la solicitud - " + codigo));
        this.cuerpoCorreo.append("</br>");
        this.cuerpoCorreo.append("<p>Buenos días.</p>");//
        this.cuerpoCorreo.append("<p>Se solicitó el cambio para el itinerario de vuelo de la solicitud <b>").append(codigo).append("</b></p>");
        this.cuerpoCorreo.append("<p>A continuación el mensaje enviado: <b>").append(mensaje).append("</b></p>");
        //Datos del itinerario actual
        cuerpoCorreo.append("</br>");
        mostrarSgItinerario(idSolicitud, tipoItinerario.equals(Constantes.BOOLEAN_TRUE));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder coreoCambioItinerario(int idSolicitud, String consecutivo, boolean ida) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        //Usuario u = usuarioRemote.find(idSesion);
        List<ViajeroVO> lo = new ArrayList<ViajeroVO>();

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Itinerario de la solicitud - " + consecutivo));
        this.cuerpoCorreo.append("</br>");
        this.cuerpoCorreo.append("<p>Se envia el itienrario de vuelo para la solicitud <b>").append(consecutivo).append("</b></p>");
        this.cuerpoCorreo.append("<p></p>");
        //Datos del itinerario actual
        cuerpoCorreo.append("</br>");
        mostrarSgItinerario(idSolicitud, ida);
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder pausarViajeSeguridadServiciosGenerales(int idViaje, String codigo, String motivo, String nombre) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SgViaje sgViaje = sgViajeRemote.find(idViaje);
        //Usuario u = usuarioRemote.find(idSesion);
        List<ViajeroVO> lo = new ArrayList<ViajeroVO>();

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Gestión de Riesgos supende el viaje - " + codigo));
        this.cuerpoCorreo.append("</br>");
        this.cuerpoCorreo.append("<p>Debido al alto riesgo, solo se permiten viajes críticos pero  unicamente con autorización previa de Gestion de Riesgos. "
                .concat("Póngase en contacto directamente con Centops lo más pronto posible  al email <b>centops@ihsa.mx</b> o al telefono <b>811 946 9999</b>, ")
                .concat("con la documentación adecuada para justificar la necesidad del viaje, para obtener autorización."));

        //datos del vehiculo
        datosViaje(sgViaje);
        //
        //Datos del  vehiculo
        if (sgViaje.isVehiculoAsignadoEmpresa()) {
            datosVehiculo(sgViaje.getId());
        }

        lo.addAll(sgViajeroRemote.getTravellersByTravel(idViaje, null));
        listaViajeros(lo);

        //
        if (sgViaje.getSgViajeCiudad() != null) {
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_CIUDAD);
        } else if (sgViaje.getSgViajeLugar() != null) {
            log("Poner Ruta");
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_LUGAR);
        } else {
            rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_OFICINA);
        }

        if (sgViaje.isVehiculoAsignadoEmpresa()) {
            agregarConductorResponsable(sgViaje.getResponsable().getNombre(), true, true, "100%", sgViaje.getResponsable().getTelefono());
        } else {
            agregarConductorResponsable(sgViaje.getResponsable().getNombre(), false, true, "100%", sgViaje.getResponsable().getTelefono());
        }
        //this.cuerpoCorreo.append("</table>");
        //Pie del correo
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    private void log(String mensaje) {
        UtilLog4j.log.info(this, mensaje);
    }

    
    public StringBuilder mailNotificaSalidaViajeAereo(ViajeVO viajeVO, String correoSeguridad, String telefonoSeguridad) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Salida del viaje  - " + viajeVO.getCodigo()));

        this.cuerpoCorreo.append("<br/><p>El día de mañana  <b> ").append(Constantes.FMT_TextDateLarge.format(viajeVO.getFechaProgramada())).append("</b>");
        cuerpoCorreo.append(" tiene programado el viaje <b>").append(viajeVO.getCodigo()).append("</b>. Debido a la situación de inseguridad que se viven en el país, ");
        cuerpoCorreo.append(" se le pide se ponga en contacto con el departamento de Gestión de Riesgos al correo <b>").append(correoSeguridad).append("</b> o a los teléfonos <b>").append(telefonoSeguridad).append("</b>");
        cuerpoCorreo.append(" ").append(" para posibles recomendaciones.");
        this.cuerpoCorreo.append("<br/>");
        //Datos del viaje
        List<ViajeVO> lv = new ArrayList<ViajeVO>();
        lv.add(viajeVO);
        datosViajeViajeros(lv, true);
        //Pie del correo
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder mailNotificaSalidaViajeAereoGestionRiesgo(List<ViajeVO> lv, String fechaSalida) {
        UtilLog4j.log.info(this, "bodyMailNotificationByAnalistInRoute");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Reporte de salida de viaje - " + fechaSalida));

        this.cuerpoCorreo.append("<br/><p>Para el día de mañana <b> ").append(fechaSalida).append("</b>").append(" estan programados los siguientes viajes aéreos. </p>");
        this.cuerpoCorreo.append("<br/>");
        //Datos del viaje
        datosViajeViajeros(lv, true);
        //Pie del correo
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    private void agregarNotaHorarioAprobacion(Date fechaSalida) {
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<center>");
        this.cuerpoCorreo.append("<p style=\"font-family:Arial, Helvetica, sans-serif; font-size:13px;color:red;font-weight: bold;\">");
        if (siManejoFechaLocal.dayIsToday(fechaSalida)) {
            this.cuerpoCorreo.append("Esta solicitud deberá Aprobarse lo antes posible, ya que tiene fecha de salida para el día de hoy.");
        } else {
            String tiempo = "";
            Date fechaAprobar = new Date();
            int dia = siManejoFechaLocal.obtenerNumeroDiaDeFecha(fechaSalida);
             if(dia == 2){
                 fechaAprobar = siManejoFechaLocal.traerDiaSemana(new Date(), fechaSalida);
                tiempo = "las 12:00 hrs ";
             } else {
                 tiempo = "las 17:00 hrs ";
                fechaAprobar = this.siManejoFechaLocal.fechaRestarDias(fechaSalida, 1);
            }
            this.cuerpoCorreo.append("Esta solicitud deberá Aprobarse antes de ").append(tiempo).append(" del ");
            this.cuerpoCorreo.append("<span style=\"font-family:Arial, Helvetica, sans-serif; font-size:13px;color:red;font-weight: bold;text-decoration:underline;\">");
            this.cuerpoCorreo.append(Constantes.FMT_TextDateLarge.format(fechaAprobar));
            this.cuerpoCorreo.append(".");
            this.cuerpoCorreo.append("</span>");
        }
        this.cuerpoCorreo.append("</p>");
        this.cuerpoCorreo.append("</center>");
    }

    
    public StringBuilder correoCambioSolicitudViaje(List<EstatusAprobacionVO> lo, String nombreTenia, String nombreAprobara, String asunto, String status) {
        UtilLog4j.log.info(this, "bodyMailNotificationByAnalistInRoute");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(getTitulo(asunto));

        cuerpoCorreo.append("<br/><p>La lista de solicitudes de viaje pendientes de <b>").append(status).append("</b> por <b> ").append(nombreTenia).append("</b>");
        cuerpoCorreo.append(", se pasaron a la bandeja de <b>").append(nombreAprobara).append("</b> para continuar con el proceso de aprobación. </p>");
        this.cuerpoCorreo.append("<br/>");
        //lista de sol viaje
        listaSolicitudViaje(lo);
        //Pie del correo
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    private void listaSolicitudViaje(List<EstatusAprobacionVO> lo) {
        this.cuerpoCorreo.append("<br/><table width=\"100%\" align=\"center\" cellspacing=\"0\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"6\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Solicitudes de viaje(s)</th></tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Consecutivo</th>");
        this.cuerpoCorreo.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Origen</th>");
        this.cuerpoCorreo.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">F. Salida</th>");
        this.cuerpoCorreo.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">H. Salida</th>");
        this.cuerpoCorreo.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Motivo</th>");
        this.cuerpoCorreo.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Viajeros</th>");
        this.cuerpoCorreo.append("</tr>");
        for (EstatusAprobacionVO ea : lo) {
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(ea.getCodigo()).append("</td>");
            this.cuerpoCorreo.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(ea.getNombreOrigen()).append("</td>");
            this.cuerpoCorreo.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(Constantes.FMT_ddMMyyy.format(ea.getFechaSalida())).append("</td>");
            this.cuerpoCorreo.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(Constantes.FMT_HHmmss.format(ea.getHoraSalida())).append("</td>");
            this.cuerpoCorreo.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(ea.getNombreMotivo()).append("</td>");
            this.cuerpoCorreo.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(ea.getViajerosCount()).append("</td>");
            this.cuerpoCorreo.append("</tr>");
        }
        this.cuerpoCorreo.append("</table><br/>");
    }

    
    public StringBuilder mensajeSalidaViaje(SgViaje sgViaje, String asunto, String telefono, List<ViajeroVO> viajeros) {
        UtilLog4j.log.info(this, "mensajeSalidaViaje");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Viaje -- ".concat(sgViaje.getCodigo())));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<p> El viaje <b>".concat(sgViaje.getCodigo()).concat("</b>"));
        this.cuerpoCorreo.append(" salió de la oficina <b>").append(sgViaje.getSgOficina().getNombre()).append(".</b>");

        if (siManejoFechaLocal.compare(sgViaje.getFechaSalida(), new Date()) == 0) {
            cuerpoCorreo.append(" <b>Hoy</b> a las <b>").append(Constantes.FMT_hmm_a.format(sgViaje.getHoraSalida())+"Hrs");
        } else {
            cuerpoCorreo.append(" El día <b>").append(Constantes.FMT_TextDate.format(sgViaje.getFechaSalida())).append("</b> a las <b>").append(Constantes.FMT_hmm_a.format(sgViaje.getHoraSalida())+"Hrs");
        }

        this.cuerpoCorreo.append("</b>. </p> <br/>");
        this.cuerpoCorreo.append("A continuación se muestran los datos del viaje.<br/>");

        datosViaje(sgViaje);
        this.cuerpoCorreo.append("</br>");
        //poner conductor
        if (sgViaje.isVehiculoAsignadoEmpresa()){
            agregarConductorResponsable(sgViaje.getResponsable().getNombre(), true, false, "100%", telefono);
        } else {
            agregarConductorResponsable(sgViaje.getResponsable().getNombre(), false, false, "100%", telefono);
        }
        //
        if (sgViaje.isVehiculoAsignadoEmpresa()){
            SgViajeVehiculo sgVehiculo = sgViajeVehiculoRemote.getVehicleByTravel(sgViaje.getId());
            datosVehiculo(sgVehiculo.getSgVehiculo());
        }

//        this.cuerpoCorreo.append("</table>");
        int tipoViaje = obtenerTipoViaje(sgViaje);

        switch (tipoViaje) {
            case Constantes.RUTA_TIPO_CIUDAD:
                rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_CIUDAD);
                break;
            case Constantes.RUTA_TIPO_OFICINA:
                rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_OFICINA);
                break;
            case Constantes.RUTA_TIPO_LUGAR:
                rutaViaje(sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), Constantes.RUTA_TIPO_LUGAR);
                break;
        }
        if (viajeros != null && !viajeros.isEmpty()) {
            listaViajeros(viajeros);
        } else {
            this.cuerpoCorreo.append("</br>");
            this.cuerpoCorreo.append("El viaje <b>").append(sgViaje.getCodigo()).append("</b> se generó sin viajeros. ").append(" Posteriormente se podrán agregar ");
        }

        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder mensajeLlegada(ViajeVO viajeVO, int llegoA, String punto) {
        this.limpiarCuerpoCorreo();
        String llego = "";
        List<ViajeroVO> listaViajeros = null;
        switch (llegoA) {
            case Constantes.ORIGEN:
                llego = " en la oficina " + viajeVO.getOficina();
                listaViajeros = sgViajeroRemote.traerViajerosPorViajeMovimiento(viajeVO.getId(), Constantes.ID_SI_OPERACION_INTERCAMBIO_VIAJERO, Constantes.ORIGEN);
                if (listaViajeros == null || listaViajeros.isEmpty()) {
                    listaViajeros = sgViajeroRemote.getTravellersByTravel(viajeVO.getId(), null);
                }
                break;
            case Constantes.PUNTO_SEGURIDAD:
                llego = " en el paraje conocido como " + punto;
                listaViajeros = sgViajeroRemote.getTravellersByTravel(viajeVO.getId(), null);
                break;
            case Constantes.DESTINO:
                listaViajeros = sgViajeroRemote.getTravellersByTravel(viajeVO.getId(), null);
                if (viajeVO.getIdSgViajeCiudad() > Constantes.CERO) {
                    llego = "en la ciudad " + viajeVO.getDestino();
                } else {
                    llego = "en la oficina " + viajeVO.getDestino();
                }

                break;
        }
        try {
            SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
            this.cuerpoCorreo.append(plantilla.getInicio());
            this.cuerpoCorreo.append(this.getTitulo("Viaje -- ".concat(viajeVO.getCodigo())));
            this.cuerpoCorreo.append("<br/>");
            cuerpoCorreo.append("<p> El viaje <b>").append(viajeVO.getCodigo()).append("</b>, salió de la oficina <b>").append(viajeVO.getOficina());
            cuerpoCorreo.append("</b>");
            if (siManejoFechaLocal.compare(viajeVO.getFechaSalida(), new Date()) == 0) {
                cuerpoCorreo.append(" <b>hoy</b> a las <b>").append(viajeVO.getHoraSalida());
            } else {
                cuerpoCorreo.append(" el día <b>").append(Constantes.FMT_TextDate.format(viajeVO.getFechaSalida())).append("</b> a las <b>").append(viajeVO.getHoraSalida());
            }
            cuerpoCorreo.append("</b> horas y arribó  <b>").append(llego).append("</b>");
            cuerpoCorreo.append(" <b>hoy</b> a las <b>").append(siManejoFechaLocal.convertirHoraStringHHmmss(new Date())).append("</b> horas");
            //
            if (viajeVO != null
                    && viajeVO.getFechaSalida() != null && viajeVO.getHoraSalida() != null) {
                cuerpoCorreo.append(", con una duración de <b>").append(siManejoFechaLocal.horasEntreFechas(
                        siManejoFechaLocal.componerFecha(viajeVO.getFechaSalida(), viajeVO.getHoraSalida()
                        ), new Date())).append("</b>.</p>");
            } else {
                cuerpoCorreo.append(".</p>");
            }
            datosViaje(sgViajeRemote.find(viajeVO.getId()));
            this.cuerpoCorreo.append("</br>");

//
            if (viajeVO.isVehiculoEmpresa()) {
                agregarConductorResponsable(viajeVO.getResponsable(), true, false, "100%", viajeVO.getResponsableTel());
            } else {
                agregarConductorResponsable(viajeVO.getResponsable(), false, false, "100%", viajeVO.getResponsableTel());
            }
            //
            if (viajeVO.isVehiculoEmpresa()) {
                SgViajeVehiculo sgVehiculo = sgViajeVehiculoRemote.getVehicleByTravel(viajeVO.getId());
                datosVehiculo(sgVehiculo.getSgVehiculo());
            }

            if (listaViajeros != null && !listaViajeros.isEmpty()) {
                listaViajeros(listaViajeros);
            }
            this.cuerpoCorreo.append(plantilla.getFin());
        } catch (Exception ex) {
            Logger.getLogger(HtmlNotificacionViajeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.cuerpoCorreo;
    }

    private int obtenerTipoViaje(SgViaje sgViaje) {
        if (sgViaje.getSgViajeCiudad() != null) {
            return Constantes.RUTA_TIPO_CIUDAD;
        } else if (sgViaje.getSgViajeCiudad() != null) {
            return Constantes.RUTA_TIPO_LUGAR;
        } else {
            return Constantes.RUTA_TIPO_OFICINA;
        }
    }

    private int obtenerTipoViaje(ViajeVO viajeVO) {
        if (viajeVO.getIdSgViajeCiudad() > 0) {
            return Constantes.RUTA_TIPO_CIUDAD;
        } else {
            return Constantes.RUTA_TIPO_OFICINA;
        }
    }

    private String obtenerDestino(int idRuta, int idTipoRuta) {
        String destino = "";
        if (idTipoRuta == Constantes.RUTA_TIPO_OFICINA) {
            List<SgDetalleRutaTerrestreVo> det = sgDetalleRutaTerrestreRemote.getAllSgDetalleRutaTerrestreBySgRutaTerrestre(idRuta, "id", true, false);
            for (SgDetalleRutaTerrestreVo terrestre : det) {
                if (terrestre.isDestino()) {
                    destino = terrestre.getNombreSgOficina();
                }
            }
        } else if (idTipoRuta == Constantes.RUTA_TIPO_CIUDAD) {
            List<SgDetalleRutaTerrestreVo> det = sgDetalleRutaCiudadRemote.traerDetalleRutaPorRuta(idRuta, Constantes.BOOLEAN_FALSE);
            for (SgDetalleRutaTerrestreVo terrestre : det) {
                if (terrestre.isDestino()) {
                    destino = terrestre.getCiudad();
                }
            }
        } else if (idTipoRuta == Constantes.RUTA_TIPO_LUGAR) {
            List<SgDetalleRutaTerrestreVo> det = sgDetalleRutaLugarRemote.traerDetalleRutaLugarPorRuta(idRuta);
            for (SgDetalleRutaTerrestreVo terrestre : det) {
                if (terrestre.isDestino()) {
                    destino = terrestre.getNombreLugar();
                }
            }
        }
        return destino;
    }

    
    public StringBuilder mensajeSalidaPunto(ViajeVO viajeVO, List<ViajeroVO> lv, String punto, String destino) {
        try {
            limpiarCuerpoCorreo();
            SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
            this.cuerpoCorreo.append(plantilla.getInicio());
            this.cuerpoCorreo.append(this.getTitulo("Viaje -- ".concat(viajeVO.getCodigo())));
            this.cuerpoCorreo.append("<br/>");
            cuerpoCorreo.append("<p> El viaje <b>").append(viajeVO.getCodigo()).append("</b>, esta partiendo de <b>").append(punto);
            cuerpoCorreo.append("</b>");
            cuerpoCorreo.append(" con dirección a <b>").append(destino).append("</b>.");
            //
            if (viajeVO.isVehiculoEmpresa()) {
                agregarConductorResponsable(viajeVO.getResponsable(), true, false, "100%", viajeVO.getResponsableTel());
                //
                SgViajeVehiculo sgVehiculo = sgViajeVehiculoRemote.getVehicleByTravel(viajeVO.getId());
                datosVehiculo(sgVehiculo.getSgVehiculo());
            } else {
                agregarConductorResponsable(viajeVO.getResponsable(), false, false, "100%", viajeVO.getResponsableTel());
            }
            //;
            if (lv != null && !lv.isEmpty()) {
                listaViajeros(lv);
            }
            this.cuerpoCorreo.append(plantilla.getFin());
        } catch (Exception ex) {
            Logger.getLogger(HtmlNotificacionViajeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.cuerpoCorreo;
    }

    
    public StringBuilder mensajeSalidaPunto(String asunto, String punto, ViajeVO viajeA, List<ViajeroVO> viajerosViajeB, ViajeVO viajeB, List<ViajeroVO> viajerosViajeA) {
        try {
            limpiarCuerpoCorreo();
            SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
            this.cuerpoCorreo.append(plantilla.getInicio());
            this.cuerpoCorreo.append(this.getTitulo(asunto));
            this.cuerpoCorreo.append("<br/>");
            cuerpoCorreo.append("<p> En el punto de seguridad <b>").append(punto).append("</b>");
            cuerpoCorreo.append(" los viajeros del viaje <b>").append(viajeA.getCodigo()).append("</b>");
            if (viajerosViajeA != null && !viajerosViajeA.isEmpty()) {
                listaViajeros(viajerosViajeA);
            }
            cuerpoCorreo.append(" se han pasado al viaje<b>").append(viajeB.getCodigo()).append("</b> y viceversa.</p>");
            if (viajerosViajeB != null && !viajerosViajeB.isEmpty()) {
                listaViajeros(viajerosViajeB);
            }
            this.cuerpoCorreo.append(plantilla.getFin());
        } catch (Exception ex) {
            Logger.getLogger(HtmlNotificacionViajeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.cuerpoCorreo;
    }

    
    public StringBuilder mensajeDirecto(String titulo, String mensaje, String longitud, String latitud) {
        try {
            limpiarCuerpoCorreo();
            SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
            this.cuerpoCorreo.append(plantilla.getInicio());
            this.cuerpoCorreo.append(this.getTitulo(titulo));
            this.cuerpoCorreo.append("<br/>");
            cuerpoCorreo.append("<p><b>").append(mensaje).append(".</b></p>");
            if (!longitud.isEmpty()) {
                cuerpoCorreo.append("<p> El mensaje se envío desde la ubicación, ").append(longitud).append(", ").append(latitud).append(".</p>");
            }

            this.cuerpoCorreo.append(plantilla.getFin());
        } catch (Exception ex) {
            Logger.getLogger(HtmlNotificacionViajeImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.cuerpoCorreo;
    }

    public StringBuilder bodyMailNotificationByGR(List<SolicitudViajeVO> listaViaje, String fechaSalida) {
        UtilLog4j.log.info(this, "bodyMailNotificationByGR");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Reporte de solicitudes de viajes no aprobadas - " + fechaSalida));

        this.cuerpoCorreo.append("<br/><p>Se envía el reporte de solicitudes de viajes no aprobadas <b> ").append(fechaSalida).append("</b>");
        this.cuerpoCorreo.append("<br/>");
        //Datos del viaje
        datosSolicitudes(listaViaje, true, "Solicitudes sin aprobar", Constantes.TRUE);
        //Pie del correo
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    private void datosSolicitudes(List<SolicitudViajeVO> lsv, boolean conViajeros, String tituloTabla, boolean gr) {

        this.cuerpoCorreo.append("<br/><table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\" >");
        this.cuerpoCorreo.append("<thead><tr><th colspan=\"8\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman\', Times, serif; font-weight:bold; color:#FFFFFF; font-size:16px; background-color:#0099FF;\">" + tituloTabla + "</th></tr>");
        this.cuerpoCorreo.append("<tr><th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Código </th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Ruta </th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Gerencia </th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Gerente Responsable</th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Fecha salida</th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Hora salida</th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Viajeros</th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Estatus</th>");
        this.cuerpoCorreo.append("</tr>");
        String color = "";
        for (SolicitudViajeVO solicitudViajeVO : lsv) {

            if (solicitudViajeVO.isConChofer() && !gr) {
                color = "style=\"background-color: #D6EAF8;\"";
            } else {
                color = "";
            }
            Usuario u = getResponsableByGerencia(solicitudViajeVO.getIdGerencia());
            this.cuerpoCorreo.append("<tr " + color + "> <td  style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(solicitudViajeVO.getCodigo()).append("</td>");
            this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">")
                    .append((solicitudViajeVO.getOrigen() != null && solicitudViajeVO.getDestino() != null) ? solicitudViajeVO.getOrigen() + " a " + solicitudViajeVO.getDestino() : '-')
                    .append("</td>");
            this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(solicitudViajeVO.getGerencia() != null ? solicitudViajeVO.getGerencia() : '-').append("</td>");
            this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append((u != null ? u.getNombre() : '-')).append("</td>");
            this.cuerpoCorreo.append("<td height=\"18\"  style=\"border: 1px solid #b5b5b5;\">".concat(Constantes.FMT_ddMMyyy.format(solicitudViajeVO.getFechaSalida())).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(Constantes.FMT_HHmmss.format(solicitudViajeVO.getHoraSalida())).append("</td>");
            this.cuerpoCorreo.append("<td style=\"border:1px solid #b5b5b5;\" align=\"center\">").append("<table><tbody>");
            int i = 1;
            for (ViajeroVO via : solicitudViajeVO.getViajeros()) {
                this.cuerpoCorreo.append("<tr><td>").append(via.getIdInvitado() < 1 ? i + ".-" + via.getUsuario() : i + ".-" + via.getInvitado()).append("</td></tr>");
                i++;
            }
            this.cuerpoCorreo.append("</tbody></table>");
            this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(solicitudViajeVO.getEstatus()).append("</td>");
            this.cuerpoCorreo.append("</tr>");
        }
        cuerpoCorreo.append("</table><br/>");

    }

    private void datosViajes(List<ViajeVO> lv) {

        this.cuerpoCorreo.append("<br/><table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\" >");
        this.cuerpoCorreo.append("<thead><tr><th colspan=\"8\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman\', Times, serif; font-weight:bold; color:#FFFFFF; "
                + "font-size:16px; background-color:#0099FF;\">Viajes programados <b>" + lv.get(0).getOrigen() + "</b></th></tr>");
        this.cuerpoCorreo.append("<tr><th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Código </th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Ruta </th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Conductor</th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Telefono</th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Fecha programada</th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Hora programada</th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Viajeros</th>");
        this.cuerpoCorreo.append("<th style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"center\">Se intercepta</th>");
        this.cuerpoCorreo.append("</tr>");
        String color = "";
        for (ViajeVO via : lv) {
            if (via.isConChofer()) {
                color = "style=\"background-color: #D6EAF8;\"";
            } else {
                color = "";
            }
            // Usuario u = getResponsableByGerencia(solicitudViajeVO.getIdGerencia());
            this.cuerpoCorreo.append("<tr " + color + "> <td  style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(via.getCodigo()).append("</td>");
            this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">")
                    .append(via.getRuta() != null ? via.getRuta() : '-')
                    .append("</td>");
            this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(via.getResponsable()).append("</td>");
            this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append((via.getResponsableTel() != null ? via.getResponsableTel() : '-')).append("</td>");
            this.cuerpoCorreo.append("<td height=\"18\"  style=\"border: 1px solid #b5b5b5;\">".concat(Constantes.FMT_ddMMyyy.format(via.getFechaProgramada())).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(Constantes.FMT_HHmmss.format(via.getHoraProgramada())).append("</td>");
            this.cuerpoCorreo.append("<td style=\"border:1px solid #b5b5b5;\" align=\"center\">").append("<table><tbody>");
            int i = 1;
            for (ViajeroVO viajero : via.getListaViajeros()) {
                this.cuerpoCorreo.append("<tr><td>").append(viajero.getUsuario() != null ? i + ".-" + viajero.getUsuario() : i + ".-" + viajero.getInvitado()).append("</td></tr>");
                i++;
            }
            this.cuerpoCorreo.append("</tbody></table>");
            this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(via.isConInter() ? "si" : "no").append("</td>");
            this.cuerpoCorreo.append("</tr>");
        }
        cuerpoCorreo.append("</table><br/>");

    }

    
    public StringBuilder bodyMailNotificatioRespnsableSGL(List<List<ViajeVO>> listaViajes, String fechaSalida, List<SolicitudViajeVO> listSol, List<SolicitudViajeVO> listSolSA) {
        UtilLog4j.log.info(this, "bodyMailNotificatioRespnsableSGL");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Reporte de viajes programados - " + fechaSalida));

        this.cuerpoCorreo.append("<br/><p>Se envía el reporte de los viajes programados para mañana. <br/>");
        this.cuerpoCorreo.append("<br/><p>Las solicitudes y los viajes marcador con color azul son con conductores del departamento de Servicios Generales.<br/>");

        int i = 0;
        //Datos del viaje
        for (List<ViajeVO> list : listaViajes) {
            if (list.size() > 0) {
                datosViajes(list);
                i++;
            }

        }
        this.cuerpoCorreo.append("<br/>");
        if (listSol.size() > 0) {
            datosSolicitudes(listSol, true, "Solicitudes del " + fechaSalida, Constantes.FALSE);
            i++;
        }
        if (listSol.size() > 0) {
            datosSolicitudes(listSolSA, true, "Solicitudes sin aprobar", Constantes.FALSE);
            i++;
        }
        if (i == 0) {
            this.cuerpoCorreo.append("<br/><p>No se encontraron viajes ni solicitudes para mañana<br/>");
        }
        //Pie del correo
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    private void detallesEstanciaAerea(String hotel, String direccion, String ubicacion) {
        this.cuerpoCorreo.append("<br/><table width=\"100%\" align=\"center\" cellspacing=\"0\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Datos de Estancia</th></tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<th width=\"35%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Zona</th>");
        this.cuerpoCorreo.append("<th width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Direccion a visitar</th>");
        this.cuerpoCorreo.append("<th width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Hotel Sugerido</th>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td ").append(" width=\"35%\" align=\"center\" style=\"border: 1px solid #b5b5b5;").append("\">");
        cuerpoCorreo.append(ubicacion).append("</td>");
        this.cuerpoCorreo.append("<td ").append(" width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5;").append("\">");
        this.cuerpoCorreo.append(direccion).append("</td>");
        this.cuerpoCorreo.append("<td ").append(" width=\"10%\" align=\"center\" style=\"border: 1px solid #b5b5b5;").append("\">");
        cuerpoCorreo.append(hotel).append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table><br/>");
    }
    
    
    public StringBuilder getHtmlSolicitudActivar(SolicitudViajeVO estatus, String motivoCancelacion, Usuario usuarioRealizo, Date fechaModifico, Date horaModifico, String responsable) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
//        SgMotivoRetraso sgMotivoRetraso = sgMotivoRetrasoRemote.findBySolicitud(estatus.getSgSolicitudViaje().getId());

        try {
            this.limpiarCuerpoCorreo();
            this.cuerpoCorreo.append(plantilla.getInicio());
            this.cuerpoCorreo.append(this.getTitulo("Se ha activado la solicitud de viaje : ".concat(estatus.getCodigo() != null ? estatus.getCodigo() : "Sin Código").concat("")));
            this.cuerpoCorreo.append("<br/>");
            //motivo de cancelacion
            mostrarMotivoCancelacionSolicitudViaje(usuarioRealizo.getNombre(), motivoCancelacion, fechaModifico, horaModifico, "Activo");
            this.cuerpoCorreo.append("<br/>");
            //encabezados
            mostrarEncabezadoSolicitudViaje(estatus.getTipoSolicitud(), estatus.getTipoEspecifico(), responsable, estatus.getGerencia());
            this.cuerpoCorreo.append("<br/>");
            log("-------Encabezados agregados");
            //detalle
            mostrarDetalleSolicitudViajeTerrestre(estatus);
            log("-------detalle agregado");
            //viajeros
            listaViajeros(sgViajeroRemote.getAllViajerosList(estatus.getIdSolicitud()));
            log("-------Viajeros agregados");
            //observaciones
            if (estatus.getIdSgTipoEspecifico() == Constantes.SOLICITUDES_TERRESTRE) {
                mostrarObservacionSolicitudViaje(estatus.getMotivo(), estatus.getObservacion());
            }

            if (estatus.getIdMotivoRetraso() != 0) {
                this.cuerpoCorreo.append("<br/>");
                mostrarJustificacionRetrasoSolicitudViaje(estatus.getMotivoRetrasoVo());
            }
            this.cuerpoCorreo.append("<br/>");
//            mostrarHistorialAprobacionesSolicitud(estatus.getIdSolicitud());

            this.cuerpoCorreo.append(plantilla.getFin());

            return this.cuerpoCorreo;
        } catch (SIAException ex) {
            Logger.getLogger(HtmlNotificacionViajeImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
