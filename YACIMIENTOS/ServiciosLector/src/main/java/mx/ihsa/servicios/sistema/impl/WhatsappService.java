/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.servicios.sistema.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import mx.ihsa.modelo.CCuenta;
import mx.ihsa.modelo.SiParametro;
import mx.ihsa.servicios.catalogos.impl.CCuentaImpl;
import mx.ihsa.util.UtilLog4j;
import static mx.ihsa.util.WhatsappUtils.*;

/**
 *
 * @author jorodriguez
 */
@Stateless
public class WhatsappService {

    private static final UtilLog4j log = UtilLog4j.log;
    
    @Inject
    private SiParametroImpl parametroService;

    @Inject
    private CCuentaImpl cuentaService;

    public String send(String numero, String mensaje,int idCuenta) {
        
        log.info("@send "+numero+" cuenta "+idCuenta+" mensaje "+mensaje);
        System.out.println("@send "+numero+" cuenta "+idCuenta+" mensaje "+mensaje);

        final SiParametro siParametro = parametroService.find(1);
        
        final CCuenta cuenta = cuentaService.find(idCuenta);        

        String jsonMessage = Json.createObjectBuilder()
                .add("phoneNumber", numero)
                .add("message", mensaje)
                .add("apiKey", cuenta.getApiKeyWhatsapp())
                .build()
                .toString();

        System.out.println("enviar : " + jsonMessage);

        return sendAsync(jsonMessage, URI.create(siParametro.getApiWhatsapp()));

    }

    private String sendAsync(String jsonMessage, URI uri) {

         String retVal = null;
         
        try {

            final HttpClient client = HttpClient.newHttpClient();

            final HttpRequest request = getHttpPostRequest(jsonMessage, uri);

            final CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            final HttpResponse<String> response = futureResponse.get();
            
             retVal = response.body();
             
              System.out.println("RESPUESTA : " + jsonMessage+"  "+retVal);

        } catch (InterruptedException e) {

            System.out.println("PROCESO INTERRUMPIDO :");

            Logger.getLogger(WhatsappService.class.getName()).log(Level.SEVERE, null, e);

        } catch (ExecutionException ex) {
            Logger.getLogger(WhatsappService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }

    // SYNC - Espera la respuesta de cada mensaje y usa el mismo hilo de ejecución - 
    // Recomendado para mensaje mensajes unicos o pocos mensajes < 50
    private static String sendSync(String jsonMessage, URI uri) {

        String retVal = null;

        try {

            final HttpClient client = HttpClient.newHttpClient();

            final HttpRequest request = getHttpPostRequest(jsonMessage, uri);

            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            guardarRespuesta(response.statusCode());

            retVal = response.body();

            System.out.println("RESPUESTA: " + retVal);

        } catch (InterruptedException e) {

            System.out.println("PROCESO INTERRUMPIDO :");

            Logger.getLogger(WhatsappService.class.getName()).log(Level.SEVERE, null, e);

        } catch (IOException ex) {
            System.out.println("Error al enviar el mensaje :" + jsonMessage + " " + ex.getMessage());
            Logger.getLogger(WhatsappService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }

}
