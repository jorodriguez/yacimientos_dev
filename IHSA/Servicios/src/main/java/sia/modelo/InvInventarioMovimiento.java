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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Aplimovil SA de CV
 */
@Entity
@Table(name = "INV_INVENTARIO_MOVIMIENTO")
@SequenceGenerator(sequenceName = "inv_inventario_movimiento_id_seq", name = "inv_inventario_movimiento_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
@ToString
public class InvInventarioMovimiento implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
@GeneratedValue(generator =  "inv_inventario_movimiento_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @JoinColumn(name = "INVENTARIO", referencedColumnName = "ID")
    @ManyToOne
    private InvInventario inventario;

    @Column(name = "FECHA")
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Column(name = "TIPO_MOVIMIENTO")
    Integer tipoMovimiento;

    @Column(name = "NUMERO_UNIDADES")
    double numeroUnidades; // Este numero es positivo si fue un aumento, o negativo si fue una disminucion

    @JoinColumn(name = "TRANSACCION", referencedColumnName = "ID")
    @ManyToOne
    private InvTransaccion transaccion;

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

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvInventario)) {
            return false;
        }
        InvInventario other = (InvInventario) object;
        return this.id.equals(other.getId());
    }

    public InvInventarioMovimiento(){

    }
}
