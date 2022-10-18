/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.proveedor.Vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class CuentaBancoVO {
    //Datos Bancarios
    private int idCuentaBanco;
    private int idProveedor;
    private String banco;
    private String cuenta;
    private String moneda;
    private int idMoneda;
    private String clabe;
    private String swift;
    private String aba;
}
