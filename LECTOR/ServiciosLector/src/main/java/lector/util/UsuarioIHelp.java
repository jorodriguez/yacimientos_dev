/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package lector.util;

import lector.dominio.modelo.usuario.vo.UsuarioVO;
import lector.modelo.CCuenta;
import lector.modelo.CTipoContacto;
import lector.modelo.Usuario;
import static lector.util.UtilsProcess.castToInt;

/**
 *
 * @author jorodriguez
 */
public interface UsuarioIHelp {
    
    static Usuario buildUsuarioDto(UsuarioVO usuario ){
        
        return Usuario.builder()
                .nombre(usuario.getNombre())
                .domicilio(usuario.getDomicilio())
                .clave(usuario.getClave())
                .curp(usuario.getCurp())
                .estado(castToInt(usuario.getEstado()))
                .municipio(usuario.getMunicipio())
                .localidad(usuario.getLocalidad())
                .seccion(usuario.getSeccion())
                .emision(usuario.getAnioEmision())
                .vigencia(usuario.getVigencia())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .sexo(usuario.getSexo())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .cCuenta(new CCuenta(usuario.getCCuenta()))
                .cTipoContacto(new CTipoContacto(usuario.getCTipoContacto()))
                .genero(new Usuario(usuario.getGenero()))
                .registro(new Usuario(usuario.getGenero()))                
                .build();
        
    }
      
}
