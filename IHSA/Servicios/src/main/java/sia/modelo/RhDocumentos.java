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
 * @author mluis
 */
@Entity
@Table(name = "RH_DOCUMENTOS")
@SequenceGenerator(sequenceName = "rh_documentos_id_seq", name = "rh_documentos_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "RhDocumentos.findAll", query = "SELECT p FROM RhDocumentos p where p.eliminado = false")
    ,@NamedQuery(name = "RhDocumentos.documentosPeriodicos", query = "SELECT p FROM RhDocumentos p where p.sgPeriodicidad is not null and p.eliminado = false")
    ,@NamedQuery(name = "RhDocumentos.documentosNoPeriodicos", query = "SELECT p FROM RhDocumentos p where p.sgPeriodicidad is null and p.eliminado = false")
})
@Getter
@Setter
public class RhDocumentos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "rh_documentos_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)

    @Column(name = "ID")
    private Integer id;
    @Size(max = 512)
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "MANDATORIO")
    private boolean mandatorio;
    @JoinColumn(name = "SG_PERIODICIDAD", referencedColumnName = "ID")
    @ManyToOne
    private SgPeriodicidad sgPeriodicidad;
    //
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
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

    public RhDocumentos() {
    }

    public RhDocumentos(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RhDocumentos)) {
            return false;
        }
        RhDocumentos other = (RhDocumentos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.PvDocumento[ id=" + id + " ]";
    }

}
