package com.ihsa.sia.inventario.beans.herramientas;

import com.ihsa.sia.commons.AbstractBean;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import sia.excepciones.SIAException;
import sia.inventarios.service.ArticuloRemote;
import sia.modelo.vo.inventarios.ArticuloVO;

/**
 * @author Aplimovil SA de CV
 */
@Named(value = "impresionEtiqueta")
@ViewScoped
public class ImpresionEtiquetaBean extends AbstractBean implements Serializable {

    @Inject
    private ArticuloRemote servicioArticulo;
    private ArticuloVO articulo;
    private int numeroDeEtiquetas;
    private String datosEtiqueta;

    @PostConstruct
    public void init() {
        articulo = new ArticuloVO();
    }

    public void articuloChanged(AjaxBehaviorEvent e) {
        try {
            if (articulo.getId() == null) {
                return;
            }
            articulo = servicioArticulo.buscar(articulo.getId(), getCampoId());
            //
            if (articulo.getNombre().length() > 20) {
                datosEtiqueta = articulo.getNombre().substring(0, 20);
            } else {
                datosEtiqueta = articulo.getNombre();
            }

        } catch (SIAException ex) {
            ManejarExcepcion(ex);
        }
    }

    public void setArticulo(ArticuloVO articulo) {
        this.articulo = articulo;
    }

    public ArticuloVO getArticulo() {
        return articulo;
    }

    public int getNumeroDeEtiquetas() {
        return numeroDeEtiquetas;
    }

    public void setNumeroDeEtiquetas(int numeroDeEtiquetas) {
        this.numeroDeEtiquetas = numeroDeEtiquetas;
    }

    /**
     * @return the datosEtiqueta
     */
    public String getDatosEtiqueta() {
        return datosEtiqueta;
    }

    /**
     * @param datosEtiqueta the datosEtiqueta to set
     */
    public void setDatosEtiqueta(String datosEtiqueta) {
        this.datosEtiqueta = datosEtiqueta;
    }
}
