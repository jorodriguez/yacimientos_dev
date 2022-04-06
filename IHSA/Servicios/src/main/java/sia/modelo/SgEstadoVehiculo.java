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
 * @author mluis
 */
@Entity
@Table(name = "SG_ESTADO_VEHICULO")
@SequenceGenerator(sequenceName = "sg_estado_vehiculo_id_seq", name = "sg_estado_vehiculo_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgEstadoVehiculo.findAll", query = "SELECT s FROM SgEstadoVehiculo s")})
public class SgEstadoVehiculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_estado_vehiculo_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "ACTIVO")
    private boolean activo;
    @Size(max = 512)
    @Column(name = "OBSERVACION")
    private String observacion;
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
    @JoinColumn(name = "SG_VEHICULO", referencedColumnName = "ID")
    @ManyToOne
    private SgVehiculo sgVehiculo;
    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;
    @JoinColumn(name = "SG_TIPO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipo sgTipo;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;

    public SgEstadoVehiculo() {
    }

    public SgEstadoVehiculo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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

    public SgVehiculo getSgVehiculo() {
        return sgVehiculo;
    }

    public void setSgVehiculo(SgVehiculo sgVehiculo) {
        this.sgVehiculo = sgVehiculo;
    }

    public SgTipoEspecifico getSgTipoEspecifico() {
        return sgTipoEspecifico;
    }

    public void setSgTipoEspecifico(SgTipoEspecifico sgTipoEspecifico) {
        this.sgTipoEspecifico = sgTipoEspecifico;
    }

    public SgTipo getSgTipo() {
        return sgTipo;
    }

    public void setSgTipo(SgTipo sgTipo) {
        this.sgTipo = sgTipo;
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

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgEstadoVehiculo)) {
            return false;
        }
        SgEstadoVehiculo other = (SgEstadoVehiculo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "SgEstadoVehiculo{" + "id=" + this.getId() +
                ", activo=" + this.isActivo() + 
                ", observacion=" + this.getObservacion() + 
                ", fechaGenero=" + this.getFechaGenero() + 
                ", horaGenero=" + this.getHoraGenero() + 
                ", eliminado=" + this.isEliminado() + 
                ", genero=" + (this.getGenero()!= null ? this.getGenero().getId() : null)+ 
                ", sgVehiculo=" +(this.getSgVehiculo() != null ? this.getSgVehiculo().getId() : null) + 
                ", sgTipoEspecifico=" + (this.getSgTipoEspecifico()!= null ? this.getSgTipoEspecifico().getId():null)  + 
                ", sgTipo=" + (this.getSgTipo() != null ? this.getSgTipo().getId():null) + 
                ", modifico=" + (this.getModifico()!=null ? this.getModifico().getId():null) +
                ", fechaModifico=" + this.getFechaModifico() +
                ", horaModifico=" + this.getHoraModifico() +
                '}';
    }

    
}
