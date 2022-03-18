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
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;

/**
 *
 * @author ihsa
 */
@Entity
@Table(name = "OC_FLUJO")
@SequenceGenerator(sequenceName = "oc_flujo_id_seq", name = "oc_flujo_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OcFlujo.findAll", query = "SELECT o FROM OcFlujo o")})
@Setter
@Getter
public class OcFlujo implements Serializable {

    @JoinColumn(name = "OC_SUBCAMPO", referencedColumnName = "ID")
    @ManyToOne
    private OcSubcampo ocSubcampo;

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "oc_flujo_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;        
    @Size(max = 2)
    @Column(name = "ACCION")
    private String accion;
    @JoinColumn(name = "EJECUTA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario ejecuta;        
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
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
//
    //

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof sia.modelo.OcFlujo)) {
            return false;
        }
        sia.modelo.OcFlujo other = (sia.modelo.OcFlujo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append("{");
        sb.append("id=").append(this.id);
        sb.append(", genero=").append(this.genero != null ? this.genero.getId() : null);        
        sb.append(", accion=").append(this.accion);
        sb.append(", apCampo=").append(this.apCampo != null ? this.apCampo.getId() : null);
        sb.append(", ejecuta =").append(this.ejecuta != null ? this.ejecuta.getId() : null);
        sb.append(", fechaGenero=").append(this.fechaGenero != null ? (Constantes.FMT_ddMMyyy.format(this.fechaGenero)) : null);
        sb.append(", horaGenero=").append(this.horaGenero != null ? (Constantes.FMT_HHmmss.format(this.horaGenero)) : null);
        sb.append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null);
        sb.append(", fechaModifico=").append(this.fechaModifico != null ? (Constantes.FMT_ddMMyyy.format(this.fechaModifico)) : null);
        sb.append(", horaModifico=").append(this.horaModifico != null ? (Constantes.FMT_HHmmss.format(this.horaModifico)) : null);
        sb.append(", eliminado=").append(this.eliminado);
        sb.append("}");

        return sb.toString();
    }

    public OcFlujo() {
    }

    public OcSubcampo getOcSubcampo() {
        return ocSubcampo;
    }

    public void setOcSubcampo(OcSubcampo ocSubcampo) {
        this.ocSubcampo = ocSubcampo;
    }
}
