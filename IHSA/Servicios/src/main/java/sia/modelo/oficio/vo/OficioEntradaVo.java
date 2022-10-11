

package sia.modelo.oficio.vo;

import java.util.*;
import sia.constantes.Constantes;
import sia.excepciones.PromotionFailedException;
import sia.modelo.estatus.vo.EstatusVo;

/**
 * Representa un oficio de entrada (emitido de Pemex hacia IHSA) en el módulo 
 * de Control de Oficios.
 *
 * @author esapien
 */
public class OficioEntradaVo extends OficioPromovibleVo {
    
    /**
     * Contiene las relaciones entre los ID de estatus y sus nombres.
     * 
     */
    private static final Map<Integer, String> MAPA_ESTATUS;
    
    /**
     * Contiene las relaciones entre los ID de estatus y el texto informativo correspondiente.
     * 
     */
    private static final Map<Integer, String> MAPA_ESTATUS_INFORMACION;
    
    
    /**
     * Mapa de equivalencias de operaciones/estatus de oficios de entrada
     */
    private static final Map<Integer, Integer> MAPA_OPERACIONES_ESTATUS;
    
    /**
     * Mapa de equivalencias de estatus/operaciones de oficios de entrada
     */
    private static final Map<Integer, Integer> MAPA_ESTATUS_OPERACIONES;
    
    /**
     * Mapa de equivalencias de descripción de movimiento por estatus para
     * registro en la bitácora de movimientos en la base de datos
     */
    private static final Map<Integer, String> MAPA_ESTATUS_MOTIVOS;
    
    
    /**
     * Contiene los estatus a cuya promoción se requiere un archivo adjunto. Se
     * valida en el proceso de promoción de esta implementación de oficio.
     */
    private static final Set<Integer> SET_ESTATUS_PROMOCION_ARCHIVO_ADJUNTO;
    
    
    /**
     * 
     */
    private static final Map<Integer, String> MAPA_SIGUIENTE_ESTATUS_NOMBRES;
    
    
    static {
        
        
        // Estatus aplicables a este tipo de oficio
        // Las descripciones corresponden a la tabla de ESTATUS
        
        Map<Integer, String> mapaEstatus = new HashMap();
        
        mapaEstatus.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO, "Oficio Creado");
        mapaEstatus.put(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY, "Enviado Monterrey");
        mapaEstatus.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO, "Oficio Terminado");
        mapaEstatus.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO, "Oficio Anulado");
        
        MAPA_ESTATUS = Collections.unmodifiableMap(mapaEstatus);
        
        // estatus y textos informativos
        
        Map<Integer, String> mapaInformacion = new HashMap();
        
        mapaInformacion.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO, "Oficio registrado en el sistema para su envío de Reynosa a Monterrey.");
        mapaInformacion.put(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY, "Oficio enviado a Monterrey para su recepción.");
        mapaInformacion.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO, "Oficio recibido correctamente en Monterrey.");
        mapaInformacion.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO, "Oficio anulado; consultar el historial de movimientos del oficio para mayor información.");
        
        MAPA_ESTATUS_INFORMACION = Collections.unmodifiableMap(mapaInformacion);
        
        
        // estatus-operaciones
        
        Map<Integer, Integer> map = new HashMap();
        
        map.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO, Constantes.OFICIOS_OPERACION_ID_OFICIO_CREADO);
        map.put(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY, Constantes.OFICIOS_OPERACION_ID_ENVIADO_MONTERREY);
        map.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO, Constantes.OFICIOS_OPERACION_ID_OFICIO_TERMINADO);
        map.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO, Constantes.OFICIOS_OPERACION_ID_OFICIO_ANULADO);
        
        MAPA_ESTATUS_OPERACIONES = Collections.unmodifiableMap(map);
        
        // operaciones-estatus
        
        map = new HashMap();
        
        map.put(Constantes.OFICIOS_OPERACION_ID_OFICIO_CREADO, Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO);
        map.put(Constantes.OFICIOS_OPERACION_ID_ENVIADO_MONTERREY, Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY);
        map.put(Constantes.OFICIOS_OPERACION_ID_OFICIO_TERMINADO, Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO);
        map.put(Constantes.OFICIOS_OPERACION_ID_OFICIO_ANULADO, Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO);
        
        MAPA_OPERACIONES_ESTATUS = Collections.unmodifiableMap(map);
        
        
        // estatus-movimientos
        
        Map<Integer, String> mapaMotivos = new HashMap();
        
        mapaMotivos.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO, "Alta de nuevo oficio de entrada con estatus Oficio Creado.");
        mapaMotivos.put(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY, "Se promueve oficio de entrada a estatus Enviado Monterrey.");
        mapaMotivos.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO, "Se promueve oficio de entrada a estatus Oficio Terminado.");
        
        // El motivo de anulación será proporcionado por el usuario
        mapaMotivos.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO, null);
        
        MAPA_ESTATUS_MOTIVOS = Collections.unmodifiableMap(mapaMotivos);
        
        // siguientes nombres de estatus
        
        Map<Integer, String> mapaNombres = new HashMap();
        
        mapaNombres.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO, MAPA_ESTATUS.get(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY));
        mapaNombres.put(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY, MAPA_ESTATUS.get(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO));
        mapaNombres.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO, null);
        
        MAPA_SIGUIENTE_ESTATUS_NOMBRES = Collections.unmodifiableMap(mapaNombres);
        
        // estatus cuya promoción se requiere archivo adjunto
        
        // En oficios de entrada no se requieren archivos adjuntos en promoción 
        // de estatus. Por lo tanto se establece un set vacío
        
        SET_ESTATUS_PROMOCION_ARCHIVO_ADJUNTO 
                = Collections.unmodifiableSet(new HashSet<Integer>());
        
    }

    
    public OficioEntradaVo() {
        // valores por defecto para BD y UI
        setTipoOficioId(Constantes.OFICIOS_TIPO_OFICIO_ENTRADA_ID);
        setTipoOficioNombre(Constantes.OFICIOS_TIPO_OFICIO_ENTRADA_NOMBRE);
        
    }
    
    /**
     * 
     * Promueve este oficio al siguiente estatus.
     * 
     * En cada caso de promoción, valida los requisitos y reglas de negocio
     * aplicables.
     * 
     * 
     * @throws PromotionFailedException
     */
    
    public void promover() throws PromotionFailedException {
        
        switch(this.getEstatusId()) {
            
            case Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO:
                setEstatusId(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY);
                break;
            
            case Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY:
                setEstatusId(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO);
                break;
            
            case Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO:
                throw new PromotionFailedException("El oficio ya está terminado.");
                
            default:
                throw new PromotionFailedException("El estatus actual es inválido.");
            
        }
        
    }
    
    

    
    public Integer getEstatusId(Integer operacionId) {
        return MAPA_OPERACIONES_ESTATUS.get(operacionId);
    }
    
    
    
    /**
     * 
     * @return 
     */
    
    public boolean isEstatusPorTerminar() {
        return getEstatusId() == Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY;
    }

    /**
     * 
     * @param estatusId
     * @return 
     */
    
    public String getMotivoMovimiento(Integer estatusId) {
        
        return MAPA_ESTATUS_MOTIVOS.get(estatusId);
    }
    
    
    public Boolean requiereArchivoAdjuntoPromocion() {
        
        return SET_ESTATUS_PROMOCION_ARCHIVO_ADJUNTO.contains(getEstatusId());
        
    }

    
    public String getSiguienteEstatusNombre() {
        return MAPA_SIGUIENTE_ESTATUS_NOMBRES.get(getEstatusId());
    }

    
    public Map<Integer, Integer> getMapaEstatusOperaciones() {
        return MAPA_ESTATUS_OPERACIONES;
    }

    
    public String getEstatusNombre(Integer estatusId) {
        return MAPA_ESTATUS.get(estatusId);
    }
    
    
    public String getEstatusInformacion(Integer estatusId) {
        return MAPA_ESTATUS_INFORMACION.get(estatusId);
    }

    /**
     * 
     * @return 
     */
    
    public String rolCodigoNotificarPromocionEstatusActual() {

        String codigo = null;

        switch (getEstatusId()) {

            case Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY:

                codigo = Constantes.OFICIOS_ROL_RECEPTOR_MONTERREY_CODIGO;

                break;
        }

        return codigo;

    }

    /**
     * 
     * @return 
     */
    
    public List<EstatusVo> getEstatusLista() {
        
        List<EstatusVo> estatus = new ArrayList<EstatusVo>();
        
        int est1 = Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO;
        int est2 = Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY;
        int est3 = Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO;
        int est4 = Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO;
        
        estatus.add(new EstatusVo(est1, getEstatusNombre(est1), getEstatusInformacion(est1)));
        estatus.add(new EstatusVo(est2, getEstatusNombre(est2), getEstatusInformacion(est2)));
        estatus.add(new EstatusVo(est3, getEstatusNombre(est3), getEstatusInformacion(est3)));
        estatus.add(new EstatusVo(est4, getEstatusNombre(est4), getEstatusInformacion(est4)));
        
        return estatus;
        
    }

    /**
     * 
     * @param estatusId
     * @return 
     */
    
    public boolean isEstatusIdInformeAvance(int estatusId) {
        
        // el oficio de entrada se registra en el primer estatus con archivo PDF 
        // adjunto
        return estatusId == Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO;
    }


}
