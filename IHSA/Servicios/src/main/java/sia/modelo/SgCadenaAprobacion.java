/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_CADENA_APROBACION")
@SequenceGenerator(sequenceName = "sg_cadena_aprobacion_id_seq", name = "sg_cadena_aprobacion_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgCadenaAprobacion.findAll", query = "SELECT s FROM SgCadenaAprobacion s")})
public @Data class SgCadenaAprobacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_cadena_aprobacion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "SG_TIPO_SOLICITUD_VIAJE", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoSolicitudViaje sgTipoSolicitudViaje;
    @JoinColumn(name = "GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private Gerencia gerencia;
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatus;
            
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
    
    
    @Column(name = "APRUEBA_ROL")
    private String apruebaRol;
    
    
    @Column(name = "VEFIRICA_SEMAFORO_ALTERNO")
    private String verificaSemaforoAlterno;
    
    
    @Column(name = "APRUEBA_GERENTE_AREA")
    private String apruebaGerenteArea;
    
   

    public SgCadenaAprobacion() {
    }

    public SgCadenaAprobacion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public SgTipoSolicitudViaje getSgTipoSolicitudViaje() {
        return sgTipoSolicitudViaje;
    }

    public void setSgTipoSolicitudViaje(SgTipoSolicitudViaje sgTipoSolicitudViaje) {
        this.sgTipoSolicitudViaje = sgTipoSolicitudViaje;
    }

    public Gerencia getGerencia() {
        return gerencia;
    }

    public void setGerencia(Gerencia gerencia) {
        this.gerencia = gerencia;
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
        if (!(object instanceof SgCadenaAprobacion)) {
            return false;
        }
        SgCadenaAprobacion other = (SgCadenaAprobacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
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

//    
//    public String toString() {
//        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
//        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
//        StringBuilder sb = new StringBuilder();
//        sb.append(this.getClass().getName())
//            .append("{")
//            .append("id=").append(this.id)
//            .append(", sgTipoSolicitudViaje=").append(this.sgTipoSolicitudViaje.getId() != null ? this.sgTipoSolicitudViaje.getId() : null)
//            .append(", gerencia=").append(this.gerencia.getId() != null ? this.gerencia.getId() : null)
//            .append(", estatus=").append(this.estatus.getId() != null ? this.estatus.getId() : null)
//            .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
//            .append(", fechaGenero=").append(this.fechaGenero != null ? (sdfFecha.format(this.fechaGenero)) : null)
//            .append(", horaGenero=").append(this.horaGenero != null ? (sdfHora.format(this.horaGenero)) : null)
//            .append(", modifico=").append(this.getModifico() != null ? this.getModifico().getId() : null)
//            .append(", fechaModifico=").append(this.getFechaModifico() != null ? (sdfFecha.format(this.getFechaModifico())) : null)
//            .append(", horaModifico=").append(this.getHoraModifico() != null ? (sdfHora.format(this.getHoraModifico())) : null)
//            .append(", eliminado=").append(this.eliminado)
//            .append("}");
//        
//        return sb.toString();
//    }
    
}
