package com.ihsa.sia.servlets;

import com.ihsa.sia.commons.SessionBean;
import java.io.IOException;
import javax.faces.application.ResourceHandler;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Aplimovil SA de CV
 */
@WebFilter(urlPatterns={"/", "/views/*",})
public class SecurityFilter  implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
       
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //check if its a request for the login page or other static resource
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        //check for the session info to check if the user is authenticated
        SessionBean sessionInfo = (SessionBean)req.getSession().getAttribute("principal");
       
        
        if (!req.getRequestURI().startsWith(req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) { // Skip JSF resources (CSS/JS/Images/etc)
            
            res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            res.setDateHeader("Expires", 0); // Proxies.
        }
        
        if(sessionInfo != null && sessionInfo.isLoggedIn()) {
            chain.doFilter(request, response);
        } else {
            request.getRequestDispatcher("/login.jsf").forward(request, response);     
        }
    }

    @Override
    public void destroy() {
        
    }
}
