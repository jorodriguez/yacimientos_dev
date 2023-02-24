/*
 */
package lector.vision;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lector.dominio.modelo.usuario.vo.UsuarioVO;
import lector.process.ItemNative;
import static lector.constantes.Constantes.ETIQUETAS_INE;


/**
 *
 * @author jorodriguez
 */
public class ToolsStr {

    private static final String LABEL_NOT_FOUND = "NO_ENCONTRADO";
    
    
    
  /*  public static List<Item> getTextoImagen(String filePath){        
        System.out.println("@getTextoImagen filepath");
        final List<Item> listaItemTexto;        
        try {
            
            listaItemTexto = getTexto(filePath);
            
            return procesarTextoImagen(listaItemTexto);
            
        } catch (IOException ex) {
            System.err.println("Error getTextoImagen "+ex);
            
            return Collections.emptyList();
        }     
        
    }
    
    public static List<Item> getTextoImagen(URL url){        
        System.out.println("@getTextoImagen URL");
        
        final List<Item> listaItemTexto;        
        try {
            
            listaItemTexto = getTexto(url);
            
            return procesarTextoImagen(listaItemTexto);
            
        } catch (IOException ex) {
            System.err.println("Error getTextoImagen "+ex);
            
            return Collections.emptyList();
        }     
        
    }
    
    public static List<Item> getTextoImagen(byte[] bytes){        
        System.out.println("@getTextoImagen byte");
        final List<Item> listaItemTexto;        
        try {
            
            listaItemTexto = getTexto(bytes);
            
            return procesarTextoImagen(listaItemTexto);
            
        } catch (IOException ex) {
            System.err.println("Error getTextoImagen bytes "+ex);
            
            return Collections.emptyList();
        }     
        
    }*/
    
        
    /*public static List<Item> detectarEtiquetas(List<ItemNative> listaItemTexto){    
        
        System.out.println("@procesarTextoImagen");
        
        try {
            
            if(listaItemTexto.isEmpty()){
                return Collections.emptyList();
            }
            
            final List<Item> listaItems = new ArrayList<>();           
            
            String textoCompleto = listaItemTexto.get(0).getValor();
            
            String cadenaLimpia = prepararCadena(textoCompleto);
            
            for(String etiqueta : ETIQUETAS_INE){
             
                cadenaLimpia = marcarEtiquetas(etiqueta, cadenaLimpia);                
            }
            
            int pos = 0;
            
            for (String etiqueta : ETIQUETAS_INE) {

                String valor = obtenerValor(etiqueta, cadenaLimpia);

                Item item = new Item();
                item.setEtiqueta(etiqueta);
                item.setPosicion(pos++);
                item.setValor(valor);
                listaItems.add(item);
                
                System.out.println(item.getPosicion() + " " + item.getEtiqueta() + "=" + item.getValor());
                
            }
            
            return listaItems;
        
        
        } catch (Exception ex) {
            Logger.getLogger(ToolsStr.class.getName()).log(Level.SEVERE, null, ex);
            return Collections.emptyList();
        }
        
    }     */
    
    public static InformacionCredencialDto detectarEtiquetas(List<ItemNative> listaItemTexto){    
        
        System.out.println("@procesarTextoImagen");
            
            if(listaItemTexto.isEmpty()){
                return null;
            }           
            
            final Map<String,Item> etiquetas = new HashMap<>();
            
            String textoCompleto = listaItemTexto.get(0).getValor();
            
            String cadenaLimpia = prepararCadena(textoCompleto);
            
            for(String etiqueta : ETIQUETAS_INE){
             
                cadenaLimpia = marcarEtiquetas(etiqueta, cadenaLimpia);                
            }
            
            int pos = 0;
            
            for (String etiqueta : ETIQUETAS_INE) {

                String valor = obtenerValor(etiqueta, cadenaLimpia);

                Item item = new Item();
                item.setEtiqueta(etiqueta);
                item.setPosicion(pos++);
                item.setValor(valor);
                
                etiquetas.put(etiqueta, item);
                
                System.out.println(item.getPosicion() + " " + item.getEtiqueta() + "=" + item.getValor());
                
            }
            
            InformacionCredencialDto info = 
                        InformacionCredencialDto
                                .builder()
                                .etiquetasDetectadas(etiquetas)
                                .metadatoLectura(textoCompleto)
                                .usuarioDto(new UsuarioVO())
                                .build();                            
            
            return info;
    }     
     
    
    private static String prepararCadena(String cadena) {

        final String cn = cadena.replaceAll("([A-Z])\\\\.(?=[ A-Z.])", "");

        return cn.replaceAll("\n", " ");

    }

    private static String marcarEtiquetas(String etiqueta, String cadena) {

        final String nuevaCadena = cadena.replace(etiqueta, "<" + etiqueta + ">");

        return nuevaCadena;
    }

    private static String obtenerValor(String etiqueta, String cadena) {
        try {
            
            final String etiquetaCompleta = "<" + etiqueta + ">";

            final int pos = cadena.indexOf(etiquetaCompleta);

            final String cadenaPosterior = cadena.substring(pos + etiquetaCompleta.length(), cadena.length());

            int posicionEtiquetaProxEncontrada = -1;

            //List<char> list = cadenaPosterior.chars().mapToObj(i -> (char) i).collect(Collectors.toList());
            posicionEtiquetaProxEncontrada = cadenaPosterior.indexOf("<");

            if (posicionEtiquetaProxEncontrada == -1) {
                return LABEL_NOT_FOUND;
            }

            String valor = cadenaPosterior.substring(0, posicionEtiquetaProxEncontrada);

            return valor;

        } catch (Exception e) {
            System.err.println("ERROR" + e);
            return LABEL_NOT_FOUND;

        }

    }
    

}
