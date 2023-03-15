/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lector.vision;

import java.util.Map;
import lector.archivador.DocumentoAnexo;
import lector.dominio.modelo.usuario.vo.UsuarioVO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jorodriguez
 */
@Getter
public class InformacionCredencialDto {
    @Setter
    private DocumentoAnexo imagen;
    private Map<String,Item> etiquetasDetectadas;
    private UsuarioVO usuarioDto;
    private String metadatoLectura;

    @Builder
    public InformacionCredencialDto(DocumentoAnexo imagen, Map<String, Item> etiquetasDetectadas, UsuarioVO usuarioDto, String metadatoLectura) {
        this.imagen = imagen;
        this.etiquetasDetectadas = etiquetasDetectadas;
        this.usuarioDto = usuarioDto;
        this.metadatoLectura = metadatoLectura;
    }
    
    /*
    public Usuario buildUsuarioModel() throws LectorException{
        
            if(this.usuarioDto == null){
               
                throw new LectorException("usuarioDto es null");                       
                        
            }
        
           final Usuario usuario = new Usuario();
           usuario.setDomicilio(this.usuarioDto.getDomicilio());
           
           return usuario;
           
    }*/
    
    public boolean contieneFoto(){
            return this.imagen != null;
    }
    
    
}
