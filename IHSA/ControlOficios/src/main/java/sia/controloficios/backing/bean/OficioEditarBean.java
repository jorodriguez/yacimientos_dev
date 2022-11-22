package sia.controloficios.backing.bean;

//import com.icesoft.faces.component.ext.HtmlDataTable;
import javax.faces.component.html.HtmlDataTable;
//import com.icesoft.faces.context.effects.JavascriptContext;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.mail.MessagingException;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FilesUploadEvent;
import sia.constantes.Constantes;
import sia.controloficios.sistema.soporte.FacesUtils;
import sia.controloficios.sistema.soporte.PrimeUtils;
import sia.excepciones.*;
import sia.modelo.oficio.vo.*;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.util.Env;
import sia.util.UtilSia;
import sia.util.ui.AccionUI;


/**
 * Bean para la pantalla de edición de oficios. Para las operaciones de altas y
 * modificación.
 *
 * @author esapien
 */
//@ManagedBean
@Named(value ="oficioEditarBean" )
public class OficioEditarBean extends OficioOpcionesBloquesUIBean {

    /**
     * Elementos de UI
     */
    private String titulo;

    // alta
    private AccionUI botonGuardarNuevo;
    private AccionUI botonGuardarPromover;
    private AccionUI botonCancelarNuevo;

    // edicion
    private AccionUI botonGuardarEdicion;
    private AccionUI botonCancelarEdicion;

    // para dialogo de búsqueda para oficio asociado
    private String numeroOficioAsociado;

    // para dialogo de búsqueda de usuario con acceso al oficio restringido
    private String nombreUsuarioAcceso;

    // para binding con datatable de resultados para la 
    // manipulación de los registros
    //private HtmlDataTable tablaResultados;
    private DataTable tablaResultados;

    // para binding con datatable de resultados de usuarios para acceso a 
    // oficio restringido, para la manipulación de los registros
    private DataTable tablaResultadosUsuariosAcceso;

    // para binding con datatable de resultados para la 
    // manipulación de los registros
    private DataTable tablaAsociados;

    /**
     * Para resultados de búsqueda de usuarios para acceso a oficio restringido
     *
     */
    private List<UsuarioVO> usuariosAcceso;

    /**
     *
     */
    //private DataModel resultadosUsuariosAcceso;
    /**
     * Binding para la tabla de usuarios con acceso al oficio de tipo de
     * privacidad Restringido.
     *
     */
    private DataTable tablaUsuariosRestringido;
    
    private DataModel resultadoOficiosAsociados;

    // para identificar si es alta o cambio
    private boolean modificacion;

    //mostrar si se crea o no en otro bloque
    private boolean crearCopia;

    //Lista de Bloques donde se creara la copia 
    private List<SelectItem> bloquesCopia;

    private int bloqueACopiarId;

    private String ListaBloques;
    
    //Validar rol para poder hacer multi bloque
    private  boolean multiBloque;

    /**
     *
     *
     * @throws InsufficientPermissionsException
     */
    @Override
    protected void postConstruct() throws InsufficientPermissionsException {
        
        System.out.println("@postcontruct de oficioEditar");

        // valor inicial para la vista
        // en caso de recibir un oficioId, obtener objeto vo correspondiente
        //String oficioId = FacesUtils.getRequestParameter(OFICIO_ID);
        
        int oficioId = getContextParamOficioId();
               
        setCrearCopia(Constantes.FALSE);
        setBloquesCopia(new ArrayList<SelectItem>());
        setBloqueACopiarId(-1);
        setListaBloques("");
        setMulitoBlque(getPermisos().isCopiarEnOtroBloque());

        LOGGER.info(this, "oficioId = " + oficioId);

        // validar si es alta o modificacion
        //modificacion = !UtilSia.isNullOrBlank(oficioId);
        modificacion = oficioId > 0;

        if (modificacion) {
            System.out.println("Es una edicion de oficio "+oficioId);
            // Operacion de Modificacion
            // obtener desde bd
            this.setVo(buscarOficioVo(oficioId));

            // establecer el ID numérico para la compañía
            this.getVo().setCompaniaId(getCatalogosBean()
                    .obtenerCompaniaId(this.getVo().getCompaniaRfc()));

            // preparar UI
            titulo = "Modificar Oficio de " + this.getVo().getTipoOficioNombre();

            // botones de alta
            botonGuardarNuevo = new AccionUI();
            botonGuardarNuevo.setVisible(false);

            botonGuardarPromover = new AccionUI();
            botonGuardarPromover.setVisible(false);

            botonCancelarNuevo = new AccionUI();
            botonCancelarNuevo.setVisible(false);

            // botones de modificacion
            botonGuardarEdicion = new AccionUI();
            botonGuardarEdicion.setVisible(true);

            botonCancelarEdicion = new AccionUI();
            botonCancelarEdicion.setVisible(true);

            // inicializar opciones de combo de bloques en pantalla
            // mostrar los valores existentes de bloque y gerencia
            configurarCombosCompaniaBloqueGerencia();

            LOGGER.info(this, "Oficio Vo desde BD = " + this.getVo());

        } else {

            System.out.println("Es una captura nueva de oficio ");
            
            // Operacion de Alta
            // inicializar bean default
            // el tipo dependerá del rol de emisor de oficio del usuario
            // (rol exclusivo)
            setVo(crearOficioVo(getPermisos()));

            // preparar UI
            // valores iniciales para pantalla de consulta
            titulo = "Alta de Oficio de " + this.getVo().getTipoOficioNombre();

            // botones de alta
            botonGuardarNuevo = new AccionUI();
            botonGuardarNuevo.setVisible(true);

            botonGuardarPromover = new AccionUI();
            botonGuardarPromover.setVisible(true);

            botonCancelarNuevo = new AccionUI();
            botonCancelarNuevo.setVisible(true);

            // botones de modificacion
            botonGuardarEdicion = new AccionUI();
            botonGuardarEdicion.setVisible(false);

            botonCancelarEdicion = new AccionUI();
            botonCancelarEdicion.setVisible(false);

            // restablecer VO para limpiar forma
            inicializarVo();

            // inicializar opciones de combo de bloques en pantalla
            // poner bloque activo del usuario como bloque inicial
            configurarCombosCompaniaBloqueGerencia();

        }
    }

    /**
     * Este método es ejecutado previo al método postConstruct() durante el
     * evento @PostConstruct de la clase base.
     *
     * @return
     */
    @Override
    protected boolean permisosRequeridos() throws InsufficientPermissionsException {

        // en caso de recibir un oficioId, obtener objeto vo correspondiente
        String oficioId = FacesUtils.getRequestParameter("oficioId");

        // validar si es alta o modificacion
        boolean resultado;

        if (UtilSia.isNullOrBlank(oficioId)) {

            // alta
            resultado = getPermisos().isAltaOficio();

        } else {
            // modificacion
            resultado = getPermisos().isModificarOficio();

        }

        getLogger().info(this, "@permisosRequeridos - oficioId = " + oficioId + ", resultado = " + resultado);

        return resultado;

    }

    /**
     * Registra un nuevo oficio en base de datos y realiza la promoción al
     * siguiente estatus en un solo paso.
     *
     * @param actionEvent
     * @return
     */
    //public String guardarPromover(ActionEvent actionEvent) {
    public String guardarPromover() {
        
        System.out.println("=== Click en guardar y promover");

        String resultado = Constantes.VACIO;

        try {

          //  List<InformacionOficioVo> listVo = new ArrayList<>();
                OficioPromovibleVo newOficio = (OficioPromovibleVo) getVo();
                List<List<Object>> listOfProm = new ArrayList<>();

                // informacion a guardar
                InformacionOficioVo informacionVo
                        = new InformacionOficioVo(
                               newOficio,
                                getUsuarioId());
//                listVo.add(informacionVo);
//                if (!bloquesCopia.isEmpty()){
//                    for(SelectItem i : bloquesCopia){
//                        informacionVo.getOficioVo().setBloqueId(Integer.parseInt(i.getValue().toString()));
//                        informacionVo.getOficioVo().setBloqueNombre(i.getLabel());
//                        listVo.add(informacionVo);
//                    }
//                }

             listOfProm = getOficioServicioRemoto().agregarOficio(informacionVo,bloquesCopia);
            
            for(List<Object> lo : listOfProm){                
                        System.out.println("@@ "+lo);
                        String nombreBloque = lo.get(2) != null ? lo.get(2).toString():"";                                
                        informacionVo.getOficioVo().setOficioId(Integer.parseInt(lo.get(0).toString()));
                        informacionVo.getOficioVo().setBloqueId(Integer.parseInt(lo.get(1).toString()));
                        informacionVo.getOficioVo().setBloqueNombre(nombreBloque);
                        informacionVo.getOficioVo().setEstatusId(Integer.parseInt(lo.get(3).toString()));
                        informacionVo.getOficioVo().setEstatusNombre(lo.get(4).toString());
                    
                getOficioServicioRemoto().promoverEstatusOficio( informacionVo.getOficioVo(), getUsuario());
            }

           // getVo().setOficioId(oficioId);

            

            mostrarMensaje(FacesMessage.SEVERITY_INFO,
                    "El oficio " + getVo().getOficioNumero() + " fue registrado y promovido exitosamente.", null);

            // limpiar valores en la forma
            inicializarVo();

            // inicializar opciones de combo de bloques en pantalla
            configurarCombosCompaniaBloqueGerencia();

            resultado = Constantes.OFICIOS_VISTA_EDITAR;

        } catch (MissingRequiredValuesException ex) {

            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Proporcione los siguientes campos obligatorios: " + ex.getValoresFaltantes(), null);

        } catch (ExistingItemException ex) {

            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Ya existe un registro con el número de oficio proporcionado. Favor de revisar.", null);

        } catch (UnavailableItemException ex) {

            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El oficio a asociar ya se encuentra asociado. Favor de revisar.", null);

        } catch (InvalidValuesException ex) {

            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Los oficios a asociar deben pertenecer al mismo bloque.", null);

        } catch (PromotionFailedException ex) {

            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Ocurrió un error en el proceso de promoción del oficio: " + ex.getMessage(), null);

        } catch (MessagingException ex) {

            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Ocurrió un error en el proceso de promoción del oficio: " + ex.getMessage(), null);

        } catch (SIAException ex) {

            mostrarMensaje(FacesMessage.SEVERITY_FATAL, "Ocurrió un error en el proceso de guardado: " + ex.getMessage(), null);

        } finally {

            desbloquearPantalla();

        }

        return resultado;

    }

    /**
     *
     * Realiza el guardado de la información de un oficio.
     *
     * TODO: Al invocar este metodo desde la misma página se recrea el bean,
     * generando llamadas a PostConstruct y PreDestroy. Revisar
     *
     * @param actionEvent
     */
    //public String guardarOficio(ActionEvent actionEvent) {
    public String guardarOficio() {
        
        System.out.println("=== Click en guardarOficio");

        String resultado = Constantes.VACIO;

        try {

            // si se recibe un oficio ID, es para modificación
            if (UtilSia.greaterThanZero(getVo().getOficioId())) {
                  
                System.out.println("## es una modificacion");
                
                // modificacion
                getLogger().info(this, "@guardarOficio - modificando oficioId = " + getVo().getOficioId());

                getOficioServicioRemoto().modificarOficio((OficioPromovibleVo) getVo(), getUsuarioId());

                mostrarMensaje(FacesMessage.SEVERITY_INFO, "El oficio "
                        + getVo().getOficioNumero() + " se actualizó correctamente.", null);

                resultado = Constantes.OFICIOS_VISTA_DETALLE;

            } else {
                System.out.println("## es un alta");
                // alta
                getLogger().info(this, "@guardarOficio - realizando alta de nuevo oficio");
                List<InformacionOficioVo> listVo = new ArrayList<>();
                OficioPromovibleVo newOficio = (OficioPromovibleVo) getVo();

                // informacion a guardar
                InformacionOficioVo informacionVo
                        = new InformacionOficioVo(
                               newOficio,
                                getUsuarioId());
                listVo.add(informacionVo);
//                if (!bloquesCopia.isEmpty()){
//                    for(SelectItem i : bloquesCopia){
//                        OficioPromovibleVo copy = newOficio;
//                        copy.setBloqueId(Integer.parseInt(i.getValue().toString()));
//                        copy.setBloqueNombre(i.getLabel());
//                        listVo.add(new InformacionOficioVo(copy, getUsuarioId()));
//                    }
//                }

                getOficioServicioRemoto().agregarOficio(informacionVo, bloquesCopia);

                mostrarMensaje(FacesMessage.SEVERITY_INFO, "El oficio "
                        + getVo().getOficioNumero() + " se registró correctamente.", null);

                // limpiar valores en la forma
                inicializarVo();

                // inicializar opciones de combo de bloques en pantalla
                configurarCombosCompaniaBloqueGerencia();

                // permanecer en la pagina de altas
                resultado = Constantes.OFICIOS_VISTA_EDITAR;

            }

        } catch (MissingRequiredValuesException ex) {

            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Proporcione los siguientes campos obligatorios: " + ex.getValoresFaltantes(), null);

        } catch (ExistingItemException ex) {

            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Ya existe un registro con el número de oficio proporcionado. Favor de revisar.", null);

        } catch (UnavailableItemException ex) {

            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "El oficio a asociar ya se encuentra asociado. Favor de revisar.", null);

        } catch (InvalidValuesException ex) {

            mostrarMensaje(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);

        } catch (SIAException ex) {

            mostrarMensaje(FacesMessage.SEVERITY_FATAL, "Ocurrió un error en el proceso de guardado: " + ex.getMessage(), null);

        } finally {

            desbloquearPantalla();
        }

        return resultado;
    }

    /**
     * Remueve los valores en el VO de oficio para limpiar la forma en pantalla.
     *
     */
    private void inicializarVo() {

        // si el usuario no tiene permisos para ver todas las gerencias, 
        // establecer la del usuario
        getVo().setCompaniaId(null);
        getVo().setBloqueId(null);
        getVo().setGerenciaId(null);

        getVo().setAcceso(PrivacidadOficio.GERENCIA);

        getVo().setOficioId(null);
        getVo().setOficioNumero(null);
        getVo().setOficioAsunto(null);
        getVo().setOficioFecha(null);
        //getVo().setOficioDescripcion(null);
        getVo().setObservaciones(null);

        // establecer datos de bloque activo del usuario
        configurarVoNoSeleccion(getVo(), getSesion().getBloqueActivo());

        this.removerArchivoAdjunto(null);

    }

    /**
     * Obtiene los oficios candidatos para ser asociados por un otro oficio.
     *
     * Los oficios deben pertenecer al mismo bloque y gerencia.
     *
     *
     * @param actionEvent
     */
    public void buscarOficiosAsociacion(ActionEvent actionEvent) {

        getLogger().info(this, "@buscarOficioAsociacion");

        getLogger().info(this, "Oficio actual = " + getVo().getOficioId());

        OficioVo asociadoVo = new OficioConsultaVo();
        asociadoVo.setOficioNumero(this.getNumeroOficioAsociado());

        // en caso de modificación, no incluir oficio actual en los resultados
        asociadoVo.setOficioIdExcluir(getVo().getOficioId());

        // excluir de los resultados los oficios asociados existentes
        asociadoVo.setAsociadoHaciaOficios(getVo().getAsociadoHaciaOficios());

        // obtener solamente los oficios del bloque 
        // del oficio a asociar
        setOficios(getOficioConsultaServicioRemoto()
                .buscarOficiosAsociacion(
                        asociadoVo,
                        getVo().getBloqueId()));
       
        this.resultadoOficiosAsociados = new ListDataModel(getOficios());
         
    }

    /**
     *
     * @param actionEvent
     */
    public void buscarUsuariosAcceso(ActionEvent actionEvent) {

        // excluir de los resultados los usuarios ya agregados
        usuariosAcceso
                = getOficioConsultaServicioRemoto().buscarUsuariosAccesoOficioRestringido(
                                getNombreUsuarioAcceso(),
                                getVo());

    }

    /**
     * Para pruebas en internet explorer
     *
     * @param actionEvent
     */
    public void mostrarDialogoAdjunto(ActionEvent actionEvent) {
                
        PrimeUtils.executeScript("dialogoAdjunto.show()");        
        //JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), ";dialogoAdjunto.show();");
    }

    /**
     * Es invocado al utilizar el dialogo de selección de oficio asociado para
     * limpiar la forma.
     *
     * @param actionEvent
     */
    public void limpiarOficiosAsociados(ActionEvent actionEvent) {

        getLogger().info(this, "@limpiarOficiosAsociados");

        this.numeroOficioAsociado = null;
        this.setOficios(null);

        // mostrar inicialmente todos los oficios existentes
        buscarOficiosAsociacion(null);

    }

    /**
     *
     * @param actionEvent
     */
    public void limpiarUsuariosAcceso(ActionEvent actionEvent) {

        this.nombreUsuarioAcceso = null;
        this.setUsuariosAcceso(null);

        // mostrar inicialmente los usuarios con acceso
        buscarUsuariosAcceso(null);

    }

    /**
     * Validación para mostrar botón de Remover Archivo Adjunto en la vista.
     *
     * @return
     */
    public boolean isArchivoAdjuntoSeleccionado() {

        AdjuntoOficioVo archivo = getVo().getArchivoAdjunto();

        return archivo != null && archivo.getNombre() != null && archivo.getNombre().trim().length() > 0;

    }

    /**
     * Remueve el archivo adjunto existente seleccionado.
     *
     * @param actionEvent
     */
    public void removerArchivoAdjunto(ActionEvent actionEvent) {

        getVo().setArchivoAdjunto(new AdjuntoOficioVo());

    }

    public String getNumeroOficioAsociado() {
        return numeroOficioAsociado;
    }

    public void setNumeroOficioAsociado(String numeroOficioAsociado) {
        this.numeroOficioAsociado = numeroOficioAsociado;
    }

    public String getNombreUsuarioAcceso() {
        return nombreUsuarioAcceso;
    }

    public void setNombreUsuarioAcceso(String nombreUsuarioAcceso) {
        this.nombreUsuarioAcceso = nombreUsuarioAcceso;
    }

 
    public DataTable getTablaResultadosUsuariosAcceso() {
        return tablaResultadosUsuariosAcceso;
    }

    public void setTablaResultadosUsuariosAcceso(DataTable tablaResultadosUsuariosAcceso) {
        this.tablaResultadosUsuariosAcceso = tablaResultadosUsuariosAcceso;
    }

    public DataTable getTablaAsociados() {
        return tablaAsociados;
    }

    public void setTablaAsociados(DataTable tablaAsociados) {
        this.tablaAsociados = tablaAsociados;
    }

    public DataTable getTablaUsuariosRestringido() {
        return tablaUsuariosRestringido;
    }

    public void setTablaUsuariosRestringido(DataTable tablaUsuariosRestringido) {
        this.tablaUsuariosRestringido = tablaUsuariosRestringido;
    }

    /**
     *
     * @return
     */
    public DataModel getResultadosUsuariosAcceso() {

        ListDataModel resultadosUsuariosAcceso = new ListDataModel(this.usuariosAcceso);

        return resultadosUsuariosAcceso;
    }

    public List<UsuarioVO> getUsuariosAcceso() {
        return usuariosAcceso;
    }

    public void setUsuariosAcceso(List<UsuarioVO> usuariosAcceso) {
        this.usuariosAcceso = usuariosAcceso;
    }

    /**
     *
     * @param actionEvent
     */
    public void agregarAAsociados(ActionEvent actionEvent) {
        
        OficioVo voAsociado = (OficioVo) this.getTablaResultados().getRowData();
        
        getVo().getAsociadoHaciaOficios().add(voAsociado);

        getLogger().info(this, "@agregarAAsociados - asociado a oficios (cant.) = " + getVo().getAsociadoHaciaOficios().size());

        // refrescar resultados de oficios a asociar
        this.buscarOficiosAsociacion(null);

    }

    /**
     *
     * @param actionEvent
     */
    public void removerDeAsociados(ActionEvent actionEvent) {

        OficioVo voAsociado = (OficioVo) this.getTablaAsociados().getRowData();

        getVo().getAsociadoHaciaOficios().remove(voAsociado);

        // refrescar resultados de oficios a asociar
        this.buscarOficiosAsociacion(null);

    }

    /**
     *
     * @param actionEvent
     */
    public void agregarAUsuariosAcceso(ActionEvent actionEvent) {

        UsuarioVO usuarioVo = (UsuarioVO) this.getTablaResultadosUsuariosAcceso().getRowData();

        getVo().getRestringidoAUsuarios().add(usuarioVo);

        getLogger().info(this, "@agregarAUsuariosAcceso - restringido a usuarios (cant.) = " + getVo().getRestringidoAUsuarios().size());

        // refrescar resultados de oficios a asociar
        this.buscarUsuariosAcceso(null);

    }

    /**
     *
     * @param actionEvent
     */
    public void removerUsuarioRestringido(ActionEvent actionEvent) {

        UsuarioVO usuario = (UsuarioVO) this.getTablaUsuariosRestringido().getRowData();

        getVo().getRestringidoAUsuarios().remove(usuario);

        // refrescar resultados de oficios a asociar
        //this.buscarOficiosAsociacion(null);
    }

    /**
     * Prepara el VO de archivo adjunto de un nuevo oficio con la información de
     * un archivo guardado en disco.
     *
     * @param e
     */
    public void prepararArchivoAdjuntoVo(FileUploadEvent e) {

        prepararArchivoAdjuntoVo(e, getVo().getArchivoAdjunto());

    }

    public AccionUI getBotonCancelarEdicion() {
        return botonCancelarEdicion;
    }

    public AccionUI getBotonCancelarNuevo() {
        return botonCancelarNuevo;
    }

    public AccionUI getBotonGuardarEdicion() {
        return botonGuardarEdicion;
    }

    public AccionUI getBotonGuardarNuevo() {
        return botonGuardarNuevo;
    }

    public AccionUI getBotonGuardarPromover() {
        return botonGuardarPromover;
    }

    public String getTitulo() {
        return titulo;
    }

    public boolean isModificacion() {
        return modificacion;
    }

    /**
     * @return the crearCopia
     */
    public boolean isCrearCopia() {
        return crearCopia;
    }

    /**
     * @param crearCopia the crearCopia to set
     */
    public void setCrearCopia(boolean crearCopia) {
        this.crearCopia = crearCopia;
    }

    /**
     * @return the bloquesCopia
     */
    public List<SelectItem> getBloquesCopia() {
        return bloquesCopia;
    }

    /**
     * @param bloquesCopia the bloquesCopia to set
     */
    public void setBloquesCopia(List<SelectItem> bloquesCopia) {
        this.bloquesCopia = bloquesCopia;
    }

    public void agregarBloqueCopy(ValueChangeEvent e) {
        int idBloque = (int) e.getNewValue();

        if (idBloque > 0 && getVo().getBloqueId() != idBloque) {
            SelectItem newItem = agregarBloqueCopy(idBloque);
            if (getBloquesCopia().isEmpty()) {
                getBloquesCopia().add(newItem);
                setBloqueACopiarId(idBloque);
                setListaBloques(newItem.getLabel());
            } else {
                    if(!getBloquesCopia().contains(newItem)){
                        getBloquesCopia().add(newItem);
                        setBloqueACopiarId(idBloque);
                        String newEtiqueta = getListaBloques();
                        setListaBloques(newEtiqueta+", "+newItem.getLabel());
                }
            }System.out.println("bloques: "+idBloque+" == "+this.getVo().getBloqueId());
        }
    }

    /**
     * @return the bloqueACopiarId
     */
    public int getBloqueACopiarId() {
        return bloqueACopiarId;
    }

    /**
     * @param bloqueACopiarId the bloqueACopiarId to set
     */
    public void setBloqueACopiarId(int bloqueACopiarId) {
        this.bloqueACopiarId = bloqueACopiarId;
    }

    /**
     * @return the ListaBloques
     */
    public String getListaBloques() {
        return ListaBloques;
    }

    /**
     * @param ListaBloques the ListaBloques to set
     */
    public void setListaBloques(String ListaBloques) {
        this.ListaBloques = ListaBloques;
    }

    /**
     * @return the multiBloque
     */
    public boolean isMulitoBlque() {
        return multiBloque;
    }

    /**
     * @param mulitoBlque the multiBloque to set
     */
    public void setMulitoBlque(boolean mulitoBlque) {
        this.multiBloque = mulitoBlque;
    }

    public DataTable getTablaResultados() {
        return tablaResultados;
    }

    public void setTablaResultados(DataTable tablaResultados) {
        this.tablaResultados = tablaResultados;
    }

    public DataModel getResultadoOficiosAsociados() {
        return resultadoOficiosAsociados;
    }

    public void setResultadoOficiosAsociados(ListDataModel resultadoOficiosAsociados) {
        this.resultadoOficiosAsociados = resultadoOficiosAsociados;
    }

}
