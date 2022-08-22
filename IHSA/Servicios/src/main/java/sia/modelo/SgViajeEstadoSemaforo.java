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
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_VIAJE_ESTADO_SEMAFORO")
@SequenceGenerator(sequenceName = "sg_viaje_estado_semaforo_id_seq", name = "sg_viaje_estado_semaforo_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgViajeEstadoSemaforo.findAll", query = "SELECT s FROM SgViajeEstadoSemaforo s")})
@Getter
@Setter
public class SgViajeEstadoSemaforo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id 
@GeneratedValue(generator =  "sg_viaje_estado_semaforo_seq", strategy = GenerationType.SEQUENCE)
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
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SG_VIAJE", referencedColumnName = "ID")
    @ManyToOne
    private SgViaje sgViaje;
    @JoinColumn(name = "SG_ESTADO_SEMAFORO", referencedColumnName = "ID")
    @ManyToOne
    private SgEstadoSemaforo sgEstadoSemaforo;

    public SgViajeEstadoSemaforo() {
    }


    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgViajeEstadoSemaforo)) {
            return false;
        }
        SgViajeEstadoSemaforo other = (SgViajeEstadoSemaforo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
}
