/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.accesorio;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class LineaVo implements Serializable {

    private int id;
    private AccesorioVo accesorioVo;
    private String cuenta;
    private String subCuenta;
    private String tipoLinea;
    private String numero;
    private int idEstado;
    private String estado;
    private String emei;
}
