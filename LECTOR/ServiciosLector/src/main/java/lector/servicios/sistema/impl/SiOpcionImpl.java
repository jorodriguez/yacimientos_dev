/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lector.constantes.Constantes;
import lector.dominio.modelo.usuario.vo.UsuarioVO;
import lector.modelo.SiOpcion;
import lector.servicios.sistema.vo.MenuSiOpcionVo;
import lector.servicios.sistema.vo.SiOpcionVo;
import lector.sistema.AbstractImpl;
import lector.util.UtilLog4j;

/**
 *
 */
@Stateless
public class SiOpcionImpl extends AbstractImpl<SiOpcion> {

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    @PersistenceContext(unitName =  Constantes.PERSISTENCE_UNIT)
    private EntityManager em;

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

    public List<MenuSiOpcionVo> getListaMenu(UsuarioVO usuario) {
        
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

        }
        return vo;
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
