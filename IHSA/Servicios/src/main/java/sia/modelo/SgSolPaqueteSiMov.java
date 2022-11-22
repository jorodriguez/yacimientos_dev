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
@Table(name = "SG_SOL_PAQUETE_SI_MOV")
@SequenceGenerator(sequenceName = "sg_sol_paquete_si_mov_id_seq", name = "sg_sol_paquete_si_mov_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgSolPaqueteSiMov.findAll", query = "SELECT s FROM SgSolPaqueteSiMov s")})
public class SgSolPaqueteSiMov implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
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
    @JoinColumn(name = "SG_SOLICITUD_PAQUETERIA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgSolicitudPaqueteria sgSolicitudPaqueteria;

    public SgSolPaqueteSiMov() {
    }

    public SgSolPaqueteSiMov(Integer id) {
        this.id = id;
    }

    public SgSolPaqueteSiMov(Integer id, Date fechaGenero, Date horaGenero) {
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

    public SgSolicitudPaqueteria getSgSolicitudPaqueteria() {
        return sgSolicitudPaqueteria;
    }

    public void setSgSolicitudPaqueteria(SgSolicitudPaqueteria sgSolicitudPaqueteria) {
        this.sgSolicitudPaqueteria = sgSolicitudPaqueteria;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgSolPaqueteSiMov)) {
            return false;
        }
        SgSolPaqueteSiMov other = (SgSolPaqueteSiMov) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SgSolPaqueteSiMov[ id=" + id + " ]";
    }
    
}
