/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.fecha.asueto.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jooq.DSLContext;
import sia.constantes.Constantes;
import sia.fechas.asueto.SiDiasAsueto;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class SiDiasAsuetoImpl extends AbstractFacade<SiDiasAsueto> {
    
    @Inject
    DSLContext dbCtx;

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiDiasAsuetoImpl() {
        super(SiDiasAsueto.class);
    }

    
    public boolean buscarByFechaAlDia() {
        boolean b = true;
        clearQuery();
        query.append("SELECT s.id FROM SI_DIAS_ASUETO s WHERE s.FECHA = ");
        query.append("cast('now' as date)");
        try {
            em.createNativeQuery(query.toString()).getSingleResult();
        } catch (Exception nre) {
            b = false;
            UtilLog4j.log.info(this, "No se encontró ninguna opción: " + nre.getMessage());
        }

        return b;

    }
    
    
    public List <SiDiasAsueto> diasAsuetoByYear(int year){
        
        List<SiDiasAsueto> ld;
        String sql = "SELECT * from si_dias_asueto where fecha > '"+(year-1)+"-12-31'";
        
        ld = dbCtx.fetch(sql).into(SiDiasAsueto.class);
        return ld;
    }
    
    
    public boolean  esDiaFestivo(Date dia){
        
        List<SiDiasAsueto> ld;
        String sql = "SELECT s.id FROM SI_DIAS_ASUETO s WHERE s.FECHA = '"+Constantes.FMT_yyyy_MM_dd.format(dia)+"'";
        
        ld = dbCtx.fetch(sql).into(SiDiasAsueto.class);
        
        if(ld != null && !ld.isEmpty()){
            return true;
        } else {
            return false;
        }
        
    }

}
