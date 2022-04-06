

package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author esapien
 */
@Entity
@Table(name = "OF_OFICIO_SI_MOV_SI_ADJUNTO")
@SequenceGenerator(sequenceName = "of_oficio_si_mov_si_adjunto_id_seq", name = "of_oficio_si_mov_si_adjunto_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "OfOficioSiMovSiAdjunto.findAll", query = "SELECT o FROM OfOficioSiMovSiAdjunto o")})
@Data
public class OfOficioSiMovSiAdjunto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    
@GeneratedValue(generator =  "of_oficio_si_mov_si_adjunto_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Integer id;
    
    @NotNull
    @JoinColumn(name = "OF_OFICIO_SI_MOVIMIENTO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private OfOficioSiMovimiento ofOficioSiMovimiento;
    @NotNull
    @JoinColumn(name = "SI_ADJUNTO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SiAdjunto siAdjunto;
    
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario genero;
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
    
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    

    public OfOficioSiMovSiAdjunto() {
    }

    public OfOficioSiMovSiAdjunto(Integer id) {
        this.id = id;
    }

    public OfOficioSiMovSiAdjunto(Integer id, Date fechaGenero, Date horaGenero) {
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

    public OfOficioSiMovimiento getOfOficioSiMovimiento() {
        return ofOficioSiMovimiento;
    }

    public void setOfOficioSiMovimiento(OfOficioSiMovimiento ofOficioSiMovimiento) {
        this.ofOficioSiMovimiento = ofOficioSiMovimiento;
    }
    
    
    
    
    public SiAdjunto getSiAdjunto() {
        return siAdjunto;
    }

    public void setSiAdjunto(SiAdjunto siAdjunto) {
        this.siAdjunto = siAdjunto;
    }
    
    
    
    

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OfOficioSiMovSiAdjunto)) {
            return false;
        }
        OfOficioSiMovSiAdjunto other = (OfOficioSiMovSiAdjunto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
}
