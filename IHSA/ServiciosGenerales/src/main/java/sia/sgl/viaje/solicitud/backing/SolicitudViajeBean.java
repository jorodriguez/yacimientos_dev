/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.solicitud.backing;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.excepciones.SIAException;
import sia.modelo.Gerencia;
import sia.modelo.SgMotivo;
import sia.modelo.SgOficina;
import sia.modelo.SgTipoSolicitudViaje;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.vo.MotivoVo;
import sia.modelo.sgl.vo.SiCiudadVO;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.viaje.solicitud.bean.model.SolicitudViajeBeanModel;
import sia.util.UtilLog4j;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import sia.excepciones.ItemUsedBySystemException;
import sia.fechas.asueto.SiDiasAsueto;
import sia.modelo.SgAerolinea;
import sia.modelo.SgLugar;
import sia.modelo.sgl.viaje.vo.InvitadoVO;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.usuario.vo.UsuarioVO;

/**
 *
 * @author b75ckd35th
 */
/*
 * @Named @RequestScoped
 */
@Named(value = "solicitudViajeBean")
@RequestScoped
public class SolicitudViajeBean implements Serializable {

    @Inject
    private SolicitudViajeBeanModel solicitudViajeBeanModel;
    /*
     * @Inject private Sesion sesion;
     */

    private UIData lisTerrestre;
    private UIData listAerea;
    
    private String spliterInvitado = " - ";

    /*
     * @Inject private Sesion sesion; @Inject private SolicitudViajeBeanModel
     * solicitudViajeBeanModel;
     *
     *
     * @Inject private ViajeBean viajeBean;
     */
    public SolicitudViajeBean() throws SIAException {
    }

//    public String goToSolicitudViaje() throws SIAException {
//        UtilLog4j.log.info(this, "goToSolicitudViaje");
//        UtilLog4j.log.info(this, "Mensaje de prueba");
//        //  solicitudViajeBeanModel.setCadena(null);
//        if (getOperacion().equals(Constantes.insertar)) {
//            inicializarComponentes();
//        }
//        solicitudViajeBeanModel.setUrl("/vistas/sgl/viaje/solicitud/solicitudViaje");
//
//        //   cargarSolicitudesPorJustificar();
//        return solicitudViajeBeanModel.getUrl();
//    }

    @Deprecated
    public String goToCreateSgSolicitudViaje() {
        solicitudViajeBeanModel.inicioPopUpFalse("popupCreateSgMotivo");
        solicitudViajeBeanModel.setCadena(Constantes.redondo);
        solicitudViajeBeanModel.setOperacion("INSERTAR");
        solicitudViajeBeanModel.inicializaValores();
        solicitudViajeBeanModel.tiemposPropuestosViaje();
        return "/vistas/sgl/viaje/solicitud/fragmentos/solicitudViaje";
    }

    public void goToSolicitudesPorAprobar() {
        if (solicitudViajeBeanModel.isActualizar()) { //se utiliza la opcion insertar para que el metodo solo se utilice cuando entra por primera vez.
            UtilLog4j.log.info(this, "goToSolicitudesPorAprobar()");
            solicitudViajeBeanModel.mostrarSolicitudesByAprobar();
        }
    }

    public void goToSolicitudesJustificar() {
        if (solicitudViajeBeanModel.isActualizar()) {
            UtilLog4j.log.info(this, "goToSolicitudesJustificar()");
            solicitudViajeBeanModel.mostrarSolicitudesByAprobar();
        }

    }

    public void goToSolicitudesCanceladas() {
        if (solicitudViajeBeanModel.isActualizar()) {
            UtilLog4j.log.info(this, "goToSolicitudesCanceladas()");
            solicitudViajeBeanModel.mostrarSolicitudesCanceladas();
        }

    }

    public void goToCreateItinerario() {
        UtilLog4j.log.info(this, "goToCreateItinerario()");
        solicitudViajeBeanModel.cargarDatosItinerario();
    }

    /**
     * Este método limpia el valor de un Componente HTML
     *
     * @param nombreFormulario
     * @param nombreComponente
     */
    public void clearComponent(String nombreFormulario, String nombreComponente) {
        log("Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
            UIComponent parentComponent = component.getParent();
            parentComponent.getChildren().clear();
        } catch (Exception e) {
            log("Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
        }
    }

    public void valueChangeListenerWithEstancia(ValueChangeEvent valueChangeEvent) {
        setWithEstancia((Boolean) valueChangeEvent.getNewValue());
    }

//    public void valueChangeListenerOficinaOrigen(ValueChangeEvent valueChangeEvent) {
//        int tmp = (Integer) valueChangeEvent.getNewValue();
//
//        log("Traer los destinos de las rutas.......");
//
//        List<SgDetalleRutaTerrestreVo> list = this.solicitudViajeBeanModel.getAllSgDetalleRutaTerrestreBySgOficinaOrigen(tmp);
//
//        for (SgDetalleRutaTerrestreVo vo : list) {
//            log(vo.getNombreSgOficina());
//        }//
//        log("fin traer destinos de las rutas.....");
//    }
    //----------------------------------------- INICIO SOLICITUD DE VIAJE BEAN ----------------------*/
    public List<SelectItem> getListaTipoSolicitud() {
        try {
            return solicitudViajeBeanModel.listaTipoSolicitud();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    public List<SelectItem> getListaGerenciaRol() {
        try {
            return solicitudViajeBeanModel.traerListaGerencia();
        } catch (Exception e) {
        }
        return null;
    }

    public void selectOptionPropia(ValueChangeEvent valueChangeEvent) {
        String optionSelected = (String) valueChangeEvent.getNewValue();
        this.solicitudViajeBeanModel.setOptionPropia(optionSelected);
        //Selecciona la opcion de estancia SI
        if (solicitudViajeBeanModel.getOptionPropia().equals(Constantes.OPCION_PROPIA)) {
            this.solicitudViajeBeanModel.setOptionEstancia(Constantes.LETRA_S);
        } else {
            this.solicitudViajeBeanModel.setOptionEstancia(Constantes.LETRA_N);
        }

        //solicitudViajeBeanModel.traerListaGerencia();
    }

    public List<SelectItem> getTraerOficina() {
        try {
            return solicitudViajeBeanModel.traerOficina();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    public void getTraerOficinaModal() {
        try {

            solicitudViajeBeanModel.traerOficinaModal();

        } catch (Exception e) {
            e.getStackTrace();
        }
        //  return getDataModelOficina();
    }

    public void rutaTipoOficina(ActionEvent event) {
        solicitudViajeBeanModel.setIdDetinoRuta(Constantes.RUTA_TIPO_OFICINA);
        solicitudViajeBeanModel.setSemaforoVo(null);
        solicitudViajeBeanModel.listaDestino();
        PrimeFaces.current().executeScript(";dialogoCambiarSemaforo.hide();");
    }

    public void rutaTipoCiudad(ActionEvent event) {
        solicitudViajeBeanModel.setIdDetinoRuta(Constantes.RUTA_TIPO_CIUDAD);
        solicitudViajeBeanModel.setSemaforoVo(null);
        solicitudViajeBeanModel.listaDestino();
        PrimeFaces.current().executeScript(";dialogoCambiarSemaforo.hide();");
    }

    public void rutaTipoLugar(ActionEvent event) {
        solicitudViajeBeanModel.setIdDetinoRuta(Constantes.RUTA_TIPO_LUGAR);
        solicitudViajeBeanModel.setSemaforoVo(null);
        solicitudViajeBeanModel.listaDestino();
        PrimeFaces.current().executeScript(";dialogoCambiarSemaforo.hide();");
    }

    public void selectOptionViajeAereoOrTerrestre(ValueChangeEvent valueChangeEvent) {
        this.solicitudViajeBeanModel.setOptionViaje((String) valueChangeEvent.getNewValue());
        if (solicitudViajeBeanModel.getOperacion().equals("INSERTAR")) {
            solicitudViajeBeanModel.tiemposPropuestosViaje();//Agrega lostiempos propuestos de viaje
            if (solicitudViajeBeanModel.getOptionViaje().equals("TERRESTRE")) {
                solicitudViajeBeanModel.setIdOficinaDestino(-1);
                this.solicitudViajeBeanModel.setTiemposPropuestosViajeTerrestre();
                solicitudViajeBeanModel.listaTipoSolicitud();
                solicitudViajeBeanModel.setIdDetinoRuta(Constantes.RUTA_TIPO_OFICINA);
            } else if (solicitudViajeBeanModel.getOptionViaje().equals("AEREO")) {
                //Proponer fecha de salida y regreso
                solicitudViajeBeanModel.setWithEstancia(false);
                solicitudViajeBeanModel.setOptionEstancia("N");
                solicitudViajeBeanModel.setOptionRangoSiCiudadOrigen("AF");
                solicitudViajeBeanModel.setOptionRangoSiCiudadDestino("AF");
                solicitudViajeBeanModel.setIdSiCiudadDestino(-1);
                solicitudViajeBeanModel.setIdSiCiudadOrigen(-1);
                solicitudViajeBeanModel.listaTipoSolicitud();
                solicitudViajeBeanModel.setSiCiudadOrigenSelectItem(solicitudViajeBeanModel.getAllSiCiudadSelectItemByRange("A", "F"));
                solicitudViajeBeanModel.setSiCiudadDestinoSelectItem(solicitudViajeBeanModel.getAllSiCiudadSelectItemByRange("A", "F"));
            }
        } else if (solicitudViajeBeanModel.getOperacion().equals(Constantes.modificar)) {
            solicitudViajeBeanModel.setIdDetinoRuta(-1);
        }
    }

    public void cambiarOficinaOrigen() {
        // 
        String o = FacesUtils.getRequestParameter("ori");
        List<Object[]> ofi = getListaOrigenes().get(0);
        List<Object[]> cd = getListaOrigenes().get(1);
        boolean encontro = false;
        int tip = 0;
        for (Object[] ob : ofi) {
            if (o.equals(ob[1].toString() + "-Oficina")) {
                setIdOficinaOrigen((Integer) ob[0]);
                setOrigen(o);
                encontro = true;
                tip = 1;
                //
                break;
            }
        }
        if (!encontro) {
            for (Object[] ob : cd) {
                if (o.equals(ob[1] + "-" + ob[2] + "-" + ob[3])) {
                    setIdOficinaOrigen((Integer) ob[0] + 1000);
                    setOrigen(o);
                    tip = 2;
                    encontro = true;
                    // 
                    break;
                }
            }
        }
        solicitudViajeBeanModel.listaDestino();
    }

    public void cambiarRuta() {
        //  setIdDestino(Integer.parseInt(FacesUtils.getRequestParameter("idDestino")));
        // setDestino(FacesUtils.getRequestParameter("Destino"));
        // setIdDetinoRuta(Constantes.RUTA_TIPO_OFICINA);

    }

    public void cambiarRutaByCiudad() {
        setIdDestino(Integer.parseInt(FacesUtils.getRequestParameter("idDestino")));
        setDestino(FacesUtils.getRequestParameter("Destino"));
        setIdDetinoRuta(Constantes.RUTA_TIPO_CIUDAD);
        // solicitudViajeBeanModel.setSemaforoVo(solicitudViajeBeanModel.buscarSemaforoActual());
        //   solicitudViajeBeanModel.listaDestino();
    }

    public List<SelectItem> getSelectItemSgMotivo() {
        List<MotivoVo> motivoList = null;
        List<SelectItem> listSelectItem = null;
        motivoList = this.solicitudViajeBeanModel.getAllSgMotivoList();
        if (!motivoList.isEmpty()) {
            listSelectItem = new ArrayList<SelectItem>();
            for (MotivoVo motivo : motivoList) {
                listSelectItem.add(new SelectItem(motivo.getId(), motivo.getNombre().toUpperCase()));
            }
        }
        return listSelectItem;
    }

    public String guardarGuardarSolicitudTerrestre() {
        try {

            if (solicitudViajeBeanModel.validarFormulario()) {
                solicitudViajeBeanModel.saveSgSolicitudViajeTerrestre();
            }
            llenarInvitadoJson();
            usuarioJson();
            return null;
        } catch (SIAException e) {
            log("en guardarGuardarSolicitudTerrestre()" + e.getMessage());
            UtilLog4j.log.error(e);
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle(e.getLiteral()));
            return "";
        }
    }

    public void modificarSolicitud() throws Exception {

        String des = FacesUtils.getRequestParameter("des");
//        String ori = FacesUtils.getRequestParameter("ori");
//        System.out.println(ori+" = "+getOrigen()+" y "+getDestino()+" = "+des);
//        if(!getOrigen().equals(ori)){
//            setOrigen(ori);
//        }
        List<Object[]> ofi = null;
        List<Object[]> cd = null;
        boolean encontro = false;
        ofi = getListaDestinos().get(0); //fixme  problemas al crear sv aerea de cd a cd
        cd = getListaDestinos().get(1);
        String desTesrrestre = "";

        for (Object[] ob : ofi) {
            if (ob[2].equals("a Oficina")) {
                desTesrrestre = ob[1] + "-Oficina";
            } else {
                desTesrrestre = (String) ob[1];
            }
            if (des.equals(desTesrrestre)) {
                setIdDestino((Integer) ob[0]);
                setDestino(ob[1].toString());
                encontro = true;
                break;
            }
        }
        if (!encontro) {
            for (Object[] ob : cd) {
                if (des.equals(ob[1] + "-" + ob[2] + "-" + ob[3])) {
                    setIdDestino((Integer) ob[0] + 1000);
                    encontro = true;
                    break;
                }
            }
        }
        if (solicitudViajeBeanModel.validarFormulario()) {

            String evluaViajeros = actualizarListaViajeros();
            if (!evluaViajeros.isEmpty()) {
                FacesUtils.addErrorMessage(evluaViajeros);
            } else {
                if (solicitudViajeBeanModel.modificarSolicitudViaje()) {

                    PrimeFaces.current().executeScript("$(modalFinalizar).modal('show');");
                } else {
                    FacesUtils.addErrorMessage("Ocurrio un problema al  actualizar la solicitud, favor de contactar al equipo del SIA al correo soportesia@ihsa.mx");

                }
            }

        }

    }

    /**
     * ****** el viaje es doble ****
     */
    public void seleccionarViajeSenccilloDoble(ValueChangeEvent event) {
        try {
            solicitudViajeBeanModel.setCadena(event.getNewValue().toString());
            if (solicitudViajeBeanModel.getCadena().equals(Constantes.sencillo)) {
                solicitudViajeBeanModel.setFechaRegreso(null);
                solicitudViajeBeanModel.setHoraRegreso(0);
                solicitudViajeBeanModel.setMinutoRegreso(0);
            }
        } catch (Exception e) {
            log("Excepcion al seleecionar el tipo de solicitud " + e.getMessage());
            log("en getSelectItemSgTipoSolicitudViaje" + e.getMessage());
            UtilLog4j.log.error(e);
        }
    }

    //-------------------------------------------------FINSOLICITUD VIAJE BEAN -----------------------*/
    public void validateFechaSalidaSolicitudViaje(FacesContext context, UIComponent validate, Object value) {
        if (value != null && value instanceof Date) {
            Date fechaSalida = (Date) value;
            if (fechaSalida != null) {
                Calendar cFechaSalida = this.solicitudViajeBeanModel.converterDateToCalendar(fechaSalida, false);
                if (!solicitudViajeBeanModel.validateDateIsAfterYesterday(cFechaSalida)) {
                    ((UIInput) validate).setValid(false);
                    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.mensaje.error.fechaAnteriorHoy"));
                } else if ("TERRESTRE".equals(this.solicitudViajeBeanModel.getOptionViaje())) {
                    if (this.solicitudViajeBeanModel.validateDateIsToday(cFechaSalida)) {
                        FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.solicitudViaje.mensaje.error.viajeTerrestre.fechaSalidaIncorrecta1"));
                        FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.solicitudViaje.mensaje.error.viajeTerrestre.fechaSalidaIncorrecta2"));
                    }
                } else if ("AEREO".equals(this.solicitudViajeBeanModel.getOptionViaje())) {
                    if (getIdSgTipoSolicitudViaje() > 0) {
                        SgTipoSolicitudViaje sgTipoSolicitudViaje = this.solicitudViajeBeanModel.findSgTipoSolicitudViajeById(getIdSgTipoSolicitudViaje());
                        if (!this.solicitudViajeBeanModel.validateFechaSalidaViajeAereo(cFechaSalida, getIdSgTipoSolicitudViaje())) {
                            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.solicitudViaje.mensaje.error.viajeAereo.fechaSalidaIncorrecta1")
                                    + ": " + getMessageForAnticipacionViajeAereo(sgTipoSolicitudViaje.getHorasAnticipacion()));
                            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.solicitudViaje.mensaje.error.viajeAereo.fechaSalidaIncorrecta2"));
                        }
                    }
                }
            }//fecha salida null
        }
    }

    public Date getFechaHoy() {
        return new Date();
    }

    public void validateFechaRegresoSolicitudViaje(FacesContext context, UIComponent validate, Object value) {
        if (value != null && value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date fechaRegreso = (Date) value;
            if (fechaRegreso != null && this.solicitudViajeBeanModel.getFechaSalida() != null) {
                try {
                    Date cFechaSalida = getFechaSalida();//sdf.parse(this.solicitudViajeBeanModel.getFechaSalida());
                    if (this.solicitudViajeBeanModel.getFechaSalida() != null) {
                        if (this.solicitudViajeBeanModel.validateFechaRegresoIsAfterOrEqualFechaSalida(fechaRegreso, cFechaSalida) < 0) {
                            //                        ((UIInput) validate).setValid(false);
                            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.mensaje.error.fechaRegresoAntesFechaSalida"));
                        }
                    }
                } catch (Exception ex) {
                    ex.getStackTrace();
                }
            }
        }
    }

    public void initFechasValidasForViajeAereo(ValueChangeEvent event) {
        int idSgTipoSolicitudViaje = (Integer) event.getNewValue();
        log("idSgTipoSolicitudViaje: " + idSgTipoSolicitudViaje);
        clearComponent("createSgSolicitudViajeAereoEmpleadoNormal", "fechaSalida");
        clearComponent("createSgSolicitudViajeAereoEmpleadoNormal", "fechaRegreso");
        clearComponent("createSgSolicitudViajeAereoAdministrador", "fechaSalida");
        clearComponent("createSgSolicitudViajeAereoAdministrador", "fechaRegreso");
        clearComponent("createSgSolicitudViajeAereoAnalista", "fechaSalida");
        clearComponent("createSgSolicitudViajeAereoAnalista", "fechaRegreso");
        clearComponent("createSgSolicitudViajeAereoAsistenteDireccion", "fechaSalida");
        clearComponent("createSgSolicitudViajeAereoAsistenteDireccion", "fechaRegreso");
        clearComponent("createSgSolicitudViajeAereoGerente", "fechaSalida");
        clearComponent("createSgSolicitudViajeAereoGerente", "fechaRegreso");
        clearComponent("createSgSolicitudViajeAereoResponsable", "fechaSalida");
        clearComponent("createSgSolicitudViajeAereoResponsable", "fechaRegreso");

        this.solicitudViajeBeanModel.setTiemposPropuestosViajeAereo(idSgTipoSolicitudViaje);
        log("Se asignaron los nuevos tiempos para la SVA y son: fechaSalida: " + getFechaSalida() + " fechaRegreso: " + getFechaRegreso());
    }

    public void selectOptionEstancia(ValueChangeEvent valueChangeEvent) {
        String optionSelected = (String) valueChangeEvent.getNewValue();
        log("optionEstanciaSelected: " + optionSelected);

        if ("S".equals(optionSelected)) {
            setWithEstancia(true);
        } else if ("N".equals(optionSelected)) {
            setWithEstancia(false);
        }
    }

    public void selectOptionRangoSiCiudadOrigen(ValueChangeEvent valueChangeEvent) {
        String optionSelected = (String) valueChangeEvent.getNewValue();
        log("optionRangoSiCiudadOrigenSelected: " + optionSelected);

        String startFilter = String.valueOf(optionSelected.charAt(0));
        String endFilter = String.valueOf(optionSelected.charAt(1));

        this.solicitudViajeBeanModel.setSiCiudadOrigenSelectItem(this.solicitudViajeBeanModel.getAllSiCiudadSelectItemByRange(startFilter, endFilter));
    }

    public void selectOptionRangoSiCiudadDestino(ValueChangeEvent valueChangeEvent) {
        String optionSelected = (String) valueChangeEvent.getNewValue();
        String startFilter = String.valueOf(optionSelected.charAt(0));
        String endFilter = String.valueOf(optionSelected.charAt(1));

        this.solicitudViajeBeanModel.setSiCiudadDestinoSelectItem(this.solicitudViajeBeanModel.getAllSiCiudadSelectItemByRange(startFilter, endFilter));
    }

    //Limpia todas las variables
    public void clearViews(ActionEvent actionEvent) {
        clearViews();
    }

    public void clearViews() {
        clearComponent("createSgSolicitudViajeTerrestreEmpleadoNormal", "observacion");
        clearComponent("createSgSolicitudViajeAereoEmpleadoNormal", "observacion");
        clearComponent("createSgSolicitudViajeAereoEmpleadoNormal", "siCiudadOrigen");
        clearComponent("createSgSolicitudViajeAereoEmpleadoNormal", "siCiudadDestino");
        clearComponent("createSgSolicitudViajeAereoEmpleadoNormal", "fSiCiudadOrigen");
        clearComponent("createSgSolicitudViajeAereoEmpleadoNormal", "fSiCiudadDestino");
        clearComponent("createSgSolicitudViajeTerrestreAsistenteDireccion", "observacion");
        clearComponent("createSgSolicitudViajeAereoAsistenteDireccion", "observacion");
        clearComponent("createSgSolicitudViajeAereoAsistenteDireccion", "siCiudadOrigen");
        clearComponent("createSgSolicitudViajeAereoAsistenteDireccion", "siCiudadDestino");
        clearComponent("createSgSolicitudViajeTerrestreGerente", "observacion");
        clearComponent("createSgSolicitudViajeAereoGerente", "observacion");
        clearComponent("createSgSolicitudViajeAereoGerente", "siCiudadOrigen");
        clearComponent("createSgSolicitudViajeAereoGerente", "siCiudadDestino");
        clearComponent("createSgSolicitudViajeTerrestreAnalista", "observacion");
        clearComponent("createSgSolicitudViajeAereoAnalista", "observacion");
        clearComponent("createSgSolicitudViajeAereoAnalista", "siCiudadOrigen");
        clearComponent("createSgSolicitudViajeAereoAnalista", "siCiudadDestino");
        clearComponent("createSgSolicitudViajeTerrestreResponsable", "observacion");
        clearComponent("createSgSolicitudViajeAereoResponsable", "observacion");
        clearComponent("createSgSolicitudViajeAereoResponsable", "siCiudadOrigen");
        clearComponent("createSgSolicitudViajeAereoResponsable", "siCiudadDestino");

        this.solicitudViajeBeanModel.clearVariables();
    }

    public List<SelectItem> getSelectItemOficina() {
        List<SgOficina> oficinaList = null;
        List<SelectItem> listSelectItem = null;
        oficinaList = this.solicitudViajeBeanModel.getSgOficinaList();
        if (!oficinaList.isEmpty()) {
            listSelectItem = new ArrayList<SelectItem>();
            for (SgOficina oficina : oficinaList) {
                listSelectItem.add(new SelectItem(oficina.getId(), oficina.getNombre()));
            }
        }
        return listSelectItem;
    }

    public List<SelectItem> getSelectItemOficinaAux() {
        List<SgOficina> oficinaList = null;
        List<SelectItem> listSelectItem = null;
        oficinaList = this.solicitudViajeBeanModel.getSgOficinaListAux();
        if (!oficinaList.isEmpty()) {
            listSelectItem = new ArrayList<SelectItem>();
            for (SgOficina oficina : oficinaList) {
                listSelectItem.add(new SelectItem(oficina.getId(), oficina.getNombre()));
            }
        }
        return listSelectItem;
    }

    public List<SelectItem> getSelectItemSgTipoSolicitudViaje() {
        List<SgTipoSolicitudViaje> tipoSolicitudViajeList = null;
        List<SelectItem> listSelectItem = null;
        try {
            tipoSolicitudViajeList = this.solicitudViajeBeanModel.getSgTipoSolicitudViajeList();
            if (!tipoSolicitudViajeList.isEmpty()) {
                listSelectItem = new ArrayList<SelectItem>();
                for (SgTipoSolicitudViaje tsv : tipoSolicitudViajeList) {
                    listSelectItem.add(new SelectItem(tsv.getId(), tsv.getNombre()));
                }
            }
        } catch (Exception e) {
            log("en getSelectItemSgTipoSolicitudViaje" + e.getMessage());
            UtilLog4j.log.error(e);
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            return listSelectItem;
        }
    }

    public List<SelectItem> getSelectItemGerencia() {
        List<GerenciaVo> gerenciaList = null;
        List<SelectItem> listSelectItem = null;
        gerenciaList = this.solicitudViajeBeanModel.getGerenciaList();
        if (gerenciaList != null && !gerenciaList.isEmpty()) {
            listSelectItem = new ArrayList<SelectItem>();
            for (GerenciaVo gerencia : gerenciaList) {
                listSelectItem.add(new SelectItem(gerencia.getId(), gerencia.getNombre()));
            }
        }
        return listSelectItem;
    }

    public List<SelectItem> getSelectItemHoras() {
        List<SelectItem> listSelectItem = new ArrayList<SelectItem>();

        for (Integer i = 1; i < 10; i++) { //01am - 09am
            listSelectItem.add(new SelectItem(i, ("0" + i.toString() + " hrs")));
        }
        for (Integer i = 10; i < 24; i++) { //10am - 11am
            listSelectItem.add(new SelectItem(i, i.toString() + " hrs"));
        }
//        listSelectItem.add(new SelectItem(12, "12" + " pm")); //12 pm
//
//        for (Integer i = 13; i < 22; i++) { //01pm - 09pm
//            listSelectItem.add(new SelectItem(i, ("0" + (i - 12) + " pm")));
//        }
//        for (Integer i = 22; i < 24; i++) { //10pm - 11pm
//            listSelectItem.add(new SelectItem(i, (i - 12) + " pm"));
//        }
//        listSelectItem.add(new SelectItem(0, "12" + " am")); //12am
        return listSelectItem;
    }

    public List<SelectItem> getSelectItemMinutos() {
        List<SelectItem> listSelectItem = new ArrayList<SelectItem>();

        for (Integer i = 0; i < 60;) {
            listSelectItem.add(new SelectItem(i, ((i < 10) ? ("0".concat(i.toString())) : i.toString())));
            i += 15;
        }
        return listSelectItem;
    }

    public String getMessageForAnticipacionViajeAereo(int hours) {
        //168 hrs. - 1 semana
        //720 hrs. - 1 mes
        //1440 hrs. - 2 meses

        if (hours < 168) {
            return ((hours / 24) + " " + FacesUtils.getKeyResourceBundle("sgl.solicitudViaje.mensaje.warning.solicitar.aereo.fueraDeTiempo2a")); //dias
        } else if (hours < 720) {
            return ((hours / 24 / 7) + " " + FacesUtils.getKeyResourceBundle("sgl.solicitudViaje.mensaje.warning.solicitar.aereo.fueraDeTiempo2b")); //semanas
        } else {
            return ((hours / 24 / 30) + " " + FacesUtils.getKeyResourceBundle("sgl.solicitudViaje.mensaje.warning.solicitar.aereo.fueraDeTiempo2c")); //meses
        }
    }

    public void openPopupCreateSgMotivo(ActionEvent actionEvent) {
        solicitudViajeBeanModel.inicioPopUpTrue("popupCreateSgMotivo");
        //sesion.getControladorPopups().put("popupCreateSgMotivo", Boolean.TRUE);
    }

    public void closePopupCreateSgMotivo(ActionEvent actionEvent) {
        setNombre(null);
        setNombre("");
        clearComponent("formPopupCreateSgMotivo", "inpTxtNombre");
        solicitudViajeBeanModel.inicioPopUpFalse("popupCreateSgMotivo");
        //sesion.getControladorPopups().put("popupCreateSgMotivo", Boolean.FALSE);
    }

    public void saveSgMotivo(ActionEvent actionEvent) {
        try {
            this.solicitudViajeBeanModel.saveSgMotivo();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sgl.solicitudViaje.motivo") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
            closePopupCreateSgMotivo(actionEvent);
        } catch (ExistingItemException eie) {
            FacesUtils.addErrorMessage("formPopupCreateSgMotivo:msgsPopupCreateSgMotivo", FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + ((SgMotivo) eie.getElemento()).getNombre());
            log(eie.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage("formPopupCreateSgMotivo:msgsPopupCreateSgMotivo", new SIAException().getMessage());
            log("en saveSgMotivo" + e.getMessage());
            UtilLog4j.log.error(e);
        }
    }

    /**
     * @return the viajeAereo
     */
    public boolean isViajeAereo() {
        return this.solicitudViajeBeanModel.isViajeAereo();
    }

    /**
     * @param viajeAereo the viajeAereo to set
     */
    public void setViajeAereo(boolean viajeAereo) {
        this.solicitudViajeBeanModel.setViajeAereo(viajeAereo);
    }

    /**
     * @return the optionViaje
     */
    public String getOptionViaje() {
        return this.solicitudViajeBeanModel.getOptionViaje();
    }

    /**
     * @param optionViaje the optionViaje to set
     */
    public void setOptionViaje(String optionViaje) {
        this.solicitudViajeBeanModel.setOptionViaje(optionViaje);
    }

    /**
     * @return the asistenteDireccion
     */
    public boolean isAsistenteDireccion() {
        return this.solicitudViajeBeanModel.isAsistenteDireccion();
    }

    /**
     * @param asistenteDireccion the asistenteDireccion to set
     */
    public void setAsistenteDireccion(boolean asistenteDireccion) {
        this.solicitudViajeBeanModel.setAsistenteDireccion(asistenteDireccion);
    }

    /**
     * @return the horaSalida
     */
    public int getHoraSalida() {
        return this.solicitudViajeBeanModel.getHoraSalida();
    }

    /**
     * @param horaSalida the horaSalida to set
     */
    public void setHoraSalida(int horaSalida) {
        this.solicitudViajeBeanModel.setHoraSalida(horaSalida);
    }

    /**
     * @return the horaRegreso
     */
    public int getHoraRegreso() {
        return this.solicitudViajeBeanModel.getHoraRegreso();
    }

    /**
     * @param horaRegreso the horaRegreso to set
     */
    public void setHoraRegreso(int horaRegreso) {
        this.solicitudViajeBeanModel.setHoraRegreso(horaRegreso);
    }

    /**
     * @return the minutoSalida
     */
    public int getMinutoSalida() {
        return this.solicitudViajeBeanModel.getMinutoSalida();
    }

    /**
     * @param minutoSalida the minutoSalida to set
     */
    public void setMinutoSalida(int minutoSalida) {
        this.solicitudViajeBeanModel.setMinutoSalida(minutoSalida);
    }

    /**
     * @return the minutoRegreso
     */
    public int getMinutoRegreso() {
        return this.solicitudViajeBeanModel.getMinutoRegreso();
    }

    /**
     * @param minutoRegreso the minutoRegreso to set
     */
    public void setMinutoRegreso(int minutoRegreso) {
        this.solicitudViajeBeanModel.setMinutoRegreso(minutoRegreso);
    }

    /**
     * @return the idOficinaOrigen
     */
    public int getIdOficinaOrigen() {
        return this.solicitudViajeBeanModel.getIdOficinaOrigen();
    }

    /**
     * @param idOficinaOrigen the idOficinaOrigen to set
     */
    public void setIdOficinaOrigen(int idOficinaOrigen) {
        this.solicitudViajeBeanModel.setIdOficinaOrigen(idOficinaOrigen);
    }

    /**
     * @return the idOficinaDestino
     */
    public int getIdOficinaDestino() {
        return this.solicitudViajeBeanModel.getIdOficinaDestino();
    }

    /**
     * @param idOficinaDestino the idOficinaDestino to set
     */
    public void setIdOficinaDestino(int idOficinaDestino) {
        this.solicitudViajeBeanModel.setIdOficinaDestino(idOficinaDestino);
    }

    /**
     * @return the idSgMotivo
     */
    public int getIdSgMotivo() {
        return this.solicitudViajeBeanModel.getIdSgMotivo();
    }

    /**
     * @param idSgMotivo the idSgMotivo to set
     */
    public void setIdSgMotivo(int idSgMotivo) {
        this.solicitudViajeBeanModel.setIdSgMotivo(idSgMotivo);
    }

    /**
     * @return the sgOficinaList
     */
    public List<SgOficina> getSgOficinaList() {
        return this.solicitudViajeBeanModel.getSgOficinaList();
    }

    /**
     * @param sgOficinaList the sgOficinaList to set
     */
    public void setSgOficinaList(List<SgOficina> sgOficinaList) {
        this.solicitudViajeBeanModel.setSgOficinaList(sgOficinaList);
    }

    /**
     * @return the sgMotivoList
     */
    public List<MotivoVo> getSgMotivoList() {
        return this.solicitudViajeBeanModel.getSgMotivoList();
    }

    /**
     * @param sgMotivoList the sgMotivoList to set
     */
    public void setSgMotivoList(List<MotivoVo> sgMotivoList) {
        this.solicitudViajeBeanModel.setSgMotivoList(sgMotivoList);
    }

    /**
     * @return the fechaSalida
     */
    public Date getFechaSalida() {
        return (this.solicitudViajeBeanModel.getFechaSalida() != null ? this.solicitudViajeBeanModel.getFechaSalida() : new Date());
    }

    /**
     * @param fechaSalida the fechaSalida to set
     */
    public void setFechaSalida(Date fechaSalida) {
        this.solicitudViajeBeanModel.setFechaSalida(fechaSalida != null ? fechaSalida : null);
    }

    /**
     * @return the fechaRegreso
     */
    public Date getFechaRegreso() {
        return (this.solicitudViajeBeanModel.getFechaRegreso() != null ? this.solicitudViajeBeanModel.getFechaRegreso() : getFechaSalida());
    }

    /**
     * @param fechaRegreso the fechaRegreso to set
     */
    public void setFechaRegreso(Date fechaRegreso) {
        this.solicitudViajeBeanModel.setFechaRegreso(fechaRegreso != null ? fechaRegreso : null);
    }

    /**
     * @return the observacion
     */
    public String getObservacion() {
        return this.solicitudViajeBeanModel.getObservacion();
    }

    /**
     * @param observacion the observacion to set
     */
    public void setObservacion(String observacion) {
        this.solicitudViajeBeanModel.setObservacion(observacion);
    }

    /**
     * @return the idSgTipoSolicitudViaje
     */
    public int getIdSgTipoSolicitudViaje() {
        return this.solicitudViajeBeanModel.getIdSgTipoSolicitudViaje();
    }

    /**
     * @param idSgTipoSolicitudViaje the idSgTipoSolicitudViaje to set
     */
    public void setIdSgTipoSolicitudViaje(int idSgTipoSolicitudViaje) {
        this.solicitudViajeBeanModel.setIdSgTipoSolicitudViaje(idSgTipoSolicitudViaje);
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
        return this.solicitudViajeBeanModel.getIdGerencia();
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
        this.solicitudViajeBeanModel.setIdGerencia(idGerencia);
    }

    /**
     * @return the sgTipoSolicitudViajeList
     */
    public List<SgTipoSolicitudViaje> getSgTipoSolicitudViajeList() {
        return this.solicitudViajeBeanModel.getSgTipoSolicitudViajeList();
    }

    /**
     * @param sgTipoSolicitudViajeList the sgTipoSolicitudViajeList to set
     */
    public void setSgTipoSolicitudViajeList(List<SgTipoSolicitudViaje> sgTipoSolicitudViajeList) {
        this.solicitudViajeBeanModel.setSgTipoSolicitudViajeList(sgTipoSolicitudViajeList);
    }

    /**
     * @return the siCiudadVOOrigen
     */
    public SiCiudadVO getSiCiudadVOOrigen() {
        return this.solicitudViajeBeanModel.getSiCiudadVOOrigen();
    }

    /**
     * @param siCiudadVOOrigen the siCiudadVOOrigen to set
     */
    public void setSiCiudadVOOrigen(SiCiudadVO siCiudadVOOrigen) {
        this.solicitudViajeBeanModel.setSiCiudadVOOrigen(siCiudadVOOrigen);
    }

    /**
     * @return the siCiudadVODestino
     */
    public SiCiudadVO getSiCiudadVODestino() {
        return this.solicitudViajeBeanModel.getSiCiudadVODestino();
    }

    /**
     * @param siCiudadVODestino the siCiudadVODestino to set
     */
    public void setSiCiudadVODestino(SiCiudadVO siCiudadVODestino) {
        this.solicitudViajeBeanModel.setSiCiudadVODestino(siCiudadVODestino);
    }

    /**
     * @return the cadena
     */
    public String getCadena() {
        return this.solicitudViajeBeanModel.getCadena();
    }

    /**
     * @param cadena the cadena to set
     */
    public void setCadena(String cadena) {
        this.solicitudViajeBeanModel.setCadena(cadena);
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return this.solicitudViajeBeanModel.getMensaje();
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.solicitudViajeBeanModel.setMensaje(mensaje);
    }

    /**
     * @return the siCiudadOrigenSelectItem
     */
    public List<SelectItem> getSiCiudadOrigenSelectItem() {
        return this.solicitudViajeBeanModel.getSiCiudadOrigenSelectItem();
    }

    /**
     * @param siCiudadOrigenSelectItem the siCiudadOrigenSelectItem to set
     */
    public void setSiCiudadOrigenSelectItem(List<SelectItem> siCiudadOrigenSelectItem) {
        this.solicitudViajeBeanModel.setSiCiudadOrigenSelectItem(siCiudadOrigenSelectItem);
    }

    /**
     * @return the siCiudadDestinoSelectItem
     */
    public List<SelectItem> getSiCiudadDestinoSelectItem() {
        return this.solicitudViajeBeanModel.getSiCiudadDestinoSelectItem();
    }

    /**
     * @param siCiudadDestinoSelectItem the siCiudadDestinoSelectItem to set
     */
    public void setSiCiudadDestinoSelectItem(List<SelectItem> siCiudadDestinoSelectItem) {
        this.solicitudViajeBeanModel.setSiCiudadDestinoSelectItem(siCiudadDestinoSelectItem);
    }

    /**
     * @return the optionPropia
     */
    public String getOptionPropia() {
        return this.solicitudViajeBeanModel.getOptionPropia();
    }

    /**
     * @param optionPropia the optionPropia to set
     */
    public void setOptionPropia(String optionPropia) {
        this.solicitudViajeBeanModel.setOptionPropia(optionPropia);
    }

    /**
     * @return the gerencia
     */
    public Gerencia getGerencia() {
        return this.solicitudViajeBeanModel.getGerencia();
    }

    /**
     * @param gerencia the gerencia to set
     */
    public void setGerencia(Gerencia gerencia) {
        this.solicitudViajeBeanModel.setGerencia(gerencia);
    }

    /**
     * @return the gerenciaList
     */
    public List<GerenciaVo> getGerenciaList() {
        return this.solicitudViajeBeanModel.getGerenciaList();
    }

    /**
     * @param gerenciaList the gerenciaList to set
     */
    public void setGerenciaList(List<GerenciaVo> gerenciaList) {
        this.solicitudViajeBeanModel.setGerenciaList(gerenciaList);
    }

    /**
     * @return the withEstancia
     */
    public boolean isWithEstancia() {
        return this.solicitudViajeBeanModel.isWithEstancia();
    }

    /**
     * @param withEstancia the withEstancia to set
     */
    public void setWithEstancia(boolean withEstancia) {
        this.solicitudViajeBeanModel.setWithEstancia(withEstancia);
    }

    /**
     * @return the optionEstancia
     */
    public String getOptionEstancia() {
        return this.solicitudViajeBeanModel.getOptionEstancia();
    }

    /**
     * @param optionEstancia the optionEstancia to set
     */
    public void setOptionEstancia(String optionEstancia) {
        this.solicitudViajeBeanModel.setOptionEstancia(optionEstancia);
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return this.solicitudViajeBeanModel.getNombre();
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.solicitudViajeBeanModel.setNombre(nombre);
    }

    /**
     * @return the sgOficina
     */
    public SgOficina getSgOficina() {
        return this.solicitudViajeBeanModel.getSgOficina();
    }

    /**
     * @param sgOficina the sgOficina to set
     */
    public void setSgOficina(SgOficina sgOficina) {
        this.solicitudViajeBeanModel.setSgOficina(sgOficina);
    }

    /**
     * @return the optionRangoSiCiudadOrigen
     */
    public String getOptionRangoSiCiudadOrigen() {
        return this.solicitudViajeBeanModel.getOptionRangoSiCiudadOrigen();
    }

    /**
     * @param optionRangoSiCiudadOrigen the optionRangoSiCiudadOrigen to set
     */
    public void setOptionRangoSiCiudadOrigen(String optionRangoSiCiudadOrigen) {
        this.solicitudViajeBeanModel.setOptionRangoSiCiudadOrigen(optionRangoSiCiudadOrigen);
    }

    /**
     * @return the optionRangoSiCiudadDestino
     */
    public String getOptionRangoSiCiudadDestino() {
        return this.solicitudViajeBeanModel.getOptionRangoSiCiudadDestino();
    }

    /**
     * @param optionRangoSiCiudadDestino the optionRangoSiCiudadDestino to set
     */
    public void setOptionRangoSiCiudadDestino(String optionRangoSiCiudadDestino) {
        this.solicitudViajeBeanModel.setOptionRangoSiCiudadDestino(optionRangoSiCiudadDestino);
    }

    /**
     * @return the idSiCiudadOrigen
     */
    public int getIdSiCiudadOrigen() {
        return this.solicitudViajeBeanModel.getIdSiCiudadOrigen();
    }

    /**
     * @param idSiCiudadOrigen the idSiCiudadOrigen to set
     */
    public void setIdSiCiudadOrigen(int idSiCiudadOrigen) {
        this.solicitudViajeBeanModel.setIdSiCiudadOrigen(idSiCiudadOrigen);
    }

    /**
     * @return the idSiCiudadDestino
     */
    public int getIdSiCiudadDestino() {
        return this.solicitudViajeBeanModel.getIdSiCiudadDestino();
    }

    /**
     * @param idSiCiudadDestino the idSiCiudadDestino to set
     */
    public void setIdSiCiudadDestino(int idSiCiudadDestino) {
        this.solicitudViajeBeanModel.setIdSiCiudadDestino(idSiCiudadDestino);
    }

    /**
     * @return the idDestino
     */
    public int getIdDestino() {
        return solicitudViajeBeanModel.getIdDestino();
    }

    /**
     * @param idDestino the idDestino to set
     */
    public void setIdDestino(int idDestino) {
        solicitudViajeBeanModel.setIdDestino(idDestino);
    }

    /**
     * @return the sgOficinaListAux
     */
    public List<SgOficina> getSgOficinaListAux() {
        return this.solicitudViajeBeanModel.getSgOficinaListAux();
    }

    /**
     * @param sgOficinaListAux the sgOficinaListAux to set
     */
    public void setSgOficinaListAux(List<SgOficina> sgOficinaListAux) {
        this.solicitudViajeBeanModel.setSgOficinaListAux(sgOficinaListAux);
    }

    /**
     * @return the operacion
     */
    public String getOperacion() {
        return solicitudViajeBeanModel.getOperacion();
    }

    /**
     * @param operacion the operacion to set
     */
    public void setOperacion(String operacion) {
        this.solicitudViajeBeanModel.setOperacion(operacion);
    }

    public boolean isSencillo() {
        return solicitudViajeBeanModel.isSencillo();
    }

    /**
     * @param sencillo the sencillo to set
     */
    public void setSencillo(boolean sencillo) {
        solicitudViajeBeanModel.setSencillo(sencillo);
    }

    /**
     * @param solicitudViajeBeanModel the solicitudViajeBeanModel to set
     */
    public void setSolicitudViajeBeanModel(SolicitudViajeBeanModel solicitudViajeBeanModel) {
        this.solicitudViajeBeanModel = solicitudViajeBeanModel;
    }

    /**
     * @return the semaforoVo
     */
    public SemaforoVo getSemaforoVo() {
        return solicitudViajeBeanModel.getSemaforoVo();
    }

    /**
     * @param semaforoVo the semaforoVo to set
     */
    public void setSemaforoVo(SemaforoVo semaforoVo) {
        solicitudViajeBeanModel.setSemaforoVo(semaforoVo);
    }

    /**
     * @return the tipoSolicitud
     */
    public String getTipoSolicitud() {
        return solicitudViajeBeanModel.getTipoSolicitud();
    }

    /**
     * @param tipoSolicitud the tipoSolicitud to set
     */
    public void setTipoSolicitud(String tipoSolicitud) {
        solicitudViajeBeanModel.setTipoSolicitud(tipoSolicitud);
    }

    /**
     * @return the solicitudViajeVO
     */
    public SolicitudViajeVO getSolicitudViajeVO() {
        return solicitudViajeBeanModel.getSolicitudViajeVO();
    }

    /**
     * @param solicitudViajeVO the solicitudViajeVO to set
     */
    public void setSolicitudViajeVO(SolicitudViajeVO solicitudViajeVO) {
        solicitudViajeBeanModel.setSolicitudViajeVO(solicitudViajeVO);
    }

    /**
     * @return the nombreRol
     */
    public String getNombreRol() {
        return solicitudViajeBeanModel.getNombreRol();
    }

    /**
     * @param nombreRol the nombreRol to set
     */
    public void setNombreRol(String nombreRol) {
        solicitudViajeBeanModel.setNombreRol(nombreRol);
    }

    /**
     * @return the idDetinoRuta
     */
    public int getIdDetinoRuta() {
        return solicitudViajeBeanModel.getIdDetinoRuta();
    }

    /**
     * @param idDetinoRuta the idDetinoRuta to set
     */
    public void setIdDetinoRuta(int idDetinoRuta) {
        solicitudViajeBeanModel.setIdDetinoRuta(idDetinoRuta);
    }

    /**
     * @return the listaDestinoRuta
     */
    public List<SelectItem> getListaDestinoRuta() {
        return solicitudViajeBeanModel.getListaDestinoRuta();
    }

    /**
     * @param listaDestinoRuta the listaDestinoRuta to set
     */
    public void setListaDestinoRuta(List<SelectItem> listaDestinoRuta) {
        solicitudViajeBeanModel.setListaDestinoRuta(listaDestinoRuta);
    }

    private void log(String mensaje) {
        UtilLog4j.log.info(this, mensaje);
    }

    public void viajeSencillo() {
        setCadena(Constantes.sencillo);
    }

    public void tipoDeViaje() {
        if (getCadena().equals(Constantes.redondo)) {
            setCadena(Constantes.sencillo);
        } else {
            setCadena(Constantes.redondo);
        }
    }

    public boolean getVerPantalla() {
        return solicitudViajeBeanModel.isPanelSV();
    }

    public DataModel getDataModelViajeros() {
        return this.solicitudViajeBeanModel.getDataModelViajeros();
    }

    public void setDataModelViajeros(DataModel dataModelViajeros) {
        this.solicitudViajeBeanModel.setDataModelViajeros(dataModelViajeros);
    }

    public String getListInvitados() {

        if (solicitudViajeBeanModel.getListaInvitados() == null || solicitudViajeBeanModel.getListaInvitados().isEmpty()) {
            solicitudViajeBeanModel.inicializarlistInvitados(true);
        }
        return solicitudViajeBeanModel.getListaInvitados();
    }

    public void modificarSolicitudViaje() {
        solicitudViajeBeanModel.setPanelSV(Constantes.TRUE);
        setOperacion(Constantes.modificar);
    }

    public void changeViajeroAddOrRemove(String idUsuario, int idInvitado, String addOrRemove) {

        solicitudViajeBeanModel.addOrRemoveViajeros(idUsuario, Constantes.param_ADD_OR_REMOVE.equals(addOrRemove), idInvitado);

    }

    public List<String> usuarioListener(String cadena) {
        solicitudViajeBeanModel.setEmpleado("");
        List<String> nombres = new ArrayList<>();
        List<UsuarioVO> usVos = solicitudViajeBeanModel.traerUsuarios(cadena);
        usVos.stream().forEach(us -> {
            nombres.add(us.getNombre());
        });
        return nombres;
    }

    //Cambio de joel 1 nov 22 - optimización de código
    public List<String> invitadoListener(String cadena) {
        solicitudViajeBeanModel.setInvitado("");
        /*List<String> nombres = new ArrayList<>();
        List<InvitadoVO> invs = solicitudViajeBeanModel.traerInvitados(cadena);
        invs.stream().forEach(us -> {
            nombres.add(us.getNombre() + " // " + us.getEmpresa());
        });*/
        
        final List<String> nombres = 
                solicitudViajeBeanModel.traerInvitados(cadena)
                                       .stream().map(item -> (item.getNombre()+ this.spliterInvitado +item.getEmpresa()))
                                       .collect(Collectors.toList());      
        
        return nombres;
    }

    public void addEmpleado() {
        UsuarioVO usVo = solicitudViajeBeanModel.buscarUsuario();
        if (usVo != null) {
            setEmpleadoAdd(usVo.getId());
            if (getEmpleadoAdd() == null || getEmpleadoAdd().isEmpty()) {
                FacesUtils.addErrorMessage("El empleado no se encuentra registrado en el sistema");
                UtilLog4j.log.info("El empleado no se encuentra registrado en el sistema");
            } else {
                solicitudViajeBeanModel.confirmarUsuarioYTelefono(0, getEmpleadoAdd());
            }
            solicitudViajeBeanModel.addOrRemoveViajeros(usVo.getId(), Constantes.TRUE, 0);
            solicitudViajeBeanModel.setEmpleado("");
            // solicitudViajeBeanModel.usuariosActivos(Constantes.CERO);
        }
    }

    public void addInvitado() {
        setEmpleadoAdd("");
        
        final String[] splitters = solicitudViajeBeanModel.getInvitado().split(spliterInvitado);
        
        final String criteria = (splitters != null && splitters.length > 0) ? splitters[0] : "";
        
        if(criteria.equals("")){
            FacesUtils.addInfoMessage("Escribe un nombre de invitado");
            return;
        }
        
        final InvitadoVO inVo = solicitudViajeBeanModel.buscarInvitado(criteria);
                              
        if (inVo != null) {
            
            setInvitadoAdd(inVo.getIdInvitado());

            if (getInvitadoAdd() > 0) {
                solicitudViajeBeanModel.addOrRemoveViajeros("", Constantes.TRUE, inVo.getIdInvitado());
                //solicitudViajeBeanModel.confirmarUsuarioYTelefono(getInvitadoAdd(), "");
                solicitudViajeBeanModel.confirmarUsuarioYTelefono(inVo.getIdInvitado(), "");
            } else {
                setNewInvitado(inVo.getNombre());
                //solicitudViajeBeanModel.empresas();
                setNewTelefono("");
                PrimeFaces.current().executeScript(";$(modalCrearInvitado).modal('show');");
            }
        }

        solicitudViajeBeanModel.setInvitado("");

        //solicitudViajeBeanModel.llenarInvitadoJson();
    }

    public void addViajero() {
        String tel = FacesUtils.getRequestParameter("telfonoNew");

        if (tel != null && !tel.isEmpty()) {
            getTemporal().setTelefono(tel);
            getTemporal().setConfirTel(Constantes.TRUE);
            solicitudViajeBeanModel.addOrRemoveViajeros(getEmpleadoAdd(), Constantes.TRUE, (getTemporal().getIdInvitado() != null ? getTemporal().getIdInvitado().intValue() : 0));
            PrimeFaces.current().executeScript(";$(modalAddTel).modal('hide');");
        } else {
            FacesUtils.addErrorMessage("Favor de agregar el Telefono");
        }

    }

    public void crearInvitado() {
        setNewInvitado(FacesUtils.getRequestParameter("newInv"));
        String empresa = FacesUtils.getRequestParameter("empresaNewInv");
        setNewTelefono(FacesUtils.getRequestParameter("telfonoNew"));

        if (!getNewInvitado().isEmpty() && !empresa.isEmpty() && !getNewTelefono().isEmpty()) {
            solicitudViajeBeanModel.crearInvitado(empresa.toUpperCase());
        }

    }

    /**
     * @return the origen
     */
    public String getOrigen() {
        return solicitudViajeBeanModel.getOrigen();
    }

    /**
     * @param origen the origen to set
     */
    public void setOrigen(String origen) {
        solicitudViajeBeanModel.setOrigen(origen);
    }

    /**
     * @return the destino
     */
    public String getDestino() {
        return solicitudViajeBeanModel.getDestino();
    }

    /**
     * @param destino the destino to set
     */
    public void setDestino(String destino) {
        solicitudViajeBeanModel.setDestino(destino);
    }

    /**
     * @return the dataModelOficina
     */
    public DataModel getDataModelOficina() {
        return solicitudViajeBeanModel.getDataModelOficina();
    }

    /**
     * @param dataModelOficina the dataModelOficina to set
     */
    public void setDataModelOficina(DataModel dataModelOficina) {
        this.solicitudViajeBeanModel.setDataModelOficina(dataModelOficina);
    }

    /**
     * @return the dataModelOficinaDestino
     */
    public DataModel getDataModelOficinaDestino() {
        return solicitudViajeBeanModel.getDataModelOficinaDestino();
    }

    /**
     * @param dataModelOficinaDestino the dataModelOficinaDestino to set
     */
    public void setDataModelOficinaDestino(DataModel dataModelOficinaDestino) {
        solicitudViajeBeanModel.setDataModelOficinaDestino(dataModelOficinaDestino);
    }

    /**
     * @return the dataModelCiudad
     */
    public DataModel getDataModelCiudad() {
        return solicitudViajeBeanModel.getDataModelCiudad();
    }

    /**
     * @param dataModelCiudad the dataModelCiudad to set
     */
    public void setDataModelCiudad(DataModel dataModelCiudad) {
        solicitudViajeBeanModel.setDataModelCiudad(dataModelCiudad);
    }

    public String getCompaneros() {

        if (solicitudViajeBeanModel.getListaEmpleados() == null || solicitudViajeBeanModel.getListaEmpleados().isEmpty()) {
            solicitudViajeBeanModel.usuariosActivos(Constantes.CERO);
        }
        return solicitudViajeBeanModel.getListaEmpleados();

    }

    /**
     * @return the tabEmpOInv
     */
    public boolean isTabEmpOInv() {
        return solicitudViajeBeanModel.isTabEmpOInv();
    }

    /**
     * @param tabEmpOInv the tabEmpOInv to set
     */
    public void setTabEmpOInv(boolean tabEmpOInv) {
        solicitudViajeBeanModel.setTabEmpOInv(tabEmpOInv);
    }

    public void esEmpleado() {

        setTabEmpOInv(Constantes.TRUE);
        solicitudViajeBeanModel.usuariosActivos(Constantes.CERO);
    }

    public void esInvitado() {
        setTabEmpOInv(Constantes.FALSE);
        solicitudViajeBeanModel.inicializarlistInvitados(true);
    }

    public void reiniciarComponentes() throws SIAException {

        setCadena(Constantes.redondo);
        setOrigen("Origen...");
        setIdOficinaOrigen(Constantes.CERO);
        setDestino("Destino...");
        setIdDestino(Constantes.CERO);
        setIdSgMotivo(-1);
        setObservacion("");
        setDataModelCiudad(new ListDataModel());
        setDataModelOficinaDestino(new ListDataModel());
        setFechaSalida(new Date());
        setFechaRegreso(new Date());

        solicitudViajeBeanModel.setPanelSV(Constantes.TRUE);
             
        // return "/vistas/sgl/viaje/solicitud/solicitudViaje";
    }
    
        public String cancelarCapturaSolicitudViaje() throws SIAException{
            
            reiniciarComponentes();
            
            return "/principal.xhtml?faces-redirect=true;";
    }

    public void visitoAEmpleado() {
        if (getEmpleadoAdd() == null || getEmpleadoAdd().isEmpty()) {
            FacesUtils.addErrorMessage("El empleado no se encuentra registrado en el sistema");
            UtilLog4j.log.info("El empleado no se encuentra registrado en el sistema");
        } else {
            solicitudViajeBeanModel.visito(Constantes.EMPLEADO);
           // PrimeFaces.current().executeScript(";$(modalFinalizar).modal('show');");
        }

    }

    public void visitoAOtro() {
        solicitudViajeBeanModel.visito(Constantes.INVITADO);
        //PrimeFaces.current().executeScript(";$(modalFinalizar).modal('show');");

    }

    /**
     * @return the visita
     */
    public InvitadoVO getVisita() {
        return solicitudViajeBeanModel.getVisita();
    }

    /**
     * @param visita the visita to set
     */
    public void setVisita(InvitadoVO visita) {
        solicitudViajeBeanModel.setVisita(visita);
    }

    /**
     * @return the justifica
     */
    public boolean isJustificaVisita() {
        return solicitudViajeBeanModel.isJustificaVisita();
    }

    /**
     * @param justifica the justifica to set
     */
    public void setJustificaVisita(boolean justifica) {
        this.solicitudViajeBeanModel.setJustificaVisita(justifica);
    }

    public void usuarioJson() {
        setCadena("");
        // String usuario = solicitudViajeBeanModel.usuariosJson();
        // JavascriptContext.addJavascriptCall((FacesContext.getCurrentInstance()),";llenarRuta('finalizarSolicitud',"+usuario+",'hidenDes', 'confirmaVistEmpleado','autocomplete');"); 
        setTabEmpOInv(true);
    }

    public void llenarInvitadoJson() {
        setIdVisito(Constantes.CERO);
        // String invitado = solicitudViajeBeanModel.llenarInvitadoJson();
        //PrimeFaces.current().executeScript(";llenarInvitado('formTerrestre'," + invitado + ", 'nombreInv');");
        //  JavascriptContext.addJavascriptCall((FacesContext.getCurrentInstance()),";llenarRuta('finalizarSolicitud',"+invitado+",'hidenDesInv', 'confirmaVistOtro','autocompleteInvi');"); 
        setTabEmpOInv(false);
    }

    /**
     * @return the idVisito
     */
    public int getIdVisito() {
        return solicitudViajeBeanModel.getIdVisito();
    }

    /**
     * @param idVisito the idVisito to set
     */
    public void setIdVisito(int idVisito) {
        solicitudViajeBeanModel.setIdVisito(idVisito);
    }

    public List<SelectItem> getSelectItemSgLugar() {
        List<SgLugar> sgLugarList = solicitudViajeBeanModel.getSgLugarList();
        List<SelectItem> listSelectItem = null;
        if (!sgLugarList.isEmpty()) {
            listSelectItem = new ArrayList<SelectItem>();
            for (SgLugar sgLugar : sgLugarList) {
                listSelectItem.add(new SelectItem(sgLugar.getId(), sgLugar.getNombre().toUpperCase()));
            }
        }
        return listSelectItem;
    }

    /**
     * @return the Justifica
     */
    public boolean isJustifica() {
        return solicitudViajeBeanModel.isJustifica();
    }

    /**
     * @param Justifica the Justifica to set
     */
    public void setJustifica(boolean Justifica) {
        solicitudViajeBeanModel.setJustifica(Justifica);
    }

    public void inicializarComponentes() throws SIAException {
        solicitudViajeBeanModel.inicializarComponetes();
    }

    public void solicitarSolicitud() {        
        try {
            boolean solicitado = false;
            if (isJustifica()) {
                if (getMensaje().length() < 50) {
                    FacesUtils.addErrorMessage("Deben de ser minimo 50 caracteres en la justificación");
                } else {
                    if (isConChofer()) {
                        solicitado = solicitudViajeBeanModel.solicitarViaje();
                        solicitudViajeBeanModel.setPanelSV(Constantes.TRUE);
                        PrimeFaces.current().executeScript(";$(modalFinalizar).modal('hide');");
                    } else {
                        solicitudViajeBeanModel.cargarVehiculo();
                        if (isConfirVehiculo()) {
                            solicitado = solicitudViajeBeanModel.solicitarViaje();
                            //InicializarComponentes();
                            // reiniciarComponentes();
                            solicitudViajeBeanModel.setPanelSV(Constantes.TRUE);
                            PrimeFaces.current().executeScript(";$(modalFinalizar).modal('hide');");
                        }
                    }
                }
            } else {
                if (isConChofer()) {
                    solicitado = solicitudViajeBeanModel.solicitarViaje();
                    //InicializarComponentes();
                    // reiniciarComponentes();
                    solicitudViajeBeanModel.setPanelSV(Constantes.TRUE);
                    PrimeFaces.current().executeScript(";$(modalFinalizar).modal('hide');");
                } else {
                    solicitudViajeBeanModel.cargarVehiculo();
                    if (isConfirVehiculo()) {
                        solicitado = solicitudViajeBeanModel.solicitarViaje();
                        //InicializarComponentes();
                        // reiniciarComponentes();
                        solicitudViajeBeanModel.setPanelSV(Constantes.TRUE);
                        PrimeFaces.current().executeScript(";$(modalFinalizar).modal('hide');");
                    }
                }
            }
            if (solicitado) {

                solicitudViajeBeanModel.setUrl("");
                //InicializarComponentes();
                solicitudViajeBeanModel.traerOficinaModal();
                PrimeFaces.current().executeScript(";$(modalFinalizar).modal('hide');");
            }
            FacesUtils.addInfoMessage("Solicitud completada.");

        } catch (Exception e) {
            UtilLog4j.log.info(this, " " + e.getMessage());
        }
    }

    /**
     * @return the idLugar
     */
    public int getIdLugar() {
        return solicitudViajeBeanModel.getIdLugar();
    }

    /**
     * @param idLugar the idLugar to set
     */
    public void setIdLugar(int idLugar) {
        solicitudViajeBeanModel.setIdLugar(idLugar);
    }

    public String actualizarListaViajeros() throws Exception {
        return solicitudViajeBeanModel.actualizarListaViajeros();
    }

    /**
     * @return the EmpleadoAdd
     */
    public String getEmpleadoAdd() {
        return solicitudViajeBeanModel.getEmpleadoAdd();
    }

    /**
     * @param EmpleadoAdd the EmpleadoAdd to set
     */
    public void setEmpleadoAdd(String EmpleadoAdd) {
        solicitudViajeBeanModel.setEmpleadoAdd(EmpleadoAdd);
    }

    /**
     * @return the addInvitado
     */
    public int getInvitadoAdd() {
        return solicitudViajeBeanModel.getAddInvitado();
    }

    /**
     * @param addInvitado the addInvitado to set
     */
    public void setInvitadoAdd(int addInvitado) {
        solicitudViajeBeanModel.setAddInvitado(addInvitado);
    }

    /**
     * @return the listaOrigenes
     */
    public List<List<Object[]>> getListaOrigenes() {
        return solicitudViajeBeanModel.getListaOrigenes();
    }

    public List<List<Object[]>> getListaDestinos() {
        return solicitudViajeBeanModel.getListaDestinos();
    }

    /**
     * @return the listViajeroVO
     */
    public List<ViajeroVO> getListViajeroVO() {
        return solicitudViajeBeanModel.getListViajeroVO();
    }

    /**
     * @param listViajeroVO the listViajeroVO to set
     */
    public void setListViajeroVO(List<ViajeroVO> listViajeroVO) {
        solicitudViajeBeanModel.setListViajeroVO(listViajeroVO);
    }

    /**
     * @return the viajaSolicitante
     */
    public boolean isViajaSolicitante() {
        return solicitudViajeBeanModel.isViajaSolicitante();
    }

    /**
     * @return the tieneVehiculo
     */
    public boolean isTieneVehiculo() {
        return solicitudViajeBeanModel.isTieneVehiculo();
    }

    public void vehiculoAsignado() {

        setConChofer(Constantes.FALSE);
        solicitudViajeBeanModel.vehiculosJson();
    }

    public void vehiculoNoAsignado() {

        setConChofer(Constantes.TRUE);
        PrimeFaces.current().executeScript(";limpiarDataListVehiculo();");
    }

    /**
     * @return the conChofer
     */
    public boolean isConChofer() {
        return solicitudViajeBeanModel.isconChofer();
    }

    /**
     * @param asignado the conChofer to set
     */
    public void setConChofer(boolean asignado) {
        solicitudViajeBeanModel.setconChofer(asignado);
    }

    /**
     * @return the vehiculoVO
     */
    public VehiculoVO getVehiculoVO() {
        return solicitudViajeBeanModel.getVehiculoVO();
    }

    /**
     * @param vehiculoVO the vehiculoVO to set
     */
    public void setVehiculoVO(VehiculoVO vehiculoVO) {
        solicitudViajeBeanModel.setVehiculoVO(vehiculoVO);
    }

    /**
     * @return the listVehiculoVO
     */
    public List<SelectItem> getListVehiculoVO() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        try {
            List<VehiculoVO> lv = solicitudViajeBeanModel.getListVehiculoVO();
            for (VehiculoVO sgV : lv) {
                l.add(new SelectItem(sgV.getId(), sgV.getMarca() + " - " + sgV.getModelo() + " - " + sgV.getNumeroPlaca() + " - " + sgV.getColor()));
            }
        } catch (Exception ex) {
            Logger.getLogger(SolicitudViajeBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return l;
    }

    /**
     * @return the newInvitado
     */
    public String getNewInvitado() {
        return solicitudViajeBeanModel.getNewInvitado();
    }

    /**
     * @param newInvitado the newInvitado to set
     */
    public void setNewInvitado(String newInvitado) {
        solicitudViajeBeanModel.setNewInvitado(newInvitado);
    }

    /**
     * @return the listSolicitudesVo
     */
    public List<SolicitudViajeVO> getListSolicitudesVo() {
        return solicitudViajeBeanModel.getListSolicitudesVo();
    }

    /**
     * @param listSolicitudesVo the listSolicitudesVo to set
     */
    public void setListSolicitudesVo(List<SolicitudViajeVO> listSolicitudesVo) {
        solicitudViajeBeanModel.setListSolicitudesVo(listSolicitudesVo);
    }

    public void aprobar(boolean tipo) {

        validaSeleccion(Constantes.FALSE, tipo);

        if (isValidaCheck()) {
            solicitudViajeBeanModel.aprobarSV(tipo);
        } else {
            FacesUtils.addErrorMessage("Debe de seleccionar al menos una solictud");
        }

    }

    public void activar() {
        validaSeleccion(Constantes.FALSE, Constantes.TRUE);
        if (isValidaCheck()) {
            solicitudViajeBeanModel.activarSv();
        } else {
            FacesUtils.addErrorMessage("Debe de seleccionar al menos una solictud");
        }

    }

    public void aprobar() {
        aprobar(Constantes.TRUE);
    }

    public void aprobarSVAereas() {
        aprobar(Constantes.FALSE);
    }

    public void aprobarJustificada() {

        setMensaje(FacesUtils.getRequestParameter("msjJustifica"));

        if (solicitudViajeBeanModel.aprobarWhitJustificacion()) {
            solicitudViajeBeanModel.mostrarSolicitudesByAprobar();
        } else {
            FacesUtils.addErrorMessage("Ocurrio un error al aprobar la solicitud, favor de comunicarse con soportesia@ihsa.mx");
        }
    }

    public void cancelar() {
        setMensaje(FacesUtils.getRequestParameter("msjCancelacion"));
        solicitudViajeBeanModel.cancelarSolictud();
        PrimeFaces.current().executeScript(";$(modalCancelar).modal('hide');");
    }

    public void validaSeleccion(boolean cancel, boolean gerenteAp) {
        solicitudViajeBeanModel.validaSeleccion(cancel, gerenteAp);
    }

    /**
     * @return the validaCheck
     */
    public boolean isValidaCheck() {
        return solicitudViajeBeanModel.isValidaCheck();
    }

    /**
     * @param validaCheck the validaCheck to set
     */
    public void setValidaCheck(boolean validaCheck) {
        solicitudViajeBeanModel.setValidaCheck(validaCheck);
    }

    public void popMotivoCancelar(boolean terrestre, boolean gerenteAp) {
        validaSeleccion(Constantes.TRUE, gerenteAp);
        if (isValidaCheck()) {
            PrimeFaces.current().executeScript(";$(modalCancelar).modal('show');");
        } else {
            FacesUtils.addErrorMessage("Debe de seleccionar al menos una Solicitud para poder Cancelar");
        }

    }

    public void popMotivoCancelar() {
        popMotivoCancelar(Constantes.TRUE, Constantes.TRUE);
    }

    public void popMotivoCancelarAereas() {
        popMotivoCancelar(Constantes.FALSE, Constantes.FALSE);
    }

    /**
     * @return the codigos
     */
    public String getCodigos() {
        return solicitudViajeBeanModel.getCodigos();
    }

    /**
     * @param codigos the codigos to set
     */
    public void setCodigos(String codigos) {
        solicitudViajeBeanModel.setCodigos(codigos);
    }

    /**
     * @return the listSolicitudesVoAereas
     */
    public List<SolicitudViajeVO> getListSolicitudesVoAereas() {
        return solicitudViajeBeanModel.getListSolicitudesVoAereas();
    }

    /**
     * @param listSolicitudesVoAereas the listSolicitudesVoAereas to set
     */
    public void setListSolicitudesVoAereas(List<SolicitudViajeVO> listSolicitudesVoAereas) {
        solicitudViajeBeanModel.setListSolicitudesVoAereas(listSolicitudesVoAereas);
    }

    public void addViajeroAereo() {
        solicitudViajeBeanModel.cargarListaViajeros(Constantes.FALSE);
    }

    public void addViajeroTerrestre() {
        solicitudViajeBeanModel.cargarListaViajeros(Constantes.TRUE);
    }

    public void removerViajero() {
        solicitudViajeBeanModel.removeViajeros();
    }

    public void addviajeros() {
        solicitudViajeBeanModel.addViajeros();
    }

    public void agregarOEliminarViajeros() throws Exception {
        solicitudViajeBeanModel.addEditOrRemoveViajeros();
    }

    public void addEscalaIda() {
        solicitudViajeBeanModel.addEscalas(Constantes.TRUE);
    }

    public void addEscalaVuelta() {
        solicitudViajeBeanModel.addEscalas(Constantes.FALSE);
    }

    public List<SelectItem> getAllSgAerolineaSelectItem() {
        List<SgAerolinea> list = this.solicitudViajeBeanModel.getAllSgAerolineaList();
        List<SelectItem> items = new ArrayList<SelectItem>();

        for (SgAerolinea aerolinea : list) {
            SelectItem si = new SelectItem(aerolinea.getId(), aerolinea.getNombre());
            items.add(si);
        }
        return items;
    }

    /**
     * @return the idAerolinia
     */
    public int getIdAerolinia() {
        return solicitudViajeBeanModel.getIdAerolinia();
    }

    /**
     * @param idAerolinia the idAerolinia to set
     */
    public void setIdAerolinia(int idAerolinia) {
        solicitudViajeBeanModel.setIdAerolinia(idAerolinia);
    }

    /**
     * @return the fechaSalida2
     */
    public Date getFechaSalida2() {
        return solicitudViajeBeanModel.getFechaSalida2();
    }

    /**
     * @param fechaSalida2 the fechaSalida2 to set
     */
    public void setFechaSalida2(Date fechaSalida2) {
        solicitudViajeBeanModel.setFechaSalida2(fechaSalida2);
    }

    /**
     * @return the fechaRegreso2
     */
    public Date getFechaRegreso2() {
        return solicitudViajeBeanModel.getFechaRegreso2();
    }

    /**
     * @param fechaRegreso2 the fechaRegreso2 to set
     */
    public void setFechaRegreso2(Date fechaRegreso2) {
        solicitudViajeBeanModel.setFechaRegreso2(fechaRegreso2);
    }

    /**
     * @return the horaSalida2
     */
    public int getHoraSalida2() {
        return solicitudViajeBeanModel.getHoraSalida2();
    }

    /**
     * @param horaSalida2 the horaSalida2 to set
     */
    public void setHoraSalida2(int horaSalida2) {
        solicitudViajeBeanModel.setHoraSalida2(horaSalida2);
    }

    /**
     * @return the horaRegreso2
     */
    public int getHoraRegreso2() {
        return solicitudViajeBeanModel.getHoraSalida();
    }

    /**
     * @param horaRegreso2 the horaRegreso2 to set
     */
    public void setHoraRegreso2(int horaRegreso2) {
        solicitudViajeBeanModel.setHoraRegreso2(horaRegreso2);
    }

    /**
     * @return the minutoSalida2
     */
    public int getMinutoSalida2() {
        return solicitudViajeBeanModel.getMinutoSalida2();
    }

    /**
     * @param minutoSalida2 the minutoSalida2 to set
     */
    public void setMinutoSalida2(int minutoSalida2) {
        solicitudViajeBeanModel.setMinutoSalida2(minutoSalida2);
    }

    /**
     * @return the minutoRegreso2
     */
    public int getMinutoRegreso2() {
        return solicitudViajeBeanModel.getMinutoRegreso2();
    }

    /**
     * @param minutoRegreso2 the minutoRegreso2 to set
     */
    public void setMinutoRegreso2(int minutoRegreso2) {
        solicitudViajeBeanModel.setMinutoRegreso2(minutoRegreso2);
    }

    /**
     * @return the idAerolinia2
     */
    public int getIdAerolinia2() {
        return solicitudViajeBeanModel.getIdAerolinia2();
    }

    /**
     * @param idAerolinia2 the idAerolinia2 to set
     */
    public void setIdAerolinia2(int idAerolinia2) {
        solicitudViajeBeanModel.setIdAerolinia2(idAerolinia2);
    }

    public void removerEcalaIda() {
        solicitudViajeBeanModel.removerEscala(Constantes.TRUE);
    }

    public void removerEcalaVuejta() {
        solicitudViajeBeanModel.removerEscala(Constantes.FALSE);
    }

    public void guardarCambiosEscala() throws ItemUsedBySystemException {
        solicitudViajeBeanModel.guardarCambiosEscala();
    }

    public void notificarIda() {
        solicitudViajeBeanModel.notificarItinerario(Constantes.TRUE);
    }

    public void notificarVuelta() {
        solicitudViajeBeanModel.notificarItinerario(Constantes.FALSE);
    }

    public void selecionarTodoAereo(ValueChangeEvent e) {

        boolean select = ((Boolean) e.getNewValue());
        if (select) {
            solicitudViajeBeanModel.seleccionoTodo(Constantes.FALSE);
        } else {
            solicitudViajeBeanModel.desSelecinarTodo(Constantes.FALSE);
        }

    }

    public void selecionarTodoTer(ValueChangeEvent e) {

        boolean select = ((Boolean) e.getNewValue());
        if (select) {
            solicitudViajeBeanModel.seleccionoTodo(Constantes.TRUE);
        } else {
            solicitudViajeBeanModel.desSelecinarTodo(Constantes.TRUE);
        }
    }

    public void selecionarTodoTer2(ValueChangeEvent e) {

        boolean select = ((Boolean) e.getNewValue());
        if (select) {
            solicitudViajeBeanModel.seleccionoTodo(Constantes.TRUE);
        } else {
            solicitudViajeBeanModel.desSelecinarTodo(Constantes.TRUE);
        }
    }

    /**
     * @return the selectTodo
     */
    public boolean isSelectTodo() {
        return solicitudViajeBeanModel.isSelectTodo();
    }

    /**
     * @param selectTodo the selectTodo to set
     */
    public void setSelectTodo(boolean selectTodo) {
        solicitudViajeBeanModel.setSelectTodo(selectTodo);
    }

    public void selectOneToOne(ValueChangeEvent e) {

        boolean select = ((Boolean) e.getNewValue());
        setSolicitudViajeVO((SolicitudViajeVO) getLisTerrestre().getRowData());
//        if (select){
//          solicitudViajeBeanModel.seleccionoTodo(Constantes.TRUE);  
//        } else {
//            solicitudViajeBeanModel.desSelecinarTodo(Constantes.TRUE);
//        }
    }

    /**
     * @return the lisTerrestre
     */
    public UIData getLisTerrestre() {
        return lisTerrestre;
    }

    /**
     * @param lisTerrestre the lisTerrestre to set
     */
    public void setLisTerrestre(UIData lisTerrestre) {
        this.lisTerrestre = lisTerrestre;
    }

    /**
     * @return the listAerea
     */
    public UIData getListAerea() {
        return listAerea;
    }

    /**
     * @param listAerea the listAerea to set
     */
    public void setListAerea(UIData listAerea) {
        this.listAerea = listAerea;
    }

    public void confirmarTelefon() {
        solicitudViajeBeanModel.confirmarTelefon();
    }

    public void editarTelefono() {
        solicitudViajeBeanModel.editarTel();
    }

    /**
     * @return the newTelefono
     */
    public String getNewTelefono() {
        return solicitudViajeBeanModel.getNewTelefono();
    }

    /**
     * @param newTelefono the newTelefono to set
     */
    public void setNewTelefono(String newTelefono) {
        solicitudViajeBeanModel.setNewTelefono(newTelefono);
    }

    public void addNewtelefono(ActionEvent event) {
        solicitudViajeBeanModel.addNewTelefono();

    }

    /**
     * @return the temporal
     */
    public ViajeroVO getTemporal() {
        return solicitudViajeBeanModel.getTemporal();
    }

    /**
     * @param temporal the temporal to set
     */
    public void setTemporal(ViajeroVO temporal) {
        solicitudViajeBeanModel.setTemporal(temporal);
    }

    /**
     * @return the VehiculoActual
     */
    public String getVehiculoActual() {
        return solicitudViajeBeanModel.getVehiculoActual();
    }

    /**
     * @param VehiculoActual the VehiculoActual to set
     */
    public void setVehiculoActual(String VehiculoActual) {
        solicitudViajeBeanModel.setVehiculoActual(VehiculoActual);
    }

    /**
     * @return the confirVehiculo
     */
    public boolean isConfirVehiculo() {
        return solicitudViajeBeanModel.isConfirVehiculo();
    }

    /**
     * @param confirVehiculo the confirVehiculo to set
     */
    public void setConfirVehiculo(boolean confirVehiculo) {
        solicitudViajeBeanModel.setConfirVehiculo(confirVehiculo);
    }

    /**
     * @return the cambiarVehiculo
     */
    public boolean isCambiarVehiculo() {
        return solicitudViajeBeanModel.isCambiarVehiculo();
    }

    /**
     * @param cambiarVehiculo the cambiarVehiculo to set
     */
    public void setCambiarVehiculo(boolean cambiarVehiculo) {
        solicitudViajeBeanModel.setCambiarVehiculo(cambiarVehiculo);
    }

    public void editarVehiculo() {
        setCambiarVehiculo(Constantes.TRUE);
        solicitudViajeBeanModel.vehiculosJson();

    }

    public void confirVehiculoActual() {
        setConfirVehiculo(Constantes.TRUE);
    }

    public void cerrarConfirmacion() {
        //vehiculoNoAsignado();
        PrimeFaces.current().executeScript(";limpiarDataListVehiculo();");
        setCambiarVehiculo(Constantes.FALSE);
    }

    /**
     * @return the ubicacion
     */
    public List<SelectItem> getUbicacion() {
        return solicitudViajeBeanModel.getUbicacion();
    }

    /**
     * @param ubicacion the ubicacion to set
     */
    public void setUbicacion(List<SelectItem> ubicacion) {
        solicitudViajeBeanModel.setUbicacion(ubicacion);
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return solicitudViajeBeanModel.getDireccion();
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(String direccion) {
        solicitudViajeBeanModel.setDireccion(direccion);
    }

    /**
     * @return the hotelSugerido
     */
    public String getHotelSugerido() {
        return solicitudViajeBeanModel.getHotelSugerido();
    }

    /**
     * @param hotelSugerido the hotelSugerido to set
     */
    public void setHotelSugerido(String hotelSugerido) {
        solicitudViajeBeanModel.setHotelSugerido(hotelSugerido);
    }

    public void seleccinaZona(ValueChangeEvent event) {

        int ubicacionSel = (Integer) event.getNewValue();

        if (ubicacionSel > 0) {
            setIdUbicacion(ubicacionSel);
        }
    }

    /**
     * @return the idUbicacion
     */
    public int getIdUbicacion() {
        return solicitudViajeBeanModel.getIdUbicacion();
    }

    /**
     * @param idUbicacion the idUbicacion to set
     */
    public void setIdUbicacion(int idUbicacion) {
        solicitudViajeBeanModel.setIdUbicacion(idUbicacion);
    }

    /**
     * @return the countSVT
     */
    public int getCountSVT() {
        return solicitudViajeBeanModel.getCountSVT();
    }

    /**
     * @param countSVT the countSVT to set
     */
    public void setCountSVT(int countSVT) {
        solicitudViajeBeanModel.setCountSVT(countSVT);
    }

    /**
     * @return the countSVA
     */
    public int getCountSVA() {
        return solicitudViajeBeanModel.getCountSVA();
    }

    /**
     * @param countSVA the countSVA to set
     */
    public void setCountSVA(int countSVA) {
        solicitudViajeBeanModel.setCountSVA(countSVA);
    }

    /**
     * @return the listCampos
     */
    public List<SelectItem> getListEmpresaByUser() {
        return solicitudViajeBeanModel.getListEmpresaByUser();
    }

    /**
     * @param listCampos the listCampos to set
     */
    public void setListEmpresaByUser(List<SelectItem> listCampos) {
        solicitudViajeBeanModel.setListEmpresaByUser(listCampos);
    }

    /**
     * @return the idCampoActual
     */
    public int getIdCampoActual() {
        return solicitudViajeBeanModel.getIdCampoActual();
    }

    /**
     * @param idCampoActual the idCampoActual to set
     */
    public void setIdCampoActual(int idCampoActual) {
        solicitudViajeBeanModel.setIdCampoActual(idCampoActual);
    }

    /**
     * @return the rfcEmpresaSeleccionada
     */
    public String getRfcEmpresaSeleccionada() {
        return solicitudViajeBeanModel.getRfcEmpresaSeleccionada();
    }

    public void cambiarEmpresa() {
        solicitudViajeBeanModel.traerOficinaModal();
        solicitudViajeBeanModel.listaDestino();
    }

    /**
     * @param rfcEmpresaSeleccionada the rfcEmpresaSeleccionada to set
     */
    public void setRfcEmpresaSeleccionada(String rfcEmpresaSeleccionada) {
        solicitudViajeBeanModel.setRfcEmpresaSeleccionada(rfcEmpresaSeleccionada);
    }

    /**
     * @return the listDias
     */
    public List<SiDiasAsueto> getListDias() {
        return solicitudViajeBeanModel.getListDias();
    }

    /**
     * @param listDias the listDias to set
     */
    public void setListDias(List<SiDiasAsueto> listDias) {
        solicitudViajeBeanModel.setListDias(listDias);
    }

    /**
     * @return the empleado
     */
    public String getEmpleado() {
        return solicitudViajeBeanModel.getEmpleado();
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(String empleado) {
        solicitudViajeBeanModel.setEmpleado(empleado);
    }

    /**
     * @return the invitado
     */
    public String getInvitado() {
        return solicitudViajeBeanModel.getInvitado();
    }

    /**
     * @param invitado the invitado to set
     */
    public void setInvitado(String invitado) {
        solicitudViajeBeanModel.setInvitado(invitado);
    }

}
