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
 * @author nlopez
 */
@Entity
@Table(name = "SG_VEHICULO_SI_MOVIMIENTO")
@SequenceGenerator(sequenceName = "sg_vehiculo_si_movimiento_id_seq", name = "sg_vehiculo_si_movimiento_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgVehiculoSiMovimiento.findAll", query = "SELECT s FROM SgVehiculoSiMovimiento s")})
public class SgVehiculoSiMovimiento  implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_vehiculo_si_movimiento_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
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
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SI_MOVIMIENTO", referencedColumnName = "ID")
    @ManyToOne
    private SiMovimiento siMovimiento;
    @JoinColumn(name = "SG_VEHICULO", referencedColumnName = "ID")
    @ManyToOne
    private SgVehiculo sgVehiculo;
    @JoinColumn(name = "SG_OFICINA_ACTUAL", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina oficinaOrigen;
    @JoinColumn(name = "SG_OFICINA_CAMBIO", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina oficinaDestino;
    
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
     * @return the siMovimiento
     */
    public SiMovimiento getSiMovimiento() {
        return siMovimiento;
    }

    /**
     * @param siMovimiento the siMovimiento to set
     */
    public void setSiMovimiento(SiMovimiento siMovimiento) {
        this.siMovimiento = siMovimiento;
    }

    /**
     * @return the sgVehiculo
     */
    public SgVehiculo getSgVehiculo() {
        return sgVehiculo;
    }

    /**
     * @param sgVehiculo the sgVehiculo to set
     */
    public void setSgVehiculo(SgVehiculo sgVehiculo) {
        this.sgVehiculo = sgVehiculo;
    }

    /**
     * @return the oficinaOrigen
     */
    public SgOficina getOficinaOrigen() {
        return oficinaOrigen;
    }

    /**
     * @param oficinaOrigen the oficinaOrigen to set
     */
    public void setOficinaOrigen(SgOficina oficinaOrigen) {
        this.oficinaOrigen = oficinaOrigen;
    }

    /**
     * @return the oficinaDestino
     */
    public SgOficina getOficinaDestino() {
        return oficinaDestino;
    }

    /**
     * @param oficinaDestino the oficinaDestino to set
     */
    public void setOficinaDestino(SgOficina oficinaDestino) {
        this.oficinaDestino = oficinaDestino;
    }
    
    
}
