/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "SG_VEHICULO_MANTENIMIENTO")
@SequenceGenerator(sequenceName = "sg_vehiculo_mantenimiento_id_seq", name = "sg_vehiculo_mantenimiento_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgVehiculoMantenimiento.findAll", query = "SELECT s FROM SgVehiculoMantenimiento s")})
public class SgVehiculoMantenimiento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_vehiculo_mantenimiento_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "IMPORTE")
    private BigDecimal importe;
    @Column(name = "PROX_MANTENIMIENTO_KILOMETRAJE")
    private Integer proxMantenimientoKilometraje;
    @Column(name = "PROX_MANTENIMIENTO_FECHA")
    @Temporal(TemporalType.DATE)
    private Date proxMantenimientoFecha;
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
    @JoinColumn(name = "SI_ADJUNTO", referencedColumnName = "ID")
    @ManyToOne
    private SiAdjunto siAdjunto;
    @JoinColumn(name = "SG_VEHICULO", referencedColumnName = "ID")
    @ManyToOne
    private SgVehiculo sgVehiculo;
    @JoinColumn(name = "SG_KILOMETRAJE", referencedColumnName = "ID")
    @ManyToOne
    private SgKilometraje sgKilometraje;
    @JoinColumn(name = "PROVEEDOR", referencedColumnName = "ID")
    @ManyToOne
    private Proveedor proveedor;
    @JoinColumn(name = "MONEDA", referencedColumnName = "ID")
    @ManyToOne
    private Moneda moneda;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    
    @Column(name = "TERMINADO")
    private boolean terminado;
    @Column(name = "OBSERVACION")
    private String observacion;
    @Column(name = "FECHA_INGRESO")
    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;
    @Column(name = "FECHA_SALIDA")
    @Temporal(TemporalType.DATE)
    private Date fechaSalida;
    
    @Column(name = "ACTUAL")
    private boolean actual;

    public SgVehiculoMantenimiento() {
    }

    public SgVehiculoMantenimiento(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public Integer getProxMantenimientoKilometraje() {
        return proxMantenimientoKilometraje;
    }

    public void setProxMantenimientoKilometraje(Integer proxMantenimientoKilometraje) {
        this.proxMantenimientoKilometraje = proxMantenimientoKilometraje;
    }

    public Date getProxMantenimientoFecha() {
        return proxMantenimientoFecha;
    }

    public void setProxMantenimientoFecha(Date proxMantenimientoFecha) {
        this.proxMantenimientoFecha = proxMantenimientoFecha;
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

    public SiAdjunto getSiAdjunto() {
        return siAdjunto;
    }

    public void setSiAdjunto(SiAdjunto siAdjunto) {
        this.siAdjunto = siAdjunto;
    }

    public SgVehiculo getSgVehiculo() {
        return sgVehiculo;
    }

    public void setSgVehiculo(SgVehiculo sgVehiculo) {
        this.sgVehiculo = sgVehiculo;
    }

    public SgKilometraje getSgKilometraje() {
        return sgKilometraje;
    }

    public void setSgKilometraje(SgKilometraje sgKilometraje) {
        this.sgKilometraje = sgKilometraje;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
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
     * @return the terminado
     */
    public boolean isTerminado() {
        return terminado;
    }

    /**
     * @param terminado the terminado to set
     */
    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
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

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgVehiculoMantenimiento)) {
            return false;
        }
        SgVehiculoMantenimiento other = (SgVehiculoMantenimiento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName()).append("{").append(" id=").append(this.id).append(", importe=").append(this.importe).append(", proxMantenimientoKilometraje=").append(this.proxMantenimientoKilometraje).append(", proxMantenimientoFecha=").append(this.proxMantenimientoFecha).append(", actual=").append(this.actual).append(", eliminado=").append(this.eliminado).append(", siAdjunto=").append(this.siAdjunto != null ? this.siAdjunto.getId() : null).append(", sgVehiculo=").append(this.sgVehiculo != null ? this.sgVehiculo.getId() : null).append(", sgKilometraje=").append(this.sgKilometraje != null ? this.sgKilometraje.getId() : null).append(", proveedor=").append(this.proveedor != null ? this.proveedor.getId() : null).append(", moneda=").append(this.moneda != null ? this.moneda.getId() : null).append(", genero=").append(this.genero != null ? this.genero.getId() : null).append(", fechaGenero=").append(this.fechaGenero != null ? (sdfFecha.format(this.fechaGenero)) : null).append(", horaGenero=").append(this.horaGenero != null ? (sdfHora.format(this.horaGenero)) : null).append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null).append(", fechaModifico=").append(this.fechaModifico != null ? (sdfFecha.format(this.fechaModifico)) : null).append(", horaModifico=").append(this.horaModifico != null ? (sdfHora.format(this.horaModifico)) : null).append("}");

        return sb.toString();
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
     * @return the actual
     */
    public boolean isActual() {
        return actual;
    }

    /**
     * @param actual the actual to set
     */
    public void setActual(boolean actual) {
        this.actual = actual;
    }
}
