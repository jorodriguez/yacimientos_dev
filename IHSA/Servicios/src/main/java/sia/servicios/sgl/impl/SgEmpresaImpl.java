/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.*;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Stateless 
public class SgEmpresaImpl extends AbstractFacade<SgEmpresa> {
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgEmpresaImpl() {
        super(SgEmpresa.class);
    }

    
    public List<SgEmpresa> getAllEmpresa(boolean eliminado) {
        UtilLog4j.log.info(this,"SgEmpresaImpl.getAllEmpresa()");
        List<SgEmpresa> empresaList = null;
        try {
            empresaList = em.createQuery("SELECT e FROM SgEmpresa e WHERE e.eliminado = :eliminado ORDER BY e.nombre ASC").setParameter("eliminado", eliminado).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Existió un error al consultar las empresas " + e.getMessage());
            return null;
        }

        return empresaList;
    }

    
    public void guardarEmpresa(Usuario usuario, SgEmpresa sgEmpresa) {
        UtilLog4j.log.info(this,"guardarEmpresa " + sgEmpresa.getNombre());
        sgEmpresa.setGenero(usuario);
        sgEmpresa.setFechaGenero(new Date());
        sgEmpresa.setHoraGenero(new Date());
        sgEmpresa.setEliminado(Constantes.BOOLEAN_FALSE);
        super.create(sgEmpresa);        
    }

    
    public void modificarEmpresa(Usuario usuario, SgEmpresa sgEmpresa) {
        try {
            UtilLog4j.log.info(this,"modificarEmpresa" + sgEmpresa.getNombre());
            UtilLog4j.log.info(this,"existe se modifico");            
            sgEmpresa.setModifico(usuario);
            sgEmpresa.setFechaModifico(new Date());
            sgEmpresa.setHoraModifico(new Date());
            super.edit(sgEmpresa);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"excepcion en modificar Empresa " + e.getMessage());
        }
    }

    
    public void eliminarEmpresa(Usuario usuario, SgEmpresa sgEmpresa, boolean eliminado) {        
        sgEmpresa.setGenero(usuario);
        sgEmpresa.setFechaGenero(new Date());
        sgEmpresa.setHoraGenero(new Date());
        sgEmpresa.setEliminado(eliminado);
        edit(sgEmpresa);
    }

    
    public SgEmpresa buscarPorNombre(String nombre) {
        UtilLog4j.log.info(this,"buscarporNombre" + nombre);
        try {
            return (SgEmpresa) em.createQuery("SELECT e FROM SgEmpresa e WHERE e.nombre = :nombre AND e.eliminado = :eli").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("nombre", nombre).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"excepcion al buscar Empresa por nombre " + e.getMessage());
            return null;
        }
    }

    
    public boolean buscarEmpresaOcupado(int idEmpresa) {
        UtilLog4j.log.info(this,"buscrEmpresaOcupado");
        SgEmpresa empresa = null;
        List<SgInvitado> listV = null;
        try {
            listV = em.createQuery("SELECT i FROM SgInvitado i WHERE i.sgEmpresa.id = :idEmpresa AND i.eliminado = :eli").setParameter("eli", Constantes.BOOLEAN_FALSE).setParameter("idEmpresa", idEmpresa).getResultList();

            if (listV.size() > 0) {
                UtilLog4j.log.info(this,"Se encontro en la relacion ");
                return true;
            } else {
                UtilLog4j.log.info(this,"NO se encontro en la relacion ");
                return false;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion en " + e.getMessage());
            return true;
        }
    }

    
    public List<SgEmpresa> getAllCompanyByNomina() {
        try {
            return em.createQuery("SELECT e FROM SgEmpresa e WHERE e.eliminado = :eliminado AND  e.nomina = :nom"
                    + " ORDER BY e.nombre ASC ").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("nom", Constantes.BOOLEAN_TRUE).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Existió un error al consultar las empresas " + e);
            return null;
        }
    }
    
    
    public List<Object[]> traerEmpresasJson() {
	List<Object[]> lista = null;
	clearQuery();
	try{
            query.append("SELECT e.id, e.nombre FROM Sg_Empresa e");
	query.append(" WHERE e.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append(" order by e.nombre asc");
	lista = em.createNativeQuery(query.toString()).getResultList();
        } catch (Exception ex) {
	    Logger.getLogger(SgColorImpl.class.getName()).log(Level.SEVERE, null, ex);
	    
	}
	
	return lista;

    }
    
}
