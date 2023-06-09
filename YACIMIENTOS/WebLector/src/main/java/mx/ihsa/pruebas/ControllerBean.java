
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.pruebas;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;

import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author javier
 */
@Named
@RequestScoped
public class ControllerBean implements Serializable {

    @Inject
    private ControllerBeanModel controllerBeanModel;

    public void sumar() {
        this.controllerBeanModel.sumar();
    }

    public void beginConversation() {
        this.controllerBeanModel.beginConversation();
    }

    public void endConversation() {
        this.controllerBeanModel.endConversation();
    }

    /**
     * @return the contador
     */
    public Integer getContador() {
        return this.controllerBeanModel.getContador();
    }

    /**
     * @param contador the contador to set
     */
    public void setContador(Integer contador) {
        this.controllerBeanModel.setContador(contador);
    }
    
    /**
     * @return the texto
     */
    public String getTexto() {
        return this.controllerBeanModel.getTexto();
    }

    /**
     * @param texto the texto to set
     */
    public void setTexto(String texto) {
        this.controllerBeanModel.setTexto(texto);
    }    
}
