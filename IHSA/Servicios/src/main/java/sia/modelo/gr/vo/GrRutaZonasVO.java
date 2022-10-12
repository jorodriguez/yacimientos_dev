/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.gr.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.util.UtilSia;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class GrRutaZonasVO {
    
    private int id;
    private RutaTerrestreVo ruta;
    private MapaVO zona;
    private Date fechaGenero;
    private Date horaGenero;
    private boolean activa = false;
    private boolean cancelasr = false;
    private boolean cancelasn = false;
    private int idPunto;
    private GrPuntoVO punto;
    private String secuencia;
    private String codigo;
        
    public String getJson(){        
        return UtilSia.getGson().toJson(this);
    }
}
