/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.backing;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.shaded.json.JSONException;
import org.primefaces.shaded.json.JSONObject;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.contrato.bean.model.ProveedorModel;
import sia.contrato.bean.soporte.FacesUtils;
import sia.excepciones.SIAException;
import sia.ihsa.contratos.Sesion;
import sia.inventarios.service.ArticuloImpl;
import sia.inventarios.service.ArticuloRemote;
import sia.modelo.CvTipo;
import sia.modelo.contrato.vo.ClasificacionVo;
import sia.modelo.contrato.vo.ContratoDocumentoVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.ConvenioArticuloVo;
import sia.modelo.contrato.vo.FiltroVo;
import sia.modelo.contrato.vo.RhConvenioDocumentoVo;
import sia.modelo.documento.vo.DocumentoVO;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.proveedor.Vo.ProveedorDocumentoVO;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sgl.vo.Vo;
import sia.modelo.sistema.vo.CategoriaVo;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.GeneralVo;
import sia.modelo.vo.StatusVO;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.notificaciones.sistema.impl.ServicioNotificacionSistemaImpl;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
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
import sia.servicios.convenio.impl.CvHitoImpl;
import sia.servicios.convenio.impl.CvTipoImpl;
import sia.servicios.evaluacion.impl.CvConvenioEvaluacionImpl;
import sia.servicios.evaluacion.impl.CvEvaluacionImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.proveedor.impl.ContactoProveedorImpl;
import sia.servicios.proveedor.impl.CuentaBancoProveedorImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.proveedor.impl.PvClasificacionArchivoImpl;
import sia.servicios.proveedor.impl.PvDocumentoImpl;
import sia.servicios.rh.impl.RhConvenioDocumentosImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiCategoriaImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiRelCategoriaImpl;
import sia.servicios.sistema.impl.SiUnidadImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.servicios.sistema.vo.CatalogoContratoVo;
import sia.servicios.sistema.vo.MenuSiOpcionVo;
import sia.servicios.sistema.vo.MonedaVO;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.util.OrdenEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
//@ManagedBean
//@SessionScoped
@Named(value = "contratoBean")
@ViewScoped
public class ContratoBean implements Serializable {

    static final long serialVersionUID = 1;
    /**
     * Creates a new instance of contratoBean
     */
    @Inject
    private Sesion sesion;

    @Inject
    private ConvenioImpl convenioServicioRemoto;
    @Inject
    private MonedaImpl monedaServicioRemoto;
    @Inject
    private SiParametroImpl parametrosSistemaServicioRemoto;
    @Inject
    private EstatusImpl estatusServicioRemoto;
    @Inject
    private SiAdjuntoImpl siAdjuntoServicioRemoto;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private SiOpcionImpl siOpcionImpl;
    @Inject
    private PvDocumentoImpl pvDocumentoImpl;
    @Inject
    private CvConvenioDocumentoImpl cvConvenioDocumentoImpl;
    @Inject
    private CvConvenioCondicionPagoImpl cvConvenioCondicionPagoImpl;
    @Inject
    private CvCondicionPagoImpl cvCondicionPagoImpl;
    @Inject
    private CvHitoImpl cvHitoImpl;
    @Inject
    private CvConvenioHitoImpl cvConvenioHitoImpl;
    @Inject
    private CvConvenioAdjuntoImpl cvConvenioAdjuntoImpl;
    @Inject
    private CvTipoImpl cvTipoImpl;
    @Inject
    private CvClasificacionImpl cvClasificacionImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private CuentaBancoProveedorImpl cuentaBancoProveedorImpl;
    @Inject
    private ContactoProveedorImpl contactoProveedorImpl;
    @Inject
    private PvClasificacionArchivoImpl pvClasificacionArchivoImpl;
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenImpl;
    @Inject
    private CvConvenioGerenciaImpl cvConvenioGerenciaImpl;
    @Inject
    private ServicioNotificacionSistemaImpl notificacionSistemaImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    private ApCampoGerenciaImpl apCampoGerenciaImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    // PRECIO
    @Inject
    private SiCategoriaImpl siCategoriaImpl;
    @Inject
    private SiRelCategoriaImpl siRelCategoriaImpl;
    @Inject
    private ArticuloRemote articuloImpl;
    @Inject
    private CvConvenioArticuloImpl convenioArticuloImpl;
    @Inject
    private SiUnidadImpl siUnidadImpl;
    @Inject
    private CvConvenioEvaluacionImpl cvConvenioEvaluacionImpl;
    @Inject
    private CvEvaluacionImpl cvEvaluacionImpl;
    @Inject
    RhConvenioDocumentosImpl rhConvenioDocumentosImpl;

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    @Getter
    @Setter
    private UploadedFile fileUpload;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    public ContratoBean() {
    }

    @Getter
    @Setter
    private List<ArticuloVO> articulosResultadoBqda = new ArrayList<>();
    @Getter
    @Setter
    private ArticuloVO articuloVO;
    @Getter
    @Setter
    private ContratoVO contratoVo;
    @Getter
    @Setter
    private String articuloTx;
    @Getter
    @Setter
    private ConvenioArticuloVo convenioArticuloVo;
    //    
//Primitivos
    @Getter
    @Setter
    private String dir = "";

    @Getter
    @Setter
    private List<ContratoVO> listaConvenios = null;
    @Getter
    @Setter
    private List<ContratoVO> listaContactos = null;
//

    @Getter
    @Setter
    private int pagina;
    @Getter
    @Setter
    private String url;
    @Getter
    @Setter
    private String rutaPagina = "";
    //

    @Getter
    @Setter
    private List<SiOpcionVo> menu = null;
    @Getter
    @Setter
    private List<ContratoVO> lstConveniosTabs;
    @Getter
    @Setter
    private boolean consultaDocumentos = false;
    @Getter
    @Setter
    private boolean editarProveedor = false;
    //
    @Getter
    @Setter
    private int idProveedor;
    @Getter
    @Setter
    private List<DocumentoVO> listaDoctos;
    @Getter
    @Setter
    private ContratoDocumentoVo contratoDocumentoVo;
    @Getter
    @Setter
    private Vo vo;
    @Getter
    @Setter
    private List<CatalogoContratoVo> listaCondicionesPago;
    @Getter
    @Setter
    private List<CatalogoContratoVo> listaHitoPago;
    @Getter
    @Setter
    private List<AdjuntoVO> listaArchivoConvenio;
    @Getter
    @Setter
    private boolean subirContrato;
    @Getter
    @Setter
    private boolean subirProveedor;
    @Getter
    @Setter
    private boolean subirListaPrecio;
    //private int idContrato;
    @Getter
    @Setter
    private int indice;
    @Getter
    @Setter
    private boolean mostrarCerrar = false;
    //
    @Getter
    @Setter
    private ContactoProveedorVO contactoProveedorVO;
    @Getter
    @Setter
    private List<MenuSiOpcionVo> listaMenu;
    @Getter
    @Setter
    private Map<String, List<ContactoProveedorVO>> listaCorreo;
    //
    @Getter
    @Setter
    private Map<String, List<SelectItem>> lista = new HashMap<>();

    //
    //
    @Getter
    @Setter
    private FiltroVo filtroVo;
    @Getter
    @Setter
    private int maximoRegistros;
    @Getter
    @Setter
    private ProveedorDocumentoVO docProveedor;
    @Getter
    @Setter
    private String mesAnio;
    @Getter
    @Setter
    private int idGerencia;
    @Getter
    @Setter
    private List<UsuarioVO> listaUsuarioGerencia;
    @Getter
    @Setter
    private UsuarioVO usuarioVo;
    //
    @Getter
    @Setter
    private List<GerenciaVo> listaGerencia = new ArrayList<>();
    // lista de precio
    @Getter
    @Setter
    private List<CategoriaVo> categoriasSeleccionadas = new ArrayList<>();
    @Getter
    @Setter
    private CategoriaVo categoriaVo;
    @Getter
    @Setter
    private List<CategoriaVo> listaCategoría = new ArrayList<>();
    @Getter
    @Setter
    private List<ArticuloVO> listaArticulos;
    @Getter
    @Setter
    private List<SelectItem> listaUnidad;
    // lista precio archivo
    @Getter
    @Setter
    private boolean editarEvals = false;
    @Getter
    @Setter
    private List<Tab> tabs;
    @Getter
    @Setter
    private TabView tabView;

    @PostConstruct
    public void irContrato() {
        tabView = new TabView();
        tabs = new LinkedList<>();
        listaCorreo = new HashMap<>();
        lstConveniosTabs = new ArrayList<>();
        filtroVo = new FiltroVo();
        contratoVo = new ContratoVO();
        usuarioVo = new UsuarioVO();
        llenarLista();
        listaUnidad = new ArrayList<>();
        categoriaVo = new CategoriaVo();
        categoriaVo.setListaCategoria(siCategoriaImpl.traerCategoriaPrincipales());
        convenioArticuloVo = new ConvenioArticuloVo();
        List<GeneralVo> lu = siUnidadImpl.traerUnidad();
        for (GeneralVo generalVo : lu) {
            listaUnidad.add(new SelectItem(generalVo.getValor(), generalVo.getNombre()));
        }
        listaDoctos = new ArrayList<>();
        listaHitoPago = new ArrayList<>();
        listaUsuarioGerencia = new ArrayList<>();
        listaCorreo.put("para", new ArrayList<>());
        listaCorreo.put("copia", new ArrayList<>());
    }

    private void llenarLista() {
        switch (sesion.getIdRol()) {
            case Constantes.ROL_ADMINISTRA_CONTRATO:
                llenarListaConveniosInicio();
                //  
                setEditarEvals(true);
                setEditarProveedor(true);
                setConsultaDocumentos(false);
                //
                break;
            case Constantes.ROL_REVISA_CONTRATO:
                llenarListaConveniosInicio();
                setEditarProveedor(false);
                setEditarEvals(false);
                setConsultaDocumentos(true);
                //
                break;
            case Constantes.ROL_CONSULTA_CONTRATO:
                llenarListaConveniosInicio();
                setEditarProveedor(false);
                setEditarEvals(false);
                //
                break;
            default:
                setEditarEvals(false);
                setEditarProveedor(false);
                setConsultaDocumentos(false);
        }
        llenarMapaLista();
    }
    int id;

    public void addTab() {
        Tab tab = new Tab();
        tab.setTitle("Nuevo_" + id++);
        tabs.add(tab);
    }

    public void llenarListaConveniosInicio() {
        if (sesion.getUsuarioSesion() != null) {
            setListaConvenios(convenioServicioRemoto.traerConveniosPorProveedorPermisos(
                    getIdProveedor(),
                    sesion.getUsuarioSesion().getId(), sesion.getIdRol(), getFiltroVo().getImporte(),
                    getFiltroVo().getIdMoneda(), getFiltroVo().getFecha(), getFiltroVo().getIdOperador(),
                    sesion.getUsuarioSesion().getIdCampo(), 10, getFiltroVo().getIdEstado()));
        }
    }

    public void llenarMapaLista() {
        if (sesion.getUsuarioSesion() != null) {
            List<SelectItem> le = new ArrayList<>();
            for (StatusVO listaConvenio : estatusServicioRemoto.traerPorTipo(Constantes.ESTATUS_COMPROBANTE_CONV)) {
                le.add(new SelectItem(listaConvenio.getIdStatus(), listaConvenio.getNombre()));
            }
            getLista().put("estados", le);
            le = new ArrayList<>();
            for (MonedaVO le1 : monedaServicioRemoto.traerMonedaActiva(sesion.getUsuarioSesion().getIdCampo())) {
                le.add(new SelectItem(le1.getId(), le1.getNombre()));
            }
            getLista().put("monedas", le);
            le = new ArrayList<>();
            for (ClasificacionVo le1 : cvClasificacionImpl.traerClasificacionPrincipal()) {
                le.add(new SelectItem(le1.getId(), le1.getNombre()));
            }
            getLista().put("clasificaciones", le);
            le = new ArrayList<>();
            for (GerenciaVo le1 : gerenciaImpl.traerGerenciaActivaPorCampo(sesion.getUsuarioSesion().getIdCampo())) {
                le.add(new SelectItem(le1.getId(), le1.getNombre()));
            }
            getLista().put("gerencias", le);
            le = new ArrayList<>();
            for (CvTipo le1 : cvTipoImpl.findAll()) {
                le.add(new SelectItem(le1.getId(), le1.getNombre()));
            }
            getLista().put("tipos", le);
            //
            le = new ArrayList<>();
            for (Vo le1 : cvCondicionPagoImpl.traerTodo()) {
                le.add(new SelectItem(le1.getId(), le1.getNombre()));
            }
            getLista().put("condicionesPago", le);

        }
    }

    public String regrearPrincipal() {
        //traerUltimosConvenio();
        return "/vistas/contrato/admin/inicio";
    }

    public void onTabChange(TabChangeEvent event) {
        String cont = event.getTab().getTitle();
        if (!cont.equals("Buscar")) {
            contratoVo = convenioServicioRemoto.buscarPorNumero(cont, sesion.getUsuarioSesion().getId(), Boolean.TRUE, sesion.getUsuarioSesion().getIdCampo());

            int index = lstConveniosTabs.indexOf(contratoVo);
            contratoVo.setProveedorVo(new ProveedorVo());
            contratoVo.getProveedorVo().setTodoContactos(new ArrayList<>());
            llenarDatos(index, contratoVo.getId(), getListaConvenios());
            //Activar tabl
            //
            ocsPorConvenio(index);
            setIndice(index);
            listaCorreo = new HashMap<>();
            listaUsuarioGerencia = new ArrayList<>();
            listaCorreo.put("para", new ArrayList<>());
            listaCorreo.put("copia", new ArrayList<>());

        }
    }

    public void onTabClose(TabCloseEvent event) {
        String cont = event.getTab().getTitle();
        contratoVo = convenioServicioRemoto.buscarPorNumero(cont, sesion.getUsuarioSesion().getId(), Boolean.TRUE, sesion.getUsuarioSesion().getIdCampo());
        indice = lstConveniosTabs.indexOf(contratoVo);
        //regreso a la table
        getListaConvenios().add(getListaConvenios().size(), contratoVo);
        //borra de las pestanas
        getLstConveniosTabs().remove(getIndice());
    }

    public void seleccionarPestana(int ind) {
        setIndice(ind);
        llenarDatos(ind, lstConveniosTabs.get(ind).getId(), listaConvenios);
    }

    public void cerrarPestanaTab(int ind) {
        setIndice(ind);
        //regreso a la table
        getListaConvenios().add(
                getListaConvenios().size(),
                buscarPorId(
                        getLstConveniosTabs().get(getIndice()).getId(), false
                )
        );
        //borra de las pestanas
        getLstConveniosTabs().remove(getIndice());
    }

    public ContratoVO buscarPorId(int idContrato, boolean validarFormalizado) {
        return convenioServicioRemoto.buscarPorId(idContrato, sesion.getUsuarioSesion().getIdCampo(), sesion.getUsuarioSesion().getId(), validarFormalizado);
    }

    public void seleccionarContactoPara(SelectEvent<ContactoProveedorVO> event) {
        ContactoProveedorVO con = (ContactoProveedorVO) event.getObject();
        List<ContactoProveedorVO> lc = new ArrayList<>();
        lc.add(con);
        //
        if (getListaCorreo().get("para") == null
                || getListaCorreo().get("para").isEmpty()) {
            getListaCorreo().put("para", lc);
        } else {
            getListaCorreo().get("copia").add(con);
        }
        getLstConveniosTabs().get(getIndice()).getProveedorVo().getTodoContactos().remove(con);
    }

    public void quitarUsuarioPara(int ind) {
        getListaCorreo().get("para").remove(ind);
        //
        //setContactoProveedorVO(new ContactoProveedorVO());
        if (getContactoProveedorVO() != null) {
            getLstConveniosTabs().get(getIndice()).getProveedorVo().getTodoContactos().add(getContactoProveedorVO());
        }
    }

    public void traerEmpleadoPorGerencia() {
        if (getIdGerencia() > 0) {
            traerUsuarioPorGerencia();
        }
    }

    public void traerUsuarioPorGerencia() {
        setListaUsuarioGerencia(apCampoUsuarioRhPuestoImpl.traerUsurioGerenciaCampo(getIdGerencia(), lstConveniosTabs.get(getIndice()).getIdCampo()));
        //
        UsuarioResponsableGerenciaVo urgv = apCampoGerenciaImpl.buscarResponsablePorGerencia(getIdGerencia(), lstConveniosTabs.get(getIndice()).getIdCampo());
        ContactoProveedorVO cpvo = new ContactoProveedorVO();
        cpvo.setNombre(urgv.getNombreUsuario());
        cpvo.setCorreo(urgv.getEmailUsuario());
        listaCorreo.get("copia").add(cpvo);
    }

    public void seleccionarContactoCopia(SelectEvent<UsuarioVO> event) {
        UsuarioVO con = (UsuarioVO) event.getObject();
        ContactoProveedorVO cpVo = new ContactoProveedorVO();
        cpVo.setNombre(con.getNombre());
        cpVo.setCorreo(con.getMail());
        //
        getListaCorreo().get("copia").add(cpVo);
        //getListaUsuarioGerencia().remove(con);
    }

    public void quitarUsuarioCopia(int ind) {
        getListaCorreo().get("copia").remove(ind);
        //
        if (getContactoProveedorVO() != null) {
            if (getContactoProveedorVO().getIdProveedor() > 0) {
                getLstConveniosTabs().get(getIndice()).getProveedorVo().getTodoContactos().add(getContactoProveedorVO());
            }
        }
    }

    public void inicioContratoFormalizado() {
        if (validaContratoFormalizado()) {
            if (validaProveedorContrato()) {
                listaCorreo = new HashMap<>();
                llenarCorreoCopia();
                traerContactosPorProveedor();
                PrimeFaces.current().executeScript(
                        ";$(dialogoFormalizarContrato"
                        + getLstConveniosTabs().get(getIndice()).getId()
                        + ").modal('show');"
                );
            } else {
                FacesUtils.addErrorMessage("Los datos del proveedor estan incompletos.");
            }
        } else {
            FacesUtils.addErrorMessage("Es necesario agregar todos los datos del contrato para poder ser cambiado de estado.");
        }
    }

    public void traerContactosPorProveedor() {
        lstConveniosTabs.get(getIndice()).getProveedorVo().setTodoContactos(contactoProveedorImpl.traerTodosContactoPorProveedor(lstConveniosTabs.get(getIndice()).getProveedorVo().getIdProveedor()));
    }

    public void llenarCorreoCopia() {
        List<ContactoProveedorVO> lc = new ArrayList<>();
        ContactoProveedorVO cpVo = new ContactoProveedorVO();
        List<GerenciaVo> lg = cvConvenioGerenciaImpl.convenioPorGerenica(lstConveniosTabs.get(getIndice()).getId());

        // gerencia compras
        UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo = apCampoGerenciaImpl.buscarResponsablePorGerencia(Constantes.GERENCIA_ID_COMPRAS, lstConveniosTabs.get(getIndice()).getIdCampo());
        if (usuarioResponsableGerenciaVo != null) {
            cpVo.setNombre(usuarioResponsableGerenciaVo.getNombreUsuario());
            cpVo.setCorreo(usuarioResponsableGerenciaVo.getEmailUsuario());
            lc.add(cpVo);
        }

        //Gerencia tesoreria
        usuarioResponsableGerenciaVo = apCampoGerenciaImpl.buscarResponsablePorGerencia(Constantes.GERENCIA_ID_DIRECCION_FINANZAS, lstConveniosTabs.get(getIndice()).getIdCampo());
        if (usuarioResponsableGerenciaVo != null) {
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(usuarioResponsableGerenciaVo.getNombreUsuario());
            cpVo.setCorreo(usuarioResponsableGerenciaVo.getEmailUsuario());
            lc.add(cpVo);
        }
        //Gerencia de juridico
        usuarioResponsableGerenciaVo = apCampoGerenciaImpl.buscarResponsablePorGerencia(Constantes.GERENCIA_JURIDICO, lstConveniosTabs.get(getIndice()).getIdCampo());
        if (usuarioResponsableGerenciaVo != null) {
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(usuarioResponsableGerenciaVo.getNombreUsuario());
            cpVo.setCorreo(usuarioResponsableGerenciaVo.getEmailUsuario());
            lc.add(cpVo);
        }
        //Gerencia de juridico
        usuarioResponsableGerenciaVo = apCampoGerenciaImpl.buscarResponsablePorGerencia(Constantes.GERENCIA_ID_RR_HH, lstConveniosTabs.get(getIndice()).getIdCampo());
        if (usuarioResponsableGerenciaVo != null) {
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(usuarioResponsableGerenciaVo.getNombreUsuario());
            cpVo.setCorreo(usuarioResponsableGerenciaVo.getEmailUsuario());
            lc.add(cpVo);
        }
        // Rol contrato
        for (UsuarioRolVo urVo : siUsuarioRolImpl.traerUsuarioPorRolModulo(Constantes.ROL_ADMINISTRA_CONTRATO, Constantes.MODULO_CONTRATO, lstConveniosTabs.get(getIndice()).getIdCampo())) {
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(urVo.getUsuario());
            cpVo.setCorreo(urVo.getCorreo());
            lc.add(cpVo);
        }

        for (GerenciaVo lg1 : lg) {
            UsuarioResponsableGerenciaVo urgv = apCampoGerenciaImpl.buscarResponsablePorGerencia(lg1.getId(), lstConveniosTabs.get(getIndice()).getIdCampo());
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(urgv.getNombreUsuario());
            cpVo.setCorreo(urgv.getEmailUsuario());
            lc.add(cpVo);
        }
        listaCorreo.put("copia", lc);
    }

    public boolean validaContratoFormalizado() {
        if (!getLstConveniosTabs().get(getIndice()).getListaArchivoConvenio().isEmpty()) {
            if (!getLstConveniosTabs().get(getIndice()).getListaConvenioDocumento().isEmpty()) {
                if (!getLstConveniosTabs().get(getIndice()).getListaGerencia().isEmpty()) {
                    if (getLstConveniosTabs().get(getIndice()).getFechaInicio() != null
                            && getLstConveniosTabs().get(getIndice()).getFechaVencimiento() != null
                            && (getLstConveniosTabs().get(getIndice()).getIdContratoRelacionado() > 0
                            || getLstConveniosTabs().get(getIndice()).getMonto() > 0)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean validaProveedorContrato() {
        return getLstConveniosTabs().get(getIndice()).getProveedorVo().getLstRL() != null && !getLstConveniosTabs().get(getIndice()).getProveedorVo().getLstRL().isEmpty();
    }

    public void finlizarContratoFormalizado() {
        if (contratoFormalizado()) {	    //
            getLstConveniosTabs().set(getIndice(),
                    buscarPorId(getLstConveniosTabs().get(getIndice()).getId(), false));
            llenarDatosContrato(getIndice(), getLstConveniosTabs().get(getIndice()).getId());
            FacesUtils.addInfoMessage("Se envío la notificación de contrato formalizado.");
        } else {
            FacesUtils.addErrorMessage("Ocurrio un error al enviar la notificación del contrato formalizado, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
        PrimeFaces.current().executeScript(
                ";$(dialogoFormalizarContrato"
                + getLstConveniosTabs().get(getIndice()).getId()
                + ").modal('hide');"
        );
    }

    public boolean contratoFormalizado() {
        try {
            String correo = "";
            for (ContactoProveedorVO lstRL : getLstConveniosTabs().get(indice).getProveedorVo().getLstRL()) {
                if (lstRL.isSelected()) {
                    if (correo.isEmpty()) {
                        correo = lstRL.getCorreo();
                    } else {
                        correo += "," + lstRL.getCorreo();
                    }
                }
            }
            convenioServicioRemoto.promoverEstadoConvenio(sesion.getUsuarioSesion().getId(), getLstConveniosTabs().get(getIndice()).getId(), Constantes.ESTADO_CONVENIO_ACTIVO, listaCorreo);

            return true;
        } catch (Exception e) {
            notificacionSistemaImpl.enviarExcepcion(sesion.getUsuarioSesion().getId(), correoExcepcion(), getLstConveniosTabs().get(getIndice()), "Error - " + getLstConveniosTabs().get(getIndice()).getNumero(), e.toString());
            UtilLog4j.log.fatal(this, e);
            return false;
        }
    }

    private String correoExcepcion() {
        List<UsuarioRolVo> ur = siUsuarioRolImpl.traerRolPorCodigo(Constantes.ROL_DESARROLLO_SISTEMA, Constantes.AP_CAMPO_NEJO, Constantes.MODULO_ADMIN_SIA);
        StringBuilder correo = new StringBuilder();

        if (ur == null || ur.isEmpty()) {
            correo.append("siaihsa@ihsa.mx");
        } else {
            for (UsuarioRolVo usuarioRolVo : ur) {

                if (correo.length() > 0) {
                    correo.append(',');
                }

                correo.append(usuarioRolVo.getCorreo());
            }
        }
        return correo.toString();
    }

    public void inicioContratoFiniquitado() {
        if (validaContratoFormalizado()) {
            setListaCorreo(new HashMap<>());
            llenarCorreoCopia();
            //
            traerContactosPorProveedor();
            PrimeFaces.current().executeScript(
                    ";$(dialogoFiniquitarContrato"
                    + getLstConveniosTabs().get(getIndice()).getId()
                    + ").modal('show');"
            );
        } else {
            FacesUtils.addErrorMessage("Es necesario agregar todos los datos del contrato para poder ser cambiado de estado.");
        }
    }

    public void finlizarContratoFiniquitado() {
        if (getListaCorreo().get("para").size() > Constantes.CERO) {
            if (contratoFiniquitado()) {	    //
                getLstConveniosTabs().set(getIndice(),
                        buscarPorId(getLstConveniosTabs().get(getIndice()).getId(), false));
                llenarDatosContrato(getIndice(), getLstConveniosTabs().get(getIndice()).getId());
                FacesUtils.addInfoMessage("Se envío la notificación de contrato finiquitado.");
            } else {
                FacesUtils.addErrorMessage("Ocurrio un error al enviar la notificación del contrato finiquitado, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
            }
            PrimeFaces.current().executeScript(
                    ";$(dialogoFiniquitarContrato"
                    + getLstConveniosTabs().get(getIndice()).getId()
                    + ").modal('hide');"
            );
        } else {
            FacesUtils.addInfoMessage("Falta seleccionar el correo para");
        }
    }

    public boolean contratoFiniquitado() {
        try {
            String correo = "";
            convenioServicioRemoto.promoverEstadoConvenio(sesion.getUsuarioSesion().getId(), getLstConveniosTabs().get(getIndice()).getId(), Constantes.ESTADO_CONVENIO_FINIQUITO, listaCorreo);

            return true;
        } catch (Exception e) {
            notificacionSistemaImpl.enviarExcepcion(sesion.getUsuarioSesion().getId(), correoExcepcion(), getLstConveniosTabs().get(getIndice()), "Error - " + getLstConveniosTabs().get(getIndice()).getNumero(), e.toString());
            UtilLog4j.log.fatal(this, e);
            return false;
        }
    }

    public void eliminarContrato() {
        convenioServicioRemoto.eliminarContrato(sesion.getUsuarioSesion().getId(), lstConveniosTabs.get(getIndice()).getId());
        getLstConveniosTabs().remove(getIndice());
    }

    public void iniciarIncide() {
        setMostrarCerrar(Constantes.FALSE);
    }

    public List<MenuSiOpcionVo> getListaMenus() {
        List<MenuSiOpcionVo> listaMenu = new ArrayList<>();
        try {
            listaMenu.addAll(taerListaMenu(Constantes.MODULO_CONTRATO));
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return listaMenu;
    }

    public List<MenuSiOpcionVo> taerListaMenu(Integer modulo) {
        try {
            listaMenu = new ArrayList<>();
            listaMenu.addAll(makeItems(siOpcionImpl.getListaMenu(modulo, sesion.getUsuarioSesion().getId(), sesion.getUsuarioSesion().getIdCampo())));
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);

            return new ArrayList<>();
        }
        return listaMenu;
    }

    private List<MenuSiOpcionVo> makeItems(List<MenuSiOpcionVo> listaItems) {
        List<MenuSiOpcionVo> itemsReturn = new ArrayList<>();

        for (MenuSiOpcionVo oldVO : listaItems) {
            MenuSiOpcionVo menuSiOpcionVo = new MenuSiOpcionVo();
            menuSiOpcionVo.setPadre(oldVO.getPadre());
            for (SiOpcionVo hijo : oldVO.getHijos()) {
                System.out.println("pag: : : " + hijo.getPagina());
                //
                menuSiOpcionVo.getHijos().add(hijo);
            }
            itemsReturn.add(menuSiOpcionVo);
        }
        return itemsReturn;
    }

    public List<SelectItem> getTraerEstatus() {
        return getLista().get("estados");
    }

    public List<SelectItem> getTraerMoneda() {
        return getLista().get("monedas");
    }

    public List<SelectItem> getTraerClasificacion() {
        return getLista().get("clasificaciones");
    }

    public List<SelectItem> getTraerGerencia() {
        return getLista().get("gerencias");
    }

    public List<SelectItem> getTraerTipo() {
        return getLista().get("tipos");
    }

    public List getTraerProveedor() {
        List<SelectItem> listProv = null;
        try {
            listProv = new ArrayList<SelectItem>();

            for (Object obj : this.getLista().get("proveedores")) {
                ProveedorVo prov = (ProveedorVo) obj;
                SelectItem item = new SelectItem(prov.getIdProveedor(), prov.getNombre());
                listProv.add(item);
            }

        } catch (Exception e) {
            LOGGER.error(e);
        }

        return listProv;
    }

    public List getListaTotalCondicionesPago() {
        return getLista().get("condicionesPago");
    }

    /////////////////////////////////////
    public void ocsPorConvenio(int index) {
        try {
            List<ContratoVO> lo = traerOCSConvenio(index);
            if (lo != null && !lo.isEmpty()) {
                llenarOCSConvenio(index);
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
                        + ",'" + getLstConveniosTabs().get(index).getNumero() + "'"
                        + ", 'frmOcsConvenio" + getLstConveniosTabs().get(index).getId() + "'"
                        + ", 'graficaOCSConvenio" + getLstConveniosTabs().get(index).getId() + "'"
                        + ", 'txtMesAnio" + getLstConveniosTabs().get(index).getId() + "'"
                        + ", 'btnBuscar" + getLstConveniosTabs().get(index).getId() + "'"
                        + ", " + getLstConveniosTabs().get(index).getAcumulado()
                        + ", '" + (getLstConveniosTabs().get(index).getMonto() - getLstConveniosTabs().get(index).getAcumulado()) + "'"
                        + ");");
            } else {
                PrimeFaces.current().executeScript(
                        ";ocultarDiv('graficaOCSConvenio"
                        + getLstConveniosTabs().get(index).getId()
                        + "');;"
                );
            }
        } catch (JSONException ex) {
            LOGGER.error(ex);
        }

    }

    public void llenarOCSConvenio(int index) {
        getLstConveniosTabs().get(index).setListaOrdenConvenio(ordenImpl.traerOCSPorContrato(getLstConveniosTabs().get(index).getId(), OrdenEstadoEnum.POR_SOLICITAR.getId(), sesion.getUsuarioSesion().getIdCampo()));
        getLstConveniosTabs().get(index).getListaOrdenConvenio().addAll(ordenImpl.traerOCSPorContratoDet(getLstConveniosTabs().get(index).getId(), OrdenEstadoEnum.POR_SOLICITAR.getId(), sesion.getUsuarioSesion().getIdCampo()));
    }

    public List<ContratoVO> traerOCSConvenio(int index) {
        List<ContratoVO> lc = new ArrayList<>();
        lc.addAll(convenioServicioRemoto.consultaOCSPorConvenio(getLstConveniosTabs().get(index).getNumero(), OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId()));
        lc.addAll(convenioServicioRemoto.consultaOCSPorConvenioDet(getLstConveniosTabs().get(index).getNumero(), OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId()));
        return lc;
    }
/////////////////////

    public List<SiOpcionVo> childOpcion(Integer id) {
        return childOpcion(id);
    }

    public void llenarJson() {
        llenarJsonProveedor();
    }

    private void llenarJsonProveedor() {
        String jsonProveedores = traerJson();
        PrimeFaces.current().executeScript(";setJson(" + jsonProveedores + ");");
    }

    public String traerJson() {
        String jsonProveedores = proveedorImpl.traerProveedorPorCompaniaSesionJson("'" + sesion.getRfcEmpresa() + "'", Constantes.CERO);
        return jsonProveedores;
    }

    public String goToReturn(String page) {
        //viajeBeanModel.setOpcionViaje(1);
        if (!page.equals(getRutaPagina())) {
            setRutaPagina(page);
            // limpiarVar();
        }
        return page;
    }

    public void limpiarLista() {
        limpiar();
    }

    public void limpiar() {
        lstConveniosTabs.clear();
        llenarLista();
    }

    public void limpiarVar(int i) {
        getLstConveniosTabs().remove(i);
        setPagina(1);
    }

    public void cancelarActualizarDatosGeneralesContrato() {
        setListaGerencia(new ArrayList<>());

    }

    public void actualizarDatosGeneralesContrato() {
        actualizarContratoGenerales();
        PrimeFaces.current().executeScript(";$(dialogoModificarDatosGenerales"
                + getLstConveniosTabs().get(getIndice()).getId()
                + ").modal('hide');"
        );
        setListaGerencia(new ArrayList<>());
        llenarDatosContrato(getIndice(), getLstConveniosTabs().get(getIndice()).getId());
    }

    public void actualizarContratoGenerales() {
        //
        convenioServicioRemoto.actualizarDatosGenerales(sesion.getUsuarioSesion().getId(), getLstConveniosTabs().get(indice), listaGerencia);
        getLstConveniosTabs().get(getIndice()).setListaGerencia(cvConvenioGerenciaImpl.convenioPorGerenica(getLstConveniosTabs().get(indice).getId()));
        //getLstConveniosTabs().set(indice, convenioServicioRemoto.buscarPorId(getLstConveniosTabs().get(indice).getId()));
    }

    public void actualizarContrato() {
        convenioServicioRemoto.actualizar(sesion.getUsuarioSesion().getId(), getLstConveniosTabs().get(getIndice()));
        llenarDatosContrato(
                getIndice(),
                getLstConveniosTabs().get(getIndice()).getId()
        );
        getLstConveniosTabs().set(
                getIndice(), buscarPorId(
                        getLstConveniosTabs().get(getIndice()).getId(), true)
        );
        llenarDatosContrato(getIndice(), getLstConveniosTabs().get(getIndice()).getId());
    }
/////////////////////////////////////////////////

    public void eliminarGerencia(int idConvGer) {

        cvConvenioGerenciaImpl.eliminar(sesion.getUsuarioSesion().getId(), idConvGer);
        //
        getLstConveniosTabs().get(getIndice()).setListaGerencia(cvConvenioGerenciaImpl.convenioPorGerenica(lstConveniosTabs.get(getIndice()).getId()));

    }

    public void quitarGerencia() {
        int indice = Integer.parseInt(FacesUtils.getRequestParam("idGerencia"));
        getListaGerencia().remove(indice);
        //
    }

    public void agregarGerenciaContrato() {
        if (idGerencia > 0) {
            agregarGerenciaListaContrato(idGerencia);
        }
    }

    public void agregarGerenciaListaContrato(int idGer) {
        int i = 0;
        for (GerenciaVo listaGerencia1 : listaGerencia) {
            if (listaGerencia1.getId() == idGer) {
                i++;
                break;
            }
        }
        if (i == 0) {
            listaGerencia.add(listaGerencia.size(), gerenciaImpl.buscarPorId(idGer));
        }
    }

    public void traerDocumentos() {
        setListaDoctos(pvDocumentoImpl.traerDocumentoPorTipo(Constantes.DOCUMENTO_TIPO_CONTRATO));
    }

    public void agregarArchivoDocto(int idDoctoConv) {
        setContratoDocumentoVo(new ContratoDocumentoVo());
        setSubirContrato(false);
        getContratoDocumentoVo().setId(idDoctoConv);
        PrimeFaces.current().executeScript(";$(adjuntarArchivo).modal('show');;");
        setContratoDocumentoVo(cvConvenioDocumentoImpl.buscarPorId(contratoDocumentoVo.getId()));
    }

    public void agregarDocumentos() {
        List<Vo> ltemp = new ArrayList<>();
        for (DocumentoVO voSelected : getListaDoctos()) {
            if (voSelected.isSelected()) {
                ltemp.add(voSelected);
            }
        }
        cvConvenioDocumentoImpl.guardar(sesion.getUsuarioSesion().getId(), ltemp, getLstConveniosTabs().get(indice).getId());
        doctosPorConvenio(getIndice(), getLstConveniosTabs().get(getIndice()).getId());
    }

    public void agregarArchivoDocumento(int idDocConv) {
        setContratoDocumentoVo(new ContratoDocumentoVo());
        getContratoDocumentoVo().setId(idDocConv);
        PrimeFaces.current().executeScript(
                ";$(dialogoAgregarArchivoDocto"
                + getLstConveniosTabs().get(getIndice()).getId()
                + ").modal('show');;"
        );
        setContratoDocumentoVo(cvConvenioDocumentoImpl.buscarPorId(contratoDocumentoVo.getId()));

    }

    public void quitarArchivoDocumento(int idDoctoConvenio) {
        setContratoDocumentoVo(new ContratoDocumentoVo());
        getContratoDocumentoVo().setId(idDoctoConvenio);
        cvConvenioDocumentoImpl.eliminar(sesion.getUsuarioSesion().getId(), contratoDocumentoVo.getId());
        getLstConveniosTabs().get(getIndice()).setListaConvenioDocumento(cvConvenioDocumentoImpl.traerDoctosPorConveni(getLstConveniosTabs().get(getIndice()).getId(), null, null));
    }

    public void quitarSoloArchivoDocumento(int idDoctoConvenio, int idDoctoConvenioAdjunto) {
        setContratoDocumentoVo(new ContratoDocumentoVo());
        getContratoDocumentoVo().setId(idDoctoConvenio);
        getContratoDocumentoVo().getAdjuntoVO().setId(idDoctoConvenioAdjunto);
        siAdjuntoServicioRemoto.eliminarArchivo(contratoDocumentoVo.getAdjuntoVO().getId(), sesion.getUsuarioSesion().getId());
        cvConvenioDocumentoImpl.quitarArchivoDocumento(sesion.getUsuarioSesion().getId(), contratoDocumentoVo.getId());
        getLstConveniosTabs().get(getIndice()).setListaConvenioDocumento(cvConvenioDocumentoImpl.traerDoctosPorConveni(getLstConveniosTabs().get(getIndice()).getId(), null, null));
    }

    public void agregarAdjuntoDocumento() {
        try {

            LOGGER.info("Adjunto : " + contratoDocumentoVo.getAdjuntoVO().getNombre());

            contratoDocumentoVo.getAdjuntoVO().setId(
                    siAdjuntoServicioRemoto.saveSiAdjunto(
                            contratoDocumentoVo.getAdjuntoVO().getNombre(),
                            contratoDocumentoVo.getAdjuntoVO().getTipoArchivo(),
                            contratoDocumentoVo.getAdjuntoVO().getUrl(),
                            contratoDocumentoVo.getAdjuntoVO().getTamanio(),
                            sesion.getUsuarioSesion().getId()
                    )
            );
            cvConvenioDocumentoImpl.agregarArchivo(
                    sesion.getUsuarioSesion().getId(),
                    getContratoDocumentoVo(),
                    contratoDocumentoVo.getAdjuntoVO().getId()
            );
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public void actualizarDocto() {
        cvConvenioDocumentoImpl.agregarArchivo(sesion.getUsuarioSesion().getId(), contratoDocumentoVo, contratoDocumentoVo.getAdjuntoVO().getId());
        getLstConveniosTabs().get(indice).setListaConvenioDocumento(cvConvenioDocumentoImpl.traerDoctosPorConveni(getLstConveniosTabs().get(indice).getId(), null, null));
        FacesUtils.addInfoMessage("Se actualizaron los datos.");
    }
///////////////////////////

    public void quitarRelacionConvCondicion(int idCondicionConvenio) {
        setVo(new Vo());
        getVo().setId(idCondicionConvenio);
        cvConvenioCondicionPagoImpl.eliminar(sesion.getUsuarioSesion().getId(), vo.getId());
        setVo(new Vo());
        getLstConveniosTabs().get(indice).setListaConvenioCondicion(cvConvenioCondicionPagoImpl.traerCondicionesPago(getLstConveniosTabs().get(getIndice()).getId()));
        // llenar las hitos de pago 
        getLstConveniosTabs().get(indice).setListaConvenioHito(cvConvenioHitoImpl.traerHitosPorConvenio(getLstConveniosTabs().get(getIndice()).getId()));
    }

    public void agregarConvCondicionPago() {
        List<CatalogoContratoVo> ltemp = new ArrayList<>();
        for (CatalogoContratoVo voSelected : getListaCondicionesPago()) {
            if (voSelected.isSelected()) {
                ltemp.add(voSelected);
            }
        }
        cvConvenioCondicionPagoImpl.guardar(sesion.getUsuarioSesion().getId(), ltemp, getLstConveniosTabs().get(indice).getId());
        getLstConveniosTabs().get(indice).setListaConvenioCondicion(cvConvenioCondicionPagoImpl.traerCondicionesPago(getLstConveniosTabs().get(getIndice()).getId()));
    }

    public void traerCondicionesPago() {
        setListaCondicionesPago(cvCondicionPagoImpl.traerCondicionFaltante(getLstConveniosTabs().get(getIndice()).getId()));
    }

    /////////////////////////////////////////////////////////////////////////////
    public void traerHitosPago() {
        setListaHitoPago(cvHitoImpl.traerHitoFaltante(getLstConveniosTabs().get(getIndice()).getId()));
    }

    public void agregarConvHitoConvenio() {
        List<CatalogoContratoVo> ltemp = new ArrayList<>();
        for (CatalogoContratoVo voSelected : getListaHitoPago()) {
            if (voSelected.isSelected()) {
                ltemp.add(voSelected);
            }
        }
        cvConvenioHitoImpl.guardar(sesion.getUsuarioSesion().getId(), ltemp,
                getLstConveniosTabs().get(indice).getId());
        getLstConveniosTabs().get(indice).setListaConvenioHito(cvConvenioHitoImpl.traerHitosPorConvenio(getLstConveniosTabs().get(getIndice()).getId()));
    }

    public void quitarRelacionConvHito(int idHitoConvenio) {
        setVo(new Vo());
        getVo().setId(idHitoConvenio);
        cvConvenioHitoImpl.eliminar(sesion.getUsuarioSesion().getId(), vo.getId());
        setVo(new Vo());
        getLstConveniosTabs().get(indice).setListaConvenioHito(cvConvenioHitoImpl.traerHitosPorConvenio(getLstConveniosTabs().get(getIndice()).getId()));
        // llenar las condiciones de pago
        getLstConveniosTabs().get(indice).setListaConvenioHito(cvConvenioHitoImpl.traerHitosPorConvenio(getLstConveniosTabs().get(getIndice()).getId()));

    }

    public void subirArchivo(FileUploadEvent fileEntryEvent) {
        LOGGER.info("Now with AlmacenDocumentos ....");
        fileUpload = fileEntryEvent.getFile();
        AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
        //
        try {
            Path path = Paths.get("/tmp/" + fileUpload.getFileName());
            Files.write(path, fileUpload.getContent());

            DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileUpload.getContent());
            String extFile = FilenameUtils.getExtension(fileUpload.getFileName());

            if (isSubirProveedor()) {
                documentoAnexo.setNombreBase(this.getDocProveedor().getDocumento() + '.' + extFile);
                documentoAnexo.setTipoMime(fileUpload.getContentType());
                documentoAnexo.setRuta(getSubDirectorioDocumento());
                almacenDocumentos.guardarDocumento(documentoAnexo);
                if (!getLstConveniosTabs().isEmpty()
                        && getLstConveniosTabs().get(getIndice()).getId() > 0) {
                    getLstConveniosTabs()
                            .get(getIndice())
                            .setAdjuntoVO(buildAdjuntoVO(documentoAnexo));
                    //
                    getLstConveniosTabs().get(getIndice()).getAdjuntoVO().setId(
                            siAdjuntoServicioRemoto.saveSiAdjunto(
                                    getLstConveniosTabs().get(getIndice()).getAdjuntoVO().getNombre(),
                                    getLstConveniosTabs().get(getIndice()).getAdjuntoVO().getTipoArchivo(),
                                    getLstConveniosTabs().get(getIndice()).getAdjuntoVO().getUrl(),
                                    getLstConveniosTabs().get(getIndice()).getAdjuntoVO().getTamanio(),
                                    sesion.getUsuarioSesion().getId()
                            )
                    );
                    pvClasificacionArchivoImpl.guardar(
                            sesion.getUsuarioSesion().getId(),
                            this.getDocProveedor().getId(),
                            getLstConveniosTabs().get(getIndice()).getAdjuntoVO().getId()
                    );
                    getLstConveniosTabs().get(indice).getProveedorVo().setLstDocsProveedor(pvClasificacionArchivoImpl.traerArchivoPorProveedorOid(getLstConveniosTabs().get(indice).getProveedorVo().getIdProveedor(), 0));
                } else {
                    ProveedorModel proveedorBean = (ProveedorModel) FacesUtils.getManagedBean(FacesContext.getCurrentInstance(), "proveedorBean");
                    proveedorBean.subirArchivo(path.toFile());
                }

            } else if (isSubirContrato()) {
                documentoAnexo.setNombreBase(fileUpload.getFileName());
                documentoAnexo.setRuta(getSubDirectorioDocumento());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                getLstConveniosTabs().
                        get(getIndice())
                        .setAdjuntoVO(buildAdjuntoVO(documentoAnexo));

                getLstConveniosTabs().get(getIndice()).getAdjuntoVO().setId(
                        siAdjuntoServicioRemoto.saveSiAdjunto(
                                getLstConveniosTabs().get(getIndice()).getAdjuntoVO().getNombre(),
                                getLstConveniosTabs().get(getIndice()).getAdjuntoVO().getTipoArchivo(),
                                getLstConveniosTabs().get(getIndice()).getAdjuntoVO().getUrl(),
                                getLstConveniosTabs().get(getIndice()).getAdjuntoVO().getTamanio(),
                                sesion.getUsuarioSesion().getId()
                        )
                );
                cvConvenioAdjuntoImpl.guardar(
                        sesion.getUsuarioSesion().getId(),
                        getLstConveniosTabs().get(indice).getId(),
                        getLstConveniosTabs().get(getIndice()).getAdjuntoVO().getId()
                );
                getLstConveniosTabs().get(indice).setListaArchivoConvenio(cvConvenioAdjuntoImpl.traerPorConvenio(getLstConveniosTabs().get(indice).getId()));
            } else if (isSubirListaPrecio()) {
                try {
                    //lstConveniosTabs.get(getIndice()).setListaArticulo(new ArrayList<ConvenioArticuloVo>());
                    lstConveniosTabs.get(getIndice()).getListaArticulo().addAll(convenioServicioRemoto.cargarArchivoPrecio(path.toFile()));
                } catch (Exception ex) {
                    Logger.getLogger(ContratoBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                documentoAnexo.setNombreBase(fileUpload.getFileName());
                documentoAnexo.setRuta(getSubDirectorioDocumento());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                getContratoDocumentoVo().setAdjuntoVO(buildAdjuntoVO(documentoAnexo));

                agregarAdjuntoDocumento();
                getLstConveniosTabs().get(indice).setListaConvenioDocumento(cvConvenioDocumentoImpl.traerDoctosPorConveni(getLstConveniosTabs().get(getIndice()).getId(), null, null));
            }

            fileUpload.delete();
            setSubirContrato(false);
            PrimeFaces.current().executeScript("$(adjuntarArchivo).modal('hide');");
            FacesUtils.addInfoMessage("Se cargó el documento");
        } catch (IOException | SIAException e) {
            FacesUtils.addErrorMessage("Ocurrio un error: " + e.getMessage());
            UtilLog4j.log.fatal(this, "+ + + ERROR + + +" + e.getMessage(), e);
        } finally {
            if (isSubirProveedor()) {
                setSubirProveedor(false);
            }
        }
    }

    private AdjuntoVO buildAdjuntoVO(DocumentoAnexo documentoAnexo) {
        AdjuntoVO adjunto = new AdjuntoVO();
        adjunto.setUrl(documentoAnexo.getRuta() + documentoAnexo.getNombreBase());
        adjunto.setNombre(documentoAnexo.getNombreBase());
        adjunto.setTipoArchivo(documentoAnexo.getTipoMime());
        adjunto.setTamanio(documentoAnexo.getTamanio());

        return adjunto;
    }

    public void agregarArchivoContrato() {
        setSubirContrato(true);
    }

    public void quitarRelacionConvAdjunto(int idConvenioArchivo) {
        setVo(new Vo());
        getVo().setId(idConvenioArchivo);
        cvConvenioAdjuntoImpl.eliminar(sesion.getUsuarioSesion().getId(), vo.getId());
        setVo(new Vo());
        getLstConveniosTabs().get(indice).setListaArchivoConvenio(cvConvenioAdjuntoImpl.traerPorConvenio(getLstConveniosTabs().get(indice).getId()));

    }
/////////////////////////////////////

    public void elimiarArchivoConvenio() {
        try {

        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public void subirArchivo() {
        //setConvenioAcutal((Convenio) getListaConvenios().getRowData());
    }

    public String getDirectorioDocumento() {
        String dir = "";
        try {
            if (isSubirProveedor()) {
                dir = getDirectorio(getSubDirectorioDocumento());
            } else if (isSubirContrato()) {
                dir = getDirectorio(getSubDirectorioDocumento());
            } else {
                dir = getDirectorio(getSubDirectorioDocumento());
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }

        return dir;
    }

    public String getDirectorio(String codigo) {
        if (this.getDir().isEmpty()) {
            this.setDir(this.parametrosSistemaServicioRemoto.find(1).getUploadDirectory());
        }
        return this.getDir() + codigo;
    }

    public String getSubDirectorioDocumento() {
        String subDir = "";
        try {
            if (isSubirProveedor()) {
                subDir = "CV/Proveedor/" + getLstConveniosTabs().get(getIndice()).getProveedorVo().getRfc() + "/";
            } else if (isSubirContrato()) {
                subDir = "CV/" + getLstConveniosTabs().get(getIndice()).getNumero() + "/";
            } else {
                subDir = "CV/" + getLstConveniosTabs().get(getIndice()).getNumero() + "/Doctos" + "/";
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }

        LOGGER.info(this, "Subdir {0}", new Object[]{subDir});

        return subDir;
    }

    /**
     * MLUIS: Este metodo esta incompleto, la consulta esta nativa pero la vista
     * esta en JPA y se ocupara de dos lados e es por esto que se transformo de
     * nativa JPA. una tarde para cambiarlo
     *
     * @param event
     */
    public void buscarPorProveedor() {
        //PrimeFaces.current().executeScript(";mostrarElemento('frmPrincipalContrato', 'btnQuitarProveedor');;");
        setListaConvenios(convenioServicioRemoto.traerConveniosPorProveedorPermisos(getIdProveedor(),
                sesion.getUsuarioSesion().getId(), sesion.getIdRol(), getFiltroVo().getImporte(), getFiltroVo().getIdMoneda(), getFiltroVo().getFecha(), getFiltroVo().getIdOperador(),
                sesion.getUsuarioSesion().getIdCampo(), getMaximoRegistros(), getFiltroVo().getIdEstado()));

    }

    public void ultimosConvenios() {
        try {
            listaConvenios = this.convenioServicioRemoto.traerConvenios(Constantes.ULTIMOS_CONVENIOS, sesion.getUsuarioSesion().getIdCampo());
        } catch (Exception e) {
            LOGGER.error(e);
        }
        //return getListaConvenios();
    }
//
//    public void conveniosPorVencer() {
//	traerConvenioPorVencer();
//    }

    public void conveniosIncompletos() {
        listaConvenios = this.convenioServicioRemoto.traerConvenios(Constantes.ULTIMOS_CONVENIOS, sesion.getUsuarioSesion().getIdCampo());
    }
//
//    public void conveniosEnProceso() {
//	traerConvenioPorVencer();
//    }

    public String regresar() {
        return "/vistas/contrato/principalContrato";
    }

    public void eliminarConvenio() {

    }

    public void buscarConvenioPorProveedor() {
        try {
            setListaConvenios(convenioServicioRemoto.traerConveniosPorProveedorPermisos(getIdProveedor(),
                    sesion.getUsuarioSesion().getId(), sesion.getIdRol(), getFiltroVo().getImporte(), getFiltroVo().getIdMoneda(), getFiltroVo().getFecha(), getFiltroVo().getIdOperador(),
                    sesion.getUsuarioSesion().getIdCampo(), getMaximoRegistros(), getFiltroVo().getIdEstado()));
//
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public void revisarConVigente() {

        //setContratoVO((ContratoVO) getListaConveniosVigente().getRowData());
        PrimeFaces.current().executeScript(";abrirDialogoModal(datosContrato);");
    }

    public void revisar() {
        PrimeFaces.current().executeScript(";abrirDialogoModal(datosContrato);");
    }

    public void cerrarDatosContrato() {
        getLstConveniosTabs().remove(getIndice());//.setContratoVO(null);

    }

    public void administrarContrato(SelectEvent<ContratoVO> event) {
        ContratoVO con = (ContratoVO) event.getObject();
        int index = getLstConveniosTabs().size();
        contratoVo.setProveedorVo(new ProveedorVo());
        contratoVo.getProveedorVo().setTodoContactos(new ArrayList<>());
        llenarDatos(index, con.getId(), getListaConvenios());
        //Activar tabl
        PrimeFaces.current().executeScript(";activarTab('" + con.getNombreTab().trim() + "');");
        //
        ocsPorConvenio(index);
        setIndice(index);
        listaCorreo = new HashMap<>();
        listaUsuarioGerencia = new ArrayList<>();
        listaCorreo.put("para", new ArrayList<>());
        listaCorreo.put("copia", new ArrayList<>());

        Tab tab = new Tab();
        tab.setTitle(con.getNumero());
        tabs.add(tab);
    }

    private void llenarDatos(int index, int con, List<ContratoVO> lista) {

        //buscar el convenio en el tab
        int encontrar = 0;
        for (ContratoVO lstConveniosTab : getLstConveniosTabs()) {
            if (con == lstConveniosTab.getId()) {
                encontrar++;
                break;
            }
        }
        //
        if (encontrar == 0) {
            getLstConveniosTabs().add(index, buscarPorId(con, true));
            llenarDatosContrato(index, con);
        }

        //
        Iterator<ContratoVO> it = lista.iterator();
        while (it.hasNext()) {
            ContratoVO next = it.next();
            if (next.getId() == con) {
                it.remove();
                break;
            }
        }
        //        
    }

    private void llenarDatosContrato(int index, int con) {
        getLstConveniosTabs().get(index).setListaConvenioDocumento(cvConvenioDocumentoImpl.traerDoctosPorConveni(con, null, null));
        evaluacionesPorConvenio(index, con);
        evaluacionesPendientesPorConvenio(index, con);
        getLstConveniosTabs().get(index).setListaConvenioCondicion(cvConvenioCondicionPagoImpl.traerCondicionesPago(con));

        hitosPorConvenio(index, con);
        traerArchivosConvenio(index);
        //
        traerDatosProveedor(index);
        //	traerContratos relacionados
        traerContratosRelacionados(index, con);
        //
        traerGerenciaContrato(index, con);
        //
        traerDiasYRemanente(index, con);
        //
        traerConvenoArticulo(index, con);
        //
        traerConvenioDoctosRh(index, con);
    }

    public void traerConvenoArticulo(int index, int contrato) {
        lstConveniosTabs.get(index).setListaArticulo(new ArrayList<>());
        lstConveniosTabs.get(index).getListaArticulo().addAll(convenioArticuloImpl.traerConvenioArticulo(contrato, lstConveniosTabs.get(getIndice()).getIdCampo()));
    }

    public void traerConvenioDoctosRh(int index, int contrato) {
        lstConveniosTabs.get(index).setDoctosRh(new ArrayList<>());
        lstConveniosTabs.get(index).getDoctosRh().addAll(rhConvenioDocumentosImpl.traerDocumentacionDistintaPorConvenio(contrato));
    }

    public void traerDiasYRemanente(int index, int convenio) {
        if (getLstConveniosTabs().get(index).getFechaVencimiento() != null) {

            if (getLstConveniosTabs().get(index).getFechaVencimiento().compareTo(new Date()) >= 0) {
                getLstConveniosTabs().get(index).setDiasRestantes(siManejoFechaImpl.dias(getLstConveniosTabs().get(index).getFechaVencimiento(), new Date()));
            } else {
                getLstConveniosTabs().get(index).setDiasRestantes(0);
            }
        }
        getLstConveniosTabs().get(index).setAcumulado(ordenImpl.sumaToalOCSPorContrato(getLstConveniosTabs().get(index).getId(), getLstConveniosTabs().get(index).getIdCampo()));
        getLstConveniosTabs().get(index).setRemanente(getLstConveniosTabs().get(index).getMonto() - getLstConveniosTabs().get(index).getAcumulado());

    }

    public void traerGerenciaContrato(int index, int convenio) {
        getLstConveniosTabs().get(index).setListaGerencia(cvConvenioGerenciaImpl.convenioPorGerenica(getLstConveniosTabs().get(index).getId()));
    }

    public void traerContratosRelacionados(int i, int contRel) {
        getLstConveniosTabs().get(i).setListaContratoRelacionado(convenioServicioRemoto.contratosRelacionados(getLstConveniosTabs().get(i).getIdContratoRelacionado(), getLstConveniosTabs().get(i).getId()));

    }

    public void traerDatosProveedor(int i) {
        getLstConveniosTabs().get(i).setProveedorVo(proveedorImpl.traerProveedor(getLstConveniosTabs().get(i).getProveedor(), sesion.getRfcEmpresa()));
    }

    public void traerArchivosConvenio(int i) {
        getLstConveniosTabs().get(i).setListaArchivoConvenio(cvConvenioAdjuntoImpl.traerPorConvenio(getLstConveniosTabs().get(i).getId()));
    }

    public void traerArchivosProveedor(int i) {
        getLstConveniosTabs().get(i).getProveedorVo().setLstDocsProveedor(pvClasificacionArchivoImpl.traerArchivoPorProveedorOid(getLstConveniosTabs().get(i).getProveedorVo().getIdProveedor(), 0));
    }

    public void hitosPorConvenio(int i, int cont) {
        getLstConveniosTabs().get(i).setListaConvenioHito(cvConvenioHitoImpl.traerHitosPorConvenio(cont));
    }

    public void evaluacionesPorConvenio(int i, int contrato) {
        getLstConveniosTabs().get(i).setListaConvenioEvals(cvConvenioEvaluacionImpl.traerEvaluacionTemplate(contrato));
    }

    public void evaluacionesPendientesPorConvenio(int i, int contrato) {
        getLstConveniosTabs().get(i).setListaEvalsPendientes(cvEvaluacionImpl.traerEvaluaciones(contrato, true, false));
    }

    public void quitarDoctoRh(int idDocto) {
        rhConvenioDocumentosImpl.eliminarConvenioDocumento(sesion.getUsuarioSesion().getId(),
                idDocto, lstConveniosTabs.get(indice).getId());
        //
        traerConvenioDoctosRh(indice, lstConveniosTabs.get(indice).getId());
    }

    public void seleccionarContratoRelacionado() {
        int idC = Integer.parseInt(FacesUtils.getRequestParam("idContrato"));
        int index = getLstConveniosTabs().size();
        //
        Iterator<ContratoVO> it = getLstConveniosTabs().iterator();
        int agregar = 0;
        while (it.hasNext()) {
            ContratoVO next = it.next();
            if (next.getId() == idC) {
                agregar++;
                break;
            }
        }
        if (agregar == 0) {
            ContratoVO c = buscarPorId(idC, false);
            //
            getLstConveniosTabs().add(index, c);
            doctosPorConvenio(index, c.getId());
            evaluacionesPorConvenio(index, c.getId());
            evaluacionesPendientesPorConvenio(index, c.getId());
            condicionesPorConvenio(index, c.getId());
            hitosPorConvenio(index, c.getId());
            traerArchivosConvenio(index);
            getLstConveniosTabs().get(index).setProveedorVo(new ProveedorVo());
            traerDatosProveedor(index);
            //	traerContratos relacionados
            traerContratosRelacionados(index, c.getId());
            //
            Iterator<ContratoVO> listaC = getListaConvenios().iterator();
            while (listaC.hasNext()) {
                ContratoVO next = listaC.next();
                if (next.getId().intValue() == c.getId()) {
                    it.remove();
                    break;
                }
            }
        }

    }

    public void doctosPorConvenio(int i, int contrato) {
        getLstConveniosTabs().get(i).setListaConvenioDocumento(cvConvenioDocumentoImpl.traerDoctosPorConveni(contrato, null, null));
    }

    public void condicionesPorConvenio(int i, int cont) {
        getLstConveniosTabs().get(i).setListaConvenioCondicion(cvConvenioCondicionPagoImpl.traerCondicionesPago(cont));
    }

    /*
     public DataModel getTraerConvenios() {
     return getListaConvenios();
     }
     */
    /////////////////////////////////////////////////////////////////////////
    public void iniciarFiltro(ActionEvent e) {
        setIdProveedor(Constantes.MENOS_UNO);

        getFiltroVo().setIdOperador(Constantes.MENOS_UNO);
        getFiltroVo().setIdMoneda(Constantes.MENOS_UNO);
        getFiltroVo().setImporte(Constantes.CERO);
        getFiltroVo().setFecha(null);
    }

    public void buscarContratoPorUsuario(ActionEvent e) {
        setIdProveedor(Constantes.MENOS_UNO);
        getFiltroVo().setIdOperador(Constantes.MENOS_UNO);
        getFiltroVo().setIdMoneda(Constantes.MENOS_UNO);
        getFiltroVo().setImporte(Constantes.CERO);
        getFiltroVo().setFecha(null);
        buscarContratoPorUsuario();
    }

    public void buscarContratoPorUsuario() {
        setListaConvenios(convenioServicioRemoto.buscarConvenioPorUsuario(sesion.getUsuarioSesion().getId(), sesion.getUsuarioSesion().getIdCampo()));
    }

    public void quitarProveedor() {
        setIdProveedor(Constantes.MENOS_UNO);
        getFiltroVo().setProveedor("");
        traerContratosPorProveedor();
    }

    public void quitarFiltroImporte() {
        getFiltroVo().setIdOperador(Constantes.MENOS_UNO);
        getFiltroVo().setIdMoneda(Constantes.MENOS_UNO);
        getFiltroVo().setImporte(Constantes.CERO);
        getFiltroVo().setFiltroImporte(false);
        traerContratosPorProveedor();
        //PrimeFaces.current().executeScript(";ocultarDiv('divFiltroImporte');;");

    }

    public void quitarFiltroAlcance() {
        traerContratosPorProveedor();
        getFiltroVo().setAlcance("");
        getFiltroVo().setFiltroBuscarAlcance(false);
        getFiltroVo().setFiltroAlcance(false);
    }

    public void traerContratosPorProveedor() {
        setListaConvenios(convenioServicioRemoto.traerConveniosPorProveedorPermisos(getIdProveedor(),
                sesion.getUsuarioSesion().getId(), sesion.getIdRol(), getFiltroVo().getImporte(), getFiltroVo().getIdMoneda(), getFiltroVo().getFecha(), getFiltroVo().getIdOperador(),
                sesion.getUsuarioSesion().getIdCampo(), getMaximoRegistros(), getFiltroVo().getIdEstado()));
    }

    //
    public void buscarContratoCambiarProveedor() {
        for (Object col : getLista().get("proveedores")) {
            ProveedorVo p = (ProveedorVo) col;
            if (getIdProveedor() == p.getIdProveedor()) {
                getFiltroVo().setProveedor(p.getNombre());
                getFiltroVo().setAlcance("");
                getFiltroVo().setFiltroAlcance(false);
                getFiltroVo().setFiltroBuscarAlcance(false);
                break;
            }

            //PrimeFaces.current().executeScript(";mostrarElemento('frmPrincipalContrato', 'btnQuitarProveedor');;");
            traerContratosPorProveedor();
        }
    }

    public void buscarContratoOperador() {
        //
        switch (getFiltroVo().getIdOperador()) {
            case 1:
                getFiltroVo().setOperador("Igual ");
                break;
            case 2:
                getFiltroVo().setOperador("Menor a ");
                break;
            case 3:
                getFiltroVo().setOperador("Mayor a ");
                break;
            default:
                getFiltroVo().setImporte(0.0);
                getFiltroVo().setOperador("Sin Cond.");
                break;
        }
        getFiltroVo().setFiltroImporte(true);

        traerContratosPorProveedor();
    }

    public void cambiarMaximoRegistros() {
        traerContratosPorProveedor();
    }

    public void iniciarFiltroImporte() {
        getFiltroVo().setFiltroImporte(!getFiltroVo().isFiltroImporte());
        getFiltroVo().setIdMoneda(-1);
        getFiltroVo().setIdOperador(-1);
        getFiltroVo().setImporte(0);
        traerContratosPorProveedor();
    }

    public void iniciarFiltroalcance() {
        getFiltroVo().setAlcance("");
        //getFiltroVo().setFiltroAlcance(!getFiltroVo().isFiltroAlcance());
        getFiltroVo().setFiltroBuscarAlcance(!getFiltroVo().isFiltroBuscarAlcance());
//
        traerContratosPorProveedor();
    }

    public void buscarPorAlcance() {
        if (getFiltroVo().isFiltroAlcance()) {
            getFiltroVo().setFiltroAlcance(false);
        }
        getFiltroVo().setFiltroBuscarAlcance(true);

    }

    public void reiniciarListaConvenio() {
        if (getFiltroVo().getAlcance().length() > 3) {
            getFiltroVo().setFiltroBuscarAlcance(false);
            getFiltroVo().setFiltroAlcance(true);
        } else {
            getFiltroVo().setFiltroBuscarAlcance(false);
            getFiltroVo().setFiltroAlcance(false);
            getFiltroVo().setAlcance("");
            traerContratosPorProveedor();
        }
    }

    public void buscarContratoSinProveedor() {
        setIdProveedor(Constantes.MENOS_UNO);
        traerContratosPorProveedor();
    }

    /*
    public void buscarContratoImporte() {
        if ((event.getNewValue() != null) && (Long.valueOf(((Integer) event.getNewValue()).longValue()) > 0) && (!event.getNewValue().toString().equals(""))) {
            try {
                Object o = event.getNewValue();
                if (o != null) {
                    Double d = (Double) o;
                    if (!d.isNaN()) {
                        getFiltroVo().setImporte(d);
                        traerContratosPorProveedor();
                    }
                }
                getFiltroVo().setFiltroImporte(true);
                //PrimeFaces.current().executeScript(";llenarEtiqueta('frmPrincipalContrato', 'cjImporte', '" + getFiltroVo().getImporte() + "');;");
                //PrimeFaces.current().executeScript(";mostrarDiv('divFiltroImporte');;");
            } catch (Exception e) {
                UtilLog4j.log.fatal(e);
            }
        }

    }
     */
    public void buscarContratoMoneda() {
        switch (getFiltroVo().getIdMoneda()) {
            case 1:
                getFiltroVo().setMoneda("MXN");
                break;
            case 2:
                getFiltroVo().setMoneda("USD");
                break;
            case 3:
                getFiltroVo().setMoneda("EURO");
                break;
            default:
                getFiltroVo().setMoneda("S/M");
                break;
        }
        getFiltroVo().setFiltroImporte(true);
        //PrimeFaces.current().executeScript(";valorSeleccionado('frmPrincipalContrato', 'cboMoneda', 'cjMoneda');;");
        //PrimeFaces.current().executeScript(";mostrarDiv('divFiltroImporte');;");

        traerContratosPorProveedor();
    }

    /*
    public void buscarContratoCambiarEstado() {
        if (event.getNewValue() != null) {
            getFiltroVo().setIdEstado((Integer) event.getNewValue());
            traerContratosPorProveedor();
        }
    }

    public void buscarConvenioPorImporte() {
        traerContratosPorProveedor();
    }

    public void buscarContratoPorFecha() {
        getFiltroVo().setFecha((Date) event.getNewValue());
        traerContratosPorProveedor();
    }
     */
    public DataModel getTraerReporteFechas() {
        DataModel retVal = null;

        try {
            retVal = new ListDataModel(getListaConvenios());
        } catch (Exception e) {
            LOGGER.fatal(e);
        }

        return retVal;
    }

    public void buscarContratoPorGrafica() {
        String[] cad = getMesAnio().split("-");
        List<OrdenVO> lo = autorizacionesOrdenImpl.traerOrdenSolicidasPorMesAnio(Integer.parseInt(cad[0]), Integer.parseInt(cad[1]), OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId(), sesion.getUsuarioSesion().getIdCampo(), lstConveniosTabs.get(indice).getNumero());
        lstConveniosTabs.get(getIndice()).setListaOrdenConvenio(lo);

    }

    public void traerOcsPorconvenio() {
        ocsPorConvenio(getIndice());
//	traerOCSConvenio(getIndice());
    }

    ////// //////////////////LISTA DE PRECIO
    public List<CategoriaVo> getListaCatPrin() {
        try {
            return getCategoriaVo().getListaCategoria();
        } catch (Exception ex) {
            Logger.getLogger(ContratoBean.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void seleccionarCategoria(SelectEvent event) {
        CategoriaVo con = (CategoriaVo) event.getObject();
        getCategoriaVo().setId(con.getId());
        llenarCategoria(con.getId());
        //
        //setListaCategoría(new ArrayList<>());
        listaCategoría = siCategoriaImpl.traerCategoriMenosPrincipalMenosSubcategorias(categoriaVo.getId());
        if (getCategoriaVo().getListaCategoria() == null
                || getCategoriaVo().getListaCategoria().isEmpty()) {
            verArticulos();
        }
        setArticulosResultadoBqda(obtenerArticulosItems("", sesion.getUsuarioSesion().getIdCampo(), 0, ""));
        this.setArticuloTx("");
    }

    public void verArticulos() {
        listaArticulos = articuloImpl.obtenerArticulos(null, sesion.getUsuarioSesion().getIdCampo(), this.getCategoriaVo().getId(),
                catCodigo(this.getCategoriasSeleccionadas().size() > 2
                        ? this.getCategoriasSeleccionadas().subList(2, this.getCategoriasSeleccionadas().size())
                        : new ArrayList<>()
                ));
        //
    }

    private String catCodigo(List<CategoriaVo> categorias) {
        StringBuilder codigosTxt = new StringBuilder();
        for (CategoriaVo cat : categorias) {
            codigosTxt.append(" and upper(a.CODIGO) like upper('%").append(cat.getCodigo()).append("%') ");
        }
        return codigosTxt.toString();
    }

    private void iniciarCatSel() {
        CategoriaVo c = new CategoriaVo();
        c.setNombre("Pricipales");
        c.setId(Constantes.CERO);
        categoriasSeleccionadas.add(c);
    }

    public void llenarCategoria(int idCategoria) {
        categoriaVo = siRelCategoriaImpl.traerCategoriaPorCategoria(idCategoria, null, sesion.getUsuarioSesion().getIdCampo());
        categoriasSeleccionadas.add(categoriaVo);
    }

    public void seleccionarCategoriaCabecera(int id) {
        //int id = Integer.parseInt(FacesUtils.getRequestParameter("idCatSelecionada"));
        //
        CategoriaVo c = categoriasSeleccionadas.get(indice);
        if (indice == 0) {
            categoriaVo = new CategoriaVo();
            categoriaVo.setListaCategoria(siCategoriaImpl.traerCategoriaPrincipales());
            categoriasSeleccionadas = new ArrayList<>();
            iniciarCatSel();
            listaCategoría = new ArrayList<>();
        } else {
            categoriaVo = siRelCategoriaImpl.traerCategoriaPorCategoria(c.getId(), null, sesion.getUsuarioSesion().getIdCampo());
            if (c.getId() != categoriaVo.getId()) {
                categoriasSeleccionadas.add(categoriaVo);// limpiar lista seleccionadas
            }
            if ((indice + 1) < categoriasSeleccionadas.size()) {
                for (int i = (categoriasSeleccionadas.size() - 1); i > indice; i--) {
                    categoriasSeleccionadas.remove(i);
                }
            }
        }

        if (categoriasSeleccionadas.size() < 3) {
            listaArticulos = new ArrayList<>();
        } else {
            verArticulos();
        }
        traerArticulosItemsLstCat();
    }

    public void traerArticulosItemsLstCat() {
        setArticulosResultadoBqda(obtenerArticulosItems("", sesion.getUsuarioSesion().getIdCampo(), 0, ""));
        this.setArticuloTx("");
    }

    public void traerArticulosItemsListener(String cadena) {
        if ((cadena != null && !cadena.isEmpty() && cadena.length() > 2)
                || (cadena == null || cadena.isEmpty())) {
            traerArticulosItemsLst(cadena);
        }
        PrimeFaces.current().executeScript(";marcarBusqueda();");
    }

    public void traerArticulosItemsLst(String cadena) {
        if ((cadena != null && !cadena.isEmpty() && cadena.length() > 2)
                || (cadena == null || cadena.isEmpty())) {
            setArticulosResultadoBqda(obtenerArticulosItems(cadena, sesion.getUsuarioSesion().getIdCampo(),
                    Constantes.CERO,
                    getCodigos(this.getCategoriasSeleccionadas().size() > 1
                            ? this.getCategoriasSeleccionadas().subList(1, this.getCategoriasSeleccionadas().size())
                            : new ArrayList<>())));
        }
    }

   

    public List<ArticuloVO> obtenerArticulosItems(String cadenaDigitada, int campoID, int categoriaID, String codigosCategorias) {
        return articuloImpl.obtenerArticulos(cadenaDigitada, campoID, categoriaID, codigosCategorias);
    }

    private String getCodigos(List<CategoriaVo> categorias) {
        String codigosTxt = "";
        for (CategoriaVo cat : categorias) {
            codigosTxt = codigosTxt + " and upper(a.CODIGO) like upper('%" + cat.getCodigo() + "%') ";
        }
        return codigosTxt;
    }

    private String filtrosCadena(String cadena) {
        String[] output = cadena.split("\\%");
        StringBuilder cadenaNombre = new StringBuilder("and ((");
//        StringBuilder cadenaCodigo = new StringBuilder(") or (");
        String and = "";
        for (String s : output) {
            cadenaNombre.append(and).append("upper(a.NOMBRE||a.CODIGO_INT) like upper('%").append(s).append("%') ");
//            cadenaCodigo.append(and).append("upper(a.CODIGO_INT) like upper('%").append(s).append("%') ");                        
            and = " and ";
        }
//        return cadenaNombre.toString()+cadenaCodigo.toString()+"))";
        return cadenaNombre.toString() + "))";
    }

    public void seleccionarArticulo(SelectEvent event) {
        ArticuloVO con = (ArticuloVO) event.getObject();
        setArticuloVO(new ArticuloVO());
        setArticuloVO(con);
        llenarDatosCambiarArticulo();

    }

    public void seleccionarResultadoBA(SelectEvent event) {
        try {
            SelectItem artItem = (SelectItem) event.getObject();
            cambiarArticuloBda((ArticuloVO) artItem.getValue());
            //getArticulosResultadoBqda().remove(artItem);
            //}
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void cargarListaPrecio() {
        setSubirListaPrecio(Constantes.TRUE);
        PrimeFaces.current().executeScript("$(adjuntarArchivo).modal('show');");
    }

    public void seleccionarResultadoCargaExcel(SelectEvent event) {
        try {
            SelectItem artItem = (SelectItem) event.getObject();
            cambiarArticuloExcel((ConvenioArticuloVo) artItem.getValue());
            //getArticulosResultadoBqda().remove(artItem);
            //}
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void cambiarArticuloExcel(ConvenioArticuloVo articuloVO) {
        try {
            ConvenioArticuloVo cav = new ConvenioArticuloVo();
            cav.setIdConvenioArticulo(Constantes.CERO);
            cav.setIdConvenio(lstConveniosTabs.get(getIndice()).getId());
            cav.setIdArticulo(articuloVO.getId());
            cav.setNombre(articuloVO.getNombre());
            cav.setUnidadNombre(articuloVO.getUnidadNombre());
            cav.setCantidad(articuloVO.getCantidad());
            cav.setPrecioUnitario(articuloVO.getPrecioUnitario());
            cav.setImporte(articuloVO.getImporte());
            lstConveniosTabs.get(getIndice()).getListaArticulo().add(cav);
            //
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void llenarDatosCambiarArticulo() {
        ConvenioArticuloVo cav = new ConvenioArticuloVo();
        cav.setIdArticulo(articuloVO.getId());
        lstConveniosTabs.get(getIndice()).getListaArticulo().add(cav);
    }

    public void cambiarArticuloBda(ArticuloVO articuloVO) {
        try {
            ConvenioArticuloVo cav = new ConvenioArticuloVo();
            cav.setIdConvenioArticulo(Constantes.CERO);
            cav.setIdConvenio(lstConveniosTabs.get(getIndice()).getId());
            cav.setIdArticulo(articuloVO.getId());
            cav.setItem("");
            cav.setCodigo(articuloVO.getCodigo());
            cav.setNombre(articuloVO.getNombre());
            cav.setUnidadNombre(articuloVO.getUnidadNombre());
            cav.setRegistrado(Constantes.TRUE);
            cav.setUnidadId(articuloVO.getUnidadId());
            cav.setCantidad(Constantes.CERO);
            cav.setPrecioUnitario(Constantes.CERO);
            lstConveniosTabs.get(getIndice()).getListaArticulo().add(cav);
            //
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void eliminarConvenioArticulo(ConvenioArticuloVo convArtVo) {
        this.convenioArticuloVo = convArtVo;
        if (convenioArticuloVo.getIdConvenioArticulo() > 0) {
            convenioArticuloImpl.eliminar(convenioArticuloVo, sesion.getUsuarioSesion().getId());
        }
        lstConveniosTabs.get(getIndice()).getListaArticulo().remove(convenioArticuloVo);
    }

    public void agregarListaArticulosContratoRelacionado() {
        lstConveniosTabs.get(getIndice()).getListaArticulo().addAll(convenioArticuloImpl.traerArticulosConvenioAnterior(lstConveniosTabs.get(getIndice()).getIdContratoRelacionado(), lstConveniosTabs.get(getIndice()).getId(), lstConveniosTabs.get(getIndice()).getIdCampo()));
    }

    public void quitarListaPrecio() {
        for (ConvenioArticuloVo convenioArticuloVo1 : lstConveniosTabs.get(getIndice()).getListaArticulo()) {
            if (convenioArticuloVo1.getIdConvenioArticulo() > Constantes.CERO) {
                convenioArticuloImpl.eliminar(convenioArticuloVo1, sesion.getUsuarioSesion().getId());
            }
        }
        lstConveniosTabs.get(getIndice()).setListaArticulo(new ArrayList<>());
    }

    public void modificarLista(ConvenioArticuloVo convArtVo) {
        this.convenioArticuloVo = convArtVo;
        convenioArticuloVo.setGuardado(Constantes.FALSE);
    }

    public void guardarListaPrecio() {
        boolean lleno = true;
        for (ConvenioArticuloVo cartvo : lstConveniosTabs.get(getIndice()).getListaArticulo()) {
            if (cartvo.getCantidad() == Constantes.CERO || cartvo.getPrecioUnitario() == Constantes.CERO
                    || cartvo.getUnidadId() == Constantes.CERO) {
                lleno = false;
                break;
            }
        }
        if (lleno) {
            convenioArticuloImpl.guardar(sesion.getUsuarioSesion().getId(), lstConveniosTabs.get(getIndice()).getListaArticulo(), lstConveniosTabs.get(getIndice()).getId(), lstConveniosTabs.get(getIndice()).getIdCampo());
            lstConveniosTabs.get(getIndice()).setListaArticulo(new ArrayList<>());
            lstConveniosTabs.get(getIndice()).setListaArticulo(convenioArticuloImpl.traerConvenioArticulo(lstConveniosTabs.get(getIndice()).getId(), lstConveniosTabs.get(getIndice()).getIdCampo()));
        } else {
            FacesUtils.addErrorMessage("En necesario agregar unidad,  cantidades y precios unitarios");
        }
    }

    public void actualizarCtasProveedor() {
        actualizarCuentas(indice);
    }

    public void actualizarEvaluaciones() {
        actualizarEvaluaciones(indice);
    }

    public void actualizarEvaluacionesPendientes() {
        actualizarEvaluacionesPendientes(indice);
    }

    public void actualizarEvaluacionesPendientes(int i) {
        getLstConveniosTabs().get(i).setListaEvalsPendientes(cvEvaluacionImpl.traerEvaluaciones(getLstConveniosTabs().get(i).getId(), true, false));
    }

    public void actualizarRepLegalProveedor() {
        actualizarRepLegal(indice);
    }

    public void actualizarRepTecnicoProveedor() {
        actualizarRepTecnico(indice);
    }

    public void actualizarContactosProveedor() {
        actualizarContactos(indice);
    }

    public void actualizarDocumentosProveedor() {
        actualizarDocumentos(indice);
    }

    public void actualizarCuentas(int i) {
        getLstConveniosTabs().get(i).getProveedorVo().setCuentas(cuentaBancoProveedorImpl.traerCuentas(getLstConveniosTabs().get(i).getProveedor(), sesion.getRfcEmpresa()));
    }

    public void actualizarEvaluaciones(int i) {
        getLstConveniosTabs().get(i).setListaConvenioEvals(cvConvenioEvaluacionImpl.traerEvaluacionTemplate(getLstConveniosTabs().get(i).getId()));
    }

    public void actualizarRepLegal(int i) {
        getLstConveniosTabs().get(i).getProveedorVo().setLstRL(contactoProveedorImpl.traerContactoPorProveedor(getLstConveniosTabs().get(i).getProveedor(), Constantes.CONTACTO_REP_LEGAL));
    }

    public void actualizarRepTecnico(int i) {
        getLstConveniosTabs().get(i).getProveedorVo().setLstRT(contactoProveedorImpl.traerContactoPorProveedor(getLstConveniosTabs().get(i).getProveedor(), Constantes.CONTACTO_REP_TECNICO));
    }

    public void actualizarContactos(int i) {
        getLstConveniosTabs().get(i).getProveedorVo().setContactos(contactoProveedorImpl.traerContactoPorProveedor(getLstConveniosTabs().get(i).getProveedor(), Constantes.CONTACTO_REP_COMPRAS));
        //getLstConveniosTabs().get(i).getProveedorVo().getContactos().addAll(contactoProveedorImpl.traerContactoPorProveedor(getLstConveniosTabs().get(i).getProveedor(), 0));
    }

    public void actualizarDocumentos(int i) {
        getLstConveniosTabs().get(i).getProveedorVo().setLstDocsProveedor(pvClasificacionArchivoImpl.traerArchivoPorProveedorOid(getLstConveniosTabs().get(i).getProveedor(), 0));
    }
}
