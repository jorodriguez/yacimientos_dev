/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.SequenceGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "PV_NOTIFICA")
@SequenceGenerator(sequenceName = "pv_notifica_id_seq", name = "pv_notifica_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PvNotifica.findAll", query = "SELECT p FROM PvNotifica p")})
public class PvNotifica implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "pv_notifica_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Column(name = "FECHA")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "HORA")
    @Temporal(TemporalType.DATE)
    private Date hora;
    
    @Column(name = "ENTREGADA")
    private String entregada;
    @Column(name = "CIRCULAR")
    private Integer circular;
    @JoinColumn(name = "CONTACTO", referencedColumnName = "ID")
    @ManyToOne
    private ContactoProveedor contacto;

    public PvNotifica() {
    }

    public PvNotifica(Integer id) {
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

    public String getEntregada() {
        return entregada;
    }

    public void setEntregada(String entregada) {
        this.entregada = entregada;
    }

    public Integer getCircular() {
        return circular;
    }

    public void setCircular(Integer circular) {
        this.circular = circular;
    }

    public ContactoProveedor getContacto() {
        return contacto;
    }

    public void setContacto(ContactoProveedor contacto) {
        this.contacto = contacto;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PvNotifica)) {
            return false;
        }
        PvNotifica other = (PvNotifica) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.PvNotifica[ id=" + id + " ]";
    }
    
}
