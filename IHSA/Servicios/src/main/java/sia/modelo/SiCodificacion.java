/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "SI_CODIFICACION")
@SequenceGenerator(sequenceName = "si_codificacion_id_seq", name = "si_codificacion_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SiCodificacion.findAll", query = "SELECT c FROM SiCodificacion c")})
public class SiCodificacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "si_codificacion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "MODIFICAR")
    private boolean modificar;
    
    @Column(name = "INTEGRA_ANO")
    private String integraAno;
    @Size(max = 3)
    @Column(name = "LETRAS")
    private String letras;
    @Column(name = "INICIO")
    private Integer inicio;
    @Column(name = "VALOR_ACTUAL")
    private Integer valorActual;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @OneToMany(mappedBy = "siCodificacion")
    private Collection<SiTipoNotificacion> siTipoNotificacionCollection;
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

    public SiCodificacion() {
    }

    public SiCodificacion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isModificar() {
        return modificar;
    }

    public void setModificar(boolean modificar) {
        this.modificar = modificar;
    }

    public String getIntegraAno() {
        return integraAno;
    }

    public void setIntegraAno(String integraAno) {
        this.integraAno = integraAno;
    }

    public String getLetras() {
        return letras;
    }

    public void setLetras(String letras) {
        this.letras = letras;
    }

    public Integer getInicio() {
        return inicio;
    }

    public void setInicio(Integer inicio) {
        this.inicio = inicio;
    }

    public Integer getValorActual() {
        return valorActual;
    }

    public void setValorActual(Integer valorActual) {
        this.valorActual = valorActual;
    }

    public Date getFechaGenero() {
        return fechaGenero;
    }

    public void setFechaGenero(Date fechaGenero) {
        this.fechaGenero = fechaGenero;
    }

    public Date getHoraGenero() {
        return horaGenero;
    }

    public void setHoraGenero(Date horaGenero) {
        this.horaGenero = horaGenero;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    @XmlTransient
    public Collection<SiTipoNotificacion> getSiTipoNotificacionCollection() {
        return siTipoNotificacionCollection;
    }

    public void setSiTipoNotificacionCollection(Collection<SiTipoNotificacion> siTipoNotificacionCollection) {
        this.siTipoNotificacionCollection = siTipoNotificacionCollection;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiCodificacion)) {
            return false;
        }
        SiCodificacion other = (SiCodificacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * @return the modifico
     */
    public Usuario getModifico() {
        return modifico;
    }

    /**
     * @param modifico the modifico to set
     */
    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
    }

    /**
     * @return the fechaModifico
     */
    public Date getFechaModifico() {
        return fechaModifico;
    }

    /**
     * @param fechaModifico the fechaModifico to set
     */
    public void setFechaModifico(Date fechaModifico) {
        this.fechaModifico = fechaModifico;
    }

    /**
     * @return the horaModifico
     */
    public Date getHoraModifico() {
        return horaModifico;
    }

    /**
     * @param horaModifico the horaModifico to set
     */
    public void setHoraModifico(Date horaModifico) {
        this.horaModifico = horaModifico;
    }
    
    
    public String toString() {
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
            .append("id=").append(this.id)
            .append(",integraAno=").append(this.integraAno)
            .append(",letras=").append(this.letras)
            .append(",inicio=").append(this.inicio)
            .append(",valorActual=").append(this.valorActual)
            .append(",genero=").append(this.getGenero() != null ? this.getGenero().getId() : null)
            .append(",fechaGenero=").append(this.fechaGenero != null ? (sdfFecha.format(this.fechaGenero)) : null)
            .append(",horaGenero=").append(this.horaGenero != null ? (sdfHora.format(this.horaGenero)) : null)
            .append(",modifico=").append(this.modifico != null ? this.modifico.getId() : null)
            .append(",fechaModifico=").append(this.fechaModifico != null ? (sdfFecha.format(this.fechaModifico)) : null)
            .append(",horaModifico=").append(this.horaModifico != null ? (sdfHora.format(this.horaModifico)) : null)
            .append(",eliminado=").append(this.eliminado)
            .append("}");
        
        return sb.toString();
    }

    
}
