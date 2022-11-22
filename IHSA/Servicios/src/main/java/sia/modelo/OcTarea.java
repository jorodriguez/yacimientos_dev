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
import sia.constantes.Constantes;

/**
 *
 * @author ihsa
 */
@Entity
@Table(name = "OC_TAREA")
@SequenceGenerator(sequenceName = "oc_tarea_id_seq", name = "oc_tarea_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OcTarea.findAll", query = "SELECT o FROM OcTarea o")
    ,
@NamedQuery(name = "OcTarea.buscarPorId", query = "SELECT o FROM OcTarea o where o.id = ?1 ")
})
@Setter
@Getter
public class OcTarea implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "oc_tarea_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)

    @Column(name = "ID")
    private Integer id;
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
    @JoinColumn(name = "PROYECTO_OT", referencedColumnName = "ID")
    @ManyToOne
    private ProyectoOt proyectoOt;
    @JoinColumn(name = "GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private Gerencia gerencia;
    @JoinColumn(name = "OC_NOMBRE_TAREA", referencedColumnName = "ID")
    @ManyToOne
    private OcNombreTarea ocNombreTarea;
    @JoinColumn(name = "OC_UNIDAD_COSTO", referencedColumnName = "ID")
    @ManyToOne
    private OcUnidadCosto ocUnidadCosto;
    //
    @JoinColumn(name = "OC_CODIGO_TAREA", referencedColumnName = "ID")
    @ManyToOne
    private OcCodigoTarea ocCodigoTarea;
    @JoinColumn(name = "OC_ACTIVIDADPETROLERA", referencedColumnName = "ID")
    @ManyToOne
    private OcActividadPetrolera ocActividadPetrolera;

    public OcTarea() {
    }

    public OcTarea(int id) {
        this.id = id;
    }

    ////
    ////
    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OcTarea)) {
            return false;
        }
        OcTarea other = (OcTarea) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append("{");
        sb.append("id=").append(this.id);
        sb.append(", proyectoOt = ").append(proyectoOt != null ? proyectoOt.getId() : null);
        sb.append(", gerencia = ").append(gerencia != null ? gerencia.getId() : null);
        sb.append(", ocNombreTarea = ").append(ocNombreTarea != null ? ocNombreTarea.getId() : null);
        sb.append(", ocUnidadCosto = ").append(ocUnidadCosto != null ? ocUnidadCosto.getId() : null);
        sb.append(", ocCodigoTarea = ").append(ocCodigoTarea != null ? ocCodigoTarea : null);
        sb.append(", genero=").append(this.genero != null ? this.genero.getId() : null);
        sb.append(", fechaGenero=").append(this.fechaGenero != null ? (Constantes.FMT_ddMMyyy.format(this.fechaGenero)) : null);
        sb.append(", horaGenero=").append(this.horaGenero != null ? (Constantes.FMT_HHmmss.format(this.horaGenero)) : null);
        sb.append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null);
        sb.append(", fechaModifico=").append(this.fechaModifico != null ? (Constantes.FMT_ddMMyyy.format(this.fechaModifico)) : null);
        sb.append(", horaModifico=").append(this.horaModifico != null ? (Constantes.FMT_HHmmss.format(this.horaModifico)) : null);
        sb.append(", eliminado=").append(this.eliminado);
        sb.append("}");

        return sb.toString();
    }
}
