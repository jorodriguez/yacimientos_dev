/*
 * To change this template, choose Tools | Templates
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "PROVEEDOR_ACTIVIDAD")
@SequenceGenerator(sequenceName = "proveedor_actividad_id_seq", name = "proveedor_actividad_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProveedorActividad.findAll", query = "SELECT p FROM ProveedorActividad p")})
public class ProveedorActividad implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "proveedor_actividad_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "PROVEEDOR", referencedColumnName = "ID")
    @ManyToOne
    private Proveedor proveedor;
    @JoinColumn(name = "ACTIVIDAD", referencedColumnName = "ID")
    @ManyToOne
    private Actividad actividad;
    @OneToMany(mappedBy = "proveedorActividad")
    private Collection<ClasificacionServicio> clasificacionServicioCollection;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;

    public ProveedorActividad() {
    }

    public ProveedorActividad(Integer id) {
	this.id = id;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public Proveedor getProveedor() {
	return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
	this.proveedor = proveedor;
    }

    public Actividad getActividad() {
	return actividad;
    }

    public void setActividad(Actividad actividad) {
	this.actividad = actividad;
    }

    @XmlTransient
    public Collection<ClasificacionServicio> getClasificacionServicioCollection() {
	return clasificacionServicioCollection;
    }

    public void setClasificacionServicioCollection(Collection<ClasificacionServicio> clasificacionServicioCollection) {
	this.clasificacionServicioCollection = clasificacionServicioCollection;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof ProveedorActividad)) {
	    return false;
	}
	ProveedorActividad other = (ProveedorActividad) object;
	if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
	    return false;
	}
	return true;
    }

    
    public String toString() {
	return "sia.modelo.ProveedorActividad[ id=" + id + " ]";
    }

    /**
     * @return the genero
     */
    public Usuario getGenero() {
	return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(Usuario genero) {
	this.genero = genero;
    }

    /**
     * @return the fechaGenero
     */
    public Date getFechaGenero() {
	return fechaGenero;
    }

    /**
     * @return the horaGenero
     */
    public Date getHoraGenero() {
	return horaGenero;
    }

    /**
     * @return the modifico
     */
    public Usuario getModifico() {
	return modifico;
    }

    /**
     * @param modifico the modifico to set
     */
    public void setModifico(Usuario modifico) {
	this.modifico = modifico;
    }

    /**
     * @return the fechaModifico
     */
    public Date getFechaModifico() {
	return fechaModifico;
    }

    /**
     * @param fechaModifico the fechaModifico to set
     */
    public void setFechaModifico(Date fechaModifico) {
	this.fechaModifico = fechaModifico;
    }

    /**
     * @return the horaModifico
     */
    public Date getHoraModifico() {
	return horaModifico;
    }

    /**
     * @param horaModifico the horaModifico to set
     */
    public void setHoraModifico(Date horaModifico) {
	this.horaModifico = horaModifico;
    }

    /**
     * @return the eliminado
     */
    public boolean isEliminado() {
	return eliminado;
    }

    /**
     * @param eliminado the eliminado to set
     */
    public void setEliminado(boolean eliminado) {
	this.eliminado = eliminado;
    }

    /**
     * @param fechaGenero the fechaGenero to set
     */
    public void setFechaGenero(Date fechaGenero) {
	this.fechaGenero = fechaGenero;
    }

    /**
     * @param horaGenero the horaGenero to set
     */
    public void setHoraGenero(Date horaGenero) {
	this.horaGenero = horaGenero;
    }

}
