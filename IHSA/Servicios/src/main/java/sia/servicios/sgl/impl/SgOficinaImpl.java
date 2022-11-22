/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.excepciones.SIAException;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgCaracteristicaComedor;
import sia.modelo.SgCaracteristicaOficina;
import sia.modelo.SgCaracteristicaSalaJunta;
import sia.modelo.SgCaracteristicaSanitario;
import sia.modelo.SgDireccion;
import sia.modelo.SgOficina;
import sia.modelo.SgOficinaAnalista;
import sia.modelo.Usuario;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.oficina.vo.SgOficinaAnalistaVo;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgOficinaImpl extends AbstractFacade<SgOficina> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;    
    @Inject
    private SgDireccionImpl sgDireccionRemote;
    @Inject
    private SgOficinaAnalistaImpl sgOficinaAnalistaRemote;
    @Inject
    private SgCaracteristicaImpl caracteristicaService;
    @Inject
    private SgCaracteristicaOficinaImpl caracteristicaOficinaService;
    @Inject
    private SgCaracteristicaComedorImpl caracteristicaComedorRemote;
    @Inject
    private SgCaracteristicaSalaJuntaImpl caracteristicaSalaJuntaRemote;
    @Inject
    private SgCaracteristicaSanitarioImpl caracteristicaSanitarioRemote;    
    @Inject
    private UsuarioImpl usuarioRemote;
    
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgOficinaImpl() {
	super(SgOficina.class);
    }

    public boolean isUsed(SgOficina sgOficina) {

	int cont = 0;

	List<Object> list;

	list = em.createQuery("SELECT l FROM SgSolicitudViaje l WHERE l.oficinaOrigen.id = :idSgOficina OR l.oficinaDestino.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgSolicitudViaje");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgHistorialConvenioOficina l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgHistorialConvenioOficina");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgSalaJunta l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgSalaJunta");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgSanitario l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgSanitario");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgStaff l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgStaff");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgPagoServicioOficina l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgPagoServicioOficina");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgComedor l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgComedor");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgOficinaPlano l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgOficinaPlano");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgCaracteristicaOficina l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgCaracteristicaOficina");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgSolicitudEstancia l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgSolicitudEstancia");
	    cont++;
	    list.clear();
	}

//        list = em.createQuery("SELECT l FROM SgOficinaAnalista l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado")
//                .setParameter("eliminado", Constantes.NO_ELIMINADO)
//                .setParameter("idSgOficina", sgOficina.getId())
//                .getResultList();
//        if(list != null && !list.isEmpty()) {
//            UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgOficinaAnalista");
//            cont++;
//            list.clear();
//        }
	list = em.createQuery("SELECT l FROM SgOficinaChecklist l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgOficinaChecklist");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgHotel l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgHotel");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgVehiculo l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgVehiculo");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgAccesorio l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgAccesorio");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgTallerMantenimiento l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgTallerMantenimiento");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgAvisoPagoOficina l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgAvisoPagoOficina");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgRutaTerrestre l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgRutaTerrestre");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgDetalleRutaTerrestre l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgDetalleRutaTerrestre");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SgViaje l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SgViaje");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT l FROM SiUsuarioTipo l WHERE l.sgOficina.id = :idSgOficina AND l.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSgOficina", sgOficina.getId()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SgOficina " + sgOficina.getId() + " usado en SiUsuarioTipo");
	    cont++;
	    list.clear();
	}

	return (cont == 0 ? false : true);
    }

    
    public void save(OficinaVO vo, String nombreAnalista, String idUsuario) throws ExistingItemException {

	SgOficina existente = findByNameAndSiCiudad(vo.getNombre(), -1, false);
	SgOficina sgOficina;

	if (existente == null) {
	    SgDireccion sgDireccion = this.sgDireccionRemote.saveReturn(vo, idUsuario);

	    sgOficina = new SgOficina();
	    sgOficina.setNombre(vo.getNombre());
	    sgOficina.setNumeroTelefono(vo.getNumeroTelefono());
	    sgOficina.setSgDireccion(sgDireccion);
	    sgOficina.setVistoBueno(Constantes.BOOLEAN_FALSE);
	    sgOficina.setGenero(new Usuario(idUsuario));
	    sgOficina.setFechaGenero(new Date());
	    sgOficina.setHoraGenero(new Date());
	    sgOficina.setLatitud(vo.getLatitud());
	    sgOficina.setLongitud(vo.getLongitud());
	    sgOficina.setEliminado(Constantes.NO_ELIMINADO);

	    create(sgOficina);
	    UtilLog4j.log.info(this, "SgOficina CREATED SUCCESSFULLY");

	    Usuario analista = this.usuarioRemote.buscarPorNombre(nombreAnalista);
	    this.sgOficinaAnalistaRemote.save(analista.getId(), sgOficina.getId(), idUsuario);
	} else {
	    throw new ExistingItemException("sgOficina.mensaje.error.sgOficinaExistente", existente.getNombre(), existente);
	}
    }

    
    public void update(int idSiOficina, OficinaVO vo, String idUsuario) throws ExistingItemException {

	SgOficina original = find(idSiOficina);
	SgOficina existente = findByNameAndSiCiudad(vo.getNombre(), -1, false);
	if (existente == null || original.getId() == existente.getId()) {
	    this.sgDireccionRemote.update(original.getSgDireccion().getId(), vo, idUsuario);

	    original.setNombre(vo.getNombre());
	    original.setNumeroTelefono(vo.getNumeroTelefono());
	    original.setModifico(new Usuario(idUsuario));
	    original.setFechaModifico(new Date());
	    original.setHoraModifico(new Date());
	    original.setLatitud(vo.getLatitud());
	    original.setLongitud(vo.getLongitud());

	    edit(original);

	    UtilLog4j.log.info(this, "className: " + original.getClass().getName());
	    UtilLog4j.log.info(this, "fieldId: " + original.getId());
	    UtilLog4j.log.info(this, "userId: " + idUsuario);
	    UtilLog4j.log.info(this, "afterEvent: " + original.toString());
	    UtilLog4j.log.info(this, "SgOficina UPDATED SUCCESSFULLY");
	} else {
	    throw new ExistingItemException("sgOficina.mensaje.error.sgOficinaExistente", existente.getNombre(), existente);
	}
    }

    
    public void delete(int idSgOficina, String idUsuario) throws ItemUsedBySystemException {
	SgOficina sgOficina = find(idSgOficina);

	if (!isUsed(sgOficina)) {
	    sgOficina.setModifico(new Usuario(idUsuario));
	    sgOficina.setFechaModifico(new Date());
	    sgOficina.setHoraModifico(new Date());
	    sgOficina.setEliminado(Constantes.ELIMINADO);

	    //Eliminar también todos los Analistas para esta oficina
	    List<SgOficinaAnalistaVo> list = this.sgOficinaAnalistaRemote.getAllSgOficinaAnalista(idSgOficina, "nombre", true, false);
	    for (SgOficinaAnalistaVo vo : list) {
		this.sgOficinaAnalistaRemote.delete(vo.getId(), idUsuario);
	    }

	    edit(sgOficina);
	    UtilLog4j.log.info(this, "SgOficina DELETED SUCCESSFULLY");

	    //Eliminar Dirección
	    this.sgDireccionRemote.delete(sgOficina.getSgDireccion().getId(), idUsuario);

	} else {
	    throw new ItemUsedBySystemException(sgOficina.getNombre(), sgOficina);
	}
    }

    
    public SgOficina findByNameAndSiCiudad(String nombre, int idSiCiudad, boolean eliminado) {

	SgOficina sgOficina = null;

	try {
	    if (idSiCiudad < 0) {
		sgOficina = (SgOficina) em.createQuery("SELECT e FROM SgOficina e WHERE e.eliminado = :eliminado AND e.nombre = :nombre").setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO)).setParameter("nombre", nombre).getSingleResult();
	    } else {
		sgOficina = (SgOficina) em.createQuery("SELECT e FROM SgOficina e WHERE e.eliminado = :eliminado AND e.nombre = :nombre AND e.sgDireccion.siCiudad.id = :idSiCiudad").setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO)).setParameter("nombre", nombre).setParameter("idSiCiudad", idSiCiudad).getSingleResult();
	    }
	} catch (NonUniqueResultException nure) {
	    UtilLog4j.log.fatal(this, nure.getMessage());
	    UtilLog4j.log.fatal(this, "Se encontró más de un resultado para el SgOficina con nombre: " + nombre + " en la ciudad con id: " + idSiCiudad);
	    return sgOficina;
	} catch (NoResultException nre) {
	    UtilLog4j.log.fatal(this, nre.getMessage());
	    UtilLog4j.log.fatal(this, "No se encontró ningún SgOficina con nombre:" + nombre + " en la ciudad con id: " + idSiCiudad);
	    return sgOficina;
	}

	return sgOficina;
    }

    
    public SgCaracteristica addCaracteristica(Object relacion, Object area, String nombreCaracteristica, Integer cantidad, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgOficina.addCaracteristica()");

	SgCaracteristica caracteristicaNueva = null;
	UtilLog4j.log.info(this, "SgCaracteristicaOficina");

	//Buscar si existe la Característica
	SgCaracteristica car = caracteristicaService.findByName(nombreCaracteristica);

	if (car != null) { //La Característica existe. Solo hay que crear la relación
	    //No permitir duplicar la realación
	    SgCaracteristicaOficina carOficinaExistente = caracteristicaOficinaService.findByCaracteristicaAndOficina(car, ((OficinaVO) area).getId());

	    if (carOficinaExistente == null) {
		caracteristicaOficinaService.create(car, ((OficinaVO) area).getId(), cantidad, idUsuario);
	    } else {
		UtilLog4j.log.info(this, "La Característica ya está asignada");
		throw new SIAException("La Característica \" " + car.getNombre() + "\"  ya está asignada");
	    }
	} else { //La Característica no existe. Hay que crearla y luego la relación
	    UtilLog4j.log.info(this, "Nombre nueva Característica: " + nombreCaracteristica);
	    //Crear Característica
	    caracteristicaNueva = caracteristicaService.create(nombreCaracteristica, false, Constantes.TIPO_PAGO_OFICINA, idUsuario);
	    //Crear relación
	    caracteristicaOficinaService.create(caracteristicaNueva, ((OficinaVO) area).getId(), cantidad, idUsuario);
	}
	return caracteristicaNueva;
    }

    
    public SgCaracteristica addCaracteristicaComedor(int oficina, String nombreCaracteristica, int comedor, Integer cantidad, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaComedor");

	//Buscar si existe la Característica
	SgCaracteristica car = caracteristicaService.findByName(nombreCaracteristica);

	if (car != null) { //La Característica existe. Solo hay que crear la relación
	    //No permitir duplicar la realación
	    SgCaracteristicaComedor carComedorExistente = caracteristicaComedorRemote.findByCaracteristicaAndComedor(car, comedor);

	    if (carComedorExistente == null) {
		caracteristicaComedorRemote.create(car, comedor, cantidad, idUsuario);
	    } else {
		UtilLog4j.log.info(this, "La Característica ya está asignada");
		throw new SIAException("La Característica \" " + car.getNombre() + "\"  ya está asignada");
	    }
	    return car;
	} else { //La Característica no existe. Hay que crearla y luego la relación
	    UtilLog4j.log.info(this, "Nombre nueva Característica: " + nombreCaracteristica);
	    //Crear Característica
	    SgCaracteristica caracteristicaNueva = caracteristicaService.create(nombreCaracteristica, false, Constantes.TIPO_PAGO_OFICINA, idUsuario);
	    //Crear relación
	    caracteristicaComedorRemote.create(caracteristicaNueva, comedor, cantidad, idUsuario);
	    return caracteristicaNueva;
	}
    }

    
    public SgCaracteristica addCaracteristicaSanitario(int oficina, String nombreCaracteristica, int sanitario, Integer cantidad, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSanitario");

	//Buscar si existe la Característica
	SgCaracteristica car = caracteristicaService.findByName(nombreCaracteristica);

	if (car != null) { //La Característica existe. Solo hay que crear la relación
	    //No permitir duplicar la realación
	    SgCaracteristicaSanitario carSanitarioExistente = caracteristicaSanitarioRemote.findByCaracteristicaAndSanitario(car, sanitario);

	    if (carSanitarioExistente == null) {
		caracteristicaSanitarioRemote.create(car, sanitario, cantidad, idUsuario);
	    } else {
		UtilLog4j.log.info(this, "La Característica ya está asignada");
		throw new SIAException("La Característica \" " + car.getNombre() + "\"  ya está asignada");
	    }
	    return car;
	} else { //La Característica no existe. Hay que crearla y luego la relación
	    UtilLog4j.log.info(this, "Nombre nueva Característica: " + nombreCaracteristica);
	    //Crear Característica
	    SgCaracteristica caracteristicaNueva = caracteristicaService.create(nombreCaracteristica, false, Constantes.TIPO_PAGO_OFICINA, idUsuario);
	    //Crear relación
	    caracteristicaSanitarioRemote.create(caracteristicaNueva, sanitario, cantidad, idUsuario);
	    return caracteristicaNueva;
	}
    }

    
    public SgCaracteristica addCaracteristicaSala(int oficina, String nombreCaracteristica, int salaJunta, Integer cantidad, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSalaJunta");

	//Buscar si existe la Característica
	SgCaracteristica car = caracteristicaService.findByName(nombreCaracteristica);

	if (car != null) { //La Característica existe. Solo hay que crear la relación
	    //No permitir duplicar la realación
	    SgCaracteristicaSalaJunta carSalaJuntaExistente = caracteristicaSalaJuntaRemote.findByCaracteristicaAndSalaJunta(car, salaJunta);

	    if (carSalaJuntaExistente == null) {
		caracteristicaSalaJuntaRemote.create(car, salaJunta, cantidad, idUsuario);
	    } else {
		UtilLog4j.log.info(this, "La Característica ya está asignada");
		throw new SIAException("La Característica \" " + car.getNombre() + "\"  ya está asignada");
	    }
	    return car;
	} else { //La Característica no existe. Hay que crearla y luego la relación
	    UtilLog4j.log.info(this, "Nombre nueva Característica: " + nombreCaracteristica);
	    //Crear Característica
	    SgCaracteristica caracteristicaNueva = caracteristicaService.create(nombreCaracteristica, false, Constantes.TIPO_PAGO_OFICINA, idUsuario);
	    //Crear relación
	    caracteristicaSalaJuntaRemote.create(caracteristicaNueva, salaJunta, cantidad, idUsuario);
	    return caracteristicaNueva;
	}
    }

    
    public void removeCaracteristica(Object relacion, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgOficinaImpl.removeCaracteristica()");
	caracteristicaOficinaService.delete((CaracteristicaVo) relacion, idUsuario);
    }

    
    public void removeCaracteristicaComedor(CaracteristicaVo caracteristicaVo, String idUsuario) throws SIAException, Exception {
	caracteristicaComedorRemote.delete(caracteristicaVo, idUsuario);
    }

    
    public void removeCaracteristicaSala(CaracteristicaVo caracteristicaVo, String idUsuario) throws SIAException, Exception {
	caracteristicaSalaJuntaRemote.delete(caracteristicaVo, idUsuario);
    }

    
    public void removeCaracteristicaSanitario(CaracteristicaVo caracteristicaVo, String idUsuario) throws SIAException, Exception {
	caracteristicaSanitarioRemote.delete(caracteristicaVo, idUsuario);
    }

    
    public void guardarOficina(SgOficina sgOficina, SgDireccion sgDireccion, Usuario usuario, boolean eliminado, String analista, int idPais) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgOficinaImpl.guardarOficina()");

	//Guardar Dirección
	sgDireccion = sgDireccionRemote.guardarDireccion(sgDireccion, usuario, eliminado, idPais);

	sgOficina.setGenero(usuario);
	sgOficina.setSgDireccion(sgDireccion);
	sgOficina.setFechaGenero(new Date());
	sgOficina.setHoraGenero(new Date());
	sgOficina.setEliminado(Constantes.NO_ELIMINADO);
	sgOficina.setVistoBueno(Constantes.BOOLEAN_FALSE);

	create(sgOficina);

	//Guardar la relación Oficina-Analista
	sgOficinaAnalistaRemote.guardarAnalistaOficina(sgOficina, usuario, analista, eliminado);

	UtilLog4j.log.info(this, "SgOficina CREATED SUCCESSFULLY");
    }

    
    public void modificarOficina(SgOficinaAnalista sgOficinaAnalista, SgOficina sgOficina, Usuario usuario, String analista, int idPais) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgOficinaImpl.modificarOficina()");

	//Modificar Dirección
	sgDireccionRemote.modificarSgDireccion(sgOficina.getSgDireccion(), usuario, idPais);

	sgOficina.setModifico(usuario);
	sgOficina.setFechaModifico(new Date());
	sgOficina.setHoraModifico(new Date());

	edit(sgOficina);

	Usuario usuarioAnalista = usuarioRemote.buscarPorNombre(analista);
	//Modificar relación Oficina-Analista
	sgOficinaAnalistaRemote.modificarSgOficinaAnalista(sgOficinaAnalista, usuario, usuarioAnalista);

	UtilLog4j.log.info(this, "SgOficina UPDATED SUCCESSFULLY");
    }

    
    public void eliminarOficina(SgOficina sgOficina, SgOficinaAnalista sgOficinaAnalista, Usuario usuario, boolean eliminado) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgOficinaImpl.eliminarOficina()");

	//Eliminar Dirección
	sgDireccionRemote.deleteDireccion(sgOficina.getSgDireccion(), usuario.getId(), Constantes.ELIMINADO);

	sgOficina.setModifico(usuario);
	sgOficina.setFechaModifico(new Date());
	sgOficina.setHoraModifico(new Date());
	sgOficina.setEliminado(eliminado);

	edit(sgOficina);

	//Eliminar relación Oficina-Analista
	sgOficinaAnalistaRemote.eliminarOficinaAnalista(sgOficinaAnalista, usuario, eliminado);

	UtilLog4j.log.info(this, "SgOficina DELETED SUCCESSFULLY");
    }

    
    public List<SgOficina> findAll(boolean eliminado) {
	UtilLog4j.log.info(this, "SgOficinaImpl.findAll()");

	List<SgOficina> oficinasList = null;

	oficinasList = em.createQuery("SELECT o FROM SgOficina O WHERE o.eliminado = :eliminado ORDER BY o.nombre").setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO)).getResultList();

	UtilLog4j.log.info(this, "Se encontraron " + (oficinasList != null ? oficinasList.size() : 0) + " oficinas");

	return oficinasList;
    }

    
    public List<OficinaVO> findAll(int idSiCiudad, Boolean vistoBueno, String orderByField, boolean sortAscending, boolean eliminado) {
	clearQuery();
	query.append(consulta());
	query.append(" WHERE o.SG_DIRECCION=d.ID ");
	if (vistoBueno != null) {
	    query.append(" AND o.VISTO_BUENO='").append(vistoBueno ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).append("' ");
	}
	if (idSiCiudad > 0) {
	    query.append(" AND c.ID=").append(idSiCiudad).append(" ");
	}
	query.append(" AND o.ELIMINADO='").append(eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).append("' ");
	if (orderByField != null && !orderByField.isEmpty()) {
	    query.append(" ORDER BY o.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
	}

	Query q = em.createNativeQuery(query.toString());

	UtilLog4j.log.info(this, q.toString());

	List<Object[]> result = q.getResultList();
	List<OficinaVO> list = new ArrayList<OficinaVO>();

	for (Object[] objects : result) {
	    list.add(castOficina(objects));
	}
	return list;
    }

    private OficinaVO castOficina(Object[] objects) {
	OficinaVO vo = new OficinaVO();
	vo.setId((Integer) objects[0]);
	vo.setNombre((String) objects[1]);
	vo.setNumeroTelefono((String) objects[2]);
	vo.setVistoBueno((Boolean) objects[3]);
	vo.setIdSgDireccion((Integer) objects[4]);
	vo.setColonia((String) objects[5]);
	vo.setCalle((String) objects[6]);
	vo.setNumeroExterior((String) objects[7]);
	vo.setNumeroInterior((String) objects[8]);
	vo.setNumeroPiso((String) objects[9]);
	vo.setCodigoPostal((String) objects[10]);
	vo.setEstado((String) objects[11]);
	vo.setMunicipio((String) objects[12]);
	vo.setCiudad((String) objects[13]);
	vo.setIdSiPais((Integer) objects[14] != null ? (Integer) objects[14] : 0);
	vo.setIdSiEstado((Integer) objects[15] != null ? (Integer) objects[15] : 0);
	vo.setIdSiCiudad((Integer) objects[16] != null ? (Integer) objects[16] : 0);
	vo.setNombreSiPais((String) objects[17]);
	vo.setNombreSiEstado((String) objects[18]);
	vo.setNombreSiCiudad((String) objects[19]);
	vo.setLatitud((String) objects[20] != null ? (String) objects[20] : "");
	vo.setLongitud((String) objects[21] != null ? (String) objects[21] : "");

	return vo;
    }

    
    public List<OficinaVO> findByVistoBuenoList(boolean vistoBueno, boolean eliminado) {
	clearQuery();
	List<OficinaVO> lv = new ArrayList<OficinaVO>();
	query.append(consulta());
	query.append(" where o.eliminado = '").append(eliminado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).append("'");
	query.append(" and o.visto_bueno = '").append(vistoBueno ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).append("'");
	query.append(" order by o.id asc");
	List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	for (Object[] objects : lo) {
	    lv.add(castOficina(objects));
	}
	UtilLog4j.log.info(this, "lv.size(): " + (lv != null ? lv.size() : null));
	return lv;
    }

    
    public List<SgOficina> traerOficina(Usuario usuario, boolean eliminado, boolean vistoBueno) {
	return em.createQuery("SELECT o FROM SgOficina o WHERE o.vistoBueno = :vobo AND o.eliminado = :eli ORDER BY o.id ASC ").setParameter("eli", eliminado).setParameter("vobo", vistoBueno).getResultList();
    }

    
    public void setVistoBuenoSgOficina(int idSgOficina, String idUsuario) {
	SgOficina sgOficina = find(idSgOficina);
	sgOficina.setVistoBueno(Constantes.BOOLEAN_TRUE);
	sgOficina.setModifico(new Usuario(idUsuario));
	sgOficina.setFechaModifico(new Date());
	sgOficina.setHoraModifico(new Date());

	edit(sgOficina);
	UtilLog4j.log.info(this, "SgOficina VISTO_BUENO SUCCESSFULLY");
    }

    
    public void vistoBuenoOficina(SgOficina sgOficina, Usuario usuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgOficinaImpl.vistoBuenoOficina()");

	UtilLog4j.log.info(this, "Oficina: " + sgOficina);
	UtilLog4j.log.info(this, "Usuario: " + usuario.getNombre());

	sgOficina.setVistoBueno(Constantes.BOOLEAN_TRUE);
	sgOficina.setModifico(usuario);
	sgOficina.setFechaModifico(new Date());
	sgOficina.setHoraModifico(new Date());

	edit(sgOficina);
    }

    
    public List<CaracteristicaVo> getAllCaracteristicasOficinaList(int oficina) throws SIAException, Exception {
	return caracteristicaOficinaService.getAllCaracteristicaOficinaByOficinaList(oficina);
    }

    
    public List<CaracteristicaVo> getAllCaracteristicasComedorList(int comedor) throws SIAException, Exception {
	return caracteristicaComedorRemote.getAllCaracteristicaComedorByComedorList(comedor);
    }

    
    public List<CaracteristicaVo> getAllCaracteristicasSalaJuntaList(int salaJuntas) throws SIAException, Exception {
	return caracteristicaSalaJuntaRemote.getAllCaracteristicaSalaJuntaBySalaJuntaList(salaJuntas);
    }

    
    public List<CaracteristicaVo> getAllCaracteristicasSanitarioList(int sanitario) throws SIAException, Exception {
	return caracteristicaSanitarioRemote.getAllCaracteristicaSanitarioBySanitarioList(sanitario);
    }

    
    public List<SgOficina> getOfficeWhitoutCurrent(int oficina) throws SIAException {
	try {
	    return em.createQuery("SELECT o FROM SgOficina o WHERE o.id <> :id AND o.vistoBueno = :vobo AND o.eliminado = :eli ORDER BY o.nombre ASC").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("vobo", Constantes.BOOLEAN_TRUE).setParameter("id", (oficina)).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    e.printStackTrace();
	    throw new SIAException("Ocurrio un problema al consultar las oficinas");
	}
    }

    
    public List<OficinaVO> getIdOffices() {
	List<Object[]> lo;
	List<OficinaVO> lv = new ArrayList<OficinaVO>();
	OficinaVO o;
	Query q = em.createNativeQuery("SELECT o.id, o.nombre FROM sg_oficina o where o.eliminado = 'False'"
		+ " AND o.visto_bueno = 'True' order by o.id asc");

	lo = q.getResultList();
	UtilLog4j.log.info(this, "Q recupera id oficina: " + q.toString());
	for (Object[] objects : lo) {
	    o = new OficinaVO();
	    o.setId((Integer) objects[0]);
	    o.setNombre((String) objects[1]);
	    lv.add(o);
	}
	UtilLog4j.log.info(this, "lv.size(): " + (lv != null ? lv.size() : null));
	return lv;
    }

    
    public List<OficinaVO> traerListaOficina() {
	clearQuery();
	List<OficinaVO> lv = new ArrayList<OficinaVO>();
	query.append(consulta());
	query.append(" where o.eliminado = 'False'");
	query.append(" AND o.visto_bueno = 'True' order by o.id asc");
	List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	for (Object[] objects : lo) {
	    lv.add(castOficina(objects));
	}
	UtilLog4j.log.info(this, "lv.size(): " + (lv != null ? lv.size() : null));
	return lv;
    }

    /**
     * mluis
     *
     * @param idOficina
     * @return
     */
    
    public OficinaVO buscarPorId(int idOficina) {
	clearQuery();

	query.append(consulta());
	query.append(" WHERE o.ID = ").append(idOficina);
	Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	return castOficina(obj);
    }

    
    public String traerCiudadPorIdOficina(int idOficina) {
	clearQuery();
	appendQuery(" select ci.id, ci.nombre from si_ciudad ci where ci.id in (select dir.si_ciudad from ");
	appendQuery(" sg_direccion dir where dir.id in (select o.sg_direccion from sg_oficina o where o.id = ");
	appendQuery(idOficina).append("))");
	Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	if (obj != null) {
	    return (String) obj[1];
	}
	return null;
    }

    
    public String traerOficinaJsonPorCampo() {
	List<Object[]> lista;
	clearQuery();

	Gson gson = new Gson();
	query.append("SELECT o.id, o.nombre   FROM Sg_oficina o");
	query.append(" WHERE o.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append(" AND o.Visto_bueno = '").append(Constantes.BOOLEAN_TRUE).append("'");
	query.append(" order by o.nombre asc");
	lista = em.createNativeQuery(query.toString()).getResultList();
	JsonArray a = new JsonArray();

	for (Object[] o : lista) {
	    if (lista != null) {
		JsonObject ob = new JsonObject();
		ob.addProperty("value", o[0] != null ? (Integer) o[0] : 0);
		ob.addProperty("label", o[1] != null ? (String) o[1] : "-");
		a.add(ob);
	    }
	}
	return gson.toJson(a);

    }

    
    public OficinaVO buscarPorNombre(String nombre) {
	//clearQuery();
	final StringBuilder sql = new StringBuilder();
	sql.append(consulta());
	sql.append(" where  o.nombre = ?");
        
	final Object[] obj = 
                (Object[]) em.createNativeQuery(sql.toString())
                        .setParameter(1, nombre)
                        .getSingleResult();
	
        return castOficina(obj);
    }

    private String consulta() {
        String sql
                = "SELECT o.ID,  o.NOMBRE,  o.NUMERO_TELEFONO,  o.VISTO_BUENO,  d.ID AS idSgDireccion, " //4
                + " d.COLONIA,  d.CALLE,  d.NUMERO_EXTERIOR,  d.NUMERO_INTERIOR, " //8
                + " d.PISO,  d.CODIGO_POSTAL, d.ESTADO,  d.MUNICIPIO, d.CIUDAD, " //13
                + " d.SI_PAIS AS idSiPais,  d.SI_ESTADO AS idSiEstado,  d.SI_CIUDAD AS idSiCiudad, " //16
                + " p.NOMBRE AS nombreSiPais,  e.NOMBRE AS nombreSiEstado, " //18
                + " c.NOMBRE AS nombreSiCiudad,  o.LATITUD,  o.LONGITUD " //21
                + " FROM SG_OFICINA o "
                + "	    inner join SG_DIRECCION d on d.ID = o.SG_DIRECCION "
                + "	    left join SI_PAIS p on p.ID = d.SI_PAIS "
                + "	    left join SI_ESTADO e on e.ID = d.SI_ESTADO "
                + "	    left join SI_CIUDAD c on c.ID = d.SI_CIUDAD ";

        return sql;
    }
}
