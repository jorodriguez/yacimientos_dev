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
import lombok.Data;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_VIAJE_LUGAR")
@SequenceGenerator(sequenceName = "sg_viaje_lugar_id_seq", name = "sg_viaje_lugar_seq", allocationSize = 1)
@Data
public class SgViajeLugar implements Serializable {
    @Column(name =     "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name =     "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name =     "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name =     "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @OneToMany(mappedBy = "sgViajeLugar")
    private Collection<SgViaje> sgViajeCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_viaje_lugar_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "SG_SOLICITUD_VIAJE", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgSolicitudViaje sgSolicitudViaje; 
    @JoinColumn(name = "SG_LUGAR", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgLugar sgLugar;            
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;    

    public SgViajeLugar() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

  

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgViajeLugar)) {
            return false;
        }
        SgViajeLugar other = (SgViajeLugar) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
}
