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
@Table(name = "cv_evaluacion_resp")
@SequenceGenerator(sequenceName = "cv_evaluacion_resp_id_seq", name = "cv_evaluacion_resp_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CvEvaluacionResp.findAll", query = "SELECT o FROM CvEvaluacionResp o")})
public class CvEvaluacionResp implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator =  "cv_evaluacion_resp_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)    
    @Column(name = "ID")
    private Integer id;
    
    @JoinColumn(name = "cv_evaluacion", referencedColumnName = "ID")
    @ManyToOne
    private CvEvaluacion cvEvaluacion;
    
    @Column(name = "RESPUESTA")
    private boolean respuesta;
    
    @Column(name = "PUNTOS")
    private Double puntos;
    
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
    
    @Size(max = 2048)
    @Column(name = "seccion")
    private String seccion;
    
    @Size(max = 2048)
    @Column(name = "pregunta")
    private String pregunta;
    
    @Column(name = "maximo")
    private Integer maximo;
    
    @Column(name = "PUNTOSTOTAL")
    private Double puntosTotal;

    public CvEvaluacionResp() {
    }

    public CvEvaluacionResp(Integer id) {
	this.id = id;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof CvEvaluacionResp)) {
	    return false;
	}
	CvEvaluacionResp other = (CvEvaluacionResp) object;
	return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

}
