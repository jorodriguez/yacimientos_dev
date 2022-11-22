/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.orden.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.OcFlujo;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class OcFlujoImpl extends AbstractFacade<OcFlujo>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcFlujoImpl() {
        super(OcFlujo.class);
    }

    
    public List<String> getEjecutaPorAccion(String accion, int campo, boolean eliminado) {
        StringBuilder q = new StringBuilder();
        q.append(" select u.NOMBRE ");
        q.append(" from OC_FLUJO f ");
        q.append(" inner join USUARIO u on u.ID = f.EJECUTA ");
        q.append(" where f.ACCION = '").append(accion).append("' ");
        q.append(" and f.ELIMINADO = '").append(eliminado).append("' ");
        q.append(" and f.AP_CAMPO = ").append(campo);

        return em.createNativeQuery(q.toString()).getResultList();
    }

    
    public List<UsuarioTipoVo> getUsuariosPorAccion(String accion, int campo, boolean eliminado) {
        List<UsuarioTipoVo> lv = new ArrayList<UsuarioTipoVo>();
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("select ");
            sb.append(" u.id,");
            sb.append(" u.nombre, ");
            sb.append(" u.email, ");
            sb.append(" u.telefono, f.id ");
            sb.append(" from OC_FLUJO f ");
            sb.append(" inner join USUARIO u on u.ID = f.EJECUTA ");
            sb.append(" where f.ACCION = '").append(accion).append("' ");
            sb.append(" and f.ELIMINADO = '").append(eliminado).append("' ");
            sb.append(" and f.AP_CAMPO = ").append(campo);

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();

            for (Object[] objects : lo) {
                lv.add(castUsuarioTipoVo(objects));
            }
            return lv;
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return lv;
        }
    }

    private UsuarioTipoVo castUsuarioTipoVo(Object[] objects) {
        UsuarioTipoVo usuarioTipoVo = new UsuarioTipoVo();
        usuarioTipoVo.setIdUser((String) objects[0]);
        usuarioTipoVo.setUsuario((String) objects[1]);
        usuarioTipoVo.setCorreo((String) objects[2]);
        usuarioTipoVo.setTelefono((String) objects[3]);
        usuarioTipoVo.setId((Integer) objects[4]);
        return usuarioTipoVo;
    }

    
    public OcFlujo getByUsrActionCampo(String accion, int campo, String usrID) {
        try {
            return (OcFlujo) em.createQuery("SELECT u FROM OcFlujo u WHERE u.apCampo.id = :APCAMPO AND u.ejecuta.id = :eJECUTA AND u.accion = :aCCION")
                    .setParameter("APCAMPO", campo)
                    .setParameter("eJECUTA", usrID)
                    .setParameter("aCCION", accion)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    
    public void eliminar(String sesion, int idFlujo) {
        OcFlujo flujo = find(idFlujo);
        flujo.setEliminado(Constantes.BOOLEAN_TRUE);
        flujo.setModifico(new Usuario(sesion));
        flujo.setFechaModifico(new Date());
        flujo.setHoraModifico(new Date());
        edit(flujo);
    }
}
