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
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "PV_AREA")
@SequenceGenerator(sequenceName = "pv_area_id_seq", name = "pv_area_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PvArea.findAll", query = "SELECT p FROM PvArea p")})
public class PvArea implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 50)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 140)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @OneToMany(mappedBy = "PvArea")
    private Collection<ContactoProveedor> contactoProveedorCollection;

    public PvArea() {
    }

    public PvArea(Integer id) {
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

    @XmlTransient
    public Collection<ContactoProveedor> getContactoProveedorCollection() {
        return contactoProveedorCollection;
    }

    public void setContactoProveedorCollection(Collection<ContactoProveedor> contactoProveedorCollection) {
        this.contactoProveedorCollection = contactoProveedorCollection;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PvArea)) {
            return false;
        }
        PvArea other = (PvArea) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.PvArea[ id=" + id + " ]";
    }
    
}
