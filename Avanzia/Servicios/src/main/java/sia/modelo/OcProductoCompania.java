/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "OC_PRODUCTO_COMPANIA")
@SequenceGenerator(sequenceName = "oc_producto_compania_id_seq", name = "oc_producto_compania_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OcProductoCompania.findAll", query = "SELECT u FROM OcProductoCompania u")})
@Getter
@Setter
public class OcProductoCompania implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
@GeneratedValue(generator =  "oc_producto_compania_seq", strategy = GenerationType.SEQUENCE)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "COMPANIA", referencedColumnName = "RFC")
    @ManyToOne
    private Compania compania;
    @JoinColumn(name = "OC_PRODUCTO", referencedColumnName = "ID")
    @ManyToOne
    private OcProducto ocProducto;
    //
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
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
    //

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        if (!(object instanceof OcProductoCompania)) {
            return false;
        }
        OcProductoCompania other = (OcProductoCompania) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    
    public String toString() {
        return "sia.modelo.OcProductoCompania[ id=" + id + " ]";
    }
}
