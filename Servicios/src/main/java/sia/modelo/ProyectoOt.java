/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import javax.persistence.SequenceGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "PROYECTO_OT")
@SequenceGenerator(sequenceName = "proyecto_ot_id_seq", name = "proyecto_ot_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProyectoOt.findAll", query = "SELECT p FROM ProyectoOt p")})
@Getter
@Setter
public class ProyectoOt implements Serializable {

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
    
    @Column(name = "VISIBLE")
    private boolean visible;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator =  "proyecto_ot_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 100)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 30)
    @Column(name = "CUENTA_CONTABLE")
    private String cuentaContable;
//    
//    @Column(name = "VISIBLE")
//    private String visible;
    @OneToMany(mappedBy = "proyectoOt")
    private Collection<Orden> ordenCollection;
    @JoinColumn(name = "COMPANIA", referencedColumnName = "RFC")
    @ManyToOne(optional = false)
    private Compania compania;
    @OneToMany(mappedBy = "proyectoOt")
    private Collection<RelGerenciaProyecto> relGerenciaProyectoCollection;
    @OneToMany(mappedBy = "proyectoOt")
    private Collection<RelProyectoTipoObra> relProyectoTipoObraCollection;
    @OneToMany(mappedBy = "proyectoOt")
    private Collection<Requisicion> requisicionCollection;

    //actualizacion 19/marzo/2013
    @JoinColumn(name = "AP_CAMPO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private ApCampo apCampo;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;

    
    @Column(name = "ABIERTO")
    private boolean abierto;
    
    @Column(name = "CODIGO")
    private String codigo;
    
    @JoinColumn(name = "OC_SUBCAMPO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private OcSubcampo ocSubcampo;
    
    @JoinColumn(name = "OC_YACIMIENTO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private OcYacimiento ocYacimiento;

    public ProyectoOt() {
    }

    public ProyectoOt(Integer id) {
        this.id = id;
    }
    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProyectoOt)) {
            return false;
        }
        ProyectoOt other = (ProyectoOt) object;
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
                .append("id=").append(this.id)
                .append(", nombre=").append(this.nombre)
                .append(", cuentaContable=").append(this.cuentaContable)
                .append(", compania=").append(this.compania != null ? this.compania.getRfc() : null)
                .append(", abierto=").append(this.abierto)
                .append(", genero=").append(this.genero != null ? this.genero.getId() : null)
                .append(", fechaGenero=").append(this.fechaGenero != null ? (sdfFecha.format(this.fechaGenero)) : null)
                .append(", horaGenero=").append(this.horaGenero != null ? (sdfHora.format(this.horaGenero)) : null)
                .append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null)
                .append(", fechaModifico=").append(this.fechaModifico != null ? (sdfFecha.format(this.fechaModifico)) : null)
                .append(", horaModifico=").append(this.horaModifico != null ? (sdfHora.format(this.horaModifico)) : null)
                .append(", eliminado=").append(this.eliminado)
                .append(", codigo=").append(this.codigo)
                .append(", subcampo=").append(this.getOcSubcampo())
                .append("}");

        return sb.toString();
    }

}
