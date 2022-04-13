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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "CO_NOTICIA")
@SequenceGenerator(sequenceName = "co_noticia_id_seq", name = "co_noticia_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
@NamedQuery(name = "CoNoticia.findAll", query = "SELECT c FROM CoNoticia c")})
public class CoNoticia implements Serializable {
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
    @OneToMany(mappedBy = "coNoticia")
    private Collection<SgSolicitudViaje> sgSolicitudViajeCollection;
    @OneToMany(mappedBy = "coNoticia")
    private Collection<SgEstadoSemaforo> sgEstadoSemaforoCollection;
    @OneToMany(mappedBy = "coNoticia")
    private Collection<SgViaje> sgViajeCollection;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "co_noticia_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Column(name = "ID_ELEMENTO")
    private Integer idElemento;
    @Size(max = 200)
    @Column(name = "TITULO")
    private String titulo;
    @Lob    
    @Column(name = "MENSAJE")
    private String mensaje;
    @Lob
    @Column(name = "MENSAJE_AUTOMATICO")
    private String mensajeAutomatico;
    @Column(name = "COMENTARIOS")
    private Integer comentarios;
    @Column(name = "MEGUSTA")
    private Integer megusta;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @OneToMany(mappedBy = "coNoticia")
    private Collection<CoComentario> coComentarioCollection;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SI_OPCION", referencedColumnName = "ID")
    @ManyToOne
    private SiOpcion siOpcion;
    @JoinColumn(name = "CO_PRIVACIDAD", referencedColumnName = "ID")
    @ManyToOne
    private CoPrivacidad coPrivacidad;
    
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;

    public CoNoticia() {
    }

    public CoNoticia(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(Integer idElemento) {
        this.idElemento = idElemento;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensajeAutomatico() {
        return mensajeAutomatico;
    }

    public void setMensajeAutomatico(String mensajeAutomatico) {
        this.mensajeAutomatico = mensajeAutomatico;
    }

    public Integer getComentarios() {
        return comentarios;
    }

    public void setComentarios(Integer comentarios) {
        this.comentarios = comentarios;
    }

    public Integer getMegusta() {
        return megusta;
    }

    public void setMegusta(Integer megusta) {
        this.megusta = megusta;
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
    public Collection<CoComentario> getCoComentarioCollection() {
        return coComentarioCollection;
    }

    public void setCoComentarioCollection(Collection<CoComentario> coComentarioCollection) {
        this.coComentarioCollection = coComentarioCollection;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public SiOpcion getSiOpcion() {
        return siOpcion;
    }

    public void setSiOpcion(SiOpcion siOpcion) {
        this.siOpcion = siOpcion;
    }

    public CoPrivacidad getCoPrivacidad() {
        return coPrivacidad;
    }

    public void setCoPrivacidad(CoPrivacidad coPrivacidad) {
        this.coPrivacidad = coPrivacidad;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CoNoticia)) {
            return false;
        }
        CoNoticia other = (CoNoticia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
//            .append("id=").append(this.getId())                
//            .append(", titulo=").append(this.titulo)
//            .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
//            .append(", fechaGenero=").append(this.fechaGenero != null ? (sdfFecha.format(this.fechaGenero)) : null)
//            .append(", horaGenero=").append(this.horaGenero != null ? (sdfHora.format(this.horaGenero)) : null)
//            .append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
//            .append(", fechaModifico=").append(this.fechaModifico != null ? (sdfFecha.format(this.fechaModifico)) : null)
//            .append(", horaModifico=").append(this.horaModifico != null ? (sdfHora.format(this.horaModifico)) : null)
//            .append(", eliminado=").append(this.eliminado)
            .append("}");
        
        return sb.toString();
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


    public Collection<SgSolicitudViaje> getSgSolicitudViajeCollection() {
        return sgSolicitudViajeCollection;
    }

    public void setSgSolicitudViajeCollection(Collection<SgSolicitudViaje> sgSolicitudViajeCollection) {
        this.sgSolicitudViajeCollection = sgSolicitudViajeCollection;
    }

    public Collection<SgEstadoSemaforo> getSgEstadoSemaforoCollection() {
        return sgEstadoSemaforoCollection;
    }

    public void setSgEstadoSemaforoCollection(Collection<SgEstadoSemaforo> sgEstadoSemaforoCollection) {
        this.sgEstadoSemaforoCollection = sgEstadoSemaforoCollection;
    }

    public Collection<SgViaje> getSgViajeCollection() {
        return sgViajeCollection;
    }

    public void setSgViajeCollection(Collection<SgViaje> sgViajeCollection) {
        this.sgViajeCollection = sgViajeCollection;
    }

  
    
}
