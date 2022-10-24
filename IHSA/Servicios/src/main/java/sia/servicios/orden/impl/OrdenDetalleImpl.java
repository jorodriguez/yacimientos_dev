/*
 * OrdenDetalleFacade.java
 * Creada el 13/10/2009, 06:06:31 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.orden.impl;

import com.newrelic.api.agent.Trace;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jooq.DSLContext;
import sia.constantes.Constantes;
import sia.constantes.TipoRequisicion;
import sia.inventarios.service.ArticuloImpl;
import sia.inventarios.service.ArticuloRemote;
import sia.modelo.OcActividadPetrolera;
import sia.modelo.OcSubTarea;
import sia.modelo.OcUnidadCosto;
import sia.modelo.Orden;
import sia.modelo.OrdenDetalle;
import sia.modelo.ProyectoOt;
import sia.modelo.RequisicionDetalle;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.ConvenioArticuloVo;
import sia.modelo.requisicion.vo.RequisicionDetalleVO;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.convenio.impl.CvConvenioArticuloImpl;
import sia.servicios.requisicion.impl.OcTareaImpl;
import sia.servicios.requisicion.impl.RequisicionDetalleImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 13/10/2009
 */
@Stateless 
public class OrdenDetalleImpl extends AbstractFacade<OrdenDetalle>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    DSLContext dslCtx;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrdenDetalleImpl() {
        super(OrdenDetalle.class);
    }

    //
    @Inject
    private RequisicionDetalleImpl requisicionDetalleRemote;
    @Inject
    private OrdenImpl ordenRemote;
    @Inject
    private OcTareaImpl ocTareaRemote;
    @Inject
    private ArticuloRemote articuloRemote;
    @Inject
    private CvConvenioArticuloImpl cvConvenioArticuloLocal;

    
    public void crear(OrdenDetalle ordenDetalle) {
        create(ordenDetalle);
    }

    
    public void editar(OrdenDetalle ordenDetalle) {
        edit(ordenDetalle);
    }

    
    public OrdenDetalle findLazy(int id) {
        return (OrdenDetalle) em.createQuery("SELECT o FROM OrdenDetalle o WHERE o.id = :id ", OrdenDetalle.class).setParameter("id", id).getSingleResult();
    }

    
    public List<OrdenDetalle> getItemsPorOrden(Object idOrden) {
        return em.createQuery("SELECT o FROM OrdenDetalle o WHERE o.orden.id = :orden ORDER BY o.id ASC").setParameter("orden", idOrden).getResultList();
    }

    
    public List<OrdenDetalle> getItemsPorOrden(Object idOrden, int agrupadorID) {
        return em.createQuery("SELECT o FROM OrdenDetalle o WHERE o.orden.id = :orden and o.multiproyectoId = :multiproyectoID ORDER BY o.id ASC")
                .setParameter("orden", idOrden)
                .setParameter("multiproyectoID", agrupadorID).getResultList();
    }

    
    @Trace
    public List<OrdenDetalleVO> itemsPorOrden(int idOrden) {
        StringBuilder sb = new StringBuilder();
        List<OrdenDetalleVO> lrd = null;
        try {
            sb.append(consulta())
                    .append(" WHERE r.id =  ").append(idOrden)
                    .append(" ORDER BY o.id ASC ");
            //
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null && lo.size() > 0) {
                lrd = new ArrayList<>();
                for (Object[] objects : lo) {
                    OrdenDetalleVO o = new OrdenDetalleVO();
                    o.setId((Integer) objects[0]);
                    o.setEnCatalogo(objects[1] != null ? (Boolean) objects[1] : Constantes.BOOLEAN_FALSE);
                    o.setArtNumeroParte(String.valueOf(objects[2]));
                    o.setCantidad(objects[3] != null ? (Double) objects[3] : Constantes.CERO);
                    if (objects[12] == null) {
                        o.setArtUnidad((String) objects[4]);
                        o.setArtIdUnidad(0);
                    } else {
                        o.setArtIdUnidad((Integer) objects[12]);
                        o.setArtUnidad((String) objects[13]);
                    }
                    o.setArtDescripcion((String) objects[5]);
                    o.setPrecioUnitario((Double) objects[6] != null ? (Double) objects[6] : 0.0);
                    o.setImporte((Double) objects[7] != null ? (Double) objects[7] : 0.0);
                    //
                    o.setOcTarea(objects[9] != null ? (Integer) objects[9] : 0);
                    o.setCodeTarea((String) objects[10]);
                    o.setNombreTarea((String) objects[11]);
                    //
                    o.setOcUnidadCosto((Integer) objects[15]);
                    o.setOcProductoID((Integer) objects[16]);
                    o.setOcProductoDesc((String) objects[17]);
                    o.setOcProductoCode((String) objects[18]);
                    o.setArtNombre((String) objects[19]);
                    o.setArtID((Integer) objects[20] != null ? (Integer) objects[20] : 0);
                    
                    if(o.getArtID() == 0){
                        o.setArtNumeroParte((String) objects[49]);
                        
                        if(o.getArtUnidad() == null || o.getArtUnidad().isEmpty()){
                            o.setArtUnidad((String) objects[50]);
                        }
                    }
                    
                    o.setSelected(true);
                    o.setObservaciones((String) objects[21]);
                    o.setTextNav(objects[22] != null ? (String) objects[22] : Constantes.VACIO);
                    o.setFechaRecibido(objects[23] != null ? (Date) objects[23] : new Date());
                    o.setTotalRecibido(objects[24] != null ? (Double) objects[24] : Constantes.CERO);
                    o.setRecibido((Boolean) objects[25]);
                    o.setTotalPendiente(o.getCantidad() - o.getTotalRecibido());
                    o.setIdProyectoOt((Integer) objects[26] != null ? (Integer) objects[26] : 0);
                    o.setProyectoOt((String) objects[27]);
                    o.setIdSubTarea((Integer) objects[28] != null ? (Integer) objects[28] : 0);
                    o.setSubTarea((String) objects[29]);
                    o.setIdActividadPetrolera((Integer) objects[30] != null ? (Integer) objects[30] : 0);
                    o.setActividadPetrolera((String) objects[31]);
                    o.setIdTipoTarea((Integer) objects[32] != null ? (Integer) objects[32] : 0);
                    o.setTipoTarea((String) objects[33]);
                    o.setDescuento(objects[34] != null ? (Double) objects[34] : Constantes.CERO);
                    //
                    o.setCodigoSubTarea((String) objects[35]);
                    o.setProyectoOtCC((String) objects[36]);
                    o.setOrden((Integer) objects[37]);
                    o.setCantidadFacturada(BigDecimal.valueOf(o.getCantidad()));
                    o.setIdpresupuesto((Integer) objects[38]);
                    o.setPresupuestoCodigo((String) objects[39]);
                    o.setPresupuestoNombre((String) objects[40]);
                    o.setMesPresupuesto((Integer) objects[41]);
                    o.setIdOcCodigoTarea((Integer) objects[42]);
                    o.setIdOcCodigoSubtarea((Integer) objects[43]);
                    o.setAnioPresupuesto((Integer) objects[44]);
                    o.setIdRequisicionDetalle(objects[45] != null ? (Integer) objects[45] : Constantes.CERO);
                    o.setConvenio((String) objects[46]);
                    o.setIdConvenio((Integer) objects[47]);
                    
                    o.setDetDescripcion((String) objects[48]);

                    lrd.add(o);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar los items de la OC/S: " + idOrden + " : : " + e.getMessage());
        }
        return lrd;
    }
    
    
    @Trace
    public List<OrdenDetalleVO> itemsPorOrdenEliminar(int idOrden) {
        StringBuilder sb = new StringBuilder();
        List<OrdenDetalleVO> lrd = null;
        try {
            sb.append(consulta())
                    .append(" WHERE r.id =  ").append(idOrden)
                    .append(" and (o.cantidad_recibida = 0 or o.cantidad_recibida is null) ")
                    .append(" ORDER BY o.id ASC ");
            //
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null && lo.size() > 0) {
                lrd = new ArrayList<>();
                for (Object[] objects : lo) {
                    OrdenDetalleVO o = new OrdenDetalleVO();
                    o.setId((Integer) objects[0]);
                    o.setEnCatalogo(objects[1] != null ? (Boolean) objects[1] : Constantes.BOOLEAN_FALSE);
                    o.setArtNumeroParte(String.valueOf(objects[2]));
                    o.setCantidad(objects[3] != null ? (Double) objects[3] : Constantes.CERO);
                    if (objects[12] == null) {
                        o.setArtUnidad((String) objects[4]);
                        o.setArtIdUnidad(0);
                    } else {
                        o.setArtIdUnidad((Integer) objects[12]);
                        o.setArtUnidad((String) objects[13]);
                    }
                    o.setArtDescripcion((String) objects[5]);
                    o.setPrecioUnitario((Double) objects[6] != null ? (Double) objects[6] : 0.0);
                    o.setImporte((Double) objects[7] != null ? (Double) objects[7] : 0.0);
                    //
                    o.setOcTarea(objects[9] != null ? (Integer) objects[9] : 0);
                    o.setCodeTarea((String) objects[10]);
                    o.setNombreTarea((String) objects[11]);
                    //
                    o.setOcUnidadCosto((Integer) objects[15]);
                    o.setOcProductoID((Integer) objects[16]);
                    o.setOcProductoDesc((String) objects[17]);
                    o.setOcProductoCode((String) objects[18]);
                    o.setArtNombre((String) objects[19]);
                    o.setArtID((Integer) objects[20] != null ? (Integer) objects[20] : 0);
                    
                    if(o.getArtID() == 0){
                        o.setArtNumeroParte((String) objects[49]);
                        
                        if(o.getArtUnidad() == null || o.getArtUnidad().isEmpty()){
                            o.setArtUnidad((String) objects[50]);
                        }
                    }
                    
                    o.setSelected(true);
                    o.setObservaciones((String) objects[21]);
                    o.setTextNav(objects[22] != null ? (String) objects[22] : Constantes.VACIO);
                    o.setFechaRecibido(objects[23] != null ? (Date) objects[23] : new Date());
                    o.setTotalRecibido(objects[24] != null ? (Double) objects[24] : Constantes.CERO);
                    o.setRecibido((Boolean) objects[25]);
                    o.setTotalPendiente(o.getCantidad() - o.getTotalRecibido());
                    o.setIdProyectoOt((Integer) objects[26] != null ? (Integer) objects[26] : 0);
                    o.setProyectoOt((String) objects[27]);
                    o.setIdSubTarea((Integer) objects[28] != null ? (Integer) objects[28] : 0);
                    o.setSubTarea((String) objects[29]);
                    o.setIdActividadPetrolera((Integer) objects[30] != null ? (Integer) objects[30] : 0);
                    o.setActividadPetrolera((String) objects[31]);
                    o.setIdTipoTarea((Integer) objects[32] != null ? (Integer) objects[32] : 0);
                    o.setTipoTarea((String) objects[33]);
                    o.setDescuento(objects[34] != null ? (Double) objects[34] : Constantes.CERO);
                    //
                    o.setCodigoSubTarea((String) objects[35]);
                    o.setProyectoOtCC((String) objects[36]);
                    o.setOrden((Integer) objects[37]);
                    o.setCantidadFacturada(BigDecimal.valueOf(o.getCantidad()));
                    o.setIdpresupuesto((Integer) objects[38]);
                    o.setPresupuestoCodigo((String) objects[39]);
                    o.setPresupuestoNombre((String) objects[40]);
                    o.setMesPresupuesto((Integer) objects[41]);
                    o.setIdOcCodigoTarea((Integer) objects[42]);
                    o.setIdOcCodigoSubtarea((Integer) objects[43]);
                    o.setAnioPresupuesto((Integer) objects[44]);
                    o.setIdRequisicionDetalle(objects[45] != null ? (Integer) objects[45] : Constantes.CERO);
                    o.setConvenio((String) objects[46]);
                    o.setIdConvenio((Integer) objects[47]);
                    
                    o.setDetDescripcion((String) objects[48]);

                    lrd.add(o);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar los items de la OC/S: " + idOrden + " : : " + e.getMessage());
        }
        return lrd;
    }

    
    public List<OrdenDetalleVO> itemsPorOrdenMulti(Object idOrden) {
        StringBuilder sb = new StringBuilder();
        List<OrdenDetalleVO> lrd = null;
        try {
            sb.append(" SELECT "
                    + " o.En_Catalogo, "
                    + " case when art.id > 0 then art.codigo_int else o.codigo end as artCodigo,  "
                    + " sum(o.cantidad),  "
                    + " o.unidad,  "
                    + " case when art.id > 0 then art.descripcion else o.descripcion end as artDesc,  "
                    + " o.Precio_Unitario,  "
                    + " sum(o.Importe),  "
                    + " m.nombre as monedaNombre,  "
                    + " ct.nombre as tareaCodigo,  "
                    + " nt.NOMBRE as tareaNombre,  "
                    + " case when art.id > 0 then art.UNIDAD else o.SI_UNIDAD end as artUnidad,  "
                    + " case when art.id > 0 then cu.nombre else cuu.nombre end as artUnidadNombre,  "
                    + " m.id as monedaID,  "
                    + " r.OC_UNIDAD_COSTO as unidadCostoID,  "
                    + " cp.id as comProdID,  "
                    + " ocp.NOMBRE as comProdNombre,  "
                    + " ocp.CODIGO as comProdCodigo,  "
                    + " art.nombre as artNombre,  "
                    + " art.id as artID,  "
                    + " o.OBSERVACIONES,  "
                    + " o.textNav,  "
                    + " o.fecha_recepcion,  "
                    + " o.cantidad_recibida,  "
                    + " o.recibido,  "
                    + " cst.nombre as subTareaNombre,  "
                    + " ap.id as actPetrolID,  "
                    + " ap.nombre as actPetrolNombre,  "
                    + " uc.id as unidadCostoIDN,  "
                    + " uc.nombre as unidadCostoNombre,  "
                    + " o.MULTIPROYECTO_ID,  "
                    + " o.oc_presupuesto, "
                    + " pres.codigo, "
                    + " pres.nombre, "
                    + " o.mes_presupuesto, "
                    + " o.oc_codigo_tarea, "
                    + " o.oc_codigo_subtarea, "
                    + " o.anio_presupuesto, "
                    + " coalesce(sum(o.descuento), 0),"
                    + " o.convenio_codigo, "
                    + " COALESCE(o.convenio, 0) "
                    + " FROM Orden r "
                    + " inner join Orden_Detalle o ON o.ORDEN = r.ID "
                    + " left join oc_unidad_costo uc on uc.ID = o.OC_unidad_costo"
                    + " left join INV_ARTICULO art on art.ID = o.INV_ARTICULO "
                    + " LEFT join Moneda m on m.ID = o.MONEDA "
                    + " left join SI_UNIDAD cu on cu.ID = art.UNIDAD "
                    + " left join SI_UNIDAD cuu on cuu.ID = o.SI_UNIDAD "
                    + " left join OC_TAREA ot on ot.ID = o.OC_TAREA "
                    + " left join oc_actividadpetrolera ap on ot.oc_actividadpetrolera = ap.id "
                    + " left join OC_NOMBRE_TAREA nt on nt.ID = ot.OC_NOMBRE_TAREA "
                    + " left join OC_codigo_TAREA ct on ct.ID = ot.OC_codigo_TAREA "
                    + " left join OC_PRODUCTO_compania cp on o.OC_PRODUCTO_compania = cp.id "
                    + " left join OC_PRODUCTO ocp on cp.oc_producto = ocp.id "
                    + " left join oc_subtarea st on st.id = o.oc_subtarea"
                    + " left join oc_codigo_subtarea cst on cst.id = st.oc_codigo_subtarea"
                    + " left join oc_presupuesto pres on pres.id = o.oc_presupuesto"
                    + " WHERE r.id =  " + idOrden
                    + " group by  "
                    + " MULTIPROYECTO_ID, "
                    + " En_Catalogo, "
                    + " artCodigo, "
                    + " cantidad, "
                    + " o.unidad, "
                    + " artDesc, "
                    + " Precio_Unitario, "
                    + " Importe, "
                    + " monedaNombre, "
                    + " tareaCodigo, "
                    + " tareaNombre, "
                    + " artUnidad, "
                    + " artUnidadNombre, "
                    + " monedaID, "
                    + " unidadCostoID, "
                    + " comProdID, "
                    + " comProdNombre, "
                    + " comProdCodigo, "
                    + " artNombre, "
                    + " artID,  "
                    + " textNav, "
                    + " fecha_recepcion,  "
                    + " cantidad_recibida,  "
                    + " recibido,  "
                    + " subTareaNombre,  "
                    + " actPetrolID, "
                    + " actPetrolNombre, "
                    + " unidadCostoIDN, "
                    + " unidadCostoNombre,  "
                    + " o.oc_presupuesto, "
                    + " pres.codigo, "
                    + " pres.nombre, "
                    + " o.mes_presupuesto, "
                    + " o.anio_presupuesto,"
                    + " o.oc_codigo_tarea, "
                    + " o.oc_codigo_subtarea, "
                    + " o.convenio_codigo, "
                    + " COALESCE(o.convenio, 0), "
                    + " o.OBSERVACIONES  ");

            //
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null && lo.size() > 0) {
                lrd = new ArrayList<>();
                for (Object[] objects : lo) {
                    OrdenDetalleVO o = new OrdenDetalleVO();
                    //o.setId((Integer) objects[0]);
                    o.setEnCatalogo((Boolean) (objects[0]));
                    o.setArtNumeroParte(String.valueOf(objects[1]));
                    o.setCantidad((Double) objects[2]);

                    o.setArtIdUnidad((Integer) objects[10]);
                    o.setArtUnidad((String) objects[11]);

                    o.setArtDescripcion((String) objects[4]);
                    o.setPrecioUnitario((Double) objects[5] != null ? (Double) objects[5] : 0.0);
                    o.setImporte((Double) objects[6] != null ? (Double) objects[6] : 0.0);
//                    o.setMoneda((String) objects[7] != null ? (String) objects[7] : "");
                    //o.setOcTarea(objects[9] != null ? (Integer) objects[9] : 0);
                    o.setCodeTarea((String) objects[8]);
                    o.setNombreTarea((String) objects[9]);
//                    o.setIdMoneda((Integer) objects[12] != null ? (Integer) objects[12] : 0);
                    o.setOcUnidadCosto((Integer) objects[13]);
                    o.setOcProductoID((Integer) objects[14]);
                    o.setOcProductoDesc((String) objects[15]);
                    o.setOcProductoCode((String) objects[16]);
                    o.setArtNombre((String) objects[17]);
                    o.setArtID((Integer) objects[18] != null ? (Integer) objects[18] : 0);
//                    o.setEditar(o.getOcProductoID() == null || o.getOcProductoID() == 0);
                    o.setSelected(true);
                    o.setObservaciones((String) objects[19]);
                    o.setTextNav(objects[20] != null ? (String) objects[20] : Constantes.VACIO);
                    o.setFechaRecibido(objects[21] != null ? (Date) objects[21] : new Date());
                    o.setTotalRecibido(objects[22] != null ? (Double) objects[22] : Constantes.CERO);
                    o.setRecibido((Boolean) objects[23]);
                    o.setTotalPendiente(o.getCantidad() - o.getTotalRecibido());
                    //o.setIdProyectoOt((Integer) objects[26] != null ? (Integer) objects[26] : 0);
                    //o.setProyectoOt((String) objects[27]);
                    //o.setIdSubTarea((Integer) objects[28] != null ? (Integer) objects[28] : 0);
                    o.setSubTarea((String) objects[24]);
                    o.setIdActividadPetrolera((Integer) objects[25] != null ? (Integer) objects[25] : 0);
                    o.setActividadPetrolera((String) objects[26]);
                    o.setIdTipoTarea((Integer) objects[27] != null ? (Integer) objects[27] : 0);
                    o.setTipoTarea((String) objects[28]);
                    o.setIdAgrupador((Integer) objects[29] != null ? (Integer) objects[29] : 0);
                    o.setMultiProyectos(getItemsMultiProyecto(idOrden, o.getIdAgrupador()));

                    o.setIdpresupuesto((Integer) objects[30] != null ? (Integer) objects[30] : 0);
                    o.setPresupuestoCodigo((String) objects[31]);
                    o.setPresupuestoNombre((String) objects[32]);
                    o.setMesPresupuesto((Integer) objects[33] != null ? (Integer) objects[33] : 0);
                    o.setIdOcCodigoTarea((Integer) objects[34] != null ? (Integer) objects[34] : 0);
                    o.setIdOcCodigoSubtarea((Integer) objects[35] != null ? (Integer) objects[35] : 0);
                    o.setAnioPresupuesto((Integer) objects[36] != null ? (Integer) objects[36] : 0);
                    o.setDescuento((Double) objects[37] != null ? (Double) objects[37] : 0);
                    o.setConvenio((String) objects[38]);
                    o.setIdConvenio((Integer) objects[39]);
                    lrd.add(o);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar los items de la OC/S: " + idOrden + " : : " + e.getMessage());
        }
        return lrd;
    }

    
    public int itemsPorOrdenMultiID(Object idOrden, int agrupadorID) {
        int ret = 0;
        StringBuilder sb = new StringBuilder();
        List<OrdenDetalleVO> lrd = null;
        try {
            sb.append(" SELECT "
                    + " o.ID "
                    + " FROM Orden r "
                    + " inner join Orden_Detalle o ON o.ORDEN = r.ID "
                    + " WHERE r.id =  " + idOrden
                    + " and o.MULTIPROYECTO_ID =" + agrupadorID
                    + " limit 1 "
            );

            Object lo = em.createNativeQuery(sb.toString()).getSingleResult();
            if (lo != null) {
                ret = ((Integer) lo).intValue();
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar los items de la OC/S: " + idOrden + " : : " + e.getMessage());
        }
        return ret;
    }

    private String getItemsMultiProyecto(Object idOrden, int agrupadorID) {
        //Crea una instancia de Query
        String ret = "";
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT ");
        sb.append("  array_to_string(array_agg(pot.NOMBRE), ',') ");
        sb.append(" FROM ORDEN q ");
        sb.append(" inner join ORDEN_DETALLE r on r.ORDEN = q.ID ");
        sb.append(" left join proyecto_Ot pot on pot.id = r.proyecto_Ot ");
        sb.append(" WHERE r.ORDEN = ").append(idOrden);
        sb.append(" and r.MULTIPROYECTO_ID = ").append(agrupadorID);

        Object l = em.createNativeQuery(sb.toString()).getSingleResult();
        if (l != null) {
            ret = String.valueOf(l);
        }
        return ret;
    }

    
    public List<OrdenDetalleVO> traerDetalleOrdenCompra(String cadena, String idUsuario, int idCampo) {
        try {
            List<OrdenDetalleVO> lod = null;
            StringBuilder sb = new StringBuilder();
            sb.append("select od.id, o.id, o.CONSECUTIVO, od.REQUISICION_DETALLE, art.CODIGO, art.DESCRIPCION, ");
            //                  0     1         2                   3                  4               5
            sb.append(" od.DESCRIPCION, od.CANTIDAD, art.UNIDAD, m.id, m.siglas, ");
            //                  6             7             8      9       10
            sb.append(" od.PRECIO_UNITARIO, od.IMPORTE, od.OBSERVACIONES, od.EN_CATALOGO, art.NOMBRE, u.NOMBRE, od.textNav ");
            //                11                 12            13             14            15            16        17
            sb.append(" from ORDEN_DETALLE od");
            sb.append(" inner join INV_ARTICULO art on art.ID = od.INV_ARTICULO ");
            sb.append(" left join SI_UNIDAD u on u.id = art.UNIDAD ");
            sb.append(" LEFT join Orden o on od.ORDEN = o.id");
            sb.append(" LEFT join MONEDA m on od.MONEDA = m.id");
            sb.append(" where UPPER(od.DESCRIPCION) like UPPER('%").append(cadena).append("%')");
            sb.append(" and o.ANALISTA = '").append(idUsuario).append("'");
            sb.append(" and o.ap_campo = ").append(idCampo);
            sb.append(" order by od.id asc");
            UtilLog4j.log.fatal(this, "Q: ref pal: " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                lod = new ArrayList<OrdenDetalleVO>();
                for (Object[] objects : lo) {
                    lod.add(castDetalleOrden(objects));
                }
            }

            return lod;

        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar los items por cadena " + e.getMessage());
            return null;
        }

    }
    
    
    public List<OrdenVO> historicoDetalleOrden(int idInv, int idCampo) {
        List<OrdenVO> lod = null;
        try {            
            String s = " select o.fecha, o.consecutivo, p.nombre, u.nombre, d.precio_unitario, m.nombre "
                    + " , cast(substring(o.consecutivo from position('-' in o.consecutivo)+1 for (char_length(o.consecutivo)-position('-' in o.consecutivo))) as integer) as xx "
                    + " from orden_detalle d "
                    + " inner join orden o on o.id = d.orden "
                    + " inner join proveedor p on p.id = o.proveedor "
                    + " inner join moneda m on m.id = o.moneda "
                    + " inner join autorizaciones_orden ao on ao.orden = o.id "
                    + " inner join inv_articulo inv on inv.id = d.inv_articulo "
                    + " inner join si_unidad u on u.id = inv.unidad "
                    + " where d.eliminado = false "
                    + " and ao.estatus > 150 "
                    + " and d.inv_articulo = " + idInv
                    + " and o.ap_campo = " + idCampo
                    + " group by o.fecha, o.consecutivo, p.nombre, u.nombre, d.precio_unitario, m.nombre, xx "
                    + " order by xx desc "
                    + " limit 5 ";

            UtilLog4j.log.fatal(this, "Q: ref pal: " + s);
            List<Object[]> lo = em.createNativeQuery(s).getResultList();
            if (lo != null) {                
                lod = new ArrayList<OrdenVO>();
                for (Object[] objects : lo) {
                    lod.add(castHistoricoDetalleOrden(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar los items por cadena " + e.getMessage());
            lod = null;
        }
        
        return lod;

    }

    private OrdenVO castHistoricoDetalleOrden(Object[] objects) {
        OrdenVO o = null;
        try {
            o = new OrdenVO();
            o.setFecha((Date) objects[0]);
            o.setConsecutivo((String) objects[1]);
            o.setProveedor((String) objects[2]);
            o.setUnidad((String) objects[3]);
            o.setPrecioU((Double) objects[4]);
            o.setMoneda((String) objects[5]);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error + + + + + ++ " + e.getMessage());
            o = null;
        }
        return o;
    }

    private OrdenDetalleVO castDetalleOrden(Object[] objects) {
        OrdenDetalleVO o = null;
        try {
            o = new OrdenDetalleVO();
            o.setId((Integer) objects[0]);
            o.setOrden((Integer) objects[1]);
            o.setOrdenConsecutivo((String) objects[2]);
            o.setRequisicionDetalle((Integer) objects[3]);
            o.setArtNumeroParte((String) objects[4]);
            o.setArtDescripcion((String) objects[5]);
            o.setCantidad((Double) objects[7]);
            o.setArtIdUnidad((Integer) objects[8]);
//            o.setIdMoneda((Integer) objects[9]);
//            o.setMoneda((String) objects[10]);
            o.setPrecioUnitario((Double) objects[11]);
            o.setImporte((Double) objects[12]);
            o.setObservaciones((String) objects[13]);
            o.setEnCatalogo((Boolean) objects[14]);
            o.setArtNombre((String) objects[15]);
            o.setArtUnidad((String) objects[16]);
            o.setTextNav((String) objects[17]);            

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error + + + + + ++ " + e.getMessage());
            o =  null;
        }
        return o;
    }

    
    public void guardarItem(OrdenDetalleVO ordenDetalleVO, int idOrden, String idSesion) {
        OrdenDetalle od = new OrdenDetalle();
        Orden orOrigen = ordenRemote.find(idOrden);
        od.setCantidad(ordenDetalleVO.getCantidad());

        od.setImporte(ordenDetalleVO.getImporte());
        if (TipoRequisicion.PS.equals(orOrigen.getTipo())) {
            od.setOcTarea(ocTareaRemote.find(ordenDetalleVO.getOcTarea()));
        } else {
            od.setOcTarea(null);
        }
        //
        od.setOcActividadPetrolera(ordenDetalleVO.getIdActividadPetrolera() > 0 ? new OcActividadPetrolera(ordenDetalleVO.getIdActividadPetrolera()) : null);
        od.setProyectoOt(ordenDetalleVO.getIdProyectoOt() > 0 ? new ProyectoOt(ordenDetalleVO.getIdProyectoOt()) : null);
        od.setOcUnidadCosto(ordenDetalleVO.getIdTipoTarea() > 0 ? new OcUnidadCosto(ordenDetalleVO.getIdTipoTarea()) : null);
        od.setOcSubTarea(ordenDetalleVO.getIdSubTarea() > 0 ? new OcSubTarea(ordenDetalleVO.getIdSubTarea()) : null);
        //        
//        od.setMoneda(monedaRemote.find(ordenDetalleVO.getIdMoneda()));
        od.setObservaciones(ordenDetalleVO.getObservaciones());
//        od.setSiUnidad(ocUnidadRemote.find(ordenDetalleVO.getOcUnidad()));
        od.setEnCatalogo(Constantes.BOOLEAN_FALSE);
        od.setOrden(orOrigen);
        od.setPrecioUnitario(ordenDetalleVO.getPrecioUnitario());
        od.setGenero(new Usuario(idSesion));
        od.setFechaGenero(new Date());
        od.setHoraGenero(new Date());
        od.setRecibido(Constantes.BOOLEAN_FALSE);
        od.setEliminado(Constantes.NO_ELIMINADO);
        od.setInvArticulo(articuloRemote.find(ordenDetalleVO.getArtID()));
        od.setDescuento(ordenDetalleVO.getDescuento());
        create(od);
    }

    
    @Trace
    public boolean guardarListaItems(List<RequisicionDetalleVO> listaAuto, int idOrden, String idSesion, String convenio) {
        double total = 0.0;
        boolean v = false;
        Orden orOrigen = ordenRemote.find(idOrden);
        try {
            for (RequisicionDetalleVO requisicionDetalleVO : listaAuto) {
                if (requisicionDetalleVO.getIdAgrupador() > 0) {
                    total = guardarListaItemsDB(requisicionDetalleRemote.getItemPorIdConsultaNativa(requisicionDetalleVO.getIdRequisicion(), requisicionDetalleVO.getIdAgrupador()),
                            orOrigen, idSesion, convenio);
                } else {
                    List<RequisicionDetalleVO> lstAux = new ArrayList<>();
                    lstAux.add(requisicionDetalleVO);
                    total = guardarListaItemsDB(lstAux, orOrigen, idSesion, convenio);
                }
            }
            v = true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "ERROR AL TRAER ITEMS + + + + + + " + e.getMessage());
            v = false;
        }
        //Actualiza el monto de la Orden
        if (!convenio.equals(Constantes.OCS_SIN_CONTRATO)) {
            orOrigen.setSubtotal(total);
            ordenRemote.actualizarMontoOrden(orOrigen, total);
        }
        return v;
    }

    private double guardarListaItemsDB(List<RequisicionDetalleVO> listaAuto, Orden orOrigen, String idSesion, String convenio) {
        double total = 0.0;
        boolean v = false;
        for (RequisicionDetalleVO rdVO : listaAuto) {
            //- - - - - - - -}
            try {
                RequisicionDetalle requisicionDetalleVO = requisicionDetalleRemote.find(rdVO.getIdRequisicionDetalle());
                OrdenDetalle ordenDetalle = new OrdenDetalle();
                ordenDetalle.setOrden(orOrigen);
                ordenDetalle.setRequisicionDetalle(requisicionDetalleVO);
                ordenDetalle.setMultiproyectoId(requisicionDetalleVO.getMultiproyectoId());

                ordenDetalle.setCantidad(requisicionDetalleVO.getCantidadAutorizada());
                if (TipoRequisicion.PS.toString().equals(orOrigen.getTipo()) 
                        || ("C".equals(orOrigen.getApCampo().getTipo()))) {
                    ordenDetalle.setOcTarea(requisicionDetalleVO.getOcTarea());
                    ordenDetalle.setOcUnidadCosto(requisicionDetalleVO.getOcUnidadCosto() != null ? requisicionDetalleVO.getOcUnidadCosto() : orOrigen.getOcUnidadCosto());
                }

                ordenDetalle.setObservaciones(requisicionDetalleVO.getObservaciones());
                ordenDetalle.setEnCatalogo(Constantes.BOOLEAN_FALSE);
                ordenDetalle.setGenero(new Usuario(idSesion));
                ordenDetalle.setFechaGenero(new Date());
                ordenDetalle.setHoraGenero(new Date());
                ordenDetalle.setRecibido(Constantes.BOOLEAN_FALSE);
                ordenDetalle.setEliminado(Constantes.NO_ELIMINADO);
                ordenDetalle.setInvArticulo(requisicionDetalleVO.getInvArticulo());
                ordenDetalle.setTextNav(requisicionDetalleVO.getTextNav());
                //4
                ordenDetalle.setCodigo(requisicionDetalleVO.getInvArticulo().getCodigoInt());
                ordenDetalle.setDescripcion(requisicionDetalleVO.getDescripcionSolicitante());
                //
                //ot, act pet, sub
                ordenDetalle.setProyectoOt(requisicionDetalleVO.getProyectoOt() != null ? requisicionDetalleVO.getProyectoOt() : orOrigen.getProyectoOt());

                if ("C".equals(orOrigen.getApCampo().getTipo())) {
                    ordenDetalle.setOcActividadPetrolera(requisicionDetalleVO.getOcActividadpetrolera());
                    ordenDetalle.setOcPresupuesto(requisicionDetalleVO.getOcPresupuesto());
                    ordenDetalle.setAnioPresupuesto(requisicionDetalleVO.getAnioPresupuesto());
                    ordenDetalle.setMesPresupuesto(requisicionDetalleVO.getMesPresupuesto());
                    ordenDetalle.setOcSubTarea(requisicionDetalleVO.getOcSubTarea());
                    ordenDetalle.setOcCodigoTarea(requisicionDetalleVO.getOcCodigoTarea());
                    ordenDetalle.setOcCodigoSubtarea(requisicionDetalleVO.getOcCodigoSubtarea());
                }
                //
                if (!convenio.equals(Constantes.OCS_SIN_CONTRATO)) {
                    ConvenioArticuloVo ca = cvConvenioArticuloLocal.codigoConvenioArticulo(convenio, requisicionDetalleVO.getInvArticulo().getId(), orOrigen.getApCampo().getId());
                    if (ca != null) {
                        ordenDetalle.setPrecioUnitario(ca.getPrecioUnitario());
                        ordenDetalle.setImporte(requisicionDetalleVO.getCantidadAutorizada() * ca.getPrecioUnitario());
                        //
                        total += ordenDetalle.getImporte();
                    }
                    //
                    orOrigen.setContrato(convenio);
                } else {
                    orOrigen.setContrato(Constantes.OCS_SIN_CONTRATO);
                }
                create(ordenDetalle);
                //
                requisicionDetalleRemote.cambiarDisgregadoItemRequisicion(rdVO, idSesion, Constantes.BOOLEAN_TRUE);
                v = true;
            } catch (Exception e) {
                UtilLog4j.log.fatal(this, "ERROR AL CREAR ITEMS + + + + + + " + e);
                v = false;
            }
        }
        //Actualiza el monto de la Orden
        if (!convenio.equals(Constantes.OCS_SIN_CONTRATO)) {
            orOrigen.setSubtotal(total);
            ordenRemote.actualizarMontoOrden(orOrigen, total);
        }
        return total;
    }

    
    public boolean tieneInvArticulo(int idOrden, boolean onlyInvArticulo) {
        boolean tiene = false;
        try {
            //Crea una instancia de Query
            StringBuilder sb = new StringBuilder();
            sb.append(" select count(q.INV_ARTICULO), (select count(id) from ORDEN_DETALLE where INV_ARTICULO is null and ORDEN = ").append(idOrden).append(")");
            sb.append(" FROM ORDEN_DETALLE q ");
            sb.append(" where q.ORDEN = ").append(idOrden);
            //
            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();

            if (!onlyInvArticulo && (Long) obj[0] == 0 && (Long) obj[1] > 0) {
                tiene = true;
            } else if ((Long) obj[0] > 0 && (Long) obj[1] == 0) {
                tiene = true;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return tiene;
    }

    private String consulta() {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT "
                + " o.id, o.En_Catalogo, art.codigo_int, o.cantidad, o.unidad, "
                + "            art.descripcion, o.Precio_Unitario, o.Importe, m.nombre, o.OC_TAREA, "
                + "            ct.nombre, nt.NOMBRE, art.UNIDAD, cu.nombre, "
                + "     m.id, r.OC_UNIDAD_COSTO, cp.id, ocp.NOMBRE, ocp.CODIGO, art.nombre, art.id, o.OBSERVACIONES, o.textNav , o.fecha_recepcion, "
                + "     o.cantidad_recibida, o.recibido, pot.id, pot.nombre, st.id, cst.nombre, ap.id, ap.nombre, uc.id, uc.nombre, o.descuento, cst.codigo, pot.CUENTA_CONTABLE, r.id, "
                + " COALESCE(o.oc_presupuesto, 0), pres.codigo, pres.nombre, COALESCE(o.mes_presupuesto, 0), COALESCE(o.oc_codigo_tarea, 0),"
                + " COALESCE(o.oc_codigo_subtarea, 0), COALESCE(o.anio_presupuesto, 0), "
                + " o.requisicion_detalle, o.convenio_codigo, COALESCE(o.convenio, 0), o.descripcion, o.codigo, (select x.nombre from si_unidad x where x.id = o.si_unidad) "
                + " FROM Orden r "
                + "   inner join Orden_Detalle o ON o.ORDEN = r.ID  and o.eliminado = 'False' "
                + "   left join oc_unidad_costo uc on uc.ID = o.OC_unidad_costo"
                + "   left join INV_ARTICULO art on art.ID = o.INV_ARTICULO "
                + "   LEFT join Moneda m on m.ID = o.MONEDA "
                + "   left join SI_UNIDAD cu on cu.ID = art.UNIDAD "
                + "   left join OC_TAREA ot on ot.ID = o.OC_TAREA "
                + "   left join oc_actividadpetrolera ap on ot.oc_actividadpetrolera = ap.id "
                + "   left join OC_NOMBRE_TAREA nt on nt.ID = ot.OC_NOMBRE_TAREA "
                + "   left join OC_codigo_TAREA ct on ct.ID = ot.OC_codigo_TAREA "
                + "   left join OC_PRODUCTO_compania cp on o.OC_PRODUCTO_compania = cp.id "
                + "   left join OC_PRODUCTO ocp on cp.oc_producto = ocp.id "
                + "   left join proyecto_Ot pot on pot.id = o.proyecto_Ot "
                + "   left join oc_subtarea st on st.id = o.oc_subtarea "
                + "   left join oc_codigo_subtarea cst on cst.id = st.oc_codigo_subtarea "
                + "   left join oc_presupuesto pres on pres.id = o.oc_presupuesto ");
        return sb.toString();
    }

    
    public void actualizarItem(OrdenDetalleVO ordenDetalleVO, String idSesion, int fromOrdenID, int toOrdenID) {
        OrdenDetalle od;
        try {
            if (ordenDetalleVO.getId() > Constantes.CERO) {
                od = find(ordenDetalleVO.getId());
                //
                od.setPrecioUnitario(ordenDetalleVO.getPrecioUnitario());
                if (ordenDetalleVO.getCantidadFacturada() != null) {
                    od.setCantidad(ordenDetalleVO.getCantidadFacturada().doubleValue());
                }
                od.setCantidad(ordenDetalleVO.getCantidad());
                od.setImporte(ordenDetalleVO.getImporte());
                od.setDescuento(ordenDetalleVO.getDescuento());
                od.setConvenio(ordenDetalleVO.getIdConvenio());
                od.setConvenioCodigo(ordenDetalleVO.getConvenio());
                //
                actualizar(od, toOrdenID, idSesion);
            } else {
                List<OrdenDetalle> ld = getItemsPorOrden(fromOrdenID, ordenDetalleVO.getIdAgrupador());
                double cant = 0.0;
                if (ordenDetalleVO.getCantidad() > 0) {
                    cant = ordenDetalleVO.getCantidad() / ld.size();
                }
                double desc = ordenDetalleVO.getDescuento() / ld.size();

                for (OrdenDetalle ordenDetalle : ld) {
                    ordenDetalle.setCantidad(cant);
                    ordenDetalle.setPrecioUnitario(ordenDetalleVO.getPrecioUnitario());
                    if (ordenDetalleVO.getCantidadFacturada() != null) {
                        ordenDetalle.setCantidad(ordenDetalleVO.getCantidadFacturada().doubleValue());
                    }
                    ordenDetalle.setImporte(ordenDetalle.getCantidad() * ordenDetalle.getPrecioUnitario());
                    ordenDetalle.setDescuento(desc);
                    ordenDetalle.setConvenio(ordenDetalleVO.getIdConvenio());
                    ordenDetalle.setConvenioCodigo(ordenDetalleVO.getConvenio());
                    //
                    actualizar(ordenDetalle, toOrdenID, idSesion);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    private void actualizar(OrdenDetalle od, int orden, String idSesion) {
        od.setOrden(new Orden(orden));
        //
        od.setGenero(new Usuario(idSesion));
        od.setFechaGenero(new Date());
        od.setHoraGenero(new Date());
        od.setRecibido(Constantes.BOOLEAN_FALSE);
        od.setEliminado(Constantes.NO_ELIMINADO);
        edit(od);
    }

    
    public double traerTotalOrden(int orden) {
        String s = "select coalesce(sum(importe) - SUM(DESCUENTO), 0) from orden_detalle where orden = " + orden + " and eliminado = false ";
        return (Double) em.createNativeQuery(s).getSingleResult();
    }

    
    public double traerTotalDescuentoOrden(int orden) {
        String s = "select coalesce(SUM(DESCUENTO), 0) from orden_detalle where orden = " + orden + " and eliminado = false ";
        return (Double) em.createNativeQuery(s).getSingleResult();
    }

    
    public void agragarMonedaItems(List<OrdenDetalleVO> items, int moneda, String sesion) {
        for (OrdenDetalleVO item : items) {
            OrdenDetalle od = find(item.getId());
//            od.setMoneda(new Moneda(moneda));
            od.setModifico(new Usuario(sesion));
            od.setFechaModifico(new Date());
            od.setHoraModifico(new Date());
            edit(od);
        }
    }

    
    public List<OrdenDetalleVO> traerDetalleOrdenAgrupadoMultiProyecto(int idOrden) {
        List<OrdenDetalleVO> lista = null;
        String sql
                = //                                                      0                             1
                "SELECT COALESCE(art.nombre, od.descripcion) AS art_nombre, od.textnav as textNav, sum(od.cantidad) AS cantidad, "
                + " COALESCE(sum(fd.cantidad), 0) AS cantidad_facturada, od.precio_unitario, sum(od.importe) AS importe, "
                + " COALESCE(od.multiproyecto_id, 0) AS id_agrupador, true AS selected, "
                + " sum(od.cantidad) - COALESCE(sum(fd.cantidad), 0) AS cantidad_por_facturar \n"
                + "FROM orden_detalle od \n"
                + "      LEFT JOIN inv_articulo art ON od.inv_articulo = art.id \n"
                + "      LEFT JOIN si_factura_detalle fd  ON fd.orden_detalle = od.id AND fd.eliminado = false \n"
                + " WHERE od.orden = ? AND od.eliminado = false \n"
                + " GROUP BY art_nombre, textNav, od.cantidad,  od.precio_unitario, od.importe, od.multiproyecto_id \n"
                + " ORDER BY art_nombre";

        try {
            List<Object[]> objs = em.createNativeQuery(sql).setParameter(1, idOrden).getResultList();
            lista = new ArrayList<>();
            for (Object[] obj : objs) {
                lista.add(castMultiProyecto(obj));
            }
            //lista = dslCtx.fetch(sql, idOrden).into(OrdenDetalleVO.class);
        } catch (Exception e) {
            UtilLog4j.log.warn(this, "SQL: {0}", new Object[]{sql}, e);
            lista = Collections.emptyList();
        }

        return lista;
    }

    private OrdenDetalleVO castMultiProyecto(Object[] obj) {
        OrdenDetalleVO odvo = new OrdenDetalleVO();
        odvo.setArtNombre((String) obj[0]);
        odvo.setTextNav(obj[1] != null ? (String) obj[1] : "");        
        odvo.setCantidad((Double) obj[2]);
        odvo.setCantidadFacturada(((BigDecimal) obj[3]));        
        odvo.setPrecioUnitario((Double) obj[4]);        
        odvo.setImporte((Double) obj[5]);
        odvo.setIdAgrupador((Integer) obj[6]);
        odvo.setSelected((Boolean) obj[7]);
        odvo.setCantidadPorFacturar(BigDecimal.valueOf((Double) obj[8]));
        //
        return odvo;
    }

    
    public List<OrdenDetalleVO> traerPartidasNoFacturadaMultiProyecto(int idOrden) {
        String c = " select  articuloId, nombre, cantidad, precio, importe, multiproyecto from ( \n"
                + " select art.id as articuloId, COALESCE(art.nombre, od.descripcion) as nombre, sum(od.cantidad) as cantidad, od.precio_unitario as precio, sum(od.importe) as importe, od.multiproyecto_id as multiproyecto \n"
                + "                 from orden_detalle od \n"
                + "                      left join inv_articulo art on od.inv_articulo = art.id \n"
                + "                 where od.orden = ?1 and od.eliminado = false \n"
                + "                 GROUP by art.id, COALESCE(art.nombre, od.descripcion),od.precio_unitario, od.importe, od.multiproyecto_id \n"
                + " ) as factura_detalle \n"
                + " where nombre  not in (SELECT fd.descripcion from si_factura_detalle  fd \n"
                + "					inner join si_factura f on fd.si_factura = f.id \n"
                + "					where fd.eliminado = false \n"
                + "					and f.orden = ?2)";

        List<Object[]> lo = em.createNativeQuery(c).setParameter(1, idOrden).setParameter(2, idOrden).getResultList();
        List<OrdenDetalleVO> lista = new ArrayList<>();
        for (Object[] objects : lo) {
            OrdenDetalleVO odvo = new OrdenDetalleVO();
            odvo.setArtID((Integer) objects[0]);
            odvo.setArtNombre((String) objects[1]);
            odvo.setCantidad((Double) objects[2]);
            odvo.setCantidadFacturada(new BigDecimal(BigInteger.ZERO));
            odvo.setCantidadPorFacturar(new BigDecimal(odvo.getCantidad()));
            odvo.setPrecioUnitario((Double) objects[3]);
            odvo.setImporte((Double) objects[4]);
            odvo.setIdAgrupador(objects[5] != null ? (Integer) objects[5] : 0);
            odvo.setSelected(Boolean.TRUE);
            //
            lista.add(odvo);
        }
        return lista;
    }

    
    public OrdenDetalle itemsPorOrdenMultiID(int idOrden, int agrupadorID) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(" SELECT o.* FROM Orden_detalle o")
                    .append(" WHERE o.orden  =  ?1 ")
                    .append(" and o.MULTIPROYECTO_ID = ?2  and o.eliminado = false limit 1 ");

            return (OrdenDetalle) em.createNativeQuery(sb.toString(), OrdenDetalle.class).setParameter(1, idOrden).setParameter(2, agrupadorID).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar los items multiproyecto de la OC/S: " + idOrden + " : : " + e.getMessage());
        }
        return null;
    }
            
    
    public String validarPresupuesto(int idOrden) {
        String msg = null;
        try {
            String sb
                    = " select dd.oc_presupuesto, dd.anio_presupuesto, dd.oc_codigo_subtarea, p.nombre,cs.nombre, o.moneda, "
                    + " sum(case when m.siglas = 'USD' then dd.importe::numeric else (dd.importe/(select pv.valor "
                    + " from paridad a "
                    + " inner join paridad_valor pv on pv.paridad = a.id "
                    + " where a.monedades = o.moneda "
                    + " order by pv.fecha_valido desc "
                    + " limit 1))::numeric end)+ "
                    + " ( "
                    + " select sum(d.importe::numeric) "
                    + " from orden o "
                    + " inner join orden_detalle d on d.orden = o.id and d.eliminado = " + Constantes.BOOLEAN_FALSE
                    + " inner join autorizaciones_orden ao on ao.orden = o.id and ao.eliminado = " + Constantes.BOOLEAN_FALSE
                    + " where o.eliminado = " + Constantes.BOOLEAN_FALSE
                    + " and ao.estatus > " + Constantes.ORDENES_SIN_SOLICITAR
                    + " and d.oc_presupuesto = dd.oc_presupuesto "
                    + " and d.oc_codigo_subtarea = dd.oc_codigo_subtarea "
                    + " ), "
                    + " (select  "
                    + " sum(coalesce(mano_obra_cn, 0)+ "
                    + " coalesce(mano_obra_ex, 0)+ "
                    + " coalesce(bienes_cn, 0)+ "
                    + " coalesce(bienes_ex, 0)+ "
                    + " coalesce(servicios_cn, 0)+ "
                    + " coalesce(servicios_ex, 0)+ "
                    + " coalesce(capacitacion_cn, 0)+ "
                    + " coalesce(capacitacion_ex, 0)+ "
                    + " coalesce(trans_tecnologia, 0)+ "
                    + " coalesce(infraestructura, 0)) "
                    + " from oc_presupuesto_detalle   "
                    + " where oc_presupuesto = dd.oc_presupuesto "
                    + " and anio_presupuesto = dd.anio_presupuesto "
                    + " and oc_codigo_subtarea = dd.oc_codigo_subtarea) "
                    + " from orden_detalle dd "
                    + " inner join oc_codigo_subtarea cs on cs.id = dd.oc_codigo_subtarea "
                    + " inner join oc_presupuesto p on p.id = dd.oc_presupuesto "
                    + " inner join orden o on o.id = dd.orden "
                    + " inner join moneda m on m.id = o.moneda and m.eliminado = false "
                    + " where dd.eliminado =  " + Constantes.BOOLEAN_FALSE
                    + " and dd.orden = " + idOrden
                    + " group by dd.oc_presupuesto, dd.anio_presupuesto, dd.oc_codigo_subtarea, p.nombre,cs.nombre,o.moneda ";

            List<Object[]> objs = em.createNativeQuery(sb).getResultList();

            for (Object[] obj : objs) {
                if (((BigDecimal) obj[7]).compareTo(((BigDecimal) obj[6])) < 0) {
                    if (msg == null || msg.isEmpty()) {
                        msg = "Presupuesto:" + (String) obj[3] + "/ Partida presupuestal:" + (String) obj[4];
                    } else {
                        msg += ", " + "Presupuesto:" + (String) obj[3] + "/ Partida presupuestal:" + (String) obj[4];
                    }
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            msg = null;
        }
        return msg;
    }
    
    
    public void eliminarItem(int idPartida, int orderID, String idSesion, String motivo) {
        OrdenDetalle od;
        try {
            if (idPartida > Constantes.CERO) {
                od = find(idPartida);                
                if(od.getOrden().getId() == orderID){
                    od.setEliminado(true);
                    od.setCancelo(new Usuario(idSesion));
                    od.setMotivoCancelar(motivo);
                    od.setFechaCancelo(new Date());                                    
                    edit(od);
                }                                
            } 
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }
}
