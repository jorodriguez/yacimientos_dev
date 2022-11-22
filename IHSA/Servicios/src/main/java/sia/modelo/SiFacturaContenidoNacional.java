/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SI_FACTURA_CONTENIDO_NACIONAL")
@SequenceGenerator(sequenceName = "si_factura_contenido_nacional_id_seq", name = "factura_cont_nac_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SiFacturaContenidoNacional.findId", query = "SELECT o FROM SiFacturaContenidoNacional o where o.id = ?1 "),
    @NamedQuery(name = "SiFacturaContenidoNacional.findAll", query = "SELECT o FROM SiFacturaContenidoNacional o")
})
@Setter
@Getter
public class SiFacturaContenidoNacional implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "factura_cont_nac_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;

    @JoinColumn(name = "SI_FACTURA", referencedColumnName = "ID")
    @ManyToOne
    private SiFactura siFactura;

    @JoinColumn(name = "SI_CATALOGO_HIDROCARBURO", referencedColumnName = "ID")
    @ManyToOne
    private SiCatalogoHidrocarburo siCatalogoHidrocarburo;
    //

    @Column(name = "CONTENIDO_NACIONAL")
    private Double contenidoNacional;

    @Column(name = "MONTO_FACTURADO")
    private BigDecimal montoFacturado;

    //
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @Column(name = "ELIMINADO")
    private boolean eliminado;

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiFacturaContenidoNacional)) {
            return false;
        }
        SiFacturaContenidoNacional other = (SiFacturaContenidoNacional) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    
    public String toString() {
        return "sia.modelo.SiFacturaContenidoNacional[ id=" + id + " ]";
    }
}

