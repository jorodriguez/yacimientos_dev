package com.ihsa.sia.inventario.beans.inventario;

import com.ihsa.sia.commons.Messages;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.model.SelectItem;
import static sia.constantes.Constantes.INV_TRANSACCION_STATUS_APLICADA;
import static sia.constantes.Constantes.INV_TRANSACCION_STATUS_PREPARACION;
import static sia.constantes.Constantes.INV_TRANSACCION_STATUS_RECHAZADA;
import static sia.constantes.Constantes.INV_TRANSACCION_STATUS_TRASPASO_PENDIENTE_REVISION;

/**
 *
 * @author Aplimovil SA de CV
 */
@SessionScoped
@Named
public class EstatusMovimiento implements Serializable{

    private Map<Integer, String> estatus;
    private Map<Integer, String> estilos;

    @PostConstruct
    public void init() {
        estatus = new HashMap<Integer, String>();
        estatus.put(INV_TRANSACCION_STATUS_PREPARACION, Messages.getString("sia.inventarios.movimiento.estatus.preparacion"));
        estatus.put(INV_TRANSACCION_STATUS_APLICADA, Messages.getString("sia.inventarios.movimiento.estatus.aplicado"));
        estatus.put(INV_TRANSACCION_STATUS_RECHAZADA, Messages.getString("sia.inventarios.movimiento.estatus.rechazado"));
        estatus.put(INV_TRANSACCION_STATUS_TRASPASO_PENDIENTE_REVISION, Messages.getString("sia.inventarios.movimiento.estatus.traspasoRevision"));
        //se fijan los estilos por tipo
        estilos = new HashMap<Integer, String>();
        estilos.put(INV_TRANSACCION_STATUS_PREPARACION, Messages.getString("sia.inventarios.estilos.estatus.preparacion"));
        estilos.put(INV_TRANSACCION_STATUS_APLICADA, Messages.getString("sia.inventarios.estilos.estatus.aplicado"));
        estilos.put(INV_TRANSACCION_STATUS_RECHAZADA, Messages.getString("sia.inventarios.estilos.estatus.rechazado"));
        estilos.put(INV_TRANSACCION_STATUS_TRASPASO_PENDIENTE_REVISION, Messages.getString("sia.inventarios.estilos.estatus.traspasoRevision"));
    }

    public String getNombreEstatus(Integer id) {
        return estatus.get(id);
    }

    public String getEstilo(Integer id) {
      return estilos.get(id);
    }
    
    public List<SelectItem> getEstatus() {
      List<SelectItem> items = new ArrayList<SelectItem>();
      for(Map.Entry<Integer, String> entry : estatus.entrySet()) {
        items.add(new SelectItem(entry.getKey(), entry.getValue()));
      }
      return items;
    }
    
    public Integer getEstatusRechazada() {
        return INV_TRANSACCION_STATUS_RECHAZADA;
    }
    
    public boolean esPendienteRevision(Integer estatus) {
        return Objects.equals(INV_TRANSACCION_STATUS_TRASPASO_PENDIENTE_REVISION, estatus);
    }
}
