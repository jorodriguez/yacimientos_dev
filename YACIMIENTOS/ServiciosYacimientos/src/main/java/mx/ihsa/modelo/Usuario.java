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
import lombok.Builder;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author jorodriguez
 */
@Entity
@Table(name = "usuario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.findByCorreo", query = "SELECT u FROM Usuario u where u.email = ?1  and u.eliminado = false"),    
    @NamedQuery(name = "Usuario.findById", query = "SELECT u FROM Usuario u where u.id = ?1 and u.eliminado = false"),    
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u where u.eliminado = false")
})

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "nombre")
    private String nombre;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 120)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "clave")
    private String clave;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "telefono")
    private String telefono;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;
    @Size(max = 2147483647)
    @Column(name = "domicilio")
    private String domicilio;
    @Size(max = 20)
    @Column(name = "curp")
    private String curp;
    @Size(max = 2147483647)
    @Column(name = "foto")
    private String foto;
    @Size(max = 10)
    @Column(name = "seccion")
    private String seccion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_genero")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaGenero;
    @Column(name = "fecha_modifico")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModifico;
    @Basic(optional = false)
    @NotNull
    @Column(name = "eliminado")
    private boolean eliminado;
    @JoinColumn(name = "si_adjunto", referencedColumnName = "id")
    @ManyToOne
    private SiAdjunto siAdjunto;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<Usuario> usuarioCollection;
    @JoinColumn(name = "genero", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario genero;
    @OneToMany(mappedBy = "modifico")
    private Collection<Usuario> usuarioCollection1;
    @JoinColumn(name = "modifico", referencedColumnName = "id")
    @ManyToOne
    private Usuario modifico;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<SiAdjunto> siAdjuntoCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<SiAdjunto> siAdjuntoCollection1;

    public Usuario() {
    }

    public Usuario(Integer id) {
        this.id = id;
    }

    @Builder
    public Usuario(Integer id, String nombre, String email, String clave, String telefono, Date fechaNacimiento, String domicilio, String curp, String foto, String seccion, Date fechaGenero, Date fechaModifico, boolean eliminado, SiAdjunto siAdjunto, Collection<Usuario> usuarioCollection, Usuario genero, Collection<Usuario> usuarioCollection1, Usuario modifico, Collection<SiAdjunto> siAdjuntoCollection, Collection<SiAdjunto> siAdjuntoCollection1) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.clave = clave;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.domicilio = domicilio;
        this.curp = curp;
        this.foto = foto;
        this.seccion = seccion;
        this.fechaGenero = fechaGenero;
        this.fechaModifico = fechaModifico;
        this.eliminado = eliminado;
        this.siAdjunto = siAdjunto;
        this.usuarioCollection = usuarioCollection;
        this.genero = genero;
        this.usuarioCollection1 = usuarioCollection1;
        this.modifico = modifico;
        this.siAdjuntoCollection = siAdjuntoCollection;
        this.siAdjuntoCollection1 = siAdjuntoCollection1;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
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

    public boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public SiAdjunto getSiAdjunto() {
        return siAdjunto;
    }

    public void setSiAdjunto(SiAdjunto siAdjunto) {
        this.siAdjunto = siAdjunto;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Usuario> getUsuarioCollection() {
        return usuarioCollection;
    }

    public void setUsuarioCollection(Collection<Usuario> usuarioCollection) {
        this.usuarioCollection = usuarioCollection;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Usuario> getUsuarioCollection1() {
        return usuarioCollection1;
    }

    public void setUsuarioCollection1(Collection<Usuario> usuarioCollection1) {
        this.usuarioCollection1 = usuarioCollection1;
    }

    public Usuario getModifico() {
        return modifico;
    }

    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiAdjunto> getSiAdjuntoCollection() {
        return siAdjuntoCollection;
    }

    public void setSiAdjuntoCollection(Collection<SiAdjunto> siAdjuntoCollection) {
        this.siAdjuntoCollection = siAdjuntoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiAdjunto> getSiAdjuntoCollection1() {
        return siAdjuntoCollection1;
    }

    public void setSiAdjuntoCollection1(Collection<SiAdjunto> siAdjuntoCollection1) {
        this.siAdjuntoCollection1 = siAdjuntoCollection1;
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
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.ihsa.modelo.Usuario[ id=" + id + " ]";
    }
    
}
