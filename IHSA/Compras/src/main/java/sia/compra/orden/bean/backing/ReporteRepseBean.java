/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.orden.bean.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.modelo.sgl.vo.OrdenVO;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OrdenImpl;

/**
 *
 * @author jcarranza
 */
@Named(value = "reporteRepseBean")
@ViewScoped
public class ReporteRepseBean implements Serializable {

    /**
     * Creates a new instance of RevisaRepseBean
     */
    public ReporteRepseBean() {
    }
    //Sistema
    @Inject
    private UsuarioBean sesion;

    @Inject
    OrdenImpl ordenImpl;
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenImpl;

    @Getter
    @Setter
    private List<OrdenVO> comprasRepse;
    @Getter
    @Setter
    private LocalDate inicio;
    @Getter
    @Setter
    private LocalDate fin;
    @Getter
    @Setter
    private String codigo;
    @Getter
    @Setter
    private String proveedor;

    @PostConstruct
    public void init() {
        setFin(LocalDate.now());
        setInicio(fin.minusDays(30));
        llenarCompras();
    }

    private void llenarCompras() {
        setComprasRepse(autorizacionesOrdenImpl.traerOrdenReporteRepse(inicio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                fin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), codigo, proveedor, this.sesion.getUsuarioConectado().getApCampo().getId()));

    }

    public void buscar() {
        llenarCompras();
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(UsuarioBean sesion) {
        this.sesion = sesion;
    }

    public int getBloque() {
        return this.sesion.getUsuarioConectado().getApCampo().getId();
    }

}
