/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Configurador;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@WebServlet(name = "generaPDF", urlPatterns = {"/generaPDF"})
public class GeneraPDF extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(sia.compra.servlets.GeneraPDF.class.getName());

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
            throws ServletException, IOException {
        Connection conn = null;
        try {
            // utilizar un managedbean
            UsuarioBean sesion = (UsuarioBean) request.getSession().getAttribute("usuarioBean");

            if (sesion != null) {
                // si el managed bean usuario es diferente de null podemos verificar si ya inicio sesion
                if (sesion.getUsuarioConectado() != null) {
                    // si inicio sesion buscar el convenio q viene en el parametro del servlet
                    String ordenID = request.getParameter("ZWZ2W");

                    HashMap _params = null;
                    File tempFile = null;
                    String _repName = request.getParameter("ZWZ3W");//"reporteOC";
                    String _repCon = request.getParameter("ZWZ4W");//;
                    String extPDF = ".pdf";
                    String JASPER_EXTENSION = ".jasper";
                    String REPOSITORYPATH = "/home/mluis/local/files/";
                    String JASPERFilePath = "Formatos/fuentesOrden/";//String jasperPath = "/home/ihsa/Reportes/";
                    String PDFFILEPATH = "Formatos/formatosOrden/";//String pdfPath = "/home/ihsa/Reportes/PDF/";

//                    JasperCompileManager.compileReportToFile(jrxmlFileName, jasperFileName);
                    // String dbUrl = props.getProperty("jdbc.url");
                    String dbUrl = "jdbc:firebirdsql://mtyvwdvsrv/Sia?encoding=ISO8859_1&defaultResultSetHoldable=true";
                    // String dbDriver = props.getProperty("jdbc.driver");
                    String dbDriver = "org.firebirdsql.jdbc.FBDriver";
                    // String dbUname = props.getProperty("db.username");
                    String dbUname = "SIA";
                    // String dbPwd = props.getProperty("db.password");
                    

                    // Load the JDBC driver
                    Class.forName(dbDriver);
                    // Get the connection
                    conn = DriverManager.getConnection(dbUrl, dbUname, _repCon);

                    // Create arguments
                    // Map params = new HashMap();
                    _params = new HashMap();
                    _params.put("REPORT_LOCALE", new Locale("es", "MX"));
                    _params.put("ORDENID", Integer.parseInt(ordenID));
                    String subRepotPath = new StringBuilder().append(REPOSITORYPATH).append(JASPERFilePath).toString();
                    _params.put("SUBREPORT_DIR", subRepotPath);

                    String auxPath = new StringBuilder().append(REPOSITORYPATH).append(JASPERFilePath).append(_repName).append(JASPER_EXTENSION).toString();
                    // Generate jasper print
                    JasperPrint jprint = (JasperPrint) JasperFillManager.fillReport(auxPath, _params, conn);

                    String auxPath2 = new StringBuilder().append(REPOSITORYPATH).append(PDFFILEPATH).append(_repName).append(extPDF).toString();
                    // Export pdf file
                    JasperExportManager.exportReportToPdfFile(jprint, auxPath2);

                    try(InputStream in = new FileInputStream(auxPath2);
                            ServletOutputStream servletoutputstream = response.getOutputStream();) {

                        byte[] data = new byte[in.available()];
                        in.read(data);
                     
                        response.setContentType("application/pdf");
                        
                        // la siguiente linea es para mostrar un dialogo con la opcion de abrir guardar o cancelar
                        // response.setHeader("Content-Disposition", "attachment;filename=\"convenio.pdf\";");
                        response.setContentLength(data.length);
                        
                        servletoutputstream.write(data);
                        servletoutputstream.flush();
                    } catch (IOException e) {
                        UtilLog4j.log.error(e);
                    }
                } // si usuarioConectado es null el usuario no a iniciado sesion
                else {
                    response.sendRedirect(Configurador.urlSia() + "Sia");
                }
            } // si el managed bean usuario es null ni si quiera a entrado a la aplicacion
            else {
                response.sendRedirect(Configurador.urlSia() + "Sia");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(GeneraPDF.class.getName()).log(Level.SEVERE, null, ex);
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
        processRequest(request, response);
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
