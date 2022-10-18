/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
@Entity
@Table(name = "SI_INCIDENCIA_VEHICULO")
@SequenceGenerator(sequenceName = "si_incidencia_vehiculo_id_seq", name = "si_incidencia_vehiculo_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SiIncidenciaVehiculo.findAll", query = "SELECT s FROM SiIncidenciaVehiculo s")})
public class SiIncidenciaVehiculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "si_incidencia_vehiculo_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
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
    @JoinColumn(name = "SI_INCIDENCIA", referencedColumnName = "ID")
    @ManyToOne
    private SiIncidencia siIncidencia;
    @JoinColumn(name = "SG_VEHICULO", referencedColumnName = "ID")
    @ManyToOne
    private SgVehiculo sgVehiculo;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;

    public SiIncidenciaVehiculo() {
    }

    public SiIncidenciaVehiculo(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiIncidenciaVehiculo)) {
            return false;
        }
        SiIncidenciaVehiculo other = (SiIncidenciaVehiculo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SiIncidenciaVehiculo[ id=" + id + " ]";
    }
}
