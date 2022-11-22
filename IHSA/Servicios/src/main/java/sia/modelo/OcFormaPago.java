package sia.modelo;
//

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "OC_FORMA_PAGO")
@SequenceGenerator(sequenceName = "oc_forma_pago_id_seq", name = "oc_forma_pago_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OcFormaPago.findAll", query = "SELECT u FROM OcFormaPago u where u.eliminado = 'False' order by u.nombre asc")
    ,@NamedQuery(name = "OcFormaPago.traerPorNombre", query = "SELECT u FROM OcFormaPago u where u.nombre = ?1 and u.eliminado = 'False' order by u.nombre asc")
    ,@NamedQuery(name = "OcFormaPago.traerPorCodigo", query = "SELECT u FROM OcFormaPago u where u.codigo = ?1 and u.eliminado = 'False' order by u.nombre asc")
})
@Getter
@Setter
public class OcFormaPago implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 512)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 12)
    @Column(name = "CODIGO")
    private String codigo;
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
//

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        if (!(object instanceof OcFormaPago)) {
            return false;
        }
        OcFormaPago other = (OcFormaPago) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.OcFormaPago[ id=" + id + " ]";
    }
}
