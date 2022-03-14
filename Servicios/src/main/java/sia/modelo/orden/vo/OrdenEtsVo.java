/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.orden.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class OrdenEtsVo extends Vo implements Serializable {

    //private String nombre;

    private String descripcion;
    private String url;
    private String tipoArchivo;
    private String peso;
    private String uuid;
    private int idTabla;
    private String requerido;
    private String categoria;
    private int idEtsOrden;

    public String getNombreSinUUID() {
        String nombre = null;
        String extension = null;
        String nombreREal = this.getNombre();
        // validar el nombre de archivo contenga un punto y que no sea
        // el último caracter
        if (this.getNombre() != null
                && this.getNombre().trim().length() > 1
                && this.getNombre().contains(Constantes.PUNTO)
                && this.getNombre().contains("UUID")
                && this.getNombre().lastIndexOf(Constantes.PUNTO) != 0
                && this.getNombre().lastIndexOf(Constantes.PUNTO) != this.getNombre().length() - 1) {

            nombre = this.getNombre().substring(0, this.getNombre().indexOf("UUID"));
            extension = this.getNombre().substring(this.getNombre().lastIndexOf(Constantes.PUNTO)).toLowerCase();
            nombreREal = new StringBuilder().append(nombre).append(extension).toString();
        }else{
            nombreREal = this.getNombre();
        }

        return nombreREal;
    }

}
