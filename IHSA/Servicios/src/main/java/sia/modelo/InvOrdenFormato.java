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
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sia.modelo.vo.inventarios.TransaccionVO;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "INV_ORDEN_FORMATO")
@SequenceGenerator(sequenceName = "inv_orden_formato_id_seq", name = "inv_orden_formato_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
@ToString
public class InvOrdenFormato implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)    
    @Column(name = "ID")
    @GeneratedValue(generator =  "inv_orden_formato_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @JoinColumn(name = "ORDEN", referencedColumnName = "ID")
    @ManyToOne
    private Orden orden;
    @JoinColumn(name = "SI_ADJUNTO", referencedColumnName = "ID")
    @ManyToOne
    private SiAdjunto siAdjunto;

    @JoinColumn(name = "INV_TIPO_MOVIMIENTO", referencedColumnName = "ID")
    @ManyToOne
    private InvtipoMovmiento invtipoMovmiento;

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
	if (!(object instanceof InvOrdenFormato)) {
	    return false;
	}
	InvOrdenFormato other = (InvOrdenFormato) object;
	return this.id.equals(other.getId());
    }

    public InvOrdenFormato() {

    }

    public InvOrdenFormato(Integer id) {
	this.id = id;
    }

    
}
