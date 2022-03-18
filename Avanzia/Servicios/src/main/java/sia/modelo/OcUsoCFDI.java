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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 *
 *
 * @author mluis
 */
@Getter
@Setter
@Entity
@Table(name = "OC_USO_CFDI")
@NamedQueries({
    @NamedQuery(name = "OcUsoCFDI.findAll", query = "SELECT s FROM OcUsoCFDI s where s.eliminado = false"),
    @NamedQuery(name = "OcUsoCFDI.traerPorTipo", query = "SELECT s FROM OcUsoCFDI s where s.tipo = ?1 and s.eliminado = false"),
    @NamedQuery(name = "OcUsoCFDI.traerPorCodigo", query = "SELECT s FROM OcUsoCFDI s where s.codigo = ?1 and s.tipo = ?2 and s.eliminado = false")
})
public class OcUsoCFDI implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Size(max = 516)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 12)
    @Column(name = "CODIGO")
    private String codigo;
    @Column(name = "TIPO")
    @Size(max = 2)
    private String tipo;

    //
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    //
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    //
    @Column(name = "ELIMINADO")
    private boolean eliminado;

    public OcUsoCFDI() {
    }

    public OcUsoCFDI(int id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OcUsoCFDI)) {
            return false;
        }
        OcUsoCFDI other = (OcUsoCFDI) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
