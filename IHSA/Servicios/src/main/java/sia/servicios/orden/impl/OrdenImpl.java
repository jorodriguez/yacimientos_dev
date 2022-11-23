/*
 * OrdenImpl.java
 * Creada el 13/10/2009, 06:06:30 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.orden.impl;

import com.google.api.client.util.Strings;
import com.newrelic.api.agent.Trace;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jooq.DSLContext;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.constantes.TipoRequisicion;
import sia.modelo.AutorizacionesOrden;
import sia.modelo.ContactosOrden;
import sia.modelo.Convenio;
import sia.modelo.Estatus;
import sia.modelo.Impuesto;
import sia.modelo.Moneda;
import sia.modelo.NotaOrden;
import sia.modelo.OcActividadPetrolera;
import sia.modelo.OcSubTarea;
import sia.modelo.OcTarea;
import sia.modelo.Orden;
import sia.modelo.OrdenDetalle;
import sia.modelo.OrdenSiMovimiento;
import sia.modelo.ProyectoOt;
import sia.modelo.RechazosOrden;
import sia.modelo.SiUsuarioCodigo;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.campoVO.CampoOrden;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.orden.vo.CompaniaAcumuladoVo;
import sia.modelo.orden.vo.ContactoOrdenVo;
import sia.modelo.orden.vo.OcActivoFijoVO;
import sia.modelo.orden.vo.OcRequisicionCheckcodeVO;
import sia.modelo.orden.vo.OrdenEtsVo;
import sia.modelo.orden.vo.OrdenView;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.CompaniaVo;
import sia.notificaciones.orden.impl.NotificacionOrdenImpl;
import sia.pdf.impl.SiaPDFImpl;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.ImpuestoImpl;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.catalogos.impl.ParidadValorImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvConvenioAdjuntoImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.proveedor.impl.PvProveedorSinCartaIntencionImpl;
import sia.servicios.requisicion.impl.OcRequisicionCoNoticiaImpl;
import sia.servicios.requisicion.impl.OcTareaImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.servicios.sistema.impl.FolioImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiUsuarioCodigoImpl;
import sia.servicios.sistema.vo.ParidadValorVO;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.FacturaEstadoEnum;
import sia.util.OrdenEstadoEnum;
import sia.util.UtilLog4j;
import sia.util.notificacion.FCMSender;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 13/10/2009
 */
@Stateless
public class OrdenImpl extends AbstractFacade<Orden> {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @Inject
    private NotaOrdenImpl notaOrdenServicioImpl;
    @Inject
    private RechazosOrdenImpl rechazosOrdenServicioImpl;
    @Inject
    private UsuarioImpl usuarioServicioImpl;
    @Inject
    private MonedaImpl monedaServicioImpl;
    @Inject
    private FolioImpl folioServicioImpl;
    @Inject
    private ContactosOrdenImpl contactosOrdenServicioImpl;
    @Inject
    private ProveedorServicioImpl proveedorServicioImpl;
    @Inject
    private EstatusImpl estatusServicioImpl;
    @Inject
    private RequisicionImpl requisicionServicioImpl;
    @Inject
    private OrdenDetalleImpl ordenDetalleServicioImpl;
    @Inject
    private NotificacionOrdenImpl notificacionesOrdenRemote;
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenRemote;
    @Inject
    private OrdenSiMovimientoImpl ordenSiMovimientoRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private ConvenioImpl convenioRemote;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;
    @Inject
    private SiaPDFImpl siaPDFRemote;
    @Inject
    private OcTerminoPagoImpl ocTerminoPagoRemote;
    @Inject
    private OcTareaImpl ocTareaRemote;
    @Inject
    private OcActivoFijoImpl ocActivoFijoRemote;
    @Inject
    private SiParametroImpl parametrosSistemaServicioRemoto;
    @Inject
    private OcCompaniaAcumuladoImpl ocCompaniaAcumuladoRemote;
    @Inject
    private OcOrdenCoNoticiaImpl ocOrdenCoNoticiaRemote;
    @Inject
    private OcRequisicionCoNoticiaImpl ocRequisicionCoNoticiaRemote;
    @Inject
    private OcRequisicionCheckcodeImpl ocRequisicionCheckcodeRemote;
    @Inject
    private SiOperacionImpl siOperacionRemote;
    @Inject
    private OcCampoProveedorImpl ocCampoProveedorLocal;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;
    @Inject
    private OcOrdenEtsImpl ocOrdenEtsRemote;
    @Inject
    private ImpuestoImpl impuestoRemote;
    @Inject
    private ParidadValorImpl paridadValorRemote;
    @Inject
    private SiUsuarioCodigoImpl siUsuarioCodigoLocal;
    @Inject
    private CvConvenioAdjuntoImpl cvConvenioAdjuntoLocal;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private OcProductoCompaniaImpl ocProductoCompaniaRemote;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    private ApCampoGerenciaImpl apCampoGerenciaRemote;
    @Inject
    private PvProveedorSinCartaIntencionImpl proveedorSinCartaIntencionLocal;

    @Inject
    private DSLContext dbCtx;

    // 
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrdenImpl() {
        super(Orden.class);
    }

    public void creaarOrden(Orden orden) {
        if (orden.getUuid() == null || orden.getUuid().isEmpty()) {
            orden.setUuid(getUUID());
        }
        orden.setSuperaRequisicion(Constantes.BOOLEAN_TRUE);
        orden.setSuperaMonto(Constantes.BOOLEAN_FALSE);
        orden.setLeida(Constantes.BOOLEAN_FALSE);
        //
        create(orden);
        //
        autorizacionesOrdenRemote.crearAutorizaOrden(orden, Constantes.ESTATUS_PENDIENTE_R);
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    public Orden createReturnOrden(Orden orden) {
        if (orden.getUuid() == null || orden.getUuid().isEmpty()) {
            orden.setUuid(getUUID());
        }
        orden.setSuperaRequisicion(Constantes.BOOLEAN_TRUE);
        create(orden);
        //
        autorizacionesOrdenRemote.crearAutorizaOrden(orden, Constantes.ESTATUS_PENDIENTE_R);
        //

        return orden;
    }

    public void editarOrden(Orden orden) {
        edit(orden);
    }

    public void remove(int idOrden, String idUser) {
        //- - - regresar la requisición
        Orden orden = find(idOrden);
        orden.getRequisicion().setEstatus(estatusServicioImpl.find(Constantes.ESTATUS_ASIGNADA)); //40
        requisicionServicioImpl.edit(orden.getRequisicion());
        // - - - regresar los items d la requisicion
        List<OrdenDetalleVO> lo = itemsPorOrdenCompra(orden.getId());
        if (lo != null) {
            for (OrdenDetalleVO odvo : lo) {
                OrdenDetalle ordenDetalle = ordenDetalleServicioImpl.find(odvo.getId());
                if (ordenDetalle.getRequisicionDetalle() != null) {
                    ordenDetalle.getRequisicionDetalle().setDisgregado(Constantes.BOOLEAN_FALSE);
                    requisicionServicioImpl.actualizarItem(ordenDetalle.getRequisicionDetalle());
                }
            }
        }

//        verificar si no tiene un consecutivo asignado.
        if (orden.getConsecutivo() != null) {
            AutorizacionesOrden autorizacionesOrden = autorizacionesOrdenRemote.buscarPorOrden(orden.getId());

            autorizacionesOrden.setEstatus(estatusServicioImpl.find(Constantes.ESTATUS_CANCELADA)); // 100 = cancelada
            autorizacionesOrden.setCancelo(orden.getAnalista());
            autorizacionesOrden.setFechaCancelo(new Date());
            autorizacionesOrden.setHoraCancelo(new Date());
            autorizacionesOrden.setMotivoCancelo("Porque el usuario decidió eliminar la orden y por algún motivo esta ya tenía un consecutivo asignado.");

            autorizacionesOrdenRemote.editar(autorizacionesOrden);

            //FIXME : en lugar de iterar, hacer un UPDATE directo a los registros
            //-- Finalizar las notas que tenga la orden de compra
            List<NotaOrden> listaNotas = getNotasPorOrdenParaFinalizar(orden.getId());
            if (listaNotas != null) {
                for (NotaOrden lista : listaNotas) {
                    lista.setFinalizada(Constantes.BOOLEAN_TRUE);
                    notaOrdenServicioImpl.edit(lista);
                }
            }

        } else {
            orden.setModifico(this.usuarioServicioImpl.find(idUser));
            orden.setFechaModifico(new Date());
            orden.setHoraModifico(new Date());
            orden.setEliminado(Constantes.ELIMINADO);
            //- - - eliminar la orden
            //poner en estatus eliminada
            edit(orden);
        }
    }

    //un metodo privado para que recoja las notas de ordenes y devuelva objetos, usados para finalizar todas las notas
    private List<NotaOrden> getNotasPorOrdenParaFinalizar(Integer idOrden) {
        return em.createQuery("SELECT n FROM NotaOrden n WHERE n.orden.id = :orden AND n.identificador = :identificador AND n.finalizada = :finalizada")
                .setParameter("orden", idOrden)
                .setParameter("identificador", 0)
                .setParameter("finalizada", Constantes.BOOLEAN_FALSE)
                .getResultList();
    }

    @Override
    public Orden find(Object id) {
        return em.find(Orden.class, id);
    }

    public Orden buscarPorConsecutivo(Object consecutivo) {
        try {
            return (Orden) em.createQuery("SELECT o FROM Orden o WHERE o.consecutivo = :consecutivo OR o.navCode = :consecutivo2")
                    .setParameter("consecutivo", consecutivo)
                    .setParameter("consecutivo2", consecutivo)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Orden buscarPorConsecutivoEmpresa(String consecutivo, String rfcEmpresa) {
        Orden r = null;

        try {
            String sql
                    = "select * from orden o where o.consecutivo = ? and o.compania = ?";

            r
                    = (Orden) em.createNativeQuery(sql, "orden_map")
                            .setParameter(1, consecutivo)
                            .setParameter(2, rfcEmpresa)
                            .getSingleResult();

        } catch (Exception e) {
            LOGGER.info(this, "Error al buscar la orden por consecutivo: " + consecutivo, e);
        }

        return r;
    }

    public Orden buscarPorConsecutivoBloque(String consecutivo, int idBloque, String idUsuario) {
        Orden r = null;
        try {
            String sql = "SELECT o.* FROM orden o WHERE o.ap_campo = ? AND o.consecutivo = ?";

            LOGGER.info(this, "Q: : Por bloque : : : " + sql);

            r = (Orden) em.createNativeQuery(sql, "orden_map")
                    .setParameter(1, idBloque)
                    .setParameter(2, consecutivo)
                    .getSingleResult();

        } catch (Exception e) {
            LOGGER.info(this, "Error al buscar la orden por consecutivo: " + consecutivo, e);
        }

        return r;
    }

    public List<Orden> findAll() {
        return em.createQuery("select object(o) from Orden as o").getResultList();
    }

    //-- Ordenes nuevas
    public List<OrdenVO> ordenesPendientes(String usuario, int apCampo, boolean conContrato) {

        String sql
                = "select r.ID, r.REFERENCIA, q.consecutivo, r.FECHA, r.TOTAL, a.rechazada, r.tipo, q.PROVEEDOR, r.leida, g.nombre "
                + ", (select MONEDA from ORDEN_DETALLE where ORDEN = r.ID group by MONEDA limit 1) "
                + "  from AUTORIZACIONES_ORDEN a"
                + "     inner join orden r on a.ORDEN = r.ID "
                + "     inner join REQUISICION q on r.REQUISICION = q.ID "
                + "     inner join Gerencia g on r.gerencia = g.ID "
                + "  WHERE a.ESTATUS  = ? "
                + "  AND r.ANALISTA = ? "
                + "  AND r.con_convenio = ? "
                + "  AND r.AP_CAMPO = ? "
                + "  AND r.eliminado = ? "
                + "  ORDER BY r.id ASC";

        List<OrdenVO> locs = null;
        List<Object[]> lo = em.createNativeQuery(sql)
                .setParameter(1, OrdenEstadoEnum.POR_SOLICITAR.getId())
                .setParameter(2, usuario)
                .setParameter(3, conContrato)
                .setParameter(4, apCampo)
                .setParameter(5, Constantes.NO_ELIMINADO)
                .getResultList();

        if (lo != null) {
            locs = new ArrayList<>();
            for (Object[] objects : lo) {
                locs.add(castSolicitarOrden(objects));
            }
        }

        return locs;
    }

    private OrdenVO castSolicitarOrden(Object[] objects) {
        OrdenVO o = new OrdenVO();
        o.setId((Integer) objects[0]);
        o.setReferencia(String.valueOf(objects[1]));
        o.setConsecutivo(String.valueOf(objects[2]));
        o.setFecha((Date) objects[3]);
        o.setTotal((Double) objects[4]);
        o.setDevuelta((Boolean) objects[5]);
        o.setTipo((String) objects[6]);
        o.setProveedor((String) objects[7]);
        o.setLeida((Boolean) objects[8]);
        o.setGerencia((String) objects[9]);
        o.setIdMoneda((Integer) objects[10] == null ? 1 : (Integer) objects[10]);
        o.setSelected(false);
        return o;
    }

    //-- ordenes q se tienen que aprobar por la gerencia solicitante ---
    public List<OrdenVO> getOrdenesApruebaGerenciaSolicitante(String usuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(consultaOrden());
        sb.append("  WHERE a.ESTATUS  = ").append(Constantes.ORDENES_SIN_APROBAR); //110
        sb.append("  AND a.AUTORIZA_GERENCIA = '").append(usuario).append("'");
        sb.append("  AND o.AP_CAMPO = ").append(apCampo);
        sb.append("  AND o.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        sb.append("  ORDER BY a.FECHA_SOLICITO ASC");
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<OrdenVO> lor = null;
        if (lo != null) {
            lor = new ArrayList<>();
            for (Object[] objects : lo) {
                lor.add(castOrdenColumnaGenerales(objects));
            }
        }
        return lor;
    }

    //-- ordenes que se tienen que autorizar por la direccion de MPG - -
    public List<OrdenVO> getOrdenesAutorizaMPG(String usuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(consultaOrden());
        sb.append("  WHERE a.ESTATUS  = ").append(Constantes.ORDENES_SIN_AUTORIZAR_MPG); //120
        sb.append("  AND a.AUTORIZA_MPG = '").append(usuario).append("'");
        sb.append("  AND o.AP_CAMPO = ").append(apCampo);
        sb.append("  AND o.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        sb.append("  ORDER BY a.FECHA_SOLICITO ASC");
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<OrdenVO> lor = null;
        if (lo != null) {
            lor = new ArrayList<>();
            for (Object[] objects : lo) {
                lor.add(castOrdenColumnaGenerales(objects));
            }
        }
        return lor;

    }

    //-- ordenes que se tienen q autorizar por la direccion de IHSA ---
    public List<OrdenVO> getOrdenesAutorizaFinanzas(String usuario, int apCampo) {

        StringBuilder sb = new StringBuilder();
        sb.append(consultaOrden());
        sb.append("  WHERE a.ESTATUS  = ").append(Constantes.ESTATUS_POR_APROBAR_SOCIO); //125
        sb.append("  AND a.AUTORIZA_FINANZAS = '").append(usuario).append("'");
        sb.append("  AND o.AP_CAMPO = ").append(apCampo);
        sb.append("  AND o.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        sb.append("  ORDER BY a.FECHA_SOLICITO ASC");
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<OrdenVO> lor = null;
        if (lo != null) {
            lor = new ArrayList<>();
            for (Object[] objects : lo) {
                lor.add(castOrdenColumnaGenerales(objects));
            }
        }
        return lor;
    }

    //-- ordenes que se tienen q autorizar por la direccion de IHSA ---
    public List<OrdenVO> getOrdenesAutorizaSocio(String usuario, int apCampo) {

        StringBuilder sb = new StringBuilder();
        sb.append(consultaOrden());
        sb.append("  WHERE a.ESTATUS  = ").append(Constantes.ESTATUS_POR_APROBAR_SOCIO); //135
        sb.append("  AND (a.AUTORIZA_FINANZAS = '").append(usuario).append("'").append(" or a.AUTORIZA_FINANZAS is null )");
        sb.append("  AND o.AP_CAMPO = ").append(apCampo);
        sb.append("  AND o.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        sb.append("  ORDER BY a.FECHA_SOLICITO ASC");
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<OrdenVO> lor = null;
        if (lo != null) {
            lor = new ArrayList<>();
            for (Object[] objects : lo) {
                lor.add(castOrdenColumnaGenerales(objects));
            }
        }
        return lor;
    }

    //-- ordenes que se tienen q autorizar por la direccion de IHSA ---
    public List<OrdenVO> getOrdenesAutorizaIHSA(String usuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(consultaOrden());
        sb.append("  WHERE a.ESTATUS  = ").append(Constantes.ORDENES_SIN_AUTORIZAR_IHSA);     //130
        sb.append("     AND a.AUTORIZA_IHSA = '").append(usuario).append("'");
        sb.append("     AND o.AP_CAMPO = ").append(apCampo);
        sb.append("     AND o.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        sb.append(" ORDER BY a.FECHA_SOLICITO ASC");

        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<OrdenVO> lor = null;
        if (lo != null) {
            lor = new ArrayList<>();
            for (Object[] objects : lo) {
                lor.add(castOrdenColumnaGenerales(objects));
            }
        }
        return lor;
    }

    //- - - - - Ordenes que se tienen que autorizar por la gerencia de Compras
    public List<OrdenVO> getOrdenesAutorizaCompras(String usuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(consultaOrden());
        sb.append("  WHERE a.ESTATUS  = ").append(Constantes.ORDENES_SIN_AUTORIZAR_COMPRAS);//140
        sb.append("     AND o.AP_CAMPO = ").append(apCampo);
        sb.append("     AND o.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        if (usuario != null) {
            sb.append(" and a.AUTORIZA_COMPRAS = '").append(usuario).append("' ");
        }
        sb.append("  ORDER BY a.FECHA_AUTORIZO_IHSA ASC");
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<OrdenVO> lor = null;
        if (lo != null) {
            lor = new ArrayList<>();
            for (Object[] objects : lo) {
                lor.add(castOrdenColumnaGenerales(objects));
            }
        }
        return lor;
    }

    public List<OrdenVO> getOrdenesAutorizaLicitacion(String usuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(consultaOrden());
        sb.append("  WHERE a.ESTATUS  = ").append(Constantes.ORDENES_SIN_AUTORIZAR_LICITACION);//151
        sb.append("     AND o.AP_CAMPO = ").append(apCampo);
        sb.append("     AND o.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        sb.append("  ORDER BY a.FECHA_AUTORIZO_IHSA ASC");
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<OrdenVO> lor = null;
        if (lo != null) {
            lor = new ArrayList<>();
            for (Object[] objects : lo) {
                lor.add(castOrdenColumnaGenerales(objects));
            }
        }
        return lor;
    }

    public List getOrdenesAutorizadasCompras(String usuario, int apCampo, String tipo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT r.ID, r.CONSECUTIVO,  r.REFERENCIA, q.consecutivo, a.FECHA_SOLICITO, ");
        sb.append(" e.nombre, p.nombre,  r.CONTRATO,  r.TOTAL, m.siglas, r.supera_monto "); //10
        sb.append(" FROM ORDEN r ");
        sb.append(" LEFT JOIN AUTORIZACIONES_ORDEN  a ON a.ORDEN = r.ID ");
        sb.append(" LEFT JOIN Proveedor p ON p.id = r.PROVEEDOR ");
        sb.append(" LEFT JOIN Estatus e ON e.ID = a.ESTATUS ");
        sb.append(" LEFT JOIN Moneda m ON m.ID = r.MONEDA ");
        sb.append(" LEFT JOIN Requisicion q ON q.ID = r.REQUISICION ");
        sb.append(" LEFT JOIN OC_unidad_costo uc ON uc.ID = r.OC_unidad_costo");
        //
        sb.append(" WHERE a.ESTATUS  = ").append(Constantes.ESTATUS_AUTORIZADA);//150
        sb.append(" AND a.SOLICITO = '").append(usuario).append("'");
        sb.append(" AND r.AP_CAMPO = ").append(apCampo);
        //
        sb.append(" AND r.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        //
        sb.append(" AND r.TIPO = '").append(tipo).append("' ");
        sb.append(" ORDER BY a.FECHA_AUTORIZO_IHSA ASC");

        return em.createNativeQuery(sb.toString()).getResultList();

    }

    //- - - - - Ordenes que se hicieron por la reequisicion
    public List<OrdenVO> getOrdenesPorRequisicion(Object idRequisicion, String canceladas) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT o.ID, o.CONSECUTIVO, e.nombre, c.nombre ");
        sb.append(" FROM AUTORIZACIONES_ORDEN ao");
        sb.append("     inner join ESTATUS e on ao.ESTATUS = e.ID");
        if (canceladas != null && !canceladas.isEmpty()) {
            sb.append(" ").append(canceladas).append(" ");
        }
        sb.append("     inner join ORDEN o  on ao.ORDEN = o.ID and o.ELIMINADO = 'False' ");
        sb.append("     inner join COMPANIA c on o.COMPANIA = c.RFC");
        sb.append(" Where o.REQUISICION = ").append(idRequisicion);
        sb.append("     ORDER BY o.CONSECUTIVO ASC");
//
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<OrdenVO> lista = new ArrayList<>();
        OrdenVO o;
        for (Object[] objects : lo) {
            o = new OrdenVO();
            o.setId((Integer) objects[0]);
            o.setConsecutivo(String.valueOf(objects[1]));
            o.setEstatus(String.valueOf(objects[2]));
            o.setCompania((String) objects[3]);
            lista.add(o);
        }
        return lista;
    }

    public List<OrdenVO> traerOrdenDeProyectoOt(Integer idProyectoOt) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select o.id, o.consecutivo, g.nombre, p.NOMBRE, r.CONSECUTIVO as consecutivo_requisicion, o.SUBTOTAL, o.TOTAL,");
            sb.append(" o.TOTAL_USD, e.NOMBRE");
            sb.append(" FROM AUTORIZACIONES_ORDEN ao");
            sb.append("     inner join ESTATUS e on ao.ESTATUS = e.ID");
            sb.append("     inner join ORDEN o  on ao.ORDEN = o.ID");
            sb.append("     inner join GERENCIA g on o.GERENCIA = g.ID");
            sb.append("     inner join PROVEEDOR p on o.PROVEEDOR = p.ID");
            sb.append("     inner join REQUISICION r on o.REQUISICION = r.ID");
            sb.append(" WHERE o.proyecto_ot = ").append(idProyectoOt);
            sb.append(" Order by p.NOMBRE ");

            List<OrdenVO> le = new ArrayList<>();

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            for (Object[] objects : lo) {
                OrdenVO or = new OrdenVO();
                or.setId((Integer) objects[0]);
                or.setConsecutivo((String) objects[1]);
                or.setGerencia((String) objects[2]);
                or.setProveedor((String) objects[3]);
                or.setConsecutivoRequisicion((String) objects[4]);
                or.setSubTotal((Double) (objects[5] != null ? objects[5] : 0.0));
                or.setTotal((Double) (objects[6] != null ? objects[6] : 0.0));
                or.setTotalUsd((Double) (objects[7] != null ? objects[7] : 0.0));
                or.setEstatus((String) objects[8]);
                le.add(or);
            }
            return le;
        } catch (Exception e) {
            LOGGER.info(this, "Excepción al buscar las ordenes generadas de proyecto Ot" + e.getMessage());
            return null;
        }
    }

    public List<Orden> getOrdenesPorRequisicionJPA(Object idRequisicion) {
        return em.createQuery("SELECT o FROM Orden o WHERE "
                + " o.requisicion.id = :requisicion and o.eliminado = :eli"
                + " ORDER BY o.consecutivo ASC").setParameter("requisicion", idRequisicion).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
    }

    // Trae totales de las OC/S
    public long totalOrdenesSinSolicitar(String usuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select count(*) from AUTORIZACIONES_ORDEN a ");
        sb.append("     inner join orden r on a.ORDEN = r.ID");
        sb.append("  WHERE a.ESTATUS  = ").append(Constantes.ORDENES_SIN_SOLICITAR); //101
        sb.append("  AND r.ANALISTA = '").append(usuario).append("'");
        sb.append("  AND r.AP_CAMPO = ").append(apCampo);
        sb.append("  AND r.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");

        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());
    }

    //-- Total ordenes sin aprobar por gerencia solicitante
    public long totalOrdenesSinAprobar(String usuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from AUTORIZACIONES_ORDEN ao ");
        sb.append("     inner join orden o on ao.ORDEN = o.id ");
        sb.append(" where ao.AUTORIZA_GERENCIA = '").append(usuario).append("'");
        sb.append(" and ao.ESTATUS = ").append(Constantes.ORDENES_SIN_APROBAR);
        sb.append(" and o.AP_CAMPO = ").append(apCampo);
        sb.append(" and o.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        sb.append(" and o.MONEDA is not null and o.CONSECUTIVO is not null");

        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());
    }

    //-- Total ordenes sin autorizar por direccion MPG
    public long totalOrdenesSinAutorizarMPG(String usuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from AUTORIZACIONES_ORDEN ao ");
        sb.append("     inner join orden o on ao.ORDEN = o.id ");
        sb.append(" where ao.AUTORIZA_MPG = '").append(usuario).append("'");
        sb.append(" and ao.ESTATUS = ").append(Constantes.ORDENES_SIN_AUTORIZAR_MPG);
        sb.append(" and o.AP_CAMPO = ").append(apCampo);
        sb.append(" and o.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        sb.append(" and o.MONEDA is not null");

        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());

    }
    //-- Total ordenes sin autorizar por direccion IHSA

    public long getTotalOrdenesSinAutorizarIHSA(String usuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from AUTORIZACIONES_ORDEN ao ");
        sb.append("     inner join orden o on ao.ORDEN = o.id ");
        sb.append(" where ao.autoriza_Ihsa = '").append(usuario).append("'");
        sb.append(" and ao.ESTATUS = ").append(Constantes.ORDENES_SIN_AUTORIZAR_IHSA);
        sb.append(" and o.AP_CAMPO = ").append(apCampo);
        sb.append(" and o.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        sb.append(" and o.MONEDA is not null");

        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());
    }

    public long totalOrdenesSinAutorizarFinanzas(String usuario, int apCampo) {

        String sql
                = "select count(*) from AUTORIZACIONES_ORDEN ao "
                + "     inner join orden o on ao.ORDEN = o.id "
                + " where ao.autoriza_Finanzas = ?"
                + " and ao.ESTATUS = ? "
                + " and o.AP_CAMPO = ? "
                + " and o.ELIMINADO = ? "
                + " and o.MONEDA is not null";

        return ((Long) em.createNativeQuery(sql)
                .setParameter(1, usuario)
                .setParameter(2, Constantes.ESTATUS_POR_APROBAR_SOCIO)
                .setParameter(3, apCampo)
                .setParameter(4, Constantes.NO_ELIMINADO)
                .getSingleResult());
    }

    public long totalOrdenesSinAprobarSocio(String usuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT count(*) from AUTORIZACIONES_ORDEN a ");
        sb.append("     inner join Orden o  ON a.ORDEN = o.ID");
        sb.append(" WHERE o.eliminado = '").append(Constantes.NO_ELIMINADO).append("' ");
        sb.append(" AND a.ESTATUS = ").append(Constantes.ESTATUS_POR_APROBAR_SOCIO);
        sb.append(" AND o.AP_CAMPO = ").append(apCampo);
        sb.append(" and '").append(usuario).append("' in (SELECT ur.USUARIO from SI_USUARIO_ROL ur where ur.SI_ROL = ").append(Constantes.ROL_SOCIO).append("  and ur.eliminado = 'False') ");
        sb.append(" AND (a.AUTORIZA_FINANZAS IS NULL OR a.AUTORIZA_FINANZAS = '").append(usuario).append("')");
        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());
    }

    public long getTotalOrdenesSinAutorizarCompras(String usuario, int apCampo) {

        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from AUTORIZACIONES_ORDEN ao ");
        sb.append("     inner join orden o on ao.ORDEN = o.id ");
        sb.append(" where ao.autoriza_Compras= '").append(usuario).append("'");
        sb.append(" and ao.ESTATUS = ").append(Constantes.ORDENES_SIN_AUTORIZAR_COMPRAS);
        sb.append(" and o.AP_CAMPO = ").append(apCampo);
        sb.append(" and o.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        sb.append(" and o.MONEDA is not null");

        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());

    }

    public long getTotalOrdenesSinAutorizarComprasLicitacion(String usuario, int apCampo) {

        String sql
                = "select count(*) from AUTORIZACIONES_ORDEN ao "
                + "     inner join orden o on ao.ORDEN = o.id "
                + " where ao.AUTORIZA_LICITACION = ?"
                + " and ao.ESTATUS = ? "
                + " and o.AP_CAMPO = ? "
                + " and o.ELIMINADO = ? "
                + " and o.MONEDA is not null";

        return ((Long) em.createNativeQuery(sql)
                .setParameter(1, usuario)
                .setParameter(2, Constantes.ORDENES_SIN_AUTORIZAR_LICITACION)
                .setParameter(3, apCampo)
                .setParameter(4, Constantes.NO_ELIMINADO)
                .getSingleResult());
    }

    public long getTotalTareasSinCompleta(String usuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT count(*) from AUTORIZACIONES_ORDEN a ");
        sb.append("     inner join Orden o  ON a.ORDEN = o.ID");
        sb.append(" WHERE a.SOLICITO = '").append(usuario).append("' ");
        sb.append(" AND a.ESTATUS = ").append(Constantes.ESTATUS_AUTORIZADA);
        sb.append(" AND o.AP_CAMPO = ").append(apCampo);
        sb.append(" AND o.eliminado = '").append(Constantes.NO_ELIMINADO).append("' ");
        //
        sb.append(" AND (o.TIPO = '").append(TipoRequisicion.AF.name()).append("' or o.TIPO = '").append(TipoRequisicion.PS.name()).append("') ");

        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());
    }

    public long getTotalTareasSinCompletaAF(String usuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT count(*) from AUTORIZACIONES_ORDEN a ");
        sb.append("     inner join Orden o  ON a.ORDEN = o.ID");
        sb.append(" WHERE a.SOLICITO = '").append(usuario).append("' ");
        sb.append(" AND a.ESTATUS = ").append(Constantes.ESTATUS_AUTORIZADA);
        sb.append(" AND o.AP_CAMPO = ").append(apCampo);
        sb.append(" AND o.eliminado = '").append(Constantes.NO_ELIMINADO).append("' ");
        sb.append(" AND o.NAVCODE is null ");
        sb.append(" AND o.TIPO = '").append(TipoRequisicion.AF.name()).append("' ");

        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());
    }

    public long getTotalTareasSinCompletaPS(String usuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT count(*) from AUTORIZACIONES_ORDEN a ");
        sb.append("     inner join Orden o  ON a.ORDEN = o.ID");
        sb.append(" WHERE a.SOLICITO = '").append(usuario).append("' ");
        sb.append(" AND a.ESTATUS = ").append(Constantes.ESTATUS_AUTORIZADA);
        sb.append(" AND o.AP_CAMPO = ").append(apCampo);
        sb.append(" AND o.eliminado = '").append(Constantes.NO_ELIMINADO).append("' ");
        sb.append(" AND o.NAVCODE is null ");
        sb.append(" AND o.TIPO = '").append(TipoRequisicion.PS.name()).append("' ");

        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());
    }

    public long totalOrdenesEstatusUsuario(String usuario, int apCampo, int status) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT count(*) from AUTORIZACIONES_ORDEN a ");
        sb.append("     inner join Orden o  ON a.ORDEN = o.ID");
        sb.append(" WHERE a.SOLICITO = '").append(usuario).append("' ");
        sb.append(" AND a.ESTATUS = ").append(status);
        sb.append(" AND o.AP_CAMPO = ").append(apCampo);
        sb.append(" AND o.eliminado = '").append(Constantes.NO_ELIMINADO).append("' ");

        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());
    }

    public void actualizarMontoOrden(Orden orden, double total) {
        orden.setTotal(total);
        orden.setSubtotal(orden.getTotal());
        orden.setModifico(orden.getGenero());
        orden.setFechaModifico(new Date());
        orden.setHoraModifico(new Date());
        edit(orden);
    }

    private void actualizarMonto(Orden orden, List<OrdenDetalleVO> items) {
        double total = 0;
        if (items != null) {
            for (OrdenDetalleVO objects : items) {
                if (objects.getImporte() > Constantes.CERO) {
                    total += objects.getImporte();
                }
            }
        }
        orden.setTotal(total);
        orden.setSubtotal(orden.getTotal());
        edit(orden);
    }

    //JAVIER
    // - - - Items o Lineas de la Orden - - -
    public void crearItem(OrdenDetalle ordenDetalle, String idSesion, int idTarea) {
        try {
            ordenDetalle.setImporte(ordenDetalle.getCantidad() * ordenDetalle.getPrecioUnitario());

            if (ordenDetalle.getOrden().getApCampo().getTipo().equals("N")) {
                ordenDetalle.setOcSubTarea(null);
                ordenDetalle.setOcActividadPetrolera(null);
                ordenDetalle.setProyectoOt(new ProyectoOt(ordenDetalle.getProyectoOt().getId()));
                if (TipoRequisicion.PS.name().equals(ordenDetalle.getOrden().getTipo().toString())) {
                    ordenDetalle.setOcTarea(new OcTarea(idTarea));
                    ordenDetalle.setOcUnidadCosto(ordenDetalle.getOrden().getOcUnidadCosto());
                } else {
                    ordenDetalle.setOcUnidadCosto(null);
                }
            } else { // contractual
                ordenDetalle.setOcActividadPetrolera(new OcActividadPetrolera(ordenDetalle.getOcActividadPetrolera().getId()));
                ordenDetalle.setProyectoOt(new ProyectoOt(ordenDetalle.getProyectoOt().getId()));
                if (TipoRequisicion.PS.name().equals(ordenDetalle.getOrden().getTipo().toString())) {
                    ordenDetalle.setOcTarea(ocTareaRemote.find(idTarea));
                    ordenDetalle.setOcUnidadCosto(ordenDetalle.getOrden().getOcUnidadCosto());
                    ordenDetalle.setOcSubTarea(new OcSubTarea(ordenDetalle.getOcSubTarea().getId()));
                } else {
                    ordenDetalle.setOcTarea(null);
                    ordenDetalle.setOcUnidadCosto(null);
                    ordenDetalle.setOcSubTarea(null);
                }
            }
            ordenDetalle.setRecibido(Constantes.BOOLEAN_FALSE);
            ordenDetalle.setGenero(this.usuarioServicioImpl.find(idSesion));
            ordenDetalle.setFechaGenero(new Date());
            ordenDetalle.setHoraGenero(new Date());
            ordenDetalle.setEliminado(Constantes.NO_ELIMINADO);
            if (ordenDetalle.getRequisicionDetalle() == null) {
                ordenDetalle.setEnCatalogo(Constantes.BOOLEAN_FALSE);
            }
            ordenDetalleServicioImpl.crear(ordenDetalle);
            //
            actualizarMontoOrden(ordenDetalle.getOrden(), ordenDetalleServicioImpl.traerTotalOrden(ordenDetalle.getOrden().getId()));

        } catch (Exception e) {
            LOGGER.error("#ERRORO AL CREAR ITEM ORDEN ", e);
        }
    }

    public void actualizarItem(OrdenDetalle ordenDetalle, String idSesion, int idTarea) {
        Double importeAux = ordenDetalle.getCantidad() * ordenDetalle.getPrecioUnitario();
        boolean actMonto = false;
        if (!importeAux.equals(ordenDetalle.getImporte())) {
            ordenDetalle.setImporte(importeAux);
            actMonto = true;
        }
        //
//	ordenDetalle.setSiUnidad(ocUnidadRemote.find(idUnidad));
        if (TipoRequisicion.PS.name().equals(ordenDetalle.getOrden().getTipo().toString())
                && ordenDetalle.getOcTarea().getId() != idTarea) {
            ordenDetalle.setOcTarea(new OcTarea(idTarea));
        }
        ordenDetalle.setModifico(this.usuarioServicioImpl.find(idSesion));
        ordenDetalle.setFechaModifico(new Date());
        ordenDetalle.setHoraModifico(new Date());
        ordenDetalleServicioImpl.editar(ordenDetalle);

        if (actMonto) {
            List<OrdenDetalleVO> items = ordenDetalleServicioImpl.itemsPorOrden(ordenDetalle.getOrden().getId());
            actualizarMonto(ordenDetalle.getOrden(), items);
        }
    }

    public void actualizarMultiItems(int idOrden, OrdenDetalle itemP, String idSesion, String monedaNombre, String compania) {
        Moneda m = this.monedaServicioImpl.buscarPorNombre(monedaNombre, compania);
        List<OrdenDetalle> items = ordenDetalleServicioImpl.getItemsPorOrden(idOrden, itemP.getMultiproyectoId());
        boolean actMonto = false;
        Orden orden = this.find(idOrden);

        for (OrdenDetalle item : items) {
            if (item.getPrecioUnitario() == null || item.getPrecioUnitario() != itemP.getPrecioUnitario()) {
                item.setPrecioUnitario(itemP.getPrecioUnitario());
            }
            Double importeAux = item.getCantidad() * itemP.getPrecioUnitario();
            if (!importeAux.equals(item.getImporte())) {
                item.setImporte(importeAux);
                actMonto = true;
            }

//            if (item.getMoneda() == null || !(item.getMoneda().getId() == m.getId())) {
//                item.setMoneda(m);
//            }
            if (item.getObservaciones() == null || (itemP.getObservaciones() != null && !itemP.getObservaciones().equalsIgnoreCase(item.getObservaciones()))) {
                item.setObservaciones(itemP.getObservaciones());
            }

            if (item.getTextNav() == null || (itemP.getTextNav() != null && !itemP.getTextNav().equalsIgnoreCase(item.getTextNav()))) {
                item.setTextNav(itemP.getTextNav());
            }

            item.setModifico(this.usuarioServicioImpl.find(idSesion));
            item.setFechaModifico(new Date());
            item.setHoraModifico(new Date());
            this.ordenDetalleServicioImpl.editar(item);

//            if (item.getOrden().getMoneda() == null || item.getOrden().getMoneda().getId() != item.getMoneda().getId()) {
//                item.getOrden().setMoneda(m);
//                this.edit(item.getOrden());
//            }
        }

        if (actMonto) {
            List<OrdenDetalleVO> itemsVO = ordenDetalleServicioImpl.itemsPorOrden(idOrden);
            this.actualizarMonto(orden, itemsVO);
        }
    }

    public void actualizarMultiItemsProducto(int idOrden, int agrupadorID, int idProducto) {
        List<OrdenDetalle> items = ordenDetalleServicioImpl.getItemsPorOrden(idOrden, agrupadorID);
        for (OrdenDetalle item : items) {
            item.setOcProductoCompania(ocProductoCompaniaRemote.find(idProducto));
            this.ordenDetalleServicioImpl.editar(item);
        }

    }

    public void eliminarItem(OrdenDetalle ordenDetalle) {
        ordenDetalleServicioImpl.remove(ordenDetalle);
        //
        List<OrdenDetalleVO> items = ordenDetalleServicioImpl.itemsPorOrden(ordenDetalle.getOrden().getId());
        actualizarMonto(ordenDetalle.getOrden(), items);
    }

    public List<OrdenDetalle> getItems(Object orden) {
        return ordenDetalleServicioImpl.getItemsPorOrden(orden);
    }

    private ParidadValorVO paridadMoneda(String siglas, String fecha, int monedaID) {
        return paridadValorRemote.traerParidadValor(siglas, fecha, monedaID);
    }

    public boolean solicitarOrden(int idProveedor, String revisa, String aprueba, Object condicionPago, String moneda,
            Date fechaEntrega, Orden orden, int iva, Object tipoOrden, int idCampo, List<ContactoProveedorVO> lcp,
            String idSesion, int idTerminoPago, String contrato) {
        boolean solicitada = false;
        try {
            //
            orden.setContrato(contrato);

            //Guardar los contactos
            contactosOrdenServicioImpl.guardarContacto(idSesion, lcp, orden.getId());
            //Recupera los contactos
            List<ContactoOrdenVo> listaContactosOrden = getContactosVo(orden.getId());
            // Recupera los items
            List<OrdenDetalleVO> items = null;
            if (orden != null && orden.isMultiproyecto()) {
                items = ordenDetalleServicioImpl.itemsPorOrdenMulti(orden.getId());
            } else {
                items = ordenDetalleServicioImpl.itemsPorOrden(orden.getId());
            }
            //Se modifico pq aparentemente no tiene ninguan utilidad
            orden.setProveedor(proveedorServicioImpl.find(idProveedor));
            //
            orden.setOcTerminoPago(ocTerminoPagoRemote.find(idTerminoPago));
            //
            orden.setMoneda(monedaServicioImpl.buscarPorNombre(moneda, orden.getApCampo().getCompania().getRfc()));
            orden.setFechaEntrega(fechaEntrega);
            ///
            orden.setResponsableGerencia(gerenciaRemote.getResponsableByApCampoAndGerencia(orden.getApCampo().getId(), orden.getGerencia().getId(), Constantes.FALSE));
            //
            orden.setSuperaMonto(Constantes.BOOLEAN_FALSE);
            //
            orden.setFecha(new Date());

            if (orden.getConsecutivo() == null) {
                if (tipoOrden.equals("Orden de Compra")) {
                    orden.setEsOc(true);
                    orden.setConsecutivo(folioServicioImpl.getFolio("ORDEN_CONSECUTIVO", orden.getApCampo().getId()));
                } else if (tipoOrden.equals("Orden de Servicio")) {
                    orden.setEsOc(false);
                    orden.setConsecutivo(folioServicioImpl.getFolio("ORDEN_SERVICIO_CONSECUTIVO", orden.getApCampo().getId()));
                } else {
                    orden.setEsOc(false);
                    orden.setConsecutivo(folioServicioImpl.getFolio("ORDEN_SUBCONTRATO", orden.getApCampo().getId()));
                }
            }

            if (iva > 0) {
                Impuesto impuesto = impuestoRemote.find(iva);
                orden.setConIva(Constantes.BOOLEAN_TRUE);
                orden.setPorcentajeIva(impuesto.getNombre() + " " + impuesto.getValor() + "%");
                orden.setImpuesto(impuesto);
                //String v = "0." + iva;
                LOGGER.info(this, "IVA: " + (impuesto.getValor() / 100));
                orden.setIva(orden.getSubtotal() * (impuesto.getValor() / 100));
                orden.setTotal(orden.getSubtotal() + orden.getIva());
            } else {
                orden.setConIva(Constantes.BOOLEAN_FALSE);
                orden.setPorcentajeIva("No aplica");
                orden.setIva(.0);
            }
            // comparar total Orden con totalItems de la req. si total orden es en pesos convertir su equivalente en dolares
            if (Constantes.USD_SIGLAS.equals(orden.getMoneda().getSiglas())) {
                orden.setTotalUsd(orden.getTotal());
            } else {
                ParidadValorVO vo = paridadMoneda(Constantes.USD_SIGLAS, Constantes.FMT_yyyy_MM_dd.format(new Date()), orden.getMoneda().getId());
                if (vo != null && vo.getId() > 0) {
                    orden.setTotalUsd(orden.getTotal() / vo.getValor());
                }
            }

            orden.setSuperaRequisicion(Constantes.BOOLEAN_TRUE);
            orden.setLeida(Constantes.BOOLEAN_FALSE);

            AutorizacionesOrden autorizacionesOrden = autorizacionesOrdenRemote.buscarPorOrden(orden.getId());
            UsuarioVO uvo = usuarioServicioImpl.traerResponsableGerencia(orden.getApCampo().getId(), Constantes.ID_GERENCIA_IHSA, orden.getCompania().getRfc());
            if (autorizacionesOrden.getFechaSolicito() == null) {
                autorizacionesOrden.setFechaSolicito(new Date());
                autorizacionesOrden.setHoraSolicito(new Date());
                autorizacionesOrden.setSolicito(usuarioServicioImpl.find(orden.getAnalista().getId()));
            }
            autorizacionesOrden.setRechazada(Constantes.BOOLEAN_FALSE);
            autorizacionesOrden.setAutorizaGerencia(usuarioServicioImpl.find(orden.getGerenteCompras().getId()));                    //Revisiores
            autorizacionesOrden.setAutorizaMpg(usuarioServicioImpl.find(revisa));                    //Aprobadores
            autorizacionesOrden.setAutorizaIhsa(usuarioServicioImpl.find(aprueba));
            autorizacionesOrden.setAutorizaCompras(usuarioServicioImpl.find(uvo.getId()));
            autorizacionesOrden.setEnviarPdf(usuarioServicioImpl.find(orden.getAnalista().getId()));
            if (orden.getApCampo() != null && orden.getApCampo().getTipo() != null
                    && orden.getApCampo().getCompania() != null && orden.getApCampo().getCompania().getMontoLicitacion() != null) {
                List<UsuarioRolVo> usrLicitacion = siUsuarioRolRemote.traerRolPorCodigo(Constantes.CODIGO_ROL_LICITACION, orden.getApCampo().getId(), Constantes.MODULO_COMPRA);
                if (usrLicitacion != null && usrLicitacion.size() > 0) {
                    autorizacionesOrden.setAutorizaLicitacion(usuarioServicioImpl.find(usrLicitacion.get(0).getIdUsuario()));
                }
            }
            //
            orden.setAutorizacionesOrden(autorizacionesOrden);
            //Envia correo de solicita
            if (notificacionesOrdenRemote.enviarNotificacionOrdenSolicitada(orden,
                    new StringBuilder().append("Solicitó la Orden: (").append(orden.getConsecutivo()).append("), de la Requisición: (").append(orden.getRequisicion().getConsecutivo()).append(")").toString(),
                    listaContactosOrden, items)) {
                //Notificar
                if (notificacionesOrdenRemote.enviarNotificacionAprobarOrden(autorizacionesOrden.getAutorizaGerencia().getEmail(), "",
                        orden,
                        new StringBuilder().append("Vo. Bo. Orden: (").append(orden.getConsecutivo()).append("), de la Requisición: (").append(orden.getRequisicion().getConsecutivo()).append(")").toString(),
                        listaContactosOrden, items)) {
                    solicitada = true;
                }
            }
            //Editar la OC/S
            if (solicitada) {
                //estado de la ocs
                autorizacionesOrden.setEstatus(estatusServicioImpl.find(Constantes.ORDENES_SIN_APROBAR));
                autorizacionesOrdenRemote.editar(autorizacionesOrden);
                if (orden.getUuid() == null) {
                    orden.setUuid(UUID.randomUUID().toString());
                }
                //
                edit(orden);
                //Verifica monto
                Date fechaFin = autorizacionesOrdenRemote.buscarPorOrden(orden.getId()).getFechaSolicito();
                Date fInico = siManejoFechaLocal.fechaRestarDias(fechaFin, 30);
                verificaProveedorMonto(orden.getId(), orden.getProveedor().getId(), Constantes.FMT_yyyyMMdd.format(fInico),
                        Constantes.FMT_yyyyMMdd.format(orden.getFecha()), orden.getGerencia().getId(), orden.getTotalUsd(),
                        orden.getReferencia(), fechaFin, orden.getConsecutivo(), orden.getCompania().getRfc());
                //
                enviarNotificacion(orden.getConsecutivo(), orden.getReferencia() + ", " + orden.getProveedor().getNombre(), orden.getAutorizacionesOrden().getAutorizaGerencia().getId());
            }
        } catch (Exception e) {
            LOGGER.error(this, "", e);
            solicitada = false;
        }
        //
        return solicitada;
    }

    private void verificaProveedorMonto(int idOrden, int idProveedor, String inicio, String fin, int idGerencia, double totalUSD, String referencia, Date fecha, String codigo, String rfcEmpresa) {
        try {
            clearQuery();
            Orden or = find(idOrden);
            //Verificamos si el proveedor tiene contrato
            Date di = Constantes.FMT_yyyyMMdd.parse(inicio);
            Date df = Constantes.FMT_yyyyMMdd.parse(fin);

            CompaniaAcumuladoVo compA = ocCompaniaAcumuladoRemote.montoAcumuladoPorEmpresa(rfcEmpresa);
            LOGGER.info(this, "Empresa : : :  " + compA.getCompania() + " : : : Verifica Monto : : " + compA.isVerificaMonto());
            if (compA.isVerificaMonto()) {
                //     pvProveedorCompaniaRemote.buscarRelacionProveedorCompania(idProveedor, rfcEmpresa);
                List<Convenio> lc = convenioRemote.getConveniosPorProveedor(proveedorServicioImpl.find(idProveedor).getNombre());
                if (lc.isEmpty()) { // proveedor no tiene contrato
                    guardarVerificaMonto(idOrden, idProveedor, inicio, fin, idGerencia, totalUSD, referencia, fecha, codigo, compA.getMontoDolar());
                } else {
                    List<ContratoVO> lisc = convenioRemote.getListConvenioVigente(idProveedor, or.getApCampo().getId(), null);
                    if (lisc.isEmpty()) { // El proveedor no tiene contrato vigente y se verifica si el sa hay contrato vencido en un rango de  fecha
                        List<ContratoVO> list = convenioRemote.getContratoPorFecha(di, df, or.getApCampo().getId());// verifica si el proveedor tiene contrato en el rango de 30 dias
                        if (list.isEmpty()) { // No tiene contrato vigente en el rango de fechas a evaluar
                            guardarVerificaMonto(idOrden, idProveedor, inicio, fin, idGerencia, totalUSD, referencia, fecha, codigo, compA.getMontoDolar());
                        } else { // El proveedor tiene contrato vigente entre el rango de fechas de hoy - 30 dias
                            String fechaIni = Constantes.FMT_yyyyMMdd.format(siManejoFechaLocal.fechaSumarDias(list.get(0).getFechaVencimiento(), 1)); // //Sacamos la fecha del utimo contrato vigente
                            guardarVerificaMonto(idOrden, idProveedor, fechaIni, fin, idGerencia, totalUSD, referencia, fecha, codigo, compA.getMontoDolar());
                        }
                    } else { //El proveedor tiene cotrato viegente a hoy
                        or.setSuperaMonto(Constantes.BOOLEAN_FALSE);
                        or.setFechaModifico(new Date());
                        or.setHoraModifico(new Date());
                        edit(or);
                    }
                }
            } else {
                or.setSuperaMonto(Constantes.BOOLEAN_FALSE);
                or.setFechaModifico(new Date());
                or.setHoraModifico(new Date());
                edit(or);
            }
        } catch (ParseException ex) {
            LOGGER.info(this, "Error: convertir fecha: " + ex.getMessage());
        }
    }

    private void guardarVerificaMonto(int idOrden, int idProveedor, String inicio, String fin, int idGerencia, double totalUSD, String referencia, Date fecha, String codigo,
            double montoAcumulado) {
        clearQuery();
        Orden or = find(idOrden);
        boolean v = true;
        double total = 0;
        //query.append(" SELECT case  whenround(sum(a.TOTAL_USD)) >= ").append(Constantes.MONTO_MAXIMO_REVISA_USD).append(" then round(sum(a.TOTAL_USD)) ").append(" when round(sum(a.TOTAL_USD))  is null  then 0 ").append(" when round(sum(a.TOTAL_USD))  < ").append(Constantes.MONTO_MAXIMO_REVISA_USD).append("  then 0 end ").append(" FROM ORDEN a, autorizaciones_orden au where au.orden = a.id and a.proveedor = ").append(idProveedor).append(" and au.fecha_solicito between cast('").append(inicio).append("' as date) and cast('").append(fin).append("' as date) and a.GERENCIA = ").append(idGerencia).append("  and a.eliminado = 'False'");

        query.append("SELECT case  when round(sum(a.TOTAL_USD)) >= ").append(montoAcumulado).append(" then round(sum(a.TOTAL_USD)) ");
        query.append("  when round(sum(a.TOTAL_USD))  is null  then 0 ");
        query.append("  when round(sum(a.TOTAL_USD))  < ").append(montoAcumulado).append("  then 0 end ");
        query.append(" FROM ORDEN a, autorizaciones_orden au where au.orden = a.id and a.proveedor = ").append(idProveedor);
        query.append(" and au.ESTATUS <> ").append(Constantes.ORDENES_CANCELADAS);
        query.append("      and au.fecha_solicito between cast('").append(inicio).append("' as date) and cast('").append(fin).append("' as date)");
        query.append("      and a.GERENCIA = ").append(idGerencia).append("  and a.eliminado = 'False'");

        Object t = (Object) em.createNativeQuery(query.toString()).getSingleResult();

        if (t != null) {
            total = (Double) t;
        }

        //if (total >= Constantes.MONTO_MAXIMO_REVISA_USD) {
        if (total >= montoAcumulado) {
            //Recupera las OC/S
            clearQuery();
            query.append("select o.consecutivo, p.nombre, g.nombre, o.referencia, o.fecha, ");
            query.append("  round(o.total_USD::numeric, 2), pot.nombre  from AUTORIZACIONES_ORDEN ao ");
            query.append("      inner join orden o on ao.ORDEN = o.ID");
            query.append("      inner join PROVEEDOR p on o.PROVEEDOR = p.ID");
            query.append("      inner join GERENCIA g on o.GERENCIA = g.ID");
            query.append("      inner join PROYECTO_OT pot on o.PROYECTO_OT = pot.ID");
            query.append("  where o.proveedor = ").append(idProveedor);
            query.append("  and o.FECHA between cast('").append(inicio).append("' as date) and cast('").append(fin).append("' as date) ");
            query.append("  and o.GERENCIA = ").append(idGerencia);
            query.append("  and ao.ESTATUS <> ").append(Constantes.ORDENES_CANCELADAS);
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            List<OrdenVO> lor = new ArrayList<>();
            for (Object[] object : lo) {
                lor.add(castOrdenVO(object));
            }
            if (lor.size() > 1) {
                if (v) {
                    or.setSuperaMonto(Constantes.BOOLEAN_TRUE);
                    or.setFechaModifico(new Date());
                    or.setHoraModifico(new Date());
                    edit(or);
                }
            } else {
                or.setSuperaMonto(Constantes.BOOLEAN_FALSE);
                or.setFechaModifico(new Date());
                or.setHoraModifico(new Date());
                edit(or);
            }

        } else {
            or.setSuperaMonto(Constantes.BOOLEAN_FALSE);
            or.setFechaModifico(new Date());
            or.setHoraModifico(new Date());
            edit(or);
        }
    }

    private OrdenVO castOrdenVO(Object[] object) {
        OrdenVO o = new OrdenVO();
        try {
            o.setConsecutivo((String) object[0]);
            o.setProveedor((String) object[1]);
            o.setGerencia((String) object[2]);
            o.setReferencia((String) object[3]);
            o.setFecha((Date) object[4]);
            o.setTotalUsd((Double) object[5]);
            o.setNombreProyectoOT((String) object[6]);
        } catch (Exception e) {
            LOGGER.info(this, "Exc: cst Orden VO" + e.getMessage());
        }
        return o;
    }

    //-- Aprobar Orden por gerencia solicitante // ihsa comptras
    public boolean aprobarOrdenGerenciaSolicitante(int ordenID, String sesion, String correoSesion) {
        boolean enviada = false;
        //
        Orden orden = find(ordenID);
        //AutorizacionesOrden autorizacionesOrden = autorizacionesOrdenRemote.buscarPorOrden(orden.getId());
        AutorizacionesOrden autorizacionesOrden = orden.getAutorizacionesOrden();

        //
        try {
            orden.setLeida(Constantes.BOOLEAN_FALSE);
            edit(orden);
            //finalizar Notas
            ocOrdenCoNoticiaRemote.finalizarNoticia(sesion, ordenID);
            //
            List<ContactoOrdenVo> listaContactosOrden = getContactosVo(orden.getId());
//
            // Recupera los items
            List<OrdenDetalleVO> items = null;
            if (orden.isMultiproyecto()) {
                items = ordenDetalleServicioImpl.itemsPorOrdenMulti(orden.getId());
            } else {
                items = ordenDetalleServicioImpl.itemsPorOrden(orden.getId());
            }

            autorizacionesOrden.setErrorEnvio(Constantes.BOOLEAN_FALSE);
            if (autorizacionesOrden.getFechaAutorizoGerencia() == null) {
                autorizacionesOrden.setFechaAutorizoGerencia(new Date());
                autorizacionesOrden.setHoraAutorizoGerencia(new Date());
            }
            autorizacionesOrden.setAutorizacionGerenciaAuto(Constantes.BOOLEAN_FALSE);
            orden.setAutorizacionesOrden(autorizacionesOrden);
            //LOGGER.info(this, "USer: " + autorizacionesOrden.getAutorizaMpg().getNombre());
            if (notificacionesOrdenRemote.enviarNotificacionAprobarOrden(
                    autorizacionesOrden.getAutorizaMpg().getEmail(), "", orden,
                    new StringBuilder().append("REVISAR ORDEN: ").append(orden.getConsecutivo()).toString(),
                    listaContactosOrden, items)) {
                autorizacionesOrden.setEstatus(new Estatus(Constantes.ESTATUS_VISTO_BUENO_R));  //120
                enviada = true;
                //
            }
            if (enviada) {
                autorizacionesOrden.setRechazada(Constantes.BOOLEAN_FALSE);
                autorizacionesOrden.setErrorEnvio(Constantes.BOOLEAN_FALSE);
                autorizacionesOrdenRemote.editar(autorizacionesOrden);
            }
            //
            enviarNotificacion(orden.getConsecutivo(), orden.getReferencia() + ", " + orden.getProveedor().getNombre(), autorizacionesOrden.getAutorizaMpg().getId());
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }        //   return enviada;
        return enviada;
    }
    //--- Autorizar Orden por direccion MPG estatus 120

    public void enviarExcepcionSia(String sesion, String para, String modulo, String opcion, String consecutivo, int idOrden) {
        Orden orden = find(idOrden);
        AutorizacionesOrden autorizaOCS = orden.getAutorizacionesOrden();
        int operacion = siOperacionRemote.buscarPorNombre(Constantes.OPERACION_ERROR);
        ordenSiMovimientoRemote.guardarMovimiento(sesion, orden.getId(), "Ocurrio una excepcion en el envio de la OC/S.", "SIA", operacion);
        autorizaOCS.setErrorEnvio(Constantes.BOOLEAN_TRUE);
        autorizacionesOrdenRemote.editar(autorizaOCS);
        //
        StringBuilder sbM = new StringBuilder();
        sbM.append("No se pudo aprobar la Orden de Compra ").append(consecutivo).append(". El problema ya fue notificado al Equipo de Soporte para su oportuna revisión.");
        sbM.append(" En cuanto se resuelva el inconveniente se le notificará.");
        String asunto = "Excepción en ".concat(opcion).concat(" la OC/S: ").concat(consecutivo);
        //out.println("Ausunto:  " + asunto);
        notificacionesOrdenRemote.enviarExcepcionSIA(para, "", asunto, modulo, opcion, sbM.toString());
    }

    public boolean autorizarOrdenMPG(int ordenID, String sesion, String correoSesion) {
        boolean enviada = false;
        boolean enviadaTarea = false;
        Usuario usr = usuarioServicioImpl.find(sesion);
        Orden orden = find(ordenID);
        //AutorizacionesOrden autorizacionesOrden = autorizacionesOrdenRemote.buscarPorOrden(orden.getId());
        AutorizacionesOrden autorizacionesOrden = orden.getAutorizacionesOrden();

        try {
            //
            orden.setLeida(Constantes.BOOLEAN_FALSE);
            //finalizar Notas
            ocOrdenCoNoticiaRemote.finalizarNoticia(sesion, ordenID);
            //
            // Recupera los items
            List<OrdenDetalleVO> items = null;
            if (orden.isMultiproyecto()) {
                items = ordenDetalleServicioImpl.itemsPorOrdenMulti(orden.getId());
            } else {
                items = ordenDetalleServicioImpl.itemsPorOrden(orden.getId());
            }
            List<ContactoOrdenVo> listaContactosOrden = this.getContactosVo(orden.getId());
            // verificar los montos de la orden si es mayor a 5000 usd mandarla a autorizar con MPG y valida que el monot no haya pasado los 5000 dolares acumulados
            if (orden.getTotalUsd() >= orden.getCompania().getMontoRevisa() || orden.isSuperaMonto()) {

                if (autorizacionesOrden.getFechaAutorizoMpg() == null) {
                    autorizacionesOrden.setFechaAutorizoMpg(new Date());
                    autorizacionesOrden.setHoraAutorizoMpg(new Date());
                }
                autorizacionesOrden.setAutorizacionMpgAuto(Constantes.BOOLEAN_FALSE);
                orden.setAutorizacionesOrden(autorizacionesOrden);
                if (notificacionesOrdenRemote.enviarNotificacionOrden(
                        orden.getAutorizacionesOrden().getAutorizaIhsa().getEmail(),
                        "", "",
                        orden,
                        new StringBuilder().append("APROBAR ORDEN: ").append(orden.getConsecutivo()).toString(),
                        listaContactosOrden, items)) {
                    autorizacionesOrden.setEstatus(estatusServicioImpl.find(Constantes.ESTATUS_REVISADA)); //130
                    enviada = true;
                }
            } else {
                if (autorizaOCS(orden, usr, listaContactosOrden)) {
                    enviadaTarea = true;
                    LOGGER.info(this, "Se autorizó y envió la OC/S: " + orden.getConsecutivo());
                }

            }
            if (enviada) {                //
                autorizacionesOrden.setErrorEnvio(Constantes.BOOLEAN_FALSE);
                autorizacionesOrden.setRechazada(Constantes.BOOLEAN_FALSE);
                autorizacionesOrdenRemote.editar(autorizacionesOrden);
                edit(orden);
            }
            if (enviada) {
                autorizacionesOrden.setRechazada(Constantes.BOOLEAN_FALSE);
                autorizacionesOrdenRemote.editar(autorizacionesOrden);
                //
                enviarNotificacion(orden.getConsecutivo(), orden.getReferencia() + ", " + orden.getProveedor().getNombre(), autorizacionesOrden.getAutorizaIhsa().getId());
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, "", ex);
        }
        return (enviada || enviadaTarea);
    }

    /**
     * REvisa el SOCIO
     *
     * @param orden MLUIS
     * @return
     */
    public boolean revisarOrdenExterno(Orden orden) {
        boolean enviada = false;
        orden.setLeida(Constantes.BOOLEAN_FALSE);
        try {
            List<ContactoOrdenVo> listaContactosOrden = getContactosVo(orden.getId());

            AutorizacionesOrden autorizacionesOrden = autorizacionesOrdenRemote.buscarPorOrden(orden.getId());
            autorizacionesOrden.setFechaAutorizoFinanzas(new Date());
            autorizacionesOrden.setHoraAutorizoFinanzas(new Date());
            autorizacionesOrden.setAutorizacionFinanzasAuto(Constantes.BOOLEAN_FALSE);
            orden.setAutorizacionesOrden(autorizacionesOrden);
            LOGGER.info(this, "USer: " + autorizacionesOrden.getAutorizaIhsa().getNombre());
            // Recupera los items
            List<OrdenDetalleVO> items = ordenDetalleServicioImpl.itemsPorOrden(orden.getId());
            if (notificacionesOrdenRemote.enviarNotificacionAprobarOrden(autorizacionesOrden.getAutorizaIhsa().getEmail(), "",
                    orden, new StringBuilder().append("APROBAR ORDEN: ").append(orden.getConsecutivo()).toString(), listaContactosOrden, items)) {
                autorizacionesOrden.setEstatus(estatusServicioImpl.find(Constantes.ESTATUS_REVISADA)); //130
                enviada = true;
            }
            if (enviada) {
                autorizacionesOrden.setRechazada(Constantes.BOOLEAN_FALSE);
                autorizacionesOrdenRemote.editar(autorizacionesOrden);
                //
                edit(orden);
            }
        } catch (Exception ex) {
            //    enviarExcepcion("Aprobar", orden.getAutorizacionesOrden().getAutorizaFinanzas().getEmail(), orden.getConsecutivo());
            LOGGER.fatal(this, ex.getMessage());
        }
        return enviada;
    }

    //-- Autorizar Orden por direccion IHSA
    public boolean autorizarOrdenIHSA(int ordenID, String sesion, String correoSesion) {
        boolean enviada = false;
        boolean enviadaTarea = false;
        Usuario usr = usuarioServicioImpl.find(sesion);
        //   for (OrdenVO ordenVO : lor) {
        Orden orden = find(ordenID);
        orden.setLeida(Constantes.BOOLEAN_FALSE);
        edit(orden);
        List<OrdenDetalleVO> items = null;
        if (orden.isMultiproyecto()) {
            items = ordenDetalleServicioImpl.itemsPorOrdenMulti(orden.getId());
        } else {
            items = ordenDetalleServicioImpl.itemsPorOrden(orden.getId());
        }
        AutorizacionesOrden autorizacionesOrden = orden.getAutorizacionesOrden();// this.autorizacionesOrdenRemote.buscarPorOrden(orden.getId());
        try {
            List<ContactoOrdenVo> listaContactosOrden = getContactosVo(orden.getId());
            if (orden.getCompania().isSocio()) {
                // valaida que el monot no revsa los 5000 dolares y el monto acumulado no pasa de 5000 dolares
                if (orden.getTotalUsd() >= orden.getCompania().getMontoAutorizaAlfa()
                        || orden.isSuperaMonto()) {
                    String correos = traerCorreosXRol(Constantes.ROL_SOCIO, Constantes.MODULO_COMPRA, orden.getApCampo().getId());
                    if (autorizacionesOrden.getFechaAutorizoIhsa() == null) {
                        autorizacionesOrden.setFechaAutorizoIhsa(new Date());
                        autorizacionesOrden.setHoraAutorizoIhsa(new Date());
                        autorizacionesOrden.setAutorizacionIhsaAuto(Constantes.BOOLEAN_FALSE);
                        orden.setAutorizacionesOrden(autorizacionesOrden);
                    }
                    if (correos != null && !correos.isEmpty() && notificacionesOrdenRemote.enviarNotificacionOrden(
                            correos, "", "", orden, new StringBuilder().append("APROBAR ORDEN: ").append(orden.getConsecutivo()).toString(),
                            listaContactosOrden, items)) {

                        autorizacionesOrden.setAutorizacionFinanzasAuto(Constantes.BOOLEAN_FALSE);
                        autorizacionesOrden.setEstatus(new Estatus(Constantes.ESTATUS_POR_APROBAR_SOCIO)); //135
                        enviada = true;
                    } else {
                        autorizacionesOrden.setFechaAutorizoIhsa(null);
                        autorizacionesOrden.setHoraAutorizoIhsa(null);
                        autorizacionesOrden.setAutorizacionIhsaAuto(Constantes.BOOLEAN_FALSE);
                    }

                } else {
                    if (autorizaOCS(orden, usr, listaContactosOrden)) {
                        enviadaTarea = true;
                        LOGGER.info(this, "Se autorizó y envió la OC/S: " + orden.getConsecutivo());
                    }
                }
            } else { // todo el resto  de empresas (no socio)

                //vlaida que el monot no revsa los 20000 dolares y el monto acumulado no pasa de 5000 dolares
                boolean v = ocCampoProveedorLocal.estaProveedor(orden.getProveedor().getId(), orden.getApCampo().getId());
                if (v) {
                    if (autorizarTareaCompra(ordenID, sesion, correoSesion, Constantes.TRUE)) {
                        enviadaTarea = true;
                        LOGGER.info(this, "Se autorizó y envió la OC/S: " + orden.getConsecutivo());
                    } else {
                        throw new Exception("No se pudo autorizar la orden de compra y/o servicio, por favor notifique el problema a: sia@ihsa.mx");
                    }
                } else {
                    if (orden.getTotalUsd() >= orden.getCompania().getMontoAutoriza() || orden.isSuperaMonto()) {
                        if (autorizacionesOrden.getFechaAutorizoIhsa() == null) {
                            autorizacionesOrden.setFechaAutorizoIhsa(new Date());
                            autorizacionesOrden.setHoraAutorizoIhsa(new Date());
                            autorizacionesOrden.setAutorizacionIhsaAuto(Constantes.BOOLEAN_FALSE);
                        }
                        autorizacionesOrden.setAutorizacionComprasAuto(Constantes.BOOLEAN_FALSE);
                        orden.setAutorizacionesOrden(autorizacionesOrden);
                        autorizacionesOrden.setEstatus(estatusServicioImpl.find(Constantes.ESTATUS_APROBADA)); //140
                        enviada = true;
                    } else { // La osc no supera los montos
                        if (autorizaOCS(orden, usr, listaContactosOrden)) {
                            enviadaTarea = true;
                            enviada = true;
                            LOGGER.info(this, "Se autorizó y envió la OC/S: " + orden.getConsecutivo());
                        }
                    }
                }
            }
            if (enviada) {
                //
                autorizacionesOrden.setRechazada(Constantes.BOOLEAN_FALSE);
                autorizacionesOrden.setErrorEnvio(Constantes.BOOLEAN_FALSE);
                autorizacionesOrdenRemote.editar(autorizacionesOrden);
                //finalizar Notas
                ocOrdenCoNoticiaRemote.finalizarNoticia(sesion, ordenID);
                //
                enviarNotificacion(orden.getConsecutivo(), orden.getReferencia() + ", " + orden.getProveedor().getNombre(), autorizacionesOrden.getAutorizaCompras().getId());
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }
        //  }
        return (enviada || enviadaTarea);
    }

    private String traerCorreosXRol(int rol, int modulo, int campo) {
        try {
            StringBuilder sb = new StringBuilder();
            List<UsuarioVO> lu = usuarioServicioImpl.traerListaRolPrincipalUsuarioRolModulo(rol, modulo, campo);
            for (UsuarioVO usuarioVO : lu) {
                if (sb.length() == 0) {
                    sb.append(usuarioVO.getMail());
                } else {
                    sb.append(",").append(usuarioVO.getMail());
                }
                LOGGER.info(this, "Nombre asignar: " + usuarioVO.getNombre());
            }
            return sb.toString();
        } catch (Exception e) {
            LOGGER.info(this, "Ocurrio un error al traer los correos de asiganción de orden " + e.getMessage());
            return "";
        }
    }

    public boolean autorizarOrdenSocio(int ordenID, String sesion, String correoSesion) {
        boolean enviada = false;
        boolean enviadaTarea = false;
        Usuario usr = usuarioServicioImpl.find(sesion);
        Orden orden = find(ordenID);
        orden.setLeida(Constantes.BOOLEAN_FALSE);
        edit(orden);
        //Finalizar Notas
        ocOrdenCoNoticiaRemote.finalizarNoticia(sesion, ordenID);
        // Recupera los items
        List<OrdenDetalleVO> items = ordenDetalleServicioImpl.itemsPorOrden(orden.getId());
        AutorizacionesOrden autorizacionesOrden = orden.getAutorizacionesOrden();
        try {
            orden.setLeida(Constantes.BOOLEAN_FALSE);
            List<ContactoOrdenVo> listaContactosOrden = getContactosVo(orden.getId());

            if (orden.getCompania().isSocio()) {
                //vlaida que el monot no revsa los 20000 dolares y el monto acumulado no pasa de 5000 dolares
                if (orden.getTotalUsd() >= orden.getCompania().getMontoAutoriza() || orden.isSuperaMonto()) {
                    autorizacionesOrden.setAutorizaFinanzas(usr);
                    autorizacionesOrden.setFechaAutorizoFinanzas(new Date());
                    autorizacionesOrden.setHoraAutorizoFinanzas(new Date());
                    autorizacionesOrden.setAutorizacionFinanzasAuto(Constantes.BOOLEAN_FALSE);

                    //
                    autorizacionesOrden.setAutorizacionComprasAuto(Constantes.BOOLEAN_FALSE);
                    orden.setAutorizacionesOrden(autorizacionesOrden);
                    autorizacionesOrden.setEstatus(estatusServicioImpl.find(Constantes.ESTATUS_APROBADA)); //140
                    enviada = true;
                } else {
                    if (autorizaOCS(orden, usr, listaContactosOrden)) {
                        enviadaTarea = true;
                        LOGGER.info(this, "Se autorizó y envió la OC/S: " + orden.getConsecutivo());
                    } else {
                        throw new Exception("No se pudo autorizar la orden de compra y/o servicio, por favor notifique el problema a: sia@ihsa.mx");
                    }

                }
            }

            if (enviada) {
                //
                autorizacionesOrden.setRechazada(Constantes.BOOLEAN_FALSE);
                autorizacionesOrden.setErrorEnvio(Constantes.BOOLEAN_FALSE);
                autorizacionesOrdenRemote.editar(autorizacionesOrden);
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
            enviada = false;
        }
        return (enviada || enviadaTarea);
    }

    //-- Autorizar Orden por gerencia compras
    public boolean autorizarOrdenCompras(int ordenID, String sesion, String correoSesion) throws Exception {
        boolean enviada = false;
        Usuario usr = usuarioServicioImpl.find(sesion);
        Orden orden = find(ordenID);
        File ordenPDF = null;
        File pdfCG = null;
        try {
            ordenPDF = siaPDFRemote.getPDF(orden, usr, true);
            pdfCG = siaPDFRemote.buscarPdfCG(orden.getCompania());

            if (ordenPDF == null || !ordenPDF.exists()) {
                throw new Exception("Error: El archivo PDF de la orden de compra no pudo ser generado correctamente. Favor de contactar al equipo de soporte.");
            }

            List<ContactoOrdenVo> listaContactosOrden = getContactosVo(orden.getId());
            // Recupera los items
            List<OrdenDetalleVO> items = ordenDetalleServicioImpl.itemsPorOrden(orden.getId());

            if (listaContactosOrden == null || listaContactosOrden.isEmpty()) {
                throw new Exception("Error: Los contactos del proveedor relacionados con la orden de compra fueron eliminados. "
                        + "Se requiere actualizar los contactos del proveedor.  ");
            }

            AutorizacionesOrden autorizacionesOrden = orden.getAutorizacionesOrden();

            if (notificacionesOrdenRemote.enviarNotificacionOrdenProveedor(orden, listaContactosOrden, ordenPDF, pdfCG, items)
                    && notificacionesOrdenRemote.enviarNotificacionOrdenAnalista(orden)) {

                autorizacionesOrden.setEstatus(estatusServicioImpl.find(Constantes.ESTATUS_ENVIADA_PROVEEDOR)); // 160 enviada proveedor

                if (autorizacionesOrden.getFechaAutorizoCompras() == null) {
                    autorizacionesOrden.setFechaAutorizoCompras(new Date());
                    autorizacionesOrden.setHoraAutorizoCompras(new Date());
                    autorizacionesOrden.setAutorizacionComprasAuto(Constantes.BOOLEAN_FALSE);
                    autorizacionesOrden.setFechaEnvioProveedor(new Date());
                    autorizacionesOrden.setHoraEnvioProveedor(new Date());
                }

                autorizacionesOrden.setRechazada(Constantes.BOOLEAN_FALSE);
                autorizacionesOrden.setErrorEnvio(Constantes.BOOLEAN_FALSE);
                autorizacionesOrdenRemote.editar(autorizacionesOrden);

                orden.setAutorizacionesOrden(autorizacionesOrden);
                orden.setLeida(Constantes.BOOLEAN_FALSE);
                //Finalizar Notas
                ocOrdenCoNoticiaRemote.finalizarNoticia(sesion, ordenID);
                enviada = true;
                edit(orden);
            } else {
                throw new Exception("Error: El sistema no pudo enviar los correos de notificación al proveedor ni al comprador. Favor de contactar al equipo de soporte.");
            }
        } catch (Exception ex) {
            Logger
                    .getLogger(OrdenImpl.class
                            .getName()).log(Level.SEVERE, new StringBuilder().append(orden.getConsecutivo())
                            .append(" ").append(ex.getMessage()).toString());

            if (ordenPDF != null && ordenPDF.exists() && ordenPDF.delete()) {
                Logger
                        .getLogger(OrdenImpl.class
                                .getName()).log(Level.SEVERE, new StringBuilder().append(orden.getConsecutivo())
                                .append(" ").append("El archivo PDF de la orden se genero correctamente, pero fue eliminado por un error en el envio del correo.").toString());
            }
            throw ex;
        }

        return enviada;
    }

    public boolean autorizarOrdenCompras(Orden orden, Usuario usr) {
        boolean enviada = false;
        boolean enviadaTarea = false;
        orden.setLeida(Constantes.BOOLEAN_FALSE);
        File ordenPDF = null;
        File pdfCG = null;

        try {
            ordenPDF = siaPDFRemote.getPDF(orden, usr, true);
            pdfCG = siaPDFRemote.buscarPdfCG(orden.getCompania());

        } catch (Exception e) {
            ordenPDF = null;
            Logger
                    .getLogger(OrdenImpl.class
                            .getName()).log(Level.SEVERE, e.getMessage());
        }
//            if (ordenPDF == null || !ordenPDF.exists()) {
//                throw new Exception("El archivo PDF de la orden no se genero corectamente.");
//            }

        List<ContactoOrdenVo> listaContactosOrden = getContactosVo(orden.getId());
//            if (listaContactosOrden == null || listaContactosOrden.isEmpty()) {
//                throw new Exception("La orden no tiene contactos relacionados. ");
//            }
        AutorizacionesOrden autorizacionesOrden = orden.getAutorizacionesOrden();
        // Recupera los items
        List<OrdenDetalleVO> items = ordenDetalleServicioImpl.itemsPorOrden(orden.getId());
        try {
            if (notificacionesOrdenRemote.enviarNotificacionOrdenProveedor(orden, listaContactosOrden, ordenPDF, pdfCG, items)
                    && notificacionesOrdenRemote.enviarNotificacionOrdenAnalista(orden)) {

                autorizacionesOrden.setEstatus(estatusServicioImpl.find(Constantes.ESTATUS_ENVIADA_PROVEEDOR)); // 160 enviada proveedor
                //ESTATUS_GENERAR_EXCEL

                if (autorizacionesOrden.getFechaAutorizoCompras() == null) {
                    autorizacionesOrden.setFechaAutorizoCompras(new Date());
                    autorizacionesOrden.setHoraAutorizoCompras(new Date());
                    autorizacionesOrden.setAutorizacionComprasAuto(Constantes.BOOLEAN_FALSE);
                    autorizacionesOrden.setFechaEnvioProveedor(new Date());
                    autorizacionesOrden.setHoraEnvioProveedor(new Date());
                }

                autorizacionesOrden.setRechazada(Constantes.BOOLEAN_FALSE);
                autorizacionesOrden.setErrorEnvio(Constantes.BOOLEAN_FALSE);
                autorizacionesOrdenRemote.editar(autorizacionesOrden);
                orden.setAutorizacionesOrden(autorizacionesOrden);
//                edit(orden);
                //-- Finalizar las notas que tenga la orden de compra
                List<NotaOrden> listaNotas = getNotasPorOrdenParaFinalizar(orden.getId());
                for (NotaOrden lista : listaNotas) {
                    lista.setFinalizada(Constantes.BOOLEAN_TRUE);
                    notaOrdenServicioImpl.edit(lista);
                }
                enviada = true;
                edit(orden);

                //Finalizar Notas
                ocOrdenCoNoticiaRemote.finalizarNoticia(usr.getId(), orden.getId());
                //
            } else {
                orden.setNavCode(null);
                orden.setUrl(null);
                orden.setCheckcode(null);
                edit(orden);
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
            enviada = false;
        }

        return enviada;
    }

    private boolean autorizaOCS(Orden orden, Usuario usr, List<ContactoOrdenVo> listaContactosOrden) throws Exception {
        boolean enviada = false;
        try {
            AutorizacionesOrden autorizacionesOrdenOld = orden.getAutorizacionesOrden();
            AutorizacionesOrden autorizacionesOrdenNew = orden.getAutorizacionesOrden();
            if (null != autorizacionesOrdenNew.getEstatus().getId()) {
                switch (autorizacionesOrdenNew.getEstatus().getId()) {
                    case Constantes.ESTATUS_VISTO_BUENO_R:
                        autorizacionesOrdenNew.setAutorizacionMpgAuto(Constantes.BOOLEAN_FALSE);
                        autorizacionesOrdenNew.setAutorizacionIhsaAuto(Constantes.BOOLEAN_TRUE);
                        autorizacionesOrdenNew.setAutorizacionComprasAuto(Constantes.BOOLEAN_TRUE);
                        break;
                    case Constantes.ESTATUS_REVISADA:
                        autorizacionesOrdenNew.setAutorizacionIhsaAuto(Constantes.BOOLEAN_FALSE);
                        autorizacionesOrdenNew.setAutorizacionComprasAuto(Constantes.BOOLEAN_TRUE);
                        break;
                    case Constantes.ESTATUS_APROBADA:
                        autorizacionesOrdenNew.setAutorizacionComprasAuto(Constantes.BOOLEAN_FALSE);
                        break;
                    default:
                        break;
                }
            }
            //
            if (autorizacionesOrdenNew.getFechaAutorizoMpg() == null) {
                autorizacionesOrdenNew.setFechaAutorizoMpg(new Date());
                autorizacionesOrdenNew.setHoraAutorizoMpg(new Date());
            }
            //-- Autoriza ihsa
            if (autorizacionesOrdenNew.getFechaAutorizoIhsa() == null) {
                autorizacionesOrdenNew.setFechaAutorizoIhsa(new Date());
                autorizacionesOrdenNew.setHoraAutorizoIhsa(new Date());

            }
            //-- Autoriza Socio
            if ((orden.getCompania().isSocio())
                    && autorizacionesOrdenNew.getFechaAutorizoFinanzas() == null) {
                List<UsuarioRolVo> socios = siUsuarioRolRemote.traerUsuarioPorRolModulo(Constantes.ROL_SOCIO, Constantes.MODULO_COMPRA, orden.getApCampo().getId());
                if (socios != null && socios.size() > 0) {
                    autorizacionesOrdenNew.setAutorizaFinanzas(usuarioServicioImpl.find(socios.get(0).getIdUsuario()));
                    autorizacionesOrdenNew.setFechaAutorizoFinanzas(new Date());
                    autorizacionesOrdenNew.setHoraAutorizoFinanzas(new Date());
                    autorizacionesOrdenNew.setAutorizacionFinanzasAuto(Constantes.BOOLEAN_FALSE);
                }
            }
            //-- Autoriza compras
            if (autorizacionesOrdenNew.getFechaAutorizoCompras() == null) {
                autorizacionesOrdenNew.setFechaAutorizoCompras(new Date());
                autorizacionesOrdenNew.setHoraAutorizoCompras(new Date());
                autorizacionesOrdenNew.setAutorizacionComprasAuto(Constantes.BOOLEAN_TRUE);
            }
            //Aquí esta el cambio de estatus para agregar la carta de intención
            String asunto = "";
            if (orden.getApCampo().isCartaIntencion()) {
                if (proveedorSinCartaIntencionLocal.buscarProveedorCampo(orden.getApCampo().getId(), orden.getProveedor().getId()) == null) {
                    autorizacionesOrdenNew.setEstatus(estatusServicioImpl.find(OrdenEstadoEnum.POR_ACEPTAR_CARTA_INTENCION.getId())); // 150 = Autorizada x compras
                    //enviar la notificación a proveedor (Carta de intención).
                    List<OrdenDetalleVO> items = ordenDetalleServicioImpl.itemsPorOrden(orden.getId());
                    notificacionesOrdenRemote.enviarCartaIntencion(orden, listaContactosOrden, items);
                } else {
                    aceptarRepse(orden.getId(), null, usr.getId(), Boolean.FALSE);
                }
            } else {
                asunto = "Revisar REPSE";
                autorizacionesOrdenNew.setEstatus(estatusServicioImpl.find(OrdenEstadoEnum.POR_REVISAR_REPSE.getId())); // 150 = Autorizada x compras
                // notifica a revision repse
                List<OrdenDetalleVO> items = itemsPorOrdenCompra(orden.getId());
                notificacionesOrdenRemote.enviarCorreoAceptarCartaIntencion(orden, items, asunto);
            }
            //
            autorizacionesOrdenNew.setRechazada(Constantes.BOOLEAN_FALSE);
            autorizacionesOrdenNew.setErrorEnvio(Constantes.BOOLEAN_FALSE);
            autorizacionesOrdenRemote.editar(autorizacionesOrdenNew);
            //
            orden.setLeida(Constantes.BOOLEAN_FALSE);
            orden.setAutorizacionesOrden(autorizacionesOrdenNew);
            edit(orden);
            //
            autorizacionesOrdenRemote.editar(autorizacionesOrdenNew);
//            //
            enviada = true;
        } catch (Exception ex) {
            throw ex;
        }
        return enviada;
    }

    public boolean autorizarTareaCompra(int ordenID, String sesion,
            String correoSesion, boolean aprobarIHSA
    ) {
        boolean enviada = false;
        boolean enviarCompras = true;
        Orden orden = find(ordenID);
        orden.setLeida(Constantes.BOOLEAN_FALSE);
        //
        List<ContactoOrdenVo> listaContactosOrden = getContactosVo(orden.getId());
        // Recupera los items
        List<OrdenDetalleVO> items = null;
        if (orden.isMultiproyecto()) {
            items = ordenDetalleServicioImpl.itemsPorOrdenMulti(orden.getId());
        } else {
            items = ordenDetalleServicioImpl.itemsPorOrden(orden.getId());
        }

        AutorizacionesOrden autorizacionesOrden = orden.getAutorizacionesOrden();
        try {
            if (aprobarIHSA) {
                autorizacionesOrden.setFechaAutorizoIhsa(new Date());
                autorizacionesOrden.setHoraAutorizoIhsa(new Date());
                autorizacionesOrden.setAutorizacionIhsaAuto(Constantes.BOOLEAN_FALSE);
            }
            if (orden.getApCampo() != null && orden.getApCampo().getTipo() != null
                    && orden.getApCampo().getCompania() != null && orden.getApCampo().getCompania().getMontoLicitacion() > 0
                    && orden.getTotalUsd() >= orden.getApCampo().getCompania().getMontoLicitacion()
                    && autorizacionesOrden != null && autorizacionesOrden.getAutorizaLicitacion() != null) {
                autorizacionesOrden.setEstatus(new Estatus(Constantes.ORDENES_SIN_AUTORIZAR_LICITACION));
                enviarCompras = false;
            } else {
                //Aquí esta el cambio de estatus para agregar la carta de intención
                String asunto = "";
                if (orden.getApCampo().isCartaIntencion()) {
                    if (proveedorSinCartaIntencionLocal.buscarProveedorCampo(orden.getApCampo().getId(), orden.getProveedor().getId()) == null) {
                        autorizacionesOrden.setEstatus(estatusServicioImpl.find(OrdenEstadoEnum.POR_ACEPTAR_CARTA_INTENCION.getId())); // 145 = por revisar carta de intenci
                        //enviar la notificación a proveedor (Carta de intención).
                        notificacionesOrdenRemote.enviarCartaIntencion(orden, listaContactosOrden, items);
                    } else {
                        aceptarRepse(ordenID, items, sesion, Boolean.FALSE);
                    }
                } else {
                    asunto = "Revisar REPSE";
                    autorizacionesOrden.setEstatus(estatusServicioImpl.find(OrdenEstadoEnum.POR_REVISAR_REPSE.getId())); // 150 = Autorizada x compras
                    // notifica a revision repse
                    notificacionesOrdenRemote.enviarCorreoAceptarCartaIntencion(orden, items, asunto);
                }
                enviada = true;
                enviarCompras = true;
            }
            //-- Autoriza compras
            autorizacionesOrden.setFechaAutorizoCompras(new Date());
            autorizacionesOrden.setHoraAutorizoCompras(new Date());
            autorizacionesOrden.setAutorizacionComprasAuto(aprobarIHSA);
            autorizacionesOrden.setRechazada(Constantes.BOOLEAN_FALSE);
            autorizacionesOrdenRemote.editar(autorizacionesOrden);
            orden.setAutorizacionesOrden(autorizacionesOrden);
            //Notificar carta intencion

//
//            int dias = siManejoFechaLocal.dias(new Date(), orden.getFecha());
//            if (orden.getFechaEntrega().compareTo(new Date()) < 1) {
//                int diasEntregaHoy = siManejoFechaLocal.dias(orden.getFechaEntrega(), orden.getFecha());
//                orden.setFechaEntrega(siManejoFechaLocal.fechaSumarDias(new Date(), diasEntregaHoy));
//            } else {
//                orden.setFechaEntrega(siManejoFechaLocal.fechaSumarDias(orden.getFechaEntrega(), dias));
//            }
//            edit(orden);
//            //-- Finalizar las notas que tenga la orden de compra
//            if (enviarCompras && this.notificacionesOrdenRemote.enviarNotificacionTarea(orden, listaContactosOrden, items)) {
//                if (TipoRequisicion.AF.equals(orden.getTipo())) {
//                    if (notificacionesOrdenRemote.enviarNotificacionCotabilidad(orden, listaContactosOrden, items)) {
//                        enviada = true;
//                    }
//                } else {
//                    enviada = true;
//                }
//            }
            if (enviada) {
                //Finalizar Notas
                ocOrdenCoNoticiaRemote.finalizarNoticia(sesion, ordenID);
                //
                autorizacionesOrdenRemote.editar(autorizacionesOrden);
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
            enviada = false;
        }
        return enviarCompras ? enviada : !enviarCompras;
    }

    public boolean enviarCompraLicitacion(int ordenID, String sesion,
            String correoSesion, boolean aprobarIHSA
    ) {
        boolean enviada = false;
        Orden orden = find(ordenID);
        orden.setLeida(Constantes.BOOLEAN_FALSE);
        //
        List<ContactoOrdenVo> listaContactosOrden = getContactosVo(orden.getId());
        List<OrdenDetalleVO> items = null;
        if (orden.isMultiproyecto()) {
            items = ordenDetalleServicioImpl.itemsPorOrdenMulti(orden.getId());
        } else {
            items = ordenDetalleServicioImpl.itemsPorOrden(orden.getId());
        }
        AutorizacionesOrden autorizacionesOrden = orden.getAutorizacionesOrden();
        try {

            //autorizacionesOrden.setEstatus(new Estatus(OrdenEstadoEnum.POR_ACEPTAR_CARTA_INTENCION.getId()));//Constantes.ESTATUS_AUTORIZADA));
            //-- Autoriza compras
            autorizacionesOrden.setFechaAutorizoLicitacion(new Date());
            autorizacionesOrden.setHoraAutorizoLicitacion(new Date());
            autorizacionesOrden.setAutorizacionLicitacionAuto(aprobarIHSA);
            autorizacionesOrden.setRechazada(Constantes.BOOLEAN_FALSE);
            autorizacionesOrdenRemote.editar(autorizacionesOrden);
            orden.setAutorizacionesOrden(autorizacionesOrden);
//Aquí esta el cambio de estatus para agregar la carta de intención
            String asunto = "";
            if (orden.getApCampo().isCartaIntencion()) {
                if (proveedorSinCartaIntencionLocal.buscarProveedorCampo(orden.getApCampo().getId(), orden.getProveedor().getId()) == null) {
                    autorizacionesOrden.setEstatus(estatusServicioImpl.find(OrdenEstadoEnum.POR_ACEPTAR_CARTA_INTENCION.getId())); // 150 = Autorizada x compras
                    notificacionesOrdenRemote.enviarCartaIntencion(orden, listaContactosOrden, items);
                    enviada = true;
                } else {
                    aceptarRepse(ordenID, items, sesion, Boolean.FALSE);
                }
            } else {
                asunto = "Revisar REPSE";
                autorizacionesOrden.setEstatus(estatusServicioImpl.find(OrdenEstadoEnum.POR_REVISAR_REPSE.getId())); // 150 = Autorizada x compras
                notificacionesOrdenRemote.enviarCorreoAceptarCartaIntencion(orden, items, asunto);
                enviada = true;
            }
            if (enviada) {
                //Finalizar Notas
                ocOrdenCoNoticiaRemote.finalizarNoticia(sesion, ordenID);
                //
                autorizacionesOrdenRemote.editar(autorizacionesOrden);
            }
            //
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
            enviada = false;
        }
        return enviada;
    }

    public boolean cancelarOrden(Orden orden, String nombreUsrSolicito,
            String idUsrGenero, Object Motivo,
            boolean getContactosOrden, boolean eliminarReq) throws Exception {
        boolean notificacionEnviada = false;
        StringBuilder para = new StringBuilder();
        StringBuilder cc = new StringBuilder();
        List<ContactoOrdenVo> listaContactosOrden = this.getContactosVo(orden.getId());
        //
        cc.append(ContactosdevolucionOrden(orden));
        //
        AutorizacionesOrden autorizacionesOrden = this.autorizacionesOrdenRemote.buscarPorOrden(orden.getId());
        autorizacionesOrden.setEstatus(this.estatusServicioImpl.find(Constantes.ESTATUS_CANCELADA)); // 100 = Cancelada
        Usuario usrSolicita = this.usuarioServicioImpl.buscarPorNombre(nombreUsrSolicito);
        Usuario usrGenero = this.usuarioServicioImpl.find(idUsrGenero);
        autorizacionesOrden.setCancelo(usrSolicita);
        autorizacionesOrden.setFechaCancelo(new Date());
        autorizacionesOrden.setHoraCancelo(new Date());
        autorizacionesOrden.setMotivoCancelo(Motivo.toString());
        autorizacionesOrden.setRechazada(Constantes.BOOLEAN_FALSE);
        autorizacionesOrden.setModifico(usrGenero);
        autorizacionesOrden.setFechaModifico(new Date());
        autorizacionesOrden.setHoraModifico(new Date());

        orden.setAutorizacionesOrden(autorizacionesOrden);
        //
        ordenSiMovimientoRemote.saverOrderMoove(usrGenero, orden.getId(), Motivo.toString(), usrSolicita, Constantes.ID_SI_OPERACION_CANCELAR);

        // Recupera los items
        List<OrdenDetalleVO> items = ordenDetalleServicioImpl.itemsPorOrden(orden.getId());
        if (usrSolicita != null && usrSolicita.getEmail() != null && !usrSolicita.getEmail().isEmpty()) {
            if (cc.toString().isEmpty()) {
                cc.append(usrSolicita.getEmail());
            } else {
                cc.append(", ").append(usrSolicita.getEmail());
            }
        }
        if (usrGenero != null && usrGenero.getEmail() != null && !usrGenero.getEmail().isEmpty()) {
            if (para.toString().isEmpty()) {
                para.append(usrGenero.getEmail());
            } else {
                para.append(", ").append(usrGenero.getEmail());
            }
        }
        if (getContactosOrden) {
            if (para.toString().isEmpty()) {
                para.append(getDestinatariosOrden(listaContactosOrden));
            } else {
                para.append(", ").append(getDestinatariosOrden(listaContactosOrden));
            }
        }

        notificacionEnviada = notificacionesOrdenRemote.enviarNotificacionCancelarOrden(
                para.toString(), cc.toString(), "", orden,
                new StringBuilder().append("ORDEN: ").append(orden.getConsecutivo()).append(" CANCELADA").toString(),
                autorizacionesOrden, listaContactosOrden, items);

        if (notificacionEnviada) {
            if (eliminarReq) {
                //Asigno fecha en que se cancela la requisiciòn
                orden.getRequisicion().setFechaCancelo(new Date());
                orden.getRequisicion().setHoraCancelo(new Date());
                orden.getRequisicion().setCancelo(usrSolicita);
                orden.getRequisicion().setEstatus(estatusServicioImpl.find(50)); // 50 = Cancelada
                requisicionServicioImpl.edit(orden.getRequisicion());
            } else {
                //- - - regresar la requisición
                orden.getRequisicion().setEstatus(estatusServicioImpl.find(Constantes.ESTATUS_ASIGNADA));  //40
                requisicionServicioImpl.edit(orden.getRequisicion());
            }
            // - - - regresar los items d la requisicion
            for (OrdenDetalle lista : getItems(orden.getId())) {
                if (lista.getRequisicionDetalle() != null) {
                    lista.getRequisicionDetalle().setDisgregado(Constantes.BOOLEAN_FALSE);
                    requisicionServicioImpl.actualizarItem(lista.getRequisicionDetalle());
                }
            }
            //-- Marcar como cancelada la orden
            autorizacionesOrdenRemote.editar(autorizacionesOrden);
            //-- Finalizar las notas que tenga la orden de compra
            ocOrdenCoNoticiaRemote.finalizarNoticia(usrGenero.getId(), orden.getId());
        }

        if (notificacionEnviada) {
            DocumentoAnexo pdfFile = siaPDFRemote.buscarOrdenPDF(orden.getNavCode());
            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            if (Strings.isNullOrEmpty(orden.getNavCode())) {
                pdfFile = siaPDFRemote.buscarOrdenPDF(orden.getConsecutivo());
            } else {
                pdfFile = siaPDFRemote.buscarOrdenPDF(orden.getNavCode());
                orden.setNavCode(null);
                edit(orden);
            }

            if (pdfFile != null) {
                almacenDocumentos.borrarDocumento(pdfFile.getRuta() + File.separator + pdfFile.getNombreBase());
            }
        }

        return notificacionEnviada;
    }

    private String getDestinatariosOrden(List<ContactoOrdenVo> listaContactos) {
        StringBuilder destinatarios = new StringBuilder();
        for (ContactoOrdenVo lista : listaContactos) {
            if (destinatarios.toString().isEmpty()) {
                destinatarios.append(lista.getCorreo());
            } else {
                destinatarios.append(",").append(lista.getCorreo());
            }
        }
        return destinatarios.toString();
    }

    private String ContactosdevolucionOrden(Orden orden) {

        StringBuilder mailUsuarios = new StringBuilder();
        mailUsuarios.append(orden.getAutorizacionesOrden().getSolicito().getEmail());

        if (orden.getAutorizacionesOrden().getEstatus().getId() == Constantes.ESTATUS_SOLICITADA_R) {  //110
            mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaGerencia().getEmail());
        }
        if (orden.getAutorizacionesOrden().getEstatus().getId() == Constantes.ESTATUS_VISTO_BUENO_R) { //120
            mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaGerencia().getEmail());
            mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaMpg().getEmail());
        }

        if (orden.getAutorizacionesOrden().getEstatus().getId() == Constantes.ESTATUS_REVISADA) { //130 //5000
            mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaGerencia().getEmail());
            mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaMpg().getEmail());
            if (!orden.getAutorizacionesOrden().isAutorizacionIhsaAuto()) {
                mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaIhsa().getEmail());
            }
        }

        if (orden.getAutorizacionesOrden().getEstatus().getId() == Constantes.ESTATUS_POR_APROBAR_SOCIO) { //135 5000 20000
            mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaGerencia().getEmail());
            mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaMpg().getEmail());
            mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaIhsa().getEmail());
            if (!orden.getAutorizacionesOrden().isAutorizacionComprasAuto()) {
                mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaCompras().getEmail());
            }
        }

        if (orden.getAutorizacionesOrden().getEstatus().getId() == Constantes.ESTATUS_APROBADA) { //140 5000 20000
            mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaGerencia().getEmail());
            mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaMpg().getEmail());
            mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaIhsa().getEmail());

            if (!orden.getAutorizacionesOrden().isAutorizacionFinanzasAuto()
                    && orden.getCompania().isSocio()) {
                mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaFinanzas().getEmail());
            }

            if (!orden.getAutorizacionesOrden().isAutorizacionComprasAuto()) {
                mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaCompras().getEmail());
            }
        }
        if (orden.getAutorizacionesOrden().getEstatus().getId() == Constantes.ESTATUS_AUTORIZADA) { //150 20000
            mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaGerencia().getEmail());
            mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaMpg().getEmail());

            if (!orden.getAutorizacionesOrden().isAutorizacionIhsaAuto()) {
                mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaIhsa().getEmail());
            }

            if (!orden.getAutorizacionesOrden().isAutorizacionComprasAuto()) {
                mailUsuarios.append(",").append(orden.getAutorizacionesOrden().getAutorizaCompras().getEmail());
            }

        }
        return mailUsuarios.toString();

    }

    public boolean devolverOrden(Orden orden, String nombreUsrSolicito, String idUsrGenero, String motivo) throws Exception {
        boolean enviarCorreoDevolucion = false;
        StringBuilder para = new StringBuilder();
        StringBuilder cc = new StringBuilder();
        List<ContactoOrdenVo> listaContactosOrden = getContactosVo(orden.getId());
        // Recupera los items
        List<OrdenDetalleVO> items = ordenDetalleServicioImpl.itemsPorOrden(orden.getId());
        if (orden != null) {
            cc.append(ContactosdevolucionOrden(orden));
        }
        AutorizacionesOrden autorizacionesOrden = autorizacionesOrdenRemote.buscarPorOrden(orden.getId());

        autorizacionesOrden.setEstatus(estatusServicioImpl.find(Constantes.ESTATUS_PENDIENTE_R)); // 101
        Usuario usrSolicita = usuarioServicioImpl.buscarPorNombre(nombreUsrSolicito);
        if (usrSolicita == null) {
            usrSolicita = usuarioServicioImpl.find(nombreUsrSolicito);
        }
        autorizacionesOrden.setMotivoCancelo(motivo);
        autorizacionesOrden.setRechazada(Constantes.BOOLEAN_TRUE);
        Usuario genero = usuarioServicioImpl.find(idUsrGenero);
        autorizacionesOrden.setModifico(genero);
        autorizacionesOrden.setFechaModifico(new Date());
        autorizacionesOrden.setHoraModifico(new Date());
        orden.setAutorizacionesOrden(autorizacionesOrden);
        //Asigno orden fecha hora y usuario de rechazo
        OrdenSiMovimiento ordenSiMovimiento = ordenSiMovimientoRemote.saverOrderMoove(genero,
                orden.getId(),
                motivo,
                usrSolicita,
                Constantes.ID_SI_OPERACION_DEVOLVER);
        UsuarioVO uvo = usuarioServicioImpl.traerResponsableGerencia(orden.getApCampo().getId(), Constantes.GERENCIA_ID_COMPRAS, orden.getCompania().getRfc());

        if (usrSolicita != null && usrSolicita.getEmail() != null && !usrSolicita.getEmail().isEmpty()) {
            cc.append(", ").append(usrSolicita.getEmail());
        }
        if (genero != null && genero.getEmail() != null && !genero.getEmail().isEmpty()) {
            para.append(genero.getEmail());
        }

        enviarCorreoDevolucion = notificacionesOrdenRemote.enviarNotificacionDevolverOrden(
                para.toString(), cc.toString(), uvo.getMail(),
                orden, new StringBuilder().append("ORDEN: ").append(orden.getConsecutivo()).append(" DEVUELTA").toString(),
                listaContactosOrden, ordenSiMovimiento, items);
        if (enviarCorreoDevolucion) {

            //--- Quitarle el iva al monto total si lo tiene aplicado
            if (orden.isConIva()) {
                orden.setIva(.0);
                orden.setTotal(orden.getSubtotal());
                edit(orden);
            }
            //-- Aprueba gerencia
            if (autorizacionesOrden.getFechaAutorizoGerencia() != null) {
                autorizacionesOrden.setFechaAutorizoGerencia(null);
                autorizacionesOrden.setHoraAutorizoGerencia(null);
                autorizacionesOrden.setAutorizaGerencia(null);
            }
            //-- autoriza mpg
            if (autorizacionesOrden.getFechaAutorizoMpg() != null) {
                autorizacionesOrden.setFechaAutorizoMpg(null);
                autorizacionesOrden.setHoraAutorizoMpg(null);
                autorizacionesOrden.setAutorizaMpg(null);
            }

            //-- Autoriza Finanzas
            if (autorizacionesOrden.getFechaAutorizoFinanzas() != null) {
                autorizacionesOrden.setFechaAutorizoFinanzas(null);
                autorizacionesOrden.setHoraAutorizoFinanzas(null);
                autorizacionesOrden.setAutorizaFinanzas(null);
            }
            //-- Autoriza ihsa
            if (autorizacionesOrden.getFechaAutorizoIhsa() != null) {
                autorizacionesOrden.setFechaAutorizoIhsa(null);
                autorizacionesOrden.setHoraAutorizoIhsa(null);
                autorizacionesOrden.setAutorizaIhsa(null);
            }
            //-- Autoriza compras
            if (autorizacionesOrden.getFechaAutorizoCompras() != null) {
                autorizacionesOrden.setFechaAutorizoCompras(null);
                autorizacionesOrden.setHoraAutorizoCompras(null);
                autorizacionesOrden.setAutorizaCompras(null);
            }
            //-- Marcar como rechazada la orden
            autorizacionesOrdenRemote.editar(autorizacionesOrden);
            //rechazosOrdenServicioImpl.create(rechazo);
        }
        return enviarCorreoDevolucion;
    }

    // - - - - Contactos
    public void crearContacto(ContactosOrden contactoOrden) {
        contactosOrdenServicioImpl.create(contactoOrden);
    }

    public void eliminarContacto(ContactosOrden contactoOrden) {
        contactosOrdenServicioImpl.remove(contactoOrden);
    }

    public ContactosOrden buscarPorNombre(Object idOrden, Object nombre) {
        return contactosOrdenServicioImpl.buscarPorNombre(idOrden, nombre);
    }

    public List getContactos(Object idOrden) {
        return contactosOrdenServicioImpl.getContactosPorOrden(idOrden);
    }

    public List<ContactoOrdenVo> getContactosVo(int idOrden) {
        return contactosOrdenServicioImpl.traerContactoPorOrden(idOrden);
    }

    public List<RechazosOrden> getRechazos(Object idOrden) {
        return rechazosOrdenServicioImpl.getRechazosPorOrden(idOrden);
    }

    // - - - - Notas - - - -
    public void createNota(NotaOrden notaOrden) {
        notaOrdenServicioImpl.create(notaOrden);
    }

    public List<OrdenDetalleVO> itemsPorOrdenCompra(int id) {
        return ordenDetalleServicioImpl.itemsPorOrden(id);
    }

    public List<OrdenDetalleVO> itemsPorOrdenCompraMulti(int id) {
        return ordenDetalleServicioImpl.itemsPorOrdenMulti(id);
    }

    //Historial de orden de compra
    public List<OrdenVO> getHistorialOrdenes(String usuario, int apCampo, String inicio, String fin, int idStatus) {
        //gerencia hmunoz
        //visto bueno jose carmen jrodriguez
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("SELECT ord.id, ord.CONSECUTIVO as codigo, ord.REFERENCIA, a.RECHAZADA as devuelta, ");//4
            sb.append(" ge.NOMBRE, re.CONSECUTIVO as codigo_requisicion,ord.fecha as creada, com.SIGLAS, ord.SUBTOTAL,");//7
            sb.append(" ord.TOTAL,   est.NOMBRE, a.FECHA_SOLICITO, a.HORA_SOLICITO, mo.nombre, pro.nombre, ");//10
            sb.append(" ord.navCode, ord.url, a.estatus , pot.CUENTA_CONTABLE, pot.NOMBRE ");//14
            sb.append(" FROM ORDEN ord ");
            sb.append("     inner join AUTORIZACIONES_ORDEN  a on a.ORDEN = ord.ID ");
            sb.append("     left join ESTATUS est on a.ESTATUS = est.ID");
            sb.append("     inner join REQUISICION re on ord.REQUISICION = re.id");
            sb.append("     inner join COMPANIA com on ord.COMPANIA = com.RFC");
            sb.append("     inner join MONEDA mo on ord.MONEDA = mo.ID");
            sb.append("     inner join GERENCIA ge on ord.GERENCIA = ge.ID");
            sb.append("     inner join PROVEEDOR pro on ord.PROVEEDOR = pro.ID");
            sb.append("     inner join PROYECTO_OT pot on ord.PROYECTO_OT = pot.ID");
            sb.append(" WHERE ord.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            sb.append(" and '").append(usuario).append("'");
            sb.append("   in (a.SOLICITO, a.AUTORIZA_GERENCIA, a.AUTORIZA_MPG, a.AUTORIZA_IHSA, a.AUTORIZA_FINANZAS, a.AUTORIZA_COMPRAS )");
            sb.append(" and a.FECHA_SOLICITO between cast('").append(inicio).append("' as date) and ").append(" cast('").append(fin).append("' as date)");
            sb.append(" and a.estatus >= ").append(idStatus);
            sb.append(" and a.estatus <> ").append(Constantes.ORDENES_CANCELADAS);

            sb.append(" and ord.AP_CAMPO = ").append(apCampo);
            sb.append(" order by a.FECHA_SOLICITO desc ");
            LOGGER.info(this, "Q: historial OC/S: " + sb.toString());

            List<OrdenVO> le = new ArrayList<>();
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            for (Object[] objects : lo) {
                le.add(castOrden(objects));
            }

            return le;
        } catch (Exception e) {
            LOGGER.fatal(this, "Excepción al traer el historial de ordenenes " + e.getMessage());
            return null;
        }
    }

    //Historial de orden de compra
    public List<OrdenVO> traerHistorialOrdenePorCadenaItems(String cadena, String idUsuario, int idCampo) {
        StringBuilder sb = new StringBuilder();
        boolean v = siUsuarioRolRemote.buscarRolPorUsuarioModulo(idUsuario, Constantes.MODULO_COMPRA, Constantes.CODIGO_ROL_CONS_OCS, idCampo);
        sb.append("select distinct(o.id), o.CONSECUTIVO as codigo, o.REFERENCIA, ao.RECHAZADA as devuelta,");
        sb.append(" g.NOMBRE, o.DESTINO, o.fecha as creada, c.SIGLAS, o.SUBTOTAL, o.TOTAL,   e.NOMBRE, ao.FECHA_SOLICITO, ao.HORA_SOLICITO, ");
        sb.append(" m.nombre, p.nombre,");
        sb.append(" o.navCode, o.url, ao.estatus , pot.CUENTA_CONTABLE, pot.NOMBRE ");//14
        sb.append(" from ORDEN_DETALLE od ");
        sb.append(" right join orden o on od.ORDEN = o.id").append(" and o.AP_CAMPO =").append(idCampo);;
        sb.append(" inner join PROYECTO_OT pot on o.PROYECTO_OT = pot.ID");
        sb.append(" left join AUTORIZACIONES_ORDEN ao on ao.ORDEN = o.id");
        sb.append("   and '").append(idUsuario).append("'");
        sb.append("   in (ao.SOLICITO, ao.AUTORIZA_GERENCIA, ao.AUTORIZA_MPG, ao.AUTORIZA_IHSA, ao.AUTORIZA_FINANZAS, ao.AUTORIZA_COMPRAS)");
        sb.append(" left join ESTATUS e on ao.ESTATUS = e.ID");
        sb.append(" left join MONEDA m on o.MONEDA = m.ID");
        sb.append(" left join PROVEEDOR p on o.PROVEEDOR = p.ID");
        sb.append(" left join GERENCIA g on o.GERENCIA = g.ID");
        sb.append(" left join COMPANIA c on o.COMPANIA = c.RFC");
//

        sb.append(" where ao.estatus >= ").append(Constantes.ORDENES_SIN_APROBAR);
        sb.append(" and  UPPER(od.DESCRIPCION) like UPPER('%").append(cadena).append("%')");
        sb.append(" order by o.consecutivo desc");
        LOGGER.info(this, "Q: historial OC/S por cadena : " + sb.toString());

        List<OrdenVO> le = new ArrayList<>();
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        for (Object[] objects : lo) {
            le.add(castOrden(objects));
        }
        return le;
    }

    private OrdenVO castOrden(Object[] obj) {
        OrdenVO o = new OrdenVO();
        o.setId((Integer) obj[0]);
        o.setConsecutivo((String) obj[1]);
        o.setReferencia((String) obj[2]);
        o.setDevuelta((Boolean) obj[3]);
        o.setGerencia((String) obj[4]);
        o.setDestino((String) obj[5]);
        o.setFecha((Date) obj[6]);
        o.setCompania((String) obj[7]);
        o.setSubTotal((Double) obj[8]);
        o.setTotal((Double) obj[9]);
        o.setEstatus((String) obj[10]);
        o.setFechaOperacion((Date) obj[11]);
        o.setHora((Date) obj[12]);
        o.setMoneda((String) obj[13]);
        o.setProveedor((String) obj[14]);
        o.setNavCode(obj[15] != null ? (String) obj[15] : "-");
        o.setUrl(obj[16] != null ? (String) obj[16] : "-");
        o.setIdStatus(obj[17] != null ? (Integer) obj[17] : 0);
        o.setCuentaContable((String) obj[18]);
        o.setNombreProyectoOT((String) obj[19]);
        return o;
    }

    /*
    *  Historial de ultimas ordenes modificadas 
     */
    public List<OrdenView> getUltimasOrdenesModificadas(String idUsuario, int idCampo) {

        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT ord.id, ")
                .append("               ord.consecutivo as consecutivo, ")
                .append("   		ord.referencia, ")
                .append("   		ord.destino, ")
                .append("   		ord.contrato, ")
                .append("   		a.rechazada as devuelta, ")
                .append("             	ge.nombre as gerencia, ")
                .append("             	re.consecutivo as consecutivo_requisicion,")
                .append("             	ord.observaciones,")
                .append("             	com.siglas as siglas_compania, ")
                .append("             	ord.subtotal,")
                .append("             	ord.total,   ")
                .append("             	est.nombre as estatus, ")
                .append("             	to_char( (a.fecha_solicito + a.hora_solicito)::timestamp,'YYYY-MM-DD HH24:MI') as fecha_solicito,")
                .append("             	mo.nombre as moneda, ")
                .append("             	pro.nombre as proveedor,")
                .append("             	ord.url,")
                .append("             	pot.NOMBRE as cuenta_contable,")
                .append("             	analista.nombre as comprador,\n")
                .append("               solicita.nombre as solicita, \n")
                .append("             	to_char((a.fecha_solicito + a.hora_solicito)::timestamp,'YYYY-MM-DD HH24:MI') as fecha_solicita,\n")
                .append("             	visto_bueno.nombre as visto_bueno, \n")
                .append("             	to_char((a.fecha_autorizo_gerencia + a.hora_autorizo_gerencia)::timestamp,'YYYY-MM-DD HH24:MI') as fecha_visto_bueno,\n")
                .append("		revisa.nombre as revisa,\n")
                .append("		to_char((a.fecha_autorizo_mpg + a.hora_autorizo_mpg)::timestamp,'YYYY-MM-DD HH24:MI') as fecha_revisa,\n")
                .append("		aprueba.nombre as aprueba,\n")
                .append("		to_char((a.fecha_autorizo_ihsa + a.hora_autorizo_ihsa)::timestamp,'YYYY-MM-DD HH24:MI') as fecha_aprueba,				  \n")
                .append("		autoriza.nombre as autoriza,\n")
                .append("		to_char((a.fecha_autorizo_compras + a.hora_autorizo_compras)::timestamp,'YYYY-MM-DD HH24:MI') as fecha_autoriza,				  				  				  \n")
                .append("		campo.carta_intencion as campo_con_carta_intencion,				  		  \n")
                .append("		psin_carta.id is null es_proveedor_sin_carta,\n")
                .append("		pro.nombre as acepta_carta_intencion,\n")
                .append("		to_char((a.fecha_aceptacion_carta + a.hora_aceptacion_carta)::timestamp,'YYYY-MM-DD HH24:MI') as fecha_acepta_carta_intencion,				  				  				  \n")
                .append("		revisa_juridico.nombre as revisa_juridico,\n")
                .append("		to_char((a.fecha_revisa_repse + a.hora_revisa_repse)::timestamp,'YYYY-MM-DD HH24:MI') as fecha_revisa_juridico,	\n")
                .append("		enviarpdf.nombre as envia_proveedor,\n")
                .append("		to_char((a.fecha_envio_proveedor + a.hora_envio_proveedor)::timestamp,'YYYY-MM-DD HH24:MI') as fecha_envia_proveedor	")
                .append("             FROM ORDEN ord ")
                .append("                 inner join AUTORIZACIONES_ORDEN  a on a.ORDEN = ord.ID ")
                .append("                 left join ESTATUS est on a.ESTATUS = est.ID")
                .append("                 inner join REQUISICION re on ord.REQUISICION = re.id")
                .append("                 inner join COMPANIA com on ord.COMPANIA = com.RFC")
                .append("                 inner join MONEDA mo on ord.MONEDA = mo.ID")
                .append("                 inner join GERENCIA ge on ord.GERENCIA = ge.ID")
                .append("                 inner join PROVEEDOR pro on ord.PROVEEDOR = pro.ID")
                .append("                 inner join PROYECTO_OT pot on ord.PROYECTO_OT = pot.ID")
                .append("                 inner join ap_campo campo on campo.id = ord.ap_campo")
                .append("                 left join usuario analista on analista.id = ord.analista\n")
                .append("                 left join usuario solicita on solicita.id = a.solicito\n")
                .append("                 left join usuario visto_bueno on visto_bueno.id = a.autoriza_gerencia\n")
                .append("                 left join usuario revisa on revisa.id = a.autoriza_mpg                 \n")
                .append("                 left join usuario aprueba on aprueba.id = a.autoriza_ihsa\n")
                .append("                 left join usuario autoriza on autoriza.id = a.autoriza_compras\n")
                .append("                 left join usuario revisa_juridico on revisa_juridico.id = a.usuario_revisa_juridico\n")
                .append("                 left join usuario enviarpdf on enviarpdf.id = a.enviarpdf\n")
                .append("                 left join pv_proveedor_sin_carta_intencion psin_carta on psin_carta.id = pro.id")
                .append("		WHERE ord.eliminado = false")
                .append("			and ? in (a.SOLICITO, a.AUTORIZA_GERENCIA, a.AUTORIZA_MPG, a.AUTORIZA_IHSA, a.AUTORIZA_FINANZAS, a.AUTORIZA_COMPRAS )")
                .append("			and a.estatus >= ").append(Constantes.ORDENES_SIN_APROBAR)
                //.append("			--and a.estatus <> 100 			")
                .append("			and ord.AP_CAMPO = ?")
                .append("			and to_char(a.fecha_modifico,'YYYY') =  to_char(current_date,'YYYY')")
                .append("			order by a.fecha_modifico desc\n")
                .append("		LIMIT 4 ");

        LOGGER.info(this, "historial ultimas ordenes modificadas: " + sb.toString());

        return dbCtx
                .fetch(
                        sb.toString(),
                        idUsuario,
                        idCampo
                ).into(OrdenView.class);

    }

    public List<CampoOrden> buscarTrabajoPendienteCampo(String idUsuario, int idCampo) {

        //List<CampoVo> lc = apCampoRemote.getAllFieldExceptCurrent(idCampo);
        List<CampoUsuarioPuestoVo> lc = apCampoUsuarioRhPuestoRemote.traerCampoPorUsurioMenosActual(idUsuario, idCampo);
        List<CampoOrden> lco = new ArrayList<CampoOrden>();
        CampoOrden campoOrden;
        try {
            long total = 0;
            if (!lc.isEmpty()) {
                for (CampoUsuarioPuestoVo campoVo : lc) {
                    //Revisa las OC/S

                    total = requisicionServicioImpl.getTotalRequisicionesSinDisgregar(idUsuario, campoVo.getIdCampo());
                    total += totalOrdenesSinSolicitar(idUsuario, campoVo.getIdCampo());
                    total += totalOrdenesSinAprobar(idUsuario, campoVo.getIdCampo());
                    total += totalOrdenesSinAutorizarMPG(idUsuario, campoVo.getIdCampo());
                    total += totalOrdenesSinAutorizarFinanzas(idUsuario, campoVo.getIdCampo());
                    total += getTotalOrdenesSinAutorizarIHSA(idUsuario, campoVo.getIdCampo());
                    total += getTotalOrdenesSinAutorizarCompras(idUsuario, campoVo.getIdCampo());
                    total += getTotalTareasSinCompleta(idUsuario, campoVo.getIdCampo());
                    total += ocRequisicionCoNoticiaRemote.totalNoticiaPorUsuario(idUsuario, campoVo.getIdCampo());

                    //REvisa las Requisiciones
                    total += requisicionServicioImpl.getTotalRequisicionesSinSolicitar(idUsuario, campoVo.getIdCampo());
                    total += requisicionServicioImpl.getTotalRequisicionesSinRevisar(idUsuario, campoVo.getIdCampo());
                    total += requisicionServicioImpl.getTotalRequisicionesSinAprobar(idUsuario, campoVo.getIdCampo());
                    total += requisicionServicioImpl.getTotalRequisicionesSinAsignar(idUsuario, campoVo.getIdCampo(),
                            Constantes.REQUISICION_VISTO_BUENO, Constantes.ROL_ASIGNA_REQUISICION);
//		    total += requisicionServicioImpl.getTotalRequisicionesSinAsignar(idUsuario, campoVo.getIdCampo(),
//			    Constantes.REQUISICION_VISTO_BUENO_C, Constantes.ROL_VISTO_BUENO_COSTO);
                    total += ocOrdenCoNoticiaRemote.totalNoticiaPorUsuario(idUsuario, campoVo.getIdCampo());
                    total += requisicionServicioImpl.getTotalRequisicionesSinVistoBueno(idUsuario, campoVo.getIdCampo(), TipoRequisicion.AF.name(), Constantes.ROL_VISTO_BUENO_CONTABILIDAD);
                    total += requisicionServicioImpl.getTotalRequisicionesSinVistoBueno(idUsuario, campoVo.getIdCampo(), TipoRequisicion.PS.name(), Constantes.ROL_VISTO_BUENO_COSTO);

                    total += totalOrdenesSinAprobarSocio(idUsuario, campoVo.getIdCampo());
                    // se agregan a la lista
                    if (total > 0) {
                        campoOrden = new CampoOrden();
                        campoOrden.setIdCampo(campoVo.getIdCampo());
                        campoOrden.setCampo(campoVo.getCampo());
                        campoOrden.setTotal(String.valueOf(total));
                        lco.add(campoOrden);
                    }
                    total = 0;
                }
            }
            return lco;
        } catch (Exception e) {
            LOGGER.info(this, "exc: totaltes campos : " + e.getMessage() + "  - - -  " + e.getCause());
            return null;
        }

    }

    public long obtieneTotalOrdenes(String usuario) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("select count(o) FROM Orden o WHERE ");
        stringBuilder.append("(o.analista.id='").append(usuario).append("' or ");
        stringBuilder.append("o.autorizacionesOrden.autorizaGerencia.id='").append(usuario).append("' or ");
        stringBuilder.append("o.autorizacionesOrden.autorizaMpg.id='").append(usuario).append("' or ");
        stringBuilder.append("o.autorizacionesOrden.autorizaIhsa.id='").append(usuario).append("' or ");
        stringBuilder.append("o.autorizacionesOrden.autorizaCompras.id='").append(usuario).append("')  AND ");
        stringBuilder.append("o.autorizacionesOrden.estatus.id IN(").append(Constantes.ORDENES_SIN_SOLICITAR).append(",");
        stringBuilder.append(Constantes.ORDENES_SIN_APROBAR).append(",");
        stringBuilder.append(Constantes.ORDENES_SIN_AUTORIZAR_MPG).append(",");
        stringBuilder.append(Constantes.ORDENES_SIN_AUTORIZAR_IHSA).append(",");
        stringBuilder.append(Constantes.ORDENES_SIN_AUTORIZAR_COMPRAS).append(")");
        stringBuilder.append(" AND o.eliminado = :eli");

        return ((Long) em.createQuery(stringBuilder.toString()).setParameter("eli", Constantes.NO_ELIMINADO).getSingleResult());
    }

    /**
     * Consulta para OC/S superan el monto
     */
    public List<OrdenVO> traerOrdenSuperaMonto() {
        try {
            List<OrdenVO> lo = null;
            clearQuery();
            query.append("select ao.id, o.ID, g.id, p.id, o.CONSECUTIVO, ao.FECHA_SOLICITO, g.NOMBRE,");
            query.append(" p.NOMBRE, o.REFERENCIA, o.TOTAL_USD, pot.nombre, ao.estatus ");
            query.append(" from AUTORIZACIONES_ORDEN ao, orden o, GERENCIA g, PROVEEDOR p, proyecto_ot pot");
            query.append(" where o.SUPERA_MONTO = 'True' and ao.FECHA_SOLICITO = cast('NOW' as date) ");
            query.append(" and ao.ORDEN = o.ID and o.GERENCIA = g.ID and o.PROVEEDOR = p.id");
            query.append(" and o.proyecto_ot = pot.id");
            query.append(" and o.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" order by o.GERENCIA, o.PROVEEDOR asc");
            LOGGER.info(this, "Q: supera monto hoy " + query.toString());
            List<Object[]> lobj = em.createNativeQuery(query.toString()).getResultList();
            LOGGER.info(this, "Aca despues de consulta");
            if (lobj != null) {
                lo = new ArrayList<>();
                for (Object[] objects : lobj) {
                    lo.add(castOrdenSuperaMonto(objects));
                }
                LOGGER.info(this, "Aca antes de return");
                return lo;
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.info(this, "Esrror al recuperar las OC/S con monto superado : " + e.getMessage());
            e.getStackTrace();
            return null;
        }
    }

    private OrdenVO castOrdenSuperaMonto(Object[] object) {

        try {
            OrdenVO o = new OrdenVO();
            o.setIdAutorizaOrden((Integer) object[0]);
            o.setId((Integer) object[1]);
            o.setIdGerencia((Integer) object[2]);
            o.setIdProveedor((Integer) object[3]);
            o.setConsecutivo((String) object[4]);
            o.setFechaSolicita((Date) object[5]);
            o.setGerencia((String) object[6]);
            o.setProveedor((String) object[7]);
            o.setReferencia((String) object[8]);
            o.setTotalUsd((Double) object[9]);
            o.setNombreProyectoOT((String) object[10]);
            o.setIdStatus((Integer) object[11]);
            return o;
        } catch (Exception e) {
            LOGGER.info(this, "Exc: cst Orden VO" + e.getMessage());
            return null;
        }
    }

    public List<OrdenVO> ordenesCompraAutorizadas(String comprador, int status, int anio, int mes) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select p.NOMBRE, r.CONSECUTIVO, o.CONSECUTIVO, r.REFERENCIA , r.MONTOTOTAL_USD, ao.FECHA_SOLICITO, ao.FECHA_AUTORIZO_COMPRAS");
            sb.append(" from ORDEN o, REQUISICION r, PROVEEDOR p, AUTORIZACIONES_ORDEN ao");
            sb.append(" where ao.SOLICITO = '").append(comprador).append("'");
            sb.append(" and ao.ESTATUS = ").append(status);
            if (anio > 0) {
                sb.append(" and extract(year from ao.FECHA_SOLICITO) = ").append(anio);
            }
            if (mes > 0) {
                sb.append(" and extract(month from ao.FECHA_SOLICITO) = ").append(mes);
            }
            sb.append(" and r.id = o.REQUISICION");
            sb.append(" and ao.ORDEN = o.id and o.PROVEEDOR = p.ID");
            sb.append("  order by p.NOMBRE asc");
            List<Object[]> lobj = em.createNativeQuery(sb.toString()).getResultList();
            List<OrdenVO> lo = null;
            if (lobj != null) {
                lo = new ArrayList<>();
                for (Object[] objects : lobj) {
                    lo.add(castOrdenReporteVO(objects));
                }
                return lo;
            }
            return lo;
        } catch (Exception e) {
            return null;
        }
    }

    private OrdenVO castOrdenReporteVO(Object[] object) {
        OrdenVO o = new OrdenVO();
        try {
            o.setProveedor((String) object[0]);
            o.setRequisicion((String) object[1]);
            o.setConsecutivo((String) object[2]);
            o.setReferencia((String) object[3]);
            o.setMontoTotalRequisicion((Double) object[4]);
            o.setFechaSolicita((Date) object[5]);
            o.setFechaAutoriza((Date) object[5]);
        } catch (Exception e) {
            LOGGER.info(this, "Exc: cst Orden VO" + e.getMessage());
        }
        return o;
    }

    public List<Integer> ordenesSinUUID(int anio, int estatus) {
        List<Integer> lo = null;
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(" select o.ID ");
            sb.append(" from ORDEN o  ");
            if (estatus > 0) {
                sb.append(" INNER JOIN AUTORIZACIONES_ORDEN ao on ao.ORDEN = o.ID and ao.ESTATUS = ").append(estatus);
            }
            sb.append(" WHERE o.ELIMINADO = 'False' AND o.UUID is null  ");

            if (anio > 0) {
                sb.append(" and extract(year from o.FECHA) = ").append(anio);
            }

            lo = em.createNativeQuery(sb.toString()).getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lo;
    }

    public boolean reenviarOrdenCompras(Orden orden, Usuario usr) throws Exception {
        File ordenPDF = null;
        File pdfCG = null;
        try {
            ordenPDF = siaPDFRemote.getPDF(orden, usr, true);
            pdfCG = siaPDFRemote.buscarPdfCG(orden.getCompania());

        } catch (Exception e) {
            ordenPDF = null;
            Logger
                    .getLogger(OrdenImpl.class
                            .getName()).log(Level.SEVERE, e.getMessage());
        }
        List<ContactoOrdenVo> listaContactosOrden = getContactosVo(orden.getId());
        return notificacionesOrdenRemote.reenviarNotificacionOrdenProveedor(orden, listaContactosOrden, ordenPDF, pdfCG);
    }

    public boolean reenviarCodigos(Orden orden) throws Exception {
        return notificacionesOrdenRemote.enviarNotificacionOrdenAnalista(orden);
    }

    @Trace
    private File buscarOrdenExcel(String nombre, File temp) throws Exception {
        //FIXME : utilizar try-with-resources
        try {
            String repositoryPath = parametrosSistemaServicioRemoto.find(1).getUploadDirectory();
            String plantillaPath = "Plantillas/ExcelNAV/";

            InputStream inputDocument
                    = Files.newInputStream(
                            Paths.get(
                                    new StringBuilder()
                                            .append(repositoryPath)
                                            .append(plantillaPath)
                                            .append(nombre)
                                            .append(".xlsx")
                                            .toString()
                            )
                    );

            OPCPackage pkg = OPCPackage.open(inputDocument);
            XSSFWorkbook wb = new XSSFWorkbook(pkg);
            OutputStream outputFile = new FileOutputStream(temp);

            wb.write(outputFile);
            outputFile.close();
            inputDocument.close();
            pkg.close();
        } catch (Exception ex) {
            LOGGER.error(this, "", ex);
            throw new Exception(ex.getMessage());
        }
        return temp;
    }

    @Trace
    private void copiarArchivo(String pathOrigen, String pathDestino) throws Exception {
        File copied = new File(pathDestino);
        File original = new File(pathOrigen);
        try ( InputStream in = new BufferedInputStream(new FileInputStream(original));  OutputStream out = new BufferedOutputStream(new FileOutputStream(copied))) {
            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
        } catch (Exception ex) {
            LOGGER.error(this, "", ex);
            throw new Exception(ex.getMessage());
        }
    }

    private void setValueExcel(XSSFSheet my_worksheet, Object valor, int hoja, int x, int y) throws Exception {
        try {
            XSSFCell cell = my_worksheet.getRow(x).getCell(y);
            if (cell == null) {
                cell = my_worksheet.getRow(x).createCell(y);
            }
            if (valor instanceof String) {
                cell.setCellValue(String.valueOf(valor));
            }
            if (valor instanceof Double) {
                Double valorAux = (Double) valor;
                cell.setCellValue(valorAux.doubleValue());
            }
            if (valor instanceof Integer) {
                Integer valorAux = (Integer) valor;
                cell.setCellValue(valorAux);
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
        }
    }

    private void setValueExcelFormulas(XSSFSheet my_worksheet, XSSFWorkbook wb, int hoja, int x, int y) throws Exception {
        XSSFCell cell = my_worksheet.getRow(x).getCell(y);

        XSSFFormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        if (cell.getCellType() == CellType.FORMULA) {
            evaluator.evaluateFormulaCell(cell);
        }
    }

    @Trace(dispatcher = true)
    public File generarExcel(Orden orden, File fileTemp) throws Exception {

        try ( InputStream inputDocument = new FileInputStream(fileTemp);) {
            if (orden != null
                    && orden.getCompania() != null
                    && !Strings.isNullOrEmpty(orden.getCompania().getRfc())) {

                fileTemp = buscarOrdenExcel(orden.getCompania().getRfc(), fileTemp);

                int i = 22;

                OPCPackage pkg = OPCPackage.open(inputDocument);
                XSSFWorkbook wb = new XSSFWorkbook(pkg);
                XSSFSheet myWorksheet = wb.getSheetAt(0);

                if (TipoRequisicion.AF.equals(orden.getTipo())) {
                    cargarExcelAF(myWorksheet, wb, orden, i);
                } else {
                    cargarExcelPS(myWorksheet, wb, orden, i);
                }

                OutputStream outputFile = new FileOutputStream(fileTemp);
                wb.write(outputFile);
                outputFile.close();
                pkg.close();
            }
        } catch (Exception e) {
            LOGGER.info(this, e.getMessage(), e);
        }

        return fileTemp;
    }

    @Trace(dispatcher = true)
    public File generarExcel(Orden orden, String pathOrigen, String pathDestino) throws Exception {
        File fileTemp = null;
        copiarArchivo(pathOrigen, pathDestino);
        fileTemp = new File(pathDestino);
        if (fileTemp.exists()) {
            try ( InputStream inputDocument = new FileInputStream(fileTemp);) {
                int i = 22;
                OPCPackage pkg = OPCPackage.open(inputDocument);
                XSSFWorkbook wb = new XSSFWorkbook(pkg);
                XSSFSheet myWorksheet = wb.getSheetAt(0);

                if (TipoRequisicion.AF.equals(orden.getTipo())) {
                    cargarExcelAF(myWorksheet, wb, orden, i);
                } else {
                    cargarExcelPS(myWorksheet, wb, orden, i);
                }
                try ( OutputStream outputFile = new FileOutputStream(fileTemp);) {
                    wb.write(outputFile);
                    outputFile.close();
                    pkg.close();
                } catch (Exception e) {
                    LOGGER.info(this, e.getMessage(), e);
                }
            } catch (Exception e) {
                LOGGER.info(this, e.getMessage(), e);
            }
        }
        return fileTemp;
    }

    public File generarExcelOCSAI(Orden orden, File fileTemp) throws Exception {
        //FIXME : usar try-with-resources
        try {
            fileTemp = buscarOrdenExcel(orden.getConsecutivo(), fileTemp);
            int i = 22;
            InputStream input_document = new FileInputStream(fileTemp);
            OPCPackage pkg = OPCPackage.open(input_document);
            XSSFWorkbook wb = new XSSFWorkbook(pkg);
            XSSFSheet my_worksheet = wb.getSheetAt(0);
            if (TipoRequisicion.AF.equals(orden.getTipo())) {
                cargarExcelAF(my_worksheet, wb, orden, i);
            } else if (TipoRequisicion.PS.name().equals(orden.getTipo())) {
                cargarExcelPS(my_worksheet, wb, orden, i);
            } else {
                cargarExcelAI(my_worksheet, wb, orden, i);
            }
            input_document.close();
            OutputStream output_file = new FileOutputStream(fileTemp);
            wb.write(output_file);
            output_file.close();
            pkg.close();
        } catch (Exception e) {
            LOGGER.info(this, e.getMessage(), e);
        }
        return fileTemp;
    }

    @Trace
    private void cargarExcelAF(XSSFSheet fileExcel, XSSFWorkbook wb, Orden orden, int i) throws Exception {
        excelCargarEncabezado(fileExcel, wb, orden);
        for (OrdenDetalleVO linea : itemsPorOrdenCompra(orden.getId())) {
            for (OcActivoFijoVO afVO : ocActivoFijoRemote.getDetActivoFijo(orden.getId(), linea.getId())) {
                setValueExcel(fileExcel, "Activo Fijo", 0, i, 0);//Tipo Línea
                setValueExcel(fileExcel, afVO.getCodigo(), 0, i, 1);//Nº Activo (Debe Existir)
                i = i + excelCargarLineaComunAF(fileExcel, wb, orden, linea, i);
                //i++;
            }
        }
        if (i > 22) {
            borrarLineas(fileExcel, i);
        }
    }

    @Trace
    private void cargarExcelPS(XSSFSheet fileExcel, XSSFWorkbook wb, Orden orden, int i) throws Exception {
        excelCargarEncabezado(fileExcel, wb, orden);
        for (OrdenDetalleVO linea : this.itemsPorOrdenCompra(orden.getId())) {
            setValueExcel(fileExcel, "Producto", 0, i, 0);//Tipo Línea
            setValueExcel(fileExcel, linea.getOcProductoCode(), 0, i, 2);//Producto
            i = i + excelCargarLineaComunPS(fileExcel, wb, orden, linea, i);
            //i++;
        }
        if (i > 22) {
            borrarLineas(fileExcel, i);
        }
    }

    private void cargarExcelAI(XSSFSheet fileExcel, XSSFWorkbook wb, Orden orden, int i) throws Exception {
        excelCargarEncabezado(fileExcel, wb, orden);
        for (OrdenDetalleVO linea : this.itemsPorOrdenCompra(orden.getId())) {
            setValueExcel(fileExcel, "", 0, i, 0);//Tipo Línea
            i = i + excelCargarLineaComunAI(fileExcel, orden, linea, i);
        }
        if (i > 22) {
            borrarLineas(fileExcel, i);
        }
    }

//    private void excelCargarTotales(XSSFSheet fileExcel, XSSFWorkbook wb, int i) throws Exception {
//        setValueExcelFormulas(fileExcel, wb, 0, i, 10);//Base
//        setValueExcelFormulas(fileExcel, wb, 0, i, 11);//IVA
//        setValueExcelFormulas(fileExcel, wb, 0, i, 12);//Total        
//    }
    private void borrarLineas(XSSFSheet fileExcel, int i) throws Exception {
        try {
            for (; i <= fileExcel.getLastRowNum(); i++) {
                fileExcel.removeRow(fileExcel.getRow(i));
            }
        } catch (Exception e) {
            LOGGER.fatal(e);
        }
    }

    private int excelCargarLineaComunAI(XSSFSheet fileExcel, Orden orden, OrdenDetalleVO linea, int i) throws Exception {
        int rows = 1;
        setValueExcel(fileExcel, orden.getProyectoOt().getCuentaContable(), 0, i, 4);//Proyecto
        setValueExcel(fileExcel, linea.getArtUnidad(), 0, i, 6);//Unidad Medida
        setValueExcel(fileExcel, linea.getCantidad(), 0, i, 7);//Cantidad
        setValueExcel(fileExcel, linea.getPrecioUnitario(), 0, i, 8);//Costo Unitario        
        if (orden.getPorcentajeIva() != null && !orden.getPorcentajeIva().isEmpty()
                && !"No aplica".equals(orden.getPorcentajeIva())) {
            BigDecimal bd = null;
            if (orden.getImpuesto() != null) {
                bd = new BigDecimal((orden.getImpuesto().getValor()) / 100);
            } else {
                bd = new BigDecimal(0.00);
            }
            setValueExcel(fileExcel, bd, 0, i, 9);//% IVA
        } else {
            setValueExcel(fileExcel, "0%", 0, i, 9);//% IVA
        }

        if (linea.getArtDescripcion().length() <= 250) {
            setValueExcel(fileExcel, linea.getArtDescripcion(), 0, i, 3);//Descripción
        } else {
            int length = linea.getArtDescripcion().length();
            for (int l = 0; l < length; l = l + 250) {
                if (length - l > 250) {
                    setValueExcel(fileExcel, linea.getArtDescripcion().substring(l, l + 250), 0, i, 3);//Descripción
                } else {
                    setValueExcel(fileExcel, linea.getArtDescripcion().substring(l, length), 0, i, 3);//Descripción
                }
                i++;
                rows++;
            }
        }
        return rows;
    }

    @Trace
    private int excelCargarLineaComunAF(XSSFSheet fileExcel, XSSFWorkbook wb, Orden orden, OrdenDetalleVO linea, int i) throws Exception {
        int rows = 1;

        if (Constantes.PAIS_MEXICO == orden.getCompania().getSiPais().getId()) {
            setValueExcel(fileExcel, orden.getProyectoOt().getCuentaContable(), 0, i, 4);//Proyecto
            setValueExcel(fileExcel, linea.getArtUnidad(), 0, i, 6);//Unidad Medida
            setValueExcel(fileExcel, 1, 0, i, 7);//Cantidad
            setValueExcel(fileExcel, linea.getPrecioUnitario(), 0, i, 8);//Costo Unitario
            if (orden.getPorcentajeIva() != null && !orden.getPorcentajeIva().isEmpty()
                    && !"No aplica".equals(orden.getPorcentajeIva())) {
                BigDecimal bd = null;
                if (orden.getImpuesto() != null) {
                    bd = new BigDecimal((orden.getImpuesto().getValor()) / 100);
                } else {
                    bd = new BigDecimal(0.00);
                }
                bd = bd.setScale(2, RoundingMode.HALF_UP);
                setValueExcel(fileExcel, bd.doubleValue(), 0, i, 9);//% IVA

            } else {
                BigDecimal bd = new BigDecimal(0.00);
                bd = bd.setScale(2, RoundingMode.HALF_UP);
                setValueExcel(fileExcel, bd.doubleValue(), 0, i, 9);//% IVA
            }

            setValueExcelFormulas(fileExcel, wb, 0, i, 10);
            setValueExcelFormulas(fileExcel, wb, 0, i, 11);
            setValueExcelFormulas(fileExcel, wb, 0, i, 12);
            if (orden.getFechaEntrega() != null) {
                SimpleDateFormat fs = new SimpleDateFormat("dd/MM/yyyy");
                setValueExcel(fileExcel, fs.format(orden.getFechaEntrega()), 0, i, 16);//Fecha entrega
            }

        } else {
            if (orden.getApCampo().getAlmacen() != null && !orden.getApCampo().getAlmacen().isEmpty()) {
                setValueExcel(fileExcel, orden.getApCampo().getAlmacen(), 0, i, 4);//Almacen                               
            }
            setValueExcel(fileExcel, orden.getProyectoOt().getCuentaContable(), 0, i, 5);//Proyecto
            setValueExcel(fileExcel, linea.getArtUnidad(), 0, i, 7);//Unidad Medida
            setValueExcel(fileExcel, 1, 0, i, 8);//Cantidad
            setValueExcel(fileExcel, linea.getPrecioUnitario(), 0, i, 9);//Costo Unitario    
            if (orden.getPorcentajeIva() != null && !orden.getPorcentajeIva().isEmpty()
                    && !"No aplica".equals(orden.getPorcentajeIva())) {
                BigDecimal bd = null;
                if (orden.getImpuesto() != null) {
                    bd = new BigDecimal((orden.getImpuesto().getValor()) / 100);
                } else {
                    bd = new BigDecimal(0.00);
                }
                bd = bd.setScale(2, RoundingMode.HALF_UP);
                setValueExcel(fileExcel, bd.doubleValue(), 0, i, 10);//% IVA

            } else {
                BigDecimal bd = new BigDecimal(0.00);
                bd = bd.setScale(2, RoundingMode.HALF_UP);
                setValueExcel(fileExcel, bd.doubleValue(), 0, i, 10);//% IVA
            }

            setValueExcelFormulas(fileExcel, wb, 0, i, 11);
            setValueExcelFormulas(fileExcel, wb, 0, i, 12);
            setValueExcelFormulas(fileExcel, wb, 0, i, 13);
        }
        //Agregar el usuario beneficiado
        setValueExcel(fileExcel, linea.getUsuarioBeneficiado(), 0, i, 17);//usuario beneficiado

        if (linea.getArtDescripcion().length() <= 250) {
            setValueExcel(fileExcel, linea.getArtDescripcion(), 0, i, 3);//Descripción
        } else {
            int length = linea.getArtDescripcion().length();
            for (int l = 0; l < length; l = l + 250) {
                if (length - l > 250) {
                    setValueExcel(fileExcel, linea.getArtDescripcion().substring(l, l + 250), 0, i, 3);//Descripción
                } else {
                    setValueExcel(fileExcel, linea.getArtDescripcion().substring(l, length), 0, i, 3);//Descripción
                }
                i++;
                rows++;
            }
        }

        return rows;
    }

    @Trace
    private int excelCargarLineaComunPS(XSSFSheet fileExcel, XSSFWorkbook wb, Orden orden, OrdenDetalleVO linea, int i) throws Exception {
        int rows = 1;
        if (Constantes.PAIS_MEXICO == orden.getCompania().getSiPais().getId()) {
            setValueExcel(fileExcel, linea.getProyectoOtCC(), 0, i, 4);//Proyecto            
            if (TipoRequisicion.PS.name().equals(orden.getTipo()) && !Constantes.RFC_MPG.equals(orden.getCompania().getRfc())) {
                if (linea != null && linea.getSubTarea() != null && !linea.getSubTarea().isEmpty()) {
                    if (linea.getIdpresupuesto() > 0 && linea.getIdpresupuesto() > 2) {
                        setValueExcel(fileExcel, (linea.getIdpresupuesto() > 9 ? "" : "0") + linea.getIdpresupuesto() + linea.getCodigoSubTarea(), 0, i, 5);//SubTarea
                    } else {
                        setValueExcel(fileExcel, linea.getCodigoSubTarea(), 0, i, 5);//SubTarea
                    }
                } else {
                    setValueExcel(fileExcel, linea.getCodeTarea(), 0, i, 5);//Tarea
                }
            } else if (TipoRequisicion.PS.name().equals(orden.getTipo()) && Constantes.RFC_MPG.equals(orden.getCompania().getRfc())) {
                setValueExcel(fileExcel, linea.getProyectoOtCC(), 0, i, 5);//Tarea
            }
            setValueExcel(fileExcel, linea.getArtUnidad(), 0, i, 6);//Unidad Medida
            setValueExcel(fileExcel, linea.getCantidad(), 0, i, 7);//Cantidad
            setValueExcel(fileExcel, linea.getPrecioUnitario(), 0, i, 8);//Costo Unitario

            if (orden.getPorcentajeIva() != null && !orden.getPorcentajeIva().isEmpty()
                    && !"No aplica".equals(orden.getPorcentajeIva())) {
                BigDecimal bd = null;
                if (orden.getImpuesto() != null) {
                    bd = new BigDecimal((orden.getImpuesto().getValor()) / 100);
                } else {
                    bd = new BigDecimal(0.00);
                }
                bd = bd.setScale(2, RoundingMode.HALF_UP);
                setValueExcel(fileExcel, bd.doubleValue(), 0, i, 9);//% IVA

            } else {
                BigDecimal bd = new BigDecimal(0.00);
                bd = bd.setScale(2, RoundingMode.HALF_UP);
                setValueExcel(fileExcel, bd.doubleValue(), 0, i, 9);//% IVA
            }

            setValueExcelFormulas(fileExcel, wb, 0, i, 10);
            setValueExcelFormulas(fileExcel, wb, 0, i, 11);
            setValueExcelFormulas(fileExcel, wb, 0, i, 12);
            if (orden.getFechaEntrega() != null) {
                SimpleDateFormat fs = new SimpleDateFormat("dd/MM/yyyy");
                setValueExcel(fileExcel, fs.format(orden.getFechaEntrega()), 0, i, 16);//Fecha entrega
            }

        } else {
            if (orden.getApCampo().getAlmacen() != null && !orden.getApCampo().getAlmacen().isEmpty()) {
                setValueExcel(fileExcel, orden.getApCampo().getAlmacen(), 0, i, 4);//Almacen                        
            }
            setValueExcel(fileExcel, linea.getProyectoOtCC(), 0, i, 5);//Proyecto

            if (TipoRequisicion.PS.name().equals(orden.getTipo()) && !Constantes.RFC_MPG.equals(orden.getCompania().getRfc())) {
                if (linea != null && linea.getSubTarea() != null && !linea.getSubTarea().isEmpty()) {
                    setValueExcel(fileExcel, linea.getCodigoSubTarea(), 0, i, 6);//SubTarea
                } else {
                    setValueExcel(fileExcel, linea.getCodeTarea(), 0, i, 6);//Tarea
                }
            } else if (TipoRequisicion.PS.name().equals(orden.getTipo()) && Constantes.RFC_MPG.equals(orden.getCompania().getRfc())) {
                setValueExcel(fileExcel, linea.getProyectoOtCC(), 0, i, 6);//Tarea
            }

            setValueExcel(fileExcel, linea.getArtUnidad(), 0, i, 7);//Unidad Medida
            setValueExcel(fileExcel, linea.getCantidad(), 0, i, 8);//Cantidad
            setValueExcel(fileExcel, linea.getPrecioUnitario(), 0, i, 9);//Costo Unitario

            if (orden.getPorcentajeIva() != null && !orden.getPorcentajeIva().isEmpty()
                    && !"No aplica".equals(orden.getPorcentajeIva())) {
                BigDecimal bd = null;
                if (orden.getImpuesto() != null) {
                    bd = new BigDecimal((orden.getImpuesto().getValor()) / 100);
                } else {
                    bd = new BigDecimal(0.00);
                }
                bd = bd.setScale(2, RoundingMode.HALF_UP);
                setValueExcel(fileExcel, bd.doubleValue(), 0, i, 10);//% IVA

            } else {
                BigDecimal bd = new BigDecimal(0.00);
                bd = bd.setScale(2, RoundingMode.HALF_UP);
                setValueExcel(fileExcel, bd.doubleValue(), 0, i, 10);//% IVA
            }

            setValueExcelFormulas(fileExcel, wb, 0, i, 11);
            setValueExcelFormulas(fileExcel, wb, 0, i, 12);
            setValueExcelFormulas(fileExcel, wb, 0, i, 13);

            if (orden.getApCampo().getCodeproy() != null && !orden.getApCampo().getCodeproy().isEmpty()) {
                setValueExcel(fileExcel, orden.getApCampo().getCodeproy(), 0, i, 16);//ProyCode
            }
        }
        //Agregar el usuario beneficiado
        setValueExcel(fileExcel, linea.getUsuarioBeneficiado(), 0, i, 17);//usuario beneficiado
        if (linea.getArtDescripcion().length() <= 250) {
            setValueExcel(fileExcel, linea.getArtDescripcion(), 0, i, 3);//Descripción
        } else {
            int length = linea.getArtDescripcion().length();
            for (int l = 0; l < length; l = l + 250) {
                if (length - l > 250) {
                    setValueExcel(fileExcel, linea.getArtDescripcion().substring(l, l + 250), 0, i, 3);//Descripción
                } else {
                    setValueExcel(fileExcel, linea.getArtDescripcion().substring(l, length), 0, i, 3);//Descripción
                }
                i++;
                rows++;
            }
        }

        return rows;
    }

    @Trace
    private void excelCargarEncabezado(XSSFSheet fileExcel, XSSFWorkbook wb, Orden orden) throws Exception {
        SimpleDateFormat fs = new SimpleDateFormat("dd/MM/yyyy");
        setValueExcel(fileExcel, orden.getProveedor().getRfc(), 0, 2, 1);//RFC Proveedor(2,1)
        setValueExcel(fileExcel, fs.format(orden.getFecha()), 0, 2, 4);//Fecha Pedido(2,4)
        if (orden.getNavCode() != null && !orden.getNavCode().isEmpty()) {
            setValueExcel(fileExcel, "Pedido NAV", 0, 2, 5);//Etiqueta Pedido Navision(2,5)
            setValueExcel(fileExcel, orden.getNavCode(), 0, 2, 6);//Pedido Navision(2,6)
        }
        setValueExcel(fileExcel, orden.getRequisicion().getUrl(), 0, 2, 8);//Enlace Requisición SIA(2,8)

        OcRequisicionCheckcodeVO check = ocRequisicionCheckcodeRemote.getRequiCheckCode(orden.getId(), orden.getRequisicion().getId(),
                orden.getProveedor().getRfc());
        setValueExcel(fileExcel, check.getCheckcode(), 0, 2, 9);//Check Code(2,9)

        if (orden.getMoneda() != null && orden.getMoneda().getSiglas() != null && !orden.getMoneda().getSiglas().isEmpty()
                && !"MXP".equalsIgnoreCase(orden.getMoneda().getSiglas())) {
            setValueExcel(fileExcel, orden.getMoneda().getSiglas(), 0, 4, 1);//Divisa(4,1)
        }
        setValueExcel(fileExcel, orden.getConsecutivo(), 0, 4, 4);//No Pedido Prov(4,4)

        //setValueExcel(fileExcel, orden.getProveedor().getRfc(), 0, 6,1);//% Anticipo(6,1)
        String codigoOT = orden.getProyectoOt().getCuentaContable().trim();
        setValueExcel(fileExcel, codigoOT, 0, 6, 4);//OT Código(6,4)
        if (Constantes.PAIS_MEXICO != orden.getCompania().getSiPais().getId()) {
            String codigoOTSub = codigoOT.substring(0, 4);
            if (codigoOTSub != null && !codigoOTSub.isEmpty()) {
                setValueExcel(fileExcel, "Delegacion", 0, 6, 5);//Delegacion (6,6)
                setValueExcel(fileExcel, codigoOTSub, 0, 6, 6);//Delegacion (6,6)
            }
            setValueExcel(fileExcel, "ProyCode", 0, 21, 16);//Etiqueta ProyCode
        } else {
            setValueExcel(fileExcel, "Fecha de entrega", 0, 21, 16);//Etiqueta ProyCode
        }
        setValueExcelFormulas(fileExcel, wb, 0, 6, 13);
        if (orden.getOcTerminoPago() != null) {
            setValueExcel(fileExcel, orden.getOcTerminoPago().getCodigo(), 0, 8, 4);//Términos de Pago(8,4)
            String codigoStr = orden.getOcTerminoPago().getCodigo().replaceAll("D", "");
            int codigoInt = Integer.parseInt(codigoStr);
            setValueExcel(fileExcel, codigoInt, 0, 8, 6);//Términos de Pago(8,4)
            //setValueExcelFormulas(fileExcel, 0, 8, 6);
            setValueExcelFormulas(fileExcel, wb, 0, 8, 13);
        }
        if (orden.getUrl() != null) {
            setValueExcel(fileExcel, orden.getUrl(), 0, 9, 8);//Enlace Requisición SIA(9,8)
        }
        if (orden.getCheckcode() != null) {
            setValueExcel(fileExcel, orden.getCheckcode(), 0, 9, 9);//Check Code(9,9)
        }
        setValueExcel(fileExcel, orden.getReferencia(), 0, 11, 1);//Descripción(11,1)
        setValueExcelFormulas(fileExcel, wb, 0, 11, 6);//Descripción(11,1)

        setValueExcel(fileExcel, orden.getRequisicion().getSolicita().getNombre(), 0, 14, 1);//Envío a Nombre(14,1)
        setValueExcel(fileExcel, orden.getCompania().getEstado(), 0, 14, 4);//Envío a Estado(14,4)

        setValueExcel(fileExcel, new StringBuilder().append(orden.getCompania().getCalle()).toString().substring(0, orden.getCompania().getCalle().length() > 47 ? 47 : orden.getCompania().getCalle().length()), 0, 15, 1);//Envío a Dirección(15,1)
        setValueExcel(fileExcel, orden.getCompania().getCp(), 0, 15, 4);//Envío a C.P.(15,4)

        setValueExcel(fileExcel, orden.getCompania().getColonia(), 0, 16, 1);//Envío a Colonia(16,1)

        setValueExcel(fileExcel, orden.getCompania().getCiudad(), 0, 17, 1);//Envío a Ciudad(17,1)

        setValueExcel(fileExcel, "REPSE", 0, 1, 5);//Etiqueta Archivo REPSE

        setValueExcel(fileExcel, orden.isRepse() ? "1" : "0", 0, 1, 6);//Valor Archivo REPSE

    }

    public List<OrdenVO> ordenesPorCondicionPago(int condicionPago, int bloque) {
        List<OrdenVO> lo = null;
        List<Object[]> lobj = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ord.id, ord.CONSECUTIVO as codigo, ord.REFERENCIA, a.RECHAZADA as devuelta, ge.NOMBRE,");//4
            sb.append(" re.CONSECUTIVO as codigo_requisicion, ord.fecha as creada, com.SIGLAS,");//7
            sb.append(" ord.SUBTOTAL, ord.TOTAL,   est.NOMBRE,");//10
            sb.append(" a.FECHA_SOLICITO, a.HORA_SOLICITO,");
            sb.append(" mo.nombre, pro.nombre, ");//14
            sb.append(" ord.navCode, ord.url, a.estatus");
            sb.append(" FROM ORDEN ord, AUTORIZACIONES_ORDEN a, REQUISICION re, Compania com, estatus est,");
            sb.append(" moneda mo, GERENCIA ge, proveedor pro");
            sb.append(" WHERE ord.condicion_pago = ").append(condicionPago);
            if (bloque > 0) {
                sb.append(" and ord.AP_CAMPO = ").append(bloque);
            }
            sb.append(" and ord.PROVEEDOR = pro.ID ");
            sb.append(" and ord.ID = a.ORDEN and ord.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            sb.append(" and ord.GERENCIA = ge.id and a.ESTATUS = est.id");
            sb.append(" and ord.REQUISICION = re.id and ord.COMPANIA = com.rfc");
            sb.append(" and ord.MONEDA = mo.id ");
            sb.append(" order by ord.consecutivo asc");
            LOGGER.info(this, "Q: OC/S por condicion pago: " + sb.toString());

            lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lobj != null && lo.size() > Constantes.CERO) {
                for (Object[] objects : lobj) {
                    lo.add(castOrden(objects));
                }
            }

            return lo;
        } catch (Exception e) {
            return null;
        }
    }

    public long totalOrdenesPorProveedor(int idProveedor) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from ORDEN o where o.PROVEEDOR  = ").append(idProveedor);
        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult()).intValue();
    }

    public OrdenVO buscarOrdenPorConsecutivoEmpresa(String consecutivo, int idBloque, boolean condetalle) {
        StringBuilder sb = new StringBuilder();
        OrdenVO ovo = null;
        try {
            sb.append(buscarOrden());
            sb.append(" where o.consecutivo = '").append(consecutivo).append("'");
            sb.append(" and o.ap_campo = ").append(idBloque);
            LOGGER.info(this, "Q : :: :  : " + sb.toString());
            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (obj != null) {
                ovo = castOrdenTotal(obj, condetalle);
            }
        } catch (Exception e) {
            LOGGER.fatal(this, "Error al recuperar la OC/S " + consecutivo + " # # # # # # " + e.getMessage());
        }
        return ovo;
    }

    public OrdenVO buscarOrdenPorId(int idOrden, int idBloque, boolean condetalle) {
        StringBuilder sb = new StringBuilder();
        OrdenVO ovo = null;
        try {
            sb.append(buscarOrden());
            sb.append(" where o.id = ").append(idOrden);
            //
            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (obj != null) {
                ovo = castOrdenTotal(obj, condetalle);
            }
        } catch (Exception e) {
            LOGGER.fatal(this, "Error al recuperar la OC/S " + idOrden + " # # # # # # " + e);
        }
        return ovo;
    }

    private OrdenVO castOrdenTotal(Object[] obj, boolean conDetalle) {
        OrdenVO o = new OrdenVO();
        o.setId((Integer) obj[0]);
        o.setConsecutivo(obj[1] != null ? (String) obj[1] : "");
        o.setIdRequisicion((Integer) obj[2]);
        o.setRequisicion((String) obj[3]);
        o.setIdGerencia((Integer) obj[4]);
        o.setGerencia((String) obj[5]);
        o.setIdResponsableGerencia((String) obj[6]);
        o.setResponsableGerencia((String) obj[7]);
        o.setIdProyectoOt((Integer) obj[8]);
        o.setNombreProyectoOT((String) obj[9]);
        o.setCuentaContable((String) obj[10]);
        o.setIdProveedor(obj[11] == null ? 0 : (Integer) obj[11]);
        o.setProveedor(obj[12] == null ? "" : (String) obj[12]);
        o.setRfcCompania((String) obj[13]);
        o.setCompania((String) obj[14]);
        o.setIdContactoCompania((String) obj[15]);
        o.setNombreContactoCompania((String) obj[16]);
        o.setIdGerenteCompras((String) obj[17]);
        o.setNombreGerenteCompras((String) obj[18]);
        o.setIdAnalista((String) obj[19]);
        o.setAnalista((String) obj[20]);
        o.setIdMoneda(obj[21] == null ? 0 : (Integer) obj[21]);
        o.setMoneda(obj[22] == null ? Constantes.VACIO : (String) obj[22]);
        o.setMonedaSiglas(obj[23] == null ? Constantes.VACIO : (String) obj[23]);
        o.getContratoVO().setNumero((String) obj[24]);
        o.setReferencia((String) obj[25]);
        o.setFecha((Date) obj[26]);
        o.setFechaEntrega((Date) obj[27]);
        o.setDestino((String) obj[28]);
        o.setSuperaRequisicion((Boolean) obj[29]);
        o.setSubTotal((Double) obj[30]);
        o.setConIva((Boolean) obj[31]);
        o.setPorcentajeIva(obj[32] == null ? Constantes.VACIO : (String) obj[32]);
        o.setIva(obj[33] == null ? 0.0 : Double.valueOf(obj[33].toString()));
        o.setTotal((Double) obj[34]);
        o.setTotalUsd(obj[35] == null ? 0.0 : Double.valueOf(obj[35].toString()));
        o.setNota(obj[36] == null ? Constantes.VACIO : (String) obj[36]);
        o.setObservaciones(obj[37] == null ? Constantes.VACIO : (String) obj[37]);
        o.setEsOC((Boolean) obj[38] != null ? (Boolean) obj[38] : Constantes.BOOLEAN_FALSE);
        o.setIdBloque((Integer) obj[39]);
        o.setBloque((String) obj[40]);
        o.setSuperaMonto(obj[41] != null ? (Boolean) obj[41] : Constantes.BOOLEAN_FALSE);
        o.setIdUnidadCosto(obj[42] == null ? 0 : (Integer) obj[42]);
        o.setUnidadCosto(obj[43] == null ? Constantes.VACIO : (String) obj[43]);
        o.setIdStatus((Integer) obj[44]);
        o.setEstatus((String) obj[45]);
        //
        o.setTipo(obj[46] == null ? Constantes.VACIO : (String) obj[46]);
        o.setCheckCode(obj[47] == null ? Constantes.VACIO : (String) obj[47]);
        o.setUrl(obj[48] == null ? Constantes.VACIO : (String) obj[48]);
        o.setNavCode(obj[49] == null ? Constantes.VACIO : (String) obj[49]);
        o.setMultiproyecto((Boolean) obj[50]);
        o.setIdCfdi(obj[51] != null ? (Integer) obj[51] : Constantes.CERO);
        o.setCodigoCfdi(obj[52] != null ? (String) obj[52] : Constantes.VACIO);
        o.setNombreCfdi((String) obj[53]);
        o.setIdAutorizaOrden((Integer) obj[54]);
        o.setTotalItems((Double) obj[55]);
        o.setTotalRecibidos((Double) obj[56]);
        o.setTerminoPago(obj[57] == null ? Constantes.VACIO : (String) obj[57]);
        o.setRepse((boolean) obj[58]);

        if (conDetalle) {
            o.setDetalleOrden(ordenDetalleServicioImpl.itemsPorOrden(o.getId()));
        }
        return o;
    }
    //

    public void actualizaMontoOrden(Orden orden, String idSesion) {
        double total = ordenDetalleServicioImpl.traerTotalOrden(orden.getId());
        double desc = ordenDetalleServicioImpl.traerTotalDescuentoOrden(orden.getId());
        try {
            // Actualizo la orden
            orden.setModifico(this.usuarioServicioImpl.find(idSesion));
            orden.setFechaModifico(new Date());
            orden.setHoraModifico(new Date());
            orden.setTotal(total);
            orden.setSubtotal(total);
            orden.setDescuento(desc);
            edit(orden);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public List<OrdenVO> traerOrdenComporaUsuarioEstatus(String usuario, int idEstatus, int campo) {
        StringBuilder sb = new StringBuilder();
        List<OrdenVO> lord = null;
        try {
            LOGGER.info(this, "Estatus  : : : :: : " + idEstatus);
            sb.append("select  a.ID, o.ID, o.CONSECUTIVO,o.FECHA, o.REFERENCIA, o.TOTAL as Dolar, ");
            sb.append(" p.id, p.nombre, m.ID, m.NOMBRE, uc.id, uc.NOMBRE, ");
            sb.append(estatusCampoSelccion(idEstatus));
            sb.append(" from orden o ");
            sb.append("	    inner join AUTORIZACIONES_ORDEN a on o.ID = a.ORDEN");
            if (Constantes.ESTATUS_PENDIENTE_R == idEstatus) {
                sb.append(estatusCampoCondicion(idEstatus));
            } else {
                sb.append(estatusCampoCondicion(idEstatus)).append(usuario).append("'");
            }
            sb.append("	    left join PROVEEDOR p on o.PROVEEDOR = p.id");
            sb.append("	    left join MONEDA m on o.MONEDA = m.ID");
            sb.append("	    left join OC_unidad_costo uc on o.OC_unidad_costo = uc.ID  ");
            sb.append(" where o.ap_campo =").append(campo);
            if (Constantes.ESTATUS_PENDIENTE_R == idEstatus) {
                sb.append(" and o.analista = '").append(usuario).append("'");
            }
            sb.append(" and o.ELIMINADO = 'False'");
            sb.append(" order by o.consecutivo asc");
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                lord = new ArrayList<>();
                for (Object[] objects : lo) {
                    lord.add(castOrdenCambiarUsuario(objects));
                }
            }
        } catch (Exception e) {
            LOGGER.fatal(this, "Ocurrio un error al buscar ordenes " + e.getMessage());
        }
        return lord;
    }

    private StringBuilder estatusCampoSelccion(int idStatus) {
        StringBuilder comodinSelec = new StringBuilder();
        switch (idStatus) {
            case Constantes.ESTATUS_PENDIENTE_R: //101
                comodinSelec.append(" o.fecha, o.HORA_Genero,");
                comodinSelec.append(" o.analista");
                break;
            case Constantes.ESTATUS_SOLICITADA_R: //110
                comodinSelec.append(" a.FECHA_AUTORIZO_GERENCIA ,a.HORA_AUTORIZO_GERENCIA,");
                comodinSelec.append(" a.AUTORIZA_GERENCIA");
                break;
            case Constantes.ESTATUS_VISTO_BUENO_R: //120
                comodinSelec.append(" a.FECHA_AUTORIZO_MPG, a.HORA_AUTORIZO_MPG,");
                comodinSelec.append(" a.AUTORIZA_MPG");
                break;
            case Constantes.ESTATUS_POR_APROBAR_SOCIO: //135
                comodinSelec.append(" a.FECHA_AUTORIZO_FINANZAS, a.HORA_AUTORIZO_FINANZAS,");
                comodinSelec.append(" a.AUTORIZA_FINANZAS");
                break;
            case Constantes.ESTATUS_REVISADA://130
                comodinSelec.append(" a.FECHA_AUTORIZO_IHSA, a.HORA_AUTORIZO_IHSA,");
                comodinSelec.append(" a.AUTORIZA_IHSA");
                break;
            case Constantes.ESTATUS_APROBADA://140
                comodinSelec.append(" a.FECHA_AUTORIZO_COMPRAS, a.HORA_AUTORIZO_COMPRAS,");
                comodinSelec.append(" a.AUTORIZA_COMPRAS");
                break;
            case Constantes.ESTATUS_REVISA_SOCIO: //125
                comodinSelec.append(" a.FECHA_AUTORIZO_FINANZAS, a.HORA_AUTORIZO_FINANZAS,");
                comodinSelec.append(" a.AUTORIZA_FINANZAS ");
        }
        return comodinSelec;
    }

    private StringBuilder estatusCampoCondicion(int idStatus) {
        StringBuilder comodinWhere = new StringBuilder();
        comodinWhere.append(" and a.estatus = ").append(idStatus);
        switch (idStatus) {
            case Constantes.ESTATUS_PENDIENTE_R: //101
                comodinWhere.append(" ");//and a.SOLICITO = '");
                break;
            case Constantes.ESTATUS_SOLICITADA_R: //110
                comodinWhere.append(" and a.AUTORIZA_GERENCIA = '");
                break;
            case Constantes.ESTATUS_VISTO_BUENO_R: //120
                comodinWhere.append(" and a.AUTORIZA_MPG = '");
                break;
            case Constantes.ESTATUS_POR_APROBAR_SOCIO: //135
                comodinWhere.append(" and a.AUTORIZA_FINANZAS = '");
                break;
            case Constantes.ESTATUS_REVISADA://130
                comodinWhere.append(" and a.AUTORIZA_IHSA = '");
                break;
            case Constantes.ESTATUS_APROBADA://140
                comodinWhere.append(" and a.AUTORIZA_COMPRAS = '");
                break;
            case Constantes.ESTATUS_REVISA_SOCIO: //125
                comodinWhere.append("and a.AUTORIZA_FINANZAS = '");
                break;
        }
        return comodinWhere;
    }

    private StringBuilder traduccionEstatus(int idStatus) {
        StringBuilder comodinWhere = new StringBuilder();
        switch (idStatus) {
            case Constantes.ESTATUS_PENDIENTE_R: //101
                comodinWhere.append(" Sin Solicitar");
                break;
            case Constantes.ESTATUS_SOLICITADA_R: //110
                comodinWhere.append(" Visto Bueno");
                break;
            case Constantes.ESTATUS_VISTO_BUENO_R: //120
                comodinWhere.append(" Revisar");
                break;
            case Constantes.ESTATUS_POR_APROBAR_SOCIO: //125
                comodinWhere.append(" Revisar ");
                break;
            case Constantes.ESTATUS_REVISADA://130
                comodinWhere.append(" Aprobar");
                break;
            case Constantes.ESTATUS_APROBADA://140
                comodinWhere.append(" Autorizar");
                break;
        }
        return comodinWhere;
    }

    private OrdenVO castOrdenCambiarUsuario(Object[] objects) {
        OrdenVO o = new OrdenVO();
        o.setIdAutorizaOrden((Integer) objects[0]);
        o.setId((Integer) objects[1]);
        o.setConsecutivo((String) objects[2]);
        o.setFechaGenero((Date) objects[3]);
        o.setReferencia((String) objects[4]);
        o.setTotal((Double) objects[5]);
        o.setIdProveedor((Integer) objects[6] == null ? 0 : (Integer) objects[6]);
        o.setProveedor((String) objects[7]);
        o.setIdMoneda((Integer) objects[8] == null ? 0 : (Integer) objects[8]);
        o.setMoneda((String) objects[9]);
        o.setIdUnidadCosto((Integer) objects[10] == null ? 0 : (Integer) objects[10]);
        o.setUnidadCosto((String) objects[11]);
        o.setFecha((Date) objects[12]);
        o.setHora((Date) objects[13]);
        o.setAnalista((String) objects[14]);
        o.setSelected(true);
        return o;
    }

    public boolean pasarOrdenesCompra(List<OrdenVO> lo, int idStatus, String usuarioOrden, String usuarioAprobara, String idSesion, String rfcEmpresa, String correoSesion) {
        boolean v;
        Usuario para = usuarioServicioImpl.find(usuarioAprobara);
        Usuario cc = usuarioServicioImpl.find(usuarioOrden);
        String status = traduccionEstatus(idStatus).toString();
        v = notificacionesOrdenRemote.enviarNotificacionCambioOrden(para.getEmail(), cc.getEmail(), correoSesion, lo, para.getNombre(), cc.getNombre(), rfcEmpresa, status);
        if (v) {
            for (OrdenVO ordenVO : lo) {
                v = estatusCampoAprueba(ordenVO.getIdAutorizaOrden(), idStatus, usuarioAprobara, idSesion);
            }
        }
        return v;
    }

    private boolean estatusCampoAprueba(int idAutorizaOrden, int idStatus, String usuarioAprobara, String idSesion) {
        try {
            AutorizacionesOrden ao = autorizacionesOrdenRemote.find(idAutorizaOrden);
            Orden o = ao.getOrden();
            ao.setFechaGenero(new Date());
            ao.setHoraModifico(new Date());
            ao.setModifico(this.usuarioServicioImpl.find(idSesion));
            switch (idStatus) {
                case Constantes.ESTATUS_PENDIENTE_R: //101
                    ao.setSolicito(this.usuarioServicioImpl.find(usuarioAprobara));
                    o.setAnalista(this.usuarioServicioImpl.find(usuarioAprobara));
                    break;
                case Constantes.ESTATUS_SOLICITADA_R: //110
                    ao.setAutorizaGerencia(this.usuarioServicioImpl.find(usuarioAprobara));
                    break;
                case Constantes.ESTATUS_VISTO_BUENO_R: //120
                    ao.setAutorizaMpg(this.usuarioServicioImpl.find(usuarioAprobara));
                    break;
                case Constantes.ESTATUS_REVISADA: //130
                    ao.setAutorizaIhsa(this.usuarioServicioImpl.find(usuarioAprobara));
                    break;
                case Constantes.ESTATUS_POR_APROBAR_SOCIO: //125
                    ao.setAutorizaFinanzas(this.usuarioServicioImpl.find(usuarioAprobara));
                    break;
                case Constantes.ESTATUS_APROBADA://140
                    ao.setAutorizaCompras(this.usuarioServicioImpl.find(usuarioAprobara));
                    break;
            }

            autorizacionesOrdenRemote.editar(ao);
            if (Constantes.ESTATUS_PENDIENTE_R == idStatus) {
                edit(o);
            }
            return true;

        } catch (Exception e) {
            LOGGER.info(this, "Ocurrio un error al cambiar el usuario en OC/S " + e.getMessage());
            return false;
        }
    }

    public boolean existeNavCode(String navCode) {
        StringBuilder sb = new StringBuilder();
        boolean existe = false;
        try {

            sb.append(" select count(o.ID) ");
            sb.append(" from ORDEN o ");
            sb.append(" where o.NAVCODE = '").append(navCode).append("' ");
            sb.append(" and o.ELIMINADO = 'False' ");

            int ordenes = ((Long) em.createNativeQuery(sb.toString()).getSingleResult()).intValue();
            if (ordenes > 0) {
                existe = true;
            }
        } catch (Exception e) {
            existe = true;
            LOGGER.fatal(this, "Ocurrio un error al buscar ordenes " + e.getMessage());
        }
        return existe;
    }

    public boolean productosLineasGuadados(int orderID) {
        StringBuilder sb = new StringBuilder();
        boolean listos = false;
        try {
            sb.append(" select count(od.ORDEN), ");
            sb.append(" (select count(od.ID) from ORDEN_DETALLE od where od.ORDEN = ").append(orderID);
            sb.append(" and (od.ELIMINADO = 'False' or od.ELIMINADO is null)) ");
            sb.append(" from ORDEN o ");
            sb.append(" inner join ORDEN_DETALLE od on od.ORDEN = o.ID ");
            sb.append(" where od.OC_PRODUCTO_COMPANIA is not null  ");
            sb.append(" and o.ID = ").append(orderID);
            sb.append(" and (o.ELIMINADO = 'False' or o.ELIMINADO is null) ");

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();

            if (lo != null && lo.size() == 1 && ((Object[]) lo.get(0)).length == 2
                    && ((Long) ((Object[]) lo.get(0))[0]).intValue() == ((Long) ((Object[]) lo.get(0))[1]).intValue()) {
                listos = true;
            }
        } catch (Exception e) {
            LOGGER.fatal(this, "Ocurrio un error al buscar ordenes " + e.getMessage());
        }
        return listos;
    }

    private OrdenVO castOrdenColumnaGenerales(Object[] objects) {
        OrdenVO o = new OrdenVO();
        try {
            o.setId((Integer) objects[0]);
            o.setConsecutivo(String.valueOf(objects[1]));
            o.setReferencia(String.valueOf(objects[2]));
            o.setRequisicion((String) objects[3]);
            o.setFecha((Date) objects[4]);
            o.setEstatus((String) objects[5]);
            o.setProveedor((String) objects[6]);
            o.getContratoVO().setNumero(objects[7] == null ? Constantes.OCS_SIN_CONTRATO : (String) objects[7]);
            o.setTotal((Double) objects[8]);
            o.setMoneda((String) objects[9]);
            o.setSuperaMonto(objects[10] != null ? (Boolean) objects[10] : Constantes.BOOLEAN_FALSE);
            o.setIva((Double) objects[11]);
            o.setSubTotal((Double) objects[12]);
            o.setAnalista((String) objects[13]);
            o.setLeida(objects[14] != null ? (Boolean) objects[14] : Constantes.BOOLEAN_FALSE);
            o.setNavCode(objects[15] == null ? "-" : (String) objects[15]);
            o.setErrorEnvio(objects[16] != null ? (Boolean) objects[16] : Constantes.BOOLEAN_FALSE);
            o.setTipo((String) objects[17]);
            o.setIdProveedor((Integer) objects[18]);
            o.setGerencia((String) objects[19]);
            o.setSelected(false);
            // //Buscar en movimiento
            // buscar los el convenio de la OCS
            o.setContratoActivo(objects[20] == null ? 0 : (Integer) objects[20]);
            o.setFechaEntrega(objects[21] == null ? null : (Date) objects[21]);
            o.setProveedorRepse((boolean) objects[22]);
            o.setRepse((boolean) objects[23]);
            //
            List<ContratoVO> contratoVO = cvConvenioAdjuntoLocal.traerPorConvenioPorNumero(o.getContratoVO().getNumero());

            if (contratoVO == null || contratoVO.isEmpty()) {
                o.getContratoVO().setId(0);
            } else {
                o.setListaConvenio(contratoVO);
                o.getContratoVO().setId(Constantes.MODULO_CONTRATO);
            }

            List<OrdenEtsVo> listaETS = ocOrdenEtsRemote.traerEtsPorOrdenCategoria(o.getId(), Constantes.OCS_CATEGORIA_TABLA);
            if (listaETS != null) {
                if (listaETS.size() == Constantes.UNO) {
                    o.getAdjuntoETS().setId(listaETS.get(0).getId());
                    o.getAdjuntoETS().setUuid(listaETS.get(0).getUuid());
                    o.getAdjuntoETS().setNombre(listaETS.get(0).getNombreSinUUID());
                } else {
                    o.getAdjuntoETS().setId(Constantes.CERO);
                    o.setListaETS(listaETS);
                }
            }

        } catch (Exception e) {
            LOGGER.error(e);
        }
        return o;
    }

    public List<OrdenVO> traerOrdenStatusUsuarioRol(int idStatus, int idCampo, String idUsuario, String codRol) {
        StringBuilder sb = new StringBuilder();
        sb.append(consultaOrden());
        sb.append("     inner join si_usuario_rol ur on ur.usuario = '").append(idUsuario).append("'");
        sb.append("  where o.AP_CAMPO = ").append(idCampo);
        sb.append("  and ao.ESTATUS = ").append(idStatus);
        sb.append("  and '").append(idUsuario).append("' in (select ur.USUARIO from  si_usuario_rol ur where ur.SI_ROL = (SELECT id from SI_ROL where codigo = '").append(codRol).append("'))");
        //    System.out.println("sia-nav : : :  " + sb.toString());
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<OrdenVO> lor = null;
        if (lo != null) {
            lor = new ArrayList<>();
            for (Object[] objects : lo) {
                lor.add(castOrdenColumnaGenerales(objects));
            }
        }
        return lor;

    }

    public List<OrdenVO> buscarOCS(int idGerencia, int idProveedor, int idMoneda, int minimo, double maximo, String tipo, int idCampo, int rango, boolean agergarFecha, String inicio, String fin) {
        List<OrdenVO> lor = null;
        StringBuilder sb = new StringBuilder();
        sb.append(" select o.id, o.CONSECUTIVO, o.REFERENCIA, p.nombre from orden o ");
        sb.append("     inner join proveedor p on o.proveedor = p.id");
        sb.append(" where o.CONSECUTIVO is not null");
        if (idProveedor > 0) {
            sb.append(" and o.PROVEEDOR = ").append(idProveedor);
        }
        if (idGerencia > 0) {
            sb.append(" and o.GERENCIA = ").append(idGerencia);
        }

        if (rango != 5) {
            if (idMoneda == 1) {
                sb.append(" and o.TOTAL between ").append(minimo).append(" and ").append(maximo);
            } else {
                sb.append(" and o.TOTAL_USD between ").append(minimo).append(" and ").append(maximo);
            }
        }
        if (agergarFecha) {
            sb.append(" and o.fecha between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date)").append(" and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
        }
        sb.append(" and o.ap_campo = ").append(idCampo);
        if (tipo.equals(TipoRequisicion.PS.name())
                || tipo.equals(TipoRequisicion.AF.name())
                || tipo.equals(TipoRequisicion.AI.name())) {
            sb.append(" and o.TIPO = '").append(tipo).append("'");
        }
        sb.append(" and o.MONEDA = ").append(idMoneda);
        sb.append(" order by upper(o.REFERENCIA) asc");
        //
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        if (lo != null) {
            lor = new ArrayList<>();
            for (Object[] objects : lo) {
                lor.add(ordenBuscar(objects));
            }
        }
        return lor;
    }

    private OrdenVO ordenBuscar(Object[] obj) {
        OrdenVO o = new OrdenVO();
        o.setId((Integer) obj[0]);
        o.setConsecutivo((String) obj[1]);
        o.setReferencia((String) obj[2]);
        o.setProveedor((String) obj[3]);
        return o;
    }

    public double traerTotalMaximoOCS(int moneda) {
        StringBuilder sb = new StringBuilder();
        if (moneda == 1) {
            sb.append(" select max(o.TOTAL)::numeric  from orden o");
        } else {
            sb.append(" select max(o.TOTAL_USD)::numeric  from orden o");
        }
        return (Double) em.createNativeQuery(sb.toString()).getSingleResult();
    }

    public List<OrdenVO> ordenesContado(String fecha, int campo) {
        StringBuilder sb = new StringBuilder();
        sb.append(consultaOrden());
        sb.append("  where o.fecha = cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fecha)).append("' as date)'");
        sb.append("  and ao.ESTATUS > ").append(Constantes.ORDENES_SIN_SOLICITAR);
        sb.append("  and ao.ESTATUS <> ").append(Constantes.ORDENES_CANCELADAS);
        sb.append("  and o.AP_CAMPO = ").append(campo);
        sb.append("  and o.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        //    System.out.println("sia-nav : : :  " + sb.toString());
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<OrdenVO> lor = null;
        if (lo != null) {
            lor = new ArrayList<>();
            for (Object[] objects : lo) {
                lor.add(castOrdenColumnaGenerales(objects));
            }
        }
        return lor;
    }

    public Orden buscarPorOrdenConsecutivo(String consecutivo, int bloque, String usuario) {
        Orden r = null;
        try {
            boolean v = siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_COMPRA, Constantes.CODIGO_ROL_CONS_OCS, bloque);
            StringBuilder sb = new StringBuilder();
            sb.append("select o.* from AUTORIZACIONES_ORDEN ao ");
            sb.append("     inner join orden o on ao.ORDEN = o.ID");
            sb.append("  where o.consecutivo = '").append(consecutivo).append("'");
            sb.append("   and o.ap_campo = ").append(bloque);
            if (!v) {
                sb.append("   and '").append(usuario).append("'");
                sb.append("   in (ao.SOLICITO, ao.AUTORIZA_GERENCIA, ao.AUTORIZA_MPG, ao.AUTORIZA_IHSA, ao.AUTORIZA_FINANZAS, ao.AUTORIZA_COMPRAS)");
            }
            LOGGER.info(this, "Q: : : : : " + sb.toString());
            r = (Orden) em.createNativeQuery(sb.toString(), "orden_map").getSingleResult();
            return r;
        } catch (Exception e) {
            LOGGER.fatal(this, "Error al buscar la orden por consecutivo: " + e.getMessage());
        }
        return r;
    }

    public OrdenVO buscarOrdenPorUsuarioInvArticulo(String consecutivo, int idBloque, String usuario, boolean condetalle) {
        StringBuilder sb = new StringBuilder();
        OrdenVO ovo = null;
        try {
            boolean v = siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_COMPRA, Constantes.CODIGO_ROL_CONS_OCS, idBloque);
            sb.append(buscarOrden());
            sb.append(" ");
            sb.append(" where o.consecutivo = '").append(consecutivo).append("'");
            sb.append(" and o.ap_campo = ").append(idBloque);
            sb.append(" and (select count(od.id) from ORDEN_DETALLE od where od.ORDEN = o.id and od.INV_ARTICULO is not null) > 0 ");
            if (!v) {
                sb.append("   and '").append(usuario).append("'");
                sb.append("   in (ao.SOLICITO, ao.AUTORIZA_GERENCIA, ao.AUTORIZA_MPG, ao.AUTORIZA_IHSA, ao.AUTORIZA_FINANZAS, ao.AUTORIZA_COMPRAS)");
            }
            LOGGER.info(this, "Q : :: :  : " + sb.toString());
            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (obj != null) {
                ovo = castOrdenTotal(obj, condetalle);
            }
        } catch (Exception e) {
            LOGGER.fatal(this, "Error al recuperar la OC/S " + consecutivo + " # # # # # # " + e.getMessage());
        }
        return ovo;
    }

    public OrdenVO buscarOrdenPorUsuario(String consecutivo, int idBloque, String usuario, boolean condetalle) {
        StringBuilder sb = new StringBuilder();
        OrdenVO ovo = null;
        try {
            boolean v = siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_COMPRA, Constantes.CODIGO_ROL_CONS_OCS, idBloque);
            sb.append(buscarOrden());
            sb.append(" where o.consecutivo = '").append(consecutivo).append("'");
            sb.append(" and o.ap_campo = ").append(idBloque);
            if (!v) {
                sb.append("   and '").append(usuario).append("'");
                sb.append("   in (ao.SOLICITO, ao.AUTORIZA_GERENCIA, ao.AUTORIZA_MPG, ao.AUTORIZA_IHSA, ao.AUTORIZA_FINANZAS, ao.AUTORIZA_COMPRAS)");
            }
            LOGGER.info(this, "Q : :: :  : " + sb.toString());
            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (obj != null) {
                ovo = castOrdenTotal(obj, condetalle);
            }
        } catch (Exception e) {
            LOGGER.fatal(this, "Error al recuperar la OC/S " + consecutivo + " # # # # # # " + e.getMessage());
        }
        return ovo;
    }

    private String buscarOrden() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("select o.ID, o.CONSECUTIVO,o.requisicion, r.consecutivo,o.gerencia, g.nombre, o.RESPONSABLE_GERENCIA,");
            sb.append(" u.nombre, o.PROYECTO_OT, pot.nombre, pot.cuenta_contable, o.PROVEEDOR, p.nombre, o.COMPANIA, c.nombre,");
            sb.append(" o.CONTACTO_COMPANIA, uc.nombre, o.GERENTE_COMPRAS, ugc.nombre,o.ANALISTA, ucomp.nombre, o.MONEDA, m.nombre, m.siglas,");
            sb.append(" o.CONTRATO, o.REFERENCIA, o.FECHA, o.FECHA_ENTREGA, o.DESTINO, o.SUPERA_REQUISICION, o.SUBTOTAL, ");
            sb.append(" o.CON_IVA, o.PORCENTAJE_IVA, o.IVA, o.TOTAL, o.TOTAL_USD, o.NOTA, o.OBSERVACIONES,o.ES_OC, o.AP_CAMPO,");
            sb.append(" cp.nombre, o.SUPERA_MONTO, o.OC_unidad_costo, uco.nombre, ao.estatus, e.nombre, o.tipo, o.checkcode, o.url, o.navcode, "
                    + " o.multiproyecto, uso.id, uso.codigo, uso.nombre, ao.id, "
                    + " (SELECT COALESCE(sum(od.cantidad), 0) from ORDEN_DETALLE od where od.orden = o.id and od.ELIMINADO = 'False'),"
                    + " (SELECT COALESCE(sum(od.CANTIDAD_RECIBIDA), 0) from ORDEN_DETALLE od where od.orden = o.id  and od.ELIMINADO = 'False') ,");
            sb.append(" tpago.nombre as termino_pago, o.repse ");
            sb.append(" from ORDEN o");
            sb.append("     left join REQUISICION r on o.REQUISICION = r.ID");
            sb.append("     left join GERENCIA g on o.GERENCIA = g.ID");
            sb.append("     left join USUARIO u on o.RESPONSABLE_GERENCIA = u.ID");
            sb.append("     left join PROYECTO_OT pot on o.PROYECTO_OT = pot.ID");
            sb.append("     left join PROVEEDOR p on o.PROVEEDOR = p.ID");
            sb.append("     left join compania c on o.COMPANIA = c.RFC");
            sb.append("     left join USUARIO uc on o.CONTACTO_COMPANIA = uc.id ");
            sb.append("     left join USUARIO ugc on o.GERENTE_COMPRAS = ugc.ID");
            sb.append("     left join USUARIO ucomp on o.ANALISTA = ucomp.ID");
            sb.append("     left join MONEDA m on o.MONEDA = m.ID");
            sb.append("     left  join AP_CAMPO cp on o.AP_CAMPO = cp.ID ");
            sb.append("     left join OC_unidad_costo uco on o.OC_unidad_costo = uco.ID");
            sb.append("     right join AUTORIZACIONES_ORDEN ao on ao.ORDEN = o.ID");
            sb.append("     left join ESTATUS e on ao.ESTATUS = e.id");
            sb.append("     left join oc_uso_cfdi uso on o.oc_uso_cfdi = uso.id");
            sb.append("     left join oc_termino_pago tpago on tpago.id = o.oc_termino_pago"); //--Cambio por joel rodriguez 17-jul-2021
        } catch (Exception e) {
            LOGGER.fatal(this, "Error en la concsulta  # # # # # # " + e.getMessage());
        }
        return sb.toString();
    }

    public boolean cambiarAnalistaOCS(String sesion, int idOrden, String usuarioSolicita) {
        boolean v = true;
        try {
            Orden orden = find(idOrden);
            orden.setAnalista(this.usuarioServicioImpl.find(usuarioSolicita));
            orden.setModifico(this.usuarioServicioImpl.find(sesion));
            orden.setFechaModifico(new Date());
            orden.setHoraModifico(new Date());
            edit(orden);
            //autorizacione OCS
            autorizacionesOrdenRemote.cambiarAnalistaOCS(sesion, idOrden, usuarioSolicita);
            //requisicioes
            requisicionServicioImpl.cambiarAnalistaRequisicion(sesion, orden.getRequisicion().getId(), usuarioSolicita);
            //
        } catch (Exception e) {
            LOGGER.fatal(this, "Error al cambiar la OCS  + + + + " + e.getMessage());
            v = false;
        }
        return v;
    }

    /**
     *
     * @param idUsuario
     * @param idCampo
     * @return
     */
    public long totalOcsPendientePorCampo(String idUsuario, int idCampo) {
        int total = 0;
        if (idCampo > 0) {
            //List<CampoVo> lc = apCampoRemote.getAllFieldExceptCurrent(idCampo);
            try {
                total = total(idUsuario, idCampo);
            } catch (Exception e) {
                LOGGER.info(this, "exc: totaltes campos : " + e.getMessage() + "  - - -  " + e.getCause());
            }
        } else {
            List<CampoUsuarioPuestoVo> lista = apCampoUsuarioRhPuestoRemote.getAllPorUsurio(idUsuario);
            for (CampoUsuarioPuestoVo campo : lista) {
                total += total(idUsuario, campo.getIdCampo());
            }
        }
        return total;
    }

    public long totalOcsPendientePorCampoAprobadores(String idUsuario, int idCampo) {
        int total = 0;
        try {
            //System.out.println("@totalOcsPendientePorCampoAprobadores");
            //se omiten las oc sin solicitar y por recibir
            //total += totalOrdenesSinSolicitar(idUsuario, idCampo);
            //total += getTotalTareasSinCompletaAF(idUsuario, idCampo);
            //total += getTotalTareasSinCompletaPS(idUsuario, idCampo);
            total += totalOrdenesSinAprobar(idUsuario, idCampo);
            total += totalOrdenesSinAutorizarMPG(idUsuario, idCampo);
            total += totalOrdenesSinAutorizarFinanzas(idUsuario, idCampo);
            total += getTotalOrdenesSinAutorizarIHSA(idUsuario, idCampo);
            total += getTotalOrdenesSinAutorizarCompras(idUsuario, idCampo);
            total += totalOrdenesSinAprobarSocio(idUsuario, idCampo);
            //total += totalOrdenesEstatusUsuario(idUsuario, idCampo, OrdenEstadoEnum.POR_RECIBIR.getId());

        } catch (Exception e) {
            LOGGER.info(this, "exc: totaltes campos : " + e.getMessage() + "  - - -  " + e.getCause());
        }

        return total;
    }

    private int total(String idUsuario, int idCampo) {
        int t = 0;
        //List<CampoVo> lc = apCampoRemote.getAllFieldExceptCurrent(idCampo);
        t += getTotalTareasSinCompletaAF(idUsuario, idCampo);
        t += getTotalTareasSinCompletaPS(idUsuario, idCampo);
        t += totalOrdenesSinSolicitar(idUsuario, idCampo);
        t += totalOrdenesSinAprobar(idUsuario, idCampo);
        t += totalOrdenesSinAutorizarMPG(idUsuario, idCampo);
        t += totalOrdenesSinAutorizarFinanzas(idUsuario, idCampo);
        t += getTotalOrdenesSinAutorizarIHSA(idUsuario, idCampo);
        t += getTotalOrdenesSinAutorizarCompras(idUsuario, idCampo);
        t += totalOrdenesSinAprobarSocio(idUsuario, idCampo);
        t += totalOrdenesEstatusUsuario(idUsuario, idCampo, OrdenEstadoEnum.POR_RECIBIR.getId());
        return t;
    }

    private String consultaOrden() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT o.ID, o.CONSECUTIVO,o.REFERENCIA, r.consecutivo, a.FECHA_SOLICITO,e.nombre, p.nombre, ");
        sb.append(" o.CONTRATO, o.TOTAL,  m.siglas, o.supera_monto,  o.iva,  o.subTotal,u.nombre,  o.leida , o.navCode, a.ERROR_ENVIO, o.tipo, p.id"); //13
        sb.append(" , g.nombre,  (o.FECHA - c.FECHA_VENCIMIENTO), o.fecha_entrega, p.repse, o.repse ");
        sb.append("  FROM AUTORIZACIONES_ORDEN a ");
        sb.append("     inner join usuario u on a.solicito = u.ID");
        sb.append("     inner join ORDEN o on a.ORDEN  = o.ID");
        sb.append("     inner join REQUISICION r on o.REQUISICION = r.ID");
        sb.append("     inner join ESTATUS e on a.ESTATUS = e.ID");
        sb.append("     inner join PROVEEDOR p on o.PROVEEDOR = p.ID");
        sb.append("     inner join MONEDA m on o.MONEDA = m.ID");
        sb.append("     inner join GERENCIA g on  o.GERENCIA = g.ID");
        sb.append("     LEFT join CONVENIO c on  o.CONTRATO = c.CODIGO");
        return sb.toString();
    }
//- - - - - Ordenes que se tienen que autorizar por la gerencia de Compras

    public List<OrdenVO> traerOCSPorContrato(int idContrato, int estado, int campo) {
        List<OrdenVO> lor = null;
        try {
            StringBuilder sb
                    = new StringBuilder("SELECT o.ID, o.CONSECUTIVO, o.REFERENCIA \n"
                            + ", r.consecutivo AS requisicion, a.FECHA_SOLICITO AS fecha \n"
                            + " , e.nombre AS estatus, p.nombre AS proveedor \n"
                            + ", o.CONTRATO as numero_contrato, o.TOTAL,  m.siglas AS moneda \n"
                            + ", o.supera_monto, o.iva,  o.subTotal AS sun_total \n"
                            + ", u.nombre AS analista, o.leida, o.navCode AS nav_code\n"
                            + ", a.ERROR_ENVIO, o.tipo, p.id AS id_proveedor \n" //13
                            + " , g.nombre AS gerencia,  (o.FECHA - c.FECHA_VENCIMIENTO) \n"
                            + "FROM orden o \n"
                            + "     inner join AUTORIZACIONES_ORDEN a on a.ORDEN  = o.ID \n"
                            + "     inner join usuario u on a.solicito = u.ID \n"
                            + "     inner join REQUISICION r on o.REQUISICION = r.ID \n"
                            + "     inner join ESTATUS e on a.ESTATUS = e.ID \n"
                            + "     inner join PROVEEDOR p on o.PROVEEDOR = p.ID \n"
                            + "     inner join MONEDA m on o.MONEDA = m.ID \n"
                            + "     inner join GERENCIA g on  o.GERENCIA = g.ID \n"
                            + "	inner join Convenio c on  o.CONTRATO = c.CODIGO \n"
                            + "WHERE a.ESTATUS  > ?1 \n" //).append(estado);//101
                            + "     AND c.id  = ?2 \n" //).append(idContrato);
                            + "     AND o.AP_CAMPO = ?3 \n" //).append(campo);
                            + "     AND o.eliminado = ?4 \n" //).append(Constantes.NO_ELIMINADO).append("'");
                            + "ORDER BY a.FECHA_AUTORIZO_IHSA ASC");

            List<Object[]> lo
                    = em.createNativeQuery(sb.toString())
                            .setParameter(1, estado)
                            .setParameter(2, idContrato)
                            .setParameter(3, campo)
                            .setParameter(4, Constantes.NO_ELIMINADO)
                            .getResultList();

            lor = new ArrayList<>();

            if (lo != null) {
                for (Object[] objects : lo) {
                    OrdenVO o = new OrdenVO();
                    o.setId((Integer) objects[0]);
                    o.setConsecutivo(String.valueOf(objects[1]));
                    o.setReferencia(String.valueOf(objects[2]));
                    o.setRequisicion((String) objects[3]);
                    o.setFecha((Date) objects[4]);
                    o.setEstatus((String) objects[5]);
                    o.setProveedor((String) objects[6]);
                    o.getContratoVO().setNumero(objects[7] == null ? Constantes.OCS_SIN_CONTRATO : (String) objects[7]);
                    o.setTotal((Double) objects[8]);
                    o.setMoneda((String) objects[9]);
                    o.setSuperaMonto((Boolean) objects[10]);
                    o.setIva((Double) objects[11]);
                    o.setSubTotal((Double) objects[12]);
                    o.setAnalista((String) objects[13]);
                    o.setLeida((Boolean) objects[14]);
                    o.setNavCode(objects[15] != null ? (String) objects[15] : "-");
                    o.setErrorEnvio(objects[16] != null ? (Boolean) objects[16] : Constantes.BOOLEAN_FALSE);
                    o.setTipo((String) objects[17]);
                    o.setIdProveedor((Integer) objects[18]);
                    o.setGerencia((String) objects[19]);
                    o.setSelected(false);
                    lor.add(o);
                }
            }
        } catch (Exception ex) {
            LOGGER.warn(this, "", ex);
            lor = new ArrayList<>();
        }
        return lor;
    }

    public List<OrdenVO> traerOCSPorContratoDet(int idContrato, int estado, int campo) {
        List<OrdenVO> lor = null;
        try {
            StringBuilder sb
                    = new StringBuilder("SELECT o.ID, o.CONSECUTIVO, o.REFERENCIA \n"
                            + ", r.consecutivo AS requisicionn, a.FECHA_SOLICITO AS fechaa \n"
                            + " , e.nombre AS estatuss, p.nombre AS proveedorr \n"
                            + ", o.CONTRATO as numero_contrato, o.TOTAL,  m.siglas AS monedaa \n"
                            + ", o.supera_monto, o.iva,  o.subTotal AS sun_total \n"
                            + ", u.nombre AS analistaa, o.leida, o.navCode AS nav_code\n"
                            + ", a.ERROR_ENVIO, o.tipo, p.id AS id_proveedor \n" //13
                            + " , g.nombre AS gerenciaa,  (o.FECHA - c.FECHA_VENCIMIENTO) \n"
                            + " FROM convenio c \n"
                            + "	inner join orden_detalle d on d.convenio_codigo = c.codigo \n"
                            + "	inner join orden o on o.id = d.orden and o.eliminado = false \n"
                            + "	inner join AUTORIZACIONES_ORDEN a on a.ORDEN  = o.ID  \n"
                            + "	inner join usuario u on a.solicito = u.ID  \n"
                            + "	inner join REQUISICION r on o.REQUISICION = r.ID  \n"
                            + "	inner join ESTATUS e on a.ESTATUS = e.ID  \n"
                            + "	inner join PROVEEDOR p on o.PROVEEDOR = p.ID  \n"
                            + "	inner join MONEDA m on o.MONEDA = m.ID  \n"
                            + "	inner join GERENCIA g on  o.GERENCIA = g.ID 	 \n"
                            + "WHERE a.ESTATUS  > ?1 \n" //).append(estado);//101
                            + "     AND c.id  = ?2 \n" //).append(idContrato);
                            + "     AND o.AP_CAMPO = ?3 \n" //).append(campo);
                            + "     AND o.eliminado = ?4 \n" //).append(Constantes.NO_ELIMINADO).append("'");
                            + "     group by o.ID, o.CONSECUTIVO, o.REFERENCIA "
                            + " , requisicionn,  fechaa  "
                            + " , estatuss, proveedorr  "
                            + " , numero_contrato, o.TOTAL,   monedaa "
                            + " , o.supera_monto, o.iva,  sun_total  "
                            + " , analistaa, o.leida, nav_code "
                            + " , a.ERROR_ENVIO, o.tipo, id_proveedor  "
                            + " ,  gerenciaa,  (o.FECHA - c.FECHA_VENCIMIENTO) ");

            List<Object[]> lo
                    = em.createNativeQuery(sb.toString())
                            .setParameter(1, estado)
                            .setParameter(2, idContrato)
                            .setParameter(3, campo)
                            .setParameter(4, Constantes.NO_ELIMINADO)
                            .getResultList();

            lor = new ArrayList<>();
            if (lo != null) {
                for (Object[] objects : lo) {
                    OrdenVO o = new OrdenVO();
                    o.setId((Integer) objects[0]);
                    o.setConsecutivo(String.valueOf(objects[1]));
                    o.setReferencia(String.valueOf(objects[2]));
                    o.setRequisicion((String) objects[3]);
                    o.setFecha((Date) objects[4]);
                    o.setEstatus((String) objects[5]);
                    o.setProveedor((String) objects[6]);
                    o.getContratoVO().setNumero(objects[7] == null ? Constantes.OCS_SIN_CONTRATO : (String) objects[7]);
                    o.setTotal((Double) objects[8]);
                    o.setMoneda((String) objects[9]);
                    o.setSuperaMonto((Boolean) objects[10]);
                    o.setIva((Double) objects[11]);
                    o.setSubTotal((Double) objects[12]);
                    o.setAnalista((String) objects[13]);
                    o.setLeida((Boolean) objects[14]);
                    o.setNavCode(objects[15] != null ? (String) objects[15] : "-");
                    o.setErrorEnvio(objects[16] != null ? (Boolean) objects[16] : Constantes.BOOLEAN_FALSE);
                    o.setTipo((String) objects[17]);
                    o.setIdProveedor((Integer) objects[18]);
                    o.setGerencia((String) objects[19]);
                    o.setSelected(false);
                    lor.add(o);
                }
            }
        } catch (Exception ex) {
            LOGGER.warn(this, "", ex);
            lor = new ArrayList<>();
        }
        return lor;
    }

    private void enviarNotificacion(String titulo, String mensaje, String usuarioDestino) {
        try {
            //
            List<SiUsuarioCodigo> lu = siUsuarioCodigoLocal.buscarPorUsuario(usuarioDestino);
            if (lu != null && !lu.isEmpty()) {
                for (SiUsuarioCodigo lu1 : lu) {
                    FCMSender.notificaciones(titulo, mensaje, lu1.getToken(), Constantes.ORDEN_TOKEN);

                }
            }
        } catch (Exception ex) {
            LOGGER.warn(this, "", ex);
        }
    }

    public double sumaToalOCSPorContrato(int idContrato, int campo) {
        try {

            StringBuilder sb = new StringBuilder();
            sb.append(" select sum(o.subtotal) from  autorizaciones_orden a ");
            sb.append("	    inner join orden o  on a.orden = o.id ");
            sb.append("	    inner join Convenio c on  o.CONTRATO = c.CODIGO ");
            sb.append("  WHERE a.ESTATUS  > ").append(Constantes.ORDENES_SIN_SOLICITAR);
            sb.append("     AND c.id  = ").append(idContrato);
            sb.append("     AND o.AP_CAMPO = ").append(campo);
            sb.append("     AND o.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");

            return (Double) em.createNativeQuery(sb.toString()).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<CampoVo> totalPorTodosCampos(String sesion) {
        List<CampoUsuarioPuestoVo> lista = apCampoUsuarioRhPuestoRemote.getAllPorUsurio(sesion);
        List<CampoVo> campo = new ArrayList<>();
        for (CampoUsuarioPuestoVo campoUsuarioVo : lista) {
            int t = total(sesion, campoUsuarioVo.getIdCampo());
            if (t > Constantes.CERO) {
                campo.add(new CampoVo(campoUsuarioVo.getIdCampo(), campoUsuarioVo.getCampo(), t));
            }

        }
        return campo;
    }

    public Orden buscarPorOrdenConsecutivo(String consecutivo, String usuario) {
        Orden r = null;
        try {
            r = buscarPorConsecutivo(consecutivo);
            boolean v = siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_COMPRA, Constantes.CODIGO_ROL_CONS_OCS, r.getApCampo().getId());
            StringBuilder sb = new StringBuilder();
            if (!v) {
                if (!((r.getAnalista() != null && r.getAnalista().getId().equals(usuario))
                        || (r.getAutorizacionesOrden().getSolicito() != null && r.getAutorizacionesOrden().getSolicito().getId().equals(usuario))
                        || (r.getAutorizacionesOrden().getAutorizaGerencia() != null && r.getAutorizacionesOrden().getAutorizaGerencia().getId().equals(usuario))
                        || (r.getAutorizacionesOrden().getAutorizaMpg() != null && r.getAutorizacionesOrden().getAutorizaMpg().getId().equals(usuario))
                        || (r.getAutorizacionesOrden().getAutorizaIhsa() != null && r.getAutorizacionesOrden().getAutorizaIhsa().getId().equals(usuario))
                        || (r.getAutorizacionesOrden().getAutorizaFinanzas() != null && r.getAutorizacionesOrden().getAutorizaFinanzas().getId().equals(usuario))
                        || (r.getAutorizacionesOrden().getAutorizaCompras() != null && r.getAutorizacionesOrden().getAutorizaCompras().getId().equals(usuario)))) {
                    r = null;
                }
            }
        } catch (Exception e) {
            r = null;
            LOGGER.fatal(this, "Error al buscar la orden por consecutivo: " + e.getMessage());
        }
        return r;
    }

    public List<OrdenVO> traerOrdenPorRequisicion(int idRequisicion) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select o.id, o.consecutivo, g.nombre, p.NOMBRE, r.CONSECUTIVO as consecutivo_requisicion, o.SUBTOTAL, o.TOTAL,");
            sb.append(" o.TOTAL_USD, e.NOMBRE");
            sb.append(" FROM AUTORIZACIONES_ORDEN ao");
            sb.append("     inner join ESTATUS e on ao.ESTATUS = e.ID");
            sb.append("     inner join ORDEN o  on ao.ORDEN = o.ID");
            sb.append("     inner join GERENCIA g on o.GERENCIA = g.ID");
            sb.append("     inner join PROVEEDOR p on o.PROVEEDOR = p.ID");
            sb.append("     inner join REQUISICION r on o.REQUISICION = r.ID");
            sb.append(" WHERE o.eliminado = false and o.requisicion = ").append(idRequisicion);
            sb.append(" and ao.ESTATUS > 100 ");
            sb.append(" Order by p.NOMBRE ");

            List<OrdenVO> le = new ArrayList<>();

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            for (Object[] objects : lo) {
                OrdenVO or = new OrdenVO();
                or.setId((Integer) objects[0]);
                or.setConsecutivo((String) objects[1]);
                or.setGerencia((String) objects[2]);
                or.setProveedor((String) objects[3]);
                or.setConsecutivoRequisicion((String) objects[4]);
                or.setSubTotal((Double) (objects[5] != null ? objects[5] : 0.0));
                or.setTotal((Double) (objects[6] != null ? objects[6] : 0.0));
                or.setTotalUsd((Double) (objects[7] != null ? objects[7] : 0.0));
                or.setEstatus((String) objects[8]);
                le.add(or);
            }
            return le;
        } catch (Exception e) {
            LOGGER.info(this, "Excepción al buscar las ordenes generadas de proyecto Ot" + e.getMessage());
            return null;
        }
    }

    public List<OrdenVO> traerTotalesPorCompania(String compania, int anio, int monedaId) {
        List<OrdenVO> lista = new ArrayList<>();
        try {
            String c = "SELECT a.codeproy, round(sum(o.total::numeric),2) from orden o\n"
                    + "     inner join autorizaciones_orden ao on ao.orden = o.id "
                    + "     inner join ap_campo a on o.ap_campo = a.id \n"
                    + " where o.compania = '" + compania + "'"
                    + " and extract(year from o.fecha) = " + anio
                    + " and o.eliminado = false "
                    + " and ao.estatus >= " + Constantes.ESTATUS_ENVIADA_PROVEEDOR
                    + " and o.moneda = " + monedaId
                    + " GROUP by a.codeproy \n"
                    + " order by a.codeproy";
            List<Object[]> lo = em.createNativeQuery(c).getResultList();
            //
            for (Object[] objects : lo) {
                OrdenVO o = new OrdenVO();
                o.setBloque((String) objects[0]);
                o.setTotal(((BigDecimal) objects[1]).doubleValue());
                //
                lista.add(o);
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return lista;
    }

    public List<Integer> traerAniosOrden(String compania) {
        String c = "SELECT DISTINCT extract(year from fecha)::int from orden "
                + " where consecutivo is not null\n"
                + "  and compania = '" + compania + "'"
                + " order by extract(year from fecha)::int desc ";
        return em.createNativeQuery(c).getResultList();
    }

    public List<OrdenVO> totalProveedores(String compania, int moneda, int anio) {
        List<OrdenVO> lista = new ArrayList<>();
        String cons = "SELECT p.nombre, round(sum(o.total)::numeric,2) as total from orden o \n"
                + "	inner join proveedor p on o.proveedor = p.id \n"
                + "	inner join autorizaciones_orden ao on ao.orden = o.id\n"
                + " where extract(year from o.fecha) = ? \n"
                + " and o.compania = ?  \n"
                + " and o.moneda = ? \n"
                + " and ao.estatus >= ? \n"
                + " and o.eliminado = false "
                + "GROUP by p.nombre\n"
                + "order by round(sum(o.total)::numeric,4) desc\n"
                + "limit 5";

        List<Object[]> lt = em.createNativeQuery(cons)
                .setParameter(1, anio)
                .setParameter(2, compania)
                .setParameter(3, moneda)
                .setParameter(4, OrdenEstadoEnum.POR_RECIBIR.getId())
                .getResultList();
        // out.println("Lo,. " + lo.size());
        for (Object[] objects : lt) {
            OrdenVO o = new OrdenVO();
            o.setProveedor((String) objects[0]);
            o.setTotal(((BigDecimal) objects[1]).doubleValue());
            //
            lista.add(o);
        }
        return lista;
    }

    public List<CompaniaVo> empresasPorProveedor(int idproveedor, int statusIncial, int statusFinal) {
        StringBuilder c = new StringBuilder();
        c.append(" SELECT o.compania, c.nombre from orden o \n")
                .append("	inner join compania c on o.compania = c.rfc ")
                .append("     inner join autorizaciones_orden ao on ao.orden = o.id \n")
                .append(" where o.proveedor = ? ");
        if (statusIncial > 0 && statusFinal > 0) {
            c.append(" and ao.estatus between ").append(statusIncial).append(" and ").append(statusFinal);
        }
        c.append(" and o.eliminado = false  GROUP by o.compania, c.nombre \n")
                .append(" union \n")
                .append(" SELECT o.compania, c.nombre from si_factura f ")
                .append("       inner join oc_factura_status fe on fe.si_factura = f.id ")
                .append("       inner join orden o on f.orden = o.id ")
                .append("       inner join compania c on o.compania = c.rfc  ")
                .append(" where fe.estatus = ").append(FacturaEstadoEnum.CREADA.getId()).append(" and fe.actual = true ")
                .append(" and o.proveedor = ").append(idproveedor)
                .append(" and f.id in (SELECT si_factura from si_factura_movimiento where eliminado = false) ")
                .append(" group by o.compania, c.nombre ");
        List<Object[]> objs = em.createNativeQuery(c.toString()).setParameter(1, idproveedor).getResultList();
        List<CompaniaVo> compa = new ArrayList<>();
        for (Object[] obj : objs) {
            CompaniaVo comp = new CompaniaVo();
            comp.setRfcCompania((String) obj[0]);
            comp.setNombre((String) obj[1]);
            compa.add(comp);
        }
        return compa;
    }

    public Orden ordenPorEmpresaEstatus(String consecutivo, String rfcEmpresa, int statusInicial, int statatusFinal) {
        Orden r = null;

        try {
            String sql
                    = "select o.* from orden o "
                    + "     inner join autorizaciones_orden ao on ao.orden = o.id "
                    + " where o.consecutivo = ? and o.compania = ? "
                    + " and ao.estatus between ? and ? ";

            r
                    = (Orden) em.createNativeQuery(sql, "orden_map")
                            .setParameter(1, consecutivo)
                            .setParameter(2, rfcEmpresa)
                            .setParameter(3, statusInicial)
                            .setParameter(4, statatusFinal)
                            .getSingleResult();

        } catch (Exception e) {
            LOGGER.info(this, "Error al buscar la orden por consecutivo: " + consecutivo, e);
        }

        return r;
    }

    public boolean validarConvenio(int idOrden, String codigoConvenio) {
        boolean continuar = false;
        try {
            String sb = " select "
                    + " coalesce( (select sum(monto) from (  "
                    + " select monto::numeric from convenio where convenio = c.id  "
                    + " union all select monto::numeric from convenio where id = c.id  "
                    + " union all select monto::numeric from convenio where id = c.convenio  "
                    + " union all select monto::numeric from convenio where convenio = c.convenio and id <> c.id  "
                    + " ) as montosConvenio ),0 ) as montoLimite,  "
                    + " coalesce "
                    + " ( "
                    + " (select sum(subtotal) from (  "
                    + " select o.subtotal::numeric from orden o where o.id =  " + idOrden
                    + " union all select o.subtotal::numeric from convenio cc inner join orden o on o.contrato = cc.codigo and o.eliminado = false and o.moneda = cc.moneda inner join autorizaciones_orden ao on ao.orden = o.id and ao.eliminado = false and ao.estatus > 101 where cc.convenio = c.id  "
                    + " union all select o.subtotal::numeric from convenio cc inner join orden o on o.contrato = cc.codigo and o.eliminado = false and o.moneda = cc.moneda inner join autorizaciones_orden ao on ao.orden = o.id and ao.eliminado = false and ao.estatus > 101 where cc.id = c.id  "
                    + " union all select o.subtotal::numeric from convenio cc inner join orden o on o.contrato = cc.codigo and o.eliminado = false and o.moneda = cc.moneda inner join autorizaciones_orden ao on ao.orden = o.id and ao.eliminado = false and ao.estatus > 101 where cc.id = c.convenio  "
                    + " union all select o.subtotal::numeric from convenio cc inner join orden o on o.contrato = cc.codigo and o.eliminado = false and o.moneda = cc.moneda inner join autorizaciones_orden ao on ao.orden = o.id and ao.eliminado = false and ao.estatus > 101 where cc.convenio = c.convenio and cc.id <> c.id     "
                    + " union all select o.subtotal_usd::numeric from convenio cc inner join orden o on o.contrato = cc.codigo and o.eliminado = false and o.moneda <> cc.moneda inner join autorizaciones_orden ao on ao.orden = o.id and ao.eliminado = false and ao.estatus > 101 where cc.id = c.id  "
                    + " union all select o.subtotal_usd::numeric from convenio cc inner join orden o on o.contrato = cc.codigo and o.eliminado = false and o.moneda <> cc.moneda inner join autorizaciones_orden ao on ao.orden = o.id and ao.eliminado = false and ao.estatus > 101 where cc.convenio = c.id  "
                    + " union all select o.subtotal_usd::numeric from convenio cc inner join orden o on o.contrato = cc.codigo and o.eliminado = false and o.moneda <> cc.moneda inner join autorizaciones_orden ao on ao.orden = o.id and ao.eliminado = false and ao.estatus > 101 where cc.id = c.convenio "
                    + " union all select o.subtotal_usd::numeric from convenio cc inner join orden o on o.contrato = cc.codigo and o.eliminado = false and o.moneda <> cc.moneda inner join autorizaciones_orden ao on ao.orden = o.id and ao.eliminado = false and ao.estatus > 101 where cc.convenio = c.convenio and cc.id <> c.id     "
                    + " ) as totalComprasConvenio), "
                    + " 0 "
                    + " ) "
                    + " as montoToalOrdenes "
                    + " from convenio c "
                    + " where c.codigo = '" + codigoConvenio + "' ";

            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();

            if (((BigDecimal) obj[0]).compareTo(((BigDecimal) obj[1])) > 0) {
                continuar = true;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            continuar = false;
        }
        return continuar;
    }

    public void notificarValidarPresupuesto(Orden orden, String partidas) {
        String correos = siUsuarioRolRemote.correosListaDestinatarios(orden.getApCampo().getId(), "PresupuestoSinMonto");
        if (!correos.isEmpty()) {
            correos += ",";
        }
        correos += apCampoGerenciaRemote.correoGerencia(
                orden.getApCampo().getId(),
                "" + orden.getGerencia().getId()
                + "," + Constantes.GERENCIA_ID_DIRECCION_GENERAL
                + "," + Constantes.GERENCIA_ID_DIRECCION_FINANZAS
                + "," + Constantes.GERENCIA_ID_DIRECCION_TECNICA);
        notificacionesOrdenRemote.enviarNotificacionValidarPresupuesto(correos, partidas, orden.getApCampo().getNombre());
    }

    public void notificarValidarContrato(Orden orden, String contrato) {
        String correos = siUsuarioRolRemote.correosListaDestinatarios(orden.getApCampo().getId(), "ConvenioSinMonto");
        if (!correos.isEmpty()) {
            correos += ",";
        }
        correos += apCampoGerenciaRemote.correoGerencia(
                orden.getApCampo().getId(),
                "" + Constantes.GERENCIA_ID_COMPRAS);
        notificacionesOrdenRemote.enviarNotificacionValidarContrato(correos, contrato, orden.getApCampo().getNombre());
    }

    public List<OrdenVO> totalCompraProceso(int campo, int statusIncial, int statusFinal, String proveedor) {
        String cad = "SELECT nombre,  total, siglas from (\n"
                + "	SELECT pot.nombre,  m.siglas,	  round(sum(o.total::numeric),2) as total from orden o\n"
                + "     	inner join autorizaciones_orden ao on ao.orden = o.id \n"
                + "     	inner join proyecto_ot pot on o.proyecto_ot = pot.id     \n"
                + "     	inner join moneda m on o.moneda = m.id\n"
                + "             inner join proveedor p on o.proveedor = p.id "
                + " 	where o.ap_campo =   " + campo
                + " 	and o.eliminado = false \n"
                + " 	and ao.estatus BETWEEN " + statusIncial + " and " + statusFinal;

        if (!proveedor.isEmpty()) {
            cad += " and p.nombre = '" + proveedor + "'";
        }
        cad += " 	GROUP by rollup (pot.nombre), m.siglas\n"
                + " 	) as compras order by siglas asc";
        //

        List<Object[]> lo = em.createNativeQuery(cad).getResultList();
        List<OrdenVO> ordens = new ArrayList<>();
        for (Object[] objects : lo) {
            OrdenVO ordenVO = new OrdenVO();
            ordenVO.setProyectoOt((String) objects[0]);
            ordenVO.setTotalProyectoOt(((BigDecimal) objects[1]).doubleValue());
            ordenVO.setMonedaSiglas((String) objects[2]);
            if (objects[0] == null) {
                ordenVO.setTotal(((BigDecimal) objects[1]).doubleValue());
            }
            ordens.add(ordenVO);
        }
        return ordens;
    }

    public List<OrdenVO> totalCompras(int campo, int statusInicial, int statusFinal, String proveedor) {
        String cad = " SELECT m.nombre, round(sum(o.total::numeric),2) as total from orden o\n"
                + "     	inner join autorizaciones_orden ao on ao.orden = o.id    \n"
                + "     	inner join moneda m on o.moneda = m.id\n"
                + "             inner join proveedor p on o.proveedor = p.id "
                + " where o.ap_campo =   " + campo
                + " and o.eliminado = false \n"
                + " and ao.estatus BETWEEN " + statusInicial + " and " + statusFinal;

        if (!proveedor.isEmpty()) {
            cad += " and p.nombre = '" + proveedor + "'";
        }
        cad += "GROUP by  m.nombre";

        List<Object[]> lo = em.createNativeQuery(cad).getResultList();
        List<OrdenVO> ordens = new ArrayList<>();
        for (Object[] objects : lo) {
            OrdenVO ordenVO = new OrdenVO();
            ordenVO.setMoneda((String) objects[0]);
            ordenVO.setTotal(((BigDecimal) objects[1]).doubleValue());
            //
            ordens.add(ordenVO);
        }
        return ordens;
    }

    public List<OrdenVO> comprasPorProyecto(String proyecto, int campo, int statusInicial, int statusFinal, String proveedor) {
        String c = "SELECT pot.nombre, pot.cuenta_contable, r.consecutivo, o.consecutivo,  o.navcode,  p.nombre, o.fecha, o.referencia, o.destino, round(o.subtotal::numeric,2) as subtotal, \n"
                + "		round(o.iva::numeric,2) as iva, round(o.total::numeric,2) as total, m.siglas, es.nombre \n"
                + "		from orden o\n"
                + "	inner join autorizaciones_orden ao on ao.orden = o.id\n"
                + "	inner join proyecto_ot pot on o.proyecto_ot = pot.id\n"
                + "	inner join moneda m on o.moneda = m.id\n"
                + "	inner join estatus es on ao.estatus = es.id\n"
                + "	inner join requisicion r on o.requisicion = r.id\n"
                + "	inner join proveedor p on o.proveedor =p.id\n"
                + " where o.ap_campo =   " + campo
                + " and o.eliminado = false \n"
                + " and ao.estatus BETWEEN " + statusInicial + " and " + statusFinal;

        if (!proveedor.isEmpty()) {
            c += " and p.nombre = '" + proveedor + "'";
        }
        c += " 	and pot.nombre = '" + proyecto + "'";
        //

        List<Object[]> lo = em.createNativeQuery(c).getResultList();
        List<OrdenVO> ordens = new ArrayList<>();
        for (Object[] objects : lo) {
            ordens.add(castReporteContabilidad(objects));
        }
        return ordens;
    }

    public List<OrdenVO> comprasPorProveedor(int campo, int statusInicial, int statusFinal, String proveedor) {
        String c = "SELECT pot.nombre, pot.cuenta_contable, r.consecutivo, o.consecutivo,  o.navcode,  p.nombre, o.fecha, o.referencia, o.destino, round(o.subtotal::numeric,2) as subtotal, \n"
                + "		round(o.iva::numeric,2) as iva, round(o.total::numeric,2) as total, m.siglas, es.nombre \n"
                + "		from orden o\n"
                + "	inner join autorizaciones_orden ao on ao.orden = o.id\n"
                + "	inner join proyecto_ot pot on o.proyecto_ot = pot.id\n"
                + "	inner join moneda m on o.moneda = m.id\n"
                + "	inner join estatus es on ao.estatus = es.id\n"
                + "	inner join requisicion r on o.requisicion = r.id\n"
                + "	inner join proveedor p on o.proveedor =p.id\n"
                + " where o.ap_campo =   " + campo
                + " and o.eliminado = false \n"
                + " and ao.estatus BETWEEN " + statusInicial + " and " + statusFinal;

        if (!proveedor.isEmpty()) {
            c += " and p.nombre = '" + proveedor + "'";
        }
        List<Object[]> lo = em.createNativeQuery(c).getResultList();
        List<OrdenVO> ordens = new ArrayList<>();
        for (Object[] objects : lo) {
            ordens.add(castReporteContabilidad(objects));
        }
        return ordens;
    }

    private OrdenVO castReporteContabilidad(Object[] objects) {
        OrdenVO ordenVO = new OrdenVO();
        ordenVO.setProyectoOt((String) objects[0]);
        ordenVO.setCuentaContable((String) objects[1]);
        ordenVO.setRequisicion((String) objects[2]);
        ordenVO.setConsecutivo((String) objects[3]);
        ordenVO.setNavCode((String) objects[4]);
        ordenVO.setProveedor((String) objects[5]);
        ordenVO.setFecha((Date) objects[6]);
        ordenVO.setReferencia((String) objects[7]);
        ordenVO.setDestino((String) objects[8]);

        ordenVO.setSubTotal(((BigDecimal) objects[9]).doubleValue());
        ordenVO.setIva(((BigDecimal) objects[10]).doubleValue());
        ordenVO.setTotal(((BigDecimal) objects[11]).doubleValue());
        ordenVO.setMonedaSiglas((String) objects[12]);
        ordenVO.setEstatus((String) objects[13]);
        return ordenVO;
    }

    public OrdenVO buscarOrdenPorConsecutivo(String consecutivo, boolean condetalle) {
        StringBuilder sb = new StringBuilder();
        OrdenVO ovo = null;
        try {
            sb.append(buscarOrden());
            sb.append(" where o.consecutivo = '").append(consecutivo).append("'");
            //
            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (obj != null) {
                ovo = castOrdenTotal(obj, condetalle);
            }
        } catch (Exception e) {
            LOGGER.fatal(this, "Error al recuperar la Compra por consecutivo " + consecutivo + " # # # # # # " + e.getMessage());
        }
        return ovo;
    }

    public OrdenVO buscarOrdenPorUuId(String uuId, boolean condetalle) {
        StringBuilder sb = new StringBuilder();
        OrdenVO ovo = null;
        try {
            sb.append(buscarOrden());
            sb.append(" where o.uuid = '").append(uuId).append("'");
            //
            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (obj != null) {
                ovo = castOrdenTotal(obj, condetalle);
            }
        } catch (Exception e) {
            LOGGER.fatal(this, "Error al recuperar la Compra por uuId " + uuId + " # # # # # # " + e.getMessage());
        }
        return ovo;
    }

    public void aceptarCartaIntencion(int ordenId, List<OrdenDetalleVO> items, UsuarioVO usuarioSesion) {
        AutorizacionesOrden au = autorizacionesOrdenRemote.buscarPorOrden(ordenId);
        au.setEstatus(estatusServicioImpl.find(OrdenEstadoEnum.POR_REVISAR_REPSE.getId())); // 148 para revision de Jridico
        // Envia la        
        notificacionesOrdenRemote.enviarCorreoAceptarCartaIntencion(au.getOrden(), items, "Carta de intención de compra - Aceptada");
        //
        au.setFechaAceptacionCarta(new Date());
        au.setHoraAceptacionCarta(new Date());
        au.setModifico(new Usuario(usuarioSesion.getId()));
        au.setFechaModifico(new Date());
        au.setHoraModifico(new Date());
        autorizacionesOrdenRemote.editar(au);
    }

    public void rechazarCartaIntencion(int ordenId, List<OrdenDetalleVO> items, String motivo, UsuarioVO usuarioSesion) {
        try {
            AutorizacionesOrden au = autorizacionesOrdenRemote.buscarPorOrden(ordenId);
            au.setEstatus(estatusServicioImpl.find(OrdenEstadoEnum.POR_SOLICITAR.getId())); // 148 para revision de Jridico
            // Envia la        
            if (items == null) {
                items = itemsPorOrdenCompra(ordenId);
            }
            notificacionesOrdenRemote.enviarMensajeRechazoCartaIntencion(au.getOrden(), items, "Carta de Intención de Compra - Rechazada", motivo);
            //
            au.setModifico(new Usuario(usuarioSesion.getId()));
            au.setFechaModifico(new Date());
            au.setHoraModifico(new Date());
            autorizacionesOrdenRemote.editar(au);
            //
            //Asigno orden fecha hora y usuario de rechazo
            ordenSiMovimientoRemote.guardarMovimiento(usuarioSesion.getId(),
                    ordenId,
                    motivo,
                    usuarioSesion.getId(), Constantes.ID_OPERACION_REC_CARTA_INTENCION);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    public void rechazarRepse(int ordenId, List<OrdenDetalleVO> items, String correoSesion, String motivo, String sesion) {
        AutorizacionesOrden au = autorizacionesOrdenRemote.buscarPorOrden(ordenId);
        au.setEstatus(estatusServicioImpl.find(OrdenEstadoEnum.POR_ACEPTAR_CARTA_INTENCION.getId())); // 150 para revision de Jridico
        //Notifica al proveedor
        List<ContactoOrdenVo> contactos = getContactosVo(ordenId);
        if (items == null) {
            items = itemsPorOrdenCompra(ordenId);
        }
        //        
        notificacionesOrdenRemote.rechazarRepse(au.getOrden(), items, getDestinatariosOrden(contactos), correoSesion, motivo);
        //
        au.setModifico(new Usuario(sesion));
        au.setFechaModifico(new Date());
        au.setHoraModifico(new Date());
        autorizacionesOrdenRemote.editar(au);
        //
        Orden o = au.getOrden();
        //o.setRepse(Boolean.TRUE);
        o.setModifico(new Usuario(sesion));
        o.setFechaModifico(new Date());
        o.setHoraModifico(new Date());
        edit(o);
        //
        ordenSiMovimientoRemote.guardarMovimiento(sesion, ordenId, motivo, sesion, Constantes.ID_SI_RECHAZAR_REPSE);
    }

    public void aceptarRepse(int ordenId, List<OrdenDetalleVO> items, String usuarioSesion, boolean proveedorConCartaIntencion) {
        AutorizacionesOrden au = autorizacionesOrdenRemote.buscarPorOrden(ordenId);
        au.setEstatus(estatusServicioImpl.find(OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId())); // 150 para revision de Jridico
        Orden orden = au.getOrden();
        // Envia la
        //
        int dias = siManejoFechaLocal.dias(new Date(), orden.getFecha());
        if (orden.getFechaEntrega().compareTo(new Date()) < 1) {
            int diasEntregaHoy = siManejoFechaLocal.dias(orden.getFechaEntrega(), orden.getFecha());
            orden.setFechaEntrega(siManejoFechaLocal.fechaSumarDias(new Date(), diasEntregaHoy));
        } else {
            orden.setFechaEntrega(siManejoFechaLocal.fechaSumarDias(orden.getFechaEntrega(), dias));
        }
        orden.setModifico(new Usuario(usuarioSesion));
        orden.setFechaModifico(new Date());
        orden.setHoraModifico(new Date());
        edit(orden);
        //-- Finalizar las notas que tenga la orden de compra
        List<ContactoOrdenVo> contactos = getContactosVo(ordenId);
        // Recupera los items
        if (items == null) {
            items = itemsPorOrdenCompra(ordenId);
        }
        notificacionesOrdenRemote.enviarNotificacionTarea(orden, contactos, items);
        if (TipoRequisicion.AF.equals(orden.getTipo())) {
            notificacionesOrdenRemote.enviarNotificacionCotabilidad(orden, contactos, items);
        }
        //
        if (proveedorConCartaIntencion) {
            au.setUsuarioRevisaJuridicoUsuario(new Usuario(usuarioSesion));
            au.setFechaRevisaRepse(new Date());
            au.setHoraRevisaRepse(new Date());
        }
        au.setModifico(new Usuario(usuarioSesion));
        au.setFechaModifico(new Date());
        au.setHoraModifico(new Date());
        autorizacionesOrdenRemote.editar(au);
        //
    }

    public List<OrdenTiemposVO> ordenTiempos(int idCampo, String consecutivo, int status, int gerencia, String fecha1, String fecha2, String proveedor) {
        List<OrdenTiemposVO> lr = null;

        try {
            String q = " select  "
                    + " oo.id, "
                    + " oo.consecutivo, "
                    + " oo.referencia, "
                    + " g.nombre, "
                    + " oo.tipo, "
                    + " us.nombre, (o.fecha_solicito), "
                    + " uv.nombre, (o.fecha_autorizo_gerencia), (coalesce((o.fecha_autorizo_gerencia), current_date) - (coalesce((o.fecha_solicito),current_date))), "
                    + " ur.nombre, (o.fecha_autorizo_mpg), coalesce((o.fecha_autorizo_mpg), current_date) - (coalesce((o.fecha_autorizo_gerencia),current_date)), "
                    + " ua.nombre, (o.fecha_autorizo_ihsa), coalesce((o.fecha_autorizo_ihsa), current_date) - (coalesce((o.fecha_autorizo_mpg),current_date)), "
                    + " uaa.nombre, (o.fecha_autorizo_compras), coalesce((o.fecha_autorizo_compras), current_date) - (coalesce((o.fecha_autorizo_ihsa),current_date)), "
                    + " pc.nombre, (o.fecha_aceptacion_carta), coalesce((o.fecha_aceptacion_carta), current_date) - (coalesce((o.fecha_autorizo_compras),current_date)), "
                    + " uj.nombre, (o.fecha_revisa_repse), coalesce((o.fecha_revisa_repse), current_date) - (coalesce((o.fecha_aceptacion_carta),current_date)), "
                    + " ue.nombre, (o.fecha_envio_proveedor), "
                    + " coalesce(o.fecha_envio_proveedor,current_date) - (coalesce((case when (select count(id) > 0 from pv_proveedor_sin_carta_intencion where proveedor = pc.id) then o.fecha_autorizo_compras else o.fecha_revisa_repse end),o.fecha_autorizo_compras,current_date)), "
                    + " (select count(id) > 0 from pv_proveedor_sin_carta_intencion where proveedor = pc.id), "
                    + " case when ((select count(id) > 0 from pv_proveedor_sin_carta_intencion where proveedor = pc.id)) then (o.fecha_envio_proveedor) - (coalesce((o.fecha_autorizo_compras),current_date)) else 0.0 end, "
                    + " ((coalesce((select fecha_recepcion from orden_detalle where orden = oo.id and fecha_recepcion is not null order by fecha_recepcion desc limit 1), current_date)) - (o.fecha_envio_proveedor)), "
                    + " oo.fecha_entrega,  "
                    + " coalesce((select fecha_recepcion from orden_detalle where orden = oo.id and fecha_recepcion is not null order by fecha_recepcion desc limit 1), current_date),  "
                    + " ((oo.fecha_entrega) - (coalesce((select fecha_recepcion from orden_detalle where orden = oo.id and fecha_recepcion is not null order by fecha_recepcion desc limit 1), current_date))),  "
                    + " o.estatus, "
                    + " r.consecutivo, "
                    + " uf.nombre, (o.fecha_autorizo_finanzas), coalesce((o.fecha_autorizo_finanzas),current_date) - (coalesce((o.fecha_autorizo_mpg),current_date)) "
                    + " from autorizaciones_orden o "
                    + " inner join orden oo on oo.id = o.orden "
                    + " inner join requisicion r on r.id = oo.requisicion "
                    + " inner join gerencia g on g.id = oo.gerencia "
                    + " inner join proveedor pc on pc.id = oo.proveedor "
                    + " left join usuario us on us.id = o.solicito "
                    + " left join usuario uv on uv.id = o.autoriza_gerencia "
                    + " left join usuario ur on ur.id = o.autoriza_mpg "
                    + " left join usuario ua on ua.id = o.autoriza_ihsa "
                    + " left join usuario uaa on uaa.id = o.autoriza_compras "
                    + " left join usuario uj on uj.id = o.usuario_revisa_juridico "
                    + " left join usuario ue on ue.id = o.enviarpdf "
                    + " left join usuario uf on uf.id = o.autoriza_finanzas "
                    + " where oo.eliminado = false "
                    + " and o.estatus > 101 ";

            if (idCampo > 0) {
                q += " and oo.ap_campo = " + idCampo;
            }

            if (proveedor != null && !proveedor.isEmpty()) {
                q += " and (upper(pc.nombre) like upper('%" + proveedor + "%') ";
                q += "     or upper(pc.rfc) like upper('%" + proveedor + "%') )";
            }

            if (consecutivo != null && !consecutivo.isEmpty()) {
                q += " and (oo.consecutivo = '" + consecutivo.trim() + "' ";
                q += "     or r.consecutivo = '" + consecutivo.trim() + "' )";
            }

            if (status > 0) {
                q += " and o.estatus = " + status;
            }

            if (gerencia > 0) {
                q += " and o.gerencia = " + gerencia;
            }

            if (fecha1 != null && fecha2 != null && !fecha1.isEmpty() && !fecha2.isEmpty()) {
                q += " and o.fecha_solicito >= '" + fecha1 + "' and o.fecha_solicito <= '" + fecha2 + "' ";
            }

            q += " ORDER BY o.ID DESC";

            OrdenTiemposVO o;

            List<Object[]> l = em.createNativeQuery(q).getResultList();
            if (!l.isEmpty()) {
                lr = new ArrayList<>();
                for (Object[] objects : l) {
                    o = new OrdenTiemposVO();
                    o.setIdOrden((Integer) objects[0]);
                    o.setConsecutivo(String.valueOf(objects[1]));
                    o.setReferencia(String.valueOf(objects[2]));
                    o.setGerencia(String.valueOf(objects[3]));
                    o.setTipo(String.valueOf(objects[4]));

                    o.setNombreSolicita(String.valueOf(objects[5]));
                    o.setFechaSolicita((Date) objects[6]);

                    o.setNombreVistoBueno(objects[7] != null ? String.valueOf(objects[7]) : "");
                    o.setFechaVistoBueno((Date) objects[8]);
                    o.setDiasVistoBueno(objects[9] != null ? (((Integer) objects[9])) : 0);

                    o.setNombreRevisa(objects[10] != null ? String.valueOf(objects[10]) : "");
                    o.setFechaRevisa((Date) objects[11]);
                    o.setDiasRevisa(objects[12] != null ? (((Integer) objects[12])) : 0);

                    o.setNombreAprueba(objects[13] != null ? String.valueOf(objects[13]) : "");
                    o.setFechaAprueba((Date) objects[14]);
                    o.setDiasAprueba(objects[15] != null ? (((Integer) objects[15])) : 0);

                    o.setNombreAutoriza(objects[16] != null ? String.valueOf(objects[16]) : "");
                    o.setFechaAutoriza((Date) objects[17]);
                    o.setDiasAutoriza(objects[18] != null ? (((Integer) objects[18])) : 0);

                    o.setNombreCarta(objects[19] != null ? String.valueOf(objects[19]) : "");
                    o.setFechaCarta((Date) objects[20]);
                    o.setDiasCarta(objects[21] != null ? (((Integer) objects[21])) : 0);

                    o.setNombreJuridico(objects[22] != null ? String.valueOf(objects[22]) : "");
                    o.setFechaJuridico((Date) objects[23]);
                    o.setDiasJuridico(objects[24] != null ? (((Integer) objects[24])) : 0);

                    o.setNombreEnvia(objects[25] != null ? String.valueOf(objects[25]) : "");
                    o.setFechaEnvia((Date) objects[26]);
                    o.setDiasEnvia(objects[27] != null ? (((Integer) objects[27])) : 0);

                    o.setSinCarta((Boolean) objects[28]);

                    if (o.isSinCarta()) {
                        o.setDiasEnvia(objects[29] != null ? (((BigDecimal) objects[29]).intValue()) : 0);
                    }
                    o.setDiasEnvidoRecepcion(objects[30] != null ? ((Integer) objects[30]) : 0);
                    o.setFechaEntrega((Date) objects[31]);
                    o.setFechaRecepcion((Date) objects[32]);
                    o.setDiasEntregaRecepcion(objects[33] != null ? (Integer) objects[33] : 0);
                    o.setEstatus((Integer) objects[34]);

                    o.setConsecutivoRequi(String.valueOf(objects[35]));

                    o.setNombreSocio(objects[36] != null ? String.valueOf(objects[36]) : "");
                    o.setFechaSocio((Date) objects[37]);
                    o.setDiasSocio(objects[38] != null ? (((Integer) objects[38])) : 0);

                    lr.add(o);
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            lr = null;
        }

        return lr;
    }

}
