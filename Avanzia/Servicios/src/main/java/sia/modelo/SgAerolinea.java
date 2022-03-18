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
@Table(name = "SG_AEROLINEA")
@SequenceGenerator(sequenceName = "sg_aerolinea_id_seq", name = "sg_aerolinea_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgAerolinea.findAll", query = "SELECT a FROM SgAerolinea a")})
public class SgAerolinea implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sg_aerolinea_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)

    @Column(name = "ID")
    private Integer id;
    @Size(max = 64)
    @Column(name = "NOMBRE")
    private String nombre;
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

    public SgAerolinea() {
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
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
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
        if (!(object instanceof SgAerolinea)) {
            return false;
        }
        SgAerolinea other = (SgAerolinea) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
                .append("{")
                .append("id=").append(this.id)
                .append(", nombre=").append(this.nombre)
                .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
                .append(", fechaGenero=").append(this.fechaGenero != null ? (Constantes.FMT_ddMMyyy.format(this.fechaGenero)) : null)
                .append(", horaGenero=").append(this.horaGenero != null ? (Constantes.FMT_HHmmss.format(this.horaGenero)) : null)
                .append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
                .append(", fechaModifico=").append(this.fechaModifico != null ? (Constantes.FMT_ddMMyyy.format(this.fechaModifico)) : null)
                .append(", horaModifico=").append(this.horaModifico != null ? (Constantes.FMT_HHmmss.format(this.horaModifico)) : null)
                .append(", eliminado=").append(this.eliminado)
                .append("}");

        return sb.toString();
    }
}
