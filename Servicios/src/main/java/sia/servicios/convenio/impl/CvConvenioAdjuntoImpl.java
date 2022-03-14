/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.convenio.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Convenio;
import sia.modelo.CvConvenioAdjunto;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiAdjuntoImpl;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class CvConvenioAdjuntoImpl extends AbstractFacade<CvConvenioAdjunto>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @Inject
    private SiAdjuntoImpl siAdjuntoRemote;

    public CvConvenioAdjuntoImpl() {
        super(CvConvenioAdjunto.class);
    }

    
    public List<ContratoVO> traerPorConvenioPorNumero(String numero) {
        List<ContratoVO> lista = null;
        String sb = "select c.CODIGO, c.ID, c.NOMBRE, c.FECHA_VENCIMIENTO, a.ID, a.NOMBRE,  a.UUID, tc.nombre  from CV_CONVENIO_ADJUNTO ca ";
        sb += "     inner join convenio c on ca.convenio = c.ID";
        sb += "     inner join SI_ADJUNTO a on ca.SI_ADJUNTO = a.ID";
        sb += "     inner join cv_tipo tc on c.cv_tipo = tc.ID";
        sb += "   where c.codigo = '" + numero + "'";
        sb += "   and ca.ELIMINADO = false ";
        sb += "   and a.ELIMINADO = false ";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        if (lo != null) {
            lista = new ArrayList<>();
            for (Object[] obj : lo) {
                lista.add(castConvenioOrdenVo(obj));
            }
        }
        return lista;
    }

    private ContratoVO castConvenioOrdenVo(Object[] objects) {
        ContratoVO contratoVO = new ContratoVO();
        contratoVO.setNumero((String) objects[0]);
        contratoVO.setId((Integer) objects[1]);
        contratoVO.setNombre((String) objects[2]);
        contratoVO.setFechaVencimiento((Date) objects[3]);
        contratoVO.getAdjuntoVO().setId((Integer) objects[4]);
        contratoVO.getAdjuntoVO().setNombre((String) objects[5]);
        contratoVO.getAdjuntoVO().setUuid((String) objects[6]);
        contratoVO.setTipo((String) objects[7]);
        return contratoVO;
    }

    
    public List<AdjuntoVO> traerPorConvenio(int idConvenio) {
        List<AdjuntoVO> lista = null;
        String sb = "select ca.ID, a.ID, a.NOMBRE, a.UUID from CV_CONVENIO_ADJUNTO ca ";
        sb += "	    inner join SI_ADJUNTO a on ca.SI_ADJUNTO = a.ID";
        sb += "	  where ca.CONVENIO = " + idConvenio;
        sb += "	  and ca.ELIMINADO = '" + Constantes.NO_ELIMINADO + "'";
        sb += "	  and a.ELIMINADO = '" + Constantes.NO_ELIMINADO + "'";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        if (lo != null) {
            lista = new ArrayList<>();
            for (Object[] obj : lo) {
                AdjuntoVO adjuntoVO = new AdjuntoVO();
                adjuntoVO.setIdTabla((Integer) obj[0]);
                adjuntoVO.setId((Integer) obj[1]);
                adjuntoVO.setNombre((String) obj[2]);
                adjuntoVO.setUuid((String) obj[3]);
                lista.add(adjuntoVO);
            }
        }
        return lista;
    }

    
    public void guardar(String sesion, int contratoVO, int adjunto) {
        CvConvenioAdjunto cvConvenioAdjunto = new CvConvenioAdjunto();
        cvConvenioAdjunto.setConvenio(new Convenio(contratoVO));
        cvConvenioAdjunto.setSiAdjunto(new SiAdjunto(adjunto));
        cvConvenioAdjunto.setGenero(new Usuario(sesion));
        cvConvenioAdjunto.setFechaGenero(new Date());
        cvConvenioAdjunto.setHoraGenero(new Date());
        cvConvenioAdjunto.setEliminado(Constantes.NO_ELIMINADO);
        create(cvConvenioAdjunto);
    }

    
    public void eliminar(String sesion, int id) {
        try {
            CvConvenioAdjunto cvConvenioAdjunto = find(id);
            cvConvenioAdjunto.setModifico(new Usuario(sesion));
            cvConvenioAdjunto.setFechaModifico(new Date());
            cvConvenioAdjunto.setHoraModifico(new Date());
            cvConvenioAdjunto.setEliminado(Constantes.ELIMINADO);
            edit(cvConvenioAdjunto);
            //
            //
            siAdjuntoRemote.eliminarArchivo(cvConvenioAdjunto.getSiAdjunto().getId(), sesion);
        } catch (Exception ex) {
            Logger.getLogger(CvConvenioAdjuntoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
