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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad de avisos
 *
 * @author Aplimovil SA de CV
 */
@Entity
@Table(name = "INV_AVISO")
@SequenceGenerator(sequenceName = "inv_aviso_id_seq", name = "inv_aviso_seq", allocationSize = 1)
@XmlRootElement
@Getter
@Setter
public class InvAviso implements Serializable {

    private static final long serialVersionUID = 938015182248096158L;

    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
@GeneratedValue(generator =  "inv_aviso_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "FECHA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @Size(max = 512)
    @Column(name = "ASUNTO")
    private String asunto;

    @Size(max = 2056)
    @Column(name = "MENSAJE")
    private String mensaje;

    
    @Column(name = "LEIDO")
    private boolean leido;

    
    @Column(name = "ELIMINADO")
    private boolean eliminado;

    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuario;

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
