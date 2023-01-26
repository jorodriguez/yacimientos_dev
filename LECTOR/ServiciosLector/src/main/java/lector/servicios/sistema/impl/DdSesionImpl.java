package lector.servicios.sistema.impl;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TransactionRequiredException;
import lector.constantes.Constantes;
import lector.modelo.DdSesion;
import lector.modelo.sistema.vo.Sesion;
import lector.sistema.AbstractImpl;
import lector.util.UtilLog4j;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

/**
 * Administración de datos de registro de sesiones de usuarios.
 * @author mrojas
 */
@Stateless 
public class DdSesionImpl extends AbstractImpl<DdSesion> {

    private static final UtilLog4j log = UtilLog4j.log;
    
    
    public DdSesionImpl() {
        super(DdSesion.class);
    }
    
    public void create(DdSesion entity) {
        em.persist(entity);
    }

    
    
    public void edit(DdSesion entity) {
        em.merge(entity);
    }
    
    
    public void remove(DdSesion entity) {
        em.remove(entity);
    }
    
    
    public DdSesion find(Object id) {
        return em.find(DdSesion.class, id);
    }

    public DdSesion findBySesionId(String sesionId) {
        DdSesion retVal = null;
        
        try {
            retVal = 
                    dbCtx.fetchOne("SELECT * FROM dd_sesion WHERE sesion_id = ? ORDER BY fecha_inicio DESC LIMIT 1", sesionId)
                            .into(DdSesion.class);
        } catch (DataAccessException e) {
            log.warn(this, "*** Al recuperar la sesion {0}", new Object[]{sesionId}, e);
        }
        
        return retVal;
    }
    
    
    public void guardarSesion(Sesion sesion) {
        try {
            DdSesion ddSesion = new DdSesion();
            ddSesion.setSesionId(sesion.getSesionId());
            ddSesion.setDatosCliente(sesion.getDatosCliente());
            ddSesion.setFechaInicio(Timestamp.valueOf(LocalDateTime.now()));
            ddSesion.setPuntoAcceso(sesion.getPuntoAcceso());
            ddSesion.setGenero(sesion.getGenero());
            ddSesion.setFechaGenero(Date.valueOf(LocalDate.now()));
            ddSesion.setHoraGenero(Time.valueOf(LocalTime.now()));

            create(ddSesion);
        } catch (EntityExistsException | TransactionRequiredException e) {
            log.warn(this, "*** Al guardar los datos de la sesion: {0}", new Object[]{sesion}, e);
        }
    }
    
    
    public void registrarSalida(String sesionId, String usuario) {
        DdSesion sesion = findBySesionId(sesionId);
        
        if(sesion == null) {
            log.warn(this, "*** No se localizó el registro para la sesión {0}", new Object[]{sesionId});
        } else {
            try {
                sesion.setFechaFin(Timestamp.valueOf(LocalDateTime.now()));
                sesion.setModifico(usuario);
                sesion.setFechaModifico(Date.valueOf(LocalDate.now()));
                sesion.setHoraModifico(Time.valueOf(LocalTime.now()));

                edit(sesion);
            } catch(IllegalArgumentException | TransactionRequiredException e) {
                log.warn(this, "*** No fue posible registrar la salida de la sesión {0}", new Object[]{sesionId}, e);
            }
        }
    }
}
