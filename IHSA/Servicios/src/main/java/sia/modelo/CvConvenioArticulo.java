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
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Entity
@Getter
@Setter
@Table(name = "cv_convenio_articulo")
@SequenceGenerator(sequenceName = "cv_convenio_articulo_id_seq", name = "cv_convenio_articulo_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CvConvenioArticulo.findAll", query = "SELECT o FROM CvConvenioArticulo o")})
public class CvConvenioArticulo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "cv_convenio_articulo_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    //
    @JoinColumn(name = "CONVENIO", referencedColumnName = "ID")
    @ManyToOne
    private Convenio convenio;
    @JoinColumn(name = "INV_ARTICULO", referencedColumnName = "ID")
    @ManyToOne
    private InvArticulo invArticulo;
    //
    @Column(name = "CANTIDAD")
    private Double cantidad;
    @Column(name = "precio_unitario")
    private Double precioUnitario;
    @Column(name = "importe")
    private Double importe;
    @Column(name = "item")
    private String item;
    @Column(name = "alcance")
    private String alcance;
    
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
    
    public CvConvenioArticulo() {
    }

    public CvConvenioArticulo(Integer id) {
	this.id = id;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof CvConvenioArticulo)) {
	    return false;
	}
	CvConvenioArticulo other = (CvConvenioArticulo) object;
	return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
}
