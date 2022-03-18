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
import lombok.Data;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_ITINERARIO")
@SequenceGenerator(sequenceName = "sg_itinerario_id_seq", name = "sg_itinerario_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgItinerario.findAll", query = "SELECT s FROM SgItinerario s")})
@Data
public class SgItinerario implements Serializable {
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
    @OneToMany(mappedBy = "sgItinerario")
    private Collection<SgViaje> sgViajeCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_itinerario_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @NotNull
    @JoinColumn(name = "SI_CIUDAD_ORIGEN", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SiCiudad siCiudadOrigen;        
    @NotNull
    @JoinColumn(name = "SI_CIUDAD_DESTINO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SiCiudad siCiudadDestino;     
    @NotNull
    @JoinColumn(name = "SG_SOLICITUD_VIAJE", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgSolicitudViaje sgSolicitudViaje;
    
    @Column(name = "IDA")
    private boolean ida;     
    @NotNull
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    
    @Column(name = "NOTIFICADO")
    private boolean notificado;   

    
    public SgItinerario() {
    }


    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgItinerario)) {
            return false;
        }
        SgItinerario other = (SgItinerario) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }


}