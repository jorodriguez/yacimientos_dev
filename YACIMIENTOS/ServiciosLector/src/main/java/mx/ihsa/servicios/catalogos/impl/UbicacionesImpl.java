/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.servicios.catalogos.impl;

import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import mx.ihsa.dominio.vo.CLocalidadVo;
import mx.ihsa.dominio.vo.CSeccionVo;
import mx.ihsa.util.UtilLog4j;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

/**
 *
 * @author jorodriguez
 */
@Stateless
public class UbicacionesImpl {
    
     @Inject
    protected DSLContext dbCtx;
    
    private static final UtilLog4j log = UtilLog4j.log;
    
    public List<CLocalidadVo> findAllLocalidades(Integer municipioId ) {
        
        log.info("@findAllLocalidades "+municipioId);
        
        List<CLocalidadVo> retVal = Collections.emptyList();
        
        try {
            retVal = 
                    dbCtx.fetch("select loc.id, loc.clave,loc.nombre,e.nombre as estado, m.nombre as municipio" +
                                " from c_localidad loc inner join c_estado e on e.id = loc.c_estado\n" +
                                "                       inner join c_municipio m on m.id = loc.c_municipio\n" +
                                "  where m.id = ? \n" +
                                "	and m.eliminado = false\n" +
                                "	and loc.eliminado = false\n" +
                                "	and e.eliminado = false", municipioId)
                            .into(CLocalidadVo.class);
            
        } catch (DataAccessException e) {
            
            log.warn(this, "*** Al recuperar la sesion {0}", new Object[]{municipioId}, e);
            
        }
        
        return retVal;
    }
    
    
    public List<CSeccionVo> findAllSeccionesLocalidad(Integer municipioId ) {
        
        log.info("@findAllSeccionesLocalidad "+municipioId);
        System.out.println("@findAllSeccionesLocalidad");
        
        List<CSeccionVo> retVal = Collections.emptyList();
        
        try {
            retVal = 
                    dbCtx.fetch("select s.id, s.nombre  from c_seccion s where s.c_localidad = ? and eliminado = false", municipioId)
                            .into(CSeccionVo.class);
            
        } catch (DataAccessException e) {
            
            log.warn(this, "*** Al recuperar la sesion {0}", new Object[]{municipioId}, e);
            
        }
        
        return retVal;
    }

}
