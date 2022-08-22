/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "CO_MIEMBRO")
@SequenceGenerator(sequenceName = "co_miembro_id_seq", name = "co_miembro_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CoMiembro.findAll", query = "SELECT c FROM CoMiembro c")})
public class CoMiembro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "co_miembro_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Column(name = "FECHA_AGREGO")
    @Temporal(TemporalType.DATE)
    private Date fechaAgrego;
    @Column(name = "HORA_AGREGO")
    @Temporal(TemporalType.TIME)
    private Date horaAgrego;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MIEMBRO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario miembro;
    @JoinColumn(name = "CO_GRUPO", referencedColumnName = "ID")
    @ManyToOne
    private CoGrupo coGrupo;

    public CoMiembro() {
    }

    public CoMiembro(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaAgrego() {
        return fechaAgrego;
    }

    public void setFechaAgrego(Date fechaAgrego) {
        this.fechaAgrego = fechaAgrego;
    }

    public Date getHoraAgrego() {
        return horaAgrego;
    }

    public void setHoraAgrego(Date horaAgrego) {
        this.horaAgrego = horaAgrego;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Usuario getMiembro() {
        return miembro;
    }

    public void setMiembro(Usuario miembro) {
        this.miembro = miembro;
    }

    public CoGrupo getCoGrupo() {
        return coGrupo;
    }

    public void setCoGrupo(CoGrupo coGrupo) {
        this.coGrupo = coGrupo;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CoMiembro)) {
            return false;
        }
        CoMiembro other = (CoMiembro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.CoMiembro[ id=" + id + " ]";
    }
    
}
