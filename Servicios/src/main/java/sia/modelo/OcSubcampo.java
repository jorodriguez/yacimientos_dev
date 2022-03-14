/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import sia.constantes.Constantes;

/**
 *
 * @author jcarranza
 */
@Entity
@Table(name = "OC_SUBCAMPO")
@SequenceGenerator(sequenceName = "oc_subcampo_id_seq", name = "oc_subcampo_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OcSubcampo.findAll", query = "SELECT o FROM OcSubcampo o")})
@Setter
@Getter
public class OcSubcampo implements Serializable {
    
    public OcSubcampo(){
    
    } 
    
    public OcSubcampo(int id){
        this.id = id;
    }

    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<OcRequisicionCoNoticia> ocRequisicionCoNoticiaCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<InvArticuloCampo> invArticuloCampoCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<NotaOrden> notaOrdenCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<ApCampoUsuarioRhPuesto> apCampoUsuarioRhPuestoCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<Usuario> usuarioCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<OcFlujo> ocFlujoCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<Convenio> convenioCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<CadenasMando> cadenasMandoCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<ProyectoOt> proyectoOtCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<Orden> ordenCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<Folio> folioCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<ApCampoGerencia> apCampoGerenciaCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<OcCampoProveedor> ocCampoProveedorCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<OcGerenciaTarea> ocGerenciaTareaCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<RhCampoGerencia> rhCampoGerenciaCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<SiUsuarioRol> siUsuarioRolCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<OcGerenciaProyecto> ocGerenciaProyectoCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<NotaRequisicion> notaRequisicionCollection;
    @OneToMany(mappedBy = "ocSubcampo")
    private Collection<OcOrdenCoNoticia> ocOrdenCoNoticiaCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "oc_subcampo_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 256)
    @Column(name = "NOMBRE")
    private String nombre;
    
    @Column(name = "CODIGO")
    private String codigo;
    @Size(max = 1024)
    @Column(name = "DESCRIPCION")
    private String descripcion;
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
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
//
    //
    
    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OcSubcampo)) {
            return false;
        }
        OcSubcampo other = (OcSubcampo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
            .append("id=").append(this.id)
            .append(", genero=").append(this.genero != null ? this.genero.getId() : null)    
            .append(", nombre=").append(this.nombre)
            .append(", codigo=").append(this.codigo)
            .append(", descripcion=").append(this.descripcion)
            .append(", fechaGenero=").append(this.fechaGenero != null ? (Constantes.FMT_ddMMyyy.format(this.fechaGenero)) : null)
            .append(", horaGenero=").append(this.horaGenero != null ? (Constantes.FMT_HHmmss.format(this.horaGenero)) : null)
            .append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
            .append(", fechaModifico=").append(this.fechaModifico != null ? (Constantes.FMT_ddMMyyy.format(this.fechaModifico)) : null)
            .append(", horaModifico=").append(this.horaModifico != null ? (Constantes.FMT_HHmmss.format(this.horaModifico)) : null)
            .append(", eliminado=").append(this.eliminado)
            .append("}");
        
        return sb.toString();
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
    public Collection<InvArticuloCampo> getInvArticuloCampoCollection() {
        return invArticuloCampoCollection;
    }

    public void setInvArticuloCampoCollection(Collection<InvArticuloCampo> invArticuloCampoCollection) {
        this.invArticuloCampoCollection = invArticuloCampoCollection;
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
    public Collection<ApCampoUsuarioRhPuesto> getApCampoUsuarioRhPuestoCollection() {
        return apCampoUsuarioRhPuestoCollection;
    }

    public void setApCampoUsuarioRhPuestoCollection(Collection<ApCampoUsuarioRhPuesto> apCampoUsuarioRhPuestoCollection) {
        this.apCampoUsuarioRhPuestoCollection = apCampoUsuarioRhPuestoCollection;
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
    public Collection<OcFlujo> getOcFlujoCollection() {
        return ocFlujoCollection;
    }

    public void setOcFlujoCollection(Collection<OcFlujo> ocFlujoCollection) {
        this.ocFlujoCollection = ocFlujoCollection;
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
    public Collection<CadenasMando> getCadenasMandoCollection() {
        return cadenasMandoCollection;
    }

    public void setCadenasMandoCollection(Collection<CadenasMando> cadenasMandoCollection) {
        this.cadenasMandoCollection = cadenasMandoCollection;
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
    public Collection<Orden> getOrdenCollection() {
        return ordenCollection;
    }

    public void setOrdenCollection(Collection<Orden> ordenCollection) {
        this.ordenCollection = ordenCollection;
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
    public Collection<ApCampoGerencia> getApCampoGerenciaCollection() {
        return apCampoGerenciaCollection;
    }

    public void setApCampoGerenciaCollection(Collection<ApCampoGerencia> apCampoGerenciaCollection) {
        this.apCampoGerenciaCollection = apCampoGerenciaCollection;
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
    public Collection<OcGerenciaTarea> getOcGerenciaTareaCollection() {
        return ocGerenciaTareaCollection;
    }

    public void setOcGerenciaTareaCollection(Collection<OcGerenciaTarea> ocGerenciaTareaCollection) {
        this.ocGerenciaTareaCollection = ocGerenciaTareaCollection;
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
    public Collection<SiUsuarioRol> getSiUsuarioRolCollection() {
        return siUsuarioRolCollection;
    }

    public void setSiUsuarioRolCollection(Collection<SiUsuarioRol> siUsuarioRolCollection) {
        this.siUsuarioRolCollection = siUsuarioRolCollection;
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
    public Collection<NotaRequisicion> getNotaRequisicionCollection() {
        return notaRequisicionCollection;
    }

    public void setNotaRequisicionCollection(Collection<NotaRequisicion> notaRequisicionCollection) {
        this.notaRequisicionCollection = notaRequisicionCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcOrdenCoNoticia> getOcOrdenCoNoticiaCollection() {
        return ocOrdenCoNoticiaCollection;
    }

    public void setOcOrdenCoNoticiaCollection(Collection<OcOrdenCoNoticia> ocOrdenCoNoticiaCollection) {
        this.ocOrdenCoNoticiaCollection = ocOrdenCoNoticiaCollection;
    }
}



