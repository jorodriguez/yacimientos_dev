package sia.inventarios.audit;

/**
 *
 * @author AdminSia
 */
public enum AuditActions {

    /**
     * Insercion de un registro
     */
    CREATE(1),
    /**
     * Actualizacion de un registro
     */
    UPDATE(2),
    /**
     * Eliminacion de un registro
     */
    DELETE(3);
    private Integer canonico;

    AuditActions(Integer canonico) {
        this.canonico = canonico;
    }

    public Integer getCanonico() {

        return this.canonico;
    }
}
