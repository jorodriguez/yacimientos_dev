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
@Table(name = "cv_historial_externo")
@SequenceGenerator(sequenceName = "cv_historial_externo_id_seq", name = "cv_historial_externo_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CvHistorialExterno.findAll", query = "SELECT o FROM CvHistorialExterno o")})
public class CvHistorialExterno implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator =  "cv_historial_externo_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)    
    @Column(name = "ID")
    private Integer id;
    
    @JoinColumn(name = "GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private Gerencia gerencia;
    
    @JoinColumn(name = "gerencia_realiza", referencedColumnName = "ID")
    @ManyToOne
    private Gerencia gerenciaRealiza;
    
    @JoinColumn(name = "Proveedor", referencedColumnName = "ID")
    @ManyToOne
    private Proveedor proveedor;
    
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
    
    @Size(max = 1024)
    @Column(name = "contrato")
    private String contrato;
    
    @Size(max = 512)
    @Column(name = "gerencia_nom")
    private String gerenciaNom;
    
    @Size(max = 512)
    @Column(name = "proveedor_nom")
    private String proveedorNom;
    
    @Size(max = 2048)
    @Column(name = "observaciones")
    private String observaciones;
    
    @Size(max = 512)
    @Column(name = "gerencia_realiza_nombre")
    private String gerenciaRealizaNombre;
    
    @Size(max = 512)
    @Column(name = "correo_proveedor")
    private String correoProveedor;
        
    @Column(name = "calidad_del_servicio")
    private Double calidadDelServicio;
    
    @Column(name = "cumplimiento_de_plazo")
    private Double cumplimientoDePlazo;
    
    @Column(name = "cumplimiento_hse")
    private Double cumplimientoHse;
    
    @Column(name = "servicio_diario")
    private Double servicioDiario;
    
    @Column(name = "cumplimiento_de_cantidad")
    private Double cumplimientoDeCantidad;
    
    @Column(name = "servicio_durante_y_postventa")
    private Double servicioDuranteYPostventa;
    
    @Column(name = "seguridad_y_medio_ambiente")
    private Double seguridadYMedioAmbiente;
    
    @Column(name = "documentacion_final")
    private Double documentacionFinal;
    
    @Column(name = "servicio_durante_la_obra")
    private Double servicioDuranteLaObra;
    
    @Column(name = "puntaje_total")
    private Double puntajeTotal;
    
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;

    public CvHistorialExterno() {
    }

    public CvHistorialExterno(Integer id) {
	this.id = id;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof CvHistorialExterno)) {
	    return false;
	}
	CvHistorialExterno other = (CvHistorialExterno) object;
	return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

}

