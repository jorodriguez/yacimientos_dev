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
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Entity
@Table(name = "SG_LINEA")
@SequenceGenerator(sequenceName = "sg_linea_id_seq", name = "sg_linea_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgLinea.findAll", query = "SELECT s FROM SgLinea s")})
@Setter
@Getter
public class SgLinea implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_linea_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 16)
    @Column(name = "CUENTA")
    private String cuenta;
    @Size(max = 16)
    @Column(name = "SUBCUENTA")
    private String subcuenta;
    @Size(max = 16)
    @Column(name = "TIPO_LINEA")
    private String tipoLinea;
    @Size(max = 12)
    @Column(name = "NUMERO")
    private String numero;
    @Size(max = 32)
    @Column(name = "EMEI")
    private String Emei;

    @JoinColumn(name = "ESTADO", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatus;

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

    public SgLinea() {

    }

    public SgLinea(int id) {
	this.id = id;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (getId() != null ? getId().hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof SgLinea)) {
	    return false;
	}
	SgLinea other = (SgLinea) object;
	return !((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id)));
    }

}
