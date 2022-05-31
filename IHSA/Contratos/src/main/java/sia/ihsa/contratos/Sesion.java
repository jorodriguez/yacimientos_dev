/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.contratos;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sia.constantes.Constantes;
import sia.contrato.bean.backing.ContratoBean;
import sia.contrato.bean.soporte.FacesUtils;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.servicios.sistema.vo.MenuSiOpcionVo;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.util.UtilLog4j;
import sia.util.UtilSia;

/**
 *
 * @author ihsa
 */
@Named
@SessionScoped
public class Sesion implements Serializable {

    static final long serialVersionUID = 1;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    /**
     * Creates a new instance of Sesion
     */
    public Sesion() {
    }

    private UsuarioVO usuarioSesion;
    private String paginaInicio;

    private List<MenuSiOpcionVo> menu;
    private String rfcEmpresa;
    private int idRol;
    private List<CampoUsuarioPuestoVo> lista;
    @Getter
    @Setter
    private CampoUsuarioPuestoVo campoUsuarioPuestoVo;
    @Inject
    UsuarioImpl usuarioImpl;
    @Inject
    private ConvenioImpl convenioImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    private SiOpcionImpl siOpcionImpl;
    @Inject
    private ApCampoImpl apCampoImpl;

    /**
     * Cierra la sesión actual
     *
     * @param actionEvent
     */
    public void cerrarSesion() {
        setUsuarioSesion(null);
        menu = null;
        redireccionar(Constantes.URL_REL_SIA_SIGN_OUT);
    }

    public void siaGo() {
        redireccionar(Constantes.URL_REL_SIA_PRINCIPAL);
    }

    public String direccionar() {

        return getPaginaInicio();
    }

    public String sustituirArrancarModulo(String url) {
        if (!url.endsWith(".xhtml")) {
            url += ".xhtml";
        }
        return url + "?page-redirect=true";
    }

    /**
     * Redirecciona a la URL proporcionada.
     *
     * @param url
     */
    private void redireccionar(final String url) {
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String prefix = UtilSia.getUrl(origRequest);

        FacesContext fc = FacesContext.getCurrentInstance();

        try {
            fc.getExternalContext().redirect(prefix + url); // redirecciona la página
        } catch (IOException ex) {
            LOGGER.fatal("Error de IO al redireccionar", ex);
        }

    }

    public void cambioCampoUsuario(SelectEvent<CampoUsuarioPuestoVo> event) {
        CampoUsuarioPuestoVo con = (CampoUsuarioPuestoVo) event.getObject();
        usuarioImpl.cambiarCampoUsuario(con.getIdUsuario(), getUsuarioSesion().getId(), con.getIdCampo());
        //
        this.setUsuarioSesion(usuarioImpl.findById(con.getIdUsuario()));
        //
        PrimeFaces.current().executeScript(";$(dialogoUsuarioCampo).modal('hide');");
        //
        List<UsuarioRolVo> lu = siUsuarioRolImpl.traerRolPorUsuarioModulo(getUsuarioSesion().getId(), Constantes.MODULO_CONTRATO, con.getIdCampo());

        setRfcEmpresa(apCampoImpl.find(getUsuarioSesion().getIdCampo()).getCompania().getRfc());
        //
        List<SiOpcionVo> listaItems = siOpcionImpl.getAllSiOpcionBySiModulo(Constantes.MODULO_CONTRATO, getUsuarioSesion().getId(), getUsuarioSesion().getIdCampo());
        List<MenuSiOpcionVo> itemsReturn = new ArrayList<>();
        for (SiOpcionVo oldVO : listaItems) {
            MenuSiOpcionVo menuSiOpcionVo = new MenuSiOpcionVo();
            menuSiOpcionVo.setPadre(oldVO);
            List<SiOpcionVo> listaOpciones = siOpcionImpl.getChildSiOpcion(oldVO.getId(), getUsuarioSesion().getId(), Constantes.MODULO_CONTRATO);
            for (SiOpcionVo hijo : listaOpciones) {
                menuSiOpcionVo.getHijos().add(hijo);
            }
            itemsReturn.add(menuSiOpcionVo);
        }
        setMenu(itemsReturn);
        OUTER:
        for (UsuarioRolVo usuarioRolVo : lu) {
            switch (usuarioRolVo.getIdRol()) {
                case Constantes.ROL_ADMINISTRA_CONTRATO:
                    setIdRol(Constantes.ROL_ADMINISTRA_CONTRATO);
                    break OUTER;
                case Constantes.ROL_REVISA_CONTRATO:
                    setIdRol(Constantes.ROL_REVISA_CONTRATO);
                    break OUTER;
                case Constantes.ROL_CONSULTA_CONTRATO:
                    setIdRol(Constantes.ROL_CONSULTA_CONTRATO);
                    break OUTER;
                case Constantes.ROL_REGISTRA_PROVEEDOR:
                    setIdRol(Constantes.ROL_REGISTRA_PROVEEDOR);
                    break OUTER;
                default:
                    break;
            }
        }

        ContratoBean contratoBean = (ContratoBean) FacesUtils.getManagedBean(FacesContext.getCurrentInstance(), "contratoBean");
        contratoBean.setListaConvenios(new ArrayList<>());
        contratoBean.setListaConvenios(convenioImpl.traerConveniosPorProveedorPermisos(
                Constantes.CERO, usuarioSesion.getId(), getIdRol(), Constantes.CERO, Constantes.CERO, null,
                Constantes.CERO, usuarioSesion.getIdCampo(), 10, Constantes.CERO));
        redireccionar(Constantes.URL_REL_CONTRATO);
    }

    /**
     * @return the usuarioSesion
     */
    public UsuarioVO getUsuarioSesion() {
        return usuarioSesion;
    }

    /**
     * @param usuarioSesion the usuarioSesion to set
     */
    public void setUsuarioSesion(UsuarioVO usuarioSesion) {
        this.usuarioSesion = usuarioSesion;
    }

    /**
     * @return the menu
     */
    public List<MenuSiOpcionVo> getMenu() {
        return menu;
    }

    /**
     * @param menu the menu to set
     */
    public void setMenu(List<MenuSiOpcionVo> menu) {
        this.menu = menu;
    }

    /**
     * @return the paginaInicio
     */
    public String getPaginaInicio() {
        return paginaInicio;
    }

    /**
     * @param paginaInicio the paginaInicio to set
     */
    public void setPaginaInicio(String paginaInicio) {
        this.paginaInicio = paginaInicio;
    }

    /**
     * @return the rfcEmpresa
     */
    public String getRfcEmpresa() {
        return rfcEmpresa;
    }

    /**
     * @param rfcEmpresa the rfcEmpresa to set
     */
    public void setRfcEmpresa(String rfcEmpresa) {
        this.rfcEmpresa = rfcEmpresa;
    }

    /**
     * @return the idRol
     */
    public int getIdRol() {
        return idRol;
    }

    /**
     * @param idRol the idRol to set
     */
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    /**
     * @return the lista
     */
    public List<CampoUsuarioPuestoVo> getLista() {
        return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(List<CampoUsuarioPuestoVo> lista) {
        this.lista = lista;
    }

}
