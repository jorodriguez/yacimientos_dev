/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.orden.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class CompaniaAcumuladoVo {
    private int id;
    private String compania;
    private boolean verificaMonto;
    private double montoDolar;
    private double  montoPesos;
}
