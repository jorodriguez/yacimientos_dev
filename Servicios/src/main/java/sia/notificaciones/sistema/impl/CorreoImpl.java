/*
 * CorreoImpl.java
 * Creado el 30/06/2009, 10:39:53 AM
 * EJB con estado desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este EJB con estado (Stateless Session EJB), asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.notificaciones.sistema.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.correo.impl.EnviarCorreoImpl;
import sia.modelo.Compania;
import sia.modelo.ContactosOrden;
import sia.modelo.NotaOrden;
import sia.modelo.NotaRequisicion;
import sia.modelo.OcOrdenEts;
import sia.modelo.Orden;
import sia.modelo.ReRequisicionEts;
import sia.modelo.Requisicion;
import sia.modelo.SiNotificacion;
import sia.modelo.SiNotificacionAdjunto;
import sia.modelo.SiPlantillaHtml;
import sia.modelo.Usuario;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.comunicacion.impl.SiNotificacionAdjuntoImpl;
import sia.servicios.orden.impl.InvitadosNotaOrdenImpl;
import sia.servicios.orden.impl.OcOrdenEtsImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.requisicion.impl.InvitadosNotaRequisicionImpl;
import sia.servicios.requisicion.impl.ReRequisicionEtsImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiPlantillaHtmlImpl;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com @date 30/06/2009
 */
@LocalBean 
public class CorreoImpl {

    //Servicios
    @Inject
    private ReRequisicionEtsImpl reRequisicionEtsRemote;
    @Inject
    private InvitadosNotaRequisicionImpl invitadosNotaRequisicionServicioRemoto;
    @Inject
    private InvitadosNotaOrdenImpl invitadosNotaOrdenRemote;
    @Inject
    private OrdenImpl ordenRemote;
    @Inject
    private RequisicionImpl requisicionRemote;
    @Inject
    private EnviarCorreoImpl enviarCorreoRemote;
    @Inject
    private SiPlantillaHtmlImpl plantillaHtmlRemote;
    @Inject
    private SiNotificacionAdjuntoImpl siNotificacionAdjuntoRemote;
    @Inject
    private SiParametroImpl siParametroRemote;
    @Inject
    private OcOrdenEtsImpl ocOrdenEtsRemote;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;
    @Inject
    private UsuarioImpl usuarioRemote;
    //Clases
    private DecimalFormat formatoMoneda = new DecimalFormat("###,###,###.##");
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoHora = new SimpleDateFormat("hh:mm a");
    private SimpleDateFormat formatoFechaLargo = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));
    private String s, f, f1;//, cuerpoMensaje = "";
    private Integer a;
    private String copiasOcultas = "";

    
    @Deprecated
    public boolean enviarCorreo(String para, String conCopia, String copiasOcultas, String asunto, boolean formatoHTML, StringBuilder cuerpoDelMensaje, boolean debug) {
	return this.enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto, cuerpoDelMensaje);
    }

    private String getEts(int idRequisicion) {
	StringBuilder cuerpoEtsSB = new StringBuilder();
	//if (!this.etsServicioImpl.traerArchivos(1, idRequisicion, "ETS").isEmpty()) {
	if (!this.reRequisicionEtsRemote.traerAdjuntosPorRequisicion(idRequisicion).isEmpty()) {
	    f = "<td bgcolor=#ffffff valign=middle><font face=arial size=-1>";
	    f1 = "<td bgcolor=#ffffff valign=middle align=right><font face=arial size=-1>";

	    cuerpoEtsSB.append("<center><table bgcolor=#bcbcbc width=90%><tr><td><font face=arial><center>Especificación Técnica de Suministro.</center></table></center>").append("<table width=90% bgcolor=#000000 cellspacing=1 cellpadding=2 border=0>");
	    //---------------------- el encabezado de las lineas ------
	    f = "<td bgcolor=#000000 valign=middle ><font face=arial size=-1 color=ffffff><center>";
	    cuerpoEtsSB.append("<tr>").append(f).append("Núm.").append(f).append("Nombre").append(f).append("Descripción").append(f);

	    a = 1;
	    //for (SiAdjunto ets : this.etsServicioImpl.traerArchivos(1, idRequisicion, "ETS")) {
	    for (ReRequisicionEts ets : this.reRequisicionEtsRemote.traerAdjuntosPorRequisicion(idRequisicion)) {
		if (getModulo(a).equals(0)) {
		    f = "<td bgcolor=#dfdfdf valign=middle><font face=arial size=-1>";
		    f1 = "<td bgcolor=#dfdfdf valign=middle align=right><font face=arial size=-1>";
		} else {
		    f = "<td bgcolor=#ffffff valign=middle><font face=arial size=-1>";
		    f1 = "<td bgcolor=#ffffff valign=middle align=right><font face=arial size=-1>";
		}

		cuerpoEtsSB.append("<tr>");
		s = f + "<center>" + a.toString();
		s = s + f + ets.getSiAdjunto().getNombreSinUUID();
		s = s + f + ets.getSiAdjunto().getDescripcion();
		//-- poner el link para abrir el archivo

		s = s + f + "<center><A HREF='" + Configurador.urlSia() + "Compras/OFWSS?Z4BX2=SIA&ZWZ4W=" + ets.getSiAdjunto().getId() + "&ZWZ3W=" + ets.getSiAdjunto().getUuid() + " TARGET=_new'> Abrir </A></center>";
		cuerpoEtsSB.append(s);
		a = a + 1;
	    }
	    cuerpoEtsSB.append("</table><br>").append("<center><table bgcolor=#bcbcbc width=90%><tr><td><font face=arial><center>Items.</center></table></center>");
	}
	return cuerpoEtsSB.toString();
    }

    private String getEtsOrdenCategoria(int idOrden) {
	String cuerpoEts = "";
	String ff = "";
	String f11 = "";
	String ss = "";
	Integer aa;
        // 
	if (!ocOrdenEtsRemote.traerOcOrdenEts(idOrden, 2).isEmpty()) {
            //FIXME : no se deben concatenar cadenas de texto con +=, revisar si es posible utilizar plantillas con Velocity
	    cuerpoEts += "<center>";
	    cuerpoEts += "<table bgcolor=#bcbcbc width=90%><tr><td><font face=arial><center>Especificación Técnica de Suministro.</center></table>";
	    cuerpoEts += "</center>";
	    cuerpoEts += "<center>";
	    cuerpoEts += "<table width=90% bgcolor=#000000 cellspacing=1 cellpadding=2 border=0>";
	    cuerpoEts += "<tr>";
	    cuerpoEts += "<td bgcolor=#000000 valign=middle ><font face=arial size=-1 color=ffffff>";
	    cuerpoEts += "<center>Núm.</center>";
	    cuerpoEts += "</td>";
	    cuerpoEts += "<td bgcolor=#000000 valign=middle ><font face=arial size=-1 color=ffffff>";
	    cuerpoEts += "<center>Nombre</center>";
	    cuerpoEts += "</td>";
	    cuerpoEts += "<td bgcolor=#000000 valign=middle ><font face=arial size=-1 color=ffffff>";
	    cuerpoEts += "<center>Descripción</center>";
	    cuerpoEts += "</td>";
	    cuerpoEts += "<td bgcolor=#000000 valign=middle ><font face=arial size=-1 color=ffffff>";
	    cuerpoEts += "</td>";
	    cuerpoEts += "</tr>";
	    aa = 1;
	    for (OcOrdenEts ets : ocOrdenEtsRemote.traerOcOrdenEts(idOrden, 2)) {
		if (getModulo(aa).equals(0)) {
		    ff = "<td bgcolor=#dfdfdf valign=middle><font face=arial size=-1>";
		    f11 = "<td bgcolor=#dfdfdf valign=middle align=right><font face=arial size=-1>";
		} else {
		    ff = "<td bgcolor=#ffffff valign=middle><font face=arial size=-1>";
		    f11 = "<td bgcolor=#ffffff valign=middle align=right><font face=arial size=-1>";
		}
		cuerpoEts += "<tr>";
		cuerpoEts += ff + "<center>" + aa.toString() + "</center>";
		if (ets.getSiAdjunto().getNombre() != null) {
		    cuerpoEts += ff + ets.getSiAdjunto().getNombreSinUUID() + "</td>";
		} else {
		    cuerpoEts += ff + "</td>";
		}
		if (ets.getSiAdjunto().getDescripcion() != null) {
		    cuerpoEts += ff + ets.getSiAdjunto().getDescripcion() + "</td>";
		} else {
		    cuerpoEts += ff + "</td>";
		}
		cuerpoEts += ff + "<center><A HREF='" + Configurador.urlSia() + "Compras/OFWSS?Z4BX2=SIA&ZWZ4W=" + ets.getSiAdjunto().getId() + "&ZWZ3W=" + ets.getSiAdjunto().getUuid() + " TARGET=_new>' Abrir </A></center> </td>";
		cuerpoEts += "</tr>";
		aa = aa + 1;
	    }
	    cuerpoEts += "</table></center><br><br>";
	    cuerpoEts += "<center><table bgcolor=#bcbcbc width=90%><tr><td><font face=arial><center>Items.</font></center></td></tr></table></center>";
	}
	return cuerpoEts;
    }

    
    public StringBuilder mensajeCancelar(Requisicion requisicion, Usuario usuarioConectado) {
	StringBuilder cuerpoMensajeSB = new StringBuilder();
	cuerpoMensajeSB.append("<H3>" + "Requisición: " + requisicion.getConsecutivo() + "</H3>");
	cuerpoMensajeSB.append("<center><table width=90% bgcolor=#000000 cellspacing=1 cellpadding=2 border=0>");
	//---------------------- el encabezado de las lineas ------
	f = "<td bgcolor=#000000 valign=middle ><font face=arial size=-1 color=ffffff><center>";
	cuerpoMensajeSB.append("<tr>").append(f).append("Operación").append(f).append("Usuario").append(f).append("Fecha").append(f).append("Hora").append(f).append("Motivo");
	//---------------------------------------------------------
	f = "<td bgcolor=#ffffff valign=middle><font face=arial size=-1>";
	f1 = "<td bgcolor=#ffffff valign=middle align=center><font face=arial size=-1>";
	//---------------------------------------------------------
	cuerpoMensajeSB.append("<tr>");
	s = f + "<center>" + "Cancelo";
	s = s + f + usuarioConectado.getNombre();
	s = s + f + formatoFecha.format(requisicion.getFechaCancelo());
	s = s + f + formatoHora.format(requisicion.getHoraCancelo());
	s = s + f + requisicion.getMotivoCancelo();
	cuerpoMensajeSB.append(s);
	cuerpoMensajeSB.append("</table><br>").append("<Br>").append("<FONT FACE='arial' SIZE=1>").append("mensaje generado automáticamente por el sistema integral de administración. </FONT> <Br>");

	return cuerpoMensajeSB;
    }

    private Integer getModulo(Integer Valor) {
	return Valor % 2;
    }

    
    public StringBuilder mensajeRequisicionAutorizada(Requisicion requisicion) {
	StringBuilder cuerpoMensajeSB = new StringBuilder();
//        Cuerpo = "<H3>" + "Requisición: " + requisicion.getConsecutivo() + "</H3>";
	cuerpoMensajeSB.append("<center><table width=90% bgcolor=000066 cellspacing=1 cellpadding=2 border=0>");
	//---------------------- el encabezado de las lineas ------
	f = "<td bgcolor=000066 valign=middle ><font face=arial size=-1 color=ffffff><center>";
	cuerpoMensajeSB.append("<tr>").append(f).append("Operación").append(f).append("Usuario").append(f).append("Fecha").append(f).append("Hora");
	//---------------------------------------------------------
	f = "<td bgcolor=#ffffff valign=middle><font face=arial size=-1>";
	f1 = "<td bgcolor=#dfdfdf valign=middle><font face=arial size=-1>";
	// Información del estatus
	cuerpoMensajeSB.append("<tr>");
	s = f + "<center>" + "Solicitó";
	s = s + f + "<center>" + requisicion.getSolicita().getNombre();
	s = s + f + "<center>" + formatoFecha.format(requisicion.getFechaSolicito());
	s = s + f + "<center>" + formatoHora.format(requisicion.getHoraSolicito());
	cuerpoMensajeSB.append(s);
	//-------------------------------------------------------------
	cuerpoMensajeSB.append("<tr>");
	s = f1 + "<center>" + "Revisó";
	s = s + f1 + "<center>" + requisicion.getRevisa().getNombre();
	s = s + f1 + "<center>" + formatoFecha.format(requisicion.getFechaReviso());
	s = s + f1 + "<center>" + formatoHora.format(requisicion.getHoraReviso());
	cuerpoMensajeSB.append(s);
	//-------------------------------------------------------------
	cuerpoMensajeSB.append("<tr>");
	s = f + "<center>" + "Aprobó";
	s = s + f + "<center>" + requisicion.getAprueba().getNombre();
	s = s + f + "<center>" + formatoFecha.format(requisicion.getFechaAprobo());
	s = s + f + "<center>" + formatoHora.format(requisicion.getHoraAprobo());
	cuerpoMensajeSB.append(s);
	//-------------------------------------------------------------
//        if ((requisicion.getMontototalUsd() >= 5000) || (requisicion.getAutoriza() != null)) {
//            cuerpoMensaje = cuerpoMensaje + "<tr>";
//            s = f1 + "<center>" + "Autorizo";
//            s = s + f1 + "<center>" + requisicion.getAutoriza().getNombre();
//            s = s + f1 + "<center>" + formatoFecha.format(requisicion.getFechaAutorizo());
//            s = s + f1 + "<center>" + formatoHora.format(requisicion.getHoraAutorizo());
//            cuerpoMensaje = cuerpoMensaje + s;
//        }
//
//        //--------------- Si monto es mayor de 10000 dolares mostrar ----------
//        if ((requisicion.getMontototalUsd() >= 20000) || (requisicion.getVistoBueno() != null)) {
//            cuerpoMensaje = cuerpoMensaje + "<tr>";
//            s = f + "<center>" + "Visto Bueno";
//            s = s + f + "<center>" + requisicion.getVistoBueno().getNombre();
//            s = s + f + "<center>" + formatoFecha.format(requisicion.getFechaVistoBueno());
//            s = s + f + "<center>" + formatoHora.format(requisicion.getHoraVistoBueno());
//            cuerpoMensaje = cuerpoMensaje + s;
//        }
	cuerpoMensajeSB.append("</table><br>");
	// Quien colocara la orden de compra.
	cuerpoMensajeSB.append("Analista asignado para colocar la orden de compra y/o servicio: " + requisicion.getCompra().getNombre() + "<Br><Br>");
	// Cuerpo de la requisicion
//        f = "<td valign=middle><font face=arial size=-1>";
//        s = f + "<b>";
//        f1 = "<td valign=middle align=right><font face=arial size=-1><b>";
//        cuerpoMensaje = cuerpoMensaje + "<center><table bgcolor=#bcbcbc width=90%><tr><td><font face=arial><center>Requisición Interna de Compras (NO NEGOCIABLE CON PROVEEDOR)</table><br>" +
//                "<center><table width=90%>" +
//                "<tr>" + f + "Compañía" + s + requisicion.getCompania().getNombre() +
//                f + "No. de requisición" + f1 + requisicion.getConsecutivo() +
//                "<tr>" + f + "Tipo de obra" + s + requisicion.getTipoObra().getNombre() +
//                f + "Solicitada" + f1 + formatoFecha.format(requisicion.getFechaSolicito()) +
//                "<tr>" + f + "Proyecto" + s + requisicion.getProyectoOt().getNombre() +
//                f + "Requerida" + f1 + formatoFecha.format(requisicion.getFechaRequerida()) +
//                "<tr>" + f + "Gerencia" + s + requisicion.getGerencia().getNombre() +
//                f + "Prioridad" + f1 + requisicion.getPrioridad().getNombre() +
//                //     "<tr>"+f+"Documentos anexos"+"<td valign=middle colspan=3><font face=arial size=-1><b>"+
//                //if ff.Items.Count=0 then mail.Lines.Add('Ninguno') else mail.Lines.Add(inttostr(ff.Items.Count)+' (documento(s) anexo(s) en correo posterior)');
//                    "<tr>" + f + "Monto (MXP)" + s + "$" + formatoMoneda.format(requisicion.getMontoMn()) +
//                    f + "Monto (USD)" + f1 + "$" + formatoMoneda.format(requisicion.getMontoUsd()) +
//                    "<tr>" + f + "Proveedor" + s + requisicion.getProveedor() +
//                    f + "Monto total (USD)" + f1 + "$" + formatoMoneda.format(requisicion.getMontototalUsd()) +
//                "<tr>" + f + "Lugar de entrega" + "<td valign=middle colspan=3><font face=arial size=-1><b>" + requisicion.getLugarEntrega() +
//                "</table>" +
//                "<br>" +
//                "<center><table width=90% bgcolor=#000000 cellspacing=1 cellpadding=2 border=0>";
//        //---------------------- el encabezado de las lineas ------
//        f = "<td bgcolor=#000000 valign=middle ><font face=arial size=-1 color=ffffff><center>";
//        cuerpoMensaje = cuerpoMensaje + "<tr>" + f + "Partida" + f + "No. de parte" + f + "Cantidad" + f + "Unidad" + f + "Descripción" + f + "Precio Unitario" + f + "Importe" + f + "Moneda";
//        //---------------------------------------------------------
//        f = "<td bgcolor=#ffffff valign=middle><font face=arial size=-1>";
//        f1 = "<td bgcolor=#ffffff valign=middle align=right><font face=arial size=-1>";
//        //---------------------------------------------------------
//
//        a = 1;
//        for (RequisicionDetalle Lista : requisicionServicioRemoto.getItems(requisicion.getId())) {
//            //--- Poner condiciòn de mostrar solo los Items  Autorizados
//            if (Lista.getAutorizado().equals("Si")) {
//                if (getModulo(a).equals(0)) {
//                    f = "<td bgcolor=#dfdfdf valign=middle><font face=arial size=-1>";
//                    f1 = "<td bgcolor=#dfdfdf valign=middle align=right><font face=arial size=-1>";
//                } else {
//                    f = "<td bgcolor=#ffffff valign=middle><font face=arial size=-1>";
//                    f1 = "<td bgcolor=#ffffff valign=middle align=right><font face=arial size=-1>";
//                }
//
//                cuerpoMensaje = cuerpoMensaje + "<tr>";
//                s = f + "<center>" + a.toString();
//                s = s + f + Lista.getNumeroParte();
//                s = s + f1 + Lista.getCantidadAutorizada().toString();
//                s = s + f + Lista.getUnidad();
//                s = s + f + Lista.getDescripcionSolicitante();
//                // if bases.RequisicionArtServ.fieldbyname('MontoMN').asfloat > 0 then
//                s = s + f1 + "$" + formatoMoneda.format(Lista.getPrecioUnitario());
//                // else
//                //  s:=s+f1+format('%m',[bases.RequisicionArtServ.fieldbyname('MontoDLLS').asfloat / bases.RequisicionArtServ.fieldbyname('Cantidad').asinteger]);
//                s = s + f1 + "$" + formatoMoneda.format(Lista.getImporte());
//                s = s + f + Lista.getMoneda().getNombre();
//                cuerpoMensaje = cuerpoMensaje + s;
//                a = a + 1;
//            }
//        }
//        cuerpoMensaje = cuerpoMensaje + "</table><br>";
//        f = "<td bgcolor=#bcbcbc><center><font face=arial size=-1><center>";
//        cuerpoMensaje = cuerpoMensaje + "<center><table width=90%>" +
//                "<tr>" + f + "Solicita" + f + "Revisa" + f + "Aprueba" +
//                "<tr><td width=33%>&nbsp;<td width=33%>&nbsp;<td width=33%>&nbsp;" +
//                //"<tr><td>&nbsp;<td>&nbsp;<td>&nbsp;" +
//                "<tr><td>&nbsp;<td>&nbsp;<td>&nbsp;<td>&nbsp;";
//        f = "<td><center><font face=arial size=-1><center>";
//        cuerpoMensaje = cuerpoMensaje + "<tr>" + f + requisicion.getSolicita().getNombre() + f + requisicion.getRevisa().getNombre() + f + requisicion.getAprueba().getNombre();
//        f = "<td><center><font face=arial size=-2><center>";
//        cuerpoMensaje = cuerpoMensaje + "<tr>" + f + requisicion.getSolicita().getPuesto() + f + requisicion.getRevisa().getPuesto() + f + requisicion.getAprueba().getPuesto() +
//                "</table>" +
//                "<br>";
//        //--- Por si es mayor de 5000 Dolares y menor de 10000
//        if ((requisicion.getMontototalUsd() >= 5000) & (requisicion.getMontototalUsd() < 20000)) {
//            f = "<td bgcolor=#bcbcbc><center><font face=arial size=-1><center>";
//            f1 = "<td bgcolor=white><center><font face=arial size=-1><center>";
//            cuerpoMensaje = cuerpoMensaje + "<center><table width=90%>" +
//                    "<tr>" + f1 + "" + f + "Autoriza" + f1 + "" +
//                    "<tr><td width=33%>&nbsp;<td width=33%>&nbsp;<td width=33%>&nbsp;" +
//                    //  "<tr><td>&nbsp;<td>&nbsp;<td>&nbsp;" +
//                    "<tr><td>&nbsp;<td>&nbsp;<td>&nbsp;";
//            f = "<td><center><font face=arial size=-1><center>";
//            cuerpoMensaje = cuerpoMensaje + "<tr>" + f + "" + f + requisicion.getAutoriza().getNombre() + f + "";
//            f = "<td><center><font face=arial size=-2><center>";
//            cuerpoMensaje = cuerpoMensaje + "<tr>" + f + "" + f + requisicion.getAutoriza().getPuesto() + f + "" +
//                    "</table>" +
//                    "<br>";
//        }//-- Si es igual o mayor de 10000
//        else {
//            if (requisicion.getMontototalUsd() >= 20000) {
//                f = "<td bgcolor=#bcbcbc><center><font face=arial size=-1><center>";
//                f1 = "<td bgcolor=white><center><font face=arial size=-1><center>";
//                cuerpoMensaje = cuerpoMensaje + "<center><table width=90%>" +
//                        "<tr>" + f1 + "" + f + "Autoriza" + f + "Visto Bueno" + f1 + "" +
//                        "<tr><td width=25%>&nbsp;<td width=25%>&nbsp;<td width=25%>&nbsp;<td width=25%>&nbsp;" +
//                        //  "<tr><td>&nbsp;<td>&nbsp;<td>&nbsp;" +
//                        "<tr><td>&nbsp;<td>&nbsp;<td>&nbsp;";
//                f = "<td><center><font face=arial size=-1><center>";
//                cuerpoMensaje = cuerpoMensaje + "<tr>" + f + "" + f + requisicion.getAutoriza().getNombre() + f + requisicion.getVistoBueno().getNombre() + f + "";
//                f = "<td><center><font face=arial size=-2><center>";
//                cuerpoMensaje = cuerpoMensaje + "<tr>" + f + "" + f + requisicion.getAutoriza().getPuesto() + f + requisicion.getVistoBueno().getPuesto() + f + "" +
//                        "</table>" +
//                        "<br>";
//            }
//        }
//
//        f = "<TR><td><p align=justify><font face=arial size=-1>";
//        cuerpoMensaje = cuerpoMensaje + "<center><table width=80%>";
//        if (requisicion.getObservaciones().equals("")) {
//        } else {
//            cuerpoMensaje = cuerpoMensaje + f + "<b>Observaciones de la requisición:</b>";
//            f = "<TR><td><p align=justify><font face=arial size=-2>";
//            cuerpoMensaje = cuerpoMensaje + f + requisicion.getObservaciones();// +
//        }
//
//        cuerpoMensaje = cuerpoMensaje + "<Br><Br>";
//        a = 1;
//        for (RequisicionDetalle Lista : requisicionServicioRemoto.getItems(requisicion.getId())) {
//            if (Lista.getAutorizado().equals("Si")) {
//                if (Lista.getObservaciones().equals("")) {
//                } else {
//                    cuerpoMensaje = cuerpoMensaje + "Observación partida " + a.toString() + ": " + Lista.getObservaciones() + "<Br>";
//                }
//                a = a + 1;
//            }
//        }
//        cuerpoMensaje = cuerpoMensaje + "</table>";
	return cuerpoMensajeSB;
    }

    /**
     *
     * Inicia Zona de la nueva implementación de envio de correos (Optimizado)
     *
     */
    private enum ComandoRequisicion {
        //FIXME : los elementos deben estar en mayusculas
	solicitarRequisicion, revisarRequisicion, aprobarRequisicion, autorizarRequisicion,
	vistoBuenoRequisicion, asignarRequisicion, cancelarRequisicion, novalue;

	public static ComandoRequisicion getOpcion(String Str) {
	    try {
		return valueOf(Str);
	    } catch (Exception ex) {
		return novalue;
	    }
	}
    }

    /**
     * Metodo para enviar correos desde los diferentes modulos del sia
     * (Web,Escritorio,Movil)
     *
     * @param tipoCorreo = (revisar,aprobar,autorizar etc una requisición),
     * usuarioConectado,requisicion
     * @return boolean true si se pudo enviar o false si no se logro el envio
     */
    
    public boolean enviarCorreo(String tipoCorreo, Usuario usuarioConectado, Requisicion requisicion) {
	boolean correoEnviado = false;

	switch (ComandoRequisicion.getOpcion(tipoCorreo)) {
	case solicitarRequisicion:

	    break;
	case revisarRequisicion:

	    break;
	case aprobarRequisicion:

	    break;
	case autorizarRequisicion:

	    break;
	case vistoBuenoRequisicion:

	    break;
	case asignarRequisicion:

	    break;
	case cancelarRequisicion:

	    break;
	default:	    
	    break;
	}
	return correoEnviado;
    }

    private boolean enviar(String para, String conCopia, String copiasOcultas, String asunto, StringBuilder mensaje) {
	return this.enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto, mensaje);
    }

    private boolean enviarOrden(String de, String para, String conCopia, String copiasOcultas, String asunto, StringBuilder mensaje, byte[] logo) {
	return this.enviarCorreoRemote.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto, mensaje, logo, null);
    }

//-- Tomar los destinatarios de la notificacion de rechazo de una requisicion
    private String getDestinatarios(Requisicion requisicion) {
	StringBuilder destinatariosSB = new StringBuilder();
	if (requisicion.getEstatus().getId() == 10) {
//            destinatarios = new String[1];
	    destinatariosSB.append(requisicion.getSolicita().getDestinatarios());
	}
	if (requisicion.getEstatus().getId() == 15) {
//            destinatarios = new String[2];
	    destinatariosSB.append(requisicion.getSolicita().getEmail());
	    destinatariosSB.append(",").append(requisicion.getRevisa().getEmail());
	}
	if (requisicion.getEstatus().getId() == 20) {
//            destinatarios = new String[3];
	    destinatariosSB.append(requisicion.getSolicita().getEmail());
	    destinatariosSB.append(",").append(requisicion.getRevisa().getEmail());
	    destinatariosSB.append(",").append(requisicion.getAprueba().getEmail());
	}
	if (requisicion.getEstatus().getId() == 40) {
//            destinatarios = new String[4];
	    destinatariosSB.append(requisicion.getSolicita().getEmail());
	    destinatariosSB.append(",").append(requisicion.getRevisa().getEmail());
	    destinatariosSB.append(",").append(requisicion.getAprueba().getEmail());
	    destinatariosSB.append(",").append(requisicion.getAsigna().getEmail());
	}
	return destinatariosSB.toString();
    }

    private String getDestinatariosOrden(List<ContactosOrden> listaContactos) {
	String destinatarios = "";
	for (ContactosOrden lista : listaContactos) {
	    if (destinatarios.isEmpty()) {
		destinatarios = lista.getContactoProveedor().getCorreo();
	    } else {
		destinatarios = destinatarios + "," + lista.getContactoProveedor().getCorreo();
	    }
	}
	return destinatarios;
    }

    private String getContactosOrden(List<ContactosOrden> listaContactos) {
	String destinatarios = "";
	for (ContactosOrden lista : listaContactos) {
	    if (destinatarios.isEmpty()) {
		destinatarios = lista.getContactoProveedor().getNombre();
	    } else {
		destinatarios = destinatarios + "," + lista.getContactoProveedor().getNombre();
	    }
	}
	return destinatarios;
    }

    private String getTotalesOrden(Orden orden) {
	f = "<td bgcolor=#ffffff valign=middle width=15%><font face=arial size=-1 >";
	f1 = "<td bgcolor=#ffffff valign=middle align=right width=25%><font face=arial size=-1>";
	String totales = "<tr>";
	s = f + "<center>" + "";
	s = s + f + "";
	s = s + f + "";
	s = s + f + "";
	s = s + f + "";
	s = s + f1 + "SUBTOTAL:";
	if (orden.getSubtotal() == null) {
	    s = s + f1 + "";
	} else {
	    s = s + f1 + "$" + formatoMoneda.format(orden.getSubtotal());
	}

	totales = totales + s;

	totales = totales + "<Tr>";
	s = f + "<center>" + "";
	s = s + f + "";
	s = s + f + "";
	s = s + f + "";
	s = s + f + "";
	if (!orden.isConIva()) {
	    s = s + f1 + "";
	    s = s + f1 + "";
	} else {
	    s = s + f1 + orden.getPorcentajeIva() + ":";
	    s = s + f1 + "$" + formatoMoneda.format(orden.getIva());
	}

//        if (orden.getConIva().equals("No")) {
//            s = s + f1 + "";
//        } else {
//            s = s + f1 + "$" + formatoMoneda.format(orden.getDescuento());
//        }
	totales = totales + s;

	totales = totales + "<Tr>";
	s = f + "<center>" + "";
	s = s + f + "";
	s = s + f + "";
	s = s + f + "";
	s = s + f + "";
	if (!orden.isConIva()) {
	    s = s + f1 + "TOTAL SIN IVA:";
	} else {
	    s = s + f1 + "TOTAL CON IVA:";
	}

	if (orden.getTotal() == null) {
	    s = s + f1 + "";
	} else {
	    s = s + f1 + "$" + formatoMoneda.format(orden.getTotal());
	}
	totales = totales + s;

	totales = totales + "<Tr>";
	s = f + "<center>" + "";
	s = s + f + "";
	s = s + f + "";
	s = s + f + "";
	s = s + f + "";
	s = s + f1 + "MONEDA:";
	s = s + f1 + orden.getMoneda().getSiglas();
	totales = totales + s + "</table><br>";
	return totales;
    }

    private String cuerpoRequisito(Compania compania) {
	String cuerpoRequisitoFac;
	cuerpoRequisitoFac = "Empresa: ";
	cuerpoRequisitoFac += compania.getNombre();
	cuerpoRequisitoFac += "<br/> Domincilio: ";
	cuerpoRequisitoFac += compania.getDomicilioFiscal();
	cuerpoRequisitoFac += "<br/> RFC: ";
	cuerpoRequisitoFac += compania.getRfc();
	cuerpoRequisitoFac += "<br/>";
	cuerpoRequisitoFac += compania.getRequisitoFactura();
	return cuerpoRequisitoFac;
    }

    private String getAutorizacionesRequisicion(Requisicion requisicion) {
	StringBuilder autorizacionesSB = new StringBuilder();
	autorizacionesSB.append("<center>").append("HISTORIAL AUTORIZACIONES DE LA REQUISICIÓN:  " + requisicion.getConsecutivo()).append("<table width=90% bgcolor=000066 cellspacing=1 cellpadding=2 border=0>");
	//---------------------- el encabezado de las lineas ------
	f = "<td bgcolor=000066 valign=middle ><font face=arial size=-1 color=ffffff><center>";
	autorizacionesSB.append("<tr>").append(f).append("Operación").append(f).append("Usuario").append(f).append("Fecha").append(f).append("Hora");
	//---------------------------------------------------------
	f = "<td bgcolor=#ffffff valign=middle><font face=arial size=-1>";
	f1 = "<td bgcolor=#dfdfdf valign=middle><font face=arial size=-1>";

	autorizacionesSB.append("<tr>");
	s = f + "<center>" + "Solicitó";
	s = s + f + "<center>" + requisicion.getSolicita().getNombre();
	s = s + f + "<center>" + formatoFecha.format(requisicion.getFechaSolicito());
	s = s + f + "<center>" + formatoHora.format(requisicion.getHoraSolicito());
	autorizacionesSB.append(s);
	if (requisicion.getFechaReviso() != null) {
	    //-------------------------------------------------------------
	    autorizacionesSB.append("<tr>");
	    s = f1 + "<center>" + "Revisó";
	    s = s + f1 + "<center>" + requisicion.getRevisa().getNombre();
	    s = s + f1 + "<center>" + formatoFecha.format(requisicion.getFechaReviso());
	    s = s + f1 + "<center>" + formatoHora.format(requisicion.getHoraReviso());
	    autorizacionesSB.append(s);
	}
	if (requisicion.getFechaAprobo() != null) {
	    //-------------------------------------------------------------
	    autorizacionesSB.append("<tr>");
	    s = f + "<center>" + "Aprobó";
	    s = s + f + "<center>" + requisicion.getAprueba().getNombre();
	    s = s + f + "<center>" + formatoFecha.format(requisicion.getFechaAprobo());
	    s = s + f + "<center>" + formatoHora.format(requisicion.getHoraAprobo());
	    autorizacionesSB.append(s);
	}

	//-------------------------------------------------------------
//            if ((requisicion.getMontototalUsd() >= 5000) || (requisicion.getAutoriza() != null)) {
//                autorizaciones = autorizaciones + "<tr>";
//                s = f1 + "<center>" + "Autorizo";
//                s = s + f1 + "<center>" + requisicion.getAutoriza().getNombre();
//                s = s + f1 + "<center>" + formatoFecha.format(requisicion.getFechaAutorizo());
//                s = s + f1 + "<center>" + formatoHora.format(requisicion.getHoraAutorizo());
//                autorizaciones = autorizaciones + s;
//            }
//
//            //--------------- Si monto es mayor de 10000 dolares mostrar o si tiene aignado visto bueno tambien mostrar----------
//            if ((requisicion.getMontototalUsd() >= 20000) || (requisicion.getVistoBueno() != null)) {
//                autorizaciones = autorizaciones + "<tr>";
//                s = f + "<center>" + "Visto Bueno";
//                s = s + f + "<center>" + requisicion.getVistoBueno().getNombre();
//                s = s + f + "<center>" + formatoFecha.format(requisicion.getFechaVistoBueno());
//                s = s + f + "<center>" + formatoHora.format(requisicion.getHoraVistoBueno());
//                autorizaciones = autorizaciones + s;
//            }
	autorizacionesSB.append("</table><br>");

	return autorizacionesSB.toString();
    }

    private String getAutorizacionesOrden(Orden orden) {
	StringBuilder autorizacionesSB = new StringBuilder();
	autorizacionesSB.append("<center> HISTORIAL AUTORIZACIONES DE LA ORDEN: " + orden.getConsecutivo()).append("<center><table width=90% bgcolor=000066 cellspacing=1 cellpadding=2 border=0>");
	//---------------------- el encabezado de las lineas ------
	f = "<td bgcolor=000066 valign=middle ><font face=arial size=-1 color=ffffff><center>";
	autorizacionesSB.append("<tr>").append(f).append("Operación").append(f).append("Usuario").append(f).append("Fecha").append(f).append("Hora").append(f).append("Automaticamente");
	//---------------------------------------------------------
	f = "<td bgcolor=#ffffff valign=middle><font face=arial size=-1>";
	f1 = "<td bgcolor=#dfdfdf valign=middle><font face=arial size=-1>";
	//----- Notificación de que Solicitó una orden de compra
	autorizacionesSB.append("<tr>");
	s = f + "<center>" + "Solicitó";
	s = s + f + "<center>" + orden.getAutorizacionesOrden().getSolicito().getNombre();
	s = s + f + "<center>" + formatoFecha.format(orden.getAutorizacionesOrden().getFechaSolicito());
	s = s + f + "<center>" + formatoHora.format(orden.getAutorizacionesOrden().getHoraSolicito());
	s = s + f + "<center>" + "No";
	autorizacionesSB.append(s);
	if ((orden.getAutorizacionesOrden().getFechaAutorizoGerencia() != null) 
                && (orden.getAutorizacionesOrden().getAutorizaGerencia() != null)) {
	    autorizacionesSB.append("<tr>");
	    s = f + "<center>" + "Vo. Bo.";
	    s = s + f + "<center>" + orden.getAutorizacionesOrden().getAutorizaGerencia().getNombre();
	    s = s + f + "<center>" + formatoFecha.format(orden.getAutorizacionesOrden().getFechaAutorizoGerencia());
	    s = s + f + "<center>" + formatoHora.format(orden.getAutorizacionesOrden().getHoraAutorizoGerencia());
	    s = s + f + "<center>" + (orden.getAutorizacionesOrden().isAutorizacionGerenciaAuto() ? Constantes.PALABRA_SI : "No");
	    autorizacionesSB.append(s);
	}
	//-----
	if ((orden.getAutorizacionesOrden().getFechaAutorizoMpg() != null) 
                && (orden.getAutorizacionesOrden().getAutorizaMpg() != null)) {
	    autorizacionesSB.append("<tr>");
	    s = f + "<center>" + "Revisó";
	    s = s + f + "<center>" + orden.getAutorizacionesOrden().getAutorizaMpg().getNombre();
	    s = s + f + "<center>" + formatoFecha.format(orden.getAutorizacionesOrden().getFechaAutorizoMpg());
	    s = s + f + "<center>" + formatoHora.format(orden.getAutorizacionesOrden().getHoraAutorizoMpg());
	    s = s + f + "<center>" + (orden.getAutorizacionesOrden().isAutorizacionMpgAuto()  ? Constantes.PALABRA_SI : "No");
	    autorizacionesSB.append(s);
	}
	//-----
	if ((orden.getAutorizacionesOrden().getFechaAutorizoIhsa() != null) 
                && (orden.getAutorizacionesOrden().getAutorizaIhsa() != null)) {
	    autorizacionesSB.append("<tr>");
	    s = f + "<center>" + "Aprobo";
	    s = s + f + "<center>" + orden.getAutorizacionesOrden().getAutorizaIhsa().getNombre();
	    s = s + f + "<center>" + formatoFecha.format(orden.getAutorizacionesOrden().getFechaAutorizoIhsa());
	    s = s + f + "<center>" + formatoHora.format(orden.getAutorizacionesOrden().getHoraAutorizoIhsa());
	    s = s + f + "<center>" + (orden.getAutorizacionesOrden().isAutorizacionIhsaAuto() ? Constantes.PALABRA_SI : "No");
	    autorizacionesSB.append(s);
	}
	//-----
	if ((orden.getAutorizacionesOrden().getFechaAutorizoCompras() != null) 
                && (orden.getAutorizacionesOrden().getAutorizaCompras() != null)) {
	    autorizacionesSB.append("<tr>");
	    s = f + "<center>" + "Autorizo";
	    s = s + f + "<center>" + orden.getAutorizacionesOrden().getAutorizaCompras().getNombre();
	    s = s + f + "<center>" + formatoFecha.format(orden.getAutorizacionesOrden().getFechaAutorizoCompras());
	    s = s + f + "<center>" + formatoHora.format(orden.getAutorizacionesOrden().getHoraAutorizoCompras());
	    s = s + f + "<center>" + (orden.getAutorizacionesOrden().isAutorizacionComprasAuto()  ? Constantes.PALABRA_SI : "No");
	    autorizacionesSB.append(s);
	}
	autorizacionesSB.append("</table><br>");

	return autorizacionesSB.toString();
    }

    
    public boolean enviarCorreoOrdenCancelada(Orden orden) {
	StringBuilder cuerpoMensajeSB = new StringBuilder();
	cuerpoMensajeSB.append("<H3>Orden: ").append(orden.getConsecutivo()).append("</H3>");
	cuerpoMensajeSB.append("<center><table width=90% bgcolor=#000000 cellspacing=1 cellpadding=2 border=0>");

	//---------------------- el encabezado de las lineas ------
	f = "<td bgcolor=#000000 valign=middle ><font face=arial size=-1 color=ffffff><center>";
	cuerpoMensajeSB.append("<tr>").append(f).append("Operación").append(f).append("Usuario").append(f).append("Fecha").append(f).append("Hora").append(f).append("Motivo");
	//---------------------------------------------------------
	f = "<td bgcolor=#ffffff valign=middle><font face=arial size=-1>";
	f1 = "<td bgcolor=#ffffff valign=middle align=center><font face=arial size=-1>";
	//---------------------------------------------------------

	cuerpoMensajeSB.append(s);
	cuerpoMensajeSB.append("</table><br>").append("<Br>").append("<FONT FACE='arial' SIZE=1>").append("mensaje generado automáticamente por el sistema integral de administración. </FONT> <Br>");

	return this.enviar(orden.getAnalista().getEmail(), "", "", "ORDEN: " + orden.getConsecutivo() + " CANCELADA", cuerpoMensajeSB);

    }

    private String getCorreoInvitados(NotaOrden notaOrden) {
	String destinatarios = "";
	List<UsuarioVO> listaInvitados = this.invitadosNotaOrdenRemote.getInvitadosPorNota(notaOrden.getIdentificador(), notaOrden.getAutor().getId());
	for (UsuarioVO lista : listaInvitados) {
	    if (destinatarios.isEmpty()) {
		destinatarios = lista.getMail();
	    } else {
		destinatarios = destinatarios + "," + lista.getMail();
	    }
	}
	return destinatarios;
    }

    private String getCorreoInvitadosNotaRequisicion(NotaRequisicion notaRequisicion) {
	String destinatarios = "";
	//USUARIO VO por que ahi tiene el email y los destinatarios
	List<UsuarioVO> listaInvitados = this.invitadosNotaRequisicionServicioRemoto.getInvitadosPorNota(notaRequisicion.getIdentificador(), notaRequisicion.getAutor().getId());
	for (UsuarioVO lista : listaInvitados) {
	    if (destinatarios.isEmpty()) {
		destinatarios = lista.getMail(); //--Originalmente el atributo se llama EMail
	    } else {
		destinatarios = destinatarios + "," + lista.getMail();
	    }
	}
	return destinatarios;
    }

    
    public boolean enviarClave(Usuario para) {
	StringBuilder cuerpoMensajeSB = new StringBuilder();

	cuerpoMensajeSB.append("Hola, ").append(para.getNombre()).append(s).append(".<Br/><Br/> Recientemente solicitaste tu contraseña, la cual es: ").append(para.getClave()).append(" <Br/><Br/> Gracias, <Br/> El equipo del SIA. <Br/><Br/><Br/><Br/>" + "<center><font face=arial size=1> Mensaje generado automáticamente por el Sistema Integral de Administración. </font></center>");
        // esta es la evidencia q se pueden utilizar 2 dbs en un proyecto pero en diferentes ejb

//    Sistema  sistema= new Sistema();
//
//    sistema.setCompañia("Sistemas Acosta");
//    sistema.setNombre("Sistema de prueba...");
//    this.sistemaFacade.create(sistema);
	return this.enviar(para.getEmail(), "", "", "Contraseña de usuario", cuerpoMensajeSB);
    }

    //Envio de Informes
    
    public boolean enviarNotificacionInforme(SiNotificacion siNotificacion, String correos) {
	List<SiNotificacionAdjunto> ln = null;
	SiPlantillaHtml plantilla = this.plantillaHtmlRemote.find(1);
//        StringBuilder sb= new StringBuilder();
	//
	StringBuilder cuerpoMensajeSB = new StringBuilder();
	cuerpoMensajeSB.append(plantilla.getInicio());
	cuerpoMensajeSB.append("<header>");
	cuerpoMensajeSB.append("<h3 style= \"text-decoration:none; border-bottom: 1px dotted #b5b5b5; color: #004181;padding:0px 12px 0px 12px;\"> ");
	cuerpoMensajeSB.append(siNotificacion.getSiTipoNotificacion().getNombre()).append(siNotificacion.getCodigo()).append(siNotificacion.getTitulo());
	cuerpoMensajeSB.append("</h3>");
	cuerpoMensajeSB.append("</header>");
	cuerpoMensajeSB.append("</td>");
	cuerpoMensajeSB.append("</tr>");
	cuerpoMensajeSB.append("<tr>");
	cuerpoMensajeSB.append("<td colspan= \"2\">");
	cuerpoMensajeSB.append("<p>");
	cuerpoMensajeSB.append("El pasado ").append(formatoFechaLargo.format(siNotificacion.getFecha())).append(",");
	cuerpoMensajeSB.append("se aplicó ");
	cuerpoMensajeSB.append(siNotificacion.getSiTipoNotificacion().getNombre()).append(siNotificacion.getCodigo()).append(siNotificacion.getTitulo()).append(".");
	cuerpoMensajeSB.append(" La cual consiste en lo siguiente : ");
	cuerpoMensajeSB.append("</p>");
	cuerpoMensajeSB.append(siNotificacion.getMensaje());
	cuerpoMensajeSB.append("<br/>");
	ln = siNotificacionAdjuntoRemote.findAllNotificacionAdjuntoToNotificacion(siNotificacion);
	if (ln != null) {
	    cuerpoMensajeSB.append("<hr/>");
	    cuerpoMensajeSB.append("<table>");
	    cuerpoMensajeSB.append("<p style=\"font-size:10px;\"> Lista de archivos adjuntos a esta notificación </p>");
	    for (SiNotificacionAdjunto ad : ln) {
		cuerpoMensajeSB.append("<tr>");
		cuerpoMensajeSB.append("<td>");
		cuerpoMensajeSB.append("<a href =\"").append(Configurador.urlSia()).append("Compras/OFWSS?Z4BX2=SIA&ZWZ4W=").append(String.valueOf(ad.getSiAdjunto().getId())).append("&ZWZ3W=").append(String.valueOf(ad.getSiAdjunto().getUuid())).append("\">").append(ad.getSiAdjunto().getNombreSinUUID()).append("</a>");
		cuerpoMensajeSB.append("</td>");
		cuerpoMensajeSB.append("</tr>");
	    }
	    cuerpoMensajeSB.append("</table>");
	}
	cuerpoMensajeSB.append(plantilla.getFin());
	//
	return this.enviarCorreoRemote.enviarCorreoIhsa(correos, "", "", siNotificacion.getTitulo(), cuerpoMensajeSB, siParametroRemote.find(1).getLogo());
	//return true;
    }
}
