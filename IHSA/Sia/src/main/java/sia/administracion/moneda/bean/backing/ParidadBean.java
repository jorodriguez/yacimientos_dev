/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.moneda.bean.backing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.administracion.moneda.bean.model.ParidadBeanModel;
import sia.constantes.Constantes;
import sia.modelo.Compania;
import sia.modelo.Moneda;
import sia.modelo.Paridad;
import sia.modelo.ParidadValor;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.catalogos.impl.ParidadImpl;
import sia.servicios.catalogos.impl.ParidadValorImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.vo.MonedaVO;
import sia.servicios.sistema.vo.ParidadAnual;
import sia.servicios.sistema.vo.ParidadMensual;
import sia.servicios.sistema.vo.ParidadVO;
import sia.servicios.sistema.vo.ParidadValorVO;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "paridadBean")
@ViewScoped
public class ParidadBean implements Serializable {

    /**
     * @return the sesion
     */
    @Inject
    Sesion sesion;
    @Setter
    @Getter
    private List<ParidadVO> lstParidad;
    @Setter
    @Getter
    private ParidadVO newParidad;
    @Setter
    @Getter
    private List<SelectItem> companias;
    @Setter
    @Getter
    private int paridadSeleccionada;
    @Setter
    @Getter
    private String companiaSeleccionada;
    @Setter
    @Getter
    private List<SelectItem> monedasOrigen;
    @Setter
    @Getter
    private List<SelectItem> monedasDestino;
    @Setter
    @Getter
    private MonedaVO monedaOrigen;
    @Setter
    @Getter
    private Compania companiaSelec;
    @Setter
    @Getter
    private int indexTab = 1;
    @Setter
    @Getter
    private String activeTab1 = "active";
    @Setter
    @Getter
    private String activeTab2 = "";
    @Setter
    @Getter
    private ParidadAnual paridadAnual;
    @Setter
    @Getter
    private ParidadValorVO newParidadValorVO;

    @Inject
    private MonedaImpl monedaImpl;
    @Inject
    private CompaniaImpl companiaImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private ParidadImpl paridadImpl;
    @Inject
    private ParidadValorImpl paridadValorImpl;

    @PostConstruct
    public void init() {
        lstParidad = new ArrayList<>();
        companias = new ArrayList<>();
        monedasOrigen = new ArrayList<>();
        monedasDestino = new ArrayList<>();
        this.cargarCompanias();
        this.setNewParidad(null);
        if (this.getCompaniaSeleccionada() == null || this.getCompaniaSeleccionada().isEmpty()) {
            this.setCompaniaSeleccionada(sesion.getRfcCompania());
            refrescarTabla();
        }
        cargarParidadAnual();
    }

    public void cargarParidadAnual() {
        this.paridadAnual = new ParidadAnual();
        Calendar calInicio = Calendar.getInstance();
        Calendar calFin = Calendar.getInstance();
        int year = calInicio.get(Calendar.YEAR);
        for (int i = 0; i < 12; i++) {
            calInicio.set(year, i, 1);
            calFin.set(year, i, 31);
            getParidadAnual().getMeses().add(new ParidadMensual());
            getParidadAnual().getMeses().get(i).setParidades(new ArrayList<>());
            ParidadVO pp = null;
            for (ParidadVO p : getLstParidad()) {
                pp = new ParidadVO();
                pp.setId(p.getId());
                pp.setNombre(p.getNombre());
                pp.setMonedaOrigenSiglas(p.getMonedaOrigenSiglas());
                pp.setMes(paridadValorImpl.traerParidadValor(p.getId(),
                        Constantes.FMT_yyyy_MM_dd.format(calInicio.getTime()),
                        Constantes.FMT_yyyy_MM_dd.format(calFin.getTime()), 0));
                getParidadAnual().getMeses().get(i).getParidades().add(pp);
            }
        }

    }

    public void crearParidad() {
        try {
            this.setNewParidad(new ParidadVO());
            this.companiaSelec = companiaImpl.find(this.getCompaniaSeleccionada());
            cargarMonedasOri();
            String metodo = ";abrirDialogoCrearParidad();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void crearParidadValor(int idParidad) {
        try {

            this.setNewParidadValorVO(new ParidadValorVO());
            this.getNewParidadValorVO().setIdParidad(idParidad);
            this.companiaSelec = companiaImpl.find(this.getCompaniaSeleccionada());
            String metodo = ";abrirDialogoCrearParidadValor();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void editarParidad(int idParidad) {
        try {

            this.companiaSelec = companiaImpl.find(this.getCompaniaSeleccionada());
            cargarMonedasOri();
            cargarParidad(idParidad);
            String metodo = ";abrirDialogoCrearParidad();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void editarParidadValor(int idParidadValor) {
        try {
            if (idParidadValor > 0) {
                this.companiaSelec = companiaImpl.find(this.getCompaniaSeleccionada());
                cargarParidadValor(idParidadValor);
                String metodo = ";abrirDialogoCrearParidadValor();";
                PrimeFaces.current().executeScript(metodo);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void deleteParidad(int idParidad) {
        try {

            if (idParidad > 0) {
                cargarParidad(idParidad);
                desactivarParidad();
                this.refrescarTabla();
                this.setNewParidad(null);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void guardarParidad() {
        try {
            try {
                if (getNewParidad() != null && getCompaniaSelec() != null) {
                    if (getNewParidad().getId() > 0) {
                        Paridad paridad = paridadImpl.find(getNewParidad().getId());
                        boolean guardar = false;

                        Moneda monedaOr = monedaImpl.find(this.getNewParidad().getMonedaOrigen());
                        Moneda monedaDes = monedaImpl.find(this.getNewParidad().getMonedaDestino());
                        if (paridad != null && paridad.getMoneda().getId() != getNewParidad().getMonedaOrigen()) {
                            paridad.setMoneda(monedaOr);
                            paridad.setNombre(monedaOr.getSiglas() + " -> " + monedaDes.getSiglas());
                            guardar = true;
                        }

                        if (paridad != null && paridad.getMonedades().getId() != getNewParidad().getMonedaDestino()) {
                            paridad.setMonedades(monedaDes);
                            paridad.setNombre(monedaOr.getSiglas() + " -> " + monedaDes.getSiglas());
                            guardar = true;
                        }

                        if (guardar) {
                            paridadImpl.edit(paridad);
                        }
                    } else {
                        Moneda monedaOr = monedaImpl.find(this.getNewParidad().getMonedaOrigen());
                        Moneda monedaDe = monedaImpl.find(this.getNewParidad().getMonedaDestino());
                        Paridad paridad = new Paridad();
                        paridad.setNombre(monedaOr.getSiglas() + " -> " + monedaDe.getSiglas());
                        paridad.setFechaValido(this.getNewParidad().getFechaValido());
                        paridad.setMoneda(monedaOr);
                        paridad.setMonedades(monedaDe);
                        paridad.setEliminado(Constantes.BOOLEAN_FALSE);
                        paridad.setGenero(usuarioImpl.find(sesion.getUsuario().getId()));
                        paridad.setFechaGenero(new Date());
                        paridad.setHoraGenero(new Date());
                        paridad.setCompania(this.getCompaniaSelec());

                        paridadImpl.create(paridad);
                    }
                }
                this.refrescarTabla();
                this.setNewParidad(null);
                String metodo = ";cerrarDialogoCrearParidad();";
                PrimeFaces.current().executeScript(metodo);

            } catch (Exception e) {
                UtilLog4j.log.fatal(e);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void guardarParidadValor() {
        try {

            try {
                if (getNewParidadValorVO() != null && getCompaniaSelec() != null) {
                    if (getNewParidadValorVO().getId() > 0) {
                        ParidadValor paridadValor = paridadValorImpl.find(getNewParidadValorVO().getId());
                        boolean guardar = false;

                        if (paridadValor != null && paridadValor.getValor() != getNewParidadValorVO().getValor()) {
                            BigDecimal valor = new BigDecimal(getNewParidadValorVO().getValor());
                            valor = valor.setScale(4, RoundingMode.DOWN);
                            paridadValor.setValor(valor.doubleValue());
                            guardar = true;
                        }

                        if (paridadValor != null && paridadValor.getFechaValido().getTime() != getNewParidadValorVO().getFechaValido().getTime()) {
                            paridadValor.setFechaValido(getNewParidadValorVO().getFechaValido());
                            guardar = true;
                        }

                        if (guardar) {
                            paridadValorImpl.edit(paridadValor);
                        }
                    } else {
                        Paridad paridad = paridadImpl.find(this.getNewParidadValorVO().getIdParidad());
                        ParidadValor paridadValor = new ParidadValor();
                        paridadValor.setFechaValido(this.getNewParidadValorVO().getFechaValido());
                        BigDecimal valor = new BigDecimal(getNewParidadValorVO().getValor());
                        valor = valor.setScale(4, RoundingMode.DOWN);
                        paridadValor.setValor(valor.doubleValue());
                        paridadValor.setEliminado(Constantes.BOOLEAN_FALSE);
                        paridadValor.setGenero(usuarioImpl.find(sesion.getUsuario().getId()));
                        paridadValor.setFechaGenero(new Date());
                        paridadValor.setHoraGenero(new Date());
                        paridadValor.setParidad(paridad);
                        paridadValorImpl.create(paridadValor);
                    }
                }
                cargarParidadAnual();
                this.setNewParidadValorVO(null);
                String metodo = ";cerrarDialogoCrearParidadValor();";
                PrimeFaces.current().executeScript(metodo);

            } catch (EJBException e) {
                UtilLog4j.log.fatal(e);
                throw new EJBException();
            } catch (Exception e) {
                UtilLog4j.log.fatal(e);
                throw new Exception();
            }
        } catch (EJBException e) {
            FacesUtils.addErrorMessage("El valor de la paridad esta mal capturado o ya existe una paridad para esta fecha.");
        } catch (Exception ex) {
            FacesUtils.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx).");
        }
    }

    public void cargarCompanias() {
        setCompanias(companiaImpl.traerCompaniasByUsuario(this.sesion.getUsuario().getId()));
    }

    public void cargarMonedasOri() {
        this.setMonedasOrigen(monedaImpl.traerMonedasPorCompaniaItems(this.getCompaniaSeleccionada(), null));
    }

    public void cargarMonedasDes(int mOID) {
        refrescarMonedaOrigen(mOID);
        this.setMonedasDestino(monedaImpl.traerMonedasPorCompaniaItems(this.getCompaniaSeleccionada(), getMonedaOrigen().getSiglas()));
    }

    public void refrescarMonedaOrigen(int monedaOrID) {
        List<MonedaVO> monedaLoc = monedaImpl.traerMonedasPorCompania(this.getCompaniaSeleccionada(), monedaOrID);
        if (monedaLoc != null && monedaLoc.size() > 0) {
            this.setMonedaOrigen(monedaLoc.get(0));
        }
    }

    public void cargarParidad(int idParidad) {
        List<ParidadVO> paridades = paridadImpl.traerParidad(this.companiaSeleccionada, 0, idParidad);
        if (paridades.size() > 0) {
            setNewParidad(paridades.get(0));
        }
    }

    public void cargarParidadValor(int idParidadValor) {
        List<ParidadValorVO> paridades = paridadValorImpl.traerParidadValor(0, null, null, idParidadValor);
        if (paridades.size() > 0) {
            setNewParidadValorVO(paridades.get(0));
        }
    }

    public void refrescarTabla() {
        this.setLstParidad(paridadImpl.traerParidad(this.getCompaniaSeleccionada(), 0, 0));
    }

    public void desactivarParidad() {
        try {
            if (getNewParidad() != null) {
                if (getNewParidad().getId() > 0) {
                    Paridad paridad = paridadImpl.find(getNewParidad().getId());
                    if (paridad != null) {
                        paridad.setEliminado(Constantes.BOOLEAN_TRUE);
                        paridad.setFechaModifico(new Date());
                        paridad.setHoraModifico(new Date());
                        paridad.setModifico(sesion.getUsuario());
                        paridadImpl.edit(paridad);
                    }
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    public void cambiarValorCompania() {
        try {
            this.refrescarTabla();
            this.cargarParidadAnual();
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void cambiarValorMonedaOrigen() {
        try {
            cargarMonedasDes(newParidad.getMonedaOrigen());
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

}
