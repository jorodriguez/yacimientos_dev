/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sistema.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.orden.vo.MovimientoVO;

/**
 *
 * @author mluis
 */
@Setter
@Getter
public class FacturaMovimientoVo implements Serializable{
    private int id;
    private int idFactura;
    private Date fecha;
    private String genero;
    private MovimientoVO movimientoVO;
}
