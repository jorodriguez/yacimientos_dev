/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.servicios.sistema.impl;

import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import lector.constantes.Constantes;
import lector.correo.impl.CodigoHtml;
import lector.modelo.SiPlantillaHtml;

/**
 *
 * @author ihsa
 */
@Stateless 
public class HtmlNotificaSistemaImpl extends CodigoHtml{

    @Inject
    private SiPlantillaHtmlImpl plantillaHtml;
    
    
    public StringBuilder mensajeExcepcion(String asunto, String modulo, String opcion, String mensaje){
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo(asunto));
        cuerpoCorreo.append("<p> El día de hoy <b>").append(Constantes.FMT_ddMMyyy.format(new Date()));
        cuerpoCorreo.append(" </b> a las ").append(Constantes.FMT_HHmmss.format(new Date())).append(", se registró");
        cuerpoCorreo.append(" una excepción en el módulo ").append(modulo).append(" en la opción ").append(opcion);
        cuerpoCorreo.append(" </p>");
        cuerpoCorreo.append(" Mensaje: <b>").append(mensaje).append("</b>");
        cuerpoCorreo.append(" ");
        cuerpoCorreo.append("<br/><br/> Saludos.");
        //Aquí va todo el contenido del cuerpo,
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }

    
    public StringBuilder mensajeNotificaError(String asunto, String mensaje) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo(asunto));
        cuerpoCorreo.append("<p> El día de hoy <b>").append(Constantes.FMT_ddMMyyy.format(new Date()));
        cuerpoCorreo.append(" </b> a las <b>").append(Constantes.FMT_HHmmss.format(new Date())).append("</b>, se registró").append(" una excepción en el SIA.");
        cuerpoCorreo.append(" </p><br/><br/>");
        cuerpoCorreo.append("   Mensaje: <b>").append(mensaje).append("</b>");
        cuerpoCorreo.append(" ");
        cuerpoCorreo.append("<br/><br/><br/> Saludos.");
        //Aquí va todo el contenido del cuerpo,
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }
}
