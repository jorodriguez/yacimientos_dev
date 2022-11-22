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
@Table(name = "SG_VEHICULO")
@SequenceGenerator(sequenceName = "sg_vehiculo_id_seq", name = "sg_vehiculo_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgVehiculo.findAll", query = "SELECT s FROM SgVehiculo s")})
@Setter
@Getter
public class SgVehiculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_vehiculo_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 100)
    @Column(name = "OBSERVACION")
    private String observacion;
    @Size(max = 20)
    @Column(name = "NUMERO_PLACA")
    private String numeroPlaca;
    @Size(max = 50)
    @Column(name = "SERIE")
    private String serie;
    @Column(name = "CAJON_ESTACIONAMIENTO")
    private String cajonEstacionamiento;
    @Column(name = "CAPACIDAD_PASAJEROS")
    private Integer capacidadPasajeros;
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
    
    @Column(name = "BAJA")
    private boolean baja;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @Column(name = "PERIODO_KM_MANTENIMIENTO")
    private Integer periodoKmMantenimiento;
    @Column(name = "PARTIDA_PERIODO_KM")
    private Integer partidaPeriodoKm;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "SG_OFICINA", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina sgOficina;
    @JoinColumn(name = "SG_MODELO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgModelo sgModelo;
    @JoinColumn(name = "SG_MARCA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgMarca sgMarca;
    @JoinColumn(name = "SG_COLOR", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgColor sgColor;
    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgTipoEspecifico sgTipoEspecifico;
    @JoinColumn(name = "SG_TIPO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgTipo sgTipo;
    @JoinColumn(name = "PROVEEDOR", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Proveedor proveedor;
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Estatus estatus;
    
    @Column(name = "CAJA_HERRAMIENTA")
    private boolean cajaHerramienta;
    
    @Column(name = "GPS")
    private boolean gps;
    @Size(max = 64)
    @Column(name = "NUMERO_ACTIVO")
    private String numeroActivo;
    @Size(max = 100)
    @Column(name = "NUMERO_ECONOMICO")
    private String numeroEconomico;
    @Size(max = 100)
    @Column(name = "SEGURO")
    private String seguro;

    public SgVehiculo() {
    }

    public SgVehiculo(int id) {
	this.id = id;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof SgVehiculo)) {
	    return false;
	}
	SgVehiculo other = (SgVehiculo) object;
	if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
	    return false;
	}
	return true;
    }

    
    public String toString() {
	SimpleDateFormat sdfd = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdft = new SimpleDateFormat("HH:mm:ss");
	StringBuilder sb = new StringBuilder();
	sb.append(this.getClass().getName())
		.append("{")
		.append("id=").append(this.id)
		.append(", numeroPlaca=").append(this.numeroPlaca)
		.append(", serie=").append(this.serie)
		.append(", cajonEstacionamiento=").append(this.cajonEstacionamiento)
		.append(", capacidadPasajeros=").append(this.capacidadPasajeros)
		.append(", observacion=").append(this.observacion)
		.append(", numero_economico=").append(this.numeroEconomico)
		.append(", periodoKmMantenimiento=").append(this.periodoKmMantenimiento)
		.append(", partidaPeriodoKm=").append(this.partidaPeriodoKm)
		.append(", sgOficina=").append(this.sgOficina != null ? this.sgOficina.getId() : null)
		.append(", sgMarca=").append(this.sgMarca != null ? this.sgMarca.getId() : null)
		.append(", sgModelo=").append(this.sgModelo != null ? this.sgModelo.getId() : null)
		.append(", sgColor=").append(this.sgColor != null ? this.sgColor.getId() : null)
		.append(", sgTipo=").append(this.sgTipo != null ? this.sgTipo.getId() : null)
		.append(", sgTipoEspecifico=").append(this.sgTipoEspecifico != null ? this.sgTipoEspecifico.getId() : null)
		.append(", genero=").append(this.genero != null ? this.genero.getId() : null)
		.append(", fechaGenero=").append(this.fechaGenero != null ? (sdfd.format(this.fechaGenero)) : null)
		.append(", horaGenero=").append(this.horaGenero != null ? (sdft.format(this.horaGenero)) : null)
		.append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
		.append(", fechaModifico=").append(this.fechaModifico != null ? (sdfd.format(this.fechaModifico)) : null)
		.append(", horaModifico=").append(this.horaModifico != null ? (sdft.format(this.horaModifico)) : null)
		.append(", eliminado=").append(this.eliminado)
		.append(", baja=").append(this.baja)
		.append("}");

	return sb.toString();
    }

}
