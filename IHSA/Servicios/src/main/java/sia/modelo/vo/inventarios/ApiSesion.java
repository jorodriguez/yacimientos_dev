package sia.modelo.vo.inventarios;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sia.modelo.Usuario;

/**
 * Entidad que representa la sesion utilizada por los servicios REST del api de
 * aplicaciones moviles
 *
 * @author Aplimovil SA de CV
 */
@Entity
@Table(name = "API_SESIONES")
@IdClass(ApiSesionPK.class)
@XmlRootElement
@Getter
@Setter
@ToString
@NamedQueries({
    @NamedQuery(name = "ApiSesion.BuscarSesionActivaPorUsuarioId", query = "SELECT s FROM ApiSesion s WHERE s.usuario.id = :usuarioId and s.activo = :activo"),
    @NamedQuery(name = "ApiSesion.EliminarKeysMenoresATresMeses", query = "DELETE FROM ApiSesion s WHERE s.fechaGenero < :tresMesesAtras and s.usuario.id = :usuarioId")
})
public class ApiSesion implements Serializable {

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "API_KEY")
    private String apiKey;

    @Id
    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @Column(name = "ACTIVO")
    private boolean activo;

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

}
