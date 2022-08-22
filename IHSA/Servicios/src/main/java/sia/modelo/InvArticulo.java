package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sia.modelo.vo.inventarios.ArticuloVO;

/**
 *
 * @author Aplimovil SA de CV
 */
@Entity
@Table(name = "INV_ARTICULO")
@SequenceGenerator(sequenceName = "inv_articulo_id_seq", name = "inv_articulo_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
@ToString
@NamedNativeQueries({
    @NamedNativeQuery(name = "InvArticulo.ObtenerCodigoBarrasPorCodigoSKU", query = "SELECT CODIGO_EAN13 FROM INV_ARTICULO WHERE CODIGO = ?1 AND ELIMINADO = ?2"),
    @NamedNativeQuery(name = "InvArticulo.ExisteUnidadesEnInventario", query = "SELECT COUNT(0) FROM INV_INVENTARIO WHERE ARTICULO = ?1 AND NUMERO_UNIDADES > 1 AND ELIMINADO = ?2")
})
public class InvArticulo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)

    @Column(name = "ID")
    @GeneratedValue(generator = "inv_articulo_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Size(max = 30)
    @Column(name = "CODIGO", unique = true)
    private String codigo;
    @Size(max = 13)
    @Column(name = "CODIGO_EAN13")
    private String codigoBarras;
    @Size(max = 2048)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 2048)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @JoinColumn(name = "UNIDAD", referencedColumnName = "ID")
    @ManyToOne
    private SiUnidad unidad;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;

    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @JoinColumn(name = "SI_CATEGORIA", referencedColumnName = "ID")
    @ManyToOne
    private SiCategoria siCategoria;
    @Size(max = 30)
    @Column(name = "CODIGO_INT", unique = true)
    private String codigoInt;
    @Size(max = 128)
    @Column(name = "CATEGORIAS")
    private String categorias;

    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvArticulo)) {
            return false;
        }
        InvArticulo other = (InvArticulo) object;
        return this.id.equals(other.getId());
    }

    public InvArticulo() {

    }

    public InvArticulo(Integer id) {
        this.id = id;
    }

    public InvArticulo(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public InvArticulo(ArticuloVO vo) {
        this.id = vo.getId();
        this.codigo = vo.getCodigo();
        this.nombre = vo.getNombre();
        this.descripcion = vo.getDescripcion();
        this.unidad = new SiUnidad(vo.getUnidadId());
        this.unidad.setNombre(vo.getUnidadNombre());
//        if(vo.getCampoID() > 0){
//            this.apCampo = new ApCampo();
//            this.apCampo.setId(vo.getCampoID());
//        }
    }
}
