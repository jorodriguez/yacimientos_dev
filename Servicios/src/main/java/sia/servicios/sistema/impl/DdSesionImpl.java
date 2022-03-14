package sia.servicios.sistema.impl;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TransactionRequiredException;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import sia.modelo.DdSesion;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.Sesion;
import sia.util.UtilLog4j;

/**
 * Administración de datos de registro de sesiones de usuarios.
 * @author mrojas
 */
@LocalBean 
public class DdSesionImpl extends AbstractFacade<DdSesion> {

    private static final UtilLog4j log = UtilLog4j.log;
    
    
    @Inject
    private DSLContext dbCtx;
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

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
                    dbCtx.fetchOne("SELECT * FROM dd_sesion WHERE sesion_id = ? ", sesionId)
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
