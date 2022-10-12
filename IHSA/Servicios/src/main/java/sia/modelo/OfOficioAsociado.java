
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author esapien
 */
@Entity
@Table(name = "OF_OFICIO_ASOCIADO")
@SequenceGenerator(sequenceName = "of_oficio_asociado_id_seq", name = "of_oficio_asociado_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "OfOficioAsociado.findAll", query = "SELECT o FROM OfOficioAsociado o")})
@Data
public class OfOficioAsociado implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
@GeneratedValue(generator =  "of_oficio_asociado_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional = false)
    @NotNull
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
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "OF_OFICIO_ASOCIADO_A", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private OfOficio ofOficioAsociadoA;
    @JoinColumn(name = "OF_OFICIO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private OfOficio ofOficio;

    public OfOficioAsociado() {
    }

    public OfOficioAsociado(Integer id) {
        this.id = id;
    }

    public OfOficioAsociado(Integer id, Date fechaGenero, Date horaGenero) {
        this.id = id;
        this.fechaGenero = fechaGenero;
        this.horaGenero = horaGenero;
    }
    
    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OfOficioAsociado)) {
            return false;
        }
        OfOficioAsociado other = (OfOficioAsociado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
}
