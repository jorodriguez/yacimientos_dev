/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.notificaciones.estilos;

import java.io.Serializable;

/**
 *
 * @author hacosta
 */
public class Estilos implements Serializable{

    protected StringBuilder cuerpoCorreo = new StringBuilder("");
    protected StringBuilder cuerpoCorreoFactura = new StringBuilder();
    /**
     * Atributos
     */
    private String test;

    /**
     * Constructor
     */
    public Estilos() {
    }

    /**
     * @return the test
     */
    protected String getTest() {
        return test;
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

    public StringBuilder getCuerpoCorreoFactura() {
        return cuerpoCorreoFactura;
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

    public String getStyle() {
        return style;
    }

    public String getTabla() {
        return tabla;
    }
}
