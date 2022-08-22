/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgDetalleSolicitudEstancia;
import sia.modelo.SgEmpresa;
import sia.modelo.SgInvitado;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.InvitadoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Stateless
public class SgInvitadoImpl extends AbstractFacade<SgInvitado> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SgEmpresaImpl sgEmpresaRemote;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgInvitadoImpl() {
        super(SgInvitado.class);
    }

    public List<SgInvitado> getAllInvitado(boolean eliminado) {
        UtilLog4j.log.info(this, "sgInivitadoImpl.getAllInvitado()");
        List<SgInvitado> invitadoList = null;
        try {
            invitadoList = em.createQuery("SELECT i FROM SgInvitado i WHERE i.eliminado = :eliminado ORDER BY i.nombre ASC").setParameter("eliminado", eliminado).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Existió un error al consultar las Inivitado " + e.getMessage());
            return null;
        }

        return invitadoList;
    }

    public List<InvitadoVO> traerInvitado() {
        UtilLog4j.log.info(this, "sgInivitadoImpl.getAllInvitado()");
        try {
            String sb = "Select i.id, i.nombre, i.email, e.id, e.nombre from sg_invitado i "
                    + "	    inner join sg_empresa e on i.sg_empresa = e.id "
                    + "	 where i.eliminado = 'False'"
                    + "	 order by i.nombre asc";
            List<Object[]> lo = em.createNativeQuery(sb).getResultList();
            List<InvitadoVO> li = new ArrayList<InvitadoVO>();
            for (Object[] lo1 : lo) {
                InvitadoVO i = new InvitadoVO();
                i.setIdInvitado((Integer) lo1[0]);
                i.setNombre((String) lo1[1]);
                i.setEmail((String) lo1[2]);
                i.setIdEmpresa((Integer) lo1[3]);
                i.setEmpresa((String) lo1[4]);
                //
                li.add(i);
            }
            return li;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Existió un error al consultar las Inivitado " + e.getMessage());
            return null;
        }
    }

    public SgInvitado guardarInvitado(Usuario usuario, InvitadoVO invitadoVO, int idEmpresa) {
        try {
            SgInvitado sgInvitado = new SgInvitado();
            String nombre = "";
            String[] arrayNombre = invitadoVO.getNombre().split(" ");
            for (String arrayNombre1 : arrayNombre) {
                nombre += arrayNombre1.substring(0, 1).toUpperCase() + arrayNombre1.substring(1, arrayNombre1.length()).toLowerCase() + " ";
            }
            nombre = nombre.trim();
            sgInvitado.setNombre(nombre);
            sgInvitado.setEmail(invitadoVO.getEmail());
            sgInvitado.setSgEmpresa(new SgEmpresa(idEmpresa));
            sgInvitado.setGenero(usuario);
            sgInvitado.setFechaGenero(new Date());
            sgInvitado.setHoraGenero(new Date());
            sgInvitado.setEliminado(Constantes.BOOLEAN_FALSE);
            sgInvitado.setTelefono(invitadoVO.getTelefono());
            create(sgInvitado);

            return sgInvitado;
        } catch (Exception ex) {
            Logger.getLogger(SgColorImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void modificarInvitado(Usuario usuario, InvitadoVO invitadoVO, int idEmpresa) {
        try {
            UtilLog4j.log.info(this, "modificarInvitado" + invitadoVO.getNombre());
            UtilLog4j.log.info(this, "existe se modifico");
            SgInvitado sgInvitado = find(invitadoVO.getIdInvitado());
            sgInvitado.setSgEmpresa(this.sgEmpresaRemote.find(idEmpresa));
            sgInvitado.setModifico(usuario);
            sgInvitado.setFechaModifico(new Date());
            sgInvitado.setHoraModifico(new Date());
            super.edit(sgInvitado);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "excepcion en modificar Invitado " + e.getMessage());
        }
    }

    public void eliminarInvitado(Usuario usuario, InvitadoVO invitadoVO, boolean eliminado) {

        SgInvitado sgInvitado = find(invitadoVO.getIdInvitado());
        sgInvitado.setModifico(usuario);
        sgInvitado.setFechaModifico(new Date());
        sgInvitado.setHoraModifico(new Date());
        sgInvitado.setEliminado(eliminado);
        edit(sgInvitado);
    }

    public boolean buscarInvitado(String nombre, int idEmpresa) {
        UtilLog4j.log.info(this, "buscarInvitado" + nombre);
        UtilLog4j.log.info(this, "buscarInvitado" + idEmpresa);
        List<SgInvitado> in;
        try {
            in = em.createQuery("SELECT i FROM SgInvitado i "
                    + "     WHERE i.nombre = :nombre AND i.sgEmpresa.id = :idEmpresa AND i.eliminado = :eli").setParameter("nombre", nombre).setParameter("idEmpresa", idEmpresa).setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
            if (in.size() > 0) {
                UtilLog4j.log.info(this, "Se encontro un registro igual");
                return true;
            } else {
                UtilLog4j.log.info(this, "No se encontro un registro igual");
                return false;
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "excepcion al buscar el invitado " + e.getMessage());
            return true;
        }

    }

    public boolean buscarInvitadoOcupado(int idInvitado) {
        UtilLog4j.log.info(this, "buscrInvitadoOcupado");
        SgInvitado invitado = null;
        List<SgDetalleSolicitudEstancia> listSE = null;
        try {
            //buscar invitado ocupado en SgDetalleSolicitudEstancia
            listSE = em.createQuery("SELECT ds FROM SgDetalleSolicitudEstancia ds WHERE ds.sgInvitado.id = :idInvitado AND ds.eliminado = :eli").setParameter("eli", Constantes.BOOLEAN_FALSE).setParameter("idInvitado", idInvitado).getResultList();

            if (listSE.size() > 0) {
                UtilLog4j.log.info(this, "Se encontro alguna relacion ");
                return true;
            } else {
                UtilLog4j.log.info(this, "No se encontro ninguna relacion ");
                return false;
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en " + e.getMessage());
            return true;
        }
    }

    public InvitadoVO buscarInvitado(String nombre) {
        try {
            clearQuery();
            appendQuery("SELECT i.id, i.nombre, i.email, em.id, em.nombre FROM Sg_Invitado i, sg_empresa em ");
            appendQuery(" WHERE i.nombre = '").append(nombre).append("'");
            appendQuery(" and i.sg_empresa = em.id limit 1");
            Object[] objects = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            return castInvitadoVO(objects);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "excepcion al buscar el invitado " + e.getMessage());
            return null;
        }
    }

    public List<InvitadoVO> buscarInvitado() {
        List<InvitadoVO> lstInvitados = new ArrayList<InvitadoVO>();
        try {
            clearQuery();
            appendQuery("SELECT i.id, i.nombre, i.email, em.id, em.nombre FROM Sg_Invitado i, sg_empresa em ");
            appendQuery(" WHERE i.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            if (lo != null) {
                for (Object[] objects : lo) {
                    lstInvitados.add(castInvitadoVO(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            lstInvitados = new ArrayList<InvitadoVO>();
        }
        return lstInvitados;
    }

    private InvitadoVO castInvitadoVO(Object[] objects) {
        InvitadoVO invitadoVO = new InvitadoVO();
        invitadoVO.setIdInvitado((Integer) objects[0]);
        invitadoVO.setNombre((String) objects[1]);
        invitadoVO.setEmail((String) objects[2]);
        invitadoVO.setIdEmpresa((Integer) objects[3]);
        invitadoVO.setEmpresa((String) objects[4]);
        return invitadoVO;
    }

    public String traerInvitadoJsonPorCampo() {
        List<Object[]> lista;
        clearQuery();
        Gson gson = new Gson();
        query.append("SELECT i.id, i.nombre   FROM Sg_Invitado i");
        query.append(" WHERE i.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" order by i.nombre asc");
        lista = em.createNativeQuery(query.toString()).getResultList();
        JsonArray a = new JsonArray();

        for (Object[] o : lista) {
            if (lista != null) {
                JsonObject ob = new JsonObject();
                ob.addProperty("value", o[0] != null ? (Integer) o[0] : 0);
                ob.addProperty("label", o[1] != null ? (String) o[1] : "-");
                a.add(ob);
            }
        }
        return gson.toJson(a);

    }

    public List<Object[]> traerInvitadosJsonPorCampo() {
        List<Object[]> lista = null;
        clearQuery();
        try {
            query.append("SELECT i.id, i.nombre, e.NOMBRE   FROM Sg_Invitado i");
            query.append(" inner join SG_EMPRESA e on e.ID=i.SG_EMPRESA");
            query.append(" WHERE i.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" order by i.nombre asc");
            lista = em.createNativeQuery(query.toString()).getResultList();
        } catch (Exception ex) {
            Logger.getLogger(SgColorImpl.class.getName()).log(Level.SEVERE, null, ex);

        }

        return lista;

    }

    public int guardarInvitado(String usuario, String nombre, String correo, int idEmpresa) {
        UtilLog4j.log.info(this, "guardarInvitado " + nombre);
        try {
            SgInvitado sgInvitado = new SgInvitado();
            sgInvitado.setNombre(nombre);
            sgInvitado.setEmail(correo);
            sgInvitado.setSgEmpresa(new SgEmpresa(idEmpresa));
            sgInvitado.setGenero(new Usuario(usuario));
            sgInvitado.setFechaGenero(new Date());
            sgInvitado.setHoraGenero(new Date());
            sgInvitado.setEliminado(Constantes.BOOLEAN_FALSE);
            create(sgInvitado);

            return sgInvitado.getId();
        } catch (Exception ex) {
            Logger.getLogger(SgColorImpl.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public List<InvitadoVO> buscarInvitadoParteNombre(String cadena) {
        List<InvitadoVO> lstInvitados = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT i.id, i.nombre, i.email, em.id, em.nombre FROM Sg_Invitado i")
                    .append("   inner join sg_empresa em on i.sg_empresa = em.id ")
                    .append(" WHERE i.eliminado = false ")
                    .append(" and upper(i.nombre) like upper('%").append(cadena).append("%')");
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                for (Object[] objects : lo) {
                    lstInvitados.add(castInvitadoVO(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            lstInvitados = new ArrayList<>();
        }
        return lstInvitados;
    }

}
