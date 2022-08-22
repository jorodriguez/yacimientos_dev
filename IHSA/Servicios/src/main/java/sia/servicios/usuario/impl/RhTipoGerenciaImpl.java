/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.usuario.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import sia.constantes.Constantes;
import sia.modelo.Gerencia;
import sia.modelo.RhTipoGerencia;
import sia.modelo.Usuario;
import sia.modelo.gerencia.vo.RhTipoGerenciaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioGerenciaVo;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.sgl.impl.SgTipoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class RhTipoGerenciaImpl extends AbstractFacade<RhTipoGerencia> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private SgTipoImpl sgTipoRemote;    
    private StringBuilder bodyQuery = new StringBuilder();

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RhTipoGerenciaImpl() {
        super(RhTipoGerencia.class);
    }

    private void clearBodyQuery() {
        bodyQuery.delete(0, bodyQuery.length());
    }

    
    public boolean agregarGerencia(int idGerencia, String idUsuario) {
        try {
            Gerencia gerenciaAgregar = gerenciaRemote.find(idGerencia);
            if (gerenciaAgregar != null) {
                RhTipoGerencia rhTipoGerencia = new RhTipoGerencia();
//                rhTipoGerencia.setGerencia(gerenciaAgregar);
                rhTipoGerencia.setSgTipo(sgTipoRemote.find(22));
                rhTipoGerencia.setEliminado(Constantes.BOOLEAN_FALSE);
                rhTipoGerencia.setGenero(new Usuario(idUsuario));
                rhTipoGerencia.setFechaGenero(new Date());
                rhTipoGerencia.setHoraGenero(new Date());
                create(rhTipoGerencia);
                return true;
            }
            return false;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion al agregar la gerencia a la lista de gerencias que liberan al empleado " + e.getMessage());
            return false;
        }
    }

    
    public List<UsuarioGerenciaVo> traerUsuarioNoLiberadoGerencia(String idUsuario) {
        UtilLog4j.log.info(this,"SgViajeImpl.getAllSgViajeForAutomaticReturn()");
        List<UsuarioGerenciaVo> lv = null;
        List<Object[]> l;
        UsuarioGerenciaVo o;
        String porUsuario = "";
        if (!idUsuario.isEmpty()) {
            porUsuario = " and u.id = '" + idUsuario + "'";
        }
        try {
            clearBodyQuery();
            bodyQuery.append("SELECT ug.id, u.id, u.nombre, g.id, g.nombre, u.fecha_baja");
            bodyQuery.append(" FROM  rh_usuario_gerencia ug, usuario u, gerencia g ");
            bodyQuery.append(" WHERE ug.usuario = u.id and ug.gerencia = g.id and ug.liberado = false and ug.eliminado = false ");
            bodyQuery.append(porUsuario);
            bodyQuery.append(" order by g.nombre asc");

            l = em.createNativeQuery(bodyQuery.toString()).getResultList();

            UtilLog4j.log.info(this,"Size " + l.size());

            if (l != null && !l.isEmpty()) {
                lv = new ArrayList<>();
                for (Object[] objects : l) {
                    lv.add(castUsuarioGerenciaVO(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Error al traer usuario  gerenciad " + e.getMessage());
            return null;
        }
        return lv;
    }

    
    public boolean buscarGerenciaExistente(int idGerencia) {
        try {
            clearBodyQuery();
            bodyQuery.append(" Select * ");
            bodyQuery.append(" From RH_TIPO_GERENCIA t");
            bodyQuery.append(" Where t.GERENCIA = ").append(idGerencia);
            bodyQuery.append(" and t.ELIMINADO = 'False'");

            if (em.createNativeQuery(bodyQuery.toString()).getSingleResult() != null) {
                UtilLog4j.log.info(this,"si esta en la lista ");
                return true;
            }
            return false;
        } catch (NonUniqueResultException nure) {
            UtilLog4j.log.fatal(this,nure.getMessage());
            UtilLog4j.log.fatal(this,"si esta en la lista " + nure.getMessage());
            return true;
        } catch (NoResultException nre) {
            UtilLog4j.log.fatal(this,nre.getMessage());
            UtilLog4j.log.fatal(this,"no esta en la lista:" + nre.getMessage());
            return false;
        }
    }

    
    public List<RhTipoGerenciaVo> traerListaRhTipoGerencia() {
        UtilLog4j.log.info(this,"traerListaRhTipoGerencia()");
        List<RhTipoGerenciaVo> lv = null;
        List<Object[]> l;
        RhTipoGerenciaVo rhTipo = null;
        try {
            clearBodyQuery();
            bodyQuery.append("SELECT r.id,r.GERENCIA,g.NOMBRE,r.SG_TIPO,t.NOMBRE");
            bodyQuery.append(" FROM RH_TIPO_GERENCIA r,gerencia g,sg_tipo t");
            bodyQuery.append(" where r.GERENCIA = g.ID and r.SG_TIPO = t.ID");
            bodyQuery.append(" and r.ELIMINADO = 'False'");
            bodyQuery.append(" and g.ELIMINADO = 'False'");
            bodyQuery.append(" and t.ELIMINADO = 'False'");

            l = em.createNativeQuery(bodyQuery.toString()).getResultList();

            UtilLog4j.log.info(this,"Size " + l.size());

            if (l != null && !l.isEmpty()) {
                lv = new ArrayList<RhTipoGerenciaVo>();
                for (Object[] objects : l) {

                    rhTipo = new RhTipoGerenciaVo();
                    rhTipo.setId((Integer) objects[0]);
//                    rhTipo.setIdGerencia((Integer) objects[1]);
//                    rhTipo.setNombreGerencia((String) objects[2]);
                    rhTipo.setIdSgTipo((Integer) objects[3]);
                    rhTipo.setNombreSgTipo((String) objects[4]);
                    lv.add(rhTipo);
                }
            }
            return lv;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Error al traer la lista de gerencias para liberar " + e.getMessage());
            return null;
        }
    }

    private UsuarioGerenciaVo castUsuarioGerenciaVO(Object[] objects) {
        UsuarioGerenciaVo vo;
        try {
            vo = new UsuarioGerenciaVo();
            vo.setIdUsuarioGerencia((Integer) objects[0]);
            vo.setIdUsuario((String) objects[1]);
            vo.setNombre((String) objects[2]);
            vo.setIdGerencia((Integer) objects[3]);
            vo.setGerencia((String) objects[4]);
            vo.setLiberado((String) objects[5]);
            return vo;
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }

    
    public List<RhTipoGerenciaVo> findAllRhTipoGerenciaByRhCampoGerencia(int idRhCampoGerencia, String orderByField, boolean sortAscending, boolean eliminado) {
        clearBodyQuery();
        this.bodyQuery.append(" SELECT tg.ID, "); //0
        this.bodyQuery.append(" tg.SG_TIPO AS ID_SG_TIPO, "); //1
        this.bodyQuery.append(" t.NOMBRE AS NOMBRE_SG_TIPO, "); //2
        this.bodyQuery.append(" tg.USUARIO AS USUARIO, "); //3
        this.bodyQuery.append(" u.NOMBRE AS NOMBRE_USUARIO, "); //4
        this.bodyQuery.append(" tg.RH_CAMPO_GERENCIA AS RH_CAMPO_GERENCIA "); //5
        this.bodyQuery.append(" FROM RH_TIPO_GERENCIA tg, USUARIO u, SG_TIPO t ");
        this.bodyQuery.append(" WHERE tg.ELIMINADO='").append(Constantes.BOOLEAN_FALSE).append("' ");
        if (idRhCampoGerencia > 0) {
            this.bodyQuery.append(" AND tg.RH_CAMPO_GERENCIA=").append(idRhCampoGerencia).append(" ");
        }
        this.bodyQuery.append(" AND tg.SG_TIPO = 23 ");
        this.bodyQuery.append(" AND tg.SG_TIPO=t.ID ");
        this.bodyQuery.append(" AND tg.USUARIO=u.ID ");

        if (orderByField != null && !orderByField.isEmpty()) {
            if ("id".equals(orderByField)) {
                this.bodyQuery.append("ORDER BY tg.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
            } else if ("nombre".equals(orderByField)) {
                this.bodyQuery.append("ORDER BY u.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
            }
        }

        Query consulta = em.createNativeQuery(this.bodyQuery.toString());

        UtilLog4j.log.info(this,consulta.toString());

        List<Object[]> result = consulta.getResultList();
        List<RhTipoGerenciaVo> list = new ArrayList<>();
        RhTipoGerenciaVo vo = null;

        for (Object[] objects : result) {
            vo = new RhTipoGerenciaVo();
            vo.setId((Integer) objects[0]);
            vo.setIdSgTipo((Integer) objects[1]);
            vo.setNombreSgTipo((String) objects[2]);
            vo.setIdUsuario((String) objects[3]);
            vo.setNombreUsuario((String) objects[4]);
            vo.setIdRhCampoGerencia((Integer) objects[5]);
            list.add(vo);
        }

        UtilLog4j.log.info(this,"Se encontraron " + (list.isEmpty() ? "0" : list.size()) + " RhTipoGerenciaImpl");

        return (list.isEmpty() ? Collections.EMPTY_LIST : list);
    }

    
    public boolean isLiberador(String idUsuario) {
        clearBodyQuery();

        this.bodyQuery.append("SELECT COUNT(*) FROM RH_TIPO_GERENCIA tg WHERE tg.USUARIO='").append(idUsuario).append("' AND tg.ELIMINADO='False'");

        Query consulta = em.createNativeQuery(this.bodyQuery.toString());

        UtilLog4j.log.info(this,consulta.toString());

        long result = (Long) consulta.getSingleResult();


        UtilLog4j.log.info(this,"El Usuario " + idUsuario + " es liberador: " + (result > 0));

        return (result > 0);
    }
}