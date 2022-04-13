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
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author hacosta
 */
@Getter
@Setter
@Entity
@Table(name = "NOTA_ORDEN")
@SequenceGenerator(sequenceName = "nota_orden_id_seq", name = "nota_orden_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NotaOrden.findAll", query = "SELECT n FROM NotaOrden n")})
public class NotaOrden implements Serializable {

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
    @JoinColumn(name = "OC_SUBCAMPO", referencedColumnName = "ID")
    @ManyToOne
    private OcSubcampo ocSubcampo;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "nota_orden_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 100)
    @Column(name = "TITULO")
    private String titulo;
    @Lob
    @Column(name = "MENSAJE")
    private String mensaje;
    @Column(name = "FECHA")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "HORA")
    @Temporal(TemporalType.TIME)
    private Date hora;
    @Column(name = "RESPUESTAS")
    private Integer respuestas;
    @Column(name = "IDENTIFICADOR")
    private Integer identificador;
    @Column(name = "ULT_RESPUESTA")
    @Temporal(TemporalType.DATE)
    private Date ultRespuesta;
    
    @Column(name = "FINALIZADA")
    private boolean finalizada;
    @JoinColumn(name = "AUTOR", referencedColumnName = "ID")
    @ManyToOne
    private Usuario autor;
    @JoinColumn(name = "ORDEN", referencedColumnName = "ID")
    @ManyToOne
    private Orden orden;
    @OneToMany(mappedBy = "notaOrden")
    private Collection<InvitadosNotaOrden> invitadosNotaOrdenCollection;

     //actualizacion 19/marzo/2013
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private ApCampo apCampo;
    
    public NotaOrden() {
    }

    public NotaOrden(Integer id) {
        this.id = id;
    }


    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NotaOrden)) {
            return false;
        }
        NotaOrden other = (NotaOrden) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.NotaOrden[ id=" + id + " ]";
    }
    
}
