/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.pago.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.oficina.vo.OficinaVO;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class PagoServicioVo implements Serializable {

    private int idPago;
    private int idTipo;
    private String tipo;
    private int idTipoEspecifico;
    private String tipoEspecifico;
    private int idMoneda;
    private String moneda;
    private int idAdjunto;
    private String adjunto;
    private int idProveedor;
    private String proveedor;
    private Double importe;
    private Date inicio;
    private Date fin;
    private Date vencimiento;
    private String observacion;
    private String recibo;
    private Double total;
    private String adjuntoUUID;
    private String vehiculo;
    private long totalEntero;
    //
    private OficinaVO oficinaVO = new OficinaVO();

}
