
package sia.modelo.orden.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * Se ocupa como el objeto cast para mostrar en la pantalla principal, no contiene ids 
 * @author jorodriguez
 */

@Getter
@Setter
public class OrdenView {
    
       private Integer id;
       private String consecutivo;
       private String referencia;
       private String devuelta;              
       private String gerencia;
       private String consecutivoRequisicion;
       private String observaciones;
       private String siglasCompania;
       private double subtotal;
       private double total;                    
       private String estatus;       
       private String fechaSolicito;
       private String moneda;       
       private String proveedor;       
       private String url;       
       private String cuentaContable;       
       private String comprador;       
       private String solicita;       
       private String fechaSolicita;       
       private String vistoBueno;       
       private String fechaVistoBueno;       
       private String revisa;       
       private String fechaRevisa;       
       private String aprueba;       
       private String fechaAprueba;       
       private String autoriza;       
       private String fechaAutoriza;       
       private Boolean campoConCartaIntencion;       
       private Boolean esProveedorSinCarta;       
       private String aceptaCartaIntencion;       
       private String fechaAceptaCartaIntencion;       
       private String revisaJuridico;       
       private String fechaRevisaJuridico;       
       private String enviaProveedor;       
       private String fechaEnviaProveedor;           
       

}