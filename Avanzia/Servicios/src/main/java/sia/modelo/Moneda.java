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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author hacosta
 */
@Entity
@Getter
@Setter
@Table(name = "MONEDA")
@SequenceGenerator(sequenceName = "moneda_id_seq", name = "moneda_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Moneda.findAll", query = "SELECT m FROM Moneda m")})

public class Moneda implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "moneda_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 15)
    @Column(name = "NOMBRE")
    private String nombre;
    
    @Column(name = "SIGLAS")
    private String siglas;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
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
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;        
//    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
//    @Column(name = "PARIDAD")
//    private Double paridad;
    @JoinColumn(name = "COMPANIA", referencedColumnName = "RFC")
    @ManyToOne
    private Compania compania;

    public Moneda() {
    }

    public Moneda(Integer id) {
	this.id = id;
    }
    public Moneda(Integer id, String empresa) {
	this.id = id;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Moneda)) {
	    return false;
	}
	Moneda other = (Moneda) object;
	if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
	    return false;
	}
	return true;
    }

    
    public String toString() {
	return "sia.modelo.Moneda[ id=" + id + " ]";
    }

}
