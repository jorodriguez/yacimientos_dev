
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import sia.constantes.TipoRequisicion;

/**
 *
 */
@Entity
@Table(name = "ORDEN")
@SequenceGenerator(sequenceName = "orden_id_seq", name = "orden_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Orden.findAll", query = "SELECT o FROM Orden o")
    ,
    @NamedQuery(name = "Orden.contarPorFolioCompra",
            query = "SELECT COUNT(o) FROM Orden o where o.consecutivo = :folioCompra")
    ,@NamedQuery(name = "Orden.contarPorFolioCompraEnviada",
            query = "SELECT COUNT(o) FROM Orden o where o.eliminado = false and o.url is not null and o.consecutivo = :folioCompra")
    ,
    @NamedQuery(name = "Orden.buscarPorFolioCompra",
            query = "SELECT o FROM Orden o where o.consecutivo = :folioCompra")})
@SqlResultSetMapping(name = "orden_map",
        entities = {
            @EntityResult(entityClass = Orden.class)
        })
@Getter
@Setter
public class Orden implements Serializable {

    @OneToMany(mappedBy = "orden")
    private Collection<NotaOrden> notaOrdenCollection;
    @JoinColumn(name = "OC_SUBCAMPO", referencedColumnName = "ID")
    @ManyToOne
    private OcSubcampo ocSubcampo;
    @OneToMany(mappedBy = "orden")
    private Collection<OcOrdenCoNoticia> ocOrdenCoNoticiaCollection;

    @Column(name = "FECHA")
    @Temporal(TemporalType.DATE)
    private Date fecha;
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
    @OneToMany(mappedBy = "orden")
    private Collection<OrdenSiMovimiento> ordenSiMovimientoCollection;
    @OneToMany(mappedBy = "orden")
    private Collection<SgGastoInsumo> sgGastoInsumoCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "orden_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Integer id;
    @Size(max = 20)
    @Column(name = "CONSECUTIVO")
    private String consecutivo;
    @Size(max = 100)
    @Column(name = "CONTRATO")
    private String contrato;
    @Size(max = 35)
    @Column(name = "REFERENCIA")
    private String referencia;
    @Column(name = "FECHA_ENTREGA")
    @Temporal(TemporalType.DATE)
    private Date fechaEntrega;
    @Size(max = 250)
    @Column(name = "DESTINO")
    private String destino;
    @Column(name = "CENTRO_COSTOS")
    private Integer centroCostos;
    @Column(name = "CUENTA_CONTABLE")
    private Integer cuentaContable;
    @Size(max = 30)
    @Column(name = "CUENTA_CONTABLE_PROYECTO_OT")
    private String cuentaContableProyectoOt;

    @Column(name = "SUPERA_REQUISICION")
    private boolean superaRequisicion;
    @Column(name = "SUBTOTAL")
    private Double subtotal;
    @Column(name = "DESCUENTO")
    private Double descuento;

    @Column(name = "CON_IVA")
    private boolean conIva;
    @Size(max = 15)
    @Column(name = "PORCENTAJE_IVA")
    private String porcentajeIva;
    @Column(name = "IVA")
    private Double iva;
    @Column(name = "TOTAL")
    private Double total;
    @Column(name = "TOTAL_USD")
    private Double totalUsd;
    @Lob
    @Column(name = "NOTA")
    private String nota;
    @Lob
    @Column(name = "OBSERVACIONES")
    private String observaciones;

    @Column(name = "ES_OC")
    private boolean esOc;
    @JoinColumn(name = "CONTACTO_COMPANIA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario contactoCompania;
    @JoinColumn(name = "RESPONSABLE_GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario responsableGerencia;
    @JoinColumn(name = "ANALISTA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario analista;
    @JoinColumn(name = "GERENTE_COMPRAS", referencedColumnName = "ID")
    @ManyToOne
    private Usuario gerenteCompras;
    @JoinColumn(name = "REQUISICION", referencedColumnName = "ID")
    @ManyToOne
    private Requisicion requisicion;
    @JoinColumn(name = "PROYECTO_OT", referencedColumnName = "ID")
    @ManyToOne
    private ProyectoOt proyectoOt;
    @JoinColumn(name = "PROVEEDOR", referencedColumnName = "ID")
    @ManyToOne
    private Proveedor proveedor;
    @JoinColumn(name = "MONEDA", referencedColumnName = "ID")
    @ManyToOne
    private Moneda moneda;
    @JoinColumn(name = "GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private Gerencia gerencia;
    @JoinColumn(name = "CONDICION_PAGO", referencedColumnName = "ID")
    @ManyToOne
    private CondicionPago condicionPago;
    @OneToMany(mappedBy = "orden")
    private Collection<OcOrdenEts> ocOrdenEtsCollection;
    @JoinColumn(name = "COMPANIA", referencedColumnName = "RFC")
    @ManyToOne
    private Compania compania;
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;
    //
    @OneToOne(mappedBy = "orden")
    private AutorizacionesOrden autorizacionesOrden;
    @OneToMany(mappedBy = "orden")
    private Collection<RechazosOrden> rechazosOrdenCollection;
    @OneToMany(mappedBy = "orden")
    private Collection<OrdenDetalle> ordenDetalleCollection;
    @OneToMany(mappedBy = "orden")
    private Collection<ContactosOrden> contactosOrdenCollection;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;

    @Column(name = "ELIMINADO")
    private boolean eliminado;

    @Column(name = "SUPERA_MONTO")
    private boolean superaMonto;
    @Size(max = 64)
    @Column(name = "UUID")
    private String uuid;
    @Size(max = 32)
    @Column(name = "CHECKCODE")
    private String checkcode;
    @Size(max = 256)
    @Column(name = "URL")
    private String url;
    //
    @JoinColumn(name = "OC_UNIDAD_COSTO", referencedColumnName = "ID")
    @ManyToOne
    private OcUnidadCosto ocUnidadCosto;

    @Size(max = 20)
    @Column(name = "NAVCODE")
    private String navCode;

    @Column(name = "TIPO")
    @Enumerated(EnumType.STRING)
    private TipoRequisicion tipo;
    //
    @JoinColumn(name = "OC_TERMINO_PAGO", referencedColumnName = "ID")
    @ManyToOne
    private OcTerminoPago ocTerminoPago;
    //

    @Column(name = "LEIDA")
    private boolean leida;

    @Column(name = "COMPLETA")
    private boolean completa;

    @JoinColumn(name = "IMPUESTO", referencedColumnName = "ID")
    @ManyToOne
    private Impuesto impuesto;
    //
    @JoinColumn(name = "PARIDAD_VALOR", referencedColumnName = "ID")
    @ManyToOne
    private ParidadValor paridadValor;
    //
    @JoinColumn(name = "OC_FORMA_PAGO", referencedColumnName = "ID")
    @ManyToOne
    private OcFormaPago ocFormaPago;
    //
    @JoinColumn(name = "OC_TIPO_COMPRA", referencedColumnName = "ID")
    @ManyToOne
    private OcTipoCompra ocTipoCompra;
    //

    @Column(name = "CON_CONVENIO")
    private boolean conConvenio;

    @Column(name = "MULTIPROYECTO")
    private boolean multiproyecto;

    @Column(name = "DETALLE_PROCESADO")
    private boolean detalleProcesado;

    @Column(name = "INICIO_EJECUCION")
    @Temporal(TemporalType.DATE)
    private Date inicioEjecucion;
    
    @Column(name = "FIN_EJECUCION")
    @Temporal(TemporalType.DATE)
    private Date finEjecucion;
    
    //
    @JoinColumn(name = "OC_USO_CFDI", referencedColumnName = "ID")
    @ManyToOne
    private OcUsoCFDI ocUsoCFDI;
    //
    
    @Column(name = "SUBTOTAL_USD")
    private Double subtotalUsd;
    
    @Column(name = "REPSE")
    private boolean repse;
    
    @JoinColumn(name = "OC_METODO_PAGO", referencedColumnName = "ID")
    @ManyToOne
    private OcMetodoPago ocMetodoPago;
    
    public Orden() {
    }

    public Orden(int idOrden) {
        this.id = idOrden;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Orden)) {
            return false;
        }
        Orden other = (Orden) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    
    public String toString() {
        return "sia.modelo.Orden[ id=" + id + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public Collection<NotaOrden> getNotaOrdenCollection() {
        return notaOrdenCollection;
    }

    public void setNotaOrdenCollection(Collection<NotaOrden> notaOrdenCollection) {
        this.notaOrdenCollection = notaOrdenCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OcOrdenCoNoticia> getOcOrdenCoNoticiaCollection() {
        return ocOrdenCoNoticiaCollection;
    }

    public void setOcOrdenCoNoticiaCollection(Collection<OcOrdenCoNoticia> ocOrdenCoNoticiaCollection) {
        this.ocOrdenCoNoticiaCollection = ocOrdenCoNoticiaCollection;
    }

}
