/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.orden.impl;

import com.google.common.base.Strings;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import sia.constantes.Constantes;
import sia.correo.impl.EnviarCorreoImpl;
import sia.modelo.AutorizacionesOrden;
import sia.modelo.Compania;
import sia.modelo.Orden;
import sia.modelo.OrdenSiMovimiento;
import sia.modelo.orden.vo.ContactoOrdenVo;
import sia.modelo.orden.vo.OrdenCorreoVo;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.inventarios.OrdenFormatoVo;
import sia.notificaciones.estilos.Estilos;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.HtmlNotificaSistemaImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class NotificacionOrdenImpl extends Estilos {

    @Inject
    private EnviarCorreoImpl enviarCorreo;
    @Inject
    private CompaniaImpl companiaServicioRemoto;
    @Inject
    private HtmlNotificacionOrdenImpl html;
    @Inject
    private SiParametroImpl parametrosSistema;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;
    @Inject
    private SiParametroImpl siParametroRemote;
    @Inject
    private HtmlNotificaSistemaImpl htmlNotificaSistemaLocal;
    @Inject
    private SiUsuarioRolImpl usuarioRolRemote;
    //

    
    public boolean pruebaCorreo() {
        return this.enviarCorreo.enviarCorreoIhsa("mluis@ihsa.mx", "", "", "Prueba de sistema", this.cuerpoMensajePruebaIhsa());
    }

//    private StringBuilder cuerpoMensajePruebaGmail() {
//        this.cuerpoCorreo.delete(0, this.cuerpoCorreo.length());
//        this.cuerpoCorreo.append("Hola, Usuario .<Br/><Br/>");
//        this.cuerpoCorreo.append("<table width=90% cellspacing=0><tr>");
//        this.cuerpoCorreo.append("<td > <img src='cid:logoCompany' class=logo_compania/> </td>");
//        this.cuerpoCorreo.append("<td class=nombre_compania colspan=3>IBEROAMERICANA DE HIDROCARBUROS S.A. DE C.V.</td>");
//        this.cuerpoCorreo.append("<td> <img src='cid:logoEsr' class=esr/> </td> </tr></table>");
//        this.cuerpoCorreo.append(" Este correo es una prueba con Autenticación en el servidor utilizando Gmail...");
//        this.cuerpoCorreo.append(" <Br/><Br/> Gracias, <Br/> El equipo del SIA. <Br/><Br/><Br/><Br/>");
//        this.cuerpoCorreo.append("<center><font face=arial size=1 color=red> Mensaje generado automáticamente ");
//        this.cuerpoCorreo.append("por el Sistema Integral de Administración v3. </font></center>");
//        return cuerpoCorreo;
//    }
    private StringBuilder cuerpoMensajePruebaIhsa() {
        this.cuerpoCorreo.delete(0, this.cuerpoCorreo.length());
        this.cuerpoCorreo.append("Hola, Usuario .<Br/><Br/>").append(" Este correo es una prueba con Autenticación en el servidor SMTP de IHSA...").append(" <Br/><Br/> Gracias, <Br/> El equipo del SIA. <Br/><Br/><Br/><Br/>").append("<center><font face=arial size=1 color=red> Mensaje generado automáticamente ").append("por el Sistema Integral de Administración v3. </font></center>");
        return cuerpoCorreo;
    }

    
    public boolean envioMailDireccionGeneralMontoAlto(String correoDireccion, String correoSia, List<OrdenVO> lor, double totalAcumulado, String inicio, String fin) {
        return enviarCorreo.enviarCorreoIhsa(correoDireccion, correoSia, "", "Notificación Monto acumulado OC/S", html.mensajeNotificacionMontoDireccion(lor, totalAcumulado, inicio, fin), parametrosSistema.find(1).getLogo());
    }

    
    public boolean sendMailNotificaOrdenSuperaMonto(String correoNotificaOrden, String correo, List<OrdenCorreoVo> listaCorreo, boolean mostrarEstatus) {
        String asunto = "Reporte de OC/S  (Monto acumulado) ";
        return enviarCorreo.enviarCorreoIhsa(correoNotificaOrden, "", correo, asunto + Constantes.FMT_TextDateLarge.format(new Date()),
                html.mensajeNotificacionMontoAcumulado(listaCorreo, asunto, mostrarEstatus), parametrosSistema.find(1).getLogo());
    }

    
    public boolean sendMailNotificaOrdenPorAutorizar(String correoNotificaOrden, String correo, List<OrdenCorreoVo> loAuto, List<OrdenCorreoVo> listaCorreo, boolean mostrarEstatus) {
        String asunto = "Reporte de OC/S - ";
        return enviarCorreo.enviarCorreoIhsa(
                correoNotificaOrden,
                "", correo,
                asunto + Constantes.FMT_ddMMyyy.format(new Date()),
                html.mensajeNotificacionAutorizarOrdeCompra(
                        asunto,
                        loAuto,
                        listaCorreo,
                        mostrarEstatus),
                parametrosSistema.find(1).getLogo()
        );
    }

    
    public boolean enviarNotificacionOrdenSolicitada(Orden orden, String asunto, List<ContactoOrdenVo> contactos, List<OrdenDetalleVO> items) {
        try {
            return enviarCorreo.enviarCorreoIhsa(
                    orden.getAnalista().getEmail(),
                    "", "", asunto,
                    html.msjNotificacionOrdenSolicitada(orden, contactos, items),
                    orden.getCompania().getLogo(),
                    orden.getCompania().getLogoEsr());
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            return false;
        }
    }

    
    public boolean enviarNotificacionAprobarOrden(String para, String cc, Orden orden, String asunto, List<ContactoOrdenVo> contactos, List<OrdenDetalleVO> items) {
        return enviarCorreo.enviarCorreoIhsa(para, "", "", asunto,
                html.msjNotificacionAprobarOrden(orden, contactos, items),
                orden.getCompania().getLogo(), orden.getCompania().getLogoEsr());
    }

    
    public boolean enviarNotificacionNotaOrden(String para, String cc, String cco, Orden orden, String asunto, String autor, String nota, List<ContactoOrdenVo> contactos) {
        return enviarCorreo.enviarCorreoIhsa(
                para,
                cc,
                cco,
                asunto,
                html.msjNotaOrden(orden, autor, nota, contactos),
                orden.getCompania().getLogo(),
                orden.getCompania().getLogoEsr());
    }

    
    public boolean enviarNotificacionDevolverOrden(String para, String cc, String cco, Orden orden, String asunto, List<ContactoOrdenVo> contactos, OrdenSiMovimiento movimiento, List<OrdenDetalleVO> items) {
        boolean enviado = enviarCorreo.enviarCorreoIhsa(
                para,
                cc,
                cco,
                asunto,
                html.msjRechazoOrden(orden, movimiento, contactos, items),
                orden.getCompania().getLogo(),
                orden.getCompania().getLogoEsr());
        if (enviado) {
            enviarCorreo.enviarCorreoIhsa(orden.getRequisicion().getSolicita().getEmail(),
                    Constantes.VACIO,
                    Constantes.VACIO,
                    "Orden de compra " + orden.getConsecutivo() + " - Devuelta",
                    html.msjRequisitorOrdenDevuelta(orden, items),
                    orden.getCompania().getLogo(), orden.getCompania().getLogoEsr());
        }
        return enviado;
    }

    
    public boolean enviarNotificacionCancelarOrden(String para, String cc, String cco, Orden orden, String asunto, AutorizacionesOrden autorizacionesOrden, List<ContactoOrdenVo> contactos, List<OrdenDetalleVO> items) {
        return enviarCorreo.enviarCorreoIhsa(
                para, cc, cco, asunto,
                html.msjCancelarOrden(orden, autorizacionesOrden, contactos, items),
                orden.getCompania().getLogo(),
                orden.getCompania().getLogoEsr());
    }

    
    public boolean enviarNotificacionOrden(String para, String conCopia, String copiasOcultas, Orden orden, String asunto, List<ContactoOrdenVo> contactos, List<OrdenDetalleVO> items) {
        return enviarCorreo.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto,
                html.msjNotificacionOrden(orden, contactos, items),
                orden.getCompania().getLogo(),
                orden.getCompania().getLogoEsr());
    }

    
    public boolean enviarNotificacionOrdenProveedor(Orden orden, List<ContactoOrdenVo> contactos, File pdf, File pdfCG, List<OrdenDetalleVO> items) {
        boolean retorno = false;

        try {

            StringBuilder c = new StringBuilder();
            StringBuilder copiasOcultas = new StringBuilder();
            List<UsuarioVO> lurv = usuarioRemote.traerRolPrincipalUsuarioRolModulo(Constantes.ROL_LOGISTICA_MATERIAL, Constantes.MODULO_REQUISICION, orden.getCompania().getRfc());
            if (lurv != null) {
                for (UsuarioVO usuarioVO : lurv) {
                    c.append(",").append(usuarioVO.getMail());
                }
            }

            copiasOcultas.append(orden.getAnalista().getEmail()).append(",").append(orden.getResponsableGerencia().getEmail());
            UsuarioResponsableGerenciaVo urv2 = gerenciaRemote.traerResponsablePorApCampoYGerencia(orden.getApCampo().getId(), Constantes.GERENCIA_ID_COMPRAS);
            if (urv2 != null) {
                c.append(",").append(urv2.getEmailUsuario());
            }
            //
            StringBuilder subject = new StringBuilder();
            subject.append("ORDEN: ");

            if (Strings.isNullOrEmpty(orden.getNavCode())) {
                subject.append(orden.getConsecutivo());
            } else {
                subject.append(orden.getNavCode())
                        .append(" de código interno (")
                        .append(orden.getConsecutivo()).append(")");
            }

            subject.append(" de la Requisición:(").append(orden.getRequisicion().getConsecutivo()).append(")");

            retorno = enviarCorreo.enviarCorreoIhsa(
                    getDestinatariosOrden(contactos),
                    Constantes.VACIO,
                    copiasOcultas.append(c).toString(),
                    subject.toString(),
                    html.msjNotificacionProveedor(orden, contactos),
                    orden.getCompania().getLogo(),
                    orden.getCompania().getLogoEsr(),
                    pdf,
                    pdfCG,
                    orden.getCompania().getSiglas());
            // enviar correo al requisitor
            if (retorno) {
                enviarCorreo.enviarCorreoIhsa(orden.getRequisicion().getSolicita().getEmail(),
                        Constantes.VACIO,
                        Constantes.VACIO,
                        "Orden de compra " + orden.getConsecutivo() + " - Enviada a proveedor",
                        html.msjRequisitorOrdenEnviada(orden, contactos, items),
                        orden.getCompania().getLogo(), orden.getCompania().getLogoEsr());
            }

        } catch (Exception ex) {
            UtilLog4j.log.fatal(ex);
            retorno = false;
        }

        return retorno;
    }

    
    public boolean enviarNotificacionOrdenAnalista(Orden orden) {
        boolean enviado = false;
        try {
            StringBuilder para = new StringBuilder();
            para.append(orden.getAnalista().getEmail());
            enviado = enviarCorreo.enviarCorreoIhsa(para.toString(), "", "",
                    new StringBuilder().append("Códigos de NAVISION Orden: ").append(orden.getConsecutivo()).toString(),
                    html.msjNotificacionAnalista(orden),
                    parametrosSistema.find(1).getLogo());
        } catch (Exception ex) {
            UtilLog4j.log.fatal(ex);
            enviado = false;
        }
        return enviado;
    }

    
    public boolean enviarNotificacionTarea(Orden orden, List<ContactoOrdenVo> contactos, List<OrdenDetalleVO> items) {
        boolean retorno = false;
        StringBuilder para = new StringBuilder();
        para.append(orden.getAnalista().getEmail());
        retorno = enviarCorreo.enviarCorreoIhsa(
                para.toString(),
                "",
                "",
                new StringBuilder().append("CARGAR ORDEN: ").append(orden.getConsecutivo()).append(" EN NAVISION").toString(),
                html.msjNotificacionOrden(orden, contactos, items),
                orden.getCompania().getLogo(), orden.getCompania().getLogoEsr());
        return retorno;
    }

    private String getEmails(List<UsuarioVO> usrs) {
        StringBuilder mails = new StringBuilder();
        boolean primero = true;
        for (UsuarioVO usr : usrs) {
            if (primero) {
                mails.append(usr.getMail());
                primero = false;
            } else {
                mails.append(",").append(usr.getMail());
            }
        }
        return mails.toString();
    }

    
    public boolean enviarNotificacionCotabilidad(Orden orden, List<ContactoOrdenVo> contactos, List<OrdenDetalleVO> items) {
        boolean retorno = false;
        try {
            StringBuilder para = new StringBuilder();
            List<UsuarioVO> usrConta = usuarioRemote.traerListaRolPrincipalUsuarioRolModulo(35, 1, orden.getApCampo().getId());
            para.append(getEmails(usrConta));
            retorno = enviarCorreo.enviarCorreoIhsa(para.toString(), "", "",
                    new StringBuilder().append("GENERAR activos fijos de la Orden:").append(orden.getConsecutivo()).append(" en NAVISION").toString(),
                    html.msjNotificacionOrdenContabilidad(orden, contactos, items),
                    orden.getCompania().getLogo(), orden.getCompania().getLogoEsr());
        } catch (Exception ex) {
            Logger.getLogger(NotificacionOrdenImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    private String getDestinatariosOrden(List<ContactoOrdenVo> listaContactos) {
        StringBuilder destinatarios = new StringBuilder();
        for (ContactoOrdenVo lista : listaContactos) {
            if (destinatarios.toString().isEmpty()) {
                destinatarios.append(lista.getCorreo());
            } else {
                String newCorreo = lista.getCorreo().trim();
                int ascii = newCorreo.codePointAt(newCorreo.length() - 1);
                if (ascii < 65 || (ascii > 90 && ascii < 97) || ascii > 122) {
                    lista.setCorreo(newCorreo.substring(Constantes.CERO, newCorreo.length() - 1));
                }
                destinatarios.append(",").append(lista.getCorreo());
            }
        }
        return destinatarios.toString();
    }

    
    public boolean reenviarNotificacionOrdenProveedor(Orden orden, List<ContactoOrdenVo> contactos, File pdf, File pdfCG) {
        StringBuilder c = new StringBuilder();
        StringBuilder copiasOcultas = new StringBuilder();
        List<UsuarioVO> lurv = usuarioRemote.traerRolPrincipalUsuarioRolModulo(Constantes.ROL_LOGISTICA_MATERIAL, Constantes.MODULO_REQUISICION, orden.getCompania().getRfc());
        if (lurv != null) {
            for (UsuarioVO usuarioVO : lurv) {
                c.append(",").append(usuarioVO.getMail());
            }
        }
        copiasOcultas.append(orden.getAnalista().getEmail());

        StringBuilder subject = new StringBuilder();
        subject.append("ORDEN: ");
        if (orden.getNavCode() != null && !orden.getNavCode().isEmpty()) {
            subject.append(orden.getNavCode()).append(" de código interno (").append(orden.getConsecutivo()).append(")");
        } else {
            subject.append(orden.getConsecutivo());
        }
        subject.append(" de la Requisición:(").append(orden.getRequisicion().getConsecutivo()).append(")");

        return enviarCorreo.enviarCorreoIhsa(
                getDestinatariosOrden(contactos),
                "",
                copiasOcultas.append(c).toString(),
                subject.toString(),
                html.msjNotificacionProveedor(orden, contactos),
                orden.getCompania().getLogo(), orden.getCompania().getLogoEsr(), pdf, pdfCG, orden.getCompania().getSiglas());
    }

    
    public boolean enviarNotificacionCambioOrden(String para, String cc, String cco, List<OrdenVO> lo, String nombreAprobara, String nombreTiene, String rfcEmpresa, String status) {
        boolean retorno = false;
        Compania c = companiaServicioRemoto.find(rfcEmpresa);
        String sb = new StringBuilder().append("CAMBIO DE ORDEN(ES): ").append(" -- ").append(Constantes.FMT_ddMMyyy.format(new Date())).toString();
        retorno = enviarCorreo.enviarCorreoIhsa(para,
                cc,
                cco, sb,
                html.msjNotificacionCambioOrden(lo, nombreTiene, nombreAprobara, c.getNombre(), sb, status),
                parametrosSistema.find(1).getLogo());

        return retorno;
    }

    
    public void enviarExcepcionSIA(String para, String cc, String asunto, String compras, String opcion, String mensaje) {
        enviarCorreo.enviarCorreoIhsa(para, buscarCorreo(Constantes.ROL_DESARROLLO_SISTEMA), "", asunto, htmlNotificaSistemaLocal.mensajeNotificaError(asunto, mensaje), siParametroRemote.find(1).getLogo());
    }

    /**
     * MLUIS
     *
     * @return
     */
    private String buscarCorreo(String rol) {
        String correoPara = "";
        List<UsuarioRolVo> lu = siUsuarioRolRemote.traerRolPorCodigo(rol, Constantes.AP_CAMPO_DEFAULT, Constantes.MODULO_COMPRA);
        for (UsuarioRolVo ur : lu) {
            if (correoPara.isEmpty()) {
                correoPara = ur.getCorreo();
            } else {
                correoPara += "," + correoPara;
            }
        }
        return correoPara;

    }

    
    public void enviarExcepcionDesarrollo(String asunto, String compras, String opcion, String mensaje) {
        enviarCorreo.enviarCorreoIhsa(buscarCorreo(Constantes.ROL_DESARROLLO_SISTEMA), "", "", asunto, htmlNotificaSistemaLocal.mensajeExcepcion(asunto, compras, opcion, mensaje), siParametroRemote.find(1).getLogo());
    }

    
    public void enviarNotificacionOrdenSinAutorizar(String correoGerencia, List<OrdenVO> listaOrden, String campo) {
        String asunto = "OC/S pendientes de autorizar ( " + campo + " )";
        enviarCorreo.enviarCorreoIhsa(correoGerencia, "", "", asunto,
                html.msjNotificacionOrdenSinAutorizar(asunto, listaOrden),
                parametrosSistema.find(1).getLogo());
    }

    
    public void enviarNotificacionValidarPresupuesto(String correoGerencia, String partidas, String campo) {
        String asunto = "Validación de presupoesto para el bloque " + campo;
        enviarCorreo.enviarCorreoIhsa(correoGerencia, "", "", asunto,
                html.msjNotificacionValidarPresupuesto(asunto, "Por favor validar con la Gerencia de Costos. No se cuenta con saldo para : " + partidas + "."),
                parametrosSistema.find(1).getLogo());
    }

    
    public void enviarNotificacionValidarContrato(String correoGerencia, String contrato, String campo) {
        String asunto = "Validación de contrato para el bloque " + campo;
        enviarCorreo.enviarCorreoIhsa(correoGerencia, "", "", asunto,
                html.msjNotificacionValidarContrato(asunto, contrato),
                parametrosSistema.find(1).getLogo());
    }

    
    public void enviarNotificacionFormatoEntrada(String para, List<OrdenFormatoVo> formatos) {
        String asunto = "Formatos de entrada almacén";
        enviarCorreo.enviarCorreoIhsa(para, "", "", asunto, html.msjFormatoEntrada(asunto, formatos), parametrosSistema.find(1).getLogo());
    }

    
    public boolean enviarNotificacionRecepcionOrden(String para, String conCopia, String copiasOcultas, Orden orden, String asunto, List<OrdenDetalleVO> items, boolean isCompleta) {
        return enviarCorreo.enviarCorreoIhsa(para, conCopia, copiasOcultas, asunto,
                html.msjNotificacionRecepcionOrden(orden, items, isCompleta),
                orden.getCompania().getLogo(),
                orden.getCompania().getLogoEsr());
    }

    
    public boolean enviarCartaIntencion(Orden orden, List<ContactoOrdenVo> listaContactosOrden, List<OrdenDetalleVO> items) {
        return enviarCorreo.enviarCorreoIhsa(getDestinatariosOrden(listaContactosOrden), orden.getAnalista().getEmail(), "", "Carta de Intención",
                html.mensajeCartaIntencion(orden, items),
                orden.getCompania().getLogo(),
                orden.getCompania().getLogoEsr());
    }

    
    public boolean rechazarRepse(Orden orden, List<OrdenDetalleVO> items, String destinatariosOrden, String correoSesion, String motivo) {
        String correoCopia = usuarioRolRemote.traerCorreosPorCodigoRolList(Constantes.COD_ROL_ORDEN_REPSE, orden.getApCampo().getId());
        return enviarCorreo.enviarCorreoIhsa(destinatariosOrden, orden.getAnalista().getEmail(), correoCopia, "REPSE - Rechazada ",
                html.mensajeRechazarRepse(orden, items, motivo),
                orden.getCompania().getLogo(),
                orden.getCompania().getLogoEsr());
    }

    
    public boolean enviarCorreoAceptarCartaIntencion(Orden orden, List<OrdenDetalleVO> items, String asunto) {
        String correo = usuarioRolRemote.traerCorreosPorCodigoRolList(Constantes.COD_ROL_ORDEN_REPSE, orden.getApCampo().getId());
        return enviarCorreo.enviarCorreoIhsa(correo, "", "", asunto,
                html.mensajeAceptarCarta(orden, items),
                orden.getCompania().getLogo(),
                orden.getCompania().getLogoEsr());
    }

    
    public boolean enviarMensajeRechazoCartaIntencion(Orden orden, List<OrdenDetalleVO> items, String asunto, String motivo) {
        return enviarCorreo.enviarCorreoIhsa(orden.getAnalista().getEmail(), "", "", asunto,
                html.mensajeRechazarCarta(orden, items, motivo),
                orden.getCompania().getLogo(),
                orden.getCompania().getLogoEsr());
    }

}
