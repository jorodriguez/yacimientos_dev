/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.excepciones;

/**
 * Deberá usarse cuando se intente duplicar un elemento ya existente en el
 * Sistema.
 *
 * Normalmente serán registros de nombres duplicados, pero no está limitado a
 * ellos solamente
 *
 * El mensaje mostrado por esta excepción es: "Ya existe el elemento"
 *
 * @author b75ckd35th
 */
public class ExistingItemException extends SIAException {

    /**
     * Nombre del elemento que ya existe y que provoca la excepción
     */
    private String nombreElemento;
    /**
     * Elemento que provoca la Excepción
     */
    private Object elemento;

    public ExistingItemException() {
        super.setLiteral("sistema.mensaje.error.elementoExistente");
    }

    /**
     *
     * @param nombreElementoExistente
     */
    public ExistingItemException(String nombreElemento) {
        super.setLiteral("sistema.mensaje.error.elementoExistente");
        this.nombreElemento = nombreElemento;
    }

    /**
     *
     * @param nombreElemento
     * @param elemento
     */
    public ExistingItemException(String nombreElemento, Object elemento) {
        super.setLiteral("sistema.mensaje.error.elementoExistente");
        this.nombreElemento = nombreElemento;
        this.elemento = elemento;
    }
    
    /**
     *
     * @param literal 
     * @param nombreElemento
     * @param elemento
     */
    public ExistingItemException(String literal, String nombreElemento, Object elemento) {
        super.setLiteral(literal);
        this.nombreElemento = nombreElemento;
        this.elemento = elemento;
    }    

    /**
     * @return the nombreElemento
     */
    public String getNombreElemento() {
        return nombreElemento;
    }

    /**
     * @param nombreElemento the nombreElemento to set
     */
    public void setNombreElemento(String nombreElemento) {
        this.nombreElemento = nombreElemento;
    }

    /**
     * @return the elemento
     */
    public Object getElemento() {
        return elemento;
    }

    /**
     * @param elemento the elemento to set
     */
    public void setElemento(Object elemento) {
        this.elemento = elemento;
    }

    
    public String toString() {
        return "ExistingItemException{" + "nombreElemento=" + nombreElemento + ", elemento=" + elemento + '}';
    }
    
}
