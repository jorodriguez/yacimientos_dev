/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import sia.modelo.sistema.vo.CategoriaVo;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.modelo.vo.inventarios.InventarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.sistema.impl.SiCategoriaImpl;

/**
 *
 * @author Aplimovil SA de CV
 */
@Path("articulo")
@RequestScoped
public class ArticuloApi {

    private static final Integer LIMITE_REGISTROS = 100;

    @Inject
    protected ArticuloImpl articuloService;

    @Inject
    protected InventarioImpl inventarioService;

    @Inject
    private SiCategoriaImpl categoriaServicio;
    @Inject
    protected ApCampoImpl apCampoRemote;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ArticuloVO> getArticulos(@QueryParam("palabra") String palabra, @QueryParam("campo") String campo) {
        List<ArticuloVO> resultados = articuloService.buscarPorPalabras(palabra, campo);
        List<ArticuloVO> sublista = new ArrayList<ArticuloVO>();
        Integer conteo = 0;

        for (ArticuloVO articuloVO : resultados) {
            conteo++;

            if (conteo > LIMITE_REGISTROS) {
                break;
            }

            sublista.add(articuloVO);
        }

        if (sublista.isEmpty()) {
            sublista.add(new ArticuloVO(0, Messages.getString("sia.inventarios.comun.noResultados")));
        }

        return sublista;
    }

    @GET
    @Path("inventario")
    @Produces(MediaType.APPLICATION_JSON)
    public List<InventarioVO> getInventarios(@QueryParam("articuloId") Integer articuloId, @QueryParam("campo") String campo) {
        return articuloService.buscarInventarios(articuloId, apCampoRemote.buscarPorNombre(campo).getId());
    }

    @GET
    @Path("/subcategorias")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CategoriaVo> getSubcategorias(@QueryParam("categoriaId") Integer categoriaId) {
        return categoriaServicio.traerSubCategorias(categoriaId);
    }
}
