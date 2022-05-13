package com.ihsa.sia.commons;

import java.io.Serializable;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;
import sia.modelo.usuario.vo.UsuarioVO;

/**
 *
 * @author Aplimovil SA de CV
 */
@Named(value="principal")
@SessionScoped
public class SessionBean implements Serializable {
    private boolean loggedIn;
    private UsuarioVO user;
    
    
    public String login() {
        return "inventory/unit" + "?faces-redirect=true";
    }

    public UsuarioVO getUser() {
        return user;
    }

    public void setUser(UsuarioVO user) {
        this.user = user;
    }

    public void setLoggedIn(boolean value){
        this.loggedIn = value;
    }

    public boolean getLoggedIn(){
        return this.loggedIn;
    }

    public boolean isLoggedIn(){
        return this.getLoggedIn();
    }
}
