package sia.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.WebServiceException;
import mx.bigdata.sat.cfdi.CFDv33;
import mx.bigdata.sat.cfdi.v33.schema.Comprobante;
import mx.bigdata.sat.cfdi.v33.schema.TimbreFiscalDigital;
import mx.grupocorasa.sat.cfdi.v4.CFDv40;
import org.datacontract.schemas._2004._07.sat_cfdi_negocio_consultacfdi.Acuse;
import org.tempuri.ConsultaCFDIService;
import org.tempuri.IConsultaCFDIService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Valida el archivo XML de la factura. Se utiliza el servicio web publicado por
 * el SAT para verificar su vigencia y la biblioteca CFDI Base para verificar
 * que el XML no haya sufrido alteraciones.
 *
 * @author mrojas
 */
public final class ValidadorFactura {

    /**
     * URL del servicio web del SAT
     */
    private static final String SAT_WS_URL = "https://consultaqr.facturaelectronica.sat.gob.mx/ConsultaCFDIService.svc?wsdl";
    /**
     * Namespace de los métodos
     */
    private static final String QNAME_NAMESPACE = "http://tempuri.org/";
    /**
     * Servicio
     */
    private static final String QNAME_LOCAL_PART = "ConsultaCFDIService";

    /**
     * Formateo numérico
     */
    private static final DecimalFormat DEC_FORMAT = new DecimalFormat("0.00");

    /**
     * Salida para el archivo de bitácora
     */
    private static final UtilLog4j LOGGER = UtilLog4j.log;

    private ValidadorFactura() {
        super();
    }

    /**
     * Devolvemos un {@link Comprobante} a partir de un {@link InputStream}
     *
     * @param inputStream El flujo de bytes con el contenido del CFDI
     * @return El comprobante contenido en el {@link InputStream}
     * @throws JAXBException
     */
    public static Comprobante getComprobanteFromStream(final InputStream inputStream)
            throws JAXBException {
        final JAXBContext jaxbCtx = JAXBContext.newInstance(Comprobante.class);
        final Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
        return (Comprobante) unmarshaller.unmarshal(inputStream);
    }

    /**
     * Devolvemos un {@link Comprobante} a partir de un {@link File}.
     *
     * @param file El archivo con el contenido del CFDI.
     * @return El comprobante contenido en el {@link File}
     * @throws FileNotFoundException
     * @throws JAXBException
     */
    public static Comprobante getComprobanteFromFile(final File file)
            throws FileNotFoundException, JAXBException {
        final InputStream inputStream = new FileInputStream(file);
        return getComprobanteFromStream(inputStream);
    }

    public static Document getDocumentFromFile(final File file)
            throws IOException, SAXException {
        Document document = null;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            document = documentBuilder.parse(file);
            document.getDocumentElement().normalize();
        } catch (ParserConfigurationException e) {
            LOGGER.warn(ValidadorFactura.class, e);
        }

        return document;
    }

    /**
     * Validamos el {@link Comprobante}. Se valida la estructura del documento
     * XML y que no haya sufrido alteraciones.
     *
     * @param comp
     */
    public static void validarComprobante(final Comprobante comp) {
        LOGGER.info(ValidadorFactura.class, "*** Starting validation for CFDI {0}...", new Object[]{comp.getFolio()});

        try {
            final CFDv33 cfd = new CFDv33(comp);
            cfd.validar();
            cfd.verificar();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        LOGGER.info(ValidadorFactura.class, "*** Validation ended for CFDI {0} ...", new Object[]{comp.getFolio()});
    }

    /**
     * Se hacen las validaciones del comprobante contenido en el
     * {@link InputStream}.
     *
     * @see validaComprobante(Comprobante comprobante)
     * @param inputStream
     * @throws JAXBException
     */
    public static void validarComprobante(final InputStream inputStream) throws JAXBException {
        final Comprobante comp = getComprobanteFromStream(inputStream);
        validarComprobante(comp);
    }

    /**
     * Se hacen las validaciones del comprobante contenido en el {@link File}.
     *
     * @see validaComprobante(Comprobante comprobante)
     * @param file
     * @throws FileNotFoundException
     * @throws JAXBException
     */
    public static void validarComprobante(final File file) throws FileNotFoundException, JAXBException {
        final Comprobante comp = getComprobanteFromFile(file);
        validarComprobante(comp);
    }

    /**
     * Se realiza la verificación de la vigencia del comprobante contra el
     * servicio web proporcionado por el SAT.
     *
     * @param comp El comprobante a verificar
     * @return El estatus del comprobante devuelto por el SAT.
     */
    public static String verificarEstatusSAT(final Comprobante comp) {
        String retVal = null;
        try {
            AtomicReference<TimbreFiscalDigital> tfd
                    = new AtomicReference<>(new TimbreFiscalDigital());

            comp.getComplemento().forEach(compl -> {
                compl.getAny().stream()
                        .filter(TimbreFiscalDigital.class::isInstance)
                        .map(TimbreFiscalDigital.class::cast)
                        .findAny().ifPresent(c -> tfd.set(c));
            });

            //?re='.$emisor.'&amp;rr='.$receptor.'&amp;tt='.$total.'&amp;id='.$uuid.'
            final String expresionImpresa
                    = MessageFormat.format("?re={0}&rr={1}&tt={2}&id={3}",
                            comp.getEmisor().getRfc(),
                            comp.getReceptor().getRfc(),
                            DEC_FORMAT.format(comp.getTotal()),
                            tfd.get().getUUID()
                    );

            final URL url = new URL(SAT_WS_URL);
            final QName qName = new QName(QNAME_NAMESPACE, QNAME_LOCAL_PART);

            final ConsultaCFDIService service = new ConsultaCFDIService(url, qName);
            final IConsultaCFDIService s2 = service.getPort(IConsultaCFDIService.class);
            final Acuse acuse = s2.consulta(expresionImpresa);

            retVal = acuse.getCodigoEstatus().getValue();

            if (retVal != null) {
                LOGGER.info(ValidadorFactura.class, "*** Estado CFDI {0} ", new Object[]{retVal});
            }

        } catch (MalformedURLException e) {
            LOGGER.error("", e);
        }

        if (retVal == null) {
            retVal = "";
        }

        return retVal;
    }

    public static String getUUID(Document document) {
        String uuid = "";

        NodeList listaComplemento = document.getElementsByTagName("tfd:TimbreFiscalDigital");
        for (int i = 0; i < listaComplemento.getLength(); i++) {
            Node nodoCompl = listaComplemento.item(i);
            Element element = (Element) nodoCompl;
            uuid = element.getAttribute("UUID");
        }

        return uuid;
    }

    /**
     * Verifica el estatus del CFDI contenido en el archivo contra el servicio
     * web proporcionado por el SAT y la integridad del propio archivo XML.
     *
     * @param file
     * @return
     */
    public static boolean verificarEstatusSAT(final File file) {
        boolean retVal = false;

        try {
            final Comprobante comp = getComprobanteFromFile(file);
            validarComprobante(comp);

            final String estatusSAT = verificarEstatusSAT(comp);
            retVal = 'S' == estatusSAT.charAt(0);

        } catch (FileNotFoundException | JAXBException
                | IllegalArgumentException | WebServiceException ex) {
            LOGGER.warn("*** Error al validar el comprobante.", ex);
        }

        return retVal;
    }

    public static boolean verificarEstatusFactura4(final File file) {
        boolean retVal = false;

        try {
            final mx.grupocorasa.sat.cfd._40.Comprobante comprobante = getComprobanteFromStreamFactura4(file);
            validarComprobanteFactura4(comprobante);

            final String estatusSAT = verificarEstatusSATFactura4(comprobante);
            retVal = 'S' == estatusSAT.charAt(0);

        } catch (IllegalArgumentException | WebServiceException ex) {
            LOGGER.warn("*** Error al validar el comprobante.", ex);
        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(ValidadorFactura.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }

    public static mx.grupocorasa.sat.cfd._40.Comprobante getComprobanteFromStreamFactura4(final File file)
            throws FileNotFoundException, JAXBException {
        final InputStream inputStream = new FileInputStream(file);
        return getComprobanteFromStreamFactura4(inputStream);
    }

    public static void validarComprobanteFactura4(final mx.grupocorasa.sat.cfd._40.Comprobante comp) {
        LOGGER.info(ValidadorFactura.class, "*** Starting validation for CFDI {0}...", new Object[]{comp.getFolio()});
        System.out.println("*** Starting validation for CFDI {0}..." + comp.getFolio());
        try {
            CFDv40 cFDv4 = new CFDv40(comp);
            cFDv4.validar(null);
            cFDv4.verificar();
        } catch (Exception e) {
            System.out.println("*** Error..." + e);
            throw new IllegalArgumentException(e);
        }
        System.out.println("*** Validation ended for CFDI {0} ..." + comp.getFolio());

        LOGGER.info(ValidadorFactura.class, "*** Validation ended for CFDI {0} ...", new Object[]{comp.getFolio()});
    }

    public static String verificarEstatusSATFactura4(final mx.grupocorasa.sat.cfd._40.Comprobante comp) {
        String retVal = null;
        try {
            AtomicReference<TimbreFiscalDigital> tfd
                    = new AtomicReference<>(new TimbreFiscalDigital());

            comp.getComplemento().getAny().stream()
                    .filter(TimbreFiscalDigital.class::isInstance)
                    .map(TimbreFiscalDigital.class::cast)
                    .findAny().ifPresent(c -> tfd.set(c));

            //?re='.$emisor.'&amp;rr='.$receptor.'&amp;tt='.$total.'&amp;id='.$uuid.'
            final String expresionImpresa
                    = MessageFormat.format("?re={0}&rr={1}&tt={2}&id={3}",
                            comp.getEmisor().getRfc(),
                            comp.getReceptor().getRfc(),
                            DEC_FORMAT.format(comp.getTotal()),
                            tfd.get().getUUID()
                    );

            final URL url = new URL(SAT_WS_URL);
            final QName qName = new QName(QNAME_NAMESPACE, QNAME_LOCAL_PART);

            final ConsultaCFDIService service = new ConsultaCFDIService(url, qName);
            final IConsultaCFDIService s2 = service.getPort(IConsultaCFDIService.class);
            final Acuse acuse = s2.consulta(expresionImpresa);

            retVal = acuse.getCodigoEstatus().getValue();

            if (retVal != null) {
                LOGGER.info(ValidadorFactura.class, "*** Estado CFDI {0} ", new Object[]{retVal});
            }

        } catch (MalformedURLException e) {
            LOGGER.error("", e);
        }

        if (retVal == null) {
            retVal = "";
        }

        return retVal;
    }

    public static mx.grupocorasa.sat.cfd._40.Comprobante getComprobanteFromStreamFactura4(final InputStream inputStream)
            throws JAXBException {
        final JAXBContext jaxbCtx = JAXBContext.newInstance(mx.grupocorasa.sat.cfd._40.Comprobante.class);
        final Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
        return (mx.grupocorasa.sat.cfd._40.Comprobante) unmarshaller.unmarshal(inputStream);
    }
}
