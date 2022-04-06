/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_COMPROBANTE")
@SequenceGenerator(sequenceName = "sg_comprobante_id_seq", name = "sg_comprobante_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgComprobante.findAll", query = "SELECT s FROM SgComprobante s")})
public class SgComprobante implements Serializable {
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SI_ADJUNTO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SiAdjunto siAdjunto;
    @JoinColumn(name = "SG_PAGO_SERVICIO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgPagoServicio sgPagoServicio;

    public SgComprobante() {
    }

    public SgComprobante(Integer id) {
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

    public SiAdjunto getSiAdjunto() {
        return siAdjunto;
    }

    public void setSiAdjunto(SiAdjunto siAdjunto) {
        this.siAdjunto = siAdjunto;
    }

    public SgPagoServicio getSgPagoServicio() {
        return sgPagoServicio;
    }

    public void setSgPagoServicio(SgPagoServicio sgPagoServicio) {
        this.sgPagoServicio = sgPagoServicio;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgComprobante)) {
            return false;
        }
        SgComprobante other = (SgComprobante) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SgComprobante[ id=" + id + " ]";
    }
}
