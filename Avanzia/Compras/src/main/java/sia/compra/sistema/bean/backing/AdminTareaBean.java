/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.sistema.bean.backing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.file.UploadedFile;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.modelo.SiAdjunto;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.requisicion.vo.OcTareaVo;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.ProyectoOtImpl;
import sia.servicios.requisicion.impl.OcTareaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named(value = "adminTareaBean")
@ViewScoped
public class AdminTareaBean implements Serializable {

    /**
     * Creates a new instance of AdminTareaBean
     */
    public AdminTareaBean() {
    }

    //Sistema
    @Inject
    private UsuarioBean sesion;

    @Inject
    private ProyectoOtImpl proyectoOtImpl;
    @Inject
    private OcTareaImpl ocTareaImpl;
    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;

    private List<ProyectoOtVo> listaProyecto;
    private List<OcTareaVo> listaGerencia;
    private List<OcTareaVo> listaTipoTarea;
    private List<OcTareaVo> listaTarea;
    private List<OcTareaVo> lstTareaTemp = new ArrayList<>();
    private Map<String, List<OcTareaVo>> tareasTemp = new HashMap<>();
    private List<CampoVo> listaCampo;
    //
    private ProyectoOtVo proyectoOtVo;
    private OcTareaVo tareaVo;
    //
    private int tabSeleccionada;
    private int idGerencia;
    private int idTipoTarea;
    private int idTarea;
    private String ot;
    @Getter
    @Setter
    private UploadedFile fileInfo;

    @PostConstruct
    public void iniciar() {
        tabSeleccionada = 0;
        listaProyecto = proyectoOtImpl.getListaProyectosOtPorCampo(sesion.getUsuarioConectado().getApCampo().getId(), sesion.getUsuarioConectado().getApCampo().getCompania().getRfc(), null, false);
    }

    private void cargarCampos() {
        listaCampo = new ArrayList<>();
        List<CampoUsuarioPuestoVo> lc = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuarioConectado().getId());
        for (CampoUsuarioPuestoVo campoUsuarioPuestoVo : lc) {
            if (campoUsuarioPuestoVo.getTipo().equals("N")) {
                CampoVo c = new CampoVo();
                c.setId(campoUsuarioPuestoVo.getIdCampo());
                c.setNombre(campoUsuarioPuestoVo.getCampo());
                c.setSelected(Boolean.FALSE);
                listaCampo.add(c);
            }
        }
    }

    public void cargarTareas() {
        this.cargarCampos();
        String jsMetodo = ";activarTab('tabOCSProc',0, 'divDatos', 'divTabla', 'divOperacion', 'divAutoriza');";
        PrimeFaces.current().executeScript(jsMetodo);
    }

    public void regresarTareas() {
        this.setLstTareaTemp(new ArrayList<OcTareaVo>());
        this.setTareasTemp(new HashMap<String, List<OcTareaVo>>());
        String jsMetodo = ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');";
        PrimeFaces.current().executeScript(jsMetodo);

    }

    public void guardarTareas() {
        String msgSalida = "No se regitro ninguna tarea.";
        boolean entrar = true;
        for (Map.Entry<String, List<OcTareaVo>> entry : this.getTareasTemp().entrySet()) {
            for (OcTareaVo newTarea : entry.getValue()) {
                if (!newTarea.isExisteTarea()
                        && newTarea.getIdProyectoOt() > 0
                        && newTarea.getIdGerencia() > 0
                        && newTarea.getIdNombreTarea() > 0
                        && newTarea.getIdcodigoTarea() > 0
                        && newTarea.getIdUnidadCosto() > 0) {
                    ocTareaImpl.guardar(newTarea, sesion.getUsuarioConectado().getId(), false);
                    if (entrar) {
                        msgSalida = "Las nuevas tareas se registraron exitosamente, ya pueden ser utilizadas en el sistema.";
                        entrar = false;
                    }
                }
            }
        }

        FacesUtilsBean.addInfoMessage(msgSalida);
        String jsMetodo = ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');";
        PrimeFaces.current().executeScript(jsMetodo);
        this.setLstTareaTemp(new ArrayList<OcTareaVo>());
        this.setTareasTemp(new HashMap<String, List<OcTareaVo>>());
    }

    public void uploadFile(FileUploadEvent event) {
        List<CampoVo> ltemp = new ArrayList<>();
        for (CampoVo campoVo : listaCampo) {
            if (campoVo.isSelected()) {
                ltemp.add(campoVo);
            }
        }
        if (!ltemp.isEmpty()) {
            try {
                fileInfo = event.getFile();
                SiAdjunto adj;
                File file = new File("/tmp/" + fileInfo.getFileName());
                try (OutputStream os = new FileOutputStream(file)) {
                    os.write(fileInfo.getContent());
                    setTareasTemp(ocTareaImpl.cargarTareas(file, sesion.getUsuarioConectado().getId(), ltemp));                    
                    Files.deleteIfExists(file.toPath());
                } catch (IOException ex) {
                    UtilLog4j.log.error(ex);
                }
                listaProyecto = proyectoOtImpl.getListaProyectosOtPorCampo(sesion.getUsuarioConectado().getApCampo().getId(), sesion.getUsuarioConectado().getApCampo().getCompania().getRfc(), null, false);
                PrimeFaces.current().executeScript(";quitarAareaBlockCSS();");
//                FacesUtilsBean.addInfoMessage("Se agregaron las tareas.");
            } catch (Exception e) {
                UtilLog4j.log.error(e);
                FacesUtilsBean.addErrorMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
            }
        } else {
            FacesUtilsBean.addInfoMessage("Seleccione al menos un campo");
        }
    }

    public void cerrarCargaTareas() {
        setLstTareaTemp(new ArrayList<OcTareaVo>());
        PrimeFaces.current().executeScript(";$(dialogoCargarTareas).modal('hide');");
    }

    public void eliminarOt() {
        List<ProyectoOtVo> ltemp = new ArrayList<>();
        for (ProyectoOtVo potVo : listaProyecto) {
            if (potVo.isSelected()) {
                ltemp.add(potVo);
            }
        }
        if (!ltemp.isEmpty()) {
            proyectoOtImpl.cerrarProyectoOt(ltemp, sesion.getUsuarioConectado().getId());
            //
            listaProyecto = proyectoOtImpl.getListaProyectosOtPorCampo(sesion.getUsuarioConectado().getApCampo().getId(), sesion.getUsuarioConectado().getApCampo().getCompania().getRfc(), null, false);
        } else {
            FacesUtilsBean.addErrorMessage("Seleccione al menos un proyecto OT");
        }
    }

    public void seleccionarOt(SelectEvent event) {
        proyectoOtVo = new ProyectoOtVo();
        proyectoOtVo = (ProyectoOtVo) event.getObject();
        tabSeleccionada = 1;
        //
        listaGerencia = ocTareaImpl.traerGerenciaPorProyectoOT(proyectoOtVo.getId());
        //
        listaTipoTarea = new ArrayList<>();
        listaTarea = new ArrayList<>();
        String jsMetodo = ";pintarOpaa();";
        PrimeFaces.current().executeScript(jsMetodo);
    }

    public void seleccionarGerencia(SelectEvent event) {
        tareaVo = new OcTareaVo();
        tareaVo = (OcTareaVo) event.getObject();
        tabSeleccionada = 2;
        System.out.println("Proy: " + proyectoOtVo.getId() + "Ger; " + tareaVo.getIdGerencia());
        listaTipoTarea = ocTareaImpl.traerTipoTareaPorGerenciaOt(tareaVo.getIdGerencia(), proyectoOtVo.getId());
        listaTarea = new ArrayList<OcTareaVo>();
        String jsMetodo = ";pintarOpaa();";
        PrimeFaces.current().executeScript(jsMetodo);
    }

    public void eliminarGerencia() {
        List<OcTareaVo> ltemp = new ArrayList<OcTareaVo>();
        for (OcTareaVo potVo : listaGerencia) {
            if (potVo.isSelected()) {
                ltemp.add(potVo);
            }
        }
        if (!ltemp.isEmpty()) {
            for (OcTareaVo ocTareaVo : ltemp) {
                ocTareaImpl.eliminarRelacionGerenciaProyectoOt(proyectoOtVo.getId(), ocTareaVo.getIdGerencia(), sesion.getUsuarioConectado().getId());
                //                
            }
            listaGerencia = ocTareaImpl.traerGerenciaPorProyectoOT(proyectoOtVo.getId());
        } else {
            FacesUtilsBean.addErrorMessage("Seleccione al menos una gerencia");
        }
    }

    public void seleccionarTipoTarea(SelectEvent event) {
        tareaVo = new OcTareaVo();
        tareaVo = (OcTareaVo) event.getObject();
        tabSeleccionada = 3;
        System.out.println("Proy: " + proyectoOtVo.getId() + " Gerencia; " + tareaVo.getIdGerencia() + " Tipo tarea; " + tareaVo.getIdUnidadCosto());
        listaTarea = ocTareaImpl.traerTarea(tareaVo.getIdGerencia(), proyectoOtVo.getId(), tareaVo.getIdUnidadCosto());
        String jsMetodo = ";pintarOpaa();";
        PrimeFaces.current().executeScript(jsMetodo);
    }

    public void eliminarTipoTarea() {
        List<OcTareaVo> ltemp = new ArrayList<OcTareaVo>();
        for (OcTareaVo potVo : listaTipoTarea) {
            if (potVo.isSelected()) {
                ltemp.add(potVo);
            }
        }
        if (!ltemp.isEmpty()) {
            for (OcTareaVo ocTareaVo : ltemp) {
                ocTareaImpl.eliminarRelacionUnidadCosto(proyectoOtVo.getId(), ocTareaVo.getIdGerencia(), ocTareaVo.getIdUnidadCosto(), sesion.getUsuarioConectado().getId());
            }
            listaTipoTarea = ocTareaImpl.traerTipoTareaPorGerenciaOt(tareaVo.getIdGerencia(), proyectoOtVo.getId());
        } else {
            FacesUtilsBean.addErrorMessage("Seleccione al menos tipo de tarea");
        }
    }

    public void eliminarTarea() {
        List<OcTareaVo> ltemp = new ArrayList<OcTareaVo>();
        for (OcTareaVo potVo : listaTarea) {
            if (potVo.isSelected()) {
                ltemp.add(potVo);
            }
        }
        if (!ltemp.isEmpty()) {
            for (OcTareaVo ocTareaVo : ltemp) {
                ocTareaImpl.eliminarTarea(ocTareaVo.getIdTarea(), sesion.getUsuarioConectado().getId());
            }
            listaTarea = ocTareaImpl.traerTarea(tareaVo.getIdGerencia(), proyectoOtVo.getId(), tareaVo.getIdUnidadCosto());
        } else {
            FacesUtilsBean.addErrorMessage("Seleccione al menos una tarea");
        }
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(UsuarioBean sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the listaProyecto
     */
    public List<ProyectoOtVo> getListaProyecto() {
        return listaProyecto;
    }

    /**
     * @param listaProyecto the listaProyecto to set
     */
    public void setListaProyecto(List<ProyectoOtVo> listaProyecto) {
        this.listaProyecto = listaProyecto;
    }

    /**
     * @return the proyectoOtVo
     */
    public ProyectoOtVo getProyectoOtVo() {
        return proyectoOtVo;
    }

    /**
     * @param proyectoOtVo the proyectoOtVo to set
     */
    public void setProyectoOtVo(ProyectoOtVo proyectoOtVo) {
        this.proyectoOtVo = proyectoOtVo;
    }

    /**
     * @return the listaGerencia
     */
    public List<OcTareaVo> getListaGerencia() {
        return listaGerencia;
    }

    /**
     * @param listaGerencia the listaGerencia to set
     */
    public void setListaGerencia(List<OcTareaVo> listaGerencia) {
        this.listaGerencia = listaGerencia;
    }

    /**
     * @return the listaTarea
     */
    public List<OcTareaVo> getListaTarea() {
        return listaTarea;
    }

    /**
     * @param listaTarea the listaTarea to set
     */
    public void setListaTarea(List<OcTareaVo> listaTarea) {
        this.listaTarea = listaTarea;
    }

    /**
     * @return the listaTipoTarea
     */
    public List<OcTareaVo> getListaTipoTarea() {
        return listaTipoTarea;
    }

    /**
     * @param listaTipoTarea the listaTipoTarea to set
     */
    public void setListaTipoTarea(List<OcTareaVo> listaTipoTarea) {
        this.listaTipoTarea = listaTipoTarea;
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
        return idGerencia;
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
        this.idGerencia = idGerencia;
    }

    /**
     * @return the idTipoTarea
     */
    public int getIdTipoTarea() {
        return idTipoTarea;
    }

    /**
     * @param idTipoTarea the idTipoTarea to set
     */
    public void setIdTipoTarea(int idTipoTarea) {
        this.idTipoTarea = idTipoTarea;
    }

    /**
     *
     * @return the tabSeleccionada
     */
    public int getTabSeleccionada() {
        return tabSeleccionada;
    }

    /**
     * @param tabSeleccionada the tabSeleccionada to set
     */
    public void setTabSeleccionada(int tabSeleccionada) {
        this.tabSeleccionada = tabSeleccionada;
    }

    public int getIdTarea() {
        return idTarea;
    }

    /**
     * @param idTarea the idTarea to set
     */
    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }

    /**
     * @return the tareaVo
     */
    public OcTareaVo getTareaVo() {
        return tareaVo;
    }

    /**
     * @param tareaVo the tareaVo to set
     */
    public void setTareaVo(OcTareaVo tareaVo) {
        this.tareaVo = tareaVo;
    }

    /**
     * @return the listaCampo
     */
    public List<CampoVo> getListaCampo() {
        return listaCampo;
    }

    /**
     * @param listaCampo the listaCampo to set
     */
    public void setListaCampo(List<CampoVo> listaCampo) {
        this.listaCampo = listaCampo;
    }

    /**
     * @return the ot
     */
    public String getOt() {
        return ot;
    }

    /**
     * @param ot the ot to set
     */
    public void setOt(String ot) {
        this.ot = ot;
    }

    /**
     * @return the lstTareaTemp
     */
    public List<OcTareaVo> getLstTareaTemp() {
        return lstTareaTemp;
    }

    /**
     * @param lstTareaTemp the lstTareaTemp to set
     */
    public void setLstTareaTemp(List<OcTareaVo> lstTareaTemp) {
        this.lstTareaTemp = lstTareaTemp;
    }

    /**
     * @return the tareasTemp
     */
    public Map<String, List<OcTareaVo>> getTareasTemp() {
        return tareasTemp;
    }

    /**
     * @param tareasTemp the tareasTemp to set
     */
    public void setTareasTemp(Map<String, List<OcTareaVo>> tareasTemp) {
        this.tareasTemp = tareasTemp;
    }

}
