/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.campo.nuevo.impl;

import java.util.Date;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.ApCompaniaGerencia;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class ApCompaniaGerenciaImpl extends AbstractFacade<ApCompaniaGerencia> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @Inject
    private CompaniaImpl companiaRemote;    
    @Inject
    private GerenciaImpl gerenciaRemote;    

    public ApCompaniaGerenciaImpl() {
        super(ApCompaniaGerencia.class);
    }

    
    public void guardarRelacionGerencia(String sesion, String rfc, int idGerencia) {
        ApCompaniaGerencia apCompaniaGerencia = new ApCompaniaGerencia();
        apCompaniaGerencia.setCompania(companiaRemote.find(rfc));
        apCompaniaGerencia.setGerencia(gerenciaRemote.find(idGerencia));
        apCompaniaGerencia.setGenero(new Usuario(sesion));
        apCompaniaGerencia.setFechaGenero(new Date());
        apCompaniaGerencia.setHoraGenero(new Date());
        apCompaniaGerencia.setEliminado(Constantes.NO_ELIMINADO);
        create(apCompaniaGerencia);
    }
}
