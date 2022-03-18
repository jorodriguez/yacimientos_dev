/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.sgl.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.correo.impl.CodigoHtml;
import sia.modelo.CoComentario;
import sia.modelo.CoNoticia;
import sia.modelo.Convenio;
import sia.modelo.SgAvisoPago;
import sia.modelo.SgDetalleSolicitudEstancia;
import sia.modelo.SgHotel;
import sia.modelo.SgHuespedHotel;
import sia.modelo.SgHuespedStaff;
import sia.modelo.SgOficina;
import sia.modelo.SgPagoServicioVehiculo;
import sia.modelo.SgStaff;
import sia.modelo.SgStaffHabitacion;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgVehiculo;
import sia.modelo.SiPlantillaHtml;
import sia.modelo.Usuario;
import sia.modelo.cursoManejo.vo.CursoManejoVo;
import sia.modelo.gr.vo.GrRutaZonasVO;
import sia.modelo.licencia.vo.LicenciaVo;
import sia.modelo.sgl.estancia.vo.DetalleEstanciaVO;
import sia.modelo.sgl.estancia.vo.HuespedVo;
import sia.modelo.sgl.estancia.vo.SgSolicitudEstanciaVo;
import sia.modelo.sgl.semaforo.vo.EstadoSemaforoCambioVO;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;
import sia.modelo.sgl.semaforo.vo.SgEstadoSemaforoVO;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.sgl.impl.SgAvisoPagoImpl;
import sia.servicios.sgl.impl.SgDetalleSolicitudEstanciaImpl;
import sia.servicios.sgl.impl.SgHotelImpl;
import sia.servicios.sgl.impl.SgHuespedStaffImpl;
import sia.servicios.sgl.impl.SgStaffImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiPlantillaHtmlImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Stateless 
public class HtmlNotificacionServiciosGeneralesImpl extends CodigoHtml {

    @Inject
    private SiPlantillaHtmlImpl plantillaHtml;
    @Inject
    private SgHotelImpl sgHotelRemote;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    @Inject
    private SgDetalleSolicitudEstanciaImpl sgDetalleSolicitudEstanciaRemote;
    @Inject
    private SgStaffImpl staffService;
    @Inject
    private SgAvisoPagoImpl avisoPagoRemoteService;
    @Inject
    private SgHuespedStaffImpl sgHuespedStaffService;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private SiManejoFechaImpl  siManejoFechaLocal;

    private Usuario getResponsableByGerencia(int idGerencia) {
        return this.gerenciaRemote.getResponsableByApCampoAndGerencia(1, idGerencia, false);
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StringBuilder getHtmlSolicitaEstancia(int idGerencia, String codigo, Date inicio, Date fin, int dias,
            String motivo, String gerencia, List<DetalleEstanciaVO> listaDetalle, String oficina, String observacion) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = Constantes.FMT_ddMMyyy;
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Solicitud de Estancia ".concat(codigo)));
        //this.cuerpoCorreo.append("<br/><p>Estimado <b> ".concat(nombre).concat("</b>"));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<p>Ha recibido una Solicitud de Estancia para la sede de <b>".concat(oficina).concat("</b> de parte de la gerencia de <b>").concat(gerencia).concat(".</b> </p>"));
        this.cuerpoCorreo.append("<p>Por favor, proceder con la asignación de staff house u hotel a el/los empleado(s)/invitado(s) solicitado(s)</p>");
        this.cuerpoCorreo.append("<table bordercolor=\"#000000\" cellspacing=\"8\" border=\"0\"  align=\"center\" width=\"80%\">"
                + "<tr><td valign=\"top\">"
                + "<table width=\"100%\"  align=\"center\" cellspacing=\"0\" cellpadding=\"1\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"6\"   bgcolor=\"#0099FF\">Datos de la solicitud</th></tr>");
        this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5;\" width=\"20%\" align=\"center\"><strong>Solicita</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"20%\" align=\"center\"><strong>Inicio</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"20%\" align=\"center\"><strong>Fin</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"10%\" align=\"center\"><strong>Días</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"50%\" align=\"center\"><strong>Motivo</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"50%\" align=\"center\"><strong>Observación</strong></td>");
        this.cuerpoCorreo.append("</tr><tr>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">").append(gerencia).append("</td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat(sdf.format(inicio)).concat("</td>"));
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat(sdf.format(fin)).concat("</td>"));
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat("" + dias).concat("</td>"));
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat("" + motivo).concat("</td>"));
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat("" + observacion).concat("</td></tr> </table>"));
        this.cuerpoCorreo.append("</td></tr>");
        this.cuerpoCorreo.append("<tr><td valign=\"top\"> </td><tr>");
        this.cuerpoCorreo.append("<tr><td valign=\"top\"></td><tr>");
        this.cuerpoCorreo.append("<tr><td valign=\"top\">"
                + "<table width=\"100%\" align=\"center\" cellspacing=\"0\" cellpadding=\"1\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"3\" bgcolor=\"#0099FF\">Empleado(s)/Invitado(s)</th></tr>");
        this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5;\" width=\"35%\" align=\"left\"><strong>Nombre</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"20%\" align=\"left\"><strong>Tipo</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"45%\" align=\"left\"><strong>Descripción</strong></td></tr>");
        UtilLog4j.log.info(this, "Agregar detalles");
        for (DetalleEstanciaVO sgDetalleSolicitudEstancia : listaDetalle) {
            if (sgDetalleSolicitudEstancia.getIdInvitado() == 0) {
                this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5;\" width=\"35%\">".concat(sgDetalleSolicitudEstancia.getUsuario()).concat("</td>"));
                this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"20%\">".concat(sgDetalleSolicitudEstancia.getTipoDetalle()).concat("</td>"));
                this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"45%\">".concat(sgDetalleSolicitudEstancia.getDescripcion() != null ? sgDetalleSolicitudEstancia.getDescripcion() : "-").concat("</td></tr>"));
            } else {
                this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5;\" width=\"35%\">".concat(sgDetalleSolicitudEstancia.getInvitado()).concat("</td>"));
                this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"20%\">".concat(sgDetalleSolicitudEstancia.getTipoDetalle()).concat("</td>"));
                this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"45%\">".concat(sgDetalleSolicitudEstancia.getDescripcion() != null ? sgDetalleSolicitudEstancia.getDescripcion() : "-").concat("</td></tr>"));
            }
        }
        this.cuerpoCorreo.append("</td></tr></table></td></tr></table>");
        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StringBuilder getHtmlSolicitaEstanciaParaGerencia(SgSolicitudEstanciaVo sgSolicitudEstancia, List<DetalleEstanciaVO> listaDetalle, String oficina) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        SimpleDateFormat sdf = Constantes.FMT_ddMMyyy;
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Solicitud de Estancia ".concat(sgSolicitudEstancia.getCodigo())));
        this.cuerpoCorreo.append("<br/><p>Estimado <b> ".concat(sgSolicitudEstancia.getNombreGenero()).concat("</b>"));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<p>Se envió la Solicitud de Estancia para la sede de <b>".concat(oficina).concat("</b> al departamento de Servicios Generales.</p>"));
        this.cuerpoCorreo.append("<table bordercolor=\"#000000\" cellspacing=\"8\" border=\"0\"  align=\"center\" width=\"80%\">"
                + "<tr><td valign=\"top\">"
                + "<table width=\"100%\"  align=\"center\" cellspacing=\"0\" cellpadding=\"1\"> ");
        this.cuerpoCorreo.append("<tr><th colspan=\"4\"   bgcolor=\"#0099FF\">Datos de la solicitud</th></tr>");
        this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5;\" width=\"20%\" align=\"center\"><strong>Inicio</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"20%\" align=\"center\"><strong>Fin</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"10%\" align=\"center\"><strong>Días</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"50%\" align=\"center\"><strong>Motivo</strong></td>");
        this.cuerpoCorreo.append("</tr><tr>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat(sdf.format(sgSolicitudEstancia.getInicioEstancia())).concat("</td>"));
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat(sdf.format(sgSolicitudEstancia.getFinEstancia())).concat("</td>"));
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat("" + sgSolicitudEstancia.getDiasEstancia()).concat("</td>"));
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat("" + sgSolicitudEstancia.getNombreSgMotivo()).concat("</td></tr> </table>"));
        this.cuerpoCorreo.append("</td></tr>");
        this.cuerpoCorreo.append("<tr><td valign=\"top\"> </td><tr>");
        this.cuerpoCorreo.append("<tr><td valign=\"top\"> </td><tr>");
        this.cuerpoCorreo.append("<tr><td valign=\"top\">"
                + "<table width=\"100%\" align=\"center\" cellspacing=\"0\" cellpadding=\"1\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"3\" bgcolor=\"#0099FF\">Empleado(s)/Invitado(s)</th></tr>");
        this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5;\" width=\"35%\" align=\"left\"><strong>Nombre</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"10%\" align=\"left\"><strong>Tipo</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"45%\" align=\"left\"><strong>Descripción</strong></td></tr>");
        for (DetalleEstanciaVO sgDetalleSolicitudEstancia : listaDetalle) {
            if (sgDetalleSolicitudEstancia.getIdInvitado() == 0) {
                this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5;\" width=\"35%\">".concat(sgDetalleSolicitudEstancia.getUsuario()).concat("</td>"));
                this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"20%\">".concat(sgDetalleSolicitudEstancia.getTipoDetalle()).concat("</td>"));
                this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"45%\">".concat(sgDetalleSolicitudEstancia.getDescripcion() != null ? sgDetalleSolicitudEstancia.getDescripcion() : "-").concat("</td></tr>"));
            } else {
                this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5;\" width=\"35%\">".concat(sgDetalleSolicitudEstancia.getInvitado()).concat("</td>"));
                this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"20%\">".concat(sgDetalleSolicitudEstancia.getTipoDetalle()).concat("</td>"));
                this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"45%\">".concat(sgDetalleSolicitudEstancia.getDescripcion() != null ? sgDetalleSolicitudEstancia.getDescripcion() : "-").concat("</td></tr>"));
            }
        }
        this.cuerpoCorreo.append("</td></tr></table></td></tr></table>");
        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlAvisoVencimientoConvenioOficina(SgOficina oficina, Convenio convenio, String nombreUsuario, int numeroDias) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Aviso de vencimiento de Convenio de Oficina"));
        this.cuerpoCorreo.append("<br/><p>Estimado <b> ".concat(nombreUsuario).concat("</b>"));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<p>El Convenio con nombre: <b>".concat(convenio.getNombre()).concat("</b></p>"));
        this.cuerpoCorreo.append("<p>con Código: <b>".concat(convenio.getCodigo()).concat("</b></p>"));
        this.cuerpoCorreo.append("asignado al Staff: <b>".concat(oficina.getNombre()).concat("</b></p>"));
        this.cuerpoCorreo.append("vencerá en ".concat(String.valueOf(numeroDias)).concat(" días, el: <b>").concat(sdf.format(convenio.getFechaVencimiento())).concat("</b></p>"));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<p>Favor de tomar las medidas necesarias</p>");

        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlAvisoVencimientoConvenioStaff(SgStaff staff, Convenio convenio, String nombreUsuario, int numeroDias) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Aviso de vencimiento de Convenio de Staff"));
        this.cuerpoCorreo.append("<br/><p>Estimado <b> ".concat(nombreUsuario).concat("</b>"));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<p>El Convenio con nombre: <b>".concat(convenio.getNombre()).concat("</b></p>"));
        this.cuerpoCorreo.append("<p>con Código: <b>".concat(convenio.getCodigo()).concat("</b></p>"));
        this.cuerpoCorreo.append("asignado al Staff: <b>".concat(staff.getNombre()).concat("</b></p>"));
        this.cuerpoCorreo.append("vencerá en ".concat(String.valueOf(numeroDias)).concat(" días, el: <b>").concat(sdf.format(convenio.getFechaVencimiento())).concat("</b></p>"));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<p>Favor de tomar las medidas necesarias</p>");

        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlRegistroHuespedHotel(SgHuespedHotel sgHuespedHotel, String estancia, int idDetalleEstancia, int idInvitado, String invitado, String empleado, String tipoEspecifico, String correoEmpleado, SgSolicitudEstanciaVo sgSolicitudEstancia, int idHotel, int idTipoEspecifico) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SgTipoEspecifico sgTipoEspecifico = sgTipoEspecificoRemote.find(idTipoEspecifico);
        SgHotel sgHotel = sgHotelRemote.find(idHotel);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Registro de huésped en Hotel - " + sgSolicitudEstancia.getCodigo()));
        this.cuerpoCorreo.append("<p>Estimado <b>".concat(getResponsableByGerencia(sgSolicitudEstancia.getIdGerencia()).getNombre()).concat("</b></p>"));
        this.cuerpoCorreo.append("<p>Se ha registrado el huésped<b> ".concat(idInvitado == 0 ? empleado : invitado).concat(", </b> de la solicitud de estancia <b>".concat(sgSolicitudEstancia.getCodigo() + "</b></p>")));
        this.cuerpoCorreo.append("<p>Datos del registro</p>");
        this.cuerpoCorreo.append("<table bordercolor=\"#000000\"  border=\"0\"  align=\"center\" width=\"695\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\"><table  border=\"0\" align=\"center\"  bordercolor=\"#000000\" width=\"100%\">");
        this.cuerpoCorreo.append("<tr><th style=\"").append(getEstiloTitulo()).append("\" align=\"center\" colspan=\"2\" bgcolor=\"#0099FF\"><b>Datos de la solicitud</b></th></tr>");
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Solicitud</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(sgSolicitudEstancia.getCodigo()).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Inicio</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(sdf.format(sgSolicitudEstancia.getInicioEstancia())).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Fin</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(sdf.format(sgSolicitudEstancia.getFinEstancia())).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Tipo</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(tipoEspecifico).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Motivo</td><td style=\"").append(getEstiloContenido()).append("\"  >".concat(validarNullHtml(sgSolicitudEstancia.getNombreSgMotivo())).concat("</td></tr>"));
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("</td><td>");
        this.cuerpoCorreo.append("<table  border=\"0\" align=\"center\" width=\"100%\" bordercolor=\"#000000\"><tr><th colspan=\"2\" align=\"center\" bgcolor=\"#0099FF\"><b>Datos del registro hotel </b></th></tr>");
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Hotel</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(sgHotel.getProveedor().getNombre()).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Ciudad</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(sgHotel.getProveedor().getCiudad()).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Colonia</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(sgHotel.getProveedor().getColonia()).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Calle</td><td style=\"").append(getEstiloContenido()).append("\"  >".concat(sgHotel.getProveedor().getCalle()).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Número</td><td style=\"").append(getEstiloContenido()).append("\"  >".concat(sgHotel.getProveedor().getNumero()).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Reservación</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(sgHuespedHotel.getNumeroHabitacion()).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Huesped</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(idInvitado == 0 ? empleado : invitado).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Ingreso</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(sdf.format(sgHuespedHotel.getFechaIngreso())).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Fecha Salida</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(sgHuespedHotel.getFechaSalida() != null ? sdf.format(sgHuespedHotel.getFechaSalida()) : "Indefinido").concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"right\" >Tipo</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(sgTipoEspecifico.getNombre()).concat("</td></tr>"));
        this.cuerpoCorreo.append("</table></td></tr>");
        this.cuerpoCorreo.append("<tr><td colspan=\"2\">Favor de pasar al departamento de Servicios Generales y Logística para recoger la carta de asignación");
        this.cuerpoCorreo.append("</td></tr></table>");

        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;

    }

    
    public StringBuilder getHtmlRegistroHuespedStaff(int idInvitado, String invitado, String empleado, String tipoEspecifico, SgSolicitudEstanciaVo solicitudEstancia, SgStaffHabitacion habitacion, SgTipoEspecifico tipoHuesped, Date fechaIngreso, Date fechaSalida) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Registro de huésped en Staff House - " + solicitudEstancia.getCodigo()));
        this.cuerpoCorreo.append("<p>Estimado <b>".concat(getResponsableByGerencia(solicitudEstancia.getIdGerencia()).getNombre()).concat("</b></p>"));
        this.cuerpoCorreo.append("<p>Se ha registrado el huésped <b>".concat(idInvitado == 0 ? empleado : invitado).concat(", </b> de la solicitud de estancia <b>".concat(solicitudEstancia.getCodigo() + "</b></p>")));
        this.cuerpoCorreo.append("<p>Datos del registro</p>");
        this.cuerpoCorreo.append("<table bordercolor=\"#000000\" align=\"center\" width=\"695\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\"><table  border=\"0\" align=\"center\"  bordercolor=\"#000000\" width=\"100%\">");
        this.cuerpoCorreo.append("<tr><th align=\"center\" colspan=\"2\" bgcolor=\"#0099FF\"><b>Datos de la solicitud</b></th></tr>");
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"center\" width=\"69\">Solicitud</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(solicitudEstancia.getCodigo().toString()).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"center\" width=\"69\">Inicio</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(sdf.format(solicitudEstancia.getInicioEstancia())).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"center\" width=\"69\">Fin</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(sdf.format(solicitudEstancia.getFinEstancia())).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"center\" width=\"69\">Tipo</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(tipoEspecifico).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"center\" width=\"69\">Motivo</td><td style=\"").append(getEstiloContenido()).append("\" >".concat("" + solicitudEstancia.getNombreSgMotivo()).concat("</td></tr>"));
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("</td><td>");
        this.cuerpoCorreo.append("<table  border=\"0\" align=\"center\" width=\"100%\" bordercolor=\"#000000\"><tr><th align=\"center\" colspan=\"2\" bgcolor=\"#0099FF\"><b>Datos del registro </b></th></tr>");
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"center\" width=\"69\">Staff House</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(habitacion.getSgStaff().getNombre()).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"center\" width=\"69\"># Staff House</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(habitacion.getSgStaff().getNumeroStaff()).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"center\" width=\"69\">Habitación</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(habitacion.getNombre()).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"center\" width=\"69\"># Habitación</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(habitacion.getNumeroHabitacion()).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"center\" width=\"69\">Fecha Ingreso</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(sdf.format(fechaIngreso)).concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"center\" width=\"69\">Fecha Salida</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(fechaSalida != null ? sdf.format(fechaSalida) : "Indefinido").concat("</td></tr>"));
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" width=\"69\">Tipo</td><td style=\"").append(getEstiloContenido()).append("\" >".concat(tipoHuesped.getNombre()).concat("</td></tr>"));
        this.cuerpoCorreo.append("</table></td></tr>");
        this.cuerpoCorreo.append("</td></table>");

        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlCancelaSolicitudEstancia(SgSolicitudEstanciaVo sgSolicitudEstancia, String mensaje, boolean notificar) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        List<DetalleEstanciaVO> listaDetalle = sgDetalleSolicitudEstanciaRemote.traerDetallePorSolicitud(sgSolicitudEstancia.getId(), Constantes.NO_ELIMINADO);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Solicitud de Estancia - ".concat(sgSolicitudEstancia.getCodigo() != null ? sgSolicitudEstancia.getCodigo() : "Sin código").concat(" - Cancelada")));
        this.cuerpoCorreo.append("<br/><p>Estimado <b> ").append(getResponsableByGerencia(sgSolicitudEstancia.getIdGerencia()).getNombre()).append("</b>");
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<p>Se canceló la Solicitud de Estancia ").append(sgSolicitudEstancia.getCodigo() != null);
        this.cuerpoCorreo.append("<b><br/>Motivo: </b>").append(mensaje).append("</p>");
        this.cuerpoCorreo.append("<table bordercolor=\"#000000\"  align=\"center\" width=\"80%\">").append("<tr><td valign=\"top\">").append("<table width=\"100%\"  align=\"center\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"3\"  align=\"center\" bgcolor=\"#990000\"><font color=\"#FFFFFF\" ><b>Datos de la solicitud</b></font></th></tr>");
        this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Inicio</strong></td>");
        this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Fin</strong></td>");
        this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Motivo</strong></td>");
        this.cuerpoCorreo.append("</tr><tr>");
        this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" align=\"center\">").append(sgSolicitudEstancia.getInicioEstancia() != null ? sdf.format(sgSolicitudEstancia.getInicioEstancia()) : "--").append("</td>");
        this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" align=\"center\" >").append(sgSolicitudEstancia.getFinEstancia() != null ? sdf.format(sgSolicitudEstancia.getFinEstancia()) : "..").append("</td>");
        this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" align=\"center\">").append(sgSolicitudEstancia.getNombreSgMotivo()).append("</td></tr> </table>");
        this.cuerpoCorreo.append("</td></tr>");
        this.cuerpoCorreo.append("<tr><td valign=\"top\"> </td></tr>");
        this.cuerpoCorreo.append("<tr><td valign=\"top\"> </td></tr>");

        this.cuerpoCorreo.append("<tr><td valign=\"top\">");
        if (notificar) {
            this.cuerpoCorreo.append("<table width=\"100%\" align=\"center\">");
            this.cuerpoCorreo.append("<tr><th align=\"center\" colspan=\"2\" bgcolor=\"#990000\"><font color=\"#FFFFFF\" > <b>Detalle de la solicitud</b></font></th></tr>");
            this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Nombre</strong></td>");
            this.cuerpoCorreo.append("  <td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Tipo</strong></td></tr>");
            for (DetalleEstanciaVO sgDetalleSolicitudEstancia : listaDetalle) {
                if (sgDetalleSolicitudEstancia.getIdInvitado() == 0) {
                    this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloContenido()).append("\" align=\"center\" >").append(sgDetalleSolicitudEstancia.getUsuario()).append("</td>");
                    this.cuerpoCorreo.append("<td  style=\"").append(getEstiloContenido()).append("\" align=\"center\">").append((sgDetalleSolicitudEstancia.getTipoDetalle())).append("</td></tr>");
                } else if (sgDetalleSolicitudEstancia.getInvitado() != null) {
                    this.cuerpoCorreo.append("<tr><td align=\"center\" style=\"").append(getEstiloContenido()).append("\" >").append(sgDetalleSolicitudEstancia.getInvitado()).append("</td>");
                    this.cuerpoCorreo.append("<td align=\"center\" style=\"").append(getEstiloContenido()).append("\" >").append(sgDetalleSolicitudEstancia.getTipoDetalle()).append("</td></tr>");
                }
            }
            this.cuerpoCorreo.append("</td></tr></table>");
        }
        this.cuerpoCorreo.append("</td></tr></table>");
        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlSalidaHuesped(SgDetalleSolicitudEstancia sgDetalleSolicitudEstancia, Object object) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String nombre = sgDetalleSolicitudEstancia.getUsuario() == null ? sgDetalleSolicitudEstancia.getSgInvitado().getNombre() : sgDetalleSolicitudEstancia.getUsuario().getNombre();
        UsuarioResponsableGerenciaVo urgv = gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.UNO, sgDetalleSolicitudEstancia.getSgSolicitudEstancia().getGerencia().getId());
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Estancia terminada - " + sgDetalleSolicitudEstancia.getSgSolicitudEstancia().getCodigo() + " - Terminada"));
        this.cuerpoCorreo.append("<br/><p>Estimado <b> ").append(getResponsableByGerencia(sgDetalleSolicitudEstancia.getSgSolicitudEstancia().getGerencia().getId().intValue()).getNombre()).append("</b>");
        this.cuerpoCorreo.append("<br/>");
        if (object instanceof SgHuespedHotel) {
            SgHuespedHotel sgHuespedHotel = (SgHuespedHotel) object;
            this.cuerpoCorreo.append("<p>El usuario <b> ").append(nombre).append("</b>, ha dejado el hotel ").append("<b>").append(sgHuespedHotel.getSgHotelHabitacion().getSgHotel().getProveedor().getNombre()).append("</b>, el día ").append(sdf.format(sgHuespedHotel.getFechaRealSalida())).append("</p>");
            this.cuerpoCorreo.append("<table  align=\"center\" width=\"100%\">").append("<tr><td valign=\"top\">").append("<table width=\"100%\"  align=\"center\">");
            this.cuerpoCorreo.append("<tr><th colspan=\"7\"   bgcolor=\"#CDE4F6\">Datos del huésped</th></tr>");
            this.cuerpoCorreo.append("<tr><td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Gerencia</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Responsable</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Huésped</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Tipo Huésped</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Inicio</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Fin</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Tipo estancia</strong></td>");
            this.cuerpoCorreo.append("</tr><tr>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" align=\"center\">".concat(urgv.getNombreGerencia()).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" align=\"center\">".concat(urgv.getNombreUsuario()).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" align=\"center\">".concat(nombre).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" align=\"center\">".concat(sgDetalleSolicitudEstancia.getSgTipoEspecifico().getNombre()).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" align=\"center\">".concat(sdf.format(sgHuespedHotel.getFechaRealIngreso())).concat("</td>"));
            this.cuerpoCorreo.append(sgHuespedHotel.getFechaRealSalida() != null ? "<td  align=\"center\">".concat(sdf.format(sgHuespedHotel.getFechaRealSalida())).concat("</td>") : "<td align=\"center\">Sin fecha</td>");
            this.cuerpoCorreo.append("<td align=\"center\">".concat(sgHuespedHotel.getSgTipoEspecifico().getNombre()).concat("</td>"));
            this.cuerpoCorreo.append("</td></tr>");
            this.cuerpoCorreo.append("</td></tr></table></td></tr></table>");
        }//Termina el envio de correo de salida huesped hotel
        if (object instanceof SgHuespedStaff) {
            SgHuespedStaff sgHuespedStaff = (SgHuespedStaff) object;
            this.cuerpoCorreo.append("<p>El usuario <b>".concat(nombre).concat("</b>, ha dejado el staff "
                    + "<b>").concat(sgHuespedStaff.getSgStaffHabitacion().getSgStaff().getNombre()).concat("</b>, ").concat("el "
                    + "día ").concat(sdf.format(sgHuespedStaff.getFechaSalida())).concat("</p>"));
            this.cuerpoCorreo.append("<table  align=\"center\" width=\"100%\">"
                    + "<tr><td valign=\"top\">"
                    + "<table width=\"100%\"  align=\"center\">");
            this.cuerpoCorreo.append("<tr><th colspan=\"7\"   bgcolor=\"#CDE4F6\">Datos del huésped</th></tr>");
            this.cuerpoCorreo.append("<tr><td width=\"13%\" align=\"center\"><strong>Gerencia</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Responsable</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Huésped</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Tipo Huésped</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Inicio</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Fin</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Tipo estancia</strong></td>");
            this.cuerpoCorreo.append("</tr><tr>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" align=\"center\">".concat(urgv.getNombreGerencia()).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" align=\"center\">".concat(urgv.getNombreUsuario()).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" align=\"center\">".concat(nombre).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" align=\"center\">".concat(sgDetalleSolicitudEstancia.getSgTipoEspecifico().getNombre()).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" align=\"center\">".concat(sdf.format(sgHuespedStaff.getFechaIngreso())).concat("</td>"));
            this.cuerpoCorreo.append(sgHuespedStaff.getFechaRealSalida() != null ? ("<td align=\"center\">".concat(sdf.format(sgHuespedStaff.getFechaRealSalida())).concat("</td>")) : ("<td align=\"center\">Sin fecha</td>"));
            this.cuerpoCorreo.append("<td align=\"center\">".concat(sgHuespedStaff.getSgTipoEspecifico().getNombre()).concat("</td>"));
            this.cuerpoCorreo.append("</td></tr>");
            this.cuerpoCorreo.append("</td></tr></table></td></tr></table>");
        }
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlCancelaHuesped(SgDetalleSolicitudEstancia sgDetalleSolicitudEstancia, Object object) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        UsuarioResponsableGerenciaVo urgv = gerenciaRemote.traerResponsablePorApCampoYGerencia(1, sgDetalleSolicitudEstancia.getSgSolicitudEstancia().getGerencia().getId().intValue());
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Registro de huésped - " + sgDetalleSolicitudEstancia.getSgSolicitudEstancia().getCodigo() + " - Cancelado"));
        this.cuerpoCorreo.append("<br/><p>Estimado, <b> ".concat(urgv.getNombreUsuario()).concat("</b></p>"));
        this.cuerpoCorreo.append("<br/>");
        if (object instanceof SgHuespedHotel) {
            SgHuespedHotel sgHuespedHotel = (SgHuespedHotel) object;
            this.cuerpoCorreo.append("<p>Se canceló el registro del huésped <b>".concat(sgDetalleSolicitudEstancia.getUsuario() == null ? sgDetalleSolicitudEstancia.getSgInvitado().getNombre() : sgDetalleSolicitudEstancia.getUsuario().getNombre()).concat("</p>"));
            this.cuerpoCorreo.append("<table  align=\"center\" width=\"100%\">"
                    + "<tr><td valign=\"top\">"
                    + "<table width=\"100%\"  align=\"center\">");
            this.cuerpoCorreo.append("<tr><th colspan=\"7\"   bgcolor=\"#CDE4F6\">Datos del huésped</th></tr>");
            this.cuerpoCorreo.append("<tr><td align=\"center\"><strong>Gerencia</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Responsable</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\"  align=\"center\"><strong>Huésped</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Tipo Huésped</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Inicio</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Fin</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Tipo estancia</strong></td>");
            this.cuerpoCorreo.append("</tr><tr>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" >".concat(urgv.getNombreGerencia()).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" >".concat(urgv.getNombreUsuario()).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" >".concat(sgDetalleSolicitudEstancia.getUsuario() == null ? sgDetalleSolicitudEstancia.getSgInvitado().getNombre() : sgDetalleSolicitudEstancia.getUsuario().getNombre()).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" >".concat(sgDetalleSolicitudEstancia.getSgTipoEspecifico().getNombre()).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" >".concat(sdf.format(sgHuespedHotel.getFechaIngreso())).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" >".concat(sgHuespedHotel.getFechaSalida() != null ? sdf.format(sgHuespedHotel.getFechaSalida()) : "Indefinido").concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" >".concat(sgHuespedHotel.getSgTipoEspecifico().getNombre()).concat("</td>"));
            this.cuerpoCorreo.append("</td></tr>");
            this.cuerpoCorreo.append("</td></tr></table>");

            this.cuerpoCorreo.append("</td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("</table>");
        }//Termina el envio de correo de salida huesped hotel
        if (object instanceof SgHuespedStaff) {
            SgHuespedStaff sgHuespedStaff = (SgHuespedStaff) object;
            this.cuerpoCorreo.append("<p>Se canceló el registro del huésped <b>".concat(sgDetalleSolicitudEstancia.getUsuario() == null ? sgDetalleSolicitudEstancia.getSgInvitado().getNombre() : sgDetalleSolicitudEstancia.getUsuario().getNombre()).concat("</p>"));
            this.cuerpoCorreo.append("<table align=\"center\" width=\"100%\">"
                    + "<tr><td valign=\"top\">"
                    + "<table width=\"100%\"  align=\"center\">");
            this.cuerpoCorreo.append("<tr><th colspan=\"7\"   bgcolor=\"#CDE4F6\">Datos del huésped</th></tr>");
            this.cuerpoCorreo.append("<tr><td  align=\"center\"><strong>Gerencia</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Responsable</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Huésped</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Tipo</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Inicio</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\"  align=\"center\"><strong>Fin</strong></td>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloTitulo()).append("\" align=\"center\"><strong>Tipo estancia</strong></td>");
            this.cuerpoCorreo.append("</tr><tr>");
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" >".concat(urgv.getNombreGerencia()).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" >".concat(urgv.getNombreUsuario()).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" >".concat(sgDetalleSolicitudEstancia.getUsuario() == null ? sgDetalleSolicitudEstancia.getSgInvitado().getNombre() : sgDetalleSolicitudEstancia.getUsuario().getNombre()).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" >".concat(sgDetalleSolicitudEstancia.getSgTipoEspecifico().getNombre()).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" >".concat(sdf.format(sgHuespedStaff.getFechaIngreso())).concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" >".concat(sgHuespedStaff.getFechaSalida() != null ? sdf.format(sgHuespedStaff.getFechaSalida()) : "Indefinido").concat("</td>"));
            this.cuerpoCorreo.append("<td style=\"").append(getEstiloContenido()).append("\" >".concat(sgHuespedStaff.getSgTipoEspecifico().getNombre()).concat("</td>"));
            this.cuerpoCorreo.append("</td></tr>");
            this.cuerpoCorreo.append("</td></tr></table>");
            this.cuerpoCorreo.append("</td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("</table>");
        }
        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlEnvioAvisoPagoStaff(SgOficina oficina) {
        UtilLog4j.log.info(this, "Armando el correo");
        List<SgStaff> listaStaff = null;
        List<SgAvisoPago> listaAvisosPagos = null;
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Recordatorio de pagos - " + sdf.format(new Date())));
        this.cuerpoCorreo.append("<p>Estimado Usuario, </p>");
        this.cuerpoCorreo.append("<p>En los próximos dias se deben realizar los siguientes pagos</p>");
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<table width=\"54%\" border=\"0\" align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\">");
        this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\" border=\"0\" align=\"left\">");

        this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(oficina.getNombre()).concat("</th></tr>"));

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"27%\" align=\"center\"><strong>Pago</strong></td>");
        this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\"><strong>Periodo</strong></td>");
        this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\"><strong>Fecha</strong></td>");
        this.cuerpoCorreo.append("</tr>");

        listaStaff = staffService.getAllStaffByStatusAndOficina(Constantes.BOOLEAN_FALSE, oficina.getId());
        if (!listaStaff.isEmpty()) {
            for (SgStaff st : listaStaff) {
                listaAvisosPagos = this.avisoPagoRemoteService.traerAvisosPagosPorStaffConFechaHoy(st);
                if (listaAvisosPagos != null && !listaAvisosPagos.isEmpty()) {
                    UtilLog4j.log.info(this, "## La lista tiene " + listaAvisosPagos.size() + " registros ###");
                    this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(st.getNombre()).concat("</th></tr>"));
                    for (SgAvisoPago pago : listaAvisosPagos) {
                        this.cuerpoCorreo.append("<tr>");
                        this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\">".concat(pago.getSgTipoEspecifico().getNombre().concat("</td>")));
                        this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\">".concat(pago.getSgPeriodicidad().getNombre().concat("</td>")));
                        this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\">".concat(sdf.format(this.siManejoFechaLocal.componerFechaApartirDeDia(new Date(), pago.getDiaEstimadoPago())).concat("</td>")));
                        this.cuerpoCorreo.append("</tr>");
                    }
                    //pinta raya al final de la lista..
//                        this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\"></th></tr>");
                }

            }

            this.cuerpoCorreo.append("</td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("</table>");
            this.cuerpoCorreo.append("</td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("</table>");
        }
        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlEnvioAvisoPagoOficina(SgOficina oficina, List<SgAvisoPago> listaAvisosPagos) {
        UtilLog4j.log.info(this, "Armando el correo para oficina " + oficina.getNombre());
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Recordatorio de pagos - " + sdf.format(new Date())));
        this.cuerpoCorreo.append("<p>Estimado Usuario, </p>");
        this.cuerpoCorreo.append("<p>En los próximos dias se deben realizar los siguientes pagos</p>");
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<table width=\"54%\" border=\"0\"  align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\">");
        this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\" border=\"0\" align=\"left\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(oficina.getNombre()).concat("</th></tr>"));
        if (!listaAvisosPagos.isEmpty()) {
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td width=\"27%\" align=\"center\"><strong>Pago</strong></td>");
            this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\"><strong>Periodo</strong></td>");
            this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\"><strong>Fecha</strong></td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\"></th></tr>");

            for (SgAvisoPago pago : listaAvisosPagos) {
                this.cuerpoCorreo.append("<tr>");
                this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\">".concat(pago.getSgTipoEspecifico().getNombre().concat("</td>")));
                this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\">".concat(pago.getSgPeriodicidad().getNombre().concat("</td>")));
                this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\">".concat(sdf.format(this.siManejoFechaLocal.componerFechaApartirDeDia(new Date(), pago.getDiaEstimadoPago())).concat("</td>")));
                this.cuerpoCorreo.append("</tr>");
            }
            this.cuerpoCorreo.append("</td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("</table>");
            this.cuerpoCorreo.append("</td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("</table>");
        }
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlEnvioSalidaHuespedStaff(SgOficina oficina, Date fechaVencimiento) {
        UtilLog4j.log.info(this, "Armando el correo para enviar correos de salida de huesped");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfl = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));
        List<SgStaff> listaStaff = null;
        List<SgHuespedStaff> listaHuespedes = null;
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Vencimiento de estancia  - " + oficina.getNombre() + " " + sdf.format(new Date())));
        this.cuerpoCorreo.append("<p>Estimado Usuario, </p>");
        this.cuerpoCorreo.append("<p>El próximo ".concat(sdfl.format(fechaVencimiento)).concat(" terminarán la siguientes estancias :</p>"));
        this.cuerpoCorreo.append("<p></p>");
        this.cuerpoCorreo.append("<table width=\"85%\" border=\"0\"  align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\">");
        this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\" border=\"0\" align=\"left\">");

        listaStaff = staffService.getAllStaffByStatusAndOficina(Constantes.BOOLEAN_FALSE, oficina.getId());
        if (!listaStaff.isEmpty()) {
            UtilLog4j.log.info(this, "recorriendo lista de staff ");
            for (SgStaff st : listaStaff) {
                listaHuespedes = this.sgHuespedStaffService.findAllVencimientoEstanciaPorStaff(fechaVencimiento, st, 0);
                if (!listaHuespedes.isEmpty()) {
                    UtilLog4j.log.info(this, "recorriendo lista de huespedes ");
                    this.cuerpoCorreo.append("<tr><th colspan=\"4\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(st.getNombre()).concat("</th></tr>"));
                    this.cuerpoCorreo.append("<tr>");
                    this.cuerpoCorreo.append("<td width=\"27%\" align=\"center\"><strong>Nombre</strong></td>");
                    this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\"><strong>Habitación</strong></td>");
                    this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\"><strong>Fecha de Ingreso</strong></td>");
                    this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\"><strong>Invitado</strong></td>");
                    this.cuerpoCorreo.append("<tr><th colspan=\"4\" scope=\"col\"  bgcolor=\"#CDE4F6\"></th></tr>");
                    this.cuerpoCorreo.append("</tr>");
                    for (SgHuespedStaff hs : listaHuespedes) {
                        this.cuerpoCorreo.append("<tr>");
                        if (hs.getSgDetalleSolicitudEstancia().getUsuario() != null) {
                            this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\">".concat(hs.getSgDetalleSolicitudEstancia().getUsuario().getNombre()).concat("</td>"));
                        } else if (hs.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre() != null && !hs.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre().equals("")) {
                            this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\">".concat(hs.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre()).concat("</td>"));
                        } else {
                            this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\">".concat("NULL").concat("</td>"));
                        }
                        this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\">".concat(hs.getSgStaffHabitacion().getNumeroHabitacion()).concat("</td>"));
                        this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\">".concat(sdf.format(hs.getFechaIngreso())).concat("</td>"));
                        if (hs.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre() != null) {
                            this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\">SI</td>");
                        } else {
                            this.cuerpoCorreo.append("<td width=\"16%\" height=\"20\" align=\"center\">NO</td>");
                        }
                        this.cuerpoCorreo.append("</tr>");
                    }

                }
            }
            this.cuerpoCorreo.append("</td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("</table>");
            this.cuerpoCorreo.append("</td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("</table>");
        }
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlCancelacionAntesDeRegistroHuesped(int idInvitado, String invitado, String empleado, String gerencia, Date inicio, Date fin, String codigo, String nombreGenero, String tipoDetalle) {
        UtilLog4j.log.info(this, "Armando el correo para enviar correos de cancelación de registro  de huesped");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        String nombre = idInvitado == 0 ? empleado : invitado;
        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Integrante de solicitud de estancia - ".concat(codigo != null ? codigo : "Sin Código").concat(" - Cancelado")));
        //Cuerpo de correo
        this.cuerpoCorreo.append("<p>El integrante <b>").append(idInvitado == 0 ? empleado : invitado).append("</b> de la solicitud de estancia <b>").append(codigo).append(" </b> ha sido cancelado</p>");
        this.cuerpoCorreo.append("<table cellspacing=\"8\" align=\"center\" width=\"100%\"><tr><td valign=\"top\"><table width=\"100%\"  align=\"center\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"6\"   bgcolor=\"#CDE4F6\">Datos del huésped</th></tr>");
        this.cuerpoCorreo.append("<tr><td width=\"13%\" align=\"center\"><strong>Gerencia</strong></td>");
        this.cuerpoCorreo.append("<td width=\"13%\" align=\"center\"><strong>Responsable</strong></td>");
        this.cuerpoCorreo.append("<td width=\"10%\" align=\"center\"><strong>Huésped</strong></td>");
        this.cuerpoCorreo.append("<td width=\"8%\" align=\"center\"><strong>Tipo</strong></td>");
        this.cuerpoCorreo.append("<td width=\"7%\" align=\"center\"><strong>Inicio</strong></td>");
        this.cuerpoCorreo.append("<td width=\"7%\" align=\"center\"><strong>Fin</strong></td>");
        this.cuerpoCorreo.append("</tr><tr>");
        this.cuerpoCorreo.append("<td>").append(gerencia).append("</td>");
        this.cuerpoCorreo.append("<td>").append(nombreGenero).append("</td>");
        this.cuerpoCorreo.append("<td>").append(nombre).append("</td>");
        this.cuerpoCorreo.append("<td>").append(tipoDetalle).append("</td>");
        this.cuerpoCorreo.append("<td>").append(inicio != null ? sdf.format(inicio) : "--").append("</td>");
        this.cuerpoCorreo.append("<td>").append(fin != null ? sdf.format(fin) : "--").append("</td>");
        this.cuerpoCorreo.append("</td></tr>");
        this.cuerpoCorreo.append("</td></tr></table>");
        this.cuerpoCorreo.append("</td></tr></table>");
        //Fin del cuerpo de correo

        this.cuerpoCorreo.append(plantilla.getFin());
        UtilLog4j.log.info(this, "HTML: " + cuerpoCorreo.toString());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlAvisoVencimientoLicencia(List<LicenciaVo> l, String oficina, Date fechaVencimiento) {
        SimpleDateFormat sdfl = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));
        UtilLog4j.log.info(this, "Armando el correo de vencimiento de licencias ");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Vencimiento de licencia"));
        this.cuerpoCorreo.append("<p>Estimado Usuario, </p>");
        if (l.size() > 1) {
            this.cuerpoCorreo.append("<p>El próximo <strong>".concat(sdfl.format(fechaVencimiento)).concat("</strong> ,vencerán las siguientes Licencias :</p>"));
        } else {
            this.cuerpoCorreo.append("<p>El próximo <strong>".concat(sdfl.format(fechaVencimiento)).concat("</strong> ,vencerá la siguiente Licencia :</p>"));
        }
        this.cuerpoCorreo.append("<p></p>");
        this.cuerpoCorreo.append("<table width=\"80%\" border=\"0\"  align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\">");
        this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\" border=\"0\" align=\"left\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(oficina).concat("</th></tr>"));

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"35%\" align=\"center\"><strong>Usuario</strong></td>");
        this.cuerpoCorreo.append("<td width=\"30%\" height=\"20\" align=\"center\"><strong>No. Licencia<strong></strong></td>");
        this.cuerpoCorreo.append("<td width=\"30%\" height=\"20\" align=\"center\"><strong>País</strong></td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\"></th></tr>");

        for (LicenciaVo licencia : l) {
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td width=\"35%\" height=\"20\" align=\"center\">").append(licencia.getUsuario()).append("</td>");
            this.cuerpoCorreo.append("<td width=\"30%\" height=\"20\" align=\"center\">").append(licencia.getNumero()).append("</td>");
            this.cuerpoCorreo.append("<td width=\"30%\" height=\"20\" align=\"center\">").append(licencia.getPais()).append("</td>");
            this.cuerpoCorreo.append("</tr>");
        }

        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");

        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlAvisoVencimientoPagoServicioVehiculo(List<SgPagoServicioVehiculo> lista, SgOficina oficina, Date fechaVencimiento, SgTipoEspecifico tipoEspecifico) {
        SimpleDateFormat sdfl = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));
        UtilLog4j.log.info(this, "Armando el correo de vencimiento de pagos de servicio de pagos ");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Vencimiento de pago de servicio para vehículo - " + sdf.format(new Date())));
        this.cuerpoCorreo.append("<p>Estimado Usuario, </p>");
        if (lista.size() > 1) {
            this.cuerpoCorreo.append("<p>El próximo <strong>".concat(sdfl.format(fechaVencimiento)).concat("</strong> vencerán los pagos de " + tipoEspecifico.getNombre() + " :</p>"));
        } else {
            this.cuerpoCorreo.append("<p>El próximo <strong>".concat(sdfl.format(fechaVencimiento)).concat("</strong> vencerá el pago de " + tipoEspecifico.getNombre() + " :</p>"));
        }
        this.cuerpoCorreo.append("<p></p>");
        this.cuerpoCorreo.append("<table width=\"80%\" border=\"0\"  align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\">");
        this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\" border=\"0\" align=\"left\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"4\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(oficina.getNombre()).concat("</th></tr>"));

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"35%\" align=\"center\"><strong>Véhiculo</strong></td>");
        this.cuerpoCorreo.append("<td width=\"40%\" height=\"20\" align=\"center\"><strong>Proveedor<strong></strong></td>");
        this.cuerpoCorreo.append("<td width=\"25%\" height=\"20\" align=\"center\"><strong>Importe</strong></td>");
        this.cuerpoCorreo.append("<td width=\"22%\" height=\"20\" align=\"center\"><strong>F. Vencimiento</strong></td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("<tr><th colspan=\"4\" scope=\"col\"  bgcolor=\"#CDE4F6\"></th></tr>");

        for (SgPagoServicioVehiculo pagoVehiculo : lista) {
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td width=\"35%\" height=\"20\" align=\"center\">".concat(pagoVehiculo.getSgVehiculo().getSgMarca().getNombre().concat(pagoVehiculo.getSgVehiculo().getSgModelo().getNombre()).concat("</td>")));
            this.cuerpoCorreo.append("<td width=\"40%\" height=\"20\" align=\"center\">".concat(pagoVehiculo.getSgPagoServicio().getProveedor().getNombre().concat("</td>")));
            this.cuerpoCorreo.append("<td width=\"25%\" height=\"20\" align=\"center\">$".concat(Constantes.formatoMoneda.format(pagoVehiculo.getSgPagoServicio().getImporte()).concat("(").concat(pagoVehiculo.getSgPagoServicio().getMoneda().getNombre()).concat(")").concat("</td>")));
            this.cuerpoCorreo.append("<td width=\"22%\" height=\"20\" align=\"center\">".concat(sdf.format(pagoVehiculo.getSgPagoServicio().getFechaVencimiento())).concat("</td>"));
            this.cuerpoCorreo.append("</tr>");
        }
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");

        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlAvisoProxMantenimientoPorKm(List<VehiculoVO> l, String oficina) {
        UtilLog4j.log.info(this, "Armando el correo de proximos mantenimientos de vehiculos ");
        String bgGris = "bgcolor=#FAFAFA";
        String bgBlanco = "bgcolor=#ffffff";
        String f = "";

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Próximo mantenimiento por kilometraje"));
        this.cuerpoCorreo.append("<p>Estimado Usuario, </p>");

        if (l.size() > 1) {
            this.cuerpoCorreo.append("<p>Se deben realizar mantenimientos a los siguientes vehículos:</p>");
        } else {
            this.cuerpoCorreo.append("<p>Se debe realizar mantenimiento al siguiente vehículo:</p>");
        }
        this.cuerpoCorreo.append("<p></p>");
        this.cuerpoCorreo.append("<table width=\"100%\" border=\"0\"  align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\">");
        this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\" border=\"0\" align=\"left\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(oficina).concat("</th></tr>"));

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"30%\" align=\"center\"><strong>Placa</strong></td>");
        this.cuerpoCorreo.append("<td width=\"30%\" align=\"center\"><strong>Marca / Modelo</strong></td>");
        this.cuerpoCorreo.append("<td width=\"25%\" align=\"center\"><strong>Color</strong></td>");
        this.cuerpoCorreo.append("<td width=\"25%\" height=\"20\" align=\"center\"><strong>Km. Actual</strong></td>");
        this.cuerpoCorreo.append("<td width=\"25%\" height=\"20\" align=\"center\"><strong>Km. Próx. Mantenimiento <strong></strong></td>");

        //this.cuerpoCorreo.append("<td width=\"22%\" height=\"20\" align=\"center\"><strong></strong></td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\" scope=\"col\"  bgcolor=\"#CDE4F6\"></th></tr>");
        int i = 1;
        for (int x = 0; x < l.size(); x++) {
            if (i % 2 == 0) {
                f = bgBlanco;
            } else {
                f = bgGris;
            }
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"30%\" height=\"20\" align=\"center\">").concat(l.get(x).getNumeroPlaca()).concat("</td>"));
            this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"30%\" height=\"20\" align=\"center\">").concat(l.get(x).getMarca().concat(" / ").concat(l.get(x).getModelo()).concat("</td>")));
            this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"25%\" height=\"20\" align=\"center\">").concat(String.valueOf(l.get(x).getColor()).concat("</td>")));
            this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"25%\" height=\"20\" align=\"center\">").concat(String.valueOf(l.get(x).getKmActual()).concat("</td>")));
            this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"25%\" height=\"20\" align=\"center\">").concat(String.valueOf(l.get(x).getKmProximoMantenimiento()).concat("</td>")));
            this.cuerpoCorreo.append("</tr>");
        }
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");

        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;

        /*
	 * SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
	 * SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	 * this.limpiarCuerpoCorreo();
	 * this.cuerpoCorreo.append(plantilla.getInicio());
	 * this.cuerpoCorreo.append(this.getTitulo("Próximo mantenimiento por
	 * kilometraje")); this.cuerpoCorreo.append("<p>Estimado Usuario,
	 * </p>"); if (l.size() > 1) { this.cuerpoCorreo.append("<p>Se deben
	 * realizar mantenimientos a los siguientes vehículos:</p>"); } else {
	 * this.cuerpoCorreo.append("<p>Se debe realizar mantenimiento al
	 * siguiente vehículo:</p>"); } this.cuerpoCorreo.append("<p></p>");
	 * this.cuerpoCorreo.append("<table width=\"80%\" border=\"0\"
	 * align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
	 * this.cuerpoCorreo.append("<tr><td valign=\"top\">");
	 * this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\"
	 * border=\"0\" align=\"left\">"); this.cuerpoCorreo.append("<tr><th
	 * colspan=\"4\" scope=\"col\"
	 * bgcolor=\"#CDE4F6\">".concat(oficina.getNombre()).concat("</th></tr>"));
	 *
	 * this.cuerpoCorreo.append("<tr>"); this.cuerpoCorreo.append("<td
	 * width=\"35%\" align=\"center\"><strong>Véhiculo</strong></td>");
	 * this.cuerpoCorreo.append("<td width=\"35%\"
	 * align=\"center\"><strong>Marca / Modelo</strong></td>");
	 * this.cuerpoCorreo.append("<td width=\"40%\" height=\"20\"
	 * align=\"center\"><strong>Km. para Próx. Mtto.
	 * <strong></strong></td>"); this.cuerpoCorreo.append("<td width=\"25%\"
	 * height=\"20\" align=\"center\"><strong>Km. Actual</strong></td>");
	 * //this.cuerpoCorreo.append("<td width=\"22%\" height=\"20\"
	 * align=\"center\"><strong></strong></td>");
	 * this.cuerpoCorreo.append("</tr>"); this.cuerpoCorreo.append("<tr><th
	 * colspan=\"4\" scope=\"col\" bgcolor=\"#CDE4F6\"></th></tr>");
	 *
	 * for (int x = 0; x < l.size(); x++) {
	 * this.cuerpoCorreo.append("<tr>"); this.cuerpoCorreo.append("<td
	 * width=\"35%\" height=\"20\"
	 * align=\"center\">".concat(l.get(x).getSgVehiculo().getSgMarca().getNombre()).concat(l.get(x).getSgVehiculo().getSgModelo().getNombre()).concat("</td>"));
	 * this.cuerpoCorreo.append("<td width=\"35%\" height=\"20\"
	 * align=\"center\">".concat(l.get(x).getSgVehiculo().getSgMarca().getNombre().concat("
	 * /
	 * ").concat(l.get(x).getSgVehiculo().getSgModelo().getNombre()).concat("</td>")));
	 * this.cuerpoCorreo.append("<td width=\"40%\" height=\"20\"
	 * align=\"center\">".concat(String.valueOf(l.get(x).getProxMantenimientoKilometraje()).concat("</td>")));
	 * this.cuerpoCorreo.append("<td width=\"25%\" height=\"20\"
	 * align=\"center\">".concat(String.valueOf(listaKm.get(x).getKilometraje()).concat("</td>")));
	 * this.cuerpoCorreo.append("</tr>"); }
	 * this.cuerpoCorreo.append("</td>"); this.cuerpoCorreo.append("</tr>");
	 * this.cuerpoCorreo.append("</table>");
	 * this.cuerpoCorreo.append("</td>"); this.cuerpoCorreo.append("</tr>");
	 * this.cuerpoCorreo.append("</table>");
	 *
	 * //Aquí va todo el contenido del cuerpo,
	 * this.cuerpoCorreo.append(plantilla.getFin()); return
	 * this.cuerpoCorreo;
         */
    }

    
    public StringBuilder getHtmlAvisoProxMantenimientoPorPeriodicidad(String nombreOficina, List<VehiculoVO> l) {
        UtilLog4j.log.info(this, "Armando el correo de proximos mantenimientos de vehiculos por periodicidad ");
        String bgGris = "bgcolor=#FAFAFA";
        String bgBlanco = "bgcolor=#ffffff";
        String f = "";

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Próximo mantenimiento por periodo"));
        this.cuerpoCorreo.append("<p>Estimado Usuario, </p>");

        if (l.size() > 1) {
            this.cuerpoCorreo.append("<p>Se deben realizar mantenimientos a los siguientes vehículos:</p>");
        } else {
            this.cuerpoCorreo.append("<p>Se debe realizar mantenimiento al siguiente vehículo:</p>");
        }
        this.cuerpoCorreo.append("<p></p>");
        this.cuerpoCorreo.append("<table width=\"100%\" border=\"0\"  align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\">");
        this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\" border=\"0\" align=\"left\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(nombreOficina).concat("</th></tr>"));

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"30%\" align=\"center\"><strong>Placa</strong></td>");
        this.cuerpoCorreo.append("<td width=\"30%\" align=\"center\"><strong>Marca / Modelo</strong></td>");
        this.cuerpoCorreo.append("<td width=\"25%\" align=\"center\"><strong>Color</strong></td>");
        this.cuerpoCorreo.append("<td width=\"25%\" height=\"20\" align=\"center\"><strong>Km. Actual</strong></td>");
        this.cuerpoCorreo.append("<td width=\"25%\" height=\"20\" align=\"center\"><strong>Km. Próx. Mantenimiento <strong></strong></td>");

        //this.cuerpoCorreo.append("<td width=\"22%\" height=\"20\" align=\"center\"><strong></strong></td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\" scope=\"col\"  bgcolor=\"#CDE4F6\"></th></tr>");
        int i = 1;
        for (int x = 0; x < l.size(); x++) {
            if (i % 2 == 0) {
                f = bgBlanco;
            } else {
                f = bgGris;
            }
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"30%\" height=\"20\" align=\"center\">").concat(l.get(x).getNumeroPlaca()).concat("</td>"));
            this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"30%\" height=\"20\" align=\"center\">").concat(l.get(x).getMarca().concat(" / ").concat(l.get(x).getModelo()).concat("</td>")));
            this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"25%\" height=\"20\" align=\"center\">").concat(String.valueOf(l.get(x).getColor()).concat("</td>")));
            this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"25%\" height=\"20\" align=\"center\">").concat(String.valueOf(l.get(x).getKmActual()).concat("</td>")));
            this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"25%\" height=\"20\" align=\"center\">").concat(String.valueOf(l.get(x).getKmProximoMantenimiento() == 0 ? "<p style= \"color :red \";>Pendiente <p>" : l.get(x).getKmProximoMantenimiento()).concat("</td>")));
            this.cuerpoCorreo.append("</tr>");
        }
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");

        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlAvisoProxMantenimientoPorFecha(List<VehiculoVO> l, String oficina, Date fecha) {
        SimpleDateFormat sdfl = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));
        UtilLog4j.log.info(this, "Armando el correo de proximos mantenimientos de vehiculos por fecha ");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String bgGris = "bgcolor=#FAFAFA";
        String bgBlanco = "bgcolor=#ffffff";
        String f = "";

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Próximo mantenimiento por fecha "));
        this.cuerpoCorreo.append("<p>Estimado Usuario, </p>");
        if (l.size() > 1) {
            this.cuerpoCorreo.append("<p>Antes del próximo <strong>".concat(sdfl.format(fecha)).concat("</strong>, los siguientes vehículos requieren mantenimiento:</p>"));
        } else {
            this.cuerpoCorreo.append("<p>El próximo <strong>".concat(sdfl.format(fecha)).concat("</strong>, el siguiente vehículo requiere mantenimiento:</p>"));
        }
        this.cuerpoCorreo.append("<p></p>");
        this.cuerpoCorreo.append("<table width=\"100%\" border=\"0\"  align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\">");
        this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\" border=\"0\" align=\"left\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(oficina).concat("</th></tr>"));

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"30%\" align=\"center\"><strong>Placa</strong></td>");
        this.cuerpoCorreo.append("<td width=\"30%\" align=\"center\"><strong>Marca / Modelo</strong></td>");
        this.cuerpoCorreo.append("<td width=\"25%\" align=\"center\"><strong>Color</strong></td>");
        this.cuerpoCorreo.append("<td width=\"25%\" height=\"20\" align=\"center\"><strong>Fecha. Próx. Mantenimiento <strong></strong></td>");
        this.cuerpoCorreo.append("<td width=\"25%\" height=\"20\" align=\"center\"><strong>Km. Actual</strong></td>");

        //this.cuerpoCorreo.append("<td width=\"22%\" height=\"20\" align=\"center\"><strong></strong></td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\" scope=\"col\"  bgcolor=\"#CDE4F6\"></th></tr>");
        int i = 1;
        for (int x = 0; x < l.size(); x++) {
            if (i % 2 == 0) {
                f = bgBlanco;
            } else {
                f = bgGris;
            }
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"30%\" height=\"20\" align=\"center\">").concat(l.get(x).getNumeroPlaca()).concat("</td>"));
            this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"30%\" height=\"20\" align=\"center\">").concat(l.get(x).getMarca().concat(" / ").concat(l.get(x).getModelo()).concat("</td>")));
            this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"25%\" height=\"20\" align=\"center\">").concat(String.valueOf(l.get(x).getColor()).concat("</td>")));
            this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"25%\" height=\"20\" align=\"center\">").concat(String.valueOf(sdf.format(l.get(x).getFechaProxMantenimiento())).concat("</td>")));
            this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"25%\" height=\"20\" align=\"center\">").concat(String.valueOf(l.get(x).getKmActual()).concat("</td>")));
            this.cuerpoCorreo.append("</tr>");
        }
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");

        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    /*Cambiar el formato de correo
     Poner El departamento de seguridad pidio informaciòn para la solicitud de viaje PVI13*/
    
    public StringBuilder getHtmlComentarioNoticia(CoNoticia coNoticia, CoComentario coComentario, boolean isRecomendacionSeguridad) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        //this.cuerpoCorreo.append(this.getTitulo(isRecomendacionSeguridad ? "Seguridad ha recomendado " :  coComentario.getGenero().getNombre() + " ha comentado "));
        this.cuerpoCorreo.append(this.getTitulo(isRecomendacionSeguridad ? "CENTOPS ha comentado " : coComentario.getGenero().getNombre() + " ha comentado "));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<table width=\"85%\" border=\"0\">");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"50%\" align=\"left\" style=\"font:Arial, Helvetica, sans-serif; font-size:12px;\">");
        this.cuerpoCorreo.append("<strong>").append(isRecomendacionSeguridad ? "El Departamento de Gestión de Riesgos " : coComentario.getGenero().getNombre()).append("</strong> ").append("ha comentado").append(" la <strong>").append(coNoticia.getTitulo()).append(":</strong>");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        //
        cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"100%\" align=\"left\" style=\"font:Arial, Helvetica, sans-serif; color:gray; font-size:12px;\">");
        this.cuerpoCorreo.append("<b> Nota: </b>");
        this.cuerpoCorreo.append(coNoticia.getMensajeAutomatico());
        this.cuerpoCorreo.append("</td>");
        cuerpoCorreo.append("</tr>");
        //
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"100%\" align=\"left\" style=\"font:Arial, Helvetica, sans-serif; color:gray; font-size:12px;\"> <b>Comentario: </b>");
        this.cuerpoCorreo.append("\"").append(coComentario.getMensaje()).append("\"");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"50%\" align=\"left\" style=\"font:Arial, Helvetica, sans-serif; font-size:9px; color:gray;\">");
        this.cuerpoCorreo.append(Constantes.FMT_TextDate.format(coComentario.getFechaGenero()).concat(" ").concat(Constantes.FMT_hmm_a.format(coComentario.getHoraGenero())));
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"50%\" align=\"left\" style=\"font:Arial, Helvetica, sans-serif; font-size:11px; color:silver;\">");
        this.cuerpoCorreo.append("Para comentar esta publicación ").append("da clic <A HREF='").append(Configurador.urlSia()).append("Sia' TARGET='_new'>aqui</A>.");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td>");
        this.cuerpoCorreo.append("");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");

        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlNotificacionReinicioModificacionKilometraje(SgVehiculo sgVehiculo, int kmActual, int kmNuevo, String motivo, String nombreUsuario, boolean isModificacion) {
        UtilLog4j.log.info(this, "Armando el correo para notificacion de kilometraje ");
        String bgGris = "bgcolor=#FAFAFA";
        String bgBlanco = "bgcolor=#ffffff";
        String f = "";

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo((isModificacion ? "Modificación" : "Reinicio") + " de kilometraje"));
        //this.cuerpoCorreo.append("<p>Se ha reiniciado el kilometraje al vehiculo ").append(sgVehiculo.getSgMarca().getNombre()).append(sgVehiculo.getSgModelo().getNombre())
        //                      .append(" con placa numero de placa ").append(sgVehiculo.getNumeroPlaca()).append(".</p>");
        this.cuerpoCorreo.append("<br/><p>Se ha ");
        this.cuerpoCorreo.append(isModificacion ? "modificado" : "reiniciado");
        this.cuerpoCorreo.append(" el kilometraje del siguiente vehículo ");

        this.cuerpoCorreo.append("<table width=\"100%\" border=\"0\"  align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\">");
        this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\" border=\"0\" align=\"left\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(sgVehiculo.getSgOficina().getNombre()).concat("</th></tr>"));

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"30%\" align=\"center\"><strong>Placa</strong></td>");
        this.cuerpoCorreo.append("<td width=\"50%\" align=\"center\"><strong>Marca / Modelo</strong></td>");
        this.cuerpoCorreo.append("<td width=\"25%\" align=\"center\"><strong>Color</strong></td>");

        //this.cuerpoCorreo.append("<td width=\"22%\" height=\"20\" align=\"center\"><strong></strong></td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\"></th></tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"30%\" height=\"20\" align=\"center\">").concat(sgVehiculo.getNumeroPlaca()).concat("</td>"));
        this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"50%\" height=\"20\" align=\"center\">").concat(sgVehiculo.getSgMarca().getNombre().concat(" / ").concat(sgVehiculo.getSgModelo().getNombre()).concat("</td>")));
        this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"25%\" height=\"20\" align=\"center\">").concat(sgVehiculo.getSgColor().getNombre().concat("</td>")));
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");

        if (isModificacion) {
            this.cuerpoCorreo.append("<br/><strong>Km. Anterior :</strong>").append(kmActual);
            this.cuerpoCorreo.append("<br/><strong>Km. Nuevo :</strong>").append(kmNuevo);
        }
        this.cuerpoCorreo.append("<br/><strong>").append(isModificacion ? "Modificó" : "Reinició").append(":</strong>");
        this.cuerpoCorreo.append("<br/>".concat(nombreUsuario).concat(""));
        this.cuerpoCorreo.append("<br/><strong>Fecha : </strong>").append(Constantes.FMT_TextDate.format(new Date())).append(" a las ").append(Constantes.FMT_hmm_a.format(new Date()));
        this.cuerpoCorreo.append("<br/><b>Motivo de ");
        this.cuerpoCorreo.append(isModificacion ? "modificación</b>" : "reinicio</b>");
        this.cuerpoCorreo.append("<br/>".concat(motivo).concat(""));
        this.cuerpoCorreo.append("<br/>");

        if (!isModificacion) {
            this.cuerpoCorreo.append("<p style=\"font-size:12px;color:red;\"><b>Las notificaciones del sistema para los mantenimientos preventivos de este vehículo se han desactivado temporalmente.".concat(" Éstas se activarán automaticamente cuando se realize el siguiente registro de mantenimiento preventivo.</b></p>"));
        }
        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

//    
//    public StringBuilder getHtmlNotificacionForTeamSIA(String notification, String asunto) {
//        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
//        this.limpiarCuerpoCorreo();
//        this.cuerpoCorreo.append(plantilla.getInicio());
//        this.cuerpoCorreo.append(this.getTitulo(asunto));
//        this.cuerpoCorreo.append("<p><hr/></p>");
//        this.cuerpoCorreo.append("<p>").append(notification).append("</p>");
//        this.cuerpoCorreo.append("<p><hr/></p>");
//        this.cuerpoCorreo.append(plantilla.getFin());
//        return this.cuerpoCorreo;
//
//    }
    
    public StringBuilder getHtmlNotificacionSeguridadDireccion(List<EstadoSemaforoCambioVO> lista, String justificacion, String asunto) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo(asunto));
        this.cuerpoCorreo.append("</b></p>");

        cuerpoCorreo.append("El <b>Departamento de Gestión de Riesgos</b> ha cambiado el estado del semáforo en la(s) ruta(s)<b>");
        cuerpoCorreo.append("</b> debido a recientes acontecimientos.<b>");
        //Rutas
        rutasSemaforo(lista);
        this.cuerpoCorreo.append("</br>");
        this.cuerpoCorreo.append("<p>Justificación: <b>");
        this.cuerpoCorreo.append(justificacion);
        this.cuerpoCorreo.append("</br>");

        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlNotificacionSeguridadDireccion(SgEstadoSemaforoVO semaforoVO, String asunto) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo(asunto));
        this.cuerpoCorreo.append("</b></p>");

        cuerpoCorreo.append("El <b>Departamento de Gestión de Riesgos</b> ha cambiado el estado del semáforo en la(s) ruta(s)<b>");
        cuerpoCorreo.append("</b> debido a recientes acontecimientos.<b>");
        //Rutas
        rutasSemaforo(semaforoVO);
        this.cuerpoCorreo.append("</br>");
        this.cuerpoCorreo.append("<p>Justificación: <b>");
        this.cuerpoCorreo.append(semaforoVO.getJustificacion());
        this.cuerpoCorreo.append("</br>");

        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlNotificacionSeguridadViajeros(RutaTerrestreVo rutaVO, ViajeVO viajeVO, SgEstadoSemaforoVO semaforo, String asunto) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo(asunto));
        this.cuerpoCorreo.append("</b></p>");

        cuerpoCorreo.append("El <b>Departamento de Gestión de Riesgos</b> ha cambiado el estado del semáforo de color <b>");
        cuerpoCorreo.append(semaforo.getUltimoSemaforo().getSgSemaforo().getColor());
        cuerpoCorreo.append("</b> a color <b>").append(semaforo.getNuevoSemaforo().getColor());
        cuerpoCorreo.append("</b> en la ruta <b>");
        cuerpoCorreo.append(rutaVO.getNombre());
        cuerpoCorreo.append("</b> debido a recientes acontecimientos.<b>");
        puntosSeguridadRuta(rutaVO);
        this.cuerpoCorreo.append("</br>");
        this.cuerpoCorreo.append("<p>Justificación: <b>");
        this.cuerpoCorreo.append(semaforo.getJustificacion());
        this.cuerpoCorreo.append("</br>");

        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlNotificacionSeguridadTodoIhsa(List<EstadoSemaforoCambioVO> lista, String asunto) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo(asunto));
        cuerpoCorreo.append("<p> El departamento de Gestión de Riesgos, ha cambiado el estado del semáforo en la(s) ruta(s).</p>");
        rutasSemaforo(lista);
        cuerpoCorreo.append("<p> Favor de tener en cuenta para el planeamiento de viajes. </p>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;

    }

    
    public StringBuilder getHtmlNotificacionSeguridadTodoIhsa(SgEstadoSemaforoVO semaforoVO, String asunto) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo(asunto));
        cuerpoCorreo.append("<p> El departamento de Gestión de Riesgos, ha cambiado el estado del semáforo en la(s) ruta(s).</p>");
        rutasSemaforo(semaforoVO);
        cuerpoCorreo.append("<p> Favor de tener en cuenta para el planeamiento de viajes. </p>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;

    }

    private void rutasSemaforo(List<EstadoSemaforoCambioVO> lista) {
        this.cuerpoCorreo.append("<br/><table width=\"100%\" align=\"left\" cellspacing=\"0\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Estado del semáforo</th></tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<th width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Ruta</th>");
        this.cuerpoCorreo.append("<th width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Color Anterior</th>");
        this.cuerpoCorreo.append("<th width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Color Actual</th>");
        this.cuerpoCorreo.append("<th width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Descripción del color</th>");

        this.cuerpoCorreo.append("</tr>");
        for (EstadoSemaforoCambioVO semaforo : lista) {
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td ").append(" width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(semaforo.getRuta()).append("</td>");
            this.cuerpoCorreo.append("<td ").append(" width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(semaforo.getColorAnterior()).append("</td>");
            this.cuerpoCorreo.append("<td ").append(" width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(semaforo.getColorNuevo()).append("</td>");
            this.cuerpoCorreo.append("<td ").append(" width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(semaforo.getDescripcion()).append("</td>");

            this.cuerpoCorreo.append("</tr>");
        }
        this.cuerpoCorreo.append("</table><br/>");
    }

    private void rutasSemaforo(SgEstadoSemaforoVO semaforoVO) {
        this.cuerpoCorreo.append("<br/><table width=\"100%\" align=\"left\" cellspacing=\"0\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Estado del semáforo</th></tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<th width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Ruta</th>");
        this.cuerpoCorreo.append("<th width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Color Anterior</th>");
        this.cuerpoCorreo.append("<th width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Color Actual</th>");
        this.cuerpoCorreo.append("<th width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Descripción del color</th>");

        this.cuerpoCorreo.append("</tr>");
        for (RutaTerrestreVo ruta : semaforoVO.getLstRutaByZona()) {
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td ").append(" width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(ruta.getNombre()).append("</td>");
            this.cuerpoCorreo.append("<td ").append(" width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(semaforoVO.getUltimoSemaforo() != null ? semaforoVO.getUltimoSemaforo().getSgSemaforo().getColor() : "").append("</td>");
            this.cuerpoCorreo.append("<td ").append(" width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(semaforoVO.getNuevoSemaforo().getColor()).append("</td>");
            this.cuerpoCorreo.append("<td ").append(" width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(semaforoVO.getNuevoSemaforo().getDescripcion()).append("</td>");

            this.cuerpoCorreo.append("</tr>");
        }
        this.cuerpoCorreo.append("</table><br/>");
    }

    private void puntosSeguridadRuta(RutaTerrestreVo rutaVO) {
        this.cuerpoCorreo.append("<br/><table width=\"100%\" align=\"left\" cellspacing=\"0\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Puntos de Seguridad en la Ruta</th></tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<th width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Punto de Seguridad</th>");
        this.cuerpoCorreo.append("<th width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Zona</th>");
        this.cuerpoCorreo.append("<th width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Color Actual</th>");
        this.cuerpoCorreo.append("<th width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Descripción del punto</th>");

        this.cuerpoCorreo.append("</tr>");
        for (GrRutaZonasVO zona : rutaVO.getZonas()) {
            if (zona.getPunto() != null && zona.getIdPunto() > 0) {
                this.cuerpoCorreo.append("<tr>");
                this.cuerpoCorreo.append("<td ").append(" width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(zona.getPunto().getNombre()).append("</td>");
                this.cuerpoCorreo.append("<td ").append(" width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(zona.getZona().getNombre()).append("</td>");
                this.cuerpoCorreo.append("<td ").append(" width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(zona.getZona().getSemaforoActual().getColor()).append("</td>");
                this.cuerpoCorreo.append("<td ").append(" width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(zona.getPunto().getDescripcion()).append("</td>");

                this.cuerpoCorreo.append("</tr>");
            }
        }
        this.cuerpoCorreo.append("</table><br/>");
    }

    /**
     * Modifico: 15/10/2013 Nestor Lopez * Modifico: 08/11/2013 MLUIS
     *
     * @param listaRutaSeleccionada
     */
    public void listaRuta(List<SemaforoVo> listaRutaSeleccionada) {
        this.cuerpoCorreo.append("<br/><table width=\"100%\" align=\"left\" cellspacing=\"0\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"5\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Ruta(s)</th></tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<th width=\"35%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Color</th>");
        this.cuerpoCorreo.append("<th width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Descripción</th>");
        this.cuerpoCorreo.append("<th width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Hora Minima</th>");
        this.cuerpoCorreo.append("<th width=\"10%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Hora Maxima</th>");

        this.cuerpoCorreo.append("</tr>");
        for (SemaforoVo semaforo : listaRutaSeleccionada) {
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td ").append(" width=\"35%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(semaforo.getColor()).append("</td>");
            this.cuerpoCorreo.append("<td ").append(" width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(semaforo.getDescripcion()).append("</td>");
//            this.cuerpoCorreo.append("<td ").append(" width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(semaforo.getHoraMinima() != null ? Constantes.FMT_hmm_a.format(semaforo.getHoraMinima()) : "--").append("</td>");
//            this.cuerpoCorreo.append("<td ").append(" width=\"10%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(semaforo.getHoraMaxima() != null ? Constantes.FMT_hmm_a.format(semaforo.getHoraMaxima()) : "--").append("</td>");

            this.cuerpoCorreo.append("</tr>");
        }
        this.cuerpoCorreo.append("</table><br/>");
        //    return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlNotificacionEstanciaProlongadaPorSemaforoNegroParaHuespedStaff(String asunto, String nombreHuesped, String nombreStaff, String numeroStaff, String nombreHabitacion, String numberoHabitacion, Date nuevaFechaSalida, String nombreAnalistaContacto) {

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo(asunto));
        this.cuerpoCorreo.append("<br/><p>Estimado(a) <b> ".concat(nombreHuesped).concat("</b></p>"));
        this.cuerpoCorreo.append("<p>Su estancia en el Staff <b>").append(nombreStaff).append(" | ").append(numeroStaff).append("</b> en la habitación <b>").append(nombreHabitacion).append(" | ").append(numberoHabitacion).append("</b> ha sido prolongada hasta la fecha <b>").append(Constantes.FMT_TextDate.format(nuevaFechaSalida)).append("</b> ").append("debido a que el semáforo de control de viajes ha sido cambiado a color <b>").append(" Negro </b>").append(".</p>");
        this.cuerpoCorreo.append("<p>Por favor ponte en contacto con el Analista <b>").append(nombreAnalistaContacto).append("</b> para más información.</p>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getCuerpoHtmlNotificacionEstanciaProlongadaPorSolicitud(SgSolicitudEstanciaVo solicitudEstanciaVo, List<HuespedVo> listaHuespedHotel, List<HuespedVo> listaHuespedStaff, Date fechaProlongadaInicio, Date fechaProlongadaFin) {
        this.limpiarCuerpoCorreo();
        boolean retornar = false;
        this.cuerpoCorreo.append("<table bordercolor=\"#000000\" border=\"0\" align=\"center\" width=\"90%\">");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<th colspan=\"7\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> ").append(solicitudEstanciaVo.getCodigo()).append("</th>");
        this.cuerpoCorreo.append("</tr>");

        if (listaHuespedHotel != null && !listaHuespedHotel.isEmpty()) {
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td colspan=\"7\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Hospedados en hotel</td>");
            this.cuerpoCorreo.append("</tr>");
            //titulos para tabla de hotel
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td colspan=\"1\" style=\"font-family:Georgia, 'Times New Roman', Times, serif;  color:#FFFFFF; font-size:12px; background-color:#CCC;\">Hotel</td>");
            this.cuerpoCorreo.append("<td colspan=\"1\" style=\"font-family:Georgia, 'Times New Roman', Times, serif;  color:#FFFFFF; font-size:12px; background-color:#CCC;\">Habitación</td>");
            this.cuerpoCorreo.append("<td colspan=\"1\" style=\"font-family:Georgia, 'Times New Roman', Times, serif;  color:#FFFFFF; font-size:12px; background-color:#CCC;\">Huesped</td>");
            this.cuerpoCorreo.append("<td colspan=\"1\" style=\"font-family:Georgia, 'Times New Roman', Times, serif;  color:#FFFFFF; font-size:12px; background-color:#CCC;\">F. Inicio</td>");
            this.cuerpoCorreo.append("<td colspan=\"1\" style=\"font-family:Georgia, 'Times New Roman', Times, serif;  color:#FFFFFF; font-size:12px; background-color:#CCC;\">F. Fin</td>");
            this.cuerpoCorreo.append("<td colspan=\"2\" style=\"font-family:Georgia, 'Times New Roman', Times, serif;  color:#FFFFFF; font-size:12px; background-color:#CCC;\">F. Prolongada</td>");

            this.cuerpoCorreo.append("</tr>");

            for (HuespedVo huesped : listaHuespedHotel) {
                this.cuerpoCorreo.append("<tr>");
                this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\">").append(huesped.getNombreHuesped()).append("</td>");
                this.cuerpoCorreo.append("<td width=\"30%\" height=\"20\" align=\"center\">").append(huesped.getNombreProveedorHotel()).append("</td>");
                this.cuerpoCorreo.append("<td width=\"10%\" height=\"20\" align=\"center\">").append(huesped.getNombreHabitacion()).append("</td>");
                this.cuerpoCorreo.append("<td width=\"10%\" height=\"20\" align=\"center\">").append(Constantes.FMT_ddMMyyy.format(huesped.getFechaIngreso())).append("</td>");
                this.cuerpoCorreo.append("<td width=\"10%\" height=\"20\" align=\"center\">").append(Constantes.FMT_ddMMyyy.format(huesped.getFechaSalida())).append("</td>");
                this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\">").append("Del ").append(Constantes.FMT_ddMMyyy.format(fechaProlongadaFin)).append(" al ").append(Constantes.FMT_ddMMyyy.format(fechaProlongadaInicio)).append("</td>");
                //this.cuerpoCorreo.append("<td width=\"100%\" height=\"20\" align=\"center\">").append(Constantes.FMT_ddMMyyy.format(fechaProlongadaFin)).append("</td>");
                this.cuerpoCorreo.append("</tr>");
            }
            retornar = true;
        }
        if (listaHuespedStaff != null && !listaHuespedStaff.isEmpty()) {
            //huespedes en staff
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td colspan=\"7\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Hospedados en Staff House</td>");
            this.cuerpoCorreo.append("</tr>");
            //titulos para tabla de hospedados en staff
            this.cuerpoCorreo.append("<tr>");
            this.cuerpoCorreo.append("<td colspan=\"1\" style=\"font-family:Georgia, 'Times New Roman', Times, serif;  color:#FFFFFF; font-size:12px; background-color:#CCC;\">Staff</td>");
            this.cuerpoCorreo.append("<td colspan=\"1\" style=\"font-family:Georgia, 'Times New Roman', Times, serif;  color:#FFFFFF; font-size:12px; background-color:#CCC;\">Habitación</td>");
            this.cuerpoCorreo.append("<td colspan=\"1\" style=\"font-family:Georgia, 'Times New Roman', Times, serif;  color:#FFFFFF; font-size:12px; background-color:#CCC;\">Huesped</td>");
            this.cuerpoCorreo.append("<td colspan=\"1\" style=\"font-family:Georgia, 'Times New Roman', Times, serif;  color:#FFFFFF; font-size:12px; background-color:#CCC;\">F. Inicio</td>");
            this.cuerpoCorreo.append("<td colspan=\"1\" style=\"font-family:Georgia, 'Times New Roman', Times, serif;  color:#FFFFFF; font-size:12px; background-color:#CCC;\">F. Fin</td>");
            this.cuerpoCorreo.append("<td colspan=\"2\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#CCC;\">F. Prolongada</td>");
            this.cuerpoCorreo.append("</tr>");
            for (HuespedVo huesped : listaHuespedStaff) {
                this.cuerpoCorreo.append("<tr>");
                this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\">").append(huesped.getNombreHuesped()).append("</td>");
                this.cuerpoCorreo.append("<td width=\"30%\" height=\"20\" align=\"center\">").append(huesped.getNombreSgStaff()).append("</td>");
                this.cuerpoCorreo.append("<td width=\"10%\" height=\"20\" align=\"center\">").append(huesped.getNombreHabitacion()).append("</td>");
                this.cuerpoCorreo.append("<td width=\"10%\" height=\"20\" align=\"center\">").append(Constantes.FMT_ddMMyyy.format(huesped.getFechaIngreso())).append("</td>");
                this.cuerpoCorreo.append("<td width=\"10%\" height=\"20\" align=\"center\">").append(Constantes.FMT_ddMMyyy.format(huesped.getFechaSalida())).append("</td>");
                this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\">").append("Del ").append(Constantes.FMT_ddMMyyy.format(fechaProlongadaFin)).append(" al ").append(Constantes.FMT_ddMMyyy.format(fechaProlongadaInicio)).append("</td>");
                //this.cuerpoCorreo.append("<td width=\"100%\" height=\"20\" align=\"center\">").append(Constantes.FMT_ddMMyyy.format(fechaProlongadaFin)).append("</td>");
                this.cuerpoCorreo.append("</tr>");
            }
            retornar = true;
        }
        this.cuerpoCorreo.append("</table><br/>");
        return retornar ? this.cuerpoCorreo : null;
    }

    
    public StringBuilder getHtmlNotificacionEstanciaProlongadaPorSolicitud(String asunto, StringBuilder contenido, String nombreRuta) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo(asunto));

        this.cuerpoCorreo.append("<p>El departamento de Gestión de Riesgos ha puesto en alerta la ruta <strong>").append(nombreRuta).append("</strong>.</p>");
        this.cuerpoCorreo.append("<p>Por el motivo anterior se han prolongado las estancias de los siguientes huespedes :</p>");

        this.cuerpoCorreo.append(contenido);

        this.cuerpoCorreo.append("<p>Para mas información contacta al Departamento de Gestión de Riesgos.</p>");

        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlNotificacionEstanciaProlongadaParaAnalista(String asunto, List<StringBuilder> listaCuerpos, String nombreRuta) {

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo(asunto));

        this.cuerpoCorreo.append("<p>El departamento de Gestión de Riesgos ha puesto en alerta la ruta <strong>").append(nombreRuta).append("</strong>.</p>");
        this.cuerpoCorreo.append("<p>Por el motivo anterior mencionado el <b>SIA</b> ha generado automáticamente nuevos registros para los huéspedes :</p>");
        this.cuerpoCorreo.append("<p style=\"font-family:Georgia, 'Times New Roman', Times, serif;  color:#FFFFFF; font-size:11px; color:gray;\">").append("(Clic aqui para entrar al ").append(Constantes.LINK_SIA).append(")</p>");

        for (StringBuilder sb : listaCuerpos) {
            this.cuerpoCorreo.append("<br/>");
            this.cuerpoCorreo.append(sb);
        }
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }
//    
//    public StringBuilder sendNotificacionEstanciaProlongadaParaHuespedForGerenteByGerencia(String asunto, String nombreGerente, String nombreRuta, List<String> huespedes) {
//
//        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
//        this.limpiarCuerpoCorreo();
//        this.cuerpoCorreo.append(plantilla.getInicio());
//        this.cuerpoCorreo.append(this.getTitulo(asunto));
//        this.cuerpoCorreo.append("<br/><p>Estimado(a) <b> ").append(nombreGerente).append("</b></p>");
//        this.cuerpoCorreo.append("<p>Seguridad ha cambiado el semáforo de control de viajes a color <b>").append("Negro").append("</b> ").append("para la ruta <b>").append(nombreRuta).append("</b>.</p>");
//
//        this.cuerpoCorreo.append("<p>Por el motivo anteriormente mencionado el SIA ha prolongado las estancias de los siguientes huéspedes: <br/>");
//
//        this.cuerpoCorreo.append("<table bordercolor=\"#000000\" border=\"0\" align=\"center\" width=\"50%\">");
//        this.cuerpoCorreo.append("<tr><th colspan=\"1\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Huéspedes</th></tr>");
//        for (String huesped : huespedes) {
//            this.cuerpoCorreo.append("<tr><td width=\"100%\" height=\"20\" align=\"center\">").append(huesped).append("</td></tr>");
//        }
//        this.cuerpoCorreo.append("</table><br/>");
//
//        this.cuerpoCorreo.append("<p>Para más información por favor ponte en contacto con los Analistas y con Seguridad.</p>");
//        this.cuerpoCorreo.append(plantilla.getFin());
//        return this.cuerpoCorreo;
//    }

    
    public StringBuilder getHtmlRegistroProlongadoSemaforoHuespedHotelStaff(String codigo, String nombreUsuario, String nombreStaffHotel, String nombreOficina, boolean isHotel) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Registro prolongado para huésped en el " + (isHotel ? "Hotel " : "Staff House") + " - " + nombreStaffHotel));
        this.cuerpoCorreo.append("<p>Estimado <strong>".concat(nombreUsuario).concat("</strong></p>"));
        this.cuerpoCorreo.append("<p>Se ha realizado un registro más para su estancia en el ").append(isHotel ? "Hotel " : "Staff House").append("<b> ".concat(nombreStaffHotel).concat(".</b>"));
        this.cuerpoCorreo.append(" Para más detalle sobre esta operación por favor contacte al departamento de Servicios Generale y Logística. </p>");
        this.cuerpoCorreo.append(" <br/><strong>Código de Solicitud : </strong> ").append(codigo);
        this.cuerpoCorreo.append("<br/>");

        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlNotificacionCambioOficinaVehiculo(SgVehiculo sgVehiculo, SgOficina sgOficinaDestino, String motivo, String nombreUsuario) {
        String bgGris = "bgcolor=#FAFAFA";
        String bgBlanco = "bgcolor=#ffffff";
        String f = "";

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Cambio de vehiculo de oficina"));
        //this.cuerpoCorreo.append("<p>Se ha reiniciado el kilometraje al vehiculo ").append(sgVehiculo.getSgMarca().getNombre()).append(sgVehiculo.getSgModelo().getNombre())
        //                      .append(" con placa numero de placa ").append(sgVehiculo.getNumeroPlaca()).append(".</p>");
        this.cuerpoCorreo.append("<br/><p>Se realizó un cambio de vehiculo de la oficina ");
        this.cuerpoCorreo.append("<strong>");
        this.cuerpoCorreo.append(sgVehiculo.getSgOficina().getNombre());
        this.cuerpoCorreo.append("</strong>");
        this.cuerpoCorreo.append(" a la oficina ");
        this.cuerpoCorreo.append("<strong>");
        this.cuerpoCorreo.append(sgOficinaDestino.getNombre());
        this.cuerpoCorreo.append(".</strong>");
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<table width=\"100%\" border=\"0\"  align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
        this.cuerpoCorreo.append("<tr><td valign=\"top\">");
        this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\" border=\"0\" align=\"left\">");
        //this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(sgVehiculo.getSgOficina().getNombre()).concat("</th></tr>"));

        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td width=\"30%\" align=\"center\"><strong>Placa</strong></td>");
        this.cuerpoCorreo.append("<td width=\"50%\" align=\"center\"><strong>Marca / Modelo</strong></td>");
        this.cuerpoCorreo.append("<td width=\"25%\" align=\"center\"><strong>Color</strong></td>");

        //this.cuerpoCorreo.append("<td width=\"22%\" height=\"20\" align=\"center\"><strong></strong></td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\"></th></tr>");
        this.cuerpoCorreo.append("<tr>");
        this.cuerpoCorreo.append("<td ".concat(f).concat(" width=\"30%\" height=\"20\" align=\"center\">").concat(sgVehiculo.getNumeroPlaca()).concat("</td>"));
        this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"50%\" height=\"20\" align=\"center\">").concat(sgVehiculo.getSgMarca().getNombre().concat(" / ").concat(sgVehiculo.getSgModelo().getNombre()).concat("</td>")));
        this.cuerpoCorreo.append("<td ".concat(f).concat("width=\"25%\" height=\"20\" align=\"center\">").concat(sgVehiculo.getSgColor().getNombre().concat("</td>")));
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("</td>");
        this.cuerpoCorreo.append("</tr>");
        this.cuerpoCorreo.append("</table>");
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<strong>Cambio :</strong>");
        this.cuerpoCorreo.append("<br/>".concat(nombreUsuario).concat(""));
        this.cuerpoCorreo.append("<br/><strong>Fecha : </strong>").append(Constantes.FMT_TextDate.format(new Date())).append(" a las ").append(Constantes.FMT_hmm_a.format(new Date()));
        this.cuerpoCorreo.append("<br/><strong>Motivo de cambio :</strong>");
        //this.cuerpoCorreo.append("<p style=\"font-size:11px;\">".concat(motivo).concat("</p>"));
        this.cuerpoCorreo.append("<br/>".concat(motivo).concat(""));
        this.cuerpoCorreo.append("<br/>");

        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;

    }

    
    public StringBuilder getHtmlAprobarSolicitaEstancia(SgSolicitudEstanciaVo sgSolicitudEstancia, List<DetalleEstanciaVO> detalle, String UsuarioAprueba) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = Constantes.FMT_ddMMyyy;
        String cad = "Aprobar";
        String link = "<center><a style = \"" + getBotonConfirmar() + "\" HREF=" + Configurador.urlSia() + "ServiciosGenerales/AUTSE?mg4merg235m=" + sgSolicitudEstancia.getId() + "&e3g9m93e=" + UsuarioAprueba + ">" + cad + "</a></center>";
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Aprobar estancia ".concat(sgSolicitudEstancia.getCodigo())));
        //this.cuerpoCorreo.append("<br/><p>Estimado <b> ".concat(nombre).concat("</b>"));

        //aqui ba el link para la aprobacion de la  estancia
        this.cuerpoCorreo.append("<center>".concat(link).concat("</center>"));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<p>Se realizó la solicitud de estancia <b> ").append(sgSolicitudEstancia.getCodigo()).append("</b> para la sede <b> ").append(sgSolicitudEstancia.getNombreSgOficina()).append("</b> favor de aprobarla, para continuar con el proceso. ");
        this.cuerpoCorreo.append("<table bordercolor=\"#000000\" cellspacing=\"8\" border=\"0\"  align=\"center\" width=\"80%\">"
                + "<tr><td valign=\"top\">"
                + "<table width=\"100%\"  align=\"center\" cellspacing=\"0\" cellpadding=\"1\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"6\"   bgcolor=\"#0099FF\">Datos de la solicitud</th></tr>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"20%\" align=\"center\"><strong>Inicio</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"20%\" align=\"center\"><strong>Fin</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"10%\" align=\"center\"><strong>Días</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"50%\" align=\"center\"><strong>Motivo</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"50%\" align=\"center\"><strong>Observación</strong></td>");
        this.cuerpoCorreo.append("</tr><tr>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat(sdf.format(sgSolicitudEstancia.getInicioEstancia())).concat("</td>"));
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat(sdf.format(sgSolicitudEstancia.getFinEstancia())).concat("</td>"));
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat("" + sgSolicitudEstancia.getDiasEstancia()).concat("</td>"));
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat("" + sgSolicitudEstancia.getNombreSgMotivo()).concat("</td>"));
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\">".concat("" + sgSolicitudEstancia.getObservacion()).concat("</td></tr> </table>"));
        this.cuerpoCorreo.append("</td></tr>");
        this.cuerpoCorreo.append("<tr><td valign=\"top\"> </td><tr>");
        this.cuerpoCorreo.append("<tr><td valign=\"top\"></td><tr>");
        this.cuerpoCorreo.append("<tr><td valign=\"top\">"
                + "<table width=\"100%\" align=\"center\" cellspacing=\"0\" cellpadding=\"1\">");
        this.cuerpoCorreo.append("<tr><th colspan=\"3\" bgcolor=\"#0099FF\">Empleado(s)/Invitado(s)</th></tr>");
        this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5;\" width=\"35%\" align=\"left\"><strong>Nombre</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"20%\" align=\"left\"><strong>Tipo</strong></td>");
        this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"45%\" align=\"left\"><strong>Descripción</strong></td></tr>");
        UtilLog4j.log.info(this, "Agregar detalles");
        for (DetalleEstanciaVO sgDetalleSolicitudEstancia : detalle) {
            if (sgDetalleSolicitudEstancia.getIdInvitado() == 0) {
                this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5;\" width=\"35%\">".concat(sgDetalleSolicitudEstancia.getUsuario()).concat("</td>"));
                this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"20%\">".concat(sgDetalleSolicitudEstancia.getTipoDetalle()).concat("</td>"));
                this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"45%\">".concat(validarNullHtml(sgDetalleSolicitudEstancia.getDescripcion())).concat("</td></tr>"));
            } else {
                this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5;\" width=\"35%\">".concat(validarNullHtml(sgDetalleSolicitudEstancia.getInvitado())).concat("</td>"));
                this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"20%\">".concat(validarNullHtml(sgDetalleSolicitudEstancia.getTipoDetalle())).concat("</td>"));
                this.cuerpoCorreo.append("<td style=\"border: 1px solid #b5b5b5;\" width=\"45%\">".concat(validarNullHtml(sgDetalleSolicitudEstancia.getDescripcion())).concat("</td></tr>"));
            }
        }
        this.cuerpoCorreo.append("</td></tr></table></td></tr></table>");
        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    public StringBuilder getHtmlAvisoVencimientoLicenciaSemanal(List<LicenciaVo> l) {
        UtilLog4j.log.info(this, "Armando el correo de vencimiento de licencias semanal");
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Licencias a Vencer del " + Constantes.FMT_TextDate.format(new Date()) + " al " + Constantes.FMT_TextDate.format(siManejoFechaLocal.fechaSumarMes(new Date(), Constantes.MESES_PREVIOS))));

        this.cuerpoCorreo.append("<p>Licencias a vencer por oficina</p>");
        int idoficina = 0;
        int count = 0;
        if (l.size() > 0) {
            for (LicenciaVo lic : l) {
                if (count == 0 && idoficina == 0) {
                    this.cuerpoCorreo.append("<p></p>");
                    this.cuerpoCorreo.append("<table width=\"80%\" border=\"0\"  align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
                    this.cuerpoCorreo.append("<tr><td valign=\"top\">");
                    this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\" border=\"0\" align=\"left\">");
                    this.cuerpoCorreo.append("<tr><th colspan=\"4\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(lic.getNombreOficina()).concat("</th></tr>"));
                    this.cuerpoCorreo.append("<tr>");
                    this.cuerpoCorreo.append("<td width=\"35%\" align=\"center\"><strong>Usuario</strong></td>");
                    this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\"><strong>No. Licencia<strong></strong></td>");
                    this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\"><strong>País</strong></td>");
                    this.cuerpoCorreo.append("<td width=\"25%\" height=\"20\" align=\"center\"><strong>Fecha Vencimiento</strong></td>");
                    this.cuerpoCorreo.append("</tr>");
                    this.cuerpoCorreo.append("<tr><th colspan=\"4\" scope=\"col\"  bgcolor=\"#CDE4F6\"></th></tr>");
                    this.cuerpoCorreo.append("<tr>");
                    idoficina = lic.getIdOficina();
                    count++;
                }
                if (idoficina != lic.getIdOficina()) {
                    this.cuerpoCorreo.append("</td>");
                    this.cuerpoCorreo.append("</tr>");
                    this.cuerpoCorreo.append("</table>");
                    this.cuerpoCorreo.append("</td>");
                    this.cuerpoCorreo.append("</tr>");
                    this.cuerpoCorreo.append("</table>");

                    this.cuerpoCorreo.append("<p></p>");
                    this.cuerpoCorreo.append("<table width=\"80%\" border=\"0\"  align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
                    this.cuerpoCorreo.append("<tr><td valign=\"top\">");
                    this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\" border=\"0\" align=\"left\">");
                    this.cuerpoCorreo.append("<tr><th colspan=\"4\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(lic.getNombreOficina()).concat("</th></tr>"));
                    this.cuerpoCorreo.append("<tr>");
                    this.cuerpoCorreo.append("<td width=\"35%\" align=\"center\"><strong>Usuario</strong></td>");
                    this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\"><strong>No. Licencia<strong></strong></td>");
                    this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\"><strong>País</strong></td>");
                    this.cuerpoCorreo.append("<td width=\"25%\" height=\"20\" align=\"center\"><strong>Fecha Vencimiento</strong></td>");
                    this.cuerpoCorreo.append("</tr>");
                    this.cuerpoCorreo.append("<tr><th colspan=\"4\" scope=\"col\"  bgcolor=\"#CDE4F6\"></th></tr>");
                    this.cuerpoCorreo.append("<tr>");
                    this.cuerpoCorreo.append("<td width=\"35%\" height=\"20\" align=\"center\">").append(lic.getUsuario()).append("</td>");
                    this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\">").append(lic.getNumero()).append("</td>");
                    this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\">").append(lic.getPais()).append("</td>");
                    this.cuerpoCorreo.append("<td width=\"25%\" height=\"20\" align=\"center\">").append(Constantes.FMT_yyyyMMdd.format(lic.getVencimiento())).append("</td>");
                    this.cuerpoCorreo.append("</tr>");
                    idoficina = lic.getIdOficina();
                } else {
                    this.cuerpoCorreo.append("<td width=\"35%\" height=\"20\" align=\"center\">").append(lic.getUsuario()).append("</td>");
                    this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\">").append(lic.getNumero()).append("</td>");
                    this.cuerpoCorreo.append("<td width=\"20%\" height=\"20\" align=\"center\">").append(lic.getPais()).append("</td>");
                    this.cuerpoCorreo.append("<td width=\"25%\" height=\"20\" align=\"center\">").append(Constantes.FMT_yyyyMMdd.format(lic.getVencimiento())).append("</td>");
                    this.cuerpoCorreo.append("</tr>");
                }

            }

            this.cuerpoCorreo.append("</td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("</table>");
            this.cuerpoCorreo.append("</td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("</table>");
        }

        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlAvisoVencimientoCursoManejo(List<CursoManejoVo> list) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Cursos de manejo a vencer del " + Constantes.FMT_TextDate.format(new Date()) + " al " + Constantes.FMT_TextDate.format(siManejoFechaLocal.fechaSumarMes(new Date(), Constantes.MESES_PREVIOS))));

        this.cuerpoCorreo.append("<p>Cursos de manejo a vencer por oficina</p>");
        int idoficina = 0;
        int count = 0;

        if (list.size() > 0) {
            for (CursoManejoVo lic : list) {
                if (count == 0 && idoficina == 0) {
                    this.cuerpoCorreo.append("<p></p>");
                    this.cuerpoCorreo.append("<table width=\"80%\" border=\"0\"  align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
                    this.cuerpoCorreo.append("<tr><td valign=\"top\">");
                    this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\" border=\"0\" align=\"left\">");
                    this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(lic.getOficina()).concat("</th></tr>"));
                    this.cuerpoCorreo.append("<tr>");
                    this.cuerpoCorreo.append("<td width=\"35%\" align=\"center\"><strong>Usuario</strong></td>");
                    this.cuerpoCorreo.append("<td width=\"30%\" height=\"20\" align=\"center\"><strong>No. Curso Manejo<strong></strong></td>");
                    this.cuerpoCorreo.append("<td width=\"30%\" height=\"20\" align=\"center\"><strong>Fecha Vencimiento</strong></td>");
                    this.cuerpoCorreo.append("</tr>");
                    this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\"></th></tr>");
                    this.cuerpoCorreo.append("<tr>");
                    idoficina = lic.getIdSgOficina();
                    count++;
                }
                if (idoficina != lic.getIdSgOficina()) {
                    this.cuerpoCorreo.append("</td>");
                    this.cuerpoCorreo.append("</tr>");
                    this.cuerpoCorreo.append("</table>");
                    this.cuerpoCorreo.append("</td>");
                    this.cuerpoCorreo.append("</tr>");
                    this.cuerpoCorreo.append("</table>");

                    this.cuerpoCorreo.append("<p></p>");
                    this.cuerpoCorreo.append("<table width=\"80%\" border=\"0\"  align=\"center\" cellspacing=\"8\" bordercolor=\"#000000\">");
                    this.cuerpoCorreo.append("<tr><td valign=\"top\">");
                    this.cuerpoCorreo.append("<table width=\"100%\" height=\"0\" border=\"0\" align=\"left\">");
                    this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\">".concat(lic.getOficina()).concat("</th></tr>"));
                    this.cuerpoCorreo.append("<tr>");
                    this.cuerpoCorreo.append("<td width=\"35%\" align=\"center\"><strong>Usuario</strong></td>");
                    this.cuerpoCorreo.append("<td width=\"30%\" height=\"20\" align=\"center\"><strong>No. Curso Manejo<strong></strong></td>");
                    this.cuerpoCorreo.append("<td width=\"30%\" height=\"20\" align=\"center\"><strong>Fecha Vencimiento</strong></td>");
                    this.cuerpoCorreo.append("</tr>");
                    this.cuerpoCorreo.append("<tr><th colspan=\"3\" scope=\"col\"  bgcolor=\"#CDE4F6\"></th></tr>");
                    this.cuerpoCorreo.append("<tr>");
                    this.cuerpoCorreo.append("<td width=\"35%\" height=\"20\" align=\"center\">").append(lic.getNameUser()).append("</td>");
                    this.cuerpoCorreo.append("<td width=\"30%\" height=\"20\" align=\"center\">").append(lic.getNumCurso()).append("</td>");
                    this.cuerpoCorreo.append("<td width=\"30%\" height=\"20\" align=\"center\">").append(Constantes.FMT_yyyyMMdd.format(lic.getFechaVencimiento())).append("</td>");
                    this.cuerpoCorreo.append("</tr>");
                    idoficina = lic.getIdSgOficina();
                } else {
                    this.cuerpoCorreo.append("<td width=\"35%\" height=\"20\" align=\"center\">").append(lic.getNameUser()).append("</td>");
                    this.cuerpoCorreo.append("<td width=\"30%\" height=\"20\" align=\"center\">").append(lic.getNumCurso()).append("</td>");
                    this.cuerpoCorreo.append("<td width=\"30%\" height=\"20\" align=\"center\">").append(Constantes.FMT_yyyyMMdd.format(lic.getFechaVencimiento())).append("</td>");
                    this.cuerpoCorreo.append("</tr>");
                }

            }

            this.cuerpoCorreo.append("</td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("</table>");
            this.cuerpoCorreo.append("</td>");
            this.cuerpoCorreo.append("</tr>");
            this.cuerpoCorreo.append("</table>");
        }

        //Aquí va todo el contenido del cuerpo,
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlAvisoVencimientoLicenciaByUser(LicenciaVo l) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Licencia de manejo a vencer el " + Constantes.FMT_TextDate.format(l.getVencimiento())));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<table width=\"98%\" border=\"0\" align=\"left\"> <tr> <td width=\"90%\" valign=\"middle\" align=\"left\">");
        this.cuerpoCorreo.append("<p>Le informamos que su licencia de manejo esta por expirar el dia <strong>").append(Constantes.FMT_TextDate.format(l.getVencimiento())).append("</strong>.</p>");
        this.cuerpoCorreo.append("<p>De no renovar en tiempo y forma, la unidad de trabajo asignada le será retirada, esto hasta cumplir con las políticas internas de la empresa</p> ");
        this.cuerpoCorreo.append("<p>Favor de renovarla y enviar una copia al departamento de Servicios Generales para su debida actualizacion en el sistema.</p>");
        this.cuerpoCorreo.append("</td> <td>");
        this.cuerpoCorreo.append("<img src='cid:logoWarning'   width=\"90\" height=\"90\" style=\"padding:0px 10px 0px 10px; border :none;\"/>");
        this.cuerpoCorreo.append("</td> </tr> </table>");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlAvisoVencimientoCursoManejoByUser(CursoManejoVo l) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(this.getTitulo("Curso de manejo a vencer el " + Constantes.FMT_TextDate.format(l.getFechaVencimiento())));
        this.cuerpoCorreo.append("<br/>");
        this.cuerpoCorreo.append("<table width=\"98%\" border=\"0\" align=\"left\"> <tr> <td width=\"90%\" valign=\"middle\" align=\"left\">");
        this.cuerpoCorreo.append("<p>Le informamos que su curso de manejo esta por expirar el dia <strong>").append(Constantes.FMT_TextDate.format(l.getFechaVencimiento())).append("</strong></p>");
        this.cuerpoCorreo.append("<p>De no renovar en tiempo y forma, la unidad de trabajo asignada le será retirada, esto hasta cumplir con las políticas internas de la empresa.</p> ");
        this.cuerpoCorreo.append("<p>Favor de agendar una fecha para su renovación.</p>");
        this.cuerpoCorreo.append("</td> <td>");
        this.cuerpoCorreo.append("<img src='cid:logoWarning'   width=\"90\" height=\"90\" style=\"padding:0px 10px 0px 10px; border :none;\"/>");
        this.cuerpoCorreo.append("</td> </tr> </table>");

        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }
}
