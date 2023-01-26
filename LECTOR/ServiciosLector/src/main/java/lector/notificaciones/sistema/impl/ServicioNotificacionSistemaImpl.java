/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.notificaciones.sistema.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import lector.constantes.Constantes;
import lector.correo.service.EnviarCorreoImpl;
import lector.servicios.sistema.impl.SiParametroImpl;
import lector.servicios.sistema.impl.SiUsuarioRolImpl;

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
    @Inject
    private SiUsuarioRolImpl usuarioRolRemote;

    
    public boolean enviarClave(String nombre, String correo, String clave, String idUsuario) {
        return this.enviarCorreo.enviarCorreoIhsa(correo, "", "", "Contraseña de usuario", this.html.getHtmlClaveUsuario(nombre, clave, idUsuario), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarClaveIhsa(String nombre, String correo, String clave) {
        return this.enviarCorreo.enviarCorreoIhsa(correo, "", "", "Contraseña de usuario", this.html.getHtmlClaveUsuario(nombre), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarCorreoCambioClaveIhsa(String nombre, String correo) {
        return this.enviarCorreo.enviarCorreoIhsa(correo, "siaihsa@ihsa.mx", "", "Cambio de contraseña", this.html.getHtmlClaveUsuario(nombre), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarReinicioClave(String nombre, String email, String clave, String idUsuario) {
        return this.enviarCorreo.enviarCorreoIhsa(email, "siaihsa@ihsa.mx", "", "Contraseña de usuario", this.html.getHtmlClaveUsuario(nombre, clave, idUsuario), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarExcepcion(String sesion, String para, Object object, String asunto, String excepcion) {
        return false;
    //    return enviarCorreo.enviarCorreoIhsa(para, "", "", asunto, html.htmlExcepcionSia(sesion, object, asunto, excepcion), parametrosSistema.find(1).getLogo());
    }

}
