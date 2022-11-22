/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;

/**
 *
 * @author ihsa
 */
@Entity
@Table(name = "OC_NOMBRE_TAREA")
@XmlRootElement
@SequenceGenerator(sequenceName = "oc_nombre_tarea_id_seq", name = "nombre_tarea_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "OcNombreTarea.findAll", query = "SELECT o FROM OcNombreTarea o where o.eliminado = false"),
    @NamedQuery(name = "OcNombreTarea.buscarPorNombre", query = "SELECT o FROM OcNombreTarea o where o.nombre = ?1 and o.eliminado = false")
})
@Setter
@Getter
public class OcNombreTarea implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "nombre_tarea_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Size(max = 1024)
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
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
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
//

    public OcNombreTarea() {

    }

    public OcNombreTarea(int id) {
        this.id = id;
    }
    //

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OcNombreTarea)) {
            return false;
        }
        OcNombreTarea other = (OcNombreTarea) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
                .append("{")
                .append("id=").append(this.id)
                .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
                .append(", nombre=").append(this.nombre)
                .append(", fechaGenero=").append(this.fechaGenero != null ? (Constantes.FMT_ddMMyyy.format(this.fechaGenero)) : null)
                .append(", horaGenero=").append(this.horaGenero != null ? (Constantes.FMT_HHmmss.format(this.horaGenero)) : null)
                .append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
                .append(", fechaModifico=").append(this.fechaModifico != null ? (Constantes.FMT_ddMMyyy.format(this.fechaModifico)) : null)
                .append(", horaModifico=").append(this.horaModifico != null ? (Constantes.FMT_HHmmss.format(this.horaModifico)) : null)
                .append(", eliminado=").append(this.eliminado)
                .append("}");

        return sb.toString();
    }
}
