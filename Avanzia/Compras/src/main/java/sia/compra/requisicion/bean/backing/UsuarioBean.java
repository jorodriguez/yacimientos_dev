/**
 * UsuarioBean.java
 *
 * Creada el 9/06/2009, 05:36:48 PM
 *
 * Clase desarrollada por: Héctor Acosta Sierra para la compañia: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, actualizaciones o mejoras
 * enviar un correo a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;

import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import sia.compra.orden.bean.backing.NotaOrdenBean;
import sia.compra.orden.bean.backing.OrdenBean;
import sia.compra.sistema.bean.backing.ContarBean;
import sia.constantes.Constantes;
import sia.modelo.Compania;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;
import sia.util.UtilSia;

/**
 * @author Héctor Acosta Sierra @versión 1.0
 * @author-mail hacosta.0505@gmail.com @date 9/06/2009
 */
@Named(value = "usuarioBean")
@SessionScoped
public class UsuarioBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    @Inject
    private UsuarioImpl usuarioServicioRemoto;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    //
    @Inject
    CadenasMandoBean cadenasMandoBean;
    private String usuario, clave, accionUsuario, identificacion;
    //
    @Getter
    @Setter
    private Usuario usuarioConectado;
    private Compania compania;
    //
    private boolean cambioCampo = false;
    private String origenPeticion;
    //
    @Getter
    @Setter
    private String paginaInicial;
    //
    private String puesto;
    //
    private List<CampoUsuarioPuestoVo> listaCampo;
    private Map<String, Boolean> mapaRoles;

    /**
     * Creates a new instance of ManagedBeanUsuario
     */
    public UsuarioBean() {
    }

    public void iniciar() {
        direccionar();
    }

    public void cambiarCampo(int idCampo, String nombrePuesto) {
        usuarioServicioRemoto.cambiarCampoUsuario(usuarioConectado.getId(), usuarioConectado.getId(), idCampo);
        usuarioConectado = usuarioServicioRemoto.find(usuarioConectado.getId());

        if (nombrePuesto != null && !nombrePuesto.isEmpty()) {
            setPuesto(nombrePuesto);
        }

        setCompania(usuarioConectado.getApCampo().getCompania());
        RequisicionBean requisicionBean = (RequisicionBean) FacesUtilsBean.getManagedBean("requisicionBean");
//        RecepcionRequisicionBean recepcionRequisicionBean = (RecepcionRequisicionBean) FacesUtilsBean.getManagedBean("recepcionRequisicionBean");
//        recepcionRequisicionBean.setActualizar(true);
        OrdenBean ordenBean = (OrdenBean) FacesUtilsBean.getManagedBean("ordenBean");
        ordenBean.setOrdenActual(null);

        //Listas de historial
        requisicionBean.setRequisicionesSolicitadas(null);
        requisicionBean.setRequisicionesRevisadas(null);
        requisicionBean.setRequisicionesAprobadas(null);
        requisicionBean.setRequisicionesAprobadas(null);
        requisicionBean.setRequisicionesAutorizadas(null);
        requisicionBean.setRequisicionesVistoBueno(null);
        requisicionBean.setRequisicionesAsignadas(null);
        requisicionBean.setRequisicionActual(null);

        llenarRoles();
//
        NotaOrdenBean notaOrdenBean = (NotaOrdenBean) FacesUtilsBean.getManagedBean("notaOrdenBean");
        notaOrdenBean.setNotaActual(null);
        notaOrdenBean.setListaNotas(null);
        setCambioCampo(false);
        //
        ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
        contarBean.taerPendiente();
        //
        redireccionar(Constantes.URL_REL_SIA_WEB);
    }

    /**
     * Realiza limpieza de información de sesion y elimina la sesión actual.
     *
     */
    private void eliminarSesionActual() {
        LOGGER.info(this, "@eliminarSesionActual");
        // eliminar bean de usuario de sesion
        this.usuarioConectado = null;

        // realizar limpieza de informacion
        RequisicionBean requisicionBean = (RequisicionBean) FacesUtilsBean.getManagedBean("requisicionBean");
        requisicionBean.setRequisicionesAprobadas(null);
        requisicionBean.setRequisicionesSolicitadas(null);
        requisicionBean.setRequisicionesRevisadas(null);
        requisicionBean.setRequisicionesAutorizadas(null);
        requisicionBean.setRequisicionesAsignadas(null);
        requisicionBean.setPanelSeleccionado(0);
    }

    /**
     * Redirecciona a la URL proporcionada.
     *
     * @param url
     */
    private void redireccionar(final String url) {
        /*
	 * FacesContext fc = FacesContext.getCurrentInstance(); try {
	 * fc.getExternalContext().redirect(url);//redirecciona la página }
	 * catch (IOException ex) { logger.log(Level.SEVERE, null, ex); }
         */
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String prefix = UtilSia.getUrl(origRequest);

        FacesContext fc = FacesContext.getCurrentInstance();

        try {
            fc.getExternalContext().redirect(prefix + url); // redirecciona la página
        } catch (IOException ex) {
            LOGGER.info(this, "Error de IO al redireccionar: {0}", new Object[]{url}, ex);
        }

    }

    private void log(String mensaje) {
        LOGGER.info(this, mensaje);
    }

    /**
     * Cierra la sesión actual de SiaWeb y la del sistema Sia.
     *
     * @param
     */
    public void cerrarSesion() {
        eliminarSesionActual();
        redireccionar(Constantes.URL_REL_SIA_SIGN_OUT);
    }

    /**
     * Redirige a la página principal del sistema Sia. Se remueve la sesión
     * actual del sistema SiaWeb para prevenir accesos directos por medio de
     * URL.
     *
     * @param
     */
    public void siaGo() {
        eliminarSesionActual();
        redireccionar(Constantes.URL_REL_SIA_PRINCIPAL);
    }

    public Usuario buscarPorId(String idUsuario) {
        return usuarioServicioRemoto.find(idUsuario);
    }

    public Usuario buscarPorNombre(Object nombre) {
        return usuarioServicioRemoto.buscarPorNombre(nombre);
    }

    public void mostrarPanel() {
        PopupOlvidoClave popupOlvidoClave = (PopupOlvidoClave) FacesUtilsBean.getManagedBean("popupOlvidoClave");
        if (this.getUsuarioConectado() != null) {
            popupOlvidoClave.toggleModal();
        }
    }

    // ------ Listas  --------------------
    /**
     * @param idUsuario
     * @param revisa
     * @return Lista de usuarios Que Aprueban Requisiciones
     */
    public List<SelectItem> getListaAprueban(String idUsuario, String revisa) {
        return cadenasMandoBean.getListaAprueban(idUsuario, revisa, getUsuarioConectado().getApCampo().getId());
    }

    /**
     * @param solicita
     * @return Lista de usuarios Que Revisan Requisiciones
     */
    public List<SelectItem> listaRevisa(String solicita) {
        return cadenasMandoBean.traerRevisan(solicita, getUsuarioConectado().getApCampo().getId());
    }

    /**
     * @return Lista de usuarios Que Colocan orden de compra y o servicio
     */
    public List<SelectItem> getListaAnalista() {
        List<SelectItem> resultList = new ArrayList<>();
        try {
            //List<Usuario> tempList = usuarioServicioRemoto.getAnalistas();

            List<UsuarioVO> tempList = usuarioServicioRemoto.traerListaRolPrincipalUsuarioRolModulo(Constantes.ROL_COMPRADOR, Constantes.MODULO_COMPRA, usuarioConectado.getApCampo().getId());
            for (UsuarioVO usuarioVO : tempList) {
                SelectItem item = new SelectItem(usuarioVO.getId(), usuarioVO.getNombre());
                // esta linea es por si quiero agregar mas de un valoritem.setValue(Lista.getId());
                resultList.add(item);
            }

            return resultList;
        } catch (RuntimeException ex) {
            LOGGER.fatal(this, "Error  : :  :" + ex.getMessage());
        }
        return resultList;
    }

    public void cambiarUsuarioCampo() {
        setCambioCampo(true);
    }

    public void cerrarCambioCampo() {
        setCambioCampo(false);
    }

    /**
     * @return the Usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param Usuario the Usuario to set
     */
    public void setUsuario(String Usuario) {
        this.usuario = Usuario;
    }

    /**
     * @return the Clave
     */
    public String getClave() {
        return clave;
    }

    /**
     * @param Clave the Clave to set
     */
    public void setClave(String Clave) {
        this.clave = Clave;
    }

    /**
     * @return the AccionUsuario
     */
    public String accionUsuario() {
        return accionUsuario;
    }

   
    /**
     * @return the Identificacion
     */
    public String getIdentificacion() {
        return identificacion;
    }

    /**
     * @param Identificacion the Identificacion to set
     */
    public void setIdentificacion(String Identificacion) {
        this.identificacion = Identificacion;
    }

    /**
     * @return the cambioCampo
     */
    public boolean isCambioCampo() {
        return cambioCampo;
    }

    /**
     * @param cambioCampo the cambioCampo to set
     */
    public void setCambioCampo(boolean cambioCampo) {
        this.cambioCampo = cambioCampo;
    }

    public String getOrigenPeticion() {
        return origenPeticion;
    }

    public void setOrigenPeticion(String origenPeticion) {
        this.origenPeticion = origenPeticion;
    }

    /**
     * @return the compania
     */
    public Compania getCompania() {
        return compania;
    }

    /**
     * @param compania the compania to set
     */
    public void setCompania(Compania compania) {
        this.compania = compania;
    }

    public String direccionar() {
        return getPaginaInicial();
    }

    public void llenarRoles() {

        //.println("siUsuarioRolImpl " + siUsuarioRolImpl);

        setMapaRoles(new HashMap<>());
        List<UsuarioRolVo> rolUsuario = siUsuarioRolImpl.traerRolPorUsuarioModulo(usuarioConectado.getId(), Constantes.MODULO_REQUISICION, usuarioConectado.getApCampo().getId());
        rolUsuario.addAll(siUsuarioRolImpl.traerRolPorUsuarioModulo(usuarioConectado.getId(), Constantes.MODULO_COMPRA, usuarioConectado.getApCampo().getId()));
        rolUsuario.addAll(siUsuarioRolImpl.traerRolPorUsuarioModulo(usuarioConectado.getId(), Constantes.MODULO_CONTRATO, usuarioConectado.getApCampo().getId()));
        for (UsuarioRolVo usuarioRolVo : rolUsuario) {
            getMapaRoles().put(usuarioRolVo.getNombreRol(), Boolean.TRUE);
        }
    }

    /**
     * @return the puesto
     */
    public String getPuesto() {
        return puesto;
    }

    /**
     * @param puesto the puesto to set
     */
    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    /**
     * @return the listaCampo
     */
    public List<CampoUsuarioPuestoVo> getListaCampo() {
        return listaCampo;
    }

    /**
     * @param listaCampo the listaCampo to set
     */
    public void setListaCampo(List<CampoUsuarioPuestoVo> listaCampo) {
        this.listaCampo = listaCampo;
    }

    /**
     * @return the mapaRoles
     */
    public Map<String, Boolean> getMapaRoles() {
        return mapaRoles;
    }

    /**
     * @param mapaRoles the mapaRoles to set
     */
    public void setMapaRoles(Map<String, Boolean> mapaRoles) {
        this.mapaRoles = mapaRoles;
    }
}
