/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
@Table(name = "SG_MODELO")
@SequenceGenerator(sequenceName = "sg_modelo_id_seq", name = "sg_modelo_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgModelo.findAll", query = "SELECT s FROM SgModelo s")})
public class SgModelo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_modelo_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 50)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 512)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @JoinColumn(name = "SG_MARCA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgMarca sgMarca;
    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;
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

    public SgModelo() {
    }

    public SgModelo(int idModelo) {
	this.id = idModelo;
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
     * @return the sgMarca
     */
    public SgMarca getSgMarca() {
	return sgMarca;
    }

    /**
     * @param sgMarca the sgMarca to set
     */
    public void setSgMarca(SgMarca sgMarca) {
	this.sgMarca = sgMarca;
    }

    /**
     * @return the sgTipoEspecifico
     */
    public SgTipoEspecifico getSgTipoEspecifico() {
	return sgTipoEspecifico;
    }

    /**
     * @param sgTipoEspecifico the sgTipoEspecifico to set
     */
    public void setSgTipoEspecifico(SgTipoEspecifico sgTipoEspecifico) {
	this.sgTipoEspecifico = sgTipoEspecifico;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (getId() != null ? getId().hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof SgModelo)) {
	    return false;
	}
	SgModelo other = (SgModelo) object;
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
		.append(", sgMarca=").append(this.sgMarca != null ? this.sgMarca.getId() : null)
		.append(", sgTipoEspecifico=").append(this.sgTipoEspecifico != null ? this.sgTipoEspecifico.getId() : null)
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
}
