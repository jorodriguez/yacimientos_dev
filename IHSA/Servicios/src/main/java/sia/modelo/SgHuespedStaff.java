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
@Table(name = "SG_HUESPED_STAFF")
@SequenceGenerator(sequenceName = "sg_huesped_staff_id_seq", name = "sg_huesped_staff_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgHuespedStaff.findAll", query = "SELECT s FROM SgHuespedStaff s")})
public class SgHuespedStaff implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_huesped_staff_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "HOSPEDADO")
    private boolean hospedado;
    
    @Column(name = "CANCELADO")
    private boolean cancelado;
    @Column(name = "FECHA_INGRESO")
    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;
    @Column(name = "FECHA_SALIDA")
    @Temporal(TemporalType.DATE)
    private Date fechaSalida;    
    @Column(name = "FECHA_REAL_INGRESO")
    @Temporal(TemporalType.DATE)
    private Date fechaRealIngreso;
    @Column(name = "FECHA_REAL_SALIDA")
    @Temporal(TemporalType.DATE)
    private Date fechaRealSalida;
    @JoinColumn(name = "SG_TIPO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipo sgTipo;
    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;    
    @JoinColumn(name = "SG_STAFF_HABITACION", referencedColumnName = "ID")
    @ManyToOne
    private SgStaffHabitacion sgStaffHabitacion;    
    @JoinColumn(name = "SG_SOLICITUD_ESTANCIA", referencedColumnName = "ID")
    @ManyToOne
    private SgSolicitudEstancia sgSolicitudEstancia;    
    @JoinColumn(name = "SG_DETALLE_SOLICITUD_ESTANCIA", referencedColumnName = "ID")
    @ManyToOne
    private SgDetalleSolicitudEstancia sgDetalleSolicitudEstancia;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;     
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;      
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
    
    @Column(name = "prolongada")
    private boolean prolongada;    

    public SgHuespedStaff() {
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
     * @return the hospedado
     */
    public boolean isHospedado() {
        return hospedado;
    }

    /**
     * @param hospedado the hospedado to set
     */
    public void setHospedado(boolean hospedado) {
        this.hospedado = hospedado;
    }

    /**
     * @return the cancelado
     */
    public boolean isCancelado() {
        return cancelado;
    }

    /**
     * @param cancelado the cancelado to set
     */
    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    /**
     * @return the fechaIngreso
     */
    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    /**
     * @param fechaIngreso the fechaIngreso to set
     */
    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
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
     * @return the fechaRealIngreso
     */
    public Date getFechaRealIngreso() {
        return fechaRealIngreso;
    }

    /**
     * @param fechaRealIngreso the fechaRealIngreso to set
     */
    public void setFechaRealIngreso(Date fechaRealIngreso) {
        this.fechaRealIngreso = fechaRealIngreso;
    }

    /**
     * @return the fechaRealSalida
     */
    public Date getFechaRealSalida() {
        return fechaRealSalida;
    }

    /**
     * @param fechaRealSalida the fechaRealSalida to set
     */
    public void setFechaRealSalida(Date fechaRealSalida) {
        this.fechaRealSalida = fechaRealSalida;
    }

    /**
     * @return the sgTipo
     */
    public SgTipo getSgTipo() {
        return sgTipo;
    }

    /**
     * @param sgTipo the sgTipo to set
     */
    public void setSgTipo(SgTipo sgTipo) {
        this.sgTipo = sgTipo;
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
     * @return the sgStaffHabitacion
     */
    public SgStaffHabitacion getSgStaffHabitacion() {
        return sgStaffHabitacion;
    }

    /**
     * @param sgStaffHabitacion the sgStaffHabitacion to set
     */
    public void setSgStaffHabitacion(SgStaffHabitacion sgStaffHabitacion) {
        this.sgStaffHabitacion = sgStaffHabitacion;
    }

    /**
     * @return the sgSolicitudEstancia
     */
    public SgSolicitudEstancia getSgSolicitudEstancia() {
        return sgSolicitudEstancia;
    }

    /**
     * @param sgSolicitudEstancia the sgSolicitudEstancia to set
     */
    public void setSgSolicitudEstancia(SgSolicitudEstancia sgSolicitudEstancia) {
        this.sgSolicitudEstancia = sgSolicitudEstancia;
    }

    /**
     * @return the sgDetalleSolicitudEstancia
     */
    public SgDetalleSolicitudEstancia getSgDetalleSolicitudEstancia() {
        return sgDetalleSolicitudEstancia;
    }

    /**
     * @param sgDetalleSolicitudEstancia the sgDetalleSolicitudEstancia to set
     */
    public void setSgDetalleSolicitudEstancia(SgDetalleSolicitudEstancia sgDetalleSolicitudEstancia) {
        this.sgDetalleSolicitudEstancia = sgDetalleSolicitudEstancia;
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
        if (!(object instanceof SgHuespedStaff)) {
            return false;
        }
        SgHuespedStaff other = (SgHuespedStaff) object;
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
            .append(", hospedado=").append(this.hospedado)
            .append(", cancelado=").append(this.cancelado)
            .append(", fechaIngreso=").append(this.fechaIngreso)
            .append(", fechaSalida=").append(this.fechaSalida)
            .append(", fechaRealIngreso=").append(this.fechaRealIngreso)
            .append(", fechaRealSalida=").append(this.fechaRealSalida)
            .append(", sgTipo=").append(this.sgTipo != null ? this.sgTipo.getId() : null)
            .append(", sgTipoEspecifico=").append(this.sgTipoEspecifico != null ? this.sgTipoEspecifico.getId() : null)
            .append(", sgHotelHabitacion=").append(this.sgStaffHabitacion != null ? this.sgStaffHabitacion.getId() : null)
            .append(", sgSolicitudEstancia=").append(this.sgSolicitudEstancia != null ? this.sgSolicitudEstancia.getId() : null)
            .append(", sgDetalleSolicitudEstancia=").append(this.sgDetalleSolicitudEstancia != null ? this.sgDetalleSolicitudEstancia.getId() : null)                
            .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
            .append(", fechaGenero=").append(this.fechaGenero != null ? (sdfd.format(this.fechaGenero)) : null)
            .append(", horaGenero=").append(this.horaGenero != null ? (sdft.format(this.horaGenero)) : null)
            .append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
            .append(", fechaModifico=").append(this.fechaModifico != null ? (sdfd.format(this.fechaModifico)) : null)
            .append(", horaModifico=").append(this.horaModifico != null ? (sdft.format(this.horaModifico)) : null)
            .append(", eliminado=").append(this.eliminado)
            .append(", prolongada=").append(this.prolongada)
            .append("}");
        
        return sb.toString();
    }

    /**
     * @return the prolongada
     */
    public boolean isProlongada() {
        return prolongada;
    }

    /**
     * @param prolongada the prolongada to set
     */
    public void setProlongada(boolean prolongada) {
        this.prolongada = prolongada;
    }
}