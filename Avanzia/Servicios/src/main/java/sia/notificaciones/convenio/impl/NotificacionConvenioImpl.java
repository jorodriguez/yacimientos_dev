/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.convenio.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import sia.correo.impl.EnviarCorreoImpl;
import sia.modelo.Compania;
import sia.modelo.contrato.vo.ContratoFormasNotasVo;
import sia.modelo.contrato.vo.ContratoFormasVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.ExhortoVo;
import sia.modelo.contrato.vo.RhConvenioDocumentoVo;
import sia.servicios.sistema.impl.SiParametroImpl;

/**
 *
 * @author ihsa
 */
@Stateless 
public class NotificacionConvenioImpl {

    @Inject
    private EnviarCorreoImpl enviarCorreoRemote;
    @Inject
    private HtmlNotificacionConvenioImpl htmlNotificacionConvenio;
    @Inject
    private SiParametroImpl siParametroRemote;

    
    public boolean notificacionConvenioPorVencer(String para, String conCopia, String copiasOcultas, String asunto, List<ContratoVO> convenios) {
	return enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto, htmlNotificacionConvenio.conveniosPorVencer(asunto, convenios));
    }

    
    public boolean notificacionConvenioVencidos(String para, String copia, String copiaOculta, String asunto, List<ContratoVO> convenios) {
	return enviarCorreoRemote.enviarCorreoIhsa(para, copia, copiaOculta, asunto, htmlNotificacionConvenio.conveniosPorVencidos(asunto, convenios));
    }

    
    public boolean notificacionConvenioPromovido(String para, String cc, String asunto, ContratoVO convenio) {
	return enviarCorreoRemote.enviarCorreoIhsa(para, cc, "", asunto, htmlNotificacionConvenio.convenioPorRevisar(asunto, convenio));
    }

    
    public boolean notificacionConvenioFormalizado(String para, String cc, String asunto, ContratoVO convenio, String empresa, String represetnante, String puesto) throws Exception {
	try {
	    return enviarCorreoRemote.enviarCorreoIhsa(para, cc, "", asunto, htmlNotificacionConvenio.convenioFormalizado(asunto, convenio, empresa, represetnante, puesto), siParametroRemote.find(1).getLogo());
	} catch (Exception e) {
	    throw e;
	}
    }

    
    public boolean notificacionConvenioFiniquitado(String para, String cc, String asunto, ContratoVO contratoVO, String empresa, String represetnante, String puesto) throws Exception {
	try {
	    return enviarCorreoRemote.enviarCorreoIhsa(para, cc, "", asunto, htmlNotificacionConvenio.convenioFiniquitado(asunto, contratoVO, empresa, represetnante, puesto), siParametroRemote.find(1).getLogo());
	} catch (Exception e) {
	    throw e;
	}
    }
    
    /**
     *
     * @param para
     * @param cc
     * @param asunto
     * @param contratoVO
     * @param exhortoVo
     * @param empresa
     * @return
     * @throws Exception
     */
    
    public boolean notificacionExhortoFiniquito(String para, String cc, String asunto, ContratoVO contratoVO, 
            ExhortoVo exhortoVo, Compania empresa) throws Exception {
	try {
	    return enviarCorreoRemote.enviarCorreoIhsa(para, cc, "", 
                    asunto, 
                    htmlNotificacionConvenio.exhortoFiniquito(asunto, contratoVO, exhortoVo, empresa), siParametroRemote.find(1).getLogo());
	} catch (Exception e) {
	    throw e;
	}
    }

    
    public boolean notificacionSolicitudFiniquito(String para, String cc, String asunto, ContratoVO contratoVo, ExhortoVo exhortoVo) {
         try {
	    return enviarCorreoRemote.enviarCorreoIhsa(para, cc, "", 
                    asunto, 
                    htmlNotificacionConvenio.solicitudFiniquito(asunto, contratoVo, exhortoVo), siParametroRemote.find(1).getLogo());
	} catch (Exception e) {
	    throw e;
	}
    }

    
    public boolean notificacionValidacionFormaFiniquito(String para, String asunto, ContratoVO coVo, ContratoFormasVo conFormaVo) {
        try {
	    return enviarCorreoRemote.enviarCorreoIhsa(para, "", "", 
                    asunto, 
                    htmlNotificacionConvenio.validacionFormaFiniquito(coVo, conFormaVo), siParametroRemote.find(1).getLogo());
	} catch (Exception e) {
	    throw e;
	} 
    }

    
    public boolean notificacionValidaForma(String para, String asunto, ContratoFormasVo conFormaVo) {
         try {
	    return enviarCorreoRemote.enviarCorreoIhsa(para, "", "", 
                    asunto, 
                    htmlNotificacionConvenio.mensajeValidarFormaFiniquito(conFormaVo), siParametroRemote.find(1).getLogo());
	} catch (Exception e) {
	    throw e;
	} 
    }

    
    public boolean notificacionObservacionForma(String para, String copia, String asunto, ContratoFormasVo contratoFormasVo, ContratoFormasNotasVo contratoFormasNotasVo) {
         try {
	    return enviarCorreoRemote.enviarCorreoIhsa(para, copia, "", 
                    asunto, 
                    htmlNotificacionConvenio.mensajeObservacionFormaFiniquito(contratoFormasVo, contratoFormasNotasVo), siParametroRemote.find(1).getLogo());
	} catch (Exception e) {
	    throw e;
	} 
    }
    
    
    public boolean notificacionObservacionRh(String para, String asunto, RhConvenioDocumentoVo documentoVo) {
         try {
	    return enviarCorreoRemote.enviarCorreoIhsa(para, "", "", 
                    asunto, 
                    htmlNotificacionConvenio.mensajeDocuementoRh(documentoVo), siParametroRemote.find(1).getLogo());
	} catch (Exception e) {
	    throw e;
	} 
    }
}
