/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_TIPO_SOLICITUD_VIAJE")
@SequenceGenerator(sequenceName = "sg_tipo_solicitud_viaje_id_seq", name = "sg_tipo_solicitud_viaje_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgTipoSolicitudViaje.findAll", query = "SELECT s FROM SgTipoSolicitudViaje s")})
public @Data
class SgTipoSolicitudViaje implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_tipo_solicitud_viaje_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 50)
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "HORAS_ANTICIPACION")
    private Integer horasAnticipacion;
    @Column(name = "HORA_MAXIMA")
    private Integer horaMaxina;
    @JoinColumn(name = "SG_TIPO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipo sgTipo;
    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
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

    public SgTipoSolicitudViaje() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgTipoSolicitudViaje)) {
            return false;
        }
        SgTipoSolicitudViaje other = (SgTipoSolicitudViaje) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}