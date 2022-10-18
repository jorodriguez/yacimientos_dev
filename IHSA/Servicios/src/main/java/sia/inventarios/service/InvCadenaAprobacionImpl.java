/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.ApCampo;
import sia.modelo.InvCadenaAprobacion;
import sia.modelo.Usuario;
import sia.modelo.cadena.aprobacion.vo.CadenaAprobacionVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;

/**
 *
 * @author mluis
 */
@Stateless 
public class InvCadenaAprobacionImpl extends AbstractFacade<InvCadenaAprobacion>  {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InvCadenaAprobacionImpl() {
        super(InvCadenaAprobacion.class);
    }

    @Inject
    SiUsuarioRolImpl siUsuarioRolRemote;
    @Inject
    UsuarioImpl UsuarioRemote;

    
    public List<CadenaAprobacionVo> traerPorCampo(int campoId) {
        String c = " select ica.id, us.id, us.nombre , ua.id , ua.nombre from inv_cadena_aprobacion ica \n"
                + "	inner join usuario us on ica.solicita = us.id \n"
                + "	inner join usuario ua on ica.autoriza = ua.id \n"
                + " where ica.ap_campo  = " + campoId
                + " and ica.eliminado  = false ";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<CadenaAprobacionVo> lista = new ArrayList<CadenaAprobacionVo>();
        for (Object[] obj : objs) {
            CadenaAprobacionVo cadVo = new CadenaAprobacionVo();
            cadVo.setId((Integer) obj[0]);
            cadVo.setIdSolicita((String) obj[1]);
            cadVo.setSolicita((String) obj[2]);
            cadVo.setIdAprueba((String) obj[3]);
            cadVo.setAprueba((String) obj[4]);
            lista.add(cadVo);
        }
        return lista;
    }

    
    public void guardar(CadenaAprobacionVo cadenaAprobacionVo, int campoId, String sesion) {
        InvCadenaAprobacion cadenaAprobacion = new InvCadenaAprobacion();
        cadenaAprobacion.setSolicita(new Usuario(cadenaAprobacionVo.getIdSolicita()));
        cadenaAprobacion.setAutoriza(new Usuario(cadenaAprobacionVo.getIdAprueba()));
        cadenaAprobacion.setApCampo(new ApCampo(campoId));
        cadenaAprobacion.setGenero(new Usuario(sesion));
        cadenaAprobacion.setFechaGenero(new Date());
        cadenaAprobacion.setHoraGenero(new Date());
        cadenaAprobacion.setEliminado(Constantes.NO_ELIMINADO);
        //
        create(cadenaAprobacion);
        // agegar el rol al usuario para ingresar a inventario
        if (!siUsuarioRolRemote.buscarRolPorUsuarioModulo(cadenaAprobacionVo.getIdSolicita(), Constantes.MODULO_INVENTARIOS, Constantes.ROL_SOL_MAT, campoId)) {
            siUsuarioRolRemote.guardarUsuarioRol(Constantes.ROL_SOL_MAT, cadenaAprobacionVo.getIdSolicita(), campoId, sesion);
        }
        if (!siUsuarioRolRemote.buscarRolPorUsuarioModulo(cadenaAprobacionVo.getIdAprueba(), Constantes.MODULO_INVENTARIOS, Constantes.ROL_AUTORIZA_MAT, campoId)) {
            siUsuarioRolRemote.guardarUsuarioRol(Constantes.ROL_AUTORIZA_MAT, cadenaAprobacionVo.getIdAprueba(), campoId, sesion);
        }
    }

    
    public void modificar(CadenaAprobacionVo cadenaAprobacionVo, int campoId, String sesion) {
        InvCadenaAprobacion cadenaAprobacion = find(cadenaAprobacionVo.getId());
        cadenaAprobacion.setSolicita(new Usuario(cadenaAprobacionVo.getSolicita()));
        cadenaAprobacion.setAutoriza(new Usuario(cadenaAprobacionVo.getAprueba()));
        cadenaAprobacion.setModifico(new Usuario(sesion));
        cadenaAprobacion.setFechaModifico(new Date());
        cadenaAprobacion.setHoraModifico(new Date());
        //
        edit(cadenaAprobacion);
    }

    
    public void eliminar(int idCadenaAprobacion, String sesion) {
        InvCadenaAprobacion cadenaAprobacion = find(idCadenaAprobacion);
        cadenaAprobacion.setModifico(new Usuario(sesion));
        cadenaAprobacion.setFechaModifico(new Date());
        cadenaAprobacion.setHoraModifico(new Date());
        cadenaAprobacion.setEliminado(Constantes.BOOLEAN_TRUE);
        //
        edit(cadenaAprobacion);
    }

    
    public List<CadenaAprobacionVo> traerPorSolicita(String idSolicita, int idCampo) {
        String c = " select ica.id, ua.id , ua.nombre from inv_cadena_aprobacion ica \n"
                + "	inner join usuario ua on ica.autoriza = ua.id \n"
                + " where ica.ap_campo  = " + idCampo
                + " and ica.solicita = '" + idSolicita + "'"
                + " and ica.eliminado  = false ";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<CadenaAprobacionVo> lista = new ArrayList<CadenaAprobacionVo>();
        for (Object[] obj : objs) {
            CadenaAprobacionVo cadVo = new CadenaAprobacionVo();
            cadVo.setId((Integer) obj[0]);
            cadVo.setIdAprueba((String) obj[1]);
            cadVo.setAprueba((String) obj[2]);
            lista.add(cadVo);
        }
        return lista;
    }

}
