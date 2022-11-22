/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
@Entity
@Table(name = "INV_ESTADO_APROBACION_SOLICITUD")
@SequenceGenerator(sequenceName = "inv_estado_aprobacion_solicitud_id_seq", name = "estado_aprobacion_solicitud_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "InvEstadoAprobacionSolicitud.findAll", query = "SELECT s FROM InvEstadoAprobacionSolicitud s"),
    @NamedQuery(name = "InvEstadoAprobacionSolicitud.findFacturaStatus", query = "SELECT s FROM InvEstadoAprobacionSolicitud s where s.invSolicitudMaterial.id = ?1 and s.estatus.id = ?2 and s.eliminado = false and s.actual = true ")
})
public class InvEstadoAprobacionSolicitud implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "estado_aprobacion_solicitud_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "INV_SOLICITUD_MATERIAL", referencedColumnName = "ID")
    @ManyToOne
    private InvSolicitudMaterial invSolicitudMaterial;
    
    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuario;
    
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatus;
    
    @JoinColumn(name = "SI_ROL", referencedColumnName = "ID")
    @ManyToOne
    private SiRol siRol;
    
    @Column(name = "PUESTO")
    private boolean puesto;
    
    @Column(name = "ACTUAL")
    private boolean actual;
    
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    
    public InvEstadoAprobacionSolicitud() {
    }

    public InvEstadoAprobacionSolicitud(Integer id) {
        this.id = id;
    }

  

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvEstadoAprobacionSolicitud)) {
            return false;
        }
        InvEstadoAprobacionSolicitud other = (InvEstadoAprobacionSolicitud) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
}
