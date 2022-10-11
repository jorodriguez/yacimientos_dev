/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_TIPO_ESPECIFICO")
@SequenceGenerator(sequenceName = "sg_tipo_especifico_id_seq", name = "sg_tipo_especifico_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgTipoEspecifico.findAll", query = "SELECT s FROM SgTipoEspecifico s")})
public class SgTipoEspecifico implements Serializable {

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
    @OneToMany(mappedBy = "sgTipoEspecifico")
    private Collection<SgRutaTerrestre> sgRutaTerrestreCollection;
    @OneToMany(mappedBy = "sgTipoEspecifico")
    private Collection<SgViaje> sgViajeCollection;
    @OneToMany(mappedBy = "sgTipoEspecifico")
    private Collection<SgDetalleSolicitudEstancia> sgDetalleSolicitudEstanciaCollection;
    @OneToMany(mappedBy = "sgTipoEspecifico")
    private Collection<SgTipoSolicitudViaje> sgTipoSolicitudViajeCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_tipo_especifico_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 64)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 1024)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @Column(name = "PAGO")
    private boolean pago;
    @Basic(optional = false)
    @NotNull
    
    @Column(name = "USADO")
    private boolean usado;
    @Column(name = "SISTEMA")
    private boolean sistema;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;

    public SgTipoEspecifico() {
    }

    public SgTipoEspecifico(Integer id) {
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
     * @return the descripcion
     */
    public String getDescripcion() {
	return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
	this.descripcion = descripcion;
    }

    /**
     * @return the pago
     */
    public boolean isPago() {
	return pago;
    }

    /**
     * @param pago the pago to set
     */
    public void setPago(boolean pago) {
	this.pago = pago;
    }

    /**
     * @return the usado
     */
    public boolean isUsado() {
	return usado;
    }

    /**
     * @param usado the usado to set
     */
    public void setUsado(boolean usado) {
	this.usado = usado;
    }

    /**
     * @return the sistema
     */
    public boolean isSistema() {
	return sistema;
    }

    /**
     * @param sistema the sistema to set
     */
    public void setSistema(boolean sistema) {
	this.sistema = sistema;
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
	if (!(object instanceof SgTipoEspecifico)) {
	    return false;
	}
	SgTipoEspecifico other = (SgTipoEspecifico) object;
	if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
	    return false;
	}
	return true;
    }

    
    public String toString() {
	SimpleDateFormat sdfd = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdft = new SimpleDateFormat("HH:mm:ss");
	StringBuilder sb = new StringBuilder();
	sb.append(this.getClass().getName())
		.append("{")
		.append("id=").append(this.id)
		.append(", nombre=").append(this.nombre)
		.append(", descripcion=").append(this.descripcion)
		.append(", pago=").append(this.pago)
		.append(", usado=").append(this.usado)
		.append(", sistema=").append(this.sistema)
		.append(", genero=").append(this.genero != null ? this.genero.getId() : null)
		.append(", fechaGenero=").append(this.fechaGenero != null ? (sdfd.format(this.fechaGenero)) : null)
		.append(", horaGenero=").append(this.horaGenero != null ? (sdft.format(this.horaGenero)) : null)
		.append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
		.append(", fechaModifico=").append(this.fechaModifico != null ? (sdfd.format(this.fechaModifico)) : null)
		.append(", horaModifico=").append(this.horaModifico != null ? (sdft.format(this.horaModifico)) : null)
		.append(", eliminado=").append(this.eliminado)
		.append("}");

	return sb.toString();
    }

    public Collection<SgTipoSolicitudViaje> getSgTipoSolicitudViajeCollection() {
	return sgTipoSolicitudViajeCollection;
    }

    public void setSgTipoSolicitudViajeCollection(Collection<SgTipoSolicitudViaje> sgTipoSolicitudViajeCollection) {
	this.sgTipoSolicitudViajeCollection = sgTipoSolicitudViajeCollection;
    }

    public Collection<SgDetalleSolicitudEstancia> getSgDetalleSolicitudEstanciaCollection() {
	return sgDetalleSolicitudEstanciaCollection;
    }

    public void setSgDetalleSolicitudEstanciaCollection(Collection<SgDetalleSolicitudEstancia> sgDetalleSolicitudEstanciaCollection) {
	this.sgDetalleSolicitudEstanciaCollection = sgDetalleSolicitudEstanciaCollection;
    }

    public Collection<SgViaje> getSgViajeCollection() {
	return sgViajeCollection;
    }

    public void setSgViajeCollection(Collection<SgViaje> sgViajeCollection) {
	this.sgViajeCollection = sgViajeCollection;
    }

    public Collection<SgRutaTerrestre> getSgRutaTerrestreCollection() {
	return sgRutaTerrestreCollection;
    }

    public void setSgRutaTerrestreCollection(Collection<SgRutaTerrestre> sgRutaTerrestreCollection) {
	this.sgRutaTerrestreCollection = sgRutaTerrestreCollection;
    }

}
