/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sistema.vo;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.AdjuntoVO;

/**
 *
 * @author ihsa
 */
@Setter
@Getter
public class IncidenciaAdjuntoVo {

    private int id;
    private Date fechaGenero;
    private int idIncidencia;
    private List<AdjuntoVO> adjuntos;
    private AdjuntoVO adjuntoVo;
}
