/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.accesorio.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgAccesorio;
import sia.modelo.SgSistemaOperativo;
import sia.modelo.SgTipo;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgSistemaOperativoImpl extends AbstractFacade<SgSistemaOperativo>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;

    public SgSistemaOperativoImpl() {
        super(SgSistemaOperativo.class);
    }

    
    public void guardarSistemaOperativo(Usuario usuario, SgAccesorio sgAccesorio, SgTipo sgTipoSitema, int idSistemaOperativo) {
        try {
            SgSistemaOperativo sgSistemaOperativo = new SgSistemaOperativo();
            sgSistemaOperativo.setSgAccesorio(sgAccesorio);
            sgSistemaOperativo.setSgTipo(sgTipoSitema);
            sgSistemaOperativo.setSgTipoEspecifico(sgTipoEspecificoRemote.find(idSistemaOperativo));
            sgSistemaOperativo.setEliminado(Constantes.NO_ELIMINADO);
            sgSistemaOperativo.setGenero(usuario);
            sgSistemaOperativo.setFechaGenero(new Date());
            sgSistemaOperativo.setHoraGenero(new Date());
            create(sgSistemaOperativo);

        } catch (Exception e) {
        }
    }

    
    public List<SgSistemaOperativo> buscarAccesorioEnSistemaOperativo(SgAccesorio sgAccesorio) {
        try {
            return em.createQuery("SELECT op FROM SgSistemaOperativo op WHERE op.sgAccesorio.id = :acc AND op.eliminado = :eli ORDER BY op.nombre ASC ").setParameter("acc", sgAccesorio.getId()).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
