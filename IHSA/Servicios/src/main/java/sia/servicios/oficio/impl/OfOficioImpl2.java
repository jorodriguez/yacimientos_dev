package sia.servicios.oficio.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.OfOficio;
import sia.modelo.oficio.vo.AdjuntoOficioVo;
import sia.modelo.oficio.vo.OficioEntradaVo;
import sia.modelo.oficio.vo.OficioPromovibleVo;
import sia.modelo.oficio.vo.OficioSalidaVo;
import sia.modelo.oficio.vo.OficioVo;
import sia.modelo.oficio.vo.PrivacidadOficio;
import sia.modelo.oficio.vo.ResultadosConsultaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;
//import sia.util.UtilLog4j;
import sia.util.UtilSia;

/**
 * Implementa los métodos de negocio para el módulo de Control de Oficios.
 *
 * @author esapien
 */

@Stateless 
public class OfOficioImpl2 extends AbstractFacade<OfOficio> {

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
                + "where 1=1 \n";;

    private final String queryBaseGroupBy =  getQueryBaseGroupBy();

    //private final String queryOficiosAsociados;
    private final String queryMovimientos =  "SELECT \n"
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
                + "WHERE 1=1 \n";;

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Métodos del ciclo de vida del bean">
    /**
     * Constructor
     *
     */
    public OfOficioImpl2() {

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
     *
     * @param vo
     * @param filtrarRestringidos Si es true, se excluyen los oficios
     * restringidos a los que el usuario no tiene acceso.
     * @param usuarioId
     * @return
     */
    

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
        
        System.out.println("@buscar oficios "+vo.toString());

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
                +"LIMIT "
        ).append(vo.getMaxOficios());

        getLogger().info(this, "query = {0}", new Object[]{sql});
        
        System.out.println(" ====== em "+ (em == null));
                        
        final Query qryProm = em.createNativeQuery(sql.toString());

        // establecer condiciones de consulta
        int i = 0;

        for (final Object param : params) {            
            System.out.println("PARAM "+param);
            qryProm.setParameter(++i, param);
        }

        final List<Object[]> resultado = qryProm.getResultList();

        getLogger().debug(this, "resultados = " + resultado.size());

        return castVo(resultado);

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
}
