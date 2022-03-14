/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.convenio.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Convenio;
import sia.modelo.CvConvenioUsuario;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class CvConvenioUsuarioImpl extends AbstractFacade<CvConvenioUsuario> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public CvConvenioUsuarioImpl() {
	super(CvConvenioUsuario.class);
    }

    
    public List<ContratoVO> traerContratoPorUsuario(String idUsuario) {
	String sb = "select cu.id, c.id, c.codigo, c.nombre, c.fecha_inicio, c.fecha_vencimiento from cv_convenio_usuario cu ";
	sb += "	inner join convenio c on cu.convenio = c.id ";
	sb += "	where cu.usuario = '" + idUsuario + "'";
	sb += "	and cu.eliminado  = 'False'";
	List<Object[]> lo = em.createNativeQuery(sb).getResultList();
	//
	List<ContratoVO> lc = new ArrayList<ContratoVO>();
	for (Object[] lo1 : lo) {
	    ContratoVO contratoVO = new ContratoVO();
	    contratoVO.setIdRelacion((Integer) lo1[0]);
	    contratoVO.setId((Integer) lo1[1]);
	    contratoVO.setNumero((String) lo1[2]);
	    contratoVO.setNombre((String) lo1[3]);
	    contratoVO.setFechaInicio(lo1[4] != null ? (Date) lo1[4] : null);
	    contratoVO.setFechaVencimiento(lo1[5] != null ? (Date) lo1[5] : null);
	    lc.add(contratoVO);
	}
	return lc;
    }

    private Object traerContratoPorUsuarioConvenio(String idUsuario, int idConvenio) {
	String sb = "select * from cv_convenio_usuario cu ";
	sb += "	where cu.usuario = '" + idUsuario + "'";
	sb += "	and  cu.convenio = " + idConvenio;
	sb += "	and cu.eliminado  = 'False'";
	try {
	    return (Object[]) em.createNativeQuery(sb).getSingleResult();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	    return null;
	}
    }

    
    public void guardar(String idUsuario, List<ContratoVO> listaContratosAgregar, String sesion) {
	for (ContratoVO cvo : listaContratosAgregar) {
	    guardar(idUsuario, cvo.getId(), sesion);
	}
    }

    
    public void guardar(String idUsuario, int idConvenio, String sesion) {
	if (traerContratoPorUsuarioConvenio(idUsuario, idConvenio) == null) {
	    CvConvenioUsuario cvConvenioUsuario = new CvConvenioUsuario();
	    cvConvenioUsuario.setConvenio(new Convenio(idConvenio));
	    cvConvenioUsuario.setUsuario(new Usuario(idUsuario));
	    cvConvenioUsuario.setGenero(new Usuario(sesion));
	    cvConvenioUsuario.setFechaGenero(new Date());
	    cvConvenioUsuario.setHoraGenero(new Date());
	    cvConvenioUsuario.setEliminado(Constantes.NO_ELIMINADO);
	    //
	    create(cvConvenioUsuario);
	}
    }

    
    public void eliminar(int idRelacion, String sesion) {
	CvConvenioUsuario cvConvenioUsuario = find(idRelacion);
	cvConvenioUsuario.setModifico(new Usuario(sesion));
	cvConvenioUsuario.setFechaModifico(new Date());
	cvConvenioUsuario.setHoraModifico(new Date());
	cvConvenioUsuario.setEliminado(Constantes.ELIMINADO);
	//
	edit(cvConvenioUsuario);
    }
}
