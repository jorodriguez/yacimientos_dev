/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.proveedor.Vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class ProveedorSinCartaIntencionVo implements Serializable{
    private int id;
    private int idProveedor;
    private String proveedor;
    private String rfcProveedor;
    private int idCampo;
    private String campo;
}
