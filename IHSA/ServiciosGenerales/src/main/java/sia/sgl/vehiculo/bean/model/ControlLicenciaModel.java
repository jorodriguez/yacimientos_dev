/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.vehiculo.bean.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;

import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgLicencia;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoTipoEspecifico;
import sia.modelo.SiAdjunto;
import sia.modelo.SiPais;
import sia.modelo.Usuario;
import sia.modelo.licencia.vo.LicenciaVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.notificaciones.sgl.impl.NotificacionServiciosGeneralesImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgTipoImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.servicios.sgl.vehiculo.impl.SgLicenciaImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiPaisImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author ihsa
 */
//
@Named(value = "controlLicenciaBean")
@ViewScoped
public class ControlLicenciaModel implements Serializable {

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    @Inject
    Sesion sesion;

    @Inject
    SgLicenciaImpl sgLicenciaImpl;
    @Inject
    SiPaisImpl siPaisImpl;
    @Inject
    SgTipoImpl sgTipoImpl;
    @Inject
    SiParametroImpl siParametroImpl;
    @Inject
    SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    SgTipoTipoEspecificoImpl sgTipoTipoEspecificoImpl;
    @Inject
    UsuarioImpl usuarioImpl;
    @Inject
    SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    NotificacionServiciosGeneralesImpl notificacionServiciosGeneralesImpl;
    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    private Usuario usuario;
    private SgTipo sgTipo;
    private List<LicenciaVo> lista;
    private int idPais;
    private String user;
    private int idTipoEspecifico;
    private SgLicencia sgLicencia;
    private LicenciaVo licenciaVo;
    //

    private boolean popUp = false;
    private boolean subirArchivoPop = false;
    private boolean modificarPopUp = false;
    private boolean crearPop = false;
    private boolean selectTodos;
    private int numeroDias;
    private String mensaje;
    private List<SelectItem> listaUsuario;
    private List<UsuarioVO> listaUsuarioVo;
    private List<SelectItem> listaPaises;
    private List<SelectItem> listaTipoEspecifico;

    private Date fechaVencimientoInicio;
    private Date fechaVencimientoFin;
    private Date fechaInicio;
    private Date fechaFin;
    @Setter
    @Getter
    private List<SelectItem> listaFiltro;
    @Setter
    @Getter
    private String filtro;
    @Getter
    @Setter
    private UploadedFile fileInfo;

    /**
     * Creates a new instance of ControlLicenciaModel
     */
    public ControlLicenciaModel() {
    }

    @PostConstruct
    public void goToControlLicencias() {
        filtro = "todo";
        listaFiltro = new ArrayList<>();
        lista = new ArrayList<>();
        listaTipoEspecifico = new ArrayList<>();
        listaPaises = new ArrayList<>();
        //
        traerLiciencia();
        listaPais();
        buscarTipoGeneral();
        listaTipoEspecifico();
        llenarFiiltro();
        //cargarListaUsuarios();
        //

    }

    private void llenarFiiltro() {
        listaFiltro.add(new SelectItem("todo", "Todos"));
        listaFiltro.add(new SelectItem("vigentes", "Vigentes"));
        listaFiltro.add(new SelectItem("vencidas", "Vencidos"));
        listaFiltro.add(new SelectItem("PorVencer", "Por vencer"));
    }

    public void cambiarFiltro() {
        buscarLicenciasByFiltros();
    }

    public List<LicenciaVo> traerLiciencia() {
        setLista(sgLicenciaImpl.traerLiciencia(sesion.getUsuario().getApCampo().getId(), ""));
        return getLista();
    }

    public void listaPais() {
        List<SelectItem> l = new ArrayList<>();
        try {
            List<SiPais> lt = siPaisImpl.findAll("nombre", true, false);
            for (SiPais siPais : lt) {
                SelectItem item = new SelectItem(siPais.getId(), siPais.getNombre());
                l.add(item);
            }
            setListaPaises(l);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
    }

    public void buscarTipoGeneral() {
        setSgTipo(sgTipoImpl.find(11));
    }

    public boolean quitarLicenciaVigente(LicenciaVo licenciaVo) {
        return sgLicenciaImpl.quitarLicenciaVigente(sesion.getUsuario(), licenciaVo.getId());
    }

    public void guardarLicencia() {
        sgLicenciaImpl.guardarLicencia(
                sesion.getUsuario(),
                getLicenciaVo(),
                getUser(),
                getIdTipoEspecifico(),
                getSgTipo(),
                getIdPais()
        );
    }

    public String dirLicencia() {
        return "SGyL/Vehiculo/Licencia" + "/" + getLicenciaVo().getId() + "/";
        //return siParametroImpl.find(1).getUploadDirectory() + "SGyL/Vehiculo/Licencia" + "/" + getLicenciaVo().getId() + "/";
    }

    public boolean guardarArchivo(String fileName, String ruta, String contentType, long size) {
        boolean v = false;
//        UtilLog4j.log.info(this, "Absolute path " + dirLicencia());
        SiAdjunto siAdjunto
                = siAdjuntoImpl.guardarArchivoDevolverArchivo(
                        sesion.getUsuario().getId(),
                        1,
                        ruta + fileName,
                        fileName,
                        contentType,
                        size,
                        9,
                        "SGyL"
                );
//        UtilLog4j.log.info(this, "Aqui después de guardar el archivo");
        if (siAdjunto != null) {
            v = sgLicenciaImpl.agregarArchivoLicencia(getLicenciaVo().getId(), sesion.getUsuario(), siAdjunto);
        }
//        else {
//            siAdjuntoImpl.eliminarArchivo(siAdjunto, sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
//        }
        return v;
    }

    public void eliminarLicencia() {

        if (!getLista().isEmpty()) {

            for (LicenciaVo lic : getLista()) {
                if (lic.isSelect()) {
                    sgLicenciaImpl.eliminarLicencia(sesion.getUsuario(), lic.getId());
                }
            }
            FacesUtils.addInfoMessage("Se eliminaron las licencias seleccionadas");
            goToControlLicencias();
        } else {
            FacesUtils.addErrorMessage("no se encotro alguna licencia");
        }

    }

    public List<String> autocompletar(String cadena) {
        //usuarioImpl.
        List<UsuarioVO> lu = apCampoUsuarioRhPuestoImpl.traerUsuarioCampo(sesion.getUsuario().getApCampo().getId());
        List<String> nombres = new ArrayList<>();
        lu.stream().filter(us -> us.getNombre().toLowerCase().startsWith(cadena.toLowerCase())).forEach(u -> {
            nombres.add(u.getNombre());
        });
        return nombres;

    }

    public void completarModificacionLicencia() {
        if (licenciaVo.getId() > 0) {
            sgLicenciaImpl.modificarLicencia(sesion.getUsuario(), getLicenciaVo().getId(), getIdTipoEspecifico(), getIdPais(), getLicenciaVo().getExpedida(), getLicenciaVo().getVencimiento(), getLicenciaVo().getNumero());
        } else {
            terminarEdicionLicencia(licenciaVo.getNumero());
        }
        //
        traerLiciencia();
        PrimeFaces.current().executeScript(";$(editLicenciaModal).modal('hide');");
    }

    public void subirLicencia(FileUploadEvent fileEvent) {
        fileInfo = fileEvent.getFile();
        boolean valid = false;
        final ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();

        final AlmacenDocumentos almacenDocumentos
                = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

        if (addArchivo) {
            try {
                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                documentoAnexo.setTipoMime(documentoAnexo.getTipoMime());
                documentoAnexo.setRuta(getDirLicencia());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                valid = guardarArchivo(
                        documentoAnexo.getNombreBase(),
                        documentoAnexo.getRuta(),
                        documentoAnexo.getTipoMime(),
                        documentoAnexo.getTamanio()
                );
                setLicenciaVo(null);
                goToControlLicencias();

                fileInfo.delete();
            } catch (IOException e) {
                LOGGER.error(e);
            } catch (SIAException e) {
                LOGGER.error(e);
            }

            if (!valid) {
                FacesUtils.addInfoMessage("Ocurrio una excepción, favor de comunicar a sia@ihsa.mx");
            }
        } else {
            FacesUtils.addInfoMessage(new StringBuilder()
                    .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                    .append(validadorNombreArchivo.getCaracteresNoValidos())
                    .toString());
        }

        PrimeFaces.current().executeScript(";$(addArchivoModal).modal('hide');");
    }

    public String getDirLicencia() {
        if (getLicenciaVo() != null) {
            return dirLicencia();
        } else {
            return "";
        }
    }

    public void quitarArchivo(int idlic) {
        for (LicenciaVo vo : getLista()) {
            if (vo.getId() == idlic) {
                setLicenciaVo(vo);
                setIdPais(vo.getIdPais());
                setIdTipoEspecifico(vo.getIdTipo());
                break;
            }
        }
        SiAdjunto adjunto = siAdjuntoImpl.find(getLicenciaVo().getAdjunto());
        if (adjunto != null) {
            try {
                proveedorAlmacenDocumentos.getAlmacenDocumentos().borrarDocumento(adjunto.getUrl());
                sgLicenciaImpl.quitarArchivoLicencia(getLicenciaVo().getId(), sesion.getUsuario());
                siAdjuntoImpl.eliminarArchivo(adjunto, sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
            } catch (Exception e) {
                LOGGER.fatal(this, "Error : " + e.getMessage(), e);
            }
        }
        //goToControlLicencias();

    }

    public Usuario buscarEmpledoPorNombre() {
        UtilLog4j.log.info(this, "Usuario a buscar " + getUser());

        Usuario retVal = null;

        try {
            if (getUser() != null) {
                setUsuario(usuarioImpl.buscarPorNombre(getUser()));
                retVal = getUsuario();
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }

        return retVal;
    }

    public LicenciaVo buscarLiecinciaVigente() {
        LicenciaVo retVal = null;

        try {
            retVal = sgLicenciaImpl.buscarLicenciaVigentePorUsuario(getUsuario().getId());
        } catch (Exception e) {
            LOGGER.error(e);
        }

        return retVal;
    }

    public int licenciaPorVencer() {
        LicenciaVo l = sgLicenciaImpl.buscarLicenciaVigentePorUsuario(getUsuario().getId());
        if (l != null) {
//            UtilLog4j.log.info(this, "Fecha vence: " + l.getFechaVencimiento());
            setNumeroDias(siManejoFechaLocal.dias(l.getVencimiento(), new Date()));
//            UtilLog4j.log.info(this, "Numero de dias " + getNumerDias());
            return getNumeroDias();
        } else {
            return 0;
        }
    }

    public void listaTipoEspecifico() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        UtilLog4j.log.info(this, "Tipo: " + (this.sgTipo != null ? this.sgTipo.getNombre() : null));
        try {
            List<SgTipoTipoEspecifico> lt = sgTipoTipoEspecificoImpl.traerPorTipoPago(getSgTipo(), Constantes.NO_ELIMINADO, Constantes.BOOLEAN_FALSE);
            for (SgTipoTipoEspecifico sgTipoTipoEspecifico : lt) {
                SelectItem item = new SelectItem(sgTipoTipoEspecifico.getSgTipoEspecifico().getId(), sgTipoTipoEspecifico.getSgTipoEspecifico().getNombre());
                l.add(item);
            }
            setListaTipoEspecifico(l);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Fui a ver que tenia la excepción de traer tipo especifico");
        }
    }

    public List<SelectItem> getSgTipoEspecificoBySgTipoSelectItem() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        UtilLog4j.log.info(this, "Tipo: " + (this.sgTipo != null ? this.sgTipo.getNombre() : null));
        try {
            List<SgTipoTipoEspecifico> lt = sgTipoTipoEspecificoImpl.getSgTipoTipoEspecificoBySgTipo(getSgTipo().getId().intValue(), false, false, false);
            for (SgTipoTipoEspecifico sgTipoTipoEspecifico : lt) {
                SelectItem item = new SelectItem(sgTipoTipoEspecifico.getSgTipoEspecifico().getId(), sgTipoTipoEspecifico.getSgTipoEspecifico().getNombre());
                l.add(item);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepción en VehiculoBeanModel.getSgTipoEspecificoBySgTipoSelectItem()");
        }
        return l;
    }

    public List<SelectItem> traerUsuarioActivo(String cadena) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (UsuarioVO p : getListaUsuarioVo()) {
            if (p.getNombre() != null) {
                String cadenaPersona = p.getNombre().toLowerCase();
                cadena = cadena.toLowerCase();
                if (cadenaPersona.startsWith(cadena)) {
                    SelectItem item = new SelectItem(p, p.getNombre());
                    list.add(item);
                }
            }
        }
        return list;
    }

    public List<UsuarioVO> getListaUsuarioVo() {
        if (listaUsuarioVo == null) {
            UtilLog4j.log.fatal(this, "Aqui");
            listaUsuarioVo = usuarioImpl.usuarioActio(-1);
            UtilLog4j.log.fatal(this, "despues de aquí");
        }
        return listaUsuarioVo;
    }

    /**
     * @return the lisLicenciaVo
     */
    public LicenciaVo getLicenciaVo() {
        return licenciaVo;
    }

    /**
     * @param licenciaVo the licenciaVo to set
     */
    public void setLicenciaVo(LicenciaVo licenciaVo) {
        this.licenciaVo = licenciaVo;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the sgTipo
     */
    public SgTipo getSgTipo() {
        return sgTipo;
    }

    /**
     * @param sgTipo the sgTipo to set
     */
    public void setSgTipo(SgTipo sgTipo) {
        this.sgTipo = sgTipo;
    }

    /**
     * @return the lista
     */
    public List<LicenciaVo> getLista() {
        return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(List<LicenciaVo> lista) {
        this.lista = lista;
    }

    /**
     * @return the idPais
     */
    public int getIdPais() {
        return idPais;
    }

    /**
     * @param idPais the idPais to set
     */
    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
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
     * @return the sgLicencia
     */
    public SgLicencia getSgLicencia() {
        return sgLicencia;
    }

    /**
     * @param sgLicencia the sgLicencia to set
     */
    public void setSgLicencia(SgLicencia sgLicencia) {
        this.sgLicencia = sgLicencia;
    }

    /**
     * @return the popUp
     */
    public boolean isPopUp() {
        return popUp;
    }

    /**
     * @param popUp the popUp to set
     */
    public void setPopUp(boolean popUp) {
        this.popUp = popUp;
    }

    /**
     * @return the subirArchivoPop
     */
    public boolean isSubirArchivoPop() {
        return subirArchivoPop;
    }

    /**
     * @param subirArchivoPop the subirArchivoPop to set
     */
    public void setSubirArchivoPop(boolean subirArchivoPop) {
        this.subirArchivoPop = subirArchivoPop;
    }

    /**
     * @return the modificarPopUp
     */
    public boolean isModificarPopUp() {
        return modificarPopUp;
    }

    /**
     * @param modificarPopUp the modificarPopUp to set
     */
    public void setModificarPopUp(boolean modificarPopUp) {
        this.modificarPopUp = modificarPopUp;
    }

    /**
     * @return the numeroDias
     */
    public int getNumeroDias() {
        return numeroDias;
    }

    /**
     * @param numeroDias the numeroDias to set
     */
    public void setNumeroDias(int numeroDias) {
        this.numeroDias = numeroDias;
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * @return the listaUsuario
     */
    public List<SelectItem> getListaUsuario() {
        return listaUsuario;
    }

    /**
     * @param listaUsuario the listaUsuario to set
     */
    public void setListaUsuario(List<SelectItem> listaUsuario) {
        this.listaUsuario = listaUsuario;
    }

    /**
     * @return the crearPop
     */
    public boolean isCrearPop() {
        return crearPop;
    }

    /**
     * @param crearPop the crearPop to set
     */
    public void setCrearPop(boolean crearPop) {
        this.crearPop = crearPop;
    }

    public void buscarLicenciasByFiltros() {
        String complemento = "";
        if (!filtro.equals("todo")) {
            if (filtro.equals("vigentes")) {
                complemento = " and l.vigente = true";
            } else if (filtro.equals("vencidas")) {
                complemento = " and l.vigente = false";
            } else {
                String d1 = Constantes.FMT_yyyy_MM_dd.format(new Date()) + "";
                String d2 = Constantes.FMT_yyyy_MM_dd.format(siManejoFechaLocal.fechaSumarMes(new Date(), 2)) + "";
                complemento = " and l.fecha_vencimiento BETWEEN '" + d1
                        + "' :: Date AND '" + d2 + "' :: Date";
            }
        }

        if (getFechaVencimientoInicio() != null && !filtro.equals("PorVencer")) {
            if (getFechaVencimientoFin() != null) {
                setLista(sgLicenciaImpl.traerLicienciaByFechas(complemento, getFechaVencimientoInicio(),
                        getFechaVencimientoFin(), sesion.getUsuario().getApCampo().getId()));
            } else {
                setLista(sgLicenciaImpl.traerLicienciaByfechaMayor(complemento, getFechaVencimientoInicio(), sesion.getUsuario().getApCampo().getId()));
            }
        } else if (getFechaVencimientoFin() != null && !filtro.equals("PorVencer")) {
            setLista(sgLicenciaImpl.traerLicienciaByfechaMenor(complemento, getFechaVencimientoFin(), sesion.getUsuario().getApCampo().getId()));
        } else {
            setLista(sgLicenciaImpl.traerLiciencia(sesion.getUsuario().getApCampo().getId(), complemento));
        }

    }

    /**
     * @return the listaPaises
     */
    public List<SelectItem> getListaPaises() {
        return listaPaises;
    }

    /**
     * @param listaPaises the listaPaises to set
     */
    public void setListaPaises(List<SelectItem> listaPaises) {
        this.listaPaises = listaPaises;
    }

    /**
     * @return the listaTipoEspecifico
     */
    public List<SelectItem> getListaTipoEspecifico() {
        return listaTipoEspecifico;
    }

    /**
     * @param listaTipoEspecifico the listaTipoEspecifico to set
     */
    public void setListaTipoEspecifico(List<SelectItem> listaTipoEspecifico) {
        this.listaTipoEspecifico = listaTipoEspecifico;
    }

    public void crearNewLicencia() {
        LicenciaVo vo = new LicenciaVo();
        String name = FacesUtils.getRequestParameter("completarEmpleado");
        String numero = FacesUtils.getRequestParameter("numeroLicenciaNew");

        if (name != null && !name.isEmpty()) {

            if (getIdPais() > 0) {

                if (numero != null && !numero.isEmpty()) {

                    if (getIdTipoEspecifico() > 0) {

                        if (getFechaInicio() != null) {

                            if (getFechaFin() != null) {
                                vo.setNombre(name);
                                vo.setIdPais(getIdPais());
                                vo.setNumero(numero);
                                vo.setIdTipo(getIdTipoEspecifico());
                                vo.setExpedida(getFechaInicio());
                                vo.setVencimiento(getFechaFin());

                                sgLicenciaImpl.guardarLicencia(sesion.getUsuario(),
                                        vo, name, getIdTipoEspecifico(), getSgTipo(), getIdPais());
                                goToControlLicencias();
                                PrimeFaces.current().executeScript(";datosAddLicencia();");
                                setIdPais(-1);
                                setIdTipoEspecifico(-1);
                                setFechaInicio(new Date());
                                setFechaFin(new Date());
                            } else {
                                FacesUtils.addErrorMessage("Es necesario agregar la fecha de Vencimiento");
                            }
                        } else {
                            FacesUtils.addErrorMessage("Es necesario agregar la fecha de expedición");
                        }
                    } else {
                        FacesUtils.addErrorMessage("Es neceario seleccionar el tipo de licencia");
                    }
                } else {
                    FacesUtils.addErrorMessage("Es necesario agregar el numero de licencia");
                }
            } else {
                FacesUtils.addErrorMessage("Es necesario seleccionar un país");
            }
        } else {
            FacesUtils.addErrorMessage("Es necesario agregar un usuario");
        }
    }

    public void cargarListaUsuarios() {

        List<Object[]> listOb = usuarioImpl.traerUsuarioActivosJson(Constantes.CERO);

        JsonArray a = new JsonArray();
        Gson gson = new Gson();
        if (listOb != null) {
            for (Object[] o : listOb) {

                JsonObject ob = new JsonObject();
                ob.addProperty("value", (o[0] != null ? (String) o[0].toString() : "-"));
                ob.addProperty("label", o[1] != null ? o[1].toString() : "-");
                //ob.addProperty("type","-");
                a.add(ob);
            }
        }
        String usuarios = gson.toJson(a);
        PrimeFaces.current().executeScript(";limpiarDataListUsuario();");
        PrimeFaces.current().executeScript(";cargarUsuarioCurso(" + usuarios + ",'usuariosList');");
    }

    public void agregarLicencia() {
        setLicenciaVo(new LicenciaVo());
        licenciaVo.setId(Constantes.CERO);
        PrimeFaces.current().executeScript(";$(editLicenciaModal).modal('show');");

    }

    public void modLicencia(LicenciaVo liVo) {
        setLicenciaVo(new LicenciaVo());
        licenciaVo = liVo;
        setIdPais(licenciaVo.getIdPais());
        setIdTipoEspecifico(licenciaVo.getIdTipo());
        PrimeFaces.current().executeScript(";$(editLicenciaModal).modal('show');");

    }

    public void terminarEdicionLicencia(String numLicencia) {
        if (getIdPais() > 0) {
            if (getIdTipoEspecifico() > 0) {

                if (!getLicenciaVo().getNumero().isEmpty()) {

                    if (numLicencia != null && !numLicencia.isEmpty()) {

                        if (getLicenciaVo().getVencimiento() != null) {
                            getLicenciaVo().setNumero(numLicencia);
                            sgLicenciaImpl.guardarLicencia(usuario, licenciaVo, user, idTipoEspecifico, sgTipo, idPais);
                            setNumeroDias(0);
                            setIdTipoEspecifico(-1);
                            setIdPais(-1);
                            setModificarPopUp(false);
                            setLicenciaVo(null);
                            PrimeFaces.current().executeScript(";$(editLicenciaModal).modal('hide');");
                            //          goToControlLicencias();
                        } else {
                            FacesUtils.addInfoMessage("Es neceario agregar la fecha de vencimiento");
                        }
                    } else {
                        FacesUtils.addInfoMessage("Es neceario agregar la fecha de expedición");
                    }
                } else {
                    FacesUtils.addInfoMessage("Es neceario agregar el número");
                }
            } else {
                FacesUtils.addInfoMessage("Es necesario seleccionar el tipo");
            }
        } else {
            FacesUtils.addInfoMessage("El pais es requerido");
        }
    }

    public void addlicencia(LicenciaVo liVo) {
        setLicenciaVo(new LicenciaVo());
        if (liVo != null) {
            licenciaVo = liVo;
        }
        PrimeFaces.current().executeScript(";$(addArchivoModal).modal('show');");

    }

    public void enviarNotificacionLicencia() {

        if (!getLista().isEmpty()) {
            for (LicenciaVo lic : getLista()) {
                if (lic.isSelect()) {
                    notificacionServiciosGeneralesImpl.enviarAvisoNotificacionVencimientoLicenciaByUser(lic, Constantes.FALSE);
                }
                FacesUtils.addInfoMessage("Se notificaron las licencias seleccionadas");
            }
        } else {
            FacesUtils.addErrorMessage("no se encotro alguna licencia");
        }

    }

    /**
     * @return the selectTodos
     */
    public boolean isSelectTodos() {
        return selectTodos;
    }

    /**
     * @param selectTodos the selectTodos to set
     */
    public void setSelectTodos(boolean selectTodos) {
        this.selectTodos = selectTodos;
    }

    public void seleccionarTodo() {
        for (int i = 0; i < getLista().size(); i++) {
            getLista().get(i).setSelect(selectTodos);
        }

    }

    /**
     * @return the fechaInicio
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * @return the fechaFin
     */
    public Date getFechaFin() {
        return fechaFin;
    }

    /**
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * @return the fechaVencimientoInicio
     */
    public Date getFechaVencimientoInicio() {
        return fechaVencimientoInicio;
    }

    /**
     * @param fechaVencimientoInicio the fechaVencimientoInicio to set
     */
    public void setFechaVencimientoInicio(Date fechaVencimientoInicio) {
        this.fechaVencimientoInicio = fechaVencimientoInicio;
    }

    /**
     * @return the fechaVencimientoFin
     */
    public Date getFechaVencimientoFin() {
        return fechaVencimientoFin;
    }

    /**
     * @param fechaVencimientoFin the fechaVencimientoFin to set
     */
    public void setFechaVencimientoFin(Date fechaVencimientoFin) {
        this.fechaVencimientoFin = fechaVencimientoFin;
    }
}
