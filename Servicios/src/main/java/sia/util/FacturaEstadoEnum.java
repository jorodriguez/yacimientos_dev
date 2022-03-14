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
public enum FacturaEstadoEnum {
    CANCELADA(700),
    CREADA(710),
    ENVIADA_CLIENTE(720),
    PROCESO_INTERNO_CLIENTE(730),
    CORREO_FACTURA_AVANZIA(731),
    PROCESO_DE_PAGO(740),
    PAGADA(750),
    ENVIADA_CNH(760);

    private final int id;

    private FacturaEstadoEnum(final int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }

}
