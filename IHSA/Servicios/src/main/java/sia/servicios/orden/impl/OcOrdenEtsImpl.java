package sia.servicios.orden.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.OcCategoriaEts;
import sia.modelo.OcOrdenEts;
import sia.modelo.Orden;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.orden.vo.OrdenEtsVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

@Stateless 
public class OcOrdenEtsImpl extends AbstractFacade<OcOrdenEts> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcOrdenEtsImpl() {
        super(OcOrdenEts.class);
    }

    
    public boolean crearOcOrdenEts(int orden, int ocCategoriaEts, SiAdjunto siAdjunto, Usuario usuario) {
        try {
            OcOrdenEts ocOrdenEts = new OcOrdenEts();
            ocOrdenEts.setOrden(new Orden(orden));
            ocOrdenEts.setOcCategoriaEts(new OcCategoriaEts(ocCategoriaEts));
            ocOrdenEts.setSiAdjunto(siAdjunto);
            ocOrdenEts.setEliminado(Constantes.BOOLEAN_FALSE);
            ocOrdenEts.setGenero(usuario);
            ocOrdenEts.setFechaGenero(new Date());
            ocOrdenEts.setHoraGenero(new Date());
            create(ocOrdenEts);
            return true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al crear ocCategoria " + e.getMessage());
            return false;
        }
    }

    
    public boolean eliminarOcOrdenEts(int ordenEtsId, String usuarioSesion) {
        UtilLog4j.log.info(this, "eliminarOcOrdenEts");
        try {
            OcOrdenEts ocOrdenEts = find(ordenEtsId);
            ocOrdenEts.setEliminado(Constantes.BOOLEAN_TRUE);
            ocOrdenEts.setFechaModifico(new Date());
            ocOrdenEts.setHoraModifico(new Date());
            ocOrdenEts.setModifico(new Usuario(usuarioSesion));
            edit(ocOrdenEts);
            UtilLog4j.log.info(this, "se elimino el registro de ocOrdenEts");
            return true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al eliminar eliminarOcOrdenEts " + e.getMessage());
            return false;
        }
    }

    
    public List<OcOrdenEts> traerOcOrdenEts(int idOrden, int idOcCategoriaEts) {
        UtilLog4j.log.info(this, "OcOrdenEtsImp.TraerOcOrdenEts");
        try {
            return em.createQuery("SELECT o FROM OcOrdenEts o "
                    + " WHERE o.eliminado = :eliminado "
                    + " AND o.ocCategoriaEts.id = :idOcCategoria "
                    + " AND o.orden.id = :idOrden "
                    + " ORDER BY o.id ASC").setParameter("eliminado", Constantes.BOOLEAN_FALSE).setParameter("idOcCategoria", idOcCategoriaEts).setParameter("idOrden", idOrden).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en traer idOcCategoriaEts " + e.getMessage());
            return null;
        }
    }

    
    public List<OcOrdenEts> traerOcOrdenEtsPorOrden(Orden orden) {
        try {
            return em.createQuery("SELECT o FROM OcOrdenEts o "
                    + " WHERE o.eliminado = :eliminado "
                    + " AND o.orden.id = :idOrden "
                    + " ORDER BY o.id ASC").setParameter("eliminado", Constantes.BOOLEAN_FALSE).setParameter("idOrden", orden.getId()).getResultList();

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer ocOrdenEtspor Orden " + e.getMessage());
            return null;
        }
    }

    
    public List<OrdenEtsVo> traerEtsPorOrdenCategoria(int idOrden) {
        clearQuery();
        List<OrdenEtsVo> loest = null;
        try {
            query.append("select a.ID, a.NOMBRE, a.DESCRIPCION, a.URL, a.UUID, a.TIPO_ARCHIVO, ce.NOMBRE, oe.id,")
                    .append(" oe.genero, ce.id from OC_ORDEN_ETS oe")
                    .append(" inner join orden o on oe.ORDEN = o.ID")
                    .append(" inner join SI_ADJUNTO a on oe.SI_ADJUNTO = a.ID")
                    .append(" inner join OC_CATEGORIA_ETS ce on oe.OC_CATEGORIA_ETS = ce.ID")
                    .append(" where o.id = ").append(idOrden)
                    .append(" and ce.ELIMINADO = false ")
                    .append(" and oe.ELIMINADO =  false ")
                    .append(" order by ce.nombre, a.id asc");
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            if (lo != null && !lo.isEmpty()) {
                loest = new ArrayList<>();
                for (Object[] objects : lo) {
                    loest.add(castOrdenEts(objects));
                }
            }
            return loest;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return loest;
    }

    private OrdenEtsVo castOrdenEts(Object[] objects) {
        OrdenEtsVo oev = new OrdenEtsVo();
        oev.setId((Integer) objects[0]);
        oev.setNombre((String) objects[1]);
        oev.setDescripcion((String) objects[2]);
        oev.setUrl((String) objects[3]);
        oev.setUuid((String) objects[4]);
        oev.setTipoArchivo((String) objects[5]);
        oev.setCategoria((String) objects[6]);
        oev.setIdEtsOrden((Integer) objects[7]);
        oev.setGenero((String) objects[8]);
        oev.setIdTabla((Integer) objects[9]);
        return oev;
    }

    
    public List<OrdenEtsVo> traerEtsPorOrdenGenero(int idOrden, String generoId) {
        clearQuery();
        List<OrdenEtsVo> loest = new ArrayList<OrdenEtsVo>();
        try {
            StringBuilder sb = new StringBuilder();
            String cad = "";
            sb.append("select a.ID, a.NOMBRE, a.DESCRIPCION, a.URL, a.UUID, a.TIPO_ARCHIVO, ce.NOMBRE, oe.id,   ")
                    .append(" oe.genero, ce.id  from OC_ORDEN_ETS oe")
                    .append("      inner join SI_ADJUNTO a on oe.SI_ADJUNTO = a.ID")
                    .append("      inner join OC_CATEGORIA_ETS ce on oe.OC_CATEGORIA_ETS = ce.ID")
                    .append(" where oe.orden = ").append(idOrden)
                    .append(" and oe.genero = '").append(generoId).append("'")
                    .append(" and oe.ELIMINADO = false ")
                    .append(" order by ce.nombre, a.id asc");
            // System.out.println("Ets por categoria  : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null && !lo.isEmpty()) {
                loest = new ArrayList<OrdenEtsVo>();
                for (Object[] objects : lo) {
                    loest.add(castOrdenEts(objects));
                }
            }
            return loest;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }

        return loest;
    }

    
    public List<OrdenEtsVo> traerEtsPorOrdenCategoria(int idOrden, int categoria) {
        clearQuery();
        List<OrdenEtsVo> loest = new ArrayList<OrdenEtsVo>();
        try {
            StringBuilder sb = new StringBuilder();
            String cad = "";
            if (categoria > 0) {
                cad = " and ce.id = " + categoria;
            } else {
                cad = " and ce.id <> " + Constantes.OCS_CATEGORIA_TABLA;
                cad = " and ce.id <> " + Constantes.OCS_CATEGORIA_OCSPDF;
            }
            sb.append("select a.ID, a.NOMBRE, a.DESCRIPCION, a.URL, a.UUID, a.TIPO_ARCHIVO, ce.NOMBRE, oe.id,   "
                    + " oe.genero, ce.id from OC_ORDEN_ETS oe");
            sb.append("      inner join SI_ADJUNTO a on oe.SI_ADJUNTO = a.ID");
            sb.append("      inner join OC_CATEGORIA_ETS ce on oe.OC_CATEGORIA_ETS = ce.ID");
            sb.append(" where oe.orden = ").append(idOrden);
            sb.append(cad);
//	    sb.append(" and ce.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            sb.append(" and oe.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
//
            sb.append(" order by ce.nombre, a.id asc");
            // System.out.println("Ets por categoria  : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null && !lo.isEmpty()) {
                loest = new ArrayList<OrdenEtsVo>();
                for (Object[] objects : lo) {
                    loest.add(castOrdenEts(objects));
                }
            }            
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            loest = new ArrayList<OrdenEtsVo>();
        }

        return loest;
    }
}
