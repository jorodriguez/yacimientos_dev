/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgTipoSolicitudViaje;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.TipoSolicitudViajeVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@LocalBean 
public class SgTipoSolicitudViajeImpl extends AbstractFacade<SgTipoSolicitudViaje>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SgTipoEspecificoImpl tipoEspecificoService;
    @Inject
    private SgTipoImpl tipoService;
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgTipoSolicitudViajeImpl() {
        super(SgTipoSolicitudViaje.class);
    }

    
    public SgTipoSolicitudViaje findToNombre(String nombre, boolean eliminado) {
        UtilLog4j.log.info(this,"sgTipoSolicitudViajeImpl.findToNombre()");
        SgTipoSolicitudViaje tipoSolicitud = null;

        if (nombre != null && !nombre.equals("")) {
            try {
                tipoSolicitud = (SgTipoSolicitudViaje) em.createQuery("SELECT t FROM SgTipoSolicitudViaje t WHERE t.eliminado = :estado AND t.nombre = :nombre").setParameter("eliminado", eliminado).setParameter("nombre", nombre).getSingleResult();
            } catch (NonUniqueResultException nure) {
                UtilLog4j.log.fatal(this,nure.getMessage());
                UtilLog4j.log.fatal(this,"Se encontró más de un resultado para Tipo por Nombre");
            }
        }
        return tipoSolicitud;
    }

    
    public void crearTipoSolicitud(SgTipoSolicitudViaje tipoSolicitud, int idTipoEspecifico, Usuario usuarioGenero) {
        UtilLog4j.log.info(this,"crearTipoSolicitud");
        try {
            tipoSolicitud.setEliminado(Constantes.BOOLEAN_FALSE);
            tipoSolicitud.setFechaGenero(new Date());
            tipoSolicitud.setHoraGenero(new Date());
            tipoSolicitud.setGenero(usuarioGenero);
            tipoSolicitud.setSgTipoEspecifico(tipoEspecificoService.find(idTipoEspecifico));
            tipoSolicitud.setSgTipo(tipoService.find(5));
            create(tipoSolicitud);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion en Crear tipo Solicitud en servicios" + e.getMessage());
        }
    }

    
    public void modificarTipoSolicitud(SgTipoSolicitudViaje tipoSolicitud, int idTipoEspecifico, Usuario usuarioModifico) {
        try {
            tipoSolicitud.setFechaModifico(new Date());
            tipoSolicitud.setHoraModifico(new Date());
            tipoSolicitud.setModifico(usuarioModifico);
            tipoSolicitud.setSgTipoEspecifico(tipoEspecificoService.find(idTipoEspecifico));
            super.edit(tipoSolicitud);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion en modificar tipo Solicitud " + e.getMessage());
        }
    }

    
    public void eliminarTipoSolicitud(SgTipoSolicitudViaje tipoSolicitud, Usuario usuarioModifico) {
        try {
            tipoSolicitud.setEliminado(Constantes.BOOLEAN_TRUE);
            tipoSolicitud.setModifico(usuarioModifico);
            tipoSolicitud.setFechaModifico(new Date());
            tipoSolicitud.setHoraModifico(new Date());

            //Enviar a el log
            super.edit(tipoSolicitud);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion en eliminar tipo Solicitud " + e.getMessage());
        }
    }

    
    public List<SgTipoSolicitudViaje> findAllTipoSolicitud(int idTipoEspecifico) {
        UtilLog4j.log.info(this,"findAllTipoSolicitud(idTipoEspecifico)");
        try {
            return em.createQuery("SELECT t FROM SgTipoSolicitudViaje t "
                    + " WHERE t.eliminado = :eli "
                    + " AND t.sgTipoEspecifico.id = :idTipoEspecifico"
                    + " ORDER BY t.sgTipoEspecifico.id ASC").setParameter("idTipoEspecifico", idTipoEspecifico).setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion al traer los tipos de solicitudes " + e.getMessage());
            return null;
        }
    }

    
    public List<TipoSolicitudViajeVO> findAllTipoSolicitud() {
        UtilLog4j.log.info(this,"findAllTipoSolicitud");
        String q = "";
        TipoSolicitudViajeVO o;
        List<TipoSolicitudViajeVO> lo = new ArrayList<TipoSolicitudViajeVO>();
        try {
//            q = "SELECT ts.id,tEsp.nombre AS nombreTipoEspecifico, "
//                    + "ts.nombre AS nombreTipoSolicitud, "
//                    + "ts.horas_anticipacion AS horasAnticipacion "
//                    + "FROM SG_TIPO_SOLICITUD_VIAJE AS ts, "
//                    + "SG_TIPO_ESPECIFICO AS "
//                    + "WHERE ts.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "' "
//                    + " AND ts.SG_TIPO_ESPECIFICO = tEsp.id ";
//            if (orderByField.equals("tipoEspecifico")) {
//                q += " ORDER BY tEsp.'" + orderByField + "'";
//            } else {
//                q += " ORDER BY ts.'" + orderByField + "' ";
//            }
//            if (sortAscending) {
//                q += " ASC";
//            } else {
//                q += " DESC";
//            }
//            //convertir a lista    
//            Query query = em.createNativeQuery(q);
//            TipoSolicitudViajeVO o;
//            List<Object[]> l = query.getResultList();
//            for (Object[] objects : l) {
//                o = new TipoSolicitudViajeVO();
//                o.setId((Integer) objects[0]);
//                o.setTipoEspecifico(String.valueOf(objects[1]));
//                o.setNombreSolicitud(String.valueOf(objects[2]));
//                o.setHorasAnticipacion((Integer) objects[3]);
//                lo.add(o);
//            }
//            return lo;
//            
//           return em.createQuery("SELECT t FROM SgTipoSolicitudViaje t "
//                   + " WHERE t.eliminado = :eli "
//                   + " ORDER BY t.sgTipoEspecifico.id ASC")
//                   .setParameter("eli", Constantes.BOOLEAN_FALSE)                                      
//                   .getResultList();

            q = " SELECT tsv.ID," //0
                    + " tsv.NOMBRE, "//1
                    + " tsv.HORAS_ANTICIPACION,"//2
                    + " te.ID,"//3
                    + " te.NOMBRE "//4
                    + " FROM SG_TIPO_SOLICITUD_VIAJE  tsv,SG_TIPO_ESPECIFICO te "
                    + " WHERE tsv.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " AND tsv.SG_TIPO_ESPECIFICO = te.ID"
                    + " ORDER BY te.ID ";
            Query query = em.createNativeQuery(q);
            List<Object[]> l = query.getResultList();
            for (Object[] objects : l) {
                o = new TipoSolicitudViajeVO();
                o.setId((Integer) objects[0]);
                o.setNombreSolicitud(String.valueOf(objects[1]));
                o.setHorasAnticipacion((Integer) objects[2]);
                o.setIdTipoEspecifico((Integer) objects[3]);
                o.setTipoEspecifico(String.valueOf(objects[4]));
                lo.add(o);
            }
            return lo;

        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion al traer los tipos de solicitudes " + e.getMessage());
            return null;
        }
    }

    
    public List<SgTipoSolicitudViaje> findAll(String orderByField, String orderByOrder, boolean eliminado) throws Exception {
        UtilLog4j.log.info(this,"findAllTipo");
        UtilLog4j.log.info(this,"SgTipoSolicitudViajeImpl.findAll()");

        List<SgTipoSolicitudViaje> tiposSolicitudesViaje = null;


        String query = "SELECT tsv FROM SgTipoSolicitudViaje tsv WHERE tsv.eliminado = :eliminado";

        if (orderByField != null && !orderByField.equals("") && orderByOrder != null && !orderByOrder.equals("")) {
            query += " ORDER BY tsv." + orderByField + " " + orderByOrder;
        }

        Query q = em.createQuery(query);

        //Asignando parámetros
        q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));

        tiposSolicitudesViaje = q.getResultList();

        UtilLog4j.log.info(this,"Se encontraron " + (!tiposSolicitudesViaje.isEmpty() ? tiposSolicitudesViaje.size() : 0) + " Tipos de Solicitudes de Viaje");

        return tiposSolicitudesViaje;
    }

    
    public boolean buscarTipoSolicitudRepetida(String nombre, int hrs, int tipo) {
        List<SgTipoSolicitudViaje> ret = null;
        boolean bolret = false;
        try {
            ret = em.createQuery("SELECT t FROM SgTipoSolicitudViaje t "
                    + " WHERE  t.nombre = :nombre "
                    + " AND t.horasAnticipacion = :hrs "
                    + " AND t.sgTipoEspecifico.id = :tipo"
                    + " AND t.eliminado = :eli").setParameter("hrs", hrs).setParameter("tipo", tipo).setParameter("nombre", nombre).setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
            if (ret.size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion al buscar el nombre de solicitud repetida " + e.getMessage());
            return false;
        }

    }

//    
//    public boolean buscarTipoSolicitudEnCadenas(int idTipoSolicitud) {
//        SgTipoSolicitudViaje ret = null;
//             boolean bolret = false;
//        try {
//            ret = (SgCadenaApobacion) em.createQuery("SELECT c FROM SgCadenaAprobacion c "
//                   + " WHERE c.sgTipoSolicitudViaje.id = :idTipoSolicitud "                                        
//                    + " AND c.eliminado = :eli")
//                   .setParameter("tipo", idTipoSolicitud)                   
//                   .setParameter("eli", Constantes.BOOLEAN_FALSE)                                      
//                   .getSingleResult();
//            if (ret != null) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception e) {
//            UtilLog4j.log.fatal(this,"Excepcion al buscar el tipo de solicitud ocupada "+e.getMessage());
//            return false;            
//        }
//    }
    
    public List<SgTipoSolicitudViaje> findByTipoEspecificoList(SgTipoEspecifico tipoEspecifico, boolean eliminado) throws Exception {
        UtilLog4j.log.info(this,"SgTipoSolicitudViajeImpl.findByTipoEspecificoList()");

        List<SgTipoSolicitudViaje> tiposSolicitudViaje = null;

        tiposSolicitudViaje = em.createQuery("SELECT tsv FROM SgTipoSolicitudViaje tsv WHERE tsv.sgTipoEspecifico.id = :tipoEspecificoId AND tsv.eliminado = :eliminado").setParameter("tipoEspecificoId", tipoEspecifico.getId()).setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO)).getResultList();

        UtilLog4j.log.info(this,"Se encontraron " + (!tiposSolicitudViaje.isEmpty() ? tiposSolicitudViaje.size() : 0) + " Tipos de Solicitudes de Viaje");

        return tiposSolicitudViaje;
    }

    
    public TipoSolicitudViajeVO buscarPorId(int idSgTipoSolicitudViaje) {
        clearQuery();
        query.append(" SELECT tsv.ID,"); //0
        query.append(" tsv.NOMBRE, ");//1
        query.append(" tsv.HORAS_ANTICIPACION,");//2
        query.append(" case when tsv.hora_maxima is not null then tsv.hora_maxima else 0 end, ");//3
        query.append(" te.ID,");//4
        query.append(" te.NOMBRE ");//5
        query.append(" FROM SG_TIPO_SOLICITUD_VIAJE  tsv, SG_TIPO_ESPECIFICO te ");
        query.append(" WHERE tsv.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        query.append(" AND tsv.SG_TIPO_ESPECIFICO = te.ID");
        query.append(" AND tsv.id = ").append(idSgTipoSolicitudViaje);
        Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();

        if (obj == null) {
            return null;
        } else {
            return castTipoSolicitudVO(obj);
        }
    }

    private TipoSolicitudViajeVO castTipoSolicitudVO(Object[] obj) {
        TipoSolicitudViajeVO tipoSolicitudViajeVO = new TipoSolicitudViajeVO();
        tipoSolicitudViajeVO.setId((Integer) obj[0]);
        tipoSolicitudViajeVO.setNombreSolicitud((String) obj[1]);
        tipoSolicitudViajeVO.setHorasAnticipacion((Integer) obj[2]);
        tipoSolicitudViajeVO.setHoraMaxima((Integer) obj[3]);
        tipoSolicitudViajeVO.setIdTipoEspecifico((Integer) obj[4]);
        tipoSolicitudViajeVO.setTipoEspecifico((String) obj[5]);

        return tipoSolicitudViajeVO;
    }
}
