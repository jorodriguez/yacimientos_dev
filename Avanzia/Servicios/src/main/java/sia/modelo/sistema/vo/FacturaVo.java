/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sistema.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.orden.vo.OrdenEtsVo;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class FacturaVo implements Serializable {

    private int id;
    private int idProveedor;
    private String proveedor;
    private BigDecimal monto;
    private int idMoneda;
    private String moneda;
    private String concepto;
    private String folio;
    private String observacion;
    private Date fechaEmision;
    private int idAdjunto;
    private String uuId;
    private String urlArchivo;
    private String adjunto;
    private int idRelacion;
    private int cantidad;
    private Date fechaGenero;
    //
    private int idStatusFactura;
    private int idStatus;
    private String status;
    private int idCampo;
    private String rfcCompania;
    private String compania;
    //
    private List<FacturaDetalleVo> detalleFactura = new ArrayList<>();
    //
    private String codigoOrden;
    private String pedidoNav;
    private boolean selected;
    private boolean leida;
    private String campo;
    private int idMovimiento;
    private String motivo;
    //
    private int idFactura;
    private String codigoUsoCfdi;
    private String metodoPago;
    private String formaPago;
    private String folioFiscal;
    //
    private String tipoNotaCredito;
    private int idUsoCfdi;
    private String nombreUsoCfdi;
    private String tipoComprobante;

    private BigDecimal subTotal;
    private BigDecimal subTotalPesos;
    private BigDecimal tipoCambio;
    private String poliza;
    private String proveedorRfc;
    private String urlPdfCompra;
    private String polizaPago;
    private Date fechaEstatus;
    private int idGerencia;
    private String gerencia;

    private Date fechaCreada;
    private Date fechaEnvcnn;
    private Date fechaAcecnn;
    private Date fechaAcefin;
    
    private boolean rechazoCnn;
    private boolean rechazoFin;
    
    private int comprobantePago;
    private int complementoPago;
    private int complementoPagoPdf;    
    private String terminoPago;
    private List<OrdenEtsVo> repses = new ArrayList<>();
    private boolean aceptaAvanzia; 
    private boolean notaCredito;
    private BigDecimal porcentaje;
    
    private List<FacturaAdjuntoVo> soportesNC = new ArrayList<>();
    private int soportesNCSize;
    
    private boolean aceptaAvanziaNc; 

    public Date getFechaStatus() {
        Date fecha = new Date();
        switch (this.getIdStatus()) {
            case 710:
                fecha = this.getFechaCreada();
                break;
            case 720:
                fecha = this.getFechaEnvcnn();
                break;
            case 730:
                fecha = this.getFechaAcecnn();
                break;
            case 740:
                fecha = this.getFechaAcefin();
                break;

            default:
                fecha = this.getFechaEstatus();
        }

        return fecha;
    }

}
