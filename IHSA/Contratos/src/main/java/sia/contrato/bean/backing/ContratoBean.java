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
import sia.inventarios.service.ArticuloRemote;
import sia.modelo.CvTipo;
import sia.modelo.contrato.vo.ClasificacionVo;
import sia.modelo.contrato.vo.ContratoDocumentoVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.ConvenioArticuloVo;
import sia.modelo.contrato.vo.FiltroVo;
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
    private TabView tabView;
    @Getter
    @Setter
    private int activeTab;

    @PostConstruct
    public void irContrato() {
        tabView = new TabView();
        activeTab = 0;
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

            // int index = lstConveniosTabs.indexOf(contratoVo);
            contratoVo.setProveedorVo(new ProveedorVo());
            contratoVo.getProveedorVo().setTodoContactos(new ArrayList<>());
            //llenarDatos(index, contratoVo.getId());
            //Activar tabl
            //

            //setIndice(index);
            listaCorreo = new HashMap<>();
            listaUsuarioGerencia = new ArrayList<>();
            listaCorreo.put("para", new ArrayList<>());
            listaCorreo.put("copia", new ArrayList<>());
            llenarDatosContrato(contratoVo.getId());
            //
            categoriasSeleccionadas = new ArrayList<>();
            iniciarCatSel();

            categoriaVo = new CategoriaVo();
            categoriaVo.setListaCategoria(siCategoriaImpl.traerCategoriaPrincipales());
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
        contratoVo.getProveedorVo().getTodoContactos().remove(con);
    }

    public void quitarUsuarioPara(int ind) {
        getListaCorreo().get("para").remove(ind);
        //
        //setContactoProveedorVO(new ContactoProveedorVO());
        if (getContactoProveedorVO() != null) {
            contratoVo.getProveedorVo().getTodoContactos().add(getContactoProveedorVO());
        }
    }

    public void traerEmpleadoPorGerencia() {
        if (getIdGerencia() > 0) {
            traerUsuarioPorGerencia();
        }
    }

    public void traerUsuarioPorGerencia() {
        setListaUsuarioGerencia(apCampoUsuarioRhPuestoImpl.traerUsurioGerenciaCampo(getIdGerencia(), contratoVo.getIdCampo()));
        //
        UsuarioResponsableGerenciaVo urgv = apCampoGerenciaImpl.buscarResponsablePorGerencia(getIdGerencia(), contratoVo.getIdCampo());
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
                contratoVo.getProveedorVo().getTodoContactos().add(getContactoProveedorVO());
            }
        }
    }

    public void inicioContratoFormalizado() {
        if (validaContratoFormalizado()) {
            if (validaProveedorContrato()) {
                listaCorreo = new HashMap<>();
                llenarCorreoCopia();
                traerContactosPorProveedor();
                PrimeFaces.current().executeScript(";$(dialogoFormalizarContrato).modal('show');");
            } else {
                FacesUtils.addErrorMessage("Los datos del proveedor estan incompletos.");
            }
        } else {
            FacesUtils.addErrorMessage("Es necesario agregar todos los datos del contrato para poder ser cambiado de estado.");
        }
    }

    public void traerContactosPorProveedor() {
        contratoVo.getProveedorVo().setTodoContactos(contactoProveedorImpl.traerTodosContactoPorProveedor(contratoVo.getProveedorVo().getIdProveedor()));
    }

    public void llenarCorreoCopia() {
        List<ContactoProveedorVO> lc = new ArrayList<>();
        ContactoProveedorVO cpVo = new ContactoProveedorVO();
        List<GerenciaVo> lg = cvConvenioGerenciaImpl.convenioPorGerenica(contratoVo.getId());

        // gerencia compras
        UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo = apCampoGerenciaImpl.buscarResponsablePorGerencia(Constantes.GERENCIA_ID_COMPRAS, contratoVo.getIdCampo());
        if (usuarioResponsableGerenciaVo != null) {
            cpVo.setNombre(usuarioResponsableGerenciaVo.getNombreUsuario());
            cpVo.setCorreo(usuarioResponsableGerenciaVo.getEmailUsuario());
            lc.add(cpVo);
        }

        //Gerencia tesoreria
        usuarioResponsableGerenciaVo = apCampoGerenciaImpl.buscarResponsablePorGerencia(Constantes.GERENCIA_ID_DIRECCION_FINANZAS, contratoVo.getIdCampo());
        if (usuarioResponsableGerenciaVo != null) {
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(usuarioResponsableGerenciaVo.getNombreUsuario());
            cpVo.setCorreo(usuarioResponsableGerenciaVo.getEmailUsuario());
            lc.add(cpVo);
        }
        //Gerencia de juridico
        usuarioResponsableGerenciaVo = apCampoGerenciaImpl.buscarResponsablePorGerencia(Constantes.GERENCIA_JURIDICO, contratoVo.getIdCampo());
        if (usuarioResponsableGerenciaVo != null) {
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(usuarioResponsableGerenciaVo.getNombreUsuario());
            cpVo.setCorreo(usuarioResponsableGerenciaVo.getEmailUsuario());
            lc.add(cpVo);
        }
        //Gerencia de juridico
        usuarioResponsableGerenciaVo = apCampoGerenciaImpl.buscarResponsablePorGerencia(Constantes.GERENCIA_ID_RR_HH, contratoVo.getIdCampo());
        if (usuarioResponsableGerenciaVo != null) {
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(usuarioResponsableGerenciaVo.getNombreUsuario());
            cpVo.setCorreo(usuarioResponsableGerenciaVo.getEmailUsuario());
            lc.add(cpVo);
        }
        // Rol contrato
        for (UsuarioRolVo urVo : siUsuarioRolImpl.traerUsuarioPorRolModulo(Constantes.ROL_ADMINISTRA_CONTRATO, Constantes.MODULO_CONTRATO, contratoVo.getIdCampo())) {
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(urVo.getUsuario());
            cpVo.setCorreo(urVo.getCorreo());
            lc.add(cpVo);
        }

        for (GerenciaVo lg1 : lg) {
            UsuarioResponsableGerenciaVo urgv = apCampoGerenciaImpl.buscarResponsablePorGerencia(lg1.getId(), contratoVo.getIdCampo());
            cpVo = new ContactoProveedorVO();
            cpVo.setNombre(urgv.getNombreUsuario());
            cpVo.setCorreo(urgv.getEmailUsuario());
            lc.add(cpVo);
        }
        listaCorreo.put("copia", lc);
    }

    public boolean validaContratoFormalizado() {
        if (!contratoVo.getListaArchivoConvenio().isEmpty()) {
            if (!contratoVo.getListaConvenioDocumento().isEmpty()) {
                if (!contratoVo.getListaGerencia().isEmpty()) {
                    if (contratoVo.getFechaInicio() != null
                            && contratoVo.getFechaVencimiento() != null
                            && (contratoVo.getIdContratoRelacionado() > 0
                            || contratoVo.getMonto() > 0)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean validaProveedorContrato() {
        return contratoVo.getProveedorVo().getLstRL() != null && !contratoVo.getProveedorVo().getLstRL().isEmpty();
    }

    public void finlizarContratoFormalizado() {
        if (contratoFormalizado()) {	    //
            getLstConveniosTabs().set(getIndice(),
                    buscarPorId(contratoVo.getId(), false));
            llenarDatosContrato(contratoVo.getId());
            FacesUtils.addInfoMessage("Se envío la notificación de contrato formalizado.");
        } else {
            FacesUtils.addErrorMessage("Ocurrio un error al enviar la notificación del contrato formalizado, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
        PrimeFaces.current().executeScript(
                ";$(dialogoFormalizarContrato"
                + contratoVo.getId()
                + ").modal('hide');"
        );
    }

    public boolean contratoFormalizado() {
        try {
            String correo = "";
            for (ContactoProveedorVO lstRL : contratoVo.getProveedorVo().getLstRL()) {
                if (lstRL.isSelected()) {
                    if (correo.isEmpty()) {
                        correo = lstRL.getCorreo();
                    } else {
                        correo += "," + lstRL.getCorreo();
                    }
                }
            }
            convenioServicioRemoto.promoverEstadoConvenio(sesion.getUsuarioSesion().getId(), contratoVo.getId(), Constantes.ESTADO_CONVENIO_ACTIVO, listaCorreo);

            return true;
        } catch (Exception e) {
            notificacionSistemaImpl.enviarExcepcion(sesion.getUsuarioSesion().getId(), correoExcepcion(), getLstConveniosTabs().get(getIndice()), "Error - " + contratoVo.getNumero(), e.toString());
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
            PrimeFaces.current().executeScript(";$(dialogoFiniquitarContrato).modal('show');");
        } else {
            FacesUtils.addErrorMessage("Es necesario agregar todos los datos del contrato para poder ser cambiado de estado.");
        }
    }

    public void finlizarContratoFiniquitado() {
        if (getListaCorreo().get("para").size() > Constantes.CERO) {
            if (contratoFiniquitado()) {	    //
                getLstConveniosTabs().set(getIndice(),
                        buscarPorId(contratoVo.getId(), false));
                llenarDatosContrato(contratoVo.getId());
                FacesUtils.addInfoMessage("Se envío la notificación de contrato finiquitado.");
            } else {
                FacesUtils.addErrorMessage("Ocurrio un error al enviar la notificación del contrato finiquitado, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
            }
            PrimeFaces.current().executeScript(
                    ";$(dialogoFiniquitarContrato"
                    + contratoVo.getId()
                    + ").modal('hide');"
            );
        } else {
            FacesUtils.addInfoMessage("Falta seleccionar el correo para");
        }
    }

    public boolean contratoFiniquitado() {
        try {
            String correo = "";
            convenioServicioRemoto.promoverEstadoConvenio(sesion.getUsuarioSesion().getId(), contratoVo.getId(), Constantes.ESTADO_CONVENIO_FINIQUITO, listaCorreo);

            return true;
        } catch (Exception e) {
            notificacionSistemaImpl.enviarExcepcion(sesion.getUsuarioSesion().getId(), correoExcepcion(), getLstConveniosTabs().get(getIndice()), "Error - " + contratoVo.getNumero(), e.toString());
            UtilLog4j.log.fatal(this, e);
            return false;
        }
    }

    public void eliminarContrato() {
        convenioServicioRemoto.eliminarContrato(sesion.getUsuarioSesion().getId(), contratoVo.getId());
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
            LOGGER.error(ex);
        }

    }

    public void llenarOCSConvenio() {
        contratoVo.setListaOrdenConvenio(ordenImpl.traerOCSPorContrato(contratoVo.getId(), OrdenEstadoEnum.POR_SOLICITAR.getId(), sesion.getUsuarioSesion().getIdCampo()));
        contratoVo.getListaOrdenConvenio().addAll(ordenImpl.traerOCSPorContratoDet(contratoVo.getId(), OrdenEstadoEnum.POR_SOLICITAR.getId(), sesion.getUsuarioSesion().getIdCampo()));
    }

    public List<ContratoVO> traerOCSConvenio() {
        List<ContratoVO> lc = new ArrayList<>();
        lc.addAll(convenioServicioRemoto.consultaOCSPorConvenio(contratoVo.getNumero(), OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId()));
        lc.addAll(convenioServicioRemoto.consultaOCSPorConvenioDet(contratoVo.getNumero(), OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId()));
        return lc;
    }
/////////////////////

    public List<SiOpcionVo> childOpcion(Integer id) {
        if (menu == null && sesion.getUsuarioSesion().getMapaRol().containsKey(Constantes.ROL_ADMINISTRA_CONTRATO)) {

            if (sesion.getUsuarioSesion().getMapaRol() != null) {
                menu = siOpcionImpl.getChildSiOpcion(id, sesion.getUsuarioSesion().getId(), Constantes.MODULO_CONTRATO);
            }
        }
        return menu;
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
                + contratoVo.getId()
                + ").modal('hide');"
        );
        setListaGerencia(new ArrayList<>());
        llenarDatosContrato(contratoVo.getId());
    }

    public void actualizarContratoGenerales() {
        //
        convenioServicioRemoto.actualizarDatosGenerales(sesion.getUsuarioSesion().getId(), getLstConveniosTabs().get(indice), listaGerencia);
        contratoVo.setListaGerencia(cvConvenioGerenciaImpl.convenioPorGerenica(contratoVo.getId()));
        //getLstConveniosTabs().set(indice, convenioServicioRemoto.buscarPorId(contratoVo.getId()));
    }

    public void actualizarContrato() {
        convenioServicioRemoto.actualizar(sesion.getUsuarioSesion().getId(), getLstConveniosTabs().get(getIndice()));
        llenarDatosContrato(
                contratoVo.getId()
        );
        getLstConveniosTabs().set(
                getIndice(), buscarPorId(
                        contratoVo.getId(), true)
        );
        llenarDatosContrato(contratoVo.getId());
    }
/////////////////////////////////////////////////

    public void eliminarGerencia(int idConvGer) {

        cvConvenioGerenciaImpl.eliminar(sesion.getUsuarioSesion().getId(), idConvGer);
        //
        contratoVo.setListaGerencia(cvConvenioGerenciaImpl.convenioPorGerenica(contratoVo.getId()));

    }

    public void quitarGerencia(int ind) {
        getListaGerencia().remove(ind);
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
        cvConvenioDocumentoImpl.guardar(sesion.getUsuarioSesion().getId(), ltemp, contratoVo.getId());
        doctosPorConvenio(getIndice(), contratoVo.getId());
    }

    public void agregarArchivoDocumento(int idDocConv) {
        setContratoDocumentoVo(new ContratoDocumentoVo());
        getContratoDocumentoVo().setId(idDocConv);
        PrimeFaces.current().executeScript(
                ";$(dialogoAgregarArchivoDocto"
                + contratoVo.getId()
                + ").modal('show');;"
        );
        setContratoDocumentoVo(cvConvenioDocumentoImpl.buscarPorId(contratoDocumentoVo.getId()));

    }

    public void quitarArchivoDocumento(int idDoctoConvenio) {
        setContratoDocumentoVo(new ContratoDocumentoVo());
        getContratoDocumentoVo().setId(idDoctoConvenio);
        cvConvenioDocumentoImpl.eliminar(sesion.getUsuarioSesion().getId(), contratoDocumentoVo.getId());
        contratoVo.setListaConvenioDocumento(cvConvenioDocumentoImpl.traerDoctosPorConveni(contratoVo.getId(), null, null));
    }

    public void quitarSoloArchivoDocumento(int idDoctoConvenio, int idDoctoConvenioAdjunto) {
        setContratoDocumentoVo(new ContratoDocumentoVo());
        getContratoDocumentoVo().setId(idDoctoConvenio);
        getContratoDocumentoVo().getAdjuntoVO().setId(idDoctoConvenioAdjunto);
        siAdjuntoServicioRemoto.eliminarArchivo(contratoDocumentoVo.getAdjuntoVO().getId(), sesion.getUsuarioSesion().getId());
        cvConvenioDocumentoImpl.quitarArchivoDocumento(sesion.getUsuarioSesion().getId(), contratoDocumentoVo.getId());
        contratoVo.setListaConvenioDocumento(cvConvenioDocumentoImpl.traerDoctosPorConveni(contratoVo.getId(), null, null));
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
        contratoVo.setListaConvenioDocumento(cvConvenioDocumentoImpl.traerDoctosPorConveni(contratoVo.getId(), null, null));
        FacesUtils.addInfoMessage("Se actualizaron los datos.");
    }
///////////////////////////

    public void quitarRelacionConvCondicion(int idCondicionConvenio) {
        setVo(new Vo());
        getVo().setId(idCondicionConvenio);
        cvConvenioCondicionPagoImpl.eliminar(sesion.getUsuarioSesion().getId(), vo.getId());
        setVo(new Vo());
        contratoVo.setListaConvenioCondicion(cvConvenioCondicionPagoImpl.traerCondicionesPago(contratoVo.getId()));
        // llenar las hitos de pago 
        contratoVo.setListaConvenioHito(cvConvenioHitoImpl.traerHitosPorConvenio(contratoVo.getId()));
    }

    public void agregarConvCondicionPago() {
        List<CatalogoContratoVo> ltemp = new ArrayList<>();
        for (CatalogoContratoVo voSelected : getListaCondicionesPago()) {
            if (voSelected.isSelected()) {
                ltemp.add(voSelected);
            }
        }
        cvConvenioCondicionPagoImpl.guardar(sesion.getUsuarioSesion().getId(), ltemp, contratoVo.getId());
        contratoVo.setListaConvenioCondicion(cvConvenioCondicionPagoImpl.traerCondicionesPago(contratoVo.getId()));
    }

    public void traerCondicionesPago() {
        setListaCondicionesPago(cvCondicionPagoImpl.traerCondicionFaltante(contratoVo.getId()));
    }

    /////////////////////////////////////////////////////////////////////////////
    public void traerHitosPago() {
        setListaHitoPago(cvHitoImpl.traerHitoFaltante(contratoVo.getId()));
    }

    public void agregarConvHitoConvenio() {
        List<CatalogoContratoVo> ltemp = new ArrayList<>();
        for (CatalogoContratoVo voSelected : getListaHitoPago()) {
            if (voSelected.isSelected()) {
                ltemp.add(voSelected);
            }
        }
        cvConvenioHitoImpl.guardar(sesion.getUsuarioSesion().getId(), ltemp,
                contratoVo.getId());
        contratoVo.setListaConvenioHito(cvConvenioHitoImpl.traerHitosPorConvenio(contratoVo.getId()));
    }

    public void quitarRelacionConvHito(int idHitoConvenio) {
        setVo(new Vo());
        getVo().setId(idHitoConvenio);
        cvConvenioHitoImpl.eliminar(sesion.getUsuarioSesion().getId(), vo.getId());
        setVo(new Vo());
        contratoVo.setListaConvenioHito(cvConvenioHitoImpl.traerHitosPorConvenio(contratoVo.getId()));
        // llenar las condiciones de pago
        contratoVo.setListaConvenioHito(cvConvenioHitoImpl.traerHitosPorConvenio(contratoVo.getId()));

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
                        && contratoVo.getId() > 0) {
                    getLstConveniosTabs()
                            .get(getIndice())
                            .setAdjuntoVO(buildAdjuntoVO(documentoAnexo));
                    //
                    contratoVo.getAdjuntoVO().setId(
                            siAdjuntoServicioRemoto.saveSiAdjunto(
                                    contratoVo.getAdjuntoVO().getNombre(),
                                    contratoVo.getAdjuntoVO().getTipoArchivo(),
                                    contratoVo.getAdjuntoVO().getUrl(),
                                    contratoVo.getAdjuntoVO().getTamanio(),
                                    sesion.getUsuarioSesion().getId()
                            )
                    );
                    pvClasificacionArchivoImpl.guardar(
                            sesion.getUsuarioSesion().getId(),
                            this.getDocProveedor().getId(),
                            contratoVo.getAdjuntoVO().getId()
                    );
                    contratoVo.getProveedorVo().setLstDocsProveedor(pvClasificacionArchivoImpl.traerArchivoPorProveedorOid(contratoVo.getProveedorVo().getIdProveedor(), 0));
                } else {
                    ProveedorModel proveedorBean = (ProveedorModel) FacesUtils.getManagedBean(FacesContext.getCurrentInstance(), "proveedorBean");
                    proveedorBean.subirArchivo(path.toFile());
                }

            } else if (isSubirContrato()) {
                documentoAnexo.setNombreBase(fileUpload.getFileName());
                documentoAnexo.setRuta(getSubDirectorioDocumento());
                documentoAnexo.setTipoMime(fileUpload.getContentType());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                contratoVo.setAdjuntoVO(buildAdjuntoVO(documentoAnexo));

                contratoVo.getAdjuntoVO().setId(
                        siAdjuntoServicioRemoto.saveSiAdjunto(
                                contratoVo.getAdjuntoVO().getNombre(),
                                contratoVo.getAdjuntoVO().getTipoArchivo(),
                                contratoVo.getAdjuntoVO().getUrl(),
                                contratoVo.getAdjuntoVO().getTamanio(),
                                sesion.getUsuarioSesion().getId()
                        )
                );
                cvConvenioAdjuntoImpl.guardar(
                        sesion.getUsuarioSesion().getId(),
                        contratoVo.getId(),
                        contratoVo.getAdjuntoVO().getId()
                );
                contratoVo.setListaArchivoConvenio(cvConvenioAdjuntoImpl.traerPorConvenio(contratoVo.getId()));
            } else if (isSubirListaPrecio()) {
                try {
                    //contratoVo.setListaArticulo(new ArrayList<ConvenioArticuloVo>());
                    contratoVo.getListaArticulo().addAll(convenioServicioRemoto.cargarArchivoPrecio(path.toFile()));
                } catch (Exception ex) {
                    Logger.getLogger(ContratoBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                documentoAnexo.setNombreBase(fileUpload.getFileName());
                documentoAnexo.setRuta(getSubDirectorioDocumento());
                documentoAnexo.setTipoMime(fileUpload.getContentType());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                getContratoDocumentoVo().setAdjuntoVO(buildAdjuntoVO(documentoAnexo));

                agregarAdjuntoDocumento();
                contratoVo.setListaConvenioDocumento(cvConvenioDocumentoImpl.traerDoctosPorConveni(contratoVo.getId(), null, null));
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
        contratoVo.setListaArchivoConvenio(cvConvenioAdjuntoImpl.traerPorConvenio(contratoVo.getId()));

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
                subDir = "CV/Proveedor/" + contratoVo.getProveedorVo().getRfc() + "/";
            } else if (isSubirContrato()) {
                subDir = "CV/" + contratoVo.getNumero() + "/";
            } else {
                subDir = "CV/" + contratoVo.getNumero() + "/Doctos" + "/";
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
        setIndice(index);
        contratoVo = buscarPorId(con.getId(), Boolean.TRUE);//new ContratoVO();
        contratoVo.setCodigo(con.getNumero());
        getLstConveniosTabs().add(index, contratoVo);
        contratoVo.setProveedorVo(new ProveedorVo());
        contratoVo.getProveedorVo().setTodoContactos(new ArrayList<>());
        listaConvenios.remove(con);
        llenarDatosContrato(con.getId());
        //Activar tabl
        //PrimeFaces.current().executeScript(";activarTab('" + con.getNombreTab().trim() + "');");
        //
        //ocsPorConvenio(index);
        listaCorreo = new HashMap<>();
        listaUsuarioGerencia = new ArrayList<>();
        listaCorreo.put("para", new ArrayList<>());
        listaCorreo.put("copia", new ArrayList<>());
        categoriasSeleccionadas = new ArrayList<>();
        iniciarCatSel();
        articulosResultadoBqda = new ArrayList<>();
        categoriaVo = new CategoriaVo();
        categoriaVo.setListaCategoria(siCategoriaImpl.traerCategoriaPrincipales());

        activeTab = index + 1;
        tabView.setActiveIndex(activeTab);
    }

    private void llenarDatos(int index, int con) {

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
            getLstConveniosTabs().add(index, contratoVo);
            //  llenarDatosContrato(index, con);
        }

    }

    private void llenarDatosContrato(int con) {
        contratoVo.setListaConvenioDocumento(cvConvenioDocumentoImpl.traerDoctosPorConveni(con, null, null));
        evaluacionesPorConvenio(con);
        evaluacionesPendientesPorConvenio(con);
        contratoVo.setListaConvenioCondicion(cvConvenioCondicionPagoImpl.traerCondicionesPago(con));

        hitosPorConvenio(con);
        traerArchivosConvenio();
        //
        traerDatosProveedor();
        //	traerContratos relacionados
        traerContratosRelacionados(con);
        //
        traerGerenciaContrato(con);
        //
        traerDiasYRemanente(con);
        //
        traerConvenoArticulo(con);
        //
        traerConvenioDoctosRh(con);
        ocsPorConvenio();
        buscarContratoPorGrafica();
    }

    public void traerConvenoArticulo(int contrato) {
        contratoVo.setListaArticulo(new ArrayList<>());
        contratoVo.getListaArticulo().addAll(convenioArticuloImpl.traerConvenioArticulo(contrato, contratoVo.getIdCampo()));
    }

    public void traerConvenioDoctosRh(int contrato) {
        contratoVo.setDoctosRh(new ArrayList<>());
        contratoVo.getDoctosRh().addAll(rhConvenioDocumentosImpl.traerDocumentacionDistintaPorConvenio(contrato));
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

    public void traerGerenciaContrato(int convenio) {
        contratoVo.setListaGerencia(cvConvenioGerenciaImpl.convenioPorGerenica(contratoVo.getId()));
    }

    public void traerContratosRelacionados(int contRel) {
        contratoVo.setListaContratoRelacionado(convenioServicioRemoto.contratosRelacionados(contratoVo.getIdContratoRelacionado(), contratoVo.getId()));

    }

    public void traerDatosProveedor() {
        contratoVo.setProveedorVo(proveedorImpl.traerProveedor(contratoVo.getProveedor(), sesion.getRfcEmpresa()));
    }

    public void traerArchivosConvenio() {
        contratoVo.setListaArchivoConvenio(cvConvenioAdjuntoImpl.traerPorConvenio(contratoVo.getId()));
    }

    public void traerArchivosProveedor(int i) {
        contratoVo.getProveedorVo().setLstDocsProveedor(pvClasificacionArchivoImpl.traerArchivoPorProveedorOid(contratoVo.getProveedorVo().getIdProveedor(), 0));
    }

    public void hitosPorConvenio(int cont) {
        contratoVo.setListaConvenioHito(cvConvenioHitoImpl.traerHitosPorConvenio(cont));
    }

    public void evaluacionesPorConvenio(int contrato) {
        contratoVo.setListaConvenioEvals(cvConvenioEvaluacionImpl.traerEvaluacionTemplate(contrato));
    }

    public void evaluacionesPendientesPorConvenio(int contrato) {
        contratoVo.setListaEvalsPendientes(cvEvaluacionImpl.traerEvaluaciones(contrato, true, false));
    }

    public void quitarDoctoRh(int idDocto) {
        rhConvenioDocumentosImpl.eliminarConvenioDocumento(sesion.getUsuarioSesion().getId(),
                idDocto, contratoVo.getId());
        //
        traerConvenioDoctosRh(contratoVo.getId());
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
            evaluacionesPorConvenio(c.getId());
            evaluacionesPendientesPorConvenio(c.getId());
            condicionesPorConvenio(index, c.getId());
            hitosPorConvenio(c.getId());
            traerArchivosConvenio();
            contratoVo.setProveedorVo(new ProveedorVo());
            traerDatosProveedor();
            //	traerContratos relacionados
            traerContratosRelacionados(c.getId());
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
        contratoVo.setListaConvenioDocumento(cvConvenioDocumentoImpl.traerDoctosPorConveni(contrato, null, null));
    }

    public void condicionesPorConvenio(int i, int cont) {
        contratoVo.setListaConvenioCondicion(cvConvenioCondicionPagoImpl.traerCondicionesPago(cont));
    }

    /*
     public DataModel getTraerConvenios() {
     return getListaConvenios();
     }
     */
    /////////////////////////////////////////////////////////////////////////
    public void iniciarFiltro() {
        setIdProveedor(Constantes.MENOS_UNO);

        getFiltroVo().setIdOperador(Constantes.MENOS_UNO);
        getFiltroVo().setIdMoneda(Constantes.MENOS_UNO);
        getFiltroVo().setImporte(Constantes.CERO);
        getFiltroVo().setFecha(null);
    }

    public void buscarContratoPorUsuario() {
        setIdProveedor(Constantes.MENOS_UNO);
        getFiltroVo().setIdOperador(Constantes.MENOS_UNO);
        getFiltroVo().setIdMoneda(Constantes.MENOS_UNO);
        getFiltroVo().setImporte(Constantes.CERO);
        getFiltroVo().setFecha(null);
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
        if (mesAnio != null) {
            String[] cad = getMesAnio().split("-");
            List<OrdenVO> lo = autorizacionesOrdenImpl.traerOrdenSolicidasPorMesAnio(Integer.parseInt(cad[0]), Integer.parseInt(cad[1]), OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId(), sesion.getUsuarioSesion().getIdCampo(), contratoVo.getNumero());
            contratoVo.setListaOrdenConvenio(lo);
        }

    }

    public void traerOcsPorconvenio() {
        ocsPorConvenio();
//	traerOCSConvenio(getIndice());
    }

    ////// //////////////////LISTA DE PRECIO    
    public void seleccionarCategoria(int idCat) {
        getCategoriaVo().setId(idCat);
        llenarCategoria(idCat);
        //
        //setListaCategoría(new ArrayList<>());
        listaCategoría = siCategoriaImpl.traerCategoriMenosPrincipalMenosSubcategorias(categoriaVo.getId());
        if (getCategoriaVo().getListaCategoria() == null
                || getCategoriaVo().getListaCategoria().isEmpty()) {
            verArticulos();
        }
        setArticulosResultadoBqda(obtenerArticulosItems("", sesion.getUsuarioSesion().getIdCampo(), idCat, ""));
    }

    public void verArticulos() {
        listaArticulos = articuloImpl.obtenerArticulos(null, sesion.getUsuarioSesion().getIdCampo(), Constantes.CERO,
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
        categoriasSeleccionadas = new ArrayList<>();
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
        CategoriaVo c = categoriasSeleccionadas.get(id);
        if (id == 0) {
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
            List<CategoriaVo> operatedList = new ArrayList<>();
            categoriasSeleccionadas.indexOf(c);

            categoriasSeleccionadas.stream().filter(ct -> ct.getId() > c.getId()).forEach(ca -> {
                operatedList.add(ca);
            });
            categoriasSeleccionadas.removeAll(operatedList);
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

    public void seleccionarResultadoBA(SelectEvent<ArticuloVO> event) {
        try {
            cambiarArticuloBda(event.getObject());
            articulosResultadoBqda.remove(event.getObject());
            //}
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void iniciaCargaArticulos() {
        iniciarCatSel();
        articulosResultadoBqda = new ArrayList<>();
        categoriaVo = new CategoriaVo();
        categoriaVo.setListaCategoria(siCategoriaImpl.traerCategoriaPrincipales());
        setSubirListaPrecio(Constantes.FALSE);
        PrimeFaces.current().executeScript("$(dialogoCargaArticulos).modal('show');");
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
            cav.setIdConvenio(contratoVo.getId());
            cav.setIdArticulo(articuloVO.getId());
            cav.setNombre(articuloVO.getNombre());
            cav.setUnidadNombre(articuloVO.getUnidadNombre());
            cav.setCantidad(articuloVO.getCantidad());
            cav.setPrecioUnitario(articuloVO.getPrecioUnitario());
            cav.setImporte(articuloVO.getImporte());
            contratoVo.getListaArticulo().add(cav);
            //
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void llenarDatosCambiarArticulo() {
        ConvenioArticuloVo cav = new ConvenioArticuloVo();
        cav.setIdArticulo(articuloVO.getId());
        contratoVo.getListaArticulo().add(cav);
    }

    public void cambiarArticuloBda(ArticuloVO articuloVO) {
        try {
            ConvenioArticuloVo cav = new ConvenioArticuloVo();
            cav.setIdConvenioArticulo(Constantes.CERO);
            cav.setIdConvenio(contratoVo.getId());
            cav.setIdArticulo(articuloVO.getId());
            cav.setItem("");
            cav.setCodigo(articuloVO.getCodigo());
            cav.setNombre(articuloVO.getNombre());
            cav.setUnidadNombre(articuloVO.getUnidadNombre());
            cav.setRegistrado(Constantes.TRUE);
            cav.setUnidadId(articuloVO.getUnidadId());
            cav.setCantidad(Constantes.CERO);
            cav.setPrecioUnitario(Constantes.CERO);
            contratoVo.getListaArticulo().add(cav);
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
        contratoVo.getListaArticulo().remove(convenioArticuloVo);
    }

    public void agregarListaArticulosContratoRelacionado() {
        contratoVo.getListaArticulo().addAll(convenioArticuloImpl.traerArticulosConvenioAnterior(contratoVo.getIdContratoRelacionado(), contratoVo.getId(), contratoVo.getIdCampo()));
    }

    public void quitarListaPrecio() {
        for (ConvenioArticuloVo convenioArticuloVo1 : contratoVo.getListaArticulo()) {
            if (convenioArticuloVo1.getIdConvenioArticulo() > Constantes.CERO) {
                convenioArticuloImpl.eliminar(convenioArticuloVo1, sesion.getUsuarioSesion().getId());
            }
        }
        contratoVo.setListaArticulo(new ArrayList<>());
    }

    public void modificarLista(ConvenioArticuloVo convArtVo) {
        this.convenioArticuloVo = convArtVo;
        convenioArticuloVo.setGuardado(Constantes.FALSE);
    }

    public void guardarListaPrecio() {
        boolean lleno = true;
        for (ConvenioArticuloVo cartvo : contratoVo.getListaArticulo()) {
            if (cartvo.getCantidad() == Constantes.CERO || cartvo.getPrecioUnitario() == Constantes.CERO
                    || cartvo.getUnidadId() == Constantes.CERO) {
                lleno = false;
                break;
            }
        }
        if (lleno) {
            convenioArticuloImpl.guardar(sesion.getUsuarioSesion().getId(), contratoVo.getListaArticulo(), contratoVo.getId(), contratoVo.getIdCampo());
            contratoVo.setListaArticulo(new ArrayList<>());
            contratoVo.setListaArticulo(convenioArticuloImpl.traerConvenioArticulo(contratoVo.getId(), contratoVo.getIdCampo()));
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
        contratoVo.setListaEvalsPendientes(cvEvaluacionImpl.traerEvaluaciones(contratoVo.getId(), true, false));
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
        contratoVo.getProveedorVo().setCuentas(cuentaBancoProveedorImpl.traerCuentas(contratoVo.getProveedor(), sesion.getRfcEmpresa()));
    }

    public void actualizarEvaluaciones(int i) {
        contratoVo.setListaConvenioEvals(cvConvenioEvaluacionImpl.traerEvaluacionTemplate(contratoVo.getId()));
    }

    public void actualizarRepLegal(int i) {
        contratoVo.getProveedorVo().setLstRL(contactoProveedorImpl.traerContactoPorProveedor(contratoVo.getProveedor(), Constantes.CONTACTO_REP_LEGAL));
    }

    public void actualizarRepTecnico(int i) {
        contratoVo.getProveedorVo().setLstRT(contactoProveedorImpl.traerContactoPorProveedor(contratoVo.getProveedor(), Constantes.CONTACTO_REP_TECNICO));
    }

    public void actualizarContactos(int i) {
        contratoVo.getProveedorVo().setContactos(contactoProveedorImpl.traerContactoPorProveedor(contratoVo.getProveedor(), Constantes.CONTACTO_REP_COMPRAS));
        //contratoVo.getProveedorVo().getContactos().addAll(contactoProveedorImpl.traerContactoPorProveedor(contratoVo.getProveedor(), 0));
    }

    public void actualizarDocumentos(int i) {
        contratoVo.getProveedorVo().setLstDocsProveedor(pvClasificacionArchivoImpl.traerArchivoPorProveedorOid(contratoVo.getProveedor(), 0));
    }
}
