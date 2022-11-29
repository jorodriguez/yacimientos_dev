/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sistema.bean.backing;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionListener;
import javax.inject.Inject;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.requisicion.vo.RequisicionView;
import sia.servicios.requisicion.impl.RequisicionImpl;
import static sia.constantes.Constantes.RUTA_COMPRAS_DESDE_REQ;
import static sia.constantes.Constantes.RUTA_SGL_MODULO;
import sia.modelo.orden.vo.OrdenView;
import sia.modelo.sgl.vo.AccesosDirectosView;
import sia.modelo.sgl.vo.SolicitudViajeView;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sistema.impl.SiModuloImpl;
import sia.servicios.sistema.vo.SiModuloVo;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author jorodriguez
 */
@Named
@ViewScoped
public class PrincipalViewBean implements Serializable {

    @Inject
    private Sesion sesion;

    @Inject
    private RequisicionImpl requisicionImpl;

    @Inject
    private OrdenImpl ordenImpl;

    @Inject
    private SgSolicitudViajeImpl solicitudViajeImpl;

    @Getter
    private List<RequisicionView> listaRequisiciones;

    @Getter
    private List<OrdenView> listaOrdenes;

    @Getter
    private List<SolicitudViajeView> listaSolicitudesViaje;

    @Getter
    private List<AccesosDirectosView> listaAccesos;

    @Getter
    @Setter
    private RequisicionView requisicion;

    @Getter
    @Setter
    private String consecutivo;

    @Getter
    @Setter
    private BusquedaEnum tipoBusqueda;

    @Getter
    @Setter
    private Boolean modalBusqueda;
    
    @Inject
    private SiModuloImpl siModuloImpl;

    @Getter
    @Setter
     private List<SiModuloVo> listaModulos;
    
    private enum BusquedaEnum {
        REQUISICION, ORDEN, PEDIDO, VIAJE
    };

    public PrincipalViewBean() {
    }

    @PostConstruct
    public void iniciar() {
        System.out.println("@Postconstruc");
        //loaders
        this.iniciarInformacionModulos();        
        this.cargarListas();
        this.modalBusqueda = false;
    }

    private void cargarListas() {
        this.listaRequisiciones = requisicionImpl.getUltimasRequisicionesModificadas(sesion.getUsuarioVo().getId(), sesion.getUsuarioVo().getIdCampo());
        this.listaOrdenes = ordenImpl.getUltimasOrdenesModificadas(sesion.getUsuarioVo().getId(), sesion.getUsuarioVo().getIdCampo());
        //this.listaSolicitudesViaje = solicitudViajeImpl.getUltimasSolicitudesViaje(sesion.getUsuarioVo().getId(),sesion.getUsuarioVo().getIdCampo());
        this.cargarAccesosDirectos();
    }

    private void cargarAccesosDirectos() {
        this.listaAccesos = Arrays.asList(
                AccesosDirectosView.builder()
                        .etiqueta("Crear una Requisición")
                        .icono("fa-edit").rutaModulo(getRutaModuloComprasDeRequ())
                        .rutaOpcion(getUrlSolicitarRequisicion())
                        .build(),
                AccesosDirectosView.builder()
                        .etiqueta("Crear una solicitud de viaje")
                        .icono("fa-car-side").rutaModulo(RUTA_SGL_MODULO)
                        .rutaOpcion(getUrlCrearSolicitudViaje())
                        .build()
        );
    }

    public void buscar(ActionListener actionListener) {

        if (this.consecutivo.isBlank() || this.consecutivo.isEmpty()) {
            FacesUtils.addErrorMessage("Es requerido escribir el código");
            return;
        }

        tipoBusqueda = BusquedaEnum.REQUISICION;

        this.requisicion = requisicionImpl.buscarConsecutivo(this.consecutivo, sesion.getUsuarioVo().getId());

        if (this.requisicion != null) {
            //PrimeUtils.executeScript("$('#modal_busqueda_requi').modal('show')");            
            return;
        }

        this.modalBusqueda = true;
    }


    /*
        FIXME: se queda en este punto debido a la fecha de entrega de la liberacion
        falta: agregar el parametro en el servlet para pasar el codigo de la requi, orden o pedido  y subirlo al contexto para que en 
        en la pantalla de busqueda estilosBean -> obtener el valor
        */

    /*public void buscar() {

        System.out.println("@Buscar-----");

        if (this.consecutivo.isBlank() || this.consecutivo.isEmpty()) {
            System.out.println("es null la requi");
            FacesUtils.addErrorMessage("Es requerido escribir el código");
            return;
        }
        

        tipoBusqueda = BusquedaEnum.REQUISICION;

        this.requisicion = requisicionImpl.buscarConsecutivo(this.consecutivo, sesion.getUsuarioVo().getId());

        if (this.requisicion != null) {
            System.out.println("se encontro la requi");

            this.modalBusqueda = true;

            if (requisicion.getTieneRolConsulta()) {
                //redireccionar al modulo

                //sesion.sustituirArrancarModulo(getRutaModuloComprasDeRequ());
                String redirect = sesion.sustituirArrancarModuloPorCampo(getRutaModuloComprasDeRequ(), requisicion.getApCampo(), getUrlConsultaRequicisionOrden());
                
                redireccionar(redirect+"&ZWZPF=R"+consecutivo);               

            }

            //return;
        }

    }*/

    private void redireccionar(String url) {
        try {
            System.out.println("redirect " + url);
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException ex) {
            Logger.getLogger(PrincipalViewBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // validar que el campo al que pertenece la requisicion 
    // esta dentro de los campos asignados al usuario en sesion    
    private boolean validarAccesoCampo(int campoComparar) {

        //boolean existe =  sesion.getCamposPorUsuario().stream().anyMatch(item -> item.getIdCampo() == campoComparar);
        boolean tieneAcceso = sesion.getCamposPorUsuario().stream().filter(item -> Objects.equals(item.getIdCampo(), campoComparar)).findFirst().isPresent();

        return tieneAcceso;

    }

    public void cerrarModalBusqueda() {
        this.modalBusqueda = false;
    }

    public void abrirModalBusqueda() {
        this.modalBusqueda = true;
    }

    public Boolean getModal() {
        return this.modalBusqueda;
    }

    public void seleccionarRequisicion(ActionListener actionListener) {

        String param = FacesUtils.getRequestParameter("indexRequisicion");

        int paramInx = Integer.parseInt(param);

        this.requisicion = this.listaRequisiciones.get(paramInx);
    }

    public void seleccionarRequisicionRow(RequisicionView row) {

        this.requisicion = row;

    }

    public String getRutaModuloComprasDeRequ() {
        return RUTA_COMPRAS_DESDE_REQ;
    }

    public String getUrlSolicitarRequisicion() {
        return "/vistas/SiaWeb/Requisiciones/CrearRequisicion.xhtml";
    }

    public String getUrlConsultaRequicisionOrden() {
        return "/vistas/SiaWeb/Requisiciones/DetalleHistorial.xhtml";
    }

    public String getRutaModuloSolicitaOrdenDeRequ() {
        //falta
        return RUTA_COMPRAS_DESDE_REQ;
    }

    public String getUrlSolicitarOrden() {
        return "/vistas/SiaWeb/Requisiciones/CrearRequisicion.xhtml";
    }

    public String getUrlCrearSolicitudViaje() {
        return "/vistas/sgl/viaje/solicitud/solicitudViaje.xhtml";
    }
    
    /********* pantalla principal *************/
    
     public void iniciarInformacionModulos() {
        
        listaModulos = siModuloImpl.getModulosUsuario(sesion.getUsuarioVo().getId(), 0);
        
    }

    

}
