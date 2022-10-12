package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mrojas
 */
@Entity
@Table(name = "dd_sesion")
@SequenceGenerator(sequenceName = "dd_sesion_id_seq", name = "dd_sesion_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DdSesion.findAll", query = "SELECT d FROM DdSesion d")
    , @NamedQuery(name = "DdSesion.findById", query = "SELECT d FROM DdSesion d WHERE d.id = :id")
    , @NamedQuery(name = "DdSesion.findBySesionId", query = "SELECT d FROM DdSesion d WHERE d.sesionId = :sesionId")
    , @NamedQuery(name = "DdSesion.findByFechaInicio", query = "SELECT d FROM DdSesion d WHERE d.fechaInicio = :fechaInicio")
    , @NamedQuery(name = "DdSesion.findByFechaFin", query = "SELECT d FROM DdSesion d WHERE d.fechaFin = :fechaFin")
    , @NamedQuery(name = "DdSesion.findByPuntoAcceso", query = "SELECT d FROM DdSesion d WHERE d.puntoAcceso = :puntoAcceso")
    , @NamedQuery(name = "DdSesion.findByFechaGenero", query = "SELECT d FROM DdSesion d WHERE d.fechaGenero = :fechaGenero")
    , @NamedQuery(name = "DdSesion.findByHoraGenero", query = "SELECT d FROM DdSesion d WHERE d.horaGenero = :horaGenero")
    , @NamedQuery(name = "DdSesion.findByFechaModifico", query = "SELECT d FROM DdSesion d WHERE d.fechaModifico = :fechaModifico")
    , @NamedQuery(name = "DdSesion.findByHoraModifico", query = "SELECT d FROM DdSesion d WHERE d.horaModifico = :horaModifico")
    , @NamedQuery(name = "DdSesion.findByEliminado", query = "SELECT d FROM DdSesion d WHERE d.eliminado = :eliminado")})
public class DdSesion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "sesion_id")
    private String sesionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFin;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "punto_acceso")
    private String puntoAcceso;
    @Basic(optional = false)
    @NotNull
    @Column(name = "datos_cliente")
    private String datosCliente;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_genero")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional = false)
    @NotNull
    @Column(name = "hora_genero")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name = "fecha_modifico")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "hora_modifico")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @Basic(optional = false)
    @NotNull
    private boolean eliminado;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "genero")
    private String genero;
    
    
    @Column(name = "modifico")
    private String modifico;
    

    public DdSesion() {
    }

    public DdSesion(Integer id) {
        this.id = id;
    }

    public DdSesion(Integer id, String sesionId, Date fechaInicio, String puntoAcceso, String datosCliente, Date fechaGenero, Date horaGenero, boolean eliminado) {
        this.id = id;
        this.sesionId = sesionId;
        this.fechaInicio = fechaInicio;
        this.puntoAcceso = puntoAcceso;
        this.datosCliente = datosCliente;
        this.fechaGenero = fechaGenero;
        this.horaGenero = horaGenero;
        this.eliminado = eliminado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSesionId() {
        return sesionId;
    }

    public void setSesionId(String sesionId) {
        this.sesionId = sesionId;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getPuntoAcceso() {
        return puntoAcceso;
    }

    public void setPuntoAcceso(String puntoAcceso) {
        this.puntoAcceso = puntoAcceso;
    }

    public Object getDatosCliente() {
        return datosCliente;
    }

    public void setDatosCliente(String datosCliente) {
        this.datosCliente = datosCliente;
    }

    public Date getFechaGenero() {
        return fechaGenero;
    }

    public void setFechaGenero(Date fechaGenero) {
        this.fechaGenero = fechaGenero;
    }

    public Date getHoraGenero() {
        return horaGenero;
    }

    public void setHoraGenero(Date horaGenero) {
        this.horaGenero = horaGenero;
    }

    public Date getFechaModifico() {
        return fechaModifico;
    }

    public void setFechaModifico(Date fechaModifico) {
        this.fechaModifico = fechaModifico;
    }

    public Date getHoraModifico() {
        return horaModifico;
    }

    public void setHoraModifico(Date horaModifico) {
        this.horaModifico = horaModifico;
    }

    public boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    
    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getModifico() {
        return modifico;
    }

    public void setModifico(String modifico) {
        this.modifico = modifico;
    }
    
    
    
    
    

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DdSesion)) {
            return false;
        }
        DdSesion other = (DdSesion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.DdSesion[ id=" + id + " ]";
    }
    
}
