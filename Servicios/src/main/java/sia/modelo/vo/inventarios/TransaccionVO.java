package sia.modelo.vo.inventarios;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author Aplimovil SA de CV
 */
@Getter
@Setter
public class TransaccionVO extends Vo {

    private static final long serialVersionUID = 1L;

    private Integer almacenId;
    private String almacenNombre;
    private Integer tipoMovimiento;
    private Integer traspasoAlmacenDestinoId;
    private Date fecha;
    private Integer numeroArticulos;
    private String notas;
    private String folioOrdenCompra;
    private String folioRemision;
    private String motivoRechazo;
    private Integer status;
    private String generoId;
    private String generoNombre;
    private String modificoId;
    private String modificoNombre;
    private Date fechaInicio;
    private Date fechaFin;
    private int idCampo;
    private String campo;
    private int idSolicitud;

    public TransaccionVO() {

    }

    public TransaccionVO(Integer id) {
	this.id = id;
    }

    public TransaccionVO(Integer id, Integer almacenId, String almacenNombre, Integer tipoMovimiento,
	    Integer traspasoAlmacenDestinoId, Date fecha, Integer numeroArticulos, String notas,
	    String folioOrdenCompra, String folioRemision, String motivoRechazo, Integer status,
	    String generoId, String generoNombre, String modificoId, String modificoNombre,
	    Date fechaGenero, Integer idCampo, String campo) {
	this.id = id;
	this.almacenId = almacenId;
	this.almacenNombre = almacenNombre;
	this.tipoMovimiento = tipoMovimiento;
	this.traspasoAlmacenDestinoId = traspasoAlmacenDestinoId;
	this.fecha = fecha;
	this.numeroArticulos = numeroArticulos;
	this.notas = notas;
	this.folioOrdenCompra = folioOrdenCompra;
	this.folioRemision = folioRemision;
	this.motivoRechazo = motivoRechazo;
	this.status = status;
	this.generoId = generoId;
	this.generoNombre = generoNombre;
	this.modificoId = modificoId;
	this.modificoNombre = modificoNombre;
	setFechaGenero(fechaGenero);
	this.idCampo = idCampo;
	this.campo = campo;
    }
    
    public TransaccionVO(Integer id, Integer almacenId, String almacenNombre, Integer tipoMovimiento,
	    Integer traspasoAlmacenDestinoId, Date fecha, Integer numeroArticulos, String notas,
	    String folioOrdenCompra, String folioRemision, String motivoRechazo, Integer status,
	    String generoId, String generoNombre, String modificoId, String modificoNombre,
	    Date fechaGenero, Integer idCampo, String campo, int idSolicitud) {
	this.id = id;
	this.almacenId = almacenId;
	this.almacenNombre = almacenNombre;
	this.tipoMovimiento = tipoMovimiento;
	this.traspasoAlmacenDestinoId = traspasoAlmacenDestinoId;
	this.fecha = fecha;
	this.numeroArticulos = numeroArticulos;
	this.notas = notas;
	this.folioOrdenCompra = folioOrdenCompra;
	this.folioRemision = folioRemision;
	this.motivoRechazo = motivoRechazo;
	this.status = status;
	this.generoId = generoId;
	this.generoNombre = generoNombre;
	this.modificoId = modificoId;
	this.modificoNombre = modificoNombre;
	setFechaGenero(fechaGenero);
	this.idCampo = idCampo;
	this.campo = campo;
        this.idSolicitud = idSolicitud;
    }

    public String getFechaConFormato() {
	return Constantes.FMT_ddMMyyy.format(this.fecha);
    }
}
