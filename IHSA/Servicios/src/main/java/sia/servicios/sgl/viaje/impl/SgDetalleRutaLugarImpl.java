/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgDetalleRutaLugar;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.SgDetalleRutaTerrestreVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgDetalleRutaLugarImpl extends AbstractFacade<SgDetalleRutaLugar>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgDetalleRutaLugarImpl() {
        super(SgDetalleRutaLugar.class);
    }
    //
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreRemote;
    @Inject
    private SgLugarImpl sgLugarRemote;
    @Inject
    private UsuarioImpl usuarioRemote;

    
    public List<SgDetalleRutaTerrestreVo> traerDetalleRutaLugarPorRuta(int idSgRutaTerrestre) {
        clearQuery();

        try {
            List<SgDetalleRutaTerrestreVo> list = new ArrayList<SgDetalleRutaTerrestreVo>();

            appendQuery("SELECT drl.ID, "); //0
            appendQuery(" drl.SG_RUTA_TERRESTRE , "); //1
            appendQuery(" drl.SG_LUGAR , "); //2
            appendQuery(" l.NOMBRE ,"); //3
            appendQuery(" drl.destino "); //4
            appendQuery(" FROM SG_DETALLE_RUTA_LUGAR drl, SG_LUGAR l ");
            appendQuery(" WHERE drl.SG_RUTA_TERRESTRE = ").append(idSgRutaTerrestre);
            appendQuery(" AND drl.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" AND drl.SG_LUGAR = l.ID ");
            appendQuery(" AND l.ELIMINADO = 'False' ");
            appendQuery(" ORDER BY l.nombre asc");

            List<Object[]> result = em.createNativeQuery(query.toString()).getResultList();

            SgDetalleRutaTerrestreVo vo;
            for (Object[] objects : result) {
                vo = new SgDetalleRutaTerrestreVo();
                vo.setId((Integer) objects[0]);
                vo.setIdSgRutaTerrestre((Integer) objects[1]);
                vo.setIdLugar((Integer) objects[2]);
                vo.setNombreLugar((String) objects[3]);
                vo.setDestino((Boolean) objects[4]);
                vo.setIdCiudad(0);
                list.add(vo);
            }
            return (list.isEmpty() ? Collections.EMPTY_LIST : list);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    
    public SgDetalleRutaTerrestreVo buscarRutaLugarPorOficina(int idOficina, int idLugar) {
        clearQuery();
        try {
            appendQuery("SELECT drl.ID, "); //0
            appendQuery("drl.SG_RUTA_TERRESTRE, "); //1
            appendQuery("drl.sg_lugar, "); //2
            appendQuery("l.NOMBRE, "); //3
            appendQuery("drl.destino "); //3
            appendQuery("FROM sg_detalle_ruta_lugar drl, sg_lugar l, sg_ruta_terrestre rt ");
            appendQuery(" WHERE rt.sg_oficina = ").append(idOficina);
            appendQuery(" and drl.sg_lugar = ").append(idLugar);
            appendQuery(" and drl.sg_ruta_terrestre = rt.id ");
            appendQuery(" AND drl.eliminado = 'False'");
            appendQuery(" AND drl.sg_lugar = l.ID ");
            Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            return castDetalleRutaLugar(obj);
        } catch (NoResultException e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    private SgDetalleRutaTerrestreVo castDetalleRutaLugar(Object[] obj) {
        SgDetalleRutaTerrestreVo sgDetalleRutaTerrestreVo = new SgDetalleRutaTerrestreVo();
        sgDetalleRutaTerrestreVo.setId((Integer) obj[0]);
        sgDetalleRutaTerrestreVo.setIdSgRutaTerrestre((Integer) obj[1]);
        sgDetalleRutaTerrestreVo.setIdLugar((Integer) obj[2]);
        sgDetalleRutaTerrestreVo.setNombreLugar((String) obj[3]);
        sgDetalleRutaTerrestreVo.setDestino((Boolean) obj[4]);
        return sgDetalleRutaTerrestreVo;
    }

    
    public void guardarDetalleRuta(Usuario usuario, int idLugar, int idRuta) {
        try {
            SgDetalleRutaLugar sgDetalleRutaLugar = new SgDetalleRutaLugar();
            sgDetalleRutaLugar.setDestino(Constantes.BOOLEAN_TRUE);
            sgDetalleRutaLugar.setSgLugar(sgLugarRemote.find(idLugar));
            sgDetalleRutaLugar.setSgRutaTerrestre(sgRutaTerrestreRemote.find(idRuta));
            sgDetalleRutaLugar.setEliminado(Constantes.NO_ELIMINADO);
            sgDetalleRutaLugar.setGenero(usuario);
            sgDetalleRutaLugar.setFechaGenero(new Date());
            sgDetalleRutaLugar.setHoraGenero(new Date());
            create(sgDetalleRutaLugar);
        } catch (Exception ex) {
            Logger.getLogger(SgDetalleRutaCiudadImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public SgDetalleRutaTerrestreVo buscarDetalleRutaLugarDestinoPorRuta(int idRuta) {
        clearQuery();
        try {
            appendQuery("SELECT drl.ID, "); //0
            appendQuery("drl.SG_RUTA_TERRESTRE, "); //1
            appendQuery("drl.sg_lugar , "); //2
            appendQuery("l.NOMBRE, "); //3
            appendQuery("drl.destino "); //3
            appendQuery("FROM sg_detalle_ruta_lugar drl, sg_lugar l, sg_ruta_terrestre rt ");
            appendQuery(" WHERE rt.id = ").append(idRuta);
            appendQuery(" and drl.sg_ruta_terrestre = rt.id ");
            appendQuery(" AND drl.eliminado = 'False'");
            appendQuery(" AND drl.destino = '").append(Constantes.BOOLEAN_TRUE).append("'");
            appendQuery(" AND drl.sg_lugar = l.ID ");
            Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            return castDetalleRutaLugar(obj);
        } catch (NoResultException e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }
}
