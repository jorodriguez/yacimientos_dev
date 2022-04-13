
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.we.servicio;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.AutorizacionesOrden;
import sia.modelo.Orden;
import sia.modelo.Requisicion;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.orden.vo.OrdenEtsVo;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.sistema.impl.SiUsuarioCodigoImpl;
import sia.util.UtilLog4j;
import static sia.compra.we.servicio.UtilsApi.*;
import sia.constantes.TipoRequisicion;
import sia.modelo.requisicion.vo.RequisicionEtsVo;
import sia.modelo.vo.RespuestaVo;
import sia.notificaciones.orden.impl.NotificacionOrdenImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OcOrdenEtsImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.requisicion.impl.ReRequisicionEtsImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;

/**
 * REST Web Service
 *
 * @author ihsa
 */
@Stateless
@LocalBean
@Path("serviciosWebCompras")
public class ServiciosCompras implements Serializable {

    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenImpl;
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private EstatusImpl estatusImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private SiUsuarioCodigoImpl siUsuarioCodigoImpl;
    @Inject
    private NotificacionOrdenImpl notificacionOrdenImpl;
    @Inject
    private RequisicionImpl requisicionImpl;
    @Inject
    private SgEstatusAprobacionImpl sgEstatusAprobacionImpl;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaImpl;
    @Inject
    private OcOrdenEtsImpl ocOrdenEtsImpl;
    @Inject
    private RequisicionImpl requisicionServicioRemoto;

    @Inject
    private ReRequisicionEtsImpl reRequisicionEtsImpl;

    final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
    final SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm");

    private enum OperacionRequisicion {
        REVISAR, APROBAR, DEVOLVER, CANCELAR, VISTO_BUENO_CONTABILIDAD, ASIGNAR
    };

    private final static UtilLog4j LOGGER = UtilLog4j.log;

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
                    jsonObject.addProperty("campo", u.getApCampo().getId());
                    jsonObject.addProperty("correo", u.getEmail());
                    jsonObject.addProperty("nombreCampo", u.getApCampo().getNombre());
                    jsonObject.addProperty("telefono", u.getTelefono());
                    jsonObject.addProperty("puesto", apCampoUsuarioRhPuestoImpl.getPuestoPorUsurioCampo(u.getId(), u.getApCampo().getId()));
                    //
//		    String generaCod = siUsuarioCodigoImpl.generaCodigo(u.getId());
//		    if (siUsuarioCodigoImpl.validaToken(generaCod)) {
//			jsonObject.addProperty("codigo", siUsuarioCodigoImpl.guardar(u.getId()));
//		    } else {
//			jsonObject.addProperty("codigo", generaCod);
//		    }
                }
            }
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.fatal(this, "Ocurrio un error : : : " + ex.getMessage());
        }
        return jsonObject.toString();
    }

    /**
     * Retrieves representation of an instance of
     * sia.compra.we.servicio.ServiciosCompras
     *
     * @param sesion
     * @param campo
     * @return an instance of java.lang.String
     * @throws sia.excepciones.SIAException
     */
    @GET
    @Path("/traerOrdenesPorAprobar/{sesion}/{campo}")
    @Produces(MediaType.APPLICATION_JSON)
    public String ordenesPorAprobar(@PathParam("sesion") String sesion, @PathParam("campo") int campo) throws SIAException {

        List<OrdenVO> listaOrden;
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // autorizacionesOrdenImpl.traerOrdenPorStatusUsuario(campo, estado, sesion);
        //List<StatusVO> lista = estatusImpl.traerPorTipo(Constantes.ESTATUS_COMPROBANTE_ORDEN);

        List<Integer> lista = Arrays.asList(
                Constantes.ORDENES_SIN_APROBAR,//110
                Constantes.ORDENES_SIN_AUTORIZAR_MPG,//120
                Constantes.ESTATUS_REVISA_SOCIO, //125
                Constantes.ESTATUS_REVISADA, //130
                Constantes.ESTATUS_POR_APROBAR_SOCIO,//135
                Constantes.ESTATUS_APROBADA, //140
                Constantes.ESTATUS_AUTORIZADA, //150
                Constantes.ORDENES_SIN_AUTORIZAR_LICITACION // 151
        );

        JsonArray a = new JsonArray();
        for (Integer idEstatus : lista) {
            //System.out.println("=====> id estatus Orden "+idEstatus);
            //if (statusVO.getIdStatus() >= Constantes.ORDENES_SIN_SOLICITAR && statusVO.getIdStatus() < Constantes.ESTATUS_ENVIADA_PROVEEDOR) {            
            //if (statusVO.getIdStatus() >= Constantes.ORDENES_SIN_AUTORIZAR_MPG && statusVO.getIdStatus() <= Constantes.ORDENES_SIN_AUTORIZAR_LICITACION ) {
            listaOrden = autorizacionesOrdenImpl.traerOrdenPorStatusUsuario(campo, idEstatus, sesion, sesion);
            if (listaOrden != null && !listaOrden.isEmpty()) {

                for (OrdenVO ordenVO : listaOrden) {
                    JsonObject ob = new JsonObject();
                    ob.addProperty("idOrden", ordenVO.getId());
                    ob.addProperty("idEstatus", ordenVO.getIdStatus());
                    ob.addProperty("estatus", ordenVO.getEstatus());
                    ob.addProperty("fecha", format.format(ordenVO.getFecha()));
                    ob.addProperty("ccontrato", ordenVO.getContratoVO() != null ? ordenVO.getContratoVO().getCodigo() : null);
                    ob.addProperty("consecutivo", ordenVO.getConsecutivo());
                    ob.addProperty("referencia", ordenVO.getReferencia());
                    ob.addProperty("subtotal", ordenVO.getSubTotal());
                    ob.addProperty("total", ordenVO.getTotal());
                    ob.addProperty("iva", ordenVO.getIva());
                    ob.addProperty("moneda", ordenVO.getMonedaSiglas());
                    ob.addProperty("estado", ordenVO.getEstatus());
                    ob.addProperty("proveedor", ordenVO.getProveedor());
                    ob.addProperty("compania", ordenVO.getCompania());
                    ob.addProperty("requisicion", ordenVO.getRequisicion());
                    ob.addProperty("analista", ordenVO.getAnalista());
                    ob.addProperty("gerencia", ordenVO.getGerencia());
                    ob.addProperty("idBloque", ordenVO.getIdBloque());
                    ob.addProperty("bloque", ordenVO.getBloque());
                    ob.addProperty("proyecto", ordenVO.getNombreProyectoOT());
                    a.add(ob);
                }
            }
            //}
        }
//	System.out.println("regres a las ocs : : :" + a);
        return a.toString();
    }

    @GET
    @Path("/buscarOrdenPorConsecutivo/{consecutivo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarOrdenPorConsecutivo(@PathParam("consecutivo") String consecutivo) throws SIAException {
        //OrdenVO orden = ordenImpl.buscarOrdenPorConsecutivoEmpresa(consecutivo, true);

        try {
            OrdenVO orden = ordenImpl.buscarOrdenPorConsecutivo(consecutivo, true);
            LOGGER.info(this,"orden encontrada");
            /*GenericEntity entity = new GenericEntity<OrdenVO>(orden){};
           return Response.ok(entity,MediaType.APPLICATION_JSON).build();*/
            JsonObject jsonOrden = new JsonObject();
            jsonOrden.addProperty("id", orden.getId());
            jsonOrden.addProperty("consecutivo", orden.getConsecutivo());
            jsonOrden.addProperty("estado", orden.getEstatus());
            jsonOrden.addProperty("idEstado", orden.getIdStatus());
            jsonOrden.addProperty("proveedor", orden.getProveedor());
            jsonOrden.addProperty("referencia", orden.getReferencia());
            jsonOrden.addProperty("gerencia", orden.getGerencia());
            jsonOrden.addProperty("proyecto", orden.getNombreProyectoOT());
            jsonOrden.addProperty("destino", orden.getDestino());
            jsonOrden.addProperty("analista", orden.getAnalista());
            jsonOrden.addProperty("subtotal", orden.getSubTotal());
            jsonOrden.addProperty("iva", orden.getIva());
            jsonOrden.addProperty("total", orden.getTotal());
            jsonOrden.addProperty("moneda", orden.getMoneda());
            jsonOrden.addProperty("contrato", orden.getContratoVO().getNumero());
            jsonOrden.addProperty("fecha", formatDate.format(orden.getFecha()));

            jsonOrden.addProperty("tipo", orden.getTipo());
            jsonOrden.addProperty("compania", orden.getCompania());
            jsonOrden.addProperty("fechaEntrega", formatDate.format(orden.getFechaEntrega()));
            jsonOrden.addProperty("cantidadTotal", orden.getTotalItems());
            jsonOrden.addProperty("unidad", orden.getUnidad());
            jsonOrden.addProperty("url", orden.getUrl());
            jsonOrden.addProperty("responsable", orden.getResponsableGerencia());
            jsonOrden.addProperty("ets", orden.getListaETS().size());
            jsonOrden.addProperty("cfdi", orden.getNombreCfdi());
            jsonOrden.addProperty("terminoPago", orden.getTerminoPago());
            jsonOrden.addProperty("repse", orden.isRepse());
            jsonOrden.addProperty("consecutivo_requisicion", orden.getRequisicion());
            //jsonOrden.addProperty("nota", orden.getNota());
            //jsonOrden.addProperty("observaciones", orden.getObservaciones());
            //a.add(jsonOrden);
            //
            JsonArray detalleJson = new JsonArray();
            for (OrdenDetalleVO ordenDetalleVO : orden.getDetalleOrden()) {
                JsonObject ob = new JsonObject();
                ob.addProperty("id", ordenDetalleVO.getId());
                ob.addProperty("descripcion", ordenDetalleVO.getArtDescripcion());
                ob.addProperty("cantidad", ordenDetalleVO.getCantidad());
                ob.addProperty("precio", ordenDetalleVO.getPrecioUnitario());
                ob.addProperty("importe", ordenDetalleVO.getImporte());
                detalleJson.add(ob);
            }
            jsonOrden.addProperty("listaDetalle", detalleJson.toString());

            //Carga  ets
             LOGGER.info(this,"buscar ets ");
            JsonArray etsJson = new JsonArray();
            try {
                List<OrdenEtsVo> ets = ocOrdenEtsImpl.traerEtsPorOrdenCategoria(orden.getId());
                   LOGGER.info(this,"ets encontrados");
                for (OrdenEtsVo item : ets) {
                    JsonObject ob = new JsonObject();
                    ob.addProperty("id", item.getId());
                    ob.addProperty("nombre", item.getNombre());
                    ob.addProperty("idEtsOrden", item.getIdEtsOrden());
                    ob.addProperty("descripcion", item.getDescripcion());
                    ob.addProperty("url", item.getUrl());
                    ob.addProperty("tipoArchivo", item.getTipoArchivo());
                    ob.addProperty("peso", item.getPeso());
                    ob.addProperty("categoria", item.getCategoria());
                    ob.addProperty("uuid", item.getUuid());
                    //ob.addProperty("idTabla", item.getIdTabla());
                    etsJson.add(ob);
                }
            } catch (Exception ex) {
                LOGGER.fatal(this, "Ocurio un error al buscar los ets " + consecutivo + " ERROR  : :: :: : " + ex.getMessage());
            }
            jsonOrden.addProperty("ets", etsJson.toString());
            

            //Carga de tabla comparativa
            LOGGER.info(this,"buscar comparativa encontrada");
            JsonArray tablaComparativaJson = new JsonArray();
            try {                
                List<OrdenEtsVo> tablaComparativa = ocOrdenEtsImpl.traerEtsPorOrdenCategoria(orden.getId(), Constantes.OCS_CATEGORIA_TABLA);
                LOGGER.info(this,"tabla comparativa encontrada");
                for (OrdenEtsVo item : tablaComparativa) {
                    JsonObject ob = new JsonObject();
                    ob.addProperty("id", item.getId());
                    ob.addProperty("nombre", item.getNombre());
                    ob.addProperty("idEtsOrden", item.getIdEtsOrden());
                    ob.addProperty("idRelacion", item.getIdRelacion());
                    ob.addProperty("descripcion", item.getDescripcion());
                    ob.addProperty("url", item.getUrl());
                    ob.addProperty("tipoArchivo", item.getTipoArchivo());
                    ob.addProperty("peso", item.getPeso());
                    ob.addProperty("categoria", item.getCategoria());
                    ob.addProperty("uuid", item.getUuid());
                    ob.addProperty("idTabla", item.getIdTabla());
                    tablaComparativaJson.add(ob);
                }
            } catch (Exception ex) {
                LOGGER.fatal(this, "Ocurio un error al buscar la tabla comparativa " + consecutivo + " ERROR  : :: :: : " + ex.getMessage());
            }
            jsonOrden.addProperty("tabla_comparativa", tablaComparativaJson.toString());

            return buildAcceptedResponse(jsonOrden);
        } catch (Exception e) {
            LOGGER.fatal(this, "Ocurio un error al buscar la orden " + consecutivo + " ERROR  : :: :: : " + e.getMessage());
            return buildBadResponse("Error " + e.getMessage());
        }
    }

    @GET
    @Path("/obtenerAutorizacionesOrdenId/{consecutivo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerAutorizacionesOrdenId(@PathParam("consecutivo") String consecutivo) throws SIAException {
        LOGGER.info(this, "obtenerAutorizacionesOrdenId " + consecutivo);
        try {

            Response respuesta;
            //final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //final SimpleDateFormat formatHora = new SimpleDateFormat("hh:mm");

            final OrdenVO orden = ordenImpl.buscarOrdenPorConsecutivo(consecutivo, false);
            LOGGER.info(this, "Orden encontrada " + orden.getId());
            if (orden == null) {
                respuesta = buildBadResponse("No se encuentra la orden");
            } else {

                final AutorizacionesOrden aOrden = autorizacionesOrdenImpl.buscarPorOrden(orden.getId());

                final JsonObject ob = new JsonObject();
                ob.addProperty("solicito", aOrden.getSolicito() != null ? aOrden.getSolicito().getNombre() : "");
                ob.addProperty("fecha_solicito", aOrden.getFechaSolicito() != null ? formatDate.format(aOrden.getFechaSolicito()) : "");
                ob.addProperty("hora_solicito", aOrden.getHoraSolicito() != null ? formatTime.format(aOrden.getHoraSolicito()) : "");
                ob.addProperty("solicito_auto", false);
                //visto bueno                
                ob.addProperty("visto_bueno", aOrden.getAutorizaGerencia() != null ? aOrden.getAutorizaGerencia().getNombre() : "");
                ob.addProperty("fecha_visto_bueno", aOrden.getFechaAutorizoGerencia() != null ? formatDate.format(aOrden.getFechaAutorizoGerencia()) : "");
                ob.addProperty("hora_visto_bueno", aOrden.getHoraAutorizoGerencia() != null ? formatTime.format(aOrden.getHoraAutorizoGerencia()) : "");
                ob.addProperty("visto_bueno_auto", aOrden.isAutorizacionGerenciaAuto());
                //revisada                
                ob.addProperty("revisada", aOrden.getAutorizaMpg() != null ? aOrden.getAutorizaMpg().getNombre() : "");
                ob.addProperty("fecha_revisada", aOrden.getFechaAutorizoMpg() != null ? formatDate.format(aOrden.getFechaAutorizoMpg()) : "");
                ob.addProperty("hora_revisada", aOrden.getHoraAutorizoMpg() != null ? formatTime.format(aOrden.getHoraAutorizoMpg()) : "");
                ob.addProperty("revisada_auto", aOrden.isAutorizacionMpgAuto());
                //aprobada
                ob.addProperty("aprobada", aOrden.getAutorizaIhsa() != null ? aOrden.getAutorizaIhsa().getNombre() : "");
                ob.addProperty("fecha_aprobada", aOrden.getFechaAutorizoIhsa() != null ? formatDate.format(aOrden.getFechaAutorizoIhsa()) : "");
                ob.addProperty("hora_aprobada", aOrden.getHoraAutorizoIhsa() != null ? formatTime.format(aOrden.getHoraAutorizoIhsa()) : "");
                ob.addProperty("aprobada_auto", aOrden.isAutorizacionIhsaAuto());
                //Aprobada por socio - falta
                ob.addProperty("aprobada_socio", aOrden.getAutorizaFinanzas() != null ? aOrden.getAutorizaFinanzas().getNombre() : "");
                ob.addProperty("fecha_aprobada_socio", aOrden.getFechaAutorizoFinanzas() != null ? formatDate.format(aOrden.getFechaAutorizoFinanzas()) : "");
                ob.addProperty("hora_aprobada_socio", aOrden.getHoraAutorizoFinanzas() != null ? formatTime.format(aOrden.getHoraAutorizoFinanzas()) : "");
                ob.addProperty("aprobada_socio_auto", aOrden.isAutorizacionFinanzasAuto());
                //Autorizada
                ob.addProperty("autorizada", aOrden.getAutorizaCompras() != null ? aOrden.getAutorizaCompras().getNombre() : "");
                ob.addProperty("fecha_autorizada", aOrden.getFechaAutorizoCompras() != null ? formatDate.format(aOrden.getFechaAutorizoCompras()) : "");
                ob.addProperty("hora_autorizada", aOrden.getHoraAutorizoCompras() != null ? formatTime.format(aOrden.getHoraAutorizoCompras()) : "");
                ob.addProperty("autorizada_automatica", aOrden.isAutorizacionComprasAuto());
                respuesta = buildAcceptedResponse(ob);
            }

            return respuesta;
        } catch (Exception e) {
            LOGGER.fatal(this, "Ocurio un error al buscar la orden " + consecutivo + " ERROR  : :: :: : " + e.getMessage());
            return buildBadResponse("Error " + e.getMessage());
        }
    }

    @POST
    //@Path("/aprobarOrden/{tokenSesion}/{idOrden}/{estado}")
    @Path("/aprobarOrden")
    @Produces(MediaType.APPLICATION_JSON)
    //public String aprobarOrden(@PathParam("idOrden") int idOrden, @PathParam("usuario") String usuario) throws SIAException {
    public Response aprobarOrden(final Params params) {
        LOGGER.info(this, "ID PARAMS " + params.id);
        LOGGER.info(this, "USUARIO PARAMS " + params.usuario);
        LOGGER.info(this, "ESTADO PARAMS " + params.estado);

        if (params.id == 0 || params.usuario == null || params.usuario.isEmpty() || params.estado == 0) {
            return buildBadResponse("Datos requeridos");
        }

        Response retorno;

        try {
            final UsuarioVO usuario = usuarioImpl.findById(params.usuario);
            final String mensaje = aprobarOCS(params.id, params.usuario, usuario.getMail(), params.estado);
            JsonObject retOk = new JsonObject();
            retOk.addProperty("mensaje", mensaje);

            retorno = buildAcceptedResponse(retOk);

        } catch (Exception e) {
            retorno = buildBadResponse(e.getMessage());
            LOGGER.fatal(this, "Ocurrio un error al aprobar . . . " + e.getMessage());
            LOGGER.fatal(this, "Se intentó autorizar la OC/S con ID" + params.id + " y no funcionó correctamente.");
            //
            String mensajeExcepcion = "No se completó la acción de autorizar la OCS " + params.id + ", favor de revisar el sistema de log para mayor detalle de la operación.";
            notificacionOrdenImpl.enviarExcepcionDesarrollo("Intento de autorizar la OC/S -- " + params.id, "Compras", "Autorizar OC/S", mensajeExcepcion);
        }
        return retorno;
    }

    @POST
    @Path("/devolverOrden")
    @Produces(MediaType.APPLICATION_JSON)
    public Response devolverOrdenPost(final Params params) throws SIAException {

        LOGGER.info(this, "ID ORDEN PARAMS " + params.id);
        LOGGER.info(this, "USUARIO PARAM " + params.usuario);
        LOGGER.info(this, "MOTIVO " + params.motivo);

        Response ret = null;
        try {

            if (params.id == 0 || params.usuario == null || params.usuario.isEmpty() || params.motivo == null || params.motivo.isEmpty()) {
                return buildBadResponse("Datos requeridos");
            }

            final UsuarioVO usuario = usuarioImpl.findById(params.usuario);

            if (usuario == null) {
                return buildBadResponse("No se encontró el usuario");
            }

            final Orden orden = ordenImpl.find(params.id);

            if (orden == null) {
                return buildBadResponse("No se encontró la orden");
            }

            boolean ordenDevuelta = ordenImpl.devolverOrden(orden, usuario.getId(), usuario.getId(), params.motivo);

            JsonObject retOk = new JsonObject();
            retOk.addProperty("mensaje", "Se realizo la devolución");

            ret = ordenDevuelta ? buildAcceptedResponse(retOk) : buildBadResponse("Sucedió algo al devolver la orden " + orden.getConsecutivo());

        } catch (Exception ex) {
            LOGGER.fatal(this, "Ocurio un error al dev. la OCS  ERROR  : :: :: : " + ex.getMessage());

            String mensajeExcepcion = "No se completó la acción de devolver la OCS , favor de revisar el sistema de log para mayor detalle de la operación.";

            notificacionOrdenImpl.enviarExcepcionDesarrollo("Intento de devolver la OC/S -- ", "Compras", "Devolver OC/S", mensajeExcepcion);

            return buildBadResponse("Algo sucedió al intentar devolver la orden");
        }

        return ret;
    }

    /**
     *
     * @param sesion
     * @return
     * @throws sia.excepciones.SIAException
     */
    @GET
    @Path("/totalOrdenesPorAprobar/{sesion}")
    @Produces(MediaType.APPLICATION_JSON)
    public String totalOrdenesPorAprobar(@PathParam("sesion") String sesion) throws SIAException {
        int total = 0;
//	System.out.println("Total de ocs por la sesion : " + sesion);
        String user = siUsuarioCodigoImpl.buscarUsuario(sesion);
        JsonObject jsonOrden = new JsonObject();
        if (!user.isEmpty()) {
            for (CampoUsuarioPuestoVo col : apCampoUsuarioRhPuestoImpl.getAllPorUsurio(user)) {
                //total += ordenImpl.totalOcsPendientePorCampo(user, col.getIdCampo());
                total += ordenImpl.totalOcsPendientePorCampoAprobadores(user, col.getIdCampo());
            }
//	    System.out.println("regres a las ocs : : :" + jsonOrden.toString());
        }
        jsonOrden.addProperty("total", total);
        return jsonOrden.toString();
    }

    @GET
    @Path("/totalOrdenesPorCampo/{usuario}/{campo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response totalOrdenesPorCampo(@PathParam("usuario") String usuario, @PathParam("campo") int campo) throws SIAException {

        if (usuario == null || usuario.equals("undefined") || campo == 0) {
            LOGGER.info(this, "validation error ");
            return buildBadResponse("validation error sesion and campo is required");
        }

        //final String user = siUsuarioCodigoImpl.buscarUsuario(sesion);      
        final UsuarioVO user = usuarioImpl.findById(usuario);

        LOGGER.info(this, "User dont exist " + user);
        if (user == null) {
            return buildBadResponse("User dont exist");
        }

        //final long total = ordenImpl.totalOcsPendientePorCampo(usuario, campo);
        final long total = ordenImpl.totalOcsPendientePorCampoAprobadores(usuario, campo);
        final JsonObject jsonOrden = new JsonObject();
        jsonOrden.addProperty("idCampo", campo);
        jsonOrden.addProperty("campo", user.getCampo());
        jsonOrden.addProperty("total", total);
        jsonOrden.addProperty("user", usuario);

        return buildAcceptedResponse(jsonOrden);
    }

    @GET
    @Path("/validaToken/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public String validaToken(@PathParam("codigo") String codigo) {
        LOGGER.info(this, "Codigo: " + codigo);
        JsonObject jsonToken = new JsonObject();
        if (siUsuarioCodigoImpl.validaToken(codigo)) {
            jsonToken.addProperty("codigo", codigo);
        } else {
            jsonToken.addProperty("codigo", "");
        }
//	System.out.println("Token : : : " + jsonToken.toString());
        return jsonToken.toString();
    }

    @GET
    @Path("/devolverOrden/{tokenSesion}/{idOrden}/{motivo}")
    @Produces(MediaType.TEXT_PLAIN)
    public String devolverOrden(@PathParam("tokenSesion") String tokenSesion,
            @PathParam("idOrden") int idOrden,
            @PathParam("motivo") String motivo) throws SIAException {
        String valor = "";
//	System.out.println("motivo : : " + motivo);
        String mot = motivo.replaceAll("%20", " ");
//	System.out.println("dev: : token: : " + tokenSesion + " Orden: " + idOrden + " Motivo: " + mot);
        String sesion = siUsuarioCodigoImpl.buscarUsuario(tokenSesion);

        if (!sesion.isEmpty()) {
            try {
                final UsuarioVO usuario = usuarioImpl.findById(sesion);
                final Orden orden = ordenImpl.find(idOrden);

                ordenImpl.devolverOrden(orden, usuario.getId(), usuario.getId(), mot);
                valor = Constantes.OK;
            } catch (Exception ex) {
                LOGGER.fatal(this, "Ocurio un error al dev. la OCS " + idOrden + " ERROR  : :: :: : " + ex.getMessage());

                String mensajeExcepcion = "No se completó la acción de devolver la OCS " + idOrden + ", favor de revisar el sistema de log para mayor detalle de la operación.";
                notificacionOrdenImpl.enviarExcepcionDesarrollo("Intento de devolver la OC/S -- " + idOrden, "Compras", "Devolver OC/S", mensajeExcepcion);
            }
        }
        return valor;
    }

    @GET
    @Path("/traerTrabajoPendiente/{sesion}")
    @Produces(MediaType.TEXT_PLAIN)
    public String traerTrabajoPendiente(@PathParam("sesion") String sesion) throws SIAException {
        JsonObject jsonObject = new JsonObject();
        //  System.out.println("sesion : : : : : " + sesion);
        //
        try {
            //  final UsuarioVO u = usuarioImpl.findById(sesion);
            JsonArray listaCampo = new JsonArray();
            for (CampoUsuarioPuestoVo col : apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion)) {
                JsonObject jsonCampo = new JsonObject();
                jsonCampo.addProperty("id", col.getIdCampo());
                jsonCampo.addProperty("nombre", col.getCampo());
                //contar OCS
                //long total = ordenImpl.totalOcsPendientePorCampo(sesion, col.getIdCampo());
                long total = ordenImpl.totalOcsPendientePorCampoAprobadores(sesion, col.getIdCampo());
                jsonCampo.addProperty("pendiente", total);
                if (total > 0) {
                    listaCampo.add(jsonCampo);
                }
            }
            jsonObject.add("listaCampo", listaCampo);
        } catch (Exception e) {
            LOGGER.info(this, "excg    +  e                " + e.getMessage());
        }
        //System.out.println("Pendiente : : : " + jsonObject.toString());
        return jsonObject.toString();
    }

    @GET
    @Path("/aprobarListaOrden/{listaOrden}/{tokenSesion}")
    @Produces(MediaType.APPLICATION_JSON)
    public String aprobarListaOrden(@PathParam("listaOrden") String listaOrden, @PathParam("tokenSesion") String tokenSesion) {
        //  System.out.println("Lista de ocs por aprobar + + + + + " + listaOrden);
        String sesion = siUsuarioCodigoImpl.buscarUsuario(tokenSesion);
        List<OrdenVO> list = new ArrayList<OrdenVO>();
        String[] cadena = listaOrden.split("QWERT");
        for (String cadena1 : cadena) {
            OrdenVO ordenVO = new OrdenVO();
            String[] cad = cadena1.split(":");
            ordenVO.setId(Integer.parseInt(cad[0]));
            ordenVO.setIdStatus(Integer.parseInt(cad[1]));
            list.add(ordenVO);
        }

        final UsuarioVO usuario = usuarioImpl.findById(sesion);
        String mensaje = "";
        for (Object object : list) {
            OrdenVO ordenVo = (OrdenVO) object;
            mensaje += aprobarOCS(ordenVo.getId(), sesion, usuario.getMail(), ordenVo.getIdStatus());
        }
//	System.out.println("usuario en sesion : : : " + sesion + "Orden a aprobar + +  desde smartphone + + " + listaOrden);

        return mensaje;
    }

    protected String aprobarOCS(int idOrden, String sesion, String correoSesion, int estado) {
        String mensaje = "";
        switch (estado) {
            case Constantes.ESTATUS_SOLICITADA_R: //110
                ordenImpl.aprobarOrdenGerenciaSolicitante(idOrden, sesion, correoSesion);
                break;
            case Constantes.ESTATUS_VISTO_BUENO_R: //120
                ordenImpl.autorizarOrdenMPG(idOrden, sesion, correoSesion);
                break;
            case Constantes.ESTATUS_REVISADA://130
                ordenImpl.autorizarOrdenIHSA(idOrden, sesion, correoSesion);
                break;
            case Constantes.ESTATUS_POR_APROBAR_SOCIO: //135
                ordenImpl.autorizarOrdenSocio(idOrden, sesion, correoSesion);
                break;
            case Constantes.ESTATUS_APROBADA://140
                ordenImpl.autorizarTareaCompra(idOrden, sesion, correoSesion, Constantes.FALSE);
                break;
            default:
                break;
        }
        mensaje = "Se autorizó la OCS - " + idOrden;
        return mensaje;
    }

    @GET
    @Path("/totalPendiente/{sesion}")
    @Produces(MediaType.APPLICATION_JSON)
    public String totalPendiente(@PathParam("sesion") String sesion) throws SIAException {
        JsonObject json = new JsonObject();
        int t = 0;
        int totReq = 0;
        if (!sesion.isEmpty()) {
            JsonArray listaCampo = new JsonArray();
            JsonArray listaCampoReqs = new JsonArray();
            for (CampoUsuarioPuestoVo allPorUsurio : apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion)) {
                // ocs
                //long to = ordenImpl.totalOcsPendientePorCampo(sesion, allPorUsurio.getIdCampo());
                long to = ordenImpl.totalOcsPendientePorCampoAprobadores(sesion, allPorUsurio.getIdCampo());
                if (to > 0) {
                    JsonObject jsonCampo = new JsonObject();
                    jsonCampo.addProperty("id", allPorUsurio.getIdCampo());
                    jsonCampo.addProperty("nombre", allPorUsurio.getCampo());
                    jsonCampo.addProperty("pendiente", to);
                    listaCampo.add(jsonCampo);
                }
                t += to;

                long tReq = requisicionImpl.totalRequisicionesPorCampo(sesion, allPorUsurio.getIdCampo());
                if (tReq > 0) {
                    JsonObject jsonCampo = new JsonObject();
                    jsonCampo.addProperty("id", allPorUsurio.getIdCampo());
                    jsonCampo.addProperty("nombre", allPorUsurio.getCampo());
                    jsonCampo.addProperty("pendiente", tReq);
                    listaCampoReqs.add(jsonCampo);
                }
                totReq += tReq;
            }
            json.add("pendientocs", listaCampo);
            json.add("pendientocsreqs", listaCampoReqs);
            //
            json.addProperty("totalOCS", t);
            json.addProperty("totalReq", totReq);
            json.addProperty("totalSolViaje", sgEstatusAprobacionImpl.totalPendiente(sesion));
            json.addProperty("totalSolEst", sgSolicitudEstanciaImpl.totalSolicituesPorAprobar(sesion));
            
            // permiso para ver cml                    
            Usuario usuario = usuarioImpl.buscarPorId(sesion);        
            json.addProperty("ver_cml",usuario.isMonitorCmlVisible());

        }     
        
        
        return json.toString();
    }

    @GET
    @Path("/guardarTelefonoId/{sesion}/{telefonoId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String guardarTelefonoId(@PathParam("sesion") String sesion, @PathParam("telefonoId") String telefonoId) throws SIAException {
        LOGGER.info(this, "@guardarTelefonoId " + sesion + " telefonoid" + telefonoId);
        try {
            siUsuarioCodigoImpl.guardarToken(sesion, telefonoId);
            return "ok";
        } catch (Exception e) {
            return "Error";
        }
    }
    
    @POST
    @Path("/token_mensajeria")
    @Produces(MediaType.APPLICATION_JSON)
    public Response guardartoken(final ParamsToken params) throws SIAException {
        
        if(params == null || params.token == null || params.usuario == null || params.token == "" || params.usuario == ""){
             return buildBadResponse("validation error token and user is required");
        }
        
        LOGGER.info(this, "@guardarTelefonoId token " + params.token  + " usuario" + params.usuario
        );
        try {
            siUsuarioCodigoImpl.guardarToken(params.usuario, params.token);
            JsonObject res = new JsonObject();
            res.addProperty("status", Boolean.TRUE);                    
            return buildAcceptedResponse(res);
        } catch (Exception e) {
            LOGGER.error(e);
            return buildBadResponse("Sucedió un error");
        }
    }

    @GET
    @Path("/eliminarTelefonoId/{sesion}/{telefonoId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String eliminarTelefonoId(@PathParam("sesion") String sesion, @PathParam("telefonoId") String telefonoId) throws SIAException {
        LOGGER.fatal(this, "@eliminarrTelefonoId " + sesion + " telefonoid" + telefonoId);

        final JsonObject json = new JsonObject();

        try {
            boolean eliminado = siUsuarioCodigoImpl.eliminarToken(sesion, telefonoId);

            json.addProperty("status", eliminado);

            return json.toString();

        } catch (Exception e) {
            json.addProperty("status", false);

            return json.toString();
        }
    }

    //::::::::::::::::::::::::::::: REQUISICIONES :::::::::::::::::::::::::
    //@QueryParam("ZWZ2W") int zwz2w, @QueryParam("ZWZ3W") String zwz3w
    @GET
    @Path("/traerRequisiciones/{usuario}/{campo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerRequisiciones(@PathParam("usuario") String usuario, @PathParam("campo") int campo) throws SIAException {
        LOGGER.info(this, "@traerRequisiciones");
        try {
            if (usuario == null || usuario.isEmpty() || campo == 0) {
                return buildBadResponse("El usuario el campo son requeridos.");
            }

            final List<Object[]> consultaSinRevisar = requisicionServicioRemoto.getRequisicionesSinRevisar(usuario, campo);

            final List<Object[]> consultaSinAprobar = requisicionServicioRemoto.getRequisicionesSinAprobar(usuario, campo);

            //final List<Object[]> consultaSinAutorizar = requisicionServicioRemoto.getRequisicionesSinAutorizar(usuario);
            final List<RequisicionVO> listaSinVistoBuenoContabibilidad
                    = requisicionServicioRemoto.requisicionesSinVistoBueno(usuario, campo, TipoRequisicion.AF.name(), Constantes.ROL_VISTO_BUENO_CONTABILIDAD);

            if (listaSinVistoBuenoContabibilidad != null) {
                wrapEstatusRequisicion(listaSinVistoBuenoContabibilidad, OperacionRequisicion.VISTO_BUENO_CONTABILIDAD.name());
                //listaSinVistoBuenoContabibilidad.forEach(requisicionVo -> requisicionVo.setEstatus(OperacionRequisicion.VISTO_BUENO_CONTABILIDAD.name()));
            }

            final List<RequisicionVO> listaSinAsignar
                    = requisicionServicioRemoto.getRequisicionesSinAsignar(usuario, campo, Constantes.ROL_ASIGNA_REQUISICION, Constantes.REQUISICION_APROBADA);
            if (listaSinAsignar != null) {
                wrapEstatusRequisicion(listaSinAsignar, OperacionRequisicion.ASIGNAR.name());
            }

            final List<RequisicionVO> listaRequisicion = new ArrayList<>();
            final List<RequisicionVO> listaSinRevisar = castToListRequisicionVo(consultaSinRevisar, OperacionRequisicion.REVISAR.name());
            final List<RequisicionVO> listaSinAprobar = castToListRequisicionVo(consultaSinAprobar, OperacionRequisicion.APROBAR.name());

            listaRequisicion.addAll(listaSinRevisar);
            listaRequisicion.addAll(listaSinAprobar);
            listaRequisicion.addAll(listaSinVistoBuenoContabibilidad != null ? listaSinVistoBuenoContabibilidad : Collections.EMPTY_LIST);
            listaRequisicion.addAll(listaSinAsignar != null ? listaSinAsignar : Collections.EMPTY_LIST);

            final Gson gson = new Gson();
            final String json = gson.toJson(listaRequisicion);
            return buildAcceptedResponse(json);
        } catch (Exception ex) {
            System.out.println(ex);
            LOGGER.fatal(this, "Error al tratar de consultar las requisiciones : " + ex.getMessage());
            //System.out.println("Error al tratar de consultar las requisiciones : " + ex.getMessage());
            return buildBadResponse("Error al tratar de consultar las requisiciones ");
        }
    }

    @GET
    @Path("/requisicion/{consecutivo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerRequisicion(@PathParam("consecutivo") String consecutivo,
            @QueryParam("infoOrdenes") boolean infoOrdenes,
            @QueryParam("infoDevoluciones") boolean infoDevoluciones,
            @QueryParam("infoNotas") boolean infoNotas) throws SIAException {
        try {
            if (consecutivo == null || consecutivo.isEmpty()) {
                return buildBadResponse("El id es requerido.");
            }

            //final Requisicion requisicion = requisicionServicioRemoto.buscarPorConsecutivo(consecutivo);
            final RequisicionVO requisicion = requisicionServicioRemoto.buscarPorConsecutivoConDetalle(consecutivo);
            final List<RequisicionEtsVo> ets = reRequisicionEtsImpl.traerAdjuntosPorRequisicion(consecutivo);
            requisicion.setListaEts(ets);

            if (infoOrdenes) {

            }
            if (infoDevoluciones) {
                //requisicionServicioRemoto.getRechazosPorRequisicion(requisicion.getId());
            }
            if (infoNotas) {

            }

            final Gson gson = new Gson();
            final String json = gson.toJson(requisicion);

            return buildAcceptedResponse(json);
        } catch (Exception ex) {
            LOGGER.fatal(this, "Error al tratar de consultar la requisicion : " + ex.getMessage());
            //System.out.println("Error al tratar de consultar la requisicion : " + ex.getMessage());
            return buildBadResponse("Error al tratar de consultar la requisicion");
        }
    }

    @POST
    @Path("/revisarRequisicion")
    @Produces(MediaType.APPLICATION_JSON)
    public Response revisarRequisicion(final Params params) {
        LOGGER.info(this, "@revisarRequisicion");
        return procesarRequisicion(params, OperacionRequisicion.REVISAR);
    }

    @POST
    @Path("/aprobarRequisicion")
    @Produces(MediaType.APPLICATION_JSON)
    public Response aprobarRequisicion(final Params params) {
        LOGGER.info(this, "@AprobarRequisicion");
        return procesarRequisicion(params, OperacionRequisicion.APROBAR);
    }

    @POST
    @Path("/aceptarRequisicion")
    @Produces(MediaType.APPLICATION_JSON)
    public Response aceptarRequisicion(final Params params) {
        LOGGER.info(this, "@aceptarRequisicion");
        try {
            OperacionRequisicion operacion = OperacionRequisicion.valueOf(params.operacionEstatus);
            LOGGER.info(this, "Operacion aplicar " + operacion.name());
            return procesarRequisicion(params, operacion);
        } catch (Exception ia) {
            LOGGER.fatal(ia);
            return buildBadResponse("La operacion no coincide ");
        }
    }

    public Response procesarRequisicion(final Params params, OperacionRequisicion operacion) {
        LOGGER.info(this, "ID PARAMS " + params.id);
        LOGGER.info(this, "PARAMS " + params);

        if (params.id == 0 || params.usuario == null || params.usuario.isEmpty()) {
            return buildBadResponse("Datos requeridos");
        }

        if (operacion == OperacionRequisicion.CANCELAR
                || operacion == OperacionRequisicion.DEVOLVER
                && (params.motivo == null || params.motivo.isEmpty())) {
            return buildBadResponse("Se requiere un motivo.");
        }

        Response retorno;

        try {
            final Requisicion requisicion = requisicionImpl.find(params.id);
            final Usuario usuario = usuarioImpl.find(params.usuario);
            RespuestaVo respuesta = null;

            /* if (!operacionPermitida(requisicion, operacion)) {
                System.out.println(""+requisicion.getEstatus().getNombre()+"  "+operacion.name());
                respuesta = RespuestaVo.builder().realizado(false).mensaje("Las operaciones no coinciden").build();
                
            } else {*/
            switch (operacion) {
                case REVISAR:
                    respuesta = requisicionServicioRemoto.revisarRequisicion(requisicion, usuario);
                    break;
                case APROBAR:
                    respuesta = requisicionServicioRemoto.aprobarRequisicion(requisicion, usuario);
                    break;
                case DEVOLVER:
                    respuesta = requisicionServicioRemoto.rechazar(usuario, new RequisicionVO(requisicion.getId()), params.motivo);
                    ;
                    break;
                case CANCELAR:
                    respuesta = requisicionServicioRemoto.cancelar(usuario, new RequisicionVO(requisicion.getId()), params.motivo);
                    break;
                case ASIGNAR:
                    //respuesta = requisicionServicioRemoto.
                    break;
                case VISTO_BUENO_CONTABILIDAD:
                    //pedir el cfdi y que la compañia se hsa_cq buscar la implementacion en vistobuenocontabilidad.java
                    //respuesta = requisicionServicioRemoto.vistoBuenoRequisicion(usuario, requisicionVO);
                    break;
                default:
                    throw new IllegalArgumentException("No existe la operacion a procesar " + operacion);

            }
            //  }

            final Gson gson = new Gson();
            final String respuestaJson = gson.toJson(respuesta);

            retorno = buildAcceptedResponse(respuestaJson);

        } catch (Exception e) {
            retorno = buildBadResponse(e.getMessage());
            LOGGER.fatal(this, "Ocurrio un error al aprobar la requisicion desde API . . " + e.getMessage());
            notificacionOrdenImpl.enviarExcepcionDesarrollo("Intento de aprobar una requisicion " + params, "Compras", "Autorizar OC/S", e.getMessage());
        }

        return retorno;
    }

    private boolean operacionPermitida(Requisicion requisicion, OperacionRequisicion operacion) {
        return requisicion.getEstatus().getNombre().equals(operacion.name());
    }

    @POST
    @Path("/devolverRequisicion")
    @Produces(MediaType.APPLICATION_JSON)
    public Response devolverRequisicion(final Params params) {
        LOGGER.info(this, "@devolverRequisicion");
        return procesarRequisicion(params, OperacionRequisicion.DEVOLVER);
    }

    @POST
    @Path("/cancelarRequisicion")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelarRequisicion(final Params params) {
        LOGGER.info(this, "@cancelarRequisicion");
        return procesarRequisicion(params, OperacionRequisicion.CANCELAR);
    }
    

    /*private RequisicionVO casting(Object[] objects,String estatus){
                RequisicionVO requisicionVo = new RequisicionVO();
                requisicionVo.setId((Integer) objects[0]);
                requisicionVo.setConsecutivo(String.valueOf(objects[1]));
                requisicionVo.setReferencia(String.valueOf(objects[2]));
                requisicionVo.setFechaSolicitada((Date) objects[3]);
                requisicionVo.setFechaRequerida((Date) objects[4]);
                requisicionVo.setPrioridad((String) objects[5]);
                requisicionVo.setCompania((String) objects[6]);
                requisicionVo.setMontoPesos(((Double) objects[7]));
                requisicionVo.setMontoDolares((Double) objects[8]);
                requisicionVo.setMontoTotalDolares((Double) objects[9]);
                requisicionVo.setEstatus(estatus);                
                return requisicionVo;
    };*/
    private List<RequisicionVO> castToListRequisicionVo(List<Object[]> lista, String estatus) {

        final List<RequisicionVO> listaRequisiciones = new ArrayList<>();

        if (!lista.isEmpty()) {
            RequisicionVO requisicionVo;
            for (Object[] objects : lista) {
                requisicionVo = new RequisicionVO();
                requisicionVo.setId((Integer) objects[0]);
                requisicionVo.setConsecutivo(String.valueOf(objects[1]));
                requisicionVo.setReferencia(String.valueOf(objects[2]));
                requisicionVo.setFechaSolicitada((Date) objects[3]);
                requisicionVo.setFechaRequerida((Date) objects[4]);
                requisicionVo.setPrioridad((String) objects[5]);
                requisicionVo.setCompania((String) objects[6]);
                requisicionVo.setMontoPesos(((Double) objects[7]));
                requisicionVo.setMontoDolares((Double) objects[8]);
                requisicionVo.setMontoTotalDolares((Double) objects[9]);
                requisicionVo.setEstatus(estatus);
                listaRequisiciones.add(requisicionVo);
            }
        }

        return listaRequisiciones;
    }

    private void wrapEstatusRequisicion(List<RequisicionVO> lista, String estatus) {

        if (!lista.isEmpty()) {
            for (RequisicionVO requisicionVo : lista) {
                requisicionVo.setEstatus(estatus);
            }
        }

    }

}

@XmlRootElement
class Params {

    @XmlElement
    public int id;
    @XmlElement
    public String usuario;
    @XmlElement
    public int estado;
    @XmlElement
    public String motivo;
    //--para requisicion
    @XmlElement
    public int idCdfi;
    @XmlElement
    public int idCompradorAsignar;
    @XmlElement
    public String operacionEstatus;

    @Override
    public String toString() {
        return "id:" + this.id + ",usuario:" + this.usuario;
    }

}


@XmlRootElement
class ParamsToken {
    
    @XmlElement
    public String usuario;
    @XmlElement
    public String token;
    
}
