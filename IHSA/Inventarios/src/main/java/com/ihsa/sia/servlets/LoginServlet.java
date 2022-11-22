package com.ihsa.sia.servlets;

import com.ihsa.sia.commons.SessionBean;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;

/**
 *
 * @author Aplimovil SA de CV
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;

    @Inject
    SessionBean sessionBean;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("ZWZ4W");
            String hash = request.getParameter("hash");

            if (username != null) {
                if (password != null && !password.isEmpty()) { // Es un POST desde el login form del sistema (se provee el password en texto plano)
                    //String passwordHash = this.getHash(password); // Obtener el hash SHA1 del password
                    this.processLogin(request, response, username, password);
                } else if (hash != null && !hash.isEmpty()) { // Es un POST externo para login automatico (se provee el hash del password directamente)
                    this.processLogin(request, response, username, hash);
                } else {
                    respondWithLoginError(request, response);
                }
            } else {
                respondWithLoginError(request, response);
            }
        } catch (SIAException e) {
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            throw new SIAException("LoginServlet", "processRequest", e.getMessage());
        }
    }

    protected void processLogin(HttpServletRequest request, HttpServletResponse response, String username, String passwordHash)
            throws SIAException, IOException, ServletException {
        // Realizar el login
        Usuario usuario = usuarioImpl.find(username);

        if (usuario != null) {
            if (usuario.getClave().equals(passwordHash)) {
                String redirectTo = request.getParameter("redirectTo");

                if (redirectTo == null) {
                    redirectTo = "views/index.jsf";
                }
                List<UsuarioRolVo> roles = siUsuarioRolImpl.traerRolPorUsuarioModulo(username,
                        Constantes.MODULO_INVENTARIOS, usuario.getApCampo().getId());
                sessionBean.setLoggedIn(true);
                sessionBean.setUser(new UsuarioVO(usuario.getId(), usuario.getNombre(), usuario.getEmail(), roles));
                sessionBean.getUser().setIdCampo(usuario.getApCampo().getId());
                sessionBean.getUser().setCampo(usuario.getApCampo().getNombre());
                sessionBean.getUser().setCampos(apCampoUsuarioRhPuestoImpl.getAllPorUsurio(usuario.getId()));
                CampoUsuarioPuestoVo cupv = apCampoUsuarioRhPuestoImpl.findByUsuarioCampo(usuario.getApCampo().getId(), usuario.getId());
                sessionBean.getUser().setPuesto(cupv.getPuesto());
                sessionBean.getUser().setIdGerencia(cupv.getIdGerencia());
                request.getSession().setAttribute("principal", sessionBean);
                //request.getRequestDispatcher(redirectTo).forward(request, response);
                //Se prefiere redirect porque dispatcher provoca que el mecanismo de forward
                //de jsf mal interprete la url y genere un error de que la vista fue procesada
                response.sendRedirect(redirectTo);
            } else {
                respondWithLoginError(request, response);
            }
        }
    }

    protected void respondWithLoginError(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setAttribute("showError", true);
        request.getRequestDispatcher("/login.jsf").forward(request, response);
    }

    public String getHash(String input) throws SIAException, Exception {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // Obtener hash SHA-1 como string binario
            byte[] b = md.digest(input.getBytes());

            // Convertir a un string hexadecimal
            int size = b.length;
            StringBuilder h = new StringBuilder(size);

            for (int i = 0; i < size; i++) {
                int u = b[i] & 255;
                if (u < 16) {
                    h.append(Integer.toHexString(u));
                } else {
                    h.append(Integer.toHexString(u));
                }
            }

            return h.toString();
        } catch (Exception e) {
            throw new SIAException("AuthService", "getHash", e.getMessage());
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //  throw new IOException("LoginServlet no acepta peticiones GET");
        try {
            processRequest(request, response);
        } catch (Exception e) {
//          e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            processRequest(request, response);
        } catch (Exception e) {
            //     e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "SIA Inventarios Login Servlet";
    }
}
