
package sia.modelo.requisicion.vo;

import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;

/**
 * Se ocupa como el objeto cast para mostrar en la pantalla principal, no contiene ids 
 * @author jorodriguez
 */

@Getter
@Setter
public class RequisicionView {
    
       private Integer id;
       private String consecutivo;
       private String referencia;
       private String fechaRequerida;
       private String prioridad;
       private String siglasCompania;
       private String gerencia;
       private double montoMn;
       private double montoUsd;
       private double montoTotalUsd;
       private String url;
       private String idEstatus;
       private String estatus;
       private String solicita;
       private String fechaSolicito;
       private String revisa;
       private String fechaRevisa;
       private String aprueba;
       private String fechaAprueba;
       private String vistoBueno;
       private String fechaVistoBueno;
       private String cancelo;
       private String fechaCancelo;
       private String asigno;
       private String fechaAsigno;
       private String finalizo;
       private String fechaFinalizo;
       private String comprador;
       private String observaciones;
       private String lugarEntrega;
       private String tipo;
       private String proveedor;
       private String motivoCancelo;
       private String motivoFinalizo;

       public boolean isAprobada(){ return this.fechaAprueba != null; }
       public boolean isRevisada(){ return this.fechaRevisa != null; }
       public boolean isVistoBueno(){ return this.fechaVistoBueno != null; }
       public boolean isCancelada(){ return this.fechaCancelo != null; }
       public boolean isAsignada(){ return this.fechaAsigno != null; }
       public boolean isFinalidada(){ return this.fechaFinalizo != null; }
       
           
}