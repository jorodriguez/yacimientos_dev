/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.notificaciones.sistema.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import mx.ihsa.correo.service.EnviarCorreoImpl;
import mx.ihsa.servicios.sistema.impl.SiParametroImpl;
import mx.ihsa.servicios.sistema.impl.SiUsuarioRolImpl;

/**
 * @author hacosta
 */
@Stateless 
public class ServicioNotificacionSistemaImpl{

    @Inject
    private HtmlNotificacionSistemaImpl html;
    @Inject
    private SiParametroImpl parametrosSistema;
    @Inject
    private EnviarCorreoImpl enviarCorreo;

    
    public boolean enviarClave(String nombre, String correo, String clave, Integer idUsuario) {
        return this.enviarCorreo.enviarCorreo(correo, "", "", "Contraseña de usuario", this.html.getHtmlClaveUsuario(nombre, clave, idUsuario), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarClaveIhsa(String nombre, String correo, String clave) {
        return this.enviarCorreo.enviarCorreo(correo, "", "", "Contraseña de usuario", this.html.getHtmlClaveUsuario(nombre), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarCorreoCambioClave(String nombre, String correo) {
        return this.enviarCorreo.enviarCorreo(correo, "", "", "Cambio de contraseña", this.html.getHtmlClaveUsuario(nombre), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarReinicioClave(String nombre, String email, String clave, Integer idUsuario) {
        return this.enviarCorreo.enviarCorreo(email, "", "", "Contraseña de usuario", this.html.getHtmlClaveUsuario(nombre, clave, idUsuario), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarExcepcion(String sesion, String para, Object object, String asunto, String excepcion) {
        return false;
    //    return enviarCorreo.enviarCorreoIhsa(para, "", "", asunto, html.htmlExcepcionSia(sesion, object, asunto, excepcion), parametrosSistema.find(1).getLogo());
    }

}
