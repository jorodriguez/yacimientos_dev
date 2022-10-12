package sia.sistema.servlet.support;

import java.io.IOException;
import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.util.UtilLog4j;

/**
 *
 * @author mrojas
 */
@WebFilter
public class SiaFilter implements Filter {
    
    private final static UtilLog4j LOGGER = UtilLog4j.log;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.debug(this,"Initializing filter ...");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        
        //String viewState = request.getParameter("javax.faces.ViewState");
        
        if (!request.getRequestURI().startsWith(request.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) { // Skip JSF resources (CSS/JS/Images/etc)
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            response.setDateHeader("Expires", 0); // Proxies.
        }
        
        //ThreadContext.put("viewState", viewState);

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        LOGGER.debug(this,"Destroying filter ...");
    }
    
}
