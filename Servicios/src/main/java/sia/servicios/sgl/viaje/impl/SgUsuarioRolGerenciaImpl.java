/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgUsuarioRolGerencia;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.UsuarioRolGerenciaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiRolImpl;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgUsuarioRolGerenciaImpl extends AbstractFacade<SgUsuarioRolGerencia>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    //
    @Inject
    private SiRolImpl siRolRemote;    
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    //

    public SgUsuarioRolGerenciaImpl() {
        super(SgUsuarioRolGerencia.class);
    }

    /**
     * MLUIS 12/13/2013 -1 para ignorar el idRol
     */
    
    public List<UsuarioRolGerenciaVo> traerGerenciaPorRol(String idUsuario, int idRol) {
        clearQuery();
        String s = "";
        if (idRol != -1) {
            s = " and r.id = " + idRol;
        }
        List<UsuarioRolGerenciaVo> list = new ArrayList<UsuarioRolGerenciaVo>();
        appendQuery("select urg.id, u.id, u.nombre, g.id,g.nombre, r.id, r.nombre from sg_usuario_rol_gerencia urg, usuario u, si_rol r, gerencia g ");
        appendQuery(" where u.id = '").append(idUsuario).append("'");
        appendQuery(s);
        appendQuery(" and urg.usuario = u.id");
        appendQuery(" and urg.si_rol = r.id");
        appendQuery(" and urg.gerencia = g.id");
        appendQuery(" and urg.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        for (Object[] objects : lo) {
            list.add(castUsuarioRolGerencia(objects));
        }
        return list;
    }

    private UsuarioRolGerenciaVo castUsuarioRolGerencia(Object[] objects) {
        UsuarioRolGerenciaVo usuarioRolGerenciaVo = new UsuarioRolGerenciaVo();
        usuarioRolGerenciaVo.setIdUsuarioRolGerencia((Integer) objects[0]);
        usuarioRolGerenciaVo.setIdUsuario((String) objects[1]);
        usuarioRolGerenciaVo.setUsuario((String) objects[2]);
        usuarioRolGerenciaVo.setIdGerencia((Integer) objects[3]);
        usuarioRolGerenciaVo.setGerencia((String) objects[4]);
        usuarioRolGerenciaVo.setIdRol((Integer) objects[5]);
        usuarioRolGerenciaVo.setNombreRol((String) objects[6]);

        return usuarioRolGerenciaVo;
    }

    
    public void guardarUsuarioRolGerencia(String idSesion, String nombreUsuario, int idRol, int idGerencia) {
        SgUsuarioRolGerencia sgUsuarioRolGerencia = new SgUsuarioRolGerencia();
        sgUsuarioRolGerencia.setSiRol(siRolRemote.find(idRol));
        sgUsuarioRolGerencia.setUsuario(usuarioRemote.buscarPorNombre(nombreUsuario));
        sgUsuarioRolGerencia.setGerencia(gerenciaRemote.find(idGerencia));
        sgUsuarioRolGerencia.setGenero(new Usuario(idSesion));
        sgUsuarioRolGerencia.setFechaGenero(new Date());
        sgUsuarioRolGerencia.setHoraGenero(new Date());
        sgUsuarioRolGerencia.setEliminado(Constantes.NO_ELIMINADO);
        create(sgUsuarioRolGerencia);        
    }

    
    public void quitarRelacionUsuarioRolGerencia(String idSesion, int idUsuarioRolGerencia) {
        SgUsuarioRolGerencia sgUsuarioRolGerencia = find(idUsuarioRolGerencia);        
        sgUsuarioRolGerencia.setModifico(new Usuario(idSesion));
        sgUsuarioRolGerencia.setFechaModifico(new Date());
        sgUsuarioRolGerencia.setHoraModifico(new Date());
        sgUsuarioRolGerencia.setEliminado(Constantes.ELIMINADO);
        create(sgUsuarioRolGerencia);        
    }
}
