/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.evaluacion.impl;

import java.io.File;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import sia.correo.impl.EnviarCorreoImpl;
import sia.modelo.CvConvenioEvaluacion;
import sia.modelo.CvEvaluacion;
import sia.servicios.sistema.impl.SiParametroImpl;

/**
 *
 * @author jcarranza
 */
@LocalBean 
public class NotificacionEvaluacionImpl  {
    
    @Inject
    private EnviarCorreoImpl enviarCorreoRemote;
    @Inject
    private HtmlNotificacionEvaluacionImpl htmlNotificacionEvaluacion;
    @Inject
    private SiParametroImpl siParametroRemote;
    
    
    
    public boolean notificacionEvaluacion(String para, String conCopia, String copiasOcultas, String asunto, CvConvenioEvaluacion conEva) {
	return enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto, 
                siParametroRemote.find(1).getLogo(), 
                siParametroRemote.find(2).getLogo(), 
                htmlNotificacionEvaluacion.nuevaEvaluacion(asunto, conEva),                 
                null);
    }

    
    public boolean notificacionRespuestaEvaluacion(String para, String copia, String copiaOculta, String asunto, CvEvaluacion eval, File pdf) {
	return enviarCorreoRemote.enviarCorreoIhsa(para, copia, copiaOculta, asunto,  
                siParametroRemote.find(1).getLogo(), 
                siParametroRemote.find(2).getLogo(), 
                htmlNotificacionEvaluacion.respuestaEvaluacion(asunto, eval),
                pdf);
    }
    
}
