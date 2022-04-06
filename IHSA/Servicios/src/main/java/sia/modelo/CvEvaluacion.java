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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jcarranza
 */
@Entity
@Getter
@Setter
@Table(name = "cv_evaluacion")
@SequenceGenerator(sequenceName = "cv_evaluacion_id_seq", name = "cv_evaluacion_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CvEvaluacion.findAll", query = "SELECT o FROM CvEvaluacion o")})
public class CvEvaluacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator =  "cv_evaluacion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)    
    @Column(name = "ID")
    private Integer id;
    
    @JoinColumn(name = "cv_evaluacion_template", referencedColumnName = "ID")
    @ManyToOne
    private CvEvaluacionTemplate cvEvaluacionTemplate;
    @JoinColumn(name = "PROVEEDOR", referencedColumnName = "ID")
    @ManyToOne
    private Proveedor proveedor;
    @JoinColumn(name = "GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private Gerencia gerencia;
    @JoinColumn(name = "CONVENIO", referencedColumnName = "ID")
    @ManyToOne
    private Convenio convenio;
    @Size(max = 1024)
    @Column(name = "correo")
    private String correo;
    @Size(max = 1024)
    @Column(name = "nombre_gerencia")
    private String nombreGerencia;
    @Size(max = 1024)
    @Column(name = "nombre_proveedor")
    private String nombreProveedor;
    @Column(name = "FECHA")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 8192)
    @Column(name = "observaciones")
    private String observaciones;
    @Size(max = 8192)
    @Column(name = "interpretacion")
    private String interpretacion;
    @Size(max = 8192)
    @Column(name = "notas")
    private String notas;
    
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
    
    @Column(name = "contestada")
    private boolean contestada;
    
    @JoinColumn(name = "responsable", referencedColumnName = "ID")
    @ManyToOne
    private Usuario responsable;

    public CvEvaluacion() {
    }

    public CvEvaluacion(Integer id) {
	this.id = id;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof CvEvaluacion)) {
	    return false;
	}
	CvEvaluacion other = (CvEvaluacion) object;
	return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

}

