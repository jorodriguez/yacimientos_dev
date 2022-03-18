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
import sia.constantes.Constantes;

/**
 *
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_DIRECCION")
@SequenceGenerator(sequenceName = "sg_direccion_id_seq", name = "sg_direccion_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgDireccion.findAll", query = "SELECT s FROM SgDireccion s")})
public class SgDireccion implements Serializable {
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
    @Size(max = 64)
    @Column(name = "ESTADO")
    private String estado;
    @Size(max = 128)
    @Column(name = "CIUDAD")
    private String ciudad;
    @OneToMany(mappedBy = "sgDireccion")
    private Collection<SgOficina> sgOficinaCollection;

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_direccion_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 128)
    @Column(name = "COLONIA")
    private String colonia;
    @Size(max = 128)
    @Column(name = "CALLE")
    private String calle;
    @Size(max = 8)
    @Column(name = "NUMERO_EXTERIOR")
    private String numeroExterior;
    @Size(max = 8)
    @Column(name = "NUMERO_INTERIOR")
    private String numeroInterior;
    @Size(max = 8)
    @Column(name = "PISO")
    private String piso;
    @Size(max = 8)
    @Column(name = "CODIGO_POSTAL")
    private String codigoPostal;
    @Size(max = 128)
    @Column(name = "MUNICIPIO")
    private String municipio;
    @JoinColumn(name = "SI_PAIS", referencedColumnName = "ID")
    @ManyToOne
    private SiPais siPais;
    @JoinColumn(name = "SI_ESTADO", referencedColumnName = "ID")
    @ManyToOne
    private SiEstado siEstado;
    @JoinColumn(name = "SI_CIUDAD", referencedColumnName = "ID")
    @ManyToOne
    private SiCiudad siCiudad;
    
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;

    public SgDireccion() {
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
     * @return the colonia
     */
    public String getColonia() {
        return colonia;
    }

    /**
     * @param colonia the colonia to set
     */
    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    /**
     * @return the calle
     */
    public String getCalle() {
        return calle;
    }

    /**
     * @param calle the calle to set
     */
    public void setCalle(String calle) {
        this.calle = calle;
    }

    /**
     * @return the numeroExterior
     */
    public String getNumeroExterior() {
        return numeroExterior;
    }

    /**
     * @param numeroExterior the numeroExterior to set
     */
    public void setNumeroExterior(String numeroExterior) {
        this.numeroExterior = numeroExterior;
    }

    /**
     * @return the numeroInterior
     */
    public String getNumeroInterior() {
        return numeroInterior;
    }

    /**
     * @param numeroInterior the numeroInterior to set
     */
    public void setNumeroInterior(String numeroInterior) {
        this.numeroInterior = numeroInterior;
    }

    /**
     * @return the piso
     */
    public String getPiso() {
        return piso;
    }

    /**
     * @param piso the piso to set
     */
    public void setPiso(String piso) {
        this.piso = piso;
    }

    /**
     * @return the codigoPostal
     */
    public String getCodigoPostal() {
        return codigoPostal;
    }

    /**
     * @param codigoPostal the codigoPostal to set
     */
    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

//    /**
//     * @return the estado
//     */
//    public String getEstado() {
//        return estado;
//    }
//
//    /**
//     * @param estado the estado to set
//     */
//    public void setEstado(String estado) {
//        this.estado = estado;
//    }
    /**
     * @return the municipio
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * @param municipio the municipio to set
     */
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

//    /**
//     * @return the ciudad
//     */
//    public String getCiudad() {
//        return ciudad;
//    }
//
//    /**
//     * @param ciudad the ciudad to set
//     */
//    public void setCiudad(String ciudad) {
//        this.ciudad = ciudad;
//    }
    /**
     * @return the siPais
     */
    public SiPais getSiPais() {
        return siPais;
    }

    /**
     * @param siPais the siPais to set
     */
    public void setSiPais(SiPais siPais) {
        this.siPais = siPais;
    }

    /**
     * @return the siEstado
     */
    public SiEstado getSiEstado() {
        return siEstado;
    }

    /**
     * @param siEstado the siEstado to set
     */
    public void setSiEstado(SiEstado siEstado) {
        this.siEstado = siEstado;
    }

    /**
     * @return the siCiudad
     */
    public SiCiudad getSiCiudad() {
        return siCiudad;
    }

    /**
     * @param siCiudad the siCiudad to set
     */
    public void setSiCiudad(SiCiudad siCiudad) {
        this.siCiudad = siCiudad;
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

    
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SgDireccion)) {
            return false;
        }
        SgDireccion other = (SgDireccion) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append("{");
        sb.append("id=").append(getId());
        sb.append(", colonia=").append(getColonia());
        sb.append(", calle=").append(getCalle());
        sb.append(", numeroExterior=").append(getNumeroExterior());
        sb.append(", numeroInterior=").append(getNumeroInterior());
        sb.append(", piso=").append(getPiso());
        sb.append(", codigoPostal=").append(getCodigoPostal());
//            sb.append(", estado=").append(getEstado());
        sb.append(", municipio=").append(getMunicipio());
//            sb.append(", ciudad=").append(getCiudad());
        sb.append(", siPais=").append(getSiPais() != null ? getSiPais().getId() : null);
        sb.append(", siEstado=").append(getSiEstado() != null ? getSiEstado().getId() : null);
        sb.append(", siCiudad=").append(getSiCiudad() != null ? getSiCiudad().getId() : null);
        sb.append(", genero=").append(getGenero() != null ? getGenero().getId() : null);
        sb.append(", fechaGenero=").append(getFechaGenero() != null ? (Constantes.FMT_ddMMyyy.format(getFechaGenero())) : null);
        sb.append(", horaGenero=").append(getHoraGenero() != null ? (Constantes.FMT_HHmmss.format(getHoraGenero())) : null);
        sb.append(", modifico=").append(getModifico() != null ? getModifico().getId() : null);
        sb.append(", fechaModifico=").append(getFechaModifico() != null ? (Constantes.FMT_ddMMyyy.format(getFechaModifico())) : null);
        sb.append(", horaModifico=").append(getHoraModifico() != null ? (Constantes.FMT_HHmmss.format(getHoraModifico())) : null);
        sb.append(", eliminado=").append(isEliminado());
        sb.append("}");

        return sb.toString();
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Collection<SgOficina> getSgOficinaCollection() {
        return sgOficinaCollection;
    }

    public void setSgOficinaCollection(Collection<SgOficina> sgOficinaCollection) {
        this.sgOficinaCollection = sgOficinaCollection;
    }

}