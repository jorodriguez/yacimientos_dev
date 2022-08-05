

package sia.controloficios.backing.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import sia.constantes.Constantes;
import sia.controloficios.sistema.bean.backing.Sesion;
import sia.controloficios.sistema.soporte.FacesUtils;
import sia.excepciones.SIAException;
import sia.modelo.campo.usuario.puesto.vo.CompaniaBloqueGerenciaVo;
import sia.modelo.rol.vo.RolVO;

/**
 * Managed bean para los elementos de plantilla del modulo de Control de Oficios:
 * 
 * Menu, cabecera, pie.
 *
 * @author esapien
 */
@ManagedBean
public class OficioPlantillaBean extends OficioBaseBean {
    
    /**
     * Para combo en UI en cabecera
     */
    private List<SelectItem> opcionesBloques;
    
    
    /**
     * Mapa de valores para las opciones de bloques del usuario
     */
    private Map<String, CompaniaBloqueGerenciaVo> companiasBloquesGerencias;

    /**
     * 
     * @throws SIAException 
     */
    @Override
    protected void postConstruct() throws SIAException {
        
        // llenar combo de bloques del usuario
        
        List<CompaniaBloqueGerenciaVo> bloquesUsuario = getSesion().getBloquesUsuario();
        
        opcionesBloques = new ArrayList<SelectItem>();
            
        companiasBloquesGerencias = new HashMap<String, CompaniaBloqueGerenciaVo>();
        
        for (CompaniaBloqueGerenciaVo vo : bloquesUsuario) {
            
            // cargar combo para vista

            SelectItem item = new SelectItem(vo.getRegistroId(), vo.getBloqueNombre());

            opcionesBloques.add(item);

            // agregar a mapa correspondiente

            companiasBloquesGerencias.put(vo.getRegistroId(), vo);

        }
        
    }

    /**
     * 
     * @return 
     */
    @Override
    protected boolean permisosRequeridos() {
        
        // validar permisos minimos
        return getPermisos().isConsultarOficio();
        
    }
    
    
    /**
     * 
     * @return 
     */
    public void cambiarBloqueActivo() {
        
        String registroId = FacesUtils.getRequestParameter("registroId");
        
        getLogger().info(this, "registroId = " + registroId);
        
        // obtener datos para compania y gerencia
        
        CompaniaBloqueGerenciaVo cbg = companiasBloquesGerencias.get(registroId);
        
        this.getSesion().setBloqueActivo(cbg);
        getSesion().setPermisos(null);
        //falta poder cambiar de permisos segun el bloque en el que se encuentre
        //List<RolVO> roles = siPermisoServicio.fetchPermisosPorUsuarioModulo(getSesion().getUsuario().getId(), Constantes.OFICIOS_MODULO_ID, 1);
        
        // reiniciar vo de consulta
        getSesion().setOficioConsultaVo(null);
        
        // redireccionar a inicio
        redireccionar(Constantes.URL_REL_CONTROL_OFICIOS);
        
        
    }
    

    public List<SelectItem> getOpcionesBloques() {
        return opcionesBloques;
    }
    
    
    
    
    
    
}
