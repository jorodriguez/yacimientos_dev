/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package lector.process;

import com.google.api.client.util.IOUtils;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author joel
 */

/*
      ANTES DE COMENZAR 
    * 1. Configurar una cuenta en google api vision https://cloud.google.com/vision?utm_source=google&utm_medium=cpc&utm_campaign=latam-MX-all-es-dr-BKWS-all-all-trial-e-dr-1605194-LUAC0014889&utm_content=text-ad-none-any-DEV_c-CRE_548047189087-ADGP_Hybrid%20%7C%20BKWS%20-%20EXA%20%7C%20Txt%20~%20AI%20&%20ML_Vision-AI-KWID_43700066578950557-kwd-1432325904212&utm_term=KW_google%20cloud%20vision%20ai-ST_Google%20Cloud%20Vision%20AI&gclid=Cj0KCQiAw8OeBhCeARIsAGxWtUxIYAV1F3lTQNABVsYlzmfN70ROwTbDLG49Bj44o-XGwxVOyft43wgaAjJcEALw_wcB&gclsrc=aw.ds&hl=es-419#section-1
    * 2. Seguir los pasos para activar una llave de api vision JSON https://cloud.google.com/vision/docs/setup?hl=es-419
    * 3. Configurar la variable de entorno export GOOGLE_APPLICATION_CREDENTIALS=/home/joel/tools/credenciales-374423-09f1f26c3239.json

// For full list of available annotations, see http://g.co/cloud/vision/docs
 */
public class Lector {

    // private static List<String> ETIQUETAS_INE = Arrays.asList("NOMBRE", "DOMICILIO", "CLAVE DE ELECTOR", "CURP", "AÑO DE REGISTRO", "FECHA DE NACIMIENTO", "SECCIÓN", "VIGENCIA", "SEXO", "ESTADO", "MUNICIPIO", "LOCALIDAD", "EMISIÓN");

    /*    public static List<Item> procesar(String filePath) throws IOException {

        String filePath = "https://res.cloudinary.com/dwttlkcmu/image/upload/v1674658258/samples/mingo_yhlwbs.jpg";
        List<Item> list = detectTextInternet(filePath);
       
        List<Item> listaItems = new ArrayList<>();

        String textoCompleto = list.get(0).getValor();

        String cadenaLimpia = prepararCadena(textoCompleto);

        System.out.println("TEXT " + cadenaLimpia);

        for (String etiqueta : ETIQUETAS_INE) {

            cadenaLimpia = marcarEtiquetaInicio(etiqueta, cadenaLimpia);

        }

        System.out.println("Cadena final " + cadenaLimpia);

        int pos = 0;

        for (String etiqueta : ETIQUETAS_INE) {

            String valor = obtenerValor(etiqueta, cadenaLimpia);

            Item item = new Item();
            item.setEtiqueta(etiqueta);
            item.setPosicion(pos++);
            item.setValor(valor);
            listaItems.add(item);

            //System.out.println(item.getPosicion() + " " + item.getEtiqueta() + "=" + item.getValor());

        }

        System.out.println("========================");
        for (Item etiqueta : list) {
            System.out.println("" + etiqueta.getValor());
        }

        //MEJORAS, 
        //1 buscar la curp pero calularla antes, 
        //2 buscar la fecha de naciento por regex de fecha 
        return listaItems;

    }*/

 /*  public static String prepararCadena(String cadena) {

        String cn = cadena.replaceAll("([A-Z])\\\\.(?=[ A-Z.])", "");

        return cn.replaceAll("\n", " ");

    }

    public static String marcarEtiquetaInicio(String etiqueta, String cadena) {

        String nuevaCadena = cadena.replace(etiqueta, "<" + etiqueta + ">");

        return nuevaCadena;
    }

    public static String obtenerValor(String etiqueta, String cadena) {
        try {
            
            String etiquetaCompleta = "<" + etiqueta + ">";

            int pos = cadena.indexOf(etiquetaCompleta);

            String cadenaPosterior = cadena.substring(pos + etiquetaCompleta.length(), cadena.length());

            int posicionEtiquetaProxEncontrada = -1;
            
            posicionEtiquetaProxEncontrada = cadenaPosterior.indexOf("<");

            if (posicionEtiquetaProxEncontrada == -1) {
                return "NO_ENCONTRADO";
            }

            String valor = cadenaPosterior.substring(0, posicionEtiquetaProxEncontrada);

            return valor;

        } catch (Exception e) {
            System.err.println("ERROR" + e);
            return "NO_ENCONTRADO";

        }

    }

    */
    public static List<ItemNative> getTexto(String fileLocalPath) throws IOException {

        final ByteString imgBytes = ByteString.readFrom(new FileInputStream(fileLocalPath));

        return procesarImagen(imgBytes);

    }

    public static List<ItemNative> getTexto(byte[] bytes) throws IOException {

        final ByteString imgBytes = ByteString.copyFrom(bytes);

        System.out.println("ByteString " + imgBytes.isEmpty());

        return procesarImagen(imgBytes);

    }

    public static List<ItemNative> getTexto(URL url) throws IOException {
        System.out.println("@detectaTexto");

        final byte[] data = downloadFile(url);

        final ByteString imgBytes = ByteString.copyFrom(data);
       
        return procesarImagen(imgBytes);

    }

    private static List<ItemNative> procesarImagen(ByteString imgBytes) throws IOException {
        
        if(imgBytes == null || imgBytes.isEmpty()){
            System.out.println("ERROR imgBytes es null");
            return Collections.emptyList();
        }

        final List<AnnotateImageRequest> requests = new ArrayList<>();

        final Image img = Image.newBuilder().setContent(imgBytes).build();

        final Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();

        final AnnotateImageRequest request
                = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

        requests.add(request);

        List<ItemNative> listaItems = new ArrayList<>();

        ItemNative itemPrincipal;

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {

            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);

            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return Collections.emptyList();
                }

                int index = 0;

                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {

                    itemPrincipal = new ItemNative();

                    if (index == 0) {
                        itemPrincipal.setEtiqueta("TODO");
                        itemPrincipal.setPosicion(0);
                    } else {
                        itemPrincipal.setEtiqueta(null);
                        itemPrincipal.setPosicion(index);
                    }

                    itemPrincipal.setValor(annotation.getDescription());
                    
                                        
                    index++;

                    listaItems.add(itemPrincipal);
                }
            }
        }
        return listaItems;
    }
    
   
    //FIXME: usar nio en vez de esto
    public static byte[] downloadFile(URL url) {
        try {
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(conn.getInputStream(), baos);

            return baos.toByteArray();
        } catch (IOException e) {
            System.out.println("ERROR AL DESCARGAR LA IMAGEN DE LA URL  " + e);
            // Log error and return null, some default or throw a runtime exception
        }
        return null;
    }

  }
