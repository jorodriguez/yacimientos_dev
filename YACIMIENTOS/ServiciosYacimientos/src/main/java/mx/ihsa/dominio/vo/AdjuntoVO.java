/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.dominio.vo;

import lombok.Getter;
import lombok.Setter;
import mx.ihsa.constantes.Constantes;

/**
 *
 * @author jrodriguez
 */
@Getter
@Setter
public class AdjuntoVO extends Vo {

    //private String nombre;

    private String descripcion;
    private String url;
    private String tipoArchivo;
    private String peso;
    private String uuid;
    private int idTabla;
    private String requerido;
    private long tamanio;
    private byte[] contenido;
    
    private String urlZip;

    public String getExtension() {

        String nombreArchivo = this.getNombre();

        String extension = null;

        // validar el nombre de archivo contenga un punto y que no sea
        // el último caracter
        if (nombreArchivo != null
                && nombreArchivo.trim().length() > 1
                && nombreArchivo.contains(Constantes.PUNTO)
                && nombreArchivo.lastIndexOf(Constantes.PUNTO) != 0
                && nombreArchivo.lastIndexOf(Constantes.PUNTO) != nombreArchivo.length() - 1) {

            extension = nombreArchivo.substring(nombreArchivo.lastIndexOf(Constantes.PUNTO)).toLowerCase();

        }

        return extension;
    }
    
    public String getNombreUUID() {
        String ret =  null;
        String nombreAux = null;
        String extension = null;
        // validar el nombre de archivo contenga un punto y que no sea
        // el último caracter
        if (this.getNombre() != null
                && this.getNombre().trim().length() > 1
                && this.getNombre().contains(Constantes.PUNTO)
                && this.getNombre().lastIndexOf(Constantes.PUNTO) != 0
                && this.getNombre().lastIndexOf(Constantes.PUNTO) != this.getNombre().length() - 1
                && this.getNombre().contains("UUID")) {

            nombreAux = this.getNombre().substring(0, this.getNombre().indexOf("UUID"));
            extension = this.getNombre().substring(this.getNombre().lastIndexOf(Constantes.PUNTO));
            ret = nombreAux + extension;
        } else {
            ret = this.getNombre();
        }

        return ret;
    }

}
