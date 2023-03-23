/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.procesador.bean;

import lector.sistema.bean.backing.*;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lector.sistema.bean.support.FacesUtils;
import lombok.Getter;
import lombok.Setter;
import lector.servicios.sistema.impl.WhatsappService;
import lector.util.UtilLog4j;

/**
 *
 * @author jorodriguez
 */
@Named
@ViewScoped
public class TestMensajeriaView implements Serializable {

    @Inject
    private Sesion sesion;

    @Inject
    private WhatsappService whatsappService;

    @Getter
    @Setter
    private String telefono;

    @Getter
    @Setter
    private String mensaje;

    private static final UtilLog4j log = UtilLog4j.log;

    public TestMensajeriaView() {
    }

    @PostConstruct
    public void iniciar() {
        System.out.println("@Postconstruc" + this.getClass().getCanonicalName());
    }

    public void enviar() {

        log.info("@enviar");

        if (this.telefono.isBlank() || this.telefono.isEmpty()
                || this.mensaje.isBlank() || this.mensaje.isEmpty()) {
            System.out.println("Validacion error");
            FacesUtils.addErrorMessage("Escribe el telefono y el mensaje a enviar");
            return;
        }

        String ret = whatsappService.send(telefono, mensaje, sesion.getUsuarioSesion().getCCuenta());

        this.mensaje ="";
        
        FacesUtils.addInfoMessage(ret);

    }

}
