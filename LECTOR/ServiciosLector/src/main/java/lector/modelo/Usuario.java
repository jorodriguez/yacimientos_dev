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
import javax.xml.bind.annotation.XmlTransient;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author jorodriguez
 */
@Entity
@Getter
@Setter
@Table(name = "usuario")
@NamedQueries({
    @NamedQuery(name = "Usuario.findByCorreo", query = "SELECT u FROM Usuario u where u.email = ?1 and u.eliminado = false"),    
    @NamedQuery(name = "Usuario.findById", query = "SELECT u FROM Usuario u where u.id = ?1 and u.eliminado = false"),    
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u where u.eliminado = false")
})
public class Usuario implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<CTipoContacto> cTipoContactoCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<CTipoContacto> cTipoContactoCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<SiOpcion> siOpcionCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<SiOpcion> siOpcionCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<SiRelRolOpcion> siRelRolOpcionCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<SiRelRolOpcion> siRelRolOpcionCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<DdSesion> ddSesionCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<DdSesion> ddSesionCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<CLocalidad> cLocalidadCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<CLocalidad> cLocalidadCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<CEstado> cEstadoCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<CEstado> cEstadoCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<SiParametro> siParametroCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<SiParametro> siParametroCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<SiModulo> siModuloCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<SiModulo> siModuloCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<CMunicipio> cMunicipioCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<CMunicipio> cMunicipioCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<SiPlantillaHtml> siPlantillaHtmlCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<SiPlantillaHtml> siPlantillaHtmlCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<CCuenta> cCuentaCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<CCuenta> cCuentaCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<LogLectura> logLecturaCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<LogLectura> logLecturaCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario")
    private Collection<LogLectura> logLecturaCollection2;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<SiUsuarioRol> siUsuarioRolCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<SiUsuarioRol> siUsuarioRolCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario")
    private Collection<SiUsuarioRol> siUsuarioRolCollection2;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<CSeccion> cSeccionCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<CSeccion> cSeccionCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<SiRol> siRolCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<SiRol> siRolCollection1;
    @JoinColumn(name = "c_estado", referencedColumnName = "id")
    @ManyToOne
    private CEstado cEstado;
    @JoinColumn(name = "c_localidad", referencedColumnName = "id")
    @ManyToOne
    private CLocalidad cLocalidad;
    @JoinColumn(name = "c_municipio", referencedColumnName = "id")
    @ManyToOne
    private CMunicipio cMunicipio;
    @JoinColumn(name = "c_seccion", referencedColumnName = "id")
    @ManyToOne
    private CSeccion cSeccion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<Usuario> usuarioCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<Usuario> usuarioCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "registro")
    private Collection<Usuario> usuarioCollection2;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<SiAdjunto> siAdjuntoCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<SiAdjunto> siAdjuntoCollection1;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @Column(name = "clave")
    private String clave;
    @Basic(optional = false)
    @Column(name = "telefono")
    private String telefono;
    @Basic(optional = false)
    @Column(name = "fecha_nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;
    @Column(name = "domicilio")
    private String domicilio;
    @Column(name = "curp")
    private String curp;
    @Column(name = "foto")
    private String foto;
    @Column(name = "anio_registro")
    private String anioRegistro;
    @Column(name = "anio_emision")
    private Integer anioEmision;
    @Basic(optional = false)
    @Column(name = "estado")
    private int estado;
    @Basic(optional = false)
    @Column(name = "municipio")
    private String municipio;
    @Basic(optional = false)
    @Column(name = "seccion")
    private String seccion;
    @Basic(optional = false)
    @Column(name = "localidad")
    private String localidad;
    @Basic(optional = false)
    @Column(name = "emision")
    private int emision;
    @Basic(optional = false)
    @Column(name = "vigencia")
    private int vigencia;
    @Basic(optional = false)
    @Column(name = "sexo")
    private String sexo;
    @Basic(optional = false)
    @Column(name = "fecha_genero")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaGenero;
    @Column(name = "fecha_modifico")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModifico;
    @Column(name = "eliminado")
    private Boolean eliminado;
    @JoinColumn(name = "c_cuenta", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private CCuenta cCuenta;
    @JoinColumn(name = "c_tipo_contacto", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private CTipoContacto cTipoContacto;
    @JoinColumn(name = "si_adjunto", referencedColumnName = "id")
    @ManyToOne
    private SiAdjunto siAdjunto;
    @JoinColumn(name = "genero", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "modifico", referencedColumnName = "id")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "registro", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario registro;

    public Usuario() {
    }

    public Usuario(Integer id) {
        this.id = id;
    }

    @Builder
    public Usuario(Integer id, String nombre, String email, String clave, String telefono, Date fechaNacimiento, String domicilio, String curp, String foto, String anioRegistro, Integer anioEmision, int estado, String municipio, String seccion, String localidad, int emision, int vigencia, String sexo, Date fechaGenero, Date fechaModifico, Boolean eliminado, CCuenta cCuenta, CTipoContacto cTipoContacto, SiAdjunto siAdjunto, Usuario genero, Usuario modifico, Usuario registro) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.clave = clave;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.domicilio = domicilio;
        this.curp = curp;
        this.foto = foto;
        this.anioRegistro = anioRegistro;
        this.anioEmision = anioEmision;
        this.estado = estado;
        this.municipio = municipio;
        this.seccion = seccion;
        this.localidad = localidad;
        this.emision = emision;
        this.vigencia = vigencia;
        this.sexo = sexo;
        this.fechaGenero = fechaGenero;
        this.fechaModifico = fechaModifico;
        this.eliminado = eliminado;
        this.cCuenta = cCuenta;
        this.cTipoContacto = cTipoContacto;
        this.siAdjunto = siAdjunto;
        this.genero = genero;
        this.modifico = modifico;
        this.registro = registro;
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

     public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(getClass().getSimpleName());
	sb.append("{");
	sb.append("id=").append(id);
	sb.append(", nombre").append(nombre != null ? nombre : null);	
	sb.append(", genero=").append(this.genero != null ? this.genero.getId() : null);
	sb.append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null);
	sb.append(", eliminado=").append(this.eliminado);
	sb.append("}");

	return sb.toString();
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CTipoContacto> getCTipoContactoCollection() {
        return cTipoContactoCollection;
    }

    public void setCTipoContactoCollection(Collection<CTipoContacto> cTipoContactoCollection) {
        this.cTipoContactoCollection = cTipoContactoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CTipoContacto> getCTipoContactoCollection1() {
        return cTipoContactoCollection1;
    }

    public void setCTipoContactoCollection1(Collection<CTipoContacto> cTipoContactoCollection1) {
        this.cTipoContactoCollection1 = cTipoContactoCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiOpcion> getSiOpcionCollection() {
        return siOpcionCollection;
    }

    public void setSiOpcionCollection(Collection<SiOpcion> siOpcionCollection) {
        this.siOpcionCollection = siOpcionCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiOpcion> getSiOpcionCollection1() {
        return siOpcionCollection1;
    }

    public void setSiOpcionCollection1(Collection<SiOpcion> siOpcionCollection1) {
        this.siOpcionCollection1 = siOpcionCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiRelRolOpcion> getSiRelRolOpcionCollection() {
        return siRelRolOpcionCollection;
    }

    public void setSiRelRolOpcionCollection(Collection<SiRelRolOpcion> siRelRolOpcionCollection) {
        this.siRelRolOpcionCollection = siRelRolOpcionCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiRelRolOpcion> getSiRelRolOpcionCollection1() {
        return siRelRolOpcionCollection1;
    }

    public void setSiRelRolOpcionCollection1(Collection<SiRelRolOpcion> siRelRolOpcionCollection1) {
        this.siRelRolOpcionCollection1 = siRelRolOpcionCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<DdSesion> getDdSesionCollection() {
        return ddSesionCollection;
    }

    public void setDdSesionCollection(Collection<DdSesion> ddSesionCollection) {
        this.ddSesionCollection = ddSesionCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<DdSesion> getDdSesionCollection1() {
        return ddSesionCollection1;
    }

    public void setDdSesionCollection1(Collection<DdSesion> ddSesionCollection1) {
        this.ddSesionCollection1 = ddSesionCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CLocalidad> getCLocalidadCollection() {
        return cLocalidadCollection;
    }

    public void setCLocalidadCollection(Collection<CLocalidad> cLocalidadCollection) {
        this.cLocalidadCollection = cLocalidadCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CLocalidad> getCLocalidadCollection1() {
        return cLocalidadCollection1;
    }

    public void setCLocalidadCollection1(Collection<CLocalidad> cLocalidadCollection1) {
        this.cLocalidadCollection1 = cLocalidadCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CEstado> getCEstadoCollection() {
        return cEstadoCollection;
    }

    public void setCEstadoCollection(Collection<CEstado> cEstadoCollection) {
        this.cEstadoCollection = cEstadoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CEstado> getCEstadoCollection1() {
        return cEstadoCollection1;
    }

    public void setCEstadoCollection1(Collection<CEstado> cEstadoCollection1) {
        this.cEstadoCollection1 = cEstadoCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiParametro> getSiParametroCollection() {
        return siParametroCollection;
    }

    public void setSiParametroCollection(Collection<SiParametro> siParametroCollection) {
        this.siParametroCollection = siParametroCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiParametro> getSiParametroCollection1() {
        return siParametroCollection1;
    }

    public void setSiParametroCollection1(Collection<SiParametro> siParametroCollection1) {
        this.siParametroCollection1 = siParametroCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiModulo> getSiModuloCollection() {
        return siModuloCollection;
    }

    public void setSiModuloCollection(Collection<SiModulo> siModuloCollection) {
        this.siModuloCollection = siModuloCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiModulo> getSiModuloCollection1() {
        return siModuloCollection1;
    }

    public void setSiModuloCollection1(Collection<SiModulo> siModuloCollection1) {
        this.siModuloCollection1 = siModuloCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CMunicipio> getCMunicipioCollection() {
        return cMunicipioCollection;
    }

    public void setCMunicipioCollection(Collection<CMunicipio> cMunicipioCollection) {
        this.cMunicipioCollection = cMunicipioCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CMunicipio> getCMunicipioCollection1() {
        return cMunicipioCollection1;
    }

    public void setCMunicipioCollection1(Collection<CMunicipio> cMunicipioCollection1) {
        this.cMunicipioCollection1 = cMunicipioCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiPlantillaHtml> getSiPlantillaHtmlCollection() {
        return siPlantillaHtmlCollection;
    }

    public void setSiPlantillaHtmlCollection(Collection<SiPlantillaHtml> siPlantillaHtmlCollection) {
        this.siPlantillaHtmlCollection = siPlantillaHtmlCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiPlantillaHtml> getSiPlantillaHtmlCollection1() {
        return siPlantillaHtmlCollection1;
    }

    public void setSiPlantillaHtmlCollection1(Collection<SiPlantillaHtml> siPlantillaHtmlCollection1) {
        this.siPlantillaHtmlCollection1 = siPlantillaHtmlCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CCuenta> getCCuentaCollection() {
        return cCuentaCollection;
    }

    public void setCCuentaCollection(Collection<CCuenta> cCuentaCollection) {
        this.cCuentaCollection = cCuentaCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CCuenta> getCCuentaCollection1() {
        return cCuentaCollection1;
    }

    public void setCCuentaCollection1(Collection<CCuenta> cCuentaCollection1) {
        this.cCuentaCollection1 = cCuentaCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<LogLectura> getLogLecturaCollection() {
        return logLecturaCollection;
    }

    public void setLogLecturaCollection(Collection<LogLectura> logLecturaCollection) {
        this.logLecturaCollection = logLecturaCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<LogLectura> getLogLecturaCollection1() {
        return logLecturaCollection1;
    }

    public void setLogLecturaCollection1(Collection<LogLectura> logLecturaCollection1) {
        this.logLecturaCollection1 = logLecturaCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<LogLectura> getLogLecturaCollection2() {
        return logLecturaCollection2;
    }

    public void setLogLecturaCollection2(Collection<LogLectura> logLecturaCollection2) {
        this.logLecturaCollection2 = logLecturaCollection2;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiUsuarioRol> getSiUsuarioRolCollection() {
        return siUsuarioRolCollection;
    }

    public void setSiUsuarioRolCollection(Collection<SiUsuarioRol> siUsuarioRolCollection) {
        this.siUsuarioRolCollection = siUsuarioRolCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiUsuarioRol> getSiUsuarioRolCollection1() {
        return siUsuarioRolCollection1;
    }

    public void setSiUsuarioRolCollection1(Collection<SiUsuarioRol> siUsuarioRolCollection1) {
        this.siUsuarioRolCollection1 = siUsuarioRolCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiUsuarioRol> getSiUsuarioRolCollection2() {
        return siUsuarioRolCollection2;
    }

    public void setSiUsuarioRolCollection2(Collection<SiUsuarioRol> siUsuarioRolCollection2) {
        this.siUsuarioRolCollection2 = siUsuarioRolCollection2;
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
    public Collection<CSeccion> getCSeccionCollection1() {
        return cSeccionCollection1;
    }

    public void setCSeccionCollection1(Collection<CSeccion> cSeccionCollection1) {
        this.cSeccionCollection1 = cSeccionCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiRol> getSiRolCollection() {
        return siRolCollection;
    }

    public void setSiRolCollection(Collection<SiRol> siRolCollection) {
        this.siRolCollection = siRolCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SiRol> getSiRolCollection1() {
        return siRolCollection1;
    }

    public void setSiRolCollection1(Collection<SiRol> siRolCollection1) {
        this.siRolCollection1 = siRolCollection1;
    }

    public CEstado getCEstado() {
        return cEstado;
    }

    public void setCEstado(CEstado cEstado) {
        this.cEstado = cEstado;
    }

    public CLocalidad getCLocalidad() {
        return cLocalidad;
    }

    public void setCLocalidad(CLocalidad cLocalidad) {
        this.cLocalidad = cLocalidad;
    }

    public CMunicipio getCMunicipio() {
        return cMunicipio;
    }

    public void setCMunicipio(CMunicipio cMunicipio) {
        this.cMunicipio = cMunicipio;
    }

    public CSeccion getCSeccion() {
        return cSeccion;
    }

    public void setCSeccion(CSeccion cSeccion) {
        this.cSeccion = cSeccion;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Usuario> getUsuarioCollection() {
        return usuarioCollection;
    }

    public void setUsuarioCollection(Collection<Usuario> usuarioCollection) {
        this.usuarioCollection = usuarioCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Usuario> getUsuarioCollection1() {
        return usuarioCollection1;
    }

    public void setUsuarioCollection1(Collection<Usuario> usuarioCollection1) {
        this.usuarioCollection1 = usuarioCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Usuario> getUsuarioCollection2() {
        return usuarioCollection2;
    }

    public void setUsuarioCollection2(Collection<Usuario> usuarioCollection2) {
        this.usuarioCollection2 = usuarioCollection2;
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
    
}
