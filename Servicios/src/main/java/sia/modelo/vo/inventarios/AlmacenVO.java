package sia.modelo.vo.inventarios;

import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;
import sia.modelo.usuario.vo.UsuarioVO;

/**
 *
 * @author Aplimovil SA de CV
 */
@Getter
@Setter
public class AlmacenVO extends Vo {

    private static final long serialVersionUID = 1L;

    private String descripcion;
    private String responsable1UsuarioId;
    private String responsable1Nombre;
    private String responsable1Email;
    private String responsable2UsuarioId;
    private String responsable2Nombre;
    private String responsable2Email;
    private String supervisorUsuarioId;
    private String supervisorNombre;
    private String supervisorEmail;
    private Integer idCampo;
    private String campo;

    public AlmacenVO() {

    }

    public AlmacenVO(Integer id, String nombre, String descripcion, String responsable1UsuarioId,
	    String responsable1Nombre, String responsable1Email, String responsable2Id,
	    String responsable2Nombre, String responsable2Email, Integer idCampo, String campo, String supervisorUsuarioId,
	    String supervisorNombre, String supervisorEmail) {
	this.id = id;
	this.nombre = nombre;
	this.descripcion = descripcion;
	this.responsable1UsuarioId = responsable1UsuarioId;
	this.responsable1Nombre = responsable1Nombre;
	this.responsable1Email = responsable1Email;
	this.responsable2UsuarioId = responsable2Id;
	this.responsable2Nombre = responsable2Nombre;
	this.responsable2Email = responsable2Email;
	this.supervisorUsuarioId = supervisorUsuarioId;
	this.supervisorNombre = supervisorNombre;
	this.supervisorEmail = supervisorEmail;
	this.idCampo = idCampo;
	this.campo = campo;
    }

    public AlmacenVO(Integer id, String nombre, String descripcion, UsuarioVO responsable1, UsuarioVO responsable2, Integer idCampo,
	    String campo,
	    UsuarioVO supervisor) {

	this.id = id;
	this.nombre = nombre;
	this.descripcion = descripcion;
	this.responsable1UsuarioId = responsable1.getId();
	this.responsable1Nombre = responsable1.getNombre();
	this.responsable1Email = responsable1.getMail();
	this.responsable2UsuarioId = responsable2.getId();
	this.responsable2Nombre = responsable2.getNombre();
	this.responsable2Email = responsable2.getMail();
	this.supervisorUsuarioId = supervisor.getId();
	this.supervisorNombre = supervisor.getNombre();
	this.supervisorEmail = supervisor.getMail();
	this.idCampo = idCampo;
	this.campo = campo;
    }
}
