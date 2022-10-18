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
@Table(name = "si_factura_movimiento")
@SequenceGenerator(sequenceName = "si_factura_movimiento_id_seq", name = "factura_movimiento_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SiFacturaMovimiento.findAll", query = "SELECT o FROM SiFacturaMovimiento o")})
public class SiFacturaMovimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "factura_movimiento_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
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
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SI_MOVIMIENTO", referencedColumnName = "ID")
    @ManyToOne
    private SiMovimiento siMovimiento;
    @JoinColumn(name = "SI_FACTURA", referencedColumnName = "ID")
    @ManyToOne
    private SiFactura siFactura;    

    public SiFacturaMovimiento() {
    }

    public SiFacturaMovimiento(Integer id) {
        this.id = id;
    }
    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiFacturaMovimiento)) {
            return false;
        }
        SiFacturaMovimiento other = (SiFacturaMovimiento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
