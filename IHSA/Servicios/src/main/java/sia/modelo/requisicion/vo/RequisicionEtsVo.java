/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.requisicion.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jorodriguez
 */

@Getter
@Setter
public class RequisicionEtsVo {
    
       private Integer idReRequisicion;
       private Integer idRequisicion;
       private Integer idAdjunto;
       private Boolean disgregado;
       private Boolean visible;
       private String consecutivo;
       private String url;
       private String nombreAdjunto;
       private String descripcionAdjunto;
       private String tipoArchivo;
       private String peso;
       private String uuid;       
    
}
