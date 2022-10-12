/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
@Table(name = "SG_SOL_VIAJE_SI_MOVIMIENTO")
@SequenceGenerator(sequenceName = "sg_sol_viaje_si_movimiento_id_seq", name = "sg_sol_viaje_si_movimiento_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgSolViajeSiMovimiento.findAll", query = "SELECT s FROM SgSolViajeSiMovimiento s")})
@Getter
@Setter
public class SgSolViajeSiMovimiento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_sol_viaje_si_movimiento_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
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
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "SI_MOVIMIENTO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SiMovimiento siMovimiento;
    @JoinColumn(name = "SG_SOLICITUD_VIAJE", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgSolicitudViaje sgSolicitudViaje;

    public SgSolViajeSiMovimiento() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgSolViajeSiMovimiento)) {
            return false;
        }
        SgSolViajeSiMovimiento other = (SgSolViajeSiMovimiento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    
    public String toString() {
        SimpleDateFormat sdfd = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdft = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
            .append("id=").append(this.id)
            .append(", sgSolicitudViaje =").append(this.sgSolicitudViaje != null ? this.sgSolicitudViaje.getId() : null)
            .append(", siMovimiento =").append(this.siMovimiento != null ? this.siMovimiento.getId() : null)
            .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
            .append(", fechaGenero=").append(this.fechaGenero != null ? (sdfd.format(this.fechaGenero)) : null)
            .append(", horaGenero=").append(this.horaGenero != null ? (sdft.format(this.horaGenero)) : null)
            .append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
            .append(", fechaModifico=").append(this.fechaModifico != null ? (sdfd.format(this.fechaModifico)) : null)
            .append(", horaModifico=").append(this.horaModifico != null ? (sdft.format(this.horaModifico)) : null)
            .append(", eliminado=").append(this.eliminado)            
            .append("}");
        
        return sb.toString();
    }    
    
}
