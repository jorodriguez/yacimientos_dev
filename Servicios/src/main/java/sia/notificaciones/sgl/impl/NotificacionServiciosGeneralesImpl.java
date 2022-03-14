/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.sgl.impl;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import sia.constantes.Constantes;
import sia.correo.impl.EnviarCorreoImpl;
import sia.excepciones.EmailNotFoundException;
import sia.modelo.CoComentario;
import sia.modelo.CoNoticia;
import sia.modelo.Convenio;
import sia.modelo.SgAvisoPago;
import sia.modelo.SgHuespedHotel;
import sia.modelo.SgHuespedStaff;
import sia.modelo.SgLicencia;
import sia.modelo.SgOficina;
import sia.modelo.SgOficinaAnalista;
import sia.modelo.SgPagoServicioVehiculo;
import sia.modelo.SgStaff;
import sia.modelo.SgStaffHabitacion;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgVehiculo;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.ComparteCon;
import sia.modelo.cursoManejo.vo.CursoManejoVo;
import sia.modelo.licencia.vo.LicenciaVo;
import sia.modelo.sgl.estancia.vo.DetalleEstanciaVO;
import sia.modelo.sgl.estancia.vo.HuespedVo;
import sia.modelo.sgl.estancia.vo.SgSolicitudEstanciaVo;
import sia.modelo.sgl.oficina.vo.SgOficinaAnalistaVo;
import sia.modelo.sgl.semaforo.vo.EstadoSemaforoCambioVO;
import sia.modelo.sgl.semaforo.vo.SgEstadoSemaforoVO;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.notificaciones.estilos.Estilos;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.comunicacion.impl.CoCompartidaImpl;
import sia.servicios.sgl.impl.SgDetalleSolicitudEstanciaImpl;
import sia.servicios.sgl.impl.SgOficinaAnalistaImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class NotificacionServiciosGeneralesImpl extends Estilos {

    @Inject
    private EnviarCorreoImpl enviarCorreoRemote;
    @Inject
    private HtmlNotificacionServiciosGeneralesImpl html;
    @Inject
    private SgOficinaAnalistaImpl sgOficinaAnalistaRemote;
    @Inject
    private SgOficinaImpl sgOficinaRemote;
    @Inject
    private SgDetalleSolicitudEstanciaImpl sgDetalleSolicitudEstanciaRemote;
    @Inject
    private SiParametroImpl siParametroRemote;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private CoCompartidaImpl coCompartidaRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private SgVehiculoImpl sgVehiculoRemote;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaRemote;
    @Inject
    private ApCampoGerenciaImpl apCampoGerenciaRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
//

    private UsuarioResponsableGerenciaVo getResponsableByGerencia(int idGerencia) {
        return this.gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.AP_CAMPO_DEFAULT, idGerencia);
    }

    /*
     * 14-enero-2014 Joel Rodriguez Se cambio el destinatario del mail de la
     * solicitud por el usuario que hospeda.
     *
     */
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean enviarCorreoSolicitaEstancia(int idBloque, SgSolicitudEstanciaVo sgSolicitudEstancia, List<DetalleEstanciaVO> listaDet) {
        boolean v;
        StringBuilder correos = new StringBuilder();
        UsuarioResponsableGerenciaVo user = getResponsableByGerencia(Constantes.GERENCIA_ID_SGL);
        String gerenteSGL = (user != null ? "," + user.getEmailUsuario() : "");
        correos.append(traerResponsableSGL(Constantes.AP_CAMPO_DEFAULT))
                .append(gerenteSGL);
        String oficina = "";
        if (sgSolicitudEstancia.getNombreSgOficina() != null) {
            oficina = sgSolicitudEstancia.getNombreSgOficina();
        }

        List<String> para = siUsuarioRolRemote.traerCorreosByRolAndOficina(Constantes.COD_ROL_ADMINISTRA_ESTANCIA, sgSolicitudEstancia.getIdSgOfina(), Constantes.AP_CAMPO_DEFAULT);
        Joiner j = Joiner.on(",").skipNulls();
        String correoEstancias = j.join(para);
        //List<DetalleEstanciaVO> listaDet = sgDetalleSolicitudEstanciaRemote.traerDetallePorSolicitud(sgSolicitudEstancia.getId(), false);
        //verifica si es el usuario es gerente 
        String c = "";
        String cc = "";
        UsuarioResponsableGerenciaVo u = gerenciaRemote.traerResponsablePorApCampoYGerencia(idBloque, sgSolicitudEstancia.getIdGerencia());
        if (u.getId().equals(sgSolicitudEstancia.getIdUsuario())) {
            c = u.getEmailUsuario();
        } else {
            c = u.getEmailUsuario();
            cc = sgSolicitudEstancia.getCorreoGenero();
        }
        // j.join(para);
        v = enviarCorreoRemote.enviarCorreoIhsa(j.join(para),
                correos.toString(), "",
                "Solicitud de Estancia " + sgSolicitudEstancia.getCodigo(),
                html.getHtmlSolicitaEstancia(sgSolicitudEstancia.getIdGerencia(), sgSolicitudEstancia.getCodigo(), sgSolicitudEstancia.getInicioEstancia(),
                        sgSolicitudEstancia.getFinEstancia(), sgSolicitudEstancia.getDiasEstancia(), sgSolicitudEstancia.getNombreSgMotivo(), sgSolicitudEstancia.getNombreGerencia(), listaDet, oficina, sgSolicitudEstancia.getObservacion()), siParametroRemote.find(1).getLogo());
        if (v) {
            //Correo para el gerente
            v = enviarCorreoRemote.enviarCorreoIhsa(c, cc, "",
                    "Solicitud de Estancia " + sgSolicitudEstancia.getCodigo(),
                    html.getHtmlSolicitaEstanciaParaGerencia(sgSolicitudEstancia, listaDet, oficina), siParametroRemote.find(1).getLogo());
        }

        return v;
    }

    /*
     * 14-enero-2014 Joel Rodriguez Se cambio el destinatario del mail de la
     * solicitud por el usuario que hospeda.
     *
     */
    
    public boolean enviarCorreoAprobarEstancia(SgSolicitudEstanciaVo sgSolicitudEstancia, List<DetalleEstanciaVO> detalle) {
        boolean v;
        //List<DetalleEstanciaVO> listaDet = sgDetalleSolicitudEstanciaRemote.traerDetallePorSolicitud(sgSolicitudEstancia.getId(), false);
        UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo = apCampoGerenciaRemote.buscarResponsablePorGerencia(sgSolicitudEstancia.getIdGerencia(), Constantes.AP_CAMPO_DEFAULT);
        v = enviarCorreoRemote.enviarCorreoIhsa(usuarioResponsableGerenciaVo.getEmailUsuario(),
                sgSolicitudEstancia.getCorreoGenero(),
                "",
                "Aprobar estancia " + sgSolicitudEstancia.getCodigo(),
                html.getHtmlAprobarSolicitaEstancia(sgSolicitudEstancia, detalle, usuarioResponsableGerenciaVo.getIdUsuario()), siParametroRemote.find(1).getLogo());
        return v;
    }

    
    public boolean enviarCorreoAvisoVencimientoContratoOficina(SgOficina oficina, Convenio convenio, Usuario analista, int numeroDias) {
        String correos = traerResponsableSGL(Constantes.AP_CAMPO_DEFAULT);

        correos += "," + analista.getEmail();
        log("Correo Cane sol" + correos);

        return enviarCorreoRemote.enviarCorreoIhsa(correos, "", "", "Aviso de vencimiento de Convenio de Oficina",
                html.getHtmlAvisoVencimientoConvenioOficina(oficina, convenio, analista.getNombre(), numeroDias), siParametroRemote.find(1).getLogo());

    }

    
    public boolean enviarCorreoAvisoVencimientoContratoStaff(SgStaff staff, Convenio convenio, Usuario analista, int numeroDias) {
        String correos = traerResponsableSGL(Constantes.AP_CAMPO_DEFAULT);
        //Trae los los usuarios
        correos += "," + analista.getEmail();
        log("Correo Cane sol" + correos);
        return enviarCorreoRemote.enviarCorreoIhsa(correos, "", "", "Aviso de vencimiento de Convenio de Staff",
                html.getHtmlAvisoVencimientoConvenioStaff(staff, convenio, analista.getNombre(), numeroDias), siParametroRemote.find(1).getLogo());

    }

    
    public boolean enviarCorreoRegistroHuespedHotel(SgHuespedHotel sgHuespedHotel, String estancia, int idDetalleEstancia, int idInvitado, String invitado, String empleado, String tipoEspecifico, String correoEmpleado,
            int idSolEst, int idHotel, int idTipoEspecifico) {
        String correos = traerResponsableSGLySeguridad();
        //Trae los los usuarios invol
        //Verifica si el registro es para usuario
        if (idInvitado == 0) {
            correos += "," + correoEmpleado;
        }
        log("Correo Cane sol" + correos);
        //Seguridad, gerencia solocita, sia
        SgSolicitudEstanciaVo sgSolicitudEstancia = sgSolicitudEstanciaRemote.buscarEstanciaPorId(idSolEst);
        return enviarCorreoRemote.enviarCorreoIhsa(sgSolicitudEstancia.getCorreoGenero(), correos, "", "Registro de Huésped en Hotel " + sgSolicitudEstancia.getCodigo(),
                html.getHtmlRegistroHuespedHotel(sgHuespedHotel, estancia, idDetalleEstancia,
                        idInvitado, invitado, empleado, tipoEspecifico, correoEmpleado, sgSolicitudEstancia, idHotel, idTipoEspecifico), siParametroRemote.find(1).getLogo());

    }

    
    public boolean enviarCorreoRegistroHuespedStaff(int idInvitado, String invitado, String empleado, String tipoEspecifico, String correoEmpleado, SgSolicitudEstanciaVo solicitudEstancia, SgStaffHabitacion habitacion, SgTipoEspecifico tipoHuesped, Date fechaIngreso, Date fechaSalida) {
        log("enviarCorreoRegistroHuespedStaff");
        String correos = traerResponsableSGLySeguridad();
        //Verifica si el registro es para usuario
        log("Linea dentro del envio de correo " + correoEmpleado);
        if (idInvitado == 0) {
            correos += "," + correoEmpleado;
        }
        log("Correo Cane sol" + correos);
        //Seguridad, gerencia solicita, sia
        return enviarCorreoRemote.enviarCorreoIhsa(solicitudEstancia.getCorreoGenero(), correos, "", "Registro de Huésped en Staff House " + solicitudEstancia.getCodigo(),
                html.getHtmlRegistroHuespedStaff(idInvitado, invitado, empleado, tipoEspecifico, solicitudEstancia, habitacion, tipoHuesped, fechaIngreso, fechaSalida), siParametroRemote.find(1).getLogo());

    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean enviaCorreoCancelaSolicitudEstancia(Usuario usuario, SgSolicitudEstanciaVo sgSolicitudEstancia, String mensaje, boolean notificar) {
        //Gerente que generó la Solicitud de Estancia
        String para = getResponsableByGerencia(sgSolicitudEstancia.getIdGerencia()).getEmailUsuario();
        String paraIntegrantes = traerMailDetalleSolicitusEstancia(sgSolicitudEstancia.getId());
        if (!para.trim().isEmpty() && !paraIntegrantes.trim().isEmpty()) {
            para += ",";
        }
        para += paraIntegrantes;
        //Responsable de SGL, Analista que canceló la Solicitud de Estancia
        String cc = traerResponsableSGL(Constantes.AP_CAMPO_DEFAULT) + "," + usuario.getEmail();
        log("para al cancelar solicitud estancia: " + para);

        //Seguridad, gerencia solicita, sia
        return enviarCorreoRemote.enviarCorreoIhsa(para,
                cc,
                "",
                "Solicitud de Estancia " + sgSolicitudEstancia.getCodigo() + " - Cancelada",
                html.getHtmlCancelaSolicitudEstancia(sgSolicitudEstancia, mensaje, notificar), siParametroRemote.find(1).getLogo());

    }

    
    public boolean enviaCorreoSalidaHusped(Usuario usuario, Object object) {
        boolean v = false;
        String correos = traerResponsableSGLySeguridad();
        log("Correos SGL y SEG: " + correos);
        if (object instanceof SgHuespedHotel) {
            SgHuespedHotel sgHuespedHotel = (SgHuespedHotel) object;
            log("Numero de habitación : " + sgHuespedHotel.getNumeroHabitacion());
            //Agrega el usuario solicita
            correos += "," + sgHuespedHotel.getGenero().getEmail() + "," + getResponsableByGerencia(sgHuespedHotel.getSgSolicitudEstancia().getGerencia().getId()).getEmailUsuario();

            log("Gerencia: " + correos);
            if (sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario() != null) {
                correos += "," + sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario().getEmail();
            }
            log("Correos : " + correos);
            if (usuario.getId().equals("PRUEBA")) {
                log("Enviar correo a usuario de prueba ");
                v = enviarCorreoRemote.enviarCorreoIhsa(traerCorreoSIAoPRUEBA("PRUEBA"), "", "", "Estancia terminada " + " " + sgHuespedHotel.getSgDetalleSolicitudEstancia().getSgSolicitudEstancia().getCodigo(),
                        html.getHtmlSalidaHuesped(sgHuespedHotel.getSgDetalleSolicitudEstancia(), sgHuespedHotel), siParametroRemote.find(1).getLogo());
            } else {
                //Seguridad, gerencia solocita, sia
                v = enviarCorreoRemote.enviarCorreoIhsa(correos, "", "", "Estancia terminada " + " " + sgHuespedHotel.getSgDetalleSolicitudEstancia().getSgSolicitudEstancia().getCodigo(),
                        html.getHtmlSalidaHuesped(sgHuespedHotel.getSgDetalleSolicitudEstancia(), sgHuespedHotel), siParametroRemote.find(1).getLogo());
            }
        }
        if (object instanceof SgHuespedStaff) {
            SgHuespedStaff sgHuespedStaff = (SgHuespedStaff) object;
            //Agrega el usuario solicita
            correos += "," + sgHuespedStaff.getGenero().getEmail() + "," + getResponsableByGerencia(sgHuespedStaff.getSgSolicitudEstancia().getGerencia().getId()).getEmailUsuario();
            if (sgHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario() != null) {
                correos += "," + sgHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getEmail();
            }
            log("Correos : " + correos);
            if (usuario.getId().equals("PRUEBA")) {
                v = enviarCorreoRemote.enviarCorreoIhsa(traerCorreoSIAoPRUEBA("PRUEBA"), "", "", "Estancia terminada " + " " + sgHuespedStaff.getSgDetalleSolicitudEstancia().getSgSolicitudEstancia().getCodigo(),
                        html.getHtmlSalidaHuesped(sgHuespedStaff.getSgDetalleSolicitudEstancia(), sgHuespedStaff), siParametroRemote.find(1).getLogo());
            } else {
                //Seguridad, gerencia solocita, sia
                v = enviarCorreoRemote.enviarCorreoIhsa(correos, "", "", "Estancia terminada " + " " + sgHuespedStaff.getSgDetalleSolicitudEstancia().getSgSolicitudEstancia().getCodigo(),
                        html.getHtmlSalidaHuesped(sgHuespedStaff.getSgDetalleSolicitudEstancia(), sgHuespedStaff), siParametroRemote.find(1).getLogo());
            }
        }
        return v;
    }

    
    public boolean enviaCorreoCancelaRegistroHuesped(Usuario usuario, Object object) {
        boolean v = false;
        String correos = traerResponsableSGLySeguridad();
        if (object instanceof SgHuespedHotel) { //Huésped en Hotel
            SgHuespedHotel sgHuespedHotel = (SgHuespedHotel) object;
            //Agrega el usuario solicita

            correos += "," + sgHuespedHotel.getGenero().getEmail() + "," + getResponsableByGerencia(sgHuespedHotel.getSgSolicitudEstancia().getGerencia().getId()).getEmailUsuario();
            if (sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario() != null) {
                correos += "," + sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario().getEmail();
            }
            log("Copia Correos : " + correos);
            if (usuario.getId().equals("PRUEBA")) {
                v = enviarCorreoRemote.enviarCorreoIhsa(traerCorreoSIAoPRUEBA("PRUEBA"), "", "", "Estancia -" + " " + sgHuespedHotel.getSgDetalleSolicitudEstancia().getSgSolicitudEstancia().getCodigo() + " Cancelada",
                        html.getHtmlCancelaHuesped(sgHuespedHotel.getSgDetalleSolicitudEstancia(), sgHuespedHotel), siParametroRemote.find(1).getLogo());
            } else {
                //Seguridad, gerencia solicita, sia
                v = enviarCorreoRemote.enviarCorreoIhsa(sgHuespedHotel.getSgSolicitudEstancia().getGenero().getEmail(), correos, "", "Estancia -" + " " + sgHuespedHotel.getSgDetalleSolicitudEstancia().getSgSolicitudEstancia().getCodigo() + "- Cancelada",
                        html.getHtmlCancelaHuesped(sgHuespedHotel.getSgDetalleSolicitudEstancia(), sgHuespedHotel), siParametroRemote.find(1).getLogo());
            }

        } else if (object instanceof SgHuespedStaff) { //Huésped en Staff
            SgHuespedStaff sgHuespedStaff = (SgHuespedStaff) object;
            //Agrega el usuario solicita
            if (sgHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario() != null) {
                correos += "," + sgHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getEmail();
            }
            correos += "," + sgHuespedStaff.getGenero().getEmail() + "," + getResponsableByGerencia(sgHuespedStaff.getSgSolicitudEstancia().getGerencia().getId()).getEmailUsuario();
            log("Copia Correos : " + correos);
            if (usuario.getId().equals("PRUEBA")) {
                v = enviarCorreoRemote.enviarCorreoIhsa(traerCorreoSIAoPRUEBA("PRUEBA"), "", "", "Estancia -" + " " + sgHuespedStaff.getSgDetalleSolicitudEstancia().getSgSolicitudEstancia().getCodigo() + "- Cancelada",
                        html.getHtmlCancelaHuesped(sgHuespedStaff.getSgDetalleSolicitudEstancia(), sgHuespedStaff), siParametroRemote.find(1).getLogo());
            } else {
                //Seguridad, gerencia solocita, sia
                v = enviarCorreoRemote.enviarCorreoIhsa(sgHuespedStaff.getSgSolicitudEstancia().getGenero().getEmail(), correos, "", "Estancia -" + " " + sgHuespedStaff.getSgDetalleSolicitudEstancia().getSgSolicitudEstancia().getCodigo() + "- Cancelada",
                        html.getHtmlCancelaHuesped(sgHuespedStaff.getSgDetalleSolicitudEstancia(), sgHuespedStaff), siParametroRemote.find(1).getLogo());
            }
        }

        return v;
    }

    /**
     * Modifico: NLopez 07/11/2013 Traer correos de responsable y seguridad
     *
     * @param li
     */
    
    public void enviaCorreoLiciencia(List<SgLicencia> li) {
        StringBuilder correoPara = new StringBuilder();
        List<UsuarioVO> lu = usuarioRemote.getUsuariosByRol(Constantes.SGL_RESPONSABLE);
        int nlist = lu.size();
        int x = 1;

        for (UsuarioVO usuario1 : lu) {
            correoPara.append(usuario1.getMail());
            if (x < nlist) {
                correoPara.append(", ");
            }
            x++;
        }
        log("Correos: " + correoPara.toString());
        enviarCorreoRemote.enviarCorreoIhsa(correoPara.toString(), "", "", "Vencimiento de licencia", cuerpoCorreo, siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviarCorreoAvisoNotificacionPagoStaff(SgOficina oficina) {
        //
        String correoAnalista = "";
        String sia = "";
        List<SgOficinaAnalista> lOfi = sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(oficina.getId(), Constantes.BOOLEAN_FALSE);
        for (SgOficinaAnalista sgOficinaAnalista : lOfi) {
            if (sgOficinaAnalista.getAnalista().getId().equals("PRUEBA")) {
                correoAnalista = sgOficinaAnalista.getAnalista().getEmail();
            } else {
                if (correoAnalista != null && correoAnalista.isEmpty()) {
                    sia = usuarioRemote.find("SIA").getEmail();
                    correoAnalista = sgOficinaAnalista.getAnalista().getEmail();
                } else {
                    correoAnalista += "," + sgOficinaAnalista.getAnalista().getEmail();
                }
            }
        }
        //Falta poner la lista de correosResponsablesSGL destinatarios
//        return enviarCorreoRemote.envia|rCorreoIhsa("mluis@ihsa.mx,jorodriguez@ihsa.mx", "", "", "Recordatorio de pago(s) para Staff House's",
//                html.getHtmlEnvioAvisoPagoStaff(oficina), siParametroRemote.find(1).getLogo());

        return enviarCorreoRemote.enviarCorreoIhsa(correoAnalista, "", sia, "Recordatorio de pago(s) para Staff House's",
                html.getHtmlEnvioAvisoPagoStaff(oficina), siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviarCorreoAvisoNotificacionPagoOficina(SgOficina oficina, List<SgAvisoPago> listaAvisosPagos) {
        //
        String correoAnalista = "";
        String sia = "";
        List<SgOficinaAnalista> lOfi = sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(oficina.getId(), Constantes.BOOLEAN_FALSE);

        for (SgOficinaAnalista sgOficinaAnalista : lOfi) {
            if (sgOficinaAnalista.getAnalista().getId().equals("PRUEBA")) {
                correoAnalista = sgOficinaAnalista.getAnalista().getEmail();
            } else {
                sia = usuarioRemote.find("SIA").getEmail();
                if (correoAnalista != null && correoAnalista.isEmpty()) {
                    correoAnalista = sgOficinaAnalista.getAnalista().getEmail();
                } else {
                    correoAnalista += "," + sgOficinaAnalista.getAnalista().getEmail();
                }
            }
        }
//        return enviarCorreoRemote.enviarCorreoIhsa("mluis@ihsa.mx,jorodriguez@ihsa.mx", "", sia, "Recordatorio de pago(s) para oficina(s)",
//                html.getHtmlEnvioAvisoPagoOficina(oficina, listaAvisosPagos), siParametroRemote.find(1).getLogo());

        return enviarCorreoRemote.enviarCorreoIhsa(correoAnalista, "", sia, "Recordatorio de pago(s) para oficina(s)",
                html.getHtmlEnvioAvisoPagoOficina(oficina, listaAvisosPagos), siParametroRemote.find(1).getLogo());
    }

    /*
     * Privados
     */
    private String traerCorreoSIAoPRUEBA(String nombre) {
        try {
            return usuarioRemote.find(nombre).getDestinatarios();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Modifico: NLopez 07/11/2013 Traer correos de responsable y seguridad
     *
     * @return
     */
    private String traerResponsableSGLySeguridad() {
        String correoPara = "";
        List<Integer> li = new ArrayList<Integer>();
        li.add(Constantes.SGL_RESPONSABLE);
        li.add(Constantes.SGL_SEGURIDAD);
        li.add(Constantes.ROL_CENTRO_OPERACION);
        List<UsuarioRolVo> lu = siUsuarioRolRemote.traerUsuarioByRol(li, Constantes.AP_CAMPO_NEJO);
        if (lu != null) {
            for (UsuarioRolVo usuario1 : lu) {
                if (correoPara.isEmpty()) {
                    correoPara = usuario1.getCorreo();
                } else {
                    correoPara += "," + usuario1.getCorreo();
                }
            }
        } else {
            correoPara = "sia@ihsa.mx";
        }

        return correoPara;

    }

    /**
     * Modifico: NLopez 07/11/2013 Traer correos de responsable y seguridad
     *
     * @return
     */
    //Traer responsable por bloque
    private String traerResponsableSGL(int apCampo) {
        StringBuilder correoPara = new StringBuilder();

        List<UsuarioVO> lu = usuarioRemote.getUsuariosPorRolBloque(Constantes.SGL_RESPONSABLE, Constantes.AP_CAMPO_DEFAULT);
        int nlist = lu.size();
        int x = 1;

        for (UsuarioVO usuario1 : lu) {
            correoPara.append(usuario1.getMail());
            if (x < nlist) {
                correoPara.append(", ");
            }
            x++;
        }

        return correoPara.toString();

    }

    /**
     * Modifico: NLopez 07/11/2013 Traer correos de responsable y seguridad
     *
     * @return
     */
    private String traerUsuarioAdministradoresSGL() {
        StringBuilder correoPara = new StringBuilder();

        List<UsuarioVO> lu = usuarioRemote.getUsuariosByRol(Constantes.SGL_ADMINISTRA);
        int nlist = lu.size();
        int x = 1;

        for (UsuarioVO usuario1 : lu) {
            correoPara.append(usuario1.getMail());
            if (x < nlist) {
                correoPara.append(", ");
            }
            x++;
        }
        return correoPara.toString();

    }

    private String traerCorreoAnalistaOficina(int sgOficina) {
        log("traerCorreoAnalistaOficina");
        String correoAnalista = "";
        List<SgOficinaAnalista> lOfi = sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(sgOficina, Constantes.BOOLEAN_FALSE);
        for (SgOficinaAnalista sgOficinaAnalista : lOfi) {
            if (correoAnalista != null && correoAnalista.isEmpty()) {
                correoAnalista = sgOficinaAnalista.getAnalista().getEmail();
            } else {
                correoAnalista += "," + sgOficinaAnalista.getAnalista().getEmail();
            }
        }
        return correoAnalista;
    }

    
    public boolean enviaCorreoAvisoSalidaHuespedStaffPorOficina(SgOficina oficina, Date fechaVencimientoEn15Dias) {
        log("enviaCorreoAvisoSalidaHuespedStaffPorOficina");
        log("Oficina " + oficina.getNombre());
        String correoAnalista = "";
        String sia = "";
        String correos = traerResponsableSGL(Constantes.AP_CAMPO_DEFAULT);
        //Responsables de SGyL
//        for (Usuario usuario : usuarioRemote.traerUsuarioResponsableSGL()) {
//            if (correos.isEmpty()) {
//                correos = usuario.getEmail();
//            } else {
//                correos += "," + usuario.getEmail();
//            }
//        }
        //traer analistas por oficinas
        List<SgOficinaAnalista> lOfi = sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(oficina.getId(), Constantes.BOOLEAN_FALSE);
        for (SgOficinaAnalista sgOficinaAnalista : lOfi) {
            if (correoAnalista != null && correoAnalista.isEmpty()) {
                correoAnalista = sgOficinaAnalista.getAnalista().getEmail();
            } else {
                correoAnalista += "," + sgOficinaAnalista.getAnalista().getEmail();
            }
        }
        return enviarCorreoRemote.enviarCorreoIhsa(correos + "," + correoAnalista, "", "", "Vencimiento de estancia",
                html.getHtmlEnvioSalidaHuespedStaff(oficina, fechaVencimientoEn15Dias), siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviaCorreoCancelacionAntesDeRegistroHuesped(Usuario usuario, int idInvitado, String invitado, String empleado, String gerencia, Date inicio, Date fin, String codigo, String nombreGenero, String tipoDetalle, String correoGenero, int idGerencia) {
        boolean v = false;
        
        List<String> lEmail = new ArrayList<>();
        String correos = traerResponsableSGL(Constantes.AP_CAMPO_DEFAULT);
        lEmail.add(correos);
        lEmail.add(correoGenero);
        lEmail.add(usuario.getEmail());
        
        UsuarioResponsableGerenciaVo urgv = gerenciaRemote.traerResponsablePorApCampoYGerencia(1, idGerencia);

        Joiner joiner = Joiner.on(',').skipNulls();

        if (urgv != null) {
            lEmail.add(urgv.getEmailUsuario());
        }
        
        List<UsuarioVO> lu = usuarioRemote.getUsuariosPorRol(Constantes.ROL_ID_ADM_ESTANCIA);
        if(lu != null && !lu.isEmpty()){
            for (UsuarioVO u : lu) {
                            lEmail.add(u.getMail());
                        }  
        }
                              
        correos =  joiner.join(lEmail);
        log("Correo Cane sol" + correos);
        if (usuario.getId().equals("PRUEBA")) {
            return enviarCorreoRemote.enviarCorreoIhsa(traerCorreoSIAoPRUEBA("PRUEBA"), "", "", "Solicitud de estancia " + codigo + " - Cancelada",
                    html.getHtmlCancelacionAntesDeRegistroHuesped(idInvitado != 0 ? idInvitado : 0,
                            idInvitado != 0 ? invitado : "",
                            idInvitado == 0 ? empleado : "",
                            gerencia,
                            inicio, fin,
                            codigo, urgv.getNombreUsuario(),
                            tipoDetalle), siParametroRemote.find(1).getLogo());
        } else {
            //Seguridad, gerencia solocita, sia
            return enviarCorreoRemote.enviarCorreoIhsa(correos, "", "", "Solicitud de estancia " + codigo + " - Cancelada",
                    html.getHtmlCancelacionAntesDeRegistroHuesped(idInvitado != 0 ? idInvitado : 0,
                            idInvitado != 0 ? invitado : "",
                            idInvitado == 0 ? empleado : "",
                            gerencia,
                            inicio, fin,
                            codigo, urgv.getNombreUsuario(),
                            tipoDetalle), siParametroRemote.find(1).getLogo());
        }
    }

    
    public boolean enviarAvisoNotificacionVencimientoLicenciasPorOficina(int oficina, List<LicenciaVo> lista, Date fechaVencimiento, String nombreOficina) {
        log("enviarAvisoNotificacionVencimientoLicenciasPorOficina");
        //FALTA PONER A LOS RESPONSABLES
        String correoAnalista = "";
        String correos = traerResponsableSGL(Constantes.AP_CAMPO_DEFAULT);
        //traer analistas por oficinas
        List<SgOficinaAnalistaVo> lOfi = sgOficinaAnalistaRemote.getAllSgOficinaAnalista(oficina, "nombre", true, false);
        if (lOfi != null) {
            for (SgOficinaAnalistaVo sgOficinaAnalista : lOfi) {
                if (correoAnalista != null && correoAnalista.isEmpty()) {
                    log("*analista " + sgOficinaAnalista.getNombreAnalista());
                    correoAnalista = sgOficinaAnalista.getEmailAnalista();

                } else {
                    correoAnalista += "," + sgOficinaAnalista.getEmailAnalista();
                }
            }
        }
        return enviarCorreoRemote.enviarCorreoIhsa(correos + "," + correoAnalista,
                "",
                "",
                "Vencimiento de licencia",
                html.getHtmlAvisoVencimientoLicencia(lista, nombreOficina, fechaVencimiento), siParametroRemote.find(1).getLogo());

    }

    
    public boolean enviarAvisoNotificacionVencimientoPagosVehiculoPorOficina(SgOficina oficina, List<SgPagoServicioVehiculo> lista, Date fechaVencimiento, SgTipoEspecifico tipoEspecifico) {
        log("enviarAvisoNotificacionVencimientoPagosVehiculoPorOficina");
        String correoAnalista = "";
        String sia = "";
        String correos = traerResponsableSGL(Constantes.AP_CAMPO_DEFAULT);
        log(" buscar analistas..");
        //traer analistas por oficinas
        List<SgOficinaAnalista> lOfi = sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(oficina.getId(), Constantes.BOOLEAN_FALSE);
        if (lOfi != null) {
            for (SgOficinaAnalista sgOficinaAnalista : lOfi) {
                if (correoAnalista != null && correoAnalista.isEmpty()) {
                    log("Armando mails de destinos de analistas " + sgOficinaAnalista.getAnalista().getNombre());
                    log("Mail " + sgOficinaAnalista.getAnalista().getEmail());
                    correoAnalista = sgOficinaAnalista.getAnalista().getEmail();
                } else {
                    correoAnalista += "," + sgOficinaAnalista.getAnalista().getEmail();
                }
            }
        }
        return enviarCorreoRemote.enviarCorreoIhsa(correos + "," + correoAnalista, usuarioRemote.find("SIA").getEmail(), "", "Vencimiento de ".concat(tipoEspecifico.getNombre()).concat(" de ").concat(oficina.getNombre()),
                html.getHtmlAvisoVencimientoPagoServicioVehiculo(lista, oficina, fechaVencimiento, tipoEspecifico), siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviarAvisoNotificacionProxMantenimientoPorKm(int oficina, List<VehiculoVO> lista, String nombreOficina) {
        boolean v = false;
        log("enviarAvisoNotificacionProxMantenimientoPorKm");
        //traer analistas por oficinas
        List<SgOficinaAnalistaVo> lOfi = sgOficinaAnalistaRemote.getAllSgOficinaAnalista(oficina, "id", true, false);
        if (lOfi != null && lOfi.size() > 0) {
            StringBuilder correoAnalista = new StringBuilder();
            String correos = traerResponsableSGL(Constantes.AP_CAMPO_DEFAULT);
            for (SgOficinaAnalistaVo sgOficinaAnalista : lOfi) {
                if (correoAnalista.length() != 0) {
                    correoAnalista.append(',');
                }
                correoAnalista.append(sgOficinaAnalista.getEmailAnalista());
            }
            v = enviarCorreoRemote.enviarCorreoIhsa(correos + "," + correoAnalista, "", "",
                    "Próximo(s) mantenimiento(s) por kilometraje",
                    html.getHtmlAvisoProxMantenimientoPorKm(lista, nombreOficina),
                    siParametroRemote.find(1).getLogo());
        }
        return v;
    }

    
    public boolean enviarAvisoNotificacionProxMantenimientoPorFecha(int oficina, List<VehiculoVO> lista, Date fecha, String nombreOficina) {
        log("enviarAvisoNotificacionProxMantenimientoPorFecha");
        StringBuilder correoAnalista = new StringBuilder();
        String correos = traerResponsableSGL(Constantes.AP_CAMPO_DEFAULT);
        //traer analistas por oficinas
        List<SgOficinaAnalistaVo> lOfi = sgOficinaAnalistaRemote.getAllSgOficinaAnalista(oficina, "id", true, false);
        if (lOfi != null) {
            for (SgOficinaAnalistaVo sgOficinaAnalista : lOfi) {
                if (correoAnalista.length() > 0) {
                    correoAnalista.append(',');
                }
                correoAnalista.append(sgOficinaAnalista.getEmailAnalista());
            }
            log("correos " + correos + correoAnalista);

            return enviarCorreoRemote.enviarCorreoIhsa(correos + "," + correoAnalista, "", "",
                    "Próximo(s) mantenimiento(s) por fecha",
                    html.getHtmlAvisoProxMantenimientoPorFecha(lista, nombreOficina, fecha), siParametroRemote.find(1).getLogo()
            );
        }
        return true;
    }

    private String traerMailDetalleSolicitusEstancia(int sgSolicitudEstancia) {
        log("idSol: " + sgSolicitudEstancia);
        StringBuilder correo = new StringBuilder();
        List<DetalleEstanciaVO> ld = sgDetalleSolicitudEstanciaRemote.traerDetallePorSolicitud(sgSolicitudEstancia, Constantes.NO_ELIMINADO);
        if (ld != null) {
            for (DetalleEstanciaVO sgDetalleSolicitudEstancia : ld) {
                if (sgDetalleSolicitudEstancia.getIdInvitado() == 0) {
                    if (correo.length() > 0) {
                        correo.append(',');
                    }

                    correo.append(sgDetalleSolicitudEstancia.getCorreoUsuario());
                }
            }
        }

        return correo.toString();
    }

    
    public boolean enviarComentarioNoticia(CoNoticia coNoticia, CoComentario comentario, boolean isRecomendacionSeguridad, int campo, int modulo) {

        StringBuilder cco = new StringBuilder();
        StringBuilder correo = new StringBuilder();

        log("NotificacionServiciosGeneresImpl.enviarComentarioNoticia()");
//
        List<UsuarioRolVo> lur = siUsuarioRolRemote.traerRolPorCodigo(Constantes.CODIGO_ROL_NOTI_NOT, campo, modulo);
        //
        List<ComparteCon> lc = this.coCompartidaRemote.getListaUsuarioCompartidos(coNoticia.getId());

        for (ComparteCon comparteCon : lc) {
            if (comparteCon != null && !comparteCon.getCorreoUsuario().equals("")) {
                if (correo.length() > 0) {
                    correo.append(",");
                }
                correo.append(comparteCon.getCorreoUsuario());
            }
        }

        if (lur != null && !lur.isEmpty()) {
            for (UsuarioRolVo urVo : lur) {
                if (cco.length() > 0) {
                    cco.append(",");
                }
                cco.append(urVo.getCorreo());
            }
        }

        return enviarCorreoRemote.enviarCorreoIhsa(
                correo.toString(),
                comentario.getGenero().getEmail(),
                cco.toString(),
                (isRecomendacionSeguridad ? "Seguridad ha comentado" : comentario.getGenero().getNombre() + " ha comentado"),
                html.getHtmlComentarioNoticia(coNoticia, comentario, isRecomendacionSeguridad),
                siParametroRemote.find(1).getLogo()
        );
    }

    
    public boolean enviarAvisoNotificacionProxMantenimientoPorPeriodicidad(Integer idOficina, List<VehiculoVO> lista) {
        log("enviarAvisoNotificacionProxMantenimientoPorPeriodicidad");
        StringBuilder correoAnalista = new StringBuilder();
        String correos = traerResponsableSGL(Constantes.AP_CAMPO_DEFAULT);

        SgOficina oficina = this.sgOficinaRemote.find(idOficina);

        //traer analistas por oficinas
        List<SgOficinaAnalista> lOfi = sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(oficina.getId(), Constantes.BOOLEAN_FALSE);

        if (lOfi != null) {
            for (SgOficinaAnalista sgOficinaAnalista : lOfi) {
                if (correoAnalista.length() > 0) {
                    correoAnalista.append(',');
                }

                correoAnalista.append(sgOficinaAnalista.getAnalista().getEmail());
            }
        }

        return enviarCorreoRemote.enviarCorreoIhsa(
                correos + ',' + correoAnalista, "",
                "",
                "Próximo(s) mantenimiento(s) preventivos por kilometraje",
                html.getHtmlAvisoProxMantenimientoPorPeriodicidad(oficina.getNombre(), lista),
                siParametroRemote.find(1).getLogo()
        );
    }

    
    public boolean enviarNofiticacionReinicioModificacionKilometraje(int vehiculo, int oficina, int kmActual, int kmNuevo, String motivo, String idUsuario, boolean isModificacion) {
        StringBuilder correoAnalista = new StringBuilder();
        String correos = "";
        UsuarioVO vo = usuarioRemote.findById(idUsuario);

        correoAnalista.append(traerResponsableSGL(Constantes.AP_CAMPO_DEFAULT));

        if (isModificacion) {
            //solo notificar a analistas de la ofna
            correos = vo.getMail();
            correoAnalista.append(this.traerCorreoAnalistaOficina(oficina));

        } else {
            List<SgOficinaAnalista> lOfi
                    = sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(oficina, Constantes.BOOLEAN_FALSE);

            if (lOfi != null) {
                for (SgOficinaAnalista sgOficinaAnalista : lOfi) {
                    if (correoAnalista.length() > 0) {
                        correoAnalista.append(',');
                    }

                    correoAnalista.append(sgOficinaAnalista.getAnalista().getEmail());
                }
            }
        }

        String administradores = traerUsuarioAdministradoresSGL();
        if (!administradores.trim().isEmpty()) {
            correoAnalista.append(',').append(administradores);
        }

        return enviarCorreoRemote.enviarCorreoIhsa(
                correoAnalista.toString(),
                correos, "",
                (isModificacion ? "Modificación de kilometraje" : "Reinicio de kilometraje"),
                html.getHtmlNotificacionReinicioModificacionKilometraje(sgVehiculoRemote.find(vehiculo), kmActual, kmNuevo, motivo, vo.getNombre(), isModificacion),
                siParametroRemote.find(1).getLogo()
        );
    }

//    
//    public boolean sendNotificacionForTeamSIA(String notificacion, String asunto) {
//        return this.enviarCorreoRemote.enviarCorreoGmail("guepardo190889@gmail.com",
//                "",
//                "",
//                asunto, this.html.getHtmlNotificacionForTeamSIA(notificacion, asunto));
//
//    }
//--------------------------------------SEMAFORO --------------------------------------------
    
    public boolean enviarCorreoCambioEstadoSemaforoDireccion(String cc, String ccp, String cco, List<EstadoSemaforoCambioVO> lista, int idSemaforo, String justificacion) {
        return this.enviarCorreoRemote.enviarCorreoIhsa(cc, ccp, cco, "Notificación de seguridad -- ".concat(Constantes.FMT_ddMMyyy.format(new Date())),
                this.html.getHtmlNotificacionSeguridadDireccion(lista, justificacion,
                        "Notificación de seguridad -- ".concat(Constantes.FMT_ddMMyyy.format(new Date()))), siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviarCorreoCambioEstadoSemaforoDireccion(String cc, String ccp, String cco, SgEstadoSemaforoVO semaforoVO) {
        return this.enviarCorreoRemote.enviarCorreoIhsa(cc, ccp, cco, "Notificación de seguridad -- ".concat(Constantes.FMT_ddMMyyy.format(new Date())),
                this.html.getHtmlNotificacionSeguridadDireccion(semaforoVO, "Notificación de seguridad -- ".concat(Constantes.FMT_ddMMyyy.format(new Date()))), siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviarCorreoCambioEstadoSemaforoViajeros(RutaTerrestreVo rutaVO, ViajeVO viajeVO, SgEstadoSemaforoVO semaforo) {
        String cc = viajeVO.getViajerosEmails();
        String ccp = "";
        String cco = "";
        StringBuilder asunto = new StringBuilder();
        asunto.append("Viaje: ").append(viajeVO.getCodigo()).append(" Notificación de seguridad -- ").append(Constantes.FMT_ddMMyyy.format(new Date()));
        return this.enviarCorreoRemote.enviarCorreoIhsa(cc, ccp, cco, asunto.toString(),
                this.html.getHtmlNotificacionSeguridadViajeros(rutaVO, viajeVO, semaforo, asunto.toString()), siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviarCorreoCambioEstadoSemaforoTodoIhsa(String cc, String ccp, String cco, List<EstadoSemaforoCambioVO> lista, int idSemaforo) {
        return this.enviarCorreoRemote.enviarCorreoIhsa(cc, ccp, cco, "Notificación de seguridad -- ".concat(Constantes.FMT_ddMMyyy.format(new Date())),
                this.html.getHtmlNotificacionSeguridadTodoIhsa(lista, "Notificación de seguridad -- ".concat(Constantes.FMT_ddMMyyy.format(new Date()))), siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviarCorreoCambioEstadoSemaforoTodoIhsa(String cc, String ccp, String cco, SgEstadoSemaforoVO semaforoVO) {
        return this.enviarCorreoRemote.enviarCorreoIhsa(cc, ccp, cco, "Notificación de seguridad -- ".concat(Constantes.FMT_ddMMyyy.format(new Date())),
                this.html.getHtmlNotificacionSeguridadTodoIhsa(semaforoVO, "Notificación de seguridad -- ".concat(Constantes.FMT_ddMMyyy.format(new Date()))), siParametroRemote.find(1).getLogo());
    }

//    
//    public boolean enviarNotificacionEstanciaProlongadaParaSgHuespedStaffPorSemaforoNegro(String nombreSgHuespedStaff, String emailSgHuespedStaff, String nombreSgStaff, String numeroSgStaff, String nombreHabitacion, String numeroHabitacion, Date nuevaFechaSalida, String analistaContacto, String idUsuarioCambiaSemaforo) throws EmailNotFoundException {
//
//        boolean sendEmail = (emailSgHuespedStaff != null && !emailSgHuespedStaff.isEmpty());
//        boolean isPrueba = Constantes.USUARIO_PRUEBA.equals(idUsuarioCambiaSemaforo);
//
//        if (sendEmail) {
//            return this.enviarCorreoRemote.enviarCorreoIhsa((isPrueba ? this.usuarioRemote.find(Constantes.USUARIO_PRUEBA).getEmail() : emailSgHuespedStaff),
//                    "",
//                    this.usuarioRemote.find(Constantes.USUARIO_SIA).getEmail(),
//                    Constantes.ASUNTO_NOTIFICACION_ESTANCIA_PROLONGADA,
//                    this.html.getHtmlNotificacionEstanciaProlongadaPorSemaforoNegroParaHuespedStaff(Constantes.ASUNTO_NOTIFICACION_ESTANCIA_PROLONGADA,
//                    nombreSgHuespedStaff,
//                    nombreSgStaff,
//                    numeroSgStaff,
//                    nombreHabitacion,
//                    numeroHabitacion,
//                    nuevaFechaSalida,
//                    analistaContacto),
//                    siParametroRemote.find(1).getLogo());
//        } else {
//            throw new EmailNotFoundException(nombreSgHuespedStaff);
//        }
//    }
//    
//    public boolean sendNotificacionEstanciaProlongadaParaSgHuespedHotelPorSemaforoNegro(String nombreSgHuespedHotel, String emailSgHuespedHotel, String nombreHotel, String numeroReservacion, Date nuevaFechaSalida, String analistaContacto, String idUsuarioCambiaSemaforo) throws EmailNotFoundException {
//
//        boolean sendEmail = (emailSgHuespedHotel != null && !emailSgHuespedHotel.isEmpty());
//        boolean isPrueba = Constantes.USUARIO_PRUEBA.equals(idUsuarioCambiaSemaforo);
//
//        if (sendEmail) {
//            return this.enviarCorreoRemote.enviarCorreoIhsa((isPrueba ? this.usuarioRemote.find(Constantes.USUARIO_PRUEBA).getEmail() : emailSgHuespedHotel),
//                    "",
//                    this.usuarioRemote.find(Constantes.USUARIO_SIA).getEmail(),
//                    Constantes.ASUNTO_NOTIFICACION_ESTANCIA_PROLONGADA,
//                    this.html.getHtmlNotificacionEstanciaProlongada(Constantes.ASUNTO_NOTIFICACION_ESTANCIA_PROLONGADA,
//                    nombreSgHuespedHotel,
//                    nombreHotel,
//                    numeroReservacion,
//                    nuevaFechaSalida,
//                    analistaContacto),
//                    siParametroRemote.find(1).getLogo());
//        } else {
//            throw new EmailNotFoundException(nombreSgHuespedHotel);
//        }
//    }
//    
//    public boolean sendNotificacionEstanciaProlongadaParaHuespedForAnalistaBySgOficina(String nombreRuta, int idSgOficina, List<String> huespedes, String idUsuarioCambiaSemaforo) throws EmailNotFoundException {
//
//        boolean sendEmail = true;
//        boolean isPrueba = Constantes.USUARIO_PRUEBA.equals(idUsuarioCambiaSemaforo);
//        String para = "";
//        List<String> usuariosWithoutEmail = new ArrayList<String>();
//        List<SgOficinaAnalistaVo> analistasBySgOficina = this.sgOficinaAnalistaRemote.getAllSgOficinaAnalista(idSgOficina, "nombre", true, false);
//
//        if (analistasBySgOficina != null && !analistasBySgOficina.isEmpty()) {
//            for (SgOficinaAnalistaVo vo : analistasBySgOficina) {
//                if (vo.getEmailAnalista() != null && !vo.getEmailAnalista().isEmpty()) {
//                    if (para.isEmpty()) {
//                        para = vo.getEmailAnalista();
//                    } else {
//                        para += ("," + vo.getEmailAnalista());
//                    }
//                } else {
//                    sendEmail = false;
//                    usuariosWithoutEmail.add(vo.getNombreAnalista());
//                }
//            }
//        }
//
//        if (sendEmail) {
//            return this.enviarCorreoRemote.enviarCorreoIhsa((isPrueba ? this.usuarioRemote.find(Constantes.USUARIO_PRUEBA).getEmail() : para),
//                    "",
//                    this.usuarioRemote.find(Constantes.USUARIO_SIA).getEmail(),
//                    Constantes.ASUNTO_NOTIFICACION_ESTANCIA_PROLONGADA,
//                    this.html.sendNotificacionEstanciaProlongadaParaHuespedForAnalistaBySgOficina(Constantes.ASUNTO_NOTIFICACION_ESTANCIA_PROLONGADA, nombreRuta, huespedes),
//                    siParametroRemote.find(1).getLogo());
//        } else {
//            throw new EmailNotFoundException(usuariosWithoutEmail);
//        }
//    }
//    
//    public boolean sendNotificacionEstanciaProlongadaParaHuespedForGerenteByGerencia(String nombreRuta, int idGerencia, List<String> huespedes, String idUsuarioCambiaSemaforo) throws EmailNotFoundException {
//
//        boolean sendEmail;
//        boolean isPrueba = Constantes.USUARIO_PRUEBA.equals(idUsuarioCambiaSemaforo);
//        UsuarioResponsableGerenciaVo responsableGerencia = this.gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.AP_CAMPO_DEFAULT, idGerencia, false);
//
//        sendEmail = (responsableGerencia.getEmailUsuario() != null && !responsableGerencia.getEmailUsuario().isEmpty());
//
//        if (sendEmail) {
//            return this.enviarCorreoRemote.enviarCorreoIhsa(isPrueba ? this.usuarioRemote.find(Constantes.USUARIO_PRUEBA).getEmail() : responsableGerencia.getEmailUsuario(),
//                    "",
//                    this.usuarioRemote.find(Constantes.USUARIO_SIA).getEmail(),
//                    Constantes.ASUNTO_NOTIFICACION_ESTANCIA_PROLONGADA,
//                    this.html.sendNotificacionEstanciaProlongadaParaHuespedForGerenteByGerencia(Constantes.ASUNTO_NOTIFICACION_ESTANCIA_PROLONGADA, responsableGerencia.getNombreUsuario(), nombreRuta, huespedes),
//                    siParametroRemote.find(1).getLogo());
//        } else {
//            throw new EmailNotFoundException(responsableGerencia.getNombreUsuario());
//        }
//    }
//**---------------------------------------------------------------------------------------
    
    public boolean enviaNotificacionEstanciaProlongadaPorSolicitud(SgSolicitudEstanciaVo solicitudEstanciaVo, List<HuespedVo> listaHuespedHotel, List<HuespedVo> listaHuespedStaff, StringBuilder cuepoCorreo, String nombreRuta, String asunto) throws EmailNotFoundException {
        boolean sendEmail;
        String copia = "";

        UsuarioResponsableGerenciaVo responsableGerencia = this.gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.AP_CAMPO_DEFAULT, solicitudEstanciaVo.getIdGerencia());

        sendEmail = (responsableGerencia.getEmailUsuario() != null && !responsableGerencia.getEmailUsuario().isEmpty());

        if (sendEmail) {
            if (listaHuespedHotel != null && !listaHuespedHotel.isEmpty()) {
                for (HuespedVo vo : listaHuespedHotel) {
                    if (!vo.isInvitado()) {
                        copia += copia.equals("") ? (vo.getEmailHuesped()) : ("," + vo.getEmailHuesped());
                    }
                }
            }

            if (listaHuespedStaff != null && !listaHuespedStaff.isEmpty()) {
                for (HuespedVo vo : listaHuespedStaff) {
                    if (!vo.isInvitado()) {
                        copia += copia.equals("") ? (vo.getEmailHuesped()) : ("," + vo.getEmailHuesped());
                    }
                }
            }

            //terminar correo
            return this.enviarCorreoRemote.enviarCorreoIhsa(responsableGerencia.getEmailUsuario(),
                    copia,
                    this.usuarioRemote.find(Constantes.USUARIO_SIA).getEmail(),
                    Constantes.ASUNTO_NOTIFICACION_ESTANCIA_PROLONGADA,
                    html.getHtmlNotificacionEstanciaProlongadaPorSolicitud(asunto, cuepoCorreo, nombreRuta),
                    siParametroRemote.find(1).getLogo());
        } else {
            throw new EmailNotFoundException(responsableGerencia.getNombreUsuario());
        }

    }

    //**---------------------------------------------------------------------------------------
    
    public StringBuilder obtenerCuerpoCorreoEstanciaProlongadaPorSolicitud(SgSolicitudEstanciaVo solicitudEstanciaVo, List<HuespedVo> listaHuespedHotel, List<HuespedVo> listaHuespedStaff, Date fechaProlongada) {
        return html.getCuerpoHtmlNotificacionEstanciaProlongadaPorSolicitud(solicitudEstanciaVo, listaHuespedHotel, listaHuespedStaff, fechaProlongada, fechaProlongada);
    }

    
    public boolean enviaNotificacionEstanciaProlongadaParaAnalistas(List<StringBuilder> listaCuerposCorreos, int idOficina, String nombreRuta, String idUsuarioGenero) {
        boolean sendEmail;
        String para = "";
        String copia = traerUsuarioAdministradoresSGL();
        List<SgOficinaAnalistaVo> listaAnalista = sgOficinaAnalistaRemote.getAllSgOficinaAnalista(idOficina, "id", true, false);
        for (SgOficinaAnalistaVo vo : listaAnalista) {
            para += para.equals("") ? vo.getEmailAnalista() : "," + vo.getEmailAnalista();
        }

        copia += copia.equals("") ? usuarioRemote.find(idUsuarioGenero).getEmail() : "," + usuarioRemote.find(idUsuarioGenero).getEmail();
        return this.enviarCorreoRemote.enviarCorreoIhsa(para,
                copia,
                "",
                Constantes.ASUNTO_NOTIFICACION_ESTANCIA_PROLONGADA,
                html.getHtmlNotificacionEstanciaProlongadaParaAnalista(Constantes.ASUNTO_NOTIFICACION_ESTANCIA_PROLONGADA, listaCuerposCorreos, nombreRuta),
                siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviarCorreoRegistroProlongadoPorSemaforoHuespedHotel(SgHuespedHotel sgHuespedHotel, String idUsuario) {
        if (sgHuespedHotel.getSgDetalleSolicitudEstancia() != null) {
            log("enviarCorreoRegistroProlongadoPorSemaforoHuespedHotel");
            String cc = "";
            String p = "";
            String nombre = sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario() != null ? sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : sgHuespedHotel.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre();
            UsuarioResponsableGerenciaVo voGerencia = gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.AP_CAMPO_DEFAULT, sgHuespedHotel.getSgSolicitudEstancia().getGerencia().getId());
            if (voGerencia != null) {
                List<Integer> listaRoles = new ArrayList<Integer>();
                listaRoles.add(Constantes.ROL_CENTRO_OPERACION);
                listaRoles.add(Constantes.SGL_SEGURIDAD);
                p = sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario() != null ? sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario().getEmail() : sgHuespedHotel.getSgDetalleSolicitudEstancia().getSgInvitado().getEmail();
                cc = voGerencia.getEmailUsuario();
                for (UsuarioRolVo vo : siUsuarioRolRemote.traerUsuarioByRol(listaRoles, Constantes.AP_CAMPO_NEJO)) {
                    cc += (cc.equals("") ? vo.getCorreo() : "," + vo.getCorreo());
                }
                return enviarCorreoRemote.enviarCorreoIhsa(p,
                        cc,
                        "",
                        "Registro prolongado para Huésped en Hotel " + sgHuespedHotel.getSgDetalleSolicitudEstancia().getSgSolicitudEstancia().getCodigo(),
                        html.getHtmlRegistroProlongadoSemaforoHuespedHotelStaff(sgHuespedHotel.getSgSolicitudEstancia().getCodigo(), nombre, sgHuespedHotel.getSgHotelHabitacion().getSgHotel().getProveedor().getNombre(), sgHuespedHotel.getSgHotelHabitacion().getSgHotel().getSgOficina().getNombre(), true),
                        siParametroRemote.find(1).getLogo());
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    
    public boolean enviarCorreoRegistroProlongadoPorSemaforoHuespedStaff(SgHuespedStaff sgHuespedStaff, String idUsuario) {
        if (sgHuespedStaff.getSgDetalleSolicitudEstancia() != null) {
            log("enviarCorreoRegistroProlongadoPorSemaforoHuespedStaff");
            UsuarioResponsableGerenciaVo voGerencia = gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.AP_CAMPO_DEFAULT, sgHuespedStaff.getSgSolicitudEstancia().getGerencia().getId());
            if (voGerencia != null) {
                String nombre = sgHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario() != null ? sgHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : sgHuespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre();
                return enviarCorreoRemote.enviarCorreoIhsa(voGerencia.getEmailUsuario(),
                        sgHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario() != null ? sgHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getEmail() : sgHuespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado().getEmail(),
                        "",
                        "Registro prolongado para Huésped en Staff House " + sgHuespedStaff.getSgStaffHabitacion().getSgStaff().getNombre(),
                        html.getHtmlRegistroProlongadoSemaforoHuespedHotelStaff(sgHuespedStaff.getSgSolicitudEstancia().getCodigo(), nombre, sgHuespedStaff.getSgStaffHabitacion().getSgStaff().getNombre(), sgHuespedStaff.getSgStaffHabitacion().getSgStaff().getSgOficina().getNombre(), false),
                        siParametroRemote.find(1).getLogo());
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    
    public boolean enviaNotificacionCambioOficinaVehiculo(int idVehiculo, int idOficinaDestino, String motivo, String idUsuario) {
        SgVehiculo sgVehiculo = sgVehiculoRemote.find(idVehiculo);
        SgOficina sgOficina = sgOficinaRemote.find(idOficinaDestino);

        String correoAnalistas = "";

        correoAnalistas = traerCorreoAnalistaOficina(sgVehiculo.getSgOficina().getId());
        correoAnalistas += "," + traerCorreoAnalistaOficina(sgOficina.getId());
        UsuarioVO vo = usuarioRemote.findById(idUsuario);

        return enviarCorreoRemote.enviarCorreoIhsa(vo.getMail(),
                correoAnalistas,
                traerCorreoSIAoPRUEBA(Constantes.USUARIO_SIA),
                "Cambio de vehiculo de oficina",
                html.getHtmlNotificacionCambioOficinaVehiculo(sgVehiculo, sgOficina, motivo, vo.getNombre()),
                siParametroRemote.find(1).getLogo());

    }

    private void log(String mensaje) {
        UtilLog4j.log.info(this, mensaje);
    }

    
    public boolean enviarAvisoNotificacionVencimientoLicenciasPorSemana(int idCampo, List<LicenciaVo> lista, int gerencia) {
        log("enviarAvisoNotificacionVencimientoLicenciasPorSemana");
        String correoAnalistas = "";
        //String correos = traerResponsableSGL();
        //traer analistas por oficinas
        UsuarioResponsableGerenciaVo user;
        List<String> listEmail = new ArrayList<String>();

        if (gerencia > 0) {
            user = getResponsableByGerencia(gerencia);
        } else {
            user = getResponsableByGerencia(Constantes.GERENCIA_ID_SGL);
            listEmail.addAll(sgOficinaAnalistaRemote.getEmailAllAnalistaSGLByCampo(idCampo));
            listEmail.add(traerResponsableSGL(idCampo));
        }

        listEmail.add(user.getEmailUsuario());
        if (!listEmail.isEmpty()) {
            Joiner j = Joiner.on(",").skipNulls();
            correoAnalistas = j.join(listEmail);
        }
        return enviarCorreoRemote.enviarCorreoIhsa(correoAnalistas,
                "",
                "",
                "Licencias a vencer del " + Constantes.FMT_TextDate.format(new Date()) + " al " + siManejoFechaLocal.fechaSumarMes(new Date(), Constantes.MESES_PREVIOS),
                html.getHtmlAvisoVencimientoLicenciaSemanal(lista), siParametroRemote.find(1).getLogo());
//crear vairavle para consulta
    }

    
    public boolean enviarAvisoNotificacionVencimientoCursoManejo(int idCampo, List<CursoManejoVo> lista, int gerencia) {
        String correoAnalistas = "";
        int idoficina = lista.get(0).getIdSgOficina();
        UsuarioResponsableGerenciaVo user;
        List<String> listEmail = new ArrayList<String>();
        List<String> listEmailRolHSE = new ArrayList<>();

        if (gerencia > 0) {
            user = getResponsableByGerencia(gerencia);
        } else {
            user = getResponsableByGerencia(Constantes.GERENCIA_ID_SGL);
            listEmail.addAll(sgOficinaAnalistaRemote.getEmailAllAnalistaSGLByCampo(idCampo));
            listEmailRolHSE = siUsuarioRolRemote.traerCorreosByRolAndOficina(Constantes.CODIGO_ROL_VER_CURSO_MANEJO, idoficina, idCampo);//editar esta parte ya que se cre el rol
            listEmail.add(traerResponsableSGL(idCampo));

        }
        listEmail.add(user.getEmailUsuario());

        if (!listEmail.isEmpty()) {
            Joiner j = Joiner.on(",").skipNulls();
            correoAnalistas = j.join(listEmail);
        }

        if (!listEmailRolHSE.isEmpty()) {
            Joiner j = Joiner.on(",").skipNulls();
            correoAnalistas = j.join(listEmail);
        }

        return enviarCorreoRemote.enviarCorreoIhsa(correoAnalistas,
                "",
                "",
                "Cursos de Manejo a vencer del " + Constantes.FMT_TextDate.format(new Date()) + " al " + siManejoFechaLocal.fechaSumarMes(new Date(), 3),
                html.getHtmlAvisoVencimientoCursoManejo(lista), siParametroRemote.find(1).getLogo());
    }

    
    public void enviarAvisoNotificacionVencimientoLicenciaByUser(LicenciaVo licencia, boolean timer) {
        Usuario user = usuarioRemote.find(licencia.getIdUsuario());
        //UsuarioResponsableGerenciaVo gerente = getResponsableByGerencia(user.getGerencia().getId());
        StringBuilder sb = html.getHtmlAvisoVencimientoLicenciaByUser(licencia);
        String asunto = "";

        if (timer) {
            asunto = "Su licencia de manejo esta proxima a vencer el día " + Constantes.FMT_TextDate.format(licencia.getVencimiento());
        } else {
            asunto = "Aviso importante! Su licencia de manejo esta proxima a vencer el día " + Constantes.FMT_TextDate.format(licencia.getVencimiento());
        }

        enviarCorreoRemote.enviarCorreoIhsa(user.getEmail(),
                "",
                "",
                asunto,
                siParametroRemote.find(1).getLogo(),
                siParametroRemote.find(2).getLogo(),
                sb);
    }

    
    public void enviarAvisoNotificacionVencimientoCursoManejoByUser(CursoManejoVo curso, boolean timer) {
        Usuario user = usuarioRemote.find(curso.getIdUsuario());
        //UsuarioResponsableGerenciaVo gerente = getResponsableByGerencia(user.getGerencia().getId());
        StringBuilder sb = html.getHtmlAvisoVencimientoCursoManejoByUser(curso);
        String asunto = "";

        if (timer) {
            asunto = "El curso de manejo esta próximo a vencer el día " + Constantes.FMT_TextDate.format(curso.getFechaVencimiento());
        } else {
            asunto = "Aviso importante! Curso de manejo próximo a vencer el día " + Constantes.FMT_TextDate.format(curso.getFechaVencimiento());
        }

        enviarCorreoRemote.enviarCorreoIhsa(user.getEmail(),
                "",
                "",
                asunto,
                siParametroRemote.find(1).getLogo(),
                siParametroRemote.find(2).getLogo(),
                sb);

    }
}
