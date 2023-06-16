package mx.ihsa.dominio.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase base para todas las clases de VO de la plataforma SIA.
 *
 * @author jrodriguez
 */
@Getter
@Setter
public class Vo implements Serializable {

    public Integer id;
    private String genero;
    private Date fechaGenero;
    private Date horaGenero;
    private String modifico;
    private Date fechaModifico;
    private Date horaModifico;
    private boolean eliminado;
    public String nombre;
    private String descripcion;
    private boolean modificar;
    private int idTabla;
    private int idRelacion;
    private String nombreRelacion;
    private String codigo;

    public Vo() {
    }

    public Vo(Integer id, String nombre) {
	this.id = id;
	this.nombre = nombre;
    }
}
