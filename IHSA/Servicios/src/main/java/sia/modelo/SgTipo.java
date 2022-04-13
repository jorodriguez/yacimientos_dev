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
import javax.validation.constraints.Size;

/**
 *
 * 
 * @author mluis
 */
@Entity
@Table(name = "SG_TIPO")
@SequenceGenerator(sequenceName = "sg_tipo_id_seq", name = "sg_tipo_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgTipo.findAll", query = "SELECT s FROM SgTipo s")})
public class SgTipo implements Serializable {

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
    @OneToMany(mappedBy = "sgTipo")
    private Collection<RhTipoGerencia> rhTipoGerenciaCollection;
    @OneToMany(mappedBy = "sgTipo")
    private Collection<SiUsuarioTipo> siUsuarioTipoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sgTipo")
    private Collection<SgVehiculo> sgVehiculoCollection;
    @OneToMany(mappedBy = "sgTipo")
    private Collection<SgAccesorio> sgAccesorioCollection;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sgTipo")
    private Collection<SgMarca> sgMarcaCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_tipo_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 50)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 1024)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @OneToMany(mappedBy = "sgTipo")
    private Collection<SgKilometraje> sgKilometrajeCollection;
    @OneToMany(mappedBy = "sgTipo")
    private Collection<SgHuespedHotel> sgHuespedHotelCollection;
    @OneToMany(mappedBy = "sgTipo")
    private Collection<SgHuespedStaff> sgHuespedStaffCollection;
    @OneToMany(mappedBy = "sgTipo")
    private Collection<SgPagoServicio> sgPagoServicioCollection;
    @OneToMany(mappedBy = "sgTipo")
    private Collection<SgEvento> sgEventoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sgTipo")
    private Collection<SgDetalleSolicitudEstancia> sgDetalleSolicitudEstanciaCollection;
    @OneToMany(mappedBy = "sgTipo")
    private Collection<SgTipoSolicitudViaje> sgTipoSolicitudViajeCollection;
    @OneToMany(mappedBy = "sgTipo")
    private Collection<SgGastoInsumo> sgGastoInsumoCollection;
    @OneToMany(mappedBy = "sgTipo")
    private Collection<SgGastoViaje> sgGastoViajeCollection;
    @OneToMany(mappedBy = "sgTipo")
    private Collection<SgTipoTipoEspecifico> sgTipoTipoEspecificoCollection;
    @OneToMany(mappedBy = "sgTipo")
    private Collection<SgIncidencia> sgIncidenciaCollection;

    public SgTipo() {
    }

    public SgTipo(int sgTipo) {
	this.id = sgTipo;
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

    public String getDescripcion() {
	return descripcion;
    }

    public void setDescripcion(String descripcion) {
	this.descripcion = descripcion;
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

    public boolean isEliminado() {
	return eliminado;
    }

    public void setEliminado(boolean eliminado) {
	this.eliminado = eliminado;
    }

    public Collection<SgKilometraje> getSgKilometrajeCollection() {
	return sgKilometrajeCollection;
    }

    public void setSgKilometrajeCollection(Collection<SgKilometraje> sgKilometrajeCollection) {
	this.sgKilometrajeCollection = sgKilometrajeCollection;
    }

    public Collection<SgHuespedHotel> getSgHuespedHotelCollection() {
	return sgHuespedHotelCollection;
    }

    public void setSgHuespedHotelCollection(Collection<SgHuespedHotel> sgHuespedHotelCollection) {
	this.sgHuespedHotelCollection = sgHuespedHotelCollection;
    }

    public Collection<SgHuespedStaff> getSgHuespedStaffCollection() {
	return sgHuespedStaffCollection;
    }

    public void setSgHuespedStaffCollection(Collection<SgHuespedStaff> sgHuespedStaffCollection) {
	this.sgHuespedStaffCollection = sgHuespedStaffCollection;
    }

    public Collection<SgPagoServicio> getSgPagoServicioCollection() {
	return sgPagoServicioCollection;
    }

    public void setSgPagoServicioCollection(Collection<SgPagoServicio> sgPagoServicioCollection) {
	this.sgPagoServicioCollection = sgPagoServicioCollection;
    }

    public Collection<SgEvento> getSgEventoCollection() {
	return sgEventoCollection;
    }

    public void setSgEventoCollection(Collection<SgEvento> sgEventoCollection) {
	this.sgEventoCollection = sgEventoCollection;
    }

    public Collection<SgDetalleSolicitudEstancia> getSgDetalleSolicitudEstanciaCollection() {
	return sgDetalleSolicitudEstanciaCollection;
    }

    public void setSgDetalleSolicitudEstanciaCollection(Collection<SgDetalleSolicitudEstancia> sgDetalleSolicitudEstanciaCollection) {
	this.sgDetalleSolicitudEstanciaCollection = sgDetalleSolicitudEstanciaCollection;
    }

    public Collection<SgTipoSolicitudViaje> getSgTipoSolicitudViajeCollection() {
	return sgTipoSolicitudViajeCollection;
    }

    public void setSgTipoSolicitudViajeCollection(Collection<SgTipoSolicitudViaje> sgTipoSolicitudViajeCollection) {
	this.sgTipoSolicitudViajeCollection = sgTipoSolicitudViajeCollection;
    }

    public Collection<SgGastoInsumo> getSgGastoInsumoCollection() {
	return sgGastoInsumoCollection;
    }

    public void setSgGastoInsumoCollection(Collection<SgGastoInsumo> sgGastoInsumoCollection) {
	this.sgGastoInsumoCollection = sgGastoInsumoCollection;
    }

    public Usuario getGenero() {
	return genero;
    }

    public void setGenero(Usuario genero) {
	this.genero = genero;
    }

    public Collection<SgGastoViaje> getSgGastoViajeCollection() {
	return sgGastoViajeCollection;
    }

    public void setSgGastoViajeCollection(Collection<SgGastoViaje> sgGastoViajeCollection) {
	this.sgGastoViajeCollection = sgGastoViajeCollection;
    }

    public Collection<SgTipoTipoEspecifico> getSgTipoTipoEspecificoCollection() {
	return sgTipoTipoEspecificoCollection;
    }

    public void setSgTipoTipoEspecificoCollection(Collection<SgTipoTipoEspecifico> sgTipoTipoEspecificoCollection) {
	this.sgTipoTipoEspecificoCollection = sgTipoTipoEspecificoCollection;
    }

    public Collection<SgIncidencia> getSgIncidenciaCollection() {
	return sgIncidenciaCollection;
    }

    public void setSgIncidenciaCollection(Collection<SgIncidencia> sgIncidenciaCollection) {
	this.sgIncidenciaCollection = sgIncidenciaCollection;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof SgTipo)) {
	    return false;
	}
	SgTipo other = (SgTipo) object;
	if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
	    return false;
	}
	return true;
    }

    
    public String toString() {
	return "sia.modelo.SgTipo[ id=" + id + " ]";
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

    public Collection<SgAccesorio> getSgAccesorioCollection() {
	return sgAccesorioCollection;
    }

    public void setSgAccesorioCollection(Collection<SgAccesorio> sgAccesorioCollection) {
	this.sgAccesorioCollection = sgAccesorioCollection;
    }

    public Usuario getModifico() {
	return modifico;
    }

    public void setModifico(Usuario modifico) {
	this.modifico = modifico;
    }

    public Collection<SgMarca> getSgMarcaCollection() {
	return sgMarcaCollection;
    }

    public void setSgMarcaCollection(Collection<SgMarca> sgMarcaCollection) {
	this.sgMarcaCollection = sgMarcaCollection;
    }

    public Collection<SgVehiculo> getSgVehiculoCollection() {
	return sgVehiculoCollection;
    }

    public void setSgVehiculoCollection(Collection<SgVehiculo> sgVehiculoCollection) {
	this.sgVehiculoCollection = sgVehiculoCollection;
    }

    public Collection<SiUsuarioTipo> getSiUsuarioTipoCollection() {
	return siUsuarioTipoCollection;
    }

    public void setSiUsuarioTipoCollection(Collection<SiUsuarioTipo> siUsuarioTipoCollection) {
	this.siUsuarioTipoCollection = siUsuarioTipoCollection;
    }

    public Collection<RhTipoGerencia> getRhTipoGerenciaCollection() {
	return rhTipoGerenciaCollection;
    }

    public void setRhTipoGerenciaCollection(Collection<RhTipoGerencia> rhTipoGerenciaCollection) {
	this.rhTipoGerenciaCollection = rhTipoGerenciaCollection;
    }

}
