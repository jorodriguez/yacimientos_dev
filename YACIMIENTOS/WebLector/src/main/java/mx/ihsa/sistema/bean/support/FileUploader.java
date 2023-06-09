/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.ihsa.sistema.bean.support;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author hacosta
 */

public class FileUploader implements Serializable {
    public static final Log log = LogFactory.getLog(FileUploader.class);
    // File sizes used to generate formatted label
    private static final long MEGABYTE_LENGTH_BYTES = 1048000l;
    private static final long KILOBYTE_LENGTH_BYTES = 1024l;
    // file upload completed percent (Progress)
    private int fileProgress;
    //private FileInfo archivoCargado;
    private String uploadDirectory = "";
    private String systemUploadDirectory;
    /** Creates a new instance of FileUploader */
    public FileUploader() {
//        this.systemUploadDirectory = this.parametrosSistemaServicioImpl.buscarPorId(0).getUploadDirectory();
    }

}
