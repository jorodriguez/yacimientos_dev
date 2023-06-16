package mx.ihsa.archivador;

import mx.ihsa.excepciones.GeneralException;

/**
 * Almacén de documentos anexos.
 */
public abstract class AlmacenDocumentos {
    
    private String raizAlmacen = "/";
    
    public abstract void guardarDocumento(DocumentoAnexo documento) throws GeneralException;
    public abstract void borrarDocumento(DocumentoAnexo documento) throws GeneralException;
    public abstract void borrarDocumento(String rutaCompleta) throws GeneralException;
    public abstract DocumentoAnexo cargarDocumento(String rutaCompleta) throws GeneralException;
    public abstract void moverDocumento(DocumentoAnexo documento, String nuevaRuta) throws GeneralException;
    
    public String getRaizAlmacen() {
        return raizAlmacen;
    }
    
    public void setRaizAlmacen(String raizAlmacen) {
        this.raizAlmacen = raizAlmacen;
    }
}
