/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.usuario.movil.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import sia.modelo.Requisicion;
import sia.modelo.SiUsuarioCodigo;
import sia.modelo.Usuario;
import sia.modelo.usuario.movil.vo.NotificacionMovilVo;
import sia.servicios.sistema.impl.SiUsuarioCodigoImpl;
import sia.util.UtilLog4j;
import sia.util.notificacion.FCMSender;

/**
 * @author jorodriguez
 */
@LocalBean 
public class NotificacionMovilUsuarioImpl {
   
   
    @Inject
    private SiUsuarioCodigoImpl  siUsuarioCodigoLocal;

    
    public void enviarNotificacion(NotificacionMovilVo notificacion) {
        try {
            if (notificacion != null && !notificacion.getUsuario().isEmpty()) {

                List<SiUsuarioCodigo> listaUsuarioCodigo = siUsuarioCodigoLocal.buscarPorUsuario(notificacion.getUsuario());

                if (listaUsuarioCodigo != null && !listaUsuarioCodigo.isEmpty()) {
                    
                    for(int i=0; i < listaUsuarioCodigo.size();i++){
                        
                        final SiUsuarioCodigo elemento = listaUsuarioCodigo.get(i);
                        
                        FCMSender
                                .notificaciones(
                                        notificacion.getTitulo(),
                                        notificacion.getMensaje(),
                                        elemento.getToken(),
                                        notificacion.getIcono()
                                );
                    }                  
            
                }
            } else {
                UtilLog4j.log.warn(this, "No se envio la notificacion el objeto notificacion es null o no se especifico el usuario receptor ");
            }
        } catch (Exception ex) {
            UtilLog4j.log.warn(this, "", ex);
        }

    }

    
    public void enviarNotificacionRequisicion(Requisicion requisicion, Usuario usuarioReceptor, String titulo) {
        try {

            if (usuarioReceptor != null) {

                NotificacionMovilVo notificacion
                        = NotificacionMovilVo
                                .builder()
                                .titulo(titulo)
                                .mensaje(
                                        new StringBuilder("de la gerencia ")
                                                .append(requisicion.getGerencia() != null ? requisicion.getGerencia().getNombre() : "")
                                                .append(" ")
                                                .append(requisicion.getObservaciones())
                                                .toString()
                                ).usuario(usuarioReceptor.getId())
                                .build();

                enviarNotificacion(notificacion);

            } else {
                UtilLog4j.log.debug(this, "No se envio la notificacion el usuario receptor es null");
            }
        } catch (Exception e) {
            UtilLog4j.log.error("Error al enviar notificaciones de requisición ", e);
        }

    }

}
