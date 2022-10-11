/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;

import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import sia.constantes.Constantes;
import sia.ihsa.utils.FacesUtilsBean;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.vo.CompaniaVo;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.convenio.impl.CvConvenioFormasImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sistema.impl.DdSesionImpl;
import sia.util.OrdenEstadoEnum;
import org.primefaces.PrimeFaces;
import sia.util.Env;

/**
 *
 * @author mluis
 */
@Named(value = "sesion")
@SessionScoped
@Slf4j
public class Sesion implements Serializable {

    @EJB
    ProveedorServicioImpl proveedorImpl;
    @EJB
    OrdenImpl ordenImpl;
    @EJB
    CompaniaImpl companiaImpl;

    @EJB
    private DdSesionImpl sesionImpl;

    @EJB
    CvConvenioFormasImpl convenioFormasImpl;

    //
    @Getter
    @Setter
    private ProveedorVo proveedorVo;
    @Getter
    @Setter
    private CompaniaVo companiaVoSesion;
    @Getter
    @Setter
    private String rfcCompania;
    @Getter
    @Setter
    private String rfc;
    @Getter
    @Setter
    private String clave;
    @Getter
    @Setter
    private String claveNueva;
    @Getter
    @Setter
    private Map<String, String> requisitosFactura;
    @Getter
    @Setter
    private List<SelectItem> empresas;
    private ProveedorVo pVo;
    @Getter
    @Setter
    private String mensajeClave;
    @Getter
    @Setter
    private long formasPendientes;
    @Getter
    private Properties ctx;
    public static final String USER = "user";

    @PostConstruct
    public void iniciar() {
        ctx = new Properties();
    }

    private void subirValoresContexto() {
        Env.setContext(ctx, Env.SESSION_ID, SessionUtils.getSession().getId());
        Env.setContext(ctx, Env.CLIENT_INFO, SessionUtils.getClientInfo(SessionUtils.getRequest()));
        Env.setContext(ctx, Env.PUNTO_ENTRADA, "Proveedor");
    }

    public String login() {
        String pagina = "";
        pVo = proveedorImpl.traerProveedorPorRfc(rfc, clave, Constantes.CERO, Constantes.VACIO);
        if (pVo == null) {
            FacesUtilsBean.addErrorMessage("El rfc o la clave del proveedor está incorrecta");
        } else {
            if (pVo.isPrimerSesion()) {
                proveedorVo = pVo;
                requisitosFactura = new HashMap<>();
                empresas = new ArrayList<>();

                List<CompaniaVo> coms = ordenImpl.empresasPorProveedor(proveedorVo.getIdProveedor(), OrdenEstadoEnum.RECIBIDA_PARCIAL.getId(), OrdenEstadoEnum.POR_RECIBIR_FACTURA.getId());

                if (coms.isEmpty()) {
                    empresas.add(new SelectItem("VACIO", "No hay empresa"));
                } else {
                    rfcCompania = coms.get(0).getRfcCompania();
                    companiaVoSesion = companiaImpl.traerPorRFC(rfcCompania);
                }

                for (CompaniaVo com : coms) {
                    empresas.add(new SelectItem(com.getRfcCompania(), com.getNombre()));
                }

                if (companiaVoSesion != null) {
                    PrimeFaces.current().executeScript("$(dialogoMensaje).modal('show');");
                }

                guardarDatosSesion();

                SessionUtils.setAttribute(USER, proveedorVo);
                ctx = new Properties();
                subirValoresContexto();
                formasPendientes = convenioFormasImpl.totalFormasSinValidar(proveedorVo.getIdProveedor());
                pVo = null;
                pagina = "principal.xhml";
            } else {
                PrimeFaces.current().executeScript(";$(dialogoPrimerSesion).modal('show');");
            }

            // TODO : registrar datos de la sesion actual, usuario y datos del cliente web
            // será necesario crear un servicio específico para poder guardar las sesiones
            // tanto del proveedor como de los clientes internos de IHSA
        }
        return pagina;
    }

    public void seleccionarEmpresa(AjaxBehaviorEvent event) {
        companiaVoSesion = companiaImpl.traerPorRFC(rfcCompania);
    }

    public String refrescarPrincipalFactura() {
        PrimeFaces.current().executeScript("$(dialogoMensajePrincipal).modal('hide');");
        //

        return "/principal.xhtml?faces-redirect=true";
    }

    public String irPrincipalFactura() {
        PrimeFaces.current().executeScript("$(dialogoMensaje).modal('hide');");
        return "/principal.xhtml?faces-redirect=true";
    }

    public String cerrarSesion() {
        //registrar salida normal del sistema
        sesionImpl.registrarSalida(FacesUtilsBean.getHttpSession(false).getId(), proveedorVo.getRfc());

//        FacesUtilsBean.getHttpSession(false).invalidate();
        proveedorVo = null;
        companiaVoSesion = null;
        return "/principal.xhtml?faces-redirect=true";
    }

    public void cambioClave() {
        if (getClave().equals(getClaveNueva())) {
            proveedorImpl.cambiarPass(proveedorVo.getIdProveedor(), getClaveNueva());
            PrimeFaces.current().executeScript(";$(dialogoCambiarClave).modal('hide');");
            mensajeClave = "";
        } else {
            mensajeClave = "Las claves no son iguales";
        }
    }

    public void cerrarCambioClave() {
        PrimeFaces.current().executeScript(";$(dialogoCambiarClave).modal('hide');");
        mensajeClave = "";
    }

    public void cambioClavePrimerSesion() {
        if (getClave().equals(getClaveNueva())) {
            proveedorImpl.cambiarPass(pVo.getIdProveedor(), getClaveNueva());
            PrimeFaces.current().executeScript(";$(dialogoPrimerSesion).modal('hide');");
            pVo = null;
        } else {
            FacesUtilsBean.addErrorMessage("msgClavePrimer", "Las claves no coinciden.");
        }
    }

    private void guardarDatosSesion() {
        sia.modelo.sistema.vo.Sesion datosSesion
                = sia.modelo.sistema.vo.Sesion.builder()
                        .sesionId(FacesUtilsBean.getHttpSession(false).getId())
                        .datosCliente(FacesUtilsBean.getClientInfo(FacesUtilsBean.getRequest(FacesUtilsBean.getExternalContext())))
                        .puntoAcceso(FacesUtilsBean.getServletContext().getContextPath())
                        .genero(pVo.getRfc())
                        .build();

        sesionImpl.guardarSesion(datosSesion);

        log.info("*** Client info : Vendor {} , browser {}", datosSesion);
    }
}
