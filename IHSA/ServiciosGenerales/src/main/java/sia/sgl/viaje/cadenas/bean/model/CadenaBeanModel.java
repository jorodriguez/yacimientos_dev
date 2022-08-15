/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.cadenas.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import sia.modelo.*;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.sgl.viaje.vo.CadenaAprobacionSolicitudVO;
import sia.modelo.sgl.viaje.vo.TipoSolicitudViajeVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.sgl.impl.SgCadenaAprobacionImpl;
import sia.servicios.sgl.impl.SgTipoSolicitudViajeImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.servicios.sgl.viaje.impl.SgCadenaNegacionImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.ConversationsManager;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

@Named
@ConversationScoped
public class CadenaBeanModel implements Serializable {

    @Inject
    private Sesion sesion;
    @Inject
    private Conversation conversationCadena;
    @Inject
    private ConversationsManager conversationsManager;
    @Inject
    private SgCadenaAprobacionImpl cadenaAprobacionService;
    @Inject
    private SgCadenaNegacionImpl cadenaNegacionService;
    @Inject
    private SgTipoSolicitudViajeImpl tipoSolicitudViajeService;
    @Inject
    private SgTipoTipoEspecificoImpl tipoTipoEspecificoService;
    @Inject
    private SiOperacionImpl siOperacionService;
    @Inject
    private EstatusImpl estatusService;
    @Inject
    private GerenciaImpl gerenciaService;
    private DataModel cadenasAprobacionModel;
    private DataModel cadenasNegacionModel;
    private DataModel estatusModel;
    private List<SelectItem> listaGerenciaItems;
    private List<SelectItem> listaTiposSolicitudesItems;
    private List<SelectItem> listaTiposEspecificoItems;
    private List<SelectItem> listaEstatusItems;
    private Estatus estatusActivo = null;
    private SiOperacion operacionActiva = null;
    private Gerencia gerenciaActiva = null;
    private SgTipoSolicitudViaje tipoSolicitudActiva = null;
    private SgCadenaAprobacion cadenaAprobacionActiva;
    private SgCadenaNegacion cadenaNegacionActiva;
//    private Collection<SgCadenaAprobacion> coleccionCadenas = new HashSet<SgCadenaAprobacion>();
    private List<CadenaAprobacionSolicitudVO> listCadenasAprobacionTemp = new ArrayList<CadenaAprobacionSolicitudVO>();
    private String seleccionRadio;
    private int idTipoEspecifico;
    private int idCadenaNegacion;
    private int idEstatusSeleccionado;
    private int idTipoSolicitud = -1, idGerencia, idEstatus;
    private HtmlInputHidden inputHidden;
    //pop
    private boolean mrPopup = false;
    private boolean mrPopupCadenaNegacion = false;

    public CadenaBeanModel() {
    }

    public <T> List<T> getDataModelAsList(DataModel dm) {
        return (List<T>) dm.getWrappedData();
    }

    public void beginConversationCadenasAprobacion() {
        UtilLog4j.log.info(this, "beginConversationCadenasAprobacion");
        this.conversationsManager.beginConversation(conversationCadena, "CadenaAprobacion");
    }

    public void traerEstatusModel() {
        UtilLog4j.log.info(this, "id tipo de solicitud #### " + getIdTipoSolicitud());
        try {
            setEstatusModel(null);
            if (getIdTipoSolicitud() == 2) {//terrestre para empleado
                ListDataModel<Estatus> model = new ListDataModel(estatusService.traerPorRango(430, 440));
                setEstatusModel(model);
                UtilLog4j.log.info(this, "de 430 al 440 ");
            } else {
                ListDataModel<Estatus> model = new ListDataModel(estatusService.traerPorRango(420, 440));
                setEstatusModel(model);
                UtilLog4j.log.info(this, "de 420 al 440 ");
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion en traer todas las cadenas de aprobacion " + e.getMessage());
        }
    }

    public DataModel traerCadenasNegacionModel() {
        UtilLog4j.log.info(this, "traerCadenasNegacionModel");
        try {
            UtilLog4j.log.info(this, "antes de entrar ");
            if (inputHidden.getValue() != null) {
                UtilLog4j.log.info(this, "input " + getInputHidden().getValue().toString());
                ListDataModel<SgCadenaNegacion> model = new ListDataModel(cadenaNegacionService.traerCadenasNegacionPorCadenaAprobacion((Integer) inputHidden.getValue()));
                setCadenasNegacionModel(model);
                UtilLog4j.log.info(this, "Model asignado de cadena de negacion ");
            }
            return getCadenasNegacionModel();

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en traer todas las cadenas de negacion  " + e.getMessage());
            return null;
        }
    }

    public List<SelectItem> traerListaEstatusItems() {
        UtilLog4j.log.info(this, "traerEstatus Items");
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<Estatus> le = null;
        try {
            le = estatusService.traerPorRango(420, (getCadenaAprobacionActiva().getEstatus().getId() - 10));
            //for (Estatus e : le) {
            for (int x = 0; x < le.size(); x++) {
                SelectItem item = new SelectItem(le.get(x).getId(), le.get(x).getNombre());
                l.add(item);
            }
            this.setListaEstatusItems(l);
            return getListaEstatusItems();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error en la consulta de items de estatus" + e.getMessage());
            return null;
        }
    }

    public List<SelectItem> traerListaGerenciasItems() {
        UtilLog4j.log.info(this, "traerListaGerencias");
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<GerenciaVo> lg = null;
        try {
            lg = gerenciaService.getAllGerenciaByApCompaniaAndApCampo("IHI070320FI3", 1, "nombre", true, null, false);

            for (GerenciaVo g : lg) {
                SelectItem item = new SelectItem(g.getId(), g.getNombre());
                l.add(item);
                UtilLog4j.log.info(this, "gerencia " + g.getNombre());
            }
            this.setListaGerenciaItems(l);
            return getListaGerenciaItems();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error en la consulta de items de gerencias" + e.getMessage());
            return null;
        }
    }

    public List<SelectItem> traerListaSolicitudesItems() {
        UtilLog4j.log.info(this, "traerListaSolici");
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<TipoSolicitudViajeVO> ls = null;
        try {
            //ls = tipoSolicitudViajeService.findAll("sgTipoEspecifico.id", Constantes.ORDER_BY_ASC, false);
            ls = tipoSolicitudViajeService.findAllTipoSolicitud();
            for (TipoSolicitudViajeVO t : ls) {
                SelectItem item = new SelectItem(t.getId(), t.getNombreSolicitud());
                l.add(item);
            }
            this.setListaTiposSolicitudesItems(l);
            return getListaTiposSolicitudesItems();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error en la consulta de items de tipos de solicitudes" + e.getMessage());
            return null;
        }
    }

    public void traerTodasCadenasAprobacion() {
        try {
            ListDataModel<SgCadenaAprobacion> model = new ListDataModel(this.cadenaAprobacionService.traerCadenaAprobacion());
            setCadenasAprobacionModel(model);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en traer todas las cadenas de aprobacion " + e.getMessage());
        }
    }

    public void traerCadenasAprobacionPorTipoSolicitud() {
        try {
            ListDataModel<SgCadenaAprobacion> model = new ListDataModel(this.cadenaAprobacionService.traerCadenaAprobacionPorTipoSolicitudNativa(getIdTipoSolicitud()));
            setCadenasAprobacionModel(model);
            setTipoSolicitudActiva(tipoSolicitudViajeService.find(getIdTipoSolicitud()));
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en traer cadenas de aprobacion por tipo" + e.getMessage());
        }
    }

    public void traerCadenasNegacionPorCadenaAprobacion() {
        try {
            ListDataModel<SgCadenaNegacion> model = new ListDataModel(this.cadenaNegacionService.traerCadenasNegacionPorCadenaAprobacion(this.cadenaAprobacionActiva.getId()));
            setCadenasNegacionModel(model);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en traer cadenas de negacion por caena de aprobacion" + e.getMessage());
        }
    }

    public void guardarListaFragmentosCadenaAprobacion() {
        try {
            for (Iterator it = getListCadenasAprobacionTemp().iterator(); it.hasNext();) {
                CadenaAprobacionSolicitudVO x = (CadenaAprobacionSolicitudVO) it.next();
                cadenaAprobacionActiva = cadenaAprobacionService.crearCadenaAprobacion(x, sesion.getUsuario());
                guardarFragmentoCadenaNegacion(cadenaAprobacionActiva);
            }
            traerCadenasAprobacionPorTipoSolicitud();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en guardar fragmeto " + e.getMessage());
        }
    }

    public void modificarFragmentoCadenaAprobacion() {
        try {
            this.cadenaAprobacionService.modificarCadenaAprobacion(getCadenaAprobacionActiva(), getGerenciaActiva(), sesion.getUsuario());
            traerCadenasAprobacionPorTipoSolicitud();
            setMrPopup(false);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en guardar fragmeto " + e.getMessage());
        }
    }

    private void guardarFragmentoCadenaNegacion(SgCadenaAprobacion cadenaAprobacion) {
        try {
            //traer operaciones           
            if (cadenaAprobacion != null) {
//                List<SiOperacion> listOperacion = siOperacionService.traerOperacionPorRango(4, 5);
                if (cadenaAprobacion.getEstatus().getId() == 420) {//si es aprobar
                    cadenaNegacionService.crearCadenaNegacion(getCadenaAprobacionActiva(), siOperacionService.find(3), 400, sesion.getUsuario());
                    cadenaNegacionService.crearCadenaNegacion(getCadenaAprobacionActiva(), siOperacionService.find(4), 401, sesion.getUsuario());
                } else {
                    //mayor de aprobar
                    cadenaNegacionService.crearCadenaNegacion(getCadenaAprobacionActiva(), siOperacionService.find(3), 400, sesion.getUsuario());
                    cadenaNegacionService.crearCadenaNegacion(getCadenaAprobacionActiva(), siOperacionService.find(4), buscarEstatusRegreso(cadenaAprobacion.getEstatus().getId()).getId(), sesion.getUsuario());
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al guardar fragmento de cadena de negacion " + e.getMessage());
        }
    }

    public void modificarCadenaNegacion() {
        try {
            this.cadenaNegacionService.modificarCadenaNegacion(getCadenaNegacionActiva(), getIdEstatusSeleccionado(), sesion.getUsuario());
            traerCadenasAprobacionPorTipoSolicitud();
            setMrPopupCadenaNegacion(false);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en guardar fragmeto " + e.getMessage());
        }
    }

    public Estatus buscarEstatusRegreso(int idEstatus) {
        List<Estatus> listEstatus = estatusService.traerPorRango(420, 440);
        int index = 0;
        for (Estatus e : listEstatus) {
            if (e.getId() == idEstatus) {
                UtilLog4j.log.info(this, "Esta en el indice " + index);
                UtilLog4j.log.info(this, "Es el estatus " + e.getNombre());
                break;
            }
            index++;
        }
        return listEstatus.get(index - 1);
    }

    public void traerCadeNegacionSeleccionada() {
        UtilLog4j.log.info(this, "traercadena negacion seleccionada");
        if (getIdCadenaNegacion() != 0) {
            setCadenaNegacionActiva(cadenaNegacionService.find(getIdCadenaNegacion()));
            UtilLog4j.log.info(this, "Cadena de negacion ACTIVA " + getCadenaNegacionActiva().getSiOperacion().getNombre());
        }
    }

    public void traerGerenciaSeleccionada() {
        UtilLog4j.log.info(this, "traerGerenciaSeleccionada gerencia " + getIdGerencia());
        if (getIdGerencia() != 0) {
            setGerenciaActiva(gerenciaService.find(getIdGerencia()));
            UtilLog4j.log.info(this, "gerencia ACTIVA " + getGerenciaActiva().getNombre());
        }
    }

    public void traerEstatusSeleccionado() {
        if (getIdEstatus() != 0) {
            setEstatusActivo(estatusService.find(getIdEstatus()));
            UtilLog4j.log.info(this, "estatus ACTIVO " + getEstatusActivo().getNombre());
        }
    }

    public SgCadenaAprobacion traerCadenaAprobacionSeleccionada(Integer idCadenaAprobacion) {
        return cadenaAprobacionService.find(idCadenaAprobacion);
    }

    public void extraerListaParaAsigar() {
        try {
            getListCadenasAprobacionTemp().clear();
            List<Estatus> listaEstatus = null;
            if (getIdTipoSolicitud() == 2) { // Viaje terrestre empleado
                listaEstatus = estatusService.traerPorRango(430, 440);
            } else {
                if (getIdTipoSolicitud() == 4) { //Company man
                    listaEstatus = estatusService.traerPorRango(420, 430);
                } else {
                    listaEstatus = estatusService.traerPorRango(420, 440);
                }
            }

            for (Estatus e : listaEstatus) {
                //agregar a la nueva lista para asignar
                /*
                 * SgCadenaAprobacion c = new SgCadenaAprobacion();
                 * c.setEstatus(e);
                 * c.setSgTipoSolicitudViaje(getTipoSolicitudActiva());
                c.setGerencia(null);
                 */
                CadenaAprobacionSolicitudVO fragmento = new CadenaAprobacionSolicitudVO();
                fragmento.setIdEstatus(e.getId());
                fragmento.setNombreEstatus(e.getNombre());
                fragmento.setIdTipoSolicitudViaje(getTipoSolicitudActiva().getId());
                fragmento.setIdGerencia(null);
                fragmento.setNombrePuestoResponsableGerencia(null);
                fragmento.setNombreResponsableGerencia(null);
                getListCadenasAprobacionTemp().add(fragmento);
                UtilLog4j.log.info(this, "Estatus asignado a la lista" + fragmento.getNombre());
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en la lista para asignar " + e.getMessage());
        }
    }

    public void addGerenciaFragmentoCadenaLista() {
        UsuarioVO usuarioVo;
        int index = -1;
        try {
            
            for (Iterator it = getListCadenasAprobacionTemp().iterator(); it.hasNext();) {
                //SgCadenaAprobacion x = (SgCadenaAprobacion) it.next();
                index++;
                CadenaAprobacionSolicitudVO x = (CadenaAprobacionSolicitudVO) it.next();
                UtilLog4j.log.info(this, "estatus a comparar " + x.getNombreEstatus());
                UtilLog4j.log.info(this, "estatus activo ." + getEstatusActivo().getNombre());
                if (x.getNombreEstatus().equals(getEstatusActivo().getNombre())) {
                    UtilLog4j.log.info(this, "entro al indice " + index);
                    x.setIdGerencia(getGerenciaActiva().getId());
                    x.setNombreGerencia(getGerenciaActiva().getNombre());
                    //buscar el puesto del usuario responsable de la gerencia y el puesto
                    //asignarlo al atributo nombrePuesto
                    usuarioVo = gerenciaService.findDetailGerencia(getGerenciaActiva().getId(), 1);
                    if (usuarioVo != null) {
                        UtilLog4j.log.info(this, "encontro el detalle de la gerencia");
                        x.setNombreResponsableGerencia(usuarioVo.getNombre());
                        x.setNombrePuestoResponsableGerencia(usuarioVo.getPuesto());
                                
                        getListCadenasAprobacionTemp().set(index, x);
                        UtilLog4j.log.info(this, "Se agrego correctamente a la lista" + getListCadenasAprobacionTemp().get(index).getNombreGerencia());
                    }else{
                        FacesUtils.addErrorMessage("No se pudó asignar la gerencia debido a un error..");
                    }                    
                    break;
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en addGerencia " + e.getMessage());
        }
    }

    public DataModel cadenaAsignarDatamodelTMP() {
        UtilLog4j.log.info(this, "Cadenas Adignar Datamodel temp");
//        List<SgCadenaAprobacion> list = new ArrayList<SgCadenaAprobacion> (getColeccionCadenas());
        return new ListDataModel(getListCadenasAprobacionTemp());
    }

    public boolean validarListaCadena() {
        boolean ret = false;
        for (Iterator it = getListCadenasAprobacionTemp().iterator(); it.hasNext();) {
            CadenaAprobacionSolicitudVO x = (CadenaAprobacionSolicitudVO) it.next();
            if (x.getIdGerencia() == null) {
                UtilLog4j.log.info(this, "se encontro alguna cadena vacia ");
                ret = true;
                break;
            }
        }
        return ret;
    }

    public boolean validarExistenciaDatosListaCadena() {
        boolean ret = false;
        for (Iterator it = getListCadenasAprobacionTemp().iterator(); it.hasNext();) {
            CadenaAprobacionSolicitudVO x = (CadenaAprobacionSolicitudVO) it.next();
            if (x.getIdGerencia() != null) {
                UtilLog4j.log.info(this, "se encontro alguna cadena vacia ");
                ret = true;
                break;
            }
        }
        return ret;
    }

    /**
     * @return the cadenasAprobacionModel
     */
    public DataModel getCadenasAprobacionModel() {
        return cadenasAprobacionModel;
    }

    /**
     * @param cadenasAprobacionModel the cadenasAprobacionModel to set
     */
    public void setCadenasAprobacionModel(DataModel cadenasAprobacionModel) {
        this.cadenasAprobacionModel = cadenasAprobacionModel;
    }

    /**
     * @return the idTipoSolicitud
     */
    public int getIdTipoSolicitud() {
        return idTipoSolicitud;
    }

    /**
     * @param idTipoSolicitud the idTipoSolicitud to set
     */
    public void setIdTipoSolicitud(int idTipoSolicitud) {
        this.idTipoSolicitud = idTipoSolicitud;
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
        return idGerencia;
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
        this.idGerencia = idGerencia;
    }

    /**
     * @return the idEstatus
     */
    public int getIdEstatus() {
        return idEstatus;
    }

    /**
     * @param idEstatus the idEstatus to set
     */
    public void setIdEstatus(int idEstatus) {
        this.idEstatus = idEstatus;
    }

    /**
     * @return the estatusModel
     */
    public DataModel getEstatusModel() {
        return estatusModel;
    }

    /**
     * @param estatusModel the estatusModel to set
     */
    public void setEstatusModel(DataModel estatusModel) {
        this.estatusModel = estatusModel;
    }

    /**
     * @return the listaGerenciaItems
     */
    public List<SelectItem> getListaGerenciaItems() {
        return listaGerenciaItems;
    }

    /**
     * @param listaGerenciaItems the listaGerenciaItems to set
     */
    public void setListaGerenciaItems(List<SelectItem> listaGerenciaItems) {
        this.listaGerenciaItems = listaGerenciaItems;
    }

    /**
     * @return the listaTiposSolicitudesItems
     */
    public List<SelectItem> getListaTiposSolicitudesItems() {
        return listaTiposSolicitudesItems;
    }

    /**
     * @param listaTiposSolicitudesItems the listaTiposSolicitudesItems to set
     */
    public void setListaTiposSolicitudesItems(List<SelectItem> listaTiposSolicitudesItems) {
        this.listaTiposSolicitudesItems = listaTiposSolicitudesItems;
    }

    /**
     * @return the estatusActivo
     */
    public Estatus getEstatusActivo() {
        return estatusActivo;
    }

    /**
     * @param estatusActivo the estatusActivo to set
     */
    public void setEstatusActivo(Estatus estatusActivo) {
        this.estatusActivo = estatusActivo;
    }

    /**
     * @return the operacionActiva
     */
    public SiOperacion getOperacionActiva() {
        return operacionActiva;
    }

    /**
     * @param operacionActiva the operacionActiva to set
     */
    public void setOperacionActiva(SiOperacion operacionActiva) {
        this.operacionActiva = operacionActiva;
    }

    /**
     * @return the cadenaAprobacionActiva
     */
    public SgCadenaAprobacion getCadenaAprobacionActiva() {
        return cadenaAprobacionActiva;
    }

    /**
     * @param cadenaAprobacionActiva the cadenaAprobacionActiva to set
     */
    public void setCadenaAprobacionActiva(SgCadenaAprobacion cadenaAprobacionActiva) {
        this.cadenaAprobacionActiva = cadenaAprobacionActiva;
    }

    /**
     * @return the cadenasNegacionModel
     */
    public DataModel getCadenasNegacionModel() {
        return cadenasNegacionModel;
    }

    /**
     * @param cadenasNegacionModel the cadenasNegacionModel to set
     */
    public void setCadenasNegacionModel(DataModel cadenasNegacionModel) {
        this.cadenasNegacionModel = cadenasNegacionModel;
    }

    /**
     * @return the mrPopup
     */
    public boolean isMrPopup() {
        return mrPopup;
    }

    /**
     * @param mrPopup the mrPopup to set
     */
    public void setMrPopup(boolean mrPopup) {
        this.mrPopup = mrPopup;
    }

    /**
     * @return the gerenciaActiva
     */
    public Gerencia getGerenciaActiva() {
        return gerenciaActiva;
    }

    /**
     * @param gerenciaActiva the gerenciaActiva to set
     */
    public void setGerenciaActiva(Gerencia gerenciaActiva) {
        this.gerenciaActiva = gerenciaActiva;
    }

    /**
     * @return the tipoSolicitudActiva
     */
    public SgTipoSolicitudViaje getTipoSolicitudActiva() {
        return tipoSolicitudActiva;
    }

    /**
     * @param tipoSolicitudActiva the tipoSolicitudActiva to set
     */
    public void setTipoSolicitudActiva(SgTipoSolicitudViaje tipoSolicitudActiva) {
        this.tipoSolicitudActiva = tipoSolicitudActiva;
    }

    public int getSizeColumns() {
        return getEstatusModel().getRowCount();
    }

    public List<CadenaAprobacionSolicitudVO> getListCadenasAprobacionTemp() {
        return listCadenasAprobacionTemp;
    }

    /**
     * @param listCadenasAprobacionTemp the listCadenasAprobacionTemp to set
     */
    public void setListCadenasAprobacionTemp(List<CadenaAprobacionSolicitudVO> listCadenasAprobacionTemp) {
        this.listCadenasAprobacionTemp = listCadenasAprobacionTemp;
    }

    /**
     * @return the listaTiposEspecificoItems
     */
    public List<SelectItem> getListaTiposEspecificoItems() {
        return listaTiposEspecificoItems;
    }

    /**
     * @param listaTiposEspecificoItems the listaTiposEspecificoItems to set
     */
    public void setListaTiposEspecificoItems(List<SelectItem> listaTiposEspecificoItems) {
        this.listaTiposEspecificoItems = listaTiposEspecificoItems;
    }

    /**
     * @return the idTipoEspecifico
     */
    public int getIdTipoEspecifico() {
        return idTipoEspecifico;
    }

    /**
     * @param idTipoEspecifico the idTipoEspecifico to set
     */
    public void setIdTipoEspecifico(int idTipoEspecifico) {
        this.idTipoEspecifico = idTipoEspecifico;
    }

    /**
     * @return the seleccionRadio
     */
    public String getSeleccionRadio() {
        return seleccionRadio;
    }

    /**
     * @param seleccionRadio the seleccionRadio to set
     */
    public void setSeleccionRadio(String seleccionRadio) {
        this.seleccionRadio = seleccionRadio;
    }

    /**
     * @return the inputHidden
     */
    public HtmlInputHidden getInputHidden() {
        return inputHidden;
    }

    /**
     * @param inputHidden the inputHidden to set
     */
    public void setInputHidden(HtmlInputHidden inputHidden) {
        this.inputHidden = inputHidden;
    }

    /**
     * @return the idCadenaNegacion
     */
    public int getIdCadenaNegacion() {
        return idCadenaNegacion;
    }

    /**
     * @param idCadenaNegacion the idCadenaNegacion to set
     */
    public void setIdCadenaNegacion(int idCadenaNegacion) {
        this.idCadenaNegacion = idCadenaNegacion;
    }

    /**
     * @return the cadenaNegacionActiva
     */
    public SgCadenaNegacion getCadenaNegacionActiva() {
        return cadenaNegacionActiva;
    }

    /**
     * @param cadenaNegacionActiva the cadenaNegacionActiva to set
     */
    public void setCadenaNegacionActiva(SgCadenaNegacion cadenaNegacionActiva) {
        this.cadenaNegacionActiva = cadenaNegacionActiva;
    }

    /**
     * @return the mrPopupCadenaNegacion
     */
    public boolean isMrPopupCadenaNegacion() {
        return mrPopupCadenaNegacion;
    }

    /**
     * @param mrPopupCadenaNegacion the mrPopupCadenaNegacion to set
     */
    public void setMrPopupCadenaNegacion(boolean mrPopupCadenaNegacion) {
        this.mrPopupCadenaNegacion = mrPopupCadenaNegacion;
    }

    /**
     * @return the listaEstatusItems
     */
    public List<SelectItem> getListaEstatusItems() {
        return listaEstatusItems;
    }

    /**
     * @param listaEstatusItems the listaEstatusItems to set
     */
    public void setListaEstatusItems(List<SelectItem> listaEstatusItems) {
        this.listaEstatusItems = listaEstatusItems;
    }

    /**
     * @return the idEstatusSeleccionado
     */
    public int getIdEstatusSeleccionado() {
        return idEstatusSeleccionado;
    }

    /**
     * @param idEstatusSeleccionado the idEstatusSeleccionado to set
     */
    public void setIdEstatusSeleccionado(int idEstatusSeleccionado) {
        this.idEstatusSeleccionado = idEstatusSeleccionado;
    }
}
