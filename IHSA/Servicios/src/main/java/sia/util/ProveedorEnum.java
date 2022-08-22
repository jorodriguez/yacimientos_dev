/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.util;

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 *
 * @author mluis
 */
public enum ProveedorEnum {

    CANCELADO(900),
    REGISTRADO(910),
    EN_PROCESO(920),
    ACTIVO(930);

    private final int id;

    private ProveedorEnum(final int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }

    public static final List<ProveedorEnum> TODOS_ESTADOS_PROVEEDOR = prepararTodosEstados();
    public static final List<ProveedorEnum> ESTADOS_PROVEEDOR = prepararEstadosProveedor();

    private static List<ProveedorEnum> prepararTodosEstados() {

        // lista debe ser de solo lectura
        final List<ProveedorEnum> list = ImmutableList.of(
                CANCELADO,
                REGISTRADO,
                EN_PROCESO,
                ACTIVO
        );

        return list;
    }

    //-Estados utilizados en el informe para complementar la sección de pozos inactivos    
    private static List<ProveedorEnum> prepararEstadosProveedor() {

        final List<ProveedorEnum> list = ImmutableList.of(
                CANCELADO,
                REGISTRADO,
                EN_PROCESO,
                ACTIVO
        );
        return list;
    }

}
