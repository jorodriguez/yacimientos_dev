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
@Table(name = "PRIORIDAD")
@SequenceGenerator(sequenceName = "prioridad_id_seq", name = "prioridad_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Prioridad.findAll", query = "SELECT p FROM Prioridad p  order by p.id desc ")})
public class Prioridad implements Serializable {
    @Size(max = 1)
    @Column(name = "LETRA")
    private String letra;
    @OneToMany(mappedBy = "prioridad")
    private Collection<SgIncidencia> sgIncidenciaCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Size(min = 1, max = 2)
    @Column(name = "ID")
    private Integer id;
    @Size(max = 50)
    @Column(name = "NOMBRE")
    private String nombre;
//    @Column(name = "DIAS")
//    private Integer dias;
    @Column(name = "ORDEN")
    private Integer orden;
    @OneToMany(mappedBy = "prioridad")
    private Collection<Requisicion> requisicionCollection;

    public Prioridad() {
    }

    public Prioridad(Integer id) {
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

//    public Integer getDias() {
//        return dias;
//    }
//
//    public void setDias(Integer dias) {
//        this.dias = dias;
//    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    @XmlTransient
    public Collection<Requisicion> getRequisicionCollection() {
        return requisicionCollection;
    }

    public void setRequisicionCollection(Collection<Requisicion> requisicionCollection) {
        this.requisicionCollection = requisicionCollection;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Prioridad)) {
            return false;
        }
        Prioridad other = (Prioridad) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.Prioridad[ id=" + id + " ]";
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public Collection<SgIncidencia> getSgIncidenciaCollection() {
        return sgIncidenciaCollection;
    }

    public void setSgIncidenciaCollection(Collection<SgIncidencia> sgIncidenciaCollection) {
        this.sgIncidenciaCollection = sgIncidenciaCollection;
    }
    
}
