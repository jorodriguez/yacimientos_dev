/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.contrato.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class ContenedorClasificacionVo implements Serializable {

    private int id;
    private ClasificacionVo clasificacionVo = new ClasificacionVo();
    private List<ClasificacionVo> clasificacion = new ArrayList<ClasificacionVo>();
    //
    private List<ContenedorClasificacionVo> contenedorClasificacionVo = new ArrayList<ContenedorClasificacionVo>();

}
