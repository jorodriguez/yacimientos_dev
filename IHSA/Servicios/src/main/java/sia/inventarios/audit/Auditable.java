/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Eduardo
 *
 * Esta interfaz define el comportamiento de la anotacion @Auditable, que es utilizada por la clase Audit,
 * para determinar que operaciones se van a auditar en un modelo. La clase Audit es utilizada por la clase GenericDAO
 * en los metodos create(), update() y remove()
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface Auditable {
    /**
     * La configuracion "auditOn" de la anotacion @Auditable para los modelos permite especificar los tipos de accion que exclusivamente se van a registrar.
     * Ejemplo: @Auditable(auditOn= {AuditActions.CREATE})
     * 
     * Si se requiere el comportamiento default, solo basta con especificar la anotacion @Auditable sin configuracion en el modelo
     *
     * Abajo se especifica el comportamiento default de la anotacion.
     * @return
     */
    AuditActions[] auditOn() default {AuditActions.CREATE, AuditActions.UPDATE, AuditActions.DELETE};
}
