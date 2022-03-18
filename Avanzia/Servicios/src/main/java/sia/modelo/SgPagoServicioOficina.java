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

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_PAGO_SERVICIO_OFICINA")
@SequenceGenerator(sequenceName = "sg_pago_servicio_oficina_id_seq", name = "sg_pago_servicio_oficina_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgPagoServicioOficina.findAll", query = "SELECT s FROM SgPagoServicioOficina s")})
public class SgPagoServicioOficina implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_pago_servicio_oficina_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SG_PAGO_SERVICIO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgPagoServicio sgPagoServicio;
    @JoinColumn(name = "SG_OFICINA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgOficina sgOficina;

    public SgPagoServicioOficina() {
    }

    public SgPagoServicioOficina(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaGenero() {
        return fechaGenero;
    }

    public void setFechaGenero(Date fechaGenero) {
        this.fechaGenero = fechaGenero;
    }

    public Date getHoraGenero() {
        return horaGenero;
    }

    public void setHoraGenero(Date horaGenero) {
        this.horaGenero = horaGenero;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public SgPagoServicio getSgPagoServicio() {
        return sgPagoServicio;
    }

    public void setSgPagoServicio(SgPagoServicio sgPagoServicio) {
        this.sgPagoServicio = sgPagoServicio;
    }

    public SgOficina getSgOficina() {
        return sgOficina;
    }

    public void setSgOficina(SgOficina sgOficina) {
        this.sgOficina = sgOficina;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgPagoServicioOficina)) {
            return false;
        }
        SgPagoServicioOficina other = (SgPagoServicioOficina) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SgPagoServicioOficina[ id=" + id + " ]";
    }
    
}
