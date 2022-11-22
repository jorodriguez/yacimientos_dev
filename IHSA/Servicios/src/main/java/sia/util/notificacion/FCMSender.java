/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.util.notificacion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import sia.constantes.Constantes;
import static sia.constantes.Constantes.RESPONSE_OK;
import static sia.constantes.Constantes.UNAUTHORIZED;
import static sia.constantes.Constantes.SERVER_ERROR;
import static sia.constantes.Constantes.SERVER_UNAVAILABLE;
import sia.util.UtilLog4j;
import sia.constantes.Configurador;

/**
 *
 * @author ihsa
 */
public class FCMSender {

    /**
     *
     * @param titulo
     * @param mensaje
     * @param destinatario
     */
    public static void notificaciones(String titulo, String mensaje, String destinatario, String icono) {
        JSONObject json = new JSONObject();
        json.put("to", destinatario);
        JSONObject infoJson = new JSONObject();
        infoJson.put("title", titulo);
        infoJson.put("body", mensaje);
        infoJson.put("icon", icono);
        infoJson.put("color", "#1DA0FF");
        json.put("notification", infoJson);
        enviarNoti(json);
        enviarNotificacionExterno(titulo, mensaje, destinatario);
    }

    private static void enviarNotificacionExterno(String titulo, String mensaje, String token) {
        UtilLog4j.log.info("@enviarNotificacionExterno");
        try {

            final URL url = new URL(Configurador.getUrlApiMensajeriaExterna());

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            final String jsonParse
                    = new JSONObject()
                            .put("title", titulo)
                            .put("message", mensaje)
                            .put("token", token)
                            .toString();

            final OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(jsonParse);
            wr.flush();

            final int statusResponse = conn.getResponseCode(); //throw a exception if fail any

            if (statusResponse == RESPONSE_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                UtilLog4j.log.info(
                        FCMSender.class.getSimpleName(),
                        new StringBuilder("Mensaje enviado (api mensajeria externa) : ")
                                .append("title ").append(titulo).append(" token ")
                                .append(token)
                                .append(reader.readLine())
                                .toString()
                );
            } else {
                UtilLog4j.log.info(
                        FCMSender.class.getSimpleName(),
                        new StringBuilder("NO ENVIADO (api mensajeria externa) : ")
                                .append("title ").append(titulo).append(" token ")
                                .append(token)
                                .append(" statusResponse ")
                                .append(statusResponse)
                                .toString()
                );
            }

        } catch (IOException | JSONException exception) {            
            UtilLog4j.log.error(
                    FCMSender.class.getSimpleName(),
                    new StringBuilder("Error al consumir el api externo de mensajeria ").append(exception.getMessage()).toString()
            );

        }

    }

    private static void enviarNoti(final JSONObject jSONObject) {
        String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

        try {

            final URL url = new URL(API_URL_FCM);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + Constantes.AUTH_KEY_FCM);
            conn.setRequestProperty("Content-Type", "application/json");

            final OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(jSONObject.toString());
            wr.flush();

            final int status = conn.getResponseCode();

            switch (status) {
                case RESPONSE_OK: 
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    UtilLog4j.log.warn(FCMSender.class.getSimpleName(), "Android Notification Response : " + reader.readLine());
                    break;
                case UNAUTHORIZED: 
                    UtilLog4j.log.warn(FCMSender.class.getSimpleName(), "Notification Response : TokenId : " + jSONObject.getString("to") + " Error occurred :" + status);
                    break;
                case SERVER_ERROR:                    
                    UtilLog4j.log.warn(FCMSender.class.getSimpleName(), "Notification Response : [ errorCode=ServerError ] TokenId : " + jSONObject.getString("to"));
                    break;
                case SERVER_UNAVAILABLE:                    
                    UtilLog4j.log.warn(FCMSender.class.getSimpleName(), "Notification Response : FCM Service is Unavailable  TokenId : " + jSONObject.getString("to"));
                    break;
                default:
                    break;
            }

        } catch (MalformedURLException mlfexception) {
            // Prototcal Error
            UtilLog4j.log.error(FCMSender.class.getSimpleName(), "Error occurred while sending push Notification!.." + mlfexception.getMessage());
        } catch (IOException mlfexception) {
            //URL problem
            UtilLog4j.log.error(FCMSender.class.getSimpleName(), "Reading URL, Error occurred while sending push Notification!.." + mlfexception.getMessage());
        } catch (JSONException jsonexception) {
            //Message format error
            UtilLog4j.log.error(FCMSender.class.getSimpleName(), "Message Format, Error occurred while sending push Notification!.." + jsonexception.getMessage());
        } catch (Exception exception) {
            //General Error or exception.
            UtilLog4j.log.error(FCMSender.class.getSimpleName(), "Error occurred while sending push Notification!.." + exception.getMessage());
        }
    }
}
