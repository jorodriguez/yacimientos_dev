/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.requisicion.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jcarranza
 */
@Setter
@Getter
public class OcCodigoSubTareaVO {
    
    private int id;
    private String codigo;
    private String nombre;
    
    public OcCodigoSubTareaVO(){
    
    }
    
    public OcCodigoSubTareaVO(int id, String codigo, String nombre){
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;    
    }
}
