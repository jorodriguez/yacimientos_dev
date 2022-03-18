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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author ihsa
 */
@Entity
@Table(name = "INV_PISO")
@SequenceGenerator(sequenceName = "inv_piso_id_seq", name = "inv_piso_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
@ToString
public class InvPiso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    @GeneratedValue(generator = "inv_piso_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Size(max = 12)
    @Column(name = "CODIGO")
    private String codigo;

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

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvPiso)) {
            return false;
        }
        InvPiso other = (InvPiso) object;
        return this.id.equals(other.getId());
    }

    public InvPiso() {

    }

    public InvPiso(Integer id) {
        this.id = id;
    }
}
