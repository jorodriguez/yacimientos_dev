/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.vehiculo.bean.backing;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
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
import sia.modelo.SgLicencia;
import sia.modelo.SgTipo;
import sia.modelo.Usuario;
import sia.modelo.licencia.vo.LicenciaVo;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.vehiculo.bean.model.ControlLicenciaModel;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author ihsa
 */
@Named(value = "controlLicenciaBean_old")
@RequestScoped
public class ControlLicenciaBean implements Serializable {

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Getter
    @Setter
    private UploadedFile fileInfo;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    /**
     * Creates a new instance of controlLicenciaBean
     */
    public ControlLicenciaBean() {
    }

    @ManagedProperty(value = "#{controlLicenciaModel}")
    private ControlLicenciaModel controlLicenciaModel;

    // *****************************************************INICIO DE LICENCIAS ****************************************
    public void nuevaLicencia(ActionEvent event) {
        controlLicenciaModel.buscarTipoGeneral();
        controlLicenciaModel.setCrearPop(true);
        controlLicenciaModel.setLicenciaVo(new LicenciaVo());
        controlLicenciaModel.setMensaje("");
    }

    /**
     * @return the listaTipoEspecifico
     */
    public List<SelectItem> getListaTipoEspecifico() {
        return controlLicenciaModel.getListaTipoEspecifico();
    }

    /**
     * @param listaTipoEspecifico the listaTipoEspecifico to set
     */
    public void setListaTipoEspecifico(List<SelectItem> listaTipoEspecifico) {
        controlLicenciaModel.setListaTipoEspecifico(listaTipoEspecifico);
    }

    public List<SelectItem> getSgTipoEspecificoBySgTipoSelectItem() {
        try {
            if (controlLicenciaModel.getSgTipo() != null) {
                return controlLicenciaModel.getSgTipoEspecificoBySgTipoSelectItem();
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepción dentro de traer tipo especifico");
            return null;
        }
        return null;
    }

    /**
     * @return the listaPaises
     */
    public List<SelectItem> getListaPaises() {
        return controlLicenciaModel.getListaPaises();
    }

    /**
     * @param listaPaises the listaPaises to set
     */
    public void setListaPaises(List<SelectItem> listaPaises) {
        controlLicenciaModel.setListaPaises(listaPaises);
    }

    public void usuarioListenerLiciencia(String event) {
        controlLicenciaModel.setListaUsuario(controlLicenciaModel.traerUsuarioActivo(event));
        //listaUsuariosAlta = soporteProveedor.regresaUsuarioActivoVO(event.getNewValue().toString());
    }

    public void buscarUsuario(ActionEvent event) {
        if (controlLicenciaModel.getUser().trim().isEmpty() || controlLicenciaModel.getUser() == null
                || controlLicenciaModel.buscarEmpledoPorNombre() == null) {
            controlLicenciaModel.setMensaje("No se encontró el usario en el SIA, para el registro favor de enviar la "
                    + "solicitud a sia@ihsa.mx, para asignar, licencia, vehiculos, accesorios es necesario registrar al usuario.");
            controlLicenciaModel.setUsuario(null);
        } else {
            controlLicenciaModel.setLista(controlLicenciaModel.traerLiciencia());
            if (controlLicenciaModel.getLista().size() > 0) {
                if (controlLicenciaModel.buscarLiecinciaVigente() != null) {
                    controlLicenciaModel.setNumeroDias(controlLicenciaModel.licenciaPorVencer());
                    controlLicenciaModel.setMensaje("");
                    if (controlLicenciaModel.getNumeroDias() < 15 && controlLicenciaModel.getNumeroDias() > 0) {
                        controlLicenciaModel.setMensaje("La licencia esta por vencer");
                    } else if (controlLicenciaModel.getNumeroDias() < 1) {
                        controlLicenciaModel.setMensaje("La liciencia esta vencida");
                    }
                } else {
                    controlLicenciaModel.setMensaje("Este usuario no tiene licencia vigente");
                }
            } else {
                controlLicenciaModel.setMensaje("No se han registrado liciencias para el usuario seleccionado");
            }
            //controlLicenciaModel.traerLiciencia();
        }
    }

    public void completarRegistroLicencia(ActionEvent event) {
        //Busca la licincia vigente dek usuario
        if (controlLicenciaModel.getIdPais() > 0) {
            if (!controlLicenciaModel.getLicenciaVo().getNumero().isEmpty()) {
                if (controlLicenciaModel.getIdTipoEspecifico() > 0) {
                    if (controlLicenciaModel.getLicenciaVo().getExpedida() != null) {
                        if (controlLicenciaModel.getLicenciaVo().getVencimiento() != null) {
                            if (controlLicenciaModel.traerLiciencia().size() > 0) {
                                for (Iterator it = controlLicenciaModel.traerLiciencia().iterator(); it.hasNext();) {
                                    LicenciaVo shSgL = (LicenciaVo) it.next();
                                    controlLicenciaModel.quitarLicenciaVigente(shSgL);
                                }
                                controlLicenciaModel.guardarLicencia();
                                controlLicenciaModel.setUser("");
                                controlLicenciaModel.setCrearPop(false);
                                controlLicenciaModel.setLicenciaVo(null);
                            } else {
                                //Crea la nueva
                                controlLicenciaModel.guardarLicencia();
                                controlLicenciaModel.setUser("");
                                controlLicenciaModel.setCrearPop(false);
                                controlLicenciaModel.setLicenciaVo(null);
                                controlLicenciaModel.setUsuario(null);
                            }
                        } else {
                            FacesUtils.addInfoMessage("Es neceario agregar la fecha de vencimiento");
                        }
                    } else {
                        FacesUtils.addInfoMessage("Es neceario agregar la fecha de expedición");
                    }
                } else {
                    FacesUtils.addInfoMessage("Es necesario seleccionar el tipo");
                }
            } else {
                FacesUtils.addInfoMessage("Es neceario agregar el número");
            }

        } else {
            FacesUtils.addInfoMessage("El pais es requerido");
        }
    }

    public String getDirLicencia() {
        if (controlLicenciaModel.getLicenciaVo() != null) {
            return controlLicenciaModel.dirLicencia();
        } else {
            return "";
        }
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

                valid = controlLicenciaModel.guardarArchivo(
                        documentoAnexo.getNombreBase(),
                        documentoAnexo.getRuta(),
                        documentoAnexo.getTipoMime(),
                        documentoAnexo.getTamanio()
                );
                controlLicenciaModel.setLicenciaVo(null);
                controlLicenciaModel.goToControlLicencias();

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

        PrimeFaces.current().executeScript(";$('#addArchivoModal').modal('hide');");
    }

    public void validaFecha(FacesContext context, UIComponent validate, Object value) {
        Date f = (Date) value;
        Date d = new Date();
        if (f.compareTo(d) < 0) {
            ((UIInput) validate).setValid(false);
            FacesMessage msg = new FacesMessage("Elija una fecha posterior a hoy");
            context.addMessage(validate.getClientId(context), msg);
        }
    }

    @Deprecated
    public void modificacionLicencia(ActionEvent event) {
        //controlLicenciaModel.setLicenciaVo((LicenciaVo) controlLicenciaModel.getLista().getRowData());
        controlLicenciaModel.setIdTipoEspecifico(controlLicenciaModel.getLicenciaVo().getIdTipo());
        controlLicenciaModel.setIdPais(controlLicenciaModel.getLicenciaVo().getIdPais());
        controlLicenciaModel.buscarTipoGeneral();
        controlLicenciaModel.setModificarPopUp(true);
    }

    public void modLicencia() {
//        controlLicenciaModel.ModLicencia();
    }

    public void eliminarLicencia() {
        controlLicenciaModel.eliminarLicencia();
        if (controlLicenciaModel.getLista().size() == 0) {
            controlLicenciaModel.setUsuario(null);
            controlLicenciaModel.setUser("");
        }
        controlLicenciaModel.setLicenciaVo(null);
    }

    public void abrirPopSubirArchivo(ActionEvent event) {
        //controlLicenciaModel.setLicenciaVo((LicenciaVo) controlLicenciaModel.getLista().getRowData());
        controlLicenciaModel.setSubirArchivoPop(true);
    }

    public void quitarArchivo() {
//	controlLicenciaModel.setLicenciaVo((LicenciaVo) controlLicenciaModel.getLista().getRowData());
//        controlLicenciaModel.quitarArchivo();
        controlLicenciaModel.setLicenciaVo(null);
    }

    public void completarModificacionLicencia() {
  //      controlLicenciaModel.terminarEdicionLicencia();
    }

    public void cerrarPop(ActionEvent event) {
        controlLicenciaModel.setUser("");
        controlLicenciaModel.setLicenciaVo(null);
        controlLicenciaModel.setIdTipoEspecifico(-1);
        controlLicenciaModel.setModificarPopUp(false);
        controlLicenciaModel.setSubirArchivoPop(false);
        controlLicenciaModel.setCrearPop(false);
    }

    public void uploadFile() {
        UtilLog4j.log.info(this, "upload");
    }

    /**
     * @return the modificarPopUp
     */
    public boolean isModificarPopUp() {
        return controlLicenciaModel.isModificarPopUp();
    }

    /**
     * @param modificarPopUp the modificarPopUp to set
     */
    public void setModificarPopUp(boolean modificarPopUp) {
        controlLicenciaModel.setModificarPopUp(modificarPopUp);
    }

    /**
     * @return the subirArchivoPop
     */
    public boolean isSubirArchivoPop() {
        return controlLicenciaModel.isSubirArchivoPop();
    }

    /**
     * @param subirArchivoPop the subirArchivoPop to set
     */
    public void setSubirArchivoPop(boolean subirArchivoPop) {
        controlLicenciaModel.setSubirArchivoPop(subirArchivoPop);
    }

    /**
     * @param controlLicenciaModel the controlLicenciaModel to set
     */
    public void setControlLicenciaModel(ControlLicenciaModel controlLicenciaModel) {
        this.controlLicenciaModel = controlLicenciaModel;
    }

    /**
     * @return the lisLicenciaVo
     */
    public LicenciaVo getLicenciaVo() {
        return controlLicenciaModel.getLicenciaVo();
    }

    /**
     * @param licenciaVo the LicenciaVo to set
     */
    public void setLicenciaVo(LicenciaVo licenciaVo) {
        controlLicenciaModel.setLicenciaVo(licenciaVo);
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return controlLicenciaModel.getUsuario();
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        controlLicenciaModel.setUsuario(usuario);
    }

    /**
     * @return the sgTipo
     */
    public SgTipo getSgTipo() {
        return controlLicenciaModel.getSgTipo();
    }

    /**
     * @param sgTipo the sgTipo to set
     */
    public void setSgTipo(SgTipo sgTipo) {
        controlLicenciaModel.setSgTipo(sgTipo);
    }

    /**
     * @return the lista
     */
    public List<LicenciaVo> getLista() {
        return controlLicenciaModel.getLista();
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(List<LicenciaVo> lista) {
        controlLicenciaModel.setLista(lista);
    }

    /**
     * @return the idPais
     */
    public int getIdPais() {
        return controlLicenciaModel.getIdPais();
    }

    /**
     * @param idPais the idPais to set
     */
    public void setIdPais(int idPais) {
        controlLicenciaModel.setIdPais(idPais);
    }

    /**
     * @return the user
     */
    public String getUser() {
        return controlLicenciaModel.getUser();
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        controlLicenciaModel.setUser(user);
    }

    /**
     * @return the idTipoEspecifico
     */
    public int getIdTipoEspecifico() {
        return controlLicenciaModel.getIdTipoEspecifico();
    }

    /**
     * @param idTipoEspecifico the idTipoEspecifico to set
     */
    public void setIdTipoEspecifico(int idTipoEspecifico) {
        controlLicenciaModel.setIdTipoEspecifico(idTipoEspecifico);
    }

    /**
     * @return the sgLicencia
     */
    public SgLicencia getSgLicencia() {
        return controlLicenciaModel.getSgLicencia();
    }

    /**
     * @param sgLicencia the sgLicencia to set
     */
    public void setSgLicencia(SgLicencia sgLicencia) {
        controlLicenciaModel.setSgLicencia(sgLicencia);
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return controlLicenciaModel.getMensaje();
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        controlLicenciaModel.setMensaje(mensaje);
    }

    public List<SelectItem> getListaUsuario() {
        return controlLicenciaModel.getListaUsuario();
    }

    public void setListaUsuario(List<SelectItem> lista) {
        controlLicenciaModel.setListaUsuario(lista);
    }

    /**
     * @return the crearPop
     */
    public boolean isCrearPop() {
        return controlLicenciaModel.isCrearPop();
    }

    /**
     * @param crearPop the crearPop to set
     */
    public void setCrearPop(boolean crearPop) {
        controlLicenciaModel.setCrearPop(crearPop);
    }

    /**
     * @return the fechaVencimientoInicio
     */
    public Date getFechaVencimientoInicio() {
        return controlLicenciaModel.getFechaVencimientoInicio();
    }

    /**
     * @param fechaVencimientoInicio the fechaVencimientoInicio to set
     */
    public void setFechaVencimientoInicio(Date fechaVencimientoInicio) {
        controlLicenciaModel.setFechaVencimientoInicio(fechaVencimientoInicio);
    }

    /**
     * @return the fechaVencimientoFin
     */
    public Date getFechaVencimientoFin() {
        return controlLicenciaModel.getFechaVencimientoFin();
    }

    /**
     * @param fechaVencimientoFin the fechaVencimientoFin to set
     */
    public void setFechaVencimientoFin(Date fechaVencimientoFin) {
        controlLicenciaModel.setFechaVencimientoFin(fechaVencimientoFin);
    }

    public void buscarLicenciasByFiltros() {
        controlLicenciaModel.buscarLicenciasByFiltros();
    }

    /**
     * @return the fechaInicio
     */
    public Date getFechaInicio() {
        return controlLicenciaModel.getFechaInicio();
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
        controlLicenciaModel.setFechaInicio(fechaInicio);
    }

    /**
     * @return the fechaFin
     */
    public Date getFechaFin() {
        return controlLicenciaModel.getFechaFin();
    }

    /**
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(Date fechaFin) {
        controlLicenciaModel.setFechaFin(fechaFin);
    }

    public void crearNewLicencia() {
        controlLicenciaModel.crearNewLicencia();
    }

    public void adjuntarlicencia() {
  //      controlLicenciaModel.addlicencia();
    }

    /**
     * @return the selectTodos
     */
    public boolean isSelectTodos() {
        return controlLicenciaModel.isSelectTodos();
    }

    /**
     * @param selectTodos the selectTodos to set
     */
    public void setSelectTodos(boolean selectTodos) {
        controlLicenciaModel.setSelectTodos(selectTodos);
    }

    public void seleccionTodo(ValueChangeEvent e) {
        boolean select = ((Boolean) e.getNewValue());
       // controlLicenciaModel.seleccionarTodo(select);
    }

    public void notificarLicencia() {
        controlLicenciaModel.enviarNotificacionLicencia();
    }
}
