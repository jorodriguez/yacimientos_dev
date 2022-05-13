package com.ihsa.sia.api.mobile.inventario;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import sia.modelo.vo.inventarios.TransaccionArticuloVO;

/**
 * Clase que represanta una lista de artículos como parámetro de un endpoint
 * Rest del api para aplicaciones moviles
 *
 * @author Aplimovil SA de CV
 */
public class ArticulosParametro {

    private String value;
    private List<TransaccionArticuloVO> articulos;

    public ArticulosParametro(String value) {
	this.value = value;
    }

    public static ArticulosParametro valueOf(String value) {
	return new ArticulosParametro(value);
    }

    List<TransaccionArticuloVO> getArticulos() {
	if (articulos != null) {
	    return articulos;
	}
	Gson gson = new Gson();
	Type listType = new TypeToken<ArrayList<ArticuloMovimientoParametro>>() {
	}.getType();
	List<ArticuloMovimientoParametro> lista = gson.fromJson(value, listType);
	articulos = mapearObjeto(lista);
	return articulos;
    }

    ;

    private static List<TransaccionArticuloVO> mapearObjeto(List<ArticuloMovimientoParametro> lista) {
	List<TransaccionArticuloVO> resultado = new ArrayList<TransaccionArticuloVO>();
	for (ArticuloMovimientoParametro parametro : lista) {
	    TransaccionArticuloVO vo = new TransaccionArticuloVO();
	    vo.setArticuloId(parametro.getId());
	    vo.setNumeroUnidades(parametro.getUnidades());
	    vo.setIdentificador(parametro.getIdentificador());
	    resultado.add(vo);
	}
	return resultado;
    }
}
