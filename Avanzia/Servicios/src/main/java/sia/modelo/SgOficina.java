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
import sia.constantes.Constantes;

/**
 *
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_OFICINA")
@SequenceGenerator(sequenceName = "sg_oficina_id_seq", name = "sg_oficina_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgOficina.findAll", query = "SELECT s FROM SgOficina s")})
public class SgOficina implements Serializable {

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
    @OneToMany(mappedBy = "sgOficina")
    private Collection<SgRutaTerrestre> sgRutaTerrestreCollection;
    @OneToMany(mappedBy = "oficinaDestino")
    private Collection<SgSolicitudViaje> sgSolicitudViajeCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "oficinaOrigen")
    private Collection<SgSolicitudViaje> sgSolicitudViajeCollection1;
    @OneToMany(mappedBy = "sgOficina")
    private Collection<Usuario> usuarioCollection;
    @OneToMany(mappedBy = "sgOficina")
    private Collection<SgViaje> sgViajeCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_oficina_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 64)
    @Column(name = "NOMBRE")
    private String nombre;
    
    @Column(name = "VISTO_BUENO")
    private boolean vistoBueno;
    @Size(max = 15)
    @Column(name = "NUMERO_TELEFONO")
    private String numeroTelefono;
    @JoinColumn(name = "SG_DIRECCION", referencedColumnName = "ID")
    @ManyToOne
    private SgDireccion sgDireccion;
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
    
    public SgOficina() {
    }

    public SgOficina(Integer id) {
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
     * @return the vistoBueno
     */
    public boolean isVistoBueno() {
	return vistoBueno;
    }

    /**
     * @param vistoBueno the vistoBueno to set
     */
    public void setVistoBueno(boolean vistoBueno) {
	this.vistoBueno = vistoBueno;
    }

    /**
     * @return the numeroTelefono
     */
    public String getNumeroTelefono() {
	return numeroTelefono;
    }

    /**
     * @param numeroTelefono the numeroTelefono to set
     */
    public void setNumeroTelefono(String numeroTelefono) {
	this.numeroTelefono = numeroTelefono;
    }

    /**
     * @return the sgDireccion
     */
    public SgDireccion getSgDireccion() {
	return sgDireccion;
    }

    /**
     * @param sgDireccion the sgDireccion to set
     */
    public void setSgDireccion(SgDireccion sgDireccion) {
	this.sgDireccion = sgDireccion;
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
	if (!(object instanceof SgOficina)) {
	    return false;
	}
	SgOficina other = (SgOficina) object;
	if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
	    return false;
	}
	return true;
    }

    
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(this.getClass().getName());
	sb.append("{");
	sb.append("id=").append(this.getId());
	sb.append(", nombre=").append(this.getNombre());
	sb.append(", vistoBueno=").append(this.isVistoBueno());
	sb.append(", numeroTelefono=").append(this.getNumeroTelefono());
	sb.append(", sgDireccion=").append(this.getSgDireccion() != null ? this.getSgDireccion().getId() : null);
	sb.append(", genero=").append(this.getGenero() != null ? this.getGenero().getId() : null);
	sb.append(", fechaGenero=").append(this.getFechaGenero() != null ? (Constantes.FMT_ddMMyyy.format(this.getFechaGenero())) : null);
	sb.append(", horaGenero=").append(this.getHoraGenero() != null ? (Constantes.FMT_HHmmss.format(this.getHoraGenero())) : null);
	sb.append(", modifico=").append(this.getModifico() != null ? this.getModifico().getId() : null);
	sb.append(", fechaModifico=").append(this.getFechaModifico() != null ? (Constantes.FMT_ddMMyyy.format(this.getFechaModifico())) : null);
	sb.append(", horaModifico=").append(this.getHoraModifico() != null ? (Constantes.FMT_HHmmss.format(this.getHoraModifico())) : null);
	sb.append(", eliminado=").append(this.isEliminado());
        sb.append(", longitud=").append(this.getLongitud());
        sb.append(", latitud=").append(this.getLatitud());
	sb.append("}");

	return sb.toString();
    }
 
    public Collection<SgRutaTerrestre> getSgRutaTerrestreCollection() {
	return sgRutaTerrestreCollection;
    }

    public void setSgRutaTerrestreCollection(Collection<SgRutaTerrestre> sgRutaTerrestreCollection) {
	this.sgRutaTerrestreCollection = sgRutaTerrestreCollection;
    }

    public Collection<SgSolicitudViaje> getSgSolicitudViajeCollection() {
	return sgSolicitudViajeCollection;
    }

    public void setSgSolicitudViajeCollection(Collection<SgSolicitudViaje> sgSolicitudViajeCollection) {
	this.sgSolicitudViajeCollection = sgSolicitudViajeCollection;
    }

    public Collection<SgSolicitudViaje> getSgSolicitudViajeCollection1() {
	return sgSolicitudViajeCollection1;
    }

    public void setSgSolicitudViajeCollection1(Collection<SgSolicitudViaje> sgSolicitudViajeCollection1) {
	this.sgSolicitudViajeCollection1 = sgSolicitudViajeCollection1;
    }

    public Collection<Usuario> getUsuarioCollection() {
	return usuarioCollection;
    }

    public void setUsuarioCollection(Collection<Usuario> usuarioCollection) {
	this.usuarioCollection = usuarioCollection;
    }

    public Collection<SgViaje> getSgViajeCollection() {
	return sgViajeCollection;
    }

    public void setSgViajeCollection(Collection<SgViaje> sgViajeCollection) {
	this.sgViajeCollection = sgViajeCollection;
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
