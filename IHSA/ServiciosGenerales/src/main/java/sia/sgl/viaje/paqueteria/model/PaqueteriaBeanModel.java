/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.paqueteria.model;

import java.awt.event.ActionEvent;
import javax.inject.Named;
import javax.enterprise.context.ConversationScoped;
import java.io.Serializable;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import sia.modelo.SgSolicitudViaje;
import sia.modelo.SgViajero;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.ConversationsManager;

/**
 *
 * @author jrodriguez
 */
@Named(value = "paqueteriaBeanModel")
@ConversationScoped
public class PaqueteriaBeanModel implements Serializable {

    @Inject
    private Sesion sesion;
    @Inject
    private Conversation conversation;
    @Inject
    private ConversationsManager conversationsManager;
    
    
    
    public PaqueteriaBeanModel() {
    }
    
     public void beginConversationSolicitudPaqueteria() {
        if (this.conversation.isTransient()) {
            this.conversation.setTimeout(1800000);
            this.conversation.begin();            
        }
    }
     
   
     
}
