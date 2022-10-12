/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;




import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.file.UploadedFile;
import sia.administracion.bean.model.PresupuestoAdminBeanModel;
import sia.modelo.OcActividadPetrolera;
import sia.modelo.OcCodigoSubtarea;
import sia.modelo.OcCodigoTarea;
import sia.modelo.OcNombreTarea;
import sia.modelo.OcUnidadCosto;
import sia.modelo.ProyectoOt;
import sia.modelo.SiAdjunto;
import sia.modelo.presupuesto.vo.PresupuestoDetVO;
import sia.modelo.presupuesto.vo.PresupuestoVO;
import sia.modelo.requisicion.vo.OcActividadVO;
import sia.modelo.requisicion.vo.OcTareaVo;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@Named(value = "presupuestoAdminBean")
@ViewScoped
public class PresupuestoAdminBean implements Serializable {

    @Inject
    private PresupuestoAdminBeanModel presupuestoAdminBeanModel;

    @Inject
    private Sesion sesion;
    private UploadedFile fileUpload;

    public PresupuestoAdminBean() {
    }

    @PostConstruct
    public void llenaCampo() {
        this.presupuestoAdminBeanModel.inicia();
    }

    /**
     * @param presupuestoAdminBeanModel the presupuestoAdminBeanModel to set
     */
    public void setPresupuestoAdminBeanModel(PresupuestoAdminBeanModel presupuestoAdminBeanModel) {
        this.presupuestoAdminBeanModel = presupuestoAdminBeanModel;
    }

    /**
     * @return the presupuestos
     */
    public List<PresupuestoVO> getPresupuestos() {
        return this.presupuestoAdminBeanModel.getPresupuestos();
    }

    /**
     * @param presupuestos the presupuestos to set
     */
    public void setPresupuestos(List<PresupuestoVO> presupuestos) {
        this.presupuestoAdminBeanModel.setPresupuestos(presupuestos);
    }

    public void seleccionarPresupuesto() {
        int idPresupuesto = Integer.parseInt(FacesUtils.getRequestParameter("idPresupuesto"));
        if (idPresupuesto > 0) {
            this.presupuestoAdminBeanModel.setIdPres(idPresupuesto);
            this.presupuestoAdminBeanModel.iniciarPresupuesto();
            PrimeFaces.current().executeScript(";muestraPresupuesto('divDatos', 'divTabla', 'divOperacion', 'divAutoriza');");
        }
    }

    public void seleccionarImportar() {
        int idPresupuesto = Integer.parseInt(FacesUtils.getRequestParameter("idPresupuesto"));
        if (idPresupuesto > 0) {
            this.presupuestoAdminBeanModel.setIdPres(idPresupuesto);
            this.presupuestoAdminBeanModel.iniciarImportar();
            PrimeFaces.current().executeScript(";muestraPresupuesto('divImportar', 'divTabla', 'divOpImportar', 'divAutoriza');");
        }
    }

    public void borrarPresupuesto() {
        int idPresupuesto = Integer.parseInt(FacesUtils.getRequestParameter("idPresupuesto"));
        if (idPresupuesto > 0) {
            this.presupuestoAdminBeanModel.setIdPres(idPresupuesto);
            this.presupuestoAdminBeanModel.borrarPresupuesto();
            this.presupuestoAdminBeanModel.cargarPresupuestos();
            PrimeFaces.current().executeScript(";muestraPresupuesto('divDatos', 'divTabla', 'divOperacion', 'divAutoriza');");
        }
    }

    public void agrgarOTPartida() {
        int indice = Integer.parseInt(FacesUtils.getRequestParameter("indice"));
        if (indice > -1) {
//            this.presupuestoAdminBeanModel.setPartidaProy(this.getNuevasActsPbjs().get(indice));
            this.presupuestoAdminBeanModel.cargarOTspartidas();
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoPresAltaPart);");
        }
    }

    public void regresarPresupuestos() {
        regresarDat();
        PrimeFaces.current().executeScript(";muestraPresupuesto('divTabla', 'divDatos', 'divAutoriza', 'divOperacion');");
        PrimeFaces.current().executeScript(";muestraPresupuesto('', 'divImportar', '', 'divOpImportar');");
    }

    private void regresarDat() {
        this.presupuestoAdminBeanModel.setIdPres(0);
        this.presupuestoAdminBeanModel.limpiarPresupuesto();
        this.presupuestoAdminBeanModel.iniciarCargaPresupuesto();
        this.presupuestoAdminBeanModel.cargarPresupuestos();
    }

    public void nuevoPresupuestos() {
        this.presupuestoAdminBeanModel.setIdPres(0);
        this.presupuestoAdminBeanModel.limpiarPresupuesto();
        this.presupuestoAdminBeanModel.setPresVO(new PresupuestoVO());
        this.presupuestoAdminBeanModel.cargarMonedas();
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoNuevoPres);");
    }

    public void modificarMonto() {
        this.presupuestoAdminBeanModel.cargarPartida();
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoPresModMonto);");
    }

    public void agregarProyOT() {
        this.presupuestoAdminBeanModel.iniciarProyectosOTs();
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoPresProyOT);");
    }

    public void agregarPartidaPres() {
        this.presupuestoAdminBeanModel.iniciarAltaPartida();
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoPresAltaPart);");
    }

    public void guardarMonto() {
        this.presupuestoAdminBeanModel.guardarPartida();
        this.presupuestoAdminBeanModel.cargarPresupuestoDet();
        FacesUtils.addInfoMessage("El monto fue modificado exitosamente.");
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoPresModMonto);");
    }

    public void guardarPresupuesto() {
        if (getPresVO() != null
                && getPresVO().getNombre() != null && !getPresVO().getNombre().isEmpty()
                && getPresVO().getCodigo() != null && !getPresVO().getCodigo().isEmpty()
                && getPresVO().getIdMoneda() > 0) {
            this.presupuestoAdminBeanModel.guardarPresupuesto();
            this.presupuestoAdminBeanModel.cargarPresupuestos();
            FacesUtils.addInfoMessage("El nuevo presupuesto fue registrado exitosamente.");
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoNuevoPres);");
        }
    }

    public void abrirGuardarNuevoActP() {
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoNuevoActP);");
    }

    public void guardarNuevoActP() {
        if (this.getNuevosActividadP().size() > 0) {
            this.presupuestoAdminBeanModel.guardarNuevosActP();
            if (this.getNuevosActividadP().size() > 0) {
                FacesUtils.addErrorMessage("Ocurrio un error al guardar la nueva actividad petrolera, por favor valide el código y el nombre de la actividad.");
            }
        }
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoNuevoActP);");
        validarRecargaPres();
    }

    private void validarRecargaPres() {
        if (this.getNuevosActividadP().size() < 1
                && this.getNuevosCodigosTarea().size() < 1
                && this.getNuevosNombresTarea().size() < 1
                && this.getNuevosProyectosOts().size() < 1
                && this.getNuevosSubActividadP().size() < 1
                && this.getNuevosSubTareas().size() < 1) {
            regresarDat();
            FacesUtils.addInfoMessage("Se guardaron los catalogos correctamente.");
            PrimeFaces.current().executeScript(";muestraPresupuesto('divTabla', 'divDatos', 'divAutoriza', 'divOperacion');");
            PrimeFaces.current().executeScript(";muestraPresupuesto('', 'divImportar', '', 'divOpImportar');");
        }
    }

    public void abrirGuardarNuevosCodigoTarea() {
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoNuevoCodigoTarea);");
    }

    public void guardarNuevosCodigoTarea() {
        if (this.getNuevosCodigosTarea().size() > 0) {
            this.presupuestoAdminBeanModel.guardarNuevosCodigosTarea();
            if (this.getNuevosCodigosTarea().size() > 0) {
                FacesUtils.addErrorMessage("Ocurrio un error al guardar el nuevo código de tarea, por favor valide el texto del código.");
            }
        }
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoNuevoCodigoTarea);");
        validarRecargaPres();
    }

    public void abrirGuardarNuevosNombreTarea() {
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoNuevoNombreTarea);");
    }

    public void guardarNuevosNombreTarea() {
        if (this.getNuevosNombresTarea().size() > 0) {
            this.presupuestoAdminBeanModel.guardarNuevosNombreTarea();
            if (this.getNuevosNombresTarea().size() > 0) {
                FacesUtils.addErrorMessage("Ocurrio un error al guardar el nuevo nombre de tarea, por favor valide el texto del nombre.");
            }
        }
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoNuevoNombreTarea);");
        validarRecargaPres();
    }

    public void abrirGuardarNuevosProyectoOT() {
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoNuevoProyOT);");
    }

    public void guardarNuevosProyectoOT() {
        if (this.getNuevosProyectosOts().size() > 0) {
            this.presupuestoAdminBeanModel.guardarNuevosProyectosOt();
            if (this.getNuevosProyectosOts().size() > 0) {
                FacesUtils.addErrorMessage("Ocurrio un error al guardar el nuevo proyecto ot, por favor valide el código y nombre del proyecto ot.");
            }
        }
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoNuevoProyOT);");
        validarRecargaPres();
    }

    public void abrirGuardarNuevoSubActP() {
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoNuevoSubActividad);");
    }

    public void guardarNuevoSubActP() {
        if (this.getNuevosSubActividadP().size() > 0) {
            this.presupuestoAdminBeanModel.guardarNuevosSubActivP();
            if (this.getNuevosSubActividadP().size() > 0) {
                FacesUtils.addErrorMessage("Ocurrio un error al guardar la nueva Subactividad, por favor valide el código y el nombre de la Subactividad.");
            }
        }
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoNuevoSubActividad);");
        validarRecargaPres();
    }

    public void abrirGuardarNuevoSubtarea() {
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoNuevoSubTarea);");
    }

    public void guardarNuevoSubtarea() {
        if (this.getNuevosSubTareas().size() > 0) {
            this.presupuestoAdminBeanModel.guardarNuevosSubTarea();
            if (this.getNuevosSubTareas().size() > 0) {
                FacesUtils.addErrorMessage("Ocurrio un error al guardar la nueva Subtarea, por favor valide el código y el nombre de la Subtarea.");
            }
        }
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoNuevoSubTarea);");
        validarRecargaPres();
    }

    public void guardarProyOT() {
        this.presupuestoAdminBeanModel.guardarProyOT();
        this.presupuestoAdminBeanModel.cargarPresupuestoDet();
        FacesUtils.addInfoMessage("El proyecto OT fue registrado correctamente en el presupuesto seleccionado.");
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoPresProyOT);");
    }

    public void agregarProyOTPartida() {
        this.presupuestoAdminBeanModel.agregarProyOTPartida();
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoPresAltaPart);");
    }

    public void refrescarContratoMes(ValueChangeEvent event) {
        int newMes = (int) event.getNewValue();
        if (newMes > 0) {
            this.presupuestoAdminBeanModel.setMes(newMes);
            this.presupuestoAdminBeanModel.cargarPresupuestoDet();
        }
    }

    public void cambiarAnio(AjaxBehaviorEvent event) {
        this.presupuestoAdminBeanModel.cambiarAnio();
        this.presupuestoAdminBeanModel.cargarPresupuestoDet();
    }

    public void refrescarContratoDet(AjaxBehaviorEvent event) {
        this.presupuestoAdminBeanModel.cargarPresupuestoDet();
    }

    public void refrescarSubact(AjaxBehaviorEvent event) {
        this.presupuestoAdminBeanModel.refrescarSubact();
    }

    public void refrescarProyectosOTs(AjaxBehaviorEvent event) {
        this.presupuestoAdminBeanModel.refrescarProyectosOTs();
    }

    public void refrescarTarea(AjaxBehaviorEvent event) {
        this.presupuestoAdminBeanModel.refrescarTarea();
    }

    public void refrescarSubtarea(AjaxBehaviorEvent event) {
        this.presupuestoAdminBeanModel.refrescarSubtarea();
    }

    public void refrescarTipos(AjaxBehaviorEvent event) {
        this.presupuestoAdminBeanModel.refrescarTipos();
    }

    public void refrescarMontos(AjaxBehaviorEvent event) {
        this.presupuestoAdminBeanModel.refrescarMontos();
    }

    public void refrescarContratoAnio(ValueChangeEvent event) {
        int newAnio = (int) event.getNewValue();
        if (newAnio > 0) {
            this.presupuestoAdminBeanModel.setAnio(newAnio);
            this.presupuestoAdminBeanModel.cargarPresupuestoDet();
        }
    }

    /**
     * @return the sesion
     */
    public Sesion getSesion() {
        return sesion;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the detalle
     */
    public List<PresupuestoDetVO> getDetalle() {
        return this.presupuestoAdminBeanModel.getDetalle();
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(List<PresupuestoDetVO> detalle) {
        this.presupuestoAdminBeanModel.setDetalle(detalle);
    }

    /**
     * @return the anios
     */
    public List<SelectItem> getAnios() {
        return this.presupuestoAdminBeanModel.getAnios();
    }

    /**
     * @param anios the anios to set
     */
    public void setAnios(List<SelectItem> anios) {
        this.presupuestoAdminBeanModel.setAnios(anios);
    }

    /**
     * @return the meses
     */
    public List<SelectItem> getMeses() {
        return this.presupuestoAdminBeanModel.getMeses();
    }

    /**
     * @param meses the meses to set
     */
    public void setMeses(List<SelectItem> meses) {
        this.presupuestoAdminBeanModel.setMeses(meses);
    }

    /**
     * @return the anio
     */
    public int getAnio() {
        return this.presupuestoAdminBeanModel.getAnio();
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.presupuestoAdminBeanModel.setAnio(anio);
    }

    /**
     * @return the mes
     */
    public int getMes() {
        return this.presupuestoAdminBeanModel.getMes();
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this.presupuestoAdminBeanModel.setMes(mes);
    }

    /**
     * @return the presVO
     */
    public PresupuestoVO getPresVO() {
        return this.presupuestoAdminBeanModel.getPresVO();
    }

    /**
     * @param presVO the presVO to set
     */
    public void setPresVO(PresupuestoVO presVO) {
        this.presupuestoAdminBeanModel.setPresVO(presVO);
    }

    /**
     * @return the idPres
     */
    public int getIdPres() {
        return this.presupuestoAdminBeanModel.getIdPres();
    }

    /**
     * @param idPres the idPres to set
     */
    public void setIdPres(int idPres) {
        this.presupuestoAdminBeanModel.setIdPres(idPres);
    }

    /**
     * @return the lstUnidadCosto
     */
    public List<SelectItem> getLstUnidadCosto() {
        return this.presupuestoAdminBeanModel.getLstUnidadCosto();
    }

    /**
     * @param lstUnidadCosto the lstUnidadCosto to set
     */
    public void setLstUnidadCosto(List<SelectItem> lstUnidadCosto) {
        this.presupuestoAdminBeanModel.setLstUnidadCosto(lstUnidadCosto);
    }

    /**
     * @return the lstActividad
     */
    public List<SelectItem> getLstActividad() {
        return this.presupuestoAdminBeanModel.getLstActividad();
    }

    /**
     * @param lstActividad the lstActividad to set
     */
    public void setLstActividad(List<SelectItem> lstActividad) {
        this.presupuestoAdminBeanModel.setLstActividad(lstActividad);
    }

    /**
     * @return the lstTarea
     */
    public List<OcTareaVo> getLstTarea() {
        return this.presupuestoAdminBeanModel.getLstTarea();
    }

    /**
     * @param lstTarea the lstTarea to set
     */
    public void setLstTarea(List<OcTareaVo> lstTarea) {
        this.presupuestoAdminBeanModel.setLstTarea(lstTarea);
    }

    /**
     * @return the lstSubTarea
     */
    public List<SelectItem> getLstSubTarea() {
        return this.presupuestoAdminBeanModel.getLstSubTarea();
    }

    /**
     * @param lstSubTarea the lstSubTarea to set
     */
    public void setLstSubTarea(List<SelectItem> lstSubTarea) {
        this.presupuestoAdminBeanModel.setLstSubTarea(lstSubTarea);

    }

    /**
     * @return the partidaModificar
     */
    public PresupuestoDetVO getPartidaModificar() {
        return this.presupuestoAdminBeanModel.getPartidaModificar();
    }

    /**
     * @param partidaModificar the partidaModificar to set
     */
    public void setPartidaModificar(PresupuestoDetVO partidaModificar) {
        this.presupuestoAdminBeanModel.setPartidaModificar(partidaModificar);
    }

    /**
     * @return the lstTipos
     */
    public List<SelectItem> getLstTipos() {
        return this.presupuestoAdminBeanModel.getLstTipos();
    }

    /**
     * @param lstTipos the lstTipos to set
     */
    public void setLstTipos(List<SelectItem> lstTipos) {
        this.presupuestoAdminBeanModel.setLstTipos(lstTipos);
    }

    /**
     * @return the tipo
     */
    public int getTipo() {
        return this.presupuestoAdminBeanModel.getTipo();
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(int tipo) {
        this.presupuestoAdminBeanModel.setTipo(tipo);
    }

    /**
     * @return the lstOldProyectosOTs
     */
    public List<SelectItem> getLstOldProyectosOTs() {
        return this.presupuestoAdminBeanModel.getLstOldProyectosOTs();
    }

    /**
     * @param lstOldProyectosOTs the lstOldProyectosOTs to set
     */
    public void setLstOldProyectosOTs(List<SelectItem> lstOldProyectosOTs) {
        this.presupuestoAdminBeanModel.setLstOldProyectosOTs(lstOldProyectosOTs);
    }

    /**
     * @return the lstNewProyectosOTs
     */
    public List<SelectItem> getLstNewProyectosOTs() {
        return this.presupuestoAdminBeanModel.getLstNewProyectosOTs();
    }

    /**
     * @param lstNewProyectosOTs the lstNewProyectosOTs to set
     */
    public void setLstNewProyectosOTs(List<SelectItem> lstNewProyectosOTs) {
        this.presupuestoAdminBeanModel.setLstNewProyectosOTs(lstNewProyectosOTs);
    }

    /**
     * @return the idNewProy
     */
    public int getIdNewProy() {
        return this.presupuestoAdminBeanModel.getIdNewProy();
    }

    /**
     * @param idNewProy the idNewProy to set
     */
    public void setIdNewProy(int idNewProy) {
        this.presupuestoAdminBeanModel.setIdNewProy(idNewProy);
    }

    /**
     * @return the idActPetro
     */
    public int getIdActPetro() {
        return this.presupuestoAdminBeanModel.getIdActPetro();
    }

    /**
     * @param idActPetro the idActPetro to set
     */
    public void setIdActPetro(int idActPetro) {
        this.presupuestoAdminBeanModel.setIdActPetro(idActPetro);
    }

    public void guardarArchivoPresupuesto(UploadedFile fileInfo) {
        this.presupuestoAdminBeanModel.guardarArchivoPresupuesto(fileInfo);
    }

    public void uploadFile(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        boolean completo = true;
        try {
            SiAdjunto adj;
            try {
                File fileTmp = new File("/tpm/" + file.getFileName());
                try (FileOutputStream outputStream = new FileOutputStream(fileTmp)) {
                    outputStream.write(file.getContent());
                }
                guardarArchivoPresupuesto(file);
                completo = this.presupuestoAdminBeanModel.cargarPresupuestoFile(fileTmp);
                if (!completo) {
                    FacesUtils.addErrorMessage("El ID del presupuesto por cargar no coincide con el presupuesto seleccionado.");
                }
            } catch (Exception ex) {
                UtilLog4j.log.error(ex);
            }
            file.delete();

            if (completo && this.getNuevosActividadP().size() < 1
                    && this.getNuevosCodigosTarea().size() < 1
                    && this.getNuevosNombresTarea().size() < 1
                    && this.getNuevosProyectosOts().size() < 1
                    && this.getNuevosSubActividadP().size() < 1
                    && this.getNuevosSubTareas().size() < 1) {
                FacesUtils.addInfoMessage("Se cargo el archivo del presupuesto temporalmente para poder ser confirmado.");

            } else if (completo) {
                if (this.getNuevosActividadP().size() > 0) {
//                    FacesUtils.addErrorMessage("Se detectaron nuevas actividades petroleras no registradas al cargar el archivo del presupuesto.");
                }
                if (this.getNuevosCodigosTarea().size() > 0) {
//                    FacesUtils.addErrorMessage("Se detectaron nuevos códigos de tareas no registrados al cargar el archivo del presupuesto.");
                }
                if (this.getNuevosNombresTarea().size() > 0) {
//                    FacesUtils.addErrorMessage("Se detectaron nuevos nombres de tareas no registrados al cargar el archivo del presupuesto.");
                }
                if (this.getNuevosProyectosOts().size() > 0) {
//                    FacesUtils.addErrorMessage("Se detectaron nuevos proyectos ots no registradas al cargar el archivo del presupuesto.");
                }
                if (this.getNuevosSubActividadP().size() > 0) {
//                    FacesUtils.addErrorMessage("Se detectaron nuevas subactividades petroleras no registradas al cargar el archivo del presupuesto.");
                }
                if (this.getNuevosSubTareas().size() > 0) {
//                    FacesUtils.addErrorMessage("Se detectaron nuevs subtareas no registradas al cargar el archivo del presupuesto.");
                }
            }
            PrimeFaces.current().executeScript(";quitarAareaBlockCSS();");
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            FacesUtils.addErrorMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }

    }

    /**
     * @return the lstPartidas
     */
    public List<PresupuestoDetVO> getLstPartidas() {
        return this.presupuestoAdminBeanModel.getLstPartidas();
    }

    /**
     * @param lstPartidas the lstPartidas to set
     */
    public void setLstPartidas(List<PresupuestoDetVO> lstPartidas) {
        this.presupuestoAdminBeanModel.setLstPartidas(lstPartidas);
    }

//    /**
//     * @return the nuevasActs
//     */
//    public HashMap<String, OcActividadVO> getNuevasActs() {
//        return this.presupuestoAdminBeanModel.getNuevasActs();
//    }
//
//    /**
//     * @param nuevasActs the nuevasActs to set
//     */
//    public void setNuevasActs(HashMap<String, OcActividadVO> nuevasActs) {
//        this.presupuestoAdminBeanModel.setNuevasActs(nuevasActs);
//    }
//    
//   /**
//     * @return the nuevasActs
//     */
//    public List<OcActividadVO> getNuevasActsPbjs() {
//        return new ArrayList<>(this.presupuestoAdminBeanModel.getNuevasActs().values());
//    }
    /**
     * @return the partidaProy
     */
    public OcActividadVO getPartidaProy() {
        return this.presupuestoAdminBeanModel.getPartidaProy();
    }

    /**
     * @param partidaProy the partidaProy to set
     */
    public void setPartidaProy(OcActividadVO partidaProy) {
        this.presupuestoAdminBeanModel.setPartidaProy(partidaProy);
    }

    /**
     * @return the monedas
     */
    public List<SelectItem> getMonedas() {
        return this.presupuestoAdminBeanModel.getMonedas();
    }

    /**
     * @param monedas the monedas to set
     */
    public void setMonedas(List<SelectItem> monedas) {
        this.presupuestoAdminBeanModel.setMonedas(monedas);
    }

    /**
     * @return the partidaDisplay
     */
    public PresupuestoDetVO getPartidaDisplay() {
        return this.presupuestoAdminBeanModel.getPartidaDisplay();
    }

    /**
     * @param partidaDisplay the partidaDisplay to set
     */
    public void setPartidaDisplay(PresupuestoDetVO partidaDisplay) {
        this.presupuestoAdminBeanModel.setPartidaDisplay(partidaDisplay);
    }

    public void displayPartida(SelectEvent event) {
        this.setPartidaDisplay((PresupuestoDetVO) event.getObject());
        if (this.getPartidaDisplay() != null
                && this.getPartidaDisplay().isExistePresupuesto()
                && this.getPartidaDisplay().getMontos().size() > 0) {
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoDisplayPartida);");
        }

    }

    public void guardarPresupuestoDet() {
        if (this.presupuestoAdminBeanModel.guardarPresupuestoDet()) {
            this.presupuestoAdminBeanModel.setIdPres(0);
            this.presupuestoAdminBeanModel.limpiarPresupuesto();
            this.presupuestoAdminBeanModel.inicia();
            PrimeFaces.current().executeScript(";muestraPresupuesto('divTabla', 'divDatos', 'divAutoriza', 'divOperacion');");
            PrimeFaces.current().executeScript(";muestraPresupuesto('', 'divImportar', '', 'divOpImportar');");
        } else {
            FacesUtils.addErrorMessage("Ocurrió un problema al guardar el presupuesto, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
    }

    /**
     * @return the nuevosCodigosTarea
     */
    public List<OcCodigoTarea> getNuevosCodigosTarea() {
        return this.presupuestoAdminBeanModel.getNuevosCodigosTarea();
    }

    /**
     * @param nuevosCodigosTarea the nuevosCodigosTarea to set
     */
    public void setNuevosCodigosTarea(List<OcCodigoTarea> nuevosCodigosTarea) {
        this.presupuestoAdminBeanModel.setNuevosCodigosTarea(nuevosCodigosTarea);
    }

    /**
     * @return the nuevosNombresTarea
     */
    public List<OcNombreTarea> getNuevosNombresTarea() {
        return this.presupuestoAdminBeanModel.getNuevosNombresTarea();
    }

    /**
     * @param nuevosNombresTarea the nuevosNombresTarea to set
     */
    public void setNuevosNombresTarea(List<OcNombreTarea> nuevosNombresTarea) {
        this.presupuestoAdminBeanModel.setNuevosNombresTarea(nuevosNombresTarea);
    }

    /**
     * @return the nuevosProyectosOts
     */
    public List<ProyectoOt> getNuevosProyectosOts() {
        return this.presupuestoAdminBeanModel.getNuevosProyectosOts();
    }

    /**
     * @param nuevosProyectosOts the nuevosProyectosOts to set
     */
    public void setNuevosProyectosOts(List<ProyectoOt> nuevosProyectosOts) {
        this.presupuestoAdminBeanModel.setNuevosProyectosOts(nuevosProyectosOts);
    }

    /**
     * @return the nuevosSubTareas
     */
    public List<OcCodigoSubtarea> getNuevosSubTareas() {
        return this.presupuestoAdminBeanModel.getNuevosSubTareas();
    }

    /**
     * @param nuevosSubTareas the nuevosSubTareas to set
     */
    public void setNuevosSubTareas(List<OcCodigoSubtarea> nuevosSubTareas) {
        this.presupuestoAdminBeanModel.setNuevosSubTareas(nuevosSubTareas);
    }

    /**
     * @return the nuevosSubActividadP
     */
    public List<OcUnidadCosto> getNuevosSubActividadP() {
        return this.presupuestoAdminBeanModel.getNuevosSubActividadP();
    }

    /**
     * @param nuevosSubActividadP the nuevosSubActividadP to set
     */
    public void setNuevosSubActividadP(List<OcUnidadCosto> nuevosSubActividadP) {
        this.presupuestoAdminBeanModel.setNuevosSubActividadP(nuevosSubActividadP);
    }

    /**
     * @return the nuevosActividadP
     */
    public List<OcActividadPetrolera> getNuevosActividadP() {
        return this.presupuestoAdminBeanModel.getNuevosActividadP();
    }

    /**
     * @param nuevosActividadP the nuevosActividadP to set
     */
    public void setNuevosActividadP(List<OcActividadPetrolera> nuevosActividadP) {
        this.presupuestoAdminBeanModel.setNuevosActividadP(nuevosActividadP);
    }

}
