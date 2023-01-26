/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.notificaciones.usuario.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import lector.correo.service.EnviarCorreoImpl;
import lector.servicios.catalogos.impl.UsuarioImpl;
import lector.servicios.sistema.impl.SiParametroImpl;

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
