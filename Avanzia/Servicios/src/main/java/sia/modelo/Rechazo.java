/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "RECHAZO")
@SequenceGenerator(sequenceName = "rechazo_id_seq", name = "rechazo_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rechazo.findAll", query = "SELECT r FROM Rechazo r")})
public class Rechazo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "rechazo_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Column(name = "FECHA")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "HORA")
    @Temporal(TemporalType.TIME)
    private Date hora;
    @Lob
    @Column(name = "MOTIVO")
    private String motivo;
    
    @Column(name = "CUMPLIDO")
    private boolean cumplido;
    @JoinColumn(name = "RECHAZO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario rechazo;
    @JoinColumn(name = "REQUISICION", referencedColumnName = "ID")
    @ManyToOne
    private Requisicion requisicion;

    public Rechazo() {
    }

    public Rechazo(Integer id) {
	this.id = id;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public Date getFecha() {
	return fecha;
    }

    public void setFecha(Date fecha) {
	this.fecha = fecha;
    }

    public Date getHora() {
	return hora;
    }

    public void setHora(Date hora) {
	this.hora = hora;
    }

    public String getMotivo() {
	return motivo;
    }

    public void setMotivo(String motivo) {
	this.motivo = motivo;
    }

    public boolean isCumplido() {
	return cumplido;
    }

    public void setCumplido(boolean cumplido) {
	this.cumplido = cumplido;
    }

    public Usuario getRechazo() {
	return rechazo;
    }

    public void setRechazo(Usuario rechazo) {
	this.rechazo = rechazo;
    }

    public Requisicion getRequisicion() {
	return requisicion;
    }

    public void setRequisicion(Requisicion requisicion) {
	this.requisicion = requisicion;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Rechazo)) {
	    return false;
	}
	Rechazo other = (Rechazo) object;
	if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
	    return false;
	}
	return true;
    }

    
    public String toString() {
	return "sia.modelo.Rechazo[ id=" + id + " ]";
    }

}
