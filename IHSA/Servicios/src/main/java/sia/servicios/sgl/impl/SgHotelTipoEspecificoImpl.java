/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.modelo.SgHotelTipoEspecifico;
import sia.modelo.SgHuespedHotel;
import sia.modelo.SgHuespedHotelServicio;
import sia.modelo.Usuario;
import sia.modelo.sgl.hotel.vo.SgHotelTipoEspecificoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Stateless 
public class SgHotelTipoEspecificoImpl extends AbstractFacade<SgHotelTipoEspecifico> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;    
    @Inject
    private SgHotelImpl sgHotelRemote;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    @Inject
    private SgHuespedHotelServicioImpl sgHuespedHotelServicioRemote;
    @Inject
    private SgHuespedHotelImpl sgHuespedHotelRemote;
    
    private StringBuilder bodyQuery = new StringBuilder();
    

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgHotelTipoEspecificoImpl() {
        super(SgHotelTipoEspecifico.class);
    }

    private void limpiarCuerpoQuery() {
        bodyQuery.delete(0, bodyQuery.length());
    }

    
    public void save(int idSgHotel, int idSgTipoEspecifico, String idUsuario) throws ExistingItemException {
        UtilLog4j.log.info(this, "SgHotelTipoEspecificoImpl.save()");

        SgHotelTipoEspecifico sgHotelTipoEspecifico = null;
        sgHotelTipoEspecifico = findRelation(idSgHotel, idSgTipoEspecifico);
        UtilLog4j.log.info(this, sgHotelTipoEspecifico == null ? ("No se encontró la relación: idSgHotel " + idSgHotel + " | idSgTipoEspecifico: " + idSgTipoEspecifico) : ("Se encontró la relación: idSgHotel " + idSgHotel + " | idSgTipoEspecifico: " + idSgTipoEspecifico));

        if (sgHotelTipoEspecifico == null) {
            sgHotelTipoEspecifico = new SgHotelTipoEspecifico();
            sgHotelTipoEspecifico.setSgHotel(this.sgHotelRemote.find(idSgHotel));
            sgHotelTipoEspecifico.setSgTipoEspecifico(this.sgTipoEspecificoRemote.find(idSgTipoEspecifico));
            sgHotelTipoEspecifico.setGenero(new Usuario(idUsuario));
            sgHotelTipoEspecifico.setFechaGenero(new Date());
            sgHotelTipoEspecifico.setHoraGenero(new Date());
            sgHotelTipoEspecifico.setEliminado(Constantes.NO_ELIMINADO);

            create(sgHotelTipoEspecifico);            
        } else {
            if (!sgHotelTipoEspecifico.isEliminado()) {
                throw new ExistingItemException(sgHotelTipoEspecifico.getSgTipoEspecifico().getNombre(), sgHotelTipoEspecifico);
            } else {
                reactivateRelation(idSgHotel, idSgTipoEspecifico, idUsuario);
            }
        }
    }

    
    public void saveWithUpdateForHuespedes(int idSgHotel, int idSgTipoEspecifico, String idUsuario) throws ExistingItemException {
        save(idSgHotel, idSgTipoEspecifico, idUsuario);

        //Buscar todos los huéspedes que estén hospedados en el hotel y que aún no tengan una carta asignada y ponerles el nuevo servicio del hotel
        List<SgHuespedHotel> list = this.sgHuespedHotelRemote.traerHuespedPorHotel(idSgHotel, idUsuario);

        if (list != null && !list.isEmpty()) {
            for (SgHuespedHotel hh : list) {
                if (hh.getSiAdjunto() == null) {
                    this.sgHuespedHotelServicioRemote.save(hh.getId().intValue(), idSgTipoEspecifico, false, idUsuario);
                }
            }
        }
    }

    
    public void delete(int idSgHotelTipoEspecifico, String idUsuario) {
        UtilLog4j.log.info(this, "SgHotelTipoEspecificoImpl.delete()");

        SgHotelTipoEspecifico sgHotelTipoEspecifico = find(idSgHotelTipoEspecifico);
        
        sgHotelTipoEspecifico.setModifico(new Usuario(idUsuario));
        sgHotelTipoEspecifico.setFechaModifico(new Date());
        sgHotelTipoEspecifico.setHoraModifico(new Date());
        sgHotelTipoEspecifico.setEliminado(Constantes.ELIMINADO);

        edit(sgHotelTipoEspecifico);
        UtilLog4j.log.info(this, "SgHotelTipoEspecifico DELETED SUCCESSFULLY");
    }

    
    public void deleteWithUpdateForHuespedes(int idSgHotelTipoEspecifico, String idUsuario) {
        UtilLog4j.log.info(this, "SgHotelTipoEspecificoImpl.deleteWithUpdateForHuespedes");

        SgHotelTipoEspecifico ht = find(idSgHotelTipoEspecifico);

        delete(idSgHotelTipoEspecifico, idUsuario);

        //Buscar todos los huéspedes que estén hospedados en el hotel y que aún no tengan una carta asignada y quitarles el servicio del hotel
        List<SgHuespedHotel> list = this.sgHuespedHotelRemote.traerHuespedPorHotel(ht.getSgHotel().getId().intValue(), idUsuario);

        if (list != null && !list.isEmpty()) {
            for (SgHuespedHotel hh : list) {
                if (hh.getSiAdjunto() == null) {
                    SgHuespedHotelServicio hhs = sgHuespedHotelServicioRemote.findRelation(hh.getId().intValue(), ht.getSgTipoEspecifico().getId().intValue(), false);
                    if (hhs != null) {
                        this.sgHuespedHotelServicioRemote.delete(hhs.getId().intValue(), idUsuario);
                    }
                }
            }
        }
    }

    public SgHotelTipoEspecifico findRelation(int idSgHotel, int idSgTipoEspecifico) {
        UtilLog4j.log.info(this, "SgHotelTipoEspecificoImpl.existRelation()");

        StringBuilder sb = new StringBuilder();
        sb.delete(0, sb.length());

        try {
            Query q = em.createNativeQuery(sb.append("SELECT *").append(" FROM SG_HOTEL_TIPO_ESPECIFICO AS hte ").append(" WHERE hte.SG_HOTEL = ").append(idSgHotel).append(" AND hte.SG_TIPO_ESPECIFICO = ").append(idSgTipoEspecifico).toString(), SgHotelTipoEspecifico.class);

//            UtilLog4j.log.info(this, "Query " + q.toString());

            return (SgHotelTipoEspecifico) q.getSingleResult();
        } catch (NoResultException nre) {
            UtilLog4j.log.fatal(this, nre.getMessage());
            return null;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "SgHotelTipoEspecificoImpl.findRelation()" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void reactivateRelation(int idSgHotel, int idSgTipoEspecifico, String idUsuario) {
        UtilLog4j.log.info(this, "SgHotelTipoEspecificoImpl.reactivateRelation()");

        SgHotelTipoEspecifico sgHotelTipoEspecifico = findRelation(idSgHotel, idSgTipoEspecifico);

        sgHotelTipoEspecifico.setFechaModifico(new Date());
        sgHotelTipoEspecifico.setHoraModifico(new Date());
        sgHotelTipoEspecifico.setModifico(new Usuario(idUsuario));
        sgHotelTipoEspecifico.setEliminado(Constantes.NO_ELIMINADO);

        edit(sgHotelTipoEspecifico);
        UtilLog4j.log.info(this, "SgHotelTipoEspecificoImpl UPDATED SUCCESSFULLY");
    }

    
    public List<SgHotelTipoEspecificoVo> getAllSgHotelTipoEspecificoBySgHotelAndProvided(int idSgHotel, boolean provided, String orderByField, boolean sortAscending, boolean eliminado) {
        UtilLog4j.log.info(this, "SgHotelTipoEspecificoImpl.getAllSgHotelTipoEspecificoBySgHotel()");

        String q = "";
        String q2 = "";
        StringBuilder sb = new StringBuilder();
        sb.delete(0, sb.length());

        if (provided) {
            q = sb.append("SELECT ").append("hte.ID, ")//0
                    .append("h.ID AS ID_HOTEL, ") //1
                    .append("p.NOMBRE AS NOMBRE_PROVEEDOR_HOTEL, ") //2
                    .append("te.ID AS ID_SG_TIPO_ESPECIFICO, ") //3
                    .append("te.NOMBRE AS NOMBRE_SG_TIPO_ESPECIFICO ") //4
                    .append("FROM ").append("SG_HOTEL_TIPO_ESPECIFICO AS hte, ").append("SG_HOTEL AS h, ").append("SG_TIPO_ESPECIFICO te,").append("PROVEEDOR p ").append("WHERE ").append("h.ID = ").append(idSgHotel).append(" ").append("AND hte.ELIMINADO = '").append(eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).append("' ").append("AND hte.SG_HOTEL = h.ID ").append("AND hte.SG_TIPO_ESPECIFICO = te.ID ").append("AND h.PROVEEDOR = p.ID ").toString();

            if (orderByField != null && !orderByField.isEmpty()) {
                q += " ORDER BY te." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
            }
        } else {
            q = sb.append("SELECT ").append("te.ID AS ID_SG_TIPO_ESPECIFICO, ").append("te.NOMBRE AS NOMBRE_SG_TIPO_ESPECIFICO ") //4
                    .append("FROM SG_TIPO_TIPO_ESPECIFICO tte, SG_TIPO_ESPECIFICO te ").append("WHERE ").append("tte.ELIMINADO='False' ").append("AND tte.SG_TIPO=8 ").append("AND tte.SG_TIPO_ESPECIFICO=te.ID ").append("AND te.SISTEMA='False' ").append("AND te.PAGO='True' ").append("AND te.PAGO='True' ").append("AND te.ID NOT IN (").append("SELECT ").append("te.ID AS ID_SG_TIPO_ESPECIFICO ") //3
                    .append("FROM ").append("SG_HOTEL_TIPO_ESPECIFICO AS hte, ").append("SG_HOTEL AS h, ").append("SG_TIPO_ESPECIFICO te, ").append("PROVEEDOR p ").append("WHERE ").append("h.ID = ").append(idSgHotel).append(" ").append("AND hte.ELIMINADO = '").append(eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).append("' ").append("AND hte.SG_HOTEL = h.ID ").append("AND hte.SG_TIPO_ESPECIFICO = te.ID ").append("AND h.PROVEEDOR = p.ID) ").toString();

            if (orderByField != null && !orderByField.isEmpty()) {
                q += " ORDER BY te." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
            }
        }

        Query query = em.createNativeQuery(q);

        UtilLog4j.log.info(this, "query: " + query.toString());

        List<Object[]> result = query.getResultList();
        List<SgHotelTipoEspecificoVo> list = new ArrayList<SgHotelTipoEspecificoVo>();

        SgHotelTipoEspecificoVo vo = null;

        if (provided) {
            for (Object[] objects : result) {
                vo = new SgHotelTipoEspecificoVo();
                vo.setId((Integer) objects[0]);
                vo.setIdSgHotel((Integer) objects[1]);
                vo.setNombreSgHotel((String) objects[2]);
                vo.setIdSgTipoEspecifico((Integer) objects[3]);
                vo.setNombreSgTipoEspecifico((String) objects[4]);
                list.add(vo);
            }
        } else {
            for (Object[] objects : result) {
                vo = new SgHotelTipoEspecificoVo();
                vo.setIdSgHotel(idSgHotel);
                vo.setIdSgTipoEspecifico((Integer) objects[0]);
                vo.setNombreSgTipoEspecifico((String) objects[1]);
                list.add(vo);
            }
        }

        UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SgHotelTipoEspecifico");

        return list;
    }
}
