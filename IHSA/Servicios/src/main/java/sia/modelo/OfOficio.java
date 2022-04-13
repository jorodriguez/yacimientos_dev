

package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author esapien
 */
@Entity
@Table(name = "OF_OFICIO")
@SequenceGenerator(sequenceName = "of_oficio_id_seq", name = "of_oficio_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "OfOficio.findAll", query = "SELECT o FROM OfOficio o")})
@Data
public class OfOficio implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    
@GeneratedValue(generator =  "of_oficio_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Integer id;
    
    @Basic(optional =     false)
    @NotNull
    @Column(name = "FECHA_OFICIO")
    @Temporal(TemporalType.DATE)
    private Date fechaOficio;
    @Column(name =     "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name =     "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name =     "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name =     "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Estatus estatus;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "OF_TIPO_OFICIO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private OfTipoOficio ofTipoOficio;
    @JoinColumn(name = "AP_CAMPO_GERENCIA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private ApCampoGerencia apCampoGerencia;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "NUMERO_OFICIO")
    private String numeroOficio;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 300)
    @Column(name = "ASUNTO")
    private String asunto;
    @Size(max = 200)
    @Column(name = "OBSERVACIONES")
    private String observaciones;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @Column(name =     "FECHA_NOTIFICO_ALTA")
    @Temporal(TemporalType.DATE)
    private Date fechaNotificoAlta;
    @Column(name =     "HORA_NOTIFICO_ALTA")
    @Temporal(TemporalType.TIME)
    private Date horaNotificoAlta;
    
    @Column(name = "URGENTE")
    private boolean urgente;
    
    // Valor default inicial: False
    
    @Column(name = "SEGUIMIENTO")
    private boolean seguimiento = false;
    
    @JoinColumn(name = "CO_PRIVACIDAD", referencedColumnName = "ID")
    @ManyToOne
    private CoPrivacidad coPrivacidad;
    

    public String getNumeroOficio() {
        return numeroOficio;
    }

    public void setNumeroOficio(String numeroOficio) {
        this.numeroOficio = numeroOficio;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }
    
    public OfOficio() {
    }

    public OfOficio(Integer id) {
        this.id = id;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaOficio() {
        return fechaOficio;
    }

    public void setFechaOficio(Date fechaOficio) {
        this.fechaOficio = fechaOficio;
    }
    
    public String getObservaciones() {
        return this.observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Date getFechaNotificoAlta() {
        return fechaNotificoAlta;
    }

    public void setFechaNotificoAlta(Date fechaNotificoAlta) {
        this.fechaNotificoAlta = fechaNotificoAlta;
    }

    public Date getHoraNotificoAlta() {
        return horaNotificoAlta;
    }

    public void setHoraNotificoAlta(Date horaNotificoAlta) {
        this.horaNotificoAlta = horaNotificoAlta;
    }

    public boolean isUrgente() {
        return urgente;
    }

    public void setUrgente(boolean urgente) {
        this.urgente = urgente;
    }
    
    public Usuario getModifico() {
        return modifico;
    }

    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public OfTipoOficio getOfTipoOficio() {
        return ofTipoOficio;
    }

    public void setOfTipoOficio(OfTipoOficio ofTipoOficio) {
        this.ofTipoOficio = ofTipoOficio;
    }

    public ApCampoGerencia getApCampoGerencia() {
        return apCampoGerencia;
    }

    public void setApCampoGerencia(ApCampoGerencia apCampoGerencia) {
        this.apCampoGerencia = apCampoGerencia;
    }

    public Estatus getEstatus() {
        return estatus;
    }

    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OfOficio)) {
            return false;
        }
        OfOficio other = (OfOficio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
