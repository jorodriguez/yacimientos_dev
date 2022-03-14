/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.proveedor.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.SucursalProveedor;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.FolioImpl;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SucursalProveedorImpl extends AbstractFacade<SucursalProveedor> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SucursalProveedorImpl() {
        super(SucursalProveedor.class);
    }

    
    public void guardarModificacionSucursal(SucursalProveedor sucursalProveedor) {
        this.edit(sucursalProveedor);
    }
    @Inject
    private FolioImpl folioServicioRemoto;
    @Inject
    private ProveedorServicioImpl proveedorServicioRemoto;

    
    public void guardarSucursal(int idProveedor, SucursalProveedor sucursalProveedor) {
        sucursalProveedor.setProveedor(this.proveedorServicioRemoto.find(idProveedor));
        this.create(sucursalProveedor);
    }

    
    public List<SucursalProveedor> traerSucursalPorProveedor(int idProveedor) {
        return em.createQuery("SELECT s FROM SucursalProveedor s WHERE s.proveedor.id = :id ").setParameter("id", idProveedor).getResultList();
    }
}
