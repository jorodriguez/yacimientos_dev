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
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */

@Entity
@Table(name = "OC_ACTIVO_FIJO")
@SequenceGenerator(sequenceName = "oc_activo_fijo_id_seq", name = "oc_activo_fijo_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OcActivoFijo.findAll", query = "SELECT u FROM OcActivoFijo u")})
@Getter
@Setter
public class OcActivoFijo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
@GeneratedValue(generator =  "oc_activo_fijo_seq", strategy = GenerationType.SEQUENCE)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 40)
    @Column(name = "CODIGO")
    private String codigo;   
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "ORDEN", referencedColumnName = "ID")
    @ManyToOne
    private Orden orden;
    @JoinColumn(name = "ORDEN_DETALLE", referencedColumnName = "ID")
    @ManyToOne
    private OrdenDetalle orden_detalle;
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

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof sia.modelo.SiUnidad)) {
            return false;
        }
        sia.modelo.OcActivoFijo  other = (sia.modelo.OcActivoFijo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.OcActivoFijo[ id=" + id + " ]";
    }
    
}


