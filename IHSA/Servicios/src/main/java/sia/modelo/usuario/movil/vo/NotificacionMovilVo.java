/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.usuario.movil.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jorodriguez
 */
@Getter
@Setter
public class NotificacionMovilVo {       
    
    private String usuario;
    private String titulo;
    private String mensaje;
    private String token;
    private String icono;

    @Builder
    public NotificacionMovilVo(String usuario, String titulo, String mensaje, String token, String icono) {
        this.usuario = usuario;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.token = token;
        this.icono = icono;
    }

       
    
    
}


