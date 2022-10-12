

package sia.modelo.oficio.vo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import sia.constantes.Constantes;
import sia.excepciones.InvalidPermissionsException;
import sia.modelo.permiso.vo.PermisoVo;
import sia.modelo.rol.vo.RolVO;
import sia.util.UtilLog4j;

/**
 * Define todos los posibles roles y permisos requeridos y válidos para un 
 * usuario en el módulo de Control de Oficios, así como operaciones y reglas 
 * de negocio relacionadas.
 *
 * @author esapien
 */
public class PermisosVo {
    
    /*
     * Roles de edición
     */
    private final boolean rolEmisorOficiosEntrada;
    private final boolean rolEmisorOficiosSalida;
    private final boolean rolReceptorReynosa;
    private final boolean rolReceptorMonterrey;
    private final boolean rolEditorOficiosSalida;
    private final boolean rolEditorAdjuntoSalida;
    
    /*
     * Editor que puede modificar los oficios de cualquier tipo y 
     * en cualquier estatus
     */
    private final boolean rolEditorMaestro;
    
    /* 
     * Permisos básicos (obligatorios)
     */
    private final boolean ingresarModuloOficios;
    private final boolean consultarOficio;
    private final boolean verDetalleOficio;
    private final boolean verHistorialOficio;
    
    
    /**
     * Permisos de edición (restringidos)
     * 
     * Solo para roles Emisores y Receptores
     * 
     */
    private final boolean altaOficio;
    private final boolean modificarOficio;
    private final boolean anularOficio;
    private final boolean promoverEstatusOficio;
    private final boolean modificarAdjuntoHistorialOficio;
    
    /**
     * Permisos opcionales
     */
    
    // El permiso Ver Todas las Gerencias es para acceder a todas las compañías,
    // bloques y gerencias.
    private final boolean verTodoGerencias;
    private final boolean verArchivoAdjuntoHistorialOficio;
    private final boolean descargarArchivoAdjuntoHistorialOficio;
    private final boolean recibirCorreoAltaOficio;
    private final boolean gestionarSeguimientoOficio;
    private final boolean modificarOficioSalida;
    private final boolean modificarAdjuntoSalida;

    //permiso para crear copia en otro bloque
    private final boolean copiarEnOtroBloque;
    /**
     * 
     * 
     * @param roles
     * @param opciones 
     */
    public PermisosVo(List<RolVO> roles) throws InvalidPermissionsException {
        
        // guardar sets de roles y permisos
        
        Set<String> setRoles = new HashSet();
        Set<String> setPermisos = new HashSet();
        
        for (RolVO rol : roles) {
            
            setRoles.add(rol.getCodigo());
            
            List<PermisoVo> permisos = rol.getPermisos();
            
            for (PermisoVo permiso : permisos) {
                setPermisos.add(permiso.getNombre());
            }
        }
        
        // configurar roles
        
        // Rol Editor Maestro, tiene permiso abierto a modificar los registros de oficios 
        // de cualquier tipo y en cualquier estatus, así como sus archivos adjuntos
        this.rolEditorMaestro = setRoles.contains(Constantes.OFICIOS_ROL_EDITOR_MAESTRO_CODIGO);
        
        // los roles de emisión de oficios son exclusivos
        this.rolEmisorOficiosEntrada = setRoles.contains(Constantes.OFICIOS_ROL_EMISOR_OFICIOS_ENTRADA_CODIGO);
        this.rolEmisorOficiosSalida = setRoles.contains(Constantes.OFICIOS_ROL_EMISOR_OFICIOS_SALIDA_CODIGO);
        
        // los roles receptores de oficios son exclusivos
        this.rolReceptorReynosa = setRoles.contains(Constantes.OFICIOS_ROL_RECEPTOR_REYNOSA_CODIGO);
        this.rolReceptorMonterrey = setRoles.contains(Constantes.OFICIOS_ROL_RECEPTOR_MONTERREY_CODIGO);
        
        // roles para modificar oficios en proceso
        this.rolEditorOficiosSalida = setRoles.contains(Constantes.OFICIOS_ROL_EDITOR_OFICIOS_SALIDA_CODIGO);
        this.rolEditorAdjuntoSalida = setRoles.contains(Constantes.OFICIOS_ROL_EDITOR_ADJUNTO_SALIDA_CODIGO);
        
        // permisos básicos obligatorios
        this.ingresarModuloOficios = setPermisos.contains(Constantes.OFICIOS_PERMISO_INGRESAR_MODULO_OFICIO);
        this.verHistorialOficio = setPermisos.contains(Constantes.OFICIOS_PERMISO_VER_HISTORIAL_OFICIO);
        this.consultarOficio = setPermisos.contains(Constantes.OFICIOS_PERMISO_CONSULTAR_OFICIO);
        this.verDetalleOficio = setPermisos.contains(Constantes.OFICIOS_PERMISO_VER_DETALLE_OFICIO);
        
        // permisos de edición
        this.altaOficio = setPermisos.contains(Constantes.OFICIOS_PERMISO_ALTA_OFICIO);
        this.modificarOficio = setPermisos.contains(Constantes.OFICIOS_PERMISO_MODIFICAR_OFICIO);
        this.anularOficio = setPermisos.contains(Constantes.OFICIOS_PERMISO_ANULAR_OFICIO);
        this.promoverEstatusOficio = setPermisos.contains(Constantes.OFICIOS_PERMISO_PROMOVER_ESTATUS_OFICIO);
        this.modificarAdjuntoHistorialOficio = setPermisos.contains(Constantes.OFICIOS_PERMISO_MODIFICAR_ARCHIVOADJUNTO_HISTORIAL_OFICIO);
        this.modificarOficioSalida = setPermisos.contains(Constantes.OFICIOS_PERMISO_MODIFICAR_OFICIO_SALIDA);
        this.modificarAdjuntoSalida = setPermisos.contains(Constantes.OFICIOS_PERMISO_MODIFICAR_ADJUNTO_SALIDA);
        
        // permisos opcionales
        this.verArchivoAdjuntoHistorialOficio = setPermisos.contains(Constantes.OFICIOS_PERMISO_VER_ARCHIVOADJUNTO_HISTORIAL_OFICIO);
        this.descargarArchivoAdjuntoHistorialOficio = setPermisos.contains(Constantes.OFICIOS_PERMISO_DESCARGAR_ARCHIVOADJUNTO_HISTORIAL_OFICIO);
        this.verTodoGerencias = setPermisos.contains(Constantes.OFICIOS_PERMISO_VER_TODO_GERENCIAS);
        this.recibirCorreoAltaOficio = setPermisos.contains(Constantes.OFICIOS_PERMISO_RECIBIR_CORREO_ALTA_OFICIO);
        this.gestionarSeguimientoOficio = setPermisos.contains(Constantes.OFICIOS_PERMISO_GESTIONAR_SEGUIMIENTO_OFICIO);
        
        //Permiso para crear copia en otro bloque de la misma compañia
        this.copiarEnOtroBloque = setPermisos.contains(Constantes.OFICIO_PERMISO_COPIAR_EN_OTRO_BLOQUE);
        
        // validar configuración correcta para ingresar al sistema así como posibles conflictos de permisos
        validarPermisos();
        
        
    }
    
    /**
     * Indica si en base a estos permisos puede ver para editar el oficio 
     * indicado dependiendo de su tipo y estatus.
     * 
     * @param oficioVo
     * @return 
     */
    public boolean puedeEditar(OficioPromovibleVo vo) {
        
        boolean resultado = false;
        
        getLogger().info(this, 
                "@puedeEditar - vo {"
                + "num ofic = '" + vo.getOficioNumero() + "', "
                + "tipo = '" + vo.getTipoOficioId() + "', "
                + "estatusId = '" + vo.getEstatusId() + "'}");
        
        
        // validar por tipo de oficio
        if (vo instanceof OficioSalidaVo) {
            
            // roles que pueden ver cada estatus aplicables de este tipo
            switch(vo.getEstatusId()) {
                
                
                case Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO:
                    
                    resultado = rolEmisorOficiosSalida;
                    break;
                    
                    
                case Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA:
                    
                    resultado = rolReceptorReynosa;
                    break;
                    
                case Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX:
                    
                    resultado = rolReceptorMonterrey;
                    break;
                    
                case Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO:
                    // estatus no editable
                    
                    resultado = false;
                    break;
                    
                case Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO:
                    // estatus no editable
                    
                    resultado = false;
                    break;
                    
                default:
                    // estatus desconocido/no válido para este tipo de oficio
                    throw new UnsupportedOperationException("Not supported yet.");
                    
                    
            }
        } else if (vo instanceof OficioEntradaVo) {
            
            // roles que pueden ver cada estatus de este tipo
            switch(vo.getEstatusId()) {
                
                case Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO:
                    
                    resultado = rolEmisorOficiosEntrada;
                    break;
                    
                case Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY:
                    
                    resultado = rolReceptorMonterrey;
                    break;
                    
                case Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO:
                    
                    // estatus no editable
                    resultado = false;
                    break;
                    
                case Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO:
                    
                    // estatus no editable
                    resultado = false;
                    break;
                    
                default:
                    // estatus desconocido/no válido para este tipo de oficio
                    throw new UnsupportedOperationException("Not supported yet.");
                    
            }
            
        } else {
            throw new UnsupportedOperationException("Tipo de oficio no soportado.");
        }
        
        
        return resultado;
        
    }
    
    
    /**
     * Valida la correcta configuración de los permisos para un usuario del 
     * módulo de Control de Oficios. Se verifica lo siguiente: 
     * 
     * - Permisos mínimos para utilizar el sistema
     * - Posibles conflictos entre permisos
     * 
     */
    private void validarPermisos() throws InvalidPermissionsException {
        
        StringBuilder sb = new StringBuilder();
        
        // todo usuario debe tener todos los siguientes permisos mínimos:
        
        if (!(ingresarModuloOficios && consultarOficio && verDetalleOficio && verHistorialOficio)) {
            sb.append("El usuario no tiene los permisos mínimos para utilizar el sistema.\n");
        }
        
        // validar posibles conflictos entre roles exclusivos
        
        if (this.rolEmisorOficiosEntrada && this.rolEmisorOficiosSalida) {
            sb.append("Contiene ambos roles de Emisor de oficios de entrada y salida. Solo puede tener un rol de Emisor a la vez.\n");
        }
        
        
        if (this.rolReceptorMonterrey && this.rolReceptorReynosa) {
            sb.append("Contiene ambos roles de Receptor de oficios de Monterrey y Reynosa. Solo puede tener un rol de Receptor a la vez.\n");
        }
        
        
        if (sb.toString().length() > 0) {
            throw new InvalidPermissionsException(sb.toString());
            
        }
        
    }
    
    
    
    
    
    /**
     * Roles de edición
     * 
     * @return 
     */
    public boolean isRolEmisorOficiosEntrada() {
        return rolEmisorOficiosEntrada;
    }

    public boolean isRolEmisorOficiosSalida() {
        return rolEmisorOficiosSalida;
    }

    public boolean isRolReceptorMonterrey() {
        return rolReceptorMonterrey;
    }

    public boolean isRolReceptorReynosa() {
        return rolReceptorReynosa;
    }

    public boolean isRolEditorOficiosSalida() {
        return rolEditorOficiosSalida;
    }

    public boolean isRolEditorAdjuntoSalida() {
        return rolEditorAdjuntoSalida;
    }

    public boolean isRolEditorMaestro() {
        return rolEditorMaestro;
    }
    
    
    
    /**
     * Metodo de conveniencia para determinar si un rol puede modificar 
     * oficios.
     * 
     * @return 
     */
    public boolean isRolEdicionOficios() {
        
        // roles de edición
        boolean b1 = rolEmisorOficiosEntrada;
        boolean b2 = rolEmisorOficiosSalida;
        boolean b3 = rolReceptorMonterrey;
        boolean b4 = rolReceptorReynosa;
        
        boolean result = b1 || b2 || b3 || b4;
        
        return result;
        
    }
    
    
    /**
     * Solo los roles de edición pueden entrar a la Bandeja de Entrada.
     * 
     * @return 
     */
    public boolean puedeEntrarBandejaEntrada() {
        
        return isRolEdicionOficios();
        
    }
    
    /**
     * Metodo de conveniencia para determinar si un rol puede emitir 
     * (dar de alta) oficios.
     * 
     * @return 
     */
    public boolean isRolEmisorOficios() {
        
        // roles de edición
        boolean b1 = rolEmisorOficiosEntrada;
        boolean b2 = rolEmisorOficiosSalida;
        
        boolean result = b1 || b2;
        
        return result;
        
    }

    
    
    /**
     * Opciones
     * 
     * @return 
     */
    
    
    public boolean isAltaOficio() {
        return altaOficio;
    }

    public boolean isConsultarOficio() {
        return consultarOficio;
    }
    
    public boolean isDescargarArchivoAdjuntoHistorialOficio() {
        return descargarArchivoAdjuntoHistorialOficio;
    }

    public boolean isModificarAdjuntoHistorialOficio() {
        return modificarAdjuntoHistorialOficio;
    }
    
    public boolean isIngresarModuloOficios() {
        return ingresarModuloOficios;
    }

    public boolean isModificarOficio() {
        return modificarOficio;
    }
    
    public boolean isModificarOficioSalida() {
        return modificarOficioSalida;
    }

    public boolean isModificarAdjuntoSalida() {
        return modificarAdjuntoSalida;
    }
    
    

    public boolean isAnularOficio() {
        return anularOficio;
    }
    
    

    public boolean isPromoverEstatusOficio() {
        return promoverEstatusOficio;
    }

    public boolean isRecibirCorreoAltaOficio() {
        return recibirCorreoAltaOficio;
    }

    public boolean isVerDetalleOficio() {
        return verDetalleOficio;
    }

    public boolean isVerHistorialOficio() {
        return verHistorialOficio;
    }

    public boolean isVerTodoGerencias() {
        return verTodoGerencias;
    }

    public boolean isVerArchivoAdjuntoHistorialOficio() {
        return verArchivoAdjuntoHistorialOficio;
    }

    public boolean isGestionarSeguimientoOficio() {
        return gestionarSeguimientoOficio;
    }
    
    
    
    
    /**
     * 
     * @return 
     */
    private UtilLog4j getLogger() {
        return UtilLog4j.log;
    }

    /**
     * @return the copiarEnOtroBloque
     */
    public boolean isCopiarEnOtroBloque() {
        return copiarEnOtroBloque;
    }
    
    
    
}
