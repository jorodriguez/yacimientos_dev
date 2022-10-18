/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.bitacora.bean.model;

import java.io.Serializable;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.sgl.noticia.bean.model.NoticiaListModel;
import sia.modelo.*;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.ConversationsManager;

/**
 *
 * @author jrodriguez
 */
@Named
@ConversationScoped
public class BitacoraSolicitudViajeBeanModel implements Serializable {

    @Inject
    private Sesion sesion;
    @Inject
    private Conversation conversation;
    @Inject
    private ConversationsManager conversationsManager;
    
    @Inject 
    private NoticiaListModel noticiaBeanModel;

    /**
     * Creates a new instance of mantenimientoBeanModel
     */
    public BitacoraSolicitudViajeBeanModel() {
    }

    public void beginBitacoraSolicitudViaje() {
        this.conversationsManager.finalizeAllConversations();
        this.conversationsManager.beginConversation(conversation, BitacoraSolicitudViajeBeanModel.class.getName());
    }
    
    
   
}
