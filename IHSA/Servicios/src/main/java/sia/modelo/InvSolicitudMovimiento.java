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
import lombok.ToString;

/**
 *
 * @author mluis
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "INV_SOLICITUD_MOVIMIENTO")
@SequenceGenerator(sequenceName = "inv_solicitud_movimiento_id_seq", name = "solicitud_movimiento_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "InvSolicitudMovimiento.findAll", query = "SELECT o FROM InvSolicitudMovimiento o")})
public class InvSolicitudMovimiento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "solicitud_movimiento_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;

    @JoinColumn(name = "SI_MOVIMIENTO", referencedColumnName = "ID")
    @ManyToOne
    private SiMovimiento siMovimiento;
    @JoinColumn(name = "INV_SOLICITUD_MATERIAL", referencedColumnName = "ID")
    @ManyToOne
    private InvSolicitudMaterial invSolicitudMaterial;
    //
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    //
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
    public InvSolicitudMovimiento() {
    }

    public InvSolicitudMovimiento(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvSolicitudMovimiento)) {
            return false;
        }
        InvSolicitudMovimiento other = (InvSolicitudMovimiento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
