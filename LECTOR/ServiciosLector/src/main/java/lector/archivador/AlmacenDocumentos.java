package lector.archivador;

import lector.excepciones.SIAException;

/**
 * Almacén de documentos anexos.
 * @author mrojas
 */
public abstract class AlmacenDocumentos {
    
    private String raizAlmacen = "/";
    
    public abstract void guardarDocumento(DocumentoAnexo documento) throws SIAException;
    public abstract void borrarDocumento(DocumentoAnexo documento) throws SIAException;
    public abstract void borrarDocumento(String rutaCompleta) throws SIAException;
    public abstract DocumentoAnexo cargarDocumento(String rutaCompleta) throws SIAException;
    public abstract void moverDocumento(DocumentoAnexo documento, String nuevaRuta) throws SIAException;
    
    public String getRaizAlmacen() {
        return raizAlmacen;
    }
    
    public void setRaizAlmacen(String raizAlmacen) {
        this.raizAlmacen = raizAlmacen;
    }
}
