/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.correo.service;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
    protected static final int plantilla_LogoSIA = 1;
    
    private StringBuilder encabezadoHtml, inicioHtml, finHtml = new StringBuilder("");
    private String autor = "Sistema";
    private String descripcion = "Notificaciones";
    
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
	    this.encabezadoHtml.append("<style type=\"text/css\">");
	

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
        this.inicioHtml.append("</style>");
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


    public String getEncabezado() {
        return encabezado;
    }

    public String getHtmlEncabezado() {
        return htmlEncabezado;
    }

    public String getHtmlPie() {
        return htmlPie;
    }


}
