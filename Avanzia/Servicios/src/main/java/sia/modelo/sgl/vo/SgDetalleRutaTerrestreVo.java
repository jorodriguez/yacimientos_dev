/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author b75ckd35th
 */
@Getter
@Setter
public class SgDetalleRutaTerrestreVo extends Vo {

    private int idSgOficina;
    private int idLugar;
    private int idSgRutaTerrestre;
    private String nombreSgOficina;
    private String nombreLugar;
    private boolean destino;
    private String destinoCiudad;
    private int idCiudad;
    private String ciudad;
    private String latitud;
    private String longitud;
}
