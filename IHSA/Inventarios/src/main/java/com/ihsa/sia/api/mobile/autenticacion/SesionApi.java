package com.ihsa.sia.api.mobile.autenticacion;

import com.ihsa.sia.api.mobile.Estado;
import com.ihsa.sia.api.mobile.MovilApiBase;
import com.ihsa.sia.api.mobile.Respuesta;
import com.ihsa.sia.commons.Messages;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import sia.modelo.usuario.vo.ApiSessionVO;

/**
 * Rest Endpoint para el manejo de sesión para la aplicaciones moviles
 *
 * @author Aplimovil SA de CV
 */
@Path("/mobile")
@RequestScoped
public class SesionApi extends MovilApiBase {

    private static final String HEADER_CHARSET = MediaType.APPLICATION_JSON + "; charset=UTF-8";

    @Path("/login")
    @POST
    @Produces(HEADER_CHARSET)
    public Response iniciarSesion(@FormParam("usuario") String usuario, @FormParam("hash") String hash) {
	try {
	    //se autentica con las credenciales recibidas
	    ApiSessionVO sessionVO = getAuthService().login(usuario, hash);
	    //si no se inicio sesion las credenciales no son validas
	    if (sessionVO == null) {
		return Response.ok(new Respuesta(Estado.error,
			Messages.getString("sia.inventarios.mobile.mensaje.credenciales_no_validas")))
			.build();
	    }
	    //limpiar api keys invalidos del usuario
	    getAuthService().limpiarAPIKeysAntiguos(usuario);
	    //retornar el objecto de sesion
	    Respuesta<ApiSessionVO> respuesta = new Respuesta<>(sessionVO);
	    return Response.ok(respuesta).build();
	} catch (Exception ex) {
	    return crearRespuestaDeError(ex);
	}
    }

    @Path("/logout")
    @POST
    @Produces(HEADER_CHARSET)
    public Response finalizarSesion(@FormParam(API_KEY_PARAM) String apiKey) {
	try {
	    getAuthService().finalizarSesion(apiKey);
	    return Response.ok(new Respuesta(Estado.ok)).build();
	} catch (Exception ex) {
	    return crearRespuestaDeError(ex);
	}
    }
}
