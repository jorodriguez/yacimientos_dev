/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.vo.inventarios;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class OrdenFormatoVo {
    private int id;
    private String orden;
    private String pedido;
    private String referencia;
    private int idProveedor;
    private String proveedor;
    private int idAdjunto;
    private String uuId;
    private String archivo;
    private Date fechaGenero;
}
