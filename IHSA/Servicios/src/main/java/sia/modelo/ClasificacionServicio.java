/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "CLASIFICACION_SERVICIO")
@SequenceGenerator(sequenceName = "clasificacion_servicio_id_seq", name = "clasificacion_servicio_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ClasificacionServicio.findAll", query = "SELECT c FROM ClasificacionServicio c")})
public class ClasificacionServicio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "clasificacion_servicio_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 120)
    @Column(name = "NOMBRE")
    private String nombre;
    @OneToMany(mappedBy = "clasificacion")
    private Collection<CvRelacionActividad> cvRelacionActividadCollection;
    @JoinColumn(name = "PROVEEDOR_ACTIVIDAD", referencedColumnName = "ID")
    @ManyToOne
    private ProveedorActividad proveedorActividad;
    @OneToMany(mappedBy = "clasificacionServicio")
    private Collection<CaracteristicasServicio> caracteristicasServicioCollection;

    public ClasificacionServicio() {
    }

    public ClasificacionServicio(Integer id) {
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

    @XmlTransient
    public Collection<CvRelacionActividad> getCvRelacionActividadCollection() {
        return cvRelacionActividadCollection;
    }

    public void setCvRelacionActividadCollection(Collection<CvRelacionActividad> cvRelacionActividadCollection) {
        this.cvRelacionActividadCollection = cvRelacionActividadCollection;
    }

    public ProveedorActividad getProveedorActividad() {
        return proveedorActividad;
    }

    public void setProveedorActividad(ProveedorActividad proveedorActividad) {
        this.proveedorActividad = proveedorActividad;
    }

    @XmlTransient
    public Collection<CaracteristicasServicio> getCaracteristicasServicioCollection() {
        return caracteristicasServicioCollection;
    }

    public void setCaracteristicasServicioCollection(Collection<CaracteristicasServicio> caracteristicasServicioCollection) {
        this.caracteristicasServicioCollection = caracteristicasServicioCollection;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ClasificacionServicio)) {
            return false;
        }
        ClasificacionServicio other = (ClasificacionServicio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.ClasificacionServicio[ id=" + id + " ]";
    }
}
