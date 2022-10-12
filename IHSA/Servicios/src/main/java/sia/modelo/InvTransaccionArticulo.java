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
import sia.modelo.vo.inventarios.TransaccionArticuloVO;

/**
 *
 * @author Aplimovil SA de CV
 */
@Entity
@Table(name = "INV_TRANSACCION_ARTICULO")
@SequenceGenerator(sequenceName = "inv_transaccion_articulo_id_seq", name = "inv_transaccion_articulo_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
@ToString
public class InvTransaccionArticulo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
@GeneratedValue(generator =  "inv_transaccion_articulo_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @JoinColumn(name = "TRANSACCION", referencedColumnName = "ID")
    @ManyToOne
    private InvTransaccion transaccion;

    @JoinColumn(name = "ARTICULO", referencedColumnName = "ID")
    @ManyToOne
    private InvArticulo articulo;

    @Column(name = "NUMERO_UNIDADES")
    private double numeroUnidades;  // Este numero siempre debera ser positivo

    @Column(name = "IDENTIFICADOR")
    private String identificador;

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
	if (!(object instanceof InvTransaccionArticulo)) {
	    return false;
	}
	InvTransaccionArticulo other = (InvTransaccionArticulo) object;
	return this.id.equals(other.getId());
    }

    public InvTransaccionArticulo() {

    }

    public InvTransaccionArticulo(Integer id) {
	this.id = id;
    }

    public InvTransaccionArticulo(TransaccionArticuloVO vo) {
	this.id = vo.getId();
	this.transaccion = new InvTransaccion(vo.getTransaccionId());
	this.articulo = new InvArticulo(vo.getArticuloId(), vo.getArticuloNombre());
	this.numeroUnidades = vo.getNumeroUnidades();
    }
}
