
package org.datacontract.schemas._2004._07.sat_cfdi_negocio_consultacfdi;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.datacontract.schemas._2004._07.sat_cfdi_negocio_consultacfdi package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Acuse_QNAME = new QName("http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", "Acuse");
    private final static QName _AcuseEsCancelable_QNAME = new QName("http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", "EsCancelable");
    private final static QName _AcuseCodigoEstatus_QNAME = new QName("http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", "CodigoEstatus");
    private final static QName _AcuseEstado_QNAME = new QName("http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", "Estado");
    private final static QName _AcuseEstatusCancelacion_QNAME = new QName("http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", "EstatusCancelacion");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.datacontract.schemas._2004._07.sat_cfdi_negocio_consultacfdi
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Acuse }
     * 
     */
    public Acuse createAcuse() {
        return new Acuse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Acuse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", name = "Acuse")
    public JAXBElement<Acuse> createAcuse(Acuse value) {
        return new JAXBElement<Acuse>(_Acuse_QNAME, Acuse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", name = "EsCancelable", scope = Acuse.class)
    public JAXBElement<String> createAcuseEsCancelable(String value) {
        return new JAXBElement<String>(_AcuseEsCancelable_QNAME, String.class, Acuse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", name = "CodigoEstatus", scope = Acuse.class)
    public JAXBElement<String> createAcuseCodigoEstatus(String value) {
        return new JAXBElement<String>(_AcuseCodigoEstatus_QNAME, String.class, Acuse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", name = "Estado", scope = Acuse.class)
    public JAXBElement<String> createAcuseEstado(String value) {
        return new JAXBElement<String>(_AcuseEstado_QNAME, String.class, Acuse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", name = "EstatusCancelacion", scope = Acuse.class)
    public JAXBElement<String> createAcuseEstatusCancelacion(String value) {
        return new JAXBElement<String>(_AcuseEstatusCancelacion_QNAME, String.class, Acuse.class, value);
    }

}
