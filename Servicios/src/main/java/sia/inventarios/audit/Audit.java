/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.audit;

import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.excepciones.SIAException;
import sia.modelo.Usuario;
import sia.inventarios.log.EjbLog;

/**
 *
 * @author AdminSia
 */
@LocalBean 
public class Audit {
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    public <T> void register(AuditActions action, T entity, Usuario user) throws AuditException, SIAException, Exception {
        if (entity == null) {
            throw new AuditException("No se a especificado el registro para la auditoria");
        }

        Auditable auditAnnotation = entity.getClass().getAnnotation(Auditable.class);

        if (auditAnnotation != null) { // Solamente auditar si esta definida la anotación Auditable en el model
            for (AuditActions a : auditAnnotation.auditOn()) {
                if (action.equals(a)) {
                    this.registerAuditEvent(action, entity, user);
                    break;
                }
            }
        }
    }
    
    private <T> void registerAuditEvent(AuditActions action, T entity, Usuario user) throws SIAException, Exception {
        Class entityClass = entity.getClass();
        String currentlySavedEntityString = "";
        String entityString = entity.toString();
        
        try{
            Object entityId = entityClass.getMethod("getId", entityClass).invoke(entity);
            T currentlySavedEntity = (T) em.find(entityClass, entityId);
            if(currentlySavedEntity != null){
                currentlySavedEntityString = currentlySavedEntity.toString();
            }
        } catch (IllegalAccessException ex) {
            currentlySavedEntityString = "[N/A]";
            EjbLog.warn("Audit.registerAuditEvent: No se pudo invocar el metodo getId() en " + entityClass);
        } catch(Exception ex) {
            EjbLog.warn("Audit.registerAuditEvent: " + ex.getMessage());
        }
        
        EjbLog.info("Audit: " + action + " [" +  entityClass + "]: " + entityString);
    }
}
