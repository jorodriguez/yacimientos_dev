/*
 * To change this template, choose Tools | Templates
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
import javax.persistence.Lob;
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
 * @author hacosta
 */
@Entity
@Table(name = "ORDEN_DETALLE")
@SequenceGenerator(sequenceName = "orden_detalle_id_seq", name = "orden_detalle_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OrdenDetalle.findAll", query = "SELECT o FROM OrdenDetalle o")
    ,
    @NamedQuery(name = "OrdenDetalle.buscarPorFolioCompra",
            query = "SELECT o FROM OrdenDetalle o where o.orden.consecutivo = :folioCompra")
    ,
    @NamedQuery(name = "OrdenDetalle.buscarPorOrdenIdYArticuloId",
            query = "SELECT o FROM OrdenDetalle o where o.orden.id = ?1 and o.invArticulo.id = ?2 and o.eliminado = ?3")
    
})
@Setter
@Getter
public class OrdenDetalle implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "orden_detalle_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)

    @Column(name = "ID")
    private Integer id;
    @Size(max = 50)
    @Column(name = "CODIGO")
    private String codigo;
    @Size(max = 5000)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "CANTIDAD")
    private Double cantidad;
    @Size(max = 35)
    @Column(name = "UNIDAD")
    private String unidad;
    @Column(name = "PRECIO_UNITARIO")
    private Double precioUnitario;
    @Column(name = "IMPORTE")
    private Double importe;
    @Lob
    @Column(name = "OBSERVACIONES")
    private String observaciones;

    @Column(name = "EN_CATALOGO")
    private boolean enCatalogo;
    @JoinColumn(name = "REQUISICION_DETALLE", referencedColumnName = "ID")
    @ManyToOne
    private RequisicionDetalle requisicionDetalle;
    @JoinColumn(name = "ORDEN", referencedColumnName = "ID")
    @ManyToOne
    private Orden orden;
//    @JoinColumn(name = "MONEDA", referencedColumnName = "ID")
//    @ManyToOne
//    private Moneda moneda;
    //
    @JoinColumn(name = "SI_UNIDAD", referencedColumnName = "ID")
    @ManyToOne
    private SiUnidad siUnidad;
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

    @Column(name = "RECIBIDO")
    private boolean recibido;
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_RECEPCION")
    private Date fechaRecepcion;
    //
    @JoinColumn(name = "OC_TAREA", referencedColumnName = "ID")
    @ManyToOne
    private OcTarea ocTarea;

    @JoinColumn(name = "OC_PRODUCTO_COMPANIA", referencedColumnName = "ID")
    @ManyToOne
    private OcProductoCompania ocProductoCompania;

    @JoinColumn(name = "INV_ARTICULO", referencedColumnName = "ID")
    @ManyToOne
    private InvArticulo invArticulo;

    @Column(name = "CANTIDAD_RECIBIDA")
    private Double unidadesRecibidas;

    @Size(max = 512)
    @Column(name = "TEXTNAV")
    private String textNav;
    //
    @JoinColumn(name = "PROYECTO_OT", referencedColumnName = "ID")
    @ManyToOne
    private ProyectoOt proyectoOt;
    @JoinColumn(name = "OC_SUBTAREA", referencedColumnName = "ID")
    @ManyToOne
    private OcSubTarea ocSubTarea;
    @JoinColumn(name = "OC_UNIDAD_COSTO", referencedColumnName = "ID")
    @ManyToOne
    private OcUnidadCosto ocUnidadCosto;
    @JoinColumn(name = "OC_ACTIVIDADPETROLERA", referencedColumnName = "ID")
    @ManyToOne
    private OcActividadPetrolera ocActividadPetrolera;

    @Column(name = "MULTIPROYECTO_ID")
    private Integer multiproyectoId;

    @Column(name = "DESCUENTO")
    private Double descuento;
    
    @JoinColumn(name = "oc_codigo_tarea", referencedColumnName = "ID")
    @ManyToOne
    private OcCodigoTarea ocCodigoTarea;
    
    @JoinColumn(name = "oc_codigo_subtarea", referencedColumnName = "ID")
    @ManyToOne
    private OcCodigoSubtarea ocCodigoSubtarea;
    
    @JoinColumn(name = "oc_presupuesto", referencedColumnName = "ID")
    @ManyToOne
    private OcPresupuesto ocPresupuesto;
    
    @Column(name = "mes_presupuesto")
    private Integer mesPresupuesto;
    
    @Column(name = "anio_presupuesto")
    private Integer anioPresupuesto;
    
    @Column(name = "convenio")
    private Integer convenio;
    
    @Size(max = 100)
    @Column(name = "convenio_codigo")
    private String convenioCodigo;
    
    @JoinColumn(name = "CANCELO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario cancelo;
    @Size(max = 2048)
    @Column(name = "MOTIVO_CANCELAR")
    private String motivoCancelar;
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_CANCELO")
    private Date fechaCancelo;
    
    @Column(name = "USUARIO_BENEFICIADO")
    private String usuarioBeneficiado; 
    
    public OrdenDetalle() {
    }

    public OrdenDetalle(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrdenDetalle)) {
            return false;
        }
        OrdenDetalle other = (OrdenDetalle) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    
    public String toString() {
        return "sia.modelo.OrdenDetalle[ id=" + id + " ]";
    }

}
