/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SI_MOVIMIENTO")
@SequenceGenerator(sequenceName = "si_movimiento_id_seq", name = "si_movimiento_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SiMovimiento.findAll", query = "SELECT s FROM SiMovimiento s")})
@Getter
@Setter
@ToString
public class SiMovimiento implements Serializable {
    @Basic(optional =     false)
    
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional =     false)
    @NotNull
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name =     "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name =     "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @OneToMany(mappedBy = "siMovimiento")
    private Collection<OrdenSiMovimiento> ordenSiMovimientoCollection;
    @OneToMany(mappedBy = "siMovimiento")
    private Collection<RequisicionSiMovimiento> requisicionSiMovimientoCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "si_movimiento_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Size(max = 512)
    @Column(name = "MOTIVO")
    private String motivo;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "siMovimiento")
    private Collection<SgViajeSiMovimiento> sgViajeSiMovimientoCollection;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "SI_OPERACION", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SiOperacion siOperacion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "siMovimiento")
    private Collection<SgPaqueteSiMovimiento> sgPaqueteSiMovimientoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "siMovimiento")
    private Collection<SgSolPaqueteSiMov> sgSolPaqueteSiMovCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "siMovimiento")
    private Collection<SgViajeroSiMovimiento> sgViajeroSiMovimientoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "siMovimiento")
    private Collection<SgSolViajeSiMovimiento> sgSolViajeSiMovimientoCollection;

    public SiMovimiento() {
    }


    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiMovimiento)) {
            return false;
        }
        SiMovimiento other = (SiMovimiento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
}
