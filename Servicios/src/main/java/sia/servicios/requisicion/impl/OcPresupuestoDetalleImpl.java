/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.OcPresupuestoDetalle;
import sia.modelo.presupuesto.vo.PresupuestoDetVO;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.requisicion.vo.CodigoSubtareaVO;
import sia.modelo.requisicion.vo.OcActividadVO;
import sia.modelo.requisicion.vo.OcSubtareaVO;
import sia.modelo.requisicion.vo.OcTareaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@LocalBean 
public class OcPresupuestoDetalleImpl extends AbstractFacade<OcPresupuestoDetalle>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcPresupuestoDetalleImpl() {
        super(OcPresupuestoDetalle.class);
    }

    
    public List<OcActividadVO> getActividadesVOs(int presupuesto, int anio, int mes, boolean all) {
        UtilLog4j.log.info(this, "#getActividadesVOs ");
        ArrayList<OcActividadVO> lst = new ArrayList<OcActividadVO>();
        try {
            String query = "select a.oc_actividadpetrolera, ap.codigo, ap.nombre "
                    + " from oc_presupuesto_detalle a "
                    + " inner join oc_actividadpetrolera ap on ap.id = a.oc_actividadpetrolera and ap.eliminado = false "
                    + " where a.eliminado =  false "
                    + " and a.oc_presupuesto = " + presupuesto;

            if (anio > 0) {
                query += " and a.anio_presupuesto = " + anio;
            }

            if (mes > 0) {
                query += " and a.mes = " + mes;
            }
            query += " group by a.oc_actividadpetrolera, ap.codigo, ap.nombre "
                    + " order by ap.codigo ";

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            OcActividadVO vo = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    vo = new OcActividadVO();
                    vo.setId((Integer) objects[0]);
                    vo.setCodigo((String) objects[1]);
                    vo.setNombre((String) objects[2]);
                    lst.add(vo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<OcActividadVO>();
        }
        return lst;
    }

    
    public List<SelectItem> getActividadesItems(int presupuesto, int anio, int mes, boolean all) {
        UtilLog4j.log.info(this, "#getActividadesItems ");
        ArrayList<SelectItem> lst = new ArrayList<>();
        try {
            String query = "select a.oc_actividadpetrolera, ap.codigo, ap.nombre "
                    + " from oc_presupuesto_detalle a "
                    + " inner join oc_actividadpetrolera ap on ap.id = a.oc_actividadpetrolera and ap.eliminado = false "
                    + " where a.eliminado =  false "
                    + " and a.oc_presupuesto = " + presupuesto;

            if (anio > 0) {
                query += " and a.anio_presupuesto = " + anio;
            }

            if (mes > 0) {
                query += " and a.mes = " + mes;
            }
            query += " group by a.oc_actividadpetrolera, ap.codigo, ap.nombre "
                    + " order by ap.codigo ";

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            SelectItem item = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    item = new SelectItem((Integer) objects[0], (String) objects[2]);
                    lst.add(item);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<>();
        }
        return lst;
    }

    
    public List<ProyectoOtVo> getProyectoOtVOs(int presupuesto, int actividadID, int apCampoID, int anio, int mes, boolean all) {
        UtilLog4j.log.info(this, "#getProyectoOtVOs ");
        ArrayList<ProyectoOtVo> lst = new ArrayList<>();
        try {
            String query = "select pot.id, pot.nombre, pot.cuenta_contable "
                    + " from oc_presupuesto_detalle a  "
                    + " inner join oc_tarea t on t.oc_codigo_tarea = a.oc_codigo_tarea and t.eliminado = false "
                    + " inner join proyecto_ot pot on pot.id = t.proyecto_ot and pot.eliminado = false and pot.abierto = true and pot.ap_campo = " + apCampoID
                    + " where a.eliminado =  false "
                    + " and a.oc_presupuesto = " + presupuesto
                    + " and a.oc_actividadpetrolera = " + actividadID;
            if (anio > 0) {
                query += " and a.anio_presupuesto = " + anio;
            }
            if (mes > 0) {
                query += " and a.mes = " + mes;
            }
            query += " group by pot.id, pot.nombre, pot.cuenta_contable "
                    + " order by pot.nombre ";

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            ProyectoOtVo vo = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    vo = new ProyectoOtVo();
                    vo.setId((Integer) objects[0]);
                    vo.setNombre((String) objects[1]);
                    vo.setCuentaContable((String) objects[2]);
                    lst.add(vo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<>();
        }
        return lst;
    }

    
    public List<SelectItem> getProyectoOtItems(int presupuesto, int actividadID, int apCampoID, int anio, int mes, boolean except) {
        UtilLog4j.log.info(this, "#getProyectoOtItems ");
        ArrayList<SelectItem> lst = new ArrayList<SelectItem>();
        try {
            String query = "";
            if (except) {
                query = " select p.id, p.nombre, p.cuenta_contable "
                        + " from proyecto_ot p "
                        + " where p.eliminado = false "
                        + " and p.ap_campo = " + apCampoID
                        + " except "
                        + " select pot.id, pot.nombre, pot.cuenta_contable "
                        + " from oc_presupuesto_detalle a  "
                        + " inner join oc_tarea t on t.oc_codigo_tarea = a.oc_codigo_tarea and t.eliminado = false "
                        + " inner join oc_codigo_subtarea cs on cs.id = a.oc_codigo_subtarea and cs.eliminado = false "
                        + " inner join proyecto_ot pot on pot.id = t.proyecto_ot and pot.eliminado = false and pot.abierto = true and pot.ap_campo = " + apCampoID
                        + " inner join oc_subtarea st on st.oc_tarea = t.id and st.eliminado = false and st.oc_codigo_subtarea = cs.id "
                        + " where a.eliminado =  false "
                        + " and a.oc_presupuesto = " + presupuesto //+ " and a.oc_actividadpetrolera = " + actividadID
                        ;
            } else {
                query = " select pot.id, pot.nombre, pot.cuenta_contable "
                        + " from oc_presupuesto_detalle a  "
                        + " inner join oc_tarea t on t.oc_codigo_tarea = a.oc_codigo_tarea and t.eliminado = false "
                        + " inner join oc_codigo_subtarea cs on cs.id = a.oc_codigo_subtarea and cs.eliminado = false "
                        + " inner join proyecto_ot pot on pot.id = t.proyecto_ot and pot.eliminado = false and pot.abierto = true and pot.ap_campo = " + apCampoID
                        + " inner join oc_subtarea st on st.oc_tarea = t.id and st.eliminado = false and st.oc_codigo_subtarea = cs.id "
                        + " where a.eliminado =  false "
                        + " and a.oc_presupuesto = " + presupuesto //+ " and a.oc_actividadpetrolera = " + actividadID
                        ;
            }
            if (actividadID > 0) {
                query += " and a.oc_actividadpetrolera = " + actividadID;
            }

            if (anio > 0) {
                query += " and a.anio_presupuesto = " + anio;
            }
            if (mes > 0) {
                query += " and a.mes = " + mes;
            }

            query += " group by pot.id, pot.nombre, pot.cuenta_contable "
                    + " order by nombre ";

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            SelectItem item = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    item = new SelectItem((Integer) objects[0], (String) objects[1]);
                    lst.add(item);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<SelectItem>();
        }
        return lst;
    }

    
    public List<OcTareaVo> getUnidadCostosVOs(int presupuesto, int actividadID, int apCampoID, int proyectoOtID, int anio, int mes, boolean all) {
        UtilLog4j.log.info(this, "#getUnidadCostosVOs ");
        ArrayList<OcTareaVo> lst = new ArrayList<>();
        try {
            String query = "select a.oc_unidad_costo, uc.nombre, uc.codigo "
                    + " from oc_presupuesto_detalle a  "
                    + " inner join oc_tarea t on t.oc_codigo_tarea = a.oc_codigo_tarea and t.eliminado = false "
                    + " inner join proyecto_ot pot on pot.id = t.proyecto_ot and pot.eliminado = false and pot.abierto = true and pot.ap_campo = " + apCampoID
                    + " inner join oc_unidad_costo uc on uc.id = a.oc_unidad_costo and uc.eliminado = false "
                    + " where a.eliminado =  false "
                    + " and a.oc_presupuesto = " + presupuesto
                    + " and a.oc_actividadpetrolera =  " + actividadID;

            if (proyectoOtID > 0) {
                query += " and t.proyecto_ot =  " + proyectoOtID;
            }

            if (anio > 0) {
                query += " and a.anio_presupuesto = " + anio;
            }

            if (mes > 0) {
                query += " and a.mes = " + mes;
            }

            query += " group by a.oc_unidad_costo, uc.nombre, uc.codigo "
                    + " order by uc.codigo ";

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            OcTareaVo vo = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    vo = new OcTareaVo();
                    vo.setIdUnidadCosto((Integer) objects[0]);
                    vo.setUnidadCosto((String) objects[1]);
                    lst.add(vo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<>();
        }
        return lst;
    }

    
    public List<SelectItem> getUnidadCostosItems(int presupuesto, int actividadID, int apCampoID, int proyectoOtID, int anio, int mes, boolean all) {
        UtilLog4j.log.info(this, "#getUnidadCostosItems ");
        ArrayList<SelectItem> lst = new ArrayList<>();
        try {
            String query = "select a.oc_unidad_costo, uc.nombre, uc.codigo "
                    + " from oc_presupuesto_detalle a  "
                    + " inner join oc_tarea t on t.oc_codigo_tarea = a.oc_codigo_tarea and t.eliminado = false "
                    + " inner join proyecto_ot pot on pot.id = t.proyecto_ot and pot.eliminado = false and pot.abierto = true and pot.ap_campo = " + apCampoID
                    + " inner join oc_unidad_costo uc on uc.id = a.oc_unidad_costo and uc.eliminado = false "
                    + " where a.eliminado =  false "
                    + " and a.oc_presupuesto = " + presupuesto
                    + " and a.oc_actividadpetrolera =  " + actividadID;

            if (proyectoOtID > 0) {
                query += " and t.proyecto_ot =  " + proyectoOtID;
            }

            if (anio > 0) {
                query += " and a.anio_presupuesto = " + anio;
            }

            if (mes > 0) {
                query += " and a.mes = " + mes;
            }

            query += " group by a.oc_unidad_costo, uc.nombre, uc.codigo "
                    + " order by uc.codigo ";

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            SelectItem item = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    item = new SelectItem((Integer) objects[0], (String) objects[1]);
                    lst.add(item);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<>();
        }
        return lst;
    }

    
    public List<SelectItem> getMesesItems(int presupuesto, int anio, boolean all) {
        UtilLog4j.log.info(this, "#getMesesItems ");
        ArrayList<SelectItem> lst = new ArrayList<>();
        try {
            String query = " select a.mes, 'Mes '||a.mes "
                    + " from oc_presupuesto_detalle a  "
                    + " where a.oc_presupuesto = " + presupuesto
                    + " and a.anio_presupuesto = " + anio
                    + " group by a.mes "
                    + " order by a.mes ";

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            SelectItem item = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    item = new SelectItem((Integer) objects[0], (String) objects[1]);
                    lst.add(item);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<>();
        }
        return lst;
    }

    
    public List<SelectItem> getAniosItems(int presupuesto, boolean all) {
        UtilLog4j.log.info(this, "#getAniosItems ");
        ArrayList<SelectItem> lst = new ArrayList<>();
        try {
            String query = " select a.anio_presupuesto, 'Año '||a.anio_presupuesto "
                    + " from oc_presupuesto_detalle a  "
                    + " where a.oc_presupuesto = " + presupuesto
                    + " group by a.anio_presupuesto "
                    + " order by a.anio_presupuesto ";

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            SelectItem item = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    item = new SelectItem((Integer) objects[0], (String) objects[1]);
                    lst.add(item);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<>();
        }
        return lst;
    }

    
    public List<OcTareaVo> getTaresVOs(int presupuesto, int actividadID, int apCampoID, int proyectoOtID, int unidadCosto, int anio, int mes, boolean all) {
        UtilLog4j.log.info(this, "#getTaresVOs ");
        ArrayList<OcTareaVo> lst = new ArrayList<OcTareaVo>();
        try {
            String query = "select a.oc_codigo_tarea, ct.nombre, nt.nombre "
                    + " from oc_presupuesto_detalle a  "
                    + " inner join oc_tarea t on t.oc_codigo_tarea = a.oc_codigo_tarea and t.eliminado = false "
                    + " inner join proyecto_ot pot on pot.id = t.proyecto_ot and pot.eliminado = false and pot.abierto = true and pot.ap_campo = " + apCampoID
                    + " inner join oc_codigo_tarea ct on ct.id = t.oc_codigo_tarea and ct.eliminado = false "
                    + " inner join oc_nombre_tarea nt on nt.id = t.oc_nombre_tarea and nt.eliminado = false "
                    + " where a.eliminado =  false "
                    + " and a.oc_presupuesto = " + presupuesto
                    + " and a.oc_actividadpetrolera = " + actividadID
                    + " and a.oc_unidad_costo= " + unidadCosto;

            if (proyectoOtID > 0) {
                query += " and t.proyecto_ot =  " + proyectoOtID;
            }

            if (anio > 0) {
                query += " and a.anio_presupuesto = " + anio;
            }

            if (mes > 0) {
                query += " and a.mes = " + mes;
            }

            query += " group by a.oc_codigo_tarea, ct.nombre, nt.nombre ";

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            OcTareaVo vo = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    vo = new OcTareaVo();
                    vo.setIdNombreTarea((Integer) objects[0]);
                    vo.setCodigoTarea((String) objects[1]);
                    vo.setNombreTarea((String) objects[2]);
                    lst.add(vo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<OcTareaVo>();
        }
        return lst;
    }

    
    public List<SelectItem> getTareasItems(int presupuesto, int actividadID, int apCampoID, int proyectoOtID, int unidadCosto, int anio, int mes, boolean all) {
        UtilLog4j.log.info(this, "#getTareasItems ");
        ArrayList<SelectItem> lst = new ArrayList<SelectItem>();
        try {
            String query = "select a.oc_codigo_tarea, ct.nombre, nt.nombre "
                    + " from oc_presupuesto_detalle a  "
                    + " inner join oc_tarea t on t.oc_codigo_tarea = a.oc_codigo_tarea and t.eliminado = false "
                    + " inner join proyecto_ot pot on pot.id = t.proyecto_ot and pot.eliminado = false and pot.abierto = true and pot.ap_campo = " + apCampoID
                    + " inner join oc_codigo_tarea ct on ct.id = t.oc_codigo_tarea and ct.eliminado = false "
                    + " inner join oc_nombre_tarea nt on nt.id = t.oc_nombre_tarea and nt.eliminado = false "
                    + " where a.eliminado =  false "
                    + " and a.oc_presupuesto = " + presupuesto
                    + " and a.oc_actividadpetrolera = " + actividadID
                    + " and a.oc_unidad_costo= " + unidadCosto;

            if (proyectoOtID > 0) {
                query += " and t.proyecto_ot =  " + proyectoOtID;
            }

            if (anio > 0) {
                query += " and a.anio_presupuesto = " + anio;
            }

            if (mes > 0) {
                query += " and a.mes = " + mes;
            }

            query += " group by a.oc_codigo_tarea, ct.nombre, nt.nombre ";

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            SelectItem item = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    item = new SelectItem((Integer) objects[0], (String) objects[2]);
                    lst.add(item);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<SelectItem>();
        }
        return lst;
    }

    
    public List<CodigoSubtareaVO> getSubTaresVOs(int presupuesto, int actividadID, int apCampoID, int proyectoOtID, int unidadCosto, int codigotareaID, int anio, int mes, boolean all) {
        UtilLog4j.log.info(this, "#getSubTaresVOs ");
        ArrayList<CodigoSubtareaVO> lst = new ArrayList<>();
        try {
            String query = "select a.oc_codigo_subtarea, cst.nombre, cst.codigo "
                    + " from oc_presupuesto_detalle a  "
                    + " inner join oc_tarea t on t.oc_codigo_tarea = a.oc_codigo_tarea and t.eliminado = false "
                    + " inner join proyecto_ot pot on pot.id = t.proyecto_ot and pot.eliminado = false and pot.abierto = true and pot.ap_campo = " + apCampoID
                    + " inner join oc_codigo_subtarea cst on cst.id = a.oc_codigo_subtarea and cst.eliminado = false "
                    + " where a.eliminado =  false "
                    + " and a.oc_presupuesto = " + presupuesto
                    + " and a.oc_actividadpetrolera = " + actividadID
                    + " and a.oc_unidad_costo= " + unidadCosto;

            if (proyectoOtID > 0) {
                query += " and t.proyecto_ot =  " + proyectoOtID;
            }

            if (anio > 0) {
                query += " and a.anio_presupuesto = " + anio;
            }

            if (mes > 0) {
                query += " and a.mes = " + mes;
            }
            query += " and a.oc_codigo_tarea = " + codigotareaID
                    + " group by a.oc_codigo_subtarea, cst.nombre, cst.codigo "
                    + " order by cst.codigo ";

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            CodigoSubtareaVO vo = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    vo = new CodigoSubtareaVO();
                    vo.setId((Integer) objects[0]);
                    vo.setNombre((String) objects[1]);
                    vo.setCodigo((String) objects[2]);
                    lst.add(vo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<CodigoSubtareaVO>();
        }
        return lst;
    }

    
    public List<SelectItem> getSubTareasItems(int presupuesto, int actividadID, int apCampoID, int proyectoOtID, int unidadCosto, int codigotareaID, int anio, int mes, boolean all) {
        UtilLog4j.log.info(this, "#getSubTareasItems ");
        ArrayList<SelectItem> lst = new ArrayList<SelectItem>();
        try {
            String query = "select a.oc_codigo_subtarea, cst.nombre, cst.codigo "
                    + " from oc_presupuesto_detalle a  "
                    + " inner join oc_tarea t on t.oc_codigo_tarea = a.oc_codigo_tarea and t.eliminado = false "
                    + " inner join proyecto_ot pot on pot.id = t.proyecto_ot and pot.eliminado = false and pot.abierto = true and pot.ap_campo = " + apCampoID
                    + " inner join oc_codigo_subtarea cst on cst.id = a.oc_codigo_subtarea and cst.eliminado = false "
                    + " inner join oc_subtarea s on s.oc_tarea = t.id and s.oc_codigo_subtarea = cst.id and s.eliminado = false "
                    + " where a.eliminado =  false "
                    + " and a.oc_presupuesto = " + presupuesto
                    + " and a.oc_actividadpetrolera = " + actividadID
                    + " and a.oc_unidad_costo= " + unidadCosto;

            if (proyectoOtID > 0) {
                query += " and t.proyecto_ot =  " + proyectoOtID;
            }

            if (anio > 0) {
                query += " and a.anio_presupuesto = " + anio;
            }

            if (mes > 0) {
                query += " and a.mes = " + mes;
            }
            query += " and a.oc_codigo_tarea = " + codigotareaID
                    + " group by a.oc_codigo_subtarea, cst.nombre, cst.codigo "
                    + " order by cst.codigo ";

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            SelectItem item = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    item = new SelectItem((Integer) objects[0], (String) objects[1]);
                    lst.add(item);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<SelectItem>();
        }
        return lst;
    }

    
    public List<PresupuestoDetVO> getPresupuestoDet(int presupuesto, int apCampoId, int anio, int mes, boolean completo, boolean conMonto, boolean conOT, boolean conOTTxt) {
        UtilLog4j.log.info(this, "#getPresupuestoDet ");
        ArrayList<PresupuestoDetVO> lst = new ArrayList<>();
        try {
            String query = " select  "
                    + " d.oc_presupuesto,d.anio_presupuesto, d.mes "
                    + " ,ac.id, ac.codigo, ac.nombre "
                    + " ,uc.id, uc.codigo, uc.nombre "
                    + " ,ct.id, nt.id, ct.nombre, nt.nombre "
                    + " ,cs.id, cs.codigo, cs.nombre "
                    + " , d.mano_obra_cn, d.mano_obra_ex, d.bienes_cn, d.bienes_ex, d.servicios_cn, d.servicios_ex, d.capacitacion_cn, d.capacitacion_ex, d.trans_tecnologia, d.infraestructura ";

            if (conOT) {
                query += " ,ot.id, ot.nombre, ot.cuenta_contable";
            }

            query += " from oc_presupuesto_detalle d  "
                    + " inner join oc_actividadpetrolera ac on ac.id = d.oc_actividadpetrolera and ac.eliminado = false "
                    + " inner join oc_unidad_costo uc on uc.id = d.oc_unidad_costo and uc.eliminado = false "
                    + " inner join oc_codigo_subtarea cs on cs.id = d.oc_codigo_subtarea and cs.eliminado = false "
                    + " inner join oc_tarea t on t.oc_codigo_tarea = d.oc_codigo_tarea and t.eliminado = false ";

            if (conOT) {
                query += " inner join proyecto_ot ot on ot.id = t.proyecto_ot and ot.eliminado = false ";
            }

            query += " inner join oc_codigo_tarea ct on ct.id = t.oc_codigo_tarea and ct.eliminado = false "
                    + " inner join oc_nombre_tarea nt on nt.id = t.oc_nombre_tarea and nt.eliminado = false "
                    + " inner join oc_subtarea st on st.oc_tarea = t.id and st.eliminado = false and st.oc_codigo_subtarea = cs.id "
                    + " where d.eliminado = false "
                    + " and d.oc_presupuesto = " + presupuesto
                    + " and d.anio_presupuesto = " + anio
                    + " and d.mes = " + mes;

            if (conMonto) {
                query += " and (coalesce(d.mano_obra_cn,0)+ coalesce(d.mano_obra_ex,0)+ coalesce(d.bienes_cn,0)+ coalesce(d.bienes_ex,0)+ coalesce(d.servicios_cn,0) + coalesce(d.servicios_ex,0) + coalesce(d.capacitacion_cn,0) + coalesce(d.capacitacion_ex,0) + coalesce(d.trans_tecnologia,0) + coalesce(d.infraestructura,0)) > 0 ";
            }

            query += " group by  "
                    + " d.oc_presupuesto,d.anio_presupuesto, d.mes "
                    + " ,ac.id, ac.codigo, ac.nombre "
                    + " ,uc.id, uc.codigo, uc.nombre "
                    + " ,ct.id, nt.id, ct.nombre, nt.nombre "
                    + " ,cs.id, cs.codigo, cs.nombre "
                    + " , d.mano_obra_cn, d.mano_obra_ex, d.bienes_cn, d.bienes_ex, d.servicios_cn, d.servicios_ex, d.capacitacion_cn, d.capacitacion_ex, d.trans_tecnologia, d.infraestructura ";

            if (conOT) {
                query += " ,ot.id, ot.nombre, ot.cuenta_contable";
                query += " order by ac.codigo, uc.codigo, ct.nombre, cs.codigo, ot.nombre ";
            } else {
                query += " order by ac.codigo, uc.codigo, ct.nombre, cs.codigo ";
            }

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            PresupuestoDetVO vo = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    vo = new PresupuestoDetVO();
                    if (completo) {
                        vo.setIdPresupuesto((Integer) objects[0]);
                        vo.setMes((Integer) objects[1]);
                        vo.setAnio((Integer) objects[2]);

                        vo.setActPetroleraId((Integer) objects[3]);
                        vo.setActPetroleraCodigo((String) objects[4]);
                        vo.setActPetroleraNombre((String) objects[5]);

                        vo.setUnidadCostoId((Integer) objects[6]);
                        vo.setUnidadCostoCodigo((String) objects[7]);
                        vo.setUnidadCostoNombre((String) objects[8]);

                        vo.setTareaCodigoId((Integer) objects[9]);
                        vo.setTareaNombreId((Integer) objects[10]);
                        vo.setTareaCodigo((String) objects[11]);
                        vo.setTareaNombre((String) objects[12]);

                        vo.setSubTareaCodigoId((Integer) objects[13]);
                        vo.setSubTareaCodigo((String) objects[14]);
                        vo.setSubTareaNombre((String) objects[15]);

                        if (conOTTxt) {
                            vo.setOtsTexto(this.getProyectoOtText(presupuesto,
                                    vo.getActPetroleraId(), apCampoId, anio, mes, vo.getTareaCodigoId(), vo.getSubTareaCodigoId()));
                            vo.setOtsCCTexto(this.getProyectoOtCCText(presupuesto,
                                    vo.getActPetroleraId(), apCampoId, anio, mes, vo.getTareaCodigoId(), vo.getSubTareaCodigoId()));
                        }
                    }
                    vo.setManoObraCn(objects[16] != null ? (BigDecimal) objects[16] : BigDecimal.ZERO);
                    vo.setManoObraEx(objects[17] != null ? (BigDecimal) objects[17] : BigDecimal.ZERO);
                    vo.setBienasCn(objects[18] != null ? (BigDecimal) objects[18] : BigDecimal.ZERO);
                    vo.setBienesEx(objects[19] != null ? (BigDecimal) objects[19] : BigDecimal.ZERO);
                    vo.setServiciosCn(objects[20] != null ? (BigDecimal) objects[20] : BigDecimal.ZERO);
                    vo.setServiciosEx(objects[21] != null ? (BigDecimal) objects[21] : BigDecimal.ZERO);
                    vo.setCapacitacionCn(objects[22] != null ? (BigDecimal) objects[22] : BigDecimal.ZERO);
                    vo.setCapacitacionEx(objects[23] != null ? (BigDecimal) objects[23] : BigDecimal.ZERO);
                    vo.setTransferenciaTec(objects[24] != null ? (BigDecimal) objects[24] : BigDecimal.ZERO);
                    vo.setInfraestructura(objects[25] != null ? (BigDecimal) objects[25] : BigDecimal.ZERO);

                    if (conOT) {
                        vo.setProyectoOtId((Integer) objects[26]);
                        vo.setProyectoOtNombre((String) objects[27]);
                        vo.setProyectoOtCodigo((String) objects[28]);
                    }

                    lst.add(vo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener el detallde del presupuesto" + e.getMessage(), e);
            lst = new ArrayList<>();
        }
        return lst;
    }

    
    public PresupuestoDetVO llenarPresupuestoDet(PresupuestoDetVO vo) {
        UtilLog4j.log.info(this, "#getPresupuestoDet ");
        ArrayList<PresupuestoDetVO> lst = new ArrayList<PresupuestoDetVO>();
        try {
            String query = " select  "
                    + " d.mano_obra_cn, d.mano_obra_ex, d.bienes_cn, d.bienes_ex, d.servicios_cn, d.servicios_ex, d.capacitacion_cn, d.capacitacion_ex, d.trans_tecnologia, d.infraestructura, d.id "
                    + " from oc_presupuesto_detalle d  "
                    + " where d.eliminado = false "
                    + " and d.oc_presupuesto = " + vo.getIdPresupuesto()
                    + " and d.anio_presupuesto = " + vo.getAnio()
                    + " and d.mes = " + vo.getMes()
                    + " and d.oc_actividadpetrolera = " + vo.getActPetroleraId()
                    + " and d.oc_unidad_costo = " + vo.getUnidadCostoId()
                    + " and d.oc_codigo_tarea = " + vo.getTareaCodigoId()
                    + " and d.oc_codigo_subtarea = " + vo.getSubTareaCodigoId();

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();

            if (lo != null) {
                for (Object[] objects : lo) {
                    vo.setManoObraCn(objects[0] != null ? (BigDecimal) objects[0] : BigDecimal.ZERO);
                    vo.setManoObraEx(objects[1] != null ? (BigDecimal) objects[1] : BigDecimal.ZERO);
                    vo.setBienasCn(objects[2] != null ? (BigDecimal) objects[2] : BigDecimal.ZERO);
                    vo.setBienesEx(objects[3] != null ? (BigDecimal) objects[3] : BigDecimal.ZERO);
                    vo.setServiciosCn(objects[4] != null ? (BigDecimal) objects[4] : BigDecimal.ZERO);
                    vo.setServiciosEx(objects[5] != null ? (BigDecimal) objects[5] : BigDecimal.ZERO);
                    vo.setCapacitacionCn(objects[6] != null ? (BigDecimal) objects[6] : BigDecimal.ZERO);
                    vo.setCapacitacionEx(objects[7] != null ? (BigDecimal) objects[7] : BigDecimal.ZERO);
                    vo.setTransferenciaTec(objects[8] != null ? (BigDecimal) objects[8] : BigDecimal.ZERO);
                    vo.setInfraestructura(objects[9] != null ? (BigDecimal) objects[9] : BigDecimal.ZERO);
                    vo.setId(objects[10] != null ? (int) objects[10] : 0);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener el detallde del presupuesto" + e.getMessage(), e);
        }
        return vo;
    }

    
    public List<OcTareaVo> getTareasNuevaOT(int presupuesto, int actividadID, int apCampoID) {
        UtilLog4j.log.info(this, "#getTareasNuevaOT ");
        ArrayList<OcTareaVo> lst = new ArrayList<>();
        try {
            String consulta = " select ct.id, nt.id, uc.id "
                    + " from oc_presupuesto_detalle a   "
                    + " inner join oc_tarea t on t.oc_codigo_tarea = a.oc_codigo_tarea and t.eliminado = false  "
                    + " inner join oc_codigo_tarea ct on ct.id = a.oc_codigo_tarea and ct.eliminado = false "
                    + " inner join oc_nombre_tarea nt on nt.id = t.oc_nombre_tarea and nt.eliminado = false "
                    + " inner join oc_unidad_costo uc on uc.id = t.oc_unidad_costo and uc.eliminado = false "
                    + " inner join proyecto_ot pot on pot.id = t.proyecto_ot and pot.eliminado = false and pot.abierto = true and pot.ap_campo = " + apCampoID
                    + " where a.eliminado =  false  "
                    + " and a.oc_presupuesto = " + presupuesto
                    + " and a.oc_actividadpetrolera = " + actividadID
                    + " group by ct.id, nt.id, uc.id ";

            UtilLog4j.log.info(this, "query" + consulta);

            List<Object[]> lo = em.createNativeQuery(consulta).getResultList();
            OcTareaVo vo = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    vo = new OcTareaVo();
                    vo.setIdcodigoTarea(objects[0] != null ? (int) objects[0] : 0);
                    vo.setIdNombreTarea(objects[1] != null ? (int) objects[1] : 0);
                    vo.setIdUnidadCosto(objects[2] != null ? (int) objects[2] : 0);
                    vo.setIdProyectoOt(apCampoID);
                    vo.setIdActPetrolera(actividadID);

                    lst.add(vo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener el detallde de las nuevas tareas para el nuevo proyecto ligado al presupuesto" + e.getMessage(), e);
            lst = new ArrayList<>();
        }
        return lst;
    }

    
    public List<OcSubtareaVO> getSubTareasNuevaOT(int presupuesto, int apCampoID, OcTareaVo tarea) {
        UtilLog4j.log.info(this, "#getTareasNuevaOT ");
        ArrayList<OcSubtareaVO> lst = new ArrayList<>();
        try {
            String consulta = " select ct.id, nt.id, uc.id, a.oc_codigo_subtarea "
                    + " from oc_presupuesto_detalle a   "
                    + " inner join oc_tarea t on t.oc_codigo_tarea = a.oc_codigo_tarea and t.eliminado = false  "
                    + " inner join oc_codigo_tarea ct on ct.id = a.oc_codigo_tarea and ct.eliminado = false "
                    + " inner join oc_nombre_tarea nt on nt.id = t.oc_nombre_tarea and nt.eliminado = false "
                    + " inner join oc_unidad_costo uc on uc.id = t.oc_unidad_costo and uc.eliminado = false "
                    + " inner join proyecto_ot pot on pot.id = t.proyecto_ot and pot.eliminado = false and pot.abierto = true and pot.ap_campo = " + apCampoID
                    + " where a.eliminado =  false  "
                    + " and a.oc_presupuesto = " + presupuesto
                    + " and a.oc_codigo_tarea = " + tarea.getIdcodigoTarea()
                    + " and a.oc_actividadpetrolera = " + tarea.getIdActPetrolera()
                    + " group by ct.id, nt.id, uc.id, a.oc_codigo_subtarea ";

            UtilLog4j.log.info(this, "query" + consulta);

            List<Object[]> lo = em.createNativeQuery(consulta).getResultList();
            OcSubtareaVO vo = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    vo = new OcSubtareaVO();
                    vo.setIdCodigoSubtarea(objects[3] != null ? (int) objects[3] : 0);

                    lst.add(vo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener el detallde de las nuevas tareas para el nuevo proyecto ligado al presupuesto" + e.getMessage(), e);
            lst = new ArrayList<>();
        }
        return lst;
    }

    
    public String getProyectoOtText(int presupuesto, int actividadID, int apCampoID, int anio, int mes, int tarea, int subtarea) {
        UtilLog4j.log.info(this, "#getProyectoOtText ");
        String retVal = "";
        try {
            String query = " select COALESCE(array_to_string(array_agg(DISTINCT pot.nombre), ', '), '') "
                    + " from oc_presupuesto_detalle a  "
                    + " inner join oc_tarea t on t.oc_codigo_tarea = a.oc_codigo_tarea and t.eliminado = false "
                    + " inner join oc_codigo_subtarea cs on cs.id = a.oc_codigo_subtarea and cs.eliminado = false "
                    + " inner join proyecto_ot pot on pot.id = t.proyecto_ot and pot.eliminado = false and pot.abierto = true and pot.ap_campo = " + apCampoID
                    + " inner join oc_subtarea st on st.oc_tarea = t.id and st.eliminado = false and st.oc_codigo_subtarea = cs.id "
                    + " where a.eliminado =  false "
                    + " and a.oc_presupuesto = " + presupuesto //+ " and a.oc_actividadpetrolera = " + actividadID
                    ;

            if (actividadID > 0) {
                query += " and a.oc_actividadpetrolera = " + actividadID;
            }

            if (anio > 0) {
                query += " and a.anio_presupuesto = " + anio;
            }
            if (mes > 0) {
                query += " and a.mes = " + mes;
            }

            if (tarea > 0) {
                query += " and t.oc_codigo_tarea  = " + tarea;
            }

            if (subtarea > 0) {
                query += " and cs.id = " + subtarea;
            }

            UtilLog4j.log.info(this, "query" + query);

            Object lo = em.createNativeQuery(query).getSingleResult();
            if (lo != null) {
                retVal = (String) lo;
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            retVal = "";
        }
        return retVal;
    }
    
    
    public String getProyectoOtCCText(int presupuesto, int actividadID, int apCampoID, int anio, int mes, int tarea, int subtarea) {
        UtilLog4j.log.info(this, "#getProyectoOtCCText ");
        String retVal = "";
        try {
            String query = " select COALESCE(array_to_string(array_agg(DISTINCT pot.cuenta_contable), ', '), '') "
                    + " from oc_presupuesto_detalle a  "
                    + " inner join oc_tarea t on t.oc_codigo_tarea = a.oc_codigo_tarea and t.eliminado = false "
                    + " inner join oc_codigo_subtarea cs on cs.id = a.oc_codigo_subtarea and cs.eliminado = false "
                    + " inner join proyecto_ot pot on pot.id = t.proyecto_ot and pot.eliminado = false and pot.abierto = true and pot.ap_campo = " + apCampoID
                    + " inner join oc_subtarea st on st.oc_tarea = t.id and st.eliminado = false and st.oc_codigo_subtarea = cs.id "
                    + " where a.eliminado =  false "
                    + " and a.oc_presupuesto = " + presupuesto //+ " and a.oc_actividadpetrolera = " + actividadID
                    ;

            if (actividadID > 0) {
                query += " and a.oc_actividadpetrolera = " + actividadID;
            }

            if (anio > 0) {
                query += " and a.anio_presupuesto = " + anio;
            }
            if (mes > 0) {
                query += " and a.mes = " + mes;
            }

            if (tarea > 0) {
                query += " and t.oc_codigo_tarea  = " + tarea;
            }

            if (subtarea > 0) {
                query += " and cs.id = " + subtarea;
            }

            UtilLog4j.log.info(this, "query" + query);

            Object lo = em.createNativeQuery(query).getSingleResult();
            if (lo != null) {
                retVal = (String) lo;
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            retVal = "";
        }
        return retVal;
    }
    
    
    public int getTareaIDByVO(OcTareaVo vo) {
        UtilLog4j.log.info(this, "#getTareaIDByVO ");
        int retVal = 0;
        try {
            String query = "select t.id "
                    + " from  oc_tarea t  "                    
                    + " where 1 = 1 ";

            if (vo.getIdProyectoOt() > 0) {
                query += " and t.proyecto_ot =  " + vo.getIdProyectoOt();
            }
            
            if (vo.getIdNombreTarea() > 0) {
                query += " and t.oc_nombre_tarea =  " + vo.getIdNombreTarea();
            }
            
            if (vo.getIdcodigoTarea() > 0) {
                query += " and t.oc_codigo_tarea =  " + vo.getIdcodigoTarea();
            }
            
            if (vo.getIdUnidadCosto() > 0) {
                query += " and t.oc_unidad_costo =  " + vo.getIdUnidadCosto();
            }
            
            if (vo.getIdActPetrolera() > 0) {
                query += " and t.oc_actividadpetrolera =  " + vo.getIdActPetrolera();
            }
            
            query += " order by t.eliminado, t.id ";
            query += " limit 1";
            
            UtilLog4j.log.info(this, "query" + query);

            Object lo = em.createNativeQuery(query).getSingleResult();            
            if (lo != null) {
                retVal = (Integer) lo;
            }
            
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener tarea getTareaIDByVO " + e.getMessage(), e);
            retVal = 0;
        }
        return retVal;
    }
    
    
    public int getPresupuestoDetByVO(OcTareaVo vo, int codigoSubtareaID, int presupuestoID, int mes, int anio) {
        UtilLog4j.log.info(this, "#getPresupuestoDetByVO ");
        int retVal = 0;
        String query = "";
        try {
            query = "select d.id "
                    + " from  oc_presupuesto_detalle d  "                    
                    + " where 1 = 1 and d.eliminado = false ";

            if (presupuestoID > 0) {
                query += " and d.oc_presupuesto =  " + presupuestoID;
            }
            
            if (vo.getIdcodigoTarea() > 0) {
                query += " and d.oc_codigo_tarea =  " + vo.getIdcodigoTarea();
            }
            
            if (vo.getIdUnidadCosto() > 0) {
                query += " and d.oc_unidad_costo =  " + vo.getIdUnidadCosto();
            }
            
            if (vo.getIdActPetrolera() > 0) {
                query += " and d.oc_actividadpetrolera =  " + vo.getIdActPetrolera();
            }
            
            if (codigoSubtareaID > 0) {
                query += " and d.oc_codigo_subtarea =  " + codigoSubtareaID;
            }
            
            if (mes > 0) {
                query += " and d.mes =  " + mes;
            }
            
            if (anio > 0) {
                query += " and d.anio_presupuesto =  " + anio;
            }
            
            query += " order by d.eliminado, d.id ";
            query += " limit 1";
            
            UtilLog4j.log.info(this, "query" + query);

            Object lo = em.createNativeQuery(query).getSingleResult();            
            if (lo != null) {
                retVal = (Integer) lo;
            }
            
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener presupuesto det getPresupuestoDetByVO " + e.getMessage(), e);
            retVal = 0;
        }
        return retVal;
    }

}
