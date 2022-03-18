/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.contrato.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.vo.inventarios.ArticuloVO;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class ConvenioArticuloVo extends ArticuloVO{
    private int idConvenioArticulo;
    private int idConvenio;
    private int idProveedor;
    private String proveedor;
    private double cantidad;
    private double precioUnitario;
    private double importe;
    private String item;
    private String alcance;
    private Boolean guardado = false;
    private Boolean registrado = false;
    private int convenioArticulo; 
    private String convenioArticuloCabecera;    
    private List<ConvenioArticuloVo> listaArticuloConvenio = new ArrayList<>();
    private String convenio;    
    private String nombreConvenio;
    private OrdenDetalleVO ordenDetalleVo = new OrdenDetalleVO();
    private Integer idMoneda;
    private String moneda;
    private int idArticulo;
    private Date fecha;
    private String etiquetaItem;
    private int idConvenioMarco;
    private String articuloCodigoInterno;
    
}
