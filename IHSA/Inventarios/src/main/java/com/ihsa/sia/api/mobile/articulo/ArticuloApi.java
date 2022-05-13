package com.ihsa.sia.api.mobile.articulo;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import sia.constantes.Constantes;
import sia.inventarios.service.ArticuloImpl;
import sia.inventarios.service.InvInventarioCeldaImpl;
import sia.modelo.vo.inventarios.ArticuloInventarioVO;
import sia.modelo.vo.inventarios.InventarioVO;

/**
 * Rest Endpoint para artículos, contiene funcionalidad para la aplicaciones
 * moviles
 *
 * @author Aplimovil SA de CV
 */
@Path("/mobile/articulo")
@RequestScoped
public class ArticuloApi extends MovilApiBase {

    @Inject
    protected ArticuloImpl articuloService;
    @Inject
    protected InvInventarioCeldaImpl inventarioCeldaLocal;

    @Path("/buscarPorSKU")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPorSku(@QueryParam("sku") String codigo, @QueryParam("apiKey") String key) {
	try {
	    //System.out.println("Art; " + codigo);
	    //  System.out.println("apikey: " + key);
	    //si no se pudo autenticar se retorna la respues con la información del porque no se pudo autenticar
	    if (!autenticar(key)) {
		return getRespuestaAutenticacion();
	    }
	    //se busca el artículo en base al código            
	    ArticuloInventarioVO articulo = articuloService.buscarArticuloConInventarios(codigo, Constantes.AP_CAMPO_DEFAULT);
	    //si el artículo no existe se retorna una respuesta de la lista vacia de resultados
	    if (articulo == null) {
		return Response.ok(new Respuesta(Estado.ok)).build();
	    }
	    //se crea el objeto de resultado del api
	    UnidadMovilVO unidadMovilVO = new UnidadMovilVO(articulo.getUnidadId(), articulo.getUnidadNombre());
            //            
            // String ubicacion = inventarioCeldaLocal.ubicacionPorArticulo(articulo.getId());
            //            
	    ArticuloMovilVO resultado = new ArticuloMovilVO(articulo.getId(), articulo.getCodigo(), articulo.getNombre(),
		    articulo.getDescripcion(), unidadMovilVO);
	    resultado.setExistencias(mapearArticuloAlmacenMovilVo(articulo.getInventarios()));
	    //Se crea la respuesta del servicio con el resultado
	    Respuesta<ArticuloMovilVO> respuesta = new Respuesta<>(resultado);
	    return Response.ok(respuesta).build();
	} catch (Exception ex) {
	    return crearRespuestaDeError(ex);
	}
    }

    private List<ArticuloAlmacenMovilVO> mapearArticuloAlmacenMovilVo(List<InventarioVO> lista) {
	List<ArticuloAlmacenMovilVO> resultado = new ArrayList<>();
	for (InventarioVO inventario : lista) {
            String ubicacion =  inventarioCeldaLocal.ubicacion(inventario.getId());
	    resultado.add(new ArticuloAlmacenMovilVO(inventario.getId(), inventario.getAlmacenNombre(),
		    inventario.getNumeroUnidades(), ubicacion));
	}
	return resultado;
    }

}
