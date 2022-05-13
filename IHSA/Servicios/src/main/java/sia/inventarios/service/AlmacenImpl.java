package sia.inventarios.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.inventarios.audit.Audit;
import static sia.inventarios.service.Utilitarios.crearExpresionLike;
import static sia.inventarios.service.Utilitarios.esNuloOVacio;
import sia.modelo.ApCampo;
import sia.modelo.InvAlmacen;
import sia.modelo.InvInventario;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.inventarios.AlmacenVO;
import sia.modelo.vo.inventarios.InventarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Aplimovil SA de CV
 */
//Stateless (name = "Inventarios_AlmacenService")
@Stateless
public class AlmacenImpl extends AbstractFacade<InvAlmacen> implements AlmacenRemote{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    Audit audit;

    @Inject
    InventarioImpl inventarioService;

    @Inject
    ApCampoImpl apCampoRemote;

    public AlmacenImpl() {
        super(InvAlmacen.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    
    public List<AlmacenVO> buscarPorFiltros(AlmacenVO filtro, Integer campo) {
        return buscarPorFiltros(filtro, null, null, null, true, campo);
    }

    /**
     *
     * @param filtro
     * @param inicio
     * @param tamanioPagina
     * @param campoOrdenar
     * @param esAscendente
     * @param idCampo
     * @return
     */
    
    public List<AlmacenVO> buscarPorFiltros(AlmacenVO filtro, Integer inicio, Integer tamanioPagina,
            String campoOrdenar, boolean esAscendente, Integer idCampo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery query = criteriaBuilder.createQuery();
        Root almacen = query.from(InvAlmacen.class);
        Join campo = almacen.join("apCampo");
        Join responsable1 = almacen.join("responsable1", JoinType.LEFT);
        Join responsable2 = almacen.join("responsable2", JoinType.LEFT);
        Join supervisor = almacen.join("supervisor", JoinType.LEFT);

        //Obtener el campo por el cual se va a ordenar la lista
        Path orderBy = obtenerOrderBy(campoOrdenar, almacen, responsable1, responsable2, supervisor);
        Order order = esAscendente ? criteriaBuilder.asc(orderBy) : criteriaBuilder.desc(orderBy);

        aplicarFiltros(filtro, criteriaBuilder, query, almacen, responsable1, responsable2, campo, supervisor);
        query.orderBy(order);
        query.select(criteriaBuilder.construct(AlmacenVO.class, almacen.get("id"), almacen.get("nombre"), almacen.get("descripcion"), responsable1.get("id"), responsable1.get("nombre"), responsable1.get("email"),
                responsable2.get("id"), responsable2.get("nombre"), responsable2.get("email"),
                campo.get("id"), campo.get("nombre"), supervisor.get("id"), supervisor.get("nombre"),
                supervisor.get("email")))
                .where(criteriaBuilder.equal(almacen.get("eliminado"), Constantes.BOOLEAN_FALSE),
                        criteriaBuilder.and(criteriaBuilder.equal(campo.get("id"), idCampo)));

        TypedQuery<AlmacenVO> typedQuery = getEntityManager().createQuery(query);

        //establecer la paginacion basados en los parametros
        if (inicio != null && tamanioPagina != null) {
            typedQuery.setFirstResult(inicio);
            typedQuery.setMaxResults(tamanioPagina);
        }

        return typedQuery.getResultList();
    }

    
    public int contarPorFiltros(AlmacenVO filtro, Integer idCampo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery query = criteriaBuilder.createQuery();
        Root almacen = query.from(InvAlmacen.class);
        Join campo = almacen.join("apCampo");
        Join responsable1 = almacen.join("responsable1", JoinType.LEFT);
        Join responsable2 = almacen.join("responsable2", JoinType.LEFT);
        Join supervisor = almacen.join("supervisor", JoinType.LEFT);

        aplicarFiltros(filtro, criteriaBuilder, query, almacen, campo, responsable1, responsable2, supervisor);
        query.select(criteriaBuilder.count(almacen))
                .where(criteriaBuilder.and(criteriaBuilder.equal(campo.get("id"), idCampo)));
        return ((Long) getEntityManager().createQuery(query).getSingleResult()).intValue();
    }

    
    public AlmacenVO buscar(Integer id) throws SIAException {
        InvAlmacen almacen = this.find(id);
        Usuario responsable1 = almacen.getResponsable1();
        Usuario responsable2 = almacen.getResponsable2();
        Usuario supervisor = almacen.getSupervisor();

        return new AlmacenVO(
                almacen.getId(), almacen.getNombre(), almacen.getDescripcion(),
                responsable1 != null ? responsable1.toVO() : new UsuarioVO(),
                responsable2 != null ? responsable2.toVO() : new UsuarioVO(),
                almacen.getApCampo().getId(),
                almacen.getApCampo().getNombre(),
                supervisor != null ? supervisor.toVO() : new UsuarioVO()
        );
    }

    
    public void crear(AlmacenVO almacenVO, String username, int campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "AlmacenService.create()");

            InvAlmacen almacen = new InvAlmacen();
            Usuario usuario = new Usuario(username);

            if (!esNuloOVacio(almacenVO.getResponsable1UsuarioId())) {
                Usuario usuarioResponsable1 = new Usuario();
                usuarioResponsable1.setId(almacenVO.getResponsable1UsuarioId());
                almacen.setResponsable1(usuarioResponsable1);
            }

            if (!esNuloOVacio(almacenVO.getResponsable2UsuarioId())) {
                Usuario usuarioResponsable2 = new Usuario();
                usuarioResponsable2.setId(almacenVO.getResponsable2UsuarioId());
                almacen.setResponsable2(usuarioResponsable2);
            }

            if (!esNuloOVacio(almacenVO.getSupervisorUsuarioId())) {
                Usuario supervisor = new Usuario();
                supervisor.setId(almacenVO.getSupervisorUsuarioId());
                almacen.setSupervisor(supervisor);
            }

            almacen.setNombre(almacenVO.getNombre());
            almacen.setDescripcion(almacenVO.getDescripcion());

            almacen.setEliminado(Constantes.BOOLEAN_FALSE);
            almacen.setGenero(usuario);
            almacen.setFechaGenero(new Date());
            almacen.setHoraGenero(new Date());
            almacen.setApCampo(new ApCampo(campo));

            // this.audit.register(AuditActions.CREATE, almacen, usuario);
            super.create(almacen);

            almacenVO.setId(almacen.getId());

            UtilLog4j.log.info(this, "Almacen creado exitosamente.");
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    
    public void actualizar(AlmacenVO almacenVO, String username, int campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "AlmacenService.update()");

            InvAlmacen almacen = this.find(almacenVO.getId());
            Usuario usuario = new Usuario(username);

            if (!esNuloOVacio(almacenVO.getResponsable1UsuarioId())) {
                Usuario usuarioResponsable1 = new Usuario();
                usuarioResponsable1.setId(almacenVO.getResponsable1UsuarioId());
                almacen.setResponsable1(usuarioResponsable1);
            } else {
                almacen.setResponsable1(null);
            }

            if (!esNuloOVacio(almacenVO.getResponsable2UsuarioId())) {
                Usuario usuarioResponsable2 = new Usuario();
                usuarioResponsable2.setId(almacenVO.getResponsable2UsuarioId());
                almacen.setResponsable2(usuarioResponsable2);
            } else {
                almacen.setResponsable2(null);
            }

            if (!esNuloOVacio(almacenVO.getSupervisorUsuarioId())) {
                Usuario supervisor = new Usuario();
                supervisor.setId(almacenVO.getSupervisorUsuarioId());
                almacen.setSupervisor(supervisor);
            } else {
                almacen.setSupervisor(null);
            }

            almacen.setNombre(almacenVO.getNombre());
            almacen.setDescripcion(almacenVO.getDescripcion());

            almacen.setModifico(usuario);
            almacen.setFechaModifico(new Date());
            almacen.setHoraModifico(new Date());

            // this.audit.register(AuditActions.UPDATE, almacen, usuario);
            super.edit(almacen);

            UtilLog4j.log.info(this, "Almacen actualizado exitosamente.");
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    
    public void eliminar(Integer id, String username, Integer campo) throws SIAException {
        try {
            UtilLog4j.log.info(this, "AlmacenService.delete()");

            int numeroInventarios = getEntityManager()
                    .createNamedQuery("InvAlmacen.ExisteUnidadesEnInventario", Integer.class)
                    .setParameter(1, id)
                    .setParameter(2, Constantes.BOOLEAN_FALSE)
                    .getSingleResult();

            if (numeroInventarios > 0) {
                throw new Exception("No se puede eliminar el almacén porque existen " + numeroInventarios + " inventarios en este almacén.");
            }

            InvAlmacen almacen = this.find(id);
            Usuario usuario = new Usuario(username);

            almacen.setModifico(usuario);
            almacen.setFechaModifico(new Date());
            almacen.setHoraModifico(new Date());
            almacen.setEliminado(Constantes.BOOLEAN_TRUE);

            //    this.audit.register(AuditActions.DELETE, almacen, usuario);
            super.edit(almacen);

            UtilLog4j.log.info(this, "Almacen eliminado exitosamente.");
        } catch (Exception ex) {
            throw new SIAException(ex.getMessage());
        }
    }

    
    public List<InventarioVO> buscarInventariosPorArticulo(Integer almacenId, String keywords) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery query = criteriaBuilder.createQuery();
        List<Predicate> predicates = new ArrayList<Predicate>(5);

        Root inventario = query.from(InvInventario.class);
        Join almacen = inventario.join("almacen");
        Join articulo = inventario.join("articulo");
        String keywordsExpresionLike = crearExpresionLike(keywords.toLowerCase());

        predicates.add(criteriaBuilder.equal(inventario.get("eliminado"), Constantes.BOOLEAN_FALSE));
        predicates.add(criteriaBuilder.equal(almacen.get("id"), almacenId));
        predicates.add(criteriaBuilder.or(
                criteriaBuilder.equal(articulo.get("id"), keywords), // Buscar por codigo de barras (ID)
                criteriaBuilder.like(criteriaBuilder.lower(articulo.get("codigo")), keywordsExpresionLike), // Buscar por codigo de articulo (SKU)
                criteriaBuilder.like(criteriaBuilder.lower(articulo.get("nombre")), keywordsExpresionLike) // Buscar por nombre de articulo
        ));

        query.where(predicates.toArray(new Predicate[0])).orderBy(criteriaBuilder.asc(articulo.get("nombre")));
        query.select(criteriaBuilder.construct(InventarioVO.class, inventario.get("id"), almacen.get("id"), almacen.get("nombre"), articulo.get("id"), articulo.get("nombre"), inventario.get("numeroUnidades"), inventario.get("minimoUnidades"), inventario.get("fechaUltimaRevision")));

        TypedQuery<InventarioVO> typedQuery = getEntityManager().createQuery(query);

        return typedQuery.getResultList();
    }

    
    public InventarioVO buscarInventario(Integer almacenId, Integer articuloId, Integer campo) {
        InventarioVO filtro = new InventarioVO();
        filtro.setAlmacenId(almacenId);
        filtro.setArticuloId(articuloId);

        List<InventarioVO> resultados = inventarioService.buscarPorFiltros(filtro, campo);

        if (!resultados.isEmpty()) {
            return resultados.get(0);
        } else {
            return null;
        }
    }

    
    public InventarioVO obtenerInventario(Integer almacenId, Integer articuloId, String username, Integer campo) throws SIAException {
        InventarioVO inventarioVO = this.buscarInventario(almacenId, articuloId, campo);

        if (inventarioVO == null) {
            // Si el inventario no existe en el almacen entonces crear el inventario automaticamente
            inventarioVO = new InventarioVO();
            inventarioVO.setAlmacenId(almacenId);
            inventarioVO.setArticuloId(articuloId);
            inventarioVO.setNumeroUnidades(0);
            inventarioVO.setMinimoUnidades(0);
            inventarioVO.setFechaUltimaRevision(new Date());
            inventarioService.crear(inventarioVO, username, campo);
        }

        return inventarioVO;
    }

    private void aplicarFiltros(AlmacenVO filtro, CriteriaBuilder criteriaBuilder,
            CriteriaQuery query, Root almacen,
            Join responsable1, Join responsable2, Join campo, Join supervisor) {
        List<Predicate> predicates = new ArrayList<Predicate>(5);

        predicates.add(criteriaBuilder.equal(almacen.get("eliminado"), Constantes.BOOLEAN_FALSE));

        if (!esNuloOVacio(filtro.getNombre())) {
            predicates.add(criteriaBuilder.like(almacen.get("nombre"), crearExpresionLike(filtro.getNombre())));
        }

        if (!esNuloOVacio(filtro.getDescripcion())) {
            predicates.add(criteriaBuilder.like(almacen.get("descripcion"), crearExpresionLike(filtro.getDescripcion())));
        }

        if (!esNuloOVacio(filtro.getResponsable1UsuarioId())) {
            predicates.add(criteriaBuilder.equal(responsable1.get("id"), filtro.getResponsable1UsuarioId()));
        }

        if (!esNuloOVacio(filtro.getResponsable2UsuarioId())) {
            predicates.add(criteriaBuilder.equal(responsable2.get("id"), filtro.getResponsable2UsuarioId()));
        }

        if (!esNuloOVacio(filtro.getSupervisorUsuarioId())) {
            predicates.add(criteriaBuilder.equal(supervisor.get("id"), filtro.getSupervisorUsuarioId()));
        }

        if (!esNuloOVacio(filtro.getIdCampo())) {
            predicates.add(criteriaBuilder.equal(campo.get("id"), filtro.getIdCampo()));
        }

        query.where(predicates.toArray(new Predicate[0]));
    }

    private Path obtenerOrderBy(String campoOrdenar, Root almacen, Join responsable1, Join responsable2, Join supervisor) {
        if (campoOrdenar == null) {
            return almacen.get("nombre");
        }
        if (campoOrdenar.equals("responsable1Nombre")) {
            return responsable1.get("nombre");
        } else if (campoOrdenar.equals("responsable2Nombre")) {
            return responsable2.get("nombre");
        } else if (campoOrdenar.equals("supervisorNombre")) {
            return supervisor.get("nombre");
        } else {
            return almacen.get(campoOrdenar);
        }
    }

    
    public List<AlmacenVO> almacenesPorCampo(int idCampo) {
        String c = "select ia.id , ia.nombre   from inv_almacen ia where ia.eliminado = false and ia.ap_campo  = " + idCampo;
        //
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<AlmacenVO> lista = new ArrayList<>();
        for (Object[] obj : objs) {
            AlmacenVO aVo = new AlmacenVO();
            aVo.setId((Integer) obj[0]);
            aVo.setNombre((String) obj[1]);
            lista.add(aVo);
        }
        return lista;
    }

}
