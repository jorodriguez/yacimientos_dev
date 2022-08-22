/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.bean.backing;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.sgl.viaje.vo.InvitadoVO;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.viaje.bean.model.AdministrarViajeBeanModel;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "old_administrarViajeBean")
@RequestScoped
public class AdministrarViajeBean implements Serializable {

    @Inject
    AdministrarViajeBeanModel administrarViajeBeanModel;

    public AdministrarViajeBean() {

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

    public void iniciarConversasionCrearViaje() {
        cargarSolicitudesYViajes();
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

    public void goPopupCrearViaje() {
        administrarViajeBeanModel.inicializarCrearViaje();
        PrimeFaces.current().executeScript("$(dialogoPopUpCrearViaje).modal('show');");
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

    public void goPopUpAddOrRemoveViajeros(int idViaje) {
        administrarViajeBeanModel.llenarlistaViajeros(idViaje);
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

    public void removerViajero(String viajero, String usuario, String invitado) {
        administrarViajeBeanModel.removeViajeros(viajero, usuario, invitado);
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
        administrarViajeBeanModel.setInvitadoEmergente("");
    }

    public void agregarEmpleado() {
        administrarViajeBeanModel.agreagarEmpleadoOInvitadoEmergente(Constantes.TRUE);
        administrarViajeBeanModel.setEmpleadoEmergente("");
    }

    public void addAndOrRemoveViajeros() throws SIAException {
        administrarViajeBeanModel.addAndOrRemoveViajeros(Constantes.FALSE);
//        String metodo = ";draggableInit();";
//        PrimeFaces.current().executeScript(metodo);
    }

    public List<String> usuarioListener(String cadena) {
        administrarViajeBeanModel.setInvitadoEmergente("");
        List<String> nombres = new ArrayList<>();
        List<UsuarioVO> usVos = administrarViajeBeanModel.traerUsuarios(cadena);
        usVos.stream().forEach(us -> {
            nombres.add(us.getNombre());
        });
        return nombres;
    }

    public List<String> invitadoListener(String cadena) {
        administrarViajeBeanModel.setInvitadoEmergente("");
        List<String> nombres = new ArrayList<>();
        List<InvitadoVO> invs = administrarViajeBeanModel.traerInvitados(cadena);
        invs.stream().forEach(us -> {
            nombres.add(us.getNombre() + " // " + us.getEmpresa());
        });
        return nombres;
    }

    /**
     * @return the estatusViaje
     */
    public int getEstatusViaje() {
        return administrarViajeBeanModel.getEstatusViaje();
    }

    public void iniciarModifcarViaje(int idViaje) {
       // administrarViajeBeanModel.iniciarModificarViaje(idViaje);
        String metodo = ";abrirDialogModal(dialogoPopUpCrearViaje);";
        PrimeFaces.current().executeScript(metodo);
    }

    public void selecionarSolicitud(int idSol) {
        administrarViajeBeanModel.llenarlistaViajerosPorSolicitud(idSol);
    }

    public void moverViajeAPorSalir(int idV) {
        administrarViajeBeanModel.moverViaje(idV);
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

    public void crearViajeRegreso(int idVi) {
        try {
            administrarViajeBeanModel.crearRegreso(idVi);
//            String metodo = ";draggableInit();";
//            PrimeFaces.current().executeScript(metodo);
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

    public void agregarDeSV(int idViajero) {
        administrarViajeBeanModel.addViajeroConSV(idViajero);

    }

    public void agregarDeSVTodos() {
        int idSV = Integer.parseInt(FacesUtils.getRequestParameter("seleccionado"));
        administrarViajeBeanModel.addTodosSV(idSV);
    }

    public void cargarConductoresSGL() {
        if (administrarViajeBeanModel.isEmpleadosSgl()) {
            administrarViajeBeanModel.llenarListaEmpleadosSGL(Constantes.FALSE);
        } else {
            cargarConductoresTodos();
        }
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

    public void conInterception() {
        getViajeVO().setConInter(Constantes.TRUE);
    }

    public void sinInterception() {
        getViajeVO().setConInter(Constantes.FALSE);
    }

    public void eliminarViaje(int idV) {
        administrarViajeBeanModel.eliminarViajeById(idV);

    }

    /**
     * @return the redondoSencillo
     */
    public boolean isVehiculoSgl() {
        return administrarViajeBeanModel.isVehiculoSgl();
    }

    /**
     */
    public void setVehiculoSgl(boolean vehiculoSgl) {
        administrarViajeBeanModel.setVehiculoSgl(vehiculoSgl);
    }

    /**
     * @return the redondoSencillo
     */
    public boolean isEmpleadosSgl() {
        return administrarViajeBeanModel.isEmpleadosSgl();
    }

    /**
     */
    public void setEmpleadosSgl(boolean empleadosSgl) {
        administrarViajeBeanModel.setEmpleadosSgl(empleadosSgl);
    }

    /**
     * @return the listaEmpleadosSGL
     */
    public List<String> getListaEmpleadosSGL() {
        return administrarViajeBeanModel.getListaEmpleadosSGL();
    }

    /**
     * @param listaEmpleadosSGL the listaEmpleadosSGL to set
     */
    public void setListaEmpleadosSGL(List<String> listaEmpleadosSGL) {
        administrarViajeBeanModel.setListaEmpleadosSGL(listaEmpleadosSGL);
    }

    /**
     * @return the listaViajerosSolicitud
     */
    public List<ViajeroVO> getListaViajerosSolicitud() {
        return administrarViajeBeanModel.getListaViajerosSolicitud();
    }

    /**
     * @param listaViajerosSolicitud the listaViajerosSolicitud to set
     */
    public void setListaViajerosSolicitud(List<ViajeroVO> listaViajerosSolicitud) {
        administrarViajeBeanModel.setListaViajerosSolicitud(listaViajerosSolicitud);
    }

    /**
     * @return the destinos
     */
    public List<SelectItem> getDestinos() {
        return administrarViajeBeanModel.getDestinos();
    }

    /**
     * @param destinos the destinos to set
     */
    public void setDestinos(List<SelectItem> destinos) {
        administrarViajeBeanModel.setDestinos(destinos);
    }
}
