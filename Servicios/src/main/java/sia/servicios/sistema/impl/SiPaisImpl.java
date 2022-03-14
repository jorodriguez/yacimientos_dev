/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.*;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.modelo.SiPais;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SiPaisImpl extends AbstractFacade<SiPais> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;        
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiPaisImpl() {
        super(SiPais.class);
    }
    
    
    public void save(SiPais siPais, String idUsuario) throws ExistingItemException {
        UtilLog4j.log.info(this,"SiPaisImpl.save()");

        SiPais existente = findByName(siPais.getNombre(), false);

        if (existente == null) {
            siPais.setGenero(new Usuario(idUsuario));
            siPais.setFechaGenero(new Date());
            siPais.setHoraGenero(new Date());
            siPais.setEliminado(Constantes.NO_ELIMINADO);

            super.create(siPais);
            UtilLog4j.log.info(this,"SiPais CREATED SUCCESSFULLY");

        } else {
            throw new ExistingItemException("siPais.mensaje.error.siPaisExistente", siPais.getNombre(), siPais);
        }
    }    
    
    
    public void update(SiPais siPais, String idUsuario) throws ExistingItemException {
        UtilLog4j.log.info(this,"SiPaisImpl.update()");
        
        SiPais original = super.find(siPais.getId());
        SiPais existente = findByName(siPais.getNombre(), false);
        if (existente == null) {
            siPais.setModifico(new Usuario(idUsuario));
            siPais.setFechaModifico(new Date());
            siPais.setHoraModifico(new Date());
            super.edit(siPais);
            UtilLog4j.log.info(this,"SiPais UPDATED SUCCESSFULLY");
        } else {
            if (original.getId().intValue() == existente.getId().intValue()) {
                siPais.setModifico(new Usuario(idUsuario));
                siPais.setFechaModifico(new Date());
                siPais.setHoraModifico(new Date());
                super.edit(siPais);
                UtilLog4j.log.info(this,"SiPais UPDATED SUCCESSFULLY");
            } else {
                throw new ExistingItemException("siPais.mensaje.error.siPaisExistente", existente.getNombre(), existente);
            }
        }        
    }   
    
    
    public void delete(SiPais siPais, String idUsuario) throws ItemUsedBySystemException {
        UtilLog4j.log.info(this,"SiPaisImpl.delete()");
        if(!isUsed(siPais)) {
            siPais.setModifico(new Usuario(idUsuario));
            siPais.setFechaModifico(new Date());
            siPais.setHoraModifico(new Date());
            siPais.setEliminado(Constantes.ELIMINADO);

            super.edit(siPais);
            UtilLog4j.log.info(this,"SiPais DELETED SUCCESSFULLY");
        } else {
            throw new ItemUsedBySystemException(siPais.getNombre(), siPais);
        }
    }  
    
    
    public SiPais findByName(String nombre, boolean eliminado) {
        UtilLog4j.log.info(this,"SiPaisImpl.findByName()");

        SiPais siPais = null;

        try {
            siPais = (SiPais) em.createQuery("SELECT p FROM SiPais p WHERE p.eliminado = :eliminado AND p.nombre = :nombre")
                    .setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO))
                    .setParameter("nombre", nombre)
                    .getSingleResult();
        } catch (NonUniqueResultException nure) {
            UtilLog4j.log.fatal(this,nure.getMessage());
            UtilLog4j.log.fatal(this,"Se encontró más de un resultado para el SiPais con nombre: " + nombre);
            return siPais;
        } catch (NoResultException nre) {
            UtilLog4j.log.fatal(this,nre.getMessage());
            UtilLog4j.log.fatal(this,"No se encontró ningún SiPais con nombre:" + nombre);
            return siPais;
        }
        
        return siPais;
    }   
    
    
    public List<SiPais> findAll(String orderByField, boolean sortAscending, boolean eliminado) {
        UtilLog4j.log.info(this,"SiPaisImpl.findAll()");
        
        List<SiPais> list;

        String query = "SELECT p FROM SiPais p WHERE p.eliminado = :eliminado";

        if (orderByField != null && !orderByField.isEmpty()) {
            query += " ORDER BY p." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
        }

        Query q = em.createQuery(query);

        //Asignando parámetros
        q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));

        list = q.getResultList();

        UtilLog4j.log.info(this,"Se encontraron " + (list != null ? list.size() : 0) + " SiPais");

        return (list != null ? list : Collections.EMPTY_LIST);
    }
    
    public boolean isUsed(SiPais siPais) {
        UtilLog4j.log.info(this,"SiPaisImpl.isUsed()");
    
        int cont = 0;
        
        List<Object> list = Collections.EMPTY_LIST;

        list = em.createQuery("SELECT l FROM SgLicencia l WHERE l.siPais.id = :idSiPais AND l.eliminado = :eliminado")
                .setParameter("eliminado", Constantes.NO_ELIMINADO)
                .setParameter("idSiPais", siPais.getId())
                .getResultList();
        if(list != null && !list.isEmpty()) {
            UtilLog4j.log.info(this,"SiPais " + siPais.getId().intValue() + " usado en SgLicencia");
            cont++;
            list.clear();
        }      
        
        list = em.createQuery("SELECT d FROM SgDireccion d WHERE d.siPais.id = :idSiPais AND d.eliminado = :eliminado")
                .setParameter("eliminado", Constantes.NO_ELIMINADO)
                .setParameter("idSiPais", siPais.getId().intValue())
                .getResultList();
        if(list != null && !list.isEmpty()) {
            UtilLog4j.log.info(this,"SiPais " + siPais.getId().intValue() + " usado en SgDireccion");
            cont++;
            list.clear();
        }   
        
        list = em.createQuery("SELECT e FROM SiEstado e WHERE e.siPais.id = :idSiPais AND e.eliminado = :eliminado")
                .setParameter("eliminado", Constantes.NO_ELIMINADO)
                .setParameter("idSiPais", siPais.getId().intValue())
                .getResultList();
        if(list != null && !list.isEmpty()) {
            UtilLog4j.log.info(this,"SiPais " + siPais.getId().intValue() + " usado en SiEstado");
            cont++;
            list.clear();
        }   
        
        list = em.createQuery("SELECT e FROM SiCiudad e WHERE e.siPais.id = :idSiPais AND e.eliminado = :eliminado")
                .setParameter("eliminado", Constantes.NO_ELIMINADO)
                .setParameter("idSiPais", siPais.getId().intValue())
                .getResultList();
        if(list != null && !list.isEmpty()) {
            UtilLog4j.log.info(this,"SiPais " + siPais.getId().intValue() + " usado en SiCiudad");
            cont++;
            list.clear();
        }        
        
        return (cont == 0 ? false : true);       
    }    
}
