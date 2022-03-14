/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Getter
@Setter
@Table(name = "PV_REGISTROFISCAL")
@SequenceGenerator(sequenceName = "pv_registrofiscal_id_seq", name = "pv_registrofiscal_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PvRegistroFiscal.findAll", query = "SELECT o FROM PvRegistroFiscal o")})
public class PvRegistroFiscal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "pv_registrofiscal_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
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
    @JoinColumn(name = "PROVEEDOR", referencedColumnName = "ID")
    @ManyToOne
    private Proveedor proveedor;
    @Size(max = 40)
    @Column(name = "NOACTA")
    private String noActa;

    @Size(max = 150)
    @Column(name = "NOMBRENOT")
    private String nombreNot;

    @Size(max = 100)
    @Column(name = "NONOTARIA")
    private String noNotaria;

    @Size(max = 40)
    @Column(name = "NOBOLETA")
    private String noBoleta;

    @Size(max = 50)
    @Column(name = "SEDE")
    private String sede;

    @Column(name = "EMISION")
    @Temporal(TemporalType.DATE)
    private Date emision;

    @Column(name = "INSCRIPCION")
    @Temporal(TemporalType.DATE)
    private Date inscripcion;

    public PvRegistroFiscal() {
    }

    public PvRegistroFiscal(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PvRegistroFiscal)) {
            return false;
        } 
        PvRegistroFiscal other = (PvRegistroFiscal) object;
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
        sb.append(", fechaGenero=").append(this.fechaGenero != null ? (Constantes.FMT_ddMMyyy.format(this.fechaGenero)) : null);
        sb.append(", horaGenero=").append(this.horaGenero != null ? (Constantes.FMT_HHmmss.format(this.horaGenero)) : null);
        sb.append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null);
        sb.append(", fechaModifico=").append(this.fechaModifico != null ? (Constantes.FMT_ddMMyyy.format(this.fechaModifico)) : null);
        sb.append(", horaModifico=").append(this.horaModifico != null ? (Constantes.FMT_HHmmss.format(this.horaModifico)) : null);
        sb.append(", eliminado=").append(this.eliminado);
        sb.append(", proveedor=").append(this.proveedor != null ? this.proveedor.getId() : null);
        sb.append(", noActa=").append(this.noActa != null ? this.noActa : null);        
        sb.append(", nombreNot=").append(this.nombreNot != null ? this.nombreNot : null);
        sb.append(", noNotaria=").append(this.noNotaria != null ? this.noNotaria : null);
        sb.append(", noBoleta=").append(this.noBoleta != null ? this.noBoleta : null);
        sb.append(", sede=").append(this.sede != null ? this.sede : null);
        sb.append(", inscripcion=").append(this.inscripcion != null ? (Constantes.FMT_ddMMyyy.format(this.inscripcion)) : null);
        sb.append(", emision=").append(this.emision != null ? (Constantes.FMT_ddMMyyy.format(this.emision)) : null);
        sb.append("}");

        return sb.toString();
    }

}
