package mx.ihsa.archivador;

import mx.ihsa.excepciones.LectorException;

/**
 * Almacén de documentos anexos.
 */
public abstract class AlmacenDocumentos {
    
    private String raizAlmacen = "/";
    
    public abstract void guardarDocumento(DocumentoAnexo documento) throws LectorException;
    public abstract void borrarDocumento(DocumentoAnexo documento) throws LectorException;
    public abstract void borrarDocumento(String rutaCompleta) throws LectorException;
    public abstract DocumentoAnexo cargarDocumento(String rutaCompleta) throws LectorException;
    public abstract void moverDocumento(DocumentoAnexo documento, String nuevaRuta) throws LectorException;
    
    public String getRaizAlmacen() {
        return raizAlmacen;
    }
    
    public void setRaizAlmacen(String raizAlmacen) {
        this.raizAlmacen = raizAlmacen;
    }
}
