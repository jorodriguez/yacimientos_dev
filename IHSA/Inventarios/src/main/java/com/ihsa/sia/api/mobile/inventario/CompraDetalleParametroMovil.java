/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ihsa.sia.api.mobile.inventario;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import sia.modelo.vo.inventarios.TransaccionArticuloVO;

/**
 *
 * @author mluis
 */
public class CompraDetalleParametroMovil {

    private String value;
    private List<TransaccionArticuloVO> articulos;

    public CompraDetalleParametroMovil(String value) {
        this.value = value;
    }

    public static CompraDetalleParametroMovil valueOf(String value) {
        return new CompraDetalleParametroMovil(value);
    }

    List<TransaccionArticuloVO> getArticulos() {
        try {
            if (articulos != null) {
                return articulos;
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<OrdenDetalleMovilVo>>() {
            }.getType();
            List<OrdenDetalleMovilVo> lista = gson.fromJson(value, listType);
            articulos = mapearObjeto(lista);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articulos;
    }

    ;

    private static List<TransaccionArticuloVO> mapearObjeto(List<OrdenDetalleMovilVo> lista) {
        List<TransaccionArticuloVO> resultado = new ArrayList<TransaccionArticuloVO>();
        for (OrdenDetalleMovilVo parametro : lista) {
            TransaccionArticuloVO vo = new TransaccionArticuloVO();
            vo.setDetalleCompraId(parametro.getId());
            vo.setArticuloNombre(parametro.getNombreArticulo());
            vo.setArticuloId(parametro.getIdArticulo());
            vo.setPrecioUnitario(parametro.getPrecio());
            vo.setNumeroUnidades(parametro.getTotalRecibido());
            vo.setCantidad(parametro.getCantidad());
            vo.setTotalPendiente(parametro.getTotalPendiente());
            resultado.add(vo);
        }
        return resultado;
    }

}
