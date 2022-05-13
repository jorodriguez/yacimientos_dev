package com.ihsa.sia.inventario.beans.inventario;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import com.ihsa.sia.commons.Messages;
import java.util.ArrayList;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Named;
import static sia.constantes.Constantes.*;

/**
 *
 * @author Aplimovil SA de CV
 */
@Named(value ="tipoMovimiento")
@ApplicationScoped
public class TipoMovimientoBean implements Serializable{

  private Map<Integer, String> tipos;

  @PostConstruct
  public void init() {
    tipos = new HashMap<Integer, String>();
    tipos.put(INV_MOVIMIENTO_TIPO_ENTRADA, Messages.getString("sia.inventarios.movimiento.tipo.entrada"));
    tipos.put(INV_MOVIMIENTO_TIPO_SALIDA, Messages.getString("sia.inventarios.movimiento.tipo.salida"));
    tipos.put(INV_MOVIMIENTO_TIPO_TRASPASO_SALIENTE, Messages.getString("sia.inventarios.movimiento.tipo.traspaso"));
    tipos.put(INV_MOVIMIENTO_TIPO_MERMA, Messages.getString("sia.inventarios.movimiento.tipo.merma"));
    tipos.put(INV_MOVIMIENTO_TIPO_PERDIDA, Messages.getString("sia.inventarios.movimiento.tipo.perdida"));
  }
  
  public Map<Integer, String> getTiposMovimiento() {
    return tipos;
  }

  public List<SelectItem> buildSelectItems() {
    List<SelectItem> list = new ArrayList<SelectItem>();
    for(Map.Entry<Integer, String> entry : tipos.entrySet()) {
        list.add(new SelectItem(entry.getKey(), entry.getValue()));
    }
    return list;
  }
  
  public String getNombre(Integer id) {
      return tipos.get(id);
  }
  
}
