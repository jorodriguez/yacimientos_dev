/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "SERVICIO")
@SequenceGenerator(sequenceName = "servicio_id_seq", name = "servicio_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Servicio.findAll", query = "SELECT s FROM Servicio s")})
public class Servicio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Lob
    
    @Column(name = "NOMBRE")
    private String nombre;
    @OneToMany(mappedBy = "servicio")
    private Collection<CaracteristicasServicio> caracteristicasServicioCollection;
    @OneToMany(mappedBy = "servicio")
    private Collection<ServiciosAdquiridos> serviciosAdquiridosCollection;

    public Servicio() {
    }

    public Servicio(Integer id) {
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
    public Collection<CaracteristicasServicio> getCaracteristicasServicioCollection() {
        return caracteristicasServicioCollection;
    }

    public void setCaracteristicasServicioCollection(Collection<CaracteristicasServicio> caracteristicasServicioCollection) {
        this.caracteristicasServicioCollection = caracteristicasServicioCollection;
    }

    @XmlTransient
    public Collection<ServiciosAdquiridos> getServiciosAdquiridosCollection() {
        return serviciosAdquiridosCollection;
    }

    public void setServiciosAdquiridosCollection(Collection<ServiciosAdquiridos> serviciosAdquiridosCollection) {
        this.serviciosAdquiridosCollection = serviciosAdquiridosCollection;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Servicio)) {
            return false;
        }
        Servicio other = (Servicio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.Servicio[ id=" + id + " ]";
    }
    
}
