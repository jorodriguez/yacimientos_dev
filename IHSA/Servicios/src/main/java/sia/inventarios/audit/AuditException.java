/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.audit;

import sia.excepciones.SIAException;

/**
 *
 * @author AdminSia
 */
public class AuditException extends SIAException {

    /**
     * Creates a new instance of
     * <code>AuditException</code> without detail message.
     */
    public AuditException() {
        super();
    }

    /**
     * Constructs an instance of
     * <code>AuditException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public AuditException(String msg) {
        super(msg);
    }
}
