/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.rh.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.RhCampoGerencia;
import sia.modelo.Usuario;
import sia.modelo.gerencia.vo.RhCampoGerenciaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class RhCampoGerenciaImpl extends AbstractFacade<RhCampoGerencia>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;    
    @Inject
    private ApCampoImpl apCampoRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private ApCampoGerenciaImpl  apCampoGerenciaRemote;
    
    private StringBuilder bodyQuery = new StringBuilder();

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RhCampoGerenciaImpl() {
        super(RhCampoGerencia.class);
    }

    public void clearBodyQuery() {
        this.bodyQuery.delete(0, this.bodyQuery.length());
    }

    
    public List<RhCampoGerenciaVo> findAll(int idApCampo, int idGerencia, String orderByField, boolean sortAscending, boolean eliminado) {

        clearBodyQuery();
        this.bodyQuery.append("SELECT cg.ID, "); //0
        this.bodyQuery.append("cg.AP_CAMPO AS ID_AP_CAMPO, "); //1
        this.bodyQuery.append("c.NOMBRE AS NOMBRE_AP_CAMPO, "); //2
        this.bodyQuery.append("cg.GERENCIA AS ID_GERENCIA, "); //3
        this.bodyQuery.append("g.NOMBRE AS NOMBRE_GERENCIA, "); //4
        this.bodyQuery.append("FROM RH_CAMPO_GERENCIA cg, AP_CAMPO c, GERENCIA g ");
        this.bodyQuery.append("WHERE cg.ELIMINADO='").append(eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).append("' ");
        if (idGerencia > 0) {
            this.bodyQuery.append("AND cg.GERENCIA=").append(idGerencia).append(" ");
            this.bodyQuery.append("AND g.ELIMINADO='").append(Constantes.BOOLEAN_FALSE).append("' ");
        }
        if (idApCampo > 0) {
            this.bodyQuery.append("AND cg.AP_CAMPO=").append(idApCampo).append(" ");
            this.bodyQuery.append("AND c.ELIMINADO='").append(Constantes.BOOLEAN_FALSE).append("' ");
        }
        this.bodyQuery.append("AND cg.GERENCIA=g.ID ");
        this.bodyQuery.append("AND u.AP_CAMPO=c.ID ");

        if (orderByField != null && !orderByField.isEmpty()) {
            if ("id".equals(orderByField)) {
                this.bodyQuery.append("ORDER BY cg.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
            } else if ("nombre".equals(orderByField)) {
                this.bodyQuery.append("ORDER BY g.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
            }
        }

        Query query = em.createNativeQuery(this.bodyQuery.toString());

        UtilLog4j.log.info(this, query.toString());

        List<Object[]> result = query.getResultList();
        List<RhCampoGerenciaVo> list = new ArrayList<>();
        RhCampoGerenciaVo vo = null;

        for (Object[] objects : result) {
            vo = new RhCampoGerenciaVo();
            vo.setId((Integer) objects[0]);
            vo.setIdApCampo((Integer) objects[1]);
            vo.setNombreApCampo((String) objects[2]);
            vo.setIdGerencia((Integer) objects[3]);
            vo.setNombreGerencia((String) objects[4]);
            list.add(vo);
        }

        UtilLog4j.log.info(this, "Se encontraron " + (list.isEmpty() ? "0" : list.size()) + " RhCampoGerencia");

        return (list.isEmpty() ? Collections.EMPTY_LIST : list);
    }

    
    public RhCampoGerencia buscarCampoGerencia(int idApCampo, int idGerencia) {
      try{
        clearBodyQuery();
        this.bodyQuery.append(" Select *");
        this.bodyQuery.append(" From RH_CAMPO_GERENCIA rh");
        this.bodyQuery.append(" Where rh.AP_CAMPO = ").append(idApCampo).append(" and rh.GERENCIA = ").append(idGerencia);

        Query consulta = em.createNativeQuery(this.bodyQuery.toString(),RhCampoGerencia.class);

        UtilLog4j.log.info(this, consulta.toString());

        return ((RhCampoGerencia) consulta.getSingleResult());
        
      }catch(Exception e){
          UtilLog4j.log.fatal(this, "Excepcion al buscar rhCampoGerenca "+e.getMessage());
          return null;
      }
        
    }
    
    
    
    public boolean agregarRelacionCampoGerencia(int idApCampo, int idGerencia,String idResponsable, String usuarioRealiza) {
        UtilLog4j.log.info(this, "agregarRelacionCampoGerencia "+idApCampo);
        UtilLog4j.log.info(this, "agregarRelacionCampoGerencia "+idGerencia);
        UtilLog4j.log.info(this, "agregarRelacionCampoGerencia "+idResponsable);
        UtilLog4j.log.info(this, "agregarRelacionCampoGerencia "+usuarioRealiza);
        try{
            RhCampoGerencia campoGerencia = new RhCampoGerencia();
            campoGerencia.setApCampo(apCampoRemote.find(idApCampo));
            campoGerencia.setGerencia(gerenciaRemote.find(idGerencia));
            campoGerencia.setGenero(new Usuario(usuarioRealiza));
            campoGerencia.setFechaGenero(new Date());
            campoGerencia.setHoraGenero(new Date());
            campoGerencia.setEliminado(Constantes.BOOLEAN_FALSE);            
            create(campoGerencia);            
            //log
            UtilLog4j.log.info(this, "Se ha agrehado EhCampoGerencia");
          //guardar en la tabla 
            apCampoGerenciaRemote.guardarCampoGerenciaResponsable(usuarioRealiza,idResponsable,idApCampo, idGerencia);
            UtilLog4j.log.info(this, "Se a agredo correctamente la relacion en RH_CAMPO_GERENCIA u en AP_CAMPO_GERENCIA");
            
            return true;
        }catch(Exception e){
            UtilLog4j.log.fatal(this, "Excepcion al agregar la relacion de gerencia y campo "+e.getMessage());
            return false;
        }
    }
    
}
