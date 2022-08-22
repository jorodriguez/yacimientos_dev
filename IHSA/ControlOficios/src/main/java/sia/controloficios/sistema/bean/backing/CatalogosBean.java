package sia.controloficios.sistema.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import sia.modelo.Compania;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.vo.ApCampoVo;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.util.UtilLog4j;

/**
 * Contiene los valores de catálogos a utilizar en cada vista.
 *
 * TODO: ESAPIEN 13ene15 - Evaluar refactorización a Singleton EJB, con opción
 * de refrescado de información desde base de datos.
 *
 * @author esapien
 */
@ManagedBean
@ViewScoped
public class CatalogosBean implements Serializable {

    //private final static Logger logger = Logger.getLogger(CatalogosBean.class.getName());
    private List<SelectItem> companias;
    private List<SelectItem> bloques;
    private List<SelectItem> gerencias;
    private List<SelectItem> estatus;

    // para registrar los ID de las companias
    private Map<Integer, String> companiasIds;

    // para registrar los bloques de cada compañía
    private Map<Integer, List<SelectItem>> bloquesPorCompania;

    // para registrar las gerencias por cada bloque
    private Map<Integer, List<SelectItem>> gerenciasPorBloques;

    // Servicios remotos
    @Inject
    private CompaniaImpl companiaServicioRemoto;
    @Inject
    private ApCampoImpl campoServicioRemoto;
    @Inject
    private GerenciaImpl gerenciaServicioRemoto;

    /**
     * Realiza la carga inicial de los valores a utilizar en la aplicación.
     *
     */
    @PostConstruct
    public void iniciar() {
        getLogger().info(this, "CatalogosBean @PostConstruct - iniciar()");

        companiasIds = new HashMap();
        bloquesPorCompania = new HashMap();
        gerenciasPorBloques = new HashMap();

        cargarCatalogosCompaniaBloqueGerencia();

    }

    @PreDestroy
    public void terminar() {
        getLogger().info(this, "CatalogosBean @PreDestroy - terminar()");

    }

    /**
     *
     */
    private void cargarCatalogosCompaniaBloqueGerencia() {

        this.companias = new ArrayList<SelectItem>();

        List<GerenciaVo> gerenciasCampos = gerenciaServicioRemoto.getAllGerenciasByCampo();

        int auxCompaniaId = 1;

            


            // por cada compañia, obtener sus bloques

            List<SelectItem> bloquesTemp = new ArrayList<SelectItem>();



                    //logger.log(Level.INFO, "Agregando bloque = \t{0}/{1}/{2}", new Object[]{auxCompaniaId, bloqueTemp.getId(), bloqueTemp.getNombre()});
                    // por cada bloque, obtener sus gerencias

                    List<SelectItem> gerenciasTemp = new ArrayList<SelectItem>();

                    int idCampoActual = 0;
                    String rfcActual = "";
                    int idGerenciaActual = 0;
                    for (GerenciaVo gerenciaTemp : gerenciasCampos) {
                        SelectItem bloqueItem;
                        if(!gerenciaTemp.getRfcCompania().equals(rfcActual)){
                            bloquesTemp = new ArrayList<SelectItem>();
                            SelectItem companiaItem = new SelectItem(auxCompaniaId, gerenciaTemp.getNombreCompania());
                            rfcActual = gerenciaTemp.getRfcCompania();
                            companiasIds.put(auxCompaniaId, rfcActual);
                            companias.add(companiaItem);
                            
                            
                            if(idCampoActual != gerenciaTemp.getIdApCampo()){
                                gerenciasTemp = new ArrayList<SelectItem>();
                                 bloqueItem = new SelectItem(gerenciaTemp.getIdApCampo(), gerenciaTemp.getNombreApCampo());
                                idCampoActual = gerenciaTemp.getIdApCampo();
                                bloquesTemp.add(bloqueItem);
                                
                                if(gerenciaTemp.getId() != idGerenciaActual){
                                    SelectItem gerenciaItem = new SelectItem(gerenciaTemp.getId(), gerenciaTemp.getNombre());
                                    gerenciasTemp.add(gerenciaItem);
                                    idGerenciaActual = gerenciaTemp.getId();
                                    gerenciasPorBloques.put(idCampoActual, gerenciasTemp);
                                }
                                bloquesPorCompania.put(auxCompaniaId, bloquesTemp);
                                auxCompaniaId++;
                            } else {
                                
                                if(gerenciaTemp.getId() != idGerenciaActual){
                                    SelectItem gerenciaItem = new SelectItem(gerenciaTemp.getId(), gerenciaTemp.getNombre());
                                    gerenciasTemp.add(gerenciaItem);
                                    idGerenciaActual = gerenciaTemp.getId();
                                    gerenciasPorBloques.put(idCampoActual, gerenciasTemp);
                                }
                            }
                        } else {
                            if(idCampoActual != gerenciaTemp.getIdApCampo()){
                                gerenciasTemp = new ArrayList<SelectItem>();
                                 bloqueItem = new SelectItem(gerenciaTemp.getIdApCampo(), gerenciaTemp.getNombreApCampo());
                                idCampoActual = gerenciaTemp.getIdApCampo();
                                bloquesTemp.add(bloqueItem);
                                bloquesPorCompania.put(auxCompaniaId, bloquesTemp);
                                auxCompaniaId++;
                                if(gerenciaTemp.getId() != idGerenciaActual){
                                    SelectItem gerenciaItem = new SelectItem(gerenciaTemp.getId(), gerenciaTemp.getNombre());
                                    gerenciasTemp.add(gerenciaItem);
                                    idGerenciaActual = gerenciaTemp.getId();
                                    gerenciasPorBloques.put(idCampoActual, gerenciasTemp);
                                }
                            } else {
                                if(gerenciaTemp.getId() != idGerenciaActual){
                                    SelectItem gerenciaItem = new SelectItem(gerenciaTemp.getId(), gerenciaTemp.getNombre());
                                    gerenciasTemp.add(gerenciaItem);
                                    idGerenciaActual = gerenciaTemp.getId();
                                    gerenciasPorBloques.put(idCampoActual, gerenciasTemp);
                                }
                            }
                        }
                    }
                    
            bloquesPorCompania.put(auxCompaniaId, bloquesTemp);
    }

    /**
     * Actualiza la lista de bloques en función a la compañía seleccionada para
     * la vista.
     *
     * @param event
     */
    public void actualizarBloques(ValueChangeEvent event) {
        getLogger().info(this, "@CatalogosBean.actualizarBloques");

        int idCompania = (Integer) event.getNewValue();

        actualizarBloques(idCompania);

    }

    /**
     *
     * @param idCompania
     */
    public void actualizarBloques(Integer idCompania) {

        this.bloques = idCompania == null ? null : bloquesPorCompania.get(idCompania);
        this.gerencias = null;

    }

    /**
     *
     *
     * @param companiaId
     * @return
     */
    public String obtenerCompaniaRfc(int companiaId) {
        return companiasIds.get(companiaId);
    }

    /**
     * Obtiene la llave numérica de la compañía relacionada con el RFC para su
     * mostrado en la lista en la interfaz de usuario.
     *
     * @param rfc
     * @return
     */
    public int obtenerCompaniaId(String rfc) {

        return getKeyByValue(this.companiasIds, rfc);

    }

    /**
     * Método de utilería para obtener una llave de un mapa en función de su
     * valor. Esta función es válida solo las llaves y valores que tienen una
     * relación de uno a uno.
     *
     * @param <T>
     * @param <E>
     * @param map
     * @param value
     * @return
     */
    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Actualiza la lista de gerencias en función al bloque seleccionado para la
     * vista.
     *
     * @param event
     */
    public void actualizarGerencias(ValueChangeEvent event) {
        getLogger().info(this, "@CatalogosBean.actualizarGerencias");

        int idBloque = (Integer) event.getNewValue();

        actualizarGerencias(idBloque);
    }

    /**
     * Se utiliza para indicar que el combo está en proceso de carga de valores
     * (gerencias).
     *
     * @param idBloque
     */
    /*public void indicarCargaGerencias() {
        
        List<SelectItem> items = new ArrayList<SelectItem>(1);
        
        // TODO: I18N
        SelectItem item = new SelectItem(-1, "Cargando...", "Cargando gerencias del bloque", true);
        
        items.add(item);
        
        this.gerencias = items;
        
    }*/
    public void actualizarGerencias(Integer idBloque) {

        this.gerencias = idBloque == null ? null : gerenciasPorBloques.get(idBloque);
    }

    public List<SelectItem> getCompanias() {
        return companias;
    }

    public void setCompanias(List<SelectItem> companias) {
        this.companias = companias;
    }

    public List<SelectItem> getBloques() {
        return bloques;
    }

    public void setBloques(List<SelectItem> bloques) {
        this.bloques = bloques;
    }

    public List<SelectItem> getGerencias() {
        return gerencias;
    }

    public void setGerencias(List<SelectItem> gerencias) {
        this.gerencias = gerencias;
    }

    public List<SelectItem> getEstatus() {
        return estatus;
    }

    public void setEstatus(List<SelectItem> estatus) {
        this.estatus = estatus;
    }

    /**
     *
     * @return
     */
    private UtilLog4j getLogger() {
        return UtilLog4j.log;
    }

}
