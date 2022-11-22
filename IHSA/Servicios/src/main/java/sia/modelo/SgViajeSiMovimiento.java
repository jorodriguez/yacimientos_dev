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
import sia.constantes.Constantes;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_VIAJE_SI_MOVIMIENTO")
@SequenceGenerator(sequenceName = "sg_viaje_si_movimiento_id_seq", name = "sg_viaje_si_movimiento_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgViajeSiMovimiento.findAll", query = "SELECT s FROM SgViajeSiMovimiento s")})
public class SgViajeSiMovimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_viaje_si_movimiento_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional = false)
    @NotNull
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
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "SI_MOVIMIENTO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SiMovimiento siMovimiento;
    @JoinColumn(name = "SG_VIAJE", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgViaje sgViaje;

    public SgViajeSiMovimiento() {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName()).append("{")
                .append("id=").append(this.id)
                .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
                .append(", siMovimiento =").append(this.siMovimiento != null ? this.siMovimiento.getId() : null)
                .append(", sgViaje=").append(this.sgViaje != null ? this.sgViaje.getId() : null)
                .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
                .append(", fechaGenero=").append(this.fechaGenero != null ? (Constantes.FMT_ddMMyyy.format(this.fechaGenero)) : null)
                .append(", horaGenero=").append(this.horaGenero != null ? (Constantes.FMT_HHmmss.format(this.horaGenero)) : null)
                .append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
                .append(", fechaModifico=").append(this.fechaModifico != null ? (Constantes.FMT_ddMMyyy.format(this.fechaModifico)) : null)
                .append(", horaModifico=").append(this.horaModifico != null ? (Constantes.FMT_HHmmss.format(this.horaModifico)) : null)
                .append(", eliminado=")
                .append(this.eliminado)
                .append("}");
        return sb.toString();
    }
    public SgViajeSiMovimiento(Integer id) {
        this.id = id;
    }

    public SgViajeSiMovimiento(Integer id, Date fechaGenero, Date horaGenero) {
        this.id = id;
        this.fechaGenero = fechaGenero;
        this.horaGenero = horaGenero;
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

    public Date getFechaModifico() {
        return fechaModifico;
    }

    public void setFechaModifico(Date fechaModifico) {
        this.fechaModifico = fechaModifico;
    }

    public Date getHoraModifico() {
        return horaModifico;
    }

    public void setHoraModifico(Date horaModifico) {
        this.horaModifico = horaModifico;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Usuario getModifico() {
        return modifico;
    }

    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public SiMovimiento getSiMovimiento() {
        return siMovimiento;
    }

    public void setSiMovimiento(SiMovimiento siMovimiento) {
        this.siMovimiento = siMovimiento;
    }

    public SgViaje getSgViaje() {
        return sgViaje;
    }

    public void setSgViaje(SgViaje sgViaje) {
        this.sgViaje = sgViaje;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgViajeSiMovimiento)) {
            return false;
        }
        SgViajeSiMovimiento other = (SgViajeSiMovimiento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
}
