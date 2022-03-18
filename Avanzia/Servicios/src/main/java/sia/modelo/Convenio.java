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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "CONVENIO")
@SequenceGenerator(sequenceName = "convenio_id_seq", name = "convenio_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Convenio.findAll", query = "SELECT c FROM Convenio c")})
@Setter
@Getter
public class Convenio implements Serializable {

    @OneToMany(mappedBy = "convenio")
    private Collection<Convenio> convenioCollection;
    @JoinColumn(name = "OC_SUBCAMPO", referencedColumnName = "ID")
    @ManyToOne
    private OcSubcampo ocSubcampo;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "convenio_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)

    @Column(name = "ID")
    private Integer id;
    @Size(max = 48)
    @Column(name = "CODIGO")
    private String codigo;
    @Size(max = 1000)
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "MONTO")
    private Double monto;
    @Column(name = "PORCENTAJE_DEDUCCION")
    private Double porcentajeDeduccion;
    @Column(name = "FECHA_INICIO")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    @Column(name = "FECHA_VENCIMIENTO")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;
    @Column(name = "FECHA_FIRMA")
    @Temporal(TemporalType.DATE)
    private Date fechaFirma;
    @Size(max = 50)
    @Column(name = "VIGENCIA")
    private String vigencia;
    @JoinColumn(name = "PROVEEDOR", referencedColumnName = "ID")
    @ManyToOne
    private Proveedor proveedor;
    @JoinColumn(name = "MONEDA", referencedColumnName = "ID")
    @ManyToOne
    private Moneda moneda;
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatus;
    @JoinColumn(name = "CV_TIPO", referencedColumnName = "ID")
    @ManyToOne
    private CvTipo cvTipo;

    @JoinColumn(name = "CV_CLASIFICACION", referencedColumnName = "ID")
    @ManyToOne
    private CvClasificacion cvClasificacion;

    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;
    @JoinColumn(name = "CONVENIO", referencedColumnName = "ID")
    @ManyToOne
    private Convenio convenio;
    @OneToMany(mappedBy = "convenio")
    private Collection<CvRelacionActividad> cvRelacionActividadCollection;
    @OneToMany(mappedBy = "convenio")
    private Collection<CaracteristicasServicio> caracteristicasServicioCollection;

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

    @JoinColumn(name = "FINALIZO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario finalizo;
    @Column(name = "MENSAJE_FINALIZO")
    private String mensajeFinalizo;
    @Column(name = "FECHA_FINALIZO")
    @Temporal(TemporalType.DATE)
    private Date fechaFinalizo;
    @Column(name = "HORA_FINALIZO")
    @Temporal(TemporalType.TIME)
    private Date horaFinalizo;

    //
    public Convenio() {
    }

    public Convenio(int id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Convenio)) {
            return false;
        }
        Convenio other = (Convenio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.Convenio[ id=" + id + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Convenio> getConvenioCollection() {
        return convenioCollection;
    }

    public void setConvenioCollection(Collection<Convenio> convenioCollection) {
        this.convenioCollection = convenioCollection;
    }

    public OcSubcampo getOcSubcampo() {
        return ocSubcampo;
    }

    public void setOcSubcampo(OcSubcampo ocSubcampo) {
        this.ocSubcampo = ocSubcampo;
    }
}
