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
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_STAFF_HABITACION")
@SequenceGenerator(sequenceName = "sg_staff_habitacion_id_seq", name = "sg_staff_habitacion_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgStaffHabitacion.findAll", query = "SELECT s FROM SgStaffHabitacion s")})
public class SgStaffHabitacion implements Serializable {
    @Basic(optional = false)
    
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional = false)
    @NotNull
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
       private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_staff_habitacion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Size(max = 32)
    @Column(name = "NOMBRE")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "NUMERO_HABITACION")
    private String numeroHabitacion;
    
    @Column(name = "OCUPADA")
    private boolean ocupada;
    @Basic(optional = false)
    @NotNull    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sgStaffHabitacion")
    private Collection<SgCaracteristicaHabitacion> sgCaracteristicaHabitacionCollection;    
    @OneToMany(mappedBy = "sgStaffHabitacion")
    private Collection<SgHuespedStaff> sgHuespedStaffCollection;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "SG_STAFF", referencedColumnName = "ID")
    @ManyToOne
    private SgStaff sgStaff;

    public SgStaffHabitacion() {
    }

    public SgStaffHabitacion(Integer id) {
        this.id = id;
    }

    public SgStaffHabitacion(Integer id, String numeroHabitacion, Date fechaGenero, Date horaGenero, boolean eliminado) {
        this.id = id;
        this.numeroHabitacion = numeroHabitacion;
        this.fechaGenero = fechaGenero;
        this.horaGenero = horaGenero;
        this.eliminado = eliminado;
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

    public String getNumeroHabitacion() {
        return numeroHabitacion;
    }

    public void setNumeroHabitacion(String numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
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

    public Collection<SgHuespedStaff> getSgHuespedStaffCollection() {
        return sgHuespedStaffCollection;
    }

    public void setSgHuespedStaffCollection(Collection<SgHuespedStaff> sgHuespedStaffCollection) {
        this.sgHuespedStaffCollection = sgHuespedStaffCollection;
    }
    
    public Collection<SgCaracteristicaHabitacion> getSgCaracteristicaHabitacionCollection() {
        return sgCaracteristicaHabitacionCollection;
    }

    public void setSgCaracteristicaHabitacionCollection(Collection<SgCaracteristicaHabitacion> sgCaracteristicaHabitacionCollection) {
        this.sgCaracteristicaHabitacionCollection = sgCaracteristicaHabitacionCollection;
    }    

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public SgStaff getSgStaff() {
        return sgStaff;
    }

    public void setSgStaff(SgStaff sgStaff) {
        this.sgStaff = sgStaff;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgStaffHabitacion)) {
            return false;
        }
        SgStaffHabitacion other = (SgStaffHabitacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SgStaffHabitacion[ id=" + id + " ]";
    }
}
