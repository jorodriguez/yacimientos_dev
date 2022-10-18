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
@Table(name = "cv_evaluacion_template_det")
@SequenceGenerator(sequenceName = "cv_evaluacion_template_det_id_seq", name = "cv_evaluacion_template_det_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CvEvaluacionTemplateDet.findAll", query = "SELECT o FROM CvEvaluacionTemplateDet o")})
public class CvEvaluacionTemplateDet implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator =  "cv_evaluacion_template_det_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)    
    @Column(name = "ID")
    private Integer id;
    
    @JoinColumn(name = "cv_evaluacion_template", referencedColumnName = "ID")
    @ManyToOne
    private CvEvaluacionTemplate cvEvaluacionTemplate;
    
    @JoinColumn(name = "cv_evaluacion_seccion", referencedColumnName = "ID")
    @ManyToOne
    private CvEvaluacionSeccion cvEvaluacionSeccion;
    
    @Size(max = 2048)
    @Column(name = "PREGUNTA")
    private String pregunta;
        
    @Column(name = "MAXIMO")
    private Integer maximo;
    
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

    public CvEvaluacionTemplateDet() {
    }

    public CvEvaluacionTemplateDet(Integer id) {
	this.id = id;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof CvEvaluacionTemplateDet)) {
	    return false;
	}
	CvEvaluacionTemplateDet other = (CvEvaluacionTemplateDet) object;
	return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

}

