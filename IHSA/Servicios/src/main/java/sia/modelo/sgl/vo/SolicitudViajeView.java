package sia.modelo.sgl.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jorodriguez
 */
@Getter
@Setter
public class SolicitudViajeView {

    private int id;
    private String codigo;
    private String observacion;
    private String fechaSalida;
    private String fechaRegreso;
    private String origen;
    private String destino;
    private String tipo;
    private String estatus;
    private String gerencia;
    private String motivo;
    private String numeroViajeros;    
    private String motivoRetraso;    
    
    
    public SolicitudViajeView() {
    }
    
    
       
}
