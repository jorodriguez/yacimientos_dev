/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import javax.persistence.SequenceGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 * GEN_CUENTA_BANCO_PROVEEDOR_ID
 *
 * @author hacosta
 */
@Getter
@Setter
@Entity
@Table(name = "CUENTA_BANCO_PROVEEDOR")
@SequenceGenerator(sequenceName = "cuenta_banco_proveedor_id_seq", name = "cuenta_banco_proveedor_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CuentaBancoProveedor.findAll", query = "SELECT c FROM CuentaBancoProveedor c")})
public class CuentaBancoProveedor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "cuenta_banco_proveedor_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)

    @Column(name = "ID")
    private Integer id;
    @Size(max = 30)
    @Column(name = "CUENTA")
    private String cuenta;
    @Size(max = 100)
    @Column(name = "BANCO")
    private String banco;
    @Size(max = 20)
    @Column(name = "CLABE")
    private String clabe;
    @Size(max = 50)
    @Column(name = "PLAZA")
    private String plaza;
    @Size(max = 20)
    @Column(name = "NUMERO_PLAZA")
    private String numeroPlaza;
    @Size(max = 35)
    @Column(name = "SUCURSAL")
    private String sucursal;
    @Size(max = 20)
    @Column(name = "NUMERO_SUCURSAL")
    private String numeroSucursal;
    @JoinColumn(name = "PROVEEDOR", referencedColumnName = "ID")
    @ManyToOne
    private Proveedor proveedor;
    @JoinColumn(name = "MONEDA", referencedColumnName = "ID")
    @ManyToOne
    private Moneda moneda;

    @Column(name = "NACIONAL")
    private boolean nacional;
    @Size(max = 100)
    @Column(name = "SWIFT")
    private String swift;
    @Size(max = 100)
    @Column(name = "ABA")
    private String aba;
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

    public CuentaBancoProveedor() {
    }

    public CuentaBancoProveedor(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CuentaBancoProveedor)) {
            return false;
        }
        CuentaBancoProveedor other = (CuentaBancoProveedor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.CuentaBancoProveedor[ id=" + id + " ]";
    }

}
