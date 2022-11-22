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
public class ContratoEvaluacionVo {
    
    private int id;
    private int idConvenio;
    private int idEvaTemp;
    private int idGerencia;
    private String nombreGerencia;
    private String usuario;
    private String nombreUsuario;
    private String nombreTemplate;
    private String nombreTipo;
    
}
