/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.vehiculo.bean.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.modelo.Usuario;
import sia.modelo.cursoManejo.vo.CursoManejoVo;
import sia.notificaciones.sgl.impl.NotificacionServiciosGeneralesImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.vehiculo.impl.SgCursoManejoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.LecturaLibro;
import sia.util.UtilLog4j;

/**
 *
 * @author jevazquez
 */
@Named(value = "cursoManejoBeanModel")
@ViewScoped
public class CursoManejoBeanModel implements Serializable {

    @Inject
    private Sesion sesion;

    @Inject
    private SgCursoManejoImpl cursoManejoImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private NotificacionServiciosGeneralesImpl notificacionServiciosGeneralesImpl;
    @Inject
    private SiParametroImpl parametroImpl;

    private List<CursoManejoVo> usuariosConCurso;
    private List<Object[]> listUsuariosConCurso;
    private CursoManejoVo cursoSeleccionado;

    private Date fechaInicio;
    private Date fechaFin;
    private Date fechaVencimientoInicio;
    private Date fechaVencimientoFin;

    private String nombreUsuarioSeleccionado;

    private int idCursoSeleccionado;
    private int numeroCurso;
    private int idCampoActual;

    private boolean selectTodos;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    public CursoManejoBeanModel() {
    }

    @PostConstruct
    public void goToVentanaCurso() {
        limpiarListaUsuariosConCurso();
        cargarListaCursoManejo();
        cargarListaUsuarios();
    }

    public void cargarListaCursoManejo() {

        List<CursoManejoVo> lm = cursoManejoImpl.relacionUsuarioCursos(Constantes.TRUE, Constantes.FALSE, Constantes.FALSE);

        if (lm != null && !lm.isEmpty()) {
            setUsuariosConCurso(lm);
        }

    }

    /**
     * @return the usuariosConCurso
     */
    public List<CursoManejoVo> getUsuariosConCurso() {
        return usuariosConCurso;
    }

    /**
     * @param usuariosConCurso the usuariosConCurso to set
     */
    public void setUsuariosConCurso(List<CursoManejoVo> usuariosConCurso) {
        this.usuariosConCurso = usuariosConCurso;
    }

    public void cargarListaUsuarios() {

        setIdCampoActual(sesion.getUsuario().getApCampo().getId());
        setListUsuariosConCurso(usuarioImpl.traerUsuarioActivosJson(Constantes.CERO));

        JsonArray a = new JsonArray();
        Gson gson = new Gson();
        if (getListUsuariosConCurso() != null) {
            for (Object[] o : getListUsuariosConCurso()) {

                JsonObject ob = new JsonObject();
                ob.addProperty("value", (o[0] != null ? (String) o[0].toString() : "-"));
                ob.addProperty("label", o[1] != null ? o[1].toString() : "-");
                //ob.addProperty("type","-");
                a.add(ob);
            }
        }
        String usuarios = gson.toJson(a);
        PrimeFaces.current().executeScript(";cargarUsuarioCurso(" + usuarios + ",'usuariosCursoList');");
    }

    public void limpiarListaUsuariosConCurso() {
        PrimeFaces.current().executeScript(";limpiarDataListUsuario();");
    }

    /**
     * @return the listUsuariosConCurso
     */
    public List<Object[]> getListUsuariosConCurso() {
        return listUsuariosConCurso;
    }

    /**
     * @param listUsuariosConCurso the listUsuariosConCurso to set
     */
    public void setListUsuariosConCurso(List<Object[]> listUsuariosConCurso) {
        this.listUsuariosConCurso = listUsuariosConCurso;
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
     * @return the nombreUsuarioSeleccionado
     */
    public String getNombreUsuarioSeleccionado() {
        return nombreUsuarioSeleccionado;
    }

    /**
     * @param nombreUsuarioSeleccionado the nombreUsuarioSeleccionado to set
     */
    public void setNombreUsuarioSeleccionado(String nombreUsuarioSeleccionado) {
        this.nombreUsuarioSeleccionado = nombreUsuarioSeleccionado;
    }

    /**
     * @return the idCursoSeleccionado
     */
    public int getIdCursoSeleccionado() {
        return idCursoSeleccionado;
    }

    /**
     * @param idCursoSeleccionado the idCursoSeleccionado to set
     */
    public void setIdCursoSeleccionado(int idCursoSeleccionado) {
        this.idCursoSeleccionado = idCursoSeleccionado;
    }

    public void cursoByUsuario() {
        try {
            String usuario = FacesUtils.getRequestParameter("completarEmpleado");
            List<Object[]> newList = getListUsuariosConCurso();

            if (usuario != null && !usuario.isEmpty()) {
                for (Object[] o : newList) {
                    if (usuario.equals(o[1])) {
                        String idUsuario = o[0].toString();
                        setUsuariosConCurso(cursoManejoImpl.usuariosCursosActivosByUsuarioUId(idUsuario));
                        break;
                    }

                }
            } else {
                FacesUtils.addErrorMessage("El usuario no cuenta con el curso de manejo registrado");
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    public void cursoByFechaVencimiento() {
        List<CursoManejoVo> newList = cursoManejoImpl.usuariosCursosActivosByVencimeintoAndCampo(
                getFechaInicio(), getFechaFin(), Constantes.AP_CAMPO_DEFAULT, Constantes.TRUE, Constantes.FALSE,"   order by ofi.id,c.FECHA_VENCIMIENTO");
        if (!newList.isEmpty()) {
            setUsuariosConCurso(newList);
        }

    }

    public void cursoById() {
        int numCurso = Integer.parseInt(FacesUtils.getRequestParameter("idCursoM"));
        setUsuariosConCurso(cursoManejoImpl.usuariosCursosActivosByNumCurso(numCurso));
    }

    public void crearCursoManejo() {
        String usuario = FacesUtils.getRequestParameter("completarEmpleado");
        String idcurso = FacesUtils.getRequestParameter("idCursoM");

        if (usuario != null && getFechaFin().after(new Date()) && this.getFechaFin().after(this.getFechaInicio())) {

            Usuario u = usuarioImpl.buscarPorNombre(usuario);
            int numCurso = 0;
            if (idcurso != null) {
                numCurso = Integer.parseInt(idcurso);
            }

            if (cursoManejoImpl.cursoExiste(numCurso, u.getNombre()) == 0) {
                cursoManejoImpl.nuevoCursoManejo(getFechaInicio(), getFechaFin(), u.getId(), numCurso, getSesion().getUsuario().getId());
                cargarListaCursoManejo();
            } else {
                FacesUtils.addErrorMessage("El usuario o el numero de curso ya existe");
            }

        }
    }

    public void editarCurso() {
        String idcurso = FacesUtils.getRequestParameter("idActual");

        if (idcurso != null && !idcurso.isEmpty()) {
            int cursoActual = Integer.parseInt(idcurso);
            setCursoSeleccionado(cursoManejoImpl.usuariosCursosActivosByidCurso(cursoActual));
            setNombreUsuarioSeleccionado(getCursoSeleccionado().getNameUser());
            setFechaInicio(getCursoSeleccionado().getFechaExpedicion());
            setFechaFin(getCursoSeleccionado().getFechaVencimiento());
            setNumeroCurso(getCursoSeleccionado().getNumCurso());

            PrimeFaces.current().executeScript(";$('#editFechas').modal('show');");
        }
        //   cursoManejoImpl.ActualizarCurso(0, idUsuario, fechaInicio, fechaInicio);
    }

    /**
     * @return the cursoSeleccionado
     */
    public CursoManejoVo getCursoSeleccionado() {
        return cursoSeleccionado;
    }

    /**
     * @param cursoSeleccionado the cursoSeleccionado to set
     */
    public void setCursoSeleccionado(CursoManejoVo cursoSeleccionado) {
        this.cursoSeleccionado = cursoSeleccionado;
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
     * @return the numeroCurso
     */
    public int getNumeroCurso() {
        return numeroCurso;
    }

    /**
     * @param numeroCurso the numeroCurso to set
     */
    public void setNumeroCurso(int numeroCurso) {
        this.numeroCurso = numeroCurso;
    }

    public void finEditCurso() {
        int nuevoNum = Integer.parseInt(FacesUtils.getRequestParameter("idCursoMEdit"));

        cursoManejoImpl.actualizarCurso(getCursoSeleccionado().getIdCursoManejo(), sesion.getUsuario().getId(),
                getFechaInicio(), getFechaFin(), nuevoNum);

        cargarListaCursoManejo();
        PrimeFaces.current().executeScript(";$('#editFechas').modal('hide');");

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

    public void buscarCursosByFiltros() {
        String var = FacesUtils.getRequestParameter("selecFiltro");
        boolean tipo = false;
        boolean todo = false;
        boolean porVencer = false;
        if (var != null && !var.isEmpty()) {
            if (var.equals("todos")) {
                todo = true;
            } else if (var.equals("vigentes")) {
                tipo = true;
            } else if (var.equals("PorVencer")){
                porVencer = true;
            }
        }
        if (getFechaVencimientoInicio() != null && !porVencer) {
            if (getFechaVencimientoFin() != null) {
                setUsuariosConCurso(cursoManejoImpl.usuariosCursosActivosByVencimeintoAndCampo(
                        getFechaVencimientoInicio(), getFechaVencimientoFin(), Constantes.AP_CAMPO_NEJO, todo, tipo,"   order by ofi.id,c.FECHA_VENCIMIENTO"));
            } else {
                setUsuariosConCurso(cursoManejoImpl.usuariosCursosActivosByVencimeintoMAyorOMenor(
                        getFechaVencimientoInicio(), Constantes.AP_CAMPO_NEJO, todo, tipo, Constantes.TRUE));
            }
        } else if (getFechaVencimientoFin() != null && !porVencer) {
            setUsuariosConCurso(cursoManejoImpl.usuariosCursosActivosByVencimeintoMAyorOMenor(
                    getFechaVencimientoFin(), Constantes.AP_CAMPO_NEJO, todo, tipo, Constantes.FALSE));
        } else {
            setUsuariosConCurso(cursoManejoImpl.relacionUsuarioCursos(todo, tipo, porVencer));
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

    public void seleccionarTodo(boolean select) {
        for (int i = 0; i < getUsuariosConCurso().size(); i++) {
            getUsuariosConCurso().get(i).setSelect(select);
        }

    }

    public void enviarNotificacionCursoManejo() {

        if (!getUsuariosConCurso().isEmpty()) {

            for (CursoManejoVo curso : getUsuariosConCurso()) {
                if (curso.isSelect()) {
                    notificacionServiciosGeneralesImpl.enviarAvisoNotificacionVencimientoCursoManejoByUser(curso, Constantes.FALSE);
                }

            }
        } else {
            FacesUtils.addErrorMessage("no se encotro algun curso");
        }

    }

    /**
     * @return the idCampoActual
     */
    public int getIdCampoActual() {
        return idCampoActual;
    }

    /**
     * @param idCampoActual the idCampoActual to set
     */
    public void setIdCampoActual(int idCampoActual) {
        this.idCampoActual = idCampoActual;
    }

    public void insertOrUpdateNuevoCursoMultiple(File file) throws Exception {

        boolean regresa = false;
        String informacion = "";
        String modal = "";
        LecturaLibro ll = new LecturaLibro();

        XSSFWorkbook wb = ll.loadFileXLSX(file);
        XSSFSheet sheet = ll.loadSheet(wb);
        XSSFRow row;
        Cell cell;
        List<List<Object>> newCursosByUsuarios = new ArrayList<List<Object>>();
        List<Object> insertar;
        List<String> cursos = new ArrayList<>();
        String name = "";
        Date fechaCurso;
        Date fechaVencimiento;
        int numeroTarjeta = 0;
        CursoManejoVo cursoManejoVo;
        int idCurso = 0;

        if (sheet.getLastRowNum() > 1) {
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                row = sheet.getRow(i);
                if (row.getCell(0) != null && row.getCell(3) != null && row.getCell(4) != null) {
                    insertar = new ArrayList<>();
                    insertar.add(row.getCell(0).getStringCellValue());
                    insertar.add(row.getCell(3).getDateCellValue());
                    insertar.add(row.getCell(4).getDateCellValue());
                    if (row.getCell(5) != null) {
                        insertar.add(row.getCell(5).getNumericCellValue());
                    }
                    newCursosByUsuarios.add(insertar);
                }
            }
            if (!newCursosByUsuarios.isEmpty()) {
                for (List<Object> listInterna : newCursosByUsuarios) {
                    cursoManejoVo = new CursoManejoVo();
                    if (listInterna.size() == 3) {
                        name = listInterna.get(0).toString();
                        fechaCurso = (Date) listInterna.get(1);
                        fechaVencimiento = (Date) listInterna.get(2);
                        numeroTarjeta = 0;
                        cursoManejoVo.setNameUser(name);
                        cursoManejoVo.setFechaExpedicion(fechaCurso);
                        cursoManejoVo.setFechaVencimiento(fechaVencimiento);
                    } else if (listInterna.size() == 4) {
                        name = listInterna.get(0).toString();
                        fechaCurso = (Date) listInterna.get(1);
                        fechaVencimiento = (Date) listInterna.get(2);
                        numeroTarjeta = ((Double) listInterna.get(3)).intValue();
                        cursoManejoVo.setNameUser(name);
                        cursoManejoVo.setFechaExpedicion(fechaCurso);
                        cursoManejoVo.setFechaVencimiento(fechaVencimiento);
                        cursoManejoVo.setNumCurso(numeroTarjeta);
                    }

                    idCurso = cursoManejoImpl.cursoExiste(cursoManejoVo.getNumCurso(), cursoManejoVo.getNameUser());
                    if (idCurso == 0) {
                        cursoManejoImpl.insertarCursoNuevo(cursoManejoVo, sesion.getUsuario().getId());

                    } else if (idCurso > 0) {
                        cursoManejoImpl.actualizarCurso(idCurso, sesion.getUsuario().getId(),
                                cursoManejoVo.getFechaExpedicion(), cursoManejoVo.getFechaVencimiento(), cursoManejoVo.getNumCurso());
                    }
                }
            }
            cargarListaCursoManejo();
            PrimeFaces.current().executeScript(";$('#addOrUpdateCursos').modal('hide');");
        } else {
            FacesUtils.addErrorMessage("No se encontraron registros para insertar o actualizar.");
        }

    }

}
