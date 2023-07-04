/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.ihsa.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jorodriguez
 */
@Getter
@Setter
@Entity
@Table(name = "rep_adjunto_categoria ")
public class RepAdjuntoCategoria implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "si_adjunto_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private SiAdjunto siAdjuntoId;
    @JoinColumn(name = "si_categoria_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private SiCategoria siCategoriaId;
    @Column(name = "fecha")
    private LocalDate fecha;
    @Column(name = "fase")
    private String fase;
    @Column(name = "categorias")
    private String categorias;
    @Column(name = "notas")
    private String notas;
    @Column(name = "archivo_texto")
    private String archivoTexto;
    //
    @NotNull
    @Column(name = "fecha_genero")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaGenero;
    @Column(name = "fecha_modifico")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModifico;
    @Column(name = "eliminado")
    private Boolean eliminado;
    @JoinColumn(name = "genero", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario genero;
    @JoinColumn(name = "modifico", referencedColumnName = "id")
    @ManyToOne
    private Usuario modifico;

    public RepAdjuntoCategoria() {
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RepAdjuntoCategoria)) {
            return false;
        }
        RepAdjuntoCategoria other = (RepAdjuntoCategoria) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.ihsa.modelo.RepAdjuntoCategoria[ id=" + id + " ]";
    }

}
