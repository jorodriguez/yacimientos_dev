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
@Table(name = "TIPO_OBRA")
@SequenceGenerator(sequenceName = "tipo_obra_id_seq", name = "tipo_obra_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipoObra.findAll", query = "SELECT t FROM TipoObra t")})
public class TipoObra implements Serializable {
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
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "tipo_obra_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 100)
    @Column(name = "NOMBRE")
    private String nombre;
    
    @Column(name = "VISIBLE")
    private boolean visible;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoObra")
    private Collection<RelProyectoTipoObra> relProyectoTipoObraCollection;
    @OneToMany(mappedBy = "tipoObra")
    private Collection<Requisicion> requisicionCollection;

    public TipoObra() {
    }

    public TipoObra(Integer id) {
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @XmlTransient
    public Collection<RelProyectoTipoObra> getRelProyectoTipoObraCollection() {
        return relProyectoTipoObraCollection;
    }

    public void setRelProyectoTipoObraCollection(Collection<RelProyectoTipoObra> relProyectoTipoObraCollection) {
        this.relProyectoTipoObraCollection = relProyectoTipoObraCollection;
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
        if (!(object instanceof TipoObra)) {
            return false;
        }
        TipoObra other = (TipoObra) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.TipoObra[ id=" + id + " ]";
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

}
