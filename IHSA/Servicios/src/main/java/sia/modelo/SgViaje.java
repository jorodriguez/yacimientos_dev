/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_VIAJE")
@SequenceGenerator(sequenceName = "sg_viaje_id_seq", name = "sg_viaje_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgViaje.findAll", query = "SELECT s FROM SgViaje s")})
@Getter
@Setter
public class SgViaje implements Serializable {

    @Column(name = "FECHA_SALIDA")
    @Temporal(TemporalType.DATE)
    private Date fechaSalida;
    @Column(name = "HORA_SALIDA")
    @Temporal(TemporalType.TIME)
    private Date horaSalida;
    @Column(name = "FECHA_REGRESO")
    @Temporal(TemporalType.DATE)
    private Date fechaRegreso;
    @Column(name = "HORA_REGRESO")
    @Temporal(TemporalType.TIME)
    private Date horaRegreso;
    @Basic(optional = false)
    
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional = false)
    @NotNull
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @Column(name = "FECHA_PROGRAMADA")
    @Temporal(TemporalType.DATE)
    private Date fechaProgramada;
    @Column(name = "HORA_PROGRAMADA")
    @Temporal(TemporalType.TIME)
    private Date horaProgramada;
    @OneToMany(mappedBy = "sgViaje")
    private Collection<SgViajeEstadoSemaforo> sgViajeEstadoSemaforoCollection;
    @OneToMany(mappedBy = "sgViaje")
    private Collection<SgViaje> sgViajeCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_viaje_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Size(max = 12)
    @Column(name = "CODIGO")
    private String codigo;
    @JoinColumn(name = "RESPONSABLE", referencedColumnName = "ID")
    @ManyToOne
    private Usuario responsable;
    @JoinColumn(name = "SI_ADJUNTO", referencedColumnName = "ID")
    @ManyToOne
    private SiAdjunto siAdjunto;
    @JoinColumn(name = "SG_OFICINA", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina sgOficina;
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatus;
    @JoinColumn(name = "SG_VIAJE", referencedColumnName = "ID")
    @ManyToOne
    private SgViaje sgViaje;
    
    @Column(name = "AUTOBUS")
    private boolean autobus;
    
    @Column(name = "VEHICULO_PROPIO")
    private boolean vehiculoPropio;
    
    @Column(name = "VEHICULO_ASIGNADO_EMPRESA")
    private boolean vehiculoAsignadoEmpresa;
    @JoinColumn(name = "SG_RUTA_TERRESTRE", referencedColumnName = "ID")
    @ManyToOne
    private SgRutaTerrestre sgRutaTerrestre;
    @JoinColumn(name = "SG_VIAJE_CIUDAD", referencedColumnName = "ID")
    @ManyToOne
    private SgViajeCiudad sgViajeCiudad;
    @JoinColumn(name = "SG_VIAJE_LUGAR", referencedColumnName = "ID")
    @ManyToOne
    private SgViajeLugar sgViajeLugar;
    //
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;
    @JoinColumn(name = "CO_NOTICIA", referencedColumnName = "ID")
    @ManyToOne
    private CoNoticia coNoticia;
    @JoinColumn(name = "SG_ITINERARIO", referencedColumnName = "ID")
    @ManyToOne
    private SgItinerario sgItinerario;
    
    @Column(name = "REDONDO")
    private boolean redondo;
    @JoinColumn(name = "ESTATUS_ANTERIOR", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatusAnterior;
    @JoinColumn(name = "USUARIO_REGRESA_VIAJE", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuarioRegresaViaje;
    @JoinColumn(name = "AUTORIZO_EME", referencedColumnName = "ID")
    @ManyToOne
    private Usuario autorizo_eme;
    @Column(name = "FECHA_LLEGADA")
    @Temporal(TemporalType.DATE)
    private Date fechaLlegada;
    @Column(name = "HORA_LLEGADA")
    @Temporal(TemporalType.TIME)
    private Date horaLlegada;
    
    @Column(name = "CONCHOFER")
    private boolean conChofer;
    
    @Column(name = "CON_INTERCEPCION")
    private boolean conIntercepcion;

    public SgViaje(int id) {
        this.id = id;
    }

    public SgViaje() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgAccesorio)) {
            return false;
        }
        SgViaje other = (SgViaje) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}