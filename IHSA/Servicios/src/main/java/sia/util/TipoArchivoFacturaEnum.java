/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.util;

import java.io.Serializable;

/**
 *
 * @author mluis
 */
public enum TipoArchivoFacturaEnum implements Serializable{
    
    TIPO_XML("XML (Factura)"),
    TIPO_PDF("PDF (Factura)"),
    TIPO_CXP_PDF("PDF (CXP)"),
    TIPO_CXP_XML("XML (CXP)"),
    TIPO_SOPORTE("SOPORTES"),
    TIPO_COMPLEMENTO("COMPLEMENTO"),
    TIPO_DOCUMENTO_ADUANAL("DOCUMENTO ADUANAL"),
    TIPO_PAGO("PAGO"),
    TIPO_NC_XML("XML (Nota Credito)"),
    TIPO_NC_PDF("PDF (Nota Credito)");

    private final String text;

    /**
     * @param text
     */
    TipoArchivoFacturaEnum(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    
    public String toString() {
        return text;
    }
}
