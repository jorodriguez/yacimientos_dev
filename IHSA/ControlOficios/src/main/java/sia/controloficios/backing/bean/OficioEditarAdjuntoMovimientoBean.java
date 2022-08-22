

package sia.controloficios.backing.bean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;
import javax.mail.MessagingException;
//import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.primefaces.event.FilesUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;

import sia.constantes.Constantes;
import sia.controloficios.sistema.soporte.FacesUtils;
import sia.excepciones.InsufficientPermissionsException;
import sia.excepciones.MissingRequiredValuesException;
import sia.excepciones.SIAException;
import sia.modelo.oficio.vo.AdjuntoOficioVo;
import sia.modelo.oficio.vo.MovimientoVo;
import sia.modelo.oficio.vo.OficioPromovibleVo;
import sia.modelo.oficio.vo.OficioSalidaVo;

/**
 * Managed bean para la pantalla de Sustituir Archivo Adjunto de Movimiento.
 * 
 * @author esapien
 */
@ManagedBean
public class OficioEditarAdjuntoMovimientoBean extends OficioBaseBean {
    
    /**
     * Movimiento al cual pertenece el archivo
     * 
     */
    private MovimientoVo movimiento;
    
    
    /**
     * ID del archivo adjunto a remplazar.
     */
    private Integer archivoAdjuntoId;
    
    /**
     * Motivo de sustitución de archivo adjunto.
     */
    private String motivo;

    /**
     * 
     */
    @Override
    protected void postConstruct() throws InsufficientPermissionsException {
        
        // obtener registro de oficio
        
        int oficioId = Integer.parseInt(FacesUtils.getRequestParameter("oficioId"));
        int movimientoId = Integer.parseInt(FacesUtils.getRequestParameter("movimientoId"));
        int oficioMovimientoId = Integer.parseInt(FacesUtils.getRequestParameter("oficioMovimientoId"));
        //int adjuntoId = Integer.parseInt(FacesUtils.getRequestParameter("adjuntoId"));
        
        getLogger().info(this,
                "oficioId = " + oficioId + ", movimientoId = " + movimientoId + ", oficioMovimientoId = " + oficioMovimientoId);
        
        // obtener informacion de oficio
        setVo(buscarOficioVo(oficioId));
        
        // obtener el movimiento
        
        MovimientoVo movimientoSeleccionado = getVo().getMovimiento(movimientoId);
        
        setMovimiento(movimientoSeleccionado);
        
        // id del archivo a sustituir
        
        archivoAdjuntoId = movimientoSeleccionado.getAdjunto().getId();
        
        getLogger().info(this, "adjunto de mov = " + movimientoSeleccionado.getAdjunto());
        
    }
    
    /**
     * Realiza la sustitución del archivo adjunto correspondiente en la 
     * base de datos.
     * 
     * @param actionEvent 
     * *///jevazquez 18/02/15
    public String editarAdjunto(ActionEvent actionEvent) throws MessagingException {
        
        String resultado;
        
        getLogger().info(this, "@editarAdjunto");
        
        try {
            
            AdjuntoOficioVo adjunto = getMovimiento().getAdjunto();
            
            // restaurar ID del registro del archivo a actualizar
            adjunto.setId(archivoAdjuntoId);
            
            getLogger().info(this, "Archivo a guardar: " + adjunto);
            
            // validar que se cambió el archivo
            
            if (adjunto.isArchivoGuardado()) {

                getOficioServicioRemoto().actualizarAdjunto(getVo(), getMovimiento(), motivo, getUsuarioId());
                
                // restaurar bandera para nuevo ciclo de cambio
                adjunto.setArchivoGuardado(false);

                mostrarMensaje(FacesMessage.SEVERITY_INFO,
                        "El archivo adjunto se sustituyó correctamente.", null);
                
                resultado = Constantes.OFICIOS_VISTA_DETALLE;
                
            } else {

                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "Proporcione un nuevo archivo adjunto.", null);
                
                // permanecer en la pantalla actual
                resultado = Constantes.VACIO;
            }
            
        } catch (MissingRequiredValuesException ex) {
            
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                    ex.getMessage(), null);
            
            // permanecer en la pantalla actual
            resultado = Constantes.VACIO;
            
        } catch (SIAException ex) {
            
            mostrarMensaje(FacesMessage.SEVERITY_FATAL, 
                    "Ocurrió un error en el proceso de sustitución del archivo: " 
                    + ex.getMessage(), null);
            
            // permanecer en la pantalla actual
            resultado = Constantes.VACIO;
            
        } finally {
            
            // desbloquear al terminar el proceso
            desbloquearPantalla();
            
        }
        
        return resultado;
        
    }

    /**
     * 
     * @return 
     */
    @Override
    protected boolean permisosRequeridos() throws InsufficientPermissionsException {
        
        boolean resultado;
        
        if (getPermisos().isRolEditorMaestro()) {
            
            resultado = true;
            
        } else  {

            int oficioId = Integer.parseInt(FacesUtils.getRequestParameter("oficioId"));

            OficioPromovibleVo vo = buscarOficioVo(oficioId);

            boolean isAdjuntoEditable = !vo.isEstatusAnulado() && !vo.isEstatusTerminado();

            if (isAdjuntoEditable) {

                boolean permisoEdicion = getPermisos().isModificarOficio()
                        && getPermisos().isModificarAdjuntoHistorialOficio();

                boolean permisoConsulta = getPermisos().isModificarAdjuntoSalida()
                        && vo instanceof OficioSalidaVo;

                resultado = permisoEdicion || permisoConsulta;

            } else {
                resultado = false;
            }
            
        }
        
        return resultado;
    }
    
    
    
    
    /**
     * Validación para mostrar botón de Remover Archivo Adjunto en la vista.
     * 
     * @return 
     */
    public boolean isArchivoAdjuntoSeleccionado() {
        
        AdjuntoOficioVo archivoAdjunto = getMovimiento().getAdjunto();
        
        return archivoAdjunto != null 
                && archivoAdjunto.getNombre() != null 
                && archivoAdjunto.getNombre().trim().length() > 0;
        
    }
    
    
    
    /**
     * Remueve el archivo adjunto existente seleccionado.
     * 
     * @param actionEvent 
     */
    public void removerArchivoAdjunto(ActionEvent actionEvent) {
        
        this.getMovimiento().setAdjunto(new AdjuntoOficioVo());
        
        //TODO : remover el adjunto en el repositorio de documentos?
        // en un gestor de contenido se podría poner una etiqueta o algún atributo
        // para indicar que ya no está activo, pero en el sistema de archivos no
        // habría manera de implementar esto
    }
    
    
    
    /**
     * Prepara el VO de archivo adjunto de un oficio con la información 
     * de un archivo guardado en disco.
     * 
     * @param e 
     */
    public void prepararArchivoAdjuntoVo(FilesUploadEvent e) {
        
        prepararArchivoAdjuntoVo(e, getMovimiento().getAdjunto());
        
    }
    
    
    
    
    
    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">
    

    public MovimientoVo getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoVo movimiento) {
        this.movimiento = movimiento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    
    // </editor-fold>
    
    
    
}
