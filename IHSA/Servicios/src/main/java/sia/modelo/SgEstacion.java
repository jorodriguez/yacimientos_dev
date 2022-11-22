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
@Table(name = "SG_ESTACION")
@SequenceGenerator(sequenceName = "sg_estacion_id_seq", name = "sg_estacion_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
@NamedQueries({
    @NamedQuery(name = "SgEstacion.buscarPorNumero", query = "SELECT e FROM SgEstacion e where e.numeroEstacion = ?1 and e.eliminado = 'False' ")})
public class SgEstacion implements Serializable {

    private static final long serialVersionUID = 1L;
@GeneratedValue(generator =  "sg_estacion_seq", strategy = GenerationType.SEQUENCE)
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 24)
    @Column(name = "NUMERO_ESTACION")
    private String numeroEstacion;
    @Size(max = 24)
    @Column(name = "RFC")
    private String rfc;

    @JoinColumn(name = "SI_PAIS", referencedColumnName = "ID")
    @ManyToOne
    private SiPais siPais;

    @JoinColumn(name = "SI_ESTADO", referencedColumnName = "ID")
    @ManyToOne
    private SiEstado siEstado;

    @JoinColumn(name = "SI_CIUDAD", referencedColumnName = "ID")
    @ManyToOne
    private SiCiudad siCiudad;

    @Size(max = 256)
    @Column(name = "COLONIA")
    private String colonia;

    @Size(max = 512)
    @Column(name = "CALLE")
    private String calle;

    @Size(max = 512)
    @Column(name = "CIUDAD_SIN_REGISTRO")
    private String ciudadSinRegistro;

    @Size(max = 32)
    @Column(name = "NUMERO")
    private String numero;
    //
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

    public SgEstacion() {

    }

    public SgEstacion(int idAccesorio) {
	this.id = idAccesorio;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (getId() != null ? getId().hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	if (!(object instanceof SgEstacion)) {
	    return false;
	}
	SgEstacion other = (SgEstacion) object;
	return !((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id)));
    }

    
    public String toString() {
	return "sia.modelo.SgEstacion[ id=" + id + " ]";
    }
}
