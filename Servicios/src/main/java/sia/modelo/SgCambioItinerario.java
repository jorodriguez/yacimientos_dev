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

/**
 *
 * @author nlopez
 */
@Entity
@Table(name = "SG_CAMBIO_ITINERARIO")
@SequenceGenerator(sequenceName = "sg_cambio_itinerario_id_seq", name = "sg_cambio_itinerario_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgCambioItinerario .findAll", query = "SELECT s FROM SgCambioItinerario s")})
public class SgCambioItinerario implements Serializable {
    @Id
@GeneratedValue(generator =  "sg_cambio_itinerario_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;

    @Size(max = 500)
    @Column(name = "MENSAJE")
    private String mensaje;
    
    @JoinColumn(name = "SG_ITINERARIO", referencedColumnName = "ID")
    @ManyToOne
    private SgItinerario sgItinerario;
    
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    
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
    
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario modifico;
    
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    
    
    @Column(name = "HISTORIAL")
    private boolean historial;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
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

