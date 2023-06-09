/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.ihsa.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author jorodriguez
 */
@Entity
@Table(name = "c_municipio")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CMunicipio.findAll", query = "SELECT c FROM CMunicipio c"),
    @NamedQuery(name = "CMunicipio.findById", query = "SELECT c FROM CMunicipio c WHERE c.id = :id"),
    @NamedQuery(name = "CMunicipio.findByClave", query = "SELECT c FROM CMunicipio c WHERE c.clave = :clave"),
    @NamedQuery(name = "CMunicipio.findByNombre", query = "SELECT c FROM CMunicipio c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "CMunicipio.findByFechaGenero", query = "SELECT c FROM CMunicipio c WHERE c.fechaGenero = :fechaGenero"),
    @NamedQuery(name = "CMunicipio.findByFechaModifico", query = "SELECT c FROM CMunicipio c WHERE c.fechaModifico = :fechaModifico"),
    @NamedQuery(name = "CMunicipio.findByEliminado", query = "SELECT c FROM CMunicipio c WHERE c.eliminado = :eliminado")})
public class CMunicipio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "clave")
    private int clave;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 512)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_genero")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaGenero;
    @Column(name = "fecha_modifico")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModifico;
    @Column(name = "eliminado")
    private Boolean eliminado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cMunicipio")
    private Collection<CLocalidad> cLocalidadCollection;
    @JoinColumn(name = "c_estado", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private CEstado cEstado;
    @JoinColumn(name = "genero", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "modifico", referencedColumnName = "id")
    @ManyToOne
    private Usuario modifico;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cMunicipio")
    private Collection<CSeccion> cSeccionCollection;
    @OneToMany(mappedBy = "cMunicipio")
    private Collection<Usuario> usuarioCollection;

    public CMunicipio() {
    }

    public CMunicipio(Integer id) {
        this.id = id;
    }

    public CMunicipio(Integer id, int clave, String nombre, Date fechaGenero) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.fechaGenero = fechaGenero;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getClave() {
        return clave;
    }

    public void setClave(int clave) {
        this.clave = clave;
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

    public Date getFechaModifico() {
        return fechaModifico;
    }

    public void setFechaModifico(Date fechaModifico) {
        this.fechaModifico = fechaModifico;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CLocalidad> getCLocalidadCollection() {
        return cLocalidadCollection;
    }

    public void setCLocalidadCollection(Collection<CLocalidad> cLocalidadCollection) {
        this.cLocalidadCollection = cLocalidadCollection;
    }

    public CEstado getCEstado() {
        return cEstado;
    }

    public void setCEstado(CEstado cEstado) {
        this.cEstado = cEstado;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public Usuario getModifico() {
        return modifico;
    }

    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CSeccion> getCSeccionCollection() {
        return cSeccionCollection;
    }

    public void setCSeccionCollection(Collection<CSeccion> cSeccionCollection) {
        this.cSeccionCollection = cSeccionCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Usuario> getUsuarioCollection() {
        return usuarioCollection;
    }

    public void setUsuarioCollection(Collection<Usuario> usuarioCollection) {
        this.usuarioCollection = usuarioCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CMunicipio)) {
            return false;
        }
        CMunicipio other = (CMunicipio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "lector.modelo.CMunicipio[ id=" + id + " ]";
    }
    
}
