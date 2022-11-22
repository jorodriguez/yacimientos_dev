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
import lombok.Data;


   
/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_SOL_VIAJE_INCUM")
@SequenceGenerator(sequenceName = "sg_sol_viaje_incum_id_seq", name = "sg_sol_viaje_incum_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgSolViajeIncum.findAll", query = "SELECT s FROM SgSolViajeIncum s")})
@Data
public class SgSolViajeIncum implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_sol_viaje_incum_seq", strategy = GenerationType.SEQUENCE)
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
    
    @Column(name = "HISTORIAL")
    private boolean historial;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SG_SOLICITUD_VIAJE", referencedColumnName = "ID")
    @ManyToOne
    private SgSolicitudViaje sgSolicitudViaje;
    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;

    public SgSolViajeIncum() {
    }
    
    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgSolViajeIncum)) {
            return false;
        }
        SgSolViajeIncum other = (SgSolViajeIncum) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }   
}
