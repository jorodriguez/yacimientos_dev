/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.incidencia.vehiculo.impl;

import java.util.Date;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiIncidenciaTag;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class SiIncidenciaTagImpl extends AbstractFacade<SiIncidenciaTag> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiIncidenciaTagImpl() {
        super(SiIncidenciaTag.class);
    }

    
    public void guardar(String sesion, int id, int idTag) throws Exception {
        try {

            SiIncidenciaTag siIncidenciaTag = new SiIncidenciaTag();
            siIncidenciaTag.setGenero(new Usuario(sesion));
            siIncidenciaTag.setFechaGenero(new Date());
            siIncidenciaTag.setHoraGenero(new Date());
            siIncidenciaTag.setEliminado(Constantes.NO_ELIMINADO);
            create(siIncidenciaTag);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al guardar las etiquetas: " + e.getMessage());
            throw e;
        }
    }
}
