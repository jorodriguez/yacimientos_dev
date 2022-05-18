package com.ihsa.sia.api.mobile.inventario;

import com.ihsa.sia.api.mobile.Estado;
import com.ihsa.sia.api.mobile.MovilApiBase;
import com.ihsa.sia.api.mobile.Respuesta;
import com.ihsa.sia.commons.Messages;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import static sia.constantes.Constantes.INV_MOVIMIENTO_TIPO_ENTRADA;
import static sia.constantes.Constantes.INV_MOVIMIENTO_TIPO_SALIDA;
import static sia.constantes.Constantes.INV_MOVIMIENTO_TIPO_TRASPASO_SALIENTE;
import sia.excepciones.SIAException;
import sia.inventarios.authentication.ApiAuthService;
import sia.inventarios.service.InvCeldaImpl;
import sia.inventarios.service.InvDetalleSolicitudMaterialImpl;
import sia.inventarios.service.InvEstadoAprobacionSolicitudImpl;
import sia.inventarios.service.InvInventarioCeldaImpl;
import sia.inventarios.service.InvOrdenFormatoImpl;
import sia.inventarios.service.InvSolicitudMaterialImpl;
import sia.inventarios.service.InventarioImpl;
import sia.inventarios.service.TransaccionImpl;
import sia.inventarios.service.TransaccionRemote;
import sia.inventarios.service.Utilitarios;
import sia.modelo.SiAdjunto;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.inventarios.CeldaVo;
import sia.modelo.vo.inventarios.DetalleSolicitudMaterialAlmacenVo;
import sia.modelo.vo.inventarios.SolicitudMaterialAlmacenVo;
import sia.modelo.vo.inventarios.TransaccionArticuloVO;
import sia.modelo.vo.inventarios.TransaccionVO;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.util.OrdenEstadoEnum;
import sia.util.SolicitudMaterialEstadoEnum;
import sia.util.UtilLog4j;

/**
 * Rest Endpoint para inventarios, contiene funcionalidad para la aplicaciones
 * moviles //
 *
 * @author Aplimovil SA de CV
 */
@Path("/mobile/inventario")
@RequestScoped
public class InventarioApi extends MovilApiBase {

    @Inject
    private TransaccionRemote transaccionService;
    @Inject
    protected ApiAuthService apiAuthService;
    @Inject
    private InventarioImpl inventarioImpl;
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private InvEstadoAprobacionSolicitudImpl estadoAprobacionSolicitudImpl;
    @Inject
    private InvDetalleSolicitudMaterialImpl detalleSolicitudMaterialImpl;
    @Inject
    private InvSolicitudMaterialImpl solicitudMaterialImpl;
    @Inject
    ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    InvOrdenFormatoImpl invOrdenFormatoImpl;
    @Inject
    InvCeldaImpl invCeldaImpl;
    @Inject
    InvInventarioCeldaImpl inventarioCeldaImpl;

    //
    protected static final String API_KEY_PARAM = "apiKey";
    private static final String HEADER_CHARSET = MediaType.APPLICATION_JSON + "; charset=UTF-8";

    @Path("/procesarMovimiento")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response procesarInventario(@FormParam("fecha") FechaPametro fechaPametro,
            @FormParam("almacenOrigenId") int almacenOrigenId,
            @FormParam("tipoMovimiento") int tipoMovimiento,
            @FormParam("almacenDestinoId") int almacenDestinoId,
            @FormParam("observaciones") String observaciones,
            @FormParam("folioDeCompra") String folioDeCompra,
            @FormParam("folioDeRemision") String folioDeRemision,
            @FormParam("articulos") ArticulosParametro articulosParametro,
            @FormParam(API_KEY_PARAM) String apiKey) {

        try {
            if (tipoMovimiento == 2) {
                folioDeRemision = folioDeCompra;
            }
            //si no se pudo autenticar se retorna la respues con la información del porque no se pudo autenticar
//	    if (!autenticar(apiKey)) {
//		return getRespuestaAutenticacion();
//	    }
            //validar folio de compra si ha sido enviado al servicio
            if (INV_MOVIMIENTO_TIPO_ENTRADA.equals(tipoMovimiento) && !Utilitarios.esNuloOVacio(folioDeCompra)) {
                //si no es valido retornar mensaje de error
                if (!transaccionService.validarFolioOrdenDeCompra(folioDeCompra)) {
                    return Response.ok(new Respuesta(Estado.error, Messages.getString("sia.inventarios.movimiento.api.folioOrdenCompraInvalido")))
                            .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                            .build();
                }
            }
            //validar folio remision si es de tipo salida
            if (INV_MOVIMIENTO_TIPO_SALIDA.equals(tipoMovimiento) && Utilitarios.esNuloOVacio(folioDeCompra)) {
                return Response.ok(new Respuesta(Estado.error, Messages.getString("sia.inventarios.movimiento.api.folioRemisionInvalido")))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            }

            //se crea el elemento de transacción con los parámetros recibidos
            TransaccionVO transaccion = new TransaccionVO();
            transaccion.setFecha(fechaPametro.getFecha());
            transaccion.setAlmacenId(almacenOrigenId);
            transaccion.setTipoMovimiento(tipoMovimiento);
            if (tipoMovimiento == INV_MOVIMIENTO_TIPO_TRASPASO_SALIENTE) {
                transaccion.setTraspasoAlmacenDestinoId(almacenDestinoId);
            }
            if (tipoMovimiento == INV_MOVIMIENTO_TIPO_SALIDA) {
                transaccion.setFolioRemision(folioDeRemision);
                if (Utilitarios.esNuloOVacio(folioDeRemision) && !Utilitarios.esNuloOVacio(folioDeCompra)) {
                    transaccion.setFolioRemision(folioDeCompra);
                }
            }
            transaccion.setNotas(observaciones);
            transaccion.setNumeroArticulos(0);
            transaccion.setFolioOrdenCompra(folioDeCompra);
            //se llama al ejb que crea el movimiento
            transaccionService.crearYProcesar(transaccion, articulosParametro.getArticulos(),
                    getUsuario(apiKey).getId(), Constantes.AP_CAMPO_DEFAULT);
            //se retorna el mensaje exitoso si no existio error
            return Response.ok(new Respuesta(Estado.ok)).build();
        } catch (Exception ex) {
            if (ex.getCause() instanceof IllegalArgumentException) {
                return Response.ok(new Respuesta(Estado.error,
                        Messages.getString("sia.inventarios.mobile.mensaje.api_key.no_valido")))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            } else if (ex.getCause() instanceof SIAException) {
                return Response.ok(new Respuesta(Estado.error, ex.getMessage()))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            }
            return Response.ok(new Respuesta(Estado.error,
                    Messages.getString("sia.inventarios.mobile.mensaje.error")))
                    .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                    .build();
        }
    }

    @Path("/conciliarExistencia")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response conciliarExistencia(@FormParam("inventarioId") int inventarioId,
            @FormParam("unidadesReales") double unidadesReales,
            @FormParam(API_KEY_PARAM) String apiKey) {
        try {
            UsuarioVO userVo = getUsuario(apiKey);
            if (userVo != null) {
                inventarioImpl.conciliar(inventarioId, unidadesReales, "", userVo.getId(), userVo.getIdCampo());
                return Response.ok(new Respuesta(Estado.ok)).build();
            }
            return Response.ok(new Respuesta(Estado.error)).build();
        } catch (Exception ex) {
            if (ex.getCause() instanceof IllegalArgumentException) {
                return Response.ok(new Respuesta(Estado.error,
                        Messages.getString("sia.inventarios.mobile.mensaje.api_key.no_valido")))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            } else if (ex.getCause() instanceof SIAException) {
                return Response.ok(new Respuesta(Estado.error, ex.getMessage()))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            }
            return Response.ok(new Respuesta(Estado.error,
                    Messages.getString("sia.inventarios.mobile.mensaje.error")))
                    .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                    .build();
        }
    }

    @Path("/entradaArticulos")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarEntrada(@FormParam("compraFolio") String compra,
            @FormParam("almacenId") int almacenId,
            @FormParam("articulos") CompraDetalleParametroMovil compraDetalleParametroMovil,
            @FormParam("observaciones") String observacion,
            @FormParam("evidencia") String evidencia,
            @FormParam(API_KEY_PARAM) String apiKey) {
        try {
            UsuarioVO userVo = getUsuario(apiKey);
            if (userVo != null) {
                try {
                    OrdenVO ordenVo = ordenImpl.buscarOrdenPorConsecutivo(compra, false);
                    //
                    List<TransaccionArticuloVO> arts = compraDetalleParametroMovil.getArticulos();
                    TransaccionVO transaccionVo = new TransaccionVO();
                    transaccionVo.setAlmacenId(almacenId);
                    transaccionVo.setTipoMovimiento(Constantes.INV_MOVIMIENTO_TIPO_ENTRADA);
                    transaccionVo.setFecha(new Date());
                    transaccionVo.setNumeroArticulos(arts.size());
                    transaccionVo.setFolioOrdenCompra(compra);
                    transaccionVo.setNotas(observacion);
                    transaccionVo.setFolioRemision(Constantes.VACIO);
                    transaccionVo.setStatus(Constantes.INV_TRANSACCION_STATUS_PREPARACION);
                    //
                    transaccionService.crear(transaccionVo, arts, userVo.getId(), ordenVo.getIdBloque());
                    //
                    byte[] data = null;
                    try {
                        data = Base64.getUrlDecoder().decode(evidencia);
                    } catch (Exception e) {
                        return Response.ok(new Respuesta(Estado.error,
                                "Error al procesar la imagen"))
                                .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                                .build();
                    }

                    //byte[] data = evidencia.getBytes();
                    AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
                    DocumentoAnexo documentoAnexo = new DocumentoAnexo(data);
                    documentoAnexo.setNombreBase(ordenVo.getConsecutivo().replaceAll("-", "") + "_" + (new Date()).getTime() + "_entrada.jpeg");
                    documentoAnexo.setRuta(new StringBuilder().append("ETS/Orden/Inventario/").append(ordenVo.getConsecutivo().replace("-", "")).toString());
                    documentoAnexo.setTipoMime("image/jpeg");
                    almacenDocumentos.guardarDocumento(documentoAnexo);

                    SiAdjunto adj = siAdjuntoImpl.save(documentoAnexo.getNombreBase(),
                            new StringBuilder()
                                    .append(documentoAnexo.getRuta())
                                    .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                            documentoAnexo.getTipoMime(), documentoAnexo.getTamanio(), userVo.getId());

                    if (adj != null) {
                        invOrdenFormatoImpl.guardar(userVo.getId(), ordenVo.getConsecutivo(), adj.getId(), INV_MOVIMIENTO_TIPO_ENTRADA);
                    }
                    //
                    return Response.ok(new Respuesta(Estado.ok,
                            "La compra fue recibida con exito."))
                            .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                            .build();
                } catch (SIAException e) {
                    UtilLog4j.log.error(e);
                    return Response.ok(new Respuesta(Estado.error,
                            Messages.getString("sia.inventarios.mobile.mensaje.error")))
                            .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                            .build();
                }
            }
            return Response.serverError().build();

        } catch (Exception ex) {
            if (ex.getCause() instanceof IllegalArgumentException) {
                return Response.ok(new Respuesta(Estado.error,
                        Messages.getString("sia.inventarios.mobile.mensaje.api_key.no_valido")))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            } else if (ex.getCause() instanceof SIAException) {
                return Response.ok(new Respuesta(Estado.error, ex.getMessage()))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            }
            return Response.ok(new Respuesta(Estado.error,
                    Messages.getString("sia.inventarios.mobile.mensaje.error")))
                    .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                    .build();
        }
    }

    @Path("/valesSalidaMaterial")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response valesSalidaMaterial(@QueryParam(API_KEY_PARAM) String apiKey) {
        try {
            UsuarioVO userVo = getUsuario(apiKey);
            if (userVo != null) {
                SolicitudMaterialMovilVo solMovilVo;
                List<SolicitudMaterialMovilVo> solMovilvos;
                List<SolicitudMaterialAlmacenVo> solicitudes = estadoAprobacionSolicitudImpl.traerSolicitudesRolStatus(1, "MZAMARRON", SolicitudMaterialEstadoEnum.POR_ENTREGAR_MATERIAL.getId(), Constantes.ROL_ENTREGA_MAT);
// estadoAprobacionSolicitudImpl.traerSolicitudesPorStatus(SolicitudMaterialEstadoEnum.POR_ENTREGAR_MATERIAL.getId());
                if (solicitudes != null) {
                    solMovilVo = new SolicitudMaterialMovilVo();
                    solMovilvos = new ArrayList<SolicitudMaterialMovilVo>();
                    for (SolicitudMaterialAlmacenVo solVo : solicitudes) {
                        SolicitudMaterialMovilVo solMvo = new SolicitudMaterialMovilVo();
                        solMvo.setId(solVo.getId());
                        solMvo.setAlmacen(solVo.getAlmacen());
                        solMvo.setFolio(solVo.getFolio());
                        solMvo.setSolicita(solVo.getSolicita());
                        solMvo.setFechaRequiere(solVo.getFechaRequiere());
                        solMvo.setRecogeMaterial(solVo.getUsuarioRecoge());
                        //
                        solMovilvos.add(solMvo);
                    }
                    solMovilVo.setSolicitudes(solMovilvos);
                    Respuesta<SolicitudMaterialMovilVo> respuesta = new Respuesta<>(solMovilVo);
                    return Response.ok(respuesta).build();
                } else {
                    return Response.ok(new Respuesta(Estado.error,
                            "No hay solicitudes pendientes"))
                            .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                            .build();
                }
            } else {
                return Response.ok(new Respuesta(Estado.error,
                        "Es necesario cerrar sesión y volver a entrar a la aplicación."))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return Response.ok(new Respuesta(Estado.error,
                    "Ocurrió un error al recuperar las solicitudes."))
                    .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                    .build();
        }
    }

    @Path("/materialesSolicitados")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response materialesPorSolicitud(@QueryParam("solicitudId") int solicitudId) {
        try {

            List<DetalleSolicitudMaterialAlmacenVo> detalle = detalleSolicitudMaterialImpl.traerPorSolicitudId(solicitudId);
            SolicitudMaterialMovilVo solMat = new SolicitudMaterialMovilVo();
            solMat.setId(solicitudId);
            List<DetalleSolicitudMaterialMovilVo> solDetMovilvos;
            solDetMovilvos = new ArrayList<DetalleSolicitudMaterialMovilVo>();
            for (DetalleSolicitudMaterialAlmacenVo solVo : detalle) {
                DetalleSolicitudMaterialMovilVo solMvo = new DetalleSolicitudMaterialMovilVo();
                solMvo.setId(solVo.getId());
                solMvo.setArticulo(solVo.getArticulo());
                solMvo.setUnidad(solVo.getUnidad());
                solMvo.setArticuloId(solVo.getIdArticulo());
                solMvo.setCantidadSolicitada(solVo.getCantidad());
                solMvo.setDisponible(solVo.getDisponibles());
                //
                solDetMovilvos.add(solMvo);
            }
            solMat.setDetalleSolicitud(solDetMovilvos);
            Respuesta<SolicitudMaterialMovilVo> respuesta = new Respuesta<>(solMat);
            return Response.ok(respuesta).build();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return Response.ok(new Respuesta(Estado.error,
                    "Ocurrió un error al recuperar las solicitudes."))
                    .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                    .build();
        }
    }

    @Path("/entregarMaterial")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response entregarMaterial(@FormParam("solicitudId") int solicitudId,
            @FormParam("observacion") String observacion,
            @FormParam("recibe") String recibe,
            @FormParam("materiales") SolicitudMaterialesDetalleParametroMovil materiales,
            @FormParam(API_KEY_PARAM) String apiKey) {

        try {
            UsuarioVO userVo = getUsuario(apiKey);
            if (userVo != null) {
                SolicitudMaterialAlmacenVo solicitudVo = solicitudMaterialImpl.solicitudesPorId(solicitudId);
                if (!observacion.isEmpty()) {
                    solicitudVo.setObservacion(solicitudVo.getObservacion() + " * " + userVo.getNombre() + ": " + observacion);
                }
                solicitudVo.setCantidadRecibida(materiales.getArticulos().size());
                solicitudVo.setUsuarioRecibeMaterial(recibe);
                solicitudVo.setMateriales(materiales.getArticulos());
                estadoAprobacionSolicitudImpl.entregarMaterial(solicitudVo, userVo);
            }
            return Response.ok(new Respuesta(Estado.ok)).build();

        } catch (Exception ex) {
            if (ex.getCause() instanceof IllegalArgumentException) {
                return Response.ok(new Respuesta(Estado.error,
                        Messages.getString("sia.inventarios.mobile.mensaje.api_key.no_valido")))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            } else if (ex.getCause() instanceof SIAException) {
                return Response.ok(new Respuesta(Estado.error, ex.getMessage()))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            }
            return Response.ok(new Respuesta(Estado.error,
                    Messages.getString("sia.inventarios.mobile.mensaje.error")))
                    .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                    .build();
        }
    }

    @Path("/finalizarSolicitudMaterial")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response finalizarSolicitudMateriles(@FormParam("solicitudId") int solicitudId,
            @FormParam("motivo") String motivo, @FormParam(API_KEY_PARAM) String apiKey) {
        try {
            UsuarioVO userVo = getUsuario(apiKey);
            if (userVo != null) {
                SolicitudMaterialAlmacenVo smVo = solicitudMaterialImpl.solicitudesPorId(solicitudId);
                estadoAprobacionSolicitudImpl.finalizarSolicitudMaterial(smVo, userVo, motivo);
                return Response.ok(new Respuesta(Estado.ok)).build();
            } else {
                return Response.ok(new Respuesta(Estado.error,
                        "Ocurrió un error autenticar al usuario."))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return Response.ok(new Respuesta(Estado.error,
                    "Ocurrió un error al recuperar las solicitudes."))
                    .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                    .build();
        }
    }

    @Path("/imprimirEtiqueta")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response imprimirEtiquetas(@FormParam("codigo") String codigoArt, @FormParam("numero") int numeroEtiquetas,
            @FormParam("nombreCorto") String nombreCorto) {
        try {
            for (int i = 0; i < numeroEtiquetas; i++) {
                try (Socket clientSocket = new Socket(Configurador.inventarioImpresoraUrl(), 9100)) {
                    //
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    //
                    outToServer.writeBytes("^XA^FO40,30^BQN,6,6^FH^FDMM,B0032" + codigoArt + "^FS^FT15,175,Y,N^FD" + nombreCorto + "^FS^PQ1,0,1,Y^XZ");
                }
            }
            return Response.ok(new Respuesta(Estado.ok)).build();
        } catch (IOException ex) {
            if (ex.getCause() instanceof IllegalArgumentException) {
                return Response.ok(new Respuesta(Estado.error,
                        Messages.getString("sia.inventarios.mobile.mensaje.api_key.no_valido")))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            } else if (ex.getCause() instanceof SIAException) {
                return Response.ok(new Respuesta(Estado.error, ex.getMessage()))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            }
            return Response.ok(new Respuesta(Estado.error,
                    Messages.getString("sia.inventarios.mobile.mensaje.error")))
                    .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                    .build();
        }
    }

    @GET
    @Path("/buscarCompra")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarCompra(@QueryParam("codigoCompra") String codigo) {
        OrdenVO orden = ordenImpl.buscarOrdenPorConsecutivo(codigo, Boolean.TRUE);
        if (orden != null) {
            if (orden.getIdStatus() == OrdenEstadoEnum.POR_RECIBIR.getId()
                    || orden.getIdStatus() == OrdenEstadoEnum.RECIBIDA_PARCIAL.getId()) {
                try {
                    OrdenMovilVo compra = new OrdenMovilVo();
                    compra.setId(orden.getId());
                    compra.setCodigo(orden.getConsecutivo());
                    compra.setReferencia(orden.getReferencia());
                    //
                    List<OrdenDetalleMovilVo> art = new ArrayList<OrdenDetalleMovilVo>();
                    for (OrdenDetalleVO ordenDetalleVO : orden.getDetalleOrden()) {
                        OrdenDetalleMovilVo odVo = new OrdenDetalleMovilVo();
                        odVo.setId(ordenDetalleVO.getId());
                        odVo.setIdArticulo(ordenDetalleVO.getArtID());
                        odVo.setNombreArticulo(ordenDetalleVO.getArtNombre());
                        odVo.setUnidad(ordenDetalleVO.getArtUnidad());
                        odVo.setCantidad(ordenDetalleVO.getCantidad());
                        odVo.setTotalRecibido(ordenDetalleVO.getTotalRecibido());
                        odVo.setTotalPendiente(ordenDetalleVO.getTotalPendiente());
                        odVo.setPrecio(ordenDetalleVO.getPrecioUnitario());
                        art.add(odVo);
                    }
                    compra.setArticulos(art);
                    Respuesta<OrdenMovilVo> respuesta = new Respuesta<>(compra);
                    return Response.ok(respuesta).build();
                } catch (Exception e) {
                    return crearRespuestaDeError(e);
                }
            } else {
                return Response.ok(new Respuesta(Estado.error,
                        "La compra ya fue recibida."))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            }
        } else {
            return Response.ok(new Respuesta(Estado.error,
                    "Compra no encontrada"))
                    .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                    .build();
        }
    }

    @GET
    @Path("/buscarCompraPorUuid")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarCompraPorUuid(@QueryParam("uuId") String uuId) {
        OrdenVO orden = ordenImpl.buscarOrdenPorUuId(uuId, Boolean.TRUE);
        if (orden != null) {
            if (orden.getIdStatus() == OrdenEstadoEnum.POR_RECIBIR.getId()
                    || orden.getIdStatus() == OrdenEstadoEnum.RECIBIDA_PARCIAL.getId()) {
                try {
                    OrdenMovilVo compra = new OrdenMovilVo();
                    compra.setId(orden.getId());
                    compra.setCodigo(orden.getConsecutivo());
                    compra.setReferencia(orden.getReferencia());
                    //
                    List<OrdenDetalleMovilVo> art = new ArrayList<OrdenDetalleMovilVo>();
                    for (OrdenDetalleVO ordenDetalleVO : orden.getDetalleOrden()) {
                        OrdenDetalleMovilVo odVo = new OrdenDetalleMovilVo();
                        odVo.setId(ordenDetalleVO.getId());
                        odVo.setIdArticulo(ordenDetalleVO.getArtID());
                        odVo.setNombreArticulo(ordenDetalleVO.getArtNombre());
                        odVo.setUnidad(ordenDetalleVO.getArtUnidad());
                        odVo.setCantidad(ordenDetalleVO.getCantidad());
                        odVo.setTotalRecibido(ordenDetalleVO.getTotalRecibido());
                        odVo.setTotalPendiente(ordenDetalleVO.getTotalPendiente());
                        odVo.setPrecio(ordenDetalleVO.getPrecioUnitario());
                        art.add(odVo);
                    }
                    compra.setArticulos(art);
                    Respuesta<OrdenMovilVo> respuesta = new Respuesta<>(compra);
                    return Response.ok(respuesta).build();
                } catch (Exception e) {
                    return crearRespuestaDeError(e);
                }
            } else {
                return Response.ok(new Respuesta(Estado.error,
                        "La compra ya fue recibida."))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            }
        } else {
            return Response.ok(new Respuesta(Estado.error,
                    "Compra no encontrada"))
                    .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                    .build();
        }
    }

    @GET
    @Path("/traerCeldasAlmacen")
    @Produces(MediaType.APPLICATION_JSON)
    public Response traerCeldasAlmacen(@QueryParam("almacen") String almacen) {
        List<CeldaVo> celdas = invCeldaImpl.celdasPorAlmacen(almacen);
        if (celdas != null && !celdas.isEmpty()) {
            try {
                List<CeldaMovilVo> cmvs = new ArrayList<CeldaMovilVo>();
                AlmacenUbicacionMovilVo aumv = new AlmacenUbicacionMovilVo();
                aumv.setAlmacen(almacen);
                for (CeldaVo celda : celdas) {
                    CeldaMovilVo cmv = new CeldaMovilVo();
                    cmv.setId(celda.getId());
                    cmv.setCelda(celda.getCelda());
                    cmvs.add(cmv);
                }
                aumv.setCeldas(cmvs);
                Respuesta<AlmacenUbicacionMovilVo> respuesta = new Respuesta<>(aumv);
                return Response.ok(respuesta).build();

            } catch (Exception e) {
                UtilLog4j.log.error(e);
                return Response.ok(new Respuesta(Estado.error,
                        "Ocurrio un error al traer las  ubicaciones."))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            }
        } else {
            return Response.ok(new Respuesta(Estado.error,
                    "No hay registro de celdas para el almancén seleccionado."))
                    .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                    .build();
        }
    }

    @Path("/actualizarUbicacion")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarUbicacion(@FormParam("inventarioId") int inventarioId,
            @FormParam("celdaId") int celdaId,
            @FormParam("isNueva") boolean isNueva,
            @FormParam(API_KEY_PARAM) String apiKey) {
        try {
            UsuarioVO usuarioVO = getUsuario(apiKey);
            if (usuarioVO != null) {
                if (isNueva) {
                    inventarioCeldaImpl.eliminar(inventarioId, usuarioVO.getId());
                }
                inventarioCeldaImpl.guardar(inventarioId, celdaId, usuarioVO.getId());
            }
            return Response.ok(new Respuesta(Estado.ok)).build();
        } catch (Exception ex) {
            if (ex.getCause() instanceof IllegalArgumentException) {
                return Response.ok(new Respuesta(Estado.error,
                        Messages.getString("sia.inventarios.mobile.mensaje.api_key.no_valido")))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            } else if (ex.getCause() instanceof SIAException) {
                return Response.ok(new Respuesta(Estado.error, ex.getMessage()))
                        .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                        .build();
            }
            return Response.ok(new Respuesta(Estado.error,
                    "Ocurrio un error al actualizar la ubicación."))
                    .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
                    .build();
        }
    }

    @Override
    protected UsuarioVO getUsuario(String key) throws Exception {
        return apiAuthService.obtenerUsuario(key);
    }
}
