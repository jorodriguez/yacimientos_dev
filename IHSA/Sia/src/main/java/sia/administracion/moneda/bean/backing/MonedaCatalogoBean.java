/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.moneda.bean.backing;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.modelo.Moneda;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.vo.MonedaVO;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "monedaCatalogoBean")
@ViewScoped
public class MonedaCatalogoBean implements Serializable {

    @Inject
    private Sesion sesion;
    @Getter
    @Setter
    private List<MonedaVO> lstMoneda;
    @Getter
    @Setter
    private MonedaVO newMoneda;
    @Getter
    @Setter
    private List<SelectItem> companias;
    @Getter
    @Setter
    private String companiaSeleccionada;

    @Inject
    private MonedaImpl monedaImpl;
    @Inject
    private CompaniaImpl companiaImpl;
    @Inject
    private UsuarioImpl usuarioImpl;

    @PostConstruct
    public void init() {
        this.cargarCompanias();
        this.setNewMoneda(null);
        if (this.getCompaniaSeleccionada() == null || this.getCompaniaSeleccionada().isEmpty()) {
            this.setCompaniaSeleccionada(sesion.getRfcCompania());
            refrescarTabla();
        }
    }

    public void crearMoneda() {
        try {
            this.setNewMoneda(new MonedaVO());
            cargarCompanias();
            String metodo = ";abrirDialogoCrearMoneda();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void editarMoneda( int idMoneda ) {
        try {
            if (idMoneda > 0) {
                cargarCompanias();
                cargarMoneda(idMoneda);
                String metodo = ";abrirDialogoCrearMoneda();";
                PrimeFaces.current().executeScript(metodo);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void deleteMoneda( int idMoneda ) {
        try {
            if (idMoneda > 0) {
                cargarMoneda(idMoneda);
                if (getNewMoneda() != null) {
                    if (getNewMoneda().getId() > 0) {
                        Moneda moneda = monedaImpl.find(getNewMoneda().getId());
                        if (moneda != null) {
                            moneda.setEliminado(Constantes.BOOLEAN_TRUE);
                            moneda.setFechaModifico(new Date());
                            moneda.setHoraModifico(new Date());
                            moneda.setModifico(sesion.getUsuario());
                            monedaImpl.edit(moneda);
                        }
                    }
                }
                this.refrescarTabla();
                this.setNewMoneda(null);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void guardarMoneda() {
        try {
            if (getNewMoneda() != null) {
                if (getNewMoneda().getId() > 0) {
                    Moneda moneda = monedaImpl.find(getNewMoneda().getId());
                    boolean guardar = false;
                    if (moneda != null && !moneda.getNombre().equals(getNewMoneda().getNombre())) {
                        moneda.setNombre(getNewMoneda().getNombre());
                        guardar = true;
                    }
                    if (moneda != null && !moneda.getSiglas().equals(getNewMoneda().getSiglas())) {
                        moneda.setSiglas(getNewMoneda().getSiglas());
                        guardar = true;
                    }
                    if (moneda != null
                            && (getNewMoneda().isActivo() && moneda.isEliminado())) {
                        moneda.setEliminado(Constantes.BOOLEAN_FALSE);
                        guardar = true;
                    }
                    if (moneda != null
                            && (!getNewMoneda().isActivo() && moneda.isEliminado())) {
                        moneda.setEliminado(Constantes.BOOLEAN_TRUE);
                        guardar = true;
                    }
                    if (guardar) {
                        monedaImpl.edit(moneda);
                    }
                } else {
                    Moneda moneda = new Moneda();
                    moneda.setNombre(getNewMoneda().getNombre());
                    moneda.setSiglas(getNewMoneda().getSiglas());
                    moneda.setCompania(companiaImpl.find(getNewMoneda().getCompania()));
                    moneda.setEliminado(Constantes.BOOLEAN_FALSE);
                    moneda.setGenero(usuarioImpl.find(sesion.getUsuario().getId()));
                    moneda.setFechaGenero(new Date());
                    moneda.setHoraGenero(new Date());
                    monedaImpl.create(moneda);
                }
            }
            this.refrescarTabla();
            this.setNewMoneda(null);
            String metodo = ";cerrarDialogoCrearMoneda();";
            PrimeFaces.current().executeScript(metodo);

        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    public void refrescarTabla() {
        this.setLstMoneda(monedaImpl.traerMonedasPorCompania(this.getCompaniaSeleccionada(), 0));
    }

    public void cargarCompanias() {
        this.setCompanias(companiaImpl.traerCompaniasByUsuario(sesion.getUsuario().getId()));
    }

    public void cargarMoneda(int idMoneda) {
        List<MonedaVO> monedas = monedaImpl.traerMonedasPorCompania(sesion.getRfcCompania(), idMoneda);
        if (monedas != null && monedas.size() > 0) {
            setNewMoneda(monedas.get(0));
        }
    }

    public void cambiarValorCompania() {
        try {
            this.refrescarTabla();
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

}
