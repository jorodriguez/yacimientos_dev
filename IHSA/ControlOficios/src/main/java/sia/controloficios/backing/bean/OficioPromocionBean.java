

package sia.controloficios.backing.bean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;
import javax.mail.MessagingException;
//import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.primefaces.event.FilesUploadEvent;
import sia.constantes.Constantes;
import sia.controloficios.sistema.soporte.FacesUtils;
import sia.excepciones.InvalidStateException;
import sia.excepciones.MissingRequiredValuesException;
import sia.excepciones.PromotionFailedException;
import sia.excepciones.SIAException;
import sia.modelo.oficio.vo.AdjuntoOficioVo;
import sia.modelo.oficio.vo.OficioPromovibleVo;

/**
 * Bean para la pantalla de Promoción de Estatus de oficio.
 *
 * @author esapien
 */
@ManagedBean
public class OficioPromocionBean extends OficioBaseBean {
    
    
    /**
     * PostConstruct
     * 
     * @throws SIAException 
     */
    @Override
    protected void postConstruct() throws SIAException {
        
        getLogger().info(this, "@postConstruct");
        
        // obtener registro de oficio
        
        int oficioId = Integer.parseInt(FacesUtils.getRequestParameter("oficioId"));
                
        setVo(buscarOficioVo(oficioId));
        
        getLogger().info(this, "Estatus actual = " + getVo().getEstatusId());
        
    }
    
    

    /**
     * 
     * @return 
     */
    @Override
    protected boolean permisosRequeridos() {
        
        return getPermisos().isPromoverEstatusOficio();
        
    }
    
    
    /**
     * 
     * @return 
     */
    @Override
    public OficioPromovibleVo getVo() {
        
        return (OficioPromovibleVo)super.getVo();
        
    }
    

    /**
     * Para indicar si se debe solicitar un archivo adjunto en el proceso de 
     * promoción de estatus.
     * 
     * @return 
     */
    public boolean requiereArchivoPromocion() {
        
        OficioPromovibleVo vo = getVo();
        
        return vo.requiereArchivoAdjuntoPromocion();
        
    }
    
    
    /**
     * Validación para mostrar botón de Remover Archivo para promover estatus en la vista.
     * 
     * @return 
     */
    public boolean isArchivoPromocionSeleccionado() {
        
        AdjuntoOficioVo archivo = getVo().getArchivoPromocion();
        
        return archivo != null && archivo.getNombre() != null && archivo.getNombre().trim().length() > 0;
        
    }
    
    /**
     * 
     * @return 
     */
    public boolean porTerminar() {
        return getVo().isEstatusPorTerminar();
    }
    
    
    
    
    /**
     * Remueve el archivo para promover estatus seleccionado.
     * 
     * @param actionEvent 
     */
    public void removerArchivoPromocion(ActionEvent actionEvent) {
        
        getVo().setArchivoPromocion(new AdjuntoOficioVo());
        
    }
    
    
    /**
     * 
     * @param actionEvent 
     */
    public String promoverEstatusOficio(ActionEvent actionEvent) {
        
        getLogger().info(this, "@promoverEstatusOficio = " + getVo().toString());
        
        String resultado = Constantes.OFICIOS_VISTA_PROMOCION_ESTATUS;
        
        try {
            
            getOficioServicioRemoto().promoverEstatusOficio(getVo(), getUsuario());
            
            mostrarMensaje(
                    FacesMessage.SEVERITY_INFO,
                    "El oficio " + getVo().getOficioNumero() + " fue promovido exitosamente.",
                    null
            );

            resultado = Constantes.OFICIOS_VISTA_BANDEJA_ENTRADA;

        } catch (MissingRequiredValuesException ex) {
            
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                    "Proporcione los siguientes campos obligatorios: " 
                    + ex.getValoresFaltantes(), null);
            
            
        } catch (InvalidStateException ex) {
            
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                    "El registro del oficio ha cambiado en la base de datos. "
                    + "Favor de reiniciar el proceso para obtener la versión "
                    + "actual del registro e intentar de nuevo.", null);
            
            
        } catch (PromotionFailedException ex) {
            
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                    "Ocurrió un error en el proceso de promoción del oficio: " 
                    + ex.getMessage(), null);
            
            
        } catch (MessagingException ex) {
            
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                    "Ocurrió un error en el proceso de promoción del oficio: " 
                    + ex.getMessage(), null);
            
            
        } catch (SIAException ex) {
            
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                    "Ocurrió un error en el proceso de promoción del oficio: " 
                    + ex.getMessage(), null);
            
            
        } finally {
            
            // desbloquear al terminar el proceso
            desbloquearPantalla();
            
        }
        
        return resultado;
        
    }
    
    
    /**
     * Prepara el VO de archivo adjunto de promoción de un oficio con la 
     * información de un archivo guardado en disco.
     * 
     * @param e 
     */
    public void prepararArchivoPromocionVo(FilesUploadEvent e) {
        
        prepararArchivoAdjuntoVo(e, getVo().getArchivoPromocion());
        
    }
    
}
