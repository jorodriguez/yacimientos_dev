package com.ihsa.sia.inventario.beans;

import java.io.IOException;
import java.io.Serializable;
import javax.inject.Named;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.constantes.Constantes;
import sia.util.UtilLog4j;
import sia.util.UtilSia;

/**
 *
 * @author Aplimovil SA de CV
 */
@Named(value = "login")
@RequestScoped
public class LoginBean implements Serializable {

    private String userName;
    private String password;

    public void processLogin(ActionEvent e) throws Exception {
	HttpServletRequest request
		= (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	HttpServletResponse response
		= (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	RequestDispatcher dispatcher = request.getRequestDispatcher("/LoginServlet");
	dispatcher.forward(request, response);
    }

    public void cerrarSesion(ActionEvent actionEvent) {
	redireccionar(Constantes.URL_REL_SIA_SIGN_OUT);
    }

    private void redireccionar(final String url) {
	try {
	    HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	    String prefix = UtilSia.getUrl(origRequest);
	    FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getExternalContext().redirect(prefix + url); // redirecciona la página
	} catch (IOException ex) {
	    UtilLog4j.log.fatal(this, "Error de IO al redireccionar: " + ex.getMessage());
	}

    }

    public String getUserName() {
	return userName;
    }

    public void setUserName(String userName) {
	this.userName = userName;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }
}
