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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_CHECKLIST")
@SequenceGenerator(sequenceName = "sg_checklist_id_seq", name = "sg_checklist_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgChecklist.findAll", query = "SELECT s FROM SgChecklist s")})
public class SgChecklist implements Serializable {

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
    @Column(name = "FECHA_INICIO_SEMANA")
    @Temporal(TemporalType.DATE)
    private Date fechaInicioSemana;
    @Column(name = "FECHA_FIN_SEMANA")
    @Temporal(TemporalType.DATE)
    private Date fechaFinSemana;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @OneToMany(mappedBy = "sgChecklist")
    private Collection<SgAsignarVehiculo> sgAsignarVehiculoCollection;

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_checklist_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "MODIFICADO")
    private boolean modificado;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @OneToMany(mappedBy = "sgChecklist")
    private Collection<SgVehiculoChecklist> sgVehiculoChecklistCollection;
    @OneToMany(mappedBy = "sgChecklist")
    private Collection<SgChecklistLlantas> sgChecklistLlantasCollection;
    @OneToMany(mappedBy = "sgChecklist")
    private Collection<SgChecklistExtVehiculo> sgChecklistExtVehiculoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sgChecklist")
    private Collection<SgOficinaChecklist> sgOficinaChecklistCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sgChecklist")
    private Collection<SgStaffChecklist> sgStaffChecklistCollection;
    @OneToMany(mappedBy = "sgChecklist")
    private Collection<SgChecklistDetalle> sgChecklistDetalleCollection;

    public SgChecklist() {
    }

    public SgChecklist(int idChecklist) {
	this.id = idChecklist;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public boolean isModificado() {
	return modificado;
    }

    public void setModificado(boolean modificado) {
	this.modificado = modificado;
    }

    public Date getFechaGenero() {
	return fechaGenero;
    }

    public void setFechaGenero(Date fechaGenero) {
	this.fechaGenero = fechaGenero;
    }

    public Date getHoraGenero() {
	return horaGenero;
    }

    public void setHoraGenero(Date horaGenero) {
	this.horaGenero = horaGenero;
    }

    public Date getFechaModifico() {
	return fechaModifico;
    }

    public void setFechaModifico(Date fechaModifico) {
	this.fechaModifico = fechaModifico;
    }

    public Date getHoraModifico() {
	return horaModifico;
    }

    public void setHoraModifico(Date horaModifico) {
	this.horaModifico = horaModifico;
    }

    public boolean isEliminado() {
	return eliminado;
    }

    public void setEliminado(boolean eliminado) {
	this.eliminado = eliminado;
    }

    public Date getFechaInicioSemana() {
	return fechaInicioSemana;
    }

    public void setFechaInicioSemana(Date fechaInicioSemana) {
	this.fechaInicioSemana = fechaInicioSemana;
    }

    public Date getFechaFinSemana() {
	return fechaFinSemana;
    }

    public void setFechaFinSemana(Date fechaFinSemana) {
	this.fechaFinSemana = fechaFinSemana;
    }

    public Collection<SgOficinaChecklist> getSgOficinaChecklistCollection() {
	return sgOficinaChecklistCollection;
    }

    public void setSgOficinaChecklistCollection(Collection<SgOficinaChecklist> sgOficinaChecklistCollection) {
	this.sgOficinaChecklistCollection = sgOficinaChecklistCollection;
    }

    public Collection<SgStaffChecklist> getSgStaffChecklistCollection() {
	return sgStaffChecklistCollection;
    }

    public void setSgStaffChecklistCollection(Collection<SgStaffChecklist> sgStaffChecklistCollection) {
	this.sgStaffChecklistCollection = sgStaffChecklistCollection;
    }

    public Usuario getGenero() {
	return genero;
    }

    public void setGenero(Usuario genero) {
	this.genero = genero;
    }

    public Collection<SgChecklistDetalle> getSgChecklistDetalleCollection() {
	return sgChecklistDetalleCollection;
    }

    public void setSgChecklistDetalleCollection(Collection<SgChecklistDetalle> sgChecklistDetalleCollection) {
	this.sgChecklistDetalleCollection = sgChecklistDetalleCollection;
    }

    public Collection<SgVehiculoChecklist> getSgVehiculoChecklistCollection() {
	return sgVehiculoChecklistCollection;
    }

    public void setSgVehiculoChecklistCollection(Collection<SgVehiculoChecklist> sgVehiculoChecklistCollection) {
	this.sgVehiculoChecklistCollection = sgVehiculoChecklistCollection;
    }

    public Collection<SgChecklistLlantas> getSgChecklistLlantasCollection() {
	return sgChecklistLlantasCollection;
    }

    public void setSgChecklistLlantasCollection(Collection<SgChecklistLlantas> sgChecklistLlantasCollection) {
	this.sgChecklistLlantasCollection = sgChecklistLlantasCollection;
    }

    public Collection<SgChecklistExtVehiculo> getSgChecklistExtVehiculoCollection() {
	return sgChecklistExtVehiculoCollection;
    }

    public void setSgChecklistExtVehiculoCollection(Collection<SgChecklistExtVehiculo> sgChecklistExtVehiculoCollection) {
	this.sgChecklistExtVehiculoCollection = sgChecklistExtVehiculoCollection;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof SgChecklist)) {
	    return false;
	}
	SgChecklist other = (SgChecklist) object;
	if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
	    return false;
	}
	return true;
    }

    
    public String toString() {
	return "sia.modelo.SgChecklist[ id=" + id + " ]";
    }

    public Usuario getModifico() {
	return modifico;
    }

    public void setModifico(Usuario modifico) {
	this.modifico = modifico;
    }

    public Collection<SgAsignarVehiculo> getSgAsignarVehiculoCollection() {
	return sgAsignarVehiculoCollection;
    }

    public void setSgAsignarVehiculoCollection(Collection<SgAsignarVehiculo> sgAsignarVehiculoCollection) {
	this.sgAsignarVehiculoCollection = sgAsignarVehiculoCollection;
    }
}
