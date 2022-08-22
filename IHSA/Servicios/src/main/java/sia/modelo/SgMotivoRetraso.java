
/*
 *  @Fecha : 15/Octubre/2013
 *  @Modificó : Joel Rodriguez Rojas * 
 *  @Descripción : Se quitó el campo de llave foranea que apuntaba a Sg_solicitud_viaje 
 *  @Actualización : Semaforo
 * 
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
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_MOTIVO_RETRASO")
@SequenceGenerator(sequenceName = "sg_motivo_retraso_id_seq", name = "sg_motivo_retraso_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgMotivoRetraso.findAll", query = "SELECT s FROM SgMotivoRetraso s")})
@Data
public class SgMotivoRetraso implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_motivo_retraso_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "SG_INVITADO", referencedColumnName = "ID")
    @ManyToOne    
    private SgInvitado sgInvitado;
    @JoinColumn(name = "SG_LUGAR", referencedColumnName = "ID")
    @ManyToOne
    private SgLugar sgLugar;
    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;
    @Column(name = "HORA_REUNION")
    @Temporal(TemporalType.TIME)
    private Date horaReunion; 
    @Size(max = 256)
    @Column(name = "JUSTIFICACION_RETRASO")
    private String justificacionRetraso;    
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @NotNull
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
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
    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuario;

    public SgMotivoRetraso() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgMotivoRetraso)) {
            return false;
        }
        SgMotivoRetraso other = (SgMotivoRetraso) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
     
}