/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "REQUISICION_DETALLE")
@SequenceGenerator(sequenceName = "requisicion_detalle_id_seq", name = "requisicion_detalle_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RequisicionDetalle.findAll", query = "SELECT r FROM RequisicionDetalle r")})
@Setter
@Getter
@ToString
public class RequisicionDetalle implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "requisicion_detalle_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 30)
    @Column(name = "BIEN")
    private String bien;
    @Size(max = 50)
    @Column(name = "NUMERO_PARTE")
    private String numeroParte;
    @Size(max = 35)
    @Column(name = "UNIDAD")
    private String unidad;
    @Column(name = "CANTIDAD_SOLICITADA")
    private Double cantidadSolicitada;
    @Column(name = "CANTIDAD_AUTORIZADA")
    private Double cantidadAutorizada;
    @Lob
    @Column(name = "DESCRIPCION_SOLICITANTE")
    private String descripcionSolicitante;
    
    @Column(name = "AUTORIZADO")
    private boolean autorizado;
    @Lob
    @Column(name = "OBSERVACIONES")
    private String observaciones;
    @Column(name = "PRECIO_UNITARIO")
    private Double precioUnitario;
    @Column(name = "IMPORTE")
    private Double importe;
    
    @Column(name = "DISGREGADO")
    private boolean disgregado;
    @JoinColumn(name = "REQUISICION", referencedColumnName = "ID")
    @ManyToOne
    private Requisicion requisicion;
//    @JoinColumn(name = "MONEDA", referencedColumnName = "ID")
//    @ManyToOne
//    private Moneda moneda;
    @JoinColumn(name = "CARACTERISTICA_SERVICIO", referencedColumnName = "ID")
    @ManyToOne
    private CaracteristicasServicio caracteristicaServicio;
    @OneToMany(mappedBy = "requisicionDetalle")
    private Collection<OrdenDetalle> ordenDetalleCollection;
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
    //
    @JoinColumn(name = "SI_UNIDAD", referencedColumnName = "ID")
    @ManyToOne
    private SiUnidad siUnidad;
    //
    
    @JoinColumn(name = "OC_TAREA", referencedColumnName = "ID")
    @ManyToOne
    private OcTarea ocTarea;
    
    @JoinColumn(name = "INV_ARTICULO", referencedColumnName = "ID")
    @ManyToOne
    private InvArticulo invArticulo;
    
    @Size(max = 512)
    @Column(name = "TEXTNAV")
    private String textNav;
    
    @JoinColumn(name = "PROYECTO_OT", referencedColumnName = "ID")
    @ManyToOne
    private ProyectoOt proyectoOt;
    
    @JoinColumn(name = "OC_UNIDAD_COSTO", referencedColumnName = "ID")
    @ManyToOne
    private OcUnidadCosto ocUnidadCosto;
    
    @JoinColumn(name = "OC_SUBTAREA", referencedColumnName = "ID")
    @ManyToOne
    private OcSubTarea ocSubTarea;

    @Column(name = "MULTIPROYECTO_ID")
    private Integer multiproyectoId;
    
    @JoinColumn(name = "oc_codigo_tarea", referencedColumnName = "ID")
    @ManyToOne
    private OcCodigoTarea ocCodigoTarea;
    
    @JoinColumn(name = "oc_codigo_subtarea", referencedColumnName = "ID")
    @ManyToOne
    private OcCodigoSubtarea ocCodigoSubtarea;
    
    @JoinColumn(name = "oc_actividadpetrolera", referencedColumnName = "ID")
    @ManyToOne
    private OcActividadPetrolera ocActividadpetrolera;
    
    @JoinColumn(name = "oc_presupuesto", referencedColumnName = "ID")
    @ManyToOne
    private OcPresupuesto ocPresupuesto;
    
    @Column(name = "mes_presupuesto")
    private Integer mesPresupuesto;
    
    @Column(name = "anio_presupuesto")
    private Integer anioPresupuesto;
    
    public RequisicionDetalle() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {        
        if (!(object instanceof RequisicionDetalle)) {
            return false;
        }
        RequisicionDetalle other = (RequisicionDetalle) object;
        return this.id != null && other.id != null && this.id.equals(other.id);
    }
}
