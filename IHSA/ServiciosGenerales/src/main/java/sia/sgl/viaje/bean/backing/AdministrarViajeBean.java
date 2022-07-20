/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.bean.backing;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.viaje.bean.model.AdministrarViajeBeanModel;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "administrarViajeBean")
@RequestScoped
public class AdministrarViajeBean implements Serializable {
    
    @ManagedProperty(value = "#{administrarViajeBeanModel}")
    private AdministrarViajeBeanModel administrarViajeBeanModel;
    
    public AdministrarViajeBean() {
        
    }

    /**
     * @param administrarViajeBeanModel the administrarViajeBeanModel to set
     */
    public void setAdministrarViajeBeanModel(AdministrarViajeBeanModel administrarViajeBeanModel) {
        this.administrarViajeBeanModel = administrarViajeBeanModel;
    }

    /**
     * @return the solicitudesViajeros
     */
    public List<SolicitudViajeVO> getSolicitudesViajeros() {
        return this.administrarViajeBeanModel.getSolicitudesViajeros();
    }

    /**
     * @param solicitudesViajeros the solViajeros to set
     */
    public void setSolicitudesViajeros(List<SolicitudViajeVO> solicitudesViajeros) {
        this.administrarViajeBeanModel.setSolicitudesViajeros(solicitudesViajeros);
    }

    /**
     * @return the viajesCreados
     */
    public List<ViajeVO> getViajesCreados() {
        return this.administrarViajeBeanModel.getViajesCreados();
    }

    /**
     * @param viajesCreados the viajesCreados to set 
     */
    public void setViajesCreados(List<ViajeVO> viajesCreados) {
        this.administrarViajeBeanModel.setViajesCreados(viajesCreados);
    }

    /**
     * @return the viajesProgramados
     */
    public List<ViajeVO> getViajesProgramados() {
        return this.administrarViajeBeanModel.getViajesProgramados();
    }

    /**
     * @param viajesProgramados the viajesProgramados to set
     */
    public void setViajesProgramados(List<ViajeVO> viajesProgramados) {
        this.administrarViajeBeanModel.setViajesProgramados(viajesProgramados);
    }

    /**
     * @return the viajesEnProceso
     */
    public List<ViajeVO> getViajesEnProceso() {
        return this.administrarViajeBeanModel.getViajesEnProceso();
    }

    /**
     * @param viajesEnProceso the viajesEnProceso to set
     */
    public void setViajesEnProceso(List<ViajeVO> viajesEnProceso) {
        this.administrarViajeBeanModel.setViajesEnProceso(viajesEnProceso);
    }
    
    public void iniciarConversasionCrearViaje(ActionEvent actionEvent) {
        this.cargarSolicitudesYViajes();
        String metodo = ";draggableInit();";
        PrimeFaces.current().executeScript(metodo);
    }
    
    public void cargarSolicitudesYViajes() {
        administrarViajeBeanModel.cargarSolicitudesYViajes();        
    }

    /**
     * @return the fechaInt1
     */
    public Date getFechaInt1() {
        return this.administrarViajeBeanModel.getFechaInt1();
    }

    /**
     * @param fechaInt1 the fechaInt1 to set
     */
    public void setFechaInt1(Date fechaInt1) {
        this.administrarViajeBeanModel.setFechaInt1(fechaInt1);
    }

    /**
     * @return the fechaInt2
     */
    public Date getFechaInt2() {
        return this.administrarViajeBeanModel.getFechaInt2();
    }

    /**
     * @param fechaInt2 the fechaInt2 to set
     */
    public void setFechaInt2(Date fechaInt2) {
        this.administrarViajeBeanModel.setFechaInt2(fechaInt2);
    }

    /**
     * @return the textBusqueda
     */
    public String getTextBusqueda() {
        return this.administrarViajeBeanModel.getTextBusqueda();
    }

    /**
     * @param textBusqueda the textBusqueda to set
     */
    public void setTextBusqueda(String textBusqueda) {
        this.administrarViajeBeanModel.setTextBusqueda(textBusqueda);
    }
    
    public String getUsrID() {
        return this.administrarViajeBeanModel.getUsrID();
    }
    
    public void goPopupCrearViaje(ActionEvent actionEvent) {
        administrarViajeBeanModel.inicializarCrearViaje();
        String metodo = ";abrirDialogModal(dialogoPopUpCrearViaje);";
        PrimeFaces.current().executeScript(metodo);
    }

    /**
     * @return the viajeVO
     */
    public ViajeVO getViajeVO() {
        return administrarViajeBeanModel.getViajeVO();
    }

    /**
     * @param viajeVO the viajeVO to set
     */
    public void setViajeVO(ViajeVO viajeVO) {
        administrarViajeBeanModel.setViajeVO(viajeVO);
    }

    /**
     * @return the horaSalida
     */
    public String getHoraSalida() {
        return administrarViajeBeanModel.getHoraSalida();
    }

    /**
     * @param horaSalida the horaSalida to set
     */
    public void setHoraSalida(String horaSalida) {
        administrarViajeBeanModel.setHoraSalida(horaSalida);
    }

    /**
     * @return the idOficinaOrigen
     */
    public int getIdOficinaOrigen() {
        return administrarViajeBeanModel.getIdOficinaOrigen();
    }

    /**
     * @param idOficinaOrigen the idOficinaOrigen to set
     */
    public void setIdOficinaOrigen(int idOficinaOrigen) {
        administrarViajeBeanModel.setIdOficinaOrigen(idOficinaOrigen);
    }

    /**
     * @return the listaOficinaVehiculo
     */
    public List<SelectItem> getListaOficina() {
        return administrarViajeBeanModel.getListaOficina();
    }

    /**
     * @return the listaVehiculos
     */
    public List<SelectItem> getListaVehiculos() {
        return administrarViajeBeanModel.getListaVehiculos();
    }

    /**
     * @return the listaRuta
     */
    public List<SelectItem> getListaRuta() {
        return administrarViajeBeanModel.getListaRuta();
    }

    /**
     * @return the idVehiculo
     */
    public int getIdVehiculo() {
        return administrarViajeBeanModel.getIdVehiculo();
    }

    /**
     * @param idVehiculo the idVehiculo to set
     */
    public void setIdVehiculo(int idVehiculo) {
        administrarViajeBeanModel.setIdVehiculo(idVehiculo);
    }

    /**
     * @return the idOficinaVehiculo
     */
    public int getIdOficinaVehiculo() {
        return administrarViajeBeanModel.getIdOficinaVehiculo();
    }

    /**
     * @param idOficinaVehiculo the idOficinaVehiculo to set
     */
    public void setIdOficinaVehiculo(int idOficinaVehiculo) {
        administrarViajeBeanModel.setIdOficinaVehiculo(idOficinaVehiculo);
    }

    /**
     * @return the idOficinaRuta
     */
    public int getIdOficinaRuta() {
        return administrarViajeBeanModel.getIdOficinaRuta();
    }

    /**
     * @param idOficinaRuta the idOficinaRuta to set
     */
    public void setIdOficinaRuta(int idOficinaRuta) {
        administrarViajeBeanModel.setIdOficinaRuta(idOficinaRuta);
    }
    
    public void tipoRedondo() {
        setRedondoSencillo(Constantes.TRUE);
        getViajeVO().setRedondo(Constantes.BOOLEAN_TRUE);
    }
    
    public void tipoSencillo() {
        setRedondoSencillo(Constantes.FALSE);
        getViajeVO().setRedondo(Constantes.BOOLEAN_FALSE);
    }
    
    public void cargarListaVehiculos(ValueChangeEvent event) {
        setIdOficinaVehiculo((Integer) event.getNewValue());
        
        if (getIdOficinaVehiculo() != -1) {
            String cv = FacesUtils.getRequestParameter("cargarVehiculos");
            if (cv != null && cv.equals("on")) {
                administrarViajeBeanModel.actualizarListaVehiculos();
            } else {
                administrarViajeBeanModel.tdosLosVehiculosByOficina();
            }
            
        }
        
    }
    
    public void cargarResponsableVehiculo(ValueChangeEvent event) {
        setIdVehiculo((Integer) event.getNewValue());
        if (getIdVehiculo() != -1) {
            administrarViajeBeanModel.traerResponsableVehiculo();
        }
        
    }

    /**
     * @return the redondoSencillo
     */
    public boolean isRedondoSencillo() {
        return administrarViajeBeanModel.isRedondoSencillo();
    }

    /**
     * @param redondoSencillo the redondoSencillo to set
     */
    public void setRedondoSencillo(boolean redondoSencillo) {
        administrarViajeBeanModel.setRedondoSencillo(redondoSencillo);
    }

    /**
     * @return the tieneResponsable
     */
    public boolean isTieneResponsable() {
        return administrarViajeBeanModel.isTieneResponsable();
    }
    
    public void cambiarResponsable() {
        administrarViajeBeanModel.cambiarResponsable();
    }
    
    public void crearViaje() throws ParseException {
        try {
            if (administrarViajeBeanModel.crearViaje()) {
                String metodo = ";cerrarDialogoCrearViaje();";
                PrimeFaces.current().executeScript(metodo);
            }            
            
        } catch (Exception e) {            
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
            UtilLog4j.log.fatal(e);
        }
    }
    
    public void actualizaHoraSalida() {
        administrarViajeBeanModel.actualizaHoraSalida();
    }
    
    public void goPopUpAddOrRemoveViajeros(ActionEvent actionEvent) {
        administrarViajeBeanModel.llenarlistaViajeros();
        administrarViajeBeanModel.usuariosActivos();
        administrarViajeBeanModel.listInvitados();
        String metodo = ";abrirDialogModal(dialogoPopUpAddOrRemoveViajeros);";
        PrimeFaces.current().executeScript(metodo);
    }

    /**
     * @return the listaViajeros
     */
    public List<ViajeroVO> getListaViajeros() {
        return administrarViajeBeanModel.getListaViajeros();
    }
    
    public void removerViajero() {
        administrarViajeBeanModel.removeViajeros();
    }

    /**
     * @return the invitadoEmergente
     */
    public String getInvitadoEmergente() {
        return administrarViajeBeanModel.getInvitadoEmergente();
    }

    /**
     * @param invitadoEmergente the invitadoEmergente to set
     */
    public void setInvitadoEmergente(String invitadoEmergente) {
        this.administrarViajeBeanModel.setInvitadoEmergente(invitadoEmergente);
    }

    /**
     * @return the empleadoEmergente
     */
    public String getEmpleadoEmergente() {
        return administrarViajeBeanModel.getEmpleadoEmergente();
    }

    /**
     * @param empleadoEmergente the empleadoEmergente to set
     */
    public void setEmpleadoEmergente(String empleadoEmergente) {
        administrarViajeBeanModel.setEmpleadoEmergente(empleadoEmergente);
    }
    
    public void agregarInvitado() {
        administrarViajeBeanModel.agreagarEmpleadoOInvitadoEmergente(Constantes.FALSE);
    }
    
    public void agregarEmpleado() {
        administrarViajeBeanModel.agreagarEmpleadoOInvitadoEmergente(Constantes.TRUE);
    }
    
    public void addAndOrRemoveViajeros() throws SIAException {
        administrarViajeBeanModel.addAndOrRemoveViajeros(Constantes.FALSE);
        String metodo = ";draggableInit();";
        PrimeFaces.current().executeScript(metodo);
    }

    /**
     * @return the estatusViaje
     */
    public int getEstatusViaje() {
        return administrarViajeBeanModel.getEstatusViaje();
    }
    
    public void iniciarModifcarViaje(ActionEvent actionEvent) {
        administrarViajeBeanModel.iniciarModificarViaje();
        String metodo = ";abrirDialogModal(dialogoPopUpCrearViaje);";
        PrimeFaces.current().executeScript(metodo);
    }
    
    public void moverViajeAPorSalir(){
        administrarViajeBeanModel.moverViaje();
    }

    /**
     * @return the modificar
     */
    public boolean isModificar() {
        return administrarViajeBeanModel.isModificar();
    }

    /**
     * @param modificar the modificar to set
     */
    public void setModificar(boolean modificar) {
        administrarViajeBeanModel.setModificar(modificar);
    }
    
    public void editarViaje() throws ParseException {
        try {
            administrarViajeBeanModel.modificarViaje();
            String metodo = ";cerrarDialogoCrearViaje();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        
    }
    
    public void crearViajeRegreso() {
        try {
            administrarViajeBeanModel.crearRegreso();
            String metodo = ";draggableInit();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }
    
    public void cargarListaConTodosLosVehiculosOficina() {
        if (getIdOficinaVehiculo() != -1) {
            administrarViajeBeanModel.tdosLosVehiculosByOficina();
        }
    }
    
    public void cargarVehiculos() {
        if (getIdOficinaVehiculo() != -1) {
            administrarViajeBeanModel.actualizarListaVehiculos();
        }
        
    }

    public void agregarDeSV() {
        int idViajero = Integer.parseInt(FacesUtils.getRequestParameter("seleccionado"));
        administrarViajeBeanModel.addViajeroConSV(idViajero);
        
    }

    public void agregarDeSVTodos() {
        int idSV = Integer.parseInt(FacesUtils.getRequestParameter("seleccionado"));
        administrarViajeBeanModel.addTodosSV(idSV);
    }
    
    public void cargarConductoresSGL() {
        administrarViajeBeanModel.llenarListaEmpleadosSGL(Constantes.FALSE);
    }
    
    public void cargarConductoresTodos() {
        administrarViajeBeanModel.llenarListaEmpleadosSGL(Constantes.TRUE);
    }

    /**
     * @return the ultimaActualizacion
     */
    public String getUltimaActualizacion() {
        return administrarViajeBeanModel.getUltimaActualizacion();
    }

    /**
     * @param ultimaActualizacion the ultimaActualizacion to set
     */
    public void setUltimaActualizacion(String ultimaActualizacion) {
        administrarViajeBeanModel.setUltimaActualizacion(ultimaActualizacion);
    }
    public void conInterception(){
        getViajeVO().setConInter(Constantes.TRUE);
    } 
    
    public void sinInterception(){
         getViajeVO().setConInter(Constantes.FALSE);
    }
    
    public void eliminarViaje(){
        administrarViajeBeanModel.eliminarViajeById();
        
    }
}
