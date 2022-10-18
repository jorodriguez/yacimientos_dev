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
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_ASIGNAR_ACCESORIO")
@SequenceGenerator(sequenceName = "sg_asignar_accesorio_id_seq", name = "sg_asignar_accesorio_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgAsignarAccesorio.findAll", query = "SELECT s FROM SgAsignarAccesorio s")})
@Getter
@Setter
public class SgAsignarAccesorio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_asignar_accesorio_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Column(name = "FECHA_OPERACION")
    @Temporal(TemporalType.DATE)
    private Date fechaOperacion;
    @Column(name = "HORA_OPERACION")
    @Temporal(TemporalType.TIME)
    private Date horaOperacion;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    
    @Column(name = "TERMINADA")
    private boolean terminada;
    
    @Column(name = "RECIBIDO")
    private boolean recibido;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @Column(name = "PERTENECE")
    private Integer pertenece;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "SI_CONDICION", referencedColumnName = "ID")
    @ManyToOne
    private SiCondicion siCondicion;
    @JoinColumn(name = "SI_OPERACION", referencedColumnName = "ID")
    @ManyToOne
    private SiOperacion siOperacion;
    @JoinColumn(name = "SG_ACCESORIO", referencedColumnName = "ID")
    @ManyToOne
    private SgAccesorio sgAccesorio;
    @JoinColumn(name = "SI_ADJUNTO", referencedColumnName = "ID")
    @ManyToOne
    private SiAdjunto siAdjunto;

    public SgAsignarAccesorio() {
    }

    public SgAsignarAccesorio(Integer id) {
	this.id = id;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof SgAsignarAccesorio)) {
	    return false;
	}
	SgAsignarAccesorio other = (SgAsignarAccesorio) object;
	return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    
    public String toString() {
	SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
	StringBuilder sb = new StringBuilder();
	sb.append(this.getClass().getName())
		.append("{")
		.append("id=").append(this.id)
		.append(", genero=").append(this.genero != null ? this.genero.getId() : null)
		.append(", siCondicion=").append(this.siCondicion != null ? this.siCondicion.getId() : null)
		.append(", usuario=").append(this.usuario != null ? this.usuario.getId() : null)
		.append(", siOperacion=").append(this.siOperacion != null ? this.siOperacion.getId() : null)
		.append(", siAdjunto=").append(this.siAdjunto != null ? this.siAdjunto.getId() : null)
		.append(", fechaGenero=").append(this.fechaGenero != null ? (sdfFecha.format(this.fechaGenero)) : null)
		.append(", horaGenero=").append(this.horaGenero != null ? (sdfHora.format(this.horaGenero)) : null)
		.append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
		.append(", fechaModifico=").append(this.fechaModifico != null ? (sdfFecha.format(this.fechaModifico)) : null)
		.append(", horaModifico=").append(this.horaModifico != null ? (sdfHora.format(this.horaModifico)) : null)
		.append(", eliminado=").append(this.eliminado)
		.append("}");

	return sb.toString();
    }

    /**
     * @return the pertenece
     */
    public Integer getPertenece() {
	return pertenece;
    }

    /**
     * @param pertenece the pertenece to set
     */
    public void setPertenece(Integer pertenece) {
	this.pertenece = pertenece;
    }

    /**
     * @return the siAdjunto
     */
    public SiAdjunto getSiAdjunto() {
	return siAdjunto;
    }

    /**
     * @param siAdjunto the siAdjunto to set
     */
    public void setSiAdjunto(SiAdjunto siAdjunto) {
	this.siAdjunto = siAdjunto;
    }

    /**
     * @return the terminada
     */
    public boolean isTerminada() {
	return terminada;
    }

    /**
     * @param terminada the terminada to set
     */
    public void setTerminada(boolean terminada) {
	this.terminada = terminada;
    }

    /**
     * @return the siOperacion
     */
    public SiOperacion getSiOperacion() {
	return siOperacion;
    }

    /**
     * @param siOperacion the siOperacion to set
     */
    public void setSiOperacion(SiOperacion siOperacion) {
	this.siOperacion = siOperacion;
    }

}
