/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "PV_PROVEEDOR_EMPLEADO")
@SequenceGenerator(sequenceName = "pv_proveedor_empleado_id_seq", name = "pv_proveedor_empleado_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PvProveedorEmpleado.findAll", query = "SELECT r FROM PvProveedorEmpleado r")})
@Getter
@Setter
public class PvProveedorEmpleado implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "pv_proveedor_empleado_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)

    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "PROVEEDOR", referencedColumnName = "ID")
    @ManyToOne
    private Proveedor proveedor;
    @JoinColumn(name = "SI_ADJUNTO", referencedColumnName = "ID")
    @ManyToOne
    private SiAdjunto siAdjunto;
    @Column(name = "NUMERO_EMPLEADO")
    private String numeroEmpleado;
    @Column(name = "NSS")
    private String nss;
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "PUESTO")
    private String puesto;
    @Column(name = "FECHA_INGRESO")
    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;
    @Column(name = "FECHA_BAJA")
    @Temporal(TemporalType.DATE)
    private Date fechaBaja;
    @Column(name = "SALARIO_DIARIO")
    private Double salarioDiario;
    @Column(name = "SALARIO_DIARIO_INTEGRADO")
    private Double salarioDiarioIntegrado;
    @Column(name = "FINIQUITO_FIRMADO")
    private boolean finiquitoFirmado;
    @Column(name = "PERSONAL_SINDICALIZADO")
    private boolean personalSindicalizado;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;

    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;

    public PvProveedorEmpleado() {
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PvProveedorEmpleado)) {
            return false;
        }
        PvProveedorEmpleado other = (PvProveedorEmpleado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
