/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sistema.bean.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.catalogos.bean.model.UsuarioListModel;
import sia.comunicacion.bean.backing.NoticiaBean;
import sia.comunicacion.bean.model.ComparteConListModel;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.ComparteCon;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@Named(value = "soporteSistemaBean")
@ViewScoped
public class SoporteSistemaBean implements Serializable {
    // -- Managed beans 

    @Inject
    private ComparteConListModel comparteConListModel;
    @Inject
    private NoticiaBean noticiaBean;
    @Inject
    private Sesion sesion;
    @Inject
    UsuarioImpl usuarioImpl;
    //--
    private List<SelectItem> selectItems;
    private List<Usuario> usuarios;
    //--- para compartir
    private List<ComparteCon> listaElementos;
    private List<ComparteCon> listaGrupos;
    private List<SelectItem> matchesList;

    /**
     * Creates a new instance of SoporteSistema
     */
    public SoporteSistemaBean() {
    }

    /**
     * @return the usuarios
     */
    private List<Usuario> getUsuarios() {
        if (this.usuarios == null) {
            this.usuarios = usuarioImpl.getActivos();
        }
        return this.usuarios;
    }

    public List<SelectItem> getUserItems() {
        if (this.selectItems == null) {
            this.selectItems = new ArrayList<SelectItem>();
            for (Usuario usuario : this.getUsuarios()) {
                SelectItem selectItem = new SelectItem(usuario, usuario.getNombre());
                selectItems.add(selectItem);
            }
        }
        return this.selectItems;
    }

    public void userListener(String texto) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIViewRoot uiViewRoot = facesContext.getViewRoot();
        String userNameStartsWith = texto;
        if (!userNameStartsWith.equals("")) {
            filterUserItems(userNameStartsWith);
            Usuario user = getUserByName(userNameStartsWith);
            if (user != null) {
                UIInput userInputText = (UIInput) uiViewRoot.findComponent("editarGrupo:usuario");
                userInputText.setSubmittedValue(user.getNombre());
                userInputText.setValue(user.getNombre());
            }
        }
    }

    private void filterUserItems(String userNameStartsWith) {
        this.selectItems = new ArrayList<SelectItem>();
        for (Usuario usuario : this.getUsuarios()) {
            boolean addUser = false;
            if (userNameStartsWith == null) {
                addUser = true;
            } else {
                if (usuario.getNombre().toLowerCase().startsWith(userNameStartsWith.toLowerCase())) {
                    addUser = true;
                }
            }
            if (addUser) {
                SelectItem selectItem = new SelectItem(usuario, usuario.getNombre());
                selectItems.add(selectItem);
            }
        }
    }

    private Usuario getUserByName(String userName) {
        for (Usuario usuario : this.getUsuarios()) {
            if (usuario.getNombre().equals(userName)) {
                return usuario;
            }
        }
        return null;
    }

    /*
     * Para la opción de compartir 
     * 
     * 
     */
    private List<ComparteCon> getElementos() {
        if (this.listaElementos == null) {
            this.listaElementos = this.comparteConListModel.getElementos(this.sesion.getUsuario().getId());
            UtilLog4j.log.info(this, "Fue a la Db por elementos...");
        }
        return this.listaElementos;
    }

    private List<ComparteCon> getGrupos() {
        if (this.listaGrupos == null) {
            this.listaGrupos = this.comparteConListModel.getGrupos(this.sesion.getUsuario().getId());
            UtilLog4j.log.info(this, "Fue a la Db por grupos y privacidad...");
        }
        return this.listaGrupos;
    }

    public List<SelectItem> getMatchesList() {
        if (this.matchesList == null) {
            this.matchesList = new ArrayList<SelectItem>();
            for (ComparteCon elemento : this.getElementos()) {
                SelectItem selectItem = new SelectItem(elemento, elemento.getNombre());
                matchesList.add(selectItem);
            }
        }
        return this.matchesList;
    }

    public void quitarElemento() {
        ComparteCon e = (ComparteCon) this.noticiaBean.getComparteCon().getRowData();
        List<ComparteCon> lista = (List<ComparteCon>) this.noticiaBean.getComparteCon().getWrappedData();
        //quita de la lista comparteCon 
        this.quitar(lista, e);
        // regresa a la lista principal el elemento
        this.listaElementos.add(e);
        // si es grupo o privasidad lo agrega a esa lista
        if (!e.getTipo().equals("Usuario")) {
            this.listaGrupos.add(e);
        }
        // convertir la lista a DataModel
        this.noticiaBean.setComparteCon(new ListDataModel(lista));
    }

    private void quitar(List<ComparteCon> lista, ComparteCon elemento) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getNombre().equals(elemento.getNombre())) {
                lista.remove(i);
            }
        }
    }

    public void elementoListener(ValueChangeEvent valueChangeEvent) {
        if (!valueChangeEvent.getNewValue().toString().equals("")) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            UIViewRoot uiViewRoot = facesContext.getViewRoot();
            String elementNameStartsWith = valueChangeEvent.getNewValue().toString();
            if (!elementNameStartsWith.equals("")) {
                filterElementosItem(elementNameStartsWith);
                ComparteCon elemento = getElementByName(elementNameStartsWith);
                if (elemento != null) {
                    //UIInput elementInputText = (UIInput) uiViewRoot.findComponent("compartir:elemento");
                    UIInput elementInputText = (UIInput) uiViewRoot.findComponent("noticiaForm:elemento");
//                    elementInputText.setSubmittedValue(elemento.getNombre());
//                    elementInputText.setValue(elemento.getNombre());
                    elementInputText.setSubmittedValue("");
                    elementInputText.setValue("");
                    // agregarlo a la lista para compartir 
                    List<ComparteCon> lista;
                    if (this.noticiaBean.getComparteCon() != null) {
                        lista = (List<ComparteCon>) this.noticiaBean.getComparteCon().getWrappedData();
                    } else {
                        lista = new ArrayList<ComparteCon>();
                    }
                    lista.add(elemento);
                    // convertir la lista a DataModel
                    this.noticiaBean.setComparteCon(new ListDataModel(lista));
//                     quitarlo de la lista que se muestra
                    this.listaElementos.remove(elemento);
                    //mostrar nuevos elementos
                    this.matchesList.clear();
                    for (int i = 0; i < this.getGrupos().size(); i++) {
                        if (listaGrupos.get(i).getNombre().equals(elemento.getNombre())) {
                            listaGrupos.remove(i);
                        } else {
                            SelectItem selectItem = new SelectItem(listaGrupos.get(i), listaGrupos.get(i).getNombre());
                            matchesList.add(selectItem);
                        }
                    }
                }
            } else {
                this.matchesList.clear();
                for (ComparteCon grupo : this.getGrupos()) {
                    SelectItem selectItem = new SelectItem(grupo, grupo.getNombre());
                    matchesList.add(selectItem);
                }
            }
        } else {
            this.matchesList.clear();
            for (ComparteCon grupo : this.getGrupos()) {
                SelectItem selectItem = new SelectItem(grupo, grupo.getNombre());
                matchesList.add(selectItem);
            }
        }
    }

    private void filterElementosItem(String elementNameStartsWith) {
        this.matchesList = new ArrayList<SelectItem>();
        for (ComparteCon elemento : this.getElementos()) {
            boolean addElement = false;
            if (elementNameStartsWith == null) {
                addElement = true;
            } else {
                if (elemento.getNombre().toLowerCase().startsWith(elementNameStartsWith.toLowerCase())) {
                    addElement = true;
                }
            }
            if (addElement) {
                SelectItem selectItem = new SelectItem(elemento, elemento.getNombre());
                matchesList.add(selectItem);
            }
        }
    }

    private ComparteCon getElementByName(String elementName) {
        for (ComparteCon elemento : this.getElementos()) {
            if (elemento.getNombre().equals(elementName)) {
                return elemento;
            }
        }
        return null;
    }


}
