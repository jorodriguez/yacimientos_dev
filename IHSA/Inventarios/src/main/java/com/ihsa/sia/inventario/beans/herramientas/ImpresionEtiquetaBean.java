package com.ihsa.sia.inventario.beans.herramientas;

import com.ihsa.sia.commons.SessionBean;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;
import sia.inventarios.service.ArticuloRemote;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.util.UtilLog4j;

/**
 * @author Aplimovil SA de CV
 */
@Named(value = "impresionEtiqueta")
@ViewScoped
public class ImpresionEtiquetaBean  implements Serializable {

    @Inject
    ArticuloRemote servicioArticulo;
    @Inject
    SessionBean principal;
    private ArticuloVO articulo;
    private int numeroDeEtiquetas;
    private String datosEtiqueta;
    

    @PostConstruct
    public void init() {
        articulo = new ArticuloVO();
    }

    public List<ArticuloVO> completarArticulo(String cadena) {
        return servicioArticulo.buscarPorPalabras(cadena, principal.getUser().getCampo()
    

    );
    }

    public void articuloChanged(SelectEvent<String> event) {
        try {
            articulo = servicioArticulo.buscar(articulo.getId(), principal.getUser().getIdCampo());
            //
            if (articulo.getNombre().length() > 20) {
                datosEtiqueta = articulo.getNombre().substring(0, 20);
            } else {
                datosEtiqueta = articulo.getNombre();
            }
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
            System.out.println("EXc:" + ex);
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
