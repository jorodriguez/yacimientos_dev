/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.usuario.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.correo.impl.CodigoHtml;
import sia.modelo.SiPlantillaHtml;
import sia.modelo.Usuario;
import sia.modelo.usuario.vo.EmpleadoMaterialVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgEmpresaImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sistema.impl.SiPlantillaHtmlImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@Stateless 
public class HtmlNotificacionUsuarioImpl extends CodigoHtml {

    @Inject
    private SiPlantillaHtmlImpl plantillaHtml;
    @Inject
    private SgOficinaImpl sgOficinaRemote;
    @Inject
    private SgEmpresaImpl sgEmpresaRemote;
    @Inject
    private UsuarioImpl usuarioRemote;

    
    public StringBuilder getHtmlMaterial(UsuarioVO usuarioVO, List<EmpleadoMaterialVO> lista, int idGerencia, int nuevoIngreso, String asunto) {
	SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
	this.limpiarCuerpoCorreo();
	boolean nuevo = true;
	this.cuerpoCorreo.append(plantilla.getInicio());
	//titulo principal en el correo
	this.cuerpoCorreo.append(this.getTitulo(asunto));
	String fecha = "";
	if (usuarioVO.getFechaIngreso() != null) {
	    fecha = Constantes.FMT_TextDate.format(usuarioVO.getFechaIngreso());
	}
	if (nuevoIngreso == Constantes.UNO) {
	    cuerpoCorreo.append("<Br/><p>Por el presente hago de su conocimiento del ingreso de <b>").append(usuarioVO.getNombre()).append("</b> con fecha <b>");
	    cuerpoCorreo.append(fecha).append("</b>, a continuación desgloso brevemente sus datos");
	} else {
	    nuevo = false;
	    cuerpoCorreo.append("<br/><p>Por el presente hago de su conocimiento el proceso de contratacion de <b>").append(usuarioVO.getNombre()).append("</b>.");
	}

	if (idGerencia != 54) {
	    cuerpoCorreo.append(" y hago mención del material y equipo a requerir.</p>");
	} else {
	    cuerpoCorreo.append(".</p>");
	}
	//datos del vehiculo
	this.cuerpoCorreo.append("<table width=\"100%\" border=\"0\" aling=\"center\" cellspacing=\"8\">");
	this.cuerpoCorreo.append("<tr><td valign=\"top\" >");
	datosEmpleado(usuarioVO, nuevo);        //
	this.cuerpoCorreo.append("</td></td></table>");
	//
	this.cuerpoCorreo.append("<table width=\"100%\" border=\"0\" aling=\"center\" cellspacing=\"8\">");
	this.cuerpoCorreo.append("<tr><td valign=\"top\" >");
	//Materiales
	if (idGerencia != 54) {
	    materialEmpleado(lista);
	    this.cuerpoCorreo.append("</td></tr></table>");
	}
	this.cuerpoCorreo.append("</br>");
	for (EmpleadoMaterialVO vo : lista) {
	    if (vo.getIdGerencia() == 61 && vo.getId() == 15) {
		//
		this.cuerpoCorreo.append("<p>Para asignar la dirección de correo electrónico al empleado ingresar al SIA en la opción RH, menu de Empleados, submenu Asignar dirección de correo a usuario. </p>");
		this.cuerpoCorreo.append("<center><A  HREF ='").append(Configurador.urlSia()).append("Sia'>Clic aquí para ir al SIA</A></center>");
		break;
	    }
	}

	this.cuerpoCorreo.append("</br>");
	this.cuerpoCorreo.append("</br>");

	this.cuerpoCorreo.append("<p> Cualquier duda o comentario favor de indicarlo a Recursos Humanos. </p>");
	//Fin de la plantilla
	this.cuerpoCorreo.append("</table>");
	this.cuerpoCorreo.append(plantilla.getFin());
	return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlSolicitudMaterial(UsuarioVO usuarioVO, List<EmpleadoMaterialVO> lista, String asunto, int nuevoIngreso) {
	SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
	this.limpiarCuerpoCorreo();
	this.cuerpoCorreo.append(plantilla.getInicio());
	//titulo principal en el correo
	this.cuerpoCorreo.append(this.getTitulo(asunto));
	String f = " - ";
	if (usuarioVO.getFechaIngreso() != null) {
	    f = Constantes.FMT_TextDate.format(usuarioVO.getFechaIngreso());
	}
	boolean nuevo = true;
	if (nuevoIngreso == 1) {
	    this.cuerpoCorreo.append("<Br/><p>Se solicitó para el empleado <b>".concat(usuarioVO.getNombre()).concat("</b> con fecha de ingreso </b>".concat(f).concat("</b> lo siguiente: </p>")));
	} else {
	    this.cuerpoCorreo.append("<Br/><p>Se solicitó para el empleado en proceso de contración <b>".concat(usuarioVO.getNombre()).concat("</b> lo siguiente: </p>"));
	    nuevo = false;
	}

	//datos del vehiculo
	this.cuerpoCorreo.append("<table width=\"100%\" border=\"0\" aling=\"center\" cellspacing=\"8\">");
	this.cuerpoCorreo.append("<tr><td valign=\"top\" >");
	datosEmpleado(usuarioVO, nuevo);        //
	this.cuerpoCorreo.append("</td></td></table>");
	//
	this.cuerpoCorreo.append("<table width=\"100%\" border=\"0\" aling=\"center\" cellspacing=\"8\">");
	this.cuerpoCorreo.append("<tr><td valign=\"top\" >");
	//Materiales
	if (!lista.isEmpty()) {
	    materialEmpleado(lista);
	}
	this.cuerpoCorreo.append("</td></tr></table>");
	this.cuerpoCorreo.append("</br>");
	this.cuerpoCorreo.append("</br>");
	//Fin de la plantilla
	this.cuerpoCorreo.append("</table>");
	this.cuerpoCorreo.append(plantilla.getFin());
	return this.cuerpoCorreo;
    }

    private void datosEmpleado(UsuarioVO usuarioVO, boolean nuevoIngreso) {
	String nomina = " - ";
	String oficina = " - ";
	String fecha = " - ";
	if (usuarioVO.getIdNomina() > 0) {
	    nomina = sgEmpresaRemote.find(usuarioVO.getIdNomina()).getNombre();
	}
	if (usuarioVO.getIdOficina() > 0) {
	    oficina = sgOficinaRemote.find(usuarioVO.getIdOficina()).getNombre();
	}
	if (usuarioVO.getFechaIngreso() != null) {
	    fecha = Constantes.FMT_TextDate.format(usuarioVO.getFechaIngreso());
	}
	this.cuerpoCorreo.append("<br/>");
	this.cuerpoCorreo.append("<center> ");
	this.cuerpoCorreo.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\" >");
	this.cuerpoCorreo.append("<tr > <th colspan=\"2\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\">Datos del empleado</th></tr>");
	this.cuerpoCorreo.append("<tr>	<td width=\"41%\" align=\"left\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\">Nombre</td>");
	this.cuerpoCorreo.append("<td width=\"59%\" height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(usuarioVO.getNombre()).concat("</td></tr>"));
	this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"left\">Puesto</td>");
	this.cuerpoCorreo.append("<td height=\"18\"  style=\"border: 1px solid #b5b5b5;\">".concat(usuarioVO.getPuesto()).concat("</td> </tr>"));
	this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"left\">Gerencia</td>");
	this.cuerpoCorreo.append("<td height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(usuarioVO.getGerencia()).concat("</td></tr>"));
	this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"left\">Nómina</td>");
	this.cuerpoCorreo.append("<td height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(nomina).concat("</td></tr>"));
	this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"left\">Oficina </td>");
	this.cuerpoCorreo.append("<td height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(oficina).concat("</td> </tr>"));
	this.cuerpoCorreo.append("<tr> <td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"left\">");
	cuerpoCorreo.append(nuevoIngreso ? "Fecha ingreso" : "Posible ingreso").append("</td>");
	this.cuerpoCorreo.append("<td height=\"18\" style=\"border: 1px solid #b5b5b5;\">").append(fecha).append("</td> </tr>");
	this.cuerpoCorreo.append("</table>");
	this.cuerpoCorreo.append("</center> ");
    }

    private void materialEmpleado(List<EmpleadoMaterialVO> lista) {
	this.cuerpoCorreo.append("<table width=\"100%\" cellspacing=\"0\">");
	this.cuerpoCorreo.append("<th colspan=\"2\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\"> Material</th>");
	this.cuerpoCorreo.append("<tr>");
	this.cuerpoCorreo.append("<td width=\"30%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Nombre</td>");
	this.cuerpoCorreo.append("<td width=\"60%\" align=\"center\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:12px; font-weight: bold;\">Descripción</td>");

	this.cuerpoCorreo.append("</tr>");
	for (EmpleadoMaterialVO em : lista) {
	    this.cuerpoCorreo.append("<tr>");
	    this.cuerpoCorreo.append("<td ".concat(" width=\"30%\" align=\"left\" style=\"border: 1px solid #b5b5b5;\">".concat(em.getNombre()).concat("</td>")));
	    this.cuerpoCorreo.append("<td ".concat(" width=\"60%\" align=\"center\" style=\"border: 1px solid #b5b5b5;\">".concat(em.getDescripcion().isEmpty() ? "Si" : em.getDescripcion()).concat("</td>")));
	    this.cuerpoCorreo.append("</tr>");

	}
    }

    
    public StringBuilder getHtmlNotificacionCorreoAsignado(Usuario usuario, String asignadoPor) {
	//
	try {
	    SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
	    this.limpiarCuerpoCorreo();
	    this.cuerpoCorreo.append(plantilla.getInicio());
	    //titulo principal en el correo
	    this.cuerpoCorreo.append(this.getTitulo("Asignación de dirección de correo electronico "));
	    this.cuerpoCorreo.append("<br/><p>Por el presente hago de su conocimiento de la asignación de nueva dirección de correo electronico del usuario que acontinuación se  muestra : <b>");
	    //datos del vehiculo

	    this.cuerpoCorreo.append("<center> ");

	    this.cuerpoCorreo.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\" >");
	    this.cuerpoCorreo.append("<tr > <th colspan=\"2\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\">Datos del empleado</th></tr>");

	    this.cuerpoCorreo.append("<tr>	<td width=\"41%\" align=\"left\" style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\">Nombre</td>");
	    this.cuerpoCorreo.append("<td width=\"59%\" height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(usuario.getNombre()).concat("</td></tr>"));

	    this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"left\">Gerencia</td>");
	    this.cuerpoCorreo.append("<td height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(usuario.getGerencia().getNombre()).concat("</td></tr>"));

	    this.cuerpoCorreo.append("<tr><td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"left\">Oficina </td>");
	    this.cuerpoCorreo.append("<td height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(usuario.getSgOficina().getNombre()).concat("</td> </tr>"));

	    this.cuerpoCorreo.append("<tr> <td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"left\">Fecha ingreso</td>");
	    this.cuerpoCorreo.append("<td height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(Constantes.FMT_TextDate.format(usuario.getFechaIngreso())).concat("</td> </tr>"));

	    this.cuerpoCorreo.append("<tr> <td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"left\">Direción de correo</td>");
	    this.cuerpoCorreo.append("<td height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(usuario.getEmail()).concat("</td> </tr>"));

	    this.cuerpoCorreo.append("</table>");
	    this.cuerpoCorreo.append("<br/>");
	    this.cuerpoCorreo.append("</center> ");

	    this.cuerpoCorreo.append("<center> ");
	    this.cuerpoCorreo.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\" >");
	    this.cuerpoCorreo.append("<tr > <th colspan=\"1\" style=\"font-family:Georgia, 'Times New Roman', Times, serif; font-weight:bold; color:#FFFFFF; font-size:12px; background-color:#0099FF;\">Asignó</th></tr>");
	    // this.cuerpoCorreo.append("<tr> <td style=\"border: 1px solid #b5b5b5; font:Arial, Helvetica, sans-serif; font-size:13px; font-weight: bold;\" align=\"left\">Asignó</td>");
	    this.cuerpoCorreo.append("<tr><td height=\"18\" style=\"border: 1px solid #b5b5b5;\">".concat(this.usuarioRemote.find(asignadoPor).getNombre()).concat("</td> </tr>"));
	    this.cuerpoCorreo.append("</table>");

	    this.cuerpoCorreo.append("</center> ");

	    //Fin de la plantilla
	    this.cuerpoCorreo.append(plantilla.getFin());
	    return this.cuerpoCorreo;
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	    return null;
	}
    }

    
    public StringBuilder getHtmlNotificationBajaUsuario(String nombreResponsableGerencia, String nombreUsuarioBaja, String asunto, String link) {
	SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
	this.limpiarCuerpoCorreo();
	this.cuerpoCorreo.append(plantilla.getInicio());
	this.cuerpoCorreo.append(this.getTitulo(asunto));
	this.cuerpoCorreo.append("<br/><p>Estimado(a) <b> ".concat(nombreResponsableGerencia).concat("</b></p>"));
	this.cuerpoCorreo.append("<p>El Departamento de Recursos Humanos ha iniciado el proceso de separación laboral del empleado <b>").append(nombreUsuarioBaja).append(".</b></p>");
	this.cuerpoCorreo.append("<p>Como parte del proceso es necesario liberar al empleado de asuntos pendientes ingresando al ").append(Constantes.LINK_SIA).append(" y notificándolo.</p>");
	this.cuerpoCorreo.append("<p>Si existiera algún adeudo del personal informarlo a Recursos Humanos.</p><br/>");
	this.cuerpoCorreo.append(plantilla.getFin());
	return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlNotificationUsuarioLiberadoCompletamente(String nombreResponsableGerencia, String nombreUsuarioLiberado, String asunto) {
	SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
	this.limpiarCuerpoCorreo();
	this.cuerpoCorreo.append(plantilla.getInicio());
	this.cuerpoCorreo.append(this.getTitulo(asunto));
	this.cuerpoCorreo.append("<br/><p>Estimado(a) <b> ".concat(nombreResponsableGerencia).concat("</b></p>"));
	this.cuerpoCorreo.append("<p>El empleado <b>").append(nombreUsuarioLiberado).append("</b> ha sido liberado por todas las gerencias a las que se notificó de su separación laboral y está listo para terminar su proceso.</p>");
	this.cuerpoCorreo.append(plantilla.getFin());
	return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlNotificationFinalizaProcesoBaja(String idUsuario, String asunto) {
	SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
	this.limpiarCuerpoCorreo();
	this.cuerpoCorreo.append(plantilla.getInicio());
	this.cuerpoCorreo.append(this.getTitulo(asunto));
	this.cuerpoCorreo.append("<br/><p>Ha concluido el proceso de separación laboral del empleado (a) <b> ".concat(usuarioRemote.find(idUsuario).getNombre()).concat(".</b>"));

	this.cuerpoCorreo.append(plantilla.getFin());
	return this.cuerpoCorreo;
    }
}
