package mx.ihsa.sistema.servlet.support;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import mx.ihsa.sistema.bean.backing.Sesion;

/**
 *
 * @author mrojas
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = "*.xhtml")
@Slf4j
public class SiaFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("@filter");
        try {
            
            HttpServletRequest reqt = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            HttpSession ses = reqt.getSession(false);

            String reqURI = reqt.getRequestURI();
            

            boolean isResource
                    = reqURI.contains("javax.faces.resource")
                    || reqURI.endsWith(".js")
                    || reqURI.endsWith(".css")
                    || reqURI.endsWith(".ico")
                    || reqURI.endsWith(".jpg")
                    || reqURI.endsWith(".jpeg")
                    || reqURI.endsWith(".svg")
                    || reqURI.endsWith(".png")
                    || reqURI.endsWith(".gif")
                    || reqURI.endsWith(".otf")
                    || reqURI.endsWith(".eot")
                    || reqURI.endsWith(".map")
                    || reqURI.endsWith(".ttf")
                    || reqURI.endsWith(".woff")
                    || reqURI.endsWith(".woff2");

            if ((ses != null && ses.getAttribute(Sesion.USER) != null)
                    || reqURI.contains("/loginEnter.xhtml")
                    || isResource) {
                System.out.println(" proceder con request");
                chain.doFilter(request, response);
            } else {
                System.out.println("Redirect to login");
                resp.sendRedirect(reqt.getContextPath() + "/loginEnter.xhtml");
            }

        } catch (IOException | ServletException e) {
            log.error("", e);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

}
