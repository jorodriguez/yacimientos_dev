/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author mluis
 */
@Entity
@Table(name = "SI_LOCALIZACION")
@SequenceGenerator(sequenceName = "si_localizacion_id_seq", name = "si_localizacion_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SiLocalizacion.findAll", query = "SELECT l FROM SiLocalizacion l")})
@Getter
@Setter
@ToString
public class SiLocalizacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "si_localizacion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)

    @Column(name = "ID")
    private Integer id;
    @Size(max = 64)
    @Column(name = "TELEFONO_ID")
    private String telefonoId;

    @Size(max = 64)
    @Column(name = "TELEFONO_SIM_SERIE")
    private String telefonoSimSerie;

    @Size(max = 128)
    @Column(name = "TELEFONO_SIM_OPERADOR")
    private String telefonoSimOperador;

    @Size(max = 32)
    @Column(name = "LONGITUD")
    private String longitud;

    @Size(max = 32)
    @Column(name = "LATITUD")
    private String latitud;

    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @NotNull
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @NotNull
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

    public SiLocalizacion() {
    }

    public SiLocalizacion(int id) {
        this.id = id;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SiLocalizacion)) {
            return false;
        }
        SiLocalizacion other = (SiLocalizacion) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
