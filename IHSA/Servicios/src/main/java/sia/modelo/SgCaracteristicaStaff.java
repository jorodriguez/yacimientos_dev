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
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_CARACTERISTICA_STAFF")
@SequenceGenerator(sequenceName = "sg_caracteristica_staff_id_seq", name = "sg_caracteristica_staff_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgCaracteristicaStaff.findAll", query = "SELECT s FROM SgCaracteristicaStaff s")})
public class SgCaracteristicaStaff implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_caracteristica_staff_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Column(name = "CANTIDAD")
    private Integer cantidad;    
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
    @JoinColumn(name = "SG_STAFF", referencedColumnName = "ID")
    @ManyToOne
    private SgStaff sgStaff;
    @JoinColumn(name = "SG_CARACTERISTICA", referencedColumnName = "ID")
    @ManyToOne
    private SgCaracteristica sgCaracteristica;

    public SgCaracteristicaStaff() {
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
     * @return the cantidad
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
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
     * @return the sgStaff
     */
    public SgStaff getSgStaff() {
        return sgStaff;
    }

    /**
     * @param sgStaff the sgStaff to set
     */
    public void setSgStaff(SgStaff sgStaff) {
        this.sgStaff = sgStaff;
    }

    /**
     * @return the sgCaracteristica
     */
    public SgCaracteristica getSgCaracteristica() {
        return sgCaracteristica;
    }

    /**
     * @param sgCaracteristica the sgCaracteristica to set
     */
    public void setSgCaracteristica(SgCaracteristica sgCaracteristica) {
        this.sgCaracteristica = sgCaracteristica;
    }
    
    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgCaracteristicaStaff)) {
            return false;
        }
        SgCaracteristicaStaff other = (SgCaracteristicaStaff) object;
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
            .append(", cantidad=").append(this.cantidad)
            .append(", sgStaff=").append(this.sgStaff != null ? this.sgStaff.getId() : null)
            .append(", sgCaracteristica=").append(this.sgCaracteristica != null ? this.sgCaracteristica.getId() : null)                
            .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
            .append(", fechaGenero=").append(this.fechaGenero != null ? (sdfFecha.format(this.fechaGenero)) : null)
            .append(", horaGenero=").append(this.horaGenero != null ? (sdfHora.format(this.horaGenero)) : null)
            .append(", eliminado=").append(this.eliminado)
            .append("}");
        
        return sb.toString();
    }
}
