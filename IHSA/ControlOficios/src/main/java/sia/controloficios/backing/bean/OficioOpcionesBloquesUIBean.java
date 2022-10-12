

package sia.controloficios.backing.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import sia.modelo.campo.usuario.puesto.vo.CompaniaBloqueGerenciaVo;


/**
 * Define la gestión para la configuración de los  elementos para una interfaz 
 * de usuario que maneja opciones de compañía, bloque y gerencia (ej. combos 
 * relacionados), a configurar en base a los permisos del usuario.
 * 
 * <p/>Si el usuario puede ver todas las gerencias, se le muestran todas las opciones.
 * De lo contrario:
 * 
 * <ul>
 * <li>Si el usuario es editor (emisor o receptor), se le muestran las opciones de 
 * bloques a los que está asignado.
 * </li>
 * <li>
 * Si el usuario no es editor (ej. solo consultas), se muestra solo la gerencia 
 * a la que está asignado.
 * </li>
 *      
 * </ul>
 * 
 * @author esapien
 */
public abstract class OficioOpcionesBloquesUIBean extends OficioBaseBean {
    
    
    /**
     * Para combo en UI en caso de no tener acceso a todas las gerencias.
     */
    private List<SelectItem> opcionesBloques;
    
    
    /**
     * Para modos de visualizacion de los bloques y gerencias en pantalla
     */
    private boolean mostrarTodasGerencias;
    private boolean mostrarOpcionUnicaBloque;
    private boolean mostrarOpcionesBloques;
    
    
    /**
     * Mapa de valores para campos
     */
    private Map<Integer, CompaniaBloqueGerenciaVo> companiasBloquesGerencias;
    
    
    /**
     * 
     */
    protected void configurarCombosCompaniaBloqueGerencia() {
        
        // inicializar combos de bloques y gerencias en funcion de los permisos
        // del usuario
        if (getPermisos().isVerTodoGerencias()) {

            mostrarTodasGerencias = true;
            mostrarOpcionesBloques = false;
            mostrarOpcionUnicaBloque = false;

            // cargar los combos correspondientes en vista de usuario

            getCatalogosBean().actualizarBloques(getVo().getCompaniaId());
            getCatalogosBean().actualizarGerencias(getVo().getBloqueId());

        } else if (getPermisos().isRolEdicionOficios()) {

            // mostrar combos si el usuario es editor
            // mostrar opciones de bloques en combo

            mostrarTodasGerencias = false;
            mostrarOpcionesBloques = true;
            mostrarOpcionUnicaBloque = false;

            cargarOpcionesBloques(getSesion().getBloquesUsuario());

            // actualizar lista de gerencias para combo en UI en funcion del bloque actual
            getCatalogosBean().actualizarGerencias(getVo().getBloqueId());

        } else {

            // mostrar opcion única de bloque como texto

            mostrarTodasGerencias = false;
            mostrarOpcionesBloques = false;
            mostrarOpcionUnicaBloque = true;
        }

    }
    
    /**
     * Carga la lista de bloques para el combo en UI.
     * 
     * 
     * @param bloquesUsuario 
     */
    private void cargarOpcionesBloques(List<CompaniaBloqueGerenciaVo> bloquesUsuario) {
        
        opcionesBloques = new ArrayList<SelectItem>();

        companiasBloquesGerencias = new HashMap<Integer, CompaniaBloqueGerenciaVo>();

        for (CompaniaBloqueGerenciaVo cbgVo : bloquesUsuario) {

            getLogger().info(this, "bloqueUsuario = " + cbgVo.toString());

            // cargar combo para vista

            SelectItem item = new SelectItem(cbgVo.getBloqueId(), cbgVo.getBloqueNombre());

            opcionesBloques.add(item);

            // agregar a mapa correspondiente

            companiasBloquesGerencias.put(cbgVo.getBloqueId(), cbgVo);

        }
    }
    
    
    /**
     * Invocado en caso que se ha cambiado de valor en el combo de 
     * opciones de bloques, para un usuario sin permiso Ver Todas las Gerencias.
     * 
     * @param event 
     */
    public void actualizarCompaniaGerencia(ValueChangeEvent event) {
        
        Integer bloqueId = Integer.parseInt(event.getNewValue().toString());
        
        getLogger().info(this, "@actualizarCompaniaGerencia - bloqueId = " + bloqueId);
        
        // obtener datos para compania y gerencia
        CompaniaBloqueGerenciaVo cbg = companiasBloquesGerencias.get(bloqueId);
        
        // actualizar valores en bean
        configurarVo(getVo(), cbg);
        
        // actualizar gerencias
        getCatalogosBean().actualizarGerencias(cbg.getBloqueId());
        
    }
    
    public SelectItem agregarBloqueCopy(int bloque) {
        SelectItem s = new SelectItem();
        for(SelectItem  o : this.getCatalogosBean().getBloques()){
            if(Integer.parseInt(o.getValue().toString())== bloque){
                s = o; 
                break;
            }
        }
        return  s;

    }
    
    
    public boolean isMostrarOpcionUnicaBloque() {
        return mostrarOpcionUnicaBloque;
    }

    public boolean isMostrarOpcionesBloques() {
        return mostrarOpcionesBloques;
    }

    public boolean isMostrarTodasGerencias() {
        return mostrarTodasGerencias;
    }

    public List<SelectItem> getOpcionesBloques() {
        return opcionesBloques;
    }
    
    
}
