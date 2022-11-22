
package sia.controloficios.backing.bean;

import javax.faces.application.FacesMessage;
import javax.inject.Named;
import sia.excepciones.InsufficientPermissionsException;
import sia.excepciones.InvalidBusinessOperationException;
import sia.excepciones.MissingRequiredValuesException;
import sia.modelo.oficio.vo.OficioPromovibleVo;
import static sia.constantes.Constantes.*;
        
/**
 * Bean correspondiente a la pantalla de Anulación de Oficios.
 *
 * @author esapien
 */
//@ManagedBean
@Named(value = "oficioAnularBean")
public class OficioAnularBean extends OficioBaseBean {
    
    /**
     * Atributos de UI
     */
    
    /**
     * Motivo de anulación
     */
    private String motivo;

    /**
     * 
     * @throws SIAException 
     */
    @Override
    protected void postConstruct() throws InsufficientPermissionsException {
        System.out.println("@postConstruct - OficioAnular ");
        // obtener registro de oficio
        
        //int oficioId = Integer.parseInt(FacesUtils.getRequestParameter("oficioId"));
        
        int oficioId = getContextParamOficioId();
        
        System.out.println("oficioIdContext "+oficioId);
        
        setVo(buscarOficioVo(oficioId));
        
    }
    
    

    /**
     * 
     * @return 
     */
    @Override
    protected boolean permisosRequeridos() {
        return getPermisos().isAnularOficio();
    }
    
    
    
    
    /**
     * Realiza la anulación de un registro de oficio.
     * 
     * @param actionEvent 
     */
    //public String anularOficio(ActionEvent actionEvent) {
    public String anularOficio() {
        
        String resultado;

        try {
            
            getLogger().info(this, "@anularOficio = " + getVo().toString());

            String idUsuario = getSesion().getUsuario().getId();
            
            getOficioServicioRemoto().anularOficio((OficioPromovibleVo)getVo(), getMotivo(), idUsuario);           
                       
            mostrarMensaje(FacesMessage.SEVERITY_INFO, 
                    "El oficio " + getVo().getOficioNumero() + " fue anulado exitosamente.", null);

            //resultado = "bandejaEntrada.xhtml?faces-redirect=true;";
            resultado = OFICIOS_VISTA_BANDEJA_ENTRADA;

        } catch (InvalidBusinessOperationException ex) {

            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El oficio no puede ser anulado debido a que se encuentra en una asociación.", null);
            
            resultado = "";
            //resultado = OFICIOS_VISTA_ANULAR;            
            
        } catch (MissingRequiredValuesException ex) {

            //mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Proporcione los siguientes campos obligatorios: " + ex.getValoresFaltantes(), null);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Proporcione un motivo de anulación.", null);
            
            resultado = "";
            //resultado = OFICIOS_VISTA_ANULAR;            

        } finally {
            
            desbloquearPantalla();
        }
        
        return resultado;
        
        
    }
    
    
    public String goToDetalleOficio(){
        System.out.println("@ToDetalleOficio");
        
        setContextParamOficioId(this.getVo().getId());       
        
        //return "detalle.xhtml?faces-redirect=true;";
        return OFICIOS_VISTA_DETALLE;
    }
    

    /**
     * 
     * @return 
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * 
     * @param motivo 
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    
    
    
}
