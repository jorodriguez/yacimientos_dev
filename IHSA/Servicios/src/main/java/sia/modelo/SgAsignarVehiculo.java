/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_ASIGNAR_VEHICULO")
@SequenceGenerator(sequenceName = "sg_asignar_vehiculo_id_seq", name = "sg_asignar_vehiculo_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgAsignarVehiculo.findAll", query = "SELECT s FROM SgAsignarVehiculo s")})
public class SgAsignarVehiculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sg_asignar_vehiculo_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Size(max = 1024)
    @Column(name = "OBSERVACION")
    private String observacion;
    @Column(name = "FECHA_OPERACION")
    @Temporal(TemporalType.DATE)
    private Date fechaOperacion;
    @Column(name = "HORA_OPERACION")
    @Temporal(TemporalType.TIME)
    private Date horaOperacion;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;

    @Column(name = "ELIMINADO")
    private boolean eliminado;

    @Column(name = "TERMINADA")
    private boolean terminada;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @Column(name = "PERTENECE")
    private Integer pertenece;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "SI_ADJUNTO", referencedColumnName = "ID")
    @ManyToOne
    private SiAdjunto siAdjunto;
    @JoinColumn(name = "SG_VEHICULO", referencedColumnName = "ID")
    @ManyToOne
    private SgVehiculo sgVehiculo;
    @JoinColumn(name = "SI_OPERACION", referencedColumnName = "ID")
    @ManyToOne
    private SiOperacion siOperacion;
    @JoinColumn(name = "SG_CHECKLIST", referencedColumnName = "ID")
    @ManyToOne
    private SgChecklist sgChecklist;

    public SgAsignarVehiculo() {
    }

    public SgAsignarVehiculo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Date getFechaOperacion() {
        return fechaOperacion;
    }

    public void setFechaOperacion(Date fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public Date getHoraOperacion() {
        return horaOperacion;
    }

    public void setHoraOperacion(Date horaOperacion) {
        this.horaOperacion = horaOperacion;
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

    public Date getFechaModifico() {
        return fechaModifico;
    }

    public void setFechaModifico(Date fechaModifico) {
        this.fechaModifico = fechaModifico;
    }

    public Date getHoraModifico() {
        return horaModifico;
    }

    public void setHoraModifico(Date horaModifico) {
        this.horaModifico = horaModifico;
    }

    public Usuario getModifico() {
        return modifico;
    }

    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public SgChecklist getSgChecklist() {
        return sgChecklist;
    }

    public void setSgChecklist(SgChecklist sgChecklist) {
        this.sgChecklist = sgChecklist;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgAsignarVehiculo)) {
            return false;
        }
        SgAsignarVehiculo other = (SgAsignarVehiculo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
                .append("{").append("id=").append(this.id)
                .append(", usuario=").append(this.usuario.getId())
                .append(", sgVehiculo=").append(this.sgVehiculo.getId())
                .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
                .append(", fechaGenero=").append(this.fechaGenero != null ? (sdfFecha.format(this.fechaGenero)) : null)
                .append(", horaGenero=").append(this.horaGenero != null ? (sdfHora.format(this.horaGenero)) : null)
                .append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
                .append(", fechaModifico=").append(this.fechaModifico != null ? (sdfFecha.format(this.fechaModifico)) : null)
                .append(", horaModifico=").append(this.horaModifico != null ? (sdfHora.format(this.horaModifico)) : null)
                .append(", eliminado=").append(this.eliminado)
                .append(", sgChecklist=").append(this.sgChecklist != null ? sgChecklist.getId() : null)
                .append(", siAdjunto=").append(this.siAdjunto != null ? this.siAdjunto.getId() : null)
                .append(", siOperacion=").append(this.getSiOperacion() != null ? getSiOperacion().getId() : null)
                .append(", fechaOperacion=").append(this.fechaOperacion != null ? fechaOperacion : null)
                .append("}");

        return sb.toString();
    }

    /**
     * @return the terminada
     */
    public boolean isTerminada() {
        return terminada;
    }

    /**
     * @param terminada the terminada to set
     */
    public void setTerminada(boolean terminada) {
        this.terminada = terminada;
    }

    /**
     * @return the pertenece
     */
    public Integer getPertenece() {
        return pertenece;
    }

    /**
     * @param pertenece the pertenece to set
     */
    public void setPertenece(Integer pertenece) {
        this.pertenece = pertenece;
    }

    /**
     * @return the siOperacion
     */
    public SiOperacion getSiOperacion() {
        return siOperacion;
    }

    /**
     * @param siOperacion the siOperacion to set
     */
    public void setSiOperacion(SiOperacion siOperacion) {
        this.siOperacion = siOperacion;
    }

}
