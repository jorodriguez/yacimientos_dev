package com.ihsa.sia.servlets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.constantes.Configurador;
import sia.util.UtilLog4j;

/**
 * @author Aplimovil SA de CV
 */
@WebServlet(urlPatterns = "/labels")
public class LabelServlet extends HttpServlet implements Serializable {

    private static final String MESSAGE_PARAMETER = "message";
    private static final String NUMBER_PARAMETER = "numero";
    private static final String NOMBRE_ART = "art";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String message = req.getParameter(MESSAGE_PARAMETER);
            String numero = req.getParameter(NUMBER_PARAMETER);
            String nombreArtCorto = req.getParameter(NOMBRE_ART);
            Objects.requireNonNull(message);
            int et = Integer.parseInt(numero);
            for (int i = 0; i < et; i++) {
                try (Socket clientSocket = new Socket(Configurador.inventarioImpresoraUrl(), 9100)) {
                    //
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    //
                    outToServer.writeBytes("^XA^FO40,30^BQN,6,6^FH^FDMM,B0032" + message + "^FS^FT15,175,Y,N^FD" + nombreArtCorto + "^FS^PQ1,0,1,Y^XZ");
                }
            }
        } catch (NumberFormatException e) {
            writeMessage(resp, "No se emprimieron las etiquetas, es necesrio enviar números.");
        } catch (IOException ex) {
            throw new ServletException(ex);
        }
    }

    public void writeMessage(HttpServletResponse response, String message) {
        try {
            response.setContentType("text/html");
            response.getWriter().println(message);
        } catch (IOException ex) {
            UtilLog4j.log.error(ex);
        }
    }
}
