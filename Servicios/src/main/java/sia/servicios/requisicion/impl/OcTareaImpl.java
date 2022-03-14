/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sia.constantes.Constantes;
import sia.modelo.Gerencia;
import sia.modelo.OcCodigoTarea;
import sia.modelo.OcNombreTarea;
import sia.modelo.OcTarea;
import sia.modelo.OcUnidadCosto;
import sia.modelo.ProyectoOt;
import sia.modelo.Usuario;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.requisicion.vo.OcSubtareaVO;
import sia.modelo.requisicion.vo.OcTareaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.ProyectoOtImpl;
import sia.servicios.orden.impl.OcUnidadCostoImpl;
import sia.util.LecturaLibro;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class OcTareaImpl extends AbstractFacade<OcTarea> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcTareaImpl() {
        super(OcTarea.class);
    }
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    ProyectoOtImpl proyectoOtRemote;
    @Inject
    OcCodigoTareaImpl ocCodigoTareaLocal;
    @Inject
    OcUnidadCostoImpl ocUnidadCostoRemote;
    @Inject
    OcNombreTareaImpl ocNombreTareaRemote;

    
    public List<ProyectoOtVo> traerProyectoOtPorGerencia(int idGerencia, int idCampo) {
        try {
            List<ProyectoOtVo> lot = null;
            List<Object[]> lobj = null;
            clearQuery();

            query.append(" select *   from ( ");
            query.append(" select distinct(ot.ID), ot.NOMBRE from oc_tarea t ");
            query.append(" inner join PROYECTO_OT ot on t.PROYECTO_OT = ot.ID ");
            query.append(" and ot.AP_CAMPO = ").append(idCampo);
            query.append(" and ot.eliminado  = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" and ot.abierto = '").append(Constantes.BOOLEAN_TRUE).append("'");
            query.append(" inner join AP_CAMPO p on p.id = ot.AP_CAMPO and p.TIPO = 'N' ");
            query.append(" where t.GERENCIA = ").append(idGerencia);
            query.append(" and t.eliminado  = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" union ");
            query.append("select distinct(ot.ID), ot.NOMBRE from oc_tarea t ");
            query.append(" inner join PROYECTO_OT ot on t.PROYECTO_OT = ot.ID ");
            query.append(" and ot.AP_CAMPO = ").append(idCampo);
            query.append(" and ot.eliminado  = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" and ot.abierto = '").append(Constantes.BOOLEAN_TRUE).append("'");
            query.append(" inner join AP_CAMPO p on p.id = ot.AP_CAMPO and p.TIPO = 'C' ");
            query.append(" where t.eliminado  = '").append(Constantes.NO_ELIMINADO).append("') as tareas");
            query.append(" order by nombre asc");
            UtilLog4j.log.info(this, "Lobj: " + query.toString());
            //
            lobj = em.createNativeQuery(query.toString()).getResultList();
            if (lobj != null) {
                lot = new ArrayList<>();
                for (Object[] objects : lobj) {
                    lot.add(castAProyectoOt(objects));
                }
            }
            return lot;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurio un error al traer proyectos por geerncias y campo: : : " + e.getMessage());
            return null;
        }
    }

    
    public List<ProyectoOtVo> traerProyectoOtPorGerenciaItems(int idGerencia, int idCampo, int idActPetrolera, int idTipoTarea, int idNombreTarea, int presupuesto, int idCentroCostos) {
        List<ProyectoOtVo> lot = null;
        try {
            List<Object[]> lobj = null;
            clearQuery();
            query.append(" select * from ( ");
            query.append(" select distinct(ot.ID), ot.NOMBRE from oc_tarea t ");
            query.append(" inner join PROYECTO_OT ot on t.PROYECTO_OT = ot.ID ");
            query.append(" and ot.AP_CAMPO = ").append(idCampo);
            query.append(" and ot.eliminado  = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" and ot.abierto = '").append(Constantes.BOOLEAN_TRUE).append("'");
            query.append(" inner join AP_CAMPO p on p.id = ot.AP_CAMPO and p.TIPO = 'N' ");
            query.append(" where t.GERENCIA = ").append(idGerencia);
            query.append(" and t.eliminado  = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" union ");
            query.append(" select distinct(pot.ID), pot.NOMBRE ");
            query.append(" from oc_presupuesto_detalle pr  ");
            query.append(" inner join oc_tarea t on t.oc_codigo_tarea = pr.oc_codigo_tarea and t.eliminado = false  ");
            query.append(" inner join proyecto_ot pot on pot.id = t.proyecto_ot ");
            query.append(" and pot.AP_CAMPO = ").append(idCampo);
            query.append(" and pot.eliminado  = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" and pot.abierto = '").append(Constantes.BOOLEAN_TRUE).append("'");
            query.append(" inner join AP_CAMPO p on p.id = pot.AP_CAMPO and p.TIPO = 'C' ");

            if (idActPetrolera > 0 && idTipoTarea > 0 && idNombreTarea > 0 //                    && labelSubtarea != null && !labelSubtarea.isEmpty()
                    ) {
                query.append(" inner join OC_SUBTAREA st on st.OC_TAREA = t.id and st.ELIMINADO = 'False' ");
                query.append(" left  join OC_CODIGO_SUBTAREA cst on cst.id = st.OC_CODIGO_SUBTAREA and cst.ELIMINADO = 'False' ");
                query.append(" where t.eliminado  = '").append(Constantes.NO_ELIMINADO).append("' ");
                query.append(" and t.OC_ACTIVIDADPETROLERA = ").append(idActPetrolera);
                query.append(" and t.OC_UNIDAD_COSTO = ").append(idTipoTarea);
                query.append(" and t.oc_codigo_tarea = ").append(idNombreTarea);
//                query.append(" and UPPER(cst.NOMBRE) = UPPER('").append(labelSubtarea).append("') ");
            } else {
                query.append(" where t.eliminado  = '").append(Constantes.NO_ELIMINADO).append("' ");
            }
            if (presupuesto > 0) {
                query.append(" and pr.oc_presupuesto  = ").append(presupuesto);
            }
            if (idCentroCostos > 0) {
                query.append(" and st.oc_codigo_subtarea  = ").append(idCentroCostos);
            }
            query.append(" )  as ProyectosOTs order by nombre asc");
            UtilLog4j.log.info(this, "Lobj: " + query.toString());
            //
            lobj = em.createNativeQuery(query.toString()).getResultList();
            if (lobj != null) {
                lot = new ArrayList<ProyectoOtVo>();
                ProyectoOtVo pOT = null;
                for (Object[] objects : lobj) {
                    pOT = new ProyectoOtVo();
                    pOT.setId((Integer) objects[0]);
                    pOT.setNombre((String) objects[1]);
                    lot.add(pOT);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurio un error al traer proyectos por geerncias y campo: : : " + e.getMessage());
            lot = new ArrayList<ProyectoOtVo>();
        }
        return lot;
    }

    private int traerIdGerenciaPorAbreviatura(int idGerencia) {
        GerenciaVo ger = gerenciaRemote.buscarPorId(idGerencia);
        String[] cad = ger.getAbrev().split(";");
        if (cad == null) {
            ger = gerenciaRemote.traerGerenciaVOAbreviatura(ger.getAbrev());
        } else {
            ger = gerenciaRemote.traerGerenciaVOAbreviatura(cad[0]);
        }
        return ger.getId();
    }

    
    public List<OcTareaVo> traerProyectoOtPorGerenciaYProyectoOt(int idGerencia, int idProyectoOt) {
        try {
            List<OcTareaVo> lot = null;
            List<Object[]> lobj = null;
            clearQuery();
            query.append("select distinct(nt.ID), nt.NOMBRE, t.ID, ct.nombre from oc_tarea t ");
            query.append(" inner join OC_NOMBRE_TAREA nt on t.OC_NOMBRE_TAREA = nt.ID and t.PROYECTO_OT =").append(idProyectoOt).append(" and t.GERENCIA = ").append(idGerencia);
            query.append(" inner join OC_CODIGO_TAREA ct on t.OC_CODIGO_TAREA = ct.ID ");
            query.append(" and ct.nombre not like '%.00'");
            UtilLog4j.log.info(this, "Lobj tarea: " + query.toString());
            //
            lobj = em.createNativeQuery(query.toString()).getResultList();
            if (lobj != null) {
                lot = new ArrayList<OcTareaVo>();
                for (Object[] objects : lobj) {
                    lot.add(castTareaGerenciaVo(objects));
                }
            }
            return lot;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurio un error al traer proyectos por geerncias y proyecto OT: : : " + e.getMessage());
            return null;
        }
    }

    
    public List<OcTareaVo> traerNombrePorProyectoOtGerenciaUnidadCosto(int idGerencia, int idProyectoOt, int idUnidadCosto) {
        try {
            List<OcTareaVo> ltn = null;
            List<Object[]> obj = null;
            clearQuery();
            //    int idGer = traerIdGerenciaPorAbreviatura(idGerencia);
            query.append("select nt.id, nt.nombre, ct.nombre, t.id  from OC_NOMBRE_TAREA nt  ");
            query.append(" inner join OC_TAREA t on t.OC_NOMBRE_TAREA = nt.ID  ");
            query.append(" left join OC_CODIGO_TAREA ct on t.OC_CODIGO_TAREA = ct.ID and ct.NOMBRE not like '%.00'");
            query.append(" where t.gerencia = ").append(idGerencia);
            query.append(" and t.PROYECTO_OT = ").append(idProyectoOt);
            query.append(" and t.oc_unidad_costo = ").append(idUnidadCosto);
            query.append(" and t.ELIMINADO = 'False' ");// and t.codigo_tarea not like '%.00'");
            //query.append(" and t.codigo_tarea not like '%.00'");
            query.append(" order by nt.nombre asc");
            UtilLog4j.log.info(this, "nombre de la tarea: " + query.toString());
            //
            obj = em.createNativeQuery(query.toString()).getResultList();
            if (obj != null) {
                ltn = new ArrayList<OcTareaVo>();
                for (Object[] objects : obj) {
                    ltn.add(castTareaVo(objects));
                }
            }
            return ltn;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio  un error al recuperar la tarea por gerencia, proyecto y  unidad de costo . " + e.getMessage());
            return null;
        }
    }

    
    public List<SelectItem> traerNombrePorProyectoOtGerenciaUnidadCostoItems(int idGerencia, int idProyectoOt, int idUnidadCosto, String nombre, int apCampoID, String tipoCampo) {
        List<SelectItem> ltn = null;
        try {
            List<Object[]> obj = null;
            clearQuery();

            query.append(" select * from ( ");
            if (idProyectoOt > 0) {
                query.append(" select nt.id as ntId, nt.nombre as ntNombre, ct.nombre as ctNombre , t.id as tId, ct.id as ctID ");
            } else {
                query.append(" select nt.id as ntId, nt.nombre as ntNombre, ct.nombre as ctNombre, ct.id as ctID ");
            }
            query.append(" from OC_NOMBRE_TAREA nt  ");
            query.append(" inner join OC_TAREA t on t.OC_NOMBRE_TAREA = nt.ID  ");
            query.append(" left join OC_CODIGO_TAREA ct on t.OC_CODIGO_TAREA = ct.ID and ct.NOMBRE not like '%.00'");
            query.append(" inner join PROYECTO_OT ot on ot.id = t.PROYECTO_OT ");
            query.append(" inner join AP_CAMPO c on c.id = ot.AP_CAMPO and c.TIPO = 'N' ");
            query.append(" where t.ELIMINADO = 'False' ");
            query.append(" and t.gerencia = ").append(idGerencia);
            query.append(" and t.oc_unidad_costo = ").append(idUnidadCosto);
            if (idProyectoOt > 0) {
                query.append(" and t.PROYECTO_OT = ").append(idProyectoOt);
            } else {
                query.append(" group by ntId, ntNombre, ctNombre  ");
            }

            query.append(" union ");
            if (idProyectoOt > 0) {
                query.append(" select nt.id as ntId, nt.nombre as ntNombre, ct.nombre as ctNombre , t.id as tId, ct.id as ctID ");
            } else {
                query.append(" select nt.id as ntId, nt.nombre as ntNombre, ct.nombre as ctNombre, ct.id as ctID ");
            }
            query.append(" from OC_NOMBRE_TAREA nt  ");
            query.append(" inner join OC_TAREA t on t.OC_NOMBRE_TAREA = nt.ID  ");
            query.append(" left join OC_CODIGO_TAREA ct on t.OC_CODIGO_TAREA = ct.ID and ct.NOMBRE not like '%.00'");
            query.append(" inner join PROYECTO_OT ot on ot.id = t.PROYECTO_OT ");
            query.append(" inner join AP_CAMPO c on c.id = ot.AP_CAMPO and c.TIPO = 'C' ");
            query.append(" where t.ELIMINADO = 'False'");
            query.append(" and t.oc_unidad_costo = ").append(idUnidadCosto);
            if (nombre != null && !nombre.isEmpty()) {
                query.append(" and upper(nt.NOMBRE) = upper('").append(nombre).append("') ");
            }
            if (idProyectoOt > 0) {
                query.append(" and t.PROYECTO_OT = ").append(idProyectoOt);
            } else {
                query.append(" group by ntId, ntNombre, ctNombre  ");
            }

            query.append(" ) as nombre_tarea order by ntNombre asc");
            //
            UtilLog4j.log.info(this, "nombre de la tarea: " + query.toString());
            //
            obj = em.createNativeQuery(query.toString()).getResultList();
            if (obj != null) {
                ltn = new ArrayList<SelectItem>();
                SelectItem item = null;
                for (Object[] objects : obj) {
                    if (idProyectoOt > 0 && "C".equals(tipoCampo)) {
                        item = new SelectItem((Integer) objects[4], (String) objects[1]);
                    } else {
                        item = new SelectItem((Integer) objects[3], (String) objects[1]);
                    }
                    ltn.add(item);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio  un error al recuperar la tarea por gerencia, proyecto y  unidad de costo . " + e.getMessage());
            ltn = new ArrayList<SelectItem>();
        }
        return ltn;
    }

    private ProyectoOtVo castAProyectoOt(Object[] objects) {
        ProyectoOtVo pot = new ProyectoOtVo();
        pot.setId((Integer) objects[0]);
        pot.setNombre((String) objects[1]);
        return pot;
    }

    private OcTareaVo castTareaVo(Object[] objects) {
        OcTareaVo otv = new OcTareaVo();
        otv.setIdNombreTarea((Integer) objects[0]);
        otv.setNombreTarea((String) objects[1]);
        otv.setCodigoTarea((String) objects[2]);
        otv.setIdTarea((Integer) objects[3]);
        return otv;
    }

    private OcTareaVo castTareaGerenciaVo(Object[] objects) {
        OcTareaVo otv = new OcTareaVo();
        otv.setIdNombreTarea((Integer) objects[0]);
        otv.setNombreTarea((String) objects[1]);
        otv.setIdTarea((Integer) objects[2]);
        otv.setCodigoTarea((String) objects[3]);
        return otv;
    }

    
    public List<OcTareaVo> traerUnidadCostoPorGerenciaProyectoOT(int idGerencia, int idProyectoOt, int actividadID) {
        List<OcTareaVo> lot = null;
        List<Object[]> lobj = null;
        clearQuery();
        query.append(" select * from ( ");
        query.append("select distinct(uc.ID), uc.NOMBRE from OC_UNIDAD_COSTO uc ");
        query.append(" inner join OC_TAREA t on t.OC_UNIDAD_COSTO = uc.ID  ");
        query.append(" inner join OC_CODIGO_TAREA ct on t.OC_CODIGO_TAREA = ct.ID and ct.NOMBRE not like '%.00'");
        query.append(" inner join PROYECTO_OT p on p.id = t.PROYECTO_OT ");
        query.append(" inner join AP_CAMPO c on c.id = p.AP_CAMPO and c.TIPO = 'N' ");
        query.append(" where t.PROYECTO_OT = ").append(idProyectoOt).append(" and t.GERENCIA = ").append(idGerencia);
        query.append(" and t.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" union ");
        query.append("select distinct(uc.ID), uc.NOMBRE from OC_UNIDAD_COSTO uc ");
        query.append(" inner join OC_TAREA t on t.OC_UNIDAD_COSTO = uc.ID  ");
        query.append(" inner join OC_CODIGO_TAREA ct on t.OC_CODIGO_TAREA = ct.ID and ct.NOMBRE not like '%.00'");
        query.append(" inner join PROYECTO_OT p on p.id = t.PROYECTO_OT ");
        query.append(" inner join AP_CAMPO c on c.id = p.AP_CAMPO and c.TIPO = 'C' ");
        query.append(" where t.PROYECTO_OT = ").append(idProyectoOt);
        query.append(" and t.OC_ACTIVIDADPETROLERA = ").append(actividadID);
        query.append(" and t.eliminado = '").append(Constantes.NO_ELIMINADO).append("' ) as tareas");
        query.append(" order by nombre asc");

        UtilLog4j.log.info(this, "Lobj tarea unidad costo: " + query.toString());
        //
        lobj = em.createNativeQuery(query.toString()).getResultList();
        if (lobj != null) {
            lot = new ArrayList<OcTareaVo>();
            for (Object[] objects : lobj) {
                lot.add(castTareaUnidadCosto(objects));
            }
        }
        return lot;
    }

    
    public List<SelectItem> traerUnidadCostoPorGerenciaProyectoOTItems(int idGerencia, int idProyectoOt, int actividadID, String noombre, int apCampoID) {
        List<SelectItem> lot = null;
        List<Object[]> lobj = null;
        clearQuery();
        query.append(" select * from ( ");
        query.append("select distinct(uc.ID), uc.NOMBRE from OC_UNIDAD_COSTO uc ");
        query.append(" inner join OC_TAREA t on t.OC_UNIDAD_COSTO = uc.ID  ");
        query.append(" inner join OC_CODIGO_TAREA ct on t.OC_CODIGO_TAREA = ct.ID and ct.NOMBRE not like '%.00'");
        query.append(" inner join PROYECTO_OT p on p.id = t.PROYECTO_OT ");
        query.append(" inner join AP_CAMPO c on c.id = p.AP_CAMPO and c.TIPO = 'N' and c.id = ").append(apCampoID);
        query.append(" where t.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        if (idProyectoOt > 0) {
            query.append(" AND t.PROYECTO_OT = ").append(idProyectoOt);
        }
        query.append(" and t.GERENCIA = ").append(idGerencia);
        query.append(" union ");
        query.append("select distinct(uc.ID), uc.NOMBRE from OC_UNIDAD_COSTO uc ");
        query.append(" inner join OC_TAREA t on t.OC_UNIDAD_COSTO = uc.ID  ");
        query.append(" inner join OC_CODIGO_TAREA ct on t.OC_CODIGO_TAREA = ct.ID and ct.NOMBRE not like '%.00'");
        query.append(" inner join PROYECTO_OT p on p.id = t.PROYECTO_OT ");
        query.append(" inner join AP_CAMPO c on c.id = p.AP_CAMPO and c.TIPO = 'C' ");
        query.append(" where t.eliminado = '").append(Constantes.NO_ELIMINADO).append("' ");
        if (idProyectoOt > 0) {
            query.append(" and t.PROYECTO_OT = ").append(idProyectoOt);
        }
        query.append(" and t.OC_ACTIVIDADPETROLERA = ").append(actividadID);
        if (noombre != null && !noombre.isEmpty()) {
            query.append(" and upper(uc.NOMBRE) = upper('").append(noombre).append("') ");
        }
        query.append(" ) as UnidadesCosto order by nombre asc");

        UtilLog4j.log.info(this, "Lobj tarea unidad costo: " + query.toString());
        //
        lobj = em.createNativeQuery(query.toString()).getResultList();
        if (lobj != null) {
            lot = new ArrayList<SelectItem>();
            SelectItem item = null;
            for (Object[] objects : lobj) {
                item = new SelectItem((Integer) objects[0], (String) objects[1]);
                lot.add(item);
            }
        }
        return lot;
    }

    private OcTareaVo castTareaUnidadCosto(Object[] objects) {
        OcTareaVo otv = new OcTareaVo();
        otv.setIdUnidadCosto((Integer) objects[0]);
        otv.setUnidadCosto((String) objects[1]);
//////        otv.setIdTarea((Integer) objects[2]);
//////        otv.setCodigoTarea((String) objects[3]);
        return otv;
    }

    
    public OcTareaVo traerTarea(int idGerencia, int idProyectoOt, int idUnidadCosto, int idNombreTarea, int idCodigoTarea) {
        OcTareaVo tvo = null;
        try {
            Object[] lobj = null;
            clearQuery();
            //    int idGer = traerIdGerenciaPorAbreviatura(idGerencia);
            query.append("select t.ID, g.ID, g.NOMBRE, uc.id, uc.NOMBRE, pot.ID, pot.NOMBRE, nt.id, nt.nombre, ct.nombre from oc_tarea t ");
            query.append(" inner join GERENCIA g on t.GERENCIA = g.ID and g.ID = ").append(idGerencia);
            query.append(" inner join PROYECTO_OT  pot on t.PROYECTO_OT = pot.ID and pot.ID = ").append(idProyectoOt);
            query.append(" inner join oc_unidad_costo uc on t.oc_unidad_costo = uc.ID  and uc.ID = ").append(idUnidadCosto);
            query.append(" inner join OC_NOMBRE_TAREA nt on t.OC_NOMBRE_TAREA = nt.ID and nt.ID = ").append(idNombreTarea);
            query.append(" inner join OC_codigo_TAREA ct on t.OC_codigo_TAREA = ct.ID ");
            query.append(" where ");
            if (idGerencia > 0) {
                query.append(" and t.gerencia = ").append(idGerencia);
            }
            if (idProyectoOt > 0) {
                query.append(" and t.PROYECTO_OT = ").append(idProyectoOt);
            }
            if (idUnidadCosto > 0) {
                query.append(" and t.oc_unidad_costo = ").append(idUnidadCosto);
            }
            if (idNombreTarea > 0) {
                query.append(" and t.oc_nombre_tarea = ").append(idNombreTarea);
            }
            if (idCodigoTarea > 0) {
                query.append(" and t.OC_codigo_TAREA = ").append(idCodigoTarea);
            }
            query.append(" and t.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" and ct.nombre not like '%.00'");
            UtilLog4j.log.info(this, "Lobj tarea comleta: " + query.toString());
            //
            lobj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            if (lobj != null) {
                tvo = castTareaCompletaVo(lobj);
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar la tarea : : : : " + e.getMessage() + "  # # #  # # " + e.getCause());
        }
        return tvo;
    }

    private OcTareaVo castTareaCompletaVo(Object[] objects) {
        OcTareaVo otv = new OcTareaVo();
        otv.setIdTarea((Integer) objects[0]);
        otv.setIdGerencia((Integer) objects[1]);
        otv.setGerencia((String) objects[2]);
        otv.setIdUnidadCosto((Integer) objects[3]);
        otv.setUnidadCosto((String) objects[4]);
        otv.setIdProyectoOt((Integer) objects[5]);
        otv.setProyectoOt((String) objects[6]);
        otv.setIdNombreTarea((Integer) objects[7]);
        otv.setNombreTarea((String) objects[8]);
        otv.setCodigoTarea((String) objects[9]);

        return otv;
    }

    
    public List<OcTareaVo> traerGerenciaPorProyectoOT(int idProyectoOt) {
        List<OcTareaVo> lot = null;
        List<Object[]> lobj = null;
        clearQuery();
        query.append("select distinct(g.ID), g.NOMBRE, g.ABREV from GERENCIA g ");
        query.append(" inner join OC_TAREA t on t.GERENCIA = g.ID    ");
        query.append(" and t.PROYECTO_OT = ").append(idProyectoOt);
        query.append(" and t.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" order by g.nombre asc");
        UtilLog4j.log.info(this, "Lobj tarea gerencia  : : : : : : : " + query.toString());
        //
        lobj = em.createNativeQuery(query.toString()).getResultList();
        if (lobj != null) {
            lot = new ArrayList<OcTareaVo>();
            for (Object[] objects : lobj) {
                lot.add(castTareaGerencia(objects));
            }
        }
        return lot;
    }

    private OcTareaVo castTareaGerencia(Object[] objects) {
        OcTareaVo otv = new OcTareaVo();
        otv.setIdGerencia((Integer) objects[0]);
        otv.setGerencia((String) objects[1]);
        //
        otv.setListaGerencia(new ArrayList<GerenciaVo>());
        //
        otv.setListaGerencia(gerenciaRemote.traerGerenciaVoSecundariaAbreviatura((String) objects[2]));

        return otv;
    }

    
    public void eliminarRelacionGerenciaProyectoOt(int idProyectoOT, int idGerencia, String sesion) {
        try {
            List<OcTareaVo> lt = traerProyectoOtPorGerenciaYProyectoOt(idGerencia, idProyectoOT);
            for (OcTareaVo ocTareaVo : lt) {
                OcTarea tarea = find(ocTareaVo.getIdTarea());
                tarea.setEliminado(Constantes.ELIMINADO);
                tarea.setModifico(new Usuario(sesion));
                tarea.setFechaModifico(new Date());
                tarea.setHoraModifico(new Date());
                edit(tarea);
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    
    public boolean eliminarTarea(int idTarea, String sesion) {
        OcTarea tarea = find(idTarea);
        String ae = tarea.toString();
        boolean v = false;
        tarea.setEliminado(Constantes.ELIMINADO);
        tarea.setModifico(new Usuario(sesion));
        tarea.setFechaModifico(new Date());
        tarea.setHoraModifico(new Date());
        edit(tarea);
        return v;
    }

    
    public boolean eliminarRelacionUnidadCosto(int idProyectoOT, int idGerencia, int idUnidadCosto, String sesion) {
        boolean v = false;
        //
        List<OcTareaVo> lt = traerNombrePorProyectoOtGerenciaUnidadCosto(idGerencia, idProyectoOT, idUnidadCosto);
        for (OcTareaVo ocTareaVo : lt) {
            OcTarea tarea = find(ocTareaVo.getIdTarea());
            String ae = tarea.toString();
            tarea.setEliminado(Constantes.ELIMINADO);
            tarea.setModifico(new Usuario(sesion));
            tarea.setFechaModifico(new Date());
            tarea.setHoraModifico(new Date());
            edit(tarea);
            v = true;
        }
        return v;
    }

    
    public List<OcTareaVo> traerTarea(int idGerencia, int idProyectoOt, int idUnidadCosto) {

        List<OcTareaVo> lot = null;
        try {
            List<Object[]> lobj = null;
            clearQuery();
            //    int idGer = traerIdGerenciaPorAbreviatura(idGerencia);
            query.append("select t.ID, g.ID, g.NOMBRE, uc.id, uc.NOMBRE, pot.ID, pot.NOMBRE, nt.id, nt.nombre, ct.nombre   from oc_tarea t ");
            query.append(" inner join GERENCIA g on t.GERENCIA = g.ID and g.ID = ").append(idGerencia);
            query.append(" inner join PROYECTO_OT  pot on t.PROYECTO_OT = pot.ID and pot.ID = ").append(idProyectoOt);
            query.append(" inner join oc_unidad_costo uc on t.oc_unidad_costo = uc.ID  and uc.ID = ").append(idUnidadCosto);
            query.append(" inner join OC_NOMBRE_TAREA nt on t.OC_NOMBRE_TAREA = nt.ID");
            query.append(" inner join OC_codigo_TAREA ct on t.OC_codigo_TAREA = ct.ID");
            query.append(" where t.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" and ct.nombre not like '%.00' order by nt.nombre  asc");
            UtilLog4j.log.info(this, "Lobj tarea comleta: " + query.toString());
            //
            lobj = em.createNativeQuery(query.toString()).getResultList();
            if (lobj != null) {
                lot = new ArrayList<OcTareaVo>();
                for (Object[] object : lobj) {
                    lot.add(castTareaCompletaVo(object));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar la tarea : : : : " + e.getMessage() + "  # # #  # # " + e.getCause());
        }
        return lot;
    }

    
    public List<OcTareaVo> traerTipoTareaPorGerenciaOt(int idGerencia, int idProyectoOt) {
        try {
            List<OcTareaVo> lot = null;
            List<Object[]> lobj = null;
            String c = "select uc.ID, uc.NOMBRE, g.id, g.nombre from oc_tarea t "
                    + "      inner join OC_UNIDAD_COSTO uc on t.OC_UNIDAD_COSTO = uc.ID "
                    + "      inner join GERENCIA g on t.GERENCIA = g.ID and g.id = " + idGerencia + ""
                    + "  where t.proyecto_ot = " + idProyectoOt + " and t.eliminado = 'False' "
                    + " group by uc.ID, uc.NOMBRE, g.id, g.nombre order by uc.nombre";
            UtilLog4j.log.info(this, "Lobj tarea: " + c);
            //
            lobj = em.createNativeQuery(c).getResultList();
            if (lobj != null) {
                lot = new ArrayList<OcTareaVo>();
                for (Object[] objects : lobj) {
                    OcTareaVo tareaVo = new OcTareaVo();
                    tareaVo.setIdUnidadCosto((Integer) objects[0]);
                    tareaVo.setUnidadCosto((String) objects[1]);
                    tareaVo.setIdGerencia((Integer) objects[2]);
                    tareaVo.setGerencia((String) objects[3]);
                    lot.add(tareaVo);
                }
            }
            return lot;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurio un error al traer proyectos por geerncias y proyecto OT: : : " + e.getMessage());
            return null;
        }
    }

    
    public List<OcTareaVo> traerTareaPorOt(int idProyectoOt) {

        List<OcTareaVo> lot = null;
        try {
            List<Object[]> lobj = null;
            clearQuery();
            //    int idGer = traerIdGerenciaPorAbreviatura(idGerencia);
            query.append("select t.ID, g.ID, g.NOMBRE, uc.id, uc.NOMBRE, pot.ID, pot.NOMBRE, nt.id, nt.nombre, ct.nombre   from oc_tarea t ");
            query.append(" inner join GERENCIA g on t.GERENCIA = g.ID ");
            query.append(" inner join PROYECTO_OT  pot on t.PROYECTO_OT = pot.ID and pot.ID = ").append(idProyectoOt);
            query.append(" inner join oc_unidad_costo uc on t.oc_unidad_costo = uc.ID  ");
            query.append(" inner join OC_NOMBRE_TAREA nt on t.OC_NOMBRE_TAREA = nt.ID");
            query.append(" inner join OC_codigo_TAREA ct on t.OC_codigo_TAREA = ct.ID");
            query.append(" where t.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" and ct.nombre not like '%.00'");
            UtilLog4j.log.info(this, "Lobj tarea comleta: " + query.toString());
            //
            lobj = em.createNativeQuery(query.toString()).getResultList();
            if (lobj != null) {
                lot = new ArrayList<OcTareaVo>();
                for (Object[] object : lobj) {
                    lot.add(castTareaCompletaVo(object));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar la tarea : : : : " + e.getMessage() + "  # # #  # # " + e.getCause());
        }
        return lot;
    }

    
    public OcSubtareaVO traerIDTarea(int idProyectoOt, int idUnidadCosto, int idCodigoTarea, int ocActPetrolera, int idCodigoSubtarea) {
        OcSubtareaVO ret = null;
        try {
            Object[] lobj = null;
            clearQuery();
            String consulta = " select a.id, st.id, st.oc_codigo_subtarea, a.proyecto_ot "
                    + " from oc_tarea a "
                    + " inner join oc_subtarea st on st.oc_tarea = a.id and st.eliminado = false and st.oc_codigo_subtarea = " + idCodigoSubtarea
                    + " where a.eliminado = false ";
            if (idProyectoOt > 0) {
                consulta += " and a.proyecto_ot = " + idProyectoOt;
            }
            consulta += " and a.oc_unidad_costo = " + idUnidadCosto
                    + " and a.oc_codigo_tarea = " + idCodigoTarea
                    + " and a.oc_actividadpetrolera = " + ocActPetrolera
                    + " limit 1 ";

            UtilLog4j.log.info(this, "Lobj tarea comleta: " + consulta);
            lobj = (Object[]) em.createNativeQuery(consulta).getSingleResult();
            if (lobj != null) {
                ret = new OcSubtareaVO();
                ret.setIdTarea((Integer) lobj[0]);
                ret.setId((Integer) lobj[1]);
                ret.setIdCodigoSubtarea((Integer) lobj[2]);
                ret.setIdProyectoOT((Integer) lobj[3]);
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar la tarea : : : : " + e.getMessage() + "  # # #  # # " + e.getCause());
        }
        return ret;
    }

    
    public Map<String, List<OcTareaVo>> cargarTareas(File file, String sesion, List<CampoVo> listaCampos) {
        List<OcTareaVo> listaTarea = new ArrayList<>();
        List<OcTareaVo> tareasToAdd = new ArrayList<>();
        Map<String, List<OcTareaVo>> tareasTemp = new HashMap<>();
        List<OcTareaVo> tareasNoAdd = new ArrayList<>();
        try {
            //
            LecturaLibro lecturaLibro = new LecturaLibro();
            XSSFWorkbook archivo = lecturaLibro.loadFileXLSX(file);
            XSSFSheet workSheet = lecturaLibro.loadSheet(archivo);
            for (int i = 4; i <= workSheet.getLastRowNum(); i++) {
                //int rowNum = i + 1;
                OcTareaVo ca = readSheetData(workSheet, i);
                if (ca != null) {
                    listaTarea.add(ca);
                } else {
                    break;
                }
            }
            //
            ProyectoOt pot = null;
            GerenciaVo gv = null;
            OcUnidadCosto uc = null;
            OcNombreTarea nt = null;
            OcCodigoTarea ct = null;
            OcTareaVo newOcTareaVo = null;
            for (CampoVo campoVo : listaCampos) {
                tareasToAdd = new ArrayList<>();
                for (OcTareaVo ocTareaVo : listaTarea) {                    
                    if (ocTareaVo.getCodigoTarea().equals("AAA.0000.00")) {
                        String cuenta = "";
                        if (ocTareaVo.getCuentaContable().contains("E")) {
                            cuenta = ocTareaVo.getCuentaContable().replace(".", "").replace("E7", "");
                        } else {
                            cuenta = ocTareaVo.getCuentaContable();
                        }
                        pot = proyectoOtRemote.traerProyectoOTPorCuentaContableCampo(cuenta, campoVo.getId());
                        if (pot == null) {
                            pot = proyectoOtRemote.guardarProyectoOT(ocTareaVo.getProyectoOt(), cuenta, campoVo.getId(), sesion);
                        } else {
                            proyectoOtRemote.activarProyectoOt(pot, sesion);
                        }
                        // es un proyecto ot insert proyecto
                    } else if (ocTareaVo.getCodigoTarea().contains(".0000.00") && !ocTareaVo.getCodigoTarea().startsWith("AAA.")) {
                        gv = gerenciaRemote.traerGerenciaVOAbreviatura(ocTareaVo.getCodigoTarea().substring(0, 3));
                        // es la gerencia
                    } else if (ocTareaVo.getCodigoTarea().endsWith(".00")
                            && !ocTareaVo.getCodigoTarea().startsWith("AAA.")
                            && !ocTareaVo.getCodigoTarea().substring(4, 8).equals("0000")) {
                        uc = ocUnidadCostoRemote.buscarPorNombreYCodigo(ocTareaVo.getUnidadCosto(), ocTareaVo.getCodigoTarea().substring(4, 8));
                        if (uc == null) {
                            uc = ocUnidadCostoRemote.guardarUnidadCosto(ocTareaVo.getUnidadCosto(), ocTareaVo.getCodigoTarea().substring(4, 8), sesion);
                        }
                    } else {
                        newOcTareaVo = new OcTareaVo();
                        newOcTareaVo.setCuentaContable(ocTareaVo.getCuentaContable());
                        newOcTareaVo.setNombreTarea(ocTareaVo.getNombreTarea());
                        nt = ocNombreTareaRemote.buscarPorNombre(ocTareaVo.getNombreTarea());
                        if (nt == null) {
                            nt = ocNombreTareaRemote.guardarNombreTarea(ocTareaVo.getNombreTarea(), sesion);
                        }
                        // codigo tarea
                        ct = ocCodigoTareaLocal.buscarPorCodigo(ocTareaVo.getCodigoTarea());
                        newOcTareaVo.setCodigoTarea(ocTareaVo.getCodigoTarea());
                        if (ct == null) {
                            ct = ocCodigoTareaLocal.guardarCodigo(ocTareaVo.getCodigoTarea(), sesion);
                        }
                        newOcTareaVo.setCampo(campoVo.getNombre());
                        //
                        if (pot != null && gv != null && uc != null && nt != null && ct != null) {
                            newOcTareaVo.setIdProyectoOt(pot.getId());
                            newOcTareaVo.setIdGerencia(gv.getId());
                            newOcTareaVo.setGerencia(gv.getNombre());
                            newOcTareaVo.setIdUnidadCosto(uc.getId());
                            newOcTareaVo.setIdNombreTarea(nt.getId());
                            newOcTareaVo.setIdcodigoTarea(ct.getId());
                            newOcTareaVo.setExisteTarea(this.existeTarea(
                                    newOcTareaVo.getIdProyectoOt(),
                                    newOcTareaVo.getIdGerencia(),
                                    newOcTareaVo.getIdUnidadCosto(),
                                    newOcTareaVo.getIdNombreTarea(),
                                    newOcTareaVo.getIdcodigoTarea()));
                            tareasToAdd.add(newOcTareaVo);
                        } else {
                            tareasNoAdd.add(ocTareaVo);
                        }
                    }
                }
                 tareasTemp.put(campoVo.getNombre(), tareasToAdd);
            }
        } catch (Exception ex) {
            UtilLog4j.log.info("Tareas " + new Date());
            for (OcTareaVo tareaVo : tareasNoAdd) {
                UtilLog4j.log.info(tareaVo.getCampo() + "-" + tareaVo.getProyectoOt() + "-" + tareaVo.getGerencia() + "-" + tareaVo.getUnidadCosto() + "-" + tareaVo.getNombreTarea() + "-" + tareaVo.getCodigoTarea());
            }
            Logger.getLogger(OcTareaImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tareasTemp;
    }

    private OcTareaVo readSheetData(XSSFSheet workSheet, int fila) throws Exception {
        UtilLog4j.log.info("Leyendo datos ...");
        LecturaLibro lecturaLibro = new LecturaLibro();
        OcTareaVo tarea = new OcTareaVo();
        try {
            String ccText = lecturaLibro.getValFromReference(workSheet, "A" + fila);
            if(ccText == null || ccText.isEmpty()){
                throw new Exception("Termino el listado de tareas o no pudo ser leida la información");
            }
            tarea.setCuentaContable(lecturaLibro.getValFromReference(workSheet, "A" + fila));
            tarea.setCodigoTarea(lecturaLibro.getValFromReference(workSheet, "B" + fila));
            tarea.setProyectoOt(lecturaLibro.getValFromReference(workSheet, "C" + fila));
            tarea.setGerencia(lecturaLibro.getValFromReference(workSheet, "C" + fila));
            tarea.setNombreTarea(lecturaLibro.getValFromReference(workSheet, "C" + fila));
            tarea.setUnidadCosto(lecturaLibro.getValFromReference(workSheet, "C" + fila));
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            System.out.println("weerwer: " + e);
            tarea = null;
        }
        return tarea;
    }

    /**
     *
     * @param tareaVo
     * @param sesion
     * @param confirmarExiste
     * @return
     */
    
    public OcTarea guardar(OcTareaVo tareaVo, String sesion, boolean confirmarExiste) {
        OcTareaVo tVo = null;
        if(confirmarExiste){                
            tVo = traerTarea(tareaVo.getIdGerencia(), tareaVo.getIdProyectoOt(), tareaVo.getIdUnidadCosto(), tareaVo.getIdNombreTarea(), tareaVo.getIdcodigoTarea());
        }
        OcTarea t = null;
        try {
            if (tVo == null) {
                t = new OcTarea();
                t.setProyectoOt(new ProyectoOt(tareaVo.getIdProyectoOt()));
                t.setGerencia(new Gerencia(tareaVo.getIdGerencia()));
                t.setOcNombreTarea(new OcNombreTarea(tareaVo.getIdNombreTarea()));
                t.setOcCodigoTarea(new OcCodigoTarea(tareaVo.getIdcodigoTarea()));
                t.setOcUnidadCosto(new OcUnidadCosto(tareaVo.getIdUnidadCosto()));
                //
                t.setGenero(new Usuario(sesion));
                t.setFechaGenero(new Date());
                t.setHoraGenero(new Date());
                t.setEliminado(Constantes.BOOLEAN_FALSE);
                //
                create(t);
                //
            } else {
                t = buscarLazyPorId(tVo.getIdTarea());
                t.setModifico(new Usuario(sesion));
                t.setFechaModifico(new Date());
                t.setHoraModifico(new Date());
                t.setEliminado(Constantes.BOOLEAN_FALSE);
                //
                edit(t);
            }
        } catch (Exception e) {
            System.out.println("eeee: " + e);
        }
        return t;
    }

    private OcTarea buscarLazyPorId(int id) {
        try {
            return (OcTarea) em.createNamedQuery("OcTarea.buscarPorId", OcTarea.class).setParameter(1, id).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return null;
    }

    
    public boolean existeTarea(int idProyectoOt, int idGerencia, int idUnidadCosto, int idNombreTarea, int idcodigoTarea) {
        boolean ret = false;
        try {
            Object[] lobj = null;
            clearQuery();
            String consulta = " select a.id, a.proyecto_ot, a.gerencia, a.oc_unidad_costo, a.oc_nombre_tarea, a.oc_codigo_tarea "
                    + " from oc_tarea a "
                    + " where a.eliminado = false "
                    + " and a.proyecto_ot = " + idProyectoOt
                    + " and a.gerencia = " + idGerencia
                    + " and a.oc_unidad_costo = " + idUnidadCosto
                    + " and a.oc_nombre_tarea = " + idNombreTarea
                    + " and a.oc_codigo_tarea = " + idcodigoTarea;

            UtilLog4j.log.info(this, "Lobj tarea comleta: " + consulta);
            List<Object[]> lo = em.createNativeQuery(consulta).getResultList();
            if (lo != null && lo.size() > 0) {
                ret = true;
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar la tarea : : : : " + e.getMessage() + "  # # #  # # " + e.getCause());
            ret = false;
        }
        return ret;
    }
}
