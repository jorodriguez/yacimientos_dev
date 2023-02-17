/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.archivador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lector.excepciones.LectorException;
import lector.util.UtilLog4j;


/**
 * Implementación de almacén de documentos con acceso al sistema de archivos. Se
 * asume que los documentos se almacenan en una estructura de directorios del
 * servidor, a los cuales se accede utilizando una ruta proporcionada.
 *
 */
public class AlmacenSistemaArchivos extends AlmacenDocumentos {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @Override
    public void guardarDocumento(DocumentoAnexo documento) throws LectorException {
	FileOutputStream fos = null;
	/*String rutaBase
	 = getRaizAlmacen()
	 + File.separator
	 + documento.getRuta();*/

	String rutaCompleta
		= getRaizAlmacen() + File.separator
		+ documento.getRuta() + File.separator
		+ documento.getNombreBase();

	File archivo = new File(rutaCompleta);

	LOGGER.info(this, "Guardando archivo {0} - {1} - {2} ",
		new Object[]{documento.getNombreBase(), documento.getRuta(), rutaCompleta}
	);

	try {
	    Path directory = Paths.get(getRaizAlmacen() + File.separator + documento.getRuta());
	    if (Files.exists(directory)) {
		LOGGER.info("The directory {} already exists.", documento.getRuta());
	    } else {
		Files.createDirectories(directory);
	    }

	    fos = new FileOutputStream(archivo);
	    fos.write(documento.getContenido());
	} catch (FileNotFoundException ex) {
	    LOGGER.error(this, ex);

	    throw new LectorException("No fue posible guardar el archivo : " + ex.getMessage());
	} catch (IOException ex) {
	    LOGGER.error(this, ex);

	    throw new LectorException("No fue posible guardar el archivo : " + ex.getMessage());
	} finally {
	    if (fos != null) {
		try {
		    fos.close();

		    if (!Files.exists(Paths.get(rutaCompleta))) {
			//throw new SIAException("No existe el archivo físico.");
			LOGGER.error(this, "No existe el archivo fisico: {0}", new Object[]{rutaCompleta});
		    }
		} catch (IOException ex) {
		    LOGGER.error(this, ex);
		}
	    }
	}
    }

    @Override
    public void borrarDocumento(DocumentoAnexo documento) throws LectorException {
	borrarDocumento(documento.getRuta());
    }

    @Override
    public void borrarDocumento(String rutaCompleta) throws LectorException {
	LOGGER.debug(this, "Borrando archivo {0}", new Object[]{rutaCompleta});
	//System.out.println("Borrando el archivo " + new Object[]{rutaCompleta});

	try {
	    Files.delete(Paths.get(getRaizAlmacen() + File.separator + rutaCompleta));
	} catch (IOException ex) {
	    LOGGER.error(this, ex);
	    throw new LectorException("No fue posible borrar el archivo: " + ex.getMessage());
	}
    }

    @Override
    public DocumentoAnexo cargarDocumento(String rutaCompleta) throws LectorException {
	InputStream inFile = null;
	DocumentoAnexo retVal = null;

	LOGGER.info(this, "Cargando archivo {0}", new Object[]{rutaCompleta});

	try {

	    Path path = Paths.get(getRaizAlmacen() + File.separator + rutaCompleta);

	    inFile = Files.newInputStream(path);

	    byte[] data = new byte[inFile.available()];
	    inFile.read(data);

	    retVal = new DocumentoAnexo(data);
	    retVal.setRuta(rutaCompleta);
	    retVal.setNombreBase(Utilerias.obtenerNombreBase(path));

	} catch (IOException e) {
	    LOGGER.error(this, e);
	    throw new LectorException(
		    "Al cargar el documento desde el sistema de archivos del servidor"
		    + e.getMessage()
	    );
	} finally {
	    if (inFile != null) {
		try {
		    inFile.close();
		} catch (IOException e) {
		    LOGGER.error(this, e);
		}
	    }
	}

	return retVal;
    }

    @Override
    public void moverDocumento(DocumentoAnexo documento, String nuevaRuta) throws LectorException {
	//TODO : implementar funcionalidad
	LOGGER.debug(this, "Moviendo archivo {0} a {1}", new Object[]{documento.getNombreBase(), nuevaRuta});
	throw new UnsupportedOperationException("Not supported yet.");
    }

}
