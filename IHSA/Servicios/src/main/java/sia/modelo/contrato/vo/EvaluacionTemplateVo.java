/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.contrato.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jcarranza
 */
@Setter
@Getter
public class EvaluacionTemplateVo {
    
    private int id;
    private String nombre;
    private String descripcion;
    private String titulo;
    private String interpretacion;
    private String notas;
    private String clasificacion;
    private int idClasificacion;
    
}
