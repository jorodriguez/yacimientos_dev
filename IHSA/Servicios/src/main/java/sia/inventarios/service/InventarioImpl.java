package sia.inventarios.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import sia.constantes.Constantes;
import sia.correo.impl.EnviarCorreoImpl;
import sia.excepciones.SIAException;
import sia.inventarios.audit.Audit;
import sia.inventarios.audit.AuditActions;
import sia.inventarios.log.EjbLog;
import static sia.inventarios.service.Utilitarios.esNuloOVacio;
import sia.modelo.InvAlmacen;
import sia.modelo.InvArticulo;
import sia.modelo.InvInventario;
import sia.modelo.SiPlantillaHtml;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.inventarios.CeldaVo;
import sia.modelo.vo.inventarios.InventarioMovimientoVO;
import sia.modelo.vo.inventarios.InventarioVO;
import sia.modelo.vo.inventarios.TransaccionArticuloVO;
import sia.modelo.vo.inventarios.TransaccionVO;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiPlantillaHtmlImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Aplimovil SA de CV
 */
//Stateless (name = "Inventarios_InventarioService")
@Stateless
public class InventarioImpl extends AbstractFacade<InvInventario> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    Audit audit;

    @Inject
    TransaccionRemote transaccionService;

    @Inject
    InventarioMovimientoImpl inventarioMovimientoService;

    @Inject
    AlmacenRemote almacenService;

    @Inject
    ArticuloRemote articuloService;

    @Inject
    UsuarioImpl usuarioService;

    @Inject
    InvCeldaImpl invCeldaLocal;

    @Inject
    EnviarCorreoImpl enviarCorreoService;

    @Inject
    SiPlantillaHtmlImpl plantillaHtml;

    @Inject
    InvInventarioCeldaImpl invInventarioCeldaLocal;

    public InventarioImpl() {
        super(InvInventario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<InventarioVO> buscarPorFiltros(InventarioVO filtro, Integer campo) {
        return buscarPorFiltros(filtro, null, null, null, true, campo);
    }

    public List<InventarioVO> buscarPorFiltros(InventarioVO filtro, Integer inicio, Integer tamanioPagina, String campoOrdenar,
            boolean esAscendente, Integer idCampo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root inventario = criteriaQuery.from(InvInventario.class);
        Join almacen = inventario.join("almacen");
        Join articulo = inventario.join("articulo");

        //Obtener el campo por el cual se va a ordenar la lista
        Path orderBy = campoOrdenar == null ? inventario.get("id") : inventario.get(campoOrdenar);
        Order order = esAscendente ? criteriaBuilder.asc(orderBy) : criteriaBuilder.desc(orderBy);
        //
        aplicarFiltros(filtro, criteriaBuilder, criteriaQuery, inventario, almacen, articulo);
        criteriaQuery.orderBy(order);
        //
        //
        criteriaQuery.select(criteriaBuilder.construct(InventarioVO.class,
                inventario.get("id"),
                almacen.get("id"),
                almacen.get("nombre"),
                articulo.get("id"),
                articulo.get("nombre"),
                inventario.get("numeroUnidades"),
                inventario.get("minimoUnidades"), inventario.get("maximoDeInventario"),
                inventario.get("puntoDeReorden"),
                inventario.get("fechaUltimaRevision")));

        TypedQuery<InventarioVO> typedQuery = getEntityManager().createQuery(criteriaQuery);

        //establecer la paginacion basados en los parametros
        if (inicio != null && tamanioPagina != null) {
            typedQuery.setFirstResult(inicio);
            typedQuery.setMaxResults(tamanioPagina);
        }

        return typedQuery.getResultList();
    }

    public int contarPorFiltros(InventarioVO filtro, Integer idCampo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery query = criteriaBuilder.createQuery();
        Root inventario = query.from(InvInventario.class);
        Join almacen = inventario.join("almacen");
        Join campo = almacen.join("apCampo");
        Join articulo = inventario.join("articulo");

        aplicarFiltros(filtro, criteriaBuilder, query, inventario, almacen, articulo);
        query.select(criteriaBuilder.count(inventario)).where(criteriaBuilder.equal(inventario.get("eliminado"), Constantes.BOOLEAN_FALSE),
                criteriaBuilder.and(criteriaBuilder.equal(campo.get("id"), idCampo)));
        return ((Long) getEntityManager().createQuery(query).getSingleResult()).intValue();
    }

    public InventarioVO buscar(Integer id) throws SIAException {
        InvInventario inventario = this.find(id);
        InvAlmacen almacen = inventario.getAlmacen();
        InvArticulo articulo = inventario.getArticulo();
        return new InventarioVO(
                inventario.getId(),
                almacen.getId(), almacen.getNombre(),
                articulo.getId(), articulo.getNombre(),
                inventario.getNumeroUnidades(),
                inventario.getMinimoUnidades(),
                inventario.getMaximoDeInventario(),
                inventario.getPuntoDeReorden(),
                inventario.getFechaUltimaRevision()
        );

    }

    public void crear(InventarioVO inventarioVO, String username, int campo) throws SIAException {
        try {
            EjbLog.info("InventarioService.create()");

            InvInventario inventario = new InvInventario();
            Usuario usuario = new Usuario(username);

            inventario.setAlmacen(new InvAlmacen(inventarioVO.getAlmacenId()));
            inventario.setArticulo(new InvArticulo(inventarioVO.getArticuloId()));
            inventario.setNumeroUnidades(inventarioVO.getNumeroUnidades());
            inventario.setMinimoUnidades(inventarioVO.getMinimoUnidades());
            inventario.setMaximoDeInventario(inventarioVO.getMaximoDeInventario());
            inventario.setPuntoDeReorden(inventarioVO.getPuntoDeReorden());
            inventario.setFechaUltimaRevision(inventarioVO.getFechaUltimaRevision());
            //
            //
            inventario.setEliminado(Constantes.BOOLEAN_FALSE);
            inventario.setGenero(usuario);
            inventario.setFechaGenero(new Date());
            inventario.setHoraGenero(new Date());

            this.audit.register(AuditActions.CREATE, inventario, usuario);

            super.create(inventario);

            inventarioVO.setId(inventario.getId());
            //
            for (CeldaVo celda : inventarioVO.getCeldas()) {
                invInventarioCeldaLocal.guardar(inventario.getId(), celda.getId(), username);
            }

            EjbLog.info("Inventario creado exitosamente.");
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    public void actualizar(InventarioVO inventarioVO, String username, int campo) throws SIAException {
        try {
            EjbLog.info("InventarioService.update()");

            InvInventario inventario = this.find(inventarioVO.getId());
            // Usuario usuario = obtenerUsuario(this.em, username);

            inventario.setAlmacen(new InvAlmacen(inventarioVO.getAlmacenId()));
            inventario.setArticulo(new InvArticulo(inventarioVO.getArticuloId()));
            inventario.setNumeroUnidades(inventarioVO.getNumeroUnidades());
            inventario.setMinimoUnidades(inventarioVO.getMinimoUnidades());
            inventario.setFechaUltimaRevision(inventarioVO.getFechaUltimaRevision());
            inventario.setModifico(new Usuario(username));
            inventario.setMaximoDeInventario(inventarioVO.getMaximoDeInventario());
            inventario.setPuntoDeReorden(inventarioVO.getPuntoDeReorden());
            inventario.setFechaModifico(new Date());
            inventario.setHoraModifico(new Date());

            this.audit.register(AuditActions.UPDATE, inventario, new Usuario(username));

            super.edit(inventario);

            // Enviar alertas a responsables de almacen si numeroUnidades es menor a minimoUnidades
            if (inventarioVO.getNumeroUnidades() < inventarioVO.getMinimoUnidades()) {
                this.enviarEmailAlertaMinimoInventario(inventarioVO);
            }
            // elimina las ubicaciones actuales
            if (inventarioVO.getCeldas() != null && !inventarioVO.getCeldas().isEmpty()) {
                invInventarioCeldaLocal.eliminar(inventario.getId(), username);
            }
            // guarda las ubicaciones            
            for (CeldaVo celda : inventarioVO.getCeldas()) {
                invInventarioCeldaLocal.guardar(inventario.getId(), celda.getId(), username);
            }

            EjbLog.info("Inventario actualizado exitosamente.");
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    public void eliminar(Integer id, String username, Integer campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "InventarioService.delete()");

            InvInventario inventario = this.find(id);
            Usuario usuario = new Usuario(username);

            inventario.setModifico(usuario);
            inventario.setFechaModifico(new Date());
            inventario.setHoraModifico(new Date());
            inventario.setEliminado(Constantes.BOOLEAN_TRUE);

            this.audit.register(AuditActions.DELETE, inventario, usuario);

            super.edit(inventario);

            UtilLog4j.log.info(this, "Inventario eliminado exitosamente.");
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    public void conciliar(Integer inventarioId, double unidadesReales, String notas, String username, Integer campo) throws SIAException {
        try {
            InventarioVO inventarioVO = this.buscar(inventarioId);

            if (inventarioVO.getNumeroUnidades() != unidadesReales) {
                if (unidadesReales >= 0) {
                    double diferenciaUnidades = unidadesReales - inventarioVO.getNumeroUnidades();
                    Integer tipoMovimiento;

                    if (diferenciaUnidades < 0) {
                        tipoMovimiento = Constantes.INV_MOVIMIENTO_TIPO_SALIDA;
                    } else {
                        tipoMovimiento = Constantes.INV_MOVIMIENTO_TIPO_ENTRADA;
                    }

                    TransaccionVO transaccionVO = new TransaccionVO();
                    transaccionVO.setAlmacenId(inventarioVO.getAlmacenId());
                    transaccionVO.setTipoMovimiento(tipoMovimiento);
                    transaccionVO.setFecha(new Date());
                    transaccionVO.setNumeroArticulos(0);//el método TransaccionArticuloImpl#crear autoincrementa el numero
                    transaccionVO.setNotas(notas);

                    List<TransaccionArticuloVO> transaccionArticuloVOs = new ArrayList<>(1);
                    TransaccionArticuloVO transaccionArticuloVO = new TransaccionArticuloVO();
                    transaccionArticuloVO.setArticuloId(inventarioVO.getArticuloId());
                    transaccionArticuloVO.setNumeroUnidades(diferenciaUnidades);//unidadesReales);
                    transaccionArticuloVOs.add(transaccionArticuloVO);
                    //
                    transaccionService.crearConciliar(transaccionVO, transaccionArticuloVOs, username, campo);
                    transaccionService.procesar(transaccionVO.getId(), username, campo);

                    // Actualizar fecha de ultima revision de inventario
                    inventarioVO.setNumeroUnidades(unidadesReales);
                    inventarioVO.setFechaUltimaRevision(new Date());
                    this.actualizar(inventarioVO, username, campo);
                } else {
                    throw new Exception("El numero de unidades reales debe ser mayor o igual a cero.");
                }
            } else {
                throw new Exception("No es necesario conciliar el inventario ya que numero de unidades reales es igual al numero de unidades actuales.");
            }
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    public List<InventarioMovimientoVO> obtenerMovimientos(Integer inventarioId, Integer campo) {
        InventarioMovimientoVO filtro = new InventarioMovimientoVO();
        filtro.setInventarioId(inventarioId);

        return inventarioMovimientoService.buscarPorFiltros(filtro, campo);
    }

    private void aplicarFiltros(InventarioVO filtro, CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
            Root inventario, Join almacen, Join articulo) {
        List<Predicate> predicates = new ArrayList<Predicate>(6);

        predicates.add(criteriaBuilder.equal(inventario.get("eliminado"), Constantes.BOOLEAN_FALSE));

        if (!esNuloOVacio(filtro.getAlmacenId())) {
            predicates.add(criteriaBuilder.equal(almacen.get("id"), filtro.getAlmacenId()));
        }

        if (!esNuloOVacio(filtro.getArticuloId())) {
            predicates.add(criteriaBuilder.equal(articulo.get("id"), filtro.getArticuloId()));
        }

        if (!esNuloOVacio(filtro.getNumeroUnidades())) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(inventario.get("numeroUnidades"), filtro.getNumeroUnidades()));
        }

        if (!esNuloOVacio(filtro.getMinimoUnidades())) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(inventario.get("minimoUnidades"), filtro.getMinimoUnidades()));
        }

        if (filtro.getFechaUltimaRevision() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(inventario.get("fechaUltimaRevision"), filtro.getFechaUltimaRevision()));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));
    }

    @TransactionAttribute(REQUIRES_NEW)
    private void enviarEmailAlertaMinimoInventario(InventarioVO inventarioVO) {
        InvAlmacen almacen = almacenService.find(inventarioVO.getAlmacenId());
        Usuario usuarioResponsable1 = almacen.getResponsable1();
        Usuario usuarioResponsable2 = almacen.getResponsable2();
        String usuarioResponsable1Email = usuarioResponsable1.getEmail();
        String usuarioResponsable2Email = usuarioResponsable2.getEmail();
        String asunto = "Alerta: Inventario Mínimo Alcanzado";
        StringBuilder mensaje = new StringBuilder();
        InvArticulo articulo = articuloService.find(inventarioVO.getArticuloId());

        mensaje.append("<p>El número de unidades del inventario de")
                .append(articulo.getNombre())
                .append(" en el almacén")
                .append(almacen.getNombre())
                .append(" ha llegado a ")
                .append(inventarioVO.getNumeroUnidades())
                .append(" que esta por debajo del mínimo establecido de ")
                .append(inventarioVO.getMinimoUnidades())
                .append(". Favor de revisarlo.</p>");

        Boolean emailEnviado = false;

        if (!esNuloOVacio(usuarioResponsable1Email)) {
            emailEnviado = enviarCorreoConPlantilla(usuarioResponsable1Email, asunto, mensaje);
        }

        if (!esNuloOVacio(usuarioResponsable2Email)) {
            emailEnviado = enviarCorreoConPlantilla(usuarioResponsable2Email, asunto, mensaje);
        }

        if (emailEnviado) {
            EjbLog.info("InventarioImpl.enviarEmailAlertaMinimoInventario: Se envio correo de alerta de minimo de inventario al responsable(s) del almacen.");
        }
    }

    private boolean enviarCorreoConPlantilla(String para, String asunto, StringBuilder mensaje) {
        SiPlantillaHtml plantilla = plantillaHtml.find(1);
        StringBuilder cuerpo = new StringBuilder();
        cuerpo.append(plantilla.getInicio());
        cuerpo.append(mensaje);
        cuerpo.append(plantilla.getFin());
        return enviarCorreoService.enviarCorreoIhsa(para, "", "", asunto, cuerpo, new byte[0]);
    }

    public List<InventarioVO> traerInventario(InventarioVO inventarioVO, int campo) {
        String cons = " SELECT i.id, a.nombre, ar.nombre, i.numero_unidades, i.minimo_unidades, i.maximo_de_inventario, coalesce(i.punto_de_reorden, 0), i.fecha_ultima_revision,\n"
                + "	(SELECT coalesce(string_agg(r.codigo || p.codigo || c.codigo, ','), '') as celda  from inv_inventario_celda ic \n"
                + "                	inner join inv_celda c on ic.inv_celda = c.id \n"
                + "                	inner join inv_rack r on c.inv_rack = r.id \n"
                + "                	inner join inv_piso p on c.inv_piso = p.id \n"
                + "                where ic.inv_inventario = i.id \n"
                + "                and ic.eliminado = false) \n"
                + " from inv_inventario i \n"
                + "	inner join inv_almacen a on i.almacen = a.id \n"
                + "	inner join inv_articulo ar on i.articulo = ar.id \n"
                + " where i.eliminado = false"
                + " and a.ap_campo = ? " + aplicarFiltroNativo(inventarioVO);
        //
        List<Object[]> lo = em.createNativeQuery(cons).setParameter(1, campo).getResultList();
        //
        List<InventarioVO> lista = new ArrayList<>();
        for (Object[] objects : lo) {
            lista.add(castInventario(objects));
        }
        return lista;
    }

    private InventarioVO castInventario(Object[] obj) {
        InventarioVO inv = new InventarioVO();
        inv.setId((Integer) obj[0]);
        inv.setAlmacenNombre((String) obj[1]);
        inv.setArticuloNombre((String) obj[2]);
        inv.setNumeroUnidades(obj[3] != null ? ((BigDecimal) obj[3]).doubleValue() : 0.0);
        inv.setMinimoUnidades(obj[4] != null ? ((BigDecimal) obj[4]).doubleValue() : 0.0);
        inv.setMaximoDeInventario(obj[5] != null ? ((BigDecimal) obj[5]).doubleValue() : 0.0);
        inv.setPuntoDeReorden(obj[6] != null ? ((BigDecimal) obj[6]).doubleValue() : 0.0);
        inv.setFechaUltimaRevision((Date) obj[7]);
        inv.setUbicacion((String) obj[8]);
        return inv;
    }

    private String aplicarFiltroNativo(InventarioVO inventarioVO) {
        String filtro = "";
        if (!esNuloOVacio(inventarioVO.getAlmacenId())) {
            filtro = " and a.id = " + inventarioVO.getAlmacenId();
        }

        if (!esNuloOVacio(inventarioVO.getArticuloId())) {
            filtro += " and ar.id = " + inventarioVO.getArticuloId();
        }

        if (!esNuloOVacio(inventarioVO.getNumeroUnidades())) {
            filtro += " and i.numero_unidades <= " + inventarioVO.getNumeroUnidades();
        }

        if (!esNuloOVacio(inventarioVO.getMinimoUnidades())) {
            filtro += " and i.minimo_unidades <= " + inventarioVO.getMinimoUnidades();
        }

        if (inventarioVO.getFechaUltimaRevision() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            filtro += " and i.fecha_ultima_revision <= '" + sdf.format(inventarioVO.getFechaUltimaRevision()) + "'";
        }

        return filtro;
    }

    public List<InventarioVO> inventarioMovimientos(int campo) {
        final StringBuilder query = new StringBuilder("");
        query.append(" WITH celdas AS ( ")
                .append(" SELECT IC.INV_INVENTARIO, COALESCE(STRING_AGG(R.CODIGO || P.CODIGO || C.CODIGO, ','),'') AS CELDA ")
                .append(" FROM INV_INVENTARIO_CELDA IC	 ")
                .append(" INNER JOIN INV_CELDA C ON IC.INV_CELDA = C.ID ")
                .append(" INNER JOIN INV_RACK R ON C.INV_RACK = R.ID ")
                .append(" INNER JOIN INV_PISO P ON C.INV_PISO = P.ID ")
                .append(" WHERE 1 = 1 ")
                .append(" AND IC.ELIMINADO = FALSE ")
                .append(" GROUP BY IC.INV_INVENTARIO ")
                .append(" ), movimiento AS ( ")
                .append(" SELECT INVENTARIO, max(FECHA) AS fecha ")
                .append(" FROM INV_INVENTARIO_MOVIMIENTO ")
                .append(" WHERE 1 = 1 ")
                .append(" AND ELIMINADO = FALSE ")
                .append(" GROUP BY INVENTARIO ")
                .append(" ORDER BY INVENTARIO ")
                .append(" ) ")
                .append(" SELECT AL.NOMBRE, ")
                .append(" A.NOMBRE, ")
                .append(" A.CODIGO, ")
                .append(" A.CODIGO_INT, ")
                .append(" II.NUMERO_UNIDADES, ")
                .append(" AC.PRECIO, ")
                .append(" M.NOMBRE AS MONEDA, ")
                .append(" celdas.celda,  ")
                .append(" CASE  ")
                .append(" WHEN MO.TIPO_MOVIMIENTO = 1 THEN 'Entrada' ")
                .append(" WHEN MO.TIPO_MOVIMIENTO = 2 THEN 'Salida'  ")
                .append(" WHEN MO.TIPO_MOVIMIENTO = 3 THEN 'Traspaso Entrada' ")
                .append(" WHEN MO.TIPO_MOVIMIENTO = 4 THEN 'Traspaso Salida'  ")
                .append(" WHEN MO.TIPO_MOVIMIENTO = 5 THEN 'Merma'  ")
                .append(" WHEN MO.TIPO_MOVIMIENTO = 6 THEN 'Perdida' ")
                .append(" END, ")
                .append(" MO.FECHA, ")
                .append(" CASE  ")
                .append(" WHEN MO.TIPO_MOVIMIENTO = 1 THEN T.FOLIO_ORDEN_COMPRA ")
                .append(" WHEN MO.TIPO_MOVIMIENTO = 2 THEN T.FOLIO_REMISION  ")
                .append(" ELSE '' ")
                .append(" END, ")
                .append(" MO.NUMERO_UNIDADES, ")
                .append(" CASE  ")
                .append(" WHEN MO.TIPO_MOVIMIENTO = 1 THEN G.NOMBRE ")
                .append(" WHEN MO.TIPO_MOVIMIENTO = 2 THEN GG.NOMBRE  ")
                .append(" ELSE '' ")
                .append(" END, ")
                .append(" P.NOMBRE, ")
                .append(" CASE  ")
                .append(" WHEN MO.TIPO_MOVIMIENTO = 1 THEN U.NOMBRE ")
                .append(" WHEN MO.TIPO_MOVIMIENTO = 2 THEN MA.USUARIO_RECIBE_MATERIAL  ")
                .append(" ELSE ''  ")
                .append(" END,	 ")
                .append(" current_date - (coalesce(movimiento.fecha, current_date)) ")
                .append(" FROM INV_INVENTARIO II ")
                .append(" INNER JOIN INV_ARTICULO A ON II.ARTICULO = A.ID ")
                .append(" INNER JOIN INV_ALMACEN AL ON II.ALMACEN = AL.ID ")
                .append(" INNER JOIN INV_ARTICULO_CAMPO AC ON AC.INV_ARTICULO = A.ID AND AL.AP_CAMPO = AC.AP_CAMPO ")
                .append(" LEFT JOIN MONEDA M ON M.ID = AC.MONEDA ")
                .append(" INNER JOIN INV_INVENTARIO_MOVIMIENTO MO ON MO.INVENTARIO = II.ID AND MO.ELIMINADO = FALSE ")
                .append(" INNER JOIN INV_TRANSACCION T ON T.ID = MO.TRANSACCION AND T.ELIMINADO = FALSE ")
                .append(" LEFT JOIN ORDEN O ON O.CONSECUTIVO = T.FOLIO_ORDEN_COMPRA AND O.ELIMINADO = FALSE ")
                .append(" LEFT JOIN PROVEEDOR P ON P.ID = O.PROVEEDOR AND P.ELIMINADO = FALSE ")
                .append(" LEFT JOIN REQUISICION R ON R.ID = O.REQUISICION AND R.ELIMINADO = FALSE ")
                .append(" LEFT JOIN USUARIO U ON U.ID = R.SOLICITA ")
                .append(" LEFT JOIN GERENCIA G ON G.ID = O.GERENCIA ")
                .append(" LEFT JOIN INV_SOLICITUD_MATERIAL MA ON MA.FOLIO = T.FOLIO_REMISION AND MA.ELIMINADO = FALSE ")
                .append(" LEFT JOIN GERENCIA GG ON GG.ID = MA.GERENCIA ")
                .append(" LEFT JOIN celdas ON ii.id = celdas.inv_inventario ")
                .append(" LEFT JOIN movimiento ON ii.id = movimiento.inventario ")
                .append(" WHERE AL.AP_CAMPO = ").append(campo)
                .append(" AND II.ELIMINADO = FALSE ")
                .append(" ORDER BY AL.NOMBRE, ")
                .append(" A.NOMBRE, ")
                .append(" A.CODIGO, ")
                .append(" A.CODIGO_INT ");

        //out.println("sdadasd: " + query.toString());
        List<Object[]> lobj = em.createNativeQuery(query.toString()).getResultList();
        List<InventarioVO> inventarios = new ArrayList<InventarioVO>();
        for (Object[] objects : lobj) {

            InventarioVO invVo = new InventarioVO();
            invVo.setAlmacenNombre((String) objects[0]); //nombre (almacén)
            invVo.setArticuloNombre((String) objects[1]); //nombre (articulo)
            invVo.setCodigo((String) objects[2]); //codigo
            invVo.setCodigoInt((String) objects[3]); //codigo_int
            invVo.setTotalUnidades((objects[4] == null ? 0.0 : ((BigDecimal) objects[4]).doubleValue()));//numero_unidades            
            invVo.setPrecio((objects[5] == null ? 0.0 : ((BigDecimal) objects[5]).doubleValue()));//precio
            invVo.setMoneda((String) objects[6]); //moneda
            invVo.setUbicacion((String) objects[7]); //ubicacion

            invVo.setTipoMov((String) objects[8]);
            invVo.setFechaMov((Date) objects[9]);
            invVo.setFolio((String) objects[10]);
            invVo.setUnidadesMov((objects[11] == null ? 0.0 : ((BigDecimal) objects[11]).doubleValue()));
            invVo.setGerenciaMov((String) objects[12]);
            invVo.setProveedor((String) objects[13]);
            invVo.setUsuarioMov((String) objects[14]);
            invVo.setDiferenciaDias((Integer) objects[15]);

            inventarios.add(invVo);
        }
        return inventarios;
    }

    public List<InventarioVO> inventario(int campo) {
        final StringBuilder query = new StringBuilder("");
        query.append("SELECT al.nombre, a.nombre, a.codigo,a.codigo_int, sum(ii.numero_unidades), ac.precio,m.nombre as moneda, ")
                .append(" (SELECT ")
                .append(" coalesce(string_agg(r.codigo || p.codigo || c.codigo,','),'') as celda ")
                .append(" from inv_inventario_celda ic ")
                .append(" inner join inv_celda c on ic.inv_celda = c.id ")
                .append(" inner join inv_rack r on c.inv_rack = r.id ")
                .append(" inner join inv_piso p on c.inv_piso = p.id ")
                .append(" where ic.inv_inventario = ii.id ")
                .append(" and ic.eliminado = false) ")
                .append(" FROM inv_inventario ii  \n")
                .append("      INNER join inv_articulo  a on ii.articulo = a.id \n")
                .append("      INNER join inv_almacen  al on ii.almacen = al.id \n")
                .append("      INNER join inv_articulo_campo  ac on ac.inv_articulo = a.id \n")
                .append(" left join moneda m on m.id = ac.moneda")
                .append(" WHERE ac.ap_campo = ").append(campo)
                .append("          AND ii.eliminado  = false  ")
                .append(" GROUP  by al.nombre, a.nombre, a.codigo , a.codigo_int,ac.precio,m.nombre, celda")
                .append(" HAVING  sum(ii.numero_unidades) is not null")
                .append(" ORDER BY a.nombre");

        //out.println("sdadasd: " + query.toString());
        List<Object[]> lobj = em.createNativeQuery(query.toString()).getResultList();
        List<InventarioVO> inventarios = new ArrayList<InventarioVO>();
        for (Object[] objects : lobj) {
            InventarioVO invVo = new InventarioVO();
            invVo.setAlmacenNombre((String) objects[0]); //nombre (almacén)
            invVo.setArticuloNombre((String) objects[1]); //nombre (articulo)
            invVo.setCodigo((String) objects[2]); //codigo
            invVo.setCodigoInt((String) objects[3]); //codigo_int
            invVo.setTotalUnidades((objects[4] == null ? 0.0 : ((BigDecimal) objects[4]).doubleValue()));//numero_unidades            
            invVo.setPrecio((objects[5] == null ? 0.0 : ((BigDecimal) objects[5]).doubleValue()));//precio
            invVo.setMoneda((String) objects[6]); //moneda
            invVo.setUbicacion((String) objects[7]); //ubicacion
            inventarios.add(invVo);
        }
        return inventarios;
    }

    public void salidaInventario(int idInventario, int idArticulo, int idAlmacen, Integer unidadesEntregada, String sesion, int campo, String folio) {
        try {
            InventarioVO inventarioVO = invetarioPorArticulo(idArticulo, idAlmacen);
            //
            if (inventarioVO != null) {
                // Actualizar fecha de ultima revision de inventario
                inventarioVO.setNumeroUnidades(inventarioVO.getNumeroUnidades() - unidadesEntregada);
                this.actualizar(inventarioVO, sesion, campo);
            } else {
                UtilLog4j.log.info("El numero de unidades reales debe ser mayor o igual a cero.");
            }
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
    }

    public InventarioVO invetarioPorArticulo(int idArticulo, int idAlmacen) {
        try {
            String c = " select  ii.id, ii.almacen, ii.articulo, ii.numero_unidades, ii.minimo_unidades, ii.fecha_ultima_revision , \n"
                    + " ii.maximo_de_inventario,\n"
                    + " ii.punto_de_reorden  from inv_inventario ii\n"
                    + " where ii.almacen  = " + idAlmacen + " and ii.articulo  = " + idArticulo + " and ii.eliminado  = false \n"
                    + " ";
            //
            Object[] obj = (Object[]) em.createNativeQuery(c).getSingleResult();

            InventarioVO inventarioVO = new InventarioVO();
            inventarioVO.setId((Integer) obj[0]);
            inventarioVO.setAlmacenId((Integer) obj[1]);
            inventarioVO.setArticuloId((Integer) obj[2]);
            inventarioVO.setNumeroUnidades(((BigDecimal) obj[3]).doubleValue());
            inventarioVO.setMinimoUnidades(((BigDecimal) obj[4]).doubleValue());
            inventarioVO.setFechaUltimaRevision((Date) obj[5]);
            inventarioVO.setMaximoDeInventario(((BigDecimal) obj[6]).doubleValue());
            inventarioVO.setPuntoDeReorden(((BigDecimal) obj[7]).doubleValue());
            //
            return inventarioVO;

        } catch (Exception e) {
            UtilLog4j.log.error(e);
            UtilLog4j.log.info("articulo:" + idArticulo + " No encontró su inventario");
            return null;
        }
    }

    public List<InventarioVO> inventarioPorCampo(int campo) {
        return inventarioPorCampoYAlmacen(campo, 0);
    }

    public List<InventarioVO> inventarioPorCampoYAlmacen(int campo, int almacen) {
        String c = "select al.nombre, a.nombre, a.codigo , a.codigo_int, sum(ii.numero_unidades), u.nombre, u.id, ii.id from inv_inventario ii \n"
                + "	inner join inv_articulo  a on ii.articulo = a.id \n"
                + "	inner join si_unidad u on a.unidad = u.id \n"
                + "	inner join inv_almacen  al on ii.almacen = al.id \n"
                + "	inner join inv_articulo_campo  ac on ac.inv_articulo = a.id \n"
                + "where ac.ap_campo = " + campo
                + "and ii.eliminado  = false \n";
        if (almacen > 0) {
            c += " and al.id = " + almacen;
        }

        c += " group  by al.nombre, a.nombre, a.codigo , a.codigo_int, u.nombre, u.id, ii.id"
                + " having  sum(ii.numero_unidades) is not null "
                + " order by a.nombre";

        // out.println("sdadasd: " + c);
        List<Object[]> lobj = em.createNativeQuery(c).getResultList();
        List<InventarioVO> inventarios = new ArrayList<>();
        for (Object[] objects : lobj) {
            InventarioVO invVo = new InventarioVO();
            invVo.setAlmacenNombre((String) objects[0]);
            invVo.setArticuloNombre((String) objects[1]);
            invVo.setCodigo((String) objects[2]);
            invVo.setCodigoInt((String) objects[3]);
            invVo.setTotalUnidades((objects[4] == null ? 0.0 : ((BigDecimal) objects[4]).doubleValue()));
            invVo.setArticuloUnidad((String) objects[5]);
            invVo.setUnidadId((Integer) objects[6]);
            invVo.setId((Integer) objects[7]);
            inventarios.add(invVo);
        }
        return inventarios;

    }

    public List<InventarioVO> inventarioPorArticuloCampo(int articuloId, int campo) {
        String c = "select al.nombre, a.nombre, a.codigo , a.codigo_int, sum(ii.numero_unidades), u.nombre, u.id from inv_inventario ii \n"
                + "	inner join inv_articulo  a on ii.articulo = a.id \n"
                + "	inner join si_unidad u on a.unidad = u.id \n"
                + "	inner join inv_almacen  al on ii.almacen = al.id \n"
                + "	inner join inv_articulo_campo  ac on ac.inv_articulo = a.id \n"
                + "where ac.ap_campo = " + campo
                + " and ii.articulo = " + articuloId
                + " and ii.eliminado  = false \n";

        c += " group  by al.nombre, a.nombre, a.codigo , a.codigo_int, u.nombre, u.id"
                + " having  sum(ii.numero_unidades) is not null "
                + " order by a.nombre";

        // out.println("sdadasd: " + c);
        List<Object[]> lobj = em.createNativeQuery(c).getResultList();
        List<InventarioVO> inventarios = new ArrayList<>();
        for (Object[] objects : lobj) {
            InventarioVO invVo = new InventarioVO();
            invVo.setAlmacenNombre((String) objects[0]);
            invVo.setArticuloNombre((String) objects[1]);
            invVo.setCodigo((String) objects[2]);
            invVo.setCodigoInt((String) objects[3]);
            invVo.setTotalUnidades((objects[4] == null ? 0.0 : ((BigDecimal) objects[4]).doubleValue()));
            invVo.setArticuloUnidad((String) objects[5]);
            invVo.setUnidadId((Integer) objects[6]);
            inventarios.add(invVo);
        }
        return inventarios;

    }

}
