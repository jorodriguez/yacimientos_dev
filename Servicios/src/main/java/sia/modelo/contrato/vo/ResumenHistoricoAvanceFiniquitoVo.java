/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.contrato.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Setter
@Getter
public class ResumenHistoricoAvanceFiniquitoVo {
    private int id;
    private int idCampo;
    private String campo;
    private Date fechaGenero;
    private String avance;
    private String diferencia;
    private int totalContabilizado;
    private Date horaGenero;
    //
    //
}
