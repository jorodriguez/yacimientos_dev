/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.accesorio.bean.model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;

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
import sia.modelo.SiAdjunto;
import sia.modelo.SiCondicion;
import sia.modelo.sgl.accesorio.AccesorioAsignadoVo;
import sia.modelo.sgl.accesorio.AccesorioVo;
import sia.modelo.sgl.viaje.vo.TipoEspecificoVo;
import sia.modelo.sgl.vo.Vo;
import sia.modelo.vo.StatusVO;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sgl.accesorio.impl.SgAccesorioImpl;
import sia.servicios.sgl.accesorio.impl.SgAsignarAccesorioImpl;
import sia.servicios.sgl.accesorio.impl.SgLineaImpl;
import sia.servicios.sgl.impl.SgMarcaImpl;
import sia.servicios.sgl.impl.SgModeloImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiCondicionImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author mluis
 */
@Named(value = "accesorioBean")
@ViewScoped
public class AccesorioBeanModel implements Serializable {

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    @Inject
    private Sesion sesion;

    public AccesorioBeanModel() {
        accesorioVo = new AccesorioVo();
    }

    @PostConstruct
    public void iniciar() {
        setIdTipo(12);
        traerUsuarioJson();
        traerAccesorioTipo();
    }
    //Inject
    @Inject
    private SgMarcaImpl sgMarcaImpl;
    @Inject
    private SgModeloImpl sgModeloImpl;
    @Inject
    private SgAccesorioImpl sgAccesorioImpl;
    @Inject
    private SgTipoTipoEspecificoImpl sgTipoTipoEspecificoImpl;
    @Inject
    private SiCondicionImpl siCondicionImpl;
    @Inject
    private SiParametroImpl siParametroImpl;
    @Inject
    private SgAsignarAccesorioImpl sgAsignarAccesorioImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private SgLineaImpl sgLineaImpl;
    @Inject
    private EstatusImpl estatusImpl;
    //ENtidad
    private AccesorioVo accesorioVo;
    private int idAccesorio;
    private int idTipo;
    private String tipo;
    private AccesorioAsignadoVo accesorioRecibido;
    private AccesorioAsignadoVo accesorioAsignadoVo;
    private String idUsuario;
    //VAriables enteras
    //boleean
    private boolean subirArchivo = false;
    //Lista
    private DataModel lista;
    private List listaAsignacion;
    private int idLinea;
    private String numLinea;
    @Getter
    @Setter
    private UploadedFile fileInfo;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
 public void subirCartaGenerada(FileUploadEvent fileEvent) throws Exception {
        boolean validate = false;
        fileInfo = fileEvent.getFile();

        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        AlmacenDocumentos almacenDocumentos
                = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

        if (addArchivo) {

            try {

                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                documentoAnexo.setRuta(dir());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                validate = guardarCartaAsigna(
                        documentoAnexo.getNombreBase(),
                        documentoAnexo.getRuta(),
                        documentoAnexo.getTipoMime(),
                        documentoAnexo.getTamanio()
                );

                asignado();

            } catch (IOException e) {
                LOGGER.error(e);
            } catch (SIAException e) {
                LOGGER.error(e);
            }

        } else {
            FacesUtils.addErrorMessage(new StringBuilder()
                    .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                    .append(validadorNombreArchivo.getCaracteresNoValidos())
                    .toString());
        }

        if (!validate) {
            FacesUtils.addInfoMessage("Ocurrio una excepción, favor de comunicar a sia@ihsa.mx");
        }

        fileInfo.delete();
    }

    public List<SelectItem> listaMarca() {
        List<Vo> lc;
        try {
            List<SelectItem> l = new ArrayList<SelectItem>();
            lc = sgMarcaImpl.traerMarcaPorTipo(getIdTipo());
            for (Vo st : lc) {
                SelectItem item = new SelectItem(st.getId(), st.getNombre());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            return null;
        }
    }

    public List<SelectItem> listaEstado() {
        List<StatusVO> lc;
        try {
            List<SelectItem> l = new ArrayList<SelectItem>();
            lc = estatusImpl.traerPorTipo(Constantes.ESTATUS_COMPROBANTE_LIN);
            for (StatusVO st : lc) {
                SelectItem item = new SelectItem(st.getIdStatus(), st.getNombre());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            System.out.println("E . " + e);
            UtilLog4j.log.fatal(e);
            return null;
        }
    }

    public void traerAccesorioTipo() {
        try {
            setLista(new ListDataModel(sgAccesorioImpl.traerAccesorioPorTipo(getIdTipo(), sesion.getOficinaActual().getId())));
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);

        }
    }

    public List<SelectItem> listaModelo() {
        List<Vo> lc;
        try {
            List<SelectItem> l = new ArrayList<SelectItem>();
            lc = sgModeloImpl.traerModeloPorTipo(getAccesorioVo().getIdTipoEspecifico(), getAccesorioVo().getIdMarca());
            for (Vo st : lc) {
                SelectItem item = new SelectItem(st.getId(), st.getNombre());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            return null;
        }
    }

    public List<SelectItem> listaTipoEspecifico() {
        List<TipoEspecificoVo> lc;
        try {
            List<SelectItem> l = new ArrayList<SelectItem>();
            lc = sgTipoTipoEspecificoImpl.traerPorTipoEspecificoPorTipo(getIdTipo());
            for (TipoEspecificoVo st : lc) {
                SelectItem item = new SelectItem(st.getId(), st.getNombre());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            return null;
        }
    }

    public List<SelectItem> listaCondicion() {
        List<SiCondicion> lc;
        try {
            List<SelectItem> l = new ArrayList<SelectItem>();
            lc = siCondicionImpl.traerCondicion(Constantes.NO_ELIMINADO);
            for (SiCondicion st : lc) {
                SelectItem item = new SelectItem(st.getId(), st.getNombre());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            return null;
        }
    }

    public void llearAccesorio() {
        setAccesorioVo(sgAccesorioImpl.buscarPorId(getIdAccesorio()));
        setNumLinea(sgLineaImpl.traerLineaJson());
    }

    public void asignado() {
        setAccesorioAsignadoVo(sgAsignarAccesorioImpl.buscarAccesorioAsingado(getIdAccesorio()));
    }

    public void eliminarAccesorio() {
        sgAccesorioImpl.eliminarAccesorio(sesion.getUsuario().getId(), getIdAccesorio());
        traerAccesorioTipo();
    }

    public String traerProveedorJson() {
        //System.out.println("Empresa  : " + sesion.getRfcEmpresa());
        return proveedorImpl.traerJsonProveedorPorCompania(sesion.getRfcEmpresa(), ProveedorEnum.ACTIVO.getId());
    }

    public void guardarAccesorio() {
        sgAccesorioImpl.guardarAccesorio(sesion.getUsuario().getId(), accesorioVo.getIdMarca(),
                accesorioVo.getIdModelo(), getIdTipo(), getAccesorioVo().getIdTipoEspecifico(),
                accesorioVo, accesorioVo.isGarantia(), accesorioVo.getIdCondicion(),
                accesorioVo.getIdProveedor(), sesion.getOficinaActual().getId());
        setAccesorioVo(null);
    }

    public int guardarModelo() {
        try {
            PrimeFaces.current().executeScript(";$(dialogoAgregarModelo).modal('hide');");
            return sgModeloImpl.save(sesion.getUsuario().getId(), accesorioVo.getModelo(), accesorioVo.getIdMarca(), accesorioVo.getIdTipoEspecifico());

        } catch (Exception ex) {
            Logger.getLogger(AccesorioBeanModel.class.getName()).log(Level.SEVERE, null, ex);
            return Constantes.CERO;
        }
    }

    public int agregarMarca() {
        try {
            return sgMarcaImpl.save(sesion.getUsuario().getId(), accesorioVo.getMarca());
        } catch (Exception ex) {
            Logger.getLogger(AccesorioBeanModel.class.getName()).log(Level.SEVERE, null, ex);
            return Constantes.CERO;
        }
    }

    public String traerUsuarioJson() {
        return usuarioImpl.traerUsuarioActivoJson();
    }

    public void asignarAccesorio() {
        sgAsignarAccesorioImpl.guardarAsignarAccesorio(sesion.getUsuario().getId(), getIdAccesorio(),
                getAccesorioVo().getIdCondicion(), getIdUsuario(),
                getAccesorioAsignadoVo().getFechaAsignacion());
        //
        accesorioAsignadoVo = sgAsignarAccesorioImpl.buscarAccesorioAsingado(idAccesorio);
    }

    public void accesorioDisponible() {
        sgAccesorioImpl.accesorioDisponible(sesion.getUsuario().getId(), getAccesorioVo().getId(),
                Constantes.BOOLEAN_TRUE);
    }

    public void ponerTerminadaAsignacion() {
        sgAsignarAccesorioImpl.ponerTerminandaAsignacion(getAccesorioAsignadoVo().getId(), sesion.getUsuario().getId());
    }

    public void accesorioNoDisponible() {
        sgAccesorioImpl.accesorioDisponible(
                sesion.getUsuario().getId(),
                getAccesorioAsignadoVo().getAccesorioVo().getId(),
                Constantes.BOOLEAN_FALSE
        );
    }

    public void elimimarAsignacion() {
        sgAsignarAccesorioImpl.eliminarAsignacionAccesorio(
                sesion.getUsuario().getId(),
                getAccesorioAsignadoVo().getId()
        );
    }

    public String dir() {
        String retVal = Constantes.VACIO;

        try {
            if (getAccesorioAsignadoVo().getAdjuntoVO().getId() != null) {
                retVal = "SGyL/Accesorio/Carta/" + getAccesorioVo().getId() + "/";
            }
        } catch (RuntimeException e) {
            LOGGER.error(e);
        }

        return retVal;
    }

    public void eliminarCarta() {
        boolean v = false;
        //Se eliminan fisicamente los archivos
        String path = this.siParametroImpl.find(1).getUploadDirectory();
        try {
            File file = new File(path + getAccesorioAsignadoVo().getAdjuntoVO().getUrl());
            if (file != null) {
                if (file.exists()) {
                    file.delete();
                }
            }
            v = sgAsignarAccesorioImpl.eliminarCarta(sesion.getUsuario(), getAccesorioAsignadoVo().getId());
            if (v) {
                siAdjuntoImpl.eliminarArchivo(getAccesorioAsignadoVo().getAdjuntoVO().getId(), sesion.getUsuario().getId());
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
        }
    }

    public boolean guardarCartaAsigna(String fileName, String ruta, String contentType, long size) throws SIAException, Exception {
        boolean v = false;
        SiAdjunto siAdjunto
                = siAdjuntoImpl.save(
                        fileName,
                        ruta + fileName,
                        contentType,
                        size,
                        sesion.getUsuario().getId()
                );

        if (siAdjunto != null) {
            sgAsignarAccesorioImpl.subirCarta(sesion.getUsuario(), getAccesorioAsignadoVo().getId(), siAdjunto);
            v = true;
        }
        return v;
    }

    public void completarAgregarLinea() {
        accesorioVo.getLineaVo().setEmei(accesorioVo.getSerie());
        int linea = sgLineaImpl.guardar(sesion.getUsuario().getId(), accesorioVo.getLineaVo());
        //
    }

    public void completarModificarLinea() {
        accesorioVo.getLineaVo().setEmei(accesorioVo.getSerie());
        sgLineaImpl.modificar(sesion.getUsuario().getId(), accesorioVo.getLineaVo());
        //

    }

    public void modificarAccesorio() {
        sgAccesorioImpl.modificarAccesorio(sesion.getUsuario().getId(), accesorioVo);
    }

    public String traleLineaJson() {
        if (getNumLinea() == null) {
            setNumLinea(sgLineaImpl.traerLineaJson());
        }
        return getNumLinea();
    }

    public void agregarAccesorioLinea() {
        sgAccesorioImpl.agregarLineaAccesorio(sesion.getUsuario().getId(), accesorioVo.getId(), getIdLinea());

    }

    /**
     * *********************************** INICIO
     *
     * ******************************************** @return
     * @return
     */
    public List buscarAsignaciones() {
        return sgAsignarAccesorioImpl.traerAsignacion();
    }

    /**
     * ************************************ FIN
     * *******************************************
     */
    /////
    /**
     * @return the accesorioVo
     */
    public AccesorioVo getAccesorioVo() {
        return accesorioVo;
    }

    /**
     * @param accesorioVo the accesorioVo to set
     */
    public void setAccesorioVo(AccesorioVo accesorioVo) {
        this.accesorioVo = accesorioVo;
    }

    /**
     * @return the idTipo
     */
    public int getIdTipo() {
        return idTipo;
    }

    /**
     * @param idTipo the idTipo to set
     */
    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the accesorioRecibido
     */
    public AccesorioAsignadoVo getAccesorioRecibido() {
        return accesorioRecibido;
    }

    /**
     * @return the accesorioAsignadoVo
     */
    public AccesorioAsignadoVo getAccesorioAsignadoVo() {
        return accesorioAsignadoVo;
    }

    /**
     * @param accesorioAsignadoVo the accesorioAsignadoVo to set
     */
    public void setAccesorioAsignadoVo(AccesorioAsignadoVo accesorioAsignadoVo) {
        this.accesorioAsignadoVo = accesorioAsignadoVo;
    }

    /**
     * @return the idUsuario
     */
    public String getIdUsuario() {
        return idUsuario;
    }

    /**
     * @param idUsuario the idUsuario to set
     */
    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * @return the subirArchivo
     */
    public boolean isSubirArchivo() {
        return subirArchivo;
    }

    /**
     * @param subirArchivo the subirArchivo to set
     */
    public void setSubirArchivo(boolean subirArchivo) {
        this.subirArchivo = subirArchivo;
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
        return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
        this.lista = lista;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @param accesorioRecibido the accesorioRecibido to set
     */
    public void setAccesorioRecibido(AccesorioAsignadoVo accesorioRecibido) {
        this.accesorioRecibido = accesorioRecibido;
    }

    /**
     * @return the idAccesorio
     */
    public int getIdAccesorio() {
        return idAccesorio;
    }

    /**
     * @param idAccesorio the idAccesorio to set
     */
    public void setIdAccesorio(int idAccesorio) {
        this.idAccesorio = idAccesorio;
    }

    /**
     * @return the idLinea
     */
    public int getIdLinea() {
        return idLinea;
    }

    /**
     * @param idLinea the idLinea to set
     */
    public void setIdLinea(int idLinea) {
        this.idLinea = idLinea;
    }

    /**
     * @return the numLinea
     */
    public String getNumLinea() {
        return numLinea;
    }

    /**
     * @param numLinea the numLinea to set
     */
    public void setNumLinea(String numLinea) {
        this.numLinea = numLinea;
    }

    /**
     * @return the listaAsignacion
     */
    public List getListaAsignacion() {
        return listaAsignacion;
    }

    /**
     * @param listaAsignacion the listaAsignacion to set
     */
    public void setListaAsignacion(List listaAsignacion) {
        this.listaAsignacion = listaAsignacion;
    }

}
