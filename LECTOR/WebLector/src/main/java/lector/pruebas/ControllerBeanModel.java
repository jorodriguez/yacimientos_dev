/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.pruebas;

import java.io.Serializable;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lector.util.UtilLog4j;

@Named
@ConversationScoped
public class ControllerBeanModel implements Serializable {

    @Inject
    private Conversation conversation;
    private Integer contador = 0;
    private String texto;

    public void beginConversation() {
        if (conversation.isTransient()) {
            UtilLog4j.log.info(this, "Antes de iniciar la Conversación--");
        }
        UtilLog4j.log.info(this, "bean: " + this);
        UtilLog4j.log.info(this, "contador: " + this.contador);
        UtilLog4j.log.info(this, "conversation: " + this.conversation.getId());

        conversation.begin();
    }

    public void endConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }

    }

    public void sumar() {
        UtilLog4j.log.info(this, "Sumando uno");
        this.contador++;
    }

    /**
     * @return the contador
     */
    public Integer getContador() {
        return contador;
    }

    /**
     * @param contador the contador to set
     */
    public void setContador(Integer contador) {
        this.contador = contador;
    }

    /**
     * @return the texto
     */
    public String getTexto() {
        return texto;
    }

    /**
     * @param texto the texto to set
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }
}