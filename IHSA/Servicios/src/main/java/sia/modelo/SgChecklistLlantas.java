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

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_CHECKLIST_LLANTAS")
@SequenceGenerator(sequenceName = "sg_checklist_llantas_id_seq", name = "sg_checklist_llantas_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgChecklistLlantas.findAll", query = "SELECT s FROM SgChecklistLlantas s")})
public class SgChecklistLlantas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_checklist_llantas_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "BUEN_ESTADO")
    private boolean buenEstado;
    @Size(max = 1024)
    @Column(name = "OBSERVACION")    
    private String observacion;
    @Size(max = 10)
    @Column(name = "REFACCION")
    private String refaccion;
    @Size(max = 10)
    @Column(name = "TRASERA_IZQUIERDA")
    private String traseraIzquierda;
    @Size(max = 10)
    @Column(name = "TRASERA_DERECHA")
    private String traseraDerecha;
    @Size(max = 10)
    @Column(name = "DELANTERA_IZQUIERDA")
    private String delanteraIzquierda;
    @Size(max = 10)
    @Column(name = "DELANTERA_DERECHA")
    private String delanteraDerecha;
    @JoinColumn(name = "SG_CHECKLIST", referencedColumnName = "ID")
    @ManyToOne
    private SgChecklist sgChecklist;    
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

    public SgChecklistLlantas() {
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
     * @return the buenEstado
     */
    public boolean isBuenEstado() {
        return buenEstado;
    }

    /**
     * @param buenEstado the buenEstado to set
     */
    public void setBuenEstado(boolean buenEstado) {
        this.buenEstado = buenEstado;
    }

    /**
     * @return the observacion
     */
    public String getObservacion() {
        return observacion;
    }

    /**
     * @param observacion the observacion to set
     */
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    /**
     * @return the refaccion
     */
    public String getRefaccion() {
        return refaccion;
    }

    /**
     * @param refaccion the refaccion to set
     */
    public void setRefaccion(String refaccion) {
        this.refaccion = refaccion;
    }

    /**
     * @return the traseraIzquierda
     */
    public String getTraseraIzquierda() {
        return traseraIzquierda;
    }

    /**
     * @param traseraIzquierda the traseraIzquierda to set
     */
    public void setTraseraIzquierda(String traseraIzquierda) {
        this.traseraIzquierda = traseraIzquierda;
    }

    /**
     * @return the traseraDerecha
     */
    public String getTraseraDerecha() {
        return traseraDerecha;
    }

    /**
     * @param traseraDerecha the traseraDerecha to set
     */
    public void setTraseraDerecha(String traseraDerecha) {
        this.traseraDerecha = traseraDerecha;
    }

    /**
     * @return the delanteraIzquierda
     */
    public String getDelanteraIzquierda() {
        return delanteraIzquierda;
    }

    /**
     * @param delanteraIzquierda the delanteraIzquierda to set
     */
    public void setDelanteraIzquierda(String delanteraIzquierda) {
        this.delanteraIzquierda = delanteraIzquierda;
    }

    /**
     * @return the delanteraDerecha
     */
    public String getDelanteraDerecha() {
        return delanteraDerecha;
    }

    /**
     * @param delanteraDerecha the delanteraDerecha to set
     */
    public void setDelanteraDerecha(String delanteraDerecha) {
        this.delanteraDerecha = delanteraDerecha;
    }

    /**
     * @return the sgChecklist
     */
    public SgChecklist getSgChecklist() {
        return sgChecklist;
    }

    /**
     * @param sgChecklist the sgChecklist to set
     */
    public void setSgChecklist(SgChecklist sgChecklist) {
        this.sgChecklist = sgChecklist;
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

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgChecklistLlantas)) {
            return false;
        }
        SgChecklistLlantas other = (SgChecklistLlantas) object;
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
            .append("id=").append(this.id)
            .append(", buenEstado=").append(this.buenEstado)
            .append(", observacion=").append(this.observacion)
            .append(", refaccion=").append(this.refaccion)
            .append(", traseraIzquierda=").append(this.traseraIzquierda)
            .append(", traseraDerecha=").append(this.traseraDerecha)
            .append(", delanteraDerecha=").append(this.delanteraDerecha)
            .append(", delanteraIzquierda=").append(this.delanteraIzquierda)
            .append(", sgChecklist=").append(this.sgChecklist != null ? this.sgChecklist.getId() : null)
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
}