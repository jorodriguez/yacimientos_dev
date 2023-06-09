/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.sistema.bean.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import lector.util.UtilLog4j;

/**
 * Clase que provee diversos métodos para poder administrar las Conversaciones
 * que tenga el Usuario durante su sesión en el Sistema
 * 
 * @author sluis
 */
@Named
@SessionScoped
public class ConversationsManager implements Serializable {

    public ConversationsManager(){
        
    }
    
    private TreeMap<String,Conversation> conversaciones = new TreeMap<String,Conversation>();

    /**
     * Regresa todas las Conversaciones iniciadas
     * @return
     */
    public List<Conversation> getAllConversacionesList() {
        return new ArrayList(conversaciones.values());
    }

    /**
     * Inicia una Conversación y lagrega al Administrador de Conversaciones
     * Por default a la Conversación se le asigna un tiempo de vida de 1 800 000 milisegundos o 30 min.
     *
     * @param conversacion
     * @param idConversacion 
     */
    public void beginConversation(Conversation conversacion, String idConversacion) {
        if (conversacion.isTransient()) {
            conversacion.begin();
            conversacion.setTimeout(1800000);
            UtilLog4j.log.info(this, ">>Iniciada conversación: " + idConversacion + "<<");
            this.conversaciones.put(idConversacion, conversacion);
        }
        else {
            UtilLog4j.log.info(this, "No se pudo iniciar la conversación  " + idConversacion + " porque no es Transient");
        }
    }
    
    /**
     * Inicia una Conversación y lagrega al Administrador de Conversaciones
     * @param conversation
     * @param milisecondsTime - Tiempo de vida especificado de la Conversación en milisegundos
     * @param idConversation
     */
    public void beginConversation(Conversation conversacion, Long milisecondsTimeLife, String idConversacion) {
        if(conversacion.isTransient()) {
            conversacion.begin();
            conversacion.setTimeout(milisecondsTimeLife);
            this.conversaciones.put(idConversacion, conversacion);
            UtilLog4j.log.info(this, ">>Iniciada conversación: " + idConversacion + "<<");
        }
        else {
            UtilLog4j.log.info(this, "No se pudo iniciar la conversación  " + idConversacion + " porque no es Transient");
        }
    }

    /**
     * Regresa la cantidad de Conversaciones iniciadas del Sistema
     * @return
     */
    public Integer countConversations() {
        return this.conversaciones.size();
    }

    /**
     * Finaliza todas las Conversaciones iniciadas del Sistema
     * Si se pudieron terminar todas las Conversaciones devuelve 'True', caso contrario,
     * devuelve 'False'
     *
     * @return
     */
    public void finalizeAllConversations() {
        int contConversacionesNoTerminadas = 0;
        int contConversacionesTerminadas = 0;
        if (!this.conversaciones.isEmpty()) {
            List<Conversation> conversaciones = new ArrayList(this.conversaciones.values());

            UtilLog4j.log.info(this, "Se encontraron " + conversaciones.size() + " conversaciones");
            
            for (Conversation c : conversaciones) {
                if(finalizeConversation(c)) {
                   contConversacionesTerminadas++; 
                }
                else {
                    contConversacionesNoTerminadas++;
                }
            }
            this.conversaciones.clear();
            UtilLog4j.log.info(this, "Se finalizaron: " + contConversacionesTerminadas + " conversaciones");
            UtilLog4j.log.info(this, "No se pudieron finalizar: " + contConversacionesNoTerminadas + " conversaciones");
        } else {
            UtilLog4j.log.info(this, "No hay conversaciones para terminar");
        }
    }

    /**
     * Finaliza una Conversación
     * @param c
     * @return
     */
    public boolean finalizeConversation(Conversation conversacion) {

        boolean isConversacionFinished = true;
        
        if (this.conversaciones.containsValue(conversacion)) {
            if(!conversacion.isTransient()) {
                UtilLog4j.log.info(this, "Finalizada la conversación: " + conversacion.getId());
                conversacion.end();
            }
            else {
                UtilLog4j.log.info(this, "No se pude finalizar la conversación porque ya es Transient");
                isConversacionFinished = !isConversacionFinished;
            }
        }
       return isConversacionFinished;
    }
    
    public boolean finalizeConversation(String keyConversacion) {
        
        boolean isConversacionFinished = true;
        
        if(this.conversaciones.containsKey(keyConversacion)) {
            Conversation conversacion = this.conversaciones.get(keyConversacion);
            if(!conversacion.isTransient()) {
                conversacion.end();
                this.conversaciones.remove(keyConversacion);
                UtilLog4j.log.info(this, "Finalizada la conversación: " + conversacion.getId());
            }
            else {
                UtilLog4j.log.info(this, "No se pudo finalizar la conversación porque ya es Transient");
                isConversacionFinished = !isConversacionFinished;
            }            
        }
         return isConversacionFinished;
    }
}
