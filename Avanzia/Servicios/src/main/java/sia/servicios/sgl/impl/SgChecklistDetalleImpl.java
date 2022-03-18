/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgChecklist;
import sia.modelo.SgChecklistDetalle;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.CheckListDetalleVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgChecklistDetalleImpl extends AbstractFacade<SgChecklistDetalle>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    //    
    @Inject
    private SgCaracteristicaImpl sgCaracteristicaRemote;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgChecklistDetalleImpl() {
	super(SgChecklistDetalle.class);
    }

    
    public SgChecklistDetalle createChecklistDetalle(SgChecklist checklist, int caracteristica, boolean estado, String observacion, String idUsuario) {
	UtilLog4j.log.info(this, "SgChecklistDetalleImpl.createChecklistDetalle()");

	SgChecklistDetalle item = new SgChecklistDetalle();
	item.setSgChecklist(checklist);
	item.setSgCaracteristica(sgCaracteristicaRemote.find(caracteristica));
	item.setEstado(estado);
	item.setObservacion(observacion);
	item.setFechaGenero(new Date());
	item.setHoraGenero(new Date());
	item.setGenero(new Usuario(idUsuario));
	item.setEliminado(Constantes.NO_ELIMINADO);

	try {
	    super.create(item);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Hubo un error al guardar el ítem (detalle) del Checklist");
	    UtilLog4j.log.fatal(this, e.getMessage());
	    e.printStackTrace();
	    return null;
	}
	return item;
    }

    
    public Map<String, SgChecklistDetalle> getAllItemsChecklistMap(SgChecklist checklist, boolean status) {
	UtilLog4j.log.info(this, "SgChecklistDetalleImpl.getAllItemsChecklistMap()");

	List<SgChecklistDetalle> itemsChecklistList = null;
	Map<String, SgChecklistDetalle> itemsCheckListMap = new TreeMap<String, SgChecklistDetalle>();

	try {
	    itemsChecklistList = em.createQuery("SELECT chk FROM SgChecklistDetalle chk WHERE chk.eliminado = :estado AND chk.sgChecklist.id = :idChecklist")
		    .setParameter("estado", status)
		    .setParameter("idChecklist", checklist.getId())
		    .getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    UtilLog4j.log.fatal(this, "Hubo un error al obtener los ítems del Checklist: " + checklist.getId());
	    return null;
	}

	if (itemsChecklistList != null) {//Convertir a Map
	    for (SgChecklistDetalle chkd : itemsChecklistList) {
		itemsCheckListMap.put(chkd.getSgCaracteristica().getNombre(), chkd);
	    }
	    UtilLog4j.log.info(this, "Se encontraron " + itemsChecklistList.size() + "ítems para el Checklist: " + checklist.getId());
	    return itemsCheckListMap;
	} else {
	    UtilLog4j.log.info(this, "No se encontraron ítems para el Checklist: " + checklist.getId());
	    return null;
	}
    }

    
    public List<CheckListDetalleVo> getAllItemsChecklistList(int checklist, boolean status) {
	UtilLog4j.log.info(this, "SgChecklistDetalleImpl.getAllItemsChecklistList()");

	try {
	    if (checklist > 0) {
		String q = " select cd.ID, c.ID, c.FECHA_INICIO_SEMANA, c.FECHA_FIN_SEMANA, cd.OBSERVACION, ca.ID, ca.NOMBRE, ca.PRINCIPAL, "
			+ "  t.ID, t.NOMBRE, cd.estado from SG_CHECKLIST_DETALLE cd"
			+ "	inner join SG_CHECKLIST c on cd.SG_CHECKLIST = c.ID"
			+ "	inner join SG_CARACTERISTICA ca on cd.SG_CARACTERISTICA = ca.ID"
			+ "	inner join SG_TIPO t on ca.SG_TIPO = t.ID"
			+ "	where c.ID = ?"
			+ " and cd.ELIMINADO = 'False'";
		List<Object[]> lo = em.createNativeQuery(q).setParameter(1, checklist).getResultList();
		List<CheckListDetalleVo> lch = new ArrayList<CheckListDetalleVo>();
		for (Object[] obj : lo) {
		    CheckListDetalleVo cldv = new CheckListDetalleVo();
		    cldv.setId((Integer) obj[0]);
		    cldv.getChecklistVO().setIdChecklist((Integer) obj[1]);
		    cldv.getChecklistVO().setInicoSemana((Date) obj[2]);
		    cldv.getChecklistVO().setFinSemana((Date) obj[3]);
		    cldv.setObservacion((String) obj[4]);
		    cldv.getCaracteristicaVo().setId((Integer) obj[5]);
		    cldv.getCaracteristicaVo().setNombre((String) obj[6]);
		    cldv.getCaracteristicaVo().setPrincipal((Boolean) obj[7]);
		    cldv.getCaracteristicaVo().setIdTipo((Integer) obj[8]);
		    cldv.getCaracteristicaVo().setTipo((String) obj[9]);
		    cldv.setEstado((Boolean) obj[10]);
		    lch.add(cldv);
		}

		return lch;
	    } else {
		UtilLog4j.log.info(this, "Faltan datos para hacer la consulta de los ítems de Checklist");
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    e.getStackTrace();
	    return null;
	}
	return null;

    }

    
    public boolean editChecklistDetalle(CheckListDetalleVo itemC, String idUsuario, boolean status) {
	UtilLog4j.log.info(this, "SgChecklistDetalleImpl.editChecklistDetalle()");
	boolean editSuccessfull = true;

	SgChecklistDetalle item = find(itemC.getId());
	item.setObservacion(itemC.getObservacion());
	item.setEstado(itemC.isEstado());
	item.setModifico(new Usuario(idUsuario));
	item.setFechaModifico(new Date());
	item.setHoraModifico(new Date());
	item.setEliminado(status);

	try {
	    super.edit(item);
	    return editSuccessfull;
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Hubo un error al actualizar el ítem (detalle): " + item.getId() + " del Checklist");
	    UtilLog4j.log.info(this, e.getMessage());
	    return !editSuccessfull;
	}
    }

    
    public boolean deleteItemChecklist(int idChekDet, String idUsuario) {
	UtilLog4j.log.info(this, "SgChecklistDetalleImpl.deleteItemChecklist()");

	boolean deleteSuccessfull = true;
	SgChecklistDetalle item = find(idChekDet);
	item.setGenero(new Usuario(idUsuario));
	item.setFechaGenero(new Date());
	item.setHoraGenero(new Date());
	item.setEliminado(Constantes.ELIMINADO);

	try {
	    super.edit(item);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Ocurrió un error al eliminar el ítem: " + item.getId());
	    UtilLog4j.log.fatal(this, e.getMessage());
	    return !deleteSuccessfull;
	}
	return deleteSuccessfull;
    }

}
