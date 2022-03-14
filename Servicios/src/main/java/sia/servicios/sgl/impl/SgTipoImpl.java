/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgTipo;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.excepciones.SIAException;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgTipoImpl extends AbstractFacade<SgTipo> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgTipoImpl() {
        super(SgTipo.class);
    }

    
    public void guardarTipo(SgTipo sgTipo, Usuario usuario, boolean BOOLEAN_FALSE) throws SIAException, Exception {
        UtilLog4j.log.info(this,"SgTipoImpl.guardarTipo()");

        SgTipo tipoExistente = null;

        tipoExistente = getTipoByNombre(sgTipo.getNombre(), BOOLEAN_FALSE);

        if (tipoExistente == null) {
            sgTipo.setGenero(usuario);
            sgTipo.setFechaGenero(new Date());
            sgTipo.setHoraGenero(new Date());
            sgTipo.setEliminado(BOOLEAN_FALSE);
            super.create(sgTipo);
        }
        else {
            throw new SIAException(SgTipoImpl.class.getName()
                    , "guardarTipo"
                    , ("Ya existe el Tipo: " + sgTipo.getNombre()));
        }
    }

    
    public void modificarTipo(SgTipo sgTipo, Usuario usuario) throws SIAException, Exception {
        SgTipo original = super.find(sgTipo.getId());

        SgTipo tipoExistente = null;
        tipoExistente = getTipoByNombre(sgTipo.getNombre(), Constantes.NO_ELIMINADO);

        if (tipoExistente == null) {
            sgTipo.setGenero(usuario);
            sgTipo.setFechaGenero(new Date());
            sgTipo.setHoraGenero(new Date());
            super.edit(sgTipo);
        } else {
            if (original.getId().intValue() == tipoExistente.getId().intValue()) {
                sgTipo.setModifico(usuario);
                sgTipo.setFechaModifico(new Date());
                sgTipo.setHoraModifico(new Date());
                super.edit(sgTipo);
            } else {
                throw new SIAException(SgTipoImpl.class.getName()
                        , "guardarTipo"
                        , ("Ya existe el Tipo: " + sgTipo.getNombre()));
            }
        }
    }

    
    public void eliminarTipo(SgTipo sgTipo, Usuario usuario, boolean eliminado) throws SIAException, Exception {
        UtilLog4j.log.info(this,"SgTipoImpl.eliminarTipo()");

        if (!isUsed(sgTipo)) {
            sgTipo.setGenero(usuario);
            sgTipo.setFechaGenero(new Date());
            sgTipo.setHoraGenero(new Date());
            sgTipo.setEliminado(eliminado);
            super.edit(sgTipo);
        }
        else {
            throw new SIAException(SgTipoImpl.class.getName()
                    , "eliminarTipo"
                    , ("El Tipo : " + sgTipo.getNombre() + " no se puede eliminar porque ya está siendo usado en otras partes del Sistema"));
        }
    }

    
    public List<SgTipo> traerTipo(Usuario usuario, boolean eliminado) {
        try {
            return em.createQuery("SELECT t FROM SgTipo t WHERE t.eliminado = :eli ORDER BY t.id ASC").setParameter("eli", eliminado).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    
    public SgTipo getTipoByNombre(String nombreTipo, boolean status) throws Exception {
        UtilLog4j.log.info(this,"SgTipoImpl.getTipoByNombre()");
        SgTipo tipo = null;
        
        if(nombreTipo != null && !nombreTipo.equals("")) {
            try {
                tipo = (SgTipo)em.createQuery("SELECT t FROM SgTipo t WHERE t.eliminado = :estado AND t.nombre = :nombreTipo")
                        .setParameter("estado", status)
                        .setParameter("nombreTipo", nombreTipo)
                        .getSingleResult();
            }
            catch (NonUniqueResultException nure) {
                UtilLog4j.log.info(this,nure.getMessage());
                UtilLog4j.log.info(this,"Se encontró más de un resultado para Tipo por Nombre");
                return tipo;
            }
            catch(NoResultException nre) {
                UtilLog4j.log.info(this,nre.getMessage());
                UtilLog4j.log.info(this,"No se encontró ningún tipo con nombre:" + nombreTipo);
                return tipo;                
            }
            catch(Exception e) {
                UtilLog4j.log.info(this,e.getMessage());
                UtilLog4j.log.info(this,"Ocurrió un error desconocido al hacer la consulta de tipo por nombre: " + nombreTipo);
                return tipo;
            }
            if(tipo != null) {
                UtilLog4j.log.info(this,"Se encontró el tipo: " + tipo);
                return tipo;
            }
            else {
                return tipo;
            }
        }
        else {
            return tipo;
        }
    }
    
    public boolean isUsed(SgTipo sgTipo) {
        UtilLog4j.log.info(this,"SgTipoImpl.isUsed()");
        
        int cont = 0;
        
        List<Object> tipos = null;

        tipos = em.createQuery("SELECT t FROM SgTipoTipoEspecifico t WHERE t.sgTipo.id = :idSgTipo AND t.eliminado = :eliminado")
                .setParameter("eliminado", Constantes.NO_ELIMINADO)
                .setParameter("idSgTipo", sgTipo.getId())
                .getResultList();
        if(tipos != null && !tipos.isEmpty()) {
            UtilLog4j.log.info(this,"SgTipo " + sgTipo.getId() + " usado en SgTipoTipoEspecifico");
            cont++;
            tipos.clear();
        }      
        
        tipos = em.createQuery("SELECT t FROM SgTipoSolicitudViaje t WHERE t.sgTipo.id = :idSgTipo AND t.eliminado = :eliminado")
                .setParameter("eliminado", Constantes.NO_ELIMINADO)
                .setParameter("idSgTipo", sgTipo.getId())
                .getResultList();
        if(tipos != null && !tipos.isEmpty()) {
            UtilLog4j.log.info(this,"SgTipo " + sgTipo.getId() + " usado en SgTipoSolicitudViaje");
            cont++;
            tipos.clear();
        }           
        
        return (cont == 0 ? false : true);
    }
}