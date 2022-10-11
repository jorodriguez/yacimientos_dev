/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.vo.inventarios;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class DetalleSolicitudMaterialAlmacenVo {
    private int id;
    private int idSolicitudMaterial;
    private int idArticulo;
    private int idInventario;
    private double cantidad;
    private double cantidadRecibida;
    private double disponibles;
    private String articulo;
    private int idUnidad;
    private String unidad;
    private String codigoArt;
    private String referencia;
    private boolean editar;
    private String ubicacion;
    
}
