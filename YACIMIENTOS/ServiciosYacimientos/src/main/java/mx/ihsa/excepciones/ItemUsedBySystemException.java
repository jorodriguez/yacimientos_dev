/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.excepciones;

/**
 * Deberá usarse cuando se intenté duplicar un elemento ya existente en el
 * Sistema.
 *
 * Normalmente podrán serán registros de nombres duplicados, pero no está
 * limitado a ellos solamente
 *
 * El mensaje mostrado por esta excepción es: "El registro no puede ser
 * eliminado debido a que ya ha sido utilizado en alguna otra parte del sistema"
 *
 * @author b75ckd35th
 */
public class ItemUsedBySystemException extends GeneralException {

    /**
     * Nombre del elemento que ya existe y que provoca la excepción
     */
    private String nombreElemento;
    /**
     * Elemento que provoca la Excepción
     */
    private Object elemento;

    public ItemUsedBySystemException() {
        super.setLiteral("sistema.mensaje.error.eliminar.registroUsado");
    }

    /**
     *
     * @param nombreElementoExistente
     */
    public ItemUsedBySystemException(String nombreElemento) {
        super.setLiteral("sistema.mensaje.error.eliminar.registroUsado");
        this.nombreElemento = nombreElemento;
    }

    /**
     *
     * @param nombreElemento
     * @param elemento
     */
    public ItemUsedBySystemException(String nombreElemento, Object elemento) {
        super.setLiteral("sistema.mensaje.error.eliminar.registroUsado");
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
}
