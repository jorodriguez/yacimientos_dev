/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.orden.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;


import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.inventarios.service.InvOrdenFormatoImpl;
import sia.modelo.vo.inventarios.OrdenFormatoVo;

/**
 *
 * @author marin
 */
@Named (value = "formatoEntradaBean")
@ViewScoped
public class FormatoEntradaBean implements Serializable{

    /**
     * Creates a new instance of FormatoEntradaBean
     */
    public FormatoEntradaBean() {
    }
    @Inject
    InvOrdenFormatoImpl invOrdenFormatoImpl;

    @Inject
    private UsuarioBean sesion;
    private List<OrdenFormatoVo> formatos;
    private OrdenFormatoVo ordenFormato;
    private Date inicio;
    private Date fin;
    private String compra;

    @PostConstruct
    public void iniciar() {
        inicio = new Date();
        fin = new Date();
        formatos = new ArrayList<OrdenFormatoVo>();
        //
        formatos = invOrdenFormatoImpl.traerPorCampo(sesion.getUsuarioConectado().getApCampo().getId());
    }

    public void buscarPorFechas() {
        formatos = invOrdenFormatoImpl.traerPorFecha(inicio, fin, sesion.getUsuarioConectado().getApCampo().getId());
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(UsuarioBean sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the formatos
     */
    public List<OrdenFormatoVo> getFormatos() {
        return formatos;
    }

    /**
     * @param formatos the formatos to set
     */
    public void setFormatos(List<OrdenFormatoVo> formatos) {
        this.formatos = formatos;
    }

    /**
     * @return the ordenFormato
     */
    public OrdenFormatoVo getOrdenFormato() {
        return ordenFormato;
    }

    /**
     * @param ordenFormato the ordenFormato to set
     */
    public void setOrdenFormato(OrdenFormatoVo ordenFormato) {
        this.ordenFormato = ordenFormato;
    }

    /**
     * @return the inicio
     */
    public Date getInicio() {
        return inicio;
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    /**
     * @return the fin
     */
    public Date getFin() {
        return fin;
    }

    /**
     * @param fin the fin to set
     */
    public void setFin(Date fin) {
        this.fin = fin;
    }

    /**
     * @return the compra
     */
    public String getCompra() {
        return compra;
    }

    /**
     * @param compra the compra to set
     */
    public void setCompra(String compra) {
        this.compra = compra;
    }

}
