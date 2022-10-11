/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.comunicacion.impl;

import java.util.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.CoMeGusta;
import sia.modelo.CoNoticia;
import sia.modelo.CoNoticiaMeGusta;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.vo.MeGustaVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@Stateless 
public class CoNoticiaMeGustaImpl extends AbstractFacade<CoNoticiaMeGusta> {

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    //
  
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CoNoticiaMeGustaImpl() {
        super(CoNoticiaMeGusta.class);
    }

    
    public boolean crearMeGusta(CoNoticia noticia, CoMeGusta meGusta, String idUsuario) {
        boolean retVal = false;
        
        try {
            CoNoticiaMeGusta relacion = new CoNoticiaMeGusta();
            relacion.setGenero(new Usuario(idUsuario));
            relacion.setCoNoticia(noticia);
            relacion.setCoMeGusta(meGusta);
            relacion.setEliminado(Constantes.BOOLEAN_FALSE);
            relacion.setFechaGenero(new Date());
            relacion.setHoraGenero(new Date());
            create(relacion);
            retVal = true;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        
        return retVal;
    }

    
    public boolean eliminarMeGusta(Integer idNoticia, Integer idMeGusta, String idUsuario) {
        boolean retVal = false;
        
        try {
            CoNoticiaMeGusta relacion = findRelacionFromIdMegusta(idNoticia, idMeGusta);
            if (relacion != null) {
                relacion.setModifico(new Usuario(idUsuario));
                relacion.setFechaModifico(new Date());
                relacion.setHoraModifico(new Date());
                relacion.setEliminado(Constantes.BOOLEAN_TRUE);
                edit(relacion);
            }
            retVal = true;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        
        return retVal;
    }

    private CoNoticiaMeGusta findRelacionFromIdMegusta(Integer idNoticia, Integer idMegusta) {
        CoNoticiaMeGusta retVal = null;
        
        try {
            retVal =  (CoNoticiaMeGusta) em.createQuery(
                    "SELECT r FROM CoNoticiaMeGusta r "
                    + "WHERE r.coNoticia.id = :idNoticia AND r.coMeGusta.id = :idMeGusta AND r.eliminado = :elim ")
                    .setParameter("idNoticia", idNoticia)
                    .setParameter("idMeGusta", idMegusta)
                    .setParameter("elim", Constantes.BOOLEAN_FALSE)
                    .getSingleResult();
        } catch (Exception e) {
            LOGGER.error(e);
        }
        
        return retVal;
    }

    
    public List<MeGustaVO> getLikes(Integer idNoticia, String idUsuario, int maxResult) {
        List<MeGustaVO> lista = null;
        
        Query qry;
        try {
            qry = em.createNativeQuery(
                    "SELECT lik.FECHA_GENERO,"
                    + " lik.HORA_GENERO,"
                    + " u.nombre,"
                    + " u.foto"
                    + " FROM CO_NOTICIA_ME_GUSTA notiLike,CO_MEGUSTA lik ,usuario u "
                    + " where notiLike.CO_NOTICIA= ? "
                    + " and notiLike.eliminado = ? "
                    + " AND notiLike.genero = u.id"
                    + " and notiLike.CO_MEGUSTA = lik.id"
                    + " AND lik.ELIMINADO = ? "
                    + " ORDER BY notiLike.id ASC")
                    .setParameter(1, idNoticia)
                    .setParameter(2, Constantes.BOOLEAN_FALSE)
                    .setParameter(3, Constantes.BOOLEAN_FALSE)
                    ;


            lista = castVo(qry.getResultList());
        } catch (Exception e) {
            LOGGER.error(e);
        }
        
        return lista;
    }

    private List<MeGustaVO> castVo(List lista) {
        List<MeGustaVO> lo = null;
        MeGustaVO meGustaVO;
        
        if (lista != null) {
            Collections.reverse(lista);
            lo = new ArrayList<MeGustaVO>();
            for (Iterator it = lista.iterator(); it.hasNext();) {
                Object[] obj = (Object[]) it.next();
                meGustaVO = new MeGustaVO();
                meGustaVO.setFechaGenero((Date) obj[0]);
                meGustaVO.setHoraGenero((Date) obj[1]);
                meGustaVO.setNombreUsuario((String) obj[2]);
                meGustaVO.setFotoUsuario((String) obj[3]);
                lo.add(meGustaVO);
            }
        }
        
        return lo;
    }
}
