
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.we.servicio;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.excepciones.SIAException;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.util.UtilLog4j;

/**
 * REST Web Service
 *
 * @author ihsa
 */
@Stateless
@LocalBean
@Path("serviciosWebUtils")
public class ServiciosUtils implements Serializable {

       /*
    @Inject
    private UsuarioImpl usuarioImpl;
        
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    @Inject
    private SiAdjuntoImpl servicioSiAdjuntoImpl;
   
    @Inject
    private SiParametroImpl parametrosSistemaServicioRemoto;

    final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
    final SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm");

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    @GET
    @Path("/download")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@QueryParam("ZWZ2W") int zwz2w, @QueryParam("ZWZ3W") String zwz3w) {
        
        LOGGER.info("@downloadFile ");
        
        Response responseReturn = null;        
        
        String sb = new StringBuilder()
                .append("<html><body><table><tr><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:15px\">")
                .append("La solicitud que est&#225;s realizando es incorrecta o no existe el archivo. ") //.append("Los identificadores del archivo de la petici&#243;n no coinciden con los identificadores en la base de datos.")
                .append("</td></tr></table></body></html>").toString();

        LOGGER.info("zwz2w "+zwz2w+" zwz3w"+zwz3w);
        
        if (zwz2w == 0 || zwz3w == null || zwz3w.isEmpty()) {
                LOGGER.info("Error de validacion");
                return Response.ok("Error de validaci&#225;n de datos", MediaType.TEXT_HTML).build();
        }
                
        AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        boolean error = false;

        try {
            
            LOGGER.info("Iniciando la descarga");

            final int SAId = zwz2w;

            final String SAUUID = zwz3w;

            AdjuntoVO adjunto = servicioSiAdjuntoImpl.buscarArchivo(SAId, SAUUID);

            String path = this.parametrosSistemaServicioRemoto.find(1).getUploadDirectory();
            

            if (adjunto == null) {
                UtilLog4j.log.info("No existe el adjunto");
                responseReturn = Response.ok(sb, MediaType.TEXT_HTML).header("text/html", sb).build();

            } else {
                UtilLog4j.log.info("Adjunto "+adjunto.getNombre()+" EN "+adjunto.getUrl());
                UtilLog4j.log.info("Iniciando contenedores de descarga");
                LOGGER.info("URL : " + almacenDocumentos.getRaizAlmacen());
                DocumentoAnexo documento = almacenDocumentos.cargarDocumento(adjunto.getUrl());
               
                final StreamingOutput fileStream = new StreamingOutput() {
                    @Override
                    public void write(OutputStream output) throws IOException, WebApplicationException {
                        try {
                            output.write(documento.getContenido());
                            output.flush();
                        } catch (Exception e) {
                            LOGGER.error(e);
                            throw new WebApplicationException(e);
                        }
                    }
                };

                return Response
                        .ok(fileStream, documento.getTipoMime())
                        .header("content-disposition", "attachment;filename=\"" + adjunto.getNombre() + "\"")
                        .build();

            }

        } catch (SIAException e) {            
            LOGGER.error("File : ", e);
            error = true;
        } catch (Exception e) {
            System.out.println("Error "+e.getMessage());
            LOGGER.error("File : ", e);            
            error = true;
        } finally {
            if (error) {                
                responseReturn = Response.ok(sb, MediaType.TEXT_HTML).header("text/html", sb).build();
            }
        }

        return responseReturn;
    }   
*/
}
