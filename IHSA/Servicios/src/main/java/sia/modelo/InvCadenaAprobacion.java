/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author mluis
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "INV_CADENA_APROBACION")
@SequenceGenerator(sequenceName = "inv_cadena_aprobacion_id_seq", name = "cadena_aprobacion_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InvCadenaAprobacion.findAll", query = "SELECT c FROM InvCadenaAprobacion c")})
public class InvCadenaAprobacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator =  "cadena_aprobacion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "AUTORIZA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario autoriza;
    @JoinColumn(name = "SOLICITA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario solicita;
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Basic(optional = false)
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional = false)
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;

    public InvCadenaAprobacion() {
    }
    
    public InvCadenaAprobacion(int id) {
        this.id = id;
    }
    

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvCadenaAprobacion)) {
            return false;
        }
        InvCadenaAprobacion other = (InvCadenaAprobacion) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
