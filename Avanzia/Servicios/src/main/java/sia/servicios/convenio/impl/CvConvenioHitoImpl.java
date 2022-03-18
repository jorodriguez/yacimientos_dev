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
import sia.modelo.CvConvenioHito;
import sia.modelo.CvHito;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.vo.CatalogoContratoVo;

/**
 *
 * @author ihsa
 */
@Stateless 
public class CvConvenioHitoImpl extends AbstractFacade<CvConvenioHito> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public CvConvenioHitoImpl() {
	super(CvConvenioHito.class);
    }

    
    public void guardar(String sesion, List<CatalogoContratoVo> listaCondiciones, int idConvenio) {
	for (CatalogoContratoVo listaCondicione : listaCondiciones) {
	    guardar(sesion, idConvenio, listaCondicione.getId(), listaCondicione.getIdRelacion());
	}
    }

    
    public void guardar(String sesion, int idConvenio, int idHito, int idCondicionPago) {
	CvConvenioHito cvConvenioHito = new CvConvenioHito();
	cvConvenioHito.setConvenio(new Convenio(idConvenio));
	cvConvenioHito.setCvHito(new CvHito(idHito));
	cvConvenioHito.setCvCondicionPago(new CvCondicionPago(idCondicionPago));
	cvConvenioHito.setGenero(new Usuario(sesion));
	cvConvenioHito.setFechaGenero(new Date());
	cvConvenioHito.setHoraGenero(new Date());
	cvConvenioHito.setEliminado(Constantes.NO_ELIMINADO);
	create(cvConvenioHito);
    }

    
    public void eliminar(String sesion, int idConvHito) {
	CvConvenioHito cvConvenioHito = find(idConvHito);
	cvConvenioHito.setGenero(new Usuario(sesion));
	cvConvenioHito.setFechaGenero(new Date());
	cvConvenioHito.setHoraGenero(new Date());
	cvConvenioHito.setEliminado(Constantes.ELIMINADO);
	edit(cvConvenioHito);
    }

    
    public List<CatalogoContratoVo> traerHitosPorConvenio(int idConvenio) {
	List<CatalogoContratoVo> lista = null;
	String sb = "select ch.ID, h.NOMBRE, h.id, cp.id, cp.nombre from CV_CONVENIO_HITO ch";
	sb += "	    inner join CV_HITO h on ch.CV_hito =  h.ID";
	sb += "	    left join CV_CONDICION_PAGO cp on ch.CV_CONDICION_PAGO =  cp.ID";
	sb += "	 where ch.convenio = " + idConvenio;
	sb += "	 and ch.ELIMINADO = '" + Constantes.NO_ELIMINADO + "'";
	List<Object[]> lo = em.createNativeQuery(sb).getResultList();
	if (lo != null) {
	    lista = new ArrayList<CatalogoContratoVo>();
	    for (Object[] lo1 : lo) {
		CatalogoContratoVo vo = new CatalogoContratoVo();
		vo.setId((Integer) lo1[0]);
		vo.setNombre((String) lo1[1]);
		vo.setIdTabla((Integer) lo1[2]);
		vo.setIdRelacion(lo1[3] != null ? (Integer) lo1[3] : 0);
		vo.setNombreRelacion((String) lo1[4]);
		lista.add(vo);
	    }
	}
	return lista;
    }
}
