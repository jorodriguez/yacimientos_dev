package sia.servicios.oficio.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.InsufficientPermissionsException;
import sia.modelo.OfOficio;
import sia.modelo.campo.usuario.puesto.vo.CompaniaBloqueGerenciaVo;
import sia.modelo.oficio.vo.AdjuntoOficioVo;
import sia.modelo.oficio.vo.MovimientoVo;
import sia.modelo.oficio.vo.OficioEntradaVo;
import sia.modelo.oficio.vo.OficioPromovibleVo;
import sia.modelo.oficio.vo.OficioSalidaVo;
import sia.modelo.oficio.vo.OficioVo;
import sia.modelo.oficio.vo.PermisosVo;
import sia.modelo.oficio.vo.PrivacidadOficio;
import sia.modelo.oficio.vo.Promovible;
import sia.modelo.oficio.vo.ResultadosConsultaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.util.UtilLog4j;
//import sia.util.UtilLog4j;
import sia.util.UtilSia;

/**
 * Implementa los métodos de negocio para el módulo de Control de Oficios.
 *
 * @author esapien
 */
@Stateless
public class OfOficioConsultaImpl extends AbstractFacade<OfOficio> {

    @Inject
    private OfOficioUsuarioImpl oficioUsuarioRemote;

    @Inject
    private OfOficioAsociadoImpl oficioAsociadoRemoto;

    @Inject
    private OfOficioUsuarioImpl oficioUsuarioRemoto;

    @Inject
    private UsuarioImpl usuarioServicioRemoto;

    private final String queryBase = "select "
            // tipo de oficio
            + " tof.id as ID_TIPO_OFICIO, \n"
            + " tof.nombre as TIPO_OFICIO, \n"
            // oficio
            + " of1.ID as ID_OFICIO, \n"
            + " of1.numero_oficio, \n"
            + " of1.fecha_oficio, \n"
            + " of1.fecha_genero, \n"
            + " of1.asunto as asunto_oficio, \n"
            + " of1.observaciones as observaciones_oficio, \n"
            // compañía
            + " c.rfc, \n"
            + " c.NOMBRE as COMPANIA,\n"
            + " c.SIGLAS as COMPANIA_SIGLAS,\n"
            // bloque
            + " ap_c.id as ID_BLOQUE, \n"
            + " ap_c.NOMBRE as BLOQUE, \n"
            // gerencia
            + " g.id as ID_GERENCIA, \n"
            + " g.nombre as GERENCIA, \n"
            // estatus
            + " est.id as ID_ESTATUS, \n"
            + " est.nombre as ESTATUS, \n"
            // usuario
            + " us1.id as USUARIO_GENERO_ID, \n"
            + " us1.nombre as USUARIO_GENERO, \n"
            + " of1.eliminado, \n"
            + " of1.urgente, \n"
            + " of1.seguimiento, \n"
            + " of1.co_privacidad, \n"
            + " (SELECT array_agg(usuario) FROM OF_OFICIO_USUARIO WHERE of_oficio = of1.id) \n"
            + "from OF_OFICIO of1 \n"
            + " inner join AP_CAMPO_GERENCIA ap_cg on (of1.AP_CAMPO_GERENCIA = ap_cg.ID) \n"
            + " inner join AP_CAMPO ap_c on (ap_cg.AP_CAMPO = ap_c.ID) \n"
            + " inner join GERENCIA g on (ap_cg.GERENCIA = g.ID) \n"
            + " inner join COMPANIA c on (ap_c.COMPANIA = c.RFC) \n"
            + " inner join OF_TIPO_OFICIO tof on (of1.OF_TIPO_OFICIO = tof.ID) \n"
            + " inner join ESTATUS est on (of1.ESTATUS = est.ID) \n"
            + " inner join USUARIO us1 on (of1.GENERO = us1.ID) \n"
            + "where 1=1 \n";
    ;

    private final String queryBaseGroupBy = getQueryBaseGroupBy();

    //private final String queryOficiosAsociados;
    private final String queryMovimientos = "SELECT \n"
            + "  ofic_mov.ID as ID_OFICIO_MOVIMIENTO,  \n"
            + "  ofic.ID as ID_OFICIO,  \n"
            + "  ofic.NUMERO_OFICIO,  \n"
            + "  oper.ID as ID_OPERACION, \n"
            + "  oper.NOMBRE as OPERACION, \n"
            + "  mov.ID as MOVIMIENTO_ID,  \n"
            + "  mov.MOTIVO as MOTIVO,  \n"
            + "  adj.ID as ADJUNTO_ID, \n"
            + "  adj.NOMBRE as ADJUNTO_NOMBRE, \n"
            + "  adj.URL as ADJUNTO_URL, \n"
            + "  adj.TIPO_ARCHIVO as ADJUNTO_TIPO_ARCHIVO, \n"
            + "  adj.PESO as ADJUNTO_PESO, \n"
            + "  adj.UUID as ADJUNTO_UUID, \n"
            + "  adj.ELIMINADO as ADJUNTO_ELIMINADO, \n"
            + "  mov.GENERO as USUARIO_MOVIMIENTO,  \n"
            + "  usu.NOMBRE as USUARIO_NOMBRE,  \n"
            + "  mov.FECHA_GENERO as FECHA_MOVIMIENTO,  \n"
            + "  mov.HORA_GENERO as HORA_MOVIMIENTO \n"
            + "FROM SI_MOVIMIENTO mov \n"
            + "  inner join USUARIO usu on (mov.GENERO = usu.ID) \n"
            + "  inner join OF_OFICIO_SI_MOVIMIENTO ofic_mov on (ofic_mov.SI_MOVIMIENTO = mov.ID) \n"
            + "  inner join OF_OFICIO ofic on (ofic_mov.OF_OFICIO = ofic.ID) \n"
            + "  inner join ESTATUS est on (ofic.ESTATUS = est.ID) \n"
            + "  inner join SI_OPERACION oper on (mov.SI_OPERACION = oper.ID) \n"
            + "  left outer join OF_OFICIO_SI_MOV_SI_ADJUNTO ofic_mov_adj on (ofic_mov_adj.OF_OFICIO_SI_MOVIMIENTO = ofic_mov.ID) \n"
            + "  left outer join SI_ADJUNTO adj on (ofic_mov_adj.SI_ADJUNTO = adj.ID) \n"
            + "WHERE 1=1 \n";

    ;

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Métodos del ciclo de vida del bean">
    /**
     * Constructor
     *
     */
    public OfOficioConsultaImpl() {

        super(OfOficio.class);
    }

    private String getQueryBaseGroupBy() {

        return " GROUP BY \n"
                + " tof.id, \n"
                + " tof.nombre, \n"
                // oficio
                + " of1.ID, \n"
                + " of1.numero_oficio, \n"
                + " of1.fecha_oficio, \n"
                + " of1.fecha_genero, \n"
                + " of1.asunto, \n"
                + " of1.observaciones, \n"
                // compañía
                + " c.rfc, \n"
                + " c.NOMBRE,\n"
                + " c.SIGLAS,\n"
                // bloque
                + " ap_c.id, \n"
                + " ap_c.NOMBRE, \n"
                // gerencia
                + " g.id, \n"
                + " g.nombre, \n"
                // estatus
                + " est.id, \n"
                + " est.nombre, \n"
                // usuario
                + " us1.id, \n"
                + " us1.nombre, \n"
                + " of1.eliminado, \n"
                + " of1.urgente, \n"
                + " of1.seguimiento, \n"
                + " of1.co_privacidad";

    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Métodos de negocio">
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Realiza una búsqueda de registros de oficios con los parámetros de
     * consulta especificados.
     *
     * @param vo Objeto contenedor con los parámetros de consulta.
     * @return Lista con los resultados. En caso de no encontrar registros,
     * retorna lista vacía (nunca nulo).
     */
    public List<OficioPromovibleVo> buscarOficios(OficioVo vo) {

        getLogger().info(this, "@buscarOficios - params = {0}", new Object[]{vo});

        System.out.println("@buscar oficios " + vo.toString());

        // preparar query en función de los parámetros recibidos
        final StringBuilder sql = new StringBuilder();

        sql.append(queryBase);

        final List<Object> params = new ArrayList<>();

        if (!UtilSia.isNullOrBlank(vo.getCompaniaRfc()) && UtilSia.greaterThanZero(vo.getBloqueId())
                && UtilSia.greaterThanZero(vo.getGerenciaId())) {
            sql.append("and ( (of1.co_privacidad <= 4 "
                    + " and c.rfc COLLATE \"es_ES\" = ? "); // compañia
            params.add(vo.getCompaniaRfc().trim());

            sql.append("and ap_c.id = ? "); // bloque
            params.add(vo.getBloqueId());

            sql.append("and g.id = ? )"); // gerencia
            params.add(vo.getGerenciaId());

            // mostrar los oficios publicos
            if (vo.getMostrarPublicos() != Constantes.CERO) {
                sql.append(" or(of1.co_privacidad = 1")
                        .append(" and c.rfc COLLATE \"es_ES\" = ? "); // compañia
                params.add(vo.getCompaniaRfc().trim());

                sql.append("and ap_c.id = ? )"); // bloque
                params.add(vo.getBloqueId());
            }

            sql.append(" ) ");
        } else if (!UtilSia.isNullOrBlank(vo.getCompaniaRfc()) && UtilSia.greaterThanZero(vo.getBloqueId())) { //busqueda pro compañia y bloque
            sql.append("and ( of1.co_privacidad <= 4 and ")
                    .append(" c.rfc COLLATE \"es_ES\" = ? "); // compañia
            params.add(vo.getCompaniaRfc().trim());

            sql.append("and ap_c.id = ? "); // bloque
            params.add(vo.getBloqueId());

            sql.append("  ) ");
        } else if (!UtilSia.isNullOrBlank(vo.getCompaniaRfc())) { //AQUI ENTRA A BUSCAR POR COMPAÑIA
            sql.append("and ( of1.co_privacidad <= 4 AND  ")
                    .append(" c.rfc COLLATE \"es_ES\" = ? "); // compañia

            params.add(vo.getCompaniaRfc().trim());
            sql.append(" )");
        }

        if (UtilSia.greaterThanZero(vo.getEstatusId())) {
            sql.append("and est.id = ? "); // estatus
            params.add(vo.getEstatusId());
        }

        if (!UtilSia.isNullOrBlank(vo.getOficioNumero())) {
            sql.append("and UPPER(of1.numero_oficio) COLLATE \"es_ES\" like UPPER(?) "); // num oficio
            params.add("%" + vo.getOficioNumero().trim() + "%");
        }

        if (!UtilSia.isNullOrBlank(vo.getOficioAsunto())) {
            sql.append("and UPPER(of1.asunto) COLLATE \"es_ES\" like UPPER(?) "); // asunto
            params.add("%" + vo.getOficioAsunto().trim() + "%");
        }

        if (!UtilSia.isNullOrBlank(vo.getObservaciones())) {
            sql.append("and UPPER(of1.observaciones) COLLATE \"es_ES\" like UPPER(?) "); // descr oficio
            params.add("%" + vo.getObservaciones().trim() + "%");
        }

        if (UtilSia.greaterThanZero(vo.getTipoOficioId())) {
            sql.append("and tof.id = ? "); // tipo oficio
            params.add(vo.getTipoOficioId());
        }

        // aplicar fechas de oficio
        if (vo.getOficioFechaDesde() != null && vo.getOficioFechaHasta() != null) {
            sql.append("and of1.fecha_oficio between ? and ? ");
            params.add(vo.getOficioFechaDesde());
            params.add(vo.getOficioFechaHasta());
        } else if (vo.getOficioFechaDesde() != null && vo.getOficioFechaHasta() == null) {
            sql.append("and of1.fecha_oficio >= ? ");
            params.add(vo.getOficioFechaDesde());
        } else if (vo.getOficioFechaDesde() == null && vo.getOficioFechaHasta() != null) {
            sql.append("and of1.fecha_oficio <= ? ");
            params.add(vo.getOficioFechaHasta());
        }

        // aplicar fechas de alta
        if (vo.getFechaAltaDesde() != null && vo.getFechaAltaHasta() != null) {
            sql.append("and of1.fecha_genero between ? and ? ");
            params.add(vo.getFechaAltaDesde());
            params.add(vo.getFechaAltaHasta());
        } else if (vo.getFechaAltaDesde() != null && vo.getFechaAltaHasta() == null) {
            sql.append("and of1.fecha_genero >= ? ");
            params.add(vo.getFechaAltaDesde());
        } else if (vo.getFechaAltaDesde() == null && vo.getFechaAltaHasta() != null) {
            sql.append("and of1.fecha_genero <= ? ");
            params.add(vo.getFechaAltaHasta());
        }

        // para consulta de oficios para asociación
        if (UtilSia.greaterThanZero(vo.getOficioIdExcluir())) {
            sql.append("and of1.id <> ? ");
            params.add(vo.getOficioIdExcluir());
        }

        // requiere seguimiento
        if (vo.getSeguimientoConsulta() != Constantes.OFICIOS_CONSULTA_SEGUIMIENTO_TODOS) {
            sql.append("and of1.seguimiento = ? ");

            switch (vo.getSeguimientoConsulta()) {
                case Constantes.OFICIOS_CONSULTA_SEGUIMIENTO_SI:
                    params.add(Constantes.BOOLEAN_TRUE);
                    break;
                case Constantes.OFICIOS_CONSULTA_SEGUIMIENTO_NO:
                    params.add(Constantes.BOOLEAN_FALSE);
                    break;
            }
        }

        //sb.append(queryBaseGroupBy);
        //sb.append(" ORDER BY of1.FECHA_OFICIO DESC "); se cambia a ordenar como en bandeja de entrada jevazquez
        sql.append(
                " ORDER BY c.NOMBRE asc, ap_c.NOMBRE asc, g.NOMBRE asc, of1.numero_oficio asc \n"
                + "LIMIT "
        ).append(vo.getMaxOficios());

        getLogger().info(this, "query = {0}", new Object[]{sql});

        System.out.println(" ====== em " + (em == null));

        final Query qryProm = em.createNativeQuery(sql.toString());

        // establecer condiciones de consulta
        int i = 0;

        for (final Object param : params) {
            System.out.println("PARAM " + param);
            qryProm.setParameter(++i, param);
        }

        final List<Object[]> resultado = qryProm.getResultList();

        getLogger().debug(this, "resultados = " + resultado.size());

        return castVo(resultado);

    }

    public List<OficioPromovibleVo> buscarOficiosBandejaEntrada(
            OficioVo oficioVo,
            PermisosVo permisosVo,
            List<CompaniaBloqueGerenciaVo> bloques, int bloqueActivo) {

        getLogger().info(this, "@buscarOficiosBandejaEntrada");

        // preparar query en función de los parámetros recibidos
        final StringBuilder sql = new StringBuilder(300);

        // para los parámetros
        final List<String> params = new ArrayList<>();

        sql.append(queryBase)
                .append(" and ap_c.id = ?::integer");
        params.add(String.valueOf(bloqueActivo));

        sql.append(" and (");

        condicionesBandeja(permisosVo, sql, params);

        sql.append(")");

        // filtros de la forma
        if (oficioVo.getOficioNumero() != null && oficioVo.getOficioNumero().trim().length() > 0) {
            sql.append(" and of1.numero_oficio collate \"es_ES\" like ? "); // num oficio
            params.add("%" + oficioVo.getOficioNumero().trim() + "%");
        }
        if (oficioVo.getOficioAsunto() != null && oficioVo.getOficioAsunto().trim().length() > 0) {
            sql.append(" and of1.asunto collate \"es_ES\" like ? "); // asunto
            params.add("%" + oficioVo.getOficioAsunto().trim() + "%");
        }

        // filtrar por los campos a los que el usuario tiene acceso
//        if (!permisosVo.isVerTodoGerencias()) {
//
//            //agregarFiltroBloques(bloques, sb, params);
//        }
        sql.append(queryBaseGroupBy)
                .append(" ORDER BY of1.fecha_genero desc,trim(upper (c.NOMBRE)) asc, trim(upper(ap_c.NOMBRE)) asc, trim(upper(g.NOMBRE)) asc, trim(upper(of1.numero_oficio)) asc ");

        final Query nativeQuery = em.createNativeQuery(sql.toString());

        // establecer parámetros de busqueda
        int i = 0;

        for (String param : params) {
            nativeQuery.setParameter(++i, param);
        }

        final List<Object[]> resultado = nativeQuery.getResultList();

        return castVo(resultado);

    }

    public List<OficioPromovibleVo> buscarOficiosPorId(List<Integer> ids) {

        List<OficioPromovibleVo> resultado;

        if (ids != null && !ids.isEmpty()) {

            String cadenaIds = UtilSia.toCommaSeparatedString(ids, false);

            // preparar query en función de los parámetros recibidos
            StringBuilder sb = new StringBuilder();

            sb.append(queryBase)
                    .append(" and of1.ID in (")
                    .append(cadenaIds)
                    .append(") ")
                    .append(queryBaseGroupBy);

            Query nativeQuery = em.createNativeQuery(sb.toString());

            resultado = castVo(nativeQuery.getResultList());

        } else {

            // regresar lista vacía
            resultado = new ArrayList<OficioPromovibleVo>();

        }

        return resultado;

    }

    public OficioPromovibleVo buscarOficioVoPorId(
            Integer oficioId,
            String usuarioId,
            boolean validarRestringido) throws InsufficientPermissionsException {

        OficioPromovibleVo vo = buscarOficioVoPorId(oficioId, usuarioId);

        if (validarRestringido && vo.isRestringido()) {

            // validar si el usuario tiene acceso a este oficio
            if (!vo.tieneAccesoOficioRestringido(usuarioId)) {
                throw new InsufficientPermissionsException("El usuario no tiene acceso a este oficio restringido.");
            }
        }

        return vo;
    }

    public OficioPromovibleVo buscarOficioVoPorId(Integer oficioId, String usuarioId) {

        getLogger().info(this, "@buscarOficioVoPorId");

        OficioPromovibleVo resultadoVo = obtenerOficioVo(oficioId);

        // obtener cadena de oficios asociados - removido 15/oct/14
        //resultadoVo.setOficiosAsociados(obtenerOficiosAsociados(resultadoVo));
        //printAsociados(resultadoVo.getOficiosAsociados());
        // obtener oficios hacia los que este oficio está asociado
        resultadoVo.setAsociadoHaciaOficios(
                oficioAsociadoRemoto.buscarOficiosAsociados(oficioId,
                        Constantes.OFICIOS_ASOCIACION_HACIA));

        // obtener oficios por los que este oficio está asociado
        resultadoVo.setAsociadoDesdeOficios(
                oficioAsociadoRemoto.buscarOficiosAsociados(oficioId,
                        Constantes.OFICIOS_ASOCIACION_DESDE));

        // obtener lista de usuariosRey con acceso al oficio restringido
        resultadoVo.setRestringidoAUsuarios(
                obtenerUsuariosOficioRestringido(resultadoVo));

        // obtener historial de movimientos
        resultadoVo.setMovimientos(obtenerMovimientos(oficioId));

        // el archivo adjunto de este oficio será el registrado
        // en el movimiento con el estatus correspondiente
        for (MovimientoVo movimiento : resultadoVo.getMovimientos()) {

            int operacionId = movimiento.getOperacionId();

            Integer movimientoEstatusId = ((Promovible) resultadoVo).getEstatusId(operacionId);

            // se valida contra nulo para validar que es una operacion relacionada
            // con la promocion de estatus de oficio. Si es una operacion no relacionada
            // (ej. activacion o desactivacion de seguimiento) el valor será nulo
            if (UtilSia.greaterThanZero(movimientoEstatusId)) {
                getLogger().info(this,
                        "vo estatus id = " + resultadoVo.getEstatusId() + ", "
                        + "mov estatus id = " + movimientoEstatusId + ", "
                        + "adjunto = " + movimiento.getAdjunto());

                if (resultadoVo.getEstatusId().intValue() == movimientoEstatusId.intValue()) {

                    resultadoVo.setArchivoAdjunto(movimiento.getAdjunto());
                    break;

                }
            }

        }

        // copiar archivos adjuntos de los movimientos a ruta para
        // acceso por el visor de archivos
        /*
	 * ESAPIEN-29/ene/15 - Opción de visor deshabilitado a la fecha. No
	 * se requiere copia temporal.
	 *
         */
        //copiarArchivosAdjuntos(resultadoVo.getMovimientos(), usuarioId);
        return resultadoVo;

    }

    public List<OficioPromovibleVo> buscarOficiosAsociacion(
            OficioVo vo,
            int bloqueId) {

        // preparar query en función de los parámetros recibidos
        StringBuilder sb = new StringBuilder();

        sb.append(queryBase);

        List params = new ArrayList();

        // busqueda por numero de oficio
        if (vo.getOficioNumero() != null && vo.getOficioNumero().trim().length() > 0) {
            sb.append("and of1.numero_oficio COLLATE \"es_ES\" like ? ");
            params.add("%" + vo.getOficioNumero().trim() + "%");
        }

        // para consulta de oficios para asociación
        if (vo.getOficioIdExcluir() != null && vo.getOficioIdExcluir() > 0) {
            sb.append("and of1.id <> ? ");
            params.add(vo.getOficioIdExcluir());
        }

        // excluir los oficios ya asociados
        if (vo.getAsociadoHaciaOficios().size() > 0) {

            sb.append("and of1.id not in (")
                    .append(UtilSia.toCommaSeparatedString(vo.getAsociadoHaciaOficiosListaIds(), false))
                    .append(") ");
        }

        // estatus debe estar terminado
        sb.append("and est.id = ? "); // estatus
        params.add(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO);

        /*if (!puedeVerTodasGerencias) {

	 agregarFiltroBloques(bloques, sb, params);
	 }*/
        // bloque ID
        sb.append("and ap_c.id = ? ");
        params.add(bloqueId);

        sb.append(queryBaseGroupBy)
                .append(" ORDER BY of1.FECHA_OFICIO DESC ");

        getLogger().info(this, "query = " + sb.toString());

        Query q = em.createNativeQuery(sb.toString());

        // establecer condiciones de consulta
        int i = 0;

        for (Object param : params) {
            q.setParameter(++i, param);
        }

        List<Object[]> resultado = q.getResultList();

        getLogger().info(this, "resultados = " + resultado.size());

        return castVo(resultado);

    }

    public List<OficioPromovibleVo> buscarOficiosNoNotificados(final int idCampo) {

        getLogger().info(this, "@buscarOficiosNoNotificados");

        final StringBuilder sql = new StringBuilder(200);

        sql.append(queryBase)// obtener oficios publicos, no restringidos, no anulados
                .append(" and of1.CO_PRIVACIDAD in ( ?,?)  "
                        + " and of1.FECHA_NOTIFICO_ALTA is null "
                        + " and of1.ELIMINADO = ? "
                        + " and ap_c.id = ?")
                .append(queryBaseGroupBy)
                /*
	 * se agreaga nuevo order by por nombre de compañia, bloque, gerencia,
	 * oficio jevazquez 17/02/15
                 */
                .append(" order by c.NOMBRE asc, ap_c.NOMBRE asc, g.NOMBRE asc, of1.numero_oficio asc ");

        Query qryNoNotif = em.createNativeQuery(sql.toString());
        qryNoNotif.setParameter(1, PrivacidadOficio.ID_PUBLICO);
        qryNoNotif.setParameter(2, PrivacidadOficio.ID_GERENCIA);
        qryNoNotif.setParameter(3, Constantes.BOOLEAN_FALSE);
        qryNoNotif.setParameter(4, idCampo);

        final List<Object[]> resultadoConsulta = qryNoNotif.getResultList();

        final List<OficioPromovibleVo> oficios = castVo(resultadoConsulta);

        final List<OficioPromovibleVo> resultado = new ArrayList<>();

        // agregar movimientos de oficio
        for (OficioPromovibleVo vo : oficios) {

            vo.setMovimientos(obtenerMovimientos(vo.getOficioId()));

            if (vo.contieneArchivoInformeAvance()) {
                resultado.add(vo);
            }

        }

        return resultado;

    }

    public List<OficioPromovibleVo> buscarOficiosNoNotificadosSemana() {

        getLogger().info(this, "@buscarOficiosNoNotificados");

        final StringBuilder sql = new StringBuilder(300);

        sql.append(queryBase)
                // obtener oficios publicos, no restringidos, no anulados
                .append(" and of1.CO_PRIVACIDAD in ( ?,?) "
                        + " and of1.FECHA_NOTIFICO_ALTA is null "
                        + " and of1.ELIMINADO = ? ");

        Date fechaPrevia = new Date();
        fechaPrevia.setDate(fechaPrevia.getDate() - 7);

        sql.append(" and of1.FECHA_MODIFICO < CAST ('")
                .append(Constantes.FMT_yyyyMMdd.format(fechaPrevia))
                .append("' as date)")
                .append(queryBaseGroupBy)
                .append(" ORDER BY trim(upper (c.NOMBRE)) asc, trim(upper(ap_c.NOMBRE)) asc, trim(upper(g.NOMBRE)) asc, trim(upper(of1.numero_oficio)) asc");

        final Query qryNoNotif = em.createNativeQuery(sql.toString());
        qryNoNotif.setParameter(1, PrivacidadOficio.ID_PUBLICO);
        qryNoNotif.setParameter(2, PrivacidadOficio.ID_GERENCIA);
        qryNoNotif.setParameter(3, Constantes.BOOLEAN_FALSE);

        final List<Object[]> resultadoConsulta = qryNoNotif.getResultList();

        final List<OficioPromovibleVo> oficios = castVo(resultadoConsulta);

        final List<OficioPromovibleVo> resultado = new ArrayList<>();

        // agregar movimientos de oficio
        for (final OficioPromovibleVo vo : oficios) {

            vo.setMovimientos(obtenerMovimientos(vo.getOficioId()));
            if (vo.getEstatusId() != Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO
                    && vo.getEstatusId() != Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO) {
                resultado.add(vo);
            }
        }

        return resultado;

    }

    public List<UsuarioVO> buscarUsuariosAccesoOficioRestringido(String nombre, OficioVo oficioVo) {

        getLogger().info(this, "@buscarUsuariosAccesoOficioRestringido - bloqueId = " + oficioVo.getBloqueId());

        // los usuariosRey deberán tener acceso mínimo de consulta y tener acceso
        // al bloque del oficio para que puedan buscar y acceder al detalle del
        // oficio restringido
        // excluir los usuariosRey ya seleccionados
        List<UsuarioVO> usuariosAcceso
                = usuarioServicioRemoto.getUsuariosPorRolBloque(
                        Constantes.OFICIOS_ROL_CONSULTA_OFICIOS,
                        oficioVo.getBloqueId(),
                        null,
                        nombre,
                        oficioVo.getRestringidoAUsuariosIds());

        return usuariosAcceso;
    }

    private List<OficioPromovibleVo> castVo(List<Object[]> lista) {

        List<OficioPromovibleVo> result = new ArrayList();

        for (Object[] obj : lista) {

            OficioPromovibleVo vo = castVo(obj);

            result.add(vo);

        }

        return result;

    }

    private OficioPromovibleVo castVo(Object[] obj) {

        int i = 0;

        int tipoOficioId = (Integer) obj[i++];

        OficioPromovibleVo vo = instanciarOficioVo(tipoOficioId);

        // tipo de oficio
        vo.setTipoOficioId(tipoOficioId);
        vo.setTipoOficioNombre(String.valueOf(obj[i++]));

        // información del oficio
        vo.setOficioId((Integer) obj[i++]);
        vo.setOficioNumero(String.valueOf(obj[i++]));
        vo.setOficioFecha((java.util.Date) obj[i++]);
        vo.setFechaGenero((java.util.Date) obj[i++]);
        vo.setOficioAsunto(String.valueOf(obj[i++]));
        vo.setObservaciones(String.valueOf(obj[i++]));

        // información de oficio asociado
        /*Object asociadoId = obj[i++];
	 Object asociadoNumero = obj[i++];*/
        // compañía
        vo.setCompaniaRfc(String.valueOf(obj[i++]));
        vo.setCompaniaNombre(String.valueOf(obj[i++]));
        vo.setCompaniaSiglas(String.valueOf(obj[i++]));

        // bloque
        vo.setBloqueId((Integer) obj[i++]);
        vo.setBloqueNombre(String.valueOf(obj[i++]));

        // gerencia
        vo.setGerenciaId((Integer) obj[i++]);
        vo.setGerenciaNombre(String.valueOf(obj[i++]));

        // estatus
        vo.setEstatusId((Integer) obj[i++]);
        vo.setEstatusNombre(String.valueOf(obj[i++]));

        // usuario
        vo.setGenero(String.valueOf(obj[i++]));
        vo.setUsuarioGeneroNombre(String.valueOf(obj[i++]));

        // archivo adjunto
        // Los archivos adjuntos se relacionan con los movimeintos
        // de este oficio
        AdjuntoOficioVo adj = new AdjuntoOficioVo();

        vo.setArchivoAdjunto(adj);
        vo.setEliminado((Boolean) obj[i++]);
        vo.setUrgente((Boolean) obj[i++]);
        vo.setRequiereSeguimiento((Boolean) obj[i++]);
        vo.setAcceso(PrivacidadOficio.getPrivacidadOficio((Integer) obj[i++]));
        vo.setRestringidoAUsuariosIds(String.valueOf(obj[i++]));

        return vo;

    }

    private OficioPromovibleVo instanciarOficioVo(int tipoOficio) {

        OficioPromovibleVo vo = null;

        if (tipoOficio == Constantes.OFICIOS_TIPO_OFICIO_SALIDA_ID) {
            vo = new OficioSalidaVo();
        } else if (tipoOficio == Constantes.OFICIOS_TIPO_OFICIO_ENTRADA_ID) {
            vo = new OficioEntradaVo();
        }

        return vo;

    }

    /**
     *
     * @return
     */
    private UtilLog4j getLogger() {
        return UtilLog4j.log;
    }

    // </editor-fold>
    /**
     *
     * @param vo
     * @param filtrarRestringidos Si es true, se excluyen los oficios
     * restringidos a los que el usuario no tiene acceso.
     * @param usuarioId
     * @return
     */
    public final ResultadosConsultaVo buscarOficios(
            final OficioVo vo,
            final boolean filtrarRestringidos,
            final String usuarioId) {

        //FIXME : revisar la lógica para filtrar desde la consulta y no tener que iterar en los resultados.
        List<OficioPromovibleVo> resultados = buscarOficios(vo);

        // validar si se deben remover los oficios restringidos
        if (filtrarRestringidos) {

            List<OficioPromovibleVo> remover = new ArrayList<>();

            for (OficioPromovibleVo oficio : resultados) {

                getLogger().debug(this, "@buscarOficios-filtrarRestringidos -- num ofic = '" + oficio.getOficioNumero() + "', "
                        + "restringido = '" + oficio.isRestringido() + "', "
                        + "tiene acceso = '" + oficio.tieneAccesoOficioRestringido(usuarioId) + "'");

                if (oficio.isRestringido() && !oficio.tieneAccesoOficioRestringido(usuarioId)) {
                    remover.add(oficio);
                }
            }

            resultados.removeAll(remover);
        }

        ResultadosConsultaVo resultadosVo = new ResultadosConsultaVo();

        //FIXME : poner el límite en la consulta a la base de datos
        // verificar máximo de 100 registros
        if (resultados.size() > vo.getMaxOficios()) {

            resultadosVo.setCantidadOriginal(resultados.size());
            resultadosVo.setCantidadMaximaExcedida(true);

            // remover los elementos excedentes
            resultados.subList(vo.getMaxOficios(), resultados.size()).clear();

        }

        resultadosVo.setResultados(resultados);

        return resultadosVo;

    }

    private void condicionesBandeja(PermisosVo permisosVo, StringBuilder sql, List<String> params) {

        String condicionOficio = " (of1.OF_TIPO_OFICIO = ?::integer and of1.ESTATUS = ?::integer) ";

        StringBuilder sb = new StringBuilder();
        if (permisosVo.isRolEmisorOficiosEntrada() && permisosVo.isRolEmisorOficiosSalida()) {
            if (permisosVo.isRolReceptorMonterrey() && permisosVo.isRolReceptorReynosa()) {
                condicionOficio = "(of1.ESTATUS <> ?::integer)";
                sb.append(condicionOficio);

                params.add(String.valueOf(Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO));
                sb.append(" and ");

                sb.append(condicionOficio);

                params.add(String.valueOf(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO));
            }
        } else {
            // los permisos emisores son exclusivos
            if (permisosVo.isRolEmisorOficiosSalida()) {

                sb.append(condicionOficio);

                params.add(String.valueOf(Constantes.OFICIOS_TIPO_OFICIO_SALIDA_ID));
                params.add(String.valueOf(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO));

            } else if (permisosVo.isRolEmisorOficiosEntrada()) {

                sb.append(condicionOficio);

                params.add(String.valueOf(Constantes.OFICIOS_TIPO_OFICIO_ENTRADA_ID));
                params.add(String.valueOf(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO));

            }

            // los permisos receptores son exclusivos
            if (permisosVo.isRolReceptorMonterrey()) {

                if (sb.length() > 0) {
                    sb.append(" or ");
                }

                sb.append(condicionOficio);
                sb.append(" or ");
                sb.append(condicionOficio);

                params.add(String.valueOf(Constantes.OFICIOS_TIPO_OFICIO_SALIDA_ID));
                params.add(String.valueOf(Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX));

                params.add(String.valueOf(Constantes.OFICIOS_TIPO_OFICIO_ENTRADA_ID));
                params.add(String.valueOf(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY));

            } else if (permisosVo.isRolReceptorReynosa()) {

                if (sb.length() > 0) {
                    sb.append(" or ");
                }

                sb.append(condicionOficio);

                params.add(String.valueOf(Constantes.OFICIOS_TIPO_OFICIO_SALIDA_ID));
                params.add(String.valueOf(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA));

            }
        }
        sql.append(sb);

    }

    public OficioPromovibleVo obtenerOficioVo(final Integer id) {

        // obtener información de oficio
        final StringBuilder sql = new StringBuilder();

        sql.append(queryBase)
                .append("and of1.ID = ? ");

        //sb.append(queryBaseGroupBy);
        final Query qryOficio = em.createNativeQuery(sql.toString());
        qryOficio.setParameter(1, id);

        final List resultado = qryOficio.getResultList();

        OficioPromovibleVo resultadoVo = (OficioPromovibleVo) castVo(resultado).get(0);

        return resultadoVo;
    }

    private List<UsuarioVO> obtenerUsuariosOficioRestringido(OficioVo vo) {

        List<UsuarioVO> usuarios;

        if (vo.isRestringido()) {

            List<String> usuarioIds = oficioUsuarioRemoto.buscarUsuariosOficioRestringidoIds(vo.getOficioId());

            usuarios = usuarioServicioRemoto.findUsuariosById(usuarioIds);
        } else {
            usuarios = new ArrayList<UsuarioVO>();
        }

        return usuarios;

    }

    public List<MovimientoVo> obtenerMovimientos(final Integer oficioId) {

        final StringBuilder sql = new StringBuilder(100);

        // se agrega desc para los casos en donde la hora y fecha sea igual
        // (ej. en opción Guardar y Promover)
        sql.append(queryMovimientos)
                .append("and ofic.id = ? \n "
                        + "order by mov.FECHA_GENERO DESC, mov.HORA_GENERO DESC, mov.ID DESC ");

        final Query queryMov = em.createNativeQuery(sql.toString());
        queryMov.setParameter(1, oficioId);

        final List resultado = queryMov.getResultList();

        getLogger().debug(this, "@obtenerMovimientos - ID = {0}, query = {1}", new Object[]{oficioId, sql});

        return castMovimientosVo(resultado);

    }

    private List<MovimientoVo> castMovimientosVo(List lista) {

        List<MovimientoVo> result = new ArrayList();

        for (Iterator it = lista.iterator(); it.hasNext();) {

            Object[] obj = (Object[]) it.next();

            MovimientoVo vo = castMovimientoVo(obj);

            result.add(vo);

        }

        return result;

    }

    private MovimientoVo castMovimientoVo(Object[] obj) {

        MovimientoVo vo = new MovimientoVo();

        int i = 0;

        vo.setOficioMovimientoId((Integer) obj[i++]);

        vo.setOficioId((Integer) obj[i++]);
        vo.setOficioNumero(String.valueOf(obj[i++]));

        vo.setOperacionId((Integer) obj[i++]);
        vo.setOperacion(String.valueOf(obj[i++]));

        vo.setId((Integer) obj[i++]);
        vo.setMotivo(String.valueOf(obj[i++]));

        // archivo adjunto
        AdjuntoOficioVo adj = new AdjuntoOficioVo();

        adj.setId((Integer) obj[i++]);
        adj.setNombre(String.valueOf(obj[i++]));
        adj.setUrl(String.valueOf(obj[i++]));
        adj.setTipoArchivo(String.valueOf(obj[i++]));
        adj.setPeso(String.valueOf(obj[i++]));
        adj.setUuid(String.valueOf(obj[i++]));

        final Object eliminado = obj[i++];
        adj.setEliminado(eliminado == null ? Boolean.FALSE : (Boolean) eliminado);

        vo.setAdjunto(adj);

        vo.setGenero(String.valueOf(obj[i++]));
        vo.setNombre(String.valueOf(obj[i++]));
        vo.setFechaGenero((java.util.Date) obj[i++]);
        vo.setHoraGenero((java.util.Date) obj[i++]);

        return vo;

    }

    public OficioVo obtenerOficioVo(String numeroOficio, int idbloque) {

        // obtener información de oficio
        final StringBuilder sql = new StringBuilder(100);

        sql.append(queryBase)
                .append(" and of1.NUMERO_OFICIO COLLATE \"es_ES\" = ? and  ap_c.id= ? ")
                .append(queryBaseGroupBy);

        final Query q = em.createNativeQuery(sql.toString());

        q.setParameter(1, numeroOficio.trim());
        q.setParameter(2, idbloque);

        final List resultados = q.getResultList();

        OficioVo resultadoVo = null;

        if (!resultados.isEmpty()) {
            resultadoVo = (OficioVo) castVo(resultados).get(0);
        }

        return resultadoVo;
    }

}
