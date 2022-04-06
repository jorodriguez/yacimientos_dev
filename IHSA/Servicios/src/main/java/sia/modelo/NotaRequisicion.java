/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
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
@Table(name = "NOTA_REQUISICION")
@SequenceGenerator(sequenceName = "nota_requisicion_id_seq", name = "nota_requisicion_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NotaRequisicion.findAll", query = "SELECT n FROM NotaRequisicion n")})
public class NotaRequisicion implements Serializable {

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
    @JoinColumn(name = "OC_SUBCAMPO", referencedColumnName = "ID")
    @ManyToOne
    private OcSubcampo ocSubcampo;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "nota_requisicion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 100)
    @Column(name = "TITULO")
    private String titulo;
    @Lob
    
    @Column(name = "MENSAJE")
    private String mensaje;
    @Column(name = "FECHA")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "HORA")
    @Temporal(TemporalType.TIME)
    private Date hora;
    @Column(name = "RESPUESTAS")
    private Integer respuestas;
    @Column(name = "IDENTIFICADOR")
    private Integer identificador;
    @Column(name = "ULT_RESPUESTA")
    @Temporal(TemporalType.DATE)
    private Date ultRespuesta;
    
    @Column(name = "FINALIZADA")
    private String finalizada;
    @OneToMany(mappedBy = "notaRequisicion")
    private Collection<InvitadosNotaRequisicion> invitadosNotaRequisicionCollection;
    @JoinColumn(name = "AUTOR", referencedColumnName = "ID")
    @ManyToOne
    private Usuario autor;
    @JoinColumn(name = "REQUISICION", referencedColumnName = "ID")
    @ManyToOne
    private Requisicion requisicion;

     //actualizacion 19/marzo/2013
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private ApCampo apCampo;
    
    public NotaRequisicion() {
    }

    public NotaRequisicion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public Integer getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(Integer respuestas) {
        this.respuestas = respuestas;
    }

    public Integer getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Integer identificador) {
        this.identificador = identificador;
    }

    public Date getUltRespuesta() {
        return ultRespuesta;
    }

    public void setUltRespuesta(Date ultRespuesta) {
        this.ultRespuesta = ultRespuesta;
    }

    public String getFinalizada() {
        return finalizada;
    }

    public void setFinalizada(String finalizada) {
        this.finalizada = finalizada;
    }

    @XmlTransient
    public Collection<InvitadosNotaRequisicion> getInvitadosNotaRequisicionCollection() {
        return invitadosNotaRequisicionCollection;
    }

    public void setInvitadosNotaRequisicionCollection(Collection<InvitadosNotaRequisicion> invitadosNotaRequisicionCollection) {
        this.invitadosNotaRequisicionCollection = invitadosNotaRequisicionCollection;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public Requisicion getRequisicion() {
        return requisicion;
    }

    public void setRequisicion(Requisicion requisicion) {
        this.requisicion = requisicion;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NotaRequisicion)) {
            return false;
        }
        NotaRequisicion other = (NotaRequisicion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "sia.modelo.NotaRequisicion[ id=" + id + " ]";
    }

    /**
     * @return the apCampo
     */
    public ApCampo getApCampo() {
        return apCampo;
    }

    /**
     * @param apCampo the apCampo to set
     */
    public void setApCampo(ApCampo apCampo) {
        this.apCampo = apCampo;
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

    public Date getFechaModifico() {
        return fechaModifico;
    }

    public void setFechaModifico(Date fechaModifico) {
        this.fechaModifico = fechaModifico;
    }

    public Date getHoraModifico() {
        return horaModifico;
    }

    public void setHoraModifico(Date horaModifico) {
        this.horaModifico = horaModifico;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public OcSubcampo getOcSubcampo() {
        return ocSubcampo;
    }

    public void setOcSubcampo(OcSubcampo ocSubcampo) {
        this.ocSubcampo = ocSubcampo;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public Usuario getModifico() {
        return modifico;
    }

    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
    }
    
}
