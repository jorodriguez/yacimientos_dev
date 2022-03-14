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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Setter
@Getter
@Entity
@Table(name = "SI_INCIDENCIA")
@SequenceGenerator(sequenceName = "si_incidencia_id_seq", name = "si_incidencia_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SiIncidencia.findAll", query = "SELECT s FROM SiIncidencia s")})
public class SiIncidencia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "si_incidencia_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)

    @Column(name = "ID")
    private Integer id;
    @Size(max = 1024)
    @Column(name = "TITULO")
    private String titulo;
    @Size(max = 5024)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    
    @Column(name = "CODIGO")
    private Integer codigo;

    @Column(name = "GENERA_COSTO")
    private boolean generaCosto;
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
    @JoinColumn(name = "ESTADO", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatus;

    @JoinColumn(name = "SI_LOCALIZACION", referencedColumnName = "ID")
    @ManyToOne
    private SiLocalizacion siLocalizacion;

    @JoinColumn(name = "PRIORIDAD", referencedColumnName = "ID")
    @ManyToOne
    private Prioridad prioridad;
    @JoinColumn(name = "GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private Gerencia gerencia;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @OneToMany(mappedBy = "siIncidencia")
    private Collection<SiIncidenciaFactura> siIncidenciaFacturaCollection;
    @OneToMany(mappedBy = "siIncidencia")
    private Collection<SiIncidenciaVehiculo> siIncidenciaVehiculoCollection;
    @OneToMany(mappedBy = "siIncidencia")
    private Collection<SiIncidenciaAdjunto> siIncidenciaAdjuntoCollection;

    @JoinColumn(name = "SI_CATEGORIA_INCIDENCIA", referencedColumnName = "ID")
    @ManyToOne
    private SiCategoriaIncidencia siCategoriaIncidencia;
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;
    @JoinColumn(name = "ASIGNADO_A", referencedColumnName = "ID")
    @ManyToOne
    private Usuario asignadoA;
    @Column(name = "SOLUCION")
    private String solucion;
    @Column(name = "CODIGO_CATEGORIA")
    private String codigoCategoria;
    @Column(name = "MOTIVO_CIERRE")
    private String motivoCierre;
    @Column(name = "MOTIVO_ESCALA")
    private String motivoEscala;
    
    @JoinColumn(name = "SI_NIVEL", referencedColumnName = "ID")
    @ManyToOne
    private SiNivel siNivel;
    
    @Column(name = "ESCALADO")
    private boolean escalado;
    
    @Column(name = "duracion")
    private Integer duracion;

    public SiIncidencia() {
    }

    public SiIncidencia(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiIncidencia)) {
            return false;
        }
        SiIncidencia other = (SiIncidencia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SiIncidencia[ id=" + id + " ]";
    }
}
