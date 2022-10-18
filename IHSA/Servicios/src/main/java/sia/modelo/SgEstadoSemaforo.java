/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SG_ESTADO_SEMAFORO")
@SequenceGenerator(sequenceName = "sg_estado_semaforo_id_seq", name = "sg_estado_semaforo_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgEstadoSemaforo.findAll", query = "SELECT s FROM SgEstadoSemaforo s")})
@Data
public class SgEstadoSemaforo implements Serializable {
    @Column(name = "HORA_INICIO")
    @Temporal(TemporalType.TIME)
    private Date horaInicio;
    @Column(name = "HORA_FIN")
    @Temporal(TemporalType.TIME)
    private Date horaFin;
    @Column(name = "FECHA_INICIO")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    @Column(name = "FECHA_FIN")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
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
    @OneToMany
    private Collection<SgViajeEstadoSemaforo> sgViajeEstadoSemaforoCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_estado_semaforo_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "ACTUAL")
    private boolean actual;
    @Size(max = 2056)
    @Column(name = "JUSTIFICACION")
    private String justificacion;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
//    @JoinColumn(name = "SG_RUTA_TERRESTRE", referencedColumnName = "ID")
//    @ManyToOne
//    private SgRutaTerrestre sgRutaTerrestre;
    @JoinColumn(name = "SG_SEMAFORO", referencedColumnName = "ID")
    @ManyToOne
    private SgSemaforo sgSemaforo;
    @JoinColumn(name = "CO_NOTICIA", referencedColumnName = "ID")
    @ManyToOne
    private CoNoticia coNoticia;
    @JoinColumn(name = "GR_MAPA", referencedColumnName = "ID")
    @ManyToOne
    private GrMapa grMapa;

    public SgEstadoSemaforo() {
    }

    public SgEstadoSemaforo(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgEstadoSemaforo)) {
            return false;
        }
        SgEstadoSemaforo other = (SgEstadoSemaforo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
}
