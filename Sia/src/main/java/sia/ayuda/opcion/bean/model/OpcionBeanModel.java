/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ayuda.opcion.bean.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import sia.ayuda.bean.model.MyUserObject;
import sia.constantes.Constantes;
import sia.modelo.SiModulo;
import sia.modelo.SiOpcion;
import sia.modelo.SiRol;
import sia.modelo.rol.vo.RolVO;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import javax.enterprise.context.Conversation;
import sia.servicios.sistema.impl.SiModuloImpl;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.servicios.sistema.impl.SiRelRolOpcionImpl;
import sia.servicios.sistema.impl.SiRolImpl;
import sia.sistema.bean.support.ConversationsManager;
import sia.util.UtilLog4j;

/**
 *
 * @author sluis
 */
@ManagedBean
@CustomScoped(value = "#{window}")
public class OpcionBeanModel implements Serializable {
    
    @Inject
    private Conversation conversation;
    @Inject
    private ConversationsManager conversationsManager;   
    @EJB
    private SiOpcionImpl opcionService;
    @EJB
    private SiModuloImpl moduloService;
    //JVG
    @EJB
    private SiRolImpl rolService;
    @EJB
    private SiRelRolOpcionImpl relRolOpcionService;
    private List<SiOpcion> opcionesListFiltradasByModulo = null;
    private DataModel<SiOpcion> opcionesFiltradasByModuloDataModel = null;
    private DataModel<SiOpcionVo> opcionesFiltradas=null;
    private Sesion sesion;
    private String nombreOpcion;
    private String paginaOpcion;
    private String estatusContar="0";
    private SiOpcion opcionSeleccionada;
    private String nombreModuloSeleccionado;
    private String nombreRolSeleccionado;//JVG
    private String nombreOpcionSeleccionada;
    private String nombreSiOpcionSeleccionada;
    private boolean modalRenderedCrearOpcion = false;
    private boolean mrPopupCrearModulo = false;
      private boolean mrPopupCrearRol = false;
    private List<SiModulo> modulosList;
    private List<SiOpcionVo> opcionesList;
    private  List<RolVO> rolList;
    private DefaultTreeModel arbolModulos;
    private boolean selecionada=false;
    private boolean disableSiOpcion=true;
    private boolean disableSiRol=true;
    private DefaultTreeModel arbolOpciones;
    private  List<SiOpcionVo> opcionesHijasList;

    public  OpcionBeanModel() {        
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        UtilLog4j.log.info(this, "idUsuario: " + sesion.getUsuario().getId());
        //Reiniciando variables por si acaso
        this.opcionesListFiltradasByModulo = null;
        this.opcionesFiltradasByModuloDataModel = null;
        this.opcionSeleccionada = null;
        this.nombreOpcion = null;
        this.paginaOpcion = null;
        this.nombreModuloSeleccionado = null;
    }
    
     public void getAllOpcionesActivasByModulo( ) {
         //out.println("Cargando tabla por modulo");
         if (this.getNombreModuloSeleccionado()!=null && !this.getNombreModuloSeleccionado().equals("") ){
                  if(this.opcionesFiltradas == null) {
            this.opcionesFiltradas = (DataModel)new ListDataModel(opcionService.getSiOpcionBySiOpcionPadre(moduloService.findModuloByName(this.getNombreModuloSeleccionado(), Constantes.NO_ELIMINADO).getId()));
        }
         }
     }
    public void getAllOpcionesActivasBySiOpcion() {
         System.out.println("Cargando tabla por SiOpcion");
         if (this.getNombreOpcionSeleccionada()!=null && !this.getNombreOpcionSeleccionada().equals("") ){
                  if(this.opcionesFiltradas == null) {
            this.opcionesFiltradas = (DataModel)new ListDataModel(opcionService.getSiOpcionBySiOpcion(opcionService.findOpcionByNameAndModulo(this.getNombreOpcionSeleccionada(), this.getNombreModuloSeleccionado(), Constantes.NO_ELIMINADO, "0").getId().toString()));
        }
         }
     }
     public void reloadAllOpcionesByModulo() { 
        if(this.getNombreModuloSeleccionado() !=null && !this.getNombreModuloSeleccionado().equals("")){
         this.opcionesFiltradas = (DataModel)new ListDataModel(opcionService.getSiOpcionBySiOpcionPadre(moduloService.findModuloByName(this.getNombreModuloSeleccionado(), Constantes.NO_ELIMINADO).getId())); 
    }
    }
     
    public void reloadAllopcionBySiOpcion(){
         if(this.getNombreOpcionSeleccionada()!=null &!this.getNombreOpcionSeleccionada().equals("")){
      this.opcionesFiltradas = (DataModel)new ListDataModel(opcionService.getSiOpcionBySiOpcion(opcionService.findOpcionByNameAndModulo(this.getNombreOpcionSeleccionada(), this.getNombreModuloSeleccionado(), Constantes.NO_ELIMINADO, "0").getId().toString()));
     }
     }
    
    /**
     * @return the opcionesFiltradasByModuloDataModel
     */
   /* public DataModel<SiOpcion> getOpcionesFiltradasByModuloDataModel() {
        return this.opcionesFiltradasByModuloDataModel;
    }*/
    
      public DataModel<SiOpcionVo> getOpcionesFiltradas() {
        
         return this.opcionesFiltradas;
    }
    
    /* public List<SiModulo> getAllModulosActivos() {
        return moduloService.getAllModulosByEstado(Constantes.NO_ELIMINADO);
    }*/
    
    public List<SiOpcionVo> getAllOpciones() {
         System.out.println("----------"+this.moduloService.findModuloByName(getNombreModuloSeleccionado(), Constantes.NO_ELIMINADO).getId());
        return opcionService.getSiOpcionBySiOpcionPadre(this.moduloService.findModuloByName(getNombreModuloSeleccionado(), Constantes.NO_ELIMINADO).getId());
    }
    
      public List<RolVO> getAllRolesByModuloActivos() { 
          return  rolService.traerRol(moduloService.findModuloByName(this.getNombreModuloSeleccionado(), Constantes.NO_ELIMINADO).getId());
   }
    
       public List<SiRol> getAllRoles() { 
          return  rolService.findAll();
   }
       
    public void crearOpcion() throws Exception{
        this.opcionSeleccionada= this.opcionService.findOpcionByNameAndModulo(this.getNombreSiOpcionSeleccionada(),this.getNombreModuloSeleccionado(),Constantes.NO_ELIMINADO,String.valueOf(Constantes.CERO) );
        System.out.println("prueba insert "+this.getNombreOpcion()+","+ ","+ this.sesion.getUsuario().getId()+","+ this.getNombreModuloSeleccionado()+","+this.getEstatusContar()+",");
        if(this.isDisableSiOpcion()==true){
        if(this.getPaginaOpcion() !=null && !this.getPaginaOpcion().trim().equals("")){
        opcionService.crearOpcion(this.getNombreOpcion(), this.getPaginaOpcion(), this.sesion.getUsuario().getId(), Constantes.NO_ELIMINADO, this.getNombreModuloSeleccionado(),Integer.parseInt(this.getEstatusContar().trim()),opcionSeleccionada );
        }
         else {
             FacesUtils.addInfoMessage("La página no puede ser vacía");
        }
        
        } else{
            UtilLog4j.log.info(this, "Crear opcion(params): nombreOpcion: " + this.getNombreOpcion() + " paginaOpcion: " + this.getPaginaOpcion() + " nombreModulo: " + this.getNombreModuloSeleccionado() + " usuario: " + this.sesion.getUsuario().getId());
           opcionService.crearOpcion(this.getNombreOpcion(), "", this.sesion.getUsuario().getId(), Constantes.NO_ELIMINADO, this.getNombreModuloSeleccionado(),Integer.parseInt(this.getEstatusContar().trim()),null ); 
           
        }
        if(this.isDisableSiRol()==true){
                insertarRelacionRolOpcion();
        
     }
        // reloadAllOpciones();
        
     //   opcionService.crearOpcion(this.getNombreOpcion(), this.getPaginaOpcion(), this.sesion.getUsuario().getId(), Constantes.NO_ELIMINADO, this.getNombreModuloSeleccionado());
       
    }
    
    public void actualizarOpcion() throws Exception{
        System.out.println("--"+this.getOpcionSeleccionada()+ " <---->"+this.getOpcionSeleccionada().getPosicion().toString());
        if(this.getOpcionSeleccionada().getPosicion()>0){
            if (this.getPaginaOpcion() != null && !this.getPaginaOpcion().trim().equals("")) {
        opcionService.actualizarOpcion(this.getOpcionSeleccionada(), this.getNombreOpcion(), this.getPaginaOpcion(), this.sesion.getUsuario().getId());
     } else {
            FacesUtils.addInfoMessage("La página no puede ser vacía");
        }
        }
        else{
            opcionService.actualizarOpcion(this.getOpcionSeleccionada(), this.getNombreOpcion(), this.getPaginaOpcion(), this.sesion.getUsuario().getId());
        }
        System.out.println("-2-"+this.getOpcionSeleccionada()+ "<---->"+this.getOpcionSeleccionada().getPosicion().toString());
            }
    
    public void eliminarOpcion() throws Exception{
        opcionService.eliminarOpcion(this.getOpcionSeleccionada(), this.sesion.getUsuario().getId(), Constantes.ELIMINADO);
      //  reloadAllOpciones();
    }
    
    public void crearModulo() throws Exception {
        UtilLog4j.log.info(this, "OpcionBeanModel.crearModulo()");
        moduloService.crearModulo(this.nombreOpcion, this.paginaOpcion, sesion.getUsuario().getId(), Constantes.NO_ELIMINADO);
    }

    public void insertarRelacionRolOpcion(){
      
     SiOpcionVo opcion = new SiOpcionVo();
                opcion.setIdSiModulo(this.moduloService.findModuloByName(this.getNombreModuloSeleccionado(), Constantes.NO_ELIMINADO).getId());
                opcion.setNombre(this.getNombreOpcion());
                opcion.setPagina(this.getPaginaOpcion());
                opcion.setFechaGenero(new Date());
                opcion.setHoraGenero(new Date()); 
                opcion.setEliminado(Constantes.NO_ELIMINADO);
                opcion.setId(opcionService.findOpcionByNameAndModulo(this.getNombreOpcion(), this.getNombreModuloSeleccionado(), Constantes.NO_ELIMINADO,null).getId());

          relRolOpcionService.guardar(opcion, this.rolService.findRolByNombre(
                  this.moduloService.findModuloByName(getNombreModuloSeleccionado(), Constantes.NO_ELIMINADO), getNombreRolSeleccionado()).getId());
   }
    public void insertarRelacionRolOpcion(SiOpcionVo vo){


          relRolOpcionService.guardar(vo, this.rolService.findRolByNombre(null, getNombreRolSeleccionado()).getId());
   }
    /**
     * @return the nombreOpcion
     */
    public String getNombreOpcion() {
        return nombreOpcion;
    }

    /**
     * @param nombreOpcion the nombreOpcion to set
     */
    public void setNombreOpcion(String nombreOpcion) {
        this.nombreOpcion = nombreOpcion;
    }

    /**
     * @return the opcionSeleccionada
     */
    public SiOpcion getOpcionSeleccionada() {
        return opcionSeleccionada;
    }

    /**
     * @param opcionSeleccionada the opcionSeleccionada to set
     */
    public void setOpcionSeleccionada(SiOpcionVo opcionSeleccionadaVo) {
        if(opcionSeleccionada != null){
            UtilLog4j.log.info(this, "setOpcionSeleccionada() - nombre: " + opcionSeleccionada.getNombre() + " pagina: " + opcionSeleccionada.getPagina());
        }
        this.opcionSeleccionada = opcionService.find(opcionSeleccionadaVo.getId());
    }

    /**
     * @return the conversation
     */
//    public Conversation getConversation() {
//        return conversation;
//    }
//
//    /**
//     * @param conversation the conversation to set
//     */
//    public void setConversation(Conversation conversation) {
//        this.conversation = conversation;
//    }

    /**
     * @return the paginaOpcion
     */
    public String getPaginaOpcion() {
        return paginaOpcion;
    }
 
    /**
     * @param paginaOpcion the paginaOpcion to set
     */
    public void setPaginaOpcion(String paginaOpcion) {
        this.paginaOpcion = paginaOpcion;
    }
    
    /**
     * @return the nombreModuloSeleccionado
     */
    public String getNombreModuloSeleccionado() {
        return nombreModuloSeleccionado;
    }

    /**
     * @param nombreModuloSeleccionado the nombreModuloSeleccionado to set
     */
    public void setNombreModuloSeleccionado(String nombreModuloSeleccionado) {
        this.nombreModuloSeleccionado = nombreModuloSeleccionado;
    }

    /**
     * @param opcionesFiltradasByModuloDataModel the opcionesFiltradasByModuloDataModel to set
     */
  /*  public void setOpcionesFiltradasByModuloDataModel(DataModel<SiOpcion> opcionesFiltradasByModuloDataModel) {
        this.opcionesFiltradasByModuloDataModel = opcionesFiltradasByModuloDataModel;
    }*/

    /**
     * @return the opcionesListFiltradasByModulo
     */
   /* public List<SiOpcion> getOpcionesListFiltradasByModulo() {
        return opcionesListFiltradasByModulo;
    }*/

    /**
     * @param opcionesListFiltradasByModulo the opcionesListFiltradasByModulo to set
     */
    /*public void setOpcionesListFiltradasByModulo(List<SiOpcion> opcionesListFiltradasByModulo) {
        this.opcionesListFiltradasByModulo = opcionesListFiltradasByModulo;
    }*/

//    /**
//     * @return the nombreModulo
//     */
//    public String getNombreModulo() {
//        return nombreModulo;
//    }
//
//    /**
//     * @param nombreModulo the nombreModulo to set
//     */
//    public void setNombreModulo(String nombreModulo) {
//        this.nombreModulo = nombreModulo;
//    }

    /**
     * @return the modalRenderedCrearOpcion
     */
    public boolean isModalRenderedCrearOpcion() {
        return modalRenderedCrearOpcion;
    }

    /**
     * @param modalRenderedCrearOpcion the modalRenderedCrearOpcion to set
     */
    public void setModalRenderedCrearOpcion(boolean modalRenderedCrearOpcion) {
        this.modalRenderedCrearOpcion = modalRenderedCrearOpcion;
    }

    /**
     * @return the mrPopupCrearModulo
     */
    public boolean isMrPopupCrearModulo() {
        return mrPopupCrearModulo;
    }
    
    /**
     * @return the mrPopupCrearModulo
     */
    public boolean isMrPopupCrearRol() {
        return mrPopupCrearRol;
    }

    /**
     * @param mrPopupCrearModulo the mrPopupCrearModulo to set
     */
    public void setMrPopupCrearModulo(boolean mrPopupCrearModulo) {
        this.mrPopupCrearModulo = mrPopupCrearModulo;
    }
    
    /**
     * @param mrPopupCrearModulo the mrPopupCrearModulo to set
     */
    public void setMrPopupCrearRol(boolean mrPopupCrearRol) {
        this.mrPopupCrearRol = mrPopupCrearRol;
    }
    
    
     /**
     * @return the nombreRolSeleccionado
     */
    public String getNombreRolSeleccionado() {
        return nombreRolSeleccionado;
    }

    /**
     * @param nombreRolSeleccionado the nombreRolSeleccionado to set
     */
    public void setNombreRolSeleccionado(String nombreRolSeleccionado) {
        this.nombreRolSeleccionado = nombreRolSeleccionado;
    }
   /* public void validarRoles(){
        if(getAllRolesByModuloActivos().size()==1){
            System.out.println("probando  validarRoles()");
            setNombreRolSeleccionado(getAllRolesByModuloActivos().get(0).getNombre());
        }else {
            if(this.rolService.findRolByNombre(this.moduloService.findModuloByName(getNombreModuloSeleccionado(), Constantes.NO_ELIMINADO),  getNombreRolSeleccionado() )==null){
           
             setNombreRolSeleccionado(getAllRolesByModuloActivos().get(0).getNombre());
          }
         }
  }*/

    /**
     * @return the nombreOpcionSeleccionada
     */
    public String getNombreOpcionSeleccionada() {
        return nombreOpcionSeleccionada;
    }

    /**
     * @param nombreOpcionSeleccionada the nombreOpcionSeleccionada to set
     */
    public void setNombreOpcionSeleccionada(String nombreOpcionSelecionada) {
        this.nombreOpcionSeleccionada = nombreOpcionSelecionada;
    }

    /**
     * @return the estatusContar
     */
    public String getEstatusContar() {
        return estatusContar;
    }

    /**
     * @param estatusContar the estatusContar to set
     */
    public void setEstatusContar(String estatusContar) {
        this.estatusContar = estatusContar;
    }
        public boolean isSelecionada() {
        return selecionada;
    }

    /**
     * @param selecionada the selecionada to set
     */
    public void setSelecionada(boolean selecionada) {
        this.selecionada = selecionada;
    }
    
    //<--------------------------------------------------Arbol--------------------------------------------------->

    /**
     * @return the disableSiOpcion
     */
    public boolean isDisableSiOpcion() {
        return disableSiOpcion;
    }

    /**
     * @param disableSiOpcion the disableSiOpcion to set
     */
    public void setDisableSiOpcion(boolean disableSiOpcion) {
        this.disableSiOpcion = disableSiOpcion;
    }

    /**
     * @return the disableSiRol
     */
    public boolean isDisableSiRol() {
        return disableSiRol;
    }

    /**
     * @param disableSiRol the disableSiRol to set
     */
    public void setDisableSiRol(boolean disableSiRol) {
        this.disableSiRol = disableSiRol;
    }

    /**
     * @return the nombreSiOpcionSeleccionada
     */
    public String getNombreSiOpcionSeleccionada() {
        return nombreSiOpcionSeleccionada;
    }

    /**
     * @param nombreSiOpcionSeleccionada the nombreSiOpcionSeleccionada to set
     */
    public void setNombreSiOpcionSeleccionada(String nombreSiOpcionSelecionada) {
        this.nombreSiOpcionSeleccionada = nombreSiOpcionSelecionada;
    }

    /**
     * @return the arbolOpciones
     */
    public DefaultTreeModel getArbolOpciones() {
        return arbolOpciones;
    }

    /**
     * @param arbolOpciones the arbolOpciones to set
     */
    public void setArbolOpciones(DefaultTreeModel arbolOpciones) {
        this.arbolOpciones = arbolOpciones;
    }
    //------------------------------------------------------------Arbol-------------------------------------------\\
  //   public OpcionBeanModel(){}
     
   public void  ÁrbolOpcionBean (){
       System.out.println("Entró ÁrbolOpcionBean Constructor----------------------->");

       //this.conversation = conversacion;
        //this.conversationsManager = conversationsManager;

        this.conversationsManager.finalizeConversation(Constantes.CONVERSACION_CATALOGO_OPCIONES); //Terminando todas las conversaciones abiertas hasta este momento
        //Iniciando la conversación
        this.conversationsManager.beginConversation(this.conversation , Constantes.CONVERSACION_CATALOGO_OPCIONES);
        //this.moduloService = siModuloImpl;
        //this.opcionService = siOpcionImpl;
        //this.rolService = siRolImpl;
        

        //Borrando el arbol anterior
        this.arbolOpciones = null;
        this.arbolOpciones = construirArbol();
        
   }
   
   public DefaultTreeModel construirArbol() {
        return generarArbol();
    }

    public DefaultTreeModel generarArbol() {
        //Trayendo contenido
        if (modulosList == null) {
            this.modulosList = moduloService.getAllModulosByEstado(Constantes.NO_ELIMINADO);
        }
        else{
          if(  modulosList.size()!=moduloService.getAllModulosByEstado(Constantes.NO_ELIMINADO).size()){
               this.modulosList = moduloService.getAllModulosByEstado(Constantes.NO_ELIMINADO);
          }
        }
        if (opcionesList == null) {
            this.opcionesList = opcionService.getSiOpcionByRol();
        }
        else{
           if( opcionesList.size()!= opcionService.getSiOpcionByRol().size()){
               this.opcionesList = opcionService.getSiOpcionByRol();
           }
        }

        if(opcionesHijasList==null){
            this.opcionesHijasList= opcionService.getSiOpcionBySiOpcion();
        }
        else{
            if(opcionesHijasList.size() != opcionService.getSiOpcionBySiOpcion().size()){
               this.opcionesHijasList= opcionService.getSiOpcionBySiOpcion();  
            }
        }

        System.out.println("modulosList.size(): " + this.modulosList.size());
        System.out.println("padres: " + this.opcionesList.size());
        System.out.println("hijo: "+opcionesHijasList.size());


        // Configurando el nodo raiz
        DefaultMutableTreeNode nodoRaiz = agregarNodo(null, "Modulos", "", 0, null, false);
        DefaultTreeModel arbolReturn = new DefaultTreeModel(nodoRaiz);

        // Agregando carpetas y elementos
        DefaultMutableTreeNode nodoHojaModulos = null;
        DefaultMutableTreeNode nodoHojaOpciones = null;
         DefaultMutableTreeNode nodoHojaOpcionesHijas = null;

        for (SiModulo m : modulosList) {
            if (m.getId() != 0) {
                nodoHojaModulos = agregarNodo(nodoRaiz, m.getNombre(), null, 1, null, false);
                        for (SiOpcionVo o : opcionesList) {
                                             if(o.getIdSiModulo()==m.getId()){
                                nodoHojaOpciones = agregarNodo(nodoHojaModulos, o.getNombre(), null, 2, null, false); 
                                for (SiOpcionVo h : opcionesHijasList) {
                                    if (o.getId().equals( h.getIdPadre())) {
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
        userObject.setLeaf(isHoja);
        userObject.setText(texto);
        userObject.setUrl(url);
        userObject.setIdArchivo(idArchivo);
        userObject.setTooltip(texto);
        userObject.setUuidArchivo(uuid);
        //Expandimos si no es hoja
        if (!isHoja) {
            userObject.setExpanded(false); //Para que el nodo salga expandido o desplegado
        }
        if (texto.equals("Modulos")) {
            userObject.setExpanded(true);
        }

        // Asignando iconos
       
        userObject.setBranchContractedIcon(Constantes.EXPANDED_ICON);
        userObject.setBranchExpandedIcon(Constantes.CONTRACTED_ICON);

        //Personalizando el nodo
        nodo.setUserObject(userObject);
        nodo.setAllowsChildren(!isHoja);

        //Agregar nuestro nodo ya completo a su padre
        if (padre != null) {
            padre.add(nodo);
        }
        return nodo;
    }
   public void recorrerArbol(String text,int id){
         System.out.println("si entro");
        if( this.getArbolOpciones()!=null){
                for (int i = 0; i < getArbolOpciones().getChildCount(getArbolOpciones().getRoot()); i++) {
                    if(id==1){
                        
                    if(text.equals(getArbolOpciones().getChild(getArbolOpciones().getRoot(), i).toString())){
                       this.setNombreModuloSeleccionado(text); 
                       this.setNombreOpcionSeleccionada("");
                         System.out.println("consulta por modulo especifico  "+getArbolOpciones().getChild(getArbolOpciones().getRoot(), i)); 
                                          break;  
                    }
                    }
                    else{
                        for (int j = 0; j < getArbolOpciones().getChildCount(getArbolOpciones().getChild(getArbolOpciones().getRoot(), i)); j++) {
                            if(id==2){
                            if(text.equals(getArbolOpciones().getChild(getArbolOpciones().getChild(getArbolOpciones().getRoot(), i), j).toString())){
                                this.setNombreModuloSeleccionado(getArbolOpciones().getChild(getArbolOpciones().getRoot(), i).toString());  
                              this.setNombreOpcionSeleccionada(text);
                                   // setTabla((DataModel)new ListDataModel(siOpcionImpl.getSiOpcionBySiOpcion(siOpcionImpl.findOpcionByNameAndModulo(text, getArbolOpciones().getChild(getArbolOpciones().getRoot(), i).toString(), "False","0").getId().toString()))); System.out.println( "--------->"+      getTabla().getRowCount());
                                System.out.println("consulta por opcion " +getArbolOpciones().getChild(getArbolOpciones().getChild(getArbolOpciones().getRoot(), i), j));         
                                break;
                            }
                            }
                          }
                        }
                    
                    }
        }
        
    }
   
}