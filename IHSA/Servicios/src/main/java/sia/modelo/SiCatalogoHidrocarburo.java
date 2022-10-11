/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SI_CATALOGO_HIDROCARBURO")
@NamedQueries({
    @NamedQuery(name = "SiCatalogoHidrocarburo.findAll", query = "SELECT s FROM SiCatalogoHidrocarburo s where s.eliminado = false")})
@Getter
@Setter
@ToString
public class SiCatalogoHidrocarburo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Size(max = 32)
    @Column(name = "CODIGO")
    private String codigo;
    @Size(max = 32)
    @Column(name = "NOMBRE")
    private String nombre;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario genero;
    @Basic(optional = false)
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional = false)
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
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    //
    @JoinColumn(name = "SI_GRUPO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SiGrupo siGrupo;

    public SiCatalogoHidrocarburo() {
    }

    public SiCatalogoHidrocarburo(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiCatalogoHidrocarburo)) {
            return false;
        }
        SiCatalogoHidrocarburo other = (SiCatalogoHidrocarburo) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

}
