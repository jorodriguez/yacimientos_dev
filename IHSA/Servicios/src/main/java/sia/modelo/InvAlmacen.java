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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sia.modelo.vo.inventarios.AlmacenVO;

/**
 *
 * @author Aplimovil SA de CV
 */
@Entity
@Table(name = "INV_ALMACEN")
@SequenceGenerator(sequenceName = "inv_almacen_id_seq", name = "inv_almacen_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
@ToString
@NamedNativeQueries({
    @NamedNativeQuery(name = "InvAlmacen.ExisteUnidadesEnInventario", query = "SELECT COUNT(0) FROM INV_INVENTARIO WHERE ALMACEN = ?1 AND NUMERO_UNIDADES > 1 AND ELIMINADO = ?2")
})
public class InvAlmacen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    @GeneratedValue(generator = "inv_almacen_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Size(max = 70)
    @Column(name = "NOMBRE")
    private String nombre;

    @Size(max = 255)
    @Column(name = "DESCRIPCION")
    private String descripcion;

    @JoinColumn(name = "SUPERVISOR", referencedColumnName = "ID")
    @ManyToOne
    private Usuario supervisor;

    @JoinColumn(name = "RESPONSABLE1", referencedColumnName = "ID")
    @ManyToOne
    private Usuario responsable1;

    @JoinColumn(name = "RESPONSABLE2", referencedColumnName = "ID")
    @ManyToOne
    private Usuario responsable2;

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

    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvAlmacen)) {
            return false;
        }
        InvAlmacen other = (InvAlmacen) object;
        return this.id.equals(other.getId());
    }

    public InvAlmacen() {

    }

    public InvAlmacen(Integer id) {
        this.id = id;
    }

    public InvAlmacen(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public InvAlmacen(AlmacenVO vo) {
        Usuario usuarioResponsable1 = new Usuario(vo.getResponsable1UsuarioId());
        usuarioResponsable1.setNombre(vo.getResponsable1Nombre());
        usuarioResponsable1.setEmail(vo.getResponsable1Email());

        Usuario usuarioResponsable2 = new Usuario(vo.getResponsable2UsuarioId());
        usuarioResponsable2.setNombre(vo.getResponsable2Nombre());
        usuarioResponsable2.setEmail(vo.getResponsable2Email());

        this.id = vo.getId();
        this.nombre = vo.getNombre();
        this.descripcion = vo.getDescripcion();
        this.responsable1 = usuarioResponsable1;
        this.responsable2 = usuarioResponsable2;
    }
}
