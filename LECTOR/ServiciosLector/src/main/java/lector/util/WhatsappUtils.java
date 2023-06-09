/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package lector.util;

import java.net.URI;
import java.net.http.HttpRequest;
import javax.inject.Inject;
import lector.servicios.sistema.impl.SiParametroImpl;

/**
 *
 * @author jorodriguez
 */
public interface WhatsappUtils {
    
    
    static HttpRequest getHttpPostRequest(String jsonSend,URI uri){
        
          HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .header("Authorization", "Bearer TOKDUM") //To-CONFIG : configurar en ENV la apiKey
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonSend))
            .build();
          
          return request;
          
    }
    
    static void guardarRespuesta(int statusCode){
        
         switch(statusCode){
        
                case 200:
                        System.out.println("MENSAJE ENVIADO ");
                        break;
                case 500:
                        System.out.println("NO ENVIADO ");
                        break;
                default: 
                    System.out.println("NO HAY RESPUESTA:");            
                    
        }
        
    }
      
}
