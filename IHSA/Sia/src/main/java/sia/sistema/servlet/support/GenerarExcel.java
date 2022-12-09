/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sistema.servlet.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.constantes.Configurador;
import sia.modelo.OcPresupuesto;
import sia.servicios.requisicion.impl.OcPresupuestoImpl;
import sia.servicios.requisicion.impl.OcPresupuestoMovimientosImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@WebServlet(name = "generarExcel", urlPatterns = {"/generarExcel"})
public class GenerarExcel extends HttpServlet {

    @Inject
    private SiParametroImpl parametrosSistemaServicioRemoto;
    @Inject
    private OcPresupuestoImpl ocPresupuestoImpl;
    @Inject
    private OcPresupuestoMovimientosImpl ocPresupuestoMovimientosImpl;
    @Inject
    private Sesion sesion;


    static Logger log = Logger.getLogger(GenerarExcel.class.getName());

    private File creatTempFile(OcPresupuesto presupuesto) {
        File fileTempExcel = null;
        try {
            String REPOSITORYPATH = this.parametrosSistemaServicioRemoto.find(1).getUploadDirectory();
            String PLANTILLAPATH = "Plantillas/Presupuestos";
            String URL_Temporal = new StringBuilder().append(REPOSITORYPATH).append(PLANTILLAPATH).append(File.separator).toString();
            fileTempExcel = File.createTempFile("excelTemporal", ".xlsx", new File(URL_Temporal));
            if (presupuesto != null && presupuesto.getId() > 0) {
                fileTempExcel = ocPresupuestoImpl.generarExcel(presupuesto, fileTempExcel);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
        return fileTempExcel;
    }

    private File createFileTemplate(OcPresupuesto presupuesto) {
        File fileTempExcel = null;
        try {
            String REPOSITORYPATH = this.parametrosSistemaServicioRemoto.find(1).getUploadDirectory();
            String PLANTILLAPATH = "Plantillas/Presupuestos";
            String URL_PARAMETROS = new StringBuilder().append(REPOSITORYPATH).append(PLANTILLAPATH).toString();
            if (presupuesto != null && presupuesto.getId() > 0) {
                String fileNameNavision = File.separator + presupuesto.getCodigo();
                String fileNamePlantilla = File.separator + "plantilla.xlsx";
                String rutaOrigen = URL_PARAMETROS + fileNamePlantilla;
                fileTempExcel = ocPresupuestoImpl.generarExcel(presupuesto, rutaOrigen, URL_PARAMETROS + fileNameNavision + ".xlsx");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
        return fileTempExcel;
    }

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
        File fileExcel = null;
        try {            
            if (sesion != null) {
                if (sesion.getUsuario() != null) {
                    String presID = request.getParameter("ZWZ2W");
                    int presupuestoID = Integer.parseInt(presID);

                    OcPresupuesto presupuesto = ocPresupuestoImpl.find(presupuestoID);
                    if (presupuesto != null && presupuesto.getId() > 0) {
                        ocPresupuestoMovimientosImpl.exportar(presupuestoID, sesion.getUsuario().getId());

                        fileExcel = createFileTemplate(presupuesto);

                        if (fileExcel != null && fileExcel.exists()) {

                            try (InputStream in = new FileInputStream(fileExcel);
                                    ServletOutputStream servletoutputstream = response.getOutputStream();) {
                                byte[] data = new byte[in.available()];
                                in.read(data);
                                //response.setContentType("application/vnd.ms-excel");
                                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                                response.setContentLength(data.length);

                                servletoutputstream.write(data);
                                servletoutputstream.flush();

                            } catch (IOException e) {
                                UtilLog4j.log.error(e);
                            }
                        } else {
                            response.setContentType("text/html");
                            PrintWriter out = response.getWriter();
                            out.println(
                                    new StringBuilder().append("<html><body><table><tr><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:15px\">").append("La solicitud que est&#225;s realizando es incorrecta. ") //                                .append("Los identificadores del archivo de la petici&#243;n no coinciden con los identificadores en la base de datos.")
                                            .append("</td></tr></table></body></html>").toString());
                        }
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
            log.log(Level.SEVERE, null, e);
        }
//        finally {
//            fileExcelNavision.delete();            
//        }
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
