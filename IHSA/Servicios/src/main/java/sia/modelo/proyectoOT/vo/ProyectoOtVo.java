/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.proyectoOT.vo;

import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author mluis
 */
@Setter
@Getter
public class ProyectoOtVo extends Vo{
    private String cuentaContable;
    private String nombreGerencia;
    private String rfcCompania;
    private boolean abierto;
    private boolean selected;
    private int idYacimiento;
    private int idSubCampo;
    private String yacimientoNombre;
    private String subcampoNombre;
    
}
