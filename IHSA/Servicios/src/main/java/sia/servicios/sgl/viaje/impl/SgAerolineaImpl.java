/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.*;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.modelo.SgAerolinea;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Stateless 
public class SgAerolineaImpl extends AbstractFacade<SgAerolinea> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgAerolineaImpl() {
        super(SgAerolinea.class);
    }
    
    
    public void save(SgAerolinea sgAerolinea, String idUsuario) throws ExistingItemException {
        UtilLog4j.log.info(this,"SgAerolineaImpl.save()");
        System.out.println("@save aerolinea");

        SgAerolinea existente = findByName(sgAerolinea.getNombre(), false);

        if (existente == null) {
            sgAerolinea.setGenero(new Usuario(idUsuario));
            sgAerolinea.setFechaGenero(new Date());
            sgAerolinea.setHoraGenero(new Date());
            sgAerolinea.setEliminado(Constantes.NO_ELIMINADO);

            super.create(sgAerolinea);
            UtilLog4j.log.info(this,"SgAerolinea CREATED SUCCESSFULLY");
        } else {
            throw new ExistingItemException("sgl.aerolinea.mensaje.error.aerolineaExistente", sgAerolinea.getNombre(), sgAerolinea);
        }

    }

    
    public void update(SgAerolinea aerolinea, String idUsuario) throws ExistingItemException {
        UtilLog4j.log.info(this,"SgAerolineaImpl.update()");
        
        SgAerolinea original = super.find(aerolinea.getId());
        SgAerolinea existente = findByName(aerolinea.getNombre(), false);
        
        if (existente == null) {
            aerolinea.setModifico(new Usuario(idUsuario));
            aerolinea.setFechaModifico(new Date());
            aerolinea.setHoraModifico(new Date());

            super.edit(aerolinea);
            UtilLog4j.log.info(this,"SgAerolinea UPDATED SUCCESSFULLY");
        } else {
            if (original.getId().intValue() == existente.getId().intValue()) {
                aerolinea.setModifico(new Usuario(idUsuario));
                aerolinea.setFechaModifico(new Date());
                aerolinea.setHoraModifico(new Date());

                super.edit(aerolinea);
                UtilLog4j.log.info(this,"SgAerolinea UPDATED SUCCESSFULLY");
            } else {
                throw new ExistingItemException("sgl.aerolinea.mensaje.error.aerolineaExistente", existente.getNombre(), existente);
            }
        }        
    }

    
    public void delete(SgAerolinea sgAerolinea, String idUsuario) throws ItemUsedBySystemException {
        UtilLog4j.log.info(this,"SgAerolineaImpl.delete()");

        if(!isUsed(sgAerolinea)) {
            sgAerolinea.setModifico(new Usuario(idUsuario));
            sgAerolinea.setFechaModifico(new Date());
            sgAerolinea.setHoraModifico(new Date());
            sgAerolinea.setEliminado(Constantes.ELIMINADO);

            super.edit(sgAerolinea);
            UtilLog4j.log.info(this,"SgAerolinea DELETED SUCCESSFULLY");
        } else {
            throw new ItemUsedBySystemException(sgAerolinea.getNombre(), sgAerolinea);
        }
    }
    
    
    public SgAerolinea findByName(String nombre, boolean eliminado) {
        UtilLog4j.log.info(this,"SgAerolineaImpl.findByName()");

        SgAerolinea aerolinea = null;

        try {
            aerolinea = (SgAerolinea) em.createQuery("SELECT a FROM SgAerolinea a WHERE a.eliminado = :eliminado AND a.nombre = :nombre")
                    .setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO))
                    .setParameter("nombre", nombre)
                    .getSingleResult();
        } catch (NonUniqueResultException nure) {
            UtilLog4j.log.fatal(this,nure.getMessage());
            UtilLog4j.log.fatal(this,"Se encontró más de un resultado para la SgAerolinea con nombre: " + nombre);
            return aerolinea;
        } catch (NoResultException nre) {
            UtilLog4j.log.fatal(this,nre.getMessage());
            UtilLog4j.log.fatal(this,"No se encontró ninguna SgAerolinea con nombre:" + nombre);
            return aerolinea;
        }
        
        return aerolinea;
    }   

    
    public List<SgAerolinea> findAll(String orderByField, boolean sortAscending, boolean eliminado) {
        UtilLog4j.log.info(this,"SgAerolineaImpl.findAll()");
        
        List<SgAerolinea> aerolineas;

        String query = "SELECT a FROM SgAerolinea a WHERE a.eliminado = :eliminado";

        if (orderByField != null && !orderByField.isEmpty()) {
            query += " ORDER BY a." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
        }

        Query q = em.createQuery(query);

        //Asignando parámetros
        q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));

        aerolineas = q.getResultList();

        UtilLog4j.log.info(this,"Se encontraron " + (aerolineas != null ? aerolineas.size() : 0) + " Aerolíneas");

        return (aerolineas != null ? aerolineas : Collections.EMPTY_LIST);
    }
    
    public boolean isUsed(SgAerolinea sgAerolinea) {
        UtilLog4j.log.info(this,"SgAerolineaImpl.isUsed()");
        
        int cont = 0;
        
        List<Object> list = null;

        list = em.createQuery("SELECT a FROM SgDetalleItinerario a WHERE a.sgAerolinea.id = :idSgAerolinea AND a.eliminado = :eliminado")
                .setParameter("eliminado", Constantes.NO_ELIMINADO)
                .setParameter("idSgAerolinea", sgAerolinea.getId().intValue())
                .getResultList();
        if(list != null && !list.isEmpty()) {
            UtilLog4j.log.info(this,"SgAerolinea " + sgAerolinea.getId().intValue() + " usado en SgDetalleItinerario");
            cont++;
            list.clear();
        }         
        
        return (cont == 0 ? false : true);
    }
}
