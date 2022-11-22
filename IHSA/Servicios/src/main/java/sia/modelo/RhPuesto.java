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
@Table(name = "RH_PUESTO")
@SequenceGenerator(sequenceName = "rh_puesto_id_seq", name = "rh_puesto_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "RhPuesto.findAll", query = "SELECT r FROM RhPuesto r")})
public class RhPuesto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "rh_puesto_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 100)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 256)
    @Column(name = "DESCRIPCION")
    private String descripcion;
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

    public RhPuesto() {
    }

    public RhPuesto(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RhPuesto)) {
            return false;
        }
        RhPuesto other = (RhPuesto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

  
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
            .append("id=").append(getId())
            .append(", nombre=").append(getNombre())    
                 .append(", descripcion=").append(getDescripcion())  
          .append(", genero=").append(getGenero() != null ? getGenero().getId() : null)
            .append(", fechaGenero=").append(getFechaGenero() != null ? (Constantes.FMT_ddMMyyy.format(getFechaGenero())) : null)
            .append(", horaGenero=").append(getHoraGenero()!= null ? (Constantes.FMT_HHmmss.format(getHoraGenero())) : null)
            .append(", modifico=").append(getModifico() != null ? this.getModifico().getId() : null)
            .append(", fechaModifico=").append(getFechaModifico() != null ? (Constantes.FMT_ddMMyyy.format(getFechaModifico())) : null)
            .append(", horaModifico=").append(getHoraModifico() != null ? (Constantes.FMT_HHmmss.format(getHoraModifico())) : null)
            .append(", eliminado=").append(isEliminado())
            .append("}");
        
        return sb.toString();
    } 
    
}
