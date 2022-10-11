/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.accesorio.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.excepciones.SIAException;
import sia.modelo.sgl.accesorio.AccesorioAsignadoVo;
import sia.modelo.sgl.accesorio.AccesorioVo;
import sia.modelo.sgl.accesorio.LineaVo;
import sia.sgl.accesorio.bean.model.AccesorioBeanModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author mluis
 */
@Named(value = "accesorioBean_old")
@RequestScoped
public class AccesorioBean implements Serializable {

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    /**
     * Creates a new instance of AccesorioBean
     */
    /**
     * Creates a new instance of oficinaBean
     */
    @ManagedProperty(value = "#{accesorioBeanModel}")
    private AccesorioBeanModel accesorioBeanModel;

    public AccesorioBean() {
    }
    @Getter
    @Setter
    private UploadedFile fileInfo;

    public void agregarAccesorio(ActionEvent event) {
        accesorioBeanModel.setAccesorioVo(new AccesorioVo());
        accesorioBeanModel.getAccesorioVo().setFechaAdquisicion(new Date());
        llenarJson();
    }

    public void traerAccesorioPorTipo(ValueChangeEvent valueChangeEvent) {
        accesorioBeanModel.getAccesorioVo().setIdMarca((Integer) valueChangeEvent.getNewValue());
        accesorioBeanModel.traerAccesorioTipo();
    }

    public List<SelectItem> getListaMarca() {
        return accesorioBeanModel.listaMarca();
    }

    public void cambiarMarca(ValueChangeEvent event) {
        accesorioBeanModel.getAccesorioVo().setIdMarca(Integer.parseInt(event.getNewValue().toString()));
    }

    public void agragarMarca(ActionEvent event) {
        accesorioBeanModel.getAccesorioVo().setIdMarca(accesorioBeanModel.agregarMarca());

        PrimeFaces.current().executeScript(";$(dialogoAgregarMarca).modal('hide');");
    }

    public List<SelectItem> getListaModelo() {
        return accesorioBeanModel.listaModelo();
    }

    public List<SelectItem> getListaCondicion() {
        return accesorioBeanModel.listaCondicion();
    }

    public void guardarAccesorio(ActionEvent event) {
        accesorioBeanModel.guardarAccesorio();
        accesorioBeanModel.traerAccesorioTipo();
        PrimeFaces.current().executeScript(";$(dialogoAgregarAccesorio).modal('hide');");
    }

    public void guardarModelo(ActionEvent event) {
        try {
            accesorioBeanModel.getAccesorioVo().setIdModelo(accesorioBeanModel.guardarModelo());
            PrimeFaces.current().executeScript(";$(dialogoAgregarModelo).modal('hide');");
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    public void llenarJson() {
        String jsonProveedores = accesorioBeanModel.traerProveedorJson();
        PrimeFaces.current().executeScript(";setJson(" + jsonProveedores + ");");
    }

    public DataModel getTraerAccesorio() {
        try {
            return accesorioBeanModel.getLista();
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            return null;
        }
    }

    public String seleccionarAccesorio() {
        accesorioBeanModel.setIdAccesorio(Integer.parseInt(FacesUtils.getRequestParameter("idAccesorio")));
        accesorioBeanModel.llearAccesorio();
        accesorioBeanModel.asignado();
        //accesorioBeanModel.setLista(null);
        return "/vistas/sgl/accesorio/administrarAccesorios";
    }

    public void eliminarAccesorio(ActionEvent event) {
        accesorioBeanModel.setIdAccesorio(Integer.parseInt(FacesUtils.getRequestParameter("idAccesorio")));
        accesorioBeanModel.eliminarAccesorio();
    }

    public List getListaTipoEspecifico() {
        return accesorioBeanModel.listaTipoEspecifico();
    }

    public void traerUsuarioJson() {
        String datos = accesorioBeanModel.traerUsuarioJson();
        PrimeFaces.current().executeScript(";llenarJsonUsuario(" + datos + ");");
    }

    ///
    public void asignarAccesorio(ActionEvent event) {
        accesorioBeanModel.setAccesorioAsignadoVo(new AccesorioAsignadoVo());
        traerUsuarioJson();
        PrimeFaces.current().executeScript(";$(dialogoAsignarAccesorio).modal('show');");
    }

    public void completarAsignarAccesorio(ActionEvent event) {
        accesorioBeanModel.asignarAccesorio();
        accesorioBeanModel.accesorioNoDisponible();
        accesorioBeanModel.asignado();
        PrimeFaces.current().executeScript(";$(dialogoAsignarAccesorio).modal('hide');");

    }

    public void cancelarAsignarAccesorio(ActionEvent event) {
        accesorioBeanModel.setAccesorioAsignadoVo(null);
    }

    public void quitarCarta(ActionEvent event) {
        accesorioBeanModel.eliminarCarta();
        accesorioBeanModel.asignado();
    }

    public void modificarAsignacionAccesorio(ActionEvent event) {
        if (accesorioBeanModel.getAccesorioAsignadoVo().getFechaAsignacion() == null) {
            FacesUtils.addInfoMessage("Es necesario agregar una fecha");
        } else if (accesorioBeanModel.getAccesorioVo().getIdCondicion() < 1) {
            FacesUtils.addInfoMessage("Es necesario seleccionar la condición");
        } else {
            //accesorioBeanModel.modificarAsignacionAccesorio();
            //accesorioBeanModel.setModificarPop(false);
            ///accesorioBeanModel.setSgAsignarAccesorio(null);
        }
    }

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
                documentoAnexo.setRuta(getDir());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                validate = accesorioBeanModel.guardarCartaAsigna(
                        documentoAnexo.getNombreBase(),
                        documentoAnexo.getRuta(),
                        documentoAnexo.getTipoMime(),
                        documentoAnexo.getTamanio()
                );

                accesorioBeanModel.asignado();

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

    public String getDir() {
        return accesorioBeanModel.dir();
    }

    public void quitarAsignacion(ActionEvent event) {
        accesorioBeanModel.elimimarAsignacion();
        accesorioBeanModel.asignado();
        accesorioBeanModel.accesorioDisponible();
    }

    public void recibirAccesorio(ActionEvent event) {
        accesorioBeanModel.ponerTerminadaAsignacion();
        accesorioBeanModel.accesorioDisponible();
    }
///

    public void agregarLinea(ActionEvent event) {
        accesorioBeanModel.getAccesorioVo().setLineaVo(new LineaVo());
        PrimeFaces.current().executeScript(";$(dialogoAgregarLinea).modal('show');");
    }

    public List<SelectItem> getListaEstado() {
        if (accesorioBeanModel.getAccesorioVo().getLineaVo() != null) {
            return accesorioBeanModel.listaEstado();
        }
        return null;
    }

    public void completarAgregarLinea(ActionEvent event) {
        accesorioBeanModel.completarAgregarLinea();
        PrimeFaces.current().executeScript(";$(dialogoAgregarLinea).modal('hide');");
        accesorioBeanModel.asignado();
    }

    public void completarModificarLinea(ActionEvent event) {
        accesorioBeanModel.completarModificarLinea();
        PrimeFaces.current().executeScript(";$(dialogoModificarLinea).modal('hide');");
        accesorioBeanModel.asignado();
    }

    public void cancelarAgregarLinea(ActionEvent event) {
        accesorioBeanModel.getAccesorioVo().setLineaVo(null);
    }

    public void modificarAccesorio(ActionEvent event) {
        accesorioBeanModel.modificarAccesorio();
        accesorioBeanModel.asignado();
    }

    public void agregarAccesorioLinea(ActionEvent event) {
        if (accesorioBeanModel.getNumLinea() != null) {
            PrimeFaces.current().executeScript(";jsonLinea(" + accesorioBeanModel.getNumLinea() + ");");
            PrimeFaces.current().executeScript(";$(dialogoRelacionLinea).modal('show');");
        } else {
            PrimeFaces.current().executeScript(";alert('No se cargaron las lineas, por favor cierre la ventana e ingrese nuevamente a la opción.');");
        }

    }

    public void completarAccesorioLinea(ActionEvent event) {
        accesorioBeanModel.agregarAccesorioLinea();
        accesorioBeanModel.llearAccesorio();
        PrimeFaces.current().executeScript(";$(dialogoRelacionLinea).modal('hide');");
    }

    /**
     * ***********************************
     * INICIO********************************************
     */
    /**
     *
     * @param event
     */
    public void buscarAsignaciones(ActionEvent event) {
        accesorioBeanModel.setListaAsignacion(accesorioBeanModel.buscarAsignaciones());
    }

    /**
     * ************************************ FIN
     * *******************************************
     */
    /**
     * @return the accesorioVo
     */
    public AccesorioVo getAccesorioVo() {
        return accesorioBeanModel.getAccesorioVo();
    }

    /**
     * @param accesorioVo the accesorioVo to set
     */
    public void setAccesorioVo(AccesorioVo accesorioVo) {
        accesorioBeanModel.setAccesorioVo(accesorioVo);
    }

    /**
     * @return the idTipo
     */
    public int getIdTipo() {
        return accesorioBeanModel.getIdTipo();
    }

    /**
     * @param idTipo the idTipo to set
     */
    public void setIdTipo(int idTipo) {
        accesorioBeanModel.setIdTipo(idTipo);
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return accesorioBeanModel.getTipo();
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        accesorioBeanModel.setTipo(tipo);
    }

    /**
     * @return the accesorioRecibido
     */
    public AccesorioAsignadoVo getAccesorioRecibido() {
        return accesorioBeanModel.getAccesorioRecibido();
    }

    /**
     * @param accesorioRecibido the accesorioAsignadoVo to set
     */
    public void setAccesorioRecibido(AccesorioAsignadoVo accesorioRecibido) {
        accesorioBeanModel.setAccesorioRecibido(accesorioRecibido);
    }

    /**
     * @return the accesorioAsignadoVo
     */
    public AccesorioAsignadoVo getAccesorioAsignadoVo() {
        return accesorioBeanModel.getAccesorioAsignadoVo();
    }

    /**
     * @param accesorioAsignadoVo the accesorioAsignadoVo to set
     */
    public void setAccesorioAsignadoVo(AccesorioAsignadoVo accesorioAsignadoVo) {
        accesorioBeanModel.setAccesorioAsignadoVo(accesorioAsignadoVo);
    }

    /**
     * @return the idUsuario
     */
    public String getIdUsuario() {
        return accesorioBeanModel.getIdUsuario();
    }

    /**
     * @param idUsuario the idUsuario to set
     */
    public void setIdUsuario(String idUsuario) {
        accesorioBeanModel.setIdUsuario(idUsuario);
    }

    /**
     * @return the subirArchivo
     */
    public boolean isSubirArchivo() {
        return accesorioBeanModel.isSubirArchivo();
    }

    /**
     * @param subirArchivo the subirArchivo to set
     */
    public void setSubirArchivo(boolean subirArchivo) {
        accesorioBeanModel.setSubirArchivo(subirArchivo);
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
        return accesorioBeanModel.getLista();
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
        accesorioBeanModel.setLista(lista);
    }

    /**
     * @param accesorioBeanModel the accesorioBeanModel to set
     */
    public void setAccesorioBeanModel(AccesorioBeanModel accesorioBeanModel) {
        this.accesorioBeanModel = accesorioBeanModel;
    }

    /**
     * @return the idAccesorio
     */
    public int getIdAccesorio() {
        return accesorioBeanModel.getIdAccesorio();
    }

    /**
     * @param idAccesorio the idAccesorio to set
     */
    public void setIdAccesorio(int idAccesorio) {
        accesorioBeanModel.setIdAccesorio(idAccesorio);
    }

    /**
     * @return the idLinea
     */
    public int getIdLinea() {
        return accesorioBeanModel.getIdLinea();
    }

    /**
     * @param idLinea the idLinea to set
     */
    public void setIdLinea(int idLinea) {
        accesorioBeanModel.setIdLinea(idLinea);
    }

    /**
     * @return the listaAsignacion
     */
    public List getListaAsignacion() {
        return accesorioBeanModel.getListaAsignacion();
    }

    /**
     * @param listaAsignacion the listaAsignacion to set
     */
    public void setListaAsignacion(List listaAsignacion) {
        accesorioBeanModel.setListaAsignacion(listaAsignacion);
    }
}
