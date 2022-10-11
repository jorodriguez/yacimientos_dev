/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.campo.nuevo.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import sia.constantes.Constantes;
import sia.modelo.ApCampo;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.ApCampoVo;
import sia.util.UtilLog4j;

/**
 *
 * @author jorodriguez
 */
@Stateless 
public class ApCampoImpl extends AbstractFacade<ApCampo>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Inject
    DSLContext dbCtx;

    public ApCampoImpl() {
        super(ApCampo.class);
    }

    
    public List<ApCampoVo> traerApCampo() {
        Query q;
        try {
            q = em.createNativeQuery("select id,compania,nombre,descripcion "
                    + " From AP_CAMPO"
                    + " where eliminado='False'");
            List<Object[]> lista = q.getResultList();
            List<ApCampoVo> lap = new ArrayList<ApCampoVo>();
            for (Object[] object : lista) {
                lap.add(castVo(object));
            }
            return lap;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer likes");
            return null;
        }
    }

    
    public List<ApCampoVo> traerApCampoPorEmpresa(String rfc) {

        String ret = "";
        clearQuery();
        try {
            query.append("select c.id, c.compania, c.nombre, c.descripcion ");
            query.append(" From AP_CAMPO c");
            query.append(" where eliminado='False'");
            query.append(" and c.compania = '").append(rfc).append("'");
            List<Object[]> lista = em.createNativeQuery(query.toString()).getResultList();
            List<ApCampoVo> lap = new ArrayList<ApCampoVo>();
            for (Object[] object : lista) {
                lap.add(castVo(object));
            }
            return lap;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer likes");
            return null;
        }
    }

    private ApCampoVo castVo(Object[] obj) {
        ApCampoVo apCampoVo = new ApCampoVo();
        apCampoVo.setId((Integer) obj[0]);
        apCampoVo.setCompania((String) obj[1]);
        apCampoVo.setNombre((String) obj[2]);
        apCampoVo.setDescripcion((String) obj[3]);
        return apCampoVo;
    }

    @Deprecated
    
    public List<CampoVo> getAllField() {
        List<Object[]> list;
        Query q = em.createNativeQuery("SELECT a.ID, "//0
                + " a.NOMBRE,"//1
                + " a.DESCRIPCION" //2
                + " FROM AP_CAMPO a "
                + " WHERE a.ELIMINADO='" + Constantes.NO_ELIMINADO + "'");
        list = q.getResultList();
        UtilLog4j.log.info(this, "query: " + q.toString());
        List<CampoVo> voList = new ArrayList<CampoVo>();

        for (Object[] objeto : list) {
            voList.add(castPuesto(objeto));
        }
        UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " Campos");
        return voList;
    }

    
    public Map<String, List<ApCampoVo>> getAllFieldsByCompany() {
        Map<String, List<ApCampoVo>> retVal = null;

        try {

            retVal = dbCtx.fetch(
                    "SELECT c.rfc, a.* \n"
                    + "FROM compania c \n"
                    + "   INNER JOIN ap_campo a ON a.compania = c.rfc \n"
                    + "WHERE a.eliminado = false \n"
                    + "ORDER BY c.rfc, a.nombre"
            ).intoGroups(DSL.field("rfc", String.class), ApCampoVo.class);

        } catch (DataAccessException e) {
            UtilLog4j.log.warn(this, "", e);
            retVal = Collections.emptyMap();
        }

        return retVal;
    }

    
    public List<CampoVo> getAllFieldExceptCurrent(int idCampo) {
        List<Object[]> list;
        Query q = em.createNativeQuery("SELECT a.ID, "//0
                + " a.NOMBRE,"//1
                + " a.DESCRIPCION" //2
                + " FROM AP_CAMPO a "
                + " WHERE a.ELIMINADO='" + Constantes.NO_ELIMINADO + "'"
                + " and a.id <> " + idCampo);
        list = q.getResultList();
        UtilLog4j.log.info(this, "query: " + q.toString());
        List<CampoVo> voList = new ArrayList<CampoVo>();

        for (Object[] objeto : list) {
            voList.add(castPuesto(objeto));
        }
        UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " Campos");
        return voList;
    }

    private CampoVo castPuesto(Object[] objeto) {
        CampoVo vo = new CampoVo();
        vo.setId((Integer) objeto[0]);
        vo.setNombre(String.valueOf(objeto[1]));
        vo.setDescripcion(String.valueOf(objeto[2]));
        return vo;

    }

    
    public ApCampo buscarPorNombre(String campo) {
        String sb = "select a.* from ap_Campo a where a.nombre = '" + campo + "'";
        return (ApCampo) em.createNativeQuery(sb, ApCampo.class).getSingleResult();
    }

    
    public ApCampoVo buscarPorId(int idCampo) {
        clearQuery();
        try {
            query.append("select c.id, c.compania, c.nombre, c.descripcion ");
            query.append(" From AP_CAMPO c");
            query.append(" where eliminado='False'");
            query.append(" and c.id = ").append(idCampo);
            Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            return castVo(obj);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer likes");
            return null;
        }
    }

    
    public ApCampo buscarCampoPorId(int idCampo) {
        clearQuery();
        try {
            query.append("select c");
            query.append(" From ApCampo c");
            query.append(" where eliminado = 'False'");
            query.append(" and c.id = ").append(idCampo);
            return em.createQuery(query.toString(), ApCampo.class).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer campo");
            return null;
        }
    }

    
    public int campoByUserAndCompani(String rfc, String user) {
        String r = "";
        int regresa = 0;

        try {
            r = dbCtx.fetchValue(
                    "SELECT  a.id FROM ap_campo a \n"
                    + "                INNER JOIN ap_campo_usuario_rh_puesto acr on acr.ap_campo = a.id and acr.eliminado = ?\n"
                    + "                WHERE a.compania = ? and acr.usuario = ? order by a.id limit 1",
                    Constantes.FALSE, rfc, user
            ).toString();

            regresa = Integer.parseInt(r);
        } catch (NumberFormatException | DataAccessException e) {
            UtilLog4j.log.error(e);
        }

        return regresa;
    }

    
    public List<ApCampoVo> traerCampoConCartaIntencion() {
        String ret = "";
        clearQuery();
        try {
            query.append("select c.id, c.compania, c.nombre, c.descripcion ")
                    .append(" From AP_CAMPO c")
                    .append(" where c.eliminado = false")
                    .append(" and c.carta_intencion = true ")
                    .append(" order by c.nombre asc");
            List<Object[]> lista = em.createNativeQuery(query.toString()).getResultList();
            List<ApCampoVo> lap = new ArrayList<ApCampoVo>();
            for (Object[] object : lista) {
                lap.add(castVo(object));
            }
            return lap;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer campos sin carta de intencion");
            return null;
        }
    }

}
