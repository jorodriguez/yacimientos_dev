package com.ihsa.sia.inventario.beans.herramientas;

import com.ihsa.sia.commons.AbstractBean;
import sia.inventarios.service.TransaccionImpl;
import sia.modelo.vo.inventarios.TransaccionVO;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * @author Aplimovil SA de CV
 */
@Named(value = "rastrearArticulo")
@ViewScoped
public class RastrearArticuloBean extends AbstractBean implements Serializable {

    @Inject
    private TransaccionImpl servicio;

    private String filtro;
    private List<TransaccionVO> lista;

    @PostConstruct
    public void init() {
        lista = new ArrayList<TransaccionVO>();
    }

    public void buscar() {
        lista = servicio.rastrearArticulo(getFiltro());
        if (lista.isEmpty()) {
            addInfoMessage(obtenerCadenaDeRecurso("sia.inventarios.herramientas.rastrear.noResultados"));
        }
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<TransaccionVO> getLista() {
        return lista;
    }
}
