package sia.modelo.vo.inventarios;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;
import sia.modelo.sgl.vo.Vo;

/**
 * VO que representa la información de avisos en el sistema de inventarios
 *
 * @author Aplimovil SA de CV
 */
@Getter
@Setter
public class AvisoVO extends Vo {

    private static final long serialVersionUID = 3152736503917744888L;

    private Date fecha;
    private String asunto;
    private String mensaje;
    private boolean leido;
    private boolean seleccionado;

    public AvisoVO() {
    }

    public AvisoVO(Integer id, Date fecha, String asunto, String mensaje, boolean leido) {
	this.id = id;
	this.fecha = fecha;
	this.asunto = asunto;
	this.mensaje = mensaje;
	this.leido = leido;
    }

    public static AvisoVO nuevo(String asunto, String mensaje) {
	AvisoVO avisoVO = new AvisoVO();
	avisoVO.setAsunto(asunto);
	avisoVO.setMensaje(mensaje);
	avisoVO.setLeido(false);
	avisoVO.setFecha(new Date());
	return avisoVO;
    }
}
