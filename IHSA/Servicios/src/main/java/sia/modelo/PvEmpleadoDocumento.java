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
@Table(name = "PV_EMPLEADO_DOCUMENTO")
@SequenceGenerator(sequenceName = "pv_empleado_documento_id_seq", name = "pv_empleado_documento_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
@ToString
public class PvEmpleadoDocumento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    @GeneratedValue(generator = "cat_empleado_documento_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;
    @JoinColumn(name = "PV_PROVEEDOR_EMPLEADO", referencedColumnName = "ID")
    @ManyToOne
    private PvProveedorEmpleado pvProveedorEmpleado;
    @JoinColumn(name = "CAT_PROVEEDOR_EMPLEADO", referencedColumnName = "ID")
    @ManyToOne
    private CatEmpleadoDocumento catEmpleadoDocumento;
    @JoinColumn(name = "SI_ADJUNTO", referencedColumnName = "ID")
    @ManyToOne
    private SiAdjunto siAdjunto;
    @Column(name = "VALIDO")
    private boolean valido;

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
        if (!(object instanceof PvEmpleadoDocumento)) {
            return false;
        }
        PvEmpleadoDocumento other = (PvEmpleadoDocumento) object;
        return this.id.equals(other.getId());
    }

    public PvEmpleadoDocumento() {

    }

    public PvEmpleadoDocumento(Integer id) {
        this.id = id;
    }
}
