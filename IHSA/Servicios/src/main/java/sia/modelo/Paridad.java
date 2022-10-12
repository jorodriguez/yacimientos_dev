/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
 * @author ihsa
 */

@Entity
@Table(name = "PARIDAD")
@SequenceGenerator(sequenceName = "paridad_id_seq", name = "paridad_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Paridad.findAll", query = "SELECT c FROM Paridad c")})
@Getter
@Setter
public class Paridad implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "paridad_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
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
    @Column(name = "FECHA_VALIDO")
    @Temporal(TemporalType.DATE)
    private Date fechaValido;        
    @JoinColumn(name = "MONEDA", referencedColumnName = "ID")
    @ManyToOne
    private Moneda moneda;    
    @JoinColumn(name = "MONEDADES", referencedColumnName = "ID")
    @ManyToOne
    private Moneda monedades;    
    @Size(max = 15)
    @Column(name = "NOMBRE")
    private String nombre;
    @JoinColumn(name = "COMPANIA", referencedColumnName = "RFC")
    @ManyToOne
    private Compania compania;
    
    public Paridad() {
    }

    public Paridad(int id) {
	this.id = id;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (getId() != null ? getId().hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Compania)) {
	    return false;
	}
	Paridad other = (Paridad) object;
	if ((this.getId() == 0 && other.getId() > 0) || (this.getId() > 0 && !(this.getId() == other.getId()))) {
	    return false;
	}
	return true;
    }
}
