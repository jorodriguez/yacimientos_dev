/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_PAQUETE")
@SequenceGenerator(sequenceName = "sg_paquete_id_seq", name = "sg_paquete_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgPaquete.findAll", query = "SELECT s FROM SgPaquete s")})
public class SgPaquete implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 20)
    @Column(name = "DESTINATARIO")
    private String destinatario;
    @Size(max = 128)
    @Column(name = "OBSERVACION_ENVIO")
    private String observacionEnvio;
    @Size(max = 128)
    @Column(name = "OBSERVACION_ENTREGA")
    private String observacionEntrega;
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
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "SG_VIAJE", referencedColumnName = "ID")
    @ManyToOne
    private SgViaje sgViaje;
    @JoinColumn(name = "SG_SOLICITUD_PAQUETERIA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgSolicitudPaqueteria sgSolicitudPaqueteria;
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Estatus estatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sgPaquete")
    private Collection<SgPaqueteSiMovimiento> sgPaqueteSiMovimientoCollection;

    public SgPaquete() {
    }

    public SgPaquete(Integer id) {
        this.id = id;
    }

    public SgPaquete(Integer id, Date fechaGenero, Date horaGenero) {
        this.id = id;
        this.fechaGenero = fechaGenero;
        this.horaGenero = horaGenero;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getObservacionEnvio() {
        return observacionEnvio;
    }

    public void setObservacionEnvio(String observacionEnvio) {
        this.observacionEnvio = observacionEnvio;
    }

    public String getObservacionEntrega() {
        return observacionEntrega;
    }

    public void setObservacionEntrega(String observacionEntrega) {
        this.observacionEntrega = observacionEntrega;
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

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
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

    public SgViaje getSgViaje() {
        return sgViaje;
    }

    public void setSgViaje(SgViaje sgViaje) {
        this.sgViaje = sgViaje;
    }

    public SgSolicitudPaqueteria getSgSolicitudPaqueteria() {
        return sgSolicitudPaqueteria;
    }

    public void setSgSolicitudPaqueteria(SgSolicitudPaqueteria sgSolicitudPaqueteria) {
        this.sgSolicitudPaqueteria = sgSolicitudPaqueteria;
    }

    public Estatus getEstatus() {
        return estatus;
    }

    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    public Collection<SgPaqueteSiMovimiento> getSgPaqueteSiMovimientoCollection() {
        return sgPaqueteSiMovimientoCollection;
    }

    public void setSgPaqueteSiMovimientoCollection(Collection<SgPaqueteSiMovimiento> sgPaqueteSiMovimientoCollection) {
        this.sgPaqueteSiMovimientoCollection = sgPaqueteSiMovimientoCollection;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgPaquete)) {
            return false;
        }
        SgPaquete other = (SgPaquete) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SgPaquete[ id=" + id + " ]";
    }
    
}
