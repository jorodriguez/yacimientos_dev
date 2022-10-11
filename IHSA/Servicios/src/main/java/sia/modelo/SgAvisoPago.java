 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author jrodriguez
 */
@Entity
@Table(name = "SG_AVISO_PAGO")
@SequenceGenerator(sequenceName = "sg_aviso_pago_id_seq", name = "sg_aviso_pago_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgAvisoPago.findAll", query = "SELECT s FROM SgAvisoPago s")})
public class SgAvisoPago implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_aviso_pago_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Column(name = "DIA_ESTIMADO_PAGO")
    private Integer diaEstimadoPago;
    @Column(name = "DIA_ANTICIPADO_PAGO")
    private Integer diaAnticipadoPago;
    @Column(name = "FECHA_PROXIMO_AVISO")
    @Temporal(TemporalType.DATE)
    private Date fechaProximoAviso;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @OneToMany(mappedBy = "sgAvisoPago")
    private Collection<SgAvisoPagoStaff> sgAvisoPagoStaffCollection;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;
    @JoinColumn(name = "SG_PERIODICIDAD", referencedColumnName = "ID")
    @ManyToOne
    private SgPeriodicidad sgPeriodicidad;
    @OneToMany(mappedBy = "sgAvisoPago")
    private Collection<SgAvisoPagoOficina> sgAvisoPagoOficinaCollection;

    public SgAvisoPago() {
    }

    public SgAvisoPago(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDiaEstimadoPago() {
        
        return diaEstimadoPago;
    }

    public void setDiaEstimadoPago(Integer diaEstimadoPago) {
        this.diaEstimadoPago = diaEstimadoPago;
    }

    public Integer getDiaAnticipadoPago() {
        return diaAnticipadoPago;
    }

    public void setDiaAnticipadoPago(Integer diaAnticipadoPago) {
        this.diaAnticipadoPago = diaAnticipadoPago;
    }

    public Date getFechaProximoAviso() {
        return fechaProximoAviso;
    }

    public void setFechaProximoAviso(Date fechaProximoAviso) {
        this.fechaProximoAviso = fechaProximoAviso;
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

    public Collection<SgAvisoPagoStaff> getSgAvisoPagoStaffCollection() {
        return sgAvisoPagoStaffCollection;
    }

    public void setSgAvisoPagoStaffCollection(Collection<SgAvisoPagoStaff> sgAvisoPagoStaffCollection) {
        this.sgAvisoPagoStaffCollection = sgAvisoPagoStaffCollection;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public SgTipoEspecifico getSgTipoEspecifico() {
        return sgTipoEspecifico;
    }

    public void setSgTipoEspecifico(SgTipoEspecifico sgTipoEspecifico) {
        this.sgTipoEspecifico = sgTipoEspecifico;
    }

    public SgPeriodicidad getSgPeriodicidad() {
        return sgPeriodicidad;
    }

    public void setSgPeriodicidad(SgPeriodicidad sgPeriodicidad) {
        this.sgPeriodicidad = sgPeriodicidad;
    }

    public Collection<SgAvisoPagoOficina> getSgAvisoPagoOficinaCollection() {
        return sgAvisoPagoOficinaCollection;
    }

    public void setSgAvisoPagoOficinaCollection(Collection<SgAvisoPagoOficina> sgAvisoPagoOficinaCollection) {
        this.sgAvisoPagoOficinaCollection = sgAvisoPagoOficinaCollection;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgAvisoPago)) {
            return false;
        }
        SgAvisoPago other = (SgAvisoPago) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.constantes.sistema.SgAvisoPago[ id=" + id + " ]";
    }
    
}
