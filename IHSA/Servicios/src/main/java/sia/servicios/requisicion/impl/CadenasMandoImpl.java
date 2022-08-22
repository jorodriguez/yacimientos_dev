/*
 * CadenasMandoImpl.java
 * Creado el 7/07/2009, 08:47:52 AM
 * EJB sin estado desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este EJB sin estado (Stateless Session EJB), asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: hacosta.0505@gmail.com
 */
package sia.servicios.requisicion.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.ApCampo;
import sia.modelo.CadenasMando;
import sia.modelo.Usuario;
import sia.modelo.cadena.aprobacion.vo.CadenaAprobacionVo;
import sia.modelo.requisicion.vo.CadenasMandoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com @date 7/07/2009
 */
@Stateless
public class CadenasMandoImpl extends AbstractFacade<CadenasMando> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    //    

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CadenasMandoImpl() {
        super(CadenasMando.class);
    }

    @Inject
    UsuarioImpl usuarioImpl;

    public boolean registroCadenaMando(int idApCampo, String solicita, String revisa, String aprueba, String idUsuario) {
        UtilLog4j.log.info(this, "CadenasMandoImpl.registroCadenaMando()");
        boolean v;
        try {
            CadenasMando cadenasMando = new CadenasMando();
            cadenasMando.setUsuario(usuarioImpl.buscarPorNombre(solicita));
            cadenasMando.setRevisa(usuarioImpl.buscarPorNombre(revisa));
            cadenasMando.setAprueba(usuarioImpl.buscarPorNombre(aprueba));
            cadenasMando.setApCampo(new ApCampo(idApCampo));
            cadenasMando.setGenero(new Usuario(idUsuario));
            cadenasMando.setFechaGenero(new Date());
            cadenasMando.setHoraGenero(new Date());
            cadenasMando.setEliminado(Constantes.NO_ELIMINADO);
            create(cadenasMando);
            UtilLog4j.log.info(this, "CadenasMando CREATED SUCCESSFULLY");

            v = true;
        } catch (Exception e) {
            v = false;
        }
        return v;
    }

    public boolean completarModificacion(int idCad, String revisa, String aprueba, String idUsuario) {
        UtilLog4j.log.info(this, "CadenasMandoImpl.completarModificacion()");
        boolean v;
        try {
            CadenasMando cadenasMando = this.find(idCad);
            if (revisa != null && !revisa.isEmpty()) {
                cadenasMando.setRevisa(usuarioImpl.buscarPorNombre(revisa));
            }
            if (aprueba != null && !aprueba.isEmpty()) {
                cadenasMando.setAprueba(usuarioImpl.buscarPorNombre(aprueba));
            }
            cadenasMando.setModifico(new Usuario(idUsuario));
            cadenasMando.setFechaModifico(new Date());
            cadenasMando.setHoraModifico(new Date());
            edit(cadenasMando);
            UtilLog4j.log.info(this, "CadenasMando UPDATED SUCCESSFULLY");
            v = true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al modificar: " + e.getMessage() + " + + + " + e.getCause().toString());
            v = false;
        }
        return v;
    }

    public List<CadenasMando> findAll(int idApCampo, boolean eliminado) {
        UtilLog4j.log.info(this, "CadenasMandoImpl.findAll()");

//        return em.createQuery("select object(o) from CadenasMando as o WHERE o.apCampo.id = : idApCampo")
//                .setParameter("idApCampo", idApCampo)
//                .getResultList();
        List<CadenasMando> list = null;
        String consulta = "SELECT object(o) FROM CadenasMando AS o WHERE o.eliminado = :eliminado";

        if (idApCampo > 0) {
            consulta += " AND o.apCampo.id = :idApCampo";
        }

        Query q = em.createQuery(consulta);

        //Asignando parámetros
        q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));

        if (idApCampo > 0) {
            q.setParameter("idApCampo", idApCampo);
        }

        UtilLog4j.log.info(this, "query CadenasMando: " + q.toString());

        list = q.getResultList();

        UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : null) + " CadenasMando filtrados por idApCampo: " + idApCampo + " eliminado: " + eliminado);

        return list;
    }

    public List<Usuario> getRevisan(Object idUsuario, int idApCampo, boolean eliminado) {
        UtilLog4j.log.info(this, "CadenasMandoImpl.getRevisan()");
        List<Usuario> list = null;

        String qr = "SELECT DISTINCT c.revisa FROM CadenasMando as c WHERE c.usuario.id = :idUsuario AND c.eliminado = :eliminado";

        if (idApCampo > 0) {
            qr += " AND c.apCampo.id = :idApCampo";
        }

        Query q = em.createQuery(qr);

        //Asignando parámetros
        q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));
        q.setParameter("idUsuario", idUsuario);

        if (idApCampo > 0) {
            q.setParameter("idApCampo", idApCampo);
        }

        UtilLog4j.log.info(this, "query CadenasMando: " + q.toString());

        list = q.getResultList();

        UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : null) + " Usuarios que REVISAN para el Usuario: " + idUsuario + " filtradas por idApCampo: " + idApCampo + " eliminado: " + eliminado);

        return list;
    }

    public List<Usuario> getAprueban(Object idUsuario, int idApCampo, boolean eliminado) {
        UtilLog4j.log.info(this, "CadenasMandoImpl.getAprueban()");
        List<Usuario> list = null;

        String qr = "SELECT DISTINCT c.aprueba FROM CadenasMando as c WHERE c.usuario.id = :idUsuario AND c.eliminado = :eliminado";

        if (idApCampo > 0) {
            qr += " AND c.apCampo.id = :idApCampo";
        }

        Query q = em.createQuery(qr);

        //Asignando parámetros
        q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));
        q.setParameter("idUsuario", idUsuario);

        if (idApCampo > 0) {
            q.setParameter("idApCampo", idApCampo);
        }

        UtilLog4j.log.info(this, "query CadenasMando: " + q.toString());

        list = q.getResultList();

        UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : null) + " Usuarios que APRUEBAN para el Usuario: " + idUsuario + " filtradas por idApCampo: " + idApCampo + " eliminado: " + eliminado);

        return list;
    }

    public List<CadenasMandoVo> traerUsuarioRevisa(String solicita, int idApCampo, boolean eliminado) {
        clearQuery();
        query.append("select cm.id, cm.revisa, (select u.nombre from usuario u where u.id = cm.revisa) from cadenas_mando cm, ap_campo c");
        query.append(" where cm.usuario = '").append(solicita).append("'");
        query.append(" and cm.ap_campo = ").append(idApCampo);
        query.append(" and cm.ap_campo = c.id ");
        query.append(" and cm.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<CadenasMandoVo> lr = null;
        if (lo != null) {
            lr = new ArrayList<CadenasMandoVo>();
            for (Object[] objects : lo) {
                CadenasMandoVo cadenasMAndoVo = new CadenasMandoVo();
                cadenasMAndoVo.setId((Integer) objects[0]);
                cadenasMAndoVo.setIdRevisa((String) objects[1]);
                cadenasMAndoVo.setRevisa((String) objects[2]);
                lr.add(cadenasMAndoVo);
            }
        }
        return lr;
    }

    public CadenaAprobacionVo traerPorId(int idCadMando) {
        clearQuery();
        query.append("select cm.id, u.id, u.nombre, r.id, r.nombre, a.id, a.nombre from cadenas_mando cm");
        query.append(" inner join usuario u on cm.usuario = u.id ");
        query.append(" inner join usuario r on cm.revisa = r.id ");
        query.append(" inner join usuario a on cm.aprueba = a.id ");
        query.append(" where cm.id = ").append(idCadMando);
        //
        Object[] objects = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
        CadenaAprobacionVo lr = null;
        if (objects != null) {
            lr = new CadenaAprobacionVo();
            lr.setId((Integer) objects[0]);
            lr.setIdSolicita((String) objects[1]);
            lr.setSolicita((String) objects[2]);
            lr.setIdRevisa((String) objects[3]);
            lr.setRevisa((String) objects[4]);
            lr.setIdAprueba((String) objects[5]);
            lr.setAprueba((String) objects[6]);
        }
        return lr;
    }

    public List<CadenasMandoVo> traerUsuarioAprueba(String solicita, String revisa, int idApCampo, boolean eliminado) {
        clearQuery();
        query.append("select cm.id, cm.aprueba, (select u.nombre from usuario u where u.id = cm.aprueba) from cadenas_mando cm, ap_campo c");
        query.append(" where cm.usuario = '").append(solicita).append("'");
        query.append(" and cm.revisa = '").append(revisa).append("'");
        query.append(" and cm.ap_campo = ").append(idApCampo);
        query.append(" and cm.ap_campo = c.id ");
        query.append(" and cm.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<CadenasMandoVo> lr = null;
        if (lo != null) {
            lr = new ArrayList<CadenasMandoVo>();
            for (Object[] objects : lo) {
                CadenasMandoVo cadenasMAndoVo = new CadenasMandoVo();
                cadenasMAndoVo.setId((Integer) objects[0]);
                cadenasMAndoVo.setIdAprueba((String) objects[1]);
                cadenasMAndoVo.setAprueba((String) objects[2]);
                lr.add(cadenasMAndoVo);
            }
        }
        return lr;
    }

    //NUEVOS
    public List<CadenaAprobacionVo> traerCadenaAprobacion(String idUsuario, int idApCampo, int idOrdena, boolean eliminado, boolean revisa, boolean aprueba) {
        UtilLog4j.log.info(this, "CadenasMandoImpl.traerCadenaAprobacion()");
        //Recupera todas las cadenas de aprobacion 
        StringBuilder q = new StringBuilder();
        q.append("select c.id,  c.ap_campo,   c.usuario,   u.nombre,  c.revisa,   r.nombre,"
                + " c.aprueba, "
                + " a.nombre "
                + " from cadenas_mando c, usuario u, usuario r, usuario a, ap_campo  cp  "
                + " where c.usuario = u.id "
                + " and c.revisa = r.id "
                + " and c.aprueba = a.id   "
                + " and c.ap_campo = cp.id "
                + " and cp.eliminado = 'False' "
                + " and c.eliminado = 'False'"
                + " and u.eliminado = 'False'"
                + " and c.usuario is not null "
                + " and c.revisa is not null "
                + " and c.aprueba is not null "
                + " and c.ap_campo = ?");

        String ordena = null;
        if (idUsuario != null) {
            ordena = "usuario";
            if (revisa && aprueba) {
                q.append(" and (r.id = ? or a.id = ?) ");
            } else if (revisa) {
                q.append(" and r.id = ?");
            } else if (aprueba) {
                q.append(" and a.id = ?");
            } else {
                if (idOrdena == 1) {
                    ordena = "usuario";
                    q.append(" and u.id = ?"); // Filtra por usuario + operacion
                } else if (idOrdena == 2) {
                    ordena = "revisa";
                    q.append(" and r.id = ?");
                } else {
                    ordena = "aprueba";
                    q.append(" and a.id = ?");
                }
            }

        } else {
            if (idOrdena == 1) {
                ordena = "usuario";
            } else if (idOrdena == 2) {
                ordena = "revisa";
            } else {
                ordena = "aprueba";
            }
        }
        q.append(" order by c.").append(ordena).append(" ASC"); //Ordena por operacion
        Query query2 = em.createNativeQuery(q.toString())
                .setParameter(1, idApCampo);
        if (idUsuario != null) {
            query2.setParameter(2, idUsuario);
        }
        if (revisa && aprueba) {
            query2.setParameter(3, idUsuario);
        }

        UtilLog4j.log.info(this, "Query cadena mando: " + query2.toString());
        List<Object[]> lo = query2.getResultList();
        List<CadenaAprobacionVo> lc = new ArrayList<CadenaAprobacionVo>();
        for (Object[] objects : lo) {
            lc.add(castCadenaVO(objects));
        }
        UtilLog4j.log.info(this, "Lista : " + lc.size());
        return lc;
//
//        List<CadenasMando> list = null;
//
////        String query = "SELECT c FROM CadenasMando c "
////                + " WHERE c.usuario.nombre = :user AND c.eliminado = :eliminado OR c.revisa.nombre = :rev OR c.aprueba.nombre = :ap";
//
////        if (idApCampo > 0) {
////            query += " AND c.apCampo.id = :idApCampo";
////        }
////
////        query += " ORDER BY c.usuario.nombre ASC";
////
////        Query q = em.createQuery(query);
//
//        //Asignando parámetros
//        q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));
//        q.setParameter("user", idUsuario);
//        q.setParameter("rev", idUsuario);
//        q.setParameter("ap", idUsuario);

//        if (idApCampo > 0) {
//            q.setParameter("idApCampo", idApCampo);
//        }
//
//        UtilLog4j.log.info(this,"query CadenasMando: " + q.toString());
//
//        list = q.getResultList();
//
//        UtilLog4j.log.info(this,"Se encontraron " + (list != null ? list.size() : null) + " CadenasAprobación para el Usuario: " + idUsuario + " filtradas por idApCampo: " + idApCampo + " eliminado: " + eliminado);
//
//        return list;
    }

    private CadenaAprobacionVo castCadenaVO(Object[] obj) {
        CadenaAprobacionVo cadenaAprobacionVo = new CadenaAprobacionVo();
        cadenaAprobacionVo.setId((Integer) obj[0]);
        cadenaAprobacionVo.setIdCampo((Integer) obj[1]);
        cadenaAprobacionVo.setIdSolicita((String) obj[2]);
        cadenaAprobacionVo.setSolicita((String) obj[3]);
        cadenaAprobacionVo.setIdRevisa((String) obj[4]);
        cadenaAprobacionVo.setRevisa((String) obj[5]);
        cadenaAprobacionVo.setIdAprueba((String) obj[6]);
        cadenaAprobacionVo.setAprueba((String) obj[7]);
        cadenaAprobacionVo.setSelected(false);
        return cadenaAprobacionVo;
    }

    public List<CadenasMando> traerPorCadenaPorSolicita(String solicita, int idApCampo, boolean eliminado) {
        UtilLog4j.log.info(this, "CadenasMandoImpl.traerPorCadenaPorSolicita()");
        try {
//            return em.createQuery("SELECT c FROM CadenasMando c WHERE c.usuario.nombre = :user ORDER BY c.usuario.nombre ASC")
//                    .setParameter("user", solicita)
//                    .getResultList();

            List<CadenasMando> list = null;

            String consulta = "SELECT c FROM CadenasMando c WHERE c.usuario.nombre = :user AND c.eliminado = :eliminado";

            if (idApCampo > 0) {
                consulta += " AND c.apCampo.id = :idApCampo";
            }

            consulta += " ORDER BY c.usuario.nombre ASC";

            Query q = em.createQuery(consulta);

            //Asignando parámetros
            q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));
            q.setParameter("user", solicita);

            if (idApCampo > 0) {
                q.setParameter("idApCampo", idApCampo);
            }

            UtilLog4j.log.info(this, "query CadenasMando: " + q.toString());

            list = q.getResultList();

            UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : null) + " CadenasAprobación para el Usuario: " + solicita + " filtradas por idApCampo: " + idApCampo + " eliminado: " + eliminado);

            return list;

        } catch (Exception e) {
            return null;
        }
    }

    public void eliminar(int idCadena, String idUser) {
        CadenasMando cadenasMando = find(idCadena);
        cadenasMando.setEliminado(Constantes.ELIMINADO);
        cadenasMando.setModifico(new Usuario(idUser));
        cadenasMando.setFechaModifico(new Date());
        cadenasMando.setHoraModifico(new Date());
        edit(cadenasMando);
        //
    }
}
