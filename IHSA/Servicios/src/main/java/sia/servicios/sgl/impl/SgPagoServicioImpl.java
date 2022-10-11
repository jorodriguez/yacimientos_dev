/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgOficina;
import sia.modelo.SgPagoServicio;
import sia.modelo.SgPagoServicioOficina;
import sia.modelo.SgPagoServicioStaff;
import sia.modelo.SgPagoServicioVehiculo;
import sia.modelo.SgStaff;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgPagoServicioImpl extends AbstractFacade<SgPagoServicio> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgPagoServicioImpl() {
	super(SgPagoServicio.class);
    }
    @Inject
    private SgPagoServicioOficinaImpl sgPagoServicioOficinaRemote;
    @Inject
    private SgPagoServicioStaffImpl sgPagoServicioStaffRemote;
    @Inject
    private MonedaImpl monedaRemote;
    @Inject
    private ProveedorServicioImpl proveedorRemote;
    @Inject
    private SgPagoServicioVehiculoImpl sgPagoServicioVehiculoRemote;
    @Inject
    private SgTipoEspecificoImpl tipoEspecificoService;

    
    public List<SgPagoServicio> traerPagoPorTipoEspecifico(SgTipo sgTipo, SgTipoEspecifico sgTipoEspecifico, SgOficina sgOficina, String elimando) {
	try {
	    return em.createQuery("SELECT p FROM SgPagoServicio p WHERE p.sgTipo.id = :idTipo "
		    + " AND p.sgTipoEspecifico.id = :idTipoEspecifico AND p.eliminado = :eli "
		    + " ORDER BY p.id ASC").setParameter("idTipo", sgTipo.getId()).setParameter("idTipoEspecifico", sgTipoEspecifico.getId()).setParameter("eli", elimando).getResultList();

	} catch (Exception e) {
	    return null;
	}
    }

    
    public void guardarPagoServicio(int sgTipo, SgTipoEspecifico sgTipoEspecifico,
	    SgPagoServicio sgPagoServicio, Object object, Usuario usuario, boolean eliminado,
	    int idMoneda, String opcionPagar, String pro, String rfcEmpresa) {

	SgPagoServicio servicio
		= guardar(sgTipo, sgTipoEspecifico, sgPagoServicio, usuario, eliminado, idMoneda, pro, rfcEmpresa);

	if (object instanceof OficinaVO) {
	    OficinaVO sgOficina = (OficinaVO) object;
	    sgPagoServicioOficinaRemote.guardarRelacionPago(servicio, sgOficina.getId(), usuario, eliminado);
	}
	if (object instanceof SgStaff) {
	    SgStaff sgStaff = (SgStaff) object;
	    sgPagoServicioStaffRemote.guardarRelacionPago(servicio, sgStaff, usuario, eliminado);
	}
	if (object instanceof VehiculoVO) {
	    VehiculoVO sgVehiculo = (VehiculoVO) object;
	    sgPagoServicioVehiculoRemote.guardarRelacionPago(servicio, sgVehiculo.getId(), usuario, eliminado);
	}
    }

    private SgPagoServicio guardar(int sgTipo, SgTipoEspecifico sgTipoEspecifico,
	    SgPagoServicio sgPagoServicio, Usuario usuario, boolean eliminado, int idMoneda,
	    String pro, String rfcEmpresa) {
	sgPagoServicio.setSgTipo(new SgTipo(sgTipo));
	sgPagoServicio.setProveedor(proveedorRemote.getPorNombre(pro, rfcEmpresa));
	sgPagoServicio.setSgTipoEspecifico(sgTipoEspecifico);
	sgPagoServicio.setMoneda(monedaRemote.find(idMoneda));
	sgPagoServicio.setGenero(usuario);
	sgPagoServicio.setFechaGenero(new Date());
	sgPagoServicio.setHoraGenero(new Date());
	sgPagoServicio.setEliminado(eliminado);
	create(sgPagoServicio);

	//Actualizar el campo 'usado' a True del tipoEspecifico
	sgTipoEspecifico.setFechaGenero(new Date());
	sgTipoEspecifico.setHoraGenero(new Date());
	sgTipoEspecifico.setUsado(Constantes.BOOLEAN_TRUE);
	tipoEspecificoService.edit(sgTipoEspecifico);

	return sgPagoServicio;
    }

    
    public void modificarPagoServicio(SgPagoServicio sgPagoServicio, Usuario usuario, int idMoneda) {
	sgPagoServicio.setGenero(usuario);
	sgPagoServicio.setFechaGenero(new Date());
	sgPagoServicio.setHoraGenero(new Date());
	sgPagoServicio.setMoneda(monedaRemote.find(idMoneda));
	edit(sgPagoServicio);
    }

    
    public void eliminarPagoServicio(Object object, Object objectPago, Usuario usuario, boolean eliminado) {
	if (objectPago instanceof SgPagoServicioOficina) {
	    SgPagoServicioOficina sgPagoServicioOficina = (SgPagoServicioOficina) objectPago;
	    sgPagoServicioOficinaRemote.eliminarRelacionPagoServicio(sgPagoServicioOficina, usuario, eliminado);
	    editarPago(sgPagoServicioOficina.getSgPagoServicio(), usuario);
	}

	if (objectPago instanceof SgPagoServicioStaff) {
	    SgPagoServicioStaff sgPagoServicioStaff = (SgPagoServicioStaff) objectPago;
	    sgPagoServicioStaffRemote.eliminarRelacionPagoServicio(sgPagoServicioStaff, usuario, eliminado);
	    editarPago(sgPagoServicioStaff.getSgPagoServicio(), usuario);
	}
	if (objectPago instanceof SgPagoServicioVehiculo) {
	    SgPagoServicioVehiculo sgPagoServicioVehiculo = (SgPagoServicioVehiculo) objectPago;
	    sgPagoServicioVehiculoRemote.eliminarRelacionPagoServicio(sgPagoServicioVehiculo, usuario, eliminado);
	    editarPago(sgPagoServicioVehiculo.getSgPagoServicio(), usuario);
	}
    }

    private void editarPago(SgPagoServicio sgPagoServicio, Usuario usuario) {
	sgPagoServicio.setGenero(usuario);
	sgPagoServicio.setFechaGenero(new Date());
	sgPagoServicio.setHoraGenero(new Date());
	sgPagoServicio.setEliminado(Constantes.ELIMINADO);
	edit(sgPagoServicio);

    }

    
    public boolean agregarArchivoPagoServicio(SgPagoServicio sgPagoServicio, Usuario usuario, SiAdjunto siAdjunto) {
	boolean v;
	try {
	    sgPagoServicio.setSiAdjunto(siAdjunto);
	    edit(sgPagoServicio);
	    v = true;
	} catch (Exception e) {
	    return false;
	}
	return v;
    }

    
    public List<SgPagoServicio> buscarPorTipoEspecifico(SgTipoEspecifico sgTipoEspecifico, boolean noEliminado) {
	try {
	    return em.createQuery("SELECT p FROM SgPagoServicio p WHERE p.sgTipoEspecifico.id = :idTipoEspecifico AND p.eliminado = :eli "
		    + " ORDER BY p.id ASC").setParameter("idTipoEspecifico", sgTipoEspecifico.getId()).setParameter("eli", noEliminado).getResultList();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public Date traerPrimerRegistro() {
	String q = "select min(fecha_genero) from SG_PAGO_SERVICIO";
	return (Date) em.createNativeQuery(q).getSingleResult();
    }
}
