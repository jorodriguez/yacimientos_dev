/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;

/**
 *
 * @author b75ckd35th
 */
@Entity
@Table(name = "AP_CAMPO_GERENCIA")
@SequenceGenerator(sequenceName = "ap_campo_gerencia_id_seq", name = "ap_campo_gerencia_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "ApCampoGerencia.findAll", query = "SELECT c FROM ApCampoGerencia c")})
@Setter
@Getter
public class ApCampoGerencia implements Serializable {

    @JoinColumn(name = "OC_SUBCAMPO", referencedColumnName = "ID")
    @ManyToOne
    private OcSubcampo ocSubcampo;
    @Column(name =     "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name =     "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name =     "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name =     "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "apCampoGerencia")
    private List<OfOficio> ofOficioList;

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "ap_campo_gerencia_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;
    @JoinColumn(name = "GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private Gerencia gerencia;
    @NotNull
    @JoinColumn(name = "RESPONSABLE", referencedColumnName = "ID")
    @ManyToOne
    private Usuario responsable;  
    
    @Column(name = "VISIBLE")
    private boolean visible;    
    @NotNull
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;

    public ApCampoGerencia() {
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ApCampoGerencia)) {
            return false;
        }
        ApCampoGerencia other = (ApCampoGerencia) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }


    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
            .append("id=").append(this.getId())
            .append(", apCampo=").append(getApCampo() != null ? getApCampo().getId() : null)
            .append(", gerencia=").append(getGerencia() != null ? getGerencia().getId() : null)
            .append(", visible=").append(isVisible())  
            .append(", responsable=").append(getResponsable() != null ? getResponsable().getId() : null)    
            .append(", genero=").append(getGenero() != null ? getGenero().getId() : null)
            .append(", fechaGenero=").append(getFechaGenero() != null ? (Constantes.FMT_ddMMyyy.format(getFechaGenero())) : null)
            .append(", horaGenero=").append(getHoraGenero() != null ? (Constantes.FMT_HHmmss.format(getHoraGenero())) : null)
            .append(", modifico=").append(getModifico() != null ? getModifico().getId() : null)
            .append(", fechaModifico=").append(getFechaModifico() != null ? (Constantes.FMT_ddMMyyy.format(getFechaModifico())) : null)
            .append(", horaModifico=").append(getHoraModifico() != null ? (Constantes.FMT_HHmmss.format(getHoraModifico())) : null)
            .append(", eliminado=").append(isEliminado())
            .append("}");
        
        return sb.toString();
    }


    public List<OfOficio> getOfOficioList() {
        return ofOficioList;
    }

    public void setOfOficioList(List<OfOficio> ofOficioList) {
        this.ofOficioList = ofOficioList;
    }

    public OcSubcampo getOcSubcampo() {
        return ocSubcampo;
    }

    public void setOcSubcampo(OcSubcampo ocSubcampo) {
        this.ocSubcampo = ocSubcampo;
    }
}