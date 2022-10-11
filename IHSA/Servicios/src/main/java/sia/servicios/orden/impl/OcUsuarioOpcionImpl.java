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
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.OcUsuarioOpcionInicio;
import sia.modelo.SiOpcion;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class OcUsuarioOpcionImpl extends AbstractFacade<OcUsuarioOpcionInicio>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcUsuarioOpcionImpl() {
        super(OcUsuarioOpcionInicio.class);
    }

    
    public void guardar(String sesion, int opcion) {
        // limpia las opciones principales anteriores
        List<SiOpcionVo> lop = buscarOpcionPrincipal(sesion);
        if (lop != null) {
            for (SiOpcionVo siOpcionVo : lop) {
                modificarPrincipal(sesion, siOpcionVo.getIdUsuarioOpcion());
            }
        }
        // Agrega la nueva opcion
        OcUsuarioOpcionInicio usuarioOpcion = new OcUsuarioOpcionInicio();
        usuarioOpcion.setUsuario(new Usuario(sesion));
        usuarioOpcion.setSiOpcion(new SiOpcion(opcion));
        usuarioOpcion.setPrincipal(Constantes.BOOLEAN_TRUE);
        usuarioOpcion.setGenero(new Usuario(sesion));
        usuarioOpcion.setFechaGenero(new Date());
        usuarioOpcion.setHoraGenero(new Date());
        usuarioOpcion.setEliminado(Constantes.BOOLEAN_FALSE);
        create(usuarioOpcion);
//
    }

    
    public void modificarPrincipal(String sesion, int opcion) {
        OcUsuarioOpcionInicio usuarioOpcion = find(opcion);
        usuarioOpcion.setPrincipal(Constantes.BOOLEAN_FALSE);
        usuarioOpcion.setModifico(new Usuario(sesion));
        usuarioOpcion.setFechaModifico(new Date());
        usuarioOpcion.setHoraModifico(new Date());
        edit(usuarioOpcion);
    }

    
    public List<SiOpcionVo> buscarOpcionPrincipal(String usuario) {
        clearQuery();
        query.append("select op.id, op.si_opcion from OC_USUARIO_OPCION_INICIO op ");
        query.append(" where op.usuario = '").append(usuario).append("'");
        query.append(" and op.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<SiOpcionVo> lop = null;
        if (lo != null) {
            lop = new ArrayList<SiOpcionVo>();
            for (Object[] objects : lo) {
                lop.add(castOpcionPrincipal(objects));
            }
        }
        return lop;
    }

    
    public SiOpcionVo opcionPrincipal(String usuario) {
        clearQuery();
        query.append("select op.ID, o.id, o.NOMBRE, o.PAGINA from OC_USUARIO_OPCION_INICIO op ");
        query.append("      inner join SI_OPCION o on op.SI_OPCION = o.ID ");
        query.append(" where op.usuario = '").append(usuario).append("'");
        query.append(" and op.principal = '").append(Constantes.BOOLEAN_TRUE).append("'");
        query.append(" and op.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        try {
            Object[] lo = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            return castOpcion(lo);

        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    private SiOpcionVo castOpcionPrincipal(Object[] objects) {
        SiOpcionVo opcionVo = new SiOpcionVo();
        opcionVo.setIdUsuarioOpcion((Integer) objects[0]);
        opcionVo.setId((Integer) objects[1]);
        return opcionVo;
    }

    private SiOpcionVo castOpcion(Object[] objects) {
        SiOpcionVo opcionVo = new SiOpcionVo();
        opcionVo.setIdUsuarioOpcion((Integer) objects[0]);
        opcionVo.setId((Integer) objects[1]);
        opcionVo.setNombre((String) objects[2]);
        opcionVo.setPagina((String) objects[3]);
        return opcionVo;
    }
}
