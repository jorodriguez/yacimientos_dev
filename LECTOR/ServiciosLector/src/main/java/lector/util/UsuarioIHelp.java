/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package lector.util;

import java.util.Date;
import lector.dominio.modelo.usuario.vo.UsuarioVO;
import lector.modelo.CCuenta;
import lector.modelo.CEstado;
import lector.modelo.CLocalidad;
import lector.modelo.CMunicipio;
import lector.modelo.CSeccion;
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
                .clave("")
                .curp(usuario.getCurp())
                .estado(usuario.getEstadoClave())
                .municipio(String.valueOf(usuario.getMunicipioClave()))
                .localidad(String.valueOf(usuario.getLocalidadClave()))
                .seccion(String.valueOf(usuario.getSeccionClave()))
                .emision(usuario.getAnioEmision())
                .vigencia(usuario.getVigencia())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .sexo(usuario.getSexo())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .cCuenta(new CCuenta(usuario.getCCuenta()))
                .cTipoContacto(new CTipoContacto(usuario.getCTipoContacto()))                
                .cEstado(new CEstado(usuario.getCEstado()))
                .cMunicipio(new CMunicipio(usuario.getCMunicipio()))
                .cLocalidad(new CLocalidad(usuario.getCLocalidad()))                
                .cSeccion(new CSeccion(usuario.getCSeccion()))
                .genero(new Usuario(usuario.getGenero()))
                .registro(new Usuario(usuario.getGenero()))                
                .fechaGenero(new Date())                
                .build();
        
    }
      
}
