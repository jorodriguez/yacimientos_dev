/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.vo;

import lombok.Getter;
import lombok.Setter;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class ApCampoGerenciaVo extends Vo {

    private int idGerencia;
    private int idApCampo;
    private String nombreGerencia;
    private String nombreApCampo;
    private String idResponsable;
    private String nombreResponsable;
    private boolean visibleApCampo;
    private GerenciaVo vo2;
    private boolean selected;

    
    public String toString() {
	return "ApCampoGerenciaVo{" + "idGerencia=" + idGerencia + ", idApCampo=" + idApCampo + ", nombreGerencia=" + nombreGerencia + ", nombreApCampo=" + nombreApCampo + ", idResponsable=" + idResponsable + ", nombreResponsable=" + nombreResponsable + ", visibleApCampo=" + visibleApCampo + '}';
    }
}
