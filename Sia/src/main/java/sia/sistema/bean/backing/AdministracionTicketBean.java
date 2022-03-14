/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sistema.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.vo.IncidenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.servicios.sgl.incidencia.vehiculo.impl.SiIncidenciaAdjuntoImpl;
import sia.servicios.sgl.incidencia.vehiculo.impl.SiIncidenciaImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.sistema.bean.support.FacesUtils;
import sia.util.TicketEstadoEnum;

/**
 *
 * @author mluis
 */
@ManagedBean
@ViewScoped
public class AdministracionTicketBean implements Serializable {

    /**
     * Creates a new instance of AdministracionTicketBean
     */
    public AdministracionTicketBean() {
    }

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;

    @EJB
    SiIncidenciaImpl incidenciaLocal;
    @EJB
    SiIncidenciaAdjuntoImpl incidenciaAdjuntoLocal;
    @EJB
    SiAdjuntoImpl adjuntoImpl;
    @EJB
    SiUsuarioRolImpl siUsuarioRolImpl;
    //
    private List<IncidenciaVo> incidencias;
    private List<AdjuntoVO> incidenciasAdjunto;
    private IncidenciaVo incidenciaVo;
    private String solucion, idAsignado;
    private List<SelectItem> asignados;
    private int duracion;

    //    
    @PostConstruct
    public void iniciar() {
        setIncidencias(new ArrayList<>());
        asignados = new ArrayList<>();
        //
        llenarIncidencias();
        //
        List<UsuarioRolVo> usrs = siUsuarioRolImpl.traerRolPorCodigo(Constantes.COD_ROL_SOPORTE_TECNICO, Constantes.AP_CAMPO_DEFAULT, Constantes.MODULO_ADMIN_SIA);
        for (UsuarioRolVo usr : usrs) {
            asignados.add(new SelectItem(usr.getIdUsuario(), usr.getUsuario()));
        }
    }

    private void llenarIncidencias() {
        setIncidencias(incidenciaLocal.traerPorStatus(TicketEstadoEnum.NUEVO.getId()));
        getIncidencias().addAll(incidenciaLocal.traerPorStatus(TicketEstadoEnum.ASIGNADO.getId()));
    }

    public void mostrarAdjuntos() {
        int idT = Integer.parseInt(FacesUtils.getRequestParameter("idTicket"));
        incidenciaVo = incidenciaLocal.buscarPorId(idT);
        incidenciasAdjunto = incidenciaAdjuntoLocal.traerArchivoPorIncidencia(idT);
        PrimeFaces.current().executeScript("$(dialogoAdjuntosTickt).modal('show');");
    }

    public void inicioAsignarTickect() {
        int idT = Integer.parseInt(FacesUtils.getRequestParameter("idTicket"));
        incidenciaVo = incidenciaLocal.buscarPorId(idT);
        //
        PrimeFaces.current().executeScript("$(dialogoAsignarTickt).modal('show');");
    }

    public void asignarTickect() {
        incidenciaLocal.asignarIncidencia(incidenciaVo, idAsignado, sesion.getUsuario());

        llenarIncidencias();
        PrimeFaces.current().executeScript("$(dialogoAsignarTickt).modal('hide');");
    }

    public void inicioFinalizarTickect() {
        int idT = Integer.parseInt(FacesUtils.getRequestParameter("idTicket"));
        incidenciaVo = incidenciaLocal.buscarPorId(idT);
        //
        PrimeFaces.current().executeScript("$(dialogoFinalizarTickt).modal('show');");

    }

    public void finalizarTickect() {
        incidenciaLocal.finalizarIncidencia(incidenciaVo, getSolucion(), getDuracion(), sesion.getUsuario());
        //
        llenarIncidencias();
        PrimeFaces.current().executeScript("$(dialogoFinalizarTickt).modal('hide');");
    }

    public void inicioEscalarTicket() {
        int idT = Integer.parseInt(FacesUtils.getRequestParameter("idTicket"));
        incidenciaVo = incidenciaLocal.buscarPorId(idT);
        //
        PrimeFaces.current().executeScript("$(dialogoEscalarTickt).modal('show');");
    }

    public void escalarTickect() {
        incidenciaLocal.escalarIncidencia(incidenciaVo, sesion.getUsuario().getId());
        llenarIncidencias();
        //
        PrimeFaces.current().executeScript("$(dialogoEscalarTickt).modal('hide');");
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the incidencias
     */
    public List<IncidenciaVo> getIncidencias() {
        return incidencias;
    }

    /**
     * @param incidencias the incidencias to set
     */
    public void setIncidencias(List<IncidenciaVo> incidencias) {
        this.incidencias = incidencias;
    }

    /**
     * @return the incidenciasAdjunto
     */
    public List<AdjuntoVO> getIncidenciasAdjunto() {
        return incidenciasAdjunto;
    }

    /**
     * @param incidenciasAdjunto the incidenciasAdjunto to set
     */
    public void setIncidenciasAdjunto(List<AdjuntoVO> incidenciasAdjunto) {
        this.incidenciasAdjunto = incidenciasAdjunto;
    }

    /**
     * @return the incidenciaVo
     */
    public IncidenciaVo getIncidenciaVo() {
        return incidenciaVo;
    }

    /**
     * @param incidenciaVo the incidenciaVo to set
     */
    public void setIncidenciaVo(IncidenciaVo incidenciaVo) {
        this.incidenciaVo = incidenciaVo;
    }

    /**
     * @return the solucion
     */
    public String getSolucion() {
        return solucion;
    }

    /**
     * @param solucion the solucion to set
     */
    public void setSolucion(String solucion) {
        this.solucion = solucion;
    }

    /**
     * @return the idAsignado
     */
    public String getIdAsignado() {
        return idAsignado;
    }

    /**
     * @param idAsignado the idAsignado to set
     */
    public void setIdAsignado(String idAsignado) {
        this.idAsignado = idAsignado;
    }

    /**
     * @return the asignados
     */
    public List<SelectItem> getAsignados() {
        return asignados;
    }

    /**
     * @param asignados the asignados to set
     */
    public void setAsignados(List<SelectItem> asignados) {
        this.asignados = asignados;
    }
    
    public void refrescarTickets() {
        this.llenarIncidencias();
    }

    /**
     * @return the duracion
     */
    public int getDuracion() {
        return duracion;
    }

    /**
     * @param duracion the duracion to set
     */
    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

}
