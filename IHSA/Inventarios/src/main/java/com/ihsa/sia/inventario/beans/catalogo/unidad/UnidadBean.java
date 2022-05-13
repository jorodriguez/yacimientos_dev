package com.ihsa.sia.inventario.beans.catalogo.unidad;

import com.ihsa.sia.inventario.beans.LocalAbstractBean;
import java.io.Serializable;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import sia.modelo.vo.inventarios.UnidadVO;
import sia.inventarios.service.UnidadImpl;

/**
 *
 * @author Aplimovil SA de CV
 */
@Named(value = "unidad")
@ViewScoped
public class UnidadBean extends LocalAbstractBean<UnidadVO, Integer> implements Serializable {

    //Inyeccion de servicio
    @Inject
    private UnidadImpl servicio;
    private boolean embeddedUnidad;

    public UnidadBean() {
        super(UnidadVO.class);
    }

    protected UnidadImpl getServicio() {
        return servicio;
    }

    @Override
    protected String mensajeCrearKey() {
        return "sia.inventarios.catalogo.unidades.crearMensaje";
    }

    @Override
    protected String mensajeEditarKey() {
        return "sia.inventarios.catalogo.unidades.editarMensaje";
    }

    @Override
    protected String mensajeEliminarKey() {
        return "sia.inventarios.catalogo.unidades.eliminarMensaje";
    }

    public void cancelar(ActionEvent e) {
        setEmbedded(false);
    }

    public boolean isEmbedded() {
        return embeddedUnidad;
    }

    public void setEmbedded(boolean embeddedUnidad) {
        this.embeddedUnidad = embeddedUnidad;
    }

}
