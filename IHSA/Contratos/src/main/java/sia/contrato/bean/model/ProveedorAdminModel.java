/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.contrato.bean.soporte.FacesUtils;
import sia.ihsa.contratos.Sesion;
import sia.modelo.ContactoProveedor;
import sia.modelo.CuentaBancoProveedor;
import sia.modelo.Proveedor;
import sia.modelo.PvClasificacionArchivo;
import sia.modelo.PvRegistroFiscal;
import sia.modelo.documento.vo.DocumentoVO;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.proveedor.Vo.CuentaBancoVO;
import sia.modelo.proveedor.Vo.ProveedorDocumentoVO;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.proveedor.impl.ContactoProveedorImpl;
import sia.servicios.proveedor.impl.CuentaBancoProveedorImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.proveedor.impl.PvAreaServicioImpl;
import sia.servicios.proveedor.impl.PvClasificacionArchivoImpl;
import sia.servicios.proveedor.impl.PvDocumentoImpl;
import sia.servicios.proveedor.impl.PvRegistroFiscalImpl;
import sia.servicios.proveedor.impl.PvTipoPersonaImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiListaElementoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.vo.MonedaVO;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author ihsa
 */
@Named(value = "proveedorAdminBean")
@ViewScoped
public class ProveedorAdminModel implements Serializable {

    static final long serialVersionUID = 1;
    /**
     * Creates a new instance of ProveedorModel
     */
    @Inject
    private Sesion sesion;

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    private SiParametroImpl parametrosSistemaServicioRemoto;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private PvRegistroFiscalImpl pvRegistroFiscalImpl;
    @Inject
    private CuentaBancoProveedorImpl cuentaBancoProveedorImpl;
    @Inject
    private MonedaImpl monedaImpl;
    @Inject
    private ContactoProveedorImpl contactoProveedorImpl;
    @Inject
    private PvTipoPersonaImpl pvTipoPersonaImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SiListaElementoImpl siListaElementoImpl;
    @Inject
    private PvAreaServicioImpl pvAreaImpl;
    @Inject
    private PvDocumentoImpl pvDocumentoImpl;
    @Inject
    private PvClasificacionArchivoImpl pvClasificacionArchivoImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;

    private ProveedorVo proveedor;
    private Proveedor proveedorOrg;
    private PvRegistroFiscal registroOrg;
    private int idProveedor;
    private CuentaBancoVO cuentaVO;
    private ContactoProveedorVO rlVO;
    private ContactoProveedorVO rtVO;
    private ContactoProveedorVO coVO;
    private boolean nuevaCta;
    private boolean nuevoCto;
    private boolean nuevoDoc;
    private boolean editarProveedor;
    private List<DocumentoVO> lstDocsFaltantes;
    private ProveedorDocumentoVO docProveedor;
    private String dir = "";
    @Getter
    @Setter
    private UploadedFile fileInfo;
    @Getter
    @Setter
    private String proveedorSeleccionado;

    public ProveedorAdminModel() {
    }

    @PostConstruct
    public void iniciar() {
        proveedor = new ProveedorVo();
        proveedorSeleccionado = "";
    }

    public List<String> completarProveedor(String cadena) {
        List<String> proveedores = new ArrayList<>();
        List<ProveedorVo> pvrs = proveedorImpl.traerProveedorPorParteNombre(cadena, sesion.getUsuarioSesion().getId(), ProveedorEnum.ACTIVO.getId());
        pvrs.stream().forEach(p -> {
            proveedores.add(p.getNombre());
        });
        return proveedores;
    }

    public void buscarProveedor() {
        proveedor = proveedorImpl.traerProveedorPorNombre(proveedorSeleccionado);//getLstConveniosTabs().get(0).setProveedorVo(getProveedor());
        idProveedor = proveedor.getIdProveedor();
        proveedorSeleccionado = "";
        setEditarProveedor(true);
    }

    public void procesarProveedor() {
        if (!getProveedor().getLstDocsProveedor().isEmpty()) {
            boolean continuar = true;
            for (ProveedorDocumentoVO proveedorDocumentoVO : getProveedor().getLstDocsProveedor()) {
                if (proveedorDocumentoVO.getAdjuntoVO().getId() == 0) {
                    continuar = false;
                    break;
                }
            }
            if (continuar) {
                procesarProv();
                getProveedor().setStatus(ProveedorEnum.EN_PROCESO.getId());
                FacesUtils.addInfoMessage("Se envío el proveedor. ");
            } else {
                FacesUtils.addErrorMessage("Hace falta agregar la documentación física. ");
            }
        } else {
            FacesUtils.addErrorMessage("Hace falta agregar documentación. ");
        }

    }

    public void cargarDatosProveedor(FileUploadEvent fileEvent) throws NamingException {
        boolean valid = true;
        fileInfo = fileEvent.getFile();
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        if (fileInfo.getFileName().endsWith(".xlsx")) {
            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());
            try {
                if (addArchivo) {
                    try {
                        if (getProveedor().getIdProveedor() == Constantes.MENOS_UNO) {
                            FacesUtils.addErrorMessage("Proveedor vetado por Grupo Cobra ");
                            throw new Exception("Proveedor vetado por Grupo Cobra ");
                        }

                        //
                    } catch (Exception ex) {
                        UtilLog4j.log.fatal(this, "Ocurrio un error al guardar el archivo . . . . . " + ex.getMessage());
                        valid = false;
                    }
                } else {
                    FacesUtils.addErrorMessage(new StringBuilder()
                            .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                            .append(validadorNombreArchivo.getCaracteresNoValidos())
                            .toString());
                }

                PrimeFaces.current().executeScript(";$(registroProv).modal('hide');;");

            } catch (Exception e) {
                LOGGER.fatal(e);
                valid = false;
            }

            if (!valid) {
                FacesUtils.addErrorMessage("Ocurrió un error al subir el archivo del Pago. Porfavor contacte al Equipo del SIA: soportesia@ihsa.mx");
            }
        } else {
            FacesUtils.addErrorMessage("El archivo no es del tipo esperado (*.xlsx).");
        }
    }

    public void guardarProveedorDG() {
        try {
            saveProveedorDG();
            traerDatosProveedor();
            //PrimeFaces.current().ajax().update("frmAdmin:tabView:tabViewProv");
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoDGP);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void guardarProveedorDF() {
        try {
            boolean guardar = saveProveedorDF();
            traerDatosProveedor();
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoDF);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void guardarProveedorREG() {
        try {
            boolean guardar = saveProveedorREG();
            traerDatosProveedor();
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoREG);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void nuevaCuenta(int idProv) {
        try {
            if (idProv > 0 && (getIdProveedor() == 0 || (getIdProveedor() != idProv))) {
                setIdProveedor(idProv);
                this.traerDatosProveedor();
            }
            setNuevaCta(true);
            setCuentaVO(new CuentaBancoVO());
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoDB);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void editarCuenta(int idCuenta) {
        try {
            setNuevaCta(false);
            setCuentaVOEdit(idCuenta);
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoDB);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void borrarCuenta(int idCuenta) {
        try {
            setNuevaCta(false);
            setCuentaVOEdit(idCuenta);
            boolean guardar = eliminarCuenta();
            actualizarCuentas();

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

        public void guardarNuevaCuenta() {
        try {
            boolean guardar = saveNuevaCuenta();
            actualizarCuentas();
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoDB);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void nuevoRepLegal(int idProv) {
        try {
            if (idProv > 0 && (getIdProveedor() == 0 || (getIdProveedor() != idProv))) {
                setIdProveedor(idProv);
                this.traerDatosProveedor();
            }
            setRlVO(new ContactoProveedorVO());
            setNuevoCto(true);
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoRL);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void editContactoRL(int idContacto) {
        try {
            setRlVOID(idContacto);
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoRL);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void borrarContacto(int idContacto) {
        try {
            boolean guardar = eliminarContacto(idContacto);
            actualizarContactos();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void borrarContactoRL(int idContacto) {
        try {
            boolean guardar = eliminarContacto(idContacto);
            actualizarRepLegal();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void borrarContactoRT(int idContacto) {
        try {
            boolean guardar = eliminarContacto(idContacto);
            actualizarRepTecnico();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void editContactoRT(int idContacto) {
        try {
            setRtVOID(idContacto);
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoRT);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void editContacto(int idContacto) {
        try {
            setCoVOID(idContacto);
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoCO);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void guardarRepLegal() {
        try {

            boolean guardar = guardarNuevoRL();
            actualizarRepLegal();
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoRL);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void nuevoRepTecnico(int idProv) {
        try {
            if (idProv > 0 && (getIdProveedor() == 0 || (getIdProveedor() != idProv))) {
                setIdProveedor(idProv);
                this.traerDatosProveedor();
            }
            setRtVO(new ContactoProveedorVO());
            setNuevoCto(true);
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoRT);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void guardarTecnico() {
        try {
            boolean guardar = guardarNuevoRT();
            actualizarRepTecnico();
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoRT);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void nuevoContacto(int idProv) {
        try {
            if (idProv > 0 && (getIdProveedor() == 0 || (getIdProveedor() != idProv))) {
                setIdProveedor(idProv);
                this.traerDatosProveedor();
            }
            setCoVO(new ContactoProveedorVO());
            setNuevoCto(true);
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoCO);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void nuevoDocumento(int idProv) {
        try {
            if (idProv > 0 && (getIdProveedor() == 0 || (getIdProveedor() != idProv))) {
                setIdProveedor(idProv);
                this.traerDatosProveedor();
            }
            traerDocumentos();
            setDocProveedor(new ProveedorDocumentoVO());
            setNuevoDoc(true);
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoAgregarDoctoProv);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void guardarDocumento() {
        try {
            boolean guardar = guardarNuevoDOC();
            actualizarDocumentos();
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoCO);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void guardarContacto() {
        try {
            boolean guardar = guardarNuevoCO();
            actualizarContactos();
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoCO);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goDGP(int idProv) {
        try {
            if (idProv > 0 && (getIdProveedor() == 0 || (getIdProveedor() != idProv))) {
                setIdProveedor(idProv);
                this.traerDatosProveedor();
            }
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoDGP);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goDF(int idProv) {
        try {
            if (idProv > 0 && (getIdProveedor() == 0 || (getIdProveedor() != idProv))) {
                setIdProveedor(idProv);
                this.traerDatosProveedor();
            }
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoDF);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goREG(int idProv) {
        try {
            if (idProv > 0 && (getIdProveedor() == 0 || (getIdProveedor() != idProv))) {
                setIdProveedor(idProv);
                this.traerDatosProveedor();
            }
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoREG);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void traerDocumentos() {
        setLstDocsFaltantes(pvDocumentoImpl.traerDocFaltanteProveedor(getIdProveedor(), Constantes.DOCUMENTO_TIPO_PROVEEDOR));
    }

    private void traerDatosProveedor() {
        if (getIdProveedor() > 0) {
            this.traerDatosProveedor(getIdProveedor());
        }
    }

    public void agregarDocumentos(int idProv) {
        try {
            if (idProv > 0) {
                boolean guardo = addDocumentos(idProv);
                actualizarDocumentos();
            }
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoAgregarDoctoProv);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void agregarArchivoDocumento(int idDocProv) {
        try {
            this.setDocProveedor(new ProveedorDocumentoVO());
            this.getDocProveedor().setId(idDocProv);
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoAgregarArchivoDoctoProv);");
            this.setDocProveedor(this.buscarDoctosConvePorId());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void quitarArchivoDocumento(int idDocProv) {
        try {
            this.setDocProveedor(new ProveedorDocumentoVO());
            this.getDocProveedor().setId(idDocProv);
            traerArchivosProveedor();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void quitarSoloArchivoDocumento(int idDocProv, int idDocProvAdj) {
        try {
            this.setDocProveedor(new ProveedorDocumentoVO());
            this.getDocProveedor().setId(idDocProv);
            this.getDocProveedor().getAdjuntoVO().setId(idDocProvAdj);
            this.quitarArchivo();
            this.traerArchivosProveedor();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void agregarArchivoDocto(int idDocProv) {
        try {
            this.setDocProveedor(new ProveedorDocumentoVO());
            this.getDocProveedor().setId(idDocProv);
            this.setDocProveedor(this.buscarDoctosConvePorId());
            String metodo = "";
            metodo = ";abrirDialogoModal(adjuntarArchivo);";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public String getDirectorioDocumento() {
        String dir = "";
        try {
            dir = getDirectorio(getDirectorioDocumentoProveedor());
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return dir;
    }

    public String getDirectorioDocumentoProveedor() {
        String dir = "";
        try {
            dir = "CV/Proveedor/" + this.getProveedor().getRfc() + "/";
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return dir;
    }

    public void actualizarDocto() {
        try {
            this.actualizaDocto();
            FacesUtils.addInfoMessage("Se actualizaron los datos.");
            this.traerArchivosProveedor();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void subirArchivo(File file) {

        AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
        try {
            String extFile = FilenameUtils.getExtension(file.getName());
            String nombreFile = getDocProveedor().getDocumento() + '.' + extFile;
            if (this.getDocProveedor().isMultiArchivo()) {
                nombreFile = getDocProveedor().getDocumento()
                        + traerIndiceMultiArchivosProveedor(getDocProveedor().getIdDocumento())
                        + '.'
                        + extFile;
            }

            LOGGER.info("Nombre de archivo:" + nombreFile);

            DocumentoAnexo documentoAnexo = new DocumentoAnexo(file);
            documentoAnexo.setNombreBase(nombreFile);
            documentoAnexo.setRuta(getDirectorioDocumentoProveedor());
            almacenDocumentos.guardarDocumento(documentoAnexo);

            AdjuntoVO adjunto = new AdjuntoVO();
            adjunto.setUrl(documentoAnexo.getRuta() + documentoAnexo.getNombreBase());
            adjunto.setNombre(documentoAnexo.getNombreBase());
            adjunto.setTipoArchivo(documentoAnexo.getTipoMime());
            adjunto.setTamanio(documentoAnexo.getTamanio());

            getDocProveedor().setAdjuntoVO(adjunto);

            agregarAdjuntoProveedor();
            traerArchivosProveedor();
            FacesUtils.addInfoMessage("Se agrego el documento");
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Ocurrio un error " + e.getMessage());
            LOGGER.fatal(this, "+ + + ERROR + + +" + e.getMessage(), e);
        }
    }

    public void traerDatosProveedor(int idProveedor) {
        setProveedor(proveedorImpl.traerProveedor(idProveedor, sesion.getRfcEmpresa()));
        //traerDocumentos();
    }

    public void traerProveedorOrg(int idProveedor) {
        setProveedorOrg(proveedorImpl.find(idProveedor));
    }

    public void traerRegistroFiscalOrg() {
        setRegistroOrg(pvRegistroFiscalImpl.find(getProveedor().getPvRegistroFiscal()));
    }

    public String traerJson() {
        String jsonProveedores = proveedorImpl.traerProveedorPorCompaniaSesionJson("'" + sesion.getRfcEmpresa() + "'", ProveedorEnum.ACTIVO.getId());
        return jsonProveedores;
    }

    public Proveedor buscarProveedorPorNombre(String proveedor) {
        try {
            return proveedorImpl.getPorNombre(proveedor, sesion.getUsuarioSesion().getRfcEmpresa());
        } catch (Exception e) {
            return null;
        }
    }

    public void procesarProv() {
        proveedor.setIdPago(Constantes.ID_60_DIAS);
        proveedor.setTipoProveedor(Constantes.CERO);
        proveedorImpl.procesarProveedor(sesion.getUsuarioSesion(), proveedor);
        proveedor = (proveedorImpl.traerProveedor(idProveedor, sesion.getRfcEmpresa()));
    }

    public boolean saveProveedorDG() {
        boolean guardar = false;
        try {
            traerProveedorOrg(idProveedor);

            if (this.proveedor.getNombre() != null
                    && !this.proveedor.getNombre().isEmpty()
                    && (!this.proveedor.getNombre().equals(getProveedorOrg().getNombre()))) {
                guardar = true;
                getProveedorOrg().setNombre(getProveedor().getNombre());
            }
            if (this.proveedor.getGiro() != null
                    && !this.proveedor.getGiro().isEmpty()
                    && (!this.proveedor.getGiro().equals(getProveedorOrg().getGiro()))) {
                guardar = true;
                getProveedorOrg().setGiro(getProveedor().getGiro());
            }
            if (this.proveedor.isNacional()) {
                guardar = true;
                getProveedorOrg().setNacional(getProveedor().isNacional());
            }
            if (getProveedorOrg().getPvTipoPersona() == null || (this.proveedor.getPersona() > 0
                    && (this.proveedor.getPersona() != getProveedorOrg().getPvTipoPersona().getId()))) {
                guardar = true;
                getProveedorOrg().setPvTipoPersona(pvTipoPersonaImpl.find(getProveedor().getPersona()));
            }
            if (!this.proveedor.getRfc().equals(getProveedorOrg().getRfc())) {
                guardar = true;
                getProveedorOrg().setRfc(getProveedor().getRfc());
            }
            if (!this.proveedor.getCurp().equals(getProveedorOrg().getCurp())) {
                guardar = true;
                getProveedorOrg().setCurp(getProveedor().getCurp());
            }
            if (!this.proveedor.getIdCIF().equals(getProveedorOrg().getIdCif())) {
                guardar = true;
                getProveedorOrg().setIdCif(getProveedor().getIdCIF());
            }

            if (!this.proveedor.isNacional()) {
                guardar = true;
                getProveedorOrg().setIdCif(null);
                getProveedorOrg().setCurp(null);
                getProveedorOrg().setPvTipoPersona(null);
            } else if (2 > getProveedorOrg().getPvTipoPersona().getId()) {
                getProveedorOrg().setCurp(null);
            }

            if (guardar) {
                proveedorOrg.setModifico(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
                proveedorOrg.setFechaModifico(new Date());
                proveedorOrg.setHoraModifico(new Date());
                proveedorImpl.edit(proveedorOrg);
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
        }
        return guardar;
    }

    public boolean saveProveedorDF() {
        boolean guardar = false;
        try {
            traerProveedorOrg(idProveedor);

            if (!this.proveedor.getCalle().equals(getProveedorOrg().getCalle())) {
                guardar = true;
                getProveedorOrg().setCalle(getProveedor().getCalle());
            }
            if (!this.proveedor.getColonia().equals(getProveedorOrg().getColonia())) {
                guardar = true;
                getProveedorOrg().setColonia(getProveedor().getColonia());
            }
            if (!this.proveedor.getCiudad().equals(getProveedorOrg().getCiudad())) {
                guardar = true;
                getProveedorOrg().setCiudad(getProveedor().getCiudad());
            }
            if (!this.proveedor.getEstado().equals(getProveedorOrg().getEstado())) {
                guardar = true;
                getProveedorOrg().setEstado(getProveedor().getEstado());
            }
            if (!this.proveedor.getPais().equals(getProveedorOrg().getPais())) {
                guardar = true;
                getProveedorOrg().setPais(getProveedor().getPais());
            }
            if (!this.proveedor.getNumero().equals(getProveedorOrg().getNumero())) {
                guardar = true;
                getProveedorOrg().setNumero(getProveedor().getNumero());
            }
            if (!this.proveedor.getCodigoPostal().equals(getProveedorOrg().getCodigoPostal())) {
                guardar = true;
                getProveedorOrg().setCodigoPostal(getProveedor().getCodigoPostal());
            }
            if (!this.proveedor.getNumeroInt().equals(getProveedorOrg().getNumeroInterior())) {
                guardar = true;
                getProveedorOrg().setNumeroInterior(getProveedor().getNumeroInt());
            }

            if (guardar) {
                proveedorOrg.setModifico(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
                proveedorOrg.setFechaModifico(new Date());
                proveedorOrg.setHoraModifico(new Date());
                proveedorImpl.edit(proveedorOrg);
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
        }
        return guardar;
    }

    public boolean saveProveedorREG() {
        boolean guardar = false;
        boolean guardarReg = false;
        try {
            traerProveedorOrg(idProveedor);
            traerRegistroFiscalOrg();
            boolean crearReg = false;

            if (getRegistroOrg() == null) {
                setRegistroOrg(new PvRegistroFiscal());
                crearReg = true;
            }

            if (!this.proveedor.getImssPatronal().equals(getProveedorOrg().getImsspatronal())) {
                guardar = true;
                getProveedorOrg().setImsspatronal(getProveedor().getImssPatronal());
            }
            if (!this.proveedor.getNoNotaria().equals(getRegistroOrg().getNoNotaria())) {
                guardarReg = true;
                getRegistroOrg().setNoNotaria(getProveedor().getNoNotaria());
            }
            if (!this.proveedor.getNoBoleta().equals(getRegistroOrg().getNoBoleta())) {
                guardarReg = true;
                getRegistroOrg().setNoBoleta(getProveedor().getNoBoleta());
            }
            if (!this.proveedor.getSede().equals(getRegistroOrg().getSede())) {
                guardarReg = true;
                getRegistroOrg().setSede(getProveedor().getSede());
            }
            if (!this.proveedor.getNoActa().equals(getRegistroOrg().getNoActa())) {
                guardarReg = true;
                getRegistroOrg().setNoActa(getProveedor().getNoActa());
            }
            if (!this.proveedor.getNombreNot().equals(getRegistroOrg().getNombreNot())) {
                guardarReg = true;
                getRegistroOrg().setNombreNot(getProveedor().getNombreNot());
            }
            if (!this.proveedor.getEmision().equals(getRegistroOrg().getEmision())) {
                guardarReg = true;
                getRegistroOrg().setEmision(getProveedor().getEmision());
            }
            if (!this.proveedor.getInscripcion().equals(getRegistroOrg().getInscripcion())) {
                guardarReg = true;
                getRegistroOrg().setInscripcion(getProveedor().getInscripcion());
            }

            if (guardar) {
                proveedorOrg.setModifico(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
                proveedorOrg.setFechaModifico(new Date());
                proveedorOrg.setHoraModifico(new Date());
                proveedorImpl.edit(proveedorOrg);
            }

            if (guardarReg) {
                if (crearReg) {
                    registroOrg.setProveedor(proveedorOrg);
                    registroOrg.setEliminado(Constantes.BOOLEAN_FALSE);
                    registroOrg.setGenero(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
                    registroOrg.setFechaGenero(new Date());
                    registroOrg.setHoraGenero(new Date());
                    pvRegistroFiscalImpl.create(registroOrg);
                } else {
                    registroOrg.setModifico(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
                    registroOrg.setFechaModifico(new Date());
                    registroOrg.setHoraModifico(new Date());
                    pvRegistroFiscalImpl.edit(registroOrg);
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
            guardarReg = false;
        }
        return (guardar || guardarReg);
    }

    /**
     * @return the proveedorOrg
     */
    public Proveedor getProveedorOrg() {
        return proveedorOrg;
    }

    /**
     * @param proveedorOrg the proveedorOrg to set
     */
    public void setProveedorOrg(Proveedor proveedorOrg) {
        this.proveedorOrg = proveedorOrg;
    }

    /**
     * @return the registroOrg
     */
    public PvRegistroFiscal getRegistroOrg() {
        return registroOrg;
    }

    /**
     * @param registroOrg the registroOrg to set
     */
    public void setRegistroOrg(PvRegistroFiscal registroOrg) {
        this.registroOrg = registroOrg;
    }

    /**
     * @return the cuentaVO
     */
    public CuentaBancoVO getCuentaVO() {
        return cuentaVO;
    }

    /**
     * @param cuentaVO the cuentaVO to set
     */
    public void setCuentaVO(CuentaBancoVO cuentaVO) {
        this.cuentaVO = cuentaVO;
    }

    public void setCuentaVOEdit(int idCuenta) {
        this.setCuentaVO(cuentaBancoProveedorImpl.traerCuenta(idCuenta));
    }

    public boolean saveNuevaCuenta() {
        boolean guardar = false;
        try {
            this.traerProveedorOrg(idProveedor);
            CuentaBancoProveedor nueva = null;
            if (this.isNuevaCta()) {
                guardar = true;
                nueva = new CuentaBancoProveedor();
                nueva.setBanco(this.getCuentaVO().getBanco());
                nueva.setCuenta(this.getCuentaVO().getCuenta());
                nueva.setMoneda(monedaImpl.buscarPorNombre(this.getCuentaVO().getMoneda(), sesion.getRfcEmpresa()));
                nueva.setClabe(this.getCuentaVO().getClabe());
                nueva.setSwift(this.getCuentaVO().getSwift());
                nueva.setAba(this.getCuentaVO().getAba());
                nueva.setProveedor(this.getProveedorOrg());

                nueva.setEliminado(Constantes.BOOLEAN_FALSE);
                nueva.setGenero(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
                nueva.setFechaGenero(new Date());
                nueva.setHoraGenero(new Date());
                cuentaBancoProveedorImpl.create(nueva);
            } else {
                nueva = cuentaBancoProveedorImpl.find(this.getCuentaVO().getIdCuentaBanco());
                if (!nueva.getBanco().equals(this.getCuentaVO().getBanco())) {
                    nueva.setBanco(this.getCuentaVO().getBanco());
                    guardar = true;
                }

                if (!nueva.getCuenta().equals(this.getCuentaVO().getCuenta())) {
                    nueva.setCuenta(this.getCuentaVO().getCuenta());
                    guardar = true;
                }

                if (!nueva.getMoneda().getNombre().equals(this.getCuentaVO().getMoneda())) {
                    nueva.setMoneda(monedaImpl.buscarPorNombre(this.getCuentaVO().getMoneda(), sesion.getRfcEmpresa()));
                    guardar = true;
                }

                if (!nueva.getClabe().equals(this.getCuentaVO().getClabe())) {
                    nueva.setClabe(this.getCuentaVO().getClabe());
                    guardar = true;
                }

                if (!nueva.getSwift().equals(this.getCuentaVO().getSwift())) {
                    nueva.setSwift(this.getCuentaVO().getSwift());
                    guardar = true;
                }

                if (!nueva.getAba().equals(this.getCuentaVO().getAba())) {
                    nueva.setAba(this.getCuentaVO().getAba());
                    guardar = true;
                }

                if (!nueva.getProveedor().equals(this.getProveedorOrg())) {
                    nueva.setProveedor(this.getProveedorOrg());
                    guardar = true;
                }

                if (guardar) {
                    nueva.setModifico(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
                    nueva.setFechaModifico(new Date());
                    nueva.setHoraModifico(new Date());
                    cuentaBancoProveedorImpl.edit(nueva);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
        }
        return guardar;
    }

    public boolean eliminarCuenta() {
        boolean guardar = false;
        try {
            CuentaBancoProveedor nueva = cuentaBancoProveedorImpl.find(this.getCuentaVO().getIdCuentaBanco());
            if (nueva != null && nueva.getId() > 0) {
                nueva.setModifico(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
                nueva.setFechaModifico(new Date());
                nueva.setHoraModifico(new Date());
                nueva.setEliminado(Constantes.BOOLEAN_TRUE);
                cuentaBancoProveedorImpl.edit(nueva);
                guardar = true;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
        }
        return guardar;
    }

    public void actualizarCuentas() {
        getProveedor().setCuentas(cuentaBancoProveedorImpl.traerCuentas(getProveedor().getIdProveedor(), sesion.getRfcEmpresa()));
    }

    public void actualizarRepLegal() {
        getProveedor().setLstRL(contactoProveedorImpl.traerContactoPorProveedor(getProveedor().getIdProveedor(), Constantes.CONTACTO_REP_LEGAL));
    }

    public void actualizarRepTecnico() {
        getProveedor().setLstRT(contactoProveedorImpl.traerContactoPorProveedor(getProveedor().getIdProveedor(), Constantes.CONTACTO_REP_TECNICO));
    }

    public void actualizarContactos() {
        getProveedor().setContactos(contactoProveedorImpl.traerContactoPorProveedor(getProveedor().getIdProveedor(), Constantes.CONTACTO_REP_COMPRAS));
        //getProveedor().getContactos().addAll(contactoProveedorImpl.traerContactoPorProveedor(getProveedor().getIdProveedor(), 0));
    }

    public void actualizarDocumentos() {
        getProveedor().setLstDocsProveedor(pvClasificacionArchivoImpl.traerArchivoPorProveedorOid(getProveedor().getIdProveedor(), 0));
    }

    public List<SelectItem> getListaMonedas() {
        List resultList = new ArrayList<SelectItem>();
        try {
            List<MonedaVO> tempList = monedaImpl.traerMonedasPorCompania(sesion.getRfcEmpresa(), 0);
            for (MonedaVO Lista : tempList) {
                SelectItem item = new SelectItem(Lista.getNombre());
                resultList.add(item);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return resultList;
    }

    public boolean contenidoHTML(String rfc, String idCif) throws IOException {
        HttpURLConnection connection = null;
        boolean encontrado = false;

        URL url = new URL("https://siat.sat.gob.mx/app/qr/faces/pages/mobile/validadorqr.jsf?D1=10&D2=1&D3=" + idCif + "_" + rfc);
        connection = (HttpURLConnection) url.openConnection();

//        URLConnection uc = url.openConnection();
        connection.connect();
//        boolean encontrado = false;
//        //Creamos el objeto con el que vamos a leer
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            //contenido += inputLine + "\n";
            if (inputLine.contains("tiene asociada la siguiente información")) {
                encontrado = true;
                break;
            }
        }
        in.close();
        return encontrado;
    }

    /**
     * @return the rlVO
     */
    public ContactoProveedorVO getRlVO() {
        return rlVO;
    }

    /**
     * @param rlVO the rlVO to set
     */
    public void setRlVO(ContactoProveedorVO rlVO) {
        this.rlVO = rlVO;
    }

    public void setRlVOID(int idContacto) {
        setRlVO(contactoProveedorImpl.traerContacto(idContacto));
        setNuevoCto(false);
    }

    public void setRtVOID(int idContacto) {
        setRtVO(contactoProveedorImpl.traerContacto(idContacto));
        setNuevoCto(false);
    }

    public void setCoVOID(int idContacto) {
        setCoVO(contactoProveedorImpl.traerContacto(idContacto));
        setNuevoCto(false);
    }

    /**
     * @return the rtVO
     */
    public ContactoProveedorVO getRtVO() {
        return rtVO;
    }

    /**
     * @param rtVO the rtVO to set
     */
    public void setRtVO(ContactoProveedorVO rtVO) {
        this.rtVO = rtVO;
    }

    /**
     * @return the coVO
     */
    public ContactoProveedorVO getCoVO() {
        return coVO;
    }

    /**
     * @param coVO the coVO to set
     */
    public void setCoVO(ContactoProveedorVO coVO) {
        this.coVO = coVO;
    }

    private boolean guardarContactoNuevo(ContactoProveedorVO vo, int tipoContacto) {
        boolean guardar = false;
        ContactoProveedor contacto = null;
        try {
            this.traerProveedorOrg(idProveedor);
            contacto = new ContactoProveedor();
            contacto.setEliminado(Constantes.BOOLEAN_FALSE);
            contacto.setGenero(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
            contacto.setFechaGenero(new Date());
            contacto.setHoraGenero(new Date());
            contacto.setProveedor(getProveedorOrg());

            contacto.setPvArea(pvAreaImpl.find(tipoContacto));
            contacto.setCelular(vo.getCelular());

            if (vo.getCorreo() != null && !vo.getCorreo().isEmpty()) {
                String newCorreo = vo.getCorreo().trim();
                int ascii = newCorreo.codePointAt(newCorreo.length() - 1);
                if (ascii < 65 || (ascii > 90 && ascii < 97) || ascii > 122) {
                    vo.setCorreo(newCorreo.substring(Constantes.CERO, newCorreo.length() - 1));
                }
            }
            contacto.setCorreo(vo.getCorreo());
            contacto.setCurp(vo.getCurp());
            contacto.setEmision(vo.getEmision());
            contacto.setIdVigencia(vo.getIdVigencia());
            contacto.setNombre(vo.getNombre());
            contacto.setNotario(vo.getNombreNotario());
            contacto.setNoNotaria(vo.getNotaria());
            contacto.setPoderNotarial(vo.getPoder());
            contacto.setPuesto(vo.getPuesto());
            contacto.setReferencia(vo.getReferencia());
            contacto.setRfc(vo.getRfc());
            contacto.setTelefono(vo.getTelefono());
            contacto.setSiListaElemento(siListaElementoImpl.find(vo.getTipoID()));
            contactoProveedorImpl.create(contacto);
            guardar = true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
        }
        return guardar;
    }

    private boolean guardarDocumentoNuevo(ProveedorDocumentoVO vo, int tipoContacto) {
        boolean guardar = false;
        PvClasificacionArchivo doc = null;
        try {
            this.traerProveedorOrg(idProveedor);
            doc = new PvClasificacionArchivo();
            doc.setEliminado(Constantes.BOOLEAN_FALSE);
            doc.setGenero(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
            doc.setFechaGenero(new Date());
            doc.setHoraGenero(new Date());
            doc.setProveedor(getProveedorOrg());
            pvClasificacionArchivoImpl.create(doc);
            guardar = true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
        }
        return guardar;
    }

    private boolean editarContacto(ContactoProveedorVO vo, int tipoContacto) {
        boolean guardar = false;
        try {
            ContactoProveedor contacto = null;
            this.traerProveedorOrg(idProveedor);
            contacto = contactoProveedorImpl.find(vo.getIdContactoProveedor());

            if (contacto.getPvArea().getId() != tipoContacto) {
                contacto.setPvArea(pvAreaImpl.find(tipoContacto));
                guardar = true;
            }

            if (contacto.getCelular() == null || !contacto.getCelular().equals(vo.getCelular())) {
                contacto.setCelular(vo.getCelular());
                guardar = true;
            }

            if (contacto.getCorreo() == null || !contacto.getCorreo().equals(vo.getCorreo())) {
                String newCorreo = vo.getCorreo().trim();
                int ascii = newCorreo.codePointAt(newCorreo.length() - 1);
                if (ascii < 65 || (ascii > 90 && ascii < 97) || ascii > 122) {
                    vo.setCorreo(newCorreo.substring(Constantes.CERO, newCorreo.length() - 1));
                }
                contacto.setCorreo(vo.getCorreo());
                guardar = true;
            }

            if (contacto.getCurp() == null || !contacto.getCurp().equals(vo.getCurp())) {
                contacto.setCurp(vo.getCurp());
                guardar = true;
            }

            if (Constantes.CONTACTO_REP_LEGAL == tipoContacto && (contacto.getEmision() == null || !contacto.getEmision().equals(vo.getEmision()))) {
                contacto.setEmision(vo.getEmision());
                guardar = true;
            }

            if (contacto.getIdVigencia() == null || !contacto.getIdVigencia().equals(vo.getIdVigencia())) {
                contacto.setIdVigencia(vo.getIdVigencia());
                guardar = true;
            }

            if (contacto.getNombre() == null || !contacto.getNombre().equals(vo.getNombre())) {
                contacto.setNombre(vo.getNombre());
                guardar = true;
            }

            if (contacto.getNotario() == null || !contacto.getNotario().equals(vo.getNombreNotario())) {
                contacto.setNotario(vo.getNombreNotario());
                guardar = true;
            }

            if (contacto.getNoNotaria() == null || !contacto.getNoNotaria().equals(vo.getNotaria())) {
                contacto.setNoNotaria(vo.getNotaria());
                guardar = true;
            }

            if (contacto.getPoderNotarial() == null || !contacto.getPoderNotarial().equals(vo.getPoder())) {
                contacto.setPoderNotarial(vo.getPoder());
                guardar = true;
            }

            if (contacto.getPuesto() == null || !contacto.getPuesto().equals(vo.getPuesto())) {
                contacto.setPuesto(vo.getPuesto());
                guardar = true;
            }

            if (contacto.getReferencia() == null || !contacto.getReferencia().equals(vo.getReferencia())) {
                contacto.setReferencia(vo.getReferencia());
                guardar = true;
            }

            if (contacto.getRfc() == null || !contacto.getRfc().equals(vo.getRfc())) {
                contacto.setRfc(vo.getRfc());
                guardar = true;
            }

            if (contacto.getTelefono() == null || !contacto.getTelefono().equals(vo.getTelefono())) {
                contacto.setTelefono(vo.getTelefono());
                guardar = true;
            }

            if (contacto.getSiListaElemento() == null || contacto.getSiListaElemento().getId() != vo.getTipoID()) {
                contacto.setSiListaElemento(siListaElementoImpl.find(vo.getTipoID()));
                guardar = true;
            }

            if (guardar) {
                contacto.setModifico(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
                contacto.setFechaModifico(new Date());
                contacto.setHoraModifico(new Date());
                contactoProveedorImpl.edit(contacto);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
        }
        return guardar;
    }

    private boolean eliminarContacto(int contactoID) {
        boolean guardar = false;
        try {
            ContactoProveedor contacto = contactoProveedorImpl.find(contactoID);
            if (contacto != null && contacto.getId() > 0) {
                contacto.setModifico(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
                contacto.setFechaModifico(new Date());
                contacto.setHoraModifico(new Date());
                contacto.setEliminado(Constantes.BOOLEAN_TRUE);
                contactoProveedorImpl.edit(contacto);
                guardar = true;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
        }
        return guardar;
    }

    public boolean guardarNuevoRL() {
        boolean guardar = false;
        try {
            this.traerProveedorOrg(idProveedor);
            if (this.isNuevoCto()) {
                guardar = guardarContactoNuevo(this.getRlVO(), Constantes.CONTACTO_REP_LEGAL);
            } else {
                guardar = editarContacto(this.getRlVO(), Constantes.CONTACTO_REP_LEGAL);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
        }
        return guardar;
    }

    public boolean guardarNuevoRT() {
        boolean guardar = false;
        try {
            this.traerProveedorOrg(idProveedor);
            if (this.isNuevoCto()) {
                guardar = guardarContactoNuevo(this.getRtVO(), Constantes.CONTACTO_REP_TECNICO);
            } else {
                guardar = editarContacto(this.getRtVO(), Constantes.CONTACTO_REP_TECNICO);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
        }
        return guardar;
    }

    public boolean guardarNuevoCO() {
        boolean guardar = false;
        try {
            this.traerProveedorOrg(idProveedor);
            if (this.isNuevoCto()) {
                guardar = guardarContactoNuevo(this.getCoVO(), Constantes.CONTACTO_REP_COMPRAS);
            } else {
                guardar = editarContacto(this.getCoVO(), Constantes.CONTACTO_REP_COMPRAS);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
        }
        return guardar;
    }

    public boolean guardarNuevoDOC() {
        boolean guardar = false;
        try {
            this.traerProveedorOrg(idProveedor);
            if (this.isNuevoDoc()) {
                guardar = guardarDocumentoNuevo(this.getDocProveedor(), Constantes.CONTACTO_REP_COMPRAS);
            } else {
                guardar = editarContacto(this.getCoVO(), Constantes.CONTACTO_REP_COMPRAS);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
        }
        return guardar;
    }

    public void cargarDatosProveedor(File file) {
        try {
            idProveedor = proveedorImpl.cargarDatosProveedor(sesion.getUsuarioSesion().getId(), file, sesion.getRfcEmpresa());
            if (idProveedor > 0) {
                traerDatosProveedor(idProveedor);
            } else {
                ProveedorVo p = new ProveedorVo();
                p.setIdProveedor(idProveedor);
                setProveedor(p);

            }

        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    /**
     * @return the nuevaCta
     */
    public boolean isNuevaCta() {
        return nuevaCta;
    }

    /**
     * @param nuevaCta the nuevaCta to set
     */
    public void setNuevaCta(boolean nuevaCta) {
        this.nuevaCta = nuevaCta;
    }

    /**
     * @return the nuevoCto
     */
    public boolean isNuevoCto() {
        return nuevoCto;
    }

    /**
     * @param nuevoCto the nuevoCto to set
     */
    public void setNuevoCto(boolean nuevoCto) {
        this.nuevoCto = nuevoCto;
    }

    /**
     * @return the docProveedor
     */
    public ProveedorDocumentoVO getDocProveedor() {
        return docProveedor;
    }

    /**
     * @param docProveedor the docProveedor to set
     */
    public void setDocProveedor(ProveedorDocumentoVO docProveedor) {
        this.docProveedor = docProveedor;
    }

    /**
     * @return the lstDocsFaltantes
     */
    public List<DocumentoVO> getLstDocsFaltantes() {
        return lstDocsFaltantes;
    }

    /**
     * @param lstDocsFaltantes the lstDocsFaltantes to set
     */
    public void setLstDocsFaltantes(List<DocumentoVO> lstDocsFaltantes) {
        this.lstDocsFaltantes = lstDocsFaltantes;
    }

    /**
     * @return the nuevoDoc
     */
    public boolean isNuevoDoc() {
        return nuevoDoc;
    }

    /**
     * @param nuevoDoc the nuevoDoc to set
     */
    public void setNuevoDoc(boolean nuevoDoc) {
        this.nuevoDoc = nuevoDoc;
    }

    private boolean addDocumentos(int provId) {
        boolean guardo = false;
        try {
            List<DocumentoVO> ltemp = new ArrayList<>();
            for (DocumentoVO voSelected : getLstDocsFaltantes()) {
                if (voSelected.isSelected()) {
                    ltemp.add(voSelected);
                }
            }
            guardo = pvClasificacionArchivoImpl.guardar(sesion.getUsuarioSesion().getId(), ltemp, provId);
        } catch (Exception e) {
            guardo = false;
            UtilLog4j.log.fatal(e);
        }
        return guardo;
    }

    public void quitarArchivo() {
        try {
            siAdjuntoImpl.eliminarArchivo(getDocProveedor().getAdjuntoVO().getId(), sesion.getUsuarioSesion().getId());
            pvClasificacionArchivoImpl.quitarArchivoDocumento(sesion.getUsuarioSesion().getId(), getDocProveedor().getId());
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    public void actualizaDocto() {
        try {
            pvClasificacionArchivoImpl.agregarArchivo(sesion.getUsuarioSesion().getId(), getDocProveedor(), getDocProveedor().getAdjuntoVO().getId());
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    public ProveedorDocumentoVO buscarDoctosConvePorId() {
        ProveedorDocumentoVO docProv = null;
        try {
            List<ProveedorDocumentoVO> lstDocProv = pvClasificacionArchivoImpl.traerArchivoPorProveedorOid(0, getDocProveedor().getId());
            if (lstDocProv != null && lstDocProv.size() > 0) {
                docProv = lstDocProv.get(0);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return docProv;
    }

    public void eliminarArchivoDocumento() {
        try {
            pvClasificacionArchivoImpl.eliminar(sesion.getUsuarioSesion().getId(), getDocProveedor().getId());
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    /**
     * @return the dir
     */
    public String getDir() {
        return dir;
    }

    /**
     * @param dir the dir to set
     */
    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getDirectorio(String codigo) {
        if (this.getDir().isEmpty()) {
            this.setDir(this.parametrosSistemaServicioRemoto.find(1).getUploadDirectory());
        }
        return this.getDir() + codigo;
    }

    public void agregarAdjuntoProveedor() throws Exception {
        getDocProveedor().getAdjuntoVO().setId(siAdjuntoImpl.saveSiAdjunto(getDocProveedor().getAdjuntoVO().getNombre(),
                getDocProveedor().getAdjuntoVO().getTipoArchivo(), getDocProveedor().getAdjuntoVO().getUrl(),
                getDocProveedor().getAdjuntoVO().getTamanio(),
                sesion.getUsuarioSesion().getId()));
        pvClasificacionArchivoImpl.guardar(sesion.getUsuarioSesion().getId(), this.getDocProveedor().getId(), getDocProveedor().getAdjuntoVO().getId());
    }

    public void traerArchivosProveedor() {
        getProveedor().setLstDocsProveedor(pvClasificacionArchivoImpl.traerArchivoPorProveedorOid(getProveedor().getIdProveedor(), 0));
    }

    public int traerIndiceMultiArchivosProveedor(int tipoArchivo) {
        int indice = pvClasificacionArchivoImpl.traerArchivoPorProveedorYDoc(getProveedor().getIdProveedor(), tipoArchivo, true).size();
        return indice + 1;
    }

    /**
     * @return the editarProveedor
     */
    public boolean isEditarProveedor() {
        return editarProveedor;
    }

    /**
     * @param editarProveedor the editarProveedor to set
     */
    public void setEditarProveedor(boolean editarProveedor) {
        this.editarProveedor = editarProveedor;
    }

    /**
     * @return the idProveedor
     */
    public int getIdProveedor() {
        return idProveedor;
    }

    /**
     * @param idProveedor the idProveedor to set
     */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    /**
     * @return the proveedor
     */
    public ProveedorVo getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(ProveedorVo proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

}
