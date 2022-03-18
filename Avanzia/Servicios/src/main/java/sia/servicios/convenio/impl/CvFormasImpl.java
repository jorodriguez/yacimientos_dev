/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.convenio.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.CvFormas;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.sistema.impl.SiAdjuntoImpl;

/**
 *
 * @author mluis
 */
@Stateless 
public class CvFormasImpl extends AbstractFacade<CvFormas>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CvFormasImpl() {
        super(CvFormas.class);
    }

    @Inject
    SiAdjuntoImpl adjuntoRemote;

    
    public List<CvFormas> traerTodo() {
        return em.createNamedQuery("CvFormas.findAll").getResultList();
    }

    
    public void eliminarArchivo(UsuarioVO usuarioSesion, int idForma) {
        CvFormas forma = find(idForma);
        //
        adjuntoRemote.eliminarArchivo(forma.getSiAdjunto().getId(), usuarioSesion.getId());
        //
        forma.setSiAdjunto(null);
        forma.setModifico(new Usuario(usuarioSesion.getId()));
        forma.setFechaModifico(new Date());
        forma.setHoraModifico(new Date());
        //
        edit(forma);
    }

    
    public void agregarArchivo(UsuarioVO usuarioSesion, int idForma, AdjuntoVO adjuntoVo) {
        CvFormas forma = find(idForma);
        int idAdj = adjuntoRemote.saveSiAdjunto(adjuntoVo.getNombre(), adjuntoVo.getTipoArchivo(), adjuntoVo.getUrl(), adjuntoVo.getTamanio(), usuarioSesion.getId());
        //
        forma.setSiAdjunto(new SiAdjunto(idAdj));
        forma.setModifico(new Usuario(usuarioSesion.getId()));
        forma.setFechaModifico(new Date());
        forma.setHoraModifico(new Date());
        //
        edit(forma);
    }

    
    public List<CvFormas> formasProveedor() {
        String c = "select  cf from CvFormas cf \n"                
                + " where cf.proveedor = true \n"
                + " and cf.eliminado  = false ";
        return em.createQuery(c).getResultList();
    }
}
