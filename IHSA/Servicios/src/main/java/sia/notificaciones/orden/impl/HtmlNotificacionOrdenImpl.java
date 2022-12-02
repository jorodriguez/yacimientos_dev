/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.orden.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.constantes.TipoRequisicion;
import sia.correo.impl.CodigoHtml;
import sia.modelo.AutorizacionesOrden;
import sia.modelo.Compania;
import sia.modelo.OcRequisicionCheckcode;
import sia.modelo.Orden;
import sia.modelo.OrdenSiMovimiento;
import sia.modelo.ReRequisicionEts;
import sia.modelo.Requisicion;
import sia.modelo.SiPlantillaHtml;
import sia.modelo.orden.vo.ContactoOrdenVo;
import sia.modelo.orden.vo.OcRequisicionCheckcodeVO;
import sia.modelo.orden.vo.OrdenCorreoVo;
import sia.modelo.orden.vo.OrdenEtsVo;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.vo.inventarios.OrdenFormatoVo;
import sia.notificaciones.requisicion.impl.HtmlNotificacionRequisicionImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.orden.impl.OcOrdenEtsImpl;
import sia.servicios.orden.impl.OcRequisicionCheckcodeImpl;
import sia.servicios.requisicion.impl.ReRequisicionEtsImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sistema.impl.SiPlantillaHtmlImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@Stateless 
public class HtmlNotificacionOrdenImpl extends CodigoHtml {

    private NumberFormat formatoCantidad = NumberFormat.getInstance();

    @Inject
    private SiPlantillaHtmlImpl plantillaHtml;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;
    @Inject
    private HtmlNotificacionRequisicionImpl htmlRequisicion;
    @Inject
    private ReRequisicionEtsImpl reRequisicionEtsRemote;
    @Inject
    private RequisicionImpl requisicionRemote;
    @Inject
    private OcRequisicionCheckcodeImpl ocRequisicionCheckcodeRemote;
    @Inject
    private OcOrdenEtsImpl ocOrdenEtsRemote;
    //

    
    public StringBuilder mensajeNotificacionMontoDireccion(List<OrdenVO> ov, double totalAcumulado, String inicio, String fin) {
        try {
            SiPlantillaHtml plantilla = plantillaHtml.find(1);
            limpiarCuerpoCorreo();
            SimpleDateFormat fs = new SimpleDateFormat("yyyy/MM/dd");
            Date i = fs.parse(inicio);
            Date ff = fs.parse(fin);
            cuerpoCorreo.append(plantilla.getInicio());
            cuerpoCorreo.append(getTitulo("Notificación monto acumulado de OC/S"));
            //Aquí va todo el contenido del cuerpo,
            //Motivo
            cuerpoCorreo.append("<p>La gerencia de <b>").append(ov.get(0).getGerencia()).append("</b> ha generado OC/S con un monto acumulado de ").append(totalAcumulado).append(" USD al proveedor <b> ").append(ov.get(0).getProveedor()).append("</b> del <b>").append(Constantes.FMT_TextDateLarge.format(i)).append(" </b>al <b>").append(Constantes.FMT_TextDateLarge.format(ff)).append(" </b>. </p>");

            cuerpoCorreo.append("</br>");
            //Lista de Ordenes de compra
            getListaOrdenCompra(ov);
            cuerpoCorreo.append("</br>");
            cuerpoCorreo.append("</td></tr></table>");
            cuerpoCorreo.append(plantilla.getFin());
            return cuerpoCorreo;
        } catch (ParseException ex) {
            Logger.getLogger(HtmlNotificacionOrdenImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void getListaOrdenCompra(List<OrdenVO> ov) {
        cuerpoCorreo.append("<table width=\"100%\" cellspacing=\"0\">");
        cuerpoCorreo.append("<th colspan=\"5\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Ordenes de compra</th>");
        cuerpoCorreo.append("<tr>");
        cuerpoCorreo.append("<td width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Código</td>");
        cuerpoCorreo.append("<td width=\"40%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Proyecto OT</td>");
        cuerpoCorreo.append("<td width=\"40%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Referencia</td>");
        cuerpoCorreo.append("<td width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Fecha</td>");
        cuerpoCorreo.append("<td width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Monto (USD)</td>");
        cuerpoCorreo.append("</tr>");
        for (OrdenVO o : ov) {
            cuerpoCorreo.append("<tr>");
            cuerpoCorreo.append("<td ".concat(" width=\"15%\" align=\"left\" style=\"border: 1px solid #b5b5b5;\">".concat(o.getConsecutivo()).concat("</td>")));
            cuerpoCorreo.append("<td ".concat(" width=\"40%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(o.getNombreProyectoOT()).concat("</td>")));
            cuerpoCorreo.append("<td ".concat(" width=\"40%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat((o.getReferencia() == null) ? "--" : o.getReferencia()).concat("</td>")));
            cuerpoCorreo.append("<td ".concat(" width=\"25%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(Constantes.FMT_TextDateLarge.format(o.getFecha())).concat("</td>")));
            cuerpoCorreo.append("<td ".concat(" width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(String.valueOf(o.getTotalUsd())).concat("</td>")));
            cuerpoCorreo.append("</tr>");
        }
    }

    
    public StringBuilder mensajeNotificacionMontoAcumulado(List<OrdenCorreoVo> listaCorreo, String asunto, boolean mostrarEstatus) {
        try {
            SiPlantillaHtml plantilla = plantillaHtml.find(1);
            limpiarCuerpoCorreo();
            String t = asunto.concat(" - ").concat(Constantes.FMT_TextDateLarge.format(new Date()));
            cuerpoCorreo.append(plantilla.getInicio());
            cuerpoCorreo.append(getTitulo(t));
            cuerpoCorreo.append("<br/>");
            cuerpoCorreo.append(" Reporte de las OC/S solicitadas el día ").append(Constantes.FMT_TextDateLarge.format(new Date()));
            cuerpoCorreo.append(" que superan el monto establecido como máximo.");
            cuerpoCorreo.append("<br/>");
            cuerpoCorreo.append("<br/>");
            cuerpoMontoAcumulado(listaCorreo, mostrarEstatus);
            cuerpoCorreo.append(plantilla.getFin());
            return cuerpoCorreo;
        } catch (Exception ex) {
            Logger.getLogger(HtmlNotificacionOrdenImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void cuerpoMontoAcumulado(List<OrdenCorreoVo> listaCorreo, boolean mostrarEstatus) {
        for (OrdenCorreoVo ordenCorreoVo : listaCorreo) {
            cuerpoCorreo.append("<b>Gerencia: </b> ").append(ordenCorreoVo.getGerencia()).append(" ");
            cuerpoCorreo.append("<b>Proveedor: </b>").append(ordenCorreoVo.getProveedor());
            cuerpoCorreo.append("</br>");
            //
            cuerpoCorreo.append("<table width=\"100%\" cellspacing=\"0\">");
            if (mostrarEstatus) {
                cuerpoCorreo.append("<th colspan=\"6\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Ordenes de compra</th>");
            } else {
                cuerpoCorreo.append("<th colspan=\"5\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Ordenes de compra</th>");
            }

            cuerpoCorreo.append("<tr>");
            cuerpoCorreo.append("<td align=\"center\" style=\"").append(getEstiloTitulo()).append("\">").append("Código</td>");
            cuerpoCorreo.append("<td align=\"center\" style=\"").append(getEstiloTitulo()).append("\">").append("Solicitada</td>");
            cuerpoCorreo.append("<td align=\"center\" style=\"").append(getEstiloTitulo()).append("\">").append("Referencia</td>");
            cuerpoCorreo.append("<td align=\"center\" style=\"").append(getEstiloTitulo()).append("\">").append("Proyecto OT</td>");
            cuerpoCorreo.append("<td align=\"center\" style=\"").append(getEstiloTitulo()).append("\">").append("Total (USD)</td>");
            if (mostrarEstatus) {
                cuerpoCorreo.append("<td align=\"center\" style=\"").append(getEstiloTitulo()).append("\">").append("Estado</td>");
            }
            cuerpoCorreo.append("</tr>");
            for (OrdenVO ordenVO : ordenCorreoVo.getLorden()) {
                cuerpoCorreo.append("<tr>");
                cuerpoCorreo.append("<td  align=\"center\"  style=\" ").append(getEstiloContenido()).append("\">").append(ordenVO.getConsecutivo()).append(" </td>");
                cuerpoCorreo.append("<td  align=\"center\" style=\" ").append(getEstiloContenido()).append("\">").append(Constantes.FMT_ddMMyyy.format(ordenVO.getFechaSolicita())).append(" </td>");
                cuerpoCorreo.append("<td  align=\"center\" style=\" ").append(getEstiloContenido()).append("\">").append(ordenVO.getReferencia()).append(" </td>");
                cuerpoCorreo.append("<td  align=\"center\" style=\" ").append(getEstiloContenido()).append("\">").append(ordenVO.getNombreProyectoOT()).append(" </td>");
                cuerpoCorreo.append("<td  align=\"right\" style=\" ").append(getEstiloContenido()).append("\">").append(formatoMoneda.format(ordenVO.getTotalUsd())).append("</td>");
                if (mostrarEstatus) {
                    cuerpoCorreo.append("<td  align=\"center\" style=\" ").append(getEstiloContenido()).append("\">").append(ordenVO.getIdStatus() == Constantes.ESTATUS_APROBADA ? "Pendiente de autorizar" : "En proceso").append(" </td>");
                }

                cuerpoCorreo.append("</tr>");
            }
            cuerpoCorreo.append("<tr> <td colspan=\"4\" align=\"right\">").append("<b>Total (USD) : </b>").append("</td>");
            cuerpoCorreo.append("<td  align=\"right\">").append(formatoMoneda.format(ordenCorreoVo.getTotal())).append("</td>");
            if (mostrarEstatus) {
                cuerpoCorreo.append("<td> </td></tr>");
            } else {
                cuerpoCorreo.append("</tr>");
            }
            cuerpoCorreo.append("</table>");
            cuerpoCorreo.append("</br>");
        }
    }

    
    public StringBuilder mensajeNotificacionAutorizarOrdeCompra(String asunto, List<OrdenCorreoVo> listaOAuto, List<OrdenCorreoVo> listaCorreo, boolean mostrarEstatus) {
        try {
            SiPlantillaHtml plantilla = plantillaHtml.find(1);
            limpiarCuerpoCorreo();
            String t = asunto.concat(Constantes.FMT_ddMMyyy.format(new Date()));
            cuerpoCorreo.append(plantilla.getInicio());
            cuerpoCorreo.append(getTitulo(t));

            if (listaOAuto != null) {
                cuerpoCorreo.append("</br>");
                cuerpoCorreo.append(" Reporte de OC/S pendientes de autorizar ").append(Constantes.FMT_TextDateLarge.format(new Date())).append(".");
                cuerpoCorreo.append("</br>");
                cuerpoCorreo.append("</br>");
                mensajeAutorizaOrdenCompra(listaOAuto);
                cuerpoCorreo.append("<hr/>");
            } else {
                cuerpoCorreo.append(" No hay ordenes de compra pendientes de autorizar para hoy ").append(Constantes.FMT_TextDateLarge.format(new Date())).append(".");
                cuerpoCorreo.append("</br>");
            }
            if (listaCorreo != null && listaCorreo.size() > 0) {
                cuerpoCorreo.append("</br>");
                cuerpoCorreo.append(" Reporte de las OC/S solicitadas el día ").append(Constantes.FMT_TextDateLarge.format(new Date()));
                cuerpoCorreo.append(" que superan el monto establecido como máximo.");
                cuerpoCorreo.append("</br>");
                cuerpoCorreo.append("</br>");
                cuerpoMontoAcumulado(listaCorreo, mostrarEstatus);
                cuerpoCorreo.append("</br>");
            }

            cuerpoCorreo.append(plantilla.getFin());
            return cuerpoCorreo;
        } catch (Exception ex) {
            Logger.getLogger(HtmlNotificacionOrdenImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void mensajeAutorizaOrdenCompra(List<OrdenCorreoVo> listaOAutoOr) {

        for (OrdenCorreoVo orc : listaOAutoOr) {
            cuerpoCorreo.append("<b>Bloque: </b> ").append(orc.getCampo()).append(" ");
            cuerpoCorreo.append("<b>Proveedor: </b>").append(orc.getProveedor());
            cuerpoCorreo.append("</br>");
            cuerpoCorreo.append("<table width=\"100%\" cellspacing=\"0\">");
            cuerpoCorreo.append("<th colspan=\"4\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Ordenes de compra</th>");
            cuerpoCorreo.append("<tr>");
            cuerpoCorreo.append("<td  align=\"center\" style=\"").append(getEstiloTitulo()).append("\">").append("Código</td>");
            cuerpoCorreo.append("<td  align=\"center\" style=\"").append(getEstiloTitulo()).append("\">").append("Referencia</td>");
            cuerpoCorreo.append("<td align=\"center\" style=\"").append(getEstiloTitulo()).append("\">").append("Total</td>");
            cuerpoCorreo.append("<td align=\"center\" style=\"").append(getEstiloTitulo()).append("\">").append("Moneda</td>");
            cuerpoCorreo.append("</tr>");

            for (OrdenVO ordenVo : orc.getLorden()) {
                cuerpoCorreo.append("<tr>");
                cuerpoCorreo.append("<td  align=\"center\" style=\" ").append(getEstiloContenido()).append("\">").append(ordenVo.getConsecutivo()).append(" </td>");
                cuerpoCorreo.append("<td  align=\"center\" style=\" ").append(getEstiloContenido()).append("\">").append(ordenVo.getReferencia()).append(" </td>");
                cuerpoCorreo.append("<td  align=\"center\" style=\" ").append(getEstiloContenido()).append("\">").append(formatoMoneda.format(ordenVo.getTotal())).append("</td>");
                cuerpoCorreo.append("<td  align=\"center\" style=\" ").append(getEstiloContenido()).append("\">").append(ordenVo.getMoneda()).append(" </td>");
                cuerpoCorreo.append("</tr>");
            }
            cuerpoCorreo.append("</table>");
            cuerpoCorreo.append("</br>");
        }

    }

    private String replaceAll(String origen, String llave, String replace) {
        replace = replace.replaceAll("\\$", "\\\\\\$");
        return origen.replaceAll(llave, replace);
    }

    
    public StringBuilder msjNotificacionOrdenSolicitada(Orden orden, List<ContactoOrdenVo> contactos, List<OrdenDetalleVO> items) {
        StringBuilder solicitud = new StringBuilder();
        try {
            SiPlantillaHtml plantilla = plantillaHtml.find(this.Plantilla_OrdenCompra);
            this.limpiarCuerpoCorreo();
            StringBuilder encabezado = new StringBuilder();
            encabezado.append(getAutorizacionesOrden(orden));
            encabezado.append("<tr><td>&nbsp;</td></tr>");
            String plantillaInicio = plantilla.getInicio();
            plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
            plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
            solicitud.append(plantillaInicio);
            //Aquí va todo el contenido del cuerpo,
            solicitud.append(getCuerpoOrden(orden, contactos, items));
            solicitud.append(plantilla.getFin());
//
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return solicitud;
    }

    
    public StringBuilder msjNotificacionOrden(Orden orden, List<ContactoOrdenVo> contactos, List<OrdenDetalleVO> items) {
        StringBuilder solicitud = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(Plantilla_OrdenCompra);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(getAutorizacionesOrden(orden));
        encabezado.append(getLigaSIA());
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
        solicitud.append(plantillaInicio);
        //Aquí va todo el contenido del cuerpo,
        solicitud.append(getCuerpoOrden(orden, contactos, items));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }

    
    public StringBuilder msjNotificacionOrdenContabilidad(Orden orden, List<ContactoOrdenVo> contactos, List<OrdenDetalleVO> items) {
        StringBuilder solicitud = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_OrdenCompra);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(getMsgContabilidad(orden));
        encabezado.append("<tr><td>&nbsp;</td></tr>");
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
        solicitud.append(plantillaInicio);
        //Aquí va todo el contenido del cuerpo,
        solicitud.append(getCuerpoOrden(orden, contactos, items));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }

    
    public StringBuilder msjNotificacionProveedor(Orden orden, List<ContactoOrdenVo> contactos) {
        StringBuilder solicitud = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_OrdenCompra);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
//        encabezado.append(getAutorizacionesOrden(orden));
        encabezado.append("<tr><td>&nbsp;</td></tr>");
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
        solicitud.append(plantillaInicio);
        //Aquí va todo el contenido del cuerpo,
        solicitud.append(getMsgProveedor(orden));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }

    
    public StringBuilder msjNotificacionAnalista(Orden orden) {
        StringBuilder solicitud = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_LogoSIA);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
//        encabezado.append(getAutorizacionesOrden(orden));
        encabezado.append("<tr><td>&nbsp;</td></tr>");
        String plantillaInicio = plantilla.getInicio();
        //plantillaInicio = plantillaInicio.replaceAll("@@1@@", encabezado.toString());
        //plantillaInicio = plantillaInicio.replaceAll("@@2@@", orden.getCompania().getNombre());
        solicitud.append(plantillaInicio);
        //Aquí va todo el contenido del cuerpo,
        String titulo = "C\u00f3digos de NAVISION Orden: " + orden.getConsecutivo();
        solicitud.append(this.getTitulo(titulo));
        solicitud.append(getMsgAnalista(orden));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }

    private String getNotaNotificacion(String generoNota, String nota) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss a");
        StringBuilder tabla = new StringBuilder();
        tabla.append("<tr>");
        tabla.append("<td colspan=\"3\">");
        tabla.append("<table width=\"95%\" align=\"center\" style=\"border:1px solid #A8CEF0\">");
        tabla.append("<tr>");
        tabla.append("<td bgcolor=\"#A8CEF0\" colspan=\"4\" style=\"border:1px solid #A8CEF0; text-align:center;\">");
        tabla.append("<font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;\"><b>NOTA</b></font></td>");
        tabla.append("</tr>");
        tabla.append("<tr>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:25%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Operación</center>");
        tabla.append("</font></td>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:25%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Usuario</center>");
        tabla.append("</font></td>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:25%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Fecha</center>");
        tabla.append("</font></td>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:25%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Hora</center>");
        tabla.append("</font></td>");
        tabla.append("</tr>");

        tabla.append("<tr>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
        tabla.append("<font size=\"-1\" face=\"arial\">");
        tabla.append("<center>");
        tabla.append("Generó");
        tabla.append("</center>");
        tabla.append("</font></td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
        tabla.append("<font size=\"-1\" face=\"arial\">");
        tabla.append(generoNota);
        tabla.append("</font></td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
        tabla.append("<font size=\"-1\" face=\"arial\">");
        tabla.append(validarNullFechaHtml(new Date()));
        tabla.append("</font></td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
        tabla.append("<font size=\"-1\" face=\"arial\">");
        tabla.append("<center>");
        tabla.append(validarNullHoraHtml(sdf.format(new Date())));
        tabla.append("</center>");
        tabla.append("</font></td>");
        tabla.append("</tr>");
        tabla.append("<tr>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\" colspan=\"1\">");
        tabla.append("<font face=arial size=-1> <P ALIGN=left> NOTA: </font>");
        tabla.append("</td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\" colspan=\"3\">");
        tabla.append("<font face=arial size=-1> <P ALIGN=left>").append(validarNullHtml(nota)).append("</font>");
        tabla.append("</td>");
        tabla.append("</tr>");
        tabla.append("<tr>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\" colspan=\"4\">");
        tabla.append("<center>");
        tabla.append("Para responder la nota por favor entre al modulo de Notas O.C. del Sistema Integral de Administración.");
        tabla.append("</center>");
        tabla.append("</td>");

        tabla.append("</table><br><center></center></td></tr>");

        return tabla.toString();
    }

    
    public StringBuilder msjNotificacionAprobarOrden(Orden orden, List<ContactoOrdenVo> contactos, List<OrdenDetalleVO> items) {
        StringBuilder notificacion = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_OrdenCompra);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(getAutorizacionesOrden(orden));
        encabezado.append(getLigaSIA());
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
        notificacion.append(plantillaInicio);
        //Aquí va todo el contenido del cuerpo,
        notificacion.append(getCuerpoOrden(orden, contactos, items));
        notificacion.append(plantilla.getFin());
        notificacion.append(this.htmlRequisicion.mensajeNotificacionRequisicion(orden.getRequisicion(), true, "solicito", "revisar", "aprobar"));

        return notificacion;
    }

    
    public StringBuilder msjRechazoOrden(Orden orden, OrdenSiMovimiento movimiento, List<ContactoOrdenVo> contactos, List<OrdenDetalleVO> items) {
        StringBuilder notificacion = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_OrdenCompra);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(getNotificacionConMotivo(orden,
                new StringBuilder().append(orden.getConsecutivo()).append(" DEVUELTA").toString(),
                movimiento, "devolver"));
        encabezado.append(getLigaSIA());
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
        notificacion.append(plantillaInicio);
        //Aquí va todo el contenido del cuerpo,
        notificacion.append(getCuerpoOrden(orden, contactos, items));
        notificacion.append(plantilla.getFin());
//        notificacion.append("<tr><td colspan=\"3\">");
        notificacion.append(this.htmlRequisicion.mensajeNotificacionRequisicion(orden.getRequisicion(), true, "solicito", "revisar", "aprobar"));

        return notificacion;
    }

    
    public StringBuilder msjCancelarOrden(Orden orden, AutorizacionesOrden autorizacionesOrden, List<ContactoOrdenVo> contactos, List<OrdenDetalleVO> items) {
        StringBuilder notificacion = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_OrdenCompra);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(getNotificacionConMotivo(orden, new StringBuilder().append(orden.getConsecutivo()).append(" CANCELADA").toString(),
                autorizacionesOrden, "cancelar"));
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
        notificacion.append(plantillaInicio);
        //Aquí va todo el contenido del cuerpo,
        notificacion.append(getCuerpoOrdenCancelar(orden, contactos, items));
        notificacion.append(plantilla.getFin());
        //notificacion.append(this.htmlRequisicion.mensajeNotificacionRequisicion(orden.getRequisicion(), true, "solicito", "revisar", "aprobar"));

        return notificacion;
    }

    
    public StringBuilder msjNotaOrden(Orden orden, String autorNota, String nota, List<ContactoOrdenVo> contactos) {
        StringBuilder notificacion = new StringBuilder();
        try {

            SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_OrdenCompra);
            this.limpiarCuerpoCorreo();
            StringBuilder encabezado = new StringBuilder();
            encabezado.append(getNotaNotificacion(autorNota, nota));
            //    encabezado.append(getAutorizacionesOrden(orden));
            encabezado.append(getLigaSIA());
            String plantillaInicio = plantilla.getInicio();
            plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
            plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
            notificacion.append(plantillaInicio);
            //Aquí va todo el contenido del cuerpo,
            notificacion.append(encabezadoOrden(orden, contactos));
            notificacion.append(plantilla.getFin());
//        notificacion.append("<tr><td colspan=\"3\">");
            //  notificacion.append(this.htmlRequisicion.mensajeNotificacionRequisicion(orden.getRequisicion(), true, "solicito", "revisar", "aprobar"));
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return notificacion;
    }

    private String encabezadoOrden(Orden orden, List<ContactoOrdenVo> listaContactos) {
        StringBuilder cuerpoOrdenSB = new StringBuilder();
        cuerpoOrdenSB.append("<tr><td colspan=\"3\">");
        cuerpoOrdenSB.append("<table width=95% align=\"center\">");
        cuerpoOrdenSB.append("<tr>");
        cuerpoOrdenSB.append(getProyecto(orden));
        cuerpoOrdenSB.append(getControl(orden));
        cuerpoOrdenSB.append("</tr><tr>");
        cuerpoOrdenSB.append(getProveedor(orden));
        cuerpoOrdenSB.append(getRecepcionFactura(orden));
        cuerpoOrdenSB.append("</tr>");
        cuerpoOrdenSB.append(listaContactos != null ? geteMailContacto(listaContactos) : "&nbsp;");
        cuerpoOrdenSB.append(getCondicionPago(orden));
        cuerpoOrdenSB.append(getResponsables(orden));
        cuerpoOrdenSB.append(getObservaciones(orden));
        return cuerpoOrdenSB.toString();
    }

    public String getCuerpoOrden(Orden orden, List<ContactoOrdenVo> listaContactos, List<OrdenDetalleVO> items) {
        StringBuilder mensajeOrden = new StringBuilder();
        try {
            mensajeOrden.append("<tr><td colspan=\"3\">");
            mensajeOrden.append("<table width=95% align=\"center\">");
            mensajeOrden.append("<tr>");
            mensajeOrden.append(getProyecto(orden));
            mensajeOrden.append(getControl(orden));
            mensajeOrden.append("</tr><tr>");
            mensajeOrden.append(getProveedor(orden));
            mensajeOrden.append(getRecepcionFactura(orden));
            mensajeOrden.append("</tr>");
            mensajeOrden.append(listaContactos != null ? geteMailContacto(listaContactos) : "&nbsp;");
            mensajeOrden.append(getCondicionPago(orden));
            mensajeOrden.append(getUserDet(orden));
            mensajeOrden.append("</table> ");
            mensajeOrden.append("</td></tr>");
            // Se encpsula la generacion de todas las secciones del cuerpo de la orden
            mensajeOrden.append(getDetalleOrden(orden, items));
            mensajeOrden.append(getEts(orden.getId()));
            mensajeOrden.append(getResponsables(orden));
            mensajeOrden.append(getObservaciones(orden));
            mensajeOrden.append(getDatosFacturacion(orden.getCompania()));
            mensajeOrden.append(getRequisito(orden.getCompania()));
//
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return mensajeOrden.toString();
    }

    public String cuerpoOrdenRequisitor(Orden orden, List<OrdenDetalleVO> items) {
        StringBuilder mensajeOrden = new StringBuilder();
        try {
            mensajeOrden.append("<tr><td colspan=\"3\">");
            mensajeOrden.append("<table width=95% align=\"center\">");
            mensajeOrden.append("<tr>");
            mensajeOrden.append(getProyecto(orden));
            mensajeOrden.append(getControl(orden));
            mensajeOrden.append("</tr>");
            mensajeOrden.append("</table> ");
            mensajeOrden.append("</td></tr>");
            // Se encpsula la generacion de todas las secciones del cuerpo de la orden
            mensajeOrden.append(detalleOrdenRequisitor(items));
//
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return mensajeOrden.toString();
    }

    public String getCuerpoOrdenCancelar(Orden orden, List<ContactoOrdenVo> listaContactos, List<OrdenDetalleVO> items) {
        StringBuilder mensajeOrden = new StringBuilder();
        try {
            mensajeOrden.append("<tr><td colspan=\"3\">");
            mensajeOrden.append("<table width=95% align=\"center\">");
            mensajeOrden.append("<tr>");
            mensajeOrden.append(getProyecto(orden));
            mensajeOrden.append(getControl(orden));
            mensajeOrden.append("</tr><tr>");
            mensajeOrden.append(getProveedor(orden));
            mensajeOrden.append(getRecepcionFactura(orden));
            mensajeOrden.append("</tr>");
            mensajeOrden.append(listaContactos != null ? geteMailContacto(listaContactos) : "&nbsp;");
            mensajeOrden.append(getCondicionPago(orden));
            mensajeOrden.append(getUserDet(orden));
            mensajeOrden.append("</table> ");
            mensajeOrden.append("</td></tr>");
//
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return mensajeOrden.toString();
    }

    private String getDatosFacturacion(Compania compania) {
        StringBuilder df = new StringBuilder();
        df.append("<tr><td colspan = \"3\"><table width=90% align=\"center\">");
        df.append("<TR><td style=\"text-align:left;width:30%;\" colspan = \"3\"><font face=arial size=-1><b>Datos de Facturación:</b>");
        df.append("</td><td style=\"text-align:left;width:30%;\"></td><td style=\"text-align:left;width:40%;\"></td></tr>");
        df.append("<TR>");
        df.append("<td style=\"text-align:left;width:3%;\"></td><td style=\"text-align:left;width:97%;\" colspan = \"2\">	<font face=arial size=-2>");
        df.append("Empresa: ").append(validarNullHtml(compania.getNombre())).append("</td></tr><tr>");
        df.append("<td style=\"text-align:left;width:3%;\"></td><td style=\"text-align:left;width:97%;\" colspan = \"2\">	<font face=arial size=-2>");
        df.append("Domicilio: ").append(validarNullHtml(compania.getDomicilioFiscal())).append("</td></tr><tr>");
        df.append("<td style=\"text-align:left;width:3%;\"></td><td style=\"text-align:left;width:97%;\" colspan = \"2\">	<font face=arial size=-2>");
        df.append("RFC: ").append(validarNullHtml(compania.getRfc())).append("</td>");
        df.append("</tr>");
        df.append("</table>");
        df.append("</td></tr>");
        return df.toString();
    }

    private String getRequisito(Compania compania) {
        StringBuilder requisitos = new StringBuilder();
        requisitos.append("<tr><td colspan = \"3\"><table width=90% align=\"center\">");
        requisitos.append("<TR><td style=\"text-align:left;width:30%;\" colspan = \"3\"><font face=arial size=-1>&nbsp;");
        requisitos.append("</td><td style=\"text-align:left;width:30%;\"></td><td style=\"text-align:left;width:40%;\"></td></tr>");
        requisitos.append("<TR>");
        requisitos.append("<td style=\"text-align:left;width:100%;\" colspan = \"3\">	<font face=arial size=-2>");
        requisitos.append(compania.getRequisitoFactura()).append("</td>");

        requisitos.append("</tr>");
        requisitos.append("</table>");
        requisitos.append("</td></tr>");

        return requisitos.toString();
    }

    private String getMsgProveedor(Orden orden) {
        StringBuilder requisitos = new StringBuilder();
        int i = 1;
        requisitos.append("<tr><td colspan = \"3\"><table width=90% align=\"center\">");
        requisitos.append("<TR><td style=\"text-align:left;width:30%;\" colspan = \"3\"><font face=arial size=-1>&nbsp;");
        requisitos.append("</td><td style=\"text-align:left;width:30%;\"></td><td style=\"text-align:left;width:40%;\"></td></tr>");
        requisitos.append("<TR><td style=\"text-align:left;width:100%;\" colspan = \"3\">	<font face=arial size=-1><b>");
        requisitos.append(orden.getCompania().getNombre()).append("</b> se complace en adjudicar la presente Orden de Compra/Servicio. Para lo cual se adjuntan los siguientes archivos: ");
        requisitos.append("</td></TR>");
        requisitos.append("<TR><td style=\"text-align:left;width:100%;\" colspan = \"3\">	<font face=arial size=-1>");
        requisitos.append(i++).append(".    Orden No. ");

        if (orden.getNavCode() != null && !orden.getNavCode().isEmpty()) {
            requisitos.append(orden.getNavCode());
        } else {
            requisitos.append(orden.getConsecutivo());
        }

        requisitos.append(" (PDF)").append("</td></tr>");

        if (!Constantes.RFC_MPG.equalsIgnoreCase(orden.getCompania().getRfc())) {
            requisitos.append("<TR><td style=\"text-align:left;width:100%;\" colspan = \"3\">	<font face=arial size=-1>");
            requisitos.append(i++).append(".    Condiciones Generales de Compras (PDF)").append("</td></tr>");
        }

        if (!ocOrdenEtsRemote.traerOcOrdenEts(orden.getId(), 2).isEmpty()) {
            requisitos.append("<TR><td style=\"text-align:left;width:100%;\" colspan = \"3\">	<font face=arial size=-1>");
            requisitos.append(i++).append(".    Documentación Soporte (ETS)").append("</td></tr>");
        }

        requisitos.append("</table>");
        requisitos.append("</td></tr><tr><td>&nbsp;</td></tr>");

        return requisitos.toString();
    }

    private String getMsgAnalista(Orden orden) {

        StringBuilder requisitos = new StringBuilder();
        requisitos.append("<tr>");
        requisitos.append("<td colspan = \"2\">");
        requisitos.append("<table width=90% align=\"center\">");

        requisitos.append("<TR>");
        requisitos.append("<td style=\"text-align:right;\" width=\"20%\"><font face=arial size=-1><b>Orden: </b></font></td>");
        requisitos.append("<td style=\"text-align:left;\" width=\"80%\"><font face=arial size=-1>").append(orden.getConsecutivo()).append("</font></td>");
        requisitos.append("</TR>");
        requisitos.append("<TR>");
        requisitos.append("<td style=\"text-align:right;\" width=\"20%\"><font face=arial size=-1><b>URL: </b></font></td>");
        requisitos.append("<td style=\"text-align:left;\" width=\"80%\"><font face=arial size=-1>").append(orden.getUrl()).append("</font></td>");
        requisitos.append("</TR>");
        requisitos.append("<TR>");
        requisitos.append("<td style=\"text-align:right;\" width=\"20%\"><font face=arial size=-1><b>CHECKCODE: </b></font></td>");
        requisitos.append("<td style=\"text-align:left;\" width=\"80%\"><font face=arial size=-1>").append(orden.getCheckcode()).append("</font></td>");
        requisitos.append("</TR>");
        requisitos.append("<TR>");
        requisitos.append("<TD width=\"20%\">&nbsp;</TD>");
        requisitos.append("<td style=\"text-align:left;\" width=\"80%\"><font face=arial size=-1><b>&nbsp; </b></font></td>");
        requisitos.append("</TR>");
        requisitos.append("<TR>");
        requisitos.append("<td style=\"text-align:right;\" width=\"20%\"><font face=arial size=-1><b>Requisición: </b></font></td>");
        requisitos.append("<td style=\"text-align:left;\" width=\"80%\"><font face=arial size=-1>").append(orden.getRequisicion().getConsecutivo()).append("</font></td>");
        requisitos.append("</TR>");
        requisitos.append("<TR>");
        requisitos.append("<td style=\"text-align:right;\" width=\"20%\"><font face=arial size=-1><b>URL: </b></font></td>");
        OcRequisicionCheckcodeVO auxRequi = validarRequisicion(orden, orden.getRequisicion(), orden.getProveedor().getRfc());
        requisitos.append("<td style=\"text-align:left;\" width=\"80%\"><font face=arial size=-1>").append(auxRequi.getUrl()).append("</font></td>");
        requisitos.append("</TR>");
        requisitos.append("<TR>");
        requisitos.append("<td style=\"text-align:right;\" width=\"20%\"><font face=arial size=-1><b>CHECKCODE: </b></font></td>");
        requisitos.append("<td style=\"text-align:left;\" width=\"80%\"><font face=arial size=-1>").append(auxRequi.getCheckcode()).append("</font></td>");
        requisitos.append("</TR>");
        requisitos.append("</table>");
        requisitos.append("</td></tr>");
        //requisitos.append("<tr><td>&nbsp;</td></tr>");

        return requisitos.toString();
    }

    private OcRequisicionCheckcodeVO validarRequisicion(Orden orden, Requisicion requi, String proveedorRFC) {
        OcRequisicionCheckcodeVO check = null;
        if (requi.getUrl() == null || requi.getUrl().isEmpty()) {
            List<ReRequisicionEts> ets = reRequisicionEtsRemote.traerAdjuntosPorRequisicionVisible(requi.getId(), Constantes.BOOLEAN_FALSE);
            if (ets != null && ets.size() > 0) {
                try {
                    StringBuilder url = new StringBuilder();
                    url.append(Configurador.urlSia()).append("Compras/OFWSS?Z4BX2=SIA&ZWZ4W=").append(ets.get(0).getSiAdjunto().getId()).append("&ZWZ3W=").append(ets.get(0).getSiAdjunto().getUuid());
                    requi.setUrl(url.toString());
                    //requi.setCheckcode(this.encriptar(proveedorRFC, requi.getUrl(), "ColchónPikolin"));
                    requisicionRemote.edit(requi);
                } catch (Exception ex) {
                    Logger.getLogger(HtmlNotificacionOrdenImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        try {
            check = ocRequisicionCheckcodeRemote.getRequiCheckCode(orden.getId(), requi.getId(), proveedorRFC);
            if (check == null) {
                OcRequisicionCheckcode checkCode = new OcRequisicionCheckcode();
                checkCode.setOrden(orden);
                checkCode.setRequisicion(requi);
                checkCode.setRfc(proveedorRFC);
                checkCode.setCheckcode(this.encriptar(proveedorRFC, requi.getUrl(), "ColchónPikolin"));
                checkCode.setEliminado(Constantes.BOOLEAN_FALSE);
                ocRequisicionCheckcodeRemote.create(checkCode);
                check = new OcRequisicionCheckcodeVO();
                check.setId(checkCode.getId());
                check.setIdOrden(checkCode.getOrden().getId());
                check.setIdRequisicion(checkCode.getRequisicion().getId());
                check.setCheckcode(checkCode.getCheckcode());
                check.setUrl(requi.getUrl());
            } else if (check.isEliminado()) {
                OcRequisicionCheckcode checkCode = ocRequisicionCheckcodeRemote.find(check.getId());
                checkCode.setEliminado(Constantes.BOOLEAN_FALSE);
                ocRequisicionCheckcodeRemote.edit(checkCode);
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HtmlNotificacionOrdenImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(HtmlNotificacionOrdenImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return check;
    }

    private String encriptar(String rfc, String url, String picolin) throws NoSuchAlgorithmException {
        StringBuilder text = new StringBuilder();
        try {
            text.append(rfc).append(url).append(picolin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] b = md.digest(text.toString().getBytes("ISO-8859-1"));
            StringBuilder h = new StringBuilder();
            for (int i = 0; i < b.length; i++) {
                String cad = Integer.toHexString(0xFF & b[i]);
                if (cad.length() == 1) {
                    h.append('0');
                }
                h.append(cad);
            }
            return h.toString();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return text.toString();
        }
    }

    private String getMsgContabilidad(Orden orden) {

        StringBuilder requisitos = new StringBuilder();
        requisitos.append("<tr>");
        requisitos.append("<td colspan=\"3\">");
        requisitos.append("<table width=\"95%\" align=\"center\" style=\"border:1px solid #A8CEF0\">");
        requisitos.append("<tr>");
        requisitos.append("<td style=\"text-align:left;\" width=\"80%\"><font face=arial size=-1>");
        requisitos.append("Se requiere sean generados los códigos de los activos fijos en NAVISION, para posteriormente sean enviados a: <b>").append(orden.getAnalista().getNombre()).append("</b> y los cargue en el SIA").append(".</font></td>");
        requisitos.append("</TR>");
        requisitos.append("</table>");
        requisitos.append("</td></tr>");
        //requisitos.append("<tr><td>&nbsp;</td></tr>");

        return requisitos.toString();
    }

    private String getEts(int orden) {
        StringBuilder cuerpoEtsSB = new StringBuilder();
//
        try {
            List<OrdenEtsVo> listETS = ocOrdenEtsRemote.traerEtsPorOrdenCategoria(orden, 2);
            if (listETS != null && listETS.size() > 0) {
                cuerpoEtsSB.append("<tr><td colspan=\"3\">");
                cuerpoEtsSB.append("<table width=\"95%\" align=\"center\" style=\"border:1px solid #A8CEF0\">");
                cuerpoEtsSB.append("<tr style=\"border:1px solid #A8CEF0\">");
                cuerpoEtsSB.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; text-align:center;\" colspan=\"4\">");
                cuerpoEtsSB.append("<font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;\">");
                cuerpoEtsSB.append("<b>ESPECIFICACIÓN TÉCNICA DE SUMINISTRO</b>");
                cuerpoEtsSB.append("</font>");
                cuerpoEtsSB.append("</td>");
                cuerpoEtsSB.append("</tr>");
                cuerpoEtsSB.append("<tr style=\"border:1px solid #A8CEF0\">");
                cuerpoEtsSB.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:10%;\"><font color=\"000000\" size=\"-1\" face=\"arial\"><center>Número</center></font></td>");
                cuerpoEtsSB.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:40%;\"><font color=\"000000\" size=\"-1\" face=\"arial\"><center>Nombre</center></font></td>");
                cuerpoEtsSB.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:45%;\"><font color=\"000000\" size=\"-1\" face=\"arial\"><center>Descripción</center></font></td>");
                cuerpoEtsSB.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:5%;\"><font color=\"000000\" size=\"-1\" face=\"arial\"><center></center></font></td>");
                cuerpoEtsSB.append("</tr>");
//
                a = 1;
                String bgColor = "bgcolor=\"#FFFFFF\"";
                for (OrdenEtsVo ets : listETS) {
                    if (getModulo(a).equals(0)) {
                        bgColor = "bgcolor=\"#E4EAEB\"";
                    } else {
                        bgColor = "bgcolor=\"#FFFFFF\"";
                    }
//
                    cuerpoEtsSB.append("<tr>");
                    cuerpoEtsSB.append("<td valign=\"middle\" ").append(bgColor).append(" style=\"border:1px solid #A8CEF0; width:10%;\"><font size=\"-1\" face=\"arial\"><center>").append(a).append("</center></font></td>");
                    cuerpoEtsSB.append("<td valign=\"middle\" ").append(bgColor).append(" style=\"border:1px solid #A8CEF0; width:40%;\"><font size=\"-1\" face=\"arial\">");
                    cuerpoEtsSB.append(validarNullHtml(ets.getNombreSinUUID())).append("</font></td>");
//
                    cuerpoEtsSB.append("<td valign=\"middle\" ").append(bgColor).append(" style=\"border:1px solid #A8CEF0; width:45%;\"><font size=\"-1\" face=\"arial\">");
                    cuerpoEtsSB.append(validarNullHtml(ets.getDescripcion())).append("</font></td>");
//
                    cuerpoEtsSB.append("<td valign=\"middle\" ").append(bgColor).append(" style=\"border:1px solid #A8CEF0; width:5%;\"><font size=\"-1\" face=\"arial\"><center>");
                    cuerpoEtsSB.append("<a target=\"_blank\" ");
                    cuerpoEtsSB.append("href=\"").append(Configurador.urlSia()).append("Compras/OFWSS?Z4BX2=SIA&ZWZ4W=").append(ets.getId()).append("&ZWZ3W=").append(ets.getUuid()).append("\">");
                    cuerpoEtsSB.append("Abrir");
                    cuerpoEtsSB.append("</a></center></font></td>");
                    cuerpoEtsSB.append("</tr>");
                    a++;
                }
                cuerpoEtsSB.append("</table><br><center></td></tr>");
            }

        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return cuerpoEtsSB.toString();
    }

    private String getLigaSIA() {
        StringBuilder liga = new StringBuilder();//
        liga.append("<tr>").append("<td colspan=\"3\">").append("<table width=\"95%\" align=\"center\">").append("<tr>").append("<td style=\"text-align:center\"><a target=\"_blank\" href=\"").append(Configurador.urlSia()).append("Sia\">").append("Clic aquí para ir al SIA</a><br>").append("<br>").append("<br>").append("</td>").append("</tr>").append("</table>").append("</td>").append("</tr>");

        return liga.toString();
    }

    private StringBuilder getUserDet(Orden orden) {
        StringBuilder userDet = new StringBuilder();
        userDet.append("<tr><td colspan='4' style=\"text-align:right;\"><font size=\"-2\" face=\"arial\">").append(orden.getAnalista().getId()).append("-").append(orden.getId()).append("&amp;").append(orden.getRequisicion().getId()).append("</font></td></tr>");
        return userDet;
    }

    private String getProyecto(Orden orden) {
        StringBuilder solicitante = new StringBuilder();
        String c15 = "<td bgcolor=#ffffff valign=middle style=\"text-align:left; width:20%;\"><font face=arial size=-1>";

        solicitante.append("<td style=\"width:50%\">");
        solicitante.append("<table style=\"width:100%\">");
        solicitante.append("<tr>");
        solicitante.append("<td style=\"background-color:#A8CEF0\" colspan=\"2\">");
        solicitante.append("<font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;text-align:center;\">");
        solicitante.append("<b>PROYECTO</b>");
        solicitante.append("</font></td>");
        solicitante.append("</tr>");
        //
        solicitante.append("<tr>").append(c15).append("Dpto. Solicitante </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        solicitante.append(validarNullHtml(orden.getGerencia().getNombre())).append("</b></font></td></tr>");

        solicitante.append("<tr>").append(c15).append("Responsable </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        solicitante.append(validarNullHtml(orden.getResponsableGerencia().getNombre())).append("</b></font></td></tr>");
        solicitante.append("<tr>").append(c15).append("Fecha Entrega </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        solicitante.append(validarNullFechaHtml(orden.getFechaEntrega())).append("</b></font></td></tr>");

        solicitante.append("<tr>").append(c15).append("Destino </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        solicitante.append(validarNullHtml(orden.getDestino())).append("</b></font></td></TR>");

        solicitante.append("</table>");
        solicitante.append("</td>");
        return solicitante.toString();
    }

    private String getProveedor(Orden orden) {
        StringBuilder proveedor = new StringBuilder();
        String c15 = "<td bgcolor=#ffffff valign=middle style=\"text-align:left; width:20%;\"><font face=arial size=-1>";

        proveedor.append("<td style=\"width:50%\">");
        proveedor.append("<table style=\"width:100%\">");
        proveedor.append("<tr>");
        proveedor.append("<td style=\"background-color:#A8CEF0\" colspan=\"2\">");
        proveedor.append("<font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;text-align:center;\">");
        proveedor.append("<b>PROVEEDOR</b>");
        proveedor.append("</font></td>");
        proveedor.append("</tr>");

        proveedor.append("<tr>").append(c15).append("Proveedor </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        proveedor.append(validarNullHtml(orden.getProveedor().getNombre())).append("</b></font></td></TR><TR>");
        proveedor.append(c15).append("Dirección </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        proveedor.append(validarNullHtml(orden.getProveedor().getCalle())).append(" #").append(orden.getProveedor().getNumero()).append("</b></font></td></tr>");

        proveedor.append("<tr>").append(c15).append("Colonia </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        proveedor.append(validarNullHtml(orden.getProveedor().getColonia())).append("</b></font></td></TR><TR>");
        proveedor.append(c15).append("Ciudad </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        proveedor.append(validarNullHtml(orden.getProveedor().getCiudad())).append(" C.P.").append(validarNullHtml(orden.getProveedor().getCodigoPostal())).append("</b></font></td></tr>");

        proveedor.append("<tr>").append(c15).append("Estado </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        proveedor.append(validarNullHtml(orden.getProveedor().getEstado())).append("</b></font></td></TR><TR>");
        proveedor.append(c15).append("País</td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        proveedor.append(validarNullHtml(orden.getProveedor().getPais())).append("</b></font></td></tr>");

        proveedor.append("<tr>").append(c15).append("Teléfono </td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        proveedor.append(validarNullHtml(orden.getProveedor().getTelefono())).append("</b></font></td></TR><TR>");
        proveedor.append(c15).append("Fax</td><td style=\"text-align:left; width:80%;\"><font face=arial size=-1><b>");
        proveedor.append(validarNullHtml(orden.getProveedor().getFax())).append("</b></font></td></tr>");

        proveedor.append("</table>");
        proveedor.append("</td>");
        return proveedor.toString();
    }

    private String geteMailContacto(List<ContactoOrdenVo> listaContactos) {
        StringBuilder mailContacto = new StringBuilder();

        mailContacto.append("<tr>");
        mailContacto.append("<td colspan=\"2\">");
        mailContacto.append("<table width=\"100%\">");
        mailContacto.append("<tbody>");
        mailContacto.append("<tr>");
        mailContacto.append("<td valign=\"middle\" bgcolor=\"#ffffff\" style=\"text-align:left; width:10%\"><font size=\"-1\" face=\"arial\">E-mail");
        mailContacto.append("</font></td>");
        mailContacto.append("<td style=\"text-align:left; width:90%\"><font face=arial size=-1><b>").append(validarNullHtml(this.getDestinatariosOrden(listaContactos))).append("</b></font></td>");
        mailContacto.append("</tr>");
        mailContacto.append("<tr>");
        mailContacto.append("<td valign=\"middle\" bgcolor=\"#ffffff\" style=\"text-align:left; width:10%\"><font size=\"-1\" face=\"arial\">Contacto</font></td>");
        mailContacto.append("<td style=\"text-align:left; width:90%\"><font face=arial size=-1><b>").append(validarNullHtml(this.getContactoOrdenVo(listaContactos))).append("</b></font></td>");
        mailContacto.append("</tr>");
        mailContacto.append("</tbody>");
        mailContacto.append("</table>");
        mailContacto.append("</td>");
        mailContacto.append("</tr>");

        return mailContacto.toString();
    }

    private String getControl(Orden orden) {
        StringBuilder contrato = new StringBuilder();
        String c15 = "<td bgcolor=#ffffff valign=middle style=\"text-align:left; width:15%;\"><font face=arial size=-1>";

        contrato.append("<td style=\"width:50%\">");
        contrato.append("<table style=\"width:100%\">");
        contrato.append("<tr>");
        contrato.append("<td style=\"background-color:#A8CEF0\" colspan=\"2\">");
        contrato.append("<font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;text-align:center;\">");
        contrato.append("<b>CONTROL</b>");
        contrato.append("</font></td>");
        contrato.append("</tr>");

        contrato.append("<tr>").append(c15).append("Código </td><td style=\"text-align:left; width:85%;\"><font face=arial size=-1><b>");
        contrato.append(validarNullHtml(orden.getConsecutivo())).append("</b></font></td></TR><TR>");

        contrato.append(c15).append("Fecha </td><td style=\"text-align:left; width:85%;\"><font face=arial size=-1><b>");
        contrato.append(validarNullFechaHtml(orden.getFecha())).append("</b></font></td></tr>");

        contrato.append("<tr>").append(c15).append("Contrato </td><td style=\"text-align:left; width:85%;\"><font face=arial size=-1><b>");
        contrato.append(validarNullHtml(orden.getContrato())).append("</b></font></td></TR><TR>");

        contrato.append(c15).append("Requisición </td><td style=\"text-align:left; width:85%;\"><font face=arial size=-1><b>");
        contrato.append(validarNullHtml(orden.getRequisicion().getConsecutivo())).append("</b></font></td></tr>");

        contrato.append("<tr>").append(c15).append("Bloque </td><td style=\"text-align:left; width:85%;\"><font face=arial size=-1><b>");
        contrato.append(validarNullHtml(orden.getApCampo().getNombre())).append("</b></font></td></tr>");

        contrato.append("</table>");
        contrato.append("</td>");

        return contrato.toString();
    }

    private String getCondicionPago(Orden orden) {
//            aqui poner una condicion para las oc/s que tengan condicion de pago.//;
        StringBuilder condicion = new StringBuilder();

        condicion.append("<tr>");
        condicion.append("<td colspan=\"2\">");
        condicion.append("<table  width=\"100%\">");
        if (orden.getCondicionPago() != null) {
            condicion.append("<tr>");
            //condicion.append("<td style=\"background-color:#A8CEF0\" ><font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;text-align:left;\">CONDICIÓN DE PAGO</font></td>");
            condicion.append("<td style=\"background-color:#A8CEF0\" ><font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;text-align:left;\"><b>CONDICIÓN DE PAGO</b></font></td>");
            condicion.append("</tr>");
            condicion.append("<tr>");
            condicion.append("<td style=\"text-align:left;\" ><font size=\"-1\" face=\"arial\">");
            condicion.append(validarNullHtml(orden.getCondicionPago().getNombre()));
            condicion.append("</font></td>");
            condicion.append("</tr>");
        } else {
            condicion.append("<tr>");
            condicion.append("<td style=\"background-color:#A8CEF0\" ><font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;text-align:left;\"><b>TERMINO DE PAGO</b></font></td>");
            condicion.append("</tr>");
            condicion.append("<tr>");
            condicion.append("<td style=\"text-align:left;\"><font size=\"-1\" face=\"arial\"><b>");
            condicion.append(validarNullHtml(orden.getOcTerminoPago().getNombre()));
            condicion.append("</b></font></td>");
            condicion.append("</tr>");
        }
        condicion.append("</table>");
        //
        condicion.append("</td>");
        condicion.append("</tr>");
        return condicion.toString();
    }

    private String getRecepcionFactura(Orden orden) {
        StringBuilder recepcion = new StringBuilder();
        String c15 = "<td bgcolor=#ffffff valign=middle style=\"text-align:left; width:15%;\"><font face=arial size=-1>";

        recepcion.append("<td style=\"width:50%\">");
        recepcion.append("<table style=\"width:100%\">");
        recepcion.append("<tr>");
        recepcion.append("<td style=\"background-color:#A8CEF0\" colspan=\"2\">");
        recepcion.append("<font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;text-align:center;\">");
        recepcion.append("<b>RECEPCIÓN DE FACTURAS</b>");
        recepcion.append("</font></td>");
        recepcion.append("</tr>");

        recepcion.append("<tr>").append(c15).append("Compañía </td><td style=\"text-align:left; width:85%;\"><font face=arial size=-1><b>");
        recepcion.append(validarNullHtml(orden.getCompania().getNombre())).append("</b></font></td></TR><TR>");
        recepcion.append(c15).append("Dirección </td><td style=\"text-align:left; width:85%;\"><font face=arial size=-1><b>");
        recepcion.append(validarNullHtml(orden.getCompania().getCalle())).append("</b></font></td></tr>");

        recepcion.append("<tr>").append(c15).append("Colonia </td><td style=\"text-align:left; width:85%;\"><font face=arial size=-1><b>");
        recepcion.append(validarNullHtml(orden.getCompania().getColonia())).append("</b></font></td></TR><TR>");
        recepcion.append(c15).append("Ciudad </td><td style=\"text-align:left; width:85%;\"><font face=arial size=-1><b>");
        recepcion.append(validarNullHtml(orden.getCompania().getCiudad())).append("</b></font></td></tr>");

        recepcion.append("<tr>").append(c15).append("Estado </td><td style=\"text-align:left; width:85%;\"><font face=arial size=-1><b>");
        recepcion.append(validarNullHtml(orden.getCompania().getEstado())).append("</b></font></td></TR><TR>");
        recepcion.append(c15).append("Teléfono </td><td style=\"text-align:left; width:85%;\"><font face=arial size=-1><b>");
        recepcion.append(validarNullHtml(orden.getCompania().getTelefono())).append("</b></font></td></tr>");

        recepcion.append("<tr>").append(c15).append("Atentamente</td><td style=\"text-align:left; width:85%;\"><font face=arial size=-1><b>");
        recepcion.append(validarNullHtml(orden.getCompania().getRecepcionFactura().getNombre())).append("</b></font></td></TR><TR>");
        recepcion.append(c15).append("&nbsp; </td><td style=\"text-align:left; width:85%;\"><font face=arial size=-1><b>");
        //recepcion.append("Lunes a Jueves de 9am a 5pm y Viernes de 9am a 12pm.").append("</b></font></td></tr>");
        recepcion.append("</table>");
        recepcion.append("</td>");

        return recepcion.toString();
    }

    private String getNotificacionConMotivo(Orden orden, String titulo, Object objeto, String... tipos) {
        StringBuilder tabla = new StringBuilder();
        StringBuilder tablaTitulo = new StringBuilder();
        if (titulo != null && !titulo.isEmpty()) {
            tablaTitulo.append(titulo.toUpperCase());
        } else {
            tablaTitulo.append("HISTORIAL DE AUTORIZACIÓN");
        }

        tabla.append("<tr>");
        tabla.append("<td colspan=\"3\">");
        tabla.append("<table width=\"95%\" align=\"center\" style=\"border:1px solid #A8CEF0\">");
        tabla.append("<tr>");
        tabla.append("<td bgcolor=\"#A8CEF0\" colspan=\"5\" style=\"border:1px solid #A8CEF0; text-align:center;\">");
        tabla.append("<font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;\"><b>").append(tablaTitulo).append("</b></font></td>");
        tabla.append("</tr>");
        tabla.append("<tr>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:15%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Operación</center>");
        tabla.append("</font></td>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:15%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Usuario</center>");
        tabla.append("</font></td>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:15%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Fecha</center>");
        tabla.append("</font></td>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:15%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Hora</center>");
        tabla.append("</font></td>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:40%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Motivo</center>");
        tabla.append("</font></td>");
        tabla.append("</tr>");

        for (String tipo : tipos) {
            if ("cancelar".equals(tipo) && objeto != null && objeto instanceof AutorizacionesOrden) {
                AutorizacionesOrden rechazo = (AutorizacionesOrden) objeto;
                if (!rechazo.getCancelo().getId().equalsIgnoreCase(rechazo.getModifico().getId())) {
                    tabla.append("<tr>");
                    tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                    tabla.append("<font size=\"-1\" face=\"arial\">");
                    tabla.append("<center>");
                    tabla.append("Solicitó Cancelaciòn");
                    tabla.append("</center>");
                    tabla.append("</font></td>");
                    tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                    tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                    tabla.append(validarNullHtml(rechazo.getCancelo().getNombre()));
                    tabla.append("</center></font></td>");
                    tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                    tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                    tabla.append(validarNullFechaHtml(rechazo.getFechaCancelo()));
                    tabla.append("</center></font></td>");
                    tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                    tabla.append("<font size=\"-1\" face=\"arial\">");
                    tabla.append("<center>");
                    tabla.append(validarNullHoraHtml(rechazo.getHoraCancelo()));
                    tabla.append("</center>");
                    tabla.append("</font></td>");
                    tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\" rowspan=\"2\">");
                    tabla.append("<font size=\"-1\" face=\"arial\">");
                    tabla.append("<center>");
                    tabla.append(validarNullHtml(rechazo.getMotivoCancelo()));
                    tabla.append("</center>");
                    tabla.append("</font></td>");
                    tabla.append("</tr>");
                }
                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append("Canceló");
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(validarNullHtml(rechazo.getModifico().getNombre()));
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(validarNullFechaHtml(rechazo.getFechaModifico()));
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append(validarNullHoraHtml(rechazo.getHoraModifico()));
                tabla.append("</center>");
                tabla.append("</font></td>");

                if (rechazo.getCancelo().getId().equalsIgnoreCase(rechazo.getModifico().getId())) {
                    tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\">");
                    tabla.append("<font size=\"-1\" face=\"arial\">");
                    tabla.append("<center>");
                    tabla.append(validarNullHtml(rechazo.getMotivoCancelo()));
                    tabla.append("</center>");
                    tabla.append("</font></td>");
                }

                tabla.append("</tr>");
            }

            if ("devolver".equals(tipo) && objeto != null && objeto instanceof OrdenSiMovimiento) {
                OrdenSiMovimiento movimiento = (OrdenSiMovimiento) objeto;
                if (!movimiento.getSolicitaDevolucion().getId().equalsIgnoreCase(movimiento.getGenero().getId())) {
                    tabla.append("<tr>");
                    tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                    tabla.append("<font size=\"-1\" face=\"arial\">");
                    tabla.append("<center>");
                    tabla.append("Solicitó Devolución");
                    tabla.append("</center>");
                    tabla.append("</font></td>");
                    tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                    tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                    tabla.append(validarNullHtml(movimiento.getSolicitaDevolucion() != null ? movimiento.getSolicitaDevolucion().getNombre() : null));
                    tabla.append("</center></font></td>");
                    tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                    tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                    tabla.append(validarNullFechaHtml(movimiento.getFechaGenero() != null ? movimiento.getFechaGenero() : null));
                    tabla.append("</center></font></td>");
                    tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                    tabla.append("<font size=\"-1\" face=\"arial\">");
                    tabla.append("<center>");
                    tabla.append(validarNullHoraHtml(movimiento.getHoraGenero() != null ? movimiento.getHoraGenero() : null));
                    tabla.append("</center>");
                    tabla.append("</font></td>");
                    tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\" rowspan=\"2\">");
                    tabla.append("<font size=\"-1\" face=\"arial\">");
                    tabla.append("<center>");
                    tabla.append(validarNullHtml(movimiento.getSiMovimiento().getMotivo()));
                    tabla.append("</center>");
                    tabla.append("</font></td>");
                    tabla.append("</tr>");
                }

                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append("Devolvió");
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(validarNullHtml(movimiento.getGenero().getNombre()));
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(validarNullFechaHtml(movimiento.getFechaGenero()));
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append(validarNullHoraHtml(movimiento.getHoraGenero()));
                tabla.append("</center>");
                tabla.append("</font></td>");

                if (movimiento.getSolicitaDevolucion().getId().equalsIgnoreCase(movimiento.getGenero().getId())) {
                    tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\">");
                    tabla.append("<font size=\"-1\" face=\"arial\">");
                    tabla.append("<center>&nbsp;");
                    tabla.append(validarNullHtml(movimiento.getSiMovimiento().getMotivo()));
                    tabla.append("</center>");
                    tabla.append("</font></td>");
                }

                tabla.append("</tr>");
                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" colspan=\"5\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append("La orden de compra y/o servicio fue devuelta al analista de procura para hacer los cambios necesarios, volviendo a iniciar el proceso de autorización. ");
                tabla.append(" El analista encontrara la orden de compra y/o servicio con el número: [").append(orden.getId().toString()).append("] en el modulo Solicitar Orden.");
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("</tr>");
            }
        }
        tabla.append("</table><br><center></center></td></tr>");

        return tabla.toString();
    }

    private String getAutorizacionesOrden(Orden orden) {
        StringBuilder tabla = new StringBuilder();

        tabla.append("<tr>");
        tabla.append("<td colspan=\"3\">");
        tabla.append("<table width=\"95%\" align=\"center\" style=\"border:1px solid #A8CEF0\">");
        tabla.append("<tr>");
        tabla.append("<td bgcolor=\"#A8CEF0\" colspan=\"5\" style=\"border:1px solid #A8CEF0; text-align:center;\">");
        tabla.append("<font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;\"><b>HISTORIAL DE AUTORIZACIÓN DE LA ORDEN</b></font></td>");
        tabla.append("</tr>");
        tabla.append("<tr>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:20%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Operación</center>");
        tabla.append("</font></td>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:20%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Usuario</center>");
        tabla.append("</font></td>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:20%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Fecha</center>");
        tabla.append("</font></td>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:20%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Hora</center>");
        tabla.append("</font></td>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:20%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Automaticamente</center>");
        tabla.append("</font></td>");
        tabla.append("</tr>");

        tabla.append("<tr>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
        tabla.append("<font size=\"-1\" face=\"arial\">");
        tabla.append("<center>");
        tabla.append("Solicitó");
        tabla.append("</center>");
        tabla.append("</font></td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
        tabla.append("<font size=\"-1\" face=\"arial\"><center>");
        tabla.append(validarNullHtml(orden.getAutorizacionesOrden().getSolicito().getNombre()));
        tabla.append("</center></font></td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
        tabla.append("<font size=\"-1\" face=\"arial\"><center>");
        tabla.append(validarNullFechaHtml(orden.getAutorizacionesOrden().getFechaSolicito()));
        tabla.append("</center></font></td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
        tabla.append("<font size=\"-1\" face=\"arial\">");
        tabla.append("<center>");
        tabla.append(validarNullHoraHtml(orden.getAutorizacionesOrden().getHoraSolicito()));
        tabla.append("</center>");
        tabla.append("</font></td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
        tabla.append("<font size=\"-1\" face=\"arial\">");
        tabla.append("<center>");
        tabla.append("No");
        tabla.append("</center>");
        tabla.append("</font></td>");
        tabla.append("</tr>");

        if ((orden.getAutorizacionesOrden().getFechaAutorizoGerencia() != null) && (orden.getAutorizacionesOrden().getAutorizaGerencia() != null)) {
            tabla.append("<tr>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append("Vo. Bo.");
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\"><center>");
            tabla.append(validarNullHtml(orden.getAutorizacionesOrden().getAutorizaGerencia().getNombre()));
            tabla.append("</center></font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\"><center>");
            tabla.append(validarNullFechaHtml(orden.getAutorizacionesOrden().getFechaAutorizoGerencia()));
            tabla.append("</center></font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append(validarNullHoraHtml(orden.getAutorizacionesOrden().getHoraAutorizoGerencia()));
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append(validarNullHtml(orden.getAutorizacionesOrden().isAutorizacionGerenciaAuto() ? Constantes.PALABRA_SI : "No"));
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("</tr>");
        }

        if ((orden.getAutorizacionesOrden().getFechaAutorizoMpg() != null) && (orden.getAutorizacionesOrden().getAutorizaMpg() != null)) {
            tabla.append("<tr>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append("Revisó");
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\"><center>");
            tabla.append(validarNullHtml(orden.getAutorizacionesOrden().getAutorizaMpg().getNombre()));
            tabla.append("</center></font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\"><center>");
            tabla.append(validarNullFechaHtml(orden.getAutorizacionesOrden().getFechaAutorizoMpg()));
            tabla.append("</center></font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append(validarNullHoraHtml(orden.getAutorizacionesOrden().getHoraAutorizoMpg()));
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append(validarNullHtml(orden.getAutorizacionesOrden().isAutorizacionMpgAuto() ? Constantes.PALABRA_SI : "No"));
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("</tr>");
        }

        /**
         * MPG
         */
        if (Constantes.RFC_MPG.equals(orden.getCompania().getRfc()) && orden.getAutorizacionesOrden().getFechaAutorizoFinanzas() != null && orden.getAutorizacionesOrden().getAutorizaFinanzas() != null) {
            tabla.append("<tr>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append("Aprobó");
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\"><center>");
            tabla.append(validarNullHtml(orden.getAutorizacionesOrden().getAutorizaFinanzas().getNombre()));
            tabla.append("</center></font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\"><center>");
            tabla.append(validarNullFechaHtml(orden.getAutorizacionesOrden().getFechaAutorizoFinanzas()));
            tabla.append("</center></font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append(validarNullHoraHtml(orden.getAutorizacionesOrden().getHoraAutorizoFinanzas()));
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append(validarNullHtml(orden.getAutorizacionesOrden().isAutorizacionFinanzasAuto() ? Constantes.PALABRA_SI : "No"));
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("</tr>");
        }

        if ((orden.getAutorizacionesOrden().getFechaAutorizoIhsa() != null) && (orden.getAutorizacionesOrden().getAutorizaIhsa() != null)) {
            tabla.append("<tr>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append("Aprobó");
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\"><center>");
            tabla.append(validarNullHtml(orden.getAutorizacionesOrden().getAutorizaIhsa().getNombre()));
            tabla.append("</center></font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\"><center>");
            tabla.append(validarNullFechaHtml(orden.getAutorizacionesOrden().getFechaAutorizoIhsa()));
            tabla.append("</center></font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append(validarNullHoraHtml(orden.getAutorizacionesOrden().getHoraAutorizoIhsa()));
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append(validarNullHtml(orden.getAutorizacionesOrden().isAutorizacionIhsaAuto() ? Constantes.PALABRA_SI : "No"));
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("</tr>");
        }

        if ((orden.getCompania().isSocio())
                && orden.getAutorizacionesOrden().getFechaAutorizoFinanzas() != null && orden.getAutorizacionesOrden().getAutorizaFinanzas() != null) {
            tabla.append("<tr>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append("Aprobó");
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\"><center>");
            tabla.append(validarNullHtml(orden.getAutorizacionesOrden().getAutorizaFinanzas().getNombre()));
            tabla.append("</center></font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\"><center>");
            tabla.append(validarNullFechaHtml(orden.getAutorizacionesOrden().getFechaAutorizoFinanzas()));
            tabla.append("</center></font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append(validarNullHoraHtml(orden.getAutorizacionesOrden().getHoraAutorizoFinanzas()));
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append(validarNullHtml(orden.getAutorizacionesOrden().isAutorizacionFinanzasAuto() ? Constantes.PALABRA_SI : "No"));
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("</tr>");
        }

        if ((orden.getAutorizacionesOrden().getFechaAutorizoCompras() != null) && (orden.getAutorizacionesOrden().getAutorizaCompras() != null)) {
            tabla.append("<tr>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append("Autorizó");
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\"><center>");
            tabla.append(validarNullHtml(orden.getAutorizacionesOrden().getAutorizaCompras().getNombre()));
            tabla.append("</center></font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\"><center>");
            tabla.append(validarNullFechaHtml(orden.getAutorizacionesOrden().getFechaAutorizoCompras()));
            tabla.append("</center></font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append(validarNullHoraHtml(orden.getAutorizacionesOrden().getHoraAutorizoCompras()));
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:20%\">");
            tabla.append("<font size=\"-1\" face=\"arial\">");
            tabla.append("<center>");
            tabla.append(validarNullHtml(orden.getAutorizacionesOrden().isAutorizacionComprasAuto() ? Constantes.PALABRA_SI : "No"));
            tabla.append("</center>");
            tabla.append("</font></td>");
            tabla.append("</tr>");
        }
        tabla.append("</table></td></tr>");
        return tabla.toString();
    }

    private String getContactoOrdenVo(List<ContactoOrdenVo> listaContactos) {
        String destinatarios = "";
        for (ContactoOrdenVo lista : listaContactos) {
            if (destinatarios.isEmpty()) {
                destinatarios = lista.getNombre();
            } else {
                destinatarios = destinatarios + ", " + lista.getNombre();
            }
        }
        return destinatarios;
    }

    private String getTotalesOrden(Orden orden) {
        StringBuilder totalesSB = new StringBuilder();
        totalesSB.append("<tr><td colspan=\"1\" style=\"text-align:right;width:90%;\"><font face=arial size=-1>");
        totalesSB.append("Subtotal:</font></td>");
        totalesSB.append("<td style=\"text-align:right; width:10%;\"><font face=arial size=-1>");
        totalesSB.append(orden.getSubtotal() == null ? " " : formatoMoneda.format(orden.getSubtotal()));
        totalesSB.append("</font></td></tr>");
        if (orden.isConIva()) {
            totalesSB.append("<tr><td colspan=\"1\" style=\"text-align:right;width:90%;\"><font face=arial size=-1>");
            totalesSB.append(orden.getPorcentajeIva()).append(": ");
            totalesSB.append("</font></td><td style=\"text-align:right; width:10%;\"><font face=arial size=-1>").append(formatoMoneda.format(orden.getIva()));
            totalesSB.append("</font></td></tr>");
        }
        totalesSB.append("<tr>");
        totalesSB.append("<td colspan=\"1\" style=\"text-align:right; width:90%;\"><font face=arial size=-1>");
        totalesSB.append("No".equals(orden.isConIva()) ? "Total sin iva: " : "Total con iva: ");
        totalesSB.append("</font></td>");
        totalesSB.append("<td style=\"text-align:right; width:10%;\"><font face=arial size=-1>");
        totalesSB.append(orden.getTotal() == null ? " " : formatoMoneda.format(orden.getTotal()));
        totalesSB.append("</font></td></tr>");
        totalesSB.append("<tr>");
        totalesSB.append("<td colspan=\"1\" style=\"text-align:right; width:90%;\"><font face=arial size=-1>");
        totalesSB.append("Descuento: ");
        totalesSB.append("</font></td>");
        totalesSB.append("<td style=\"text-align:right; width:10%;\"><font face=arial size=-1>");
        totalesSB.append(orden.getDescuento() == null ? Constantes.CERO : formatoMoneda.format(orden.getDescuento()));
        totalesSB.append("</font></td></tr>");
        totalesSB.append("<tr><td colspan=\"1\" style=\"text-align:right; width:90%;\"><font face=arial size=-1>");
        totalesSB.append("Moneda:");
        totalesSB.append("</font></td>");
        totalesSB.append("<td style=\"text-align:right; width:10%;\"><font face=arial size=-1>");
        totalesSB.append(orden.getMoneda().getSiglas());
        totalesSB.append("</font></td></tr>");
        return totalesSB.toString();
    }

    private String getDestinatariosOrden(List<ContactoOrdenVo> listaContactos) {
        String destinatarios = "";
        for (ContactoOrdenVo lista : listaContactos) {
            if (destinatarios.isEmpty()) {
                destinatarios = lista.getCorreo();
            } else {
                String newCorreo = lista.getCorreo().trim();
                int ascii = newCorreo.codePointAt(newCorreo.length() - 1);
                if (ascii < 65 || (ascii > 90 && ascii < 97) || ascii > 122) {
                    lista.setCorreo(newCorreo.substring(Constantes.CERO, newCorreo.length() - 1));
                }
                destinatarios = destinatarios + ", " + lista.getCorreo();
            }
        }

        return destinatarios;
    }

    private String llenarItems(Orden orden, List<OrdenDetalleVO> items) {
        StringBuilder sb = new StringBuilder();

        formatoCantidad.setMaximumFractionDigits(2);
        formatoCantidad.setMinimumFractionDigits(2);

        sb.append("<td colspan=\"3\">").append("<table width=95% align=\"center\" style=\"border:1px solid #A8CEF0;\">");
        sb.append("<tr style=\"border:1px solid #A8CEF0\">");
        sb.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; text-align:center;\" colspan=\"8\">");
        sb.append("<font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;\">");
        sb.append("<b>ITEMS</b>");
        sb.append("</font>");
        sb.append("</td>");
        sb.append("</tr>");

        int i = 1;
        if (items != null) {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append("<table style=\"width: 95%;\" align=\"center\">");
            for (OrdenDetalleVO Lista : items) {
                //--- Poner condiciòn de mostrar solo los Items  Autorizados y no disgregados

                sb.append("<tr style=\"width: 100%\"><td style=\"width: 100%;\">");
                sb.append("<table style=\"border: #eee; border-style: solid; border-width: 2px;width: 100%;\">");
                sb.append(" <tr style=\"width: 100%\">");
                sb.append("  <td style=\"width: 8%\">");
                sb.append("   <div>");
                sb.append("    <div>");
                sb.append("     <strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Partida</span></strong>");
                sb.append("    </div>");
                sb.append("    <div>");
                sb.append("     <span style=\"margin-left: 25%; font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(i).append("</span>");
                sb.append("    </div>");
                sb.append("   </div>");
                sb.append("  </td>");
                if (Lista.getIdpresupuesto() > 0) {
                    sb.append("  <td style=\"width: 22%\">");
                    sb.append("   <div>");
                    sb.append("    <div>");
                    sb.append("     <strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Presupuesto</span></strong>");
                    sb.append("    </div>");
                    sb.append("    <div>");
                    sb.append("     <span ").append("title=\"").append(Lista.getPresupuestoCodigo()).append("\" style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(Lista.getPresupuestoNombre()).append("</span>");
                    sb.append("    </div>");
                    sb.append("   </div>");
                    sb.append("  </td>");

                    sb.append("  <td style=\"width: 20%\">");
                    sb.append("   <div>");
                    sb.append("    <div>");
                    sb.append("     <strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Presup Año y Mes</span></strong>");
                    sb.append("    </div>");
                    sb.append("    <div>");
                    sb.append("     <span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Año ").append(Lista.getAnioPresupuesto()).append(" y Mes ").append(Lista.getMesPresupuesto()).append("</span>");
                    sb.append("    </div>");
                    sb.append("   </div>");
                    sb.append("  </td>");
                }
                if (Lista.getIdpresupuesto() > 0) {
                    sb.append("  <td style=\"width: 50%\" colspan=\"4\">");
                } else {
                    sb.append("  <td style=\"width: 22%\">");
                }
                sb.append("   <div>");
                sb.append("    <div>");
                sb.append("     <strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Proyecto OT</span></strong>");
                sb.append("    </div>");
                sb.append("    <div>");
                if (orden.isMultiproyecto()) {
                    sb.append("     <span ").append("title=\"").append(Lista.getMultiProyectos()).append("\" style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">")
                            .append(Lista.getMultiProyectos() != null && Lista.getMultiProyectos().length() > 40 ? (Lista.getMultiProyectos().substring(0, 40) + "...") : Lista.getMultiProyectos()).append("</span>");
                } else {
                    sb.append("     <span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(Lista.getProyectoOt()).append("</span>");
                }

                sb.append("    </div>");
                sb.append("   </div>");
                sb.append("  </td>");

                if (Lista.getIdpresupuesto() > 0) {
                    sb.append("</tr>");
                    sb.append("<tr style=\"width: 100%\">");
                }

                sb.append("  <td style=\"width: 30%\" colspan=\"2\">");
                sb.append("<div>");
                sb.append("<div>");
                sb.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">");
                if ("C".equals(orden.getApCampo().getTipo())) {
                    sb.append("Subactividad");
                } else {
                    sb.append("Tipo de Tarea");
                }
                sb.append("</span></strong>");
                sb.append("</div>");
                sb.append("<div>");
                sb.append("<span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append((orden.getTipo().equals(TipoRequisicion.PS.name()) || "C".equals(orden.getApCampo().getTipo())) ? validarNullHtml(Lista.getTipoTarea()) : "Activo Fijo").append("</span>");
                sb.append("</div>");
                sb.append("</div>");
                sb.append("</td>");
                if ("C".equals(orden.getApCampo().getTipo())) {
                    sb.append("<td style=\"width: 20%\">");
                    sb.append("<div>");
                    sb.append("<div>");
                    sb.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Tarea</span></strong>");
                    sb.append("</div>");
                    sb.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(validarNullHtml(Lista.getNombreTarea())).append("</span>");
                    sb.append("</div>");
                    sb.append("</div>");
                    sb.append("</td>");
                    sb.append("<td style=\"width: 50%\" colspan=\"4\">");
                    sb.append("<div>");
                    sb.append("<div>");
                    sb.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Subtarea</span></strong>");
                    sb.append("</div>");
                    sb.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(validarNullHtml(Lista.getSubTarea())).append("</span>");
                    sb.append("</div>");
                    sb.append("</div>");
                    sb.append("</td>");
                } else {
                    sb.append("<td style=\"width: 70%\" colspan=\"5\">");
                    sb.append("<div>");
                    sb.append("<div>");
                    sb.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Tarea</span></strong>");
                    sb.append("</div>");
                    sb.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(orden.getTipo().equals(TipoRequisicion.PS.name()) ? validarNullHtml(Lista.getNombreTarea()) : "Activo Fijo").append("</span>");
                    sb.append("</div>");
                    sb.append("</div>");
                    sb.append("</td>");
                }
                sb.append("</tr>");
                sb.append("<tr style=\"width: 100%\">");
                sb.append("<td style=\"width: 30%;\" colspan=\"2\">");
                sb.append("<div>");
                sb.append("<div>");
                sb.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Número parte</span></strong>");
                sb.append("</div>");
                sb.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(validarNullHtml(Lista.getArtNumeroParte())).append("</span>");
                sb.append("</div>");
                sb.append("</div>");
                sb.append("</td>");

                sb.append("<td style=\"width: 20%\" >");
                sb.append("<div>");
                sb.append("<div>");
                sb.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Descripción</span></strong>");
                sb.append("</div>");
                sb.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(validarNullHtml(Lista.getArtDescripcion())).append("</span>");
                sb.append("</div>");
                sb.append("</div>");
                sb.append("</td>");

                sb.append("<td style=\"width: 10%\">");
                sb.append("<div>");
                sb.append("<div>");
                sb.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Unidad</span></strong>");
                sb.append("</div>");
                sb.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(validarNullHtml(Lista.getArtUnidad())).append("</span>");
                sb.append("</div>");
                sb.append("</div>");
                sb.append("</td>");

                sb.append("<td style=\"width: 10%\">");
                sb.append("<div>");
                sb.append("<div>");
                sb.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Cantidad</span></strong>");
                sb.append("</div>");
                sb.append("<div></div>");
                sb.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(formatoCantidad.format(Lista.getCantidad())).append("</span>");
                sb.append("</div>");
                sb.append("</div>");
                sb.append("</td>");

                sb.append("<td style=\"width: 10%\">");
                sb.append("<div>");
                sb.append("<div>");
                sb.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">PrecioU</span></strong>");
                sb.append("</div>");
                sb.append("<div></div>");
                sb.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(formatoMoneda.format(Lista.getPrecioUnitario())).append("</span>");
                sb.append("</div>");
                sb.append("</div>");
                sb.append("</td>");

                sb.append("<td style=\"width: 10%\">");
                sb.append("<div>");
                sb.append("<div>");
                sb.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Importe</span></strong>");
                sb.append("</div>");
                sb.append("<div></div>");
                sb.append("<div style=\"text-align:right;\" ><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(formatoMoneda.format(Lista.getImporte())).append("</span>");
                sb.append("</div>");
                sb.append("</div>");
                sb.append("</td>");

                sb.append("</tr></table></td></tr>");
                i = i + 1;
            }

            sb.append(" </table></td>");
        }

        return sb.toString();
    }

    private String getDetalleOrden(Orden orden, List<OrdenDetalleVO> items) {
        StringBuilder detalle = new StringBuilder();

        detalle.append("<tr>");
        detalle.append(llenarItems(orden, items));
        detalle.append("</tr>");
        detalle.append("<tr>");
        detalle.append("<td colspan=\"3\">");
        detalle.append("<table style=\"width:94%;\" width=\"94%\" cellspacing=\"0\" align=\"center\">");
        detalle.append(getTotalesOrden(orden));
        detalle.append("</table>");
        detalle.append("<center></td></tr>");
        detalle.append(getObservacionesPartidas(items));
        return detalle.toString();
    }

    private String detalleOrdenRequisitor(List<OrdenDetalleVO> items) {
        StringBuilder detalle = new StringBuilder();
        detalle.append("<tr><td style=\"width:100%;\" colspan = \"3\"><table width=95% align=\"center\"  cellspacing=\"0\">");
        detalle.append("<tr><th colspan=\"4\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; font-size:12px; background-color:#A8CEF0;\"> ITEM(S)</th></tr>");
        detalle.append("<tr>");
        detalle.append("<th width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Núm. de parte</th>");
        detalle.append("<th width=\"5%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Cantidad</th>");
        detalle.append("<th width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Unidad</th>");
        detalle.append("<th width=\"65%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Descripción</th>");
        detalle.append("</tr>");
        for (OrdenDetalleVO item : items) {
            detalle.append("<tr>");
            detalle.append("<td width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(validarNullHtml(item.getArtNumeroParte())).append("</td>");
            detalle.append("<td width=\"5%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(item.getCantidad()).append("</td>");
            detalle.append("<td width=\"15%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(validarNullHtml(item.getArtUnidad())).append("</td>");
            detalle.append("<td width=\"65%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(validarNullHtml(item.getArtDescripcion())).append("</td>");

            detalle.append("</tr>");
        }
        detalle.append("</table>");
        detalle.append("</td></tr>");
        detalle.append(getObservacionesPartidas(items));
        return detalle.toString();
    }

    private String getObservacionesPartidas(List<OrdenDetalleVO> items) {
        StringBuilder observaciones = new StringBuilder();
        observaciones.append("<tr><td colspan = \"3\"><table width=95% align=\"center\">");
        a = 1;
        boolean showTitle = true;
        if (items != null) {
            for (OrdenDetalleVO item : items) {
                if (item.getObservaciones() != null && !item.getObservaciones().trim().isEmpty()) {
                    if (showTitle) {
                        observaciones.append("<TR><td style=\"text-align:left;\" colspan = \"2\"><font face=arial size=-1><b>Observaciones de las partidas:</b></td>").append("</tr>");
                        showTitle = false;
                    }
                    observaciones.append("<TR><td style=\"text-align:left;width:97%;\" colspan = \"2\"><font face=arial size=2><strong>").append("Partida ").append(a).append(":</strong> ").append(item.getObservaciones()).append("</td></tr>");
                }
                a = a + 1;
            }
        }
        observaciones.append("</table><br>");
        observaciones.append("</td></tr>");
        observaciones.append("</td></tr>");
        return observaciones.toString();
    }

    private String getResponsables(Orden orden) {
        StringBuilder responsables = new StringBuilder();
//
        responsables.append("<tr>").append("<td colspan = \"3\">").append("<table width=\"90%\" align=\"center\">").append("<tr align=\"center\">");
//
        if (orden.getAutorizacionesOrden().getFechaAutorizoCompras() == null) {
            responsables.append("<td width=\"25%\">&nbsp;</td>");
            responsables.append("<td bgcolor=\"#A8CEF0\"  colspan =\"2\">").append("<center><font size=\"-1\" face=\"arial\">").append("<center>Revisa</center>").append("</font></center><font size=\"-1\" face=\"arial\">").append("</font></td>");
            responsables.append("<td width=\"25%\">&nbsp;</td>");
            responsables.append("</tr><tr>");
            responsables.append("<td width=\"25%\">&nbsp;</td>");
            responsables.append("<td  width=\"25%\" colspan =\"2\">").append("<center><font size=\"-1\" face=\"arial\">");
            responsables.append(validarNullHtml(orden.getAutorizacionesOrden().getAutorizaMpg().getNombre())).append("</font><br><font size=\"-2\" face=\"arial\">");
            responsables.append(apCampoUsuarioRhPuestoRemote.getPuestoPorUsurioCampo(orden.getAutorizacionesOrden().getAutorizaMpg().getId(),
                    orden.getApCampo().getId()));
            responsables.append("</center>").append("</font></center>").append("</td>");
            responsables.append("<td width=\"25%\">&nbsp;</td>");
        } else {
            responsables.append("<td bgcolor=\"#A8CEF0\" colspan =\"2\">").append("<center><font size=\"-1\" face=\"arial\">").append("<center>Revisa</center>").append("</font></center><font size=\"-1\" face=\"arial\">").append("</font></td>");
            responsables.append("<td bgcolor=\"#A8CEF0\" colspan =\"2\">").append("<center><font size=\"-1\" face=\"arial\">").append("<center>Autoriza</center>").append("</font></center><font size=\"-1\" face=\"arial\">").append("</font></td>");
            responsables.append("</tr><tr><td width=\"25%\">&nbsp;</td><td width=\"25%\">&nbsp;</td><td width=\"25%\">&nbsp;</td><td width=\"25%\">&nbsp;</td></tr><tr>");
            responsables.append("<td width=\"25%\" colspan =\"2\">").append("<center><font size=\"-1\" face=\"arial\">");
            responsables.append(validarNullHtml(orden.getAutorizacionesOrden().getAutorizaMpg().getNombre())).append("</font><br><font size=\"-2\" face=\"arial\">").append(validarNullHtml(this.apCampoUsuarioRhPuestoRemote.getPuestoPorUsurioCampo(orden.getAutorizacionesOrden().getAutorizaMpg().getId(), orden.getApCampo().getId()))).append("</center>").append("</font></center>").append("</td>");
            responsables.append("<td width=\"25%\" colspan =\"2\">").append("<center><font size=\"-1\" face=\"arial\">").append(validarNullHtml(orden.getAutorizacionesOrden().getAutorizaCompras().getNombre())).append("</font><br><font size=\"-2\" face=\"arial\">").append(validarNullHtml(this.apCampoUsuarioRhPuestoRemote.getPuestoPorUsurioCampo(orden.getAutorizacionesOrden().getAutorizaCompras().getId(), orden.getAutorizacionesOrden().getAutorizaCompras().getApCampo().getId()))).append("</center>").append("</font></center>").append("</td>");
        }
//
        responsables.append("</tr>").append("<tr>").append("<td width=\"25%\">&nbsp;</td>").append("<td width=\"25%\">&nbsp;</td>").append("<td width=\"25%\">&nbsp;</td>").append("<td width=\"25%\">&nbsp;</td>").append("</tr>").append("<tr>");
//
        responsables.append("</tr>").append("<tr>").append("<td bgcolor=\"#A8CEF0\"  colspan =\"2\">");
        responsables.append("<center><font size=\"-1\" face=\"arial\">").append("<center>Elabora</center>");
        responsables.append("</font></center><font size=\"-1\" face=\"arial\">").append("</font></td>");
        responsables.append("<td bgcolor=\"#A8CEF0\"  colspan =\"2\">").append("<center><font size=\"-1\" face=\"arial\">");
        responsables.append("<center>Compras y Contrato</center>").append("</font></center><font size=\"-1\" face=\"arial\">");
        responsables.append("</font></td>").append("</tr>").append("<tr>").append("<td width=\"25%\">&nbsp;</td>");
        responsables.append("<td width=\"25%\">&nbsp;</td>").append("<td width=\"25%\">&nbsp;</td>");
        responsables.append("<td width=\"25%\">&nbsp;</td>").append("</tr>").append("<tr>").append("<td  colspan =\"2\">");
        responsables.append("<center><font size=\"-1\" face=\"arial\">");
        responsables.append(validarNullHtml(orden.getAnalista().getNombre())).append("</font><br><font size=\"-2\" face=\"arial\">");
        responsables.append(apCampoUsuarioRhPuestoRemote.getPuestoPorUsurioCampo(orden.getAnalista().getId(), orden.getApCampo().getId()));
        responsables.append("</center>").append("</font></center>").append("</td>").append("<td  colspan =\"2\">");
        responsables.append("<center><font size=\"-1\" face=\"arial\">").append(validarNullHtml(orden.getGerenteCompras().getNombre()));
        responsables.append("</font><br><font size=\"-2\" face=\"arial\">");
        responsables.append(apCampoUsuarioRhPuestoRemote.getPuestoPorUsurioCampo(orden.getGerenteCompras().getId(), orden.getApCampo().getId()));
        responsables.append("</center>").append("</font></center>").append("</td>").append("</tr>").append("</table>").append("<br>");
        responsables.append("<center></center>").append("</td>").append("</tr><tr><td>&nbsp;</td></tr>");
//
        return responsables.toString();
    }

    private String getObservaciones(Orden orden) {
        StringBuilder observaciones = new StringBuilder();
        observaciones.append("<tr><td colspan = \"3\"><table width=90% align=\"center\">");
        observaciones.append("<TR><td style=\"text-align:left;width:30%;\" colspan = \"3\"><font face=arial size=-1><b>Observaciones al Proveedor:</b>");
        observaciones.append("</td><td style=\"text-align:left;width:30%;\"></td><td style=\"text-align:left;width:40%;\"></td></tr>");
        observaciones.append("<TR><td style=\"text-align:left;width:3%;\"></td><td style=\"text-align:left;width:97%;\" colspan = \"2\">	<font face=arial size=-2>");
        observaciones.append("Favor de confirmar que la OC/S ha sido recibida y aceptada satisfactoriamente.</td><tr>");
        if (orden.getNota() != null) {
            observaciones.append("<TR><td style=\"text-align:left;width:3%;\"></td><td style=\"text-align:left;width:97%;\" colspan = \"2\">	<font face=arial size=-2>");
            observaciones.append(orden.getNota());
            observaciones.append("</font></td></tr>");
        }

        observaciones.append("</table>");
        observaciones.append("</td></tr>");
        return observaciones.toString();
    }

    
    public StringBuilder msjNotificacionCambioOrden(List<OrdenVO> lo, String nombreTiene, String nombreAprobara, String compania, String asunto, String status) {
        SiPlantillaHtml plantilla = plantillaHtml.find(1);
        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo(asunto));
        //Aquí va todo el contenido del cuerpo,
        cuerpoCorreo.append(mensajeCambioOrden(nombreTiene, nombreAprobara, status));
        cuerpoCorreo.append(listaOrden(lo));
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }

    private String mensajeCambioOrden(String nombreTiene, String nombreAprobara, String status) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p> La lista de ordenes de compra pendientes de <b>").append(status).append("</b>, por <b> ").append(nombreTiene).append("</b>");
        sb.append(", se pasaron a bandeja de <b>").append(nombreAprobara).append("</b>, para continuar con el proceso de aprobación.");
        sb.append("</p>");
        return sb.toString();
    }

    private String listaOrden(List<OrdenVO> lo) {
        StringBuilder sb = new StringBuilder();
        sb.append("<br/><table width=\"100%\" cellspacing=\"0\">");
        sb.append("<tr><th colspan=\"5\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Orden(es) de compra</th></tr>");
        sb.append("<tr>");
        sb.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Consecutivo</th>");
        sb.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Referencia</th>");
        sb.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Proveedor</th>");
        sb.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Total</th>");
        sb.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Moneda</th>");
        sb.append("</tr>");
        for (OrdenVO ordenVO : lo) {
            sb.append("<tr>");
            sb.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(ordenVO.getConsecutivo()).append("</td>");
            sb.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(ordenVO.getReferencia()).append("</td>");
            sb.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(ordenVO.getProveedor()).append("</td>");
            sb.append("<td align=\"right\" ").append(" style=\"").append(getEstiloContenido()).append("\">").append(ordenVO.getTotal()).append("</td>");
            sb.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(ordenVO.getMoneda()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table><br/>");
        return sb.toString();
    }

    
    public StringBuilder msjNotificacionOrdenSinAutorizar(String asunto, List<OrdenVO> listaOrden) {
        StringBuilder mesnajeCorreo = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_LogoSIA);
        this.limpiarCuerpoCorreo();
        String plantillaInicio = plantilla.getInicio();
        mesnajeCorreo.append(plantillaInicio);
        //
        mesnajeCorreo.append(this.getTitulo(asunto));
        mesnajeCorreo.append("<br/>");
        mesnajeCorreo.append(listaOrden(listaOrden));
        mesnajeCorreo.append("<br/>");
        mesnajeCorreo.append(plantilla.getFin());
        return mesnajeCorreo;
    }

    
    public StringBuilder msjRequisitorOrdenEnviada(Orden orden, List<ContactoOrdenVo> contactos, List<OrdenDetalleVO> items) {
        StringBuilder notificacion = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_OrdenCompra);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
        notificacion.append(plantillaInicio);
        //Aquí va todo el contenido del cuerpo,
        notificacion.append(cuerpoOrdenRequisitor(orden, items));
        notificacion.append(plantilla.getFin());
        return notificacion;
    }

    
    public StringBuilder msjNotificacionValidarPresupuesto(String asunto, String msgPresupuesto) {
        StringBuilder mesnajeCorreo = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_LogoSIA);
        this.limpiarCuerpoCorreo();
        String plantillaInicio = plantilla.getInicio();
        mesnajeCorreo.append(plantillaInicio);
        //
        mesnajeCorreo.append(this.getTitulo(asunto));
        mesnajeCorreo.append("<br/>");
        mesnajeCorreo.append(getMsgH3(msgPresupuesto));
        mesnajeCorreo.append("<br/>");
        mesnajeCorreo.append(plantilla.getFin());
        return mesnajeCorreo;
    }

    
    public StringBuilder msjNotificacionValidarContrato(String asunto, String msgContrato) {
        StringBuilder mesnajeCorreo = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_LogoSIA);
        this.limpiarCuerpoCorreo();
        String plantillaInicio = plantilla.getInicio();
        mesnajeCorreo.append(plantillaInicio);
        //
        mesnajeCorreo.append(this.getTitulo(asunto));
        mesnajeCorreo.append("<br/>");
        mesnajeCorreo.append(getMsgH3(msgContrato));
        mesnajeCorreo.append("<br/>");
        mesnajeCorreo.append(plantilla.getFin());
        return mesnajeCorreo;
    }

    
    public StringBuilder msjRequisitorOrdenDevuelta(Orden orden, List<OrdenDetalleVO> items) {
        StringBuilder notificacion = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_OrdenCompra);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezadoReq = new StringBuilder();
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezadoReq.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
        notificacion.append(plantillaInicio);
        //Aquí va todo el contenido del cuerpo,
        notificacion.append(cuerpoOrdenDevueltaRequisitor(orden, items));
        notificacion.append(plantilla.getFin());
        return notificacion;
    }

    private String cuerpoOrdenDevueltaRequisitor(Orden orden, List<OrdenDetalleVO> items) {
        StringBuilder mensajeOrden = new StringBuilder();
        try {
            mensajeOrden.append("<tr><td colspan=\"3\">");
            mensajeOrden.append("<table width=95% align=\"center\">");
            mensajeOrden.append("<tr>");
            mensajeOrden.append("<td>");
            mensajeOrden.append("<b> La orden de compra correspondiente a la requisición ").append(orden.getRequisicion().getConsecutivo()).append(" en proceso de aprobación fue DEVUELTA. </b>");
            mensajeOrden.append("</td></tr>");
            mensajeOrden.append("<tr>");
            mensajeOrden.append(getProyecto(orden));
            mensajeOrden.append(getControl(orden));
            mensajeOrden.append("</tr>");
            mensajeOrden.append("</table> ");
            mensajeOrden.append("</td></tr>");
            mensajeOrden.append(detalleOrdenRequisitor(items));
            mensajeOrden.append(getResponsables(orden));
//
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return mensajeOrden.toString();
    }

    
    public StringBuilder msjRequisitorOrdenCancelada(Orden orden, List<OrdenDetalleVO> items) {
        StringBuilder notificacion = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_OrdenCompra);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezadoReq = new StringBuilder();
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezadoReq.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
        notificacion.append(plantillaInicio);
        //Aquí va todo el contenido del cuerpo,
        notificacion.append(cuerpoOrdenCanceladaRequisitor(orden, items));
        notificacion.append(plantilla.getFin());
        return notificacion;
    }

    private String cuerpoOrdenCanceladaRequisitor(Orden orden, List<OrdenDetalleVO> items) {
        StringBuilder mensajeOrden = new StringBuilder();
        try {
            mensajeOrden.append("<tr><td colspan=\"3\">");
            mensajeOrden.append("<table width=95% align=\"center\">");
            mensajeOrden.append("<tr>");
            mensajeOrden.append("<td>");
            mensajeOrden.append("<b> La orden de compra correspondiente a la requisición ").append(orden.getRequisicion().getConsecutivo()).append(" en proceso de aprobación fue CANCELADA. </b>");
            mensajeOrden.append("</td></tr>");
            mensajeOrden.append("<tr>");
            mensajeOrden.append(getProyecto(orden));
            mensajeOrden.append(getControl(orden));
            mensajeOrden.append("</tr>");
            mensajeOrden.append("</table> ");
            mensajeOrden.append("</td></tr>");
            mensajeOrden.append(detalleOrdenRequisitor(items));
            mensajeOrden.append(getResponsables(orden));
//
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return mensajeOrden.toString();
    }

    
    public StringBuilder msjFormatoEntrada(String asunto, List<OrdenFormatoVo> formatos) {
        StringBuilder mesnajeCorreo = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_LogoSIA);
        this.limpiarCuerpoCorreo();
        String plantillaInicio = plantilla.getInicio();
        mesnajeCorreo.append(plantillaInicio);
        //
        mesnajeCorreo.append(this.getTitulo(asunto));
        mesnajeCorreo.append("<br/>");
        mesnajeCorreo.append("<p> Se envian los formatos de entrada registrados en el almacen </p>");
        mesnajeCorreo.append(listaFormatos(formatos));
        mesnajeCorreo.append(plantilla.getFin());
        return mesnajeCorreo;
    }

    private String listaFormatos(List<OrdenFormatoVo> lo) {
        StringBuilder sb = new StringBuilder();
        sb.append("<br/><table width=\"100%\" cellspacing=\"0\">");
        sb.append("<tr><th colspan=\"4\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Orden(es) de compra</th></tr>");
        sb.append("<tr>");
        sb.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Orden</th>");
        sb.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Pedido</th>");
        sb.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Proveedor</th>");
        sb.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Referencia</th>");
        sb.append("</tr>");
        for (OrdenFormatoVo ocForVo : lo) {
            sb.append("<tr>");
            sb.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(ocForVo.getOrden()).append("</td>");
            sb.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(ocForVo.getPedido()).append("</td>");
            sb.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(ocForVo.getProveedor()).append("</td>");
            sb.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(ocForVo.getReferencia()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table><br/>");
        return sb.toString();
    }

    /**
     *
     * @param orden
     * @param items
     * @param isCompleta
     * @return
     */
    
    public StringBuilder msjNotificacionRecepcionOrden(Orden orden, List<OrdenDetalleVO> items, boolean isCompleta) {
        StringBuilder solicitud = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(Plantilla_OrdenCompra);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezadoCorreo = new StringBuilder();
        encabezadoCorreo.append(getLigaSIA());
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezadoCorreo.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
        solicitud.append(plantillaInicio);
        solicitud.append("<br/>");
        solicitud.append("<tr><td colspan=\"3\">");
        solicitud.append("<p> La orden de compra <b>").append(orden.getConsecutivo()).append("</b> se recibió <b>").append(isCompleta ? " Completa" : " Parcial").append(".</p>");
        solicitud.append("</td></tr>");
        //Aquí va todo el contenido del cuerpo,

        solicitud.append("<tr><td colspan=\"3\">");
        solicitud.append("<table width=95% align=\"center\">");
        solicitud.append("<tr>");
        solicitud.append(getProyecto(orden));
        solicitud.append(getControl(orden));
        solicitud.append("</tr>");
        solicitud.append("</table> ");
        solicitud.append("</td></tr>");
        // Se encpsula la generacion de todas las secciones del cuerpo de la orden
        solicitud.append(detalleOrdenRecepcionRequisitor(items, isCompleta));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }

    private String detalleOrdenRecepcionRequisitor(List<OrdenDetalleVO> items, boolean isCompleta) {
        StringBuilder detalle = new StringBuilder();
        detalle.append("<tr><td style=\"width:100%;\" colspan = \"3\"><table width=95% align=\"center\"  cellspacing=\"0\">");
        detalle.append("<tr><th colspan=\"4\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; font-size:12px; background-color:#A8CEF0;\"> Partidas recibidas</th></tr>");
        detalle.append("<tr>");
        detalle.append("<th width=\"60%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Descripción</th>");
        detalle.append("<th width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Solicitado</th>");
        detalle.append("<th width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Recibido</th>");
        detalle.append("</tr>");
        for (OrdenDetalleVO item : items) {
            detalle.append("<tr>");
            detalle.append("<td width=\"60%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(item.getArtNombre()).append("</td>");
            detalle.append("<td width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">").append(item.getCantidad()).append("</td>");            
            if (item.getCantidad() == item.getTotalRecibido()) {
                detalle.append("<td width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">")
                        .append("Completado").append("</td>");
            } else {
                detalle.append("<td width=\"20%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">")
                        .append(item.getTotalPendiente()).append(" de ").append(item.getCantidad());
                detalle.append("</td>");
            }
            detalle.append("</tr>");
        }
        detalle.append("</table>");
        detalle.append("</td></tr>");
        detalle.append(getObservacionesPartidas(items));
        return detalle.toString();
    }

    
    public StringBuilder mensajeCartaIntencion(Orden orden, List<OrdenDetalleVO> items) {
        StringBuilder solicitud = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(Plantilla_OrdenCompra);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezadoCorreo = new StringBuilder();
        encabezadoCorreo.append(getLigaSIA());
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezadoCorreo.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
        plantillaInicio = replaceAll(plantillaInicio, "ORDEN DE COMPRA/SERVICIO", "Carta de Intención de compra de Producotos/Servicios");
        solicitud.append(plantillaInicio);
        solicitud.append("<br/>");
        solicitud.append("<tr><td colspan=\"3\">");
        solicitud.append("<p> Estimado <b> ").append(orden.getProveedor().getNombre()).append("</b>")
                .append(" se envía la Carta de Intención para la compra <b>").append(orden.getConsecutivo())
                .append("</b> referente a <b>").append(orden.getReferencia()).append("</b>")
                .append(". <br/> Para aceptar o rechazar la compra, favor de entrar al portal de proveedores, ")
                .append(" <a href =\"").append(Configurador.urlSia()).append("/Proveedor\">Clic aquí</a>");
        solicitud.append("</p></td></tr>");
        //Aquí va todo el contenido del cuerpo,

        solicitud.append("<tr><td colspan=\"3\">");
        solicitud.append("<table width=95% align=\"center\">");
        solicitud.append("<tr>");

        solicitud.append(encabezadoGenerico(orden));
        solicitud.append("</tr>");
        solicitud.append("</table> ");
        solicitud.append("</td></tr>");
        // Se encpsula la generacion de todas las secciones del cuerpo de la orden
        solicitud.append(detalleOrdenRequisitor(items));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }

    private StringBuilder encabezadoGenerico(Orden orden) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table style=\"width:100%\">");
        sb.append("<tr>");
        sb.append("<td style=\"background-color:#A8CEF0\">");
        sb.append("<b>Gerencia <br/></b>");
        sb.append(orden.getGerencia().getNombre());
        sb.append("</td>");
        sb.append("<td style=\"background-color:#A8CEF0\">");
        sb.append("<b>Fecha de entrega: <br/></b>");
        sb.append(Constantes.FMT_ddMMyyy.format(orden.getFechaEntrega()));
        sb.append("</td>");
        sb.append("<td style=\"background-color:#A8CEF0\">");
        sb.append("<b>Lugar de entrega: <br/></b>");
        sb.append(orden.getDestino());
        sb.append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<td style=\"background-color:#A8CEF0\">");
        sb.append("<b>Bloque: <br/></b>");
        sb.append(orden.getApCampo().getNombre());
        sb.append("</td>");
        sb.append("<td style=\"background-color:#A8CEF0\">");
        sb.append("<b>Referencia: <br/></b>");
        sb.append(orden.getReferencia());
        sb.append("</td>");
        sb.append("<td style=\"background-color:#A8CEF0\">");
        sb.append("<b>Contrato: <br/></b>");
        sb.append(orden.getContrato());
        sb.append("</td>");
        sb.append("</tr>");
        sb.append("</table>");
        return sb;
    }

    
    public StringBuilder mensajeRechazarRepse(Orden orden, List<OrdenDetalleVO> items, String motivo) {
        StringBuilder solicitud = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(Plantilla_OrdenCompra);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezadoCorreo = new StringBuilder();
        encabezadoCorreo.append(getLigaSIA());
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezadoCorreo.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
        plantillaInicio = replaceAll(plantillaInicio, "ORDEN DE COMPRA/SERVICIO", "Carta de Intención de compra de Producotos/Servicios");
        solicitud.append(plantillaInicio);
        solicitud.append("<br/>");
        solicitud.append("<tr><td colspan=\"3\">");
        solicitud.append("<p> Estimado <b> ").append(orden.getProveedor().getNombre()).append("</b>")
                .append(" se rechaza el <b> REPSE </b> por el siguiente motivo:<br/>")
                .append(motivo).append(".");
        solicitud.append("</p></td></tr>");
        //Aquí va todo el contenido del cuerpo,

        solicitud.append("<tr><td colspan=\"3\">");
        solicitud.append("<table width=95% align=\"center\">");
        solicitud.append("<tr>");
        solicitud.append(getProyecto(orden));
        solicitud.append(getControl(orden));
        solicitud.append("</tr>");
        solicitud.append("</table> ");
        solicitud.append("</td></tr>");
        // Se encpsula la generacion de todas las secciones del cuerpo de la orden
        solicitud.append(detalleOrdenRequisitor(items));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }

    
    public StringBuilder mensajeAceptarCarta(Orden orden, List<OrdenDetalleVO> items) {
        StringBuilder solicitud = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(Plantilla_OrdenCompra);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezadoCorreo = new StringBuilder();
        encabezadoCorreo.append(getLigaSIA());
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezadoCorreo.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
        if (orden.getApCampo().isCartaIntencion()) {
            plantillaInicio = replaceAll(plantillaInicio, "ORDEN DE COMPRA/SERVICIO", "Carta de Intención de compra de Producotos/Servicios");
        }
        solicitud.append(plantillaInicio);
        solicitud.append("<br/>");
        solicitud.append("<tr><td colspan=\"3\">");
        if (orden.getApCampo().isCartaIntencion()) {
            solicitud.append("<p> Se acepta la Carta de Intención para la compra <b>").append(orden.getConsecutivo())
                    .append("</b> referente a <b>").append(orden.getReferencia()).append("</b>.");
        }else{
            solicitud.append("<p> Revisar el archivo de REPSE para la compra <b>").append(orden.getConsecutivo())
                    .append("</b> referente a <b>").append(orden.getReferencia()).append("</b>.");
        }
        solicitud.append("</p></td></tr>");
        //Aquí va todo el contenido del cuerpo,

        solicitud.append("<tr><td colspan=\"3\">");
        solicitud.append("<table width=95% align=\"center\">");
        solicitud.append("<tr>");
        solicitud.append(getProyecto(orden));
        solicitud.append(getControl(orden));
        solicitud.append("</tr>");
        solicitud.append("</table> ");
        solicitud.append("</td></tr>");
        // Se encpsula la generacion de todas las secciones del cuerpo de la orden
        solicitud.append(detalleOrdenRequisitor(items));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }

    
    public StringBuilder mensajeRechazarCarta(Orden orden, List<OrdenDetalleVO> items, String motivo) {
        StringBuilder solicitud = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(Plantilla_OrdenCompra);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezadoCorreo = new StringBuilder();
        encabezadoCorreo.append(getLigaSIA());
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezadoCorreo.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", orden.getCompania().getNombre());
        plantillaInicio = replaceAll(plantillaInicio, "ORDEN DE COMPRA/SERVICIO", "Carta de Intención de compra de Producotos/Servicios");
        solicitud.append(plantillaInicio);
        solicitud.append("<br/>");
        solicitud.append("<tr><td colspan=\"3\">");
        solicitud.append("<p> Se <b>Rechaza</b> la Carta de Intención para la compra <b>").append(orden.getConsecutivo())
                .append("</b> referente al <b>").append(orden.getReferencia()).append("</b>. Por el siguiente motivo:<br/>")
                .append("<b>").append(motivo).append("</b>");
        solicitud.append("</p></td></tr>");
        //Aquí va todo el contenido del cuerpo,

        solicitud.append("<tr><td colspan=\"3\">");
        solicitud.append("<table width=95% align=\"center\">");
        solicitud.append("<tr>");
        solicitud.append(getProyecto(orden));
        solicitud.append(getControl(orden));
        solicitud.append("</tr>");
        solicitud.append("</table> ");
        solicitud.append("</td></tr>");
        // Se encpsula la generacion de todas las secciones del cuerpo de la orden
        solicitud.append(detalleOrdenRequisitor(items));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }

}
