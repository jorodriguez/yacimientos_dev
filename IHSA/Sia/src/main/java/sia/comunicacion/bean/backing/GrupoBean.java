/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.comunicacion.bean.backing;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.catalogos.bean.model.UsuarioListModel;
import sia.comunicacion.bean.model.GrupoListModel;
import sia.modelo.CoGrupo;
import sia.modelo.CoMiembro;
import sia.modelo.Usuario;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.sistema.bean.backing.PanelPopup;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author hacosta
 */
@Named(value = "grupoBean")
@ViewScoped
public class GrupoBean implements Serializable {
    //-- Managed Beans ----
    @Inject
    private Sesion sesion;
    @Inject
    private GrupoListModel grupoListModel;
    @Inject
    private PanelPopup panelPopup;
    @Inject
    UsuarioImpl usuarioImpl;
    //--------------
    private String operacion;
    private DataModel grupos;
    private DataModel miembros;
    private CoGrupo grupo;
    private String usuario;

    /** Creates a new instance of GrupoBean */
    public GrupoBean() {
    }

    public DataModel getGrupos() {
        try {
            if (sesion.getUsuario() != null) {
                return this.grupos = new ListDataModel(this.grupoListModel.getGrupos(this.sesion.getUsuario().getId()));
            } else {
                return this.grupos;
            }
        } catch (Exception ex) {
            Logger.getLogger(GrupoBean.class.getName()).log(Level.SEVERE, null, ex);
            return this.grupos;
        }
    }

    public void traeMiembros() {
        this.grupo = (CoGrupo) grupos.getRowData();
        this.miembros = new ListDataModel(this.grupoListModel.getMiembros(getGrupo().getId()));
    }

    public void nuevoGrupo() {
        this.grupo = new CoGrupo();
        this.miembros = null;
        this.operacion = Sesion.CREATE_OPERATION;
        this.panelPopup.tmGrupo();
    }

    public void actualizarGrupo() {
        this.grupo = (CoGrupo) grupos.getRowData();
//        this.miembros = null;
        this.miembros = new ListDataModel(this.grupoListModel.getMiembros(getGrupo().getId()));
        this.operacion = Sesion.UPDATE_OPERATION;
        this.panelPopup.tmGrupo();
    }

    public void actualizarGrupoII() {
        if (this.grupo != null) {
            this.operacion = Sesion.UPDATE_OPERATION;
            this.panelPopup.tmGrupo();
        }
    }

    public <T> List<T> getDataModelAsList() {
        return (List<T>) getGrupos().getWrappedData();
    }

//    public void filterUserItems(String userNameStartsWith) {
//        this.selectItems = new ArrayList<SelectItem>();
//        for (CoGrupo g : ) {
//            boolean addUser = false;
//            if (userNameStartsWith == null) {
//                addUser = true;
//            } else {
//                if (usuario.getNombre().toLowerCase().startsWith(userNameStartsWith.toLowerCase())) {
//                    addUser = true;
//                }
//            }
//            if (addUser) {
//                SelectItem selectItem = new SelectItem(usuario, usuario.getNombre());
//                selectItems.add(selectItem);
//            }
//        }
//    }
    public void guardarGrupo() {
        if (this.grupo.getNombre().equals("")) {
            FacesUtils.addInfoMessage("Por favor especifica un nombre...");
        } else {
            if (this.operacion.equals(Sesion.CREATE_OPERATION)) {
                try {
                    CoGrupo g = this.grupoListModel.getGrupoPorNombre(this.grupo.getNombre(), this.sesion.getUsuario().getId());
                    // diferente de null = encontro un grupo con el mismo nombre y administrador
                    if (g != null) {
                        // si eliminado es verdadero 
                        if (g.isEliminado()) {
                            // solo activar el grupo
                            g.setEliminado(false);
                            g.setFechaGenero(new Date());
                            g.setHoraGenero(new Date());
                            this.grupoListModel.actualizarGrupo(g);
                            this.panelPopup.tmGrupo();
                        } else {
                            FacesUtils.addInfoMessage("Ya tienes un grupo con el nombre " + this.grupo.getNombre());
                        }
                    } else {
                        this.grupo.setAdministrador(this.sesion.getUsuario());
                        this.grupo.setEliminado(false);
                        this.grupo.setFechaGenero(new Date());
                        this.grupo.setHoraGenero(new Date());
                        this.grupoListModel.crearGrupo(this.grupo);
                        this.panelPopup.tmGrupo();
                    }

                } catch (Exception e) {
                    Logger.getLogger(GrupoBean.class.getName()).log(Level.SEVERE, null, e);
                    FacesUtils.addInfoMessage("No se pudo crear el elemento...");
                    this.panelPopup.tmGrupo();
                }
            } else {
                try {
                    this.grupoListModel.actualizarGrupo(grupo);
                    this.panelPopup.tmGrupo();
                } catch (Exception e) {
                    Logger.getLogger(GrupoBean.class.getName()).log(Level.SEVERE, null, e);
                    FacesUtils.addInfoMessage("No se pudo actualizar el elemento...");
                    this.panelPopup.tmGrupo();
                }
            }
        }

    }

    public void eliminarGrupo() {
        try {
            this.grupo = (CoGrupo) grupos.getRowData();
//            this.miembros = null;
            this.miembros = new ListDataModel(this.grupoListModel.getMiembros(getGrupo().getId()));
            this.panelPopup.tmElimnarGrupo();
        } catch (Exception e) {
            Logger.getLogger(GrupoBean.class.getName()).log(Level.SEVERE, null, e);
            FacesUtils.addInfoMessage("No se pudo selecionar el elemento...");
        }
    }

    public void confirmarEliminarGrupo() {
        try {
            this.grupoListModel.eliminarGrupo(grupo);
            this.panelPopup.tmElimnarGrupo();
        } catch (Exception e) {
            Logger.getLogger(GrupoBean.class.getName()).log(Level.SEVERE, null, e);
            FacesUtils.addInfoMessage("No se pudo eliminar el elemento...");
        }
    }

    public DataModel getMiembros() {
        try {
            if (sesion.getUsuario() != null) {
                return this.miembros;
            } else {
                return this.miembros;
            }
        } catch (Exception ex) {
            Logger.getLogger(GrupoBean.class.getName()).log(Level.SEVERE, null, ex);
            return this.miembros;
        }
    }

    public int getTotalMiembros() {
        try {
            return this.grupoListModel.getTotalMiembros(((CoGrupo) grupos.getRowData()).getId());
        } catch (Exception e) {
            Logger.getLogger(GrupoBean.class.getName()).log(Level.SEVERE, null, e);
            return 0;
        }
    }

    public void traeMiembros(Integer idGrupo) {
        this.miembros = new ListDataModel(this.grupoListModel.getMiembros(idGrupo));
    }

    public void nuevoMiembro() {
        try {
            if (!this.usuario.equals("")) {
                if (this.grupo != null) {
                    CoMiembro miembro = this.grupoListModel.getMiembroPorNombre(this.usuario, this.grupo.getId());
                    if (miembro != null) {
                        miembro.setEliminado(false);
                        this.grupoListModel.actualizarMiembro(miembro);
                        this.usuario = "";
                        this.traeMiembros(this.grupo.getId());
                    } else {
                        Usuario userTemp =  usuarioImpl.buscarPorNombre(this.usuario);
                        if (userTemp != null) {
                            miembro = new CoMiembro();
                            miembro.setCoGrupo(this.grupo);
                            miembro.setMiembro(userTemp);
                            miembro.setEliminado(false);
                            miembro.setFechaAgrego(new Date());
                            miembro.setHoraAgrego(new Date());
                            this.grupoListModel.agregarMiembro(miembro);
                            this.usuario = "";
                            this.traeMiembros(this.grupo.getId());
                        } else {
                            FacesUtils.addInfoMessage("El nombre introducido no es válido...");
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(GrupoBean.class.getName()).log(Level.SEVERE, null, e);
            this.usuario = "";
        }
    }

    public void eliminarMiembro() {
        try {
            CoMiembro miembro = (CoMiembro) miembros.getRowData();
            miembro.setEliminado(true);
            this.grupoListModel.actualizarMiembro(miembro);
            this.usuario = "";
            this.traeMiembros(this.grupo.getId());
        } catch (Exception e) {
            Logger.getLogger(GrupoBean.class.getName()).log(Level.SEVERE, null, e);
            this.usuario = "";
        }
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @param grupoListModel the grupoListModel to set
     */
    public void setGrupoListModel(GrupoListModel grupoListModel) {
        this.grupoListModel = grupoListModel;
    }

    /**
     * @param panelPopup the panelPopup to set
     */
    public void setPanelPopup(PanelPopup panelPopup) {
        this.panelPopup = panelPopup;
    }

    /**
     * @return the grupo
     */
    public CoGrupo getGrupo() {
        return grupo;
    }

    /**
     * @return the operacion
     */
    public String getOperacion() {
        return operacion;
    }

    /**
     * @param operacion the operacion to set
     */
    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    /**
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
