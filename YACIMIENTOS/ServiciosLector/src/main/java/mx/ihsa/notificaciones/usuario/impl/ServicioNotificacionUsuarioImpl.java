/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.notificaciones.usuario.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import mx.ihsa.correo.service.EnviarCorreoImpl;
import mx.ihsa.servicios.catalogos.impl.UsuarioImpl;
import mx.ihsa.servicios.sistema.impl.SiParametroImpl;

/**
 */
@Stateless 
public class ServicioNotificacionUsuarioImpl {

    @Inject
    private HtmlNotificacionUsuarioImpl html;
    @Inject
    private SiParametroImpl siParametroRemote;
    @Inject
    private EnviarCorreoImpl enviarCorreo;
    @Inject
    private UsuarioImpl usuarioRemote;

    
   
}
