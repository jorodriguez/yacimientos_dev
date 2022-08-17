

package sia.controloficios.backing.bean;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.estatus.vo.EstatusVo;
import sia.modelo.oficio.vo.OficioConsultaVo;
import sia.modelo.oficio.vo.OficioEntradaVo;
import sia.modelo.oficio.vo.OficioSalidaVo;

/**
 *
 * Bean administrado para la pantalla de Consulta de Oficios.
 * 
 * @author esapien
 */
@ManagedBean
//@Named
@ViewScoped
public class OficioConsultaBean extends OficioOpcionesBloquesUIBean {
    
    
    private int oficioEntradaId;
    
    private int oficioSalidaId;
    
    
    /**
     * 
     * @throws SIAException 
     */
    @Override
    protected void postConstruct() throws SIAException {
        //setear valor nuevo para mostrar publicos
        // valores iniciales para pantalla de consulta
        System.out.println("@@@@@EJECUTANDO postconstruct de consulta bean");
        // validar si no hay una consulta iniciada en la sesión
        if (getSesion().getOficioConsultaVo() == null) {
            
            iniciarSesionConsultaVo();
        }
        
        setVo(getSesion().getOficioConsultaVo());
        getVo().setMaxOficios(Constantes.OFICIOS_MAXIMO_RETORNO_CONSULTA_INICIAL);
        // establecer filtros de bloques para consulta 
        configurarCombosCompaniaBloqueGerencia();
        
        
        // mostrar resultados con filtros actuales al ingresar
        this.buscarOficios(null);

        // desactivar modo edición en esta sesión
        this.getSesion().setModoEdicion(false);
        
        // valores para popup de simbologia
        this.oficioEntradaId = Constantes.OFICIOS_TIPO_OFICIO_ENTRADA_ID;
        this.oficioSalidaId = Constantes.OFICIOS_TIPO_OFICIO_SALIDA_ID;
        
        
    } // fin @PostConstruct
    

    /**
     * 
     * @return 
     */
    @Override
    protected boolean permisosRequeridos() {
        return getPermisos().isConsultarOficio();
    }
    
    public List<EstatusVo> getEstatusEntrada() {
        return new OficioEntradaVo().getEstatusLista();
    }

    public List<EstatusVo> getEstatusSalida() {
        return new OficioSalidaVo().getEstatusLista();
    }

    public int getOficioEntradaId() {
        return oficioEntradaId;
    }

    public int getOficioSalidaId() {
        return oficioSalidaId;
    }
    
    /**
     * Establece el bean de consulta en curso a sus valores iniciales
     * y resultados correspondientes.
     * 
     */
    public void limpiarConsultaVo(ActionEvent actionEvent) {
        
        iniciarSesionConsultaVo();
        
        setVo(getSesion().getOficioConsultaVo());
        
        // mostrar resultados con filtros actuales
        this.buscarOficios(null);
    }

    /**
     * 
     * Establece el vo de consulta inicial para la sesión.
     * 
     */
    private void iniciarSesionConsultaVo() {

        // inicializar bean default para las consultas
        
        
        // Removido 2/dic/14 - Generar VO vacío para búsqueda abierta
        /*OficioConsultaVo vo = OficioConsultaVo.instanciaMesActual();*/
        
        
        OficioConsultaVo vo = new OficioConsultaVo();
        
        // el rol de edición de oficios (emisores y receptores) deberán ver 
        // los combos sin preseleccionar para facilitar búsqueda y registro
        if (!getPermisos().isRolEdicionOficios()) {
            // inicializar a bloque activo del usuario
            configurarVo(vo, getSesion().getBloqueActivo());
        }

        getSesion().setOficioConsultaVo(vo);
    }


    
}
