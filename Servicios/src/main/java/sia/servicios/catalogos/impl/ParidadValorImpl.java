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
import sia.modelo.ParidadValor;
import sia.servicios.sistema.vo.ParidadValorVO;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class ParidadValorImpl{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    public void create(ParidadValor paridadValor) {
        em.persist(paridadValor);
    }

    
    public void edit(ParidadValor paridadValor) {
        em.merge(paridadValor);
    }

    
    public void remove(ParidadValor paridadValor) {
        em.remove(em.merge(paridadValor));
    }

    
    public ParidadValor find(Object id) {
        return em.find(ParidadValor.class, id);
    }

    
    public List<ParidadValor> findAll() {
        return em.createQuery("select object(o) from ParidadValor as o").getResultList();
    }

    
    public List<ParidadValorVO> traerParidadValor(int paridadID, String fechaInicio, String fechaFin, int paridadValorID) {
        List<ParidadValorVO> le = null;
        try {
            String sb = " select a.ID, a.FECHA_VALIDO, a.VALOR, a.PARIDAD, CAST(EXTRACT(DAY FROM a.FECHA_VALIDO) AS INTEGER) "
                    + " from PARIDAD_VALOR a "
                    + " where a.ELIMINADO = 'False' ";
            
            if(paridadValorID > 0){
                    sb += " and a.ID = "+paridadValorID;
            }
            
            if(paridadID > 0){
                    sb += " and a.PARIDAD = "+paridadID;
            }
            
            if(fechaInicio != null && !fechaInicio.isEmpty() && fechaFin != null && !fechaFin.isEmpty()){
                    sb += " and a.FECHA_VALIDO >= '"+fechaInicio+"' and a.FECHA_VALIDO <= '"+fechaFin+"' ";
            }
                    sb += " order by a.FECHA_VALIDO ";            
            
            List<Object[]> lo = em.createNativeQuery(sb).getResultList();
            le = new ArrayList<ParidadValorVO>();
            for (Object[] objects : lo) {
                ParidadValorVO or = new ParidadValorVO();
                or.setId((Integer) objects[0]);
                or.setFechaValido((Date) objects[1]);
                or.setValor((Double) objects[2]);
                or.setIdParidad((Integer) objects[3]);
                or.setDia((Integer) objects[4]);
                
                le.add(or);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            le = null;
        }
        return le;
    }
    
    
    public ParidadValorVO traerParidadValor(String siglas, String fecha, int monedaID) {
        ParidadValorVO or = null;
        try {

            String sb = " select pv.ID, pv.VALOR "
                    + " from PARIDAD_VALOR pv "
                    + " inner join PARIDAD p on p.id = pv.PARIDAD and p.ELIMINADO = 'False' "
                    + " inner join MONEDA m on m.id = p.MONEDA and m.ELIMINADO = 'False' "
                    + " inner join MONEDA mm on mm.id = p.MONEDADES and m.ELIMINADO = 'False' "
                    + " where pv.ELIMINADO = 'False' ";
            
            if(siglas != null && !siglas.isEmpty()){
                    sb += " and m.SIGLAS = '"+siglas+"' ";
            }
            if(monedaID > 0){        
                    sb += " and mm.id = "+monedaID;
            }
            if(fecha != null && !fecha.isEmpty()){        
                    sb += " and pv.FECHA_VALIDO <= '"+fecha+"' ";
            }
                    
                    sb += " order by pv.FECHA_VALIDO DESC, pv.FECHA_GENERO desc limit 1 ";

            List<Object[]> lo = em.createNativeQuery(sb).getResultList();            
            for (Object[] objects : lo) {                
                or = new ParidadValorVO();
                or.setId((Integer) objects[0]);                
                or.setValor((Double) objects[1]);                
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            or = null;
        }
        return or;
    }
    
    
    public ParidadValorVO traerParidadValorMonedaUSD(String siglas, String fecha, int monedaID) {
        ParidadValorVO or = null;
        try {

            String sb = " select pv.ID, pv.VALOR "
                    + " from PARIDAD_VALOR pv "
                    + " inner join PARIDAD p on p.id = pv.PARIDAD and p.ELIMINADO = 'False' "
                    + " inner join MONEDA m on m.id = p.MONEDA and m.ELIMINADO = 'False' "
                    + " inner join MONEDA mm on mm.id = p.MONEDADES and m.ELIMINADO = 'False' "
                    + " where pv.ELIMINADO = 'False' ";
            
            if(siglas != null && !siglas.isEmpty()){
                    sb += " and m.SIGLAS = '"+siglas+"' ";
            }
            if(monedaID > 0){        
                    sb += " and m.id = "+monedaID;
            }
            if(fecha != null && !fecha.isEmpty()){        
                    sb += " and pv.FECHA_VALIDO <= '"+fecha+"' ";
            }
                    
                    sb += " order by pv.FECHA_VALIDO DESC, pv.FECHA_GENERO desc limit 1 ";

            List<Object[]> lo = em.createNativeQuery(sb).getResultList();            
            for (Object[] objects : lo) {                
                or = new ParidadValorVO();
                or.setId((Integer) objects[0]);                
                or.setValor((Double) objects[1]);                
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            or = null;
        }
        return or;
    }

}

