/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.catalogos.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.Paridad;
import sia.servicios.sistema.vo.ParidadVO;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class ParidadImpl{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    public void create(Paridad paridad) {
        em.persist(paridad);
    }

    
    public void edit(Paridad paridad) {
        em.merge(paridad);
    }

    
    public void remove(Paridad paridad) {
        em.remove(em.merge(paridad));
    }

    
    public Paridad find(Object id) {
        return em.find(Paridad.class, id);
    }

    
    public List<Paridad> findAll() {
        return em.createQuery("select object(o) from Paridad as o").getResultList();
    }

    
    public List<ParidadVO> traerParidad(String companiaID, int monedaID, int paridadID) {
        List<ParidadVO> le = null;
        try {
            String sb = " select a.id, a.NOMBRE, a.COMPANIA, a.FECHA_VALIDO, mo.id, mo.NOMBRE, md.id, md.NOMBRE, mo.SIGLAS, md.SIGLAS "
                    + " from PARIDAD a   "
                    + " inner join MONEDA mo on mo.id = a.MONEDA and mo.ELIMINADO = 'False' "
                    + " inner join MONEDA md on md.id = a.MONEDADES   and mo.ELIMINADO = 'False' "
                    + " where a.ELIMINADO = 'False'   "
                    ;
            
            
            if (companiaID != null && !companiaID.isEmpty()) {
                sb += " and a.COMPANIA = '" + companiaID + "' ";
            }

            if (monedaID > 0) {
                sb += " and mo.ID = " + monedaID;
            }

            if (paridadID > 0) {
                sb += " and a.ID = " + paridadID;
            }

            List<Object[]> lo = em.createNativeQuery(sb).getResultList();
            le = new ArrayList<ParidadVO>();
            for (Object[] objects : lo) {
                ParidadVO or = new ParidadVO();
                or.setId((Integer) objects[0]);
                or.setNombre((String) objects[1]);
                or.setCompania((String) objects[2]);
                or.setFechaValido((Date) objects[3]);                
                or.setMonedaOrigen((Integer) objects[4]);
                or.setMonedaOrigenNombre((String) objects[5]);
                or.setMonedaDestino((Integer) objects[6]);
                or.setMonedaDestinoNombre((String) objects[7]);
                or.setMonedaOrigenSiglas((String) objects[8]);
                or.setMonedaDestinoSiglas((String) objects[9]);
                
                le.add(or);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            le = null;
        }
        return le;
    }

    
}
