package sia.notificaciones.oficio.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.correo.impl.CodigoHtml;
import sia.modelo.SiPlantillaHtml;
import sia.modelo.oficio.vo.AdjuntoOficioVo;
import sia.modelo.oficio.vo.OficioEntradaVo;
import sia.modelo.oficio.vo.OficioPromovibleVo;
import sia.modelo.oficio.vo.OficioSalidaVo;
import sia.modelo.oficio.vo.OficioVo;
import sia.servicios.sistema.impl.SiPlantillaHtmlImpl;
import sia.util.UtilSia;

/**
 *
 * @author esapien
 */
@LocalBean 
public class HtmlNotificacionOficioImpl extends CodigoHtml {

    @Inject
    private SiPlantillaHtmlImpl plantillaHtml;

    /**
     *
     * Regresa el estilo necesario para la tabla de nofificacion de oficios.
     * jevazquez 16/04/15 aprobado
     *
     * @return
     */
    private String obtenerEstilo() {

	StringBuilder sb = new StringBuilder();

	sb.append("<style>");

	sb.append("div");
	sb.append("{");
	sb.append(" font-family:\"Trebuchet MS\", Arial, Helvetica, sans-serif;");
	sb.append(" font-size:10pt;");
	sb.append(" width: 1100px;");
	sb.append(" color:#004181;");
	sb.append("}");

	sb.append("table.oficios");
	sb.append("{");
	sb.append(" font-family:\"Trebuchet MS\", Arial, Helvetica, sans-serif;");
	sb.append(" font-size:10pt;");
	sb.append(" width: 1000px;");
	sb.append("}");

	sb.append("table.oficios td, table.oficios th");
	sb.append("{");
	sb.append(" border:1px solid #A8CEF0;");
	sb.append(" padding:3px 7px 2px 7px;");
	sb.append(" color:#004181;");
	sb.append("}");

	sb.append("table.oficios th, table.oficios tr.header ");
	sb.append("{");
	sb.append(" text-align:center;");
	sb.append(" padding-top:5px;");
	sb.append(" padding-bottom:4px;");
	sb.append(" background-color:#A8CEF0;");
	sb.append(" color:#004181;");
	sb.append("}");

	sb.append("table.oficios tr.alt td ");
	sb.append("{");
	sb.append(" background-color:#E4EAEB;");
	sb.append("}");

	sb.append("table.oficios td.numeroOficio");
	sb.append("{");
	sb.append(" width: 25%;");
	sb.append(" white-space:nowrap;");
	sb.append("}");

	sb.append("table.oficios td.fechaOficio");
	sb.append("{");
	sb.append(" width: 9%;");
	sb.append(" white-space:nowrap;");
	sb.append(" text-align:center;");
	sb.append("}");

	sb.append("table.oficios td.asunto");
	sb.append("{");
	sb.append(" width: 54%;");
	//sb.append(" white-space:nowrap;");
	sb.append("}");

	sb.append("table.oficios td.adjunto");
	sb.append("{");
	sb.append(" width: 12%;");
	sb.append(" text-align:center;");
	//sb.append(" white-space:nowrap;");
	sb.append("}");

	sb.append("</style>");

	return sb.toString();

    }

    /**
     *
     * Genera la tabla HTML con la informacion de los oficios para el correo de
     * notificacion.
     *
     * @param oficios
     * @param titulo
     * @return
     */
    private String obtenerTablaHtml(List<OficioPromovibleVo> oficios, String titulo) {

	StringBuilder sb = new StringBuilder();

	sb.append("<table class=\"oficios\" align=\"center\" width=\"700px\">");
	sb.append("   <tr>");
	sb.append("      <th colspan=\"4\">");
	sb.append(titulo);
	sb.append("      </th>");
	sb.append("   </tr>");
	sb.append("   <tr class=\"header\">");
	sb.append("      <td class=\"numeroOficio\">");
	sb.append("         No. DE CONSECUTIVO");
	sb.append("      </td>");
	sb.append("      <td class=\"fechaOficio\">");
	sb.append("         FECHA");
	sb.append("      </td>");
	sb.append("      <td class=\"asunto\">");
	sb.append("         ASUNTO");
	sb.append("      </td>");
	sb.append("      <td class=\"adjunto\">");
	sb.append("         ARCHIVO");
	sb.append("      </td>");
	sb.append("   </tr>");

	boolean alt = false;

	for (OficioPromovibleVo vo : oficios) {

	    String numeroOficio = vo.getOficioNumero();
	    String fechaFormato = vo.getOficioFechaFormato();
	    String asunto = vo.getOficioAsunto();

	    sb.append((alt = !alt) ? "   <tr>" : "   <tr class=\"alt\">");
	    sb.append("      <td>");
	    sb.append(numeroOficio);
	    sb.append("      </td>");
	    sb.append("      <td>");
	    sb.append("         <center>").append(fechaFormato).append("</center>");
	    sb.append("      </td>");
	    sb.append("      <td>");
	    sb.append(asunto);
	    sb.append("      </td>");

            // archivos adjuntos
	    sb.append("      <td>");

	    AdjuntoOficioVo adjunto = vo.obtenerArchivoInformeAvance();

	    sb.append("<center>");
	    sb.append("<a target=\"_blank\" ");
	    sb.append(" title=\"");
	    sb.append(adjunto.getNombre());
	    sb.append("\" ");
            // #{movimiento.adjunto.uuid}
	    //sb.append(" href=\"http://localhost:8080/ControlOficios/AbrirArchivo?");
	    sb.append(" href=\"").append(Configurador.urlSia()).append("ControlOficios/DACOF?");//esta parte se tiene que modificar sea gregara un servlet
	    sb.append("Z4BX2=");
            sb.append(vo.getOficioId());
	    sb.append("&ZWZ2W=");
	    sb.append(adjunto.getId());
	    sb.append("&ZWZ3W=");
	    sb.append(adjunto.getUuid());
            sb.append("&4PC4WZ=");
            sb.append(vo.getBloqueId());
	    sb.append("\">");
	    sb.append("Abrir");
	    sb.append("</a>");
	    sb.append("</center>");
	    sb.append("&nbsp");

	    sb.append("      </td>");
	    sb.append("   </tr>");
	}

	if (oficios.isEmpty()) {

	    sb.append("   <tr>");
	    sb.append("      <td colspan=\"4\">");
	    sb.append("         No existen registros.");
	    sb.append("      </td>");
	    sb.append("   </tr>");

	}

	sb.append("</table>");

	return sb.toString();

    }

    /**
     *
     * @param oficios
     * @return
     */
    
    public String contenidoNotificacionAltaOficios(List<OficioPromovibleVo> oficios, String asunto) {

	// separar por tipo
	List<OficioPromovibleVo> oficiosSalida = new ArrayList();
	List<OficioPromovibleVo> oficiosEntrada = new ArrayList();
        String salida = "CORRESPONDENCIA ENTREGADA A PEMEX (IHSA)";
        String entrada = "CORRESPONDENCIA RECIBIDA IHSA (PEMEX)";
        String asuntoCq = Constantes.OFICIOS_CORREO_ASUNTO_IHSA_CQ_PEMEX + Constantes.FMT_ddMMyyy.format(new Date());
        if(asunto.equals(asuntoCq)){
            salida = "CORRESPONDENCIA ENVIADA (IHSA CQ)";
            entrada = "CORRESPONDENCIA RECIBIDA (IHSA CQ)";
        }

	for (OficioPromovibleVo vo : oficios) {
	    if (vo instanceof OficioSalidaVo) {
		oficiosSalida.add(vo);
	    } else if (vo instanceof OficioEntradaVo) {
		oficiosEntrada.add(vo);
	    }
	}

	// construir tablas html
	String tablaHtmlSalida
		= obtenerTablaHtml(
			oficiosSalida, salida);
	String tablaHtmlEntrada
		= obtenerTablaHtml(
			oficiosEntrada, entrada);

	SiPlantillaHtml plantilla = plantillaHtml.find(Constantes.PLANTILLA_HTML_FORMATO_CONTROL_DE_OFICIOS);

	StringBuilder contenido = new StringBuilder();

	contenido.append(obtenerEstilo());

	String plantillaInicio = plantilla.getInicio();

        //plantillaInicio = plantillaInicio.replace("border=\"0\"", "border=\"1\"");
        //System.out.println("inicio plantilla = " + plantillaInicio);
	contenido.append(plantillaInicio);

	String titulo = getTituloOficios(asunto);

	contenido.append(titulo);

	contenido.append(tablaHtmlSalida);

	contenido.append("<br><br>");

	contenido.append(tablaHtmlEntrada);

	contenido.append(plantilla.getFin());

	return contenido.toString();
    }

    /**
     *
     * @param oficios
     * @return
     */
    
    public String contenidoNotificacionPromocionOficio(OficioPromovibleVo vo) {

	SiPlantillaHtml plantilla = plantillaHtml.find(Constantes.PLANTILLA_HTML_FORMATO_CONTROL_DE_OFICIOS);

	StringBuilder contenido = new StringBuilder();

	contenido.append(obtenerEstilo());

	contenido.append(plantilla.getInicio());

	contenido.append(getTituloOficios(Constantes.OFICIOS_CORREO_ASUNTO_PROMOCION + UtilSia.getFechaActual_ddMMyyy()));

	contenido.append("<br/>");
	contenido.append("<div>");
	contenido.append("El siguiente oficio ha sido promovido a ");
	contenido.append(vo.getEstatusNombre());
	contenido.append(". Favor de ingresar al sistema para dar el seguimiento correspondiente.");
	contenido.append("</div>");
	contenido.append("<br/>");

	contenido.append("<table class=\"oficios\" align=\"center\">");
	contenido.append("   <tr class=\"header\">");
	contenido.append("      <td class=\"numeroOficio\">");
	contenido.append("         No. DE CONSECUTIVO");
	contenido.append("      </td>");
	contenido.append("      <td class=\"fechaOficio\">");
	contenido.append("         FECHA");
	contenido.append("      </td>");
	contenido.append("      <td class=\"asunto\">");
	contenido.append("         ASUNTO");
	contenido.append("      </td>");
	contenido.append("   </tr>");
	contenido.append("   <tr>");
	contenido.append("      <td>").append(vo.getOficioNumero()).append("</td>");
	contenido
		.append("      <td>")
		.append("<center>").append(vo.getOficioFechaFormato()).append("</center>")
		.append("</td>");
	contenido.append("      <td>").append(vo.getOficioAsunto()).append("</td>");
	contenido.append("   </tr>");
	contenido.append("</table>");

	contenido.append("<br/>");

	contenido.append(plantilla.getFin());

	return contenido.toString();
    }

    
    public String contenidoNotificacionModificacionOficio(OficioVo vo) {
	SiPlantillaHtml plantilla = plantillaHtml.find(Constantes.PLANTILLA_HTML_FORMATO_CONTROL_DE_OFICIOS);

	StringBuilder contenido = new StringBuilder();

	contenido.append(obtenerEstilo());

	contenido.append(plantilla.getInicio());

	contenido.append(getTituloOficios(Constantes.OFICIOS_CORREO_MODIFICA_OFICIO + UtilSia.getFechaActual_ddMMyyy()));

	contenido.append("<br/>");
	contenido.append("<div>");
	contenido.append("El siguiente oficio ha sido modificado  ");
	contenido.append(". Favor de ingresar al sistema para más detalles.");
	contenido.append("</div>");
	contenido.append("<br/>");

	contenido.append("<table class=\"oficios\" align=\"center\">");
	contenido.append("   <tr class=\"header\">");
	contenido.append("      <td class=\"numeroOficio\">");
	contenido.append("         No. DE CONSECUTIVO");
	contenido.append("      </td>");
	contenido.append("      <td class=\"fechaOficio\">");
	contenido.append("         FECHA");
	contenido.append("      </td>");
	contenido.append("      <td class=\"asunto\">");
	contenido.append("         ASUNTO");
	contenido.append("      </td>");
	contenido.append("   </tr>");
	contenido.append("   <tr>");
	contenido.append("      <td>").append(vo.getOficioNumero()).append("</td>");
	contenido
		.append("      <td>")
		.append("<center>").append(vo.getOficioFechaFormato()).append("</center>")
		.append("</td>");
	contenido.append("      <td>").append(vo.getOficioAsunto()).append("</td>");
	contenido.append("   </tr>");
	contenido.append("</table>");

	contenido.append("<br/>");

	contenido.append(plantilla.getFin());

	return contenido.toString();
    }

    /**
     *
     * @param oficios
     * @return
     */
    //jevazquez 23/feb/2015 aprobado
    
    public String contenidoNotificacionNoModificadoSemana(List<OficioPromovibleVo> oficios) {

	// separar por tipo
	List<OficioPromovibleVo> oficiosSalida = new ArrayList();
	List<OficioPromovibleVo> oficiosEntrada = new ArrayList();

	for (OficioPromovibleVo vo : oficios) {
	    if (vo instanceof OficioSalidaVo) {
		oficiosSalida.add(vo);
	    } else if (vo instanceof OficioEntradaVo) {
		oficiosEntrada.add(vo);
	    }
	}

	// construir tablas html
	String tablaHtmlSalida
		= obtenerTablaHtmlSemanal(
			oficiosSalida, "CORRESPONDENCIA IHSA - PEMEX (IHSA) PENDIENTE DE RESPUESTA");
	String tablaHtmlEntrada
		= obtenerTablaHtmlSemanal(
			oficiosEntrada, "CORRESPONDENCIA PEMEX - IHSA (PEMEX) PENDIENTE DE RESPUESTA");

	SiPlantillaHtml plantilla = plantillaHtml.find(Constantes.PLANTILLA_HTML_FORMATO_CONTROL_DE_OFICIOS);

	StringBuilder contenido = new StringBuilder();

	contenido.append(obtenerEstilo());

	String plantillaInicio = plantilla.getInicio();

        //plantillaInicio = plantillaInicio.replace("border=\"0\"", "border=\"1\"");
        //System.out.println("inicio plantilla = " + plantillaInicio);
	contenido.append(plantillaInicio);

	String titulo = getTituloOficios(Constantes.OFICIOS_CORREO_ASUNTO_IHSA_PEMEX_NO_PROMOVIDOS
		+ Constantes.FMT_ddMMyyy.format(new Date()));

	contenido.append(titulo);

	contenido.append(tablaHtmlSalida);

	contenido.append("<br><br>");

	contenido.append(tablaHtmlEntrada);

	contenido.append(plantilla.getFin());

	return contenido.toString();
    }

    //jevazquez 23/feb/2015 aprobado
    private String obtenerTablaHtmlSemanal(List<OficioPromovibleVo> oficios, String titulo) {

	StringBuilder sb = new StringBuilder();

	sb.append("<table class=\"oficios\" align=\"center\" width=\"1000px\">");
	sb.append("   <tr>");
	sb.append("      <th colspan=\"4\" >");
	sb.append(titulo);
	sb.append("      </th>");
	sb.append("   </tr>");
	sb.append("   <tr class=\"header\">");
	sb.append("      <td class=\"numeroOficio\">");
	sb.append("         No. DE CONSECUTIVO");
	sb.append("      </td>");
	sb.append("      <td class=\"fechaOficio\">");
	sb.append("         FECHA");
	sb.append("      </td>");
	sb.append("      <td class=\"asunto\">");
	sb.append("         ASUNTO");
	sb.append("      </td>");
	sb.append("<td class=\"estatus\">");
	sb.append("        ESTATUS");
	sb.append("</td>");
	sb.append("   </tr>");

	boolean alt = false;

	for (OficioPromovibleVo vo : oficios) {

	    String numeroOficio = vo.getOficioNumero();
	    String fechaFormato = vo.getOficioFechaFormato();
	    String asunto = vo.getOficioAsunto();
	    String estatus = vo.getEstatusNombre();

	    sb.append((alt = !alt) ? "   <tr>" : "   <tr class=\"alt\">");
	    sb.append("      <td>");
	    sb.append(numeroOficio);
	    sb.append("      </td>");
	    sb.append("      <td>");
	    sb.append("         <center>").append(fechaFormato).append("</center>");
	    sb.append("      </td>");
	    sb.append("      <td>");
	    sb.append(asunto);
	    sb.append("      </td>");
	    sb.append("      <td>");
	    sb.append(estatus);
	    sb.append("      </td>");
	    sb.append("   </tr>");
	}

	if (oficios.isEmpty()) {

	    sb.append("   <tr>");
	    sb.append("      <td colspan=\"4\">");
	    sb.append("         No existen registros.");
	    sb.append("      </td>");
	    sb.append("   </tr>");

	}

	sb.append("</table>");

	return sb.toString();

    }

}
