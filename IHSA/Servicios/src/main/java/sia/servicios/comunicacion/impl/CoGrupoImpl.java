/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.comunicacion.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.CoGrupo;
import sia.modelo.CoMiembro;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@Stateless 
public class CoGrupoImpl extends AbstractFacade<CoGrupo>{

    @Inject
    private CoMiembroImpl servicioCoMiembro;
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CoGrupoImpl() {
        super(CoGrupo.class);
    }
    
    
    public CoGrupo buscarPorNombre(String nombreGrupo, String administrador){
        try {
         return (CoGrupo) em.createQuery("SELECT g FROM CoGrupo g WHERE g.nombre =:nombre AND g.administrador.id = :administrador")
                .setParameter("nombre", nombreGrupo)
                .setParameter("administrador", administrador)
                .getResultList()
                .get(0);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    
    public List<CoGrupo> getGrupos(String idAdministrador) {
        return em.createQuery("SELECT g FROM CoGrupo g WHERE g.eliminado = :eliminado AND g.administrador.id = :idAdministrador ORDER BY g.id").setParameter("eliminado", false).setParameter("idAdministrador", idAdministrador).getResultList();
    }

    /**
     * Elimina "Logicamente" un grupo con todos sus miembros.
     */
    
    public void eliminarGrupo(CoGrupo grupo) {
        if (this.getTotalMiembros(grupo.getId()) > 0) {
            for (CoMiembro coMiembro : this.getMiembros(grupo.getId())) {
                coMiembro.setEliminado(true);
                this.servicioCoMiembro.edit(coMiembro);
            }
        }
        grupo.setEliminado(true);
        this.edit(grupo);
    }

    
    public List<CoMiembro> getMiembros(Integer idGrupo) {
        return this.servicioCoMiembro.getMiembros(idGrupo);
    }

    
    public int getTotalMiembros(Integer idGrupo) {
        return this.servicioCoMiembro.getTotal(idGrupo);
    }

    
    public void actualizarMiembro(CoMiembro miembro) {
        this.servicioCoMiembro.edit(miembro);
    }

    
    public void agregarMiembro(CoMiembro miembro) {
        this.servicioCoMiembro.create(miembro);
    }

    
    public CoMiembro getMiembroPorNombre(String nombreMiembro, Integer idGrupo) {
        return this.servicioCoMiembro.getMiembroPorNombre(nombreMiembro, idGrupo);
    }
    
}
