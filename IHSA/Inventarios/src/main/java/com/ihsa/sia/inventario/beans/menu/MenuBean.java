package com.ihsa.sia.inventario.beans.menu;

import com.ihsa.sia.commons.SessionBean;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import javax.faces.context.FacesContext;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import sia.constantes.Constantes;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.util.UtilLog4j;
import sia.util.UtilSia;

@Named(value = "menuBean")
@SessionScoped
public class MenuBean implements Serializable {

    @Inject
    private SiOpcionImpl siOpcionImpl;
    @Inject
    SessionBean sessionBean;
    @Getter
    @Setter
    private MenuModel modelo;

    //private SessionBean session;
    @PostConstruct
    public void init() {
        construirMenu();
    }

    public void siaGo() {
        redireccionar(Constantes.URL_REL_SIA_PRINCIPAL);
    }

    private void redireccionar(final String url) {
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String prefix = UtilSia.getUrl(origRequest);

        FacesContext fc = FacesContext.getCurrentInstance();

        try {
            fc.getExternalContext().redirect(prefix + url); // redirecciona la página
        } catch (IOException ex) {
            UtilLog4j.log.fatal(this, "Error de IO al redireccionar: " + ex.getMessage());
        }

    }

    private void construirMenu() {
        modelo = new DefaultMenuModel();
        if (sessionBean == null) {
            return;
        }
        List<SiOpcionVo> opcionesMenu = obtenerOpcionesPrincipalesMenu();
        for (SiOpcionVo opcion : opcionesMenu) {
            if (opcion.getPagina() != null) {
                //First submenu
                DefaultSubMenu firstSubmenu = DefaultSubMenu.builder()
                        .label(opcion.getNombre())
                        .build();
                modelo.getElements().add(firstSubmenu);
            } else {
                DefaultSubMenu firstSubmenu = DefaultSubMenu.builder()
                        .label(opcion.getNombre())
                        .build();

                List<SiOpcionVo> opcionesHijos = obtenerOpcionesHijoMenu(opcion.getId());
                for (SiOpcionVo opcionHijo : opcionesHijos) {
                    DefaultMenuItem item = DefaultMenuItem.builder()
                            .value(opcionHijo.getNombre())
                            .icon("pi pi-bars")
                            .ajax(false)
                            .command(opcionHijo.getPagina())
                            .update("messages")
                            .build();
                    firstSubmenu.getElements().add(item);
                }
                modelo.getElements().add(firstSubmenu);
            }
        }
    }

    private List<SiOpcionVo> obtenerOpcionesPrincipalesMenu() {
        return siOpcionImpl.getAllSiOpcionBySiModulo(Constantes.MODULO_INVENTARIOS, sessionBean.getUser().getId(), Constantes.UNO);
    }

    private List<SiOpcionVo> obtenerOpcionesHijoMenu(Integer id) {
        return siOpcionImpl.getChildSiOpcion(id, sessionBean.getUser().getId(), Constantes.MODULO_INVENTARIOS);
    }

}
