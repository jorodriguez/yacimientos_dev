/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sistema.bean.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import sia.modelo.Proveedor;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.puesto.vo.RhPuestoVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.campo.nuevo.impl.RhPuestoImpl;
import sia.servicios.catalogos.impl.ProyectoOtImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.proveedor.impl.PvProveedorCompaniaImpl;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named
@SessionScoped
public class SoporteProveedor implements Serializable {

    @Inject
    private ProveedorServicioImpl proveedorServicioRemoto;
    @Inject
    UsuarioImpl usuarioServicioRemoto;
    @Inject
    private RhPuestoImpl rhPuestoImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private ProyectoOtImpl proyectoOtImpl;
    @Inject
    private PvProveedorCompaniaImpl pvProveedorCompaniaImpl;;
    //
    private List<Proveedor> proveedores;
    private List<SelectItem> selectItems;
    private List<ProyectoOtVo> listaProyectoOtVo;
    private List<String> nombreProveedor;
    private int idApCampo;
    //PANEL TOOL TIP

    //FACTURA USUARIOS
    private List<SelectItem> selectItemUsuario;
    private List<Usuario> usuario;
    private List<UsuarioVO> listaUsuarioVo;
    public SoporteProveedor() {
    }

    public List<Proveedor> getProveedores() {
        if (this.proveedores == null) {
            this.proveedores = this.proveedorServicioRemoto.findAll();
            UtilLog4j.log.info(this, "Proveedores: " + this.proveedores.size());
        }
        UtilLog4j.log.info(this, "proveedor: " + proveedores.size() + " completa");
        return this.proveedores;
    }

    public List<SelectItem> getSelectItems() {
        if (this.selectItems == null) {
            this.selectItems = new ArrayList<SelectItem>();
//            List<City> cities = getCities();
            for (Proveedor pr : this.getProveedores()) {
                SelectItem selectItem = new SelectItem(pr, pr.getNombre());
                selectItems.add(selectItem);
            }
        }
        return this.selectItems;
    }

    public void filterSelectItems(String proNameStartsWith) {
        this.selectItems = new ArrayList<SelectItem>();
        String proveedor;
        for (Proveedor pr : this.getProveedores()) {
            boolean addPr = false;
            if (proNameStartsWith == null) {
                addPr = true;
            } else {
                proveedor = pr.getNombre();
                if (proveedor != null) {
                    if (proveedor.toLowerCase().startsWith(proNameStartsWith.toLowerCase())) {
                        addPr = true;
                    }
                }
            }
            if (addPr) {
                SelectItem selectItem = new SelectItem(pr, pr.getNombre());
                selectItems.add(selectItem);
            }
        }
    }

    public Proveedor getProByName(String proName) {
        String proveedor;
        for (Proveedor pr : this.getProveedores()) {
            proveedor = pr.getNombre();
            if (proveedor != null) {
                if (proveedor.equals(proName)) {
                    return pr;
                }
            }

        }
        return null;
    }

    public List<SelectItem> regresaUsuario(String cadenaDigitada) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (Usuario p : this.getUsuario()) {
            if (p.getNombre() != null) {
                String cadenaPersona = p.getNombre().toLowerCase();
                cadenaDigitada = cadenaDigitada.toLowerCase();
                if (cadenaPersona.indexOf(cadenaDigitada) >= 0) {
                    SelectItem item = new SelectItem(p, p.getNombre());
                    list.add(item);
                }
            }
        }
        return list;
    }

    public List<SelectItem> regresaUsuarioActivo(String cadenaDigitada, int idApCampo, String orderByField, boolean sortAscending, Boolean activo, boolean eliminado) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (Usuario p : this.getUsuarioActivo(idApCampo, orderByField, sortAscending, activo, eliminado)) {
            if (p.getNombre() != null) {
                String cadenaPersona = p.getNombre().toLowerCase();
                cadenaDigitada = cadenaDigitada.toLowerCase();
                if (cadenaPersona.startsWith(cadenaDigitada)) {
                    SelectItem item = new SelectItem(p, p.getNombre());
                    list.add(item);
                }
            }
        }
        return list;
    }

    public List<SelectItem> getSelectItemUsuario() {
        if (this.selectItemUsuario == null) {
            this.selectItemUsuario = new ArrayList<SelectItem>();
//            List<City> cities = getCities();
            for (Usuario user : this.getUsuario()) {
                SelectItem selectItem = new SelectItem(user, user.getNombre());
                selectItemUsuario.add(selectItem);
            }
        }
        return this.selectItemUsuario;
    }

    public List<Usuario> getUsuario() {
        if (this.usuario == null) {
            this.setUsuario(this.usuarioServicioRemoto.findAll());
        }
        UtilLog4j.log.info(this, "Usuario: " + this.usuario.size() + " completa");
        return this.usuario;
    }

    public List<Usuario> getUsuarioActivo(int idApCampo, String orderByField, boolean sortAscending, Boolean activo, boolean eliminado) {
        this.setUsuario(this.usuarioServicioRemoto.findAll(idApCampo, orderByField, sortAscending, activo, eliminado));
        UtilLog4j.log.info(this, "Usuario: " + this.usuario.size() + " completa");
        return this.usuario;
    }

    public List<SelectItem> regresaProyectosOtCompletados(String cadenaDigitada, int idApCampo, String sesion) {
        UtilLog4j.log.info(this, "regresaProyectosOtCompletados");
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (ProyectoOtVo vo : this.getListaProyectoOt(idApCampo, sesion)) {
            if (vo.getNombre() != null) {
                String nombre = vo.getNombre().toLowerCase();
                cadenaDigitada = cadenaDigitada.toLowerCase();
                if (nombre.contains(cadenaDigitada)) {
                    SelectItem item = new SelectItem(vo,vo.getNombre());
                    //SelectItem item = new SelectItem(vo, vo.getNombre());
                    list.add(item);
                }
            }
        }
        UtilLog4j.log.info(this, "Lista de completos " + list.size());
        return list;
    }

    public List<ProyectoOtVo> getListaProyectoOt(int idApCampo, String sesion) {
        UtilLog4j.log.info(this, "getListaProyectoOt por campo " + idApCampo);
        if (listaProyectoOtVo == null) {
            listaProyectoOtVo = this.proyectoOtImpl.getListaProyectosOtPorCampo(idApCampo,sesion, null, false);
            this.idApCampo = idApCampo;
        }
        return listaProyectoOtVo;
    }

    public Usuario getUsuarioByName(String userName) {
        String u = null;
        for (Usuario user : this.getUsuario()) {
            u = user.getNombre();
            if (u != null) {
                if (u.equals(userName)) {
                    return user;
                }
            }
        }
        return null;
    }

    public void filterSelectItemsUsuario(String userNameStartsWith) {
        this.selectItemUsuario = new ArrayList<SelectItem>();
        if (!userNameStartsWith.isEmpty()) {
            String user = null;
            for (Usuario usuari : this.getUsuario()) {
                boolean addU = false;
                if (userNameStartsWith == null) {
                    addU = true;
                } else {
                    user = usuari.getNombre();
                    if (user != null) {
                        if (user.toLowerCase().startsWith(userNameStartsWith.toLowerCase())) {
                            addU = true;
                        }
                    }
                }
                if (addU) {
                    SelectItem selectItem = new SelectItem(usuari, usuari.getNombre());
                    this.selectItemUsuario.add(selectItem);
                }
            }
        }
    }

    //Lista u voç
     /**
     * @return the listaUsuarioVo
     */
    public List<UsuarioVO> getListaUsuarioVo() {
        if (listaUsuarioVo == null) {
            UtilLog4j.log.info(this, "Aqui");
            listaUsuarioVo = usuarioServicioRemoto.usuarioActio(-1);
            UtilLog4j.log.info(this, "despues de aquí");
        }
        return listaUsuarioVo;
    }

    public List<SelectItem> regresaUsuarioActivoVO(String cadenaDigitada) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (UsuarioVO p : getListaUsuarioVo()) {
            if (p.getNombre() != null) {
                String cadenaPersona = p.getNombre().toLowerCase();
                cadenaDigitada = cadenaDigitada.toLowerCase();
                if (cadenaPersona.startsWith(cadenaDigitada)) {
                    SelectItem item = new SelectItem(p.getIdJefe(), p.getNombre());
                    list.add(item);
                }
            }
        }
        return list;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(List<Usuario> usuario) {
        this.usuario = usuario;
    }
    //REgresa puesto
    private List<RhPuestoVo> puestoVo;

    public List<SelectItem> regresaPuesto(String cadenaDigitada) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (RhPuestoVo p : this.getPuesto(cadenaDigitada)) {
            if (p.getNombre() != null) {
                String cadenaPersona = p.getNombre().toLowerCase();
                cadenaDigitada = cadenaDigitada.toLowerCase();
                if (cadenaPersona.startsWith(cadenaDigitada)) {
                    SelectItem item = new SelectItem(p.getId(), p.getNombre());
                    list.add(item);
                }
            }
        }
        return list;
    }

    private List<RhPuestoVo> getPuesto(String cadena) {
        if (this.puestoVo == null) {
            this.setPuestoVo(this.rhPuestoImpl.findAllRhPuesto("nombre", true, false));
        } else {
            this.setPuestoVo(this.rhPuestoImpl.getRhPuestoLike(cadena));
        }
        UtilLog4j.log.info(this, "puesto: " + this.puestoVo.size() + " completa");
        return this.puestoVo;
    }

    /**
     * @param puesto the puesto to set
     */
    public void setPuestoVo(List<RhPuestoVo> puesto) {
        this.puestoVo = puesto;
    }
//Usuario campo

    public List<SelectItem> regresaUsuarioCampo(String cadena, int idCampo, String nombre, boolean sortAscending, boolean activo, boolean eliminado) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (Usuario p : this.getUsuarioCampo(idCampo, nombre, sortAscending, activo, eliminado)) {
            if (p.getNombre() != null) {
                String cadenaPersona = p.getNombre().toLowerCase();
                cadena = cadena.toLowerCase();
                if (cadenaPersona.startsWith(cadena)) {
                    SelectItem item = new SelectItem(p, p.getNombre());
                    list.add(item);
                }
            }
        }
        return list;
    }

    public List<Usuario> getUsuarioCampo(int idCampo, String nombre, boolean sortAscending, boolean activo, boolean eliminado) {
        this.setUsuario(this.apCampoUsuarioRhPuestoImpl.regresaUsuarioCampo(idCampo, nombre, sortAscending, activo, eliminado));
        UtilLog4j.log.info(this, "Usuario campo: " + this.usuario.size() + " completa");
        return this.usuario;
    }
    
    //
    
    public List<SelectItem> regresaUsuariosPorBloque(String cadena, int idCampo) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (CampoUsuarioPuestoVo  p : traerUsuarioCampo(idCampo)) {
            if (p.getUsuario() != null) {
                String cadenaPersona = p.getUsuario().toLowerCase();
                cadena = cadena.toLowerCase();
                if (cadenaPersona.contains(cadena)) {
                    SelectItem item = new SelectItem(p, p.getUsuario());
                    list.add(item);
                }
            }
        }
        return list;
    }
    
    public List<CampoUsuarioPuestoVo> traerUsuarioCampo(int idCampo) {
        UsuarioVO vo = new UsuarioVO();
        return  apCampoUsuarioRhPuestoImpl.traerUsurioPorCampo(idCampo,vo);
    }
    
 //AUTO-COMPLETAR PROVEEDOR
 /**
 * @user icristobal
 * @param cadenaDigitada
 * @return 
 */      
    public List<SelectItem> regresaNombreProveedorActivo(String cadenaDigitada, String rfcCompania) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (String p : this.getNombreProveedor(cadenaDigitada, rfcCompania)) {
            if (p != null) {
                String cadenaPersona = p.toLowerCase();
                cadenaDigitada = cadenaDigitada.toLowerCase();
                if (cadenaPersona.startsWith(cadenaDigitada)) {
                    SelectItem item = new SelectItem(p);
                    list.add(item);
                }
            }
        }
        return list;
    }

    private List<String> getNombreProveedor(String cadena, String rfcCompania) {
        if (nombreProveedor == null) {
            setNombreProveedor(proveedorServicioRemoto.traerNombreProveedorQueryNativo(rfcCompania, ProveedorEnum.ACTIVO.getId()));
            UtilLog4j.log.info(this, "proveedor: " + nombreProveedor.size() + " c y nc");
        } else {
            setNombreProveedor(proveedorServicioRemoto.traerNombreLikeProveedorQueryNativo(cadena,rfcCompania, ProveedorEnum.ACTIVO.getId()));
            UtilLog4j.log.info(this, "proveedor: " + nombreProveedor.size() + " con like");
        }
        return nombreProveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setNombreProveedor(List<String> nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    /**
     * @return the listaProyectoOtVo
     */
    public List<ProyectoOtVo> getListaProyectoOtVo() {
        return listaProyectoOtVo;
    }

    /**
     * @param listaProyectoOtVo the listaProyectoOtVo to set
     */
    public void setListaProyectoOtVo(List<ProyectoOtVo> listaProyectoOtVo) {
        this.listaProyectoOtVo = listaProyectoOtVo;
    }

    /**
     * @return the idApCampo
     */
    public int getIdApCampo() {
        return idApCampo;
    }

    /**
     * @param idApCampo the idApCampo to set
     */
    public void setIdApCampo(int idApCampo) {
        this.idApCampo = idApCampo;
    }

   

    /**
     * @param listaUsuarioVo the listaUsuarioVo to set
     */
    public void setListaUsuarioVo(List<UsuarioVO> listaUsuarioVo) {
        this.listaUsuarioVo = listaUsuarioVo;
    }
}
