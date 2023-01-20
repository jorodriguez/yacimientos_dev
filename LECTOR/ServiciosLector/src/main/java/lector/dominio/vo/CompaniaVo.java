/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.dominio.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class CompaniaVo implements Serializable {
    private int idProveedorCompania;
    private int idProveedor;
   private String rfcCompania;
   private String nombre;
   private String siglas;
   private String numeroReferencia;
   private String requisitoFactura;
   private boolean selected;
   private boolean editar;
    
}
