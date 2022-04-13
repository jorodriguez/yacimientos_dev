
package org.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.sat_cfdi_negocio_consultacfdi.Acuse;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ConsultaResult" type="{http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio}Acuse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "consultaResult"
})
@XmlRootElement(name = "ConsultaResponse")
public class ConsultaResponse {

    @XmlElementRef(name = "ConsultaResult", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<Acuse> consultaResult;

    /**
     * Gets the value of the consultaResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Acuse }{@code >}
     *     
     */
    public JAXBElement<Acuse> getConsultaResult() {
        return consultaResult;
    }

    /**
     * Sets the value of the consultaResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Acuse }{@code >}
     *     
     */
    public void setConsultaResult(JAXBElement<Acuse> value) {
        this.consultaResult = value;
    }

}
