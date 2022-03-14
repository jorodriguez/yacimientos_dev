/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package sia.modelo.oficio.vo;

import java.util.List;
import java.util.Map;
import sia.excepciones.MissingRequiredValuesException;
import sia.excepciones.PromotionFailedException;
import sia.modelo.estatus.vo.EstatusVo;

/**
 *
 * @author mluis
 */
public interface Promovible {
    
    
    /**
     * 
     * Promueve al siguiente estatus válido para este elemento.
     * 
     * @throws PromotionFailedException En caso que la promoción falle o no 
     * pueda llevarse a cabo por alguna razón de negocio aplicable. 
     * 
     * <p/>Ejemplo: En caso que el elemento se encuentre en último estatus.
     * 
     * @throws MissingRequiredValuesException En caso que la promoción falle
     * por falta de algún valor requerido.
     */
    void promover() throws PromotionFailedException, MissingRequiredValuesException;
    
    
    /**
     * Transfiere este elemento a su estatus de Anulado correspondiente.
     * 
     */
    void anular();
    
    
    /**
     * Regresa el ID de estatus correspondiente a este elemento.
     * 
     * @return 
     */
    Integer getEstatusId();
    
    /**
     * Regresa el nombre del estatus correspondiente al ID.
     * 
     * @param estatusId
     * @return 
     */
    String getEstatusNombre(Integer estatusId);
    
    /**
     * Regresa el texto de información del estatus correspondiente al ID.
     * 
     * @param estatusId
     * @return 
     */
    String getEstatusInformacion(Integer estatusId);
    
    
    /**
     * Regresa una lista con los nombres de los estatus aplicables a la 
     * implementacion de este elemento, para los atributos ID (ID del estatus) 
     * y Nombre (nombre o descripción del estatus).
     * 
     * Los estatus se listan en orden ascendente por ID.
     * 
     * @return 
     */
    List<EstatusVo> getEstatusLista();
    
    
    /**
     * Regresa el estatus ID correspondiente al ID de operacion indicado.
     * 
     * El ID de operación proviene de los movimientos (promociones) del oficio.
     * 
     */
    Integer getEstatusId(Integer operacionId);
    
    
    /**
     * Para identificar si el oficio está en el primer estado.
     * 
     * @return 
     */
    boolean isEstatusCreado();
    
    
    /**
     * Indica si este elemento ha sido anulado.
     * 
     * @return 
     */
    boolean isEstatusAnulado();
    
    
    /**
     * Indica si este elemento ha pasado exitosamente a través de todo el ciclo 
     * de promociones y se encuentra en el último estatus.
     * 
     * @return 
     */
    boolean isEstatusTerminado();
    
    
    /**
     * Para indicar si este oficio está proximo a terminar. Es decir, si 
     * su estatus actual es el penúltimo en la secuencia de estatus.
     * 
     * @return 
     */
    boolean isEstatusPorTerminar();
    
    
    /**
     * Regresa el ID de estatus Anulado correspondiente a este elemento.
     * 
     * @return 
     */
    //int getEstatusAnuladoId();
    
    
    /**
     * Regresa el motivo por defecto para el registro del movimiento en la 
     * base de datos correspondiente al ID de estatus proporcionado.
     * 
     * @param estatusId
     * @return 
     */
    String getMotivoMovimiento(Integer estatusId);
    
    
    /**
     * Indica si se requiere un archivo adjunto para promover el estatus
     * actual de este elemento al siguiente estatus.
     * 
     * @return 
     */
    Boolean requiereArchivoAdjuntoPromocion();
    
    
    /**
     * Proporciona el nombre del estatus siguiente al actual para fines 
     * informativos en la base de datos.
     * 
     * @return 
     */
    String getSiguienteEstatusNombre();
    
    
    
    /**
     * 
     * @return 
     */
    Map<Integer, Integer> getMapaEstatusOperaciones();
    
    
    /**
     * Indica el código del rol a notificar en caso de promoción exitosa 
     * al estatus actual.
     * 
     * @return Código en la base de datos del rol a notificar, o nulo en caso 
     * que no se necesite notificar a ningún rol.
     */
    String rolCodigoNotificarPromocionEstatusActual();
    
    
    
    
}
