
package sia.modelo.oficio.vo;

import lombok.Data;

/**
 * Contenedor global para la transferencia de la información entre las capas y 
 * métodos de negocio del módulo de Control de Oficios.
 * 
 *
 * @author esapien
 */
public @Data class InformacionOficioVo {
    
    /**
     * Información del oficio a guardar
     */
    private OficioPromovibleVo oficioVo;
    
    /**
     * ID del usuario al que se le registrará el movimiento.
     */
    private String idUsuario;

    /**
     * Constructor
     * 
     * @param oficioVo
     * @param idUsuario 
     */
    public InformacionOficioVo(OficioPromovibleVo oficioVo, String idUsuario/*, PermisosVo permisosVo*/) {
        this.oficioVo = oficioVo;
        this.idUsuario = idUsuario;
    }
    
    
}
