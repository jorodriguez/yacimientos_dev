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
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "OC_TERMINO_PAGO")
@SequenceGenerator(sequenceName = "oc_termino_pago_id_seq", name = "oc_termino_pago_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OcTerminoPago.findAll", query = "SELECT o FROM OcTerminoPago o")})
@Setter
@Getter
public class OcTerminoPago implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 512)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 12)
    @Column(name = "CODIGO  ")
    private String codigo;
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
    @JoinColumn(name = "COMPANIA", referencedColumnName = "RFC")
    @ManyToOne
    private Compania compania;

    public OcTerminoPago() {
    }

    public OcTerminoPago(int id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        if (!(object instanceof OcTerminoPago)) {
            return false;
        }
        OcTerminoPago other = (OcTerminoPago) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append("{");
        sb.append("id= ").append(this.id);
        sb.append(", genero = ").append(this.genero != null ? this.genero.getId() : null);
        sb.append(", nombre = ").append(this.nombre);
        sb.append(", codigo = ").append(this.codigo);
        sb.append(", fechaGenero =").append(this.fechaGenero != null ? (Constantes.FMT_ddMMyyy.format(this.fechaGenero)) : null);
        sb.append(", horaGenero =").append(this.horaGenero != null ? (Constantes.FMT_HHmmss.format(this.horaGenero)) : null);
        sb.append(", modifico = ").append(this.modifico != null ? this.modifico.getId() : null);
        sb.append(", fechaModifico = ").append(this.fechaModifico != null ? (Constantes.FMT_ddMMyyy.format(this.fechaModifico)) : null);
        sb.append(", horaModifico= ").append(this.horaModifico != null ? (Constantes.FMT_HHmmss.format(this.horaModifico)) : null);
        sb.append(", eliminado= ").append(this.eliminado);
        sb.append("}");

        return sb.toString();
    }
}
