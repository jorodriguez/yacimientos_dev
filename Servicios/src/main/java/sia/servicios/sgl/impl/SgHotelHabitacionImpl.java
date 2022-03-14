/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgHotel;
import sia.modelo.SgHotelHabitacion;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@LocalBean 
public class SgHotelHabitacionImpl extends AbstractFacade<SgHotelHabitacion>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    //Variables
    @Inject
    SgTipoEspecificoImpl sgTipoEspecificoRemote;
    
    private boolean todoBien = false;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgHotelHabitacionImpl() {
        super(SgHotelHabitacion.class);
    }

    
    public void createHotelHabitacion(SgHotel sgHotel, int idTipoEspecificoHabitacion, SgHotelHabitacion hotelHabitacion, Usuario usuariogenero) {
        UtilLog4j.log.info(this,"SgHotelHabitacionImpl.createHotelHabitacion");
        try {
            
            SgTipoEspecifico tipoEspecifico = sgTipoEspecificoRemote.find(idTipoEspecificoHabitacion);
            
            //
            hotelHabitacion.setEliminado(Constantes.BOOLEAN_FALSE);
            hotelHabitacion.setFechaGenero(new Date());
            hotelHabitacion.setHoraGenero(new Date());
            hotelHabitacion.setGenero(usuariogenero);
            hotelHabitacion.setSgHotel(sgHotel);
            hotelHabitacion.setSgTipoEspecifico(tipoEspecifico);
            super.create(hotelHabitacion);
            UtilLog4j.log.info(this," Hotel-Habitacion se creo satisfactoriamente ");
      
            //Actualizar el campo 'usado' a True del tipoEspecifico
            tipoEspecifico.setFechaGenero(new Date());
            tipoEspecifico.setHoraGenero(new Date());
            tipoEspecifico.setUsado(Constantes.BOOLEAN_TRUE);
            sgTipoEspecificoRemote.edit(tipoEspecifico);

        } catch (Exception e) {
            UtilLog4j.log.fatal(this,e.getMessage());
        }

    }

    
    public void updateHotelHabitacion(SgHotel sgHotel, SgHotelHabitacion hotelHabitacion, int idTipoHabitacion, Usuario usuariogenero) {
        try {
            hotelHabitacion.setEliminado(Constantes.BOOLEAN_FALSE);
            hotelHabitacion.setFechaGenero(new Date());
            hotelHabitacion.setHoraGenero(new Date());
            hotelHabitacion.setGenero(usuariogenero);
            hotelHabitacion.setSgHotel(sgHotel);
            //   hotelHabitacion.setSgTipoHabitacionHotel(sgTipoHabitacionService.find(idTipoHabitacion));
            super.create(hotelHabitacion);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,e.getMessage());
        }
    }
    
    
    public boolean updatePrecioHabitacion(int idHabitacion,BigDecimal nuevoPrecio, String idUsuariogenero) {
        try {
           SgHotelHabitacion habitacion = find(idHabitacion);
           if(habitacion!=null){
                habitacion.setFechaModifico(new Date());
                habitacion.setHoraModifico(new Date());
                habitacion.setModifico(new Usuario(idUsuariogenero));
                habitacion.setPrecio(nuevoPrecio);
                edit(habitacion);
                UtilLog4j.log.info(this,"El precio fue modificado correctamente...");
                
           }
           return true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,e.getMessage());
            return false;
        }
    }

    
    public List<SgHotelHabitacion> findAllHabitacionesToHotel(SgHotel hotel) {
        UtilLog4j.log.info(this,"Entrando a traer habitaciones de un hotel");
        List<SgHotelHabitacion> listReturn = null;
        try {
            listReturn = em.createQuery("SELECT hh FROM SgHotelHabitacion hh "
                    + "WHERE hh.sgHotel.id = :idHotel AND hh.eliminado = :eliminado ORDER BY hh.id DESC ").setParameter("idHotel", hotel.getId()).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,e.getMessage());
        }
        UtilLog4j.log.info(this,"Se encontraron  " + listReturn.size() + "Habitaciones");
        return listReturn;
    }

    
    public void deleteHotelHabitacion(SgHotelHabitacion habitacion) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    public void deleteHotelHabitacion(SgHotelHabitacion habitacion, Usuario usuarioGenero) {
        UtilLog4j.log.info(this,"sghotelHabitacionImpl.deleteHotelHabitacion");
        try {
            habitacion.setEliminado(Constantes.BOOLEAN_TRUE);
            habitacion.setFechaGenero(new Date());
            habitacion.setHoraGenero(new Date());
            habitacion.setGenero(usuarioGenero);
            super.edit(habitacion);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,e.getMessage());
        }
        UtilLog4j.log.info(this,"se eliminó la habitación");
    }
       

    
    public boolean deleteAllHabitacionesToHotel(SgHotel hotel, Usuario usuarioGenero) {
        List<SgHotelHabitacion> listaEliminar;
        todoBien = false;
        try {
            if (hotel != null) {
                listaEliminar = findAllHabitacionesToHotel(hotel);
                for (SgHotelHabitacion hh : listaEliminar) {
                    deleteHotelHabitacion(hh, usuarioGenero);
                    UtilLog4j.log.info(this,"se eliminó la habitación" + hh.getSgTipoEspecifico().getNombre());
                }
                todoBien = true;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,e.getMessage());
        }
        UtilLog4j.log.info(this,"se eliminaron todas las habitaciones");
        return todoBien;
    }

    /*
     * Buscar la relacion de las habitaciones de un hotel en la relacion de huespedHotel
     * para validar la eliminacion del mismo
     */
    
    public boolean buscarHotelHabitacionEnRelacion(SgHotel hotel) {
        UtilLog4j.log.info(this,"Buscadndo si el hotel no tiene relacion en HuespedHotel");
        //  UtilLog4j.log.info(this,"hotel a buscarHabitacionRepetida "+hotel.getProveedor().getNombre());
        boolean ret = false;
        List<SgHotelHabitacion> lret = null;
        try {
            lret = em.createQuery("SELECT hh FROM SgHotelHabitacion hh "
                    + " WHERE hh.sgHotel.id = :idHotel "
                    + " AND hh.eliminado = :eliminado AND hh.id IN "
                    + " (SELECT hu.sgHotelHabitacion.id FROM SgHuespedHotel hu)").setParameter("idHotel", hotel.getId()).setParameter("eliminado", Constantes.NO_ELIMINADO).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"existio un error en la consulta.." + e.getMessage());
//            return false;
        }
        if (lret.size() > 0) {
            UtilLog4j.log.info(this,"se encontro relacion " + lret.size());
            return true;
        } else {
            UtilLog4j.log.info(this," No entoncotro relacion ");
            return false;
        }
    }

    
    public boolean buscarHabitacionRepetida(int idHotel, int idTipoHabitacion) {
        //SgHotelHabitacion hh = null;
        List<SgHotelHabitacion> lret = null;
        UtilLog4j.log.info(this,"SgHotelHabitacionImpl.buscarRepetido");
        try {
            lret =  em.createQuery("SELECT hh FROM SgHotelHabitacion hh "
                    + " WHERE hh.sgHotel.id = :idHotel AND hh.sgTipoEspecifico.id = :idTipoHabitacion AND hh.eliminado = :eliminado  ")
                    .setParameter("idHotel", idHotel)                    
                    .setParameter("idTipoHabitacion", idTipoHabitacion)
                    .setParameter("eliminado", Constantes.BOOLEAN_FALSE)
                    .getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion " + e.getMessage());
        }
        if (lret.size() > 0) {
            UtilLog4j.log.info(this,"se encontro el Hotel " + lret.size());
            return true;
        } else {
            UtilLog4j.log.info(this," No entoncotro Hotel");
            return false;
        }
    }

     /*
     * Buscar la relacion de las habitaciones de una habitacion en la relacion de huespedHotel
     * para validar la eliminacion de la misma
     */
    
    public boolean buscarHabitacionEnRelacion(SgHotelHabitacion habitacion) {
        UtilLog4j.log.info(this,"Buscando si la habitacion no esta relacionada con un HuespedHotel");
        boolean ret = false;
        List<SgHotelHabitacion> lret = null;        
        
        try {
//            lret = em.createQuery("SELECT hh FROM SgHotelHabitacion hh "
//                    + " WHERE hh.sgHotel.id = :idHotel AND hh.id = :idHabitacion"
//                    + " AND hh.eliminado = :eliminado AND hh.id IN "
//                    + " (SELECT hu.sgHotelHabitacion.id FROM SgHuespedHotel hu) ")
              lret = em.createQuery("SELECT hu.sgHotelHabitacion FROM SgHuespedHotel hu "
                      + " WHERE hu.sgHotelHabitacion.id = :idHabitacion ")            
//                    .setParameter("idHotel", habitacion.getSgHotel().getId())
                    .setParameter("idHabitacion", habitacion.getId())
                    .getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"existio un error en la consulta.." + e.getMessage());
        }
        if (lret.size() > 0) {
            UtilLog4j.log.info(this,"se encontro relacion " + lret.size());
            return true;
        } else {
            UtilLog4j.log.info(this," No entoncotro relacion ");
            return false;
        }
    }
}
