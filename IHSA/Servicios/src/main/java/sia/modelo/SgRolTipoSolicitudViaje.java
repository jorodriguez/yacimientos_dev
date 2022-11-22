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
import lombok.ToString;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_ROL_TIPO_SOLICITUD_VIAJE")
@SequenceGenerator(sequenceName = "sg_rol_tipo_solicitud_viaje_id_seq", name = "sg_rol_tipo_solicitud_viaje_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgRolTipoSolicitudViaje.findAll", query = "SELECT s FROM SgRolTipoSolicitudViaje s")})
@Getter
@Setter
@ToString
public class SgRolTipoSolicitudViaje implements Serializable {
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
    @JoinColumn(name = "SG_TIPO_SOLICITUD_VIAJE", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoSolicitudViaje sgTipoSolicitudViaje;
    @JoinColumn(name = "SI_ROL", referencedColumnName = "ID")
    @ManyToOne
    private SiRol siRol;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_rol_tipo_solicitud_viaje_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;

    public SgRolTipoSolicitudViaje() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgRolTipoSolicitudViaje)) {
            return false;
        }
        SgRolTipoSolicitudViaje other = (SgRolTipoSolicitudViaje) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }


}
