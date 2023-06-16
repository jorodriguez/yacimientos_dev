/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.notificaciones.sistema.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import mx.ihsa.constantes.Configurador;
import mx.ihsa.correo.service.CodigoHtml;
import mx.ihsa.modelo.SiPlantillaHtml;
import mx.ihsa.servicios.sistema.impl.SiPlantillaHtmlImpl;

/**
 *
 */
@Stateless 
public class HtmlNotificacionSistemaImpl extends CodigoHtml {

    @Inject
    private SiPlantillaHtmlImpl plantillaHtml;

    
    public StringBuilder getHtmlClaveUsuario(String nombre, String clave, Integer idUsuario) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio())
                .append(this.getTitulo("Contraseña de usuario"))
                .append("Estimado, &nbsp; ")
                .append(nombre)
                .append("<Br/><Br/><p>Se ha cambiado su contraseña para ingreso al SIA, puede acceder al sistema de inmediato.")
                .append("<Br/><Br/>Usuario: ")
                .append("<b>")
                .append(idUsuario)
                .append("</b>")
                .append("<Br/>Contraseña: ")
                .append("<b>")
                .append(clave)
                .append("</b>")
                .append("<p>").append("<a target=\"_blank\" href=\"").append(Configurador.urlSistema()).append("Sia\">")
                .append("Clic aquí para ir al Sistema</a></p>")
                .append("<p>Por seguridad y comodidad le recomendamos cambiar su contraseña.").append("</p>")
                .append("Gracias, <br/> El equipo del SIA.")
                .append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlClaveUsuario(String nombre) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        //titulo principal en el correo
        this.cuerpoCorreo.append(this.getTitulo("Cambio de contraseña"));
        this.cuerpoCorreo.append("<br/><p>Estimado, ");
        this.cuerpoCorreo.append("".concat(nombre).concat("</p><br/> Ha cambiado su contraseña para ingreso al Sistema, puede acceder al sistema de inmediato."));
        this.cuerpoCorreo.append("<Br/><Br/> Gracias, <Br/> El equipo del SIA.");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }


}
