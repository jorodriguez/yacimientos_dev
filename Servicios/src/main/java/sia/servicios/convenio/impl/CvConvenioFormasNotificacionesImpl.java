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
import sia.modelo.CvConvenioFormas;
import sia.modelo.CvConvenioFormasNotificaciones;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.ContratoFormasNotificacionesVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class CvConvenioFormasNotificacionesImpl extends AbstractFacade<CvConvenioFormasNotificaciones> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CvConvenioFormasNotificacionesImpl() {
        super(CvConvenioFormasNotificaciones.class);
    }

    
    public void guardar(String sesion, String notificoA, int cfId) {
        try {
            CvConvenioFormasNotificaciones cfn = new CvConvenioFormasNotificaciones();
            cfn.setCvConvenioFormas(new CvConvenioFormas(cfId));
            cfn.setNotificoA(notificoA);
            cfn.setGenero(new Usuario(sesion));
            cfn.setFechaGenero(new Date());
            cfn.setHoraGenero(new Date());
            cfn.setEliminado(Constantes.NO_ELIMINADO);
            //
            create(cfn);

        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }

    }

    
    public List<ContratoFormasNotificacionesVo> traerPorForma(int idForma) {
        try {
            String c = "select ccfn.id, ccfn.notifico_a, ccfn.fecha_genero, ccfn.hora_genero  "
                    + " from Cv_Convenio_Formas_Notificaciones ccfn  where ccfn.Cv_Convenio_Formas  = ? "
                    + " and ccfn.eliminado  = false order by ccfn.id desc";
            List<Object[]> objs = em.createNativeQuery(c).setParameter(1, idForma).getResultList();
            List<ContratoFormasNotificacionesVo> nots = new ArrayList<ContratoFormasNotificacionesVo>();
            for (Object[] obj : objs) {
                ContratoFormasNotificacionesVo cnVo = new ContratoFormasNotificacionesVo();
                cnVo.setId((Integer) obj[0]);
                cnVo.setNotificoA((String) obj[1]);
                cnVo.setFechaGenero((Date) obj[2]);
                cnVo.setHoraGenero((Date) obj[3]);
                //
                if (cnVo.getNotificoA().contains(",")) {
                    String[] cad  = cnVo.getNotificoA().split(",");
                    cnVo.setNotificoATodos(cnVo.getNotificoA());
                    cnVo.setNotificoA(cad[0]);
                }                
                nots.add(cnVo);
            }
            return nots;
        } catch (Exception e) {
            System.out.println("Exc: " + e);
            UtilLog4j.log.error(e);
            return null;
        }
    }

}
