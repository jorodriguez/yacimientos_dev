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
import sia.modelo.vo.inventarios.InventarioVO;

/**
 *
 * @author Aplimovil SA de CV
 */
@Entity
@Table(name = "INV_INVENTARIO")
@SequenceGenerator(sequenceName = "inv_inventario_id_seq", name = "inv_inventario_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
@ToString
public class InvInventario implements Serializable {

    private static final long serialVersionUID = 1L;
@GeneratedValue(generator =  "inv_inventario_seq", strategy = GenerationType.SEQUENCE)

    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;

    @JoinColumn(name = "ALMACEN", referencedColumnName = "ID")
    @ManyToOne
    private InvAlmacen almacen;

    @JoinColumn(name = "ARTICULO", referencedColumnName = "ID")
    @ManyToOne
    private InvArticulo articulo;

    @Column(name = "NUMERO_UNIDADES")
    private double numeroUnidades;

    @Column(name = "MINIMO_UNIDADES")
    private double minimoUnidades;

    @Column(name = "MAXIMO_DE_INVENTARIO")
    private double maximoDeInventario;

    @Column(name = "PUNTO_DE_REORDEN")
    private double puntoDeReorden;

    @Column(name = "FECHA_ULTIMA_REVISION")
    @Temporal(TemporalType.DATE)
    private Date fechaUltimaRevision;

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

    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;

    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;

    
    @Column(name = "ELIMINADO")
    private boolean eliminado;

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof InvInventario)) {
	    return false;
	}
	InvInventario other = (InvInventario) object;
	return this.id.equals(other.getId());
    }

    public InvInventario() {

    }

    public InvInventario(Integer id) {
	this.id = id;
    }

    public InvInventario(InventarioVO vo) {
	this.id = vo.getId();
	this.almacen = new InvAlmacen(vo.getAlmacenId(), vo.getAlmacenNombre());
	this.articulo = new InvArticulo(vo.getArticuloId(), vo.getArticuloNombre());
	this.numeroUnidades = vo.getNumeroUnidades();
	this.minimoUnidades = vo.getMinimoUnidades();
	this.fechaUltimaRevision = vo.getFechaUltimaRevision();
    }
}
