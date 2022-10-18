/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.vehiculo.bean.backing;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ValueChangeEvent;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.modelo.cursoManejo.vo.CursoManejoVo;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.vehiculo.bean.model.CursoManejoBeanModel;

/**
 *
 * @author jevazquez
 */
@Named(value = "cursoManejoBean")
@RequestScoped
public class CursoManejoBean implements Serializable {

    @ManagedProperty(value = "#{cursoManejoBeanModel}")
    private CursoManejoBeanModel cursoManejoBeanModel;
    @Getter
    @Setter
    private UploadedFile fileInfo;

    public CursoManejoBean() {
    }

    public void inciar() {

    }

    /**
     * @return the usuariosConCurso
     */
    public List<CursoManejoVo> getUsuariosConCurso() {
        return getCursoManejoBeanModel().getUsuariosConCurso();
    }

    /**
     * @param usuariosConCurso the usuariosConCurso to set
     */
    public void setUsuariosConCurso(List<CursoManejoVo> usuariosConCurso) {
        getCursoManejoBeanModel().setUsuariosConCurso(usuariosConCurso);
    }

    public void cargarCursos() {
        //cursoManejoBeanModel.cargarListaCursoManejo();
    }

    /**
     * @return the cursoManejoBeanModel
     */
    public CursoManejoBeanModel getCursoManejoBeanModel() {
        return cursoManejoBeanModel;
    }

    /**
     * @param cursoManejoBeanModel the cursoManejoBeanModel to set
     */
    public void setCursoManejoBeanModel(CursoManejoBeanModel cursoManejoBeanModel) {
        this.cursoManejoBeanModel = cursoManejoBeanModel;
    }

    public void addnewUsuarioCurso() {

    }

    /**
     * @return the fechaInicio
     */
    public Date getFechaInicio() {
        return cursoManejoBeanModel.getFechaInicio();
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
        cursoManejoBeanModel.setFechaInicio(fechaInicio);
    }

    /**
     * @return the fechaFin
     */
    public Date getFechaFin() {
        return cursoManejoBeanModel.getFechaFin();
    }

    /**
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(Date fechaFin) {
        cursoManejoBeanModel.setFechaFin(fechaFin);
    }

    /**
     * @return the nombreUsuarioSeleccionado
     */
    public String getNombreUsuarioSeleccionado() {
        return cursoManejoBeanModel.getNombreUsuarioSeleccionado();
    }

    /**
     * @param nombreUsuarioSeleccionado the nombreUsuarioSeleccionado to set
     */
    public void setNombreUsuarioSeleccionado(String nombreUsuarioSeleccionado) {
        cursoManejoBeanModel.setNombreUsuarioSeleccionado(nombreUsuarioSeleccionado);
    }

    /**
     * @return the idCursoSeleccionado
     */
    public int getIdCursoSeleccionado() {
        return cursoManejoBeanModel.getIdCursoSeleccionado();
    }

    /**
     * @param idCursoSeleccionado the idCursoSeleccionado to set
     */
    public void setIdCursoSeleccionado(int idCursoSeleccionado) {
        cursoManejoBeanModel.setIdCursoSeleccionado(idCursoSeleccionado);
    }

    /**
     * @return the numeroCurso
     */
    public int getNumeroCurso() {
        return cursoManejoBeanModel.getNumeroCurso();
    }

    /**
     * @param numeroCurso the numeroCurso to set
     */
    public void setNumeroCurso(int numeroCurso) {
        cursoManejoBeanModel.setNumeroCurso(numeroCurso);
    }

    /**
     * @return the fechaVencimientoInicio
     */
    public Date getFechaVencimientoInicio() {
        return cursoManejoBeanModel.getFechaVencimientoInicio();
    }

    /**
     * @param fechaVencimientoInicio the fechaVencimientoInicio to set
     */
    public void setFechaVencimientoInicio(Date fechaVencimientoInicio) {
        cursoManejoBeanModel.setFechaVencimientoInicio(fechaVencimientoInicio);
    }

    /**
     * @return the fechaVencimientoFin
     */
    public Date getFechaVencimientoFin() {
        return cursoManejoBeanModel.getFechaVencimientoFin();
    }

    /**
     * @param fechaVencimientoFin the fechaVencimientoFin to set
     */
    public void setFechaVencimientoFin(Date fechaVencimientoFin) {
        cursoManejoBeanModel.setFechaVencimientoFin(fechaVencimientoFin);
    }

    public void buscarUsuarioCurso() {

        String usuario = FacesUtils.getRequestParameter("completarEmpleado");
        String idcurso = FacesUtils.getRequestParameter("idCursoM");

        getFechaInicio();
        getFechaFin();

        if (usuario != null && !usuario.isEmpty()) {

            cursoManejoBeanModel.cursoByUsuario();

        } else if (idcurso != null && !idcurso.isEmpty()) {

            cursoManejoBeanModel.cursoById();

        } else {
            cursoManejoBeanModel.cursoByFechaVencimiento();
        }

    }

    public void modUsuarioCurso() {
        cursoManejoBeanModel.editarCurso();
    }

    public void crearCurso() {
        cursoManejoBeanModel.crearCursoManejo();
    }

    public void finEditCurso() {
        cursoManejoBeanModel.finEditCurso();
    }

    public void buscarByFiltros() {
        cursoManejoBeanModel.buscarCursosByFiltros();

    }

    /**
     * @return the selectTodos
     */
    public boolean isSelectTodos() {
        return cursoManejoBeanModel.isSelectTodos();
    }

    /**
     * @param selectTodos the selectTodos to set
     */
    public void setSelectTodos(boolean selectTodos) {
        cursoManejoBeanModel.setSelectTodos(selectTodos);
    }

    public void seleccionTodo(ValueChangeEvent e) {
        boolean select = ((Boolean) e.getNewValue());
        cursoManejoBeanModel.seleccionarTodo(select);
    }

    public void notificarCursos() {
        cursoManejoBeanModel.enviarNotificacionCursoManejo();
    }

    public void leerArchivoAltaOrUpdateUsuario(FileUploadEvent fileEvent) {
        fileInfo = fileEvent.getFile();

        if (fileInfo.getFileName().endsWith(".xlsx") || fileInfo.getFileName().endsWith(".xls")) {
            try {
                File file = new File(fileInfo.getFileName());
                //boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());
                cursoManejoBeanModel.insertOrUpdateNuevoCursoMultiple(file);
            } catch (Exception ex) {
                Logger.getLogger(CursoManejoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FacesUtils.addErrorMessage("El archivo no es el formato correcto.");
        }
    }

    /**
     * @return the idCampoActual
     */
    public int getIdCampoActual() {
        return cursoManejoBeanModel.getIdCampoActual();
    }

    /**
     * @param idCampoActual the idCampoActual to set
     */
    public void setIdCampoActual(int idCampoActual) {
        cursoManejoBeanModel.setIdCampoActual(idCampoActual);
    }

    public void actualizarSeleccion() {
        String seleccion = FacesUtils.getRequestParameter("idSeleccion");

        if (seleccion != null && !seleccion.isEmpty()) {
            System.out.println(seleccion);
        }
    }

}
