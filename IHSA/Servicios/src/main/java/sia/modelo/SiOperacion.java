/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SI_OPERACION")
@SequenceGenerator(sequenceName = "si_operacion_id_seq", name = "si_operacion_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SiOperacion.findAll", query = "SELECT s FROM SiOperacion s")})
public class SiOperacion implements Serializable {
    @Basic(optional =     false)
    
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional =     false)
    @NotNull
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name =     "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name =     "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "siOperacion")
    private Collection<SiMovimiento> siMovimientoCollection;
    @OneToMany(mappedBy = "siOperacion")
    private Collection<SgAsignarVehiculo> sgAsignarVehiculoCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "si_operacion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Size(max = 16)
    @Column(name = "NOMBRE")
    private String nombre;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @OneToMany(mappedBy = "siOperacion")
    private Collection<SgAsignarAccesorio> sgAsignarAccesorioCollection;

    public SiOperacion() {
    }

    public SiOperacion(Integer id) {
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

    public Collection<SgAsignarAccesorio> getSgAsignarAccesorioCollection() {
        return sgAsignarAccesorioCollection;
    }

    public void setSgAsignarAccesorioCollection(Collection<SgAsignarAccesorio> sgAsignarAccesorioCollection) {
        this.sgAsignarAccesorioCollection = sgAsignarAccesorioCollection;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiOperacion)) {
            return false;
        }
        SiOperacion other = (SiOperacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }


    public Collection<SgAsignarVehiculo> getSgAsignarVehiculoCollection() {
        return sgAsignarVehiculoCollection;
    }

    public void setSgAsignarVehiculoCollection(Collection<SgAsignarVehiculo> sgAsignarVehiculoCollection) {
        this.sgAsignarVehiculoCollection = sgAsignarVehiculoCollection;
    }
      
    public String toString() {
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
            .append("id=").append(this.id)
            .append(", nombre=").append(this.nombre)
            .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
            .append(", fechaGenero=").append(this.fechaGenero != null ? (sdfFecha.format(this.fechaGenero)) : null)
            .append(", horaGenero=").append(this.horaGenero != null ? (sdfHora.format(this.horaGenero)) : null)
            .append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
            .append(", fechaModifico=").append(this.fechaModifico != null ? (sdfFecha.format(this.fechaModifico)) : null)
            .append(", horaModifico=").append(this.horaModifico != null ? (sdfHora.format(this.horaModifico)) : null)
            .append(", eliminado=").append(this.eliminado)
            .append("}");
        
        return sb.toString();
    }


    public Collection<SiMovimiento> getSiMovimientoCollection() {
        return siMovimientoCollection;
    }

    public void setSiMovimientoCollection(Collection<SiMovimiento> siMovimientoCollection) {
        this.siMovimientoCollection = siMovimientoCollection;
    }

    
}
