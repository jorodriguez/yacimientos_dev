/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.dominio.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jrodriguez
 */
@Getter
@Setter
public class AdjuntoTagVo {

    private int id;
    private int idTag;
    private String tag;
    private int idAdjunto;
    private String nombreArchivo;
    private String ruta;

}
