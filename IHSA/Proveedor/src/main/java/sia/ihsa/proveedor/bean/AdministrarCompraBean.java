package sia.ihsa.proveedor.bean;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.faces.model.SelectItem;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.ihsa.admin.Sesion;
import sia.ihsa.utils.FacesUtilsBean;
import sia.modelo.SiAdjunto;
import sia.modelo.SiCatalogoHidrocarburo;
import sia.modelo.SiFactura;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sistema.vo.FacturaAdjuntoVo;
import sia.modelo.sistema.vo.FacturaContenidoNacionalVo;
import sia.modelo.sistema.vo.FacturaDetalleVo;
import sia.modelo.sistema.vo.FacturaMovimientoVo;
import sia.modelo.sistema.vo.FacturaVo;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.catalogos.impl.ParidadValorImpl;
import sia.servicios.orden.impl.OrdenDetalleImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.orden.impl.SiCatalogoHidrocarburoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaContenidoNacionalImpl;
import sia.servicios.sistema.impl.SiFacturaDetalleImpl;
import sia.servicios.sistema.impl.SiFacturaMovimientoImpl;
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.servicios.sistema.impl.SiFacturaStatusImpl;
import sia.servicios.sistema.vo.ParidadValorVO;
import sia.util.FacturaEstadoEnum;
import sia.util.TipoArchivoFacturaEnum;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;
import org.primefaces.PrimeFaces;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javax.faces.view.ViewScoped;
import org.apache.commons.io.FilenameUtils;
import javax.inject.Named;
import javax.inject.Inject;
import sia.util.Env;

/**
 *
 * @author mluis
 */
@Named(value = "administrarCompraBean")
@ViewScoped
public class AdministrarCompraBean implements Serializable {

    private static final String SOPORTE_NOTA_CREDITO = "soporteNotaCredito";
    private static final String FACTURA_PDF = "facturaPDF";
    private static final String NOTA_CREDITO_XML = "notaCreditoXML";
    private static final String NOTA_CREDITO_PDF = "notaCreditoPDF";
    private static final String SOPORTE_FACTURA = "soporteFactura";
    private static final String CARTA_CONTENIDO = "cartaContenido";
    private static final String ARCHIVO_FACTURA_XML = "archivoFacturaXML";
    private static final String DOCUMENTO_ADUANAL = "documentoAduanalpdf";

    private static final String ID_COMPRA = "idCompra";

    private static final String FACTURA_MODAL_HIDE = ";$(dialogoArchivoFactura).modal('hide');";
    private static final String ARCHIVO_FACT_FILE_ENTRY = "frmArchivoFact:file-entry";
    private static final String DIA_ARCH_FACT_MODALSHOW = ";$(dialogoArchivoFactura).modal('show');";

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @Inject
    private Sesion sesion;
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private SiFacturaImpl siFacturaImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    private SiFacturaAdjuntoImpl siFacturaAdjuntoImpl;
    @Inject
    private SiCatalogoHidrocarburoImpl siCatalogoHidrocarburoImpl;
    @Inject
    private SiFacturaDetalleImpl siFacturaDetalleImpl;
    @Inject
    private OrdenDetalleImpl ordenDetalleImpl;
    @Inject
    private SiFacturaStatusImpl siFacturaStatusImpl;
    @Inject
    private MonedaImpl monedaImpl;
    @Inject
    private ParidadValorImpl paridadValorImpl;
    @Inject
    private SiFacturaContenidoNacionalImpl facturaContenidoNacionalImpl;
    @Inject
    private SiFacturaMovimientoImpl siFacturaMovimientoImpl;
    //
    @Getter
    @Setter
    private OrdenVO compraVo;
    @Getter
    @Setter
    private FacturaVo facturaVo = new FacturaVo();
    @Getter
    @Setter
    private FacturaVo notaCreditoVo = new FacturaVo();
    @Getter
    @Setter
    private FacturaContenidoNacionalVo facturaContenidoNacionalVo = new FacturaContenidoNacionalVo();
    //

    @Getter
    @Setter
    private String pathArchivo;
    @Getter
    @Setter
    private List<FacturaDetalleVo> listaFacturaDetalle;
    @Getter
    @Setter
    private List<FacturaAdjuntoVo> listaSoporteFactura;
    @Getter
    @Setter
    private List<FacturaAdjuntoVo> listaSoporteFacturaXmlPdf;
    @Getter
    @Setter
    private List<FacturaContenidoNacionalVo> contenidoNacionalPorFactura;
    @Getter
    @Setter
    private List<SelectItem> listaCatagoloHidro;
    @Getter
    @Setter
    private List<SelectItem> listaOrdenDetalle;
    @Getter
    @Setter
    private Map<String, Boolean> tipoCarga;

    @Getter
    @Setter
    private List<OrdenDetalleVO> listaDetalleOrden;
    @Getter
    @Setter
    private BigDecimal contenidoNacional;
    @Getter
    @Setter
    private BigDecimal montoContenidoNacional;
    @Getter
    @Setter
    private int idCatalogoHidro;
    //
    @Getter
    @Setter
    private List<FacturaVo> listaNotaCredito;
    @Getter
    @Setter
    private List<FacturaDetalleVo> listaNotaDetalle;
    @Getter
    @Setter
    private List<FacturaAdjuntoVo> listaSoporteNotaCredito;

    @Getter
    @Setter
    private List<FacturaMovimientoVo> movimientosFactura;

    @Getter
    @Setter
    private double totalAcumuladoContenidoNacional;
    @Getter
    @Setter
    private double saldoContenidoNacional;
    @Getter
    @Setter
    private String montoCn;
    @Getter
    @Setter
    private Date maxDate;

    @PostConstruct
    public void iniciar() {
        setTipoCarga(new HashMap<>());
        listaCatagoloHidro = new ArrayList<>();
        listaCatagoloHidro.add(new SelectItem(Constantes.CERO, "Seleccione . . . "));
        //
        ExternalContext ex = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> map = ex.getSessionMap();

        String paramOcs = Env.getContext(sesion.getCtx(), ID_COMPRA);
        //
        String paramFact = Env.getContext(sesion.getCtx(), "idFactura");
        int value;
        if (!Strings.isNullOrEmpty(paramOcs)) {
            Env.removeContext(sesion.getCtx(), ID_COMPRA);
            value = Integer.parseInt(paramOcs);
            compraVo = ordenImpl.buscarOrdenPorId(value, 0, Boolean.FALSE);

            facturaVo = siFacturaImpl.buscarPorOrden(compraVo.getId(), FacturaEstadoEnum.CREADA.getId());

            map.put(ID_COMPRA, null);
        }

        if (!Strings.isNullOrEmpty(paramFact)) {
            Env.removeContext(sesion.getCtx(), "idFactura");
            value = Integer.parseInt(paramFact);

            facturaVo = siFacturaImpl.buscarFactura(value);
            compraVo = ordenImpl.buscarOrdenPorId(facturaVo.getIdRelacion(), facturaVo.getIdCampo(), Constantes.BOOLEAN_FALSE);
            map.put(ID_COMPRA, null);
        }

        if (map.get(ID_COMPRA) != null) {
            Object o = map.get(ID_COMPRA);
            value = Integer.parseInt(o.toString());
            compraVo = ordenImpl.buscarOrdenPorId(value, 0, Boolean.FALSE);
            facturaVo = siFacturaImpl.buscarPorOrden(compraVo.getId(), FacturaEstadoEnum.CREADA.getId());
        }

        if (compraVo != null) {
            compraVo.setDetalleOrden(new ArrayList<>());
            if (compraVo.isMultiproyecto()) {
                compraVo.setDetalleOrden(ordenDetalleImpl.traerDetalleOrdenAgrupadoMultiProyecto(compraVo.getId()));
            } else {
                compraVo.setDetalleOrden(ordenDetalleImpl.itemsPorOrden(compraVo.getId()));
            }
        }
        // listaFactura = siFacturaImpl.traerFacturaPorOrden(compraVo.getId(), FacturaEstadoEnum.CREADA.getId());
        if (facturaVo == null) {
            facturaVo = new FacturaVo();
        } else {
            listaFacturaDetalle = new ArrayList<>();
            List<FacturaDetalleVo> ltemp = siFacturaDetalleImpl.traerDetalle(facturaVo.getId());
            for (FacturaDetalleVo facturaDetalleVo : ltemp) {
                if (!facturaDetalleVo.getDescripcion().isEmpty()) {
                    listaFacturaDetalle.add(facturaDetalleVo);
                }
            }

            listaSoporteFacturaXmlPdf = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), Constantes.BOOLEAN_TRUE, "'XML (Factura)', 'PDF (Factura)'");

            listaSoporteFactura = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), Constantes.BOOLEAN_TRUE);

            // Nota de credito
            listaNotaCredito = siFacturaImpl.traerNotaCredito(facturaVo.getId());
            if (facturaVo.getIdAdjunto() > 0) {
                agregarCarta(facturaVo);
            }

            movimientosFactura = siFacturaMovimientoImpl.movimientos(facturaVo.getId());
        }

        // llenar catalogo hidro
        for (SiCatalogoHidrocarburo catHidro : siCatalogoHidrocarburoImpl.traerCatalogo()) {
            listaCatagoloHidro.add(new SelectItem(catHidro.getId(), catHidro.getCodigo() + " - " + catHidro.getNombre()));
        }
        //
        contenidoNacionalPorFactura = facturaContenidoNacionalImpl.contedinoNacionaPorFactura(facturaVo.getId());
    }

    public void inicioRegistroFacturaExtranjera() {
        maxDate = new Date();
        facturaVo.setMonto(BigDecimal.ZERO);
        PrimeFaces.current().executeScript("$(dialogoRegistroFacturaExtr).modal('show');");
    }

    public void inicioRegistroFacturaXML() {
        tipoCarga.put(ARCHIVO_FACTURA_XML, Boolean.TRUE);
        tipoCarga.put(CARTA_CONTENIDO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_FACTURA, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(NOTA_CREDITO_XML, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(FACTURA_PDF, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(NOTA_CREDITO_PDF, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_NOTA_CREDITO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(DOCUMENTO_ADUANAL, Constantes.BOOLEAN_FALSE);
        FacesUtilsBean.addErrorMessage(ARCHIVO_FACT_FILE_ENTRY, "");

        PrimeFaces.current().executeScript(DIA_ARCH_FACT_MODALSHOW);
    }

    public void inicioRegistroNotaCreditoXML() {
        tipoCarga.put(NOTA_CREDITO_XML, Constantes.BOOLEAN_TRUE);
        tipoCarga.put(ARCHIVO_FACTURA_XML, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(CARTA_CONTENIDO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_FACTURA, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(FACTURA_PDF, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(NOTA_CREDITO_PDF, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_NOTA_CREDITO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(DOCUMENTO_ADUANAL, Constantes.BOOLEAN_FALSE);
        FacesUtilsBean.addErrorMessage(ARCHIVO_FACT_FILE_ENTRY, "");
        PrimeFaces.current().executeScript(DIA_ARCH_FACT_MODALSHOW);
    }

    public void inicioRegistroFactura() {
        //validaciones
        double totalFacturado = siFacturaImpl.totalPorOrden(compraVo.getId());

        if (totalFacturado < compraVo.getSubTotal()) {
            facturaVo = new FacturaVo();
            facturaVo.setIdProveedor(sesion.getProveedorVo().getIdProveedor());
            facturaVo.setIdMoneda(compraVo.getIdMoneda());
            facturaVo.setIdRelacion(compraVo.getId());

            PrimeFaces.current().executeScript(";$(dialogoRegFactura).modal('show');");
        } else {
            FacesUtilsBean.addErrorMessage("Ya se facturó el total de la orden de compra.");
        }
    }

    public void registroFactura() {
        //List<OrdenDetalleVO> li = ordenDetalleImpl.traerOrdenNoFacturado(compraVo.getId());
        double totalFacturado = siFacturaImpl.totalPorOrden(compraVo.getId());
        facturaVo.setIdCampo(compraVo.getIdBloque());
        facturaVo.setCompania(compraVo.getCompania());
        facturaVo.setRfcCompania(compraVo.getRfcCompania());
        facturaVo.setIdMoneda(compraVo.getIdMoneda());
        facturaVo.setIdProveedor(compraVo.getIdProveedor());
        //
        if (BigDecimal.valueOf(totalFacturado).compareTo(BigDecimal.valueOf(compraVo.getSubTotal())) < 1) {
            if (facturaVo.getId() == 0) {
                siFacturaImpl.guardarFactura(facturaVo, sesion.getProveedorVo().getRfc(), new ArrayList<OrdenDetalleVO>(), compraVo.getTipo());
            } else {
                siFacturaImpl.modificarFactura(facturaVo, sesion.getProveedorVo().getRfc());
            }
        } else {
            FacesUtilsBean.addErrorMessage("La factura supera el monto de la orden de compra, es necesario corregir el monto total de la factura.");
        }
        // relacionar la factura con la orden de compra

        PrimeFaces.current().executeScript(";$(dialogoRegFactura).modal('hide');");
    }

    public void cerrarRegistroFactura() {
        PrimeFaces.current().executeScript(";$(dialogoRegFactura).modal('hide');");
    }

    public void seleccionarFactura() {
        int idFact = Integer.parseInt(FacesUtilsBean.getRequestParameter("idFac"));
        listaFacturaDetalle = siFacturaDetalleImpl.traerDetalle(idFact);
        listaSoporteFactura = siFacturaAdjuntoImpl.traerSoporteFactura(idFact, Constantes.BOOLEAN_TRUE);
        //
        facturaVo = siFacturaImpl.buscarFactura(idFact);
        if (facturaVo.getIdAdjunto() > 0) {
            agregarCarta(facturaVo);
        }
        PrimeFaces.current().executeScript(";seleccinarFactura('divProcesoFactura','divFactura');");

    }

    private void agregarCarta(FacturaVo facturaVo) {

        FacturaAdjuntoVo fav = new FacturaAdjuntoVo();
        fav.setId(0);
        fav.setTipo("Carta CN");
        fav.setIdFactura(facturaVo.getId());
        fav.setAdjuntoVo(new AdjuntoVO());
        fav.getAdjuntoVo().setId(facturaVo.getIdAdjunto());
        fav.getAdjuntoVo().setNombre(facturaVo.getAdjunto());
        fav.getAdjuntoVo().setUuid(facturaVo.getUuId());
        //
        listaSoporteFactura.add(fav);
    }

    public void regresarTablaFactura() {
        PrimeFaces.current().executeScript(";seleccinarFactura('divFactura','divProcesoFactura');");
    }

    public void iniciarAgragarContNac() {
        totalAcumuladoContenidoNacional
                = facturaContenidoNacionalImpl.totalPorFactura(facturaVo.getId()).doubleValue();
        //
        saldoContenidoNacional
                = facturaVo.getSubTotalPesos().subtract(BigDecimal.valueOf(totalAcumuladoContenidoNacional)).doubleValue();
        //
        PrimeFaces.current().executeScript(";$(dialogoContNac).modal('show');");
    }

    public void eliminarContNac(int idFacConNac) {
        //
        facturaContenidoNacionalImpl.eliminar(sesion.getProveedorVo().getRfc(), idFacConNac);
        //
        contenidoNacionalPorFactura = facturaContenidoNacionalImpl.contedinoNacionaPorFactura(facturaVo.getId());
    }

    public void agregarContenidoNacional() {
        //
        try {
            Preconditions.checkState(
                    idCatalogoHidro > 0,
                    "Seleccione un concepto del catálogo de hidrocarburos."
            );

            Preconditions.checkState(
                    contenidoNacional != null,
                    "Agregue el Contenido Nacional."
            );

            Preconditions.checkState(
                    contenidoNacional.doubleValue() >= 0.0 && contenidoNacional.doubleValue() <= 1,
                    "Solo se aceptan en Proporción de Contenido Nacional valores entre 0 y 1."
            );
            Preconditions.checkState(
                    !montoCn.trim().isEmpty(),
                    "Agregue el Monto Acumulado para el Contenido Nacional."
            );

            try {
                montoContenidoNacional = new BigDecimal(montoCn);
            } catch (Exception e) {
                FacesUtilsBean.addErrorMessage("En el Monto Acumulado, solo se aceptan números y punto decimal, No comas(,).");
            }

            Preconditions.checkState(
                    montoContenidoNacional != null
                    && montoContenidoNacional.doubleValue() > Constantes.CERO,
                    "El monto de Contenido Nacional debe mayor a cero."
            );

            double totalPesos
                    = montoContenidoNacional.add(facturaContenidoNacionalImpl.totalPorFactura(facturaVo.getId())).doubleValue();

            Preconditions.checkState(
                    totalPesos <= facturaVo.getSubTotalPesos().doubleValue(),
                    "La suma de los montos de Contenido Nacional NO debe ser mayor al subtotal de la factura."
            );

            facturaContenidoNacionalVo.setIdCatHidro(idCatalogoHidro);
            facturaContenidoNacionalVo.setProporcionContenido(contenidoNacional);
            facturaContenidoNacionalVo.setMontoFacturado(montoContenidoNacional);
            //Aqui hace el cambio
            facturaContenidoNacionalImpl.guardar(sesion.getProveedorVo().getRfc(), facturaVo.getId(), getFacturaContenidoNacionalVo());
            //
            contenidoNacionalPorFactura = facturaContenidoNacionalImpl.contedinoNacionaPorFactura(facturaVo.getId());
            FacesUtilsBean.addInfoMessage("Se agregó el Contenido Nacional.");
            contenidoNacional = BigDecimal.ZERO;
            montoContenidoNacional = BigDecimal.ZERO;//0.0;
            montoCn = "";
            //
            totalAcumuladoContenidoNacional = totalPesos;
            //
            saldoContenidoNacional
                    = facturaVo.getSubTotalPesos()
                            .subtract(BigDecimal.valueOf(totalAcumuladoContenidoNacional)).doubleValue();

        } catch (IllegalStateException e) {
            FacesUtilsBean.addErrorMessage(e.getMessage());
        }
    }

    public void cerrarAgregarContenido() {
        idCatalogoHidro = 0;
        montoContenidoNacional = BigDecimal.ZERO;//0.0;
        contenidoNacional = BigDecimal.ZERO;
        montoCn = "";
        //
        PrimeFaces.current().executeScript(";$(dialogoContNac).modal('hide');");
    }

    private boolean fechaEmisionValida(Date fechEmision) {
        boolean ret = false;
        Calendar cE = Calendar.getInstance();
        cE.setTime(fechEmision);
        Calendar cH = Calendar.getInstance();
        ret = (cE.get(Calendar.MONTH) - cH.get(Calendar.MONTH) == 0) && (cE.get(Calendar.DAY_OF_MONTH) <= 20);
        return ret;
    }

    public String procesarFactura() {
        // validaciones para enviar        
        String pagina = "";
        boolean tieneOcDet = true;
        boolean validaContenido = true;

        for (FacturaDetalleVo fdv : listaFacturaDetalle) {
            if (fdv.getIdOrdenDetalle() == 0) {
                tieneOcDet = false;
                break;
            }
        }
        if (sesion.getProveedorVo().isCarta()) {
            double totalCN = facturaContenidoNacionalImpl.totalPorFactura(facturaVo.getId()).doubleValue();
            //out.println("Total: pesos: " + totalCN);
            //out.println("tots: fac: " + facturaVo.getSubTotalPesos().doubleValue());

            if (facturaVo.getSubTotalPesos().doubleValue() != totalCN) {
                validaContenido = false;
            }
        }

        try {
            Preconditions.checkState(
                    validaContenido,
                    "El subtotal de la factura y el total declarado para para la carta de de Contenido Nacional no coinciden."
            );

//            Preconditions.checkState(
//                    fechaEmisionValida(facturaVo.getFechaEmision()),
//                    "Las facturas deben ser emitidas e ingresadas dentro del mismo mes. La fecha límite de ingreso de las facturas es el día 20 de cada mes."
//            );
            Preconditions.checkState(
                    !contenidoNacionalPorFactura.isEmpty(),
                    "Es necesario agregar el código de catálogo de hidrocarbos de la carta de contenido nacional."
            );

            Preconditions.checkState(
                    tieneOcDet,
                    "Es necesario conciliar todas las partidas de la orden de compra con las partidas de la facutura."
            );

            Preconditions.checkState(
                    !listaSoporteFactura.isEmpty(),
                    "Es necesario agregar documentos físicos de la factura."
            );

            Preconditions.checkState(
                    listaSoporteFacturaXmlPdf.size() >= 2 && listaSoporteFactura.size() >= 3,
                    "Es necesario agregar XML, PDF, Soportes/Evidencias y cuando se requiera también la carta de Contenido Nacional."
            );

            boolean continuar = false;
            listaNotaCredito = siFacturaImpl.traerNotaCredito(facturaVo.getId());
            if (siFacturaImpl.requiereNC(facturaVo.getId())) {
                if (listaNotaCredito.size() > 0) {
                    for (FacturaVo ncVO : listaNotaCredito) {
                        if (ncVO.getSoportesNC().size() >= 2) {
                            continuar = true;
                        } else {
                            continuar = false;
                        }
                    }
                } else {
                    continuar = false;
                }
            } else {
                if (listaNotaCredito.size() > 0) {
                    for (FacturaVo ncVO : listaNotaCredito) {
                        if (ncVO.getSoportesNC().size() >= 2) {
                            continuar = true;
                        } else {
                            continuar = false;
                        }
                    }
                } else {
                    continuar = true;
                }
            }

            Preconditions.checkState(
                    continuar,
                    "Es necesario agregar una NOTA DE CRÉDITO por retención como se especifica en el contrato. Archivos obligatorios XML(Nota Credito) y PDF(Nota Credito)."
            );

            siFacturaStatusImpl.procesarFactura(sesion.getProveedorVo(), facturaVo, compraVo);
            //
            pagina = "/principal";
        } catch (IllegalStateException e) {
            FacesUtilsBean.addErrorMessage(e.getMessage());
        }

        return pagina;
    }

    public String procesarFacturaExtranjera() {
        String pagina = "";
        try {
            Preconditions.checkState(
                    listaSoporteFactura != null && listaSoporteFactura.size() >= 2,
                    "Es necesario agregar pdf y el documento de la agencia aduanal."
            );
            siFacturaStatusImpl.procesarFacturaExtranjera(sesion.getProveedorVo(), facturaVo, compraVo);
            pagina = "/principal";
        } catch (IllegalStateException e) {
            FacesUtilsBean.addErrorMessage(e.getMessage());
        } catch (Exception e) {
            FacesUtilsBean.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
        return pagina;

    }

    public String eliminarFactura() {
        siFacturaImpl.eliminarFactura(facturaVo.getId(), FacturaEstadoEnum.CREADA.getId(), sesion.getProveedorVo().getRfc());
        //
        return "/principal";
    }

    public void iniciarModificarFactura() {
        PrimeFaces.current().executeScript(";$(dialogoRegFactura).modal('show');");
    }

    public void modificarFactura() {
        siFacturaImpl.modificarFactura(facturaVo, sesion.getProveedorVo().getRfc());
        PrimeFaces.current().executeScript(";$(dialogoRegFactura).modal('hide');");
    }

    //    
    public void eliminarSoporteFactura(int idFacSop) {
        if (idFacSop > 0) {
            siFacturaAdjuntoImpl.eliminar(idFacSop, sesion.getProveedorVo().getRfc());
        } else {
            //
            siFacturaImpl.quitarArchivo(facturaVo.getId(), sesion.getProveedorVo().getRfc());
            //
            facturaVo.setIdAdjunto(Constantes.CERO);
        }

        listaSoporteFactura
                = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), Constantes.BOOLEAN_TRUE);
        //
        listaSoporteFacturaXmlPdf = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), Constantes.BOOLEAN_TRUE, "'XML (Factura)', 'PDF (Factura)'");
        if (facturaVo.getIdAdjunto() > Constantes.CERO) {
            agregarCarta(facturaVo);
        }
    }

    public void iniciarCargaPDF() {
        tipoCarga.put(ARCHIVO_FACTURA_XML, Boolean.FALSE);
        tipoCarga.put(NOTA_CREDITO_XML, Boolean.FALSE);
        tipoCarga.put(CARTA_CONTENIDO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_NOTA_CREDITO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(FACTURA_PDF, Constantes.BOOLEAN_TRUE);
        tipoCarga.put(NOTA_CREDITO_PDF, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_FACTURA, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(DOCUMENTO_ADUANAL, Constantes.BOOLEAN_FALSE);
        PrimeFaces.current().executeScript(DIA_ARCH_FACT_MODALSHOW);
        FacesUtilsBean.addErrorMessage(ARCHIVO_FACT_FILE_ENTRY, "");
    }

    //
    public void iniciarCargaSoporte() {
        tipoCarga.put(ARCHIVO_FACTURA_XML, Boolean.FALSE);
        tipoCarga.put(NOTA_CREDITO_XML, Boolean.FALSE);
        tipoCarga.put(CARTA_CONTENIDO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_NOTA_CREDITO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(FACTURA_PDF, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(DOCUMENTO_ADUANAL, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(FACTURA_PDF, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(NOTA_CREDITO_PDF, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_FACTURA, Constantes.BOOLEAN_TRUE);
        PrimeFaces.current().executeScript(DIA_ARCH_FACT_MODALSHOW);
        FacesUtilsBean.addErrorMessage(ARCHIVO_FACT_FILE_ENTRY, "");
    }

    public void uploadFile(FileUploadEvent fileEvent) {

        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        try {
            UploadedFile fileInfo = fileEvent.getFile();

            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());
            if (addArchivo) {
                //
                if (tipoCarga.get(CARTA_CONTENIDO)) {
                    procesarCartaContenido(fileInfo);
                } else if (tipoCarga.get(FACTURA_PDF)) {
                    procesarFacturaPdf(fileInfo);
                } else if (tipoCarga.get(SOPORTE_FACTURA)) {
                    procesarSoporteFactura(fileInfo);
                } else if (tipoCarga.get(NOTA_CREDITO_XML)) {
                    procesarNotaCredXml(fileInfo);
                } else if (tipoCarga.get(NOTA_CREDITO_PDF)) {
                    procesarPDFNotaCred(fileInfo);
                } else if (tipoCarga.get(SOPORTE_NOTA_CREDITO)) {
                    procesarSoporteNotaCred(fileInfo);
                } else if (tipoCarga.get(DOCUMENTO_ADUANAL)) {
                    procesarFacturaExtranjeraPdf(fileInfo);
                } else {
                    procesarXml(fileInfo);
                }

                fileInfo.delete();

            } else {
                FacesUtilsBean.addErrorMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

        } catch (Exception e) {
            LOGGER.error(this, e);
            FacesUtilsBean.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }

    }

    private void procesarSoporteNotaCred(UploadedFile fileInfo) {
        SiAdjunto adj;
        adj = guardarAdjunto(fileInfo);
        //
        siFacturaAdjuntoImpl.guardar(adj, notaCreditoVo.getId(), TipoArchivoFacturaEnum.TIPO_SOPORTE.toString(), sesion.getProveedorVo().getRfc());
//        listaSoporteNotaCredito = siFacturaAdjuntoImpl.traerSoporteFactura(notaCreditoVo.getId(), Constantes.BOOLEAN_FALSE);
        listaSoporteNotaCredito = siFacturaAdjuntoImpl.traerSoporteFactura(notaCreditoVo.getId(), Constantes.BOOLEAN_FALSE, "'PDF (Nota Credito)', 'XML (Nota Credito)'");
        notaCreditoVo.setSoportesNC(listaSoporteNotaCredito);
        notaCreditoVo.setSoportesNCSize(listaSoporteNotaCredito.size());
        listaSoporteNotaCredito.addAll(siFacturaAdjuntoImpl.traerSoporteFactura(notaCreditoVo.getId(), Constantes.BOOLEAN_FALSE, "'SOPORTES'"));

        FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");
        //
        PrimeFaces.current().executeScript(FACTURA_MODAL_HIDE);
    }

    private void procesarPDFNotaCred(UploadedFile fileInfo) {
        SiAdjunto adj;
        adj = guardarAdjunto(fileInfo);
        //
        siFacturaAdjuntoImpl.guardar(adj, notaCreditoVo.getId(), TipoArchivoFacturaEnum.TIPO_NC_PDF.toString(), sesion.getProveedorVo().getRfc());
//        listaSoporteNotaCredito = siFacturaAdjuntoImpl.traerSoporteFactura(notaCreditoVo.getId(), Constantes.BOOLEAN_FALSE);
        listaSoporteNotaCredito = siFacturaAdjuntoImpl.traerSoporteFactura(notaCreditoVo.getId(), Constantes.BOOLEAN_FALSE, "'PDF (Nota Credito)', 'XML (Nota Credito)'");
        notaCreditoVo.setSoportesNC(listaSoporteNotaCredito);
        notaCreditoVo.setSoportesNCSize(listaSoporteNotaCredito.size());
        listaSoporteNotaCredito.addAll(siFacturaAdjuntoImpl.traerSoporteFactura(notaCreditoVo.getId(), Constantes.BOOLEAN_FALSE, "'SOPORTES'"));
        FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");
        //
        PrimeFaces.current().executeScript(FACTURA_MODAL_HIDE);
    }

    private void procesarSoporteFactura(UploadedFile fileInfo) {
        SiAdjunto adj = guardarAdjunto(fileInfo);
        //
        siFacturaAdjuntoImpl.guardar(
                adj,
                facturaVo.getId(),
                TipoArchivoFacturaEnum.TIPO_SOPORTE.toString(),
                sesion.getProveedorVo().getRfc()
        );

        listaSoporteFactura
                = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), Constantes.BOOLEAN_TRUE);

        FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");
        if (facturaVo.getIdAdjunto() > 0) {
            agregarCarta(facturaVo);
        }
        PrimeFaces.current().executeScript(FACTURA_MODAL_HIDE);
    }

    private void procesarFacturaPdf(UploadedFile fileInfo) {
        SiAdjunto adj = guardarAdjunto(fileInfo);
        //
        siFacturaAdjuntoImpl.guardar(adj, facturaVo.getId(), TipoArchivoFacturaEnum.TIPO_PDF.toString(), sesion.getProveedorVo().getRfc());
        listaSoporteFacturaXmlPdf = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), Constantes.BOOLEAN_TRUE, "'XML (Factura)', 'PDF (Factura)'");
        listaSoporteFactura = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), Constantes.BOOLEAN_TRUE);
        FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");
        if (facturaVo.getIdAdjunto() > 0) {
            agregarCarta(facturaVo);
        }
        PrimeFaces.current().executeScript(FACTURA_MODAL_HIDE);
    }

    private void procesarCartaContenido(UploadedFile fileInfo) {

        //out.println("file.type" + fileInfo.getContentType());
        if (fileInfo.getContentType().equals("application/pdf")) {
            SiAdjunto adj = guardarAdjunto(fileInfo);

            siFacturaImpl.agregarArchivo(facturaVo.getId(), adj.getId(), sesion.getProveedorVo().getRfc());

            facturaVo.setIdAdjunto(adj.getId());
            facturaVo.setAdjunto(adj.getNombre());
            facturaVo.setUuId(adj.getUuid());

            agregarCarta(facturaVo);

            PrimeFaces.current().executeScript(FACTURA_MODAL_HIDE);
        } else {
            FacesUtilsBean.addInfoMessage(ARCHIVO_FACT_FILE_ENTRY, "La carta de Contenido Nacional debe ser un archivo en formato PDF.");
        }
    }

    private File getFileFromUploadedFile(UploadedFile uploadedFileX) {
        try {
            Path tmpFile = Files.createTempFile(FilenameUtils.getBaseName(uploadedFileX.getFileName()), "." + FilenameUtils.getExtension(uploadedFileX.getFileName()));
            Files.copy(uploadedFileX.getInputStream(), tmpFile, StandardCopyOption.REPLACE_EXISTING);
            return tmpFile.toFile();
        } catch (IOException e) {
            FacesUtilsBean.addErrorMessage(ARCHIVO_FACT_FILE_ENTRY, e.getMessage());
            return null;
        }
    }

    private void procesarNotaCredXml(UploadedFile fileInfo) {
        //Registro de la nota de credito
        try {
            siFacturaImpl.validarXmlNotaCred(getFileFromUploadedFile(fileInfo), facturaVo);

            notaCreditoVo = new FacturaVo();
            notaCreditoVo.setIdProveedor(sesion.getProveedorVo().getIdProveedor());
            notaCreditoVo.setIdMoneda(compraVo.getIdMoneda());
            notaCreditoVo.setIdRelacion(compraVo.getId());
            notaCreditoVo.setIdCampo(compraVo.getIdBloque());
            notaCreditoVo.setCompania(compraVo.getCompania());
            notaCreditoVo.setRfcCompania(compraVo.getRfcCompania());
            notaCreditoVo.setIdFactura(facturaVo.getId());

            int idNotaCredito
                    = siFacturaImpl.cargarFactura(
                            notaCreditoVo,
                            getFileFromUploadedFile(fileInfo),
                            sesion.getProveedorVo().getRfc(),
                            compraVo.getReferencia(),
                            compraVo.getNavCode(),
                            compraVo.getTipo()
                    );
            notaCreditoVo = siFacturaImpl.buscarNotaCredito(idNotaCredito);

            SiAdjunto adj = guardarAdjunto(fileInfo);
            siFacturaAdjuntoImpl.guardar(
                    adj,
                    idNotaCredito,
                    TipoArchivoFacturaEnum.TIPO_NC_XML.toString(),
                    sesion.getProveedorVo().getRfc()
            );

            listaNotaCredito = siFacturaImpl.traerNotaCredito(facturaVo.getId());

            PrimeFaces.current().executeScript(FACTURA_MODAL_HIDE);
        } catch (EJBException | IllegalStateException e) {
            FacesUtilsBean.addErrorMessage(ARCHIVO_FACT_FILE_ENTRY, e.getMessage());
        }
    }

    private void procesarXml(UploadedFile fileInfo) {
        try {
            siFacturaImpl.validarFactura(getFileFromUploadedFile(fileInfo), compraVo);

            SiAdjunto adj = guardarAdjunto(fileInfo);
            if (adj == null) {
                FacesUtilsBean.addErrorMessage(
                        ARCHIVO_FACT_FILE_ENTRY,
                        "No se cargó el archivo de la factura, por favor envie un correo soportesia@ihsa.mx."
                );
            } else {
                facturaVo.setIdProveedor(sesion.getProveedorVo().getIdProveedor());
                facturaVo.setIdMoneda(compraVo.getIdMoneda());
                facturaVo.setIdRelacion(compraVo.getId());
                facturaVo.setIdCampo(compraVo.getIdBloque());
                facturaVo.setCompania(compraVo.getCompania());
                facturaVo.setRfcCompania(compraVo.getRfcCompania());

                int idFact = siFacturaImpl.cargarFactura(
                        facturaVo,
                        getFileFromUploadedFile(fileInfo),
                        sesion.getProveedorVo().getRfc(),
                        compraVo.getReferencia(),
                        compraVo.getNavCode(),
                        compraVo.getTipo()
                );

                facturaVo = siFacturaImpl.buscarFactura(idFact);

                siFacturaAdjuntoImpl.guardar(
                        adj,
                        idFact,
                        TipoArchivoFacturaEnum.TIPO_XML.toString(),
                        sesion.getProveedorVo().getRfc()
                );
                listaSoporteFacturaXmlPdf = siFacturaAdjuntoImpl.traerSoporteFactura(idFact, Constantes.BOOLEAN_TRUE, "'XML (Factura)', 'PDF (Factura)'");
                listaSoporteFactura = siFacturaAdjuntoImpl.traerSoporteFactura(idFact, Constantes.BOOLEAN_TRUE);
                listaFacturaDetalle = siFacturaDetalleImpl.traerDetalle(idFact);

                PrimeFaces.current().executeScript(FACTURA_MODAL_HIDE);
            }
        } catch (IllegalStateException | EJBException e) {
            PrimeFaces.current().executeScript("$(msjValidando).css('visibility', 'hidden')");
            FacesUtilsBean.addErrorMessage(ARCHIVO_FACT_FILE_ENTRY, e.getMessage());
        }
    }

    public void registrarFacturaExtranjera() {

        facturaVo.setIdProveedor(sesion.getProveedorVo().getIdProveedor());
        facturaVo.setIdMoneda(compraVo.getIdMoneda());
        facturaVo.setIdRelacion(compraVo.getId());
        facturaVo.setIdCampo(compraVo.getIdBloque());
        facturaVo.setCompania(compraVo.getCompania());
        facturaVo.setRfcCompania(compraVo.getRfcCompania());
        //
        facturaVo.setConcepto(compraVo.getReferencia());
        facturaVo.setCodigoUsoCfdi(compraVo.getCodigoCfdi());
        facturaVo.setSubTotal(facturaVo.getMonto());
        //
        //.println("Moneda: " + compraVo.getMonedaSiglas() + " fecha: " + Constantes.FMT_ddMMyyy.format(new Date()) + " moneda Id" + compraVo.getIdMoneda());
        ParidadValorVO parVal = paridadValorImpl.traerParidadValorMonedaUSD(compraVo.getMonedaSiglas(), Constantes.FMT_ddMMyyy.format(new Date()), compraVo.getIdMoneda());
        if (parVal != null) {
            facturaVo.setTipoCambio(new BigDecimal(parVal.getValor()));
        }

        SiFactura fact = siFacturaImpl.guardarFactura(facturaVo, sesion.getProveedorVo().getRfc(), new ArrayList<OrdenDetalleVO>(),
                compraVo.getTipo()
        );

        facturaVo = siFacturaImpl.buscarFactura(fact.getId());
        listaSoporteFacturaXmlPdf = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), Constantes.BOOLEAN_TRUE);
        //
        PrimeFaces.current().executeScript("$(dialogoRegistroFacturaExtr).modal('hide');");

    }

    private void procesarFacturaExtranjeraPdf(UploadedFile fileInfo) {
        try {
            SiAdjunto adj = guardarAdjunto(fileInfo);
            if (adj == null) {
                FacesUtilsBean.addErrorMessage(
                        ARCHIVO_FACT_FILE_ENTRY,
                        "No se cargó el archivo de la factura, por favor envie un correo soportesia@ihsa.mx."
                );
            } else {
                siFacturaAdjuntoImpl.guardar(
                        adj,
                        facturaVo.getId(),
                        TipoArchivoFacturaEnum.TIPO_DOCUMENTO_ADUANAL.toString(),
                        sesion.getProveedorVo().getRfc()
                );

                listaSoporteFactura = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), Constantes.BOOLEAN_TRUE);
                listaSoporteFacturaXmlPdf = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), Constantes.BOOLEAN_TRUE);
                PrimeFaces.current().executeScript(FACTURA_MODAL_HIDE);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private SiAdjunto guardarAdjunto(UploadedFile fileInfo) {
        SiAdjunto retVal = null;

        try {
            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
            documentoAnexo.setRuta(uploadDirectory());
            documentoAnexo.setNombreBase(fileInfo.getFileName());
            almacenDocumentos.guardarDocumento(documentoAnexo);
            retVal = siAdjuntoImpl.save(
                    documentoAnexo.getNombreBase(),
                    new StringBuilder()
                            .append(documentoAnexo.getRuta())
                            .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                    fileInfo.getContentType(),
                    fileInfo.getSize(),
                    sesion.getProveedorVo().getRfc()
            );

        } catch (SIAException ex) {
            LOGGER.warn(this, ex);
        }

        return retVal;
    }

    public String uploadDirectory() {

        StringBuilder ruta = new StringBuilder("Proveedor/");
        ruta.append(sesion.getProveedorVo().getRfc());

        if (tipoCarga.get(CARTA_CONTENIDO)) {
            ruta.append("/ContenidoNacional");
        } else if (tipoCarga.get(SOPORTE_FACTURA)) {
            ruta.append("/Soportes");
        } else {
            ruta.append("/Factura");
        }

        return ruta.toString();
    }

    public void cerrarCargaSoporte() {
        PrimeFaces.current().executeScript(FACTURA_MODAL_HIDE);
    }

    public void iniciarCartaContenido() {
        totalAcumuladoContenidoNacional = facturaContenidoNacionalImpl.totalPorFactura(facturaVo.getId()).doubleValue();

        if (facturaVo.getSubTotalPesos().compareTo(BigDecimal.valueOf(totalAcumuladoContenidoNacional)) == 0) {
            tipoCarga.put(ARCHIVO_FACTURA_XML, Boolean.FALSE);
            tipoCarga.put(CARTA_CONTENIDO, Constantes.BOOLEAN_TRUE);
            tipoCarga.put(SOPORTE_FACTURA, Constantes.BOOLEAN_FALSE);
            tipoCarga.put(NOTA_CREDITO_XML, Constantes.BOOLEAN_FALSE);
            //
            PrimeFaces.current().executeScript(DIA_ARCH_FACT_MODALSHOW);
        } else {
            PrimeFaces.current().executeScript("alert('Es necesario agregar el desglose del contenido nacional');");
        }
    }

    public void eliminarCartaContenido() {

    }

    public void inicioGuardarDetalleFactura() {
        PrimeFaces.current().executeScript(";$(dialogoDetalleFactura).modal('show');");
    }

    public void cerrarDetalleFactura() {
        PrimeFaces.current().executeScript(";$(dialogoDetalleFactura).modal('hide');");
    }

    public void eliminarDetalleFactura() {
        int indice = Integer.parseInt(FacesUtilsBean.getRequestParameter("idDetalleFactura"));
        siFacturaDetalleImpl.eliminar(listaFacturaDetalle.get(indice).getId(), sesion.getProveedorVo().getRfc());
        listaFacturaDetalle.remove(Integer.parseInt(FacesUtilsBean.getRequestParameter("idDetalleFactura")));
        facturaVo.setMonto(siFacturaDetalleImpl.totalPorFactura(facturaVo.getId()));
    }

    public void guardarDetalleFactura() {
        List<OrdenDetalleVO> ltem = new ArrayList<>();
        boolean continuar = true;
        //BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (OrdenDetalleVO ordenDetalleVO : listaDetalleOrden) {
            if (ordenDetalleVO.isSelected()) {
                ltem.add(ordenDetalleVO);
                //      total.add(new BigDecimal(ordenDetalleVO.getImporte()));
            }
            if ((ordenDetalleVO.getCantidadPorFacturar().add(ordenDetalleVO.getCantidadFacturada())).compareTo(new BigDecimal(ordenDetalleVO.getCantidad())) > 0) {
                continuar = false;
                break;
            }
        }

        if (ltem.isEmpty()) {
            FacesUtilsBean.addErrorMessage("Es necesario seleccionar al menos una partida. ");
        } else {
            if (continuar) {
                siFacturaDetalleImpl.guardar(facturaVo.getId(), ltem, sesion.getProveedorVo().getRfc());
                listaDetalleOrden = new ArrayList<>();
                listaFacturaDetalle = siFacturaDetalleImpl.traerDetalle(facturaVo.getId());
                //listaFactura = siFacturaImpl.traerFacturaPorOrden(compraVo.getId(), FacturaEstadoEnum.CREADA.getId());
                //
                //    facturaVo.setMonto(total);
                PrimeFaces.current().executeScript(";$(dialogoAgregarDetalleFactura).modal('hide');");
            } else {
                FacesUtilsBean.addErrorMessage("No se puede facturar más de la cantidad solicitada. ");
            }
        }
    }

    public void cerrarAgregarDetalleFactura() {
        PrimeFaces.current().executeScript(";$(dialogoAgregarDetalleFactura).modal('hide');");
    }

    public void inicioRelacionDetOcsDetFac() {
        listaOrdenDetalle = new ArrayList<>();
        listaOrdenDetalle.add(new SelectItem("0", "Seleccione . . . . "));

        DecimalFormat df = new DecimalFormat("#0.00");
//+ " " + Objects.firstNonNull(odVo.getTextNav(), "")
        for (OrdenDetalleVO odVo : compraVo.getDetalleOrden()) {
            String label
                    = odVo.getArtNombre()
                    + (!(odVo.getTextNav().isEmpty()) ? odVo.getTextNav() : "")
                    + " Cantidad: " + df.format(odVo.getCantidad())
                    + " Precio: " + df.format(odVo.getPrecioUnitario());

            listaOrdenDetalle.add(
                    new SelectItem(
                            compraVo.isMultiproyecto() ? ((Integer) odVo.getIdAgrupador()) : ((Integer) odVo.getId()),
                            label
                    )
            );

        }

        PrimeFaces.current().executeScript(";$(dialogoRelacionarDetalleFacturaOcs).modal('show');");
    }

    public void guaradarRelacionDetOcsDetFac() {
        siFacturaDetalleImpl.actualizarOrdenDetalleFactura(listaFacturaDetalle, sesion.getProveedorVo().getRfc(), compraVo.isMultiproyecto(), compraVo.getId());
        PrimeFaces.current().executeScript(";$(dialogoRelacionarDetalleFacturaOcs).modal('hide');");
        listaFacturaDetalle = siFacturaDetalleImpl.traerDetalle(facturaVo.getId());
    }

    public void cerrarRelacionDetOcsDetFac() {
        PrimeFaces.current().executeScript(";$(dialogoRelacionarDetalleFacturaOcs).modal('hide');");
    }

    // NOTA CREDITO
    public void eliminarNotaCredito(int indice) {
        siFacturaImpl.eliminarNotaCredito(listaNotaCredito.get(indice).getId(), FacturaEstadoEnum.CREADA.getId(), sesion.getProveedorVo().getRfc());
        //
        listaNotaCredito.remove(indice);
    }

    public void seleccionarNotaCredito(int idNota) {
        if (listaNotaCredito != null && listaNotaCredito.size() > 0 && idNota > -1) {
            notaCreditoVo = listaNotaCredito.get(idNota);
            //
            listaNotaDetalle = siFacturaDetalleImpl.traerDetalle(notaCreditoVo.getId());
            listaSoporteNotaCredito = siFacturaAdjuntoImpl.traerSoporteFactura(notaCreditoVo.getId(), Constantes.BOOLEAN_FALSE, "'PDF (Nota Credito)', 'XML (Nota Credito)'");
            listaNotaCredito.get(idNota).setSoportesNC(listaSoporteNotaCredito);
            notaCreditoVo.setSoportesNC(listaSoporteNotaCredito);
            notaCreditoVo.setSoportesNCSize(listaSoporteNotaCredito.size());
            listaSoporteNotaCredito.addAll(siFacturaAdjuntoImpl.traerSoporteFactura(notaCreditoVo.getId(), Constantes.BOOLEAN_FALSE, "'SOPORTES'"));
            PrimeFaces.current().executeScript(";$(dialogoDetNotaCredito).modal('show');");
        }
    }

    public void iniciarCargaSoporteNotaCredito() {
        tipoCarga.put(ARCHIVO_FACTURA_XML, Boolean.FALSE);
        tipoCarga.put(NOTA_CREDITO_XML, Boolean.FALSE);
        tipoCarga.put(CARTA_CONTENIDO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(FACTURA_PDF, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_FACTURA, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(NOTA_CREDITO_PDF, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_NOTA_CREDITO, Constantes.BOOLEAN_TRUE);
        tipoCarga.put(DOCUMENTO_ADUANAL, Constantes.BOOLEAN_FALSE);

        PrimeFaces.current().executeScript(DIA_ARCH_FACT_MODALSHOW);
    }

    public void iniciarCargaPDFNotaCredito() {
        tipoCarga.put(ARCHIVO_FACTURA_XML, Boolean.FALSE);
        tipoCarga.put(NOTA_CREDITO_XML, Boolean.FALSE);
        tipoCarga.put(CARTA_CONTENIDO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(FACTURA_PDF, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_FACTURA, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_NOTA_CREDITO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(NOTA_CREDITO_PDF, Constantes.BOOLEAN_TRUE);

        PrimeFaces.current().executeScript(DIA_ARCH_FACT_MODALSHOW);
    }

    public void eliminarSoporteNotaCredito(int idNotaSop) {
        siFacturaAdjuntoImpl.eliminar(idNotaSop, sesion.getProveedorVo().getRfc());
        //
//        listaSoporteNotaCredito = siFacturaAdjuntoImpl.traerSoporteFactura(notaCreditoVo.getId(), Constantes.BOOLEAN_FALSE);
        listaSoporteNotaCredito = siFacturaAdjuntoImpl.traerSoporteFactura(notaCreditoVo.getId(), Constantes.BOOLEAN_FALSE, "'PDF (Nota Credito)', 'XML (Nota Credito)'");
        notaCreditoVo.setSoportesNC(listaSoporteNotaCredito);
        notaCreditoVo.setSoportesNCSize(listaSoporteNotaCredito.size());
        listaSoporteNotaCredito.addAll(siFacturaAdjuntoImpl.traerSoporteFactura(notaCreditoVo.getId(), Constantes.BOOLEAN_FALSE, "'SOPORTES'"));
    }

    public void cerrarDetalleNotaCredito() {
        PrimeFaces.current().executeScript(";$(dialogoDetNotaCredito).modal('hide');");
    }

    public void iniciarCargaDocumentoAduanal() {
        tipoCarga.put(ARCHIVO_FACTURA_XML, Boolean.FALSE);
        tipoCarga.put(NOTA_CREDITO_XML, Boolean.FALSE);
        tipoCarga.put(CARTA_CONTENIDO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_NOTA_CREDITO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(FACTURA_PDF, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(NOTA_CREDITO_PDF, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_FACTURA, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(DOCUMENTO_ADUANAL, Constantes.BOOLEAN_TRUE);
        PrimeFaces.current().executeScript(DIA_ARCH_FACT_MODALSHOW);
        FacesUtilsBean.addErrorMessage(ARCHIVO_FACT_FILE_ENTRY, "");
    }
    /*
        tipoCarga.put(ARCHIVO_FACTURA_XML, Boolean.FALSE);
        tipoCarga.put(NOTA_CREDITO_XML, Boolean.FALSE);
        tipoCarga.put(CARTA_CONTENIDO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_NOTA_CREDITO, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(FACTURA_PDF, Constantes.BOOLEAN_TRUE);
        tipoCarga.put(NOTA_CREDITO_PDF, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(SOPORTE_FACTURA, Constantes.BOOLEAN_FALSE);
        tipoCarga.put(DOCUMENTO_ADUANAL, Constantes.BOOLEAN_FALSE);
        PrimeFaces.current().executeScript( DIA_ARCH_FACT_MODALSHOW);
        FacesUtilsBean.addErrorMessage(ARCHIVO_FACT_FILE_ENTRY, "");
     */
}
