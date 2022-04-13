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
import lombok.ToString;

/**
 *
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_SOLICITUD_VIAJE")
@SequenceGenerator(sequenceName = "sg_solicitud_viaje_id_seq", name = "sg_solicitud_viaje_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgSolicitudViaje.findAll", query = "SELECT s FROM SgSolicitudViaje s")
})
@Getter
@Setter
@ToString
public class SgSolicitudViaje implements Serializable {
    @Column(name =     "FECHA_SALIDA")
    @Temporal(TemporalType.DATE)
    private Date fechaSalida;
    @Column(name =     "HORA_SALIDA")
    @Temporal(TemporalType.TIME)
    private Date horaSalida;
    @Column(name =     "FECHA_REGRESO")
    @Temporal(TemporalType.DATE)
    private Date fechaRegreso;
    @Column(name = "HORA_REGRESO")
    @Temporal(TemporalType.TIME)
    private Date horaRegreso;
    @Basic(optional =     false)
    
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional =     false)
    @NotNull
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name =     "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name =     "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    
    @Column(name = "REDONDO")
    private boolean redondo;
//    @JoinColumn(name = "SG_ESTADO_SEMAFORO", referencedColumnName = "ID")
//    @ManyToOne
//    private SgEstadoSemaforo sgEstadoSemaforo;
    @Size(max = 2056)
    
    @OneToMany(mappedBy = "sgSolicitudViaje")
    private Collection<SgViajeCiudad> sgViajeCiudadCollection;

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_solicitud_viaje_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @NotNull
    @JoinColumn(name = "SG_TIPO_SOLICITUD_VIAJE", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoSolicitudViaje sgTipoSolicitudViaje;
    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;
    @NotNull
    @JoinColumn(name = "OFICINA_ORIGEN", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina oficinaOrigen;
    @JoinColumn(name = "OFICINA_DESTINO", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina oficinaDestino;
    @JoinColumn(name = "SG_MOTIVO", referencedColumnName = "ID")
    @ManyToOne
    private SgMotivo sgMotivo;
    @NotNull
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatus;
    @JoinColumn(name = "GERENCIA_RESPONSABLE", referencedColumnName = "ID")
    @ManyToOne
    private Gerencia gerenciaResponsable;      
    @Size(max = 12)
    @Column(name = "CODIGO")
    private String codigo;
    @Size(max = 256)
    @Column(name = "OBSERVACION")
    private String observacion;
    @JoinColumn(name = "CO_NOTICIA", referencedColumnName = "ID")
    @ManyToOne
    private CoNoticia coNoticia;
    @NotNull
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    
    @JoinColumn(name = "SG_MOTIVO_RETRASO", referencedColumnName = "ID")
    @ManyToOne
    private SgMotivoRetraso sgMotivoRetraso;

    @JoinColumn(name = "SG_RUTA_TERRESTRE", referencedColumnName = "ID")
    @ManyToOne
    private SgRutaTerrestre sgRutaTerrestre;
    
    
    @Column(name = "CONCHOFER")
    private boolean conChofer;
    
    @JoinColumn(name = "SG_VEHICULO", referencedColumnName = "ID")
    @ManyToOne
    private SgVehiculo sgVehiculo;
    
    @JoinColumn(name = "ap_campo", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;
    
    public SgSolicitudViaje() {
    }


    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgSolicitudViaje)) {
            return false;
        }
        SgSolicitudViaje other = (SgSolicitudViaje) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }    
   
}