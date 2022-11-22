/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.modelo.SgMotivo;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.MotivoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Stateless 
public class SgMotivoImpl extends AbstractFacade<SgMotivo>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgMotivoImpl() {
        super(SgMotivo.class);
    }

    
    public void save(String nombre, String idUsuario) throws ExistingItemException {
        UtilLog4j.log.info(this, "SgMotivoImpl.save()-");

        SgMotivo sgMotivo = new SgMotivo();
        SgMotivo existente = buscarPorNombre(nombre);

        if (existente == null) {
            sgMotivo.setNombre(nombre);
            sgMotivo.setGenero(new Usuario(idUsuario));
            sgMotivo.setFechaGenero(new Date());
            sgMotivo.setHoraGenero(new Date());
            sgMotivo.setEliminado(Constantes.NO_ELIMINADO);

            super.create(sgMotivo);
            UtilLog4j.log.info(this, "SiEstado CREATED SUCCESSFULLY");

        } else {
            throw new ExistingItemException(existente.getNombre(), existente);
        }
    }

    
    public SgMotivo saveRO(String nombre, String idUsuario) throws ExistingItemException {
        UtilLog4j.log.info(this, "SgMotivoImpl.save()-");

        SgMotivo sgMotivo = new SgMotivo();
        SgMotivo existente = buscarPorNombre(nombre);

        if (existente == null) {
            sgMotivo.setNombre(nombre);
            sgMotivo.setGenero(new Usuario(idUsuario));
            sgMotivo.setFechaGenero(new Date());
            sgMotivo.setHoraGenero(new Date());
            sgMotivo.setEliminado(Constantes.NO_ELIMINADO);

            create(sgMotivo);
            UtilLog4j.log.info(this, "SiEstado CREATED SUCCESSFULLY");

            return sgMotivo;

        } else {
            throw new ExistingItemException(existente.getNombre(), existente);
        }
    }

    /*
     * Consulta para traer los motivos de viaje
     */
    
    public List<SgMotivo> getAllMotivos(boolean eliminado) {
        UtilLog4j.log.info(this, "SgMotivoImpl.getAllMotivos()-");

        List<SgMotivo> motivosList = null;

        try {
            motivosList = em.createQuery("SELECT mv FROM SgMotivo mv WHERE mv.eliminado = :eliminado ORDER BY mv.nombre").setParameter("eliminado", eliminado).getResultList();

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Existió un error al consultar los motivos de viaje" + e.getMessage());
            return null;
        }

        return motivosList;
    }

    
    public void guardarMotivo(Usuario usuario, SgMotivo sgMotivo) {
        SgMotivo sgMotivoExistente = buscarPorNombreEliminado(sgMotivo.getNombre());
        if (sgMotivoExistente == null) {
            sgMotivo.setGenero(usuario);
            sgMotivo.setFechaGenero(new Date());
            sgMotivo.setHoraGenero(new Date());
            sgMotivo.setEliminado(Constantes.NO_ELIMINADO);
            create(sgMotivo);
        } else {
            sgMotivoExistente.setGenero(usuario);
            sgMotivoExistente.setFechaGenero(new Date());
            sgMotivoExistente.setHoraGenero(new Date());
            sgMotivoExistente.setEliminado(Constantes.ELIMINADO);
            edit(sgMotivoExistente);
        }
    }

    
    public void modificarMotivo(Usuario usuario, SgMotivo sgMotivo, boolean eliminado) {
        sgMotivo.setGenero(usuario);
        sgMotivo.setFechaGenero(new Date());
        sgMotivo.setHoraGenero(new Date());
        sgMotivo.setEliminado(eliminado);
        edit(sgMotivo);
    }

    
    public SgMotivo buscarPorNombre(String nombre) {
        try {
            return (SgMotivo) em.createQuery("SELECT m FROM SgMotivo m WHERE m.nombre = :nombre AND m.eliminado = :eli").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("nombre", nombre).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private SgMotivo buscarPorNombreEliminado(String nombre) {
        try {
            return (SgMotivo) em.createQuery("SELECT m FROM SgMotivo m WHERE m.nombre = :nombre AND m.eliminado = :eli").setParameter("eli", Constantes.ELIMINADO).setParameter("nombre", nombre).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * Consulta para traer los motivos de viaje
     */
    
    public List<MotivoVo> traerTodosMotivo() {
        clearQuery();
        query.append("select m.id, m.nombre from sg_motivo m where m.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");

        List<Object[]> listObjects = em.createNativeQuery(query.toString()).getResultList();
        List<MotivoVo> listaMotivo = null;
        try {
            if (listObjects != null) {
                listaMotivo = new ArrayList<>();
                for (Object[] objects : listObjects) {
                    listaMotivo.add(castMotivo(objects));
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Existió un error al consultar los motivos de viaje" + e.getMessage());
            return null;
        }

        return listaMotivo;
    }

    private MotivoVo castMotivo(Object[] objects) {
        MotivoVo motivoVo = new MotivoVo();
        motivoVo.setId((Integer) objects[0]);
        motivoVo.setNombre((String) objects[1]);
        return motivoVo;
    }
}
