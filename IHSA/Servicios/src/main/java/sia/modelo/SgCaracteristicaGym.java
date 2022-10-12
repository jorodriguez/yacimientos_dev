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
@Table(name = "SG_CARACTERISTICA_GYM")
@SequenceGenerator(sequenceName = "sg_caracteristica_gym_id_seq", name = "sg_caracteristica_gym_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgCaracteristicaGym.findAll", query = "SELECT s FROM SgCaracteristicaGym s")})
public class SgCaracteristicaGym implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_caracteristica_gym_seq", strategy = GenerationType.SEQUENCE)
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
    @JoinColumn(name = "SG_GYM", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgGym sgGym;
    @JoinColumn(name = "SG_CARACTERISTICA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgCaracteristica sgCaracteristica;

    public SgCaracteristicaGym() {
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
     * @return the sgGym
     */
    public SgGym getSgGym() {
        return sgGym;
    }

    /**
     * @param sgGym the sgGym to set
     */
    public void setSgGym(SgGym sgGym) {
        this.sgGym = sgGym;
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
        if (!(object instanceof SgCaracteristicaGym)) {
            return false;
        }
        SgCaracteristicaGym other = (SgCaracteristicaGym) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    
    public String toString() {
        SimpleDateFormat sdfd = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdft = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
            .append("id=").append(this.id)
            .append(", cantidad=").append(this.cantidad)
            .append(", sgGym=").append(this.sgGym != null ? this.sgGym.getId() : null)
            .append(", sgCaracteristica=").append(this.sgCaracteristica != null ? this.sgCaracteristica.getId() : null)
            .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
            .append(", fechaGenero=").append(this.fechaGenero != null ? (sdfd.format(this.fechaGenero)) : null)
            .append(", horaGenero=").append(this.horaGenero != null ? (sdft.format(this.horaGenero)) : null)
            .append(", eliminado=").append(this.eliminado)
            .append("}");
        
        return sb.toString();
    }    
}