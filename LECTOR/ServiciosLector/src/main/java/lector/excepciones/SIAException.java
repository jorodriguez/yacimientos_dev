/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.excepciones;


import javax.ejb.ApplicationException;

/**
 * Superclase a partir de la cuál extenderán todas las excepciones
 * personalizadas del SIA
 *
 * Esta excepcíón tiene un mensaje predefinido en la propiedad heredada
 * 'message', la cual dice: "Oops! Algo está mal por aquí. Por favor contacta al
 * Equipo del SIA para arreglar esto"
 *
 * @author b75ckd35th
 */
@ApplicationException(rollback=true)
public class SIAException extends Exception {

    /**
     * Nombre de la Clase donde ocurre la excepción
     */
    private String claseError;
    /**
     * Nombre del Método donde ocurre la excepción
     */
    private String metodoError;
    
    /**
     * Literal que se mostrará en la vista
     */
    private String literal;
    
    /**
     * Mensaje para el programador o que se puede imprimir en el log
     */
    private String mensajeParaProgramador;
    
    public SIAException() {
        super("Ocurrio algo inesperado, por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx.");
    }
    
    public SIAException(Exception e) {
        super(e);
    }
    
    /**
     * 
     * @param mensaje 
     */
    public SIAException (String mensaje) {
        super(mensaje);
    }
    
    /**
     * 
     * @param claseError
     * @param metodoError
     * @param mensaje 
     */
    public SIAException(String claseError, String metodoError, String mensaje) {
        super(mensaje);
        this.claseError = claseError;
        this.metodoError = metodoError;
    }
    
    /**
     * @param claseError
     * @param metodoError
     * @param mensaje
     * @param mensajeParaProgramador
     */
    public SIAException(String claseError, String metodoError, String mensaje, String mensajeParaProgramador) {
        super(mensaje);
        this.claseError = claseError;
        this.metodoError = metodoError;
        this.mensajeParaProgramador = SIAException.class.getName() + " - " + mensajeParaProgramador;
    } 
    
    /**
     * @param claseError
     * @param metodoError
     * @param mensaje
     * @param literal
     * @param mensajeParaProgramador
     */
    public SIAException(String claseError, String metodoError, String mensaje, String literal, String mensajeParaProgramador) {
        super(mensaje);
        this.claseError = claseError;
        this.metodoError = metodoError;
        this.literal = literal;
        this.mensajeParaProgramador = SIAException.class.getName() + " - " + mensajeParaProgramador;
    }  
    
    /**
     * @return the claseError
     */
    public String getClaseError() {
        return claseError;
    }

    /**
     * @param claseError the claseError to set
     */
    public void setClaseError(String claseError) {
        this.claseError = claseError;
    }

    /**
     * @return the metodoError
     */
    public String getMetodoError() {
        return metodoError;
    }

    /**
     * @param metodoError the metodoError to set
     */
    public void setMetodoError(String metodoError) {
        this.metodoError = metodoError;
    }

    /**
     * @return the mensajeParaProgramador
     */
    public String getMensajeParaProgramador() {
        return mensajeParaProgramador;
    }

    /**
     * @param mensajeParaProgramador the mensajeParaProgramador to set
     */
    public void setMensajeParaProgramador(String mensajeParaProgramador) {
        this.mensajeParaProgramador = mensajeParaProgramador;
    }

    /**
     * @return the literal
     */
    public String getLiteral() {
        return literal;
    }

    /**
     * @param literal the literal to set
     */
    public void setLiteral(String literal) {
        this.literal = literal;
    }

    
    public String toString() {
        return "SIAException{" + "claseError=" + claseError + ", metodoError=" + metodoError + ", literal=" + literal + ", mensajeParaProgramador=" + mensajeParaProgramador + '}';
    }
}
