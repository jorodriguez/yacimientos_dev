/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_SEMAFORO")
@SequenceGenerator(sequenceName = "sg_semaforo_id_seq", name = "sg_semaforo_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgSemaforo.findAll", query = "SELECT s FROM SgSemaforo s")})
@Data
public class SgSemaforo implements Serializable {
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
    
//    @Column(name = "HORA_MINIMA")
//    @Temporal(TemporalType.TIME)
//    private Date horaMinima;
//    @Column(name = "HORA_MAXIMA")
//    @Temporal(TemporalType.TIME)
//    private Date horaMaxima;
    @OneToMany(mappedBy = "sgSemaforo")
    private Collection<SgEstatusAlterno> sgEstatusAlternoCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 32)
    @Column(name = "COLOR")
    private String color;
    @Size(max = 512)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @OneToMany(mappedBy = "sgSemaforo")
    private Collection<SgEstadoSemaforo> sgEstadoSemaforoCollection;

    public SgSemaforo() {
    }
    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgSemaforo)) {
            return false;
        }
        SgSemaforo other = (SgSemaforo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
}
