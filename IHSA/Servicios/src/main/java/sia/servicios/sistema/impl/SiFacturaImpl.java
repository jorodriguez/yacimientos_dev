/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import com.google.common.base.Strings;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.ApCampo;
import sia.modelo.Compania;
import sia.modelo.Moneda;
import sia.modelo.Orden;
import sia.modelo.Proveedor;
import sia.modelo.SiAdjunto;
import sia.modelo.SiFactura;
import sia.modelo.Usuario;
import sia.modelo.sistema.vo.FacturaVo;
import sia.modelo.sistema.AbstractFacade;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import mx.bigdata.sat.cfdi.v33.schema.CTipoDeComprobante;
import mx.bigdata.sat.cfdi.v33.schema.Comprobante;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Configurador;
import sia.excepciones.SIAException;
import sia.modelo.contrato.vo.FiltroVo;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sgl.vo.Vo;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.requisicion.impl.OcUsoCFDIImpl;
import sia.util.FacturaEstadoEnum;
import sia.util.OrdenEstadoEnum;
import sia.util.UtilLog4j;
import sia.util.ValidadorFactura;

/**
 *
 * @author ihsa
 */
@Stateless
public class SiFacturaImpl extends AbstractFacade<SiFactura> {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    private static final String WHERE_FE_ESTATUS = "WHERE fe.estatus = ? \n";
    private final String VERSION_FACTURA = "3.";

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    DSLContext dbCtx;

    @Inject
    SiFacturaStatusImpl siFacturaStatusLocal;
    @Inject
    SiFacturaDetalleImpl siFacturaDetalleLocal;
    @Inject
    AutorizacionesOrdenImpl autorizacionesOrdenRemote;
    @Inject
    MonedaImpl monedaRemote;
    @Inject
    OcUsoCFDIImpl ocUsoCFDILocal;
    @Inject
    ProveedorAlmacenDocumentos proveedorAlmacenDocumentosRemote;
    @Inject
    SiFacturaAdjuntoImpl siFacturaAdjuntoLocal;

    public SiFacturaImpl() {
        super(SiFactura.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiFactura guardarFactura(FacturaVo facturaVo, String sesion, List<OrdenDetalleVO> partidas, String tipoCompra) {
        SiFactura siFactura = new SiFactura();
        siFactura.setProveedor(new Proveedor(facturaVo.getIdProveedor()));
        siFactura.setMoneda(new Moneda(facturaVo.getIdMoneda()));
        //        
        siFactura.setConcepto(facturaVo.getConcepto());
        siFactura.setFolio(facturaVo.getFolio().trim());
        siFactura.setObservacion(facturaVo.getObservacion());
        siFactura.setFechaEmision(facturaVo.getFechaEmision());
        siFactura.setSubTotal(facturaVo.getSubTotal());
        siFactura.setTipoCambio(facturaVo.getTipoCambio());
        siFactura.setMonto(facturaVo.getMonto());
        //
        //siFactura.setUsoCfdi(facturaVo.getUsoCfdi());
        siFactura.setOcUsoCfdi(ocUsoCFDILocal.buscarPorCodigo(facturaVo.getCodigoUsoCfdi(), tipoCompra));
        siFactura.setMetodoPago(facturaVo.getMetodoPago());
        siFactura.setFormaPago(facturaVo.getFormaPago());
        siFactura.setFolioFiscal(facturaVo.getFolioFiscal());
        siFactura.setTipoFactura(facturaVo.getIdFactura() == 0 ? "Factura" : "Nota de Crédito");
        //        
        siFactura.setSiFactura(facturaVo.getIdFactura() == 0 ? null : new SiFactura(facturaVo.getIdFactura()));
        //
        siFactura.setApCampo(new ApCampo(facturaVo.getIdCampo()));
        siFactura.setCompania(new Compania(facturaVo.getRfcCompania()));
        siFactura.setRevisada(Constantes.BOOLEAN_FALSE);
        //
        siFactura.setGenero(new Usuario(sesion));
        siFactura.setFechaGenero(new Date());
        siFactura.setHoraGenero(new Date());
        siFactura.setEliminado(Constantes.NO_ELIMINADO);
        create(siFactura);
        //estatus
        if (facturaVo.getIdFactura() == Constantes.CERO) {
            siFactura.setOrden(new Orden(facturaVo.getIdRelacion()));
            siFacturaStatusLocal.guardar(siFactura.getId(), FacturaEstadoEnum.CREADA.getId(), sesion);
        }
        //
        if (!partidas.isEmpty()) {
            siFacturaDetalleLocal.guardar(siFactura.getId(), partidas, sesion);
            //            
        }

        return siFactura;
    }

    public void modificarFactura(FacturaVo facturaVo, String sesion) {
        SiFactura siFactura = find(facturaVo.getId());
        siFactura.setMoneda(new Moneda(facturaVo.getIdMoneda()));
        siFactura.setConcepto(facturaVo.getConcepto());
        siFactura.setFolio(facturaVo.getFolio());
        siFactura.setMonto(facturaVo.getMonto());
        siFactura.setObservacion(facturaVo.getObservacion());
        siFactura.setFechaEmision(facturaVo.getFechaEmision());
        //

        siFactura.setModifico(new Usuario(sesion));
        siFactura.setFechaModifico(new Date());
        siFactura.setHoraModifico(new Date());
        edit(siFactura);
        //log

    }

    public void eliminarFactura(int facturaVo, int status, String sesion) {
        SiFactura siFactura = find(facturaVo);
        //
        siFactura.setEliminado(Constantes.ELIMINADO);
        siFactura.setModifico(new Usuario(sesion));
        siFactura.setFechaModifico(new Date());
        siFactura.setHoraModifico(new Date());
        edit(siFactura);
        //elimina el status factura
        if (status > 0) {
            siFacturaStatusLocal.eliminarPorFactura(facturaVo, status, sesion);
        }
        // cambia de estado la orden
        autorizacionesOrdenRemote.cambiarStatusOrden(siFactura.getOrden().getId(), sesion, OrdenEstadoEnum.POR_RECIBIR_FACTURA.getId());
    }

    public void agregarArchivo(int factura, int adjunto, String sesion) {
        SiFactura siFactura = find(factura);
        siFactura.setSiAdjunto(new SiAdjunto(adjunto));
        //        
        siFactura.setModifico(new Usuario(sesion));
        siFactura.setFechaModifico(new Date());
        siFactura.setHoraModifico(new Date());
        edit(siFactura);
        //log
    }

    public void quitarArchivo(int factura, String sesion) {
        try {
            SiFactura siFactura = find(factura);
            //
            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentosRemote.getAlmacenDocumentos();
            almacenDocumentos.borrarDocumento(siFactura.getSiAdjunto().getUrl());
            //
            siFactura.setSiAdjunto(null);
            //
            siFactura.setModifico(new Usuario(sesion));
            siFactura.setFechaModifico(new Date());
            siFactura.setHoraModifico(new Date());
            edit(siFactura);
            //log
        } catch (SIAException ex) {
            //
            LOGGER.error(ex);
        }

    }

    public FacturaVo buscarFactura(int factura) {
        FacturaVo retVal = null;

        LOGGER.debug(this, "*** Factura ID : {0}", new Object[]{factura});

        StringBuilder sql = new StringBuilder(100);
        sql.append(getBaseConsulta())
                .append("  WHERE fac.id = ? \n"
                        + "  AND fe.actual = true \n"
                        + "  AND fac.eliminado = ?");

        try {
            Record rec = dbCtx.fetchOne(sql.toString(), factura, false);

            if (rec != null) {
                retVal = rec.into(FacturaVo.class);
            }

        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        return retVal;
    }

    public List<FacturaVo> traerFacturaPorOrden(int idCompra, int status) {
        List<FacturaVo> retVal = null;

        StringBuilder sql = new StringBuilder(100);
        sql.append(getBaseConsulta())
                .append(
                        "  WHERE fac.orden = ? \n"
                        + "  AND fe.estatus = ? \n"
                        + "  AND fe.actual =  true \n"
                        + "  AND fac.eliminado = false");

        try {
            retVal
                    = dbCtx.fetch(sql.toString(), idCompra, status)
                            .into(FacturaVo.class);

        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        if (retVal == null) {
            retVal = Collections.emptyList();
        }

        return retVal;
    }

    public double totalPorOrden(int idOrden) {
        String c = " SELECT COALESCE(sum(f.subtotal), 0)  AS total FROM si_factura  f \n"
                + " WHERE f.orden =  ? "
                + " AND f.eliminado = false \n";
        return ((BigDecimal) em.createNativeQuery(c)
                .setParameter(1, idOrden)
                .getSingleResult())
                .doubleValue();
    }

    private String getBaseConsulta() {
        return "SELECT o.id AS id_relacion, fac.concepto, fac.folio, COALESCE(fac.monto, 0) AS monto, fac.observacion \n"
                + " , p.id AS id_proveedor, p.nombre AS proveedor, m.id AS id_moneda, m.nombre AS moneda\n"
                + " , fac.fecha_emision \n"
                + " , COALESCE(fac.si_adjunto, 0) AS id_adjunto, adj.uuid AS uu_Id, adj.url AS url_archivo\n"
                + " , fac.id, COALESCE(fac.cantidad, 0) AS cantidad \n"
                + " , adj.nombre AS adjunto, adj.fecha_genero\n"
                + " , fe.id AS id_status_factura, fe.estatus AS id_status, e.nombre AS status\n"
                + " , o.consecutivo AS codigo_orden, o.navcode AS pedido_nav \n"
                + " , com.rfc AS rfc_compania, com.nombre AS compania \n"
                + " , fac.revisada AS leida, cam.nombre AS campo, fac.metodo_pago, fac.forma_pago\n"
                + " , uso.codigo AS codigo_uso_cfdi, fac.folio_fiscal, uso.nombre AS nombre_uso_cfdi\n"
                + " , uso.id AS id_uso_cfdi, COALESCE(fac.subtotal, 0) AS sub_total\n"
                + " , COALESCE(fac.tipo_cambio, 0) AS tipo_cambio \n"
                + " , COALESCE(ca.id, 0) AS id_campo, ca.nombre AS campo, fac.poliza, p.rfc AS proveedor_rfc\n"
                + " , o.url AS url_pdf_compra \n"
                + " , CASE WHEN fac.tipo_cambio IS NOT NULL AND fac.tipo_cambio > 0 \n"
                + "     THEN TRUNC(fac.subtotal * fac.tipo_cambio,4) \n"
                + "     ELSE fac.subtotal END AS sub_total_pesos \n"
                + " , fe.fecha_genero as fecha_estatus, ge.id AS id_gerencia, ge.nombre as gerencia\n"
                + " , (select fecha_genero from oc_factura_status where estatus = 710 and si_factura = fac.id order by id desc limit 1) as fecha_creada\n"
                + " , (select fecha_genero from oc_factura_status where estatus = 720 and si_factura = fac.id order by id desc limit 1) as fecha_envcnn\n"
                + " , (select fecha_genero from oc_factura_status where estatus = 730 and si_factura = fac.id order by id desc limit 1) as fecha_acecnn\n"
                + " , (select fecha_genero from oc_factura_status where estatus = 740 and si_factura = fac.id order by id desc limit 1) as fecha_acefin\n"
                + " , (select count(id) from oc_factura_status where estatus = 720 and si_factura = fac.id) > 1 as rechazo_cnn\n"
                + " , (select count(id) from oc_factura_status where estatus = 730 and si_factura = fac.id) > 1 as rechazo_fin\n"
                + " , fac.complemento_pago \n"
                + " , fac.comprobante_pago \n"
                + " , fac.complemento_pago_pdf \n"
                + " , tp.nombre as termino_pago \n"
                + " , fac.acepta_avanzia \n"
                + " , coalesce((select acepta_avanzia from si_factura where si_factura = fac.id and eliminado = false order by acepta_avanzia limit 1), true) as acepta_avanzia_nc\n"
                + "FROM si_factura fac \n"
                + "      INNER JOIN oc_factura_status fe ON fe.si_factura = fac.id and fe.actual = true\n"
                + "      INNER JOIN oc_uso_cfdi uso ON fac.oc_uso_cfdi = uso.id\n"
                + "      INNER JOIN estatus e ON fe.estatus = e.id\n"
                + "      INNER JOIN orden o ON fac.orden = o.id \n"
                + "      inner join oc_termino_pago tp on tp.id = o.oc_termino_pago \n"
                + "      INNER JOIN ap_campo cam ON o.ap_campo = cam.id \n"
                + "      INNER JOIN compania com ON o.compania = com.rfc \n"
                + "      INNER JOIN proveedor p ON o.proveedor = p.id \n"
                + "      INNER JOIN moneda m ON o.moneda = m.id \n"
                + "      INNER JOIN ap_campo ca ON fac.ap_campo = ca.id \n"
                + "      INNER JOIN gerencia ge ON o.gerencia = ge.id \n"
                + "      LEFT JOIN si_adjunto adj ON fac.SI_ADJUNTO = adj.ID \n";
    }

    private String getBaseConsultaProveedor() {
        return "SELECT p.rfc as value, p.rfc||' / '||p.nombre as label \n"
                + " FROM si_factura fac \n"
                + "      INNER JOIN oc_factura_status fe ON fe.si_factura = fac.id\n"
                + "      INNER JOIN oc_uso_cfdi uso ON fac.oc_uso_cfdi = uso.id\n"
                + "      INNER JOIN estatus e ON fe.estatus = e.id\n"
                + "      INNER JOIN orden o ON fac.orden = o.id \n"
                + "      INNER JOIN ap_campo cam ON o.ap_campo = cam.id \n"
                + "      INNER JOIN compania com ON o.compania = com.rfc \n"
                + "      INNER JOIN proveedor p ON o.proveedor = p.id \n"
                + "      INNER JOIN moneda m ON o.moneda = m.id \n"
                + "      INNER JOIN ap_campo ca ON fac.ap_campo = ca.id \n"
                + "      LEFT JOIN si_adjunto adj ON fac.SI_ADJUNTO = adj.ID \n";
    }

    public void actualizarFactura(FacturaVo facturaVo, String sesion) {
        try {

            SiFactura siFactura = find(facturaVo.getId());
            siFactura.setMonto(facturaVo.getMonto());
            siFactura.setCantidad(facturaVo.getCantidad());
            siFactura.setModifico(new Usuario(sesion));
            siFactura.setFechaModifico(new Date());
            siFactura.setHoraModifico(new Date());
            //
            edit(siFactura);
        } catch (Exception e) {
            LOGGER.error(e);
            //
        }
    }

    /**
     *
     * @param status
     * @param campo
     * @param proveedor
     * @return
     */
    public long totalFacturaPorStatusCampo(int status, int campo, int proveedor) {
        long retVal = 0;
        StringBuilder sql = new StringBuilder(300);
        List<Object> paramValues = new ArrayList<>();

        sql.append("SELECT count(fac.*) FROM si_factura fac \n"
                + "     INNER JOIN oc_factura_status fe ON fe.si_factura = fac.id \n"
                + "     INNER JOIN orden o ON fac.orden = o.id \n"
                + "WHERE fe.estatus = ?");

        paramValues.add(status);

        if (campo > 0) {
            sql.append(" AND fac.ap_campo = ? \n");
            paramValues.add(campo);
        }

        if (proveedor > 0) {
            sql.append(" AND fac.proveedor = ? \n");
            paramValues.add(proveedor);
        }

        sql.append(" AND fe.actual = true AND fe.eliminado = false");

        try {
            Record rec = dbCtx.fetchOne(sql.toString(), paramValues.toArray());

            if (rec != null) {
                retVal = rec.into(long.class);
            }
        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        return retVal;
    }

    public List<FacturaVo> traerFacturaPorStatus(int status, int campo, int proveedor, String compania) {
        List<FacturaVo> retVal = null;
        List<Object> paramValues = new ArrayList<>();

        StringBuilder sql = new StringBuilder(200);
        sql.append(getBaseConsulta())
                .append("WHERE e.id = ? \n\tAND fac.compania = ? \n");

        paramValues.add(status);
        paramValues.add(compania);

        if (campo > 0) {
            sql.append("  AND fac.ap_campo = ?\n");
            paramValues.add(campo);
        }

        if (proveedor > 0) {
            sql.append("  AND fac.proveedor = ?\n");
            paramValues.add(proveedor);
        }

        sql.append("  AND fe.actual = true \n"
                + "  AND fe.eliminado  = false \n"
                + "  AND fac.eliminado = false");

        try {
            retVal = dbCtx.fetch(sql.toString(), paramValues.toArray()).into(FacturaVo.class);
        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        if (retVal == null) {
            retVal = Collections.emptyList();
        }

        return retVal;
    }

    public int cargarFactura(FacturaVo facturaVo, File file, String sesion, String referencia, String pedido, String tipoCompra) {
        int retVal = 0;

        try {
            Document document = convertirArchivo(file);

            // 
            Element nodeRaiz = document.getDocumentElement();
            //
            facturaVo.setTipoComprobante(nodeRaiz.getAttribute("TipoDeComprobante"));

            facturaVo.setFormaPago(nodeRaiz.getAttribute("FormaPago"));
            facturaVo.setMetodoPago(nodeRaiz.getAttribute("MetodoPago"));
            facturaVo.setMetodoPago(nodeRaiz.getAttribute("MetodoPago"));
            facturaVo.setFolio(nodeRaiz.getAttribute("Folio"));
            //
            if (facturaVo.getFolio().isEmpty()) {
                String uuid = obetnerUUID(file);
                facturaVo.setFolio(uuid.substring(0, 8));
            }
            facturaVo.setSubTotal(new BigDecimal(nodeRaiz.getAttribute("SubTotal")));
            if (nodeRaiz.hasAttribute("TipoCambio")) {
                facturaVo.setTipoCambio(new BigDecimal(nodeRaiz.getAttribute("TipoCambio")));
            } else {
                facturaVo.setTipoCambio(BigDecimal.ZERO);
            }

            facturaVo.setMonto(new BigDecimal(nodeRaiz.getAttribute("Total")));
            //
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                String[] arrFecha = nodeRaiz.getAttribute("Fecha").split("T");
                facturaVo.setFechaEmision(sdf.parse(arrFecha[0]));
            } catch (ParseException ex) {
                LOGGER.warn(this, ex);
            }
            facturaVo.setConcepto(referencia);
            //
            List<OrdenDetalleVO> partidas = new ArrayList<>();
            NodeList listaPartidas = document.getElementsByTagName("cfdi:Concepto");
            //
            // println("///////////////////////////////////////////////////////// partidas /////////////////////////////////");
            for (int temp = 0; temp < listaPartidas.getLength(); temp++) {
                Node nodo = listaPartidas.item(temp);
                //
                OrdenDetalleVO ordenDetVo = new OrdenDetalleVO();
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nodo;
                    ordenDetVo.setCantidad(Double.valueOf(element.getAttribute("Cantidad")));
                    ordenDetVo.setCantidadPorFacturar(new BigDecimal(element.getAttribute("Cantidad")));
                    ordenDetVo.setArtNombre(element.getAttribute("Descripcion"));
                    ordenDetVo.setPrecioUnitario(Doubles.tryParse(element.getAttribute("ValorUnitario")));
                    //
                    ordenDetVo.setImporte(Doubles.tryParse(element.getAttribute("Importe")));

                    //
                    partidas.add(ordenDetVo);
                }

            }
            // USO CFDI
            NodeList listaReceptor = document.getElementsByTagName("cfdi:Receptor");
            for (int i = 0; i < listaReceptor.getLength(); i++) {
                Node nodeRecep = listaReceptor.item(i);
                if (nodeRecep.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nodeRecep;
                    facturaVo.setCodigoUsoCfdi(element.getAttribute("UsoCFDI"));
                }

            }
            // folio fiscal (UUID)
            NodeList listaComplemento = document.getElementsByTagName("tfd:TimbreFiscalDigital");
            for (int i = 0; i < listaComplemento.getLength(); i++) {
                Node nodoCompl = listaComplemento.item(i);
                Element element = (Element) nodoCompl;
                String ffUUID = element.getAttribute("UUID");
                facturaVo.setFolioFiscal(ffUUID != null ? ffUUID.toUpperCase() : null);
            }
            //
            SiFactura siFactura = guardarFactura(facturaVo, sesion, partidas, tipoCompra);
            retVal = siFactura.getId();
        } catch (IOException | NumberFormatException | DOMException | SAXException e) {
            LOGGER.warn(this, e);
        }
        return retVal;
    }

    private Document convertirArchivo(File file) throws IOException, SAXException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            document.getDocumentElement().normalize();

            return document;
        } catch (ParserConfigurationException e) {
            LOGGER.warn(this, e);
        }
        return null;
    }

    public boolean validaTipoComprobante(Comprobante comprobante, CTipoDeComprobante tipoComprobante) {
        return comprobante.getTipoDeComprobante().equals(tipoComprobante);

        //
//        boolean encontrado = false;
//        try {
//            Document document = convertirArchivo(file);
//            // 
//            Element nodeRaiz = document.getDocumentElement();
//            //
//            encontrado = nodeRaiz.getAttribute("TipoDeComprobante").equals(comprobante);
//        } catch (IOException | SAXException e) {
//            LOGGER.error(e);
//        }
//        return encontrado;
    }

    private boolean validaTipoComprobanteFactura4(mx.grupocorasa.sat.cfd._40.Comprobante comprobante, mx.grupocorasa.sat.common.catalogos.CTipoDeComprobante tipoComprobante) {
        return comprobante.getTipoDeComprobante().equals(tipoComprobante);
    }

    /**
     *
     * @param file
     * @param pedido
     * @return
     */
    public boolean validaNavCode(File file, String pedido) {
        //
        boolean encontrado = false;
        try {
            Document document = convertirArchivo(file);
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer t = tf.newTransformer();
            StringWriter sw = new StringWriter();
            t.transform(new DOMSource(document), new StreamResult(sw));

            if (sw.toString().contains(pedido)) {
                encontrado = true;
            } else {
                LOGGER.info("No se puede cargar el XML a la oc/s, es necesario agregar el pedido o el codigo de la orden de compra. ");
            }
            //
        } catch (IOException | TransformerException | SAXException e) {
            LOGGER.error(e);
        }
        return encontrado;
    }

    public boolean validaRFC(File file, String rfc) {
        //
        boolean encontrado = false;
        try {
            Document document = convertirArchivo(file);
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer t = tf.newTransformer();
            StringWriter sw = new StringWriter();
            t.transform(new DOMSource(document), new StreamResult(sw));

            if (sw.toString().contains(rfc)) {
                encontrado = true;
            } else {
                LOGGER.info("No se puede cargar el XML a la CXP, no coincide el RFC del proveedor de factura. ");
            }
            //
        } catch (IOException | TransformerException | SAXException e) {
            LOGGER.error(e);
        }
        return encontrado;
    }

    public boolean validaFacturaFolio(File file, String folio) {
        //
        boolean encontrado = false;
        try {
            Document document = convertirArchivo(file);
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer t = tf.newTransformer();
            StringWriter sw = new StringWriter();
            t.transform(new DOMSource(document), new StreamResult(sw));

            if (sw.toString().contains(folio)) {
                encontrado = true;
            } else {
                LOGGER.info("No se puede cargar el CPX, el folio de la factura no esta ligado al archivo CXP. ");
            }
            //
        } catch (IOException | TransformerException | SAXException e) {
            LOGGER.error(e);
        }
        return encontrado;
    }

    public void aceptarFactura(String sesion, List<FacturaVo> lTemp, String correoSesion, int estatusOrg, int estatusFinal) {
        for (FacturaVo facturaVo : lTemp) {
            siFacturaStatusLocal.aceptarFactura(sesion, facturaVo, correoSesion, estatusOrg, estatusFinal);
        }
    }

    public void aceptarFacturaAvanzia(String sesion, List<FacturaVo> lTemp, String correoSesion, int estatusOrg, int estatusFinal) {
        for (FacturaVo facturaVo : lTemp) {
            siFacturaStatusLocal.aceptarFacturaAvanzia(sesion, facturaVo, correoSesion, estatusOrg, estatusFinal);
        }
    }

    public void aceptarCCN(String sesion, List<FacturaVo> lTemp, String correoSesion, int estatusOrg, int estatusFinal) {
        for (FacturaVo facturaVo : lTemp) {
            siFacturaStatusLocal.aceptarCCN(sesion, facturaVo, correoSesion, estatusOrg, estatusFinal);
        }
    }

    public void pagarFactura(String sesion, List<FacturaVo> lTemp, String correoSesion, int estatusOrg, int estatusFinal) {
        for (FacturaVo facturaVo : lTemp) {
            siFacturaStatusLocal.pagarFactura(sesion, facturaVo, correoSesion, estatusOrg, estatusFinal);
        }
    }

    public void marcarLeida(String sesion, FacturaVo facturaVo) {
        SiFactura factura = find(facturaVo.getId());
        factura.setModifico(new Usuario(sesion));
        factura.setFechaModifico(new Date());
        factura.setHoraModifico(new Date());
        //
        factura.setRevisada(Constantes.BOOLEAN_TRUE);
        //
        edit(factura);
    }

    public FacturaVo buscarPorOrden(int orden, int status) {

        FacturaVo retVal = null;

        try {
            StringBuilder sql = new StringBuilder(100);
            sql.append(getBaseConsulta())
                    .append("WHERE fac.orden = ? \n"
                            + "  AND fe.estatus = ? \n"
                            + "  AND fe.actual = true \n"
                            + "  AND fac.eliminado = false");

            Record rec = dbCtx.fetchOne(sql.toString(), orden, status);
            if (rec != null) {
                retVal = rec.into(FacturaVo.class);
            }

        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        return retVal;
    }

    public List<FacturaVo> traerFacturaDevuelta(int status, int campo, int idProveedor, String compania) {
        List<FacturaVo> retVal = null;
        List paramValues = new ArrayList<>();
        StringBuilder sql = new StringBuilder(600);
        sql.append(getBaseConsulta())
                .append("WHERE e.id = ? \n" //).append(status).append()
                        + "  AND fe.eliminado  = false\n\t AND fe.actual = true \n"
                        + " AND fac.compania = ? \n");

        paramValues.add(status);
        paramValues.add(compania);

        if (campo > 0) {
            sql.append("  AND fac.ap_campo = ? \n");
            paramValues.add(campo);
        }
        if (idProveedor > 0) {
            sql.append("  AND fac.proveedor = ? \n");
            paramValues.add(idProveedor);
        }

        sql.append("\n  AND fac.id IN (SELECT fm.si_factura \n"
                + "                     FROM si_factura_movimiento fm \n"
                + "                     INNER JOIN si_movimiento m ON fm.si_movimiento = m.id \n"
                + "                     WHERE fm.eliminado = false "
                + "                         AND fm.si_factura  = fac.id \n"
                + "                         AND m.si_operacion = 4 AND fm.eliminado = false \n"
                + "                     ORDER BY fm.id DESC ) \n"
                + " AND fac.eliminado = false");

        try {
            retVal = dbCtx.fetch(sql.toString(), paramValues.toArray()).into(FacturaVo.class);
        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        if (retVal == null) {
            retVal = Collections.emptyList();
        }

        return retVal;
    }

    public boolean validaMoneda(Comprobante comprobante, String moneda) {

        return comprobante.getMoneda().name().equals(moneda);

//        try {
//            Document document = convertirArchivo(file);
//            // 
//            Element nodeRaiz = document.getDocumentElement();
//            //
//            String monedaFact = nodeRaiz.getAttribute("Moneda");
//            return moneda.equals(monedaFact);
//        } catch (IOException | SAXException e) {
//            LOGGER.error(e);
//        }
//        return Boolean.FALSE;
    }

    private boolean validaMonedaFactura4(mx.grupocorasa.sat.cfd._40.Comprobante comprobante, String moneda) {
        return comprobante.getMoneda().name().equals(moneda);
    }

    public boolean validaUsoCfdi(Comprobante comprobante, String cfdi) {
        boolean retVal = true;

        if (!Strings.isNullOrEmpty(cfdi)) {
            retVal = comprobante.getReceptor().getUsoCFDI().value().equals(cfdi);
        }

        return retVal;

//        boolean v = true;
//        String usoFact = "";
//        try {
//            if (!cfdi.isEmpty()) {
//                Document document = convertirArchivo(file);
//                //
//                Element nodeRaiz = document.getDocumentElement();
//                //
//                NodeList listaReceptor = document.getElementsByTagName("cfdi:Receptor");
//                // println("///////////////////////////////////////////////////////// partidas /////////////////////////////////");
//                for (int temp = 0; temp < listaReceptor.getLength(); temp++) {
//                    Node nodo = listaReceptor.item(temp);
//                    //
//                    if (nodo.getNodeType() == Node.ELEMENT_NODE) {
//                        Element element = (Element) nodo;
//                        usoFact = element.getAttribute("UsoCFDI");
//                    }
//                }
//
//                v = cfdi.equals(usoFact);
//            }
//        } catch (IOException | SAXException e) {
//            LOGGER.error(e);
//        }
//        return v;
    }

    private boolean validaUsoCfdiFactura4(mx.grupocorasa.sat.cfd._40.Comprobante comprobante, String cfdi) {
        boolean retVal = true;

        if (!Strings.isNullOrEmpty(cfdi)) {
            retVal = comprobante.getReceptor().getUsoCFDI().value().equals(cfdi);
        }

        return retVal;
    }

    /**
     *
     * @param file
     * @param folioFiscal
     * @return
     */
    public boolean validaFolioFiscal(File file, String folioFiscal) {
        //
        boolean encontrado = false;
        try {
            Document document = convertirArchivo(file);
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer t = tf.newTransformer();
            StringWriter sw = new StringWriter();
            t.transform(new DOMSource(document), new StreamResult(sw));
            if (sw.toString().toUpperCase().contains(folioFiscal.toUpperCase())) {
                encontrado = true;

            } else {
                LOGGER.info("No se puede cargar el XML de la nota de crédito, no coincide el folio fiscal con el de la factura. ");
            }
            //
        } catch (IOException | TransformerException | SAXException e) {
            LOGGER.error(e);
        }
        return encontrado;
    }

    public boolean validaClaveProducto(File file, String claveNoPermitida) {
        //
        boolean encontrado = false;
        try {
            Document document = convertirArchivo(file);
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer t = tf.newTransformer();
            StringWriter sw = new StringWriter();
            t.transform(new DOMSource(document), new StreamResult(sw));

            if (sw.toString().contains(claveNoPermitida)) {
                encontrado = true;
                LOGGER.info("No se puede cargar el XML, no está permitido usar en las partidas la clave: " + claveNoPermitida + ".");
            }
            //
        } catch (IOException | TransformerException | SAXException e) {
            LOGGER.error(e);
        }
        return encontrado;
    }

    public List<FacturaVo> traerNotaCredito(int idFactura) {

        List<FacturaVo> lf = null;
        try {
            StringBuilder sql = new StringBuilder(100);
            sql.append(getConsultaNotaCredito())
                    .append("WHERE fac.si_factura = ? AND fac.eliminado = false");

            lf = dbCtx.fetch(sql.toString(), idFactura).into(FacturaVo.class);

        } catch (DataAccessException e) {
            LOGGER.error(e);
        }

        if (lf == null) {
            lf = Collections.emptyList();
        } else {
            for (int i = 0; i < lf.size(); i++) {
                lf.get(i).setSoportesNC(siFacturaAdjuntoLocal.traerSoporteFactura(lf.get(i).getId(), Constantes.BOOLEAN_FALSE, "'PDF (Nota Credito)', 'XML (Nota Credito)'"));
                lf.get(i).setSoportesNCSize(lf.get(i).getSoportesNC().size());
            }
        }

        return lf;
    }

    private String getConsultaNotaCredito() {

        return "SELECT fac.id, fac.concepto, fac.folio, fac.monto, \n "
                + " fac.observacion, p.id AS id_proveedor, p.nombre AS proveedor, \n"
                + " m.id AS id_moneda, m.nombre AS moneda, fac.fecha_emision, \n"
                + " fac.cantidad, fac.revisada AS leida, fac.metodo_pago, fac.forma_pago,\n"
                + " uso.codigo AS codigo_uso_cfdi, fac.folio_fiscal, \n"
                + " uso.nombre AS nombre_uso_cfdi, uso.id AS id_uso_cfdi, fac.si_factura, fac.acepta_avanzia, true as nota_credito \n"
                + " FROM si_factura fac \n"
                + "      INNER JOIN oc_uso_cfdi uso ON fac.oc_uso_cfdi = uso.id \n"
                + "      INNER JOIN proveedor p ON fac.proveedor = p.id \n"
                + "      INNER JOIN moneda m ON fac.moneda = m.id \n";
    }

    public FacturaVo buscarNotaCredito(int factura) {
        FacturaVo retVal = null;

        StringBuilder sql = new StringBuilder(100);
        sql.append(getConsultaNotaCredito()).append("WHERE fac.id = ? \n\tAND fac.eliminado = ? ");

        try {
            Record rec = dbCtx.fetchOne(sql.toString(), factura, false);

            if (rec != null) {
                retVal = rec.into(FacturaVo.class);
            }

        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        return retVal;
    }

    public List<FacturaVo> traerTotalesPorCompania(String compania, int anio, int monedaId) {
        List<FacturaVo> lista = null;

        try {
            String sql = "SELECT a.codeproy AS campo, m.siglas AS moneda, round(sum(f.monto::numeric),2) AS monto \n"
                    + " FROM si_factura f \n"
                    + "     INNER JOIN orden o ON f.orden = o.id \n"
                    + "     INNER JOIN ap_campo a ON o.ap_campo = a.id \n"
                    + "     INNER JOIN oc_factura_status fe ON fe.si_factura = f.id \n"
                    + "     INNER JOIN moneda m ON f.moneda = m.id \n"
                    + " WHERE o.compania = ? " //'" + compania + "'"
                    + "     AND extract(year from o.fecha) = ? " // + anio
                    + "     AND f.moneda = ? " // + monedaId
                    + "     AND fe.estatus = ? " //+ FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId()
                    + "     AND o.eliminado = false "
                    + "     and fe.eliminado = false \n"
                    + "     and fe.actual = true \n"
                    + "     and f.eliminado  = false "
                    + " GROUP BY a.codeproy, m.siglas \n"
                    + " ORDER BY a.codeproy";

            lista
                    = dbCtx.fetch(sql, compania, anio, monedaId, FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId())
                            .into(FacturaVo.class);

        } catch (DataAccessException e) {
            LOGGER.error(e);
        }

        if (lista == null) {
            lista = Collections.emptyList();
        }
        return lista;
    }

    public boolean validaFolioFiscalUUID(File file) {

        boolean v = false;
        String uuid = "";
        try {
            Document document = convertirArchivo(file);
            // 
            // folio fiscal (UUID)
            NodeList listaComplemento = document.getElementsByTagName("tfd:TimbreFiscalDigital");
            for (int i = 0; i < listaComplemento.getLength(); i++) {
                Node nodoCompl = listaComplemento.item(i);
                Element element = (Element) nodoCompl;
                uuid = element.getAttribute("UUID");
            }
            v = buscarPorFolioFiscal(uuid) != null;

        } catch (IOException | SAXException e) {
            LOGGER.error(e);
        }
        return v;
    }

    private String obetnerUUID(File file) {
        String uuid = "";
        try {
            Document document = convertirArchivo(file);
            // 
            // folio fiscal (UUID)
            NodeList listaComplemento = document.getElementsByTagName("tfd:TimbreFiscalDigital");
            for (int i = 0; i < listaComplemento.getLength(); i++) {
                Node nodoCompl = listaComplemento.item(i);
                Element element = (Element) nodoCompl;
                uuid = element.getAttribute("UUID");
            }
        } catch (IOException | SAXException e) {
            LOGGER.error(e);
        }
        return uuid.toUpperCase();
    }

    private String revisarVersionFactura(File file) {
        String version = "";
        try {
            Document document = convertirArchivo(file);
            // 
            // folio fiscal (UUID)
            NodeList listaComplemento = document.getElementsByTagName("cfdi:Comprobante");
            for (int i = 0; i < listaComplemento.getLength(); i++) {
                Node nodoCompl = listaComplemento.item(i);
                Element element = (Element) nodoCompl;
                version = element.getAttribute("Version");
            }
        } catch (IOException | SAXException e) {
            LOGGER.error(e);
        }
        return version;

    }

    private SiFactura buscarPorFolioFiscal(String uuid) {
        SiFactura retVal = null;
        String c = "SELECT * FROM si_factura f  WHERE upper(f.folio_fiscal) = upper(?) AND f.eliminado = false ";

        try {
            retVal
                    = (SiFactura) em.createNativeQuery(c, SiFactura.class)
                            .setParameter(1, uuid)
                            .getSingleResult();
        } catch (Exception e) {
            LOGGER.warn(this, e);
        }

        return retVal;
    }

    public void guardarPoliza(String sesion, FacturaVo facturaVo) {
        SiFactura factura = find(facturaVo.getId());
        factura.setModifico(new Usuario(sesion));
        factura.setFechaModifico(new Date());
        factura.setHoraModifico(new Date());
        //
        factura.setPoliza(facturaVo.getPoliza());
        //
        edit(factura);
    }

    public void guardarPolizaPago(String sesion, FacturaVo facturaVo) {
        SiFactura factura = find(facturaVo.getId());
        factura.setModifico(new Usuario(sesion));
        factura.setFechaModifico(new Date());
        factura.setHoraModifico(new Date());
        //
        factura.setPolizaPago(facturaVo.getPolizaPago());
        //
        edit(factura);
    }

    public void guardarCXPXml(String sesion, FacturaVo facturaVo, SiAdjunto cxp) {
        SiFactura factura = find(facturaVo.getId());
        factura.setModifico(new Usuario(sesion));
        factura.setFechaModifico(new Date());
        factura.setHoraModifico(new Date());
        //
        factura.setComplementoPago(cxp);
        //
        edit(factura);
    }

    public void borrarCXPXml(String sesion, FacturaVo facturaVo) {

        SiFactura factura = find(facturaVo.getId());
        factura.setModifico(new Usuario(sesion));
        factura.setFechaModifico(new Date());
        factura.setHoraModifico(new Date());
        //
        factura.setComplementoPago(null);
        //
        edit(factura);
    }

    public void guardarCXPPdf(String sesion, FacturaVo facturaVo, SiAdjunto cxp) {
        SiFactura factura = find(facturaVo.getId());
        factura.setModifico(new Usuario(sesion));
        factura.setFechaModifico(new Date());
        factura.setHoraModifico(new Date());
        //
        factura.setComplementoPagoPdf(cxp);
        //
        edit(factura);
    }

    public void guardarComprobante(String sesion, FacturaVo facturaVo, SiAdjunto cp) {
        SiFactura factura = find(facturaVo.getId());
        factura.setModifico(new Usuario(sesion));
        factura.setFechaModifico(new Date());
        factura.setHoraModifico(new Date());
        //
        factura.setComprobantePago(cp);
        //
        edit(factura);
    }

    public boolean isEnviarCNH(int facturaID) {
        boolean enviar = false;
        try {
            String c
                    = "SELECT a.poliza, a.poliza_pago, \n"
                    + "     coalesce( \n"
                    + "         (SELECT id  \n"
                    + "          FROM si_factura_adjunto  "
                    + "          WHERE si_factura = a.id "
                    + "             AND eliminado = false "
                    + "             AND tipo_archivo = 'COMPLEMENTO' "
                    + "         LIMIT 1),0) as compArchivo, "
                    + "     coalesce( \n"
                    + "         (SELECT id "
                    + "          FROM si_factura_adjunto  "
                    + "          WHERE si_factura = a.id "
                    + "             AND eliminado = false "
                    + "             AND tipo_archivo = 'PAGO' "
                    + "          LIMIT 1),0) as pagoArchivo "
                    + " FROM si_factura a"
                    + " WHERE a.eliminado = false"
                    + " AND a.id = " + facturaID;

            List<Object[]> lo = em.createNativeQuery(c).getResultList();
            if (lo != null && !lo.isEmpty()) {
                if (((Integer) ((lo.get(0))[2])) > 0 && ((Integer) ((lo.get(0))[3])) > 0) {
                    String poliza = (String) ((lo.get(0))[0]);
                    String polizaPago = (String) ((lo.get(0))[1]);
                    enviar = !Strings.isNullOrEmpty(poliza) && !Strings.isNullOrEmpty(polizaPago);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return enviar;
    }

    /**
     *
     * @param status
     * @param campo
     * @param proveedor
     * @param compania
     * @return
     */
    public long totalFacturaDevueltas(int status, int campo, int proveedor, String compania) {
        long retVal = 0;

        StringBuilder sql = new StringBuilder(700);
        List paramValues = new ArrayList<>();
        sql.append("SELECT count(fac.*) FROM si_factura fac \n"
                + "   INNER JOIN oc_factura_status fe on fe.si_factura = fac.id \n"
                + "   INNER JOIN orden o on fac.orden = o.id \n"
                + WHERE_FE_ESTATUS
                + " AND fac.compania = ? \n");

        paramValues.add(status);
        paramValues.add(compania);

        if (campo > 0) {
            sql.append(" AND fac.ap_campo = ? \n");//.append(campo);
            paramValues.add(campo);
        }

        if (proveedor > 0) {
            sql.append(" AND fac.proveedor = ? \n");//.append(proveedor);
            paramValues.add(proveedor);
        }

        sql.append(" AND fe.actual = true AND fe.eliminado = false\n"
                + " AND fac.id in (SELECT fm.si_factura \n "
                + "                 FROM si_factura_movimiento fm \n"
                + "                     INNER JOIN si_movimiento m ON fm.si_movimiento = m.id \n"
                + "                 WHERE fm.eliminado = false \n"
                + "                     AND m.si_operacion = ? \n" //Constantes.ID_SI_OPERACION_DEVOLVER
                + "                     AND fm.eliminado = false \n"
                + "                 ORDER BY fm.id DESC LIMIT 1) \n"
                + " AND fac.eliminado = false");

        paramValues.add(Constantes.ID_SI_OPERACION_DEVOLVER);

        try {
            Record rec = dbCtx.fetchOne(sql.toString(), paramValues.toArray());

            if (rec != null) {
                retVal = rec.into(long.class);
            }
        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        return retVal;
    }

    public long totalFacturaPorStatusCompania(int status, String compania, int proveedor) {

        long retVal = 0;

        List<Object> paramValues = new ArrayList();
        StringBuilder sql = new StringBuilder(300);
        sql.append("SELECT count(fac.*) \n"
                + "FROM si_factura fac \n"
                + "   INNER JOIN oc_factura_status fe ON fe.si_factura = fac.id \n"
                + "   INNER JOIN orden o ON fac.orden = o.id \n"
                + WHERE_FE_ESTATUS
                + " AND fac.compania = ? \n"
                + " AND fe.actual = true \n\tAND fe.eliminado = false"
        );

        paramValues.add(status);
        paramValues.add(compania);

        if (proveedor > 0) {
            paramValues.add(proveedor);
            sql.append("\n\tAND fac.proveedor = ?");//.append(proveedor);
        }

        try {
            Record rec = dbCtx.fetchOne(sql.toString(), paramValues.toArray());

            if (rec != null) {
                retVal = rec.into(long.class);
            }

        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        return retVal;
    }

    public long totalFacturaDevueltasPorCompania(int status, String compania, int proveedor) {
        StringBuilder sql = new StringBuilder(800);
        sql.append("SELECT count(fac.*) \n"
                + "FROM si_factura fac\n"
                + "	INNER JOIN oc_factura_status fe ON fe.si_factura = fac.id\n"
                + "	INNER JOIN orden o ON fac.orden = o.id\n"
                + WHERE_FE_ESTATUS
                + "	AND fac.compania = ? \n");

        if (proveedor > 0) {
            sql.append("	AND fac.proveedor = ? \n");
        }

        sql.append("	AND fe.actual = true \n"
                + "	AND fe.eliminado = false \n"
                + "	AND fac.id IN (\n"
                + "		SELECT fm.si_factura \n"
                + "		FROM si_factura_movimiento fm \n"
                + "			INNER JOIN si_movimiento m on fm.si_movimiento = m.id \n"
                + "		WHERE fm.eliminado = false\n"
                + "			AND fm.si_factura = fac.id\n"
                + "			AND m.si_operacion = ?  \n"
                + "			AND fm.eliminado = false \n"
                + "		ORDER BY fm.id desc limit 1\n"
                + "	) \n"
                + "	AND fac.eliminado = false"
        );

        int i = 1;
        Query qry = em.createNativeQuery(sql.toString());
        qry.setParameter(i++, status).setParameter(i++, compania);

        if (proveedor > 0) {
            qry.setParameter(i++, proveedor);
        }

        qry.setParameter(i++, Constantes.ID_SI_OPERACION_DEVOLVER);

        return (long) qry.getSingleResult();
    }

    public List<FacturaVo> facturasPorOrden(int idCompra) {
        List<FacturaVo> retVal = null;

        StringBuilder sql = new StringBuilder(200);
        sql.append(getBaseConsulta())
                .append(
                        "WHERE fac.orden = ? \n"
                        + "    AND fe.actual = ? \n"
                        + "  AND fe.estatus > ? \n"
                        + "  AND fac.eliminado = false"
                );

        try {
            retVal
                    = dbCtx.fetch(
                            sql.toString(),
                            idCompra,
                            true,
                            FacturaEstadoEnum.CREADA.getId()
                    ).into(FacturaVo.class);
        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        if (retVal == null) {
            retVal = Collections.emptyList();
        }

        return retVal;
    }

    public List<FacturaVo> traerFacturaPorStatusFecha(int status, int campo, int proveedor, Date inicio, Date fin) {
        List<FacturaVo> retVal = null;
        List<Object> paramValues = new ArrayList<>();

        StringBuilder sql = new StringBuilder(200);
        sql.append(getBaseConsulta()).append("WHERE e.id = ? \n");
        paramValues.add(status);

        if (campo > 0) {
            sql.append("  AND o.ap_campo = ? \n");
            paramValues.add(campo);
        }

        if (proveedor > 0) {
            sql.append("  AND o.proveedor = ? \n");
            paramValues.add(proveedor);
        }

        paramValues.add(new java.sql.Date(inicio.getTime()));
        paramValues.add(new java.sql.Date(fin.getTime()));

        sql.append("  AND fe.fecha_genero BETWEEN ? and ? \n"
                + "  AND fe.actual = true \n"
                + "  AND fe.eliminado  = false \n"
                + "  AND fac.eliminado = false");

        try {
            retVal = dbCtx.fetch(sql.toString(), paramValues.toArray()).into(FacturaVo.class);
        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        if (retVal == null) {
            retVal = Collections.emptyList();
        }

        return retVal;
    }

    public void eliminarNotaCredito(int facturaVo, int status, String sesion) {
        SiFactura siFactura = find(facturaVo);
        //
        siFactura.setEliminado(Constantes.ELIMINADO);
        siFactura.setModifico(new Usuario(sesion));
        siFactura.setFechaModifico(new Date());
        siFactura.setHoraModifico(new Date());
        edit(siFactura);
        //elimina el status factura
        if (status > 0) {
            siFacturaStatusLocal.eliminarPorFactura(facturaVo, status, sesion);
        }
    }

    /**
     * Validamos que el folio esté vigente en el SAT y que el archivo no haya
     * sufrido alteraciones.
     *
     * @param file El archivo con el XML de la factura.
     * @return <code>true</code> en caso de que sea válido el comprobante,
     * <code>false</code> en caso contrario.
     */
    public boolean validarSAT(File file) {
        return ValidadorFactura.verificarEstatusSAT(file);
    }

    private boolean validarSATFactura4(File file) {
        return ValidadorFactura.verificarEstatusFactura4(file);
    }

    public List<FacturaVo> traerFacturaActualPorFolioStatus(String folio, int status) {
        List<FacturaVo> retVal = null;

        StringBuilder sql = new StringBuilder(100);
        sql.append(getBaseConsulta())
                .append(WHERE_FE_ESTATUS
                        + "  AND fac.folio =  ? \n"
                        + "  AND fe.actual =  true \n"
                        + "  AND fac.eliminado = false");

        try {
            retVal
                    = dbCtx.fetch(sql.toString(), status, folio).into(FacturaVo.class);
        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        if (retVal == null) {
            retVal = Collections.emptyList();
        }

        return retVal;
    }

    public Comprobante getComprobanteFromFile(File file) throws SIAException {
        try {
            return ValidadorFactura.getComprobanteFromFile(file);
        } catch (FileNotFoundException | JAXBException e) {
            throw new SIAException(e);
        }
    }

    public mx.grupocorasa.sat.cfd._40.Comprobante getComprobanteFromFileFactura4(File file) throws SIAException {
        try {
            return ValidadorFactura.getComprobanteFromStreamFactura4(file);
        } catch (FileNotFoundException | JAXBException e) {
            throw new SIAException(e);
        }
    }

    public void validarFactura(File file, OrdenVO compraVo) {

        try {

            String uuId = obetnerUUID(file);

            Preconditions.checkState(
                    !compraVo.getNavCode().isEmpty()
                    && validaNavCode(file, compraVo.getNavCode()),
                    "El pedido " + compraVo.getNavCode() + " no se encontró en la factura. "
            );

            Preconditions.checkState(
                    compraVo.getIdCfdi() > 0,
                    "La orden de compra no tiene registrado un USO de CFDI."
            );

            Preconditions.checkState(
                    !validaClaveProducto(file, Constantes.FAC_CLAVE_NO_VALIDA),
                    "No esta permitido usar la clave " + Constantes.FAC_CLAVE_NO_VALIDA + " en la factura."
            );
            Preconditions.checkState(
                    !validaFolioFiscalUUID(file),
                    "El folio fiscal ya fue registrado, favor de revisar el archivo. "
            );
            if (revisarVersionFactura(file).startsWith(VERSION_FACTURA)) {
                Comprobante comprobante = getComprobanteFromFile(file);

                Preconditions.checkState(
                        validaTipoComprobante(comprobante, CTipoDeComprobante.I),
                        "Esta intentando cargar un archivo que NO es una factura. "
                );

                Preconditions.checkState(
                        validaMoneda(comprobante, compraVo.getMoneda()),
                        "La moneda de la factura no coincide con la moneda en la orden de compra. "
                );

                Preconditions.checkState(
                        validaUsoCfdi(comprobante, compraVo.getCodigoCfdi()),
                        "El uso de CFDI en la factura, NO es el indicado en la orden."
                );

                if (Configurador.isValidarFacturaVsSat()) {
                    Preconditions.checkState(
                            validarSAT(file),
                            "El CFDI no está vigente en el SAT o el XML no es válido. "
                    );
                }
            } else {
                // Validar si el archivo es una factura
                mx.grupocorasa.sat.cfd._40.Comprobante comprobante = getComprobanteFromFileFactura4(file);
                Preconditions.checkState(
                        validaTipoComprobanteFactura4(comprobante, mx.grupocorasa.sat.common.catalogos.CTipoDeComprobante.I),
                        "Esta intentando cargar un archivo que NO es una factura. "
                );
                //Valida la moneda
                Preconditions.checkState(
                        validaMonedaFactura4(comprobante, compraVo.getMoneda()),
                        "La moneda de la factura no coincide con la moneda en la orden de compra. "
                );
                // Valida el Uso de CFDI
                Preconditions.checkState(
                        validaUsoCfdiFactura4(comprobante, compraVo.getCodigoCfdi()),
                        "El uso de CFDI en la factura, NO es el indicado en la orden."
                );
                // Valida factura SAT                
                if (Configurador.isValidarFacturaVsSat()) {
                    Preconditions.checkState(
                            validarSATFactura4(file),
                            "El CFDI no está vigente en el SAT o el XML no es válido. "
                    );
                }
                System.out.println("La factura es Versión: " + 4.0);
                //throw new SIAException("La factura es Versión: " + 4.0 + " Estamos trabajando para cargar esta versión de facturas.");

            }
        } catch (IllegalStateException | SIAException e) {
            throw new EJBException(e.getMessage());
        }

    }

    public void validarCXP(File file, FacturaVo facturaVo) {

        try {
            if (revisarVersionFactura(file).startsWith(VERSION_FACTURA)) {
                Comprobante comprobante = getComprobanteFromFile(file);

                Preconditions.checkState(
                        validaTipoComprobante(comprobante, CTipoDeComprobante.P),
                        "Esta intentando cargar un archivo que NO es un complemento de pago. "
                );
            } else {
                mx.grupocorasa.sat.cfd._40.Comprobante comprobante = getComprobanteFromFileFactura4(file);

                Preconditions.checkState(
                        validaTipoComprobanteFactura4(comprobante, mx.grupocorasa.sat.common.catalogos.CTipoDeComprobante.P),
                        "Esta intentando cargar un archivo que NO es un complemento de pago. "
                );
            }

            Preconditions.checkState(
                    !validaFolioFiscalUUID(file),
                    "El folio fiscal ya fue registrado, favor de revisar el archivo. "
            );

            Preconditions.checkState(
                    facturaVo.getProveedorRfc() != null && !facturaVo.getProveedorRfc().isEmpty()
                    && validaNavCode(file, facturaVo.getProveedorRfc()),
                    "El RFC del proveedor " + facturaVo.getProveedorRfc() + " no se encontró en el CXP. "
            );

            Preconditions.checkState(
                    !facturaVo.getFolio().isEmpty()
                    && validaNavCode(file, facturaVo.getFolio()),
                    "El folio de la factura " + facturaVo.getFolio() + " no se encontró en el CXP. "
            );

            Preconditions.checkState(
                    !facturaVo.getFolio().isEmpty()
                    && validaNavCode(file, facturaVo.getFolioFiscal()),
                    "El folio fiscal de la factura " + facturaVo.getFolioFiscal() + " no se encontró en el CXP. "
            );

            if (Configurador.isValidarFacturaVsSat()) {
                Preconditions.checkState(
                        validarSAT(file),
                        "El CFDI no está vigente en el SAT o el XML no es válido. "
                );
            }
        } catch (IllegalStateException | SIAException e) {
            throw new EJBException(e.getMessage());
        }

    }

    public void validarXmlNotaCred(File file, FacturaVo facturaVo) {
        try {
            if (revisarVersionFactura(file).startsWith(VERSION_FACTURA)) {
                Comprobante comprobante = getComprobanteFromFile(file);
                Preconditions.checkState(
                        validaTipoComprobante(comprobante, CTipoDeComprobante.E),
                        "Esta cargando un archivo que NO es una nota de crédito."
                );
            } else {
                mx.grupocorasa.sat.cfd._40.Comprobante comprobante = getComprobanteFromFileFactura4(file);
                Preconditions.checkState(
                        validaTipoComprobanteFactura4(comprobante, mx.grupocorasa.sat.common.catalogos.CTipoDeComprobante.E),
                        "Esta cargando un archivo que NO es una nota de crédito."
                );
            }

            Preconditions.checkState(
                    validaFolioFiscal(file, facturaVo.getFolioFiscal()),
                    "El folio fiscal (UUID), no coincide con el de la factura."
            );

            Preconditions.checkState(
                    !validaClaveProducto(file, Constantes.FAC_CLAVE_NO_VALIDA),
                    "No esta permitido usar la clave " + Constantes.FAC_CLAVE_NO_VALIDA + " en la factura."
            );
        } catch (IllegalStateException | SIAException e) {
            throw new EJBException(e.getMessage());
        }
    }

    public List<SelectItem> traerProveedorPorStatusFacturas(String cadena, int status, int campo) {
        List<SelectItem> retVal = null;
        List<Object> paramValues = new ArrayList<>();

        StringBuilder sql = new StringBuilder(200);
        sql.append(getBaseConsultaProveedor()).append("WHERE e.id = ? \n");
        paramValues.add(status);

        if (campo > 0) {
            sql.append("  AND fac.ap_campo = ? \n");
            paramValues.add(campo);
        }

        if (cadena != null && !cadena.isEmpty()) {
            sql.append("  AND upper(p.nombre||p.rfc) like '%" + cadena.toUpperCase() + "%' \n");

        }

        sql.append("  AND fe.actual = true \n"
                + "  AND fe.eliminado  = false \n"
                + "  AND fac.eliminado = false"
                + "  group by value, label ");

        try {
            retVal = dbCtx.fetch(sql.toString(), paramValues.toArray()).into(SelectItem.class);
        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        if (retVal == null) {
            retVal = Collections.emptyList();
        }

        return retVal;
    }

    public List<FacturaVo> traerFacturaPorStatusFecha(int status, int campo, String rfcProveedor, Date inicio, Date fin) {
        List<FacturaVo> retVal = null;
        List<Object> paramValues = new ArrayList<>();

        StringBuilder sql = new StringBuilder(200);
        sql.append(getBaseConsulta()).append("WHERE e.id = ? \n");
        paramValues.add(status);

        if (campo > 0) {
            sql.append("  AND fac.ap_campo = ? \n");
            paramValues.add(campo);
        }

        if (rfcProveedor != null && !rfcProveedor.isEmpty()) {
            sql.append("  AND upper(p.rfc||' / '||p.nombre) = ? \n");
            paramValues.add(rfcProveedor.toUpperCase());
        }

        paramValues.add(new java.sql.Date(inicio.getTime()));
        paramValues.add(new java.sql.Date(fin.getTime()));

        sql.append("  AND fe.fecha_genero BETWEEN ? and ? \n"
                + "  AND fe.actual = true \n"
                + "  AND fe.eliminado  = false \n"
                + "  AND fac.eliminado = false"
                + "  order by p.id, fac.folio ");

        try {
            retVal = dbCtx.fetch(sql.toString(), paramValues.toArray()).into(FacturaVo.class);
        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        if (retVal == null) {
            retVal = Collections.emptyList();
        }
        return retVal;
    }

    public List<FacturaVo> traerFacturaPorProveedor(int proveedor, Date inicio, Date fin, String folio, int estatus) {
        List<FacturaVo> retVal = null;
        List<Object> paramValues = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        StringBuilder sql = new StringBuilder(200);
        sql.append(getBaseConsulta())
                .append("WHERE p.id = ").append(proveedor).append(" and fac.fecha_genero between '").append(sdf.format(inicio)).append("' and  '").append(sdf.format(fin)).append("'");

//        paramValues.add(proveedor);
        sql.append("  AND fe.actual = true \n"
                + "  AND fe.eliminado  = false \n"
                + "  AND fac.eliminado = false ");
        //out.println("sql: " + sql);
        if (folio != null && !folio.isEmpty()) {
            sql.append(" AND fac.folio = '").append(folio.trim()).append("' ");
        }

        if (estatus > 0) {
            sql.append(" AND fe.estatus = ").append(estatus);
        }

        try {
            //out.println("cons: " + sql);
            retVal = dbCtx.fetch(sql.toString()).into(FacturaVo.class);
        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        if (retVal == null) {
            retVal = Collections.emptyList();
        }

        return retVal;
    }

    public List<FacturaVo> traerFacturaCXP(int proveedor, String compania, int campo) {
        List<FacturaVo> retVal = null;
        List<Object> paramValues = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        StringBuilder sql = new StringBuilder(200);
        sql.append(getBaseConsulta())
                .append("WHERE p.id = ").append(proveedor);

        if (campo > 0) {
            sql.append(" and fac.ap_campo = ").append(campo);
        }

        if (compania != null && !compania.isEmpty()) {
            sql.append(" and cam.compania = '").append(compania).append("' ");
        }

//        paramValues.add(proveedor);
        sql.append("  AND fe.actual = true AND fe.estatus = 750 \n"
                + "  AND fe.eliminado  = false \n"
                + "  AND fac.eliminado = false "
                + "  AND (fac.complemento_pago is null or fac.complemento_pago_pdf is null) ");

        //out.println("sql: " + sql);
        try {
            //out.println("cons: " + sql);
            retVal = dbCtx.fetch(sql.toString()).into(FacturaVo.class);
        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        if (retVal == null) {
            retVal = Collections.emptyList();
        }

        return retVal;
    }

    public List<SelectItem> traerFacturaCXPCompania(int proveedor, String compania, int campo) {
        List<SelectItem> retVal = null;
        List<Object> paramValues = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        StringBuilder sql = new StringBuilder(200);
        

        sql.append(" SELECT cam.id as \"value\", cam.nombre as \"label\" "
                + " FROM si_factura fac "
                + " INNER JOIN oc_factura_status fe ON fe.si_factura = fac.id and fe.actual = true"
                + " INNER JOIN oc_uso_cfdi uso ON fac.oc_uso_cfdi = uso.id"
                + " INNER JOIN estatus e ON fe.estatus = e.id"
                + " INNER JOIN orden o ON fac.orden = o.id "
                + " inner join oc_termino_pago tp on tp.id = o.oc_termino_pago "
                + " INNER JOIN ap_campo cam ON o.ap_campo = cam.id "
                + " INNER JOIN compania com ON o.compania = com.rfc "
                + " INNER JOIN proveedor p ON o.proveedor = p.id "
                + " INNER JOIN moneda m ON o.moneda = m.id "
                + " INNER JOIN ap_campo ca ON fac.ap_campo = ca.id "
                + " INNER JOIN gerencia ge ON o.gerencia = ge.id "
                + " LEFT JOIN si_adjunto adj ON fac.SI_ADJUNTO = adj.ID ")
                .append("WHERE p.id = ").append(proveedor);

        if (campo > 0) {
            sql.append(" and fac.ap_campo = ").append(campo);
        }

        if (compania != null && !compania.isEmpty()) {
            sql.append(" and cam.compania = '").append(compania).append("' ");
        }

//        paramValues.add(proveedor);
        sql.append("  AND fe.actual = true AND fe.estatus = 750 \n"
                + "  AND fe.eliminado  = false \n"
                + "  AND fac.eliminado = false "
                + "  AND (fac.complemento_pago is null or fac.complemento_pago_pdf is null) ");
        
        sql.append(" group by \"value\", \"label\" ");

        //out.println("sql: " + sql);
        try {
            //out.println("cons: " + sql);
            retVal = dbCtx.fetch(sql.toString()).into(SelectItem.class);
        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        if (retVal == null) {
            retVal = Collections.emptyList();
        } else {
            retVal.add(0, new SelectItem(Constantes.CERO, "Todas . . . "));            
        }

        return retVal;
    }

    public List<FacturaVo> traerFacturaPAGO(int proveedor, String compania, int campo) {
        List<FacturaVo> retVal = null;
        List<Object> paramValues = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        StringBuilder sql = new StringBuilder(200);
        sql.append(getBaseConsulta())
                .append("WHERE p.id = ").append(proveedor);

        if (campo > 0) {
            sql.append(" and fac.ap_campo = ").append(campo);
        }

        if (compania != null && !compania.isEmpty()) {
            sql.append(" and cam.compania = '").append(compania).append("' ");
        }

//        paramValues.add(proveedor);
        sql.append("  AND fe.actual = true AND fe.estatus = 740 \n"
                + "  AND fe.eliminado  = false \n"
                + "  AND fac.eliminado = false");
        //out.println("sql: " + sql);

        try {
            //out.println("cons: " + sql);
            retVal = dbCtx.fetch(sql.toString()).into(FacturaVo.class);
        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        if (retVal == null) {
            retVal = Collections.emptyList();
        }

        return retVal;
    }

    public List<FacturaVo> traerFacturas(int idCampo, List<FiltroVo> filtros) {
        List<FacturaVo> retVal = null;
        StringBuilder sql = new StringBuilder(200);
        sql.append(getBaseConsulta())
                .append(" where ")
                .append(parametros(filtros, idCampo))
                .append("  AND fe.eliminado  = false \n"
                        + "  AND fac.eliminado = false ");

        try {
            //out.println("cons: " + sql);
            retVal = dbCtx.fetch(sql.toString()).into(FacturaVo.class);
        } catch (DataAccessException e) {
            LOGGER.warn(this, e);
        }

        if (retVal == null) {
            retVal = Collections.emptyList();
        }

        return retVal;
    }

    private String parametros(List<FiltroVo> filtros, int campo) {
        String q = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        for (FiltroVo filtro : filtros) {
            if (filtro.getId() == 0) {
                if (filtro.getCampoSeleccionado().equals("Fecha")) {
                    q += " fe.fecha_genero between '" + sdf.format(filtro.getFechaInicio()) + "' and '" + sdf.format(filtro.getFechaFin()) + "'";
                } else if (filtro.getCampoSeleccionado().equals("Gerencia")) {
                    q = " ge.id  = " + filtro.getIdGerencia();
                } else if (filtro.getCampoSeleccionado().equals("Estatus")) {
                    q = " fe.estatus = " + filtro.getIdEstado();
                } else if (filtro.getCampoSeleccionado().equals("Proveedor")) {
                    q = " p.id = " + filtro.getIdProveedor();
                } else if (filtro.getCampoSeleccionado().equals("Orden")) {
                    q = " (o.consecutivo =  '" + filtro.getAlcance() + "' or o.navcode = '" + filtro.getAlcance() + "') ";
                } else if (filtro.getCampoSeleccionado().equals("Folio")) {
                    q = " (fac.folio =  '" + filtro.getAlcance() + "') ";
                }
            } else if (filtro.getCampoSeleccionado() != null && filtro.getId() > 0 && !filtro.getCampoSeleccionado().isEmpty()) {
                if (filtro.getCampoSeleccionado().equals("Proveedor")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or p.id = " + filtro.getIdProveedor();
                    } else {
                        q += " and p.id = " + filtro.getIdProveedor();
                    }
                } else if (filtro.getCampoSeleccionado().equals("Gerencia")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or ge.id = " + filtro.getIdGerencia();
                    } else {
                        q += " and ge.id = " + filtro.getIdGerencia();
                    }
                } else if (filtro.getCampoSeleccionado().equals("Estatus")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or fe.estatus = " + filtro.getIdEstado();
                    } else {
                        q += " and fe.estatus = " + filtro.getIdEstado();
                    }
                } else if (filtro.getCampoSeleccionado().equals("Fecha")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or fe.fecha_genero between '" + sdf.format(filtro.getFechaInicio()) + "' and '" + sdf.format(filtro.getFechaFin()) + "'";
                    } else {
                        q += " and fe.fecha_genero between '" + sdf.format(filtro.getFechaInicio()) + "' and '" + sdf.format(filtro.getFechaFin()) + "'";
                    }
                } else if (filtro.getCampoSeleccionado().equals("Orden")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or (o.consecutivo =  '" + filtro.getAlcance() + "' or o.navcode = '" + filtro.getAlcance() + "') ";
                    } else {
                        q += " and (o.consecutivo =  '" + filtro.getAlcance() + "' or o.navcode = '" + filtro.getAlcance() + "') ";

                    }
                } else if (filtro.getCampoSeleccionado().equals("Folio")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or (fac.folio =  '" + filtro.getAlcance() + "') ";
                    } else {
                        q += " and (fac.folio =  '" + filtro.getAlcance() + "') ";

                    }
                }
            }
        }
        q += " and fac.ap_campo = " + campo;
        return q;
    }

    public Date fechaPrimerFactura(int idCampo) {
        String c = "select  min(sf.fecha_genero) from si_factura sf where sf.ap_campo  = " + idCampo + " and sf.eliminado  = false";
        return (Date) em.createNativeQuery(c).getSingleResult();
    }

    public Date fechaUltimaFactura(int idCampo) {
        String c = "select  max(sf.fecha_genero) from si_factura sf where sf.ap_campo  = " + idCampo + " and sf.eliminado  = false";
        return (Date) em.createNativeQuery(c).getSingleResult();
    }

    public List<Vo> proveedores(int idCampo) {
        String c = "select  p.id, p.nombre from si_factura sf \n"
                + "	inner join proveedor p  on sf.proveedor = p.id \n"
                + " where sf.ap_campo  = " + idCampo
                + " and sf.eliminado  = false\n"
                + " group by p.id, p.nombre  \n"
                + " order by p.nombre ";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<Vo> vos = new ArrayList<>();
        for (Object[] obj : objs) {
            Vo vo = new Vo();
            vo.setId((Integer) obj[0]);
            vo.setNombre((String) obj[1]);
            vos.add(vo);
        }
        return vos;
    }

    public List<Vo> gerencias(int idCampo) {
        String c = "select  g.id, g.nombre from si_factura sf \n"
                + "	inner join orden o on sf.orden  = o.id \n"
                + "	inner join gerencia g  on o.gerencia = g.id \n"
                + " where sf.ap_campo  = " + idCampo
                + " group by g.id, g.nombre ";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<Vo> vos = new ArrayList<>();
        for (Object[] obj : objs) {
            Vo vo = new Vo();
            vo.setId((Integer) obj[0]);
            vo.setNombre((String) obj[1]);
            vos.add(vo);
        }
        return vos;
    }

    public boolean requiereNC(int idFactura) {
        StringBuilder sql = new StringBuilder(800);

        sql.append(" select count(fac.id) > 0 as requiere_nc \n"
                + " from si_factura fac \n"
                + " inner join orden ord on ord.id = fac.orden and ord.eliminado = false \n"
                + " inner join convenio con on con.codigo = ord.contrato and con.eliminado = false \n"
                + " where fac.eliminado = false \n"
                + " and con.porcentaje_deduccion > 0 \n"
                + " and fac.id = ").append(idFactura);

        int i = 1;
        Query qry = em.createNativeQuery(sql.toString());

        return (boolean) qry.getSingleResult();
    }

    public List<FacturaVo> traerTotalFacturaContenido(String rfcCompania, int anio) {
        String c = "select  proveedor, facturado, contenido ,\n"
                + "		round(((contenido * 100 )/ facturado),2) as Porcentaje		 from ( \n"
                + "(select p.nombre as proveedor , round(sum(sf.monto * sf.tipo_cambio)::numeric,2) as facturado,\n"
                + "		round(sum(cn.monto_facturado::numeric),2) as contenido\n"
                + "from si_factura_contenido_nacional cn\n"
                + "	inner join si_factura sf on cn.si_factura  = sf.id \n"
                + "	inner join oc_factura_status ofs on ofs.si_factura = sf.id and ofs.actual  = true\n"
                + "	inner join proveedor p on sf.proveedor = p.id 	\n"
                + " where cn.eliminado  = false\n"
                + " and sf.eliminado  = false\n"
                + " and ofs.eliminado = false \n"
                + " and ofs.estatus > " + FacturaEstadoEnum.PROCESO_DE_PAGO.getId() + "\n"
                + " and sf.compania  = '" + rfcCompania + "'" + "\n"
                + " and extract(year from sf.fecha_genero) = " + anio + "\n"
                + " and sf.tipo_cambio  > 0\n"
                + " group by p.nombre \n"
                + " order by facturado desc)\n"
                + " union (\n"
                + " select p.nombre, round(sum(sf.monto)::numeric,2) as facturado,\n"
                + "		round(sum(cn.monto_facturado::numeric),2) as contenido\n"
                + " from si_factura_contenido_nacional cn\n"
                + "	inner join si_factura sf on cn.si_factura  = sf.id \n"
                + "	inner join oc_factura_status ofs on ofs.si_factura = sf.id and ofs.actual  = true\n"
                + "	inner join proveedor p on sf.proveedor = p.id 	\n"
                + " where cn.eliminado  = false\n"
                + " and sf.eliminado  = false\n"
                + " and ofs.eliminado = false \n"
                + " and ofs.estatus > " + FacturaEstadoEnum.PROCESO_DE_PAGO.getId()
                + " and sf.compania  = '" + rfcCompania + "'"
                + " and extract(year from sf.fecha_genero) =  " + anio
                + " and sf.tipo_cambio  = 0 \n"
                + " group by p.nombre \n"
                + " order by facturado desc\n"
                + " )) as facturas\n"
                + " order by facturado  desc\n"
                + " limit 10";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<FacturaVo> facts = new ArrayList<FacturaVo>();
        for (Object[] obj : objs) {
            FacturaVo facVo = new FacturaVo();
            facVo.setProveedor((String) obj[0]);
            facVo.setMonto((BigDecimal) obj[1]);
            facVo.setSubTotal((BigDecimal) obj[2]);
            facVo.setPorcentaje((BigDecimal) obj[3]);
            facts.add(facVo);
        }
        return facts;
    }
}
