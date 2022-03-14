/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sistema.servlet.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.Proveedor;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.proveedor.Vo.ProveedorDocumentoVO;
import sia.servicios.proveedor.impl.ContactoProveedorImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.proveedor.impl.PvClasificacionArchivoImpl;
import sia.util.LecturaLibro;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@WebServlet(name = "ProveedorDocumento", urlPatterns = {"/ProveedorDocumento"})
public class ProveedorDocumento extends HttpServlet {

    @EJB
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @EJB
    private PvClasificacionArchivoImpl pcar;
    @EJB
    private ProveedorServicioImpl proveedorImpl;
    @EJB
    private ContactoProveedorImpl contactoProveedorImpl;

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
        response.setContentType("text/html;charset=UTF-8");
        ServletOutputStream servletoutputstream = null;

        try {
            // utilizar un managedbean
            // si inicio sesion buscar el convenio q viene en el parametro del servlet
            String idProveedor = request.getParameter("ZWZPR");
            Proveedor proveedor = proveedorImpl.find(Integer.parseInt(idProveedor));
            // Genera la plantilla
            //
            List<ProveedorDocumentoVO> lPdoc = pcar.traerArchivoPorProveedorOid(Integer.parseInt(idProveedor), Constantes.CERO);
            File file = new File(processAttachment(proveedor, lPdoc));
            String tipo = Files.probeContentType(file.toPath());
            response.setContentType(tipo);
            response.setHeader("Content-Disposition", "attachment;filename=\"" + file.getName() + "\"");
            response.setContentLength(FileUtils.readFileToByteArray(file).length);

            servletoutputstream = response.getOutputStream();
            servletoutputstream.write(FileUtils.readFileToByteArray(file));
            servletoutputstream.flush();
        } catch (Exception e) {
            printMessage("La documentación del proveedor esta corrupta, favor de contactar al equipo del SIA (soportesia@ihsa.mx).", response, "#843534");
            UtilLog4j.log.error(e);
        } finally {
            if (servletoutputstream != null) {
                servletoutputstream.close();
            }
        }
    }

    private String processAttachment(Proveedor proveedor, List<ProveedorDocumentoVO> lista) throws Exception {
        try {
            String rutaZip = System.getProperty("java.io.tmpdir") + File.separator;
            File f = File.createTempFile(proveedor.getRfc(), ".zip", new File(rutaZip));
            try (FileOutputStream fos = new FileOutputStream(f)) {
                // temporal
                try (ZipOutputStream zos = new ZipOutputStream(fos)) {
                    // temporal
                    File fileTempExcel = null;
                    //
                    List<ContactoProveedorVO> lcp = contactoProveedorImpl.traerContactoPorProveedor(proveedor.getId(), Constantes.CONTACTO_REP_COMPRAS);
                    fileTempExcel = recuperarPlantilla(rutaZip);
                    fileTempExcel = datosPlantilla(fileTempExcel, proveedor, lcp);
                    //
                    AlmacenDocumentos al = proveedorAlmacenDocumentos.getAlmacenDocumentos();
                    for (ProveedorDocumentoVO file : lista) {
                        if (file.getDocumento() != null) {
                            ZipEntry ze = new ZipEntry(file.getAdjuntoVO().getNombre());
                            zos.putNextEntry(ze);
                            DocumentoAnexo anexo = al.cargarDocumento(file.getAdjuntoVO().getUrl());
                            zos.write(anexo.getContenido(), 0, anexo.getTamanio());
                        }
                    }
                    try (FileInputStream fins = new FileInputStream(fileTempExcel)) {
                        zos.putNextEntry(new ZipEntry(fileTempExcel.getName()));
                        int tamanio;
                        byte[] buffPlantilla = new byte[1024];
                        while ((tamanio = fins.read(buffPlantilla)) > 0) {
                            zos.write(buffPlantilla, 0, tamanio);
                        }
                    }
                    zos.closeEntry();
                }
            }
            return f.getAbsolutePath();
        } catch (IOException ex) {
            UtilLog4j.log.error(ex);
        }
        return "";
    }

    private File datosPlantilla(File fileTemp, Proveedor proveedor, List<ContactoProveedorVO> lcp) {
        try {
            OPCPackage pkg;
            XSSFWorkbook wb;
            try (FileInputStream input_document = new FileInputStream(fileTemp)) {
                pkg = OPCPackage.open(input_document);
                wb = new XSSFWorkbook(pkg);
                XSSFSheet my_worksheet = wb.getSheetAt(0);
                llenarPlantilla(my_worksheet, proveedor, lcp);
            }
            try (FileOutputStream output_file = new FileOutputStream(fileTemp)) {
                wb.write(output_file);
            }
            pkg.close();
        } catch (IOException | InvalidFormatException e) {
            UtilLog4j.log.error(e);
        }
        return fileTemp;
    }

    private void printMessage(String message, HttpServletResponse response, String color) {
        try {
            PrintWriter output = response.getWriter();
            output.println("<html>");
            output.println("<head>");
            output.print(" <meta name=\"viewport\" content=\"width=device-width\"/>");
            output.println("<title>Sistema Integral de Administración</title>");
            //
            output.print(styleCSS());
            //
            output.println("</head>");
            output.println("<center >");
            output.println("<body >");
            output.println("<section>");
            output.println("<h1 style=\"color: " + color + ";\">" + message + "</h1>");
            output.println("</section>");
            output.println("</body>");
            output.println("</center >");
            output.println("</html>");

        } catch (IOException ioe) {
            UtilLog4j.log.fatal(this, ioe.getMessage());
        }
    }

    private String styleCSS() {
        StringBuilder css = new StringBuilder();
        css.append("<script type=\"text/css\">");
        css.append("*{margin:4%;} article{ float:left;width:50%;}");
        css.append("body{   background:#C3E5F9;    color:white;    font-size:16px;    font-family:Arial;    text-shadow:1px 1px 0 black; }");
        css.append(" section{   background:#12A89D;     margin:1%;    overflow:hidden;    padding:1%;    text-align:center;");
        css.append(" width:1000px;} .fila1{background:#FFFF; } .fila2{    background:#FFFF000; } ");
        css.append("@media screen and (max-width:1000px){    section{       width:90%;    } }");
        css.append(" @media screen and (max-width:1000px){    article{       width:90%;    } }");
        css.append("</script>");

        return css.toString();
    }

    private File recuperarPlantilla(String rutaOrigen) {
//       
        try {
            String PLANTILLAPATH = "Plantillas/Proveedor/";
            AlmacenDocumentos al = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            DocumentoAnexo anexo = al.cargarDocumento(PLANTILLAPATH + "plantillaProveedor.xlsx");
            File file = File.createTempFile("plantilla", ".xlsx");
            try (OutputStream out = new FileOutputStream(file)) {
                out.write(anexo.getContenido());
            }
            //
            return file;
        } catch (IOException | SIAException ex) {
            UtilLog4j.log.error(ex);

        }
        return null;
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

    private void llenarPlantilla(XSSFSheet loadSheet, Proveedor proveedor, List<ContactoProveedorVO> lcp) {
        try {
            LecturaLibro ll = new LecturaLibro();
            ll.setValueExcel(loadSheet, proveedor.getRfc(), 9, 17);
            ll.setValueExcel(loadSheet, proveedor.getIdCif(), 9, 39);
            ll.setValueExcel(loadSheet, proveedor.getNombre(), 20, 7);
            if (lcp != null && !lcp.isEmpty()) {
                ll.setValueExcel(loadSheet, lcp.get(Constantes.CERO).getNombre(), 20, 39);
                ll.setValueExcel(loadSheet, lcp.get(Constantes.CERO).getCorreo(), 23, 7);
                ll.setValueExcel(loadSheet, lcp.get(Constantes.CERO).getTelefono(), 23, 39);
            }

            ll.setValueExcel(loadSheet, proveedor.getCalle() + proveedor.getNumero(), 29, 7);
            ll.setValueExcel(loadSheet, validarNull(proveedor.getColonia()), 29, 39);
            ll.setValueExcel(loadSheet, validarNull(proveedor.getCiudad()), 32, 7);
            ll.setValueExcel(loadSheet, validarNull(proveedor.getEstado()), 32, 39);
            ll.setValueExcel(loadSheet, validarNull(proveedor.getCodigoPostal()), 35, 7);
            ll.setValueExcel(loadSheet, validarNull(proveedor.getPais()), 35, 19);
            ll.setValueExcel(loadSheet, proveedor.getOcTerminoPago().getNombre(), 40, 7);
        } catch (Exception ex) {
            //
            Logger.getLogger(ProveedorDocumento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String validarNull(String objeto) {
        if (objeto != null && !objeto.isEmpty() && !"null".equalsIgnoreCase(objeto)) {
            return objeto;
        } else {
            return "";
        }
    }
}
