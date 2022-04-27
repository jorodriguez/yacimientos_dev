package sia.util;

import com.google.common.base.Strings;
import java.util.Locale;
import java.util.Optional;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import sia.modelo.Usuario;
import sia.sistema.bean.backing.Sesion;
import static sia.util.UtilLog4j.log;

/**
 * Métodos utilitarios para manejo de sesiones HTTP.
 *
 * @author mrojas
 */
public class SessionUtils {

    private static final String UNKNOWN = "unknown";
    private static final String USER_AGENT = "User-Agent";

    private SessionUtils() {
    }

    /**
     * Devuelve una referencia a la sesión HTTP.
     *
     * @return La referencia a la sesión HTTP para el cliente actual.
     */
    public static HttpSession getSession() {
        return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
    }

    /**
     * Devuelve una referencia a la solicitud HTTP.
     *
     * @return La referencia a la solicitud HTTP para el cliente actual.
     */
    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    /**
     * Devuelve el objeto de clase {@link mx.grupoavanzia.sia.dto.Usuario} almacenado en la sesión.
     *
     * @return El objeto que representa al usuario para el cliente actual.
     */
    public static Optional<Usuario> getUser() {
        Optional<Usuario> retVal = Optional.empty();

        if (FacesContext.getCurrentInstance() != null) {
            HttpSession session
                    = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

            retVal = Optional.ofNullable((Usuario) session.getAttribute(Sesion.USER));
        }

        return retVal;
    }

    /**
     * Devuelve la localización en base a los datos enviados por el cliente remoto.
     *
     * @return La información de localización del cliente remoto.
     */
    public static Locale getSessionLocale() {
        return FacesContext.getCurrentInstance().getViewRoot().getLocale();
    }

    // https://gist.github.com/c0rp-aubakirov/a4349cbd187b33138969
    public static String getClientInfo(HttpServletRequest request) {
        final String referer = getReferer(request);
        final String fullURL = getFullURL(request);
        final String clientIpAddr = getClientIpAddr(request);
        final String clientOS = getClientOS(request);
        final String clientBrowser = getClientBrowser(request);
        final String userAgent = getUserAgent(request);

        var retVal = new StringBuilder();
        retVal.append("{\"userAgent\" : \"").append(userAgent).append("\", ")
                .append("\"clientOS\" : \"").append(clientOS).append("\", ")
                .append("\"clientBrowser\" : \"").append(clientBrowser).append("\", ")
                .append("\"clientIpAddr\" : \"").append(clientIpAddr).append("\", ")
                .append("\"fullUrl\" : \"").append(fullURL).append("\", ")
                .append("\"referer\" : \"").append(referer).append("\"}");

        log.debug("*** Client info : {}", retVal.toString());

        return retVal.toString();
    }

    public static String getReferer(HttpServletRequest request) {
        return request.getHeader("referer");
    }

    public static String getFullURL(HttpServletRequest request) {
        final var requestURL = request.getRequestURL();
        final var queryString = request.getQueryString();

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

        var browser = "";

        //===============Browser===========================
        if (user.contains("msie")) {
            var substring = browserDetails.substring(browserDetails.indexOf("MSIE")).split(";")[0];
            browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version")) {
            browser = (browserDetails.substring(browserDetails.indexOf("Safari")).split(" ")[0]).split(
                    "/")[0] + "-" + (browserDetails.substring(
                            browserDetails.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if (user.contains("opr") || user.contains("opera")) {
            if (user.contains("opera")) {
                browser = (browserDetails.substring(browserDetails.indexOf("Opera")).split(" ")[0]).split(
                        "/")[0] + "-" + (browserDetails.substring(
                                browserDetails.indexOf("Version")).split(" ")[0]).split("/")[1];
            } else if (user.contains("opr")) {
                browser = ((browserDetails.substring(browserDetails.indexOf("OPR")).split(" ")[0]).replace("/",
                        "-")).replace(
                                "OPR", "Opera");
            }
        } else if (user.contains("chrome")) {
            browser = (browserDetails.substring(browserDetails.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1) || (user.indexOf(
                "mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf(
                "mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {
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
