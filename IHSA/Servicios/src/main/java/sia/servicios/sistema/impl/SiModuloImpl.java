/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.inventarios.service.InvEstadoAprobacionSolicitudImpl;
import sia.modelo.SiModulo;
import sia.modelo.SiOpcion;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.campo.usuario.puesto.vo.CompaniaBloqueGerenciaVo;
import sia.modelo.oficio.vo.OficioConsultaVo;
import sia.modelo.oficio.vo.OficioPromovibleVo;
import sia.modelo.oficio.vo.PermisosVo;
import sia.modelo.rol.vo.RolVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.oficio.impl.OfOficioImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.servicios.sistema.vo.SiModuloVo;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.servicios.usuario.impl.RhUsuarioGerenciaImpl;
import sia.util.RequisicionEstadoEnum;
import sia.util.SolicitudMaterialEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author sluis
 */
@Stateless
@LocalBean
public class SiModuloImpl extends AbstractFacade<SiModulo> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    @Inject
    private DSLContext dslCtx;
    
    //
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SiOpcionImpl siOpcionRemote;
    @Inject
    private RequisicionImpl requisicionRemote;
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenRemote;
    @Inject
    private OrdenImpl ordenRemote;
    @Inject
    private SgEstatusAprobacionImpl sgEstatusAprobacionRemote;
    @Inject
    private RhUsuarioGerenciaImpl rhUsuarioGerenciaRemote;
    @Inject
    private OfOficioImpl ofOficioRemote;
    @Inject
    private SiPermisoImpl siPermisoRemote;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaRemote;
    @Inject
    InvEstadoAprobacionSolicitudImpl estadoAprobacionSolicitudLocal;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiModuloImpl() {
        super(SiModulo.class);
    }

    public boolean isUsed(int idSiModulo) {
        UtilLog4j.log.info(this, "SiModuloImpl.isUsed()");

        int cont = 0;

        List<Object> list = Collections.EMPTY_LIST;

        list = em.createQuery("SELECT o FROM SiOpcion o WHERE o.siModulo.id = :idSiModulo AND a.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSiModulo", idSiModulo).getResultList();
        if (list != null && !list.isEmpty()) {
            UtilLog4j.log.info(this, "SiModulo " + idSiModulo + " usado en SiOpcion");
            cont++;
            list.clear();
        }

        return (cont == 0 ? false : true);
    }

    public void save(String nombre, String ruta, String idUsuario) throws ExistingItemException {
        UtilLog4j.log.info(this, "SiModuloImpl.save()");

        SiModulo existente = findModuloByName(nombre, Constantes.NO_ELIMINADO);

        if (existente == null) {
            SiModulo siModulo = new SiModulo();
            siModulo.setNombre(nombre);
            siModulo.setRuta(ruta);
            siModulo.setGenero(new Usuario(idUsuario));
            siModulo.setFechaGenero(new Date());
            siModulo.setHoraGenero(new Date());
            siModulo.setEliminado(Constantes.NO_ELIMINADO);

            create(siModulo);
            UtilLog4j.log.info(this, "SiModulo CREATED SUCCESSFULLY");

        } else {
            throw new ExistingItemException(nombre, existente);
        }
    }

    public void crearModulo(String nombreModulo, String ruta, String idUsuario, boolean estado) throws Exception {
        UtilLog4j.log.info(this, "SiModuloImpl.crearModulo()");

        UtilLog4j.log.info(this, "DEBUG -- Módulo antes de crear: " + "nombre: " + nombreModulo + " ruta: " + ruta
                + "eliminado: " + estado + " usuarioGenero: " + idUsuario);

        SiModulo mod = findModuloByName(nombreModulo, estado);

        if (mod != null) {
            throw new Exception("Ya existe un módulo con este nombre");
        } else {
            mod = findModuloByName(nombreModulo, true); //Verificar si ya existe un módulo 'eliminado' con el mismo nombre, solo se le cambiara el status
            if (mod != null) {
                mod.setEliminado(estado);
                mod.setRuta(ruta);
                mod.setFechaGenero(new Date());
                mod.setHoraGenero(new Date());
                mod.setEliminado(estado);
                mod.setGenero(new Usuario(idUsuario));

                super.edit(mod);
            } else {
                SiModulo modulo = new SiModulo();

                modulo.setNombre(nombreModulo);
                modulo.setRuta(ruta);
                modulo.setFechaGenero(new Date());
                modulo.setHoraGenero(new Date());
                modulo.setEliminado(estado);
                modulo.setGenero(new Usuario(idUsuario));

                UtilLog4j.log.info(this, "DEBUG -- Modulo antes de crear: " + "nombre: " + modulo.getNombre() + " ruta: " + modulo.getRuta()
                        + " fechaGenero: " + modulo.getFechaGenero() + " horaGenero: " + modulo.getHoraGenero() + " eliminado: " + modulo.isEliminado()
                        + " usuarioGenero: " + modulo.getGenero());

                super.create(modulo);
            }
        }
    }

    public void update(int idSiModulo, String nombre, String ruta, String idUsuario) throws ExistingItemException {
        UtilLog4j.log.info(this, "SiModuloImpl.update()");

        SiModulo original = find(idSiModulo);
        SiModulo existente = findModuloByName(nombre, Constantes.NO_ELIMINADO);

        if (existente == null) {
            original.setNombre(nombre);
            original.setRuta(ruta);
            original.setModifico(new Usuario(idUsuario));
            original.setFechaModifico(new Date());
            original.setHoraModifico(new Date());

            edit(original);
            UtilLog4j.log.info(this, "SiModulo UPDATED SUCCESSFULLY");

        } else {
            if (idSiModulo == existente.getId().intValue()) { //Es el mismo módulo
                original.setRuta(ruta);
                original.setModifico(new Usuario(idUsuario));
                original.setFechaModifico(new Date());
                original.setHoraModifico(new Date());
                edit(original);
                UtilLog4j.log.info(this, "SiModulo UPDATED SUCCESSFULLY");
            } else {
                throw new ExistingItemException(nombre, existente);
            }
        }
    }

    public void actualizarModulo(SiModulo modulo, String nombreModulo, String rutaModulo, String idUsuario, boolean estado) throws Exception {
        UtilLog4j.log.info(this, "SiModuloImpl.actualizarModulo()");

        if (modulo.getNombre().equals(nombreModulo)) { //si el nombre es el mismo, actualizar directo
            modulo.setNombre(nombreModulo);
            modulo.setRuta(rutaModulo);
            modulo.setGenero(new Usuario(idUsuario));
            modulo.setFechaGenero(new Date());
            modulo.setHoraGenero(new Date());
            modulo.setEliminado(estado);

            UtilLog4j.log.info(this, "DEBUG -- Modulo antes de actualizar: " + "nombre: " + modulo.getNombre() + " ruta: " + modulo.getRuta()
                    + " fechaGenero: " + modulo.getFechaGenero() + " horaGenero: " + modulo.getHoraGenero() + " eliminado: " + modulo.isEliminado()
                    + " usuarioenero: " + modulo.getGenero());

            edit(modulo);
        } else {
            SiModulo mod = findModuloByName(nombreModulo, estado); //buscar si ya existe un módulo con el nuevo nombre
            SiModulo modEliminado = findModuloByName(nombreModulo, true); //buscar si ya existe un módulo con el nuevo nombre pero estado 'eliminado'
            if (mod == null) {
                if (modEliminado != null) { //Existe un módulo con el mismo nombre. Solo hay que cambiarle su status 'eliminado' a 'False'
                    //Al mismo tiempo el módulo actual, hay que cambiarle su status eliminado a 'True' para que este disponible a futuro
                    modEliminado.setEliminado(estado);
                    modEliminado.setRuta(rutaModulo);
                    modEliminado.setGenero(new Usuario(idUsuario));
                    modEliminado.setFechaGenero(new Date());
                    modEliminado.setHoraGenero(new Date());
                    modEliminado.setEliminado(estado);

                    //Cambiando estatus del módulo actual
                    modulo.setGenero(new Usuario(idUsuario));
                    modulo.setFechaGenero(new Date());
                    modulo.setHoraGenero(new Date());
                    modulo.setEliminado(true);
                } else {
                    modulo.setNombre(nombreModulo);
                    modulo.setRuta(rutaModulo);
                    modulo.setGenero(new Usuario(idUsuario));
                    modulo.setFechaGenero(new Date());
                    modulo.setHoraGenero(new Date());
                    modulo.setEliminado(estado);

                    UtilLog4j.log.info(this, "DEBUG -- Modulo antes de actualizar: " + "nombre: " + modulo.getNombre() + " ruta: " + modulo.getRuta()
                            + " fechaGenero: " + modulo.getFechaGenero() + " horaGenero: " + modulo.getHoraGenero() + " eliminado: " + modulo.isEliminado()
                            + " usuarioenero: " + modulo.getGenero());

                    edit(modulo);
                }
            } else {
                throw new Exception("Ya existe un módulo con este nombre");
            }
        }
    }

    public void delete(int idSiModulo, String idUsuario) throws ItemUsedBySystemException {
        UtilLog4j.log.info(this, "SiModuloImpl.delete()");
        SiModulo siModulo = find(idSiModulo);
        if (!isUsed(idSiModulo)) {
            siModulo.setModifico(new Usuario(idUsuario));
            siModulo.setFechaModifico(new Date());
            siModulo.setHoraModifico(new Date());
            siModulo.setEliminado(Constantes.ELIMINADO);

            edit(siModulo);
            UtilLog4j.log.info(this, "SiModulo DELETED SUCCESSFULLY");
        } else {
            throw new ItemUsedBySystemException(siModulo.getNombre(), siModulo);
        }
    }

    public void eliminarModulo(SiModulo modulo, String idUsuario, boolean estado) throws Exception {
        UtilLog4j.log.info(this, "SiModuloImpl.eliminarModulo()");

        //Validar que el módulo NO TENGA opciones
        if (!haveOpciones(modulo.getNombre(), false)) { //No encontré como usar la constante aquí
            modulo.setGenero(new Usuario(idUsuario));
            modulo.setFechaGenero(new Date());
            modulo.setHoraGenero(new Date());
            modulo.setEliminado(estado);

            UtilLog4j.log.info(this, "DEBUG -- Módulo antes de eliminar: " + "nombre: " + modulo.getNombre() + " ruta: " + modulo.getRuta()
                    + " fechaGenero: " + modulo.getFechaGenero() + " horaGenero: " + modulo.getHoraGenero() + " eliminado: " + modulo.isEliminado()
                    + " usuarioenero: " + modulo.getGenero());

            super.edit(modulo);
        } else {
            UtilLog4j.log.info(this, "No se puede eliminar el módulo porque tiene opciones");
            throw new Exception("El módulo tiene opciones. No se puede eliminar");
        }
    }

    public boolean haveOpciones(String nombreModulo, boolean estado) {
        List<SiOpcion> opciones = siOpcionRemote.getAllOpcionesByModulo(nombreModulo, estado);
        //  UtilLog4j.log.info(this,"haveOpciones().opciones.size()" + opciones.size());
        return !opciones.isEmpty();
    }

    public SiModulo findModuloByName(String nombreModulo, boolean estado) {
        UtilLog4j.log.info(this, "SiModuloImpl.findModuloByName()");
        SiModulo mod = null;
        if (nombreModulo != null && !nombreModulo.equals("")) {
            try {
                mod = (SiModulo) em.createQuery("SELECT m FROM SiModulo m WHERE m.nombre = :nombre AND m.eliminado = :estado").setParameter("nombre", nombreModulo).setParameter("estado", estado).getSingleResult();
            } catch (NoResultException nre) {
                UtilLog4j.log.info(this, "No se encontró ningún módulo: " + nre.getMessage());
                return mod;
            } catch (NonUniqueResultException nure) {
                UtilLog4j.log.info(this, "Se encontraron mas de un solo resultado en la consulta: " + nure.getMessage());
                return mod;
            }
        }
        UtilLog4j.log.info(this, "Se encontró 1 módulo");
        return mod;
    }

    public List<SiModulo> getAllModulosByEstado(boolean estado) {
        if (estado) {
            return em.createQuery("SELECT m FROM SiModulo m WHERE m.eliminado = :estado").setParameter("estado", estado).getResultList();
        }
        return null;
    }

    public List<SiModuloVo> getAllSiModuloList(String orderByField, boolean sortAscending, boolean eliminado) {
        UtilLog4j.log.info(this, "SiModuloImpl.getAllSiModuloList()");

        StringBuilder q = new StringBuilder();
        q.append(" SELECT ");
        q.append(" m.id, "); //0
        q.append(" m.nombre, "); //1
        q.append(" m.ruta "); //2
        q.append(" FROM ");
        q.append(" SI_MODULO m ");
        q.append(" WHERE ");
        q.append(" m.ELIMINADO = '").append((eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO)).append("'");

        if (orderByField != null && !orderByField.isEmpty()) {
            q.append(" ORDER BY m.").append(orderByField).append(" ").append((sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC));
        }

        Query query = em.createNativeQuery(q.toString());

        UtilLog4j.log.info(this, "query: " + query.toString());

        List<Object[]> result = query.getResultList();
        List<SiModuloVo> list = new ArrayList<SiModuloVo>();

        SiModuloVo vo = null;

        for (Object[] objects : result) {
            vo = new SiModuloVo();
            vo.setId((Integer) objects[0]);
            vo.setNombre((String) objects[1]);
            vo.setRuta((String) objects[2]);
            list.add(vo);
        }

        UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SiModulo");

        return (list != null ? list : Collections.EMPTY_LIST);
    }

    public List<SiModuloVo> getAllSiModuloListCategory(String orderByField, boolean sortAscending, boolean eliminado, boolean in, boolean viewAll) {
        StringBuilder q = new StringBuilder();
        q.append(
                "SELECT  mo.id,  mo.nombre,  mo.ruta \n"
                + "FROM  SI_MODULO mo \n"
                + "INNER JOIN SI_ROL sr ON sr.SI_MODULO = mo.ID and sr.ELIMINADO = 'False' \n"
                + "WHERE "
                + " mo.ELIMINADO = ").append(eliminado).append('\n');
        
        if (!viewAll && in) {
            q.append(" and sr.ID in ( \n");
        } else if (!viewAll) {
            q.append(" and sr.ID not in ( \n");
        }
        
        if (!viewAll) {
            q.append(
                    " select r.ID \n"
            + " from si_usuario_rol a  \n"
            + " inner join SI_ROL r on r.ID = a.SI_ROL \n"
            + " where a.ELIMINADO = 'False' \n"
            + " group by r.ID \n"
            + " ) \n");
        }
        
        q.append("group by mo.id,  mo.nombre,  mo.ruta \n");
        
        if (orderByField != null && !orderByField.isEmpty()) {
            q.append("ORDER BY mo.").append(orderByField)
                    .append(" ").append((sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC));
        }
        
        List<SiModuloVo> retVal;
        
        try {
            retVal = dslCtx.fetch(q.toString()).into(SiModuloVo.class);
        } catch (DataAccessException e) {
            UtilLog4j.log.warn(this, "", e);
            
            retVal = Collections.emptyList();
        }
        
        return retVal;
    }

    
    // TODO : simplificar lógica
    public List<SiModuloVo> getModulosUsuario(String usuario, int moduloID) {
        StringBuilder q = new StringBuilder();

        List<SiModuloVo> list = new ArrayList<>();

        try {
            q.append("SELECT "
                    + " m.ID, "
                    + " upper(m.NOMBRE) as NOMBRE, "
                    + " m.icono, "
                    + " m.ruta, "
                    + " m.tooltip AS tool_tip, "
                    + " m.rutaservlet AS ruta_servlet, "
                    + " m.extralinkrender AS extra_link_renderer \n"
                    + "FROM SI_MODULO m \n"
                    + "WHERE m.ID in ( \n"
                    + " SELECT m.ID FROM USUARIO a \n"
                    + "         INNER JOIN SI_USUARIO_ROL ur on ur.USUARIO = a.ID and ur.ELIMINADO = 'False' \n" //  and ur.AP_CAMPO =  ").append(Constantes.AP_CAMPO_NEJO);
                    + "         INNER JOIN SI_ROL r on r.ID = ur.SI_ROL and r.ELIMINADO = 'False' \n"
                    + "         INNER JOIN SI_MODULO m on m.id = r.SI_MODULO and m.ELIMINADO = 'False' \n"
                    + "  WHERE a.ID = '");
            q.append(usuario).append("' ");
            if (moduloID > 0) {
                q.append("\n        AND m.ID = ").append(moduloID);
            }
            q.append("\n        AND a.ELIMINADO = 'False' \n"
                    + " GROUP BY m.ID \n"
                    + " ORDER BY m.ID) ");

            
            List<SiModuloVo> modulos =
            		dslCtx.fetch(q.toString()).into(SiModuloVo.class);            

            for (SiModuloVo vo : modulos) {
                //PEndiente                
                vo.setListaCampo(new ArrayList<>());
                List<CampoUsuarioPuestoVo> lca;
                switch (vo.getId()) {
                    case Constantes.MODULO_REQUISICION:
                        vo.setListaCampo(new ArrayList<>());
                        vo.setPendiente(requisicionRemote.totalRequisicionesPendientes(usuario, Constantes.CERO));
                        lca = apCampoUsuarioRhPuestoRemote.getAllPorUsurio(usuario);
                        vo.setMapOpcion(new HashMap<>());
                        for (CampoUsuarioPuestoVo campoUsuarioPuestoVo : lca) {
                            List<SiOpcionVo> lo = requisicionRemote.totalRevPagina(usuario, campoUsuarioPuestoVo.getIdCampo(), RequisicionEstadoEnum.TODOS_ESTADOS_REQUISICION);
                            if (lo != null && !lo.isEmpty()) {
                                vo.getMapOpcion().put(campoUsuarioPuestoVo.getCampo(), lo);
                            }
                        }
                        break;
                    case Constantes.MODULO_COMPRA:
                        vo.setPendiente(ordenRemote.totalOcsPendientePorCampo(usuario, 0));
                        lca = apCampoUsuarioRhPuestoRemote.getAllPorUsurio(usuario);
                        vo.setMapOpcion(new HashMap<>());

                        //prueba
                        Map<Integer, List<SiOpcionVo>> contadores
                                = autorizacionesOrdenRemote.totalRevPagina(usuario);
                        //prueba

                        for (CampoUsuarioPuestoVo campoUsuarioPuestoVo : lca) {
                            /* original
                            List<SiOpcionVo> lo = 
                                    autorizacionesOrdenRemote.totalRevPagina(
                                            usuario, 
                                            campoUsuarioPuestoVo.getIdCampo(), 
                                            OrdenEstadoEnum.values()
                                    );
                            
                            if (lo != null && !lo.isEmpty()) {
                                vo.getMapOpcion().put(campoUsuarioPuestoVo.getCampo(), lo);
                            }*/

                            if (contadores != null && contadores.containsKey(campoUsuarioPuestoVo.getIdCampo())) {
                                vo.getMapOpcion().put(
                                        campoUsuarioPuestoVo.getCampo(),
                                        contadores.get(campoUsuarioPuestoVo.getIdCampo())
                                );
                            }

                        }
                        break;
                    case Constantes.MODULO_CONTRATO:
                        vo.setPendiente(0);
                        break;
                    case Constantes.MODULO_SGYL:
                        long t = sgEstatusAprobacionRemote.totalPendiente(usuario);
                        t = t + sgSolicitudEstanciaRemote.totalSolicituesPorAprobar(usuario);
                        vo.setPendiente(t);
                        break;
                    case Constantes.MODULO_INVENTARIOS:
                        //
                        vo.setPendiente(estadoAprobacionSolicitudLocal.totalSolicitudesUsuarioStatus(usuario, SolicitudMaterialEstadoEnum.POR_AUTORIZAR.getId()));
                        break;
                    case Constantes.MODULO_RH_ADMIN:
                        vo.setPendiente(rhUsuarioGerenciaRemote.totalUsuarioPorFinalizarBaja());
                        break;
                    case Constantes.MODULO_ADMIN_SIA:
                        vo.setPendiente(0);
                        break;
                    case Constantes.MODULO_CONTROL_OFICIO:
                        List<RolVO> roles = siPermisoRemote.fetchPermisosPorUsuarioModulo(usuario, Constantes.OFICIOS_MODULO_ID, Constantes.AP_CAMPO_DEFAULT);// obtener las opciones de bloques para este usuario
                        if (roles != null && !roles.isEmpty()) {
                            //   System.out.println(" roles " + roles.size());
                            try {
                                PermisosVo permisosVo = new PermisosVo(roles);
                                if (permisosVo.isRolEdicionOficios()) {
                                    // 
                                    List<CompaniaBloqueGerenciaVo> bloquesUsuario = apCampoUsuarioRhPuestoRemote.traerCompaniasBloquesGerencias(usuario);
                                    List<OficioPromovibleVo> listaOficio = ofOficioRemote.buscarOficiosBandejaEntrada(new OficioConsultaVo(),
                                            permisosVo, bloquesUsuario, usuarioRemote.find(usuario).getApCampo().getId());
                                    vo.setPendiente(listaOficio.size());
                                } else {
                                    vo.setPendiente(0);
                                }
                            } catch (Exception e) {
                                UtilLog4j.log.fatal(e);
                                vo.setPendiente(0);
                            }
                        } else {
                            vo.setPendiente(0);
                        }

                        break;
                    case Constantes.MODULO_GR:
                        vo.setPendiente(0);
                        break;
                    default:
                        vo.setPendiente(0);
                        break;
                }
                //
                list.add(vo);
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio una excepcion: " + e.getMessage(), e);
            list = Collections.emptyList();
        }

        return list;
    }

    public boolean hasAdminModule(String usuarioId) {
        boolean retVal = false;

        String sql
                = "SELECT count(*)\n"
                + "FROM USUARIO a \n"
                + "	INNER JOIN SI_USUARIO_ROL ur on ur.USUARIO = a.ID and ur.ELIMINADO = 'False' \n"
                + "	INNER JOIN SI_ROL r on r.ID = ur.SI_ROL and r.ELIMINADO = 'False'\n"
                + "	INNER JOIN SI_MODULO m on m.id = r.SI_MODULO and m.ELIMINADO = 'False'\n"
                + "WHERE a.ID = ?\n"
                + "	AND m.ID = ?\n"
                + "	AND a.ELIMINADO = 'False'\n"
                + "GROUP BY m.ID\n"
                + "ORDER BY m.ID";

        try {
            Object mod = em.createNativeQuery(sql)
                    .setParameter(1, usuarioId)
                    .setParameter(2, Constantes.MODULO_ADMINSIA)
                    .getSingleResult();

            if (mod != null) {
                retVal = Integer.parseInt(mod.toString()) > 0;
            }
        } catch (NoResultException e) {
            UtilLog4j.log.info(this, "", e);
        }

        return retVal;
    }
}
