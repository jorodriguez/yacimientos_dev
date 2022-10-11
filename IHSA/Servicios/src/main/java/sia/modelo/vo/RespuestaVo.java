/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jorodriguez
 */
@Getter
@Setter
public class RespuestaVo {
 
    private boolean realizado = true;
    private String mensaje;    

    @Builder
    public RespuestaVo(boolean realizado, String mensaje) {
        this.realizado = realizado;
        this.mensaje = mensaje;
    }
       
    
}
