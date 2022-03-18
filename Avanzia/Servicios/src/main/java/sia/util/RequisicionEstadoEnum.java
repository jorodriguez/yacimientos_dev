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
public enum RequisicionEstadoEnum {

    POR_SOLICITAR(1),
    POR_REVISAR(10),
    POR_APROBAR(15),
    POR_ASIGNAR(20),
    POR_GENERAR_ORDEN(40),
    POR_VOBO_COSTOS(25);

    private final int id;

    private RequisicionEstadoEnum(final int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }

    public static final List<RequisicionEstadoEnum> TODOS_ESTADOS_REQUISICION = prepararTodosEstados();

    private static List<RequisicionEstadoEnum> prepararTodosEstados() {

        // lista debe ser de solo lectura
        final List<RequisicionEstadoEnum> list = ImmutableList.of(
                POR_SOLICITAR,
                POR_REVISAR,
                POR_APROBAR,
                POR_ASIGNAR,
                POR_GENERAR_ORDEN,
                POR_VOBO_COSTOS
        );

        return list;
    }

}
