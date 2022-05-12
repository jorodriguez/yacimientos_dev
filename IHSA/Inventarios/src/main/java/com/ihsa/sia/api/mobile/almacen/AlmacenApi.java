package com.ihsa.sia.api.mobile.almacen;

import com.ihsa.sia.api.mobile.Estado;
import com.ihsa.sia.api.mobile.MovilApiBase;
import com.ihsa.sia.api.mobile.Respuesta;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import sia.constantes.Constantes;
import sia.inventarios.service.AlmacenImpl;
import sia.modelo.vo.inventarios.AlmacenVO;

/**
 * Rest Endpoint para almacenes, contiene funcionalidad para la aplicaciones
 * moviles
 *
 * @author Aplimovil SA de CV
 */
@Path("/mobile/almacenes")
@RequestScoped
public class AlmacenApi extends MovilApiBase {

    @Inject
    private AlmacenImpl servicio;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarAlamacenes() {
	try {
	    //si no se pudo autenticar se retorna la respues con la información del porque no se pudo autenticar
//	    if (!autenticar(API_KEY_PARAM)) {
//		return getRespuestaAutenticacion();
//	    }
	    //se llama al servicio que retorna los almacenes
	    List<AlmacenVO> almacenes = servicio.buscarPorFiltros(new AlmacenVO(), Constantes.AP_CAMPO_DEFAULT);
	    //se crea el objeto resultado del servicio
	    Respuesta<AlmacenMovilVO> respuesta = new Respuesta<AlmacenMovilVO>(Estado.ok);
	    respuesta.setResultados(mapearMovilVo(almacenes));
	    return Response.ok(respuesta).build();
	} catch (Exception ex) {
	    return crearRespuestaDeError(ex);
	}
    }

    private static List<AlmacenMovilVO> mapearMovilVo(List<AlmacenVO> lista) {
	List<AlmacenMovilVO> resultado = new ArrayList<AlmacenMovilVO>();
	for (AlmacenVO almacen : lista) {
	    resultado.add(new AlmacenMovilVO(almacen.getId(), almacen.getNombre()));
	}
	return resultado;
    }
}
