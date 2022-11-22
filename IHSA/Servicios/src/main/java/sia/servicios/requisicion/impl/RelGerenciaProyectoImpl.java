/*
 * RelGerenciaProyectoFacade.java
 * Creada el 21/12/2009, 06:30:17 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.requisicion.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.ProyectoOt;
import sia.modelo.RelGerenciaProyecto;
import sia.modelo.Usuario;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.RelGerenciaProyectoVO;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 21/12/2009
 */
@Stateless 
public class RelGerenciaProyectoImpl extends AbstractFacade<RelGerenciaProyecto>{

    @Inject
    private GerenciaImpl gerenciaServicioRemoto;
    
    
    // 
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RelGerenciaProyectoImpl() {
        super(RelGerenciaProyecto.class);
    }
//    
    //Actualizacion APCAMPO
    
    public List<ProyectoOtVo> getProyectoPorGerencia(int idGerencia, Object nombreCompania, Integer idApCampo) {

        UtilLog4j.log.info(this, "#getProyectoPorGerencia " + idApCampo);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT proy.ID,");
            sb.append(" proy.NOMBRE, ");
            sb.append(" proy.CUENTA_CONTABLE ");
            sb.append(" FROM COMPANIA comp, GERENCIA ge, REL_GERENCIA_PROYECTO genProy, PROYECTO_OT proy "); //Iberoamericana de Hidrocarburos S.A. de C.V.
            sb.append(" WHERE ge.id = ").append(idGerencia).append(" AND ge.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE);
            sb.append("' AND comp.NOMBRE = '").append(nombreCompania).append("' AND proy.ELIMINADO = '");
            sb.append(Constantes.BOOLEAN_FALSE).append("' AND proy.ABIERTO = '").append(Constantes.BOOLEAN_TRUE);
            sb.append("' AND proy.AP_CAMPO = ").append(idApCampo).append(" AND genProy.ELIMINADO = '");
            sb.append(Constantes.BOOLEAN_FALSE).append("'");
            sb.append(" AND ge.ID = genProy.GERENCIA AND proy.ID = genProy.PROYECTO_OT AND comp.RFC = proy.COMPANIA");
            sb.append(" ORDER BY proy.NOMBRE ASC");

            UtilLog4j.log.info(this, "{{{{{{{{{{{{{{ " + sb.toString());

            List<ProyectoOtVo> le = null;
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                le = new ArrayList<ProyectoOtVo>();
                for (Object[] objects : lo) {
                    le.add(castReturnProyectoVO(objects));
                }
            }

            return le;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error: al obtener los proyectos OT'S " + e.getMessage());

            return null;
        }

//        return em.createQuery("SELECT r.proyectoOt FROM RelGerenciaProyecto r "
//                + " WHERE r.gerencia.nombre = :nombreGerencia "
//                + " AND r.gerencia.visible =:visible "
//                + " AND r.proyectoOt.compania.nombre = :nombreCompania "
//                + " AND r.proyectoOt.visible =:visible "
//                + " AND r.proyectoOt.apCampo.id = :idApCampo "
//                + " AND r.eliminado = :FALSE"
//                + " ORDER BY r.proyectoOt.nombre ASC").setParameter("nombreGerencia", nombreGerencia).setParameter("nombreCompania", nombreCompania).setParameter("idApCampo", idApCampo).setParameter("FALSE", Constantes.BOOLEAN_FALSE).setParameter("visible", Constantes.BOOLEAN_TRUE).getResultList();
    }

    private ProyectoOtVo castReturnProyectoVO(Object[] obj) {
        ProyectoOtVo proyectoOtVo = new ProyectoOtVo();
        proyectoOtVo.setId((Integer) obj[0]);
        proyectoOtVo.setNombre((String) obj[1]);
        proyectoOtVo.setCuentaContable((String) obj[2]);
        return proyectoOtVo;
    }

    
    public List<RelGerenciaProyectoVO> traerPorProyectoOT(int idProyecto) {
        UtilLog4j.log.fatal(this, "##traerProyectoOT " + idProyecto);
        try {
//            String q = "SELECT p.id, p.nombre, p.cuenta_Contable From REL_GERENCIA_PROYECTO rgp, proyecto_ot p"
//                    + "  WHERE  p.ap_campo = " + idApCampo
//                    + " AND rgp.gerencia =  " + idGerencia
//                    + " AND rgp.proyecto_ot = p.id "
//                    + " AND rgp.eliminado = 'False'";

            String q = "SELECT  gp.ID,"
                    + " pot.NOMBRE as nombre_proyecto,"
                    + " g.nombre as gerencia"
                    + " FROM REL_GERENCIA_PROYECTO gp,PROYECTO_OT pot,Gerencia g "
                    + " WHERE (gp.PROYECTO_OT = " + idProyecto + ") AND gp.PROYECTO_OT = pot.id AND g.id = gp.GERENCIA "
                    + " AND gp.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "' and g.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " ORDER BY gp.ID ASC ";

            List<RelGerenciaProyectoVO> le = new ArrayList<RelGerenciaProyectoVO>();
            List<Object[]> lo = em.createNativeQuery(q).getResultList();

            for (Object[] objects : lo) {
                UtilLog4j.log.info(this, "Entro " + objects[1]);
                RelGerenciaProyectoVO relVo = new RelGerenciaProyectoVO();
                relVo.setId((Integer) objects[0]);
                relVo.setNombreProyectoOT((String) objects[1]);
                relVo.setNombreGerencia((String) objects[2]);
                le.add(relVo);
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Error: al obtener los proyectos OT'S " + e.getMessage());
            return null;
        }

        //return em.createQuery("SELECT r FROM RelGerenciaProyecto r WHERE r.proyectoOt.id = :id ORDER BY r.id ASC").setParameter("id", idProyecto).getResultList();
    }

    
    public void guardarRelacionGerencia(Integer idProyectoOTNombre, int gerencia, String idUsuario) {
        try {
            RelGerenciaProyecto relGerenciaProyecto = new RelGerenciaProyecto();
            relGerenciaProyecto.setProyectoOt(new ProyectoOt(idProyectoOTNombre));
            relGerenciaProyecto.setGerencia(this.gerenciaServicioRemoto.find(gerencia));
            relGerenciaProyecto.setGenero(new Usuario(idUsuario));
            relGerenciaProyecto.setEliminado(Constantes.BOOLEAN_FALSE);
            relGerenciaProyecto.setFechaGenero(new Date());
            relGerenciaProyecto.setHoraGenero(new Date());
            this.create(relGerenciaProyecto);
            UtilLog4j.log.info(this, "Se creo correctamente");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al guardar la relacion " + e.getMessage());
        }
    }

    //Actualizacion APCAMPO
    
    public void eliminarRelacionGerencia(Integer idRelacion, String idUsuario) {
        RelGerenciaProyecto relGerenciaProyecto = find(idRelacion);
        relGerenciaProyecto.setEliminado(Constantes.BOOLEAN_TRUE);
        relGerenciaProyecto.setModifico(new Usuario(idUsuario));
        relGerenciaProyecto.setFechaModifico(new Date());
        relGerenciaProyecto.setHoraModifico(new Date());
        this.edit(relGerenciaProyecto);
    }

    
    public boolean buscarProyectoOtGerenciaRepetidos(int idProyectoOt, int idGerencia) {
        UtilLog4j.log.info(this, "Proyecto ot " + idProyectoOt + "  gerebcua " + idGerencia);
        try {
            String q = " select * "
                    + " from REL_GERENCIA_PROYECTO p"
                    + " where p.GERENCIA = " + idGerencia
                    + " and p.PROYECTO_OT = " + idProyectoOt
                    + " and p.ELIMINADO = 'False'";
            List<Object[]> lo = em.createNativeQuery(q).getResultList();
            UtilLog4j.log.info(this, " " + lo != null ? " es diferente null esta repetido " : "Es null no esta repetido ");
            return lo.isEmpty() ? false : true;

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Exception al buscar relacion repetida de proyecto ot y gerencia " + e.getMessage());
            return false;
        }

    }
}
