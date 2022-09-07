
package sia.controloficios.backing.bean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import sia.constantes.Constantes;
import sia.controloficios.sistema.soporte.FacesUtils;
import sia.excepciones.MissingRequiredValuesException;
import sia.excepciones.SIAException;
import sia.util.ui.AccionUI;

/**
 *
 * @author esapien
 */
//@ManagedBean
@Named(value = "oficioSeguimientoBean")
public class OficioSeguimientoBean extends OficioBaseBean {
    
    /**
     * Elementos en UI
     * 
     */
    private String titulo;
    
    private String descripcion;
    
    private AccionUI botonAceptar;
    
    private AccionUI botonCancelar;
    
    
    /**
     * Motivo de activación o desactivación de seguimiento
     */
    private String motivo;
    
    
    /**
     * 
     * @throws SIAException 
     */
    @Override
    protected void postConstruct() throws SIAException {
        System.out.println("@postconstruct en OficioSeguimientoBean");
        // obtener registro de oficio
        
        int oficioId = Integer.parseInt(FacesUtils.getRequestParameter("oficioId"));
        
        
        System.out.println("=========== OFICIOID "+oficioId);
        
        setVo(buscarOficioVo(oficioId));
        
        botonAceptar = new AccionUI();
        botonCancelar = new AccionUI();
        
        if (getVo().isRequiereSeguimiento()) {
            titulo =  "Desactivar";
            descripcion = "Se desactivará el seguimiento para este oficio. Favor de proporcionar un motivo de terminación de seguimiento. ¿Desea continuar?";
            botonAceptar.setTitulo("Confirmar desactivación de seguimiento");
            botonCancelar.setTitulo("Cancelar desactivación de seguimiento");
        } else {
            titulo =  "Activar";
            descripcion = "Se activará el seguimiento para este oficio. Favor de proporcionar un motivo de activación de seguimiento. ¿Desea continuar?";
            botonAceptar.setTitulo("Confirmar activación de seguimiento");
            botonCancelar.setTitulo("Cancelar activación de seguimiento");
            
        }
        
    }
    
    

    @Override
    protected boolean permisosRequeridos() {
        return getPermisos().isGestionarSeguimientoOficio();
    }
    
    
    
    /**
     * Gestiona el indicador de seguimiento de un oficio.
     * 
     * Si se encuentra desactivado lo activa, y viceversa.
     * 
     * @param actionEvent 
     */
    //public String gestionarSeguimiento(ActionEvent actionEvent) {
    public String gestionarSeguimiento() {
        
        String resultado;
        
        try {

            if (getVo().isRequiereSeguimiento()) {

                getOficioServicioRemoto().desactivarSeguimiento(getVo(), motivo, getUsuarioId());
                
                mostrarMensaje(FacesMessage.SEVERITY_INFO,
                        "Se desactivó exitosamente el seguimiento para el oficio " + getVo().getOficioNumero(), 
                        null);
                
                resultado = Constantes.OFICIOS_VISTA_DETALLE;

            } else {

                getOficioServicioRemoto().activarSeguimiento(getVo(), motivo, getUsuarioId());
                
                mostrarMensaje(FacesMessage.SEVERITY_INFO,
                        "Se activó exitosamente el seguimiento para el oficio " + getVo().getOficioNumero(), 
                        null);
                
                resultado = Constantes.OFICIOS_VISTA_DETALLE;

            }

            //PRUEBA:
        //} catch (MissingRequiredValuesException ex) {
        } catch (Exception ex) {
            
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
            
            // permanecer en la pantalla actual
            resultado = Constantes.VACIO;
            
        } finally {

            desbloquearPantalla();
        }
        
        return resultado;
        
    }
    
    
    
    // <editor-fold defaultstate="collapsed" desc="Getters y setters">
    

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }
    
    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public AccionUI getBotonAceptar() {
        return botonAceptar;
    }

    public AccionUI getBotonCancelar() {
        return botonCancelar;
    }
    
    // </editor-fold>
    
    
    
}
