package sia.inventarios.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import sia.constantes.Constantes;
import static sia.constantes.Constantes.BOOLEAN_FALSE;
import static sia.constantes.Constantes.INV_MOVIMIENTO_TIPO_SALIDA;
import static sia.constantes.Constantes.INV_TRANSACCION_STATUS_APLICADA;
import sia.correo.impl.EnviarCorreoImpl;
import sia.excepciones.SIAException;
import sia.inventarios.audit.Audit;
import sia.inventarios.audit.AuditActions;
import sia.inventarios.log.EjbLog;
import static sia.inventarios.service.Utilitarios.crearExpresionLike;
import static sia.inventarios.service.Utilitarios.esNuloOVacio;
import sia.modelo.InvAlmacen;
import sia.modelo.InvArticulo;
import sia.modelo.InvTransaccion;
import sia.modelo.InvTransaccionArticulo;
import sia.modelo.Orden;
import sia.modelo.OrdenDetalle;
import sia.modelo.SiPlantillaHtml;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.inventarios.ArticuloCompraVO;
import sia.modelo.vo.inventarios.AvisoVO;
import sia.modelo.vo.inventarios.InventarioMovimientoVO;
import sia.modelo.vo.inventarios.InventarioVO;
import sia.modelo.vo.inventarios.TransaccionArticuloVO;
import sia.modelo.vo.inventarios.TransaccionVO;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OrdenDetalleImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.sistema.impl.SiPlantillaHtmlImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Aplimovil SA de CV
 */
//Stateless (name = "Inventarios_TransaccionService")
@Stateless
public class TransaccionImpl extends AbstractFacade<InvTransaccion> implements TransaccionRemote {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    Audit audit;

    @Inject
    TransaccionArticuloRemote transaccionArticuloService;

    @Inject
    AlmacenRemote almacenService;

    @Inject
    InventarioImpl inventarioService;

    @Inject
    InventarioMovimientoImpl inventarioMovimientoService;

    @Inject
    AvisoImpl avisoService;

    @Inject
    EnviarCorreoImpl enviarCorreoService;

    @Inject
    SiPlantillaHtmlImpl plantillaHtml;
    @Inject
    OrdenDetalleImpl ordenDetalleRemote;
    @Inject
    OrdenImpl ordenRemote;
    @Inject
    AutorizacionesOrdenImpl autorizacionesOrdenRemote;
    @Inject
    UsuarioImpl usuario;
    @Inject
    InvArticuloCampoImpl invArticuloCampoRemote;

    public TransaccionImpl() {
        super(InvTransaccion.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public InvTransaccion find(Object id) {
        return em.find(InvTransaccion.class, id);
    }

    @Override
    public void edit(InvTransaccion invTransaccion) {
        em.merge(invTransaccion);
    }

    @Override
    public List<TransaccionVO> buscarPorFiltros(TransaccionVO filtro, Integer campo) {
        return buscarPorFiltros(filtro, null, null, null, true, campo);
    }

    @Override
    public List<TransaccionVO> buscarPorFiltros(TransaccionVO filtro, Integer inicio, Integer tamanioPagina, String campoOrdenar,
            boolean esAscendente, Integer idCampo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery query = criteriaBuilder.createQuery();
        Root transaccion = query.from(InvTransaccion.class);
        Join almacen = transaccion.join("almacen");
        Join campo = almacen.join("apCampo");
        Join usuario = transaccion.join("genero");
        Join traspasoAlmacenDestino = transaccion.join("traspasoAlmacenDestino", JoinType.LEFT);
        Join usuarioGenero = transaccion.join("genero", JoinType.LEFT);
        Join usuarioModifico = transaccion.join("modifico", JoinType.LEFT);
        Order ordenarEntonces = null;
        if (campoOrdenar == null || campoOrdenar.equals("fecha")) {
            campoOrdenar = "fechaGenero";
            esAscendente = false;
            ordenarEntonces = criteriaBuilder.desc(transaccion.get("horaGenero"));
        }
        //Obtener el campo por el cual se va a ordenar la lista
        Path orderBy = transaccion.get(campoOrdenar);
        Order order = esAscendente ? criteriaBuilder.asc(orderBy) : criteriaBuilder.desc(orderBy);
        filtro.setIdCampo(idCampo);
        aplicarFiltros(filtro, criteriaBuilder, query, transaccion, almacen, traspasoAlmacenDestino, usuario);
        if (ordenarEntonces == null) {
            query.orderBy(order);
        } else {
            query.orderBy(order, ordenarEntonces);
        }
        query.select(criteriaBuilder.construct(TransaccionVO.class, transaccion.get("id"), almacen.get("id"),
                almacen.get("nombre"), transaccion.get("tipoMovimiento"), traspasoAlmacenDestino.get("id"),
                transaccion.get("fecha"), transaccion.get("numeroArticulos"), transaccion.get("notas"),
                transaccion.get("folioOrdenCompra"), transaccion.get("folioRemision"), transaccion.get("motivoRechazo"),
                transaccion.get("status"), usuarioGenero.get("id"), usuarioGenero.get("nombre"),
                usuarioModifico.get("id"), usuarioModifico.get("nombre"), transaccion.get("fechaGenero"), campo.get("id"),
                campo.get("nombre")));

        TypedQuery<TransaccionVO> typedQuery = getEntityManager().createQuery(query);
        //establecer la paginacion basados en los parametros
        if (inicio != null && tamanioPagina != null) {
            typedQuery.setFirstResult(inicio);
            typedQuery.setMaxResults(tamanioPagina);
        }

        return typedQuery.getResultList();
    }

    @Override
    public int contarPorFiltros(TransaccionVO filtro, Integer idCampo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery consulta = criteriaBuilder.createQuery();
        Root transaccion = consulta.from(InvTransaccion.class);
        Join almacen = transaccion.join("almacen");
        Join campo = almacen.join("apCampo");
        Join user = transaccion.join("genero");
        Join traspasoAlmacenDestino = transaccion.join("traspasoAlmacenDestino", JoinType.LEFT);

        aplicarFiltros(filtro, criteriaBuilder, consulta, transaccion, almacen, traspasoAlmacenDestino, user);
        consulta.select(criteriaBuilder.count(transaccion)).where(criteriaBuilder.and(criteriaBuilder.equal(campo.get("id"), idCampo)));

        return ((Long) getEntityManager().createQuery(consulta).getSingleResult()).intValue();
    }

    @Override
    public TransaccionVO buscar(Integer id) throws SIAException {
        InvTransaccion transaccion = this.find(id);
        InvAlmacen almacen = transaccion.getAlmacen();
        InvAlmacen traspasoAlmacenDestino = transaccion.getTraspasoAlmacenDestino();
        Usuario usuarioGenero = transaccion.getGenero();
        Usuario usuarioModifico = transaccion.getModifico();

        if (traspasoAlmacenDestino == null) {
            traspasoAlmacenDestino = new InvAlmacen();
        }

        if (usuarioGenero == null) {
            usuarioGenero = new Usuario();
        }

        if (usuarioModifico == null) {
            usuarioModifico = new Usuario();
        }

        return new TransaccionVO(
                transaccion.getId(),
                almacen.getId(),
                almacen.getNombre(),
                transaccion.getTipoMovimiento(),
                traspasoAlmacenDestino.getId(),
                transaccion.getFecha(),
                transaccion.getNumeroArticulos(),
                transaccion.getNotas(),
                transaccion.getFolioOrdenCompra(),
                transaccion.getFolioRemision(),
                transaccion.getMotivoRechazo(),
                transaccion.getStatus(),
                usuarioGenero.getId(),
                usuarioGenero.getNombre(),
                usuarioModifico.getId(),
                usuarioModifico.getNombre(),
                transaccion.getFechaGenero(),
                almacen.getApCampo().getId(),
                almacen.getApCampo().getNombre()
        );
    }

    @Override
    public void crear(TransaccionVO transaccionVO, List<TransaccionArticuloVO> transaccionArticulosVO,
            String username, int campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "TransaccionImpl.create()");

            validarFolioRemision(transaccionVO.getTipoMovimiento(), transaccionVO.getFolioRemision());
            // 
            transaccionVO.setStatus(Constantes.INV_TRANSACCION_STATUS_PREPARACION);
            // 
            InvTransaccion transaccion = new InvTransaccion();
            Usuario user = new Usuario(username);

            transaccion.setAlmacen(new InvAlmacen(transaccionVO.getAlmacenId()));
            if (transaccionVO.getTraspasoAlmacenDestinoId() != null) {
                transaccion.setTraspasoAlmacenDestino(new InvAlmacen(transaccionVO.getTraspasoAlmacenDestinoId()));
            }
            transaccion.setTipoMovimiento(transaccionVO.getTipoMovimiento());
            transaccion.setFecha(transaccionVO.getFecha());
            transaccion.setNumeroArticulos(transaccionVO.getNumeroArticulos());
            transaccion.setNotas(transaccionVO.getNotas());
            transaccion.setFolioOrdenCompra(transaccionVO.getFolioOrdenCompra());
            transaccion.setFolioRemision(transaccionVO.getFolioRemision());
            transaccion.setStatus(transaccionVO.getStatus());

            transaccion.setEliminado(Constantes.BOOLEAN_FALSE);
            transaccion.setGenero(user);
            transaccion.setFechaGenero(new Date());
            transaccion.setHoraGenero(new Date());

            // audit.register(AuditActions.CREATE, transaccion, user);
            super.create(transaccion);
            transaccionVO.setId(transaccion.getId());
            if (transaccionArticulosVO != null) {
                List<OrdenDetalleVO> detalleOcs = new ArrayList<OrdenDetalleVO>();
                Set<Integer> invArt = new HashSet<Integer>();
                for (TransaccionArticuloVO transaccionArticulo : transaccionArticulosVO) {
                    // actualiza el precio del articulos
                    invArticuloCampoRemote.agregarPrecioArticulo(transaccionArticulo.getArticuloId(), campo, transaccionArticulo.getPrecioUnitario(), transaccionArticulo.getIdMoneda(), username);
                    //El detalle de articulos debe ser creado en la misma transaccion // crea la lista de items de ocs
                    if (transaccion.getTipoMovimiento().equals(Constantes.INV_MOVIMIENTO_TIPO_ENTRADA)) {
                        OrdenDetalleVO detOcsVo = new OrdenDetalleVO();
                        if (transaccionArticulo.getDetalleCompraId() != null) {
                            detOcsVo.setId(transaccionArticulo.getDetalleCompraId());
                            detOcsVo.setTotalRecibido(transaccionArticulo.getNumeroUnidades());
                            detOcsVo.setFechaRecibido(new Date());
                            detOcsVo.setCantidad(transaccionArticulo.getCantidad());
                            detOcsVo.setTotalPendiente(transaccionArticulo.getTotalPendiente());
                            //para notificar
                            detOcsVo.setArtNombre(transaccionArticulo.getArticuloNombre());
                            detOcsVo.setArtDescripcion(transaccionArticulo.getArticuloNombre());
                            detalleOcs.add(detOcsVo);
                        }
                        //
                        transaccionArticulo.setNumeroUnidades(transaccionArticulo.getTotalPendiente());
                        invArt.add(transaccionArticulo.getArticuloId());
                    } else {
                        transaccionArticulo.setTransaccionId(transaccionVO.getId());
                        transaccionArticuloService.crear(transaccionArticulo, username, campo);
                    }
                }
                List<TransaccionArticuloVO> listaArtDif = new ArrayList<TransaccionArticuloVO>();
                for (Integer idArt : invArt) {
                    double total = 0;
                    TransaccionArticuloVO artDifTemp = new TransaccionArticuloVO();
                    for (TransaccionArticuloVO transaccionArticuloVO : transaccionArticulosVO) {
                        if (idArt == transaccionArticuloVO.getArticuloId()) {
                            total += transaccionArticuloVO.getTotalPendiente();
                        }
                        artDifTemp.setCantidad(transaccionArticuloVO.getCantidad());
                        artDifTemp.setTotalPendiente(transaccionArticuloVO.getTotalPendiente());
                    }
                    artDifTemp.setTransaccionId(transaccionVO.getId());
                    artDifTemp.setArticuloId(idArt);
                    artDifTemp.setNumeroUnidades(total);
                    listaArtDif.add(artDifTemp);
                }
                for (TransaccionArticuloVO tranArticuloDifVO : listaArtDif) {
                    tranArticuloDifVO.setTransaccionId(transaccionVO.getId());
                    transaccionArticuloService.crear(tranArticuloDifVO, username, campo);
                }
                // Marca la compra como recibida
                if (transaccion.getTipoMovimiento().equals(Constantes.INV_MOVIMIENTO_TIPO_ENTRADA)) {
                    OrdenVO ocs = ordenRemote.buscarOrdenPorConsecutivo(transaccion.getFolioOrdenCompra(), Constantes.BOOLEAN_TRUE);
                    if (ocs != null) {
                        ocs.setDetalleOrden(new ArrayList<OrdenDetalleVO>());
                        ocs.setDetalleOrden(detalleOcs);
                        autorizacionesOrdenRemote.marcarOrdenRecibida(user.getId(), ocs);
                    }
                    //
                    procesar(transaccion.getId(), username, campo);
                }
            }
            //
            //
            UtilLog4j.log.info(this, "Transaccion creado exitosamente.");
            //
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    @Override
    public void crearYProcesar(TransaccionVO transaccionVO, List<TransaccionArticuloVO> articulosVO, String username, int campo) throws SIAException {
        crear(transaccionVO, articulosVO, username, campo);
        procesar(transaccionVO.getId(), username, campo);
    }

    /**
     *
     * @param transaccionVO
     * @param articulosVO
     * @param username
     * @param campo
     * @throws SIAException
     */
    @Override
    public void actualizar(TransaccionVO transaccionVO, List<TransaccionArticuloVO> articulosVO,
            String username, int campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "TransaccionImpl.update()");

            InvTransaccion transaccion = this.find(transaccionVO.getId());
            Usuario usuarioLC = new Usuario(username);

            if (transaccion.getStatus().equals(Constantes.INV_TRANSACCION_STATUS_PREPARACION)) {
                transaccion.setAlmacen(new InvAlmacen(transaccionVO.getAlmacenId()));
                transaccion.setTipoMovimiento(transaccionVO.getTipoMovimiento());
                transaccion.setFecha(transaccionVO.getFecha());
                transaccion.setNumeroArticulos(transaccionVO.getNumeroArticulos());
                transaccion.setNotas(transaccionVO.getNotas());
                transaccion.setFolioOrdenCompra(transaccionVO.getFolioOrdenCompra());
                transaccion.setFolioRemision(transaccionVO.getFolioRemision());

                // NOTA: Aqui no actualizamos el status de la transaccion, ya que el cambio de status es manejado por los metodos procesar(), confirmar() y rechazar()
                transaccion.setModifico(usuarioLC);
                transaccion.setFechaModifico(new Date());
                transaccion.setHoraModifico(new Date());

                audit.register(AuditActions.UPDATE, transaccion, usuarioLC);

                super.edit(transaccion);

                if (articulosVO != null) {
                    for (TransaccionArticuloVO articuloTranVo : articulosVO) { //El detalle de articulos debe ser creado en la misma transaccion
                        articuloTranVo.setTransaccionId(transaccionVO.getId());
                        if (articuloTranVo.getId() == null) {
                            transaccionArticuloService.crear(articuloTranVo, username, campo);
                        } else {
                            transaccionArticuloService.actualizar(articuloTranVo, username, campo);
                            //
                            invArticuloCampoRemote.agregarPrecioArticulo(articuloTranVo.getArticuloId(), campo, articuloTranVo.getPrecioUnitario(), articuloTranVo.getIdMoneda(), username);
                        }
                    }
                }

                EjbLog.info("Transaccion actualizado exitosamente.");
            } else {
                throw new Exception("No es posible actualizar una transaccion una vez que ha sido procesada.");
            }
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    @Override
    public List<TransaccionVO> rastrearArticulo(String filtro) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery query = criteriaBuilder.createQuery();
        Root transaccionArticulo = query.from(InvTransaccionArticulo.class);
        Join articulo = transaccionArticulo.join("articulo");
        Join transaccion = transaccionArticulo.join("transaccion");
        Join almacen = transaccion.join("almacen");
        Join usuario = transaccion.join("genero");
        Join campo = almacen.join("apCampo");

        //Obtener el campo por el cual se va a ordenar la lista
        Path orderBy = transaccion.get("fecha");
        query.orderBy(criteriaBuilder.desc(orderBy));

        //aplicarFiltros(filtro, criteriaBuilder, query, transaccion, almacen, traspasoAlmacenDestino, usuario);
        Predicate predicate = criteriaBuilder.or(criteriaBuilder.equal(articulo.get("codigo"), filtro),
                criteriaBuilder.equal(transaccionArticulo.get("identificador"), filtro));
        query.where(criteriaBuilder.and(predicate, criteriaBuilder.equal(transaccion.get("status"), INV_TRANSACCION_STATUS_APLICADA)));

        query.select(criteriaBuilder.construct(TransaccionVO.class, transaccion.get("id"), almacen.get("id"),
                almacen.get("nombre"), transaccion.get("tipoMovimiento"), almacen.get("id"),
                transaccion.get("fecha"), transaccion.get("numeroArticulos"), transaccion.get("notas"),
                transaccion.get("folioOrdenCompra"), transaccion.get("folioRemision"), transaccion.get("motivoRechazo"),
                transaccion.get("status"), usuario.get("id"), usuario.get("nombre"),
                usuario.get("id"), usuario.get("nombre"), transaccion.get("fechaGenero"), campo.get("id"), campo.get("nombre")));

        TypedQuery<TransaccionVO> typedQuery = getEntityManager().createQuery(query);

        return typedQuery.getResultList();
    }

    public List<ArticuloCompraVO> listarArticulosPorFolioOrdenDeCompra(String folio) {
        List<OrdenDetalle> ordenes = em.createNamedQuery("OrdenDetalle.buscarPorFolioCompra", OrdenDetalle.class)
                .setParameter("folioCompra", folio)
                .getResultList();

        List<ArticuloCompraVO> articulos = new ArrayList<ArticuloCompraVO>();
        for (OrdenDetalle detalle : ordenes) {
            InvArticulo articulo = detalle.getInvArticulo();
            if (articulo != null && !detalle.isRecibido()) {
                double unidadesRecibidas = Utilitarios.obtenerNumero(detalle.getUnidadesRecibidas());
                ArticuloCompraVO articuloCompraVO = new ArticuloCompraVO();
                articuloCompraVO.setIdDetalleCompra(detalle.getId());
                articuloCompraVO.setId(articulo.getId());
                articuloCompraVO.setNombre(articulo.getNombre());
                articuloCompraVO.setCantidad(detalle.getCantidad() - unidadesRecibidas);
                articuloCompraVO.setPrecio(detalle.getPrecioUnitario());
                articuloCompraVO.setMoneda(detalle.getOrden().getMoneda().getNombre());
                articuloCompraVO.setIdMoneda(detalle.getOrden().getMoneda().getId());
                //
                articulos.add(articuloCompraVO);
            }
        }
        return articulos;
    }

    public void eliminar(Integer id, String username, Integer campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "TransaccionImpl.delete()");

            InvTransaccion transaccion = this.find(id);
            Usuario usuarioLC = new Usuario(username);

            transaccion.setModifico(usuarioLC);
            transaccion.setFechaModifico(new Date());
            transaccion.setHoraModifico(new Date());
            transaccion.setEliminado(Constantes.BOOLEAN_TRUE);

            audit.register(AuditActions.DELETE, transaccion, usuarioLC);

            super.edit(transaccion);

            UtilLog4j.log.info(this, "Transaccion eliminado exitosamente.");
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    public List<TransaccionArticuloVO> obtenerListaArticulos(Integer transaccionId, Integer campo) throws SIAException {
        TransaccionArticuloVO filtros = new TransaccionArticuloVO();
        filtros.setTransaccionId(transaccionId);

        return transaccionArticuloService.buscarPorFiltros(filtros, campo);
    }

    public List<TransaccionVO> buscarPorStatus(Integer status, int campoID) throws SIAException {
        TransaccionVO filtros = new TransaccionVO();
        filtros.setStatus(status);
        filtros.setIdCampo(campoID);
        return buscarPorFiltros(filtros, campoID);
    }

    public void procesar(Integer transaccionId, String username, Integer campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "TransaccionImpl.procesar()");

            InvTransaccion transaccion = find(transaccionId);

            // validarFolioRemision(transaccion.getTipoMovimiento(), transaccion.getFolioRemision());
            Usuario usuarioLC = new Usuario(username);

            if (transaccion.getStatus().equals(Constantes.INV_TRANSACCION_STATUS_PREPARACION)) {
                Integer nuevoStatus;

                if (transaccion.getTipoMovimiento().equals(Constantes.INV_MOVIMIENTO_TIPO_TRASPASO_SALIENTE)) {
                    nuevoStatus = Constantes.INV_TRANSACCION_STATUS_TRASPASO_PENDIENTE_REVISION;
                } else {
                    nuevoStatus = Constantes.INV_TRANSACCION_STATUS_APLICADA;
                    generarMovimientos(transaccion, username, campo);
                }

                transaccion.setStatus(nuevoStatus);

                transaccion.setModifico(usuarioLC);
                transaccion.setFechaModifico(new Date());
                transaccion.setHoraModifico(new Date());

                audit.register(AuditActions.UPDATE, transaccion, usuarioLC);

                super.edit(transaccion);

                if (nuevoStatus.equals(Constantes.INV_TRANSACCION_STATUS_TRASPASO_PENDIENTE_REVISION)) {
                    try {
                        enviarEmailSolicitudRevisionTraspaso(transaccion, username);
                    } catch (Exception ex) {
                        EjbLog.error("TransaccionImpl.procesar: El correo de solicitud de revision de traspaso no pudo ser enviado.");
                        EjbLog.error(ex.getMessage());
                    }
                }

//                if (transaccion.getTipoMovimiento().equals(INV_MOVIMIENTO_TIPO_ENTRADA)
//                        && validarFolioOrdenDeCompra(transaccion.getFolioOrdenCompra())) {
//                    procesarOrdenDeCompra(transaccion, usuarioLC, campo);
//                }
                EjbLog.info("Transaccion procesada exitosamente.");
            } else {
                throw new Exception("La transaccion ya fue procesada anteriormente.");
            }
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    public void confirmar(Integer transaccionId, String username, Integer campo) throws SIAException {
        try {
            EjbLog.info("TransaccionImpl.confirmar()");

            InvTransaccion transaccion = find(transaccionId);
            Usuario user = new Usuario(username);

            if (transaccion.getStatus().equals(Constantes.INV_TRANSACCION_STATUS_TRASPASO_PENDIENTE_REVISION)) {
                this.generarMovimientos(transaccion, username, campo);

                transaccion.setStatus(Constantes.INV_TRANSACCION_STATUS_APLICADA);

                transaccion.setModifico(user);
                transaccion.setFechaModifico(new Date());
                transaccion.setHoraModifico(new Date());

                this.audit.register(AuditActions.UPDATE, transaccion, user);

                super.edit(transaccion);

                try {
                    enviarEmailNotificacionAplicacionTraspaso(transaccion, username);
                } catch (Exception ex) {
                    EjbLog.error("TransaccionImpl.procesar: El correo de notificacion de aplicacion de traspaso no pudo ser enviado.");
                    EjbLog.error(ex.getMessage());
                }

                EjbLog.info("Transaccion procesada exitosamente.");
            } else {
                throw new SIAException("La transaccion #" + transaccionId + " no se pudo confirmar porque no esta en status de 'traspaso pendiente de revision'");
            }
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    public void rechazar(Integer transaccionId, String motivoRechazo, String username) throws SIAException {
        try {
            EjbLog.info("TransaccionImpl.rechazar()");

            InvTransaccion transaccion = find(transaccionId);
            Usuario usuarioLC = new Usuario(username);

            if (transaccion.getStatus().equals(Constantes.INV_TRANSACCION_STATUS_TRASPASO_PENDIENTE_REVISION)) {
                transaccion.setMotivoRechazo(motivoRechazo);
                transaccion.setStatus(Constantes.INV_TRANSACCION_STATUS_RECHAZADA);

                transaccion.setModifico(usuarioLC);
                transaccion.setFechaModifico(new Date());
                transaccion.setHoraModifico(new Date());

                this.audit.register(AuditActions.UPDATE, transaccion, usuarioLC);

                super.edit(transaccion);

                try {
                    enviarEmailNotificacionRechazoTraspaso(transaccion, username);
                } catch (Exception ex) {
                    EjbLog.error("TransaccionImpl.rechazar: El correo de notificacion de rechazo de traspaso no pudo ser enviado.");
                    EjbLog.error(ex.getMessage());
                }

                EjbLog.info("Transaccion rechazada exitosamente.");
            } else {
                throw new SIAException("La transaccion #" + transaccionId + " no se pudo rechazar porque no esta en status de 'traspaso pendiente de revision'");
            }
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    public boolean validarFolioOrdenDeCompra(String folio) throws SIAException {
        return em.createNamedQuery("Orden.contarPorFolioCompraEnviada", Long.class)
                .setParameter("folioCompra", folio)
                .getSingleResult() > 0;
    }

    private void generarMovimientos(InvTransaccion transaccion, String username, Integer campo) throws SIAException {
        EjbLog.info("TransaccionImpl.generarMovimientos()");

        List<TransaccionArticuloVO> listaArticulos = this.obtenerListaArticulos(transaccion.getId(), campo);
        Integer tipoMovimiento = transaccion.getTipoMovimiento();

        // Crear un movimiento de inventario por cada articulo en la transaccion de inventario
        for (TransaccionArticuloVO transaccionArticuloVO : listaArticulos) {
            InventarioVO inventarioVO = almacenService.obtenerInventario(transaccion.getAlmacen().getId(),
                    transaccionArticuloVO.getArticuloId(), username, campo);

            double numeroUnidadesMovimiento = Math.abs(transaccionArticuloVO.getNumeroUnidades()); // Asegurarse de que este numero siempre sea positivo
            double numeroUnidadesMovimientoConSigno;

            if (tipoMovimiento.equals(Constantes.INV_MOVIMIENTO_TIPO_ENTRADA)) {
                numeroUnidadesMovimientoConSigno = numeroUnidadesMovimiento; // Una entrada inventario siempre es positiva
            } else {
                numeroUnidadesMovimientoConSigno = numeroUnidadesMovimiento * -1; // Los demas movimientos son negativos, no se cuenta "traspaso entrante" porque no esta disponible para que el usuario lo seleccione
            }

            // Crear el movimiento de inventario
            InventarioMovimientoVO movimientoVO = new InventarioMovimientoVO();
            movimientoVO.setInventarioId(inventarioVO.getId());
            movimientoVO.setFecha(transaccion.getFecha());
            movimientoVO.setTipoMovimiento(transaccion.getTipoMovimiento());
            movimientoVO.setNumeroUnidades(numeroUnidadesMovimientoConSigno);
            movimientoVO.setTransaccionId(transaccion.getId());

            inventarioMovimientoService.crear(movimientoVO, username, campo);

            // Actualizar el numero de unidades en el inventario
            double numeroUnidadesNuevo = inventarioVO.getNumeroUnidades() + numeroUnidadesMovimientoConSigno;
            validarUnidades(transaccion, inventarioVO, numeroUnidadesMovimiento, numeroUnidadesNuevo, username);

            //validar el numero de unidades de la contraparte
            if (transaccion.getTraspasoAlmacenDestino() != null) {
                InventarioVO inventarioDestinoVO = almacenService.obtenerInventario(transaccion.getTraspasoAlmacenDestino().getId(), transaccionArticuloVO.getArticuloId(), username, campo);
                double numeroUnidadesNuevoDestino = inventarioDestinoVO.getNumeroUnidades() - numeroUnidadesMovimientoConSigno;
                validarUnidades(transaccion, inventarioDestinoVO, numeroUnidadesMovimiento, numeroUnidadesNuevoDestino, username);
            }

            inventarioVO.setNumeroUnidades(numeroUnidadesNuevo);
            inventarioService.actualizar(inventarioVO, username, campo);

            if (tipoMovimiento.equals(Constantes.INV_MOVIMIENTO_TIPO_TRASPASO_SALIENTE)) { // Si el movimiento es un traspaso (saliente) de inventario
                if (transaccion.getTraspasoAlmacenDestino() != null) {
                    InventarioVO inventarioEntranteVO = almacenService.obtenerInventario(transaccion.getTraspasoAlmacenDestino().getId(), transaccionArticuloVO.getArticuloId(), username, campo);

                    // Crear el movimiento contraparte para la entrada del inventario al otro almacen
                    InventarioMovimientoVO movimientoEntranteVO = new InventarioMovimientoVO();
                    movimientoEntranteVO.setInventarioId(inventarioEntranteVO.getId());
                    movimientoEntranteVO.setFecha(transaccion.getFecha());
                    movimientoEntranteVO.setTipoMovimiento(Constantes.INV_MOVIMIENTO_TIPO_TRASPASO_ENTRANTE);
                    movimientoEntranteVO.setNumeroUnidades(numeroUnidadesMovimientoConSigno);
                    movimientoEntranteVO.setTransaccionId(transaccion.getId());

                    inventarioMovimientoService.crear(movimientoEntranteVO, username, campo);

                    inventarioEntranteVO.setNumeroUnidades(inventarioEntranteVO.getNumeroUnidades() + numeroUnidadesMovimiento);
                    inventarioService.actualizar(inventarioEntranteVO, username, campo);
                } else {
                    throw new SIAException("La transaccion no tiene un almacen destino asignado.");
                }
            }
        }

        EjbLog.info("Se crearon los movimientos de inventario de la transaccion #" + transaccion.getId());
    }

    private void validarUnidades(InvTransaccion transaccion, InventarioVO inventarioVO, double numeroUnidadesMovimiento,
            double numeroUnidadesNuevo, String userName) throws SIAException {
        if (numeroUnidadesNuevo < 0) {
            enviarEmailAlertaSobregiro(transaccion, numeroUnidadesMovimiento, inventarioVO, userName);
            throw new SIAException("ALERTA DE SOBREGIRO: No es posible procesar el movimiento del inventario ya que el numero de unidades a disminuir (" + numeroUnidadesMovimiento + " unidades) del articulo \"" + inventarioVO.getArticuloNombre() + "\" en el almacen \"" + inventarioVO.getAlmacenNombre() + "\" es mayor al numero de unidades actuales disponibles (" + inventarioVO.getNumeroUnidades() + " unidades) en este inventario. Por favor revise que el inventario este al día con las existencias físicas.");
        }

        if (//inventarioVO.getMinimoUnidades() != null && 
                numeroUnidadesNuevo < inventarioVO.getMinimoUnidades()) {
            enviarEmailAlertaMinimoDeInventario(transaccion, numeroUnidadesNuevo, inventarioVO, userName);
        }

        if (//inventarioVO.getMaximoDeInventario() != null && 
                numeroUnidadesNuevo > inventarioVO.getMaximoDeInventario()) {
            //enviarEmailAlertaMaximoDeInventario(transaccion, numeroUnidadesNuevo, inventarioVO);
        }

        if (//inventarioVO.getPuntoDeReorden() != null && 
                numeroUnidadesNuevo <= inventarioVO.getPuntoDeReorden()) {
            enviarEmailAlertaPuntoDeReorden(transaccion, numeroUnidadesNuevo, inventarioVO);
        }
    }

    private void aplicarFiltros(TransaccionVO filtro, CriteriaBuilder criteriaBuilder, CriteriaQuery query, Root transaccion, Join almacen, Join traspasoAlmacenDestino, Join usuario) {
        List<Predicate> predicates = new ArrayList<Predicate>(10);

        predicates.add(criteriaBuilder.equal(transaccion.get("eliminado"), Constantes.BOOLEAN_FALSE));

        if (!esNuloOVacio(filtro.getAlmacenId())) {
            predicates.add(criteriaBuilder.equal(almacen.get("id"), filtro.getAlmacenId()));
        }

        if (!esNuloOVacio(filtro.getTipoMovimiento())) {
            predicates.add(criteriaBuilder.equal(transaccion.get("tipoMovimiento"), filtro.getTipoMovimiento()));
        }

        if (!esNuloOVacio(filtro.getTraspasoAlmacenDestinoId())) {
            predicates.add(criteriaBuilder.equal(traspasoAlmacenDestino.get("id"), filtro.getTraspasoAlmacenDestinoId()));
        }

        if (filtro.getFecha() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(transaccion.get("fecha"), filtro.getFecha()));
        }

        if (!esNuloOVacio(filtro.getNumeroArticulos())) {
            predicates.add(criteriaBuilder.equal(transaccion.get("numeroArticulos"), filtro.getNumeroArticulos()));
        }

        if (filtro.getFechaInicio() != null && filtro.getFechaFin() != null) {
            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(transaccion.get("fecha"), filtro.getFechaFin()),
                    criteriaBuilder.greaterThanOrEqualTo(transaccion.get("fecha"), filtro.getFechaInicio())));
        }

        if (filtro.getGeneroId() != null) {
            predicates.add(criteriaBuilder.equal(usuario.get("id"), filtro.getGeneroId()));
        }

        if (!esNuloOVacio(filtro.getNotas())) {
            predicates.add(criteriaBuilder.like(transaccion.get("notas"), crearExpresionLike(filtro.getNotas())));
        }

        if (!esNuloOVacio(filtro.getFolioOrdenCompra())) {
            predicates.add(criteriaBuilder.like(transaccion.get("folioOrdenCompra"), crearExpresionLike(filtro.getFolioOrdenCompra())));
        }

        if (!esNuloOVacio(filtro.getStatus())) {
            predicates.add(criteriaBuilder.equal(transaccion.get("status"), filtro.getStatus()));
        }

        query.where(predicates.toArray(new Predicate[0]));
    }

    @TransactionAttribute(REQUIRES_NEW)
    private void enviarEmailSolicitudRevisionTraspaso(InvTransaccion transaccion, String userName) {
        InvAlmacen almacen = transaccion.getAlmacen();
        InvAlmacen almacenDestino = transaccion.getTraspasoAlmacenDestino();
        Usuario usuarioGenero = transaccion.getGenero();
        Usuario usuarioResponsable1 = almacen.getResponsable1();
        Usuario usuarioResponsable2 = almacen.getResponsable2();
        String usuarioResponsable1Email = obtenerEmail(usuarioResponsable1);
        String usuarioResponsable2Email = obtenerEmail(usuarioResponsable2);
        String asunto = "Revisar traspaso de almacen " + almacen.getNombre();
        StringBuilder mensaje = new StringBuilder();

        mensaje.append("<p>El usuario ").append(usuarioGenero.getNombre()).append(" ha registrado un traspaso de inventario desde el almacén \"").append(almacen.getNombre()).append("\" y se requiere de su revisión y confirmación para poder aplicar este traspaso.</p>");
        mensaje.append("<p>Para aplicar o rechazar este traspaso, por favor ingrese al módulo de Inventarios del SIA y en la pantalla inicial del módulo verá los traspasos pendientes de revisar.");

        StringBuilder mensajeMovimiento = new StringBuilder(mensaje.toString() + obtenerLigaDeMovimiento(transaccion.getId()));
        if (almacenDestino.getResponsable1() != null) {
            Integer avisoId = avisoService.crearAviso(almacenDestino.getResponsable1().getId(),
                    AvisoVO.nuevo(asunto, mensajeMovimiento.toString()),
                    userName);
            if (!Utilitarios.esNuloOVacio(almacenDestino.getResponsable1().getEmail())) {
                enviarCorreoConPlantilla(almacenDestino.getResponsable1().getEmail(), asunto, mensajeMovimiento, avisoId);
            }
        }
        if (almacenDestino.getResponsable2() != null) {
            Integer avisoId = avisoService.crearAviso(almacenDestino.getResponsable2().getId(),
                    AvisoVO.nuevo(asunto, mensajeMovimiento.toString()),
                    userName);
            if (!Utilitarios.esNuloOVacio(almacenDestino.getResponsable2().getEmail())) {
                enviarCorreoConPlantilla(almacenDestino.getResponsable2().getEmail(), asunto, mensajeMovimiento, avisoId);
            }
        }

        if (!esNuloOVacio(usuarioResponsable1Email)) {
            EjbLog.info("TransaccionImpl.enviarEmailSolicitudRevisionTraspaso: Se envio correo de solicitud de revision de traspaso al responsable 1 de almacen: " + usuarioResponsable1Email);
            enviarCorreoConPlantilla(usuarioResponsable1Email, asunto, mensaje);
        }

        if (!esNuloOVacio(usuarioResponsable2Email)) {
            EjbLog.info("TransaccionImpl.enviarEmailSolicitudRevisionTraspaso: Se envio correo de solicitud de revision de traspaso al responsable 2 de almacen: " + usuarioResponsable1Email);
            enviarCorreoConPlantilla(usuarioResponsable2Email, asunto, mensaje);
        }
    }

    @TransactionAttribute(REQUIRES_NEW)
    private void enviarEmailNotificacionAplicacionTraspaso(InvTransaccion transaccion, String userName) {
        InvAlmacen almacen = transaccion.getAlmacen();
        InvAlmacen almacenDestino = transaccion.getTraspasoAlmacenDestino();
        Usuario usuarioGenero = transaccion.getGenero();
        Usuario usuarioModifico = transaccion.getModifico();
        String usuarioGeneroEmail = usuarioGenero.getEmail();
        String asunto = "Traspaso confirmado al almacen \"" + almacenDestino.getNombre() + "\"";
        StringBuilder mensaje = new StringBuilder();

        mensaje.append("<p>El usuario ").append(usuarioModifico.getNombre()).append(" ha confirmado el traspaso de inventario del almacén \"").append(almacen.getNombre()).append("\" al almacén \"").append(almacenDestino.getNombre()).append("\" y los movimientos de inventario han sido aplicados exitosamente en el módulo de Inventarios del SIA.</p>");
        StringBuilder mensajeMovimiento = new StringBuilder(mensaje.toString() + obtenerLigaDeMovimiento(transaccion.getId()));
        if (almacenDestino.getResponsable1() != null) {
            Integer avisoId = avisoService.crearAviso(almacenDestino.getResponsable1().getId(),
                    AvisoVO.nuevo(asunto, mensajeMovimiento.toString()),
                    userName);
            if (!Utilitarios.esNuloOVacio(almacenDestino.getResponsable1().getEmail())) {
                enviarCorreoConPlantilla(almacenDestino.getResponsable1().getEmail(), asunto, mensajeMovimiento, avisoId);
            }
        }
        if (almacenDestino.getResponsable2() != null) {
            Integer avisoId = avisoService.crearAviso(almacenDestino.getResponsable2().getId(),
                    AvisoVO.nuevo(asunto, mensajeMovimiento.toString()),
                    userName);
            if (!Utilitarios.esNuloOVacio(almacenDestino.getResponsable2().getEmail())) {
                enviarCorreoConPlantilla(almacenDestino.getResponsable2().getEmail(), asunto, mensajeMovimiento, avisoId);
            }
        }
        if (!esNuloOVacio(usuarioGeneroEmail)) {
            EjbLog.info("TransaccionImpl.enviarEmailNotificacionAplicacionTraspaso: Se envio correo de notificación de aplicación de traspaso al e-mail: " + usuarioGeneroEmail);
            enviarCorreoConPlantilla(usuarioGeneroEmail, asunto, mensaje);
        }
    }

    @TransactionAttribute(REQUIRES_NEW)
    private void enviarEmailNotificacionRechazoTraspaso(InvTransaccion transaccion, String userName) {
        InvAlmacen almacen = transaccion.getAlmacen();
        InvAlmacen almacenDestino = transaccion.getTraspasoAlmacenDestino();
        Usuario usuarioGenero = transaccion.getGenero();
        Usuario usuarioModifico = transaccion.getModifico();
        String usuarioGeneroEmail = usuarioGenero.getEmail();
        String asunto = "Traspaso rechazado al almacen \"" + almacenDestino.getNombre() + "\"";
        StringBuilder mensaje = new StringBuilder();

        mensaje.append("<p>El usuario ").append(usuarioModifico.getNombre()).append(" ha rechazado el traspaso de inventario del almacén \"").append(almacen.getNombre()).append("\" al almacén \"").append(almacenDestino.getNombre()).append("\" por el siguiente motivo:<br /><br />")
                .append("<em>").append(transaccion.getMotivoRechazo()).append("</em>")
                .append("</p>");

        StringBuilder mensajeMovimiento = new StringBuilder(mensaje.toString() + obtenerLigaDeMovimiento(transaccion.getId()));
        if (almacenDestino.getResponsable1() != null) {
            Integer avisoId = avisoService.crearAviso(almacenDestino.getResponsable1().getId(),
                    AvisoVO.nuevo(asunto, mensajeMovimiento.toString()),
                    userName);
            if (!Utilitarios.esNuloOVacio(almacenDestino.getResponsable1().getEmail())) {
                enviarCorreoConPlantilla(almacenDestino.getResponsable1().getEmail(), asunto, mensajeMovimiento, avisoId);
            }
        }
        if (almacenDestino.getResponsable2() != null) {
            Integer avisoId = avisoService.crearAviso(almacenDestino.getResponsable2().getId(),
                    AvisoVO.nuevo(asunto, mensajeMovimiento.toString()),
                    userName);
            if (!Utilitarios.esNuloOVacio(almacenDestino.getResponsable2().getEmail())) {
                enviarCorreoConPlantilla(almacenDestino.getResponsable2().getEmail(), asunto, mensajeMovimiento, avisoId);
            }
        }

        if (!esNuloOVacio(usuarioGeneroEmail)) {
            EjbLog.info("TransaccionImpl.enviarEmailNotificacionRechazoTraspaso: Se envio correo de notificación de rechazo de traspaso al e-mail: " + usuarioGeneroEmail);
            enviarCorreoConPlantilla(usuarioGeneroEmail, asunto, mensaje);
        }
    }

    @TransactionAttribute(REQUIRES_NEW)
    private void enviarEmailAlertaSobregiro(InvTransaccion transaccion, double numeroUnidades, InventarioVO inventarioVO, String userName) {
        InvAlmacen almacen = transaccion.getAlmacen();
        Usuario usuarioGenero = transaccion.getGenero();
        String asunto = "Alerta de Sobregiro en Almacen \"" + almacen.getNombre() + "\"";
        StringBuilder mensaje = new StringBuilder();
        String emailResponsable1 = obtenerEmail(transaccion.getAlmacen().getResponsable1());
        String emailResponsable2 = obtenerEmail(transaccion.getAlmacen().getResponsable2());

        mensaje.append("<p>El usuario ").append(usuarioGenero.getNombre()).append(" intento realizar un sobregiro en el inventario del articulo \"")
                .append(inventarioVO.getArticuloNombre()).append("\" en el almacén \"").append(almacen.getNombre()).append("\" por un intento de disminuir ")
                .append(numeroUnidades).append(" unidades, cuando solo habia ").append(inventarioVO.getNumeroUnidades())
                .append(" unidades disponibles en este inventario.</p>");

        Boolean emailEnviado = false;

        if (almacen.getSupervisor() != null) {
            String mensajeSupervisor = mensaje.toString() + obtenerLigaDeMovimiento(transaccion.getId());
            Integer avisoId = avisoService.crearAviso(almacen.getSupervisor().getId(),
                    AvisoVO.nuevo(asunto, mensajeSupervisor),
                    userName);
            if (!Utilitarios.esNuloOVacio(almacen.getSupervisor().getEmail())) {
                enviarCorreoConPlantilla(almacen.getSupervisor().getEmail(), asunto, new StringBuilder(mensajeSupervisor), avisoId);
            }
        }

        if (!Utilitarios.esNuloOVacio(emailResponsable1)) {
            emailEnviado = enviarCorreoConPlantilla(emailResponsable1, asunto, mensaje);
        }

        if (!Utilitarios.esNuloOVacio(emailResponsable2)) {
            emailEnviado = enviarCorreoConPlantilla(emailResponsable2, asunto, mensaje);
        }

        if (emailEnviado) {
            EjbLog.info("TransaccionImpl.enviarEmailAlertaSobregiro: Se envio correo de alerta de sobregiro al supervisor.");
        }
    }

    private void enviarEmailAlertaMinimoDeInventario(InvTransaccion transaccion, double numeroUnidades, InventarioVO inventarioVO, String userName) {
        InvAlmacen almacen = transaccion.getAlmacen();
        StringBuilder mensaje = new StringBuilder();
        String asunto = "Alerta: Inventario Mínimo Alcanzado";
        Usuario responsable1 = transaccion.getAlmacen().getResponsable1();
        String emailResponsable1 = responsable1.getEmail();
        Usuario responsable2 = transaccion.getAlmacen().getResponsable2();
        String emailResponsable2 = responsable2.getEmail();

        mensaje.append("<p> El número de unidades del inventario de ")
                .append(inventarioVO.getArticuloNombre())
                .append(" en el almacén ")
                .append(almacen.getNombre())
                .append(" ha llegado a ")
                .append(numeroUnidades)
                .append(" unidades, que es menor al mínimo establecido de ")
                .append(inventarioVO.getMinimoUnidades())
                .append(" unidades. Favor de revisarlo.</p>");

        Boolean emailEnviado = false;

        mensaje.append(obtenerLigaDeMovimiento(transaccion.getId()));
        Integer avisoId = avisoService.crearAviso(responsable1.getId(), AvisoVO.nuevo(asunto, mensaje.toString()),
                userName);

        if (!Utilitarios.esNuloOVacio(emailResponsable1)) {
            emailEnviado = enviarCorreoConPlantilla(emailResponsable1, asunto, mensaje, avisoId);
        }

        avisoId = avisoService.crearAviso(responsable2.getId(), AvisoVO.nuevo(asunto, mensaje.toString()),
                userName);
        if (!Utilitarios.esNuloOVacio(emailResponsable2)) {
            emailEnviado = enviarCorreoConPlantilla(emailResponsable2, asunto, mensaje, avisoId);
        }

        if (emailEnviado) {
            EjbLog.info("TransaccionImpl.enviarEmailAlertaMinimoDeInventario: Se envio correo de alerta de mínimo de inventario al supervisor.");
        }
    }

    private void enviarEmailAlertaMaximoDeInventario(InvTransaccion transaccion, double numeroUnidades, InventarioVO inventarioVO) {
        InvAlmacen almacen = transaccion.getAlmacen();
        StringBuilder mensaje = new StringBuilder();
        String asunto = "Alerta: Inventario Máximo Alcanzado";
        Usuario responsable1 = transaccion.getAlmacen().getResponsable1();
        Usuario responsable2 = transaccion.getAlmacen().getResponsable2();
        String emailResponsable1 = null;
        String emailResponsable2 = null;

        if (responsable1 != null) {
            emailResponsable1 = responsable1.getEmail();
        }
        if (responsable2 != null) {
            emailResponsable2 = responsable2.getEmail();
        }

        mensaje.append("<p> El número de unidades del inventario de ")
                .append(inventarioVO.getArticuloNombre())
                .append(" en el almacén ")
                .append(almacen.getNombre())
                .append(" ha llegado a ")
                .append(numeroUnidades)
                .append(" unidades, que sobrepasa el máximo establecido de ")
                .append(inventarioVO.getMaximoDeInventario())
                .append(" unidades. Favor de revisarlo.</p>");

        Boolean emailEnviado = false;

        if (!Utilitarios.esNuloOVacio(emailResponsable1)) {
            emailEnviado = enviarCorreoConPlantilla(emailResponsable1, asunto, mensaje);
        }

        if (!Utilitarios.esNuloOVacio(emailResponsable2)) {
            emailEnviado = enviarCorreoConPlantilla(emailResponsable2, asunto, mensaje);
        }

        if (emailEnviado) {
            EjbLog.info("TransaccionImpl.enviarEmailAlertaMaximoDeInventario: Se envio correo de alerta de máximo de inventario al supervisor.");
        }
    }

    private void enviarEmailAlertaPuntoDeReorden(InvTransaccion transaccion, double numeroUnidades, InventarioVO inventarioVO) {
        InvAlmacen almacen = transaccion.getAlmacen();
        String asunto = "Alerta: Punto de Reorden Alcanzado";
        StringBuilder mensaje = new StringBuilder();
        String emailResponsable1 = obtenerEmail(transaccion.getAlmacen().getResponsable1());
        String emailResponsable2 = obtenerEmail(transaccion.getAlmacen().getResponsable2());

        Boolean emailEnviado = false;

        mensaje.append("<p>Se le recomienda generar una orden de compra del artículo ")
                .append(inventarioVO.getArticuloNombre())
                .append(" para el almacén ")
                .append(almacen.getNombre())
                .append(" el cual tiene actualmente ")
                .append(numeroUnidades)
                .append(" unidades.</p>");

        if (!Utilitarios.esNuloOVacio(emailResponsable1)) {
            emailEnviado = enviarCorreoConPlantilla(emailResponsable1, asunto, mensaje);
        }

        if (!Utilitarios.esNuloOVacio(emailResponsable2)) {
            emailEnviado = enviarCorreoConPlantilla(emailResponsable2, asunto, mensaje);
        }

        if (emailEnviado) {
            EjbLog.info("TransaccionImpl.enviarEmailAlertaPuntoDeReorden: Se envio correo de alerta de punto de reorden al supervisor.");
        }
    }

    private boolean enviarCorreoConPlantilla(String para, String asunto, StringBuilder mensaje) {
        return enviarCorreoConPlantilla(para, asunto, mensaje, null);
    }

    private boolean enviarCorreoConPlantilla(String para, String asunto, StringBuilder mensaje, Integer avisoId) {
        SiPlantillaHtml plantilla = plantillaHtml.find(1);
        StringBuilder cuerpo = new StringBuilder();
        cuerpo.append(plantilla.getInicio());
        cuerpo.append(mensaje);
        cuerpo.append(plantilla.getFin());
        if (avisoId != null && avisoId != 0) {
            cuerpo.append("<hr/>");
            cuerpo.append("<div>Para consultar este aviso en el sistema, de clic en la siguiente liga:</div>");
            String url = avisoService.obtenerAvisoUrl(avisoId);
            mensaje.append("<a target=\"_blank\" href=\"" + url + "\">" + url + "</a>");
        }
        return enviarCorreoService.enviarCorreoIhsa(para, "", "", asunto, cuerpo);
    }

    private String obtenerLigaDeMovimiento(Integer movimientoId) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("<hr/>");
        mensaje.append("<div>Para consultar este movimiento en el sistema, de clic en la siguiente liga:</div>");
        String url = avisoService.obtenerAvisoMovimientoUrl(movimientoId);
        mensaje.append("<a target=\"_blank\" href=\"" + url + "\">" + url + "</a>");
        return mensaje.toString();
    }

    private String obtenerEmail(Usuario usuario) {
        if (usuario != null) {
            return usuario.getEmail();
        }
        return null;
    }

    public void crear(TransaccionVO transaccionVO, String username, int campo) throws SIAException {
        crear(transaccionVO, null, username, campo);
    }

    public void rechazar(Integer movimientoId, String userName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void actualizar(TransaccionVO transaccionVO, String username, int campo) throws SIAException {
        actualizar(transaccionVO, null, username, campo);
    }

    private void procesarOrdenDeCompra(InvTransaccion transaccion,
            Usuario usuario, int campo) throws SIAException {
        List<TransaccionArticuloVO> listaArticulos = this.obtenerListaArticulos(transaccion.getId(), campo);
        Orden orden = obtenerOrden(transaccion.getFolioOrdenCompra());
        boolean completa = true;
        for (TransaccionArticuloVO articuloVO : listaArticulos) {
            completa = actualizarDetalleOrdenDeCompra(orden.getId(), articuloVO, usuario) && completa;
        }
        if (completa) {
            completarOrdenDeCompra(orden, usuario);
        }
    }

    private void completarOrdenDeCompra(Orden orden, Usuario usuario) {
        orden.setCompleta(Constantes.BOOLEAN_TRUE);
        orden.setModifico(usuario);
        orden.setFechaModifico(new Date());
        orden.setHoraModifico(new Date());
        ordenRemote.editarOrden(orden);
    }

    private boolean actualizarDetalleOrdenDeCompra(Integer ordenId, TransaccionArticuloVO articuloVO, Usuario usuario) {
        OrdenDetalle detalle = obtenerOrdenDetalle(ordenId, articuloVO.getArticuloId());
        if (detalle != null) {
            double recibidasTotal = articuloVO.getNumeroUnidades() + Utilitarios.obtenerNumero(detalle.getUnidadesRecibidas());
            int cantidadTotal = detalle.getCantidad().intValue();
//	    if (recibidasTotal > cantidadTotal) {
//		throw new IllegalArgumentException(String.format("No se puede procesar el movimiento ya que el numero de unidades especificado del articulo "
//			+ "%s es mayor al numero de %d unidades pendientes por recibirse en la orden de compra.", articuloVO.getArticuloNombre(), recibidasTotal));
//	    }
            detalle.setUnidadesRecibidas(recibidasTotal);
            detalle.setModifico(usuario);
            detalle.setFechaModifico(new Date());
            detalle.setHoraModifico(new Date());
            ordenDetalleRemote.editar(detalle);
            return recibidasTotal >= cantidadTotal;
        }
        return true;
    }

    private Orden obtenerOrden(String folioOrdenCompra) {
        return getEntityManager().createNamedQuery("Orden.buscarPorFolioCompra", Orden.class)
                .setParameter("folioCompra", folioOrdenCompra)
                .getSingleResult();
    }

    private OrdenDetalle obtenerOrdenDetalle(Integer ordenId, Integer articuloId) {
        try {
            return getEntityManager().createNamedQuery("OrdenDetalle.buscarPorOrdenIdYArticuloId", OrdenDetalle.class)
                    .setParameter(1, ordenId)
                    .setParameter(2, articuloId)
                    .setParameter(3, Constantes.BOOLEAN_FALSE)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    private void validarFolioRemision(Integer tipoMovimiento, String folioRemision) throws SIAException {
        if (INV_MOVIMIENTO_TIPO_SALIDA.equals(tipoMovimiento)) {
            int transactionId = buscarTransaccionConFolioRemisionAplicado(folioRemision);
            if (transactionId != 0) {
                throw new SIAException(String.format("El folio de remisión %s ya se utilizo anteriormente por el movimiento #%d", folioRemision, transactionId));
            }
        }
    }

    private int buscarTransaccionConFolioRemisionAplicado(String folioRemision) {

        List<Integer> list = em.createNamedQuery("InvTransaccion.buscarPorFolioDeRemisionAplicado", Integer.class)
                .setParameter(1, folioRemision)
                .setParameter(2, BOOLEAN_FALSE)
                .getResultList();
        return list.isEmpty() ? 0 : list.get(0);
    }

    public List<TransaccionArticuloVO> traerPorTrasaccionId(int idTransaccion, int idCampo) {
        String c = "SELECT ta.id, ta.transaccion, ta.articulo , a.nombre , ta.numero_unidades , ta.identificador, ca.precio , m.id, m.siglas FROM inv_transaccion_articulo ta\n"
                + "	inner join inv_articulo  a on ta.articulo  = a.id \n"
                + "	INNER JOIN inv_articulo_campo ca ON a.id = ca.inv_articulo\n"
                + "	left join moneda m on ca.moneda  = m.id \n"
                + "WHERE ta.transaccion = " + idTransaccion
                + "AND ca.ap_campo = " + idCampo
                + "AND ca.eliminado = false";

        List<Object[]> lista = em.createNativeQuery(c).getResultList();
        List<TransaccionArticuloVO> trans = new ArrayList<TransaccionArticuloVO>();

        for (Object[] objects : lista) {
            TransaccionArticuloVO ta = new TransaccionArticuloVO();
            ta.setId((Integer) objects[0]);
            ta.setTransaccionId((Integer) objects[1]);
            ta.setArticuloId((Integer) objects[2]);
            ta.setArticuloNombre((String) objects[3]);
            ta.setNumeroUnidades(objects[4] != null ? ((BigDecimal) objects[4]).doubleValue() : 0.0);
            ta.setIdentificador((String) objects[5]);
            ta.setPrecioUnitario(objects[6] != null ? ((BigDecimal) objects[6]).doubleValue() : 0.0);
            ta.setIdMoneda(objects[7] != null ? (Integer) objects[7] : 0);
            ta.setMoneda((String) objects[8]);
            trans.add(ta);
        }
        return trans;
    }

    public void crearConciliar(TransaccionVO transaccionVO, List<TransaccionArticuloVO> transaccionArticulosVO, String username, int campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "TransaccionImpl.create()");

            validarFolioRemision(transaccionVO.getTipoMovimiento(), transaccionVO.getFolioRemision());
            // 
            transaccionVO.setStatus(Constantes.INV_TRANSACCION_STATUS_PREPARACION);
            // 
            InvTransaccion transaccion = new InvTransaccion();
            Usuario user = new Usuario(username);

            transaccion.setAlmacen(new InvAlmacen(transaccionVO.getAlmacenId()));
            if (transaccionVO.getTraspasoAlmacenDestinoId() != null) {
                transaccion.setTraspasoAlmacenDestino(new InvAlmacen(transaccionVO.getTraspasoAlmacenDestinoId()));
            }
            transaccion.setTipoMovimiento(transaccionVO.getTipoMovimiento());
            transaccion.setFecha(transaccionVO.getFecha());
            transaccion.setNumeroArticulos(transaccionVO.getNumeroArticulos());
            transaccion.setNotas(transaccionVO.getNotas());
            transaccion.setFolioOrdenCompra(transaccionVO.getFolioOrdenCompra());
            transaccion.setFolioRemision(transaccionVO.getFolioRemision());
            transaccion.setStatus(transaccionVO.getStatus());

            transaccion.setEliminado(Constantes.BOOLEAN_FALSE);
            transaccion.setGenero(user);
            transaccion.setFechaGenero(new Date());
            transaccion.setHoraGenero(new Date());

            // audit.register(AuditActions.CREATE, transaccion, user);
            super.create(transaccion);
            transaccionVO.setId(transaccion.getId());
            if (transaccionArticulosVO != null) {
                for (TransaccionArticuloVO transaccionArticulo : transaccionArticulosVO) {
                    transaccionArticulo.setTransaccionId(transaccionVO.getId());
                    transaccionArticuloService.crear(transaccionArticulo, username, campo);
                }
            }
            UtilLog4j.log.info(this, "Transaccion creado exitosamente.");
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

}
