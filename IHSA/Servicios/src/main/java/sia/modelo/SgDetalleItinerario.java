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
import sia.constantes.Constantes;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_DETALLE_ITINERARIO")
@SequenceGenerator(sequenceName = "sg_detalle_itinerario_id_seq", name = "sg_detalle_itinerario_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgDetalleItinerario.findAll", query = "SELECT s FROM SgDetalleItinerario s")})
public class SgDetalleItinerario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_detalle_itinerario_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "SG_ITINERARIO", referencedColumnName = "ID")
    @ManyToOne
    private SgItinerario sgItinerario;       
    @JoinColumn(name = "SG_AEROLINEA", referencedColumnName = "ID")
    @ManyToOne
    private SgAerolinea sgAerolinea;
    @NotNull
    @JoinColumn(name = "SI_CIUDAD_ORIGEN", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SiCiudad siCiudadOrigen;        
    @NotNull
    @JoinColumn(name = "SI_CIUDAD_DESTINO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SiCiudad siCiudadDestino;     
    @Size(max = 8)
    @Column(name = "NUMERO_VUELO")
    private String numeroVuelo;
    @Column(name = "FECHA_SALIDA")
    @Temporal(TemporalType.DATE)
    private Date fechaSalida;
    @Column(name = "HORA_SALIDA")
    @Temporal(TemporalType.TIME)
    private Date horaSalida;   
    @Column(name = "FECHA_LLEGADA")
    @Temporal(TemporalType.DATE)
    private Date fechaLlegada;
    @Column(name = "HORA_LLEGADA")
    @Temporal(TemporalType.TIME)
    private Date horaLlegada;
    @Column(name = "TIEMPO_VUELO")
    private Double tiempoVuelo;    
    @NotNull
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional = false)
    @NotNull
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
    
    @Column(name = "HISTORIAL")
    private boolean historial;    
    
    public SgDetalleItinerario() {
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
     * @return the sgItinerario
     */
    public SgItinerario getSgItinerario() {
        return sgItinerario;
    }

    /**
     * @param sgItinerario the sgItinerario to set
     */
    public void setSgItinerario(SgItinerario sgItinerario) {
        this.sgItinerario = sgItinerario;
    }

    /**
     * @return the sgAerolinea
     */
    public SgAerolinea getSgAerolinea() {
        return sgAerolinea;
    }

    /**
     * @param sgAerolinea the sgAerolinea to set
     */
    public void setSgAerolinea(SgAerolinea sgAerolinea) {
        this.sgAerolinea = sgAerolinea;
    }

    /**
     * @return the siCiudadOrigen
     */
    public SiCiudad getSiCiudadOrigen() {
        return siCiudadOrigen;
    }

    /**
     * @param siCiudadOrigen the siCiudadOrigen to set
     */
    public void setSiCiudadOrigen(SiCiudad siCiudadOrigen) {
        this.siCiudadOrigen = siCiudadOrigen;
    }

    /**
     * @return the siCiudadDestino
     */
    public SiCiudad getSiCiudadDestino() {
        return siCiudadDestino;
    }

    /**
     * @param siCiudadDestino the siCiudadDestino to set
     */
    public void setSiCiudadDestino(SiCiudad siCiudadDestino) {
        this.siCiudadDestino = siCiudadDestino;
    }

    /**
     * @return the numeroVuelo
     */
    public String getNumeroVuelo() {
        return numeroVuelo;
    }

    /**
     * @param numeroVuelo the numeroVuelo to set
     */
    public void setNumeroVuelo(String numeroVuelo) {
        this.numeroVuelo = numeroVuelo;
    }

    /**
     * @return the fechaSalida
     */
    public Date getFechaSalida() {
        return fechaSalida;
    }

    /**
     * @param fechaSalida the fechaSalida to set
     */
    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    /**
     * @return the horaSalida
     */
    public Date getHoraSalida() {
        return horaSalida;
    }

    /**
     * @param horaSalida the horaSalida to set
     */
    public void setHoraSalida(Date horaSalida) {
        this.horaSalida = horaSalida;
    }

    /**
     * @return the fechaLlegada
     */
    public Date getFechaLlegada() {
        return fechaLlegada;
    }

    /**
     * @param fechaLlegada the fechaLlegada to set
     */
    public void setFechaLlegada(Date fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }

    /**
     * @return the horaLlegada
     */
    public Date getHoraLlegada() {
        return horaLlegada;
    }

    /**
     * @param horaLlegada the horaLlegada to set
     */
    public void setHoraLlegada(Date horaLlegada) {
        this.horaLlegada = horaLlegada;
    }

    /**
     * @return the tiempoVuelo
     */
    public Double getTiempoVuelo() {
        return tiempoVuelo;
    }

    /**
     * @param tiempoVuelo the tiempoVuelo to set
     */
    public void setTiempoVuelo(Double tiempoVuelo) {
        this.tiempoVuelo = tiempoVuelo;
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
    
    public String getTiempoVueloString() {
        Double tmp = getTiempoVuelo();
        int totalMinutos = (int) (tmp * 60);
//        int totalMinutos = tmp.intValue();

        if (totalMinutos < 60) {
            return (totalMinutos + " mins.");
        } else if (totalMinutos == 60) {
            return (1 + " hr.");
        }
        else if(totalMinutos%60 == 0) {
            return (totalMinutos/60 + " hrs.");
        }
        else {
            int horas = tmp.intValue();
            int minutos = (totalMinutos - (horas * 60));
            return (horas + " hrs., " + minutos + " mins.");
        }
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgDetalleItinerario)) {
            return false;
        }
        SgDetalleItinerario other = (SgDetalleItinerario) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
            .append("id=").append(this.getId())
            .append(", sgItinerario=").append(this.getSgItinerario() != null ? this.getSgItinerario().getId() : null)
            .append(", sgAerolinea=").append(this.getSgAerolinea() != null ? this.getSgAerolinea().getId() : null)
            .append(", siCiudadOrigen=").append(this.getSiCiudadOrigen() != null ? this.getSiCiudadOrigen().getId() : null)
            .append(", siCiudadDestino=").append(this.getSiCiudadDestino() != null ? this.getSiCiudadDestino().getId() : null)
            .append(", numeroVuelo=").append(this.getNumeroVuelo())    
            .append(", fechaSalida=").append(this.getFechaSalida() != null ? (Constantes.FMT_ddMMyyy.format(this.getFechaSalida())) : null)
            .append(", horaSalida=").append(this.getHoraSalida() != null ? (Constantes.FMT_HHmmss.format(this.getHoraSalida())) : null)
            .append(", fechaLlegada=").append(this.getFechaLlegada() != null ? (Constantes.FMT_ddMMyyy.format(this.getFechaLlegada())) : null)
            .append(", horaLlegada=").append(this.getHoraLlegada() != null ? (Constantes.FMT_HHmmss.format(this.getHoraLlegada())) : null)                      
            .append(", tiempoVuelo=").append(this.getTiempoVuelo())    
            .append(", genero=").append(this.getGenero() != null ? this.getGenero().getId() : null)
            .append(", fechaGenero=").append(this.getFechaGenero() != null ? (Constantes.FMT_ddMMyyy.format(this.getFechaGenero())) : null)
            .append(", horaGenero=").append(this.getHoraGenero() != null ? (Constantes.FMT_HHmmss.format(this.getHoraGenero())) : null)
            .append(", modifico=").append(this.getModifico() != null ? this.getModifico().getId() : null)
            .append(", fechaModifico=").append(this.getFechaModifico() != null ? (Constantes.FMT_ddMMyyy.format(this.getFechaModifico())) : null)
            .append(", horaModifico=").append(this.getHoraModifico() != null ? (Constantes.FMT_HHmmss.format(this.getHoraModifico())) : null)
            .append(", eliminado=").append(this.isEliminado())
            .append("}");
        
        return sb.toString();
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
}