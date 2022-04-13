
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
@Table(name = "OF_OFICIO_USUARIO")
@SequenceGenerator(sequenceName = "of_oficio_usuario_id_seq", name = "of_oficio_usuario_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "OfOficioUsuario.findAll", query = "SELECT o FROM OfOficioUsuario o")})
@Data
public class OfOficioUsuario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
@GeneratedValue(generator =  "of_oficio_usuario_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "OF_OFICIO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private OfOficio ofOficio;
    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario usuario;
    
    
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario genero;
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
    
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    
    
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    

    public OfOficioUsuario() {
    }

    public OfOficioUsuario(Integer id) {
        this.id = id;
    }

    public OfOficioUsuario(Integer id, Date fechaGenero, Date horaGenero) {
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
        if (!(object instanceof OfOficioUsuario)) {
            return false;
        }
        OfOficioUsuario other = (OfOficioUsuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
}
