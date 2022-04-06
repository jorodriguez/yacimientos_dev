/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.TipoRequisicion;

/**
 *
 * @author hacosta Agregado el Generador
 */
@Entity
@Table(name = "REQUISICION")
@SequenceGenerator(sequenceName = "requisicion_id_seq", name = "requisicion_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Requisicion.findAll", query = "SELECT r FROM Requisicion r")})
@Getter
@Setter
public class Requisicion implements Serializable {

    @Column(name = "FECHA_ELABORACION")
    @Temporal(TemporalType.DATE)
    private Date fechaElaboracion;
    @Column(name = "FECHA_REQUERIDA")
    @Temporal(TemporalType.DATE)
    private Date fechaRequerida;
    @Column(name = "FECHA_SOLICITO")
    @Temporal(TemporalType.DATE)
    private Date fechaSolicito;
    @Column(name = "FECHA_REVISO")
    @Temporal(TemporalType.DATE)
    private Date fechaReviso;
    @Column(name = "FECHA_APROBO")
    @Temporal(TemporalType.DATE)
    private Date fechaAprobo;
    @Column(name = "FECHA_AUTORIZO")
    @Temporal(TemporalType.DATE)
    private Date fechaAutorizo;
    @Column(name = "FECHA_VISTO_BUENO")
    @Temporal(TemporalType.DATE)
    private Date fechaVistoBueno;
    @Column(name = "FECHA_ASIGNO")
    @Temporal(TemporalType.DATE)
    private Date fechaAsigno;
    @Column(name = "FECHA_CANCELO")
    @Temporal(TemporalType.DATE)
    private Date fechaCancelo;
    @Column(name = "FECHA_SOLICITO_OFERTA")
    @Temporal(TemporalType.DATE)
    private Date fechaSolicitoOferta;
    @Column(name = "FECHA_FINALIZO")
    @Temporal(TemporalType.DATE)
    private Date fechaFinalizo;
    @Column(name = "HORA_SOLICITO")
    @Temporal(TemporalType.TIME)
    private Date horaSolicito;
    @Column(name = "HORA_REVISO")
    @Temporal(TemporalType.TIME)
    private Date horaReviso;
    @Column(name = "HORA_APROBO")
    @Temporal(TemporalType.TIME)
    private Date horaAprobo;
    @Column(name = "HORA_AUTORIZO")
    @Temporal(TemporalType.TIME)
    private Date horaAutorizo;
    @Column(name = "HORA_VISTO_BUENO")
    @Temporal(TemporalType.TIME)
    private Date horaVistoBueno;
    @Column(name = "HORA_ASIGNO")
    @Temporal(TemporalType.TIME)
    private Date horaAsigno;
    @Column(name = "HORA_CANCELO")
    @Temporal(TemporalType.TIME)
    private Date horaCancelo;
    @Column(name = "HORA_SOLICITO_OFERTA")
    @Temporal(TemporalType.TIME)
    private Date horaSolicitoOferta;
    @Column(name = "HORA_FINALIZO")
    @Temporal(TemporalType.TIME)
    private Date horaFinalizo;
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
    @OneToMany(mappedBy = "requisicion")
    private Collection<RequisicionSiMovimiento> requisicionSiMovimientoCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "requisicion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Size(max = 20)
    @Column(name = "CONSECUTIVO")
    private String consecutivo;
    @Size(max = 75)
    @Column(name = "PROVEEDOR")
    private String proveedor;
    @Size(max = 35)
    @Column(name = "REFERENCIA")
    private String referencia;
    @Column(name = "CONTRATO")
    private boolean contrato;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "MONTO_MN")
    private Double montoMn;
    @Column(name = "MONTO_USD")
    private Double montoUsd;
    @Column(name = "MONTOTOTAL_USD")
    private Double montototalUsd;
    @Column(name = "REVISION")
    private Integer revision;
    @Column(name = "PARIDAD_DOLAR")
    private Double paridadDolar;
    @Column(name = "PARIDAD_EURO")
    private Double paridadEuro;

    @Column(name = "RECHAZADA")
    private boolean rechazada;
    @Lob
    @Column(name = "LUGAR_ENTREGA")
    private String lugarEntrega;
    @Lob
    @Column(name = "MOTIVO_CANCELO")
    private String motivoCancelo;
    @Lob
    @Column(name = "MOTIVO_FINALIZO")
    private String motivoFinalizo;
    @Lob
    @Column(name = "OBSERVACIONES")
    private String observaciones;
    @OneToMany(mappedBy = "requisicion")
    private Collection<Orden> ordenCollection;
    @OneToMany(mappedBy = "requisicion")
    private Collection<Rechazo> rechazoCollection;
    @OneToMany(mappedBy = "requisicion")
    private Collection<NotaRequisicion> notaRequisicionCollection;
    @OneToMany(mappedBy = "requisicion")
    private Collection<RequisicionDetalle> requisicionDetalleCollection;
    @JoinColumn(name = "APRUEBA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario aprueba;
    @JoinColumn(name = "VISTO_BUENO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario vistoBueno;
    @JoinColumn(name = "COMPANIA", referencedColumnName = "RFC")
    @ManyToOne
    private Compania compania;
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatus;
    @JoinColumn(name = "GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private Gerencia gerencia;
    @JoinColumn(name = "PRIORIDAD", referencedColumnName = "ID")
    @ManyToOne
    private Prioridad prioridad;
    @JoinColumn(name = "PROYECTO_OT", referencedColumnName = "ID")
    @ManyToOne
    private ProyectoOt proyectoOt;
    @JoinColumn(name = "TIPO_OBRA", referencedColumnName = "ID")
    @ManyToOne
    private TipoObra tipoObra;
    @JoinColumn(name = "FINALIZO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario finalizo;
    @JoinColumn(name = "CANCELO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario cancelo;
    @JoinColumn(name = "ASIGNA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario asigna;
    @JoinColumn(name = "SOLICITA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario solicita;
    @JoinColumn(name = "REVISA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario revisa;
    @JoinColumn(name = "AUTORIZA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario autoriza;
    @JoinColumn(name = "COMPRA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario compra;
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;

    @Column(name = "ELIMINADO")
    private boolean eliminado;
//    @Size(max = 32)
//    @Column(name = "CHECKCODE")
//    private String checkcode;
    @Size(max = 256)
    @Column(name = "URL")
    private String url;
    //
    @JoinColumn(name = "OC_UNIDAD_COSTO", referencedColumnName = "ID")
    @ManyToOne
    private OcUnidadCosto ocUnidadCosto;
    //
    @Column(name = "TIPO")
    @Enumerated(EnumType.STRING)
    private TipoRequisicion tipo;
//

    @Column(name = "NUEVA")
    private boolean nueva;

    @Column(name = "MULTIPROYECTO")
    private boolean multiproyecto;
    //
    @JoinColumn(name = "OC_USO_CFDI", referencedColumnName = "ID")
    @ManyToOne
    private OcUsoCFDI ocUsoCFDI;
    
    @JoinColumn(name = "OC_METODO_PAGO", referencedColumnName = "ID")
    @ManyToOne
    private OcMetodoPago ocMetodoPago;
    
    //
    public Requisicion() {
    }

    public Requisicion(int id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Requisicion)) {
            return false;
        }
        Requisicion other = (Requisicion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.Requisicion[ id=" + id + " ]";
    }
}
