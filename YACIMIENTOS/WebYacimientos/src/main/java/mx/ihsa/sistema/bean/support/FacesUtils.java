/*
 * FacesUtils.java
 * Creado el 16/06/2009, 10:36:04 AM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: hacosta.0505@gmail.com
 */
package mx.ihsa.sistema.bean.support;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com
 * @date 16/06/2009
 */
import java.util.ResourceBundle;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIParameter;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * JSF utilities.
 */
public class FacesUtils {
    /**
     * optiene el valor del elemento especificado en el archivo de recursos del sistema
     *
     * @param key elemento que contiene el valor
     * @return string el valor del key
     */
    public static String getKeyResourceBundle(String key) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
                "sia.sistema.literales.messages",
                fc.getViewRoot().getLocale());
        return resourceBundle.getString(key);
    }

    /**
     * Get servlet context.
     *
     * @return the servlet context
     */
    public static ServletContext getServletContext() {
        return (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
    }

    public static ExternalContext getExternalContext() {
        FacesContext fc = FacesContext.getCurrentInstance();
        return fc.getExternalContext();
    }

    public static FacesContext getcurrentInstance(){
        return FacesContext.getCurrentInstance();
    }

    public static HttpSession getHttpSession(boolean create) {
        return (HttpSession) FacesContext.getCurrentInstance().
                getExternalContext().getSession(create);
    }

    /**
     * Get managed bean based on the bean name.
     *
     * @param beanName the bean name
     * @return the managed bean associated with the bean name
     */
    public static Object getManagedBean(String beanName) {
        return getValueBinding(getJsfEl(beanName)).getValue(FacesContext.getCurrentInstance());
    }

    /**
     * Remove the managed bean based on the bean name.
     *
     * @param beanName the bean name of the managed bean to be removed
     */
    public static void resetManagedBean(String beanName) {
        getValueBinding(getJsfEl(beanName)).setValue(FacesContext.getCurrentInstance(), null);
    }

    /**
     * Store the managed bean inside the session scope.
     *
     * @param beanName    the name of the managed bean to be stored
     * @param managedBean the managed bean to be stored
     */
    public static void setManagedBeanInSession(String beanName, Object managedBean) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(beanName, managedBean);
    }

    /**
     * Get parameter value from request scope.
     *
     * @param name the name of the parameter
     * @return the parameter value
     */
    public static String getRequestParameter(String name) {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
    }
/*
    public static int getParametroEntero() {
        return ((Integer) ((UIParameter) actionEvent.getComponent().getFacet("ParametroAdicional")).getValue()).intValue();
    }

    public static int getParamInt() {
        return ((Integer) ((UIParameter) actionEvent.getComponent().getFacet("myParam")).getValue()).intValue();
    }

    public static String getParametroCadena() {
        return (String) ((UIParameter) actionEvent.getComponent().getFacet("myFacet")).getValue();
    }
*/
    /**
     * Add information message.
     *
     * @param msg the information message
     */
    public static void addInfoMessage(String msg) {
        addInfoMessage(null, msg);
    }

    /**
     * Add information message to a specific client.
     *
     * @param clientId the client id
     * @param msg      the information message
     */
    public static void addInfoMessage(String clientId, String msg) {
        FacesContext.getCurrentInstance().addMessage(
                clientId, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg)
        );
    }

    /**
     * Add error message.
     *
     * @param msg the error message
     */
    public static void addErrorMessage(String msg) {
        addErrorMessage(null, msg);
    }

    /**
     * Add error message to a specific client.
     *
     * @param clientId the client id
     * @param msg      the error message
     */
    public static void addErrorMessage(String clientId, String msg) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }

    private static Application getApplication() {
        ApplicationFactory appFactory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
        return appFactory.getApplication();
    }

    private static ValueBinding getValueBinding(String el) {
        return getApplication().createValueBinding(el);
    }

    private static HttpServletRequest getServletRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    private static Object getElValue(String el) {
        return getValueBinding(el).getValue(FacesContext.getCurrentInstance());
    }

    private static String getJsfEl(String value) {
        return "#{" + value + "}";
    }

//    private static void clearFacesMessages(FacesContext facesContext, String clientId) {
//		Iterator<FacesMessage> facesMessags = facesContext.getMessages(clientId);
//		while (facesMessags.hasNext()) {
//			facesMessags.next();
//			facesMessags.remove();
//		}
//	}
//
//	public static void clearImmediateFacesMessages(FacesContext facesContext) {
//		clearImmediateFacesMessages(facesContext, FacesUtils.getcurrentInstance().getViewRoot());
//	}

//	private static void clearImmediateFacesMessages(FacesContext facesContext, UIComponent uiComponent) {
//		if (uiComponent instanceof ActionSource) {
//			ActionSource actionSource = (ActionSource) uiComponent;
//			if (actionSource.isImmediate()) {
//				clearFacesMessages(facesContext, uiComponent.getClientId(facesContext));
//			}
//		}
//		else if (uiComponent instanceof EditableValueHolder) {
//			EditableValueHolder editableValueHolder = (EditableValueHolder)uiComponent;
//			if (editableValueHolder.isImmediate()) {
//				clearFacesMessages(facesContext, uiComponent.getClientId(facesContext));
//			}
//		}
//		List<UIComponent> childComponents = uiComponent.getChildren();
//		for (UIComponent childComponent: childComponents) {
//			clearImmediateFacesMessages(facesContext, childComponent);
//		}
//	}
    
}
