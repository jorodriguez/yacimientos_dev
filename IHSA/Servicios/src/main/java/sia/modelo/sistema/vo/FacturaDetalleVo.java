/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sistema.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class FacturaDetalleVo implements Serializable{
    private int id;
    private int idFactura;
    private int idOrdenDetalle;
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal precio;
    private BigDecimal importe;
    private boolean editar;
    private double cantidadOrdenDetalle;
    private BigDecimal cantidadModificar;
}
