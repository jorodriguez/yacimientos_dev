/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.util;

/**
 *
 * @author mluis
 */
public enum TicketEstadoEnum {
    CANCELADO(1200),
    NUEVO(1210),
    ASIGNADO(1220),
    SOLUCIONADO(1230),
    CERRADO(1260);

    private final int id;

    private TicketEstadoEnum(final int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }

}
