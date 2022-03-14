/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Proveedor;
import sia.modelo.SgAseguradora;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@LocalBean 
public class SgAseguradoraImpl extends AbstractFacade<SgAseguradora> {
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgAseguradoraImpl() {
        super(SgAseguradora.class);
    }

    
    public void createAseguradora(Proveedor proveedor, Usuario usuarioGenero) {
        UtilLog4j.log.info(this, "createAseguradora");
        try {
            SgAseguradora ase = new SgAseguradora();
            ase.setProveedor(proveedor);
            ase.setGenero(usuarioGenero);
            ase.setEliminado(Constantes.BOOLEAN_FALSE);
            ase.setFechaGenero(new Date());
            ase.setHoraGenero(new Date());
            super.create(ase);            

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en crear Aseguradora " + e.getMessage());
        }

    }

    
    public void deleteAseguradora(SgAseguradora sgAseguradora, Usuario usuarioGenero) {
        UtilLog4j.log.info(this, "deleteAseguradora");        
        try {            
            sgAseguradora.setGenero(usuarioGenero);
            sgAseguradora.setFechaGenero(new Date());
            sgAseguradora.setHoraGenero(new Date());
            sgAseguradora.setEliminado(Constantes.BOOLEAN_TRUE);
            super.edit(sgAseguradora);

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en crear Aseguradora " + e.getMessage());
        }
    }

    
    public List<SgAseguradora> findAllAseguradoras() {
        UtilLog4j.log.info(this, "finallAseguradoras");
            List<SgAseguradora> ret = null;
            try {
                ret = em.createQuery("SELECT a FROM SgAseguradora a "
                        + "WHERE a.eliminado = :eliminado ORDER BY a.id DESC ").setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList();
                 UtilLog4j.log.info(this, "Resultado de aseguradoras "+ret.size());
                return ret;
            } catch (Exception e) {
                UtilLog4j.log.fatal(this, "Excepcion en buscar Aseguradoras Proveedor " + e.getMessage());
                return null;
            }
        }
    
    
    public List<Proveedor> findAllAseguradorasProveedor() {
        UtilLog4j.log.info(this, "findAllAseguradorasProveedor");
            List<Proveedor> ret = null;
            try {
                ret = em.createQuery("SELECT a.proveedor FROM SgAseguradora a "
                        + "WHERE a.eliminado = :eliminado ORDER BY a.id DESC ").setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList();
                 UtilLog4j.log.info(this, "Resultado de aseguradoras "+ret.size());
                return ret;
            } catch (Exception e) {
                UtilLog4j.log.fatal(this, "Excepcion en buscar Aseguradoras Proveedor " + e.getMessage());
                return null;
            }
            
        }


    
    public boolean findAseguradora(String nombreProv) {
        SgAseguradora ret = null;
        boolean bolret = false;
        try {
            ret = (SgAseguradora) em.createQuery("SELECT a FROM SgAseguradora a "
                    + "WHERE a.proveedor.nombre = :nombreProveedor AND a.eliminado = :eliminado ").setParameter("eliminado", Constantes.BOOLEAN_FALSE).setParameter("nombreProveedor", nombreProv).getSingleResult();
            //System.out.print("Retorno "+ret.getProveedor().getNombre());
            if (ret != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en buscar el proveedor " + e.getMessage());
            return false;
        }
    }

    /*
     * buscara en la tabla mantenimiento para validar la eliminacion
     *
     */
    
    public void findInMantenimiento(SgAseguradora aseguradora) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
