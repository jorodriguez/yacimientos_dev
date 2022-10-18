

package sia.modelo.oficio.vo;

import java.io.File;
import sia.constantes.Constantes;
import sia.modelo.sgl.vo.AdjuntoVO;

/**
 * Contiene la información de un archivo adjunto para el módulo de Control 
 * de Oficios.
 *
 * @author esapien
 */
public class AdjuntoOficioVo extends AdjuntoVO {
    
    private String directorio;
    private Long tamanoArchivo;
    
    // ruta del archivo para mostrarlo en la utilería ViewerJS
    
    private String urlVisorArchivoTemporal;
    private String urlArchivoTemporal;
    
    // referencia al archivo subido inicialmente (upload)
    private File archivoSubido;
    
    
    // bandera para indicar que se ha actualizado el archivo en un proceso de 
    // modificación de un registro de oficio
    private boolean archivoGuardado;
    
    public String getDirectorio() {
        return directorio;
    }

    public void setDirectorio(String directorio) {
        this.directorio = directorio;
    }

    public Long getTamanoArchivo() {
        return tamanoArchivo;
    }

    public void setTamanoArchivo(Long tamanoArchivo) {
        this.tamanoArchivo = tamanoArchivo;
    }
    
    public String getRutaArchivo() {
        
        String resultado = null;
        
        if (this.getArchivoSubido() != null) {
            resultado = directorio + this.getArchivoSubido().getName();
        }
        
        return resultado;
        
    }

    public boolean isArchivoGuardado() {
        return archivoGuardado;
    }

    public void setArchivoGuardado(boolean archivoGuardado) {
        this.archivoGuardado = archivoGuardado;
    }

    public String getUrlArchivoTemporal() {
        return urlArchivoTemporal;
    }

    public void setUrlArchivoTemporal(String urlArchivoTemporal) {
        this.urlArchivoTemporal = urlArchivoTemporal;
    }

    public String getUrlVisorArchivoTemporal() {
        return urlVisorArchivoTemporal;
    }

    public void setUrlVisorArchivoTemporal(String urlVisorArchivoTemporal) {
        this.urlVisorArchivoTemporal = urlVisorArchivoTemporal;
    }

    public File getArchivoSubido() {
        return archivoSubido;
    }

    public void setArchivoSubido(File archivoSubido) {
        this.archivoSubido = archivoSubido;
    }
    
    
    /**
     * Bandera para indicar que este archivo es visualizable por el componente
     * visor en pantalla (ViewerJS).
     * 
     * Regresa Verdadero si el archivo es de tipo válido: PDF u ODT.
     * 
     * @return 
     */
    public boolean isVisualizable() {
        
        boolean result;
        
        String tipoAdjunto = getTipoArchivo().trim();

        String tipoPdf = Constantes.CONTENT_TYPE_PDF;
        String tipoOdt = Constantes.CONTENT_TYPE_ODT;

        result = tipoPdf.equalsIgnoreCase(tipoAdjunto)
                || tipoOdt.equalsIgnoreCase(tipoAdjunto);
        
        return result;
        
    }
    
    /**
     * 
     * @return 
     */
    
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("{id = ").append(this.getId());
        sb.append(", url = ").append(this.getUrl());
        sb.append(", nombre = ").append(this.getNombre());
        sb.append(", rutaArchivo = ").append(this.getRutaArchivo());
        sb.append(", tipoArchivo = ").append(this.getTipoArchivo());
        sb.append(", tamanoArchivo = ").append(this.getTamanoArchivo());
        sb.append(", archivoSubido = ").append(this.getArchivoSubido());
        sb.append(", archivoGuardado = ").append(this.isArchivoGuardado());
        sb.append(", urlArchivoTemporal = ").append(this.urlArchivoTemporal);
        sb.append("}");
        
        return sb.toString();
    }
    
}
