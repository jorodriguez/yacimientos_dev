/*
 * FacesUtils.java
 * Creado el 16/06/2009, 10:36:04 AM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.ihsa.utils;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com
 * @date 16/06/2009
 */
import com.google.common.base.Strings;
import java.util.ResourceBundle;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIParameter;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * JSF utilities.
 */

public final class FacesUtilsBean {

    private FacesUtilsBean(){}
    
    private static final String UNKNOWN = "unknown";
    private static final String USER_AGENT = "User-Agent";
    
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

    public static int getParametroEntero(ActionEvent actionEvent) {
        return (Integer) ((UIParameter) actionEvent.getComponent().getFacet("ParametroAdicional")).getValue();
    }

    public static String getParametroCadena(ActionEvent actionEvent) {
        return  (String) ((UIParameter) actionEvent.getComponent().getFacet("myFacet")).getValue();
    }
    
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
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
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
    
    
    public static HttpServletRequest getRequest(ExternalContext ec) {
        return (HttpServletRequest) ec.getRequest();
    }
    
    
    public static String getClientInfo(HttpServletRequest request) {
        StringBuilder retVal = new StringBuilder();
        
        retVal.append("{\"userAgent\" : \"").append(getUserAgent(request)).append("\", ")
		.append("\"clientOS\" : \"").append(getClientOS(request)).append("\", ")
		.append("\"clientBrowser\" : \"").append(getClientBrowser(request)).append("\", ")
		.append("\"clientIpAddr\" : \"").append(getClientIpAddr(request)).append("\", ")
		.append("\"fullUrl\" : \"").append(getFullURL(request)).append("\", ")
		.append("\"referer\" : \"").append(getReferer(request)).append("\"}");
        
        return retVal.toString();
    }
    
    
    public static String getReferer(HttpServletRequest request) {
        return request.getHeader("referer");
    }

	
    public static String getFullURL(HttpServletRequest request) {
        final StringBuffer requestURL = request.getRequestURL();
        final String queryString = request.getQueryString();

        return queryString == null
                ? requestURL.toString()
                : requestURL.append('?').append(queryString).toString();

    }

    //http://stackoverflow.com/a/18030465/1845894
    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (Strings.isNullOrEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (Strings.isNullOrEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (Strings.isNullOrEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if (Strings.isNullOrEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (Strings.isNullOrEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    //http://stackoverflow.com/a/18030465/1845894
    public static String getClientOS(HttpServletRequest request) {
        final String browserDetails = request.getHeader(USER_AGENT);

        String retVal = null;

        //=================OS=======================
        final String lowerCaseBrowser = browserDetails.toLowerCase();
        if (lowerCaseBrowser.contains("windows")) {
            retVal = "Windows";
        } else if (lowerCaseBrowser.contains("mac")) {
            retVal = "Mac";
        } else if (lowerCaseBrowser.contains("x11")) {
            retVal = "Unix";
        } else if (lowerCaseBrowser.contains("android")) {
            retVal = "Android";
        } else if (lowerCaseBrowser.contains("iphone")) {
            retVal = "IPhone";
        } else {
            retVal = "UnKnown, More-Info: " + browserDetails;
        }

        return retVal;
    }

    //http://stackoverflow.com/a/18030465/1845894
    public static String getClientBrowser(HttpServletRequest request) {
        final String browserDetails = request.getHeader(USER_AGENT);
        final String user = browserDetails.toLowerCase();

        String browser = "";

        //===============Browser===========================
        if (user.contains("msie")) {
            String substring = browserDetails.substring(browserDetails.indexOf("MSIE")).split(";")[0];
            browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version")) {
            browser = (browserDetails.substring(browserDetails.indexOf("Safari")).split(" ")[0]).split(
                    "/")[0] + "-" + (browserDetails.substring(
                            browserDetails.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if (user.contains("opr") || user.contains("opera")) {
            if (user.contains("opera")) {
                browser = (
                            browserDetails.substring(browserDetails.indexOf("Opera"))
                            .split(" ")[0]
                        )
                        .split("/")[0] + "-" 
                        + (browserDetails.substring(browserDetails.indexOf("Version"))
                                .split(" ")[0]).split("/")[1];
            } else if (user.contains("opr")) {
                browser = 
                        ((browserDetails.substring(browserDetails.indexOf("OPR")).split(" ")[0]).replace("/", "-"))
                        .replace("OPR", "Opera");
            }
        } else if (user.contains("chrome")) {
            browser = (browserDetails.substring(browserDetails.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.indexOf("mozilla/7.0") > -1) 
                || (user.indexOf("netscape6") != -1) 
                || (user.indexOf("mozilla/4.7") != -1) 
                || (user.indexOf("mozilla/4.78") != -1) 
                || (user.indexOf("mozilla/4.08") != -1) 
                || (user.indexOf("mozilla/3") != -1)) {
            browser = "Netscape-?";

        } else if (user.contains("firefox")) {
            browser = (browserDetails.substring(browserDetails.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("rv")) {
            browser = "IE";
        } else {
            browser = "UnKnown, More-Info: " + browserDetails;
        }

        return browser;
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader(USER_AGENT);
    }
}
