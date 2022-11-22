package sia.modelo.vo.inventarios;

import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author Aplimovil SA de CV
 */
@Getter
@Setter
public class ArticuloVO extends Vo {

    private static final long serialVersionUID = 1L;

    private String codigo;
    private String codigoInt;
    private String codigoBarras;
    private String descripcion;
    private Integer unidadId;
    private String unidadNombre;
    private Integer campoId;
    private String campoNombre;
    private Integer idRel;
    private boolean editar;
    private boolean selected;
    private String numParte;
    private String categorias;
    private Integer categoriaId;
    private String codigoSat;
    private Double precio;
    private Integer idMoneda;
    private String moneda;

    public ArticuloVO() {
    }

    public ArticuloVO(Integer id, String nombre) {
	this();
	this.id = id;
	this.nombre = nombre;
    }

    public ArticuloVO(Integer id, String codigo, String codigoBarras, String nombre, 
            String descripcion,
	    Integer unidadId, String unidadNombre, Integer campoId, String campoNombre,
	    Integer idRel, boolean editar, String numParte, String categorias, Double precio, Integer idMoneda, String moneda) {
	this.id = id;
	this.codigo = codigo;
	this.codigoBarras = codigoBarras;
	this.nombre = nombre;
	this.descripcion = descripcion;
	this.unidadId = unidadId;
	this.unidadNombre = unidadNombre;
	this.campoId = campoId;
	this.campoNombre = campoNombre;
	this.idRel = idRel;
	this.editar = editar;
	this.numParte = numParte;
	this.categorias = categorias;
        this.precio = precio;
        this.idMoneda = idMoneda;
        this.moneda = moneda;
    }

    public ArticuloVO(Integer id, String codigo, String codigoBarras, String nombre, String descripcion,
	    Integer unidadId, String unidadNombre, Integer campoId, String campoNombre,
	    Integer idRel, Double precio, Integer idMoneda, String moneda) {
	this.id = id;
	this.codigo = codigo;
	this.codigoBarras = codigoBarras;
	this.nombre = nombre;
	this.descripcion = descripcion;
	this.unidadId = unidadId;
	this.unidadNombre = unidadNombre;
	this.campoId = campoId;
	this.campoNombre = campoNombre;
	this.idRel = idRel;
        this.precio = precio;
        this.idMoneda = idMoneda;
        this.moneda = moneda;
    }

    public ArticuloVO(Integer id, String codigoInt, String nombre) {
	this();
	this.id = id;
	this.codigoInt = codigoInt;
	this.nombre = nombre;
    }

    public ArticuloVO(Integer id, String codigo, String nombre, String descripcion, Integer unidadId, String unidadNombre,
	    Integer categoriaId) {
	this.id = id;
	this.codigo = codigo;
	this.nombre = nombre;
	this.descripcion = descripcion;
	this.unidadId = unidadId;
	this.unidadNombre = unidadNombre;
	this.categoriaId = categoriaId;
    }

    public ArticuloVO(Integer id, String codigo, String codigoBarras, String nombre, String descripcion,
	    Integer unidadId, String unidadNombre, boolean editar, String numParte, String categorias) {
	this.id = id;
	this.codigo = codigo;
	this.codigoBarras = codigoBarras;
	this.nombre = nombre;
	this.descripcion = descripcion;
	this.unidadId = unidadId;
	this.unidadNombre = unidadNombre;
	this.editar = editar;
	this.numParte = numParte;
	this.categorias = categorias;
    }

    
    public String toString() {
        return "ArticuloVO{" + "codigo=" + codigo + ", codigoInt=" + codigoInt + ", codigoBarras=" + codigoBarras + ", descripcion=" + descripcion + ", unidadId=" + unidadId + ", unidadNombre=" + unidadNombre + ", campoId=" + campoId + ", campoNombre=" + campoNombre + ", idRel=" + idRel + ", editar=" + editar + ", selected=" + selected + ", numParte=" + numParte + ", categorias=" + categorias + ", categoriaId=" + categoriaId + ", codigoSat=" + codigoSat + '}';
    }
}
