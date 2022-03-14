/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Entity
@Getter
@Setter
@Table(name = "PV_CLASIFICACION_ARCHIVO")
@SequenceGenerator(sequenceName = "pv_clasificacion_archivo_id_seq", name = "pv_clasificacion_archivo_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PvClasificacionArchivo.findAll", query = "SELECT o FROM PvClasificacionArchivo o where o.eliminado = false")
    ,
    @NamedQuery(name = "PvClasificacionArchivo.buscarPorId",
            query = "SELECT o FROM PvClasificacionArchivo o where o.id = ?1 ")})
public class PvClasificacionArchivo implements Serializable {

    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;

    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SI_ADJUNTO", referencedColumnName = "ID")
    @ManyToOne
    private SiAdjunto siAdjunto;
    @JoinColumn(name = "PV_DOCUMENTO", referencedColumnName = "ID")
    @ManyToOne
    private PvDocumento pvDocumento;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "pv_clasificacion_archivo_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)

    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "PROVEEDOR", referencedColumnName = "ID")
    @ManyToOne
    private Proveedor proveedor;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @Column(name = "FECHA_ENTREGA")
    @Temporal(TemporalType.DATE)
    private Date fechaEntrega;
    @Column(name = "INICIO_VIGENCIA")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Column(name = "FIN_VIGENCIA")
    @Temporal(TemporalType.DATE)
    private Date finVigencia;

    @Column(name = "VALIDO")
    private boolean valido;

    @Column(name = "OBLIGATORIA")
    private boolean obligatoria;

    public PvClasificacionArchivo() {
    }

    public PvClasificacionArchivo(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PvClasificacionArchivo)) {
            return false;
        }
        PvClasificacionArchivo other = (PvClasificacionArchivo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.PvClasificacionArchivo[ id=" + id + " ]";
    }
}
