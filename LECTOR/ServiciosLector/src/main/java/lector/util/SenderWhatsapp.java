/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lector.util;


import java.net.URI;
import java.net.http.HttpRequest;
import javax.json.Json;


/**
 *
 * @author jorodriguez
 */
public class SenderWhatsapp {

    private static final String URL = "http://localhost:5001/whatsapp/send";

    //TO-FIX
    public static void sendWhatsapp(String numero, String mensaje) {

        
         //jsonParse.put("phoneNumber", numero);
        //jsonParse.addProperty("message", mensaje);
        //jsonParse.addProperty("apiKey", "523bb545-0bc3-9d34-0a97-3588baefba11");
       String jsonMessage = Json.createObjectBuilder()
            .add("phoneNumber", numero)
            .add("message", mensaje)
            .build()
            .toString();
                                 
        

        System.out.println("enviar : " + jsonMessage);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                 .header("Authorization", "Bearer TOKDUM")
                .header("Content-Type", "application/json")
                .POST(
                        HttpRequest.BodyPublishers.ofString(jsonMessage)
                )
                //.header("Authorization", "Basic " +   Base64.getEncoder().encodeToString(("bearer :123456").getBytes()))
                .build();

    }

}
