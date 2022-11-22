/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.requisicion.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.constantes.TipoRequisicion;
import sia.correo.impl.CodigoHtml;
import sia.modelo.InvArticulo;
import sia.modelo.ReRequisicionEts;
import sia.modelo.Rechazo;
import sia.modelo.Requisicion;
import sia.modelo.SiPlantillaHtml;
import sia.modelo.requisicion.vo.RequisicionDetalleVO;
import sia.modelo.requisicion.vo.RequisicionMovimientoVO;
import sia.modelo.requisicion.vo.RequisicionReporteVO;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.modelo.sistema.vo.CategoriaVo;
import sia.servicios.requisicion.impl.ReRequisicionEtsImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiPlantillaHtmlImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@Stateless 
public class HtmlNotificacionRequisicionImpl extends CodigoHtml {

    @Inject
    private SiPlantillaHtmlImpl plantillaHtml;
    @Inject
    private RequisicionImpl requisicionRemote;
    @Inject
    private ReRequisicionEtsImpl reRequisicionEtsRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;

    
    public StringBuilder mensajeDevolucionRequisicion(Requisicion requisicion, String motivo) {
        StringBuilder msgDevolucion = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        msgDevolucion.append(plantilla.getInicio());
        msgDevolucion.append(this.getTitulo(new StringBuilder().append("Requisición --").append(requisicion.getConsecutivo()).toString()));
        //Aquí va todo el contenido del cuerpo,
        //Motivo
        msgDevolucion.append("<p>Se procedió con la devolución de la Requisición <b>").append(requisicion.getConsecutivo()).append("</b>, por el motivo que a continuación se menciona.</p>");
        msgDevolucion.append("<p> Motivo: <b>");
        msgDevolucion.append(motivo).append("</b></p>");
        //cuerpo de la requisicion
        msgDevolucion.append(getTituloRequisicion("Requisición Interna de Compras"));
        msgDevolucion.append(getCuerpoRequisicion(requisicion));
        msgDevolucion.append("</br>");
        msgDevolucion.append("</td></tr></table>");
        msgDevolucion.append(plantilla.getFin());
        return msgDevolucion;
    }

    private String getTituloRequisicion(String titulo) {
        StringBuilder tituloSB = new StringBuilder();
        tituloSB.append("<tr><td>");
        tituloSB.append("<table width=100%> <tr>");
        tituloSB.append("<td valign=middle width=100%><font face=arial size=-1>");
        tituloSB.append("<Br><Br> <center><table bgcolor=#bcbcbc width=90%><tr><td><font face=arial><center>").append(titulo).append("</td></tr></table><br>");
        return tituloSB.toString();
    }

    private String getCuerpoRequisicion(Requisicion requisicion) {
        StringBuilder cuerpo = new StringBuilder();
        String c15 = "<td bgcolor=#ffffff valign=middle style=\"text-align:left; width:10%;\"><font face=arial size=-1>";
        String c20 = "<td bgcolor=#ffffff valign=middle style=\"text-align:left; width:15%;\"><font face=arial size=-1>";
        cuerpo.append("<table width=95% align=\"center\">");

        cuerpo.append("<tr>");
        cuerpo.append(c15).append("Compañía </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullHtml(requisicion.getCompania().getNombre())).append("</b></font></td>");
        cuerpo.append(c20).append("No. de requisición </td><td style=\"text-align:right; width:25%;\"><font face=arial size=-1><b>");
        cuerpo.append(requisicion.getConsecutivo()).append("</b></font></td>");
        cuerpo.append("</tr>");

        cuerpo.append("<tr> ");
        cuerpo.append(c15).append("Gerencia </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullHtml(requisicion.getGerencia().getNombre())).append("</b></font></td>");
        cuerpo.append(c20).append("Solicitada </td><td style=\"text-align:right; width:25%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullFechaHtml(requisicion.getFechaSolicito())).append("</b></font></td>");
        cuerpo.append("</tr>");

        cuerpo.append("<tr>");
        cuerpo.append(c15).append("Proveedor </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullHtml(requisicion.getProveedor())).append("</b></font></td>");
        cuerpo.append(c20).append("Requerida </td><td style=\"text-align:right; width:25%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullFechaHtml(requisicion.getFechaRequerida())).append("</b></font></td>");
        cuerpo.append("</tr>");

//        cuerpo.append("<tr>");
//        cuerpo.append(c15).append("Categoría </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
//        cuerpo.append(c20).append("Prioridad </td><td style=\"text-align:right; width:25%;\"><font face=arial size=-1><b>");
//        cuerpo.append(validarNullHtml(requisicion.getPrioridad().getNombre())).append("</b></font></td>");
//        cuerpo.append("</tr>");
        cuerpo.append("<tr>");
        if (requisicion.getTipoObra() != null) {
            cuerpo.append(c15).append("Tipo de obra </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
            cuerpo.append(validarNullHtml(validarNullHtml(requisicion.getTipoObra().getNombre()))).append("</b></font></td>");
        } else if (requisicion.getOcUnidadCosto() != null) {
            cuerpo.append(c15).append(" </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
            cuerpo.append(" ").append("</b></font></td>");
        } else {
            cuerpo.append("<tr>");
            cuerpo.append(c15).append(" </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
            cuerpo.append(" ").append(" </b></font></td>");
        }
        cuerpo.append(c20).append("Prioridad </td><td style=\"text-align:right; width:25%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullHtml(requisicion.getPrioridad().getNombre())).append("</b></font></td>");
        cuerpo.append("</tr>");

        if (requisicion.getTipoObra() == null && requisicion.getOcUnidadCosto() == null) {
            cuerpo.append("<tr>");
            cuerpo.append(c15).append("&nbsp; </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
            cuerpo.append("&nbsp;</b></font></td>");
            cuerpo.append(c20).append("Lugar de entrega </td><td style=\"text-align:right; width:25%;\"><font face=arial size=-1><b>");
            cuerpo.append(validarNullHtml(requisicion.getLugarEntrega())).append("</b></font></td>");
            cuerpo.append("</tr>");

        } else {
            cuerpo.append("<tr>");
            cuerpo.append(c15).append(" </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
            cuerpo.append(" ").append(" </b></font></td>");
            cuerpo.append(c20).append("Lugar de entrega </td><td style=\"text-align:right; width:25%;\"><font face=arial size=-1><b>");
            cuerpo.append(validarNullHtml(requisicion.getLugarEntrega())).append("</b></font></td>");
            cuerpo.append("</tr>");

        }

        cuerpo.append("<tr>");
        cuerpo.append(c15).append("&nbsp; </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
        cuerpo.append("&nbsp;</b></font></td>");
        cuerpo.append("</tr>");

        cuerpo.append(getUserDet(requisicion));
        cuerpo.append("</table> ");
        cuerpo.append("</td></tr>");
        return cuerpo.toString();
    }

    private String getCuerpoSolNuevoArticulo(Requisicion requisicion, String descripcion, String uso, List<CategoriaVo> categoriasSeleccionadas, String uMedida) {
        StringBuilder cuerpo = new StringBuilder();
        String c15 = "<td bgcolor=#ffffff valign=middle style=\"text-align:left; width:10%;\"><font face=arial size=-1>";
        String c20 = "<td bgcolor=#ffffff valign=middle style=\"text-align:left; width:15%;\"><font face=arial size=-1>";
        cuerpo.append("<table width=95% align=\"center\">");

        cuerpo.append("<tr>");
        cuerpo.append(c15).append("Solicita: </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullHtml(requisicion.getSolicita().getNombre())).append("</b></font></td>");
        cuerpo.append("</tr>");

        cuerpo.append("<tr>");
        cuerpo.append(c15).append("Nombre del artículo: </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullHtml(descripcion.toUpperCase())).append("</b></font></td>");
        cuerpo.append("</tr>");

        //FIXME : no se deben contatenar cadenas con +=
        String categorias = "";
        for (CategoriaVo vo : categoriasSeleccionadas) {
            if (categorias.isEmpty()) {
                categorias += vo.getNombre().toUpperCase();
            } else {
                categorias += " > ";
                categorias += vo.getNombre().toUpperCase();
            }
        }

        if (uso != null && !uso.isEmpty()) {
            if (!categorias.isEmpty()) {
                categorias += " > <em style=\"background-color: rgb(214, 0, 0); font-style: inherit; color: rgb(0, 0, 0);\">";
            }
            categorias += uso.toUpperCase();
            categorias += "</em>";
        }

        cuerpo.append("<tr>");
        cuerpo.append(c15).append("El artículo será ubicado en: </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullHtml(categorias)).append("</b></font></td>");
        cuerpo.append("</tr>");

        cuerpo.append("<tr>");
        cuerpo.append(c15).append("Unidad de medida: </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullHtml(uMedida)).append("</b></font></td>");
        cuerpo.append("</tr>");

//	cuerpo.append(getUserDet(requisicion));
        cuerpo.append("</table> ");
        cuerpo.append("</td></tr>");
        return cuerpo.toString();
    }

    private String getCuerpoNuevoArticulo(InvArticulo articulo, List<CategoriaVo> categoriasSeleccionadas) {
        StringBuilder cuerpo = new StringBuilder();
        String c15 = "<td bgcolor=#ffffff valign=middle style=\"text-align:left; width:10%;\"><font face=arial size=-1>";
        String c20 = "<td bgcolor=#ffffff valign=middle style=\"text-align:left; width:15%;\"><font face=arial size=-1>";
        cuerpo.append("<table width=95% align=\"center\">");

        cuerpo.append("<tr>");
        cuerpo.append(c15).append("Nombre de Artículo: </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullHtml(articulo.getNombre())).append("</b></font></td>");
        cuerpo.append("</tr>");

        cuerpo.append("<tr>");
        cuerpo.append(c15).append("Unidad de medida: </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullHtml(articulo.getUnidad().getNombre())).append("</b></font></td>");
        cuerpo.append("</tr>");

        cuerpo.append("<tr>");
        cuerpo.append(c15).append("Código: </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullHtml(articulo.getCodigo())).append("</b></font></td>");
        cuerpo.append("</tr>");

        cuerpo.append("<tr>");
        cuerpo.append(c15).append("Código Interno: </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullHtml(articulo.getCodigoInt())).append("</b></font></td>");
        cuerpo.append("</tr>");

        cuerpo.append("<tr>");
        cuerpo.append(c15).append("Categoría: </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullHtml(articulo.getSiCategoria().getNombre())).append("</b></font></td>");
        cuerpo.append("</tr>");

        String categorias = "";
        for (CategoriaVo vo : categoriasSeleccionadas) {
            if (categorias.isEmpty()) {
                categorias += vo.getNombre();
            } else {
                categorias += " > ";
                categorias += vo.getNombre();
            }
        }

        cuerpo.append("<tr>");
        cuerpo.append(c15).append("Ubicación categorías: </td><td style=\"text-align:left; width:50%;\"><font face=arial size=-1><b>");
        cuerpo.append(validarNullHtml(categorias)).append("</b></font></td>");
        cuerpo.append("</tr>");

//	cuerpo.append(getUserDet(requisicion));
        cuerpo.append("</table> ");
        cuerpo.append("</td></tr>");
        return cuerpo.toString();
    }

    private String getRequisicionCompleta(Requisicion requisicion) {
        StringBuilder cuerpo = new StringBuilder();
        cuerpo.append(getCuerpoRequisicion(requisicion));
        cuerpo.append(getDetalleRequisicion(requisicion));
        cuerpo.append(getObservacionesPartidas(requisicion));
        cuerpo.append(getEts(requisicion));
        cuerpo.append(getResponsables(requisicion));
        cuerpo.append(getObservaciones(requisicion));
        return cuerpo.toString();
    }

    private String getNotificacionNuevoArticulo(Requisicion requisicion, String descripcion, String uso, List<CategoriaVo> categoriasSeleccionadas, String uMedida) {
        StringBuilder cuerpo = new StringBuilder();
        cuerpo.append(getCuerpoSolNuevoArticulo(requisicion, descripcion, uso, categoriasSeleccionadas, uMedida));
        return cuerpo.toString();
    }

    private String getNotificacionArticulo(InvArticulo articulo, List<CategoriaVo> categoriasSeleccionadas) {
        StringBuilder cuerpo = new StringBuilder();
        cuerpo.append(getCuerpoNuevoArticulo(articulo, categoriasSeleccionadas));
        return cuerpo.toString();
    }

    private StringBuilder getUserDet(Requisicion requisicion) {
        StringBuilder userDet = new StringBuilder();
        userDet.append("<tr><td colspan='4' style=\"text-align:right;\"><font size=\"-2\" face=\"arial\">").append(requisicion.getSolicita().getId()).append("-").append(requisicion.getId()).append("</font></td></tr>");
        return userDet;
    }

    private String getDetalleRequisicion(Requisicion requisicion) {
        StringBuilder detalle = new StringBuilder();

        detalle.append("<tr>");
        detalle.append("<td colspan=\"3\">").append("<table width=95% align=\"center\" style=\"border:1px solid #A8CEF0;\">");
        detalle.append("<tr style=\"border:1px solid #A8CEF0\">");
        detalle.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; text-align:center;\" colspan=\"8\">");
        detalle.append("<font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;\">");
        detalle.append("<b>ITEMS</b>");
        detalle.append("</font>");
        detalle.append("</td>");
        detalle.append("</tr>");
        
        
        detalle.append("<tr>");
        detalle.append("<td colspan=\"3\">");

        a = 1;
        List<RequisicionDetalleVO> l = null;
        if(requisicion.isMultiproyecto()){
            l = requisicionRemote.getItemsPorRequisicionMulti(requisicion.getId(), true, false);
        } else {
            l = requisicionRemote.getItemsPorRequisicion(requisicion.getId(), true, false);
        }
        
        if (l != null) {
            detalle.append("<table style=\"width: 100%;\">");
            for (RequisicionDetalleVO Lista : l) {
                //--- Poner condiciòn de mostrar solo los Items  Autorizados y no disgregados
                if (Lista.isAutorizado()) {
                    detalle.append("<tr style=\"width: 100%\"><td style=\"width: 100%;\">");
                    detalle.append("<table style=\"border: #eee; border-style: solid; border-width: 2px;width: 100%;\">");
                    detalle.append(" <tr style=\"width: 100%\">");
                    detalle.append("  <td style=\"width: 8%\">");
                    detalle.append("   <div>");
                    detalle.append("    <div>");
                    detalle.append("     <strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Partida</span></strong>");
                    detalle.append("    </div>");
                    detalle.append("    <div>");
                    detalle.append("     <span style=\"margin-left: 25%; font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(a).append("</span>");
                    detalle.append("    </div>");
                    detalle.append("   </div>");
                    detalle.append("  </td>");
                    if (Lista.getIdpresupuesto() > 0) {
                        detalle.append("  <td style=\"width: 22%\">");
                        detalle.append("   <div>");
                        detalle.append("    <div>");
                        detalle.append("     <strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Presupuesto</span></strong>");
                        detalle.append("    </div>");
                        detalle.append("    <div>");
                        detalle.append("     <span ").append("title=\"").append(Lista.getPresupuestoCodigo()).append("\" style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(Lista.getPresupuestoNombre()).append("</span>");
                        detalle.append("    </div>");
                        detalle.append("   </div>");
                        detalle.append("  </td>");

                        detalle.append("  <td style=\"width: 30%\" colspan=\"2\">");
                        detalle.append("   <div>");
                        detalle.append("    <div>");
                        detalle.append("     <strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Presup Año y Mes</span></strong>");
                        detalle.append("    </div>");
                        detalle.append("    <div>");
                        detalle.append("     <span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Año ").append(Lista.getAnioPresupuesto()).append(" y Mes ").append(Lista.getMesPresupuesto()).append("</span>");
                        detalle.append("    </div>");
                        detalle.append("   </div>");
                        detalle.append("  </td>");
                    }
                    if (Lista.getIdpresupuesto() > 0) {
                        detalle.append("  <td style=\"width: 40%\">");
                    } else {
                        detalle.append("  <td style=\"width: 22%\">");
                    }
                    detalle.append("   <div>");
                    detalle.append("    <div>");
                    detalle.append("     <strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Proyecto OT</span></strong>");
                    detalle.append("    </div>");
                    detalle.append("    <div>");
                    if(requisicion.isMultiproyecto()){
                        detalle.append("     <span ").append("title=\"").append(Lista.getMultiProyectos()).append("\" style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">")
                                .append(Lista.getMultiProyectos() != null && Lista.getMultiProyectos().length() > 40 ? (Lista.getMultiProyectos().substring(0, 40)+"...") : Lista.getMultiProyectos()).append("</span>");
                    } else {
                        detalle.append("     <span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(Lista.getProyectoOt()).append("</span>");
                    }
                    
                    detalle.append("    </div>");
                    detalle.append("   </div>");
                    detalle.append("  </td>");
                    
                    if (Lista.getIdpresupuesto() > 0) {
                        detalle.append("</tr>");
                        detalle.append("<tr style=\"width: 100%\">");
                    }
                    
                    detalle.append("  <td style=\"width: 30%\" colspan=\"2\">");
                    detalle.append("<div>");
                    detalle.append("<div>");
                    detalle.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">");
                    if ("C".equals(requisicion.getApCampo().getTipo())) {
                        detalle.append("Subactividad");
                    } else {
                        detalle.append("Tipo de Tarea");
                    }
                    detalle.append("</span></strong>");
                    detalle.append("</div>");
                    detalle.append("<div>");
                    detalle.append("<span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append((requisicion.getTipo().equals(TipoRequisicion.PS) || ("C".equals(requisicion.getApCampo().getTipo())))  ? validarNullHtml(Lista.getTipoTarea()) : "Activo Fijo").append("</span>");
                    detalle.append("</div>");
                    detalle.append("</div>");
                    detalle.append("</td>");
                    if ("C".equals(requisicion.getApCampo().getTipo())) {
                        detalle.append("<td style=\"width: 30%\" colspan=\"2\">");
                        detalle.append("<div>");
                        detalle.append("<div>");
                        detalle.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Tarea</span></strong>");
                        detalle.append("</div>");
                        detalle.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append((requisicion.getTipo().equals(TipoRequisicion.PS) || "C".equals(requisicion.getApCampo().getTipo())) ? validarNullHtml(Lista.getNombreTarea()) : "Activo Fijo").append("</span>");
                        detalle.append("</div>");
                        detalle.append("</div>");
                        detalle.append("</td>");
                        detalle.append("<td style=\"width: 40%\">");
                        detalle.append("<div>");
                        detalle.append("<div>");
                        detalle.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Subtarea</span></strong>");
                        detalle.append("</div>");
                        detalle.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append((requisicion.getTipo().equals(TipoRequisicion.PS) || "C".equals(requisicion.getApCampo().getTipo())) ? validarNullHtml(Lista.getSubTarea()) : "Activo Fijo").append("</span>");
                        detalle.append("</div>");
                        detalle.append("</div>");
                        detalle.append("</td>");
                    } else {
                        detalle.append("<td style=\"width: 40%\" colspan=\"3\">");
                        detalle.append("<div>");
                        detalle.append("<div>");
                        detalle.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Tarea</span></strong>");
                        detalle.append("</div>");
                        detalle.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append((requisicion.getTipo().equals(TipoRequisicion.PS) || "C".equals(requisicion.getApCampo().getTipo())) ? validarNullHtml(Lista.getNombreTarea()) : "Activo Fijo").append("</span>");
                        detalle.append("</div>");
                        detalle.append("</div>");
                        detalle.append("</td>");
                    }
                    detalle.append("</tr>");
                    detalle.append("<tr style=\"width: 100%\">");
                    detalle.append("<td style=\"width: 30%;\" colspan=\"2\">");
                    detalle.append("<div>");
                    detalle.append("<div>");
                    detalle.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Número parte</span></strong>");
                    detalle.append("</div>");
                    detalle.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(validarNullHtml(Lista.getArtNumeroParte())).append("</span>");
                    detalle.append("</div>");
                    detalle.append("</div>");
                    detalle.append("</td><td style=\"width: 15%\">");
                    detalle.append("<div>");
                    detalle.append("<div>");
                    detalle.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Cantidad</span></strong>");
                    detalle.append("</div>");
                    detalle.append("<div></div>");
                    detalle.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(Lista.getCantidadAutorizadaFormato()).append("</span>");
                    detalle.append("</div>");
                    detalle.append("</div>");
                    detalle.append("</td><td style=\"width: 15%\">");
                    detalle.append("<div>");
                    detalle.append("<div>");
                    detalle.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Unidad</span></strong>");
                    detalle.append("</div>");
                    detalle.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(validarNullHtml(Lista.getArtUnidad())).append("</span>");
                    detalle.append("</div>");
                    detalle.append("</div>");
                    if ("C".equals(requisicion.getApCampo().getTipo())) {
                        detalle.append("</td><td style=\"width: 40%\" >");
                    } else {
                        detalle.append("</td><td style=\"width: 40%\" >");
                    }
                    detalle.append("<div>");
                    detalle.append("<div>");
                    detalle.append("<strong><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">Descripción</span></strong>");
                    detalle.append("</div>");
                    detalle.append("<div><span style=\"font-size: -1; font-family: Arial, Helvetica, sans-serif;\">").append(validarNullHtml(Lista.getArtDescripcion())).append("</span>");
                    detalle.append("</div>");
                    detalle.append("</div>");

                    detalle.append("</td></tr></table></td></tr>");
                    a = a + 1;
                }
            }
            detalle.append(" </table></td></tr>");            
        }

        return detalle.toString();
    }

    private String getObservaciones(Requisicion requisicion) {
        StringBuilder observaciones = new StringBuilder();
        observaciones.append("<tr><td colspan = \"3\"><table width=95% align=\"center\">");
        if (requisicion.getObservaciones() != null && !requisicion.getObservaciones().isEmpty()) {
            observaciones.append("<TR><td style=\"text-align:left;\" colspan = \"2\"><font face=arial size=-1><b>Observaciones de la requisición:</b></td></TR>");
            observaciones.append("<TR><td style=\"text-align:left;width:3%;\"></td><td style=\"text-align:left;width:97%;\" colspan = \"2\">	<font face=arial size=-2>").append(requisicion.getObservaciones());
        }
        observaciones.append("</td></tr>");
        return observaciones.toString();
    }

    private String getObservacionesPartidas(Requisicion requisicion) {
        StringBuilder observaciones = new StringBuilder();
        observaciones.append("<tr><td colspan = \"3\"><table width=95% align=\"center\">");
        a = 1;
        boolean showTitle = true;
        List<RequisicionDetalleVO> l = null;
        if(requisicion.isMultiproyecto()){
            l = requisicionRemote.getItemsPorRequisicionMulti(requisicion.getId(), true, false);
        } else {
            l = requisicionRemote.getItemsPorRequisicion(requisicion.getId(), true, false);
        }
        if (l != null) {
            for (RequisicionDetalleVO Lista : l) {
                if (Lista.isAutorizado()) {
                    if (Lista.getObservacion() != null && !Lista.getObservacion().trim().isEmpty()) {
                        if (showTitle) {
                            observaciones.append("<TR><td style=\"text-align:left;\" colspan = \"2\"><font face=arial size=-1><b>Observaciones de las partidas:</b></td>").append("</tr>");
                            showTitle = false;
                        }
                        observaciones.append("<TR><td style=\"text-align:left;width:97%;\" colspan = \"2\"><font face=arial size=2>").append("<strong>Partida ").append(a).append(":</strong> ").append(Lista.getObservacion()).append("</td></tr>");
                    }
                }
                a = a + 1;
            }
        }
        observaciones.append("</table><br>");
        observaciones.append("</td></tr>");
        observaciones.append("</td></tr>");
        return observaciones.toString();
    }

    private String getResponsables(Requisicion requisicion) {
        StringBuilder responsables = new StringBuilder();

        responsables.append("<tr>").append("<td colspan = \"3\">").append("<table width=\"90%\" align=\"center\">").append("<tr>").append("<td bgcolor=\"#A8CEF0\">").append("<center><font size=\"-1\" face=\"arial\">").append("<center>Solicita</center>").append("</font></center><font size=\"-1\" face=\"arial\">").append("</font></td>").append("<td bgcolor=\"#A8CEF0\">").append("<center><font size=\"-1\" face=\"arial\">").append("<center>Revisa</center>").append("</font></center><font size=\"-1\" face=\"arial\">").append("</font></td>").append("<td bgcolor=\"#A8CEF0\">").append("<center><font size=\"-1\" face=\"arial\">").append("<center>Aprueba</center>").append("</font></center><font size=\"-1\" face=\"arial\">").append("</font></td>").append("</tr>").append("<tr>").append("<td width=\"33%\">&nbsp;</td>").append("<td width=\"33%\">&nbsp;</td>").append("<td width=\"33%\">&nbsp;</td>").append("</tr>").append("<tr>").append("<td>&nbsp;</td>").append("<td>&nbsp;</td>").append("<td>&nbsp;</td>").append("<td>&nbsp;</td>").append("</tr>").append("<tr>").append("<td>").append("<center><font size=\"-1\" face=\"arial\">").append("<center>").append(requisicion.getSolicita().getNombre()).append("</center>").append("</font></center><font size=\"-1\" face=\"arial\">").append("</font></td>").append("<td>").append("<center><font size=\"-1\" face=\"arial\">").append("<center>").append(requisicion.getRevisa().getNombre()).append("</center>").append("</font></center><font size=\"-1\" face=\"arial\">").append("</font></td>").append("<td>").append("<center><font size=\"-1\" face=\"arial\">").append("<center>").append(requisicion.getAprueba().getNombre()).append("</center>").append("</font></center>").append("</td>").append("</tr>").append("</table>").append("<br>").append("<center></center>").append("</td>").append("</tr>");

        return responsables.toString();
    }

    private String getEts(Requisicion requisicion) {
        StringBuilder cuerpoEtsSB = new StringBuilder();

        if (!this.reRequisicionEtsRemote.traerAdjuntosPorRequisicion(requisicion.getId()).isEmpty()) {
            cuerpoEtsSB.append("<tr><td colspan=\"3\">");
            cuerpoEtsSB.append("<table width=\"95%\" align=\"center\" style=\"border:1px solid #A8CEF0\">");
            cuerpoEtsSB.append("<tr style=\"border:1px solid #A8CEF0\">");
            cuerpoEtsSB.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; text-align:left;\" colspan=\"4\">");
            cuerpoEtsSB.append("<center><font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;\">");
            cuerpoEtsSB.append("<b>ESPECIFICACIÓN TÉCNICA DE SUMINISTRO</b>");
            cuerpoEtsSB.append("</font></center>");
            cuerpoEtsSB.append("</td>");
            cuerpoEtsSB.append("</tr>");
            cuerpoEtsSB.append("<tr style=\"border:1px solid #A8CEF0\">");
            cuerpoEtsSB.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:10%;\"><font color=\"000000\" size=\"-1\" face=\"arial\"><center>Número</center></font></td>");
            cuerpoEtsSB.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:40%;\"><font color=\"000000\" size=\"-1\" face=\"arial\"><center>Nombre</center></font></td>");
            cuerpoEtsSB.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:45%;\"><font color=\"000000\" size=\"-1\" face=\"arial\"><center>Descripción</center></font></td>");
            cuerpoEtsSB.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:5%;\"><font color=\"000000\" size=\"-1\" face=\"arial\"><center></center></font></td>");
            cuerpoEtsSB.append("</tr>");

            a = 1;
            String bgColor = "bgcolor=\"#FFFFFF\"";
            for (ReRequisicionEts ets : this.reRequisicionEtsRemote.traerAdjuntosPorRequisicion(requisicion.getId())) {
                if (getModulo(a).equals(0)) {
                    bgColor = "bgcolor=\"#E4EAEB\"";
                } else {
                    bgColor = "bgcolor=\"#FFFFFF\"";
                }

                cuerpoEtsSB.append("<tr>");
                cuerpoEtsSB.append("<td valign=\"middle\" ").append(bgColor).append(" style=\"border:1px solid #A8CEF0; width:10%;\"><font size=\"-1\" face=\"arial\"><center>").append(a).append("</center></font></td>");
                cuerpoEtsSB.append("<td valign=\"middle\" ").append(bgColor).append(" style=\"border:1px solid #A8CEF0; width:40%;\"><font size=\"-1\" face=\"arial\">");
                if (ets.getSiAdjunto() != null
                        && ets.getSiAdjunto().getNombre() != null
                        && !ets.getSiAdjunto().getNombre().isEmpty()) {
                    cuerpoEtsSB.append(ets.getSiAdjunto().getNombreSinUUID()).append("</font></td>");
                } else {
                    cuerpoEtsSB.append("&nbsp;").append("</font></td>");
                }

                cuerpoEtsSB.append("<td valign=\"middle\" ").append(bgColor).append(" style=\"border:1px solid #A8CEF0; width:45%;\"><font size=\"-1\" face=\"arial\">");
                if (ets.getSiAdjunto() != null
                        && ets.getSiAdjunto().getDescripcion() != null
                        && !ets.getSiAdjunto().getDescripcion().isEmpty()) {
                    cuerpoEtsSB.append(ets.getSiAdjunto().getDescripcion()).append("</font></td>");
                } else {
                    cuerpoEtsSB.append("&nbsp;").append("</font></td>");
                }

                cuerpoEtsSB.append("<td valign=\"middle\" ").append(bgColor).append(" style=\"border:1px solid #A8CEF0; width:5%;\"><font size=\"-1\" face=\"arial\"><center>");
                cuerpoEtsSB.append("<a target=\"_blank\" ");
                cuerpoEtsSB.append("href=").append(Configurador.urlSia()).append("Compras/OFWSS?Z4BX2=SIA&ZWZ4W=").append(ets.getSiAdjunto().getId()).append("&ZWZ3W=").append(ets.getSiAdjunto().getUuid()).append(">");
                cuerpoEtsSB.append("Abrir");
                cuerpoEtsSB.append("</a></center></font></td>");
                cuerpoEtsSB.append("</tr>");
                a++;
            }
            cuerpoEtsSB.append("</table><br><center></td></tr><tr><td>&nbsp;</td></tr>");
        }
        return cuerpoEtsSB.toString();
    }

    private String replaceAll(String origen, String llave, String replace) {
        replace = replace.replaceAll("\\$", "\\\\\\$");
        return origen.replaceAll(llave, replace);
    }

    
    public StringBuilder mensajeSolicitudRequisicion(Requisicion requisicion, boolean conPlantilla) {
        StringBuilder solicitud = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_Requisicion);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(" ");
        if (conPlantilla) {
            String plantillaInicio = plantilla.getInicio();
            plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
            plantillaInicio = replaceAll(plantillaInicio, "@@2@@", requisicion.getCompania().getNombre());
            solicitud.append(plantillaInicio);
        } else {
            solicitud.append(encabezado.toString());
        }
        //Aquí va todo el contenido del cuerpo,

        //cuerpo de la requisicion
        solicitud.append("<tr><td colspan=\"3\">");
        solicitud.append(getRequisicionCompleta(requisicion));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }

    
    public StringBuilder mensajeFinalizarRequisicion(Requisicion requisicion, boolean conPlantilla, String... tipos) {
        StringBuilder solicitud = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_Requisicion);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(getNotificacionConMotivo(requisicion,
                new StringBuilder().append(requisicion.getConsecutivo()).append(" FINALIZADA").toString(), null, tipos));
        if (conPlantilla) {
            String plantillaInicio = plantilla.getInicio();
            plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
            plantillaInicio = replaceAll(plantillaInicio, "@@2@@", requisicion.getCompania().getNombre());
            solicitud.append(plantillaInicio);
        } else {
            solicitud.append(encabezado.toString());
        }
        //Aquí va todo el contenido del cuerpo,

        //cuerpo de la requisicion
        solicitud.append("<tr><td colspan=\"3\">");
        solicitud.append(getRequisicionCompleta(requisicion));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }
    
    
    public StringBuilder mensajeEsperaRequisicion(Requisicion requisicion, boolean conPlantilla, String... tipos) {
        StringBuilder solicitud = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_Requisicion);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(getNotificacionConMotivo(requisicion,
                new StringBuilder().append(requisicion.getConsecutivo()).append(" EN ESPERA").toString(), null, tipos));
        if (conPlantilla) {
            String plantillaInicio = plantilla.getInicio();
            plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
            plantillaInicio = replaceAll(plantillaInicio, "@@2@@", requisicion.getCompania().getNombre());
            solicitud.append(plantillaInicio);
        } else {
            solicitud.append(encabezado.toString());
        }
        //Aquí va todo el contenido del cuerpo,

        //cuerpo de la requisicion
        solicitud.append("<tr><td colspan=\"3\">");
        solicitud.append(getRequisicionCompleta(requisicion));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }

    
    public StringBuilder mensajeRechazoRequisicion(Requisicion requisicion, Rechazo rechazo, boolean conPlantilla) {
        StringBuilder solicitud = new StringBuilder();
        try {
            SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_Requisicion);
            this.limpiarCuerpoCorreo();
            StringBuilder encabezado = new StringBuilder();
            String titulo = new StringBuilder().append(requisicion.getConsecutivo()).append(" DEVUELTA").toString();
            encabezado.append(getNotificacionConMotivo(requisicion, titulo, rechazo, "devolver"));
            if (conPlantilla) {
                String plantillaInicio = plantilla.getInicio();
                plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
                plantillaInicio = replaceAll(plantillaInicio, "@@2@@", requisicion.getCompania().getNombre());
                solicitud.append(plantillaInicio);
            } else {
                solicitud.append(encabezado.toString());
            }
            //Aquí va todo el contenido del cuerpo,

            //cuerpo de la requisicion
            solicitud.append("<tr><td colspan=\"3\">");
            solicitud.append(getRequisicionCompleta(requisicion));
            solicitud.append(plantilla.getFin());
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Cambio en el llamdo al metodo de getNotificacionConMotivo() # # ## # #  ##   " + e.getMessage());
        }
        return solicitud;
    }

    
    public StringBuilder mensajeNotaRequisicion(Requisicion requisicion, String asunto, String autor, String nota, boolean conPlantilla, String... tipos) {
        StringBuilder solicitud = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_Requisicion);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(getNotaNotificacion(autor, nota));
        encabezado.append(getLigaSIA());
        //encabezado.append(getTablaNotificacion(requisicion, null, tipos));
        if (conPlantilla) {
            String plantillaInicio = plantilla.getInicio();
            plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
            plantillaInicio = replaceAll(plantillaInicio, "@@2@@", requisicion.getCompania().getNombre());
            solicitud.append(plantillaInicio);
        } else {
            solicitud.append(encabezado.toString());
        }
        //Aquí va todo el contenido del cuerpo,

        //cuerpo de la requisicion
        solicitud.append("<tr><td colspan=\"3\">");
        solicitud.append(getRequisicionCompleta(requisicion));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }

    
    public StringBuilder mensajeAutorizadaRequisicion(Requisicion requisicion, boolean conPlantilla, String... tipos) {
        StringBuilder notificacion = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_Requisicion);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(" ");
        if (conPlantilla) {
            String plantillaInicio = plantilla.getInicio();
            plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
            plantillaInicio = replaceAll(plantillaInicio, "@@2@@", requisicion.getCompania().getNombre());
            notificacion.append(plantillaInicio);
        } else {
            notificacion.append(encabezado.toString());
        }
        //Aquí va todo el contenido del cuerpo,

        //cuerpo de la requisicion
        notificacion.append(getTablaNotificacion(requisicion, null, tipos));
        notificacion.append(getAnalista(requisicion));
        notificacion.append(plantilla.getFin());
        return notificacion;
    }
    
    
    public StringBuilder mensajeActivarRequisicion(Requisicion requisicion, boolean conPlantilla, RequisicionMovimientoVO moo, String... tipos) {
        StringBuilder notificacion = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_Requisicion);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(" ");
        if (conPlantilla) {
            String plantillaInicio = plantilla.getInicio();
            plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
            plantillaInicio = replaceAll(plantillaInicio, "@@2@@", requisicion.getCompania().getNombre());
            notificacion.append(plantillaInicio);
        } else {
            notificacion.append(encabezado.toString());
        }
        //Aquí va todo el contenido del cuerpo,

        //cuerpo de la requisicion
        notificacion.append(getTablaNotificacion(requisicion, moo, tipos));
        notificacion.append(getAnalista(requisicion));
        notificacion.append(plantilla.getFin());
        return notificacion;
    }

    
    public StringBuilder mensajeNotificacionRequisicion(Requisicion requisicion, boolean conPlantilla, String... tipos) {
        StringBuilder notificacion = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_Requisicion);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(getTablaNotificacion(requisicion, null, tipos));
        encabezado.append(getLigaSIA());
        if (conPlantilla) {
            String plantillaInicio = plantilla.getInicio();
            plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
            plantillaInicio = replaceAll(plantillaInicio, "@@2@@", requisicion.getCompania().getNombre());
            notificacion.append(plantillaInicio);
        } else {
            notificacion.append(encabezado.toString());
        }
        //Aquí va todo el contenido del cuerpo,

        //cuerpo de la requisicion
        notificacion.append("<tr><td colspan=\"3\">");
        notificacion.append(getRequisicionCompleta(requisicion));
        notificacion.append(plantilla.getFin());
        return notificacion;
    }

    
    public StringBuilder mensajeNotificacionAltaNuevoArticulo(Requisicion requisicion, boolean conPlantilla, String descripcion, String uso, List<CategoriaVo> categoriasSeleccionadas, String uMedida) {
        StringBuilder notificacion = new StringBuilder();

        //FIXME : en las constantes no aplica el this
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_Requisicion);

        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        String plantillaFin = " </table> </td> </tr>	<tr><td style='width:2.5%;'></td>"
                + "<td style='width:95%; text-align:center; padding:3px; border-bottom-left-radius:50px; border-bottom-right-radius:50px; background-color:#A8CEF0; color:#004181; font-size: 9px;'>"
                + " Notificación generada automáticamente, por el Sistema Integral de Administración.<br/> </td>"
                + "<td style='width:2.5%;'></td>  	</tr></table></body></html> ";
        if (conPlantilla) {
            String plantillaInicio = " <!DOCTYPE html><html><head><meta charset='utf-8' /><meta name='author' content='jcarranza'/>"
                    + " <meta name='description' content='Requsición'/><title>Notificación</title></head><body style='font-family:Verdana,Arial,lucida,sans-serif; font-size:12px; margin:10px auto' fpstyle='1' ocsi='1'><br><br>"
                    + " <br><br><table width='95%' cellspacing='0' cellpadding='0' align='center'>@@1@@"
                    + " <tr><td colspan='3'><table width='95%' border='0' align='center' style='background-color:#fefefe; border:1px solid #A8CEF0; padding:0px 5px 5px 5px; word-spacing:2px'>"
                    + " <tr><td colspan='3'><center><br><br><table width='95%' style='font-family:Gill,Helvetica,sans-serif; font-size:12px'>"
                    + " <tr> <td style='text-align:center; width:15%;'> <img src='cid:logoCompany' width='95px' height='45px;'  />  </td>                                <td style='text-align:center; width:70%;'><h4>@@2@@</h4></td>"
                    + "                                 <td style='text-align:center; width:15%;'> <img src='cid:logoEsr' width='95px' height='45px;' />  </td>"
                    + " </tr></table></center></td></tr><tr><td colspan='3'>"
                    + " <table width='95%' align='center' style='border:1px solid #A8CEF0'><tr>"
                    + " <td valign='middle' style='background-color:#A8CEF0; border:1px solid #A8CEF0'>"
                    + " <table align='center' style='background-color:#A8CEF0'>"
                    + " <tr><td><font color='black' face='font-family: Gill, Helvetica, sans-serif; font-size:11px;'>"
                    + " <center><b> Solicitud de registro de un nuevo artículo </b> </center>"
                    + " </font></td></tr></table></td></tr></table></td></tr>";
            plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
            plantillaInicio = replaceAll(plantillaInicio, "@@2@@", requisicion.getCompania().getNombre());
            notificacion.append(plantillaInicio);
        } else {
            notificacion.append(encabezado.toString());
        }
        //Aquí va todo el contenido del cuerpo,

        //cuerpo de la requisicion
        notificacion.append("<tr><td colspan=\"3\">");
        notificacion.append(getNotificacionNuevoArticulo(requisicion, descripcion, uso, categoriasSeleccionadas, uMedida));
        notificacion.append(plantillaFin);
        return notificacion;
    }

    
    public StringBuilder mensajeNotificacionAltaArticulo(InvArticulo articulo, boolean conPlantilla, String compania, List<CategoriaVo> categoriasSeleccionadas) {
        StringBuilder notificacion = new StringBuilder();

        //FIXME : en las constantes no aplica el this
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_Requisicion);

        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        String plantillaFin = " </table> </td> </tr>	<tr><td style='width:2.5%;'></td>"
                + "<td style='width:95%; text-align:center; padding:3px; border-bottom-left-radius:50px; border-bottom-right-radius:50px; background-color:#A8CEF0; color:#004181; font-size: 9px;'>"
                + " Notificación generada automáticamente, por el Sistema Integral de Administración.<br/> </td>"
                + "<td style='width:2.5%;'></td>  	</tr></table></body></html> ";
        if (conPlantilla) {
            String plantillaInicio = " <!DOCTYPE html><html><head><meta charset='utf-8' /><meta name='author' content='jcarranza'/>"
                    + " <meta name='description' content='Requsición'/><title>Notificación</title></head><body style='font-family:Verdana,Arial,lucida,sans-serif; font-size:12px; margin:10px auto' fpstyle='1' ocsi='1'><br><br>"
                    + " <br><br><table width='95%' cellspacing='0' cellpadding='0' align='center'>@@1@@"
                    + " <tr><td colspan='3'><table width='95%' border='0' align='center' style='background-color:#fefefe; border:1px solid #A8CEF0; padding:0px 5px 5px 5px; word-spacing:2px'>"
                    + " <tr><td colspan='3'><center><br><br><table width='95%' style='font-family:Gill,Helvetica,sans-serif; font-size:12px'>"
                    + " <tr> <td style='text-align:center; width:15%;'> <img src='cid:logoCompany' width='95px' height='45px;'  />  </td>                                <td style='text-align:center; width:70%;'><h4>@@2@@</h4></td>"
                    + "                                 <td style='text-align:center; width:15%;'> <img src='cid:logoEsr' width='95px' height='45px;' />  </td>"
                    + " </tr></table></center></td></tr><tr><td colspan='3'>"
                    + " <table width='95%' align='center' style='border:1px solid #A8CEF0'><tr>"
                    + " <td valign='middle' style='background-color:#A8CEF0; border:1px solid #A8CEF0'>"
                    + " <table align='center' style='background-color:#A8CEF0'>"
                    + " <tr><td><font color='black' face='font-family: Gill, Helvetica, sans-serif; font-size:11px;'>"
                    + " <center><b> Notificación de registro de artículo </b> </center>"
                    + " </font></td></tr></table></td></tr></table></td></tr>";
            plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
            plantillaInicio = replaceAll(plantillaInicio, "@@2@@", compania);
            notificacion.append(plantillaInicio);
        } else {
            notificacion.append(encabezado.toString());
        }
        notificacion.append("<tr><td colspan=\"3\">La solicitud de alta del articulo al Sia, ha sido procesada y ya puede ser utilizado.</td></tr>");
        notificacion.append("<tr><td colspan=\"3\">");
        notificacion.append(getNotificacionArticulo(articulo, categoriasSeleccionadas));
        notificacion.append(plantillaFin);
        return notificacion;
    }

    
    public StringBuilder mensajeCambioCatRequisicion(Requisicion requisicion, boolean conPlantilla, String... tipos) {
        StringBuilder notificacion = new StringBuilder();

        //FIXME : en las constantes no aplica el this
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_Requisicion);

        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(getTablaNotificacion(requisicion, null, tipos));
        encabezado.append(getLigaSIA());
        if (conPlantilla) {
            String plantillaInicio = plantilla.getInicio();
            plantillaInicio = plantillaInicio.replaceAll("@@1@@", encabezado.toString());
            plantillaInicio = plantillaInicio.replaceAll("@@2@@", requisicion.getCompania().getNombre());
            notificacion.append(plantillaInicio);
        } else {
            notificacion.append(encabezado.toString());
        }
        //Aquí va todo el contenido del cuerpo,

        //cuerpo de la requisicion
        notificacion.append("<tr><td colspan=\"3\">");
        notificacion.append(getRequisicionCompleta(requisicion));
        notificacion.append(plantilla.getFin());
        return notificacion;
    }

    
    public StringBuilder mensajeNotificacionRequisicionPDF(Requisicion requisicion, boolean conPlantilla, String... tipos) {
        StringBuilder notificacion = new StringBuilder();

        //FIXME : en las constantes no aplica el this
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_Requisicion);

        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(" ");
        encabezado.append(" ");
        if (conPlantilla) {
            String plantillaInicio = getInicioPlantilaAsignacion();
            plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
            plantillaInicio = replaceAll(plantillaInicio, "@@2@@", requisicion.getCompania().getNombre());
            notificacion.append(plantillaInicio);
        } else {
            notificacion.append(encabezado.toString());
        }
        notificacion.append(getMsgAsignacion(requisicion));
        notificacion.append(getLigaSIA());
        notificacion.append(plantilla.getFin());
        return notificacion;
    }

    private String getMsgAsignacion(Requisicion requisicion) {
        StringBuilder requisitos = new StringBuilder();
        requisitos.append("<tr><td colspan = \"3\"><table width=90% align=\"center\">");
        requisitos.append("<TR><td style=\"text-align:left;width:30%;\" colspan = \"3\"><font face=arial size=-1>&nbsp;");
        requisitos.append("</td><td style=\"text-align:left;width:30%;\"></td><td style=\"text-align:left;width:40%;\"></td></tr>");
        requisitos.append("<TR><td style=\"text-align:left;width:100%;\" colspan = \"3\">	<font face=arial size=-1><b>");
        requisitos.append("Por favor colocar la orden de compra. Para lo cual se adjunta el siguiente archivo: ");
        requisitos.append("</td></TR>");
        requisitos.append("<TR><td style=\"text-align:left;width:100%;\" colspan = \"3\">	<font face=arial size=-1>");
        requisitos.append(1).append(".    Requisición: ").append(requisicion.getConsecutivo()).append(" (PDF)").append("</td></tr>");
        requisitos.append("</table>");
        requisitos.append("</td></tr><tr><td>&nbsp;</td></tr>");
        return requisitos.toString();
    }

    
    public StringBuilder mensajeCancelacionRequisicion(Requisicion requisicion, boolean conPlantilla, String... tipos) {
        StringBuilder notificacion = new StringBuilder();

        //FIXME : en las constantes no aplica el this
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_Requisicion);

        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(getNotificacionConMotivo(requisicion,
                new StringBuilder().append(requisicion.getConsecutivo()).append(" CANCELADA").toString(), null, tipos));
        encabezado.append(getLigaSIA());
        if (conPlantilla) {
            String plantillaInicio = plantilla.getInicio();
            plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
            plantillaInicio = replaceAll(plantillaInicio, "@@2@@", requisicion.getCompania().getNombre());
            notificacion.append(plantillaInicio);
        } else {
            notificacion.append(encabezado.toString());
        }

        notificacion.append("<tr><td colspan=\"3\">");
        notificacion.append(getRequisicionCompleta(requisicion));
        notificacion.append(plantilla.getFin());
        return notificacion;
    }

    private String getAnalista(Requisicion requisicion) {
        StringBuilder tabla = new StringBuilder();

        //FIXME : esto genera múltiples invocaciones al objeto tabla degradando el desempeño
        tabla.append("<tr>");
        tabla.append("<td colspan=\"3\">");
        tabla.append("<table width=\"95%\" align=\"center\" style=\"border:1px solid #A8CEF0\">");
        tabla.append("<tr>");
        tabla.append("<td bgcolor=\"#A8CEF0\" style=\"border:1px solid #A8CEF0; width:95%\"><font color=\"000000\" size=\"-1\" face=\"arial\">");
        tabla.append("<center>Analista asignado para colocar la orden de compra y/o servicio:").append(validarNullHtml(requisicion.getCompra().getNombre())).append(" </center>");
        tabla.append("</font></td>");
        tabla.append("</tr>");
        tabla.append("</table><br><center></center></td></tr>");
        return tabla.toString();
    }

    private String getTablaNotificacion(Requisicion requisicion, RequisicionMovimientoVO moo, String... tipos) {
        StringBuilder tabla = new StringBuilder();
        
        tabla.append("<tr>");
        tabla.append("<td colspan=\"3\">");
        tabla.append("<table width=\"95%\" align=\"center\" style=\"border:1px solid #A8CEF0\">");
        tabla.append("<tr>");
        tabla.append("<td bgcolor=\"#A8CEF0\" colspan=\"4\" style=\"border:1px solid #A8CEF0; text-align:center\">");
        tabla.append("<font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;\"><b>HISTORIAL DE AUTORIZACIÓN</b></font></td>");
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

        for (String tipo : tipos) {
            if ("solicito".equals(tipo)) {
                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append("Solicitó");
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(requisicion.getSolicita().getNombre());
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(validarNullFechaHtml(requisicion.getFechaSolicito()));
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append(validarNullHoraHtml(requisicion.getHoraSolicito()));
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("</tr>");

            }
            if ("revisar".equals(tipo)) {
                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append("Revisó");
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(requisicion.getRevisa().getNombre());
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(validarNullFechaHtml(requisicion.getFechaReviso()));
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append(validarNullHoraHtml(requisicion.getHoraReviso()));
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("</tr>");
            }
            if ("aprobar".equals(tipo)) {
                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append("Aprobó");
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(requisicion.getAprueba().getNombre());
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(validarNullFechaHtml(requisicion.getFechaAprobo()));
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append(validarNullHoraHtml(requisicion.getHoraAprobo()));
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("</tr>");
            }
            if ("rechazar".equals(tipo)) {
                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\" colspan=\"4\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append("No fue aceptado el cambio de la categoría en la requisición con el número: [<b>");
                tabla.append(validarNullHtml(requisicion.getConsecutivo()));
                tabla.append("</b>]");
                tabla.append("</center></font>");
                tabla.append("</td>");
                tabla.append("</tr>");

            }
            if ("activar".equals(tipo)) {
                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append("Reactivó");
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(moo.getUsuario());
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(validarNullFechaHtml(moo.getFecha()));
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append(validarNullHoraHtml(moo.getHora()));
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("</tr>");
            }
        }

        tabla.append("</table><br><center></center></td></tr>");

        return tabla.toString();
    }

    private String getNotaNotificacion(String autor, String nota) {
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
        tabla.append("Genero");
        tabla.append("</center>");
        tabla.append("</font></td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
        tabla.append("<font size=\"-1\" face=\"arial\"><center>");
        tabla.append(autor);
        tabla.append("</center></font></td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
        tabla.append("<font size=\"-1\" face=\"arial\"><center>");
        tabla.append(Constantes.FMT_ddMMyyy.format(new Date()));
        tabla.append("</center></font></td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\">");
        tabla.append("<font size=\"-1\" face=\"arial\">");
        tabla.append("<center>");
        tabla.append(Constantes.FMT_HHmmss.format(new Date()));
        tabla.append("</center>");
        tabla.append("</font></td>");
        tabla.append("</tr>");
        tabla.append("<tr>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\" colspan=\"1\">");
        tabla.append("<font face=arial size=-1> <P ALIGN=left> NOTA: </font>");
        tabla.append("</td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\" colspan=\"3\">");
        tabla.append("<font face=arial size=-1> <P ALIGN=left>").append(nota).append("</font>");
        tabla.append("</td>");
        tabla.append("</tr>");
        tabla.append("<tr>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:25%\" colspan=\"4\">");
        tabla.append("<center>");
        tabla.append("Para responder la nota por favor entre al modulo de Notas Requisición del Sistema Integral de Administración.");
        tabla.append("</center>");
        tabla.append("</td>");

        tabla.append("</table><br><center></center></td></tr>");

        return tabla.toString();
    }

    private String getNotificacionConMotivo(Requisicion requisicion, String titulo, Object objeto, String... tipos) {
        UtilLog4j.log.info(this, "Esta recuperando el encabezado de requision devuelta . . . " + requisicion.getId());
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
        tabla.append("<td bgcolor=\"#A8CEF0\" colspan=\"5\" style=\"border:1px solid #A8CEF0; text-align:left\">");
        tabla.append("<font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;\"><center><b>").append(tablaTitulo.toString()).append("</b></font></center></td>");
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
            if ("cancelo".equals(tipo)) {
                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append("Canceló");
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(validarNullHtml(requisicion.getCancelo().getNombre()));
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(validarNullFechaHtml(requisicion.getFechaCancelo()));
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append(validarNullHoraHtml(requisicion.getHoraCancelo()));
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append(validarNullHtml(requisicion.getMotivoCancelo()));
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("</tr>");
            }

            if ("devolver".equals(tipo) && objeto != null) {
                Rechazo rechazo = (Rechazo) objeto;
                UtilLog4j.log.info(this, "Rechazo . . . " + rechazo.getRechazo().getNombre());
                UtilLog4j.log.info(this, "fecha . . . " + rechazo.getFecha());
                UtilLog4j.log.info(this, "Hora . . . " + rechazo.getHora());
                UtilLog4j.log.info(this, "Mot . . . " + rechazo.getMotivo());
                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append("Devolvió");
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(rechazo.getRechazo().getNombre());
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(validarNullFechaHtml(rechazo.getFecha()));
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append(validarNullHoraHtml(rechazo.getHora()));
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append(validarNullHoraHtml(rechazo.getMotivo()));
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("</tr>");
                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\" colspan=\"5\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append("La requisición fue devuelta al solicitante para hacer los cambios necesarios, iniciando de nuevo el proceso de autorización. ");
                tabla.append(" El solicitante encontrará la requisición con el número: [<b>");
                tabla.append(validarNullHtml(requisicion.getId().toString()));
                tabla.append("</b>] en la opción de solicitar requisición.");
                tabla.append("</center></font>");
                tabla.append("</td>");
                tabla.append("</tr>");
            }

            if ("finalizar".equals(tipo)) {
                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append("Finalizo");
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(requisicion.getFinalizo().getNombre());
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(validarNullFechaHtml(requisicion.getFechaFinalizo()));
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append(validarNullHoraHtml(requisicion.getHoraFinalizo()));
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append(validarNullHoraHtml(requisicion.getMotivoFinalizo()));
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("</tr>");
                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\" colspan=\"5\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append("A continuación se muestra la requisición con los ítems que no fueron procesados en ninguna orden de compra o servicio.");
                tabla.append("</center></font>");
                tabla.append("</td>");
                tabla.append("</tr>");
            }
            
            if ("espera".equals(tipo)) {
                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append("En Espera");
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(requisicion.getFinalizo().getNombre());
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append(validarNullFechaHtml(requisicion.getFechaFinalizo()));
                tabla.append("</center></font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append(validarNullHoraHtml(requisicion.getHoraFinalizo()));
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\">");
                tabla.append("<font size=\"-1\" face=\"arial\">");
                tabla.append("<center>");
                tabla.append(validarNullHoraHtml(requisicion.getMotivoFinalizo()));
                tabla.append("</center>");
                tabla.append("</font></td>");
                tabla.append("</tr>");
                tabla.append("<tr>");
                tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\" colspan=\"5\">");
                tabla.append("<font size=\"-1\" face=\"arial\"><center>");
                tabla.append("A continuación se muestra la requisición con los ítems que no fueron procesados en ninguna orden de compra o servicio.");
                tabla.append("</center></font>");
                tabla.append("</td>");
                tabla.append("</tr>");
            }

        }
        tabla.append("</table><br><center></center></td></tr>");

        return tabla.toString();
    }

    private String encabezadoRechazo(int idRequisicion, String titulo, Rechazo rechazo) {
        StringBuilder tabla = new StringBuilder();
        UtilLog4j.log.info(this, "Esta recuperando el segundo encabezado para requision devuelta . . . " + idRequisicion);
        UtilLog4j.log.info(this, "Rechazo . . . " + rechazo.getRechazo().getNombre());
        UtilLog4j.log.info(this, "fecha . . . " + rechazo.getFecha());
        UtilLog4j.log.info(this, "Hora . . . " + rechazo.getHora());
        UtilLog4j.log.info(this, "Mot . . . " + rechazo.getMotivo());
        tabla.append("<tr>");
        tabla.append("<td colspan=\"3\">");
        tabla.append("<table width=\"95%\" align=\"center\" style=\"border:1px solid #A8CEF0\">");
        tabla.append("<tr>");
        tabla.append("<td bgcolor=\"#A8CEF0\" colspan=\"5\" style=\"border:1px solid #A8CEF0; text-align:left\">");
        tabla.append("<font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;\"><center><b>").append(titulo).append("</b></font></center></td>");
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
        tabla.append("<tr>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
        tabla.append("<font size=\"-1\" face=\"arial\">");
        tabla.append("<center>");
        tabla.append("Devolvió");
        tabla.append("</center>");
        tabla.append("</font></td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
        tabla.append("<font size=\"-1\" face=\"arial\"><center>");
        tabla.append(rechazo.getRechazo().getNombre());
        tabla.append("</center></font></td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
        tabla.append("<font size=\"-1\" face=\"arial\"><center>");
        tabla.append(validarNullFechaHtml(rechazo.getFecha()));
        tabla.append("</center></font></td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:15%\">");
        tabla.append("<font size=\"-1\" face=\"arial\">");
        tabla.append("<center>");
        tabla.append(validarNullHoraHtml(rechazo.getHora()));
        tabla.append("</center>");
        tabla.append("</font></td>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\">");
        tabla.append("<font size=\"-1\" face=\"arial\">");
        tabla.append("<center>");
        tabla.append(validarNullHoraHtml(rechazo.getMotivo()));
        tabla.append("</center>");
        tabla.append("</font></td>");
        tabla.append("</tr>");
        tabla.append("<tr>");
        tabla.append("<td valign=\"middle\" bgcolor=\"#FFFFFF\" style=\"border:1px solid #A8CEF0; width:40%\" colspan=\"5\">");
        tabla.append("<font size=\"-1\" face=\"arial\"><center>");
        tabla.append("La requisición será devuelta al solicitante para hacer los cambios necesarios, iniciando de nuevo el proceso de autorización. ");
        tabla.append(" El solicitante encontrara la requisición con el número: [<b>");
        tabla.append(idRequisicion);
        tabla.append("</b>] en la opción de solicitar requisición.");
        tabla.append("</center></font>");
        tabla.append("</td>");
        tabla.append("</tr>");
        tabla.append("</table><br><center></center></td></tr>");
        return tabla.toString();

    }

    private String getLigaSIA() {
        StringBuilder liga = new StringBuilder();//
        liga.append("<tr>").append("<td colspan=\"3\">").append("<table width=\"95%\" align=\"center\">").append("<tr>").append("<td style=\"text-align:center\"><a target=\"_blank\" href=\"").append(Configurador.urlSia()).append("Sia\">").append("Clic aquí para ir al SIA</a><br>").append("<br>").append("<br>").append("</td>").append("</tr>").append("</table>").append("</td>").append("</tr>");

        return liga.toString();
    }

    
    public StringBuilder mensajeReporteRequisicion(List<RequisicionReporteVO> lReporte, String asunto, int dias,
            List<RequisicionReporteVO> listaTotalComprador) {
        StringBuilder mensaje = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        mensaje.append(plantilla.getInicio());
        mensaje.append(this.getTitulo(asunto));
        //Aquí va todo el contenido del cuerpo,

        mensaje.append("<br/>");
        mensaje.append("<br/>");
        mensaje.append(" <p> Reporte del total de requisiciones en la bandeja de analistas de compras: </p>");

        for (RequisicionReporteVO requisicionReporteVO : listaTotalComprador) {
            mensaje.append("<b>Comprador: ");
            mensaje.append(requisicionReporteVO.getComprador()).append("</b>");
            mensaje.append("    <b>Total: ( ");
            mensaje.append(requisicionReporteVO.getTotalRequisiciones()).append(" ) </b>");
            mensaje.append("<br/>");
            mensaje.append("<table  cellspacing=\"0\" cellpadding=\"0\">");
            mensaje.append("<tr>");
            mensaje.append("<td>");
            //
            mensaje.append("<table  cellspacing=\"0\" cellpadding=\"0\">");
            mensaje.append("<tr>");
            mensaje.append("<td  align=\"center\" colspan = \" ").append(requisicionReporteVO.getLRequisicion().size()).append(" \"   style=\" ").append(getEstiloTitulo()).append("\">").append(siManejoFechaLocal.traerAnioActual()).append(" </td>");
            mensaje.append("</tr>");
            mensaje.append("<tr>");
            for (RequisicionVO requisicionVO : requisicionReporteVO.getLRequisicion()) {
                mensaje.append("<td>");
                mensaje.append("<table cellspacing=\"0\" cellpadding=\"0\" >");
                mensaje.append("<tr>");
                mensaje.append("<td  align=\"center\"  style=\" ").append(getEstiloContenido()).append("\">").append(requisicionVO.getCadena()).append(" </td>");
                mensaje.append("</tr>");
                mensaje.append("<tr>");
                mensaje.append("<td  align=\"center\"  style=\" ").append(getEstiloContenido()).append("\">").append(requisicionVO.getTotalItems()).append(" </td>");
                mensaje.append("</tr>");
                mensaje.append("</table>");
                mensaje.append("</td>");
            }
            mensaje.append("</tr>");
            mensaje.append("</table>");
            mensaje.append("</td>");
            //
            mensaje.append("<td>");
            mensaje.append("<table cellspacing=\"0\" cellpadding=\"0\">");
            mensaje.append("<tr>");
            mensaje.append("<td  align=\"center\"  style=\" ").append(getEstiloTitulo()).append("\">").append(requisicionReporteVO.getCadena()).append(" </td>");
            mensaje.append("</tr>");
            mensaje.append("<tr>");
            mensaje.append("<td  align=\"center\"  style=\" ").append(getEstiloContenido()).append("\">").append("Total").append(" </td>");
            mensaje.append("</tr>");
            mensaje.append("<tr>");
            mensaje.append("<td  align=\"center\"  style=\" ").append(getEstiloContenido()).append("\">").append(requisicionReporteVO.getTotalAnioAnteriores()).append(" </td>");
            mensaje.append("</tr>");
            mensaje.append("</table>");
            mensaje.append("</td>");
            mensaje.append("</tr>");
            mensaje.append("</table>");
        }
        mensaje.append("<br/>");
        mensaje.append("<br/>");
        mensaje.append(" <p> Detalle del reporte de requisiciones en la bandeja de analistas de compras con ").append(dias).append(" días o más de asignación: </p>");
        for (RequisicionReporteVO requisicionReporteVO : lReporte) {
            mensaje.append("<b>Comprador: ");
            mensaje.append(requisicionReporteVO.getComprador()).append("</b>");
            mensaje.append("<br/>");
            mensaje.append("<table width=\"100%\" cellspacing=\"0\">");
            mensaje.append("<tr>");
            mensaje.append("<td style=\"").append(getEstiloTitulo()).append("\">").append("Consecutivo</td>");
            mensaje.append("<td style=\"").append(getEstiloTitulo()).append("\">").append("Asignada</td>");
            mensaje.append("<td style=\"").append(getEstiloTitulo()).append("\">").append("Referencia</td>");
            mensaje.append("<td style=\"").append(getEstiloTitulo()).append("\">").append("Gerencia</td>");
            mensaje.append("<td style=\"").append(getEstiloTitulo()).append("\">").append("Proyecto OT</td>");
            mensaje.append("<td style=\"").append(getEstiloTitulo()).append("\">").append("Total items</td>");
            mensaje.append("<td style=\"").append(getEstiloTitulo()).append("\">").append("Items procesados</td>");
            mensaje.append("<td style=\"").append(getEstiloTitulo()).append("\">").append("Items Pendientes</td>");
            mensaje.append("</tr>");
            for (RequisicionVO requisicionVO : requisicionReporteVO.getLRequisicion()) {
                mensaje.append("<tr>");
                mensaje.append("<td  style=\" ").append(getEstiloContenido()).append("\">").append(requisicionVO.getConsecutivo()).append(" </td>");
                mensaje.append("<td  style=\" ").append(getEstiloContenido()).append("\">").append(Constantes.FMT_ddMMyyy.format(requisicionVO.getFechaAsignada())).append("</td>");
                mensaje.append("<td  style=\" ").append(getEstiloContenido()).append("\">").append(requisicionVO.getReferencia()).append(" </td>");
                mensaje.append("<td  style=\" ").append(getEstiloContenido()).append("\">").append(requisicionVO.getGerencia()).append(" </td>");
                mensaje.append("<td  style=\" ").append(getEstiloContenido()).append("\">").append(requisicionVO.getProyectoOT()).append(" </td>");
                mensaje.append("<td  style=\" ").append(getEstiloContenido()).append("\">").append(requisicionVO.getTotalItems()).append("</td>");
                mensaje.append("<td  style=\" ").append(getEstiloContenido()).append("\">").append(requisicionVO.getTotalItemEnOrden()).append("</td>");
                mensaje.append("<td  style=\" ").append(getEstiloContenido()).append("\">").append(requisicionVO.getTotalItemSinOrden()).append("</td>");
                mensaje.append("</tr>");
            }
            mensaje.append("</table>");
        }
        mensaje.append("<br/>");

        mensaje.append(plantilla.getFin());
        return mensaje;
    }

    /**
     * MLUIS
     *
     * @param requisicion
     * @param tipos
     * @return
     */
    
    public StringBuilder mensajeReasignarRequisicion(Requisicion requisicion, String asunto) {
        StringBuilder solicitud = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(this.Plantilla_Requisicion);
        this.limpiarCuerpoCorreo();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append(asunto);
        String plantillaInicio = plantilla.getInicio();
        plantillaInicio = replaceAll(plantillaInicio, "@@1@@", encabezado.toString());
        plantillaInicio = replaceAll(plantillaInicio, "@@2@@", requisicion.getCompania().getNombre());
        solicitud.append(plantillaInicio);
        //Aquí va todo el contenido del cuerpo,

        //cuerpo de la requisicion
        solicitud.append("<tr><td colspan=\"3\">");
        solicitud.append(getCuerpoRequisicion(requisicion));
        solicitud.append(getResponsables(requisicion));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }

    
    public StringBuilder mensajeCambioRequisiciones(List<RequisicionVO> lo, String nombreActual,
            String nombreAprobara, String asunto, String status) {
        StringBuilder solicitud = new StringBuilder();
        this.limpiarCuerpoCorreo();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        solicitud.append(plantilla.getInicio());
        solicitud.append(this.getTitulo(asunto));
        //Aquí va todo el contenido del cuerpo,
        solicitud.append(mensajeCuerpoCambioRequisiciones(nombreActual, nombreAprobara, status));
        solicitud.append(listaRequisicion(lo));
        solicitud.append(plantilla.getFin());
        return solicitud;
    }

    private String mensajeCuerpoCambioRequisiciones(String nombreTiene, String nombreAprobara, String status) {
        StringBuilder sb = new StringBuilder();
        sb.append("<br/>");
        sb.append("<p> La lista de requisiciones pendientes de <b>").append(status).append("</b>, por <b> ").append(nombreTiene).append("</b>");
        sb.append(", se pasaron a bandeja de <b>").append(nombreAprobara).append("</b>, para continuar con el proceso de aprobación.");
        sb.append("</p>");
        return sb.toString();
    }

    private String listaRequisicion(List<RequisicionVO> lo) {
        StringBuilder sb = new StringBuilder();
        sb.append("<br/><table width=\"100%\"  cellspacing=\"0\">");
        sb.append("<tr><th colspan=\"2\" align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Requisiciones</th></tr>");
        sb.append("<tr>");
        sb.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Consecutivo</th>");
        sb.append("<th align=\"center\" style=\"").append(getEstiloTitulo()).append("\">Referencia</th>");
        sb.append("</tr>");
        for (RequisicionVO requisicion : lo) {
            sb.append("<tr>");
            sb.append("<td ").append("style=\" text-align:center;").append(getEstiloContenido()).append("\">").append(requisicion.getConsecutivo()).append("</td>");
            sb.append("<td ").append("style=\"").append(getEstiloContenido()).append("\">").append(requisicion.getReferencia()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table><br/>");
        return sb.toString();
    }
}
