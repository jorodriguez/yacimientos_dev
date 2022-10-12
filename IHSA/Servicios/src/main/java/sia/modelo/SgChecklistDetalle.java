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
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_CHECKLIST_DETALLE")
@SequenceGenerator(sequenceName = "sg_checklist_detalle_id_seq", name = "sg_checklist_detalle_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgChecklistDetalle.findAll", query = "SELECT s FROM SgChecklistDetalle s")})
@Getter
@Setter
public class SgChecklistDetalle implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_checklist_detalle_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "ESTADO")
    private boolean estado;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @Size(max = 1024)
    @Column(name = "OBSERVACION")
    private String observacion;
    @JoinColumn(name = "SG_CARACTERISTICA", referencedColumnName = "ID")
    @ManyToOne
    private SgCaracteristica sgCaracteristica;
    @JoinColumn(name = "SG_CHECKLIST", referencedColumnName = "ID")
    @ManyToOne
    private SgChecklist sgChecklist;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;

    public SgChecklistDetalle() {
    }

    public SgChecklistDetalle(Integer id) {
        this.id = id;
    }

     

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgChecklistDetalle)) {
            return false;
        }
        SgChecklistDetalle other = (SgChecklistDetalle) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "SgChecklistDetalle{" + "id=" + id 
                + ", estado=" + estado 
                + ", eliminado=" + eliminado 
                + ", observacion=" + observacion 
                + ", sgCaracteristica=" + (sgCaracteristica != null ? sgCaracteristica.getId() : null)
                + ", sgChecklist=" + (sgChecklist != null ? sgChecklist.getId() : null )
                + ", genero=" + genero + '}';
    }

}
