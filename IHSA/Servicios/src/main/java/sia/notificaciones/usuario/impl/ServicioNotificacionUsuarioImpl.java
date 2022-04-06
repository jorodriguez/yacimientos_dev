/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.usuario.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.correo.impl.EnviarCorreoImpl;
import sia.excepciones.EmailNotFoundException;
import sia.modelo.RhUsuarioGerencia;
import sia.modelo.Usuario;
import sia.modelo.gerencia.vo.RhTipoGerenciaVo;
import sia.modelo.usuario.vo.EmpleadoMaterialVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.usuario.impl.RhTipoGerenciaImpl;
import sia.servicios.usuario.impl.RhUsuarioGerenciaImpl;
import sia.util.UtilLog4j;

/**
 * @author hacosta
 */
@Stateless 
public class ServicioNotificacionUsuarioImpl {

    @Inject
    private HtmlNotificacionUsuarioImpl html;
    @Inject
    private SiParametroImpl siParametroRemote;
    @Inject
    private EnviarCorreoImpl enviarCorreo;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private RhUsuarioGerenciaImpl rhUsuarioGerenciaRemote;
    @Inject
    private RhTipoGerenciaImpl rhTipoGerenciaRemote;

    
    public boolean enviarSolicitudMateriar(String para, String copia, String asunto, UsuarioVO usuarioVO, List<EmpleadoMaterialVO> lista, int idGerencia, int nuevoIngreso) {
	return this.enviarCorreo.enviarCorreoIhsa(para, copia, "", asunto, this.html.getHtmlMaterial(usuarioVO, lista, idGerencia, nuevoIngreso, asunto), siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviarNotificacionSiolicitud(String para, String copia, String asunto, UsuarioVO usuarioVOAlta, List<EmpleadoMaterialVO> listaFilasSeleccionadas, int nuevoIngreso) {
	return this.enviarCorreo.enviarCorreoIhsa(para, copia, "", asunto, this.html.getHtmlSolicitudMaterial(usuarioVOAlta, listaFilasSeleccionadas, asunto, nuevoIngreso), siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviarNotificacionAsignacionCorreo(String idUsuarioBuscar, String usuarioRealizo) {
	UtilLog4j.log.info(this, "enviarNotificacionAsignacionCorreo");
	Usuario usuario = usuarioRemote.find(idUsuarioBuscar);
	Usuario usuarioRealizoModificacion = usuarioRemote.find(usuarioRealizo);

	return this.enviarCorreo.enviarCorreoIhsa(usuarioRealizoModificacion.getEmail(), "", "",
		"Asignación de dirección de correo electronico",
		this.html.getHtmlNotificacionCorreoAsignado(usuario, usuarioRealizo),
		siParametroRemote.find(1).getLogo());
    }

    
    public boolean notificationBajaUsuario(int idGerencia, int idRhCampoGerencia, String idResponsableGerencia, String idUsuarioBaja, String idUsuarioDaBaja) throws EmailNotFoundException {

	boolean isPrueba = Constantes.USUARIO_PRUEBA.equals(idUsuarioDaBaja);
	boolean throwsException = false;
	Usuario uResponsable = this.usuarioRemote.find(idResponsableGerencia);
	Usuario uBaja = this.usuarioRemote.find(idUsuarioBaja);
	String asunto = "Liberación de usuario - " + uBaja.getNombre();
	String link = "<A HREF=" + Configurador.urlSia() + "Sia/LIBUSR?"; //Producción
	String cc = "";
	List<String> usuariosWithoutMail = new ArrayList<String>();
//
	if (uResponsable.getEmail() == null || uResponsable.getEmail().isEmpty()) {
	    usuariosWithoutMail.add(uResponsable.getNombre());
	    throwsException = true;
	}

	//Copiar a todos los del detalle
	List<RhTipoGerenciaVo> liberadores = this.rhTipoGerenciaRemote.findAllRhTipoGerenciaByRhCampoGerencia(idRhCampoGerencia, "id", true, false);
	if (liberadores != null) {
	    for (RhTipoGerenciaVo vo : liberadores) {
		Usuario u = this.usuarioRemote.find(vo.getIdUsuario());
		if (!u.getId().equals(uResponsable.getId())) {
		    if (u.getEmail() != null && !u.getEmail().isEmpty()) {
			cc += (cc.isEmpty() ? u.getEmail() : ("," + u.getEmail()));
		    } else {
			usuariosWithoutMail.add(u.getNombre());
			throwsException = true;
		    }
		}
	    }
	}

	if (!throwsException) {
	    return this.enviarCorreo.enviarCorreoIhsa(uResponsable.getEmail(), cc, "", asunto,
		    this.html.getHtmlNotificationBajaUsuario(uResponsable.getNombre(), uBaja.getNombre(), asunto, link),
		    siParametroRemote.find(1).getLogo());
	} else {
//            if(usuariosWithoutMail != null) {
//                UtilLog4j.log.info(this, "Usuarios sin mail: ");
//                for(Usuario u : usuariosWithoutMail) {
//                    UtilLog4j.log.info(this, u.getId() + " - " + "mail: " + (u.getEmail() != null));
//                }
//            }
	    throw new EmailNotFoundException(usuariosWithoutMail);
	}
    }

    
    public boolean notificationUsuarioLiberadoCompletamente(int idRhUsuarioGerencia) throws EmailNotFoundException {

	RhUsuarioGerencia rhUsuarioGerencia = this.rhUsuarioGerenciaRemote.find(idRhUsuarioGerencia);
//        Gerencia gerencia = this.gerenciaRemote.find(51); //Recursos Humanos
	Usuario uResponsableRH = this.gerenciaRemote.getResponsableByApCampoAndGerencia(1, 51, false);
	List<Usuario> usersAdminRH = this.usuarioRemote.traerUsuariosAdministraRH();
	List<String> usuariosWithoutMail = new ArrayList<String>();
	String asunto = "Empleado liberado completamente";
	String cc = "";
	boolean isPrueba = Constantes.USUARIO_PRUEBA.equals(rhUsuarioGerencia.getGenero().getId());
	boolean throwsException = false;

	if (uResponsableRH.getEmail() == null || uResponsableRH.getEmail().isEmpty()) {
	    throwsException = true;
	    usuariosWithoutMail.add(uResponsableRH.getNombre());
	}

	if (usersAdminRH != null && !usersAdminRH.isEmpty()) {
	    for (int i = 0; i < usersAdminRH.size(); i++) {
		Usuario adminRh = usersAdminRH.get(i);
		if (adminRh.getEmail() != null && !adminRh.getEmail().isEmpty()) {
		    cc += adminRh.getEmail();
		    if ((i + 1) < usersAdminRH.size()) {
			cc += ",";
		    }
		} else {
		    throwsException = true;
		    usuariosWithoutMail.add(adminRh.getNombre());
		}
	    }
	}

	if (!throwsException) {
	    UtilLog4j.log.info(this, "Para: " + uResponsableRH.getEmail() + " - Responsable de la Gerencia de RH");
	    UtilLog4j.log.info(this, "CC: " + cc + " - Administradores de RH");
	    UtilLog4j.log.info(this, "CCO: " + this.usuarioRemote.find(Constantes.USUARIO_SIA).getEmail() + " - SIA");
	    UtilLog4j.log.info(this, "Asunto: " + asunto);

	    return this.enviarCorreo.enviarCorreoIhsa(uResponsableRH.getEmail(), cc, "",
		    asunto,
		    this.html.getHtmlNotificationUsuarioLiberadoCompletamente(uResponsableRH.getNombre(), rhUsuarioGerencia.getUsuario().getNombre(), asunto),
		    siParametroRemote.find(1).getLogo());
	} else {
	    throw new EmailNotFoundException(usuariosWithoutMail);
	}
    }

    
    public boolean enviaCorreoNotificaUsuarioFinalizaBaja(String cp, String cc, String cco, String idUsuario) {
	String asunto = "Conclusión del proceso de separación laboral";
	return this.enviarCorreo.enviarCorreoIhsa(cp, cc, cco, asunto,
		this.html.getHtmlNotificationFinalizaProcesoBaja(idUsuario, asunto), siParametroRemote.find(1).getLogo());
    }
}
