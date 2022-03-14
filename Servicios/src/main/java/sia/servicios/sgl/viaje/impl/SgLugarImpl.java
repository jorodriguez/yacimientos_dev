/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.*;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgLugar;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgLugarImpl extends AbstractFacade<SgLugar> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgLugarImpl() {
        super(SgLugar.class);
    }

    
    public void save(SgLugar sgLugar, String idUsuario) throws SIAException {
        UtilLog4j.log.info(this,"SgLugarImpl.save()");
        
        SgLugar sgLugarExistente = findByName(sgLugar.getNombre(), false);

        if (sgLugarExistente == null) {
            sgLugar.setGenero(new Usuario(idUsuario));
            sgLugar.setFechaGenero(new Date());
            sgLugar.setHoraGenero(new Date());
            sgLugar.setEliminado(Constantes.NO_ELIMINADO);

            super.create(sgLugar);
            UtilLog4j.log.info(this,"SgLugar CREATED SUCCESSFULLY");

        } else {
            throw new SIAException(SgLugarImpl.class.getName()
                    , "save"
                    , ""
                    , "sgl.sgLugar.mensaje.error.sgLugarExistente"
                    , ("Ya existe el sgLugar con nombre: " + sgLugar.getNombre()));
        }        
    }

    
    public void update(SgLugar sgLugar, String idUsuario) throws SIAException {
        UtilLog4j.log.info(this,"SgLugarImpl.update()");
        
        SgLugar original = super.find(sgLugar.getId());
        SgLugar existente = findByName(sgLugar.getNombre(), false);
        if (existente == null) { //Si no existe el SgLugar, crearlo
            sgLugar.setModifico(new Usuario(idUsuario));
            sgLugar.setFechaModifico(new Date());
            sgLugar.setHoraModifico(new Date());
            super.edit(sgLugar);
            UtilLog4j.log.info(this,"SgLugar UPDATED SUCCESSFULLY");
        } else { 
            if (original.getId().intValue() == existente.getId().intValue()) { //Verificar que no es el mismo SgLugar
                sgLugar.setModifico(new Usuario(idUsuario));
                sgLugar.setFechaModifico(new Date());
                sgLugar.setHoraModifico(new Date());
                super.edit(sgLugar);
                UtilLog4j.log.info(this,"SgLugar UPDATED SUCCESSFULLY");
            } else {
                throw new SIAException(SgLugar.class.getName()
                        , "update"
                        , ""
                        , "sgl.sgLugar.mensaje.error.sgLugarExistente"
                        , ("Ya existe el sgLugar con nombre: " + sgLugar.getNombre()));
            }
        }         
    }
    
    
    public void update(SgLugar sgLugar, String nombre, String idUsuario) throws SIAException {
        UtilLog4j.log.info(this,"SgLugarImpl.update()");
        
        SgLugar existente = findByName(nombre, false);
        if (existente == null) { //Si no existe el SgLugar, actualizarlo
            sgLugar.setNombre(nombre);
            sgLugar.setModifico(new Usuario(idUsuario));
            sgLugar.setFechaModifico(new Date());
            sgLugar.setHoraModifico(new Date());
            edit(sgLugar);
            UtilLog4j.log.info(this,"SgLugar UPDATED SUCCESSFULLY");
        } else { //Si existe el SgLugar, validar que no sea el mismo
            if (sgLugar.getId().intValue() == existente.getId().intValue()) { //Verificar que no es el mismo SgLugar
                sgLugar.setNombre(nombre);
                sgLugar.setModifico(new Usuario(idUsuario));
                sgLugar.setFechaModifico(new Date());
                sgLugar.setHoraModifico(new Date());
                edit(sgLugar);
                UtilLog4j.log.info(this,"SgLugar UPDATED SUCCESSFULLY");
            } else {
                throw new SIAException(SgLugar.class.getName()
                        , "update"
                        , ""
                        , "sgl.sgLugar.mensaje.error.sgLugarExistente"
                        , ("Ya existe el sgLugar con nombre: " + sgLugar.getNombre()));
            }
        }
    }    

    
    public void delete(SgLugar sgLugar, String idUsuario) throws SIAException {
        UtilLog4j.log.info(this,"SgLugarImpl.delete()");

        if(!isUsed(sgLugar)) {
            sgLugar.setModifico(new Usuario(idUsuario));
            sgLugar.setFechaModifico(new Date());
            sgLugar.setHoraModifico(new Date());
            sgLugar.setEliminado(Constantes.ELIMINADO);
            super.edit(sgLugar);
            UtilLog4j.log.info(this,"SgLugar DELETED SUCCESSFULLY");
        } else {
            throw new SIAException(SgLugar.class.getName()
                    , "delete()"
                    , ""
                    , "sistema.mensaje.error.eliminar.registroUsado"
                    , ("Ya está usado SgLugar: " + sgLugar.getNombre()));
        }
    }
    
    
    public boolean isUsed(SgLugar sgLugar) {
        UtilLog4j.log.info(this,"SgLugarImpl.isUsed()");
    
        int cont = 0;
        
        List<Object> list = null;

        list = em.createQuery("SELECT m FROM SgMotivoRetraso m WHERE m.sgLugar.id = :idSgLugar AND m.eliminado = :eliminado")
                .setParameter("eliminado", Constantes.NO_ELIMINADO)
                .setParameter("idSgLugar", sgLugar.getId().intValue())
                .getResultList();
        if(list != null && !list.isEmpty()) {
            UtilLog4j.log.info(this,"SgLugar " + sgLugar.getId().intValue() + " usado en SgMotivoRetraso");
            cont++;
            list.clear();
        }         
        
        return (cont == 0 ? false : true);         
    }    

    
    public SgLugar findByName(String nombre, boolean eliminado) throws SIAException {
        UtilLog4j.log.info(this,"SgLugarImpl.findByName()");

        SgLugar sgLugar = null;

        try {
            sgLugar = (SgLugar) em.createQuery("SELECT l FROM SgLugar l WHERE l.eliminado = :eliminado AND l.nombre = :nombre")
                    .setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO))
                    .setParameter("nombre", nombre)
                    .getSingleResult();
        } catch (NonUniqueResultException nure) {
            UtilLog4j.log.info(this,nure.getMessage());
            UtilLog4j.log.info(this,"SgLugar: " + nombre + "está duplicado");
            return null;
        } catch (NoResultException nre) {
            UtilLog4j.log.info(this,nre.getMessage());
            UtilLog4j.log.info(this,"No existe el SgLugar:" + nombre);
            return null;
        }
        
        UtilLog4j.log.info(this,"Se encontró el SgLugar: " + nombre);
        return sgLugar;
    }

    
    public List<SgLugar> findAll(String orderByField, boolean sortAscending, boolean eliminado) {
        UtilLog4j.log.info(this,"SgLugarImpl.findAll()");
        
        List<SgLugar> sgLugarList;

        String consulta = "SELECT a FROM SgLugar a WHERE a.eliminado = :eliminado";

        if (orderByField != null && !orderByField.isEmpty()) {
            consulta += " ORDER BY a." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
        }

        Query q = em.createQuery(consulta);

        //Asignando parámetros
        q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));

        sgLugarList = q.getResultList();

        UtilLog4j.log.info(this,this.getClass().getName() + ": Se encontraron " + (sgLugarList != null ? sgLugarList.size() : 0) + " registros");

        return (sgLugarList != null ? sgLugarList : Collections.EMPTY_LIST);
    }
}
