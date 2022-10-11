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
import sia.modelo.vo.inventarios.DetalleSolicitudMaterialAlmacenVo;
import sia.modelo.vo.inventarios.TransaccionArticuloVO;

/**
 *
 * @author mluis
 */
public class SolicitudMaterialesDetalleParametroMovil {

    private String value;
    private List<DetalleSolicitudMaterialAlmacenVo> materiales;

    public SolicitudMaterialesDetalleParametroMovil(String value) {
        this.value = value;
    }

    public static SolicitudMaterialesDetalleParametroMovil valueOf(String value) {
        return new SolicitudMaterialesDetalleParametroMovil(value);
    }

    List<DetalleSolicitudMaterialAlmacenVo> getArticulos() {
        try {
            if (materiales != null) {
                return materiales;
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<DetalleSolicitudMaterialMovilVo>>() {
            }.getType();
            List<DetalleSolicitudMaterialMovilVo> lista = gson.fromJson(value, listType);
            materiales = mapearObjeto(lista);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return materiales;
    }

    ;

    private static List<DetalleSolicitudMaterialAlmacenVo> mapearObjeto(List<DetalleSolicitudMaterialMovilVo> lista) {
        List<DetalleSolicitudMaterialAlmacenVo> resultado = new ArrayList<DetalleSolicitudMaterialAlmacenVo>();
        for (DetalleSolicitudMaterialMovilVo parametro : lista) {
            DetalleSolicitudMaterialAlmacenVo vo = new DetalleSolicitudMaterialAlmacenVo();
            vo.setId(parametro.getId());
            vo.setArticulo(parametro.getArticulo());
            vo.setUnidad(parametro.getUnidad());
            vo.setIdArticulo(parametro.getArticuloId());
            vo.setCantidadRecibida(parametro.getCantidadRecibida());
            resultado.add(vo);
        }
        return resultado;
    }

}
