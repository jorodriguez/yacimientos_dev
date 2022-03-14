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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "ESTATUS")
@SequenceGenerator(sequenceName = "estatus_id_seq", name = "estatus_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Estatus.findAll", query = "SELECT e FROM Estatus e")})
public class Estatus implements Serializable {
    @Column(name =     "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name =     "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name =     "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name =     "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "estatus")
    private Collection<OfOficio> ofOficioCollection;
    @OneToMany(mappedBy = "estatus")
    private Collection<SgEstatusAlterno> sgEstatusAlternoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "estatus")
    private Collection<SgPaquete> sgPaqueteCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "estatus")
    private Collection<SgSolicitudViaje> sgSolicitudViajeCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "estatus")
    private Collection<SgSolicitudPaqueteria> sgSolicitudPaqueteriaCollection;
    @OneToMany(mappedBy = "estatus")
    private Collection<SgCadenaNegacion> sgCadenaNegacionCollection;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @OneToMany(mappedBy = "estatus")
    private Collection<SgEstatusAprobacion> sgEstatusAprobacionCollection;
    @OneToMany(mappedBy = "estatus")
    private Collection<SgViaje> sgViajeCollection;
    @OneToMany(mappedBy = "estatus")
    private Collection<SgCadenaAprobacion> sgCadenaAprobacionCollection;
   @OneToMany(mappedBy = "estatus")
    private Collection<SgIncidencia> sgIncidenciaCollection;
    @OneToMany(mappedBy = "estatus")
    private Collection<SgSolicitudEstancia> sgSolicitudEstanciaCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 50)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 3)
    @Column(name = "TIPO")
    private String tipo;
    @OneToMany(mappedBy = "estatus")
    private Collection<Convenio> convenioCollection;
    @OneToMany(mappedBy = "estatus")
    private Collection<AutorizacionesOrden> autorizacionesOrdenCollection;
    @OneToMany(mappedBy = "estatus")
    private Collection<Requisicion> requisicionCollection;

    public Estatus() {
    }

    public Estatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @XmlTransient
    public Collection<Convenio> getConvenioCollection() {
        return convenioCollection;
    }

    public void setConvenioCollection(Collection<Convenio> convenioCollection) {
        this.convenioCollection = convenioCollection;
    }

    @XmlTransient
    public Collection<AutorizacionesOrden> getAutorizacionesOrdenCollection() {
        return autorizacionesOrdenCollection;
    }

    public void setAutorizacionesOrdenCollection(Collection<AutorizacionesOrden> autorizacionesOrdenCollection) {
        this.autorizacionesOrdenCollection = autorizacionesOrdenCollection;
    }

    @XmlTransient
    public Collection<Requisicion> getRequisicionCollection() {
        return requisicionCollection;
    }

    public void setRequisicionCollection(Collection<Requisicion> requisicionCollection) {
        this.requisicionCollection = requisicionCollection;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Estatus)) {
            return false;
        }
        Estatus other = (Estatus) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * 
     * @return 
     */
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("[id=").append(id);
        sb.append(",nombre=").append(nombre);
        sb.append("]");
        
        return sb.toString();
    }
    public Collection<SgIncidencia> getSgIncidenciaCollection() {
        return sgIncidenciaCollection;
    }

    public void setSgIncidenciaCollection(Collection<SgIncidencia> sgIncidenciaCollection) {
        this.sgIncidenciaCollection = sgIncidenciaCollection;
    }

    public Collection<SgSolicitudEstancia> getSgSolicitudEstanciaCollection() {
        return sgSolicitudEstanciaCollection;
    }

    public void setSgSolicitudEstanciaCollection(Collection<SgSolicitudEstancia> sgSolicitudEstanciaCollection) {
        this.sgSolicitudEstanciaCollection = sgSolicitudEstanciaCollection;
    }

    public Collection<SgEstatusAprobacion> getSgEstatusAprobacionCollection() {
        return sgEstatusAprobacionCollection;
    }

    public void setSgEstatusAprobacionCollection(Collection<SgEstatusAprobacion> sgEstatusAprobacionCollection) {
        this.sgEstatusAprobacionCollection = sgEstatusAprobacionCollection;
    }

    public Collection<SgViaje> getSgViajeCollection() {
        return sgViajeCollection;
    }

    public void setSgViajeCollection(Collection<SgViaje> sgViajeCollection) {
        this.sgViajeCollection = sgViajeCollection;
    }

    public Collection<SgCadenaAprobacion> getSgCadenaAprobacionCollection() {
        return sgCadenaAprobacionCollection;
    }

    public void setSgCadenaAprobacionCollection(Collection<SgCadenaAprobacion> sgCadenaAprobacionCollection) {
        this.sgCadenaAprobacionCollection = sgCadenaAprobacionCollection;
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

    public Collection<SgPaquete> getSgPaqueteCollection() {
        return sgPaqueteCollection;
    }

    public void setSgPaqueteCollection(Collection<SgPaquete> sgPaqueteCollection) {
        this.sgPaqueteCollection = sgPaqueteCollection;
    }

    public Collection<SgSolicitudViaje> getSgSolicitudViajeCollection() {
        return sgSolicitudViajeCollection;
    }

    public void setSgSolicitudViajeCollection(Collection<SgSolicitudViaje> sgSolicitudViajeCollection) {
        this.sgSolicitudViajeCollection = sgSolicitudViajeCollection;
    }

    public Collection<SgSolicitudPaqueteria> getSgSolicitudPaqueteriaCollection() {
        return sgSolicitudPaqueteriaCollection;
    }

    public void setSgSolicitudPaqueteriaCollection(Collection<SgSolicitudPaqueteria> sgSolicitudPaqueteriaCollection) {
        this.sgSolicitudPaqueteriaCollection = sgSolicitudPaqueteriaCollection;
    }

    public Collection<SgCadenaNegacion> getSgCadenaNegacionCollection() {
        return sgCadenaNegacionCollection;
    }

    public void setSgCadenaNegacionCollection(Collection<SgCadenaNegacion> sgCadenaNegacionCollection) {
        this.sgCadenaNegacionCollection = sgCadenaNegacionCollection;
    }



    public Collection<SgEstatusAlterno> getSgEstatusAlternoCollection() {
        return sgEstatusAlternoCollection;
    }

    public void setSgEstatusAlternoCollection(Collection<SgEstatusAlterno> sgEstatusAlternoCollection) {
        this.sgEstatusAlternoCollection = sgEstatusAlternoCollection;
    }

    public Collection<OfOficio> getOfOficioCollection() {
        return ofOficioCollection;
    }

    public void setOfOficioCollection(Collection<OfOficio> ofOficioCollection) {
        this.ofOficioCollection = ofOficioCollection;
    }
}
