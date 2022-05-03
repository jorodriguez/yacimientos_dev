/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.comunicacion.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.CoNoticia;
import sia.modelo.CoNoticiaSiAdjunto;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.vo.NoticiaAdjuntoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jorodriguez
 */
@Stateless 
public class CoNoticiaSiAdjuntoImpl extends AbstractFacade<CoNoticiaSiAdjunto> {
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    private final static UtilLog4j LOGGER = UtilLog4j.log;
    
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    

    public CoNoticiaSiAdjuntoImpl() {
        super(CoNoticiaSiAdjunto.class);
    }
    

    
    public void addArchivoAdjunto(CoNoticia noticia, Usuario usuario, SiAdjunto siAdjunto) {
        
        try {
            CoNoticiaSiAdjunto coNoticiaSiAdjunto = new CoNoticiaSiAdjunto();
            coNoticiaSiAdjunto.setCoNoticia(noticia);
            coNoticiaSiAdjunto.setFechaGenero(new Date());
            coNoticiaSiAdjunto.setHoraGenero(new Date());
            coNoticiaSiAdjunto.setGenero(usuario);
            coNoticiaSiAdjunto.setSiAdjunto(siAdjunto);
            coNoticiaSiAdjunto.setEliminado(Constantes.BOOLEAN_FALSE);
            edit(coNoticiaSiAdjunto);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    
    public void deleteArchivoAdjunto(Integer idCoNoticiaSiAdjunto, Usuario usuarioModifico) {        
        try {
            CoNoticiaSiAdjunto re = find(idCoNoticiaSiAdjunto);            
            re.setEliminado(Constantes.BOOLEAN_TRUE);
            re.setFechaModifico(new Date());
            re.setHoraModifico(new Date());
            re.setModifico(usuarioModifico);
            edit(re);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    
    public List<NoticiaAdjuntoVO> getAdjuntosNoticia(Integer idNoticia, String idUsuario) {
        List lista = null;
        String qy;
        Query q;
        try {
            qy = " SELECT adj.ID,"
                    + " nAdj.id,"
                    + " adj.DESCRIPCION,"
                    + " adj.ELIMINADO,"
                    + " adj.GENERO,"
                    + " adj.FECHA_GENERO,"
                    + " adj.HORA_GENERO,"
                    + " adj.NOMBRE,"
                    + " adj.PESO,"
                    + " adj.TIPO_ARCHIVO,"
                    + " adj.URL,"
                    + " adj.UUID"
                 + " FROM  CO_NOTICIA_SI_ADJUNTO nAdj LEFT OUTER JOIN SI_ADJUNTO adj ON (adj.ID = nAdj.SI_ADJUNTO) "
                 + " WHERE ((nAdj.CO_NOTICIA = "+idNoticia+") AND (nAdj.ELIMINADO = '"+Constantes.BOOLEAN_FALSE+"'))"
                    + " ORDER BY adj.id ASC";
            q = em.createNativeQuery(qy);
            
            lista = castVo(q.getResultList());
        } catch (Exception e) {
            LOGGER.error(e);
        }       
        
        return lista;
    }
    
    private  List<NoticiaAdjuntoVO> castVo(List lista){
        NoticiaAdjuntoVO notiAdjVo = null;
        List<NoticiaAdjuntoVO> lo = new ArrayList<>();
        for (Iterator it = lista.iterator(); it.hasNext();) {
            Object[] obj = (Object[]) it.next();
            notiAdjVo = new NoticiaAdjuntoVO();
            notiAdjVo.setId((Integer) obj[0]);
            notiAdjVo.setIdNoticiaAdjunto((Integer) obj[1]); 
            notiAdjVo.setDescripcion(String.valueOf(obj[2]));
            notiAdjVo.setEliminado((Boolean)obj[3]);
            notiAdjVo.setGenero(String.valueOf(obj[4]));
            notiAdjVo.setFechaGenero((Date) obj[5]);
            notiAdjVo.setHoraGenero((Date) obj[6]);
            notiAdjVo.setNombre(String.valueOf(obj[7]));
            notiAdjVo.setPeso(String.valueOf(obj[8]));
            notiAdjVo.setTipoArchivo(String.valueOf(obj[9]));
            notiAdjVo.setUrl(String.valueOf(obj[10]));
            notiAdjVo.setUuid(String.valueOf(obj[11]));
            lo.add(notiAdjVo);
        }
        return lo;
        
    }
}
