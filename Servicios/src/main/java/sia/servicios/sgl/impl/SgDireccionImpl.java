/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgDireccion;
import sia.modelo.SiPais;
import sia.modelo.Usuario;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.servicios.sistema.impl.SiEstadoImpl;
import sia.servicios.sistema.impl.SiPaisImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgDireccionImpl extends AbstractFacade<SgDireccion>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SiPaisImpl siPaisRemote;
    @Inject
    private SiEstadoImpl siEstadoRemote;
    @Inject
    private SiCiudadImpl siCiudadRemote;
        
    private StringBuilder bodyQuery = new StringBuilder();
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgDireccionImpl() {
        super(SgDireccion.class);
    }

    
    public void save(String municipio, String colonia, String calle, String numExterior, String numInterior, String numPiso, String codigoPostal, int idSiPais, int idSiEstado, int idSiCiudad, String idUsuario) {

        SgDireccion sgDireccion = new SgDireccion();
        sgDireccion.setMunicipio(municipio);
        sgDireccion.setColonia(colonia);
        sgDireccion.setCalle(calle);
        sgDireccion.setNumeroExterior(numExterior);
        sgDireccion.setNumeroInterior(numInterior);
        sgDireccion.setPiso(numPiso);
        sgDireccion.setCodigoPostal(codigoPostal);
        sgDireccion.setSiPais(this.siPaisRemote.find(idSiPais));
        sgDireccion.setSiEstado(this.siEstadoRemote.find(idSiEstado));
        sgDireccion.setSiCiudad(this.siCiudadRemote.find(idSiCiudad));
        sgDireccion.setGenero(new Usuario(idUsuario));
        sgDireccion.setFechaGenero(new Date());
        sgDireccion.setHoraGenero(new Date());
        sgDireccion.setEliminado(Constantes.NO_ELIMINADO);

        create(sgDireccion);
        UtilLog4j.log.info(this, "SgDireccion CREATED SUCCESSFULLY");

    }

    
    public void update(int idSgDireccion, OficinaVO vo, String idUsuario) {
        SgDireccion original = find(idSgDireccion);
        if (!original.getMunicipio().equals(vo.getMunicipio())
                || !original.getColonia().equals(vo.getColonia()) || !original.getCalle().equals(vo.getCalle()) || !original.getNumeroExterior().equals(vo.getNumeroExterior())
                || !original.getNumeroInterior().equals(vo.getNumeroInterior()) || !original.getPiso().equals(vo.getNumeroPiso()) || !original.getCodigoPostal().equals(vo.getCodigoPostal())
                || original.getSiPais() == null || original.getSiPais().getId().intValue() != vo.getIdSiPais() || original.getSiEstado() == null || original.getSiEstado().getId().intValue() != vo.getIdSiEstado() || original.getSiCiudad() == null || original.getSiCiudad().getId().intValue() != vo.getIdSiCiudad()) {

            original.setMunicipio(vo.getMunicipio());
            original.setColonia(vo.getColonia());
            original.setCalle(vo.getCalle());
            original.setNumeroExterior(vo.getNumeroExterior());
            original.setNumeroInterior(vo.getNumeroInterior());
            original.setPiso(vo.getNumeroPiso());
            original.setCodigoPostal(vo.getCodigoPostal());
            original.setSiPais(this.siPaisRemote.find(vo.getIdSiPais()));
            original.setSiEstado(this.siEstadoRemote.find(vo.getIdSiEstado()));
            original.setSiCiudad(this.siCiudadRemote.find(vo.getIdSiCiudad()));
            original.setFechaModifico(new Date());
            original.setHoraModifico(new Date());

            edit(original);
            UtilLog4j.log.info(this, "SgDireccion UPDATED SUCCESSFULLY");
        }
    }

    
    public void delete(int idSgDireccion, String idUsuario) {

        SgDireccion sgDireccion = find(idSgDireccion);

        sgDireccion.setModifico(new Usuario(idUsuario));
        sgDireccion.setFechaModifico(new Date());
        sgDireccion.setHoraModifico(new Date());
        sgDireccion.setEliminado(Constantes.ELIMINADO);

        edit(sgDireccion);
        UtilLog4j.log.info(this, "SgDireccion DELETED SUCCESSFULLY");
    }

    
    public SgDireccion saveReturn(OficinaVO vo, String idUsuario) {

        SgDireccion sgDireccion = new SgDireccion();
        sgDireccion.setMunicipio(vo.getMunicipio());
        sgDireccion.setColonia(vo.getColonia());
        sgDireccion.setCalle(vo.getCalle());
        sgDireccion.setNumeroExterior(vo.getNumeroExterior());
        sgDireccion.setNumeroInterior(vo.getNumeroInterior());
        sgDireccion.setPiso(vo.getNumeroPiso());
        sgDireccion.setCodigoPostal(vo.getCodigoPostal());
        sgDireccion.setSiPais(this.siPaisRemote.find(vo.getIdSiPais()));
        sgDireccion.setSiEstado(this.siEstadoRemote.find(vo.getIdSiEstado()));
        sgDireccion.setSiCiudad(this.siCiudadRemote.find(vo.getIdSiCiudad()));
        sgDireccion.setGenero(new Usuario(idUsuario));
        sgDireccion.setFechaGenero(new Date());
        sgDireccion.setHoraGenero(new Date());
        sgDireccion.setEliminado(Constantes.NO_ELIMINADO);

        create(sgDireccion);
        UtilLog4j.log.info(this, "SgDireccion CREATED SUCCESSFULLY");
        return sgDireccion;
    }
    
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SgDireccion guardarDireccion(SgDireccion sgDireccion, Usuario usuario, boolean eliminado, int idPais) throws SIAException, Exception {
        UtilLog4j.log.info(this, "SgDireccionImpl.guardarDireccion()");
        sgDireccion.setSiPais(siPaisRemote.find(idPais));
        sgDireccion.setGenero(usuario);
        sgDireccion.setFechaGenero(new Date());
        sgDireccion.setHoraGenero(new Date());
        sgDireccion.setEliminado(eliminado);

        create(sgDireccion);
        UtilLog4j.log.info(this, "SgDireccion CREATED SUCCESSFULLY");

        return sgDireccion;
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SgDireccion modificarSgDireccion(SgDireccion sgDireccion, Usuario usuario, int idPais) throws SIAException, Exception {
        UtilLog4j.log.info(this, "SgDireccionImpl.modificarSgDireccion()");

        sgDireccion.setSiPais(siPaisRemote.find(idPais));

        sgDireccion.setModifico(usuario);
        sgDireccion.setFechaModifico(new Date());
        sgDireccion.setHoraModifico(new Date());

        edit(sgDireccion);
        UtilLog4j.log.info(this, "SgDireccion UPDATED SUCCESSFULLY");

        return sgDireccion;
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SgDireccion deleteDireccion(SgDireccion sgDireccion, String idUsuario, boolean status) throws SIAException, Exception {
        UtilLog4j.log.info(this, "SgDireccionImpl.deleteDireccion()");

        sgDireccion.setModifico(new Usuario(idUsuario));
        sgDireccion.setFechaModifico(new Date());
        sgDireccion.setHoraModifico(new Date());
        sgDireccion.setEliminado(Constantes.ELIMINADO);

        edit(sgDireccion);
        UtilLog4j.log.info(this, "SgDireccion DELETED SUCCESSFULLY");

        return sgDireccion;
    }

    
    public boolean buscarPaisUsado(SiPais siPais) {
        boolean v = false;
        List<SgDireccion> ld = em.createQuery("SELECT d FROM SgDireccion d WHERE d.siPais.id = :pais AND d.eliminado = :eli")
                .setParameter("pais", siPais.getId())
                .setParameter("eli", Constantes.NO_ELIMINADO)
                .getResultList();
        if (!ld.isEmpty()) {
            v = true;
        }
        return v;
    }
}
