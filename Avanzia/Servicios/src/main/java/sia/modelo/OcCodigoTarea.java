/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "OC_CODIGO_TAREA")
@SequenceGenerator(sequenceName = "oc_codigo_tarea_id_seq", name = "oc_codigo_tarea_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OcCodigoTarea.findAll", query = "SELECT u FROM OcCodigoTarea u where u.eliminado = false"),
       @NamedQuery(name = "OcCodigoTarea.buscarPorCodigo", query = "SELECT u FROM OcCodigoTarea u where upper(u.nombre) = upper(?1) and u.eliminado = false")
})
@Getter
@Setter
public class OcCodigoTarea implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(generator = "oc_codigo_tarea_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Integer id;
    @Size(max = 40)
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;

    @Column(name = "ELIMINADO")
    private boolean eliminado;

    public OcCodigoTarea() {
    }

    public OcCodigoTarea(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof sia.modelo.OcCodigoTarea)) {
            return false;
        }
        sia.modelo.OcCodigoTarea other = (sia.modelo.OcCodigoTarea) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    
    public String toString() {
        return "sia.modelo.OcCodigoTarea[ id=" + id + " ]";
    }
}
