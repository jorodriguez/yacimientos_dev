

package sia.modelo.oficio.vo;

import java.util.*;
import sia.constantes.Constantes;
import sia.excepciones.MissingRequiredValuesException;
import sia.excepciones.PromotionFailedException;
import sia.modelo.estatus.vo.EstatusVo;

/**
 * Representa un oficio de salida (emitido de IHSA hacia Pemex) en el módulo 
 * de Control de Oficios.
 *
 * @author esapien
 */
public class OficioSalidaVo extends OficioPromovibleVo {
    
    
    /**
     * Mapa de equivalencias de operaciones/estatus de oficios
     */
    private static final Map<Integer, Integer> MAPA_OPERACIONES_ESTATUS;
    
    
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
     * Mapa de equivalencias de estatus/operaciones de oficios
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
        mapaEstatus.put(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA, "Enviado Reynosa");
        mapaEstatus.put(Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX, "Recibido Pemex");
        mapaEstatus.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO, "Oficio Terminado");
        mapaEstatus.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO, "Oficio Anulado");
        
        MAPA_ESTATUS = Collections.unmodifiableMap(mapaEstatus);
        
        // estatus y textos informativos
        
        Map<Integer, String> mapaInformacion = new HashMap();
        
        mapaInformacion.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO, "Oficio registrado en el sistema para su envío de Monterrey a Reynosa.");
        mapaInformacion.put(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA, "Oficio enviado a Reynosa para su sometimiento a Pemex.");
        mapaInformacion.put(Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX, "Oficio recibido en Pemex y enviado a Monterrey para su recepción.");
        mapaInformacion.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO, "Oficio recibido correctamente en Monterrey.");
        mapaInformacion.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO, "Oficio anulado; consultar el historial de movimientos del oficio para mayor información.");
        
        MAPA_ESTATUS_INFORMACION = Collections.unmodifiableMap(mapaInformacion);
        
        // estatus-operaciones
        
        Map<Integer, Integer> map = new HashMap();
        
        map.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO, Constantes.OFICIOS_OPERACION_ID_OFICIO_CREADO);
        map.put(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA, Constantes.OFICIOS_OPERACION_ID_ENVIADO_REYNOSA);
        map.put(Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX, Constantes.OFICIOS_OPERACION_ID_RECIBIDO_PEMEX);
        map.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO, Constantes.OFICIOS_OPERACION_ID_OFICIO_TERMINADO);
        map.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO, Constantes.OFICIOS_OPERACION_ID_OFICIO_ANULADO);
        
        MAPA_ESTATUS_OPERACIONES = Collections.unmodifiableMap(map);
        
        // operaciones-estatus
        
        map = new HashMap();
        
        map.put(Constantes.OFICIOS_OPERACION_ID_OFICIO_CREADO, Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO);
        map.put(Constantes.OFICIOS_OPERACION_ID_ENVIADO_REYNOSA, Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA);
        map.put(Constantes.OFICIOS_OPERACION_ID_RECIBIDO_PEMEX, Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX);
        map.put(Constantes.OFICIOS_OPERACION_ID_OFICIO_TERMINADO, Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO);
        map.put(Constantes.OFICIOS_OPERACION_ID_OFICIO_ANULADO, Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO);
        
        MAPA_OPERACIONES_ESTATUS = Collections.unmodifiableMap(map);
        
        
        // estatus-movimientos
        // Contiene los motivos por defecto para cada promoción para el registro del movimiento
        
        Map<Integer, String> mapaMotivos = new HashMap();
        
        mapaMotivos.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO, "Alta de nuevo oficio de salida con estatus Oficio Creado.");
        mapaMotivos.put(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA, "Se promueve oficio de salida a estatus Enviado Reynosa.");
        mapaMotivos.put(Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX, "Se promueve oficio de salida a estatus Recibido Pemex.");
        mapaMotivos.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO, "Se promueve oficio de salida a estatus Oficio Terminado.");
        
        // El motivo de anulación será proporcionado por el usuario
        mapaMotivos.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO, null);
        
        MAPA_ESTATUS_MOTIVOS = Collections.unmodifiableMap(mapaMotivos);
        
        // siguientes nombres de estatus
        
        Map<Integer, String> mapaNombres = new HashMap();
        
        mapaNombres.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO, MAPA_ESTATUS.get(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA));
        mapaNombres.put(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA, MAPA_ESTATUS.get(Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX));
        mapaNombres.put(Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX, MAPA_ESTATUS.get(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO));
        mapaNombres.put(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO, null);
        
        MAPA_SIGUIENTE_ESTATUS_NOMBRES = Collections.unmodifiableMap(mapaNombres);
        
        
        // estatus cuya promoción se requiere archivo adjunto
        
        Set<Integer> setEstatusPromocionAdjunto = new HashSet();
        
        // se requiere un archivo adjunto para promover desde Recibido Reynosa 
        // a Recibido Pemex
        setEstatusPromocionAdjunto.add(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA);
        
        SET_ESTATUS_PROMOCION_ARCHIVO_ADJUNTO 
                = Collections.unmodifiableSet(setEstatusPromocionAdjunto);
        
    }

    /**
     * Constructor
     * 
     */
    public OficioSalidaVo() {
        // valores por defecto para BD y UI
        setTipoOficioId(Constantes.OFICIOS_TIPO_OFICIO_SALIDA_ID);
        setTipoOficioNombre(Constantes.OFICIOS_TIPO_OFICIO_SALIDA_NOMBRE);
        
    }
    
    
    /**
     * 
     * Promueve este oficio al siguiente estatus.
     * 
     * En cada caso de promoción, valida los requisitos y reglas de negocio
     * aplicables.
     * 
     * @throws PromotionFailedException
     * @throws MissingRequiredValuesException 
     */
    
    public void promover() throws PromotionFailedException, MissingRequiredValuesException {
        
        switch(this.getEstatusId()) {
            
            case Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO:
                setEstatusId(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA);
                break;
            
            case Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA:
                
                // para promover a Recibido Pemex se debe proporcionar un 
                // archivo adjunto
             
                AdjuntoOficioVo adjunto = this.getArchivoPromocion();

                if (adjunto.getNombre() == null
                        || adjunto.getNombre().trim().length() == 0
                        || !adjunto.isArchivoGuardado()) {
                    
                    MissingRequiredValuesException ex 
                            = new MissingRequiredValuesException();
                    ex.setValoresFaltantes("Archivo adjunto");

                    throw ex;

                }
                
                setEstatusId(Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX);
                break;
            
            case Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX:
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
    
    

    
    public Map<Integer, Integer> getMapaEstatusOperaciones() {
        return MAPA_ESTATUS_OPERACIONES;
    }
    
    

    
    

    /**
     * 
     * @return 
     */
    
    public boolean isEstatusPorTerminar() {
        return getEstatusId() == Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX;
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

    
    public String getEstatusNombre(Integer estatusId) {
        return MAPA_ESTATUS.get(estatusId);
    }
    
    
    
    public String getEstatusInformacion(Integer estatusId) {
        return MAPA_ESTATUS_INFORMACION.get(estatusId);
    }
    
    


    
    public String rolCodigoNotificarPromocionEstatusActual() {
        
        String codigo = null;

        // se especifican los estatus a notificar y los roles correspondientes

        switch (getEstatusId()) {

            case Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA:

                codigo = Constantes.OFICIOS_ROL_RECEPTOR_REYNOSA_CODIGO;

                break;

            case Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX:

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
        int est2 = Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA;
        int est3 = Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX;
        int est4 = Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO;
        int est5 = Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO;
        
        estatus.add(new EstatusVo(est1, getEstatusNombre(est1), getEstatusInformacion(est1)));
        estatus.add(new EstatusVo(est2, getEstatusNombre(est2), getEstatusInformacion(est2)));
        estatus.add(new EstatusVo(est3, getEstatusNombre(est3), getEstatusInformacion(est3)));
        estatus.add(new EstatusVo(est4, getEstatusNombre(est4), getEstatusInformacion(est4)));
        estatus.add(new EstatusVo(est5, getEstatusNombre(est5), getEstatusInformacion(est5)));
        
        return estatus;
        
    }

    /**
     * 
     * @param estatusId
     * @return 
     */
    
    public boolean isEstatusIdInformeAvance(int estatusId) {
        
        // el estatus RECIBIDO PEMEX se registra con el archivo PDF
        // del oficio sellado por Pemex
        return estatusId == Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX;
                
    }

}
