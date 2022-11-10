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
@Table(name = "CAT_EMPLEADO_DOCUMENTO")
@SequenceGenerator(sequenceName = "cat_empleado_documento_id_seq", name = "cat_empleado_documento_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
@ToString
public class CatEmpleadoDocumento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    @GeneratedValue(generator = "cat_empleado_documento_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Column(name = "NOMBRE")
    private String nombre;

    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;

    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
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

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CatEmpleadoDocumento)) {
            return false;
        }
        CatEmpleadoDocumento other = (CatEmpleadoDocumento) object;
        return this.id.equals(other.getId());
    }

    public CatEmpleadoDocumento() {

    }

    public CatEmpleadoDocumento(Integer id) {
        this.id = id;
    }
}
