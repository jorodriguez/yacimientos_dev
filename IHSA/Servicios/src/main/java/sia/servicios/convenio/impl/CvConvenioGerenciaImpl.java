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
import sia.modelo.CvConvenioGerencia;
import sia.modelo.Gerencia;
import sia.modelo.Usuario;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author ihsa
 */
@Stateless 
public class CvConvenioGerenciaImpl extends AbstractFacade<CvConvenioGerencia> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CvConvenioGerenciaImpl() {
        super(CvConvenioGerencia.class);
    }

    
    public void guardar(String sesion, int convenio, int gerencia) {
        if (validaGuardar(convenio, gerencia)) {
            CvConvenioGerencia cvConvenioGerencia = new CvConvenioGerencia();
            cvConvenioGerencia.setConvenio(new Convenio(convenio));
            cvConvenioGerencia.setGerencia(new Gerencia(gerencia));
            cvConvenioGerencia.setGenero(new Usuario(sesion));
            cvConvenioGerencia.setFechaGenero(new Date());
            cvConvenioGerencia.setHoraGenero(new Date());
            cvConvenioGerencia.setEliminado(Constantes.NO_ELIMINADO);
            //
            create(cvConvenioGerencia);
        }

    }

    
    public List<GerenciaVo> convenioPorGerenica(int idConvenio) {
        String sb = "select cg.id, g.id, g.nombre from cv_convenio_gerencia cg ";
        sb += "	inner join gerencia g on cg.gerencia = g.id ";
        sb += "    where cg.convenio = " + idConvenio;
        sb += "    and cg.eliminado = 'False'";
        //
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        //
        List<GerenciaVo> lg = null;
        if (lo != null) {
            lg = new ArrayList<GerenciaVo>();
            for (Object[] lo1 : lo) {
                GerenciaVo gerenciaVo = new GerenciaVo();
                gerenciaVo.setIdTabla((Integer) lo1[0]);
                gerenciaVo.setId((Integer) lo1[1]);
                gerenciaVo.setNombre((String) lo1[2]);
                lg.add(gerenciaVo);
            }
        }
        return lg;
    }

    
    public boolean validaGuardar(int convenio, int gerencia) {
        try {
            String sb = "select * from cv_convenio_gerencia cg";
            sb += "	where cg.convenio = " + convenio;
            sb += "	and cg.gerencia = " + gerencia;
            sb += "	and cg.eliminado = 'False' ";
            //
            Object[] obj = (Object[]) em.createNativeQuery(sb).getSingleResult();
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    
    public void eliminar(String sesion, int idTabla) {
        CvConvenioGerencia cvConvenioGerencia = find(idTabla);
        cvConvenioGerencia.setEliminado(Constantes.ELIMINADO);
        cvConvenioGerencia.setModifico(new Usuario(sesion));
        cvConvenioGerencia.setFechaModifico(new Date());
        cvConvenioGerencia.setHoraModifico(new Date());
        //
        edit(cvConvenioGerencia);
    }

    
    public List<CvConvenioGerencia> gerenciasPorConvenio(int idConvenio) {
        String sb = "select cg from CvConvenioGerencia cg "
                + "    where cg.convenio.id = :conv"
                + "    and cg.eliminado = false";
        //
        return  em.createQuery(sb).setParameter("conv", idConvenio).getResultList();
        //
    }
}
