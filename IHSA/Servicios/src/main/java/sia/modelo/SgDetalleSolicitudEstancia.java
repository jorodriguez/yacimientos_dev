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
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_DETALLE_SOLICITUD_ESTANCIA")
@SequenceGenerator(sequenceName = "sg_detalle_solicitud_estancia_id_seq", name = "sg_detalle_solicitud_estancia_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgDetalleSolicitudEstancia.findAll", query = "SELECT s FROM SgDetalleSolicitudEstancia s")})
@Setter
@Getter
@ToString
public class SgDetalleSolicitudEstancia implements Serializable {

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
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "SG_INVITADO", referencedColumnName = "ID")
    @ManyToOne
    private SgInvitado sgInvitado;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_detalle_solicitud_estancia_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuario;
    @Size(max = 1024)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;
    @JoinColumn(name = "SG_TIPO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgTipo sgTipo;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    
    @Column(name = "REGISTRADO")
    private boolean registrado;
    
    @Column(name = "CANCELADO")
    private boolean cancelado;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SG_SOLICITUD_ESTANCIA", referencedColumnName = "ID")
    @ManyToOne
    private SgSolicitudEstancia sgSolicitudEstancia;

    public SgDetalleSolicitudEstancia() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgDetalleSolicitudEstancia)) {
            return false;
        }
        SgDetalleSolicitudEstancia other = (SgDetalleSolicitudEstancia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
