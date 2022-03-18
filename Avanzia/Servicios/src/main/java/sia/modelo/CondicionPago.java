/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 * GEN_CONDICION_PAGO_ID
 *
 * @author hacosta
 */
@Entity
@Table(name = "CONDICION_PAGO")
@SequenceGenerator(sequenceName = "condicion_pago_id_seq", name = "condicion_pago_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CondicionPago.findAll", query = "SELECT c FROM CondicionPago c")})
@Getter
@Setter
public class CondicionPago implements Serializable {

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
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @OneToMany(mappedBy = "condicionPago")
    private Collection<PvRelCondicionPago> pvRelCondicionPagoCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "condicion_pago_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 512)
    @Column(name = "NOMBRE")
    private String nombre;
    
    @Column(name = "NOTIFICAR")
    private boolean notificar;
    @OneToMany(mappedBy = "condicionPago")
    private Collection<Orden> ordenCollection;
    @OneToMany(mappedBy = "condicionPago")
    private Collection<CaracteristicasServicio> caracteristicasServicioCollection;
    

    public CondicionPago() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CondicionPago)) {
            return false;
        }
        CondicionPago other = (CondicionPago) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        SimpleDateFormat sdfd = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdft = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("{");
        sb.append("id=").append(this.id);
        sb.append(", nombre = ").append(this.nombre);
        sb.append(", notificar = ").append(this.notificar);
        sb.append(", genero = ").append(this.genero != null ? this.genero.getId() : null);
        sb.append(", fechaGenero = ").append(this.fechaGenero != null ? (sdfd.format(this.fechaGenero)) : null);
        sb.append(", horaGenero = ").append(this.horaGenero != null ? (sdft.format(this.horaGenero)) : null);
        sb.append(", modifico = ").append(this.modifico != null ? this.modifico.getId() : null);
        sb.append(", fechaModifico = ").append(this.fechaModifico != null ? (sdfd.format(this.fechaModifico)) : null);
        sb.append(", horaModifico = ").append(this.horaModifico != null ? (sdft.format(this.horaModifico)) : null);
        sb.append(", eliminado = ").append(this.eliminado);
        sb.append("}");

        return sb.toString();
    }
}
