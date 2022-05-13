package com.ihsa.sia.api.mobile;

import com.ihsa.sia.commons.Messages;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import sia.excepciones.SIAException;
import sia.inventarios.authentication.ApiAuthService;
import sia.inventarios.authentication.ApiKeyExpiradoException;
import sia.inventarios.service.Utilitarios;
import sia.modelo.usuario.vo.UsuarioVO;

/**
 * Clase Base para todos los servicios REST que son utilizados en las
 * aplicaciones moviles. Provee codigo común como autenticación.
 *
 * @author Aplimovil SA de CV
 */
//@RequestScoped
public class MovilApiBase implements Serializable{

    protected static final String API_KEY_PARAM = "apiKey";
    public static final String HEADER_CHARSET = MediaType.APPLICATION_JSON + "; charset=UTF-8";

    @Inject
    protected ApiAuthService authService;
    @Context
    private HttpServletRequest httpRequest;
    private Respuesta respuestaAutenticacion;
    // private String apiKey;

//    protected boolean autenticar() {
//	return autenticar(getApiKeyFromRequest());
//    }
    protected boolean autenticar(String apiKey) {
	if (Utilitarios.esNuloOVacio(apiKey)) {
	    respuestaAutenticacion = new Respuesta(Estado.error,
		    Messages.getString("sia.inventarios.mobile.mensaje.no_api_key"));
	    return false;
	}
	try {
	    if (!authService.esApiKeyValido(apiKey)) {
		respuestaAutenticacion = new Respuesta(Estado.error,
			Messages.getString("sia.inventarios.mobile.mensaje.api_key.no_valido"));
		return false;
	    }
	    //  this.apiKey = apiKey;
	    return true;
	} catch (ApiKeyExpiradoException ex) {
	    respuestaAutenticacion = new Respuesta(Estado.error,
		    Messages.getString("sia.inventarios.mobile.mensaje.api_key.expirado"));
	    return false;
	}
    }

    protected Response crearRespuestaDeError(Exception ex) {
	ex.printStackTrace();
	if (ex.getCause() instanceof IllegalArgumentException) {
	    return Response.ok(new Respuesta(Estado.error,
		    Messages.getString("sia.inventarios.mobile.mensaje.api_key.no_valido")))
		    .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
		    .build();
	} else if (ex.getCause() instanceof SIAException) {
	    return Response.ok(new Respuesta(Estado.error, ex.getMessage()))
		    .header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
		    .build();
	}
	return Response.ok(new Respuesta(Estado.error,
		Messages.getString("sia.inventarios.mobile.mensaje.error")))
		.header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
		.build();
    }

    protected Response getRespuestaAutenticacion() {
	return Response.ok(respuestaAutenticacion)
		.header(HttpHeaders.CONTENT_TYPE, HEADER_CHARSET)
		.build();
    }

    protected UsuarioVO getUsuario(String key) throws Exception {
	return authService.obtenerUsuario(key);
    }

    protected ApiAuthService getAuthService() {
	return authService;
    }

//    private String getApiKeyFromRequest() {
//	return httpRequest.getMethod().equals("GET") ? httpRequest.getParameter(API_KEY_PARAM) : null;
//    }
}
