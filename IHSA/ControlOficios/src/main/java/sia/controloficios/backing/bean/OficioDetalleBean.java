
package sia.controloficios.backing.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;
import sia.constantes.Constantes;
import sia.controloficios.sistema.soporte.FacesUtils;
import sia.excepciones.InsufficientPermissionsException;
import sia.modelo.oficio.vo.AdjuntoOficioVo;
import sia.modelo.oficio.vo.MovimientoVo;
import sia.modelo.oficio.vo.OficioPromovibleVo;
import sia.modelo.oficio.vo.OficioSalidaVo;
import sia.util.ui.AccionUI;

/**
 * Bean para la pantalla de Detalle de Oficios.
 *
 * @author esapien
 */
@ManagedBean
public class OficioDetalleBean extends OficioBaseBean {
    
    private String tituloPagina;
    
    /**
     * Botones de la vista
     * 
     */
    private AccionUI botonPromover;
    private AccionUI botonModificar;
    private AccionUI botonAnular;
    private AccionUI botonCancelar;
    private AccionUI linkEditarAdjunto;
    private AccionUI botonSeguimiento;
    
    /**
     * Auxiliar para uso en la vista.
     * 
     */
    private boolean modoEdicion;
    
    /**
     * Variable para el archivo adjunto a mostrar en la pantalla de detalle
     * en el visor de archivos.
     */
    private AdjuntoOficioVo adjuntoVisor;
    
    
    /**
     * Prepara la vista de Detalle de Oficio. Se muestran las opciones en base
     * a modalidad:
     * 
     * - Modalidad de Edición: Solo para roles de edición. Se muestran las opciones
     * para editar un oficio: promover, modificar y anular.
     * - Modalidad de Consulta: No se muestran opciones para modificar a un oficio.
     * 
     * La modalidad es establecida desde la pantalla de origen: Bandeja de Entrada 
     * o pantalla de consulta.
     * 
     * @throws InsufficientPermissionsException En caso que entrar en modo de 
     * edición sin tener un rol de edición.
     */
    @Override
    protected void postConstruct() throws InsufficientPermissionsException {
        
        // valor inicial para la vista
        
        // obtener registro de oficio
        
        // se debe recibir un ID
        Integer oficioId = Integer.parseInt(FacesUtils.getRequestParameter("oficioId"));
        
        // obtener desde bd
        this.setVo(buscarOficioVo(oficioId));
        
        // configurar link de detalle de oficio asociado en pantalla
        //configurarLinkDetalle();
        
        // configurar modalidad de pantalla (edicion o consulta)
        
        modoEdicion = getSesion().isModoEdicion();
        
        LOGGER.info(this, "oficioId = " + oficioId + ", modoEdicion = " + modoEdicion);
        
        // si se consulta detalle de oficio para para promover, validar permisos
        if (modoEdicion) {
            
            // Modo de Edición
            
            // verificar permisos correctos
            if (getVo().isEstatusCreado() && (!getPermisos().isRolEdicionOficios() 
                    || !getPermisos().puedeEditar(getVo()))) {
                throw new InsufficientPermissionsException();
            }
            
            tituloPagina = "Gestionar Oficio de " + getVo().getTipoOficioNombre();

            OficioPromovibleVo vo = getVo();

            // configurar y mostrar botón Promover
            botonPromover = new AccionUI();

            if (vo.isEstatusTerminado()) {
                botonPromover.setVisible(false);
            } else {
                botonPromover.setValor(vo.isEstatusPorTerminar() ? "Terminar" : "Promover");
                //botonPromover.setEstiloClase("cancelar");
                botonPromover.setTitulo("Promover a " + vo.getSiguienteEstatusNombre());
                botonPromover.setAccion(Constantes.OFICIOS_VISTA_PROMOCION_ESTATUS);
                botonPromover.setVisible(true);
            }
            
            // configurar botón de Modificar
            botonModificar = new AccionUI();
            
            // el oficio solo se podrá modificar en estado inicial o 
            // en caso el usuario sea Editor Maestro
            boolean visible = 
                    (vo.isEstatusCreado()
                    && getPermisos().isModificarOficio()
                    && getUsuarioId().equals(vo.getGenero())) 
                    || getPermisos().isRolEditorMaestro();
            
            botonModificar.setVisible(visible);
            
            
            // configurar botón de Anular
            botonAnular = new AccionUI();
            
            visible = 
                    getPermisos().isAnularOficio() 
                    && !vo.isEstatusAnulado() 
                    && !vo.isEstatusTerminado(); 
                    //&& !vo.isAsociado();
            
            botonAnular.setVisible(visible);
            
            // configurar link para modificar archivo adjunto de historial
            
            linkEditarAdjunto = new AccionUI();
            linkEditarAdjunto.setVisible(mostrarEdicionArchivos());
            
            // botón de Cancelar

            // si es en modo Edición, la pagina de origen es la bandeja de entrada
            // de lo contrario, es la pantalla de consulta

            botonCancelar = new AccionUI();

            botonCancelar.setValor("Cancelar");
            //botonCancelar.setEstiloClase("cancelar");
            botonCancelar.setAccion(Constantes.OFICIOS_VISTA_BANDEJA_ENTRADA);
            botonCancelar.setTitulo("Ir a bandeja de entrada");
            botonCancelar.setVisible(true);
            
            // el botón de Seguimiento solo se muestra desde la pantalla de Consulta
            botonSeguimiento = new AccionUI();
            botonSeguimiento.setVisible(false);
        

        } else {
            
            // Modo de Consulta
            
            tituloPagina = "Detalle de Oficio de " + getVo().getTipoOficioNombre();
            
            // por defecto se omiten los botones de edición
            
            botonPromover = new AccionUI();
            botonPromover.setVisible(false);
            
            botonModificar = new AccionUI();
            botonModificar.setVisible(getPermisos().isRolEditorMaestro());
            
            botonAnular = new AccionUI();
            botonAnular.setVisible(false);
            
            linkEditarAdjunto = new AccionUI();
            linkEditarAdjunto.setVisible(puedeEditarAdjunto());
            
            // botón Cancelar

            botonCancelar = new AccionUI();

            botonCancelar.setValor("Cancelar");
            //botonCancelar.setEstiloClase("cancelar");
            botonCancelar.setAccion(Constantes.OFICIOS_VISTA_CONSULTAR);
            botonCancelar.setTitulo("Ir a consulta de oficios");
            botonCancelar.setVisible(true);
            
            botonSeguimiento = new AccionUI();
            
            if (getPermisos().isGestionarSeguimientoOficio()) {
                
                if (getVo().isRequiereSeguimiento()) {

                    botonSeguimiento.setValor("Desactivar Seguimiento");
                    botonSeguimiento.setTitulo("Desactivar seguimiento para este oficio");

                } else {

                    botonSeguimiento.setValor("Activar Seguimiento");
                    botonSeguimiento.setTitulo("Activar seguimiento para este oficio");
                }
                
                botonSeguimiento.setAccion(Constantes.OFICIOS_VISTA_SEGUIMIENTO);
                botonSeguimiento.setVisible(true);
                
            } else {
                
                botonSeguimiento.setVisible(false);
            }
            
        }
        
    }
    
    
    
    /**
     * Establece si se deberá mostrar el link para acceso a detalle del oficio
     * desde la pantalla de Detalle, en función de los permisos y acceso del 
     * usuario ingresado.
     * 
     */
    /*private void configurarLinkDetalle() {
        
        for (OficioVo vo : getVo().getOficiosAsociados()) {
            
            // no mostrar link para oficio actual
            if (vo.getOficioNumero().equals(getVo().getOficioNumero())) {
                vo.setMostrarLinkDetalle(false);
            } else {
                // mostrar link de detalle de oficio si se tiene acceso a todas 
                // las gerencias
                
                if (getPermisos().isVerTodoGerencias()) {
                    vo.setMostrarLinkDetalle(true);
                } else {
                    
                    // si el usuario es editor, mostrar link si el oficio es de 
                    // un bloque a los que el usuario tiene acceso
                    if (getPermisos().isRolEdicionOficios()) {
                        
                        if (bloqueExiste(vo.getBloqueId(), getSesion().getBloquesUsuario())) {
                            vo.setMostrarLinkDetalle(true);
                        } else {
                            vo.setMostrarLinkDetalle(false);
                        }
                        
                    } else {
                        // usuario no editor (ej. consultor)
                        // mostrar link solo si el oficio es del mismo bloque
                        // y gerencia que el usuario
                        
                        int bloqueId = getUsuario().getApCampo().getId();
                        int gerenciaId = getUsuario().getGerencia().getId();
                        
                        if (vo.getBloqueId() == bloqueId && vo.getGerenciaId() == gerenciaId) {
                            vo.setMostrarLinkDetalle(true);
                        } else {
                            vo.setMostrarLinkDetalle(false);
                        }
                    }
                }
            }
        }
    }*/
    
    
    
    /**
     * Indica si el bloque proporcionado existe en los bloques a los que 
     * el usuario tiene acceso.
     * 
     * @param bloqueId
     * @param bloquesUsuario
     * @return 
     */
    /*private boolean bloqueExiste(int bloqueId, List<CompaniaBloqueGerenciaVo> bloquesUsuario) {
        
        boolean result = false;
        
        for (CompaniaBloqueGerenciaVo vo : bloquesUsuario) {
            if (vo.getBloqueId() == bloqueId) {
                result = true;
                break;
            }
        }
        return result;
    }*/
    
    
    /**
     * 
     * @return 
     */
    @Override
    public OficioPromovibleVo getVo() {
        
        return (OficioPromovibleVo) super.getVo();
        
    }

    @Override
    protected boolean permisosRequeridos() {
        
        // TODO: Agregar bloque asociado a validacion
        
        return getPermisos().isVerDetalleOficio();
    }
    
    public AccionUI getBotonPromover() {
        return botonPromover;
    }

    public AccionUI getBotonAnular() {
        return botonAnular;
    }

    public AccionUI getBotonModificar() {
        return botonModificar;
    }

    public AccionUI getLinkEditarAdjunto() {
        return linkEditarAdjunto;
    }
    
    public AccionUI getBotonCancelar() {
        return botonCancelar;
    }

    public AccionUI getBotonSeguimiento() {
        return botonSeguimiento;
    }
    
    
    
    public AdjuntoOficioVo getAdjuntoVisor() {
        return adjuntoVisor;
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }
    
    
    
    /**
     * Para determinar si mostrar la columna para la edición de los archivos
     * adjuntos en la tabla de historial de movimientos.
     * 
     * @return 
     */
    public boolean mostrarEdicionArchivos() {
        
        boolean resultado;
        
        
        if (getPermisos().isRolEditorMaestro()) {
            
            resultado = true;
            
        } else {
            
            // solo se permite modificar el archivo adjunto desde modo Edición 
            // (Bandeja de Entrada)
            if (modoEdicion) {

                if (getVo().isEstatusCreado()) {

                    boolean mismoAutor = getUsuarioId().equals(getVo().getGenero());
                    boolean tienePermiso = getPermisos().isModificarAdjuntoHistorialOficio();

                    resultado = mismoAutor && tienePermiso;

                } else {

                    resultado = puedeEditarAdjunto();

                }
            } else {
                // modo consulta

                resultado = puedeEditarAdjunto();

            }
        }
        
        return resultado;
        
    }

    /**
     * 
     * @return 
     */
    private boolean puedeEditarAdjunto() {
        
        boolean resultado;
        
        if (getPermisos().isRolEditorMaestro()) {
            
            resultado = true;
            
        } else {

            // solo se permite modificar si el oficio es de salida y tiene 
            // rol de Editor de Adjunto de Salida
            boolean isSalida = getVo() instanceof OficioSalidaVo;
            boolean isEditable =
                    !getVo().isEstatusAnulado()
                    && !getVo().isEstatusTerminado();
            boolean tienePermiso = getPermisos().isModificarAdjuntoSalida();

            resultado = isSalida && isEditable && tienePermiso;
        }

        return resultado;
    }
    
    
    
    /**
     * Invocado previo a mostrar un archivo adjunto de movimiento en el visor
     * de archivos.
     * 
     * @param actionEvent 
     */
    public void prepararAdjuntoVisor(ActionEvent actionEvent) {
        
        int adjuntoId = Integer.parseInt(FacesUtils.getRequestParameter("adjuntoId"));
        
        LOGGER.info(this, "@prepararAdjuntoVisor - adjuntoId = " + adjuntoId);
        
        for (MovimientoVo movimiento : this.getVo().getMovimientos()) {
            
            if (movimiento.getAdjunto().getId() != null
                    && adjuntoId == movimiento.getAdjunto().getId()) {
                this.adjuntoVisor = movimiento.getAdjunto();
                this.adjuntoVisor.setDescripcion(movimiento.getOperacion());
                break;
            }
        }
        
        LOGGER.info(this, "adjunto visor = " + this.adjuntoVisor.getNombre());
        
    }

    /**
     * 
     * @return 
     */
    public String getTituloPagina() {
        return tituloPagina;
    }

    
}
