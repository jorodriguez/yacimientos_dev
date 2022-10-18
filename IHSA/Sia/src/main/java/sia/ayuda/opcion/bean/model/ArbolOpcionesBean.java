/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ayuda.opcion.bean.model;

import java.io.Serializable;
import java.util.*;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import sia.ayuda.bean.model.MyUserObject;
import sia.constantes.Constantes;
import sia.modelo.SiModulo;
import sia.modelo.SiRelRolOpcion;
import sia.modelo.SiRol;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.servicios.sistema.impl.SiModuloImpl;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.servicios.sistema.impl.SiRelRolOpcionImpl;
import sia.servicios.sistema.impl.SiRolImpl;

/**
 *
 * @author jevazquez
 */
@Named
@ViewScoped
public class ArbolOpcionesBean implements Serializable {


    @Inject
    private SiModuloImpl siModuloImpl;
    @Inject
    private SiOpcionImpl siOpcionImpl;
    @Inject
    private SiRolImpl siRolImpl;
    @Inject
    private SiRelRolOpcionImpl siRelRolOpImpl;
    private List<SiModulo> modulosList;
    private List<SiOpcionVo> opcionesList;
    private List<SiRol> rolList;
    private List<SiRelRolOpcion> relRolOpcionList;
    private List<SiOpcionVo> opcionesHijasList;
    private DefaultTreeModel arbolOpciones;
    private DataModel<SiOpcionVo> tabla = null;
    private String moduloSelecionado;
    private String opcionSeleccionada;
    private int opcionSiOpcion = 0;
    private DefaultMutableTreeNode nodoRaiz = agregarNodo(null, "Modulos", "", 0, null, false);
    private DefaultMutableTreeNode nodoHojaModulos = null;
    private DefaultMutableTreeNode nodoHojaOpciones = null;
    private DefaultMutableTreeNode nodoHojaOpcionesHijas = null;

    public ArbolOpcionesBean() {
    }


    public DefaultTreeModel construirArbol() {
        return generarArbol();
    }

    public DefaultTreeModel generarArbol() {
        //Trayendo contenido
        if (modulosList == null) {
            this.modulosList = siModuloImpl.getAllModulosByEstado(Constantes.NO_ELIMINADO);
        } else {
            if (modulosList.size() != siModuloImpl.getAllModulosByEstado(Constantes.NO_ELIMINADO).size()) {
                this.modulosList = siModuloImpl.getAllModulosByEstado(Constantes.NO_ELIMINADO);
            }
        }

        if (opcionesList == null) {
            this.opcionesList = siOpcionImpl.getSiOpcionByRol();
        } else {
            if (opcionesList.size() != siOpcionImpl.getSiOpcionByRol().size()) {
                this.opcionesList = siOpcionImpl.getSiOpcionByRol();
            }
        }

        if (opcionesHijasList == null) {
            this.opcionesHijasList = siOpcionImpl.getSiOpcionBySiOpcion();
        } else {
            if (opcionesHijasList.size() != siOpcionImpl.getSiOpcionBySiOpcion().size()) {
                this.opcionesHijasList = siOpcionImpl.getSiOpcionBySiOpcion();
            }
        }

        System.out.println("modulosList.size(): " + this.modulosList.size());
        System.out.println("opcionesList.size(): " + this.opcionesList.size());

        // Configurando el nodo raiz
        DefaultTreeModel arbolReturn = new DefaultTreeModel(nodoRaiz);

        // Agregando carpetas y elementos
        for (SiModulo m : modulosList) {
            if (m.getId() != 0) {
                nodoHojaModulos = agregarNodo(nodoRaiz, m.getNombre(), null, 1, null, false);
                for (SiOpcionVo o : opcionesList) {
                    if (o.getModulo().equals(m.getNombre())) {
                        nodoHojaOpciones = agregarNodo(nodoHojaModulos, o.getNombre(), null, 2, null, false);
                        for (SiOpcionVo h : opcionesHijasList) {
                            if (o.getId().equals(h.getIdPadre())) {
                                nodoHojaOpcionesHijas = agregarNodo(nodoHojaOpciones, h.getNombre(), null, 3, null, true);
                            }
                        }
                    }
                }
            }
        }

        return arbolReturn;
    }

    public static DefaultMutableTreeNode agregarNodo(DefaultMutableTreeNode padre, String texto, String url, Integer idArchivo, String uuid, boolean isHoja) {
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode();
        MyUserObject userObject = new MyUserObject(nodo);

        //Personalizando el contenido del nodo
        //userObject.setLeaf(isHoja);
        //userObject.setText(texto);
        userObject.setUrl(url);
        userObject.setIdArchivo(idArchivo);
        //userObject.setTooltip(texto);
        userObject.setUuidArchivo(uuid);
        //Expandimos si no es hoja
        if (!isHoja) {
            // userObject.setExpanded(false); //Para que el nodo salga expandido o desplegado
        }
        if (texto.equals("Modulos")) {
            //    userObject.setExpanded(true);
        }

        // Asignando iconos
        if (texto.equals("Documento")) {
            //  userObject.setLeafIcon(Constantes.ICON_PDF);
        } else if (texto.equals("Vídeo")) {
            //  userObject.setLeafIcon(Constantes.ICON_VIDEO_WMV);
        } else {
            // userObject.setLeafIcon(Constantes.LEAF_ICON);
        }
        // userObject.setBranchContractedIcon(Constantes.EXPANDED_ICON);
        //  userObject.setBranchExpandedIcon(Constantes.CONTRACTED_ICON);

        //Personalizando el nodo
        nodo.setUserObject(userObject);
        nodo.setAllowsChildren(!isHoja);

        //Agregar nuestro nodo ya completo a su padre
        if (padre != null) {
            padre.add(nodo);
        }
        return nodo;
    }

    public DefaultTreeModel getArbolOpciones() {
        return arbolOpciones;
    }

    public void setArbolOpciones(DefaultTreeModel arbolOpciones) {
        this.arbolOpciones = arbolOpciones;
    }

    public void recorrerArbol(String text, int id) {
        System.out.println("si entro");
        if (this.getArbolOpciones() != null) {
            for (int i = 0; i < getArbolOpciones().getChildCount(getArbolOpciones().getRoot()); i++) {
                if (id == 1) {

                    if (text.equals(getArbolOpciones().getChild(getArbolOpciones().getRoot(), i).toString())) {
                        setModSelecionado(text);
                        setOpcionSeleccionada("");
                        System.out.println("consulta por modulo especifico  " + getArbolOpciones().getChild(getArbolOpciones().getRoot(), i));
                        break;
                    }
                } else {
                    for (int j = 0; j < getArbolOpciones().getChildCount(getArbolOpciones().getChild(getArbolOpciones().getRoot(), i)); j++) {
                        if (id == 2) {
                            if (text.equals(getArbolOpciones().getChild(getArbolOpciones().getChild(getArbolOpciones().getRoot(), i), j).toString())) {
                                setModSelecionado(getArbolOpciones().getChild(getArbolOpciones().getRoot(), i).toString());
                                setOpcionSeleccionada(text);
                                //       setTabla((DataModel)new ListDataModel(siOpcionImpl.getSiOpcionBySiOpcion(siOpcionImpl.findOpcionByNameAndModulo(text, getArbolOpciones().getChild(getArbolOpciones().getRoot(), i).toString(), "False","0").getId().toString()))); System.out.println( "--------->"+      getTabla().getRowCount());
                                System.out.println("consulta por opcion " + getArbolOpciones().getChild(getArbolOpciones().getChild(getArbolOpciones().getRoot(), i), j));
                                break;
                            }
                        }
                    }
                }

            }
        }

    }

    public DataModel filtrarTabla() {
        if (this.getTabla() == null) {
            setTabla((DataModel) new ListDataModel());
        }
        System.out.println("pruebaTabla " + this.tabla.getRowCount() + "  " + getModSelecionado());
        return getTabla();
    }

    /**
     * @return the tabla
     */
    public DataModel<SiOpcionVo> getTabla() {
        return tabla;
    }

    /**
     * @param tabla the tabla to set
     */
    public void setTabla(DataModel<SiOpcionVo> tabla) {
        this.tabla = tabla;
    }

    /**
     * @return the moduloSelecionado
     */
    public String getModSelecionado() {
        System.out.println("si paso a que hora lo llaman");
        return moduloSelecionado;
    }

    /**
     * @param moduloSelecionado the moduloSelecionado to set
     */
    public void setModSelecionado(String ModSelecionado) {
        this.moduloSelecionado = ModSelecionado;
    }

    /**
     * @return the opcionSiOpcion
     */
    public int getOpcionSiOpcion() {
        return opcionSiOpcion;
    }

    /**
     * @param opcionSiOpcion the opcionSiOpcion to set
     */
    public void setOpcionSiOpcion(int opcionSiOpcion) {
        this.opcionSiOpcion = opcionSiOpcion;
    }

    /**
     * @return the opcionSeleccionada
     */
    public String getOpcionSeleccionada() {
        return opcionSeleccionada;
    }

    /**
     * @param opcionSeleccionada the opcionSeleccionada to set
     */
    public void setOpcionSeleccionada(String opcionSelecionada) {
        this.opcionSeleccionada = opcionSelecionada;
    }

   
}
