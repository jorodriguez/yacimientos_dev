/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.*;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.SgHotel;
import sia.modelo.SgHuespedHotel;
import sia.modelo.SgHuespedHotelServicio;
import sia.modelo.Usuario;
import sia.modelo.sgl.hotel.vo.SgHotelTipoEspecificoVo;
import sia.modelo.sgl.hotel.vo.SgHuespedHotelServicioVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgHuespedHotelServicioImpl extends AbstractFacade<SgHuespedHotelServicio>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    @Inject
    private SgHotelTipoEspecificoImpl sgHotelTipoEspecificoRemote;
    @Inject
    private SgHuespedHotelImpl sgHuespedHotelRemote;    
    private StringBuilder bodyQuery = new StringBuilder();

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgHuespedHotelServicioImpl() {
        super(SgHuespedHotelServicio.class);
    }

    private void clearBodyQuery() {
        bodyQuery.delete(0, bodyQuery.length());
    }

    
    public void save(int idSgHuespedHotel, int idSgTipoEspecifico, boolean facturaEmpresa, String idUsuario) {
        UtilLog4j.log.info(this, "SgHuespedHotelServicioImpl.save()");

        SgHuespedHotelServicio sgHuespedHotelServicio = new SgHuespedHotelServicio();

        sgHuespedHotelServicio.setSgTipoEspecifico(this.sgTipoEspecificoRemote.find(idSgTipoEspecifico));
        sgHuespedHotelServicio.setSgHuespedHotel(this.sgHuespedHotelRemote.find(idSgHuespedHotel));
        sgHuespedHotelServicio.setFacturaEmpresa(facturaEmpresa);
        sgHuespedHotelServicio.setGenero(new Usuario(idUsuario));
        sgHuespedHotelServicio.setFechaGenero(new Date());
        sgHuespedHotelServicio.setHoraGenero(new Date());
        sgHuespedHotelServicio.setEliminado(Constantes.NO_ELIMINADO);

        create(sgHuespedHotelServicio);
    }
    
    
    public void delete(int idSgHuespedHotelServicio, String idUsuario) {
        UtilLog4j.log.info(this, "SgHuespedHotelServicioImpl.delete()");
        
        SgHuespedHotelServicio sgHuespedHotelServicio = find(idSgHuespedHotelServicio);
        
        sgHuespedHotelServicio.setModifico(new Usuario(idUsuario));
        sgHuespedHotelServicio.setFechaModifico(new Date());
        sgHuespedHotelServicio.setHoraModifico(new Date());
        sgHuespedHotelServicio.setEliminado(Constantes.ELIMINADO);
        
        edit(sgHuespedHotelServicio);
    }   
    
    
    public SgHuespedHotelServicio findRelation(int idSgHuespedHotel, int idSgTipoEspecifico, Boolean facturaEmpresa) {
        UtilLog4j.log.info(this, "SgHuespedHotelServicioImpl.findRelation()");
        
        clearBodyQuery();
        
        try {
            Query q = em.createNativeQuery(this.bodyQuery.append("SELECT *")
                    .append(" FROM SG_HUESPED_HOTEL_SERVICIO AS hse ")
                    .append(" WHERE hse.SG_HUESPED_HOTEL = ")
                    .append(idSgHuespedHotel)
                    .append(" AND hse.SG_TIPO_ESPECIFICO = ")
                    .append(idSgTipoEspecifico)
                    .append(" AND hse.ELIMINADO='False'")
                    .append(facturaEmpresa != null ? (facturaEmpresa ? " AND hse.FACTURA_EMPRESA='True'" : " AND hse.FACTURA_EMPRESA='False'") : "")
                    .append(";").toString(), SgHuespedHotelServicio.class);

            UtilLog4j.log.info(this, "Query " + q.toString());

            return (SgHuespedHotelServicio) q.getSingleResult();
        } catch (NoResultException nre) {
            UtilLog4j.log.fatal(this, nre.getMessage());
            return null;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "SgHuespedHotelServicio.findRelation()" + e.getMessage());
            return null;
        }  
    }    

    
    public List<SgHuespedHotelServicioVo> findAllBySgHuespedHotel(int idSgHuespedHotel, Boolean facturaEmpresa) {
        UtilLog4j.log.info(this, "SgHuespedHotelServicioImpl.findAllBySgHuespedHotel()");

        clearBodyQuery();

        this.bodyQuery.append("SELECT hhs.ID, "); //0
        this.bodyQuery.append("hhs.SG_HUESPED_HOTEL AS ID_SG_HUESPED_HOTEL, "); //1
        this.bodyQuery.append("hhs.SG_TIPO_ESPECIFICO AS ID_SG_TIPO_ESPECIFICO, "); //2
        this.bodyQuery.append("te.NOMBRE AS NOMBRE_SG_TIPO_ESPECIFICO, "); //3
        this.bodyQuery.append("hhs.FACTURA_EMPRESA "); //4
        this.bodyQuery.append("FROM SG_HUESPED_HOTEL_SERVICIO hhs, SG_TIPO_ESPECIFICO te ");
        this.bodyQuery.append("WHERE hhs.eliminado = 'False' ");
        this.bodyQuery.append("AND hhs.SG_HUESPED_HOTEL=").append(idSgHuespedHotel).append(" ");
        if(facturaEmpresa != null) {
            this.bodyQuery.append("AND hhs.FACTURA_EMPRESA='").append(facturaEmpresa ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).append("' ");
        }
        this.bodyQuery.append("AND hhs.SG_TIPO_ESPECIFICO=te.ID ");
        this.bodyQuery.append("ORDER BY te.NOMBRE;  ");

        Query query = em.createNativeQuery(this.bodyQuery.toString());
        
        UtilLog4j.log.info(this, query.toString());

        List<Object[]> result = query.getResultList();
        List<SgHuespedHotelServicioVo> list = new ArrayList<SgHuespedHotelServicioVo>();
        SgHuespedHotelServicioVo vo = null;

        for (Object[] objects : result) {
            vo = new SgHuespedHotelServicioVo();
            vo.setId((Integer) objects[0]);
            vo.setIdSgHuespedHotel((Integer) objects[1]);
            vo.setIdSgTipoEspecifico((Integer) objects[2]);
            vo.setNombreSgTipoEspecifico((String) objects[3]);
            vo.setFacturadoEmpresa((Boolean) objects[4]);
            list.add(vo);
        }

        UtilLog4j.log.info(this, "Se encontraron " + (list.isEmpty() ? "0" : list.size()) + " SgHuespedHotelServicio");

        return (list.isEmpty() ? Collections.EMPTY_LIST : list);
    }

    
    public List<SgHuespedHotelServicioVo> getAllSgTipoEspecificoFacturaEmpesaBySgHotel(int idSgHuespedHotel) {
        UtilLog4j.log.info(this, "SgHuespedHotelServicioImpl.getAllSgTipoEspecificoFacturaEmpesaBySgHotel()");

        SgHuespedHotel sgHuespedHotel = this.sgHuespedHotelRemote.find(idSgHuespedHotel);
        SgHotel sgHotel = sgHuespedHotel.getSgHotelHabitacion().getSgHotel();

        //Todos los servicios que no están incluidos en el hotel
        List<SgHotelTipoEspecificoVo> serviciosNoIncluidosHotel = this.sgHotelTipoEspecificoRemote.getAllSgHotelTipoEspecificoBySgHotelAndProvided(sgHotel.getId().intValue(), false, "id", true, false);
        //Todos los servicios a los que tiene acceso el usuario que son facturados por La Empresa
        List<SgHuespedHotelServicioVo> serviciosHuespedFacturadosEmpresa = findAllBySgHuespedHotel(idSgHuespedHotel, true);
        //Convertir de SgHotelTipoEspecificoVo a SgHuespedHotelServicioVo
        List<SgHuespedHotelServicioVo> serviciosNoIncluidosHotelConvertido = new ArrayList<SgHuespedHotelServicioVo>();

        for (SgHotelTipoEspecificoVo hte : serviciosNoIncluidosHotel) {
            SgHuespedHotelServicioVo vo = new SgHuespedHotelServicioVo();
            vo.setIdSgHotel(hte.getIdSgHotel());
            vo.setIdSgHuespedHotel(idSgHuespedHotel);
            vo.setIdSgTipoEspecifico(hte.getIdSgTipoEspecifico());
            vo.setNombreSgTipoEspecifico(hte.getNombreSgTipoEspecifico());
            vo.setFacturadoEmpresa(false);
            serviciosNoIncluidosHotelConvertido.add(vo);
        }

        //Quitar los que ya están facturados por la empresa
        Iterator<SgHuespedHotelServicioVo> i = serviciosNoIncluidosHotelConvertido.iterator();
        while (i.hasNext()) {
            SgHuespedHotelServicioVo tte = i.next();
            for (SgHuespedHotelServicioVo vo : serviciosHuespedFacturadosEmpresa) {
                if (vo.getIdSgTipoEspecifico() == tte.getIdSgTipoEspecifico()) {
                    i.remove();
                    break;
                }
            }
        }

        //lista a mostrar que contiene los servicios que ya le van a pagar al Huésped y los que no
        List<SgHuespedHotelServicioVo> result = new ArrayList<SgHuespedHotelServicioVo>();
        result.addAll(serviciosNoIncluidosHotelConvertido);
        result.addAll(serviciosHuespedFacturadosEmpresa);
        //
        return result;
    }

    
    public void updateServicios(List<SgHuespedHotelServicioVo> list, int idSgHuespedHotel, String idUsuario) {
        UtilLog4j.log.info(this, "SgHuespedHotelServicioImpl.updateServicios()");

//        //Todos los servicios a los que tiene acceso el usuario que son facturados por La Empresa
        List<SgHuespedHotelServicioVo> serviciosHuespedFacturadosEmpresa = findAllBySgHuespedHotel(idSgHuespedHotel, true);

        for (SgHuespedHotelServicioVo vo : list) { //recorrer la lista actualizada de servicios facturados
            boolean contain = false;
            if (serviciosHuespedFacturadosEmpresa == null || serviciosHuespedFacturadosEmpresa.isEmpty()) {
                if (vo.isFacturadoEmpresa()) {
                    save(idSgHuespedHotel, vo.getIdSgTipoEspecifico(), true, idUsuario);
                }
            } else {
                for (SgHuespedHotelServicioVo vofe : serviciosHuespedFacturadosEmpresa) { //buscar si está como servicio facturado por la empresa
                    if (vo.getIdSgTipoEspecifico() == vofe.getIdSgTipoEspecifico()) { //si se encuentra entre los servicios facturados por la empresa, quitarlo si el campo 'facturadoEmpresa'=false
                        contain = true;
                        if (!vo.isFacturadoEmpresa()) {
                            delete(vo.getId(), idUsuario);
                        }
                        break;
                    }
                }
                if (!contain) { //si no está como servicio facturado aún y la lista actualizada dice 'facturadoEmpresa'=true, ponerlo
                    if (vo.isFacturadoEmpresa()) {
                        save(idSgHuespedHotel, vo.getIdSgTipoEspecifico(), true, idUsuario);
                    }
                }
            }
        }
    }
}
