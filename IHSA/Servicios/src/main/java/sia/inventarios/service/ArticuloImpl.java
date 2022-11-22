package sia.inventarios.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
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
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.inventarios.audit.Audit;
import sia.inventarios.audit.AuditActions;
import sia.inventarios.log.EjbLog;
import static sia.inventarios.service.Utilitarios.crearExpresionLike;
import static sia.inventarios.service.Utilitarios.esNuloOVacio;
import sia.modelo.ApCampo;
import sia.modelo.InvArticulo;
import sia.modelo.InvArticuloCampo;
import sia.modelo.SiCategoria;
import sia.modelo.SiUnidad;
import sia.modelo.Usuario;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.CategoriaVo;
import sia.modelo.vo.inventarios.ArticuloInventarioVO;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.modelo.vo.inventarios.InventarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.sistema.impl.FolioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Aplimovil SA de CV
 */
//Stateless (name = "Inventarios_ArticuloService")
@Stateless
public class ArticuloImpl extends AbstractFacade<InvArticulo>  implements ArticuloRemote{

    private static final int MAXIMO_RESULTADOS = 100;

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    Audit audit;

    @Inject
    InventarioImpl inventarioService;

    @Inject
    InvArticuloCampoImpl invArticuloCampoRemote;

    @Inject
    ApCampoImpl  apCampoRemote;

    @Inject
    private FolioImpl folioRemote;

    @Inject
    DSLContext dbCtx;

    public ArticuloImpl() {
        super(InvArticulo.class);
    }

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    
    @Override
    public List<ArticuloVO> buscarPorFiltros(ArticuloVO filtro, Integer campo) {
        return buscarPorFiltros(filtro, null, null, null, false, campo);
    }

    
    @Override
    public List<ArticuloVO> buscarPorFiltros(ArticuloVO filtro, Integer inicio, Integer tamanioPagina, String campoOrdenar,
            boolean esAscendente, Integer idCampo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery queryCritBuild = criteriaBuilder.createQuery();
        Root articuloCampo = queryCritBuild.from(InvArticuloCampo.class);
        Join campo = articuloCampo.join("apCampo");
        Join articulo = articuloCampo.join("invArticulo");
        Join unidad = articulo.join("unidad");
        Join moneda = articuloCampo.join("moneda", JoinType.LEFT);

        //Obtener el campo por el cual se va a ordenar la lista
        Path orderBy = campoOrdenar == null ? articulo.get("nombre") : articulo.get(campoOrdenar);
        Order order = esAscendente ? criteriaBuilder.asc(orderBy) : criteriaBuilder.desc(orderBy);
        //
        filtro.setCampoId(idCampo);
        aplicarFiltros(filtro, criteriaBuilder, queryCritBuild, campo, articulo, unidad);
        queryCritBuild.orderBy(order);

        queryCritBuild.select(criteriaBuilder.construct(ArticuloVO.class,
                articulo.get("id"), articulo.get("codigo"), articulo.get("codigoBarras"), articulo.get("nombre"),
                articulo.get("descripcion"), unidad.get("id"), unidad.get("nombre"), campo.get("id"), campo.get("nombre"),
                articuloCampo.get("id"), articuloCampo.get("precio"), moneda.get("id"), moneda.get("nombre")
        ));

        TypedQuery<ArticuloVO> typedQuery = getEntityManager().createQuery(queryCritBuild);

        //establecer la paginacion basados en los parametros
        if (inicio != null && tamanioPagina != null) {
            typedQuery.setFirstResult(inicio);
            typedQuery.setMaxResults(tamanioPagina);
        }

        return typedQuery.getResultList();
    }

    
    @Override
    public int contarPorFiltros(ArticuloVO filtro, Integer idCampo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery query = criteriaBuilder.createQuery();
        Root articuloC = query.from(InvArticuloCampo.class);
        Join campo = articuloC.join("apCampo");
        Join articuloA = articuloC.join("invArticulo");
        Join unidad = articuloA.join("unidad");

        filtro.setCampoId(idCampo);
        aplicarFiltros(filtro, criteriaBuilder, query, campo, articuloA, unidad);
        query.select(criteriaBuilder.count(articuloC)).where(criteriaBuilder.and(criteriaBuilder.equal(campo.get("id"), idCampo)));
        return ((Long) getEntityManager().createQuery(query).getSingleResult()).intValue();
    }

    
    @Override
    public ArticuloVO buscar(Integer id) throws SIAException {
        //InvArticuloCampo artCampo = invArticuloCampoRemote.find(id);
        InvArticulo articulo = find(id);
        getEntityManager().refresh(articulo);

        SiUnidad unidad = articulo.getUnidad();
        Integer unidadId = 0;
        String unidadNombre = "";

        if (unidad != null) {
            unidadId = unidad.getId();
            unidadNombre = unidad.getNombre();
        }

        return new ArticuloVO(
                articulo.getId(), articulo.getCodigo(), articulo.getCodigoBarras(), articulo.getNombre(), articulo.getDescripcion(),
                unidadId, unidadNombre, false, articulo.getCodigoInt(), articulo.getCategorias());
    }

    
    @Override
    public ArticuloVO buscar(Integer id, Integer campo) throws SIAException {
        return this.obtenerArticulos(id, campo);
    }

    
    @Override
    public int guardarArticulo(ArticuloVO articuloVO, String sesion, List<CampoVo> listaCampo,
            List<CategoriaVo> categorias, String numParte) throws SIAException {
        int newArticuloID = 0;
        try {
            Usuario usuario = new Usuario(sesion);
            LOGGER.info(this, "ArticuloImpl.create()");
            InvArticulo articulo = this.buscarPorCodigo(articuloVO.getCodigo().toUpperCase(), articuloVO.getUnidadId());
            if (articulo == null) {
                articulo = this.buscarPorNombre(articuloVO.getNombre().toUpperCase(), articuloVO.getUnidadId());
            }

            if (articulo == null) {
                articulo = new InvArticulo();
                articulo.setCodigo(articuloVO.getCodigo().toUpperCase());
                articulo.setNombre(articuloVO.getNombre().toUpperCase());
                articulo.setDescripcion(articuloVO.getDescripcion().toUpperCase());
                articulo.setUnidad(new SiUnidad(articuloVO.getUnidadId()));
                articulo.setEliminado(Constantes.BOOLEAN_FALSE);

                articulo.setGenero(usuario);
                articulo.setFechaGenero(new Date());
                articulo.setHoraGenero(new Date());
                articulo.setSiCategoria(articuloVO.getIdRel() != null ? new SiCategoria(articuloVO.getIdRel()) : null);
                //
                articulo.setCodigoInt(numParte.isEmpty() ? articulo.getCodigo() : numParte.toUpperCase());
//            if(articulo.getCodigoBarras() == null || articulo.getCodigoBarras().isEmpty()){
//                articulo.setCodigoBarras(articulo.getCodigo().substring(articulo.getCodigo().length()-13, articulo.getCodigo().length()));
//            }) 
                if (categorias != null) {
                    String cats = "";
                    for (CategoriaVo categoria : categorias) {
                        if (categoria.getId() > 0) {
                            if (cats.isEmpty()) {
                                cats = categoria.getId() + ",";
                            } else {
                                cats += categoria.getId() + ",";
                            }
                        }
                    }
                    articulo.setCategorias(cats);
                }
                this.audit.register(AuditActions.CREATE, articulo, usuario);
                this.create(articulo);
                this.getEntityManager().flush();
            } else {
                for (int i = 0; i < listaCampo.size();) {
                    if (this.buscar(articulo.getId(), listaCampo.get(i).getId()) != null) {
                        listaCampo.remove(i);
                        i = 0;
                    } else {
                        i++;
                    }
                }
            }

            for (CampoVo listaCampo1 : listaCampo) {
                InvArticuloCampo artCampo = new InvArticuloCampo();
                artCampo.setInvArticulo(articulo);
                artCampo.setApCampo(new ApCampo(listaCampo1.getId()));
                artCampo.setGenero(usuario);
                artCampo.setFechaGenero(new Date());
                artCampo.setHoraGenero(new Date());
                artCampo.setEliminado(Constantes.BOOLEAN_FALSE);
                //
                invArticuloCampoRemote.create(artCampo);
                //
                articuloVO.setId(articulo.getId());
                articuloVO.setIdRel(artCampo.getId());
            }
            newArticuloID = articulo.getId();
            LOGGER.info(this, "Articulo creado exitosamente.");
        } catch (Exception ex) {
            throw new SIAException(ex);
        }
        return newArticuloID;
    }

    
    @Override
    public void actualizar(ArticuloVO articuloVO, String username, int campo) throws SIAException {
        try {
            LOGGER.info(this, "ArticuloImpl.update()");

            InvArticulo articulo = this.find(articuloVO.getId());
            Usuario usuario = new Usuario(username);

            //articulo.setCodigo(articuloVO.getCodigo());
            //articulo.setNombre(articuloVO.getNombre());
            articulo.setDescripcion(articuloVO.getDescripcion());
            articulo.setUnidad(new SiUnidad(articuloVO.getUnidadId()));
            articulo.setModifico(usuario);
            articulo.setFechaModifico(new Date());
            articulo.setHoraModifico(new Date());

            this.audit.register(AuditActions.UPDATE, articulo, usuario);

            super.edit(articulo);
            // actualizar en art campo
            invArticuloCampoRemote.agregarPrecioArticulo(articulo.getId(), campo, articuloVO.getPrecio(), articuloVO.getIdMoneda(), username);

            LOGGER.info(this, "Articulo actualizado exitosamente.");
        } catch (Exception ex) {
            throw new SIAException(ex);
        }
    }

    
    @Override
    public void eliminar(Integer id, String username, Integer campo) throws SIAException {
        try {
            LOGGER.info(this, "ArticuloImpl.delete()");
            InvArticuloCampo artCampo = invArticuloCampoRemote.find(id);

            long numeroInventarios = (long) getEntityManager()
                    .createNamedQuery("InvArticulo.ExisteUnidadesEnInventario")
                    .setParameter(1, artCampo.getInvArticulo().getId())
                    .setParameter(2, Constantes.BOOLEAN_FALSE)
                    .getSingleResult();

            if (numeroInventarios > 0) {
                throw new Exception("No se puede eliminar el artículo porque existen " + numeroInventarios + " inventarios de este artículo.");
            }
            InvArticulo articulo = artCampo.getInvArticulo();
            Usuario usuario = new Usuario(username);

            articulo.setModifico(usuario);
            articulo.setFechaModifico(new Date());
            articulo.setHoraModifico(new Date());
            articulo.setEliminado(Constantes.BOOLEAN_TRUE);

            this.audit.register(AuditActions.DELETE, articulo, usuario);

            super.edit(articulo);

            LOGGER.info(this, "Articulo eliminado exitosamente.");
        } catch (Exception ex) {
            throw new SIAException(ex);
        }
    }

    /**
     *
     * @param palabra
     * @param nombrCampo
     * @return
     */
    @Override
    public List<ArticuloVO> buscarPorPalabras(String palabra, String nombrCampo) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery query = criteriaBuilder.createQuery();
        Root articuloC = query.from(InvArticuloCampo.class);
        Join articuloA = articuloC.join("invArticulo");
        Join campo = articuloC.join("apCampo");

        palabra = crearExpresionLike(palabra.toLowerCase());
        query.where(criteriaBuilder.equal(articuloC.get("eliminado"), Constantes.BOOLEAN_FALSE),
                criteriaBuilder.and(criteriaBuilder.equal(campo.get("nombre"), nombrCampo)),
                criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(articuloA.get("codigo")), palabra),
                        criteriaBuilder.like(criteriaBuilder.lower(articuloA.get("nombre")), palabra),
                        criteriaBuilder.like(criteriaBuilder.lower(articuloA.get("descripcion")), palabra)
                )
        ).orderBy(criteriaBuilder.asc(articuloA.get("nombre")));
        query.select(criteriaBuilder.construct(ArticuloVO.class, articuloA.get("id"), articuloA.get("nombre")));

        TypedQuery<ArticuloVO> typedQuery = getEntityManager().createQuery(query);
        //typedQuery.setMaxResults(MAXIMO_RESULTADOS); //Establece el maximo de filas,
        //no se muestra en el sql del log pero si aplica correctamente la restriccion

        return typedQuery.getResultList();
    }

    /**
     *
     * @param articuloId
     * @param campo
     * @return
     */
    
    @Override
    public List<InventarioVO> buscarInventarios(Integer articuloId, Integer campo) {
        InventarioVO filtro = new InventarioVO();
        filtro.setArticuloId(articuloId);

        return inventarioService.buscarPorFiltros(filtro, campo);
    }

    
    @Override
    public ArticuloInventarioVO buscarArticuloConInventarios(String codigo, Integer campo) {
        //buscar articulo por codigo utilizando el método por filtros
        ArticuloVO filtro = new ArticuloVO();//fijando el codigo en el objeto de filtro
        filtro.setCodigo(codigo);
        List<ArticuloVO> articulos = buscarPorFiltros(filtro, campo);//el servicio retorna una lista
        if (articulos.isEmpty()) {
            return null; //si la lista esta vacia no se encontro el artículo
        }
        ArticuloVO articulo = articulos.get(0);//se selecciona como resultado de la busqueda por código el primer elemento de la lista
        //se busca los inventarios con el artículo id encontrado
        List<InventarioVO> inventarios = buscarInventarios(articulo.getId(), campo);
        //se crea el vo de respuesta
        ArticuloInventarioVO resultado = new ArticuloInventarioVO(articulo);
        resultado.setInventarios(inventarios);
        return resultado;
    }

    private void aplicarFiltros(ArticuloVO filtro, CriteriaBuilder criteriaBuilder, CriteriaQuery query,
            Join campo, Join articulo, Join unidad) {
        List<Predicate> predicates = new ArrayList<Predicate>(6);

        predicates.add(criteriaBuilder.equal(articulo.get("eliminado"), Constantes.BOOLEAN_FALSE));

        if (!esNuloOVacio(filtro.getCodigo())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(articulo.get("codigo")), crearExpresionLike(filtro.getCodigo().toLowerCase())));
        }

        if (!esNuloOVacio(filtro.getNombre())) {
            predicates.add(criteriaBuilder.like(articulo.get("nombre"), crearExpresionLike(filtro.getNombre())));
        }

        if (!esNuloOVacio(filtro.getDescripcion())) {
            predicates.add(criteriaBuilder.like(articulo.get("descripcion"), crearExpresionLike(filtro.getDescripcion())));
        }

        if (!esNuloOVacio(filtro.getUnidadId())) {
            predicates.add(criteriaBuilder.equal(unidad.get("id"), filtro.getUnidadId()));
        }
        if (!esNuloOVacio(filtro.getCampoId())) {
            predicates.add(criteriaBuilder.equal(campo.get("id"), filtro.getCampoId()));
        }

        query.where(predicates.toArray(new Predicate[0]));
    }

    
    @Override
    public List<ArticuloVO> obtenerArticulos(String codigo, int campoID, int categoriaID, String codigosCategorias) {

        LOGGER.info(this, "*** obtenerArticulos {0} - {1} - {2} - {3}", new Object[]{codigo, campoID, categoriaID, codigosCategorias});

        List<ArticuloVO> lstArticulos = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        //                   0      1              2                        3         4
        sb.append(" select a.ID, a.CODIGO, a.CODIGO_EAN13 AS codigo_barras, a.NOMBRE, a.DESCRIPCION, "
                // 5                      6                          7       
                + " a.UNIDAD AS unidad_id, u.NOMBRE as unidad_nombre, ac.AP_CAMPO AS campo_id, "
                // 8                         9                10                         11
                + "c.NOMBRE as campo_nombre, ac.ID AS id_rel, a.codigo_int AS num_parte, a.categorias "
                + " from INV_ARTICULO_CAMPO ac "
                + " inner join INV_ARTICULO a on a.ID = ac.INV_ARTICULO and a.ELIMINADO = 'False' and a.NOMBRE is not null ");
        if (categoriaID > 0) {
            sb.append(" and a.SI_CATEGORIA = ").append(categoriaID);
        }
        sb.append(" inner join AP_CAMPO c on c.ID = ac.AP_CAMPO and c.ELIMINADO = 'False' ");
        if (campoID > 0) {
            sb.append(" and ac.AP_CAMPO = ").append(campoID);
        }

        sb.append(" inner join SI_UNIDAD u on u.id = a.UNIDAD and u.ELIMINADO = 'False' "
                + " where ac.ELIMINADO = 'False' ");

        if (codigosCategorias != null && !codigosCategorias.isEmpty()) {
            sb.append(codigosCategorias);
        }

        if (codigo != null && !codigo.isEmpty()) {
            sb.append(" and upper(a.CODIGO_INT) = upper('").append(codigo).append("') ");
        }
        //sb.append(" order by a.NOMBRE ");

        try {
            lstArticulos
                    = dbCtx.fetch(sb.toString()).into(ArticuloVO.class);
        } catch (DataAccessException e) {
            LOGGER.fatal(this, e);
        }

        return lstArticulos;
    }

    
    @Override
    public List<ArticuloVO> obtenerArticulosUsuario(String codigo, int categoriaID, String codigosCategorias, String usuarioID) {
        List<ArticuloVO> lstArticulos = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder();
            //                   0      1              2            3        4              5         6          7           8      
            sb.append(" select a.ID, a.CODIGO, a.CODIGO_EAN13, a.NOMBRE, a.DESCRIPCION, a.UNIDAD, u.NOMBRE as UNIDADNOMBRE, a.codigo_int, a.categorias "
                    + " from INV_ARTICULO_CAMPO ac "
                    + " inner join INV_ARTICULO a on a.ID = ac.INV_ARTICULO and a.ELIMINADO = 'False' "
                    + " and a.NOMBRE is not null and a.NOMBRE <> '' and a.CODIGO is not null ");

            if (categoriaID > 0) {
                sb.append(" and a.SI_CATEGORIA = ").append(categoriaID);
            }

            sb.append(" inner join AP_CAMPO c on c.ID = ac.AP_CAMPO and c.ELIMINADO = 'False' ");

            if (usuarioID != null && !usuarioID.isEmpty()) {
                sb.append("and c.ID in (select ap_campo from AP_CAMPO_USUARIO_RH_PUESTO where ELIMINADO = 'False' and USUARIO = '").append(usuarioID).append("') ");
            }

            sb.append(" inner join SI_UNIDAD u on u.id = a.UNIDAD and u.ELIMINADO = 'False' "
                    + " where ac.ELIMINADO = 'False' ");

            if (codigosCategorias != null && !codigosCategorias.isEmpty()) {
                sb.append(codigosCategorias);
            }

            if (codigo != null && !codigo.isEmpty()) {
                sb.append(" and upper(a.CODIGO_INT) = upper('").append(codigo).append("') ");
            }

            sb.append(" GROUP BY ID,CODIGO,CODIGO_EAN13,NOMBRE,DESCRIPCION,UNIDAD,UNIDADNOMBRE,codigo_int,categorias "
                    + " order by a.NOMBRE ");

            LOGGER.info(this, "traer articulos :: " + sb);

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                for (Object[] objects : lo) {
                    lstArticulos.add(new ArticuloVO(
                            (Integer) objects[0],
                            (String) objects[1],
                            (String) objects[2],
                            (String) objects[3],
                            (String) objects[4],
                            (Integer) objects[5],
                            (String) objects[6],
                            false,
                            (String) objects[7],
                            (String) objects[8]));
                }
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e);
            lstArticulos = new ArrayList<ArticuloVO>();
        }
        return lstArticulos;
    }

    
    @Override
    public List<SelectItem> obtenerArticulosItems(String texto, String codigo, int campoID, int categoriaID, String codigosCategorias) {
        List<SelectItem> lstArticulos = new ArrayList<SelectItem>();
        try {
            //TODO : necesitamos traer todos los registros de una sola vez?
            StringBuilder sb = new StringBuilder();
            //                   0      1              2            3        4              5         6           7       8           9      10             11
            sb.append(" select a.ID, a.CODIGO, a.CODIGO_EAN13, a.NOMBRE, a.DESCRIPCION, a.UNIDAD, u.NOMBRE, ac.AP_CAMPO, c.NOMBRE, ac.ID, a.codigo_int, a.categorias "
                    + " , ac.precio, m.id , m.nombre "
                    + " from INV_ARTICULO_CAMPO ac "
                    + " inner join INV_ARTICULO a on a.ID = ac.INV_ARTICULO and a.ELIMINADO = 'False' "
                    + " and a.NOMBRE is not null and a.NOMBRE <> '' and a.CODIGO is not null ");
            if (categoriaID > 0) {
                sb.append(" and a.SI_CATEGORIA = ").append(categoriaID);
            }
            sb.append(" inner join AP_CAMPO c on c.ID = ac.AP_CAMPO and c.ELIMINADO = 'False' ");
            if (campoID > 0) {
                sb.append(" and ac.AP_CAMPO = ").append(campoID);
            }
            sb.append(" left join moneda m on m.id = ac.moneda");

            sb.append(" inner join SI_UNIDAD u on u.id = a.UNIDAD and u.ELIMINADO = 'False' "
                    + " where ac.ELIMINADO = 'False' ");

            if (codigosCategorias != null && !codigosCategorias.isEmpty()) {
                sb.append(codigosCategorias);
            }

            if (codigo != null && !codigo.isEmpty()) {
                sb.append(" and upper(a.CODIGO_INT) = upper('").append(codigo).append("') ");
            }

            if (texto != null && !texto.isEmpty()) {
                sb.append(texto);
            }

            sb.append(" order by a.NOMBRE ");
            // 
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                for (Object[] objects : lo) {
                    ArticuloVO p = new ArticuloVO(
                            (Integer) objects[0],
                            (String) objects[1],
                            (String) objects[2],
                            (String) objects[3],
                            (String) objects[4],
                            (Integer) objects[5],
                            (String) objects[6],
                            (Integer) objects[7],
                            (String) objects[8],
                            (Integer) objects[9],
                            false,
                            (String) objects[10],
                            (String) objects[11],
                            objects[12] != null ? ((BigDecimal) objects[12]).doubleValue() : 0.0,
                            (Integer) objects[13],
                            (String) objects[14]);

                    String cadenaArticulo = new StringBuilder().append(p.getNombre())
                            .append("=>").append(p.getCodigo())
                            .append("=>").append(p.getUnidadNombre())
                            .append("=>").append(p.getNumParte())
                            .toString().toUpperCase();
                    SelectItem item = new SelectItem(p, cadenaArticulo.toUpperCase());
                    lstArticulos.add(item);
                }
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e);
            lstArticulos = new ArrayList<SelectItem>();
        }
        return lstArticulos;
    }

    
    @Override
    public List<SelectItem> obtenerArticulosItemsUsuario(String texto, String codigo, int categoriaID, String codigosCategorias, String usuarioID) {
        List<SelectItem> lstArticulos = new ArrayList<SelectItem>();
        try {
            StringBuilder sb = new StringBuilder();
            //                   0      1              2            3        4              5         6                          7               8        
            sb.append(" select a.ID, a.CODIGO, a.CODIGO_EAN13, a.NOMBRE, a.DESCRIPCION, a.UNIDAD, u.NOMBRE as UNIDADNOMBRE, a.codigo_int, a.categorias "
                    + " from INV_ARTICULO_CAMPO ac "
                    + " inner join INV_ARTICULO a on a.ID = ac.INV_ARTICULO and a.ELIMINADO = 'False' "
                    + " and a.NOMBRE is not null and a.NOMBRE <> '' and a.CODIGO is not null ");
            if (categoriaID > 0) {
                sb.append(" and a.SI_CATEGORIA = ").append(categoriaID);
            }
            sb.append(" inner join AP_CAMPO c on c.ID = ac.AP_CAMPO and c.ELIMINADO = 'False' ");
            if (usuarioID != null && !usuarioID.isEmpty()) {
                sb.append("and c.ID in (select ap_campo from AP_CAMPO_USUARIO_RH_PUESTO where ELIMINADO = 'False' and USUARIO = '").append(usuarioID).append("') ");
            }

            sb.append(" inner join SI_UNIDAD u on u.id = a.UNIDAD and u.ELIMINADO = 'False' "
                    + " where ac.ELIMINADO = 'False' ");

            if (codigosCategorias != null && !codigosCategorias.isEmpty()) {
                sb.append(codigosCategorias);
            }

            if (codigo != null && !codigo.isEmpty()) {
                sb.append(" and upper(a.CODIGO_INT) = upper('").append(codigo).append("') ");
            }

            if (texto != null && !texto.isEmpty()) {
                sb.append(texto);
            }

            sb.append(" GROUP BY ID,CODIGO,CODIGO_EAN13,NOMBRE,DESCRIPCION,UNIDAD,UNIDADNOMBRE,codigo_int,categorias "
                    + " order by a.NOMBRE ");

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                for (Object[] objects : lo) {
                    ArticuloVO p = new ArticuloVO(
                            (Integer) objects[0],
                            (String) objects[1],
                            (String) objects[2],
                            (String) objects[3],
                            (String) objects[4],
                            (Integer) objects[5],
                            (String) objects[6],
                            false,
                            (String) objects[7],
                            (String) objects[8]);

                    String cadenaArticulo = new StringBuilder().append(p.getNombre())
                            .append("=>").append(p.getCodigo())
                            .append("=>").append(p.getUnidadNombre())
                            .append("=>").append(p.getNumParte())
                            .toString().toUpperCase();
                    SelectItem item = new SelectItem(p, cadenaArticulo.toUpperCase());
                    lstArticulos.add(item);
                }
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e);
            lstArticulos = new ArrayList<SelectItem>();
        }
        return lstArticulos;
    }

    
    @Override
    public ArticuloVO obtenerArticulos(int articuloID, int campoID) {
        ArticuloVO articulo = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select a.ID, a.CODIGO, a.CODIGO_EAN13, a.NOMBRE, a.DESCRIPCION, a.UNIDAD, u.NOMBRE, ac.AP_CAMPO, "
                    + "c.NOMBRE, ac.ID, a.codigo_int, a.categorias "
                    + " , ac.precio, m.id , m.nombre"
                    + " from INV_ARTICULO_CAMPO ac "
                    + " inner join INV_ARTICULO a on a.ID = ac.INV_ARTICULO and a.ELIMINADO = 'False' "
                    + " and a.NOMBRE is not null and a.NOMBRE <> '' and a.CODIGO is not null"
                    + " inner join AP_CAMPO c on c.ID = ac.AP_CAMPO and c.ELIMINADO = 'False' "
            );
            if (campoID > 0) {
                sb.append(" and ac.AP_CAMPO = ").append(campoID);
            }
            sb.append(" left join moneda m on m.id = ac.moneda");

            sb.append(" inner join SI_UNIDAD u on u.id = a.UNIDAD and u.ELIMINADO = 'False' "
                    + " where ac.ELIMINADO = 'False' ");

            if (articuloID > 0) {
                sb.append(" and ac.INV_ARTICULO = ").append(articuloID);
            }
            sb.append(" limit 1 ");
            Object[] lo = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();

            if (lo != null) {
                articulo = new ArticuloVO(
                        (Integer) lo[0],
                        (String) lo[1],
                        (String) lo[2],
                        (String) lo[3],
                        (String) lo[4],
                        (Integer) lo[5],
                        (String) lo[6],
                        (Integer) lo[7],
                        (String) lo[8],
                        (Integer) lo[9], false,
                        (String) lo[10],
                        (String) lo[11],
                        lo[12] != null ? ((BigDecimal) lo[12]).doubleValue() : 0.0,
                        (Integer) lo[13],
                        (String) lo[14]
                );

            }
        } catch (Exception e) {
            LOGGER.fatal(this, e);
        }
        return articulo;
    }

    
    @Override
    public void crear(ArticuloVO articuloVO, String username, int campo) throws SIAException {
        try {

            EjbLog.info("ArticuloImpl.create()");

            InvArticulo articulo = new InvArticulo();
            Usuario usuario = new Usuario(username);

            articulo.setCodigo(articuloVO.getCodigo().replaceAll("-", ""));
            articulo.setNombre(articuloVO.getNombre());
            articulo.setDescripcion(articuloVO.getDescripcion());
            articulo.setUnidad(new SiUnidad(articuloVO.getUnidadId()));
            articulo.setEliminado(Constantes.BOOLEAN_FALSE);
            //
            articulo.setGenero(usuario);
            articulo.setFechaGenero(new Date());
            articulo.setHoraGenero(new Date());
            articulo.setCategorias(articuloVO.getCategorias());
            articulo.setCodigoInt(articuloVO.getCodigo().replaceAll("-", ""));

            this.audit.register(AuditActions.CREATE, articulo, usuario);

            super.create(articulo);

            articuloVO.setId(articulo.getId());

            EjbLog.info("Articulo creado exitosamente.");
            //
            articuloVO.setId(articulo.getId());
            invArticuloCampoRemote.guardarArticulo(username, articuloVO, campo);
        } catch (Exception ex) {
            throw new SIAException(ex);
        }
    }

    
    @Override
    public List<ArticuloVO> articulosFrecuentes(String usr, int campoID) {
        List<ArticuloVO> lstArticulos = new ArrayList<ArticuloVO>();
        try {

            //El número de registros a recuperar está relacionado al número de elementos a mostrar
            //en la vista (popupRequisicion.xhtml)
            final String sql
                    = "WITH requis AS (\n"
                    + "	SELECT rd.inv_articulo, count(rd.inv_articulo) AS xx \n"
                    + "	FROM requisicion r \n"
                    + "		INNER JOIN requisicion_detalle rd ON rd.requisicion = r.id AND rd.inv_articulo IS NOT NULL \n"
                    + "	WHERE r.eliminado = 'False' \n"
                    + "		AND r.solicita = ? \n"
                    + "		AND r.ap_campo = ? \n"
                    + "	GROUP BY rd.inv_articulo \n"
                    + "	ORDER BY xx DESC \n"
                    + "	limit 10 \n"
                    + ") SELECT a.id, a.codigo_int, a.nombre\n"
                    + "FROM inv_articulo a \n"
                    + "	INNER JOIN requis r ON r.inv_articulo = a.id\n"
                    + "ORDER BY a.codigo_int";

            LOGGER.info(this, "traer articulos frecuentes :: " + sql);

            List<Object[]> lo
                    = em.createNativeQuery(sql)
                            .setParameter(1, usr)
                            .setParameter(2, campoID)
                            .getResultList();

            if (lo != null) {
                for (Object[] objects : lo) {
                    lstArticulos.add(new ArticuloVO(
                            (Integer) objects[0],
                            (String) objects[1],
                            (String) objects[2]
                    ));
                }
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e);
            lstArticulos = new ArrayList<ArticuloVO>();
        }
        return lstArticulos;
    }

    
    @Override
    public void cambiarArticulo(String id, List<ArticuloVO> listaCambiarArticulos, List<CategoriaVo> listaCambiarSeleccionada) {
        for (ArticuloVO listaCambiarArticulo : listaCambiarArticulos) {
            // System.out.println("uno");
            InvArticulo articuloVO = find(listaCambiarArticulo.getId());
            articuloVO.setCodigo(construirCodigo(listaCambiarSeleccionada));
            articuloVO.setModifico(new Usuario(id));
            articuloVO.setFechaModifico(new Date());
            articuloVO.setHoraModifico(new Date());
            articuloVO.setSiCategoria(new SiCategoria(listaCambiarSeleccionada.get(0).getId()));
            articuloVO.setCategorias(listaCategorias(listaCambiarSeleccionada));
            edit(articuloVO);
        }
    }

    
    @Override
    public String construirCodigo(List<CategoriaVo> categorias) {
        String cod = "";
        try {
            if (categorias != null) {
                for (CategoriaVo catVo : categorias) {
                    if (catVo.getCodigo() != null && !catVo.getCodigo().isEmpty()) {
                        if (cod.isEmpty()) {
                            cod = catVo.getCodigo();
                        } else {
                            cod += catVo.getCodigo();
                        }
                    }
                }
            }
            if (cod.length() < 24) {
                int codNum = folioRemote.traerFolio("ARTICULO_FOLIO");
                cod = String.format(cod + "%0" + (24 - cod.length()) + "d", codNum);
            }

        } catch (Exception e) {
            LOGGER.warn(this, "", e);
        }
        return cod;
    }

    private String listaCategorias(List<CategoriaVo> listaCat) {
        String cats = "";
        for (CategoriaVo listaCat1 : listaCat) {
            if (listaCat1.getId() > 0) {
                if (cats.isEmpty()) {
                    cats = "" + listaCat1.getId();
                } else {
                    cats += "," + listaCat1.getId();
                }
            }
        }
        return cats;
    }

    
    @Override
    public List<ArticuloVO> articulosFrecuentesOrden(String usr, int campoID) {
        List<ArticuloVO> lstArticulos = new ArrayList<ArticuloVO>();
        try {

            //El número de registros a recuperar está relacionado al número de elementos a mostrar
            //en la vista (popupSolicitarOrden.xhtml)
            final String sql
                    = "WITH ordenados AS (\n"
                    + "	SELECT od.inv_articulo, count(od.inv_articulo) AS xx  \n"
                    + "	FROM orden o \n"
                    + "		INNER JOIN orden_detalle od ON od.orden =  o.id\n"
                    + "		INNER JOIN autorizaciones_orden ao ON ao.orden =  o.id \n"
                    + "	WHERE o.eliminado = 'False'  \n"
                    + "		AND od.inv_articulo IS NOT NULL  \n"
                    + "		AND ao.solicito = ? \n"
                    + "		AND o.ap_campo = ? \n"
                    + "	GROUP BY od.inv_articulo\n"
                    + "	ORDER BY xx DESC\n"
                    + "	limit 10 \n"
                    + ")\n"
                    + "SELECT a.id, a.codigo_int, a.nombre\n"
                    + "FROM inv_articulo a\n"
                    + "	INNER JOIN ordenados o ON o.inv_articulo = a.id\n"
                    + "ORDER BY a.codigo";

            LOGGER.info("traer articulos frecuentes :: " + sql);

            List<Object[]> lo
                    = em.createNativeQuery(sql)
                            .setParameter(1, usr)
                            .setParameter(2, campoID)
                            .getResultList();

            if (lo != null) {
                for (Object[] objects : lo) {
                    lstArticulos.add(new ArticuloVO(
                            (Integer) objects[0],
                            (String) objects[1],
                            (String) objects[2]
                    ));
                }
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e);
            lstArticulos = new ArrayList<ArticuloVO>();
        }
        return lstArticulos;
    }

    
    @Override
    public boolean existeArticuloConCodigo(String codigo, int campo) {
        ArticuloVO filtro = new ArticuloVO();
        filtro.setCodigo(codigo);
        return !buscarPorFiltros(filtro, campo).isEmpty();
    }

    
    @Override
    public List<SelectItem> obtenerCategorias(int campoID) {
        List<SelectItem> lstCategorias = new ArrayList<SelectItem>();
        try {
            String sb = "select xx, xxx "
                    + " from ( "
                    + " select DISTINCT  "
                    + " case when (c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE ||'->'|| ch3.NOMBRE ||'->'|| ch4.NOMBRE ||'->'|| ch5.NOMBRE) is not null   "
                    + " 		then c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE ||'->'|| ch3.NOMBRE ||'->'|| ch4.NOMBRE ||'->'|| ch5.NOMBRE  "
                    + " 	else case  "
                    + " 		when (c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE ||'->'|| ch3.NOMBRE ||'->'|| ch4.NOMBRE) is not null  "
                    + " 			then c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE ||'->'|| ch3.NOMBRE ||'->'|| ch4.NOMBRE  "
                    + " 		else case  "
                    + " 			when (c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE ||'->'|| ch3.NOMBRE) is not null   "
                    + " 				then c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE ||'->'|| ch3.NOMBRE  "
                    + " 			else case				  "
                    + " 				when (c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE) is not null   "
                    + " 					then c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE  "
                    + " 				else case  "
                    + " 					when (c.NOMBRE ||'->'|| ch1.NOMBRE) is not null  "
                    + " 						then c.NOMBRE ||'->'|| ch1.NOMBRE  "
                    + " 					else c.NOMBRE  "
                    + " 					end  "
                    + " 			end  "
                    + " 		end  "
                    + " 	end  "
                    + " end as xx,  "
                    + " case when (c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE ||'->'|| ch3.NOMBRE ||'->'|| ch4.NOMBRE ||'->'|| ch5.NOMBRE) is not null   "
                    + " 		then ' and upper(a.CODIGO) like upper(''%'||c.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch1.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch2.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch3.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch4.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch5.CODIGO||'%'')'  "
                    + " 	else case  "
                    + " 		when (c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE ||'->'|| ch3.NOMBRE ||'->'|| ch4.NOMBRE) is not null  "
                    + " 			then ' and upper(a.CODIGO) like upper(''%'||c.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch1.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch2.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch3.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch4.CODIGO||'%'')'  "
                    + " 		else case  "
                    + " 			when (c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE ||'->'|| ch3.NOMBRE) is not null   "
                    + " 				then ' and upper(a.CODIGO) like upper(''%'||c.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch1.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch2.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch3.CODIGO||'%'')'  "
                    + " 			else case				  "
                    + " 				when (c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE) is not null   "
                    + " 					then ' and upper(a.CODIGO) like upper(''%'||c.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch1.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch2.CODIGO||'%'')'  "
                    + " 				else case  "
                    + " 				when (c.NOMBRE ||'->'|| ch1.NOMBRE) is not null  "
                    + " 				then ' and upper(a.CODIGO) like upper(''%'||c.CODIGO ||'%'') and upper(a.CODIGO) like upper(''%'|| ch1.CODIGO||'%'')'  "
                    + " 				else ' and upper(a.CODIGO) like upper(''%'||c.CODIGO||'%'')'  "
                    + " 				end  "
                    + " 				end  "
                    + " 				end  "
                    + " 				end  "
                    + " 				end as xxx,   "
                    + " 				case when (c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE ||'->'|| ch3.NOMBRE ||'->'|| ch4.NOMBRE ||'->'|| ch5.NOMBRE) is not null  "
                    + " 						then CAST((c.ID || ch1.ID || ch2.ID || ch3.ID || ch4.ID|| ch5.ID) AS BIGINT) "
                    + " 				else case "
                    + " 				when (c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE ||'->'|| ch3.NOMBRE ||'->'|| ch4.NOMBRE) is not null "
                    + " 				then CAST((c.ID || ch1.ID || ch2.ID || ch3.ID || ch4.ID) AS BIGINT) "
                    + " 				else case "
                    + " 				when (c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE ||'->'|| ch3.NOMBRE) is not null  "
                    + " 				then CAST((c.ID || ch1.ID || ch2.ID || ch3.ID) AS BIGINT) "
                    + " 				else case				 "
                    + " 				when (c.NOMBRE ||'->'|| ch1.NOMBRE ||'->'|| ch2.NOMBRE) is not null  "
                    + " 				then CAST((c.ID || ch1.ID || ch2.ID) AS BIGINT) "
                    + " 				else case "
                    + " 				when (c.NOMBRE ||'->'|| ch1.NOMBRE) is not null "
                    + " 				then CAST((c.ID || ch1.ID) AS BIGINT) "
                    + " 				else CAST((c.ID) AS BIGINT) "
                    + " 				end "
                    + " 				end "
                    + " 				end "
                    + " 				end "
                    + " 				end as xxxxxx "
                    + " 				from SI_CATEGORIA c  "
                    + " 				INNER JOIN SI_REL_CATEGORIA h1 on h1.SI_CATEGORIA_PADRE = c.id and h1.ELIMINADO = 'False'  "
                    + " 				INNER JOIN SI_CATEGORIA ch1 on ch1.ID = h1.SI_CATEGORIA and ch1.ELIMINADO = 'False'  "
                    + " 				LEFT JOIN SI_REL_CATEGORIA h2 on h2.SI_CATEGORIA_PADRE = ch1.id  and h2.ELIMINADO = 'False'  "
                    + " 				LEFT JOIN SI_CATEGORIA ch2 on ch2.ID = h2.SI_CATEGORIA and ch2.ELIMINADO = 'False'  "
                    + " 				LEFT JOIN SI_REL_CATEGORIA h3 on h3.SI_CATEGORIA_PADRE = ch2.id  and h3.ELIMINADO = 'False'  "
                    + " 				LEFT JOIN SI_CATEGORIA ch3 on ch3.ID = h3.SI_CATEGORIA and ch3.ELIMINADO = 'False'  "
                    + " 				LEFT JOIN SI_REL_CATEGORIA h4 on h4.SI_CATEGORIA_PADRE = ch3.id  and h4.ELIMINADO = 'False'  "
                    + " 				LEFT JOIN SI_CATEGORIA ch4 on ch4.ID = h4.SI_CATEGORIA and ch4.ELIMINADO = 'False'  "
                    + " 				LEFT JOIN SI_REL_CATEGORIA h5 on h5.SI_CATEGORIA_PADRE = ch4.id  and h5.ELIMINADO = 'False'  "
                    + " 				LEFT JOIN SI_CATEGORIA ch5 on ch5.ID = h5.SI_CATEGORIA and ch5.ELIMINADO = 'False'  "
                    + " 				where c.ELIMINADO = 'False'  "
                    + " 				and (select count(ID) from SI_REL_CATEGORIA where SI_CATEGORIA = c.ID) = 0 "
                    + " 				) AS T1 "
                    + " 				JOIN ( "
                    + " 				SELECT CAST((replace (ia.categorias,  ',', '')) AS BIGINT) as categoria "
                    + " 				from INV_ARTICULO_CAMPO iac   "
                    + " 				inner join INV_ARTICULO ia on ia.id = iac.INV_ARTICULO and ia.ELIMINADO = 'False'   "
                    + " 				where iac.ELIMINADO = 'False'               "
                    + " 				and ia.CATEGORIAS is not null ";
            if (campoID > 0) {
                sb += " 			and iac.AP_CAMPO = " + campoID;
            }
            sb += " 				) AS T2 ON T1.xxxxxx = T2.categoria "
                    + " 				GROUP BY xx, xxx ";

            List<Object[]> lo = em.createNativeQuery(sb).getResultList();
            if (lo != null) {
                for (Object[] objects : lo) {
                    lstCategorias.add(new SelectItem(
                            (String) objects[1],
                            (String) objects[0]));
                }
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e);
            lstCategorias = Collections.emptyList();
        }
        return lstCategorias;
    }

    
    @Override
    public InvArticulo buscarPorNombre(String nombre, int unidadID) {
        InvArticulo retVal = null;
        try {
            String consulta
                    = "SELECT * from INV_ARTICULO where upper(replace(nombre, '''' , '')) = upper(?) and unidad = ? and ELIMINADO = 'False'";

            retVal
                    = (InvArticulo) em.createNativeQuery(consulta, InvArticulo.class)
                            .setParameter(1, nombre)
                            .setParameter(2, unidadID)
                            .getSingleResult();
        } catch (Exception e) {
            LOGGER.info(this, "", e);
        }

        return retVal;
    }

    
    @Override
    public InvArticulo buscarPorCodigoInterno(String codigoInt, int unidadID) {
        InvArticulo retVal = null;

        try {
            String consulta
                    = "SELECT * from INV_ARTICULO where upper(replace(codigo_int, '''' , '')) = upper(?) and unidad = ? and ELIMINADO = 'False'";

            retVal
                    = (InvArticulo) em.createNativeQuery(consulta, InvArticulo.class)
                            .setParameter(1, codigoInt)
                            .setParameter(2, unidadID)
                            .getSingleResult();
        } catch (Exception e) {
            LOGGER.info(this, "", e);
        }

        return retVal;
    }

    
    @Override
    public List<ArticuloVO> buscarArticuloSinCategoriaPorGenero(String sesion) {
        List<ArticuloVO> retVal;

        try {
            String consulta
                    = "SELECT a.id, a.NOMBRE, a.DESCRIPCION, u.NOMBRE, ca.item from INV_ARTICULO a \n"
                    + "	inner join SI_UNIDAD u on a.UNIDAD = u.id \n"
                    + " inner join CV_CONVENIO_ARTICULO ca on ca.INV_ARTICULO = a.id and ca.eliminado = 'False' \n"
                    + " where a.SI_CATEGORIA is null \n"
                    + " and a.ELIMINADO = 'False' \n"
                    + " and a.genero = ? ";

            List<Object[]> lo
                    = em.createNativeQuery(consulta)
                            .setParameter(1, sesion)
                            .getResultList();

            retVal = new ArrayList<ArticuloVO>();

            for (Object[] objects : lo) {
                ArticuloVO avo = new ArticuloVO();
                avo.setId((Integer) objects[0]);
                avo.setNombre((String) objects[1]);
                avo.setDescripcion((String) objects[2]);
                avo.setUnidadNombre((String) objects[3]);
                avo.setCodigo((String) objects[4]);
                avo.setSelected(Constantes.FALSE);
                retVal.add(avo);
            }

        } catch (Exception e) {
            LOGGER.warn(this, "", e);
            retVal = null;
        }

        return retVal;
    }

    
    @Override
    public void agregarCategoriaArticulo(String id, List<ArticuloVO> latemp, List<CategoriaVo> categorias) {
        for (ArticuloVO articuloVO : latemp) {
            try {
                //
                InvArticulo articulo = find(articuloVO.getId());
                articulo.setCodigo(construirCodigo(categorias));
                articulo.setSiCategoria(new SiCategoria(categorias.get(categorias.size() - 1).getId()));
                articulo.setCategorias(listaCategorias(categorias));
                articulo.setModifico(new Usuario(id));
                articulo.setFechaModifico(new Date());
                articulo.setHoraModifico(new Date());
                edit(articulo);
            } catch (Exception e) {
                LOGGER.warn(this, "", e);
            }
        }
    }
    
     
    @Override
    public InvArticulo buscarPorCodigo(String codigo, int unidadID) {
        InvArticulo retVal = null;

        try {
            String consulta
                    = "SELECT * from INV_ARTICULO where upper(replace(codigo, '''' , '')) = upper(?) and unidad = ? and ELIMINADO = false ";

            retVal
                    = (InvArticulo) em.createNativeQuery(consulta, InvArticulo.class)
                            .setParameter(1, codigo)
                            .setParameter(2, unidadID)
                            .getSingleResult();
        } catch (Exception e) {
            LOGGER.info(this, "", e);
        }

        return retVal;
    }

    
}
