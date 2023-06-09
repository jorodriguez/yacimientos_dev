/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lector.modelo;

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
@Table(name = "c_localidad")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CLocalidad.findAll", query = "SELECT c FROM CLocalidad c"),
    @NamedQuery(name = "CLocalidad.findById", query = "SELECT c FROM CLocalidad c WHERE c.id = :id"),
    @NamedQuery(name = "CLocalidad.findByClave", query = "SELECT c FROM CLocalidad c WHERE c.clave = :clave"),
    @NamedQuery(name = "CLocalidad.findByNombre", query = "SELECT c FROM CLocalidad c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "CLocalidad.findByLatitud", query = "SELECT c FROM CLocalidad c WHERE c.latitud = :latitud"),
    @NamedQuery(name = "CLocalidad.findByLongitud", query = "SELECT c FROM CLocalidad c WHERE c.longitud = :longitud"),
    @NamedQuery(name = "CLocalidad.findByFechaGenero", query = "SELECT c FROM CLocalidad c WHERE c.fechaGenero = :fechaGenero"),
    @NamedQuery(name = "CLocalidad.findByFechaModifico", query = "SELECT c FROM CLocalidad c WHERE c.fechaModifico = :fechaModifico"),
    @NamedQuery(name = "CLocalidad.findByEliminado", query = "SELECT c FROM CLocalidad c WHERE c.eliminado = :eliminado")})
public class CLocalidad implements Serializable {

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
    @Size(max = 32)
    @Column(name = "latitud")
    private String latitud;
    @Size(max = 32)
    @Column(name = "longitud")
    private String longitud;
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
    @JoinColumn(name = "c_estado", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private CEstado cEstado;
    @JoinColumn(name = "c_municipio", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private CMunicipio cMunicipio;
    @JoinColumn(name = "genero", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "modifico", referencedColumnName = "id")
    @ManyToOne
    private Usuario modifico;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cLocalidad")
    private Collection<CSeccion> cSeccionCollection;
    @OneToMany(mappedBy = "cLocalidad")
    private Collection<Usuario> usuarioCollection;

    public CLocalidad() {
    }

    public CLocalidad(Integer id) {
        this.id = id;
    }

    public CLocalidad(Integer id, int clave, String nombre, Date fechaGenero) {
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

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
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

    public CEstado getCEstado() {
        return cEstado;
    }

    public void setCEstado(CEstado cEstado) {
        this.cEstado = cEstado;
    }

    public CMunicipio getCMunicipio() {
        return cMunicipio;
    }

    public void setCMunicipio(CMunicipio cMunicipio) {
        this.cMunicipio = cMunicipio;
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
        if (!(object instanceof CLocalidad)) {
            return false;
        }
        CLocalidad other = (CLocalidad) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "lector.modelo.CLocalidad[ id=" + id + " ]";
    }
    
}
