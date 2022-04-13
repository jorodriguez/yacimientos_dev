/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sistema.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.AdjuntoVO;

/**
 *
 * @author mluis
 */
@Setter
@Getter
public class FacturaAdjuntoVo implements Serializable{
    private int id;
    private int idFactura;
    private String tipo;
    private AdjuntoVO adjuntoVo;
}
