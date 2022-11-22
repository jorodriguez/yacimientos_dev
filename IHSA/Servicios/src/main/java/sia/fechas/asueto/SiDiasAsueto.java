/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.fechas.asueto;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import sia.modelo.SiModulo;
import sia.modelo.Usuario;

/**
 *
 * @author ihsa
 */
@Entity
@Table(name = "SI_DIAS_ASUETO")
@NamedQueries({
    @NamedQuery(name = "SiDiasAsueto.findAll", query = "SELECT s FROM SiDiasAsueto s")})
public class SiDiasAsueto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Column(name = "FECHA")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 70)
    @Column(name = "DESCRIPCION")
    private String descrpcion;
     @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
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
    @Size(max = 5)
    @Column(name = "ELIMINADO")
    private boolean eliminado;
     @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico; 
     @JoinColumn(name = "SI_MODULO", referencedColumnName = "ID")
    @ManyToOne
    private SiModulo siModulo;
    
    

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
        if (!(object instanceof SiDiasAsueto)) {
            return false;
        }
        SiDiasAsueto other = (SiDiasAsueto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.fechas.asueto.SiDiasAsueto[ id=" + id + " ]";
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the descrpcion
     */
    public String getDescrpcion() {
        return descrpcion;
    }

    /**
     * @param descrpcion the descrpcion to set
     */
    public void setDescrpcion(String descrpcion) {
        this.descrpcion = descrpcion;
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
     * @return the siModulo
     */
    public SiModulo getSiModulo() {
        return siModulo;
    }

    /**
     * @param siModulo the siModulo to set
     */
    public void setSiModulo(SiModulo siModulo) {
        this.siModulo = siModulo;
    }
    
}
