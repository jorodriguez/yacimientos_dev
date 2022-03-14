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
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_INCIDENCIA")
@SequenceGenerator(sequenceName = "sg_incidencia_id_seq", name = "sg_incidencia_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgIncidencia.findAll", query = "SELECT s FROM SgIncidencia s")})
public class SgIncidencia implements Serializable {
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;
    @JoinColumn(name = "SG_TIPO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipo sgTipo;
    @JoinColumn(name = "PRIORIDAD", referencedColumnName = "ID")
    @ManyToOne
    private Prioridad prioridad;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 2048)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @OneToMany(mappedBy = "sgIncidencia")
    private Collection<SgTag> sgTagCollection;
    @OneToMany(mappedBy = "sgIncidencia")
    private Collection<SgEvento> sgEventoCollection;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SG_OFICINA", referencedColumnName = "ID")
    @ManyToOne
    private SgOficina sgOficina;
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatus;

    public SgIncidencia() {
    }

    public SgIncidencia(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public Collection<SgTag> getSgTagCollection() {
        return sgTagCollection;
    }

    public void setSgTagCollection(Collection<SgTag> sgTagCollection) {
        this.sgTagCollection = sgTagCollection;
    }

    public Collection<SgEvento> getSgEventoCollection() {
        return sgEventoCollection;
    }

    public void setSgEventoCollection(Collection<SgEvento> sgEventoCollection) {
        this.sgEventoCollection = sgEventoCollection;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public SgOficina getSgOficina() {
        return sgOficina;
    }

    public void setSgOficina(SgOficina sgOficina) {
        this.sgOficina = sgOficina;
    }

    public Estatus getEstatus() {
        return estatus;
    }

    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgIncidencia)) {
            return false;
        }
        SgIncidencia other = (SgIncidencia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SgIncidencia[ id=" + id + " ]";
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

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }
    
}
