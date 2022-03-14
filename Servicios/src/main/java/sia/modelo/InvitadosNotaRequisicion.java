/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "INVITADOS_NOTA_REQUISICION")
@SequenceGenerator(sequenceName = "invitados_nota_requisicion_id_seq", name = "invitados_nota_requisicion_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InvitadosNotaRequisicion.findAll", query = "SELECT i FROM InvitadosNotaRequisicion i")})
public class InvitadosNotaRequisicion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "invitados_nota_requisicion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "INVITADO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario invitado;
    @JoinColumn(name = "NOTA_REQUISICION", referencedColumnName = "ID")
    @ManyToOne
    private NotaRequisicion notaRequisicion;

    public InvitadosNotaRequisicion() {
    }

    public InvitadosNotaRequisicion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getInvitado() {
        return invitado;
    }

    public void setInvitado(Usuario invitado) {
        this.invitado = invitado;
    }

    public NotaRequisicion getNotaRequisicion() {
        return notaRequisicion;
    }

    public void setNotaRequisicion(NotaRequisicion notaRequisicion) {
        this.notaRequisicion = notaRequisicion;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvitadosNotaRequisicion)) {
            return false;
        }
        InvitadosNotaRequisicion other = (InvitadosNotaRequisicion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.InvitadosNotaRequisicion[ id=" + id + " ]";
    }
    
}
