/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_ESTATUS_APROBACION")
@SequenceGenerator(sequenceName = "sg_estatus_aprobacion_id_seq", name = "sg_estatus_aprobacion_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgEstatusAprobacion.findAll", query = "SELECT s FROM SgEstatusAprobacion s")})
public class SgEstatusAprobacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_estatus_aprobacion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatus;
    
    
    @Column(name = "REALIZADO")
    private boolean realizado;
    
    
    @Column(name = "HISTORIAL")
    private boolean historial;
    
    
    @Column(name = "AUTOMATICO")
    private boolean automatico;
    
    @JoinColumn(name = "SG_SOLICITUD_VIAJE", referencedColumnName = "ID")
    @ManyToOne
    private SgSolicitudViaje sgSolicitudViaje;
    
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
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    
    
    @JoinColumn(name = "SG_ESTADO_SEMAFORO", referencedColumnName = "ID")
    @ManyToOne
    private SgEstadoSemaforo sgEstadoSemaforo;

    public SgEstatusAprobacion() {
    }

    public SgEstatusAprobacion(Integer id) {
        this.id = id;
    }

  

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgEstatusAprobacion)) {
            return false;
        }
        SgEstatusAprobacion other = (SgEstatusAprobacion) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
            .append("id=").append(this.getId())
            .append(", sgSolicitudViaje=").append(this.sgSolicitudViaje.getId() != null ? this.sgSolicitudViaje.getId() : null)
            .append(", usuario=").append(this.usuario != null ? this.usuario.getId() : null)
            .append(", estatus=").append(this.estatus != null ? this.estatus.getId() : null)
            .append(", realizado=").append(this.realizado)                
            .append(", historial=").append(this.historial)
            .append(", automatico=").append(this.automatico)
            .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
            .append(", fechaGenero=").append(this.fechaGenero != null ? (sdfFecha.format(this.fechaGenero)) : null)
            .append(", horaGenero=").append(this.horaGenero != null ? (sdfHora.format(this.horaGenero)) : null)
            .append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
            .append(", fechaModifico=").append(this.fechaModifico != null ? (sdfFecha.format(this.fechaModifico)) : null)
            .append(", horaModifico=").append(this.horaModifico != null ? (sdfHora.format(this.horaModifico)) : null)
            .append(", eliminado=").append(this.eliminado)
            .append("}");
        
        return sb.toString();
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the estatus
     */
    public Estatus getEstatus() {
        return estatus;
    }

    /**
     * @param estatus the estatus to set
     */
    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    /**
     * @return the realizado
     */
    public boolean isRealizado() {
        return realizado;
    }

    /**
     * @param realizado the realizado to set
     */
    public void setRealizado(boolean realizado) {
        this.realizado = realizado;
    }

    /**
     * @return the historial
     */
    public boolean isHistorial() {
        return historial;
    }

    /**
     * @param historial the historial to set
     */
    public void setHistorial(boolean historial) {
        this.historial = historial;
    }

    /**
     * @return the sgSolicitudViaje
     */
    public SgSolicitudViaje getSgSolicitudViaje() {
        return sgSolicitudViaje;
    }

    /**
     * @param sgSolicitudViaje the sgSolicitudViaje to set
     */
    public void setSgSolicitudViaje(SgSolicitudViaje sgSolicitudViaje) {
        this.sgSolicitudViaje = sgSolicitudViaje;
    }

    /**
     * @return the fechaGenero
     */
    public Date getFechaGenero() {
        return fechaGenero;
    }

    /**
     * @param fechaGenero the fechaGenero to set
     */
    public void setFechaGenero(Date fechaGenero) {
        this.fechaGenero = fechaGenero;
    }

    /**
     * @return the horaGenero
     */
    public Date getHoraGenero() {
        return horaGenero;
    }

    /**
     * @param horaGenero the horaGenero to set
     */
    public void setHoraGenero(Date horaGenero) {
        this.horaGenero = horaGenero;
    }

    /**
     * @return the eliminado
     */
    public boolean isEliminado() {
        return eliminado;
    }

    /**
     * @param eliminado the eliminado to set
     */
    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    /**
     * @return the genero
     */
    public Usuario getGenero() {
        return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    /**
     * @return the modifico
     */
    public Usuario getModifico() {
        return modifico;
    }

    /**
     * @param modifico the modifico to set
     */
    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
    }

    /**
     * @return the fechaModifico
     */
    public Date getFechaModifico() {
        return fechaModifico;
    }

    /**
     * @param fechaModifico the fechaModifico to set
     */
    public void setFechaModifico(Date fechaModifico) {
        this.fechaModifico = fechaModifico;
    }

    /**
     * @return the horaModifico
     */
    public Date getHoraModifico() {
        return horaModifico;
    }

    /**
     * @param horaModifico the horaModifico to set
     */
    public void setHoraModifico(Date horaModifico) {
        this.horaModifico = horaModifico;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the automatico
     */
    public boolean isAutomatico() {
        return automatico;
    }

    /**
     * @param automatico the automatico to set
     */
    public void setAutomatico(boolean automatico) {
        this.automatico = automatico;
    }

    /**
     * @return the sgEstadoSemaforo
     */
    public SgEstadoSemaforo getSgEstadoSemaforo() {
        return sgEstadoSemaforo;
    }

    /**
     * @param sgEstadoSemaforo the sgEstadoSemaforo to set
     */
    public void setSgEstadoSemaforo(SgEstadoSemaforo sgEstadoSemaforo) {
        this.sgEstadoSemaforo = sgEstadoSemaforo;
    }
    
}
