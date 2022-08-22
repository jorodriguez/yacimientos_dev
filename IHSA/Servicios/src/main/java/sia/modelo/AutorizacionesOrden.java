/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
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
@Table(name = "AUTORIZACIONES_ORDEN")
@SequenceGenerator(sequenceName = "autorizaciones_orden_id_seq", name = "autorizaciones_orden_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AutorizacionesOrden.findAll", query = "SELECT a FROM AutorizacionesOrden a")})
public class AutorizacionesOrden implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "autorizaciones_orden_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "FECHA_AUTORIZO_GERENCIA")
    @Temporal(TemporalType.DATE)
    private Date fechaAutorizoGerencia;
    @Column(name = "HORA_AUTORIZO_GERENCIA")
    @Temporal(TemporalType.TIME)
    private Date horaAutorizoGerencia;

    @Column(name = "AUTORIZACION_GERENCIA_AUTO")
    private boolean autorizacionGerenciaAuto;
    @Column(name = "FECHA_AUTORIZO_COMPRAS")
    @Temporal(TemporalType.DATE)
    private Date fechaAutorizoCompras;
    @Column(name = "HORA_AUTORIZO_COMPRAS")
    @Temporal(TemporalType.TIME)
    private Date horaAutorizoCompras;

    @Column(name = "AUTORIZACION_COMPRAS_AUTO")
    private boolean autorizacionComprasAuto;
    @Column(name = "FECHA_AUTORIZO_FINANZAS")
    @Temporal(TemporalType.DATE)
    private Date fechaAutorizoFinanzas;
    @Column(name = "HORA_AUTORIZO_FINANZAS")
    @Temporal(TemporalType.TIME)
    private Date horaAutorizoFinanzas;

    @Column(name = "AUTORIZACION_FINANZAS_AUTO")
    private boolean autorizacionFinanzasAuto;
    @Column(name = "FECHA_AUTORIZO_MPG")
    @Temporal(TemporalType.DATE)
    private Date fechaAutorizoMpg;
    @Column(name = "HORA_AUTORIZO_MPG")
    @Temporal(TemporalType.TIME)
    private Date horaAutorizoMpg;

    @Column(name = "AUTORIZACION_MPG_AUTO")
    private boolean autorizacionMpgAuto;

    @Column(name = "FECHA_AUTORIZO_IHSA")
    @Temporal(TemporalType.DATE)
    private Date fechaAutorizoIhsa;
    @Column(name = "HORA_AUTORIZO_IHSA")
    @Temporal(TemporalType.TIME)
    private Date horaAutorizoIhsa;

    @Column(name = "AUTORIZACION_IHSA_AUTO")
    private boolean autorizacionIhsaAuto;

    @JoinColumn(name = "AUTORIZA_LICITACION", referencedColumnName = "ID")
    @ManyToOne
    private Usuario autorizaLicitacion;
    @Column(name = "FECHA_AUTORIZO_LICITACION")
    @Temporal(TemporalType.DATE)
    private Date fechaAutorizoLicitacion;
    @Column(name = "HORA_AUTORIZO_LICITACION")
    @Temporal(TemporalType.TIME)
    private Date horaAutorizoLicitacion;

    @Column(name = "AUTORIZACION_LICITACION_AUTO")
    private boolean autorizacionLicitacionAuto;

    @Column(name = "FECHA_CANCELO")
    @Temporal(TemporalType.DATE)
    private Date fechaCancelo;
    @Column(name = "HORA_CANCELO")
    @Temporal(TemporalType.TIME)
    private Date horaCancelo;
    @Lob
    @Column(name = "MOTIVO_CANCELO")
    private String motivoCancelo;

    @Column(name = "RECHAZADA")
    private boolean rechazada;
    @Column(name = "FECHA_SOLICITO")
    @Temporal(TemporalType.DATE)
    private Date fechaSolicito;
    @Column(name = "HORA_SOLICITO")
    @Temporal(TemporalType.TIME)
    private Date horaSolicito;
    @JoinColumn(name = "AUTORIZA_IHSA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario autorizaIhsa;
    @JoinColumn(name = "AUTORIZA_GERENCIA", referencedColumnName = "ID")
    @ManyToOne
    private Usuario autorizaGerencia;
    @JoinColumn(name = "AUTORIZA_FINANZAS", referencedColumnName = "ID")
    @ManyToOne
    private Usuario autorizaFinanzas;
    @JoinColumn(name = "AUTORIZA_COMPRAS", referencedColumnName = "ID")
    @ManyToOne
    private Usuario autorizaCompras;
    @JoinColumn(name = "AUTORIZA_MPG", referencedColumnName = "ID")
    @ManyToOne
    private Usuario autorizaMpg;
    @JoinColumn(name = "SOLICITO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario solicito;
    @JoinColumn(name = "CANCELO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario cancelo;
    @JoinColumn(name = "ORDEN", referencedColumnName = "ID")
    @OneToOne
    private Orden orden;
    @JoinColumn(name = "ESTATUS", referencedColumnName = "ID")
    @ManyToOne
    private Estatus estatus;
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
//
    @Column(name = "FECHA_ENVIO_PROVEEDOR")
    @Temporal(TemporalType.DATE)
    private Date fechaEnvioProveedor;
    @Column(name = "HORA_ENVIO_PROVEEDOR")
    @Temporal(TemporalType.TIME)
    private Date horaEnvioProveedor;

    @Column(name = "ERROR_ENVIO")
    private boolean errorEnvio;

    @JoinColumn(name = "USUARIO_REVISA_JURIDICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuarioRevisaJuridicoUsuario;
    @Column(name = "FECHA_REVISA_REPSE")
    @Temporal(TemporalType.DATE)
    private Date fechaRevisaRepse;
    @Column(name = "HORA_REVISA_REPSE")
    @Temporal(TemporalType.TIME)
    private Date horaRevisaRepse;
    //
    @Column(name = "FECHA_ACEPTACION_CARTA")
    @Temporal(TemporalType.DATE)
    private Date fechaAceptacionCarta;
    @Column(name = "HORA_ACEPTACION_CARTA")
    @Temporal(TemporalType.TIME)
    private Date horaAceptacionCarta;
    
    @JoinColumn(name = "enviarpdf", referencedColumnName = "ID")
    @ManyToOne
    private Usuario enviarPdf;

    public AutorizacionesOrden() {
    }

    public AutorizacionesOrden(Integer id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AutorizacionesOrden)) {
            return false;
        }
        AutorizacionesOrden other = (AutorizacionesOrden) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.AutorizacionesOrden[ id=" + id + " ]";
    }
}
