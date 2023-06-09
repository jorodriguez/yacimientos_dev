/*
 * CorreoImpl.java
 * Creado el 30/06/2009, 10:39:53 AM
 * EJB con estado desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este EJB con estado (Stateless Session EJB), asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package lector.notificaciones.sistema.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.ejb.Stateless;
import javax.inject.Inject;
import lector.correo.service.EnviarCorreoImpl;
import lector.modelo.Usuario;
import lector.servicios.catalogos.impl.UsuarioImpl;
import lector.servicios.sistema.impl.SiParametroImpl;
import lector.servicios.sistema.impl.SiPlantillaHtmlImpl;

/**
 *
 */
@Stateless 
public class CorreoImpl {

    //Servicios
    @Inject
    private EnviarCorreoImpl enviarCorreoRemote;    
    private String s;

    
    @Deprecated
    public boolean enviarCorreo(String para, String conCopia, String copiasOcultas, String asunto, boolean formatoHTML, StringBuilder cuerpoDelMensaje, boolean debug) {
	return this.enviarCorreoRemote.enviarCorreo(para, conCopia, copiasOcultas, asunto, cuerpoDelMensaje);
    }

    private boolean enviar(String para, String conCopia, String copiasOcultas, String asunto, StringBuilder mensaje) {
	return this.enviarCorreoRemote.enviarCorreo(para, conCopia, copiasOcultas, asunto, mensaje);
    }

    public boolean enviarClave(Usuario para) {
	StringBuilder cuerpoMensajeSB = new StringBuilder();

	cuerpoMensajeSB.append("Hola, ").append(para.getNombre()).append(s).append(".<Br/><Br/> Recientemente solicitaste tu contraseña, la cual es: ").append(para.getClave()).append(" <Br/><Br/> Gracias, <Br/> El equipo del SIA. <Br/><Br/><Br/><Br/>" + "<center><font face=arial size=1> Mensaje generado automáticamente por el Sistema Integral de Administración. </font></center>");
	return this.enviar(para.getEmail(), "", "", "Contraseña de usuario", cuerpoMensajeSB);
    }

}
