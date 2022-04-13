/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.service.notificacion;

import java.text.SimpleDateFormat;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import sia.constantes.Constantes;
import sia.correo.impl.CodigoHtml;
import sia.correo.impl.EnviarCorreoImpl;
import sia.modelo.SiPlantillaHtml;
import sia.modelo.Usuario;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.inventarios.DetalleSolicitudMaterialAlmacenVo;
import sia.modelo.vo.inventarios.SolicitudMaterialAlmacenVo;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiPlantillaHtmlImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;

/**
 *
 * @author mluis
 */
@Stateless 
public class NotificacionInventarioImpl extends CodigoHtml {

    @Inject
    EnviarCorreoImpl enviarCorreoRemote;
    @Inject
    UsuarioImpl usuarioRemote;
    @Inject
    SiParametroImpl siParametroRemote;
    @Inject
    SiPlantillaHtmlImpl plantillaHtml;
    @Inject
    SiUsuarioRolImpl siUsuarioRolRemote;

    
    public boolean enviarCorreoAutorizarMaterial(SolicitudMaterialAlmacenVo solicitudMaterialAlmacenVo, String idUsuarioNotifica) {
        boolean v;
        UsuarioVO uVo = usuarioRemote.findById(idUsuarioNotifica);
        System.out.println("correo auto: " + uVo.getMail());
        v = enviarCorreoRemote.enviarCorreoIhsa(uVo.getMail(), solicitudMaterialAlmacenVo.getCorreoSolicita(),
                "",
                "Autorizar materiales -- " + solicitudMaterialAlmacenVo.getFolio(),
                mensajeAutorizaMaterial(solicitudMaterialAlmacenVo), siParametroRemote.find(1).getLogo());
        return v;
    }

    private StringBuilder mensajeAutorizaMaterial(SolicitudMaterialAlmacenVo smav) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = Constantes.FMT_ddMMyyy;
        StringBuilder sb = new StringBuilder();

        sb.append(plantilla.getInicio());
        sb.append(getTitulo("Autorizar materiales -- ".concat(smav.getFolio())));
        sb.append("<br/>");
        sb.append("<p>Se realizó la solicitud de materiales <b> ").append(smav.getFolio())
                .append("</b> para el almacén <b> ").append(smav.getAlmacen())
                .append("</b> favor de autorizar.")
                .append("<br/><table width=\"100%\"  align=\"center\" cellspacing=\"0\" cellpadding=\"1\">")
                .append("<tr><th  bgcolor=\"#0099FF\" colspan=\"3\">Datos de la solicitud</th></tr>")
                .append("<tr> <td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>Folio</b></td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>Solicita</b></td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>F. Requiere</b></td>")
                .append("</tr><tr>")
                .append("<td style=\"border: 1px solid #b5b5b5;\">").append(smav.getFolio()).append("</td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\">").append(smav.getSolicita()).append("</td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\">").append(sdf.format(smav.getFechaRequiere())).append("</td>")
                .append("</tr></table>")
                .append(materiales(smav.getMateriales()));
        //Aquí va todo el contenido del cuerpo,
        sb.append(plantilla.getFin());
        return sb;
    }

    
    public boolean enviarCorreoAutorizarSolicitud(SolicitudMaterialAlmacenVo solicitudMaterialAlmacenVo, int idCampo) {
        boolean v;
        String correoRol = siUsuarioRolRemote.traerCorreosPorCodigoRolList(Constantes.ROL_ENTREGA_MAT, idCampo);
        v = enviarCorreoRemote.enviarCorreoIhsa(correoRol, "",
                "",
                "Entregar materiales -- " + solicitudMaterialAlmacenVo.getFolio(),
                entregarMaterial(solicitudMaterialAlmacenVo), siParametroRemote.find(1).getLogo());
        return v;
    }

    private StringBuilder entregarMaterial(SolicitudMaterialAlmacenVo smav) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = Constantes.FMT_ddMMyyy;
        StringBuilder sb = new StringBuilder();

        sb.append(plantilla.getInicio());
        sb.append(getTitulo("Entregar materiales -- ".concat(smav.getFolio())));
        sb.append("<br/>");
        sb.append("<p>Se autorizo la solicitud de materiales <b> ").append(smav.getFolio())
                .append("</b> favor de entregar el material al personal.")
                .append("<br/><table width=\"100%\"  align=\"center\" cellspacing=\"0\" cellpadding=\"1\">")
                .append("<tr><th  bgcolor=\"#0099FF\" colspan=\"3\">Datos de la solicitud</th></tr>")
                .append("<tr> <td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>Folio</b></td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>Solicita</b></td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>F. Requiere</b></td>")
                .append("</tr><tr>")
                .append("<td style=\"border: 1px solid #b5b5b5;\">").append(smav.getFolio()).append("</td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\">").append(smav.getSolicita()).append("</td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(sdf.format(smav.getFechaRequiere())).append("</td>")
                .append("</tr></table>")
                .append(materiales(smav.getMateriales()));
        //Aquí va todo el contenido del cuerpo,
        sb.append(plantilla.getFin());
        return sb;
    }

    private StringBuilder materiales(List<DetalleSolicitudMaterialAlmacenVo> materiales) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p><b>Materiales solicitados</b></p>")
                .append("<br/><table width=\"100%\"  align=\"center\" cellspacing=\"0\" cellpadding=\"1\">")
                .append("<tr><th  bgcolor=\"#0099FF\" colspan=\"3\">Materiales</th></tr>")
                .append("<tr> <td  style=\"border: 1px solid #b5b5b5;\" width=\"70%\" align=\"center\"><b>Descripción del material</b></td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\" width=\"20%\" align=\"center\"><b>Unidad</b></td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\" width=\"10%\" align=\"center\"><b>Cantidad</b></td>")
                .append("</tr>");
        for (DetalleSolicitudMaterialAlmacenVo materiale : materiales) {
            sb.append("<tr>")
                    .append("<td width=\"70%\" style=\"border: 1px solid #b5b5b5;\">").append(materiale.getArticulo()).append("</td>")
                    .append("<td width=\"20%\" style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(materiale.getUnidad()).append("</td>")
                    .append("<td width=\"10%\" style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(materiale.getCantidadRecibida()).append("</td>")
                    .append("</tr>");
        }
        sb.append("</table>");
        return sb;
    }

    
    public boolean enviarCorreoRechazoSolicitud(SolicitudMaterialAlmacenVo solicitudMaterialAlmacenVo, String motivo, int idCampo) {
        boolean v;
        v = enviarCorreoRemote.enviarCorreoIhsa(solicitudMaterialAlmacenVo.getCorreoSolicita(), "",
                "",
                "Rechazo de solicitud -- " + solicitudMaterialAlmacenVo.getFolio(),
                rechazarSolicitud(solicitudMaterialAlmacenVo, motivo), siParametroRemote.find(1).getLogo());
        return v;
    }

    private StringBuilder rechazarSolicitud(SolicitudMaterialAlmacenVo smav, String motivo) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = Constantes.FMT_ddMMyyy;
        StringBuilder sb = new StringBuilder();

        sb.append(plantilla.getInicio());
        sb.append(getTitulo("Rechazo de la solcitud -- ".concat(smav.getFolio())));
        sb.append("<br/>");
        sb.append("<p>Se <b>rechazó</b> la solicitud de materiales <b> ").append(smav.getFolio())
                .append("</b>, por el siguiente motivo.</p>")
                .append("<p><b>Motivo: </b>").append(motivo).append("</p>")
                .append("<br/><table width=\"100%\"  align=\"center\" cellspacing=\"0\" cellpadding=\"1\">")
                .append("<tr><th  bgcolor=\"#0099FF\" colspan=\"3\">Datos de la solicitud</th></tr>")
                .append("<tr> <td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>Folio</b></td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>Solicita</b></td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>F. Requiere</b></td>")
                .append("</tr><tr>")
                .append("<td style=\"border: 1px solid #b5b5b5;\">").append(smav.getFolio()).append("</td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\">").append(smav.getSolicita()).append("</td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\">").append(sdf.format(smav.getFechaRequiere())).append("</td>")
                .append("</tr></table>")
                .append(materiales(smav.getMateriales()));
        //Aquí va todo el contenido del cuerpo,
        sb.append(plantilla.getFin());
        return sb;
    }

    
    public boolean enviarCorreoEntregaMaterial(SolicitudMaterialAlmacenVo smVo, UsuarioVO sesion) {
        boolean v;
        v = enviarCorreoRemote.enviarCorreoIhsa(smVo.getCorreoSolicita(), sesion.getMail(),
                "",
                "Materiales entregados -- " + smVo.getFolio(),
                entregaFisicaMaterial(smVo), siParametroRemote.find(1).getLogo());
        return v;
    }

    private StringBuilder entregaFisicaMaterial(SolicitudMaterialAlmacenVo smav) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        StringBuilder sb = new StringBuilder();

        sb.append(plantilla.getInicio());
        sb.append(getTitulo("Materiales entregados -- ".concat(smav.getFolio())));
        sb.append("<br/>");
        sb.append("<p>Se entregaron los materiales solicitados.")
                .append("<br/><table width=\"100%\"  align=\"center\" cellspacing=\"0\" cellpadding=\"1\">")
                .append("<tr><th  bgcolor=\"#0099FF\" colspan=\"3\">Datos de la solicitud</th></tr>")
                .append("<tr> <td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>Folio</b></td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>Solicita</b></td>")
                .append("</tr><tr>")
                .append("<td style=\"border: 1px solid #b5b5b5;\">").append(smav.getFolio()).append("</td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\">").append(smav.getSolicita()).append("</td>")
                .append("</tr></table>")
                .append(materiales(smav.getMateriales()));
        //Aquí va todo el contenido del cuerpo,
        sb.append(plantilla.getFin());
        return sb;
    }

    
    public boolean enviarCorreoCambioSolicitudMaterial(List<SolicitudMaterialAlmacenVo> solicitudes, Usuario usTenia, Usuario usApr, String correoSesion) {
        boolean v;
        String asunto = "Cambio de solicitudes de material  ";
        v = enviarCorreoRemote.enviarCorreoIhsa(usTenia.getEmail(), usApr.getEmail(),
                correoSesion,
                asunto,
                correoCambioSolicitud(solicitudes, usTenia.getNombre(), usApr.getNombre(), asunto), siParametroRemote.find(1).getLogo());
        return v;
    }

    private StringBuilder correoCambioSolicitud(List<SolicitudMaterialAlmacenVo> solicitudes, String nombreTenia, String nombreAprobara, String asunto) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        this.cuerpoCorreo.append(getTitulo(asunto));
        SimpleDateFormat sdf = Constantes.FMT_ddMMyyy;

        cuerpoCorreo.append("<br/><p>La lista de solicitudes de material pendientes de <b> autorizar </b> por <b> ").append(nombreTenia).append("</b>");
        cuerpoCorreo.append(", se pasaron a la bandeja de <b>").append(nombreAprobara).append("</b> para continuar con el proceso de aprobación. </p>");
        this.cuerpoCorreo.append("<br/>");
        //lista de sol viaje
        cuerpoCorreo.append("<br/><table width=\"100%\"  align=\"center\" cellspacing=\"0\" cellpadding=\"1\">")
                .append("<tr><th  bgcolor=\"#0099FF\" colspan=\"3\">Datos de la solicitud</th></tr>")
                .append("<tr> <td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>Folio</b></td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>Solicita</b></td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>F. Requiere</b></td>")
                .append("</tr>");
        for (SolicitudMaterialAlmacenVo solicitude : solicitudes) {
            cuerpoCorreo.append("<tr>")
                    .append("<td style=\"border: 1px solid #b5b5b5;\">").append(solicitude.getFolio()).append("</td>")
                    .append("<td style=\"border: 1px solid #b5b5b5;\">").append(solicitude.getSolicita()).append("</td>")
                    .append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\">").append(sdf.format(solicitude.getFechaRequiere())).append("</td>")
                    .append("</tr>");
        }
        cuerpoCorreo.append("</table>");
        //Pie del correo
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public boolean enviarCorreoCancelaSolicitud(SolicitudMaterialAlmacenVo solicitudMaterialAlmacenVo, String motivo, int idCampo) {
        boolean v;
        v = enviarCorreoRemote.enviarCorreoIhsa(solicitudMaterialAlmacenVo.getCorreoSolicita(), "",
                "",
                "Cancelación de solicitud -- " + solicitudMaterialAlmacenVo.getFolio(),
                cancelaSolicitud(solicitudMaterialAlmacenVo, motivo), siParametroRemote.find(1).getLogo());
        return v;
    }

    private StringBuilder cancelaSolicitud(SolicitudMaterialAlmacenVo smav, String motivo) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        SimpleDateFormat sdf = Constantes.FMT_ddMMyyy;
        StringBuilder sb = new StringBuilder();

        sb.append(plantilla.getInicio());
        sb.append(getTitulo("Solcitud cancelada -- ".concat(smav.getFolio())));
        sb.append("<br/>");
        sb.append("<p>Se <b>cancela</b> la solicitud de materiales <b> ").append(smav.getFolio())
                .append("</b>, por el siguiente motivo.</p>")
                .append("<p><b>Motivo: </b>").append(motivo).append("</p>")
                .append("<br/><table width=\"100%\"  align=\"center\" cellspacing=\"0\" cellpadding=\"1\">")
                .append("<tr><th  bgcolor=\"#0099FF\" colspan=\"3\">Datos de la solicitud</th></tr>")
                .append("<tr> <td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>Folio</b></td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>Solicita</b></td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\" align=\"center\"><b>F. Requiere</b></td>")
                .append("</tr><tr>")
                .append("<td style=\"border: 1px solid #b5b5b5;\">").append(smav.getFolio()).append("</td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\">").append(smav.getSolicita()).append("</td>")
                .append("<td style=\"border: 1px solid #b5b5b5;\">").append(sdf.format(smav.getFechaRequiere())).append("</td>")
                .append("</tr></table>")
                .append(materiales(smav.getMateriales()));
        //Aquí va todo el contenido del cuerpo,
        sb.append(plantilla.getFin());
        return sb;
    }
}
