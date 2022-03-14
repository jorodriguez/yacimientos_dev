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
@Table(name = "SERVICIO_ADICIONAL")
@SequenceGenerator(sequenceName = "servicio_adicional_id_seq", name = "servicio_adicional_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ServicioAdicional.findAll", query = "SELECT s FROM ServicioAdicional s")})
public class ServicioAdicional implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "SERVICIO_PRINCIPAL", referencedColumnName = "ID")
    @ManyToOne
    private CaracteristicasServicio servicioPrincipal;
    @JoinColumn(name = "SERVICIO_ADICIONAL", referencedColumnName = "ID")
    @ManyToOne
    private CaracteristicasServicio servicioAdicional;

    public ServicioAdicional() {
    }

    public ServicioAdicional(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CaracteristicasServicio getServicioPrincipal() {
        return servicioPrincipal;
    }

    public void setServicioPrincipal(CaracteristicasServicio servicioPrincipal) {
        this.servicioPrincipal = servicioPrincipal;
    }

    public CaracteristicasServicio getServicioAdicional() {
        return servicioAdicional;
    }

    public void setServicioAdicional(CaracteristicasServicio servicioAdicional) {
        this.servicioAdicional = servicioAdicional;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ServicioAdicional)) {
            return false;
        }
        ServicioAdicional other = (ServicioAdicional) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.ServicioAdicional[ id=" + id + " ]";
    }
    
}
