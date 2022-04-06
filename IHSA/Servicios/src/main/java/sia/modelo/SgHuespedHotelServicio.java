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
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_HUESPED_HOTEL_SERVICIO")
@SequenceGenerator(sequenceName = "sg_huesped_hotel_servicio_id_seq", name = "sg_huesped_hotel_servicio_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgHuespedHotelServicio.findAll", query = "SELECT s FROM SgHuespedHotelServicio s")})
public class SgHuespedHotelServicio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_huesped_hotel_servicio_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "FACTURA_EMPRESA")
    private boolean facturaEmpresa;        
    @JoinColumn(name = "SG_HUESPED_HOTEL", referencedColumnName = "ID")
    @ManyToOne
    private SgHuespedHotel sgHuespedHotel;
    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;
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

    public SgHuespedHotelServicio() {
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
     * @return the facturaEmpresa
     */
    public boolean isFacturaEmpresa() {
        return facturaEmpresa;
    }

    /**
     * @param facturaEmpresa the facturaEmpresa to set
     */
    public void setFacturaEmpresa(boolean facturaEmpresa) {
        this.facturaEmpresa = facturaEmpresa;
    }

    /**
     * @return the sgHuespedHotel
     */
    public SgHuespedHotel getSgHuespedHotel() {
        return sgHuespedHotel;
    }

    /**
     * @param sgHuespedHotel the sgHuespedHotel to set
     */
    public void setSgHuespedHotel(SgHuespedHotel sgHuespedHotel) {
        this.sgHuespedHotel = sgHuespedHotel;
    }

    /**
     * @return the sgTipoEspecifico
     */
    public SgTipoEspecifico getSgTipoEspecifico() {
        return sgTipoEspecifico;
    }

    /**
     * @param sgTipoEspecifico the sgTipoEspecifico to set
     */
    public void setSgTipoEspecifico(SgTipoEspecifico sgTipoEspecifico) {
        this.sgTipoEspecifico = sgTipoEspecifico;
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
        if (!(object instanceof SgHuespedHotelServicio)) {
            return false;
        }
        SgHuespedHotelServicio other = (SgHuespedHotelServicio) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
            .append("id=").append(getId())
            .append(", facturaEmpresa=").append(isFacturaEmpresa())
            .append(", sgHuespedHotel=").append(getSgHuespedHotel() != null ? getSgHuespedHotel().getId() : null)
            .append(", sgTipoEspecifico=").append(getSgTipoEspecifico() != null ? getSgTipoEspecifico().getId() : null)
            .append(", genero=").append(getGenero() != null ? getGenero().getId() : null)
            .append(", fechaGenero=").append(getFechaGenero() != null ? (Constantes.FMT_ddMMyyy.format(getFechaGenero())) : null)
            .append(", horaGenero=").append(getHoraGenero() != null ? (Constantes.FMT_HHmmss.format(getHoraGenero())) : null)
            .append(", modifico=").append(getModifico() != null ? getModifico().getId() : null)
            .append(", fechaModifico=").append(getFechaModifico() != null ? (Constantes.FMT_ddMMyyy.format(getFechaModifico())) : null)
            .append(", horaModifico=").append(getHoraModifico() != null ? (Constantes.FMT_HHmmss.format(getHoraModifico())) : null)
            .append(", eliminado=").append(isEliminado())
            .append("}");
        return sb.toString();
    }    
}
