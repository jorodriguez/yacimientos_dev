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
@Table(name = "SG_TARJETA_OPERACION")
@SequenceGenerator(sequenceName = "sg_tarjeta_operacion_id_seq", name = "sg_tarjeta_operacion_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
@NamedQueries({
    @NamedQuery(name = "SgTarjetaOperacion.traerRegistrosEntreFechas",
	    query = "SELECT t FROM SgTarjetaOperacion t where t.fechaGenero between ?1  and ?2 and t.eliminado = 'False' "),
    @NamedQuery(name = "SgTarjetaOperacion.traerFechaMinimaCargaArchivo",
	    query = "select min(t.fechaOperacion)  from SgTarjetaOperacion t where t.fechaGenero = ?1 and t.eliminado = 'False' "),
    @NamedQuery(name = "SgTarjetaOperacion.traerFechaMaximaCargaArchivo",
	    query = "select max(t.fechaOperacion)  from SgTarjetaOperacion t where t.fechaGenero = ?1 and t.eliminado = 'False' "),
    @NamedQuery(name = "SgTarjetaOperacion.traerFechaCargaArchivo",
	    query = "select t.fechaGenero from SgTarjetaOperacion t where t.eliminado = 'False' GROUP BY t.fechaGenero ORDER BY t.fechaGenero desc"),
    @NamedQuery(name = "SgTarjetaOperacion.traerPorFecha",
	    query = "SELECT t FROM SgTarjetaOperacion t where t.fechaGenero = ?1  and t.eliminado = 'False' ORDER BY t.fechaOperacion desc")
})
public class SgTarjetaOperacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_tarjeta_operacion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    //
    @Column(name = "ID_OPERACION")
    private String operacion;

    @JoinColumn(name = "SG_TARJETA_BANCARIA", referencedColumnName = "ID")
    @ManyToOne
    private SgTarjetaBancaria sgTarjeta;

    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuario;

    @JoinColumn(name = "SG_ESTACION", referencedColumnName = "ID")
    @ManyToOne
    private SgEstacion sgEstacion;

    @JoinColumn(name = "SG_VEHICULO", referencedColumnName = "ID")
    @ManyToOne
    private SgVehiculo sgVehiculo;

    @Size(max = 512)
    @Column(name = "US_SIN_REGISTRO")
    private String UsSinRegistro;

    @Size(max = 64)
    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Size(max = 8)
    @Column(name = "PLACA")
    private String placa;

    @Column(name = "CARGO")
    private double cargo;

    @Size(max = 1024)
    @Column(name = "CONCEPTO")
    private String concepto;

    @Column(name = "FECHA_OPERACION")
    @Temporal(TemporalType.DATE)
    private Date fechaOperacion;

    @Column(name = "HORA_OPERACION")
    @Temporal(TemporalType.TIME)
    private Date horaOperacion;

    @Size(max = 12)
    @Column(name = "TIPO")
    private String tipo;

    @Size(max = 32)
    @Column(name = "TIPO_COMBUSTIBLE")
    private String tipoCombustible;

    @Column(name = "PRECIO_UNITARIO")
    private double precioUnitario;

    @Column(name = "KILOMETRO_INICIAL")
    private int kilometroInicial;

    @Column(name = "KILOMETRO_FINAL")
    private int kilometroFinal;

    @Column(name = "CANTIDAD")
    private double cantidad;

    @Column(name = "RENDIMIENTO")
    private double rendimiento;

    @Column(name = "IVA")
    private double iva;

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
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "SG_OFICINA", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina sgOficina;

    public SgTarjetaOperacion() {
    }

    public SgTarjetaOperacion(Integer id) {
	this.id = id;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (getId() != null ? getId().hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	if (!(object instanceof SgTarjetaOperacion)) {
	    return false;
	}
	SgTarjetaOperacion other = (SgTarjetaOperacion) object;
	return !((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id)));
    }

    
    public String toString() {
	return "sia.modelo.SgTarjetaOperacion[ id=" + id + " ]";
    }
}
