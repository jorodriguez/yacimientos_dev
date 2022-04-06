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
@Table(name = "SG_SOLICITUD_PAQUETERIA")
@SequenceGenerator(sequenceName = "sg_solicitud_paqueteria_id_seq", name = "sg_solicitud_paqueteria_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgSolicitudPaqueteria.findAll", query = "SELECT s FROM SgSolicitudPaqueteria s")})
public class SgSolicitudPaqueteria implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 12)
    @Column(name = "CODIGO")
    private String codigo;
    @Size(max = 256)
    @Column(name = "OBSERVACION")
    private String observacion;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sgSolicitudPaqueteria")
    private Collection<SgPaquete> sgPaqueteCollection;
    @JoinColumn(name = "REMITENTE", referencedColumnName = "ID")
    @ManyToOne
    private Usuario remitente;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "OFICINA_DESTINO", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina oficinaDestino;
    @JoinColumn(name = "OFICINA_ORIGEN", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgOficina oficinaOrigen;
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Estatus estatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sgSolicitudPaqueteria")
    private Collection<SgSolPaqueteSiMov> sgSolPaqueteSiMovCollection;

    public SgSolicitudPaqueteria() {
    }

    public SgSolicitudPaqueteria(Integer id) {
        this.id = id;
    }

    public SgSolicitudPaqueteria(Integer id, Date fechaGenero, Date horaGenero) {
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public Collection<SgPaquete> getSgPaqueteCollection() {
        return sgPaqueteCollection;
    }

    public void setSgPaqueteCollection(Collection<SgPaquete> sgPaqueteCollection) {
        this.sgPaqueteCollection = sgPaqueteCollection;
    }

    public Usuario getRemitente() {
        return remitente;
    }

    public void setRemitente(Usuario remitente) {
        this.remitente = remitente;
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

    public SgOficina getOficinaDestino() {
        return oficinaDestino;
    }

    public void setOficinaDestino(SgOficina oficinaDestino) {
        this.oficinaDestino = oficinaDestino;
    }

    public SgOficina getOficinaOrigen() {
        return oficinaOrigen;
    }

    public void setOficinaOrigen(SgOficina oficinaOrigen) {
        this.oficinaOrigen = oficinaOrigen;
    }

    public Estatus getEstatus() {
        return estatus;
    }

    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    public Collection<SgSolPaqueteSiMov> getSgSolPaqueteSiMovCollection() {
        return sgSolPaqueteSiMovCollection;
    }

    public void setSgSolPaqueteSiMovCollection(Collection<SgSolPaqueteSiMov> sgSolPaqueteSiMovCollection) {
        this.sgSolPaqueteSiMovCollection = sgSolPaqueteSiMovCollection;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgSolicitudPaqueteria)) {
            return false;
        }
        SgSolicitudPaqueteria other = (SgSolicitudPaqueteria) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SgSolicitudPaqueteria[ id=" + id + " ]";
    }
    
}
