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
 * @author jrodriguez
 */
@Entity
@Table(name = "SG_AVISO_PAGO_OFICINA")
@SequenceGenerator(sequenceName = "sg_aviso_pago_oficina_id_seq", name = "sg_aviso_pago_oficina_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgAvisoPagoOficina.findAll", query = "SELECT s FROM SgAvisoPagoOficina s")})
public class SgAvisoPagoOficina implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_aviso_pago_oficina_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SG_OFICINA", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina sgOficina;
    @JoinColumn(name = "SG_AVISO_PAGO", referencedColumnName = "ID")
    @ManyToOne
    private SgAvisoPago sgAvisoPago;

    @Column(name = "ELIMINADO")
    private boolean eliminado;
    public SgAvisoPagoOficina() {
    }

    public SgAvisoPagoOficina(Integer id) {
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

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public SgOficina getSgOficina() {
        return sgOficina;
    }

    public void setSgOficina(SgOficina sgOficina) {
        this.sgOficina = sgOficina;
    }

    public SgAvisoPago getSgAvisoPago() {
        return sgAvisoPago;
    }

    public void setSgAvisoPago(SgAvisoPago sgAvisoPago) {
        this.sgAvisoPago = sgAvisoPago;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgAvisoPagoOficina)) {
            return false;
        }
        SgAvisoPagoOficina other = (SgAvisoPagoOficina) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.constantes.sistema.SgAvisoPagoOficina[ id=" + id + " ]";
    }

    /**
     * @return the eliminado
     */
    public boolean isEliminado() {
        return eliminado;
    }

    /**
     * @param eliminado the eliminado to set
     */
    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
    
}
