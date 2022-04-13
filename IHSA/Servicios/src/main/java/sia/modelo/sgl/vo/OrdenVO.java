/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.orden.vo.OrdenEtsVo;

/**
 *
 * @author mluis
 */
@Setter
@Getter
public class OrdenVO implements Serializable {

    private int id;
    private int idAutorizaOrden;
    private int idGerencia;
    private int idProveedor;
    private int idStatus;
    private String consecutivo;
    private String referencia;
    private String requisicion;
    private String consecutivoRequisicion;
    private Date fecha;
    private Date fechaSolicita;
    private String estatus;
    private String proveedor;
    private ContratoVO contratoVO = new ContratoVO();
    private String numeroContrato;
    private Double total;
    private Double totalUsd;
    private String moneda;
    private String compania;
    private Double subTotal;
    private Double iva;
    private boolean devuelta;
    private String gerencia;
    private boolean superaMonto;
    private String nombreProyectoOT;
    private double montoTotalRequisicion;
    private Date fechaAutoriza;
    private boolean selected;
    private Date hora;
    private Date fechaOperacion;
    private String destino;
    //
    private int idRequisicion;
    private String idResponsableGerencia;
    private String responsableGerencia;
    private int idProyectoOt;
    private String cuentaContable;
    private String rfcCompania;
    private String idContactoCompania;
    private String nombreContactoCompania;
    private String idGerenteCompras;
    private String nombreGerenteCompras;
    private String idAnalista;
    private String analista;
    private int idMoneda;
    private String monedaSiglas;
    private Date fechaEntrega;
    private boolean superaRequisicion;
    private boolean conIva;
    private String observaciones;
    private boolean esOC;
    private int idBloque;
    private String bloque;
    private int idTarea;
    private String codigoTarea;
    private String tarea;
    private String porcentajeIva;
    private String nota;
    private int idNombreTarea;
    private Date fechaGenero;
    private String idAprueba;
    //
    private String tipo;
    private String checkCode;
    private String url;
    List<OrdenDetalleVO> detalleOrden = new ArrayList<>();
    List<OrdenVO> listaOrden = new ArrayList<>();
    //
    private int idUnidadCosto;
    private String unidadCosto;
    private String navCode;
    //
    private long totalOrdenes;
    //
    private String motivo;
    private String usuario;
    //
    private long totalDevueltas;
    private long totalCanceladas;
    private boolean leida;
    private boolean errorEnvio;
    List<ContratoVO> listaConvenio = new ArrayList<>();
    List<OrdenEtsVo> listaETS = new ArrayList<>();
    private AdjuntoVO adjuntoETS = new AdjuntoVO();
    private long contratoActivo;
    private double totalItems;
    private double totalRecibidos;
    private double totalPendiente;
    private int diasEntrega;
    //
    private int idFormaPago;
    private String formaPago;
    private int idTipoCompra;
    private String tipoCompra;
    private boolean conConvenio;
    //
    private Date fechaEnvioProveedor;
    private double descuento;
    private long totalFactura;
    private boolean multiproyecto;
    private Date inicioEjecucion;
    private Date finEjecucion;
    private int idCfdi;
    private String nombreCfdi;
    private String codigoCfdi;
    private String urlRequisicion;
    private double precioU;
    private String unidad;
    //
    private String proyectoOt;
    private Double totalProyectoOt;
    private String usuarioReq;    
    //
    private String terminoPago;
    //
    private boolean proveedorRepse;
    private long totalCartaIntencionRechazadas;
    private String proveedorRfc;
    private boolean repse;
    private boolean fcreada;
    private String repseTxt;
    private Date fechaRevisaRepse;
    private Date fechaAceptaCarta;
}
