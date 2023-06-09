/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.ihsa.util;

import java.util.Date;
import mx.ihsa.dominio.modelo.usuario.vo.UsuarioVO;
import mx.ihsa.modelo.CCuenta;
import mx.ihsa.modelo.CEstado;
import mx.ihsa.modelo.CLocalidad;
import mx.ihsa.modelo.CMunicipio;
import mx.ihsa.modelo.CSeccion;
import mx.ihsa.modelo.CTipoContacto;
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
                .cSeccion(usuario.getCSeccion() == 0 ? null : new CSeccion(usuario.getCSeccion()))
                .genero(new Usuario(usuario.getGenero()))
                .registro(new Usuario(usuario.getGenero()))                
                .fechaGenero(new Date())                
                .eliminado(Boolean.FALSE)
                .build();
        
    }
      
}
