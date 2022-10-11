/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.servicios.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.newrelic.api.agent.Trace;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgViaje;
import sia.modelo.Usuario;
import sia.modelo.gr.vo.GrRutaZonasVO;
import sia.modelo.sgl.estancia.vo.DetalleEstanciaVO;
import sia.modelo.sgl.estancia.vo.SgSolicitudEstanciaVo;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.notificaciones.orden.impl.NotificacionOrdenImpl;
import sia.notificaciones.sgl.viaje.Impl.NotificacionViajeImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.gr.impl.GrRutasZonasImpl;
import sia.servicios.sgl.huesped.impl.SiUsuarioTipoImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.servicios.sgl.incidencia.vehiculo.impl.SiIncidenciaImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeSiMovimientoImpl;
import sia.servicios.sgl.viaje.impl.SgViajeVehiculoImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiLocalizacionImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiUsuarioCodigoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless
@LocalBean
@Path("serviciosWebSGyL")
public class ServiciosWebSGyL {

    @Inject
    private SgViajeImpl sgViajeImpl;
    @Inject
    private SgViajeroImpl sgViajeroImpl;
    @Inject
    private NotificacionViajeImpl notificacionViajeImpl;
    @Inject
    private NotificacionOrdenImpl notificacionOrdenImpl;
    @Inject
    private SiUsuarioTipoImpl siUsuarioCopiadoImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SiUsuarioCodigoImpl siUsuarioCodigoImpl;
    @Inject
    private GrRutasZonasImpl grRutasZonasImpl;
    @Inject
    private SgViajeSiMovimientoImpl sgViajeSiMovimientoImpl;
    @Inject
    private SiLocalizacionImpl  siImplizacionImpl;
    @Inject
    private SiIncidenciaImpl siIncidenciaImpl;
    @Inject
    private SgVehiculoImpl sgVehiculoImpl;
    @Inject
    private SgViajeVehiculoImpl sgViajeVehiculoImpl;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaImpl;
    @Inject
    private SgEstatusAprobacionImpl sgEstatusAprobacionImpl;
    //

    @GET
    @Path("/enviarNotificacionViaje")
    @Produces(MediaType.APPLICATION_JSON)
    public void enviarNotificacion() {
        List<ViajeVO> lv = sgViajeImpl.traerViajesTerrestrePorEstatus(Constantes.ESTATUS_VIAJE_POR_SALIR, true);
        notificacionViajeImpl.sendMailNotificarDireccionGral(lv, publicaViaje(7, Constantes.ID_OFICINA_TORRE_MARTEL), Constantes.FMT_TextDateLarge.format(siManejoFechaImpl.fechaSumarDias(new Date(), 1)));
    }
    // Tipo 7:  usuario copiados de direccion general

    private String publicaViaje(int idTipo, int idOficina) {
        String correo = "";
        //
        List<UsuarioTipoVo> luc = siUsuarioCopiadoImpl.getListUser(idTipo, idOficina);

        for (UsuarioTipoVo usuarioCopiadoVo : luc) {
            if (correo.isEmpty()) {
                correo = usuarioCopiadoVo.getCorreo();
            } else {
                correo += "," + usuarioCopiadoVo.getCorreo();
            }
        }
        return correo;
    }

    @GET
    @Path("/buscarViajes/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public String buscarViajes(@PathParam("codigo") String codigo) throws SIAException {
        ViajeVO viajeVO = sgViajeImpl.buscarPorCodigo(codigo);
        JsonObject viaje = new JsonObject();
        if (viajeVO != null && viajeVO.getIdEstatus() == Constantes.ESTATUS_VIAJE_PROCESO) {
            viaje = datosViaje(viajeVO);
        }
        //System.out.println("Viaje :: " + viaje.toString());
        return viaje.toString();

    }

    @POST
    @Path("/salidaViaje")
    @Consumes(MediaType.APPLICATION_JSON)
    public void salidaViaje(InputStream inputStream) {
        String viaje = "";
        try {

            // cambiar la lista de cadena por viajes
            List<ViajeroVO> list = new ArrayList<>();

            JsonObject jsonViaje = converirACadena(inputStream);

            SgViaje sgViaje = sgViajeImpl.findCodigo(jsonViaje.get("viaje").getAsString());
            String idUsuarioEncontrado = siUsuarioCodigoImpl.buscarUsuario(jsonViaje.get("sesion").getAsString());

            Usuario usuario = usuarioImpl.find(idUsuarioEncontrado);
            JsonArray jsonViajeros = jsonViaje.getAsJsonArray("viajeros");
            if (jsonViajeros.size() > 0) {
                for (JsonElement jsonViajero : jsonViajeros) {
                    JsonObject jsonInterno = jsonViajero.getAsJsonObject();
                    ViajeroVO viaVo = new ViajeroVO();
                    viaVo.setTipoViajero(jsonInterno.get("tipoViajero").getAsInt());
                    //
                    if (viaVo.getTipoViajero() == Constantes.EMPLEADO) {
                        viaVo.setIdInvitado(Constantes.CERO);
                        viaVo.setUsuario(jsonInterno.get("nombre").getAsString());
                        viaVo.setTipoViajero(Constantes.EMPLEADO);
                    } else {
                        viaVo.setInvitado(jsonInterno.get("nombre").getAsString());
                        viaVo.setTipoViajero(Constantes.INVITADO);
                    }
                    viaVo.setTelefono(jsonInterno.get("telefono").getAsString());
                    viaVo.setEstancia(Constantes.BOOLEAN_FALSE);
                    viaVo.setAgregado(jsonInterno.get("agregado").getAsBoolean());
                    viaVo.setEstancia(jsonInterno.get("estancia").getAsBoolean());
                    viaVo.setViajo(jsonInterno.get("viajo").getAsBoolean());
                    viaVo.setId(jsonInterno.get("viajeroId").getAsInt());
                    list.add(viaVo);
                }
            }
            viaje = sgViaje.getCodigo();
            sgViajeImpl.exitTrip(usuario, sgViaje, Constantes.ESTATUS_VIAJE_PROCESO, list, false);

        } catch (SIAException ex) {
            String mensaje = "No se completó la acción de salida del viaje " + viaje + ", favor de revisar el sistema de log para mayor detalle de la operación.";
            notificacionOrdenImpl.enviarExcepcionDesarrollo("Intento de salida de viajes -- " + viaje, "Servicios Generales", "Salida de viajes", mensaje);
            Logger.getLogger(ServiciosWebSGyL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @GET
    @Path("/viajesProgramados/{sesion}")
    @Produces(MediaType.APPLICATION_JSON)
    public String viajesProgramados(@PathParam("sesion") String sesion) throws SIAException {
        JsonArray a = new JsonArray();
        List<ViajeVO> lv = sgViajeImpl.traerViajesPorResponsable(sesion, Constantes.ESTATUS_VIAJE_POR_SALIR, Constantes.ESTATUS_VIAJE_PROCESO, true);
        for (ViajeVO viajeVO : lv) {
            a.add(datosViaje(viajeVO));
        }
        return a.toString();
    }

    private JsonObject datosViaje(ViajeVO viajeVO) {
        JsonObject ob = new JsonObject();
        //  System.out.println(" viaje : " + viajeVO.getRuta() + viajeVO.getFechaProgramada());
        ob.addProperty("id", viajeVO.getId());
        ob.addProperty("codigo", viajeVO.getCodigo());
        ob.addProperty("responsable", viajeVO.getResponsable());
        ob.addProperty("ruta", viajeVO.getRuta());
        ob.addProperty("origen", viajeVO.getOficina());
        ob.addProperty("destino", viajeVO.getDestino());
        ob.addProperty("salida", viajeVO.getFechaSalida() != null ? siManejoFechaImpl.convertirFechaStringddMMyyyy(viajeVO.getFechaSalida()) : null);
        ob.addProperty("hora", viajeVO.getHoraSalida() != null ? siManejoFechaImpl.convertirHoraStringHHmmss(viajeVO.getHoraSalida()) : null);
        ob.addProperty("fechaProgramada", siManejoFechaImpl.convertirFechaStringddMMyyyy(viajeVO.getFechaProgramada()));
        ob.addProperty("horaProgramada", siManejoFechaImpl.convertirHoraStringHHmmss(viajeVO.getHoraProgramada()));
        ob.addProperty("idStatus", viajeVO.getIdEstatus());
        ob.addProperty("idOrigen", viajeVO.getIdOficinaOrigen());
        ob.addProperty("idOperacion", viajeVO.getIdOpercion());
        ob.addProperty("idViajeCiudad", viajeVO.getIdSgViajeCiudad());
        ob.addProperty("idViajeIntercambio", viajeVO.getIdOpercionIntercambio());
        JsonArray detalleJson = new JsonArray();
        for (ViajeroVO viajero : viajeVO.getListaViajeros()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("idUsuario", viajero.getIdUsuario());
            obj.addProperty("idInvitado", viajero.getIdInvitado());
            obj.addProperty("empleado", viajero.getUsuario());
            obj.addProperty("invitado", viajero.getInvitado());
            obj.addProperty("telefono", viajero.getTelefono());
            obj.addProperty("estancia", viajero.isEstancia());
            obj.addProperty("idViajero", viajero.getId());
            detalleJson.add(obj);
        }
        ob.addProperty("idViajeVehiculo", viajeVO.getIdViajeVehiculo());
        ob.addProperty("idVehiculo", viajeVO.getVehiculoVO().getId());
        ob.addProperty("marca", viajeVO.getVehiculoVO().getMarca());
        ob.addProperty("modelo", viajeVO.getVehiculoVO().getModelo());
        ob.addProperty("tipo", viajeVO.getVehiculoVO().getTipoEspecifico());
        ob.addProperty("placa", viajeVO.getVehiculoVO().getNumeroPlaca());
        //
        ob.addProperty("listaViajero", detalleJson.toString());
        // Traer el detalle de la rut
        JsonArray rutaJson = new JsonArray();
        List<GrRutaZonasVO> listaZona = grRutasZonasImpl.traerPuntoPorRuta(viajeVO.getIdRuta());
        if (listaZona != null && !listaZona.isEmpty()) {
            for (GrRutaZonasVO rutaZonasVO : listaZona) {
                JsonObject obj = new JsonObject();
                //obj.addProperty("idRutaZona", rutaZonasVO.getId());
                //obj.addProperty("idZona", rutaZonasVO.getZona().getId());
                //obj.addProperty("zona", rutaZonasVO.getZona().getNombre());
                obj.addProperty("idPunto", rutaZonasVO.getPunto().getId());
                obj.addProperty("punto", rutaZonasVO.getPunto().getNombre());
                rutaJson.add(obj);
            }
        }
        ob.addProperty("rutaZona", rutaJson.toString());
        return ob;
    }

    @GET
    @Path("/buscarUsuario/{user}/{clave}")
    @Produces(MediaType.APPLICATION_JSON)
    public String buscarUsuario(@PathParam("user") String user, @PathParam("clave") String clave) {
        Usuario u = usuarioImpl.find(user);
        JsonObject jsonObject = new JsonObject();
        try {
            if (u != null) {
                if (u.getClave().equals(usuarioImpl.encriptar(clave)) && !u.isEliminado()) {
                    jsonObject.addProperty("id", u.getId());
                    jsonObject.addProperty("nombre", u.getNombre());
                    jsonObject.addProperty("correo", u.getEmail());
                    jsonObject.addProperty("nombreCampo", u.getApCampo().getNombre());
                    jsonObject.addProperty("telefono", u.getTelefono());
                    String generaCod = siUsuarioCodigoImpl.generaCodigo(user);
                    if (siUsuarioCodigoImpl.validaToken(generaCod)) {
                        jsonObject.addProperty("codigo", siUsuarioCodigoImpl.guardar(u.getId()));
                    } else {
                        jsonObject.addProperty("codigo", generaCod);
                    }
                }
            }
        } catch (NoSuchAlgorithmException ex) {
            UtilLog4j.log.fatal(this, "Ocurrio un error : : : " + ex.getMessage());
        }

        return jsonObject.toString();
    }

    @GET
    @Path("/validaToken/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public String validaToken(@PathParam("codigo") String codigo) {

        JsonObject jsonToken = new JsonObject();
        if (siUsuarioCodigoImpl.validaToken(codigo)) {
            jsonToken.addProperty("codigo", codigo);
        } else {
            jsonToken.addProperty("codigo", "");
        }
        return jsonToken.toString();

    }

    @GET
    @Path("/buscarEmpleado/{cadena}")
    @Produces(MediaType.APPLICATION_JSON)
    public String buscarEmpleado(@PathParam("cadena") String cadena) {
        //System.out.println("cadena : : : " + cadena);
        JsonArray a = new JsonArray();
        List<Object[]> lv = usuarioImpl.buscarUsuarioLetras(cadena, Constantes.TOTAL_FILAS_RECUPERADAS);
        for (Object[] object : lv) {
            JsonObject ob = new JsonObject();
            ob.addProperty("id", (String) object[0]);
            ob.addProperty("nombre", (String) object[1]);
            ob.addProperty("telefono", (String) object[2]);
            a.add(ob);
        }
        return a.toString();
    }

    @POST
    @Path("/finalizarViaje")
    @Consumes(MediaType.APPLICATION_JSON)
    public String finalizarViaje(InputStream inputStream) {
        /*
        /{idViaje}/{usuarioSesion}/{llegoA}/{punto}
            @PathParam("idViaje") String idViaje, @PathParam("usuarioSesion") String usuarioSesion,
            @PathParam("llegoA") String llegoA, @PathParam("punto"
            ) String punto
         */
        JsonObject jsonViaje = converirACadena(inputStream);
        switch (jsonViaje.get("llegoEn").getAsInt()) {
            case Constantes.ORIGEN:
                sgViajeImpl.finalizarViaje(jsonViaje.get("viajeId").getAsInt(), jsonViaje.get("sesion").getAsString());
                break;
            case Constantes.DESTINO:
                ViajeVO viajeVO = sgViajeImpl.buscarPorId(jsonViaje.get("viajeId").getAsInt(), false);
                /**
                 * El viaje es sencillo y va a una oficina
                 */
                switch (viajeVO.getTipoRuta()) {
                    case Constantes.RUTA_TIPO_OFICINA:
                        if (viajeVO.isRedondo()) {
                            sgViajeImpl.finalizarViaje(jsonViaje.get("viajeId").getAsInt(), jsonViaje.get("sesion").getAsString());
                        } else {
                            sgViajeImpl.cambiarEstado(jsonViaje.get("viajeId").getAsInt(), jsonViaje.get("sesion").getAsString(), Constantes.ESTATUS_VIAJE_EN_DESTINO);
                        }
                        break;
                    case Constantes.RUTA_TIPO_CIUDAD:
                        sgViajeSiMovimientoImpl.guardarViajeMovimiento(jsonViaje.get("sesion").getAsString(), jsonViaje.get("viajeId").getAsInt(), Constantes.ID_SI_OP_LLEGO_VIAJE, "Viaje en punto de seguridad o destino");
                        break;
                }
                break;
            case Constantes.PUNTO_SEGURIDAD:
                //System.out.println("Integer.parseInt(\"idViaje\") " + Integer.parseInt(idViaje));
                sgViajeSiMovimientoImpl.guardarViajeMovimiento(jsonViaje.get("sesion").getAsString(), jsonViaje.get("viajeId").getAsInt(), Constantes.ID_SI_OP_LLEGO_VIAJE, "Viaje en punto de seguridad o destino");
                break;
        }
        sgViajeImpl.mensajeLlegada(jsonViaje.get("viajeId").getAsInt(), jsonViaje.get("sesion").getAsString(), jsonViaje.get("llegoEn").getAsInt(), jsonViaje.get("punto").getAsString());
        return "";
    }

    @GET
    @Path("/pasarViajeros/{idViajeA}/{idViajeB}/{idPunto}/{punto}/{idOficinaOrigenViajeA}/{idOficinaOrigenViajeB}/{oficinaOrigenViajeA}/{oficinaOrigenViajeB}/{usuarioSesion}")
    @Produces(MediaType.APPLICATION_JSON)
    public String pasarViajeros(@PathParam("idViajeA") String idViajeA,
            @PathParam("idViajeB") String idViajeB,
            @PathParam("idPunto") String idPunto,
            @PathParam("punto") String punto,
            @PathParam("idOficinaOrigenViajeA") String idOficinaOrigenViajeA,
            @PathParam("idOficinaOrigenViajeB") String idOficinaOrigenViajeB,
            @PathParam("oficinaOrigenViajeA") String oficinaOrigenViajeA,
            @PathParam("oficinaOrigenViajeB") String oficinaOrigenViajeB,
            @PathParam("usuarioSesion") String usuarioSesion) {

        sgViajeImpl.pasarViajeros(Integer.parseInt(idViajeA), Integer.parseInt(idViajeB), Integer.parseInt(idPunto), punto, Integer.parseInt(idOficinaOrigenViajeA), Integer.parseInt(idOficinaOrigenViajeB), oficinaOrigenViajeA, oficinaOrigenViajeB, usuarioSesion);
        return "";
    }

    @POST
    @Path("/mensajeSalidaPunto")
    @Consumes(MediaType.APPLICATION_JSON)
    public String mensajeSalidaPnunto(InputStream datosViaje) {
        JsonObject json = converirACadena(datosViaje);
        System.out.println("Viaje: " + Integer.parseInt(json.get("viajeId").toString()) + " Sesion" + json.get("sesion").toString() + " - Tipo intercambio- - - " + Integer.parseInt(json.get("intercambio").toString()) + " -Lugar-- " + json.get("punto").toString());
        sgViajeImpl.mensajeSalidaPunto(Integer.parseInt(json.get("viajeId").toString()), json.get("sesion").toString(), Integer.parseInt(json.get("intercambio").toString()), json.get("punto").toString());
        return "";
    }

    @POST
    @Path("/guardarImplizacion")
    @Consumes(MediaType.APPLICATION_JSON)
    public void guardarImplizacion(InputStream inputStream) {
        // 

        siImplizacionImpl.guardar(Constantes.USUARIO_SIA, converirACadena(inputStream));

    }

    @POST
    @Path("/enviarMensaje")
    @Consumes(MediaType.APPLICATION_JSON)
    public void enviarMensaje(InputStream inputStream) {
        try {
            JsonObject jsonObject = converirACadena(inputStream);
            siIncidenciaImpl.guardarIncidenciaLocalizacion(jsonObject, jsonObject.get("sesion").getAsString());
        } catch (Exception ex) {
            Logger.getLogger(ServiciosWebSGyL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @GET
    @Path("/buscarVehiculoPorPlaca/{placa}")
    @Produces(MediaType.APPLICATION_JSON)
    public String buscarVehiculoPorPlaca(@PathParam("placa") String placa) {
        //System.out.println("cadena : : : " + cadena);
        VehiculoVO vehiculoVO = sgVehiculoImpl.buscarPorPlaca(placa);
        JsonObject ob = new JsonObject();
        if (vehiculoVO != null) {
            ob.addProperty("id", vehiculoVO.getId());
            ob.addProperty("placa", (String) vehiculoVO.getNumeroPlaca());
            ob.addProperty("marca", (String) vehiculoVO.getMarca());
            ob.addProperty("modelo", (String) vehiculoVO.getModelo());
            ob.addProperty("tipo", (String) vehiculoVO.getTipoEspecifico());
        }

        //
        return ob.toString();
    }

    @POST
    @Path("/cambiarVhiculo")
    @Consumes(MediaType.APPLICATION_JSON)
    public void cambiarVhiculo(InputStream inputStream) {
        /*
        @PathParam("idViajeVehiculo") String idViajeVehiculo,
            @PathParam("vehiculo") String vehiculo,
            @PathParam("sesion") String sesion
        /{idViajeVehiculo}/{vehiculo}/{sesion}
         */
        JsonObject jsonCambVehi = converirACadena(inputStream);
        sgViajeVehiculoImpl.actualizarVehiculo(jsonCambVehi.get("sesion").getAsString(), jsonCambVehi.get("vehiculoViajeId").getAsInt(), jsonCambVehi.get("vehiculoId").getAsInt());
        //
    }

    @GET
    @Path("/agregarViajeroViaje/{idViaje}/{idSolViaje}/{idViajero}/{sesion}")
    @Produces(MediaType.APPLICATION_JSON)
    public String agregarViajeroViaje(@PathParam("idViaje") String idViaje,
            @PathParam("idSolViaje") String idSolViaje,
            @PathParam("idViajero") String idViajero,
            @PathParam("sesion") String sesion) {
        try {
            return String.valueOf(sgViajeroImpl.agregarViaje(sesion,
                    Integer.parseInt(idViaje),
                    Integer.parseInt(idSolViaje),
                    Integer.parseInt(idViajero),
                    Constantes.FALSE));
        } catch (Exception ex) {
            UtilLog4j.log.fatal(ex);
            return Constantes.VACIO;
        }
    }

    @GET
    @Path("/moverViajeAProgramado/{idViaje}/{sesion}")
    @Produces(MediaType.APPLICATION_JSON)
    public String moverViajeAProgramado(@PathParam("idViaje") String idViaje,
            @PathParam("sesion") String sesion) {
        boolean ret = Constantes.BOOLEAN_FALSE;
        try {
            if (sgViajeImpl.moverViajeAProgramado(sesion, Integer.parseInt(idViaje))) {
                ret = Constantes.BOOLEAN_TRUE;
            } else {
                ret = Constantes.BOOLEAN_FALSE;
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(ex);
            ret = Constantes.BOOLEAN_FALSE;
        }
        return ret ? "ok" : "Fechas";
    }

    @GET
    @Path("/agregarViajeroViaje/{idViaje}/{idSolViaje}/{idViajero}/{sesion}/{escala}")
    @Produces(MediaType.APPLICATION_JSON)
    public String agregarViajeroViaje(@PathParam("idViaje") String idViaje,
            @PathParam("idSolViaje") String idSolViaje,
            @PathParam("idViajero") String idViajero,
            @PathParam("sesion") String sesion,
            @PathParam("escala") String escala) {
        try {
            return String.valueOf(sgViajeroImpl.agregarViaje(sesion,
                    Integer.parseInt(idViaje),
                    Integer.parseInt(idSolViaje),
                    Integer.parseInt(idViajero),
                    Boolean.parseBoolean(escala)));
        } catch (Exception ex) {
            UtilLog4j.log.fatal(ex);
            return "Error";
        }
    }

    @GET
    @Path("/totalViajePorAprobar/{sesion}")
    @Produces(MediaType.APPLICATION_JSON)
    public String totalSolViajePorAprobar(@PathParam("sesion") String sesion) {
        //System.out.println("session : : : " + sesion);
        //
        JsonObject jsonCampo = new JsonObject();
        try {
            long pendiente = sgEstatusAprobacionImpl.totalPendiente(sesion);
            jsonCampo.addProperty("pendiente", pendiente);
        } catch (Exception e) {
            System.out.println("excg    +  e                " + e.getMessage());
        }
        //System.out.println("Pendiente : : : " + jsonObject.toString());
        return jsonCampo.toString();
    }

    @GET
    @Path("/totalEstanciaPorAprobar/{sesion}")
    @Produces(MediaType.APPLICATION_JSON)
    public String totalEstanciaPorAprobar(@PathParam("sesion") String sesion) {
        //System.out.println("session : : : " + sesion);
        UsuarioVO usuarioVO = usuarioImpl.findById(sesion);
        //
        JsonObject jsonCampo = new JsonObject();
        try {
            long pendiene = sgSolicitudEstanciaImpl.totalSolicituesPorAprobar(usuarioVO.getId());
            jsonCampo.addProperty("pendiente", pendiene);
        } catch (Exception e) {
            System.out.println("excg    +  e                " + e.getMessage());
        }
        return jsonCampo.toString();
    }

    @GET
    @Path("/solicitudViajePorAprobar/{sesion}")
    @Produces(MediaType.APPLICATION_JSON)
    public String solViajePorAprobar(@PathParam("sesion") String sesion) throws SIAException {
        JsonArray a = new JsonArray();
        List<SolicitudViajeVO> lista = sgEstatusAprobacionImpl.totalSolicitudViaje(sesion);
        for (SolicitudViajeVO eavo : lista) {
            JsonObject jo = new JsonObject();
            //System.out.println(" codigo  "+eavo.getCodigo()+" tipo "+eavo.getIdSgTipoSolicitudViaje());
            jo.addProperty("idEstatus", eavo.getIdEstatusAprobacion());
            jo.addProperty("codigo", eavo.getCodigo());
            jo.addProperty("origen", eavo.getOrigen());
            jo.addProperty("detino", eavo.getDestino());
            jo.addProperty("fechaSalida", Constantes.FMT_ddMMyyy.format(eavo.getFechaSalida()));
            jo.addProperty("horaSalida", Constantes.FMT_HHmmss.format(eavo.getHoraSalida()));
            jo.addProperty("motivo", eavo.getMotivo());
            jo.addProperty("tipo", eavo.getIdSgTipoSolicitudViaje());
            jo.addProperty("motivoRetraso", eavo.getJustificacionRetraso());
            jo.addProperty("fRegreso", eavo.getFechaRegreso() != null ? Constantes.FMT_ddMMyyy.format(eavo.getFechaRegreso()) : Constantes.VACIO);
            jo.addProperty("hRegreso", eavo.getHoraRegreso() != null ? Constantes.FMT_HHmmss.format(eavo.getHoraRegreso()) : Constantes.VACIO);
            jo.addProperty("redondo", eavo.getFechaRegreso() != null ? Constantes.TRUE : Constantes.FALSE);
            jo.addProperty("nombreGenero", eavo.getNombreGenero());
            
            JsonArray detalleJson = new JsonArray();
            for (ViajeroVO detalle : eavo.getViajeros()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("nombre", detalle.getIdInvitado() == 0 ? detalle.getUsuario() : detalle.getInvitado());
                obj.addProperty("tipo", detalle.getIdInvitado() == 0 ? "Empleado" : "Invitado");
                obj.addProperty("estancia", detalle.isEstancia());
                detalleJson.add(obj);
            }
            jo.addProperty("detalle", detalleJson.toString());
            a.add(jo);

        }
        return a.toString();
    }

    @GET
    @Path("/solicitudEstanciaPorAprobar/{sesion}")
    @Produces(MediaType.APPLICATION_JSON)
    public String solEstanciaPorAprobar(@PathParam("sesion") String sesion) throws SIAException {
        JsonArray a = new JsonArray();
        List<SgSolicitudEstanciaVo> lista = sgSolicitudEstanciaImpl.solicituesPorAprobar(sesion);
        for (SgSolicitudEstanciaVo eavo : lista) {
            JsonObject jo = new JsonObject();
            jo.addProperty("id", eavo.getId());
            jo.addProperty("codigo", eavo.getCodigo());
            jo.addProperty("ingreso", Constantes.FMT_ddMMyyy.format(eavo.getInicioEstancia()));
            jo.addProperty("salida", Constantes.FMT_ddMMyyy.format(eavo.getFinEstancia()));
            jo.addProperty("oficina", eavo.getNombreSgOficina());
            jo.addProperty("dias", eavo.getDiasEstancia());
            jo.addProperty("nombreGenero", eavo.getNombreGenero());
            JsonArray detalleJson = new JsonArray();
            for (DetalleEstanciaVO detalle : eavo.getDetalle()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("nombre", detalle.getIdInvitado() == 0 ? detalle.getUsuario() : detalle.getInvitado());
                obj.addProperty("tipo", detalle.getIdInvitado() == 0 ? "Empleado" : "Invitado");
                detalleJson.add(obj);
            }
            //
            jo.addProperty("listaDetalle", detalleJson.toString());
            a.add(jo);
        }
        return a.toString();
    }

    @POST
    @Path("/aprobarSolicitudViaje")
    @Consumes(MediaType.APPLICATION_JSON)
    @Trace(dispatcher = true)
    public void aprobarSolViaje(InputStream inputStream) throws SIAException {
        ///{sesion}/{listaSolViaje}
        //@PathParam("sesion") String sesion, @PathParam("listaSolViaje") String listaol
        try {

            JsonObject jsonSolViaje = converirACadena(inputStream);
            System.out.println("cad; " + jsonSolViaje);
            JsonArray jsonSolicitudes = jsonSolViaje.get("solicitudes").getAsJsonArray();
            for (JsonElement jsonSolicitude : jsonSolicitudes) {
                JsonObject sols = jsonSolicitude.getAsJsonObject();
                if (sols.get("motivoRetraso").getAsString().isEmpty()) {
                    sgEstatusAprobacionImpl.aprobarSolicitud(sols.get("solicitudStatusId").getAsInt(), jsonSolViaje.get("sesion").getAsString());
                } else {
                    sgEstatusAprobacionImpl.aprobarJustificandoSolicitud(sols.get("solicitudStatusId").getAsInt(), sgEstatusAprobacionImpl.find(sols.get("solicitudStatusId").getAsInt()).getSgSolicitudViaje().getId(), sols.get("justificacion").getAsString(), jsonSolViaje.get("sesion").getAsString());
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.warn(e);
        }
    }

    @POST
    @Path("/devolverSolicitudViaje")
    @Consumes(MediaType.APPLICATION_JSON)
    public void devolverSolViaje(InputStream inputStream) throws SIAException {
        try {
            //
            JsonObject jsonObject = converirACadena(inputStream);
            JsonArray ja = jsonObject.getAsJsonArray("solicitudes");
            for (JsonElement jsonElement : ja) {
                JsonObject jo = (JsonObject) jsonElement;
                sgEstatusAprobacionImpl.cancelarSolicitud(jo.get("solicitudStatusId").getAsInt(), jsonObject.get("motivo").getAsString(), jsonObject.get("sesion").getAsString(), Constantes.FALSE, Constantes.FALSE, Constantes.FALSE);
            }
        } catch (Exception e) {
            UtilLog4j.log.warn(e);
        }
    }

    @GET
    @Path("/aprobarSolicitudEstancia/{sesion}/{listaSolEstancia}")
    @Produces(MediaType.APPLICATION_JSON)
    public String aprobarSolEstancia(@PathParam("sesion") String sesion, @PathParam("listaSolEstancia") String listaEstancia) throws SIAException {
        try {
            String[] cadListaSolViaje = listaEstancia.split("IHSA");
            for (String cadListaSolViaje1 : cadListaSolViaje) {
                sgSolicitudEstanciaImpl.aprobarEstancia(sesion, Integer.parseInt(cadListaSolViaje1));
            }
            return "Ok";
        } catch (NumberFormatException e) {
            UtilLog4j.log.warn(e);
            return "Error";
        }
    }

    @GET
    @Path("/devolverSolicitudEstancia/{sesion}/{listaSolEstancia}/{motivo}")
    @Produces(MediaType.APPLICATION_JSON)
    public String devolverSolEstancia(@PathParam("sesion") String sesion, @PathParam("listaSolEstancia") String listaEstancia, @PathParam("motivo") String motivo) throws SIAException {
        try {
            String[] cadListaSolEstancia = listaEstancia.split("IHSA");
            for (String cadIdsSolEstancia : cadListaSolEstancia) {
                sgSolicitudEstanciaImpl.cancelarSolicitudEstanciaAntesSolicitar(sesion, Integer.parseInt(cadIdsSolEstancia), motivo);
            }
            return "Ok";
        } catch (Exception e) {
            UtilLog4j.log.warn(e);
            return "Error";
        }
    }

    @GET
    @Path("/totalPendiente/{sesion}")
    @Produces(MediaType.APPLICATION_JSON)
    public String totalPendiente(@PathParam("sesion") String sesion) throws SIAException {
        //System.out.println("session : : : " + sesion);
        //
        JsonObject jsonCampo = new JsonObject();
        try {
            jsonCampo.addProperty("pendienteSolViaje", sgEstatusAprobacionImpl.totalPendiente(sesion));
            //
            jsonCampo.addProperty("pendienteSolEst", sgSolicitudEstanciaImpl.totalSolicituesPorAprobar(sesion));

        } catch (Exception e) {
            System.out.println("excg    +  e                " + e.getMessage());
        }
        return jsonCampo.toString();
    }

    private JsonObject converirACadena(InputStream inputStream) {
        StringBuilder cadena = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = null;
            while ((line = in.readLine()) != null) {
                cadena.append(line);
            }
            //System.out.println("Data Received: " + cadena.toString());
            JsonParser parser = new JsonParser();
            JsonElement jsonTree = parser.parse(cadena.toString());
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            //System.out.println("Json : " + jsonObject.toString());
            return jsonObject;
        } catch (IOException e) {
            UtilLog4j.log.error(e);
            //System.out.println("Error Parsing: - " + e);
        }
        return null;
    }
}
