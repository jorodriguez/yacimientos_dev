/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.controloficios.sistema.soporte;

//import com.newrelic.api.agent.Trace;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.controloficios.sistema.bean.backing.Sesion;
import sia.excepciones.InsufficientPermissionsException;
import sia.excepciones.InvalidPermissionsException;
import sia.modelo.campo.usuario.puesto.vo.CompaniaBloqueGerenciaVo;
import sia.modelo.oficio.vo.OficioPromovibleVo;
import sia.modelo.oficio.vo.PermisosVo;
import sia.modelo.rol.vo.RolVO;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.oficio.impl.OfOficioImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiPermisoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jevazquez
 */
@WebServlet(name = "DACOF", urlPatterns = {"/DACOF"})
public class DACOF extends HttpServlet {

    @Inject
    private Sesion sesion;
    @Inject
    private OfOficioImpl oficioServicioRemoto;
    @Inject
    private SiAdjuntoImpl siAdjuntoRemote;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SiPermisoImpl permisoRemote;
    @Inject
    private UsuarioImpl servicioUsuario;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
 //   @Trace(dispatcher = true)
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("text/html;charset=UTF-8");
        try {

            if (sesion == null || sesion.getUsuario() == null) {
                showLogin(request, response);
            } else {
                sendDocument(request, response);
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            printMessage(e.getMessage(), request, response, "red");
        }
    }

    private void showLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idDoc = Integer.parseInt(request.getParameter("ZWZ2W"));
        String uui = request.getParameter("ZWZ3W");
        int idOficio = Integer.parseInt(request.getParameter("Z4BX2"));
        int apCampo = Integer.parseInt(request.getParameter("4PC4WZ"));

        request.setAttribute("uui", uui);
        request.setAttribute("idDoc", idDoc);
        request.setAttribute("idOficio", idOficio);
        request.setAttribute("apCampo", apCampo);
        request.getRequestDispatcher("/login.ftl").forward(request, response);
    }

    private void sendDocument(HttpServletRequest request, HttpServletResponse response)
            throws InsufficientPermissionsException, IOException, InvalidPermissionsException, NoSuchAlgorithmException, ServletException {

        UtilLog4j.log.info(this, "Recovering parameters ...");

        String user = request.getParameter("user");
        String pass = request.getParameter("pass");

        int apCampo = 0;
        int idOficio = 0;

//        for (Map.Entry<String, String[]> params : request.getParameterMap().entrySet()) {
//            UtilLog4j.log.info(this, params.getKey());
//            UtilLog4j.log.info(this, "" + params.getValue());
//        }

        if (request.getParameter("4PC4WZ") != null) {
            apCampo = Integer.parseInt(request.getParameter("4PC4WZ"));
        } else if (request.getParameter("C4WZ4P0") != null) {
            apCampo = Integer.parseInt(request.getParameter("C4WZ4P0"));
        }

        if (request.getParameter("Z4BX2") != null) {
            idOficio = Integer.parseInt(request.getParameter("Z4BX2"));
        } else if (request.getParameter("1D07IC10") != null) {
            idOficio = Integer.parseInt(request.getParameter("1D07IC10"));
        }

//        int idDoc = Integer.parseInt(request.getParameter("ZWZ2W"));
//        String uui = request.getParameter("ZWZ3W");
        UtilLog4j.log.info(this, "We have the parameters ...");

        List<RolVO> roles = new ArrayList<RolVO>();
        PermisosVo permisos = null;

        if (sesion == null || sesion.getUsuario() == null) {

            UtilLog4j.log.info(this, "Sesion and user are null ...");

            if (!user.isEmpty() && !pass.isEmpty()) {

                UtilLog4j.log.info(this, "Login ...");
                //autheticate user
                sesion.setUsuario(usuarioRemote.login(user, servicioUsuario.encriptar(pass)));
            } else {
                showLogin(request, response);
            }
        }

        UtilLog4j.log.info(this, " *** " + sesion.getUsuario());

        if (sesion.getUsuario() != null) {

            UtilLog4j.log.info(this, "Get companies ...");

            List<CompaniaBloqueGerenciaVo> listCampos = apCampoUsuarioRhPuestoRemote.traerCompaniasBloquesGerencias(sesion.getUsuario().getId());

            for (CompaniaBloqueGerenciaVo c : listCampos) {

                if (c.getBloqueId() == apCampo) {
                    sesion.setBloqueActivo(c);
                    break;
                }

            }

            UtilLog4j.log.info(this, "Get roles ...");
            roles = permisoRemote.fetchPermisosPorUsuarioModulo(sesion.getUsuario().getId(), Constantes.OFICIOS_MODULO_ID, sesion.getBloqueActivo().getBloqueId());

            if (!roles.isEmpty()) {
                sesion.setPermisos(new PermisosVo(roles));
                permisos = sesion.getPermisos();
            }

            UtilLog4j.log.info(this, "Get document {0} - {1}", new Object[]{idOficio, sesion.getUsuario().getId()});
            OficioPromovibleVo of = oficioServicioRemoto.buscarOficioVoPorId(idOficio, sesion.getUsuario().getId(), Constantes.TRUE);

            if (of.isPublico()) {
                UtilLog4j.log.info(this, "Is public ...");
                descargarArchivo(request, response);

            } else if (of.isRestringido()) {
                UtilLog4j.log.info(this, "Is restricted ...");
                if (of.tieneAccesoOficioRestringido(sesion.getUsuario().getId())) {
                    UtilLog4j.log.info(this, "The user has right to access ...");
                    descargarArchivo(request, response);
                } else {
                    printMessage("no cuenta con los permisos suficientes para descargar este archivo", request, response, "red");
                }

            } else {

                UtilLog4j.log.info(this, "Check department ...");
                UtilLog4j.log.info(this, "gerencias " + of.getGerenciaId() + " == " + sesion.getUsuario().getGerencia().getId());
                if (of.getGerenciaId().equals(sesion.getUsuario().getGerencia().getId())) {
                    descargarArchivo(request, response);

                } else {
                    UtilLog4j.log.info(this, "Do the user has rights? ...");
                    if (permisos != null && permisos.isVerTodoGerencias()) {
                        UtilLog4j.log.info(this, "Has rights and can see all departments ...");
                        descargarArchivo(request, response);
                    } else {
                        printMessage("Usted no cuenta con los permisos requeridos para descargar el archivo", request, response, "red");
                    }
                }
            }

        } else {
            printMessage("Usuario o contraseña incorrecta.", request, response, "red");
        }

    }

    private void printMessage(String message, HttpServletRequest request, HttpServletResponse response, String color) {
        try {
            PrintWriter output = response.getWriter();
            output.println("<html>");
            output.println("<head>");
            output.println("<title>Sistema Integral de Administración</title>");
            output.println("</head>");
            output.println("<body >");
            output.println(
                    "<h1 style=\"color: "
                    + color
                    + "; \"> Ocurri&oacute; un error al procesar la solicitud: "
                    + message
                    + ", favor de contactar a soporte</h1>"
            );
            output.println("</body>");
            output.println("</html>");
        } catch (IOException e) {
            UtilLog4j.log.fatal(this, e);
        }
    }

    public void descargarArchivo(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ServletOutputStream servletoutputstream = null;
        InputStream inFile = null;
        AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        try {

            String uui = "";
            int idDoc = 0;
            int idOficio = 0;

            if (request.getParameter("Z4BX2") != null) {
                idOficio = Integer.parseInt(request.getParameter("Z4BX2"));
            } else if (request.getParameter("1D07IC10") != null) {
                idOficio = Integer.parseInt(request.getParameter("1D07IC10"));
            }

            if (request.getParameter("uui") != null) {
                uui = request.getParameter("uui").trim();
            } else if (request.getParameter("ZWZ3W") != null) {
                uui = request.getParameter("ZWZ3W").trim();
            }

            if (request.getParameter("1D40c1D40c") != null) {
                idDoc = Integer.parseInt(request.getParameter("1D40c1D40c"));
            } else if (request.getParameter("ZWZ2W") != null) {
                idDoc = Integer.parseInt(request.getParameter("ZWZ2W"));
            }

            UtilLog4j.log.fatal(this, "UUID : {0}, idDoc: {1}", new Object[]{uui, idDoc});

            AdjuntoVO adjunto = siAdjuntoRemote.buscarArchivoOficio(idDoc, uui, idOficio);

            if (adjunto == null) {

                printMessage("No se encontro el archivo", request, response, "red");

            } else {

                DocumentoAnexo documento = almacenDocumentos.cargarDocumento(adjunto.getUrl());

                response.setContentType(documento.getTipoMime());
                response.setHeader("Content-Disposition", "attachment;filename=\"" + adjunto.getNombreUUID() + "\"");
                response.setContentLength(documento.getContenido().length);

                servletoutputstream = response.getOutputStream();
                servletoutputstream.write(documento.getContenido());
                servletoutputstream.flush();
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            printMessage(e.getMessage(), request, response, "red");
        } finally {
            if (inFile != null) {
                inFile.close();
            }
            if (servletoutputstream != null) {
                servletoutputstream.close();
            }
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            //processRequest(request, response);
            sendDocument(request, response);
        } catch (InsufficientPermissionsException e) {
            UtilLog4j.log.fatal(this, e.getMessage());
        } catch (InvalidPermissionsException e) {
            UtilLog4j.log.fatal(this, e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            UtilLog4j.log.fatal(this, e.getMessage());
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
