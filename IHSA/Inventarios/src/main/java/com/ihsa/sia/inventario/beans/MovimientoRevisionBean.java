package com.ihsa.sia.inventario.beans;

import com.ihsa.sia.inventario.beans.inventario.MovimientosBean;
import java.io.Serializable;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.model.ListDataModel;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.vo.inventarios.TransaccionVO;

/**
 *
 * @author Aplimovil SA de CV
 */
@Named(value = "movimientosRevision")
@ViewScoped
public class MovimientoRevisionBean extends MovimientosBean implements Serializable {

    public MovimientoRevisionBean() {

    }

    @Override
    protected void cargarListaConFiltros() {
	try {
	    setLista(new ListDataModel<TransaccionVO>(getServicio().buscarPorStatus(
		    Constantes.INV_TRANSACCION_STATUS_TRASPASO_PENDIENTE_REVISION,
		    principal.getUser().getIdCampo())));
	} catch (SIAException ex) {
	    ManejarExcepcion(ex);
	}
    }
}
