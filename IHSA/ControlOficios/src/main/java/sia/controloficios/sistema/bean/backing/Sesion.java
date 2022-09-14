package sia.controloficios.sistema.bean.backing;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;
import sia.excepciones.InsufficientPermissionsException;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CompaniaBloqueGerenciaVo;
import sia.modelo.oficio.vo.OficioConsultaVo;
import sia.modelo.oficio.vo.PermisosVo;
import sia.util.Env;
import sia.util.UtilLog4j;

/**
 * Contiene los valores necesarios para la sesión del usuario.
 *
 * @author esapien
 */
@Named(value = "sesion")
@SessionScoped
public class Sesion implements Serializable {

    private Usuario usuario;
    private String puesto;
    private PermisosVo permisos;
     
    @Getter
    @Setter
    private Properties ctx;

    // opciones de bloques del usuario
    private List<CompaniaBloqueGerenciaVo> bloquesUsuario;

    private CompaniaBloqueGerenciaVo bloqueActivo;

    /**
     * Value object para preservar los valores de consulta en curso del usuario
     *
     */
    private OficioConsultaVo oficioConsultaVo;

    /**
     * Bandera para permitir hacer labores de edición de oficios.
     *
     * Solo es permitido para roles emisores y receptores.
     *
     */
    private boolean modoEdicion;

    /**
     *
     */
    public Sesion() {

	log("ControlOficios - Sesion@Constructor");

    }

    @PostConstruct
    public void iniciar() {

	log("ControlOficios - Sesion@PostConstruct");
        
        this.ctx = new Properties();
    }

    @PreDestroy
    public void terminar() {

	log("ControlOficios - Sesion@PreDestroy");
    }
    
    public void subirValoresContexto(HttpSession sesion) {
        Env.setContext(ctx, Env.SESSION_ID, sesion.getId());
        Env.setContext(ctx, Env.CLIENT_INFO, sesion.getServletContext().getContextPath());
        Env.setContext(ctx, Env.PUNTO_ENTRADA, "Sia");
        Env.setContext(ctx, Env.PROYECTO_ID, usuario.getApCampo().getId());
        Env.setContext(ctx, Env.CODIGO_COMPANIA, usuario.getApCampo().getCompania().getRfc());
    }
    
      public String goTo(String page) {
        if (!page.endsWith(".xhtml")) {
            page += ".xhtml";
        }
        return page + "?faces-redirect=true";
    }

    /**
     * Para validar si se deben activar las opciones de edición en el detalle de
     * un oficio. Las opciones de edición son todas las que cambien el estado de
     * un oficio en el sistema: altas, cambios, promociones y anulaciones.
     *
     * @return
     */
    public boolean isModoEdicion() {
	return modoEdicion;
    }

    /**
     *
     * @param modoEdicion
     */
    public void setModoEdicion(boolean modoEdicion) throws InsufficientPermissionsException {

	log("@Sesion - setModoEdicion() - estableciendo a = " + modoEdicion);

        // validar que se tienen los permisos válidos para activar
	// el modo de edición
	if (modoEdicion && !permisos.isRolEdicionOficios()) {
	    throw new InsufficientPermissionsException();
	}

	this.modoEdicion = modoEdicion;

    }

    /**
     * Redirecciona a la página principal del sistema Sia. Elimina la
     * información de la sesión actual para evitar acceso directo por medio de
     * URL.
     *
     * TODO: Confirmar si se utiliza
     *
     * @param actionEvent
     */
    public void siaGo(ActionEvent actionEvent) {

	invalidarSesionActual();

	redireccionar(Constantes.URL_REL_SIA_PRINCIPAL);

    }
   

    /**
     * Invalida la información relacionada con la sesión actual.
     *
     */
    private void invalidarSesionActual() {

	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	ec.invalidateSession();

    }

    /**
     * Redirecciona a la URL proporcionada.
     *
     * @param url
     */
    private void redireccionar(final String url) {

	FacesContext fc = FacesContext.getCurrentInstance();

	try {
	    fc.getExternalContext().redirect(url);//redirecciona la página
	} catch (IOException ex) {
	    log("Error de IO al redireccionar: " + ex.getMessage());
	}
    }
    

    private void log(String mensaje) {
	UtilLog4j.log.info(this, mensaje);
    }

    /**
     *
     * @param actionEvent
     */
    public void cerrarSesion(ActionEvent actionEvent) {

	invalidarSesionActual();

	redireccionar(Constantes.URL_REL_SIA_SIGN_OUT);

    }
    
    public String goToBandejaEntrada(){
        return "/vistas/oficios/bandejaEntrada.xhtml?faces-redirect=true";
    }
   
    public String goToConsultar(){
        return "/vistas/oficios/consultar.xhtml?faces-redirect=true";
    }
    
    public String goToOficios(){
        return "/vistas/oficios/tickectOf.xhtml?faces-redirect=true";
    }   
    

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
	return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
	this.usuario = usuario;
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

    public PermisosVo getPermisos() {
	return permisos;
    }

    public void setPermisos(PermisosVo permisos) {
	this.permisos = permisos;
    }

    public List<CompaniaBloqueGerenciaVo> getBloquesUsuario() {
	return bloquesUsuario;
    }

    public void setBloquesUsuario(List<CompaniaBloqueGerenciaVo> bloquesUsuario) {
	this.bloquesUsuario = bloquesUsuario;
    }

    public CompaniaBloqueGerenciaVo getBloqueActivo() {
	return bloqueActivo;
    }

    public void setBloqueActivo(CompaniaBloqueGerenciaVo bloqueActivo) {
	this.bloqueActivo = bloqueActivo;
    }

    public OficioConsultaVo getOficioConsultaVo() {
	return oficioConsultaVo;
    }

    public void setOficioConsultaVo(OficioConsultaVo oficioConsultaVo) {
	this.oficioConsultaVo = oficioConsultaVo;
    }

    public boolean isUsuarioEditor() {
	return this.getPermisos().isRolEdicionOficios();
    }

    /**
     * Para indicar si este usuario de acuerdo a sus servicios, puede ver
     * oficios de tipo restringido sin estar incluido en la lista de usuarios
     * con acceso al oficio.
     *
     * @return
     */
    public boolean puedeVerOficioRestringido() {
	// los usuarios editores pueden ver oficios restringidos
	return this.isUsuarioEditor();

    }

}
