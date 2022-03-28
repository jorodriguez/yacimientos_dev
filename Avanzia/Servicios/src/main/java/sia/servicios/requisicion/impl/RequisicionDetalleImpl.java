/*
 * RequisicionDetalleImpl.java
 * Creado el 7/07/2009, 08:47:52 AM
 * EJB sin estado desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este EJB sin estado (Stateless Session EJB), asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: hacosta.0505@gmail.com
 */
package sia.servicios.requisicion.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.InvArticulo;
import sia.modelo.OcTarea;
import sia.modelo.OcUnidadCosto;
import sia.modelo.ProyectoOt;
import sia.modelo.Requisicion;
import sia.modelo.RequisicionDetalle;
import sia.modelo.SiUnidad;
import sia.modelo.Usuario;
import sia.modelo.requisicion.vo.RequisicionDetalleVO;
import sia.servicios.catalogos.impl.ProyectoOtImpl;
import sia.servicios.orden.impl.OcUnidadCostoImpl;
import sia.servicios.sistema.impl.SiUnidadImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author @version 1.0
 * @author-mail
 */
@Stateless
public class RequisicionDetalleImpl {

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    private SiUnidadImpl ocUnidadRemote;
    @Inject
    private ProyectoOtImpl proyectoOtRemote;
    @Inject
    private OcUnidadCostoImpl ocUnidadCostoRemote;

    /**
     *
     * @param requisicionDetalle
     */
    public void create(RequisicionDetalle requisicionDetalle) {
        em.persist(requisicionDetalle);
    }

    public void edit(RequisicionDetalle requisicionDetalle) {
        em.merge(requisicionDetalle);
    }

    public void remove(RequisicionDetalle requisicionDetalle) {
        em.remove(em.merge(requisicionDetalle));
    }

    public RequisicionDetalle find(Object id) {
        return em.find(RequisicionDetalle.class, id);
    }

    public List<RequisicionDetalle> findAll() {
        return em.createQuery("select object(o) from RequisicionDetalle as o").getResultList();
    }

    //Recupera Items por JPA
    public List<RequisicionDetalle> getItemsPorRequisicion(Object idRequisicion, int agrupadorID) {
        //Crea una instancia de Query
        String s = "SELECT r FROM RequisicionDetalle r WHERE r.requisicion.id = :idRequisicion ";
        if (agrupadorID > 0) {
            s += " and r.multiproyectoId = :idMultiproyecto ";
        }
        s += " ORDER BY r.id ASC ";
        Query query = em.createQuery(s);
        //Fijar los parámetros proporcionados en la consulta
        query.setParameter("idRequisicion", idRequisicion);
        if (agrupadorID > 0) {
            query.setParameter("idMultiproyecto", agrupadorID);
        }
        //Retorna el resultado del Query en este caso una lista
        return query.getResultList();   
    }

    //Recupera Items por Consulta nativa
    public List<RequisicionDetalleVO> getItemsPorRequisicionConsultaNativaMulti(Object idRequisicion, boolean autorizdo, boolean seleccionado) {
        //Crea una instancia de Query
        String s = "SELECT "
                + " q.id as reqID, "//0
                + " case when r.INV_ARTICULO > 0 then art.CODIGO_INT else r.NUMERO_PARTE end as numParte, "//1
                + " case when r.INV_ARTICULO > 0 then art.descripcion else r.DESCRIPCION_SOLICITANTE end as artDesc, "//2
                + " r.autorizado, "//3
                + " r.disgregado, "//4
                + " r.observaciones, "//5
                + " r.OC_unidad_costo as ocUniCostID, "//6
                + " uc.nombre as ocUniCostNombre, "//7
                + " case when r.INV_ARTICULO > 0 then art.unidad else r.SI_UNIDAD end as artUnidadID,"//8
                + " nt.nombre as tareaNombre,"//9
                + " ct.nombre as tareaCodigo, "//10
                + " art.nombre as artNombre, "//11
                + " r.INV_ARTICULO as artID, "//12
                + " r.textNav, "
                //             13
                + " cst.nombre as subTareaNombre "
                //                 14
                + " , ap.NOMBRE as actPetrolera, ap.CODIGO as actPetroleraCodigo, ap.id as actPetroleraID, cst.CODIGO as subTareaCodigo "
                //                        15                              16                        17                    18
                + " , cu.nombre as artUnidad, " //19
                + " sum(r.cantidad_solicitada) as cantSol, "//20
                + " sum(r.cantidad_autorizada) as cantAut, " //21
                + " r.MULTIPROYECTO_ID, " //22
                + " r.oc_presupuesto,  " //23
                + " r.mes_presupuesto, " //24
                + " pres.nombre,  " //25
                + " pres.codigo, " //26
                + " r.anio_presupuesto "
                + " ,\n"
                + "(SELECT inv.numero_unidades from  inv_inventario  inv\n"
                + "									inner join inv_articulo a on inv.articulo = a.id\n"
                + "									inner join inv_almacen al on inv.almacen = al.id and al.eliminado = false \n"
                + "								where r.inv_articulo = a.id and al.id = apc.inv_almacen and inv.eliminado = false), " //27
                + "   apc.id" //28
                + " FROM Requisicion q "
                + " inner join ap_campo apc on apc.id = q.ap_campo "
                + " inner join Requisicion_Detalle r on r.REQUISICION = q.ID "
                + " left join INV_ARTICULO art on art.ID = r.INV_ARTICULO ";

        if (autorizdo) {
            s += " AND r.autorizado = '" + Constantes.BOOLEAN_TRUE + "'";
        }
        s += " left join oc_unidad_costo uc on uc.ID = r.OC_unidad_costo"
                + " left join OC_TAREA ot on ot.ID = r.OC_TAREA "
                + " left join OC_NOMBRE_TAREA nt on nt.ID = ot.OC_NOMBRE_TAREA "
                + " left join OC_codigo_TAREA ct on ct.ID = ot.OC_codigo_TAREA "
                + " left join SI_UNIDAD cu on cu.ID = art.UNIDAD "
                + " left join MONEDA m on m.id = r.MONEDA "
                + " left join proyecto_Ot pot on pot.id = r.proyecto_Ot "
                + " left join oc_subtarea st on st.id = r.oc_subtarea"
                + " left join oc_codigo_subtarea cst on cst.id = st.oc_codigo_subtarea "
                + " left join OC_ACTIVIDADPETROLERA ap on ap.id = ot.OC_ACTIVIDADPETROLERA"
                + " left join oc_presupuesto pres on pres.id = r.oc_presupuesto "
                + " WHERE r.requisicion = " + idRequisicion
                + " group BY "
                + " reqID,numParte,artDesc,autorizado,disgregado,ocUniCostID,ocUniCostNombre,artUnidadID,tareaNombre,tareaCodigo,artNombre,artID,textNav,subTareaNombre,actPetrolera,actPetroleraCodigo,actPetroleraID,subTareaCodigo,artUnidad,MULTIPROYECTO_ID,r.oc_presupuesto,r.mes_presupuesto,pres.nombre,pres.codigo,r.anio_presupuesto,r.observaciones,apc.id ";
        //
        List<RequisicionDetalleVO> lo = null;
        List<Object[]> l = em.createNativeQuery(s).getResultList();
        if (l != null && l.size() > 0) {
            lo = new ArrayList<>();
            for (Object[] objects : l) {
                lo.add(castRequisicionDetalleNativaMulti(objects, seleccionado, (Integer) idRequisicion));
            }
        }
        return lo;
    }

    private RequisicionDetalleVO castRequisicionDetalleNativaMulti(Object[] objects, boolean seleccionado, int idRequi) {
        RequisicionDetalleVO o;
        o = new RequisicionDetalleVO();
        //o.setIdRequisicionDetalle((Integer) objects[0]);
        o.setIdRequisicion((Integer) (objects[0]));
        o.setArtNumeroParte(String.valueOf(objects[1]));

        o.setArtDescripcion((String) objects[2]);
        o.setAutorizado((Boolean) objects[3]);
        o.setDisgregado((Boolean) objects[4]);
        o.setObservacion((String) objects[5]);
        //
        if (objects[8] != null) {
            o.setArtUnidad((String) objects[19]);
            o.setArtIdUnidad((Integer) objects[8]);
        }

        o.setIdUnidadCosto(objects[6] != null ? (Integer) objects[6] : 0);
        o.setIdTipoTarea(objects[6] != null ? (Integer) objects[6] : 0);
        o.setUnidadCosto(objects[7] != null ? (String) objects[7] : "");
        o.setTipoTarea(objects[7] != null ? (String) objects[7] : "");
        o.setNombreTarea(objects[9] != null ? (String) objects[9] : "");

        o.setCodeTarea(objects[10] != null ? (String) objects[10] : "");
        o.setArtNombre((String) objects[11]);
        o.setArtID((Integer) (objects[12]) != null ? (Integer) (objects[12]) : 0);
        o.setTextNav((String) objects[13]);
        o.setSubTarea((String) objects[14]);
        o.setSelected(seleccionado);

        o.setNombreActPedrolera(objects[15] != null ? (String) objects[15] : "");
        o.setCodigoActPedrolera(objects[16] != null ? (String) objects[16] : "");
        o.setIdActPedrolera((Integer) (objects[17]) != null ? (Integer) (objects[17]) : 0);
        o.setCodigoSubTarea(objects[18] != null ? (String) objects[18] : "");

        o.setCantidadSolicitada(objects[20] != null ? (Double) objects[20] : Constantes.CERO);
        o.setCantidadAutorizada(objects[21] != null ? (Double) objects[21] : Constantes.CERO);
        o.setIdAgrupador((Integer) (objects[22]) != null ? (Integer) (objects[22]) : 0);
        o.setMultiProyectos(this.getItemsMultiProyecto(idRequi, o.getIdAgrupador()));
        o.setIdpresupuesto((Integer) (objects[23]) != null ? (Integer) (objects[23]) : 0);
        o.setMesPresupuesto((Integer) (objects[24]) != null ? (Integer) (objects[24]) : 0);
        o.setPresupuestoNombre(objects[25] != null ? (String) objects[25] : "");
        o.setPresupuestoCodigo(objects[26] != null ? (String) objects[26] : "");
        o.setAnioPresupuesto((Integer) (objects[27]) != null ? (Integer) (objects[27]) : 0);
        o.setTotalInventario(objects[28] != null ? ((BigDecimal) objects[28]).doubleValue() : 0);
        return o;
    }

    private String getItemsMultiProyecto(Object idRequisicion, int AgrupadorID) {
        //Crea una instancia de Query
        String ret = "";
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT "
                + " array_to_string(array_agg(pot.NOMBRE), ', ') "
                + " FROM Requisicion q "
                + " inner join Requisicion_Detalle r on r.REQUISICION = q.ID "
                + " left join proyecto_Ot pot on pot.id = r.proyecto_Ot ");
        sb.append(" WHERE r.requisicion = ").append(idRequisicion);
        sb.append(" and r.MULTIPROYECTO_ID = ").append(AgrupadorID);

        Object l = em.createNativeQuery(sb.toString()).getSingleResult();
        if (l != null) {
            ret = String.valueOf(l);
        }
        return ret;
    }

    //Recupera Items por Consulta nativa
    public List<RequisicionDetalleVO> getItemsPorRequisicionConsultaNativa(int idRequisicion, boolean autorizdo, boolean seleccionado) {
        //Crea una instancia de Query
        StringBuilder sb = new StringBuilder();
        sb.append(consulta());
        sb.append(" WHERE r.requisicion = ").append(idRequisicion);
        if (autorizdo) {
            sb.append(" AND r.autorizado = '").append(Constantes.BOOLEAN_TRUE).append("'");
        }
        sb.append(" ORDER BY r.id ASC");
        //
        List<RequisicionDetalleVO> lo = null;
        List<Object[]> l = em.createNativeQuery(sb.toString()).getResultList();
        if (l != null && l.size() > 0) {
            lo = new ArrayList<>();
            for (Object[] objects : l) {
                lo.add(castRequisicionDetalleNativa(objects, seleccionado));
            }
        }
        return lo;
    }

    public List<RequisicionDetalle> getItemsAnalista(Object idRequisicion) {
        return em.createQuery(
                "SELECT r FROM RequisicionDetalle r WHERE r.requisicion.id = :idRequisicion AND r.autorizado = :autorizado"
                + " AND r.disgregado = :disgregado ORDER BY r.id ASC").setParameter("idRequisicion", idRequisicion).setParameter("autorizado", Constantes.BOOLEAN_TRUE).setParameter("disgregado", "No").getResultList();
    }

    public List<RequisicionDetalleVO> getItemsAnalistaNativa(int idRequisicion, boolean seleccionado) {
        //Crea una instancia de Query
        StringBuilder sb = new StringBuilder();
        sb.append(consulta())
                .append(" where q.id = ").append(idRequisicion)
                .append(" AND r.autorizado = true AND r.disgregado = false")
                .append(" ORDER BY r.id ASC");
        //
        LOGGER.info("Q: items analista" + sb.toString());
        //Retorna el resultado del Query en este caso una lista
        List<RequisicionDetalleVO> lo = null;
        List<Object[]> l = em.createNativeQuery(sb.toString()).getResultList();
        if (l != null) {
            lo = new ArrayList<>();
            for (Object[] objects : l) {
                lo.add(castRequisicionDetalleNativa(objects, seleccionado));
            }
        }
        return lo;
    }

    public List<RequisicionDetalleVO> getItemsAnalistaNativaMulti(Object idRequisicion, boolean seleccionado) {
        //Crea una instancia de Query
        String s = "SELECT "
                + " q.id as reqID, " // 0
                + " case when r.INV_ARTICULO > 0 then art.CODIGO_INT else r.NUMERO_PARTE end as numParte, " // 1
                + " sum(r.cantidad_solicitada), " // 2
                + " sum(r.cantidad_autorizada), " // 3
                + " r.unidad as reqUnidad, " // 4
                + " case when r.INV_ARTICULO > 0 then art.descripcion else r.DESCRIPCION_SOLICITANTE end as artDesc, " // 5
                + " r.autorizado, " // 6
                + " r.disgregado, " // 7
                + " r.observaciones, " // 8
                + " r.OC_unidad_costo, " // 9
                + " uc.nombre as unidadCostoNombre, " // 10
                + " art.unidad as artUnidadID, " // 11
                + " nt.nombre as nombreTarea, " // 12
                + " ct.nombre as codigoTarea, " // 13
                + " art.nombre as artNombre, " // 14
                + " r.INV_ARTICULO, " // 15
                + " r.PRECIO_UNITARIO, " // 16
                + " r.IMPORTE, " // 17
                + " r.textNav, " // 18
                + " uc.id as unidadCostoID, " // 19
                + " uc.nombre as unidadCostoNombreN, " // 20
                + " cst.nombre as subTareaNombre, " // 21
                + " ap.NOMBRE as actPetrolNombre, " // 22
                + " ap.CODIGO as actPetrolCodigo, " // 23
                + " ap.id as actPetrolID, " // 24
                + " cst.CODIGO as subTareaCodigo, " // 25
                + " cu.nombre as unidadMedidaNombre, " // 26
                + " r.MULTIPROYECTO_ID " // 27
                + " FROM Requisicion q "
                + " inner join Requisicion_Detalle r on (r.REQUISICION = q.ID "
                + " AND r.autorizado = '" + Constantes.BOOLEAN_TRUE + "'"
                + " AND r.disgregado = '" + Constantes.BOOLEAN_FALSE + "')"
                + " left join INV_ARTICULO art on art.ID = r.INV_ARTICULO "
                + " left join oc_unidad_costo uc on uc.ID = r.OC_unidad_costo"
                + " left join OC_TAREA ot on ot.ID = r.OC_TAREA "
                + " left join OC_NOMBRE_TAREA nt on nt.ID = ot.OC_NOMBRE_TAREA "
                + " left join OC_codigo_TAREA ct on ct.ID = ot.OC_codigo_TAREA "
                + " left join SI_UNIDAD cu on cu.ID = art.UNIDAD "
                + " left join proyecto_Ot pot on pot.id = r.proyecto_Ot "
                + " left join oc_subtarea st on st.id = r.oc_subtarea"
                + " left join oc_codigo_subtarea cst on cst.id = st.oc_codigo_subtarea "
                + " left join OC_ACTIVIDADPETROLERA ap on ap.id = ot.OC_ACTIVIDADPETROLERA"
                + " where q.id = " + idRequisicion
                + " group by MULTIPROYECTO_ID,reqID,numParte,cantidad_solicitada,cantidad_autorizada,reqUnidad,artDesc,autorizado,disgregado,r.OC_unidad_costo,unidadCostoNombre,artUnidadID,nombreTarea,codigoTarea,artNombre,INV_ARTICULO,PRECIO_UNITARIO,IMPORTE,textNav,unidadCostoID,unidadCostoNombreN,subTareaNombre,actPetrolNombre,actPetrolCodigo,actPetrolID,subTareaCodigo,unidadMedidaNombre,r.observaciones ";
        //
        LOGGER.info("Q: items analista" + s);
        //Retorna el resultado del Query en este caso una lista
        List<RequisicionDetalleVO> lo = null;
        List<Object[]> l = em.createNativeQuery(s).getResultList();
        if (l != null) {
            lo = new ArrayList<>();
            for (Object[] objects : l) {
                lo.add(castRequisicionDetalleNativaMulti(objects, seleccionado));
            }
        }
        return lo;
    }

    private RequisicionDetalleVO castRequisicionDetalleNativaMulti(Object[] objects, boolean seleccionado) {
        RequisicionDetalleVO o;
        o = new RequisicionDetalleVO();
        //o.setIdRequisicionDetalle((Integer) objects[0]);
        o.setIdRequisicion((Integer) (objects[0]));
        o.setArtNumeroParte(String.valueOf(objects[1]));
        o.setCantidadSolicitada(objects[2] != null ? (Double) objects[2] : Constantes.CERO);
        o.setCantidadAutorizada(objects[3] != null ? (Double) objects[3] : Constantes.CERO);
        o.setArtDescripcion((String) objects[5]);
        o.setAutorizado((Boolean) objects[6]);
        o.setDisgregado((Boolean) objects[7]);
        o.setObservacion((String) objects[8]);

        o.setArtIdUnidad((Integer) objects[11]);
        o.setArtUnidad((String) objects[26]);

        o.setIdUnidadCosto(objects[9] != null ? (Integer) objects[9] : 0);
        o.setUnidadCosto(objects[10] != null ? (String) objects[10] : "");
        o.setNombreTarea(objects[12] != null ? (String) objects[12] : "");

        o.setCodeTarea(objects[13] != null ? (String) objects[13] : "");
        o.setArtNombre((String) objects[14]);
        o.setArtID((Integer) (objects[15]) != null ? (Integer) (objects[15]) : 0);

//        o.setIdMoneda((Integer) (objects[16]) != null ? (Integer) (objects[16]) : 0);
//        o.setMoneda((String) objects[17]);
        o.setPrecioUnitario((Double) (objects[16]) != null ? (Double) (objects[16]) : 0.0);
        o.setImporte((Double) (objects[17]) != null ? (Double) (objects[17]) : 0.0);
        o.setTextNav((String) objects[18]);

        o.setIdTipoTarea(objects[19] != null ? (Integer) objects[19] : 0);
        o.setTipoTarea((String) objects[20]);

        o.setSubTarea((String) objects[21]);

        o.setSelected(seleccionado);

        o.setNombreActPedrolera(objects[22] != null ? (String) objects[22] : "");
        o.setCodigoActPedrolera(objects[23] != null ? (String) objects[23] : "");
        o.setIdActPedrolera((Integer) (objects[24]) != null ? (Integer) (objects[24]) : 0);
        o.setCodigoSubTarea(objects[25] != null ? (String) objects[25] : "");
        o.setIdAgrupador((Integer) (objects[27]) != null ? (Integer) (objects[27]) : 0);
        o.setMultiProyectos(this.getItemsMultiProyecto(o.getIdRequisicion(), o.getIdAgrupador()));
        return o;
    }

    private RequisicionDetalleVO castRequisicionDetalleNativa(Object[] objects, boolean seleccionado) {
        RequisicionDetalleVO o;
        o = new RequisicionDetalleVO();
        o.setIdRequisicionDetalle((Integer) objects[0]);
        o.setIdRequisicion((Integer) (objects[1]));
        o.setArtNumeroParte(String.valueOf(objects[2]));
        o.setCantidadSolicitada(objects[3] != null ? (Double) objects[3] : Constantes.CERO);
        o.setCantidadAutorizada(objects[4] != null ? (Double) objects[4] : Constantes.CERO);
        o.setArtDescripcion((String) objects[6]);
        o.setAutorizado((Boolean) objects[7]);
        o.setDisgregado((Boolean) objects[8]);
        o.setObservacion((String) objects[9]);
        //
        if (objects[12] == null) {
            o.setArtUnidad((String) objects[5]);
            o.setArtIdUnidad(0);
        } else {
            SiUnidad uni = ocUnidadRemote.find((Integer) objects[12]);
            o.setArtIdUnidad(uni.getId());
            o.setArtUnidad(uni.getNombre());
        }
        o.setIdUnidadCosto(objects[10] != null ? (Integer) objects[10] : Constantes.CERO);
        o.setUnidadCosto(objects[11] != null ? (String) objects[11] : "");
        o.setNombreTarea(objects[13] != null ? (String) objects[13] : "");
        o.setIdTarea(objects[14] != null ? (Integer) objects[14] : Constantes.CERO);
        o.setCodeTarea(objects[15] != null ? (String) objects[15] : "");
        o.setArtNombre((String) objects[16]);
        o.setArtID((Integer) (objects[17]) != null ? (Integer) (objects[17]) : Constantes.CERO);
        o.setPrecioUnitario((Double) (objects[18]) != null ? (Double) (objects[18]) : 0.0);
        o.setImporte((Double) (objects[19]) != null ? (Double) (objects[19]) : 0.0);
        o.setTextNav((String) objects[20]);
        o.setIdProyectoOt(objects[21] != null ? (Integer) objects[21] : Constantes.CERO);
        o.setProyectoOt((String) objects[22]);
        o.setIdTipoTarea(objects[23] != null ? (Integer) objects[23] : Constantes.CERO);
        o.setTipoTarea((String) objects[24]);
        o.setIdSubTarea(objects[25] != null ? (Integer) objects[25] : Constantes.CERO);
        o.setSubTarea((String) objects[26]);
        o.setProyectoOtCC((String) objects[27]);
        o.setSelected(seleccionado);
        o.setNombreActPedrolera(objects[28] != null ? (String) objects[28] : "");
        o.setCodigoActPedrolera(objects[29] != null ? (String) objects[29] : "");
        o.setIdActPedrolera((Integer) (objects[30]) != null ? (Integer) (objects[30]) : 0);
        o.setCodigoSubTarea(objects[31] != null ? (String) objects[31] : "");
        //
        o.setIdActPedrolera((Integer) (objects[32]) != null ? (Integer) (objects[32]) : 0);
        o.setIdpresupuesto((Integer) (objects[33]) != null ? (Integer) (objects[33]) : 0);
        o.setMesPresupuesto((Integer) (objects[34]) != null ? (Integer) (objects[34]) : 0);
        o.setIdOcCodigoTarea((Integer) (objects[35]) != null ? (Integer) (objects[35]) : 0);
        o.setIdOcCodigoSubtarea((Integer) (objects[36]) != null ? (Integer) (objects[36]) : 0);
        o.setPresupuestoNombre((String) objects[37]);
        o.setPresupuestoCodigo((String) objects[38]);
        o.setAnioPresupuesto((Integer) (objects[39]) != null ? (Integer) (objects[39]) : 0);
        o.setTotalInventario(objects[40] != null ? ((BigDecimal) (objects[40])).doubleValue() : 0);

        return o;
    }

    public void crearDetalleRequisicion(String idSesion, RequisicionDetalleVO requisicionDetalleVO, int idRequision) {
        RequisicionDetalle requisicionDetalle = new RequisicionDetalle();
        requisicionDetalle.setRequisicion(new Requisicion(idRequision));
        if (requisicionDetalleVO.getArtIdUnidad() != 0) {
            requisicionDetalle.setSiUnidad(new SiUnidad(requisicionDetalleVO.getArtIdUnidad()));
        } else {
            requisicionDetalle.setUnidad(requisicionDetalleVO.getArtUnidad());
        }
        if (requisicionDetalleVO.getIdTarea() > 0) {
            requisicionDetalle.setOcTarea(new OcTarea(requisicionDetalleVO.getIdTarea()));
        }
        requisicionDetalle.setCantidadSolicitada(requisicionDetalleVO.getCantidadSolicitada());
        requisicionDetalle.setCantidadAutorizada(requisicionDetalleVO.getCantidadAutorizada());
        requisicionDetalle.setAutorizado(Constantes.BOOLEAN_TRUE);
        requisicionDetalle.setObservaciones(requisicionDetalleVO.getObservacion());
        requisicionDetalle.setDisgregado(Constantes.BOOLEAN_FALSE);
        requisicionDetalle.setGenero(new Usuario(idSesion));
        requisicionDetalle.setFechaGenero(new Date());
        requisicionDetalle.setHoraGenero(new Date());
        requisicionDetalle.setEliminado(Constantes.NO_ELIMINADO);
        requisicionDetalle.setInvArticulo(new InvArticulo(requisicionDetalleVO.getArtID()));

        if (requisicionDetalleVO.getIdProyectoOt() > 0) {
            requisicionDetalle.setProyectoOt(new ProyectoOt(requisicionDetalleVO.getIdProyectoOt()));
        }

        if (requisicionDetalleVO.getIdTipoTarea() > 0) {
            requisicionDetalle.setOcUnidadCosto(new OcUnidadCosto(requisicionDetalleVO.getIdTipoTarea()));
        }
        create(requisicionDetalle);
    }

    public void cambiarDisgregadoItemRequisicion(RequisicionDetalleVO requisicionDetalleVO, String idSesion, boolean disgregado) {
        if (requisicionDetalleVO != null) {
            if (requisicionDetalleVO.getIdRequisicionDetalle() > 0) {
                RequisicionDetalle requisicionDetalle = find(requisicionDetalleVO.getIdRequisicionDetalle());
                disgregarItem(requisicionDetalle, idSesion, disgregado);

            } else if (requisicionDetalleVO.getIdRequisicionDetalle() == 0
                    && requisicionDetalleVO.getIdAgrupador() > 0) {
                for (RequisicionDetalle itemMulti : this.getItemsPorRequisicion(requisicionDetalleVO.getIdRequisicion(), requisicionDetalleVO.getIdAgrupador())) {
                    disgregarItem(itemMulti, idSesion, disgregado);
                }
            }
        }
    }

    private void disgregarItem(RequisicionDetalle requisicionDetalle, String idSesion, boolean disgregado) {
        requisicionDetalle.setDisgregado(disgregado);
        requisicionDetalle.setModifico(new Usuario(idSesion));
        requisicionDetalle.setFechaModifico(new Date());
        requisicionDetalle.setHoraModifico(new Date());
        requisicionDetalle.setEliminado(Constantes.NO_ELIMINADO);
        edit(requisicionDetalle);
    }

    public void limpiarTareaItems(int idRequisicionDetalle, int proyOTID, int ocUnidadCID, String idSesion) {
        RequisicionDetalle requisicionDetalle = find(idRequisicionDetalle);
        if (proyOTID > 0) {
            requisicionDetalle.setProyectoOt(proyectoOtRemote.find(proyOTID));
            requisicionDetalle.setOcUnidadCosto(null);
            requisicionDetalle.setOcTarea(null);
        } else {
            requisicionDetalle.setProyectoOt(null);
            requisicionDetalle.setOcUnidadCosto(null);
            requisicionDetalle.setOcTarea(null);
        }
        if (ocUnidadCID > 0) {
            requisicionDetalle.setOcUnidadCosto(ocUnidadCostoRemote.find(ocUnidadCID));
            requisicionDetalle.setOcTarea(null);
        } else {
            requisicionDetalle.setOcUnidadCosto(null);
            requisicionDetalle.setOcTarea(null);
        }

        requisicionDetalle.setModifico(new Usuario(idSesion));
        requisicionDetalle.setFechaModifico(new Date());
        requisicionDetalle.setHoraModifico(new Date());
        requisicionDetalle.setEliminado(Constantes.NO_ELIMINADO);
        edit(requisicionDetalle);
    }

    public boolean tieneInvArticulo(Object idRequisicion, boolean onlyInvArticulo) {
        boolean tiene = false;
        try {
            //Crea una instancia de Query
            String s = " select count(q.INV_ARTICULO), (select count(id) from REQUISICION_DETALLE where INV_ARTICULO is null and REQUISICION = " + idRequisicion + ")"
                    + " FROM REQUISICION_DETALLE q "
                    + " where q.REQUISICION = " + idRequisicion;
            //
            Object[] obj = (Object[]) em.createNativeQuery(s).getSingleResult();

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

    public boolean articuloEnRequisicion(int idArt) {
        boolean tiene = false;
        try {
            //Crea una instancia de Query
            String s = " select * from ORDEN_DETALLE where INV_ARTICULO = " + idArt;
            List<Object[]> obj = em.createNativeQuery(s).getResultList();
            if (obj != null && !obj.isEmpty()) {
                tiene = true;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return tiene;
    }

    public List<RequisicionDetalleVO> getItemPorIdConsultaNativa(Object idRequisicionDet, int idAgrupador) {
        //Crea una instancia de Query
        StringBuilder sb = new StringBuilder();
        sb.append(consulta())
                .append(" WHERE q.id = ").append(idRequisicionDet);
        if (idAgrupador > 0) {
            sb.append(" and r.MULTIPROYECTO_ID = ").append(idAgrupador);
        }
        sb.append(" ORDER BY r.id ASC");
        //
        List<RequisicionDetalleVO> lo = null;
        List<Object[]> l = em.createNativeQuery(sb.toString()).getResultList();
        if (l != null && l.size() > 0) {
            lo = new ArrayList<>();
            for (Object[] objects : l) {
                lo.add(castRequisicionDetalleNativa(objects, false));
            }
        }
        return lo;
    }

    private String consulta() {
        String s = "SELECT r.id, q.id, case when r.INV_ARTICULO > 0 then art.CODIGO_INT else r.NUMERO_PARTE end, "
                + " r.cantidad_solicitada, r.cantidad_autorizada, r.unidad, "
                + " case when r.INV_ARTICULO > 0 then art.descripcion else r.DESCRIPCION_SOLICITANTE end, "
                + " r.autorizado, r.disgregado,r.observaciones,r.OC_unidad_costo, uc.nombre, "
                + " case when r.INV_ARTICULO > 0 then art.unidad else r.SI_UNIDAD end,"
                + " nt.nombre, ot.id, ct.nombre, art.nombre, r.INV_ARTICULO, "
                + " r.PRECIO_UNITARIO, r.IMPORTE, r.textNav, "
                + " pot.id, pot.nombre, uc.id, uc.nombre, st.id, cst.nombre, pot.CUENTA_CONTABLE "
                + " , ap.NOMBRE, ap.CODIGO, ap.id, cst.CODIGO, r.OC_ACTIVIDADPETROLERA, r.oc_presupuesto, r.mes_presupuesto, "
                + " r.oc_codigo_tarea, r.oc_codigo_subtarea, pres.nombre, pres.codigo, r.anio_presupuesto "
                + " ,\n"
                + " (SELECT inv.numero_unidades from  inv_inventario  inv\n"
                + "									inner join inv_articulo a on inv.articulo = a.id\n"
                + "									inner join inv_almacen al on inv.almacen = al.id and al.eliminado = false \n"
                + "								where inv.eliminado = false and r.inv_articulo = a.id and al.id = apc.inv_almacen order by inv.numero_unidades desc limit 1 )"
                + " FROM Requisicion q "
                + "   inner join ap_campo apc on apc.id = q.ap_campo "
                + "   inner join Requisicion_Detalle r on r.REQUISICION = q.ID "
                + "   left join INV_ARTICULO art on art.ID = r.INV_ARTICULO  and  art.ELIMINADO = 'False'"
                + "   left join oc_unidad_costo uc on uc.ID = r.OC_unidad_costo"
                + "   left join OC_TAREA ot on ot.ID = r.OC_TAREA "
                + "   left join OC_NOMBRE_TAREA nt on nt.ID = ot.OC_NOMBRE_TAREA "
                + "   left join OC_codigo_TAREA ct on ct.ID = ot.OC_codigo_TAREA "
                + "   left join SI_UNIDAD cu on cu.ID = art.UNIDAD "
                + "   left join proyecto_Ot pot on pot.id = r.proyecto_Ot "
                + "   left join oc_subtarea st on st.id = r.oc_subtarea"
                + "   left join oc_codigo_subtarea cst on cst.id = st.oc_codigo_subtarea "
                + "   left join OC_ACTIVIDADPETROLERA ap on ap.id = r.OC_ACTIVIDADPETROLERA"
                + "   left join oc_presupuesto pres on pres.id = r.oc_presupuesto ";
        return s;
    }

    public Map<String, List<RequisicionDetalleVO>> traerPartidasConConvenio(int idRequisicion, int idCampo) {
        String c = "SELECT DISTINCT contrato, articulo, req_det, multi, requ from (\n"
                + "SELECT c.codigo as contrato,  a.codigo as articulo, rd.id as req_det, rd.multiproyecto_id as multi, rd.requisicion as requ, sum(ca.precio_unitario) from requisicion_detalle rd\n"
                + "	inner join inv_articulo a on rd.inv_articulo = a.id\n"
                + "	inner join cv_convenio_articulo ca on ca.inv_articulo =a.id\n"
                + "	inner join convenio c on ca.convenio = c.id\n"
                + " where rd.requisicion = ?1 \n"
                + "  and ap_campo = ?2 \n"
                + "  and rd.eliminado = false \n"
                + "  and rd.disgregado  = false "
                + " GROUP by ROLLUP (c.codigo, a.codigo, rd.id), rd.multiproyecto_id , rd.requisicion \n"
                + " order by c.codigo\n"
                + " ) as contrato_art where articulo is not null and req_det is not null\n"
                + " union\n"
                + " (\n"
                + " SELECT coalesce(c.codigo, ''), a.codigo, rd.id, rd.multiproyecto_id, rd.requisicion from inv_articulo a \n"
                + "		inner join requisicion_detalle rd on rd.inv_articulo = a.id\n"
                + "		left join cv_convenio_articulo ca on ca.inv_articulo = a.id\n"
                + "		left join convenio c on ca.convenio = c.id\n"
                + "	where rd.requisicion = ?3	\n"
                + "	and rd.eliminado = false\n"
                + "     and rd.disgregado  = false "
                + " )order by contrato ";
        List<Object[]> lo = em.createNativeQuery(c).setParameter(1, idRequisicion).setParameter(2, idCampo).setParameter(3, idRequisicion).getResultList();
        List<RequisicionDetalleVO> lista = new ArrayList<>();
        for (Object[] objects : lo) {
            RequisicionDetalleVO rdvo = new RequisicionDetalleVO();
            if (((String) objects[0]).isEmpty()) {
                rdvo.setConvenio(Constantes.OCS_SIN_CONTRATO);
            } else {
                rdvo.setConvenio((String) objects[0]);
            }
            rdvo.setArtNumeroParte((String) objects[1]);
            rdvo.setIdRequisicionDetalle((Integer) objects[2]);
            rdvo.setIdAgrupador(objects[3] != null ? (Integer) objects[3] : Constantes.CERO);
            rdvo.setIdRequisicion((Integer) objects[4]);
            //
            lista.add(rdvo);
        }
        Set<String> artNumParte = new HashSet<>();
        for (RequisicionDetalleVO requisicionDetalleVO : lista) {
            artNumParte.add(requisicionDetalleVO.getArtNumeroParte() + "@@" + (requisicionDetalleVO.getIdAgrupador() > 0 ? requisicionDetalleVO.getIdAgrupador() : requisicionDetalleVO.getIdRequisicionDetalle()));
        }
        Map<String, List<RequisicionDetalleVO>> mapa = new HashMap();
        //
        List<RequisicionDetalleVO> lTempSinContrato;
        List<RequisicionDetalleVO> lTempConContrato;
        lTempSinContrato = new ArrayList<>();
        lTempConContrato = new ArrayList<>();
        for (String artNumP : artNumParte) {
            for (RequisicionDetalleVO requisicionDetalleVO : lista) {
                String auxComp = requisicionDetalleVO.getArtNumeroParte() + "@@" + (requisicionDetalleVO.getIdAgrupador() > 0 ? requisicionDetalleVO.getIdAgrupador() : requisicionDetalleVO.getIdRequisicionDetalle());
                if (auxComp.equals(artNumP)) {
                    requisicionDetalleVO.setDisgregado(Constantes.BOOLEAN_TRUE);
                    if (requisicionDetalleVO.getConvenio().equals(Constantes.OCS_SIN_CONTRATO)) {
                        lTempSinContrato.add(requisicionDetalleVO);
                    } else {
                        lTempConContrato.add(requisicionDetalleVO);
                    }
                    break;
                }
            }
        }
        if (!lTempSinContrato.isEmpty()) {
            mapa.put(Constantes.OCS_SIN_CONTRATO, lTempSinContrato);
        }
        if (!lTempConContrato.isEmpty()) {
            mapa.put("OCS_CON_CONTRATO", lTempConContrato);
        }
        return mapa;
    }

    public boolean validarPartidas(Object idRequisicion) {
        boolean continuar = false;
        try {
            String s = " select case when (c.tipo = 'C' and d.oc_tarea > 0 and d.oc_actividadpetrolera > 0 and d.oc_presupuesto > 0 and d.anio_presupuesto > 0 and d.mes_presupuesto > 0 and d.proyecto_ot > 0 and d.oc_unidad_costo > 0 and d.oc_codigo_subtarea > 0 and d.oc_codigo_tarea > 0) then 1  "
                    + " when (c.tipo = 'N' and a.tipo = 'PS' and d.oc_tarea > 0 and d.proyecto_ot > 0 and d.oc_unidad_costo > 0) then 2  "
                    + " when (c.tipo = 'N' and a.tipo = 'AF' and d.proyecto_ot > 0) then 3 "
                    + " else 4 end AS validaTipo "
                    + " from requisicion a "
                    + " inner join ap_campo c on c.id = a.ap_campo "
                    + " inner join requisicion_detalle d on d.requisicion = a.id "
                    + " where a.id = " + idRequisicion
                    + " group by validaTipo ";

            Object obj = (Object) em.createNativeQuery(s).getSingleResult();

            if ((Integer) obj < 4) {
                continuar = true;
            }
        } catch (Exception e) {
            continuar = false;
        }
        return continuar;
    }
}
