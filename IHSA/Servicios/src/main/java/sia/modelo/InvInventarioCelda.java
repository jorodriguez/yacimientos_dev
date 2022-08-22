package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author ihsa
 */
@Entity
@Table(name = "INV_INVENTARIO_CELDA")
@SequenceGenerator(sequenceName = "inv_inventario_celda_id_seq", name = "inv_inv_celda_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
@ToString
@NamedQueries({
    @NamedQuery(name = "InvInventarioCelda.findAll", query = "SELECT a FROM InvInventarioCelda a")
    ,
    @NamedQuery(name = "InvInventarioCelda.traerPorInventario", query = "select ic from InvInventarioCelda ic where ic.invInventario = ?1 and ic.eliminado = false")
})
public class InvInventarioCelda implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    @GeneratedValue(generator = "inv_inv_celda_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @JoinColumn(name = "INV_INVENTARIO", referencedColumnName = "ID")
    @ManyToOne
    private InvInventario invInventario;

    @JoinColumn(name = "INV_CELDA", referencedColumnName = "ID")
    @ManyToOne
    private InvCelda invCelda;

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

    @Column(name = "ELIMINADO")
    private boolean eliminado;
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
        if (!(object instanceof InvInventarioCelda)) {
            return false;
        }
        InvInventarioCelda other = (InvInventarioCelda) object;
        return this.id.equals(other.getId());
    }

    public InvInventarioCelda() {

    }

    public InvInventarioCelda(Integer id) {
        this.id = id;
    }
}
