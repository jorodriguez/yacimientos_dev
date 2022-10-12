/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.moneda.bean.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.ejb.EJBException;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import sia.constantes.Constantes;
import sia.modelo.Compania;
import sia.modelo.Moneda;
import sia.modelo.Paridad;
import sia.modelo.ParidadValor;
import sia.servicios.sistema.vo.MonedaVO;
import sia.servicios.sistema.vo.ParidadAnual;
import sia.servicios.sistema.vo.ParidadVO;
import sia.servicios.sistema.vo.ParidadValorVO;
import sia.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named
@ViewScoped
public class ParidadBeanModel implements Serializable {

    @Inject
    private Sesion sesion;
//    
//    public void cargarCompanias() {
//        this.setCompanias(companiaImpl.traerCompaniasByUsuario(this.getSesion().getUsuario().getId()));
//    }
//
//    public void cargarMonedasOri() {
//        this.setMonedasOrigen(monedaImpl.traerMonedasPorCompaniaItems(this.getCompaniaSeleccionada(), null));
//    }
//
//    public void cargarMonedasDes(int idMonedaO) {
//        refrescarMonedaOrigen(idMonedaO);
//        this.setMonedasDestino(monedaImpl.traerMonedasPorCompaniaItems(this.getCompaniaSeleccionada(), getMonedaOrigen().getSiglas()));
//    }
//
//    public void refrescarTabla() {
//        this.setLstParidad(paridadImpl.traerParidad(this.getCompaniaSeleccionada(), 0, 0));
//    }
//
//    public void refrescarMonedaOrigen(int monedaOrID) {
//        List<MonedaVO> monedaLoc = monedaImpl.traerMonedasPorCompania(this.getCompaniaSeleccionada(), monedaOrID);
//        if (monedaLoc != null && monedaLoc.size() > 0) {
//            this.setMonedaOrigen(monedaLoc.get(0));
//        }
//    }
//
//    /**
//     * @return the sesion
//     */
//    public Sesion getSesion() {
//        return sesion;
//    }
//
//    /**
//     * @param sesion the sesion to set
//     */
//    public void setSesion(Sesion sesion) {
//        this.sesion = sesion;
//    }
//
//    /**
//     * @return the lstParidad
//     */
//    public List<ParidadVO> getLstParidad() {
//        return lstParidad;
//    }
//
//    /**
//     * @param lstParidad the lstParidad to set
//     */
//    public void setLstParidad(List<ParidadVO> lstParidad) {
//        this.lstParidad = lstParidad;
//    }
//
//    /**
//     * @return the newParidad
//     */
//    public ParidadVO getNewParidad() {
//        return newParidad;
//    }
//
//    /**
//     * @param newParidad the newParidad to set
//     */
//    public void setNewParidad(ParidadVO newParidad) {
//        this.newParidad = newParidad;
//    }
//
//    /**
//     * @return the companias
//     */
//    public List<SelectItem> getCompanias() {
//        return companias;
//    }
//
//    /**
//     * @param companias the companias to set
//     */
//    public void setCompanias(List<SelectItem> companias) {
//        this.companias = companias;
//    }
//
//    /**
//     * @return the paridadSeleccionada
//     */
//    public int getParidadSeleccionada() {
//        return paridadSeleccionada;
//    }
//
//    /**
//     * @param paridadSeleccionada the paridadSeleccionada to set
//     */
//    public void setParidadSeleccionada(int paridadSeleccionada) {
//        this.paridadSeleccionada = paridadSeleccionada;
//    }
//
//    /**
//     * @return the companiaSeleccionada
//     */
//    public String getCompaniaSeleccionada() {
//        return companiaSeleccionada;
//    }
//
//    /**
//     * @param companiaSeleccionada the companiaSeleccionada to set
//     */
//    public void setCompaniaSeleccionada(String companiaSeleccionada) {
//        this.companiaSeleccionada = companiaSeleccionada;
//    }
//
//    public void cargarParidad(int idParidad) {
//        List<ParidadVO> paridades = paridadImpl.traerParidad(this.companiaSeleccionada, 0, idParidad);
//        if (paridades.size() > 0) {
//            setNewParidad(paridades.get(0));
//        }
//    }
//    
//    public void cargarParidadValor(int idParidadValor) {
//        List<ParidadValorVO> paridades = paridadValorImpl.traerParidadValor(0, null, null, idParidadValor);
//        if (paridades.size() > 0) {
//            setNewParidadValorVO(paridades.get(0));
//        }
//    }
//
//    public void guardarParidad() {
//        try {
//            if (getNewParidad() != null && getCompaniaSelec() != null) {
//                if (getNewParidad().getId() > 0) {
//                    Paridad paridad = paridadImpl.find(getNewParidad().getId());
//                    boolean guardar = false;
//
//                    Moneda monedaOr = monedaImpl.find(this.getNewParidad().getMonedaOrigen());
//                    Moneda monedaDes = monedaImpl.find(this.getNewParidad().getMonedaDestino());
//                    if (paridad != null && paridad.getMoneda().getId() != getNewParidad().getMonedaOrigen()) {
//                        paridad.setMoneda(monedaOr);
//                        paridad.setNombre(monedaOr.getSiglas() + " -> " + monedaDes.getSiglas());
//                        guardar = true;
//                    }
//
//                    if (paridad != null && paridad.getMonedades().getId() != getNewParidad().getMonedaDestino()) {
//                        paridad.setMonedades(monedaDes);
//                        paridad.setNombre(monedaOr.getSiglas() + " -> " + monedaDes.getSiglas());
//                        guardar = true;
//                    }
//
//                    if (guardar) {
//                        paridadImpl.edit(paridad);
//                    }
//                } else {
//                    Moneda monedaOr = monedaImpl.find(this.getNewParidad().getMonedaOrigen());
//                    Moneda monedaDe = monedaImpl.find(this.getNewParidad().getMonedaDestino());
//                    Paridad paridad = new Paridad();
//                    paridad.setNombre(monedaOr.getSiglas() + " -> " + monedaDe.getSiglas());
//                    paridad.setFechaValido(this.getNewParidad().getFechaValido());
//                    paridad.setMoneda(monedaOr);
//                    paridad.setMonedades(monedaDe);
//                    paridad.setEliminado(Constantes.BOOLEAN_FALSE);
//                    paridad.setGenero(usuarioImpl.find(getSesion().getUsuario().getId()));
//                    paridad.setFechaGenero(new Date());
//                    paridad.setHoraGenero(new Date());
//                    paridad.setCompania(this.getCompaniaSelec());
//
//                    paridadImpl.create(paridad);
//                }
//            }
//
//        } catch (Exception e) {
//            UtilLog4j.log.fatal(e);
//        }
//    }
//    
//    public void guardarParidadValor() throws Exception {
//        try {
//            if (getNewParidadValorVO() != null && getCompaniaSelec() != null) {
//                if (getNewParidadValorVO().getId() > 0) {
//                    ParidadValor paridadValor = paridadValorImpl.find(getNewParidadValorVO().getId());
//                    boolean guardar = false;
//
//                    if (paridadValor != null && paridadValor.getValor() != getNewParidadValorVO().getValor()) {
//                        BigDecimal valor = new BigDecimal(getNewParidadValorVO().getValor());
//                        valor = valor.setScale(4, RoundingMode.DOWN);
//                        paridadValor.setValor(valor.doubleValue());                        
//                        guardar = true;
//                    }
//
//                    if (paridadValor != null && paridadValor.getFechaValido().getTime() != getNewParidadValorVO().getFechaValido().getTime()) {
//                        paridadValor.setFechaValido(getNewParidadValorVO().getFechaValido());                        
//                        guardar = true;
//                    }
//
//                    if (guardar) {
//                        paridadValorImpl.edit(paridadValor);
//                    }
//                } else {                    
//                    Paridad paridad = paridadImpl.find(this.getNewParidadValorVO().getIdParidad());
//                    ParidadValor paridadValor = new ParidadValor();                    
//                    paridadValor.setFechaValido(this.getNewParidadValorVO().getFechaValido());
//                    BigDecimal valor = new BigDecimal(getNewParidadValorVO().getValor());
//                    valor = valor.setScale(4, RoundingMode.DOWN);
//                    paridadValor.setValor(valor.doubleValue());                                            
//                    paridadValor.setEliminado(Constantes.BOOLEAN_FALSE);
//                    paridadValor.setGenero(usuarioImpl.find(getSesion().getUsuario().getId()));
//                    paridadValor.setFechaGenero(new Date());
//                    paridadValor.setHoraGenero(new Date());
//                    paridadValor.setParidad(paridad);
//                    paridadValorImpl.create(paridadValor);
//                }
//            }
//
//        } catch (EJBException e) {            
//            UtilLog4j.log.fatal(e);
//            throw new EJBException();
//        } catch (Exception e) {
//            UtilLog4j.log.fatal(e);
//            throw new Exception();
//        }
//    }
//
//    public void desactivarParidad() {
//        try {
//            if (getNewParidad() != null) {
//                if (getNewParidad().getId() > 0) {
//                    Paridad paridad = paridadImpl.find(getNewParidad().getId());
//                    if (paridad != null) {
//                        paridad.setEliminado(Constantes.BOOLEAN_TRUE);
//                        paridad.setFechaModifico(new Date());
//                        paridad.setHoraModifico(new Date());
//                        paridad.setModifico(getSesion().getUsuario());
//                        paridadImpl.edit(paridad);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            UtilLog4j.log.fatal(e);
//        }
//    }
//
//    /**
//     * @return the monedaOrigen
//     */
//    public MonedaVO getMonedaOrigen() {
//        return monedaOrigen;
//    }
//
//    /**
//     * @param monedaOrigen the monedaOrigen to set
//     */
//    public void setMonedaOrigen(MonedaVO monedaOrigen) {
//        this.monedaOrigen = monedaOrigen;
//    }
//
//    /**
//     * @return the companiaSelec
//     */
//    public Compania getCompaniaSelec() {
//        return companiaSelec;
//    }
//
//    /**
//     * @param companiaSelec the companiaSelec to set
//     */
//    public void setCompaniaSelec(Compania companiaSelec) {
//        this.companiaSelec = companiaSelec;
//    }
//
//    public void setCompaniaSelec() {
//        this.companiaSelec = companiaImpl.find(this.getCompaniaSeleccionada());
//    }
//
//    /**
//     * @return the monedasOrigen
//     */
//    public List<SelectItem> getMonedasOrigen() {
//        return monedasOrigen;
//    }
//
//    /**
//     * @param monedasOrigen the monedasOrigen to set
//     */
//    public void setMonedasOrigen(List<SelectItem> monedasOrigen) {
//        this.monedasOrigen = monedasOrigen;
//    }
//
//    /**
//     * @return the monedasDestino
//     */
//    public List<SelectItem> getMonedasDestino() {
//        return monedasDestino;
//    }
//
//    /**
//     * @param monedasDestino the monedasDestino to set
//     */
//    public void setMonedasDestino(List<SelectItem> monedasDestino) {
//        this.monedasDestino = monedasDestino;
//    }
//
//    /**
//     * @return the activeTab1
//     */
//    public String getActiveTab1() {
//        return activeTab1;
//    }
//
//    /**
//     * @param activeTab1 the activeTab1 to set
//     */
//    public void setActiveTab1(String activeTab1) {
//        this.activeTab1 = activeTab1;
//    }
//
//    /**
//     * @return the activeTab2
//     */
//    public String getActiveTab2() {
//        return activeTab2;
//    }
//
//    /**
//     * @param activeTab2 the activeTab2 to set
//     */
//    public void setActiveTab2(String activeTab2) {
//        this.activeTab2 = activeTab2;
//    }
//
//    /**
//     * @return the indexTab
//     */
//    public int getIndexTab() {
//        return indexTab;
//    }
//
//    /**
//     * @param indexTab the indexTab to set
//     */
//    public void setIndexTab(int indexTab) {
//        this.indexTab = indexTab;
//    }
//
//    /**
//     * @return the paridadAnual
//     */
//    public ParidadAnual getParidadAnual() {
//        return paridadAnual;
//    }
//
//    /**
//     * @param paridadAnual the paridadAnual to set
//     */
//    public void setParidadAnual(ParidadAnual paridadAnual) {
//        this.paridadAnual = paridadAnual;
//    }
//
//    /**
//     * @return the newParidadValorVO
//     */
//    public ParidadValorVO getNewParidadValorVO() {
//        return newParidadValorVO;
//    }
//
//    /**
//     * @param newParidadValorVO the newParidadValorVO to set
//     */
//    public void setNewParidadValorVO(ParidadValorVO newParidadValorVO) {
//        this.newParidadValorVO = newParidadValorVO;
//    }
}
