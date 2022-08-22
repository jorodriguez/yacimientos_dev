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
@Table(name = "SG_VIAJERO")
@SequenceGenerator(sequenceName = "sg_viajero_id_seq", name = "sg_viajero_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgViajero.findAll", query = "SELECT s FROM SgViajero s")})
@Getter
@Setter
@ToString
public class SgViajero implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_viajero_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "SG_INVITADO", referencedColumnName = "ID")
    @ManyToOne
    private SgInvitado sgInvitado;
    @JoinColumn(name = "SG_SOLICITUD_VIAJE", referencedColumnName = "ID")
    @ManyToOne
    private SgSolicitudViaje sgSolicitudViaje;
    @JoinColumn(name = "SG_VIAJE", referencedColumnName = "ID")
    @ManyToOne
    private SgViaje sgViaje;
    @Size(max = 256)
    @Column(name = "OBSERVACION")
    private String observacion;
    @NotNull
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
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
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    
    @Column(name = "ESTANCIA")
    private boolean estancia;
    
    @Column(name = "REDONDO")
    private boolean redondo;
    @JoinColumn(name = "SG_SOLICITUD_ESTANCIA", referencedColumnName = "ID")
    @ManyToOne  
    private SgSolicitudEstancia sgSolicitudEstancia;
    @JoinColumn(name = "SG_VIAJERO", referencedColumnName = "ID")
    @ManyToOne
    private SgViajero sgViajero;
    
    @Column(name = "GRAUT")
    private boolean grAut;
    @Size(max = 1028)
    @Column(name = "GRAUTOMOTIVO")
    private String grAutMotivo;
    

    public SgViajero() {
    }
    public SgViajero(Integer id) {
        this.id = id;
    }


    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgViajero)) {
            return false;
        }
        SgViajero other = (SgViajero) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}