/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.sistema.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.faces.bean.ManagedProperty;


import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.modelo.Gerencia;
import sia.modelo.OcGerenciaProyecto;
import sia.modelo.ProyectoOt;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.vo.ApCampoGerenciaVo;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.catalogos.impl.ProyectoOtImpl;
import sia.servicios.requisicion.impl.OcGerenciaProyectoImpl;

/**
 *
 * @author mluis
 */
@Named (value = "gerenciaProyectoBean")
@ViewScoped
public class GerenciaProyectoBean implements Serializable {

    /**
     * Creates a new instance of GerenciaProyectoBean
     */
    public GerenciaProyectoBean() {
    }

    @Inject
    UsuarioBean sesion;
    //
    @Inject
    OcGerenciaProyectoImpl ocGerenciaProyectoImpl;
    @Inject
    ApCampoGerenciaImpl campoGerenciaImpl;
    @Inject
    ProyectoOtImpl proyectoOtImpl;

    private List<OcGerenciaProyecto> lista;
    private OcGerenciaProyecto gerenciaProyecto;
    private List<SelectItem> listaGerencia;
    private List<SelectItem> listaProyecto;
    private int idGerencia;
    private int idProyecto;

    @PostConstruct
    public void iniciar() {
        lista = new ArrayList<>();
        setLista(ocGerenciaProyectoImpl.traerTodoPorCampo(sesion.getUsuarioConectado().getApCampo().getId()));
    }

    public void crearGerenciaProyecto() {
        gerenciaProyecto = new OcGerenciaProyecto();
        listaGerencia = new ArrayList<>();
        listaProyecto = new ArrayList<>();
        //
        List<ApCampoGerenciaVo> l = campoGerenciaImpl.findAllCampoGerenciaPorCampo(sesion.getUsuarioConectado().getApCampo().getId());
        //
        for (ApCampoGerenciaVo apCampoGerenciaVo : l) {
            listaGerencia.add(new SelectItem(apCampoGerenciaVo.getIdGerencia(), apCampoGerenciaVo.getNombreGerencia()));
        }
        //
        List<ProyectoOtVo> lp = proyectoOtImpl.getListaProyectosOtPorCampo(sesion.getUsuarioConectado().getApCampo().getId(), sesion.getUsuarioConectado().getApCampo().getCompania().getRfc(), null, false);
        for (ProyectoOtVo proyectoOtVo : lp) {
            listaProyecto.add(new SelectItem(proyectoOtVo.getId(), proyectoOtVo.getNombre()));
        }
        //
        PrimeFaces.current().executeScript("$(dialogoGerProy).modal('show');");
    }

    public void terminarGuardarProyecto() {
        gerenciaProyecto.setGerencia(new Gerencia(idGerencia));
        gerenciaProyecto.setProyectoOt(new ProyectoOt(idProyecto));
        gerenciaProyecto.setApCampo(sesion.getUsuarioConectado().getApCampo());
        gerenciaProyecto.setGenero(sesion.getUsuarioConectado());
        gerenciaProyecto.setFechaGenero(new Date());
        gerenciaProyecto.setHoraGenero(new Date());
        //
        ocGerenciaProyectoImpl.create(gerenciaProyecto);
        //
        setLista(ocGerenciaProyectoImpl.traerTodoPorCampo(sesion.getUsuarioConectado().getApCampo().getId()));
        //
        PrimeFaces.current().executeScript("$(dialogoGerProy).modal('hide');");
    }

    public void eliminar(int ind) {
        ocGerenciaProyectoImpl.remove(lista.get(ind));
        lista.remove(ind);
    }

    /**
     * @return the lista
     */
    public List<OcGerenciaProyecto> getLista() {
        return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(List<OcGerenciaProyecto> lista) {
        this.lista = lista;
    }

    /**
     * @return the gerenciaProyecto
     */
    public OcGerenciaProyecto getGerenciaProyecto() {
        return gerenciaProyecto;
    }

    /**
     * @param gerenciaProyecto the gerenciaProyecto to set
     */
    public void setGerenciaProyecto(OcGerenciaProyecto gerenciaProyecto) {
        this.gerenciaProyecto = gerenciaProyecto;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(UsuarioBean sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the listaGerencia
     */
    public List<SelectItem> getListaGerencia() {
        return listaGerencia;
    }

    /**
     * @param listaGerencia the listaGerencia to set
     */
    public void setListaGerencia(List<SelectItem> listaGerencia) {
        this.listaGerencia = listaGerencia;
    }

    /**
     * @return the listaProyecto
     */
    public List<SelectItem> getListaProyecto() {
        return listaProyecto;
    }

    /**
     * @param listaProyecto the listaProyecto to set
     */
    public void setListaProyecto(List<SelectItem> listaProyecto) {
        this.listaProyecto = listaProyecto;
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
     * @return the idProyecto
     */
    public int getIdProyecto() {
        return idProyecto;
    }

    /**
     * @param idProyecto the idProyecto to set
     */
    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

}
