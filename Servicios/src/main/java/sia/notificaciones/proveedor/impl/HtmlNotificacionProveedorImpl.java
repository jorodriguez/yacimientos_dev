/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.proveedor.impl;

import java.math.RoundingMode;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.correo.impl.CodigoHtml;
import sia.modelo.SiPlantillaHtml;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sistema.vo.FacturaVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.sistema.impl.SiPlantillaHtmlImpl;

/**
 *
 * @author mluis
 */
@LocalBean 
public class HtmlNotificacionProveedorImpl extends CodigoHtml{

    @Inject
    private SiPlantillaHtmlImpl plantillaHtml;

    
    public StringBuilder mensageCorreoNotificacion(int consecutivo, String rfc, String clave) {
        String cuerpoMensaje = "";
        this.cuerpoCorreo.delete(0, this.cuerpoCorreo.length());
        cuerpoMensaje += getHtmlEncabezado();
        cuerpoMensaje += "<p>Buenos días,";
        cuerpoMensaje += "<br/><br/><br/>"
                + "<p>"
                + "<br/>"
                + "<b>Favor de verificar la siguiente notificación.</b>"
                + "<br/>"
                + "Para abrir el archivo. <a href =\"" + Configurador.urlSia() + "Proveedor/OFWS?CC="
                + "" + consecutivo + "&ZWZ4W=" + rfc + "&Z4BX2=" + clave + "\">Clic aqui</a></center>"
                + "</p>";
        cuerpoMensaje += this.htmlPie;
        return this.cuerpoCorreo.append(cuerpoMensaje);
    }

    
    public StringBuilder mensageCorreo(String rfc, String pass) {
        String cuerpoMensaje = "";
        this.cuerpoCorreo.delete(0, this.cuerpoCorreo.length());
        cuerpoMensaje += getHtmlEncabezado();
        cuerpoMensaje += "<p>Buenos días, ".concat(rfc);
        cuerpoMensaje += "<br/><br/><br/>"
                + "<b>Para ofrecerle un mejor servicio, le pedimos por favor actualice sus datos.<br/>"
                + "</b>"
                + "<br/> <table aling =  \"center\" width=\"350\"" + " border = \"1\" >"
                + "<thead>"
                + "<tr>"
                + "<td>RFC</td>"
                + "<td>PASSWORD</td>"
                + "</tr>"
                + "</thead>"
                + "<tr>"
                + "<td>" + rfc + "</td>"
                + "<td>" + pass + "</td>"
                + "</tr>"
                + "</table><br/><br/>"
                + "<p>"
                + "Para completar el registro de información. <a href =\"" + Configurador.urlSia() + "Proveedor\">Clic aqui</a></center>"
                + "</p>";
        cuerpoMensaje += this.htmlPie;
        return this.cuerpoCorreo.append(cuerpoMensaje);
    }

    
    public StringBuilder correoF(ProveedorVo proveedorVo) {
        String cuerpoMensaje = "";
        this.cuerpoCorreo.delete(0, this.cuerpoCorreo.length());
        cuerpoMensaje += getHtmlEncabezado();
        cuerpoMensaje += "<br/><br/>"
                + "<p> Se agregó el proveedor <b> " + proveedorVo.getNombre() + " </b>" + " para ser utilizado en todos los procesos del SIA <p/> ";
        //
        cuerpoMensaje += datosProveedor(proveedorVo);
        cuerpoMensaje += htmlPie;
        return this.cuerpoCorreo.append(cuerpoMensaje);
    }

    private String datosProveedor(ProveedorVo proveedorVo) {
        StringBuilder proveedor = new StringBuilder();
        String c15 = "<td bgcolor=#ffffff valign=middle style=\"text-align:left; width:20%;\"><font face=arial size=-1>";

        proveedor.append("<table style=\"width:60%\">");
        proveedor.append("<tr>");
        proveedor.append("<td style=\"background-color:#A8CEF0\" colspan=\"2\">");
        proveedor.append("<font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;text-align:center;\">");
        proveedor.append("<b>PROVEEDOR</b>");
        proveedor.append("</font></td>");
        proveedor.append("</tr>");
        proveedor.append(c15).append("Nombre </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        proveedor.append(validarNullHtml(proveedorVo.getNombre())).append("</b></font></td></tr>");

        proveedor.append(c15).append("Dirección </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        proveedor.append(validarNullHtml(proveedorVo.getCalle())).append(" #").append(proveedorVo.getNumero()).append("</b></font></td></tr>");

        proveedor.append("<tr>").append(c15).append("Colonia </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        proveedor.append(validarNullHtml(proveedorVo.getColonia())).append("</b></font></td></TR><TR>");
        proveedor.append(c15).append("Ciudad </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        proveedor.append(validarNullHtml(proveedorVo.getCiudad())).append(" C.P.").append(validarNullHtml(proveedorVo.getCodigoPostal())).append("</b></font></td></tr>");

        proveedor.append("<tr>").append(c15).append("Estado </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        proveedor.append(validarNullHtml(proveedorVo.getEstado())).append("</b></font></td></TR><TR>");
        proveedor.append(c15).append("País</td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        proveedor.append(validarNullHtml(proveedorVo.getPais())).append("</b></font></td></tr>");
        proveedor.append("</table>");
        return proveedor.toString();
    }

    
    public StringBuilder mensageCorreoRecordatorio(ProveedorVo proveedorVo, String observacion) {
        String cuerpoMensaje = "";
        this.cuerpoCorreo.delete(0, this.cuerpoCorreo.length());
        cuerpoMensaje += getHtmlEncabezado();
        cuerpoMensaje += "<p>Buenos días, ".concat(proveedorVo.getNombre());
        cuerpoMensaje += "<br/>"
                + "<br/><br/>"
                + "<table   class =\"" + this.t + "\"" + "aling = \"center\"" + "width=\"650\"" + " border = \"0\" >"
                + "<tr>"
                + "<td>"
                + observacion
                + "</td>"
                + "</tr>"
                + "</table>"
                + "<p>"
                + "Saludos."
                + "</p>"
                + "<p>"
                + "Para ofrecerle un mejor servicio, le pedimos mantenga sus datos actualizados. <a href =\"" + Configurador.urlSia() + "Proveedor\">Clic aquí</a></center>"
                + "</p>";
        cuerpoMensaje += this.htmlPie;
        return this.cuerpoCorreo.append(cuerpoMensaje);
    }

    
    public StringBuilder mensajeCambioClave(String nuevoPass, ProveedorVo proveedor) {
        String cuerpoMensaje = "";
        this.cuerpoCorreo.delete(0, this.cuerpoCorreo.length());
        cuerpoMensaje += getHtmlEncabezado();
        cuerpoCorreo.append(this.getTitulo("Reinicio de contraseña"))
                .append("Estimado, <b> ")
                .append(proveedor.getNombre())
                .append("</b><br/><br/><p>Su nueva contraseña para ingreso al portal de proveedores es la siguiente.")
                .append("<br/>Contraseña: ")
                .append("<b>")
                .append(nuevoPass)
                .append("</b>")
                .append("<Br/><Br/>Por seguridad y comodidad le recomendamos cambiar su contraseña.")
                .append("<Br/><Br/> Gracias, <Br/> El equipo del SIA.");
        cuerpoMensaje += this.htmlPie;
        return this.cuerpoCorreo.append(cuerpoMensaje);
    }

    
    public StringBuilder mensajeProveedorProceso(ProveedorVo proveedorVo, UsuarioVO sesion) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio())
                .append(getTitulo("Registro de proveedor " + proveedorVo.getRfc()))
                .append("Buen día, <br/>")
                .append("<p><b>").append(sesion.getNombre()).append("</b> solicita registrar en el siguiente proveedor en NAVISION. </p>")
                .append("<p><b>").append(proveedorVo.getTipoProveedor() == Constantes.UNO ? "Es propietatrio" : "").append("</b></p>")
                .append(datosProveedor(proveedorVo))
                .append("<p>Gracias. </p>")
                .append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder mensajeProveedorDevuelto(ProveedorVo proveedorVo, String motivo) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio())
                .append(getTitulo("Devolución de proveedor " + proveedorVo.getRfc()))
                .append("Buen día, <br/>")
                .append("<p>Se devuelve el proveedor: </p>")
                .append(datosProveedor(proveedorVo))
                .append("<p>Motivo:<b> ").append(motivo).append("</b></p>")
                .append("<p>Gracias. </p>")
                .append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder mensajeEnvioFactura(FacturaVo facturaVo, ProveedorVo proveedorVo, OrdenVO compraVo) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio())
                .append(getTitulo("Envío factura: " + compraVo.getNavCode() + " - " + facturaVo.getFolio()))
                .append("Buen día, <br/>")
                .append("<p>Se envía para aceptación y revisión la carta de <b>Contenido Nacional </b> y la factura con folio: <b> ").append(facturaVo.getFolio()).append("</b></p>")
                .append(datosFactura(facturaVo))
                .append(datosProveedor(proveedorVo))
                .append("<p>Gracias. </p>")
                .append(plantilla.getFin());
        return cuerpoCorreo;
    }

    private String datosFactura(FacturaVo facturaVo) {
        StringBuilder sb = new StringBuilder();
        sb.append("<br/><table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\" >");
        sb.append("<thead><tr><th colspan=\"5\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman\', Times, serif; font-weight:bold; color:#FFFFFF; font-size:16px; background-color:#0099FF;\">Datos de la factura</th></tr>");
        sb.append("<tr><th style=\"").append(getEstiloTitulo()).append("\">Folio </th>");
        sb.append("<th style=\"").append(getEstiloTitulo()).append("\">Concepto </th>");
        sb.append("<th style=\"").append(getEstiloTitulo()).append("\">Monto</th>");
        sb.append("<th style=\"").append(getEstiloTitulo()).append("\">Emisión</th>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<td height=\"18\"  style=\"").append(getEstiloContenido()).append("\" align=\"center\">");
        sb.append(validarNullHtml(facturaVo.getFolio())).append("</td>");
        sb.append("<td height=\"18\"  style=\"").append(getEstiloContenido()).append(" \" align=\"left\">");
        sb.append(validarNullHtml(facturaVo.getConcepto())).append("</td>");
        sb.append("<td height=\"18\"  style=\"").append(getEstiloContenido()).append("\" align=\"center\">");
        sb.append(validarNullMontoHtml(facturaVo.getMonto().setScale(2, RoundingMode.HALF_UP))).append("</td>");
        sb.append("<td height=\"18\"  style=\"").append(getEstiloContenido()).append("\" align=\"center\">");
        sb.append(validarNullFechaHtml(facturaVo.getFechaEmision()));
        sb.append("</td>");
        sb.append("</tr></table><br/>");

        return sb.toString();
    }

    
    public StringBuilder mensajeDevolicionFactura(FacturaVo facturaVo, ProveedorVo proveedorVo, String motivo) {

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio())
                .append(getTitulo("Rechazo de Factura"))
                .append("Buen día, <br/>")
                .append("<p>La Factura enviada a la empresa ").append(facturaVo.getCompania()).append(" ha sido rechazada por el siguiente.</p>")
                .append("<p><b>Motivo: </b>")
                .append(motivo)
                .append("</p>")
                .append(datosFactura(facturaVo))
                .append("<p>Gracias. </p>")
                .append(plantilla.getFin());
        return cuerpoCorreo;
    }
    
    
    public StringBuilder mensajeEliminarArchivosPortal() {

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio())
                .append(getTitulo("Archivos requeridos del portal de proveedores RECHAZADOS y ELIMINADOS."))
                .append("Buen día, <br/>")
                .append("<p>Los archivos requeridos para el portal de proveedores que se habian cargados fueron rechazados y eliminados. Por favor, vuelva a cargar los archivos correctos.</p>")                
                .append("<p>Gracias. </p>")
                .append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder mensajeDevolicionCCN(FacturaVo facturaVo, ProveedorVo proveedorVo, String motivo) {

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio())
                .append(getTitulo("Rechazo de carta de Contenido Nacional"))
                .append("Buen día, <br/>")
                .append("<p>La carta de Contenido Nacional enviada a la empresa ").append(facturaVo.getCompania()).append(" ha sido rechazada por el siguiente.</p>")
                .append("<p><b>Motivo: </b>")
                .append(motivo)
                .append("</p>")
                .append(datosFactura(facturaVo))
                .append("<p>Gracias. </p>")
                .append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder mensajeAceptarFactura(FacturaVo facturaVo, ProveedorVo proveedorVo) {

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio())
                .append(getTitulo("Factura recibida y aceptada"))
                .append("Buen día, <br/>")
                .append("<p>La Factura enviada a la empresa ").append(facturaVo.getCompania()).append(" se ha recibido y aceptado.</p>")
                .append("<p> </p>")
                .append(datosFactura(facturaVo))
                .append("<p> </p>")
                .append("<p>Gracias. </p>")
                .append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder mensajeAceptarCCN(FacturaVo facturaVo, ProveedorVo proveedorVo) {

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio())
                .append(getTitulo("Carta Contenido Nacional recibida"))
                .append("Buen día, <br/>")
                .append("<p>La carta de Contenido Nacional enviada a la empresa ").append(facturaVo.getCompania()).append(" se ha recibido.</p>")
                .append("<p> </p>")
                .append(datosFactura(facturaVo))
                .append("<p>La aceptación de la Carta de Contenido Nacional no garantiza que su factura relacionada a la carta ESTÁ/O SERÁ aceptada por la empresa.").append(".</p>")
                .append("<p> </p>")
                .append("<p>Gracias. </p>")
                .append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder mensajeFacturaPagada(FacturaVo facturaVo) {

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio())
                .append(getTitulo("Factura pagada"))
                .append("Buen día, <br/>")
                .append("<p>La factura esta pagada ya con su comprobante y complemento de pago. Está lista la información de la factura para enviarla a CNH").append(".</p>")
                .append("<p> </p>")
                .append(datosFactura(facturaVo))
                .append("<p>Gracias. </p>")
                .append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder mensajeFacturasFile() {

        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio())
                .append(getTitulo("Archivos de las facturas solicitadas"))
                .append("Buen día, <br/>")
                .append("<p>Se le hace llegar el archivo comprimido con la recopilación de los archivos de las facturas solicitadas.").append(".</p>")
                .append("<p> </p>")
                .append("<p>Gracias. </p>")
                .append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder mensajeArchivosPortal(ProveedorVo proveedorVo) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio())
                .append(getTitulo("Archivos del portal de proveedores " + proveedorVo.getRfc()))
                .append("Buen día, <br/>")
                .append("<p>Se envian los archivos requeridos para el portal de proveedor de IHSA </p>")
                .append(datosProveedor(proveedorVo))
                .append("<p>Gracias. </p>")
                .append(linkEliminarArchivos(proveedorVo))
                .append(plantilla.getFin());
        return cuerpoCorreo;
    }

    private String linkEliminarArchivos(ProveedorVo proveedorVo) {
        String link = "";
        String arch1 = "";
        String arch2 = "";
        String arch3 = "";
        int proveedorID = 0;

        if (proveedorVo != null) {
            proveedorID = proveedorVo.getIdProveedor();
            if (proveedorVo.getPortalActPrep() != null
                    && proveedorVo.getPortalActPrep().getAdjuntoVO() != null
                    && proveedorVo.getPortalActPrep().getAdjuntoVO().getUuid() != null
                    && !proveedorVo.getPortalActPrep().getAdjuntoVO().getUuid().isEmpty()) {
                arch1 = proveedorVo.getPortalActPrep().getAdjuntoVO().getUuid();
            }

            if (proveedorVo.getPortalEstSocVig() != null
                    && proveedorVo.getPortalEstSocVig().getAdjuntoVO() != null
                    && proveedorVo.getPortalEstSocVig().getAdjuntoVO().getUuid() != null
                    && !proveedorVo.getPortalEstSocVig().getAdjuntoVO().getUuid().isEmpty()) {
                arch2 = proveedorVo.getPortalEstSocVig().getAdjuntoVO().getUuid();
            }
            if (proveedorVo.getPortalServEsp() != null
                    && proveedorVo.getPortalServEsp().getAdjuntoVO() != null
                    && proveedorVo.getPortalServEsp().getAdjuntoVO().getUuid() != null
                    && !proveedorVo.getPortalServEsp().getAdjuntoVO().getUuid().isEmpty()) {
                arch3 = proveedorVo.getPortalServEsp().getAdjuntoVO().getUuid();
            }
        }

        link = "<center><a style = \"margin:10px;background-color:#0895d6;border:1px solid #999;color:#fff;cursor:pointer;font-size:15px;font-weight:bold;text-decoration: none;\" HREF="
                + Configurador.urlSia() + "Proveedor/ElminarArchivos?ZWZ2W=" + proveedorID + "&ZWZ3W=" + arch1 + "&ZWZ4W=" + arch2 + "&ZWZ5W=" + arch3 + ">Rechazar archivos</a></center>";

        return link;
    }
    
    
    public StringBuilder mensajeEnvioFacturaExtranjera(FacturaVo facturaVo, ProveedorVo proveedorVo, OrdenVO compraVo) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio())
                .append(getTitulo("Envío factura: " + compraVo.getNavCode() + " - " + facturaVo.getFolio()))
                .append("Buen día, <br/>")
                .append("<p>Se envía la factura con folio: <b> ").append(facturaVo.getFolio()).append("</b> para el proceso de aceptación y pago.</p>")
                .append(datosFactura(facturaVo))
                .append("<p> </p>")
                .append(datosProveedor(proveedorVo))
                .append("<p>Gracias. </p>")
                .append(plantilla.getFin());
        return cuerpoCorreo;
    }
}
