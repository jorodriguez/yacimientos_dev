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
import sia.constantes.Constantes;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SI_CIUDAD")
@SequenceGenerator(sequenceName = "si_ciudad_id_seq", name = "si_ciudad_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SiCiudad.findAll", query = "SELECT c FROM SiCiudad c")

})
public class SiCiudad implements Serializable {

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
    @OneToMany(mappedBy = "siCiudadOrigen")
    private Collection<SgItinerario> sgItinerarioCollection;
    @OneToMany(mappedBy = "siCiudadDestino")
    private Collection<SgItinerario> sgItinerarioCollection1;
    @OneToMany(mappedBy = "siCiudad")
    private Collection<SgDireccion> sgDireccionCollection;
    @OneToMany(mappedBy = "siCiudad")
    private Collection<SgViajeCiudad> sgViajeCiudadCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "si_ciudad_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 64)
    @Column(name = "NOMBRE")
    private String nombre;
    @JoinColumn(name = "SI_PAIS", referencedColumnName = "ID")
    @ManyToOne
    private SiPais siPais;
    @JoinColumn(name = "SI_ESTADO", referencedColumnName = "ID")
    @ManyToOne
    private SiEstado siEstado;
    @NotNull
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @Size(max = 32)
    @Column(name = "LONGITUD")
    private String longitud;
    @Size(max = 32)
    @Column(name = "LATITUD")
    private String latitud;

    public SiCiudad() {
    }

    public SiCiudad(int id) {
	this.id = id;
    }

    /**
     * @return the id
     */
    public Integer getId() {
	return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
	this.id = id;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
	return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
	this.nombre = nombre;
    }

    /**
     * @return the siPais
     */
    public SiPais getSiPais() {
	return siPais;
    }

    /**
     * @param siPais the siPais to set
     */
    public void setSiPais(SiPais siPais) {
	this.siPais = siPais;
    }

    /**
     * @return the siEstado
     */
    public SiEstado getSiEstado() {
	return siEstado;
    }

    /**
     * @param siEstado the siEstado to set
     */
    public void setSiEstado(SiEstado siEstado) {
	this.siEstado = siEstado;
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
     * @param fechaGenero the fechaGenero to set
     */
    public void setFechaGenero(Date fechaGenero) {
	this.fechaGenero = fechaGenero;
    }

    /**
     * @return the horaGenero
     */
    public Date getHoraGenero() {
	return horaGenero;
    }

    /**
     * @param horaGenero the horaGenero to set
     */
    public void setHoraGenero(Date horaGenero) {
	this.horaGenero = horaGenero;
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

    
    public int hashCode() {
	int hash = 0;
	hash += (getId() != null ? getId().hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof SiCiudad)) {
	    return false;
	}
	SiCiudad other = (SiCiudad) object;
	if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
	    return false;
	}
	return true;
    }

    
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(this.getClass().getName())
		.append("{")
		.append("id=").append(this.id)
		.append(", nombre=").append(this.nombre)
		.append(", siPais=").append(this.getSiPais() != null ? this.getSiPais().getId() : null)
		.append(", siEstado=").append(this.getSiEstado() != null ? this.getSiEstado().getId() : null)
		.append(", genero=").append(this.genero != null ? this.genero.getId() : null)
		.append(", fechaGenero=").append(this.fechaGenero != null ? (Constantes.FMT_ddMMyyy.format(this.fechaGenero)) : null)
		.append(", horaGenero=").append(this.horaGenero != null ? (Constantes.FMT_HHmmss.format(this.horaGenero)) : null)
		.append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
		.append(", fechaModifico=").append(this.fechaModifico != null ? (Constantes.FMT_ddMMyyy.format(this.fechaModifico)) : null)
		.append(", horaModifico=").append(this.horaModifico != null ? (Constantes.FMT_HHmmss.format(this.horaModifico)) : null)
		.append(", eliminado=").append(this.eliminado)
		.append(", longitud=").append(this.longitud != null ? this.getLongitud() : "")
		.append(", latitud=").append(this.latitud != null ? this.getLatitud() : "")
		.append("}");

	return sb.toString();
    }

    public Collection<SgItinerario> getSgItinerarioCollection() {
	return sgItinerarioCollection;
    }

    public void setSgItinerarioCollection(Collection<SgItinerario> sgItinerarioCollection) {
	this.sgItinerarioCollection = sgItinerarioCollection;
    }

    public Collection<SgItinerario> getSgItinerarioCollection1() {
	return sgItinerarioCollection1;
    }

    public void setSgItinerarioCollection1(Collection<SgItinerario> sgItinerarioCollection1) {
	this.sgItinerarioCollection1 = sgItinerarioCollection1;
    }

    public Collection<SgDireccion> getSgDireccionCollection() {
	return sgDireccionCollection;
    }

    public void setSgDireccionCollection(Collection<SgDireccion> sgDireccionCollection) {
	this.sgDireccionCollection = sgDireccionCollection;
    }

    public Collection<SgViajeCiudad> getSgViajeCiudadCollection() {
	return sgViajeCiudadCollection;
    }

    public void setSgViajeCiudadCollection(Collection<SgViajeCiudad> sgViajeCiudadCollection) {
	this.sgViajeCiudadCollection = sgViajeCiudadCollection;
    }

    /**
     * @return the longitud
     */
    public String getLongitud() {
	return longitud;
    }

    /**
     * @param longitud the longitud to set
     */
    public void setLongitud(String longitud) {
	this.longitud = longitud;
    }

    /**
     * @return the latitud
     */
    public String getLatitud() {
	return latitud;
    }

    /**
     * @param latitud the latitud to set
     */
    public void setLatitud(String latitud) {
	this.latitud = latitud;
    }
}
