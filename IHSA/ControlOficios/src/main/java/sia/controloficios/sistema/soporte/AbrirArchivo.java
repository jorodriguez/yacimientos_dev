/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.controloficios.sistema.soporte;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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
import sia.constantes.Constantes;
import sia.controloficios.sistema.bean.backing.Sesion;
import sia.excepciones.InvalidPermissionsException;
import sia.modelo.campo.usuario.puesto.vo.CompaniaBloqueGerenciaVo;
import sia.modelo.oficio.vo.OficioPromovibleVo;
import sia.modelo.oficio.vo.PermisosVo;
import sia.modelo.rol.vo.RolVO;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.oficio.impl.OfOficioConsultaImpl;
import sia.servicios.oficio.impl.OfOficioImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiPermisoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jevazquez
 */
@WebServlet(name = "AbrirArchivo", urlPatterns = {"/AbrirArchivo"})
public class AbrirArchivo extends HttpServlet {

    @Inject
    private Sesion sesion;

    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SiPermisoImpl permisoRemote;
    @Inject
    private UsuarioImpl servicioUsuario;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;
    @Inject
    private OfOficioConsultaImpl oficioConsultaServicioRemoto;
    @Inject
    private SiAdjuntoImpl siAdjuntoRemote;
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, InvalidPermissionsException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        try {

            //String uui = request.getParameter("uui");
            // int idDoc = Integer.parseInt(request.getParameter("1D40c1D40c"));
            String user = request.getParameter("user");
            String pass = request.getParameter("pass");
            
            int idOficio = Integer.parseInt(request.getParameter("1D07IC10"));
            int apCampo = Integer.parseInt(request.getParameter("C4WZ4P0"));
            
            List<RolVO> roles = new ArrayList<RolVO>();
            PermisosVo permisos = null;
            
            
            if (!user.isEmpty() && !pass.isEmpty()) {
                
                sesion.setUsuario(usuarioRemote.login(user, servicioUsuario.encriptar(pass)));
            
                if (sesion.getUsuario() != null) {
                    
                    List<CompaniaBloqueGerenciaVo> listCampos = apCampoUsuarioRhPuestoRemote.traerCompaniasBloquesGerencias(user);
                    
                    for (CompaniaBloqueGerenciaVo c : listCampos) {
                    
                        if (c.getBloqueId() == apCampo) {
                            sesion.setBloqueActivo(c);
                            break;
                        }
                        
                    }
                    
                    roles = permisoRemote.fetchPermisosPorUsuarioModulo(sesion.getUsuario().getId(), Constantes.OFICIOS_MODULO_ID, sesion.getBloqueActivo().getBloqueId());
                    
                    if (!roles.isEmpty()) {
                        sesion.setPermisos(new PermisosVo(roles));
                        permisos = sesion.getPermisos();
                    }

                    OficioPromovibleVo of = oficioConsultaServicioRemoto.buscarOficioVoPorId(idOficio, user, Constantes.TRUE);
                    
                    
                    if (of.isPublico()) {
                        descargarArchivo(request, response);
                        
                    } else if (of.isRestringido()) {
                        
                        if (of.tieneAccesoOficioRestringido(user)) {
                            descargarArchivo(request, response);
                        } else {
                            printMessage("no cuenta con los permisos suficientes para descargar este archivo", request, response, "red");
                        }
                        
                    } else {
                        
                        if (of.getGerenciaId() == sesion.getUsuario().getGerencia().getId()) {
                            descargarArchivo(request, response);
                            
                        } else {
                            
                            if (permisos != null && permisos.isVerTodoGerencias()) {
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

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            printMessage(e.getMessage(), request, response, "red");
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
        try {
            processRequest(request, response);
        } catch (InvalidPermissionsException ex) {
            Logger.getLogger(AbrirArchivo.class.getName()).log(Level.SEVERE, null, ex);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (InvalidPermissionsException ex) {
            Logger.getLogger(AbrirArchivo.class.getName()).log(Level.SEVERE, null, ex);
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
    
    private void printMessage(String message, HttpServletRequest request, HttpServletResponse response, String color ) {
        try {
            PrintWriter output = response.getWriter();
            output.println("<html>");
            output.println("<head>");
            output.println("<title>Sistema Integral de Administración</title>");
            output.println("</head>");
            output.println("<body >");
            output.println(
                    "<h4 style=\"color: "+color+"; \"> Ocurri&oacute: un error al intentar descargar el archivo" 
                            + message 
                            + ". Favor de contactar al equipo de soporte en soportesia@ihsa.mx</h4>"
            );
            output.println("</body>");
            output.println("</html>");
        } catch (IOException ioe) {
            UtilLog4j.log.fatal(this, "", ioe);
        }
    }
    
    public void descargarArchivo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        ServletOutputStream servletoutputstream = null;
        InputStream inFile = null;

        AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
        
        try {
            
            String uui = request.getParameter("uui");
            
            int idDoc = Integer.parseInt(request.getParameter("1D40c1D40c"));
            
            int idOficio = Integer.parseInt(request.getParameter("1D07IC10"));
            
            
            AdjuntoVO adjunto = siAdjuntoRemote.buscarArchivoOficio(idDoc, uui,idOficio);
        
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
        } finally {
            if (inFile != null) {
                inFile.close();
            }
            if (servletoutputstream != null) {
                servletoutputstream.close();
            }
        }

    }

}
