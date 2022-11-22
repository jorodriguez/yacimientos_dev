/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.convenio.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Convenio;
import sia.modelo.CvCondicionPago;
import sia.modelo.CvConvenioCondicionPago;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.vo.CatalogoContratoVo;

/**
 *
 * @author ihsa
 */
@Stateless 
public class CvConvenioCondicionPagoImpl extends AbstractFacade<CvConvenioCondicionPago>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public CvConvenioCondicionPagoImpl() {
	super(CvConvenioCondicionPago.class);
    }

    
    public void guardar(String sesion, List<CatalogoContratoVo> listaCondiciones, int idConvenio) {
	for (CatalogoContratoVo listaCondicione : listaCondiciones) {
	    guardar(sesion, idConvenio, listaCondicione.getId());
	}
    }

    
    public void guardar(String sesion, int idConvenio, int condicionPago) {
	CvConvenioCondicionPago cvCondicionPago = new CvConvenioCondicionPago();
	cvCondicionPago.setConvenio(new Convenio(idConvenio));
	cvCondicionPago.setCvCondicionPago(new CvCondicionPago(condicionPago));
	cvCondicionPago.setGenero(new Usuario(sesion));
	cvCondicionPago.setFechaGenero(new Date());
	cvCondicionPago.setHoraGenero(new Date());
	cvCondicionPago.setEliminado(Constantes.NO_ELIMINADO);
	create(cvCondicionPago);
    }

    
    public void eliminar(String sesion, int idConvenioCondicionPago) {
	CvConvenioCondicionPago cvCondicionPago = find(idConvenioCondicionPago);
	cvCondicionPago.setGenero(new Usuario(sesion));
	cvCondicionPago.setFechaGenero(new Date());
	cvCondicionPago.setHoraGenero(new Date());
	cvCondicionPago.setEliminado(Constantes.ELIMINADO);
	edit(cvCondicionPago);
    }

    
    public List<CatalogoContratoVo> traerCondicionesPago(int idConvenio) {
	List<CatalogoContratoVo> lista = null;
	String sb = "select ccp.ID, cp.NOMBRE, cp.id from CV_CONVENIO_CONDICION_PAGO ccp";
	sb += "	    inner join CV_CONDICION_PAGO cp on ccp.CV_CONDICION_PAGO =  cp.ID";
	sb += "	 where ccp.convenio = " + idConvenio;
	sb += "	 and ccp.ELIMINADO = '" + Constantes.NO_ELIMINADO + "'";
	List<Object[]> lo = em.createNativeQuery(sb).getResultList();
	if (lo != null) {
	    lista = new ArrayList<CatalogoContratoVo>();
	    for (Object[] lo1 : lo) {
		CatalogoContratoVo vo = new CatalogoContratoVo();
		vo.setId((Integer) lo1[0]);
		vo.setNombre((String) lo1[1]);
		vo.setIdTabla((Integer) lo1[2]);
		lista.add(vo);
	    }
	}
	return lista;
    }

}
