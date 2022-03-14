/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.sistema.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import sia.constantes.Constantes;
import sia.correo.impl.EnviarCorreoImpl;
import sia.modelo.Usuario;
import sia.modelo.sistema.vo.IncidenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;

/**
 * @author hacosta
 */
@LocalBean 
public class ServicioNotificacionSistemaImpl{

    @Inject
    private HtmlNotificacionSistemaImpl html;
    @Inject
    private SiParametroImpl parametrosSistema;
    @Inject
    private EnviarCorreoImpl enviarCorreo;
    @Inject
    SiUsuarioRolImpl usuarioRolRemote;

    
    public boolean enviarClave(String nombre, String correo, String clave, String idUsuario) {
        return this.enviarCorreo.enviarCorreoIhsa(correo, "", "", "Contraseña de usuario", this.html.getHtmlClaveUsuario(nombre, clave, idUsuario), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarClaveIhsa(String nombre, String correo, String clave) {
        return this.enviarCorreo.enviarCorreoIhsa(correo, "", "", "Contraseña de usuario", this.html.getHtmlClaveUsuario(nombre), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarCorreoCambioClaveIhsa(String nombre, String correo) {
        return this.enviarCorreo.enviarCorreoIhsa(correo, "siaihsa@ihsa.mx", "", "Cambio de contraseña", this.html.getHtmlClaveUsuario(nombre), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarReinicioClave(String nombre, String email, String clave, String idUsuario) {
        return this.enviarCorreo.enviarCorreoIhsa(email, "siaihsa@ihsa.mx", "", "Contraseña de usuario", this.html.getHtmlClaveUsuario(nombre, clave, idUsuario), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarExcepcion(String sesion, String para, Object object, String asunto, String excepcion) {
        return enviarCorreo.enviarCorreoIhsa(para, "", "", asunto, html.htmlExcepcionSia(sesion, object, asunto, excepcion), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarIncidencia(IncidenciaVo incidenciaVo, Usuario usuario) {
        return enviarCorreo.enviarCorreoIhsa(correPoRol(Constantes.COD_ROL_SOPORTE_TECNICO), usuario.getEmail(), correPoRol(Constantes.COD_ROL_NOTI_SOPORTE_TECNICO), "Ticket " + incidenciaVo.getCodigo(),
                html.htmlNuevoTicket(incidenciaVo, correPoRol(Constantes.COD_ROL_SOPORTE_TECNICO)), parametrosSistema.find(1).getLogo());
    }

    
    public boolean reenviarIncidencia(IncidenciaVo incidenciaVo, String complemento, Usuario usuario) {
        return enviarCorreo.enviarCorreoIhsa(correPoRol(Constantes.COD_ROL_SOPORTE_TECNICO), usuario.getEmail(), "", "Complemento del Ticket " + incidenciaVo.getCodigo(),
                html.htmlReenvioTicket(incidenciaVo, complemento), parametrosSistema.find(1).getLogo());
    }

    
    public boolean cierreIncidencia(IncidenciaVo incidenciaVo, String motivo, Usuario usuario) {
        return enviarCorreo.enviarCorreoIhsa(correPoRol(Constantes.COD_ROL_SOPORTE_TECNICO), usuario.getEmail(), "", "Cierre del Ticket " + incidenciaVo.getCodigo(),
                html.htmlCierreTicket(incidenciaVo, motivo), parametrosSistema.find(1).getLogo());
    }

    //
    private String correPoRol(String codRol) {
        String correo = usuarioRolRemote.traerCorreosPorCodigoRolList(codRol, Constantes.AP_CAMPO_DEFAULT);
        return correo;
    }

    
    public boolean asignarIncidencia(IncidenciaVo incidenciaVo, UsuarioVO asignado, Usuario sesion) {
        return enviarCorreo.enviarCorreoIhsa(asignado.getMail(), correPoRol(Constantes.COD_ROL_SOPORTE_TECNICO), "", "Asignación del Ticket " + incidenciaVo.getCodigo(),
                html.htmlAsignacionTicket(incidenciaVo, asignado.getNombre()), parametrosSistema.find(1).getLogo());
    }

    
    public boolean finalizarIncidencia(IncidenciaVo incidenciaVo, Usuario sesion) {
        return enviarCorreo.enviarCorreoIhsa(incidenciaVo.getCorreoGenero(), correPoRol(Constantes.COD_ROL_SOPORTE_TECNICO), "", "Finalización del Ticket " + incidenciaVo.getCodigo(),
                html.htmlFinalizarTicket(incidenciaVo), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarEvidenciaIncidencia(IncidenciaVo incidenciaVo) {
        return enviarCorreo.enviarCorreoIhsa(incidenciaVo.getCorreoGenero(), correPoRol(Constantes.COD_ROL_SOPORTE_TECNICO), "", "Adjunto del Ticket " + incidenciaVo.getCodigo(),
                html.htmlAdjuntarTicket(incidenciaVo), parametrosSistema.find(1).getLogo());
    }

    
    public boolean reAsignarIncidencia(IncidenciaVo incidenciaVo) {
        return enviarCorreo.enviarCorreoIhsa(incidenciaVo.getCorreoGenero(), correPoRol(Constantes.COD_ROL_SOPORTE_TECNICO), incidenciaVo.getCorreoAsignado(), "Re-Asignación del Ticket " + incidenciaVo.getCodigo(),
                html.htmlAsignacionTicket(incidenciaVo, incidenciaVo.getAsignado()), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarEscalaTicket(IncidenciaVo incidenciaVo) {
        List<UsuarioRolVo> lur = usuarioRolRemote.traerRolPorCodigo(Constantes.COD_ROL_NOTI_SOPORTE_TECNICO, Constantes.AP_CAMPO_DEFAULT, Constantes.MODULO_ADMIN_SIA);
        for (UsuarioRolVo usuarioRolVo : lur) {
            enviarCorreo.enviarCorreoIhsa(usuarioRolVo.getCorreo(), "", "", "Cambio nivel del Ticket " + incidenciaVo.getCodigo(),
                    html.htmlEscalaTicket(incidenciaVo, usuarioRolVo.getIdUsuario(), correPoRol(Constantes.COD_ROL_SOPORTE_TECNICO)), parametrosSistema.find(1).getLogo());
        }

        return enviarCorreo.enviarCorreoIhsa(incidenciaVo.getCorreoAsignado(), "", "", "Cambio nivel del Ticket " + incidenciaVo.getCodigo(),
                html.htmlEscalaTicket(incidenciaVo, "", correPoRol(Constantes.COD_ROL_SOPORTE_TECNICO)), parametrosSistema.find(1).getLogo());
    }

    
    public void enviarAceptacionEscalaTicket(IncidenciaVo incidenciaVo) {
        enviarCorreo.enviarCorreoIhsa(correPoRol(Constantes.COD_ROL_SOPORTE_TECNICO), "", "", "Se Aceptó el cambio nivel del Ticket " + incidenciaVo.getCodigo(),
                html.htmlAceptarEscalaTicket(incidenciaVo), parametrosSistema.find(1).getLogo());
    }

    /**
     *
     * @param incidenciaVo
     */
    
    public void enviarNoAceptacionEscalaTicket(IncidenciaVo incidenciaVo) {
        enviarCorreo.enviarCorreoIhsa(correPoRol(Constantes.COD_ROL_SOPORTE_TECNICO), "", "", "No se Aceptó el cambio nivel del Ticket " + incidenciaVo.getCodigo(),
                html.htmlNoAceptarEscalaTicket(incidenciaVo), parametrosSistema.find(1).getLogo());
    }
}
