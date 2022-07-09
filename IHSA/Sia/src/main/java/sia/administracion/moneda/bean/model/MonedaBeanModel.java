/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.moneda.bean.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
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

@Named
@ViewScoped
public class MonedaBeanModel implements Serializable {


//        public void refrescarTabla() {
//            this.setLstMoneda(monedaImpl.traerMonedasPorCompania(this.getCompaniaSeleccionada(), 0));
//        }
//
//        public void cargarCompanias() {
//            this.setCompanias(companiaImpl.traerCompaniasByUsuario(this.getSesion().getUsuario().getId()));
//        }
//
//        public void cargarMoneda(int idMoneda) {
//            List<MonedaVO> monedas = monedaImpl.traerMonedasPorCompania(this.getSesion().getRfcCompania(), idMoneda);
//            if (monedas.size() > 0) {
//                setNewMoneda(monedas.get(0));
//            }
//        }
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
//     * @return the lstMoneda
//     */
//    public List<MonedaVO> getLstMoneda() {
//        return lstMoneda;
//    }
//
//    /**
//     * @param lstMoneda the lstMoneda to set
//     */
//    public void setLstMoneda(List<MonedaVO> lstMoneda) {
//        this.lstMoneda = lstMoneda;
//    }
//
//    /**
//     * @return the newMoneda
//     */
//    public MonedaVO getNewMoneda() {
//        return newMoneda;
//    }
//
//    /**
//     * @param newMoneda the newMoneda to set
//     */
//    public void setNewMoneda(MonedaVO newMoneda) {
//        this.newMoneda = newMoneda;
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
//    public void guardarMoneda() {
//        
//    }
//
//    public void desactivarMoneda() {
//        try {
//            
//        } catch (Exception e) {
//            UtilLog4j.log.fatal(e);
//        }
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
}
