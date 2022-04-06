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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sia.modelo.vo.inventarios.TransaccionVO;

/**
 *
 * @author Aplimovil SA de CV
 */
@Entity
@Table(name = "INV_TRANSACCION")
@SequenceGenerator(sequenceName = "inv_transaccion_id_seq", name = "inv_transaccion_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
@ToString
@NamedNativeQueries({
    @NamedNativeQuery(name = "InvTransaccion.buscarPorFolioDeRemisionAplicado", query = "select ID from INV_TRANSACCION WHERE FOLIO_REMISION = ?1 and STATUS = 2 AND ELIMINADO = ?2")
})
public class InvTransaccion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    @GeneratedValue(generator =  "inv_transaccion_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @JoinColumn(name = "ALMACEN", referencedColumnName = "ID")
    @ManyToOne
    private InvAlmacen almacen;

    @Column(name = "TIPO_MOVIMIENTO")
    private Integer tipoMovimiento;

    @JoinColumn(name = "TRASPASO_ALMACEN_DESTINO", referencedColumnName = "ID")
    @ManyToOne
    private InvAlmacen traspasoAlmacenDestino;

    @Column(name = "FECHA")
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Column(name = "NUMERO_ARTICULOS")
    private Integer numeroArticulos;

    @Column(name = "NOTAS")
    private String notas;

    @Column(name = "FOLIO_ORDEN_COMPRA")
    private String folioOrdenCompra;

    @Column(name = "FOLIO_REMISION")
    private String folioRemision;

    @Column(name = "MOTIVO_RECHAZO")
    private String motivoRechazo;

    @Column(name = "STATUS")
    private Integer status;

    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;

    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;

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
    
//    @JoinColumn(name = "SOLICITUD", referencedColumnName = "ID")
//    @ManyToOne
//    private InvSolicitudMaterial solicitud;

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof InvTransaccion)) {
	    return false;
	}
	InvTransaccion other = (InvTransaccion) object;
	return this.id.equals(other.getId());
    }

    public InvTransaccion() {

    }

    public InvTransaccion(Integer id) {
	this.id = id;
    }

    public InvTransaccion(TransaccionVO vo) {
	this.id = vo.getId();
	this.almacen = new InvAlmacen(vo.getAlmacenId());
	this.tipoMovimiento = vo.getTipoMovimiento();
	this.fecha = vo.getFecha();
	this.numeroArticulos = vo.getNumeroArticulos();
	this.notas = vo.getNotas();
	this.folioOrdenCompra = vo.getFolioOrdenCompra();
	this.status = vo.getStatus();
    }
}
