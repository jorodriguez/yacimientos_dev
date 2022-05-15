package com.ihsa.sia.inventario.beans;

import com.ihsa.sia.commons.AbstractBean;
import sia.inventarios.service.AvisoImpl;
import sia.modelo.vo.inventarios.AvisoVO;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.primefaces.component.datatable.DataTable;

/**
 * @author Aplimovil SA de CV
 */
@Named(value = "avisos")
@ViewScoped
public class AvisoBean extends AbstractBean implements Serializable {
    private static final long serialVersionUID = 164878925344220088L;

    @Inject
    private AvisoImpl servicio;

    private DataTable tablaAvisos;
    private List<AvisoVO> lista;

    @PostConstruct
    public void init() {
        actualizar();
    }

    public DataTable getTablaAvisos() {
        return tablaAvisos;
    }

    public void setTablaAvisos(DataTable tablaAvisos) {
        this.tablaAvisos = tablaAvisos;
    }

    public List<AvisoVO> getLista() {
        return lista;
    }

    public List<AvisoVO> getSeleccionados() {
        List<AvisoVO> seleccionados = new ArrayList<>();
        for (AvisoVO aviso : getLista()) {
            if (aviso.isSeleccionado()) {
                seleccionados.add(aviso);
            }
        }
        return seleccionados;
    }

    public void actualizar() {
        lista = servicio.listarAvisos(getUserName());
        if (tablaAvisos != null) {
            tablaAvisos.resetValue();
        }
    }

    public void marcarComoNoLeidos() {
        servicio.marcarComoLeidos(getSeleccionados());
        actualizar();
        addInfoMessage(obtenerCadenaDeRecurso("sia.inventarios.avisos.noLeidoMensaje"));
    }

    public void eliminar() {
        servicio.eliminarAvisos(getSeleccionados());
        actualizar();
        addInfoMessage(obtenerCadenaDeRecurso("sia.inventarios.avisos.elminadoMensaje"));
    }
}
