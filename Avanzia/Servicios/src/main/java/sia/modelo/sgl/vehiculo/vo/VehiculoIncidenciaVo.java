/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vehiculo.vo;

import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sistema.vo.IncidenciaVo;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class VehiculoIncidenciaVo {
   private int idVehiculoIncidencia;
   private VehiculoVO vehiculoVO = new VehiculoVO();
   private IncidenciaVo incidenciaVo = new  IncidenciaVo();
}
