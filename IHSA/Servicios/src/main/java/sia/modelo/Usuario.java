/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import sia.modelo.usuario.vo.UsuarioVO;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "USUARIO")
@SequenceGenerator(sequenceName = "usuario_id_seq", name = "usuario_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "Usuario.findById", query = "SELECT u FROM Usuario u where u.id = ?1 and  u.interno = true and u.eliminado = false and u.activo = true"),
    @NamedQuery(name = "Usuario.findByIdRH", query = "SELECT u FROM Usuario u where u.id = ?1"),
            @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u where  u.interno = true and u.eliminado = false and u.activo = true")
})
@Getter
@Setter
public class Usuario implements Serializable {

    @OneToMany(mappedBy = "genero")
    private Collection<OcRequisicionCoNoticia> ocRequisicionCoNoticiaCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<OcRequisicionCoNoticia> ocRequisicionCoNoticiaCollection1;
    @OneToMany(mappedBy = "genero")
    private Collection<InvArticuloCampo> invArticuloCampoCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<InvArticuloCampo> invArticuloCampoCollection1;
    @OneToMany(mappedBy = "genero")
    private Collection<NotaOrden> notaOrdenCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<NotaOrden> notaOrdenCollection1;
    @OneToMany(mappedBy = "autor")
    private Collection<NotaOrden> notaOrdenCollection2;
    @OneToMany(mappedBy = "genero")
    private Collection<OcSubcampo> ocSubcampoCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<OcSubcampo> ocSubcampoCollection1;
    @OneToMany(mappedBy = "genero")
    private Collection<ApCampoUsuarioRhPuesto> apCampoUsuarioRhPuestoCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<ApCampoUsuarioRhPuesto> apCampoUsuarioRhPuestoCollection1;
    @OneToMany(mappedBy = "usuario")
    private Collection<ApCampoUsuarioRhPuesto> apCampoUsuarioRhPuestoCollection2;
    @JoinColumn(name = "OC_SUBCAMPO", referencedColumnName = "ID")
    @ManyToOne
    private OcSubcampo ocSubcampo;
    @OneToMany(mappedBy = "genero")
    private Collection<Usuario> usuarioCollection;
    @OneToMany(mappedBy = "jefeDirecto")
    private Collection<Usuario> usuarioCollection1;
    @OneToMany(mappedBy = "modifico")
    private Collection<Usuario> usuarioCollection2;
    @OneToMany(mappedBy = "genero")
    private Collection<OcFlujo> ocFlujoCollection;
    @OneToMany(mappedBy = "ejecuta")
    private Collection<OcFlujo> ocFlujoCollection1;
    @OneToMany(mappedBy = "modifico")
    private Collection<OcFlujo> ocFlujoCollection2;
    @OneToMany(mappedBy = "genero")
    private Collection<Convenio> convenioCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<Convenio> convenioCollection1;
    @OneToMany(mappedBy = "genero")
    private Collection<CadenasMando> cadenasMandoCollection;
    @OneToMany(mappedBy = "revisa")
    private Collection<CadenasMando> cadenasMandoCollection1;
    @OneToMany(mappedBy = "modifico")
    private Collection<CadenasMando> cadenasMandoCollection2;
    @OneToMany(mappedBy = "aprueba")
    private Collection<CadenasMando> cadenasMandoCollection3;
    @OneToMany(mappedBy = "usuario")
    private Collection<CadenasMando> cadenasMandoCollection4;
    @OneToMany(mappedBy = "autorizaIhsa")
    private Collection<CadenasMando> cadenasMandoCollection5;
    @OneToMany(mappedBy = "autorizaMpg")
    private Collection<CadenasMando> cadenasMandoCollection6;
    @OneToMany(mappedBy = "genero")
    private Collection<ProyectoOt> proyectoOtCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<ProyectoOt> proyectoOtCollection1;
    @OneToMany(mappedBy = "genero")
    private Collection<Orden> ordenCollection;
    @OneToMany(mappedBy = "analista")
    private Collection<Orden> ordenCollection1;
    @OneToMany(mappedBy = "contactoCompania")
    private Collection<Orden> ordenCollection2;
    @OneToMany(mappedBy = "gerenteCompras")
    private Collection<Orden> ordenCollection3;
    @OneToMany(mappedBy = "modifico")
    private Collection<Orden> ordenCollection4;
    @OneToMany(mappedBy = "responsableGerencia")
    private Collection<Orden> ordenCollection5;
    @OneToMany(mappedBy = "genero")
    private Collection<Folio> folioCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<Folio> folioCollection1;
    @OneToMany(mappedBy = "genero")
    private Collection<ApCampoGerencia> apCampoGerenciaCollection;
    @OneToMany(mappedBy = "responsable")
    private Collection<ApCampoGerencia> apCampoGerenciaCollection1;
    @OneToMany(mappedBy = "modifico")
    private Collection<ApCampoGerencia> apCampoGerenciaCollection2;
    @OneToMany(mappedBy = "genero")
    private Collection<OcCampoProveedor> ocCampoProveedorCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<OcCampoProveedor> ocCampoProveedorCollection1;
    @OneToMany(mappedBy = "genero")
    private Collection<OcGerenciaTarea> ocGerenciaTareaCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<OcGerenciaTarea> ocGerenciaTareaCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "genero")
    private Collection<RhCampoGerencia> rhCampoGerenciaCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<RhCampoGerencia> rhCampoGerenciaCollection1;
    @OneToMany(mappedBy = "genero")
    private Collection<OcGerenciaProyecto> ocGerenciaProyectoCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<OcGerenciaProyecto> ocGerenciaProyectoCollection1;
    @OneToMany(mappedBy = "genero")
    private Collection<NotaRequisicion> notaRequisicionCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<NotaRequisicion> notaRequisicionCollection1;
    @OneToMany(mappedBy = "autor")
    private Collection<NotaRequisicion> notaRequisicionCollection2;
    @OneToMany(mappedBy = "genero")
    private Collection<OcOrdenCoNoticia> ocOrdenCoNoticiaCollection;
    @OneToMany(mappedBy = "modifico")
    private Collection<OcOrdenCoNoticia> ocOrdenCoNoticiaCollection1;

    @Column(name = "FECHANACIMIENTO")
    @Temporal(TemporalType.DATE)
    private Date fechanacimiento;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @Column(name = "FECHA_INGRESO")
    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;
    @Column(name = "FECHA_BAJA")
    @Temporal(TemporalType.DATE)
    private Date fechaBaja;
    @Column(name = "HORA_BAJA")
    @Temporal(TemporalType.TIME)
    private Date horaBaja;
    
    @Column(name = "REQUIERE_CORREO")
    private boolean requiereCorreo;
//
    @Column(name = "MOTIVO_BAJA")
    private String motivoBaja;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Size(min = 1, max = 20)
    @Column(name = "ID")
    private String id;
    @Size(max = 128)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 50)
    @Column(name = "CLAVE")
    private String clave;
    @Size(max = 120)
    @Column(name = "EMAIL")
    private String email;
    @Size(max = 300)
    @Column(name = "DESTINATARIOS")
    private String destinatarios;
    @Size(max = 25)
    @Column(name = "TELEFONO")
    private String telefono;
    @Size(max = 6)
    @Column(name = "EXTENSION")
    private String extension;
    @Size(max = 1)
    @Column(name = "SEXO")
    private String sexo;
    @Size(max = 25)
    @Column(name = "CELULAR")
    private String celular;
    @Size(max = 13)
    @Column(name = "RFC")
    private String rfc;
    @Size(max = 600)
    @Column(name = "FOTO")
    private String foto;
    
    @Size(max = 50)
    @Column(name = "PREGUNTA_SECRETA")
    private String preguntaSecreta;
    @Size(max = 50)
    @Column(name = "RESPUESTA_PREGUNTA_SECRETA")
    private String respuestaPreguntaSecreta;
    
    @Column(name = "ACTIVO")
    private boolean activo;
    
    @Column(name = "SEGURIDAD")
    private boolean seguridad;
    @JoinColumn(name = "GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private Gerencia gerencia;
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;
    //
    @JoinColumn(name = "SG_OFICINA", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina sgOficina;
    @JoinColumn(name = "JEFE_DIRECTO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario jefeDirecto;
    @JoinColumn(name = "SG_EMPRESA", referencedColumnName = "ID")
    @ManyToOne
    private SgEmpresa sgEmpresa;
    
    @Column(name = "GAFETE")
    private boolean gafete;

    @OneToMany(mappedBy = "usuario")
    private Collection<SiUsuarioRol> siUsuarioRolCollection;
    //
    @Transient
    private Collection<SiRol> siRolCollection;
    
    @Column(name = "INTERNO")
    private boolean interno;

    @Column(name = "USUARIO_DIRECTORIO")
    private String usuarioDirectorio;
    
    @Column(name = "MONITOR_CML_VISIBLE")
    private boolean monitorCmlVisible;
    
    
    public Usuario() {
    }

    public Usuario(String sesion) {
	this.id = sesion;
    }

    public Collection<SiUsuarioRol> getSiUsuarioRolCollection() {
	return siUsuarioRolCollection;
    }

    public void setSiUsuarioRolCollection(Collection<SiUsuarioRol> siUsuarioRolCollection) {
	this.siUsuarioRolCollection = siUsuarioRolCollection;
    }

    public Collection<SiRol> getSiRolCollection() {
	return siRolCollection;
    }

    public void setSiRolCollection(Collection<SiRol> siRolCollection) {
	this.siRolCollection = siRolCollection;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (getId() != null ? getId().hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Usuario)) {
	    return false;
	}
	Usuario other = (Usuario) object;
	if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
	    return false;
	}
	return true;
    }

    
    public String toString() {
	return "sia.modelo.Usuario[ id=" + id + " ]";
    }

    public UsuarioVO toVO() {
	return new UsuarioVO(id, nombre, email);
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcRequisicionCoNoticia> getOcRequisicionCoNoticiaCollection() {
        return ocRequisicionCoNoticiaCollection;
    }

    public void setOcRequisicionCoNoticiaCollection(Collection<OcRequisicionCoNoticia> ocRequisicionCoNoticiaCollection) {
        this.ocRequisicionCoNoticiaCollection = ocRequisicionCoNoticiaCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcRequisicionCoNoticia> getOcRequisicionCoNoticiaCollection1() {
        return ocRequisicionCoNoticiaCollection1;
    }

    public void setOcRequisicionCoNoticiaCollection1(Collection<OcRequisicionCoNoticia> ocRequisicionCoNoticiaCollection1) {
        this.ocRequisicionCoNoticiaCollection1 = ocRequisicionCoNoticiaCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<InvArticuloCampo> getInvArticuloCampoCollection() {
        return invArticuloCampoCollection;
    }

    public void setInvArticuloCampoCollection(Collection<InvArticuloCampo> invArticuloCampoCollection) {
        this.invArticuloCampoCollection = invArticuloCampoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<InvArticuloCampo> getInvArticuloCampoCollection1() {
        return invArticuloCampoCollection1;
    }

    public void setInvArticuloCampoCollection1(Collection<InvArticuloCampo> invArticuloCampoCollection1) {
        this.invArticuloCampoCollection1 = invArticuloCampoCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<NotaOrden> getNotaOrdenCollection() {
        return notaOrdenCollection;
    }

    public void setNotaOrdenCollection(Collection<NotaOrden> notaOrdenCollection) {
        this.notaOrdenCollection = notaOrdenCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<NotaOrden> getNotaOrdenCollection1() {
        return notaOrdenCollection1;
    }

    public void setNotaOrdenCollection1(Collection<NotaOrden> notaOrdenCollection1) {
        this.notaOrdenCollection1 = notaOrdenCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<NotaOrden> getNotaOrdenCollection2() {
        return notaOrdenCollection2;
    }

    public void setNotaOrdenCollection2(Collection<NotaOrden> notaOrdenCollection2) {
        this.notaOrdenCollection2 = notaOrdenCollection2;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcSubcampo> getOcSubcampoCollection() {
        return ocSubcampoCollection;
    }

    public void setOcSubcampoCollection(Collection<OcSubcampo> ocSubcampoCollection) {
        this.ocSubcampoCollection = ocSubcampoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcSubcampo> getOcSubcampoCollection1() {
        return ocSubcampoCollection1;
    }

    public void setOcSubcampoCollection1(Collection<OcSubcampo> ocSubcampoCollection1) {
        this.ocSubcampoCollection1 = ocSubcampoCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<ApCampoUsuarioRhPuesto> getApCampoUsuarioRhPuestoCollection() {
        return apCampoUsuarioRhPuestoCollection;
    }

    public void setApCampoUsuarioRhPuestoCollection(Collection<ApCampoUsuarioRhPuesto> apCampoUsuarioRhPuestoCollection) {
        this.apCampoUsuarioRhPuestoCollection = apCampoUsuarioRhPuestoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<ApCampoUsuarioRhPuesto> getApCampoUsuarioRhPuestoCollection1() {
        return apCampoUsuarioRhPuestoCollection1;
    }

    public void setApCampoUsuarioRhPuestoCollection1(Collection<ApCampoUsuarioRhPuesto> apCampoUsuarioRhPuestoCollection1) {
        this.apCampoUsuarioRhPuestoCollection1 = apCampoUsuarioRhPuestoCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<ApCampoUsuarioRhPuesto> getApCampoUsuarioRhPuestoCollection2() {
        return apCampoUsuarioRhPuestoCollection2;
    }

    public void setApCampoUsuarioRhPuestoCollection2(Collection<ApCampoUsuarioRhPuesto> apCampoUsuarioRhPuestoCollection2) {
        this.apCampoUsuarioRhPuestoCollection2 = apCampoUsuarioRhPuestoCollection2;
    }

    public OcSubcampo getOcSubcampo() {
        return ocSubcampo;
    }

    public void setOcSubcampo(OcSubcampo ocSubcampo) {
        this.ocSubcampo = ocSubcampo;
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
    public Collection<OcFlujo> getOcFlujoCollection() {
        return ocFlujoCollection;
    }

    public void setOcFlujoCollection(Collection<OcFlujo> ocFlujoCollection) {
        this.ocFlujoCollection = ocFlujoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcFlujo> getOcFlujoCollection1() {
        return ocFlujoCollection1;
    }

    public void setOcFlujoCollection1(Collection<OcFlujo> ocFlujoCollection1) {
        this.ocFlujoCollection1 = ocFlujoCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcFlujo> getOcFlujoCollection2() {
        return ocFlujoCollection2;
    }

    public void setOcFlujoCollection2(Collection<OcFlujo> ocFlujoCollection2) {
        this.ocFlujoCollection2 = ocFlujoCollection2;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Convenio> getConvenioCollection() {
        return convenioCollection;
    }

    public void setConvenioCollection(Collection<Convenio> convenioCollection) {
        this.convenioCollection = convenioCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Convenio> getConvenioCollection1() {
        return convenioCollection1;
    }

    public void setConvenioCollection1(Collection<Convenio> convenioCollection1) {
        this.convenioCollection1 = convenioCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CadenasMando> getCadenasMandoCollection() {
        return cadenasMandoCollection;
    }

    public void setCadenasMandoCollection(Collection<CadenasMando> cadenasMandoCollection) {
        this.cadenasMandoCollection = cadenasMandoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CadenasMando> getCadenasMandoCollection1() {
        return cadenasMandoCollection1;
    }

    public void setCadenasMandoCollection1(Collection<CadenasMando> cadenasMandoCollection1) {
        this.cadenasMandoCollection1 = cadenasMandoCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CadenasMando> getCadenasMandoCollection2() {
        return cadenasMandoCollection2;
    }

    public void setCadenasMandoCollection2(Collection<CadenasMando> cadenasMandoCollection2) {
        this.cadenasMandoCollection2 = cadenasMandoCollection2;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CadenasMando> getCadenasMandoCollection3() {
        return cadenasMandoCollection3;
    }

    public void setCadenasMandoCollection3(Collection<CadenasMando> cadenasMandoCollection3) {
        this.cadenasMandoCollection3 = cadenasMandoCollection3;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CadenasMando> getCadenasMandoCollection4() {
        return cadenasMandoCollection4;
    }

    public void setCadenasMandoCollection4(Collection<CadenasMando> cadenasMandoCollection4) {
        this.cadenasMandoCollection4 = cadenasMandoCollection4;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CadenasMando> getCadenasMandoCollection5() {
        return cadenasMandoCollection5;
    }

    public void setCadenasMandoCollection5(Collection<CadenasMando> cadenasMandoCollection5) {
        this.cadenasMandoCollection5 = cadenasMandoCollection5;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CadenasMando> getCadenasMandoCollection6() {
        return cadenasMandoCollection6;
    }

    public void setCadenasMandoCollection6(Collection<CadenasMando> cadenasMandoCollection6) {
        this.cadenasMandoCollection6 = cadenasMandoCollection6;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<ProyectoOt> getProyectoOtCollection() {
        return proyectoOtCollection;
    }

    public void setProyectoOtCollection(Collection<ProyectoOt> proyectoOtCollection) {
        this.proyectoOtCollection = proyectoOtCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<ProyectoOt> getProyectoOtCollection1() {
        return proyectoOtCollection1;
    }

    public void setProyectoOtCollection1(Collection<ProyectoOt> proyectoOtCollection1) {
        this.proyectoOtCollection1 = proyectoOtCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Orden> getOrdenCollection() {
        return ordenCollection;
    }

    public void setOrdenCollection(Collection<Orden> ordenCollection) {
        this.ordenCollection = ordenCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Orden> getOrdenCollection1() {
        return ordenCollection1;
    }

    public void setOrdenCollection1(Collection<Orden> ordenCollection1) {
        this.ordenCollection1 = ordenCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Orden> getOrdenCollection2() {
        return ordenCollection2;
    }

    public void setOrdenCollection2(Collection<Orden> ordenCollection2) {
        this.ordenCollection2 = ordenCollection2;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Orden> getOrdenCollection3() {
        return ordenCollection3;
    }

    public void setOrdenCollection3(Collection<Orden> ordenCollection3) {
        this.ordenCollection3 = ordenCollection3;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Orden> getOrdenCollection4() {
        return ordenCollection4;
    }

    public void setOrdenCollection4(Collection<Orden> ordenCollection4) {
        this.ordenCollection4 = ordenCollection4;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Orden> getOrdenCollection5() {
        return ordenCollection5;
    }

    public void setOrdenCollection5(Collection<Orden> ordenCollection5) {
        this.ordenCollection5 = ordenCollection5;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Folio> getFolioCollection() {
        return folioCollection;
    }

    public void setFolioCollection(Collection<Folio> folioCollection) {
        this.folioCollection = folioCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Folio> getFolioCollection1() {
        return folioCollection1;
    }

    public void setFolioCollection1(Collection<Folio> folioCollection1) {
        this.folioCollection1 = folioCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<ApCampoGerencia> getApCampoGerenciaCollection() {
        return apCampoGerenciaCollection;
    }

    public void setApCampoGerenciaCollection(Collection<ApCampoGerencia> apCampoGerenciaCollection) {
        this.apCampoGerenciaCollection = apCampoGerenciaCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<ApCampoGerencia> getApCampoGerenciaCollection1() {
        return apCampoGerenciaCollection1;
    }

    public void setApCampoGerenciaCollection1(Collection<ApCampoGerencia> apCampoGerenciaCollection1) {
        this.apCampoGerenciaCollection1 = apCampoGerenciaCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<ApCampoGerencia> getApCampoGerenciaCollection2() {
        return apCampoGerenciaCollection2;
    }

    public void setApCampoGerenciaCollection2(Collection<ApCampoGerencia> apCampoGerenciaCollection2) {
        this.apCampoGerenciaCollection2 = apCampoGerenciaCollection2;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcCampoProveedor> getOcCampoProveedorCollection() {
        return ocCampoProveedorCollection;
    }

    public void setOcCampoProveedorCollection(Collection<OcCampoProveedor> ocCampoProveedorCollection) {
        this.ocCampoProveedorCollection = ocCampoProveedorCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcCampoProveedor> getOcCampoProveedorCollection1() {
        return ocCampoProveedorCollection1;
    }

    public void setOcCampoProveedorCollection1(Collection<OcCampoProveedor> ocCampoProveedorCollection1) {
        this.ocCampoProveedorCollection1 = ocCampoProveedorCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcGerenciaTarea> getOcGerenciaTareaCollection() {
        return ocGerenciaTareaCollection;
    }

    public void setOcGerenciaTareaCollection(Collection<OcGerenciaTarea> ocGerenciaTareaCollection) {
        this.ocGerenciaTareaCollection = ocGerenciaTareaCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcGerenciaTarea> getOcGerenciaTareaCollection1() {
        return ocGerenciaTareaCollection1;
    }

    public void setOcGerenciaTareaCollection1(Collection<OcGerenciaTarea> ocGerenciaTareaCollection1) {
        this.ocGerenciaTareaCollection1 = ocGerenciaTareaCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RhCampoGerencia> getRhCampoGerenciaCollection() {
        return rhCampoGerenciaCollection;
    }

    public void setRhCampoGerenciaCollection(Collection<RhCampoGerencia> rhCampoGerenciaCollection) {
        this.rhCampoGerenciaCollection = rhCampoGerenciaCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RhCampoGerencia> getRhCampoGerenciaCollection1() {
        return rhCampoGerenciaCollection1;
    }

    public void setRhCampoGerenciaCollection1(Collection<RhCampoGerencia> rhCampoGerenciaCollection1) {
        this.rhCampoGerenciaCollection1 = rhCampoGerenciaCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcGerenciaProyecto> getOcGerenciaProyectoCollection() {
        return ocGerenciaProyectoCollection;
    }

    public void setOcGerenciaProyectoCollection(Collection<OcGerenciaProyecto> ocGerenciaProyectoCollection) {
        this.ocGerenciaProyectoCollection = ocGerenciaProyectoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcGerenciaProyecto> getOcGerenciaProyectoCollection1() {
        return ocGerenciaProyectoCollection1;
    }

    public void setOcGerenciaProyectoCollection1(Collection<OcGerenciaProyecto> ocGerenciaProyectoCollection1) {
        this.ocGerenciaProyectoCollection1 = ocGerenciaProyectoCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<NotaRequisicion> getNotaRequisicionCollection() {
        return notaRequisicionCollection;
    }

    public void setNotaRequisicionCollection(Collection<NotaRequisicion> notaRequisicionCollection) {
        this.notaRequisicionCollection = notaRequisicionCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<NotaRequisicion> getNotaRequisicionCollection1() {
        return notaRequisicionCollection1;
    }

    public void setNotaRequisicionCollection1(Collection<NotaRequisicion> notaRequisicionCollection1) {
        this.notaRequisicionCollection1 = notaRequisicionCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<NotaRequisicion> getNotaRequisicionCollection2() {
        return notaRequisicionCollection2;
    }

    public void setNotaRequisicionCollection2(Collection<NotaRequisicion> notaRequisicionCollection2) {
        this.notaRequisicionCollection2 = notaRequisicionCollection2;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcOrdenCoNoticia> getOcOrdenCoNoticiaCollection() {
        return ocOrdenCoNoticiaCollection;
    }

    public void setOcOrdenCoNoticiaCollection(Collection<OcOrdenCoNoticia> ocOrdenCoNoticiaCollection) {
        this.ocOrdenCoNoticiaCollection = ocOrdenCoNoticiaCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcOrdenCoNoticia> getOcOrdenCoNoticiaCollection1() {
        return ocOrdenCoNoticiaCollection1;
    }

    public void setOcOrdenCoNoticiaCollection1(Collection<OcOrdenCoNoticia> ocOrdenCoNoticiaCollection1) {
        this.ocOrdenCoNoticiaCollection1 = ocOrdenCoNoticiaCollection1;
    }

}
