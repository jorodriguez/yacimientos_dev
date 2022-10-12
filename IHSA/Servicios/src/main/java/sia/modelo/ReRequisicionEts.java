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
import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.Transient;
import sia.constantes.Constantes;
/**
 *
 * @author jorodriguez
 */
@Entity
@Table(name = "RE_REQUISICION_ETS")
@SequenceGenerator(sequenceName = "re_requisicion_ets_id_seq", name = "re_requisicion_ets_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ReRequisicionEts.findAll", query = "SELECT e FROM ReRequisicionEts e")})
public class ReRequisicionEts implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "re_requisicion_ets_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id; 
    
    @Column(name = "DISGREGADO")
    private boolean disgregado;  
    @JoinColumn(name = "REQUISICION", referencedColumnName = "ID")
    @ManyToOne
    private Requisicion requisicion;
    @JoinColumn(name = "SI_ADJUNTO", referencedColumnName = "ID")
    @ManyToOne
    private SiAdjunto siAdjunto;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    
    @Column(name = "VISIBLE")
    private boolean visible;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    
    @Transient
    private boolean selected = false;
    
    
    /**
     * @return the selected
     */
    
    public boolean isSelected() { return selected; }
    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) { this.selected = selected; }


    public ReRequisicionEts() {
    }

    public ReRequisicionEts(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ReRequisicionEts)) {
            return false;
        }
        ReRequisicionEts other = (ReRequisicionEts) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * @return the requisicion
     */
    public Requisicion getRequisicion() {
        return requisicion;
    }

    /**
     * @param requisicion the requisicion to set
     */
    public void setRequisicion(Requisicion requisicion) {
        this.requisicion = requisicion;
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
    
      
    public String toString() {
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
            .append("id=").append(this.id)
            .append(", requisicion=").append(this.requisicion != null ? this.requisicion.getId() : null)
            .append(", siAdjunto=").append(this.getSiAdjunto() != null ? this.getSiAdjunto().getId() : null)
            .append(", disgregadao=").append(this.isDisgregado())
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
     * @param horaGenero the horaGenero to set
     */
    public void setHoraGenero(Date horaGenero) {
        this.horaGenero = horaGenero;
    }

    /**
     * @return the siAdjunto
     */
    public SiAdjunto getSiAdjunto() {
        return siAdjunto;
    }

    /**
     * @param siAdjunto the siAdjunto to set
     */
    public void setSiAdjunto(SiAdjunto siAdjunto) {
        this.siAdjunto = siAdjunto;
    }

    /**
     * @return the disgregado
     */
    public boolean isDisgregado() {
        return disgregado;
    }

    /**
     * @param disgregado the disgregado to set
     */
    public void setDisgregado(boolean disgregado) {
        this.disgregado = disgregado;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
}
