/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.modelo;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author sluis
 */
@Entity
@Table(name = "SI_PARAMETRO")
@SequenceGenerator(sequenceName = "si_parametro_id_seq", name = "si_parametro_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SiParametro.findAll", query = "SELECT s FROM SiParametro s")})
public class SiParametro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "DIFERENCIA_ORDEN_REQUISICION")
    private Double diferenciaOrdenRequisicion;
    @Size(max = 100)
    @Column(name = "UPLOAD_DIRECTORY")
    private String uploadDirectory;
    @Lob
    @Column(name = "LOGO")
    private byte[] logo;
    @Column(name = "tipo_almacen_adjuntos")
    private String tipoAlmacenAdjuntos;
    @Column(name = "gest_doc_url_base")
    private String gestDocUrlBase;
    @Column(name = "gest_doc_usuario")
    private String gestDocUsuario;
    @Column(name = "gest_doc_clave")
    private String gestDocClave;
    @Column(name = "gest_doc_prop_adic")
    private String gestDocPropAdic;
    @Column(name = "directorio_usuarios")
    private String directorioUsuarios;

    public SiParametro() {
    }

    public SiParametro(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getDiferenciaOrdenRequisicion() {
        return diferenciaOrdenRequisicion;
    }

    public void setDiferenciaOrdenRequisicion(Double diferenciaOrdenRequisicion) {
        this.diferenciaOrdenRequisicion = diferenciaOrdenRequisicion;
    }

    public String getUploadDirectory() {
        return uploadDirectory;
    }

    public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
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
    
    public String getGestDocPropAdic() {
        return gestDocPropAdic;
    }

    public void setGestDocPropAdic(String gestDocPropAdic) {
        this.gestDocPropAdic = gestDocPropAdic;
    }
    
    public String getDirectorioUsuarios() {
        return directorioUsuarios;
    }
    
    public void setDirectorioUsuarios(String directorioUsuarios) {
        this.directorioUsuarios = directorioUsuarios;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
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

    
    public String toString() {
        return "sia.modelo.SiParametro[ id=" + id + " ]";
    }
    
}
