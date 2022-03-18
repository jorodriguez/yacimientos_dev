/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.contrato.vo.ConvenioArticuloVo;
import sia.modelo.orden.vo.OcActivoFijoVO;

/**
 *
 * @author mluis
 */
@Setter
@Getter
public class OrdenDetalleVO implements Serializable {

    private int id;
    private double cantidad;
//    private String unidad;
    private double precioUnitario;
    private double importe;
    private String observaciones;
    private int orden;
//    private String moneda;
    private boolean enCatalogo;
    private String ordenConsecutivo;
    private int requisicionDetalle;
//    private int idMoneda;
    private Integer ocTarea;
    private String codeTarea;
    private String nombreTarea;
    private boolean selected;
    private List<OcActivoFijoVO> navCodes;
    private Integer ocUnidadCosto;
    private String ocProductoDesc;
    private Integer ocProductoID;
    private String ocProductoCode;
    private boolean editar = true;

    private String artNumeroParte;
    private String artDescripcion;
    private String artNombre;
    private String artUnidad;
    private int artIdUnidad;
    private int artID;
    private String textNav;
    private double totalRecibido;
    private double totalPendiente;
    private Date fechaRecibido;
    private boolean recibido;
    //
    private int idProyectoOt;
    private String proyectoOt;
    private int idTipoTarea;
    private String tipoTarea;
    private int idSubTarea;
    private String subTarea;
    private int idActividadPetrolera;
    private String actividadPetrolera;
    private String multiProyectos;
    private double descuento;

    private int idAgrupador;
    private String codigoSubTarea;

    private String proyectoOtCC;
    //
    private List<SelectItem> convenios = new ArrayList<>();
    private List<ConvenioArticuloVo> conveniosVo = new ArrayList<>();
    private int idConvenio;
    private String convenio;
    private BigDecimal cantidadFacturada;
    private BigDecimal cantidadPorFacturar;
    private int idCatalogoHidro;

    private int idpresupuesto;
    private int mesPresupuesto;
    private int idOcCodigoSubtarea;
    private int idOcCodigoTarea;
    private String presupuestoNombre;
    private String presupuestoCodigo;
    private int anioPresupuesto;
    private int idRequisicionDetalle;
    
    private String detDescripcion;

    public String getArtDescripcion() {
        
        String ret = "";
        if(this.getArtID() > 0){
            ret = this.artDescripcion;
        } else {
            ret = this.detDescripcion;
        }
        
        if (this.textNav != null && !this.textNav.isEmpty()) {
            ret += " ";
            ret += this.textNav;
        }
        return ret != null ? ret.toUpperCase() : ret;
    }

    public String getTextNav() {
        return this.textNav.toUpperCase();
    }

    public List<ConvenioArticuloVo> getConveniosVo() {
        return this.conveniosVo;
    }

    public void setConveniosVo(List<ConvenioArticuloVo> conveniosVo) {
        this.conveniosVo = conveniosVo;
    }

    public String getArtNombre() {
        return this.artNombre;
    }

    public void setArtNombre(String artNombre) {
        this.artNombre = artNombre;
    }
}
