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
public enum OrdenEstadoEnum {
    POR_SOLICITAR(101),
    POR_VOBO(110),
    POR_REVISAR(120),
    POR_APROBAR(130),
    POR_APROBAR_SOCIO(135),
    POR_AUTORIZAR(140),
    POR_ACEPTAR_CARTA_INTENCION(145),
    POR_REVISAR_REPSE(148),
    POR_ENVIAR_PROVEEDOR(150),
    POR_RECIBIR(160),
    RECIBIDA_PARCIAL(163),
    POR_RECIBIR_FACTURA(165),
    OCS_RECEPCION_FACTURA(170),
    OCS_PROCESO_FACTURA(175),
    OCS_PAGADA(190),
    POR_AUTORIZAR_1MMD(151);

    private final int id;

    private OrdenEstadoEnum(final int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }

}
