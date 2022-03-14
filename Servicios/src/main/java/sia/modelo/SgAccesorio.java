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
@Table(name = "SG_ACCESORIO")
@SequenceGenerator(sequenceName = "sg_accesorio_id_seq", name = "sg_accesorio_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgAccesorio.findAll", query = "SELECT s FROM SgAccesorio s")})
@Setter
@Getter
public class SgAccesorio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_accesorio_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 75)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @Size(max = 30)
    @Column(name = "SERIE")
    private String serie;
    
    @Column(name = "GARANTIA")
    private boolean garantia;
    
    @Column(name = "DISPONIBLE")
    private boolean disponible;
    @Column(name = "FECHA_ADQUISICION")
    @Temporal(TemporalType.DATE)
    private Date fechaAdquisicion;
    @Size(max = 256)
    @Column(name = "OBSERVACION")
    private String observacion;
    @JoinColumn(name = "SI_CONDICION", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SiCondicion siCondicion;
    @JoinColumn(name = "TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;
    @JoinColumn(name = "SG_TIPO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipo sgTipo;
    @Column(name = "FECHA_VENCIMIENTO")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;
    @JoinColumn(name = "SG_OFICINA", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina sgOficina;
    @JoinColumn(name = "SG_MODELO", referencedColumnName = "ID")
    @ManyToOne
    private SgModelo sgModelo;
    @JoinColumn(name = "SG_MARCA", referencedColumnName = "ID")
    @ManyToOne
    private SgMarca sgMarca;
    @JoinColumn(name = "PROVEEDOR", referencedColumnName = "ID")
    @ManyToOne
    private Proveedor proveedor;

    @JoinColumn(name = "SG_LINEA", referencedColumnName = "ID")
    @ManyToOne
    private SgLinea sgLinea;

    @Size(max = 256)
    @Column(name = "SISTEMA_OPERATIVO")
    private String sistemaOperativo;
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

    public SgAccesorio() {
    }

    public SgAccesorio(int idAccesorio) {
	this.id = idAccesorio;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (getId() != null ? getId().hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	if (!(object instanceof SgAccesorio)) {
	    return false;
	}
	SgAccesorio other = (SgAccesorio) object;
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
		.append(", descripcion=").append(this.descripcion)
		.append(", observacion=").append(this.observacion)
		.append(", serie=").append(this.serie)
		.append(", garantia=").append(this.garantia)
		.append(", disponible=").append(this.disponible)
		.append(", garantia=").append(this.garantia)
		.append(", fechaAdquisicion=").append(this.fechaAdquisicion != null ? (sdfd.format(this.fechaAdquisicion)) : null)
		.append(", fechaVencimiento=").append(this.fechaVencimiento != null ? (sdfd.format(this.fechaVencimiento)) : null)
		.append(", sgOficina=").append(this.sgOficina != null ? this.sgOficina.getId() : null)
		.append(", sgModelo=").append(this.sgModelo != null ? this.sgModelo.getId() : null)
		.append(", sgMarca=").append(this.sgMarca != null ? this.sgMarca.getId() : null)
		.append(", sgTipo=").append(this.sgTipo != null ? this.sgTipo.getId() : null)
		.append(", sgTipoEspecifico=").append(this.sgTipoEspecifico != null ? this.sgTipoEspecifico.getId() : null)
		.append(", proveedor").append(this.proveedor != null ? this.proveedor.getId() : null)
		.append(", siCondicion=").append(this.siCondicion != null ? this.siCondicion.getId() : null)
		.append(", genero=").append(this.getGenero() != null ? this.getGenero().getId() : null)
		.append(", fechaGenero=").append(this.getFechaGenero() != null ? (sdfd.format(this.getFechaGenero())) : null)
		.append(", horaGenero=").append(this.getHoraGenero() != null ? (sdft.format(this.getHoraGenero())) : null)
		.append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
		.append(", fechaModifico=").append(this.fechaModifico != null ? (sdfd.format(this.fechaModifico)) : null)
		.append(", horaModifico=").append(this.horaModifico != null ? (sdft.format(this.horaModifico)) : null)
		.append(", eliminado=").append(this.eliminado)
		.append("}");

	return sb.toString();
    }
}
