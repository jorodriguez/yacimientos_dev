/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lector.vision.api.service;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import lector.archivador.DocumentoAnexo;
import lector.process.ItemNative;
import static lector.process.Lector.getTexto;
import lector.vision.InformacionCredencialDto;
import static lector.vision.ToolsStr.*;

/**
 *
 * @author jorodriguez
 */
@Stateless
public class LectorService {//extends AbstractImpl<SiParametro>{

  
    public InformacionCredencialDto getTextoAlt(String filePath) {
        
        try {
           
            final List<ItemNative> listaItemTexto = getTexto(new URL(filePath));
                       
            
            return detectarEtiquetas(listaItemTexto);
           
        } catch (IOException ex) {
            Logger.getLogger(LectorService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }      
        
    }
    
     /*public InformacionCredencialDto getTextoData(byte[] data) {
        
        try {
           
            final List<ItemNative> listaItemTexto = getTexto(data);
                                   
            return detectarEtiquetas(listaItemTexto);
           
        } catch (IOException ex) {
            Logger.getLogger(LectorService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }            
        
    }*/
      
      public InformacionCredencialDto getInformacionCredencial(DocumentoAnexo documento) {
        
        try {
           
            final List<ItemNative> listaItemTexto = getTexto(documento.getContenido());
                                   
             InformacionCredencialDto info = detectarEtiquetas(listaItemTexto);
             
             //ToFIx
             info.setImagen(documento);
             
             return info;
           
        } catch (IOException ex) {
            Logger.getLogger(LectorService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }             
        
    }
 
}
