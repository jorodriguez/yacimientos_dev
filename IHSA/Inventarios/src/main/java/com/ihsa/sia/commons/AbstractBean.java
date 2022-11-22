package com.ihsa.sia.commons;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import sia.inventarios.service.Utilitarios;

/**
 *
 * @author Aplimovil SA de CV
 */
public abstract class AbstractBean {

    private static final String FECHA_FORMATO = "dd/MM/yyyy";
    private static final String FECHA_HORA_FORMATO = "dd/MM/yyyy / HH:mm";
    private Integer tamanioPagina = 20;

    /**
     * Método para agregar mensajes de error
     *
     * @param string
     */
    public void addErrorMessage(String string) {
	FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, string, null);
	FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    /**
     * Método para agregar mensajes informativos hacia la aplicación
     *
     * @param string
     */
    public void addInfoMessage(String string) {
	FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, string, null);
	FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    protected void ManejarExcepcion(Exception ex) {
	String mensaje = ex.getMessage();
	if (Utilitarios.esNuloOVacio(ex.getMessage())) {
	    mensaje = obtenerCadenaDeRecurso("sia.inventarios.mobile.mensaje.error");
	    ex.printStackTrace();
	}
	addErrorMessage(mensaje);
    }

    public Integer getTamanioPagina() {
	return tamanioPagina;
    }

    public void setTamanioPagina(Integer tamanioPagina) {
	this.tamanioPagina = tamanioPagina;
    }

    protected String getUserName() {
	SessionBean principal = (SessionBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("principal");
	if (principal == null) {
	    return null;
	}
	return principal.getUser().getId();
    }

    public String fechaConFormato(Date date) {
	return crearFechaConFormato(date, FECHA_FORMATO);
    }

    public String fechaHoraConFormato(Date date) {
	return crearFechaConFormato(date, FECHA_HORA_FORMATO);
    }

    private String crearFechaConFormato(Date date, String formato) {
	if (date == null) {
	    return "";
	}
	long diff = new Date().getTime() - date.getTime();
	return MessageFormat.format(obtenerCadenaDeRecurso("sia.inventarios.comun.fechaFormato"),
		new SimpleDateFormat(formato).format(date),
		TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
    }

    protected String obtenerCadenaDeRecurso(String key) {
	return Messages.getString(key);
    }

    protected Integer getCampoId() {
	SessionBean principal = (SessionBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("principal");
	if (principal == null) {
	    return null;
	}
	return principal.getUser().getIdCampo();
    }
}
