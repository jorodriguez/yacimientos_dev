/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.ReRequisicionEts;
import sia.modelo.requisicion.vo.RequisicionDetalleVO;
import sia.modelo.requisicion.vo.RequisicionEtsVo;
import sia.util.UtilSia;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class RequisicionVO {

    private int id;
    private String consecutivo;
    private String referencia;
    private Double montoDolares;
    private Double montoTotalDolares;
    private String prioridad;
    private Double montoPesos;
    private Date fechaSolicitada;
    private Date fechaRequerida;
    private String compania;
    private String proveedor;
    private String estatus;
    private boolean rechazada;
    private boolean selected = false;
    private String gerencia;
    private String proyectoOT;
    private String comprador;
    private Date fechaAsignada;
    //
    private String rfcCompania;
    private int idStatus;
    private String lugarEntrega;
    private int idGerencia;
    private int idProyectoOT;
    private String tipoObra;
    private int idTipoObra;
    private String observaciones;
    private int idBloque;
    private String bloque;
    //

    private String idSolicita;
    private String solicita;
    private String idRevisa;
    private String revisa;
    private String idAprueba;
    private String aprueba;
    private String idAsigna;
    private String asigna;
    private String idVistoBueno; //costos
    private String vistoBueno; //costos
    private String idComprador;
    private Date horaSolicitada;
    private Date fechaRevisa;
    private Date horaRevisa;
    private Date fechaVistoBueno;
    private Date horaVistoBueno;
    private Date fechaAprueba;
    private Date horaAprueba;
    private Date horaAsignada;
    private Date fechaGenero;
    private int idPrioridad;
    private long totalItemEnOrden;
    private long totalItemSinOrden;
    private long totalItems;
    //
    private int idTarea;
    private int idNombreTarea;
    private String codigoTarea;
    private String nombreTarea;
    private String cadena;
    private String tipo;
    private String unidadCosto;
    //
    private String checkCode;
    private String url;
    private int idUnidadCosto;
    private long total;
    //
    private String cuentaOt;
    private Date ultimoRechazo;
    private int idCfdi;
    private String nombreCfdi;
    private String codigoCfdi;
    
    private Boolean nueva;

    // detalle
    List<RequisicionDetalleVO> listaDetalleRequision = new ArrayList<>();
    
    // lista de ets
    private List<RequisicionEtsVo> listaEts = new ArrayList<>();

    public RequisicionVO() {
    }
    
    public RequisicionVO(int id) {
        this.id = id;
    }    
    
    public String getJson() {
	return UtilSia.getGson().toJson(this);
    }
    
       
}
