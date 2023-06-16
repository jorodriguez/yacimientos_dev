/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.ihsa.util;

import java.util.Date;
import mx.ihsa.dominio.modelo.usuario.vo.UsuarioVO;
import mx.ihsa.modelo.Usuario;
import static mx.ihsa.util.UtilsProcess.castToInt;

/**
 *
 * @author jorodriguez
 */
public interface UsuarioIHelp {
    
    static Usuario buildUsuarioDto(UsuarioVO usuario ){
        
        return Usuario.builder()
                .nombre(usuario.getNombre())
                .domicilio(usuario.getDomicilio())
                .clave("")
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .genero(new Usuario(usuario.getGenero()))
                .registro(new Usuario(usuario.getGenero()))                
                .fechaGenero(new Date())                
                .eliminado(Boolean.FALSE)
                .build();
        
    }
      
}
