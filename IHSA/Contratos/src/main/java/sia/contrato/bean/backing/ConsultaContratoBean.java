/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package sia.contrato.bean.backing;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import org.primefaces.shaded.json.JSONException;
import org.primefaces.shaded.json.JSONObject;
import sia.constantes.Constantes;
import sia.ihsa.contratos.Sesion;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.FiltroVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sistema.vo.CategoriaVo;
import sia.modelo.vo.StatusVO;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvClasificacionImpl;
import sia.servicios.convenio.impl.CvCondicionPagoImpl;
import sia.servicios.convenio.impl.CvConvenioAdjuntoImpl;
import sia.servicios.convenio.impl.CvConvenioArticuloImpl;
import sia.servicios.convenio.impl.CvConvenioCondicionPagoImpl;
import sia.servicios.convenio.impl.CvConvenioDocumentoImpl;
import sia.servicios.convenio.impl.CvConvenioGerenciaImpl;
import sia.servicios.convenio.impl.CvConvenioHitoImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.vo.MonedaVO;
import sia.util.OrdenEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named(value = "consultaContratoBean")
@ViewScoped
public class ConsultaContratoBean implements Serializable {

    static final long serialVersionUID = 1;

    /**
     * Creates a new instance of ConsultaContratoBean
     */
    public ConsultaContratoBean() {
    }
    @Inject
    private Sesion sesion;
    @Getter
    @Setter
    private String mesAnio;
    @Getter
    @Setter
    private ContratoVO contratoVo;
    @Getter
    @Setter
    private List<ContratoVO> contratos;
    @Getter
    @Setter
    private List<ContratoVO> lstConveniosTabs;
    @Getter
    @Setter
    private TabView tabView;
    @Getter
    @Setter
    private int activeTab;
    @Getter
    @Setter
    private int indice;

    @Inject
    private CvConvenioArticuloImpl convenioArticuloImpl;
    @Getter
    @Setter
    private List<FiltroVo> listaFiltro;
    @Getter
    @Setter
    private FiltroVo filtroVo;
    @Inject
    private EstatusImpl estatusImpl;
    @Inject
    private ConvenioImpl convenioImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private MonedaImpl monedaImpl;
    @Inject
    private CvConvenioGerenciaImpl cvConvenioGerenciaImpl;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenImpl;
    @Inject
    private CvConvenioDocumentoImpl cvConvenioDocumentoImpl;
    @Inject
    private CvConvenioCondicionPagoImpl cvConvenioCondicionPagoImpl;
    @Inject
    private CvCondicionPagoImpl cvCondicionPagoImpl;
    @Inject
    private CvConvenioHitoImpl cvConvenioHitoImpl;
    @Inject
    private CvConvenioAdjuntoImpl cvConvenioAdjuntoImpl;
    @Inject
    private CvClasificacionImpl cvClasificacionImpl;

    @PostConstruct
    public void iniciar() {
        contratoVo = new ContratoVO();
        contratos = new ArrayList<>();
        lstConveniosTabs = new ArrayList<>();
        listaFiltro = new ArrayList<>();
        activeTab = 0;
        iniciarLista();
    }

    private List<SelectItem> listaEstatus() {
        List<StatusVO> le = estatusImpl.traerPorTipo("CON");
        List<SelectItem> lseItems = new ArrayList<>();
        for (StatusVO le1 : le) {
            lseItems.add(new SelectItem(le1.getIdStatus(), le1.getNombre()));
        }
        return lseItems;
    }

    private List<SelectItem> listaGererencia() {
        List<GerenciaVo> le = gerenciaImpl.traerGerenciaActiva(sesion.getUsuarioSesion().getRfcEmpresa(), sesion.getUsuarioSesion().getIdCampo());

        List<SelectItem> lseItems = new ArrayList<>();
        for (GerenciaVo le1 : le) {
            lseItems.add(new SelectItem(le1.getId(), le1.getNombre()));
        }
        return lseItems;
    }

    private List<SelectItem> listaMoneda() {
        List<MonedaVO> le = monedaImpl.traerMonedaActiva(sesion.getUsuarioSesion().getIdCampo());
        List<SelectItem> lseItems = new ArrayList<>();
        lseItems.add(new SelectItem(-1, "Todas"));
        for (MonedaVO le1 : le) {
            lseItems.add(new SelectItem(le1.getId(), le1.getSiglas()));
        }
        return lseItems;
    }

    public void agregarOperadorLogicoCondiciones(int index, String opLogSel) {
        listaFiltro.get(index).setOperadorLogicoSeleccionado(opLogSel);
    }

    public void mostrarCondiciones(int index, String opLogSel) {
//	JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(),
//		";ejecutarFiltros('frmFiltroBuscar','btnPasarParametro" + getIndice() + "');");
        //
        filtroVo = listaFiltro.get(index);
        filtroVo.setCampoSeleccionado(opLogSel);
        if (filtroVo.getCampoSeleccionado().equals("")) {
            filtroVo.setFiltroCaja(true);
            filtroVo.setFiltroCombo(false);
            filtroVo.setFiltroFecha(false);
        } else if (filtroVo.getCampoSeleccionado().equals("Vencimiento")) {
            // mostrar fecha
            filtroVo.setFiltroCombo(false);
            filtroVo.setFiltroFecha(true);
            filtroVo.setFiltroCaja(false);
            filtroVo.setOperadorRelacional(new ArrayList<>());
            //
            filtroVo.getOperadorRelacional().add("Igual a ");
            filtroVo.getOperadorRelacional().add("Menor a ");
            filtroVo.getOperadorRelacional().add("Menor igual a ");
            filtroVo.getOperadorRelacional().add("Mayor  a ");
            filtroVo.getOperadorRelacional().add("Mayor igual a ");
            filtroVo.getOperadorRelacional().add("Entre ");
        } else if (filtroVo.getCampoSeleccionado().equals("Estado")) {
            // mostrar combo
            filtroVo.setListaEstatus(new ArrayList<>());
            filtroVo.setListaEstatus(listaEstatus());
            //
            filtroVo.setFiltroCombo(true);
            filtroVo.setFiltroFechaRango(false);
            filtroVo.setFiltroFecha(false);
            filtroVo.setFiltroCaja(false);
            filtroVo.setOperadorRelacional(new ArrayList<>());
            //
            filtroVo.getOperadorRelacional().add("Igual a ");
            filtroVo.getOperadorRelacional().add("Menor a ");
            filtroVo.getOperadorRelacional().add("Menor igual a ");
            filtroVo.getOperadorRelacional().add("Mayor  a ");
            filtroVo.getOperadorRelacional().add("Mayor igual a ");
            //	    llenar los estados
        } else if (filtroVo.getCampoSeleccionado().equals("Gerencia")) {
            // mostrar combo
            filtroVo.setListaGerencia(new ArrayList<>());
            filtroVo.setListaGerencia(listaGererencia());
            //
            filtroVo.setFiltroCombo(true);
            filtroVo.setFiltroFecha(false);
            filtroVo.setFiltroCaja(false);
            filtroVo.setFiltroFechaRango(false);
            filtroVo.setOperadorRelacional(new ArrayList<>());
            //
            filtroVo.getOperadorRelacional().add("Igual a ");
        } else if (filtroVo.getCampoSeleccionado().equals("Monto")) {
            filtroVo.setListaMoneda(new ArrayList<>());
            filtroVo.setListaMoneda(listaMoneda());
            filtroVo.setFiltroCombo(false);
            filtroVo.setFiltroFecha(false);
            filtroVo.setFiltroCaja(true);
            filtroVo.setFiltroMoneda(true);
            filtroVo.setFiltroFechaRango(false);
            filtroVo.setOperadorRelacional(new ArrayList<>());
            //
            filtroVo.getOperadorRelacional().add("Igual a ");
            filtroVo.getOperadorRelacional().add("Menor a ");
            filtroVo.getOperadorRelacional().add("Menor igual a ");
            filtroVo.getOperadorRelacional().add("Mayor  a ");
            filtroVo.getOperadorRelacional().add("Mayor igual a ");
        } else {
            // mostrar caja y operador cadena
            filtroVo.setOperadorRelacional(new ArrayList<>());
            //
            filtroVo.setFiltroFechaRango(false);
            filtroVo.setFiltroCaja(true);
            filtroVo.setFiltroCombo(false);
            filtroVo.setFiltroFecha(false);
            filtroVo.getOperadorRelacional().add("Contiene ");
        }
        //filtroVo.setOperadorRelacional(new ArrayList<>());
        //
        filtroVo.setOperadorRelacionalCadena(new ArrayList<>());
//	filtroVo.getOperadorRelacionalCadena().add("Contiene ");
        ///
        listaFiltro.set(filtroVo.getId(), filtroVo);
        //setIndice(Constantes.MENOS_UNO);
    }

    public void seleccionarOperadorRelacional(int index, String opRelSel) {
        filtroVo = listaFiltro.get(index);
        filtroVo.setOperadorRelacionalSeleccionado(opRelSel);
        if (filtroVo.getCampoSeleccionado().equals("Vencimiento")) {
            if (filtroVo.getOperadorRelacionalSeleccionado().trim().equals("Entre")) {
                filtroVo.setFiltroFechaRango(true);
                filtroVo.setFiltroFecha(false);
            } else {
                filtroVo.setFiltroFechaRango(false);
                filtroVo.setFiltroFecha(true);
            }

        } else {
            filtroVo.setFiltroFechaRango(false);
        }
        listaFiltro.set(filtroVo.getId(), filtroVo);
    }

    public void pasarParametro() {
        //setIndice(Integer.parseInt(FacesUtils.getRequestParam("indice")));
        //filtroVo = listaFiltro.get(indice);
    }

    public void agregarNuevoFiltro() {
        filtroVo = new FiltroVo();
        filtroVo.setId(listaFiltro.size());
        filtroVo.setCampos(new ArrayList<>());
        //filtroVo.getCampos().add("");
        filtroVo.getCampos().add("Proveedor");
        filtroVo.getCampos().add("Estado");
        filtroVo.getCampos().add("Gerencia");
        filtroVo.getCampos().add("Monto");
        filtroVo.getCampos().add("Vencimiento");
        filtroVo.getCampos().add("Número");
        filtroVo.getCampos().add("Nombre");
//
        filtroVo.setOperadorLogico(new ArrayList<>());
        filtroVo.getOperadorLogico().add(" Y ");
        filtroVo.getOperadorLogico().add(" O ");
        //
        filtroVo.setOperadorRelacional(new ArrayList<>());
        filtroVo.getOperadorRelacional().add("Contiene ");
        //
        filtroVo.setFiltroCaja(true);
        listaFiltro.add(filtroVo.getId(), filtroVo);
    }

    public void quitarFiltro(int id) {
        //
        reasiganarId(listaFiltro, id);
        // llenar la lista
        if (listaFiltro.size() > 1) {
            contratos = (convenioImpl.traerContratosBusqueda(listaFiltro, sesion.getUsuarioSesion().getId(), sesion.getIdRol(), sesion.getUsuarioSesion().getIdCampo()));
        }
    }

    public void quitarFiltroContacto(int id) {
        reasiganarId(listaFiltro, id);
    }

    private void reasiganarId(List<FiltroVo> filtros, int idBorrado) {
        listaFiltro.remove(filtros.get(idBorrado));
        //List<FiltroVo> temList = new ArrayList<FiltroVo>();
        for (int i = idBorrado; i < filtros.size(); i++) {
            FiltroVo filtro = filtros.get(i);
            filtro.setId(i);
            listaFiltro.set(i, filtro);
        }
    }

    public void limpiarBusqueda() {//as
        iniciarLista();
    }

    public void buscarAvanzado() {
        contratos = (convenioImpl.traerContratosBusqueda(listaFiltro, sesion.getUsuarioSesion().getId(), sesion.getIdRol(), sesion.getUsuarioSesion().getIdCampo()));
    }

    private void iniciarLista() {
        filtroVo = new FiltroVo();
        setListaFiltro(new ArrayList<>());
        filtroVo.setId(Constantes.CERO);
        filtroVo.setCampos(new ArrayList<>());
        // filtroVo.getCampos().add("");
        filtroVo.getCampos().add("Proveedor");
        filtroVo.getCampos().add("Estado");
        filtroVo.getCampos().add("Gerencia");
        filtroVo.getCampos().add("Monto");
        filtroVo.getCampos().add("Vencimiento");
        filtroVo.getCampos().add("Número");
        filtroVo.getCampos().add("Nombre");
        //
        filtroVo.setOperadorRelacional(new ArrayList<>());
        filtroVo.getOperadorRelacional().add("Contiene ");
        //
        filtroVo.setFiltroCaja(true);
        listaFiltro.add(filtroVo.getId(), filtroVo);
    }

    public void consultarContrato(SelectEvent<ContratoVO> event) {
        ContratoVO con = (ContratoVO) event.getObject();
        int index = getLstConveniosTabs().size();
        setIndice(index);
        contratoVo = convenioImpl.buscarPorId(con.getId(), sesion.getUsuarioSesion().getIdCampo(), sesion.getUsuarioSesion().getId(), Boolean.TRUE);
        contratoVo.setCodigo(con.getNumero());
        getLstConveniosTabs().add(index, contratoVo);
        contratoVo.setProveedorVo(new ProveedorVo());
        contratoVo.getProveedorVo().setTodoContactos(new ArrayList<>());
        contratos.remove(con);
        llenarDatosContrato(con.getId());
        //Activar tabl
        //PrimeFaces.current().executeScript(";activarTab('" + con.getNombreTab().trim() + "');");
        //
        //ocsPorConvenio(index);
        activeTab = index + 1;
        tabView.setActiveIndex(activeTab);

    }

    private void llenarDatosContrato(int con) {
        contratoVo.setListaConvenioDocumento(cvConvenioDocumentoImpl.traerDoctosPorConveni(con, null, null));
        contratoVo.setListaConvenioCondicion(cvConvenioCondicionPagoImpl.traerCondicionesPago(con));

        hitosPorConvenio(con);
        traerArchivosConvenio();
        //
        traerDatosProveedor();
        //	traerContratos relacionados
        traerContratosRelacionados(con);
        //
        traerDiasYRemanente(con);
        //
        traerConvenoArticulo(con);
        //
        ocsPorConvenio();
        buscarContratoPorGrafica();
        traerGerenciaContrato(con);
    }

    public void hitosPorConvenio(int cont) {
        contratoVo.setListaConvenioHito(cvConvenioHitoImpl.traerHitosPorConvenio(cont));
    }

    public void buscarContratoPorGrafica() {
        if (mesAnio != null) {
            String[] cad = getMesAnio().split("-");
            List<OrdenVO> lo = autorizacionesOrdenImpl.traerOrdenSolicidasPorMesAnio(Integer.parseInt(cad[0]), Integer.parseInt(cad[1]), OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId(), sesion.getUsuarioSesion().getIdCampo(), contratoVo.getNumero());
            contratoVo.setListaOrdenConvenio(lo);
        }

    }

    public void ocsPorConvenio() {
        try {
            List<ContratoVO> lo = traerOCSConvenio();
            if (lo != null && !lo.isEmpty()) {
                llenarOCSConvenio();
                JSONObject j = new JSONObject();
                String json;
                List<String> u = new ArrayList<>();
                List<Long> total = new ArrayList<>();
                List<Double> totalMes = new ArrayList<>();
                for (ContratoVO ordenVO : lo) {
                    u.add(ordenVO.getMes() + "-" + ordenVO.getAnio());
                    total.add(ordenVO.getTotalOCS());
                    totalMes.add(ordenVO.getTotalMes());
                }
                //
                j.put("fecha", u);
                j.put("total", total);
                j.put("totalMes", totalMes);
                json = j.toString();
                //
                PrimeFaces.current().executeScript(";grafica(" + json
                        + ",'" + contratoVo.getNumero() + "'"
                        + ", 'frmOcsConvenio" + contratoVo.getId() + "'"
                        + ", 'graficaOCSConvenio" + contratoVo.getId() + "'"
                        + ", 'txtMesAnio" + contratoVo.getId() + "'"
                        + ", 'btnBuscar" + contratoVo.getId() + "'"
                        + ", " + contratoVo.getAcumulado()
                        + ", '" + (contratoVo.getMonto() - contratoVo.getAcumulado()) + "'"
                        + ");");
            } else {
                PrimeFaces.current().executeScript(
                        ";ocultarDiv('graficaOCSConvenio"
                        + contratoVo.getId()
                        + "');;"
                );
            }
        } catch (JSONException ex) {
            UtilLog4j.log.error(ex);
        }

    }

    public void llenarOCSConvenio() {
        contratoVo.setListaOrdenConvenio(ordenImpl.traerOCSPorContrato(contratoVo.getId(), OrdenEstadoEnum.POR_SOLICITAR.getId(), sesion.getUsuarioSesion().getIdCampo()));
        contratoVo.getListaOrdenConvenio().addAll(ordenImpl.traerOCSPorContratoDet(contratoVo.getId(), OrdenEstadoEnum.POR_SOLICITAR.getId(), sesion.getUsuarioSesion().getIdCampo()));
    }

    public List<ContratoVO> traerOCSConvenio() {
        List<ContratoVO> lc = new ArrayList<>();
        lc.addAll(convenioImpl.consultaOCSPorConvenio(contratoVo.getNumero(), OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId()));
        lc.addAll(convenioImpl.consultaOCSPorConvenioDet(contratoVo.getNumero(), OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId()));
        return lc;
    }

    public void traerConvenoArticulo(int contrato) {
        contratoVo.setListaArticulo(new ArrayList<>());
        contratoVo.getListaArticulo().addAll(convenioArticuloImpl.traerConvenioArticulo(contrato, contratoVo.getIdCampo()));
    }

    public void traerDiasYRemanente(int convenio) {
        if (contratoVo.getFechaVencimiento() != null) {

            if (contratoVo.getFechaVencimiento().compareTo(new Date()) >= 0) {
                contratoVo.setDiasRestantes(siManejoFechaImpl.dias(contratoVo.getFechaVencimiento(), new Date()));
            } else {
                contratoVo.setDiasRestantes(0);
            }
        }
        contratoVo.setAcumulado(ordenImpl.sumaToalOCSPorContrato(contratoVo.getId(), contratoVo.getIdCampo()));
        contratoVo.setRemanente(contratoVo.getMonto() - contratoVo.getAcumulado());

    }

    public void traerContratosRelacionados(int contRel) {
        contratoVo.setListaContratoRelacionado(convenioImpl.contratosRelacionados(contratoVo.getIdContratoRelacionado(), contratoVo.getId()));

    }

    public void traerDatosProveedor() {
        contratoVo.setProveedorVo(proveedorImpl.traerProveedor(contratoVo.getProveedor(), sesion.getRfcEmpresa()));
    }

    public void traerArchivosConvenio() {
        contratoVo.setListaArchivoConvenio(cvConvenioAdjuntoImpl.traerPorConvenio(contratoVo.getId()));
    }

    public void onTabChange(TabChangeEvent event) {
        String cont = event.getTab().getTitle();
        if (!cont.equals("Buscar")) {
            contratoVo = convenioImpl.buscarPorNumero(cont, sesion.getUsuarioSesion().getId(), Boolean.TRUE, sesion.getUsuarioSesion().getIdCampo());

            // int index = lstConveniosTabs.indexOf(contratoVo);
            contratoVo.setProveedorVo(new ProveedorVo());
            contratoVo.getProveedorVo().setTodoContactos(new ArrayList<>());
            //llenarDatos(index, contratoVo.getId());
            //Activar tabl
            //

            //setIndice(index);
            llenarDatosContrato(contratoVo.getId());
            //
        }
    }

    public void traerGerenciaContrato(int convenio) {
        contratoVo.setListaGerencia(cvConvenioGerenciaImpl.convenioPorGerenica(contratoVo.getId()));
    }

    public void onTabClose(TabCloseEvent event) {

        String cont = event.getTab().getTitle();
        contratoVo = convenioImpl.buscarPorNumero(cont, sesion.getUsuarioSesion().getId(), Boolean.TRUE, sesion.getUsuarioSesion().getIdCampo());
        indice = lstConveniosTabs.indexOf(contratoVo);
        //regreso a la table
        contratos.add(contratos.size(), contratoVo);
        //borra de las pestanas
        getLstConveniosTabs().remove(getIndice());
    }

    public void traerOcsPorconvenio() {
        ocsPorConvenio();
//	traerOCSConvenio(getIndice());
    }
}
