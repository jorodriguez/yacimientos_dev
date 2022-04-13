/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.gerencia.vo;

import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author b75ckd35th
 */
@Getter
@Setter
public class GerenciaVo extends Vo {

    private int idApCampo;
    private String nombreApCampo;
    private String idResponsable;
    private String nombreResponsable;
    private String rfcCompania;
    private String nombreCompania;
    private Integer idPuesto;
    private String puesto;
    private String abrev;
    private boolean selected = false;
}
