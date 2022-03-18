/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jcarranza
 */

@Getter
@Setter
@Entity
@Table(name = "oc_presupuesto_movimientos")
@SequenceGenerator(sequenceName = "oc_presupuesto_movimientos_id_seq", name = "oc_presupuesto_movimientos_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "OcPresupuestoMovimientos.findAll", query = "SELECT s FROM OcPresupuestoMovimientos s")})
public class OcPresupuestoMovimientos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "oc_presupuesto_movimientos_seq", strategy = GenerationType.SEQUENCE)
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
    @JoinColumn(name = "OC_PRESUPUESTO", referencedColumnName = "ID")
    @ManyToOne
    private OcPresupuesto ocPresupuesto;
    
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;

    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;

    @Column(name = "ACCION")
    private String accion;

    public OcPresupuestoMovimientos() {
    }

    public OcPresupuestoMovimientos(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OcPresupuestoMovimientos)) {
            return false;
        }
        OcPresupuestoMovimientos other = (OcPresupuestoMovimientos) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    
    public String toString() {
        return "sia.modelo.OcPresupuestoMovimientos[ id=" + id + " ]";
    }

}

