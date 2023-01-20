/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.notificaciones.usuario.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import lector.constantes.Constantes;
import lector.correo.impl.CodigoHtml;
import lector.modelo.SiPlantillaHtml;
import lector.servicios.catalogos.impl.UsuarioImpl;
import lector.servicios.sistema.impl.SiPlantillaHtmlImpl;

/**
 *
 */
@Stateless 
public class HtmlNotificacionUsuarioImpl extends CodigoHtml {

    @Inject
    private SiPlantillaHtmlImpl plantillaHtml;
    @Inject
    private UsuarioImpl usuarioRemote;

    
    public StringBuilder getHtmlNotificationBajaUsuario(String nombreResponsableGerencia, String nombreUsuarioBaja, String asunto, String link) {
	SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
	this.limpiarCuerpoCorreo();
	this.cuerpoCorreo.append(plantilla.getInicio());
	this.cuerpoCorreo.append(this.getTitulo(asunto));
	this.cuerpoCorreo.append("<br/><p>Estimado(a) <b> ".concat(nombreResponsableGerencia).concat("</b></p>"));
	this.cuerpoCorreo.append("<p>El Departamento de Recursos Humanos ha iniciado el proceso de separación laboral del empleado <b>").append(nombreUsuarioBaja).append(".</b></p>");
	this.cuerpoCorreo.append("<p>Como parte del proceso es necesario liberar al empleado de asuntos pendientes ingresando al X y notificándolo.</p>");
	this.cuerpoCorreo.append("<p>Si existiera algún adeudo del personal informarlo a Recursos Humanos.</p><br/>");
	this.cuerpoCorreo.append(plantilla.getFin());
	return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlNotificationUsuarioLiberadoCompletamente(String nombreResponsableGerencia, String nombreUsuarioLiberado, String asunto) {
	SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
	this.limpiarCuerpoCorreo();
	this.cuerpoCorreo.append(plantilla.getInicio());
	this.cuerpoCorreo.append(this.getTitulo(asunto));
	this.cuerpoCorreo.append("<br/><p>Estimado(a) <b> ".concat(nombreResponsableGerencia).concat("</b></p>"));
	this.cuerpoCorreo.append("<p>El empleado <b>").append(nombreUsuarioLiberado).append("</b> ha sido liberado por todas las gerencias a las que se notificó de su separación laboral y está listo para terminar su proceso.</p>");
	this.cuerpoCorreo.append(plantilla.getFin());
	return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlNotificationFinalizaProcesoBaja(String idUsuario, String asunto) {
	SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
	this.limpiarCuerpoCorreo();
	this.cuerpoCorreo.append(plantilla.getInicio());
	this.cuerpoCorreo.append(this.getTitulo(asunto));
	this.cuerpoCorreo.append("<br/><p>Ha concluido el proceso de separación laboral del empleado (a) <b> ".concat(usuarioRemote.find(idUsuario).getNombre()).concat(".</b>"));

	this.cuerpoCorreo.append(plantilla.getFin());
	return this.cuerpoCorreo;
    }
}
