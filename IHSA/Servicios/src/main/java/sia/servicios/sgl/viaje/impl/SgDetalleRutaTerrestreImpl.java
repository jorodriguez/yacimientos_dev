/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jooq.DSLContext;
import org.jooq.Record;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgDetalleRutaTerrestre;
import sia.modelo.SgRutaTerrestre;
import sia.modelo.Usuario;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.modelo.sgl.vo.SgDetalleRutaTerrestreVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.semaforo.impl.SgEstadoSemaforoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgDetalleRutaTerrestreImpl extends AbstractFacade<SgDetalleRutaTerrestre> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    @Inject
    DSLContext dbCtx;
    
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreRemote;    
    @Inject
    private SgOficinaImpl sgOficinaRemote;
    @Inject
    private SgEstadoSemaforoImpl sgEstadoSemaforoRemote;
    //
    private StringBuilder bodyQuery = new StringBuilder();

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgDetalleRutaTerrestreImpl() {
        super(SgDetalleRutaTerrestre.class);
    }

    public void clearBodyQuery() {
        this.bodyQuery.delete(0, this.bodyQuery.length());
    }

     //usado para extraer las oficinas de la ruta para noticia.
    public List<SgDetalleRutaTerrestre> getDetailByRuote(int idRuta, boolean eliminado) throws SIAException {
        try {
            return em.createQuery("SELECT d  FROM SgDetalleRutaTerrestre d WHERE d.sgRutaTerrestre.id = :ruta AND d.eliminado = :eli"
                    + " ORDER BY d.destino ASC ").setParameter("ruta", idRuta).setParameter("eli", eliminado).getResultList();
        } catch (Exception e) {
            throw new SIAException("Ocurrio una excepcion al recuperar las rutas ");
        }
    }

    
    public boolean findDetailRoute(List<OficinaVO> listaFilasSeleccionadas, int idRuta) {
        boolean v = false;
        try {
            //buscar si hay una detalle de ruta igual

            int guardar = 0;
            List<SgDetalleRutaTerrestre> ldrt = getAllDetailRoute();
            TreeSet<Integer> rutas = new TreeSet<Integer>();
            for (SgDetalleRutaTerrestre sgDetalleRutaTerrestre : ldrt) {
                rutas.add(sgDetalleRutaTerrestre.getSgRutaTerrestre().getId());
            }
            //
            List<SgDetalleRutaTerrestre> lDetRuta;
            for (Integer ruta : rutas) {
                lDetRuta = getDetailByRuote(ruta, Constantes.NO_ELIMINADO);
                if (lDetRuta.size() == listaFilasSeleccionadas.size()) {
                    for (int i = 0; i < lDetRuta.size(); i++) {  //recorremos la lista exxterna
                        if (lDetRuta.get(i).getId().intValue() == listaFilasSeleccionadas.get(i).getId()) {
                            if ((lDetRuta.get(i).getSgRutaTerrestre().getId().intValue() == ruta)) {
                                guardar++;
                            } else {
                                guardar = 0;
                            }
                        } else {
                            //Guarda
                            guardar = 0;
                        }
                    } //fin del for de detalle de rutas.
                } else {
                    //GUARDA
                    guardar = 0;
                }
            } //fin del for de rutas

            if (guardar != listaFilasSeleccionadas.size()) {
                v = true;
            } else {
                v = false;
            }
        } catch (SIAException ex) {
            v = false;
            Logger.getLogger(SgDetalleRutaTerrestreImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        UtilLog4j.log.info(this, "V :" + v);
        return v;
    }

    
    public void saveDetailRoute(Usuario usuario, List<OficinaVO> listaFilasSeleccionadas, int idRuta) throws SIAException {
        try {
            for (OficinaVO sgOficina : listaFilasSeleccionadas) {
                SgDetalleRutaTerrestre sgDetalleRutaTerrestre = new SgDetalleRutaTerrestre();
                sgDetalleRutaTerrestre.setSgOficina(sgOficinaRemote.find(sgOficina.getId()));
                sgDetalleRutaTerrestre.setSgRutaTerrestre(sgRutaTerrestreRemote.find(idRuta));
                sgDetalleRutaTerrestre.setGenero(usuario);
                sgDetalleRutaTerrestre.setFechaGenero(new Date());
                sgDetalleRutaTerrestre.setHoraGenero(new Date());
                sgDetalleRutaTerrestre.setEliminado(Constantes.NO_ELIMINADO);
                sgDetalleRutaTerrestre.setDestino(sgOficina.isDestino());
                create(sgDetalleRutaTerrestre);                
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error");
            throw new SIAException("Ocurrio un error");
        }
    }

    
    public List<SgDetalleRutaTerrestre> getAllDetailRoute() throws SIAException {
        try {
            return em.createQuery("SELECT d FROM  SgDetalleRutaTerrestre d WHERE d.eliminado = :eli "
                    + " ORDER BY d.sgOficina.id ASC").setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
        } catch (Exception e) {
            throw new SIAException("courrio un error al recuperar el detalle de las rutas");
        }

    }

    
    public void deleteDetailRoute(Usuario usuario, List<SgDetalleRutaTerrestreVo> listaDetalleRuta) {

        for (SgDetalleRutaTerrestreVo sgDetalleRutaTerrestreVo : listaDetalleRuta) {
            try {
                SgDetalleRutaTerrestre sgDetalleRutaTerrestre = find(sgDetalleRutaTerrestreVo.getId());                
                sgDetalleRutaTerrestre.setModfico(usuario);
                sgDetalleRutaTerrestre.setFechaModifico(new Date());
                sgDetalleRutaTerrestre.setHoraModifico(new Date());
                sgDetalleRutaTerrestre.setEliminado(Constantes.ELIMINADO);
                edit(sgDetalleRutaTerrestre);                
                sgEstadoSemaforoRemote.eliminarEstadoSemaforo(usuario.getId(), sgDetalleRutaTerrestreVo.getIdSgRutaTerrestre());
            } catch (Exception ex) {
                Logger.getLogger(SgDetalleRutaTerrestreImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    
    public List<SgDetalleRutaTerrestreVo> getAllSgDetalleRutaTerrestreBySgRutaTerrestre(int idSgRutaTerrestre, String orderByField, boolean sortAscending, boolean eliminado) {
        clearBodyQuery();

        try {
            List<SgDetalleRutaTerrestreVo> list = new ArrayList<SgDetalleRutaTerrestreVo>();

            bodyQuery.append("SELECT drt.ID, "); //0
            bodyQuery.append(" drt.SG_RUTA AS ID_SG_RUTA_TERRESTRE, "); //1
            bodyQuery.append(" drt.SG_OFICINA AS ID_SG_OFICINA, "); //2
            bodyQuery.append(" o.NOMBRE AS NOMBRE_SG_OFICINA ,"); //3
            bodyQuery.append(" drt.destino "); //4
            bodyQuery.append(" FROM SG_DETALLE_RUTA_TERRESTRE drt, SG_OFICINA o ");
            bodyQuery.append(" WHERE drt.SG_RUTA = ").append(idSgRutaTerrestre).append(" ");
            bodyQuery.append(" AND drt.ELIMINADO = '").append(eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).append("' ");
            bodyQuery.append(" AND drt.SG_OFICINA = o.ID ");
            bodyQuery.append(" AND o.ELIMINADO = 'False' ");
            if (orderByField != null && !orderByField.isEmpty()) {
                if ("id".equals(orderByField)) {
                    bodyQuery.append(" ORDER BY drt.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
                } else if ("nombre".equals(orderByField)) {
                    bodyQuery.append(" ORDER BY o.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
                }
            }
            UtilLog4j.log.info(this, "Q: " + bodyQuery.toString());
            List<Object[]> result = em.createNativeQuery(bodyQuery.toString()).getResultList();

            SgDetalleRutaTerrestreVo vo;
            for (Object[] objects : result) {
                vo = new SgDetalleRutaTerrestreVo();
                vo.setId((Integer) objects[0]);
                vo.setIdSgRutaTerrestre((Integer) objects[1]);
                vo.setIdSgOficina((Integer) objects[2]);
                vo.setNombreSgOficina((String) objects[3]);
                vo.setDestino((Boolean) objects[4]);
                vo.setIdCiudad(0);
                list.add(vo);
            }

            UtilLog4j.log.info(this, "Se encontraron " + (list.isEmpty() ? "0" : list.size()) + " SgDetalleRutaTerrestre");
            return (list.isEmpty() ? Collections.EMPTY_LIST : list);
        } catch (Exception e) {
            e.getStackTrace();
            UtilLog4j.log.fatal(this, "Al recuperar el detalle de las rutas por id: " + idSgRutaTerrestre + "Exc: " + e.getMessage());
            return null;
        }

    }

    
    public List<SgDetalleRutaTerrestreVo> getAllSgDetalleRutaTerrestreBySgOficinaOrigen(int idSgOficinaOrigen) {

        List<SgRutaTerrestre> rutas = this.sgRutaTerrestreRemote.getAllRutaTerrestre(idSgOficinaOrigen, Constantes.NO_ELIMINADO);
        List<SgDetalleRutaTerrestreVo> result = new ArrayList();

        if (rutas != null && !rutas.isEmpty()) {
            for (SgRutaTerrestre rt : rutas) {
                List<SgDetalleRutaTerrestreVo> detalles = getAllSgDetalleRutaTerrestreBySgRutaTerrestre(rt.getId(), "nombre", true, false);
                for (SgDetalleRutaTerrestreVo vo : detalles) {
                    result.add(vo);
                }
            }
        }

        UtilLog4j.log.info(this, "Se encontraron " + (result.size()) + "detalles de rutas con origen: " + idSgOficinaOrigen);

        return result;
    }

    
    public SgDetalleRutaTerrestre findSgDetalleRutaTerrestreDestinoBySgRutaTerrestre(int idSgRutaTerrestre) {

        clearBodyQuery();

        try {
            bodyQuery.append("SELECT drt.ID, "); //0
            bodyQuery.append("drt.SG_RUTA, "); //1
            bodyQuery.append("drt.SG_OFICINA, "); //2
            bodyQuery.append("drt.DESTINO "); //4
            bodyQuery.append("FROM SG_DETALLE_RUTA_TERRESTRE drt ");
            bodyQuery.append("WHERE drt.SG_RUTA = ").append(idSgRutaTerrestre).append(" ");
            bodyQuery.append("AND drt.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' ");
            bodyQuery.append("AND drt.DESTINO = '").append(Constantes.BOOLEAN_TRUE).append("' ");

            Query q = em.createNativeQuery(this.bodyQuery.toString(), SgDetalleRutaTerrestre.class);

            UtilLog4j.log.info(this, "Query " + q.toString());

            return (SgDetalleRutaTerrestre) q.getSingleResult();
        } catch (NoResultException nre) {
            UtilLog4j.log.fatal(this, nre.getMessage());
            return null;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "SgDetalleRutaTerrestreImpl.findRelation()" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    
    public SgDetalleRutaTerrestreVo buscarDetalleRutaTerrestreDestinoPorRuta(int idSgRutaTerrestre) {

        SgDetalleRutaTerrestreVo dvo = null;
        try {
            String sql = "SELECT drt.ID, drt.SG_RUTA as id_Sg_Ruta_Terrestre, drt.SG_OFICINA as id_Sg_Oficina, drt.DESTINO, o.nombre as nombre_Sg_Oficina"
                    + " FROM SG_DETALLE_RUTA_TERRESTRE drt"
                    + " inner join sg_oficina o on o.id = drt.sg_oficina "
                    + " WHERE drt.SG_RUTA = ?"
                    + " AND drt.ELIMINADO = ? AND drt.DESTINO = ?";
            
            Record record = dbCtx.fetchOne(sql,idSgRutaTerrestre,Constantes.FALSE,Constantes.TRUE);
            
            if(record != null) {
                dvo = record.into(SgDetalleRutaTerrestreVo.class);
            }

        } catch (NoResultException nre) {
            UtilLog4j.log.fatal(this, "SgDetalleRutaTerrestreImpl.findRelation()", nre);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "SgDetalleRutaTerrestreImpl.findRelation()", e);
        }
         return dvo;
    }

    private SgDetalleRutaTerrestreVo castDetalleRutaTerrestre(Object[] obj) {
        SgDetalleRutaTerrestreVo sgDetalleRutaTerrestreVo = new SgDetalleRutaTerrestreVo();
        sgDetalleRutaTerrestreVo.setId((Integer) obj[0]);
        sgDetalleRutaTerrestreVo.setIdSgRutaTerrestre((Integer) obj[1]);
        sgDetalleRutaTerrestreVo.setIdSgOficina((Integer) obj[2]);
        sgDetalleRutaTerrestreVo.setDestino(((Boolean) obj[3]));
        sgDetalleRutaTerrestreVo.setNombreSgOficina((String) obj[4]);
        return sgDetalleRutaTerrestreVo;
    }

    
    public List<SgDetalleRutaTerrestreVo> listaDetallesRutasTerrestres(List<RutaTerrestreVo> listRuta) {
        clearBodyQuery();
        List<SgDetalleRutaTerrestreVo> listDetalle = new ArrayList<SgDetalleRutaTerrestreVo>();

        try {
            bodyQuery.append("SELECT drt.ID, "); //0
            bodyQuery.append("drt.SG_RUTA, "); //1
            bodyQuery.append("drt.SG_OFICINA, "); //2
            bodyQuery.append("drt.DESTINO, o.nombre "); //4
            bodyQuery.append("FROM SG_DETALLE_RUTA_TERRESTRE drt, sg_oficina o ");
            bodyQuery.append("WHERE drt.SG_RUTA IN (").append(Constantes.CERO);
            for (RutaTerrestreVo vo : listRuta) {
                bodyQuery.append(",").append(vo.getId());
            }
            bodyQuery.append(")");
            bodyQuery.append(" and drt.sg_oficina  = o.id ");
            bodyQuery.append("AND drt.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' ");
            bodyQuery.append("AND drt.DESTINO = '").append(Constantes.BOOLEAN_TRUE).append("' ");

            List<Object[]> result = em.createNativeQuery(this.bodyQuery.toString()).getResultList();
            if (result != null) {
                for (Object[] obj : result) {
                    listDetalle.add(castDetalleRutaTerrestre(obj));
                }
            } else {
                listDetalle = null;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "SgDetalleRutaTerrestreImpl.findRelation()" + e.getMessage());
        }
        return listDetalle;
    }
}
