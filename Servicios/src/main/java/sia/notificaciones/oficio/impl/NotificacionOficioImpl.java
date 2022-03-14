

package sia.notificaciones.oficio.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.mail.MessagingException;
import sia.correo.impl.EnviarCorreoImpl;
import sia.modelo.oficio.vo.OficioPromovibleVo;
import sia.modelo.oficio.vo.OficioVo;
import sia.modelo.sistema.vo.CorreoVo;
import sia.servicios.sistema.impl.SiParametroImpl;

/**
 * Contiene los métodos para el envío de las notificaciones para el módulo de 
 * Control de Oficios.
 *
 * @author esapien
 */
@LocalBean 
public class NotificacionOficioImpl  {

    @Inject
    private EnviarCorreoImpl enviarCorreoRemote;
    @Inject
    private HtmlNotificacionOficioImpl htmlNotificacionOficioServicio;
    @Inject
    private SiParametroImpl parametrosSistema;
    
    /**
     * 
     * @param correo
     * @param oficios 
     */
    
    public void enviarNotificacionAltaOficios(CorreoVo correo, 
    List<OficioPromovibleVo> oficios) throws MessagingException {
        
        StringBuilder contenido 
                = new StringBuilder(
                htmlNotificacionOficioServicio.contenidoNotificacionAltaOficios(oficios, correo.getAsunto()));
        
        boolean enviado = enviarCorreoRemote.enviarCorreoIhsa(
                correo.getPara(),
                correo.getCc(),
                correo.getCco(),
                correo.getAsunto(),
                contenido, 
                parametrosSistema.find(1).getLogo());
        
        if (!enviado) {
            throw new MessagingException();
        }
        
    }
    
    
    /**
     * 
     * @param correo
     * @param oficios 
     */
    
    public void notificarPromocionOficio(CorreoVo correo, OficioPromovibleVo oficio) throws MessagingException {
        
        StringBuilder contenido 
                = new StringBuilder(
                htmlNotificacionOficioServicio
                .contenidoNotificacionPromocionOficio(oficio));
        
        boolean enviado = enviarCorreoRemote.enviarCorreoIhsa(
                correo.getSetPara(),
                correo.getSetCc(),
                correo.getSetCco(),
                correo.getAsunto(),
                contenido.toString(),
                parametrosSistema.find(1).getLogo());
        
        if (!enviado) {
            throw new MessagingException();
        }
        
    }
    
     
    //jevazquez 18/02/15
     public void notificarModificaOficio(CorreoVo correo, OficioVo oficio) throws MessagingException {
        
        StringBuilder contenido 
                = new StringBuilder(
                htmlNotificacionOficioServicio
                .contenidoNotificacionModificacionOficio(oficio));
        
        boolean enviado = enviarCorreoRemote.enviarCorreoIhsa(
                correo.getSetPara(),
                correo.getSetCc(),
                correo.getSetCco(),
                correo.getAsunto(),
                contenido.toString(),
                parametrosSistema.find(1).getLogo());
        
        if (!enviado) {
            throw new MessagingException();
        }
        
    }
    
    //jevazquez 23/feb/2015 aprobado
    
    public void enviarNotificacionNoPromovidas (CorreoVo correo, List<OficioPromovibleVo> oficios) throws MessagingException {
        
        StringBuilder contenido 
                = new StringBuilder(
                htmlNotificacionOficioServicio.contenidoNotificacionNoModificadoSemana(oficios));
        
        boolean enviado = enviarCorreoRemote.enviarCorreoIhsa(
                correo.getPara(),
                correo.getCc(),
                correo.getCco(),
                correo.getAsunto(),
                contenido, 
                parametrosSistema.find(1).getLogo());
        
        if (!enviado) {
            throw new MessagingException();
        }
    }
    
}
