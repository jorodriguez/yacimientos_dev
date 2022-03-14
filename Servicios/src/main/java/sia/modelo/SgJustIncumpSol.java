/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author jrodriguez
 * SgSolViajeIncum
 */
@Entity
@Table(name = "SG_JUST_INCUMP_SOL")
@SequenceGenerator(sequenceName = "sg_just_incump_sol_id_seq", name = "sg_just_incump_sol_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgJustIncumpSol.findAll", query = "SELECT c FROM SgJustIncumpSol c")})
@ToString
@Getter
@Setter
public class SgJustIncumpSol implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_just_incump_sol_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 1024)
    @Column(name = "JUSTIFICACION")
    private String justificacion;
    @JoinColumn(name = "SG_SOLICITUD_VIAJE", referencedColumnName = "ID")
    @ManyToOne
    private SgSolicitudViaje sgSolicitudViaje;    
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;

    public SgJustIncumpSol() {
    }

}