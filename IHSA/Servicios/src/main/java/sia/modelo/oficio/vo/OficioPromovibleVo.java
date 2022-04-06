
package sia.modelo.oficio.vo;

import java.util.List;
import sia.constantes.Constantes;

/**
 * Contiene los elementos base para un tipo de oficio a ser promovido
 * a través de una secuencia de estatus particular para ese oficio.
 *
 * @author esapien
 */
public abstract class OficioPromovibleVo extends OficioVo implements Promovible{
    /**
     * Crea un oficio promovible en el estatus inicial correspondiente.
     * 
     */
    public OficioPromovibleVo() {
        
        this.setEstatusId(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO);
    }
    
    /**
     * Regresa el ID de operación correspondiente al estatus actual para efectos
     * de registro en la bitácora de movimientos.
     * 
     * @return 
     */
    public Integer getOperacionId() {
        
        return getOperacionId(getEstatusId());
        
    }
    
    
    /**
     * Indica si el ID de estatus de oficio corresponde a un estatus que incluye 
     * un archivo adjunto a incluir en el informe de avance diario que se envía por 
     * correo automático a los roles correspondientes.
     * 
     * @param estatusId 
     * @return 
     */
    public abstract boolean isEstatusIdInformeAvance(int estatusId);
    
    
    /**
     * 
     * @return 
     */
    
    public boolean isEstatusCreado() {
        
        return getEstatusId() == Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO;
        
    }
    
    
    /**
     * 
     * @return 
     */
    
    public boolean isEstatusAnulado() {
        
        return getEstatusId() == Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO;
        
    }
    
    
    
    public void anular() {
        this.setEstatusId(Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO);
    }
    
    
    /**
     * 
     * @return 
     */
    
    public boolean isEstatusTerminado() {
        return getEstatusId() == Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO;
    }
    
    
    
    
    
    /**
     * 
     * Regresa el  ID de operación correspondiente al ID de estatus 
     * proporcionado de este tipo de oficio, para registro en bitácora 
     * de movimientos en la base de datos.
     * 
     * 
     * @param estatusId
     * @return 
     */
    public Integer getOperacionId(Integer estatusId) {
        
        return getMapaEstatusOperaciones().get(estatusId);
        
    }
    
    /**
     * Sincronizar IDs con nombres de estatus.
     * 
     * @param estatusId 
     */
    
    @Override
    public final void setEstatusId(Integer estatusId) {
        
        super.setEstatusId(estatusId);
        this.setEstatusNombre(this.getEstatusNombre(estatusId));
        
    }
    
    
    
    /**
     * Para indicar que este oficio contiene un movimiento para el cual 
     * se ha registrado un archivo adjunto a ser incluido en el correo 
     * de informe de avance automático que se envía diariamente.Ejemplos:
 
 Oficio de Salida = PDF de oficio de IHSA a Pemex, recibido y sellado por Pemex
 Oficio de Entrada = PDF de oficio enviado de Pemex hacia IHSA
     * 
     *
     * @return 
     */
    public boolean contieneArchivoInformeAvance() {
        
        return obtenerArchivoInformeAvance() != null;
    }
    
    
    
    /**
     * 
     * @return 
     */
    public AdjuntoOficioVo obtenerArchivoInformeAvance() {
        
        AdjuntoOficioVo resultado = null;
        
        List<MovimientoVo> movimientos = getMovimientosAdjuntosActivos();
        
        for (MovimientoVo mov : movimientos) {
            
            if (isEstatusIdInformeAvance(getEstatusId(mov.getOperacionId()))) {
                
                resultado = mov.getAdjunto();
                break;
                
            }
        }
        
        return resultado;
        
    }
    
    
    /**
     * Contiene el criterio para indicar si este oficio es editable (modificable).
     * 
     * @return 
     */
    /*public boolean isEditable() {
        
        // los oficios serán editables solo si están en estado inicial (creado)
        return getEstatusId() == Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO;
    }*/
    
    

    /**
     * Regresa una lista de los estatus de oficio a los que en base a los 
     * permisos del usuario se tiene acceso de consulta desde la bandeja 
     * de entrada.
     * 
     * Se usa en conjunto con el tipo de oficio.
     * 
     * @param permisosVo
     * @return 
     */
    /*public static List<Integer> getEstatusConsulta(PermisosVo permisosVo) {
        
        List<Integer> estatus = new ArrayList();
        
        // determinar los oficios a mostrar en la bandeja de entrada en función 
        // de los estatus y roles
        
        // los roles de Emisor son exclusivos
        if (permisosVo.isRolEmisorOficiosSalida()) {
            
            estatus.add(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO);
            
        } else if (permisosVo.isRolEmisorOficiosEntrada()) {
            
            estatus.add(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO);
            
        }
        
        // los roles Receptor son exclusivos
        if (permisosVo.isRolReceptorReynosa()) {
            
            estatus.add(OficioSalidaVo.ESTATUS_PENDIENTE_RECEPCION_REYNOSA);
            estatus.add(OficioSalidaVo.ESTATUS_RECIBIDO_REYNOSA);
            
        } else if (permisosVo.isRolReceptorMonterrey()) {
            
            // oficios de salida
            estatus.add(OficioSalidaVo.ESTATUS_RECIBIDO_PEMEX);
            estatus.add(OficioSalidaVo.ESTATUS_RECIBIDO_IHSA_MONTERREY);
            
            // oficios de entrada
            estatus.add(OficioEntradaVo.ESTATUS_PENDIENTE_RECEPCION_IHSA_MONTERREY);
            estatus.add(OficioEntradaVo.ESTATUS_RECIBIDO_IHSA_MONTERREY);
            
        }
        
        return estatus;
    }*/
    
    

}
