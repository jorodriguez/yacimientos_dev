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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Entity
@Table(name = "SG_TARJETA_BANCARIA")
@SequenceGenerator(sequenceName = "sg_tarjeta_bancaria_id_seq", name = "sg_tarjeta_bancaria_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
@NamedQueries({
    @NamedQuery(name = "SgTarjetaBancaria.findAll", query = "SELECT s FROM SgTarjetaBancaria s")})
public class SgTarjetaBancaria implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    @GeneratedValue(generator = "sg_tarjeta_bancaria_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;
    //
    @JoinColumn(name = "SG_TIPO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipo sgTipo;

    @JoinColumn(name = "SG_TIPO_ESPECIFICO", referencedColumnName = "ID")
    @ManyToOne
    private SgTipoEspecifico sgTipoEspecifico;

    @JoinColumn(name = "COMPANIA", referencedColumnName = "RFC")
    @ManyToOne
    private Compania compania;

    @Size(max = 30)
    @Column(name = "NUMERO_TARJETA")
    private String numeroTarjeta;

    @Column(name = "CODIGO_SEGURIDAD")
    private String codigoSeguridad;

    @Size(max = 6)
    @Column(name = "FECHA_VENCIMIENTO")
    private String fechaVencimiento;

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

    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;

    @Column(name = "ELIMINADO")
    private boolean eliminado;

    public SgTarjetaBancaria() {
    }

    public SgTarjetaBancaria(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        if (!(object instanceof SgTarjetaBancaria)) {
            return false;
        }
        SgTarjetaBancaria other = (SgTarjetaBancaria) object;
        return !((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id)));
    }

    
    public String toString() {
        return "sia.modelo.SgTarjetaBancaria[ id=" + id + " ]";
    }
}
