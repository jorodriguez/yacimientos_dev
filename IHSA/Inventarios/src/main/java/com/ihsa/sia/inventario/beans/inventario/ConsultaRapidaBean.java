package com.ihsa.sia.inventario.beans.inventario;

import com.ihsa.sia.commons.Messages;
import com.ihsa.sia.commons.SessionBean;
import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;
import sia.excepciones.SIAException;
import sia.inventarios.service.ArticuloRemote;
import sia.inventarios.service.InventarioImpl;
import sia.inventarios.service.InventarioMovimientoImpl;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.modelo.vo.inventarios.InventarioMovimientoVO;
import sia.modelo.vo.inventarios.InventarioVO;
import sia.util.UtilLog4j;

/**
 *
 * @author Aplimovil SA de CV
 */
@Named(value = "consultaRapida")
@ViewScoped
public class ConsultaRapidaBean implements Serializable {

    @Inject
    ArticuloRemote servicio;
    @Inject
    protected InventarioImpl servicioInventario;
    @Inject
    protected InventarioMovimientoImpl servicioMovimientos;
    @Inject
    SessionBean principal;

    private ArticuloVO articulo;
    private List<InventarioVO> listaNivelInventario;
    private List<InventarioMovimientoVO> listaMovimientos;
    private String tituloMovimientos;
    private int filasTotales;
    private final Integer tamanioPagina = 20;

    @PostConstruct
    public void init() {
        articulo = new ArticuloVO();
    }

    public void articuloChanged() {
        try {
            listaNivelInventario = servicioInventario.inventarioPorArticuloCampo(articulo.getId(), principal.getUser().getIdCampo());//buscarInventarios(articulo.getId(), principal.getUser().getIdCampo());
            listaMovimientos = null;
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
    }

    public List<ArticuloVO> completarArticulo(String cadena) {
        return servicio.buscarPorPalabras(cadena, principal.getUser().getCampo());
    }

    public void verMovimientos(Integer inventarioId, String almacenNombre) {
        try {
            String mensaje = Messages.getString("sia.inventarios.consulta.movimientos");
            tituloMovimientos = MessageFormat.format(mensaje, almacenNombre, articulo.getNombre());
            cargarMovimientos(inventarioId);
        } catch (Exception ex) {
            UtilLog4j.log.equals(ex);
        }
    }

    public void cargarMovimientos(final Integer inventarioId) {
        try {
            String campoOrdenar = "fecha";
            boolean esAscendente = false;
            InventarioMovimientoVO filtro = new InventarioMovimientoVO();
            filtro.setInventarioId(inventarioId);
            this.listaMovimientos = servicioMovimientos.buscarPorFiltros(filtro, null, null, "fecha", true, principal.getUser().getIdCampo());

            //contarFilas(inventarioId);
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
    }

    protected void contarFilas(Integer inventarioId) throws SIAException {
        InventarioMovimientoVO filtro = new InventarioMovimientoVO();
        filtro.setInventarioId(inventarioId);
        filasTotales = servicioMovimientos.contarPorFiltros(filtro, principal.getUser().getIdCampo());
    }

    public String fechaConFormato(Date date) {
        if (date == null) {
            return "";
        }
        long diff = new Date().getTime() - date.getTime();
        return MessageFormat.format(Messages.getString("sia.inventarios.comun.fechaFormato"),
                new SimpleDateFormat("dd/MM/yyyy").format(date),
                TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
    }

    public ArticuloVO getArticulo() {
        return articulo;
    }

    public void setArticulo(ArticuloVO articulo) {
        this.articulo = articulo;
    }

    public List<InventarioVO> getListaNivelInventario() {
        return listaNivelInventario;
    }

    public List<InventarioMovimientoVO> getListaMovimientos() {
        return listaMovimientos;
    }

    public String getTituloMovimientos() {
        return tituloMovimientos;
    }

    public Integer getTamanioPagina() {
        return tamanioPagina;
    }
}
