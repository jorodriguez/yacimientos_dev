

package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author esapien
 */
@Entity
@Table(name = "SI_PERMISO")
@SequenceGenerator(sequenceName = "si_permiso_id_seq", name = "si_permiso_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SiPermiso.findAll", query = "SELECT s FROM SiPermiso s")})
public class SiPermiso implements Serializable {
    
    
    // <editor-fold defaultstate="collapsed" desc="Atributos / Campos">
    
    private static final long serialVersionUID = 1L;
    
    @Id
@GeneratedValue(generator =  "si_permiso_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 64)
    @Column(name = "NOMBRE")
    private String nombre;
    
    @JoinColumn(name = "GENERO", referencedColumnName = "ID" )
    @ManyToOne
    private Usuario genero;
    @Column(name =     "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name =     "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name =     "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name =     "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    
    @JoinColumn(name = "SI_MODULO", referencedColumnName = "ID")
    @ManyToOne
    private SiModulo siModulo;
    
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="Accesores">
    

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Date getFechaGenero() {
        return fechaGenero;
    }

    public void setFechaGenero(Date fechaGenero) {
        this.fechaGenero = fechaGenero;
    }

    public Date getFechaModifico() {
        return fechaModifico;
    }

    public void setFechaModifico(Date fechaModifico) {
        this.fechaModifico = fechaModifico;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public Date getHoraGenero() {
        return horaGenero;
    }

    public void setHoraGenero(Date horaGenero) {
        this.horaGenero = horaGenero;
    }

    public Date getHoraModifico() {
        return horaModifico;
    }

    public void setHoraModifico(Date horaModifico) {
        this.horaModifico = horaModifico;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getModifico() {
        return modifico;
    }

    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public SiModulo getSiModulo() {
        return siModulo;
    }

    public void setSiModulo(SiModulo siModulo) {
        this.siModulo = siModulo;
    }
    
    
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="Util">
    
    
    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiPermiso)) {
            return false;
        }
        SiPermiso other = (SiPermiso) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SiPermiso[ id=" + id + " ]";
    }
    
    // </editor-fold>
    
    
    
}
