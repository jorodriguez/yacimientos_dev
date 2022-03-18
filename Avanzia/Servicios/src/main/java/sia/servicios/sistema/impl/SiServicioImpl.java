/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.TreeMap;
import javax.ejb.Stateless;

/**
 *
 * @author hacosta
 */
@Stateless
public class SiServicioImpl{

    private TreeMap<String,Boolean> controladorPopups = new TreeMap<String,Boolean>();
    
    
    public String[] getElementos(String c) {
        return c.split(",");
    }

//    
//    public void putValPopup(String keyPopup, Boolean value) {
//        System.out.println("Agregando popup: " + keyPopup + "|" + value);
//        this.controladorPopups.put(keyPopup, value);
//    }
//
//    
//    public boolean getValPopup(String keyPopup) throws Exception {
//        if(this.controladorPopups.containsKey(keyPopup)) {
//            return this.controladorPopups.get(keyPopup);
//        }
//        else {
//            throw new SIAException(SiServicioImpl.class.getName()
//                    , "getValPopup(String keyPopup)"
//                    , "Ocurrió un error al mostrar el Popup"
//                    , "No se encontró el popup con key: " + keyPopup);
//        }
//    }
//
//    
//    public TreeMap<String, Boolean> getControladorPopups() {
//        return this.controladorPopups;
//    }
//
//    
//    public boolean controladorPopupsContainsKey(String keyPopup) {
//        return this.controladorPopupsContainsKey(keyPopup);
//    }
}
