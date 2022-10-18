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
import sia.excepciones.SIAException;
import sia.modelo.SgChecklist;
import sia.modelo.SgKilometraje;
import sia.modelo.SgVehiculo;
import sia.modelo.SgVehiculoChecklist;
import sia.modelo.Usuario;
import sia.modelo.sgl.vehiculo.vo.SgKilometrajeVo;
import sia.modelo.sgl.vehiculo.vo.VehiculoCheckListVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Stateless 
public class SgVehiculoChecklistImpl extends AbstractFacade<SgVehiculoChecklist> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private UsuarioImpl usuarioService;
    @Inject
    private SgKilometrajeImpl kilometrajeService;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgVehiculoChecklistImpl() {
	super(SgVehiculoChecklist.class);
    }

    
    public List<SgVehiculoChecklist> getAllChecklistByVehiculoList(int sgVehiculo, boolean eliminado) throws SIAException, Exception {

	List<SgVehiculoChecklist> checklistVehiculo = null;

	checklistVehiculo = em.createQuery("SELECT vc FROM SgVehiculoChecklist vc"
		+ " WHERE vc.sgVehiculo.id = :v "
		+ " AND vc.eliminado = :eli ORDER BY vc.id DESC").setParameter("v", sgVehiculo).setParameter("eli", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO)).setMaxResults(3).getResultList();

	return checklistVehiculo;
    }

    
    public SgVehiculoChecklist create(int vehiculo, SgChecklist checklist, SgKilometraje kilometraje, int idTipoEspecifico, String idUsuario) throws SIAException, Exception {
	SgVehiculoChecklist vehiculoChecklist = null;

	if (checklist != null) {
	    kilometraje = kilometrajeService.createKilometrajeActual(vehiculo, idTipoEspecifico, kilometraje.getKilometraje(), usuarioService.find(idUsuario));

	    vehiculoChecklist = new SgVehiculoChecklist();
	    vehiculoChecklist.setSgVehiculo(new SgVehiculo(vehiculo));
	    vehiculoChecklist.setSgChecklist(checklist);
	    vehiculoChecklist.setFechaGenero(new Date());
	    vehiculoChecklist.setHoraGenero(new Date());
	    vehiculoChecklist.setGenero(new Usuario(idUsuario));
	    vehiculoChecklist.setEliminado(Constantes.NO_ELIMINADO);
	    vehiculoChecklist.setSgKilometraje(kilometraje);

	    super.create(vehiculoChecklist);
	    return vehiculoChecklist;
	} else {
	    throw new SIAException(SgVehiculoChecklistImpl.class.getName(), "create()", (" Faltan datos para poder guardar el Checklist"), (" vehiculo: " + vehiculo
		    + (" checklist: " + (checklist != null ? checklist.getId() : null))));
	}
    }

    
    public SgVehiculoChecklist buscarUltimoChecklist(int sgVehiculo) {
	SgVehiculoChecklist sgVehiculoChecklist;
	try {
	    List<SgVehiculoChecklist> list = em.createQuery("SELECT vc FROM SgVehiculoChecklist vc"
		    + " WHERE vc.sgVehiculo.id = :v "
		    + " AND vc.eliminado = :eli ORDER BY vc.id DESC").setParameter("v", sgVehiculo).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
	    if (!list.isEmpty()) {
		sgVehiculoChecklist = list.get(0);
		//
	    } else {
		sgVehiculoChecklist = null;
	    }
	} catch (Exception e) {
	    // 
            UtilLog4j.log.error(e);
	    return null;
	}
	return sgVehiculoChecklist;
    }

    
    public SgVehiculoChecklist buscarPorChecklist(SgChecklist sgChecklist) {
	try {
	    return (SgVehiculoChecklist) em.createQuery("SELECT vc FROM SgVehiculoChecklist vc"
		    + " WHERE vc.sgChecklist.id = :c "
		    + " AND vc.eliminado = :eli ").setParameter("c", sgChecklist.getId()).setParameter("eli", Constantes.NO_ELIMINADO).getSingleResult();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public int totalCheckList(int idVehiculo) {
	clearQuery();
	query.append("select count(vc.ID) from SG_VEHICULO_CHECKLIST vc ");
	query.append("  where vc.SG_VEHICULO = ").append(idVehiculo);
	query.append("  and vc.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
    }

    /**
     *
     * @param sgVehiculo
     * @return
     */
    
    public VehiculoCheckListVo buscarUltimoCheck(int sgVehiculo) {
	try {
	    String q = "select first 1 vc.id, c.FECHA_GENERO, c.FECHA_INICIO_SEMANA, c.FECHA_FIN_SEMANA, "
		    + "	k.id, k.KILOMETRAJE, k.OBSERVACION"
		    + "	from SG_VEHICULO_CHECKLIST vc "
		    + "	    inner join SG_CHECKLIST c on vc.SG_CHECKLIST = c.ID"
		    + "	    inner join SG_KILOMETRAJE k on vc.SG_KILOMETRAJE =  k.ID"
		    + "	    where vc.SG_VEHICULO =  ?"
		    + "	    and vc.ELIMINADO = 'False'"
		    + "	    order by vc.ID desc";
	    Object[] obj = (Object[]) em.createNativeQuery(q).setParameter(1, sgVehiculo).getSingleResult();
	    VehiculoCheckListVo vehiculoCheckListVo = new VehiculoCheckListVo();
	    vehiculoCheckListVo.setSgKilometrajeVo(new SgKilometrajeVo());
	    vehiculoCheckListVo.setId((Integer) obj[0]);
	    vehiculoCheckListVo.setFechaGenero((Date) obj[1]);
	    vehiculoCheckListVo.setInicioSemana((Date) obj[2]);
	    vehiculoCheckListVo.setFinSemana((Date) obj[3]);
	    vehiculoCheckListVo.getSgKilometrajeVo().setId((Integer) obj[4]);
	    vehiculoCheckListVo.getSgKilometrajeVo().setKilometrajeActual((Integer) obj[4]);
	    vehiculoCheckListVo.getSgKilometrajeVo().setObservacion((String) obj[4]);
	    return vehiculoCheckListVo;
	} catch (Exception e) {
	    UtilLog4j.log.error(e);
	    return null;
	}
    }
}
