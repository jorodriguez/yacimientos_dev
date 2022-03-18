/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
@Entity
@Table(name = "OC_REQUISICION_CO_NOTICIA")
@SequenceGenerator(sequenceName = "oc_requisicion_co_noticia_id_seq", name = "oc_requisicion_co_noticia_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OcRequisicionCoNoticia.findAll", query = "SELECT r FROM OcRequisicionCoNoticia r")})
@Getter
@Setter
public class OcRequisicionCoNoticia implements Serializable {

    @JoinColumn(name = "OC_SUBCAMPO", referencedColumnName = "ID")
    @ManyToOne
    private OcSubcampo ocSubcampo;

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "oc_requisicion_co_noticia_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "REQUISICION", referencedColumnName = "ID")
    @ManyToOne
    private Requisicion requisicion;
    @JoinColumn(name = "CO_NOTICIA", referencedColumnName = "ID")
    @ManyToOne
    private CoNoticia coNoticia;
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;
    //    
    
    @Column(name = "ACTIVA")
    private boolean activa;
    //
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

    public OcRequisicionCoNoticia() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OcRequisicionCoNoticia)) {
            return false;
        }
        OcRequisicionCoNoticia other = (OcRequisicionCoNoticia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append("{");
        sb.append("id=").append(this.id); 
        sb.append(", requisicion =").append(this.requisicion != null ? this.requisicion.getId() : null);
        sb.append(", noticia=").append(this.coNoticia != null ? this.coNoticia.getId() : null);        
        sb.append(", genero=").append(this.genero != null ? this.genero.getId() : null);
        sb.append(", fechaGenero=").append(this.fechaGenero != null ? (sdfFecha.format(this.fechaGenero)) : null);
        sb.append(", horaGenero=").append(this.horaGenero != null ? (sdfHora.format(this.horaGenero)) : null);
        sb.append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null);
        sb.append(", fechaModifico=").append(this.fechaModifico != null ? (sdfFecha.format(this.fechaModifico)) : null);
        sb.append(", horaModifico=").append(this.horaModifico != null ? (sdfHora.format(this.horaModifico)) : null);
        sb.append(", eliminado=").append(this.eliminado);
        sb.append("}");

        return sb.toString();
    }

    public OcSubcampo getOcSubcampo() {
        return ocSubcampo;
    }

    public void setOcSubcampo(OcSubcampo ocSubcampo) {
        this.ocSubcampo = ocSubcampo;
    }
}
