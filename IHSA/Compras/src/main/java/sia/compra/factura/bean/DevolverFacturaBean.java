/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.factura.bean;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedProperty;


import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.modelo.sistema.vo.FacturaVo;
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.servicios.sistema.impl.SiFacturaStatusImpl;
import sia.util.FacturaEstadoEnum;

/**
 *
 * @author mluis
 */
@Named (value = "devolverFacturaBean")
@ViewScoped
public class DevolverFacturaBean implements Serializable {

    
    @Inject
    private UsuarioBean usuarioBean;
    @Inject
    private SiFacturaImpl siFacturaImpl;
    @Inject
    private SiFacturaStatusImpl siFacturaStatusImpl;

    @Getter
    @Setter
    private List<FacturaVo> facturas;
    @Getter
    @Setter
    private FacturaVo facturaVo;
    @Getter
    @Setter
    private String folio = "";
    @Getter
    @Setter
    private String motivo = "";

    /**
     * Creates a new instance of DevolverFacturaBean
     */
    public DevolverFacturaBean() {
    }

    public void buscarFacturaFolioStatus() {
        facturas = siFacturaImpl.traerFacturaActualPorFolioStatus(folio, FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId());
        if (facturas.isEmpty()) {
            FacesUtilsBean.addErrorMessage("No hay facturas con este folio en proceso internos de revisión.");
        }
    }

    public void seleccionar(int idFac) {
        facturaVo = siFacturaImpl.buscarFactura(idFac);
    }

    public void devolverFactura() {

        try {
            Preconditions.checkState(facturaVo != null, "No hay factura par devolver");
            Preconditions.checkState(!motivo.isEmpty(), "Agregue el motivo");

            siFacturaStatusImpl.rechazarCCN(
                    usuarioBean.getUsuarioConectado().getId(), 
                    facturaVo, 
                    motivo, 
                    usuarioBean.getUsuarioConectado().getEmail(), 
                    FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId()
            );
            
            motivo = "";
            facturaVo = null;
            //
            facturas = siFacturaImpl.traerFacturaActualPorFolioStatus(folio, FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId());
        } catch (IllegalStateException e) {
            FacesUtilsBean.addErrorMessage(e.getMessage());
        }
    }

    /**
     * @param usuarioBean the usuarioBean to set
     */
    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }
}
