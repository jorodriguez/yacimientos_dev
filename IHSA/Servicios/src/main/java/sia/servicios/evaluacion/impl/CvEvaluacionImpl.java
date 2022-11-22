/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.evaluacion.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.CvEvaluacion;
import sia.modelo.contrato.vo.EvaluacionRespuestaVo;
import sia.modelo.contrato.vo.EvaluacionVo;
import sia.modelo.contrato.vo.PreguntaVo;
import sia.modelo.contrato.vo.SeccionVo;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author jcarranza
 */
@Stateless 
public class CvEvaluacionImpl extends AbstractFacade<CvEvaluacion>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    public CvEvaluacionImpl() {
        super(CvEvaluacion.class);
    }

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    
    public List<EvaluacionVo> traerEvaluaciones(int idConvenio, boolean pendientes, boolean contestados) {
        List<EvaluacionVo> lvo = null;
        String sb = "";
        sb += "	select c.id, co.id, c.cv_evaluacion_template, g.id, g.nombre, u.id, u.nombre, t.nombre, cl.nombre, c.fecha_genero, p.id, p.nombre ";
        sb += "	from cv_evaluacion c ";
        sb += "	inner join gerencia g on g.id = c.gerencia and g.eliminado = false ";
        sb += "	inner join convenio co on co.id = c.convenio  and co.eliminado = false ";
        sb += "	inner join usuario u on u.id = c.responsable  ";
        sb += "	inner join cv_evaluacion_template t on t.id = c.cv_evaluacion_template ";
        sb += "	inner join cv_clasificacion cl on cl.id = t.cv_clasificacion ";
        sb += "	inner join proveedor p on p.id = co.proveedor ";
        sb += "	where c.eliminado = false ";
        if (idConvenio > 0) {
            sb += "	and c.convenio = " + idConvenio;
        }

        if (pendientes) {
            sb += "	and c.contestada = false ";
        } else if (contestados) {
            sb += "	and c.contestada = true ";
        }

        List<Object[]> lo = em.createNativeQuery(sb).getResultList();

        if (lo != null) {
            lvo = new ArrayList<>();
            for (Object[] lo1 : lo) {
                lvo.add(castEvaluacionVo(lo1));
            }
        }
        return lvo;
    }

    
    public EvaluacionVo traerEvaluacionID(int idEvaluacion) {
        EvaluacionVo vo = null;
        String sb = "";
        sb += "	select c.id, co.id, c.cv_evaluacion_template, g.id, g.nombre, u.id, u.nombre, t.nombre, cl.nombre, c.fecha_genero, p.id, p.nombre ";
        sb += "	from cv_evaluacion c ";
        sb += "	inner join gerencia g on g.id = c.gerencia and g.eliminado = false ";
        sb += "	inner join convenio co on co.id = c.convenio  and co.eliminado = false ";
        sb += "	inner join usuario u on u.id = c.responsable  ";
        sb += "	inner join cv_evaluacion_template t on t.id = c.cv_evaluacion_template ";
        sb += "	inner join cv_clasificacion cl on cl.id = t.cv_clasificacion ";
        sb += "	inner join proveedor p on p.id = co.proveedor ";
        sb += "	where c.eliminado = false ";
        sb += "	and c.id = " + idEvaluacion;

        List<Object[]> lo = em.createNativeQuery(sb).getResultList();

        if (lo != null) {
            vo = new EvaluacionVo();
            for (Object[] lo1 : lo) {
                vo = castEvaluacionVo(lo1);
            }
        }
        return vo;
    }

    private EvaluacionVo castEvaluacionVo(Object[] obj) {
        EvaluacionVo vo = new EvaluacionVo();
        vo.setId((Integer) obj[0]);
        vo.setConvenioId((Integer) obj[1]);
        vo.setTemplateId((Integer) obj[2]);
        vo.setGerenciaId((Integer) obj[3]);
        vo.setNombreGerencia((String) obj[4]);
        vo.setResponsable((String) obj[5]);
        vo.setNombreResponsable((String) obj[6]);
        vo.setTemplateNombre((String) obj[7]);
        vo.setNombreClasificacion((String) obj[8]);
        vo.setFechaSolicitada((Date) obj[9]);
        vo.setProveedorId((Integer) obj[10]);
        vo.setNombreProveedor((String) obj[11]);
        return vo;
    }

    
    public long getTotalEvaluaciones(String usuario, int apCampo, boolean contestadas) {
        String consulta = "select count(e.id) "
                + "from cv_evaluacion  e "
                + "inner join convenio c on c.id = e.convenio and c.eliminado = false "
                + "where e.eliminado = false "
                + "and e.contestada = " + contestadas;

        if (usuario != null && !usuario.isEmpty()) {
            consulta += " and e.responsable = '" + usuario + "' ";
        }

        if (apCampo > 0) {
            consulta += " and c.ap_campo = " + apCampo;
        }

        return ((Long) em.createNativeQuery(consulta).getSingleResult());
    }

    
    public List<EvaluacionVo> traerEvaluacionesPendientes(String usuario, int apCampo, boolean contestadas) {
        List<EvaluacionVo> lvo = null;
        String sb = "";
        sb += "	select c.id, co.id, c.cv_evaluacion_template, g.id, g.nombre, u.id, u.nombre, t.nombre, cl.nombre, c.fecha_genero, p.id, p.nombre ";
        sb += "	from cv_evaluacion c ";
        sb += "	inner join gerencia g on g.id = c.gerencia and g.eliminado = false ";
        sb += "	inner join convenio co on co.id = c.convenio  and co.eliminado = false ";
        sb += "	inner join usuario u on u.id = c.responsable  ";
        sb += "	inner join cv_evaluacion_template t on t.id = c.cv_evaluacion_template ";
        sb += "	inner join cv_clasificacion cl on cl.id = t.cv_clasificacion ";
        sb += "	inner join proveedor p on p.id = co.proveedor ";
        sb += "	where c.eliminado = false ";
        sb += " and c.contestada = " + contestadas;
        sb += " and c.responsable = '" + usuario + "' ";
        sb += " and co.ap_campo = " + apCampo;

        List<Object[]> lo = em.createNativeQuery(sb).getResultList();

        if (lo != null) {
            lvo = new ArrayList<>();
            for (Object[] lo1 : lo) {
                lvo.add(castEvaluacionVo(lo1));
            }
        }
        return lvo;
    }

    
    public EvaluacionVo traerEvaluacionDetID(int idEvaluacion) {
        EvaluacionVo vo = null;
        String sb = "";
        sb += "	select  ";
        sb += "	e.id,  ";
        sb += "	t.id, t.nombre, t.descripcion, t.titulo, t.interpretacion, t.notas,  ";
        sb += "	p.id, p.nombre,  ";
        sb += "	g.id, g.nombre, ";
        sb += "	c.id, c.codigo, c.nombre, ";
        sb += "	s.id, s.nombre, s.maximo,  ";
        sb += "	d.id, d.pregunta ";
        sb += "	from cv_evaluacion e ";
        sb += "	inner join proveedor p on p.id = e.proveedor ";
        sb += "	inner join gerencia g on g.id = e.gerencia ";
        sb += "	inner join convenio c on c.id = e.convenio ";
        sb += "	inner join cv_evaluacion_template t on t.id = e.cv_evaluacion_template and t.eliminado = false ";
        sb += "	inner join cv_evaluacion_seccion s on s.cv_evaluacion_template = t.id and s.eliminado = false ";
        sb += "	inner join cv_evaluacion_template_det d on d.cv_evaluacion_seccion = s.id and d.eliminado = false ";
        sb += "	where t.eliminado = false ";
        sb += "	and e.id = " + idEvaluacion;
        sb += "	order by s.id, d.id ";

        List<Object[]> lo = em.createNativeQuery(sb).getResultList();

        if (lo != null) {
            vo = new EvaluacionVo();
            int i = 0;
            int seccionID = 0;
            int sumaMax = 0;
            SeccionVo sec = null;
            PreguntaVo preg = null;
            for (Object[] obj : lo) {
                if (i == 0) {
                    vo.setId((Integer) obj[0]);

                    vo.setTemplateId((Integer) obj[1]);
                    vo.setTemplateNombre((String) obj[2]);
                    vo.setTemplateDescripcion((String) obj[3]);
                    vo.setTemplateTitulo((String) obj[4]);
                    vo.setTemplateInterpretacion((String) obj[5]);
                    vo.setTemplateNotas((String) obj[6]);

                    vo.setProveedorId((Integer) obj[7]);
                    vo.setNombreProveedor((String) obj[8]);

                    vo.setGerenciaId((Integer) obj[9]);
                    vo.setNombreGerencia((String) obj[10]);

                    vo.setConvenioId((Integer) obj[11]);
                    vo.setConvenioCodigo((String) obj[12]);
                    vo.setConvenioNombre((String) obj[13]);

                }

                if (seccionID != (Integer) obj[14]) {
                    if (i > 0) {
                        vo.getSecciones().add(sec);
                    }
                    sec = new SeccionVo();
                    sec.setSeccionId((Integer) obj[14]);
                    sec.setSeccionNombre((String) obj[15]);
                    sec.setSeccionMaximo((Integer) obj[16]);
                    sumaMax += sec.getSeccionMaximo();
                    seccionID = (Integer) obj[14];
                }

                preg = new PreguntaVo();
                preg.setPreguntaId((Integer) obj[17]);
                preg.setPregunta((String) obj[18]);

                sec.getPreguntas().add(preg);

                i++;

            }
            vo.setSumatoriaMax(sumaMax);
            if (sec != null) {
                vo.getSecciones().add(sec);
            }
        }
        return vo;
    }

    
    public List<EvaluacionRespuestaVo> traerEvaluacionRespuestas(int idApCampo, int idGerencia, int idProveedor, Date fechaI, Date fechaF, int tipoID) {
        List<EvaluacionRespuestaVo> lvo = null;
        String sb = "";
        sb += "	select e.id, c.codigo, e.nombre_gerencia, e.nombre_proveedor, e.fecha, r.seccion, r.puntos, r.puntostotal, e.observaciones, e.nombre_gerencia, e.correo ";
        sb += "	from cv_evaluacion e ";
        sb += "	inner join cv_evaluacion_resp r on r.cv_evaluacion = e.id and r.eliminado = false ";
        sb += "	inner join convenio c on c.id = e.convenio and c.ap_campo = " + idApCampo;
        sb += "	              and c.cv_clasificacion in (select coalesce(b.id, a.id, 0) from cv_clasificacion a left join cv_clasificacion b on b.id = a.cv_clasificacion where a.id = " + tipoID + ")   ";
        sb += "	where e.eliminado = false ";
        sb += "	and e.contestada = true ";

//        if (idConvenio > 0) {
//            sb += "	and e.convenio =  " + idConvenio;
//        }

        if (idGerencia > 0) {
            sb += "	and e.gerencia =  " + idGerencia;
        }

        if (idProveedor > 0) {
            sb += "	and e.proveedor =  " + idProveedor;
        }

        if (fechaI != null && fechaF != null) {
            sb += " and e.fecha between   ?  and ? ";
        }

        sb += "	group by e.id, c.codigo, e.nombre_gerencia, e.nombre_proveedor, e.fecha, r.seccion, r.puntos, r.puntostotal, e.observaciones, e.nombre_gerencia, e.correo ";
        sb += "	order by e.id, e.nombre_proveedor ";

        List<Object[]> lo = null;
        if (fechaI != null && fechaF != null) {
            lo = em.createNativeQuery(sb).setParameter(1, fechaI).setParameter(2, fechaF).getResultList();
        } else {
            lo = em.createNativeQuery(sb).getResultList();
        }

        if (lo != null) {
            lvo = new ArrayList<>();
            int i = 0;
            int idEva = 0;
            EvaluacionRespuestaVo vo = null;
            for (Object[] obj : lo) {
                if (idEva != (Integer) obj[0]) {
                    if (i == 0) {
                        idEva = (Integer) obj[0];
                        vo = new EvaluacionRespuestaVo();
                        vo.setIdEvaluacion((Integer) obj[0]);
                        vo.setCodigoConvenio((String) obj[1]);
                        vo.setNombreGerenciaRE((String) obj[2]);
                        vo.setNombreGerencia((String) obj[2]);
                        vo.setNombreProveedor((String) obj[3]);
                        vo.setFecha((Date) obj[4]);
                        vo.setPuntosTotal(((BigDecimal) obj[7]).doubleValue());
                        vo.setObservaciones((String) obj[8]);
                        vo.setNombreGerenciaRE((String) obj[9]);
                        vo.setCorreo((String) obj[10]);
                        vo = castEvaluacionRespuestaVo(vo, obj);
                        i++;
                    } else {
                        lvo.add(vo);
                        idEva = (Integer) obj[0];
                        vo = new EvaluacionRespuestaVo();
                        vo.setIdEvaluacion((Integer) obj[0]);
                        vo.setCodigoConvenio((String) obj[1]);
                        vo.setNombreGerenciaRE((String) obj[2]);
                        vo.setNombreGerencia((String) obj[2]);
                        vo.setNombreProveedor((String) obj[3]);
                        vo.setFecha((Date) obj[4]);
                        vo.setPuntosTotal(((BigDecimal) obj[7]).doubleValue());
                        vo.setObservaciones((String) obj[8]);
                        vo.setNombreGerenciaRE((String) obj[9]);
                        vo.setCorreo((String) obj[10]);
                        vo = castEvaluacionRespuestaVo(vo, obj);
                        i++;
                    }
                } else {
                    vo = castEvaluacionRespuestaVo(vo, obj);
                }
            }
            if (vo != null && vo.getIdEvaluacion() > 0) {
                lvo.add(vo);
            }
        }
        return lvo;
    }

    private EvaluacionRespuestaVo castEvaluacionRespuestaVo(EvaluacionRespuestaVo aux, Object[] obj) {

        //5 y 6
        if ("Calidad del Servicio".equals(obj[5])) {
            aux.getCalidadDelServicio().setTotal(((BigDecimal) obj[6]).doubleValue());
        }
        if ("Cumplimiento de plazo".equals(obj[5])) {
            aux.getCumplimientoDePlazo().setTotal(((BigDecimal) obj[6]).doubleValue());
        }
        if ("Cumplimiento HSE".equals(obj[5])) {
            aux.getCumplimientoHSE().setTotal(((BigDecimal) obj[6]).doubleValue());
        }
        if ("Servicio diario".equals(obj[5])) {
            aux.getServicioDiario().setTotal(((BigDecimal) obj[6]).doubleValue());
        }
        if ("Cumplimiento de cantidad".equals(obj[5])) {
            aux.getCumplimientoDeCantidad().setTotal(((BigDecimal) obj[6]).doubleValue());
        }
        if ("Servicio durante y postventa".equals(obj[5])) {
            aux.getServicioDurantePostventa().setTotal(((BigDecimal) obj[6]).doubleValue());
        }
        if ("Seguridad y medio ambiente".equals(obj[5])) {
            aux.getSeguridadMedioAmbiente().setTotal(((BigDecimal) obj[6]).doubleValue());
        }
        if ("Documentacion final".equals(obj[5])) {
            aux.getDocumentacionFinal().setTotal(((BigDecimal) obj[6]).doubleValue());
        }
        if ("Servicio durante la obra".equals(obj[5])) {
            aux.getServicioDuranteObra().setTotal(((BigDecimal) obj[6]).doubleValue());
        }

        //5 y 6                    
        return aux;
    }

    
    public List<SelectItem> getProveedores(int apCampoID) {
        List<SelectItem> lst = null;
        String sb = "";
        sb += "	select id, nombre from ( ";
        sb += "	select p.id, p.nombre ";
        sb += "	from cv_evaluacion a ";
        sb += "	inner join proveedor p on p.id = a.proveedor ";
        sb += "	inner join convenio c on c.id = a.convenio ";
        sb += "	where a.eliminado = false ";
        sb += "	and a.contestada = true ";
        sb += "	and c.ap_campo = " + apCampoID;
        sb += "	union ";
        sb += "	select p.id,p.nombre ";
        sb += "	from cv_historial_externo h ";
        sb += "	inner join proveedor p on p.id = h.proveedor ";
        sb += "	where h.eliminado = false ";
        sb += "	and h.ap_campo = " + apCampoID;
        sb += "	group by p.id, p.nombre ";
        sb += "	) as prov order by prov.nombre ";

        List<Object[]> lo = em.createNativeQuery(sb).getResultList();

        if (lo != null && lo.size() > 0) {
            lst = new ArrayList<SelectItem>();
            for (Object[] objects : lo) {
                SelectItem item = new SelectItem((Integer) objects[0], String.valueOf(objects[1]));
                lst.add(item);
            }
        }

        return lst;
    }

    
    public List<SelectItem> getGerencias(int apCampoID) {
        List<SelectItem> lst = null;
        String sb = "";
        sb += "	select id,nombre from ( ";
        sb += "	select g.id, g.nombre ";
        sb += "	from cv_evaluacion a ";
        sb += "	inner join gerencia g on g.id = a.gerencia ";
        sb += "	inner join convenio c on c.id = a.convenio ";
        sb += "	where a.eliminado = false ";
        sb += "	and a.contestada = true ";
        sb += "	and c.ap_campo = " + apCampoID;
        sb += "	union ";
        sb += "	select g.id, g.nombre ";
        sb += "	from cv_historial_externo h ";
        sb += "	inner join gerencia g on g.id = h.gerencia ";
        sb += "	where h.eliminado = false ";
        sb += "	and h.ap_campo = " + apCampoID;
        sb += "	group by g.id, g.nombre ";
        sb += "	) as gerr order by gerr.nombre ";

        List<Object[]> lo = em.createNativeQuery(sb).getResultList();

        if (lo != null && lo.size() > 0) {
            lst = new ArrayList<SelectItem>();
            for (Object[] objects : lo) {
                SelectItem item = new SelectItem((Integer) objects[0], String.valueOf(objects[1]));
                lst.add(item);
            }
        }

        return lst;
    }

    
    public List<SelectItem> getConvenios(int apCampoID) {
        List<SelectItem> lst = null;

        String sb = "";
        sb += "	select c.id, c.codigo ";
        sb += "	from cv_evaluacion a ";
        sb += "	inner join convenio c on c.id = a.convenio ";
        sb += "	where a.eliminado = false ";
        sb += "	and a.contestada = true ";
        sb += "	and c.ap_campo = 1 ";
        sb += "	group by c.id, c.codigo ";

        List<Object[]> lo = em.createNativeQuery(sb).getResultList();

        if (lo != null && lo.size() > 0) {
            lst = new ArrayList<SelectItem>();
            for (Object[] objects : lo) {
                SelectItem item = new SelectItem((Integer) objects[0], String.valueOf(objects[1]));
                lst.add(item);
            }
        }

        return lst;
    }
}
