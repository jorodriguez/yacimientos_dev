

package sia.servicios.oficio.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.OfOficio;
import sia.modelo.OfOficioAsociado;
import sia.modelo.Usuario;
import sia.modelo.oficio.vo.OficioPromovibleVo;
import sia.modelo.oficio.vo.OficioVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;
import sia.util.UtilSia;

/**
 *
 * @author esapien
 */
@LocalBean 
public class OfOficioAsociadoImpl extends AbstractFacade<OfOficioAsociado>{
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    private final String queryBase;
    
    @Inject
    private OfOficioImpl oficioServicioRemoto;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * 
     */
    public OfOficioAsociadoImpl() {
        super(OfOficioAsociado.class);
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("SELECT ");
        sb.append("ID, ");
        sb.append("OF_OFICIO, ");
        sb.append("OF_OFICIO_ASOCIADO_A, ");
        sb.append("GENERO, ");
        sb.append("FECHA_GENERO, ");
        sb.append("HORA_GENERO, ");
        sb.append("MODIFICO, ");
        sb.append("FECHA_MODIFICO, ");
        sb.append("HORA_MODIFICO, ");
        sb.append("ELIMINADO ");
        sb.append("FROM OF_OFICIO_ASOCIADO ");
        sb.append("WHERE 1=1 ");
        
        queryBase = sb.toString();
        
    }
    
    
    /**
     * Obtiene la lista de oficios a los que el oficio del id proporcionado
     * está asociado.
     * 
     * @param oficioId      ID del oficio a encontrar sus asociaciones.
     * @param desdeHacia    Indicador para determinar el lado de la asociación a retornar.
     * @return 
     */
    
    public List<OficioVo> buscarOficiosAsociados(int oficioId, int desdeHacia) {
        
        List<OficioVo> resultado;
        
        getLogger().info(this, "@OfOficioAsociadoImpl.buscarOficiosAsociados");
        
        List<OfOficioAsociado> oficiosAsociados = buscarOfOficiosAsociadosActivos(oficioId, desdeHacia);
        
        getLogger().info(this, "resultado (cant.) = " + oficiosAsociados.size());
        
        // lista de ids de los oficios asociados
        
        List<Integer> asociadoIds = new ArrayList<Integer>();
        
        for (OfOficioAsociado asociado : oficiosAsociados) {
            
            // obtener el ID del oficio en funcion del sentido de la asociacion
            
            Integer asociadoId;
            
            switch (desdeHacia) {
                
                case Constantes.OFICIOS_ASOCIACION_HACIA:
                    asociadoId = asociado.getOfOficioAsociadoA().getId();
                    break;
                case Constantes.OFICIOS_ASOCIACION_DESDE:
                    asociadoId = asociado.getOfOficio().getId();
                    break;
                default:
                    throw new IllegalArgumentException();

            }
            
            asociadoIds.add(asociadoId);
            
        }
        
        getLogger().info(this, "ids = " + UtilSia.toCommaSeparatedString(asociadoIds, false));
        
        // este casting es necesario generar lista para la clase base
        List<OficioPromovibleVo> aux = oficioServicioRemoto.buscarOficiosPorId(asociadoIds);
        
        resultado = new ArrayList<OficioVo>();
        
        for (OficioVo auxVo : aux) {
            
            resultado.add(auxVo);
            
        }
        
        return resultado;
        
    }
    
    /**
     * 
     * @param oficioId
     * @return 
     */
    private List<OfOficioAsociado> buscarOfOficiosAsociadosActivos(int oficioId, int desdeHacia) {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(queryBase);
        
        switch(desdeHacia) {
            
            case Constantes.OFICIOS_ASOCIACION_HACIA:
                sb.append("AND OF_OFICIO = ? ");
                break;
            case Constantes.OFICIOS_ASOCIACION_DESDE:
                sb.append("AND OF_OFICIO_ASOCIADO_A = ? ");
                break;
            default:
                throw new IllegalArgumentException();
            
        }

        sb.append("AND ELIMINADO = 'False' ");
        
        String sql = sb.toString();
        
        getLogger().info(this, "sql = " + sql + ", oficioId = " + oficioId);
        
        Query q = em.createNativeQuery(sql);
        
        q.setParameter(1, oficioId);
        
        List<Object[]> resultadoSql = q.getResultList();
        
        List<OfOficioAsociado> resultado = new ArrayList<OfOficioAsociado>();
        
        for (Object[] obj : resultadoSql) {
            
            OfOficioAsociado entidad = castToEntity(obj);
            
            resultado.add(entidad);
        }
        
        return resultado;
    }
    
    
    
    /**
     * 
     * @param obj
     * @return 
     */
    private OfOficioAsociado castToEntity(Object[] obj) {
        
        OfOficioAsociado entidad = new OfOficioAsociado();
        
        int i = 0;
        
        entidad.setId((Integer)obj[i++]);
        entidad.setOfOficio(new OfOficio((Integer)obj[i++]));
        entidad.setOfOficioAsociadoA(new OfOficio((Integer)obj[i++]));
        
        entidad.setGenero(new Usuario((String)obj[i++]));
        entidad.setFechaGenero((Date)obj[i++]);
        entidad.setHoraGenero((Date)obj[i++]);
        
        Usuario usuario = obj[i] == null ? null : new Usuario((String)obj[i]);
        i++;
        Date fechaModifico = obj[i] == null ? null : (Date)obj[i];
        i++;
        Date horaModifico = obj[i] == null ? null : (Date)obj[i];
        i++;
        
        entidad.setModifico(usuario);
        entidad.setFechaModifico(fechaModifico);
        entidad.setHoraModifico(horaModifico);
        
        entidad.setEliminado((Boolean)obj[i]);
        
        return entidad;
        
    }
    
    /**
     * Realiza el borrado lógico de los registros de oficios asociados
     * pertenecientes al oficioId proporcionado, en caso de haber.
     * 
     * @param oficioId
     * @param usuarioId
     * @return 
     */
    
    public int borrarLogicoOficiosAsociados(Integer oficioId, String usuarioId) {
        
        // obtener los registros de oficios hacia los que este oficio 
        // está asociado en caso de haber
        List<OfOficioAsociado> asociados = buscarOfOficiosAsociadosActivos(oficioId, Constantes.OFICIOS_ASOCIACION_HACIA);
        
        int cantidad = asociados.size();
        
        // actualizar a eliminado
        Date fechaModifico = new Date();
        
        for (OfOficioAsociado asociado : asociados) {
            
            asociado.setModifico(new Usuario(usuarioId));
            asociado.setFechaModifico(fechaModifico);
            asociado.setHoraModifico(fechaModifico);
            asociado.setEliminado(Constantes.BOOLEAN_TRUE);
            
            this.edit(asociado);
            
        }
        
        return cantidad;
    }
    
    
    /**
     * 
     * @return 
     */
    private UtilLog4j getLogger() {
        return UtilLog4j.log;
    }

    /**
     * 
     * @param oficioId
     * @return 
     */
    
    public boolean isAsociado(Integer oficioId) {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(queryBase);

        sb.append("and ((of_oficio = ? and eliminado = 'False') ");
        sb.append("or (OF_OFICIO_ASOCIADO_A = ? and eliminado = 'False')) ");
        
        String sql = sb.toString();
        
        Query q = em.createNativeQuery(sql);
        
        q.setParameter(1, oficioId);
        q.setParameter(2, oficioId);
        
        List<Object[]> resultadoSql = q.getResultList();
        
        boolean isAsociado = resultadoSql.size() > 0;
        
        return isAsociado;
        
    }
    
    
}
