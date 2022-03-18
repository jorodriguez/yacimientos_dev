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
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.CvConvenioExhorto;
import sia.modelo.CvConvenioFormas;
import sia.modelo.CvConvenioFormasNotas;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.ContratoFormasNotasVo;
import sia.modelo.contrato.vo.ContratoFormasVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.notificaciones.convenio.impl.NotificacionConvenioImpl;

/**
 *
 * @author mluis
 */
@Stateless 
public class CvConvenioFormasNotasImpl extends AbstractFacade<CvConvenioFormasNotas> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CvConvenioFormasNotasImpl() {
        super(CvConvenioFormasNotas.class);
    }
    @Inject
    NotificacionConvenioImpl notificacionConvenioLocal;
    @Inject
    CvConvenioExhortoImpl convenioExhortoLocal;

    
    public void guardar(UsuarioVO sesion, ContratoFormasNotasVo contratoFormasNotasVo, ContratoFormasVo contratoFormasVo) {
        CvConvenioFormasNotas cfn = new CvConvenioFormasNotas();
        cfn.setCvConvenioFormas(new CvConvenioFormas(contratoFormasVo.getId()));
        cfn.setUsuario(new Usuario(sesion.getId()));
        cfn.setObservacion(contratoFormasNotasVo.getObservacion());
        cfn.setGenero(new Usuario(sesion.getId()));
        cfn.setFechaGenero(new Date());
        cfn.setHoraGenero(new Date());
        cfn.setEliminado(Constantes.NO_ELIMINADO);
        //
        create(cfn);
        //Notificar
        CvConvenioExhorto exhorto = convenioExhortoLocal.buscarUltimoExhorto(contratoFormasVo.getIdConvenio());
        if (exhorto != null) {
            notificacionConvenioLocal.notificacionObservacionForma(exhorto.getCorreoPara(), sesion.getMail(),
                    "Proceso de finiquito", contratoFormasVo, contratoFormasNotasVo);
        }

    }

    
    public List<ContratoFormasNotasVo> traerNotasPorContratoForma(int contratoFormaId) {
        String c = " select  ccfn.id, ccfn.cv_convenio_formas , u.id, u.nombre, ccfn.observacion, ccfn.fecha_genero, ccfn.hora_genero from cv_convenio_formas_notas ccfn \n"
                + "	inner join usuario u  on ccfn.usuario  = u.id \n"
                + " where ccfn.cv_convenio_formas   = " + contratoFormaId
                + " and ccfn.eliminado  = false";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<ContratoFormasNotasVo> notas = new ArrayList<ContratoFormasNotasVo>();
        for (Object[] obj : objs) {
            notas.add(cast(obj));
        }
        return notas;
    }

    private ContratoFormasNotasVo cast(Object[] obj) {
        ContratoFormasNotasVo cfn = new ContratoFormasNotasVo();
        cfn.setId((Integer) obj[0]);
        cfn.setIdConvenioFormas((Integer) obj[1]);
        cfn.setIdUsuario((String) obj[2]);
        cfn.setUsuario((String) obj[3]);
        cfn.setObservacion((String) obj[4]);
        cfn.setFechaNota((Date) obj[5]);
        cfn.setHoraNota((Date) obj[6]);
        return cfn;
    }

}
