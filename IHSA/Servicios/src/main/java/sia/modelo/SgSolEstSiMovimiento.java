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
import lombok.ToString;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_SOL_EST_SI_MOVIMIENTO")
@SequenceGenerator(sequenceName = "sg_sol_est_si_movimiento_id_seq", name = "sg_sol_est_si_movimiento_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgSolEstSiMovimiento.findAll", query = "SELECT s FROM SgSolEstSiMovimiento s")})
@Setter
@Getter
@ToString
public class SgSolEstSiMovimiento implements Serializable {

    private static final long serialVersionUID = 1L;
@GeneratedValue(generator =  "sg_sol_est_si_movimiento_seq", strategy = GenerationType.SEQUENCE)
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional = false)
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
    @NotNull
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @NotNull
    @JoinColumn(name = "SI_MOVIMIENTO", referencedColumnName = "ID")
    @ManyToOne
    private SiMovimiento siMovimiento;
    @NotNull
    @JoinColumn(name = "SG_SOLICITUD_ESTANCIA", referencedColumnName = "ID")
    @ManyToOne
    private SgSolicitudEstancia sgSolicitudEstancia;

    public SgSolEstSiMovimiento() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

   
    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgSolEstSiMovimiento)) {
            return false;
        }
        SgSolEstSiMovimiento other = (SgSolEstSiMovimiento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
