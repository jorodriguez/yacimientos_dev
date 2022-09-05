package sia.controloficios.backing.bean;


import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.event.FileUploadEvent;
/*import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;*/
import org.primefaces.event.FilesUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;

import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.controloficios.sistema.bean.backing.CatalogosBean;
import sia.controloficios.sistema.bean.backing.Sesion;
import sia.controloficios.sistema.soporte.PrimeUtils;
import sia.excepciones.InsufficientPermissionsException;
import sia.excepciones.SIAException;
import sia.modelo.ApCampo;
import sia.modelo.Compania;
import sia.modelo.Gerencia;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CompaniaBloqueGerenciaVo;
import sia.modelo.oficio.vo.*;
import sia.modelo.rol.vo.RolVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.oficio.impl.OfOficioImpl;
import sia.servicios.oficio.impl.OfOficioConsultaImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiPermisoImpl;
import sia.util.UtilLog4j;
import sia.util.UtilSia;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * Provee un conjunto de recursos y servicios básicos comunes para todos los
 * ManagedBean del módulo de Control de Oficios con el fin de garantizar
 * uniformidad y reutilización.
 *
 * <p>
 * Las clases derivadas deberán contener la anotación @ManagedBean para poder
 * ser accedidos desde los elementos de vista (*.xhtml).
 * </p>
 *
 * <p>
 * Caracteristicas principales y servicios recibidos por las clases derivadas:
 * </p>
 *
 * <ul>
 *
 * <li>Todos los managed beans del módulo de Control de Oficios son</li>
 *
 * @ViewScoped, con un ciclo de vida equivalente a la visualización de cada
 * página.
 * <li>Implementación por defecto de métodos @PostConstruct y @PreDestroy para
 * operaciones de inicialización y limpieza comunes, así como métodos
 * relacionados a sobreescribir para las necesidades particulares de las clases
 * derivadas.</li>
 * <li>Servicio de bitacoreo (logger).</li>
 * <li>Bean de value object OficioVo como contenededor para búsquedas y
 * edición.</li>
 * <li>Lista de oficios y métodos de acceso como contenedor de resultados y
 * despliegue de los mismos en pantalla.</li>
 * <li>Gestor de operaciones de I/O configurado a las rutas válidas en disco.</li>
 * <li>Obtención y acceso a la sesión y permisos actuales.</li>
 * <li>Obtención y acceso a los servicios del Control de Oficios.</li>
 *
 * </ul>
 *
 * @author esapien
 */
@ViewScoped
public abstract class OficioBaseBean implements Serializable {

    /**
     * Bitacoreo
     */
    //private final Logger logger = Logger.getLogger(this.getClass().getName());
    /**
     * Value object para consultas.
     *
     * <p/>
     * Se proveen métodos de acceso getter/setter para su inicialización y
     * acceso por las clases derivadas y vistas respectivamente.
     */
    private OficioVo vo;

    /**
     * Repositorio para una lista de oficios. Ejemplo: Utilizado en una búsqueda
     * de oficios.
     *
     * <p/>
     * Se proveen métodos de acceso getter/setter para su inicialización y
     * acceso por las clases derivadas y vistas respectivamente.
     *
     */
    private List<OficioPromovibleVo> oficios;
    //private DataModel resultados;

    /**
     * Para la gestión de las operaciones de I/O de esta sesión
     *
     */
//    private GestorArchivos gestorArchivos;
    /**
     * Acceso a la sesión actual.
     */
    @Inject
    //@ManagedProperty(value="#{sesion}")
    private Sesion sesion;

    // Servicios remotos
    @Inject
    private SiParametroImpl siParametroRemote;
    
    @Inject
    private OfOficioImpl oficioServicioRemoto;
           
    @Inject
    private OfOficioConsultaImpl ofOficioConsultaImpl;
    
    @Inject
    private ApCampoUsuarioRhPuestoImpl campoUsuarioRemote;
    @Inject
    private SiPermisoImpl siPermisoServicio;

    @ManagedProperty(value = "#{catalogosBean}")
    //@Inject
    private CatalogosBean catalogosBean;

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    protected static final UtilLog4j LOGGER = UtilLog4j.log;

    /**
     * Tareas de inicialización comunes para todas las clases derivadas.
     *
     */
    
    @PostConstruct
    //protected void iniciar() throws InsufficientPermissionsException, SIAException {
    protected void iniciar(){
        try{
        
        System.out.println("@PostConstruct en OficioBaseBean");
        getLogger().info(this, getClass().getName() + "@PostConstruct");
        if (sesion.getPermisos() == null) {
            System.out.println("@PostConstruct entro a buscar permisos");
            List<RolVO> newpermisos = siPermisoServicio.fetchPermisosPorUsuarioModulo(sesion.getUsuario().getId(), Constantes.OFICIOS_MODULO_ID, sesion.getBloqueActivo().getBloqueId());
            if (!newpermisos.isEmpty()) {
                System.out.println("@PostConstruct set permisos");
                sesion.setPermisos(new PermisosVo(newpermisos));
            }
        }

        // TODO: Pasar este proceso a un interceptor de permisos.
        if (getPermisos() != null) {
            System.out.println("@PostConstruct entro a permisso != null ");
            if (!this.permisosRequeridos()) {
                System.out.println("@ InsufficientPermissionsException");
                getLogger().fatal(this, getClass().getName() + " - Error de permisos requeridos");
                System.out.println("@PostConstruct trown InsufficientPermissionsException  ");
                throw new InsufficientPermissionsException();
            }
        }
        
        System.out.println("@ejecuanto postconstructo abstract");
        
        this.postConstruct();
        
        }catch(Exception e){
            LOGGER.info("Exceptiocn "+e);
        }

    }

    /**
     * Tareas de inicialización particulares de las clases derivadas. Se ejecuta
     * al final del evento @PostConstruct de las clases derivadas.
     *
     * @throws sia.excepciones.SIAException
     */
    protected abstract void postConstruct() throws SIAException;
    //protected abstract void postConstruct() ;

    /**
     * Define los permisos requeridos para ingresar a esta pantalla o proceso.
     *
     * Este método es ejecutado al inicio del evento @PostConstruct de cada bean
     * implementado, antes del método postConstruct(). En caso de regresar
     * 'falso', se arroja una excepción de permisos y se aborta la creación del
     * bean.
     *
     * TODO: Pasar este proceso a un interceptor de permisos.
     *
     * @return
     * @throws sia.excepciones.InsufficientPermissionsException
     */
    protected abstract boolean permisosRequeridos() throws InsufficientPermissionsException;

    /**
     *
     */
    @PreDestroy
    private void terminar() {

        getLogger().info(this, getClass().getName() + "@PreDestroy");

        // borrar los archivos temporales del usuario actual generados en el 
        // proceso
        // archivos adjuntos de oficio no guardados
//        this.gestorArchivos.borrarArchivosOficioTemporales();
        // archivos temporales para el visor
        /*
         * ESAPIEN-29/ene/15 - Opción de visor deshabilitado a la fecha. No 
         * se requiere copia temporal.
         * 
         */
        //this.gestorArchivos.borrarArchivosVisorTemporales();
        // tareas de la clase derivada
        this.preDestroy();

    }

    /**
     * Tareas de terminación y limpieza particulares de las clases derivadas. Se
     * ejecuta al final del evento @PreDestroy de las clases derivadas.
     *
     * <p>
     * Su implementación es opcional.
     * </p>
     *
     */
    protected void preDestroy() {
        getLogger().info(this, getClass().getName() + "@preDestroy");

        // Sobreescritura opcional
    }

    /**
     * Utilería de bitácoreo común para las clases derivadas.
     *
     * @return
     */
    protected UtilLog4j getLogger() {        
        return UtilLog4j.log;
    }

    /**
     * Sesión del usuario actual.
     *
     * @return
     */
    public Sesion getSesion() {
        return this.sesion;
    }
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * Permisos del usuario actual.
     *
     * @return
     */
    public PermisosVo getPermisos() {

        return this.sesion.getPermisos();

    }

    /**
     * ID del usuario actual
     *
     * @return
     */
    protected String getUsuarioId() {
        return getSesion().getUsuario().getId();
    }

    /**
     *
     * @return
     */
    protected Usuario getUsuario() {
        return getSesion().getUsuario();
    }

    /**
     * Para acceso de las vistas y clases derivadas.
     *
     * @return
     */
    public OficioVo getVo() {
        return vo;
    }

    /**
     * Para inicialización solo por las clases derivadas.
     *
     * @param vo
     */
    protected void setVo(OficioVo vo) {
        this.vo = vo;
    }

    /**
     *
     * Para inicialización solo por las clases derivadas.
     *
     * @param oficios
     */
    protected void setOficios(List<OficioPromovibleVo> oficios) {
        this.oficios = oficios;
    }

    /**
     * Para acceso desde un elemento DataTable en la vista (*.xhtml).
     *
     * @return
     */
    public DataModel getResultados() {

        ListDataModel resultados = new ListDataModel(this.oficios);

        return resultados;
    }

    /**
     * Acceso al servicio remoto del módulo de Oficios.
     *
     * @return
     */
    protected OfOficioImpl getOficioServicioRemoto() {

        return this.oficioServicioRemoto;
    }
    
    protected OfOficioConsultaImpl getOficioConsultaServicioRemoto() {

        return this.ofOficioConsultaImpl;
    }

    /**
     *
     * @return
     */
    protected ApCampoUsuarioRhPuestoImpl getCampoUsuarioRemote() {
        return campoUsuarioRemote;
    }

    public void setCatalogosBean(CatalogosBean catalogosBean) {
        this.catalogosBean = catalogosBean;
    }

    /**
     *
     * @return
     */
    protected CatalogosBean getCatalogosBean() {
        return catalogosBean;
    }

    /**
     * Regresa la compañía del usuario actual.
     *
     * @return
     */
    public Compania getCompaniaUsuario() {

        return this.sesion.getUsuario().getApCampo().getCompania();

    }

    /**
     * Regresa el campo del usuario actual.
     *
     * @return
     */
    public ApCampo getCampoUsuario() {

        return this.sesion.getUsuario().getApCampo();

    }

    /**
     * Regresa la gerencia del usuario actual.
     *
     * @return
     */
    public Gerencia getGerenciaUsuario() {

        return this.sesion.getUsuario().getGerencia();
    }

//    /**
//     * 
//     * @return 
//     */
//    protected GestorArchivos getGestorArchivos() {
//        return gestorArchivos;
//    }
//    
//    
//    
//    /**
//     * 
//     * @return 
//     */
//    public String getDirectorioOficios() {
//        return getGestorArchivos().getRutaDirectorioOficios();
//    }
//    
//    
    /**
     * Regresa la fecha actual en formato dd/MM/yyyy.
     *
     * @return
     */
    public String getFechaActual() {

        return Constantes.FMT_ddMMyyy.format(Calendar.getInstance().getTime());

    }

    /**
     * Muestra un mensaje en pantalla con la severidad e información
     * proporcionadas.
     *
     * @param severidad
     * @param resumen
     * @param detalle
     */
    protected void mostrarMensaje(FacesMessage.Severity severidad, String resumen, String detalle) {

        FacesMessage message = new FacesMessage();
        message.setSeverity(severidad);
        message.setSummary(resumen);
        message.setDetail(detalle);

        FacesContext.getCurrentInstance().addMessage(null, message);

    }

    /**
     * Prepara el archivo VO con la información del archivo guardado en disco
     * por un componente UI FileEntry (ace:fileEntry).
     *
     * Adicionalmente renombra el archivo guardado en disco con nombre de
     * archivo temporal para ser identificado por el proceso de limpieza de
     * archivos temporales del módulo.
     *
     * @param evt
     * @param archivoVo
     */   
    
    protected void prepararArchivoAdjuntoVo(FileUploadEvent evt, AdjuntoOficioVo archivoVo) {

        LOGGER.info(this, "@prepararArchivoAdjuntoVo");
        System.out.println("prepararArchivoAdjuntoVo");
        System.out.println(" evt "+evt == null);
        //UploadedFiles results = evt.getFiles();
        
               
        System.out.println(" FILE "+evt.getFile() == null);
        
        final UploadedFile fileInfo = evt.getFile();
        
        
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        try {
            // obtener información del archivo
          //  for (UploadedFile fileInfo : results.getFiles()) {

                boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

                if (addArchivo) {

                    DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                    documentoAnexo.setRuta(Constantes.OFICIOS_PATH_RELATIVO_OFICIOS);                                        
                    documentoAnexo.setNombreBase(fileInfo.getFileName());
                    documentoAnexo.setTipoMime(fileInfo.getContentType());
                    almacenDocumentos.guardarDocumento(documentoAnexo);

                    LOGGER.info(this, "Archivo guardado OK");
                    System.out.println("Archivo guardado OK");

                    // para mostrar nombre en pantalla y guardado en registro
                    archivoVo.setDirectorio(documentoAnexo.getRuta());
                    archivoVo.setNombre(documentoAnexo.getNombreBase());
                    archivoVo.setTipoArchivo(documentoAnexo.getTipoMime());
                    archivoVo.setTamanoArchivo((long) documentoAnexo.getTamanio());
                    //FIXME: Falta agregar la siguiente asignacion
                    //archivoVo.setArchivoSubido(fileInfo.getFile());                    
                    archivoVo.setArchivoGuardado(true);
                    archivoVo.setUrl(documentoAnexo.getRuta() + '/' + documentoAnexo.getNombreBase());

                } else {
                    archivoVo.setArchivoGuardado(false);

                    // error al guardar archivo en disco
                    String msg = "Caracteres no válidos en el nombre de archivo : " + validadorNombreArchivo.getCaracteresNoValidos();

                    mostrarMensaje(
                            FacesMessage.SEVERITY_ERROR,
                            "Error al guardar el archivo",
                            msg
                    );

                    getLogger().fatal(this, msg + " - " + fileInfo.getFileName() + " - " + validadorNombreArchivo.getCaracteresNoValidos());
                }

                /*if (!fileInfo.getFile().delete()) {
                    LOGGER.error(this, "File not deleted : {0}", new Object[]{fileInfo.getFileName()});
                }*/
            //}
        } catch (Exception e) {
            LOGGER.fatal(this, "+ + + ERROR + + +", e);
        }
    }
    /*
    public void subirPlanoOficina(FileUploadEvent fileEvent) {
        try {
            boolean valid = false;
            fileInfo = fileEvent.getFile();
            ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();

            AlmacenDocumentos almacenDocumentos
                    = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {
                boolean error = true;
                try {

                    DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                    documentoAnexo.setRuta(getDirPlano());
                    documentoAnexo.setNombreBase(fileInfo.getFileName());
                    documentoAnexo.setTipoMime(fileInfo.getContentType());
                    almacenDocumentos.guardarDocumento(documentoAnexo);

                    valid
                            = oficinaBeanModel.guardarPlanoOficina(
                                    documentoAnexo.getNombreBase(),
                                    documentoAnexo.getRuta(),
                                    documentoAnexo.getTipoMime(),
                                    documentoAnexo.getTamanio()
                            );
                    oficinaBeanModel.traerPlanoOficina();
                    oficinaBeanModel.setSgOficinaPlano(null);
                    oficinaBeanModel.setSubirArchivo(false);

                    error = false;

                } catch (SIAException e) {
                    LOGGER.fatal(e);
                }

                if (!valid || error) {
                    FacesUtils.addErrorMessage("No se pudo guardar el archivo. Porfavor pónganse en contacto con el Equipo del SIA al correo soportesia@ihsa.mx");
                }
            } else {
                FacesUtils.addInfoMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

            fileInfo.delete();
        } catch (IOException ex) {
            Logger.getLogger(OficinaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/


    /**
     * Busca el oficio correspondiente al ID. Si el usuario no tiene permisos
     * para ver oficios restringidos, valida que esté en la lista de usuarios
     * con acceso del oficio.
     *
     * @param oficioId
     * @return
     * @throws InsufficientPermissionsException
     */
    protected final OficioPromovibleVo buscarOficioVo(Integer oficioId)
            throws InsufficientPermissionsException {

        return getOficioConsultaServicioRemoto().buscarOficioVoPorId(
                oficioId,
                getUsuarioId(),
                !getSesion().puedeVerOficioRestringido());
    }

    /**
     * Realiza una consulta de oficios de acuerdo a los parámetros
     * proporcionados.
     *
     * @param actionEvent
     */
    public void buscarOficios(ActionEvent actionEvent) {

        getLogger().info(this, "@Bean.buscarOficios - compania ID = " + vo.getCompaniaId() + ", compania RFC = " + vo.getCompaniaRfc());
        
        System.out.println( "@Bean.buscarOficios - compania ID = " + vo.getCompaniaId() + ", compania RFC = " + vo.getCompaniaRfc());
                

        // convertir Compañía ID
        if (this.vo.getCompaniaId() > 0) {
            this.vo.setCompaniaRfc(catalogosBean.obtenerCompaniaRfc(this.vo.getCompaniaId()));
        } else {
            this.vo.setCompaniaRfc(null);
        }

                
        ResultadosConsultaVo resultadosVo = ofOficioConsultaImpl.buscarOficios(
                this.vo,
                !getSesion().puedeVerOficioRestringido(),
                getUsuarioId());

        if (resultadosVo.isCantidadMaximaExcedida()) {

            mostrarMensaje(
                    FacesMessage.SEVERITY_WARN, 
                    "Su búsqueda excede la cantidad máxima de resultados permitidos ("
                            + this.vo.getMaxOficios()+" Registros). Favor de proporcionar más parámetros de búsqueda. ", 
                    null
            );

        }

        this.oficios = resultadosVo.getResultados();

        //  getLogger().info(this, "oficio encontrados = " + getOficios().size());
    }

    /**
     * Redirecciona a la URL proporcionada.
     *
     * @param url
     */
    protected void redireccionar(final String url) {

        getLogger().info(this, "redireccionando a: " + url);

        /*
         * FacesContext fc = FacesContext.getCurrentInstance(); try {
         * fc.getExternalContext().redirect(url);//redirecciona la página }
         * catch (IOException ex) { logger.log(Level.SEVERE, null, ex); }
         */
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String prefix = UtilSia.getUrl(origRequest);

        FacesContext fc = FacesContext.getCurrentInstance();

        try {
            fc.getExternalContext().redirect(prefix + url); // redirecciona la página
        } catch (IOException ex) {

            getLogger().error(this, "Error de IO al redireccionar: " + ex.getMessage(), ex);
        }

    }

    /* Cambio para no mostrar preselecionada la gerencia 
     * jevazquez 16/02/15 */
    protected void configurarVoNoSeleccion(OficioVo oficioVo, CompaniaBloqueGerenciaVo cbg) {

        // compañía
        configurarVo(oficioVo, cbg);

        // gerencia - En proceso de alta, no debe aparecer preseleccionada la gerencia
        // jevazquez 16/02/15 
        oficioVo.setGerenciaId(-1);

    }

    /**
     * Configura el bean proporcionado con la informacion de compañía, bloque y
     * gerencia proporcionado.
     *
     * @param oficioVo
     * @param cbg
     */
    protected void configurarVo(OficioVo oficioVo, CompaniaBloqueGerenciaVo cbg) {

        // compañía
        oficioVo.setCompaniaRfc(cbg.getCompaniaRfc());
        oficioVo.setCompaniaId(getCatalogosBean().obtenerCompaniaId(cbg.getCompaniaRfc()));
        oficioVo.setCompaniaNombre(cbg.getCompaniaNombre());

        // bloque
        oficioVo.setBloqueId(cbg.getBloqueId());
        oficioVo.setBloqueNombre(cbg.getBloqueNombre());

        // gerencia
        oficioVo.setGerenciaId(cbg.getGerenciaId());
        oficioVo.setGerenciaNombre(cbg.getGerenciaNombre());

    }

    /**
     *
     * Método factory para crear una instancia del tipo correcto de oficio en
     * función de los permisos.
     *
     * @param permisosVo
     * @return
     * @throws InsufficientPermissionsException
     */
    protected OficioVo crearOficioVo(PermisosVo permisosVo) throws InsufficientPermissionsException {

        OficioVo resultadoVo;

        if (permisosVo.isRolEmisorOficiosSalida()) {

            resultadoVo = new OficioSalidaVo();

        } else if (permisosVo.isRolEmisorOficiosEntrada()) {

            resultadoVo = new OficioEntradaVo();

        } else {
            throw new InsufficientPermissionsException();
        }

        return resultadoVo;

    }

    /**
     *
     */
    protected void bloquearPantalla() {
         PrimeUtils.executeScript("bloquearPantalla('true')");
        //JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), ";bloquearPantalla('true');");

    }

    /**
     *
     */
    protected void desbloquearPantalla() {
        PrimeUtils.executeScript("bloquearPantalla('false')");
        //JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), ";bloquearPantalla('false');");

    }

    public final PrivacidadOficio getAccesoPublico() {

        return PrivacidadOficio.PUBLICO;

    }

    public final PrivacidadOficio getAccesoRestringido() {

        return PrivacidadOficio.RESTRINGIDO;

    }

    public final PrivacidadOficio getAccesoGerencia() {

        return PrivacidadOficio.GERENCIA;

    }

}
