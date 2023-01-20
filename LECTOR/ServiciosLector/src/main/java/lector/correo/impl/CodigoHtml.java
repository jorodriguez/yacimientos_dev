/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.correo.impl;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import lector.constantes.Constantes;

/**
 *
 */
public class CodigoHtml implements Serializable {

    protected StringBuilder cuerpoCorreo = new StringBuilder("");
    protected StringBuilder f1 = new StringBuilder("");
    protected StringBuilder f = new StringBuilder("");
    protected StringBuilder s = new StringBuilder("");
    protected Integer a;
    protected DecimalFormat formatoMoneda = new DecimalFormat("$###,###,###.##");
    protected SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    protected SimpleDateFormat formatoHora = new SimpleDateFormat("hh:mm a");
    protected SimpleDateFormat formatoFechaLargo = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));
    
    //FIXME : las constantes deben estar en mayúsculas
    protected static final int Plantilla_LogoSIA = 1;
    protected static final int Plantilla_Requisicion = 2;
    protected static final int Plantilla_OrdenCompra = 3;

    private StringBuilder encabezadoHtml, inicioHtml, finHtml = new StringBuilder("");
    private String autor = "Sistema Integral de Administración";
    private String descripcion = "Notificaciones, Sistema Integral de Administración";
    private boolean conEstilo = false;
    private String estiloCentrado;
    private String botonConfirmar = "margin:10px;background-color:#0895d6;border:1px solid #999;color:#fff;cursor:pointer;font-size:15px;font-weight:bold;text-decoration: none;";
    private String botonCancelar = "margin:10px;background-color:#d8e1e6; border:1px solid #999;color:#fff;cursor:pointer;font-size:15px;font-weight:bold;text-decoration: none;";
    private String estiloTitulo = "border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;text-align: center;";
    private String estiloContenido = "border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px;";
    private String estiloTituloTabla = "align=\"center\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"";

    /**
     *
     * \n Nueva línea \t Tabulador \' Comilla simple \" Comilla doble \\ Barra
     * invertida
     *
     */
    /**
     * @return the encabezadoHtml
     */
    public StringBuilder getEncabezadoHtml() {
	this.encabezadoHtml.append("<!DOCTYPE html>");
	this.encabezadoHtml.append("<html lang=\"es\">");
	this.encabezadoHtml.append("<head>");
	this.encabezadoHtml.append("<meta charset=\"utf-8\">");
	this.encabezadoHtml.append("<meta name=\"author\" content=\" ");
	this.encabezadoHtml.append(this.autor);
	this.encabezadoHtml.append("\"/>");
	this.encabezadoHtml.append("<meta name=\"description\" content=\" ");
	this.encabezadoHtml.append(this.descripcion);
	this.encabezadoHtml.append("\"/>");
	if (conEstilo) {
	    this.encabezadoHtml.append("<style type=\"text/css\">");
	}

	return encabezadoHtml;
    }

    protected String encabezado = "#Encabezado{"
            + "font-size: 1.2em;"
            + "font-weight: bold;"
            + "color: white;"
            + "font-family: Arial, Helvetica, sans-serif; "
            + "border:  thin solid  #000000;"
            + "padding-left: 40px;"
            + "background: #0895d6  no-repeat  fixed;"
            + "text-align: center;"
            + "}";
    protected String tabla = "#tr{"
            + "background-color:transparent;"
            + "border-style:solid;"
            + "border-color:666666;"
            + "font-family: Arial, Helvetica, sans-serif; "
            + "border-width:1px;"
            + "border:thin solid #000000;"
            + "border-collapse: separate; "
            + "table-layout: fixed;"
            + "font-size: 11px;"
            + "}";
    protected String t = "tr";
    protected String e = "Encabezado";
    protected String style = " <style type=\"text/css\">\n";
    protected String htmlEncabezado = "<html> \n "
            + "<head>\n "
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"> \n" + this.style
            + this.encabezado + "\n" + this.tabla + "\n" + "\n </style>\n</head>  \n<body>";
    protected String htmlPie = " </body></html>";

    /**
     * @return the inicioHtml
     *
     */
    public StringBuilder getInicioHtml() {
	if (conEstilo) {
	    this.inicioHtml.append("</style>");
	}
	this.inicioHtml.append("</head>");
	this.inicioHtml.append("<body>");
	return this.inicioHtml;
    }

    /**
     * @return the finHtml
     */
    public StringBuilder getFinHtml() {
	this.finHtml.append("</body>");
	this.finHtml.append("</html>");
	return finHtml;
    }

    protected String getTitulo(String titulo) {
	StringBuilder html = new StringBuilder();
	html.delete(0, this.cuerpoCorreo.length());
//        html.append("<article>");
	html.append("<header>");
	html.append("<h3 style= \"text-decoration:none; border-bottom: 1px dotted #b5b5b5; color: #004181;padding:0px 12px 0px 12px;\"> ");
	html.append(titulo);
	html.append("</h3>");
	html.append("</header>");
	html.append("</td>");
	html.append("</tr>");
	html.append("<tr>");
	html.append("<td colspan= \"2\">");

	return html.toString();
    }
    
    protected String getMsgH3(String msg) {
	StringBuilder html = new StringBuilder();
	html.delete(0, this.cuerpoCorreo.length());

	html.append("<h3 style= \"text-decoration:none; border-bottom: 1px dotted #b5b5b5; color: #004181;padding:0px 12px 0px 12px;\"> ");
	html.append(msg);
	html.append("</h3>");
	
	html.append("</td>");
	html.append("</tr>");

	return html.toString();
    }

    protected String getTituloRojo(String titulo) {
	StringBuilder html = new StringBuilder();
	html.delete(0, this.cuerpoCorreo.length());
//        html.append("<article>");
	html.append("<header>");
	html.append("<h3 style= \"text-decoration:none; border-bottom: 1px dotted #b5b5b5; color:Red;padding:0px 12px 0px 12px;\"> ");
	html.append(titulo);
	html.append("</h3>");
	html.append("</header>");
	html.append("</td>");
	html.append("</tr>");
	html.append("<tr>");
	html.append("<td colspan= \"2\">");

	return html.toString();
    }

    public void limpiarCuerpoCorreo() {
	this.cuerpoCorreo.delete(0, this.cuerpoCorreo.length());
	this.f.delete(0, this.f.length());
	this.f1.delete(0, this.f1.length());
	this.s.delete(0, this.s.length());
    }

    protected Integer getModulo(Integer Valor) {
	return Valor % 2;
    }

    /**
     * @param autor the autor to set
     */
    public void setAutor(String autor) {
	this.autor = autor;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
	this.descripcion = descripcion;
    }

    /**
     * @param conEstilo the conEstilo to set
     */
    public void setConEstilo(boolean conEstilo) {
	this.conEstilo = conEstilo;
    }

    /**
     * @return the estiloContenido
     */
    public String getEstiloContenido() {
	return estiloContenido;
    }

    /**
     * @param estiloContenido the estiloContenido to set
     */
    public void setEstiloContenido(String estiloContenido) {
	this.estiloContenido = estiloContenido;
    }

    /**
     * @return the estiloTitulo
     */
    public String getEstiloTitulo() {
	return estiloTitulo;
    }

    /**
     * @param estiloTitulo the estiloTitulo to set
     */
    public void setEstiloTitulo(String estiloTitulo) {
	this.estiloTitulo = estiloTitulo;
    }

    /**
     * @return the botonConfirmar
     */
    public String getBotonConfirmar() {
	return botonConfirmar;
    }

    /**
     * @param botonConfirmar the botonConfirmar to set
     */
    public void setBotonConfirmar(String botonConfirmar) {
	this.botonConfirmar = botonConfirmar;
    }

    public String validarNullHtml(String objeto) {
	if (objeto != null && !objeto.isEmpty() && !"null".equalsIgnoreCase(objeto)) {
	    return objeto;
	} else {
	    return "&nbsp;";
	}
    }

    public String validarNullFechaHtml(Object objeto) {
	if (objeto != null) {
	    if (objeto instanceof java.util.Date) {
		return formatoFecha.format(objeto);
	    } else {
		return String.valueOf(objeto);
	    }

	} else {
	    return "&nbsp;";
	}
    }

    public String validarNullMontoHtml(Object objeto) {
	if (objeto != null) {
	    if (objeto instanceof Double) {
		return formatoMoneda.format(objeto);
	    } else {
		return String.valueOf(objeto);
	    }
	} else {
	    return "&nbsp;";
	}
    }

    public String validarNullHoraHtml(Object objeto) {
	if (objeto != null) {
	    if (objeto instanceof java.util.Date) {
		return formatoHora.format(objeto);
	    } else {
		return String.valueOf(objeto);
	    }

	} else {
	    return "&nbsp;";
	}
    }

    public String getInicioPlantilaAsignacion() {
	StringBuilder inicioPlantilla = new StringBuilder();
	inicioPlantilla.append("");
	inicioPlantilla.append("<!DOCTYPE html>");
	inicioPlantilla.append("<html>");
	inicioPlantilla.append("<head>");
	inicioPlantilla.append("<meta charset=\"utf-8\" />");
	inicioPlantilla.append("<meta name=\"author\" content=\"jcarranza\"/>");
	inicioPlantilla.append("<meta name=\"description\" content=\"Requsición\"/>");
	inicioPlantilla.append("<title>Notificación</title>");
	inicioPlantilla.append("</head>");
	inicioPlantilla.append("<body style=\"font-family:Verdana,Arial,lucida,sans-serif; font-size:12px; margin:10px auto\" fpstyle=\"1\" ocsi=\"1\">");
	inicioPlantilla.append("<br>");
	inicioPlantilla.append("<br>");
	inicioPlantilla.append("<br>");
	inicioPlantilla.append("<br>");
	inicioPlantilla.append("<table width=\"95%\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">");
	inicioPlantilla.append("@@1@@");
	inicioPlantilla.append("<tr>");
	inicioPlantilla.append("<td colspan=\"3\">");
	inicioPlantilla.append("<table width=\"95%\" border=\"0\" align=\"center\" style=\"background-color:#fefefe; border:1px solid #A8CEF0; padding:0px 5px 5px 5px; word-spacing:2px\">");
	inicioPlantilla.append("<tr>");
	inicioPlantilla.append("<td colspan=\"3\">");
	inicioPlantilla.append("<center><br>");
	inicioPlantilla.append("<br>");
	inicioPlantilla.append("<table width=\"95%\" style=\"font-family:Gill,Helvetica,sans-serif; font-size:12px\">");
	inicioPlantilla.append("<tr>");
	inicioPlantilla.append("<td style=\"text-align:center; width:15%;\"> <img src='cid:logoCompany' width='95px' height='45px;'  />  </td>");
	inicioPlantilla.append("<td style=\"text-align:center; width:70%;\"><h4>@@2@@</h4></td>");
	inicioPlantilla.append("<td style=\"text-align:center; width:15%;\"> <img src='cid:logoEsr' width='95px' height='45px;' />  </td>");
	inicioPlantilla.append("</tr>");
	inicioPlantilla.append("</table>");
	inicioPlantilla.append("</center>");
	inicioPlantilla.append("</td>");
	inicioPlantilla.append("</tr>");
	return inicioPlantilla.toString();
    }

    /**
     * @return the estiloTituloTabla
     */
    public String getEstiloTituloTabla() {
	return estiloTituloTabla;
    } 
    public String getEncabezado() {
        return encabezado;
    }

    public String getHtmlEncabezado() {
        return htmlEncabezado;
    }

    public String getHtmlPie() {
        return htmlPie;
    }

    /**
     * @return the botonCancelar
     */
    public String getBotonCancelar() {
        return botonCancelar;
    }

    /**
     * @param botonCancelar the botonCancelar to set
     */
    public void setBotonCancelar(String botonCancelar) {
        this.botonCancelar = botonCancelar;
    }

}
