/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import com.google.api.client.util.Strings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.SiAyuda;
import sia.modelo.SiOpcion;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.servicios.sgl.viaje.impl.SgCambioItinerarioImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sistema.vo.MenuSiOpcionVo;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.util.UtilLog4j;

/**
 *
 * @author sluis
 */
@Stateless
public class SiOpcionImpl extends AbstractFacade<SiOpcion> {

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SiModuloImpl moduloService;
    @Inject
    private SiAyudaImpl ayudaService;

    @Inject
    SgCambioItinerarioImpl sgCambioItinerarioRemote;
    @Inject
    SgSolicitudViajeImpl sgSolicitudViajeRemote;
    @Inject
    SgEstatusAprobacionImpl sgEstatusAprobacionRemote;
    @Inject
    SgSolicitudEstanciaImpl sgSolicitudEstanciaRemote;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiOpcionImpl() {
        super(SiOpcion.class);
    }

    public List<SiOpcion> getAllOpcionesByEstado(boolean estado) {
        List<SiOpcion> retVal = null;
        if (estado) {
            retVal = em.createQuery("SELECT op FROM SiOpcion op WHERE op.eliminado = :estado").setParameter("estado", estado).getResultList();
        }
        return retVal;
    }

    /*
     * Este metodo no se debe utilizar ya Ahora se ocupara:
     * getAllSiOpcionBySiModulo
     *
     */
    @Deprecated
    public List<SiOpcion> getAllOpcionesByModulo(String nombreModulo, boolean estado) {
        UtilLog4j.log.info(this, "SiOpcionImpl.getAllOpcionesByModulo()");

        StringBuilder query = new StringBuilder();
        query.append("SELECT op FROM SiOpcion op WHERE op.siModulo.nombre = :nombreModulo ");
        query.append("AND op.eliminado = :estado");

        List<SiOpcion> opciones = em.createQuery(query.toString()).setParameter("nombreModulo", nombreModulo).setParameter("estado", estado).getResultList();

        if (opciones != null) {
            UtilLog4j.log.info(this, "Opciones filtradas por módulo:" + opciones.size());
        }

        return opciones;
    }

    public void crearOpcion(String nombreOpcion, String pagina, String idUsuario, boolean estado, String nombreModulo, int estatusContar, SiOpcion opcionPadre) throws Exception {
//	System.out.println("SiOpcionImpl.crearOpcion()");
        UtilLog4j.log.info(this, "SiOpcionImpl.crearOpcion()");

        SiOpcion op = findOpcionByNameAndModulo(nombreOpcion, nombreModulo, estado, null);

        if (op == null) { //si no existe la opcion con ese nombre en el modulo, la creo
            op = findOpcionByNameAndModulo(nombreOpcion, nombreModulo, true, null);
            if (op != null) { //existe una opción con el mismo nombre pero eliminada. Ahora solo la 'activaremos'
                op.setPagina(pagina);
                op.setFechaGenero(new Date());
                op.setHoraGenero(new Date());
                op.setGenero(new Usuario(idUsuario));
                op.setSiModulo(moduloService.findModuloByName(nombreModulo, estado));
                op.setEliminado(estado);

                op.setEstatusContar(estatusContar);
                if (opcionPadre != null) {
                    op.setPosicion(maxPosicionBySiOpcion(opcionPadre.getId()));
                    op.setSiOpcion(opcionPadre);
                } else {
                    op.setPosicion(0);
                }
//		System.out.println("DEBUG -- Opcion antes de crear: " + "nombre: " + op.getNombre() + " pagina: " + op.getPagina());
                UtilLog4j.log.info(this, "DEBUG -- Opcion antes de crear: " + "nombre: " + op.getNombre() + " pagina: " + op.getPagina()
                        + " modulo: " + op.getSiModulo().getNombre() + " fechaGenero: " + op.getFechaGenero() + " horaGenero: "
                        + op.getHoraGenero() + " eliminado: " + op.isEliminado() + " usuarioGenero: " + op.getGenero());

                super.edit(op);
            } else {
                SiOpcion opcion = new SiOpcion();

                opcion.setNombre(nombreOpcion);
                opcion.setPagina(pagina);
                opcion.setFechaGenero(new Date());
                opcion.setHoraGenero(new Date());
                opcion.setEliminado(estado);
                opcion.setGenero(new Usuario(idUsuario));
                opcion.setSiModulo(moduloService.findModuloByName(nombreModulo, estado));
                opcion.setEstatusContar(estatusContar);
                if (opcionPadre != null) {
                    opcion.setSiOpcion(opcionPadre);
                    opcion.setPosicion(maxPosicionBySiOpcion(opcionPadre.getId()));
                } else {
                    opcion.setPosicion(0);
                }
                UtilLog4j.log.info(this, "DEBUG -- Opcion antes de crear: " + "nombre: " + opcion.getNombre() + " pagina: " + opcion.getPagina()
                        + " modulo: " + opcion.getSiModulo().getNombre() + " fechaGenero: " + opcion.getFechaGenero() + " horaGenero: "
                        + opcion.getHoraGenero() + " eliminado: " + opcion.isEliminado() + " usuarioGenero: " + opcion.getGenero());

                super.create(opcion);
            }

        } else {
            throw new Exception("Ya existe una opción con este nombre: " + nombreOpcion);
        }
    }

    public void actualizarOpcion(SiOpcion opcion, String nuevoNombreOpcion, String nuevaPaginaOpcion, String idUsuario) throws Exception {
        UtilLog4j.log.info(this, "SiOpcionImpl.actualizarOpcion()");

        //	
        UtilLog4j.log.info(this, "DEBUG -- Opcion antes de actualizar: " + "nombre: " + opcion.getNombre() + " pagina: " + opcion.getPagina()
                + "modulo: " + opcion.getSiModulo().getNombre() + " fechaGenero: " + opcion.getFechaGenero() + " horaGenero: "
                + opcion.getHoraGenero() + " eliminado: " + opcion.isEliminado() + " usuarioGenero: " + opcion.getGenero());

        if (!opcion.getNombre().equals(nuevoNombreOpcion)) { //Validación para evitar que se repitan Opciones con el mismo Nombre
            SiOpcion op = findOpcionByNameAndModulo(nuevoNombreOpcion, opcion.getSiModulo().getNombre(), opcion.isEliminado(), opcion.getPosicion().toString());
            if (op != null) {
                throw new Exception("Ya existe una opción con este nombre");
            } else {
                opcion.setNombre(nuevoNombreOpcion);
                opcion.setPagina(nuevaPaginaOpcion);
                opcion.setGenero(new Usuario(idUsuario));
                opcion.setFechaGenero(new Date());
                opcion.setHoraGenero(new Date());
            }
        } else {
            opcion.setNombre(nuevoNombreOpcion);
            opcion.setPagina(nuevaPaginaOpcion);
            opcion.setGenero(new Usuario(idUsuario));
            opcion.setFechaGenero(new Date());
            opcion.setHoraGenero(new Date());
        }

        UtilLog4j.log.info(this, "DEBUG -- Opcion despues de crear: " + "nombre: " + opcion.getNombre() + " pagina: " + opcion.getPagina()
                + "modulo: " + opcion.getSiModulo().getNombre() + " fechaGenero: " + opcion.getFechaGenero() + " horaGenero: "
                + opcion.getHoraGenero() + " eliminado: " + opcion.isEliminado() + " usuarioGenero: " + opcion.getGenero());

        super.edit(opcion);
    }

    public void eliminarOpcion(SiOpcion opcion, String idUsuario, boolean estado) throws Exception {
        UtilLog4j.log.info(this, "SiOpcionImpl.eliminarOpcion()");

        //Verificar que la opción no tenga ayudas
        if (!haveAyudas(opcion.getSiModulo().getNombre(), opcion.getId(), false)) { //No encontré como poner la constante aquí.
            opcion.setGenero(new Usuario(idUsuario));
            opcion.setFechaGenero(new Date());
            opcion.setHoraGenero(new Date());
            opcion.setEliminado(estado);

            UtilLog4j.log.info(this, "DEBUG -- Opcion antes de eliminar: " + "nombre: " + opcion.getNombre() + "módulo: " + opcion.getSiModulo().getNombre() + "pagina: " + opcion.getPagina()
                    + " fechaGenero: " + opcion.getFechaGenero() + " horaGenero: " + opcion.getHoraGenero() + " eliminado: " + opcion.isEliminado()
                    + " usuarioGenero: " + opcion.getGenero());

            super.edit(opcion);
        } else {
            UtilLog4j.log.info(this, "No se puede eliminar la opción porque tiene ayudas");
            throw new Exception("La opción tiene ayudas. No se puede eliminar");
        }
    }

    public SiOpcion findOpcionByNameAndModulo(String nombreOpcion, String nombreModulo, boolean estado, String posicion) {
        UtilLog4j.log.info(this, "SiOpcionImpl.findOpcionByNameAndModulo()");

        SiOpcion op = null;
        if (!Strings.isNullOrEmpty(nombreOpcion) && !Strings.isNullOrEmpty(nombreModulo)) {
            if (posicion == null) {
                try {
                    op = (SiOpcion) em.createQuery("SELECT op FROM SiOpcion op WHERE op.nombre = :nombreOpcion AND op.siModulo.nombre = :nombreModulo and op.eliminado = :estado")
                            .setParameter("nombreOpcion", nombreOpcion)
                            .setParameter("nombreModulo", nombreModulo)
                            .setParameter("estado", estado)
                            .getSingleResult();
                } catch (NoResultException nre) {
                    UtilLog4j.log.info(this, "No se encontró ninguna opción: " + nre.getMessage(), nre);
//		    return op;
                } catch (NonUniqueResultException nure) {
                    UtilLog4j.log.info(this, "Se encontraron mas de un solo resultado en la consulta: " + nure.getMessage(), nure);
//		    return op;
                }
            } else {
                try {
                    op = (SiOpcion) em.createQuery("SELECT  op FROM SiOpcion op WHERE op.nombre = :nombreOpcion AND op.siModulo.nombre = :nombreModulo and op.eliminado = :estado and op.posicion = :posicion")
                            .setParameter("nombreOpcion", nombreOpcion)
                            .setParameter("nombreModulo", nombreModulo)
                            .setParameter("estado", estado)
                            .setParameter("posicion", Integer.valueOf(posicion))
                            .getSingleResult();
                } catch (NoResultException nre) {
                    UtilLog4j.log.info(this, "No se encontró ninguna opción: " + nre.getMessage() + " " + op, nre);
//		    return op;
                } catch (NonUniqueResultException nure) {
                    UtilLog4j.log.info(this, "Se encontraron mas de un solo resultado en la consulta: " + nure.getMessage(), nure);
//		    return op;
                }
            }
        }
        UtilLog4j.log.info("Se encontró 1 opción");
        return op;
    }

    public boolean haveAyudas(String nombreModulo, Integer idOpcion, boolean estado) {
        List<SiAyuda> ayudas = ayudaService.getAyudasByModuloAndOpcion(nombreModulo, idOpcion, estado);
        UtilLog4j.log.info(this, "haveAyudas().ayudas.size()" + ayudas.size());
//	return ayudas.isEmpty() ? false : true;
        return !ayudas.isEmpty();
    }

    public List<SiOpcionVo> getAllSiOpcion(String orderByField, boolean sortAscending, boolean eliminado) {
        UtilLog4j.log.info(this, "SiOpcionImpl.getAllSiOpcion()");

        StringBuilder q = new StringBuilder("SELECT "
                + "o.ID, " //0
                + "o.NOMBRE, " //1
                + "o.pagina, " //2
                + "o.SI_MODULO " //3
                + "FROM "
                + "SI_OPCION o "
                + "WHERE "
                + "o.ELIMINADO = '");
        q.append(eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).append('\'');

        if (orderByField != null && !orderByField.isEmpty()) {
            q.append(" ORDER BY o.")
                    .append(orderByField)
                    .append(' ')
                    .append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
        }

        Query query = em.createNativeQuery(q.toString());

        UtilLog4j.log.info(this, "query: " + query.toString());

        List<SiOpcionVo> list = new ArrayList<SiOpcionVo>();
        try {
            List<Object[]> result = query.getResultList();

            SiOpcionVo vo = null;

            for (Object[] objects : result) {
                vo = new SiOpcionVo();
                vo.setId((Integer) objects[0]);
                vo.setNombre((String) objects[1]);
                vo.setPagina((String) objects[2]);
                vo.setIdSiModulo((Integer) objects[3]);
                list.add(vo);
            }

            UtilLog4j.log.info(this, "Se encontraron " + list.size() + " SiOpcion");
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return list;
    }

    /*
     * Metodo que devuelve lista de opciones (menus) author : Seth Modifico:
     * Nestor Lopez 10/10/2013
     */
    public List<SiOpcionVo> getAllSiOpcionBySiModulo(int idSiModulo, String idUsuario, int campo) {
        clearQuery();
        query.append("select distinct(o.ID), o.NOMBRE, o.PAGINA, o.PAGINALISTENER, o.ESTATUS_CONTAR, o.POSICION, case when o.SI_OPCION is null then 0 else o.SI_OPCION end from SI_REL_ROL_OPCION rop")
                .append("      inner join SI_OPCION o on rop.SI_OPCION = o.ID")
                .append("      inner join si_usuario_rol ur on rop.SI_ROL = ur.SI_ROL ")
                .append("  where o.ELIMINADO ='").append(Constantes.NO_ELIMINADO).append("' ")
                .append("  and o.SI_MODULO = ").append(idSiModulo)
                .append("  and ur.USUARIO = '").append(idUsuario).append("'")
                .append("  and ur.AP_CAMPO = ").append(campo)
                .append("  and rop.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' ")
                .append("  and ur.eliminado = '").append(Constantes.NO_ELIMINADO).append("'")
                .append("  and o.SI_OPCION is null")
                .append(" ORDER BY o.POSICION ").append(Constantes.ORDER_BY_ASC);
        //
        List<SiOpcionVo> list = new ArrayList<SiOpcionVo>();
        try {
            List<Object[]> result = em.createNativeQuery(query.toString()).getResultList();

            for (Object[] objects : result) {
                list.add(castOpcionVO(objects, ""));
            }

            UtilLog4j.log.info(this, "Se encontraron " + list.size() + " SiOpcion");
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return list;
    }

    public List<SiOpcionVo> getAllOpcionesByModulo(int idSiModulo, boolean estado) {
        List<SiOpcionVo> list = new ArrayList<SiOpcionVo>();
        try {
            clearQuery();
            query.append("select distinct(o.ID), o.NOMBRE, o.PAGINA, o.PAGINALISTENER, o.ESTATUS_CONTAR, o.POSICION, case when o.SI_OPCION is null then 0 else o.SI_OPCION end from SI_OPCION o ")
                    .append("  where o.ELIMINADO ='").append(estado).append("' ")
                    .append("  and o.SI_MODULO = ").append(idSiModulo)
                    .append(" ORDER BY o.POSICION ").append(Constantes.ORDER_BY_ASC);

            List<Object[]> result = em.createNativeQuery(query.toString()).getResultList();

            for (Object[] objects : result) {
                list.add(castOpcionVO(objects, ""));
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            list = new ArrayList<SiOpcionVo>();
        }
        return list;
    }

    /**
     * Creo: NLopez
     *
     * @param idOpcion
     * @param rol
     * @return
     */
    public List<SiOpcionVo> getChildSiOpcion(int idOpcion, String idUsuario, int idModulo) {
        clearQuery();
        query.append("SELECT DISTINCT o.ID, o.NOMBRE, o.pagina, o.PAGINALISTENER, o.ESTATUS_CONTAR, o.POSICION, case when o.SI_OPCION is null then 0 else o.SI_OPCION end FROM SI_REL_ROL_OPCION ro ")
                .append("      inner join SI_OPCION o on ro.SI_OPCION = o.ID")
                .append("      inner join SI_USUARIO_ROL ur on ro.SI_ROL = ur.SI_ROL")
                .append("  WHERE o.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' ")
                .append("  AND ro.ELIMINADO ='").append(Constantes.NO_ELIMINADO).append("' ")
                .append("  and ur.USUARIO = '").append(idUsuario).append("' ")
                .append("  and o.SI_MODULO = ").append(idModulo)
                .append("  AND o.SI_OPCION= ").append(idOpcion)
                .append("  and ur.eliminado = '").append(Constantes.NO_ELIMINADO).append("'")
                .append("  ORDER BY o.POSICION asc");
//
        List<Object[]> result = em.createNativeQuery(query.toString()).getResultList();
        List<SiOpcionVo> list = new ArrayList<SiOpcionVo>();
        for (Object[] objects : result) {
            list.add(castOpcionVO(objects, idUsuario));
        }

        return (list != null ? list : Collections.EMPTY_LIST);
    }

    /*
     * Metodo que devuelve lista de opciones (menus) author : Seth Modifico:
     * Nestor Lopez 10/10/2013
     */
    public List<SiOpcionVo> getSiOpcionBySiModulo(int idSiModulo, int rol) {

        StringBuilder q = new StringBuilder();
        q.append("SELECT ")
                .append("o.ID, ")
                .append("o.NOMBRE, ")
                .append("o.pagina, ")
                .append("o.ESTATUS_CONTAR, ")
                .append("CASE WHEN o.SI_OPCION is null THEN '' ") //13
                .append("WHEN o.SI_OPCION is not null THEN (SELECT o1.NOMBRE FROM SI_OPCION o1 WHERE o1.ID = o.SI_OPCION) ") //13
                .append("END AS padre ")
                .append("FROM ")
                .append("SI_OPCION o, ")
                .append("SI_REL_ROL_OPCION ro ")
                .append("WHERE ")
                .append("o.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' ")
                .append(" AND ro.ELIMINADO ='").append(Constantes.NO_ELIMINADO).append("' ");
        if (idSiModulo != 0) {
            q.append("AND o.SI_MODULO= ").append(idSiModulo);
        }
        q.append(" AND o.ID = ro.SI_OPCION ")
                .append("AND ro.SI_ROL= ").append(rol)
                .append(" ORDER BY o.POSICION ").append(Constantes.ORDER_BY_ASC);

        Query query = em.createNativeQuery(q.toString());

//        UtilLog4j.log.info(this,"query: " + query.toString());
        List<SiOpcionVo> list = new ArrayList<SiOpcionVo>();
        try {
            List<Object[]> result = query.getResultList();

            for (Object[] objects : result) {
                list.add(castAllOpcionVO(objects, 1));
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            list.clear();
        }

        return list;

    }

    public List<MenuSiOpcionVo> getListaMenu(int idSiModulo, String usrID, int campo) {
        StringBuilder q = new StringBuilder();
        q.append(" select a.ID, a.SI_MODULO, ro.SI_ROL, a.NOMBRE, aa.ID, aa.SI_MODULO, aa.SI_OPCION, aa.NOMBRE, aa.PAGINA, aa.ESTATUS_CONTAR ,aa.POSICION, aa.PAGINALISTENER ")
                .append(" from SI_REL_ROL_OPCION ro  ")
                .append("	inner join SI_USUARIO_ROL ur on ur.SI_ROL = ro.SI_ROL and ur.ELIMINADO = 'False' ")
                .append("	left join SI_OPCION a on ro.SI_OPCION = a.ID AND a.ELIMINADO = 'False' ")
                .append("	left join SI_OPCION aa on aa.SI_OPCION = a.ID and aa.ELIMINADO = 'False' ").append(" and aa.id in (select SI_OPCION  ")
                .append(" from SI_REL_ROL_OPCION  ")
                .append(" where ELIMINADO = 'False'  ")
                .append(" and SI_ROL = ro.SI_ROL) ");

        q.append(" where ro.ELIMINADO = 'False' ");
        if (idSiModulo > 0) {
            q.append(" and a.SI_MODULO =  ").append(idSiModulo);
        }
        if (usrID != null && !usrID.isEmpty()) {
            q.append(" and ur.USUARIO = '").append(usrID).append("'");
        }

        if (campo > 0) {
            q.append(" and ur.AP_CAMPO = ").append(campo);
        }

        q.append(" and a.POSICION = 0 ")
                .append(" group by a.ID, a.SI_MODULO, ro.SI_ROL, a.NOMBRE, aa.ID, aa.SI_MODULO, aa.SI_OPCION, aa.NOMBRE, aa.PAGINA, aa.ESTATUS_CONTAR ,aa.POSICION, aa.PAGINALISTENER ")
                .append(" order by a.ID, aa.POSICION ");

        List<MenuSiOpcionVo> list = new ArrayList<MenuSiOpcionVo>();
        try {
            List<Object[]> result = em.createNativeQuery(q.toString()).getResultList();

            int idMenu = 0;
            MenuSiOpcionVo menu = null;
            for (Object[] objects : result) {
                if (idMenu != ((Integer) objects[0])) {
                    if (idMenu > 0) {
                        list.add(menu);
                    }
                    menu = new MenuSiOpcionVo();
                }
                castOpcionVOMenu(menu, objects, (idMenu != ((Integer) objects[0])));
                idMenu = menu.getPadre().getId();
            }
            if (idMenu > 0 && result.size() > 0) {
                list.add(menu);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            list.clear();
        }
        return list;
    }

    public void castOpcionVOMenu(MenuSiOpcionVo objeto, Object[] objects, boolean menuPadre) {

        if (menuPadre) {
            SiOpcionVo voP = new SiOpcionVo();
            voP.setId((Integer) objects[0]);
            voP.setNombre((String) objects[3]);
            objeto.setPadre(voP);

            SiOpcionVo vo = new SiOpcionVo();
            vo.setId((Integer) objects[4]);
            vo.setNombre((String) objects[7]);
            vo.setPagina((String) objects[8]);
            vo.setEstatusContar((Integer) objects[9]);
            vo.setIdPadre((Integer) objects[0]);
            vo.setPadre((String) objects[3]);
            vo.setPosicion((Integer) objects[10]);
            vo.setPaginaListener((String) objects[11]);
            objeto.getHijos().add(vo);

        } else {
            SiOpcionVo vo = new SiOpcionVo();
            vo.setId((Integer) objects[4]);
            vo.setNombre((String) objects[7]);
            vo.setPagina((String) objects[8]);
            vo.setEstatusContar((Integer) objects[9]);
            vo.setIdPadre((Integer) objects[0]);
            vo.setPadre((String) objects[3]);
            vo.setPosicion((Integer) objects[10]);
            vo.setPaginaListener((String) objects[11]);
            objeto.getHijos().add(vo);
        }
        //return objeto;
    }

    /*
     * Metodo que devuelve lista de opciones (menus) sin rol author : Nestor
     * Lopez 19/11/2013
     *
     */
    public List<SiOpcionVo> getSipcionesSinRol(int idSiModulo, String orderByFielda, boolean sortAscending,
            boolean eliminado) {

        clearQuery();

        query.append("SELECT ")
                .append("o.ID, ")
                .append("o.NOMBRE, ")
                .append("o.pagina, ")
                .append("o.ESTATUS_CONTAR, ")
                .append("CASE WHEN o.SI_OPCION is null THEN '' ")
                .append("WHEN o.SI_OPCION is not null THEN (SELECT o1.NOMBRE FROM SI_OPCION o1 WHERE o1.ID = o.SI_OPCION) ")
                .append("END AS padre ")
                .append("FROM ")
                .append("SI_OPCION o, ")
                .append("SI_REL_ROL_OPCION ro ")
                .append("WHERE ")
                .append("o.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' ")
                .append("AND ro.ELIMINADO ='").append(Constantes.ELIMINADO).append("' ")
                .append("AND o.SI_MODULO= ").append(idSiModulo)
                .append(" AND o.ID = ro.SI_OPCION ")
                .append(" ORDER BY o.POSICION ").append(Constantes.ORDER_BY_ASC);

        Query q = em.createNativeQuery(query.toString());

        List<Object[]> result = q.getResultList();
        List<SiOpcionVo> list = new ArrayList<SiOpcionVo>();

        for (Object[] objects : result) {
            list.add(castAllOpcionVO(objects, 1));
        }

        clearQuery();

        query.append("SELECT ")
                .append("o.ID, ")
                .append("o.NOMBRE, ")
                .append("o.pagina, ")
                .append("o.ESTATUS_CONTAR, ")
                .append("CASE WHEN o.SI_OPCION is null THEN '' ")
                .append("WHEN o.SI_OPCION is not null THEN (SELECT o1.NOMBRE FROM SI_OPCION o1 WHERE o1.ID = o.SI_OPCION) ")
                .append("END AS padre ")
                .append("FROM ")
                .append("SI_OPCION o ")
                .append("WHERE ")
                .append(" NOT EXISTS (SELECT ro.SI_OPCION FROM SI_REL_ROL_OPCION ro WHERE ro.SI_OPCION = o.ID ")
                .append("AND ro.ELIMINADO ='").append(Constantes.NO_ELIMINADO).append("' ) ")
                .append("AND o.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' ")
                .append("AND o.SI_MODULO= ").append(idSiModulo)
                .append(" ORDER BY o.POSICION ").append(Constantes.ORDER_BY_ASC);

        Query q1 = em.createNativeQuery(query.toString());

        List<Object[]> result1 = q1.getResultList();

        for (Object[] objects : result1) {
            list.add(castAllOpcionVO(objects, 1));
        }

        return list;
    }

    public SiOpcionVo castOpcionVO(Object[] objects, String sesion) {
        SiOpcionVo vo = new SiOpcionVo();
        vo.setId((Integer) objects[0]);
        vo.setNombre((String) objects[1]);
        vo.setPagina((String) objects[2]);
        vo.setPaginaListener((String) objects[3]);
        vo.setEstatusContar(objects[4] != null ? (Integer) objects[4] : 0);
        vo.setPosicion(objects[5] != null ? (Integer) objects[5] : 0);
        vo.setIdPadre((Integer) objects[6]);
        if (vo.getEstatusContar() > 0) {
            vo.setPendiente(trabajoPendiente(vo.getEstatusContar(), sesion));

        }
        return vo;
    }

    public long trabajoPendiente(Integer estatus, String usuario) { //aqui es el cambio de el menu
        long total;
        switch (estatus) {
            case Constantes.ESTATUS_PENDIENTE:
                total = totalSgSolicitudViaje401(usuario);
                break;
            case Constantes.SOLICITUDES_TERRESTRE_CIUDAD:
                total = totalSgSolicitudViaje401Ciudad(usuario);
                break;
            case Constantes.SOLICITUDES_AEREA:
                total = totalSgSolicitudViajeToAereos(usuario);
                break;
            case Constantes.CONTAR_CAMBIOS_ITINERARIO:
                total = totalCambiosItinerario();
                break;
            case Constantes.REQUISICION_VISTO_BUENO:
                total = totalSolEstancia(usuario);
                break;
            case Constantes.ESTATUS_JUSTIFICAR:
                total = totalSolAproJustificacion(usuario);
                break;
            case Constantes.ESTATUS_APROBAR:
                total = totalSolicitudesPorEstatus(estatus, usuario);
                break;
            default:
                total = totalSolicitudesPorEstatus(estatus, usuario);
                break;
        }

        return total;
    }

    private long totalSolAproJustificacion(String usuario) {
        return sgEstatusAprobacionRemote.contarAprobacionesPorRolPendientes(Constantes.ESTATUS_JUSTIFICAR, usuario, Constantes.ROL_JUSTIFICA_VIAJES);
    }

    private long totalSolEstancia(String usuario) {
        return sgSolicitudEstanciaRemote.totalSolicituesPorAprobar(usuario);
    }

    public int totalCambiosItinerario() {
        try {
            return sgCambioItinerarioRemote.getTotalCambiosItinerario();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepción en traer total de solicitues por darle finalizar .." + e.getMessage());
            return 0;
        }

    }

    public int totalSgSolicitudViajeToAereos(String sesion) {
        return this.sgSolicitudViajeRemote.totalSgSolicitudViajeToAereos(sesion, Constantes.ESTATUS_PENDIENTE);
    }

    public int totalSgSolicitudViaje401(String sesion) {
        UtilLog4j.log.info(this, "getTotalSgSolicitudViaje401");

        int totalSVTerrestreOficina = sgSolicitudViajeRemote.totalSgSolicitudViajeTerretreToOficina(sesion, Constantes.ESTATUS_PENDIENTE);
        int totalSVTerrestreCiudad = sgSolicitudViajeRemote.totalSgSolicitudViajeTerrestreToCiudad(sesion, Constantes.ESTATUS_PENDIENTE);
        int totalSVAereo = sgSolicitudViajeRemote.totalSgSolicitudViajeToAereos(sesion, Constantes.ESTATUS_PENDIENTE);

        return totalSVTerrestreOficina + totalSVTerrestreCiudad + totalSVAereo;
    }

    public int totalSgSolicitudViaje401Ciudad(String sesion) {
        return sgSolicitudViajeRemote.getTotalSgSolicitudViajeCiudadByEstatusAndUsuario(sesion, Constantes.ESTATUS_PENDIENTE);
    }

    /**
     *
     * @param estatus
     * @param sesion
     * @return
     */
    public int totalSolicitudesPorEstatus(Integer estatus, String sesion) {
        int retVal = 0;
        UtilLog4j.log.info(this, "getTotalSolicitudesPorFinalizar");
        try {
            retVal = sgEstatusAprobacionRemote.getTotalSolicitudesPorEstatus(sesion, estatus);
        } catch (Exception e) {
            LOGGER.fatal(this, "Excepción en traer total de solicitues por darle finalizar .." + e.getMessage(), e);
        }
        return retVal;
    }

    public SiOpcionVo castAllOpcionVO(Object[] objects, int tipo) {
        SiOpcionVo vo = new SiOpcionVo();
        vo.setId((Integer) objects[0]);
        vo.setNombre((String) objects[1]);
        vo.setPagina((String) objects[2]);
        vo.setEstatusContar((Integer) objects[3] != null ? (Integer) objects[3] : 0);
        if (tipo == 1) {
            vo.setPadre((String) objects[4]);
        } else {
            vo.setIdPadre((Integer) objects[4]);
        }
        vo.setCheck(Boolean.TRUE);
        vo.setEstatusContar((Integer) objects[3]);
        vo.setPosicion(objects[4] != null ? (Integer) objects[4] : 0);
        return vo;
    }

    public SiOpcionVo buscarOpcion(String pagina) {
        SiOpcionVo retVal = null;
        try {
            clearQuery();
            query.append("SELECT  o.ID,  o.NOMBRE,  o.pagina,  o.ESTATUS_CONTAR, o.si_opcion, m.nombre FROM SI_OPCION o ")
                    .append("      inner join si_modulo m on o.si_modulo = m.id ")
                    .append(" WHERE o.pagina = '").append(pagina).append("'")
                    .append(" and o.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            LOGGER.info("Q: opciones: " + query.toString());
            Object[] result = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            retVal = castBuscarOpcion(result);

        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return retVal;
    }

    private SiOpcionVo castBuscarOpcion(Object[] objects) {
        SiOpcionVo vo = new SiOpcionVo();
        vo.setId((Integer) objects[0]);
        vo.setNombre((String) objects[1]);
        vo.setPagina((String) objects[2]);
        vo.setEstatusContar(objects[3] != null ? (Integer) objects[3] : 0);
        vo.setPosicion(objects[4] != null ? (Integer) objects[4] : 0);
        vo.setModulo((String) objects[5]);
        return vo;
    }

    public List<SiOpcionVo> getSiOpcionByRol(String Rol) {
        List<SiOpcionVo> list = null;

        try {
            clearQuery();
            query.append(" SELECT o.ID, o.NOMBRE, o.PAGINA, ")
                    .append(" o.ESTATUS_CONTAR, o.POSICION, ")
                    .append(" o.SI_MODULO ")
                    .append(" FROM (SI_OPCION o ")
                    .append(" INNER JOIN SI_REL_ROL_OPCION re ON  ")
                    .append(" o.ID = re.SI_OPCION) ")
                    .append(" INNER JOIN SI_ROL ro ON ")
                    .append(" re.SI_ROL = ro.ID ")
                    .append(" AND ro.NOMBRE = ?1")
                    //            query.append(Rol);
                    //            query.append("'");
                    .append(" AND o.ELIMINADO = 'False'")
                    .append(" AND ro.SI_MODULO = o.SI_MODULO");

            Query q = em.createNativeQuery(query.toString());
            q.setParameter(1, Rol);

            List<Object[]> result = q.getResultList();
            list = new ArrayList<SiOpcionVo>();

            for (Object[] objects : result) {
                list.add(castBuscarOpcion(objects));
            }
        } catch (Exception e) {
            LOGGER.fatal(e);

            list = Collections.EMPTY_LIST;
        }

        //return (list != null ? list : Collections.EMPTY_LIST);
        return list;
    }

    public List<SiOpcionVo> getSiOpcionBySiOpcionPadre(int siMoudulo) { //regresa las opciones padre x el modulo
        clearQuery();
        query.append("SELECT o.ID, o.NOMBRE, ")
                .append("o.PAGINA, o.PAGINALISTENER, o.ESTATUS_CONTAR, ")
                .append("o.POSICION, case when o.SI_OPCION is null then 0 else o.SI_OPCION end ")
                .append("FROM SI_OPCION o ")
                .append("WHERE o.SI_OPCION is null AND o.SI_MODULO = ")
                .append(siMoudulo)
                .append(" AND o.POSICION = 0 ")
                .append("AND o.ELIMINADO = 'False'");
        Query q = em.createNativeQuery(query.toString());
        List<Object[]> result = q.getResultList();
        List<SiOpcionVo> list = new ArrayList<SiOpcionVo>();

        for (Object[] objects : result) {
            list.add(castOpcionVO(objects, null));
        }

        return (list != null ? list : Collections.EMPTY_LIST);
    }

    public List<SiOpcionVo> getSiOpcionByRol() {//regresa opciones padres todas
        clearQuery();
        query.append(" SELECT o.ID, o.NOMBRE, ");
        query.append(" o.PAGINA, ");
        query.append(" o.ESTATUS_CONTAR, o.POSICION, ");
        query.append(" m.nombre  ");
        query.append(" FROM SI_OPCION o ");
        query.append(" inner join SI_MODULO m on m.id = o.SI_MODULO ");
        query.append("WHERE o.SI_OPCION is null ");
        query.append(" AND o.POSICION = 0 ");
        query.append("AND O.ELIMINADO = 'False'");
        Query q = em.createNativeQuery(query.toString());
        List<Object[]> result = q.getResultList();
        List<SiOpcionVo> list = new ArrayList<SiOpcionVo>();

        for (Object[] objects : result) {
            list.add(castBuscarOpcion(objects));
        }

        return (list != null ? list : Collections.EMPTY_LIST);

    }

    public int maxPosicionBySiOpcion(int siOpcion) { //regresa la posicion maxima
        clearQuery();
        query.append("SELECT MAX(o.POSICION) ");
        query.append("FROM SI_OPCION o ");
        query.append("WHERE o.ELIMINADO = 'False' ");
        query.append("AND o.SI_OPCION = ");
        query.append(siOpcion);
        int posicion = 0;
        if (em.createNativeQuery(query.toString()).getSingleResult() != null) {
            posicion = Integer.parseInt(em.createNativeQuery(query.toString()).getSingleResult().toString()) + 1;
        } else {
            posicion = 1;
        }
        return posicion;
    }

    public List<SiOpcionVo> getSiOpcionBySiOpcion() {//regresa opciones x si_opcion todas
        List<SiOpcionVo> list = new ArrayList<SiOpcionVo>();
        String siOpcion = "is NOT null AND o.posicion >0 ";
        list = getSiOpcionBySiOpcion(siOpcion);
        return list;
    }

    public List<SiOpcionVo> getSiOpcionBySiOpcion(String siOpcion) { //regresa opciones x si_opcion
        clearQuery();
        String value = null;
        if (siOpcion.equals("is NOT null AND o.posicion >0 ")) {
            value = siOpcion;
        } else {
            value = "= " + siOpcion;
        }

        query.append("SELECT o.ID, o.NOMBRE, o.PAGINA, o.ESTATUS_CONTAR, o.SI_OPCION ");
        query.append("FROM SI_OPCION o ");
        query.append("WHERE o.si_opcion ");
        query.append(value);
        query.append(" AND o.ELIMINADO = 'False'");
        //    query.append(" AND o.posicion>0");

        Query q = em.createNativeQuery(query.toString());

        List<Object[]> result = q.getResultList();
        List<SiOpcionVo> list = new ArrayList<SiOpcionVo>();

        for (Object[] objects : result) {
            list.add(castAllOpcionVO(objects, 0));
        }

        return (list != null ? list : Collections.EMPTY_LIST);

    }

    public List<SiOpcionVo> traerOpciones() {
        String sb = "           WITH RECURSIVE arbol_opciones AS (\n"
                + "		        SELECT id,\n"
                + "		               lpad(cast(id as varchar(4)), 4, '0')||'-'||CAST(nombre As varchar(2000)) As nombre \n"
                + "		            , SI_OPCION, PAGINA, PAGINALISTENER, POSICION, ESTATUS_CONTAR \n"
                + "		        FROM SI_OPCION \n"
                + "		        WHERE SI_OPCION IS NULL\n"
                + "		        and si_modulo = 9\n"
                + "			   and eliminado =  'False' \n"
                + "		UNION ALL\n"
                + "		        SELECT si.id,\n"
                + "		                CAST(sp.nombre || '->' || lpad(cast(si.id as varchar(4)), 4, '0')||'-'||si.nombre As varchar(1000)) As nombre \n"
                + "		                ,si.si_opcion, PAGINA, PAGINALISTENER, POSICION, ESTATUS_CONTAR  \n"
                + "		        FROM SI_OPCION As si \n"
                + "		                INNER JOIN arbol_opciones AS sp \n"
                + "		                ON (si.si_opcion = sp.id) and si.eliminado = 'False' \n"
                + "		                where si.si_modulo = 9\n"
                + "		)\n"
                + "		SELECT id, nombre,SI_OPCION, PAGINA, PAGINALISTENER, POSICION, ESTATUS_CONTAR   \n"
                + "		FROM arbol_opciones \n"
                + "		ORDER BY nombre";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<SiOpcionVo> l = new ArrayList<SiOpcionVo>();
        for (Object[] lo1 : lo) {
            SiOpcionVo siOpcionVo = new SiOpcionVo();
            siOpcionVo.setId((Integer) lo1[0]);
            siOpcionVo.setNombre((String) lo1[1]);
            siOpcionVo.setIdPadre(lo1[2] != null ? (Integer) lo1[2] : 0);
            l.add(siOpcionVo);
        }
        return l;
    }

    @SuppressWarnings({"BoxedValueEquality", "NumberEquality"})
    public List<SiOpcionVo> getAllSiOpcionBySiModuloByUser(int idSiModulo, String idUsuario, int campo) {
        StringBuilder sb = new StringBuilder();
        sb.append("select DISTINCT o.ID, o.NOMBRE, o.pagina, o.PAGINALISTENER, o.ESTATUS_CONTAR, o.POSICION,"
                + " case when o.SI_OPCION is null then 0 else o.SI_OPCION end as SSIOPCION "
                + " from si_opcion o"
                + " inner join si_rel_rol_opcion r on r.SI_OPCION = O.ID AND R.ELIMINADO = ?"//1
                + " inner join si_usuario_rol u on r.SI_ROL = u.SI_ROL and u.ELIMINADO = ?"//2
                + " where o.SI_MODULO = ?"//3
                + " and o.ELIMINADO = ?"//4
                + " and u.USUARIO = ?"//5
                + " and u.AP_CAMPO = ?"//6
                + " and u.SI_ROL in(select id from si_rol  where SI_MODULO = ? and ELIMINADO = ?)"//7 8 
                + " ORDER BY SSIOPCION asc , o.POSICION asc");

        List<Object[]> result = em.createNativeQuery(sb.toString())
                .setParameter(1, Constantes.NO_ELIMINADO)
                .setParameter(2, Constantes.NO_ELIMINADO)
                .setParameter(3, idSiModulo)
                .setParameter(4, Constantes.NO_ELIMINADO)
                .setParameter(5, idUsuario)
                .setParameter(6, campo)
                .setParameter(7, idSiModulo)
                .setParameter(8, Constantes.NO_ELIMINADO)
                .getResultList();
        List<SiOpcionVo> list = new ArrayList<SiOpcionVo>();
        List<Integer> newPadre = new ArrayList<Integer>();

        for (Object[] objects : result) {
            SiOpcionVo vo = castOpcionVO(objects, idUsuario);
            if (vo.getIdPadre() == 0) {
                list.add(vo);
            } else {

                for (SiOpcionVo op : list) {
                    if (vo.getIdPadre().equals(op.getId())) {
                        op.getListaOpciones().add(vo);
                        break;
                    }
                }
            }

        }
        if (newPadre.size() > 0) {
            if (newPadre.size() == 1) {

            } else {
                for (SiOpcionVo o : list) {
                    for (Integer i : newPadre) {
                        if (o.getId().equals(i)) {
                            list.remove(o);
                        }
                    }
                }
            }
        }
        UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SiOpcion");

        return list;
    }

    public SiOpcionVo buscarOpcionPorUltimaParteURL(String lastPart) {
        SiOpcionVo retVal = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT  o.ID,  o.NOMBRE,  o.pagina,  o.ESTATUS_CONTAR, o.si_opcion, m.nombre FROM SI_OPCION o ")
                    .append("      inner join si_modulo m on o.si_modulo = m.id ")
                    .append(" WHERE o.pagina like '%").append(lastPart).append("%'")
                    .append(" and o.ELIMINADO = false ");
            LOGGER.info("Q: opcion {}: " + sb.toString());
            Object[] result = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            retVal = castBuscarOpcion(result);

        } catch (Exception e) {
            LOGGER.error("", e.getMessage());
        }
        return retVal;
    }

}
