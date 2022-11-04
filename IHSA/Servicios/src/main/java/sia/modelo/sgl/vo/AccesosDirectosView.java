package sia.modelo.sgl.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jorodriguez
 */
@Getter
@Setter
public class AccesosDirectosView {

    private String rutaModulo;
    private String rutaOpcion;
    private String etiqueta;
    private String icono;    
    
    public AccesosDirectosView() {
    }

    @Builder
    public AccesosDirectosView(String rutaModulo, String rutaOpcion, String etiqueta, String icono) {
        this.rutaModulo = rutaModulo;
        this.rutaOpcion = rutaOpcion;
        this.etiqueta = etiqueta;
        this.icono = icono;
    }
       
       
}
