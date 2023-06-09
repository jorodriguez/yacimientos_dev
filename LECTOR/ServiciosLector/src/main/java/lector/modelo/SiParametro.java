/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lector.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author jorodriguez
 */
@Entity
@Table(name = "si_parametro")
public class SiParametro implements Serializable {

    @Lob
    @Column(name = "logo")
    private byte[] logo;
    @Size(max = 1024)
    @Column(name = "api_whatsapp")
    private String apiWhatsapp;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "upload_directory")
    private String uploadDirectory;
    @Basic(optional = false)
    @Column(name = "tipo_almacen_adjuntos")
    private String tipoAlmacenAdjuntos;
    @Column(name = "gest_doc_url_base")
    private String gestDocUrlBase;
    @Column(name = "gest_doc_prop_adic")
    private String gestDocPropAdic;
    @Column(name = "gest_doc_usuario")
    private String gestDocUsuario;
    @Column(name = "gest_doc_clave")
    private String gestDocClave;
    @Column(name = "directorio_usuarios")
    private String directorioUsuarios;
    @Basic(optional = false)
    @Column(name = "fecha_genero")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaGenero;
    @Column(name = "fecha_modifico")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModifico;
    @Column(name = "eliminado")
    private Boolean eliminado;
    @JoinColumn(name = "genero", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "modifico", referencedColumnName = "id")
    @ManyToOne
    private Usuario modifico;

    public SiParametro() {
    }

    public SiParametro(Integer id) {
        this.id = id;
    }

    public SiParametro(Integer id, String tipoAlmacenAdjuntos, Date fechaGenero) {
        this.id = id;
        this.tipoAlmacenAdjuntos = tipoAlmacenAdjuntos;
        this.fechaGenero = fechaGenero;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUploadDirectory() {
        return uploadDirectory;
    }

    public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }


    public String getTipoAlmacenAdjuntos() {
        return tipoAlmacenAdjuntos;
    }

    public void setTipoAlmacenAdjuntos(String tipoAlmacenAdjuntos) {
        this.tipoAlmacenAdjuntos = tipoAlmacenAdjuntos;
    }

    public String getGestDocUrlBase() {
        return gestDocUrlBase;
    }

    public void setGestDocUrlBase(String gestDocUrlBase) {
        this.gestDocUrlBase = gestDocUrlBase;
    }

    public String getGestDocPropAdic() {
        return gestDocPropAdic;
    }

    public void setGestDocPropAdic(String gestDocPropAdic) {
        this.gestDocPropAdic = gestDocPropAdic;
    }

    public String getGestDocUsuario() {
        return gestDocUsuario;
    }

    public void setGestDocUsuario(String gestDocUsuario) {
        this.gestDocUsuario = gestDocUsuario;
    }

    public String getGestDocClave() {
        return gestDocClave;
    }

    public void setGestDocClave(String gestDocClave) {
        this.gestDocClave = gestDocClave;
    }

    public String getDirectorioUsuarios() {
        return directorioUsuarios;
    }

    public void setDirectorioUsuarios(String directorioUsuarios) {
        this.directorioUsuarios = directorioUsuarios;
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
        if (!(object instanceof SiParametro)) {
            return false;
        }
        SiParametro other = (SiParametro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.ihsa.mavenproject1.SiParametro[ id=" + id + " ]";
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getApiWhatsapp() {
        return apiWhatsapp;
    }

    public void setApiWhatsapp(String apiWhatsapp) {
        this.apiWhatsapp = apiWhatsapp;
    }
    
}
