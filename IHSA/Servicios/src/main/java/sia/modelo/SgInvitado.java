/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author jrodriguez
 */
@Entity
@Table(name = "SG_INVITADO")
@SequenceGenerator(sequenceName = "sg_invitado_id_seq", name = "sg_invitado_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgInvitado.findAll", query = "SELECT i FROM SgInvitado i")})
public class SgInvitado implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_invitado_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 60)
    @Column(name = "NOMBRE")
    private String nombre; 
    @Size(max = 60)
    @Column(name = "EMAIL")
    private String email;    
    @JoinColumn(name = "SG_EMPRESA", referencedColumnName = "ID")
    @ManyToOne
    private SgEmpresa sgEmpresa;
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
    @Size(max = 25)
    @Column(name = "TELEFONO")
    private String telefono;

    public SgInvitado() {
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the sgEmpresa
     */
    public SgEmpresa getSgEmpresa() {
        return sgEmpresa;
    }

    /**
     * @param sgEmpresa the sgEmpresa to set
     */
    public void setSgEmpresa(SgEmpresa sgEmpresa) {
        this.sgEmpresa = sgEmpresa;
    }

    /**
     * @return the genero
     */
    public Usuario getGenero() {
        return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    /**
     * @return the fechaGenero
     */
    public Date getFechaGenero() {
        return fechaGenero;
    }

    /**
     * @param fechaGenero the fechaGenero to set
     */
    public void setFechaGenero(Date fechaGenero) {
        this.fechaGenero = fechaGenero;
    }

    /**
     * @return the horaGenero
     */
    public Date getHoraGenero() {
        return horaGenero;
    }

    /**
     * @param horaGenero the horaGenero to set
     */
    public void setHoraGenero(Date horaGenero) {
        this.horaGenero = horaGenero;
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

    /**
     * @return the eliminado
     */
    public boolean isEliminado() {
        return eliminado;
    }

    /**
     * @param eliminado the eliminado to set
     */
    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
    /**
     * @return the telefono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * @param telefono the telefono to set
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String toString() {
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName())
            .append("{")
            .append("id=").append(this.getId())
            .append(", nombre=").append(this.getNombre())
            .append(", email=").append(this.getEmail())
            .append(", sgEmpresa=").append(this.getSgEmpresa() != null ? this.getSgEmpresa().getId() : null)
            .append(", genero=").append(this.getGenero() != null ? this.getGenero().getId() : null)
            .append(", fechaGenero=").append(this.getFechaGenero() != null ? (sdfFecha.format(this.getFechaGenero())) : null)
            .append(", horaGenero=").append(this.getHoraGenero() != null ? (sdfHora.format(this.getHoraGenero())) : null)
            .append(", modifico=").append(this.getModifico() != null ? this.getModifico().getId() : null)
            .append(", fechaModifico=").append(this.getFechaModifico() != null ? (sdfFecha.format(this.getFechaModifico())) : null)
            .append(", horaModifico=").append(this.getHoraModifico() != null ? (sdfHora.format(this.getHoraModifico())) : null)
            .append(", eliminado=").append(this.isEliminado())
            .append(", telefono=").append(this.getTelefono())
            .append("}");
        return sb.toString();
    }
}