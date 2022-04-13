/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "CARACTERISTICAS_SERVICIO")
@SequenceGenerator(sequenceName = "caracteristicas_servicio_id_seq", name = "caracteristicas_servicio_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CaracteristicasServicio.findAll", query = "SELECT c FROM CaracteristicasServicio c")})
public class CaracteristicasServicio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "EN_CONVENIO")
    private String enConvenio;
    @Size(max = 35)
    @Column(name = "NUMERO_PARTE")
    private String numeroParte;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PRECIO")
    private Double precio;
    
    @Column(name = "PRINCIPAL")
    private String principal;
    @JoinColumn(name = "UNIDAD", referencedColumnName = "ID")
    @ManyToOne
    private Unidad unidad;
    @JoinColumn(name = "SERVICIO", referencedColumnName = "ID")
    @ManyToOne
    private Servicio servicio;
    @JoinColumn(name = "MONEDA", referencedColumnName = "ID")
    @ManyToOne
    private Moneda moneda;
    @JoinColumn(name = "CONVENIO", referencedColumnName = "ID")
    @ManyToOne
    private Convenio convenio;
    @JoinColumn(name = "CONDICION_PAGO", referencedColumnName = "ID")
    @ManyToOne
    private CondicionPago condicionPago;
    @JoinColumn(name = "CLASIFICACION_SERVICIO", referencedColumnName = "ID")
    @ManyToOne
    private ClasificacionServicio clasificacionServicio;
    @OneToMany(mappedBy = "caracteristicaServicio")
    private Collection<RequisicionDetalle> requisicionDetalleCollection;
    @OneToMany(mappedBy = "servicioPrincipal")
    private Collection<ServicioAdicional> servicioAdicionalCollection;
    @OneToMany(mappedBy = "servicioAdicional")
    private Collection<ServicioAdicional> servicioAdicionalCollection1;

    public CaracteristicasServicio() {
    }

    public CaracteristicasServicio(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEnConvenio() {
        return enConvenio;
    }

    public void setEnConvenio(String enConvenio) {
        this.enConvenio = enConvenio;
    }

    public String getNumeroParte() {
        return numeroParte;
    }

    public void setNumeroParte(String numeroParte) {
        this.numeroParte = numeroParte;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public Unidad getUnidad() {
        return unidad;
    }

    public void setUnidad(Unidad unidad) {
        this.unidad = unidad;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public Convenio getConvenio() {
        return convenio;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }

    public CondicionPago getCondicionPago() {
        return condicionPago;
    }

    public void setCondicionPago(CondicionPago condicionPago) {
        this.condicionPago = condicionPago;
    }

    public ClasificacionServicio getClasificacionServicio() {
        return clasificacionServicio;
    }

    public void setClasificacionServicio(ClasificacionServicio clasificacionServicio) {
        this.clasificacionServicio = clasificacionServicio;
    }

    @XmlTransient
    public Collection<RequisicionDetalle> getRequisicionDetalleCollection() {
        return requisicionDetalleCollection;
    }

    public void setRequisicionDetalleCollection(Collection<RequisicionDetalle> requisicionDetalleCollection) {
        this.requisicionDetalleCollection = requisicionDetalleCollection;
    }

    @XmlTransient
    public Collection<ServicioAdicional> getServicioAdicionalCollection() {
        return servicioAdicionalCollection;
    }

    public void setServicioAdicionalCollection(Collection<ServicioAdicional> servicioAdicionalCollection) {
        this.servicioAdicionalCollection = servicioAdicionalCollection;
    }

    @XmlTransient
    public Collection<ServicioAdicional> getServicioAdicionalCollection1() {
        return servicioAdicionalCollection1;
    }

    public void setServicioAdicionalCollection1(Collection<ServicioAdicional> servicioAdicionalCollection1) {
        this.servicioAdicionalCollection1 = servicioAdicionalCollection1;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CaracteristicasServicio)) {
            return false;
        }
        CaracteristicasServicio other = (CaracteristicasServicio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.CaracteristicasServicio[ id=" + id + " ]";
    }
    
}
