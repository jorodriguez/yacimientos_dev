/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.ihsa.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
@Table(name = "si_adjunto")
public class SiAdjunto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 64)
    @Column(name = "uuid")
    private String uuid;
    @Size(max = 1024)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 1024)
    @Column(name = "descripcion")
    private String descripcion;
    @Size(max = 75)
    @Column(name = "tipo_archivo")
    private String tipoArchivo;
    @Size(max = 10)
    @Column(name = "peso")
    private String peso;
    @Size(max = 2147483647)
    @Column(name = "url")
    private String url;
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
    @OneToMany(mappedBy = "siAdjunto")
    private Collection<Usuario> usuarioCollection;
    @JoinColumn(name = "genero", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "modifico", referencedColumnName = "id")
    @ManyToOne
    private Usuario modifico;

    public SiAdjunto() {
    }

    @Builder
    public SiAdjunto(Integer id, String uuid, String nombre, String descripcion, String tipoArchivo, String peso, String url, Date fechaGenero, Date fechaModifico, Boolean eliminado, Collection<Usuario> usuarioCollection, Usuario genero, Usuario modifico) {
        this.id = id;
        this.uuid = uuid;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoArchivo = tipoArchivo;
        this.peso = peso;
        this.url = url;
        this.fechaGenero = fechaGenero;
        this.fechaModifico = fechaModifico;
        this.eliminado = eliminado;
        this.usuarioCollection = usuarioCollection;
        this.genero = genero;
        this.modifico = modifico;
    }

    
    
    
    public SiAdjunto(Integer id) {
        this.id = id;
    }

    public SiAdjunto(Integer id, Date fechaGenero) {
        this.id = id;
        this.fechaGenero = fechaGenero;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Usuario getModifico() {
        return modifico;
    }

    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
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
        if (!(object instanceof SiAdjunto)) {
            return false;
        }
        SiAdjunto other = (SiAdjunto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.ihsa.modelo.SiAdjunto_1[ id=" + id + " ]";
    }
    
}
