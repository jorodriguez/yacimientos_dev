/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.excepciones;

/**
 *
 * Deberá usarse cuando se detecte que se intenta utilizar un archivo cuyo
 * formato no está permitido en el proceso en curso.
 * 
 * @author esapien
 */
public class InvalidFileTypeException extends LectorException {

    public InvalidFileTypeException() {
    }
    
    public InvalidFileTypeException(String mensaje) {
        super(mensaje);
    }
    
}
