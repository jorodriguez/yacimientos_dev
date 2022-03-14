/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.orden.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.OcCompaniaAcumulado;
import sia.modelo.orden.vo.CompaniaAcumuladoVo;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class OcCompaniaAcumuladoImpl extends AbstractFacade<OcCompaniaAcumulado> {
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcCompaniaAcumuladoImpl() {
        super(OcCompaniaAcumulado.class);
    }
    
    
    public List<CompaniaAcumuladoVo> traerMonto(){
        clearQuery();
        List<CompaniaAcumuladoVo> lc = new ArrayList<CompaniaAcumuladoVo>();                
        query.append("select ca.id, ca.compania, ca.monto_dolar, ca.monto_pesos, ca.verifica_monto from Oc_Compania_Acumulado ca where ca.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        for (Object[] objects : lo) {
            lc.add(castCompaniaAcumulado(objects));
        }
        return lc;
    }
    
     
    public CompaniaAcumuladoVo montoAcumuladoPorEmpresa(String rfcEmpresa){
        clearQuery();
        query.append("select ca.id, ca.compania, ca.monto_dolar, ca.monto_pesos, ca.verifica_monto from Oc_Compania_Acumulado ca where ca.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" and ca.compania  = '").append(rfcEmpresa).append("'");
        Object[] objects = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
        //
        return castCompaniaAcumulado(objects);
    }

    private CompaniaAcumuladoVo castCompaniaAcumulado(Object[] objects) {
        CompaniaAcumuladoVo c = new CompaniaAcumuladoVo();
        c.setId((Integer)objects[0]);
        c.setCompania((String)objects[1]);
        c.setMontoDolar((Double)objects[2]);
        c.setMontoPesos((Double)objects[3]);
        c.setVerificaMonto((Boolean)objects[4]);
        return c;
        
    }
}
