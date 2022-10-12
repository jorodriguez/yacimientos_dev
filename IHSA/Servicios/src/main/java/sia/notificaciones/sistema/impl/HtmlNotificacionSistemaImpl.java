/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.sistema.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.correo.impl.CodigoHtml;
import sia.modelo.Orden;
import sia.modelo.SiPlantillaHtml;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.sistema.vo.IncidenciaVo;
import sia.servicios.sistema.impl.SiPlantillaHtmlImpl;

/**
 *
 * @author hacosta
 */
@Stateless 
public class HtmlNotificacionSistemaImpl extends CodigoHtml {

    @Inject
    private SiPlantillaHtmlImpl plantillaHtml;

    
    public StringBuilder getHtmlClaveUsuario(String nombre, String clave, String idUsuario) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio())
                .append(this.getTitulo("Contraseña de usuario"))
                .append("Estimado, &nbsp; ")
                .append(nombre)
                .append("<Br/><Br/><p>Se ha cambiado su contraseña para ingreso al SIA, puede acceder al sistema de inmediato.")
                .append("<Br/><Br/>Usuario: ")
                .append("<b>")
                .append(idUsuario)
                .append("</b>")
                .append("<Br/>Contraseña: ")
                .append("<b>")
                .append(clave)
                .append("</b>")
                .append("<p>").append("<a target=\"_blank\" href=\"").append(Configurador.urlSia()).append("Sia\">")
                .append("Clic aquí para ir al SIA</a></p>")
                .append("<p>Por seguridad y comodidad le recomendamos cambiar su contraseña.").append("</p>")
                .append("Gracias, <br/> El equipo del SIA.")
                .append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder getHtmlClaveUsuario(String nombre) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio());
        //titulo principal en el correo
        this.cuerpoCorreo.append(this.getTitulo("Cambio de contraseña"));
        this.cuerpoCorreo.append("<br/><p>Estimado, ");
        this.cuerpoCorreo.append("".concat(nombre).concat("</p><br/> Ha cambiado su contraseña para ingreso al SIA, puede acceder al sistema de inmediato."));
        this.cuerpoCorreo.append("<Br/><Br/> Gracias, <Br/> El equipo del SIA.");
        this.cuerpoCorreo.append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder htmlExcepcionSia(String sesion, Object object, String asunto, String excepcion) {

        StringBuilder sbM = new StringBuilder();
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        sbM.append(plantilla.getInicio());
        sbM.append(this.getTitulo(asunto));
        if (object instanceof Orden) {
            Orden orden = (Orden) object;
            sbM.append("No se pudo aprobar la Orden de Compra ").append(orden.getConsecutivo()).append(". El problema ya fue notificado al Equipo de Soporte para su oportuna revisión.");
            sbM.append(" En cuanto se resuelva el inconveniente se le notificará.");
        }
        if (object instanceof ContratoVO) {
            ContratoVO contratoVO = (ContratoVO) object;
            sbM.append(" Ocurrio un error al cambiar el estado del contrato <b>").append(contratoVO.getNumero()).append("</b>");//.append(". El problema ya fue notificado al Equipo de Soporte para su oportuna revisión.");
            sbM.append(" <br/>");
            sbM.append(" Excepción: ").append(excepcion);
            sbM.append(" <br/>");
            sbM.append(" Favor de atenderlo a la brevedad.");
            //String asunto = "Excepción en ".concat(opcion).concat(" la OC/S: ").concat(orden.getConsecutivo());
        }
        sbM.append(plantilla.getFin());
        return sbM;
        //	
    }

    
    public StringBuilder htmlNuevoTicket(IncidenciaVo incidenciaVo, String correoSoporte) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio())
                .append(this.getTitulo("Ticket " + incidenciaVo.getCodigo()))
                .append("Equipo de soporte ")
                .append("<p> Se registró la solicud de servicio de soporte <b># ").append(incidenciaVo.getCodigo()).append(" </b>.</p>")
                .append("<p>Título: ").append(incidenciaVo.getTitulo()).append("</p>")
                .append("<p>Descripción: ").append(incidenciaVo.getDescripcion()).append("</p>")
                .append("<br/>")
                .append("Si requieres agregar información adicional para seguimiento, favor de reenviar este correo a ")
                .append(correoSoporte).append(" <br> sin modificar el título del mismo.").append("<br><br>")
                .append("Gracias por utilizar nuestros servicios.")
                .append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder htmlReenvioTicket(IncidenciaVo incidenciaVo, String informacionComplementaria) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio())
                .append(this.getTitulo("Ticket " + incidenciaVo.getCodigo()))
                .append("Equipo de soporte ")
                .append("<p> el usuario <b>").append(incidenciaVo.getGenero()).append(" </b> ha reeviado el ticket de servicio ").append(incidenciaVo.getCodigo())
                .append(" con la siguiente información complementaria. </p>")
                .append("<p>Información: ").append(informacionComplementaria)
                .append("<p>")
                .append("Gracias por utilizar nuestros servicios.</p>")
                .append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder htmlCierreTicket(IncidenciaVo incidenciaVo, String motivo) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio())
                .append(this.getTitulo("Ticket " + incidenciaVo.getCodigo()))
                .append("Equipo de soporte ")
                .append("<p> El usuario <b>").append(incidenciaVo.getGenero()).append(" </b> ha cerrado el ticket de servicio ").append(incidenciaVo.getCodigo())
                .append(" por el siguiente motivo. </p>")
                .append("<p>Motivo: ").append(motivo)
                .append("<br>")
                .append("<p>Gracias por utilizar nuestros servicios.</p>")
                .append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder htmlAsignacionTicket(IncidenciaVo incidenciaVo, String asignado) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio())
                .append(this.getTitulo("Ticket " + incidenciaVo.getCodigo()))
                .append("Buen día, ").append(asignado)
                .append("<p> Se le informa que el ticket ").append(incidenciaVo.getCodigo()).append(" te ha sido asignado.</p>")
                .append("<p> Título: ").append(incidenciaVo.getTitulo()).append("</p>")
                .append("<p> Descripción: ").append(incidenciaVo.getDescripcion()).append("</p>")
                .append("<br>")
                .append("<p>Gracias por utilizar nuestros servicios.</p>")
                .append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder htmlFinalizarTicket(IncidenciaVo incidenciaVo) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio())
                .append(this.getTitulo("Ticket " + incidenciaVo.getCodigo()))
                .append("Buen día, <b>").append(incidenciaVo.getGenero()).append("</b>")
                .append("<p> El ticket <b> # ").append(incidenciaVo.getTitulo()).append("</b>")
                .append(" se va a finalizar, por favor, verificar y confirmar la solución.</p>")
                .append("<br>")
                .append("<table width=\"300\" style=\"border: 1px solid gray;\">")
                .append("<tr>")
                .append("<td colspan=\"2\" bgcolor=\"#13B0E2\" style=\"text-align:center;\"><span style=\"font-size: 18px;\"><b>¿Se solucionó el ticket?</b></span></td>")
                .append("</tr>")
                .append("<tbody>")
                .append("<tr>")
                .append("<td style=\"width: 150px; text-align: center; padding: 10px;\"><span style=\"font-size: 34px;\">")
                .append("<a style=\"margin: 10px; background-color: #0895d6; border: 1px solid #999999; color: #ffffff; cursor: pointer; font-weight: bold; text-decoration: none;\" ")
                .append(" href=\"").append(Configurador.urlSia()).append("Sia/PROCTICK?mg4mvrg205m=").append(incidenciaVo.getIdIncidencia()).append("&v3g9m9v=").append(incidenciaVo.getIdGenero()).append("&b20Iw9v3=").append(Constantes.BOOLEAN_TRUE).append("\">Si</a></span></td>")
                .append("<td style=\"width: 150px; text-align: center; padding: 10px;\"><span style=\"font-size: 34px; color: #000000;\">")
                .append("<a style=\"margin: 10px; background-color: #d8e1e6; border: 1px solid #999999; color: #000000; cursor: pointer; font-weight: bold; text-decoration: none;\" ")
                .append(" href=\"").append(Configurador.urlSia()).append("Sia/PROCTICK?mg4mvrg205m=").append(incidenciaVo.getIdIncidencia()).append("&v3g9m9v=").append(incidenciaVo.getIdGenero()).append("&b20Iw9v3=").append(Constantes.BOOLEAN_FALSE).append("\">No</a></span></td>")
                .append("</tr>")
                .append("</tbody>")
                .append("</table>")
                .append("<p>Gracias por utilizar nuestros servicios.</p>")
                .append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder htmlAdjuntarTicket(IncidenciaVo incidenciaVo) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio())
                .append(this.getTitulo("Ticket " + incidenciaVo.getCodigo()))
                .append("Buen día, ").append(incidenciaVo.getGenero())
                .append("<p> Se agregó archivo adjunto a el ticket <b># ").append(incidenciaVo.getCodigo()).append("</b></p>")
                .append("<br>")
                .append("<p>Gracias por utilizar nuestros servicios.</p>")
                .append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder htmlReAsignacionTicket(IncidenciaVo incidenciaVo) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio())
                .append(this.getTitulo("Ticket " + incidenciaVo.getCodigo()))
                .append("Buen día, ").append(incidenciaVo.getGenero())
                .append("<p> Se le informa que el ticket ").append(incidenciaVo.getCodigo()).append(" te ha sido re-asignado.</p>")
                .append("<p> En breve el personal de Soporte Técnico atenderá la solicitud, si considera necesario ")
                .append("puede complementar información desde la opción de Registro de Tickets</p>")
                .append("<br>")
                .append("<p>Gracias por utilizar nuestros servicios.</p>")
                .append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder htmlEscalaTicket(IncidenciaVo incidenciaVo, String usuarioRol, String correoSoporte) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio())
                .append(this.getTitulo("Ticket " + incidenciaVo.getCodigo()))
                .append("Buen día ")
                .append("<p> Debido a la complejidad de la petición en el ticket <b> # ").append(incidenciaVo.getCodigo()).append("</b>")
                .append(", se ha decidido cambiarlo a <b>Segundo Nivel</b>.</p>")
                .append("<p> Título: ").append(incidenciaVo.getTitulo()).append("</p>")
                .append("<p> Descripción del ticket: ").append(incidenciaVo.getDescripcion()).append("</p>")
                .append("<p> Motivo de escala: ").append(incidenciaVo.getMotivoEscala()).append("</p>")
                .append("<br>");
        if (!usuarioRol.isEmpty()) {
            cuerpoCorreo.append("<table width=\"300\" style=\"border: 1px solid gray;\">")
                    .append("<tr>")
                    .append("<td colspan=\"2\" bgcolor=\"#13B0E2\" style=\"text-align:center;\"><span style=\"font-size: 18px;\"><b>¿Estás de acuerdo?</b></span></td>")
                    .append("</tr>")
                    .append("<tbody>")
                    .append("<tr>")
                    .append("<td style=\"width: 150px; text-align: center; padding: 10px;\"><span style=\"font-size: 34px;\">")
                    .append("<a style=\"margin: 10px; background-color: #0895d6; border: 1px solid #999999; color: #ffffff; cursor: pointer; font-weight: bold; text-decoration: none;\" ")
                    .append(" href=\"").append(Configurador.urlSia()).append("Sia/ESCTICK?mg4mvrg205m=").append(incidenciaVo.getIdIncidencia()).append("&v3g9m9v=").append(usuarioRol).append("&b20Iw9v3=").append(Constantes.BOOLEAN_TRUE).append("\">Si</a></span></td>")
                    .append("<td style=\"width: 150px; text-align: center; padding: 10px;\"><span style=\"font-size: 34px; color: #000000;\">")
                    .append("<a style=\"margin: 10px; background-color: #d8e1e6; border: 1px solid #999999; color: #000000; cursor: pointer; font-weight: bold; text-decoration: none;\" ")
                    .append(" href=\"").append(Configurador.urlSia()).append("Sia/ESCTICK?mg4mvrg205m=").append(incidenciaVo.getIdIncidencia()).append("&v3g9m9v=").append(usuarioRol).append("&b20Iw9v3=").append(Constantes.BOOLEAN_FALSE).append("\">No</a></span></td>")
                    .append("</tr>")
                    .append("</tbody>")
                    .append("</table>")
                    .append("<br>");
        }

        cuerpoCorreo.append("<p> Cualquier duda u observación favor de comunicarse al correo ").append(correoSoporte).append("</p>")
                .append("<p>Gracias por utilizar nuestros servicios.</p>")
                .append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder htmlAceptarEscalaTicket(IncidenciaVo incidenciaVo) {
         SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio())
                .append(this.getTitulo("Ticket " + incidenciaVo.getCodigo()))
                .append("Buen día, ")
                .append("<p> Se le informa que el ticket<b> # ").append(incidenciaVo.getCodigo()).append("</b>, ha sido aceptado como Segundo Nivel.</p>")
                .append("<p>Gracias por utilizar nuestros servicios.</p>")
                .append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

    
    public StringBuilder htmlNoAceptarEscalaTicket(IncidenciaVo incidenciaVo) {
         SiPlantillaHtml plantilla = this.plantillaHtml.find(1);
        this.limpiarCuerpoCorreo();
        this.cuerpoCorreo.append(plantilla.getInicio())
                .append(this.getTitulo("Ticket " + incidenciaVo.getCodigo()))
                .append("Buen día, ")
                .append("<p> Se le informa que el ticket <b> # ").append(incidenciaVo.getCodigo()).append("</b>, NO ha sido aceptado como Segundo Nivel.</p>")
                .append("<p>Gracias por utilizar nuestros servicios.</p>")
                .append(plantilla.getFin());
        return this.cuerpoCorreo;
    }

}
