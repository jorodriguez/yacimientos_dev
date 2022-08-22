

package sia.controloficios.backing.bean;

import java.util.List;
import javax.faces.bean.ManagedBean;
import sia.excepciones.SIAException;
import sia.modelo.estatus.vo.EstatusVo;
import sia.modelo.oficio.vo.OficioEntradaVo;
import sia.modelo.oficio.vo.OficioSalidaVo;

/**
 * Managed bean para los popups de información de tipos de oficio.
 * 
 * Invocado desde los siguientes archivos: 
 * 
 * popupOficioSalida.xhtml
 * popupOficioEntrada.xhtml
 *
 * @author esapien
 */
@ManagedBean
public class OficioPopupBean extends OficioBaseBean {
    
    List<EstatusVo> estatusEntrada;
    List<EstatusVo> estatusSalida;

    @Override
    protected void postConstruct() throws SIAException {
        
        estatusEntrada = new OficioEntradaVo().getEstatusLista();
        estatusSalida = new OficioSalidaVo().getEstatusLista();
        
    }

    @Override
    protected boolean permisosRequeridos() {
        return getPermisos().isConsultarOficio();
    }
    

    public List<EstatusVo> getEstatusEntrada() {
        return estatusEntrada;
    }

    public List<EstatusVo> getEstatusSalida() {
        return estatusSalida;
    }
    
}
