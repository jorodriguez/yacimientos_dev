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
import sia.constantes.Constantes;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_VIAJE_FACTURA")
@SequenceGenerator(sequenceName = "sg_viaje_factura_id_seq", name = "sg_viaje_factura_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgViajeFactura.findAll", query = "SELECT s FROM SgViajeFactura s")})
@Getter
@Setter
public class SgViajeFactura implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_viaje_factura_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "SG_VIAJE", referencedColumnName = "ID")
    @ManyToOne
    private SgViaje sgViaje;
    @JoinColumn(name = "SG_VIAJERO", referencedColumnName = "ID")
    @ManyToOne
    private SgViajero sgViajero;
    @JoinColumn(name = "SI_FACTURA", referencedColumnName = "ID")
    @ManyToOne
    private SiFactura siFactura;
    //
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

    public SgViajeFactura() {
    }

    
    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgViajeFactura)) {
            return false;
        }
        SgViajeFactura other = (SgViajeFactura) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append("{");
        sb.append("id=").append(getId());
        sb.append(", sgViaje =").append(sgViaje.getId());
        sb.append(", sgViajero =").append(sgViajero.getId());
        sb.append(", siFactura =").append(siFactura.getId());
        sb.append(", genero=").append(getGenero() != null ? getGenero().getId() : null);
        sb.append(", fechaGenero=").append(getFechaGenero() != null ? (Constantes.FMT_ddMMyyy.format(getFechaGenero())) : null);
        sb.append(", horaGenero=").append(getHoraGenero() != null ? (Constantes.FMT_HHmmss.format(getHoraGenero())) : null);
        sb.append(", modifico=").append(getModifico() != null ? getModifico().getId() : null);
        sb.append(", fechaModifico=").append(getFechaModifico() != null ? (Constantes.FMT_ddMMyyy.format(getFechaModifico())) : null);
        sb.append(", horaModifico=").append(getHoraModifico() != null ? (Constantes.FMT_HHmmss.format(getHoraModifico())) : null);
        sb.append(", eliminado=").append(isEliminado());
        sb.append("}");

        return sb.toString();
    }
}
