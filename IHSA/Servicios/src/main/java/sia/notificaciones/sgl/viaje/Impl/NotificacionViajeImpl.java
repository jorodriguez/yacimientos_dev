/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.sgl.viaje.Impl;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import sia.constantes.Constantes;
import sia.correo.impl.EnviarCorreoImpl;
import sia.excepciones.SIAException;
import sia.modelo.SgEstatusAprobacion;
import sia.modelo.SgOficinaAnalista;
import sia.modelo.SgViaje;
import sia.modelo.SgViajero;
import sia.modelo.Usuario;
import sia.modelo.sgl.oficina.vo.SgOficinaAnalistaVo;
import sia.modelo.sgl.viaje.vo.CadenaAprobacionSolicitudVO;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.EstatusAprobacionVO;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.notificaciones.estilos.Estilos;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgOficinaAnalistaImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class NotificacionViajeImpl extends Estilos {

    @Inject
    private EnviarCorreoImpl enviarCorreoRemote;
    @Inject
    private HtmlNotificacionViajeImpl htmlNotificacionViajeRemote;
    @Inject
    private SiParametroImpl siParametroRemote;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SgOficinaAnalistaImpl sgOficinaAnalistaRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private SgViajeroImpl sgViajeroRemote;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;
    private Usuario uPrueba;
    private boolean prueba;
    private Usuario usuarioSia;

    private Usuario getResponsableByGerencia(int idGerencia) {
        return this.gerenciaRemote.getResponsableByApCampoAndGerencia(1, idGerencia, false);
    }

    
    public boolean sendMailTravelCompanyCar(String correoPara, String correoCopia, List<ViajeroVO> listTemp, int idVehiculo,
            Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            int idViajeIda, String codigo, boolean vehiculoEmpresa, boolean autobus, boolean vehiculoPropio, int tipoViaje, String origen, int idRuta, String conductorResponsable,
            String telefono) {
        UtilLog4j.log.info(this, "NotificacionViajeImpl.sendMailTravelCompanyCar()");
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                correoCopia,
                "",
                "Viaje - " + codigo,
                htmlNotificacionViajeRemote.bodyMailTravelCompanyCar(listTemp, idVehiculo,
                        fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo,
                        idViajeIda, codigo, vehiculoEmpresa, autobus, vehiculoPropio, tipoViaje, origen, idRuta, conductorResponsable,
                        telefono),
                siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendEmailTravelCompanyCarForGenero(String para, String cc, int idVehiculo, List<ViajeroVO> sgViajeroList,
            Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            int idViajeIda, String codigo, boolean vehiculoEmpresa, boolean autobus, boolean vehiculoPropio, int tipoViaje, String origen, int idRuta, String conductorResponsable,
            String telefono, String responsableGerencia) {
        UtilLog4j.log.info(this, "NotificacionViajeImpl.sendEmailTravelCompanyCarForGenero()");

        return enviarCorreoRemote.enviarCorreoIhsa(para,
                cc,
                "",
                "Viaje - " + codigo,
                this.htmlNotificacionViajeRemote.bodyMailTravelCompanyCarForGeneroViaje(idVehiculo, sgViajeroList, fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo,
                        idViajeIda, codigo, vehiculoEmpresa, autobus, vehiculoPropio, tipoViaje, origen, idRuta, conductorResponsable,
                        telefono, responsableGerencia),
                siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendMailTravelNoCompanyCar(String correoPara, String correoCopia, int idViajero, SolicitudViajeVO solicitudViaje,
            Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            int idViajeIda, String codigo, boolean vehiculoEmpresa, boolean autobus, boolean vehiculoPropio, int tipoViaje, String origen, int idRuta, String conductorResponsable,
            String telefono, String responsableGerencia) {
        UtilLog4j.log.info(this, "NotificacionViajeImpl.sendMailTravelNoCompanyCar()");

        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                correoCopia,
                "",
                "Viaje - " + codigo,
                htmlNotificacionViajeRemote.bodyMailTravelNoCompanyCar(solicitudViaje, idViajero, fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo,
                        idViajeIda, codigo, vehiculoEmpresa, autobus, vehiculoPropio, tipoViaje, origen, idRuta, conductorResponsable,
                        telefono, responsableGerencia),
                siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendCancelTraveller(String correoPara, String correoCopia, Usuario usuario, SgViajero sgViajero, String motivo, Usuario gerente) {
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara, correoCopia, "", "Cancelación del viajero ",
                htmlNotificacionViajeRemote.sendMailCancelTraveller(sgViajero, motivo, gerente), siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendMailCancelTripCompanyCar(String nombre, String correoPara, String correoCopia, String gerente,
            List<ViajeroVO> listaViajero, int idVehiculo, String codigo, Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida,
            Date fechaRegreso, Date horaRegreso, boolean redondo, String motivo, String responsable, String telefono) {
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara, correoCopia, "", "Cancelación de viaje terrestre - " + codigo,
                htmlNotificacionViajeRemote.bodyMailCancelTripCompanyCar(nombre, gerente, listaViajero, idVehiculo, codigo, fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso,
                        redondo, motivo, responsable, telefono), siParametroRemote.find(1).getLogo());

    }

    
    public boolean sendMailCancelTripNoCompanyCar(String nombre, String correoPara, String correoCopia, SgViaje sgViaje, String motivo) {
        UtilLog4j.log.info(this, "NotificacionViajeImpl.sendMailCancelTripNoCompanyCar()");

        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                correoCopia,
                "",
                "Cancelación de viaje terrestre - " + sgViaje.getCodigo(),
                htmlNotificacionViajeRemote.bodyMailCancelTravelNoCompanyCar(nombre, sgViaje, motivo), siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendMailAirTravel(String correoPara, String correoCopia, SolicitudViajeVO solicitudViajeVO, List<ViajeroVO> listTemp,
            Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            int idViajeIda, String codigo, String responsable) {

        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                correoCopia,
                "",
                "Viaje aéreo - " + codigo,
                htmlNotificacionViajeRemote.bodyMailsendAirTravel(solicitudViajeVO.getIdSolicitud(), solicitudViajeVO.getTipoSolicitud(), solicitudViajeVO.getTipoEspecifico(), responsable, solicitudViajeVO.getGerencia(), listTemp,
                        fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo, idViajeIda, codigo), siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviarCorreoSolicitarViaje(SolicitudViajeVO sgSolicitudViaje, CadenaAprobacionSolicitudVO cadenaVo,
            String correoSesion, String responsable) throws SIAException, Exception {
        UtilLog4j.log.info(this, "NotificacionViajeImpl.enviarCorreoSolicitarViaje()" + sgSolicitudViaje.getCodigo());
        String viajerosCopiados = "";
        boolean mailSent = false;

//        setPrueba(sgSolicitudViaje.getGenero().getId().equals(Constantes.USUARIO_PRUEBA) ? true : false);
        //Enviar mail al que generó la Solicitud de Viaje
        //if (sgSolicitudViaje.getGenero().getEmail() != null && !sgSolicitudViaje.getGenero().getEmail().isEmpty()) {
        if (!sgSolicitudViaje.getCorreoGenero().isEmpty()) {
            //Obtener direcciones de correos de viajeros
            List<ViajeroVO> listaViajerosVO = sgViajeroRemote.getAllViajerosList(sgSolicitudViaje.getIdSolicitud());
            if (listaViajerosVO != null && !listaViajerosVO.isEmpty()) {
                for (ViajeroVO vo : listaViajerosVO) {
                    if (vo.isEsEmpleado() && !vo.getIdUsuario().equals(sgSolicitudViaje.getGenero())) {
                        if (viajerosCopiados.equals("")) {
                            viajerosCopiados = !vo.getCorreo().equals("") ? vo.getCorreo() : "";
                        } else {
                            viajerosCopiados += !vo.getCorreo().equals("") ? ("," + vo.getCorreo()) : "";
                        }
                    }
                }
            }
            //Correo para   la persona que solicita el viaje--con copia a los viajeros
            StringBuilder st = htmlNotificacionViajeRemote.getHtmlSolicitarViaje(sgSolicitudViaje, responsable);
            UtilLog4j.log.info(this, "CODIGO DE LA SOLICITUD " + sgSolicitudViaje.getCodigo());
            UtilLog4j.log.info(this, "Viajeros copiados " + viajerosCopiados);
            mailSent = enviarCorreoRemote.enviarCorreoIhsa((sgSolicitudViaje.getCorreoGenero()),
                    viajerosCopiados,
                    usuarioRemote.find(Constantes.USUARIO_SIA).getEmail(),
                    //cadenaVo.getMensajeAsuntoCorreo(sgSolicitudViaje.getCodigo()),
                    Constantes.MENSAJE_ASUNTO_CORREO_SOLICITAR_VIAJE + sgSolicitudViaje.getCodigo(), // >> por ahora lo dejare asi pero este debera de llmar el metodo que retorna Asuntos de correos en el vo de cadena
                    st,
                    siParametroRemote.find(1).getLogo());
            //  }
        }
        return mailSent;
    }

    
    public boolean enviarCorreoSolicitarViajes(SolicitudViajeVO sgSolicitudViaje, SolicitudViajeVO sgSolicitudViaje2, CadenaAprobacionSolicitudVO cadenaVo,
            String correoSesion, String responsable) throws SIAException, Exception {
        UtilLog4j.log.info(this, "NotificacionViajeImpl.enviarCorreoSolicitarViaje()" + sgSolicitudViaje.getCodigo());
        String viajerosCopiados = "";
        boolean mailSent = false;

//        setPrueba(sgSolicitudViaje.getGenero().getId().equals(Constantes.USUARIO_PRUEBA) ? true : false);
        //Enviar mail al que generó la Solicitud de Viaje
        //if (sgSolicitudViaje.getGenero().getEmail() != null && !sgSolicitudViaje.getGenero().getEmail().isEmpty()) {
        if (!sgSolicitudViaje.getCorreoGenero().isEmpty()) {
            //Obtener direcciones de correos de viajeros
            List<ViajeroVO> listaViajerosVO = sgViajeroRemote.getAllViajerosList(sgSolicitudViaje.getIdSolicitud());
            if (listaViajerosVO != null && !listaViajerosVO.isEmpty()) {
                for (ViajeroVO vo : listaViajerosVO) {
                    if (vo.isEsEmpleado() && !vo.getIdUsuario().equals(sgSolicitudViaje.getGenero())) {
                        if (viajerosCopiados.equals("")) {
                            viajerosCopiados = !vo.getCorreo().equals("") ? vo.getCorreo() : "";
                        } else {
                            viajerosCopiados += !vo.getCorreo().equals("") ? ("," + vo.getCorreo()) : "";
                        }
                    }
                }
            }
            //Correo para   la persona que solicita el viaje--con copia a los viajeros
            StringBuilder st = htmlNotificacionViajeRemote.getHtmlSolicitarViajes(sgSolicitudViaje, sgSolicitudViaje2, responsable);
            UtilLog4j.log.info(this, "CODIGO DE LA SOLICITUD " + sgSolicitudViaje.getCodigo());
            UtilLog4j.log.info(this, "Viajeros copiados " + viajerosCopiados);
            mailSent = enviarCorreoRemote.enviarCorreoIhsa((sgSolicitudViaje.getCorreoGenero()),
                    viajerosCopiados,
                    usuarioRemote.find(Constantes.USUARIO_SIA).getEmail(),
                    //cadenaVo.getMensajeAsuntoCorreo(sgSolicitudViaje.getCodigo()),
                    Constantes.MENSAJE_ASUNTO_CORREO_SOLICITAR_VIAJES + sgSolicitudViaje.getCodigo() + " y " + sgSolicitudViaje2.getCodigo(), // >> por ahora lo dejare asi pero este debera de llmar el metodo que retorna Asuntos de correos en el vo de cadena
                    st,
                    siParametroRemote.find(1).getLogo());
            //  }
        }
        return mailSent;
    }

    
    public boolean enviarCorreoEstatusSolicitudViajePorAprobar(SgEstatusAprobacion sgEstatusAprobacion, String asunto,
            SolicitudViajeVO solicitudViajeVO, List<ViajeroVO> lv, int idCampo) throws SIAException, Exception {
        UtilLog4j.log.info(this, "NotificacionViajeImpl.enviarCorreoEstatusSolicitudViajePorAprobar()");
        boolean emailSent = false;
        String para = "";
        String cc = "";
        if (sgEstatusAprobacion.getEstatus().getId() == Constantes.ESTATUS_JUSTIFICAR) {
            List<Integer> l = new ArrayList<Integer>();
            l.add(Constantes.ROL_JUSTIFICA_VIAJES);
            List<UsuarioRolVo> lisUsuarios = siUsuarioRolRemote.traerUsuarioByRol(l, Constantes.AP_CAMPO_DEFAULT);
            for (UsuarioRolVo vo : lisUsuarios) {
                if (para.equals("")) {
                    para = vo.getCorreo();
                } else {
                    para += "," + vo.getCorreo();
                }
            }
            emailSent = enviarCorreoRemote.enviarCorreoIhsa(para, "", "", asunto,
                    this.htmlNotificacionViajeRemote.getHtmlNextAuthorizationSgSolicitudViaje(solicitudViajeVO, asunto,
                            sgEstatusAprobacion.getEstatus().getId(), sgEstatusAprobacion.getUsuario().getId(), idCampo),
                    siParametroRemote.find(1).getLogo());
        } else if (sgEstatusAprobacion.getUsuario() != null && !sgEstatusAprobacion.getUsuario().getEmail().equals(Constantes.VACIO)) {
            //agregar rol notifica SVA
            if (solicitudViajeVO.getIdSgTipoEspecifico() == Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_AEREA
                    && sgEstatusAprobacion.getEstatus().getId() == Constantes.ESTATUS_APROBAR) {
                List<UsuarioRolVo> usuariosList
                        = siUsuarioRolRemote.traerRolPorCodigo(Constantes.COD_ROL_NOTIFICA_SV_AEREAS, Constantes.AP_CAMPO_DEFAULT, Constantes.MODULO_SGYL);
                List<String> correos = new ArrayList<String>();
                List<String> listCc = new ArrayList<String>();
                Joiner j = Joiner.on(",").skipNulls();
                if (usuariosList != null && !usuariosList.isEmpty()) {
                    for (UsuarioRolVo u : usuariosList) {
                        if (!u.isPrincipal()) {
                            listCc.add(u.getCorreo());
                        } else {
                            correos.add(u.getCorreo());
                        }

                    }
                }
                if (lv != null) {
                    for (ViajeroVO v : lv) {
                        if (!v.getCorreo().isEmpty()) {
                            listCc.add(v.getCorreo());
                        }
                    }
                }
                listCc.add(sgEstatusAprobacion.getUsuario().getEmail());
                listCc.add(sgEstatusAprobacion.getGenero().getEmail());
                para = j.join(correos);
                cc = j.join(listCc);
            } else {
                para = sgEstatusAprobacion.getUsuario().getEmail();
            }
            emailSent = enviarCorreoRemote.enviarCorreoIhsa(para, cc, "", asunto,
                    this.htmlNotificacionViajeRemote.getHtmlNextAuthorizationSgSolicitudViaje(solicitudViajeVO, asunto,
                            sgEstatusAprobacion.getEstatus().getId(), sgEstatusAprobacion.getUsuario().getId(), idCampo),
                    siParametroRemote.find(1).getLogo());

        }
        return emailSent;
    }

    
    public boolean enviarCorreoEstatusSolicitudesViajePorAprobar(SgEstatusAprobacion sgEstatusAprobacion, SgEstatusAprobacion sgEstatusAprobacion2, String asunto, SolicitudViajeVO solicitudViajeVO, SolicitudViajeVO solicitudViajeVO2) throws SIAException, Exception {
        UtilLog4j.log.info(this, "NotificacionViajeImpl.enviarCorreoEstatusSolicitudViajePorAprobar()");
        boolean emailSent = false;
        String para = "";
        if (sgEstatusAprobacion.getEstatus().getId() == Constantes.ESTATUS_JUSTIFICAR || sgEstatusAprobacion2.getEstatus().getId() == Constantes.ESTATUS_JUSTIFICAR) {
            List<Integer> l = new ArrayList<Integer>();
            l.add(Constantes.ROL_JUSTIFICA_VIAJES);
            List<UsuarioRolVo> lisUsuarios = siUsuarioRolRemote.traerUsuarioByRol(l, Constantes.AP_CAMPO_DEFAULT);
            for (UsuarioRolVo vo : lisUsuarios) {
                if (para.equals("")) {
                    para = vo.getCorreo();
                } else {
                    para += "," + vo.getCorreo();
                }
            }
            emailSent = enviarCorreoRemote.enviarCorreoIhsa(para, "", "", asunto,
                    this.htmlNotificacionViajeRemote.getHtmlNextAuthorizationSgSolicitudesViaje(solicitudViajeVO, solicitudViajeVO2, asunto, sgEstatusAprobacion.getEstatus().getId(), sgEstatusAprobacion.getUsuario().getId()),
                    siParametroRemote.find(1).getLogo());
        } else if ((sgEstatusAprobacion.getUsuario() != null && !sgEstatusAprobacion.getUsuario().getEmail().equals(Constantes.VACIO))
                && (sgEstatusAprobacion2.getUsuario() != null && !sgEstatusAprobacion2.getUsuario().getEmail().equals(Constantes.VACIO))) {
            emailSent = enviarCorreoRemote.enviarCorreoIhsa(sgEstatusAprobacion.getUsuario().getEmail(), "", "", asunto,
                    this.htmlNotificacionViajeRemote.getHtmlNextAuthorizationSgSolicitudesViaje(solicitudViajeVO, solicitudViajeVO2, asunto, sgEstatusAprobacion.getEstatus().getId(), sgEstatusAprobacion.getUsuario().getId()),
                    siParametroRemote.find(1).getLogo());

        }
        return emailSent;
    }

    /**
     * Joel Rodriguez Metodo privado que obtiene solo las direcciones de correos
     * de los usuarios analistas de la oficina enviado como parametro usado en
     * el envio de correos para hacer viajes en la cadena de aprobaciones
     *
     *
     * @param idOficina
     * @return
     */
    private String obetenerCorreosDeAnalistasPorOficina(int idOficina) {
        StringBuilder sbRetorno = new StringBuilder();
        //List<SgOficinaAnalista> sgOficinaAnalistaList = sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(sgSolicitudViaje.getOficinaOrigen(), Constantes.NO_ELIMINADO);
        List<SgOficinaAnalistaVo> listaOficinaAnalista = sgOficinaAnalistaRemote.getAllSgOficinaAnalista(idOficina, "id", true, false);
        if (listaOficinaAnalista != null && !listaOficinaAnalista.isEmpty()) {
            for (SgOficinaAnalistaVo vo : listaOficinaAnalista) {
                if (!vo.getEmailAnalista().equals(Constantes.VACIO)) {
                    if (sbRetorno.toString().equals(Constantes.VACIO)) {
                        sbRetorno.append(vo.getEmailAnalista());
                    } else {
                        sbRetorno.append(",");
                        sbRetorno.append(vo.getEmailAnalista());
                    }
                }
            }
        }
        return sbRetorno.toString();
    }

    /**
     * Envia la notificacion a los analistas por oficina origen de la solicitud,
     * para crear el viaje.
     *
     * @param sgEstatusAprobacion
     * @return
     */
    
    public boolean sendMailPrepareTravel(String correoUsuario, SolicitudViajeVO solicitudViaje, String asunto, String responsable, List<ViajeroVO> lv) {
        UtilLog4j.log.info(this, "NotificacionViajeImpl.sendMailPrepareTravel()");
        boolean correoEnviado = false;
        //traer cuerpo de correo
        cuerpoCorreo = htmlNotificacionViajeRemote.getHtmlPrepareTravel(solicitudViaje, asunto, responsable, lv);

        return enviarCorreoRemote.enviarCorreoIhsa(correoUsuario,
                "",
                "",
                asunto,
                cuerpoCorreo,
                siParametroRemote.find(1).getLogo());

    }

    /**
     * Modifico: NLopez 07/11/2013 Traer correos de responsable y seguridad
     *
     * @param sgEstatusAprobacion
     * @param nombreUsuario
     * @return
     */
    
    public boolean sendMailApprovedTravel(SgEstatusAprobacion sgEstatusAprobacion, String nombreUsuario, SolicitudViajeVO solicitudViaje,
            String responsable, List<ViajeroVO> lv) {
        UtilLog4j.log.info(this, "NotificacionViajeImpl.sendMailApprovedTravel()");

        UsuarioResponsableGerenciaVo urgv = gerenciaRemote.traerResponsablePorApCampoYGerencia(1, solicitudViaje.getIdGerencia());

        String asunto = "Justificación de Viaje Aprobado";
        String para = "";
        boolean sendEmail = false;

        UtilLog4j.log.info(this, "SgEstatusAprobacion.estatus.id = " + sgEstatusAprobacion.getEstatus().getId().intValue());

        List<SgOficinaAnalistaVo> sgOficinaAnalistaList = sgOficinaAnalistaRemote.getAllSgOficinaAnalista(solicitudViaje.getIdOficinaOrigen(), "id", true, false);

        if (sgOficinaAnalistaList != null) {
            for (int i = 0; i < sgOficinaAnalistaList.size(); i++) {
                if (i == sgOficinaAnalistaList.size() - 1) {
                    para += sgOficinaAnalistaList.get(i).getEmailAnalista();
                } else {
                    para += "," + sgOficinaAnalistaList.get(i).getEmailAnalista();
                }
            }
        }
        if (!para.trim().isEmpty()) {
            para += ",";
        }
        para += urgv.getEmailUsuario();

        //Enviar al Responsable de Capacitación
        if (solicitudViaje.getIdSgTipoSolicitudViaje() == 6) { //SgTipoSolicitudViaje.id=6 (Capacitación)
            Usuario u = this.usuarioRemote.findUsuarioResponsableCapacitacion();
            if (u != null) {
                if (para.trim().isEmpty()) {
                    para += u.getEmail();
                } else {
                    para += ",";
                    para += u.getEmail();
                }
            }
        } //Si solicitó Dirección General(11), Dirección Técnica(62) o Subdirección Ténica(63) avisar a Asistente Dirección
        else if (solicitudViaje.getIdGerencia() == 11 || solicitudViaje.getIdGerencia() == 62 || solicitudViaje.getIdGerencia() == 63) {
            List<Usuario> asistenteDireccionlist = this.usuarioRemote.traerUsuarioAsisteneDireccion();
            if (asistenteDireccionlist != null) {
                if (!para.trim().isEmpty()) {
                    para += ",";
                }
                for (int i = 0; i < asistenteDireccionlist.size(); i++) {
                    if (i == asistenteDireccionlist.size() - 1) {
                        para += asistenteDireccionlist.get(i).getEmail();
                    } else {
                        para += asistenteDireccionlist.get(i).getEmail() + ",";
                    }
                }
            }
        }

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

        if (correoPara.length() != 0) {
            para += "," + correoPara.toString();
        }

//        List<Usuario> responsableSGLlist = this.usuarioRemote.traerUsuarioResponsableSGL();
//        if (responsableSGLlist != null) {
//
//            for (int i = 0; i < responsableSGLlist.size(); i++) {
//                if (i == responsableSGLlist.size() - 1) {
//                    if (!para.trim().isEmpty()) {
//                        para += ",";
//                    }
//                    para += responsableSGLlist.get(i).getEmail();
//                } else {
//                    para += "," + responsableSGLlist.get(i).getEmail();
//                }
//            }
//        }
        sendEmail = true;

        if (sendEmail) {
            UtilLog4j.log.info(this, "Para: " + para);
            UtilLog4j.log.info(this, "CC: " + "");
            UtilLog4j.log.info(this, "CCO: " + "");
            UtilLog4j.log.info(this, "Asunto: " + asunto);
            return enviarCorreoRemote.enviarCorreoIhsa(para,
                    "",
                    "",
                    asunto,
                    htmlNotificacionViajeRemote.getHtmlApprovedTravel(solicitudViaje, asunto, nombreUsuario, urgv.getNombreUsuario(), lv), siParametroRemote.find(1).getLogo());
        } else {
            UtilLog4j.log.info(this, "No es necesario enviar mail ya que la Solicitud de Viaje aún no llega con el Analista");
            return true;
        }
    }

    private String getEmailAnalistasByOficina(int idSgOficina) {
        List<SgOficinaAnalista> list = sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(idSgOficina, Constantes.BOOLEAN_FALSE);
        String emails = "";

        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                emails += list.get(i).getAnalista().getEmail();
            } else {
                emails += list.get(i).getAnalista().getEmail() + ",";
            }
        }
        return emails;
    }

    
    public boolean enviarCorreoSolicitudViajeCancelada(SgEstatusAprobacion estatus, String motivo, Usuario usuarioRealizo, boolean notificar,
            SolicitudViajeVO solicitudViaje, String responsable, boolean cancelaViajero) throws SIAException, Exception {
        UtilLog4j.log.info(this, "## ENVIANDO CORREO DE NOTIFICACION DE CANCELACION DE SOLICITUD........ " + estatus.getUsuario().getNombre());
        String correoCopia = "";
        String correoCopiaViajeros = "";

        if (cancelaViajero) {
            List<UsuarioVO> lu = new ArrayList<>();
            List<String> lEmail = new ArrayList<>();

            Joiner joiner = Joiner.on(',').skipNulls();
            
            switch (estatus.getEstatus().getId()) {
                case Constantes.ESTATUS_APROBAR:
                    correoCopia = getResponsableByGerencia(estatus.getSgSolicitudViaje().getGerenciaResponsable().getId()).getEmail();

                    if (solicitudViaje.getIdSgTipoEspecifico() == 3) {
                        lu = usuarioRemote.getUsuariosPorRolBloque(79, Constantes.AP_CAMPO_DEFAULT);
                        for (UsuarioVO u : lu) {
                            lEmail.add(u.getMail());
                        }
                        String sgl = joiner.join(lEmail);
                        correoCopia = getResponsableByGerencia(estatus.getSgSolicitudViaje().getGerenciaResponsable().getId()).getEmail()
                                + "," + sgl;
                    }
                    break;
                case Constantes.ESTATUS_VISTO_BUENO:
                    correoCopia = getResponsableByGerencia(estatus.getSgSolicitudViaje().getGerenciaResponsable().getId()).getEmail();
                    break;
                case Constantes.ESTATUS_CON_CENTOPS:
                case Constantes.ESTATUS_JUSTIFICAR:
                    lu = usuarioRemote.getUsuariosPorRol(Constantes.ROL_JUSTIFICA_VIAJES);
                    for (UsuarioVO u : lu) {
                        lEmail.add(u.getMail());
                    }
                    String just = joiner.join(lEmail);
                    correoCopia = getResponsableByGerencia(estatus.getSgSolicitudViaje().getGerenciaResponsable().getId()).getEmail()
                            + "," + just;
                    break;
                case Constantes.ESTATUS_PARA_HACER_VIAJE:
                    lEmail = new ArrayList<>();
                    lu = usuarioRemote.getUsuariosPorRol(Constantes.SGL_ANALISTA);
                    for (UsuarioVO u : lu) {
                        lEmail.add(u.getMail());
                    }
                    if (solicitudViaje.getIdSolicitudEstancia() > 0) {
                        lu = usuarioRemote.getUsuariosPorRol(Constantes.ROL_ID_ADM_ESTANCIA);
                        for (UsuarioVO u : lu) {
                            lEmail.add(u.getMail());
                        }
                    }
                    

                    String analistas = joiner.join(lEmail);
                    correoCopia = getResponsableByGerencia(estatus.getSgSolicitudViaje().getGerenciaResponsable().getId()).getEmail()
                            + "," + analistas;
                    break;
                default:
                    UtilLog4j.log.info(this, "estatus" + estatus.getEstatus().getId());
                    break;
            }

        } else {
            correoCopia = estatus.getSgSolicitudViaje().getGenero().getEmail();
            correoCopiaViajeros += sgViajeroRemote.correosViajerosPorSolicitud(solicitudViaje.getIdSolicitud());
            if (notificar) {
                correoCopia += "," + copiaDireccion(11) + "," + copiaDireccion(49);
            }
            //sacar viajeros de una solicitud para sacar su email
            correoCopia += !correoCopiaViajeros.equals("") ? ("," + correoCopiaViajeros) : "";
            correoCopia += ","+getResponsableByGerencia(estatus.getSgSolicitudViaje().getGerenciaResponsable().getId()).getEmail();
        }

        UtilLog4j.log.info(this, "Correo copia Direccion: " + correoCopia);
        UtilLog4j.log.info(this, "##########Correo copia viajeros: " + correoCopiaViajeros);

        if (estatus.getSgSolicitudViaje().getGenero().getId().equals("PRUEBA")) {
            return enviarCorreoRemote.enviarCorreoIhsa(usuarioRealizo.getEmail(),
                    correoCopia,
                    "", "Se ha cancelado la solicitud de viaje : " + estatus.getSgSolicitudViaje().getCodigo() + "",
                    htmlNotificacionViajeRemote.getHtmlSolicitudCancelada(solicitudViaje, motivo, usuarioRealizo, estatus.getFechaModifico(), estatus.getHoraModifico(), responsable), siParametroRemote.find(1).getLogo());
        } else {
            return enviarCorreoRemote.enviarCorreoIhsa(usuarioRealizo.getEmail(),
                    correoCopia,
                    "",
                    "Se ha cancelado la solicitud de viaje : " + estatus.getSgSolicitudViaje().getCodigo() + "",
                    htmlNotificacionViajeRemote.getHtmlSolicitudCancelada(solicitudViaje, motivo, usuarioRealizo, estatus.getFechaModifico(), estatus.getHoraModifico(), responsable), siParametroRemote.find(1).getLogo());
        }
    }

    private String copiaDireccion(int idGerencia) {
        try {
            String correo = getResponsableByGerencia(idGerencia).getEmail();
            return correo;
        } catch (Exception e) {
            return "";
        }
    }

    
    public boolean sendMailStopTrip(String correoPara, String correoCopia, SgViaje sgViaje, List<ViajeroVO> l, String motivo) {
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara, correoCopia, "", "Viaje detenido - " + sgViaje.getCodigo(),
                htmlNotificacionViajeRemote.sendMailStopTrip(sgViaje, l, motivo), siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendMailAddTravellerToTrip(String correoPara, String correoCopia, Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            String codigo, String responsable, String origen, int idRuta, int tipoViaje, int idViaje,
            String viajero) {
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                correoCopia,
                "",
                "Viajero agregado al viaje - " + codigo,
                htmlNotificacionViajeRemote.sendMailAddTravellerToTrip(fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso,
                        redondo, codigo, responsable, origen, idRuta, tipoViaje, idViaje, viajero),
                siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendTakeOutTravellToTraveller(String correoPara, String correoCopia, String codigo, Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            String responsable, String viajero, boolean ida, String origen, int idRuta, int tipoRuta) {
        UtilLog4j.log.info(this, "NotificacionViajeImpl.sendMailPublicTravelCompanyCar()");

        UtilLog4j.log.info(this, "SgViaje is ida?: " + ida);
        //Aquí solo se avisa que el Viajero se bajó antes de irse el viaje de ida
        if (ida) {
            UtilLog4j.log.info(this, "Para: " + correoPara);
            UtilLog4j.log.info(this, "CC: " + correoCopia);
            UtilLog4j.log.info(this, "CCO:" + "");
            UtilLog4j.log.info(this, "Asunto: " + "Viajero quitado del viaje - " + codigo);
            return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                    correoCopia,
                    "",
                    "Viajero quitado del viaje - " + codigo,
                    htmlNotificacionViajeRemote.sendTakeOutTravellToTraveller(codigo, fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo,
                            responsable, viajero), siParametroRemote.find(1).getLogo());
        } else {

            //Aquí se debe avisar que el Viajero se bajó en el destino o en su viaje de regreso
            UtilLog4j.log.info(this, "Para: " + correoPara);
            UtilLog4j.log.info(this, "CC: " + correoCopia);
            UtilLog4j.log.info(this, "CCO:" + "");
            UtilLog4j.log.info(this, "Asunto: " + "Viajero quitado del viaje - " + codigo);
            return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                    correoCopia,
                    "",
                    "Viajero llegó a su destino - " + codigo,
                    htmlNotificacionViajeRemote.sendEmailTakeOutTravellToTravellerInDestiny(codigo, fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo,
                            responsable, viajero, origen, idRuta, tipoRuta), siParametroRemote.find(1).getLogo());
        }
    }

    
    public boolean sendMailPublicTravelCompanyCar(String publicaViaje, Date fechaProgramada, Date horaProgramada, Date fechaSalida,
            Date horaSalida, Date fechaRegreso, Date horaRegreso, String codigo, boolean redondo, int tipoViaje, String origen,
            int idRuta, String conductorResponsable, String telefono, String idUsuarioSesion) {
        return enviarCorreoRemote.enviarCorreoIhsa(publicaViaje,
                "",
                "",
                "Logística de viaje - " + codigo,
                htmlNotificacionViajeRemote.bodyMailPublicTravelCompanyCar(fechaProgramada, horaProgramada, fechaSalida,
                        horaSalida, fechaRegreso, horaRegreso, codigo, redondo, tipoViaje, origen, idRuta, conductorResponsable, telefono), siParametroRemote.find(1).getLogo());

    }

    
    public boolean sendMailPublicTravelCompanyCarProgram(String publicaViaje, Date fechaProgramada, Date horaProgramada, Date fechaSalida,
            Date horaSalida, Date fechaRegreso, Date horaRegreso, String codigo, boolean redondo, int tipoViaje, String origen,
            int idRuta, String conductorResponsable, String telefono, String idUsuarioSesion) {
        return enviarCorreoRemote.enviarCorreoIhsa(publicaViaje,
                "",
                "",
                "Validar Viaje - " + codigo,
                htmlNotificacionViajeRemote.bodyMailAnalistaPrincipal(fechaProgramada, horaProgramada, fechaSalida,
                        horaSalida, fechaRegreso, horaRegreso, codigo, redondo, tipoViaje, origen, idRuta, conductorResponsable, telefono), siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendMailTravellerToTrip(String correoPara, String copiados, int idViaje, String codigo, SgViajero sgViajero) {
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                copiados,
                "",
                "Salida de viaje- " + codigo,
                htmlNotificacionViajeRemote.bodyMailTravel(idViaje, codigo, sgViajero),
                siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendMailCancelTripSecurity(String nombre, String correoPara, int idViaje, String codigo, String motivo) {
        UtilLog4j.log.info(this, "NotificacionViajeImpl.sendMailCancelTripSecurity()");
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                "",
                "",
                "Cancelación del viaje- " + codigo,
                htmlNotificacionViajeRemote.cancelTripSecurity(nombre, idViaje, codigo, motivo), siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendMailPausarViajeSecuridad(String nombre, String correoPara, int idViaje, String codigo, String motivo) {
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                "",
                "",
                "Gestión de Riesgos supende el viaje - " + codigo,
                htmlNotificacionViajeRemote.pausarViajeSeguridad(nombre, idViaje, codigo, motivo), siParametroRemote.find(1).getLogo());
    }

    /*
     * Notifica a los analistas en ruta para que esten enterados del viaje
     */
    
    public boolean sendMailNotificarAnalistas(String codigo, Date fechaProgramada, Date horaProgramada, Date fechaSalida,
            Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo, int tipoViaje, String origen, int idRuta,
            String conductor, String telefono, boolean ida, String eMails, String idSesion) {
        UtilLog4j.log.info(this, "sendMailNotificarAnalistas");

        return enviarCorreoRemote.enviarCorreoIhsa(eMails,
                "",
                "",
                "Logística de viaje - ".concat(codigo),
                htmlNotificacionViajeRemote.bodyMailNotificationByAnalistInRoute(codigo, fechaProgramada, horaProgramada, fechaSalida,
                        horaSalida, fechaRegreso, horaRegreso, redondo, tipoViaje, origen, idRuta,
                        conductor, telefono, ida),
                siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendMailNotificacionParaFinalizacionViaje(String codigo, Date fechaProgramada,
            Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso,
            Date horaRegreso, boolean redondo, int tipoViaje, String origen,
            int idRuta, String conductor, String telefono,
            boolean ida, String eMails, String idUsuario) {
        UtilLog4j.log.info(this, "sendMailNotificacionParaFinalizacionViaje");

        return enviarCorreoRemote.enviarCorreoIhsa(eMails,
                "",
                "",
                "Finalizar viaje - " + codigo,
                htmlNotificacionViajeRemote.bodyMailNotificationByOficceForFinalize(codigo, fechaProgramada, horaProgramada, fechaSalida,
                        horaSalida, fechaRegreso, horaRegreso, redondo, tipoViaje, origen, idRuta),
                siParametroRemote.find(1).getLogo());

    }

    /*
     * Notifica a los analistas en ruta para que esten enterados del viaje
     */
    
    public boolean sendMailNotificarDireccionGral(List<ViajeVO> listaViaje, String eMails, String fechaSalida) {
        UtilLog4j.log.info(this, "sendMailNotificarDireccionGral");
        return enviarCorreoRemote.enviarCorreoIhsa(eMails,
                "",
                "",
                "Reporte de viajes - " + fechaSalida,
                htmlNotificacionViajeRemote.bodyMailNotificationByDireccionGral(listaViaje, fechaSalida),
                siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendMailExceptionError(String by, String excepcion, String cuerpoCorreo) {
        UtilLog4j.log.info(this, "sendMailExceptionError");
        return enviarCorreoRemote.enviarCorreoIhsa(by,
                "",
                "", "Excepcion : " + excepcion,
                new StringBuilder(cuerpoCorreo),
                siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendMailReturnTripCopanyCar(String c, String cc, String cco, List<ViajeroVO> lista, int idVehiculo, Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            int idViajeIda, String codigo, boolean vehiculoEmpresa, boolean autobus, boolean vehiculoPropio, int tipoViaje, String origen, int idRuta, String conductorResponsable,
            String telefono, String responsableGerencia) {
        return enviarCorreoRemote.enviarCorreoIhsa(c,
                cc,
                "",
                "Viaje  : " + codigo,
                htmlNotificacionViajeRemote.bodyMailTravelCompanyCarForGeneroViaje(idVehiculo, lista, fechaProgramada, horaProgramada,
                        fechaSalida, horaSalida, fechaRegreso, horaRegreso, redondo,
                        idViajeIda, codigo, vehiculoEmpresa, autobus, vehiculoPropio, tipoViaje, origen, idRuta, conductorResponsable,
                        telefono, responsableGerencia), siParametroRemote.find(1).getLogo());

    }

    
    public boolean sendMailAvisoAnalistaViajeCiudadRegreso(SgViaje sgViaje) {
        UtilLog4j.log.info(this, "NotificacionViajeImpl.sendMailAvisoAnalistaViajeCiudadRegreso()");

        List<SgOficinaAnalista> oficinaAnalistaList = sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(sgViaje.getSgOficina().getId(), Constantes.NO_ELIMINADO);
        boolean sendEmail = true;

        if (oficinaAnalistaList != null) {
            for (SgOficinaAnalista oa : oficinaAnalistaList) {

                sendEmail = enviarCorreoRemote.enviarCorreoIhsa(oa.getAnalista().getEmail(),
                        "",
                        "",
                        "Viaje de regreso  : " + sgViaje.getCodigo(),
                        htmlNotificacionViajeRemote.bodyMailAvisoAnalistaViajeCiudadRegreso(sgViaje), siParametroRemote.find(1).getLogo());
            }
        }

        return sendEmail;
    }

    
    public boolean sendMailModifyTravelCompanyCar(String correoPara, String correoCopia, List<ViajeroVO> lvro, VehiculoVO vehiculoVO,
            SgViaje sgViaje) {
        int tipoViaje = 0;
        if (sgViaje.getSgViajeCiudad() != null) {
            tipoViaje = Constantes.RUTA_TIPO_CIUDAD;
        } else if (sgViaje.getSgViajeLugar() != null) {
            tipoViaje = Constantes.RUTA_TIPO_LUGAR;
        } else {
            tipoViaje = Constantes.RUTA_TIPO_OFICINA;
        }

        //
        log("NotificacionViajeImpl.modificacion Viaje()");
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                correoCopia,
                "",
                "Modificación del Viaje - " + sgViaje.getCodigo(),
                htmlNotificacionViajeRemote.bodyMailModifyTravelCompanyCar(lvro, vehiculoVO, sgViaje.getCodigo(), sgViaje.getFechaProgramada(),
                        sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(), sgViaje.getFechaRegreso(), sgViaje.getHoraRegreso(), sgViaje.isRedondo(),
                        tipoViaje, sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), sgViaje.getResponsable().getNombre(),
                        sgViaje.getResponsable().getTelefono() != null ? sgViaje.getResponsable().getTelefono() : "-", sgViaje.isVehiculoAsignadoEmpresa(),
                        sgViaje.isAutobus()),
                siParametroRemote.find(1).getLogo());
    }

    /**
     * Creo: NLopez
     *
     * @param sgViaje
     * @param listaViajero
     * @param eMails
     * @return
     */
    
    public boolean pausarViajeSeguridad(String nombre, String correoPara, int idViaje, String codigo, String motivo) {

        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                "",
                "",
                ""
                + "Gestión de Riesgos supende el viaje - " + codigo,
                htmlNotificacionViajeRemote.pausarViajeSeguridad(nombre, idViaje, codigo, motivo),
                siParametroRemote.find(1).getLogo());
    }

    /**
     * Creo: NLopez
     *
     * @param sgViaje
     * @param listaViajero
     * @param eMails
     * @return
     */
    
    public boolean aprobarViajeDireccion(String nombre, String correoPara, int idViaje, String codigo) {

        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                "",
                "",
                "Gestión de Riesgos autorizó el viaje - " + codigo,
                htmlNotificacionViajeRemote.aprobarViajeDireccion(nombre, idViaje),
                siParametroRemote.find(1).getLogo());
    }

    @Deprecated
    
    public boolean sendMailTravelNoCompanyCarCity(String email, String ccp, List<ViajeroVO> listViajero, String codigo,
            Date fechaProgramada, Date horaProgramada, Date fechaSalida, Date horaSalida, Date fechaRegreso, Date horaRegreso, boolean redondo,
            int tipoViaje, String origen, int idRuta, String conductor, String telefono) {
        return enviarCorreoRemote.enviarCorreoIhsa(email,
                ccp,
                "",
                "Viaje - " + codigo,
                htmlNotificacionViajeRemote.bodyMailTravelNoCompanyCarCity(listViajero, codigo,
                        fechaProgramada, horaProgramada, fechaSalida, horaSalida, fechaRegreso, horaRegreso,
                        redondo, tipoViaje, origen, idRuta, conductor, telefono),
                siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviarCorreoSolicitudCambioItinerario(String correoPara, String correoCopia, String tipoItinerario, String mensaje, String codigo, String empleado, int idSolicitud) {
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                correoCopia,
                "sia@ihsa.mx",
                "Cambio de itinerario de la solicitud - " + codigo,
                htmlNotificacionViajeRemote.coreoSolicitudCambioItinerario(mensaje, codigo, tipoItinerario, empleado, idSolicitud),
                siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviarCorreoSolicitudCambioItinerarioGenero(String correoPara, String tipoItinerario, String mensaje, String codigo, String empleado, int idSolicitud) {
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                "",
                "",
                "Cambio de itinerario de la solicitud - " + codigo,
                htmlNotificacionViajeRemote.coreoSolicitudCambioItinerarioGenero(mensaje, codigo, tipoItinerario, empleado, idSolicitud),
                siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviarcorreoCambioItinerario(int idSolicitud, String correoPara, String consecutivo, boolean ida) {
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                "",
                "",
                "Itinerario de la solicitud - " + consecutivo,
                htmlNotificacionViajeRemote.coreoCambioItinerario(idSolicitud, consecutivo, ida),
                siParametroRemote.find(1).getLogo());
    }

    
    public boolean enviaCorreoAnalistaViajeEnAutorizacion(String cc, String ccp, int idViaje, String codigo, String motivo, String nombre) {
        return enviarCorreoRemote.enviarCorreoIhsa(cc,
                ccp,
                "",
                "Gestión de Riesgos supende el viaje - " + codigo,
                htmlNotificacionViajeRemote.pausarViajeSeguridadServiciosGenerales(idViaje, codigo, motivo, nombre), siParametroRemote.find(1).getLogo());
    }

    private void log(String mensaje) {
        UtilLog4j.log.info(this, mensaje);
    }

    
    public boolean sendMailNotificaViajero(String correoPara, ViajeVO viajeVO, String fechaSalida, String correoSeguridad, String telefonoSeguridad) {
        return enviarCorreoRemote.enviarCorreoIhsa(correoPara,
                "",
                "",
                "Salida de viaje - " + viajeVO.getCodigo(),
                htmlNotificacionViajeRemote.mailNotificaSalidaViajeAereo(viajeVO, correoSeguridad, telefonoSeguridad), siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendMailNotificaGestionRiesgo(String cc, List<ViajeVO> lv, String fechaSalida) {
        return enviarCorreoRemote.enviarCorreoIhsa(cc,
                "",
                "",
                "Reporte de salida de viaje - " + fechaSalida,
                htmlNotificacionViajeRemote.mailNotificaSalidaViajeAereoGestionRiesgo(lv, fechaSalida), siParametroRemote.find(1).getLogo());
    }

    
    public boolean correoCambioUsuarioAprobacion(String para, String cc, List<EstatusAprobacionVO> lo, String nombreAprobara, String nombreTenia, String correoSesion, String status) {
        String asunto = "Cambio de solicitudes de viajes -- ".concat(Constantes.FMT_ddMMyyy.format(new Date()));
        return enviarCorreoRemote.enviarCorreoIhsa(para,
                cc,
                correoSesion,
                asunto,
                htmlNotificacionViajeRemote.correoCambioSolicitudViaje(lo, nombreTenia, nombreAprobara, asunto, status),
                siParametroRemote.find(1).getLogo());

    }

    
    public boolean enviarCorreoCentopsSalidaViaje(SgViaje sgViaje, String telefono, List<ViajeroVO> viajero) {
        String asunto = "Salida de viaje -- " + sgViaje.getCodigo();
        return enviarCorreoRemote.enviarCorreoIhsa(traerResponsableSGLySeguridad(),
                obetenerCorreosDeAnalistasPorOficina(Constantes.CERO),
                "", asunto,
                htmlNotificacionViajeRemote.mensajeSalidaViaje(sgViaje, asunto, telefono, viajero),
                siParametroRemote.find(1).getLogo());
    }

    /**
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
        List<String> correos = new ArrayList<String>();
        Joiner j = Joiner.on(",").skipNulls();
        if (lu != null) {
            for (UsuarioRolVo usuario1 : lu) {
                correos.add(usuario1.getCorreo());
            }
            correos.add(usuarioRemote.traerResponsableGerencia(Constantes.AP_CAMPO_DEFAULT, Constantes.GERENCIA_ID_SGL, "").getMail());
            correoPara = j.join(correos);
        } else {
            correoPara = "siaihsa@ihsa.mx";
        }
        return correoPara;
    }

    private String correoAnalista(int idOficina) {
        List<SgOficinaAnalistaVo> l = sgOficinaAnalistaRemote.getAllSgOficinaAnalista(idOficina, "id", true, false);
        String correo = "";
        for (SgOficinaAnalistaVo l1 : l) {
            if (correo.isEmpty()) {
                correo = l1.getEmailAnalista();
            } else {
                correo += "," + l1.getEmailAnalista();
            }
        }
        return correo;
    }

    
    public boolean mensajeLlegada(ViajeVO viajeVO, String usuarioSesion, int llegoA, String punto) {
        String cc = correoAnalista(viajeVO.getIdOficinaOrigen());
        if (llegoA == Constantes.DESTINO && viajeVO.getIdSgViajeCiudad() == 0) {
            cc += "," + correoAnalista(viajeVO.getIdOficinaDestino());
        }
        return enviarCorreoRemote.enviarCorreoIhsa(traerResponsableSGLySeguridad(), cc, "", "Llegada del viaje -" + viajeVO.getCodigo(),
                htmlNotificacionViajeRemote.mensajeLlegada(viajeVO, llegoA, punto), siParametroRemote.find(Constantes.UNO).getLogo());
    }

    
    public boolean mensajeSalidaPunto(ViajeVO viajeVO, int intercambio, String punto) {
        String cc = correoAnalista(viajeVO.getIdOficinaOrigen());
        List<SgOficinaAnalistaVo> l = sgOficinaAnalistaRemote.getAllSgOficinaAnalista(viajeVO.getIdOficinaOrigen(), "id", true, false);
        List<ViajeroVO> lv;
        String destino = "";
        if (intercambio == Constantes.CERO) {
            lv = sgViajeroRemote.getTravellersByTravel(viajeVO.getId(), null);
            if (viajeVO.getIdSgViajeCiudad() > Constantes.CERO) {
                destino = viajeVO.getOficina();
            } else {
                destino = viajeVO.getDestino();
            }
        } else {
            lv = sgViajeroRemote.getTravellersByTravel(viajeVO.getId(), null);
            //
            destino = viajeVO.getOficina();
        }

        return enviarCorreoRemote.enviarCorreoIhsa(traerResponsableSGLySeguridad(), cc, "", "Salida del viaje -" + viajeVO.getCodigo(),
                htmlNotificacionViajeRemote.mensajeSalidaPunto(viajeVO, lv, punto, destino), siParametroRemote.find(Constantes.UNO).getLogo());
    }

    
    public boolean mensajeCambioViajeros(String punto, ViajeVO viajeA, List<ViajeroVO> viajerosViajeA, ViajeVO viajeB, List<ViajeroVO> viajerosViajeB) {
        String cc = correoAnalista(viajeA.getIdOficinaOrigen());
        cc += "," + correoAnalista(viajeB.getIdOficinaOrigen());
        String asunto = "Cambio de viajeros - " + viajeA.getCodigo() + " y " + viajeB.getCodigo();
        return enviarCorreoRemote.enviarCorreoIhsa(traerResponsableSGLySeguridad(), cc, "", asunto,
                htmlNotificacionViajeRemote.mensajeSalidaPunto(asunto, punto, viajeA, viajerosViajeB, viajeB, viajerosViajeA), siParametroRemote.find(Constantes.UNO).getLogo());
    }

    
    public boolean mensajeDirecto(String titulo, String mensaje, String longitud, String latitud) {
        return enviarCorreoRemote.enviarCorreoIhsa(traerResponsableSGLySeguridad(), "", "", titulo,
                htmlNotificacionViajeRemote.mensajeDirecto(titulo, mensaje, longitud, latitud), siParametroRemote.find(Constantes.UNO).getLogo());
    }

    
    public boolean sendMailNotificarGR(List<SolicitudViajeVO> listSolicitudes, String eMails, String fechaSalida, String title) {
        UtilLog4j.log.info(this, "sendMailNotificarGR");

        return enviarCorreoRemote.enviarCorreoIhsa(eMails,
                "",
                "",
                title + fechaSalida,
                htmlNotificacionViajeRemote.bodyMailNotificationByGR(listSolicitudes, fechaSalida),
                siParametroRemote.find(1).getLogo());
    }

    
    public boolean sendMailNotificarGerenteServicios(List<List<ViajeVO>> list, String emails,
            String fecha, String title, List<SolicitudViajeVO> listSol, List<SolicitudViajeVO> listSolSA) {
        UtilLog4j.log.info(this, "sendMailNotificarGerenteServicios");

        return enviarCorreoRemote.enviarCorreoIhsa(emails,
                "",
                "",
                title + fecha,
                htmlNotificacionViajeRemote.bodyMailNotificatioRespnsableSGL(list, fecha, listSol, listSolSA),
                siParametroRemote.find(1).getLogo());
    }
    
    /**
     *
     * @param estatus
     * @param motivo
     * @param usuarioRealizo
     * @param notificar
     * @param solicitudViaje
     * @param responsable
     * @param cancelaViajero
     * @return
     * @throws SIAException
     * @throws Exception
     */
    
    public boolean enviarCorreoSolicitudViajeReactivada(SgEstatusAprobacion estatus, String motivo, Usuario usuarioRealizo, boolean notificar,
            SolicitudViajeVO solicitudViaje, String responsable, boolean cancelaViajero) {
        UtilLog4j.log.info(this, "## ENVIANDO CORREO DE NOTIFICACION DE ACTIVACION DE SOLICITUD........ " + estatus.getUsuario().getNombre());
        String correoCopia = "";
        String correoCopiaViajeros = "";

        
            correoCopia = estatus.getSgSolicitudViaje().getGenero().getEmail();
            correoCopiaViajeros += sgViajeroRemote.correosViajerosPorSolicitud(solicitudViaje.getIdSolicitud());
            
            //sacar viajeros de una solicitud para sacar su email
            correoCopia += !correoCopiaViajeros.equals("") ? ("," + correoCopiaViajeros) : "";
            correoCopia += ","+getResponsableByGerencia(estatus.getSgSolicitudViaje().getGerenciaResponsable().getId()).getEmail();
        

        UtilLog4j.log.info(this, "##########Correo copia viajeros: " + correoCopiaViajeros);

            return enviarCorreoRemote.enviarCorreoIhsa(usuarioRealizo.getEmail(),
                    correoCopia,
                    "",
                    "Se ha Activado la solicitud de viaje : " + estatus.getSgSolicitudViaje().getCodigo() + "",
                    htmlNotificacionViajeRemote.getHtmlSolicitudActivar(solicitudViaje, motivo, usuarioRealizo, estatus.getFechaModifico(), estatus.getHoraModifico(), responsable), siParametroRemote.find(1).getLogo());
    }
}
