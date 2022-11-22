/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.requisicion.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import sia.constantes.Constantes;
import sia.correo.impl.EnviarCorreoImpl;
import sia.modelo.InvArticulo;
import sia.modelo.Rechazo;
import sia.modelo.Requisicion;
import sia.modelo.Usuario;
import sia.modelo.requisicion.vo.RequisicionMovimientoVO;
import sia.modelo.requisicion.vo.RequisicionReporteVO;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.modelo.sistema.vo.CategoriaVo;
import sia.pdf.impl.SiaPDFImpl;
import sia.servicios.sistema.impl.SiParametroImpl;

/**
 *
 * @author hacosta
 */
@Stateless 
public class NotificacionRequisicionImpl{

    @Inject
    private HtmlNotificacionRequisicionImpl html;
    @Inject
    private SiParametroImpl parametrosSistema;
    @Inject
    private EnviarCorreoImpl enviarCorreoRemote;
    @Inject
    private SiaPDFImpl siaPDFRemote;

    
    public boolean envioMailDevolucionRequision(String email, String mailUsuarioCompra, String mailSia, Requisicion requisicion, String motivo) {
        return enviarCorreoRemote.enviarCorreoIhsa(email, mailUsuarioCompra, mailSia, "Devolución de la requisición - " + requisicion.getConsecutivo(), html.mensajeDevolucionRequisicion(requisicion, motivo), parametrosSistema.find(1).getLogo());
    }

    
    public boolean envioSolicitudRequisicion(String para, String conCopia, String copiasOcultas, String asunto, Requisicion requisicion) {
        return enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto,
                html.mensajeSolicitudRequisicion(requisicion, true), requisicion.getCompania().getLogo(), 
                requisicion.getCompania().getLogoEsr());
    }

    
    public boolean envioNotificacionRequisicion(String para, String conCopia, String copiasOcultas, String asunto, Requisicion requisicion, String... tipos) {        
        return enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto,
                html.mensajeNotificacionRequisicion(requisicion, true, tipos), requisicion.getCompania().getLogo(), requisicion.getCompania().getLogoEsr());
    }
    
    
    public boolean envioSoliciudAltaArticulo(String para, String conCopia, String copiasOcultas, String asunto, Requisicion requisicion, String descripcion, String uso, List<CategoriaVo> categoriasSeleccionadas, String uMedida) {        
        return enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto,
                html.mensajeNotificacionAltaNuevoArticulo(requisicion, true, descripcion, uso, categoriasSeleccionadas, uMedida), requisicion.getCompania().getLogo(), requisicion.getCompania().getLogoEsr());
    }

    
    public boolean envioNotificacionAltaArticulo(String para, String conCopia, String copiasOcultas, String asunto, InvArticulo articulo, String compania, List<CategoriaVo> categoriasSeleccionadas) {        
        return enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto,
                html.mensajeNotificacionAltaArticulo(articulo, true, compania, categoriasSeleccionadas));
    }
    
    
    public boolean envioNotificacionRequisicionPDF(String para, String conCopia, String copiasOcultas, String asunto, Requisicion requisicion, Usuario usr, String... tipos) throws Exception {        
        return enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto,
                html.mensajeNotificacionRequisicionPDF(requisicion, true, tipos),
                requisicion.getCompania().getLogo(), requisicion.getCompania().getLogoEsr(), siaPDFRemote.getPDF(requisicion, usr, true), null, requisicion.getCompania().getSiglas());
    }
    
    
    
    public boolean envioReporteDiarioCompradores(String para, String cc, String cco, List<RequisicionReporteVO> lReporte, String asunto, int dias, List<RequisicionReporteVO> listaTotalComprador) {
            return enviarCorreoRemote.enviarCorreoIhsa(para, cc, cco, asunto,html.mensajeReporteRequisicion(lReporte, asunto, dias, listaTotalComprador), parametrosSistema.find(1).getLogo());
    }
    
    
    public boolean envioRechazoRequisicion(String para, String conCopia, String copiasOcultas, String asunto, Requisicion requisicion, Rechazo rechazo){
        return enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto,
                html.mensajeRechazoRequisicion(requisicion, rechazo, true), requisicion.getCompania().getLogo(), requisicion.getCompania().getLogoEsr());
    }
    
    
    public boolean envioNotaRequisicion(String para, String conCopia, String copiasOcultas, String asunto, Requisicion requisicion, String autor, String nota, String... tipos){
        return enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto,
                html.mensajeNotaRequisicion(requisicion, asunto,autor, nota, true, tipos), requisicion.getCompania().getLogo(), requisicion.getCompania().getLogoEsr());
    }
    
     
    public boolean envioCancelacionRequisicion(String para, String conCopia, String copiasOcultas, String asunto, Requisicion requisicion, String... tipos) {
        return enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto,
                html.mensajeCancelacionRequisicion(requisicion, true, tipos), requisicion.getCompania().getLogo(), requisicion.getCompania().getLogoEsr());
    }
     
    
    public boolean envioAutorizadaRequisicion(String para, String conCopia, String copiasOcultas, String asunto, Requisicion requisicion, String... tipos){
     return enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto,
                html.mensajeAutorizadaRequisicion(requisicion, true, tipos), requisicion.getCompania().getLogo(), requisicion.getCompania().getLogoEsr());
     }
    
    
    public boolean envioActivarRequisicion(String para, String conCopia, String copiasOcultas, String asunto, Requisicion requisicion, RequisicionMovimientoVO moo, String... tipos){
     return enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto,
                html.mensajeActivarRequisicion(requisicion, true, moo, tipos), requisicion.getCompania().getLogo(), requisicion.getCompania().getLogoEsr());
     }
    
    
    public boolean envioFinalizarRequisicion(String para, String conCopia, String copiasOcultas, String asunto, Requisicion requisicion, String... tipos){
     return enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto,
                html.mensajeFinalizarRequisicion(requisicion, true, tipos), requisicion.getCompania().getLogo(), requisicion.getCompania().getLogoEsr());
     }
    
    
    public boolean envioEsperaRequisicion(String para, String conCopia, String copiasOcultas, String asunto, Requisicion requisicion, String... tipos){
     return enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto,
                html.mensajeEsperaRequisicion(requisicion, true, tipos), requisicion.getCompania().getLogo(), requisicion.getCompania().getLogoEsr());
     }

    
    public boolean envioReasignarRequisicion(String para, String cc, String cco, String asunto, Requisicion requisicion) {
        return enviarCorreoRemote.enviarCorreoIhsa(para, cc, cco, asunto,
                html.mensajeReasignarRequisicion(requisicion, asunto), requisicion.getCompania().getLogo(), requisicion.getCompania().getLogoEsr());
    }

    
    public boolean enviarCorreoCambioRequisiciones(String para, String cc, String mailSesion, List<RequisicionVO> lo, String nombreActual, String nombreAprobara, String rfcEmpresa, String status) {
        String asunto = new StringBuilder("CAMBIO DE REQUISICIONES  --").append(Constantes.FMT_ddMMyyy.format(new Date())).toString();
        return enviarCorreoRemote.enviarCorreoIhsa(para, cc, mailSesion, asunto, 
                html.mensajeCambioRequisiciones(lo, nombreActual, nombreAprobara, asunto, status), parametrosSistema.find(1).getLogo());
    }
}
