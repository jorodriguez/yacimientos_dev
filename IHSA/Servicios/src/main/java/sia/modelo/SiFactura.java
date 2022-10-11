/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
@Entity
@Table(name = "SI_FACTURA")
@SequenceGenerator(sequenceName = "si_factura_id_seq", name = "si_factura_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SiFactura.findAll", query = "SELECT s FROM SiFactura s")})
public class SiFactura implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "si_factura_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)

    @Column(name = "ID")
    private Integer id;
    @Size(max = 1024)
    @Column(name = "CONCEPTO")
    private String concepto;
    @Size(max = 32)
    @Column(name = "FOLIO")
    private String folio;
    @Column(name = "MONTO")
    private BigDecimal monto;
    @Size(max = 1024)
    @Column(name = "OBSERVACION")
    private String observacion;
    @Column(name = "FECHA_EMISION")
    @Temporal(TemporalType.DATE)
    private Date fechaEmision;
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
    @OneToMany(mappedBy = "siFactura")
    private Collection<SiIncidenciaFactura> siIncidenciaFacturaCollection;

    @JoinColumn(name = "PROVEEDOR", referencedColumnName = "ID")
    @ManyToOne
    private Proveedor proveedor;

    @JoinColumn(name = "MONEDA", referencedColumnName = "ID")
    @ManyToOne
    private Moneda moneda;

    @JoinColumn(name = "SI_ADJUNTO", referencedColumnName = "ID")
    @ManyToOne
    private SiAdjunto siAdjunto;

    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;

    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @Column(name = "CANTIDAD")
    private Integer cantidad;
    //
    @JoinColumn(name = "ORDEN", referencedColumnName = "ID")
    @ManyToOne
    private Orden orden;

    @JoinColumn(name = "COMPANIA", referencedColumnName = "RFC")
    @ManyToOne
    private Compania compania;

    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne
    private ApCampo apCampo;

    @Column(name = "REVISADA")
    private boolean revisada;

    @JoinColumn(name = "SI_FACTURA", referencedColumnName = "ID")
    @ManyToOne
    private SiFactura siFactura;

    @JoinColumn(name = "OC_USO_CFDI", referencedColumnName = "ID")
    @ManyToOne
    private OcUsoCFDI ocUsoCfdi;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "forma_pago")
    private String formaPago;

    @Column(name = "folio_fiscal")
    private String folioFiscal;

    @Column(name = "tipo_factura")
    private String tipoFactura;
    @Column(name = "SUBTOTAL")
    private BigDecimal subTotal;
    @Column(name = "TIPO_CAMBIO")
    private BigDecimal tipoCambio;

    @Size(max = 64)
    @Column(name = "POLIZA")
    private String poliza;

    @Size(max = 64)
    @Column(name = "POLIZA_PAGO")
    private String polizaPago;

    @JoinColumn(name = "complemento_pago", referencedColumnName = "ID")
    @ManyToOne
    private SiAdjunto complementoPago;
    
    @JoinColumn(name = "complemento_pago_pdf", referencedColumnName = "ID")
    @ManyToOne
    private SiAdjunto complementoPagoPdf;

    @JoinColumn(name = "comprobante_pago", referencedColumnName = "ID")
    @ManyToOne
    private SiAdjunto comprobantePago;
    
    @Column(name = "acepta_avanzia")
    private boolean aceptaAvanzia;

    public SiFactura() {
    }

    public SiFactura(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiFactura)) {
            return false;
        }
        SiFactura other = (SiFactura) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.SiFactura[ id=" + id + " ]";
    }

}
