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
 * @author mluis
 */
@Entity
@Table(name = "OC_GERENCIA_TAREA")
@SequenceGenerator(sequenceName = "oc_gerencia_tarea_id_seq", name = "oc_gerencia_tarea_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "OcGerenciaTarea.findAll", query = "SELECT a FROM OcGerenciaTarea a")})
@Getter
@Setter
public class OcGerenciaTarea implements Serializable {

    @JoinColumn(name = "OC_SUBCAMPO", referencedColumnName = "ID")
    @ManyToOne
    private OcSubcampo ocSubcampo;

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
     private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @JoinColumn(name = "GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private Gerencia gerencia;
    //
    
    @JoinColumn(name = "OC_NOMBRE_TAREA", referencedColumnName = "ID")
    @ManyToOne
    private OcNombreTarea ocNombreTarea;
    //    
    @JoinColumn(name = "OC_TAREA_TRABAJO", referencedColumnName = "ID")
    @ManyToOne
    private OcTareaTrabajo ocTareaTrabajo;
    //
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;
    //
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;

    public OcGerenciaTarea() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OcGerenciaTarea)) {
            return false;
        }
        OcGerenciaTarea other = (OcGerenciaTarea) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public OcSubcampo getOcSubcampo() {
        return ocSubcampo;
    }

    public void setOcSubcampo(OcSubcampo ocSubcampo) {
        this.ocSubcampo = ocSubcampo;
    }

}
