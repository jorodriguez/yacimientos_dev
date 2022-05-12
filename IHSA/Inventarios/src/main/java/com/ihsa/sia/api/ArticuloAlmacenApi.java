package com.ihsa.sia.api;

import com.ihsa.sia.commons.Messages;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import sia.inventarios.service.ArticuloImpl;
import sia.inventarios.service.InventarioImpl;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.modelo.vo.inventarios.InventarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;

/**
 *
 * @author Aplimovil SA de CV
 */
@Path("articulo/almacen")
@RequestScoped
public class ArticuloAlmacenApi {

    private static final Integer LIMITE_REGISTROS = 100;

    @Inject
    protected ArticuloImpl articuloService;

    @Inject
    protected InventarioImpl inventarioService;
    @Inject
    protected ApCampoImpl apCampoRemote;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ArticuloVO> getArticulos(@QueryParam("nombreArticulo") String nombreArticulo, @QueryParam("almacenId") Integer almacenId, @QueryParam("campo") String campo) {
	List<ArticuloVO> resultados = articuloService.buscarPorPalabras(nombreArticulo, campo);
	List<ArticuloVO> sublista = new ArrayList<>();
	Integer conteo = 0;
	//System.out.println("Resultados  = " + resultados.size());
	for (ArticuloVO articuloVO : resultados) {
	    conteo++;

	    if (conteo > LIMITE_REGISTROS) {
		break;
	    }

	    InventarioVO filtro = new InventarioVO();
	    filtro.setArticuloId(articuloVO.getId());
	    filtro.setAlmacenId(almacenId);

	    Integer numeroInventarios = inventarioService.contarPorFiltros(filtro, apCampoRemote.buscarPorNombre(campo).getId());

	    if (numeroInventarios.equals(0)) {
		continue;
	    }

	    sublista.add(articuloVO);
	    //System.out.println("conteo :: " + conteo);
	}

	if (sublista.isEmpty()) {
	    sublista.add(new ArticuloVO(0, Messages.getString("sia.inventarios.comun.noResultadosInventarios")));
	}

	return sublista;
    }
}
