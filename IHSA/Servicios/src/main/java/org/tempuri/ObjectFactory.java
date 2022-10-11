
package org.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.datacontract.schemas._2004._07.sat_cfdi_negocio_consultacfdi.Acuse;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.tempuri package. 
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

    private final static QName _ConsultaResponseConsultaResult_QNAME = new QName("http://tempuri.org/", "ConsultaResult");
    private final static QName _ConsultaExpresionImpresa_QNAME = new QName("http://tempuri.org/", "expresionImpresa");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.tempuri
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Consulta }
     * 
     */
    public Consulta createConsulta() {
        return new Consulta();
    }

    /**
     * Create an instance of {@link ConsultaResponse }
     * 
     */
    public ConsultaResponse createConsultaResponse() {
        return new ConsultaResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Acuse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "ConsultaResult", scope = ConsultaResponse.class)
    public JAXBElement<Acuse> createConsultaResponseConsultaResult(Acuse value) {
        return new JAXBElement<Acuse>(_ConsultaResponseConsultaResult_QNAME, Acuse.class, ConsultaResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "expresionImpresa", scope = Consulta.class)
    public JAXBElement<String> createConsultaExpresionImpresa(String value) {
        return new JAXBElement<String>(_ConsultaExpresionImpresa_QNAME, String.class, Consulta.class, value);
    }

}
