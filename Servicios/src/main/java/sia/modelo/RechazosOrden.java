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

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "RECHAZOS_ORDEN")
@SequenceGenerator(sequenceName = "rechazos_orden_id_seq", name = "rechazos_orden_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RechazosOrden.findAll", query = "SELECT r FROM RechazosOrden r")})
public class RechazosOrden implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "rechazos_orden_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Column(name = "FECHA")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "HORA")
    @Temporal(TemporalType.TIME)
    private Date hora;
    @Lob
    
    @Column(name = "MOTIVO")
    private String motivo;
    
    @Column(name = "CUMPLIDO")
    private String cumplido;
    @JoinColumn(name = "RECHAZO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario rechazo;
    @JoinColumn(name = "ORDEN", referencedColumnName = "ID")
    @ManyToOne
    private Orden orden;

    public RechazosOrden() {
    }

    public RechazosOrden(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getCumplido() {
        return cumplido;
    }

    public void setCumplido(String cumplido) {
        this.cumplido = cumplido;
    }

    public Usuario getRechazo() {
        return rechazo;
    }

    public void setRechazo(Usuario rechazo) {
        this.rechazo = rechazo;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RechazosOrden)) {
            return false;
        }
        RechazosOrden other = (RechazosOrden) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.RechazosOrden[ id=" + id + " ]";
    }    
}
