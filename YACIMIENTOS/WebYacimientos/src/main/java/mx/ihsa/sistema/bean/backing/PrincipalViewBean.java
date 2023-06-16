/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.sistema.bean.backing;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author jorodriguez
 */
@Named
@ViewScoped
public class PrincipalViewBean implements Serializable {

    @Inject
    private Sesion sesion;
    

    public PrincipalViewBean() {
    }

    @PostConstruct
    public void iniciar() {
        System.out.println("@Postconstruc");
        //loaders
    }


    private void redireccionar(String url) {
        try {
            System.out.println("redirect " + url);
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException ex) {
            Logger.getLogger(PrincipalViewBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
     public void iniciarInformacionModulos() {
    }

}
