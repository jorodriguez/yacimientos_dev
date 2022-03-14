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
public enum SolicitudMaterialEstadoEnum {
    SOLICITUD_CANCELADA(1000),
    SOLICITUD_CREADA(1010),
    VERIFICAR_EXISTENCIA(1020),
    POR_AUTORIZAR(1030),
    POR_ENTREGAR_MATERIAL(1040),
    MATERIAL_ENTREGADO(1050),
    SOLICITUD_TERMINADA(1060);

    private final int id;

    private SolicitudMaterialEstadoEnum(final int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }

}
