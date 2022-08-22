/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import javax.persistence.SequenceGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author hacosta
 */
@Getter
@Setter
@Entity
@Table(name = "PROVEEDOR")
@SequenceGenerator(sequenceName = "proveedor_id_seq", name = "proveedor_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Proveedor.findAll", query = "SELECT p FROM Proveedor p")})
public class Proveedor implements Serializable {

    @Column(name = "FECHA_CREACION")
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;
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
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @OneToMany(mappedBy = "proveedor")
    private Collection<SgVehiculoMantenimiento> sgVehiculoMantenimientoCollection;
    @OneToMany(mappedBy = "proveedor")
    private Collection<SgAseguradora> sgAseguradoraCollection;
    @OneToMany(mappedBy = "proveedor")
    private Collection<SgTallerMantenimiento> sgTallerMantenimientoCollection;
    @OneToMany(mappedBy = "proveedor")
    private Collection<SgPagoServicio> sgPagoServicioCollection;
    @OneToMany(mappedBy = "proveedor")
    private Collection<SgAccesorio> sgAccesorioCollection;
    @OneToMany(mappedBy = "proveedor")
    private Collection<SgHotel> sgHotelCollection;
    @OneToMany(mappedBy = "proveedor")
    private Collection<PvCalificacion> pvCalificacionCollection;
    @OneToMany(mappedBy = "proveedor")
    private Collection<PvRelCondicionPago> pvRelCondicionPagoCollection;

    @JoinColumn(name = "PV_TIPO_PERSONA", referencedColumnName = "ID")
    @ManyToOne
    private PvTipoPersona pvTipoPersona;
    @OneToMany(mappedBy = "proveedor")
    private Collection<PvClasificacionArchivo> pvClasificacionArchivoCollection;

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "proveedor_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "RFC")
    private String rfc;
    @Size(max = 150)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 150)
    @Column(name = "NOMBRE_CORTO")
    private String nombreCorto;
    @Size(max = 70)
    @Column(name = "CALLE")
    private String calle;
    @Size(max = 64)
    @Column(name = "NUMERO")
    private String numero;
    @Size(max = 50)
    @Column(name = "COLONIA")
    private String colonia;
    @Size(max = 10)
    @Column(name = "PISO")
    private String piso;
    @Size(max = 10)
    @Column(name = "CODIGO_POSTAL")
    private String codigoPostal;
    @Size(max = 30)
    @Column(name = "CIUDAD")
    private String ciudad;
    @Size(max = 30)
    @Column(name = "ESTADO")
    private String estado;
    @Size(max = 30)
    @Column(name = "PAIS")
    private String pais;
    @Size(max = 55)
    @Column(name = "TELEFONO")
    private String telefono;
    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//if the field contains phone or fax number consider using this annotation to enforce field validation

    @Size(max = 35)
    @Column(name = "FAX")
    private String fax;
    @Size(max = 70)
    @Column(name = "CORREO")
    private String correo;
    @Size(max = 70)
    @Column(name = "PAGINA_WEB")
    private String paginaWeb;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "CALIFICACION")
    private Double calificacion;
    
    @Column(name = "VISIBLE")
    private boolean visible;
    @Size(max = 50)
    @Column(name = "CLAVE")
    private String clave;
    @Size(max = 60)
    @Column(name = "REPRESENTANTE_LEGAL")
    private String representanteLegal;
    @Size(max = 20)
    @Column(name = "NUMERO_INTERIOR")
    private String numeroInterior;
    
    @Column(name = "SESION")
    private boolean sesion;
    @OneToMany(mappedBy = "proveedor")
    private Collection<Convenio> convenioCollection;
    @OneToMany(mappedBy = "proveedor")
    private Collection<Orden> ordenCollection;
    @OneToMany(mappedBy = "proveedor")
    private Collection<ProveedorActividad> proveedorActividadCollection;
    @OneToMany(mappedBy = "proveedor")
    private Collection<CuentaBancoProveedor> cuentaBancoProveedorCollection;
    @OneToMany(mappedBy = "proveedor")
    private Collection<SucursalProveedor> sucursalProveedorCollection;
    @OneToMany(mappedBy = "proveedor")
    private Collection<ContactoProveedor> contactoProveedorCollection;
    @OneToMany(mappedBy = "proveedor")
    private Collection<ServiciosAdquiridos> serviciosAdquiridosCollection;
    @Size(max = 256)
    @Column(name = "GIRO")
    private String giro;
    @Size(max = 18)
    @Column(name = "CURP")
    private String curp;
    @Size(max = 100)
    @Column(name = "IMSSPATRONAL")
    private String imsspatronal;
    @Size(max = 100)
    @Column(name = "IDCIF")
    private String idCif;
    
    @Column(name = "NACIONAL")
    private boolean nacional;
    @JoinColumn(name = "STATUS", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatus;
    @JoinColumn(name = "OC_TERMINO_PAGO", referencedColumnName = "ID")
    @ManyToOne
    private OcTerminoPago ocTerminoPago;    
    
    @Column(name = "CARTA_CONTENIDO_NACIONAL")
    private boolean cartaContenidoNacional;
    
    @Column(name = "REPSE")
    private boolean repse;
    
    public Proveedor() {
    }

    public Proveedor(Integer id) {
        this.id = id;
    }

    public Proveedor(Integer id, String rfc) {
        this.id = id;
        this.rfc = rfc;
    }


    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Proveedor)) {
            return false;
        }
        Proveedor other = (Proveedor) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    
    public String toString() {
        return "sia.modelo.Proveedor[ id=" + id + " ]";
    }

}
