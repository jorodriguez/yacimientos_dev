/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.ayuda.bean.model;

import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import sia.constantes.Constantes;
import sia.servicios.sistema.impl.SiAyudaAdjuntoImpl;
import sia.servicios.sistema.impl.SiAyudaImpl;
import sia.servicios.sistema.impl.SiModuloImpl;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.servicios.sistema.vo.SiAyudaAdjuntoVo;
import sia.servicios.sistema.vo.SiAyudaVo;
import sia.servicios.sistema.vo.SiModuloVo;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.sgl.sistema.bean.support.ConversationsManager;

/**
 *
 * @author sluis
 */
@Named
@ConversationScoped
public class ArbolAyudasBean implements Serializable {

    @Inject
    private Conversation conversacion;
    @Inject
    private ConversationsManager conversationsManager;
    @Inject
    private SiAyudaImpl siAyudaImpl;
    @Inject
    private SiModuloImpl siModuloImpl;
    @Inject
    private SiOpcionImpl siOpcionImpl;
    @Inject
    private SiAyudaAdjuntoImpl siAyudaAdjuntoImpl;
    private List<SiAyudaVo> ayudasList;
    private List<SiModuloVo> modulosList;
    private List<SiOpcionVo> opcionesList;
    private DefaultTreeModel arbolAyudas;

    public ArbolAyudasBean() {
    }

    @Inject
    public ArbolAyudasBean(Conversation conversacion, ConversationsManager conversationsManager, SiModuloImpl siModuloImpl,
            SiOpcionImpl siOpcionImpl, SiAyudaImpl siAyudaImpl, SiAyudaAdjuntoImpl siAyudaAdjuntoImpl) {
        this.conversacion = conversacion;
        this.conversationsManager = conversationsManager;

        this.conversationsManager.finalizeConversation(Constantes.CONVERSACION_ARBOL_AYUDAS); //Terminando todas las conversaciones abiertas hasta este momento
        //Iniciando la conversación
        this.conversationsManager.beginConversation(conversacion, Constantes.CONVERSACION_ARBOL_AYUDAS);
        this.siModuloImpl = siModuloImpl;
        this.siOpcionImpl = siOpcionImpl;
        this.siAyudaImpl = siAyudaImpl;
        this.siAyudaAdjuntoImpl = siAyudaAdjuntoImpl;

        //Borrando el arbol anterior
        this.arbolAyudas = null;
        this.arbolAyudas = construirArbol();
    }

    public DefaultTreeModel construirArbol() {
        return generarArbol();
    }

    public DefaultTreeModel generarArbol() {
        //Trayendo contenido
        if (modulosList == null) {
            this.modulosList = siModuloImpl.getAllSiModuloList("nombre", true, false);
        }
        if (opcionesList == null) {
            this.opcionesList = siOpcionImpl.getAllSiOpcion("nombre", true, false);
        }
        if (ayudasList == null) {
            this.ayudasList = siAyudaImpl.getAllSiAyuda("nombre", true, false);
        }

        // Configurando el nodo raiz
        DefaultMutableTreeNode nodoRaiz = agregarNodo(null, "Ayuda SIA", "", null, null, false);
        DefaultTreeModel arbolReturn = new DefaultTreeModel(nodoRaiz);

        // Agregando carpetas y elementos
        DefaultMutableTreeNode nodoHojaModulos = null;
        DefaultMutableTreeNode nodoHojaOpciones = null;
        DefaultMutableTreeNode nodoHojaAyudas = null;

        for (SiModuloVo m : modulosList) {
            if (m.getId() != 8) {
                nodoHojaModulos = agregarNodo(nodoRaiz, "Módulo " + m.getNombre(), null, null, null, false);
                for (SiOpcionVo o : opcionesList) {
                    if (m.getId() == o.getIdSiModulo()) {
                        nodoHojaOpciones = agregarNodo(nodoHojaModulos, "Opción " + o.getNombre(), null, null, null, false);
                        for (SiAyudaVo a : ayudasList) {
                            if (m.getId() == a.getIdSiModulo() && o.getId() == a.getIdSiOpcion()) {
                                nodoHojaAyudas = agregarNodo(nodoHojaOpciones, a.getNombre(), null, null, null, false);
                                //archivos pertenecientes a esta ayuda
                                List<SiAyudaAdjuntoVo> archivos = siAyudaAdjuntoImpl.getAllSiAdjuntoBySiAyuda(a.getId());
                                for (SiAyudaAdjuntoVo vo : archivos) { //aqui van a ir los archivos de la ayuda
                                    if (vo.getTipoArchivo().equals("application/pdf")) {
                                        agregarNodo(nodoHojaAyudas, "Documento", vo.getUrl(), vo.getIdSiAjunto(), vo.getUuidSiAjunto(), true);
                                    }
                                    if (vo.getTipoArchivo().equals("video/x-ms-wmv")) {
                                        agregarNodo(nodoHojaAyudas, "Vídeo", vo.getUrl(), vo.getIdSiAjunto(), null, true);
                                    }
                                }
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
//        userObject.setLeaf(isHoja);
//        userObject.setText(texto);
//        userObject.setUrl(url);
//        userObject.setIdArchivo(idArchivo);
//        userObject.setUuid(uuid);
//        userObject.setTooltip(texto);
//        //Expandimos si no es hoja
//        if (!isHoja) {
//            userObject.setExpanded(false); //Para que el nodo salga expandido o desplegado
//        }
//        if (texto.equals("Ayuda SIA")) {
//            userObject.setExpanded(true);
//        }
//
//        // Asignando iconos
//        if (texto.equals("Documento")) {
//            userObject.setLeafIcon(Constantes.ICON_PDF);
//        } else if (texto.equals("Vídeo")) {
//            userObject.setLeafIcon(Constantes.ICON_VIDEO_WMV);
//        } else {
//            userObject.setLeafIcon(Constantes.LEAF_ICON);
//        }
//        userObject.setBranchContractedIcon(Constantes.EXPANDED_ICON);
//        userObject.setBranchExpandedIcon(Constantes.CONTRACTED_ICON);

        //Personalizando el nodo
        nodo.setUserObject(userObject);
        nodo.setAllowsChildren(!isHoja);

        //Agregar nuestro nodo ya completo a su padre
        if (padre != null) {
            padre.add(nodo);
        }
        return nodo;
    }

    public String goToMenuAyuda() {
        return "/vistas/administracion/ayuda/menuAyuda";
    }

    public DefaultTreeModel getArbolAyudas() {
        return arbolAyudas;
    }

    public void setArbolAyudas(DefaultTreeModel arbolAyudas) {
        this.arbolAyudas = arbolAyudas;
    }
}