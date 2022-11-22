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
 * @author mluis
 */
@Entity
@Table(name = "ORDEN_SI_MOVIMIENTO")
@SequenceGenerator(sequenceName = "orden_si_movimiento_id_seq", name = "orden_si_movimiento_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "OrdenSiMovimiento.findAll", query = "SELECT o FROM OrdenSiMovimiento o")})
public class OrdenSiMovimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "orden_si_movimiento_seq", strategy = GenerationType.SEQUENCE)
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
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "SI_MOVIMIENTO", referencedColumnName = "ID")
    @ManyToOne
    private SiMovimiento siMovimiento;
    @JoinColumn(name = "ORDEN", referencedColumnName = "ID")
    @ManyToOne
    private Orden orden;    
    @JoinColumn(name = "SOLICITA_DEVOLUCION", referencedColumnName = "ID")
    @ManyToOne
    private Usuario solicitaDevolucion;

    public OrdenSiMovimiento() {
    }

    public OrdenSiMovimiento(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Usuario getModifico() {
        return modifico;
    }

    public void setModifico(Usuario modifico) {
        this.modifico = modifico;
    }

    public Usuario getGenero() {
        return genero;
    }

    public void setGenero(Usuario genero) {
        this.genero = genero;
    }

    public SiMovimiento getSiMovimiento() {
        return siMovimiento;
    }

    public void setSiMovimiento(SiMovimiento siMovimiento) {
        this.siMovimiento = siMovimiento;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrdenSiMovimiento)) {
            return false;
        }
        OrdenSiMovimiento other = (OrdenSiMovimiento) object;
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
                .append("{").append("id=")
                .append(this.id).append(", orden=").append(this.orden != null ? orden.getId() : null)
                .append(", siMovimiento=").append(this.siMovimiento != null ? siMovimiento.getId() : null)
                .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
                .append(", solicitaDevolucion=").append(this.solicitaDevolucion != null ? this.solicitaDevolucion.getId() : null)
                .append(", fechaGenero=").append(this.fechaGenero != null ? (sdfFecha.format(this.fechaGenero)) : null)
                .append(", horaGenero=").append(this.horaGenero != null ? (sdfHora.format(this.horaGenero)) : null)
                .append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
                .append(", fechaModifico=").append(this.fechaModifico != null ? (sdfFecha.format(this.fechaModifico)) : null)
                .append(", horaModifico=").append(this.horaModifico != null ? (sdfHora.format(this.horaModifico)) : null)
                .append(", eliminado=").append(this.eliminado)
                .append("}");
        return sb.toString();
    }


    /**
     * @return the solicitaDevolucion
     */
    public Usuario getSolicitaDevolucion() {
        return solicitaDevolucion;
    }

    /**
     * @param solicitaDevolucion the solicitaDevolucion to set
     */
    public void setSolicitaDevolucion(Usuario solicitaDevolucion) {
        this.solicitaDevolucion = solicitaDevolucion;
    }
    
}
