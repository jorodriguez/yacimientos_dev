package com.ihsa.sia.inventario.beans;

import com.ihsa.sia.commons.AbstractBean;
import sia.inventarios.service.AvisoImpl;
import sia.modelo.vo.inventarios.AvisoVO;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Collections;
import javax.inject.Inject;

/**
 * @author Aplimovil SA de CV
 */
@Named(value = "mensaje")
@ViewScoped
public class MensajeBean extends AbstractBean implements Serializable {

    private static final long serialVersionUID = 164878925344220088L;

    @Inject
    private AvisoImpl servicio;

    private AvisoVO aviso;

    @PostConstruct
    public void init() {
        leerMensaje();
    }

    public AvisoVO getAviso() {
        return aviso;
    }

    public String eliminar() {
        servicio.eliminarAvisos(Collections.singletonList(getAviso()));
        return "/views/index";
    }

    public String marcarComoNoLeido() {
        servicio.marcarComoLeidos(Collections.singletonList(getAviso()));
        return "/views/index";
    }

    private void leerMensaje() {
        String paramMensajeId = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("mensajeId");
        if (paramMensajeId == null) {
            return;
        }
        try {
            int mensajeId = Integer.valueOf(paramMensajeId);
            aviso = servicio.leerAviso(mensajeId, getUserName());
        } catch (Exception ex) {
            ManejarExcepcion(ex);
        }
    }
}
